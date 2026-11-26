package com.reverender.r95.ui.desktop

import androidx.compose.ui.layout.boundsInRoot

import androidx.compose.ui.layout.positionInRoot

import androidx.compose.ui.layout.onGloballyPositioned

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import com.reverender.r95.core.AppGroup
import com.reverender.r95.core.Apps
import com.reverender.r95.ui.win95.Skin
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import com.reverender.r95.ui.win95.bevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
 * The site's classic look (rr-app-core CSS without html.rr-os-modern): win95 bevels coloured by the theme.
 */

/** .win95-window: raised 2px bevel, 6px hard shadow, title bar gradient (title-active-1 → -2), body padding 5px. */
@Composable
internal fun ClassicWindowFrame(
    win: Win, active: Boolean, modifier: Modifier,
    onClose: () -> Unit, onMinimize: () -> Unit, onMaximize: () -> Unit,
    onDrag: (Float, Float) -> Unit, onDragEnd: () -> Unit, onResize: (Float, Float) -> Unit,
    content: @Composable () -> Unit,
) {
    val drag by rememberUpdatedState(onDrag)
    val dragEnd by rememberUpdatedState(onDragEnd)
    val maxi by rememberUpdatedState(onMaximize)
    val v = Skin.vars
    val op = com.reverender.r95.LocalAppState.current.windowOpacity.coerceIn(0.3f, 1f)
    Column(
        modifier
            .drawBehind { drawRect(Color.Black.copy(alpha = 0.55f * op), Offset(6.dp.toPx(), 6.dp.toPx()), size) }
            .background(v.winBg.copy(alpha = op)).bevel(true),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(4.dp).height(28.dp)
                .background(Brush.horizontalGradient((if (active) listOf(v.title1, v.title2) else listOf(v.inactive1, v.inactive2)).map { it.copy(alpha = it.alpha * op) }))
                .pointerInput(win.key) { detectTapGestures(onDoubleTap = { maxi() }) }
                .pointerInput(win.key) { detectDragGestures(onDragEnd = { dragEnd() }, onDragCancel = { dragEnd() }) { c, d -> c.consume(); drag(d.x, d.y) } }
                .padding(horizontal = 5.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                win.title(), maxLines = 1, modifier = Modifier.weight(1f),
                style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.4.sp, shadow = Shadow(Color.Black, Offset(1f, 1f), 0f)),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                if (win.app.id in com.reverender.r95.core.VIZ_FX_WINDOWS) ClassicTitleBtn("FX", wide = true) { win.fxOpen = !win.fxOpen }
                ClassicTitleBtn("_", onClick = onMinimize)
                ClassicTitleBtn("□", onClick = onMaximize)
                ClassicTitleBtn("×", onClick = onClose)
            }
        }
        Box(Modifier.fillMaxWidth().weight(1f).padding(start = 4.dp, end = 4.dp, bottom = 4.dp)) {
            content()
            if (!win.maximized) {
                // .resize-se: the striped grip in the corner
                Box(
                    Modifier.align(Alignment.BottomEnd).size(22.dp)
                        .pointerInput(win.key) { detectDragGestures(onDragStart = { win.resizeHintUntil = 0L }) { c, d -> c.consume(); onResize(d.x, d.y) } }
                        .drawBehind {
                            val s = 18.dp.toPx(); val o = Offset(size.width - s, size.height - s)
                            for ((a, col) in listOf(0.4f to Color(0xFF444444), 0.5f to Color.White, 0.7f to Color(0xFF444444), 0.8f to Color.White)) {
                                val w = s * 0.1f
                                drawLine(col, Offset(o.x + s * a, o.y + s), Offset(o.x + s, o.y + s * a), w)
                            }
                        },
                )
            }
            ResizeCornerHint(win)
        }
    }
}

