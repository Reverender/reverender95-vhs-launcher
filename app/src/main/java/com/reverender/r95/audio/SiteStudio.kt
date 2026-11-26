package com.reverender.r95.audio

import com.reverender.r95.AppState
import com.reverender.r95.ui.viz.js.Dom
import com.reverender.r95.ui.viz.js.num
import com.reverender.r95.ui.viz.js.str
import com.reverender.r95.ui.viz.siteviz.g_cassetteReelAngle
import com.reverender.r95.ui.viz.siteviz.g_cassetteScratchActive
import com.reverender.r95.ui.viz.siteviz.g_cassetteTapeProgress
import com.reverender.r95.ui.viz.siteviz.g_cassetteTransportMode
import com.reverender.r95.ui.viz.siteviz.g_currentTapeType
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * GRAPHIC_EQ, DSP_FX_95 and CASSETTE_95 logic, ported from rr-app-core.js (setEqPreamp, setEqBand,
 * applyEqPreset, updateEqStatusBar, updateDspFx, applyFxPreset, updateCassetteDsp, setCassetteTapeType,
 * start/releaseCassetteTransport, cassetteDeckPlay/Pause/Stop/Eject and the reel scratch handlers).
 * The functions read and write the site's own input ids and label ids; the values then drive the
 * native DSP chain (AudioBus.eq / AudioBus.fx) instead of WebAudio nodes.
 */
object SiteStudio {
    private lateinit var state: AppState
    private val p get() = state.player

    private fun v(id: String, def: Double): Double = num(Dom.el(id).prop("value")).let { if (it.isNaN()) def else it }
    private fun setV(id: String, x: Double) = Dom.el(id).setProp("value", str(x))
    private fun txt(id: String, t: String) = Dom.el(id).setProp("textContent", t)
    fun text(id: String): String = str(Dom.el(id).prop("textContent") ?: "")
    private fun js(x: Double): String = str(x)
    private fun fixed(x: Double, d: Int) = "%.${d}f".format(java.util.Locale.US, x)

    var eqPressed = "flat"; private set
    var fxPressed = "clean"; private set

    /** true once the desktop has started (widgets and MIDI use the page's sliders only then) */
    val installed: Boolean get() = ::state.isInitialized
    fun install(s: AppState) {
        state = s
        // EQ: the site's EQ is always in the chain
        if (!s.eqEnabled) s.setEqEnabled(true)
        setV("eq-preamp", s.eqPreamp.toDouble())
        txt("eq-pre-val", "${if (s.eqPreamp > 0) "+" else ""}${js(s.eqPreamp.toDouble())}dB")
        for (i in 0 until 8) {
            val g = s.eqGains[i].toDouble()
            setV("eq-b-$i", g); txt("eq-v-$i", "${if (g > 0) "+" else ""}${g.roundToInt()}")
        }
        eqPressed = s.eqPreset.lowercase().takeIf { it in EQ_KEYS } ?: ""
        txt("eq-status-curve", "3D BIQUAD SPLINE MATRIX"); txt("eq-status-gain", "MAX BOOST: +0.0 dB")
        // FX: mirror the native settings into the rack's inputs
        val f = AudioBus.fx
        setV("fx-speed", p.speed.toDouble()); setV("fx-lpf", f.lpf.toDouble()); setV("fx-hpf", f.hpf.toDouble())
        setV("fx-dtime", f.echoTime.toDouble()); setV("fx-dmix", f.echoMix.toDouble()); setV("fx-dfdbk", f.echoFeedback.toDouble())
        setV("fx-drive", f.drive.toDouble()); setV("fx-sub", f.subDb.toDouble()); setV("fx-chorus", f.chorus.toDouble())
        setV("fx-tremolo", f.tremolo.toDouble()); setV("fx-ring", f.ring.toDouble()); setV("fx-pan", f.pan.toDouble())
        setV("fx-reverb", f.reverb.toDouble()); setV("fx-crush", f.crush.toDouble()); setV("fx-phaser", f.phaser.toDouble())
        setV("fx-flanger", f.flanger.toDouble()); setV("fx-width", f.width.toDouble()); setV("fx-comp", f.comp.toDouble())
        fxPressed = s.fxPreset.takeIf { k -> FxSettings.PRESETS.any { it.first == k } } ?: ""
        labelsDspFx()
        // CASSETTE: the site's initial transport state
        if (g_cassetteTransportMode.isEmpty()) g_cassetteTransportMode = "play"
        if (g_currentTapeType.isEmpty()) g_currentTapeType = "type1"
        if (g_cassetteTapeProgress == 0.0) g_cassetteTapeProgress = 0.25
        setV("cas-wow", 0.0); setV("cas-hiss", 0.0); setV("cas-bias", 0.0)
        txt("cas-lbl-wow", "0%"); txt("cas-lbl-hiss", "0%"); txt("cas-lbl-bias", "0%")
        txt("cas-status-mode", "DECK: PLAY [NORMAL BIAS 120µs]"); txt("cas-status-counter", "CNT: 0000 | RPM: 4.76cm/s")
        AudioBus.fx.wow = 0f; AudioBus.fx.hiss = 0f; AudioBus.fx.bias = 0f; AudioBus.fx.touch()
    }

