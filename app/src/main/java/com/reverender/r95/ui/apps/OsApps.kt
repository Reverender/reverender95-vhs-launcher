package com.reverender.r95.ui.apps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.reverender.r95.LocalAppState
import com.reverender.r95.audio.AmpEngine
import com.reverender.r95.audio.GlobalEq
import com.reverender.r95.audio.MicCapture
import com.reverender.r95.audio.SystemCapture
import com.reverender.r95.audio.SystemVisualizer
import com.reverender.r95.core.Themes
import com.reverender.r95.os.CrtMonitor
import com.reverender.r95.os.GyroWarp
import com.reverender.r95.os.Launcher
import com.reverender.r95.os.R95LiveWallpaper
import com.reverender.r95.os.Midi
import com.reverender.r95.os.MusicLibrary
import com.reverender.r95.os.OsPrefs
import com.reverender.r95.os.R95Dream
import com.reverender.r95.os.RetroAlerts
import com.reverender.r95.os.SaverView
import com.reverender.r95.ui.desktop.Win
import com.reverender.r95.ui.desktop.WindowManager
import com.reverender.r95.ui.win95.Btn
import com.reverender.r95.ui.win95.Check
import com.reverender.r95.ui.win95.GroupBox
import com.reverender.r95.ui.win95.Slider95
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95

private val THEME_OPTS get() = Themes.all.map { it.id to it.label }

@Composable
private fun Note(s: String) = T(s, size = 10.sp, color = W95.Shadow, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))

@Composable
private fun Status(s: String) = Box(Modifier.fillMaxWidth().background(Color.Black).border(1.dp, W95.Shadow).padding(horizontal = 6.dp, vertical = 3.dp)) {
    T(s, size = 10.sp, mono = true, bold = true, color = Color(0xFF39FF14), maxLines = 2)
}

/** Re-reads permission-style switches when the user comes back from Android's settings screens. */
@Composable
private fun rememberResumeTick(): Int {
    var n by remember { mutableIntStateOf(0) }
    val vis = com.reverender.r95.core.AppVisibility.visible
    LaunchedEffect(vis) { if (vis) n++ }
    return n
}

// ---------------------------------------------------------------- Settings → Android integration

