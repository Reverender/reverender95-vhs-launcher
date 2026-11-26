package com.reverender.r95.ui.desktop

import com.reverender.r95.ui.win95.bevel
import androidx.compose.foundation.interaction.collectIsPressedAsState

import androidx.compose.ui.layout.positionInRoot

import androidx.compose.ui.layout.layout

import androidx.compose.ui.layout.boundsInRoot

import androidx.compose.ui.layout.onGloballyPositioned

import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

import androidx.compose.ui.draw.drawWithContent

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.reverender.r95.LocalAppState
import com.reverender.r95.TaskbarPos
import com.reverender.r95.audio.BackroomsHum
import com.reverender.r95.audio.UiSfx
import com.reverender.r95.core.AppGroup
import com.reverender.r95.core.Apps
import com.reverender.r95.ui.apps.AppContent
import com.reverender.r95.ui.viz.BassGlitch
import com.reverender.r95.ui.viz.DesktopBackground
import com.reverender.r95.ui.viz.Scanlines
import com.reverender.r95.ui.viz.Scenes
import com.reverender.r95.ui.viz.Screensaver
import com.reverender.r95.ui.viz.rememberFrameTime
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

// ---------------------------------------------------------------- Longhorn chrome colours (from the site's CSS)

private object LH {
    val frameTop = Color(0xFF5A666C); val frameMid = Color(0xFF343E43); val frameLow = Color(0xFF2B3439); val frameBottom = Color(0xFF303A3E)
    val frameEdge = Color(0xFF020405)
    val titleActive = Color(0xFFE7F4F8); val titleInactive = Color(0xFF9FB0B8)
    val ctrl = Brush.verticalGradient(0f to Color(0xFF5B6B74), 0.5f to Color(0xFF2F3C43), 0.51f to Color(0xFF1A2328), 1f to Color(0xFF2C3A41))
    val close = Brush.verticalGradient(0f to Color(0xFFE29A8C), 0.5f to Color(0xFFB8432F), 0.51f to Color(0xFF8E2312), 1f to Color(0xFFB0412C))
    val taskbar = Color(0xFF10171B)
    val taskBtn = Brush.verticalGradient(0f to Color(0xFF3A4850), 0.5f to Color(0xFF222D33), 0.51f to Color(0xFF151D21), 1f to Color(0xFF1F292E))
    val taskBtnActive = Brush.verticalGradient(0f to Color(0xFF6D8293), 0.5f to Color(0xFF3F5262), 0.51f to Color(0xFF2B3B48), 1f to Color(0xFF3A4E5E))
    val menuBg = Brush.verticalGradient(listOf(Color(0xFFF6F8F9), Color(0xFFE7ECEF)))
    val menuHot = Color(0xFFCFE0EE)
}

