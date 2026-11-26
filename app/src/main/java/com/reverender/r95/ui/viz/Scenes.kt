package com.reverender.r95.ui.viz

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import com.reverender.r95.audio.AudioFrame
import com.reverender.r95.core.VaporTheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * CUSTOMIZE_95's wallpaper worlds and Depth FX layers, drawn natively with Canvas.
 * Each scene is a function of time, theme colours and the live audio frame.
 */
class Scene(val id: Int, val name: String, val group: String, val backrooms: Boolean = false, val draw: DrawScope.(SceneCtx) -> Unit)

class SceneCtx(val t: Float, val theme: VaporTheme, val f: AudioFrame, val reactive: Boolean) {
    val c1 = argb(theme.primary); val c2 = argb(theme.secondary); val c3 = argb(theme.accent)
    val sky = theme.sky.map(::argb); val sun = theme.sun.map(::argb); val floor = theme.floor.map(::argb)
    val grid = argb(theme.grid)
    val bass get() = if (reactive) f.bass else 0f
}

private fun hash(i: Int): Float { var x = i * 374761393 + 668265263; x = (x xor (x ushr 13)) * 1274126177; return ((x xor (x ushr 16)) and 0xFFFF) / 65535f }

// ---------------------------------------------------------------- shared pieces

private fun DrawScope.skyGrad(c: SceneCtx, until: Float) = drawRect(Brush.verticalGradient(c.sky, 0f, until), size = Size(size.width, until))

private fun DrawScope.stars(c: SceneCtx, n: Int, maxY: Float, color: Color = Color(0xFFFFF9C4)) {
    for (i in 0 until n) {
        val a = 0.3f + 0.4f * sin(c.t * 0.7f + i * 1.7f)
        drawRect(color.withA(a), Offset(hash(i) * size.width, hash(i + 999) * maxY), Size(2.2f, 2.2f))
    }
}

private fun DrawScope.slicedSun(c: SceneCtx, center: Offset, r0: Float, clipBottom: Float) {
    val r = r0 * (1f + c.bass * 0.08f)
    drawCircle(c.sun[0].withA(0.18f + c.bass * 0.2f), r * 1.35f, center)
    clipRect(0f, 0f, size.width, clipBottom) {
        drawCircle(Brush.verticalGradient(c.sun, center.y - r, center.y + r), r, center)
        for (i in 0 until 7) {
            val k = (i + (c.t * 0.08f) % 1f) / 7
            drawRect(argb(c.theme.sunSlice), Offset(center.x - r, center.y + r * (0.05f + k * 0.95f)), Size(r * 2, 2f + k * r * 0.09f))
        }
    }
}

private fun DrawScope.perspectiveGrid(c: SceneCtx, horizon: Float, speed: Float = 0.45f, colorOverride: Color? = null, fill: Boolean = true) {
    val w = size.width; val h = size.height
    if (fill) drawRect(Brush.verticalGradient(c.floor, horizon, h), Offset(0f, horizon), Size(w, h - horizon))
    val g = colorOverride ?: c.grid
    val scroll = (c.t * speed) % 1f
    for (i in 0 until 14) {
        val k = ((i + scroll) / 14).pow(2.2f)
        val y = horizon + k * (h - horizon)
        drawLine(g.withA(g.alpha * (0.25f + k)), Offset(0f, y), Offset(w, y), strokeWidth = 1f + k * 2f)
    }
    for (i in -22..22) drawLine(g, Offset(w / 2f + i * w * 0.018f, horizon), Offset(w / 2f + i * w * 0.14f, h), strokeWidth = 1.3f)
}

private fun DrawScope.ridge(c: SceneCtx, horizon: Float, height: Float, seed: Float, top: Color, bottom: Color, stroke: Color?, valley: Boolean = true) {
    val w = size.width
    val p = Path().apply {
        moveTo(0f, horizon)
        val n = 16
        for (i in 0..n) {
            val wobble = (sin(i * 1.9f + seed) * 0.5f + sin(i * 0.7f + seed * 2.3f) * 0.5f + 1f) / 2f
            val edge = if (valley) 1f - 0.6f * (1f - abs(i - n / 2f) / (n / 2f)) else 1f
            lineTo(w * i / n, horizon - height * wobble * edge)
        }
        lineTo(w, horizon); close()
    }
    drawPath(p, Brush.verticalGradient(listOf(top, bottom), horizon - height, horizon))
    if (stroke != null) drawPath(p, stroke, style = Stroke(2f))
}

private fun DrawScope.columns(c: SceneCtx, n: Int, base: Float, top: Float, color: Color, cap: Color) {
    val w = size.width
    for (i in 0 until n) {
        val x = w * (i + 0.5f) / n
        val cw = w / n * 0.28f
        drawRect(Brush.horizontalGradient(listOf(color, lerp(color, Color.White, 0.5f), color), x - cw / 2, x + cw / 2), Offset(x - cw / 2, top), Size(cw, base - top))
        drawRect(cap, Offset(x - cw * 0.7f, top - 10f), Size(cw * 1.4f, 12f))
        drawRect(cap, Offset(x - cw * 0.7f, base - 6f), Size(cw * 1.4f, 8f))
    }
}

private fun DrawScope.checkerFloor(c: SceneCtx, horizon: Float, a: Color, b: Color, scroll: Float) {
    val w = size.width; val h = size.height
    val rows = 12; val cols = 10
    val m = scroll % 1f; val k0 = floor(scroll).toInt()
    for (v in 0 until rows) {
        val y0 = horizon + ((v + m) / rows).pow(1.8f) * (h - horizon)
        val y1 = horizon + ((v + 1 + m) / rows).pow(1.8f) * (h - horizon)
        val s0 = w * (0.02f + ((v + m) / rows).pow(1.8f) * 0.12f) * 5; val s1 = w * (0.02f + ((v + 1 + m) / rows).pow(1.8f) * 0.12f) * 5
        for (g in -cols / 2 until cols / 2) {
            val p = Path().apply {
                moveTo(w / 2 + g * s0 / 5 * 1.0f, y0); lineTo(w / 2 + (g + 1) * s0 / 5, y0)
                lineTo(w / 2 + (g + 1) * s1 / 5, y1); lineTo(w / 2 + g * s1 / 5, y1); close()
            }
            drawPath(p, if ((v - k0 + g) and 1 == 0) a else b)
        }
    }
}

