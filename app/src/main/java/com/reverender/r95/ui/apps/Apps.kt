package com.reverender.r95.ui.apps

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reverender.r95.ChatMessage
import com.reverender.r95.LocalAppState
import com.reverender.r95.core.AppInfo
import com.reverender.r95.core.Site
import com.reverender.r95.core.Themes
import com.reverender.r95.net.RevyClient
import com.reverender.r95.ui.desktop.WindowManager
import com.reverender.r95.ui.viz.AnalogVuViz
import com.reverender.r95.ui.viz.CanyonViz
import com.reverender.r95.ui.viz.HexRainViz
import com.reverender.r95.ui.viz.LissajousViz
import com.reverender.r95.ui.viz.PlasmaViz
import com.reverender.r95.ui.viz.TunnelViz
import com.reverender.r95.ui.viz.argb
import com.reverender.r95.ui.win95.Btn
import com.reverender.r95.ui.win95.Check
import com.reverender.r95.ui.win95.Field
import com.reverender.r95.ui.win95.GroupBox
import com.reverender.r95.ui.win95.Slider95
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import com.reverender.r95.ui.win95.bevel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.graphicsLayer
import com.reverender.r95.TaskbarPos
import com.reverender.r95.audio.UiSfx
import com.reverender.r95.ui.viz.BgLayers
import com.reverender.r95.ui.viz.Scenes
import com.reverender.r95.ui.viz.SolidColors
import com.reverender.r95.ui.viz.HyperCubeViz
import com.reverender.r95.ui.viz.WaterfallViz
import com.reverender.r95.ui.viz.RibbonViz
import com.reverender.r95.ui.viz.OrbitalViz
import com.reverender.r95.ui.viz.FractalViz
import com.reverender.r95.ui.viz.PrismViz
import com.reverender.r95.ui.viz.SonarViz
import com.reverender.r95.ui.viz.MagneticViz
import com.reverender.r95.ui.viz.KaleidoViz
import com.reverender.r95.ui.viz.StarforgeViz
import androidx.compose.foundation.shape.CircleShape
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/** Routes an app id to its native screen. */
@Composable
fun AppContent(app: AppInfo, wm: WindowManager, self: com.reverender.r95.ui.desktop.Win? = null) {
    val theme = LocalAppState.current.theme
    androidx.compose.runtime.LaunchedEffect(app.id) { android.util.Log.i("r95", "rendered window ${app.id}") }
    val win = wm.windows.firstOrNull { it.app.id == app.id }
    when (app.id) {
        "player" -> PlayerApp(wm)
        "eq" -> EqApp(win)
        "fx" -> FxApp(win)
        "cassette" -> CassetteApp(win)
        "video" -> VideoApp()
        "revy" -> RevyChatApp()
        "plasma" -> SiteVizWindow(app.id, win)
        "lissajous" -> SiteVizWindow(app.id, win)
        "matrix" -> SiteVizWindow(app.id, win)
        "vumeter" -> SiteVizWindow(app.id, win)
        "cybergrid" -> SiteVizWindow(app.id, win)
        "tunnel" -> SiteVizWindow(app.id, win)
        "hypercube" -> SiteVizWindow(app.id, win)
        "waterfall" -> SiteVizWindow(app.id, win)
        "ribbon" -> SiteVizWindow(app.id, win)
        "orbital" -> SiteVizWindow(app.id, win)
        "fractal" -> SiteVizWindow(app.id, win)
        "prism" -> SiteVizWindow(app.id, win)
        "sonar" -> SiteVizWindow(app.id, win)
        "magnetic" -> SiteVizWindow(app.id, win)
        "kaleido" -> SiteVizWindow(app.id, win)
        "starforge" -> SiteVizWindow(app.id, win)
        "drum909" -> Drum909App()
        "synth95" -> SynthApp()
        "emulator" -> EmulatorApp(wm)
        "emutouch" -> EmuTouchApp()
        "vectordreams" -> VectorDreamsApp()
        "vaporpet" -> PetApp()
        "calc" -> CalcApp()
        "notepad" -> NotepadApp()
        "sticky" -> StickyApp()
        "planner" -> PlannerApp()
        "todo" -> TodoApp()
        "focus" -> FocusApp()
        "clock" -> ClockApp()
        "browser" -> BrowserApp(wm, self?.arg)
        "customize" -> CustomizeApp(wm)
        "help" -> HelpApp(wm)
        "music" -> MusicExplorerApp(wm)
        "monitor" -> CrtMonitorApp(wm)
        "settings" -> SettingsApp(wm)
        else -> AndroidApp()
    }
}