@Composable
fun DesktopScreen(wm: WindowManager, onShutDown: () -> Unit) {
    val state = LocalAppState.current
    val theme = state.theme
    val animated = state.animatedWallpaper && !state.lowPower
    var lastTouch by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var saverOn by remember { mutableStateOf(false) }
    // only one style runs at a time: when the desktop leaves (VHS chosen), everything it started stops
    androidx.compose.runtime.DisposableEffect(Unit) {
        com.reverender.r95.core.AppVisibility.setDesktop(true)
        onDispose {
            com.reverender.r95.core.AppVisibility.setDesktop(false)
            BackroomsHum.set(false)
            com.reverender.r95.ui.viz.site.SiteWallpaper.release()
            com.reverender.r95.core.Perf.desktopCovered = false
        }
    }
    LaunchedEffect(state.saverPreview) { if (state.saverPreview > 0) { kotlinx.coroutines.delay(150); saverOn = true } }
    var taskbarVisible by remember { mutableStateOf(true) }

    UiSfx.enabled = state.uiSfx
    // the active look follows the OS style, the Longhorn accent and the theme's classic CSS variables
    com.reverender.r95.ui.win95.Skin.style = state.osStyle
    com.reverender.r95.ui.win95.Skin.accent = com.reverender.r95.ui.win95.LH_ACCENTS.firstOrNull { it.id == state.lhAccent } ?: com.reverender.r95.ui.win95.LH_ACCENTS[2]
    com.reverender.r95.ui.win95.Skin.vars = com.reverender.r95.ui.win95.CLASSIC_THEMES[state.themeId] ?: com.reverender.r95.ui.win95.CLASSIC_THEMES.getValue("vaporwave")
    val scene = if (state.wallpaper >= 0) Scenes.byId(state.wallpaper) else null
    // the Backrooms hum belongs to the wallpaper: silent while the app is in the background
    LaunchedEffect(scene?.backrooms, state.muteHum, com.reverender.r95.core.AppVisibility.visible) {
        BackroomsHum.set(scene?.backrooms == true && !state.muteHum && com.reverender.r95.core.AppVisibility.visible)
    }

    LaunchedEffect(state.screensaver, state.screensaverMin, state.pinTaskbar, state.taskbarHideSec) {
        while (true) {
            delay(1000)
            com.reverender.r95.core.AppVisibility.awaitForeground()
            val idle = System.currentTimeMillis() - lastTouch
            if (state.screensaver && idle > state.screensaverMin * 60_000L) saverOn = true
            if (!state.pinTaskbar && idle > state.taskbarHideSec.coerceIn(1, 30) * 1000L && !wm.startOpen) taskbarVisible = false
        }
    }

    Box(
        Modifier.fillMaxSize().background(Color.Black).noirFilter(com.reverender.r95.ui.win95.Skin.vars.noir).pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                lastTouch = System.currentTimeMillis()
                taskbarVisible = true
                com.reverender.r95.core.Perf.touch(true)
                com.reverender.r95.ui.viz.site.SiteHost.uiNow()
                try {
                    do { val ev = awaitPointerEvent(PointerEventPass.Initial) } while (ev.changes.any { it.pressed })
                } finally { com.reverender.r95.core.Perf.touch(false); com.reverender.r95.ui.viz.site.SiteHost.uiNow() }
            }
        },
    ) {
        // wallpaper changes fade out and back in (the site's #wallpaper-img opacity transition)
        val target = Triple(state.osWallpaper, state.wallpaper, state.solidColor)
        var shown by remember { mutableStateOf(target) }
        val wpAlpha = remember { androidx.compose.animation.core.Animatable(1f) }
        LaunchedEffect(target) {
            if (target != shown) {
                wpAlpha.animateTo(0f, androidx.compose.animation.core.tween(350))
                shown = target
                wpAlpha.animateTo(1f, androidx.compose.animation.core.tween(350))
            }
        }
        Box(Modifier.fillMaxSize().graphicsLayer { alpha = wpAlpha.value }) {
            val osWp = shown.first
            if (osWp != null) com.reverender.r95.ui.viz.site.OsWallpaperLayer(osWp, state.theme.id, Modifier.fillMaxSize().wallpaperFx())
            else DesktopBackground(shown.second, shown.third, state.bgLayers.keys.toSet(), theme, animated, state.reactiveSun, Modifier.fillMaxSize())
        }
        // one tick of the website's animate() per frame: spectrum, smoothing, timers, visualizers
        LaunchedEffect(Unit) {
            var last = 0L
            while (true) {
                com.reverender.r95.core.AppVisibility.awaitForeground()
                androidx.compose.runtime.withFrameNanos { t ->
                    // paced to 60 fps (30 on weak devices / under load): the site's animations are timed for that
                    if (t - last >= com.reverender.r95.core.Perf.minStepNs) {
                        last = t
                        com.reverender.r95.core.Perf.onFrame(t)
                        com.reverender.r95.core.Perf.profileFrame()
                        com.reverender.r95.core.Perf.time(0) { com.reverender.r95.ui.viz.site.SiteHost.tick(t, state.theme.id, state.reactiveSun, state.bassGlitch && !state.lowPower) }
                    }
                }
            }
        }
        val covered = com.reverender.r95.core.Perf.desktopCovered
        if (!state.lowPower && !covered) VaporToysLayer(state.toys.keys.toSet(), Modifier.fillMaxSize())
        val siteWall = state.wallpaper in com.reverender.r95.ui.viz.site.SiteWallpaper.ids
        if (state.bassGlitch && !state.lowPower && !siteWall && !covered) BassGlitch(theme, Modifier.fillMaxSize())
        // wobbly windows run on the same frame clock as the site (one spring step per frame)
        LaunchedEffect(state.wobble, state.lowPower) {
            com.reverender.r95.ui.viz.site.SiteHost.onKick = { e -> if (state.wobble) wm.windows.forEach { if (!it.minimized) it.wobble.glitch(e) } }
            if (!state.wobble || state.lowPower) { wm.windows.forEach { it.wobble.reset() }; return@LaunchedEffect }
            var lastW = 0L
            while (true) {
                com.reverender.r95.core.AppVisibility.awaitForeground()
                val ft = androidx.compose.runtime.withFrameNanos { it }
                // springs always step at 60 fps (cheap), so a dragged or released window never stutters
                if (ft - lastW < 15_500_000L) continue
                lastW = ft
                val ws = wm.windows; for (i in 0 until ws.size) ws[i].wobble.step()
            }
        }

        val showBar = state.pinTaskbar || taskbarVisible || wm.startOpen
        val content: @Composable (Modifier) -> Unit = { mod ->
            BoxWithConstraints(mod) {
                val density = LocalDensity.current
                val wPx = with(density) { maxWidth.toPx() }
                val hPx = with(density) { maxHeight.toPx() }
                LaunchedEffect(wPx, hPx) { wm.areaW = wPx; wm.areaH = hPx; wm.density = density.density; wm.clampAll() }
                if (wm.areaW == 0f) { wm.areaW = wPx; wm.areaH = hPx; wm.density = density.density }

                // empty desktop: swipe up opens the Start menu, swipe down searches apps (windows on top get their touches first)
                val swipeCtx = LocalContext.current
                Box(Modifier.fillMaxSize().sharedPointerInput {
                    val min = 56.dp.toPx()
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = true)
                        var end = down.position
                        var consumedByOthers = false
                        while (true) {
                            val ev = awaitPointerEvent()
                            val ch = ev.changes.firstOrNull { it.id == down.id } ?: break
                            if (ch.isConsumed) consumedByOthers = true
                            end = ch.position
                            if (!ch.pressed) break
                        }
                        val dy = end.y - down.position.y; val dx = end.x - down.position.x
                        if (!consumedByOthers && kotlin.math.abs(dy) > min && kotlin.math.abs(dy) > kotlin.math.abs(dx) * 1.4f) {
                            if (dy < 0) { wm.searchOpen = false; wm.startOpen = true } else { wm.startOpen = false; wm.searchOpen = true }
                        } else if (!consumedByOthers && kotlin.math.abs(dx) > min * 1.3f && kotlin.math.abs(dx) > kotlin.math.abs(dy) * 1.6f) {
                            // sideways: the shortcut chosen in Settings → Desktop gestures
                            val t = if (dx < 0) com.reverender.r95.os.OsPrefs.swipeLeft else com.reverender.r95.os.OsPrefs.swipeRight
                            if (t.isNotEmpty()) com.reverender.r95.os.Launcher.openTarget(swipeCtx, t) { wm.open(it) }
                        }
                    }
                })
                // The site's phone desktop has no icons: wallpaper, the clock gadget and the pet.
                // #hm-clock-wrap (Windows 95) / #rr-lh-clock (Longhorn); moved away from a top or right taskbar
                if (state.desktopClock) {
                    val top = state.taskbarPos == TaskbarPos.TOP; val right = state.taskbarPos == TaskbarPos.RIGHT
                    val align = when { top && right -> Alignment.BottomStart; top -> Alignment.BottomEnd; right -> Alignment.TopStart; else -> Alignment.TopEnd }
                    if (com.reverender.r95.ui.win95.Skin.classic) {
                        Box(
                            Modifier.align(align).padding(if (top || right) PaddingValues(16.dp) else PaddingValues(top = 14.dp, end = 18.dp))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { wm.open("clock") },
                        ) { HmClock(style = state.w95Clock, alignStart = right) }
                    } else {
                        com.reverender.r95.ui.viz.AnalogClock(
                            com.reverender.r95.ui.viz.ClockFaces.face(state.clockFace), live = true,
                            Modifier.align(align).padding(10.dp).size(92.dp)
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { wm.open("clock") },
                        )
                    }
                }
                PetWalkerDriver(wm)
                PetWalkerLayer(wm, false)
                WindowsLayer(wm)
                PetWalkerLayer(wm, true)
                // the site's startup hints (in root coordinates, so offset by this area's position)
                var rootY by remember { mutableFloatStateOf(0f) }
                if (wm.startOpen) StartMenu(wm, onShutDown)
                if (wm.searchOpen) AppSearch(wm)
                // incoming Android notifications as Win95 / Longhorn dialogs
                com.reverender.r95.ui.apps.RetroAlertLayer()
            }
        }
        val shell = Modifier.fillMaxSize().safeDrawingPadding().imePadding().funPointerListener(LocalDensity.current.density)
        if (!state.pinTaskbar) {
            // auto-hide: the taskbar floats over the desktop and glides in and out (no relayout of the windows)
            val hide by androidx.compose.animation.core.animateFloatAsState(if (showBar) 0f else 1f,
                androidx.compose.animation.core.tween(340, easing = androidx.compose.animation.core.FastOutSlowInEasing), label = "taskbar")
            Box(shell) {
                content(Modifier.fillMaxSize())
                val pos = state.taskbarPos
                val vertical = pos == TaskbarPos.LEFT || pos == TaskbarPos.RIGHT
                val align = when (pos) { TaskbarPos.BOTTOM -> Alignment.BottomCenter; TaskbarPos.TOP -> Alignment.TopCenter; TaskbarPos.LEFT -> Alignment.CenterStart; TaskbarPos.RIGHT -> Alignment.CenterEnd }
                if (hide < 0.999f) Box(
                    Modifier.align(align).then(if (vertical) Modifier.fillMaxHeight() else Modifier.fillMaxWidth())
                        .graphicsLayer {
                            when (pos) {
                                TaskbarPos.BOTTOM -> translationY = size.height * hide
                                TaskbarPos.TOP -> translationY = -size.height * hide
                                TaskbarPos.LEFT -> translationX = -size.width * hide
                                TaskbarPos.RIGHT -> translationX = size.width * hide
                            }
                            alpha = 1f - hide * 0.4f
                        },
                ) { Taskbar(wm, vertical) }
            }
        } else when (state.taskbarPos) {
            TaskbarPos.BOTTOM -> Column(shell) { content(Modifier.weight(1f).fillMaxWidth()); if (showBar) Taskbar(wm, false) }
            TaskbarPos.TOP -> Column(shell) { if (showBar) Taskbar(wm, false); content(Modifier.weight(1f).fillMaxWidth()) }
            TaskbarPos.LEFT -> Row(shell) { if (showBar) Taskbar(wm, true); content(Modifier.weight(1f).fillMaxHeight()) }
            TaskbarPos.RIGHT -> Row(shell) { content(Modifier.weight(1f).fillMaxHeight()); if (showBar) Taskbar(wm, true) }
        }
        if (state.scanlines) Scanlines(Modifier.fillMaxSize())
        FunPointerFx(Modifier.fillMaxSize())
        if (saverOn) Screensaver(theme, Modifier.fillMaxSize()) { saverOn = false; lastTouch = System.currentTimeMillis() }
    }
}

