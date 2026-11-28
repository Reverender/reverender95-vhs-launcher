package com.reverender.r95.ui.vhs

import android.net.Uri
import android.provider.OpenableColumns
import android.view.KeyEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import com.reverender.r95.LocalAppState
import com.reverender.r95.net.Downloader
import com.reverender.r95.os.Haptics
import com.reverender.r95.ui.apps.EmuTouch
import com.reverender.r95.ui.apps.LoadedGame
import com.reverender.r95.ui.apps.N64AnalogZone
import com.reverender.r95.ui.apps.RetroSurface
import com.swordfish.libretrodroid.GLRetroView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.hypot

/**
 * GAME DECK: RETRO_EMU_95 in VHS clothes. Search the Internet Archive (or open a file) like the video deck,
 * then the game runs on the screen at the top with a console controller under it — the layout follows the
 * loaded system (NES, SNES, Game Boy / Color, GBA, N64), multi-touch with sliding between buttons.
 */
@Composable
internal fun VhsGame(onExit: () -> Unit, onSwitch: (String) -> Unit = {}) {
    val ctx = LocalContext.current
    val state = LocalAppState.current
    val scope = rememberCoroutineScope()
    val o = LocalOsd.current
    val openKb = LocalOpenKb.current
    val blink = rememberBlink()
    var query by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf<String?>("TYPE A GAME TITLE") }
    val hits = remember { mutableStateListOf<com.reverender.r95.net.ArchiveRom>() }
    var job by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    var game by remember { mutableStateOf<LoadedGame?>(null) }
    var searching by remember { mutableStateOf(true) }
    var status by remember { mutableStateOf("NO CARTRIDGE") }

    fun launch(romIn: File) {
        scope.launch {
            try {
                status = "LOADING…"; msg = "LOADING…"
                val rom = com.reverender.r95.ui.apps.prepareEmuRom(romIn)
                val sys = com.reverender.r95.ui.apps.systemFor(rom.extension)
                if (sys == null) { msg = "NOT A SUPPORTED GAME FILE"; status = "REJECTED"; return@launch }
                val bad = withContext(Dispatchers.IO) { com.reverender.r95.ui.apps.romProblem(rom) }
                if (bad != null) { runCatching { rom.delete() }; msg = vhsSafe(bad).take(60); status = "REJECTED"; return@launch }
                val core = com.reverender.r95.ui.apps.ensureEmuCore(ctx, sys.core) { m -> msg = vhsSafe(m); status = "CORE…" }
                state.player.duck()
                EmuTouch.stopper?.invoke()
                game = null
                game = LoadedGame(rom, sys, core)
                status = sys.badge.substringAfter("// ").trim()
                searching = false; msg = null
                Haptics.clunk("play")
            } catch (x: kotlinx.coroutines.CancellationException) { throw x }
            catch (e: Exception) { msg = vhsSafe("ERROR: " + (e.message ?: "COULD NOT START THE GAME")).take(60); status = "ERROR" }
        }
    }
    fun search() {
        if (com.reverender.r95.BuildConfig.PLAY) return  // no ROM search in the Google Play edition
        val t = com.reverender.r95.net.ArchiveRomSearch.parse(query)
        if (t.original.length < 2 || t.tokens.isEmpty()) { msg = "TYPE AT LEAST 2 LETTERS"; return }
        job?.cancel(); hits.clear(); Haptics.tick()
        msg = "SEARCHING THE ARCHIVE…"
        job = scope.launch {
            try {
                val (all, _) = com.reverender.r95.net.ArchiveRomSearch.search(t) { u -> scope.launch { hits.clear(); hits.addAll(u.take(30)); msg = null } }
                hits.clear(); hits.addAll(all.take(30))
                msg = if (all.isEmpty()) "NO GAMES FOUND" else null
            } catch (x: kotlinx.coroutines.CancellationException) { throw x }
            catch (x: Exception) { msg = "SEARCH FAILED. CHECK THE CONNECTION." }
        }
    }
    fun pickRom(url: String, name: String) {
        job?.cancel()
        scope.launch {
            try {
                val dest = File(ctx.filesDir, "roms/$name").apply { parentFile?.mkdirs() }
                if (!dest.exists()) Downloader.download(url, dest) { pr -> msg = "DOWNLOADING… ${(pr * 100).toInt()}%" }
                launch(dest)
            } catch (x: kotlinx.coroutines.CancellationException) { throw x }
            catch (e: Exception) { msg = "DOWNLOAD FAILED" }
        }
    }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            try {
                val name = withContext(Dispatchers.IO) {
                    ctx.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c -> if (c.moveToFirst()) c.getString(0) else null }
                } ?: "game.rom"
                val dest = File(ctx.filesDir, "roms/$name").apply { parentFile?.mkdirs() }
                withContext(Dispatchers.IO) { ctx.contentResolver.openInputStream(uri)!!.use { i -> dest.outputStream().use { i.copyTo(it) } } }
                launch(dest)
            } catch (e: Exception) { msg = "COULD NOT OPEN THAT FILE" }
        }
    }
    // test hook: a ROM pushed into the app's files folder
    androidx.compose.runtime.LaunchedEffect(Unit) {
        val f = VhsTestHooks.rom ?: return@LaunchedEffect
        VhsTestHooks.rom = null
        val dest = File(ctx.filesDir, "roms/" + f.name).apply { parentFile?.mkdirs() }
        withContext(Dispatchers.IO) { runCatching { f.copyTo(dest, true) } }
        android.util.Log.i("r95", "vhs game test: loading ${dest.name}")
        launch(dest)
    }
    var stopping by remember { mutableStateOf(false) }
    /** stop the core cleanly first, then take the cartridge out (and maybe leave the deck) */
    fun shutDown(then: () -> Unit = {}) {
        if (stopping) return
        stopping = true
        scope.launch {
            try { EmuTouch.stopper?.invoke() } finally {
                game = null; EmuTouch.core = null; status = "NO CARTRIDGE"; stopping = false
                then()
            }
        }
    }
    fun eject() { Haptics.clunk("eject"); searching = true; shutDown() }
    fun leave(f: () -> Unit) { if (game == null) f() else shutDown(f) }
    // the pad drives the running core through EmuTouch (as on the desktop)
    androidx.compose.runtime.LaunchedEffect(game) {
        EmuTouch.core = game?.let { when (it.system.core) { "fceumm" -> "fceumm"; "snes9x" -> "snes9x"; "gambatte" -> "gambatte"; "mgba" -> "mgba"; else -> "n64" } }
    }
    DisposableEffect(Unit) { onDispose { job?.cancel(); EmuTouch.core = null; EmuTouch.view = null } }
    // BACK on the game: to the cartridge search (the game keeps its place, paused). BACK there: stop and leave.
    androidx.activity.compose.BackHandler(enabled = game != null && searching) { leave(onExit) }
    androidx.activity.compose.BackHandler(enabled = game != null && !searching) { searching = true }

    Box(Modifier.fillMaxSize()) {
    val g = game
    if (g != null) {
    // the running game stays put (just moved off screen and paused) while you look for another cartridge
    Column(Modifier.fillMaxSize().offset { androidx.compose.ui.unit.IntOffset(if (searching) 100_000 else 0, 0) }
        .padding(horizontal = pxDp(2f), vertical = pxDp(2f)), verticalArrangement = Arrangement.spacedBy(pxDp(1.4f))) {
            // ---------------- the screen
            Box(Modifier.fillMaxWidth().weight(1f).background(Color.Black), contentAlignment = Alignment.Center) {
                key(g) { RetroSurface(g, Modifier.fillMaxSize(), onView = { EmuTouch.view = it }, onError = { status = "CORE ERROR $it" }, paused = searching || stopping) }
                if (EmuTouch.core == "n64") N64AnalogZone()
                Column(Modifier.align(Alignment.TopStart).padding(pxDp(1.5f))) { VhsText("▶" + status.take(10)) }
            }
            VhsText(vhsSafe(g.rom.nameWithoutExtension).trim().take(22))
            // ---------------- the controller for this console
            VhsPad(EmuTouch.core ?: "fceumm", Modifier.fillMaxWidth().weight(0.9f))
            Box(Modifier.fillMaxWidth().background(o.fg).padding(pxDp(1f))) {
                Row(Modifier.fillMaxWidth()) {
                    Box(Modifier.weight(1f).pointerInput(Unit) { detectTapGestures { eject() } }) { VhsText("▲EJECT", invert = true) }
                    Box(Modifier.weight(1f).pointerInput(Unit) { detectTapGestures { Haptics.tick(); searching = true } }) { VhsText("FIND", invert = true) }
                    Box(Modifier.pointerInput(Unit) { detectTapGestures { Haptics.tick(); leave(onExit) } }) { VhsText("MENU", invert = true) }
                }
            }
    }
    }
    if (searching || g == null) {
    Column(Modifier.fillMaxSize().padding(horizontal = pxDp(2f), vertical = pxDp(2f)), verticalArrangement = Arrangement.spacedBy(pxDp(1.4f))) {
            VhsHeader("GAME DECK", if (g != null) "NEW CARTRIDGE?" else "INSERT CARTRIDGE")
            VhsDeckSwitch("game") { m -> leave { onSwitch(m) } }
            Row(horizontalArrangement = Arrangement.spacedBy(pxDp(3f)), verticalAlignment = Alignment.CenterVertically) {
                VhsKey(if (com.reverender.r95.BuildConfig.PLAY) "FREE" else "ARCHIVE", selected = true) { }
                VhsKey("FILE", boxed = true) { picker.launch(arrayOf("*/*")) }
                if (g != null) VhsKey("◀GAME", boxed = true) { searching = false }
            }
            if (com.reverender.r95.BuildConfig.PLAY) {
                // Play edition: your own game files (FILE), and free homebrew from the makers' own pages
                VhsText("LOAD YOUR OWN GAME: FILE", color = o.fg.copy(alpha = 0.7f))
                VhsText("FREE HOMEBREW GAMES:", color = o.fg.copy(alpha = 0.6f))
                Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(pxDp(1.6f))) {
                    msg?.takeIf { it != "TYPE A GAME TITLE" }?.let { VhsText(it.take(22), color = o.fg.copy(alpha = 0.7f)) }
                    HOMEBREW.forEachIndexed { i, h ->
                        Column(Modifier.fillMaxWidth().pointerInput(h.page) { detectTapGestures {
                            Haptics.tick()
                            runCatching { ctx.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(h.page)).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) }
                        } }) {
                            VhsText("%02d ".format(i + 1) + h.title.take(19))
                            VhsText("   " + h.system + "  MAKER'S PAGE", color = o.fg.copy(alpha = 0.6f))
                        }
                    }
                    VhsText("DOWNLOAD IT, THEN FILE", color = o.fg.copy(alpha = 0.5f))
                }
                Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { Haptics.tick(); leave(onExit) } }) {
                    VhsText("TAP HERE TO RETURN", invert = true)
                }
            } else {
            RegisterKb(VhsKb({ query }, { query = it.take(30) }, label = "FIND A GAME") { search() })
            Box(Modifier.fillMaxWidth().pointerInput(Unit) { detectTapGestures { openKb() } }) {
                VhsText("FIND:" + query.takeLast(16) + if (blink) "_" else " ")
            }
            VhsText("NES SNES GB GBA N64", color = o.fg.copy(alpha = 0.6f))
            Column(Modifier.fillMaxWidth().weight(1f).then(if (hits.isNotEmpty()) Modifier.verticalScroll(rememberScrollState()) else Modifier), verticalArrangement = Arrangement.spacedBy(pxDp(1.6f))) {
                msg?.let { VhsText(it.take(22), color = o.fg.copy(alpha = 0.7f)) }
                if (query.isEmpty() && hits.isEmpty()) VhsText("SWIPE ▲ OR TAP FIND", color = o.fg.copy(alpha = 0.5f))
                hits.forEachIndexed { i, h ->
                    Column(Modifier.fillMaxWidth().pointerInput(h.url) { detectTapGestures { Haptics.clunk("play"); pickRom(h.url, h.name) } }) {
                        VhsText("%02d ".format(i + 1) + vhsSafe(h.title).trim().take(19))
                        VhsText("   ." + h.ext.uppercase() + (if (h.size > 0) "  " + "%.1fMB".format(java.util.Locale.US, h.size / 1048576.0) else ""), color = o.fg.copy(alpha = 0.6f))
                    }
                }
            }
            Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { Haptics.tick(); leave(onExit) } }) {
                Column {
                    VhsText("TAP A GAME TO LOAD IT", invert = true)
                    VhsText("TAP HERE TO RETURN", invert = true)
                }
            }
            }
    }
    }
    }
}

