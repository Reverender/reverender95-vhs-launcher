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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
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
    val names = listOf("TAPE TOWER", "CHANNEL SURF", "MALLSOFT CUBES", "GIANT CASSETTE", "LASERDISC", "PRISM PYRAMID")
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
        with(scene) { overlay(s) }
    }
}

private interface Scene {
    fun DrawScope.back()
    fun DrawScope.move(s: Float)
    fun DrawScope.front() {}
    /** on top of everything, not blurred (on-screen text, counters) */
    fun DrawScope.overlay(s: Float) {}
}

private val SCENES: List<Scene> = listOf(TapeTower, ChannelSurf, MallsoftCubes, GiantCassette, Laserdisc, PrismPyramid)

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

// ---------------------------------------------------------------- 3D solids (filled, shaded, sorted)

/** one box to draw: centre, half sizes, own turn (yaw, pitch), colour, edge colour, optional picture per face */
private class Box3(
    var x: Float, var y: Float, var z: Float, var hx: Float, var hy: Float, var hz: Float,
    var ay: Float = 0f, var ax: Float = 0f, var col: Color = Color.Gray, var edge: Color? = null,
    var face: (DrawScope.(faceIndex: Int, bounds: Rect) -> Unit)? = null,
)
private val BOX_F = arrayOf(intArrayOf(0, 1, 2, 3), intArrayOf(5, 4, 7, 6), intArrayOf(4, 0, 3, 7), intArrayOf(1, 5, 6, 2), intArrayOf(4, 5, 1, 0), intArrayOf(3, 2, 6, 7))
private val BOX_V = floatArrayOf(-1f, -1f, -1f, 1f, -1f, -1f, 1f, 1f, -1f, -1f, 1f, -1f, -1f, -1f, 1f, 1f, -1f, 1f, 1f, 1f, 1f, -1f, 1f, 1f)
private class FaceDraw(var z: Float = 0f, val path: Path = Path(), var col: Color = Color.Black, var edge: Color? = null, var box: Box3? = null, var idx: Int = 0)
// reused every frame (no new paths / objects per face per frame: less garbage, steadier frame times)
private val facePool = ArrayList<FaceDraw>()
private val faceList = ArrayList<FaceDraw>()
private val faceOrder = Comparator<FaceDraw> { a, b -> b.z.compareTo(a.z) }
private val cvx = FloatArray(8); private val cvy = FloatArray(8); private val cvz = FloatArray(8)
private val csx = FloatArray(8); private val csy = FloatArray(8)

/**
 * Draws boxes as solid shaded blocks (lit from the upper front), hidden faces dropped, the rest painted far to
 * near. Camera space: x right, y down, z away; the camera sits at z = -5. [scale] = pixels per unit at z = 0.
 */