/** Visualiser window body, with a hint when nothing is playing. */
@Composable
private fun VizFrame(content: @Composable () -> Unit) {
    val p = LocalAppState.current.player
    Box(Modifier.fillMaxSize().bevel(false).padding(2.dp)) {
        content()
        if (!p.isPlaying && !com.reverender.r95.audio.AudioBus.studioActive && !com.reverender.r95.audio.MicCapture.running) {
            Box(
                Modifier.align(Alignment.BottomCenter).padding(8.dp).background(Color.Black.copy(alpha = 0.6f))
                    .clickable { p.togglePlay() }.padding(horizontal = 10.dp, vertical = 6.dp),
            ) { T("▶ Play music in VAPOR_AMP to drive the visuals", color = Color.White, size = 11.sp) }
        }
    }
}

// ---------------------------------------------------------------- REVY


// ---------------------------------------------------------------- CUSTOMIZE / SETTINGS

/** Simple wrapping grid inside a scrolling column (no nested lazy lists). */
@Composable
private fun FlowGrid(count: Int, minCellDp: Int, cell: @Composable (Int) -> Unit) {
    androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxWidth()) {
        val cols = (maxWidth.value / minCellDp).toInt().coerceAtLeast(1)
        Column {
            for (r in 0 until (count + cols - 1) / cols) {
                Row(Modifier.fillMaxWidth()) {
                    for (c in 0 until cols) {
                        val i = r * cols + c
                        Box(Modifier.weight(1f)) { if (i < count) cell(i) }
                    }
                }
            }
        }
    }
}

