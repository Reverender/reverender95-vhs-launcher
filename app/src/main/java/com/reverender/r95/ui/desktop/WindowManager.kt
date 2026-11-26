package com.reverender.r95.ui.desktop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.reverender.r95.core.AppInfo
import com.reverender.r95.core.Apps

/** One open window. Positions and sizes are in pixels of the desktop area. */
class Win(val app: AppInfo, val key: Int) {
    var x by mutableFloatStateOf(0f)
    var y by mutableFloatStateOf(0f)
    var w by mutableFloatStateOf(0f)
    var h by mutableFloatStateOf(0f)
    var minimized by mutableStateOf(false)
    /** the content-fit pass has run for this placement */
    var fitDone = false
    /** start argument for cloned windows (NET_SURF_95 ＋ NEW) */
    var arg: String? = null
    var maximized by mutableStateOf(false)
    /** .rr-resize-corner-hint: shown for 5.2 s when the window appears after boot. */
    var resizeHintUntil by mutableStateOf(0L)
    val wobble = Wobble()
    /** The site's visualizer FX panel (title-bar FX button) and this window's canvas filter. */
    var fxOpen by mutableStateOf(false)
    /** The site's window shape (cycleWindowShape): box, octagon, crt, trapezoid. */
    var shape by mutableStateOf("box")
    val vizFx = com.reverender.r95.ui.viz.site.VizFx()
}

/** Window rectangles the website uses on a 390-px-wide phone (x, y, w, h in CSS px). */
object SiteRects {
    private var rects: Map<String, IntArray> = emptyMap()
    fun load(ctx: android.content.Context) {
        rects = runCatching {
            val o = org.json.JSONObject(ctx.assets.open("site/window-rects.json").bufferedReader().readText())
            o.keys().asSequence().associateWith { k -> o.getJSONArray(k).let { a -> IntArray(4) { a.getInt(it) } } }
        }.getOrDefault(emptyMap())
    }
    fun of(appId: String): IntArray? = rects[com.reverender.r95.core.siteWindowId(appId)]
}

/** Z-ordered list of windows (last = on top), like the website's window manager. */
/** Titles the apps' own code changes at runtime (the site writes .win95-title-text span). */
object WindowTitles { val override = androidx.compose.runtime.mutableStateMapOf<String, String>() }
fun Win.title(): String = WindowTitles.override[app.id] ?: app.windowTitle

/** VIZ_WINDOW_IDS of rr-app-core.js. */
val VIZ_IDS = listOf("cassette", "eq", "fx", "hypercube", "waterfall", "tunnel", "plasma", "lissajous", "cybergrid", "matrix", "ribbon", "vumeter", "orbital", "fractal", "prism", "sonar", "magnetic", "kaleido", "starforge")

class WindowManager {
    val windows = mutableStateListOf<Win>()
    var startOpen by mutableStateOf(false)
    /** swipe-down app search over the desktop */
    var searchOpen by mutableStateOf(false)
    private val bootAt = System.currentTimeMillis()
    private var nextKey = 1
    private var cascade = 0

    var areaW = 0f; var areaH = 0f; var density = 1f
    /** Phones open windows maximised; tablets get floating windows. */
    val compact: Boolean get() = areaW / density < 600f

    val top: Win? get() = windows.lastOrNull { !it.minimized }

    /** createBrowserWindow(): a further NET_SURF_95 window */
    fun openNewBrowser(url: String) {
        val app = Apps.byId("browser") ?: return
        val win = Win(app, nextKey++).also { it.arg = url }
        place(win); win.maximized = false; cascade++
        windows.add(win); win.wobble.pop(); focus(win)
    }

    /** openRetroTouchControlsWindow() — le(): bottom-right, 220–430 × 190–320 CSS px */
    fun openEmuTouch() {
        open("emutouch")
        val w = windows.firstOrNull { it.app.id == "emutouch" } ?: return
        val d = density * uiScale; val n = maxOf(240f, areaW / d); val o = maxOf(200f, areaH / d + 40f)
        val a = maxOf(220f, minOf(430f, n - 16)); val r = maxOf(190f, minOf(320f, Math.round(o * 0.36f).toFloat()))
        w.w = a * d; w.h = r * d; w.x = maxOf(4f, n - a - 10) * d; w.y = minOf(maxOf(38f, o - r - 48), areaH / d - r).coerceAtLeast(0f) * d
        w.maximized = false
    }
    /** closeRetroTouchControlsWindow() */
    fun closeEmuTouch() { windows.firstOrNull { it.app.id == "emutouch" }?.let { minimize(it) } }