    // ================================================================ GRAPHIC_EQ
    private val EQ_KEYS = listOf("flat", "vapor", "bass", "radio", "cyber")

    private fun updateEqStatusBar() {
        var e = -12.0; var a = 12.0
        for (t in 0 until 8) { val l = v("eq-b-$t", 0.0); if (l > e) e = l; if (l < a) a = l }
        txt("eq-status-gain", "RANGE: ${if (a > 0) "+" else ""}${fixed(a, 1)} / +${fixed(max(0.0, e), 1)} dB")
    }

    fun setEqPreamp(a: Double) {
        setV("eq-preamp", a)
        txt("eq-pre-val", "${if (a > 0) "+" else ""}${js(a)}dB")
        state.setEqPreamp(a.toFloat())
    }

    fun setEqBand(e: Int, o: Double) {
        setV("eq-b-$e", o)
        txt("eq-v-$e", "${if (o > 0) "+" else ""}${o.roundToInt()}")
        state.setEqBand(e, o.toFloat())
        updateEqStatusBar()
    }

    fun applyEqPreset(e: String) {
        val a = com.reverender.r95.core.Catalog.eqPresets[e.uppercase()] ?: com.reverender.r95.core.Catalog.eqPresets.getValue("FLAT")
        eqPressed = e
        a.forEachIndexed { t, r -> setEqBand(t, r.toDouble()) }
        state.applyEqPreset(e.uppercase())
        txt("eq-status-curve", "PRESET: ${e.uppercase()} CURVE")
    }

    // ================================================================ DSP_FX_95
    private fun hzLbl(x: Double) = if (x >= 1e3) "${fixed(x / 1e3, 1)}k" else "${x.roundToInt()}Hz"

    private fun labelsDspFx() {
        val e = v("fx-speed", 1.0); val a = v("fx-lpf", 2e4); val o = v("fx-hpf", 20.0); val r = v("fx-dtime", .28)
        val t = v("fx-dmix", 0.0); val l = v("fx-dfdbk", .38); val u = v("fx-drive", 0.0); val f = v("fx-sub", 0.0)
        val m = v("fx-chorus", 0.0); val n = v("fx-tremolo", 0.0); val s = v("fx-ring", 0.0); val i = v("fx-pan", 0.0)
        val g = v("fx-reverb", 0.0); val cr = v("fx-crush", 0.0); val d = v("fx-phaser", 0.0); val y = v("fx-flanger", 0.0)
        val c = v("fx-width", 0.0); val sc = v("fx-comp", 0.0)
        txt("fx-lbl-speed", "${fixed(e, 2)}x"); txt("fx-lbl-lpf", hzLbl(a)); txt("fx-lbl-hpf", hzLbl(o))
        txt("fx-lbl-dtime", "${(r * 1e3).roundToInt()}ms"); txt("fx-lbl-dmix", "${(t * 100).roundToInt()}%")
        txt("fx-lbl-dfdbk", "${(l * 100).roundToInt()}%"); txt("fx-lbl-drive", "${u.roundToInt()}%")
        txt("fx-lbl-sub", "+${fixed(f, 1)}dB"); txt("fx-lbl-chorus", "${m.roundToInt()}%"); txt("fx-lbl-tremolo", "${n.roundToInt()}%")
        txt("fx-lbl-ring", "${s.roundToInt()}%")
        txt("fx-lbl-pan", if (i == 0.0) "CTR" else if (i < 0) "L${(abs(i) * 100).roundToInt()}" else "R${(i * 100).roundToInt()}")
        txt("fx-lbl-reverb", "${g.roundToInt()}%"); txt("fx-lbl-crush", "${cr.roundToInt()}%"); txt("fx-lbl-phaser", "${d.roundToInt()}%")
        txt("fx-lbl-flanger", "${y.roundToInt()}%"); txt("fx-lbl-width", "${c.roundToInt()}%"); txt("fx-lbl-comp", "${sc.roundToInt()}%")
        val vv = ((m + n + s + t * 100 + u + g + cr + d + y + c + sc) / 11).roundToInt()
        txt("fx-status-chain", "SPD:${fixed(e, 2)}x | REV:${g.roundToInt()}% | ECHO:${(t * 100).roundToInt()}%")
        txt("fx-status-mod", "18-CH MATRIX: $vv%")
    }