@Composable
fun AndroidSettingsGroups() {
    val ctx = LocalContext.current
    val tick = rememberResumeTick()
    GroupBox("Updates") {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            T("REVERENDER_95 ${com.reverender.r95.BuildConfig.VERSION_NAME} (build ${com.reverender.r95.BuildConfig.VERSION_CODE})", size = 12.sp, bold = true)
            // the browser downloads and Android's installer installs: the app itself needs no install permission
            Btn("⬇ Download latest version (opens browser)", {
                val i = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(com.reverender.r95.core.Updater.APK_URL))
                    .addCategory(android.content.Intent.CATEGORY_BROWSABLE).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                runCatching { ctx.startActivity(i) }
            }, Modifier.fillMaxWidth().height(34.dp), size = 12.sp)
            Note("Your browser downloads reverender_95.apk; open it to update. Settings and music stay.")
        }
    }
    GroupBox("Phone: home screen") {
        Column {
            val home = remember(tick) { Launcher.homeEnabled(ctx) }
            Check("Offer REVERENDER_95 as home launcher", home, { Launcher.setHomeMode(ctx, it) })
            Note("Then pick it in Android's \"Default home app\". The 🌴 Start menu's Phone Apps opens your installed apps.")
            if (home) Btn("Default home app…", { Launcher.openHomeSettings(ctx) }, Modifier.padding(top = 3.dp))
            Check("Open phone apps in floating windows", OsPrefs.floatingApps, OsPrefs::setFloatingApps)
            if (OsPrefs.floatingApps && !remember { Launcher.freeformLikely(ctx) })
                Note("This phone may not offer floating windows; apps then open full screen (Android's developer option \"Enable freeform windows\" turns them on).")
        }
    }
    // the full APK only: notification access is a high-risk permission for sideloaded apps
    GroupBox("Desktop gestures") { SwipeShortcutSettings() }
    if (com.reverender.r95.BuildConfig.FULL) GroupBox("Phone: retro notifications") {
        Column {
            val access = remember(tick) { RetroAlerts.listenerEnabled(ctx) }
            Check("Show notifications as Win95 dialogs", OsPrefs.retroAlerts && access, { v ->
                OsPrefs.setRetroAlerts(v)
                if (v && !RetroAlerts.listenerEnabled(ctx)) RetroAlerts.openListenerSettings(ctx)
            })
            if (OsPrefs.retroAlerts && !access) Btn("Grant notification access…", { RetroAlerts.openListenerSettings(ctx) }, Modifier.padding(start = 26.dp, top = 2.dp))
            Check("Pitched-down 90s chime", OsPrefs.alertChime, OsPrefs::setAlertChime)
            val over = remember(tick) { RetroAlerts.canOverlay(ctx) }
            Check("Also on top of other apps", OsPrefs.alertOverlay && over, { v ->
                OsPrefs.setAlertOverlay(v)
                if (v && !RetroAlerts.canOverlay(ctx)) RetroAlerts.openOverlaySettings(ctx)
            })
            Note("Texts, Discord, system alerts… Nothing leaves the phone.")
            Btn("Test dialog", { RetroAlerts.post(ctx, RetroAlerts.Alert("test", "REVERENDER_95", "Hello from 1995", "This is how your notifications will look.", "info", null)) }, Modifier.padding(top = 3.dp))
        }
    }
    GroupBox("Phone: dock screensaver") {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                T("Saver", size = 12.sp, bold = true, modifier = Modifier.width(64.dp))
                Select(OsPrefs.dreamSaver, SaverView.MODES, OsPrefs::setDreamSaver, Modifier.weight(1f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                T("Colours", size = 12.sp, bold = true, modifier = Modifier.width(64.dp))
                Select(OsPrefs.dreamTheme, THEME_OPTS, OsPrefs::setDreamTheme, Modifier.weight(1f))
            }
            Note("Android starts it by itself while charging or docked: pick \"REVERENDER_95 CRT\" and \"When to start\" in the screen saver settings.")
            Btn("Screen saver settings…", { R95Dream.openSettings(ctx) })
        }
    }
    GroupBox("Phone: live wallpaper") {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                T("World", size = 12.sp, bold = true, modifier = Modifier.width(64.dp))
                Select(OsPrefs.liveWallpaper.toString(), ALL_WORLDS.map { it.first.toString() to it.second }, { OsPrefs.setLiveWallpaper(it.toInt()) }, Modifier.weight(1f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                T("Colours", size = 12.sp, bold = true, modifier = Modifier.width(64.dp))
                Select(OsPrefs.liveTheme, THEME_OPTS, OsPrefs::setLiveTheme, Modifier.weight(1f))
            }
            Note("Reacts to everything the phone plays (needs the microphone permission, Android's rule for its audio visualizer).")
            AudioPermButton()
            Btn("Set as phone wallpaper…", { R95LiveWallpaper.open(ctx) })
        }
    }
    GroupBox("Phone: sound & motion") {
        Column {
            Check("Visualizers react to all phone audio", OsPrefs.systemReactive, OsPrefs::setSystemReactive)
            if (OsPrefs.systemReactive) { AudioPermButton(); Status(SystemVisualizer.status) }
            Check("Tape deck haptics (clunks, motor hum)", OsPrefs.haptics, OsPrefs::setHaptics)
            Check("Haptic thumps on the bass-drum glitch", OsPrefs.bassHaptics, OsPrefs::setBassHaptics)
            GyroControls()
        }
    }
}

@Composable
private fun AudioPermButton() {
    val ctx = LocalContext.current
    val tick = rememberResumeTick()
    val ok = remember(tick) { SystemVisualizer.permitted() }
    val ask = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { SystemVisualizer.refresh() }
    if (!ok) Btn("Allow microphone (for the visualizer tap)…", { ask.launch(Manifest.permission.RECORD_AUDIO) }, Modifier.padding(vertical = 2.dp))
}

