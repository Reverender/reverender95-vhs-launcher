package com.reverender.r95.ui.apps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.reverender.r95.LocalAppState
import com.reverender.r95.audio.AmpEngine
import com.reverender.r95.audio.CastBridge
import com.reverender.r95.audio.MicCapture
import com.reverender.r95.audio.SystemCapture
import com.reverender.r95.core.Catalog
import com.reverender.r95.net.ArchiveMp3Search
import com.reverender.r95.net.StationDirectory
import com.reverender.r95.net.YouTubeClient
import com.reverender.r95.net.YouTubeItem
import com.reverender.r95.ui.desktop.WindowManager
import com.reverender.r95.ui.viz.js.Dom
import com.reverender.r95.ui.viz.js.str
import com.reverender.r95.ui.viz.site.SiteCanvas
import com.reverender.r95.ui.viz.site.SiteHost
import com.reverender.r95.ui.win95.Accordion
import com.reverender.r95.ui.viz.SpectrumBars
import com.reverender.r95.ui.viz.argb
import com.reverender.r95.ui.win95.Check
import com.reverender.r95.ui.win95.Btn
import com.reverender.r95.ui.win95.Field
import com.reverender.r95.ui.win95.Lcd
import com.reverender.r95.ui.win95.SectionHeader
import com.reverender.r95.ui.win95.Skin
import com.reverender.r95.ui.win95.Slider95
import com.reverender.r95.ui.win95.SliderBox
import com.reverender.r95.ui.win95.StatusCell
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import com.reverender.r95.ui.win95.bevel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Text of a site element (the engine and the translated code write these). */
@Composable
private fun txt(id: String): String { SiteHost.uiFrame; return AmpEngine.text(id) }

/** The site's wallpaper order for the player's Wallpaper ◀ ▶ (rr-os-styles K). */
private val WALL_ORDER = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 24, 25, 26, 27, 28, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 11, 12)