    fun updateDspFx() {
        val e = v("fx-speed", 1.0)
        if (g_cassetteTransportMode == "play" || g_cassetteTransportMode.isEmpty()) p.changeSpeed(e.toFloat())
        labelsDspFx()
        val fx = AudioBus.fx
        fx.lpf = v("fx-lpf", 2e4).toFloat(); fx.hpf = v("fx-hpf", 20.0).toFloat(); fx.echoTime = v("fx-dtime", .28).toFloat()
        fx.echoMix = v("fx-dmix", 0.0).toFloat(); fx.echoFeedback = v("fx-dfdbk", .38).toFloat(); fx.drive = v("fx-drive", 0.0).toFloat()
        fx.subDb = v("fx-sub", 0.0).toFloat(); fx.chorus = v("fx-chorus", 0.0).toFloat(); fx.tremolo = v("fx-tremolo", 0.0).toFloat()
        fx.ring = v("fx-ring", 0.0).toFloat(); fx.pan = v("fx-pan", 0.0).toFloat(); fx.reverb = v("fx-reverb", 0.0).toFloat()
        fx.crush = v("fx-crush", 0.0).toFloat(); fx.phaser = v("fx-phaser", 0.0).toFloat(); fx.flanger = v("fx-flanger", 0.0).toFloat()
        fx.width = v("fx-width", 0.0).toFloat(); fx.comp = v("fx-comp", 0.0).toFloat()
        fx.touch()
    }

    fun applyFxPreset(e: String) {
        fxPressed = e
        val t = FxSettings()
        val speed = FxSettings.applyPreset(t, e)
        setV("fx-speed", speed.toDouble()); setV("fx-lpf", t.lpf.toDouble()); setV("fx-hpf", t.hpf.toDouble())
        setV("fx-dtime", t.echoTime.toDouble()); setV("fx-dmix", t.echoMix.toDouble()); setV("fx-dfdbk", t.echoFeedback.toDouble())
        setV("fx-drive", t.drive.toDouble()); setV("fx-sub", t.subDb.toDouble()); setV("fx-chorus", t.chorus.toDouble())
        setV("fx-tremolo", t.tremolo.toDouble()); setV("fx-ring", t.ring.toDouble()); setV("fx-pan", t.pan.toDouble())
        setV("fx-reverb", t.reverb.toDouble()); setV("fx-crush", t.crush.toDouble()); setV("fx-phaser", t.phaser.toDouble())
        setV("fx-flanger", t.flanger.toDouble()); setV("fx-width", t.width.toDouble()); setV("fx-comp", t.comp.toDouble())
        state.applyFxPreset(e)
        updateDspFx()
    }

    // ================================================================ CASSETTE_95
    private var cassetteHoldStart = 0L
    private var cassetteLatched = false
    private var scratchVelocity = 0.0

    private fun click(f: Double, d: Double) = UiSfx.play()

    fun updateCassetteDsp() {
        val e = v("cas-wow", 12.0); val a = v("cas-hiss", 8.0); val o = v("cas-bias", 25.0)
        txt("cas-lbl-wow", "${e.roundToInt()}%"); txt("cas-lbl-hiss", "${a.roundToInt()}%"); txt("cas-lbl-bias", "${o.roundToInt()}%")
        AudioBus.fx.wow = e.toFloat(); AudioBus.fx.hiss = a.toFloat(); AudioBus.fx.bias = o.toFloat(); AudioBus.fx.touch()
        updateDspFx()
    }

