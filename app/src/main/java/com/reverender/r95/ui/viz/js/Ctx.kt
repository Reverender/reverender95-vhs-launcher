package com.reverender.r95.ui.viz.js

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** CSS colour strings -> ARGB, cached. */
object CssColor {
    private val cache = java.util.concurrent.ConcurrentHashMap<String, Int>(512)
    fun parse(s: String): Int {
        cache[s]?.let { return it }
        val c = parseRaw(s.trim())
        if (cache.size > 6000) cache.clear()
        cache[s] = c
        return c
    }

    private fun clamp255(d: Double): Int = when { d.isNaN() -> 0; d < 0 -> 0; d > 255 -> 255; else -> Math.round(d).toInt() }
    private fun alpha(d: Double): Int = when { d.isNaN() -> 0; d < 0 -> 0; d > 1 -> 255; else -> Math.round(d * 255).toInt() }

    private fun parseRaw(s: String): Int {
        if (s.isEmpty()) return 0
        if (s[0] == '#') {
            val h = s.substring(1)
            return try {
                when (h.length) {
                    3 -> { val r = h[0].digitToInt(16); val g = h[1].digitToInt(16); val b = h[2].digitToInt(16); (0xFF shl 24) or (r * 17 shl 16) or (g * 17 shl 8) or (b * 17) }
                    4 -> { val r = h[0].digitToInt(16); val g = h[1].digitToInt(16); val b = h[2].digitToInt(16); val a = h[3].digitToInt(16); (a * 17 shl 24) or (r * 17 shl 16) or (g * 17 shl 8) or (b * 17) }
                    6 -> (0xFF shl 24) or h.toInt(16)
                    8 -> { val v = h.toLong(16); (((v and 0xFF) shl 24) or (v shr 8)).toInt() }
                    else -> 0
                }
            } catch (_: Exception) { 0 }
        }
        val open = s.indexOf('(')
        if (open > 0) {
            val fn = s.substring(0, open).trim().lowercase()
            val inner = s.substring(open + 1, s.lastIndexOf(')').let { if (it < 0) s.length else it })
            val parts = inner.split(',', ' ', '/').filter { it.isNotBlank() }.map { it.trim() }
            fun comp(i: Int, scale: Double): Double {
                val p = parts.getOrNull(i) ?: return Double.NaN
                return if (p.endsWith("%")) (p.dropLast(1).toDoubleOrNull() ?: 0.0) / 100.0 * scale else p.toDoubleOrNull() ?: 0.0
            }
            when (fn) {
                "rgb", "rgba" -> {
                    val a = if (parts.size >= 4) comp(3, 1.0) else 1.0
                    return (alpha(a) shl 24) or (clamp255(comp(0, 255.0)) shl 16) or (clamp255(comp(1, 255.0)) shl 8) or clamp255(comp(2, 255.0))
                }
                "hsl", "hsla" -> {
                    val h = ((parts.getOrNull(0)?.removeSuffix("deg")?.toDoubleOrNull() ?: 0.0) % 360 + 360) % 360 / 360.0
                    val sat = comp(1, 1.0).coerceIn(0.0, 1.0)
                    val l = comp(2, 1.0).coerceIn(0.0, 1.0)
                    val a = if (parts.size >= 4) comp(3, 1.0) else 1.0
                    val q = if (l < 0.5) l * (1 + sat) else l + sat - l * sat
                    val p = 2 * l - q
                    fun hue(t0: Double): Double { var t = t0; if (t < 0) t += 1; if (t > 1) t -= 1; return when { t < 1.0 / 6 -> p + (q - p) * 6 * t; t < 0.5 -> q; t < 2.0 / 3 -> p + (q - p) * (2.0 / 3 - t) * 6; else -> p } }
                    return (alpha(a) shl 24) or (clamp255(hue(h + 1.0 / 3) * 255) shl 16) or (clamp255(hue(h) * 255) shl 8) or clamp255(hue(h - 1.0 / 3) * 255)
                }
            }
            return 0
        }
        return when (s.lowercase()) {
            "transparent" -> 0
            "white" -> 0xFFFFFFFF.toInt(); "black" -> 0xFF000000.toInt(); "red" -> 0xFFFF0000.toInt(); "lime" -> 0xFF00FF00.toInt()
            "blue" -> 0xFF0000FF.toInt(); "yellow" -> 0xFFFFFF00.toInt(); "cyan", "aqua" -> 0xFF00FFFF.toInt(); "magenta", "fuchsia" -> 0xFFFF00FF.toInt()
            "gray", "grey" -> 0xFF808080.toInt(); "orange" -> 0xFFFFA500.toInt(); "purple" -> 0xFF800080.toInt(); "green" -> 0xFF008000.toInt()
            "pink" -> 0xFFFFC0CB.toInt(); "hotpink" -> 0xFFFF69B4.toInt(); "gold" -> 0xFFFFD700.toInt(); "silver" -> 0xFFC0C0C0.toInt()
            else -> 0xFF000000.toInt()
        }
    }
}

