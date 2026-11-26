package com.reverender.r95.ui.apps

import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.foundation.verticalScroll

import androidx.compose.foundation.layout.heightIn

import androidx.compose.foundation.border

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.reverender.r95.LocalAppState
import com.reverender.r95.audio.CastBridge
import com.reverender.r95.audio.CastButton
import com.reverender.r95.core.Site
import com.reverender.r95.net.ArchiveClient
import com.reverender.r95.net.ArchiveItem
import com.reverender.r95.net.NetSurfClient
import com.reverender.r95.net.SubtitleClient
import com.reverender.r95.net.SubtitleItem
import com.reverender.r95.net.WebResult
import com.reverender.r95.net.YouTubeClient
import com.reverender.r95.net.YouTubeItem
import com.reverender.r95.ui.win95.Btn
import com.reverender.r95.ui.win95.Field
import com.reverender.r95.ui.win95.Lcd
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import com.reverender.r95.ui.win95.bevel
import kotlinx.coroutines.launch
import java.io.File

// ================================================================ VIDEO_STREAM_95

private val videoExt = setOf("mp4", "m4v", "webm", "ogv", "mov")

/** Shifts every SRT/VTT timestamp by [ms] (the −0.5 s / +0.5 s buttons). */
internal fun shiftSubtitles(text: String, ms: Long): String {
    if (ms == 0L) return text
    val re = Regex("(\\d{1,2}):(\\d{2}):(\\d{2})([,.])(\\d{3})")
    return re.replace(text) { m ->
        val (h, mi, s, sep, f) = m.destructured
        var t = h.toLong() * 3600000 + mi.toLong() * 60000 + s.toLong() * 1000 + f.toLong() + ms
        if (t < 0) t = 0
        "%02d:%02d:%02d%s%03d".format(t / 3600000, t / 60000 % 60, t / 1000 % 60, sep, t % 1000)
    }
}

internal fun srtToVtt(text: String): String =
    if (text.trimStart().startsWith("WEBVTT")) text else "WEBVTT\n\n" + text.replace(Regex("(\\d{2}:\\d{2}:\\d{2}),(\\d{3})"), "$1.$2")

internal val SUB_LANGS = listOf("en" to "English", "nl" to "Dutch", "de" to "German", "fr" to "French", "es" to "Spanish", "it" to "Italian", "pt" to "Portuguese", "pl" to "Polish",
    "sv" to "Swedish", "da" to "Danish", "no" to "Norwegian", "fi" to "Finnish", "tr" to "Turkish", "ru" to "Russian", "uk" to "Ukrainian", "cs" to "Czech", "hu" to "Hungarian",
    "ro" to "Romanian", "el" to "Greek", "ar" to "Arabic", "he" to "Hebrew", "ja" to "Japanese", "ko" to "Korean", "zh" to "Chinese", "hr" to "Croatian", "sr" to "Serbian",
    "sl" to "Slovenian", "bg" to "Bulgarian")
private val CUE_SIZES = listOf("s" to "Small", "m" to "Medium", "l" to "Large", "xl" to "Extra large")
private val NEON_PINK_V = Color(0xFFFF00A0)
private val NEON_CYAN_V = Color(0xFF00F7FF)