/** SETTINGS.EXE — [Control Panel], laid out like the site's rr-set-grid. */
@Composable
private fun SettingsApp(wm: WindowManager) {
    val state = LocalAppState.current
    val scope = rememberCoroutineScope()
    val fx = com.reverender.r95.core.DisplayFx
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(4.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        GroupBox("Display") {
            Column {
                SetSlider("Window opacity", "${(state.windowOpacity * 100).roundToInt()}%", state.windowOpacity, 0.3f..1f, enabled = true) { state.setWindowOpacity((it * 100).roundToInt() / 100f) }
                SetSlider("Text size", "${(com.reverender.r95.os.OsPrefs.textScale * 100).roundToInt()}%", com.reverender.r95.os.OsPrefs.textScale, 0.9f..1.4f, enabled = true) { com.reverender.r95.os.OsPrefs.setTextScale((it * 20).roundToInt() / 20f) }
                Check("Desktop clock", state.desktopClock, state::setDesktopClock)
                Check("CRT scanline filter", state.scanlines, state::setScanlines)
            }
        }
        GroupBox("Effects & sound") {
            Column {
                Check("Wobbly windows", state.wobble, state::setWobble)
                Check("Cassette deck click sounds", state.uiSfx, state::setUiSfx)
                Check("Mute Backrooms hum", state.muteHum, state::setMuteHum)
            }
        }
        GroupBox("Screensaver") {
            Column {
                Check("Vaporwave screensaver", state.screensaver, state::setScreensaver)
                SetSlider("Start after", "${state.screensaverMin} min", state.screensaverMin.toFloat(), 1f..60f, enabled = state.screensaver) { state.setScreensaverMin(it.roundToInt()) }
                Btn("Preview", { state.saverPreview++ }, Modifier.padding(top = 4.dp))
            }
        }
        GroupBox("Performance") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Check("Full visual quality on this device", !state.lowPower, { v: Boolean -> state.setLowPower(!v) })
                // native: wallpaper render resolution (fewer pixels = smoother on older tablets)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T("Wallpaper resolution", size = 12.sp, bold = true, modifier = Modifier.weight(1f))
                    val ws = com.reverender.r95.os.OsPrefs.wallScale
                    Select(if (ws < 0f) "auto" else ((ws * 100).roundToInt()).toString(),
                        listOf("auto" to "Auto (${(com.reverender.r95.core.Perf.wallScale() * 100).roundToInt()}%)", "100" to "100%", "85" to "85%", "70" to "70%", "55" to "55%", "40" to "40%"),
                        { com.reverender.r95.os.OsPrefs.setWallScale(if (it == "auto") -1f else it.toInt() / 100f) })
                }
                T("Auto lowers the wallpaper's resolution when frames run late and raises it again when there is headroom, aiming for a steady 60 fps.", size = 10.sp, color = W95.Shadow, modifier = Modifier.padding(start = 4.dp))
                T("On slower devices the site lowers or pauses effects by itself to save power. Switch this on to keep every effect at the strength you choose, on every visit. Turning on a display effect switches it on too.", size = 10.sp, color = W95.Shadow, modifier = Modifier.padding(start = 30.dp))
                Box(Modifier.fillMaxWidth().background(Color.White).border(1.dp, W95.Shadow).padding(horizontal = 8.dp, vertical = 6.dp)) {
                    T(fx.statusText, size = 10.sp, bold = true, maxLines = 3)
                }
                Btn(fx.buttonText, { scope.launch { fx.onButton() } }, Modifier.fillMaxWidth().height(40.dp), pressed = fx.mode in listOf("device-max", "full"), size = 13.sp)
                if (fx.mode == "full-pending") Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Btn("CONTINUE WITH ALL EFFECTS", { fx.goFull("user") }, Modifier.weight(1f), size = 10.sp)
                    Btn("RETURN TO MODE 2", { fx.restoreDeviceMax() }, Modifier.weight(1f), size = 10.sp)
                }
                T("AUTOMATIC PERFORMANCE SCALING PROTECTS MEDIA PLAYBACK ON VERY LOW-END DEVICES.", size = 9.sp, color = W95.Shadow)
            }
        }
        AndroidSettingsGroups()
        GroupBox("Taskbar") {
            Column {
                T("Position", size = 12.sp, bold = true)
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.padding(vertical = 3.dp)) {
                    listOf(TaskbarPos.BOTTOM to "⬇ Bottom", TaskbarPos.TOP to "⬆ Top", TaskbarPos.LEFT to "⬅ Left", TaskbarPos.RIGHT to "➡ Right").forEach { (pos, label) ->
                        Btn(label, { state.setTaskbarPos(pos) }, Modifier.weight(1f), pressed = state.taskbarPos == pos, size = 12.sp)
                    }
                }
                Check("Always show taskbar", state.pinTaskbar, state::setPinTaskbar)
                SetSlider("Hide taskbar after", "${state.taskbarHideSec.coerceIn(1, 30)}s", state.taskbarHideSec.coerceIn(1, 30).toFloat(), 1f..30f, enabled = !state.pinTaskbar) { state.setTaskbarHide(it.roundToInt().coerceIn(1, 30)) }
            }
        }
        // .rr-set-actions
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Btn("Tile windows", { wm.tileOpen() }, Modifier.weight(1f))
            Btn("Open all visualizers", { wm.toggleAllVisualizers() }, Modifier.weight(1f), pressed = wm.anyVizOpen)
        }
    }
}

