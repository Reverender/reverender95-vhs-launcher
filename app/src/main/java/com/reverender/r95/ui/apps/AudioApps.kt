package com.reverender.r95.ui.apps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reverender.r95.LocalAppState
import com.reverender.r95.audio.FilterMode
import com.reverender.r95.audio.FxSettings
import com.reverender.r95.audio.SiteStudio
import com.reverender.r95.audio.StudioAudio
import com.reverender.r95.audio.SynthParams
import com.reverender.r95.audio.Wave
import com.reverender.r95.ui.desktop.Win
import com.reverender.r95.ui.viz.js.Dom
import com.reverender.r95.ui.viz.js.num
import com.reverender.r95.ui.viz.js.str
import com.reverender.r95.ui.viz.rememberFrameTime
import com.reverender.r95.ui.viz.site.SiteCanvas
import com.reverender.r95.ui.viz.site.SiteHost
import com.reverender.r95.ui.viz.site.VizFx
import com.reverender.r95.ui.win95.Btn
import com.reverender.r95.ui.win95.Lcd
import com.reverender.r95.ui.win95.Skin
import com.reverender.r95.ui.win95.Slider95
import com.reverender.r95.ui.win95.SliderBox
import com.reverender.r95.ui.win95.StatusCell
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import com.reverender.r95.ui.win95.bevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToInt

private val NeonCyan = Color(0xFF00F7FF)
private val NeonPink = Color(0xFFFF00A0)

// ================================================================ shared site pieces

private fun domV(id: String): Float = num(Dom.el(id).prop("value")).toFloat()
private fun domText(id: String): String = str(Dom.el(id).prop("textContent") ?: "")

/** A site range input: value, min, max and step come from the page element [id]. */
private fun snap(id: String, raw: Float): Double {
    val e = Dom.el(id)
    val min = num(e["min"]); val max = num(e["max"]); val step = num(e["step"]).let { if (it.isNaN() || it <= 0) 0.0 else it }
    var v = raw.toDouble().coerceIn(min, max)
    if (step > 0) v = min + Math.round((v - min) / step) * step
    return Math.round(v * 10000) / 10000.0
}

/** .fx-ctrl: label, live value label, and the range input (its oninput handler is [onInput]). */
@Composable
private fun FxCtrl(label: String, lblId: String, inputId: String, onInput: () -> Unit, modifier: Modifier = Modifier, labelId: String? = null) {
    SiteHost.uiFrame
    val e = Dom.el(inputId)
    val min = num(e["min"]).toFloat(); val max = num(e["max"]).toFloat()
    SliderBox(labelId?.let { domText(it).ifEmpty { label } } ?: label, domText(lblId), domV(inputId).coerceIn(min, max), min..max, { f ->
        e.setProp("value", str(snap(inputId, f))); onInput()
    }, modifier)
}

