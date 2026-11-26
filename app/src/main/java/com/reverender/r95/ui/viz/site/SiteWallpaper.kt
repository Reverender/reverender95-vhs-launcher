package com.reverender.r95.ui.viz.site

import android.graphics.Canvas
import android.os.Build
import com.reverender.r95.ui.viz.js.Ctx
import com.reverender.r95.ui.viz.js.Rt

/**
 * Runs the website's own wallpaper renderers (translated 1:1 into SiteWallpapers.kt by tools/tx.js).
 * Everything is drawn in CSS pixels (dp), exactly like the site's background canvas.
 */
object SiteWallpaper {
    /** Wallpaper ids drawn by the site's code (all 29 of the site's wallpapers). */
    val ids: Set<Int> = (0..28).toSet()

    /** The site's layer switches (bgLayers) with their defaults. */
    val defaultLayers = listOf("gridWarp", "solarRings", "starWarp", "skySpectrum", "cyberRain", "monoliths", "depthMotes")
    val allLayers = defaultLayers + listOf("parallaxRings", "glassShards", "auroraRibbons", "cometTrails", "lightShafts", "depthRidges")

    private var inited = false
    private val ctx = Ctx()
    private var themeKey = ""
    private var theme: Any? = null
    private var targetMouseX = 0.0
    private var targetMouseY = 0.0
    private var cssW = 1.0
    private var cssH = 1.0
    private var lastLayers: Set<String>? = null


    private fun ensureInit() { SiteHost.ensureInit() }
    internal fun initTranslated() { if (!inited) { SiteWallpapersInit(); inited = true } }

    /** Pointer position over the desktop, in px (the site eases the parallax towards it). */
    fun pointer(xPx: Float, yPx: Float, wPx: Float, hPx: Float) {
        if (wPx <= 0 || hPx <= 0) return
        targetMouseX = (xPx / wPx - 0.5) * 2
        targetMouseY = (yPx / hPx - 0.5) * 2
    }

    /** Tap on the desktop background: the site's expanding ripple. */
    fun tap(xPx: Float, yPx: Float, density: Float) {
        ensureInit()
        val x = (xPx / density).toDouble(); val y = (yPx / density).toDouble()
        // the ripple list belongs to whichever thread draws the wallpaper
        if (Async.on) Async.post { rrAddRipple(x, y) } else rrAddRipple(x, y)
    }

    /**
     * Draws the wallpaper. On Android 6+ the site's renderer runs on its own lower-priority thread and
     * records into a Picture; the UI thread only replays the newest finished one. The wallpaper looks the same,
     * but dragging, scrolling and taps no longer wait for it (it simply steps less often when the phone is busy).
     */
    fun draw(
        canvas: Canvas, wPx: Float, hPx: Float, density: Float, id: Int, themeId: String,
        layers: Set<String>, lowPower: Boolean, animated: Boolean,
    ) {
        if (!Async.on || !canvas.isHardwareAccelerated) { frame(canvas, wPx, hPx, density, id, themeId, layers, lowPower); return }
        ensureInit()
        val p = Params(wPx, hPx, density, id, themeId, layers, lowPower, animated)
        if (p != params || Async.latest == null) { params = p; Async.request(p, SiteHost.timeSec, SiteHost.dt, force = true) }
        Async.latestBitmap?.let { b ->
            // reduced-resolution frame: stretched back to full size with filtering
            Async.dst.set(0f, 0f, wPx, hPx)
            canvas.drawBitmap(b, null, Async.dst, Async.paint)
            return
        }
        val pic = Async.latest ?: return
        canvas.drawPicture(pic)
    }
    /** true when the wallpaper is drawn on its own thread: redraw on [SiteHost.wallReady] instead of every tick */
    val async: Boolean get() = Async.on
    private data class Params(val w: Float, val h: Float, val density: Float, val id: Int, val themeId: String, val layers: Set<String>, val lowPower: Boolean, val animated: Boolean)
    @Volatile private var params: Params? = null
    /** Runs [r] on the wallpaper thread (all the site's wallpaper code runs on that one thread). */
    fun runOnWorker(r: Runnable) = Async.post(r)

    /** the wallpaper left the screen: stop stepping it */
    fun stop() { params = null }
    /** the desktop itself went away (another style): also drop the last frame and the buffers */
    fun release() { params = null; Async.post { Async.dropFrames() } }
    /** next animation step (from the site clock, at the wallpaper's pace) */
    internal fun kick() { val p = params ?: return; if (p.animated) Async.request(p, SiteHost.timeSec, SiteHost.dt, force = false) }