private fun DrawScope.water(c: SceneCtx, top: Float, deep: Color, light: Color) {
    val w = size.width; val h = size.height
    drawRect(Brush.verticalGradient(listOf(light, deep), top, h), Offset(0f, top), Size(w, h - top))
    for (i in 0 until 26) {
        val y = top + (h - top) * hash(i)
        val x = (hash(i + 50) * w + c.t * 20f * (0.5f + hash(i + 7))) % w
        val len = 30f + hash(i + 3) * 80f
        drawLine(Color.White.withA(0.25f + 0.2f * sin(c.t * 2 + i)), Offset(x, y), Offset(x + len, y), strokeWidth = 2f)
    }
}

private fun DrawScope.palm(x: Float, base: Float, h: Float, color: Color, sway: Float) {
    val top = Offset(x + sway * 20f, base - h)
    drawLine(color, Offset(x, base), top, strokeWidth = h * 0.05f)
    for (i in 0 until 7) {
        val a = -PI.toFloat() * (0.1f + i * 0.13f) + sway * 0.2f
        val p = Path().apply {
            moveTo(top.x, top.y)
            quadraticTo(top.x + cos(a) * h * 0.3f, top.y + sin(a) * h * 0.3f - 20f, top.x + cos(a) * h * 0.45f, top.y + sin(a) * h * 0.25f + 25f)
        }
        drawPath(p, color, style = Stroke(h * 0.035f))
    }
}

private fun DrawScope.skyline(c: SceneCtx, base: Float, seed: Int, maxH: Float, body: Color, lit: Color, alpha: Float) {
    var x = 0f; var i = seed
    while (x < size.width) {
        val bw = 30f + hash(i) * 70f; val bh = maxH * (0.3f + hash(i + 77) * 0.7f)
        drawRect(body.withA(alpha), Offset(x, base - bh), Size(bw - 4f, bh))
        var wy = base - bh + 8f
        while (wy < base - 8f) {
            var wx = x + 5f
            while (wx < x + bw - 10f) {
                if (hash((wx * 7 + wy * 13).toInt() + i) > 0.55f) drawRect(lit.withA(alpha * (0.5f + 0.5f * sin(c.t + wx))), Offset(wx, wy), Size(4f, 5f))
                wx += 9f
            }
            wy += 11f
        }
        x += bw; i++
    }
}

private fun DrawScope.arches(c: SceneCtx, color: Color, glow: Color, n: Int) {
    val w = size.width; val h = size.height
    for (i in n downTo 1) {
        val k = ((i - (c.t * 0.25f) % 1f) / n)
        val s = 1f / (0.15f + k * 2.2f)
        val aw = w * 0.35f * s; val ah = h * 0.55f * s
        val cx = w / 2; val by = h * 0.62f + h * 0.32f * s
        val p = Path().apply {
            moveTo(cx - aw, by); lineTo(cx - aw, by - ah)
            quadraticTo(cx, by - ah * 1.55f, cx + aw, by - ah); lineTo(cx + aw, by)
        }
        drawPath(p, lerp(color, glow, 1 - k).withA((1 - k) * 0.9f + 0.1f), style = Stroke(2f + 6f * s))
    }
}

// ---------------------------------------------------------------- the worlds

