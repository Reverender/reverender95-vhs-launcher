package com.reverender.r95.ui.desktop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlin.random.Random

/** The website's Customize → Fun → "Vapor toys" (rr-os-styles.js / rr-fun-layer), drawn natively. */
object VaporToys {
    val all = listOf(
        Triple("drift", "🗿", "Vapor drift") to "Statues, palms, floppies and dolphins slowly float across the desktop",
        Triple("dolphin", "🐬", "Dolphin splash") to "Tap an empty part of the desktop to make a dolphin leap",
        Triple("vhs", "📼", "VHS tracking") to "A soft VHS tracking band drifts down the screen now and then (no flashing)",
        Triple("haze", "🌅", "Sunset haze") to "A warm pink-and-peach tint over the wallpaper",
        Triple("petals", "🌸", "Sakura petals") to "Pink petals slowly drift down the desktop",
        Triple("stars", "⭐", "Soft stars") to "Tiny stars glow and fade gently over the wallpaper (slow, no flashing)",
        Triple("horizon", "🌐", "Neon horizon") to "A faint neon grid rolls along the bottom of the desktop",
        Triple("bubbles", "🫧", "Bubbles") to "Soft bubbles float up from the bottom of the screen",
    )
    private val DRIFT = listOf("🗿", "🌴", "🐬", "💾", "📼", "🍹", "🌺", "🛍️", "💿", "🏛️", "🌊", "🍉")

    internal class Drift(val t0: Long, val emoji: String, val topFrac: Float, val sizeSp: Float, val dur: Float, val bob: Float, val ltr: Boolean)
    internal class Petal(val t0: Long, val emoji: String, val leftFrac: Float, val sizeSp: Float, val dur: Float, val sway: Float)
    internal class Bubble(val t0: Long, val leftFrac: Float, val sizeDp: Float, val dur: Float, val sway: Float)
    internal class Splash(val t0: Long, val x: Float, val y: Float, val left: Boolean, val dx: FloatArray, val dy: FloatArray)
    internal class Star(val x: Float, val y: Float, val dur: Float, val delay: Float, val big: Boolean)

    internal val drifts = mutableStateListOf<Drift>()
    internal val petals = mutableStateListOf<Petal>()
    internal val bubbles = mutableStateListOf<Bubble>()
    internal val splashes = mutableStateListOf<Splash>()
    internal val stars = List(46) { Star(Random.nextFloat(), Random.nextFloat() * 0.7f, 4 + Random.nextFloat() * 5, Random.nextFloat() * 9, Random.nextFloat() < 0.3f) }
    private var lastDolphin = 0L

    internal fun spawnDrift(now: Long) {
        if (drifts.size >= 5) return
        drifts += Drift(now, DRIFT.random(), (8 + Random.nextFloat() * 62) / 100f, (26 + Random.nextFloat() * 30).roundToInt().toFloat(), 32 + Random.nextFloat() * 16, 5 + Random.nextFloat() * 3, Random.nextBoolean())
    }
    internal fun spawnPetal(now: Long) {
        if (petals.size >= 10) return
        petals += Petal(now, if (Random.nextFloat() < 0.8f) "🌸" else "🌺", Random.nextFloat() * 0.96f, (14 + Random.nextFloat() * 12).roundToInt().toFloat(), 14 + Random.nextFloat() * 9, 3 + Random.nextFloat() * 2)
    }
    internal fun spawnBubble(now: Long) {
        if (bubbles.size >= 8) return
        bubbles += Bubble(now, Random.nextFloat() * 0.94f, (14 + Random.nextFloat() * 30).roundToInt().toFloat(), 16 + Random.nextFloat() * 10, 4 + Random.nextFloat() * 3)
    }

    /** Tap on the bare desktop (px). */
    fun tap(xPx: Float, yPx: Float, widthPx: Float, enabled: Boolean) {
        if (!enabled) return
        val now = System.nanoTime()
        if (now - lastDolphin < 450_000_000L) return
        lastDolphin = now
        splashes += Splash(now, xPx, yPx, xPx > widthPx / 2,
            FloatArray(5) { ((it - 2) * 14 + (Random.nextFloat() * 6 - 3)).roundToInt().toFloat() },
            FloatArray(5) { (-18 - Random.nextFloat() * 22).roundToInt().toFloat() })
    }
}

private fun easeInOut(t: Float): Float = t * t * (3 - 2 * t)
private fun easeOut(t: Float): Float = 1 - (1 - t) * (1 - t)
/** CSS `alternate` infinite animation phase 0..1..0 with ease-in-out. */
private fun alternate(sec: Float, period: Float): Float { val k = (sec / period); val i = k.toInt(); val f = k - i; return easeInOut(if (i % 2 == 0) f else 1 - f) }