/** .win95-title-btn: 22x20 classic button, bold glyph. */
@Composable
private fun ClassicTitleBtn(label: String, wide: Boolean = false, onClick: () -> Unit) {
    val src = remember { MutableInteractionSource() }
    val down by src.collectIsPressedAsState()
    Box(
        Modifier.size(width = if (wide) 28.dp else 22.dp, height = 20.dp).background(if (down) Color(0xFF9E9E9E) else Skin.vars.winBg).bevel(!down)
            .clickable(interactionSource = src, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { T(label, color = Color.Black, size = 11.sp, bold = true) }
}

/** #taskbar: win-bg bar with a 2px light top edge, Start button, task tabs, sunken tray. */
@Composable
internal fun ClassicTaskbar(wm: WindowManager, vertical: Boolean, tray: @Composable () -> Unit) {
    val v = Skin.vars
    val bar = Modifier.background(v.winBg).drawBehind { drawRect(v.light, Offset.Zero, Size(size.width, 2.dp.toPx())) }
    val tabs: @Composable () -> Unit = {
        wm.windows.toList().forEach { win ->
            val active = wm.top === win
            Box(
                Modifier.padding(horizontal = 2.dp).height(28.dp).widthIn(min = 78.dp, max = 130.dp)
                    .background(if (active) Color(0xFF9E9E9E) else v.winBg).bevel(!active)
                    .clickable { wm.taskbarTap(win) }.padding(horizontal = 7.dp),
                contentAlignment = Alignment.CenterStart,
            ) { T(win.app.title, size = 12.sp, bold = true, maxLines = 1, color = Color.Black) }
        }
    }
    if (!vertical) {
        Row(bar.fillMaxWidth().height(40.dp).padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            ClassicStartBtn(wm)
            Spacer(Modifier.width(4.dp))
            com.reverender.r95.ui.apps.PinnedApps(false)
            Row(Modifier.weight(1f).horizontalScroll(rememberScrollState())) { tabs() }
            tray()
        }
    } else {
        Column(bar.fillMaxHeight().width(64.dp).padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            ClassicStartBtn(wm)
            com.reverender.r95.ui.apps.PinnedApps(true)
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) { tabs() }
            tray()
        }
    }
}

@Composable
private fun ClassicStartBtn(wm: WindowManager) {
    val open = wm.startOpen
    Box(
        Modifier.height(30.dp).background(if (open) Color(0xFF9E9E9E) else Skin.vars.winBg).bevel(!open)
            .clickable { com.reverender.r95.audio.UiSfx.play(); wm.startOpen = !wm.startOpen }.padding(horizontal = 9.dp),
        contentAlignment = Alignment.Center,
    ) { T("Start", size = 14.sp, bold = true, color = Color.Black) }
}

/** #taskbar-tray: sunken 1px box. */
@Composable
internal fun ClassicTrayBox(content: @Composable () -> Unit) {
    val v = Skin.vars
    Row(
        Modifier.height(30.dp).background(v.winBg).drawBehind {
            val p = 1.dp.toPx()
            drawRect(v.dark, Offset.Zero, Size(size.width, p)); drawRect(v.dark, Offset.Zero, Size(p, size.height))
            drawRect(v.light, Offset(0f, size.height - p), Size(size.width, p)); drawRect(v.light, Offset(size.width - p, 0f), Size(p, size.height))
        }.padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) { content() }
}

/** #start-menu: 225px, raised bevel, 6px shadow; vertical gradient sidebar "REVERENDER 95"; flyout submenus. */
@Composable
internal fun ClassicStartMenu(wm: WindowManager, align: Alignment) {
    var flyout by remember { mutableStateOf<AppGroup?>(null) }
    val v = Skin.vars
    val panel = Modifier.drawBehind { drawRect(Color.Black.copy(alpha = 0.65f), Offset(6.dp.toPx(), 6.dp.toPx()), size) }.background(v.winBg).bevel(true)
    val rowBounds = remember { HashMap<AppGroup, androidx.compose.ui.geometry.Rect>() }
    var areaOrigin by remember { mutableStateOf(Offset.Zero) }
    var menuBounds by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }
    var subBounds by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }
    var preview by remember { mutableStateOf<Pair<com.reverender.r95.core.AppInfo, androidx.compose.ui.geometry.Rect>?>(null) }
    var phoneFly by remember { mutableStateOf(false) }
    var phoneRow by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }
    androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxSize().onGloballyPositioned { areaOrigin = it.positionInRoot() }.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { wm.startOpen = false }) {
        val areaW = constraints.maxWidth.toFloat(); val areaH = constraints.maxHeight.toFloat(); val phone = maxWidth.value <= 640f
        Row(Modifier.align(align).padding(4.dp).width(225.dp).height(androidx.compose.foundation.layout.IntrinsicSize.Min).then(panel).onGloballyPositioned { menuBounds = it.boundsInRoot() }.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }.padding(3.dp)) {
            Column(
                Modifier.width(28.dp).fillMaxHeight().background(Brush.verticalGradient(listOf(v.title1, v.title2))).padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
            ) {
                "REVERENDER".forEach { BasicText(it.toString(), style = TextStyle(color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp, shadow = Shadow(Color.Black, Offset(1f, 1f), 0f))) }
                Spacer(Modifier.height(10.dp))
                "95".forEach { BasicText(it.toString(), style = TextStyle(color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp, shadow = Shadow(Color.Black, Offset(1f, 1f), 0f))) }
            }
            Column(Modifier.weight(1f).padding(3.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                AppGroup.entries.filter { it.inStartMenu }.forEach { g -> Box(Modifier.onGloballyPositioned { rowBounds[g] = it.boundsInRoot() }) { ClassicItem("${g.icon} ${g.label}", arrow = true, hot = flyout == g) { phoneFly = false; flyout = if (flyout == g) null else g } } }
                Box(Modifier.onGloballyPositioned { phoneRow = it.boundsInRoot() }) { ClassicItem("📱 Phone Apps", arrow = true, hot = phoneFly) { flyout = null; phoneFly = !phoneFly } }
                ClassicItem("⚙️ Settings") { wm.toggleFromStart("settings") }
                ClassicItem("🎨 Customize") { wm.toggleFromStart("customize") }
                ClassicItem("❓ Help") { wm.toggleFromStart("help") }
                Box(Modifier.fillMaxWidth().padding(vertical = 2.dp).height(2.dp).drawBehind {
                    drawRect(v.dark, Offset.Zero, Size(size.width, size.height / 2)); drawRect(v.light, Offset(0f, size.height / 2), Size(size.width, size.height / 2))
                })
                ClassicItem("📼 Player Only Reset") { wm.playerOnlyReset() }
                val ctx = androidx.compose.ui.platform.LocalContext.current
                if (com.reverender.r95.BuildConfig.FULL) ClassicItem("⬇ " + com.reverender.r95.core.Updater.status.ifEmpty { "Update" }) { com.reverender.r95.core.Updater.start(ctx) }
            }
        }
        flyout?.let { g ->
            val item = rowBounds[g]?.translate(-areaOrigin) ?: return@let
            Column(
                Modifier.startFlyout(item, menuBounds.translate(-areaOrigin), areaW, areaH, phone).onGloballyPositioned { subBounds = it.boundsInRoot() }.width(225.dp).then(panel)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                    .verticalScroll(rememberScrollState()).padding(3.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Apps.all.filter { it.group == g }.forEach { app ->
                    val open = wm.windows.any { it.app.id == app.id && !it.minimized }
                    if (g == AppGroup.VISUALS) VizHoldRow(app, { preview = it }, { com.reverender.r95.audio.UiSfx.play(); wm.toggleFromStart(app.id) }) { ClassicItem("${app.icon} ${app.title}", openBadge = open) {} }
                    else ClassicItem("${app.icon} ${app.title}", openBadge = open) { com.reverender.r95.audio.UiSfx.play(); wm.toggleFromStart(app.id) }
                }
            }
        }
        if (phoneFly) Column(
            Modifier.startFlyout(phoneRow.translate(-areaOrigin), menuBounds.translate(-areaOrigin), areaW, areaH, phone).width(230.dp).then(panel)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }.padding(3.dp),
        ) { com.reverender.r95.ui.apps.PhoneAppsList({ phoneFly = false; wm.startOpen = false }) }
        preview?.let { (app, item) -> VizPreviewShell(app, item.translate(-areaOrigin), subBounds.translate(-areaOrigin), areaW, areaH) }
    }
}