/** true for a window that is completely hidden behind a maximized window (its visualizers pause too) */
val LocalWinHidden = androidx.compose.runtime.compositionLocalOf { false }

@Composable
private fun WindowsLayer(wm: WindowManager) {
    val state = LocalAppState.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val list = wm.windows.toList()
    // the top-most maximized window that is fully opaque (100 % window opacity, plain box shape) hides
    // everything below it: those windows stay alive (state, scroll, music) but skip drawing
    val coverIdx = if (state.windowOpacity < 0.999f) -1
        else list.indexOfLast { !it.minimized && it.maximized && it.shape == "box" }
    val coveredNow = coverIdx >= 0
    androidx.compose.runtime.SideEffect { if (com.reverender.r95.core.Perf.desktopCovered != coveredNow) com.reverender.r95.core.Perf.desktopCovered = coveredNow }
    androidx.compose.runtime.DisposableEffect(Unit) { onDispose { com.reverender.r95.core.Perf.desktopCovered = false } }
    for ((idx, win) in list.withIndex()) {
        if (win.minimized) continue
        val hidden = idx < coverIdx
        androidx.compose.runtime.key(win.key) {
          androidx.compose.runtime.CompositionLocalProvider(LocalWinHidden provides hidden) {
            val active = wm.top === win
            // --ui-scale: the site zooms every .win95-window (frame and contents) on smaller screens
            val zd = androidx.compose.ui.unit.Density(density.density * wm.uiScale, density.fontScale)
            val frameMod = if (win.maximized) Modifier.fillMaxSize() else Modifier
                .offset { IntOffset(win.x.roundToInt(), win.y.roundToInt()) }
                .requiredSize(with(zd) { win.w.toDp() }, with(zd) { win.h.toDp() })
            val onClose = { UiSfx.play(0.8f); wm.close(win) }
            val onMin = { UiSfx.play(1.2f); wm.minimize(win) }
            val onMax = { UiSfx.play(); wm.toggleMax(win) }
            val mod = frameMod
                    // under a maximized window: laid out and alive, but not drawn
                    .then(if (hidden) Modifier.drawWithContent { } else Modifier)
                    // window opacity works like the site's --rr-window-opacity: the frame and window surface turn
                    // see-through (every window), while text and controls stay solid
                    .wobbly(win.wobble, state.wobble && !state.lowPower)
                    // the window's own layer: a wobble or a move redraws a cached picture of it, not its contents
                    .graphicsLayer()
                    .clip(windowShape(win.shape))
                    .pointerInput(win.key) {
                        awaitEachGesture { awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial); wm.focus(win) }
                    }
            val onDrag: (Float, Float) -> Unit = { dx, dy ->
                    if (!win.maximized) {
                        win.x = (win.x + dx).coerceIn(-win.w + 80f * wm.density, wm.areaW - 80f * wm.density)
                        win.y = (win.y + dy).coerceIn(0f, wm.areaH - 30f * wm.density)
                        if (!win.wobble.dragging) { win.wobble.dragging = true; win.wobble.dragStart(); com.reverender.r95.core.Perf.interactive(true) }
                        win.wobble.dragMove((dx / wm.density).toDouble(), (dy / wm.density).toDouble())
                    }
                }
            val onDragEnd: () -> Unit = { win.wobble.dragging = false; win.wobble.dragEnd(); com.reverender.r95.core.Perf.interactive(false) }
            val onResize: (Float, Float) -> Unit = { dx, dy ->
                    com.reverender.r95.core.Perf.interactive(true); com.reverender.r95.core.Perf.interactive(false)
                    win.w = (win.w + dx).coerceIn(200f * wm.density, wm.areaW)
                    win.h = (win.h + dy).coerceIn(150f * wm.density, wm.areaH)
                }
            androidx.compose.runtime.CompositionLocalProvider(LocalDensity provides zd) {
                if (com.reverender.r95.ui.win95.Skin.classic) ClassicWindowFrame(win, active, mod, onClose, onMin, onMax, onDrag, onDragEnd, onResize) { ScaledBody(win, wm) { FitContent(win, wm) { AppContent(win.app, wm, win) } } }
                else WindowFrame(win, active, mod, onClose, onMin, onMax, onDrag, onDragEnd, onResize) { ScaledBody(win, wm) { FitContent(win, wm) { AppContent(win.app, wm, win) } } }
            }
          }
        }
    }
}

