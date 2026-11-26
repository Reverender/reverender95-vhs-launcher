package com.reverender.r95.ui.vhs

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import com.reverender.r95.os.OsPrefs
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/**
 * Animated 3D backgrounds for the VHS menu: neon wireframes with glow, star fields, haze and reflections,
 * like the computer graphics on 80s tapes.
 *
 * Each scene has three parts: a still back (sky, sun, mountains, skyline …) and a still front, both recorded
 * once into GPU layers and only redrawn when the screen size changes, and the moving middle drawn every frame.
 * The CRT motion blur (BGBLUR) repeats only the moving middle, a moment earlier and faint.
 */
object VhsWalls {
    val names = listOf(
        "SUNSET DRIVE '86", "WIREFRAME VALLEY", "CHROME TUNNEL", "HYPERSPACE TAPE", "CUBE DREAMS",
        "PALM HIGHWAY", "OCEAN GRID", "NEON CITY", "SYNTH PLANET", "LASER DISCO",
    )
}

private val PINK = Color(0xFFFF2BD6)
private val HOT = Color(0xFFFF4F9A)
private val CYAN = Color(0xFF29F3FF)
private val VIOLET = Color(0xFF9B5BFF)
private val SUN1 = Color(0xFFFFE860)
private val SUN2 = Color(0xFFFF2E88)
private val NIGHT = Color(0xFF07021A)
private val DUSK = Color(0xFF34085C)
private val LIME = Color(0xFF7CFF4F)

@Composable
fun VhsWallpaper(index: Int, modifier: Modifier) {
    val t = remember { mutableLongStateOf(0L) }
    LaunchedEffect(index) {
        var last = 0L
        while (true) {
            com.reverender.r95.core.AppVisibility.awaitForeground()
            if (VhsUi.drawerOpen) { kotlinx.coroutines.delay(150); continue } // covered by the apps drawer
            withFrameNanos { n -> if (n - last >= 15_000_000L) { last = n; t.longValue = n } } // ~60 fps
        }
    }
    val backLayer = rememberGraphicsLayer()
    val frontLayer = rememberGraphicsLayer()
    val key = remember { longArrayOf(-1L) }
    val ghostPaint = remember { Paint() }
    val scene = SCENES[index.coerceIn(0, SCENES.size - 1)]
    Canvas(modifier) {
        // the still parts: recorded once per size / scene
        val k = index * 1_000_000_000L + size.width.toLong() * 20_000L + size.height.toLong()
        if (key[0] != k) {
            key[0] = k
            backLayer.record { with(scene) { back() } }
            frontLayer.record { with(scene) { front() } }
        }
        // seconds within the hour (a Float of the full uptime gets coarse after days: jerky motion)
        val s = (t.longValue % 3_600_000_000_000L) / 1e9f
        drawLayer(backLayer)
        val blur = OsPrefs.vhsBlur
        if (blur >= 0.02f) {
            val dt = 0.018f + 0.03f * blur
            val ghosts = 1 // one faint copy: most of the look, a fraction of the cost
            for (g in ghosts downTo 1) {
                ghostPaint.alpha = (0.22f + 0.28f * blur) / g
                drawContext.canvas.saveLayer(Rect(Offset.Zero, size), ghostPaint)
                with(scene) { move(s - dt * g) }
                drawContext.canvas.restore()
            }
        }
        with(scene) { move(s) }
        drawLayer(frontLayer)
    }
}

private interface Scene {
    fun DrawScope.back()
    fun DrawScope.move(s: Float)
    fun DrawScope.front() {}
}

private val SCENES: List<Scene> = listOf(SunsetDrive, WireValley, ChromeTunnel, Hyperspace, CubeDreams, PalmHighway, OceanGrid, NeonCity, SynthPlanet, LaserDisco)

// ---------------------------------------------------------------- helpers

/** a neon line: a soft halo, a glow and a bright core */
private fun DrawScope.neon(col: Color, a: Offset, b: Offset, w: Float, alpha: Float = 1f) {
    if (alpha <= 0.01f) return
    drawLine(col.copy(alpha = 0.12f * alpha), a, b, w * 5f, StrokeCap.Round, blendMode = BlendMode.Plus)
    drawLine(col.copy(alpha = 0.34f * alpha), a, b, w * 2.4f, StrokeCap.Round, blendMode = BlendMode.Plus)
    drawLine(lighten(col).copy(alpha = alpha), a, b, w, StrokeCap.Round)
}
private fun DrawScope.neonXY(col: Color, x1: Float, y1: Float, x2: Float, y2: Float, w: Float, alpha: Float = 1f) = neon(col, Offset(x1, y1), Offset(x2, y2), w, alpha)
private fun lighten(c: Color) = Color(c.red + (1 - c.red) * 0.3f, c.green + (1 - c.green) * 0.3f, c.blue + (1 - c.blue) * 0.3f)
private fun DrawScope.neonPath(p: Path, col: Color, w: Float, alpha: Float = 1f) {
    if (alpha <= 0.01f) return
    drawPath(p, col.copy(alpha = 0.12f * alpha), style = Stroke(w * 5f), blendMode = BlendMode.Plus)
    drawPath(p, col.copy(alpha = 0.34f * alpha), style = Stroke(w * 2.4f), blendMode = BlendMode.Plus)
    drawPath(p, lighten(col).copy(alpha = alpha), style = Stroke(w))
}
private fun DrawScope.glow(c: Offset, r: Float, col: Color, a: Float) =
    drawCircle(Brush.radialGradient(listOf(col.copy(alpha = a), col.copy(alpha = a * 0.3f), Color.Transparent), c, r), r, c, blendMode = BlendMode.Plus)

private fun rnd(seed: Long) = java.util.Random(seed)
private val starField = List(160) { i -> val r = rnd(i * 104729L); floatArrayOf(r.nextFloat(), r.nextFloat(), r.nextFloat(), r.nextFloat() * 6.28f) }

/** sky gradient with fixed dim stars (still part) */
private fun DrawScope.sky(top: Color, bottom: Color, horizon: Float, stars: Boolean = true) {
    drawRect(Brush.verticalGradient(listOf(top, bottom), 0f, horizon), size = Size(size.width, horizon))
    if (stars) for (st in starField) {
        val y = st[1] * horizon * 0.9f
        drawCircle(Color.White.copy(alpha = (0.15f + 0.35f * st[2]) * (1f - y / horizon)), 0.5f + st[2] * 1.1f, Offset(st[0] * size.width, y))
    }
}
/** the brightest stars twinkle (moving part) */
private fun DrawScope.twinkle(horizon: Float, s: Float, count: Int = 30) {
    for (j in 0 until count) {
        val st = starField[j * 5 % starField.size]
        val y = st[1] * horizon * 0.9f
        val tw = 0.5f + 0.5f * sin(s * (1.5f + st[2] * 3f) + st[3])
        val p = Offset(st[0] * size.width, y)
        drawCircle(Color.White.copy(alpha = 0.7f * tw * (1f - y / horizon)), 1.2f + st[2], p)
        if (tw > 0.85f) {
            val l = 4f + 6f * st[2]
            drawLine(Color.White.copy(alpha = 0.35f * tw), Offset(p.x - l, p.y), Offset(p.x + l, p.y), 1f)
            drawLine(Color.White.copy(alpha = 0.35f * tw), Offset(p.x, p.y - l), Offset(p.x, p.y + l), 1f)
        }
    }
}
/** a shooting star now and then */
private fun DrawScope.shooting(s: Float, horizon: Float, period: Float = 7f) {
    val cyc = floor(s / period); val ph = s / period - cyc
    if (ph > 0.12f) return
    val r = rnd(cyc.toLong() * 31L)
    val k = ph / 0.12f
    val x0 = size.width * (0.1f + 0.8f * r.nextFloat()); val y0 = horizon * (0.1f + 0.4f * r.nextFloat())
    val dx = size.width * 0.35f; val dy = horizon * 0.18f
    val head = Offset(x0 + dx * k, y0 + dy * k)
    neon(Color.White, Offset(head.x - dx * 0.25f, head.y - dy * 0.25f), head, 1.2f, 1f - k)
}