/** .start-item: 11px 800, hover/sub-open = title-active-1 with a cyan border; open apps show an "OPEN" badge. */
@Composable
private fun ClassicItem(label: String, arrow: Boolean = false, hot: Boolean = false, openBadge: Boolean = false, onClick: () -> Unit) {
    var press by remember { mutableStateOf(false) }
    val click by rememberUpdatedState(onClick)
    val v = Skin.vars
    val lit = press || hot
    Row(
        Modifier.fillMaxWidth()
            .background(if (lit) v.title1 else if (openBadge) Color.Black.copy(alpha = 0.45f) else Color.Transparent)
            .then(if (lit) Modifier.drawBehind { drawRect(v.cyan, size = size, style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx())) } else Modifier)
            .pointerInput(Unit) { detectTapGestures(onPress = { press = true; tryAwaitRelease(); press = false }, onTap = { click() }) }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        T(label, size = 12.sp, bold = true, maxLines = 1, color = if (lit) Color.White else if (openBadge) Color(0xFF9E9E9E) else Color.Black, modifier = Modifier.weight(1f))
        if (openBadge) Box(Modifier.background(Color.Black.copy(alpha = 0.65f)).padding(horizontal = 3.dp, vertical = 1.dp)) { T("OPEN", size = 8.sp, bold = true, mono = true, color = v.cyan) }
        if (arrow) T("▶", size = 9.sp, color = if (lit) Color.White else Color.Black)
    }
}

