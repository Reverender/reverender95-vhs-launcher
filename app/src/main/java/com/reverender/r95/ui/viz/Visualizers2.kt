package com.reverender.r95.ui.viz

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import com.reverender.r95.audio.AudioBus
import com.reverender.r95.audio.AudioFrame
import com.reverender.r95.core.VaporTheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

private val bg = Modifier.fillMaxSize().background(Color.Black)
private fun band(f: AudioFrame, i: Int, of: Int) = f.bands[(i * AudioFrame.BANDS / of).coerceIn(0, AudioFrame.BANDS - 1)]
private fun h(i: Int): Float { var x = i * 374761393 + 668265263; x = (x xor (x ushr 13)) * 1274126177; return ((x xor (x ushr 16)) and 0xFFFF) / 65535f }

/** HYPER_3D: a rotating tesseract whose 4D rotation speeds up with the bass. */
@Composable
fun HyperCubeViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    val ang = remember { FloatArray(3) }
    val verts = remember { Array(16) { i -> FloatArray(4) { k -> if ((i shr k) and 1 == 1) 1f else -1f } } }
    Canvas(bg) {
        time.value
        val f = AudioBus.currentFrame()
        ang[0] += 0.008f + f.bass * 0.04f; ang[1] += 0.006f + f.mid * 0.03f; ang[2] += 0.004f
        val pts = Array(16) { Offset.Zero }
        val s = min(size.width, size.height) * (0.22f + f.bass * 0.06f)
        for (i in 0 until 16) {
            var (x, y, z, w) = verts[i].let { listOf(it[0], it[1], it[2], it[3]) }
            // rotate in XW, YZ and XY planes
            var c = cos(ang[0]); var sn = sin(ang[0]); val x1 = x * c - w * sn; w = x * sn + w * c; x = x1
            c = cos(ang[1]); sn = sin(ang[1]); val y1 = y * c - z * sn; z = y * sn + z * c; y = y1
            c = cos(ang[2]); sn = sin(ang[2]); val x2 = x * c - y * sn; y = x * sn + y * c; x = x2
            val p4 = 1f / (3f - w); val p3 = 1f / (3.5f - z * p4)
            pts[i] = Offset(center.x + x * p4 * p3 * s * 4f, center.y + y * p4 * p3 * s * 4f)
        }
        for (i in 0 until 16) for (k in 0 until 4) {
            val j = i xor (1 shl k)
            if (j > i) drawLine(lerp(argb(theme.primary), argb(theme.secondary), k / 3f), pts[i], pts[j], 2f + f.treble * 3f, cap = StrokeCap.Round)
        }
        pts.forEach { drawCircle(argb(theme.accent), 3.5f, it) }
    }
}

/** WATERFALL: scrolling spectrogram. */
@Composable
fun WaterfallViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    val rows = remember { ArrayDeque<FloatArray>() }
    val last = remember { FloatArray(1) }
    Canvas(bg) {
        val t = time.value
        if (t - last[0] > 1f / 40f) { last[0] = t; rows.addFirst(AudioBus.currentFrame().bands.copyOf()); while (rows.size > 120) rows.removeLast() }
        val rh = size.height / 120f; val bw = size.width / AudioFrame.BANDS
        val c0 = argb(theme.sky[1]); val c1 = argb(theme.secondary); val c2 = argb(theme.primary); val c3 = Color.White
        rows.forEachIndexed { r, b ->
            for (i in 0 until AudioFrame.BANDS) {
                val v = b[i]
                if (v < 0.08f) continue
                val col = when { v < 0.4f -> lerp(c0, c1, v / 0.4f); v < 0.75f -> lerp(c1, c2, (v - 0.4f) / 0.35f); else -> lerp(c2, c3, (v - 0.75f) / 0.25f) }
                drawRect(col, Offset(i * bw, r * rh), Size(bw + 0.5f, rh + 0.5f))
            }
        }
    }
}