    /** Start menu items: open the app (the menu stays open), or close it when it is already open. */
    fun toggleFromStart(appId: String) {
        val w = windows.firstOrNull { it.app.id == appId }
        if (w != null && !w.minimized) { close(w); return }
        val keep = startOpen
        open(appId)
        startOpen = keep
    }

    fun open(appId: String) {
        startOpen = false
        val existing = windows.firstOrNull { it.app.id == appId }
        if (existing != null) { if (existing.minimized) existing.resizeHintUntil = System.currentTimeMillis() + 5200; existing.minimized = false; focus(existing); existing.wobble.pop(); return }
        val app = Apps.byId(appId) ?: return
        val win = Win(app, nextKey++)
        place(win)
        win.maximized = false
        if (System.currentTimeMillis() - bootAt > 2500) win.resizeHintUntil = System.currentTimeMillis() + 5200
        cascade++
        windows.add(win)
        win.wobble.pop()
        android.util.Log.i("r95", "opened window ${app.id}")
    }

    /** smartPlaceAndSizeWindow: the site's rectangle for this window, scaled to this screen. */
    /** Grows or shrinks a window by [delta] px so its content fits, kept inside the desktop. */
    fun fitHeight(win: Win, delta: Float) {
        val minH = 120f * density
        val h = (win.h + delta).coerceIn(minH, areaH)
        win.h = h
        if (win.y + h > areaH) win.y = (areaH - h).coerceAtLeast(0f)
    }

    /** rr-master-patch's sizing table: w, h, wf, hf, minW, minH (CSS px). */
    private val SMART = mapOf(
        "win-game-vectordreams" to floatArrayOf(940f, 660f, 0.86f, 0.84f, 320f, 380f),
        "win-settings" to floatArrayOf(390f, 390f, 0.4f, 0.5f, 290f, 280f), // scrolls: no need to fill the screen
        "win-clock-themes" to floatArrayOf(460f, 400f, 0.95f, 0.7f, 280f, 260f),
        "win-game-spelunky" to floatArrayOf(860f, 590f, 0.82f, 0.8f, 320f, 340f),
        "win-vaporpet" to floatArrayOf(360f, 560f, 0.95f, 0.9f, 280f, 300f),
        "win-sticky" to floatArrayOf(300f, 340f, 0.9f, 0.6f, 220f, 240f),
        "win-notepad" to floatArrayOf(560f, 440f, 0.95f, 0.7f, 280f, 260f),
        "win-calc" to floatArrayOf(270f, 390f, 0.9f, 0.7f, 230f, 330f),
        "win-planner" to floatArrayOf(420f, 500f, 0.95f, 0.8f, 290f, 380f),
        "win-todo" to floatArrayOf(340f, 430f, 0.95f, 0.7f, 250f, 280f),
        "win-focus" to floatArrayOf(300f, 420f, 0.9f, 0.75f, 250f, 360f),
        "win-browser" to floatArrayOf(780f, 540f, 0.76f, 0.78f, 310f, 340f),
        "win-customize" to floatArrayOf(470f, 400f, 0.5f, 0.5f, 300f, 300f),
        "win-fx" to floatArrayOf(470f, 435f, 0.5f, 0.68f, 300f, 340f),
        "win-cassette" to floatArrayOf(445f, 395f, 0.46f, 0.62f, 290f, 310f),
        // rr-media-app-compact-launch + the windows' own HTML sizes
        "win-game-emulator" to floatArrayOf(480f, 345f, 0.62f, 0.5f, 260f, 220f),
        "win-video-streams" to floatArrayOf(540f, 480f, 0.62f, 0.6f, 300f, 300f),
        "win-revy-chat" to floatArrayOf(430f, 520f, 0.4f, 0.6f, 260f, 320f),
        "win-emutouch" to floatArrayOf(390f, 285f, 0.4f, 0.36f, 220f, 190f),
        "win-help" to floatArrayOf(620f, 470f, 0.6f, 0.72f, 290f, 320f),
        "win-music" to floatArrayOf(440f, 500f, 0.5f, 0.66f, 280f, 320f),
        "win-monitor" to floatArrayOf(380f, 330f, 0.4f, 0.5f, 260f, 260f),
        "win-player" to floatArrayOf(462f, 562f, 0.46f, 0.62f, 220f, 240f),
        "win-eq" to floatArrayOf(425f, 320f, 0.44f, 0.5f, 280f, 260f),
        "win-lissajous" to floatArrayOf(580f, 225f, 0.58f, 0.34f, 290f, 185f),
        "win-vumeter" to floatArrayOf(390f, 255f, 0.38f, 0.38f, 250f, 195f),
        "win-orbital" to floatArrayOf(375f, 285f, 0.36f, 0.42f, 245f, 205f),
        "win-fractal" to floatArrayOf(375f, 285f, 0.36f, 0.42f, 245f, 205f),
        "win-prism" to floatArrayOf(375f, 285f, 0.36f, 0.42f, 245f, 205f),
        "win-sonar" to floatArrayOf(375f, 285f, 0.36f, 0.42f, 245f, 205f),
        "win-magnetic" to floatArrayOf(375f, 285f, 0.36f, 0.42f, 245f, 205f),
        "win-kaleido" to floatArrayOf(375f, 285f, 0.36f, 0.42f, 245f, 205f),
        "win-starforge" to floatArrayOf(375f, 285f, 0.36f, 0.42f, 245f, 205f),
        "win-ribbon" to floatArrayOf(360f, 260f, 0.35f, 0.39f, 240f, 190f),
        "win-cybergrid" to floatArrayOf(350f, 260f, 0.34f, 0.39f, 240f, 190f),
        "win-matrix" to floatArrayOf(350f, 260f, 0.34f, 0.39f, 240f, 190f),
        "win-hypercube" to floatArrayOf(325f, 255f, 0.32f, 0.38f, 230f, 185f),
        "win-waterfall" to floatArrayOf(325f, 255f, 0.32f, 0.38f, 230f, 185f),
        "win-tunnel" to floatArrayOf(325f, 255f, 0.32f, 0.38f, 230f, 185f),
        "win-plasma" to floatArrayOf(325f, 255f, 0.32f, 0.38f, 230f, 185f),
        "win-android" to floatArrayOf(400f, 400f, 0.36f, 0.5f, 280f, 330f),
    )

