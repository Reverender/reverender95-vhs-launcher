package com.reverender.r95.ui.vhs

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.first
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.reverender.r95.LocalAppState
import com.reverender.r95.audio.AmpEngine
import com.reverender.r95.audio.AudioBus
import com.reverender.r95.core.Catalog
import com.reverender.r95.os.Haptics
import com.reverender.r95.os.Launcher
import com.reverender.r95.os.OsPrefs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.layout.windowInsetsPadding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.layout
import kotlinx.coroutines.flow.collectLatest

/**
 * VHS: a light-weight third shell — a 1980s VCR on-screen menu instead of a desktop.
 * No windows, wallpapers, toys or visualizers: one blue OSD screen with the dot-matrix font, a built-in
 * VAPOR_AMP (transport, volume, tracking, tape speed, counter), an app search typed on the OSD letters,
 * four swipe shortcuts and a SETUP menu. The VCR/CRT look (scanlines, glow, tracking noise) is exclusive to it.
 */

/** OSD colours and the dot size used by every VHS element on screen. */
internal class Osd(val fg: Color, val bg: Color, val px: Float)
internal val LocalOsd = staticCompositionLocalOf { Osd(Color.White, Color.Blue, 6f) }

/** Background / text presets (the "AUTO PAL SECAM NTSC" row and SETUP's colour lists). */
internal val VHS_COLORS = listOf(
    "BLUE" to 0xFF1A1AD0, "BLACK" to 0xFF050505, "NAVY" to 0xFF0A0A5A, "TEAL" to 0xFF006C6C, "GREEN" to 0xFF0B4D16,
    "PURPLE" to 0xFF3B0A6E, "MAGENTA" to 0xFF8A0F6A, "RED" to 0xFF7A0A0A, "GREY" to 0xFF4A4A4A, "WHITE" to 0xFFF2F2F2,
)
internal val VHS_TEXT = listOf(
    "WHITE" to 0xFFFFFFFF, "YELLOW" to 0xFFFFF04A, "GREEN" to 0xFF39FF14, "CYAN" to 0xFF5CF6FF, "AMBER" to 0xFFFFB000,
    "PINK" to 0xFFFF6EC7, "BLACK" to 0xFF000000, "BLUE" to 0xFF1A1AD0,
    // vaporwave / mallsoft pastels
    "MALL PINK" to 0xFFFFB3DE, "MINT" to 0xFF9FFFE0, "LAVENDER" to 0xFFC9A0FF, "PEACH" to 0xFFFFCBA4, "SKY" to 0xFF9BE7FF, "PLUM" to 0xFF3A1F5D,
)
/** multicolour backgrounds (top to bottom); the first colour also stands in as the plain background colour */
internal val VHS_GRADS = listOf(
    "MALLSOFT" to listOf(0xFFFFB3DE, 0xFFC9A0FF, 0xFF9FFFE0),
    "VAPORWAVE" to listOf(0xFFFF6EC7, 0xFF7A5CFF, 0xFF29F3FF),
    "PLAZA" to listOf(0xFF3A1F5D, 0xFF7A4A8F, 0xFFF7B7D2),
    "MIAMI" to listOf(0xFF2DE2E6, 0xFF8A5CFF, 0xFFFF3CAC),
    "SUNSET" to listOf(0xFFFFD319, 0xFFFF2975, 0xFF8C1EFF),
    "SEAPUNK" to listOf(0xFF00FFD1, 0xFF3D7BFF, 0xFF8A2BE2),
)
/** PASTEL MIX text: letter by letter through these */
internal val VHS_MIX = listOf(Color(0xFFFFB3DE), Color(0xFF9FFFE0), Color(0xFFC9A0FF), Color(0xFFFFCBA4), Color(0xFF9BE7FF), Color(0xFFFFF5A8))
internal fun vhsGradBrush(alpha: Float = 1f): Brush? = VHS_GRADS.getOrNull(OsPrefs.vhsGrad)?.second?.let { cs -> Brush.verticalGradient(cs.map { Color(it.toInt()).copy(alpha = alpha) }) }
/** the four "broadcast standard" presets: background + text */
private val STANDARDS = listOf(
    "AUTO" to (0xFF1A1AD0 to 0xFFFFFFFF), "PAL" to (0xFF050505 to 0xFF39FF14),
    "SECAM" to (0xFF3B0A6E to 0xFFFFF04A), "NTSC" to (0xFF006C6C to 0xFFFFFFFF),
)

// ---------------------------------------------------------------- text

/**
 * A ticker line [cols] characters wide: the text runs in from the right, off the left edge and round again,
 * so long names (radio stations, files, songs) can always be read. Moved on the GPU layer only (no redraw).
 */
@Composable
internal fun VhsMarquee(text: String, cols: Int = 21, modifier: Modifier = Modifier, color: Color? = null, running: Boolean = true, secPerChar: Float = 0.16f, fill: Boolean = false, swapAtLoop: Boolean = false) {
    val o = LocalOsd.current
    val d = LocalDensity.current
    val wDp = with(d) { ((cols * 6 + 1) * o.px).toDp() }
    val hDp = with(d) { (9 * o.px).toDp() }
    // [fill]: as wide as the space it's in (the title block's line), else [cols] characters
    val fieldPx = remember { androidx.compose.runtime.mutableFloatStateOf((cols * 6 + 1) * o.px) }
    val fieldW = if (fill) fieldPx.floatValue else (cols * 6 + 1) * o.px
    // what runs now; with [swapAtLoop] (the news) a new text waits until the current one has gone all the way
    // through, otherwise (a new song) it starts over at once
    var shown by remember { mutableStateOf(text) }
    // how far the current pass has run (kept while paused / hidden, so it carries on where it was)
    val dist = remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    LaunchedEffect(text) { if (!swapAtLoop && text != shown) { shown = text; dist.floatValue = 0f } }
    val shift = remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    val pageHidden = LocalPageHidden.current
    val latest by androidx.compose.runtime.rememberUpdatedState(text)
    LaunchedEffect(o.px, running, secPerChar, fieldW, pageHidden) {
        // stopped: the name stands at the left; it carries on from there when it plays again
        if (!running) { if (!swapAtLoop) shown = latest; dist.floatValue = fieldW; shift.floatValue = 0f; return@LaunchedEffect }
        if (pageHidden) return@LaunchedEffect
        val speed = o.px * 6 / secPerChar // a character every secPerChar seconds
        var last = 0L
        while (true) {
            androidx.compose.runtime.withFrameNanos { t ->
                val dt = if (last == 0L) 0f else ((t - last) / 1e9f).coerceAtMost(0.1f)
                last = t
                var dd = dist.floatValue + dt * speed
                if (dd >= fieldW + (shown.length * 6 + 1) * o.px) { dd = 0f; if (shown != latest) shown = latest } // round again
                dist.floatValue = dd
                shift.floatValue = fieldW - dd
            }
        }
    }
    // only the letters inside the window are drawn, each frame (a long news line drawn whole is tens of
    // thousands of pixels wide and stalled the launcher)
    val fg = color ?: o.fg
    Canvas(modifier.then(if (fill) Modifier.fillMaxWidth().height(hDp) else Modifier.size(wDp, hDp)).clipToBounds()
        .onSizeChanged { if (fill) fieldPx.floatValue = it.width.toFloat() }) {
        val px = o.px
        val cw = 6 * px
        val x0 = shift.floatValue
        val first = ((-x0) / cw).toInt().coerceAtLeast(0)
        val last = ((size.width - x0) / cw).toInt() + 1
        if (first >= shown.length || last <= 0) return@Canvas
        val part = shown.substring(first, minOf(shown.length, last))
        val x = x0 + first * cw + px
        if (OsPrefs.vhsBloom) drawBloom(part, x, px, px, fg, 1.6f * VhsBloomPulse.level.floatValue)
        val ca = OsPrefs.vhsChroma
        if (ca > 0.01f) {
            val dx = px * (0.35f + 1.6f * ca); val al = 0.35f + 0.4f * ca
            drawGlyphs(part, x - dx, px, px, null, Color(0xFFFF1A40).copy(alpha = al))
            drawGlyphs(part, x + dx, px, px, null, Color(0xFF1AE6FF).copy(alpha = al))
        }
        drawGlyphs(part, x, px, px, Color.Black.copy(alpha = 0.75f), fg)
    }
}

/** A line of OSD text: dot-matrix glyphs with a hard black drop shadow, like a VCR character generator. */
@Composable
fun VhsText(text: String, modifier: Modifier = Modifier, scale: Float = 1f, color: Color? = null, invert: Boolean = false) {
    val o = LocalOsd.current
    val px = o.px * scale
    val fg = color ?: o.fg
    val d = LocalDensity.current
    val wDp = with(d) { ((text.length * 6 + 1) * px).toDp() }
    val hDp = with(d) { (9 * px).toDp() }
    val bloom = OsPrefs.vhsBloom
    Box(modifier.size(wDp, hDp).graphicsLayer {}) {
    // bloom on its own layer: its brightness breathes and flickers like a real CRT (VhsBloomPulse) without
    // the text having to be redrawn
    if (bloom) Canvas(Modifier.matchParentSize().graphicsLayer { alpha = VhsBloomPulse.level.floatValue }) {
        if (invert) {
            // a lit box blooms past its edges
            drawRect(fg.copy(alpha = 0.16f), Offset(-px * 1.6f, -px * 1.6f), Size(size.width + px * 3.2f, size.height + px * 3.2f))
            drawRect(fg.copy(alpha = 0.24f), Offset(-px * 0.7f, -px * 0.7f), Size(size.width + px * 1.4f, size.height + px * 1.4f))
        } else drawBloom(text, px, px, px, fg, 1.6f)
    }
    Canvas(Modifier.matchParentSize()) {
        if (invert) drawRect(fg)
        val ca = OsPrefs.vhsChroma
        if (ca > 0.01f) {
            // chromatic aberration: the red and blue parts of the signal land a little left and right
            val dx = px * (0.35f + 1.6f * ca); val al = 0.35f + 0.4f * ca
            if (invert) {
                drawRect(Color(0xFFFF1A40).copy(alpha = al * 0.8f), Offset(-dx, 0f), size)
                drawRect(Color(0xFF1AE6FF).copy(alpha = al * 0.8f), Offset(dx, 0f), size)
                drawRect(fg)
            } else {
                drawGlyphs(text, px - dx, px, px, null, Color(0xFFFF1A40).copy(alpha = al))
                drawGlyphs(text, px + dx, px, px, null, Color(0xFF1AE6FF).copy(alpha = al))
            }
        }
        val ink = if (invert) o.bg else fg
        drawGlyphs(text, px, px, px, if (invert) null else Color.Black.copy(alpha = 0.75f), ink)
    }
    }
}

/**
 * How bright the bloom glows right now: a slow breath, the fast flicker of the tube, a lift on loud music
 * and a flash during tape glitches. Updated by the shell's CRT clock.
 */
internal object VhsBloomPulse {
    val level = mutableFloatStateOf(0.85f)
    private val rnd = java.util.Random()
    fun update(nowMs: Long, music: Float, glitch: Boolean) {
        val t = nowMs / 1000.0
        val breath = 0.10 * kotlin.math.sin(t * 2 * Math.PI / 3.7) + 0.05 * kotlin.math.sin(t * 2 * Math.PI / 1.3)
        val flicker = (rnd.nextFloat() - 0.5f) * 0.10f
        val v = 0.72f + breath.toFloat() + flicker + music * 0.28f + (if (glitch) 0.25f else 0f)
        level.floatValue = v.coerceIn(0.35f, 1f)
    }
}

/** Bloom: two soft, oversized passes of each lit dot under the text (cheap stand-in for a blur). */
/** VHS lettering styles, after the on-screen displays of different VCR makers */
internal val VHS_FONTS = listOf("VCR OSD", "BOLD OSD", "BLOCK 3D", "ITALIC", "DOT MATRIX", "SCANLINE", "VFD PANEL", "ROUNDED")
/** OUTLINE style: is cell (r, b) next to the letter (but not part of it)? */
private fun outlineAt(rows: IntArray, r: Int, b: Int): Boolean {
    fun on(rr: Int, bb: Int) = rr in 0..6 && bb in 0..4 && rows[rr] and (1 shl (4 - bb)) != 0
    if (on(r, b)) return false
    for (dr in -1..1) for (db in -1..1) if ((dr != 0 || db != 0) && on(r + dr, b + db)) return true
    return false
}
/** a glyph row in the current lettering style (BOLD thickens the strokes) */
private fun styledBits(bits: Int, @Suppress("UNUSED_PARAMETER") style: Int) = bits
/** ITALIC leans the rows over */
private fun rowShift(r: Int, style: Int, px: Float) = if (style == 3) (6 - r) * px * 0.17f else 0f

internal fun DrawScope.drawBloom(text: String, x0: Float, y0: Float, px: Float, ink: Color, gain: Float = 1f) {
    val outer = ink.copy(alpha = (0.06f * gain).coerceAtMost(1f)); val inner = ink.copy(alpha = (0.11f * gain).coerceAtMost(1f))
    val so = Size(px * 3.4f, px * 3.4f); val si = Size(px * 2f, px * 2f)
    val style = OsPrefs.vhsFont
    text.forEachIndexed { i, c ->
        val rows = VhsFont.rows(c) ?: return@forEachIndexed
        val gx = x0 + i * 6 * px
        for (r in 0 until 7) {
            val bits = styledBits(rows[r], style)
            if (bits == 0) continue
            val sh = rowShift(r, style, px)
            for (b in 0 until 5) if (bits and (1 shl (4 - b)) != 0) {
                val x = gx + b * px + sh; val y = y0 + r * px
                drawRect(outer, Offset(x - px * 1.2f, y - px * 1.2f), so)
                drawRect(inner, Offset(x - px * 0.5f, y - px * 0.5f), si)
            }
        }
    }
}

internal fun DrawScope.drawGlyphs(text: String, x0: Float, y0: Float, px: Float, shadow: Color?, ink: Color) {
    val style = OsPrefs.vhsFont
    // the pixel each style is built from
    val dot = when (style) { 1 -> Size(px * 1.35f, px); 5 -> Size(px, px * 0.42f); 6 -> Size(px * 0.78f, px * 0.78f); else -> Size(px, px) }
    val corner = androidx.compose.ui.geometry.CornerRadius(px * 0.45f, px * 0.45f)
    // PASTEL MIX: ordinary text (in the TEXT colour) goes letter by letter through the pastels
    val mix = OsPrefs.vhsFgMix && ink.value == Color(OsPrefs.vhsFg.toInt()).value
    var n = 0
    for (pass in 0..1) {
        if (pass == 0 && shadow == null) continue
        val off = if (pass == 0) px * 0.6f else 0f
        text.forEachIndexed { i, c ->
            val col = if (pass == 0) shadow!! else if (mix) (if (c == ' ') ink else VHS_MIX[n++ % VHS_MIX.size]) else ink
            val rows = VhsFont.rows(c) ?: return@forEachIndexed
            val gx = x0 + i * 6 * px + off
            for (r in 0 until 7) {
                val bits = styledBits(rows[r], style)
                if (bits == 0) continue
                val sh = rowShift(r, style, px)
                val y = y0 + r * px + off
                // VFD PANEL: every cell of the 5×7 grid glows faintly, like an unlit display
                if (style == 6 && pass == 1) for (b in 0 until 5) if (bits and (1 shl (4 - b)) == 0) drawRect(col.copy(alpha = col.alpha * 0.10f), Offset(gx + b * px + px * 0.11f, y + px * 0.11f), dot)
                for (b in 0 until 5) if (bits and (1 shl (4 - b)) != 0) {
                    val x = gx + b * px + sh
                    when (style) {
                        // BLOCK 3D: a deep extruded shadow down-right, then the letter
                        2 -> { if (pass == 1) drawRect(col.copy(alpha = col.alpha * 0.38f), Offset(x + px * 0.75f, y + px * 0.75f), dot); drawRect(col, Offset(x, y), dot) }
                        4 -> drawCircle(col, px * 0.47f, Offset(x + px / 2, y + px / 2))
                        5 -> { drawRect(col, Offset(x, y), dot); drawRect(col.copy(alpha = col.alpha * 0.55f), Offset(x, y + px * 0.55f), dot) }
                        6 -> { drawRect(col.copy(alpha = col.alpha * 0.3f), Offset(x - px * 0.1f, y - px * 0.1f), Size(px, px)); drawRect(col, Offset(x + px * 0.11f, y + px * 0.11f), dot) }
                        // ROUNDED: round dots joined to their neighbours, so strokes come out smooth with round ends
                        7 -> {
                            drawRoundRect(col, Offset(x, y), Size(px, px), corner)
                            if (b < 4 && bits and (1 shl (3 - b)) != 0) drawRect(col, Offset(x + px * 0.5f, y), Size(px, px))
                            val below = if (r < 6) styledBits(rows[r + 1], style) else 0
                            if (below and (1 shl (4 - b)) != 0) drawRect(col, Offset(x, y + px * 0.5f), Size(px, px))
                        }
                        else -> drawRect(col, Offset(x, y), dot)
                    }
                }
            }
        }
    }
}

/** Tappable OSD text (a "button" is just text, optionally boxed or inverted when selected). */
@Composable
internal fun VhsKey(text: String, selected: Boolean = false, boxed: Boolean = false, scale: Float = 1f, onLong: (() -> Unit)? = null, onTap: () -> Unit) {
    val o = LocalOsd.current
    // always the latest action (the key's touch handler outlives recompositions; a stale one acted on old state)
    val tap by androidx.compose.runtime.rememberUpdatedState(onTap)
    val long by androidx.compose.runtime.rememberUpdatedState(onLong)
    Box(
        Modifier.then(if (boxed && !selected) Modifier.drawBehind {
            val w = o.px * 0.5f
            glowRect(o.fg, Offset(w / 2, w / 2), Size(size.width - w, size.height - w), o.px, w)
            drawRect(o.fg, Offset(w / 2, w / 2), Size(size.width - w, size.height - w), style = Stroke(w))
        } else Modifier)
            .pointerInput(text, onLong != null) {
                detectTapGestures(onLongPress = if (onLong == null) null else { _ -> long?.invoke() }) { Haptics.tick(); tap() }
            },
    ) { VhsText(text, invert = selected, scale = scale) }
}

/** The two OSD letter rows; the key you touch lights up (inverted, a little bigger, glowing) for a moment. */
@Composable
internal fun VhsLetterPad(onChar: (Char) -> Unit) {
    val o = LocalOsd.current
    val rows = listOf("ABCDEFGHIJKLMNOPQRST", "UVWXYZ0123456789&-:.")
    var pressed by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var pressN by remember { mutableIntStateOf(0) }
    LaunchedEffect(pressN) { if (pressN > 0) { delay(380); pressed = null } }
    val on by androidx.compose.runtime.rememberUpdatedState(onChar)
    rows.forEachIndexed { ri, r ->
        Canvas(Modifier.size(pxDp(r.length * 6f + 1f), pxDp(9f)).pointerInput(r) {
            detectTapGestures(onPress = { pos -> pressed = ri to (pos.x / (o.px * 6)).toInt().coerceIn(0, r.length - 1); pressN++ }) { pos ->
                val i = (pos.x / (o.px * 6)).toInt().coerceIn(0, r.length - 1)
                Haptics.tick(); pressed = ri to i; pressN++
                on(r[i])
            }
        }) {
            val px = o.px
            if (OsPrefs.vhsBloom) drawBloom(r, px, px, px, o.fg)
            drawGlyphs(r, px, px, px, Color.Black.copy(alpha = 0.75f), o.fg)
            val hit = pressed
            if (hit != null && hit.first == ri) {
                val x = hit.second * 6 * px
                drawRect(o.fg.copy(alpha = 0.25f), Offset(x - px * 1.5f, -px * 1.5f), Size(px * 9f, px * 12f))
                drawRect(o.fg, Offset(x - px * 0.5f, -px * 0.5f), Size(px * 7f, px * 10f))
                val big = px * 1.3f
                drawGlyphs(r[hit.second].toString(), x + px * 3f - big * 2.5f, px * 4.5f - big * 3.5f, big, null, o.bg)
            }
        }
    }
}

/** The line being typed (with its name), on a panel just above the phone keyboard. */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun VhsKbPreview(kb: VhsKb?) {
    if (kb?.label == null || !androidx.compose.foundation.layout.WindowInsets.isImeVisible) return
    val o = LocalOsd.current
    val blink = rememberBlink()
    Box(Modifier.fillMaxSize().imePadding(), contentAlignment = Alignment.BottomCenter) {
        Column(
            Modifier.fillMaxWidth().background(o.bg.copy(alpha = 0.96f)).drawBehind {
                drawRect(o.fg, Offset.Zero, Size(size.width, o.px * 0.6f))
            }.padding(horizontal = pxDp(2f), vertical = pxDp(2f)),
            verticalArrangement = Arrangement.spacedBy(pxDp(1.2f)),
        ) {
            VhsText(kb.label, invert = true)
            val lines = wrap(vhsSafe(kb.get()).replace(Regex(" +"), " ") + if (blink) "_" else " ", 20).takeLast(4)
            lines.forEach { VhsText(it) }
        }
    }
}

/** tape-spine colours for the TAPE SHELF drawer */
private val SPINES = listOf(Color(0xFFFF6EC7), Color(0xFF29F3FF), Color(0xFFFFD319), Color(0xFF9DFF5C), Color(0xFFA06BFF), Color(0xFFFF2FA0), Color(0xFFFF7A2E))

/** true for a page kept composed but hidden behind another menu */
internal val LocalPageHidden = androidx.compose.runtime.compositionLocalOf { false }

/** A search field the phone keyboard can type into (swipe up); [enter] runs on the keyboard's enter key. */
internal class VhsKb(val get: () -> String, val set: (String) -> Unit, val raw: Boolean = false, val label: String? = null, val enter: () -> Unit)
internal val LocalKbHolder = staticCompositionLocalOf { androidx.compose.runtime.mutableStateOf<VhsKb?>(null) }

/** While composed, swipe up types into this field. */
@Composable
internal fun RegisterKb(kb: VhsKb) {
    if (LocalPageHidden.current) return // a page kept alive behind another menu doesn't take the keyboard
    val holder = LocalKbHolder.current
    val cur by androidx.compose.runtime.rememberUpdatedState(kb)
    val proxy = remember { VhsKb({ cur.get() }, { cur.set(it) }, kb.raw, kb.label) { cur.enter() } }
    androidx.compose.runtime.DisposableEffect(proxy) {
        holder.value = proxy
        onDispose { if (holder.value === proxy) holder.value = null }
    }
}

/** Only what the dot-matrix font can show: upper case, accents dropped. */
internal fun vhsSafe(s: String): String {
    val n = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "")
    return n.uppercase().map { c -> if (c == ' ' || VhsFont.rows(c) != null) c else ' ' }.joinToString("")
}