object Scenes {
    val all: List<Scene> = listOf(
        // The site's default Longhorn wallpaper (shown static on phones: "SLATE PANES (STATIC)").
        Scene(29, "Slate Panes", "Longhorn") { c ->
            drawRect(Brush.verticalGradient(listOf(Color(0xFF181D21), Color(0xFF242B30), Color(0xFF2D343A), Color(0xFF262C31))))
            val w = size.width; val h = size.height
            val drift = sin(c.t * 0.05f) * 6f
            // tilted translucent glass panes: (cx, cy, width, height, angle)
            val panes = listOf(
                floatArrayOf(.38f, .12f, .16f, .62f, -9f), floatArrayOf(.80f, .42f, .26f, .50f, -6f), floatArrayOf(.12f, .58f, .22f, .42f, -8f),
                floatArrayOf(.33f, .62f, .18f, .44f, -10f), floatArrayOf(.55f, .48f, .24f, .40f, -7f), floatArrayOf(.47f, .70f, .16f, .30f, -9f),
                floatArrayOf(.68f, .72f, .30f, .26f, -8f), floatArrayOf(.18f, .78f, .14f, .30f, -6f), floatArrayOf(.62f, .18f, .12f, .36f, -10f),
            )
            panes.forEachIndexed { i, p ->
                val pw = p[2] * w; val ph = p[3] * h
                val cx = p[0] * w + drift * (if (i % 2 == 0) 1 else -1); val cy = p[1] * h
                rotate(p[4], Offset(cx, cy)) {
                    val o = Offset(cx - pw / 2, cy - ph / 2)
                    drawRoundRect(Brush.verticalGradient(listOf(Color.White.withA(0.10f), Color.White.withA(0.04f)), o.y, o.y + ph), o, Size(pw, ph), androidx.compose.ui.geometry.CornerRadius(14f))
                    drawRoundRect(Color.White.withA(0.20f), o, Size(pw, ph), androidx.compose.ui.geometry.CornerRadius(14f), style = Stroke(1.5f))
                }
            }
        },
        Scene(0, "Sunset Outrun", "3D Worlds") { c ->
            val hz = size.height * 0.62f
            skyGrad(c, hz); stars(c, 60, hz * 0.5f)
            slicedSun(c, Offset(size.width / 2, hz - minOf(size.width, size.height) * 0.08f), minOf(size.width, size.height) * 0.24f, hz)
            ridge(c, hz, size.height * 0.2f, 1.3f, argb(c.theme.mtnTop), argb(c.theme.mtnBot), argb(c.theme.mtnStroke).withA(0.7f))
            ridge(c, hz, size.height * 0.13f, 4.1f, argb(c.theme.mtnTop), argb(c.theme.mtnBot), argb(c.theme.mtnStroke))
            perspectiveGrid(c, hz)
        },
        Scene(1, "Cyber City 99", "3D Worlds") { c ->
            val hz = size.height * 0.66f
            skyGrad(c, hz); stars(c, 40, hz * 0.4f)
            slicedSun(c, Offset(size.width * 0.7f, hz * 0.55f), size.minDimension * 0.14f, hz)
            skyline(c, hz, 3, size.height * 0.35f, Color(0xFF0B0620), c.c1, 0.85f)
            skyline(c, hz, 40, size.height * 0.22f, Color(0xFF130A30), c.c2, 1f)
            perspectiveGrid(c, hz, 0.6f)
        },
        Scene(2, "Helios Mall", "3D Worlds") { c ->
            val hz = size.height * 0.58f
            drawRect(Brush.verticalGradient(listOf(Color(0xFFFFD6E8), Color(0xFFB8E8FF)), 0f, hz), size = Size(size.width, hz))
            columns(c, 5, hz, size.height * 0.12f, Color(0xFFF2EAF7), Color(0xFFE0D2EE))
            drawCircle(Color(0xFFFFFFFF).withA(0.6f), size.minDimension * 0.08f, Offset(size.width / 2, hz * 0.45f))
            checkerFloor(c, hz, Color(0xFFFF8BC7), Color(0xFF7FE3FF), c.t * 0.3f)
        },
        Scene(3, "Twin Moons", "3D Worlds") { c ->
            val hz = size.height * 0.6f
            skyGrad(c, hz); stars(c, 90, hz)
            drawCircle(c.sun[0], size.minDimension * 0.13f, Offset(size.width * 0.35f, hz * 0.4f))
            drawCircle(c.c1.withA(0.9f), size.minDimension * 0.07f, Offset(size.width * 0.68f, hz * 0.28f + sin(c.t * 0.2f) * 6))
            water(c, hz, Color(0xFF020820), lerp(c.c2, Color.Black, 0.5f))
            for (i in 0 until 12) drawLine(c.sun[0].withA(0.5f - i * 0.04f), Offset(size.width * 0.35f - 60f + sin(c.t + i) * 20f, hz + i * 14f), Offset(size.width * 0.35f + 60f + sin(c.t + i) * 20f, hz + i * 14f), 3f)
        },
        Scene(4, "Fuji Canyon", "3D Worlds") { c ->
            val hz = size.height * 0.64f
            skyGrad(c, hz)
            slicedSun(c, Offset(size.width / 2, hz * 0.42f), size.minDimension * 0.16f, hz)
            val p = Path().apply {
                moveTo(size.width * 0.15f, hz); lineTo(size.width * 0.44f, hz * 0.45f); lineTo(size.width * 0.56f, hz * 0.45f); lineTo(size.width * 0.85f, hz); close()
            }
            drawPath(p, Brush.verticalGradient(listOf(argb(c.theme.mtnTop), argb(c.theme.mtnBot)), hz * 0.45f, hz))
            val snow = Path().apply { moveTo(size.width * 0.44f, hz * 0.45f); lineTo(size.width * 0.56f, hz * 0.45f); lineTo(size.width * 0.6f, hz * 0.55f); lineTo(size.width * 0.53f, hz * 0.52f); lineTo(size.width * 0.47f, hz * 0.56f); lineTo(size.width * 0.4f, hz * 0.55f); close() }
            drawPath(snow, Color.White.withA(0.9f))
            ridge(c, hz, size.height * 0.3f, 2f, Color(0xFF3A0F40), Color(0xFF12041C), c.c2.withA(0.4f))
            perspectiveGrid(c, hz)
        },
        Scene(5, "Hyper-Atrium", "3D Worlds") { c ->
            drawRect(Brush.verticalGradient(listOf(Color(0xFF12002A), lerp(c.c2, Color.Black, 0.5f), Color(0xFF12002A))))
            arches(c, c.c2, c.c1, 9)
            perspectiveGrid(c, size.height * 0.62f, 0.25f, c.c1.withA(0.5f), fill = false)
        },
        Scene(6, "Arcade 95", "3D Worlds") { c ->
            drawRect(Color(0xFF07020F))
            val n = 6; val w = size.width / n
            for (i in 0 until n) {
                val x = i * w; val top = size.height * 0.3f
                drawRect(Color(0xFF1D0B33), Offset(x + w * 0.1f, top), Size(w * 0.8f, size.height * 0.65f))
                val screen = lerp(c.c1, c.c2, hash(i))
                drawRect(screen.withA(0.6f + 0.3f * sin(c.t * 3 + i)), Offset(x + w * 0.2f, top + 30f), Size(w * 0.6f, w * 0.45f))
                for (k in 0 until 4) drawRect(Color.Black.withA(0.25f), Offset(x + w * 0.2f, top + 30f + k * w * 0.11f), Size(w * 0.6f, 2f))
                drawRect(c.c3, Offset(x + w * 0.15f, top - 18f), Size(w * 0.7f, 16f))
                drawCircle(Color.Red, 6f, Offset(x + w * 0.35f, top + 40f + w * 0.55f)); drawCircle(Color.Yellow, 6f, Offset(x + w * 0.55f, top + 40f + w * 0.55f))
            }
            checkerFloor(c, size.height * 0.92f, Color(0xFF250A40), Color(0xFF0A0315), c.t * 0.2f)
        },
        Scene(7, "Poseidon Spa", "3D Worlds") { c ->
            val hz = size.height * 0.55f
            drawRect(Brush.verticalGradient(listOf(Color(0xFFB9F1FF), Color(0xFFFFD0F0)), 0f, hz), size = Size(size.width, hz))
            columns(c, 4, hz, size.height * 0.1f, Color(0xFFFFFFFF), Color(0xFFDDE8F5))
            water(c, hz, Color(0xFF0E7FA8), Color(0xFF6FE6F2))
            for (i in 0 until 30) {
                val x = hash(i) * size.width; val y = hz + hash(i + 3) * (size.height - hz)
                drawCircle(Color.White.withA(0.15f + 0.15f * sin(c.t * 2 + i)), 10f + 20f * hash(i + 9), Offset(x + sin(c.t + i) * 10, y), style = Stroke(2f))
            }
        },
        Scene(8, "Chrono Drift", "3D Worlds") { c ->
            drawRect(Brush.radialGradient(listOf(lerp(c.c2, Color.Black, 0.5f), Color.Black), center, size.maxDimension * 0.7f))
            stars(c, 120, size.height)
            for (r in 1..6) {
                val rad = size.minDimension * 0.07f * r
                drawCircle(c.c1.withA(0.5f - r * 0.06f), rad, center, style = Stroke(2f))
                val a = c.t * (0.4f / r) * (if (r % 2 == 0) -1 else 1) + r
                drawCircle(c.c3, 5f, Offset(center.x + cos(a) * rad, center.y + sin(a) * rad))
                for (k in 0 until 12) {
                    val aa = k * PI.toFloat() / 6 + a * 0.2f
                    drawLine(c.c2.withA(0.4f), Offset(center.x + cos(aa) * (rad - 6), center.y + sin(aa) * (rad - 6)), Offset(center.x + cos(aa) * (rad + 6), center.y + sin(aa) * (rad + 6)), 2f)
                }
            }
        },
        Scene(24, "Aether Spires", "3D Worlds") { c ->
            skyGrad(c, size.height); stars(c, 50, size.height * 0.5f)
            for (i in 0 until 9) {
                val x = size.width * hash(i); val hgt = size.height * (0.25f + hash(i + 5) * 0.45f)
                val y = size.height * 0.85f - hgt + sin(c.t * 0.5f + i) * 12f
                val p = Path().apply { moveTo(x, y); lineTo(x + 26f, y + hgt); lineTo(x - 26f, y + hgt); close() }
                drawPath(p, Brush.verticalGradient(listOf(c.c1, lerp(c.c2, Color.Black, 0.6f)), y, y + hgt))
                drawCircle(c.c3.withA(0.5f), 8f, Offset(x, y - 10f))
            }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.White.withA(0.25f)), size.height * 0.7f, size.height), Offset(0f, size.height * 0.7f), Size(size.width, size.height * 0.3f))
        },
        Scene(25, "Prism Citadel", "3D Worlds") { c ->
            drawRect(Color(0xFF05020D)); stars(c, 70, size.height)
            val cx = size.width / 2; val cy = size.height * 0.45f; val r = size.minDimension * 0.22f
            val rot = c.t * 0.3f
            val tri = Path().apply { for (k in 0..3) { val a = rot + k * 2.0944f - PI.toFloat() / 2; if (k == 0) moveTo(cx + cos(a) * r, cy + sin(a) * r) else lineTo(cx + cos(a) * r, cy + sin(a) * r) } }
            drawLine(Color.White, Offset(0f, cy + 30f), Offset(cx, cy), 4f)
            val rainbow = listOf(Color(0xFFFF004C), Color(0xFFFF8A00), Color(0xFFFFE600), Color(0xFF00FF7B), Color(0xFF00B7FF), Color(0xFF8A2BFF))
            rainbow.forEachIndexed { i, col -> drawLine(col.withA(0.8f), Offset(cx, cy), Offset(size.width, cy - 120f + i * 40f + c.bass * 30f), 6f) }
            drawPath(tri, Color.White.withA(0.15f)); drawPath(tri, Color.White, style = Stroke(3f))
        },
        Scene(26, "Celestial Atrium", "3D Worlds") { c ->
            drawRect(Color(0xFF0A0420))
            val cx = size.width / 2; val base = size.height * 0.75f; val r = size.width * 0.48f
            clipRect(0f, 0f, size.width, base) {
                drawCircle(Brush.radialGradient(listOf(lerp(c.c2, Color.Black, 0.3f), Color(0xFF080216)), Offset(cx, base), r), r, Offset(cx, base))
                stars(c, 140, base)
                for (k in 0 until 10) { val a = PI.toFloat() + k * PI.toFloat() / 9; drawLine(c.c1.withA(0.25f), Offset(cx, base), Offset(cx + cos(a) * r, base + sin(a) * r), 2f) }
            }
            drawCircle(c.c1.withA(0.4f), r, Offset(cx, base), style = Stroke(3f))
            checkerFloor(c, base, Color(0xFF1B0B38), Color(0xFF0B0418), c.t * 0.15f)
        },
        Scene(27, "Tidal Vault", "3D Worlds") { c ->
            drawRect(Brush.verticalGradient(listOf(Color(0xFF00243B), Color(0xFF001018))))
            arches(c, Color(0xFF1B8CA8), c.c1, 7)
            for (i in 0 until 40) {
                val x = hash(i) * size.width; val y = (size.height - (c.t * 30f * (0.5f + hash(i + 1)) + hash(i + 2) * size.height) % size.height)
                drawCircle(Color.White.withA(0.35f), 2f + hash(i + 4) * 4f, Offset(x + sin(c.t + i) * 8f, y), style = Stroke(1.5f))
            }
            for (k in 0 until 6) {
                val p = Path(); val y0 = size.height * 0.1f * k
                p.moveTo(0f, y0)
                var x = 0f
                while (x <= size.width) { p.lineTo(x, y0 + sin(x / 60f + c.t + k) * 10f); x += 20f }
                drawPath(p, Color(0xFF6FE6F2).withA(0.08f), style = Stroke(8f))
            }
        },
        Scene(28, "Chronosphere Hall", "3D Worlds") { c ->
            drawRect(Brush.verticalGradient(listOf(Color(0xFF140026), Color(0xFF2D0A3E))))
            columns(c, 6, size.height * 0.8f, size.height * 0.15f, Color(0xFF3C1A5C), Color(0xFF5E2E86))
            val r = size.minDimension * 0.2f * (1 + c.bass * 0.1f)
            drawCircle(Brush.radialGradient(listOf(Color.White, c.c1, c.c2), Offset(center.x - r * 0.3f, center.y - r * 0.3f), r * 1.4f), r, center)
            for (k in 0 until 3) {
                val a = c.t * (0.5f + k * 0.3f)
                drawOval(c.c3.withA(0.6f), Offset(center.x - r * 1.5f, center.y - r * 0.25f * abs(cos(a))), Size(r * 3f, r * 0.5f * abs(cos(a)) + 2f), style = Stroke(2f))
            }
            checkerFloor(c, size.height * 0.8f, Color(0xFF41155E), Color(0xFF1A0629), c.t * 0.1f)
        },
        Scene(13, "Neon Rain Alley", "Animated Worlds") { c ->
            drawRect(Brush.verticalGradient(listOf(Color(0xFF05010D), Color(0xFF14051F))))
            skyline(c, size.height, 9, size.height * 0.9f, Color(0xFF0D0518), c.c1, 0.9f)
            for (k in 0 until 4) {
                val x = size.width * (0.1f + k * 0.23f); val y = size.height * (0.25f + hash(k) * 0.3f)
                val on = sin(c.t * 4 + k * 3) > -0.6f
                drawRect((if (k % 2 == 0) c.c2 else c.c1).withA(if (on) 0.95f else 0.2f), Offset(x, y), Size(70f, 26f), style = Stroke(4f))
            }
            for (i in 0 until 160) {
                val x = hash(i) * size.width; val sp = 400f + hash(i + 1) * 500f
                val y = (hash(i + 2) * size.height + c.t * sp) % size.height
                drawLine(Color(0xFFB8E8FF).withA(0.35f), Offset(x, y), Offset(x - 3f, y + 18f), 1.3f)
            }
        },
        Scene(14, "Dreamwave Coast", "Animated Worlds") { c ->
            val hz = size.height * 0.6f
            skyGrad(c, hz)
            slicedSun(c, Offset(size.width / 2, hz - 10f), size.minDimension * 0.2f, hz)
            water(c, hz, Color(0xFF071B4A), lerp(c.c2, Color(0xFF071B4A), 0.4f))
            drawRect(Color(0xFFE8B9A0).withA(0.9f), Offset(0f, size.height * 0.88f), Size(size.width, size.height * 0.12f))
            palm(size.width * 0.12f, size.height * 0.92f, size.height * 0.5f, Color(0xFF12041C), sin(c.t * 0.8f))
            palm(size.width * 0.85f, size.height * 0.95f, size.height * 0.42f, Color(0xFF12041C), sin(c.t * 0.8f + 1))
        },
        Scene(15, "Chrome Cathedral", "Animated Worlds") { c ->
            drawRect(Brush.verticalGradient(listOf(Color(0xFF0A0A12), Color(0xFF2A2A3A))))
            val n = 5
            for (i in 0 until n) {
                val x = size.width * (i + 0.5f) / n; val w = size.width / n * 0.8f
                val p = Path().apply { moveTo(x - w / 2, size.height); lineTo(x - w / 2, size.height * 0.35f); lineTo(x, size.height * 0.12f); lineTo(x + w / 2, size.height * 0.35f); lineTo(x + w / 2, size.height); close() }
                drawPath(p, Brush.horizontalGradient(listOf(Color(0xFF555566), Color(0xFFEEEEFF), Color(0xFF8888AA), Color(0xFF333344)), x - w / 2, x + w / 2))
                drawCircle(lerp(c.c1, c.c2, hash(i)).withA(0.5f + 0.3f * sin(c.t + i)), w * 0.18f, Offset(x, size.height * 0.33f))
            }
            for (k in 0 until 6) drawLine(Color.White.withA(0.07f), Offset(size.width * (k / 6f + (c.t * 0.05f) % 0.17f), 0f), Offset(size.width * (k / 6f + 0.3f), size.height), 40f)
        },
        Scene(16, "Saturn Highway", "Animated Worlds") { c ->
            val hz = size.height * 0.55f
            skyGrad(c, hz); stars(c, 80, hz)
            val pc = Offset(size.width * 0.5f, hz * 0.55f); val pr = size.minDimension * 0.17f
            drawCircle(Brush.verticalGradient(listOf(c.sun[0], c.c2), pc.y - pr, pc.y + pr), pr, pc)
            drawOval(c.c3.withA(0.8f), Offset(pc.x - pr * 1.9f, pc.y - pr * 0.25f), Size(pr * 3.8f, pr * 0.5f), style = Stroke(5f))
            drawRect(Color(0xFF0B0418), Offset(0f, hz), Size(size.width, size.height - hz))
            val road = Path().apply { moveTo(size.width * 0.47f, hz); lineTo(size.width * 0.53f, hz); lineTo(size.width * 0.95f, size.height); lineTo(size.width * 0.05f, size.height); close() }
            drawPath(road, Color(0xFF1E0F33))
            for (i in 0 until 10) {
                val k = ((i + (c.t * 1.2f) % 1f) / 10).pow(2f)
                val y = hz + k * (size.height - hz)
                drawRect(c.c3, Offset(size.width / 2 - 2f - k * 6, y), Size(4f + k * 12, 6f + k * 30f))
            }
            drawLine(c.c1, Offset(size.width * 0.47f, hz), Offset(size.width * 0.05f, size.height), 3f)
            drawLine(c.c1, Offset(size.width * 0.53f, hz), Offset(size.width * 0.95f, size.height), 3f)
        },
        Scene(17, "Liminal Atrium", "Animated Worlds") { c ->
            drawRect(Brush.verticalGradient(listOf(Color(0xFFF6E6FF), Color(0xFFD8F3FF))))
            columns(c, 7, size.height * 0.72f, size.height * 0.08f, Color(0xFFFFFFFF), Color(0xFFE8DDF3))
            checkerFloor(c, size.height * 0.72f, Color(0xFFF2D7EE), Color(0xFFD5EEF8), c.t * 0.08f)
            drawRect(Color.White.withA(0.25f + 0.05f * sin(c.t)))
        },
        Scene(18, "Aurora Observatory", "Animated Worlds") { c ->
            drawRect(Brush.verticalGradient(listOf(Color(0xFF01030D), Color(0xFF07162A))))
            stars(c, 120, size.height * 0.7f)
            for (k in 0 until 4) {
                val p = Path(); var x = 0f
                p.moveTo(0f, size.height * 0.3f)
                while (x <= size.width) { p.lineTo(x, size.height * (0.25f + k * 0.05f) + sin(x / 120f + c.t * 0.6f + k) * 40f); x += 16f }
                drawPath(p, (if (k % 2 == 0) Color(0xFF4DFFB0) else c.c1).withA(0.18f), style = Stroke(40f + c.bass * 40f))
            }
            val dome = Path().apply { moveTo(size.width * 0.3f, size.height); lineTo(size.width * 0.3f, size.height * 0.82f); quadraticTo(size.width * 0.5f, size.height * 0.62f, size.width * 0.7f, size.height * 0.82f); lineTo(size.width * 0.7f, size.height); close() }
            drawRect(Color(0xFF02040A), Offset(0f, size.height * 0.9f), Size(size.width, size.height * 0.1f))
            drawPath(dome, Color(0xFF02040A))
            drawLine(Color(0xFF02040A), Offset(size.width * 0.55f, size.height * 0.75f), Offset(size.width * 0.66f, size.height * 0.6f), 14f)
        },
        Scene(19, "Pixel Sunset", "Animated Worlds") { c ->
            val px = max(4f, size.minDimension / 115f)
            val hz = size.height * 0.62f
            val bands = listOf(c.sky[0], c.sky[1], c.sky[2], argb(c.theme.strips[1]), argb(c.theme.strips[2]), argb(c.theme.strips[3]), c.sky[3], c.sky[4])
            val bh = hz / bands.size
            bands.forEachIndexed { i, col ->
                drawRect(col, Offset(0f, i * bh), Size(size.width, bh + 1))
                if (i > 0) { var x = 0f; while (x < size.width) { drawRect(bands[i - 1], Offset(x, floor(i * bh / px) * px), Size(px, px)); x += px * 2 } }
            }
            val r = size.minDimension * 0.18f; val cx = size.width / 2; val cy = hz - r * 0.4f
            var y = cy - r
            while (y < hz) { val dy = y - cy; if (abs(dy) < r) { val half = floor(sqrt(r * r - dy * dy) / px) * px; drawRect(c.sun[0], Offset(cx - half, y), Size(half * 2, px)) }; y += px }
            drawRect(c.floor[0], Offset(0f, hz), Size(size.width, size.height - hz))
            var row = 0f
            while (row < size.height - hz) { if (((row / px).toInt() + (c.t * 6).toInt()) % 6 == 0) drawRect(c.grid, Offset(0f, hz + row), Size(size.width, px)); row += px }
        },
        Scene(20, "Low-Poly Dunes", "Lightweight 3D") { c ->
            val hz = size.height * 0.55f
            skyGrad(c, hz)
            slicedSun(c, Offset(size.width * 0.3f, hz * 0.6f), size.minDimension * 0.12f, hz)
            for (layer in 0 until 4) {
                val base = hz + layer * (size.height - hz) / 4f
                val p = Path(); p.moveTo(0f, size.height)
                for (i in 0..10) { val x = size.width * i / 10f; p.lineTo(x, base - hash(i + layer * 20) * 70f + sin(c.t * 0.3f + i + layer) * 6f) }
                p.lineTo(size.width, size.height); p.close()
                drawPath(p, lerp(argb(c.theme.mtnTop), Color(0xFF1A0614), layer / 4f))
                drawPath(p, c.c3.withA(0.25f), style = Stroke(1.5f))
            }
        },
        Scene(21, "Neon Tunnel", "Lightweight 3D") { c ->
            drawRect(Color.Black)
            for (i in 20 downTo 0) {
                val z = ((i - c.t * 3f) % 20 + 20) % 20 / 20f
                val s = 1f / (0.05f + z)
                val w = size.width * 0.12f * s; val h = size.height * 0.1f * s
                drawRect((if (i % 2 == 0) c.c1 else c.c2).withA(1 - z), Offset(center.x - w, center.y - h), Size(w * 2, h * 2), style = Stroke(2f + 3f * (1 - z)))
            }
        },
        Scene(22, "Liminal Columns", "Lightweight 3D") { c ->
            drawRect(Brush.verticalGradient(listOf(Color(0xFFE9E1F5), Color(0xFFC9D8F0))))
            for (i in 12 downTo 1) {
                val z = ((i - c.t * 0.6f) % 12 + 12) % 12 / 12f
                val s = 1f / (0.1f + z * 1.5f)
                val off = size.width * 0.32f * s
                val cw = 20f * s; val top = center.y - size.height * 0.4f * s; val bot = center.y + size.height * 0.35f * s
                for (side in listOf(-1, 1)) drawRect(Color(0xFFFFFFFF).withA(1 - z * 0.7f), Offset(center.x + side * off - cw / 2, top), Size(cw, bot - top))
            }
        },
        Scene(23, "Poolroom Rings", "Lightweight 3D") { c ->
            drawRect(Color(0xFF8FD8E8))
            for (i in 0 until 14) {
                val r = ((i * 60f + c.t * 40f) % (size.maxDimension))
                drawCircle(Color.White.withA(0.4f * (1 - r / size.maxDimension)), r, center, style = Stroke(6f))
            }
            for (x in 0..12) drawLine(Color.White.withA(0.3f), Offset(size.width * x / 12, 0f), Offset(size.width * x / 12, size.height), 1f)
            for (y in 0..20) drawLine(Color.White.withA(0.3f), Offset(0f, size.height * y / 20), Offset(size.width, size.height * y / 20), 1f)
        },
        Scene(9, "Level 0 Yellow", "Backrooms", backrooms = true) { c -> backroom(c, Color(0xFFC9B458), Color(0xFFB3A04A), Color(0xFF8C7A33), true) },
        Scene(10, "Level 1 Halls", "Backrooms", backrooms = true) { c -> backroom(c, Color(0xFF6E6E6A), Color(0xFF545450), Color(0xFF3A3A36), false) },
        Scene(11, "Poolrooms Spa", "Backrooms", backrooms = true) { c ->
            drawRect(Color(0xFFEAF6F8))
            for (x in 0..16) drawLine(Color(0xFFBFD8DE), Offset(size.width * x / 16, 0f), Offset(size.width * x / 16, size.height * 0.6f), 1.5f)
            for (y in 0..10) drawLine(Color(0xFFBFD8DE), Offset(0f, size.height * 0.06f * y), Offset(size.width, size.height * 0.06f * y), 1.5f)
            water(c, size.height * 0.6f, Color(0xFF3FB6CF), Color(0xFFA6E9F2))
        },
        Scene(12, "Abyssal Pools", "Backrooms", backrooms = true) { c ->
            drawRect(Color(0xFF02101A))
            water(c, size.height * 0.45f, Color(0xFF000308), Color(0xFF07334A))
            for (k in 0 until 5) drawRect(Color(0xFF0D3346).withA(0.6f), Offset(size.width * (0.1f + k * 0.2f), size.height * 0.1f), Size(14f, size.height * 0.35f))
            drawCircle(Color(0xFF7FE7FF).withA(0.08f + 0.05f * sin(c.t)), size.minDimension * 0.4f, Offset(center.x, size.height * 0.45f))
        },
    )

    fun byId(id: Int): Scene? = all.firstOrNull { it.id == id }
}