/** .fx-grid: controls laid out [cols] per row. */
@Composable
private fun FxGrid(cols: Int, items: List<@Composable (Modifier) -> Unit>) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        items.chunked(cols).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                row.forEach { it(Modifier.weight(1f)) }
                repeat(cols - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun StatusBar2(left: String, right: String) {
    Row(Modifier.fillMaxWidth().padding(top = 3.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        StatusCell(left, Modifier.weight(1f)); StatusCell(right, Modifier.weight(1f))
    }
}

/** .win95-inset holding a site canvas. */
@Composable
private fun InsetCanvas(id: String, fx: VizFx?, modifier: Modifier) {
    Box(modifier.background(Color.Black).then(if (Skin.classic) Modifier.bevel(false) else Modifier.border(1.dp, W95.Border))) {
        SiteCanvas(id, Modifier.fillMaxSize(), fx)
    }
}

/** Visualizer FX panel over a window that has the FX title button. */
@Composable
private fun WithFxPanel(win: Win?, content: @Composable ColumnScope.(VizFx) -> Unit) {
    val fx = win?.vizFx ?: remember { VizFx() }
    Box(Modifier.fillMaxSize().padding(4.dp)) {
        Column(Modifier.fillMaxSize()) { content(fx) }
        if (win?.fxOpen == true) VizFxPanel(fx, Modifier.align(Alignment.TopEnd).padding(top = 2.dp, end = 2.dp))
    }
}

// ================================================================ GRAPHIC_EQ.EXE

@Composable
fun EqApp(win: Win?) {
    SiteHost.uiFrame
    WithFxPanel(win) { fx ->
        InsetCanvas("canvas-eq", fx, Modifier.fillMaxWidth().weight(1.15f).heightIn(min = 78.dp))
        // .eq-sliders-wrap
        Row(Modifier.fillMaxWidth().weight(0.55f).heightIn(min = 120.dp).padding(vertical = 3.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            EqCol("eq-pre-val", "eq-preamp", "PRE") { SiteStudio.setEqPreamp(snap("eq-preamp", it)) }
            listOf("60", "170", "350", "1K", "3K", "6K", "12K", "16K").forEachIndexed { i, l ->
                EqCol("eq-v-$i", "eq-b-$i", l) { SiteStudio.setEqBand(i, snap("eq-b-$i", it)) }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            listOf("flat" to "Flat", "vapor" to "Vapor", "bass" to "Bass", "radio" to "AM 95", "cyber" to "Cyber").forEach { (k, l) ->
                Btn(l, { SiteStudio.applyEqPreset(k) }, Modifier.weight(1f), pressed = SiteStudio.eqPressed == k, size = 12.sp)
            }
        }
        EqSystemRow()
        StatusBar2(domText("eq-status-curve"), domText("eq-status-gain"))
    }
}

/** .eq-col: value, vertical slider, band label. */
@Composable
private fun androidx.compose.foundation.layout.RowScope.EqCol(valId: String, inputId: String, label: String, onInput: (Float) -> Unit) {
    SiteHost.uiFrame // follow the page value while dragging (this column is skipped when only the parent recomposes)
    Column(
        Modifier.weight(1f).fillMaxHeight().then(if (Skin.classic) Modifier.background(W95.Face).bevel(true) else Modifier.background(Color(0xFFF7F9FA)).border(1.dp, W95.SoftBorder)).padding(vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        T(domText(valId), size = 9.sp, mono = true, bold = true)
        Slider95(domV(inputId), onInput, Modifier.weight(1f).fillMaxWidth(), range = -12f..12f, vertical = true)
        Box(Modifier.fillMaxWidth().padding(horizontal = 3.dp).height(1.dp).background(Color.Black))
        T(label, size = 9.sp, bold = true)
    }
}

// ================================================================ DSP_FX_95.EXE

@Composable
fun FxApp(win: Win?) {
    SiteHost.uiFrame
    WithFxPanel(win) { fx ->
        InsetCanvas("canvas-fx", fx, Modifier.fillMaxWidth().weight(1f).heightIn(min = 64.dp))
        val u = SiteStudio::updateDspFx
        val ctrls = listOf(
            "TAPE SPEED" to "speed", "LPF CUTOFF" to "lpf", "HPF CUTOFF" to "hpf", "ECHO TIME" to "dtime", "ECHO MIX" to "dmix",
            "ECHO FDBK" to "dfdbk", "OVERDRIVE" to "drive", "SUB EXCITE" to "sub", "CHORUS WOW" to "chorus", "TREMOLO" to "tremolo",
            "RING MOD" to "ring", "STEREO PAN" to "pan", "REVERB HALL" to "reverb", "BITCRUSHER" to "crush", "PHASER JET" to "phaser",
            "FLANGER" to "flanger", "3D WIDENER" to "width", "COMP PUNCH" to "comp",
        )
        // .fx-rack-deck
        Column(
            Modifier.fillMaxWidth().weight(1f).padding(vertical = 3.dp).then(if (Skin.classic) Modifier.background(W95.Face).bevel(false) else Modifier.background(Color(0xFFEEF2F4)).border(1.dp, W95.SoftBorder))
                .verticalScroll(rememberScrollState()).padding(4.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            FxGrid(3, ctrls.map { (l, k) -> @Composable { m: Modifier -> FxCtrl(l, "fx-lbl-$k", "fx-$k", u, m) } })
            // native: other apps through the rack, and hardware knobs
            Spacer(Modifier.height(6.dp))
            FxExternalAndMidi()
        }
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            FxSettings.PRESETS.chunked(4).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    row.forEach { (k, l) -> Btn(l, { SiteStudio.applyFxPreset(k) }, Modifier.weight(1f), pressed = SiteStudio.fxPressed == k, size = 12.sp) }
                }
            }
        }
        StatusBar2(domText("fx-status-chain"), domText("fx-status-mod"))
    }
}

// ================================================================ CASSETTE_95.EXE

@Composable
fun CassetteApp(win: Win?) {
    SiteHost.uiFrame
    val fxs = com.reverender.r95.audio.AudioBus.fx
    // The tape model is part of the signal path while the deck is open, like the site's cassette chain.
    DisposableEffect(Unit) { fxs.tapeOn = true; fxs.touch(); onDispose { fxs.tapeOn = false; fxs.touch() } }
    val density = LocalDensity.current.density
    WithFxPanel(win) { fx ->
        // drag left/right on the reels to scrub/scratch the tape
        InsetCanvas("canvas-cassette", fx, Modifier.fillMaxWidth().weight(1.4f).heightIn(min = 155.dp).pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown()
                SiteStudio.scratchDown()
                var last = down.position.x
                while (true) {
                    val ev = awaitPointerEvent()
                    val ch = ev.changes.firstOrNull { it.id == down.id } ?: break
                    if (!ch.pressed) break
                    ch.consume()
                    val dx = (ch.position.x - last) / density; last = ch.position.x
                    if (dx != 0f) SiteStudio.scratchMove(dx.toDouble())
                }
                SiteStudio.scratchUp()
            }
        })
        val mode = SiteStudio.transportMode
        Row(Modifier.padding(top = 3.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            TransportHoldBtn("REW", "rew", Modifier.weight(1f), mode == "rew")
            Btn("▶ PLAY", { com.reverender.r95.os.Haptics.clunk("play"); SiteStudio.cassetteDeckPlay() }, Modifier.weight(1f), pressed = mode == "play", size = 12.sp)
            TransportHoldBtn("FF", "ff", Modifier.weight(1f), mode == "ff")
            Btn("⏹ STOP", { com.reverender.r95.os.Haptics.clunk("stop"); SiteStudio.cassetteDeckStop() }, Modifier.weight(1f), pressed = mode == "stop", size = 12.sp)
            Btn("⏸ PAUSE", { com.reverender.r95.os.Haptics.clunk("pause"); SiteStudio.cassetteDeckPause() }, Modifier.weight(1f), pressed = mode == "pause", size = 12.sp)
            Btn("↺ RESET", { com.reverender.r95.os.Haptics.clunk("eject"); SiteStudio.cassetteDeckEject() }, Modifier.weight(1f), size = 12.sp)
        }
        Box(Modifier.padding(vertical = 3.dp).fillMaxWidth().then(if (Skin.classic) Modifier.background(W95.Face).bevel(false) else Modifier.background(Color(0xFFEEF2F4)).border(1.dp, W95.SoftBorder)).padding(3.dp)) {
            FxGrid(3, listOf(
                { m -> FxCtrl("WOW & FLUTTER", "cas-lbl-wow", "cas-wow", SiteStudio::updateCassetteDsp, m) },
                { m -> FxCtrl("TAPE HISS", "cas-lbl-hiss", "cas-hiss", SiteStudio::updateCassetteDsp, m) },
                { m -> FxCtrl("MAGNETIC BIAS", "cas-lbl-bias", "cas-bias", SiteStudio::updateCassetteDsp, m) },
            ))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            FxSettings.TAPES.forEach { (k, l) -> Btn(l, { SiteStudio.setCassetteTapeType(k) }, Modifier.weight(1f), pressed = SiteStudio.tapeType == k, size = 12.sp) }
        }
        CassetteFeelRow()
        StatusBar2(domText("cas-status-mode"), domText("cas-status-counter"))
    }
}

/** REW / FF: onpointerdown start, onpointerup release, onpointerleave release(…, true). */
@Composable
private fun TransportHoldBtn(label: String, mode: String, modifier: Modifier, pressed: Boolean) {
    var down by remember { mutableStateOf(false) }
    Box(
        modifier.height(30.dp).background(W95.Face).bevel(!(down || pressed))
            .pointerInput(mode) {
                awaitEachGesture {
                    val d = awaitFirstDown()
                    down = true
                    com.reverender.r95.os.Haptics.hum(mode == "ff")
                    SiteStudio.startCassetteTransport(mode)
                    var left = false
                    while (true) {
                        val ev = awaitPointerEvent()
                        val ch = ev.changes.firstOrNull { it.id == d.id } ?: break
                        val p = ch.position
                        if (p.x < 0 || p.y < 0 || p.x > size.width || p.y > size.height) { left = true; break }
                        if (!ch.pressed) break
                    }
                    down = false
                    com.reverender.r95.os.Haptics.stopHum()
                    SiteStudio.releaseCassetteTransport(mode, left)
                }
            },
        contentAlignment = Alignment.Center,
    ) { T(label, size = 12.sp) }
}

// ================================================================ studio LCD + scope

/** .rr-studio-lcd: info on the left, the 150 × 34 scope on the right. */
@Composable
private fun StudioLcd(title: String, right: String, status: String, buffer: String, scope: Color) {
    Lcd(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T(title, color = Color.White, bold = true, mono = true, size = 11.sp, maxLines = 1, modifier = Modifier.weight(1f))
                    T(right, color = Color(0xFF00FF66), bold = true, mono = true, size = 11.sp, maxLines = 1)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T(status, color = Color(0xFFFF66CC), bold = true, mono = true, size = 10.sp, maxLines = 1, modifier = Modifier.weight(1f))
                    T(buffer, color = Color(0xFF00FF66), bold = true, mono = true, size = 9.sp, maxLines = 1)
                }
            }
            Spacer(Modifier.width(8.dp))
            StudioScope(Modifier.size(width = 150.dp, height = 34.dp), scope)
        }
    }
}

