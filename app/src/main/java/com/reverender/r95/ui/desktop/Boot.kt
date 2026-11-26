package com.reverender.r95.ui.desktop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reverender.r95.ui.win95.Skin
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

/** cubic-bezier(.55,0,.25,1), as used by the site's CRT keyframes. */
private fun bez(x: Float): Float {
    // solve x(t) = x by bisection, return y(t)
    val x1 = .55f; val y1 = 0f; val x2 = .25f; val y2 = 1f
    fun bx(t: Float) = 3 * (1 - t) * (1 - t) * t * x1 + 3 * (1 - t) * t * t * x2 + t * t * t
    fun by(t: Float) = 3 * (1 - t) * (1 - t) * t * y1 + 3 * (1 - t) * t * t * y2 + t * t * t
    var lo = 0f; var hi = 1f
    repeat(20) { val m = (lo + hi) / 2; if (bx(m) < x) lo = m else hi = m }
    return by((lo + hi) / 2)
}
/** a CSS keyframe track: (offset, value) pairs, linear between, with the animation's easing applied to the whole run */
private fun track(p: Float, vararg k: Pair<Float, Float>): Float {
    if (p <= k.first().first) return k.first().second
    for (i in 1 until k.size) if (p <= k[i].first) { val (a, va) = k[i - 1]; val (b, vb) = k[i]; return va + (vb - va) * ((p - a) / (b - a)) }
    return k.last().second
}

/**
 * #rr-boot + #rr-crt: the site's start-up. The boot screen (Longhorn or Windows 95 look) runs for ~2.85 s and
 * fades out over 0.5 s; on top, the CRT power-on: black halves part while a beam grows from a dot to the full screen (1.7 s).
 */
@Composable
fun BootOverlay(onDone: () -> Unit) {
    val start = remember { System.currentTimeMillis() }
    var now by remember { mutableLongStateOf(start) }
    var msg by remember { mutableStateOf("Loading apps and wallpapers…") }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { }
            now = System.currentTimeMillis()
            val t = now - start
            if (t > 600) msg = "Starting the desktop…"
            if (t > 2850 + 520) { onDone(); break }
        }
    }
    val t = (now - start).toFloat()
    val fade = ((t - 2850f) / 500f).coerceIn(0f, 1f)
    Box(Modifier.fillMaxSize().pointerInput(Unit) { awaitPointerEventScope { while (true) awaitPointerEvent() } }) {
        // #rr-boot
        Box(Modifier.fillMaxSize().graphicsLayer { alpha = 1f - fade }) {
            if (Skin.classic) BootW95(t, msg) else BootLonghorn(t, msg)
        }
        // #rr-crt
        if (t < 1800) Canvas(Modifier.fillMaxSize()) {
            val p = bez((t / 1700f).coerceIn(0f, 1f))
            val half = size.height * 0.505f
            val tp = track(p, 0f to 0f, .30f to 0f, .68f to 1.01f, 1f to 1.01f)
            drawRect(Color.Black, Offset(0f, -tp * half), Size(size.width, half))
            drawRect(Color.Black, Offset(0f, size.height - half + tp * half), Size(size.width, half))
            // beam
            val sx = track(p, 0f to .004f, .08f to .012f, .30f to 1.02f, .68f to 1.02f, 1f to 1.02f)
            val sy = track(p, 0f to .004f, .08f to .006f, .30f to .006f, .68f to 1f, 1f to 1f)
            val op = track(p, 0f to 0f, .08f to .9f, .30f to .85f, .68f to .22f, 1f to 0f)
            withTransform({ scale(sx, sy, center) }) {
                drawRect(
                    Brush.radialGradient(
                        0f to Color(0xF2E6FFFF), .35f to Color(0x8C96F0FF), .7f to Color(0x33FF78D2), 1f to Color(0x00FF78D2),
                        center = center, radius = size.maxDimension * 0.6f,
                    ), alpha = op,
                )
            }
            // the beam's ::after: scan lines and vignette
            if (op > 0.01f) {
                var y = 0f; val d = density
                while (y < size.height) { drawRect(Color.Black.copy(alpha = 0.18f * op), Offset(0f, y), Size(size.width, d)); y += 3 * d }
                drawRect(Brush.radialGradient(0.55f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.55f * op), center = center, radius = size.maxDimension * 0.6f))
            }
        }
    }
}