/** CanvasGradient. Coordinates are in user space; resolved against the transform at paint time. */
/** A canvas pattern (createPattern). */
class Pat internal constructor(val bmp: Bitmap)

class Grad internal constructor(private val radial: Boolean, private val c: DoubleArray) {
    private var offs = FloatArray(4)
    private var cols = IntArray(4)
    private var n = 0
    fun addColorStop(offset: Double, color: Any?) {
        cachedN = -1
        if (n == offs.size) { offs = offs.copyOf(n * 2); cols = cols.copyOf(n * 2) }
        val o = if (offset.isNaN()) 0f else offset.toFloat().coerceIn(0f, 1f)
        // stops are kept sorted (stable for equal offsets)
        var i = n
        while (i > 0 && offs[i - 1] > o) { offs[i] = offs[i - 1]; cols[i] = cols[i - 1]; i-- }
        offs[i] = o; cols[i] = CssColor.parse(str(color)); n++
    }

    private var cached: Shader? = null; private var cachedN = -1
    private val cachedM = FloatArray(9); private val curM = FloatArray(9)
    fun shader(m: Matrix): Shader? {
        if (n == 0) return null
        m.getValues(curM)
        if (cachedN == n && cached != null && curM.contentEquals(cachedM)) return cached!!.also { it.setLocalMatrix(m) } // text drawing may have reset it
        val s = build(m)
        cached = s; cachedN = n; System.arraycopy(curM, 0, cachedM, 0, 9)
        return s
    }
    private fun build(m: Matrix): Shader? {
        var p = offs.copyOf(n); var k = cols.copyOf(n)
        if (n == 1) { p = floatArrayOf(0f, 1f); k = intArrayOf(k[0], k[0]) }
        val s: Shader = if (!radial) {
            if (c[0] == c[2] && c[1] == c[3]) return null
            LinearGradient(c[0].toFloat(), c[1].toFloat(), c[2].toFloat(), c[3].toFloat(), k, p, Shader.TileMode.CLAMP)
        } else {
            val r0 = c[2].coerceAtLeast(0.0); val r1 = c[5].coerceAtLeast(0.0)
            if (r1 <= 0.0) return null
            if (android.os.Build.VERSION.SDK_INT >= 31) {
                // exact two-circle gradient, like the browser
                val lc = LongArray(k.size) { android.graphics.Color.pack(k[it]) }
                val g = RadialGradient(c[0].toFloat(), c[1].toFloat(), r0.toFloat(), c[3].toFloat(), c[4].toFloat(), r1.toFloat(), lc, p, Shader.TileMode.CLAMP)
                g.setLocalMatrix(m)
                return g
            }
            // two-circle gradient approximated around the end circle: offset t maps to radius r0 + t (r1 - r0)
            val q = FloatArray(p.size) { ((r0 + p[it] * (r1 - r0)) / r1).toFloat().coerceIn(0f, 1f) }
            for (i in 1 until q.size) if (q[i] < q[i - 1]) q[i] = q[i - 1]
            val pos: FloatArray; val col: IntArray
            if (q[0] > 0f) { pos = FloatArray(q.size + 1); col = IntArray(q.size + 1); pos[0] = 0f; col[0] = k[0]; System.arraycopy(q, 0, pos, 1, q.size); System.arraycopy(k, 0, col, 1, k.size) } else { pos = q; col = k }
            // pre-12: one centre. Put it on the inner circle (where the highlight is) and stretch to reach the outer edge.
            val dist = Math.hypot(c[3] - c[0], c[4] - c[1])
            RadialGradient(c[0].toFloat(), c[1].toFloat(), (r1 + dist * 0.5).toFloat(), col, pos, Shader.TileMode.CLAMP)
        }
        s.setLocalMatrix(m)
        return s
    }
}

class CanvasInfo { @JvmField var width = 0.0; @JvmField var height = 0.0 }

/**
 * A CanvasRenderingContext2D on top of android.graphics.Canvas.
 * Paths are built in device space (transform applied at call time, like the web), then drawn untransformed.
 */
class Ctx {
    private var canvas: Canvas? = null
    private val base = Matrix()
    private val m = Matrix()
    private val pts = FloatArray(8)
    private val vals = FloatArray(9)
    @JvmField val canvasInfo = CanvasInfo()