private fun DrawScope.solids(boxes: List<Box3>, cx: Float, cy: Float, scale: Float) {
    faceList.clear()
    var used = 0
    for (b in boxes) {
        val ca = cos(b.ay); val sa = sin(b.ay); val cb = cos(b.ax); val sb = sin(b.ax)
        for (i in 0 until 8) {
            val x = BOX_V[i * 3] * b.hx; val y = BOX_V[i * 3 + 1] * b.hy; val z = BOX_V[i * 3 + 2] * b.hz
            val x1 = x * ca + z * sa; val z1 = -x * sa + z * ca
            val y1 = y * cb - z1 * sb; val z2 = y * sb + z1 * cb
            cvx[i] = x1 + b.x; cvy[i] = y1 + b.y; cvz[i] = z2 + b.z
            val f = 4f / (cvz[i] + 5f)
            csx[i] = cx + cvx[i] * f * scale; csy[i] = cy + cvy[i] * f * scale
        }
        for ((fi, f) in BOX_F.withIndex()) {
            val a = f[0]; val b1 = f[1]; val c = f[2]
            val e1x = cvx[b1] - cvx[a]; val e1y = cvy[b1] - cvy[a]; val e1z = cvz[b1] - cvz[a]
            val e2x = cvx[c] - cvx[a]; val e2y = cvy[c] - cvy[a]; val e2z = cvz[c] - cvz[a]
            val nx = e1y * e2z - e1z * e2y; val ny = e1z * e2x - e1x * e2z; val nz = e1x * e2y - e1y * e2x // (points inwards)
            if (nx * cvx[a] + ny * cvy[a] + nz * (cvz[a] + 5f) <= 0f) continue                            // facing away
            val len = kotlin.math.sqrt(nx * nx + ny * ny + nz * nz).coerceAtLeast(1e-4f)
            val light = (-(nx * -0.3f + ny * -0.75f + nz * -0.55f) / len).coerceIn(0f, 1f)
            val shade = 0.42f + 0.58f * light
            if (used == facePool.size) facePool.add(FaceDraw())
            val fd = facePool[used++]
            val p = fd.path; p.reset(); p.moveTo(csx[f[0]], csy[f[0]]); for (k in 1..3) p.lineTo(csx[f[k]], csy[f[k]]); p.close()
            fd.z = (cvz[f[0]] + cvz[f[1]] + cvz[f[2]] + cvz[f[3]]) / 4f
            fd.col = Color(b.col.red * shade, b.col.green * shade, b.col.blue * shade, b.col.alpha); fd.edge = b.edge; fd.box = b; fd.idx = fi
            faceList.add(fd)
        }
    }
    faceList.sortWith(faceOrder)
    for (fd in faceList) {
        drawPath(fd.path, fd.col)
        val pic = fd.box?.face
        if (pic != null) {
            val bnd = fd.path.getBounds()
            clipPath(fd.path) { this.pic(fd.idx, bnd) }
        }
        fd.edge?.let { neonPath(fd.path, it, 1f, 0.9f) }
    }
}

/** grey TV snow: a noise tile moved to a random place every frame */
private object Snow {
    val shader: android.graphics.BitmapShader by lazy {
        val r = java.util.Random(11)
        val b = android.graphics.Bitmap.createBitmap(128, 128, android.graphics.Bitmap.Config.ARGB_8888)
        for (y in 0 until 128) for (x in 0 until 128) { val v = r.nextInt(256); b.setPixel(x, y, android.graphics.Color.rgb(v, v, v)) }
        android.graphics.BitmapShader(b, android.graphics.Shader.TileMode.REPEAT, android.graphics.Shader.TileMode.REPEAT)
    }
    val brush by lazy { androidx.compose.ui.graphics.ShaderBrush(shader) }
    private val m = android.graphics.Matrix()
    fun DrawScope.snow(seed: Long, rect: Rect = Rect(Offset.Zero, size), alpha: Float = 1f, cell: Float = 2f) {
        val r = rnd(seed)
        m.setScale(cell, cell); m.postTranslate(r.nextFloat() * 256f, r.nextFloat() * 256f); shader.setLocalMatrix(m)
        drawRect(brush, rect.topLeft, rect.size, alpha = alpha)
    }
}

/** SMPTE-style colour bars into [r] */
private val BARS = listOf(Color(0xFFC0C0C0), Color(0xFFC0C000), Color(0xFF00C0C0), Color(0xFF00C000), Color(0xFFC000C0), Color(0xFFC00000), Color(0xFF0000C0))
private fun DrawScope.colourBars(r: Rect) {
    val w = r.width / 7
    for (i in 0 until 7) drawRect(BARS[i], Offset(r.left + i * w, r.top), Size(w + 1f, r.height * 0.72f))
    val rev = listOf(Color(0xFF0000C0), Color(0xFF131313), Color(0xFFC000C0), Color(0xFF131313), Color(0xFF00C0C0), Color(0xFF131313), Color(0xFFC0C0C0))
    for (i in 0 until 7) drawRect(rev[i], Offset(r.left + i * w, r.top + r.height * 0.72f), Size(w + 1f, r.height * 0.08f))
    drawRect(Color(0xFF00214C), Offset(r.left, r.top + r.height * 0.8f), Size(r.width * 0.2f, r.height * 0.2f))
    drawRect(Color.White, Offset(r.left + r.width * 0.2f, r.top + r.height * 0.8f), Size(r.width * 0.2f, r.height * 0.2f))
    drawRect(Color(0xFF32006A), Offset(r.left + r.width * 0.4f, r.top + r.height * 0.8f), Size(r.width * 0.2f, r.height * 0.2f))
    drawRect(Color(0xFF131313), Offset(r.left + r.width * 0.6f, r.top + r.height * 0.8f), Size(r.width * 0.4f, r.height * 0.2f))
}
/** OSD text in the launcher's own pixel lettering, [h] = letter height in pixels */
private fun DrawScope.osd(text: String, x: Float, y: Float, h: Float, col: Color = Color.White) =
    drawGlyphs(text, x, y, h / 7f, Color.Black.copy(alpha = 0.6f), col)