/** Bloom for drawn shapes (bars, boxes, lines), matching the text glow. */
internal fun DrawScope.glowRect(fg: Color, tl: Offset, sz: Size, px: Float, stroke: Float = 0f) {
    if (!OsPrefs.vhsBloom) return
    val k = VhsBloomPulse.level.floatValue * 1.25f // breathes with the text bloom
    if (stroke > 0f) {
        drawRect(fg.copy(alpha = 0.07f * k), tl, sz, style = Stroke(stroke + px * 3f))
        drawRect(fg.copy(alpha = 0.13f * k), tl, sz, style = Stroke(stroke + px * 1.4f))
    } else {
        drawRect(fg.copy(alpha = 0.06f * k), Offset(tl.x - px * 1.3f, tl.y - px * 1.3f), Size(sz.width + px * 2.6f, sz.height + px * 2.6f))
        drawRect(fg.copy(alpha = 0.12f * k), Offset(tl.x - px * 0.55f, tl.y - px * 0.55f), Size(sz.width + px * 1.1f, sz.height + px * 1.1f))
    }
}

/** Opens the phone keyboard for the search on screen (same as swiping up). */
internal val LocalOpenKb = staticCompositionLocalOf<() -> Unit> { {} }

// ---------------------------------------------------------------- bars

@Composable
internal fun pxDp(n: Float): Dp = with(LocalDensity.current) { (LocalOsd.current.px * n).toDp() }

/** VOLUME / TRACKING style bar: an outlined box with a filled part; drag or tap to set 0..1. */
@Composable
internal fun VhsBar(value: Float, modifier: Modifier = Modifier, slider: Boolean = false, centreMark: Boolean = false, onChange: ((Float) -> Unit)? = null) {
    val o = LocalOsd.current
    val change by androidx.compose.runtime.rememberUpdatedState(onChange)
    Canvas(
        modifier.fillMaxWidth().height(pxDp(if (slider) 5f else 4f)).then(
            if (onChange == null) Modifier else Modifier.pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    change?.invoke((down.position.x / size.width).coerceIn(0f, 1f))
                    while (true) {
                        val ev = awaitPointerEvent()
                        val p = ev.changes.firstOrNull() ?: break
                        if (!p.pressed) break
                        p.consume()
                        change?.invoke((p.position.x / size.width).coerceIn(0f, 1f))
                    }
                }
            },
        ),
    ) {
        val px = o.px
        val shadow = Color.Black.copy(alpha = 0.7f)
        if (!slider) {
            // outlined box, filled to the value
            drawRect(shadow, Offset(px * 0.6f, px * 0.6f), Size(size.width - px * 0.6f, size.height - px * 0.6f), style = Stroke(px * 0.5f))
            glowRect(o.fg, Offset.Zero, Size(size.width - px * 0.6f, size.height - px * 0.6f), px, px * 0.5f)
            drawRect(o.fg, Offset.Zero, Size(size.width - px * 0.6f, size.height - px * 0.6f), style = Stroke(px * 0.5f))
            val inner = (size.width - px * 2.6f) * value.coerceIn(0f, 1f)
            if (inner > 0f) glowRect(o.fg, Offset(px, px), Size(inner, size.height - px * 2.6f), px)
            drawRect(o.fg, Offset(px, px), Size(inner, size.height - px * 2.6f))
        } else {
            // TRACKING: a thin rail with a hollow handle
            val y = size.height / 2
            drawRect(shadow, Offset(px * 0.6f, y - px * 0.25f + px * 0.6f), Size(size.width - px * 0.6f, px * 0.5f))
            glowRect(o.fg, Offset(0f, y - px * 0.25f), Size(size.width - px * 0.6f, px * 0.5f), px)
            drawRect(o.fg, Offset(0f, y - px * 0.25f), Size(size.width - px * 0.6f, px * 0.5f))
            if (centreMark) drawRect(o.fg, Offset((size.width - px * 3f) * 0.5f + px * 1.05f, y - px * 1.6f), Size(px * 0.5f, px * 3.2f))
            val hx = (size.width - px * 3f) * value.coerceIn(0f, 1f)
            glowRect(o.fg, Offset(hx, px * 0.3f), Size(px * 2.6f, size.height - px * 1f), px)
            drawRect(o.bg, Offset(hx, px * 0.3f), Size(px * 2.6f, size.height - px * 1f))
            drawRect(o.fg, Offset(hx, px * 0.3f), Size(px * 2.6f, size.height - px * 1f), style = Stroke(px * 0.5f))
        }
    }
}

/** The dashed tick row from the menu (here: a live audio level meter). */
@Composable
internal fun VhsTicks(level: Float) {
    val o = LocalOsd.current
    Canvas(Modifier.fillMaxWidth().height(pxDp(3f))) {
        val px = o.px
        val n = (size.width / (px * 2f)).toInt()
        val lit = (n * level.coerceIn(0f, 1f)).roundToInt()
        for (i in 0 until n) {
            val x = i * px * 2f
            drawRect(Color.Black.copy(alpha = 0.6f), Offset(x + px * 0.5f, px * 0.6f), Size(px, px * 2f))
            if (i < lit) glowRect(o.fg, Offset(x, 0f), Size(px, px * 2f), px * 0.7f)
            drawRect(if (i < lit) o.fg else o.fg.copy(alpha = 0.28f), Offset(x, 0f), Size(px, px * 2f))
        }
    }
}

/** BEGIN ──▼── END: the tape position, drag to seek. */
@Composable
internal fun VhsTimeline(value: Float, onSeek: (Float) -> Unit) {
    val o = LocalOsd.current
    val seek by androidx.compose.runtime.rememberUpdatedState(onSeek)
    Canvas(
        Modifier.fillMaxWidth().height(pxDp(9f)).pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown()
                fun at(x: Float) = ((x - o.px * 3) / (size.width - o.px * 6)).coerceIn(0f, 1f)
                seek(at(down.position.x))
                while (true) {
                    val p = awaitPointerEvent().changes.firstOrNull() ?: break
                    if (!p.pressed) break
                    p.consume(); seek(at(p.position.x))
                }
            }
        },
    ) {
        val px = o.px
        val y = size.height * 0.62f
        val l = px * 3; val r = size.width - px * 3
        val sh = Color.Black.copy(alpha = 0.7f)
        glowRect(o.fg, Offset(l, y), Size(r - l, px * 0.6f), px)
        for ((col, off) in listOf(sh to px * 0.6f, o.fg to 0f)) {
            drawRect(col, Offset(l + off, y + off), Size(r - l, px * 0.6f))
            drawRect(col, Offset(l + off, y - px * 1.5f + off), Size(px * 0.6f, px * 3.6f))
            drawRect(col, Offset(r - px * 0.6f + off, y - px * 1.5f + off), Size(px * 0.6f, px * 3.6f))
            // small ticks between BEGIN and END
            for (k in 1..9) drawRect(col, Offset(l + (r - l) * k / 10f + off, y - px * 0.8f + off), Size(px * 0.5f, px * 0.8f))
        }
        val mx = l + (r - l) * value.coerceIn(0f, 1f)
        if (OsPrefs.vhsBloom) drawBloom("▼", mx - px * 2.5f, y - px * 8.4f, px, o.fg)
        drawGlyphs("▼", mx - px * 2.5f, y - px * 8.4f, px, sh, o.fg)
    }
}

/** The boxed header with two rules, like "VHS/VCR MENU". */
@Composable
internal fun VhsHeader(title: String, sub: String, subContent: (@Composable () -> Unit)? = null) {
    val o = LocalOsd.current
    Box(Modifier.fillMaxWidth().padding(bottom = pxDp(2f))) {
        Canvas(Modifier.matchParentSize()) {
            val px = o.px
            glowRect(o.fg, Offset.Zero, Size(size.width - px, size.height - px), px, px * 0.5f)
            glowRect(o.fg, Offset(0f, size.height * 0.5f), Size(size.width - px, px * 0.5f), px)
            for ((col, off) in listOf(Color.Black.copy(alpha = 0.7f) to px * 0.6f, o.fg to 0f)) {
                drawRect(col, Offset(off, off), Size(size.width - px, size.height - px), style = Stroke(px * 0.5f))
                drawRect(col, Offset(off, size.height * 0.5f + off), Size(size.width - px, px * 0.5f))
            }
        }
        Column(Modifier.padding(horizontal = pxDp(2f), vertical = pxDp(1f))) {
            VhsText(title)
            Spacer(Modifier.height(pxDp(1f)))
            if (subContent != null) subContent() else VhsText(sub)
        }
    }
}

/** one-line version of [VhsHeader] */
@Composable
internal fun VhsHeader1(title: String, content: (@Composable () -> Unit)? = null) {
    val o = LocalOsd.current
    Box(Modifier.fillMaxWidth().padding(bottom = pxDp(2f))) {
        Canvas(Modifier.matchParentSize()) {
            val px = o.px
            glowRect(o.fg, Offset.Zero, Size(size.width - px, size.height - px), px, px * 0.5f)
            for ((col, off) in listOf(Color.Black.copy(alpha = 0.7f) to px * 0.6f, o.fg to 0f))
                drawRect(col, Offset(off, off), Size(size.width - px, size.height - px), style = Stroke(px * 0.5f))
        }
        Column(Modifier.padding(horizontal = pxDp(2f), vertical = pxDp(1f))) { if (content != null) content() else VhsText(title) }
    }
}

// ---------------------------------------------------------------- CRT

/**
 * CURVE: the picture bends like a curved CRT tube towards the corners (Android 13+, a GPU shader on the whole
 * screen): each corner is pulled in and rounded off into black, with a little colour fringe and darkening at
 * the tube's edge. Only the corners move; the middle of every edge stays put, so nothing gets cut off.
 */
private const val CURVE_SRC = """
uniform shader content;
uniform float2 size;
uniform float k;
half4 main(float2 p) {
    float2 uv = p / size * 2.0 - 1.0;
    float2 w = uv.yx * uv.yx;
    float2 q = uv * (1.0 + k * w);
    if (max(abs(q.x), abs(q.y)) > 1.0) return half4(0.0, 0.0, 0.0, 1.0);
    half4 c = content.eval((q + 1.0) * 0.5 * size);
    float f = uv.x * uv.x * uv.y * uv.y;
    float2 qr = clamp(uv * (1.0 + k * 1.35 * w), -1.0, 1.0);
    float2 qb = uv * (1.0 + k * 0.7 * w);
    c.r = mix(c.r, content.eval((qr + 1.0) * 0.5 * size).r, f);
    c.b = mix(c.b, content.eval((qb + 1.0) * 0.5 * size).b, f);
    float2 e = 1.0 - abs(q);
    float v = smoothstep(0.0, 0.03 + k * 0.3, min(e.x, e.y)) * (1.0 - 0.35 * f);
    return half4(c.rgb * v, c.a);
}
"""

private fun Modifier.vhsCurve(amount: Float): Modifier =
    if (amount <= 0.005f || android.os.Build.VERSION.SDK_INT < 33) this else composed {
        val cache = remember { arrayOfNulls<Any>(1) }
        var key by remember { mutableStateOf("") }
        graphicsLayer {
            val k = (amount * 100f).roundToInt() / 100f * 0.16f
            val kk = "${size.width}x${size.height}:$k"
            if (kk != key) { cache[0] = runCatching { curveEffect(size.width, size.height, k) }.onFailure { android.util.Log.w("r95", "curve shader", it) }.getOrNull(); key = kk }
            renderEffect = cache[0] as androidx.compose.ui.graphics.RenderEffect?
        }
    }

@androidx.annotation.RequiresApi(33)
private fun curveEffect(w: Float, h: Float, k: Float): androidx.compose.ui.graphics.RenderEffect {
    val sh = android.graphics.RuntimeShader(CURVE_SRC)
    sh.setFloatUniform("size", w, h); sh.setFloatUniform("k", k)
    return android.graphics.RenderEffect.createRuntimeShaderEffect(sh, "content").asComposeRenderEffect()
}

/** CURVE before Android 13: rounded black tube corners and a soft shade along the edges */
@Composable
private fun VhsCurveCorners(amount: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val r = size.minDimension * (0.02f + 0.13f * amount)
        val tube = androidx.compose.ui.graphics.Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(androidx.compose.ui.geometry.Rect(Offset.Zero, size), androidx.compose.ui.geometry.CornerRadius(r, r))) }
        clipPath(tube, androidx.compose.ui.graphics.ClipOp.Difference) { drawRect(Color.Black) }
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.15f + 0.45f * amount)), Offset(size.width / 2, size.height / 2), size.maxDimension * 0.75f))
    }
}

/** One glitch "frame" of the tape: shared by the CRT overlay and the picture's horizontal tearing. */
private class Glitch(val burst: Boolean, val tearX: Float, val jitterY: Float, val kinds: Int, val amp: Float)
private val NO_GLITCH = Glitch(false, 0f, 0f, 0, 0f)
private const val G_TEAR = 1; private const val G_BAND = 2; private const val G_CHROMA = 4; private const val G_ROLL = 8; private const val G_WASH = 16

/**
 * When the tape glitches: random, not on a beat. Gaps are random (exponential, shorter on a worn tape),
 * every burst has its own length, strength and mix of effects (tearing, noise band, colour slip, a picture
 * jump, wash-out), bursts sometimes come in pairs, and tiny one-frame tears happen in between.
 */
private class GlitchSched {
    private val r = java.util.Random()
    var start = 0L; var end = 0L; private var next = 0L
    var kinds = 0; var amp = 0f; var seed = 0L
    var microUntil = 0L; var microX = 0f
    private fun gap(g: Float): Long {
        val mean = 9000.0 - 7200.0 * g // mean gap: 9 s … 1.8 s
        val u = r.nextDouble().coerceAtLeast(1e-4)
        return (-kotlin.math.ln(u) * mean).coerceIn(250.0, mean * 4).toLong()
    }
    fun update(now: Long, g: Float) {
        if (g <= 0f) { end = 0; next = 0; microUntil = 0; return }
        if (next == 0L) next = now + gap(g)
        if (now >= next && now >= end) {
            start = now
            end = now + (90 + r.nextFloat() * r.nextFloat() * (500 + 1300 * g)).toLong()
            kinds = 1 + r.nextInt(31)
            if (r.nextFloat() < 0.5f) kinds = kinds or G_BAND
            amp = (0.25f + r.nextFloat() * 0.75f) * (0.5f + 0.5f * g)
            seed = r.nextLong()
            next = if (r.nextFloat() < 0.3f * g) end + 60 + r.nextInt(450) else end + gap(g)
        }
        if (now >= microUntil && r.nextFloat() < 0.012f + 0.06f * g) {
            microUntil = now + 40 + r.nextInt(110)
            microX = (r.nextFloat() - 0.5f) * 2f
        }
    }
    fun at(now: Long, px: Float, track: Float): Glitch {
        val inBurst = now in start until end
        val micro = now < microUntil
        // a mistracked picture wobbles a little all the time
        val trackX = if (track > 0.05f) (java.util.Random(now / 66 + 7).nextFloat() - 0.5f) * px * 2.5f * track else 0f
        if (!inBurst && !micro) return if (trackX == 0f) NO_GLITCH else Glitch(false, trackX, 0f, 0, 0f)
        val rnd = java.util.Random(seed + now / 66)
        var tear = trackX + if (micro) microX * px * 1.5f else 0f
        var jit = 0f
        if (inBurst && kinds and G_TEAR != 0) tear += (rnd.nextFloat() - 0.5f) * 2f * px * (0.8f + 6f * amp)
        if (inBurst && kinds and G_ROLL != 0 && rnd.nextInt(3) == 0) jit = (rnd.nextFloat() - 0.5f) * px * 7f * amp
        return Glitch(inBurst, tear, jit, if (inBurst) kinds else 0, amp)
    }
}

/** Scanlines, vignette, a rolling bright band, and the tape glitches: tracking noise, static, colour fringe, head-switch noise. */
@Composable
private fun VhsCrt(clock: androidx.compose.runtime.LongState, g: Float, sched: GlitchSched, track: Float, sn: Float = OsPrefs.vhsSnow, ln: Float = OsPrefs.vhsLines, sc: Float = OsPrefs.vhsScan) {
    val scan = remember {
        val b = android.graphics.Bitmap.createBitmap(1, 3, android.graphics.Bitmap.Config.ARGB_8888)
        b.setPixel(0, 2, 0x55000000); b.setPixel(0, 1, 0x14000000)
        androidx.compose.ui.graphics.ShaderBrush(android.graphics.BitmapShader(b, android.graphics.Shader.TileMode.REPEAT, android.graphics.Shader.TileMode.REPEAT))
    }
    val px = LocalOsd.current.px
    // analog snow: a small noise tile, moved to a random place every frame
    val snowShader = remember {
        val r = java.util.Random(7)
        val b = android.graphics.Bitmap.createBitmap(96, 96, android.graphics.Bitmap.Config.ARGB_8888)
        for (y in 0 until 96) for (x in 0 until 96) { val v = r.nextInt(256); b.setPixel(x, y, android.graphics.Color.argb((v * v) / 255 / 3, 255, 255, 255)) }
        android.graphics.BitmapShader(b, android.graphics.Shader.TileMode.REPEAT, android.graphics.Shader.TileMode.REPEAT)
    }
    val snow = remember { androidx.compose.ui.graphics.ShaderBrush(snowShader) }
    val snowM = remember { android.graphics.Matrix() }
    Canvas(Modifier.fillMaxSize()) {
        val now = clock.longValue
        val fr = java.util.Random(now / 66 * 31)
        // interlace twitter: the scanlines sit one line lower on every other field
        // SCANLINES (with interlace twitter)
        if (sc > 0f) { val a = (sc * 1.4f).coerceAtMost(1f); if ((now / 66) % 2L == 0L) drawRect(scan, alpha = a) else translate(top = 1f) { drawRect(scan, alpha = a) } }
        // SNOW: analog snow, drop-outs and specks
        if (sn > 0f) {
            snowM.setTranslate(fr.nextFloat() * 96f, fr.nextFloat() * 96f); snowShader.setLocalMatrix(snowM)
            drawRect(snow, alpha = 0.44f * sn + 0.2f * sn * sn, blendMode = BlendMode.Plus)
            // AGC breathing: the picture brightness pumps very slightly
            drawRect(Color.Black.copy(alpha = 0.012f + 0.06f * sn * fr.nextFloat()))
            // tape drop-outs: a short white streak where the oxide is worn
            if (fr.nextFloat() < 0.36f * sn) repeat(1 + fr.nextInt(2)) {
                val y = fr.nextFloat() * size.height
                val x = fr.nextFloat() * size.width
                val w = px * (4f + fr.nextFloat() * 30f)
                drawRect(Color.White.copy(alpha = 0.5f + fr.nextFloat() * 0.4f), Offset(x, y), Size(w, 1f + fr.nextFloat() * px * 0.6f))
                drawRect(Color.White.copy(alpha = 0.18f), Offset(x + w, y), Size(w * 0.6f, 1f))
            }
        }
        val rnd = java.util.Random(now / 66)
        // static: loose specks (SNOW)
        if (sn > 0f) repeat((sn * sn * 140).toInt()) {
            drawRect(Color.White.copy(alpha = 0.15f + rnd.nextFloat() * 0.45f), Offset(rnd.nextFloat() * size.width, rnd.nextFloat() * size.height), Size(1f + rnd.nextFloat() * px, 1f + rnd.nextFloat() * px * 0.5f))
        }
        if (ln > 0f) {
            // the rolling bright band (LINES)
            val roll = ((now % 7000) / 7000f) * (size.height * 1.3f) - size.height * 0.15f
            val bandA = 0.16f * ln
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.White.copy(alpha = bandA), Color.Transparent), roll - 60f, roll + 60f), Offset(0f, roll - 60f), Size(size.width, 120f))
            // head-switching noise: the ragged strip at the very bottom of a VHS picture
            val g = ln
            val hs = px * (1f + 3f * g)
            val y0 = size.height - hs
            var x = 0f
            while (x < size.width) {
                val w = px * (2f + rnd.nextFloat() * 14f)
                drawRect(Color.White.copy(alpha = (0.06f + rnd.nextFloat() * 0.3f) * (0.4f + g)), Offset(x, y0 + rnd.nextFloat() * hs * 0.6f), Size(w, 1f + rnd.nextFloat() * hs * 0.5f))
                x += w + rnd.nextFloat() * px * 6f
            }
        }
        if (g > 0f) {
            val gl = sched.at(now, px, track)
            if (gl.burst) {
                val br = java.util.Random(sched.seed + now / 66)
                val a = gl.amp
                if (gl.kinds and G_BAND != 0) {
                    // tracking noise: a band of white dashes, anywhere on the picture, drifting during the burst
                    val bandH = 30f + 300f * a
                    val y0b = java.util.Random(sched.seed).nextFloat() * (size.height - bandH) + (now - sched.start) * 0.08f * (if (sched.seed and 1L == 0L) 1 else -1)
                    repeat((16 + 90 * a).toInt()) {
                        val y = y0b + br.nextFloat() * bandH
                        drawRect(Color.White.copy(alpha = 0.2f + br.nextFloat() * 0.6f), Offset(br.nextFloat() * size.width, y), Size(16f + br.nextFloat() * 200f, 1.5f + br.nextFloat() * 1.5f))
                    }
                }
                if (gl.kinds and G_CHROMA != 0) {
                    // colour slip: thin red / cyan lines
                    repeat((2 + 10 * a).toInt()) {
                        val y = br.nextFloat() * size.height
                        val c = if (br.nextBoolean()) Color(0xFFFF2050) else Color(0xFF20E0FF)
                        drawRect(c.copy(alpha = 0.10f + 0.25f * a), Offset(0f, y), Size(size.width, 1f + br.nextFloat() * px * 1.5f))
                    }
                }
                if (gl.kinds and G_WASH != 0 && br.nextInt(3) == 0) drawRect(Color.White.copy(alpha = 0.03f + 0.07f * a))
            }
        }
        // TRACKING off-centre: permanent noise bars rolling slowly through the picture
        if (track > 0.04f) {
            val tr = java.util.Random(now / 66 + 99)
            val bars = 1 + (track * 2.2f).toInt()
            for (k in 0 until bars) {
                val bandH = px * (3f + 14f * track)
                val y0t = ((now / 9f + k * size.height / bars) % (size.height + bandH)) - bandH
                repeat((10 + 70 * track).toInt()) {
                    drawRect(Color.White.copy(alpha = 0.15f + tr.nextFloat() * 0.55f * track + 0.1f), Offset(tr.nextFloat() * size.width, y0t + tr.nextFloat() * bandH), Size(10f + tr.nextFloat() * 140f, 1.5f + tr.nextFloat() * 2f))
                }
            }
        }
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.55f)), center = center, radius = size.maxDimension * 0.75f))
    }
}

// ---------------------------------------------------------------- shell

private enum class Screen { MAIN, SETUP, CHOOSE, VIDEO, PLAYLIST, EQ, CHAT, WEB }