/** VAPOR_AMP 95 — [MASTER DECK + RADIO], laid out and behaving like the website. */
@Composable
fun PlayerApp(wm: WindowManager) {
    val state = LocalAppState.current
    val p = state.player
    val context = LocalContext.current
    var localOpen by remember { mutableStateOf(false) }
    LaunchedEffect(p.connected) { while (true) { com.reverender.r95.core.AppVisibility.awaitForeground(); AmpEngine.tick(); delay(250) } }
    CastBridge.source = { p.currentUrl()?.let { u -> CastBridge.CastSource(u, p.nowPlaying.ifEmpty { "REVERENDER_95" }, CastBridge.mimeFor(u), p.positionMs()) { p.pause() } } }
    CastBridge.onReturn = { at -> if (p.durationMs() > 0) p.seekTo(at); p.play() }

    androidx.compose.runtime.DisposableEffect(Unit) { onDispose { com.reverender.r95.ui.desktop.TipAnchors.playerVisible = false } }
    Column(Modifier.fillMaxSize().onGloballyPositioned { c ->
        val b = c.boundsInRoot()
        com.reverender.r95.ui.desktop.TipAnchors.playerRight = b.right
        com.reverender.r95.ui.desktop.TipAnchors.playerVisible = true
    }) {
        // .win95-menubar
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 3.dp).horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            MenuItem("File(Randomize)") { AmpEngine.playRandomTrackAndOffset() }
            MenuItem("◀") { stepWall(state, -1) }; MenuItem("Wallpaper ▶") { stepWall(state, 1) }
            MenuItem("◀") {
                // prevTheme: in Longhorn it steps back through the accents, in Windows 95 through the colour themes
                if (state.osStyle == "longhorn") {
                    val l = com.reverender.r95.ui.win95.LH_ACCENTS; val i = l.indexOfFirst { it.id == state.lhAccent }
                    state.setLhAccent(l[(i - 1 + l.size) % l.size].id)
                } else state.cycleTheme(-1)
            }
            MenuItem("Theme ▶") { state.cycleTheme(1) }
            MenuItem("CRT(Toggle)") { state.setScanlines(!state.scanlines); AmpEngine.toggleCrt(state.scanlines) }
            MenuItem("Glitch(${if (state.bassGlitch) "On" else "Off"})") {
                state.setBassGlitch(!state.bassGlitch)
                if (state.bassGlitch) com.reverender.r95.ui.viz.site.SiteWallpaper.glitch(1.0)
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(W95.Shadow))
        Row(Modifier.fillMaxWidth().weight(1f).padding(4.dp)) {
            VolumeRail(Modifier.width(36.dp).fillMaxHeight())
            Spacer(Modifier.width(5.dp))
            Column(Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState())) {
                // native: another app (Spotify, YouTube Music …) is playing → these controls drive it
                val ectx = LocalContext.current
                LaunchedEffect(Unit) { while (true) { com.reverender.r95.core.AppVisibility.awaitForeground(); com.reverender.r95.os.ExternalMedia.refresh(ectx, p.isPlaying || MicCapture.running); kotlinx.coroutines.delay(1000) } }
                val ext = com.reverender.r95.os.ExternalMedia.inControl
                if (ext) ExternalLcd() else PlayerLcd()
                Spacer(Modifier.height(5.dp))
                if (ext) {
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Btn("⏮ PREV", { com.reverender.r95.os.ExternalMedia.previous(ectx) }, Modifier.weight(1f).height(38.dp))
                        Btn(if (com.reverender.r95.os.ExternalMedia.playing) "❚❚ PAUSE" else "▶ PLAY", { com.reverender.r95.os.ExternalMedia.playPause(ectx) }, Modifier.weight(1f).height(38.dp).anchor("play"), pressed = com.reverender.r95.os.ExternalMedia.playing)
                        Btn("NEXT ⏭", { com.reverender.r95.os.ExternalMedia.next(ectx) }, Modifier.weight(1f).height(38.dp))
                    }
                    Spacer(Modifier.height(5.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Btn("📼 BACK TO DECK", { com.reverender.r95.os.ExternalMedia.release(); AmpEngine.switchToInternalDeck() }, Modifier.weight(1f).height(38.dp), size = 12.sp)
                        CastTextButton(Modifier.weight(1f).height(38.dp))
                    }
                } else {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Btn(txt("btn-play-pause").let { if (it == "▶ PLAY") "PLAY" else it }, AmpEngine::togglePlayPause, Modifier.weight(1f).height(38.dp).anchor("play"))
                    Btn("JUMP POS", AmpEngine::jumpRandomPosition, Modifier.weight(1f).height(38.dp))
                }
                Spacer(Modifier.height(5.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Btn("RND SONG", AmpEngine::playRandomTrackAndOffset, Modifier.weight(1f).height(38.dp))
                    CastTextButton(Modifier.weight(1f).height(38.dp))
                }
                }
                Spacer(Modifier.height(5.dp))
                SiteHost.uiFrame
                val dur = if (ext) com.reverender.r95.os.ExternalMedia.durationMs else p.durationMs(); val pos = if (ext) com.reverender.r95.os.ExternalMedia.positionMs else p.positionMs()
                val pct = if (dur > 0) pos * 100f / dur else 0f
                SliderBox("TRACK POSITION SCRUBBER", "%.1f%%".format(pct), pct, 0f..100f, { v -> if (dur > 0) { if (ext) com.reverender.r95.os.ExternalMedia.seekTo((v / 100f * dur).toLong()) else p.seekTo((v / 100f * dur).toLong()) } })
                Spacer(Modifier.height(5.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    StatusCell(txt("status-mode"), Modifier.weight(1f))
                    StatusCell(txt("status-jump").replace("JUMP:", "LAST JUMP:").replace("LAST LAST", "LAST"), Modifier.weight(1f))
                }
                Spacer(Modifier.height(6.dp))
                // #win-source: Radio & input / WAVE ROUTER 95
                SectionHeader("Radio & input", "WAVE ROUTER 95")
                Column(Modifier.fillMaxWidth().background(W95.Face).then(if (Skin.classic) Modifier.bevel(true) else Modifier.border(1.dp, W95.SoftBorder)).padding(4.dp)) {
                    RouterButtons(onDeviceFiles = { localOpen = true })
                    Spacer(Modifier.height(4.dp))
                    var gain by remember { mutableStateOf(state.inputGain) }
                    SliderBox("SOURCE INPUT GAIN (PRE-EQ & DSP)", "${Math.round(gain * 100)}%", gain, 0.2f..2.5f, { gain = it; state.setInputGain(it) })
                    Spacer(Modifier.height(2.dp))
                    Accordion("Local files", "PC · mobile · tablet audio", Color(0xFF2C3A47), open = localOpen, onOpenChange = { localOpen = it }) { LocalFilesSection() }
                    Accordion("Internet Archive", "Search & import MP3 tracks", Color(0xFF00F7FF)) { ArchiveSection() }
                    Accordion("YouTube Music", "Search & play", Color(0xFFFF00A0)) { YouTubeSection() }
                    Box(Modifier.anchor("radio", headerOnly = true)) { Accordion("Internet radio", "Preset stations & custom stream", Color(0xFF7A3FC8)) { RadioSection() } }
                    Box(Modifier.anchor("search", headerOnly = true)) { Accordion("Station directory", "Worldwide live search", Color(0xFF2E9E5B)) { DirectorySection() } }
                    Row(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        StatusCell(txt("src-status-active"), Modifier.weight(1.3f))
                        StatusCell(txt("src-status-dsp"), Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Btn("VIDEO_STREAM_95", { wm.open("video") }, Modifier.weight(1f).height(32.dp), bold = true, size = 11.sp)
                    Btn("RETRO_EMU_95", { wm.open("emulator") }, Modifier.weight(1f).height(32.dp), bold = true, size = 11.sp)
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

private fun stepWall(state: com.reverender.r95.AppState, dir: Int) {
    val y = WALL_ORDER.indexOf(state.wallpaper)
    val next = if (y < 0) WALL_ORDER[if (dir > 0) 0 else WALL_ORDER.size - 1] else WALL_ORDER[(y + dir + WALL_ORDER.size) % WALL_ORDER.size]
    state.setWallpaper(next)
    if (state.bassGlitch) com.reverender.r95.ui.viz.site.SiteWallpaper.glitch(0.8)
}

/** Records where a startup-hint target sits on screen (centre Y; header rows use their first 40 dp). */
private fun Modifier.anchor(id: String, headerOnly: Boolean = false): Modifier = onGloballyPositioned { c ->
    val b = c.boundsInRoot()
    com.reverender.r95.ui.desktop.TipAnchors.centerY[id] = if (headerOnly) b.top + 20f * android.content.res.Resources.getSystem().displayMetrics.density else b.center.y
}

@Composable
private fun MenuItem(label: String, onClick: () -> Unit) {
    Box(Modifier.clickable(onClick = onClick).padding(vertical = 3.dp)) { T(label, size = 11.sp, maxLines = 1, bold = true) }
}

/** .player-vol-sidebar: VOL readout, vertical slider, GAIN tag. */
@Composable
private fun VolumeRail(modifier: Modifier) {
    val p = LocalAppState.current.player
    Column(
        modifier.background(W95.Lcd).then(if (Skin.classic) Modifier.bevel(false) else Modifier.border(1.dp, Color(0xFF87969F), RoundedCornerShape(3.dp))).padding(vertical = 6.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(Modifier.fillMaxWidth().background(Color(0x9E000000)).border(1.dp, Color(0xFF333333)).padding(vertical = 2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            T("VOL", color = W95.Cyan, size = 8.sp, bold = true, mono = true)
            T("${Math.round(p.volume * 100)}%", color = W95.Pink, size = 9.sp, bold = true, mono = true)
        }
        Spacer(Modifier.height(6.dp))
        Slider95(p.volume, { p.changeVolume(it); com.reverender.r95.audio.StaticNoise.volume = it * 0.1f }, Modifier.weight(1f).fillMaxWidth(), vertical = true, accent = Color(0xFF00F7FF))
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth().background(Color(0x9E000000)).border(1.dp, Color(0xFF222222)).padding(vertical = 2.dp), contentAlignment = Alignment.Center) {
            T("GAIN", color = W95.Cyan, size = 8.sp, bold = true, mono = true)
        }
    }
}

/** The LCD while VAPOR_AMP is remote-controlling another player. */
@Composable
private fun ExternalLcd() {
    val e = com.reverender.r95.os.ExternalMedia
    SiteHost.uiFrame
    Lcd(Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val name = if (e.hasDetails) listOf(e.artist, e.title).filter { it.isNotBlank() }.joinToString(" - ").ifBlank { "UNKNOWN TRACK" } else "EXTERNAL AUDIO"
                T((if (e.playing) "▶ " else "❚❚ ") + name.uppercase(), color = Color.White, bold = true, mono = true, size = 12.sp, maxLines = 1, modifier = Modifier.weight(1f).padding(end = 8.dp))
                if (e.hasDetails && e.durationMs > 0) T("%02d:%02d".format(e.positionMs / 60000, e.positionMs / 1000 % 60), color = Color.White, bold = true, mono = true, size = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                T("REMOTE: " + e.app.uppercase(), color = Color(0xFFFF66CC), bold = true, mono = true, size = 11.sp, maxLines = 1, modifier = Modifier.weight(1f))
                T(if (e.hasDetails) "SESSION LINK" else "MEDIA KEYS", color = Color(0xFF00FF66), bold = true, mono = true, size = 10.sp)
            }
            Spacer(Modifier.height(3.dp))
            SiteCanvas("mini-osc", Modifier.fillMaxWidth().height(40.dp))
        }
    }
}

/** .lcd-screen: track, time, status, buffer and the mini visualizer (tap cycles its 5 modes). */
@Composable
private fun PlayerLcd() {
    Lcd(Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                T(txt("lcd-track-name"), color = Color.White, bold = true, mono = true, size = 12.sp, maxLines = 1, modifier = Modifier.weight(1f).padding(end = 8.dp))
                T(txt("lcd-time"), color = Color.White, bold = true, mono = true, size = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                T(txt("lcd-status"), color = Color(0xFFFF66CC), bold = true, mono = true, size = 11.sp, maxLines = 1, modifier = Modifier.weight(1f))
                T(txt("lcd-buffer"), color = Color(0xFF00FF66), bold = true, mono = true, size = 10.sp)
            }
            Spacer(Modifier.height(3.dp))
            SiteCanvas("mini-osc", Modifier.fillMaxWidth().height(40.dp).clickable(interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }, indication = null) {
                runCatching { com.reverender.r95.ui.viz.sitemini.rrMiniOscClick() }
            })
        }
    }
}

/** CAST: the text button with the Cast route picker layered on top. */
@Composable
private fun CastTextButton(modifier: Modifier) {
    Box(modifier) {
        Btn("CAST", {}, Modifier.fillMaxSize())
        AndroidView(
            factory = { ctx ->
                val themed = android.view.ContextThemeWrapper(ctx, androidx.appcompat.R.style.Theme_AppCompat_Light)
                androidx.mediarouter.app.MediaRouteButton(themed).also { btn ->
                    btn.alpha = 0.02f
                    btn.setAlwaysVisible(true)
                    if (CastBridge.init(ctx)) runCatching { com.google.android.gms.cast.framework.CastButtonFactory.setUpMediaRouteButton(ctx.applicationContext, btn) }
                    else btn.visibility = View.GONE
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/** .source-mode-grid: Built-In Deck, Device Files, Share PC/Tab, Mic / Line-In. */
@Composable
private fun RouterButtons(onDeviceFiles: () -> Unit) {
    val context = LocalContext.current
    SiteHost.uiFrame
    val mode = AmpEngine.activeSourceMode
    val micOn = mode == "mic" && MicCapture.running
    val sysOn = mode == "system" && MicCapture.running
    var micText by remember { mutableStateOf<String?>(null) }
    var sysText by remember { mutableStateOf<String?>(null) }
    val askMic = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        micText = null
        if (ok && MicCapture.start()) AmpEngine.startedMic() else AmpEngine.micDenied()
    }
    val consent = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
        SystemCapture.pendingResult = { ok -> sysText = null; if (ok) AmpEngine.startedSystem() else AmpEngine.systemCanceled() }
        SystemCapture.onConsent(context, r.resultCode, r.data)
    }
    val askSysMic = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) consent.launch(SystemCapture.consentIntent(context)) else { sysText = null; AmpEngine.systemCanceled() }
    }
    AmpEngine.onStopCapture = { if (MicCapture.kind == "system") SystemCapture.stop(context) }
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Btn("Built-In Deck", { AmpEngine.switchToInternalDeck() }, Modifier.weight(1f).height(36.dp), pressed = mode == "deck")
        Btn("Device Files", onDeviceFiles, Modifier.weight(1f).height(36.dp), pressed = mode == "files")
        // native: the phone's whole music library as an Explorer tree (no picker each session)
        Btn("💾 C:\\MUSIC", { com.reverender.r95.net.RevyBrain.wm.open("music") }, Modifier.weight(0.8f).height(36.dp), size = 12.sp)
    }
    Spacer(Modifier.height(5.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Btn(sysText ?: if (sysOn) "Share PC/Tab: ON" else "Share PC/Tab: OFF", {
            when {
                sysOn -> AmpEngine.stopLiveCapture()
                !SystemCapture.supported -> AmpEngine.systemUnavailable()
                else -> {
                    sysText = "🖥️ PC/TAB: CONNECTING…"
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) consent.launch(SystemCapture.consentIntent(context))
                    else askSysMic.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }, Modifier.weight(1f).height(36.dp), pressed = sysOn, size = 12.sp)
        Btn(micText ?: if (micOn) "Mic / Line-In: ON" else "Mic / Line-In: OFF", {
            when {
                micOn -> AmpEngine.stopLiveCapture()
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED -> { if (MicCapture.start()) AmpEngine.startedMic() else AmpEngine.micDenied() }
                else -> { micText = "🎙️ MIC: CONNECTING…"; askMic.launch(Manifest.permission.RECORD_AUDIO) }
            }
        }, Modifier.weight(1f).height(36.dp), pressed = micOn, size = 12.sp)
    }
}

/** .win95-track-listbox */
@Composable
private fun ListBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier.fillMaxWidth().background(W95.Lcd).bevel(false).padding(2.dp)) { content() }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListRow(left: String, right: String, active: Boolean, onClick: () -> Unit, onLong: (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().background(if (active) W95.Pink else Color.Transparent)
            .combinedClickable(onClick = onClick, onLongClick = onLong).padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        T(left, color = if (active) Color.White else Color(0xFFC8F7FF), mono = true, bold = true, size = 11.sp, maxLines = 1, modifier = Modifier.weight(1f))
        if (right.isNotEmpty()) T(right, color = if (active) Color.White else Color(0xFFC8F7FF), mono = true, bold = true, size = 11.sp)
    }
}

@Composable
private fun Note(text: String, color: Color = Color(0xFF99AABB)) = T(text, color = color, size = 9.sp, mono = true, modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), align = androidx.compose.ui.text.style.TextAlign.Center)

@Composable
private fun LocalFilesSection() {
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri> ->
        val files = uris.map { uri ->
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            val name = runCatching {
                context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c -> if (c.moveToFirst()) c.getString(0) else null }
            }.getOrNull() ?: uri.lastPathSegment ?: "Track"
            AmpEngine.CustomFile(name, uri.toString())
        }
        AmpEngine.handleCustomFilesSelected(files)
    }
    Column {
        Box(
            Modifier.fillMaxWidth().background(W95.Lcd).border(1.dp, W95.Cyan).clickable { picker.launch(arrayOf("audio/*")) }.padding(6.dp),
            contentAlignment = Alignment.Center,
        ) { T("Click or drop audio files here · MP3, WAV, FLAC, OGG, M4A", size = 10.sp, mono = true, color = W95.Cyan, align = androidx.compose.ui.text.style.TextAlign.Center) }
        Spacer(Modifier.height(3.dp))
        SiteHost.uiFrame
        ListBox(Modifier.heightIn(min = 68.dp)) {
            if (AmpEngine.customAudioFiles.isEmpty()) T("No device files loaded yet", color = Color(0xFF888888), mono = true, size = 11.sp, modifier = Modifier.padding(6.dp))
            AmpEngine.customAudioFiles.forEachIndexed { i, f ->
                val cur = AmpEngine.isCurrentFile(i)
                ListRow("%02d. %s".format(i + 1, f.name.ifEmpty { "UNTITLED AUDIO" }), if (cur) "▶ PLAY" else if (f.archiveUrl != null) "IA MP3" else "FILE", cur,
                    { AmpEngine.playCustomLocalFile(i) }, { AmpEngine.removeCustomFileFromPlaylist(i) })
            }
        }
        Note("Tap to play · hold to remove from the playlist")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArchiveSection() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    val hits = remember { mutableStateListOf<ArchiveMp3Search.Hit>() }
    var job by remember { mutableStateOf<Job?>(null) }
    var selected by remember { mutableStateOf("") }
    fun search() {
        job?.cancel()
        hits.clear(); status = null
        job = scope.launch {
            ArchiveMp3Search.search(query) { s ->
                when (s) {
                    is ArchiveMp3Search.Status.Msg -> { status = s.text to s.error; if (s.error) hits.clear() }
                    is ArchiveMp3Search.Status.Results -> { status = null; hits.clear(); hits.addAll(s.hits.take(40)) }
                }
            }
        }
    }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
            Field(query, { query = it.take(100) }, Modifier.weight(1f), hint = "Search artist, track or album…", onDone = ::search)
            Spacer(Modifier.width(4.dp)); Btn("Search", ::search, size = 11.sp)
        }
        Box(Modifier.fillMaxWidth().border(1.dp, Color(0x7300F7FF)).background(Color(0x1700F7FF)).padding(horizontal = 6.dp, vertical = 5.dp)) {
            T("Tap to play now · hold to add to the playlist", color = W95.Cyan, size = 10.sp, bold = true)
        }
        if (status != null || hits.isNotEmpty()) {
            Column(Modifier.fillMaxWidth().padding(vertical = 3.dp).background(Color(0xB8000000)).border(1.dp, Color(0x5900F7FF)).padding(3.dp)) {
                status?.let { (t, err) -> T(t, color = if (err) Color(0xFFFF8EC7) else W95.Cyan, bold = true, mono = true, size = 10.sp, modifier = Modifier.padding(4.dp)) }
                SiteHost.uiFrame
                hits.forEach { h ->
                    val key = h.identifier + "/" + h.path
                    val added = AmpEngine.inPlaylist(h.url)
                    Row(
                        Modifier.fillMaxWidth().background(if (selected == key) Color(0x1F00F7FF) else if (added) Color(0x1200FF88) else Color.Transparent)
                            .combinedClickable(
                                onClick = { selected = key; job?.cancel(); status = null; AmpEngine.importArchive(h.title, h.name, h.url, playNow = true) },
                                onLongClick = { AmpEngine.importArchive(h.title, h.name, h.url, playNow = false) },
                            ).padding(vertical = 5.dp, horizontal = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            T(h.title, color = Color.White, mono = true, size = 10.sp, bold = true, maxLines = 1)
                            T("${h.name} · .MP3 · ${"%.1f".format(h.size / 1048576.0)} MiB", color = Color(0xFF9AB0BB), mono = true, size = 9.sp, maxLines = 1)
                        }
                        T("IA", color = W95.Pink, bold = true, mono = true, size = 10.sp, modifier = Modifier.clickable {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://archive.org/details/" + Uri.encode(h.identifier))))
                        }.padding(4.dp))
                        if (added) T("IN TRACKLIST", color = Color(0xFF00FF88), bold = true, mono = true, size = 8.sp)
                    }
                }
            }
        }
        Note("MP3 files of 4 MB or more · added tracks join the local playlist and play on automatically")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun YouTubeSection() {
    val p = LocalAppState.current.player
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Results appear here · pick a track to play it in VIDEO_STREAM_95.") }
    val results = remember { mutableStateListOf<YouTubeItem>() }
    fun search() {
        val q = query.trim(); if (q.isEmpty()) return
        status = "Searching…"
        scope.launch { try { results.clear(); results.addAll(YouTubeClient.search(q)); status = "${results.size} results" } catch (e: Exception) { status = "Search failed: ${e.message}" } }
    }
    // Revy: radio-youtube-music-form
    LaunchedEffect(com.reverender.r95.core.RevyRequests.musicQuery) {
        val q = com.reverender.r95.core.RevyRequests.musicQuery ?: return@LaunchedEffect
        com.reverender.r95.core.RevyRequests.musicQuery = null
        query = q; search()
    }
    // ne(): the pick plays in VIDEO_STREAM_95
    fun open(r: YouTubeItem) { com.reverender.r95.core.RevyRequests.videoYoutube = r.videoId to r.title; com.reverender.r95.net.RevyBrain.wm.open("video") }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
            Field(query, { query = it.take(120) }, Modifier.weight(1f), hint = "Search artist, song or album…", onDone = ::search)
            Spacer(Modifier.width(4.dp)); Btn("Search", ::search, size = 11.sp)
        }
        Box(Modifier.fillMaxWidth().border(1.dp, Color(0x9EFF00A0)).background(Color(0x1AFF00A0)).padding(horizontal = 6.dp, vertical = 5.dp)) {
            T("Tap to play now · hold to add to the queue", color = W95.Cyan, size = 10.sp, bold = true)
        }
        T(status, color = Color(0xFF99AABB), mono = true, size = 9.sp, modifier = Modifier.padding(vertical = 2.dp))
        if (results.isNotEmpty()) Column(Modifier.fillMaxWidth().background(Color(0xC7000000)).border(1.dp, W95.Pink).padding(3.dp)) {
            results.forEach { r ->
                Column(Modifier.fillMaxWidth().combinedClickable(onClick = { open(r) }, onLongClick = { p.queueNext(com.reverender.r95.core.Track(r.title, "https://music.youtube.com/watch?v=${r.videoId}")) }).padding(vertical = 5.dp)) {
                    T(r.title, color = Color.White, mono = true, size = 10.sp, bold = true, maxLines = 1)
                    if (r.snippet.isNotEmpty()) T(r.snippet, color = Color(0xFF9AB0BB), mono = true, size = 9.sp, maxLines = 1)
                }
            }
        }
        if (p.upNext.isNotEmpty()) Column(Modifier.fillMaxWidth().padding(top = 4.dp).background(Color(0xC7000000)).border(1.dp, Color(0x8C00F7FF)).padding(4.dp)) {
            T("▶ NOW PLAYING / UP NEXT", color = W95.Cyan, bold = true, mono = true, size = 9.sp)
            p.upNext.forEachIndexed { i, t ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T("${i + 1}. ${t.title}", color = Color.White, size = 10.sp, mono = true, maxLines = 1, modifier = Modifier.weight(1f))
                    Box(Modifier.clickable { p.upNext.removeAt(i) }.padding(6.dp)) { T("✕", color = W95.Pink) }
                }
            }
        }
        Note("Tap to play now · right-click or long-press to add it to Play next.", W95.Pink)
    }
}