/** A Win95 drop-down (the site's <select class="win95-input">). */
@Composable
internal fun Select(value: String, options: List<Pair<String, String>>, onPick: (String) -> Unit, modifier: Modifier = Modifier) {
    var open by remember { mutableStateOf(false) }
    Box(modifier) {
        Row(Modifier.background(Color.White).bevel(false).clickable { open = true }.padding(horizontal = 4.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
            T(options.firstOrNull { it.first == value }?.second ?: value, size = 11.sp, maxLines = 1)
            T(" ▾", size = 10.sp)
        }
        androidx.compose.material3.DropdownMenu(open, { open = false }) {
            options.forEach { (k, l) -> androidx.compose.material3.DropdownMenuItem(text = { T(l, size = 12.sp) }, onClick = { open = false; onPick(k) }) }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoApp() {
    val state = LocalAppState.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val msf = remember {
        val http = DefaultHttpDataSource.Factory().setUserAgent(Site.USER_AGENT).setAllowCrossProtocolRedirects(true)
        DefaultMediaSourceFactory(DefaultDataSource.Factory(context, http))
    }
    val exo = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(msf)
            .setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(), false)
            .build()
    }
    var currentAudio by remember { mutableStateOf<Uri?>(null) }
    var currentHls by remember { mutableStateOf(false) }
    var sourceLabel by remember { mutableStateOf("ARCHIVE.ORG") }
    // w(): the five status fields
    var sTitle by remember { mutableStateOf("SYSTEM: STANDBY") }
    var sState by remember { mutableStateOf("READY") }
    var sDetail by remember { mutableStateOf("SEARCH INTERNET ARCHIVE / YOUTUBE OR LOAD A LOCAL VIDEO") }
    var sLeft by remember { mutableStateOf("ARCHIVE + YOUTUBE SEARCH: READY") }
    var sRight by remember { mutableStateOf("PLAYER: IDLE") }
    fun w(e: String, t: String, o: String, n: String? = null, r: String? = null) { sTitle = e; sState = t; sDetail = o; sLeft = n ?: "ARCHIVE: READY"; sRight = r ?: "PLAYER: IDLE" }

    var archiveQuery by remember { mutableStateOf("") }
    var ytQuery by remember { mutableStateOf("") }
    val archive = remember { mutableStateListOf<com.reverender.r95.net.ArchiveVideo>() }
    var archiveMsg by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var archiveVisible by remember { mutableStateOf(false) }
    var archiveJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    val youtube = remember { mutableStateListOf<YouTubeItem>() }
    var ytMsg by remember { mutableStateOf<String?>(null) }
    var nowTitle by remember { mutableStateOf("") }
    var current by remember { mutableStateOf<String?>(null) }   // N
    var isLocal by remember { mutableStateOf(false) }
    var currentUri by remember { mutableStateOf<Uri?>(null) }
    var showSearch by remember { mutableStateOf(false) }
    var subsOpen by remember { mutableStateOf(false) }
    var subsText by remember { mutableStateOf<String?>(null) }
    var subsStatus by remember { mutableStateOf("No subtitles loaded") }
    var subsOffset by remember { mutableStateOf(0L) }
    var cueSize by remember { mutableStateOf("m") }
    var subsQuery by remember { mutableStateOf("") }
    val subResults = remember { mutableStateListOf<SubtitleItem>() }
    var subLang by remember { mutableStateOf("en") }
    var playerView by remember { mutableStateOf<PlayerView?>(null) }
    val castUri = remember { arrayOfNulls<String>(1) }
    val playing = current != null
    DisposableEffect(Unit) {
        CastBridge.source = { castUri[0]?.let { u -> CastBridge.CastSource(u, nowTitle.ifEmpty { "VIDEO_STREAM_95" }, CastBridge.mimeFor(u), exo.currentPosition) { exo.pause() } } }
        CastBridge.onReturn = { at -> exo.seekTo(at); exo.play() }
        val l = object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(st: Int) {
                if (current == null) return
                when (st) {
                    androidx.media3.common.Player.STATE_BUFFERING -> w("VIDEO_STREAM_95", "BUFFERING", nowTitle, "SOURCE: STREAM", "PLAYER: BUFFERING")
                    androidx.media3.common.Player.STATE_ENDED -> w("VIDEO_STREAM_95", "ENDED", nowTitle, "SOURCE: STREAM", "PLAYER: ENDED")
                }
            }
            override fun onIsPlayingChanged(p: Boolean) {
                if (current == null) return
                if (p) w("VIDEO_STREAM_95", "PLAYING", nowTitle, "SOURCE: $sourceLabel", "PLAYER: NORMAL NATIVE AUDIO")
                else if (exo.playbackState == androidx.media3.common.Player.STATE_READY) w("VIDEO_STREAM_95", "PAUSED", nowTitle, "SOURCE: $sourceLabel", "PLAYER: READY")
            }
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                w("VIDEO_STREAM_95", "PLAYBACK ERROR", "MEDIA ERROR ${error.errorCode} — FORMAT OR STREAM MAY BE UNSUPPORTED", "TRY ANOTHER VIDEO FILE OR ARCHIVE ITEM", "PLAYER: ERROR")
            }
        }
        exo.addListener(l)
        onDispose { archiveJob?.cancel(); exo.removeListener(l); exo.release(); CastBridge.source = null; CastBridge.onReturn = null }
    }

    fun applyMedia(keepPosition: Boolean) {
        val uri = currentUri ?: return
        val pos = if (keepPosition) exo.currentPosition else 0L
        val builder = MediaItem.Builder().setUri(uri)
        subsText?.let { txt ->
            val f = File(context.cacheDir, "subs_${System.nanoTime()}.vtt")
            f.writeText(srtToVtt(shiftSubtitles(txt, subsOffset)))
            builder.setSubtitleConfigurations(listOf(
                MediaItem.SubtitleConfiguration.Builder(Uri.fromFile(f)).setMimeType(MimeTypes.TEXT_VTT).setLanguage(subLang)
                    .setSelectionFlags(C.SELECTION_FLAG_DEFAULT).build(),
            ))
        }
        if (currentHls) builder.setMimeType(MimeTypes.APPLICATION_M3U8)
        val audio = currentAudio
        if (audio == null) exo.setMediaItem(builder.build(), pos)
        else exo.setMediaSource(androidx.media3.exoplayer.source.MergingMediaSource(msf.createMediaSource(builder.build()), msf.createMediaSource(MediaItem.fromUri(audio))), pos)
        exo.prepare(); exo.play()
    }
    fun applyCueSize() { playerView?.subtitleView?.setFractionalTextSize(0.0533f * when (cueSize) { "s" -> 0.8f; "l" -> 1.3f; "xl" -> 1.65f; else -> 1f }) }

    /** ft(): play a URL or local file */
    fun ft(uri: Uri, title: String, local: Boolean) {
        state.player.duck()
        archiveJob?.cancel(); archiveVisible = false; showSearch = false
        currentUri = uri; current = uri.toString(); isLocal = local; nowTitle = title; currentAudio = null; currentHls = false; sourceLabel = if (local) "LOCAL FILE" else "ARCHIVE.ORG"
        subsText = null; subResults.clear(); subsOffset = 0; subsStatus = "No subtitles loaded"; subsQuery = title.substringBefore(" — ")
        castUri[0] = if (local) null else uri.toString()
        applyMedia(false)
        w(if (local) "LOCAL VIDEO" else "INTERNET ARCHIVE STREAM", "LOADING", title.ifEmpty { "VIDEO STREAM" }, if (local) "SOURCE: LOCAL FILE" else "SOURCE: ARCHIVE.ORG", "PLAYER: LOADING")
    }
    /** ne(): play a YouTube video here */
    fun playYoutube(videoId: String, title: String) {
        state.player.duck()
        archiveJob?.cancel(); showSearch = false
        w("VIDEO_STREAM_95", "LOADING", title.ifEmpty { "YOUTUBE VIDEO" }, "SOURCE: YOUTUBE", "PLAYER: LOADING")
        scope.launch {
            try {
                val p = com.reverender.r95.net.YtStreams.resolve(videoId)
                val uri = Uri.parse(p.video)
                currentUri = uri; current = uri.toString(); isLocal = false; nowTitle = title.ifEmpty { p.title }
                currentAudio = p.audio?.let(Uri::parse); currentHls = p.hls; sourceLabel = "YOUTUBE"
                subsText = null; subResults.clear(); subsOffset = 0; subsStatus = "No subtitles loaded"; subsQuery = nowTitle
                castUri[0] = if (p.audio == null) p.video else null
                applyMedia(false)
            } catch (x: kotlinx.coroutines.CancellationException) { throw x }
            catch (x: Exception) {
                android.util.Log.w("R95", "youtube", x)
                w("VIDEO_STREAM_95", "PLAYBACK ERROR", "YOUTUBE VIDEO COULD NOT BE LOADED", "TRY ANOTHER VIDEO", "PLAYER: ERROR")
            }
        }
    }
    LaunchedEffect(com.reverender.r95.core.RevyRequests.videoYoutube) {
        val (id, t) = com.reverender.r95.core.RevyRequests.videoYoutube ?: return@LaunchedEffect
        com.reverender.r95.core.RevyRequests.videoYoutube = null
        playYoutube(id, t)
    }
    /** stopVideo95Playback() */
    fun stop() {
        exo.stop(); exo.clearMediaItems(); currentUri = null; current = null; currentAudio = null; currentHls = false; castUri[0] = null; showSearch = false
        w("SYSTEM: STANDBY", "READY", "SEARCH INTERNET ARCHIVE / YOUTUBE OR LOAD A LOCAL VIDEO", "ARCHIVE + YOUTUBE SEARCH: READY", "PLAYER: IDLE")
    }

    val pickVideo = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val name = runCatching { context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c -> if (c.moveToFirst()) c.getString(0) else null } }.getOrNull() ?: (uri.lastPathSegment ?: "video")
        val ext = name.substringAfterLast('.', "").lowercase()
        if (ext.isNotEmpty() && ext !in setOf("mp4", "m4v", "webm", "ogv", "ogg", "mov")) {
            w("VIDEO INPUT", "REJECTED", "UNSUPPORTED VIDEO FILE: $name", "SUPPORTED: MP4 / M4V / WEBM / OGV / OGG / MOV", "PLAYER: IDLE"); return@rememberLauncherForActivityResult
        }
        ft(uri, name, true)
    }
    val pickSubs = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) runCatching {
            subsText = context.contentResolver.openInputStream(uri)!!.bufferedReader().use { it.readText() }
            applyMedia(true); subsStatus = "Subtitles: " + (uri.lastPathSegment ?: "file")
        }
    }

    /** zt(): deep Internet Archive search */
    fun searchArchive(text: String) {
        val t = com.reverender.r95.net.ArchiveVideoSearch.parse(text)
        archiveVisible = true; archive.clear()
        if (t.original.length < 2 || t.tokens.isEmpty()) { archiveMsg = "ENTER AT LEAST 2 CHARACTERS OF A VIDEO TITLE." to true; return }
        archiveJob?.cancel()
        archiveMsg = "DEEP SEARCHING INTERNET ARCHIVE MOVIES... SHOWING ONLY PLAYABLE VIDEO FILES OF 100 MB OR LARGER. FIRST MATCHES APPEAR AUTOMATICALLY." to false
        w("IA VIDEO SEARCH", "SEARCHING", "SEARCHING: " + t.title, "ARCHIVE: LIVE RESULTS + COLLECTION SEARCH", "PLAYER: READY")
        archiveJob = scope.launch {
            try {
                val (all, cols) = com.reverender.r95.net.ArchiveVideoSearch.search(text) { list ->
                    scope.launch {
                        archiveMsg = null; archive.clear(); archive.addAll(list.take(40))
                        w("IA VIDEO MATCHES", "FOUND", "${list.size} STREAMABLE VIDEO FILE(S) FOUND", "ARCHIVE: DEEP SEARCH CONTINUES", "PLAYER: READY")
                    }
                }
                if (all.isEmpty()) {
                    archive.clear(); archiveMsg = "NO PLAYABLE VIDEOS OF 100 MB OR LARGER FOUND. SMALLER FILES AND FILES WITHOUT VERIFIED SIZE METADATA ARE SKIPPED." to true
                    w("IA VIDEO SEARCH", "NO MATCHES", "NO STREAMABLE VIDEO FILES FOR: " + t.title, "ARCHIVE: NO MATCH IN ITEM OR COLLECTION SEARCH", "PLAYER: READY")
                } else {
                    archiveMsg = null; archive.clear(); archive.addAll(all.take(40))
                    w("IA VIDEO MATCHES", "COMPLETE", "${all.size} STREAMABLE VIDEO(S) FOR: " + t.title, "ARCHIVE: MOVIES · COLLECTIONS $cols", "PLAYER: READY")
                }
            } catch (x: kotlinx.coroutines.CancellationException) { throw x }
            catch (x: Exception) {
                archive.clear(); archiveMsg = "INTERNET ARCHIVE SEARCH FAILED. CHECK YOUR CONNECTION AND TRY AGAIN." to true
                w("IA VIDEO SEARCH", "ERROR", "INTERNET ARCHIVE VIDEO SEARCH FAILED", "ARCHIVE: ERROR", "PLAYER: READY")
            }
        }
    }
    /** Je(): YouTube search */
    fun searchYoutube(q0: String) {
        val q = q0.trim()
        if (q.length < 2) { ytMsg = "ENTER AT LEAST 2 CHARACTERS."; return }
        ytMsg = "SEARCHING YOUTUBE… RESULTS WILL APPEAR BELOW."; youtube.clear()
        scope.launch {
            try { val r = YouTubeClient.search(q); youtube.addAll(r); ytMsg = if (r.isEmpty()) "NO MATCHES" else null }
            catch (x: Exception) { ytMsg = "SEARCH PASS FAILED" }
        }
    }
    // Revy: video95 search forms / rrSearchYoutubeInVideo95
    LaunchedEffect(com.reverender.r95.core.RevyRequests.videoQuery) {
        val q = com.reverender.r95.core.RevyRequests.videoQuery ?: return@LaunchedEffect
        com.reverender.r95.core.RevyRequests.videoQuery = null
        if (com.reverender.r95.core.RevyRequests.videoSource == "archive") { archiveQuery = q; searchArchive(q) } else { ytQuery = q; searchYoutube(q) }
    }

    val mono = 10.sp
    Column(Modifier.fillMaxSize().padding(4.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        // .video95-topbar
        Column(Modifier.fillMaxWidth().background(Color.Black).border(1.dp, NEON_PINK_V).padding(5.dp)) {
            Row { T(sTitle, color = NEON_CYAN_V, bold = true, mono = true, size = mono, modifier = Modifier.weight(1f), maxLines = 1); T(sState, color = Color(0xFF00FF66), bold = true, mono = true, size = mono) }
            T(sDetail, color = Color.White, mono = true, size = 9.sp, maxLines = 2)
            Row(Modifier.padding(top = 4.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                Btn("OPEN FILE", { pickVideo.launch(arrayOf("video/*")) }, size = 10.sp)
                Box(Modifier.background(W95.Face).bevel(true)) { CastButton(Modifier.size(30.dp)) }
                if (playing) Btn(if (showSearch) "✕ HIDE SEARCH" else "🔎 SEARCH", { showSearch = !showSearch }, size = 10.sp)
                Btn("⏹ STOP", ::stop, size = 10.sp)
            }
        }
        // .video95-subs-bar
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Btn("Subtitles", { subsOpen = !subsOpen }, pressed = subsOpen, size = 10.sp)
            T(subsStatus, size = 10.sp, maxLines = 1)
            if (subsText != null) {
                Btn("−0.5s", { subsOffset -= 500; applyMedia(true) }, size = 10.sp)
                T("%.1fs".format(java.util.Locale.US, subsOffset / 1000f), size = 10.sp, mono = true)
                Btn("+0.5s", { subsOffset += 500; applyMedia(true) }, size = 10.sp)
            }
            T("Size", size = 10.sp)
            Select(cueSize, CUE_SIZES, { cueSize = it; applyCueSize() })
            if (subsText != null) Btn("Off", { subsText = null; subsStatus = "No subtitles loaded"; applyMedia(true) }, size = 10.sp)
        }
        // .video95-subs-panel
        if (subsOpen) Column(Modifier.fillMaxWidth().bevel(false).padding(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Field(subsQuery, { subsQuery = it.take(120) }, Modifier.weight(1f), hint = "Film or episode title (filled in from the loaded video)…")
                Select(subLang, SUB_LANGS, { subLang = it })
            }
            Row(Modifier.padding(top = 3.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Btn("Find", {
                    val q = subsQuery.trim(); if (q.isEmpty()) return@Btn
                    subsStatus = "Searching subtitles…"
                    scope.launch {
                        try { subResults.clear(); subResults.addAll(SubtitleClient.search(q, subLang)); subsStatus = "${subResults.size} subtitle files found" }
                        catch (e: Exception) { subsStatus = "Subtitle search failed" }
                    }
                }, size = 10.sp)
                Btn("Load file…", { pickSubs.launch(arrayOf("*/*")) }, size = 10.sp)
            }
            Column(Modifier.fillMaxWidth().heightIn(max = 120.dp).verticalScroll(rememberScrollState())) {
                subResults.forEach { sub ->
                    ResultRow(sub.title, "${sub.source} · ${sub.lang}") {
                        scope.launch {
                            try { subsText = SubtitleClient.get(sub.id).text; applyMedia(true); subsStatus = "Subtitles: ${sub.title}"; applyCueSize() }
                            catch (e: Exception) { subsStatus = "Subtitle download failed" }
                        }
                    }
                }
            }
            T("Sources: subtitle files inside the Internet Archive item, OpenSubtitles.org and Podnapisi.net (no key needed) and YouTube's own captions. Non-matching languages are translated on-device where the browser supports it.", size = 9.sp, color = W95.Shadow)
        }
        if (!playing || showSearch) {
            // #video95-youtube-search-area
            Column(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Field(ytQuery, { ytQuery = it.take(120) }, Modifier.weight(1f), hint = "Search YouTube: artist, song, film or show…", onDone = { searchYoutube(ytQuery) })
                    Spacer(Modifier.width(3.dp)); Btn("Search YouTube", { searchYoutube(ytQuery) }, size = 10.sp)
                }
                if (ytMsg != null || youtube.isNotEmpty()) Column(Modifier.padding(top = 3.dp).fillMaxWidth().heightIn(max = 108.dp).background(Color.Black).border(1.dp, NEON_PINK_V).verticalScroll(rememberScrollState()).padding(3.dp)) {
                    ytMsg?.let { T(it, color = NEON_CYAN_V, bold = true, mono = true, size = 10.sp) }
                    youtube.forEach { a ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                T(a.title, color = Color.White, bold = true, mono = true, size = 10.sp, maxLines = 2)
                                T((a.snippet.ifBlank { "MUSIC SEARCH RESULT" } + " · SELECT TO OPEN IN VIDEO_STREAM_95").take(260), color = Color(0xFFB8B8C8), mono = true, size = 9.sp, maxLines = 2)
                            }
                            Btn("▶ OPEN", { playYoutube(a.videoId, a.title) }, size = 9.sp)
                        }
                    }
                }
                T("Keyless YouTube search · only videos that allow embedding are listed · pick one to play it here", size = 9.sp, color = W95.Shadow)
            }
            // .video95-search-area (Archive)
            Column(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Field(archiveQuery, { archiveQuery = it.take(100) }, Modifier.weight(1f), hint = "Search Internet Archive: film or video title…", onDone = { searchArchive(archiveQuery) })
                    Spacer(Modifier.width(3.dp)); Btn("Search Archive", { searchArchive(archiveQuery) }, size = 10.sp)
                }
                if (archiveVisible) Column(Modifier.padding(top = 3.dp).fillMaxWidth().heightIn(max = 150.dp).background(Color.Black).border(1.dp, NEON_PINK_V).verticalScroll(rememberScrollState()).padding(3.dp)) {
                    archiveMsg?.let { (m, err) -> T(m, color = if (err) Color(0xFFFF8EC7) else NEON_CYAN_V, bold = true, mono = true, size = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)) }
                    archive.forEach { r ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                T(r.title, color = Color.White, bold = true, mono = true, size = 10.sp, maxLines = 2)
                                T(r.name + " · ." + r.ext.uppercase() + (if (r.size > 0) " · " + "%.1f".format(java.util.Locale.US, r.size / (1024.0 * 1024.0)) + " MB" else ""), color = Color(0xFFB8B8C8), mono = true, size = 9.sp, maxLines = 1)
                            }
                            T("IA PAGE", color = NEON_CYAN_V, mono = true, size = 9.sp, modifier = Modifier.clickable {
                                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://archive.org/details/" + Uri.encode(r.identifier)))) }
                            }.padding(horizontal = 5.dp))
                            Btn("▶ PLAY", { ft(Uri.parse(r.url), r.title + " — " + r.name, false) }, size = 9.sp)
                        }
                    }
                }
                T("Internet Archive · video files of 100 MB or more · MP4, M4V, WebM, OGV · load only content you are entitled to view", size = 9.sp, color = W95.Shadow)
            }
        }
        // #video95-viewport
        Box(Modifier.fillMaxWidth().weight(1f).heightIn(min = 150.dp).background(Color.Black).bevel(false)) {
            AndroidView(
                factory = { ctx -> PlayerView(ctx).apply { player = exo; setShowNextButton(false); setShowPreviousButton(false); playerView = this } },
                modifier = Modifier.fillMaxSize().graphicsLayer { alpha = if (playing) 1f else 0f },
            )
            if (!playing) Column(Modifier.align(Alignment.Center).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                T("📼", size = 34.sp)
                T("VIDEO_STREAM_95 READY", color = Color.White, bold = true, mono = true, size = 13.sp)
                T("SEARCH INTERNET ARCHIVE FOR FILMS, TV, DOCUMENTARIES AND OTHER VIDEOS", color = NEON_CYAN_V, mono = true, size = 9.sp, align = androidx.compose.ui.text.style.TextAlign.Center)
                T("SELECT A RESULT TO STREAM IT HERE — OR OPEN A LOCAL VIDEO FILE", color = NEON_CYAN_V, mono = true, size = 9.sp, align = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
        // .status-bar
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Box(Modifier.weight(1f).bevel(false).padding(horizontal = 3.dp, vertical = 1.dp)) { T(sLeft, size = 9.sp, mono = true, maxLines = 1) }
            Box(Modifier.weight(1f).bevel(false).padding(horizontal = 3.dp, vertical = 1.dp)) { T(sRight, size = 9.sp, mono = true, maxLines = 1) }
        }
    }
}