@Composable
fun VhsShell(onShutDown: () -> Unit) {
    val ctx = LocalContext.current
    val state = LocalAppState.current
    var screen by remember { mutableStateOf(Screen.MAIN) }
    // switching menus: a short "channel change" (0 = just switched … 1 = settled)
    // (stepped by the clock, not by screen frames: it always runs out, even if the launcher was away)
    val switchP = remember { androidx.compose.runtime.mutableFloatStateOf(1f) }
    var lastScreen by remember { mutableStateOf(screen) }
    LaunchedEffect(screen) {
        if (screen == lastScreen) return@LaunchedEffect
        lastScreen = screen
        val t0 = android.os.SystemClock.uptimeMillis()
        try {
            while (true) {
                val e = (android.os.SystemClock.uptimeMillis() - t0).toFloat() / VhsTestHooks.switchMs
                if (e >= 1f || !com.reverender.r95.core.AppVisibility.visible) break
                switchP.floatValue = e
                delay(16)
            }
        } finally { switchP.floatValue = 1f }
    }
    var chooseDir by remember { mutableStateOf("up") }
    val bg = Color(OsPrefs.vhsBg.toInt()); val fg = Color(OsPrefs.vhsFg.toInt())
    LaunchedEffect(Unit) { Launcher.load(ctx) }
    // apps installed or uninstalled (e.g. from the drawer's hold menu): the list follows
    androidx.compose.runtime.DisposableEffect(Unit) {
        val rx = object : android.content.BroadcastReceiver() {
            override fun onReceive(c: android.content.Context, i: android.content.Intent) { Launcher.stale(); Launcher.load(ctx) }
        }
        val f = android.content.IntentFilter().apply {
            addAction(android.content.Intent.ACTION_PACKAGE_ADDED); addAction(android.content.Intent.ACTION_PACKAGE_REMOVED); addAction(android.content.Intent.ACTION_PACKAGE_CHANGED); addDataScheme("package")
        }
        androidx.core.content.ContextCompat.registerReceiver(ctx, rx, f, androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED)
        onDispose { runCatching { ctx.unregisterReceiver(rx) } }
    }
    LaunchedEffect(Unit) { VhsSound.load(ctx) } // the EQ page's sound settings
    remember { runCatching { com.reverender.r95.ui.viz.site.SiteHost.ensureInit() } } // the page data VAPOR_AMP and the tape deck use
    // VAPOR_AMP's clock (the desktop's player window normally runs it)
    LaunchedEffect(Unit) { while (true) { com.reverender.r95.core.AppVisibility.awaitForeground(); runCatching { AmpEngine.tick() }; delay(250) } }
    // back on the main screen does nothing (as a home screen it would only restart the launcher); an open
    // phone keyboard still closes itself first. Other screens go back to the menu.
    // (switching menus no longer brings the auto-hidden help back; swipe up from the bottom edge does)
    BackHandler(enabled = screen != Screen.MAIN && screen != Screen.CHAT) { screen = if (screen == Screen.CHOOSE) Screen.SETUP else Screen.MAIN }

    // ---- full screen: the status bar is hidden while the VHS menu shows (a swipe from the top edge peeks it)
    val view = androidx.compose.ui.platform.LocalView.current
    val visible = com.reverender.r95.core.AppVisibility.visible
    androidx.compose.runtime.DisposableEffect(visible) {
        var c: android.content.Context? = view.context
        while (c is android.content.ContextWrapper && c !is android.app.Activity) c = c.baseContext
        val ctl = (c as? android.app.Activity)?.window?.let { androidx.core.view.WindowCompat.getInsetsController(it, view) }
        ctl?.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        if (visible) ctl?.hide(androidx.core.view.WindowInsetsCompat.Type.statusBars())
        onDispose { ctl?.show(androidx.core.view.WindowInsetsCompat.Type.statusBars()) }
    }

    // ---- the phone keyboard (opened by tapping a search line), typing into whichever search is on screen
    val kbHolder = remember { mutableStateOf<VhsKb?>(null) }
    val kbFocus = remember { androidx.compose.ui.focus.FocusRequester() }
    val kbCtl = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val focusMgr = androidx.compose.ui.platform.LocalFocusManager.current
    // an app was started (from the search, the drawer, a swipe): put the keyboard away, so it isn't still up
    // when you come back
    LaunchedEffect(Unit) {
        androidx.compose.runtime.snapshotFlow { com.reverender.r95.core.AppVisibility.visible }.collect { v -> if (!v) { kbCtl?.hide(); focusMgr.clearFocus() } }
    }
    var kbText by remember { mutableStateOf(androidx.compose.ui.text.input.TextFieldValue("")) }
    fun openKb() {
        val t = kbHolder.value?.get().orEmpty()
        kbText = androidx.compose.ui.text.input.TextFieldValue(t, androidx.compose.ui.text.TextRange(t.length))
        runCatching { kbFocus.requestFocus() }; kbCtl?.show(); Haptics.tick()
    }
    LaunchedEffect(Unit) {
        androidx.compose.runtime.snapshotFlow { kbHolder.value?.get() }.collect { t ->
            if (t != null && t != kbText.text) kbText = androidx.compose.ui.text.input.TextFieldValue(t, androidx.compose.ui.text.TextRange(t.length))
        }
    }
    // the search went away (screen changed): put the keyboard away too
    LaunchedEffect(kbHolder.value) { if (kbHolder.value == null) { kbCtl?.hide(); focusMgr.clearFocus() } }
    @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
    val imeNow by androidx.compose.runtime.rememberUpdatedState(WindowInsets.isImeVisible)
    // the help bars at the bottom stay put; with SETUP's HELP AUTO they slide away after 2 s, and a new screen or a
    // swipe up from the bottom edge brings them back
    LaunchedEffect(OsPrefs.vhsHelpAuto) {
        if (!OsPrefs.vhsHelpAuto) { VhsHint.shown = true; return@LaunchedEffect }
        androidx.compose.runtime.snapshotFlow { VhsHint.pokes }.collectLatest { VhsHint.shown = true; delay(2000); VhsHint.shown = false }
    }

    // the CRT / glitch clock (~15 fps is plenty for slow CRT drift)
    // (read only while drawing, so its ticks redraw the CRT layer without recomposing the menu)
    val clock = remember { mutableLongStateOf(System.currentTimeMillis()) }
    val sched = remember { GlitchSched() }
    // the video deck stays loaded while you go home and back; released with the shell
    val deck = remember { VideoDeck() }
    androidx.compose.runtime.DisposableEffect(Unit) { onDispose { deck.release() } }
    val crt = (OsPrefs.vhsGlitch > 0f || OsPrefs.vhsSnow > 0f || OsPrefs.vhsLines > 0f || OsPrefs.vhsScan > 0f) && !VhsCrtGate.off
    LaunchedEffect(crt) {
        if (!crt) return@LaunchedEffect
        while (true) {
            com.reverender.r95.core.AppVisibility.awaitForeground()
            val t = System.currentTimeMillis()
            sched.update(t, OsPrefs.vhsGlitch)
            clock.longValue = t; delay(66)
        }
    }
    // the bloom's breathing (runs with or without the CRT)
    LaunchedEffect(OsPrefs.vhsBloom) {
        if (!OsPrefs.vhsBloom) return@LaunchedEffect
        while (true) {
            com.reverender.r95.core.AppVisibility.awaitForeground()
            val t = System.currentTimeMillis()
            val f = AudioBus.currentFrame()
            VhsBloomPulse.update(t, ((f.rmsL + f.rmsR) * 1.6f).coerceIn(0f, 1f), crt && sched.at(t, 1f, 0f).burst)
            delay(66)
        }
    }
    // the apps drawer: fades in on a swipe up (0 = closed … 1 = open)
    var drawerP by remember { mutableFloatStateOf(0f) }
    val drawerScope = androidx.compose.runtime.rememberCoroutineScope()
    fun settleDrawer(open: Boolean, velocity: Float = 0f) {
        drawerScope.launch {
            androidx.compose.animation.core.animate(drawerP, if (open) 1f else 0f, 0f, androidx.compose.animation.core.tween(if (open) 220 else 260)) { v, _ -> drawerP = v }
        }
    }
    // only these yes/no facts reach the shell, so sliding the drawer doesn't rebuild the whole screen every frame
    val drawerShown by remember { androidx.compose.runtime.derivedStateOf { drawerP > 0.001f } }
    val drawerFull by remember { androidx.compose.runtime.derivedStateOf { drawerP > 0.995f } }
    LaunchedEffect(Unit) { androidx.compose.runtime.snapshotFlow { drawerFull }.collect { VhsUi.drawerOpen = it } }
    androidx.compose.runtime.DisposableEffect(Unit) { onDispose { VhsUi.drawerOpen = false } }
    val drawerOpenish by remember { androidx.compose.runtime.derivedStateOf { drawerP > 0.01f } }
    var drawerReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1500); drawerReady = true } // build the drawer early, off the swipe
    BackHandler(enabled = drawerOpenish) { settleDrawer(false) }
    // BACK on the main screen opens the apps drawer (BACK again closes it); the swipe still works too
    BackHandler(enabled = screen == Screen.MAIN && !drawerOpenish) { Haptics.tick(); settleDrawer(true) }
    val g = if (crt) OsPrefs.vhsGlitch else 0f
    // TRACKING: distance from the centre detent (0 … 1)
    val track = 0f // the TRACKING knob is gone (the EQ took its place)
    androidx.compose.runtime.SideEffect { com.reverender.r95.audio.AudioBus.fx.vhsTrack = track; com.reverender.r95.audio.AudioBus.fx.touch() }
    androidx.compose.runtime.DisposableEffect(Unit) { onDispose { com.reverender.r95.audio.AudioBus.fx.vhsTrack = 0f; com.reverender.r95.audio.AudioBus.fx.touch() } }
    // background and CRT cover the whole screen (status bar / cutout area too); the menu sits in the safe area
    val pic = OsPrefs.vhsPic
    val picBmp = remember(pic) { if (pic > 0) VhsBgPicture.load(ctx) else null }
    val grad = vhsGradBrush()
    val curve = if (screen == Screen.VIDEO && deck.fullscreen) 0f else OsPrefs.vhsCurve
    Box(Modifier.fillMaxSize().vhsCurve(curve)) {
    Box(Modifier.fillMaxSize().then(if (grad != null) Modifier.background(grad) else Modifier.background(bg))) {
      val wall = OsPrefs.vhsWall.takeIf { it < VhsWalls.names.size } ?: -1
      val tint = if (grad != null) Modifier.background(vhsGradBrush(OsPrefs.vhsPicDim)!!) else Modifier.background(bg.copy(alpha = OsPrefs.vhsPicDim))
      if (wall >= 0) {
          VhsWallpaper(wall, Modifier.fillMaxSize())
          Box(Modifier.fillMaxSize().then(tint))
      } else if (picBmp != null) {
          // own picture: under the menu and the CRT, tinted with the background colour so the text stays readable
          androidx.compose.foundation.Image(picBmp, null, Modifier.fillMaxSize(), contentScale = androidx.compose.ui.layout.ContentScale.Crop)
          Box(Modifier.fillMaxSize().then(tint))
      }
      // the phone keyboard slides over the bottom instead of shrinking the menu
      BoxWithConstraints(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.displayCutout.only(androidx.compose.foundation.layout.WindowInsetsSides.Horizontal))).padding(top = 4.dp)) {
        val d = LocalDensity.current
        // wide screens (landscape, an unfolded phone, tablets): the home menu with a second menu next to it
        val wide = maxWidth > maxHeight * 0.95f && OsPrefs.vhsWide != "OFF"
        val paneW = if (wide) (maxWidth - 1.dp) / 2 else maxWidth
        // 22 glyph columns across each pane (wider panes get bigger dots, capped) …
        val pxW = with(d) { (paneW.toPx() / (22 * 6 + 4)).coerceIn(1.0f, 9f * density) }
        val availH = with(d) { maxHeight.toPx() }
        // … shrunk when the page would be taller than the screen, so nothing is cut off; worked out again for
        // every new screen size (rotating, folding, split screen)
        val fits = remember(maxWidth, maxHeight, wide) { HashMap<String, FitState>() }
        val fit = fits.getOrPut((if (wide) "L" else "") + screen.name) { FitState() }
        val px = pxW * fit.fit.floatValue
        val sideDefault = when (OsPrefs.vhsWide) { "EQ" -> Screen.EQ; "PLAYLIST" -> Screen.PLAYLIST; "SETUP" -> Screen.SETUP; else -> Screen.VIDEO }
        val widthPx = with(d) { maxWidth.toPx() }
        CompositionLocalProvider(LocalOsd provides Osd(fg, bg, px), LocalKbHolder provides kbHolder, LocalOpenKb provides { if (kbHolder.value != null) openKb() }) {
            Box(
                Modifier.fillMaxSize()
                    .uiMotionBlur(if (deck.fullscreen && screen == Screen.VIDEO) 0f else OsPrefs.vhsUiBlur)
                    .graphicsLayer {
                        // tape tearing: the picture slips sideways during a tracking burst
                        // (VHS WEB: the web page is a real Android view, which can't follow a moving layer: the
                        // picture stays put there and only the noise / fade play over it)
                        val still = screen == Screen.WEB
                        val gl = if (crt && !still) sched.at(clock.longValue, px, track) else NO_GLITCH
                        translationX = gl.tearX; translationY = gl.jitterY
                        // menu switch: the new picture rolls down into place (vertical hold), slips sideways a
                        // little, and comes up out of the dark, like a VCR finding the signal
                        val sp = switchP.floatValue
                        if (sp < 1f && still) alpha = (sp * 2.2f - 0.15f).coerceIn(0f, 1f)
                        else if (sp < 1f) {
                            val e = 1f - sp
                            translationY += -size.height * 0.10f * e * e * e + kotlin.math.sin(sp * 30f) * px * 1.5f * e
                            translationX += (java.util.Random((sp * 60).toLong()).nextFloat() - 0.5f) * px * 5f * e * e
                            alpha = (sp * 2.2f - 0.15f).coerceIn(0f, 1f)
                            scaleY = 1f + 0.04f * e * e
                        }
                    }
                    .pointerInput(screen, widthPx) {
                        val min = 60.dp.toPx()
                        val slop = viewConfiguration.touchSlop
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            var end = down.position
                            var used = false
                            val vt = androidx.compose.ui.input.pointer.util.VelocityTracker()
                            while (true) {
                                val ev = awaitPointerEvent()
                                val c = ev.changes.firstOrNull { it.id == down.id } ?: break
                                // a slider / scroll taking the drag counts; a tap detector taking the lift-off doesn't (a
                                // full-width box such as the spectrum takes a swipe's release as a "tap")
                                if (c.pressed && c.isConsumed) used = true
                                end = c.position
                                vt.addPosition(c.uptimeMillis, c.position)
                                val ddx = end.x - down.position.x; val ddy = end.y - down.position.y
                                if (!c.pressed) break
                            }
                            if (used) { android.util.Log.i("r95", "swipe: used by a control"); return@awaitEachGesture }
                            val dx = end.x - down.position.x; val dy = end.y - down.position.y
                            val dir = when {
                                kotlin.math.abs(dy) > min && kotlin.math.abs(dy) > kotlin.math.abs(dx) * 1.4f -> if (dy < 0) "up" else "down"
                                kotlin.math.abs(dx) > min && kotlin.math.abs(dx) > kotlin.math.abs(dy) * 1.4f -> if (dx > 0) "right" else "left"
                                else -> null
                            } ?: return@awaitEachGesture
                            // swipe up: the apps drawer (with the help bar on auto-hide, from the bottom edge it brings the help back)
                            if (dir == "up") {
                                if (OsPrefs.vhsHelpAuto && !VhsHint.shown && down.position.y > size.height * 0.8f) { VhsHint.poke(); Haptics.tick() }
                                else if (screen == Screen.MAIN) { Haptics.tick(); settleDrawer(true) }
                                return@awaitEachGesture
                            }
                            // swipe down: the phone's notification shade, on every VHS screen
                            if (dir == "down") { Haptics.tick(); Launcher.openNotifications(ctx); return@awaitEachGesture }
                            if (screen != Screen.MAIN) return@awaitEachGesture
                            val target = OsPrefs.vhsSwipe[dir].orEmpty()
                            android.util.Log.i("r95", "swipe: $dir -> $target")
                            when {
                                target == "vhs:setup" -> screen = Screen.SETUP
                                target == "vhs:chat" && com.reverender.r95.BuildConfig.NOTIF -> { Haptics.tick(); screen = Screen.CHAT }
                                target == "vhs:eq" -> { Haptics.tick(); screen = Screen.EQ }
                                target == "vhs:playlist" -> { Haptics.tick(); screen = Screen.PLAYLIST }
                                target == "vhs:load" -> { Haptics.tick(); deck.searching = true; deck.subsOpen = false; screen = Screen.VIDEO }
                                target == "vhs:web" -> { Haptics.tick(); screen = Screen.WEB }
                                target.startsWith("pkg:") -> { Haptics.tick(); Launcher.openTarget(ctx, target, fullScreen = true) { } }
                            }
                        }
                    },
            ) {
                @Composable
                fun Page(sc: Screen, pane: String) {
                    val f = fits.getOrPut(pane + sc.name) { FitState() }
                    CompositionLocalProvider(LocalOsd provides Osd(fg, bg, pxW * f.fit.floatValue)) {
                        when (sc) {
                            Screen.MAIN -> VhsMain(f, availH, onMenu = { screen = Screen.SETUP }, onLoad = { deck.searching = true; deck.subsOpen = false; screen = Screen.VIDEO }, onPlaylist = { screen = Screen.PLAYLIST }, onEq = { screen = Screen.EQ })
                            Screen.PLAYLIST -> VhsPlaylist(onExit = { screen = Screen.MAIN })
                            Screen.EQ -> VhsEq(f, availH, onExit = { screen = Screen.MAIN })
                            // LOAD: the video, game and music decks, switched at the top
                            Screen.VIDEO -> when (deck.mode) {
                                "game" -> VhsGame(onExit = { screen = Screen.MAIN }, onSwitch = { deck.mode = it })
                                "music" -> VhsMusic(onExit = { screen = Screen.MAIN }, onSwitch = { deck.mode = it }, onPlayed = { screen = Screen.MAIN })
                                else -> VhsVideo(deck, onExit = { screen = Screen.MAIN }, onSwitch = { deck.mode = it })
                            }
                            Screen.SETUP -> VhsSetup(f, availH, onEnd = { screen = Screen.MAIN }, onChoose = { chooseDir = it; screen = Screen.CHOOSE })
                            Screen.CHOOSE -> VhsChoose(chooseDir) { screen = Screen.SETUP }
                            Screen.CHAT -> VhsChat(onExit = { screen = Screen.MAIN })
                            Screen.WEB -> VhsWeb(onExit = { screen = Screen.MAIN })
                        }
                    }
                }
                if (wide) Row(Modifier.fillMaxSize()) {
                    // left: always the home menu; right: whatever you open (or the chosen side menu)
                    Box(Modifier.weight(1f).fillMaxHeight()) { Page(Screen.MAIN, "L") }
                    Box(Modifier.width(1.dp).fillMaxHeight().background(fg.copy(alpha = 0.45f)))
                    Box(Modifier.weight(1f).fillMaxHeight()) {
                        if (screen == Screen.MAIN && OsPrefs.vhsWide == "APPS") CompositionLocalProvider(LocalOsd provides Osd(fg, bg, pxW)) { VhsAppsPane() }
                        else Page(if (screen == Screen.MAIN) sideDefault else screen, "R")
                    }
                } else {
                    // the home menu stays alive under the other menus (hidden, parked off screen, its animations
                    // resting), so coming back is instant: no rebuilding, no re-fitting, no news reload
                    val hidden = screen != Screen.MAIN
                    androidx.compose.runtime.SideEffect { VhsUi.mainHidden = hidden }
                    Box(Modifier.fillMaxSize()
                        .layout { m, c -> val pl = m.measure(c); layout(pl.width, pl.height) { pl.place(if (hidden) pl.width * 4 else 0, 0) } }
                        .graphicsLayer { alpha = if (hidden) 0f else 1f }) {
                        CompositionLocalProvider(LocalPageHidden provides hidden) { Page(Screen.MAIN, "") }
                    }
                    if (hidden) Page(screen, "")
                }
            }
            if (drawerShown || drawerReady) VhsAppsDrawer({ drawerP }, widthPx, drawerShown, onClose = { settleDrawer(false) })
            // typing in a menu (SETUP, the decks' searches): what you type shows just above the keyboard
            VhsKbPreview(kbHolder.value)
        }
      }
      if (switchP.floatValue < 1f) VhsSwitchFx({ switchP.floatValue })
      // Play edition: YouTube's own player, over everything but the CRT glass
      if (com.reverender.r95.BuildConfig.PLAY) CompositionLocalProvider(LocalOsd provides Osd(fg, bg, with(LocalDensity.current) { 2.5.dp.toPx() })) { VhsYtEmbedHost() }
      // the help tour: on first boot, and from SETUP > TOUR
      CompositionLocalProvider(LocalOsd provides Osd(fg, bg, with(LocalDensity.current) { 2.5.dp.toPx() })) { VhsTourHost(); if (com.reverender.r95.BuildConfig.PLAY) VhsNotifDisclosureHost() }
      if (crt) CompositionLocalProvider(LocalOsd provides Osd(fg, bg, with(LocalDensity.current) { 2.5.dp.toPx() })) { VhsCrt(clock, g, sched, track) }
      // full-screen video sits above everything: edge to edge, no CRT
      if (screen == Screen.VIDEO && deck.mode == "video" && deck.fullscreen && deck.loaded)
          CompositionLocalProvider(LocalOsd provides Osd(Color.White, Color.Black, with(LocalDensity.current) { 2.5.dp.toPx() })) { VhsFullscreenVideo(deck) }
      // the tube warming up when you come back from the lock screen. Timed by the clock: whatever happens to
      // the frames, after half a second it is over.
      var powerStart by remember { mutableStateOf(0L) }
      var powerNow by remember { mutableStateOf(0L) }
      var powerHold by remember { mutableStateOf(false) }
      LaunchedEffect(Unit) {
          androidx.compose.runtime.snapshotFlow { VhsPowerOn.pending }.collect { pend ->
              if (!pend) return@collect
              // 1) wait until the screen is really off (a stray signal with the screen on is dropped)
              val pm = ctx.getSystemService(android.content.Context.POWER_SERVICE) as android.os.PowerManager
              val km = ctx.getSystemService(android.content.Context.KEYGUARD_SERVICE) as android.app.KeyguardManager
              val offBy = android.os.SystemClock.uptimeMillis() + 3000
              while (pm.isInteractive && android.os.SystemClock.uptimeMillis() < offBy) delay(50)
              if (pm.isInteractive) { VhsPowerOn.pending = false; return@collect }
              powerHold = true
              // 2) play when the picture can actually be seen again: screen on, lock screen gone, the launcher's
              //    window in front with focus. Checked directly each time (no one-shot unlock signal to miss), so
              //    quick relocks, "lock after N seconds" and Smart Lock all end up here
              // (some phones hand the window its focus late, or not at all, after unlocking: once the screen is
              //  on, unlocked and the launcher is showing, it plays after 0.6 s at the latest)
              var readySince = 0L
              while (true) {
                  val on = pm.isInteractive
                  val unlocked = !km.isKeyguardLocked
                  val showing = com.reverender.r95.core.AppVisibility.visible
                  val now = android.os.SystemClock.uptimeMillis()
                  if (on && unlocked && showing) { if (readySince == 0L) readySince = now } else readySince = 0L
                  if (on && unlocked && (VhsPowerOn.focused || readySince != 0L && now - readySince > 600)) break
                  delay(if (on) 30 else 400)
              }
              android.util.Log.i("r95", "power: on")
              powerHold = false
              VhsPowerOn.pending = false
              val t0 = android.os.SystemClock.uptimeMillis()
              powerStart = t0; powerNow = t0
              // stepped by the clock (not by screen frames), so it always runs out
              while (android.os.SystemClock.uptimeMillis() - t0 < 520) { delay(16); powerNow = android.os.SystemClock.uptimeMillis() }
              powerStart = 0L
          }
      }
      val pw = when {
          powerHold -> 0f
          powerStart == 0L -> 1f
          else -> ((powerNow - powerStart) / 480f).coerceIn(0f, 1f)
      }
      if (pw < 1f) VhsPowerOnOverlay(pw)
      // test hook (am start --es r95_vhs_rom name): open the game deck with a ROM from the app's files folder
      LaunchedEffect(VhsTestHooks.rom) { if (VhsTestHooks.rom != null) { deck.mode = "game"; screen = Screen.VIDEO } }
      // invisible field the phone keyboard types into
      androidx.compose.foundation.text.BasicTextField(
          value = kbText,
          onValueChange = { v ->
              // a chat message keeps what you typed (lower case, emoji); searches only take what the font shows
              val raw = kbHolder.value?.raw == true
              val t = if (raw) v.text else vhsSafe(v.text)
              kbText = if (raw) v else androidx.compose.ui.text.input.TextFieldValue(t, androidx.compose.ui.text.TextRange(t.length))
              kbHolder.value?.set(t)
          },
          modifier = Modifier.size(1.dp).graphicsLayer { alpha = 0f }.focusRequester(kbFocus),
          singleLine = true,
          keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
              capitalization = if (kbHolder.value?.raw == true) androidx.compose.ui.text.input.KeyboardCapitalization.Sentences else androidx.compose.ui.text.input.KeyboardCapitalization.Characters,
              imeAction = if (kbHolder.value?.raw == true) androidx.compose.ui.text.input.ImeAction.Send else androidx.compose.ui.text.input.ImeAction.Go,
          ),
          keyboardActions = androidx.compose.foundation.text.KeyboardActions(onGo = {
              kbHolder.value?.enter(); kbCtl?.hide(); focusMgr.clearFocus()
          }, onSend = {
              // chat: send and keep the keyboard up for the next message
              kbHolder.value?.enter()
          }),
      )
    }
    // phones before Android 13 can't bend the picture: dark, rounded tube corners instead
    if (curve > 0.005f && android.os.Build.VERSION.SDK_INT < 33) VhsCurveCorners(curve)
    }
}

