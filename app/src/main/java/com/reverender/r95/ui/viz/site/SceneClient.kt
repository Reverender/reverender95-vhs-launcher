package com.reverender.r95.ui.viz.site

import android.graphics.Canvas
import android.graphics.Picture
import com.reverender.r95.audio.AudioBus
import com.reverender.r95.ui.viz.js.Ctx
import com.reverender.r95.ui.viz.js.Rt

/**
 * Draws one of the site's wallpapers for something other than the desktop: the Android live wallpaper,
 * the dock screensaver or the external CRT monitor. Each client has its own canvas state and clock,
 * but all of them run on the wallpaper thread, one after another, like the desktop's own renderer.
 */
class SceneClient {
    private val ctx = Ctx()
    private var themeKey = ""
    private var theme: Any? = null
    private var lastT = -1.0
    private val t0 = System.nanoTime()
    @Volatile var busy = false; private set
    @Volatile var latest: Picture? = null; private set

    init { SiteWallpaper.initTranslated() } // on the UI thread, before the worker touches anything

    /** Records the next frame into a Picture on the wallpaper thread; [done] runs there afterwards. */
    fun requestPicture(wPx: Int, hPx: Int, density: Float, id: Int, themeId: String, done: () -> Unit) {
        if (busy) return
        busy = true
        SiteWallpaper.runOnWorker {
            try {
                val p = Picture()
                val c = p.beginRecording(maxOf(1, wPx), maxOf(1, hPx))
                render(c, wPx, hPx, density, id, themeId)
                p.endRecording()
                latest = p
            } catch (e: Throwable) { android.util.Log.w("R95", "scene client failed", e) }
            finally { busy = false; done() }
        }
    }

    /** Draws straight onto a canvas (call it on the wallpaper thread via [SiteWallpaper.runOnWorker]). */
    fun render(c: Canvas, wPx: Int, hPx: Int, density: Float, id: Int, themeId: String) {
        val now = (System.nanoTime() - t0) / 1e9 + 40.0
        val dt = if (lastT < 0) 1 / 30.0 else (now - lastT).coerceIn(0.001, 0.1)
        lastT = now
        // with the desktop asleep, keep the site's bass / mid smoothing moving from the audio we have
        if (System.nanoTime() - SiteHost.lastTickNanos > 250_000_000L) {
            val f = AudioBus.currentFrame()
            val fd = Rt.freqDataArr; val td = Rt.timeDataArr
            for (i in 0 until 256) { fd[i] = f.web[i].toDouble(); td[i] = 128.0 + f.wave[i] * 127.0 }
            runCatching { rrAudioFrame(dt) }
        }
        if (themeId != themeKey) { themeKey = themeId; theme = rrThemeByKey(themeId) }
        val cssW = (wPx / density).toDouble(); val cssH = (hPx / density).toDouble()
        ctx.begin(c, cssW, cssH, density)
        try { rrWallpaperFrame(ctx, cssW, cssH, now, dt, id.toDouble(), theme, 0.0) }
        catch (e: Throwable) { android.util.Log.w("R95", "scene $id failed", e) }
        finally { ctx.end() }
    }
}
