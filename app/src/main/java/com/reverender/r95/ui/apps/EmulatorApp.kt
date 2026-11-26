package com.reverender.r95.ui.apps

import androidx.compose.foundation.verticalScroll

import androidx.compose.foundation.layout.heightIn

import androidx.compose.foundation.clickable

import androidx.compose.foundation.border

import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.view.KeyEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.reverender.r95.LocalAppState
import com.reverender.r95.net.ArchiveClient
import com.reverender.r95.net.ArchiveFile
import com.reverender.r95.net.ArchiveItem
import com.reverender.r95.net.Downloader
import com.reverender.r95.ui.win95.Btn
import com.reverender.r95.ui.win95.Field
import com.reverender.r95.ui.win95.Lcd
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import com.reverender.r95.ui.win95.bevel
import com.swordfish.libretrodroid.GLRetroView
import com.swordfish.libretrodroid.GLRetroViewData
import com.swordfish.libretrodroid.ShaderConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipInputStream

/** The same libretro cores the website's EmulatorJS uses, run natively. */
internal data class EmuSystem(val label: String, val badge: String, val core: String)

internal fun systemFor(ext: String): EmuSystem? = when (ext.lowercase()) {
    "nes" -> EmuSystem("NINTENDO ENTERTAINMENT SYSTEM (NES)", "FCEUMM // NES", "fceumm")
    "smc", "sfc", "fig", "swc", "gd3", "gd7", "dx2", "bsx", "snes" -> EmuSystem("SUPER NINTENDO ENTERTAINMENT SYSTEM (SNES)", "SNES9X // SNES", "snes9x")
    "gb" -> EmuSystem("NINTENDO GAME BOY (GB)", "GAMBATTE // GB", "gambatte")
    "gbc" -> EmuSystem("NINTENDO GAME BOY COLOR (GBC)", "GAMBATTE // GBC", "gambatte")
    "gba" -> EmuSystem("NINTENDO GAME BOY ADVANCE (GBA)", "MGBA // GBA", "mgba")
    "n64", "z64", "v64" -> EmuSystem("NINTENDO 64 (N64)", "MUPEN64PLUS NEXT // N64", "mupen64plus_next_gles3")
    else -> null
}

internal val romExts = setOf("nes", "smc", "sfc", "fig", "swc", "gd3", "gd7", "dx2", "bsx", "snes", "gb", "gbc", "gba", "n64", "z64", "v64")

/** A lifecycle owned by the emulator window, so closing the window shuts the core down cleanly. */
private class EmuLifecycle : LifecycleOwner {
    // driven from a worker thread when the core shuts down (so a stuck GL thread can never freeze the UI)
    val registry = LifecycleRegistry.createUnsafe(this)
    override val lifecycle: Lifecycle get() = registry
}

internal data class LoadedGame(val rom: File, val system: EmuSystem, val core: File)