@Composable
fun GyroControls() {
    if (!GyroWarp.available) { Note("No motion sensors on this device."); return }
    Check("Gyro tape warp (tilt / shake bends the pitch)", OsPrefs.gyroWarp, OsPrefs::setGyroWarp)
    if (OsPrefs.gyroWarp) Column(Modifier.padding(start = 26.dp)) {
        T("Warp depth ${(OsPrefs.gyroDepth * 100).toInt()}%", size = 11.sp, bold = true)
        Slider95(OsPrefs.gyroDepth, { OsPrefs.setGyroDepth(it); com.reverender.r95.audio.AudioBus.fx.gyroDepth = it }, Modifier.fillMaxWidth().height(26.dp), range = 0.1f..1f)
        Note("Keeps working with the screen off while music plays.")
    }
    Check("PLASMA_EQ gyro core follows tilt", OsPrefs.gyroPlasma, OsPrefs::setGyroPlasma)
}

// ---------------------------------------------------------------- GRAPHIC_EQ: system-wide

@Composable
fun EqSystemRow() {
    if (!GlobalEq.supported) return
    Column(Modifier.fillMaxWidth().padding(top = 4.dp)) {
        Check("Apply EQ + Sub Excite to all phone audio", OsPrefs.globalEq, OsPrefs::setGlobalEq)
        if (OsPrefs.globalEq) Status(GlobalEq.status)
    }
}

// ---------------------------------------------------------------- DSP_FX_95: external apps + MIDI

@Composable
fun FxExternalAndMidi() {
    val ctx = LocalContext.current
    val sysOn = MicCapture.running && MicCapture.kind == "system"
    var connecting by remember { mutableStateOf(false) }
    val consent = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
        SystemCapture.pendingResult = { ok -> connecting = false; if (ok) AmpEngine.startedSystem() else AmpEngine.systemCanceled() }
        SystemCapture.onConsent(ctx, r.resultCode, r.data)
    }
    val askMic = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) consent.launch(SystemCapture.consentIntent(ctx)) else connecting = false
    }
    GroupBox("🔌 EXTERNAL APPS IN") {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            if (!SystemCapture.supported) { Note("Needs Android 10 or newer."); return@Column }
            Btn(if (connecting) "CONNECTING…" else if (sysOn) "CAPTURING OTHER APPS: ON" else "CAPTURE OTHER APPS: OFF", {
                when {
                    sysOn -> AmpEngine.stopLiveCapture()
                    else -> {
                        connecting = true
                        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) consent.launch(SystemCapture.consentIntent(ctx))
                        else askMic.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            }, Modifier.fillMaxWidth().height(34.dp), pressed = sysOn, size = 12.sp)
            Check("Play it through this rack (EQ + FX + tape)", OsPrefs.dspMonitor, OsPrefs::setDspMonitor)
            Note("Spotify, YouTube & co. feed the visualizers; with the rack on you hear a processed copy on top of the original (turn that app down). Apps that block capture stay silent.")
        }
    }
    GroupBox("🎹 MIDI CONTROLLER", right = if (Midi.devices.isEmpty()) "" else "${Midi.devices.size}") {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            if (!Midi.supported) { Note("This phone has no MIDI support."); return@Column }
            LaunchedEffect(Unit) { Midi.start(ctx) }
            Status(if (Midi.devices.isEmpty()) "PLUG IN A USB / BT MIDI CONTROLLER" else Midi.devices.joinToString(" · ").uppercase())
            Status(if (Midi.learning != null) "TURN A KNOB FOR ${Midi.PARAMS.firstOrNull { it.first == Midi.learning }?.second}…" else Midi.lastEvent)
            var pick by remember { mutableStateOf("lpf") }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Select(pick, Midi.PARAMS, { pick = it }, Modifier.weight(1f))
                Btn(if (Midi.learning == pick) "CANCEL" else "LEARN", { Midi.learning = if (Midi.learning == pick) null else pick }, Modifier.height(30.dp), pressed = Midi.learning == pick, size = 11.sp)
            }
            val m = Midi.map()
            T(m.entries.sortedBy { it.key }.joinToString("  ") { "CC${it.key}→${Midi.PARAMS.firstOrNull { p -> p.first == it.value }?.second ?: it.value}" }, size = 9.sp, mono = true, color = W95.Shadow)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Btn("Reset map", { OsPrefs.setMidiMap(OsPrefs.DEFAULT_MIDI) }, Modifier.weight(1f), size = 11.sp)
            }
            Note("Keys play VAPOR_SYNTH_95 on a low-latency audio path; pitch bend rides TAPE SPEED.")
        }
    }
}

