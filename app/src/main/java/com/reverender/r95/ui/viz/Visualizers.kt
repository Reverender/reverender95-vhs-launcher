package com.reverender.r95.ui.viz

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.reverender.r95.audio.AudioBus
import com.reverender.r95.audio.AudioFrame
import com.reverender.r95.core.VaporTheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private val vizBg = Modifier.fillMaxSize().background(Color.Black)

/** Classic bar spectrum with falling peak caps (used in VAPOR_AMP and GRAPHIC_EQ). */
@Composable
fun SpectrumBars(theme: VaporTheme, modifier: Modifier = Modifier, bars: Int = 32) {
    val time = rememberFrameTime()
    val caps = remember(bars) { FloatArray(bars) }
    Canvas(modifier) {
        time.value
        val f = AudioBus.currentFrame()
        val per = AudioFrame.BANDS / bars
        val gap = 2f
        val bw = (size.width - gap * (bars - 1)) / bars
        val top = argb(theme.secondary); val bottom = argb(theme.primary)
        for (b in 0 until bars) {
            var v = 0f
            for (k in 0 until per) v = max(v, f.bands[b * per + k])
            val h = v * size.height
            val x = b * (bw + gap)
            val segH = 4f
            var y = size.height - segH
            while (y > size.height - h) {
                val k = 1f - y / size.height
                drawRect(lerp(bottom, top, k), Offset(x, y), Size(bw, segH - 1.5f))
                y -= segH
            }
            caps[b] = if (v > caps[b]) v else max(0f, caps[b] - 0.012f)
            drawRect(argb(theme.accent), Offset(x, size.height - caps[b] * size.height - 3f), Size(bw, 2f))
        }
    }
}

/** PLASMA_EQ: low-res sine plasma whose speed and palette follow the music. */
@Composable
fun PlasmaViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    val phase = remember { FloatArray(1) }
    Canvas(vizBg) {
        val t = time.value
        val f = AudioBus.currentFrame()
        phase[0] += 0.016f * (1f + f.bass * 3f)
        val p = phase[0]
        val cols = 44
        val cell = size.width / cols
        val rows = (size.height / cell).toInt() + 1
        val c1 = argb(theme.primary); val c2 = argb(theme.secondary); val c3 = argb(theme.accent)
        val cx = cols / 2f; val cy = rows / 2f
        for (y in 0 until rows) for (x in 0 until cols) {
            val dx = x - cx; val dy = y - cy
            var v = sin(x * 0.16f + p) + sin(y * 0.13f - p * 1.3f) + sin((x + y) * 0.09f + p * 0.7f) +
                sin(sqrt(dx * dx + dy * dy) * 0.32f - p * 2f - f.bass * 3f)
            v = (v + 4f) / 8f
            val band = f.bands[(x * AudioFrame.BANDS / cols).coerceIn(0, AudioFrame.BANDS - 1)]
            val col = if (v < 0.5f) lerp(c1, c2, v * 2f) else lerp(c2, c3, (v - 0.5f) * 2f)
            val bright = 0.35f + 0.45f * band + 0.2f * sin(t + v * 6f)
            drawRect(lerp(Color.Black, col, bright.coerceIn(0f, 1f)), Offset(x * cell, y * cell), Size(cell + 0.5f, cell + 0.5f))
        }
    }
}