private fun osdW(text: String, h: Float) = text.length * 6 * h / 7f

private fun tc(s: Float): String { val t = s.toInt(); return "%d:%02d:%02d".format(t / 3600, t / 60 % 60, t % 60) }

/** 1: flipping through channels — snow, then a picture for a moment, the channel number in green */
private object ChannelSurf : Scene {
    override fun DrawScope.back() { drawRect(Color.Black) }
    private fun pictureOf(k: Int) = ((k * 7 + 3) % 4)
    override fun DrawScope.move(s: Float) {
        val cyc = floor(s / 5.5f).toInt(); val ph = s - cyc * 5.5f
        val showing = ph > 2.6f
        if (showing) {
            val r = Rect(Offset.Zero, size)
            when (pictureOf(cyc)) {
                0 -> colourBars(r)
                1 -> { drawRect(Color(0xFF1A1AD0)); val h = size.minDimension * 0.06f; osd("NO SIGNAL", size.width / 2 - osdW("NO SIGNAL", h) / 2, size.height * 0.45f, h) }
                2 -> { // a tiny outrun picture
                    val hz = size.height * 0.55f
                    drawRect(Brush.verticalGradient(listOf(NIGHT, Color(0xFF5A0F6E)), 0f, hz), size = Size(size.width, hz))
                    drawCircle(Brush.verticalGradient(listOf(SUN1, SUN2), hz - size.width * 0.3f, hz), size.width * 0.22f, Offset(size.width / 2, hz - size.width * 0.05f))
                    drawRect(NIGHT, Offset(0f, hz), Size(size.width, size.height - hz))
                    grid(s, hz, PINK, rows = 10, lanes = 10)
                }
                else -> { // a spinning cube on black
                    drawRect(Color(0xFF07021A))
                    solids(listOf(Box3(0f, 0f, 0f, 1f, 1f, 1f, s * 0.9f, s * 0.6f, Color(0xFFFF6EC7), Color(0xFF29F3FF))), size.width / 2, size.height * 0.45f, size.minDimension * 0.28f)
                }
            }
            // the picture rolls and wobbles a little: a soft bar runs up the screen
            val by = size.height * (1f - (s * 0.35f % 1f))
            drawRect(Color.White.copy(alpha = 0.08f), Offset(0f, by), Size(size.width, size.height * 0.08f))
            with(Snow) { snow((s * 30f).toLong(), alpha = 0.12f) }
        } else {
            with(Snow) { snow((s * 30f).toLong(), cell = 2.5f) }
            // sync lines tearing across the snow
            val r = rnd((s * 15f).toLong())
            for (k in 0 until 3) drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, r.nextFloat() * size.height), Size(size.width, 2f + r.nextFloat() * 6f))
        }
    }
    override fun DrawScope.overlay(s: Float) {
        val cyc = floor(s / 5.5f).toInt(); val ph = s - cyc * 5.5f
        val h = size.minDimension * 0.08f
        val ch = "CH " + (3 + (cyc * 5) % 60).toString().padStart(2, '0')
        if (ph < 4.4f) osd(ch, size.width - h - osdW(ch, h), h, h, Color(0xFF39FF14))
        if (ph < 1.2f) osd("▶▶", h, h, h, Color(0xFF39FF14))
    }
}