/**
 * A page that fits the screen: [body] from the top, [hint] pinned to the bottom. Both are measured at their
 * natural height; when together they are taller than [availH], [fit] shrinks the dot size until they fit.
 */
/** Dot size of a page ([fit]) and the spare height handed to its stretchy area ([extra], in px). */
internal class FitState {
    val fit = mutableFloatStateOf(1f)
    val extra = mutableFloatStateOf(0f)
    var settledFor = -1f
}

@Composable
private fun FitPage(fit: FitState, availH: Float, gap: Float, body: @Composable ColumnScope.() -> Unit, hint: @Composable () -> Unit, spread: Boolean = false) {
    var bodyH by remember { mutableIntStateOf(0) }
    var hintH by remember { mutableIntStateOf(0) }
    var hintFull by remember { mutableIntStateOf(0) } // its height when shown (it slides away and back)
    val px = LocalOsd.current.px
    Box(Modifier.fillMaxSize().padding(horizontal = pxDp(2f), vertical = pxDp(2f))) {
        Column(
            Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top, unbounded = true).onSizeChanged { bodyH = it.height },
            // pages without a stretchy part share the spare height out between their rows (no empty band below)
            verticalArrangement = if (!spread) Arrangement.spacedBy(pxDp(gap)) else remember(fit, gap, px) {
                object : Arrangement.Vertical {
                    override val spacing = 0.dp
                    override fun androidx.compose.ui.unit.Density.arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                        val g = gap * px + if (sizes.size > 1) fit.extra.floatValue / (sizes.size - 1) else 0f
                        var y = 0f
                        for (i in sizes.indices) { outPositions[i] = y.roundToInt(); y += sizes[i] + g }
                    }
                }
            },
            content = body,
        )
        Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().onSizeChanged { hintH = it.height; if (it.height > hintFull) hintFull = it.height }) { hint() }
    }
    @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
    val ime = WindowInsets.isImeVisible
    LaunchedEffect(bodyH, hintH, hintFull, availH, ime) {
        // keyboard up: keep the size (the bottom just slides under it); old Androids may still shrink the window
        if (bodyH == 0 || ime) return@LaunchedEffect // (the help bar may be hidden: then it simply slides over the bottom when it comes back)
        // Only ever shrinks (it can't flip back and forth any more, which made some tablets flicker between two
        // sizes); it grows again only when the screen itself got taller. Height left over goes to the stretchy area.
        val f = fit.fit.floatValue
        val ex = fit.extra.floatValue
        // the shrink decision always counts the help bar at full height (whether it is showing or not), so
        // hiding / showing it never re-scales the page; only the stretchy area follows it
        val hintRes = maxOf(hintH, hintFull, (px * 22f).toInt())
        val base = bodyH + hintRes + px * 6f - ex
        val baseNow = bodyH + hintH + px * 6f - ex
        when {
            base > availH + 1f -> {
                fit.extra.floatValue = 0f
                fit.fit.floatValue = (f * availH / base * 0.995f).coerceIn(0.12f, 1f)
                fit.settledFor = availH
            }
            f < 0.999f && fit.settledFor >= 0f && availH > fit.settledFor + 8f -> {
                fit.extra.floatValue = 0f
                fit.fit.floatValue = (f * availH / base * 0.995f).coerceIn(0.12f, 1f)
                fit.settledFor = availH
            }
            else -> {
                val want = (availH - baseNow).coerceAtLeast(0f)
                if (kotlin.math.abs(want - ex) > 2f) fit.extra.floatValue = want
                if (fit.settledFor < 0f) fit.settledFor = availH
            }
        }
    }
}

/** A blinking caret / colon, like a VCR clock. */
@Composable
internal fun rememberBlink(ms: Long = 530): Boolean {
    var on by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { while (true) { com.reverender.r95.core.AppVisibility.awaitForeground(); delay(ms); on = !on } }
    return on
}

@Composable
private fun VhsMain(fit: FitState, availH: Float, onMenu: () -> Unit, onLoad: () -> Unit, onPlaylist: () -> Unit, onEq: () -> Unit) {
    val ctx = LocalContext.current
    val state = LocalAppState.current
    val p = state.player
    val o = LocalOsd.current
    var query by remember { mutableStateOf("") }
    val blink = rememberBlink()
    // once a second: clock, counter, level (the rest only changes on taps)
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var level by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            com.reverender.r95.core.AppVisibility.awaitForeground()
            now = System.currentTimeMillis()
            val f = AudioBus.currentFrame()
            level = ((f.rmsL + f.rmsR) * 1.8f).coerceIn(0f, 1f)
            delay(120)
        }
    }
    FitPage(fit, availH, 1.4f, hint = {
        // ---- the hint bar
        Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { Haptics.tick(); onMenu() } }) {
            Column {
                VhsText("▲APPS ▼NOTIF ◀▶SWIPE", invert = true)
                VhsText("TAP HERE FOR SETUP", invert = true)
            }
        }
    }, body = {
        // the title bars: both lines empty (SETUP's TITLE1 / TITLE2) → no bars at all, one empty → a single bar
        // the title block: TITLE1 on top, the news ticker (SETUP > RSS) running through the second line
        val t1 = OsPrefs.vhsTitle1.trim(); val rss = OsPrefs.vhsRss.trim()
        LaunchedEffect(rss) { VhsRss.run(rss) }
        val news: @Composable () -> Unit = { VhsMarquee(vhsSafe(VhsRss.ticker()).trim(), fill = true, swapAtLoop = true, secPerChar = 0.34f - 0.29f * OsPrefs.vhsRssSpeed) }
        // (VHS+ APK: the notification area sits at the right end of the top bar)
        if (t1.isNotEmpty() || rss.isNotEmpty()) Box(Modifier.fillMaxWidth()) {
            if (t1.isNotEmpty() && rss.isNotEmpty()) VhsHeader(t1, "", news)
            else if (t1.isNotEmpty()) VhsHeader1(t1)
            else VhsHeader1("", news)
            VhsNotifArea(Modifier.align(Alignment.TopEnd).padding(top = pxDp(1f), end = pxDp(2f)))
        }
        // swipe up opens the phone keyboard for this search; its enter key launches the top match
        // APPS or RADIO: what the search finds (tap the word in front of the search line to switch)
        var radioMode by remember { mutableStateOf(false) }
        // something chosen (station, deck, next song) that waits for PLAY
        var pending by remember { mutableStateOf<Pending?>(null) }
        // a station just held off the row: UNDO for a few seconds
        var undoStation by remember { mutableStateOf<Pair<Int, com.reverender.r95.core.Station>?>(null) }
        LaunchedEffect(undoStation) { if (undoStation != null) { delay(5000); undoStation = null } }
        val stations = remember { androidx.compose.runtime.mutableStateListOf<com.reverender.r95.net.StationDirectory.Row>() }
        var radioMsg by remember { mutableStateOf<String?>(null) }
        var radioN by remember { mutableIntStateOf(0) }
        val scope = androidx.compose.runtime.rememberCoroutineScope()
        fun tune(r: com.reverender.r95.net.StationDirectory.Row) {
            // selected, not playing yet: PLAY starts it
            Haptics.tick(); pending = Pending("url:" + r.url, vhsSafe(r.name).trim().take(15)) { AmpEngine.connectRadioStream(r.url, r.name.take(22)) }; query = ""
        }
        fun radioSearch() {
            val q = query.trim(); if (q.length < 2) return
            val n = ++radioN
            radioMsg = "SEARCHING STATIONS…"
            scope.launch {
                val (r, unavailable) = runCatching { com.reverender.r95.net.StationDirectory.search(q) }.getOrElse { emptyList<com.reverender.r95.net.StationDirectory.Row>() to true }
                if (n != radioN) return@launch
                stations.clear(); stations.addAll(r.take(5))
                radioMsg = when { unavailable && r.isEmpty() -> "DIRECTORY UNAVAILABLE"; r.isEmpty() -> "NO STATION FOUND"; else -> null }
            }
        }
        // search as you type (a moment after the last letter)
        LaunchedEffect(query, radioMode) {
            if (!radioMode) return@LaunchedEffect
            stations.clear(); radioMsg = null
            if (query.trim().length >= 2) { delay(650); radioSearch() }
        }
        fun enter() {
            if (radioMode) { stations.firstOrNull()?.let { tune(it) } ?: radioSearch(); return }
            val hit = Launcher.apps.firstOrNull { it.label.uppercase().contains(query.trim()) && query.isNotBlank() }
            if (hit != null) { Launcher.launch(ctx, hit, fullScreen = true); query = "" }
        }
        androidx.compose.runtime.key(radioMode) { RegisterKb(VhsKb({ query }, { query = it.take(18) }, label = if (radioMode) "FIND A RADIO STATION" else "FIND AN APP") { enter() }) }
        // (the phone keyboard types here: swipe up, or tap the search line)
        val openKb = LocalOpenKb.current
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(1f)), verticalAlignment = Alignment.CenterVertically) {
            VhsKey(if (radioMode) "RADIO" else "APPS", boxed = true) { radioMode = !radioMode }
            Box(Modifier.weight(1f).pointerInput(Unit) { detectTapGestures(onLongPress = { query = "" }) { openKb() } }) {
                VhsText(":" + query.takeLast(9) + if (blink) "_" else " ")
            }
            // hold to load a video tape (a quick tap does nothing)
            VhsHoldKey("⏏LOAD", onLoaded = onLoad)
        }
        // results, or the pinned apps when nothing is typed
        val q = query.trim()
        val list = if (q.isEmpty()) OsPrefs.pinned.mapNotNull { Launcher.find(it) }.take(4)
            else Launcher.apps.filter { it.label.uppercase().contains(q) }.sortedBy { if (it.label.uppercase().startsWith(q)) 0 else 1 }.take(5)
        // a fixed 5-line area, so the page never jumps while typing
        // (plus any spare screen height, so the page always reaches the bottom; the spectrum fills it)
        val spare = with(LocalDensity.current) { fit.extra.floatValue.toDp() }
        Column(Modifier.height(pxDp(5 * 9f + 4 * 1.4f) + spare), verticalArrangement = Arrangement.spacedBy(pxDp(1.4f))) {
            // VAPOR_AMP's radio stations on one line: swipe it sideways for more, tap to tune in
            if (q.isEmpty()) {
                var station by remember { mutableIntStateOf(AmpEngine.pressedStation) }
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(pxDp(3f)), verticalAlignment = Alignment.CenterVertically) {
                    VhsText("♪")
                    val deckOn = p.mode == com.reverender.r95.audio.SourceMode.DECK
                    // tapping only selects; PLAY starts it
                    val pk = pending?.key
                    VhsKey("DECK", selected = if (pk != null) pk == "deck" else deckOn, boxed = true) { pending = Pending("deck", "DECK") { AmpEngine.switchToInternalDeck() } }
                    Catalog.radioPresets.forEachIndexed { i, s ->
                        val nm = s.name.substringAfter(' ').uppercase().take(16)
                        // hold a station to take it off the row (UNDO brings it back)
                        VhsKey(nm, selected = if (pk != null) pk == "st$i" else (p.mode == com.reverender.r95.audio.SourceMode.RADIO && station == i), boxed = true, onLong = {
                            Catalog.removeStation(ctx, i)?.let { gone ->
                                Haptics.clunk("eject"); AmpEngine.stationRemoved(i)
                                if (station == i) station = -1 else if (station > i) station--
                                if (pending?.key?.startsWith("st") == true) pending = null
                                undoStation = i to gone
                            }
                        }) {
                            pending = Pending("st$i", vhsSafe(nm).take(15)) { station = i; AmpEngine.playInternetRadio(i) }
                        }
                    }
                }
            }
            if (radioMode && q.isNotEmpty()) {
                radioMsg?.let { VhsText(it, color = o.fg.copy(alpha = 0.6f)) }
                stations.forEachIndexed { i, r ->
                    // tap: listen; hold: add it to the station row
                    Box(Modifier.pointerInput(r.url) { detectTapGestures(onLongPress = {
                        val added = Catalog.addStation(ctx, com.reverender.r95.core.Station("📻 " + r.name.trim(), r.url))
                        Haptics.clunk("play"); radioMsg = if (added) "ADDED TO THE STATIONS" else "ALREADY IN THE STATIONS"
                    }) { tune(r) } }) {
                        VhsText("${i + 1} " + vhsSafe(r.name).trim().take(19))
                    }
                }
            } else {
                list.forEachIndexed { i, a ->
                    Box(Modifier.pointerInput(a.component) { detectTapGestures { Haptics.tick(); Launcher.launch(ctx, a, fullScreen = true); query = "" } }) {
                        VhsText("${i + 1} " + a.label.uppercase().take(19))
                    }
                }
                if (q.isNotEmpty() && list.isEmpty()) VhsText("NO APP FOUND", color = o.fg.copy(alpha = 0.6f))
            }
            // the free space: a VCR-style spectrum of the music (search results take its place while typing)
            if (q.isEmpty()) VhsSpectrum(Modifier.fillMaxWidth().weight(1f)) { p.isPlaying }
        }

        Spacer(Modifier.height(pxDp(1f)))
        // ---- status line: [OK] ● ▶ PLAY 11:35PM
        val playing = p.isPlaying
        // another music app (Spotify, YouTube Music …) playing: the transport keys steer that app
        LaunchedEffect(Unit) {
            while (true) {
                com.reverender.r95.core.AppVisibility.awaitForeground(); ExtMedia.poll(ctx, p.isPlaying)
                // VHS+ (notification access): the other app's song title for the ticker
                if (com.reverender.r95.BuildConfig.NOTIF && ExtMedia.active) runCatching { com.reverender.r95.os.ExternalMedia.refresh(ctx, false) }
                delay(700)
            }
        }
        val ext = ExtMedia.active && pending == null
        val clock = SimpleDateFormat(if (OsPrefs.vhs24h) "HH:mm" else "hh:mma", Locale.US).format(Date(now)).let { if (!blink) it.replace(':', ' ') else it }
        // ---- transport (VAPOR_AMP)
        Row(horizontalArrangement = Arrangement.spacedBy(pxDp(3f)), verticalAlignment = Alignment.CenterVertically) {
            val radioOn = p.mode == com.reverender.r95.audio.SourceMode.RADIO
            // ◀◀ ▶▶: while playing they switch straight away; while stopped / paused they only pick the next
            // song or station, and PLAY starts it
            fun hop(dir: Int) {
                if (ext) { Haptics.tick(); ExtMedia.key(ctx, if (dir > 0) android.view.KeyEvent.KEYCODE_MEDIA_NEXT else android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS); return }
                val files = p.mode == com.reverender.r95.audio.SourceMode.FILES
                if (playing && pending == null) {
                    if (files && AmpEngine.stepFile(dir)) return
                    if (radioOn) AmpEngine.playInternetRadio((AmpEngine.pressedStation.coerceAtLeast(0) + dir).mod(Catalog.radioPresets.size))
                    else AmpEngine.stepDeck(dir, play = true) // the next / previous deck song
                    return
                }
                val pk = pending?.key
                when {
                    pk != null && pk.startsWith("st") || pk == null && radioOn -> {
                        val cur = pk?.removePrefix("st")?.toIntOrNull() ?: AmpEngine.pressedStation.coerceAtLeast(0)
                        val i = (cur + dir).mod(Catalog.radioPresets.size)
                        pending = Pending("st$i", vhsSafe(Catalog.radioPresets[i].name.substringAfter(' ')).take(15)) { AmpEngine.playInternetRadio(i) }
                    }
                    files -> AmpEngine.cueFileStep(dir)?.let { n -> pending = Pending("file", vhsSafe(n).take(15)) { AmpEngine.playCuedFile() } }
                    else -> { val t = AmpEngine.stepDeck(dir, play = false); pending = Pending("cue", vhsSafe(t).take(15)) { AmpEngine.playCurrentAudio("PLAY") } }
                }
                Haptics.tick()
            }
            VhsKey("◀◀", boxed = true, scale = 1.3f) { hop(-1) }
            // hold to play / pause, so a brush of the thumb doesn't start the music
            VhsHoldKey(if (pending != null) "▶" else if (playing || ext && ExtMedia.playing) "❚❚" else "▶", holdMs = 450, scale = 1.3f, sound = false, selected = (playing || ext && ExtMedia.playing) && pending == null) {
                val pd = pending
                when {
                    pd != null -> { pending = null; ExtMedia.release(); pd.start() }
                    ext -> ExtMedia.key(ctx, android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                    else -> p.togglePlay()
                }
            }
            // ■ pauses the other app; pressed again (or while it's paused) the keys go back to the tape deck
            VhsKey("■", boxed = true, scale = 1.3f) {
                Haptics.clunk("stop")
                if (ext) { if (ExtMedia.playing) ExtMedia.key(ctx, android.view.KeyEvent.KEYCODE_MEDIA_PAUSE) else ExtMedia.release() } else p.stop()
            }
            VhsKey("▶▶", boxed = true, scale = 1.3f) { hop(+1) }
            Spacer(Modifier.weight(1f))
            // the clock, as big as the keys
            Box(Modifier.pointerInput(Unit) { detectTapGestures(onLongPress = { openClockApp(ctx) }) }) { VhsText(clock, scale = 1.3f) }
        }
        // ---- status on the left, the date under the clock: [OK] ● ▶PLAY   TUE 24 NOV
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(pxDp(1.5f))) {
            VhsText("OK", invert = true)
            VhsText(if (playing || ext && ExtMedia.playing) "●" else " ")
            VhsText(when {
                ext -> if (ExtMedia.playing) "EXT▶" else "EXT❚❚"
                playing -> "▶PLAY"; p.positionMs() > 0 -> "❚PAUSE"; else -> "■STOP"
            })
            Spacer(Modifier.weight(1f))
            Box(Modifier.pointerInput(Unit) { detectTapGestures(onLongPress = { openClockApp(ctx) }) }) {
                VhsText(SimpleDateFormat("EEE dd MMM", Locale.US).format(Date(now)).uppercase(), color = o.fg.copy(alpha = 0.85f))
            }
        }
        val title = when (p.mode) {
            com.reverender.r95.audio.SourceMode.DECK -> "TRK %02d:%s".format(p.deckIndex + 1, Catalog.deck.getOrNull(p.deckIndex)?.title.orEmpty())
            // radio: the station, then the song it says is playing (updated whenever the station changes song)
            com.reverender.r95.audio.SourceMode.RADIO -> com.reverender.r95.audio.IcyNow.title.let { icy ->
                if (icy.isNotBlank() && !p.nowPlaying.contains(icy)) p.nowPlaying.substringBefore(" — ") + " — " + icy else p.nowPlaying
            }.ifBlank { "NO TAPE" }
            else -> p.nowPlaying.ifBlank { "NO TAPE" }
        }
        val pd = pending
        val un = undoStation
        if (un != null) VhsKey(("UNDO: " + vhsSafe(un.second.name.substringAfter(' ')).trim()).take(21), selected = true) {
            Catalog.insertStation(ctx, un.first, un.second); AmpEngine.stationInserted(un.first); undoStation = null; Haptics.tick()
        }
        else if (pd != null) VhsText(("READY:" + pd.label).take(21), invert = blink)
        // what's playing runs past as a ticker, so long names are readable
        else if (ext) VhsMarquee(running = ExtMedia.playing, text = "♪ " + vhsSafe(listOf(com.reverender.r95.os.ExternalMedia.artist, com.reverender.r95.os.ExternalMedia.title).filter { it.isNotBlank() }.joinToString(" - ").ifBlank { "OTHER MUSIC APP" }).trim())
        // (runs only while playing; a radio station's song changes it as soon as the station says so)
        else VhsMarquee(vhsSafe(title).trim(), running = playing)

        // ---- VOLUME
        VhsText("VOLUME")
        VhsBar(p.volume) { v ->
            // a click of the motor every 5 %, like a stepped volume knob
            if ((v * 20).toInt() != (p.volume * 20).toInt()) Haptics.tick()
            p.changeVolume(v)
        }
        VhsTicks(level)
        // ---- EQ: hold EQ for the full equalizer; hold a preset to switch it on (swipe the row for more)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            VhsHoldKey("EQ", holdMs = 450, sound = false, selected = state.eqEnabled && state.eqPreset != "FLAT") { Haptics.clunk("play"); onEq() }
            listOf("FLAT", "VAPOR", "BASS", "CYBER").forEach { n ->
                VhsHoldKey(n, holdMs = 400, scale = 0.85f, sound = false, selected = state.eqEnabled && state.eqPreset == n) {
                    Haptics.tick(); state.setEqEnabled(true); state.applyEqPreset(n)
                }
            }
        }
        // ---- SP EP SLP = tape speed
        val speeds = listOf("SP" to 1f, "EP" to 0.85f, "SLP" to 0.7f, "SLO" to 0.5f, "FF" to 1.5f)
        var speedSel by remember { mutableStateOf("SP") }
        // tape speed on the left, the tape counter on the right
        val dur = p.durationMs(); val pos = p.positionMs()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(2.5f)), verticalAlignment = Alignment.CenterVertically) {
            speeds.forEach { (k, v) -> VhsKey(k, selected = speedSel == k) { speedSel = k; p.changeSpeed(v) } }
        }
        // ---- your own songs: the playlist, shuffle and repeat
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(2.5f)), verticalAlignment = Alignment.CenterVertically) {
            // hold to open, like the other deck keys
            VhsHoldKey("PLAYLIST", holdMs = 450, sound = false) { Haptics.clunk("play"); onPlaylist() }
            Spacer(Modifier.weight(1f))
            VhsKey("SHUF", selected = AmpEngine.shuffle, boxed = true) { AmpEngine.shuffle = !AmpEngine.shuffle }
            VhsKey(when (AmpEngine.repeat) { 0 -> "RPT OFF"; 2 -> "RPT ONE"; else -> "RPT ALL" }, selected = AmpEngine.repeat != 0, boxed = true) { AmpEngine.repeat = (AmpEngine.repeat + 1) % 3 }
        }
        VhsTimeline(if (dur > 0) pos.toFloat() / dur else 0f) { v -> if (dur > 0) p.seekTo((v * dur).toLong()) }
        Row(Modifier.fillMaxWidth()) {
            VhsText("BEGIN"); Spacer(Modifier.weight(1f))
            now.let { }
            VhsText("%d:%02d:%02d".format(pos / 3_600_000, pos / 60_000 % 60, pos / 1000 % 60))
            Spacer(Modifier.weight(1f)); VhsText("END")
        }
    })
}