    private class State(
        var fill: Any?, var stroke: Any?, var lw: Double, var alpha: Double, var gco: String, var font: String,
        var align: String, var baseline: String, var cap: String, var join: String, val matrix: Matrix, var shadowBlur: Double, var shadowColor: Any?,
    )
    private val stack = ArrayList<State>()

    var fillStyle: Any? = "#000"
    var strokeStyle: Any? = "#000"
    var lineWidth = 1.0
        set(v) { if (v > 0 && v.isFinite()) field = v }
    var globalAlpha = 1.0
        set(v) { if (v >= 0 && v <= 1) field = v else if (v > 1) field = 1.0 else if (v < 0) field = 0.0 }
    var globalCompositeOperation = "source-over"
    var font = "10px sans-serif"
    var textAlign = "start"
    var textBaseline = "alphabetic"
    var lineCap = "butt"
    var lineJoin = "miter"
    var shadowColor: Any? = "rgba(0,0,0,0)"
    var shadowBlur = 0.0
    var shadowOffsetX = 0.0
    var shadowOffsetY = 0.0
    var filter = "none"
    var imageSmoothingEnabled = true
    var imageSmoothingQuality = "low"
    var miterLimit = 10.0
    var lineDashOffset = 0.0

    private val path = Path()
    private var hasCur = false
    private var curX = 0f; private var curY = 0f
    private var startX = 0f; private var startY = 0f
    private var evenOdd = false

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    private val tmpPath = Path()
    private var saveCount = 0

    /** Start a frame: width/height in CSS pixels; [scale] maps CSS px to device px. */
    fun begin(c: Canvas, width: Double, height: Double, scale: Float) {
        canvas = c
        canvasInfo.width = width; canvasInfo.height = height
        base.reset(); base.setScale(scale, scale)
        m.set(base)
        stack.clear()
        fillStyle = "#000"; strokeStyle = "#000"; lineWidth = 1.0; globalAlpha = 1.0; globalCompositeOperation = "source-over"
        font = "10px sans-serif"; textAlign = "start"; textBaseline = "alphabetic"; lineCap = "butt"; lineJoin = "miter"; shadowBlur = 0.0
        path.reset(); hasCur = false
        saveCount = c.save()
    }

    /** Bind to an offscreen bitmap canvas once (site canvas element); state persists across frames like the web. */
    fun attach(c: Canvas, width: Double, height: Double) {
        canvas = c; canvasInfo.width = width; canvasInfo.height = height
        base.reset(); m.set(base); stack.clear(); path.reset(); hasCur = false
    }

    fun end() {
        canvas?.restoreToCount(saveCount)
        canvas = null
    }

    // ------------------------------------------------------------ state
    fun save() {
        stack.add(State(fillStyle, strokeStyle, lineWidth, globalAlpha, globalCompositeOperation, font, textAlign, textBaseline, lineCap, lineJoin, Matrix(m), shadowBlur, shadowColor))
        canvas?.save()
    }

    fun restore() {
        if (stack.isEmpty()) return
        val s = stack.removeAt(stack.size - 1)
        fillStyle = s.fill; strokeStyle = s.stroke; lineWidth = s.lw; globalAlpha = s.alpha; globalCompositeOperation = s.gco
        font = s.font; textAlign = s.align; textBaseline = s.baseline; lineCap = s.cap; lineJoin = s.join; m.set(s.matrix)
        shadowBlur = s.shadowBlur; shadowColor = s.shadowColor
        canvas?.restore()
    }

    fun translate(x: Double, y: Double) { if (x.isFinite() && y.isFinite()) m.preTranslate(x.toFloat(), y.toFloat()) }
    fun rotate(a: Double) { if (a.isFinite()) m.preRotate(Math.toDegrees(a).toFloat()) }
    fun scale(x: Double, y: Double) { if (x.isFinite() && y.isFinite()) m.preScale(x.toFloat(), y.toFloat()) }
    private val tm = Matrix(); private val tv = FloatArray(9)
    private fun tmOf(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double): Matrix {
        tv[0] = a.toFloat(); tv[1] = c.toFloat(); tv[2] = e.toFloat(); tv[3] = b.toFloat(); tv[4] = d.toFloat(); tv[5] = f.toFloat(); tv[6] = 0f; tv[7] = 0f; tv[8] = 1f
        tm.setValues(tv); return tm
    }
    fun setTransform(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double) {
        val t = tmOf(a, b, c, d, e, f)
        m.set(base); m.preConcat(t)
    }
    fun transform(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double) {
        m.preConcat(tmOf(a, b, c, d, e, f))
    }
    fun resetTransform() { m.set(base) }