/** Longhorn window: dark slate glass frame, glossy title, grouped _ □ × controls with a red close. */
@Composable
private fun WindowFrame(
    win: Win, active: Boolean, modifier: Modifier,
    onClose: () -> Unit, onMinimize: () -> Unit, onMaximize: () -> Unit,
    onDrag: (Float, Float) -> Unit, onDragEnd: () -> Unit, onResize: (Float, Float) -> Unit,
    content: @Composable () -> Unit,
) {
    val drag by rememberUpdatedState(onDrag)
    val dragEnd by rememberUpdatedState(onDragEnd)
    val maxi by rememberUpdatedState(onMaximize)
    val shape = RoundedCornerShape(7.dp)
    val op = LocalAppState.current.windowOpacity.coerceIn(0.3f, 1f)
    Column(
        modifier
            .drawBehind {
                val r = CornerRadius(7.dp.toPx())
                drawRoundRect(Color.Black.copy(alpha = 0.35f * op), Offset(4.dp.toPx(), 4.dp.toPx()), size, r)
                val title = 28.dp.toPx()
                drawRoundRect(Brush.verticalGradient(0f to LH.frameTop.copy(alpha = LH.frameTop.alpha * op), (title / size.height) to LH.frameMid.copy(alpha = LH.frameMid.alpha * op),
                    (title + 1) / size.height to LH.frameLow.copy(alpha = LH.frameLow.alpha * op), 1f to LH.frameBottom.copy(alpha = LH.frameBottom.alpha * op)), cornerRadius = r)
                // glass highlight across the top half of the title bar
                drawRoundRect(Brush.verticalGradient(0f to Color.White.copy(alpha = 0.20f), 0.5f to Color.White.copy(alpha = 0.04f), 0.51f to Color.Transparent, endY = title), size = Size(size.width, title), cornerRadius = r)
                drawRoundRect(Color.White.copy(alpha = 0.14f), Offset(1f, 1f), Size(size.width - 2f, size.height - 2f), r, style = Stroke(1f))
            }
            .border(1.dp, LH.frameEdge, shape),
    ) {
        Row(
            Modifier.fillMaxWidth().height(28.dp)
                .pointerInput(win.key) { detectTapGestures(onDoubleTap = { maxi() }) }
                .pointerInput(win.key) { detectDragGestures(onDragEnd = { dragEnd() }, onDragCancel = { dragEnd() }) { c, d -> c.consume(); drag(d.x, d.y) } }
                .padding(start = 9.dp),
            verticalAlignment = Alignment.Top,
        ) {
            T(win.title(), color = if (active) LH.titleActive else LH.titleInactive, bold = true, size = 13.sp, maxLines = 1,
                modifier = Modifier.weight(1f).padding(top = 5.dp))
            Row {
                if (win.app.id in com.reverender.r95.core.VIZ_FX_WINDOWS) CtrlBtn("FX", LH.ctrl, RoundedCornerShape(bottomStart = 4.dp), { win.fxOpen = !win.fxOpen }, wide = true)
                CtrlBtn("_", LH.ctrl, RoundedCornerShape(bottomStart = 4.dp), onMinimize)
                CtrlBtn("□", LH.ctrl, RoundedCornerShape(0.dp), onMaximize)
                CtrlBtn("×", LH.close, RoundedCornerShape(bottomEnd = 4.dp, topEnd = 6.dp), onClose, wide = true)
            }
        }
        Box(Modifier.fillMaxWidth().weight(1f).padding(start = 4.dp, end = 4.dp, bottom = 4.dp, top = 2.dp).background(W95.Face.copy(alpha = op), RoundedCornerShape(2.dp))) {
            content()
            if (!win.maximized) {
                Box(
                    Modifier.align(Alignment.BottomEnd).size(26.dp)
                        .pointerInput(win.key) { detectDragGestures(onDragStart = { win.resizeHintUntil = 0L }) { c, d -> c.consume(); onResize(d.x, d.y) } },
                    contentAlignment = Alignment.BottomEnd,
                ) { T("◢", color = Color(0xFF5E6E78), size = 13.sp) }
            }
            ResizeCornerHint(win)
        }
    }
}

@Composable
private fun CtrlBtn(label: String, brush: Brush, shape: RoundedCornerShape, onClick: () -> Unit, wide: Boolean = false) {
    Box(
        Modifier.size(width = if (wide) 40.dp else 30.dp, height = 21.dp)
            .background(brush, shape).border(1.dp, Color.Black.copy(alpha = 0.6f), shape)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { T(label, color = Color(0xFFE7F4F8), size = if (label == "×") 15.sp else 12.sp, bold = true) }
}

// ---------------------------------------------------------------- taskbar

@Composable
private fun Taskbar(wm: WindowManager, vertical: Boolean) {
    val state = LocalAppState.current
    val player = state.player
    val context = LocalContext.current
    var lastVol by remember { mutableFloatStateOf(0.85f) }
    val tray: @Composable () -> Unit = {
        // speaker: tap to mute / unmute
        Box(Modifier.clickable { if (player.volume > 0f) { lastVol = player.volume; player.changeVolume(0f) } else player.changeVolume(lastVol) }.padding(4.dp)) {
            T(if (player.volume > 0f) "🔈" else "🔇", color = Color(0xFFD5E2EA), size = 13.sp)
        }
        // Revy star
        Box(
            Modifier.padding(horizontal = 3.dp).size(26.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF7B5CF0), Color(0xFF3B2A8C))), RoundedCornerShape(4.dp))
                .border(1.dp, Color(0xFFB6A5FF).copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                .clickable { wm.open("revy") },
            contentAlignment = Alignment.Center,
        ) { T("✦", color = Color(0xFFBFF4FF), size = 15.sp, bold = true) }
        Box(Modifier.clickable { com.reverender.r95.ui.apps.openPhoneClock(context) }.padding(horizontal = 4.dp)) { Clock() }
    }
    if (com.reverender.r95.ui.win95.Skin.classic) {
        ClassicTaskbar(wm, vertical) {
            ClassicTrayBox {
                Box(Modifier.clickable { if (player.volume > 0f) { lastVol = player.volume; player.changeVolume(0f) } else player.changeVolume(lastVol) }.padding(horizontal = 3.dp)) {
                    T(if (player.volume > 0f) "🔊" else "🔇", size = 13.sp)
                }
                Box(
                    Modifier.padding(horizontal = 3.dp).size(24.dp).background(Color(0xFF130F2E)).border(1.dp, Color(0xFF6E5BD6))
                        .clickable { wm.open("revy") },
                    contentAlignment = Alignment.Center,
                ) { T("✦", color = Color(0xFFBFF4FF), size = 14.sp, bold = true) }
                Box(Modifier.clickable { com.reverender.r95.ui.apps.openPhoneClock(context) }.padding(horizontal = 4.dp)) {
                    var now by remember { mutableStateOf(Date()) }
                    LaunchedEffect(Unit) { while (true) { com.reverender.r95.core.AppVisibility.awaitForeground(); now = Date(); delay(5_000) } }
                    T(SimpleDateFormat("hh:mm a", Locale.US).format(now), size = 13.sp, bold = true, color = Color.Black)
                }
            }
        }
        return
    }
    val bar = Modifier.background(LH.taskbar).drawBehind {
        drawLine(Color(0xFFD3E0EC).copy(alpha = 0.45f), Offset(0f, 0f), Offset(size.width, 0f), 1f)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.06f), Color.Transparent), 0f, size.height / 2), size = Size(size.width, size.height / 2))
    }
    if (!vertical) {
        Row(bar.fillMaxWidth().height(40.dp).padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            StartOrb(wm.startOpen) { UiSfx.play(); wm.startOpen = !wm.startOpen }
            Spacer(Modifier.width(6.dp))
            com.reverender.r95.ui.apps.PinnedApps(false)
            LazyRow(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(wm.windows.toList(), key = { it.key }) { win -> TaskButton(win.app.title.substringBefore(' ').ifEmpty { win.app.title }, wm.top === win) { wm.taskbarTap(win) } }
            }
            tray()
        }
    } else {
        Column(bar.fillMaxHeight().width(64.dp).padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            StartOrb(wm.startOpen) { UiSfx.play(); wm.startOpen = !wm.startOpen }
            Spacer(Modifier.height(6.dp))
            com.reverender.r95.ui.apps.PinnedApps(true)
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(wm.windows.toList(), key = { it.key }) { win -> TaskButton(win.app.icon, wm.top === win) { wm.taskbarTap(win) } }
            }
            tray()
        }
    }
}

