package com.reverender.r95.os

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.view.View
import com.reverender.r95.audio.AudioBus
import com.reverender.r95.core.Themes
import com.reverender.r95.ui.viz.site.SceneClient
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Full-screen CRT saver used by the dock screensaver and the external monitor:
 * ANALOG_VU twin needles, LISSAJOUS phosphor scope, the WIN95 3D maze, or one of the site's wallpapers
 * ("wp:<id>"), in any of the colour themes, under a scanline / curvature glass.
 */
class SaverView(ctx: Context, var mode: String, var themeId: String) : View(ctx) {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND }
    private val path = Path()
    private val t0 = System.nanoTime()
    private var last = 0L
    private var scene: SceneClient? = null
    private var vuL = 0f; private var vuR = 0f; private var peakL = 0f; private var peakR = 0f
    private val trail = ArrayList<FloatArray>()
    var crt = true

    private fun col(v: Long, a: Int = 255) = ((a shl 24) or (v.toInt() and 0xFFFFFF))

    override fun onDraw(c: Canvas) {
        val now = System.nanoTime()
        val th = Themes.byId(themeId)
        val t = (now - t0) / 1e9f
        val w = width.toFloat(); val h = height.toFloat()
        val f = AudioBus.currentFrame(now)
        when {
            mode.startsWith("wp:") -> drawScene(c, mode.removePrefix("wp:").toIntOrNull() ?: 5)
            mode == "lissajous" -> drawScope(c, w, h, th, f)
            mode == "maze" -> drawMaze(c, w, h, th, t, f.bass)
            else -> drawVu(c, w, h, th, f)
        }
        if (crt) drawGlass(c, w, h, t)
        // ~30 fps is plenty for a saver and keeps a docked phone cool
        val wait = 33L - (System.nanoTime() - now) / 1_000_000L
        postInvalidateDelayed(wait.coerceAtLeast(1L))
        last = now
    }

    // ------------------------------------------------------------ site wallpaper through the wallpaper thread
    private fun drawScene(c: Canvas, id: Int) {
        val s = scene ?: SceneClient().also { scene = it }
        s.latest?.let { c.drawPicture(it) } ?: c.drawColor(0xFF000000.toInt())
        s.requestPicture(width, height, resources.displayMetrics.density, id, themeId) { }
    }

    // ------------------------------------------------------------ ANALOG_VU
    private fun drawVu(c: Canvas, w: Float, h: Float, th: com.reverender.r95.core.VaporTheme, f: com.reverender.r95.audio.AudioFrame) {
        c.drawColor(0xFF07040F.toInt())
        vuL += ((f.rmsL * 3.2f).coerceIn(0f, 1.05f) - vuL) * 0.25f
        vuR += ((f.rmsR * 3.2f).coerceIn(0f, 1.05f) - vuR) * 0.25f
        peakL = maxOf(vuL, peakL - 0.006f); peakR = maxOf(vuR, peakR - 0.006f)
        val portrait = h > w
        val mw = if (portrait) w * 0.86f else w * 0.43f
        val mh = mw * 0.62f
        for (k in 0..1) {
            val cx = if (portrait) w / 2 else w * (0.27f + 0.46f * k)
            val cy = if (portrait) h * (0.3f + 0.42f * k) else h * 0.5f
            val left = cx - mw / 2; val top = cy - mh / 2
            // backlit face
            p.shader = LinearGradient(0f, top, 0f, top + mh, col(th.accent, 255), col(th.sky[4], 255), Shader.TileMode.CLAMP)
            c.drawRoundRect(left, top, left + mw, top + mh, mw * 0.04f, mw * 0.04f, p)
            p.shader = null
            val pivotX = cx; val pivotY = top + mh * 0.92f; val r = mh * 0.78f
            // scale: -20 … +3 dB, red above 0
            line.strokeWidth = mw * 0.006f
            for (i in 0..20) {
                val a = (-50 + i * 5) * PI.toFloat() / 180f
                val red = i >= 15
                line.color = if (red) 0xFFD01818.toInt() else 0xFF20202A.toInt()
                val r0 = r * (if (i % 5 == 0) 0.86f else 0.9f)
                c.drawLine(pivotX + sin(a) * r0, pivotY - cos(a) * r0, pivotX + sin(a) * r, pivotY - cos(a) * r, line)
            }
            p.color = 0xFF20202A.toInt(); p.textSize = mw * 0.06f; p.textAlign = Paint.Align.CENTER; p.isFakeBoldText = true
            c.drawText("VU", cx, top + mh * 0.62f, p)
            p.textSize = mw * 0.045f
            c.drawText(if (k == 0) "LEFT" else "RIGHT", cx, top + mh * 0.75f, p)
            val v = if (k == 0) vuL else vuR; val pk = if (k == 0) peakL else peakR
            val a = (-50 + v * 100) * PI.toFloat() / 180f
            line.color = 0xFF111111.toInt(); line.strokeWidth = mw * 0.012f
            c.drawLine(pivotX, pivotY, pivotX + sin(a) * r * 0.97f, pivotY - cos(a) * r * 0.97f, line)
            val pa = (-50 + pk * 100) * PI.toFloat() / 180f
            p.color = col(th.secondary); c.drawCircle(pivotX + sin(pa) * r * 0.97f, pivotY - cos(pa) * r * 0.97f, mw * 0.012f, p)
            p.color = 0xFF111111.toInt(); c.drawCircle(pivotX, pivotY, mw * 0.03f, p)
            // peak LED
            p.color = if (v > 0.75f) 0xFFFF2040.toInt() else 0xFF401010.toInt()
            c.drawCircle(left + mw * 0.9f, top + mh * 0.14f, mw * 0.022f, p)
            line.color = col(th.primary, 200); line.strokeWidth = mw * 0.01f
            c.drawRoundRect(left, top, left + mw, top + mh, mw * 0.04f, mw * 0.04f, line)
        }
    }

    // ------------------------------------------------------------ LISSAJOUS
    private fun drawScope(c: Canvas, w: Float, h: Float, th: com.reverender.r95.core.VaporTheme, f: com.reverender.r95.audio.AudioFrame) {
        c.drawColor(0xFF020806.toInt())
        val cx = w / 2; val cy = h / 2; val s = minOf(w, h) * 0.42f
        line.color = col(th.primary, 40); line.strokeWidth = 1.5f
        for (i in -4..4) { c.drawLine(cx + i * s / 4, cy - s, cx + i * s / 4, cy + s, line); c.drawLine(cx - s, cy + i * s / 4, cx + s, cy + i * s / 4, line) }
        val pts = FloatArray(f.left.size * 2)
        for (i in f.left.indices) {
            // 45° rotated X/Y, as on a real scope fed L and R
            val l = f.left[i]; val r = f.right[i]
            pts[i * 2] = cx + (l - r) * 0.707f * s * 1.6f
            pts[i * 2 + 1] = cy - (l + r) * 0.707f * s * 1.6f
        }
        trail.add(0, pts); while (trail.size > 6) trail.removeAt(trail.size - 1)
        trail.forEachIndexed { k, q ->
            path.reset()
            for (i in 0 until q.size / 2) if (i == 0) path.moveTo(q[0], q[1]) else path.lineTo(q[i * 2], q[i * 2 + 1])
            line.color = col(if (k == 0) th.primary else th.secondary, (220 / (k + 1)).coerceAtLeast(20))
            line.strokeWidth = if (k == 0) 3f else 2f
            c.drawPath(path, line)
        }
    }

    // ------------------------------------------------------------ WIN95 3D MAZE
    private val N = 13
    private val maze: Array<BooleanArray> by lazy { genMaze() }
    private var px = 1.5; private var py = 1.5; private var dir = 0 // 0 E,1 S,2 W,3 N
    private var angle = 0.0; private var stepT = 0.0

    private fun genMaze(): Array<BooleanArray> {
        val g = Array(N) { BooleanArray(N) { true } } // true = wall
        val rnd = java.util.Random()
        fun carve(x: Int, y: Int) {
            g[y][x] = false
            val d = mutableListOf(0 to -2, 2 to 0, 0 to 2, -2 to 0); d.shuffle(rnd)
            for ((dx, dy) in d) { val nx = x + dx; val ny = y + dy
                if (nx in 1 until N - 1 && ny in 1 until N - 1 && g[ny][nx]) { g[y + dy / 2][x + dx / 2] = false; carve(nx, ny) } }
        }
        carve(1, 1)
        return g
    }
    private fun wall(x: Int, y: Int) = x !in 0 until N || y !in 0 until N || maze[y][x]
    private val DX = intArrayOf(1, 0, -1, 0); private val DY = intArrayOf(0, 1, 0, -1)

    private fun stepWalker(dt: Double) {
        // right-hand rule, gliding between cells like the old saver
        val target = dir * PI / 2
        var da = target - angle
        while (da > PI) da -= 2 * PI; while (da < -PI) da += 2 * PI
        if (abs(da) > 0.01) { angle += da.coerceIn(-dt * 2.4, dt * 2.4); return }
        angle = target
        stepT += dt * 1.6
        val cxI = floor(px).toInt(); val cyI = floor(py).toInt()
        val tx = cxI + DX[dir] + 0.5; val ty = cyI + DY[dir] + 0.5
        if (wall(cxI + DX[dir], cyI + DY[dir])) { dir = (dir + 3) % 4; return }
        val sp = dt * 1.6
        px += DX[dir] * sp; py += DY[dir] * sp
        if ((tx - px) * DX[dir] + (ty - py) * DY[dir] <= 0) {
            px = tx; py = ty
            val r = (dir + 1) % 4
            dir = if (!wall(floor(px).toInt() + DX[r], floor(py).toInt() + DY[r])) r else if (!wall(floor(px).toInt() + DX[dir], floor(py).toInt() + DY[dir])) dir else (dir + 3) % 4
        }
    }

    private fun drawMaze(c: Canvas, w: Float, h: Float, th: com.reverender.r95.core.VaporTheme, t: Float, bass: Float) {
        val dt = if (last == 0L) 0.033 else ((System.nanoTime() - last) / 1e9).coerceIn(0.0, 0.1)
        stepWalker(dt)
        p.shader = LinearGradient(0f, 0f, 0f, h / 2, col(th.sky[0]), col(th.sky[2]), Shader.TileMode.CLAMP)
        c.drawRect(0f, 0f, w, h / 2, p)
        p.shader = LinearGradient(0f, h / 2, 0f, h, col(th.floor[1]), col(th.floor[0]), Shader.TileMode.CLAMP)
        c.drawRect(0f, h / 2, w, h, p)
        p.shader = null
        val cols = (w / 3).toInt().coerceAtLeast(80)
        val fov = PI / 3
        val colW = w / cols
        val pulse = 1f + bass * 0.5f
        for (i in 0 until cols) {
            val ra = angle - fov / 2 + fov * i / cols
            val rdx = cos(ra); val rdy = sin(ra)
            var mx = floor(px).toInt(); var my = floor(py).toInt()
            val ddx = if (rdx == 0.0) 1e30 else abs(1 / rdx); val ddy = if (rdy == 0.0) 1e30 else abs(1 / rdy)
            val sx = if (rdx < 0) -1 else 1; val sy = if (rdy < 0) -1 else 1
            var sdx = if (rdx < 0) (px - mx) * ddx else (mx + 1 - px) * ddx
            var sdy = if (rdy < 0) (py - my) * ddy else (my + 1 - py) * ddy
            var side = 0; var guard = 0
            while (guard++ < 64) {
                if (sdx < sdy) { sdx += ddx; mx += sx; side = 0 } else { sdy += ddy; my += sy; side = 1 }
                if (wall(mx, my)) break
            }
            val dist = ((if (side == 0) sdx - ddx else sdy - ddy) * cos(ra - angle)).coerceAtLeast(0.05)
            val lh = (h / dist).toFloat()
            val hit = if (side == 0) py + (if (side == 0) sdx - ddx else sdy - ddy) * rdy else px + (sdy - ddy) * rdx
            val u = hit - floor(hit)
            // brick-ish Win95 texture: mortar lines on a theme gradient, darker on the side faces
            val shade = (1.0 / (1 + dist * 0.35)).toFloat() * (if (side == 1) 0.72f else 1f)
            val base = if ((u * 4).toInt() % 2 == 0) th.strips[2] else th.strips[3]
            val r = ((base shr 16 and 0xFF) * shade * pulse).toInt().coerceIn(0, 255)
            val g = ((base shr 8 and 0xFF) * shade * pulse).toInt().coerceIn(0, 255)
            val b = ((base and 0xFF) * shade * pulse).toInt().coerceIn(0, 255)
            p.color = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
            val top = h / 2 - lh / 2
            c.drawRect(i * colW, top, (i + 1) * colW + 1, top + lh, p)
            if (abs(u - 0.5) > 0.47) { p.color = col(th.primary, (160 * shade).toInt()); c.drawRect(i * colW, top, (i + 1) * colW + 1, top + lh, p) }
        }
        p.color = col(th.accent); p.textSize = h * 0.03f; p.textAlign = Paint.Align.LEFT; p.isFakeBoldText = true
        c.drawText("WIN95_MAZE_SAVER", w * 0.03f, h * 0.06f, p)
    }

    // ------------------------------------------------------------ CRT glass: scanlines, vignette, a slow roll bar
    private fun drawGlass(c: Canvas, w: Float, h: Float, t: Float) {
        p.shader = null; p.color = 0x2E000000
        var y = 0f; val d = resources.displayMetrics.density
        while (y < h) { c.drawRect(0f, y, w, y + d, p); y += 3 * d }
        val bar = ((t * 0.08f) % 1.2f - 0.1f) * h
        p.shader = LinearGradient(0f, bar - h * 0.08f, 0f, bar + h * 0.08f, intArrayOf(0, 0x14FFFFFF, 0), null, Shader.TileMode.CLAMP)
        c.drawRect(0f, bar - h * 0.08f, w, bar + h * 0.08f, p)
        p.shader = RadialGradient(w / 2, h / 2, sqrt(w * w + h * h) / 2, intArrayOf(0, 0, 0xB0000000.toInt()), floatArrayOf(0f, 0.62f, 1f), Shader.TileMode.CLAMP)
        c.drawRect(0f, 0f, w, h, p)
        p.shader = null
    }

    companion object {
        val MODES = listOf("vumeter" to "ANALOG_VU.EXE", "lissajous" to "LISSAJOUS.EXE", "maze" to "WIN95_MAZE_SAVER",
            "wp:5" to "Hyper-Atrium", "wp:1" to "Cyber City 99", "wp:0" to "Sunset Outrun")
    }
}