/** .rr-set-slider: name + value box, range below (dimmed when off). */
@Composable
private fun SetSlider(name: String, value: String, v: Float, range: ClosedFloatingPointRange<Float>, enabled: Boolean, onChange: (Float) -> Unit) {
    Column(Modifier.padding(vertical = 3.dp).graphicsLayer { alpha = if (enabled) 1f else 0.5f }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            T(name, size = 12.sp, bold = true, modifier = Modifier.weight(1f))
            Box(Modifier.background(Color.White).border(1.dp, W95.Shadow).padding(horizontal = 6.dp, vertical = 1.dp)) { T(value, size = 10.sp, mono = true, bold = true) }
        }
        Slider95(v, { if (enabled) onChange(it) }, Modifier.fillMaxWidth().height(28.dp), range = range)
    }
}

// ---------------------------------------------------------------- CALC_95

/** CALC_95, ported from rr-os-styles.js ie(): the same keys, memory, chaining and number formatting. */
private val SCI_KEYS = listOf("√", "x²", "xʸ", "1/x", "π", "sin", "cos", "tan", "log", "ln")

private class Calc {
    var g by mutableStateOf("0")       // display value
    var expr by mutableStateOf("")     // D: the expression line
    var mem by mutableStateOf(0.0)     // f
    private var l: Double? = null      // pending left operand
    private var i: String? = null      // pending operator
    private var d = true               // next digit starts a new number

    /** T(): toPrecision(12), exponential past 14 characters, "Error" when not finite. */
    fun fmt(z: Double): String {
        if (!z.isFinite()) return "Error"
        val p = java.math.BigDecimal(z).round(java.math.MathContext(12)).toDouble()
        var s = com.reverender.r95.ui.viz.js.jsStr(p)
        if (s.length > 14) {
            val e = String.format(java.util.Locale.US, "%.7e", z)
            val n = e.substringAfter('e').toInt()
            s = e.substringBefore('e') + "e" + (if (n >= 0) "+" else "-") + kotlin.math.abs(n)
        }
        return s
    }
    private fun w(z: Double, p: Double, k: String?) = when (k) { "+" -> z + p; "−" -> z - p; "×" -> z * p; "÷" -> if (p == 0.0) Double.NaN else z / p; "^" -> Math.pow(z, p); else -> p }
    /** the paper tape: finished calculations, newest first */
    val tape = androidx.compose.runtime.mutableStateListOf<String>()
    private fun pf(s: String) = com.reverender.r95.ui.viz.js.jsParseFloat(s)

    fun key(z: String) {
        when {
            z.length == 1 && z[0].isDigit() -> { g = if (d || g == "0") z else if (g.length < 15) g + z else g; d = false }
            z == "." -> if (d) { g = "0."; d = false } else if (!g.contains('.')) g += "."
            z == "⌫" -> if (!d) g = if (g.length > 1) g.dropLast(1) else "0"
            z == "C" -> { l = null; i = null; g = "0"; d = true; expr = "" }
            z == "±" -> if (g != "0") g = if (g.startsWith("-")) g.drop(1) else "-$g"
            z == "%" -> { g = fmt((l ?: 1.0) * pf(g) / 100); d = true }
            z in listOf("÷", "×", "−", "+") -> {
                val p = pf(g)
                if (l != null && i != null && !d) { l = w(l!!, p, i); g = fmt(l!!) }
                else if (l == null || i == null || !d) l = p
                i = z; d = true
                expr = fmt(l!!) + " " + z
            }
            z == "=" -> if (i != null && l != null) {
                val k = pf(g)
                expr = fmt(l!!) + " " + i + " " + fmt(k) + " ="
                l = w(l!!, k, i); g = fmt(l!!); l = null; i = null; d = true
                tape.add(0, expr + " " + g); while (tape.size > 30) tape.removeAt(tape.size - 1)
            }
            // native upgrade: scientific keys work on the number on the display
            z in SCI_KEYS -> {
                val x = pf(g)
                val r = when (z) {
                    "√" -> kotlin.math.sqrt(x); "x²" -> x * x; "1/x" -> if (x == 0.0) Double.NaN else 1 / x; "π" -> Math.PI
                    "sin" -> kotlin.math.sin(Math.toRadians(x)); "cos" -> kotlin.math.cos(Math.toRadians(x)); "tan" -> kotlin.math.tan(Math.toRadians(x))
                    "log" -> kotlin.math.log10(x); "ln" -> kotlin.math.ln(x); "xʸ" -> { l = x; i = "^"; d = true; expr = fmt(x) + " ^"; return }
                    else -> x
                }
                if (z != "π") { tape.add(0, "$z(${fmt(x)}) = ${fmt(r)}"); while (tape.size > 30) tape.removeAt(tape.size - 1) }
                g = fmt(r); d = true
            }
            z == "MC" -> mem = 0.0
            z == "MR" -> { g = fmt(mem); d = true }
            z == "M+" -> mem += pf(g).takeIf { !it.isNaN() } ?: 0.0
            z == "M−" -> mem -= pf(g).takeIf { !it.isNaN() } ?: 0.0
        }
    }
}

