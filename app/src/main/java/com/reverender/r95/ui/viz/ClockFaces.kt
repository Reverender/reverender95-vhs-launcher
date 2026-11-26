package com.reverender.r95.ui.viz

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Calendar

/**
 * CLOCK_GADGET, ported from rr-os-styles.js: the Longhorn analog faces (a[] + the t() SVG face) and the
 * Windows 95 desktop-clock styles (we[] + the data-w95-clock CSS).
 */
class LhClock(val id: String, val name: String, val b1: Long, val b2: Long, val f1: Long, val f2: Long, val t: Long, val h: Long, val s: Long, val c: Long, val gloss: Float)

object ClockFaces {
    val analog = listOf(
        LhClock("classic", "Classic", 0xFF5A5A5A, 0xFF060606, 0xFFFFFFFF, 0xFFE2E2E2, 0xFF1C1C1C, 0xFF151515, 0xFFD4202C, 0xFF151515, 0.35f),
        LhClock("jade", "Jade Glass", 0xFF3F9B84, 0xFF05261E, 0xFF123029, 0xFF030D0B, 0xFF9FF2D6, 0xFFEAFFF8, 0xFF2DF0B0, 0xFF2DF0B0, 0.18f),
        LhClock("slate", "Slate", 0xFFA9B6C2, 0xFF323D46, 0xFF33404B, 0xFF141A20, 0xFFD3E0EC, 0xFFF4F7FA, 0xFF8CC0EC, 0xFFD3E0EC, 0.16f),
        LhClock("plex", "Plex Blue", 0xFF5AA8EA, 0xFF0A2C50, 0xFF0F3B66, 0xFF05162A, 0xFFA9D8FF, 0xFFFFFFFF, 0xFFFFB000, 0xFFFFB000, 0.2f),
        LhClock("ruby", "Ruby", 0xFFB5283D, 0xFF2A0409, 0xFFFFFAFB, 0xFFEFDDE0, 0xFF3A0B14, 0xFF2A0710, 0xFFC4394F, 0xFF2A0710, 0.32f),
        LhClock("amber", "Amber Night", 0xFF4A3011, 0xFF000000, 0xFF1A0F03, 0xFF000000, 0xFFFFB347, 0xFFFFD79A, 0xFFFF7B24, 0xFFFF7B24, 0.14f),
        LhClock("chrome", "Chrome", 0xFFFFFFFF, 0xFF7D8790, 0xFFFBFCFD, 0xFFDDE4EA, 0xFF3D4952, 0xFF1D252B, 0xFF1F7AE0, 0xFF1D252B, 0.4f),
        LhClock("vapor", "Vapor", 0xFFFF6AD5, 0xFF3A0B5C, 0xFF2A0D4A, 0xFF0B0218, 0xFF00F7FF, 0xFFFFFFFF, 0xFFFF00A0, 0xFF00F7FF, 0.2f),
    )
    fun face(id: String) = analog.firstOrNull { it.id == id } ?: analog[0]

    /** Windows 95 desktop-clock styles; every visit starts with Neon 1989. */
    val w95 = listOf("neon" to "Neon 1989", "lcd" to "LCD Alarm", "flip" to "Flip Clock", "dos" to "DOS Prompt", "chrome" to "Chrome", "tray" to "Win95 Panel")
}

/** Ticks once a second, aligned to the second like Ze(). */
@Composable
fun rememberClockNow(): Long {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            com.reverender.r95.core.AppVisibility.awaitForeground()
            now = System.currentTimeMillis()
            delay(1000L - now % 1000L + 8)
        }
    }
    return now
}