/** #start-btn (Longhorn): a 96 × 28 pill with the orb and "Start"; on screens ≤ 520 px just the 34 px orb. */
@Composable
private fun StartOrb(open: Boolean, onClick: () -> Unit) {
    val wide = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp > 520
    val acc = com.reverender.r95.ui.win95.Skin.accent
    val src = remember { MutableInteractionSource() }
    val down by src.collectIsPressedAsState()
    Row(
        Modifier.padding(start = 2.dp, end = 8.dp).then(if (wide) Modifier.width(96.dp).height(28.dp) else Modifier.size(34.dp))
            .graphicsLayer { alpha = if (down) 0.85f else 1f }
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(listOf(acc.d, Color(0xFF121A1E), Color(0xFF0B1013)), 0f, Float.POSITIVE_INFINITY))
            .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.26f), Color.White.copy(alpha = 0.06f), Color.Transparent)))
            .border(1.dp, Color.White.copy(alpha = if (open) 0.3f else 0.16f), RoundedCornerShape(14.dp))
            .clickable(interactionSource = src, indication = null, onClick = onClick)
            .padding(start = if (wide) 3.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (wide) Arrangement.spacedBy(7.dp) else Arrangement.Center,
    ) {
        Canvas(Modifier.size(22.dp)) {
            val r = size.minDimension / 2
            drawCircle(acc.a.copy(alpha = if (open) 0.9f else 0.55f), r + 2f, center)
            drawCircle(Brush.sweepGradient(listOf(acc.l, acc.a, acc.d, acc.a, acc.l), center), r, center)
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.95f), Color.White.copy(alpha = 0.25f), Color.Transparent), Offset(center.x, center.y - r * 0.4f), r * 0.68f), r, center)
            drawCircle(Color.Black.copy(alpha = 0.7f), r, center, style = Stroke(1.5f))
        }
        if (wide) T("Start", color = Color.White, size = 13.sp, bold = true)
    }
}

@Composable
private fun TaskButton(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.height(30.dp).widthIn(min = 40.dp, max = 140.dp)
            .background(if (active) LH.taskBtnActive else LH.taskBtn, RoundedCornerShape(3.dp))
            .border(1.dp, Color.White.copy(alpha = if (active) 0.55f else 0.3f), RoundedCornerShape(3.dp))
            .clickable(onClick = onClick).padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) { T(label, color = Color(0xFFE7F4F8), size = 12.sp, maxLines = 1) }
}

@Composable
private fun Clock() {
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) { while (true) { com.reverender.r95.core.AppVisibility.awaitForeground(); now = Date(); delay(5_000) } }
    T(SimpleDateFormat("h:mm a", Locale.US).format(now), color = Color(0xFFE7F4F8), size = 12.sp, bold = true)
}

// ---------------------------------------------------------------- Start menu (Longhorn, with flyout submenus)