private fun DrawScope.haze(horizon: Float, col: Color, h: Float) {
    drawRect(Brush.verticalGradient(listOf(Color.Transparent, col.copy(alpha = 0.55f), Color.Transparent), horizon - h, horizon + h * 0.8f), Offset(0f, horizon - h), Size(size.width, h * 1.8f), blendMode = BlendMode.Plus)
}

/** the striped 80s sun with a glow, cut off at the horizon */
private fun DrawScope.sun(cx: Float, cy: Float, r: Float, clipY: Float) {
    glow(Offset(cx, cy), r * 2.6f, SUN2, 0.55f)
    drawContext.canvas.save()
    drawContext.canvas.clipRect(0f, 0f, size.width, clipY)
    drawCircle(Brush.verticalGradient(listOf(SUN1, Color(0xFFFF8A3C), SUN2), cy - r, cy + r), r, Offset(cx, cy))
    for (k in 0 until 9) {
        val z = (k + 0.5f) / 9f
        drawRect(NIGHT.copy(alpha = 0.92f), Offset(cx - r, cy + r * (0.05f + 0.95f * z)), Size(r * 2, r * (0.015f + 0.075f * z)))
    }
    drawContext.canvas.restore()
}

private fun DrawScope.mountains(horizon: Float, seed: Int, col: Color, height: Float, layers: Int = 3) {
    for (layer in layers - 1 downTo 0) {
        val p = Path(); p.moveTo(0f, horizon)
        val n = 14 + layer * 7
        val hh = height * (1f - layer * 0.28f)
        for (i in 0..n) {
            val x = size.width * i / n
            val v = abs(sin(i * 1.7f + seed + layer * 3.1f)) * (0.55f + 0.45f * abs(cos(i * 0.9f + seed - layer)))
            p.lineTo(x, horizon - hh * (0.25f + 0.75f * v))
        }
        p.lineTo(size.width, horizon); p.close()
        drawPath(p, Brush.verticalGradient(listOf(Color(0xFF1A0638), NIGHT), horizon - hh, horizon))
        neonPath(p, when (layer) { 0 -> col; 1 -> VIOLET; else -> DUSK }, 1.2f, 1f - layer * 0.3f)
    }
}

/** the floor below the horizon with a soft glow along it (still part) */
private fun DrawScope.floor(horizon: Float, fill: Color, glowCol: Color) {
    drawRect(fill, Offset(0f, horizon), Size(size.width, size.height - horizon))
    drawRect(Brush.verticalGradient(listOf(glowCol.copy(alpha = 0.22f), Color.Transparent), horizon, horizon + (size.height - horizon) * 0.5f), Offset(0f, horizon), Size(size.width, size.height - horizon), blendMode = BlendMode.Plus)
}

/** perspective floor grid scrolling towards the viewer */
private fun DrawScope.grid(s: Float, horizon: Float, col: Color, speed: Float = 0.35f, lanes: Int = 16, col2: Color = col, rows: Int = 18) {
    val w = size.width; val h = size.height; val cx = w / 2
    for (i in -lanes..lanes) {
        val x = cx + i * w / lanes * 2.4f
        neonXY(col2, cx + (x - cx) * 0.015f, horizon, x, h, 1.0f, 0.55f)
    }
    val ph = (s * speed) % 1f
    for (k in 0 until rows) {
        val z = (k + 1 - ph) / rows
        val y = horizon + (h - horizon) * z * z
        neonXY(col, 0f, y, w, y, 0.6f + 1.6f * z, 0.2f + 0.8f * z)
    }
    neonXY(col, 0f, horizon, w, horizon, 1.6f)
}

/** 3D → screen: rotate about y then x, simple perspective. Writes into [out] (x, y pairs), no allocation. */
private fun project(pts: FloatArray, ax: Float, ay: Float, cx: Float, cy: Float, scale: Float, out: FloatArray) {
    val ca = cos(ay); val sa = sin(ay); val cb = cos(ax); val sb = sin(ax)
    var j = 0
    while (j < pts.size / 3) {
        val x = pts[j * 3]; val y = pts[j * 3 + 1]; val z = pts[j * 3 + 2]
        val x1 = x * ca + z * sa; val z1 = -x * sa + z * ca
        val y1 = y * cb - z1 * sb; val z2 = y * sb + z1 * cb
        val f = 4f / (z2 + 5f)
        out[j * 2] = cx + x1 * f * scale; out[j * 2 + 1] = cy + y1 * f * scale
        j++
    }
}
private fun DrawScope.wire(pts: FloatArray, edges: IntArray, ax: Float, ay: Float, cx: Float, cy: Float, scale: Float, col: Color, w: Float, alpha: Float = 1f) {
    val out = scratch
    project(pts, ax, ay, cx, cy, scale, out)
    var e = 0
    while (e < edges.size) {
        val a = edges[e]; val b = edges[e + 1]
        neonXY(col, out[a * 2], out[a * 2 + 1], out[b * 2], out[b * 2 + 1], w, alpha)
        e += 2
    }
    for (j in 0 until pts.size / 3) drawCircle(Color.White.copy(alpha = 0.8f * alpha), w * 1.3f, Offset(out[j * 2], out[j * 2 + 1]))
}
private val scratch = FloatArray(256)