/** The studio's time-domain scope (xe(): trailing fade + 1.6 px line). */
@Composable
private fun StudioScope(modifier: Modifier, color: Color) {
    val time = rememberFrameTime()
    val trail = remember { ArrayList<FloatArray>() }
    Canvas(modifier.background(Color(0xBF000000)).border(1.dp, NeonCyan.copy(alpha = 0.35f))) {
        time.value
        val f = StudioAudio.core.analyzer.frameAt(System.nanoTime() - 60_000_000L)
        val fresh = System.nanoTime() - f.timeNanos < 400_000_000L
        val w = FloatArray(f.wave.size) { if (fresh) f.wave[it] else 0f }
        trail.add(0, w); while (trail.size > 4) trail.removeAt(trail.size - 1)
        trail.forEachIndexed { k, wave ->
            val path = Path()
            for (i in wave.indices) {
                val x = size.width * i / (wave.size - 1).coerceAtLeast(1)
                val y = size.height / 2 - wave[i] * size.height * 0.5f
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, color.copy(alpha = 0.55f.pow(k)), style = Stroke(1.6f * density))
        }
    }
}

// ================================================================ TR_909_95.EXE

/** The page state the 909's code keeps in its element texts. */
private object D909Ui {
    var status by mutableStateOf("READY — EMPTY PATTERN A · PAINT STEPS TO BEGIN")
    var stepText by mutableStateOf("STEP: 01/16")
    var accentPaint by mutableStateOf(false)
    var accentText by mutableStateOf("ACCENT: OFF")
    var chainText by mutableStateOf("A→D: OFF")
    var selName by mutableStateOf("VOICE: BD KICK")
    val tapTimes = ArrayList<Long>()
    val flash = LongArray(10)
    var version by mutableIntStateOf(0)
}