/** RIBBON_3D: a twisting ribbon traced by the waveform. */
@Composable
fun RibbonViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    Canvas(bg) {
        val t = time.value
        val f = AudioBus.currentFrame()
        val n = 90
        var prevA: Offset? = null; var prevB: Offset? = null
        for (i in 0 until n) {
            val u = i / (n - 1f)
            val x = size.width * (0.05f + u * 0.9f)
            val wv = f.wave[(u * (f.wave.size - 1)).toInt()]
            val y = size.height / 2 + sin(u * 6f + t * 1.4f) * size.height * 0.18f + wv * size.height * 0.25f
            val twist = u * 8f + t * 2f
            val half = size.height * (0.06f + f.bass * 0.08f) * cos(twist)
            val a = Offset(x, y - half); val b = Offset(x, y + half)
            if (prevA != null && prevB != null) {
                val p = Path().apply { moveTo(prevA.x, prevA.y); lineTo(a.x, a.y); lineTo(b.x, b.y); lineTo(prevB.x, prevB.y); close() }
                val front = cos(twist) > 0
                drawPath(p, (if (front) argb(theme.primary) else argb(theme.secondary)).withA(0.35f + 0.5f * abs(cos(twist))))
            }
            prevA = a; prevB = b
        }
    }
}

/** ORBITAL_ATLAS: planets on elliptical orbits; each orbit is driven by a band. */
@Composable
fun OrbitalViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    val phase = remember { FloatArray(8) }
    Canvas(bg) {
        time.value
        val f = AudioBus.currentFrame()
        for (i in 0 until 40) drawCircle(Color.White.withA(0.3f), 1.2f, Offset(h(i) * size.width, h(i + 77) * size.height))
        val sunR = min(size.width, size.height) * (0.06f + f.bass * 0.03f)
        drawCircle(Brush.radialGradient(listOf(Color.White, argb(theme.sun[0]), Color.Transparent), center, sunR * 2.4f), sunR * 2.4f, center)
        for (k in 0 until 8) {
            val v = band(f, k * 7 + 3, AudioFrame.BANDS)
            phase[k] += (0.004f + 0.01f / (k + 1)) * (1f + v * 4f)
            val rx = min(size.width, size.height) * (0.12f + k * 0.05f); val ry = rx * 0.45f
            drawOval(argb(theme.primary).withA(0.18f), Offset(center.x - rx, center.y - ry), Size(rx * 2, ry * 2), style = Stroke(1f))
            val p = Offset(center.x + cos(phase[k]) * rx, center.y + sin(phase[k]) * ry)
            drawCircle(lerp(argb(theme.secondary), argb(theme.accent), k / 7f), 4f + v * 10f, p)
        }
    }
}

/** FRACTAL_BLOOM: a recursive tree; branch angle and length bloom with the music. */
@Composable
fun FractalViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    Canvas(bg) {
        val t = time.value
        val f = AudioBus.currentFrame()
        val spread = 0.35f + f.mid * 0.5f + sin(t * 0.5f) * 0.08f
        fun DrawScope.branch(p: Offset, ang: Float, len: Float, depth: Int) {
            if (depth == 0 || len < 2f) {
                drawCircle(argb(theme.secondary).withA(0.5f + f.treble * 0.5f), 2f + f.treble * 4f, p); return
            }
            val e = Offset(p.x + cos(ang) * len, p.y + sin(ang) * len)
            drawLine(lerp(argb(theme.primary), argb(theme.accent), 1f - depth / 9f), p, e, depth * 0.8f, cap = StrokeCap.Round)
            branch(e, ang - spread, len * (0.68f + f.bass * 0.08f), depth - 1)
            branch(e, ang + spread, len * (0.68f + f.bass * 0.08f), depth - 1)
        }
        branch(Offset(size.width / 2, size.height * 0.95f), -PI.toFloat() / 2 + sin(t * 0.3f) * 0.05f, size.height * 0.24f, 9)
    }
}

