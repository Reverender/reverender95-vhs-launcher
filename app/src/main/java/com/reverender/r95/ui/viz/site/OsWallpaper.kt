package com.reverender.r95.ui.viz.site

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.reverender.r95.ui.viz.js.JsEl
import com.reverender.r95.ui.viz.js.jsGet
import com.reverender.r95.ui.viz.siteos.SiteOsWallpapersInit
import com.reverender.r95.ui.viz.siteos.rrOsWallRender

/**
 * The site's Style Wallpapers (RRWallpapers in rr-os-styles.js, translated 1:1): drawn once when chosen or
 * when the size / colour theme changes, then shown as a still image, so they use no CPU while you work.
 */
object OsWallpaper {
    class Info(val id: String, val name: String, val style: String, val accent: String?)
    val win95 = listOf(
        Info("w95-sky", "Daydream Sky", "win95", null), Info("w95-dither", "Dither Sunset", "win95", null),
        Info("w95-pattern", "Desktop Weave", "win95", null), Info("w95-setup", "Setup.exe", "win95", null),
        Info("w95-stars", "Starfield.scr", "win95", null), Info("w95-pipes", "Pipes.scr", "win95", null),
        Info("w95-mystify", "Mystify.scr", "win95", null),
    )
    val longhorn = listOf(
        Info("lh-aurora", "Aurora Plex", "longhorn", "jade"), Info("lh-jade", "Jade Horizon", "longhorn", "jade"),
        Info("lh-slate", "Slate Panes", "longhorn", "slate"), Info("lh-ember", "Ember Glass", "longhorn", "amber"),
    )
    fun find(id: String?) = (win95 + longhorn).firstOrNull { it.id == id }

    private var inited = false
    private fun init() { if (!inited) { SiteHost.ensureInit(); SiteOsWallpapersInit(); inited = true } }

    /** A() of the page: THEMES[currentThemeKey] (Windows 95 wallpapers follow the colour theme). */
    private fun theme(themeId: String): Any? = jsGet(com.reverender.r95.ui.viz.siteviz.g_THEMES, themeId)
        ?: jsGet(com.reverender.r95.ui.viz.siteviz.g_THEMES, "vaporwave")

    class Rendered(val bmp: Bitmap, val pixel: Boolean)
    private val cache = HashMap<String, Rendered>()

    /** render(e, canvas, w, h, theme, scale); keyed like the page (id | size | scale | theme for Windows 95). */
    fun render(id: String, wCss: Int, hCss: Int, scale: Double, themeId: String): Rendered? {
        init()
        val info = find(id) ?: return null
        val key = "$id|${wCss}x$hCss|$scale|${if (info.style == "win95") themeId else ""}"
        cache[key]?.let { return it }
        val el = JsEl("_osw")
        val pixel = try { rrOsWallRender(id, el, wCss.toDouble(), hCss.toDouble(), theme(themeId), scale) } catch (e: Throwable) {
            android.util.Log.w("R95", "style wallpaper $id failed", e); return null
        }
        val bm = el.bitmap ?: return null
        if (cache.size > 24) cache.clear()
        return Rendered(bm, pixel > 0).also { cache[key] = it }
    }
}

private val osPaint = Paint()

/** The chosen style wallpaper filling the desktop (pixel-art ones scaled without smoothing). */
@Composable
fun OsWallpaperLayer(id: String, themeId: String, modifier: Modifier) {
    val conf = LocalConfiguration.current
    val d = LocalDensity.current.density
    val w = conf.screenWidthDp.coerceAtLeast(1); val h = conf.screenHeightDp.coerceAtLeast(1)
    val scale = minOf(d.toDouble(), 1.5).let { if (w * it > 2560) 2560.0 / w else it }
    val r = remember(id, w, h, scale, themeId) { OsWallpaper.render(id, w, h, scale, themeId) }
    Canvas(modifier) {
        val bm = r?.bmp ?: return@Canvas
        osPaint.isFilterBitmap = !r.pixel
        // background-size: cover
        val k = maxOf(size.width / bm.width, size.height / bm.height)
        val dw = bm.width * k; val dh = bm.height * k
        drawContext.canvas.nativeCanvas.drawBitmap(bm, Rect(0, 0, bm.width, bm.height), RectF((size.width - dw) / 2, (size.height - dh) / 2, (size.width + dw) / 2, (size.height + dh) / 2), osPaint)
    }
}

/** Customize thumbnail (canvas.rr-os-thumb). */
@Composable
fun OsWallpaperThumb(id: String, themeId: String, modifier: Modifier) {
    val r = remember(id, themeId) { OsWallpaper.render(id, 120, 72, 1.0, themeId) }
    Canvas(modifier) {
        val bm = r?.bmp ?: return@Canvas
        osPaint.isFilterBitmap = !r.pixel
        drawContext.canvas.nativeCanvas.drawBitmap(bm, Rect(0, 0, bm.width, bm.height), RectF(0f, 0f, size.width, size.height), osPaint)
    }
}