    /** initDeviceDefaultScale(): the site zooms windows to 65 % (≤ 540 px), 78 % (≤ 960 px) or 100 %. */
    val uiScale: Float get() {
        val vw = if (density > 0f) areaW / density else 400f
        return if (vw <= 540f) 0.65f else if (vw <= 960f) 0.78f else 1f
    }

    /** smartPlaceAndSizeWindow + applyCompactPlayerSize, in the site's zoomed CSS px. */
    private fun smartPlace(win: Win) {
        val px = density * uiScale                    // one zoomed CSS px in screen px
        val r = 8f
        val vw = areaW / px; val vh = areaH / px
        val oW = maxOf(240f, vw - 2 * r); val oH = maxOf(200f, vh - 2 * r)
        if (win.app.id == "player") {
            // X(): 462 × 590, centred
            val s = maxOf(220f, vw - 12); val l = maxOf(240f, vh - 12)
            val d = minOf(462f, s); val c = minOf(590f, l)
            win.w = d * px; win.h = c * px; win.x = (6 + (s - d) / 2) * px; win.y = (6 + (l - c) / 2) * px
            return
        }
        val n = SMART[com.reverender.r95.core.siteWindowId(win.app.id)] ?: floatArrayOf(400f, 300f, 0.4f, 0.45f, 240f, 190f)
        // the desktop branch everywhere: windows keep the site's designed proportions instead of filling a phone screen
        val phone = false
        val sW: Float; val sH: Float
        if (phone) {
            sW = minOf(oW, maxOf(n[4], Math.round(oW * (if (n[2] > 0.6f) 0.98f else 0.94f)).toFloat()))
            sH = minOf(oH, maxOf(n[5], minOf(n[1], Math.round(oH * minOf(0.9f, n[3] + 0.16f)).toFloat())))
        } else {
            val d = maxOf(n[0], Math.round(oW * n[2]).toFloat()); val f = maxOf(n[1], Math.round(oH * n[3]).toFloat())
            sW = minOf(oW, maxOf(n[4], minOf(d, Math.round(n[0] * 1.15f).toFloat())))
            sH = minOf(oH, maxOf(n[5], minOf(f, Math.round(n[1] * 1.15f).toFloat())))
        }
        // the least-overlap spot, preferring top-left
        val others = windows.filter { it !== win && !it.minimized }.map { floatArrayOf(it.x / px, it.y / px, it.w / px, it.h / px) }
        val maxL = maxOf(r, r + oW - sW); val maxT = maxOf(r, r + oH - sH)
        val bx = if (phone) 2 else 6; val by = if (phone) 4 else 5
        var best = floatArrayOf(r, r); var score = Float.POSITIVE_INFINITY
        for (dy in 0..by) for (fx in 0..bx) {
            val l = Math.round(r + (maxL - r) * (fx / bx.toFloat())).toFloat(); val t = Math.round(r + (maxT - r) * (dy / by.toFloat())).toFloat()
            var ov = 0f; var pen = 0f
            for (o in others) {
                val iw = maxOf(0f, minOf(l + sW, o[0] + o[2]) - maxOf(l, o[0])); val ih = maxOf(0f, minOf(t + sH, o[1] + o[3]) - maxOf(t, o[1]))
                ov += iw * ih
                if (kotlin.math.abs(l - o[0]) < 28 && kotlin.math.abs(t - o[1]) < 28) pen += 5e5f
            }
            val sc = ov + pen + (t * 10 + l * 4)
            if (sc < score) { score = sc; best = floatArrayOf(l, t) }
        }
        win.w = sW * px; win.h = sH * px; win.x = best[0] * px; win.y = best[1] * px
    }