/** LISSAJOUS: X/Y scope with glowing phosphor trails. */
@Composable
fun LissajousViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    val trails = remember { ArrayDeque<FloatArray>() }
    Canvas(vizBg) {
        val t = time.value
        val f = AudioBus.currentFrame()
        val n = AudioFrame.WAVE
        var peak = 0.05f
        for (i in 0 until n) peak = max(peak, max(abs(f.left[i]), abs(f.right[i])))
        // Stereo content draws a true X/Y scope; near-mono content is spread with a short lag
        // so it still opens into loops instead of collapsing to a diagonal line.
        var diff = 0f
        for (i in 0 until n) diff += abs(f.left[i] - f.right[i])
        val mono = diff / n < 0.01f
        val lag = 9
        val pts = FloatArray(n * 2)
        val scale = min(size.width, size.height) * 0.42f / peak
        val rot = t * 0.25f
        val cr = cos(rot); val sr = sin(rot)
        for (i in 0 until n) {
            val x0 = if (mono) f.wave[i] else f.left[i]
            val y0 = if (mono) f.wave[(i + lag) % n] else f.right[i]
            // idle orbit when silent
            val ix = if (peak <= 0.051f) 0.6f * sin(3f * i * 2f * PI.toFloat() / n + t) * 0.05f else x0
            val iy = if (peak <= 0.051f) 0.6f * sin(2f * i * 2f * PI.toFloat() / n) * 0.05f else y0
            pts[i * 2] = size.width / 2 + (ix * cr - iy * sr) * scale
            pts[i * 2 + 1] = size.height / 2 + (ix * sr + iy * cr) * scale
        }
        trails.addLast(pts)
        while (trails.size > 7) trails.removeFirst()
        val color = argb(theme.primary)
        trails.forEachIndexed { idx, tp ->
            val age = (idx + 1f) / trails.size
            val path = Path().apply {
                moveTo(tp[0], tp[1])
                for (i in 1 until n) lineTo(tp[i * 2], tp[i * 2 + 1])
            }
            if (idx == trails.size - 1) drawPath(path, color.withA(0.25f), style = Stroke(9f, cap = StrokeCap.Round))
            drawPath(path, lerp(argb(theme.secondary), color, age).withA(age * 0.9f), style = Stroke(2.2f))
        }
        // graticule
        val g = color.withA(0.15f)
        drawLine(g, Offset(size.width / 2, 0f), Offset(size.width / 2, size.height))
        drawLine(g, Offset(0f, size.height / 2), Offset(size.width, size.height / 2))
    }
}

/** HEX_RAIN: falling hex digits; each column speeds up with its frequency band. */
@Composable
fun HexRainViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    val state = remember { HexRainState() }
    val paint = remember { Paint(Paint.ANTI_ALIAS_FLAG).apply { typeface = Typeface.MONOSPACE; isFakeBoldText = true } }
    Canvas(vizBg) {
        val t = time.value
        val f = AudioBus.currentFrame()
        val charH = 18f * density / 2.2f + 8f
        paint.textSize = charH
        val charW = charH * 0.65f
        val cols = max(1, (size.width / charW).toInt())
        state.ensure(cols, size.height, charH)
        val dt = (t - state.lastT).coerceIn(0f, 0.1f); state.lastT = t
        val head = argb(theme.accent); val body = argb(theme.primary)
        drawIntoCanvas { canvas ->
            val nc = canvas.nativeCanvas
            for (c in 0 until cols) {
                val band = f.bands[(c * AudioFrame.BANDS / cols).coerceIn(0, AudioFrame.BANDS - 1)]
                state.y[c] += (state.speed[c] * (0.6f + band * 3.5f)) * dt * charH * 8f
                if (state.y[c] - state.len[c] * charH > size.height) state.reset(c, charH)
                val x = c * charW
                for (k in 0 until state.len[c]) {
                    val y = state.y[c] - k * charH
                    if (y < -charH || y > size.height + charH) continue
                    val a = 1f - k / state.len[c].toFloat()
                    val color = if (k == 0) head else lerp(argb(theme.secondary), body, a)
                    paint.color = color.withA(a * (0.5f + band * 0.5f) + if (k == 0) 0.5f else 0f).toArgb()
                    val glyph = state.glyph(c, k, t)
                    nc.drawText(glyph, x, y, paint)
                }
            }
        }
    }
}

private class HexRainState {
    var cols = 0
    var y = FloatArray(0); var speed = FloatArray(0); var len = IntArray(0); var seed = IntArray(0)
    var lastT = 0f
    private val hex = "0123456789ABCDEF".map { it.toString() }
    fun ensure(c: Int, h: Float, charH: Float) {
        if (c == cols) return
        cols = c
        y = FloatArray(c) { Random.nextFloat() * h }
        speed = FloatArray(c) { 0.6f + Random.nextFloat() }
        len = IntArray(c) { 6 + Random.nextInt(18) }
        seed = IntArray(c) { Random.nextInt(1000) }
    }
    fun reset(c: Int, charH: Float) {
        y[c] = -Random.nextFloat() * charH * 10
        speed[c] = 0.6f + Random.nextFloat()
        len[c] = 6 + Random.nextInt(18)
        seed[c] = Random.nextInt(1000)
    }
    fun glyph(c: Int, k: Int, t: Float): String = hex[((seed[c] + k * 7 + (t * (3 + k % 5)).toInt()) and 15)]
}