/** PRISMATIC_GLASS: rotating prisms splitting light into spectrum-coloured beams. */
@Composable
fun PrismViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    val rainbow = listOf(Color(0xFFFF004C), Color(0xFFFF8A00), Color(0xFFFFE600), Color(0xFF00FF7B), Color(0xFF00B7FF), Color(0xFF8A2BFF))
    Canvas(bg) {
        val t = time.value
        val f = AudioBus.currentFrame()
        for (k in 0 until 5) {
            val cx = size.width * (0.15f + k * 0.18f); val cy = size.height * (0.5f + sin(t * 0.6f + k) * 0.18f)
            val r = min(size.width, size.height) * 0.09f * (1 + band(f, k * 10, AudioFrame.BANDS) * 0.6f)
            val a = t * (0.4f + k * 0.1f)
            val tri = Path().apply { for (q in 0..3) { val aa = a + q * 2.0944f; if (q == 0) moveTo(cx + cos(aa) * r, cy + sin(aa) * r) else lineTo(cx + cos(aa) * r, cy + sin(aa) * r) } }
            rainbow.forEachIndexed { i, c ->
                val v = band(f, i * 10 + k, AudioFrame.BANDS)
                drawLine(c.withA(0.25f + v * 0.6f), Offset(cx, cy), Offset(cx + cos(a + i * 0.12f) * size.width, cy + sin(a + i * 0.12f) * size.width), 2f + v * 6f)
            }
            drawPath(tri, Color.White.withA(0.12f)); drawPath(tri, Color.White.withA(0.8f), style = Stroke(2f))
        }
    }
}

/** SONAR_DEPTH: radar sweep; blips appear where loud bands are. */
@Composable
fun SonarViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    val blips = remember { ArrayList<FloatArray>() } // angle, radius, life
    Canvas(Modifier.fillMaxSize().background(Color(0xFF001208))) {
        val t = time.value
        val f = AudioBus.currentFrame()
        val r = min(size.width, size.height) * 0.46f
        val green = argb(theme.primary)
        for (k in 1..4) drawCircle(green.withA(0.25f), r * k / 4, center, style = Stroke(1.5f))
        drawLine(green.withA(0.25f), Offset(center.x - r, center.y), Offset(center.x + r, center.y))
        drawLine(green.withA(0.25f), Offset(center.x, center.y - r), Offset(center.x, center.y + r))
        val sweep = (t * 1.6f) % (2 * PI.toFloat())
        for (k in 0 until 24) {
            val a = sweep - k * 0.03f
            drawLine(green.withA(0.5f * (1 - k / 24f)), center, Offset(center.x + cos(a) * r, center.y + sin(a) * r), 3f)
        }
        if (f.bass > 0.55f && blips.size < 40) blips.add(floatArrayOf(sweep, r * (0.2f + h((t * 100).toInt()) * 0.75f), 1f))
        val it = blips.iterator()
        while (it.hasNext()) {
            val b = it.next(); b[2] -= 0.008f
            if (b[2] <= 0) { it.remove(); continue }
            drawCircle(argb(theme.accent).withA(b[2]), 4f + (1 - b[2]) * 10f, Offset(center.x + cos(b[0]) * b[1], center.y + sin(b[0]) * b[1]), style = Stroke(2f))
            drawCircle(argb(theme.accent).withA(b[2]), 3f, Offset(center.x + cos(b[0]) * b[1], center.y + sin(b[0]) * b[1]))
        }
    }
}