private val CUBE = floatArrayOf(-1f, -1f, -1f, 1f, -1f, -1f, 1f, 1f, -1f, -1f, 1f, -1f, -1f, -1f, 1f, 1f, -1f, 1f, 1f, 1f, 1f, -1f, 1f, 1f)
private val CUBE_E = intArrayOf(0, 1, 1, 2, 2, 3, 3, 0, 4, 5, 5, 6, 6, 7, 7, 4, 0, 4, 1, 5, 2, 6, 3, 7)
private val OCTA = floatArrayOf(0f, -1.5f, 0f, 0f, 1.5f, 0f, -1.5f, 0f, 0f, 1.5f, 0f, 0f, 0f, 0f, -1.5f, 0f, 0f, 1.5f)
private val OCTA_E = intArrayOf(0, 2, 0, 3, 0, 4, 0, 5, 1, 2, 1, 3, 1, 4, 1, 5, 2, 4, 4, 3, 3, 5, 5, 2)
private val PYRA = floatArrayOf(0f, -1.3f, 0f, -1f, 0.8f, -1f, 1f, 0.8f, -1f, 1f, 0.8f, 1f, -1f, 0.8f, 1f)
private val PYRA_E = intArrayOf(0, 1, 0, 2, 0, 3, 0, 4, 1, 2, 2, 3, 3, 4, 4, 1)
/** icosahedron (12 corners, 30 edges) */
private val ICO: FloatArray = run {
    val g = 1.618f
    floatArrayOf(-1f, g, 0f, 1f, g, 0f, -1f, -g, 0f, 1f, -g, 0f, 0f, -1f, g, 0f, 1f, g, 0f, -1f, -g, 0f, 1f, -g, g, 0f, -1f, g, 0f, 1f, -g, 0f, -1f, -g, 0f, 1f)
        .map { it * 0.85f }.toFloatArray()
}
private val ICO_E: IntArray = run {
    val l = ArrayList<Int>()
    for (a in 0 until 12) for (b in a + 1 until 12) {
        val dx = ICO[a * 3] - ICO[b * 3]; val dy = ICO[a * 3 + 1] - ICO[b * 3 + 1]; val dz = ICO[a * 3 + 2] - ICO[b * 3 + 2]
        if (abs(dx * dx + dy * dy + dz * dz - 4f * 0.85f * 0.85f) < 0.1f) { l.add(a); l.add(b) }
    }
    l.toIntArray()
}

// ---------------------------------------------------------------- scenes

/** 0: the classic — striped sun behind three ranges of neon mountains, a pink grid, shooting stars */
private object SunsetDrive : Scene {
    override fun DrawScope.back() {
        val hz = size.height * 0.56f
        sky(NIGHT, Color(0xFF5A0F6E), hz)
        sun(size.width / 2, hz - size.width * 0.13f, size.width * 0.3f, hz)
        mountains(hz, 3, CYAN, size.height * 0.12f)
        haze(hz, HOT, size.height * 0.05f)
        floor(hz, NIGHT, PINK)
        drawRect(Brush.verticalGradient(listOf(SUN2.copy(alpha = 0.35f), Color.Transparent), hz, hz + size.height * 0.25f), Offset(size.width * 0.3f, hz), Size(size.width * 0.4f, size.height * 0.25f), blendMode = BlendMode.Plus)
    }
    override fun DrawScope.move(s: Float) {
        val hz = size.height * 0.56f
        twinkle(hz, s)
        shooting(s, hz)
        grid(s, hz, PINK, col2 = Color(0xFFB04BFF))
    }
}

/** 1: flying low over a wire-mesh landscape under the moon, with an aurora (batched: a dozen draws a frame) */
private object WireValley : Scene {
    private const val ROWS = 18; private const val COLS = 20; private const val STRIDE = COLS + 1
    private val pts = FloatArray((ROWS + 1) * STRIDE * 2)
    private const val BANDS = 6
    private val rowPaths = Array(BANDS) { Path() }
    private val crossPaths = Array(BANDS) { Path() }
    override fun DrawScope.back() {
        val hz = size.height * 0.36f
        sky(NIGHT, Color(0xFF1E0A4A), hz)
        for (b in 0 until 3) {
            val p = Path()
            val y0 = hz * (0.25f + b * 0.12f)
            p.moveTo(0f, y0)
            for (i in 0..20) { val x = size.width * i / 20f; p.lineTo(x, y0 + sin(i * 0.7f + b * 2f) * hz * 0.06f) }
            drawPath(p, (if (b == 1) LIME else CYAN).copy(alpha = 0.10f), style = Stroke(hz * 0.09f), blendMode = BlendMode.Plus)
        }
        val m = Offset(size.width * 0.78f, hz * 0.35f)
        glow(m, size.width * 0.22f, Color(0xFFBFE9FF), 0.5f)
        drawCircle(Color(0xFFE6F7FF), size.width * 0.055f, m)
        drawCircle(Color(0xFFB9CFE0).copy(alpha = 0.5f), size.width * 0.012f, m + Offset(size.width * 0.015f, -size.width * 0.01f))
        haze(hz, VIOLET, size.height * 0.04f)
        floor(hz, NIGHT, VIOLET)
    }
    override fun DrawScope.move(s: Float) {
        val hz = size.height * 0.36f
        twinkle(hz, s, 16)
        val travel = s * 1.1f
        val ft = floor(travel); val fr = travel - ft
        for (r in ROWS downTo 1) {
            val z = (r - fr) / ROWS
            val y0 = hz + (size.height - hz) * (1f - z) * (1f - z)
            val spread = size.width * (1.9f - 1.5f * z)
            val wz = r + ft
            val a1 = wz * 0.55f; val a2 = wz * 0.31f
            val amp = size.height * 0.09f * (1.25f - z)
            for (c in 0..COLS) {
                val u = c / COLS.toFloat() - 0.5f
                val hgt = (sin(u * 7f + a1) * cos(u * 3f - a2) + 0.45f * sin(u * 17f + wz)) * amp
                val o = (r * STRIDE + c) * 2
                pts[o] = size.width / 2 + u * spread
                pts[o + 1] = y0 - abs(hgt) * (1.0f + abs(u) * 2.2f)
            }
        }
        for (b in 0 until BANDS) { rowPaths[b].reset(); crossPaths[b].reset() }
        for (r in ROWS downTo 1) {
            val z = (r - fr) / ROWS
            val b = ((1f - z) * BANDS).toInt().coerceIn(0, BANDS - 1)
            val rp = rowPaths[b]
            for (c in 0..COLS) { val o = (r * STRIDE + c) * 2; if (c == 0) rp.moveTo(pts[o], pts[o + 1]) else rp.lineTo(pts[o], pts[o + 1]) }
            if (r > 1) {
                val cp = crossPaths[b]
                for (c in 0..COLS step 2) { val o = (r * STRIDE + c) * 2; val o2 = ((r - 1) * STRIDE + c) * 2; cp.moveTo(pts[o], pts[o + 1]); cp.lineTo(pts[o2], pts[o2 + 1]) }
            }
        }
        for (b in 0 until BANDS) {
            val near = (b + 0.5f) / BANDS                      // 0 far … 1 near
            val col = if (near > 0.66f) PINK else if (near > 0.33f) VIOLET else CYAN
            val a = (0.2f + 0.8f * near).coerceIn(0f, 1f)
            val w = 0.7f + 1.4f * near
            neonPath(rowPaths[b], col, w, a)
            drawPath(crossPaths[b], col.copy(alpha = 0.45f * a), style = Stroke(w * 0.8f))
        }
        neonXY(CYAN, 0f, hz, size.width, hz, 1.5f)
    }
}