/** ANALOG_VU: twin needle meters with proper VU ballistics and peak LEDs. */
@Composable
fun AnalogVuViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    val needles = remember { FloatArray(2) }
    val peaks = remember { FloatArray(2) }
    Canvas(Modifier.fillMaxSize().background(Color(0xFF14100A))) {
        time.value
        val f = AudioBus.currentFrame()
        val levels = floatArrayOf(f.rmsL, f.rmsR)
        val side = size.width > size.height * 1.3f
        val mw = if (side) size.width / 2 else size.width
        val mh = if (side) size.height else size.height / 2
        for (ch in 0..1) {
            val db = if (levels[ch] < 1e-5f) -40f else 20f * log10(levels[ch]) + 18f // ~0 VU at -18 dBFS
            val target = ((db + 20f) / 23f).coerceIn(0f, 1f)
            needles[ch] += (target - needles[ch]) * 0.18f
            peaks[ch] = if (db > 0f) 1f else max(0f, peaks[ch] - 0.03f)
            val ox = if (side) ch * mw else 0f
            val oy = if (side) 0f else ch * mh
            drawMeter(Offset(ox, oy), Size(mw, mh), needles[ch], peaks[ch], if (ch == 0) "L" else "R", theme)
        }
    }
}

private fun DrawScope.drawMeter(o: Offset, s: Size, v: Float, peak: Float, label: String, theme: VaporTheme) {
    val pad = 10f
    val face = Size(s.width - pad * 2, s.height - pad * 2)
    val fo = Offset(o.x + pad, o.y + pad)
    drawRect(Color(0xFFF3E2B3), fo, face)
    drawRect(Color(0xFF6B5A3A), fo, face, style = Stroke(3f))
    val pivot = Offset(fo.x + face.width / 2, fo.y + face.height * 0.95f)
    val r = min(face.width * 0.48f, face.height * 0.8f)
    val a0 = -50f; val a1 = 50f
    // scale: -20..+3 VU, red zone above 0
    for (i in 0..23) {
        val k = i / 23f
        val ang = Math.toRadians((a0 + (a1 - a0) * k - 90).toDouble())
        val red = k > 20f / 23f
        val len = if (i % 5 == 0 || i == 20) 0.12f else 0.06f
        val p1 = Offset(pivot.x + cos(ang).toFloat() * r, pivot.y + sin(ang).toFloat() * r)
        val p2 = Offset(pivot.x + cos(ang).toFloat() * r * (1 - len), pivot.y + sin(ang).toFloat() * r * (1 - len))
        drawLine(if (red) Color(0xFFC0281E) else Color(0xFF2B2216), p1, p2, strokeWidth = if (i % 5 == 0) 3f else 1.5f)
    }
    val ang = Math.toRadians((a0 + (a1 - a0) * v - 90).toDouble())
    drawLine(
        Color(0xFF1A1208),
        pivot,
        Offset(pivot.x + cos(ang).toFloat() * r * 0.98f, pivot.y + sin(ang).toFloat() * r * 0.98f),
        strokeWidth = 3f, cap = StrokeCap.Round,
    )
    drawCircle(Color(0xFF2B2216), 8f, pivot)
    drawCircle(lerp(Color(0xFF400000), Color(0xFFFF2A1A), peak), 7f, Offset(fo.x + face.width - 18f, fo.y + 18f))
    drawCircle(argb(theme.primary).withA(0.85f), 5f, Offset(fo.x + 18f, fo.y + 18f))
    drawIntoCanvas {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 22f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF2B2216.toInt() }
        it.nativeCanvas.drawText("VU  $label", fo.x + face.width / 2 - 26f, fo.y + face.height * 0.62f, p)
    }
}

