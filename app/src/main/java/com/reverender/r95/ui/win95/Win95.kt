package com.reverender.r95.ui.win95

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Longhorn accent (rr-os-styles: id, label, accent, light, dark). */
class LhAccent(val id: String, val label: String, val a: Color, val l: Color, val d: Color)
val LH_ACCENTS = listOf(
    LhAccent("jade", "Jade", Color(0xFF20A884), Color(0xFF8FF2D2), Color(0xFF0B4F40)),
    LhAccent("plex", "Plex Blue", Color(0xFF3A8FD6), Color(0xFFA9D8FF), Color(0xFF123F69)),
    LhAccent("slate", "Slate", Color(0xFF7890A6), Color(0xFFD3E0EC), Color(0xFF2C3A47)),
    LhAccent("amber", "Amber", Color(0xFFE0962A), Color(0xFFFFD79A), Color(0xFF6A3D06)),
    LhAccent("ruby", "Ruby", Color(0xFFC4394F), Color(0xFFFFB3C0), Color(0xFF5C0F1D)),
)

/**
 * The site's two OS looks. "longhorn" (75% of visits) = dark glass frames and light glossy controls;
 * "win95" = classic bevels coloured by the active theme's CSS variables.
 */
object Skin {
    var style by mutableStateOf("longhorn")
    var accent by mutableStateOf(LH_ACCENTS[2])
    var vars by mutableStateOf(CLASSIC_THEMES.getValue("mallsoft"))
    val classic: Boolean get() = style == "win95"
}

/** Colours of the active look (Longhorn values are the computed CSS read from reverender.com). */
object W95 {
    val Face get() = if (Skin.classic) Skin.vars.winBg else Color(0xFFEEF1F2)
    val Light get() = if (Skin.classic) Skin.vars.light else Color(0xFFFFFFFF)
    val Hi get() = if (Skin.classic) Skin.vars.midLight else Color(0xFFDDE4E8)
    val Shadow get() = if (Skin.classic) Skin.vars.dark else Color(0xFF6B7A83)
    val Dark get() = if (Skin.classic) Skin.vars.black else Color(0xFF11191D)
    val Text get() = if (Skin.classic) Skin.vars.text else Color(0xFF11191D)
    val Navy get() = if (Skin.classic) Skin.vars.title1 else Color(0xFF2C4A63)
    val Lcd get() = if (Skin.classic) Color(0x9E000000) else Color(0xEB061014)
    val Border get() = if (Skin.classic) Skin.vars.dark else Color(0xFF8C9AA3)
    val SoftBorder get() = if (Skin.classic) Skin.vars.dark else Color(0xFFC3CCD1)
    val Cell get() = if (Skin.classic) Color(0xFF9E9E9E) else Color(0xFFE2E7EA)
    val HeaderA get() = if (Skin.classic) Skin.vars.title1 else Skin.accent.d
    val HeaderB get() = if (Skin.classic) Skin.vars.title2 else Skin.accent.a
    val Cyan get() = Skin.vars.cyan
    val Pink get() = Skin.vars.pink
    val font = FontFamily.SansSerif
    val mono = FontFamily.Monospace
    val text get() = TextStyle(color = Text, fontFamily = font, fontWeight = if (Skin.classic) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp)

    val btnBrush: Brush get() = if (Skin.classic) SolidColor(Skin.vars.winBg) else Brush.verticalGradient(0f to Color(0xFFFFFFFF), 0.48f to Color(0xFFEEF2F4), 0.52f to Color(0xFFDDE4E8), 1f to Color(0xFFE8EDF0))
    val btnDownBrush: Brush get() = if (Skin.classic) SolidColor(Color(0xFF9E9E9E)) else Brush.verticalGradient(0f to Color(0xFFC9D3D9), 0.5f to Color(0xFFD6DEE3), 1f to Color(0xFFE6ECEF))
    val headerBrush: Brush get() = Brush.horizontalGradient(listOf(HeaderA, HeaderB))
    /** Corner shape of controls: square in the classic look. */
    val shape get() = if (Skin.classic) RoundedCornerShape(0.dp) else RoundedCornerShape(3.dp)
}