@Composable
private fun StartMenu(wm: WindowManager, onShutDown: () -> Unit) {
    val state = LocalAppState.current
    var flyout by remember { mutableStateOf<AppGroup?>(null) }
    val top = state.taskbarPos == TaskbarPos.TOP
    val align = if (top) Alignment.TopStart else if (state.taskbarPos == TaskbarPos.RIGHT) Alignment.BottomEnd else Alignment.BottomStart
    val panelShape = RoundedCornerShape(7.dp)
    val panel = Modifier.background(LH.menuBg, panelShape).border(1.dp, LH.frameEdge, panelShape)
    if (com.reverender.r95.ui.win95.Skin.classic) { ClassicStartMenu(wm, align); return }
    val rowBounds = remember { HashMap<AppGroup, androidx.compose.ui.geometry.Rect>() }
    var areaOrigin by remember { mutableStateOf(Offset.Zero) }
    var menuBounds by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }
    var subBounds by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }
    var preview by remember { mutableStateOf<Pair<com.reverender.r95.core.AppInfo, androidx.compose.ui.geometry.Rect>?>(null) }
    var phoneFly by remember { mutableStateOf(false) }
    var phoneRow by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }
    BoxWithConstraints(Modifier.fillMaxSize().onGloballyPositioned { areaOrigin = it.positionInRoot() }.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { wm.startOpen = false }) {
        val areaW = constraints.maxWidth.toFloat(); val areaH = constraints.maxHeight.toFloat()
        val phone = maxWidth.value <= 640f
        Row(
            Modifier.align(align).padding(4.dp).width(214.dp).height(androidx.compose.foundation.layout.IntrinsicSize.Min).then(panel)
                .onGloballyPositioned { menuBounds = it.boundsInRoot() }
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { },
        ) {
            // .start-sidebar: ＲＥＶＥＲＥＮＤＥＲ ９５
            Column(
                Modifier.width(26.dp).fillMaxHeight().background(Brush.verticalGradient(listOf(Color(0xFF2C3A47), Color(0xFF11191D))), RoundedCornerShape(topStart = 7.dp, bottomStart = 7.dp))
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                "REVERENDER".forEach { T(it.toString(), color = Color.White, bold = true, size = 12.sp) }
                Spacer(Modifier.height(10.dp))
                "95".forEach { T(it.toString(), color = Color.White, bold = true, size = 12.sp) }
            }
            Column(Modifier.weight(1f).padding(vertical = 6.dp)) {
                AppGroup.entries.filter { it.inStartMenu }.forEach { g ->
                    Box(Modifier.onGloballyPositioned { rowBounds[g] = it.boundsInRoot() }) {
                        MenuRow(g.icon, g.label, arrow = true, hot = flyout == g) { phoneFly = false; flyout = if (flyout == g) null else g }
                    }
                }
                // native: the phone's installed apps (Win95 Home Launcher Mode)
                Box(Modifier.onGloballyPositioned { phoneRow = it.boundsInRoot() }) {
                    MenuRow("📱", "Phone Apps", arrow = true, hot = phoneFly) { flyout = null; phoneFly = !phoneFly }
                }
                MenuRow("⚙️", "Settings") { wm.toggleFromStart("settings") }
                MenuRow("🎨", "Customize") { wm.toggleFromStart("customize") }
                MenuRow("❓", "Help") { wm.toggleFromStart("help") }
                Box(Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp).height(1.dp).background(Color(0xFFC3CCD1)))
                MenuRow("📼", "Player Only Reset") { wm.playerOnlyReset() }
                val ctx = androidx.compose.ui.platform.LocalContext.current
                if (com.reverender.r95.BuildConfig.FULL) MenuRow("⬇", com.reverender.r95.core.Updater.status.ifEmpty { "Update" }) { com.reverender.r95.core.Updater.start(ctx) }
            }
        }
        flyout?.let { g ->
            val item = rowBounds[g]?.translate(-areaOrigin) ?: return@let
            val menu = menuBounds.translate(-areaOrigin)
            Column(
                Modifier.startFlyout(item, menu, areaW, areaH, phone).onGloballyPositioned { subBounds = it.boundsInRoot() }
                    .width(206.dp).then(panel)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                    .verticalScroll(rememberScrollState()).padding(vertical = 4.dp),
            ) {
                Apps.all.filter { it.group == g }.forEach { app ->
                    val isOpen = wm.windows.any { it.app.id == app.id && !it.minimized }
                    if (g == AppGroup.VISUALS) VizHoldRow(app, { preview = it }, { UiSfx.play(); wm.toggleFromStart(app.id) }) { MenuRow(app.icon, app.title, hot = isOpen) {} }
                    else MenuRow(app.icon, app.title, hot = isOpen) { UiSfx.play(); wm.toggleFromStart(app.id) }
                }
            }
        }
        if (phoneFly) Column(
            Modifier.startFlyout(phoneRow.translate(-areaOrigin), menuBounds.translate(-areaOrigin), areaW, areaH, phone)
                .width(220.dp).then(panel)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                .padding(vertical = 4.dp),
        ) { com.reverender.r95.ui.apps.PhoneAppsList({ phoneFly = false; wm.startOpen = false }) }
        preview?.let { (app, item) -> VizPreviewShell(app, item.translate(-areaOrigin), subBounds.translate(-areaOrigin), areaW, areaH) }
    }
}

@Composable
private fun MenuRow(icon: String, label: String, arrow: Boolean = false, hot: Boolean = false, onClick: () -> Unit) {
    var press by remember { mutableStateOf(false) }
    val click by rememberUpdatedState(onClick)
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 3.dp)
            .background(if (press || hot) LH.menuHot else Color.Transparent, RoundedCornerShape(3.dp))
            .pointerInput(Unit) { detectTapGestures(onPress = { press = true; tryAwaitRelease(); press = false }, onTap = { click() }) }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        T(icon, size = 13.sp)
        Spacer(Modifier.width(8.dp))
        T(label, size = 13.sp, maxLines = 1, modifier = Modifier.weight(1f))
        if (arrow) T("▸", size = 10.sp, color = W95.Shadow)
    }
}


private object WobbleMats { val m = android.graphics.Matrix(); val sk = android.graphics.Matrix(); val v = FloatArray(9) }

/** Applies a window's wobble as the site does: transform-origin centre, rotateZ · skew · scale. */
private fun Modifier.wobbly(w: Wobble, enabled: Boolean): Modifier = if (!enabled) this else this.drawWithContent {
    w.tick
    if (!w.moving) { drawContent(); return@drawWithContent }
    val m = WobbleMats.m; m.reset()
    val cx = size.width / 2f; val cy = size.height / 2f
    m.postTranslate(-cx, -cy)
    m.postScale(w.scaleX.toFloat(), w.scaleY.toFloat())
    val sk = WobbleMats.sk; val v = WobbleMats.v
    v[0] = 1f; v[1] = Math.tan(Math.toRadians(w.skewX)).toFloat(); v[2] = 0f; v[3] = Math.tan(Math.toRadians(w.skewY)).toFloat(); v[4] = 1f; v[5] = 0f; v[6] = 0f; v[7] = 0f; v[8] = 1f
    sk.setValues(v)
    m.postConcat(sk)
    m.postRotate(w.rot.toFloat())
    m.postTranslate(cx, cy)
    drawIntoCanvas { c -> c.nativeCanvas.save(); c.nativeCanvas.concat(m); drawContent(); c.nativeCanvas.restore() }
}


/** CSS [data-shape] clip paths from the site. */
private fun windowShape(name: String): androidx.compose.ui.graphics.Shape = when (name) {
    "octagon" -> androidx.compose.foundation.shape.GenericShape { sz, _ ->
        val k = 18f * android.content.res.Resources.getSystem().displayMetrics.density // 18 CSS px
        moveTo(k, 0f); lineTo(sz.width - k, 0f); lineTo(sz.width, k); lineTo(sz.width, sz.height - k)
        lineTo(sz.width - k, sz.height); lineTo(k, sz.height); lineTo(0f, sz.height - k); lineTo(0f, k); close()
    }
    "crt" -> RoundedCornerShape(22.dp)
    "trapezoid" -> androidx.compose.foundation.shape.GenericShape { sz, _ ->
        moveTo(0f, 0f); lineTo(sz.width, 0f); lineTo(sz.width * 0.94f, sz.height); lineTo(sz.width * 0.06f, sz.height); close()
    }
    else -> androidx.compose.ui.graphics.RectangleShape
}