/** .rr-boot-lh */
@Composable
private fun BootLonghorn(t: Float, msg: String) {
    BoxWithConstraints(Modifier.fillMaxSize().background(Brush.verticalGradient(0f to Color(0xFF0B0F14), .55f to Color(0xFF151C26), .72f to Color(0xFF2A1D3A), .82f to Color(0xFF5A2A5E), 1f to Color(0xFF151C26)))) {
        val mw = maxWidth
        Canvas(Modifier.fillMaxSize()) {
            val hz = size.height * 0.72f; val vhp = size.height / 100f
            // .rr-blh-glow: breathing
            val breathe = 0.7f + 0.3f * ((1 - cos(t / 6000f * 2 * PI.toFloat())) / 2)
            withTransform({ scale(1f, 34f / 80f, Offset(size.width / 2, hz - 34 * vhp * 0.1f)) }) {
                drawCircle(Brush.radialGradient(listOf(Color(0x59FF6FAE), Color(0x00FF6FAE)), Offset(size.width / 2, hz - 34 * vhp * 0.1f), 40 * vhp), 40 * vhp, Offset(size.width / 2, hz - 34 * vhp * 0.1f), alpha = breathe)
            }
            // .rr-blh-grid: perspective floor, rows scrolling every 4 s
            clipRect(0f, hz, size.width, size.height) {
                val scroll = (t % 4000f) / 4000f
                val depth = size.height - hz
                for (i in 0 until 14) {
                    val z = (i + scroll) / 14f
                    val y = hz + depth * z.pow(1.8f)
                    drawLine(Color(0x61FF6FAE), Offset(0f, y), Offset(size.width, y), density)
                }
                val cx = size.width / 2
                for (i in -14..14) drawLine(Color(0x4D7FD6FF), Offset(cx + i * 6 * vhp * 0.25f, hz), Offset(cx + i * 6 * vhp * 2.2f, size.height), density)
            }
            // .rr-blh-sun (top half, striped)
            val r = 15 * vhp; val c = Offset(size.width / 2, hz)
            clipRect(c.x - r, c.y - r, c.x + r, c.y) {
                drawCircle(Brush.verticalGradient(0f to Color(0xFFFFD27A), .6f to Color(0xFFFF6FAE), 1f to Color(0xFF8E6BFF), startY = c.y - r, endY = c.y + r), r, c, alpha = 0.9f)
                drawCircle(Brush.radialGradient(listOf(Color(0x8CFFFFFF), Color.Transparent), Offset(c.x, c.y - r * 0.5f), r * 0.6f), r, c)
                listOf(.31f to .33f, .38f to .41f, .45f to .49f).forEach { (a, b) -> drawRect(Color(0xE6151C26), Offset(c.x - r, c.y - r + a * 2 * r), Size(2 * r, (b - a) * 2 * r)) }
            }
            // .rr-blh-hz
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color(0xD9FFB4E6), Color.Transparent)), Offset(0f, hz), Size(size.width, density))
        }
        // .rr-blh-mark
        Column(Modifier.fillMaxWidth().offset(y = maxHeight * 0.30f - 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            val nameSize = (mw.value * 0.046f).coerceIn(26f, 56f)
            BasicText("REVERENDER", style = TextStyle(color = Color(0xFFEEF4F8), fontSize = nameSize.sp, fontWeight = FontWeight.Light, letterSpacing = (nameSize * 0.3f).sp))
            Spacer(Modifier.height(14.dp))
            BasicText("LONGHORN", style = TextStyle(color = Color(0xFFEEF4F8).copy(alpha = 0.75f), fontSize = 13.sp, letterSpacing = (13 * 0.55f).sp))
            Spacer(Modifier.height(36.dp))
            // .rr-blh-cap with its sliding light (1.9 s)
            Box(Modifier.width(200.dp).height(10.dp).clip(RoundedCornerShape(5.dp)).background(Color.White.copy(alpha = 0.06f))) {
                Canvas(Modifier.fillMaxSize()) {
                    drawRoundRect(Color.White.copy(alpha = 0.18f), style = androidx.compose.ui.graphics.drawscope.Stroke(density), cornerRadius = androidx.compose.ui.geometry.CornerRadius(5 * density))
                    val k = ((t % 1900f) / 1900f).let { val e = it; if (e < .5f) 2 * e * e else 1 - (-2 * e + 2).pow(2) / 2 }
                    val x = (-74 + k * 278) * density; val w = 70 * density
                    drawRoundRect(Brush.horizontalGradient(0f to Color(0x00FF6FAE), .4f to Color(0xFFFF8FC4), .6f to Color(0xFFBFE8FF), 1f to Color(0x007FD6FF), startX = x, endX = x + w), Offset(x, 0f), Size(w, size.height), androidx.compose.ui.geometry.CornerRadius(5 * density))
                }
            }
            Spacer(Modifier.height(14.dp))
            BasicText(msg, style = TextStyle(color = Color(0xFFEEF4F8).copy(alpha = 0.7f), fontSize = 13.sp, textAlign = TextAlign.Center))
        }
    }
}