/** #radio-tuner-fieldset */
@Composable
private fun RadioSection() {
    SiteHost.uiFrame
    val el = Dom.el("custom-radio-url")
    var url by remember { mutableStateOf(str(el.prop("value") ?: "")) }
    LaunchedEffect(SiteHost.uiFrame) { val v = str(el.prop("value") ?: ""); if (v != url && v.isNotEmpty()) url = v }
    val names = listOf("Nightwave Plaza", "Smooth 80s London", "US Jazz Station", "US Country Classics", "Vaporjazz Radio", "181.fm The Buzz", "SomaFM Vaporwaves", "SomaFM DEF CON")
    Column {
        names.chunked(2).forEachIndexed { r, pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.padding(vertical = 1.5.dp)) {
                pair.forEachIndexed { c, n ->
                    val i = r * 2 + c
                    Btn(n, { AmpEngine.playInternetRadio(i) }, Modifier.weight(1f).height(32.dp), pressed = AmpEngine.activeSourceMode == "radio" && AmpEngine.pressedStation == i, size = 10.sp)
                }
            }
        }
        Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Field(url, { url = it; el.setProp("value", it) }, Modifier.weight(1f), hint = "Stream URL (MP3 / AAC / Icecast)…", onDone = { AmpEngine.playCustomStreamUrl() })
            Spacer(Modifier.width(3.dp)); Btn("Tune in", { AmpEngine.playCustomStreamUrl() }, size = 11.sp)
        }
    }
}