/** B&W NOIR theme: the site sets filter: grayscale(100%) contrast(110%) on the whole page. */
private val noirPaint by lazy {
    val cm = android.graphics.ColorMatrix().apply { setSaturation(0f) }
    val k = 1.1f; val o = (0.5f - 0.5f * k) * 255f
    cm.postConcat(android.graphics.ColorMatrix(floatArrayOf(k, 0f, 0f, 0f, o, 0f, k, 0f, 0f, o, 0f, 0f, k, 0f, o, 0f, 0f, 0f, 1f, 0f)))
    android.graphics.Paint().apply { colorFilter = android.graphics.ColorMatrixColorFilter(cm) }
}
private fun Modifier.noirFilter(on: Boolean): Modifier = if (!on) this else this.drawWithContent {
    val paint = noirPaint
    drawIntoCanvas { c ->
        c.nativeCanvas.saveLayer(0f, 0f, size.width, size.height, paint)
        drawContent()
        c.nativeCanvas.restore()
    }
}


/** clampMenusToViewport(): a submenu beside its item (above it on ≤ 640 px screens), kept on screen. */
internal fun Modifier.startFlyout(item: androidx.compose.ui.geometry.Rect, menu: androidx.compose.ui.geometry.Rect, areaW: Float, areaH: Float, phone: Boolean): Modifier =
    this.layout { measurable, c ->
        val e = 6 * density
        val pl = measurable.measure(c.copy(minWidth = 0, minHeight = 0, maxHeight = (areaH - 2 * e).toInt().coerceAtLeast(0)))
        layout(c.maxWidth, c.maxHeight) {
            val w = pl.width; val h = pl.height
            val x = if (areaW - menu.right >= w + e || areaW - menu.right >= menu.left) menu.right else menu.left - w
            var y = if (phone) item.top - h else item.bottom + 4 * density - h
            if (y + h > areaH - e) y = areaH - h - e
            if (y < e) y = e
            pl.place(x.toInt().coerceIn(0, (areaW - w).toInt().coerceAtLeast(0)), y.toInt())
        }
    }


/** Windows whose content is a form: on opening they take exactly the height their content needs (capped to the screen). */
private val FIT_APPS = setOf("calc", "sticky", "planner", "todo", "focus", "clock", "android", "eq", "fx", "cassette", "drum909", "synth95", "vaporpet")

/** Measures the content's natural height once after the window opens and resizes the window to fit it. */
@Composable
private fun FitContent(win: Win, wm: WindowManager, content: @Composable () -> Unit) {
    androidx.compose.ui.layout.Layout({ Box(Modifier.fillMaxSize()) { content() } }, Modifier.fillMaxSize()) { ms, c ->
        val m = ms.first()
        if (!win.fitDone && !win.maximized && c.hasBoundedHeight && c.maxHeight > 0) {
            win.fitDone = true
            if (win.app.id in FIT_APPS) {
                val need = runCatching { m.minIntrinsicHeight(c.maxWidth) }.getOrNull()
                if (need != null && need > 40 * density) wm.fitHeight(win, (need - c.maxHeight).toFloat())
            }
        }
        val p = m.measure(c)
        layout(p.width, p.height) { p.place(0, 0) }
    }
}


/** rr-viz-menu-preview: press and hold (470 ms) a visualizer item to see it live; releasing closes the preview without opening it. */
@Composable
internal fun VizHoldRow(app: com.reverender.r95.core.AppInfo, onPreview: (Pair<com.reverender.r95.core.AppInfo, androidx.compose.ui.geometry.Rect>?) -> Unit, onOpen: () -> Unit, row: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    var bounds by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }
    val open by rememberUpdatedState(onOpen); val pv by rememberUpdatedState(onPreview)
    Box(
        Modifier.onGloballyPositioned { bounds = it.boundsInRoot() }.pointerInput(app.id) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                var shown = false; var cancelled = false
                val job = scope.launch { kotlinx.coroutines.delay(470); shown = true; pv(app to bounds) }
                while (true) {
                    val ev = awaitPointerEvent(PointerEventPass.Initial)
                    val c = ev.changes.firstOrNull { it.id == down.id } ?: break
                    if (!c.pressed) {
                        job.cancel()
                        if (shown) { pv(null); c.consume() } else if (!cancelled) { c.consume(); open() }
                        break
                    }
                    if (!shown && (c.position - down.position).getDistance() > 14 * density) { cancelled = true; job.cancel() }
                    if (shown) c.consume()
                }
            }
        },
    ) { row() }
}

/** #rr-viz-preview-shell, placed like the site's A(): beside the submenu where there is room, else centred above/below. */
@Composable
internal fun VizPreviewShell(app: com.reverender.r95.core.AppInfo, item: androidx.compose.ui.geometry.Rect, sub: androidx.compose.ui.geometry.Rect, areaW: Float, areaH: Float) {
    val d = LocalDensity.current.density
    val e = areaW / d; val a = areaH / d
    val small = e < 560
    val f = if (small) 204f else 292f; val x = if (small) 154f else 194f; val v = 10f
    val w = maxOf(0f, sub.left / d - v * 2); val dd = maxOf(0f, e - sub.right / d - v * 2)
    val m = minOf(x, maxOf(116f, a - 16))
    val side = if (small) maxOf(w, dd) >= 150 else maxOf(w, dd) >= 220
    val sW: Float; val left: Float; val top: Float
    if (side) {
        if (dd >= w) { sW = minOf(f, dd); left = sub.right / d + v } else { sW = minOf(f, w); left = sub.left / d - sW - v }
        top = (item.top / d - minOf(36f, m * 0.22f)).coerceIn(8f, maxOf(8f, a - m - 8))
    } else {
        sW = minOf(if (small) 250f else 280f, e - 16); left = (e - sW) / 2
        top = if (item.top / d > a / 2) 8f else maxOf(8f, a - m - 8)
    }
    val W = maxOf(144f, sW); val H = maxOf(116f, m)
    val classic = com.reverender.r95.ui.win95.Skin.classic
    Box(
        Modifier.offset(left.coerceIn(8f, maxOf(8f, e - W - 8)).dp, top.coerceIn(8f, maxOf(8f, a - H - 8)).dp).size(W.dp, H.dp)
            .drawBehind { drawRect(Color.Black.copy(alpha = 0.62f), Offset(6 * density, 6 * density), size) }
            .background(com.reverender.r95.ui.win95.W95.Face).then(if (classic) Modifier.bevel(true) else Modifier.border(1.dp, LH.frameEdge)).padding(3.dp),
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxWidth().height(23.dp).background(if (classic) Brush.horizontalGradient(listOf(com.reverender.r95.ui.win95.Skin.vars.title1, com.reverender.r95.ui.win95.Skin.vars.title2)) else Brush.verticalGradient(listOf(LH.frameTop, LH.frameMid))).padding(horizontal = 5.dp), contentAlignment = Alignment.CenterStart) {
                T(app.windowTitle, color = Color.White, bold = true, size = 9.sp, maxLines = 1)
            }
            Spacer(Modifier.height(3.dp))
            com.reverender.r95.ui.apps.SiteCanvasPreview(app.id, Modifier.fillMaxWidth().weight(1f))
        }
        T("LIVE PREVIEW", color = Color(0xFFBFFFFF), bold = true, mono = true, size = 8.sp,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 4.dp, bottom = 3.dp).background(Color.Black.copy(alpha = 0.78f)).border(1.dp, Color(0xB800F7FF)).padding(horizontal = 4.dp, vertical = 2.dp))
    }
}


