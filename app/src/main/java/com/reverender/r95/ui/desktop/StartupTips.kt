package com.reverender.r95.ui.desktop

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reverender.r95.ui.win95.Skin
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/** Screen positions (px, desktop coordinates) of the elements the site's startup hints point at. */
object TipAnchors {
    /** id -> centre Y of the target */
    val centerY = mutableStateMapOf<String, Float>()
    /** right edge of the player window */
    var playerRight by mutableStateOf(0f)
    var playerVisible by mutableStateOf(false)
}

private class Hint(val id: String, val target: String, val text: String)

private val HINTS = listOf(
    Hint("rr-player-play-hint", "play", "Press PLAY for the built-in deck, or scroll down for radio, device files and live audio inputs."),
    Hint("rr-radio-tuner-hint", "radio", "INTERNET RADIO TUNER — choose a built-in station to tune in instantly."),
    Hint("rr-radio-own-input-hint", "radio", "OWN STREAM — paste a direct MP3, AAC or Icecast stream URL, then press TUNE IN."),
    Hint("rr-radio-search-hint", "search", "SEARCH RADIO STATIONS — find stations worldwide by name, genre or country, then select a result."),
)

/**
 * The site's startup hints: 1.25 s after start, four bubbles beside VAPOR_AMP (stacked 6 px apart at the
 * height of their targets, kept on screen), visible for 8.2 s, then a 220 ms fade.
 */
@Composable
fun StartupHints(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1250)
        visible = true
        delay(8200)
        visible = false
        delay(240)
        done = true
    }
    if (done) return
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(if (visible) 120 else 220), label = "hint")
    val heights = remember { mutableStateMapOf<String, Int>() }
    val widths = remember { mutableStateMapOf<String, Int>() }
    val d = LocalDensity.current.density
    BoxWithConstraints(modifier.fillMaxSize()) {
        val W = constraints.maxWidth.toFloat(); val H = constraints.maxHeight.toFloat()
        if (!TipAnchors.playerVisible) return@BoxWithConstraints
        // layout like the site's M(): sort by preferred top, push down to avoid overlap, then lift to fit
        data class P(val h: Hint, val pref: Float, var top: Float, val height: Float, val cy: Float)
        val ps = HINTS.mapNotNull { h ->
            val cy = TipAnchors.centerY[h.target] ?: return@mapNotNull null
            val hh = (heights[h.id] ?: 60).toFloat()
            P(h, cy - hh / 2, cy - hh / 2, hh, cy)
        }.sortedBy { it.pref }
        for (i in 1 until ps.size) ps[i].top = maxOf(ps[i].pref, ps[i - 1].top + ps[i - 1].height + 6 * d)
        if (ps.isNotEmpty()) { val over = ps.last().top + ps.last().height - (H - 8 * d); if (over > 0) ps.forEach { it.top -= over } }
        for (p in ps) {
            val w = (widths[p.h.id] ?: (250 * d).toInt()).toFloat()
            var left = TipAnchors.playerRight + 10 * d
            if (left + w > W - 8 * d) left = maxOf(8 * d, W - w - 8 * d)
            val top = p.top.coerceIn(8 * d, H - p.height - 8 * d)
            val arrowY = (p.cy - top - 4.5f * d).coerceIn(8 * d, p.height - 17 * d)
            Bubble(
                p.h.text,
                Modifier.offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                    .graphicsLayer { this.alpha = alpha; translationY = (1 - alpha) * 3 * density }
                    .onSizeChanged { heights[p.h.id] = it.height; widths[p.h.id] = it.width },
                arrowY,
            )
        }
    }
}

/** .rr-hint-bubble (classic: dark pink-bordered mono; Longhorn: light gradient, rounded) with the side arrow. */
@Composable
private fun Bubble(text: String, modifier: Modifier, arrowY: Float) {
    val classic = Skin.classic
    val bg = if (classic) Color(0xFF291022) else Color(0xFFF3F6F8)
    val border = if (classic) Color(0xFFFF4FCA) else Color(0xFF7F8F98)
    Box(
        modifier.widthIn(max = 250.dp)
            .drawBehind {
                // side arrow: a 9px square rotated 135° sticking out on the left
                val s = 9.dp.toPx(); val cx = 0f; val cy = arrowY + s / 2
                val path = Path().apply { moveTo(cx, cy - s * 0.7f); lineTo(cx - s * 0.7f, cy); lineTo(cx, cy + s * 0.7f); close() }
                drawPath(path, bg); drawPath(path, border, style = Stroke(1.dp.toPx()))
            }
            .then(
                if (classic) Modifier.drawBehind { drawRect(Color(0xFF050008), Offset(2.dp.toPx(), 2.dp.toPx()), size) }.background(bg).border(1.dp, border)
                else Modifier.background(Brush.verticalGradient(listOf(Color.White, Color(0xFFE9EEF1))), RoundedCornerShape(6.dp)).border(1.dp, border, RoundedCornerShape(6.dp)),
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        BasicText(
            text,
            style = if (classic) TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, lineHeight = 14.sp)
            else TextStyle(color = Color(0xFF26343A), fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 15.sp),
        )
    }
}

/** The "TALK TO REVY" callout above the taskbar's Revy button, dismissed after 10 s (0.4 s fade). */
@Composable
fun RevyBuddyTip(modifier: Modifier = Modifier) {
    var show by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(10_000); show = false }
    val a by animateFloatAsState(if (show) 1f else 0f, tween(400), label = "revy")
    if (a <= 0f) return
    Box(
        modifier.graphicsLayer { alpha = a; translationY = (1 - a) * 4 * density }
            .drawBehind {
                val s = 7.dp.toPx(); val x = size.width - 10.dp.toPx() - s / 2; val y = size.height
                val p = Path().apply { moveTo(x - s * 0.7f, y); lineTo(x, y + s * 0.7f); lineTo(x + s * 0.7f, y); close() }
                drawPath(p, Color(0xF704051B)); drawPath(p, Color(0xD1FF00A0), style = Stroke(1.dp.toPx()))
            }
            .background(Color(0xF704051B), RoundedCornerShape(3.dp)).border(1.dp, Color(0xD1FF00A0), RoundedCornerShape(3.dp))
            .padding(horizontal = 9.dp, vertical = 7.dp),
    ) {
        BasicText("TALK TO REVY", style = TextStyle(color = Color(0xFFB9FBFF), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, fontSize = 9.sp, letterSpacing = 1.sp))
    }
}