/** 0: a tower of VHS tapes turning slowly, reels spinning, over a dusky neon grid */
private object TapeTower : Scene {
    private val boxes = List(5) { Box3(0f, 0f, 0f, 1.25f, 0.17f, 0.72f, col = Color(0xFF1C1626), edge = Color(0xFF8A6BFF)) }
    override fun DrawScope.back() {
        val hz = size.height * 0.7f
        sky(NIGHT, Color(0xFF3A0F5C), hz)
        haze(hz, PINK, size.height * 0.05f)
        floor(hz, NIGHT, PINK)
    }
    override fun DrawScope.move(s: Float) {
        val hz = size.height * 0.7f
        twinkle(hz, s, 16)
        grid(s, hz, PINK, 0.15f, lanes = 12, col2 = VIOLET, rows = 12)
        boxes.forEachIndexed { i, b ->
            b.x = sin(i * 1.7f) * 0.12f; b.y = 0.95f - i * 0.38f; b.z = 0.6f
            b.ay = s * 0.35f + i * 0.22f + sin(s * 0.5f + i) * 0.1f; b.ax = -0.45f
            b.face = { fi, r -> tapeFace(fi, r, s, i) }
        }
        solids(boxes, size.width / 2, size.height * 0.44f, size.minDimension * 0.42f)
    }
    private fun DrawScope.tapeFace(fi: Int, r: Rect, s: Float, i: Int) {
        if (fi == 4) { // top: the label and the window with two reels
            drawRect(Color(0xFFF2EBD9), Offset(r.left + r.width * 0.12f, r.top + r.height * 0.12f), Size(r.width * 0.76f, r.height * 0.22f))
            val wy = r.top + r.height * 0.45f
            drawRect(Color(0xFF0B0812), Offset(r.left + r.width * 0.25f, wy), Size(r.width * 0.5f, r.height * 0.32f))
            for (k in 0..1) {
                val c = Offset(r.left + r.width * (0.36f + 0.28f * k), wy + r.height * 0.16f)
                val rr = minOf(r.width * 0.09f, r.height * 0.14f)
                drawCircle(Color(0xFFDDDDDD), rr, c)
                val a = s * 4f * (if (i % 2 == 0) 1 else -1)
                for (sp in 0 until 3) { val an = a + sp * 2.094f; drawLine(Color(0xFF333333), c, c + Offset(cos(an) * rr, sin(an) * rr), 1.5f) }
            }
        } else if (fi == 0) { // spine: a coloured stripe
            drawRect(listOf(PINK, CYAN, SUN1, LIME, HOT)[i % 5].copy(alpha = 0.8f), Offset(r.left, r.top + r.height * 0.35f), Size(r.width, r.height * 0.3f))
        }
    }
}