/** The video deck can switch the CRT off over the video (its FX OFF key). */
internal object VhsCrtGate { var off by mutableStateOf(false) }

/** A choice waiting for PLAY: [key] identifies it for highlighting, [label] is shown as READY:…, [start] plays it. */
private class Pending(val key: String, val label: String, val start: () -> Unit)

/**
 * Segmented spectrum bars in the OSD colour with falling peak dots. Bars glide (fast rise, slow fall) instead of
 * jumping every frame. With nothing playing a standby show runs: rolling waves, a bouncing equalizer dance,
 * a centre pulse and a scanner sweep, cross-fading into each other.
 */
@Composable
private fun VhsSpectrum(modifier: Modifier, musicOn: () -> Boolean) {
    val musicNow by androidx.compose.runtime.rememberUpdatedState(musicOn)
    val o = LocalOsd.current
    val tick = remember { mutableLongStateOf(0L) }
    val n = 22
    val lvl = remember { FloatArray(n) }
    val peak = remember { FloatArray(n) }
    val peakV = remember { FloatArray(n) }
    val dance = remember { FloatArray(n) { 0.3f } }
    val ghost = remember { FloatArray(n) }
    var themeTag by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(themeTag) { if (themeTag != null) { delay(1600); themeTag = null } }
    val danceTarget = remember { FloatArray(n) { 0.3f } }
    val rain = remember { FloatArray(n) }
    var loopGen by remember { mutableStateOf(0) }
    val lastTickMs = remember { longArrayOf(0L) }
    // watchdog: if the animation loop ever stops while the screen shows, start it again
    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            val vis = com.reverender.r95.core.AppVisibility.visible
            if (vis && !VhsUi.drawerOpen && System.currentTimeMillis() - lastTickMs[0] > 1500) {
                android.util.Log.w("r95", "viz: loop stalled, restarting"); loopGen++
            }
        }
    }
    LaunchedEffect(loopGen) {
        var quietSince = 0L
        var last = System.nanoTime()
        var lastFrameT = 0L; var lastFresh = 0L; var lastLog = 0L
        val rnd = java.util.Random()
        var nextDance = 0L
        while (true) {
            lastTickMs[0] = System.currentTimeMillis()
            com.reverender.r95.core.AppVisibility.awaitForeground()
            lastTickMs[0] = System.currentTimeMillis()
            if (VhsUi.drawerOpen || VhsUi.mainHidden) { delay(120); last = System.nanoTime(); continue } // hidden: the apps drawer or another menu is over it
            val nowNs = System.nanoTime()
            val dt = ((nowNs - last) / 1e9f).coerceIn(0.005f, 0.1f); last = nowNs
            val f = AudioBus.currentFrame()
            val b = f.bands
            val nowMs = System.currentTimeMillis()
            // a frame that stopped changing is old sound (paused / stopped), not music
            if (f.timeNanos != lastFrameT) { lastFrameT = f.timeNanos; lastFresh = nowMs }
            // music only counts while something actually plays (stopped / paused / tuning = the standby shows,
            // even if the analyser still sees tape hiss or the last bit of sound)
            val loud = musicNow() && nowMs - lastFresh < 250 && b.any { it > 0.03f }
            if (loud) quietSince = 0L else if (quietSince == 0L) quietSince = nowMs
            val idle = !loud && quietSince != 0L && nowMs - quietSince > 250
            // (seconds within the hour: the full clock in a Float only changes every two minutes, which froze the shows)
            val ts = (nowMs % 3_600_000L) / 1000f
            // standby: seven shows, 6 s each, blended over the last second
            val shows = 7
            val show = (ts / 6f).toInt() % shows
            val nextShow = (show + 1) % shows
            val mix = (((ts % 6f) - 5f) / 1f).coerceIn(0f, 1f).let { it * it * (3 - 2 * it) }
            // rain: drops land on random bars and drain away
            for (i in 0 until n) rain[i] = (rain[i] - dt * 1.4f).coerceAtLeast(0.03f)
            if (rnd.nextFloat() < dt * 9f) rain[rnd.nextInt(n)] = 0.55f + rnd.nextFloat() * 0.4f
            if (idle && nowMs > nextDance) {
                nextDance = nowMs + 380
                for (i in 0 until n) danceTarget[i] = 0.1f + rnd.nextFloat() * (0.35f + 0.45f * kotlin.math.abs(kotlin.math.sin(ts * 0.7f + i * 0.3f)))
            }
            for (i in 0 until n) dance[i] += (danceTarget[i] - dance[i]) * (1 - kotlin.math.exp(-dt * 9f))
            fun standby(k: Int, i: Int): Float {
                val x = i / (n - 1f)
                return when (k) {
                    0 -> 0.32f + 0.22f * kotlin.math.sin(ts * 2.2f + i * 0.42f) + 0.10f * kotlin.math.sin(ts * 3.7f - i * 0.8f)     // rolling waves
                    1 -> dance[i]                                                                                                      // equalizer dance
                    2 -> { val c = kotlin.math.abs(x - 0.5f) * 2f; val ph = (ts * 0.9f) % 1f                                          // centre pulse rings
                        (0.75f - c * 0.45f) * (0.35f + 0.65f * kotlin.math.max(0f, 1f - kotlin.math.abs(c - ph) * 3.2f)) }
                    3 -> { val pos = 0.5f + 0.5f * kotlin.math.sin(ts * 1.6f); val d = kotlin.math.abs(x - pos)                      // scanner sweep
                        0.08f + 0.75f * kotlin.math.exp(-d * d * 90f) + 0.06f * kotlin.math.sin(ts * 5f + i) }
                    4 -> { val tri = ((ts * 0.45f) % 2f).let { if (it > 1f) 2f - it else it } * (n - 1)                          // bouncing ball
                        val bh = 0.15f + 0.8f * kotlin.math.abs(kotlin.math.sin(ts * 3.4f)); val d = i - tri
                        0.04f + bh * kotlin.math.exp(-d * d * 0.7f) }
                    5 -> rain[i]                                                                                                       // rain drops
                    else -> { val k = ((i + (ts * 9f).toInt()) % n) / (n - 1f)                                                         // marching stairs
                        if (((ts / 3f).toInt() and 1) == 0) 0.06f + 0.88f * k else 0.94f - 0.88f * k }
                }
            }
            for (i in 0 until n) {
                // 64 log bands → 22 bars (skip the very top, mostly empty)
                val a = i * 56 / n; val e = maxOf(a + 1, (i + 1) * 56 / n)
                var m = 0f
                for (k in a until e) m = maxOf(m, b.getOrElse(k) { 0f })
                val v = if (idle) (standby(show, i) * (1 - mix) + standby(nextShow, i) * mix).coerceIn(0.02f, 0.95f)
                    else (m * 1.2f).coerceIn(0f, 1f)
                // glide: quick rise, gentle fall (frame-rate independent)
                // music: bars jump straight to the hit and drop fast; the standby shows still glide
                val k = if (idle) (if (v > lvl[i]) 1 - kotlin.math.exp(-dt * 22f) else 1 - kotlin.math.exp(-dt * 6f))
                    else if (v > lvl[i]) 1f else 1 - kotlin.math.exp(-dt * 16f)
                lvl[i] += (v - lvl[i]) * k
                // peak dots: hold, then fall with gravity
                // phosphor trail: the bar's afterglow fades slower the more motion blur is set
                ghost[i] = maxOf(lvl[i], ghost[i] - dt * (2.5f + 6f * (1f - OsPrefs.vhsBlur)))
                if (lvl[i] >= peak[i]) { peak[i] = lvl[i]; peakV[i] = -0.25f } else { peakV[i] += dt * 1.6f; if (peakV[i] > 0f) peak[i] = (peak[i] - peakV[i] * dt).coerceAtLeast(lvl[i]) }
            }
            tick.longValue++
            if (nowMs - lastLog > 30000) { lastLog = nowMs; android.util.Log.i("r95", "viz: music=${musicNow()} fresh=${nowMs - lastFresh < 250} loud=$loud idle=$idle t=${f.timeNanos}") }
            delay(25) // ~40 fps: smooth, and light on slow phones
        }
    }
    Box(modifier.pointerInput(Unit) {
        // hold the bars: next look (shape + colours)
        detectTapGestures(onLongPress = {
            val nx = (OsPrefs.vhsVizTheme + 1) % VIZ_THEMES.size
            OsPrefs.setVhsVizTheme(nx); Haptics.clunk("play"); themeTag = VIZ_THEMES[nx].first
        })
    }) {
    val theme = VIZ_THEMES[OsPrefs.vhsVizTheme.coerceIn(0, VIZ_THEMES.size - 1)]
    val scopePath = remember { androidx.compose.ui.graphics.Path() }
    Canvas(Modifier.matchParentSize()) {
        val ph = (tick.longValue % 100_000L) * 0.12f // animation phase for the ripple / jitter details
        val px = o.px
        val colW = size.width / n
        val barW = colW - px * 1.6f
        val seg = px * 1.4f; val segGap = px * 0.6f
        val segs = ((size.height - px) / (seg + segGap)).toInt().coerceAtLeast(1)
        val sh = Color.Black.copy(alpha = 0.6f)
        val style = theme.second
        val palette = theme.third
        fun colAt(i: Int, h: Float): Color = when (palette) {
            5 -> when { h < 0.6f -> Color(0xFF2BFF6A); h < 0.85f -> Color(0xFFFFE23A); else -> Color(0xFFFF3030) } // VU: green, yellow, red
            6 -> when (i % 3) { 0 -> Color(0xFFFF3B3B); 1 -> Color(0xFF3BFF5A); else -> Color(0xFF4A7BFF) }         // CRT phosphor triads
            7 -> Color(0xFFFFB000)                                                                                   // amber
            8 -> if (h < 0.86f) Color(0xFF6CFFE6) else Color(0xFFFF7A2E)                                             // VFD: phosphor aqua, orange "peak" lamps
            else -> o.fg
        }
        val mirror = style == 2
        val hard = OsPrefs.vhsVizTheme != 0 // every look but CLASSIC steps from segment to segment (no in-between glow)
        val blurA = OsPrefs.vhsBlur
        for (i in 0 until n) {
            val x = i * colW
            val v = lvl[i]
            if (blurA > 0.02f && !mirror && style != 6 && ghost[i] > v + 0.01f) {
                // afterglow above the bar (or both sides of the middle when mirrored)
                val gc = colAt(i, ghost[i]).copy(alpha = 0.12f + 0.25f * blurA)
                val H = size.height - px
                if (mirror) {
                    val gh = ghost[i] * H / 2; val vh = v * H / 2
                    drawRect(gc, Offset(x, size.height / 2 - gh), Size(barW, gh - vh))
                    drawRect(gc, Offset(x, size.height / 2 + vh), Size(barW, gh - vh))
                } else drawRect(Brush.verticalGradient(listOf(gc.copy(alpha = 0f), gc), size.height - ghost[i] * H, size.height - v * H), Offset(x, size.height - ghost[i] * H), Size(barW, (ghost[i] - v) * H))
            }
            val vq = if (hard) (v * segs).toInt() / segs.toFloat() else v // stepped level for the new looks
            // tube glow: a soft halo around the lit part of the bar (every look but CLASSIC)
            if (hard && vq > 0.01f && (style == 0 || style == 4 || style == 5)) {
                val H = size.height - px
                val gc = colAt(i, vq)
                if (mirror) {
                    val hh = vq * H / 2
                    drawRect(gc.copy(alpha = 0.10f), Offset(x - px, size.height / 2 - hh - px), Size(barW + px * 2, hh * 2 + px * 2))
                } else {
                    val top = size.height - vq * H
                    drawRect(Brush.verticalGradient(listOf(gc.copy(alpha = 0.22f), gc.copy(alpha = 0.06f)), top - px * 2, size.height), Offset(x - px, top - px * 2), Size(barW + px * 2, vq * H + px * 2))
                }
            }
            when (style) {
                4 -> { // tape blocks: chunky double-height blocks with a red / cyan colour fringe
                    val bs = (seg + segGap) * 2; val nb = (size.height / bs).toInt().coerceAtLeast(1)
                    val lit = (v * nb).toInt()
                    for (k in 0 until nb) {
                        val y = size.height - (k + 1) * bs + segGap
                        if (k < lit) {
                            drawRect(Color(0xFFFF1A40).copy(alpha = 0.55f), Offset(x - px * 0.8f, y), Size(barW, bs - segGap * 2))
                            drawRect(Color(0xFF1AE6FF).copy(alpha = 0.55f), Offset(x + px * 0.8f, y), Size(barW, bs - segGap * 2))
                            drawRect(colAt(i, k / nb.toFloat()), Offset(x, y), Size(barW, bs - segGap * 2))
                        } else drawRect(colAt(i, 0f).copy(alpha = 0.07f), Offset(x, y), Size(barW, bs - segGap * 2))
                    }
                }
                5 -> { // scanlines: the bar is drawn by the beam, line by line, brighter at the top, with tube wobble
                    val H = size.height - px
                    val h = vq * H; val top = size.height - h
                    val c = colAt(i, vq)
                    var y = size.height - px; var row = 0
                    while (y > top) {
                        val k = ((size.height - y) / h.coerceAtLeast(1f)).coerceIn(0f, 1f)
                        val jit = kotlin.math.sin(row * 0.9f + ph * 0.6f + i) * px * 0.45f
                        drawRect(c.copy(alpha = 0.30f + 0.70f * k), Offset(x + jit, y), Size(barW, px * 0.75f))
                        y -= px * 1.6f; row++
                    }
                    if (h > px) {
                        drawRect(c.copy(alpha = 0.30f), Offset(x - px, top - px * 1.4f), Size(barW + px * 2, px * 2.8f))
                        drawRect(Color.White, Offset(x, top), Size(barW, px))
                    }
                }
                6 -> if (i == 0) { // scope: a phosphor trace on a graticule, the bars swinging it up and down
                    val cy = size.height / 2; val H = size.height - px * 2
                    val c = colAt(0, 0.5f)
                    for (gx in 0..10) drawRect(c.copy(alpha = if (gx == 5) 0.22f else 0.09f), Offset(size.width * gx / 10f, 0f), Size(px * 0.4f, size.height))
                    for (gy in 0..8) drawRect(c.copy(alpha = if (gy == 4) 0.22f else 0.09f), Offset(0f, size.height * gy / 8f), Size(size.width, px * 0.4f))
                    for (tk in 0..50) drawRect(c.copy(alpha = 0.2f), Offset(size.width * tk / 50f, cy - px), Size(px * 0.4f, px * 2))
                    scopePath.reset()
                    for (j in 0 until n) {
                        val lv = (lvl[j] * segs).toInt() / segs.toFloat()
                        val px2 = j * colW + colW / 2
                        val py = cy - (if (j % 2 == 0) 1f else -1f) * lv * H / 2 * 0.92f
                        if (j == 0) { scopePath.moveTo(0f, cy); scopePath.lineTo(px2, py) } else scopePath.lineTo(px2, py)
                    }
                    scopePath.lineTo(size.width, cy)
                    drawPath(scopePath, c.copy(alpha = 0.14f), style = androidx.compose.ui.graphics.drawscope.Stroke(px * 4f), blendMode = androidx.compose.ui.graphics.BlendMode.Plus)
                    drawPath(scopePath, c.copy(alpha = 0.40f), style = androidx.compose.ui.graphics.drawscope.Stroke(px * 1.8f), blendMode = androidx.compose.ui.graphics.BlendMode.Plus)
                    drawPath(scopePath, lerpC(c, Color.White, 0.45f), style = androidx.compose.ui.graphics.drawscope.Stroke(px * 0.7f))
                }
                7 -> { // wireframe: each bar a glowing wire box in 3D (front, top and side edges)
                    val H = size.height - px * 3
                    val h = vq * H
                    if (h > px) {
                        val c = colAt(i, vq)
                        val d = colW * 0.32f
                        val l = x; val r = x + barW * 0.78f; val t = size.height - h; val btm = size.height - px * 0.5f
                        val st = androidx.compose.ui.graphics.drawscope.Stroke(px * 0.5f)
                        drawRect(c.copy(alpha = 0.18f), Offset(l - px * 0.6f, t - px * 0.6f), Size(r - l + px * 1.2f, btm - t + px * 1.2f), style = androidx.compose.ui.graphics.drawscope.Stroke(px * 1.8f))
                        drawRect(c, Offset(l, t), Size(r - l, btm - t), style = st)
                        drawLine(c, Offset(l, t), Offset(l + d, t - d), px * 0.5f)
                        drawLine(c, Offset(r, t), Offset(r + d, t - d), px * 0.5f)
                        drawLine(c, Offset(l + d, t - d), Offset(r + d, t - d), px * 0.5f)
                        drawLine(c.copy(alpha = 0.6f), Offset(r + d, t - d), Offset(r + d, btm - d), px * 0.5f)
                        drawLine(c.copy(alpha = 0.6f), Offset(r, btm), Offset(r + d, btm - d), px * 0.5f)
                        // rungs every few segments, so the box reads as a stack
                        var y = btm - (seg + segGap) * 3
                        while (y > t + px) { drawLine(c.copy(alpha = 0.35f), Offset(l, y), Offset(r, y), px * 0.4f); y -= (seg + segGap) * 3 }
                    }
                }
                2 -> { // mirror: bars stand on a glowing horizon, their reflection rippling in the water below
                    val hz = size.height * 0.64f
                    if (i == 0) {
                        drawRect(colAt(0, 0.5f).copy(alpha = 0.25f), Offset(0f, hz - px * 1.5f), Size(size.width, px * 3))
                        drawRect(Color.White.copy(alpha = 0.8f), Offset(0f, hz - px * 0.25f), Size(size.width, px * 0.5f))
                    }
                    val step = seg + segGap
                    val segU = ((hz - px) / step).toInt().coerceAtLeast(1)
                    val lit = (v * segU).toInt()
                    for (sgi in 0 until segU) {
                        val y = hz - px - (sgi + 1) * step + segGap
                        val c = colAt(i, sgi / segU.toFloat())
                        drawRect(c.copy(alpha = if (sgi < lit) 1f else 0.07f), Offset(x, y), Size(barW, seg))
                    }
                    val down = size.height - hz
                    for (sgi in 0 until lit) {
                        val ry = hz + px + sgi * step * 0.6f
                        if (ry > size.height) break
                        val fade = 1f - (ry - hz) / down
                        val wob = kotlin.math.sin(sgi * 1.3f + ph * 0.5f + i * 0.7f) * px * 0.9f
                        drawRect(colAt(i, sgi / segU.toFloat()).copy(alpha = 0.42f * fade), Offset(x + wob, ry), Size(barW, seg * 0.5f))
                    }
                }
                1 -> { // solid bars with a bright cap
                    val h = v * (size.height - px)
                    val c = colAt(i, v)
                    glowRect(c, Offset(x, size.height - h), Size(barW, h), px * 0.7f)
                    drawRect(Brush.verticalGradient(listOf(c, c.copy(alpha = 0.35f)), size.height - h, size.height), Offset(x, size.height - h), Size(barW, h))
                    drawRect(Color.White.copy(alpha = 0.8f), Offset(x, size.height - h), Size(barW, px * 0.6f))
                }
                3 -> { // VFD DECK: the vacuum-fluorescent display of a VCR's front panel. Thin glowing segments
                    // behind the blue-grey mesh, every unlit segment faintly visible, the last ones orange, a hold bar
                    val step = seg + segGap
                    val lit = (v * segs).toInt()
                    val pk = (peak[i] * segs).toInt().coerceIn(0, segs - 1)
                    val sw = barW * 0.86f; val sx = x + (barW - sw) / 2
                    for (k in 0 until segs) {
                        val y = size.height - (k + 1) * step
                        val c = colAt(i, k / segs.toFloat())
                        when {
                            k < lit -> {
                                drawRect(c.copy(alpha = 0.16f), Offset(sx - px * 0.7f, y - px * 0.5f), Size(sw + px * 1.4f, seg + px))
                                drawRect(c, Offset(sx, y + seg * 0.12f), Size(sw, seg * 0.76f))
                                drawRect(Color.White.copy(alpha = 0.35f), Offset(sx, y + seg * 0.12f), Size(sw, seg * 0.22f))
                            }
                            k == pk && peak[i] > 0.02f -> drawRect(Color(0xFFFF7A2E), Offset(sx, y + seg * 0.2f), Size(sw, seg * 0.6f))
                            else -> drawRect(c.copy(alpha = 0.07f), Offset(sx, y + seg * 0.12f), Size(sw, seg * 0.76f))
                        }
                    }
                    // the panel's fine mesh over the whole display (once)
                    if (i == n - 1) {
                        var mx = 0f
                        while (mx < size.width) { drawRect(Color.Black.copy(alpha = 0.18f), Offset(mx, 0f), Size(px * 0.35f, size.height)); mx += px * 1.5f }
                    }
                }
                else -> { // segments
                    val half = segs
                    val exact = v * half
                    val lit = exact.toInt()
                    val frac = exact - lit
                    for (sgi in 0 until half) {
                        val on = sgi < lit
                        val a = when { on -> 1f; sgi == lit && !hard -> 0.10f + 0.9f * frac; else -> 0.10f }
                        val c = colAt(i, sgi / half.toFloat())
                        val y = size.height - (sgi + 1) * (seg + segGap)
                        if (on) { glowRect(c, Offset(x, y), Size(barW, seg), px * 0.7f); drawRect(sh, Offset(x + px * 0.6f, y + px * 0.6f), Size(barW, seg)) }
                        drawRect(c.copy(alpha = a), Offset(x, y), Size(barW, seg))
                    }
                }
            }
            if (style != 2 && style != 3 && style != 6 && peak[i] > 0.02f) {
                val ps = (peak[i] * segs).roundToInt().coerceIn(0, segs - 1)
                drawRect(if (palette == 0) o.fg else Color.White, Offset(x, size.height - (ps + 1) * (seg + segGap)), Size(barW, seg * 0.6f))
            }
        }
    }
    themeTag?.let { t -> Box(Modifier.align(Alignment.Center).background(o.fg).padding(pxDp(1f))) { VhsText(t, invert = true) } }
    }
}