/** Container-query sizing (cqi): the player's and touch pad's contents grow and shrink with the window width, within the site's clamps. */
@Composable
private fun ScaledBody(win: Win, wm: WindowManager, content: @Composable () -> Unit) {
    val base = when (win.app.id) { "player" -> 462f; "emutouch" -> 390f; else -> 0f }
    if (base == 0f || win.maximized) { content(); return }
    val d = LocalDensity.current
    val cssW = win.w / (wm.density * wm.uiScale)
    val s = (cssW / base).coerceIn(0.73f, 1.25f)
    androidx.compose.runtime.CompositionLocalProvider(LocalDensity provides androidx.compose.ui.unit.Density(d.density * s, d.fontScale)) { content() }
}


// ---------------------------------------------------------------- swipe-down app search

/** Find any window or phone app by name; the first result opens with the keyboard's Go key. */
@Composable
private fun AppSearch(wm: WindowManager) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    var q by remember { mutableStateOf("") }
    val focus = remember { androidx.compose.ui.focus.FocusRequester() }
    LaunchedEffect(Unit) { com.reverender.r95.os.Launcher.load(ctx); runCatching { focus.requestFocus() } }
    val needle = q.trim().lowercase()
    val apps = Apps.all.filter { it.id != "emutouch" && (needle.isEmpty() || it.title.lowercase().contains(needle) || it.desc.lowercase().contains(needle)) }
    val phone = if (needle.isEmpty()) emptyList() else com.reverender.r95.os.Launcher.apps.filter { it.label.lowercase().contains(needle) }
    fun openFirst() {
        apps.firstOrNull()?.let { wm.searchOpen = false; wm.open(it.id); return }
        phone.firstOrNull()?.let { wm.searchOpen = false; com.reverender.r95.os.Launcher.launch(ctx, it) }
    }
    val classic = com.reverender.r95.ui.win95.Skin.classic
    Box(Modifier.fillMaxSize().background(Color(0x99000000)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { wm.searchOpen = false }) {
        Column(
            Modifier.align(Alignment.TopCenter).padding(top = 18.dp, start = 10.dp, end = 10.dp).widthIn(max = 460.dp).fillMaxWidth()
                .then(if (classic) Modifier.background(W95.Face).bevel(true) else Modifier.background(LH.menuBg, RoundedCornerShape(7.dp)).border(1.dp, LH.frameEdge, RoundedCornerShape(7.dp)))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                .padding(6.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                T("🔍", size = 16.sp); Spacer(Modifier.width(6.dp))
                androidx.compose.foundation.text.BasicTextField(
                    q, { q = it }, singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, color = Color.Black),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Go),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(onGo = { openFirst() }),
                    modifier = Modifier.weight(1f).background(Color.White).border(1.dp, W95.Shadow).padding(horizontal = 6.dp, vertical = 7.dp).focusRequester(focus),
                    decorationBox = { inner -> if (q.isEmpty()) T("Search apps…", size = 15.sp, color = W95.Shadow); inner() },
                )
            }
            Column(Modifier.padding(top = 6.dp).heightIn(max = 430.dp).verticalScroll(rememberScrollState())) {
                apps.take(40).forEach { a ->
                    Row(Modifier.fillMaxWidth().clickable { wm.searchOpen = false; wm.open(a.id) }.padding(horizontal = 6.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        T(a.icon, size = 16.sp); Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            T(a.title, size = 13.sp, bold = true, maxLines = 1)
                            T(a.desc.substringAfter("— "), size = 10.sp, color = W95.Shadow, maxLines = 1)
                        }
                    }
                }
                if (phone.isNotEmpty()) {
                    T("📱 Phone apps", size = 11.sp, bold = true, color = W95.Shadow, modifier = Modifier.padding(start = 6.dp, top = 6.dp))
                    phone.take(20).forEach { p -> com.reverender.r95.ui.apps.PhoneAppRow(p) { wm.searchOpen = false } }
                }
                if (apps.isEmpty() && phone.isEmpty()) T("Nothing found for \"$q\".", size = 12.sp, modifier = Modifier.padding(8.dp))
            }
        }
    }
}


/**
 * pointerInput that still lets the sibling below (the wallpaper's taps and ripples) get the same touches:
 * Compose normally hands a touch only to the top-most sibling.
 */
private fun Modifier.sharedPointerInput(block: suspend androidx.compose.ui.input.pointer.PointerInputScope.() -> Unit): Modifier = this then SharedPointerElement(block)

private class SharedPointerElement(val block: suspend androidx.compose.ui.input.pointer.PointerInputScope.() -> Unit) :
    androidx.compose.ui.node.ModifierNodeElement<SharedPointerNode>() {
    override fun create() = SharedPointerNode(block)
    override fun update(node: SharedPointerNode) {}
    override fun hashCode() = 7
    override fun equals(other: Any?) = other is SharedPointerElement
}

private class SharedPointerNode(block: suspend androidx.compose.ui.input.pointer.PointerInputScope.() -> Unit) :
    androidx.compose.ui.node.DelegatingNode(), androidx.compose.ui.node.PointerInputModifierNode {
    private val inner = delegate(androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode(block))
    override fun onPointerEvent(pointerEvent: androidx.compose.ui.input.pointer.PointerEvent, pass: androidx.compose.ui.input.pointer.PointerEventPass, bounds: androidx.compose.ui.unit.IntSize) =
        inner.onPointerEvent(pointerEvent, pass, bounds)
    override fun onCancelPointerInput() = inner.onCancelPointerInput()
    override fun sharePointerInputWithSiblings() = true
}
