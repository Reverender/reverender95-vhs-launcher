package com.reverender.r95.ui.viz.site

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import com.reverender.r95.audio.AudioBus
import com.reverender.r95.audio.MicCapture
import com.reverender.r95.audio.PlayerConnection
import com.reverender.r95.audio.UiSfx
import com.reverender.r95.core.Catalog
import com.reverender.r95.ui.viz.js.Dom
import com.reverender.r95.ui.viz.js.Ext
import com.reverender.r95.ui.viz.js.JsArr
import com.reverender.r95.ui.viz.js.JsEl
import com.reverender.r95.ui.viz.js.Rt
import com.reverender.r95.ui.viz.js.num
import com.reverender.r95.ui.viz.js.str
import com.reverender.r95.ui.viz.siteviz.SiteVizInit
import com.reverender.r95.ui.viz.siteviz.rrVizFrame
import com.reverender.r95.ui.viz.siteviz.rrVizTheme
import org.json.JSONObject

/**
 * Hosts the website's translated code: its page elements (with the site's default values),
 * its audio element (mapped onto the native player), and the per-frame tick of the site's animate().
 */
object SiteHost {
    private var inited = false
    private lateinit var player: PlayerConnection
    var dt = 1 / 60.0; private set
    var timeSec = 0.0; private set
    private var lastNanos = 0L
    private var subBassAvg = 0.25
    private var lastKickNanos = 0L
    private var vizThemeKey = ""
    private var vizTheme: Any? = null

    /** Ticks once per rendered frame; site canvases observe it to redraw. */
    var frame by mutableLongStateOf(0L); private set
    /** the wallpaper's redraw signal: follows [frame], at a lower rate while the user touches a weak phone */
    var wallFrame by mutableLongStateOf(0L); private set
    /**
     * Signal for window UI that mirrors the site's page text (LCD, status cells, slider values).
     * It steps about 11 times a second, and right away after a touch, instead of every animation frame,
     * so whole windows are not recomposed 60 times a second. Canvases keep following [frame].
     */
    var uiFrame by mutableLongStateOf(0L); private set
    private var lastUiNanos = 0L
    @Volatile private var uiDirty = false
    fun uiNow() { uiDirty = true }
    /** a wallpaper picture finished on the wallpaper thread */
    var wallReady by mutableLongStateOf(0L); private set
    internal fun wallpaperReady() { wallReady++ }
    private var lastWallNanos = 0L

    /** Visualizer canvases currently on screen (canvasWindowIsActive / rrWindowHidden). */
    val activeCanvases = HashSet<String>()

    /** Called on bass kicks while BASS GLITCH is on. */
    var onKick: ((Double) -> Unit)? = null

    val canvasIds = listOf(
        "canvas-hypercube", "canvas-waterfall", "canvas-tunnel", "canvas-plasma", "canvas-lissajous", "canvas-cybergrid", "canvas-matrix",
        "canvas-ribbon", "canvas-vumeter", "canvas-orbital", "canvas-fractal", "canvas-prism", "canvas-sonar", "canvas-magnetic", "canvas-kaleido",
        "canvas-starforge", "canvas-eq", "canvas-fx", "canvas-cassette",
    )

