package com.reverender.r95.ui.viz

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.reverender.r95.audio.AudioFrame
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import com.reverender.r95.audio.AudioBus
import com.reverender.r95.core.VaporTheme
import kotlin.math.pow
import kotlin.math.sin

/**
 * The website's signature desktop: gradient sky, sliced sun, neon mountains and an
 * endless perspective grid, all drawn natively. The sun breathes with the bass.
 */
@Composable
fun VaporWallpaper(theme: VaporTheme, animated: Boolean, reactive: Boolean, modifier: Modifier = Modifier) {
    val time = rememberFrameTime(animated)
    val stars = remember { List(70) { i -> Offset(((i * 173.7f) % 100f) / 100f, ((i * 61.3f) % 100f) / 100f) } }
    val sky = remember(theme) { theme.sky.map(::argb) }
    val sun = remember(theme) { theme.sun.map(::argb) }
    val floor = remember(theme) { theme.floor.map(::argb) }

    Canvas(modifier) {
        val t = time.value
        val w = size.width; val h = size.height
        val horizon = h * 0.62f
        val frame = if (reactive) AudioBus.currentFrame() else null
        val bass = frame?.bass ?: 0f

        // Sky
        drawRect(Brush.verticalGradient(sky, 0f, horizon), size = Size(w, horizon))

        // Stars twinkle in the upper sky
        stars.forEachIndexed { i, s ->
            val a = 0.35f + 0.35f * sin(t * 0.7f + i * 1.7f)
            drawRect(Color(0xFFFFF9C4).withA(a), Offset(s.x * w, s.y * horizon * 0.5f), Size(2.5f, 2.5f))
        }

        // Sun with horizontal slices that widen toward the bottom
        val r = minOf(w, h) * 0.24f * (1f + bass * 0.08f)
        val c = Offset(w / 2f, horizon - r * 0.35f)
        drawCircle(argb(theme.sun[0]).withA(0.18f + bass * 0.2f), r * 1.35f, c)
        clipRect(0f, 0f, w, horizon) {
            drawCircle(Brush.verticalGradient(sun, c.y - r, c.y + r), r, c)
            val slices = 7
            for (i in 0 until slices) {
                val drift = (t * 0.08f) % 1f
                val k = (i + drift) / slices
                val y = c.y + r * (0.05f + k * 0.95f)
                val thick = 2f + k * r * 0.09f
                drawRect(argb(theme.sunSlice), Offset(c.x - r, y), Size(r * 2, thick))
            }
        }

        // Mountains: two ridges
        fun ridge(height: Float, seed: Float, alpha: Float) {
            val p = Path().apply {
                moveTo(0f, horizon)
                val n = 14
                for (i in 0..n) {
                    val x = w * i / n
                    val wobble = (sin(i * 1.9f + seed) * 0.5f + sin(i * 0.7f + seed * 2.3f) * 0.5f + 1f) / 2f
                    val edge = 1f - 0.6f * (1f - kotlin.math.abs(i - n / 2f) / (n / 2f)) // valley in the middle for the sun
                    lineTo(x, horizon - height * wobble * edge)
                }
                lineTo(w, horizon); close()
            }
            drawPath(p, Brush.verticalGradient(listOf(argb(theme.mtnTop), argb(theme.mtnBot)), horizon - height, horizon), alpha = alpha)
            drawPath(p, argb(theme.mtnStroke).withA(0.8f * alpha), style = Stroke(width = 2f))
        }
        ridge(h * 0.20f, 1.3f, 0.75f)
        ridge(h * 0.13f, 4.1f, 1f)

        // Floor + perspective grid
        drawRect(Brush.verticalGradient(floor, horizon, h), Offset(0f, horizon), Size(w, h - horizon))
        val grid = argb(theme.grid)
        val rows = 14
        val scroll = (t * 0.45f) % 1f
        for (i in 0 until rows) {
            val k = ((i + scroll) / rows).pow(2.2f)
            val y = horizon + k * (h - horizon)
            drawLine(grid.withA(grid.alpha * (0.25f + k)), Offset(0f, y), Offset(w, y), strokeWidth = 1f + k * 2f)
        }
        val cols = 22
        for (i in -cols..cols) {
            val xFar = w / 2f + i * w * 0.018f
            val xNear = w / 2f + i * w * 0.14f
            drawLine(grid, Offset(xFar, horizon), Offset(xNear, h), strokeWidth = 1.4f)
        }
        // Horizon glow
        drawRect(
            Brush.verticalGradient(listOf(Color.Transparent, argb(theme.secondary).withA(0.45f + bass * 0.3f), Color.Transparent), horizon - 18f, horizon + 18f),
            Offset(0f, horizon - 18f), Size(w, 36f),
        )
    }
}

/** Optional CRT scanline overlay across the whole screen. */
@Composable
fun Scanlines(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        var y = 0f
        val c = Color.Black.withA(0.13f)
        while (y < size.height) { drawRect(c, Offset(0f, y), Size(size.width, 1.5f)); y += 4f }
        drawRect(
            Brush.radialGradient(listOf(Color.Transparent, Color.Black.withA(0.35f)), center = Rect(Offset.Zero, size).center, radius = size.maxDimension * 0.75f),
        )
    }
}

/**
 * The live desktop background: one of the Scenes, or a solid colour, plus any Depth FX layers.
 * Falls back to a still frame when animation is off or battery saver is on.
 */