    private fun mapX(x: Double, y: Double): Boolean {
        pts[0] = x.toFloat(); pts[1] = y.toFloat()
        if (!pts[0].isFinite() || !pts[1].isFinite()) return false
        m.mapPoints(pts, 0, pts, 0, 1)
        return true
    }

    private fun scaleFactor(): Float {
        m.getValues(vals)
        val det = vals[Matrix.MSCALE_X] * vals[Matrix.MSCALE_Y] - vals[Matrix.MSKEW_X] * vals[Matrix.MSKEW_Y]
        return sqrt(abs(det))
    }

    /** True when the transform keeps circles circular (no skew, uniform scale; rotation allowed). */
    private fun conformal(): Boolean {
        m.getValues(vals)
        val a = vals[Matrix.MSCALE_X]; val b = vals[Matrix.MSKEW_Y]; val c = vals[Matrix.MSKEW_X]; val d = vals[Matrix.MSCALE_Y]
        return abs(a - d) < 1e-4f && abs(b + c) < 1e-4f
    }

    // ------------------------------------------------------------ path
    fun beginPath() { path.reset(); hasCur = false }
    fun closePath() { if (hasCur) { path.close(); curX = startX; curY = startY } }
    fun moveTo(x: Double, y: Double) {
        if (!mapX(x, y)) return
        path.moveTo(pts[0], pts[1]); curX = pts[0]; curY = pts[1]; startX = curX; startY = curY; hasCur = true
    }
    fun lineTo(x: Double, y: Double) {
        if (!mapX(x, y)) return
        if (!hasCur) { path.moveTo(pts[0], pts[1]); startX = pts[0]; startY = pts[1]; hasCur = true } else path.lineTo(pts[0], pts[1])
        curX = pts[0]; curY = pts[1]
    }
    fun quadraticCurveTo(cx: Double, cy: Double, x: Double, y: Double) {
        pts[0] = cx.toFloat(); pts[1] = cy.toFloat(); pts[2] = x.toFloat(); pts[3] = y.toFloat()
        if (pts.take(4).any { !it.isFinite() }) return
        m.mapPoints(pts, 0, pts, 0, 2)
        if (!hasCur) { path.moveTo(pts[0], pts[1]); startX = pts[0]; startY = pts[1]; hasCur = true }
        path.quadTo(pts[0], pts[1], pts[2], pts[3]); curX = pts[2]; curY = pts[3]
    }
    fun bezierCurveTo(c1x: Double, c1y: Double, c2x: Double, c2y: Double, x: Double, y: Double) {
        val p = floatArrayOf(c1x.toFloat(), c1y.toFloat(), c2x.toFloat(), c2y.toFloat(), x.toFloat(), y.toFloat())
        if (p.any { !it.isFinite() }) return
        m.mapPoints(p)
        if (!hasCur) { path.moveTo(p[0], p[1]); startX = p[0]; startY = p[1]; hasCur = true }
        path.cubicTo(p[0], p[1], p[2], p[3], p[4], p[5]); curX = p[4]; curY = p[5]
    }
    fun rect(x: Double, y: Double, w: Double, h: Double) {
        moveTo(x, y); lineTo(x + w, y); lineTo(x + w, y + h); lineTo(x, y + h); closePath()
        moveTo(x, y)
    }
    fun roundRect(x: Double, y: Double, w: Double, h: Double, r: Any?) {
        val rr = minOf(abs(num(if (r is JsArr) r.firstOrNull() else r)).let { if (it.isNaN()) 0.0 else it }, abs(w) / 2, abs(h) / 2)
        moveTo(x + rr, y); lineTo(x + w - rr, y); arcTo(x + w, y, x + w, y + rr, rr); lineTo(x + w, y + h - rr)
        arcTo(x + w, y + h, x + w - rr, y + h, rr); lineTo(x + rr, y + h); arcTo(x, y + h, x, y + h - rr, rr)
        lineTo(x, y + rr); arcTo(x, y, x + rr, y, rr); closePath()
    }

    /** Chromium's arc angle canonicalisation (third_party/blink canvas path). */
    private fun sweepOf(a0: Double, a1: Double, ccw: Boolean): Double {
        val tau = Math.PI * 2
        return when {
            !ccw && a1 - a0 >= tau -> tau
            ccw && a0 - a1 >= tau -> -tau
            !ccw && a0 > a1 -> tau - ((a0 - a1) % tau)
            ccw && a0 < a1 -> -(tau - ((a1 - a0) % tau))
            else -> a1 - a0
        }
    }