private val R3 get() = W95.shape

/** Raised = control border, sunken = inset field. Classic: the site's 2px win95 bevel; Longhorn: thin rounded outline. */
fun Modifier.bevel(raised: Boolean = true, px: Dp = 1.dp): Modifier = drawBehind { drawBevel(raised, px.toPx()) }

fun DrawScope.drawBevel(raised: Boolean, p: Float) {
    if (Skin.classic) { drawClassicBevel(raised, p); return }
    val r = CornerRadius(3.dp.toPx())
    if (raised) {
        drawRoundRect(W95.Border, cornerRadius = r, style = Stroke(p))
        drawLine(Color.White.copy(alpha = 0.9f), Offset(r.x, p * 1.5f), Offset(size.width - r.x, p * 1.5f), p)
    } else {
        drawRoundRect(Color(0xFFA9B6BE), cornerRadius = r, style = Stroke(p))
        drawLine(Color.Black.copy(alpha = 0.10f), Offset(r.x, p * 1.5f), Offset(size.width - r.x, p * 1.5f), p * 2)
    }
}

/** border 2px light/black + inset 1px midlight/dark (raised) or the inverse (sunken). */
fun DrawScope.drawClassicBevel(raised: Boolean, p: Float) {
    val v = Skin.vars
    val tl = if (raised) v.light else v.black; val br = if (raised) v.black else v.light
    val itl = if (raised) v.midLight else v.dark; val ibr = if (raised) v.dark else v.midLight
    val w = size.width; val h = size.height; val b = 2 * p
    drawRect(tl, Offset(0f, 0f), Size(w, b)); drawRect(tl, Offset(0f, 0f), Size(b, h))
    drawRect(br, Offset(0f, h - b), Size(w, b)); drawRect(br, Offset(w - b, 0f), Size(b, h))
    drawRect(itl, Offset(b, b), Size(w - 2 * b, p)); drawRect(itl, Offset(b, b), Size(p, h - 2 * b))
    drawRect(ibr, Offset(b, h - b - p), Size(w - 2 * b, p)); drawRect(ibr, Offset(w - b - p, b), Size(p, h - 2 * b))
}

@Composable
fun T(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = W95.Text,
    size: TextUnit = 13.sp,
    bold: Boolean = false,
    mono: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    align: TextAlign = TextAlign.Start,
) {
    BasicText(
        text = text,
        modifier = modifier,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = W95.text.copy(
            color = color, fontSize = size,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
            fontFamily = if (mono) W95.mono else W95.font,
            textAlign = align,
        ),
    )
}

@Composable
fun Panel(modifier: Modifier = Modifier, raised: Boolean = true, color: Color = W95.Face, content: @Composable BoxScope.() -> Unit) {
    Box(modifier.background(color, R3).bevel(raised).padding(2.dp), content = content)
}

/** Light glossy Longhorn push button. */
@Composable
fun Btn(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    pressed: Boolean = false,
    enabled: Boolean = true,
    size: TextUnit = 13.sp,
    bold: Boolean = false,
) {
    val source = remember { MutableInteractionSource() }
    val down by source.collectIsPressedAsState()
    val sunk = down || pressed
    Box(
        modifier
            .background(if (sunk) W95.btnDownBrush else W95.btnBrush, R3)
            .then(if (Skin.classic) Modifier.bevel(!sunk) else Modifier.border(1.dp, if (pressed) Color(0xFF5E7487) else W95.Border, R3))
            .clickable(interactionSource = source, indication = null, enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        T(text, color = if (enabled) W95.Text else W95.Shadow, size = if (Skin.classic && size == 13.sp) 11.sp else size, bold = bold || pressed || Skin.classic, maxLines = 1, align = TextAlign.Center,
            modifier = if (Skin.classic && sunk) Modifier.padding(start = 2.dp, top = 1.dp) else Modifier)
    }
}

@Composable
fun Check(label: String, checked: Boolean, onChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onChange(!checked) }
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            if (Skin.classic) Modifier.size(16.dp).background(Color.White).bevel(false)
            else Modifier.size(16.dp).background(Brush.verticalGradient(listOf(Color(0xFFF4F6F7), Color.White)), RoundedCornerShape(2.dp))
                .border(1.dp, W95.Border, RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center,
        ) { if (checked) T("✔", color = if (Skin.classic) Color.Black else W95.Navy, size = 11.sp, bold = true) }
        if (label.isNotEmpty()) {
            Spacer(Modifier.width(8.dp))
            T(label)
        }
    }
}