@Composable
fun Drum909App() {
    val d = StudioAudio.core.drums
    val ui = D909Ui
    val time = rememberFrameTime()
    fun wake() = StudioAudio.wake()
    fun trigger(i: Int, accent: Boolean) { d.trigger(i, accent); ui.flash[i] = System.currentTimeMillis(); wake() }
    /** ae(): select a voice and load its values into the voice sliders. */
    fun select(i: Int) {
        d.selectedVoice = i
        val t = d.voices[i]
        ui.selName = "VOICE: ${t.code} ${t.name.split(" ")[0]}"
        Dom.el("d909-v-level").setProp("value", str(t.level.toDouble())); Dom.el("d909-v-tune").setProp("value", str(t.tune))
        Dom.el("d909-v-decay").setProp("value", str(t.decay.toDouble())); Dom.el("d909-v-tone").setProp("value", str(t.tone.toDouble()))
        voiceLabels(d); ui.version++
    }
    LaunchedEffect(Unit) { masterLabels(d); if (domText("d909-lbl-vlevel").isEmpty()) initVoiceLabels() }
    // playhead / step readout while the loop runs
    LaunchedEffect(d.playing) {
        while (d.playing) {
            val t = (d.currentStep + 15) % 16
            ui.stepText = "PAT ${"ABCD"[d.currentPat]} | STEP: %02d/16".format(t + 1)
            delay(30)
        }
    }
    DisposableEffect(Unit) { onDispose { } }
    Column(Modifier.fillMaxSize().padding(4.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        ui.version
        StudioLcd("ROLAND // VAPOR-909 RHYTHM COMPOSER", ui.stepText, ui.status, "${d.bpm} BPM | SWING ${d.swing}%", NeonCyan)
        // .rr-909-top-deck
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
            Btn(if (d.playing) "⏹ STOP" else "▶ START", {
                if (!d.playing) { d.start(); wake(); ui.status = "LOOP RUNNING — LIVE EDITING ENABLED" }
                else { d.stop(); ui.status = "STOPPED — TAP PLAY OR TRIGGER PADS" }
                ui.version++
            }, pressed = d.playing, size = 12.sp)
            Btn("TAP", {
                val e = System.currentTimeMillis()
                ui.tapTimes.removeAll { e - it >= 2500 }
                ui.tapTimes.add(e)
                if (ui.tapTimes.size >= 2) {
                    val diffs = (1 until ui.tapTimes.size).map { ui.tapTimes[it] - ui.tapTimes[it - 1] }
                    val n = diffs.average()
                    val i = (60000 / n).roundToInt().coerceIn(60, 180)
                    Dom.el("d909-bpm").setProp("value", str(i.toDouble()))
                    updateMaster(d); ui.version++
                }
            }, size = 12.sp)
            Btn(ui.accentText, {
                ui.accentPaint = !ui.accentPaint
                ui.accentText = if (ui.accentPaint) "⚡ ACCENT: ON" else "⚡ ACCENT: OFF"
            }, pressed = ui.accentPaint, size = 12.sp)
            HoldBtn("ROLL", d.roll, { d.roll = true; wake() }, { d.roll = false })
            Btn("RND", { randomize909(d); ui.status = "GENERATED RANDOM 909 VAPOR GROOVE"; ui.version++ }, size = 12.sp)
            Btn("CLR", { d.clearPattern(); ui.status = "PATTERN CLEARED"; ui.version++ }, size = 12.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.background(Color(0xFF111111)).border(1.dp, Color(0xFF333333)).padding(horizontal = 5.dp, vertical = 3.dp)) {
                T("PAT", color = NeonCyan, bold = true, mono = true, size = 10.sp)
            }
            for (i in 0 until 4) Btn("ABCD"[i].toString(), { d.currentPat = i; ui.version++ }, pressed = d.currentPat == i, size = 12.sp)
            Btn(ui.chainText, {
                d.chain = !d.chain
                ui.chainText = if (d.chain) "🔗 A→D: ON" else "🔗 A→D: OFF"; ui.version++
            }, pressed = d.chain, size = 12.sp)
            Btn("COPY→", {
                val e = (d.currentPat + 1) % 4
                d.copyPatternToNext()
                ui.status = "COPIED PATTERN ${"ABCD"[d.currentPat]} → ${"ABCD"[e]}"
            }, size = 12.sp)
        }
        // .fx-rack-deck: master + voice controls
        Box(Modifier.fillMaxWidth().then(if (Skin.classic) Modifier.background(W95.Face).bevel(false) else Modifier.background(Color(0xFFEEF2F4)).border(1.dp, W95.SoftBorder)).padding(4.dp)) {
            val um: () -> Unit = { updateMaster(d); ui.version++ }
            val uv: () -> Unit = { updateVoiceParam(d); ui.version++ }
            FxGrid(4, listOf(
                { m -> FxCtrl("TEMPO BPM", "d909-lbl-bpm", "d909-bpm", um, m) },
                { m -> FxCtrl("909 SWING", "d909-lbl-swing", "d909-swing", um, m) },
                { m -> FxCtrl("TAPE DRIVE", "d909-lbl-drive", "d909-drive", um, m) },
                { m -> FxCtrl("MASTER VOL", "d909-lbl-vol", "d909-vol", um, m) },
                { m -> FxCtrl(ui.selName, "d909-lbl-vlevel", "d909-v-level", uv, m) },
                { m -> FxCtrl("VOICE TUNE", "d909-lbl-vtune", "d909-v-tune", uv, m) },
                { m -> FxCtrl("VOICE DECAY", "d909-lbl-vdecay", "d909-v-decay", uv, m) },
                { m -> FxCtrl("SNAP / TONE", "d909-lbl-vtone", "d909-v-tone", uv, m) },
            ))
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val wide = maxWidth >= 640.dp
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // .rr-909-pads-row
                d.voices.chunked(if (wide) 10 else 5).forEachIndexed { r, row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        row.forEachIndexed { c, v ->
                            val i = r * (if (wide) 10 else 5) + c
                            time.value
                            val hit = System.currentTimeMillis() - ui.flash[i] < 90 || (d.playing && (d.lastHits shr i) and 1 == 1)
                            val sel = d.selectedVoice == i
                            Box(
                                Modifier.weight(1f).height(48.dp)
                                    .background(if (hit) Brush.verticalGradient(listOf(NeonPink, NeonCyan)) else Brush.verticalGradient(listOf(Color(0xFF2A173D), Color(0xFF120824))))
                                    .border(2.dp, if (sel) NeonPink else NeonCyan)
                                    .pointerInput(i) { detectTapGestures(onPress = { select(i); trigger(i, ui.accentPaint) }) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    T(v.code, color = if (hit) Color.Black else if (sel) Color.White else NeonCyan, size = 12.sp, bold = true, mono = true)
                                    T(v.name.split(" ")[0], color = if (hit) Color.Black else Color(0xFFFF9DE2), size = 8.sp, bold = true, mono = true)
                                }
                            }
                        }
                    }
                }
                Matrix909(d, if (wide) 16 else 8, onSelect = { select(it) }, onTrigger = { i, a -> trigger(i, a) }, accent = { ui.accentPaint })
            }
        }
        StatusBar2("TOUCH/DRAG STEPS TO DRAW LOOPS · TAP PADS TO TRIGGER & EDIT", "CH/OH CHOKE: LINKED | 10-VOICE 909 DSP")
    }
}

private fun masterLabels(d: com.reverender.r95.audio.Drum909) {
    Dom.el("d909-lbl-bpm").setProp("textContent", "${d.bpm}"); Dom.el("d909-lbl-swing").setProp("textContent", "${d.swing}%")
    Dom.el("d909-lbl-drive").setProp("textContent", "${d.drive}%"); Dom.el("d909-lbl-vol").setProp("textContent", "${d.masterVol}%")
    Dom.el("d909-bpm").setProp("value", str(d.bpm.toDouble())); Dom.el("d909-swing").setProp("value", str(d.swing.toDouble()))
    Dom.el("d909-drive").setProp("value", str(d.drive.toDouble())); Dom.el("d909-vol").setProp("value", str(d.masterVol.toDouble()))
}

private fun initVoiceLabels() {
    Dom.el("d909-lbl-vlevel").setProp("textContent", "95%"); Dom.el("d909-lbl-vtune").setProp("textContent", "0 ST")
    Dom.el("d909-lbl-vdecay").setProp("textContent", "60%"); Dom.el("d909-lbl-vtone").setProp("textContent", "55%")
}