/** #hm-clock-wrap: the neon clock of the classic desktop (hmClockMove 3.4s, hmColorCycle 1.8s). */
@Composable
internal fun HmClock(modifier: Modifier = Modifier, style: String = "neon", alignStart: Boolean = false) {
    val tr = rememberInfiniteTransition(label = "hm")
    val move by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(3400, easing = LinearEasing)), label = "move")
    val col by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(1800, easing = LinearEasing)), label = "col")
    var now by remember { mutableStateOf(Date()) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) { com.reverender.r95.core.AppVisibility.awaitForeground(); now = Date(); kotlinx.coroutines.delay(1000) }
    }
    // keyframes 0/25/50/75/100% with ease-in-out between them
    val keys = arrayOf(floatArrayOf(-4.5f, -8f, -6f, 1f), floatArrayOf(2.5f, 6f, 3f, 1.06f), floatArrayOf(5f, -5f, -4f, 0.97f), floatArrayOf(-2f, 8f, 5f, 1.05f), floatArrayOf(-4.5f, -8f, -6f, 1f))
    val seg = (move * 4).toInt().coerceIn(0, 3); val f0 = move * 4 - seg; val f = f0 * f0 * (3 - 2 * f0)
    fun k(i: Int) = keys[seg][i] + (keys[seg + 1][i] - keys[seg][i]) * f
    // colour: cyan → yellow → cyan with swapped shadows
    val t = if (col < 0.5f) col * 2 else (1 - col) * 2
    val main = lerpColor(Color(0xFF00F7FF), Color(0xFFFFEA00), t)
    val shA = lerpColor(Color(0xFFFF00A0), Color(0xFF00F7FF), t)
    val shC = lerpColor(Color(0xFF7700FF), Color(0xFFFF00A0), t)
    val ctx = androidx.compose.ui.platform.LocalContext.current
    Column(
        modifier.graphicsLayer { rotationZ = k(0); rotationY = k(1); scaleX = k(3); scaleY = k(3); cameraDistance = 400f * density },
        horizontalAlignment = if (alignStart) Alignment.Start else Alignment.End,
    ) {
        val time = com.reverender.r95.ui.viz.clockTimeString(ctx, now.time)
        if (style != "neon") com.reverender.r95.ui.viz.W95ClockTime(style, time, Modifier.padding(bottom = 4.dp)) else Box {
            val st = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, fontSize = 32.sp, letterSpacing = 3.sp)
            BasicText(time, style = st.copy(color = shC), modifier = Modifier.graphicsLayer { translationX = (if (t < 0.5f) -5f else 5f) * density; translationY = 5f * density })
            BasicText(time, style = st.copy(color = Color.Black), modifier = Modifier.graphicsLayer { translationX = 3f * density; translationY = 3f * density })
            BasicText(time, style = st.copy(color = shA), modifier = Modifier.graphicsLayer { translationX = -3f * density; translationY = -2f * density })
            BasicText(time, style = st.copy(color = main, shadow = Shadow(main, Offset.Zero, 18f)))
        }
        Box(
            Modifier.padding(top = 3.dp).drawBehind { drawRect(Color(0xFF00F7FF), Offset(3.dp.toPx(), 3.dp.toPx()), size) }
                .background(if (style == "tray") Color(0xFF000080) else Color.Black.copy(alpha = 0.78f)).drawBehind {
                    val bc = when (style) { "lcd" -> Color(0xFF6AFF4A); "dos", "tray" -> Color(0xFFC0C0C0); "chrome" -> Color(0xFFC9D2DB); else -> Skin.vars.pink }
                    drawRect(bc, size = size, style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
                }
                .padding(horizontal = 6.dp, vertical = 1.dp),
        ) { T("REVERENDER // 1989", color = if (style == "lcd") Color(0xFFA8FF70) else Color.White, size = 11.sp, bold = true, mono = true) }
    }
}

private fun lerpColor(a: Color, b: Color, t: Float) = Color(a.red + (b.red - a.red) * t, a.green + (b.green - a.green) * t, a.blue + (b.blue - a.blue) * t, 1f)

/** "DRAG CORNER TO RESIZE  ↘" (the phone variant of the site's hint), hidden on the first touch of the corner. */
@Composable
internal fun BoxScope.ResizeCornerHint(win: Win) {
    val until = win.resizeHintUntil
    if (until == 0L || win.maximized) return
    var show by remember(until) { mutableStateOf(System.currentTimeMillis() < until) }
    LaunchedEffect(until) { val d = until - System.currentTimeMillis(); if (d > 0) { kotlinx.coroutines.delay(d) }; show = false; win.resizeHintUntil = 0L }
    if (!show) return
    Box(
        Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 20.dp)
            .background(Color(0xF000070F), androidx.compose.foundation.shape.RoundedCornerShape(3.dp))
            .border(1.dp, Color(0xE600F7FF), androidx.compose.foundation.shape.RoundedCornerShape(3.dp))
            .padding(horizontal = 5.dp, vertical = 4.dp),
    ) {
        androidx.compose.foundation.text.BasicText(
            "DRAG CORNER TO RESIZE  ↘",
            style = androidx.compose.ui.text.TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 7.sp, letterSpacing = 0.2.sp, color = Color(0xFFC9FBFF),
                shadow = androidx.compose.ui.graphics.Shadow(Color(0xA600F7FF), androidx.compose.ui.geometry.Offset.Zero, 5f)),
        )
    }
}