/** White inset text box. */
@Composable
fun Field(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    hint: String = "",
    onDone: (() -> Unit)? = null,
    mono: Boolean = false,
) {
    val classic = Skin.classic
    Box(modifier.background(if (classic) W95.Lcd else Color.White, R3).bevel(false).padding(horizontal = 7.dp, vertical = 6.dp)) {
        if (value.isEmpty() && hint.isNotEmpty()) T(hint, color = if (classic) W95.Cyan.copy(alpha = 0.55f) else Color(0xFF8A979F), mono = mono || classic, maxLines = if (singleLine) 1 else Int.MAX_VALUE, size = if (classic) 11.sp else 13.sp)
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = singleLine,
            textStyle = if (classic) W95.text.copy(color = W95.Cyan, fontFamily = W95.mono, fontSize = 11.sp) else W95.text.copy(fontFamily = if (mono) W95.mono else W95.font),
            cursorBrush = SolidColor(if (classic) W95.Pink else W95.Navy),
            keyboardOptions = if (onDone != null) KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Send) else KeyboardOptions.Default,
            keyboardActions = KeyboardActions(onSend = { onDone?.invoke() }),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * Longhorn trackbar: dark tick-marked groove with a glossy thumb (like TRACK POSITION SCRUBBER).
 * Horizontal by default; vertical sliders have max at the top.
 */
@Composable
fun Slider95(
    value: Float,
    onChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    range: ClosedFloatingPointRange<Float> = 0f..1f,
    vertical: Boolean = false,
    accent: Color = W95.Navy,
) {
    val cb by rememberUpdatedState(onChange)
    val latest: (Float) -> Unit = { v -> cb(v); com.reverender.r95.ui.viz.site.SiteHost.uiNow() }
    val r by rememberUpdatedState(range)
    fun fromPos(pos: Offset, w: Float, h: Float): Float {
        val t = if (vertical) 1f - (pos.y / h) else pos.x / w
        return r.start + t.coerceIn(0f, 1f) * (r.endInclusive - r.start)
    }
    Box(
        modifier
            .pointerInput(vertical) { detectTapGestures { latest(fromPos(it, size.width.toFloat(), size.height.toFloat())) } }
            .pointerInput(vertical) {
                detectDragGestures(onDragStart = { latest(fromPos(it, size.width.toFloat(), size.height.toFloat())) }) { change, _ ->
                    change.consume()
                    latest(fromPos(change.position, size.width.toFloat(), size.height.toFloat()))
                }
            }
            .drawBehind {
                val t = ((value - range.start) / (range.endInclusive - range.start)).coerceIn(0f, 1f)
                if (Skin.classic) { drawClassicRange(t, vertical); return@drawBehind }
                val groove = 6.dp.toPx()
                val tick = Color(0xFF8FA0AA)
                if (vertical) {
                    val cx = size.width / 2
                    drawRoundRect(Color(0xFF26313A), Offset(cx - groove / 2, 0f), Size(groove, size.height), CornerRadius(2f))
                    drawRoundRect(Color.White.copy(alpha = 0.55f), Offset(cx - groove / 2, 0f), Size(groove, size.height), CornerRadius(2f), style = Stroke(1f))
                    var y = 4f; while (y < size.height) { drawLine(tick, Offset(cx - 1f, y), Offset(cx + 1f, y), 1f); y += 6.dp.toPx() }
                    val thumbH = 12.dp.toPx(); val thumbW = minOf(size.width, 24.dp.toPx())
                    val ty = (1f - t) * (size.height - thumbH)
                    drawRect(accent.copy(alpha = 0.45f), Offset(cx - groove / 2 + 1, ty + thumbH / 2), Size(groove - 2, size.height - ty - thumbH / 2))
                    drawThumb(Offset(cx - thumbW / 2, ty), Size(thumbW, thumbH), vertical = true)
                } else {
                    val cy = size.height / 2
                    drawRoundRect(Color(0xFF26313A), Offset(0f, cy - groove / 2), Size(size.width, groove), CornerRadius(2f))
                    drawRoundRect(Color.White.copy(alpha = 0.55f), Offset(0f, cy - groove / 2), Size(size.width, groove), CornerRadius(2f), style = Stroke(1f))
                    var x = 4f; while (x < size.width) { drawLine(tick, Offset(x, cy - 1f), Offset(x, cy + 1f), 1f); x += 6.dp.toPx() }
                    val thumbW = 12.dp.toPx(); val thumbH = minOf(size.height, 22.dp.toPx())
                    val tx = t * (size.width - thumbW)
                    drawRect(accent.copy(alpha = 0.45f), Offset(1f, cy - groove / 2 + 1), Size(tx + thumbW / 2, groove - 2))
                    drawThumb(Offset(tx, cy - thumbH / 2), Size(thumbW, thumbH), vertical = false)
                }
            },
    )
}

/** The site's classic input[type=range]: 10px sunken track with the title gradient and cyan ticks, 14x20 bevelled thumb. */
private fun DrawScope.drawClassicRange(t: Float, vertical: Boolean) {
    val v = Skin.vars
    val dp = density
    fun track(len: Float, cross: Float, horiz: Boolean) {
        val th = 10 * dp
        val c = cross / 2
        val o = if (horiz) Offset(0f, c - th / 2) else Offset(c - th / 2, 0f)
        val sz = if (horiz) Size(len, th) else Size(th, len)
        drawRect(Color(0x9E000000), o, sz)
        // 2px title gradient line and 3px tall cyan ticks every 8px
        if (horiz) {
            drawRect(Brush.horizontalGradient(listOf(v.title1, v.title2, v.pink)), Offset(2 * dp, c - dp), Size(len - 4 * dp, 2 * dp))
            var x = 2 * dp; while (x < len - 2 * dp) { drawRect(v.cyan, Offset(x, c + th / 2 - 5 * dp), Size(dp, 3 * dp)); x += 8 * dp }
        } else {
            drawRect(Brush.verticalGradient(listOf(v.pink, v.title2, v.title1)), Offset(c - dp, 2 * dp), Size(2 * dp, len - 4 * dp))
            var y = 2 * dp; while (y < len - 2 * dp) { drawRect(v.cyan, Offset(c + th / 2 - 5 * dp, y), Size(3 * dp, dp)); y += 8 * dp }
        }
        // sunken 2px bevel
        val b = 2 * dp
        drawRect(v.black, o, if (horiz) Size(len, b) else Size(b, len)); drawRect(v.black, o, if (horiz) Size(b, th) else Size(th, b))
        drawRect(v.light, if (horiz) Offset(0f, o.y + th - b) else Offset(o.x, len - b), if (horiz) Size(len, b) else Size(th, b))
        drawRect(v.light, if (horiz) Offset(len - b, o.y) else Offset(o.x + th - b, 0f), if (horiz) Size(b, th) else Size(b, len))
    }
    fun thumb(o: Offset, w: Float, h: Float) {
        drawRect(v.pink.copy(alpha = 0.35f), Offset(o.x - 3 * dp, o.y - 3 * dp), Size(w + 6 * dp, h + 6 * dp))
        drawRect(Brush.verticalGradient(0f to v.light, 0.25f to v.winBg, 0.75f to v.winBg, 1f to v.dark, startY = o.y, endY = o.y + h), o, Size(w, h))
        // grip: dark/light pairs and the pink centre stripe
        val cx = o.x + w / 2
        drawRect(v.pink, Offset(cx - dp, o.y + 2 * dp), Size(2 * dp, h - 4 * dp))
        for (k in listOf(-4f, 3f)) { drawRect(v.dark, Offset(cx + k * dp, o.y + 3 * dp), Size(dp, h - 6 * dp)); drawRect(v.light, Offset(cx + (k + 1) * dp, o.y + 3 * dp), Size(dp, h - 6 * dp)) }
        val b = 2 * dp
        drawRect(v.light, o, Size(w, b)); drawRect(v.light, o, Size(b, h))
        drawRect(v.black, Offset(o.x, o.y + h - b), Size(w, b)); drawRect(v.black, Offset(o.x + w - b, o.y), Size(b, h))
    }
    if (vertical) {
        track(size.height, size.width, false)
        val th = 14 * dp; val tw = 20 * dp
        val y = (1f - t) * (size.height - th)
        thumb(Offset(size.width / 2 - tw / 2, y), tw, th)
    } else {
        track(size.width, size.height, true)
        val tw = 14 * dp; val th = minOf(size.height, 20 * dp)
        val x = t * (size.width - tw)
        thumb(Offset(x, size.height / 2 - th / 2), tw, th)
    }
}

private fun DrawScope.drawThumb(o: Offset, s: Size, vertical: Boolean) {
    drawRoundRect(Brush.verticalGradient(listOf(Color.White, Color(0xFFE3E9EC), Color(0xFFC9D2D8)), o.y, o.y + s.height), o, s, CornerRadius(2f))
    drawRoundRect(Color(0xFF5E6E78), o, s, CornerRadius(2f), style = Stroke(1f))
    // grip lines
    val g = Color(0xFF8A99A2)
    if (vertical) for (k in -1..1) drawLine(g, Offset(o.x + 3, o.y + s.height / 2 + k * 3), Offset(o.x + s.width - 3, o.y + s.height / 2 + k * 3), 1f)
    else for (k in -1..1) drawLine(g, Offset(o.x + s.width / 2 + k * 3, o.y + 4), Offset(o.x + s.width / 2 + k * 3, o.y + s.height - 4), 1f)
}

/** Black LCD panel used by players and meters. */
@Composable
fun Lcd(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    if (Skin.classic) Box(modifier.background(W95.Lcd).drawBehind {
        val p = 2.dp.toPx(); drawRect(Color(0xFF222222), Offset.Zero, Size(size.width, p)); drawRect(Color(0xFF222222), Offset.Zero, Size(p, size.height))
        drawRect(Color.White, Offset(0f, size.height - p), Size(size.width, p)); drawRect(Color.White, Offset(size.width - p, 0f), Size(p, size.height))
    }.padding(6.dp), content = content)
    else Box(modifier.background(W95.Lcd).border(1.dp, Color(0xFF333333)).padding(6.dp), content = content)
}

@Composable
fun Divider95(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(1.dp).background(W95.SoftBorder))
}