@Composable
fun VaporToysLayer(enabled: Set<String>, modifier: Modifier = Modifier) {
    if (enabled.isEmpty() && VaporToys.splashes.isEmpty()) return
    var now by remember { mutableLongStateOf(System.nanoTime()) }
    LaunchedEffect(enabled) {
        var nextDrift = 0L; var nextPetal = 0L; var nextBubble = 0L
        while (true) {
            com.reverender.r95.core.AppVisibility.awaitForeground()
            withFrameNanos { t ->
                if (t - now < com.reverender.r95.core.Perf.minStepNs) return@withFrameNanos
                now = t
                val n = System.nanoTime()
                if ("drift" in enabled && n >= nextDrift) { VaporToys.spawnDrift(n); nextDrift = n + 7_000_000_000L }
                if ("petals" in enabled && n >= nextPetal) { VaporToys.spawnPetal(n); nextPetal = n + 2_600_000_000L }
                if ("bubbles" in enabled && n >= nextBubble) { VaporToys.spawnBubble(n); nextBubble = n + 3_200_000_000L }
                VaporToys.drifts.removeAll { (n - it.t0) / 1e9f > it.dur || "drift" !in enabled }
                VaporToys.petals.removeAll { (n - it.t0) / 1e9f > it.dur || "petals" !in enabled }
                VaporToys.bubbles.removeAll { (n - it.t0) / 1e9f > it.dur || "bubbles" !in enabled }
                VaporToys.splashes.removeAll { (n - it.t0) / 1e9f > 1.5f }
            }
        }
    }
    val n = System.nanoTime().let { now; it }
    val dens = LocalDensity.current.density
    BoxWithConstraints(modifier.fillMaxSize()) {
        val wPx = constraints.maxWidth.toFloat(); val hPx = constraints.maxHeight.toFloat()
        // canvas parts: haze, stars, horizon, bubbles, vhs, splash
        Canvas(Modifier.fillMaxSize()) {
            now
            val sec = n / 1e9f
            if ("haze" in enabled) {
                // soft-light tint approximated with a gentle overlay blend
                drawRect(Brush.verticalGradient(0f to Color(255, 110, 199, 41), 0.55f to Color(255, 170, 120, 31), 1f to Color(130, 70, 220, 41)), blendMode = BlendMode.Overlay)
            }
            if ("stars" in enabled) for (s in VaporToys.stars) {
                val a = 0.12f + (0.6f - 0.12f) * alternate(sec + s.delay, s.dur)
                val r = (if (s.big) 1.5f else 1f) * dens
                val c = Offset(s.x * size.width, s.y * size.height)
                drawCircle(Color(200, 230, 255).copy(alpha = a * 0.5f), r * 3f, c)
                drawCircle(Color.White.copy(alpha = a), r, c)
            }
            if ("horizon" in enabled) {
                // 30vh grid tilted 62° back (perspective 320px), 46px cells rolling over 6 s, masked to fade upwards
                val gh = size.height * 0.30f
                val persp = 320f * dens
                val ang = Math.toRadians(62.0)
                val depthY = { yLocal: Float -> // yLocal: 0 at bottom edge .. gh at the top of the plane
                    val z = yLocal * Math.sin(ang).toFloat(); val yy = yLocal * Math.cos(ang).toFloat()
                    val k = persp / (persp + z); Pair(size.height - yy * k, k)
                }
                val cell = 46f * dens
                val roll = (sec / 6f % 1f) * cell
                clipRect(0f, size.height - gh, size.width, size.height) {
                    var yl = -roll + cell
                    while (yl < gh) {
                        if (yl >= 0) { val (sy, _) = depthY(yl); val fade = (1 - yl / gh).let { if (it > 0.7f) 1f else it / 0.7f }; drawLine(Color(255, 110, 199).copy(alpha = 0.9f * 0.32f * fade), Offset(0f, sy), Offset(size.width, sy), dens) }
                        yl += cell
                    }
                    val w0 = size.width * 1.6f; val x0 = -size.width * 0.3f
                    var x = x0
                    while (x <= x0 + w0) {
                        val (yTop, kTop) = depthY(gh); val cx = size.width / 2
                        val xb = x; val xt = cx + (x - cx) * kTop
                        drawLine(Brush.verticalGradient(0f to Color.Transparent, 0.7f to Color(0, 229, 255).copy(alpha = 0.8f * 0.32f), startY = yTop, endY = size.height), Offset(xt, yTop), Offset(xb, size.height), dens)
                        x += cell
                    }
                }
            }
            for (b in VaporToys.bubbles) {
                val t = ((n - b.t0) / 1e9f / b.dur).coerceIn(0f, 1f)
                val op = when { t < 0.08f -> t / 0.08f * 0.9f; t < 0.9f -> 0.9f + (0.75f - 0.9f) * (t - 0.08f) / 0.82f; else -> 0.75f * (1 - (t - 0.9f) / 0.1f) }
                val sz = b.sizeDp * dens
                val sway = (-14f + 28f * alternate((n - b.t0) / 1e9f, b.sway)) * dens
                val y = size.height + 60f * dens - sz - t * (size.height + 100f * dens)
                val x = b.leftFrac * size.width + sway
                val c = Offset(x + sz / 2, y + sz / 2)
                drawCircle(Brush.radialGradient(0f to Color.White.copy(alpha = 0.55f * op), 0.34f to Color.White.copy(alpha = 0.08f * op), 0.62f to Color(127, 255, 212).copy(alpha = 0.10f * op), 1f to Color(255, 141, 161).copy(alpha = 0.22f * op), center = Offset(x + sz * 0.32f, y + sz * 0.30f), radius = sz * 0.85f), sz / 2, c)
                drawCircle(Color.White.copy(alpha = 0.35f * op), sz / 2, c, style = Stroke(dens))
            }
            if ("vhs" in enabled) {
                val ph = sec % 22f / 22f
                if (ph > 0.62f) {
                    val k = (ph - 0.62f) / 0.38f
                    val bandH = size.height * 0.09f
                    val y = -size.height * 0.12f + k * size.height * 1.24f
                    drawRect(Brush.verticalGradient(0f to Color.Transparent, 0.4f to Color.White.copy(alpha = 0.07f), 0.6f to Color.White.copy(alpha = 0.05f), 1f to Color.Transparent, startY = y, endY = y + bandH), Offset(0f, y), Size(size.width, bandH))
                    var x = 0f
                    while (x < size.width) { drawRect(Color.White.copy(alpha = 0.035f), Offset(x, y), Size(3f * dens, bandH)); x += 9f * dens }
                }
            }
            for (s in VaporToys.splashes) {
                val e = (n - s.t0) / 1e9f
                if (e < 0.8f) { val k = easeOut(e / 0.8f); for (i in 0 until 5) drawCircle(Color(0x9F, 0xF6, 0xFF).copy(alpha = 1 - k), 3f * dens, Offset(s.x + s.dx[i] * dens * k, s.y + s.dy[i] * dens * k)) }
                if (e < 1.1f) { val k = easeOut(e / 1.1f); drawOval(Color(160, 246, 255).copy(alpha = 0.8f * (1 - k)), Offset(s.x - 5f * dens * (1 + 8 * k), s.y - 2f * dens * (1 + 6 * k)), Size(10f * dens * (1 + 8 * k), 4f * dens * (1 + 6 * k)), style = Stroke(2f * dens * (1 + 7 * k))) }
            }
        }
        // emoji parts (Compose text so EmojiCompat covers old phones)
        for (d in VaporToys.drifts) {
            val e = (n - d.t0) / 1e9f; val t = (e / d.dur).coerceIn(0f, 1f)
            val k = if (d.ltr) t else 1 - t
            val x = (-0.1f + 1.2f * k) * wPx
            val rot = -8f + 16f * k
            val bob = (-10f + 20f * alternate(e, d.bob)) * dens
            Text(d.emoji, fontSize = d.sizeSp.sp, modifier = Modifier.offset { IntOffset(x.roundToInt(), (d.topFrac * hPx + bob).roundToInt()) }.graphicsLayer { rotationZ = rot; alpha = 0.8f })
        }
        for (p in VaporToys.petals) {
            val e = (n - p.t0) / 1e9f; val t = (e / p.dur).coerceIn(0f, 1f)
            val y = -40f * dens + t * (hPx + 80f * dens)
            val sway = (-22f + 44f * alternate(e, p.sway)) * dens
            Text(p.emoji, fontSize = p.sizeSp.sp, modifier = Modifier.offset { IntOffset((p.leftFrac * wPx + sway).roundToInt(), y.roundToInt()) }.graphicsLayer { rotationZ = 320f * t; alpha = 0.85f })
        }
        for (s in VaporToys.splashes) {
            val e = (n - s.t0) / 1e9f; if (e > 1.4f) continue
            val t = e / 1.4f
            // keyframes: 0% (-40,20) rot -50 scale .6 opacity 0 · 15% opacity 1 · 50% (0,-70) rot 0 scale 1 · 100% (40,20) rot 55 scale .7 opacity 0
            val q = if (t < 0.5f) { val k = easeOut(t / 0.5f); Quad(-40f + 40f * k, 20f - 90f * k, -50f + 50f * k, 0.6f + 0.4f * k) } else { val k = easeOut((t - 0.5f) / 0.5f); Quad(40f * k, -70f + 90f * k, 55f * k, 1f - 0.3f * k) }
            val tx = q.a; val ty = q.b; val rot = q.c; val sc = q.d
            val op = if (t < 0.15f) t / 0.15f else if (t < 0.5f) 1f else 1f - (t - 0.5f) / 0.5f
            val dir = if (s.left) -1f else 1f
            Box(Modifier.offset { IntOffset((s.x - 19 * dens + dir * tx * dens).roundToInt(), (s.y - 19 * dens + ty * dens).roundToInt()) }.graphicsLayer { rotationZ = dir * rot; scaleX = dir * sc; scaleY = sc; alpha = op }) {
                Text("🐬", fontSize = 38.sp)
            }
        }
    }
}

private data class Quad(val a: Float, val b: Float, val c: Float, val d: Float)