/** 2: down an octagonal chrome tunnel, light pulses racing along its rails */
private object ChromeTunnel : Scene {
    private const val N = 22; private const val SIDES = 10
    private val ring = FloatArray((N + 1) * SIDES * 2)
    private val path = Path()
    override fun DrawScope.back() { drawRect(NIGHT) }
    override fun DrawScope.move(s: Float) {
        val cx = size.width / 2 + sin(s * 0.7f) * size.width * 0.07f
        val cy = size.height / 2 + cos(s * 0.5f) * size.height * 0.05f
        glow(Offset(cx, cy), size.minDimension * 0.28f, CYAN, 0.45f)
        drawCircle(Color.White.copy(alpha = 0.7f), size.minDimension * 0.02f, Offset(cx, cy))
        val travel = s * 0.45f
        val ph = travel % 1f
        val base = floor(travel).toInt()
        for (k in N downTo 0) {
            val z = (k - ph + 1f) / (N + 1f)
            val r = size.maxDimension * (0.012f + 0.95f * (1f - z) * (1f - z) * (1f - z))
            val id = k + base
            val rot = s * 0.25f + id * 0.16f
            path.reset()
            for (i in 0 until SIDES) {
                val a = rot + i * 2 * PI.toFloat() / SIDES
                val x = cx + cos(a) * r; val y = cy + sin(a) * r * 0.9f
                ring[(k * SIDES + i) * 2] = x; ring[(k * SIDES + i) * 2 + 1] = y
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            val fade = ((1f - z) * 6f).coerceIn(0f, 1f) * (z * 8f).coerceIn(0f, 1f)
            neonPath(path, if (id % 2 == 0) CYAN else PINK, 0.8f + 2.4f * (1f - z), fade)
        }
        // rails between the rings, every other corner
        for (k in 0 until N) for (i in 0 until SIDES step 2) {
            val z = (k - ph + 1f) / (N + 1f)
            val a = ((1f - z) * 0.8f).coerceIn(0f, 1f) * (z * 6f).coerceIn(0f, 1f)
            neonXY(VIOLET, ring[(k * SIDES + i) * 2], ring[(k * SIDES + i) * 2 + 1], ring[((k + 1) * SIDES + i) * 2], ring[((k + 1) * SIDES + i) * 2 + 1], 0.8f, a)
        }
        // light pulses running towards the viewer on four rails
        for (p in 0 until 4) {
            val q = ((s * 0.6f + p * 0.25f) % 1f)
            val kf = (1f - q) * N
            val k = kf.toInt().coerceIn(0, N - 1); val f = kf - k
            val i = (p * 2 + 1) % SIDES * 1
            val i2 = (i / 2) * 2
            val x = ring[(k * SIDES + i2) * 2] * (1 - f) + ring[((k + 1) * SIDES + i2) * 2] * f
            val y = ring[(k * SIDES + i2) * 2 + 1] * (1 - f) + ring[((k + 1) * SIDES + i2) * 2 + 1] * f
            glow(Offset(x, y), 6f + 30f * q, Color.White, 0.6f * q)
        }
    }
}

/** 3: jump to light speed — streaking stars, nebulae and warp rings */
private object Hyperspace : Scene {
    private val stars = List(240) { i -> val r = rnd(i * 7919L); floatArrayOf(r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1, r.nextFloat(), r.nextFloat()) }
    override fun DrawScope.back() {
        drawRect(NIGHT)
        val cols = listOf(DUSK, Color(0xFF0B3A6E), Color(0xFF5A0F4E))
        for (k in 0 until 3) glow(Offset(size.width * (0.25f + 0.25f * k), size.height * (0.3f + 0.2f * ((k * 7) % 3))), size.maxDimension * 0.5f, cols[k], 0.8f)
        for (st in starField) drawCircle(Color.White.copy(alpha = 0.25f * st[2]), 0.6f, Offset(st[0] * size.width, st[1] * size.height))
    }
    override fun DrawScope.move(s: Float) {
        val cx = size.width / 2 + sin(s * 0.3f) * size.width * 0.04f
        val cy = size.height / 2 + cos(s * 0.23f) * size.height * 0.03f
        // warp rings
        for (w in 0 until 3) {
            val q = (s * 0.35f + w / 3f) % 1f
            val r = size.maxDimension * q * q * 0.9f
            drawCircle(CYAN.copy(alpha = 0.18f * (1f - q)), r, Offset(cx, cy), style = Stroke(2f + 10f * q), blendMode = BlendMode.Plus)
        }
        for (st in stars) {
            val z = 1f - ((st[2] + s * 0.28f) % 1f)
            val z2 = (z + 0.05f + 0.06f * (1f - z)).coerceAtMost(1f)
            val k = 0.12f
            val col = when { st[3] < 0.4f -> CYAN; st[3] < 0.75f -> PINK; else -> Color.White }
            neonXY(col, cx + st[0] / (z2 + k) * size.width * 0.11f, cy + st[1] / (z2 + k) * size.height * 0.11f,
                cx + st[0] / (z + k) * size.width * 0.11f, cy + st[1] / (z + k) * size.height * 0.11f, 0.6f + 2.2f * (1f - z), (1f - z).coerceIn(0.1f, 1f))
        }
        glow(Offset(cx, cy), size.minDimension * 0.09f, Color.White, 0.55f)
    }
}

/** 4: wire solids spinning over a grid — a cube around an icosahedron, an orbit of pyramids, reflections */
private object CubeDreams : Scene {
    override fun DrawScope.back() {
        val hz = size.height * 0.64f
        sky(NIGHT, Color(0xFF240A55), hz)
        haze(hz, CYAN, size.height * 0.04f)
        floor(hz, NIGHT, PINK)
    }
    override fun DrawScope.move(s: Float) {
        val hz = size.height * 0.64f
        twinkle(hz, s, 20)
        grid(s, hz, PINK, 0.18f, col2 = VIOLET)
        val sc = size.minDimension * 0.24f
        val cy = size.height * 0.36f + sin(s * 1.1f) * size.height * 0.02f
        val cx = size.width * 0.5f
        val ry = hz + (hz - cy) * 0.55f
        // reflections first (dim)
        wire(CUBE, CUBE_E, -s * 0.7f, s * 0.9f, cx, ry, sc * 0.7f, CYAN, 1.1f, 0.16f)
        wire(ICO, ICO_E, s * 0.9f, -s * 0.6f, cx, ry, sc * 0.33f, PINK, 0.9f, 0.14f)
        // orbit ring of dots
        for (j in 0 until 24) {
            val a = s * 0.6f + j * 2 * PI.toFloat() / 24
            val d = sin(a)
            drawCircle(CYAN.copy(alpha = 0.35f + 0.5f * (d + 1f) / 2f), 1.5f + 1.5f * (d + 1f) / 2f, Offset(cx + cos(a) * size.width * 0.38f, cy + d * size.height * 0.05f))
        }
        wire(CUBE, CUBE_E, s * 0.7f, s * 0.9f, cx, cy, sc, CYAN, 2.0f)
        wire(ICO, ICO_E, -s * 1.1f, s * 0.6f, cx, cy, sc * 0.48f, PINK, 1.4f)
        for (p in 0 until 3) {
            val a = s * 0.8f + p * 2.09f
            wire(PYRA, PYRA_E, s * 1.3f + p, -s, cx + cos(a) * size.width * 0.38f, cy + sin(a) * size.height * 0.05f, sc * 0.22f, listOf(SUN1, HOT, LIME)[p], 1.1f, 0.6f + 0.4f * (sin(a) + 1f) / 2f)
        }
    }
}

/** 5: cruising a palm-lined highway into the sunset city, tail-lights ahead */
private object PalmHighway : Scene {
    private val skyline = List(30) { i -> val r = rnd(i * 31L); floatArrayOf(r.nextFloat(), 0.3f + r.nextFloat() * 0.7f, r.nextFloat()) }
    private val trunk = Path(); private val frond = Path()
    override fun DrawScope.back() {
        val hz = size.height * 0.5f
        sky(Color(0xFF0D0328), Color(0xFFC02E6E), hz)
        sun(size.width * 0.5f, hz - size.width * 0.06f, size.width * 0.22f, hz)
        val bw = size.width / skyline.size
        skyline.forEachIndexed { i, b ->
            val h = size.height * 0.1f * b[1]
            drawRect(Color(0xFF12052A), Offset(i * bw, hz - h), Size(bw * 0.92f, h))
            drawRect(CYAN.copy(alpha = 0.5f), Offset(i * bw, hz - h), Size(bw * 0.92f, 1f))
            val wr = rnd(i * 97L)
            var y = hz - h + 3f
            while (y < hz - 4f) { if (wr.nextFloat() < 0.35f) drawRect(SUN1.copy(alpha = 0.7f), Offset(i * bw + bw * 0.3f, y), Size(bw * 0.25f, 2f)); y += 6f }
        }
        haze(hz, HOT, size.height * 0.035f)
        val w = size.width; val h = size.height; val cx = w / 2
        drawRect(NIGHT, Offset(0f, hz), Size(w, h - hz))
        val road = Path(); road.moveTo(cx - w * 0.02f, hz); road.lineTo(cx + w * 0.02f, hz); road.lineTo(cx + w * 0.95f, h); road.lineTo(cx - w * 0.95f, h); road.close()
        drawPath(road, Brush.verticalGradient(listOf(Color(0xFF2A0D45), Color(0xFF0E0420)), hz, h))
        neon(CYAN, Offset(cx - w * 0.02f, hz), Offset(cx - w * 0.95f, h), 2.2f)
        neon(CYAN, Offset(cx + w * 0.02f, hz), Offset(cx + w * 0.95f, h), 2.2f)
    }
    private fun DrawScope.palm(x: Float, base: Float, h: Float, col: Color, sway: Float) {
        val top = Offset(x + h * 0.12f + sway * h * 0.03f, base - h)
        trunk.reset()
        trunk.moveTo(x - h * 0.03f, base); trunk.quadraticBezierTo(x + h * 0.02f, base - h * 0.5f, top.x, top.y); trunk.lineTo(top.x + h * 0.02f, top.y); trunk.quadraticBezierTo(x + h * 0.06f, base - h * 0.5f, x + h * 0.03f, base); trunk.close()
        drawPath(trunk, col)
        for (i in 0 until 7) {
            val a = -PI.toFloat() * (0.02f + i * 0.16f) + sway * 0.05f
            val len = h * (0.4f + 0.1f * ((i % 3) / 2f))
            val end = Offset(top.x + cos(a) * len, top.y - sin(a) * len * 0.4f + len * 0.3f)
            frond.reset(); frond.moveTo(top.x, top.y); frond.quadraticBezierTo((top.x + end.x) / 2, top.y - h * 0.16f, end.x, end.y)
            drawPath(frond, col, style = Stroke(h * 0.045f, cap = StrokeCap.Round))
        }
    }
    override fun DrawScope.move(s: Float) {
        val hz = size.height * 0.5f
        val w = size.width; val h = size.height; val cx = w / 2
        twinkle(hz * 0.6f, s, 16)
        val ph = (s * 0.9f) % 1f
        for (k in 0 until 12) {
            val z = (k + ph) / 12f; val z2 = (k + ph + 0.45f) / 12f
            neonXY(SUN1, cx, hz + (h - hz) * z * z, cx, hz + (h - hz) * z2 * z2, 0.8f + 5f * z, (0.4f + 0.6f * z).coerceIn(0f, 1f))
        }
        // tail-lights of a car far ahead, weaving gently
        val cz = 0.18f + 0.04f * sin(s * 0.3f)
        val cy = hz + (h - hz) * cz * cz
        val ccx = cx + sin(s * 0.5f) * w * 0.05f * cz
        for (side in listOf(-1f, 1f)) glow(Offset(ccx + side * w * 0.025f * cz * 3f, cy), 6f, Color(0xFFFF2030), 0.9f)
        for (k in 0 until 6) {
            val z = ((k / 6f) + s * 0.11f) % 1f
            val zz = z * z
            val y = hz + (h - hz) * zz
            val off = 0.08f + 0.95f * zz
            val sw = sin(s * 1.3f + k)
            palm(cx - w * off - w * 0.04f, y, h * (0.04f + 0.55f * zz), Color.Black, sw)
            palm(cx + w * off + w * 0.04f, y, h * (0.04f + 0.55f * zz), Color.Black, -sw)
        }
    }
}

/** 6: a calm neon ocean under a setting sun — rolling wire waves and glitter */
private object OceanGrid : Scene {
    private val path = Path()
    override fun DrawScope.back() {
        val hz = size.height * 0.45f
        sky(Color(0xFF020824), Color(0xFF0F4F7A), hz)
        // thin cloud bands across the sun
        sun(size.width * 0.5f, hz, size.width * 0.2f, hz)
        for (b in 0 until 4) {
            val y = hz * (0.5f + b * 0.1f)
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF2A0F50).copy(alpha = 0.7f), Color.Transparent), size.width * 0.05f * b, size.width * (0.7f + 0.08f * b)), Offset(0f, y), Size(size.width, size.height * 0.008f))
        }
        haze(hz, CYAN, size.height * 0.04f)
        drawRect(Color(0xFF020A1E), Offset(0f, hz), Size(size.width, size.height - hz))
    }
    override fun DrawScope.move(s: Float) {
        val hz = size.height * 0.45f
        twinkle(hz * 0.7f, s, 18)
        val gr = rnd((s * 8).toLong())
        for (k in 0 until 40) {
            val z = gr.nextFloat()
            val y = hz + (size.height - hz) * z * z
            val half = size.width * (0.03f + 0.18f * z) * gr.nextFloat()
            drawLine(SUN1.copy(alpha = 0.25f + 0.5f * (1f - z)), Offset(size.width / 2 - half, y), Offset(size.width / 2 + half, y), 1f + 2f * z, blendMode = BlendMode.Plus)
        }
        val rows = 22; val cols = 30
        for (r in 0 until rows) {
            val z = (r + 1) / rows.toFloat()
            val y = hz + (size.height - hz) * z * z
            path.reset()
            for (c in 0..cols) {
                val u = c / cols.toFloat()
                val wave = (sin(u * 12f + s * 1.6f + r * 0.7f) * cos(u * 5f - s) + 0.35f * sin(u * 31f - s * 2.3f + r)) * (3f + 20f * z)
                if (c == 0) path.moveTo(u * size.width, y + wave) else path.lineTo(u * size.width, y + wave)
            }
            neonPath(path, if (r % 3 == 0) PINK else CYAN, 0.6f + 1.6f * z, (0.2f + 0.8f * z).coerceIn(0f, 1f))
        }
    }
}