/** Slate-blue gradient header bar ("Radio & input ...... WAVE ROUTER 95"). */
@Composable
fun SectionHeader(title: String, right: String = "", modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().background(W95.headerBrush, if (Skin.classic) RoundedCornerShape(0.dp) else RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)).padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        T(title, color = Color.White, bold = true, size = if (Skin.classic) 11.sp else 12.sp, mono = Skin.classic, maxLines = 1, modifier = Modifier.weight(1f))
        if (right.isNotEmpty()) T(right, color = if (Skin.classic) Color.White else Color(0xFFDDE7EF), bold = true, size = 10.sp, mono = Skin.classic, maxLines = 1)
    }
}

/** Grey status cell ("MODE: STANDBY"). */
@Composable
fun StatusCell(text: String, modifier: Modifier = Modifier) {
    Box((if (Skin.classic) modifier.background(W95.Cell).drawBehind {
        val p = 1.dp.toPx(); val v = Skin.vars
        drawRect(v.dark, Offset.Zero, Size(size.width, p)); drawRect(v.dark, Offset.Zero, Size(p, size.height))
        drawRect(v.light, Offset(0f, size.height - p), Size(size.width, p)); drawRect(v.light, Offset(size.width - p, 0f), Size(p, size.height))
    } else modifier.background(W95.Cell, R3).border(1.dp, W95.SoftBorder, R3)).padding(horizontal = 6.dp, vertical = 4.dp)) {
        T(text, size = 10.sp, bold = true, maxLines = 1)
    }
}