/** #radio-search-fieldset */
@Composable
private fun DirectorySection() {
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var info by remember { mutableStateOf<Pair<String, Color>?>("Type a keyword and press Search to browse live stations" to Color(0xFF888888)) }
    val rows = remember { mutableStateListOf<StationDirectory.Row>() }
    var active by remember { mutableStateOf(-1) }
    var last by remember { mutableStateOf(0L) }
    fun search() {
        val now = System.currentTimeMillis(); if (now - last < 200) return; last = now
        val g = query.trim().ifEmpty { "vaporwave" }.replace(Regex("[<>&\"']"), "")
        rows.clear(); active = -1
        info = "🔎 Searching live stations for \"$g\"…" to W95.Cyan
        scope.launch {
            val (r, unavailable) = StationDirectory.search(query)
            info = if (unavailable) "Directory unavailable; showing the preset station list." to Color(0xFFFFCC66) else null
            rows.addAll(r.take(35))
        }
    }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Field(query, { query = it }, Modifier.weight(1f), hint = "Genre, name or country (e.g. synthwave, lofi)…", onDone = ::search)
            Spacer(Modifier.width(3.dp)); Btn("Search", ::search, size = 11.sp)
        }
        Spacer(Modifier.height(4.dp))
        ListBox(Modifier.heightIn(min = 56.dp)) {
            info?.let { (t, c) -> T(t, color = c, mono = true, bold = true, size = 11.sp, modifier = Modifier.padding(6.dp)) }
            rows.forEachIndexed { n, e ->
                ListRow("%02d. %s".format(n + 1, e.name.take(34)), "[${e.country.ifEmpty { "LIVE" }}]", active == n, {
                    active = n
                    Dom.el("custom-radio-url").setProp("value", e.url)
                    AmpEngine.connectRadioStream(e.url, e.name.take(22))
                })
            }
        }
    }
}

