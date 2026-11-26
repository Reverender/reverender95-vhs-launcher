package com.reverender.r95.ui.apps

import android.view.KeyEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reverender.r95.ui.win95.T
import com.swordfish.libretrodroid.GLRetroView
import kotlin.math.hypot

/** Shared between RETRO_EMU_95 and its separate touch-pad window (#win-emu-touch). */
object EmuTouch {
    /** the site's Le[] key: fceumm, snes9x, gambatte, mgba, n64 — null = no game (pad inactive) */
    var core by mutableStateOf<String?>(null)
    var view: GLRetroView? = null
    /** shuts the running core down cleanly (saves the battery RAM first); null when nothing runs */
    var stopper: (suspend () -> Unit)? = null
    fun key(action: Int, code: Int) { view?.sendKeyEvent(action, code) }
    fun motion(source: Int, x: Float, y: Float) { view?.sendMotionEvent(source, x, y) }
}

private val CYAN = Color(0xFF00F7FF)

/** One pad button: a RetroPad key, or a right-stick direction (the N64 C buttons). */
private class PadButton(val id: String, val label: String, val code: Int = 0, val axis: Pair<Float, Float>? = null)

private object PadKeys {
    val up = PadButton("up", "▲", KeyEvent.KEYCODE_DPAD_UP); val down = PadButton("down", "▼", KeyEvent.KEYCODE_DPAD_DOWN)
    val left = PadButton("left", "◀", KeyEvent.KEYCODE_DPAD_LEFT); val right = PadButton("right", "▶", KeyEvent.KEYCODE_DPAD_RIGHT)
    val start = PadButton("start", "START", KeyEvent.KEYCODE_BUTTON_START); val select = PadButton("select", "SELECT", KeyEvent.KEYCODE_BUTTON_SELECT)
    fun face(l: String, code: Int) = PadButton("f$l", l, code)
    val L = PadButton("L", "L", KeyEvent.KEYCODE_BUTTON_L1); val R = PadButton("R", "R", KeyEvent.KEYCODE_BUTTON_R1)
}