@kotlin.OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun EmulatorApp(wm: com.reverender.r95.ui.desktop.WindowManager? = null) {
    val state = LocalAppState.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // M(): emu-lcd-system / emu-lcd-core / emu-lcd-rom / emu-status-left / emu-status-right
    var lSys by remember { mutableStateOf("SYSTEM: STANDBY") }
    var lCore by remember { mutableStateOf("READY") }
    var lRom by remember { mutableStateOf("DROP .NES / .N64 / .Z64 / .V64 / SNES / .GB / .GBC / .GBA ROM INTO ANY WINDOW") }
    var sLeft by remember { mutableStateOf("SUPPORTED: .NES | .N64/.Z64/.V64 | .SNES (.SNES/.SMC/.SFC/.FIG/.GD3/.GD7/.DX2/.BSX/.SWC) | .GB | .GBC | .GBA") }
    var sRight by remember { mutableStateOf("CORE: IDLE") }
    fun M(e: String?, t: String?, n: String?, o: String?, a: String?) { e?.let { lSys = it }; t?.let { lCore = it }; n?.let { lRom = it }; o?.let { sLeft = it }; a?.let { sRight = it } }

    var game by remember { mutableStateOf<LoadedGame?>(null) }
    var view by remember { mutableStateOf<GLRetroView?>(null) }
    var showSearch by remember { mutableStateOf(false) }
    var showPad by remember { mutableStateOf(true) }

    suspend fun ensureCore(core: String): File {
        val abi = Build.SUPPORTED_ABIS.firstOrNull { it in listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86") } ?: "armeabi-v7a"
        val dir = File(context.filesDir, "cores/$abi").apply { mkdirs() }
        val so = File(dir, "${core}_libretro_android.so")
        if (so.exists() && so.length() > 0) return so
        lRom = "DOWNLOADING ${core.uppercase()} CORE (ONE TIME)…"
        val zip = File(context.cacheDir, "${core}.so.zip")
        Downloader.download("https://buildbot.libretro.com/nightly/android/latest/$abi/${core}_libretro_android.so.zip", zip) { pr ->
            lRom = "DOWNLOADING ${core.uppercase()} CORE… ${(pr * 100).toInt()}%"
        }
        withContext(Dispatchers.IO) {
            ZipInputStream(zip.inputStream()).use { zin ->
                while (true) {
                    val e = zin.nextEntry ?: break
                    if (e.name.endsWith(".so")) { so.outputStream().use { zin.copyTo(it) }; break }
                }
            }
            zip.delete()
        }
        return so
    }

    /** Unpacks zipped ROMs and returns a ROM file the cores can read. */
    suspend fun prepareRom(file: File): File = withContext(Dispatchers.IO) {
        if (!file.name.endsWith(".zip", true)) return@withContext file
        ZipInputStream(file.inputStream()).use { zin ->
            while (true) {
                val e = zin.nextEntry ?: break
                val ext = e.name.substringAfterLast('.', "").lowercase()
                if (ext in romExts) {
                    val out = File(file.parentFile, e.name.substringAfterLast('/'))
                    out.outputStream().use { zin.copyTo(it) }
                    return@withContext out
                }
            }
        }
        throw IllegalArgumentException("No supported ROM inside the zip")
    }

    fun launch(romIn: File) {
        scope.launch {
            try {
                val rom = prepareRom(romIn)
                val sys = systemFor(rom.extension)
                val bad = withContext(Dispatchers.IO) { romProblem(rom) }
                if (sys != null && bad != null) {
                    android.util.Log.w("r95", "emu: rejected ${rom.name}: $bad")
                    runCatching { rom.delete() }
                    M("SYSTEM: ERROR // UNREADABLE ROM", "REJECTED", bad, "REJECTED: .${rom.extension.uppercase()}", "CORE: IDLE")
                    return@launch
                }
                if (sys == null) {
                    M("SYSTEM: ERROR // UNSUPPORTED FILE", "REJECTED", "INVALID FILE \"${rom.name}\" — SUPPORTED: .NES, .N64/.Z64/.V64, SNES, .GB, .GBC, .GBA.", "REJECTED: .${rom.extension.uppercase().ifEmpty { "UNKNOWN" }}", "ERROR: UNSUPPORTED EXT")
                    return@launch
                }
                lRom = "LOADING ${sys.label}…"
                val core = ensureCore(sys.core)
                state.player.duck()
                game = null
                game = LoadedGame(rom, sys, core)
                M("SYSTEM: ${sys.label}", "RUNNING", "ROM: ${rom.name}", "AUTO-DETECTED: .${rom.extension.uppercase()}", "CORE: ${sys.badge}")
                android.util.Log.i("r95", "emu: running ${rom.name} on ${sys.core}")
            } catch (e: Exception) {
                lRom = "ERROR: ${e.message ?: "could not start the game"}"
            }
        }
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            try {
                val name = withContext(Dispatchers.IO) {
                    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c -> if (c.moveToFirst()) c.getString(0) else null }
                } ?: "game.rom"
                val dest = File(context.filesDir, "roms/$name").apply { parentFile?.mkdirs() }
                withContext(Dispatchers.IO) { context.contentResolver.openInputStream(uri)!!.use { input -> dest.outputStream().use { input.copyTo(it) } } }
                launch(dest)
            } catch (e: Exception) { lRom = "ERROR: ${e.message}" }
        }
    }

    // ge(): the touch pad follows the running core and opens itself on touch devices (le())
    androidx.compose.runtime.LaunchedEffect(game) {
        val g = game
        EmuTouch.core = g?.let { when (it.system.core) { "fceumm" -> "fceumm"; "snes9x" -> "snes9x"; "gambatte" -> "gambatte"; "mgba" -> "mgba"; else -> "n64" } }
        if (g != null) wm?.openEmuTouch() else wm?.closeEmuTouch()
    }
    DisposableEffect(Unit) { onDispose { EmuTouch.core = null; EmuTouch.view = null; wm?.windows?.firstOrNull { it.app.id == "emutouch" }?.let { wm.close(it) } } }
    fun eject() {
        game = null
        M("SYSTEM: STANDBY", "READY", "DROP .NES / .N64 / .Z64 / .V64 / SNES / .GB / .GBC / .GBA ROM INTO ANY WINDOW",
            "SUPPORTED: .NES | .N64/.Z64/.V64 | .SNES (.SNES/.SMC/.SFC/.FIG/.GD3/.GD7/.DX2/.BSX/.SWC) | .GB | .GBC | .GBA", "CORE: IDLE")
    }
    Column(Modifier.fillMaxSize().padding(4.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        // .lcd-screen
        Lcd(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth()) {
                Row { T(lSys, color = Color(0xFF00F7FF), bold = true, mono = true, size = 9.sp, maxLines = 1, modifier = Modifier.weight(1f)); T(lCore, color = Color(0xFF00FF66), bold = true, mono = true, size = 9.sp) }
                T(lRom, color = Color(0xFF00F7FF), mono = true, size = 9.sp, maxLines = 2)
                Row(Modifier.padding(top = 3.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Btn("PAD", { if (EmuTouch.core != null) wm?.openEmuTouch() }, size = 10.sp)
                    Btn("LOAD ROM", { picker.launch(arrayOf("*/*")) }, size = 10.sp)
                    Btn("CAST TAB", {
                        // the site explains Chrome tab casting; natively the system screen-cast does the same (controls stay local)
                        runCatching { context.startActivity(android.content.Intent("android.settings.CAST_SETTINGS").addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) }
                            .onFailure { runCatching { context.startActivity(android.content.Intent(android.provider.Settings.ACTION_DISPLAY_SETTINGS).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) } }
                    }, size = 10.sp)
                    Btn("⏹ EJECT", ::eject, size = 10.sp)
                }
            }
        }
        // #emu-archive-search
        RomSearch(Modifier.fillMaxWidth(), ::M) { url, name ->
            scope.launch {
                try {
                    val dest = File(context.filesDir, "roms/$name")
                    if (!dest.exists()) Downloader.download(url, dest) { pr -> lRom = "DOWNLOADING ROM… ${(pr * 100).toInt()}%" }
                    launch(dest)
                } catch (e: Exception) { android.util.Log.w("r95", "emu: download failed", e); lRom = "DOWNLOAD FAILED: ${e.message}" }
            }
        }
        // #emu-viewport
        Box(Modifier.fillMaxWidth().weight(1f).background(Color(0xFF05010D)).bevel(false), contentAlignment = Alignment.Center) {
            val g = game
            if (g == null) {
                Column(
                    Modifier.fillMaxSize().padding(12.dp).border(2.dp, Color(0xFF00F7FF).copy(alpha = 0.6f)).clickable { picker.launch(arrayOf("*/*")) }.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                ) {
                    T("🎮", size = 36.sp)
                    T("DRAG & DROP ROM FILE HERE", color = Color.White, bold = true, size = 13.sp)
                    T("AUTO-DETECTS SYSTEM BY FILE EXTENSION", color = Color(0xFF00F7FF), size = 12.sp)
                    androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(".NES (Nintendo)" to true, "SNES (.SMC / .SFC)" to true, ".GB (Game Boy)" to false, ".GBC (GB Color)" to false, ".GBA (GB Advance)" to true, ".N64 / .Z64 / .V64 (Nintendo 64)" to true).forEach { (t, pink) ->
                            T(t, color = Color.White, size = 11.sp, modifier = Modifier.border(1.dp, if (pink) Color(0xFFFF00A0) else Color(0xFF00F7FF)).padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            } else {
                key(g) { RetroSurface(g, Modifier.fillMaxSize(), onView = { view = it; EmuTouch.view = it }, onError = { lRom = "CORE ERROR $it — TRY ANOTHER ROM" }) }
                if (EmuTouch.core == "n64") N64AnalogZone()
            }
        }
        // .status-bar
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Box(Modifier.weight(1f).bevel(false).padding(horizontal = 3.dp, vertical = 1.dp)) { T(sLeft, size = 9.sp, mono = true, maxLines = 1) }
            Box(Modifier.weight(1f).bevel(false).padding(horizontal = 3.dp, vertical = 1.dp)) { T(sRight, size = 9.sp, mono = true, maxLines = 1) }
        }
    }
}

@Composable
internal fun RetroSurface(game: LoadedGame, modifier: Modifier, onView: (GLRetroView?) -> Unit, onError: (Int) -> Unit, paused: Boolean = false) {
    val context = LocalContext.current
    val activityLifecycle = LocalLifecycleOwner.current.lifecycle
    val owner = remember(game) { EmuLifecycle() }
    val scope = rememberCoroutineScope()
    val sram = File(game.rom.path + ".srm")
    val retro = remember(game) {
        val data = GLRetroViewData(context).apply {
            coreFilePath = game.core.absolutePath
            gameFilePath = game.rom.absolutePath
            systemDirectory = File(context.filesDir, "system").apply { mkdirs() }.absolutePath
            savesDirectory = File(context.filesDir, "saves").apply { mkdirs() }.absolutePath
            saveRAMState = if (sram.exists()) sram.readBytes() else null
            shader = ShaderConfig.Sharp
            preferLowLatencyAudio = true
        }
        GLRetroView(context, data)
    }
    val stopped = remember(game) { java.util.concurrent.atomic.AtomicBoolean(false) }
    val pausedNow by androidx.compose.runtime.rememberUpdatedState(paused)
    // save the battery RAM and shut the core down on a worker thread: on the UI thread this could block on a
    // GL thread that is already gone (the "app isn't responding" freeze on BACK / EJECT)
    fun teardown(): Thread = Thread({
        runCatching { sram.writeBytes(retro.serializeSRAM()) }
        runCatching { owner.registry.currentState = Lifecycle.State.DESTROYED }
    }, "emu-stop").apply { isDaemon = true; start() }
    DisposableEffect(game) {
        owner.registry.addObserver(retro)
        owner.registry.currentState = Lifecycle.State.CREATED
        owner.registry.currentState = if (!paused && activityLifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) Lifecycle.State.RESUMED else Lifecycle.State.STARTED
        val mirror = LifecycleEventObserver { _, e ->
            if (stopped.get()) return@LifecycleEventObserver
            when (e) {
                Lifecycle.Event.ON_PAUSE -> owner.registry.currentState = Lifecycle.State.STARTED
                Lifecycle.Event.ON_RESUME -> if (!pausedNow) owner.registry.currentState = Lifecycle.State.RESUMED
                else -> {}
            }
        }
        activityLifecycle.addObserver(mirror)
        val errors = scope.launch { retro.getGLRetroErrors().collect { onError(it) } }
        onView(retro)
        // a clean stop while the picture is still on screen (EJECT, a new cartridge, leaving the deck)
        val stopper: suspend () -> Unit = {
            if (stopped.compareAndSet(false, true)) {
                activityLifecycle.removeObserver(mirror)
                val t = teardown()
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { t.join(2500) }
            }
        }
        EmuTouch.stopper = stopper
        onDispose {
            errors.cancel()
            activityLifecycle.removeObserver(mirror)
            if (stopped.compareAndSet(false, true)) teardown()
            if (EmuTouch.stopper === stopper) EmuTouch.stopper = null
            onView(null)
        }
    }
    // pause the core while its deck is hidden (the cartridge search), resume when it comes back
    androidx.compose.runtime.LaunchedEffect(game, paused) {
        if (stopped.get()) return@LaunchedEffect
        owner.registry.currentState = if (!paused && activityLifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) Lifecycle.State.RESUMED else Lifecycle.State.STARTED
    }
    AndroidView(factory = { retro }, modifier = modifier.aspectRatio(if (game.system.core == "gambatte") 10f / 9f else 4f / 3f, matchHeightConstraintsFirst = true))
}

/** RETRO_EMU_95 touch pad: D-pad, A/B/X/Y, L/R, SELECT/START. Multi-touch. */
@Composable
private fun TouchPad(modifier: Modifier, send: (Int, Int) -> Unit) {
    Row(modifier.background(Color(0xFF1B1526)).bevel(false).padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PadKey("▲", KeyEvent.KEYCODE_DPAD_UP, send)
            Row { PadKey("◀", KeyEvent.KEYCODE_DPAD_LEFT, send); Spacer(Modifier.size(44.dp)); PadKey("▶", KeyEvent.KEYCODE_DPAD_RIGHT, send) }
            PadKey("▼", KeyEvent.KEYCODE_DPAD_DOWN, send)
        }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { PadKey("L", KeyEvent.KEYCODE_BUTTON_L1, send, wide = true); PadKey("R", KeyEvent.KEYCODE_BUTTON_R1, send, wide = true) }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { PadKey("SEL", KeyEvent.KEYCODE_BUTTON_SELECT, send, wide = true); PadKey("START", KeyEvent.KEYCODE_BUTTON_START, send, wide = true) }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PadKey("X", KeyEvent.KEYCODE_BUTTON_X, send, round = true)
            Row { PadKey("Y", KeyEvent.KEYCODE_BUTTON_Y, send, round = true); Spacer(Modifier.size(44.dp)); PadKey("A", KeyEvent.KEYCODE_BUTTON_A, send, round = true) }
            PadKey("B", KeyEvent.KEYCODE_BUTTON_B, send, round = true)
        }
    }
}