/** 7: gliding down an avenue of neon towers — lit windows, billboards, searchlights, flying cars */
private object NeonCity : Scene {
    private val towers = List(24) { i -> val r = rnd(i * 613L); floatArrayOf(0.35f + r.nextFloat() * 0.75f, 0.5f + r.nextFloat() * 0.6f, r.nextFloat(), r.nextFloat()) }
    private val pinkP = Path(); private val cyanP = Path(); private val winP = Path(); private val sideP = Path()
    override fun DrawScope.back() {
        val hz = size.height * 0.48f
        sky(Color(0xFF02020F), Color(0xFF2A0F50), hz)
        // far skyline with thousands of tiny windows (drawn once)
        val p = Path(); p.moveTo(0f, hz)
        var x = 0f; var i = 0
        while (x < size.width) {
            val r = rnd(i++ * 17L)
            val bw = size.width * (0.02f + 0.04f * r.nextFloat()); val bh = size.height * (0.03f + 0.10f * r.nextFloat())
            p.lineTo(x, hz - bh); p.lineTo(x + bw, hz - bh)
            drawRect(Color(0xFF0C0624), Offset(x, hz - bh), Size(bw, bh))
            var wy = hz - bh + 3f
            while (wy < hz - 3f) { var wx = x + 2f; while (wx < x + bw - 2f) { if (r.nextFloat() < 0.3f) drawRect(SUN1.copy(alpha = 0.55f), Offset(wx, wy), Size(1.5f, 1.5f)); wx += 4f }; wy += 4f }
            if (r.nextFloat() < 0.15f) drawLine(Color(0xFFFF2030), Offset(x + bw / 2, hz - bh), Offset(x + bw / 2, hz - bh - 8f), 1f)
            x += bw
        }
        p.lineTo(size.width, hz); p.close()
        neonPath(p, VIOLET, 0.8f, 0.5f)
        haze(hz, PINK, size.height * 0.05f)
        floor(hz, NIGHT, CYAN)
    }
    override fun DrawScope.move(s: Float) {
        val hz = size.height * 0.48f
        val w = size.width; val h = size.height; val cx = w / 2
        twinkle(hz * 0.55f, s, 12)
        // searchlights sweeping the clouds
        for (k in 0 until 2) {
            val o = Offset(w * (0.2f + 0.6f * k), hz)
            val a = -PI.toFloat() / 2 + sin(s * 0.35f + k * 2f) * 0.55f
            val l = h * 0.6f
            val p = Path(); p.moveTo(o.x, o.y)
            p.lineTo(o.x + cos(a - 0.05f) * l, o.y + sin(a - 0.05f) * l); p.lineTo(o.x + cos(a + 0.05f) * l, o.y + sin(a + 0.05f) * l); p.close()
            drawPath(p, Brush.radialGradient(listOf(Color(0xFFBFE9FF).copy(alpha = 0.22f), Color.Transparent), o, l), blendMode = BlendMode.Plus)
        }
        // flying cars on three sky lanes
        for (k in 0 until 6) {
            val lane = k % 3
            val dir = if (k % 2 == 0) 1f else -1f
            val q = ((s * (0.06f + 0.03f * lane) + k * 0.37f) % 1f)
            val x = if (dir > 0) q * (w + 80f) - 40f else w + 40f - q * (w + 80f)
            val y = hz * (0.42f + 0.13f * lane)
            drawLine(CYAN.copy(alpha = 0.35f), Offset(x - dir * 26f, y), Offset(x, y), 2f, blendMode = BlendMode.Plus)
            drawCircle(Color.White, 1.8f, Offset(x, y)); drawCircle(Color(0xFFFF2030), 1.4f, Offset(x - dir * 6f, y))
        }
        grid(s, hz, CYAN, 0.5f, lanes = 10, col2 = VIOLET, rows = 14)
        pinkP.reset(); cyanP.reset(); winP.reset(); sideP.reset()
        val n = towers.size / 2
        val travel = s * 0.5f
        val base = floor(travel).toInt()
        for (k in n - 1 downTo 0) {
            val z = 1f - ((k + (travel % 1f)) / n)            // 1 far … 0 near
            if (z < 0.05f) continue
            val f = 0.09f / (z + 0.02f)
            val y = hz + (h - hz) * (1f - z) * (1f - z)
            val id = ((k - base) % n + n) % n
            val a = ((z - 0.05f) * 8f).coerceIn(0f, 1f)
            for (side in 0..1) {
                val tw = towers[id * 2 + side]
                val sgn = if (side == 0) -1f else 1f
                val bw = w * 0.42f * f * tw[0]
                val bh = h * 0.75f * f * tw[1]
                val inner = cx + sgn * w * 0.32f * f
                val outer = inner + sgn * bw
                val l = minOf(inner, outer); val r = maxOf(inner, outer); val t = y - bh
                // side wall towards the avenue, leaning to the vanishing point
                val d = 0.18f
                sideP.moveTo(inner, t); sideP.lineTo(inner + (cx - inner) * d, t + (hz - t) * d); sideP.lineTo(inner + (cx - inner) * d, y + (hz - y) * d); sideP.lineTo(inner, y); sideP.close()
                drawRect(NIGHT, Offset(l, t), Size(r - l, bh))
                val path = if (tw[2] < 0.5f) pinkP else cyanP
                path.addRect(Rect(l, t, r, y))
                path.moveTo(inner, t); path.lineTo(inner + (cx - inner) * d, t + (hz - t) * d); path.lineTo(inner + (cx - inner) * d, y + (hz - y) * d)
                // window rows
                var wy = t + bh * 0.08f
                val step = maxOf(3f, bh * 0.07f)
                var row = 0
                while (wy < y - 2f) { if ((row + id) % 3 != 0) { winP.moveTo(l + (r - l) * 0.12f, wy); winP.lineTo(l + (r - l) * (0.35f + 0.5f * tw[3]), wy) }; wy += step; row++ }
                // billboard on some towers
                if (tw[3] > 0.72f && a > 0.2f) {
                    val bb = Rect(l + (r - l) * 0.1f, t + bh * 0.15f, r - (r - l) * 0.1f, t + bh * 0.32f)
                    val c = if (((s * 2f).toInt() + id) % 2 == 0) PINK else SUN1
                    drawRect(c.copy(alpha = 0.25f * a), bb.topLeft - Offset(3f, 3f), bb.size.copy(bb.width + 6f, bb.height + 6f), blendMode = BlendMode.Plus)
                    drawRect(c.copy(alpha = 0.85f * a), bb.topLeft, bb.size)
                }
                if (tw[2] > 0.8f) glow(Offset((l + r) / 2, t), 4f + 8f * (1f - z), Color(0xFFFF2030), (0.6f + 0.4f * sin(s * 4f + k)) * a)
            }
        }
        drawPath(sideP, Color(0xFF140A30))
        drawPath(winP, SUN1.copy(alpha = 0.55f), style = Stroke(1.6f))
        neonPath(pinkP, PINK, 1.2f, 0.9f)
        neonPath(cyanP, CYAN, 1.2f, 0.9f)
    }
}