/** Visualizer looks: name, shape (0 segments, 2 mirror, 3 dots, 4 tape blocks, 5 scanlines, 6 scope, 7 wireframe), colours (0 OSD, 5 VU, 6 RGB, 7 amber, 8 neon). */
private val VIZ_THEMES = listOf(
    Triple("CLASSIC", 0, 0), Triple("VU METER", 0, 5), Triple("TAPE BLOCKS", 4, 0), Triple("RGB PHOSPHOR", 0, 6),
    Triple("SCANLINES", 5, 0), Triple("SCOPE", 6, 7), Triple("VFD DECK", 3, 8), Triple("MIRROR", 2, 0), Triple("WIREFRAME", 7, 0),
)
private fun lerpC(a: Color, b: Color, t: Float): Color { val k = t.coerceIn(0f, 1f); return Color(a.red + (b.red - a.red) * k, a.green + (b.green - a.green) * k, a.blue + (b.blue - a.blue) * k) }

// ---------------------------------------------------------------- SETUP

/** a CRT / reception style: snow, glitch, scanlines, artifact lines, curve, background blur, UI blur, chroma */
private class CrtStyle(val name: String, vararg val v: Float) {
    private fun cur() = floatArrayOf(OsPrefs.vhsSnow, OsPrefs.vhsGlitch, OsPrefs.vhsScan, OsPrefs.vhsLines, OsPrefs.vhsCurve, OsPrefs.vhsBlur, OsPrefs.vhsUiBlur, OsPrefs.vhsChroma)
    fun matches() = cur().withIndex().all { (i, x) -> kotlin.math.abs(x - v[i]) < 0.006f }
    fun apply() {
        OsPrefs.setVhsSnow(v[0]); OsPrefs.setVhsGlitch(v[1]); OsPrefs.setVhsScan(v[2]); OsPrefs.setVhsLines(v[3])
        OsPrefs.setVhsCurve(v[4]); OsPrefs.setVhsBlur(v[5]); OsPrefs.setVhsUiBlur(v[6]); OsPrefs.setVhsChroma(v[7])
    }
}
//                                    SNOW  GLTCH SCAN  LINES CURVE BGBLR UIBLR CHROMA
/** test hook: CRT style N (0 = OFF) */
internal fun vhsApplyCrtStyle(i: Int) { CRT_STYLES.getOrNull(i)?.apply() }

private val CRT_STYLES = listOf(
    CrtStyle("OFF",             0f,   0f,   0f,   0f,   0f,   0f,   0f,   0f),
    CrtStyle("FLAT TUBE",       0f,   0f,   0.30f, 0.10f, 0.05f, 0.10f, 0f,   0.05f), // flat, crisp, a 90s living-room set
    CrtStyle("STUDIO MONITOR",  0.05f, 0f,  0.45f, 0.15f, 0.15f, 0.15f, 0f,   0.10f), // clean broadcast picture
    CrtStyle("CABLE TV",        0.15f, 0.15f, 0.60f, 0.40f, 0.30f, 0.30f, 0.05f, 0.30f),
    CrtStyle("RENTAL TAPE",     0.45f, 0.55f, 0.70f, 0.65f, 0.30f, 0.35f, 0.15f, 0.45f), // a well-worn video store tape
    CrtStyle("RABBIT EARS",     0.85f, 0.35f, 0.60f, 0.50f, 0.35f, 0.50f, 0.20f, 0.35f), // weak antenna reception
    CrtStyle("ARCADE CRT",      0.05f, 0.05f, 1.00f, 0.25f, 0.60f, 0.40f, 0.10f, 0.55f), // heavy scanlines, round tube
    CrtStyle("BROKEN VCR",      0.60f, 1.00f, 0.70f, 1.00f, 0.40f, 0.60f, 0.35f, 0.80f), // tracking gone, heads dirty
)

/** a ready-made look: background, text, 3D wallpaper (-1 none), visualizer look, tape wear, colour fringe */
private class VhsTheme(val name: String, val bg: Long, val fg: Long, val wall: Int, val viz: Int, val glitch: Float, val chroma: Float, val grad: Int = -1, val mix: Boolean = false) {
    fun matches() = OsPrefs.vhsBg == bg && OsPrefs.vhsFg == fg && OsPrefs.vhsWall == wall && OsPrefs.vhsVizTheme == viz && OsPrefs.vhsGrad == grad && OsPrefs.vhsFgMix == mix
    fun apply() {
        OsPrefs.setVhsColors(bg, fg); OsPrefs.setVhsWall(wall); OsPrefs.setVhsVizTheme(viz); OsPrefs.setVhsGrad(grad); OsPrefs.setVhsFgMix(mix)

        OsPrefs.setVhsGlitch(glitch); OsPrefs.setVhsChroma(chroma); OsPrefs.setVhsCrt(true)
    }
}
private val VHS_THEMES = listOf(
    VhsTheme("VCR BLUE", 0xFF1A1AD0, 0xFFFFFFFF, -1, 0, 0.5f, 0.3f),
    VhsTheme("MALLSOFT", 0xFFFFB3DE, 0xFF3A1F5D, 2, 3, 0.3f, 0.3f, grad = 0),
    VhsTheme("PLAZA MIX", 0xFF3A1F5D, 0xFFFFB3DE, -1, 6, 0.35f, 0.4f, grad = 2, mix = true),
    VhsTheme("VAPORWAVE", 0xFFFF6EC7, 0xFFFFFFFF, -1, 8, 0.45f, 0.5f, grad = 1),
    VhsTheme("CHANNEL SURF", 0xFF050505, 0xFF39FF14, 1, 4, 0.6f, 0.5f),
    VhsTheme("TAPE TOWER", 0xFF3B0A6E, 0xFFFF6EC7, 0, 6, 0.4f, 0.5f),
    VhsTheme("MIAMI", 0xFF2DE2E6, 0xFFFFFFFF, -1, 2, 0.4f, 0.45f, grad = 3),
    VhsTheme("SEAPUNK", 0xFF00FFD1, 0xFF3A1F5D, -1, 3, 0.4f, 0.4f, grad = 5),
    VhsTheme("GREEN SCREEN", 0xFF050505, 0xFF39FF14, -1, 4, 0.3f, 0.2f),
    VhsTheme("AMBER TERM", 0xFF050505, 0xFFFFB000, -1, 5, 0.35f, 0.15f),
)

private fun cycle(list: List<Pair<String, Long>>, cur: Long): Long {
    val i = list.indexOfFirst { it.second == cur }
    return list[(i + 1).mod(list.size)].second
}

private fun targetName(t: String): String = when {
    t.isEmpty() -> "---"
    t == "vhs:setup" -> "SETUP"
    t == "vhs:chat" -> if (com.reverender.r95.BuildConfig.PLAY) "VHS CHAT" else "WHATSAPP"
    t == "vhs:eq" -> "EQ"
    t == "vhs:playlist" -> "PLAYLIST"
    t == "vhs:load" -> "LOAD"
    t == "vhs:web" -> "VHS WEB"
    t.startsWith("pkg:") -> Launcher.find(t.removePrefix("pkg:"))?.label?.uppercase()?.take(12) ?: "APP"
    else -> t.uppercase()
}

@Composable
private fun VhsSetup(fit: FitState, availH: Float, onEnd: () -> Unit, onChoose: (String) -> Unit) {
    val ctx = LocalContext.current
    val state = LocalAppState.current
    val o = LocalOsd.current
    var cursor by remember { mutableIntStateOf(0) }
    // which header line is being typed (1 / 2), 0 = none
    var editTitle by remember { mutableIntStateOf(0) }
    val openKb = LocalOpenKb.current
    val blink = rememberBlink()
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val pickPic = androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.OpenDocument()) { uri: android.net.Uri? ->
        if (uri != null) scope.launch {
            val ok = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { VhsBgPicture.save(ctx, uri) }
            if (ok) OsPrefs.setVhsPic(OsPrefs.vhsPic + 1)
        }
    }
    val items: List<Pair<String, () -> Unit>> = listOf(
        // ready-made looks (colours, 3D wallpaper, visualizer, tape wear): tap for the next one, it loops
        "THEME " + (VHS_THEMES.firstOrNull { it.matches() }?.name ?: "CUSTOM") to {
            val cur = VHS_THEMES.indexOfFirst { it.matches() }
            VHS_THEMES[(cur + 1).mod(VHS_THEMES.size)].apply()
        },
        // plain colours, then the multicolour (vaporwave) ones
        "BACK  " + (VHS_GRADS.getOrNull(OsPrefs.vhsGrad)?.first ?: VHS_COLORS.firstOrNull { it.second == OsPrefs.vhsBg }?.first ?: "CUSTOM") to {
            val n = VHS_COLORS.size + VHS_GRADS.size
            val cur = if (OsPrefs.vhsGrad in VHS_GRADS.indices) VHS_COLORS.size + OsPrefs.vhsGrad else VHS_COLORS.indexOfFirst { it.second == OsPrefs.vhsBg }
            val next = (cur + 1).mod(n)
            if (next < VHS_COLORS.size) { OsPrefs.setVhsGrad(-1); OsPrefs.setVhsColors(VHS_COLORS[next].second, OsPrefs.vhsFg) }
            else { val gi = next - VHS_COLORS.size; OsPrefs.setVhsGrad(gi); OsPrefs.setVhsColors(VHS_GRADS[gi].second.first(), OsPrefs.vhsFg) }
        },
        // single colours, then PASTEL MIX (every letter its own pastel)
        "TEXT  " + (if (OsPrefs.vhsFgMix) "PASTEL MIX" else VHS_TEXT.firstOrNull { it.second == OsPrefs.vhsFg }?.first ?: "CUSTOM") to {
            val cur = if (OsPrefs.vhsFgMix) VHS_TEXT.size else VHS_TEXT.indexOfFirst { it.second == OsPrefs.vhsFg }
            val next = (cur + 1).mod(VHS_TEXT.size + 1)
            if (next == VHS_TEXT.size) { OsPrefs.setVhsFgMix(true); OsPrefs.setVhsColors(OsPrefs.vhsBg, 0xFFFFB3DE) }
            else { OsPrefs.setVhsFgMix(false); OsPrefs.setVhsColors(OsPrefs.vhsBg, VHS_TEXT[next].second) }
        },
        // the help bars: always there, or sliding away after 2 s (swipe up from the bottom edge to bring them back)
        "HELP  " + (if (OsPrefs.vhsHelpAuto) "AUTO HIDE" else "FIXED") to { OsPrefs.setVhsHelpAuto(!OsPrefs.vhsHelpAuto) },
        // the help tour from first boot, again
        "TOUR  SHOW" to { VhsTour.open = true },
    ) + (if (com.reverender.r95.BuildConfig.PLAY) listOf(
        // Play edition: the open-source licences and where the source code is
        "LICENSES" to { runCatching { ctx.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://reverender.com/vhs/licenses")).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) } },
    ) else emptyList()) + listOf(
        // the CRT look: set with the SNOW / GLITCH / LINES / CURVE sliders under this row; tapping it switches
        // the first three all off, or back to their defaults
        // CRT: TV / reception styles, each a setting of every slider below (except DIM); moving a slider = CUSTOM
        "CRT   " + (CRT_STYLES.firstOrNull { it.matches() }?.name ?: "CUSTOM") to {
            val i = CRT_STYLES.indexOfFirst { it.matches() }
            CRT_STYLES[(i + 1).mod(CRT_STYLES.size)].apply()
        },
        "CLOCK " + (if (OsPrefs.vhs24h) "24H" else "12H") to { OsPrefs.setVhs24h(!OsPrefs.vhs24h) },
        "BLOOM " + (if (OsPrefs.vhsBloom) "ON" else "OFF") to { OsPrefs.setVhsBloom(!OsPrefs.vhsBloom) },
        (if (OsPrefs.vhsPic > 0) "PICTURE CHANGE" else "PICTURE CHOOSE") to { pickPic.launch(arrayOf("image/*")) },
        // 3D backgrounds: tap for the next one, after the last back to none
        "WALL " + (VhsWalls.names.getOrNull(OsPrefs.vhsWall)?.take(16) ?: "NONE") to {
            OsPrefs.setVhsWall(if (OsPrefs.vhsWall + 1 >= VhsWalls.names.size) -1 else OsPrefs.vhsWall + 1)
        },
        // wide screens: what sits next to the home menu
        "WIDE  " + OsPrefs.vhsWide to {
            val opts = listOf("LOAD", "EQ", "PLAYLIST", "SETUP", "APPS", "OFF")
            OsPrefs.setVhsWide(opts[(opts.indexOf(OsPrefs.vhsWide) + 1).mod(opts.size)])
        },
        // lettering styles of different VCR makers: tap for the next one, it loops
        // the apps drawer's look
        "DRAWER " + listOf("FLOAT LIST", "TAPE SHELF", "TELETEXT", "ICON GRID")[OsPrefs.vhsDrawer.coerceIn(0, 3)] to { OsPrefs.setVhsDrawer((OsPrefs.vhsDrawer + 1) % 4) },
        "FONT  " + VHS_FONTS[OsPrefs.vhsFont.coerceIn(0, VHS_FONTS.size - 1)] to { OsPrefs.setVhsFont((OsPrefs.vhsFont + 1) % VHS_FONTS.size) },
        ("TITLE1 " + OsPrefs.vhsTitle1.take(14) + if (editTitle == 1 && blink) "_" else "") to { editTitle = 1 },
        // the news ticker's feed: tap and type an RSS address (empty = no ticker)
        ("RSS    " + (if (editTitle == 2) vhsSafe(OsPrefs.vhsRss).takeLast(13) + (if (blink) "_" else "")
            else OsPrefs.vhsRss.trim().ifEmpty { "OFF" }.let { u -> if (u == "OFF") u else vhsSafe(runCatching { java.net.URI(if (u.startsWith("http")) u else "https://$u").host }.getOrNull() ?: u).removePrefix("WWW.").removePrefix("FEEDS.").take(14) })) to { editTitle = 2 },
        // VHS WEB's homepage: tap and type its address
        ("WEBHOME " + (if (editTitle == 3) vhsSafe(OsPrefs.vhsWebHome).takeLast(12) + (if (blink) "_" else "")
            else vhsSafe(runCatching { java.net.URI(OsPrefs.vhsWebHome).host }.getOrNull() ?: OsPrefs.vhsWebHome).removePrefix("WWW.").take(13))) to { editTitle = 3 },
        "SWIPE◀ " + targetName(OsPrefs.vhsSwipe["left"].orEmpty()) to { onChoose("left") },
        "SWIPE▶ " + targetName(OsPrefs.vhsSwipe["right"].orEmpty()) to { onChoose("right") },
        "PIN APPS" to { onChoose("pin") },
    ) + (if (!com.reverender.r95.BuildConfig.NOTIF) emptyList() else listOf(
        // VHS+ APK: the notification area needs notification access (tap to open the phone's setting)
        ("NOTIFS " + if (VhsNotifs.connected) "ON" else "GRANT ACCESS") to { if (com.reverender.r95.BuildConfig.PLAY && !VhsNotifs.connected) VhsNotifDisclosure.open = true else com.reverender.r95.os.RetroAlerts.openListenerSettings(ctx) },
    )) + listOf(
        "HOME LAUNCHER" to { Launcher.setHomeMode(ctx, true) },
    ) + (if (com.reverender.r95.BuildConfig.VHS_ONLY) emptyList() else listOf(
        // the VHS-only APK has no desktop to switch to
        "STYLE WIN95" to { state.setOsStyle("win95") },
        "STYLE LONGHORN" to { state.setOsStyle("longhorn") },
    ))
    // typing a header line: the phone keyboard opens on it, enter finishes
    if (editTitle != 0) {
        // (an RSS address keeps its exact letters; a title goes through the OSD font)
        androidx.compose.runtime.key(editTitle) { RegisterKb(VhsKb(
            { when (editTitle) { 1 -> OsPrefs.vhsTitle1; 3 -> OsPrefs.vhsWebHome; else -> OsPrefs.vhsRss } },
            { t -> when (editTitle) { 1 -> OsPrefs.setVhsTitles(t.take(20), OsPrefs.vhsTitle2); 3 -> OsPrefs.setVhsWebHome(t.trim().take(400)); else -> OsPrefs.setVhsRss(t.trim().take(400)) } },
            raw = editTitle != 1, label = when (editTitle) { 1 -> "TITLE1"; 3 -> "WEB HOMEPAGE"; else -> "RSS FEED ADDRESS" },
        ) { editTitle = 0 }) }
        LaunchedEffect(editTitle) { delay(50); openKb() }
    }
    FitPage(fit, availH, 1.6f, hint = {
        Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { onEnd() } }) {
            Column {
                VhsText("TAP ITEM TO CHANGE", invert = true)
                VhsText("TAP HERE TO END", invert = true)
            }
        }
    }, body = {
        VhsHeader("VHS/VCR SETUP", OsPrefs.vhsTitle1.ifBlank { "REVERENDER 95" })
        items.forEachIndexed { i, (label, act) ->
            Row(Modifier.fillMaxWidth().pointerInput(label) { detectTapGestures { Haptics.tick(); cursor = i; act() } }, verticalAlignment = Alignment.CenterVertically) {
                VhsText(if (cursor == i) "▶" else " ")
                VhsText(label, invert = cursor == i)
            }
            if (label.startsWith("PICTURE") && OsPrefs.vhsPic > 0 && OsPrefs.vhsWall < 0) {
                // the picture: how much of the background colour lies over it, and back to the plain colour
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    VhsText(" ")
                    VhsText("DIM")
                    Spacer(Modifier.width(pxDp(2f)))
                    Box(Modifier.weight(1f)) { VhsBar(OsPrefs.vhsPicDim / 0.9f, slider = true) { OsPrefs.setVhsPicDim(it * 0.9f) } }
                    Spacer(Modifier.width(pxDp(2f)))
                    VhsText((OsPrefs.vhsPicDim / 0.9f * 100).roundToInt().toString().padStart(3))
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    VhsText(" ")
                    VhsKey("RESET TO SOLID COLOUR", boxed = true) { VhsBgPicture.clear(ctx); OsPrefs.setVhsPic(0) }
                }
            }
            // RSS: the news ticker's speed
            if (label.startsWith("RSS ")) Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                VhsText(" ")
                VhsText("SPEED ")
                Spacer(Modifier.width(pxDp(2f)))
                Box(Modifier.weight(1f)) { VhsBar(OsPrefs.vhsRssSpeed, slider = true) { OsPrefs.setVhsRssSpeed(it) } }
                Spacer(Modifier.width(pxDp(2f)))
                VhsText((OsPrefs.vhsRssSpeed * 100).roundToInt().toString().padStart(3))
            }
            if (label.startsWith("CRT ")) {
                // SNOW: analog snow, loose specks, tape drop-outs
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    VhsText(" ")
                    VhsText("SNOW  ")
                    Spacer(Modifier.width(pxDp(2f)))
                    Box(Modifier.weight(1f)) { VhsBar(OsPrefs.vhsSnow, slider = true) { OsPrefs.setVhsSnow(it) } }
                    Spacer(Modifier.width(pxDp(2f)))
                    VhsText((OsPrefs.vhsSnow * 100).roundToInt().toString().padStart(3))
                }
                // GLITCH: tape glitches (tearing, tracking noise bands, colour slip, picture jumps): how often and how hard
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    VhsText(" ")
                    VhsText("GLITCH")
                    Spacer(Modifier.width(pxDp(2f)))
                    Box(Modifier.weight(1f)) { VhsBar(OsPrefs.vhsGlitch, slider = true) { OsPrefs.setVhsGlitch(it) } }
                    Spacer(Modifier.width(pxDp(2f)))
                    VhsText((OsPrefs.vhsGlitch * 100).roundToInt().toString().padStart(3))
                }
                // SCAN: the scanlines
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    VhsText(" ")
                    VhsText("SCAN  ")
                    Spacer(Modifier.width(pxDp(2f)))
                    Box(Modifier.weight(1f)) { VhsBar(OsPrefs.vhsScan, slider = true) { OsPrefs.setVhsScan(it) } }
                    Spacer(Modifier.width(pxDp(2f)))
                    VhsText((OsPrefs.vhsScan * 100).roundToInt().toString().padStart(3))
                }
                // LINES: artifact lines: the rolling bright band, the head-switching strip at the bottom
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    VhsText(" ")
                    VhsText("LINES ")
                    Spacer(Modifier.width(pxDp(2f)))
                    Box(Modifier.weight(1f)) { VhsBar(OsPrefs.vhsLines, slider = true) { OsPrefs.setVhsLines(it) } }
                    Spacer(Modifier.width(pxDp(2f)))
                    VhsText((OsPrefs.vhsLines * 100).roundToInt().toString().padStart(3))
                }
                // CURVE: how much the tube bends towards the corners (0 = flat screen)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    VhsText(" ")
                    VhsText("CURVE ")
                    Spacer(Modifier.width(pxDp(2f)))
                    Box(Modifier.weight(1f)) { VhsBar(OsPrefs.vhsCurve, slider = true) { OsPrefs.setVhsCurve(it) } }
                    Spacer(Modifier.width(pxDp(2f)))
                    VhsText((OsPrefs.vhsCurve * 100).roundToInt().toString().padStart(3))
                }
                // BLUR: CRT motion blur (phosphor afterglow on the wallpaper and the visualizer)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    VhsText(" ")
                    VhsText("BGBLUR")
                    Spacer(Modifier.width(pxDp(2f)))
                    Box(Modifier.weight(1f)) { VhsBar(OsPrefs.vhsBlur, slider = true) { OsPrefs.setVhsBlur(it) } }
                    Spacer(Modifier.width(pxDp(2f)))
                    VhsText((OsPrefs.vhsBlur * 100).roundToInt().toString().padStart(3))
                }
                // UI BLUR: the same CRT afterglow on the whole interface (off unless you turn it up)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    VhsText(" ")
                    VhsText("UIBLUR")
                    Spacer(Modifier.width(pxDp(2f)))
                    Box(Modifier.weight(1f)) { VhsBar(OsPrefs.vhsUiBlur, slider = true) { OsPrefs.setVhsUiBlur(it) } }
                    Spacer(Modifier.width(pxDp(2f)))
                    VhsText((OsPrefs.vhsUiBlur * 100).roundToInt().toString().padStart(3))
                }
                // CHROMA: chromatic aberration (red / cyan colour fringes)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    VhsText(" ")
                    VhsText("CHROMA")
                    Spacer(Modifier.width(pxDp(2f)))
                    Box(Modifier.weight(1f)) { VhsBar(OsPrefs.vhsChroma, slider = true) { OsPrefs.setVhsChroma(it) } }
                    Spacer(Modifier.width(pxDp(2f)))
                    VhsText((OsPrefs.vhsChroma * 100).roundToInt().toString().padStart(3))
                }
            }
        }
    })
}