@Composable
private fun PadKey(label: String, code: Int, send: (Int, Int) -> Unit, round: Boolean = false, wide: Boolean = false) {
    var down by remember { mutableStateOf(false) }
    Box(
        Modifier.size(width = if (wide) 64.dp else 44.dp, height = if (wide) 30.dp else 44.dp)
            .padding(2.dp)
            .background(if (down) Color(0xFFFF00A0) else Color(0xFF3A2E50), if (round) CircleShape else androidx.compose.ui.graphics.RectangleShape)
            .pointerInput(code) {
                detectTapGestures(onPress = {
                    down = true; send(KeyEvent.ACTION_DOWN, code)
                    tryAwaitRelease()
                    down = false; send(KeyEvent.ACTION_UP, code)
                })
            },
        contentAlignment = Alignment.Center,
    ) { T(label, color = Color.White, size = if (wide) 10.sp else 13.sp, bold = true) }
}

/** #emu-archive-search: the site's deep Internet Archive ROM search. */
@Composable
private fun RomSearch(modifier: Modifier, M: (String?, String?, String?, String?, String?) -> Unit, onPick: (String, String) -> Unit) {
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(false) }
    val rows = remember { mutableStateListOf<com.reverender.r95.net.ArchiveRom>() }
    var job by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    fun P(t: String) { msg = t; rows.clear(); visible = true }
    /** Ke() */
    fun search(e: String) {
        val t = com.reverender.r95.net.ArchiveRomSearch.parse(e)
        if (t.original.length < 2) { P("ENTER AT LEAST 2 CHARACTERS TO SEARCH."); return }
        if (t.tokens.isEmpty()) { P("ENTER A GAME TITLE. PLATFORM HINTS SUCH AS GB, GBC, GBA (OR GBBA), NES AND SNES FILTER THE FILE EXTENSION."); return }
        job?.cancel()
        val r = if (t.systemLabel.isNotEmpty()) "SYSTEM FILTER: ${t.systemLabel}" else "SYSTEM FILTER: ALL SUPPORTED"
        P("DEEP SEARCHING INTERNET ARCHIVE... $r. FIRST MATCHES WILL APPEAR AUTOMATICALLY.")
        M(null, "IA DEEP SEARCH", "SEARCHING: ${t.title.trim()} · $r", null, "ARCHIVE: LIVE RESULTS + COLLECTION SEARCH")
        job = scope.launch {
            try {
                val (all, cols) = com.reverender.r95.net.ArchiveRomSearch.search(t) { u ->
                    scope.launch {
                        msg = null; rows.clear(); rows.addAll(u.take(40))
                        M(null, "IA MATCHES", "${u.size} ROM FILE(S) FOUND FOR: ${t.title.trim()}", null, "ARCHIVE: LIVE RESULTS · DEEP SEARCH CONTINUES")
                    }
                }
                if (all.isEmpty()) P("NO MATCHING ROM FILES FOUND. SEARCHED EXACT TITLES, ITEM METADATA AND RELEVANT COLLECTIONS. TRY THE FULL GAME NAME OR A DIFFERENT SYSTEM.")
                else { msg = null; rows.clear(); rows.addAll(all.take(40)) }
                android.util.Log.i("r95", "emu: search done, ${all.size} results")
                // test hook (am start --es r95_emu_search …): load the first result like a tap on ▶ LOAD
                if (com.reverender.r95.core.RevyRequests.emuAutoPick && all.isNotEmpty()) {
                    com.reverender.r95.core.RevyRequests.emuAutoPick = false
                    val i = all.first(); android.util.Log.i("r95", "emu: auto-loading ${i.name}")
                    visible = false; rows.clear(); onPick(i.url, i.name)
                }
                val ve = if (t.systemLabel.isNotEmpty()) t.systemKeys.flatMap { com.reverender.r95.net.ArchiveRomSearch.K.getValue(it).extensions }.joinToString("/") { ".${it.uppercase()}" } else "SUPPORTED ROM EXTENSIONS"
                M(null, if (all.isNotEmpty()) "IA MATCHES" else "NO ROMS", "${all.size} EXACT/RELEVANT FILE(S) FOR: ${t.title.trim()}", null,
                    if (all.isNotEmpty()) "ARCHIVE: $ve · COLLECTIONS $cols" else "ARCHIVE: NO MATCH IN ITEM OR COLLECTION SEARCH")
            } catch (x: kotlinx.coroutines.CancellationException) { throw x }
            catch (x: Exception) {
                P("INTERNET ARCHIVE SEARCH FAILED. CHECK YOUR CONNECTION AND TRY AGAIN.")
                M(null, "IA ERROR", "INTERNET ARCHIVE DEEP SEARCH FAILED", null, "ARCHIVE: ERROR")
            }
        }
    }
    // Revy: emu-archive-search-form
    LaunchedEffect(com.reverender.r95.core.RevyRequests.emuQuery) {
        val q = com.reverender.r95.core.RevyRequests.emuQuery ?: return@LaunchedEffect
        com.reverender.r95.core.RevyRequests.emuQuery = null
        query = q; search(q)
    }
    DisposableEffect(Unit) { onDispose { job?.cancel() } }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Field(query, { query = it.take(80) }, Modifier.weight(1f), hint = "Search Internet Archive: game name…", onDone = { search(query) })
            Spacer(Modifier.width(4.dp))
            Btn("Search Archive", { search(query) }, size = 10.sp)
        }
        if (visible) Column(Modifier.fillMaxWidth().heightIn(max = 112.dp).background(Color(0xFF05010D)).border(1.dp, Color(0xFF00F7FF)).verticalScroll(rememberScrollState()).padding(3.dp)) {
            msg?.let { T(it, color = Color.White, mono = true, size = 10.sp) }
            rows.forEach { i ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        T(i.title, color = Color.White, bold = true, mono = true, size = 10.sp, maxLines = 1)
                        T("${i.name} · .${i.ext.uppercase()}" + (if (i.size > 0) " · " + "%.1f".format(java.util.Locale.US, i.size / (1024.0 * 1024.0)) + " MB" else ""), color = Color(0xFF00F7FF), mono = true, size = 10.sp, maxLines = 1)
                    }
                    Spacer(Modifier.width(5.dp))
                    Btn("▶ LOAD", { job?.cancel(); visible = false; rows.clear(); onPick(i.url, i.name) }, size = 9.sp)
                }
            }
        }
        T("Internet Archive · .gb .gbc .gba .nes .snes .n64 .z64 .v64 · load only content you are entitled to use", size = 9.sp, color = W95.Shadow)
    }
}


