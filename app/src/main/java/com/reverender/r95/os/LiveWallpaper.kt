package com.reverender.r95.os

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.reverender.r95.audio.AudioBus
import com.reverender.r95.audio.SystemVisualizer
import com.reverender.r95.ui.viz.site.SceneClient
import com.reverender.r95.ui.viz.site.SiteWallpaper

/**
 * Any of the app's 29 animated wallpapers as the phone's Android live wallpaper. It reacts to whatever
 * the phone plays (VAPOR_AMP directly, everything else through Android's output visualizer) and to taps.
 * Drawing happens on the wallpaper thread at up to 30 fps, and stops whenever the wallpaper is hidden.
 */
class R95LiveWallpaper : WallpaperService() {
    override fun onCreateEngine(): Engine = SceneEngine()

    inner class SceneEngine : Engine() {
        private var client: SceneClient? = null
        private var visible = false
        private var w = 1; private var h = 1
        private var listening = false
        private val main = android.os.Handler(android.os.Looper.getMainLooper())
        private val frame = object : Runnable {
            override fun run() {
                if (!visible) return
                val c = client ?: return
                val holder = surfaceHolder
                val id = OsPrefs.liveWallpaper.coerceIn(0, 28); val theme = OsPrefs.liveTheme
                val density = resources.displayMetrics.density
                SiteWallpaper.runOnWorker {
                    val t0 = System.nanoTime()
                    var canvas: android.graphics.Canvas? = null
                    try {
                        canvas = if (Build.VERSION.SDK_INT >= 26) holder.lockHardwareCanvas() else holder.lockCanvas()
                        if (canvas != null) c.render(canvas, w, h, density, id, theme)
                    } catch (_: Throwable) {
                    } finally {
                        canvas?.let { runCatching { holder.unlockCanvasAndPost(it) } }
                    }
                    val spent = (System.nanoTime() - t0) / 1_000_000L
                    main.postDelayed(this, (33L - spent).coerceAtLeast(4L))
                }
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            OsPrefs.init(this@R95LiveWallpaper)
            setTouchEventsEnabled(true)
            client = SceneClient()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            w = width; h = height
        }

        override fun onVisibilityChanged(v: Boolean) {
            visible = v
            if (v) {
                if (!listening) { listening = true; AudioBus.backgroundListeners.incrementAndGet(); SystemVisualizer.acquire() }
                main.removeCallbacks(frame); main.post(frame)
            } else {
                main.removeCallbacks(frame)
                if (listening) { listening = false; AudioBus.backgroundListeners.decrementAndGet(); SystemVisualizer.release() }
            }
        }

        override fun onTouchEvent(event: MotionEvent) {
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                val d = resources.displayMetrics.density
                val x = (event.x / d).toDouble(); val y = (event.y / d).toDouble()
                SiteWallpaper.runOnWorker { runCatching { com.reverender.r95.ui.viz.site.rrAddRipple(x, y) } }
            }
            SiteWallpaper.pointer(event.x, event.y, w.toFloat(), h.toFloat())
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) { visible = false; main.removeCallbacks(frame); super.onSurfaceDestroyed(holder) }

        override fun onDestroy() {
            onVisibilityChanged(false)
            super.onDestroy()
        }
    }

    companion object {
        /** Opens Android's live-wallpaper preview with this wallpaper selected. */
        fun open(ctx: Context) {
            val i = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                .putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(ctx, R95LiveWallpaper::class.java))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            runCatching { ctx.startActivity(i) }.onFailure {
                runCatching { ctx.startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
            }
        }
    }
}