/** rr909UpdateMaster */
private fun updateMaster(d: com.reverender.r95.audio.Drum909) {
    d.bpm = domV("d909-bpm").roundToInt().takeIf { it != 0 } ?: 118
    d.swing = domV("d909-swing").roundToInt().takeIf { it != 0 } ?: 54
    d.drive = domV("d909-drive").roundToInt()
    d.masterVol = domV("d909-vol").roundToInt().takeIf { it != 0 } ?: 85
    masterLabels(d)
}

/** ge() */
private fun voiceLabels(d: com.reverender.r95.audio.Drum909) {
    val e = d.voices[d.selectedVoice]
    Dom.el("d909-lbl-vlevel").setProp("textContent", "${e.level}%")
    Dom.el("d909-lbl-vtune").setProp("textContent", "${if (e.tune > 0) "+" else ""}${str(e.tune)} ST")
    Dom.el("d909-lbl-vdecay").setProp("textContent", "${e.decay}%")
    Dom.el("d909-lbl-vtone").setProp("textContent", "${e.tone}%")
}

/** rr909UpdateVoiceParam: all four voice sliders go into the selected voice. */
private fun updateVoiceParam(d: com.reverender.r95.audio.Drum909) {
    val e = d.voices[d.selectedVoice]
    e.level = domV("d909-v-level").roundToInt(); e.tune = num(Dom.el("d909-v-tune").prop("value"))
    e.decay = domV("d909-v-decay").roundToInt(); e.tone = domV("d909-v-tone").roundToInt()
    voiceLabels(d)
}

/** rr909Randomize */
private fun randomize909(d: com.reverender.r95.audio.Drum909) {
    val e = Array(10) { IntArray(16) }
    for (t in 0 until 16) {
        if (t % 4 == 0 || (t == 14 && Math.random() < 0.5)) e[0][t] = 2
        if (t == 4 || t == 12) e[1][t] = 2
        if (Math.random() < 0.22) e[2 + (Math.random() * 3).toInt()][t] = 1
        if (Math.random() < 0.18) e[5][t] = 1
        if (t == 4 || t == 12 || Math.random() < 0.15) e[6][t] = 1
        if (t % 2 == 0 || Math.random() < 0.55) e[7][t] = if (t % 4 == 2) 2 else 1
        if (t % 4 == 2 && Math.random() < 0.65) { e[8][t] = 1; e[7][t] = 0 }
        if (t == 0 && Math.random() < 0.6) e[9][t] = 1
    }
    for (v in 0 until 10) e[v].copyInto(d.patterns[d.currentPat][v])
}

@Composable
private fun HoldBtn(label: String, active: Boolean, onDown: () -> Unit, onUp: () -> Unit) {
    var down by remember { mutableStateOf(false) }
    Box(
        Modifier.height(30.dp).background(W95.Face).bevel(!(down || active))
            .pointerInput(Unit) { detectTapGestures(onPress = { down = true; onDown(); tryAwaitRelease(); down = false; onUp() }) }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) { T(label, size = 12.sp) }
}

/**
 * #d909-matrix: per voice a track head (code + M mute) and 16 steps, wrapped [perLine] per line.
 * pointerdown on a step toggles it (accent paint = 2), dragging paints the same value.
 */
@Composable
private fun Matrix909(d: com.reverender.r95.audio.Drum909, perLine: Int, onSelect: (Int) -> Unit, onTrigger: (Int, Boolean) -> Unit, accent: () -> Boolean) {
    val time = rememberFrameTime()
    val lines = 16 / perLine
    val density = LocalDensity.current.density
    val rowH = 22f; val gap = 2f; val vGap = 3f
    val headW = if (perLine == 8) 76f else 98f
    val voiceH = lines * rowH + (lines - 1) * gap
    val totalH = 10 * voiceH + 9 * vGap + 8f
    var ver by remember { mutableIntStateOf(0) }
    Canvas(
        Modifier.fillMaxWidth().height(totalH.dp).background(Color(0xEB060212)).then(if (Skin.classic) Modifier.bevel(false) else Modifier.border(1.dp, W95.Border))
            .pointerInput(perLine) {
                fun geo(o: Offset): Triple<Int, Int, String>? { // voice, step, part
                    val x = o.x / density - 4f; val y = o.y / density - 4f
                    val v = (y / (voiceH + vGap)).toInt(); if (v !in 0..9) return null
                    val yIn = y - v * (voiceH + vGap); if (yIn > voiceH) return null
                    if (x < headW) return Triple(v, -1, if (x > headW - 26f) "mute" else "head")
                    val cw = (size.width / density - 8f - headW - gap) / perLine
                    val col = ((x - headW - gap) / cw).toInt().coerceIn(0, perLine - 1)
                    val line = (yIn / (rowH + gap)).toInt().coerceIn(0, lines - 1)
                    return Triple(v, line * perLine + col, "step")
                }
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val g = geo(down.position) ?: return@awaitEachGesture
                    down.consume()
                    when (g.third) {
                        "mute" -> { d.muted[g.first] = !d.muted[g.first]; ver++ }
                        "head" -> { onSelect(g.first); onTrigger(g.first, false) }
                        else -> {
                            val pat = d.patterns[d.currentPat]
                            onSelect(g.first)
                            val value = if (pat[g.first][g.second] > 0) 0 else if (accent()) 2 else 1
                            pat[g.first][g.second] = value
                            if (value > 0 && !d.playing) onTrigger(g.first, value == 2)
                            ver++
                            while (true) {
                                val ev = awaitPointerEvent()
                                val ch = ev.changes.firstOrNull { it.id == down.id } ?: break
                                if (!ch.pressed) break
                                ch.consume()
                                geo(ch.position)?.takeIf { it.third == "step" }?.let { (v, s, _) ->
                                    if (d.patterns[d.currentPat][v][s] != value) { d.patterns[d.currentPat][v][s] = value; ver++ }
                                }
                            }
                        }
                    }
                }
            },
    ) {
        time.value; ver; D909Ui.version
        val px = density
        val pat = d.patterns[d.currentPat]
        val play = if (d.playing) (d.currentStep + 15) % 16 else -1
        val cw = (size.width / px - 8f - headW - gap) / perLine
        val text = android.graphics.Paint().apply { isAntiAlias = true; typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD); textSize = 10f * px }
        for (v in 0 until 10) {
            val top = 4f + v * (voiceH + vGap)
            val sel = d.selectedVoice == v
            // track head
            drawRect(if (sel) NeonPink.copy(alpha = 0.22f) else Color(0xB8000000), Offset(4f * px, top * px), Size(headW * px, voiceH * px))
            drawRect(if (sel) NeonPink else NeonCyan.copy(alpha = 0.25f), Offset(4f * px, top * px), Size(headW * px, voiceH * px), style = Stroke(px))
            text.color = if (sel) android.graphics.Color.WHITE else 0xFF00F7FF.toInt()
            drawContext.canvas.nativeCanvas.drawText(d.voices[v].code, 9f * px, (top + voiceH / 2 + 4f) * px, text)
            val muted = d.muted[v]
            val mx = 4f + headW - 24f; val my = top + voiceH / 2 - 9f
            drawRect(if (muted) Color(0xFFB8004C) else Color(0xFF1B1B2F), Offset(mx * px, my * px), Size(18f * px, 18f * px))
            drawRect(if (muted) Color(0xFFFF8DA1) else Color(0xFF555555), Offset(mx * px, my * px), Size(18f * px, 18f * px), style = Stroke(px))
            text.color = if (muted) android.graphics.Color.WHITE else 0xFFAAAAAA.toInt()
            drawContext.canvas.nativeCanvas.drawText("M", (mx + 5.5f) * px, (my + 13f) * px, text)
            for (s in 0 until 16) {
                val line = s / perLine; val col = s % perLine
                val x = 4f + headW + gap + col * cw; val y = top + line * (rowH + gap)
                stepCell(Offset(x * px, y * px), Size((cw - gap) * px, rowH * px), pat[v][s], (s / 4) % 2 == 0, s == play)
            }
        }
    }
}