/** Why a ROM file can't be handed to a core (null = looks fine): empty, a web page, or the wrong size for its system. */
internal fun romProblem(f: File): String? {
    if (!f.exists() || f.length() < 512) return "THE ROM FILE IS EMPTY OR INCOMPLETE — TRY ANOTHER RESULT."
    val head = ByteArray(16)
    f.inputStream().use { it.read(head) }
    val text = String(head, Charsets.ISO_8859_1).trimStart().lowercase()
    if (text.startsWith("<!doctype") || text.startsWith("<html") || text.startsWith("<?xml") || text.startsWith("{")) return "THE ARCHIVE SENT A WEB PAGE INSTEAD OF A GAME (LOGIN NEEDED) — TRY ANOTHER RESULT."
    when (f.extension.lowercase()) {
        "nes" -> if (!(head[0] == 'N'.code.toByte() && head[1] == 'E'.code.toByte() && head[2] == 'S'.code.toByte())) return "NOT A VALID .NES ROM (MISSING iNES HEADER)."
        "gb", "gbc" -> if (f.length() < 0x150) return "NOT A VALID GAME BOY ROM."
        "gba" -> if (f.length() < 0xC0) return "NOT A VALID GBA ROM."
        "n64", "z64", "v64" -> if (f.length() < 1_048_576) return "NOT A VALID N64 ROM."
    }
    return null
}