private fun DrawScope.backroom(c: SceneCtx, wall: Color, wall2: Color, carpet: Color, flicker: Boolean) {
    val w = size.width; val h = size.height
    val hz = h * 0.5f
    drawRect(wall)
    // receding wall stripes
    for (i in 0 until 30) { val x = w * i / 30f; drawLine(wall2, Offset(x, 0f), Offset(x, h), 3f) }
    // ceiling with fluorescent panels
    drawRect(Color(0xFFE8E2C0), size = Size(w, h * 0.32f))
    for (r in 0 until 5) {
        val k = ((r + (c.t * 0.15f) % 1f) / 5).pow(2f)
        val y = h * 0.32f - k * h * 0.3f
        val pw = w * (0.1f + k * 0.5f)
        val on = !flicker || sin(c.t * 23 + r * 5) > -0.95f
        drawRect(Color(0xFFFFFFF0).withA(if (on) 0.95f else 0.3f), Offset(w / 2 - pw / 2, y), Size(pw, 6f + k * 10f))
    }
    // doorway in the back
    drawRect(Color(0xFF1A160A).withA(0.85f), Offset(w * 0.45f, hz - h * 0.12f), Size(w * 0.1f, h * 0.2f))
    // carpet
    drawRect(Brush.verticalGradient(listOf(lerp(carpet, Color.Black, 0.3f), carpet), hz + h * 0.08f, h), Offset(0f, hz + h * 0.08f), Size(w, h * 0.42f))
    drawRect(Color.Black.withA(0.12f + 0.04f * sin(c.t * 7)))
}