private fun DrawScope.stepCell(o: Offset, s: Size, value: Int, groupA: Boolean, playhead: Boolean) {
    when (value) {
        2 -> drawRect(Brush.verticalGradient(listOf(Color(0xFFFF2AA8), Color(0xFFFF9900)), o.y, o.y + s.height), o, s)
        1 -> drawRect(Brush.verticalGradient(listOf(Color(0xEB00F7FF), Color(0xEB0082B4)), o.y, o.y + s.height), o, s)
        else -> drawRect(if (groupA) Color(0xFF261D3F) else Color(0xFF181329), o, s)
    }
    val px = density
    drawLine(Color.White.copy(alpha = 0.35f), o, Offset(o.x + s.width, o.y), px); drawLine(Color.White.copy(alpha = 0.35f), o, Offset(o.x, o.y + s.height), px)
    drawLine(Color.Black, Offset(o.x + s.width, o.y), Offset(o.x + s.width, o.y + s.height), px); drawLine(Color.Black, Offset(o.x, o.y + s.height), Offset(o.x + s.width, o.y + s.height), px)
    drawRect(if (value == 1) Color.White else Color.White.copy(alpha = 0.14f), Offset(o.x + s.width / 2 - 3 * px, o.y + 3 * px), Size(6 * px, 4 * px))
    if (playhead) {
        drawRect(Color.White.copy(alpha = 0.18f), o, s)
        drawRect(Color.White, Offset(o.x + px, o.y + px), Size(s.width - 2 * px, s.height - 2 * px), style = Stroke(2 * px))
    }
}

// ================================================================ VAPOR_SYNTH_95.EXE