/** Pick a swipe target (or pin apps): SETUP, nothing, or any phone app. */
@Composable
private fun VhsChoose(dir: String, onDone: () -> Unit) {
    val o = LocalOsd.current
    val pin = dir == "pin"
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = pxDp(2f), vertical = pxDp(2f)),
        verticalArrangement = Arrangement.spacedBy(pxDp(1.4f)),
    ) {
        VhsHeader(if (pin) "PIN APPS" else "SWIPE " + mapOf("up" to "▲", "down" to "▼", "left" to "◀", "right" to "▶")[dir], if (pin) "TAP TO PIN / UNPIN" else "CHOOSE A PROGRAM")
        if (!pin) {
            (listOf("" to "---  NOTHING", "vhs:setup" to "SETUP MENU", "vhs:load" to "LOAD (VIDEO/GAME/MUSIC)", "vhs:eq" to "EQ", "vhs:playlist" to "PLAYLIST", "vhs:web" to "VHS WEB BROWSER") + (if (com.reverender.r95.BuildConfig.NOTIF) listOf("vhs:chat" to "WHATSAPP (VHS CHAT)") else emptyList())).forEach { (k, label) ->
                Box(Modifier.pointerInput(k) { detectTapGestures { OsPrefs.setVhsSwipe(dir, k); onDone() } }) {
                    VhsText(label, invert = OsPrefs.vhsSwipe[dir].orEmpty() == k)
                }
            }
        }
        Launcher.apps.forEach { a ->
            val flat = a.component.flattenToString()
            val on = if (pin) flat in OsPrefs.pinned else OsPrefs.vhsSwipe[dir] == "pkg:$flat"
            Box(Modifier.pointerInput(flat) { detectTapGestures { Haptics.tick(); if (pin) OsPrefs.togglePin(flat) else { OsPrefs.setVhsSwipe(dir, "pkg:$flat"); onDone() } } }) {
                VhsText((if (pin && on) "★" else " ") + a.label.uppercase().take(20), invert = !pin && on)
            }
        }
        Spacer(Modifier.height(pxDp(2f)))
        Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { onDone() } }) {
            VhsText("TAP HERE TO RETURN", invert = true)
        }
    }
}


/** The style picker's preview tile. */
@Composable
fun VhsThumb(modifier: Modifier) {
    val px = with(LocalDensity.current) { 1.3.dp.toPx() }
    CompositionLocalProvider(LocalOsd provides Osd(Color.White, Color(0xFF1A1AD0), px)) {
        Column(modifier.background(Color(0xFF1A1AD0)).padding(3.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            VhsText("VHS/VCR MENU")
            VhsText("ABCDEFGHIJ")
            VhsText("▶PLAY 11:35")
            VhsBar(0.6f)
        }
    }
}

/** The VHS shell's own background picture: copied into the app (downscaled), so it survives the original being moved. */
internal object VhsBgPicture {
    private fun file(ctx: android.content.Context) = java.io.File(ctx.filesDir, "vhs_bg.jpg")
    fun save(ctx: android.content.Context, uri: android.net.Uri): Boolean = runCatching {
        val cr = ctx.contentResolver
        val o = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
        cr.openInputStream(uri)!!.use { android.graphics.BitmapFactory.decodeStream(it, null, o) }
        val dm = ctx.resources.displayMetrics
        val target = maxOf(dm.widthPixels, dm.heightPixels).coerceAtLeast(720)
        var sample = 1
        while (maxOf(o.outWidth, o.outHeight) / (sample * 2) >= target) sample *= 2
        // Android 9+: ImageDecoder also turns the photo the right way up
        val out = if (android.os.Build.VERSION.SDK_INT >= 28) {
            android.graphics.ImageDecoder.decodeBitmap(android.graphics.ImageDecoder.createSource(cr, uri)) { dec: android.graphics.ImageDecoder, _: android.graphics.ImageDecoder.ImageInfo, _: android.graphics.ImageDecoder.Source -> dec.setTargetSampleSize(sample); dec.allocator = android.graphics.ImageDecoder.ALLOCATOR_SOFTWARE }
        } else cr.openInputStream(uri)!!.use { android.graphics.BitmapFactory.decodeStream(it, null, android.graphics.BitmapFactory.Options().apply { inSampleSize = sample }) }!!
        file(ctx).outputStream().use { out.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, it) }
        true
    }.getOrElse { android.util.Log.w("R95", "vhs picture", it); false }
    fun load(ctx: android.content.Context): androidx.compose.ui.graphics.ImageBitmap? = runCatching {
        android.graphics.BitmapFactory.decodeFile(file(ctx).path)?.asImageBitmap()
    }.getOrNull()
    fun clear(ctx: android.content.Context) { runCatching { file(ctx).delete() } }
}

/**
 * PLAYLIST: your own songs from the phone, saved in the launcher. ADD picks files (the launcher keeps
 * permission to read them), tap a song to play from there, ▲ ▼ move it, X takes it out; CLEAR empties the list.
 */
@Composable
private fun VhsPlaylist(onExit: () -> Unit) {
    val ctx = LocalContext.current
    val o = LocalOsd.current
    val list = OsPrefs.vhsPlaylist
    var confirmClear by remember { mutableStateOf(false) }
    LaunchedEffect(confirmClear) { if (confirmClear) { delay(3000); confirmClear = false } }
    val add = androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.OpenMultipleDocuments()) { uris: List<android.net.Uri> ->
        uris.forEach { u ->
            runCatching { ctx.contentResolver.takePersistableUriPermission(u, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            val name = runCatching { ctx.contentResolver.query(u, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c -> if (c.moveToFirst()) c.getString(0) else null } }.getOrNull()
                ?: (u.lastPathSegment ?: "SONG")
            list.add(name.substringBeforeLast('.') to u.toString())
        }
        if (uris.isNotEmpty()) { OsPrefs.savePlaylist(); Haptics.clunk("play") }
    }
    fun playFrom(i: Int) {
        if (i !in list.indices) return
        Haptics.clunk("play")
        AmpEngine.customAudioFiles.clear()
        AmpEngine.customAudioFiles.addAll(list.map { (n, u) -> AmpEngine.CustomFile(n, u) })
        AmpEngine.playCustomLocalFile(i)
    }
    // keep the engine's list in step while this playlist is the one playing
    fun changed() {
        OsPrefs.savePlaylist()
        val playing = AmpEngine.currentFileIndex
        if (playing >= 0 && AmpEngine.customAudioFiles.size != list.size) {
            AmpEngine.customAudioFiles.clear(); AmpEngine.customAudioFiles.addAll(list.map { (n, u) -> AmpEngine.CustomFile(n, u) })
        }
    }
    val nowIdx = AmpEngine.currentFileIndex
    Column(Modifier.fillMaxSize().padding(horizontal = pxDp(2f), vertical = pxDp(2f)), verticalArrangement = Arrangement.spacedBy(pxDp(1.4f))) {
        VhsHeader("PLAYLIST", "${list.size} SONG" + (if (list.size == 1) "" else "S"))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(2.5f)), verticalAlignment = Alignment.CenterVertically) {
            VhsKey("ADD", boxed = true) { add.launch(arrayOf("audio/*")) }
            VhsKey("PLAY", boxed = true) { playFrom(if (AmpEngine.shuffle && list.size > 1) list.indices.random() else 0) }
            Spacer(Modifier.weight(1f))
            VhsKey(if (confirmClear) "SURE?" else "CLEAR", selected = confirmClear, boxed = true) {
                if (!confirmClear) confirmClear = true
                else { list.clear(); OsPrefs.savePlaylist(); confirmClear = false; Haptics.clunk("eject") }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(2.5f)), verticalAlignment = Alignment.CenterVertically) {
            VhsKey("SHUF", selected = AmpEngine.shuffle, boxed = true) { AmpEngine.shuffle = !AmpEngine.shuffle }
            VhsKey(when (AmpEngine.repeat) { 0 -> "RPT OFF"; 2 -> "RPT ONE"; else -> "RPT ALL" }, selected = AmpEngine.repeat != 0, boxed = true) { AmpEngine.repeat = (AmpEngine.repeat + 1) % 3 }
        }
        Column(
            Modifier.fillMaxWidth().weight(1f).then(if (list.isNotEmpty()) Modifier.verticalScroll(rememberScrollState()) else Modifier),
            verticalArrangement = Arrangement.spacedBy(pxDp(1.6f)),
        ) {
            if (list.isEmpty()) {
                VhsText("NO SONGS YET", color = o.fg.copy(alpha = 0.7f))
                VhsText("TAP ADD TO PICK FILES", color = o.fg.copy(alpha = 0.7f))
            }
            list.forEachIndexed { i, (name, _) ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(1.5f)), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f).pointerInput(i, name) { detectTapGestures { playFrom(i) } }) {
                        VhsText("%02d ".format(i + 1) + vhsSafe(name).trim().take(13), invert = i == nowIdx)
                    }
                    VhsKey("▲", boxed = true) { if (i > 0) { list.add(i - 1, list.removeAt(i)); changed() } }
                    VhsKey("▼", boxed = true) { if (i < list.size - 1) { list.add(i + 1, list.removeAt(i)); changed() } }
                    VhsKey("X", boxed = true) { list.removeAt(i); changed(); Haptics.tick() }
                }
            }
        }
        Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { Haptics.tick(); onExit() } }) {
            Column {
                VhsText("TAP A SONG TO PLAY IT", invert = true)
                VhsText("TAP HERE TO RETURN", invert = true)
            }
        }
    }
}

/**
 * The apps drawer: every phone app, A–Z, sliding in from the right. It follows the finger both ways (drag it
 * part way and let go; it settles open or closed), and the letter strip on the right jumps the list.
 */
@Composable
private fun VhsAppsDrawer(progress: () -> Float, widthPx: Float, shown: Boolean, onClose: () -> Unit) {
    val ctx = LocalContext.current
    val o = LocalOsd.current
    val apps = remember(Launcher.apps.size) { Launcher.apps.sortedBy { it.label.uppercase() } }
    val list = androidx.compose.foundation.lazy.rememberLazyListState()
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val letters = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    var bubble by remember { mutableStateOf<Char?>(null) }
    // hold an app: APP INFO / UNINSTALL
    var menuFor by remember { mutableStateOf<Launcher.PhoneApp?>(null) }
    fun hold(a: Launcher.PhoneApp) { Haptics.clunk("eject"); menuFor = a }
    LaunchedEffect(shown) { if (!shown) menuFor = null }
    fun keyOf(label: String): Char = label.trim().firstOrNull()?.uppercaseChar()?.takeIf { it in 'A'..'Z' } ?: '#'
    fun jump(c: Char) {
        val i = apps.indexOfFirst { keyOf(it.label) >= c }.let { if (it < 0) apps.lastIndex else it }
        if (i >= 0) scope.launch { list.scrollToItem(i) }
        if (bubble != c) Haptics.tick()
        bubble = c
    }
    val vt = remember { androidx.compose.ui.input.pointer.util.VelocityTracker() }
    // swiped sideways: the box follows the finger and fades; far or fast enough and it fades out, else it springs back
    var offX by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(shown) { if (!shown) offX = 0f }
    // a floating menu box over the dimmed screen; tap outside to close
    // closed: kept composed (opens faster) but laid out at zero size, so nothing in it can catch a touch or a
    // swipe meant for the home screen (its sideways-drag and letter strip did, and ate swipe left / right)
    Box((if (shown || progress() > 0.001f) Modifier.fillMaxSize() else Modifier.size(0.dp)).graphicsLayer { alpha = if (progress() < 0.001f) 0f else 1f }) {
    if (shown) Box(
        Modifier.fillMaxSize()
            .graphicsLayer { alpha = progress() }
            .background(Color.Black.copy(alpha = 0.55f))
            .pointerInput(Unit) { detectTapGestures { Haptics.tick(); onClose() } },
    )
    Box(
        Modifier.align(Alignment.Center).fillMaxWidth(0.86f).fillMaxHeight(0.84f)
            .graphicsLayer {
                val p = progress()
                // fades in where it stands; closed: parked far off screen, so the invisible box can't catch taps
                translationX = if (p < 0.001f) widthPx * 3f else offX
                alpha = (p * (1f - kotlin.math.abs(offX) / (widthPx * 0.7f))).coerceIn(0f, 1f)
                scaleX = 0.94f + 0.06f * p; scaleY = scaleX
            }
            .background(o.bg.copy(alpha = 0.93f))
            .drawBehind {
                val w = o.px * 0.6f
                glowRect(o.fg, Offset(w / 2, w / 2), Size(size.width - w, size.height - w), o.px * 1.2f, w)
                drawRect(o.fg, Offset(w / 2, w / 2), Size(size.width - w, size.height - w), style = Stroke(w))
            }
            .pointerInput(Unit) {
                // swipe left or right to close (or part way and back)
                fun settle(v: Float) {
                    if (kotlin.math.abs(offX) > widthPx * 0.22f || kotlin.math.abs(v) > 900f) { Haptics.tick(); onClose() }
                    else scope.launch { androidx.compose.animation.core.animate(offX, 0f, animationSpec = androidx.compose.animation.core.spring(stiffness = 600f, dampingRatio = 0.85f)) { x, _ -> offX = x } }
                }
                detectHorizontalDragGestures(
                    onDragStart = { vt.resetTracking() },
                    onDragEnd = { settle(vt.calculateVelocity().x) },
                    onDragCancel = { settle(0f) },
                ) { ch, dx -> vt.addPosition(ch.uptimeMillis, ch.position); ch.consume(); offX += dx }
            },
    ) {
        Column(Modifier.fillMaxSize().padding(horizontal = pxDp(2f), vertical = pxDp(2f)), verticalArrangement = Arrangement.spacedBy(pxDp(1.4f))) {
            fun open(a: Launcher.PhoneApp) { Haptics.tick(); Launcher.launch(ctx, a, fullScreen = true); onClose() }
            when (OsPrefs.vhsDrawer) {
                // ---- B: TAPE SHELF: every app a labelled VHS tape spine, two to a shelf
                1 -> {
                    VhsHeader("TAPE SHELF", "${apps.size} TAPES")
                    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                        androidx.compose.foundation.lazy.grid.GridCells.Fixed(2), Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(pxDp(2f)), verticalArrangement = Arrangement.spacedBy(pxDp(2f)),
                    ) {
                        items(apps.size, key = { apps[it].component.toString() + it }) { i ->
                            val a = apps[i]
                            val stripe = SPINES[(a.label.hashCode() and 0x7fffffff) % SPINES.size]
                            Box(Modifier.fillMaxWidth().height(pxDp(13f)).background(Color(0xFF141018)).drawBehind {
                                val px = o.px
                                drawRect(stripe, Offset.Zero, Size(px * 2.2f, size.height))                       // the coloured spine end
                                drawRect(Color(0xFFF2EBD9), Offset(px * 3.4f, px * 2f), Size(size.width - px * 4.4f, size.height - px * 4f)) // paper label
                                drawRect(Color.White.copy(alpha = 0.08f), Offset.Zero, Size(size.width, px * 0.7f))
                            }.pointerInput(a.component) { detectTapGestures(onLongPress = { hold(a) }) { open(a) } }, contentAlignment = Alignment.CenterStart) {
                                Box(Modifier.padding(start = pxDp(3.6f))) { VhsText(vhsSafe(a.label).trim().take(8), color = Color(0xFF1C1626), scale = 0.9f) }
                            }
                        }
                    }
                }
                // ---- C: TELETEXT GUIDE: page 100, numbered "channels", coloured jump keys at the bottom
                2 -> {
                    val now = remember { java.text.SimpleDateFormat("HH:mm", java.util.Locale.US).format(java.util.Date()) }
                    Box(Modifier.fillMaxWidth().background(Color(0xFF1A1AD0)).padding(pxDp(1f))) { VhsText("P100 APPS  $now", color = Color.White) }
                    Row { VhsText("CH ", color = Color(0xFFFFF04A)); VhsText("PROGRAMME", color = Color(0xFFFFF04A)) }
                    androidx.compose.foundation.lazy.LazyColumn(Modifier.fillMaxWidth().weight(1f), state = list, verticalArrangement = Arrangement.spacedBy(pxDp(1.6f))) {
                        items(apps.size, key = { apps[it].component.toString() + it }) { i ->
                            val a = apps[i]
                            Row(Modifier.fillMaxWidth().pointerInput(a.component) { detectTapGestures(onLongPress = { hold(a) }) { open(a) } }) {
                                VhsText("%02d ".format((i + 1) % 100), color = Color(0xFF5CF6FF))
                                VhsText(vhsSafe(a.label).trim().take(14), color = Color.White)
                            }
                        }
                    }
                    // FASTEXT keys: red A–F, green G–L, yellow M–R, blue S–Z
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(1.5f))) {
                        listOf(Triple("A-F", 'A', Color(0xFFE01010)), Triple("G-L", 'G', Color(0xFF10B010)), Triple("M-R", 'M', Color(0xFFE8E010)), Triple("S-Z", 'S', Color(0xFF1030E0))).forEach { (t, c, col) ->
                            Box(Modifier.weight(1f).background(col).padding(pxDp(0.8f)).pointerInput(c) { detectTapGestures { jump(c); bubble = null } }, contentAlignment = Alignment.Center) {
                                VhsText(t, color = if (c == 'M') Color.Black else Color.White)
                            }
                        }
                    }
                }
                // ---- E: PIXEL ICON GRID: the real app icons, pixelated and tinted in the text colour
                3 -> {
                    VhsHeader("APPS", "${apps.size} INSTALLED")
                    val tint = remember(o.fg) {
                        // brightness of the icon -> shades of the text colour
                        val r = o.fg.red; val g = o.fg.green; val b = o.fg.blue
                        androidx.compose.ui.graphics.ColorFilter.colorMatrix(androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
                            0.30f * r, 0.59f * r, 0.11f * r, 0f, 0f,
                            0.30f * g, 0.59f * g, 0.11f * g, 0f, 0f,
                            0.30f * b, 0.59f * b, 0.11f * b, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f,
                        )))
                    }
                    val small = remember { HashMap<String, androidx.compose.ui.graphics.ImageBitmap?>() }
                    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                        androidx.compose.foundation.lazy.grid.GridCells.Fixed(3), Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(pxDp(3f)),
                    ) {
                        items(apps.size, key = { apps[it].component.toString() + it }) { i ->
                            val a = apps[i]
                            val k = a.component.flattenToString()
                            val img = small.getOrPut(k) {
                                a.icon?.let { ic -> runCatching { android.graphics.Bitmap.createScaledBitmap(ic.asAndroidBitmap(), 18, 18, true).asImageBitmap() }.getOrNull() }
                            }
                            Column(Modifier.fillMaxWidth().pointerInput(a.component) { detectTapGestures(onLongPress = { hold(a) }) { open(a) } }, horizontalAlignment = Alignment.CenterHorizontally) {
                                Canvas(Modifier.size(pxDp(18f))) {
                                    if (img != null) drawImage(img, androidx.compose.ui.unit.IntOffset.Zero, androidx.compose.ui.unit.IntSize(img.width, img.height),
                                        dstSize = androidx.compose.ui.unit.IntSize(size.width.toInt(), size.height.toInt()), colorFilter = tint, filterQuality = androidx.compose.ui.graphics.FilterQuality.None)
                                    else drawRect(o.fg, style = Stroke(o.px))
                                }
                                Spacer(Modifier.height(pxDp(1f)))
                                VhsText(vhsSafe(a.label).trim().take(6))
                            }
                        }
                    }
                }
                // ---- A: the floating A–Z list with the letter strip
                else -> {
                    VhsHeader("APPS", "${apps.size} INSTALLED")
                Row(Modifier.fillMaxWidth().weight(1f)) {
                    androidx.compose.foundation.lazy.LazyColumn(Modifier.weight(1f).fillMaxSize(), state = list, verticalArrangement = Arrangement.spacedBy(pxDp(2f))) {
                        items(apps.size, key = { apps[it].component.toString() + it }, contentType = { 0 }) { i ->
                            val a = apps[i]
                            val first = i == 0 || keyOf(apps[i - 1].label) != keyOf(a.label)
                            Column {
                                if (first) VhsText(keyOf(a.label).toString(), invert = true)
                                Box(Modifier.fillMaxWidth().pointerInput(a.component) { detectTapGestures(onLongPress = { hold(a) }) { Haptics.tick(); Launcher.launch(ctx, a, fullScreen = true); onClose() } }) {
                                    VhsText(vhsSafe(a.label).trim().take(15))
                                }
                            }
                        }
                    }
                    // the letter strip: drawn to fit exactly between the header and the bottom bar; touch or slide to jump
                    val stripInk = o.fg
                    val sel = bubble
                    androidx.compose.foundation.Canvas(
                        Modifier.fillMaxHeight().width(pxDp(8f)).padding(start = pxDp(1f)).pointerInput(apps) {
                            awaitEachGesture {
                                val d0 = awaitFirstDown()
                                fun at(y: Float) = letters[((y / size.height) * letters.length).toInt().coerceIn(0, letters.length - 1)]
                                jump(at(d0.position.y)); d0.consume()
                                while (true) {
                                    val ev = awaitPointerEvent()
                                    val c = ev.changes.firstOrNull { it.id == d0.id } ?: break
                                    if (!c.pressed) break
                                    jump(at(c.position.y)); c.consume()
                                }
                                bubble = null
                            }
                        },
                    ) {
                        val cell = size.height / letters.length
                        val gpx = minOf(o.px, cell / 8.5f)
                        val gx = (size.width - 5 * gpx) / 2
                        letters.forEachIndexed { k, c ->
                            val y = k * cell + (cell - 7 * gpx) / 2
                            if (sel == c) {
                                drawRect(stripInk, Offset(gx - gpx, y - gpx), Size(7 * gpx, 9 * gpx))
                                drawGlyphs(c.toString(), gx, y, gpx, null, o.bg)
                            } else drawGlyphs(c.toString(), gx, y, gpx, null, stripInk)
                        }
                    }
                }
                }
            }
            Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { Haptics.tick(); onClose() } }) {
                VhsText("TAP OUTSIDE TO CLOSE", invert = true)
            }
        }
        // the big letter while jumping
        bubble?.let { c -> Box(Modifier.align(Alignment.Center).background(o.fg).padding(pxDp(3f))) { VhsText(c.toString(), scale = 4f, invert = true) } }
        // the hold menu for one app
        menuFor?.let { a ->
            Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.6f)).pointerInput(a) { detectTapGestures { menuFor = null } }, contentAlignment = Alignment.Center) {
                Column(
                    Modifier.fillMaxWidth(0.84f).background(o.bg).drawBehind { drawRect(o.fg, style = Stroke(o.px * 0.6f)) }.padding(pxDp(3f)).pointerInput(Unit) { detectTapGestures { } },
                    verticalArrangement = Arrangement.spacedBy(pxDp(3f)),
                ) {
                    VhsText(vhsSafe(a.label).trim().take(14), invert = true)
                    val pkg = a.component.packageName
                    VhsKey("APP INFO", boxed = true) {
                        runCatching { ctx.startActivity(android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:$pkg")).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) }
                        menuFor = null; onClose()
                    }
                    VhsKey("UNINSTALL", boxed = true) {
                        // the phone's own "uninstall this app?" question; failing that, its App info page
                        val ok = runCatching { ctx.startActivity(android.content.Intent(android.content.Intent.ACTION_DELETE, android.net.Uri.parse("package:$pkg")).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) }.isSuccess
                        if (!ok) runCatching { ctx.startActivity(android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:$pkg")).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) }
                        menuFor = null
                    }
                    VhsKey("CANCEL", boxed = true) { menuFor = null }
                }
            }
        }
    }
    }
}


