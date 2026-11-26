package com.reverender.r95.ui.desktop

import android.graphics.BlendMode
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.reverender.r95.core.DisplayFx
import com.reverender.r95.ui.viz.rememberFrameTime

/** Bloom (screen-blended blur) and chromatic aberration on the wallpaper layer, like the site's overlay + SVG filter. */
@Composable
fun Modifier.wallpaperFx(): Modifier {
    if (Build.VERSION.SDK_INT < 31) return this
    val d = LocalDensity.current.density
    val bp = DisplayFx.bloomParams(); val co = DisplayFx.chromaticOffset(d)
    if (bp == null && co <= 0f) return this
    val fx = remember(bp, co, d) { wallFx(bp, co, d) }
    return this.graphicsLayer { renderEffect = fx.asComposeRenderEffect() }
}

@RequiresApi(31)
private fun wallFx(bp: Pair<Float, Float>?, co: Float, d: Float): RenderEffect {
    fun cm(vararg v: Float) = ColorMatrixColorFilter(ColorMatrix(v))
    var base: RenderEffect = RenderEffect.createOffsetEffect(0f, 0f)
    if (co > 0f) {
        val dx = co * d
        val red = RenderEffect.createColorFilterEffect(cm(1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f), RenderEffect.createOffsetEffect(-dx, -dx * 0.08f))
        val green = RenderEffect.createColorFilterEffect(cm(0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f))
        val blue = RenderEffect.createColorFilterEffect(cm(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f), RenderEffect.createOffsetEffect(dx, dx * 0.08f))
        base = RenderEffect.createBlendModeEffect(RenderEffect.createBlendModeEffect(red, green, BlendMode.PLUS), blue, BlendMode.PLUS)
    }
    if (bp != null) {
        val (blurCss, op) = bp
        val r = (blurCss * d).coerceAtLeast(0.5f)
        val b = 1.08f
        val glow = RenderEffect.createColorFilterEffect(
            cm(b, 0f, 0f, 0f, 0f, 0f, b, 0f, 0f, 0f, 0f, 0f, b, 0f, 0f, 0f, 0f, 0f, op, 0f),
            RenderEffect.createBlurEffect(r, r, base, Shader.TileMode.CLAMP),
        )
        base = RenderEffect.createBlendModeEffect(base, glow, BlendMode.SCREEN)
    }
    return base
}

/** Pointer history for the trails and sparks (fed by [funPointerListener] on the desktop root). */
private object FunPtr {
    val pts = ArrayList<FloatArray>()    // x, y, t
    val sparks = ArrayList<FloatArray>() // x, y, vx, vy, t, life, pink
}

/** Watches mouse and stylus pointers without consuming them (Initial pass on the root). */
fun Modifier.funPointerListener(density: Float): Modifier = this.pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val ev = awaitPointerEvent(PointerEventPass.Initial)
            if (!DisplayFx.trail && !DisplayFx.sparks) continue
            for (c in ev.changes) {
                if (c.type != PointerType.Mouse && c.type != PointerType.Stylus) continue
                val now = System.currentTimeMillis().toFloat()
                if (DisplayFx.trail) {
                    val l = FunPtr.pts.lastOrNull()
                    if (l == null || kotlin.math.abs(c.position.x - l[0]) + kotlin.math.abs(c.position.y - l[1]) > 7 * density) {
                        FunPtr.pts.add(floatArrayOf(c.position.x, c.position.y, now)); if (FunPtr.pts.size > 10) FunPtr.pts.removeAt(0)
                    }
                }
                if (DisplayFx.sparks && c.pressed && !c.previousPressed) {
                    for (n in 0 until 6) {
                        val a = (Math.PI * 2 * n / 6 + Math.random() * 0.18).toFloat(); val s = (0.8 + Math.random() * 1.65).toFloat()
                        FunPtr.sparks.add(floatArrayOf(c.position.x, c.position.y, kotlin.math.cos(a) * s * density, kotlin.math.sin(a) * s * density, now, (230 + Math.random() * 90).toFloat(), (n % 2).toFloat()))
                    }
                    while (FunPtr.sparks.size > 24) FunPtr.sparks.removeAt(0)
                }
            }
        }
    }
}

/** Neon pointer trails and click sparks: mouse and stylus only, as on the site. Draw-only. */
@Composable
fun FunPointerFx(modifier: Modifier) {
    if (!DisplayFx.trail && !DisplayFx.sparks) return
    val time = rememberFrameTime()
    val d = LocalDensity.current.density
    Canvas(modifier) {
        time.value
        val pts = FunPtr.pts; val sparks = FunPtr.sparks
        val t = System.currentTimeMillis().toFloat()
        pts.removeAll { t - it[2] > 240 }
        if (DisplayFx.trail && pts.size > 1) {
            val u = ((t - pts.last()[2]) / 240f).coerceIn(0f, 1f)
            val p = Path().apply { moveTo(pts[0][0], pts[0][1]); for (i in 1 until pts.size) lineTo(pts[i][0], pts[i][1]) }
            drawPath(p, Color(0xFF00F7FF), alpha = (1 - u) * 0.22f, style = Stroke(5 * d, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(p, Color(0xFFFF00A0), alpha = (1 - u) * 0.86f, style = Stroke(1.35f * d, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        val it = sparks.iterator()
        while (it.hasNext()) {
            val c = it.next()
            val h = (t - c[4]) / c[5]
            if (h >= 1) { it.remove(); continue }
            c[0] += c[2]; c[1] += c[3]; c[2] *= 0.965f; c[3] = c[3] * 0.965f + 0.018f * d
            val s = 1 - h; val m = (1.5f + s * 1.1f) * d
            drawRect(if (c[6] > 0) Color(0xFFFF00A0) else Color(0xFF00F7FF), Offset(c[0] - m / 2, c[1] - m / 2), Size(m, m), alpha = s * 0.9f)
        }
    }
}

object FunFxScope {
    @Composable fun Modifier.fx(): Modifier = wallpaperFx()
}