/** 8: a ringed synth planet — turning wire globe, an asteroid belt passing in front and behind, moons, a satellite */
private object SynthPlanet : Scene {
    private val rocks = List(140) { i -> val r = rnd(i * 977L); floatArrayOf(r.nextFloat() * 6.28f, 1.35f + r.nextFloat() * 0.5f, 0.6f + r.nextFloat() * 1.6f, r.nextFloat()) }
    private val backPts = ArrayList<Offset>(140); private val frontPts = ArrayList<Offset>(140)
    private val longP = Path()
    private fun DrawScope.centre() = Offset(size.width * 0.5f, size.height * 0.42f)
    private fun DrawScope.radius() = size.minDimension * 0.28f
    override fun DrawScope.back() {
        drawRect(Brush.verticalGradient(listOf(Color(0xFF03010C), Color(0xFF1A0838))))
        glow(Offset(size.width * 0.15f, size.height * 0.8f), size.maxDimension * 0.5f, Color(0xFF5A0F4E), 0.6f)
        glow(Offset(size.width * 0.9f, size.height * 0.15f), size.maxDimension * 0.35f, Color(0xFF0B3A6E), 0.7f)
        for (st in starField) drawCircle(Color.White.copy(alpha = 0.15f + 0.4f * st[2]), 0.5f + st[2], Offset(st[0] * size.width, st[1] * size.height))
        // a small far planet
        val fp = Offset(size.width * 0.85f, size.height * 0.75f)
        drawCircle(Brush.radialGradient(listOf(Color(0xFF2E6E8A), Color(0xFF0A1E2E)), fp - Offset(6f, 6f), 30f), 22f, fp)
        val c = centre(); val r = radius()
        glow(c, r * 1.9f, VIOLET, 0.5f)
    }
    override fun DrawScope.move(s: Float) {
        val c = centre(); val r = radius()
        twinkle(size.height, s, 20)
        val tilt = -14f
        backPts.clear(); frontPts.clear()
        for (k in rocks) {
            val a = k[0] + s * 0.08f / k[1]
            val rr = r * k[1]
            val p = Offset(c.x + cos(a) * rr, c.y + sin(a) * rr * 0.2f)
            if (sin(a) < 0f) backPts.add(p) else frontPts.add(p)
        }
        rotate(tilt, c) {
            // back half of the rings and belt
            for (k in 0 until 3) {
                val rr = r * (1.45f + k * 0.14f)
                drawArc(listOf(HOT, SUN1, CYAN)[k].copy(alpha = 0.6f), 180f, 180f, false, Offset(c.x - rr, c.y - rr * 0.2f), Size(rr * 2, rr * 0.4f), style = Stroke(1.2f))
            }
            drawPoints(backPts, androidx.compose.ui.graphics.PointMode.Points, Color(0xFFB9A0FF).copy(alpha = 0.5f), 1.6f, StrokeCap.Round)
        }
        // the globe: body, turning longitudes, still latitudes, shadow side, rim
        drawCircle(Brush.radialGradient(listOf(Color(0xFF3A1470), Color(0xFF12052A)), c + Offset(-r * 0.3f, -r * 0.3f), r * 1.4f), r, c)
        for (k in -3..3) {
            val y = c.y + r * k / 4f
            val half = r * kotlin.math.sqrt((1f - (k / 4f) * (k / 4f)).coerceAtLeast(0f))
            drawOval(PINK.copy(alpha = 0.45f), Offset(c.x - half, y - half * 0.12f), Size(half * 2, half * 0.24f), style = Stroke(1.1f))
        }
        longP.reset()
        for (k in 0 until 10) {
            val a = s * 0.3f + k * PI.toFloat() / 10
            if (sin(a) <= 0f) continue
            val half = r * abs(cos(a))
            longP.addOval(Rect(c.x - half, c.y - r, c.x + half, c.y + r))
        }
        neonPath(longP, CYAN, 0.9f, 0.8f)
        drawCircle(Brush.radialGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)), c + Offset(r * 0.6f, r * 0.4f), r * 1.6f), r, c)
        drawCircle(CYAN.copy(alpha = 0.25f), r + 3f, c, style = Stroke(6f), blendMode = BlendMode.Plus)
        drawCircle(lighten(CYAN), r, c, style = Stroke(1.4f))
        rotate(tilt, c) {
            for (k in 0 until 3) {
                val rr = r * (1.45f + k * 0.14f)
                val col = listOf(HOT, SUN1, CYAN)[k]
                drawArc(col.copy(alpha = 0.2f), 0f, 180f, false, Offset(c.x - rr, c.y - rr * 0.2f), Size(rr * 2, rr * 0.4f), style = Stroke(6f), blendMode = BlendMode.Plus)
                drawArc(lighten(col), 0f, 180f, false, Offset(c.x - rr, c.y - rr * 0.2f), Size(rr * 2, rr * 0.4f), style = Stroke(1.3f))
            }
            drawPoints(frontPts, androidx.compose.ui.graphics.PointMode.Points, Color(0xFFE6DAFF), 2.2f, StrokeCap.Round)
            for (m in 0 until 3) {
                val a = s * (0.35f + m * 0.2f) + m * 2f
                val rr = r * (2.1f + m * 0.3f)
                drawOval(Color.White.copy(alpha = 0.06f), Offset(c.x - rr, c.y - rr * 0.2f), Size(rr * 2, rr * 0.4f), style = Stroke(1f))
                val p = Offset(c.x + cos(a) * rr, c.y + sin(a) * rr * 0.2f)
                glow(p, 14f, listOf(SUN1, CYAN, PINK)[m], 0.6f)
                drawCircle(Color.White, 2.5f + m, p)
            }
        }
        // a satellite crossing, blinking
        val q = (s * 0.05f) % 1f
        val sp = Offset(-20f + q * (size.width + 40f), size.height * 0.12f + q * size.height * 0.1f)
        drawLine(Color.White.copy(alpha = 0.7f), sp - Offset(5f, 0f), sp + Offset(5f, 0f), 1.2f)
        if ((s * 2f).toInt() % 2 == 0) drawCircle(Color(0xFFFF2030), 2f, sp)
        shooting(s, size.height, 9f)
    }
}