/** CANYON_3D: spectrum history extruded into a scrolling neon terrain. */
@Composable
fun CanyonViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    val hist = remember { CanyonHistory(48, 40) }
    Canvas(vizBg) {
        val t = time.value
        val f = AudioBus.currentFrame()
        if (t - hist.lastPush > 1f / 30f) { hist.push(f); hist.lastPush = t }
        val w = size.width; val h = size.height
        val horizon = h * 0.28f
        drawRect(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color.Black, argb(theme.sky[2]).withA(0.6f)), 0f, horizon * 1.4f), size = Size(w, horizon * 1.4f))
        val near = argb(theme.primary); val far = argb(theme.secondary)
        for (r in hist.rows - 1 downTo 0) {
            val row = hist.row(r)
            val d = 1f + r * 0.32f
            val baseY = horizon + (h * 0.98f - horizon) / d
            val path = Path()
            for (b in 0 until hist.bands) {
                val u = b / (hist.bands - 1f) - 0.5f
                // valley in the middle so it reads as a canyon
                val amp = row[b] * (0.5f + abs(u) * 1.4f)
                val x = w / 2 + u * w * 2.2f / d
                val y = baseY - amp * h * 0.42f / d
                if (b == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            val fill = Path().apply { addPath(path); lineTo(w / 2 + 0.5f * w * 2.2f / d, h); lineTo(w / 2 - 0.5f * w * 2.2f / d, h); close() }
            drawPath(fill, Color.Black)
            val k = r / (hist.rows - 1f)
            drawPath(path, lerp(near, far, k).withA(1f - k * 0.8f), style = Stroke(if (r == 0) 3f else 1.6f))
        }
    }
}

private class CanyonHistory(val rows: Int, val bands: Int) {
    private val data = Array(rows) { FloatArray(bands) }
    private var head = 0
    var lastPush = 0f
    fun push(f: AudioFrame) {
        head = (head - 1).mod(rows)
        val dst = data[head]
        for (b in 0 until bands) {
            val i0 = b * AudioFrame.BANDS / bands
            dst[b] = f.bands[i0]
        }
    }
    fun row(age: Int): FloatArray = data[(head + age) % rows]
}

/** NEON_WORM: a wandering polygon tunnel; rings pulse with the bass. */
@Composable
fun TunnelViz(theme: VaporTheme) {
    val time = rememberFrameTime()
    val travel = remember { FloatArray(1) }
    Canvas(vizBg) {
        val t = time.value
        val f = AudioBus.currentFrame()
        travel[0] += 0.012f * (1f + f.bass * 2.5f + f.mid)
        val n = 26
        val c1 = argb(theme.primary); val c2 = argb(theme.secondary)
        val base = min(size.width, size.height) * 0.5f
        for (i in n - 1 downTo 0) {
            val z = ((i - travel[0] * n) % n + n) % n / n // 0 near .. 1 far
            val zz = 0.06f + z
            val r = base * 0.22f / zz * (1f + f.bass * 0.25f * (1 - z))
            if (r > size.maxDimension * 1.5f) continue
            val wander = (1f - z)
            val cx = size.width / 2 + sin(t * 0.7f + z * 4f) * size.width * 0.18f * (1 - wander * 0.6f)
            val cy = size.height / 2 + cos(t * 0.5f + z * 3f) * size.height * 0.14f * (1 - wander * 0.6f)
            val sides = 6
            val rotA = t * 0.4f + z * 2.5f
            val path = Path()
            for (s in 0..sides) {
                val a = rotA + s * 2f * PI.toFloat() / sides
                val x = cx + cos(a) * r; val y = cy + sin(a) * r
                if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            val col = if ((i + (travel[0] * n).toInt()) % 2 == 0) c1 else c2
            val alpha = (1f - z) * 0.9f
            drawPath(path, col.withA(alpha * 0.25f), style = Stroke(10f * (1 - z) + 2f))
            drawPath(path, col.withA(alpha), style = Stroke(2.5f * (1 - z) + 1f))
        }
        rotate(t * 10f) { drawCircle(c2.withA(0.12f + f.treble * 0.3f), base * 0.06f, center) }
    }
}