    fun setCassetteTapeType(e: String) {
        g_currentTapeType = e
        when (e) {
            "type1" -> { setV("cas-wow", 14.0); setV("cas-hiss", 14.0); setV("cas-bias", 38.0) }
            "type2" -> { setV("cas-wow", 8.0); setV("cas-hiss", 6.0); setV("cas-bias", 18.0) }
            "type4" -> { setV("cas-wow", 4.0); setV("cas-hiss", 3.0); setV("cas-bias", 8.0) }
            "worn" -> { setV("cas-wow", 76.0); setV("cas-hiss", 38.0); setV("cas-bias", 82.0) }
        }
        click(240.0, .04)
        updateCassetteDsp()
    }

    /** engageCassetteCueAudio(false): back to the rack's tape speed. */
    private fun cueOff() { p.setRate(v("fx-speed", 1.0).toFloat()) }

    fun startCassetteTransport(e: String) {
        click(310.0, .04)
        if (g_cassetteTransportMode == e && cassetteLatched) { cassetteDeckPlay(); return }
        cassetteHoldStart = System.currentTimeMillis()
        cassetteLatched = false
        g_cassetteTransportMode = e
        if (!p.isPlaying) AmpEngine.playCurrentAudio(if (e == "ff") "FF CUE" else "REW CUE")
        txt("cas-status-mode", if (e == "ff") "DECK: ⏩ FAST FORWARD CUEING..." else "DECK: ⏪ REWIND CUEING...")
    }

    fun releaseCassetteTransport(e: String, leave: Boolean = false) {
        if (g_cassetteTransportMode != e) return
        val o = System.currentTimeMillis() - cassetteHoldStart
        if (leave && cassetteHoldStart == 0L) return
        if (!leave && o < 240 && !cassetteLatched) { cassetteLatched = true; return }
        if (!cassetteLatched) cassetteDeckPlay()
    }

    fun cassetteDeckPlay() {
        click(220.0, .045)
        g_cassetteTransportMode = "play"
        cassetteLatched = false
        cueOff()
        if (!p.isPlaying) AmpEngine.togglePlayPause()
        updateCassetteDsp()
        txt("cas-status-mode", "DECK: ▶ PLAY [${g_currentTapeType.uppercase()}]")
    }

    fun cassetteDeckPause() {
        click(160.0, .05)
        cueOff()
        if (p.isPlaying) AmpEngine.togglePlayPause()
        g_cassetteTransportMode = "pause"
        cassetteLatched = false
        updateCassetteDsp()
        txt("cas-status-mode", "DECK: ⏸ PAUSED [PINCH ROLLER IDLE]")
    }

    fun cassetteDeckStop() {
        click(130.0, .06)
        cueOff()
        if (p.isPlaying) AmpEngine.togglePlayPause()
        g_cassetteTransportMode = "stop"
        cassetteLatched = false
        updateCassetteDsp()
        txt("cas-status-mode", "DECK: ⏹ STOPPED")
    }

    fun cassetteDeckEject() {
        click(420.0, .07)
        cueOff()
        g_cassetteTransportMode = "play"
        cassetteLatched = false
        AmpEngine.playRandomTrackAndOffset()
        txt("cas-status-mode", "EJECT -> LOADED TRK ${"%02d".format(p.deckIndex + 1)}")
    }

    // reel scratching (pointer handlers on canvas-cassette); dx in CSS px
    fun scratchDown() { g_cassetteScratchActive = true }

    fun scratchMove(l: Double) {
        if (!g_cassetteScratchActive) return
        scratchVelocity = l * 0.22
        g_cassetteReelAngle -= l * 0.045
        val n = max(0.45, min(2.6, 1 + l * 0.06))
        val d = p.durationMs() / 1000.0
        if (d > 0) {
            val cur = p.positionMs() / 1000.0
            p.seekTo((max(0.0, min(d - 0.2, cur + l * 0.18)) * 1000).toLong())
        }
        p.setRate(n.toFloat())
    }

    fun scratchUp() {
        if (!g_cassetteScratchActive) return
        g_cassetteScratchActive = false
        scratchVelocity = 0.0
        if (g_cassetteTransportMode == "play") cueOff()
    }

    val transportMode: String get() = g_cassetteTransportMode
    val tapeType: String get() = g_currentTapeType
}