    private fun place(win: Win) {
        win.fitDone = false
        smartPlace(win)
    }

    /** arrangeOpenWindowsSmart ("Tile windows"): every open window back to its smart place and size. */
    fun tileOpen() { windows.filter { !it.minimized }.forEach { it.maximized = false; place(it); it.wobble.pop() } }

    /** toggleAllVisualizers: close them all if any is open, otherwise open every visualizer window. */
    fun toggleAllVisualizers() {
        startOpen = false
        val any = windows.any { !it.minimized && it.app.id in VIZ_IDS }
        if (any) playerOnlyReset() else VIZ_IDS.forEach { open(it) }
    }
    val anyVizOpen: Boolean get() = windows.any { !it.minimized && it.app.id in VIZ_IDS }

    fun focus(win: Win) {
        if (windows.lastOrNull() === win) return
        windows.remove(win); windows.add(win)
    }

    fun close(win: Win) { windows.remove(win) }

    fun minimize(win: Win) { win.minimized = true }

    /** Start menu "Player Only Reset" (resetToPlayerOnly): minimise everything except VAPOR_AMP. */
    fun playerOnlyReset() {
        startOpen = false
        windows.forEach { it.minimized = it.app.id != "player" }
        if (windows.none { it.app.id == "player" }) open("player") else windows.first { it.app.id == "player" }.let { focus(it) }
    }

    fun toggleMax(win: Win) { win.maximized = !win.maximized; focus(win) }

    /** Taskbar button: focus / restore, or minimise if it's already in front. */
    fun taskbarTap(win: Win) {
        when {
            win.minimized -> { win.minimized = false; win.resizeHintUntil = System.currentTimeMillis() + 5200; focus(win); win.wobble.pop() }
            top === win -> win.minimized = true
            else -> focus(win)
        }
    }

    /** Back button: close the Start menu, then minimise the front window. */
    fun handleBack(): Boolean {
        if (searchOpen) { searchOpen = false; return true }
        if (startOpen) { startOpen = false; return true }
        val t = top ?: return false
        t.minimized = true
        return true
    }

    fun clampAll() {
        windows.forEach { win ->
            win.w = win.w.coerceAtMost(areaW).coerceAtLeast(200f * density)
            win.h = win.h.coerceAtMost(areaH).coerceAtLeast(150f * density)
            win.x = win.x.coerceIn(-win.w + 80f * density, (areaW - 80f * density).coerceAtLeast(0f))
            win.y = win.y.coerceIn(0f, (areaH - 30f * density).coerceAtLeast(0f))
        }
    }
}