/** A titled section: gradient header + light body, as used across the site's windows. */
@Composable
fun GroupBox(title: String, modifier: Modifier = Modifier, right: String = "", content: @Composable () -> Unit) {
    Column(modifier.padding(top = 6.dp)) {
        SectionHeader(title, right)
        Box(
            (if (Skin.classic) Modifier.fillMaxWidth().background(W95.Face).bevel(true)
            else Modifier.fillMaxWidth().background(Color(0xFFF3F5F6), RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                .border(1.dp, W95.SoftBorder, RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))).padding(8.dp),
        ) { content() }
    }
}

/** Expandable row with a coloured accent bar and ▸ chevron (the site's source accordions). */
@Composable
fun Accordion(
    title: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier,
    startOpen: Boolean = false,
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var openLocal by remember { mutableStateOf(startOpen) }
    val isOpen = open ?: openLocal
    fun toggle() { if (onOpenChange != null) onOpenChange(!isOpen) else openLocal = !openLocal }
    Column(
        modifier.fillMaxWidth().padding(vertical = 2.dp)
            .then(if (Skin.classic) Modifier.background(W95.Face).bevel(true) else Modifier.background(Color(0xFFF7F9FA), RoundedCornerShape(6.dp)).border(1.dp, W95.SoftBorder, RoundedCornerShape(6.dp))),
    ) {
        Row(
            Modifier.fillMaxWidth().clickable { toggle() }.padding(start = 6.dp, end = 8.dp, top = 7.dp, bottom = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.width(4.dp).height(20.dp).background(accent, RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                T(title, bold = true, size = 13.sp, maxLines = 1)
                if (subtitle.isNotEmpty()) T(subtitle, size = 10.sp, color = W95.Shadow, maxLines = 1)
            }
            T(if (isOpen) "▾" else "▸", size = 13.sp)
        }
        if (isOpen) Box(Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 8.dp)) { content() }
    }
}