@Composable
private fun CalcApp() {
    val calc = remember { Calc() }
    var sci by remember { mutableStateOf(false) }
    var showTape by remember { mutableStateOf(false) }
    val clip = androidx.compose.ui.platform.LocalClipboardManager.current
    val keys = listOf("MC", "MR", "M+", "M−", "C", "±", "%", "÷", "7", "8", "9", "×", "4", "5", "6", "−", "1", "2", "3", "+", "0", ".", "⌫", "=")
    Column(Modifier.fillMaxSize().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // .rr-calc-lcd
        Column(
            Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color(0xFFC9D6B4), Color(0xFFAEBD96)))).bevel(false).padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.End,
        ) {
            T((if (calc.mem != 0.0) "M  " else "") + calc.expr, size = 11.sp, mono = true, color = Color(0xB31D2B18), maxLines = 1)
            T(calc.g, size = 26.sp, mono = true, bold = true, color = Color(0xFF1D2B18), maxLines = 1)
        }
        // native upgrades: scientific keys, the paper tape and copy
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Btn("SCI", { sci = !sci }, Modifier.weight(1f), pressed = sci, size = 11.sp, bold = true)
            Btn("TAPE", { showTape = !showTape }, Modifier.weight(1f), pressed = showTape, size = 11.sp, bold = true)
            Btn("COPY", { clip.setText(androidx.compose.ui.text.AnnotatedString(calc.g)) }, Modifier.weight(1f), size = 11.sp, bold = true)
        }
        if (showTape) Column(Modifier.fillMaxWidth().height(96.dp).background(Color(0xFFFFFDF2)).bevel(false).verticalScroll(rememberScrollState()).padding(horizontal = 6.dp, vertical = 3.dp)) {
            if (calc.tape.isEmpty()) T("The tape is empty.", size = 10.sp, color = W95.Shadow)
            calc.tape.forEach { line -> T(line, size = 11.sp, mono = true, modifier = Modifier.fillMaxWidth().clickable { calc.key("C"); line.substringAfterLast(' ').forEach { ch -> calc.key(ch.toString()) } }) }
        }
        if (sci) SCI_KEYS.chunked(5).forEach { row ->
            Row(Modifier.fillMaxWidth().height(32.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.forEach { k -> Btn(k, { com.reverender.r95.audio.UiSfx.play(); calc.key(k) }, Modifier.weight(1f).fillMaxSize(), size = 12.sp, bold = true) }
            }
        }
        keys.chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.forEach { k ->
                    Box(Modifier.weight(1f).fillMaxSize()) {
                        Btn(k, { com.reverender.r95.audio.UiSfx.play(); calc.key(k) }, Modifier.fillMaxSize(), size = if (k.startsWith("M")) 11.sp else 15.sp, bold = true)
                        if (k == "=") Box(Modifier.fillMaxSize().padding(2.dp).background(Brush.verticalGradient(listOf(Color(0xFFE07A9B), Color(0xFF1F5C5C))))
                            .clickable { calc.key("=") }, contentAlignment = Alignment.Center) { T("=", color = Color.White, bold = true, size = 15.sp) }
                    }
                }
            }
        }
    }
}