/** 2: an empty pastel mall after hours — checker floor, arches, palms, pastel cubes floating and turning */
private object MallsoftCubes : Scene {
    private val cubes = List(5) { i -> val r = rnd(i * 37L); Box3(0f, 0f, 0f, 0.28f + r.nextFloat() * 0.15f, 0f, 0f, col = listOf(Color(0xFFFFB3DE), Color(0xFF9FFFE0), Color(0xFFC9A0FF), Color(0xFFFFCBA4), Color(0xFF9BE7FF))[i]).also { it.hy = it.hx; it.hz = it.hx } }
    private val seeds = List(5) { i -> val r = rnd(i * 91L); floatArrayOf(r.nextFloat() * 2 - 1, r.nextFloat(), r.nextFloat() * 6.28f) }
    override fun DrawScope.back() {
        val hz = size.height * 0.58f
        drawRect(Brush.verticalGradient(listOf(Color(0xFFF7C6DE), Color(0xFFE6D5F7), Color(0xFFCDEFF2)), 0f, hz), size = Size(size.width, hz))
        // arches on the back wall
        val n = 4; val aw = size.width / n
        for (i in 0 until n) {
            val l = i * aw + aw * 0.15f; val w = aw * 0.7f; val top = hz * 0.35f
            val p = Path(); p.moveTo(l, hz); p.lineTo(l, top + w / 2); p.arcTo(Rect(l, top, l + w, top + w), 180f, 180f, false); p.lineTo(l + w, hz); p.close()
            drawPath(p, Color(0xFFB8E3E9))
            drawPath(p, Color.White.copy(alpha = 0.7f), style = Stroke(2f))
        }
        drawRect(Color.White.copy(alpha = 0.5f), Offset(0f, hz * 0.3f), Size(size.width, 3f))
        // checker floor in perspective
        drawRect(Color(0xFFF5F0FA), Offset(0f, hz), Size(size.width, size.height - hz))
        val rows = 9; val cols = 10
        for (r in 0 until rows) for (c in -cols until cols) {
            if ((r + c) % 2 != 0) continue
            val z0 = r / rows.toFloat(); val z1 = (r + 1) / rows.toFloat()
            val y0 = hz + (size.height - hz) * z0 * z0; val y1 = hz + (size.height - hz) * z1 * z1
            val s0 = 0.15f + 0.85f * z0; val s1 = 0.15f + 0.85f * z1
            val cw = size.width / 4f
            val p = Path()
            p.moveTo(size.width / 2 + c * cw * s0, y0); p.lineTo(size.width / 2 + (c + 1) * cw * s0, y0)
            p.lineTo(size.width / 2 + (c + 1) * cw * s1, y1); p.lineTo(size.width / 2 + c * cw * s1, y1); p.close()
            drawPath(p, Color(0xFF8E7BB8).copy(alpha = 0.55f))
        }
    }
    override fun DrawScope.move(s: Float) {
        cubes.forEachIndexed { i, b ->
            val sd = seeds[i]
            b.x = sd[0] * 1.5f; b.y = -0.2f - sd[1] * 0.9f + sin(s * 0.8f + sd[2]) * 0.15f; b.z = 0.5f + i * 0.35f
            b.ay = s * (0.4f + i * 0.08f) + sd[2]; b.ax = s * 0.3f + i
        }
        // soft shadows on the floor
        val hz = size.height * 0.58f
        for ((i, b) in cubes.withIndex()) {
            val f = 4f / (b.z + 5f)
            val x = size.width / 2 + b.x * f * size.minDimension * 0.42f
            drawOval(Color(0xFF6A5A8A).copy(alpha = 0.18f), Offset(x - 40f * f, hz + (size.height - hz) * (0.5f + i * 0.08f)), Size(80f * f, 14f * f))
        }
        solids(cubes, size.width / 2, size.height * 0.5f, size.minDimension * 0.42f)
    }
}


/** a VHS tape's top face: paper label, window, two turning reels */
private fun DrawScope.tapeTop(r: Rect, s: Float, dir: Float = 1f) {
    drawRect(Color(0xFFF2EBD9), Offset(r.left + r.width * 0.12f, r.top + r.height * 0.12f), Size(r.width * 0.76f, r.height * 0.22f))
    drawRect(PINK.copy(alpha = 0.85f), Offset(r.left + r.width * 0.12f, r.top + r.height * 0.12f), Size(r.width * 0.76f, r.height * 0.05f))
    val wy = r.top + r.height * 0.45f
    drawRect(Color(0xFF0B0812), Offset(r.left + r.width * 0.25f, wy), Size(r.width * 0.5f, r.height * 0.32f))
    for (k in 0..1) {
        val c = Offset(r.left + r.width * (0.36f + 0.28f * k), wy + r.height * 0.16f)
        val rr = minOf(r.width * 0.09f, r.height * 0.14f)
        // the wound tape around the left reel shrinks as the right one grows
        val wound = rr * (1.15f + 0.5f * (if (k == 0) 0.5f + 0.5f * sin(s * 0.1f) else 0.5f - 0.5f * sin(s * 0.1f)))
        drawCircle(Color(0xFF3A2416), wound, c)
        drawCircle(Color(0xFFDDDDDD), rr, c)
        val a = s * 4f * dir
        for (sp in 0 until 3) { val an = a + sp * 2.094f; drawLine(Color(0xFF333333), c, c + Offset(cos(an) * rr, sin(an) * rr), 1.5f) }
        drawCircle(Color(0xFF333333), rr * 0.25f, c)
    }
}