@Composable
fun DesktopBackground(
    sceneId: Int,
    solid: Long,
    layers: Set<String>,
    theme: VaporTheme,
    animated: Boolean,
    reactive: Boolean,
    modifier: Modifier = Modifier,
) {
    @Suppress("NAME_SHADOWING") val modifier = with(com.reverender.r95.ui.desktop.FunFxScope) { modifier.fx() }
    if (sceneId in com.reverender.r95.ui.viz.site.SiteWallpaper.ids) {
        // the website's own renderer, translated 1:1
        val state = com.reverender.r95.LocalAppState.current
        val dens = androidx.compose.ui.platform.LocalDensity.current.density
        androidx.compose.runtime.DisposableEffect(Unit) { onDispose { com.reverender.r95.ui.viz.site.SiteWallpaper.stop() } }
        Canvas(modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val ev = awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Final)
                    val ch = ev.changes.firstOrNull() ?: continue
                    com.reverender.r95.ui.viz.site.SiteWallpaper.pointer(ch.position.x, ch.position.y, size.width.toFloat(), size.height.toFloat())
                    if (ch.pressed && !ch.previousPressed && !ch.isConsumed) {
                        com.reverender.r95.ui.viz.site.SiteWallpaper.tap(ch.position.x, ch.position.y, dens)
                        com.reverender.r95.ui.desktop.VaporToys.tap(ch.position.x, ch.position.y, size.width.toFloat(), state.toys["dolphin"] == true)
                    }
                }
            }
        }) {
            if (com.reverender.r95.ui.viz.site.SiteWallpaper.async) com.reverender.r95.ui.viz.site.SiteHost.wallReady
            else if (animated) com.reverender.r95.ui.viz.site.SiteHost.wallFrame
            drawIntoCanvas { c ->
                com.reverender.r95.ui.viz.site.SiteWallpaper.draw(
                    c.nativeCanvas, size.width, size.height, density, sceneId, theme.id,
                    layers, state.lowPower, animated,
                )
            }
        }
        return
    }
    // hidden behind a maximized window: hold the current frame
    val time = rememberFrameTime(animated && !com.reverender.r95.core.Perf.desktopCovered)
    val scene = remember(sceneId) { if (sceneId >= 0) Scenes.byId(sceneId) ?: Scenes.all.first() else null }
    val activeLayers = remember(layers) { BgLayers.all.filter { it.id in layers } }
    Canvas(modifier) {
        val ctx = SceneCtx(time.value, theme, if (reactive) AudioBus.currentFrame() else com.reverender.r95.audio.AudioFrame.SILENT, reactive)
        if (scene != null) with(scene) { draw(ctx) } else drawRect(Color(solid))
        activeLayers.forEach { layer -> with(layer) { draw(ctx) } }
    }
}

/** BASS GLITCH: RGB-split slices that jump on kick drums. */
@Composable
fun BassGlitch(theme: VaporTheme, modifier: Modifier = Modifier) {
    val time = rememberFrameTime()
    val env = remember { FloatArray(2) }
    Canvas(modifier) {
        time.value
        val f = AudioBus.currentFrame()
        // fast attack on bass jumps, slow decay
        val hit = (f.bass - env[1]).coerceAtLeast(0f)
        env[1] = f.bass
        env[0] = maxOf(env[0] * 0.86f, hit * 6f)
        val g = env[0].coerceIn(0f, 1f)
        if (g < 0.05f) return@Canvas
        val n = (3 + g * 8).toInt()
        for (i in 0 until n) {
            val y = (kotlin.random.Random.nextFloat() * size.height)
            val h = 4f + kotlin.random.Random.nextFloat() * 40f * g
            val dx = (kotlin.random.Random.nextFloat() - 0.5f) * 60f * g
            drawRect(argb(theme.secondary).withA(0.25f * g), Offset(dx, y), Size(size.width, h))
            drawRect(argb(theme.primary).withA(0.22f * g), Offset(-dx, y + h * 0.3f), Size(size.width, h * 0.6f))
        }
    }
}

/** Vaporwave screensaver: flying sliced suns over a star field; any touch wakes the desktop. */
@Composable
fun Screensaver(theme: VaporTheme, modifier: Modifier = Modifier, onWake: () -> Unit) {
    val time = rememberFrameTime()
    Canvas(
        modifier.pointerInput(Unit) { detectTapGestures { onWake() } },
    ) {
        val t = time.value
        drawRect(Color.Black)
        for (i in 0 until 160) {
            val z = ((i * 0.618f - t * 0.2f) % 1f + 1f) % 1f
            val a = i * 2.39996f
            val r = (1 - z) * size.maxDimension * 0.6f
            drawCircle(Color.White.withA(1 - z), 1.5f + (1 - z) * 2f, Offset(center.x + kotlin.math.cos(a) * r, center.y + kotlin.math.sin(a) * r))
        }
        for (k in 0 until 3) {
            val px = center.x + kotlin.math.sin(t * 0.3f + k * 2.1f) * size.width * 0.3f
            val py = center.y + kotlin.math.cos(t * 0.23f + k * 1.7f) * size.height * 0.3f
            val rr = size.minDimension * (0.08f + 0.03f * k)
            drawCircle(Brush.verticalGradient(theme.sun.map(::argb), py - rr, py + rr), rr, Offset(px, py))
            for (s in 0 until 4) drawRect(Color.Black, Offset(px - rr, py + rr * (0.15f + s * 0.22f)), Size(rr * 2, 2f + s * 2f))
        }
    }
}