/** "LABEL ........ value" over a trackbar, the layout every rack on the site uses. */
@Composable
fun SliderRow(
    label: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = W95.Navy,
) {
    Column(modifier.padding(vertical = 2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            T(label, size = 10.sp, bold = true, maxLines = 1, modifier = Modifier.weight(1f))
            T(valueText, size = 10.sp, mono = true)
        }
        Slider95(value, onChange, Modifier.fillMaxWidth().height(24.dp), range = range, accent = accent)
    }
}

/** A boxed slider like "TRACK POSITION SCRUBBER ...... 0.0%". */
@Composable
fun SliderBox(label: String, valueText: String, value: Float, range: ClosedFloatingPointRange<Float>, onChange: (Float) -> Unit, modifier: Modifier = Modifier) {
    Column((if (Skin.classic) modifier.fillMaxWidth().background(W95.Face).bevel(true) else modifier.fillMaxWidth().background(Color(0xFFF7F9FA), R3).border(1.dp, W95.SoftBorder, R3)).padding(horizontal = 8.dp, vertical = 5.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            T(label, size = 11.sp, bold = true, maxLines = 1, modifier = Modifier.weight(1f))
            T(valueText, size = 11.sp, mono = true)
        }
        Slider95(value, onChange, Modifier.fillMaxWidth().height(24.dp), range = range)
    }
}