// ---------------------------------------------------------------- Depth FX layers

class BgLayer(val id: String, val label: String, val draw: DrawScope.(SceneCtx) -> Unit)

object BgLayers {
    val all = listOf(
        BgLayer("gridWarp", "3D Grid Wave") { c ->
            val hz = size.height * 0.6f
            for (i in 0 until 12) {
                val p = Path(); val k = ((i + (c.t * 0.4f) % 1f) / 12).pow(2f); val y = hz + k * (size.height - hz)
                var x = 0f; p.moveTo(0f, y)
                while (x <= size.width) { p.lineTo(x, y + sin(x / 80f + c.t * 2) * (6f + c.bass * 30f) * k); x += 20f }
                drawPath(p, c.c1.withA(0.25f + k * 0.4f), style = Stroke(1.5f))
            }
        },
        BgLayer("solarRings", "Solar Rings") { c ->
            for (i in 0 until 6) { val r = ((c.t * 60f + i * 70f) % 420f) * size.minDimension / 500f; drawCircle(c.sun[0].withA(0.35f * (1 - r / size.minDimension)), r, Offset(center.x, size.height * 0.45f), style = Stroke(3f)) }
        },
        BgLayer("starWarp", "Star Warp") { c ->
            for (i in 0 until 90) {
                val z = ((hash(i) - c.t * 0.25f * (1 + c.bass)) % 1f + 1f) % 1f
                val a = hash(i + 500) * 2 * PI.toFloat()
                val r = (1 - z) * size.maxDimension * 0.7f
                val p = Offset(center.x + cos(a) * r, center.y + sin(a) * r)
                drawLine(Color.White.withA(1 - z), p, Offset(center.x + cos(a) * r * 0.92f, center.y + sin(a) * r * 0.92f), 2f)
            }
        },
        BgLayer("skySpectrum", "Horizon FFT") { c ->
            val n = 48; val bw = size.width / n; val hz = size.height * 0.62f
            for (i in 0 until n) { val v = c.f.bands[i * AudioFrame.BANDS / n]; drawRect(lerp(c.c1, c.c2, i / n.toFloat()).withA(0.55f), Offset(i * bw, hz - v * size.height * 0.25f), Size(bw - 2f, v * size.height * 0.25f)) }
        },
        BgLayer("cyberRain", "Cyber Rain") { c ->
            for (i in 0 until 70) {
                val x = hash(i) * size.width; val y = (hash(i + 2) * size.height + c.t * (200f + hash(i) * 300f)) % size.height
                drawLine(c.c1.withA(0.5f), Offset(x, y), Offset(x, y + 30f), 2f)
            }
        },
        BgLayer("monoliths", "3D Props") { c ->
            for (i in 0 until 3) {
                val x = size.width * (0.2f + i * 0.3f); val y = size.height * 0.45f + sin(c.t + i * 2) * 14f; val a = c.t * 0.6f + i
                val p = Path().apply { for (k in 0..4) { val aa = a + k * PI.toFloat() / 2; val px = x + cos(aa) * 26f; val py = y + sin(aa) * 26f * 0.5f + (if (k % 2 == 0) -30f else 30f); if (k == 0) moveTo(px, py) else lineTo(px, py) } }
                drawPath(p, c.c3.withA(0.7f), style = Stroke(2.5f))
            }
        },
        BgLayer("depthMotes", "Depth Motes") { c ->
            for (i in 0 until 50) { val x = (hash(i) * size.width + sin(c.t * 0.3f + i) * 30f); val y = (hash(i + 9) * size.height - c.t * 10f * (1 + hash(i)) + size.height * 10) % size.height; drawCircle(Color.White.withA(0.15f + 0.2f * hash(i + 3)), 1.5f + hash(i + 4) * 4f, Offset(x, y)) }
        },
        BgLayer("parallaxRings", "Parallax Rings") { c ->
            for (i in 0 until 4) drawOval(c.c2.withA(0.25f), Offset(center.x - (200f + i * 90f) + sin(c.t * 0.5f + i) * 30f, center.y - (40f + i * 18f)), Size((200f + i * 90f) * 2, (40f + i * 18f) * 2), style = Stroke(2f))
        },
        BgLayer("glassShards", "Glass Shards") { c ->
            for (i in 0 until 10) {
                val x = hash(i) * size.width; val y = hash(i + 1) * size.height; val a = c.t * 0.3f * (if (i % 2 == 0) 1 else -1) + i
                val p = Path().apply { moveTo(x + cos(a) * 40f, y + sin(a) * 40f); lineTo(x + cos(a + 2.2f) * 25f, y + sin(a + 2.2f) * 25f); lineTo(x + cos(a + 4f) * 32f, y + sin(a + 4f) * 32f); close() }
                drawPath(p, Color.White.withA(0.12f)); drawPath(p, c.c1.withA(0.4f), style = Stroke(1.5f))
            }
        },
        BgLayer("auroraRibbons", "Aurora Veil") { c ->
            for (k in 0 until 3) { val p = Path(); var x = 0f; p.moveTo(0f, size.height * 0.2f); while (x <= size.width) { p.lineTo(x, size.height * (0.15f + k * 0.06f) + sin(x / 140f + c.t * 0.5f + k) * 30f); x += 16f }; drawPath(p, Color(0xFF4DFFB0).withA(0.12f), style = Stroke(36f)) }
        },
        BgLayer("cometTrails", "Comet Trails") { c ->
            for (i in 0 until 4) { val k = ((c.t * 0.15f + hash(i)) % 1f); val x = size.width * (1.2f - k * 1.4f); val y = size.height * (0.1f + hash(i + 3) * 0.3f + k * 0.2f); drawLine(Brush.linearGradient(listOf(Color.White, Color.Transparent), Offset(x, y), Offset(x + 160f, y - 50f)), Offset(x, y), Offset(x + 160f, y - 50f), 3f); drawCircle(Color.White, 3.5f, Offset(x, y)) }
        },
        BgLayer("lightShafts", "Light Shafts") { c ->
            for (i in 0 until 5) { val x = size.width * (0.1f + i * 0.2f) + sin(c.t * 0.2f + i) * 30f; val p = Path().apply { moveTo(x, 0f); lineTo(x + 50f, 0f); lineTo(x + 180f, size.height); lineTo(x + 60f, size.height); close() }; drawPath(p, Color.White.withA(0.05f + c.bass * 0.05f)) }
        },
        BgLayer("depthRidges", "Depth Ridges") { c ->
            for (k in 0 until 3) { val base = size.height * (0.55f + k * 0.08f); val p = Path(); p.moveTo(0f, size.height); for (i in 0..12) p.lineTo(size.width * i / 12f, base - hash(i + k * 30) * 60f); p.lineTo(size.width, size.height); p.close(); drawPath(p, Color.Black.withA(0.2f + k * 0.1f)); drawPath(p, c.c2.withA(0.3f), style = Stroke(1.5f)) }
        },
    )
}