    fun install(context: Context, p: PlayerConnection) {
        player = p
        Rt.store = context.getSharedPreferences("r95_site", Context.MODE_PRIVATE)
        if (inited) return
        // page elements with the website's defaults
        runCatching {
            val o = JSONObject(context.assets.open("site/inputs.json").bufferedReader().readText())
            for (k in o.keys()) {
                val d = o.getJSONObject(k)
                val e = JsEl(k)
                if (d.optString("type") == "checkbox") e["checked"] = d.optBoolean("value") else e["value"] = d.optString("value")
                e["min"] = d.optString("min"); e["max"] = d.optString("max"); e["step"] = d.optString("step")
                Dom.register(e)
            }
        }
        runCatching {
            val o = JSONObject(context.assets.open("site/statusbars.json").bufferedReader().readText())
            for (k in o.keys()) {
                val cells = o.getJSONArray(k)
                for (i in 0 until cells.length()) {
                    val c = cells.getJSONArray(i)
                    if (c.getString(0).isNotEmpty()) Dom.el(c.getString(0)).setProp("textContent", c.getString(1))
                }
            }
        }
        for (id in canvasIds) {
            val c = Dom.el(id); if (c["width"] == null) { c["width"] = 300.0; c["height"] = 120.0 }
            Dom.el(id.replace("canvas-", "win-"))
        }
        Dom.documentElement.attrs["data-rr-os"] = "longhorn"

        // site functions that live natively
        Ext.fns["canvasWindowIsActive"] = { a -> (a.getOrNull(0) as? JsEl)?.id in activeCanvases }
        Ext.fns["rrWindowHidden"] = { a -> (a.getOrNull(0) as? JsEl)?.id?.replace("win-", "canvas-") !in activeCanvases }
        Ext.fns["ensureDspChain"] = { null }
        Ext.fns["cassetteDeckPlay"] = { if (!player.isPlaying) player.play(); null }
        Ext.fns["setVideo95YoutubeCassetteRate"] = { null }
        Ext.fns["playMechanicalClick"] = { UiSfx.play(); null }
        Ext.getters["isPlaying"] = { player.isPlaying || MicCapture.running || AudioBus.studioActive }
        Ext.getters["useSynthFallback"] = { false }
        Ext.getters["TRACK_TITLES"] = { JsArr().also { a -> Catalog.deck.forEach { a.add(it.title) } } }
        Ext.getters["TRACK_SOURCES"] = { JsArr().also { a -> Catalog.deck.forEach { a.add(it.url) } } }
        Ext.getters["currentTrackIndex"] = { player.deckIndex.toDouble() }
        Ext.vars["audioEl"] = AudioElement
        Ext.getters["THEMES"] = { com.reverender.r95.ui.viz.siteviz.g_THEMES }
        Ext.getters["currentThemeKey"] = { themeKey }
        Dom.el("mini-osc").let { if (it["width"] == null) { it["width"] = 260.0; it["height"] = 32.0 } }
        ensureInit()
    }

    fun ensureInit() {
        if (inited) return
        inited = true
        SiteWallpaper.initTranslated()
        SiteVizInit()
        com.reverender.r95.ui.viz.sitemini.SiteMiniInit()
    }
    private var themeKey = "vaporwave"


    /** The site's `audioEl`, backed by the native player. */
    private object AudioElement : JsEl("audio") {
        init {
            getter = { k ->
                when (k) {
                    "currentTime" -> player.positionMs() / 1000.0
                    "duration" -> player.durationMs().let { if (it > 0) it / 1000.0 else Double.NaN }
                    "paused" -> !player.isPlaying
                    "ended" -> false
                    "playbackRate" -> player.rate.toDouble()
                    else -> null
                }
            }
            onSet = { k, v ->
                when (k) {
                    "currentTime" -> player.seekTo((num(v) * 1000).toLong())
                    "playbackRate" -> player.setRate(num(v).toFloat())
                }
            }
        }
    }