    fun arc(x: Double, y: Double, r: Double, a0: Double, a1: Double, ccw: Boolean = false) {
        if (!x.isFinite() || !y.isFinite() || !r.isFinite() || !a0.isFinite() || !a1.isFinite() || r < 0) return
        ellipseSeg(x, y, r, r, 0.0, a0, sweepOf(a0, a1, ccw))
    }

    fun ellipse(x: Double, y: Double, rx: Double, ry: Double, rot: Double, a0: Double, a1: Double, ccw: Boolean = false) {
        if (!x.isFinite() || !y.isFinite() || !rx.isFinite() || !ry.isFinite() || !a0.isFinite() || !a1.isFinite() || rx < 0 || ry < 0) return
        ellipseSeg(x, y, rx, ry, rot, a0, sweepOf(a0, a1, ccw))
    }

    private fun ellipseSeg(x: Double, y: Double, rx: Double, ry: Double, rot: Double, a0: Double, sweep: Double) {
        val sf = scaleFactor()
        // fast path: conformal transform, unrotated ellipse -> native arc
        if (rot == 0.0 && conformal()) {
            m.getValues(vals)
            val rotDeg = Math.toDegrees(Math.atan2(vals[Matrix.MSKEW_Y].toDouble(), vals[Matrix.MSCALE_X].toDouble()))
            if (abs(rotDeg) < 1e-6 || rx == ry) {
                mapX(x, y)
                val cx = pts[0]; val cy = pts[1]
                val rxs = (rx * sf).toFloat(); val rys = (ry * sf).toFloat()
                rectF.set(cx - rxs, cy - rys, cx + rxs, cy + rys)
                val startDeg = (Math.toDegrees(a0) + rotDeg).toFloat()
                val sw = Math.toDegrees(sweep).toFloat()
                if (abs(sw) >= 360f) {
                    val half = if (sw > 0) 180f else -180f
                    path.arcTo(rectF, startDeg, half, !hasCur)
                    path.arcTo(rectF, startDeg + half, half, false)
                } else path.arcTo(rectF, startDeg, sw, !hasCur)
                val endA = a0 + sweep
                mapPointOnEllipse(x, y, rx, ry, 0.0, endA)
                if (!hasCur) { startX = pts[0]; startY = pts[1] }
                hasCur = true; curX = pts[0]; curY = pts[1]
                return
            }
        }
        val rMax = maxOf(rx, ry) * sf
        val segs = ceil(abs(sweep) * rMax / 3.0).toInt().coerceIn(4, 160)
        for (i in 0..segs) {
            val a = a0 + sweep * i / segs
            mapPointOnEllipse(x, y, rx, ry, rot, a)
            if (i == 0 && !hasCur) { path.moveTo(pts[0], pts[1]); startX = pts[0]; startY = pts[1]; hasCur = true }
            else path.lineTo(pts[0], pts[1])
        }
        curX = pts[0]; curY = pts[1]
    }

    private fun mapPointOnEllipse(x: Double, y: Double, rx: Double, ry: Double, rot: Double, a: Double) {
        val ex = rx * cos(a); val ey = ry * sin(a)
        val px = x + ex * cos(rot) - ey * sin(rot); val py = y + ex * sin(rot) + ey * cos(rot)
        pts[0] = px.toFloat(); pts[1] = py.toFloat(); m.mapPoints(pts, 0, pts, 0, 1)
    }

    private val arcPt = FloatArray(2)
    fun arcTo(x1: Double, y1: Double, x2: Double, y2: Double, r: Double) {
        // tangent arc between current point and two lines (device space approximation in user space)
        if (!hasCur) { moveTo(x1, y1); return }
        val inv = tm; m.invert(inv)
        val p = arcPt; p[0] = curX; p[1] = curY; inv.mapPoints(p)
        val x0 = p[0].toDouble(); val y0 = p[1].toDouble()
        val v1x = x0 - x1; val v1y = y0 - y1; val v2x = x2 - x1; val v2y = y2 - y1
        val l1 = Math.hypot(v1x, v1y); val l2 = Math.hypot(v2x, v2y)
        if (l1 == 0.0 || l2 == 0.0 || r == 0.0) { lineTo(x1, y1); return }
        val cosA = (v1x * v2x + v1y * v2y) / (l1 * l2)
        val ang = Math.acos(cosA.coerceIn(-1.0, 1.0))
        if (ang == 0.0 || ang == Math.PI) { lineTo(x1, y1); return }
        val dist = r / Math.tan(ang / 2)
        val t1x = x1 + v1x / l1 * dist; val t1y = y1 + v1y / l1 * dist
        val t2x = x1 + v2x / l2 * dist; val t2y = y1 + v2y / l2 * dist
        val bx = v1x / l1 + v2x / l2; val by = v1y / l1 + v2y / l2; val bl = Math.hypot(bx, by)
        val cd = r / Math.sin(ang / 2)
        val cx = x1 + bx / bl * cd; val cy = y1 + by / bl * cd
        val s = Math.atan2(t1y - cy, t1x - cx); val e = Math.atan2(t2y - cy, t2x - cx)
        val cross = v1x * v2y - v1y * v2x
        lineTo(t1x, t1y)
        ellipseSeg(cx, cy, r, r, 0.0, s, sweepOf(s, e, cross > 0))
    }