/** 3: one giant VHS tape, tilted and turning, reels spinning, the tape ribbon waving out of it */
private object GiantCassette : Scene {
    private val tape = Box3(0f, 0f, 0.3f, 1.4f, 0.2f, 0.85f, col = Color(0xFF1C1626), edge = PINK)
    private val ribbon = Path()
    override fun DrawScope.back() {
        sky(Color(0xFF12052A), Color(0xFF5A1A6E), size.height)
        glow(Offset(size.width / 2, size.height * 0.45f), size.minDimension * 0.7f, VIOLET, 0.4f)
    }
    override fun DrawScope.move(s: Float) {
        twinkle(size.height, s, 24)
        // the ribbon: out of the cassette's mouth, waving down off the screen
        ribbon.reset()
        for (k in 0..44) {
            val u = k / 44f
            val x = size.width * (0.55f + 0.5f * u) + sin(u * 7f - s * 2.2f) * size.width * 0.05f * u
            val y = size.height * (0.56f + 0.42f * u) + sin(u * 9f - s * 3f) * size.height * 0.03f
            if (k == 0) ribbon.moveTo(x, y) else ribbon.lineTo(x, y)
        }
        drawPath(ribbon, Color(0xFF2A160C), style = Stroke(size.minDimension * 0.022f))
        drawPath(ribbon, Color(0xFF8A5A30).copy(alpha = 0.7f), style = Stroke(size.minDimension * 0.006f))
        tape.ay = sin(s * 0.4f) * 0.35f + 0.3f; tape.ax = -0.9f + sin(s * 0.3f) * 0.1f
        tape.y = sin(s * 0.8f) * 0.06f
        tape.face = { fi, r -> if (fi == 4) tapeTop(r, s) else if (fi == 0) drawRect(CYAN.copy(alpha = 0.8f), Offset(r.left, r.top + r.height * 0.35f), Size(r.width, r.height * 0.3f)) }
        solids(listOf(tape), size.width / 2, size.height * 0.48f, size.minDimension * 0.42f)
    }
}

/** 4: a huge laserdisc spinning over the grid, a rainbow sheen sweeping across its mirror side */
private object Laserdisc : Scene {
    override fun DrawScope.back() {
        val hz = size.height * 0.64f
        sky(Color(0xFF1A0636), Color(0xFF7A1E6C), hz)
        sun(size.width / 2, hz - size.width * 0.06f, size.width * 0.2f, hz)
        floor(hz, NIGHT, PINK)
    }
    override fun DrawScope.move(s: Float) {
        val hz = size.height * 0.64f
        grid(s, hz, PINK, 0.2f, lanes = 12, col2 = VIOLET, rows = 12)
        val cx = size.width / 2; val cy = size.height * 0.4f + sin(s * 0.9f) * size.minDimension * 0.012f
        val rx = size.width * 0.4f; val ry = rx * (0.35f + 0.1f * sin(s * 0.4f))
        // shadow on the floor and the disc's edge
        drawOval(Color.Black.copy(alpha = 0.35f), Offset(cx - rx * 0.8f, size.height * 0.8f), Size(rx * 1.6f, size.minDimension * 0.03f))
        drawOval(Color(0xFF4A4A58), Offset(cx - rx, cy - ry + size.minDimension * 0.012f), Size(rx * 2, ry * 2))
        // the mirror side: silver with a rainbow band moving across
        val sw = (s * 0.3f) % 1.3f - 0.15f
        val stops = arrayOf(
            0f to Color(0xFFB8B8CC), (sw - 0.16f).coerceIn(0.01f, 0.97f) to Color(0xFFE2E2F2),
            (sw - 0.06f).coerceIn(0.02f, 0.98f) to Color(0xFFFF6EC7), sw.coerceIn(0.03f, 0.985f) to Color(0xFF29F3FF),
            (sw + 0.06f).coerceIn(0.04f, 0.99f) to Color(0xFFFFE860), (sw + 0.16f).coerceIn(0.05f, 0.995f) to Color(0xFFD8D8EA), 1f to Color(0xFF9A9AB2),
        )
        drawOval(Brush.horizontalGradient(colorStops = stops, startX = cx - rx, endX = cx + rx), Offset(cx - rx, cy - ry), Size(rx * 2, ry * 2))
        // grooves: faint rings with short bright arcs going round
        for (k in 2..9) {
            val f = k / 10f
            val tl = Offset(cx - rx * f, cy - ry * f); val sz = Size(rx * 2 * f, ry * 2 * f)
            drawOval(Color.White.copy(alpha = 0.08f), tl, sz, style = Stroke(1f))
            drawArc(Color.White.copy(alpha = 0.35f), (s * 140f * (1.2f - f)) % 360f, 40f, false, tl, sz, style = Stroke(1.4f))
        }
        // label and hole
        drawOval(Color(0xFFFF6EC7), Offset(cx - rx * 0.22f, cy - ry * 0.22f), Size(rx * 0.44f, ry * 0.44f))
        drawOval(NIGHT, Offset(cx - rx * 0.05f, cy - ry * 0.05f), Size(rx * 0.1f, ry * 0.1f))
        glow(Offset(cx - rx * 0.4f, cy - ry * 0.3f), rx * 0.25f, Color.White, 0.25f)
    }
}