@androidx.compose.runtime.Composable
internal fun ResultRow(title: String, sub: String, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 6.dp, vertical = 6.dp)) {
        T(title, size = 12.sp, bold = true, maxLines = 2)
        if (sub.isNotBlank()) T(sub, size = 10.sp, color = W95.Shadow, maxLines = 2)
    }
}

// ================================================================ NET_SURF_95

/** window.BROWSER_HOME_URL */
private const val BROWSER_HOME_URL = "https://lite.duckduckgo.com/lite/"

/** I(): the site's address normalisation (search terms → DuckDuckGo Lite, google → DuckDuckGo). */
private fun browserNormalize(e: String): String {
    val t = e.trim()
    if (t.isEmpty()) return BROWSER_HOME_URL
    if (Regex("^about:", RegexOption.IGNORE_CASE).containsMatchIn(t)) return t
    val n = Regex("^[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)+([/?#:]|$)").containsMatchIn(t)
    val o = Regex("^https?://", RegexOption.IGNORE_CASE).containsMatchIn(t)
    val ddg = { q: String -> "https://lite.duckduckgo.com/lite/?q=" + java.net.URLEncoder.encode(q, "UTF-8").replace("+", "%20") }
    if (!o && !n) return ddg(t)
    val r = if (o) t else "https://$t"
    return try {
        val s = java.net.URI(r)
        if (Regex("(^|\\.)google\\.(com|nl)$", RegexOption.IGNORE_CASE).containsMatchIn(s.host ?: "")) {
            val a = s.rawQuery?.split("&")?.firstOrNull { it.startsWith("q=") }?.substring(2)?.let { java.net.URLDecoder.decode(it, "UTF-8") }
            if (!a.isNullOrEmpty()) ddg(a) else BROWSER_HOME_URL
        } else s.toString()
    } catch (x: Exception) { ddg(t) }
}