    /** One frame of the site's animate(): spectrum, smoothing, bass kicks, timers, visualizers. */
    /** last desktop tick (other renderers step the site's audio smoothing themselves when it is idle) */
    @Volatile var lastTickNanos = 0L; private set
    private var vizDtAcc = 0.0; private var vizSkip = 0
    fun tick(nowNanos: Long, themeId: String, reactive: Boolean, bassGlitch: Boolean) {
        ensureInit()
        lastTickNanos = System.nanoTime()
        dt = if (lastNanos == 0L) 1 / 60.0 else ((nowNanos - lastNanos) / 1e9).coerceIn(0.001, 0.1)
        lastNanos = nowNanos
        val y = nowNanos / 1e9 % 100000.0
        timeSec = y
        val playing = reactive && (player.isPlaying || MicCapture.running || AudioBus.studioActive || com.reverender.r95.audio.SystemVisualizer.running)
        val audio = AudioBus.currentFrame()
        val fd = Rt.freqDataArr; val td = Rt.timeDataArr
        if (playing) {
            var sum = 0.0
            for (i in 0 until 256) { fd[i] = audio.web[i].toDouble(); if (i < 32) sum += fd[i] }
            for (i in 0 until 256) td[i] = 128.0 + audio.wave[i] * 127.0
            if (sum == 0.0) {
                val ph = (y * (125.0 / 60.0)) % 1.0
                val k = Math.exp(-ph * 9.5)
                for (b in 0 until 256) {
                    val l = if (b < 8) k * 245 else 0.0
                    fd[b] = minOf(255.0, Math.floor(l + (Math.sin(y * 6 + b * 0.25) * 0.5 + 0.5) * 165 * Math.exp(-b / 95.0)))
                    td[b] = 128 + Math.sin(y * 18 + b * 0.28) * (42 + k * 42)
                }
            }
        } else {
            for (i in 0 until 256) { fd[i] = maxOf(0.0, fd[i] * 0.92); td[i] = 128 + Math.sin(y * 2 + i * 0.15) * 12 }
        }
        var p = 0.0
        for (i in 0 until 6) p += fd[i]
        val w = p / (6 * 255)
        val a = w - subBassAvg
        subBassAvg = subBassAvg * 0.88 + w * 0.12
        if (bassGlitch && playing && ((w > 0.26 && a > 0.038) || (w > 0.66 && a > 0.015)) && nowNanos - lastKickNanos > 155_000_000L) {
            val amt = minOf(1.2, 0.72 + a * 4.5)
            rrWallpaperTrigger(amt)
            onKick?.invoke(amt)
            com.reverender.r95.os.Haptics.thump(amt)
            lastKickNanos = nowNanos
        }
        rrAudioFrame(dt)
        Rt.runTimers()
        themeKey = themeId
        if ("mini-osc" in activeCanvases) runCatching { com.reverender.r95.ui.viz.sitemini.rrMiniOscFrame(Rt.nowMs()) }
        if (activeCanvases.any { it != "mini-osc" }) {
            if (themeId != vizThemeKey) { vizThemeKey = themeId; vizTheme = rrVizTheme(themeId) }
            try {
                // touch priority on weaker phones: visualizer windows draw every other frame while a finger is down
                // (their clock still advances by the full time), leaving the frame to the drag or scroll
                vizDtAcc += dt
                if (!(com.reverender.r95.core.Perf.touching && com.reverender.r95.core.Perf.weak && (++vizSkip and 1) == 1)) {
                    val vdt = vizDtAcc; vizDtAcc = 0.0
                    com.reverender.r95.core.Perf.time(1) { rrVizFrame(y, vdt, rrSmooth(0.0), rrSmooth(1.0), rrSmooth(2.0), rrSmooth(3.0), vizTheme) }
                }
            } catch (e: Throwable) { android.util.Log.w("R95", "viz frame failed", e) }
        }
        frame++
        // page-text mirroring: ~11 Hz, ~4 Hz while scrolling/dragging (a slider being dragged still updates at once)
        val uiGap = if (com.reverender.r95.core.Perf.touching) 250_000_000L else 88_000_000L
        if (uiDirty || nowNanos - lastUiNanos >= uiGap) { uiDirty = false; lastUiNanos = nowNanos; uiFrame++ }
        if (!com.reverender.r95.core.Perf.desktopCovered && nowNanos - lastWallNanos >= com.reverender.r95.core.Perf.bgStepNs - 2_000_000L) { lastWallNanos = nowNanos; wallFrame++; SiteWallpaper.kick() }
    }

    fun text(id: String): String = str(Dom.el(id).prop("textContent") ?: "")
}