/** t(face, live): the 200 × 200 SVG face drawn natively. When not live the hands sit at 10:09:35. */
@Composable
fun AnalogClock(face: LhClock, live: Boolean, modifier: Modifier = Modifier) {
    val now = if (live) rememberClockNow() else 0L
    Canvas(modifier) {
        val k = minOf(size.width, size.height) / 200f
        val ox = (size.width - 200f * k) / 2; val oy = (size.height - 200f * k) / 2
        fun p(x: Float, y: Float) = Offset(ox + x * k, oy + y * k)
        val ctr = p(100f, 100f)
        // bezel: linearGradient x1 0 → x2 .35, y1 0 → y2 1 with stops b1 / b2 @ .55 / b1
        drawCircle(
            Brush.linearGradient(0f to Color(face.b1), 0.55f to Color(face.b2), 1f to Color(face.b1), start = p(2f, 2f), end = p(2f + 196f * 0.35f, 198f)),
            98f * k, ctr,
        )
        drawCircle(Color(0xA6000000), 98f * k, ctr, style = Stroke(2f * k))
        // dial: radialGradient cx 42% cy 34% r 72%
        drawCircle(Brush.radialGradient(listOf(Color(face.f1), Color(face.f2)), p(15f + 170f * 0.42f, 15f + 170f * 0.34f), 170f * 0.72f * k), 85f * k, ctr)
        drawCircle(Color(0x47000000), 85f * k, ctr, style = Stroke(4f * k))
        // 12 ticks
        for (i in 0 until 12) rotate(i * 30f, ctr) { drawLine(Color(face.t), p(100f, 21f), p(100f, if (i % 3 != 0) 35f else 38f), 5f * k, StrokeCap.Butt) }
        val (hr, mn, sc) = if (live) {
            val c = Calendar.getInstance().apply { timeInMillis = now }
            val s = c.get(Calendar.SECOND).toFloat(); val m = c.get(Calendar.MINUTE) + s / 60f; val h = c.get(Calendar.HOUR) + m / 60f
            Triple(h * 30f, m * 6f, s * 6f)
        } else Triple(304.5f, 54f, 210f)
        rotate(hr, ctr) { drawLine(Color(face.h), p(100f, 112f), p(100f, 56f), 8f * k, StrokeCap.Round) }
        rotate(mn, ctr) { drawLine(Color(face.h), p(100f, 114f), p(100f, 31f), 5.5f * k, StrokeCap.Round) }
        rotate(sc, ctr) { drawLine(Color(face.s), p(100f, 126f), p(100f, 26f), 2.2f * k, StrokeCap.Round) }
        drawCircle(Color(face.c), 6f * k, ctr)
        drawCircle(Color(face.s), 2.6f * k, ctr)
        // gloss ellipse (66 × 38 at 100,58), white → transparent
        scale(1f, 38f / 66f, p(100f, 58f)) {
            drawCircle(Brush.verticalGradient(listOf(Color.White, Color.White.copy(alpha = 0f)), p(100f, 58f - 66f).y, p(100f, 58f + 66f).y), 66f * k, p(100f, 58f), alpha = face.gloss)
        }
    }
}

/** The page's time string: toLocaleTimeString with 2-digit hour, minute, second. */
fun clockTimeString(context: android.content.Context, now: Long, seconds: Boolean = true): String {
    val is24 = android.text.format.DateFormat.is24HourFormat(context)
    val pat = if (is24) (if (seconds) "HH:mm:ss" else "HH:mm") else (if (seconds) "hh:mm:ss a" else "hh:mm a")
    return java.text.SimpleDateFormat(pat, java.util.Locale.getDefault()).format(java.util.Date(now))
}

private val Impact = FontFamily.SansSerif

/** #hm-clock-time in one of the non-neon styles (the neon one is HmClock's own animation). */
@Composable
fun W95ClockTime(style: String, text: String, modifier: Modifier = Modifier) {
    when (style) {
        "lcd" -> Box(
            modifier.border(3.dp, Color(0xFF2B2B2B)).background(Brush.verticalGradient(listOf(Color(0xFF0D1F0B), Color(0xFF061204)))).padding(horizontal = 12.dp, vertical = 5.dp),
        ) { BasicText(text, style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 30.sp, letterSpacing = 2.sp, color = Color(0xFFA8FF70), shadow = Shadow(Color(0xD982FF5A), Offset.Zero, 12f))) }
        "flip" -> FlipDigits(text, 30.sp, modifier)
        "dos" -> {
            val now = rememberClockNow()
            Box(modifier.drawBehind { drawRect(Color.Black, Offset(2.dp.toPx(), 2.dp.toPx()), size) }.background(Color.Black).border(2.dp, Color(0xFFC0C0C0)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                BasicText("C:\\> TIME $text" + if ((now / 600) % 2 == 0L) "_" else " ", style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = 1.sp, color = Color(0xFFD8D8D8)))
            }
        }
        "chrome" -> ChromeText(text, 36.sp, modifier)
        "tray" -> Box(
            modifier.background(Color(0xFFC0C0C0)).drawBehind {
                val p = 1.dp.toPx()
                drawRect(Color.White, Offset.Zero, Size(size.width, 2 * p)); drawRect(Color.White, Offset.Zero, Size(2 * p, size.height))
                drawRect(Color(0xFF404040), Offset(0f, size.height - 2 * p), Size(size.width, 2 * p)); drawRect(Color(0xFF404040), Offset(size.width - 2 * p, 0f), Size(2 * p, size.height))
            }.padding(horizontal = 12.dp, vertical = 6.dp),
        ) { BasicText(text, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = 1.sp, color = Color.Black)) }
        else -> BasicText(text, modifier, style = TextStyle(fontFamily = Impact, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, fontSize = 22.sp, color = Color(0xFF00F7FF)))
    }
}