/** The DuckDuckGo Lite query of a URL, or null when the URL is not a DuckDuckGo Lite page. */
private fun ddgQuery(url: String): String? {
    if (!url.startsWith("https://lite.duckduckgo.com/lite")) return null
    val q = Regex("[?&]q=([^&#]*)").find(url)?.groupValues?.get(1) ?: return ""
    return java.net.URLDecoder.decode(q, "UTF-8")
}

/** NET_SURF_95: one browser window's state (history, index, currentUrl) as in rr-master-patch. */
@Composable
@kotlin.OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
fun BrowserApp(wm: com.reverender.r95.ui.desktop.WindowManager? = null, startUrl: String? = null) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val history = remember { mutableStateListOf(BROWSER_HOME_URL) }
    var index by remember { mutableStateOf(0) }
    var currentUrl by remember { mutableStateOf(BROWSER_HOME_URL) }
    var address by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("READY: REVERENDER SEARCH") }
    var mode by remember { mutableStateOf("MODE: SMART PROXY AUTO") }
    var results by remember { mutableStateOf<List<WebResult>>(emptyList()) }
    var content by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var job by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    var homeQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    /** navigateBrowser(e, push) */
    fun navigate(e: String, push: Boolean = true) {
        val i = browserNormalize(e)
        currentUrl = i; address = i
        if (push) {
            while (history.size > index + 1) history.removeAt(history.size - 1)
            if (history.last() != i) { history.add(i); index = history.size - 1 }
        }
        job?.cancel()
        status = "LOADING: $i"; mode = "MODE: DIRECT IFRAME"; loading = true
        job = scope.launch {
            try {
                val q = ddgQuery(i)
                if (q != null) { homeQuery = q; results = if (q.isBlank()) emptyList() else NetSurfClient.search(q) }
                else { content = NetSurfClient.read(i); listState.scrollToItem(0) }
                status = "ONLINE: $i"
            } catch (x: kotlinx.coroutines.CancellationException) { throw x }
            catch (x: Exception) {
                status = "PROXY FAILED — TRYING DIRECT: $i"; mode = "MODE: DIRECT FALLBACK"
                content = "## NET_SURF_95 could not retrieve this page\n\nThe live page providers did not return a usable response. This is usually caused by the website blocking proxy access or by a temporary provider outage.\n\n`$i`\n\n[Search this address with DuckDuckGo](https://lite.duckduckgo.com/lite/?q=" + java.net.URLEncoder.encode(i, "UTF-8") + ")\n\nTip: some websites block embedded browsers and must be opened in a new tab (↗ OPEN EXTERNAL)."
                if (ddgQuery(i) != null) results = emptyList()
            }
            loading = false
        }
    }
    LaunchedEffect(Unit) { if (startUrl != null) navigate(startUrl) }
    // Revy: revyOpenWebsiteInBrowser(url)
    LaunchedEffect(com.reverender.r95.core.RevyRequests.browserUrl) {
        val u = com.reverender.r95.core.RevyRequests.browserUrl ?: return@LaunchedEffect
        com.reverender.r95.core.RevyRequests.browserUrl = null
        val q = Regex("[?&]query=([^&]+)").find(u)?.groupValues?.get(1)
        if (u.contains("startpage.com") && q != null) navigate(java.net.URLDecoder.decode(q, "UTF-8")) else navigate(u)
    }
    val q = ddgQuery(currentUrl)

    Column(Modifier.fillMaxSize().padding(3.dp)) {
        FlowRow(verticalArrangement = Arrangement.spacedBy(3.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Btn("◄", { if (index - 1 >= 0) { index--; navigate(history[index], false) } })
            Btn("►", { if (index + 1 < history.size) { index++; navigate(history[index], false) } })
            Btn("↻", { navigate(currentUrl.ifEmpty { BROWSER_HOME_URL }, false) })
            Btn("🏠", { navigate(BROWSER_HOME_URL, true) })
            Field(address, { address = it }, Modifier.fillMaxWidth(), hint = "Enter a website or search the web...", onDone = { navigate(address, true) })
            Btn("GO", { navigate(address, true) })
            Btn("＋ NEW", { wm?.openNewBrowser(BROWSER_HOME_URL) })
            Btn("↗ OPEN EXTERNAL", {
                val r = browserNormalize(address.ifEmpty { currentUrl })
                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(r)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
            })
        }
        Box(Modifier.fillMaxWidth().weight(1f).padding(vertical = 4.dp).bevel(false).background(Color.White)) {
            if (q != null && !status.startsWith("PROXY FAILED")) {
                // DuckDuckGo Lite, drawn natively
                LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
                    item {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            T("DuckDuckGo", bold = true, size = 22.sp, color = Color(0xFFDE5833), modifier = Modifier.padding(vertical = 6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Field(homeQuery, { homeQuery = it }, Modifier.weight(1f), onDone = { navigate(homeQuery, true) })
                                Spacer(Modifier.width(4.dp))
                                Btn("Search", { navigate(homeQuery, true) })
                            }
                        }
                    }
                    items(results.size) { n ->
                        val r = results[n]
                        Column(Modifier.fillMaxWidth().clickable { navigate(r.url, true) }.padding(vertical = 6.dp)) {
                            T("${n + 1}.  ${r.title}", color = Color(0xFF1168CC), size = 13.sp, maxLines = 2)
                            if (r.snippet.isNotBlank()) T(r.snippet, color = Color(0xFF222222), size = 11.sp, maxLines = 4)
                            T(r.url.removePrefix("https://").removePrefix("http://"), color = Color(0xFF006D00), size = 10.sp, maxLines = 1)
                        }
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize().padding(8.dp), state = listState) {
                    val blocks = content.split(Regex("\n{2,}"))
                    items(blocks.size) { i -> MarkdownBlock(blocks[i]) { url -> navigate(url, true) } }
                }
            }
            if (loading) T("⏳", size = 20.sp, modifier = Modifier.align(Alignment.TopEnd).padding(6.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Box(Modifier.weight(1f).bevel(false).padding(horizontal = 3.dp, vertical = 1.dp)) { T(status, size = 9.sp, mono = true, maxLines = 1) }
            Box(Modifier.bevel(false).padding(horizontal = 3.dp, vertical = 1.dp)) { T(mode, size = 9.sp, mono = true, maxLines = 1) }
        }
    }
}

/** Minimal markdown: headings, bullet lists, bold and [links](url) become tappable. */
@Composable
private fun MarkdownBlock(raw: String, onLink: (String) -> Unit) {
    val block = raw.trim()
    if (block.isEmpty()) return
    val heading = Regex("^(#{1,6})\\s+(.*)").find(block)
    val text = heading?.groupValues?.get(2) ?: block
    val size = when (heading?.groupValues?.get(1)?.length) { 1 -> 20.sp; 2 -> 17.sp; 3 -> 15.sp; null -> 13.sp; else -> 14.sp }
    val linkRe = Regex("!?\\[([^\\]]*)]\\(([^)\\s]+)[^)]*\\)|\\*\\*([^*]+)\\*\\*")
    val annotated: AnnotatedString = buildAnnotatedString {
        var last = 0
        for (m in linkRe.findAll(text)) {
            append(text.substring(last, m.range.first))
            if (m.value.startsWith("!")) { /* images are skipped in reader mode */ }
            else if (m.groupValues[3].isNotEmpty()) withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(m.groupValues[3]) }
            else {
                pushStringAnnotation("url", m.groupValues[2])
                withStyle(SpanStyle(color = Color(0xFF1A0DAB), textDecoration = TextDecoration.Underline)) { append(m.groupValues[1].ifBlank { m.groupValues[2] }) }
                pop()
            }
            last = m.range.last + 1
        }
        append(text.substring(last))
    }
    @Suppress("DEPRECATION")
    ClickableText(
        annotated,
        style = W95.text.copy(fontSize = size, fontWeight = if (heading != null) FontWeight.Bold else FontWeight.Normal),
        modifier = Modifier.padding(vertical = 4.dp),
    ) { off ->
        annotated.getStringAnnotations("url", off, off).firstOrNull()?.let { a ->
            val u = a.item
            if (u.startsWith("http")) onLink(u)
        }
    }
}