/** 9: a faceted mirror ball over the dance floor — laser fans through the fog, light-up tiles, the crowd */
private object LaserDisco : Scene {
    private val lasers = listOf(PINK, CYAN, LIME, SUN1)
    private val tileP = Array(4) { Path() }
    override fun DrawScope.back() {
        val hz = size.height * 0.7f
        drawRect(Brush.verticalGradient(listOf(Color(0xFF02010A), Color(0xFF1A0630)), 0f, hz), size = Size(size.width, hz))
        // speaker stacks
        for (side in 0..1) {
            val x = if (side == 0) size.width * 0.02f else size.width * 0.82f
            val bw = size.width * 0.16f
            for (k in 0 until 2) {
                val top = hz - size.height * 0.11f * (k + 1)
                drawRect(Color(0xFF0B0716), Offset(x, top), Size(bw, size.height * 0.105f))
                drawRect(VIOLET.copy(alpha = 0.5f), Offset(x, top), Size(bw, size.height * 0.105f), style = Stroke(1f))
                drawCircle(Color(0xFF1C1430), bw * 0.3f, Offset(x + bw / 2, top + size.height * 0.052f))
                drawCircle(VIOLET.copy(alpha = 0.6f), bw * 0.3f, Offset(x + bw / 2, top + size.height * 0.052f), style = Stroke(1f))
            }
        }
        haze(hz, VIOLET, size.height * 0.06f)
        floor(hz, NIGHT, PINK)
    }
    override fun DrawScope.front() {
        // the crowd's heads and raised hands, black against the light
        val y = size.height
        val r = rnd(5)
        var x = -10f
        while (x < size.width + 10f) {
            val hr = size.width * (0.03f + 0.012f * r.nextFloat())
            val hy = y - size.height * (0.06f + 0.04f * r.nextFloat())
            drawRect(Color.Black, Offset(x - hr * 1.3f, hy + hr * 0.6f), Size(hr * 2.6f, y - hy))
            drawCircle(Color.Black, hr, Offset(x, hy))
            drawCircle(PINK.copy(alpha = 0.35f), hr, Offset(x, hy), style = Stroke(1f))
            if (r.nextFloat() < 0.35f) {
                val side = if (r.nextBoolean()) 1f else -1f
                drawLine(Color.Black, Offset(x + side * hr, hy + hr), Offset(x + side * hr * 2.2f, hy - hr * 2.5f), hr * 0.5f, StrokeCap.Round)
            }
            x += hr * 2.4f + r.nextFloat() * hr
        }
    }
    override fun DrawScope.move(s: Float) {
        val hz = size.height * 0.7f
        val w = size.width; val h = size.height
        // fog drifting
        for (k in 0 until 2) glow(Offset(w * (0.3f + 0.4f * sin(s * 0.07f + k * 3f)), hz * (0.5f + 0.2f * k)), w * 0.6f, VIOLET, 0.18f)
        // floor tiles light up in patterns (four batched colours)
        for (p in tileP) p.reset()
        val cols = 8; val rows = 6
        for (r in 0 until rows) for (c in 0 until cols) {
            val on = (((c + r + (s * 3f).toInt()) % 4) == 0) || (((c * 7 + r * 3 + (s * 2f).toInt()) % 11) == 0)
            if (!on) continue
            val z0 = r / rows.toFloat(); val z1 = (r + 1) / rows.toFloat()
            val y0 = hz + (h - hz) * z0 * z0; val y1 = hz + (h - hz) * z1 * z1
            val sp0 = 0.3f + 0.7f * z0; val sp1 = 0.3f + 0.7f * z1
            val p = tileP[(c + r) % 4]
            p.moveTo(w / 2 + (c / cols.toFloat() - 0.5f) * w * 2 * sp0, y0); p.lineTo(w / 2 + ((c + 1) / cols.toFloat() - 0.5f) * w * 2 * sp0, y0)
            p.lineTo(w / 2 + ((c + 1) / cols.toFloat() - 0.5f) * w * 2 * sp1, y1); p.lineTo(w / 2 + (c / cols.toFloat() - 0.5f) * w * 2 * sp1, y1); p.close()
        }
        for (k in 0 until 4) drawPath(tileP[k], lasers[k].copy(alpha = 0.24f), blendMode = BlendMode.Plus)
        grid(s, hz, VIOLET, 0f, lanes = 8, rows = 6)
        // laser fans: a wide foggy beam and a hot core each
        for (side in 0..1) {
            val o = Offset(if (side == 0) w * 0.1f else w * 0.9f, hz - h * 0.22f)
            for (b in 0 until 7) {
                val sweep = sin(s * (0.9f + side * 0.2f) + b * 0.35f)
                val a = (if (side == 0) -PI.toFloat() / 2 + 0.35f else -PI.toFloat() / 2 - 0.35f) + sweep * 0.6f + (b - 3) * 0.07f
                val end = Offset(o.x + cos(a) * h * 1.2f, o.y + sin(a) * h * 1.2f)
                val col = lasers[(b + side) % 4]
                drawLine(col.copy(alpha = 0.10f), o, end, 9f, blendMode = BlendMode.Plus)
                drawLine(lighten(col).copy(alpha = 0.9f), o, end, 1.1f, blendMode = BlendMode.Plus)
            }
        }
        // mirror ball: facets catching the light as it turns
        val c = Offset(w / 2, h * 0.17f); val r = w * 0.12f
        drawLine(Color.White.copy(alpha = 0.5f), Offset(c.x, 0f), Offset(c.x, c.y - r), 1.5f)
        glow(c, r * 2.4f, Color.White, 0.22f)
        drawCircle(Color(0xFF1E1E30), r, c)
        val fs = r * 0.16f
        for (la in -4..4) {
            val phi = la * 0.33f
            val ring = cos(phi)
            for (lo in 0 until 14) {
                val th = s * 0.8f + lo * 2 * PI.toFloat() / 14
                if (cos(th) < 0f) continue                      // the far side
                val x = c.x + sin(th) * r * ring
                val y = c.y + sin(phi) * r
                val lit = (0.5f + 0.5f * sin(th * 3f + la + s * 2f)) * cos(th)
                drawRect(Color.White.copy(alpha = 0.15f + 0.8f * lit * lit), Offset(x - fs / 2 * cos(th), y - fs / 2), Size(fs * cos(th), fs * 0.9f))
            }
        }
        drawCircle(Color.White.copy(alpha = 0.6f), r, c, style = Stroke(1.2f))
        val sr = rnd((s * 6).toLong())
        for (k in 0 until 5) {
            val a = sr.nextFloat() * 6.28f; val d = sr.nextFloat() * r
            val p = Offset(c.x + cos(a) * d, c.y + sin(a) * d)
            val l = 3f + sr.nextFloat() * 9f
            drawLine(Color.White, Offset(p.x - l, p.y), Offset(p.x + l, p.y), 1.2f, blendMode = BlendMode.Plus)
            drawLine(Color.White, Offset(p.x, p.y - l), Offset(p.x, p.y + l), 1.2f, blendMode = BlendMode.Plus)
        }
        // reflections wandering over the room
        for (k in 0 until 14) {
            val a = s * 0.5f + k * 0.63f
            drawCircle(lasers[k % 4].copy(alpha = 0.4f), 2.5f, Offset((0.5f + 0.48f * sin(a * 1.3f + k)) * w, (0.1f + 0.55f * abs(sin(a + k * 2f))) * hz))
        }
        // strobe now and then
        if ((s * 10f).toInt() % 47 == 0) drawRect(Color.White.copy(alpha = 0.08f))
    }
}