/** #win-emu-touch's body: ge() — the original console layout for the running core, multi-touch + slide. */
@Composable
fun EmuTouchApp() {
    val core = EmuTouch.core
    Box(Modifier.fillMaxSize().background(Color(0xFF070A16)).padding(5.dp)) {
        if (core == null) return@Box
        val n64 = core == "n64"
        val rects = remember { HashMap<String, Rect>() }
        val buttons = remember { HashMap<String, PadButton>() }
        val pressed = remember { mutableStateMapOf<String, Boolean>() }
        var dpadRect by remember { mutableStateOf(Rect.Zero) }
        var root by remember { mutableStateOf(Rect.Zero) }
        val held = remember { HashMap<PointerId, String>() }
        val cDirs = remember { HashSet<String>() }

        fun press(id: String, down: Boolean) {
            val b = buttons[id] ?: return
            if (down) pressed[id] = true else pressed.remove(id)
            if (b.axis != null) {
                if (down) cDirs.add(id) else cDirs.remove(id)
                var x = 0f; var y = 0f
                cDirs.forEach { k -> buttons[k]?.axis?.let { x += it.first; y += it.second } }
                EmuTouch.motion(GLRetroView.MOTION_SOURCE_ANALOG_RIGHT, x.coerceIn(-1f, 1f), y.coerceIn(-1f, 1f))
            } else EmuTouch.key(if (down) KeyEvent.ACTION_DOWN else KeyEvent.ACTION_UP, b.code)
        }
        /** ye(): the button under a finger; inside the d-pad the nearest arrow within 34 % of its size */
        fun hit(p: Offset): String? {
            rects.entries.firstOrNull { (id, r) -> !id.startsWith("d:") && r.contains(p) }?.let { return it.key }
            if (dpadRect.contains(p)) {
                val best = rects.entries.filter { it.key.startsWith("d:") }.minByOrNull { (_, r) -> hypot(p.x - r.center.x, p.y - r.center.y) } ?: return null
                val d = hypot(p.x - best.value.center.x, p.y - best.value.center.y)
                if (d <= maxOf(dpadRect.width, dpadRect.height) * 0.34f) return best.key
            }
            return null
        }
        @Composable
        fun B(b: PadButton, mod: Modifier, style: String) {
            val key = (if (b.id in listOf("up", "down", "left", "right")) "d:" else "") + b.id
            buttons[key] = b
            val on = pressed[key] == true
            val shape: Shape = when (style) { "face", "ab" -> CircleShape; "shoulder" -> RoundedCornerShape(7.dp, 7.dp, 4.dp, 4.dp); "z" -> RoundedCornerShape(7.dp, 7.dp, 12.dp, 12.dp); "c" -> RoundedCornerShape(9.dp); else -> RoundedCornerShape(4.dp) }
            val bg = when (style) {
                "face" -> Brush.radialGradient(listOf(Color(0x38FFFFFF), Color(0xCCFF00A0), Color(0xFA46004B)))
                "shoulder" -> Brush.verticalGradient(listOf(Color(0xFF596273), Color(0xFF20283A), Color(0xFF101522)))
                "z" -> Brush.verticalGradient(listOf(Color(0xFFEDB45B), Color(0xFF99501A)))
                "c" -> Brush.radialGradient(listOf(Color(0xFFFFF5A9), Color(0xFFD6A900), Color(0xFF775500)))
                "abA" -> Brush.radialGradient(listOf(Color(0xFFD3F2FF), Color(0xFF278ACB), Color(0xFF10365E)))
                "abB" -> Brush.radialGradient(listOf(Color(0xFFD9FFD0), Color(0xFF36A74C), Color(0xFF07531E)))
                "start64" -> Brush.verticalGradient(listOf(Color(0xFF8D969D), Color(0xFF3B444F)))
                else -> Brush.verticalGradient(listOf(Color(0xFF2A2F45), Color(0xFF151A2C)))
            }
            Box(
                mod.onGloballyPositioned { rects[key] = it.boundsInRoot() }
                    .graphicsLayer { if (on) { translationX = density; translationY = density } }
                    .clip(shape).background(bg).border(1.dp, if (on) CYAN else if (style == "c") Color(0xFFFFF0A2) else Color.White.copy(alpha = 0.75f), shape)
                    .then(if (on) Modifier.background(CYAN.copy(alpha = 0.18f)) else Modifier),
                contentAlignment = Alignment.Center,
            ) {
                T(b.label, color = if (style == "c") Color(0xFF352400) else if (style == "dpad") CYAN else Color.White, bold = true,
                    size = when (style) { "dpad" -> 20.sp; "face", "ab" -> 18.sp; "c" -> 12.sp; else -> 11.sp }, maxLines = 1)
            }
        }
        @Composable
        fun Dpad(mod: Modifier) {
            Column(mod.aspectRatio(1f).onGloballyPositioned { dpadRect = it.boundsInRoot() }, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                listOf(listOf(null, PadKeys.up, null), listOf(PadKeys.left, null, PadKeys.right), listOf(null, PadKeys.down, null)).forEach { row ->
                    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        row.forEach { b -> if (b == null) Spacer(Modifier.weight(1f).fillMaxHeight()) else B(b, Modifier.weight(1f).fillMaxHeight(), "dpad") }
                    }
                }
            }
        }

        Column(
            Modifier.fillMaxSize().onGloballyPositioned { root = it.boundsInRoot() }.pointerInput(core) {
                awaitPointerEventScope {
                    while (true) {
                        val ev = awaitPointerEvent()
                        ev.changes.forEach { c ->
                            val p = Offset(root.left + c.position.x, root.top + c.position.y)
                            val now = if (c.pressed) hit(p) else null
                            val was = held[c.id]
                            if (now != was) {
                                if (was != null && held.entries.count { it.value == was } == 1) press(was, false)
                                if (now != null) { if (held.values.none { it == now }) press(now, true); held[c.id] = now } else held.remove(c.id)
                            }
                            if (!c.pressed) held.remove(c.id)
                            c.consume()
                        }
                    }
                }
            },
        ) {
            T(if (n64) "N64 CONTROLLER // ANALOG STICK · D-PAD · C BUTTONS · MULTI-TOUCH" else "ORIGINAL CONSOLE LAYOUT // MULTI-TOUCH + SLIDE",
                color = CYAN, bold = true, mono = true, size = 9.sp, align = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), maxLines = 1)
            if (n64) {
                Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Column(Modifier.weight(0.92f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                        B(PadKeys.L, Modifier.fillMaxWidth().widthIn(max = 100.dp).height(32.dp), "shoulder")
                        Dpad(Modifier.widthIn(max = 132.dp).fillMaxWidth())
                    }
                    Column(Modifier.weight(0.9f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                        B(PadButton("Z", "Z", KeyEvent.KEYCODE_BUTTON_L2), Modifier.fillMaxWidth(0.88f).height(34.dp), "z")
                        T("ANALOG", color = Color(0xCC9BEBFF), mono = true, bold = true, size = 8.sp)
                        Column(Modifier.fillMaxWidth().border(1.dp, Color(0x9400EAFF), RoundedCornerShape(7.dp)).padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            T("ANALOG ON GAME VIEW", color = Color(0xFF00EAFF), bold = true, size = 8.sp)
                            T("Use the glowing touch zone on the left side of the emulator screen.", color = Color(0xFFB8E8F0), size = 8.sp, align = TextAlign.Center)
                        }
                        B(PadKeys.start, Modifier.fillMaxWidth(0.9f).height(30.dp), "start64")
                    }
                    Column(Modifier.weight(1.18f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                        B(PadKeys.R, Modifier.fillMaxWidth().widthIn(max = 100.dp).height(32.dp), "shoulder")
                        // C buttons on the right stick (mupen64plus-next's default)
                        Column(Modifier.widthIn(max = 150.dp).fillMaxWidth().aspectRatio(1.35f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Spacer(Modifier.weight(1f)); B(PadButton("cu", "C↑", axis = 0f to -1f), Modifier.weight(1f).fillMaxHeight(), "c"); Spacer(Modifier.weight(1f))
                            }
                            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                B(PadButton("cl", "C←", axis = -1f to 0f), Modifier.weight(1f).fillMaxHeight(), "c")
                                B(PadButton("cd", "C↓", axis = 0f to 1f), Modifier.weight(1f).fillMaxHeight(), "c")
                                B(PadButton("cr", "C→", axis = 1f to 0f), Modifier.weight(1f).fillMaxHeight(), "c")
                            }
                        }
                        // N64 B = RetroPad Y, N64 A = RetroPad B
                        Row(Modifier.widthIn(max = 150.dp).fillMaxWidth().height(52.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            B(PadButton("n64b", "B", KeyEvent.KEYCODE_BUTTON_Y), Modifier.weight(1f).fillMaxHeight().aspectRatio(1f, true), "abB")
                            B(PadButton("n64a", "A", KeyEvent.KEYCODE_BUTTON_B), Modifier.weight(1f).fillMaxHeight().aspectRatio(1f, true), "abA")
                        }
                    }
                }
            } else {
                val shoulders = core == "mgba" || core == "snes9x"
                if (shoulders) Row(Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    B(PadKeys.L, Modifier.fillMaxWidth(0.3f).widthIn(max = 94.dp).height(30.dp), "shoulder")
                    B(PadKeys.R, Modifier.fillMaxWidth(0.43f).widthIn(max = 94.dp).height(30.dp), "shoulder")
                }
                Row(Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) { Dpad(Modifier.widthIn(max = 160.dp).fillMaxWidth()) }
                    Column(Modifier.weight(0.5f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)) {
                        B(PadKeys.select, Modifier.fillMaxWidth().height(32.dp), "system")
                        B(PadKeys.start, Modifier.fillMaxWidth().height(32.dp), "system")
                    }
                    Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                        when (core) {
                            "snes9x" -> Column(Modifier.widthIn(max = 190.dp).fillMaxWidth().aspectRatio(1f)) {
                                Row(Modifier.weight(1f)) { Spacer(Modifier.weight(1f)); B(PadKeys.face("X", KeyEvent.KEYCODE_BUTTON_X), Modifier.weight(1f).fillMaxHeight(), "face"); Spacer(Modifier.weight(1f)) }
                                Row(Modifier.weight(1f)) { B(PadKeys.face("Y", KeyEvent.KEYCODE_BUTTON_Y), Modifier.weight(1f).fillMaxHeight(), "face"); Spacer(Modifier.weight(1f)); B(PadKeys.face("A", KeyEvent.KEYCODE_BUTTON_A), Modifier.weight(1f).fillMaxHeight(), "face") }
                                Row(Modifier.weight(1f)) { Spacer(Modifier.weight(1f)); B(PadKeys.face("B", KeyEvent.KEYCODE_BUTTON_B), Modifier.weight(1f).fillMaxHeight(), "face"); Spacer(Modifier.weight(1f)) }
                            }
                            "gambatte" -> Column(Modifier.widthIn(max = 170.dp).fillMaxWidth().aspectRatio(1.45f).rotate(-12f)) {
                                Row(Modifier.weight(1f)) { Spacer(Modifier.weight(1f)); B(PadKeys.face("A", KeyEvent.KEYCODE_BUTTON_A), Modifier.weight(1f).fillMaxHeight().aspectRatio(1f, true), "face") }
                                Row(Modifier.weight(1f)) { B(PadKeys.face("B", KeyEvent.KEYCODE_BUTTON_B), Modifier.weight(1f).fillMaxHeight().aspectRatio(1f, true), "face"); Spacer(Modifier.weight(1f)) }
                            }
                            else -> Row(Modifier.widthIn(max = 180.dp).fillMaxWidth().aspectRatio(2.05f).rotate(-12f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                // NES: the B button sits a little higher (face-layout-nes-two)
                                B(PadKeys.face("B", KeyEvent.KEYCODE_BUTTON_B), Modifier.weight(1f).fillMaxHeight().graphicsLayer { if (core == "fceumm") translationY = -size.height * 0.05f }, "face")
                                B(PadKeys.face("A", KeyEvent.KEYCODE_BUTTON_A), Modifier.weight(1f).fillMaxHeight(), "face")
                            }
                        }
                    }
                }
            }
        }
    }
}