/** .rr-flip-d digits, pink separators and AM/PM. */
@Composable
fun FlipDigits(text: String, size: TextUnit, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        val parts = Regex("\\d+|:|[^\\d:\\s]+").findAll(text).map { it.value }.toList()
        parts.forEach { part ->
            when {
                part.all { it.isDigit() } -> part.forEach { d ->
                    Box(
                        Modifier.padding(horizontal = 1.dp).drawBehind { drawRect(Color.Black, Offset(0f, 3.dp.toPx()), this.size) }
                            .background(Brush.verticalGradient(0f to Color(0xFF323232), 0.49f to Color(0xFF262626), 0.5f to Color(0xFF111111), 1f to Color(0xFF1D1D1D)), androidx.compose.foundation.shape.RoundedCornerShape(5.dp))
                            .drawBehind { drawLine(Color(0xD9000000), Offset(0f, this.size.height / 2), Offset(this.size.width, this.size.height / 2), 1.dp.toPx()) }
                            .padding(horizontal = 4.dp, vertical = 3.dp),
                    ) { BasicText(d.toString(), style = TextStyle(fontFamily = Impact, fontWeight = FontWeight.Black, fontSize = size, color = Color(0xFFF4F4F4))) }
                }
                part == ":" -> BasicText(":", Modifier.padding(horizontal = 2.dp), style = TextStyle(fontFamily = Impact, fontSize = size, color = Color(0xFFFF4FB8)))
                else -> BasicText(part, Modifier.padding(start = 5.dp), style = TextStyle(fontFamily = Impact, fontSize = size * 0.45f, color = Color(0xFFFF4FB8)))
            }
        }
    }
}

/** Chrome: metallic gradient text with a thin dark outline. */
@Composable
fun ChromeText(text: String, size: TextUnit, modifier: Modifier = Modifier) {
    val st = TextStyle(fontFamily = Impact, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, fontSize = size, letterSpacing = 3.sp)
    Box(modifier) {
        BasicText(text, Modifier.padding(top = 2.dp), style = st.copy(color = Color(0x99000000)))
        BasicText(text, style = st.copy(brush = Brush.verticalGradient(0f to Color.White, 0.42f to Color(0xFFC9D2DB), 0.5f to Color(0xFF3C444C), 0.58f to Color(0xFF9FB0C0), 1f to Color(0xFFF4F8FB))))
        BasicText(text, style = st.copy(color = Color(0xFF1B2026), drawStyle = androidx.compose.ui.graphics.drawscope.Stroke(0.7f * 2)))
    }
}

/** .rr-w95ck-prev: the 86 × 52 preview tile of a Windows 95 clock style. */
@Composable
fun W95ClockPreview(style: String, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        when (style) {
            "neon" -> Box(Modifier.matchParent().background(Color(0xFF1A0B2E)), contentAlignment = Alignment.Center) {
                Box {
                    val st = TextStyle(fontFamily = Impact, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, fontSize = 22.sp)
                    BasicText("12:00", Modifier.padding(start = 2.dp, top = 2.dp), style = st.copy(color = Color(0xFFFF00A0)))
                    BasicText("12:00", style = st.copy(color = Color(0xFF00F7FF), shadow = Shadow(Color(0xFF00F7FF), Offset.Zero, 16f)))
                }
            }
            "lcd" -> Box(Modifier.matchParent().background(Color(0xFF061204)).border(2.dp, Color(0xFF2B2B2B)), contentAlignment = Alignment.Center) {
                BasicText("12:00", style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFA8FF70), shadow = Shadow(Color(0xD982FF5A), Offset.Zero, 10f)))
            }
            "flip" -> Box(Modifier.matchParent().background(Color(0xFF3A3A3A)), contentAlignment = Alignment.Center) { FlipDigits("12:00", 16.sp) }
            "dos" -> Box(Modifier.matchParent().background(Color.Black), contentAlignment = Alignment.Center) {
                BasicText("C:\\> 12:00", style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFD8D8D8)))
            }
            "chrome" -> ChromeText("12:00", 22.sp)
            "tray" -> Box(Modifier.matchParent().background(Color(0xFFC0C0C0)).drawBehind {
                val p = 1.dp.toPx()
                drawRect(Color.White, Offset.Zero, Size(size.width, 2 * p)); drawRect(Color.White, Offset.Zero, Size(2 * p, size.height))
                drawRect(Color(0xFF404040), Offset(0f, size.height - 2 * p), Size(size.width, 2 * p)); drawRect(Color(0xFF404040), Offset(size.width - 2 * p, 0f), Size(2 * p, size.height))
            }, contentAlignment = Alignment.Center) { BasicText("12:00", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)) }
        }
    }
}

private fun Modifier.matchParent() = this.fillMaxSize()