// ---------------------------------------------------------------- CASSETTE_95: feel

@Composable
fun CassetteFeelRow() {
    Column(Modifier.fillMaxWidth().padding(top = 4.dp)) {
        Check("Haptic transport (clunks, motor hum)", OsPrefs.haptics, OsPrefs::setHaptics)
        if (GyroWarp.available) {
            Check("Gyro tape warp", OsPrefs.gyroWarp, OsPrefs::setGyroWarp)
            if (OsPrefs.gyroWarp) Slider95(OsPrefs.gyroDepth, { OsPrefs.setGyroDepth(it); com.reverender.r95.audio.AudioBus.fx.gyroDepth = it }, Modifier.fillMaxWidth().height(26.dp), range = 0.1f..1f)
        }
    }
}

// ---------------------------------------------------------------- C:\MUSIC\ Explorer

@Composable
fun MusicExplorerApp(wm: WindowManager) {
    val ctx = LocalContext.current
    var granted by remember { mutableStateOf(MusicLibrary.permitted(ctx)) }
    val ask = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok -> granted = ok; if (ok) MusicLibrary.scan(ctx) }
    LaunchedEffect(granted) { if (granted && MusicLibrary.root == null) MusicLibrary.scan(ctx) }
    var path by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().padding(3.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        // address bar + toolbar
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Btn("⬆", { path = path.substringBeforeLast('/', "") }, Modifier.size(30.dp), enabled = path.isNotEmpty())
            Box(Modifier.weight(1f).height(26.dp).background(Color.White).border(1.dp, W95.Shadow).padding(horizontal = 5.dp), contentAlignment = Alignment.CenterStart) {
                T("C:\\MUSIC" + path.replace('/', '\\'), size = 11.sp, mono = true, maxLines = 1)
            }
            Btn("⟳", { MusicLibrary.scan(ctx) }, Modifier.size(30.dp))
        }
        if (!granted) {
            Column(Modifier.fillMaxWidth().weight(1f).background(Color.White).border(1.dp, W95.Shadow).padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                T("C:\\MUSIC needs permission to read the music on this phone.", size = 12.sp, bold = true)
                T("It is indexed once and kept up to date; no file picker every session.", size = 11.sp)
                Btn("Allow access to music", { ask.launch(MusicLibrary.permission) })
            }
            return@Column
        }
        val f = MusicLibrary.find(path)
        Box(Modifier.fillMaxWidth().weight(1f).background(Color.White).border(1.dp, W95.Shadow)) {
            when {
                MusicLibrary.scanning && f == null -> T("Scanning C:\\MUSIC…", size = 12.sp, modifier = Modifier.padding(8.dp))
                f == null -> T("Folder not found.", size = 12.sp, modifier = Modifier.padding(8.dp))
                else -> LazyColumn(Modifier.fillMaxSize()) {
                    items(f.folders.values.toList(), key = { "d" + it.path }) { d ->
                        Row(Modifier.fillMaxWidth().clickable { path = d.path; selected = null }.padding(horizontal = 6.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            T("📁", size = 14.sp); Spacer(Modifier.width(6.dp))
                            T(d.name, size = 12.sp, modifier = Modifier.weight(1f), maxLines = 1)
                            T("${d.count()} files", size = 10.sp, color = W95.Shadow)
                        }
                    }
                    items(f.songs, key = { "s" + it.id }) { s ->
                        val sel = selected == s.uri
                        Row(Modifier.fillMaxWidth().background(if (sel) Color(0xFF000080) else Color.Transparent)
                            .clickable { if (sel) playFrom(ctx, f, s) else selected = s.uri }.padding(horizontal = 6.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            T(if (s.ext == "FLAC") "💿" else "🎵", size = 13.sp); Spacer(Modifier.width(6.dp))
                            Column(Modifier.weight(1f)) {
                                T(s.name, size = 12.sp, color = if (sel) Color.White else W95.Text, maxLines = 1)
                                if (s.artist.isNotBlank()) T(s.artist, size = 10.sp, color = if (sel) Color(0xFFBBCCFF) else W95.Shadow, maxLines = 1)
                            }
                            T("${MusicLibrary.time(s.durationMs)}  ${MusicLibrary.size(s.sizeBytes)}", size = 10.sp, mono = true, color = if (sel) Color.White else W95.Shadow)
                        }
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Btn("▶ Play folder", { f?.let { playFrom(ctx, it, null) }; wm.open("player") }, Modifier.weight(1f).height(32.dp), enabled = f?.songs?.isNotEmpty() == true, size = 12.sp)
            Btn("▶ Play selected", { f?.songs?.firstOrNull { it.uri == selected }?.let { playFrom(ctx, f, it) }; wm.open("player") }, Modifier.weight(1f).height(32.dp), enabled = selected != null, size = 12.sp)
        }
        T("${MusicLibrary.total} songs on this phone · tap twice to play", size = 10.sp, color = W95.Shadow)
    }
}

/** Loads a folder into VAPOR_AMP's Device Files list and starts at [start]. */
private fun playFrom(ctx: android.content.Context, f: MusicLibrary.Folder, start: MusicLibrary.Song?) {
    val songs = f.songs
    if (songs.isEmpty()) return
    // add what is not in the playlist yet, then start at the chosen song
    val list = AmpEngine.customAudioFiles
    songs.forEach { sg -> if (list.none { it.url == sg.uri }) list.add(AmpEngine.CustomFile(sg.name, sg.uri)) }
    val target = (start ?: songs.first()).uri
    val i = list.indexOfFirst { it.url == target }
    if (i >= 0) AmpEngine.playCustomLocalFile(i)
}

// ---------------------------------------------------------------- CRT_MONITOR_95

@Composable
fun CrtMonitorApp(wm: WindowManager) {
    val state = LocalAppState.current
    Column(Modifier.fillMaxSize().padding(4.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Status(CrtMonitor.displayName?.let { if (CrtMonitor.showing) "📺 ON AIR: ${it.uppercase()}" else "📺 CONNECTED: ${it.uppercase()}" } ?: "NO SECOND SCREEN — PLUG IN USB-C/HDMI")
        Row(verticalAlignment = Alignment.CenterVertically) {
            T("On the TV", size = 12.sp, bold = true, modifier = Modifier.width(76.dp))
            Select(OsPrefs.monitorScene, SaverView.MODES + ALL_WORLDS.map { "wp:${it.first}" to it.second }.filter { w -> SaverView.MODES.none { it.first == w.first } },
                { CrtMonitor.setScene(it) }, Modifier.weight(1f))
        }
        LaunchedEffect(state.theme.id) { CrtMonitor.setTheme(state.theme.id) }
        Check("Send to the second screen", CrtMonitor.wanted, { CrtMonitor.wanted = it; CrtMonitor.update() })
        Btn("🎛 Open DSP_FX_95 rack here", { wm.open("fx") }, Modifier.fillMaxWidth().height(34.dp))
        Btn("🎚 Open GRAPHIC_EQ here", { wm.open("eq") }, Modifier.fillMaxWidth().height(34.dp))
        T("The phone stays the controller; the TV shows the world or visualizer full screen with CRT glass. " +
            "Wired USB-C/HDMI and wireless displays that Android offers as a second screen work; a Chromecast set to mirror shows the phone screen instead.",
            size = 10.sp, color = W95.Shadow)
    }
}

// ---------------------------------------------------------------- Start menu: Phone Apps

@Composable
fun PhoneAppsList(onLaunch: () -> Unit, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) { Launcher.load(ctx) }
    Column(modifier) {
        if (Launcher.loading && Launcher.apps.isEmpty()) T("Loading…", size = 12.sp, modifier = Modifier.padding(8.dp))
        LazyColumn(Modifier.heightIn(max = 420.dp)) {
            items(Launcher.apps, key = { it.component.flattenToString() }) { a ->
                PhoneAppRow(a, onLaunch)
            }
        }
    }
}

/** One phone app: tap opens it, long-press shows Open full screen / Pin to taskbar / App info / Uninstall. */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun PhoneAppRow(a: Launcher.PhoneApp, onLaunch: () -> Unit) {
    val ctx = LocalContext.current
    var menu by remember { mutableStateOf(false) }
    Box {
        Row(Modifier.fillMaxWidth().combinedClickable(onLongClick = { com.reverender.r95.os.Haptics.tick(); menu = true }) { Launcher.launch(ctx, a); onLaunch() }
            .padding(horizontal = 8.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            a.icon?.let { Image(it, null, Modifier.size(22.dp)) } ?: T("📱", size = 16.sp)
            Spacer(Modifier.width(8.dp))
            T(a.label, size = 12.sp, maxLines = 1, modifier = Modifier.weight(1f))
            if (a.component.flattenToString() in OsPrefs.pinned) T("📌", size = 10.sp)
        }
        PhoneAppMenu(a, menu, { menu = false }, onLaunch)
    }
}

@Composable
fun PhoneAppMenu(a: Launcher.PhoneApp, open: Boolean, dismiss: () -> Unit, onLaunch: () -> Unit = {}) {
    val ctx = LocalContext.current
    val flat = a.component.flattenToString()
    androidx.compose.material3.DropdownMenu(open, dismiss) {
        @Composable fun item(label: String, act: () -> Unit) = androidx.compose.material3.DropdownMenuItem(text = { T(label, size = 13.sp) }, onClick = { dismiss(); act() })
        T(a.label, size = 12.sp, bold = true, color = W95.Shadow, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
        item("Open") { Launcher.launch(ctx, a); onLaunch() }
        item("Open full screen") { Launcher.launch(ctx, a, fullScreen = true); onLaunch() }
        item(if (flat in OsPrefs.pinned) "Unpin from taskbar" else "📌 Pin to taskbar") { OsPrefs.togglePin(flat) }
        item("Swipe ← opens this") { OsPrefs.setSwipeLeft("pkg:$flat") }
        item("Swipe → opens this") { OsPrefs.setSwipeRight("pkg:$flat") }
        item("ℹ App info") { Launcher.appInfo(ctx, a.component.packageName) }
        item("🗑 Uninstall…") { Launcher.uninstall(ctx, a.component.packageName) }
    }
}

/** Pinned phone apps on the taskbar, right of the Start button (long-press for the app menu). */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun PinnedApps(vertical: Boolean, size: androidx.compose.ui.unit.Dp = 30.dp) {
    val ctx = LocalContext.current
    if (OsPrefs.pinned.isEmpty()) return
    LaunchedEffect(OsPrefs.pinned) { Launcher.load(ctx) }
    val list = OsPrefs.pinned.mapNotNull { Launcher.find(it) }
    val content: @Composable () -> Unit = {
        list.forEach { a ->
            var menu by remember { mutableStateOf(false) }
            Box(Modifier.padding(horizontal = 2.dp, vertical = 2.dp).size(size)
                .combinedClickable(onLongClick = { com.reverender.r95.os.Haptics.tick(); menu = true }) { Launcher.launch(ctx, a) },
                contentAlignment = Alignment.Center) {
                a.icon?.let { Image(it, a.label, Modifier.size(size - 6.dp)) } ?: T("📱", size = 16.sp)
                PhoneAppMenu(a, menu, { menu = false })
            }
        }
    }
    if (vertical) Column(horizontalAlignment = Alignment.CenterHorizontally) { content() } else Row(verticalAlignment = Alignment.CenterVertically) { content() }
}

/** The taskbar clock opens the phone's own clock app. */
fun openPhoneClock(ctx: android.content.Context) {
    val tries = listOf(
        android.content.Intent(android.provider.AlarmClock.ACTION_SHOW_ALARMS),
        ctx.packageManager.getLaunchIntentForPackage("com.google.android.deskclock"),
        ctx.packageManager.getLaunchIntentForPackage("com.sec.android.app.clockpackage"),
        ctx.packageManager.getLaunchIntentForPackage("com.android.deskclock"),
    )
    for (i in tries) if (i != null && runCatching { ctx.startActivity(i.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) }.isSuccess) return
}

/** Settings: what a horizontal swipe on the empty desktop opens. */
@Composable
fun SwipeShortcutSettings() {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) { Launcher.load(ctx) }
    val opts = listOf("" to "(nothing)") +
        com.reverender.r95.core.Apps.all.filter { it.id != "emutouch" }.map { "app:${it.id}" to "${it.icon} ${it.title}" } +
        Launcher.apps.map { "pkg:${it.component.flattenToString()}" to "📱 ${it.label}" }
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            T("Swipe ←", size = 12.sp, bold = true, modifier = Modifier.width(70.dp))
            Select(OsPrefs.swipeLeft, opts, OsPrefs::setSwipeLeft, Modifier.weight(1f))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            T("Swipe →", size = 12.sp, bold = true, modifier = Modifier.width(70.dp))
            Select(OsPrefs.swipeRight, opts, OsPrefs::setSwipeRight, Modifier.weight(1f))
        }
        Note("Swipe sideways on an empty part of the desktop. Up opens Start, down searches.")
    }
}

// ---------------------------------------------------------------- desktop: retro notification dialogs

@Composable
fun RetroAlertLayer() {
    val ctx = LocalContext.current
    val a = RetroAlerts.queue.lastOrNull() ?: return
    val classic = com.reverender.r95.ui.win95.Skin.classic
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.padding(top = 56.dp).width(320.dp)
            .background(if (classic) W95.Face else Color(0xFFEEF2F4))
            .border(2.dp, if (classic) Color.White else Color(0xFF2C3A47))) {
            Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(if (classic) listOf(Color(0xFF000080), Color(0xFF1084D0)) else listOf(Color(0xFF2C3A47), Color(0xFF11191D))))
                .padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                T("${if (a.kind == "error") "⚠" else "✉"} ${a.app}", size = 12.sp, bold = true, color = Color.White, modifier = Modifier.weight(1f), maxLines = 1)
                if (RetroAlerts.queue.size > 1) T("+${RetroAlerts.queue.size - 1}", size = 10.sp, color = Color.White)
            }
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                T(if (a.kind == "error") "⚠️" else if (a.kind == "msg") "💬" else "ℹ️", size = 26.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    if (a.title.isNotBlank()) T(a.title, size = 12.sp, bold = true, maxLines = 2)
                    T(a.text, size = 12.sp, maxLines = 5)
                }
            }
            Row(Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 8.dp), horizontalArrangement = Arrangement.End) {
                if (a.intent != null) { Btn("Open", { RetroAlerts.open(ctx, a) }, Modifier.width(76.dp)); Spacer(Modifier.width(6.dp)) }
                Btn("OK", { RetroAlerts.dismiss(a) }, Modifier.width(76.dp))
            }
        }
    }
    LaunchedEffect(a.key, a.at) { kotlinx.coroutines.delay(12_000); RetroAlerts.dismiss(a) }
}