/** 5: a glass pyramid turning in the dark; a white beam goes in and fans out into pastel colours */
private object PrismPyramid : Scene {
    private val PASTELS = listOf(Color(0xFFFFB3DE), Color(0xFFFFCBA4), Color(0xFFFFF5A8), Color(0xFF9FFFE0), Color(0xFF9BE7FF), Color(0xFFC9A0FF))
    private val face = Path()
    private val bx = FloatArray(3); private val by = FloatArray(3); private val bz = FloatArray(3)
    override fun DrawScope.back() {
        sky(NIGHT, Color(0xFF1E0A3A), size.height)
        floor(size.height * 0.78f, NIGHT, VIOLET)
    }
    override fun DrawScope.move(s: Float) {
        twinkle(size.height * 0.78f, s, 20)
        val cx = size.width / 2; val cy = size.height * 0.46f; val r = size.width * 0.3f
        val hit = Offset(cx - r * 0.32f, cy)
        val out = Offset(cx + r * 0.3f, cy + r * 0.02f)
        // the white beam in
        neon(Color.White, Offset(0f, cy + r * 0.12f), hit, size.minDimension * 0.008f)
        // the spectrum out, gently waving
        PASTELS.forEachIndexed { i, c ->
            val ey = cy - r * 0.45f + i * r * 0.2f + sin(s * 1.2f + i) * r * 0.03f
            neon(c, out, Offset(size.width, ey), size.minDimension * 0.012f, 0.9f)
        }
        // the pyramid: three base corners turning round, an apex above; back faces first
        val a = s * 0.4f
        for (i in 0..2) { val an = a + i * 2.094f; bx[i] = cos(an); bz[i] = sin(an); by[i] = 0.45f }
        val apexY = -0.9f
        val order = intArrayOf(0, 1, 2).sortedByDescending { i -> (bz[i] + bz[(i + 1) % 3]) / 2 }
        for ((k, i) in order.withIndex()) {
            val j = (i + 1) % 3
            fun px(x: Float, z: Float) = cx + x * r * (4f / (z * 0.35f + 4.2f))
            fun py(y: Float, z: Float) = cy + y * r + z * r * 0.22f
            face.reset()
            face.moveTo(px(bx[i], bz[i]), py(by[i], bz[i])); face.lineTo(px(bx[j], bz[j]), py(by[j], bz[j])); face.lineTo(cx, cy + apexY * r); face.close()
            drawPath(face, Color(0xFFC8DCFF).copy(alpha = 0.10f + 0.09f * k))
            drawPath(face, Color.White.copy(alpha = 0.85f), style = Stroke(1.6f))
        }
        glow(hit, r * 0.18f, Color.White, 0.5f)
        glow(out, r * 0.22f, Color(0xFFFFE0F0), 0.45f)
    }
}