/** The N64 analog zone on the left half of the game view (EmulatorJS' zone joystick). */
@Composable
fun BoxScope.N64AnalogZone() {
    var origin by remember { mutableStateOf<Offset?>(null) }
    var knob by remember { mutableStateOf(Offset.Zero) }
    Box(
        Modifier.align(Alignment.CenterStart).fillMaxHeight().fillMaxWidth(0.5f).pointerInput(Unit) {
            awaitPointerEventScope {
                var id: PointerId? = null
                while (true) {
                    val ev = awaitPointerEvent()
                    for (c in ev.changes) {
                        if (id == null && c.pressed) { id = c.id; origin = c.position; knob = Offset.Zero }
                        if (c.id != id) continue
                        if (c.pressed) {
                            val o = origin ?: c.position
                            val r = 50 * density
                            var d = c.position - o
                            val len = d.getDistance(); if (len > r) d = d * (r / len)
                            knob = d
                            EmuTouch.motion(GLRetroView.MOTION_SOURCE_ANALOG_LEFT, d.x / r, d.y / r)
                        } else {
                            id = null; origin = null; knob = Offset.Zero
                            EmuTouch.motion(GLRetroView.MOTION_SOURCE_ANALOG_LEFT, 0f, 0f)
                        }
                        c.consume()
                    }
                }
            }
        },
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val o = origin
            if (o == null) {
                // the glowing idle zone
                val c = Offset(size.width * 0.38f, size.height * 0.62f)
                drawCircle(Brush.radialGradient(listOf(Color(0x3300EAFF), Color.Transparent), c, 70 * density), 70 * density, c)
                drawCircle(Color(0x6600EAFF), 46 * density, c, style = androidx.compose.ui.graphics.drawscope.Stroke(2 * density))
            } else {
                drawCircle(Color(0x5500EAFF), 50 * density, o, style = androidx.compose.ui.graphics.drawscope.Stroke(2 * density))
                drawCircle(Color(0xAAC9FAFF), 20 * density, o + knob)
            }
        }
    }
}