/** MAGNETIC_FIELD: field lines between two poles, bent and charged by the bass. */
@Composable
fun MagneticViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    Canvas(bg) {
        val t = time.value
        val f = AudioBus.currentFrame()
        val sep = size.width * (0.18f + f.bass * 0.06f)
        val a = Offset(center.x - sep, center.y + sin(t) * 10f); val b = Offset(center.x + sep, center.y - sin(t) * 10f)
        for (k in 0 until 18) {
            val ang = (k / 18f) * 2 * PI.toFloat() + t * 0.1f
            var p = Offset(a.x + cos(ang) * 8f, a.y + sin(ang) * 8f)
            val path = Path().apply { moveTo(p.x, p.y) }
            for (s in 0 until 140) {
                fun field(q: Offset, pole: Offset, sign: Float): Offset { val dx = q.x - pole.x; val dy = q.y - pole.y; val d2 = max(dx * dx + dy * dy, 30f); val d = sqrt(d2); return Offset(sign * dx / (d2 * d), sign * dy / (d2 * d)) }
                val fa = field(p, a, 1f); val fb = field(p, b, -1f)
                var fx = fa.x + fb.x; var fy = fa.y + fb.y
                val m = sqrt(fx * fx + fy * fy) + 1e-9f
                fx /= m; fy /= m
                p = Offset(p.x + fx * 6f, p.y + fy * 6f)
                path.lineTo(p.x, p.y)
                if ((p - b).getDistance() < 10f || p.x < 0 || p.x > size.width || p.y < 0 || p.y > size.height) break
            }
            drawPath(path, lerp(argb(theme.primary), argb(theme.secondary), k / 17f).withA(0.45f + f.mid * 0.5f), style = Stroke(1.6f))
        }
        drawCircle(Color(0xFFFF3355), 12f + f.bass * 10f, a); drawCircle(Color(0xFF3399FF), 12f + f.bass * 10f, b)
    }
}

/** KALEIDO_ENGINE: the spectrum mirrored into a 12-fold kaleidoscope. */
@Composable
fun KaleidoViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    Canvas(bg) {
        val t = time.value
        val f = AudioBus.currentFrame()
        val r = size.maxDimension * 0.6f
        val segments = 12
        val wedge = Path().apply { moveTo(center.x, center.y); lineTo(center.x + r, center.y); lineTo(center.x + cos(2 * PI.toFloat() / segments) * r, center.y + sin(2 * PI.toFloat() / segments) * r); close() }
        for (s in 0 until segments) {
            rotate(s * 360f / segments + t * 6f, center) {
                clipPath(wedge) {
                    for (i in 0 until 14) {
                        val v = band(f, i * 4, AudioFrame.BANDS)
                        val d = r * (i + 1) / 15f
                        val off = sin(t + i) * 30f
                        drawCircle(lerp(argb(theme.primary), argb(theme.secondary), i / 13f).withA(0.3f + v * 0.6f), 6f + v * 28f, Offset(center.x + d, center.y + 20f + off))
                    }
                }
            }
        }
    }
}

/** STARFORGE: a spiral galaxy; the beat ignites new stars at the core. */
@Composable
fun StarforgeViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    Canvas(bg) {
        val t = time.value
        val f = AudioBus.currentFrame()
        val n = 700
        val rMax = size.minDimension * 0.48f
        for (i in 0 until n) {
            val arm = i % 3
            val d = h(i) * h(i + 3)
            val rr = d * rMax
            val a = arm * 2.094f + d * 9f - t * (0.3f + (1 - d) * 0.6f) + (h(i + 9) - 0.5f) * 0.5f
            val v = band(f, (d * (AudioFrame.BANDS - 1)).toInt(), AudioFrame.BANDS)
            val col = lerp(argb(theme.accent), lerp(argb(theme.primary), argb(theme.secondary), h(i + 5)), d)
            drawCircle(col.withA(0.35f + v * 0.65f), 1f + v * 3f + (1 - d) * 1.2f, Offset(center.x + cos(a) * rr, center.y + sin(a) * rr * 0.6f))
        }
        drawCircle(Brush.radialGradient(listOf(Color.White, argb(theme.accent).withA(0.6f), Color.Transparent), center, rMax * (0.15f + f.bass * 0.15f)), rMax * (0.15f + f.bass * 0.15f), center)
    }
}