private object SynUi {
    var noteText by mutableStateOf("POLY: READY")
    var xyX = 0.45f
    var xyY = 0.72f
    var version by mutableIntStateOf(0)
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun SynthApp() {
    val core = StudioAudio.core
    val sp = core.synthParams
    val ui = SynUi
    fun bump() { ui.version++; StudioAudio.wake() }
    /** rrSynthPanic(e): e = false also releases HOLD. */
    fun panic(keepHold: Boolean) { core.synth.panic(!keepHold); ui.noteText = "POLY: READY"; bump() }
    LaunchedEffect(Unit) { synthLabels(sp) }
    DisposableEffect(Unit) { onDispose { core.synth.panic(true) } }
    Column(Modifier.fillMaxSize().padding(4.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        ui.version
        StudioLcd("PATCH: INIT — CUSTOM OSCILLATORS", ui.noteText, "SLIDE FINGER ACROSS KEYS OR XY PAD · MULTI-TOUCH ENABLED", "OCT: C${sp.octave}–C${sp.octave + 2}", NeonPink)
        // .rr-synth-perf-bar
        androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Btn("◀ OCT-", { sp.octave = (sp.octave - 1).coerceIn(1, 5); panic(true) }, size = 12.sp)
                Btn("OCT+ ▶", { sp.octave = (sp.octave + 1).coerceIn(1, 5); panic(true) }, size = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Btn("OSC1: ${sp.wave1.label}", { sp.wave1 = Wave.entries[(sp.wave1.ordinal + 1) % 4]; bump() }, size = 12.sp)
                Btn("OSC2: ${sp.wave2.label}", { sp.wave2 = Wave.entries[(sp.wave2.ordinal + 1) % 4]; bump() }, size = 12.sp)
                Btn("FLT: ${sp.filterMode.label}", { sp.filterMode = FilterMode.entries[(sp.filterMode.ordinal + 1) % 3]; bump() }, size = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Btn("CHORD: ${SynthParams.CHORDS[sp.chordIdx].name}", { panic(true); sp.chordIdx = (sp.chordIdx + 1) % SynthParams.CHORDS.size; bump() }, pressed = sp.chordIdx > 0, size = 12.sp)
                Btn("ARP: ${SynthParams.ARPS[sp.arpIdx]}", { sp.arpIdx = (sp.arpIdx + 1) % SynthParams.ARPS.size; if (sp.arpIdx > 0) core.synth.panic(false); bump() }, pressed = sp.arpIdx > 0, size = 12.sp)
                Btn("HOLD", { sp.hold = !sp.hold; if (!sp.hold) panic(true); bump() }, pressed = sp.hold, size = 12.sp)
                Btn("⏹ PANIC", { panic(false) }, size = 12.sp)
            }
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val wide = maxWidth >= 640.dp
            val xy: @Composable (Modifier) -> Unit = { m -> XyBox(m, sp) { bump() } }
            val rack: @Composable (Modifier) -> Unit = { m ->
                Box(m.then(if (Skin.classic) Modifier.background(W95.Face).bevel(false) else Modifier.background(Color(0xFFEEF2F4)).border(1.dp, W95.SoftBorder)).padding(4.dp)) {
                    val u = { readSynthParams(sp); bump() }
                    FxGrid(4, listOf(
                        { mm -> FxCtrl("CUTOFF", "syn95-lbl-cutoff", "syn95-cutoff", u, mm) },
                        { mm -> FxCtrl("RESONANCE", "syn95-lbl-res", "syn95-res", u, mm) },
                        { mm -> FxCtrl("ENV MOD", "syn95-lbl-envmod", "syn95-envmod", u, mm) },
                        { mm -> FxCtrl("OSC2 DETUNE", "syn95-lbl-detune", "syn95-detune", u, mm) },
                        { mm -> FxCtrl("ATTACK", "syn95-lbl-atk", "syn95-atk", u, mm) },
                        { mm -> FxCtrl("DECAY", "syn95-lbl-dec", "syn95-dec", u, mm) },
                        { mm -> FxCtrl("SUSTAIN", "syn95-lbl-sus", "syn95-sus", u, mm) },
                        { mm -> FxCtrl("RELEASE", "syn95-lbl-rel", "syn95-rel", u, mm) },
                        { mm -> FxCtrl("SUB / OSC2", "syn95-lbl-osc2mix", "syn95-osc2mix", u, mm) },
                        { mm -> FxCtrl("CHORUS / LFO", "syn95-lbl-chorus", "syn95-chorus", u, mm) },
                        { mm -> FxCtrl("VAPOR ECHO", "syn95-lbl-delay", "syn95-delay", u, mm) },
                        { mm -> FxCtrl("MASTER GAIN", "syn95-lbl-gain", "syn95-gain", u, mm) },
                    ))
                }
            }
            if (wide) Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) { xy(Modifier.width(138.dp).height(150.dp)); rack(Modifier.weight(1f)) }
            else Column(verticalArrangement = Arrangement.spacedBy(5.dp)) { xy(Modifier.fillMaxWidth().height(95.dp)); rack(Modifier.fillMaxWidth()) }
        }
        SynthKeyboard(Modifier.fillMaxWidth().height(230.dp), sp.octave) { note, down ->
            core.synth.press(note, down); StudioAudio.wake()
            val held = core.synth.held
            ui.noteText = if (held.isNotEmpty()) "NOTE: ${SynthParams.noteName(note)} (${held.size}V)" else "POLY: READY"
        }
        StatusBar2("TOUCH & SLIDE KEYS FOR GLISSANDO · PC KEYS: Z–M & Q–P", "6-VOICE POLYPHONIC | ARP SYNCED TO 909")
    }
}

/** rrSynthUpdateParams: read the rack into the patch and refresh its labels. */
private fun readSynthParams(sp: SynthParams) {
    sp.cutoff = domV("syn95-cutoff"); sp.res = domV("syn95-res"); sp.envMod = domV("syn95-envmod"); sp.detune = domV("syn95-detune")
    sp.atk = domV("syn95-atk"); sp.dec = domV("syn95-dec"); sp.sus = domV("syn95-sus"); sp.rel = domV("syn95-rel")
    sp.osc2Mix = domV("syn95-osc2mix"); sp.chorus = domV("syn95-chorus"); sp.delay = domV("syn95-delay"); sp.gain = domV("syn95-gain")
    synthLabels(sp)
}

private fun synthLabels(sp: SynthParams) {
    fun s(id: String, v: Float) = Dom.el(id).setProp("value", str(v.toDouble()))
    s("syn95-cutoff", sp.cutoff); s("syn95-res", sp.res); s("syn95-envmod", sp.envMod); s("syn95-detune", sp.detune)
    s("syn95-atk", sp.atk); s("syn95-dec", sp.dec); s("syn95-sus", sp.sus); s("syn95-rel", sp.rel)
    s("syn95-osc2mix", sp.osc2Mix); s("syn95-chorus", sp.chorus); s("syn95-delay", sp.delay); s("syn95-gain", sp.gain)
    fun t(id: String, x: String) = Dom.el(id).setProp("textContent", x)
    val c = sp.cutoff.toDouble()
    t("syn95-lbl-cutoff", if (c >= 1e3) "%.1fkHz".format(java.util.Locale.US, c / 1e3) else "${str(c)}Hz")
    t("syn95-lbl-res", "%.1f".format(java.util.Locale.US, sp.res))
    t("syn95-lbl-envmod", "${str(sp.envMod.toDouble())}%"); t("syn95-lbl-detune", "${str(sp.detune.toDouble())}ct")
    t("syn95-lbl-atk", "${(sp.atk * 1e3).roundToInt()}ms"); t("syn95-lbl-dec", "${(sp.dec * 1e3).roundToInt()}ms")
    t("syn95-lbl-sus", "${(sp.sus * 100).roundToInt()}%"); t("syn95-lbl-rel", "${(sp.rel * 1e3).roundToInt()}ms")
    t("syn95-lbl-osc2mix", "${str(sp.osc2Mix.toDouble())}%"); t("syn95-lbl-chorus", "${str(sp.chorus.toDouble())}%")
    t("syn95-lbl-delay", "${str(sp.delay.toDouble())}%"); t("syn95-lbl-gain", "${str(sp.gain.toDouble())}%")
}

/** .rr-synth-xy-box: TOUCH XY VECTOR — x sets cutoff (80·180^x), y sets resonance. */
@Composable
private fun XyBox(modifier: Modifier, sp: SynthParams, onChange: () -> Unit) {
    var ver by remember { mutableIntStateOf(0) }
    Column(modifier.background(Color(0xFF0B0716)).border(1.dp, NeonCyan.copy(alpha = 0.25f)).padding(3.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row { T("TOUCH XY VECTOR", color = NeonCyan, size = 8.sp, bold = true, mono = true, modifier = Modifier.weight(1f)); T("CUT / RES", color = NeonCyan, size = 8.sp, bold = true, mono = true) }
        Canvas(
            Modifier.fillMaxWidth().weight(1f)
                .background(Brush.radialGradient(listOf(NeonPink.copy(alpha = 0.22f), Color(0xF5050212))))
                .pointerInput(Unit) {
                    fun u(o: Offset) {
                        SynUi.xyX = (o.x / size.width).coerceIn(0f, 1f); SynUi.xyY = (o.y / size.height).coerceIn(0f, 1f)
                        val m = Math.round(80 * 180.0.pow(SynUi.xyX.toDouble())).toDouble()
                        val s = Math.round(((1 - SynUi.xyY) * 18 + 0.7) * 10) / 10.0
                        Dom.el("syn95-cutoff").setProp("value", str(m)); Dom.el("syn95-res").setProp("value", str(s))
                        readSynthParams(sp); ver++; onChange()
                    }
                    awaitEachGesture {
                        val d = awaitFirstDown(); d.consume(); u(d.position)
                        while (true) {
                            val ev = awaitPointerEvent()
                            val ch = ev.changes.firstOrNull { it.id == d.id } ?: break
                            if (!ch.pressed) break
                            ch.consume(); u(ch.position)
                        }
                    }
                },
        ) {
            ver
            val w = size.width; val h = size.height
            for (s in 1 until 4) {
                drawLine(NeonCyan.copy(alpha = 0.22f), Offset(w / 4 * s, 0f), Offset(w / 4 * s, h), density)
                drawLine(NeonCyan.copy(alpha = 0.22f), Offset(0f, h / 4 * s), Offset(w, h / 4 * s), density)
            }
            val px = SynUi.xyX * w; val py = SynUi.xyY * h
            drawLine(NeonPink, Offset(px, 0f), Offset(px, h), density); drawLine(NeonPink, Offset(0f, py), Offset(w, py), density)
            drawCircle(NeonCyan, 5f * density, Offset(px, py))
        }
    }
}

/** #syn95-keyboard: 25 keys from C(oct); slide for glissando, multi-touch. */
@Composable
private fun SynthKeyboard(modifier: Modifier, octave: Int, onNote: (Int, Boolean) -> Unit) {
    val time = rememberFrameTime()
    val base = (octave + 1) * 12
    // layout like Ae(): white keys flex, black keys at ((whiteCount - 0.32) / 15) * 100%, 4.4% wide
    val keys = remember(octave) {
        val list = ArrayList<Triple<Int, Boolean, Float>>() // midi, black, left fraction
        var o = 0
        for (i in 0 until 25) {
            val r = base + i; val u = r % 12
            val black = u in intArrayOf(1, 3, 6, 8, 10)
            if (!black) { list.add(Triple(r, false, o / 15f)); o++ } else list.add(Triple(r, true, (o - 0.32f) / 15f))
        }
        list
    }
    fun noteAt(o: Offset, w: Float, h: Float): Int? {
        if (o.x < 0 || o.x > w || o.y < 0 || o.y > h) return null
        if (o.y < h * 0.62f) keys.filter { it.second }.firstOrNull { o.x >= it.third * w && o.x <= (it.third + 0.044f) * w }?.let { return it.first }
        val wi = (o.x / (w / 15f)).toInt().coerceIn(0, 14)
        return keys.filter { !it.second }[wi].first
    }
    Box(modifier.background(Color(0xFF080314)).padding(4.dp)) {
        Canvas(
            Modifier.fillMaxSize().pointerInput(octave) {
                awaitPointerEventScope {
                    val active = HashMap<PointerId, Int>()
                    while (true) {
                        val ev = awaitPointerEvent()
                        for (c in ev.changes) {
                            val n = if (c.pressed) noteAt(c.position, size.width.toFloat(), size.height.toFloat()) else null
                            val old = active[c.id]
                            if (n != old) {
                                if (old != null) { onNote(old, false); active.remove(c.id) }
                                if (n != null) { onNote(n, true); active[c.id] = n }
                            }
                            c.consume()
                        }
                    }
                }
            },
        ) {
            time.value
            val held = StudioAudio.core.synth.held
            val w = size.width; val h = size.height; val ww = w / 15f
            val label = android.graphics.Paint().apply { isAntiAlias = true; textSize = 9f * density; isFakeBoldText = true; color = 0xFF2B1D42.toInt() }
            keys.filter { !it.second }.forEach { (n, _, left) ->
                val x = left * w; val on = n in held
                drawRoundRect(
                    if (on) Brush.verticalGradient(listOf(NeonCyan, Color(0xFFFF77DF))) else Brush.verticalGradient(0f to Color(0xFFF7F4FF), 0.86f to Color(0xFFD9D2EC), 1f to Color(0xFFB2A7D4)),
                    Offset(x, 0f), Size(ww - density, h), CornerRadius(4 * density),
                )
                drawLine(Color(0xFF2B1D42), Offset(x + ww - density, 0f), Offset(x + ww - density, h), density)
                if (n % 12 == 0) drawContext.canvas.nativeCanvas.drawText(SynthParams.noteName(n), x + 5 * density, h - 8 * density, label)
            }
            keys.filter { it.second }.forEach { (n, _, left) ->
                val on = n in held
                val x = left * w; val bw = 0.044f * w
                drawRoundRect(
                    if (on) Brush.verticalGradient(listOf(NeonPink, Color(0xFF7B00FF))) else Brush.verticalGradient(0f to Color(0xFF2D1B4E), 0.85f to Color(0xFF0D061A), 1f to Color.Black),
                    Offset(x, 0f), Size(bw, h * 0.62f), CornerRadius(3 * density),
                )
                drawLine(NeonCyan.copy(alpha = 0.5f), Offset(x, 0f), Offset(x, h * 0.62f), density)
            }
        }
    }
}