    // ------------------------------------------------------------ painting
    private fun applyComposite(p: Paint) {
        p.xfermode = when (globalCompositeOperation) {
            "lighter" -> PorterDuffXfermode(PorterDuff.Mode.ADD)
            "screen" -> PorterDuffXfermode(PorterDuff.Mode.SCREEN)
            "multiply" -> PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
            "overlay" -> PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
            "darken" -> PorterDuffXfermode(PorterDuff.Mode.DARKEN)
            "lighten" -> PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
            "destination-out" -> PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            "destination-in" -> PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            "source-atop" -> PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
            "destination-over" -> PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
            "xor" -> PorterDuffXfermode(PorterDuff.Mode.XOR)
            "copy" -> PorterDuffXfermode(PorterDuff.Mode.SRC)
            else -> null
        }
    }

    private fun setupPaint(p: Paint, style: Any?): Boolean {
        val a = globalAlpha
        if (a <= 0.0) return false
        if (style is Pat) {
            val sh = BitmapShader(style.bmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
            sh.setLocalMatrix(Matrix(m))
            p.shader = sh
            p.color = 0xFF000000.toInt()
            p.alpha = (a * 255).toInt().coerceIn(0, 255)
        } else if (style is Grad) {
            val sh = style.shader(m) ?: return false
            p.shader = sh
            p.color = 0xFF000000.toInt()
            p.alpha = (a * 255).toInt().coerceIn(0, 255)
        } else {
            p.shader = null
            val c = CssColor.parse(str(style))
            val ca = (c ushr 24) * a
            if (ca < 0.5) return false
            p.color = (c and 0x00FFFFFF) or (ca.toInt().coerceIn(0, 255) shl 24)
        }
        applyComposite(p)
        // canvas shadowBlur ≈ 2σ; Android's radius r gives σ = 0.57735 r + 0.5
        if (shadowBlur > 0.0) {
            val sc = CssColor.parse(str(shadowColor))
            val sa = ((sc ushr 24) * a).toInt().coerceIn(0, 255)
            if (sa > 0) {
                val sigma = shadowBlur / 2 * scaleFactor()
                val r = ((sigma - 0.5) / 0.57735).toFloat().coerceAtLeast(0.5f)
                p.setShadowLayer(r, (shadowOffsetX * scaleFactor()).toFloat(), (shadowOffsetY * scaleFactor()).toFloat(), (sc and 0x00FFFFFF) or (sa shl 24))
            } else p.clearShadowLayer()
        } else p.clearShadowLayer()
        return true
    }

    fun fill(rule: String = "nonzero") {
        val c = canvas ?: return
        if (!setupPaint(fillPaint, fillStyle)) return
        path.fillType = if (rule == "evenodd") Path.FillType.EVEN_ODD else Path.FillType.WINDING
        c.drawPath(path, fillPaint)
    }

    fun stroke() {
        val c = canvas ?: return
        if (!setupPaint(strokePaint, strokeStyle)) return
        strokePaint.strokeWidth = (lineWidth * scaleFactor()).toFloat()
        strokePaint.strokeCap = when (lineCap) { "round" -> Paint.Cap.ROUND; "square" -> Paint.Cap.SQUARE; else -> Paint.Cap.BUTT }
        strokePaint.strokeJoin = when (lineJoin) { "round" -> Paint.Join.ROUND; "bevel" -> Paint.Join.BEVEL; else -> Paint.Join.MITER }
        strokePaint.strokeMiter = miterLimit.toFloat()
        val sf = scaleFactor()
        strokePaint.pathEffect = dash?.let { dd ->
            if (dd !== dashFor || sf != dashSf) { dashFor = dd; dashSf = sf; dashFx = android.graphics.DashPathEffect(FloatArray(dd.size) { (dd[it] * sf).toFloat().coerceAtLeast(0.01f) }, 0f) }
            dashFx
        }
        c.drawPath(path, strokePaint)
        strokePaint.pathEffect = null
    }

    private var dashFor: DoubleArray? = null; private var dashSf = 0f; private var dashFx: android.graphics.PathEffect? = null
    fun clip(rule: String = "nonzero") {
        val c = canvas ?: return
        path.fillType = if (rule == "evenodd") Path.FillType.EVEN_ODD else Path.FillType.WINDING
        c.clipPath(path)
    }

    private fun rectPath(x: Double, y: Double, w: Double, h: Double): Boolean {
        tmpPath.reset()
        val p = floatArrayOf(x.toFloat(), y.toFloat(), (x + w).toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), x.toFloat(), (y + h).toFloat())
        if (p.any { !it.isFinite() }) return false
        m.mapPoints(p)
        tmpPath.moveTo(p[0], p[1]); tmpPath.lineTo(p[2], p[3]); tmpPath.lineTo(p[4], p[5]); tmpPath.lineTo(p[6], p[7]); tmpPath.close()
        return true
    }

    fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        val c = canvas ?: return
        if (w == 0.0 || h == 0.0) return
        if (!setupPaint(fillPaint, fillStyle)) return
        m.getValues(vals)
        if (vals[Matrix.MSKEW_X] == 0f && vals[Matrix.MSKEW_Y] == 0f) {
            pts[0] = x.toFloat(); pts[1] = y.toFloat(); pts[2] = (x + w).toFloat(); pts[3] = (y + h).toFloat()
            if (!pts[0].isFinite() || !pts[1].isFinite() || !pts[2].isFinite() || !pts[3].isFinite()) return
            m.mapPoints(pts, 0, pts, 0, 2)
            c.drawRect(minOf(pts[0], pts[2]), minOf(pts[1], pts[3]), maxOf(pts[0], pts[2]), maxOf(pts[1], pts[3]), fillPaint)
            return
        }
        if (!rectPath(x, y, w, h)) return
        tmpPath.fillType = Path.FillType.WINDING
        c.drawPath(tmpPath, fillPaint)
    }

    fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        val c = canvas ?: return
        if (!setupPaint(strokePaint, strokeStyle)) return
        strokePaint.strokeWidth = (lineWidth * scaleFactor()).toFloat()
        strokePaint.strokeJoin = Paint.Join.MITER
        if (!rectPath(x, y, w, h)) return
        c.drawPath(tmpPath, strokePaint)
    }

    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    fun clearRect(x: Double, y: Double, w: Double, h: Double) {
        val c = canvas ?: return
        if (!rectPath(x, y, w, h)) return
        c.drawPath(tmpPath, clearPaint)
    }

    fun createLinearGradient(x0: Double, y0: Double, x1: Double, y1: Double) = Grad(false, doubleArrayOf(x0, y0, x1, y1).fix())
    fun createRadialGradient(x0: Double, y0: Double, r0: Double, x1: Double, y1: Double, r1: Double) = Grad(true, doubleArrayOf(x0, y0, r0, x1, y1, r1).fix())
    private fun DoubleArray.fix(): DoubleArray { for (i in indices) if (!this[i].isFinite()) this[i] = 0.0; return this }

    // ------------------------------------------------------------ text
    private fun setupFont(p: Paint) {
        val f = font
        val size = Regex("(\\d+(?:\\.\\d+)?)px").find(f)?.groupValues?.get(1)?.toFloatOrNull() ?: 10f
        val bold = f.contains("bold") || Regex("\\b[6-9]00\\b").containsMatchIn(f)
        val mono = f.contains("mono", true) || f.contains("courier", true) || f.contains("consol", true)
        val serif = !mono && f.contains("serif", true) && !f.contains("sans", true)
        val family = if (mono) Typeface.MONOSPACE else if (serif) Typeface.SERIF else Typeface.SANS_SERIF
        p.typeface = Typeface.create(family, if (bold) Typeface.BOLD else Typeface.NORMAL)
        p.textSize = size
    }

    private fun drawTextImpl(s: String, x: Double, y: Double, strokeIt: Boolean) {
        val c = canvas ?: return
        val p = textPaint
        if (!setupPaint(p, if (strokeIt) strokeStyle else fillStyle)) return
        p.style = if (strokeIt) Paint.Style.STROKE else Paint.Style.FILL
        if (strokeIt) p.strokeWidth = lineWidth.toFloat()
        setupFont(p)
        // gradients were resolved for device space; text is drawn in user space below
        p.shader?.setLocalMatrix(null)
        p.textAlign = when (textAlign) { "center" -> Paint.Align.CENTER; "right", "end" -> Paint.Align.RIGHT; else -> Paint.Align.LEFT }
        val fm = p.fontMetrics
        val dy = when (textBaseline) { "top", "hanging" -> -fm.ascent; "middle" -> -(fm.ascent + fm.descent) / 2; "bottom", "ideographic" -> -fm.descent; else -> 0f }
        c.save(); c.concat(m)
        c.drawText(s, x.toFloat(), (y + dy).toFloat(), p)
        c.restore()
    }

    fun fillText(s: String, x: Double, y: Double, maxWidth: Double = Double.NaN) = drawTextImpl(s, x, y, false)
    fun strokeText(s: String, x: Double, y: Double, maxWidth: Double = Double.NaN) = drawTextImpl(s, x, y, true)
    fun measureText(s: String): Any? { setupFont(textPaint); return jsObj("width" to textPaint.measureText(s).toDouble()) }
    private var dash: DoubleArray? = null
    /** setLineDash([...]): an empty list turns dashing off; odd lists repeat like the canvas spec. */
    fun setLineDash(d: Any?) {
        val l = (d as? JsArr)?.map { num(it) }?.filter { it.isFinite() && it >= 0 } ?: emptyList()
        dash = if (l.isEmpty() || l.all { it == 0.0 }) null else (if (l.size % 2 == 1) l + l else l).toDoubleArray()
    }
    private val imgPaint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val imgDst = RectF(); private val imgSrc = Rect()
    /** drawImage(canvasElement, …) in its 3-, 5- and 9-argument forms, through the current transform. */
    fun drawImage(img: Any?, a: Double = 0.0, b: Double = 0.0, c: Double = 0.0, d: Double = 0.0, e: Double = 0.0, f: Double = 0.0, g: Double = 0.0, h: Double = 0.0) {
        val cv = canvas ?: return
        val bm = (img as? JsEl)?.bitmap ?: return
        if (globalAlpha <= 0.0) return
        var src: Rect? = null
        val dst = imgDst
        if (g != 0.0 || h != 0.0) { src = imgSrc; src.set(a.toInt(), b.toInt(), (a + c).toInt(), (b + d).toInt()); dst.set(e.toFloat(), f.toFloat(), (e + g).toFloat(), (f + h).toFloat()) }
        else if (c != 0.0 || d != 0.0) dst.set(a.toFloat(), b.toFloat(), (a + c).toFloat(), (b + d).toFloat())
        else dst.set(a.toFloat(), b.toFloat(), (a + bm.width).toFloat(), (b + bm.height).toFloat())
        val p = imgPaint
        p.shader = null
        p.isFilterBitmap = imageSmoothingEnabled
        p.alpha = (globalAlpha * 255).toInt().coerceIn(0, 255)
        applyComposite(p)
        cv.save(); cv.concat(m); cv.drawBitmap(bm, src, dst, p); cv.restore()
    }

    fun createPattern(img: Any?, rep: String): Any? = (img as? JsEl)?.bitmap?.let { Pat(it) }
    fun createImageData(w: Double, h: Double): Any? {
        val wi = maxOf(1, w.toInt()); val hi = maxOf(1, h.toInt())
        return jsObj("width" to wi.toDouble(), "height" to hi.toDouble(), "data" to DoubleArray(wi * hi * 4))
    }
    fun getImageData(x: Double, y: Double, w: Double, h: Double): Any? = createImageData(w, h)
    private var putPx = IntArray(0); private var putBmp: Bitmap? = null
    private val putPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC) }
    /** putImageData: raw pixels, no transform, no alpha, replacing what is there. */
    fun putImageData(img: Any?, x: Double, y: Double) {
        val cv = canvas ?: return
        val w = num(jsGet(img, "width")).toInt(); val h = num(jsGet(img, "height")).toInt()
        val data = jsGet(img, "data") as? DoubleArray ?: return
        if (w <= 0 || h <= 0) return
        if (putPx.size != w * h) putPx = IntArray(w * h)
        val px = putPx
        for (i in 0 until w * h) {
            val k = i * 4
            fun ch(v: Double) = v.toInt().coerceIn(0, 255)
            px[i] = (ch(data[k + 3]) shl 24) or (ch(data[k]) shl 16) or (ch(data[k + 1]) shl 8) or ch(data[k + 2])
        }
        val bm = putBmp?.takeIf { it.width == w && it.height == h } ?: Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).also { putBmp = it }
        bm.setPixels(px, 0, w, 0, 0, w, h)
        val sf = scaleFactor()
        val dst = imgDst.apply { set((x * sf).toFloat(), (y * sf).toFloat(), ((x + w) * sf).toFloat(), ((y + h) * sf).toFloat()) }
        cv.drawBitmap(bm, null, dst, putPaint)
    }

    /** Glitch band: the site copies a strip of the canvas onto itself shifted sideways; approximated with a tinted offset. */
    fun shiftBand(x: Double, y: Double, w: Double, h: Double, dx: Double) {}
}
