package com.reverender.r95.ui.viz.site

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.reverender.r95.ui.viz.js.Dom

/** The visualizer FX of the site (data-rr-hue / saturation / contrast / glow on the canvas). */
class VizFx {
    var hue by mutableFloatStateOf(0f)
    var saturation by mutableFloatStateOf(100f)
    var contrast by mutableFloatStateOf(100f)
    var glow by mutableFloatStateOf(0f)

    fun preset(name: String) {
        val v = when (name) {
            "vapor" -> floatArrayOf(316f, 155f, 118f, 65f)
            "ice" -> floatArrayOf(176f, 145f, 112f, 60f)
            "mono" -> floatArrayOf(0f, 0f, 140f, 25f)
            else -> floatArrayOf(0f, 100f, 100f, 0f)
        }
        hue = v[0]; saturation = v[1]; contrast = v[2]; glow = v[3]
    }

    /** CSS hue-rotate · saturate · contrast as one colour matrix. */
    fun matrix(): ColorMatrix? {
        if (hue % 360f == 0f && saturation == 100f && contrast == 100f) return null
        val m = ColorMatrix()
        if (hue % 360f != 0f) {
            val a = Math.toRadians(hue.toDouble()); val c = Math.cos(a).toFloat(); val s = Math.sin(a).toFloat()
            // CSS filter hue-rotate matrix (Filter Effects spec)
            m.postConcat(ColorMatrix(floatArrayOf(
                0.213f + c * 0.787f - s * 0.213f, 0.715f - c * 0.715f - s * 0.715f, 0.072f - c * 0.072f + s * 0.928f, 0f, 0f,
                0.213f - c * 0.213f + s * 0.143f, 0.715f + c * 0.285f + s * 0.140f, 0.072f - c * 0.072f - s * 0.283f, 0f, 0f,
                0.213f - c * 0.213f - s * 0.787f, 0.715f - c * 0.715f + s * 0.715f, 0.072f + c * 0.928f + s * 0.072f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f,
            )))
        }
        if (saturation != 100f) {
            val s = saturation / 100f
            m.postConcat(ColorMatrix(floatArrayOf(
                0.213f + 0.787f * s, 0.715f - 0.715f * s, 0.072f - 0.072f * s, 0f, 0f,
                0.213f - 0.213f * s, 0.715f + 0.285f * s, 0.072f - 0.072f * s, 0f, 0f,
                0.213f - 0.213f * s, 0.715f - 0.715f * s, 0.072f + 0.928f * s, 0f, 0f,
                0f, 0f, 0f, 1f, 0f,
            )))
        }
        if (contrast != 100f) {
            val k = contrast / 100f; val o = (0.5f - 0.5f * k) * 255f
            m.postConcat(ColorMatrix(floatArrayOf(k, 0f, 0f, 0f, o, 0f, k, 0f, 0f, o, 0f, 0f, k, 0f, o, 0f, 0f, 0f, 1f, 0f)))
        }
        return m
    }
}

/**
 * One of the site's visualizer canvases. Its bitmap is drawn by the translated renderer at CSS-pixel
 * size (like the site's canvas) and scaled to fit; the site's per-window FX are applied when drawing.
 */
@Composable
fun SiteCanvas(id: String, modifier: Modifier = Modifier, fx: VizFx? = null) {
    val density = LocalDensity.current.density
    val el = remember(id) { Dom.el(id) }
    // a window hidden behind a maximized one counts as closed for the site's renderer (rrWindowHidden): no frames drawn for it
    val hidden = com.reverender.r95.ui.desktop.LocalWinHidden.current
    DisposableEffect(id, hidden) {
        if (!hidden) SiteHost.activeCanvases.add(id)
        onDispose { if (!hidden) SiteHost.activeCanvases.remove(id) }
    }
    val paint = remember { Paint(Paint.FILTER_BITMAP_FLAG) }
    val glowPaint = remember { Paint(Paint.FILTER_BITMAP_FLAG) }
    val glowPaint2 = remember { Paint(Paint.FILTER_BITMAP_FLAG).apply { colorFilter = android.graphics.PorterDuffColorFilter(0x9900F7FF.toInt(), android.graphics.PorterDuff.Mode.SRC_IN) } }
    val dst = remember { android.graphics.RectF() }
    val sizeMod = modifier.onSizeChanged {
        el["width"] = maxOf(1.0, Math.round(it.width / density).toDouble())
        el["height"] = maxOf(1.0, Math.round(it.height / density).toDouble())
    }
    Box(sizeMod) {
        val glow = fx?.glow ?: 0f
        // site: drop-shadow(cyan 60%) + drop-shadow(pink 31.5%) growing with the glow amount (blur needs Android 12+)
        if (glow > 0f && Build.VERSION.SDK_INT >= 31) {
            val m = (glow / 100f).coerceIn(0f, 1f); val e = m * m * (3 - 2 * m)
            Canvas(Modifier.fillMaxSize().blur((2.25f * (2 + 7.2f * e)).dp)) {
                SiteHost.frame
                val b = el.bitmap ?: return@Canvas
                if (glowPaint.colorFilter == null) glowPaint.colorFilter = android.graphics.PorterDuffColorFilter(0x50FF00A0, android.graphics.PorterDuff.Mode.SRC_IN)
                dst.set(0f, 0f, size.width, size.height)
                drawIntoCanvas { c -> c.nativeCanvas.drawBitmap(b, null, dst, glowPaint) }
            }
            Canvas(Modifier.fillMaxSize().blur((2.25f * (1 + 3.4f * e)).dp)) {
                SiteHost.frame
                val b = el.bitmap ?: return@Canvas
                dst.set(0f, 0f, size.width, size.height)
                drawIntoCanvas { c -> c.nativeCanvas.drawBitmap(b, null, dst, glowPaint2) }
            }
        }
        Canvas(Modifier.fillMaxSize()) {
            SiteHost.frame
            val b = el.bitmap ?: return@Canvas
            paint.colorFilter = fx?.matrix()?.let { ColorMatrixColorFilter(it) }
            dst.set(0f, 0f, size.width, size.height)
            drawIntoCanvas { c -> c.nativeCanvas.drawBitmap(b, null, dst, paint) }
        }
    }
}