/**
 * Google Play edition: no ROM search. Free homebrew games whose makers give them away; a tap opens the
 * maker's own download page, then the downloaded file goes in with FILE.
 */
private class Homebrew(val title: String, val system: String, val page: String)
private val HOMEBREW = listOf(
    Homebrew("TOBU TOBU GIRL DX", "GB/GBC", "https://tangramgames.itch.io/tobu-tobu-girl-deluxe"),
    Homebrew("TOBU TOBU GIRL", "GB", "https://tangramgames.itch.io/tobutobugirl"),
    Homebrew("MICRO CITY", "GBC", "https://github.com/AntonioND/ucity/releases"),
    Homebrew("MICRO CITY ADVANCE", "GBA", "https://skylyrac.itch.io/ucity-advance"),
)

/** One pad button: a RetroPad key, or a right-stick direction (the N64 C buttons). */
private class VPad(val id: String, val label: String, val code: Int = 0, val axis: Pair<Float, Float>? = null)

/**
 * The console controller in OSD style: boxed dot-matrix buttons that light up when held. Multi-touch, and a
 * finger can slide from one button to the next (same hit-testing as the desktop's touch pad).
 */
@Composable
private fun VhsPad(core: String, modifier: Modifier) {
    val o = LocalOsd.current
    val rects = remember { HashMap<String, Rect>() }
    val buttons = remember { HashMap<String, VPad>() }
    val pressed = remember { mutableStateMapOf<String, Boolean>() }
    val held = remember { HashMap<PointerId, String>() }
    val cDirs = remember { HashSet<String>() }
    var dpad by remember { mutableStateOf(Rect.Zero) }
    var root by remember { mutableStateOf(Rect.Zero) }

    fun press(id: String, down: Boolean) {
        val b = buttons[id] ?: return
        if (down) { pressed[id] = true; Haptics.tick() } else pressed.remove(id)
        if (b.axis != null) {
            if (down) cDirs.add(id) else cDirs.remove(id)
            var x = 0f; var y = 0f
            cDirs.forEach { k -> buttons[k]?.axis?.let { x += it.first; y += it.second } }
            EmuTouch.motion(GLRetroView.MOTION_SOURCE_ANALOG_RIGHT, x.coerceIn(-1f, 1f), y.coerceIn(-1f, 1f))
        } else EmuTouch.key(if (down) KeyEvent.ACTION_DOWN else KeyEvent.ACTION_UP, b.code)
    }
    fun hit(p: Offset): String? {
        rects.entries.firstOrNull { (id, r) -> !id.startsWith("d:") && r.contains(p) }?.let { return it.key }
        if (dpad.contains(p)) {
            val best = rects.entries.filter { it.key.startsWith("d:") }.minByOrNull { (_, r) -> hypot(p.x - r.center.x, p.y - r.center.y) } ?: return null
            if (hypot(p.x - best.value.center.x, p.y - best.value.center.y) <= maxOf(dpad.width, dpad.height) * 0.34f) return best.key
        }
        return null
    }
    @Composable
    fun B(b: VPad, mod: Modifier, scale: Float = 1.3f) {
        val k = (if (b.id in listOf("up", "down", "left", "right")) "d:" else "") + b.id
        buttons[k] = b
        val on = pressed[k] == true
        Box(
            mod.onGloballyPositioned { rects[k] = it.boundsInRoot() }.drawBehind {
                val w = o.px * 0.5f
                if (on) {
                    glowRect(o.fg, Offset.Zero, size, o.px)
                    drawRect(o.fg)
                } else {
                    glowRect(o.fg, Offset(w / 2, w / 2), Size(size.width - w, size.height - w), o.px, w)
                    drawRect(o.fg, Offset(w / 2, w / 2), Size(size.width - w, size.height - w), style = Stroke(w))
                }
            },
            contentAlignment = Alignment.Center,
        ) { VhsText(b.label, scale = scale, invert = false, color = if (on) o.bg else null) }
    }
    @Composable
    fun Dpad(mod: Modifier) {
        Column(mod.aspectRatio(1f).onGloballyPositioned { dpad = it.boundsInRoot() }, verticalArrangement = Arrangement.spacedBy(pxDp(1f))) {
            listOf(
                listOf(null, VPad("up", "▲", KeyEvent.KEYCODE_DPAD_UP), null),
                listOf(VPad("left", "◀", KeyEvent.KEYCODE_DPAD_LEFT), null, VPad("right", "▶", KeyEvent.KEYCODE_DPAD_RIGHT)),
                listOf(null, VPad("down", "▼", KeyEvent.KEYCODE_DPAD_DOWN), null),
            ).forEach { row ->
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(pxDp(1f))) {
                    row.forEach { b -> if (b == null) Spacer(Modifier.weight(1f).fillMaxHeight()) else B(b, Modifier.weight(1f).fillMaxHeight(), 1.6f) }
                }
            }
        }
    }
    val sel = VPad("select", "SEL", KeyEvent.KEYCODE_BUTTON_SELECT)
    val start = VPad("start", "STA", KeyEvent.KEYCODE_BUTTON_START)
    fun face(l: String, code: Int) = VPad("f$l", l, code)

    Column(
        modifier.onGloballyPositioned { root = it.boundsInRoot() }.pointerInput(core) {
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
        verticalArrangement = Arrangement.spacedBy(pxDp(2f)),
    ) {
        val shoulders = core == "snes9x" || core == "mgba" || core == "n64"
        if (shoulders) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(3f))) {
            B(VPad("L", "L", KeyEvent.KEYCODE_BUTTON_L1), Modifier.weight(1f).height(pxDp(12f)))
            if (core == "n64") B(VPad("Z", "Z", KeyEvent.KEYCODE_BUTTON_L2), Modifier.weight(1f).height(pxDp(12f)))
            else Spacer(Modifier.weight(1f))
            B(VPad("R", "R", KeyEvent.KEYCODE_BUTTON_R1), Modifier.weight(1f).height(pxDp(12f)))
        }
        Row(Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(pxDp(3f))) {
            Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) { Dpad(Modifier.fillMaxWidth().widthIn(max = pxDp(56f))) }
            Column(Modifier.weight(0.55f), verticalArrangement = Arrangement.spacedBy(pxDp(3f), Alignment.CenterVertically)) {
                if (core != "n64") B(sel, Modifier.fillMaxWidth().height(pxDp(11f)), 1f)
                B(start, Modifier.fillMaxWidth().height(pxDp(11f)), 1f)
            }
            Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                when (core) {
                    "snes9x" -> Column(Modifier.fillMaxWidth().widthIn(max = pxDp(56f)).aspectRatio(1f), verticalArrangement = Arrangement.spacedBy(pxDp(1f))) {
                        Row(Modifier.weight(1f)) { Spacer(Modifier.weight(1f)); B(face("X", KeyEvent.KEYCODE_BUTTON_X), Modifier.weight(1f).fillMaxHeight()); Spacer(Modifier.weight(1f)) }
                        Row(Modifier.weight(1f)) { B(face("Y", KeyEvent.KEYCODE_BUTTON_Y), Modifier.weight(1f).fillMaxHeight()); Spacer(Modifier.weight(1f)); B(face("A", KeyEvent.KEYCODE_BUTTON_A), Modifier.weight(1f).fillMaxHeight()) }
                        Row(Modifier.weight(1f)) { Spacer(Modifier.weight(1f)); B(face("B", KeyEvent.KEYCODE_BUTTON_B), Modifier.weight(1f).fillMaxHeight()); Spacer(Modifier.weight(1f)) }
                    }
                    "n64" -> Column(Modifier.fillMaxWidth().widthIn(max = pxDp(60f)), verticalArrangement = Arrangement.spacedBy(pxDp(1.5f))) {
                        // C buttons on the right stick (mupen64plus-next's default); N64 B = RetroPad Y, N64 A = RetroPad B
                        Row(Modifier.fillMaxWidth().aspectRatio(3f)) { Spacer(Modifier.weight(1f)); B(VPad("cu", "C▲", axis = 0f to -1f), Modifier.weight(1f).fillMaxHeight(), 1f); Spacer(Modifier.weight(1f)) }
                        Row(Modifier.fillMaxWidth().aspectRatio(3f), horizontalArrangement = Arrangement.spacedBy(pxDp(1f))) {
                            B(VPad("cl", "C◀", axis = -1f to 0f), Modifier.weight(1f).fillMaxHeight(), 1f)
                            B(VPad("cd", "C▼", axis = 0f to 1f), Modifier.weight(1f).fillMaxHeight(), 1f)
                            B(VPad("cr", "C▶", axis = 1f to 0f), Modifier.weight(1f).fillMaxHeight(), 1f)
                        }
                        Row(Modifier.fillMaxWidth().aspectRatio(2.2f), horizontalArrangement = Arrangement.spacedBy(pxDp(3f))) {
                            B(VPad("n64b", "B", KeyEvent.KEYCODE_BUTTON_Y), Modifier.weight(1f).fillMaxHeight())
                            B(VPad("n64a", "A", KeyEvent.KEYCODE_BUTTON_B), Modifier.weight(1f).fillMaxHeight())
                        }
                    }
                    else -> Row(Modifier.fillMaxWidth().widthIn(max = pxDp(56f)).aspectRatio(2f), horizontalArrangement = Arrangement.spacedBy(pxDp(3f)), verticalAlignment = Alignment.CenterVertically) {
                        // NES / Game Boy / GBA: B left, A right
                        B(face("B", KeyEvent.KEYCODE_BUTTON_B), Modifier.weight(1f).aspectRatio(1f))
                        B(face("A", KeyEvent.KEYCODE_BUTTON_A), Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
        if (core == "n64") VhsText("STICK: LEFT OF SCREEN", color = o.fg.copy(alpha = 0.6f), scale = 0.8f)
    }
}

/** VIDEO / GAMES: which deck LOAD shows (like APPS / RADIO on the home screen). */
@Composable
internal fun VhsDeckSwitch(mode: String, onSwitch: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(pxDp(3f)), verticalAlignment = Alignment.CenterVertically) {
        listOf("video" to "VIDEO", "game" to "GAMES", "music" to "MUSIC").forEach { (m, l) ->
            VhsKey(l, selected = mode == m, boxed = true) { if (mode != m) { Haptics.clunk("eject"); onSwitch(m) } }
        }
    }
}

/** Test hooks for the emulator checks (set from MainActivity's launch extras). */
internal object VhsTestHooks { var rom by mutableStateOf<File?>(null); var switchMs = 420 }