/** CUSTOMIZE_95's solid backgrounds. */
val SolidColors: List<Pair<String, Long>> = listOf(
    "Win95 Teal" to 0xFF008080, "Win95 Navy" to 0xFF000080, "Win95 Gray" to 0xFFB8B8B8, "Win98 Silver" to 0xFFC0C0C0,
    "Win98 Teal" to 0xFF018281, "Vapor Purple" to 0xFF281060, "Vapor Dark" to 0xFF0D0221, "Electric Blue" to 0xFF0057FF,
    "Matrix Green" to 0xFF001100, "Outrun Red" to 0xFF290005, "Jet Black" to 0xFF08080C, "Graphite" to 0xFF252837,
    "Deep Plum" to 0xFF24072F, "Ultraviolet" to 0xFF4E1B89, "Neon Magenta" to 0xFFC70076, "Rose Quartz" to 0xFFAD4676,
    "Ember" to 0xFF8C2814, "Tangerine" to 0xFFED6A24, "Gold CRT" to 0xFFBB8A26, "Midnight Blue" to 0xFF101D3B,
    "Arctic" to 0xFFB8E5EF, "Glacier" to 0xFF2F8FA3, "Lagoon" to 0xFF006D77, "Forest" to 0xFF123D2A, "Moss" to 0xFF536B2F,
)