/** .rr-boot-w95: "Reverender Vapor95" over the painted sky (__rrBootPaint), rainbow bar at the bottom */
@Composable
private fun BootW95(t: Float, msg: String) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val portrait = maxHeight > maxWidth
        val zoom = 1.04f - 0.04f * (1 - (1 - (t / 24000f).coerceIn(0f, 1f)).pow(3))
        Canvas(Modifier.fillMaxSize().graphicsLayer { scaleX = zoom; scaleY = zoom; transformOrigin = androidx.compose.ui.graphics.TransformOrigin(.5f, .7f) }) {
            drawRect(Brush.verticalGradient(0f to Color(0xFF1F63D6), .333f to Color(0xFF4B8BE6), .667f to Color(0xFF9CC8F6), 1f to Color(0xFFD6E8FB)))
            val z = min(size.width, size.height); val b = size.width * .5f; val m = size.height * 1.02f; val l = z * (if (size.width >= size.height) .4f else .5f)
            drawCircle(Brush.verticalGradient(0f to Color(0xFFFFF59A), .45f to Color(0xFFFF9A5C), .85f to Color(0xFFFF3FA4), startY = m - l, endY = m + l * .2f), l, Offset(b, m), alpha = .9f)
            for (k in 0 until 7) drawRect(Color(0xFFB7D6F8), Offset(b - l, m - l * .42f + k * l * .11f), Size(l * 2, l * (.012f + k * .012f)))
            drawRect(Brush.radialGradient(listOf(Color(0x59FF78BE), Color(0x00FF78BE)), Offset(b, m), l * 1.9f))
            // puffy clouds (seeded like the site)
            var seed = 11L
            fun rnd(): Float { seed = seed * 16807 % 2147483647; return seed / 2147483647f }
            val groups = if (size.width >= size.height) listOf(floatArrayOf(.17f, .2f, .55f, .22f, 90f), floatArrayOf(.82f, .14f, .5f, .18f, 70f), floatArrayOf(.5f, .82f, 1.1f, .26f, 140f), floatArrayOf(.03f, .6f, .4f, .18f, 60f), floatArrayOf(.98f, .58f, .38f, .18f, 55f), floatArrayOf(.58f, .3f, .32f, .1f, 35f))
            else listOf(floatArrayOf(.2f, .12f, .9f, .12f, 70f), floatArrayOf(.85f, .22f, .7f, .1f, 50f), floatArrayOf(.5f, .84f, 1.2f, .16f, 110f), floatArrayOf(.1f, .6f, .6f, .1f, 45f), floatArrayOf(.9f, .64f, .5f, .1f, 40f))
            groups.forEach { g ->
                val n = g[0] * size.width; val u = g[1] * size.height; val rw = g[2] * size.width; val s = g[3] * size.height
                repeat(g[4].toInt()) {
                    val v = rnd(); val f = 1 - ((v - .5f) * 2).let { it * it }
                    val x = n + (v - .5f) * rw; val y = u - rnd() * s * .9f * f + s * .25f; val rr = s * (.2f + rnd() * .3f) * (.55f + f * .6f)
                    drawCircle(Brush.radialGradient(listOf(Color(0x99B0BAEC), Color(0x00B0BAEC)), Offset(x, y + rr * .4f), rr * 1.1f), rr * 1.1f, Offset(x, y + rr * .4f))
                    drawCircle(Brush.radialGradient(listOf(Color(0xD9F8E2F6), Color(0x00F8E2F6)), Offset(x, y), rr), rr, Offset(x, y))
                    drawCircle(Brush.radialGradient(listOf(Color(0xB3FFFAFF), Color(0x00FFFAFF)), Offset(x - rr * .25f, y - rr * .35f), rr * .6f), rr * .6f, Offset(x - rr * .25f, y - rr * .35f))
                }
            }
        }
        // .rr-b95-word
        val big = if (portrait) (maxWidth.value * 0.17f).coerceIn(46f, 110f) else (maxWidth.value * 0.096f).coerceIn(46f, 132f)
        val word: @Composable () -> Unit = {
            // .rr-b95-emb: the tilted little monitor
            Canvas(Modifier.size(width = (big * 1.2f).dp, height = (big * 1.04f).dp).graphicsLayer { rotationY = -22f; rotationX = 8f; rotationZ = -5f; cameraDistance = 12 * density }) {
                val w = size.width; val h = size.height
                drawRoundRect(Color(0x4D140028), Offset(8 * density, 8 * density), size, androidx.compose.ui.geometry.CornerRadius(w * .08f))
                drawRoundRect(Brush.linearGradient(listOf(Color.White, Color(0xFFE3E0F0), Color(0xFFAAA6C8))), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * .08f))
                drawRect(Brush.verticalGradient(0f to Color(0xFFFF3FA4), .18f to Color(0xFFFF3FA4), .18f to Color(0xFFFF8A5C), .28f to Color(0xFFFF8A5C), .28f to Color(0xFFFFD36B), .36f to Color(0xFFFFD36B), .36f to Color.White, startY = h * .1f, endY = h * .62f), Offset(w * .09f, h * .1f), Size(w * .82f, h * .52f))
                drawRoundRect(Color(0xFF1B1340), Offset(w * .24f, h * .4f), Size(w * .52f, h * .2f), androidx.compose.ui.geometry.CornerRadius(20 * density))
                listOf(.33f, .67f).forEach { cx -> drawCircle(Color(0xFF7FFFD4), h * .055f, Offset(w * cx, h * .5f)) }
                drawPath(Path().apply { moveTo(w * .24f, h * .78f); lineTo(w * .76f, h * .78f); lineTo(w * .78f, h * .94f); lineTo(w * .22f, h * .94f); close() }, Color(0xFF9A96BC))
            }
            Column(horizontalAlignment = if (portrait) Alignment.CenterHorizontally else Alignment.Start) {
                BasicText("Reverender", style = TextStyle(color = Color.Black, fontSize = (big * 0.22f).sp))
                Row {
                    BasicText("Vapor", style = TextStyle(color = Color.Black, fontSize = big.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.025f * big).sp))
                    BasicText("95", style = TextStyle(brush = Brush.verticalGradient(listOf(Color(0xFFFF3FA4), Color(0xFF8A3CFF))), fontSize = (big * 0.63f).sp, fontWeight = FontWeight.Black))
                }
            }
        }
        Box(Modifier.fillMaxWidth().offset(y = maxHeight * (if (portrait) 0.40f else 0.42f) - (big * 0.7f).dp), contentAlignment = Alignment.Center) {
            if (portrait) Column(horizontalAlignment = Alignment.CenterHorizontally) { word() }
            else Row(verticalAlignment = Alignment.CenterVertically) { word() }
        }
        // .rr-b95-status + .rr-b95-bar
        BasicText(msg, style = TextStyle(color = Color(0xBF0B2A66), fontSize = 12.sp, textAlign = if (portrait) TextAlign.Center else TextAlign.End),
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(end = if (portrait) 0.dp else 16.dp, bottom = maxHeight * 0.032f + 14.dp))
        Canvas(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(maxOf(14.dp, maxHeight * 0.032f))) {
            val period = size.width * 0.5f; val off = (t % 3200f) / 3200f * size.width
            val cols = listOf(Color(0xFF4B8CFF), Color(0xFF8A3CFF), Color(0xFFFF3FA4), Color(0xFFFFD36B), Color(0xFF7FFFD4), Color(0xFF4B8CFF))
            var x = -off % period - period
            while (x < size.width) { drawRect(Brush.horizontalGradient(cols, x, x + period), Offset(x, 0f), Size(period, size.height)); x += period }
            drawRect(Color.White.copy(alpha = 0.75f), Offset.Zero, Size(size.width, density))
        }
    }
}