/**
 * UI BLUR: CRT beam smear / tape ghosting for the whole interface. The interface's own GPU picture is drawn a
 * second time, slipped a few pixels to the right, softened (Android 12+) and faint — what a smeary tube or a
 * worn tape does to everything on screen, moving or not. No snapshots: costs about one extra replay of the
 * already-recorded picture.
 */
@Composable
private fun Modifier.uiMotionBlur(amount: Float): Modifier {
    if (amount < 0.02f) return this
    val main = androidx.compose.ui.graphics.rememberGraphicsLayer()
    val ghost = androidx.compose.ui.graphics.rememberGraphicsLayer()
    return this.drawWithContent {
        main.record { this@drawWithContent.drawContent() }
        ghost.record { drawLayer(main) }
        ghost.alpha = (0.14f + 0.36f * amount).coerceAtMost(0.5f)
        if (android.os.Build.VERSION.SDK_INT >= 31) ghost.renderEffect = androidx.compose.ui.graphics.BlurEffect(1.5f + 7f * amount, 0.5f, androidx.compose.ui.graphics.TileMode.Decal)
        drawLayer(main)
        translate(left = 1.5f + 5f * amount * density) { drawLayer(ghost) }
    }
}

/** hold the clock or the date: open the phone's clock app */
private fun openClockApp(ctx: android.content.Context) {
    Haptics.clunk("play")
    // 1) the phone's own clock app, found among the installed apps (same way the apps drawer starts apps;
    //    the alarm intents below are missing or point somewhere odd on several makers' phones)
    val known = listOf(
        "com.google.android.deskclock", "com.sec.android.app.clockpackage", "com.android.deskclock", "com.oneplus.deskclock",
        "com.coloros.alarmclock", "com.oplus.alarmclock", "com.huawei.deskclock", "com.hihonor.deskclock", "com.miui.clock",
        "com.motorola.timeweatherwidget", "com.asus.deskclock", "com.lge.clock", "com.sonymobile.alarm", "com.vivo.alarmclock",
        "com.transsion.deskclock", "com.nothing.deskclock",
    )
    val app = known.firstNotNullOfOrNull { pk -> Launcher.apps.firstOrNull { it.component.packageName == pk } }
        ?: Launcher.apps.firstOrNull { it.label.trim().equals("clock", true) || it.label.trim().equals("klok", true) }
        ?: Launcher.apps.firstOrNull { it.component.packageName.contains("clock", true) }
    if (app != null) { android.util.Log.i("r95", "clock: ${app.component}"); Launcher.launch(ctx, app, fullScreen = true); return }
    // 2) the launch intent of any known clock package (the app list may not be loaded yet)
    val pm = ctx.packageManager
    for (pk in known) {
        val li = runCatching { pm.getLaunchIntentForPackage(pk) }.getOrNull() ?: continue
        if (runCatching { ctx.startActivity(li.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) }.isSuccess) return
    }
    // 3) the standard ways
    val tries = listOf(
        android.content.Intent(android.provider.AlarmClock.ACTION_SHOW_ALARMS),
        android.content.Intent(android.content.Intent.ACTION_MAIN).addCategory("android.intent.category.APP_CLOCK"),
    )
    for (i in tries) if (runCatching { ctx.startActivity(i.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) }.isSuccess) return
    android.util.Log.w("r95", "clock: no clock app found")
}

/**
 * The noise over a menu switch: a burst of snow that clears, a bright tracking bar running down the picture
 * and a few torn lines, all gone after ~0.4 s.
 */
@Composable
private fun VhsSwitchFx(p: () -> Float) {
    val tile = remember {
        val r = java.util.Random(3)
        val b = android.graphics.Bitmap.createBitmap(96, 96, android.graphics.Bitmap.Config.ARGB_8888)
        for (y in 0 until 96) for (x in 0 until 96) { val v = r.nextInt(256); b.setPixel(x, y, android.graphics.Color.argb(255, v, v, v)) }
        android.graphics.BitmapShader(b, android.graphics.Shader.TileMode.REPEAT, android.graphics.Shader.TileMode.REPEAT)
    }
    val brush = remember { androidx.compose.ui.graphics.ShaderBrush(tile) }
    val m = remember { android.graphics.Matrix() }
    Canvas(Modifier.fillMaxSize()) {
        val sp = p()
        val e = 1f - sp
        val r = java.util.Random((sp * 90).toLong() * 7919)
        // snow, clearing as the picture comes in
        m.setScale(2.5f, 2.5f); m.postTranslate(r.nextFloat() * 240f, r.nextFloat() * 240f); tile.setLocalMatrix(m)
        drawRect(brush, alpha = (e * 1.25f - 0.2f).coerceIn(0f, 0.85f))
        // the tracking bar rolling down
        val y = size.height * (sp * 1.3f - 0.15f)
        val h = size.height * 0.07f
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.55f * e), Color.Transparent), y - h, y + h), Offset(0f, y - h), Size(size.width, h * 2))
        // torn lines
        repeat((14 * e).toInt()) {
            val ly = r.nextFloat() * size.height
            drawRect(Color.White.copy(alpha = 0.25f + 0.5f * r.nextFloat() * e), Offset(r.nextFloat() * size.width * 0.6f, ly), Size(size.width * (0.1f + r.nextFloat() * 0.5f), 1.5f + r.nextFloat() * 3f))
        }
        // the black between channels at the very start
        if (sp < 0.12f) drawRect(Color.Black.copy(alpha = 1f - sp / 0.12f))
    }
}

/** Set when the screen goes off with the launcher showing: the next return plays the CRT power-on. */
internal object VhsPowerOn {
    var pending by mutableStateOf(false)
    /** the phone was unlocked (the lock screen went away) since the screen went off */
    @Volatile var present = false
    /** the launcher's window has input focus (nothing over it: no lock screen, shade or other app) */
    @Volatile var focused = false
}

/** CRT switch-on: a dot stretches into a bright line, the line opens into the picture, a last white flash. */
@Composable
private fun VhsPowerOnOverlay(p: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height; val cy = h / 2
        val line = 3.dp.toPx()
        if (p < 0.28f) {
            drawRect(Color.Black)
            val k = (p / 0.28f).let { it * it * (3 - 2 * it) }
            val lw = maxOf(line, w * k)
            drawRect(Color.White.copy(alpha = 0.3f), Offset((w - lw) / 2, cy - line * 3), Size(lw, line * 6))
            drawRect(Color.White, Offset((w - lw) / 2, cy - line / 2), Size(lw, line))
        } else {
            val k = ((p - 0.28f) / 0.42f).coerceIn(0f, 1f)
            val e = 1f - (1f - k) * (1f - k) * (1f - k)
            val open = h / 2 * e
            // the picture in the gap washes from white to normal
            drawRect(Color.White.copy(alpha = (0.75f * (1f - (p - 0.28f) / 0.72f)).coerceIn(0f, 0.75f)), Offset(0f, cy - open), Size(w, open * 2))
            drawRect(Color.Black, Offset.Zero, Size(w, (cy - open).coerceAtLeast(0f)))
            drawRect(Color.Black, Offset(0f, cy + open), Size(w, (h - cy - open).coerceAtLeast(0f)))
            if (k < 1f) {
                val a = 0.9f * (1f - k)
                drawRect(Color.White.copy(alpha = a), Offset(0f, cy - open - line / 2), Size(w, line))
                drawRect(Color.White.copy(alpha = a), Offset(0f, cy + open - line / 2), Size(w, line))
            }
        }
    }
}

/** Steering another music app (Spotify, YouTube Music …) with the deck's keys, through the phone's media keys. */
internal object ExtMedia {
    /** the transport keys go to the other app */
    var active by mutableStateOf(false); private set
    /** the other app is playing right now */
    var playing by mutableStateOf(false); private set
    private var oursSince = 0L
    fun poll(ctx: android.content.Context, ours: Boolean) {
        val now = System.currentTimeMillis()
        if (ours) { oursSince = now; active = false; playing = false; return }
        // just after our own music stops the phone may still report music for a moment
        if (now - oursSince < 1500) return
        val am = ctx.getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager
        playing = am.isMusicActive
        if (playing) active = true
    }
    fun release() { active = false; playing = false }
    fun key(ctx: android.content.Context, code: Int) {
        val am = ctx.getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager
        runCatching {
            am.dispatchMediaKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, code))
            am.dispatchMediaKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, code))
        }
    }
}

/** The graphic EQ page: on / off, presets, the eight bands (drag them), preamp. */
@Composable
private fun VhsEq(fit: FitState, availH: Float, onExit: () -> Unit) {
    val state = LocalAppState.current
    val o = LocalOsd.current
    val names = Catalog.eqPresets.keys.toList()
    fun step(dir: Int) {
        val i = names.indexOf(state.eqPreset)
        val nx = if (i < 0) (if (dir > 0) 0 else names.lastIndex) else (i + dir).mod(names.size)
        Haptics.tick(); state.setEqEnabled(true); state.applyEqPreset(names[nx])
    }
    FitPage(fit, availH, 1.6f, hint = {
        Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { Haptics.tick(); onExit() } }) {
            Column {
                VhsText("DRAG A BAND UP / DOWN", invert = true)
                VhsText("TAP HERE TO RETURN", invert = true)
            }
        }
    }, body = {
        VhsHeader("GRAPHIC EQUALIZER", if (state.eqEnabled) "PRESET " + state.eqPreset else "BYPASS")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(2f)), verticalAlignment = Alignment.CenterVertically) {
            VhsKey(if (state.eqEnabled) "ON " else "OFF", selected = state.eqEnabled, boxed = true) { Haptics.tick(); state.setEqEnabled(!state.eqEnabled) }
            VhsKey("◀", boxed = true) { step(-1) }
            VhsText(state.eqPreset.take(6).padEnd(6))
            VhsKey("▶", boxed = true) { step(+1) }
            Spacer(Modifier.weight(1f))
            VhsKey("RESET", boxed = true) { Haptics.clunk("stop"); state.applyEqPreset("FLAT"); state.setEqPreamp(0f) }
        }
        // the eight bands: ±12 dB, 0 in the middle; drag or tap a column
        val gains = state.eqGains
        Box(Modifier.fillMaxWidth().height(pxDp(64f)).pointerInput(Unit) {
            fun set(pos: Offset) {
                val i = (pos.x / size.width * 8).toInt().coerceIn(0, 7)
                var db = ((0.5f - pos.y / size.height) * 24f).coerceIn(-12f, 12f)
                db = (db * 2).roundToInt() / 2f
                if (kotlin.math.abs(db) < 0.75f) db = 0f
                if (db != state.eqGains[i]) { if ((db * 2).toInt() % 4 == 0) Haptics.tick(); state.setEqEnabled(true); state.setEqBand(i, db) }
            }
            awaitEachGesture {
                val d0 = awaitFirstDown(); set(d0.position); d0.consume()
                while (true) {
                    val ev = awaitPointerEvent()
                    val c = ev.changes.firstOrNull { it.id == d0.id } ?: break
                    if (!c.pressed) break
                    set(c.position); c.consume()
                }
            }
        }) {
            Canvas(Modifier.matchParentSize()) {
                val px = o.px
                val cw = size.width / 8; val bw = cw - px * 2
                val seg = px * 1.4f; val gap = px * 0.6f
                val half = ((size.height / 2) / (seg + gap)).toInt().coerceAtLeast(1)
                val cy = size.height / 2
                drawRect(o.fg.copy(alpha = 0.5f), Offset(0f, cy - px * 0.25f), Size(size.width, px * 0.5f)) // 0 dB line
                for (i in 0 until 8) {
                    val g = gains.getOrElse(i) { 0f }
                    val lit = (kotlin.math.abs(g) / 12f * half).roundToInt()
                    val x = i * cw + px
                    for (k in 0 until half) {
                        val on = k < lit
                        val up = g >= 0
                        for (dirUp in listOf(true, false)) {
                            val y = if (dirUp) cy - (k + 1) * (seg + gap) else cy + k * (seg + gap) + gap
                            val lit2 = on && dirUp == up
                            val c = if (!state.eqEnabled) o.fg.copy(alpha = if (lit2) 0.35f else 0.06f) else o.fg.copy(alpha = if (lit2) 1f else 0.08f)
                            drawRect(c, Offset(x, y), Size(bw, seg))
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            listOf("60", "170", "350", "1K", "3K", "6K", "12K", "16K").forEach { l ->
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { VhsText(l, scale = 0.7f) }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            gains.forEach { g ->
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { VhsText((if (g > 0) "+" else "") + g.roundToInt(), scale = 0.7f, color = o.fg.copy(alpha = 0.7f)) }
            }
        }
        // preamp: the overall level in front of the bands
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            VhsText("PRE ")
            Box(Modifier.weight(1f)) {
                VhsBar((state.eqPreamp + 12f) / 24f, slider = true, centreMark = true) { v ->
                    var db = ((v * 24f - 12f) * 2).roundToInt() / 2f
                    if (kotlin.math.abs(db) < 0.75f) db = 0f
                    state.setEqPreamp(db)
                }
            }
            Spacer(Modifier.width(pxDp(2f)))
            VhsText(((if (state.eqPreamp > 0) "+" else "") + state.eqPreamp.roundToInt() + "DB").padStart(5))
        }
        // every preset, tap to load
        VhsText("PRESETS", color = o.fg.copy(alpha = 0.7f))
        names.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(2.5f))) {
                row.forEach { nme -> VhsKey(nme, selected = state.eqEnabled && state.eqPreset == nme, boxed = true) { Haptics.tick(); state.setEqEnabled(true); state.applyEqPreset(nme) } }
            }
        }
        // ---- SOUND: the effects rack behind the EQ (kept between launches)
        val ctx = LocalContext.current
        state.fxTick // redraw when a value changes
        val fx = com.reverender.r95.audio.AudioBus.fx
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            VhsText("SOUND", color = o.fg.copy(alpha = 0.7f))
            Spacer(Modifier.weight(1f))
            VhsKey("RESET", boxed = true) { Haptics.clunk("stop"); VhsSound.reset(); state.fx { }; VhsSound.save(ctx) }
        }
        fun edit(f: com.reverender.r95.audio.FxSettings.() -> Unit) { state.fx(f); VhsSound.save(ctx) }
        val log = { v: Float, lo: Float, hi: Float -> lo * Math.pow((hi / lo).toDouble(), v.toDouble()).toFloat() }
        val unlog = { x: Float, lo: Float, hi: Float -> (kotlin.math.ln(x / lo) / kotlin.math.ln(hi / lo)).coerceIn(0f, 1f) }
        SoundRow("BASS+", fx.subDb / 15f, "+" + fx.subDb.roundToInt() + "DB") { edit { subDb = (it * 15f * 2).roundToInt() / 2f } }
        SoundRow("LOUD ", fx.comp / 100f, fx.comp.roundToInt().toString()) { edit { comp = it * 100f } }
        SoundRow("WIDTH", fx.width / 100f, fx.width.roundToInt().toString()) { edit { width = it * 100f } }
        SoundRow("ROOM ", fx.reverb / 100f, fx.reverb.roundToInt().toString()) { edit { reverb = it * 100f } }
        SoundRow("ECHO ", fx.echoMix / 0.85f, (fx.echoMix / 0.85f * 100).roundToInt().toString()) { edit { echoMix = it * 0.85f } }
        SoundRow("WARM ", fx.drive / 40f, (fx.drive / 40f * 100).roundToInt().toString()) { edit { drive = it * 40f } }
        SoundRow("LOCUT", unlog(fx.hpf, 20f, 3500f), if (fx.hpf < 25f) "OFF" else fx.hpf.roundToInt().toString()) { edit { hpf = log(it, 20f, 3500f).let { h -> if (h < 25f) 20f else h } } }
        SoundRow("HICUT", 1f - unlog(fx.lpf, 250f, 20000f), if (fx.lpf > 19000f) "OFF" else (fx.lpf / 1000f).let { "%.1fK".format(it) }) { edit { lpf = log(1f - it, 250f, 20000f).let { l -> if (l > 19000f) 20000f else l } } }
        SoundRow("BAL  ", (fx.pan + 1f) / 2f, when { fx.pan < -0.04f -> "L" + (-fx.pan * 100).roundToInt(); fx.pan > 0.04f -> "R" + (fx.pan * 100).roundToInt(); else -> "MID" }, centre = true) {
            edit { pan = (it * 2f - 1f).let { v -> if (kotlin.math.abs(v) < 0.05f) 0f else v } }
        }
    })
}

/** one sound slider on the EQ page: name, bar, value */
@Composable
private fun SoundRow(label: String, value: Float, shown: String, centre: Boolean = false, onChange: (Float) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        VhsText(label)
        Spacer(Modifier.width(pxDp(2f)))
        Box(Modifier.weight(1f)) { VhsBar(value.coerceIn(0f, 1f), slider = true, centreMark = centre) { onChange(it.coerceIn(0f, 1f)) } }
        Spacer(Modifier.width(pxDp(2f)))
        VhsText(shown.take(5).padStart(5))
    }
}

/** The EQ page's sound settings, saved so they come back after a restart. */
internal object VhsSound {
    private const val FILE = "r95_vhs_sound"
    fun save(ctx: android.content.Context) {
        val f = com.reverender.r95.audio.AudioBus.fx
        ctx.getSharedPreferences(FILE, 0).edit()
            .putFloat("sub", f.subDb).putFloat("comp", f.comp).putFloat("width", f.width).putFloat("reverb", f.reverb)
            .putFloat("echo", f.echoMix).putFloat("drive", f.drive).putFloat("hpf", f.hpf).putFloat("lpf", f.lpf).putFloat("pan", f.pan)
            .putBoolean("saved", true).apply()
    }
    fun load(ctx: android.content.Context) {
        val p = ctx.getSharedPreferences(FILE, 0)
        if (!p.getBoolean("saved", false)) return
        val f = com.reverender.r95.audio.AudioBus.fx
        f.subDb = p.getFloat("sub", 0f); f.comp = p.getFloat("comp", 0f); f.width = p.getFloat("width", 0f); f.reverb = p.getFloat("reverb", 0f)
        f.echoMix = p.getFloat("echo", 0f); f.drive = p.getFloat("drive", 0f); f.hpf = p.getFloat("hpf", 20f); f.lpf = p.getFloat("lpf", 20000f); f.pan = p.getFloat("pan", 0f)
        f.touch()
    }
    fun reset() {
        val f = com.reverender.r95.audio.AudioBus.fx
        f.subDb = 0f; f.comp = 0f; f.width = 0f; f.reverb = 0f; f.echoMix = 0f; f.drive = 0f; f.hpf = 20f; f.lpf = 20000f; f.pan = 0f
        f.touch()
    }
}

/** small shared UI facts */
internal object VhsUi {
    /** the apps drawer covers the screen: things behind it can rest */
    @Volatile var drawerOpen = false
    /** another menu is open over the (kept) home menu */
    @Volatile var mainHidden = false
}

/** The help bars at the bottom: shown for 2 s after a touch or a new screen, then they slide away. */
internal object VhsHint {
    var shown by mutableStateOf(true)
    var pokes by mutableIntStateOf(0)
    fun poke() { pokes++ }
}
/** slides a help bar down out of sight while hidden (it gives its space back to the page) */
internal fun Modifier.vhsAutoHide(): Modifier = composed {
    val f by androidx.compose.animation.core.animateFloatAsState(
        if (VhsHint.shown) 1f else 0f,
        androidx.compose.animation.core.tween(if (VhsHint.shown) 180 else 700),
        label = "hint",
    )
    this.clipToBounds().layout { m, c ->
        val pl = m.measure(c)
        val h = (pl.height * f).roundToInt()
        layout(pl.width, h) { pl.place(0, 0) }
    }
}

/** Wide screens: the apps list as a side panel next to the home menu (WIDE = APPS in setup). */
@Composable
private fun VhsAppsPane() {
    val ctx = LocalContext.current
    val o = LocalOsd.current
    val apps = remember(Launcher.apps.size) { Launcher.apps.sortedBy { it.label.uppercase() } }
    fun keyOf(label: String): Char = label.trim().firstOrNull()?.uppercaseChar()?.takeIf { it in 'A'..'Z' } ?: '#'
    Column(Modifier.fillMaxSize().padding(horizontal = pxDp(2f), vertical = pxDp(2f)), verticalArrangement = Arrangement.spacedBy(pxDp(1.4f))) {
        VhsHeader("APPS", "${apps.size} INSTALLED")
        androidx.compose.foundation.lazy.LazyColumn(Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(pxDp(2f))) {
            items(apps.size, key = { apps[it].component.toString() + it }, contentType = { 0 }) { i ->
                val a = apps[i]
                val first = i == 0 || keyOf(apps[i - 1].label) != keyOf(a.label)
                Column {
                    if (first) VhsText(keyOf(a.label).toString(), invert = true)
                    Box(Modifier.fillMaxWidth().pointerInput(a.component) { detectTapGestures { Haptics.tick(); Launcher.launch(ctx, a, fullScreen = true) } }) {
                        VhsText(vhsSafe(a.label).trim().take(20))
                    }
                }
            }
        }
    }
}