/** Downloads (once) and returns the libretro core [core] for this phone's CPU. Shared by RETRO_EMU_95 and the VHS GAME DECK. */
internal suspend fun ensureEmuCore(context: android.content.Context, core: String, progress: (String) -> Unit): File {
    val abi = Build.SUPPORTED_ABIS.firstOrNull { it in listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86") } ?: "armeabi-v7a"
    val dir = File(context.filesDir, "cores/$abi").apply { mkdirs() }
    val so = File(dir, "${core}_libretro_android.so")
    if (so.exists() && so.length() > 0) return so
    // the VHS launcher ships the cores (arm64): unpack instead of downloading
    val bundled = withContext(Dispatchers.IO) {
        runCatching {
            context.assets.open("cores/$abi/${core}_libretro_android.so").use { i -> so.outputStream().use { i.copyTo(it) } }
            so.length() > 0
        }.getOrDefault(false)
    }
    if (bundled) return so
    runCatching { so.delete() }
    progress("DOWNLOADING ${core.uppercase()} CORE (ONE TIME)…")
    val zip = File(context.cacheDir, "${core}.so.zip")
    Downloader.download("https://buildbot.libretro.com/nightly/android/latest/$abi/${core}_libretro_android.so.zip", zip) { pr ->
        progress("DOWNLOADING ${core.uppercase()} CORE… ${(pr * 100).toInt()}%")
    }
    withContext(Dispatchers.IO) {
        ZipInputStream(zip.inputStream()).use { zin ->
            while (true) {
                val e = zin.nextEntry ?: break
                if (e.name.endsWith(".so")) { so.outputStream().use { zin.copyTo(it) }; break }
            }
        }
        zip.delete()
    }
    return so
}

/** Unpacks a zipped ROM (first supported file inside) and returns a file the cores can read. */
internal suspend fun prepareEmuRom(file: File): File = withContext(Dispatchers.IO) {
    if (!file.name.endsWith(".zip", true)) return@withContext file
    ZipInputStream(file.inputStream()).use { zin ->
        while (true) {
            val e = zin.nextEntry ?: break
            val ext = e.name.substringAfterLast('.', "").lowercase()
            if (ext in romExts) {
                val out = File(file.parentFile, e.name.substringAfterLast('/'))
                out.outputStream().use { zin.copyTo(it) }
                return@withContext out
            }
        }
    }
    throw IllegalArgumentException("No supported ROM inside the zip")
}