    private object Async {
        val on = Build.VERSION.SDK_INT >= 23
        private val thread by lazy {
            android.os.HandlerThread("r95-wallpaper", android.os.Process.THREAD_PRIORITY_DEFAULT).also { it.start() }
        }
        private val handler by lazy { android.os.Handler(thread.looper) }
        private val main = android.os.Handler(android.os.Looper.getMainLooper())
        @Volatile var latest: android.graphics.Picture? = null
        /** set when the wallpaper renders below full resolution (Settings → Performance → Wallpaper resolution) */
        @Volatile var latestBitmap: android.graphics.Bitmap? = null
        val dst = android.graphics.RectF()
        val paint = android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG)
        private val soft = arrayOfNulls<android.graphics.Bitmap>(2); private var softIdx = 0
        @Volatile private var busy = false
        private var lastT = -1.0
        fun post(r: Runnable) { handler.post(r) }
        fun dropFrames() { latest = null; latestBitmap = null; soft[0] = null; soft[1] = null; lastT = -1.0 }

        /** GPU-rendered (Android 9+) or software bitmap of a small frame, drawn stretched on the desktop. */
        private fun rasterize(p: android.graphics.Picture, w: Int, h: Int): android.graphics.Bitmap? {
            if (Build.VERSION.SDK_INT >= 28) runCatching {
                return android.graphics.Bitmap.createBitmap(p, w, h, android.graphics.Bitmap.Config.HARDWARE)
            }
            // older phones: two software buffers, alternating, so the one on screen is never being drawn into
            softIdx = 1 - softIdx
            val b = soft[softIdx]?.takeIf { it.width == w && it.height == h }
                ?: android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888).also { soft[softIdx] = it }
            b.eraseColor(0)
            val c = Canvas(b)
            p.draw(c)
            return b
        }
        fun postDelayed(r: Runnable, ms: Long) { handler.postDelayed(r, ms) }
        private var again = false
        fun request(q: Params, t: Double, dt: Double, force: Boolean) {
            if (busy) { if (force) again = true; return } // still drawing the previous one: skip, never queue up
            if (t == lastT && !force) return
            busy = true; lastT = t
            handler.post {
                try {
                    val s = com.reverender.r95.core.Perf.wallScale()
                    val sw = maxOf(1, (q.w * s).toInt()); val sh = maxOf(1, (q.h * s).toInt())
                    val p = android.graphics.Picture()
                    val c = p.beginRecording(sw, sh)
                    // same CSS size, fewer device pixels: the scene itself is unchanged
                    frame(c, sw.toFloat(), sh.toFloat(), q.density * s, q.id, q.themeId, q.layers, q.lowPower, timeSec = t, dtSec = dt)
                    p.endRecording()
                    if (s >= 0.99f) { latest = p; latestBitmap = null }
                    else latestBitmap = rasterize(p, sw, sh)
                } catch (e: Throwable) {
                    android.util.Log.w("R95", "wallpaper worker failed", e)
                } finally {
                    main.post {
                        busy = false
                        SiteHost.wallpaperReady()
                        // settings changed while it was drawing: draw once more with the new ones
                        if (again) { again = false; params?.let { request(it, SiteHost.timeSec, SiteHost.dt, force = true) } }
                    }
                }
            }
        }
    }

    /** Bass-glitch kick for the wallpaper itself (window wobble is handled by the caller). */
    fun glitch(amount: Double) { ensureInit(); rrWallpaperTrigger(amount) }

    private var lastY = -1.0
    fun frame(
        canvas: Canvas, wPx: Float, hPx: Float, density: Float, id: Int, themeId: String,
        layers: Set<String>, lowPower: Boolean,
        timeSec: Double = SiteHost.timeSec, dtSec: Double = SiteHost.dt,
    ) {
        ensureInit()
        if (themeId != themeKey) { themeKey = themeId; theme = rrThemeByKey(themeId) }
        if (layers != lastLayers) { lastLayers = layers; for (k in allLayers) rrSetLayer(k, k in layers) }
        val y = timeSec
        // own step: the wallpaper may be drawn less often than the site clock ticks (touch priority, busy phone)
        val d = if (lastY < 0 || y <= lastY) dtSec else (y - lastY).coerceIn(0.001, 0.1)
        lastY = y

        Rt.mouseX += (targetMouseX - Rt.mouseX) * 0.08
        Rt.mouseY += (targetMouseY - Rt.mouseY) * 0.08

        cssW = (wPx / density).toDouble(); cssH = (hPx / density).toDouble()
        ctx.begin(canvas, cssW, cssH, density)
        try {
            com.reverender.r95.core.Perf.time(2) { rrWallpaperFrame(ctx, cssW, cssH, y, d, id.toDouble(), theme, if (lowPower) 2.0 else 0.0) }
        } catch (e: Throwable) {
            android.util.Log.w("R95", "wallpaper $id frame failed", e)
        } finally {
            ctx.end()
        }
    }

    val twoPointGradients = Build.VERSION.SDK_INT >= 31
}
