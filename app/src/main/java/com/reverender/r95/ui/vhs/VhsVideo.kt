package com.reverender.r95.ui.vhs

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.reverender.r95.LocalAppState
import com.reverender.r95.core.Site
import com.reverender.r95.os.Haptics
import com.reverender.r95.os.OsPrefs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * The VHS theme's video deck: LOAD (hold) opens it. Type a search on the OSD letters (YouTube or the
 * Internet Archive, like VIDEO_STREAM_95) or open a file, then the tape plays right here in the launcher,
 * under the CRT, with its own picture effects (colour, softness, colour fringe, snow) and VCR controls.
 */

// ---------------------------------------------------------------- hold-to-load key + tape sound

/** A boxed key that has to be held: it fills like a tape going in, then fires on release once full. */
@Composable
internal fun VhsHoldKey(text: String, holdMs: Long = 1000, scale: Float = 1f, sound: Boolean = true, selected: Boolean = false, onLoaded: () -> Unit) {
    val o = LocalOsd.current
    var progress by remember { mutableFloatStateOf(0f) }
    val d = LocalDensity.current
    val unit = o.px * scale
    val wDp = with(d) { ((text.length * 6 + 3) * unit).toDp() }
    val hDp = with(d) { (10 * unit).toDp() }
    val fire by androidx.compose.runtime.rememberUpdatedState(onLoaded)
    Canvas(
        Modifier.size(wDp, hDp).pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                down.consume()
                val t0 = System.currentTimeMillis()
                if (sound) { TapeSfx.start(); Haptics.hum(false) } else Haptics.tick()
                var full = false
                while (true) {
                    val ev = withTimeoutOrNull(16) { awaitPointerEvent() }
                    progress = ((System.currentTimeMillis() - t0).toFloat() / holdMs).coerceIn(0f, 1f)
                    if (progress >= 1f && !full) { full = true; if (sound) TapeSfx.loaded(); Haptics.clunk("play") }
                    if (ev == null) continue
                    val c = ev.changes.firstOrNull { it.id == down.id } ?: break
                    c.consume()
                    if (!c.pressed) break
                }
                if (sound) Haptics.stopHum()
                if (full) fire() else if (sound) TapeSfx.cancel()
                progress = 0f
            }
        },
    ) {
        val px = unit
        val sh = Color.Black.copy(alpha = 0.7f)
        if (selected && progress == 0f) {
            glowRect(o.fg, Offset.Zero, Size(size.width - px * 0.6f, size.height - px * 0.6f), px)
            drawRect(o.fg, Offset.Zero, Size(size.width - px * 0.6f, size.height - px * 0.6f))
            drawGlyphs(text, px * 1.5f, px * 1.2f, px, null, o.bg)
            return@Canvas
        }
        drawRect(sh, Offset(px * 0.6f, px * 0.6f), Size(size.width - px * 0.6f, size.height - px * 0.6f), style = androidx.compose.ui.graphics.drawscope.Stroke(px * 0.5f))
        glowRect(o.fg, Offset.Zero, Size(size.width - px * 0.6f, size.height - px * 0.6f), px, px * 0.5f)
        drawRect(o.fg, Offset.Zero, Size(size.width - px * 0.6f, size.height - px * 0.6f), style = androidx.compose.ui.graphics.drawscope.Stroke(px * 0.5f))
        val fillW = (size.width - px * 0.6f) * progress
        if (fillW > 0f) drawRect(o.fg, Offset.Zero, Size(fillW, size.height - px * 0.6f))
        if (OsPrefs.vhsBloom) drawBloom(text, px * 1.5f, px * 1.2f, px, o.fg)
        drawGlyphs(text, px * 1.5f, px * 1.2f, px, if (progress > 0f) null else sh, o.fg)
        // the part of the label under the fill is drawn inverted
        if (fillW > 0f) {
            clipRectCompat(fillW) { drawGlyphs(text, px * 1.5f, px * 1.2f, px, null, o.bg) }
        }
    }
}

private inline fun androidx.compose.ui.graphics.drawscope.DrawScope.clipRectCompat(right: Float, block: androidx.compose.ui.graphics.drawscope.DrawScope.() -> Unit) {
    drawContext.canvas.save()
    drawContext.canvas.clipRect(0f, 0f, right, size.height)
    block()
    drawContext.canvas.restore()
}

/**
 * A synthesized VCR tape load: the door latch, the loading motor and gears winding up while you hold,
 * then the heavy clunk of the tape seating (or a short pop if you let go early).
 */
internal object TapeSfx {
    private const val SR = 22050
    @Volatile private var running = false
    @Volatile private var endKind = 0 // 0 none, 1 loaded clunk, 2 cancel pop

    fun start() {
        if (running) return
        running = true; endKind = 0
        Thread({
            val track = runCatching {
                com.reverender.r95.audio.makeTrack(SR, false, false, android.media.AudioAttributes.USAGE_MEDIA, android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION, SR / 4)
            }.getOrNull() ?: run { running = false; return@Thread }
            runCatching { track.setVolume(0.10f) } // a quiet mechanism, not a jump scare
            val buf = ShortArray(512)
            var n = 0L
            var ph = 0.0; var lp = 0.0
            val rnd = java.util.Random()
            try {
                track.play()
                while (running) {
                    for (i in buf.indices) {
                        val t = n / SR.toDouble(); n++
                        // latch click in the first 30 ms
                        val latch = if (t < 0.03) (rnd.nextDouble() * 2 - 1) * kotlin.math.exp(-t * 160) * 0.9 else 0.0
                        // motor: a buzzy saw that spins up from 70 to 150 Hz
                        val up = kotlin.math.min(1.0, t / 0.8)
                        val f = 70 + 80 * up
                        ph += f / SR; if (ph > 1) ph -= 1
                        val saw = (ph * 2 - 1) * 0.25
                        // gear rattle: noise gated at ~18 Hz
                        lp += ((rnd.nextDouble() * 2 - 1) - lp) * 0.25
                        val gate = if (kotlin.math.sin(2 * Math.PI * (14 + 8 * up) * t) > 0.6) 1.0 else 0.15
                        val amp = kotlin.math.min(1.0, t / 0.12) * 0.55
                        buf[i] = ((latch + (saw + lp * 0.35 * gate) * amp) * 9000).toInt().coerceIn(-32000, 32000).toShort()
                    }
                    track.write(buf, 0, buf.size)
                }
                // the ending
                val end = when (endKind) {
                    1 -> ShortArray((SR * 0.32).toInt()) { i ->
                        val t = i / SR.toDouble()
                        val thump = kotlin.math.sin(2 * Math.PI * 62 * t) * kotlin.math.exp(-t * 18) * 0.9
                        val clack = (rnd.nextDouble() * 2 - 1) * kotlin.math.exp(-t * 90) * 0.7 +
                            (if (t > 0.09) (rnd.nextDouble() * 2 - 1) * kotlin.math.exp(-(t - 0.09) * 120) * 0.45 else 0.0)
                        ((thump + clack) * 12000).toInt().coerceIn(-32000, 32000).toShort()
                    }
                    2 -> ShortArray((SR * 0.08).toInt()) { i ->
                        val t = i / SR.toDouble()
                        ((rnd.nextDouble() * 2 - 1) * kotlin.math.exp(-t * 70) * 0.6 * 9000).toInt().toShort()
                    }
                    else -> ShortArray(0)
                }
                if (end.isNotEmpty()) track.write(end, 0, end.size)
                Thread.sleep(80)
            } catch (_: Exception) {
            } finally {
                runCatching { track.stop() }; runCatching { track.release() }
            }
        }, "r95-tape").start()
    }
    fun loaded() { endKind = 1; running = false }
    fun cancel() { if (running) { endKind = 2; running = false } }
}


/**
 * Everything the video deck holds, kept by the VHS shell while you go home and back: the loaded tape and
 * its player, the search and its results, subtitles and their settings.
 */
internal class VideoDeck {
    var title by mutableStateOf(""); var loaded by mutableStateOf(false); var playing by mutableStateOf(false)
    var status by mutableStateOf("INSERT TAPE"); var aspect by mutableFloatStateOf(4f / 3f); var searching by mutableStateOf(true)
    var fxOpen by mutableStateOf(false); var speedSel by mutableStateOf("SP"); var vol by mutableFloatStateOf(1f)
    var query by mutableStateOf(""); var source by mutableStateOf("YT")
    val hits = mutableStateListOf<TapeHit>()
    var msg by mutableStateOf<String?>("TYPE A TITLE, PRESS OK")
    var tick by mutableLongStateOf(0L)
    var curItem by mutableStateOf<MediaItem?>(null); var curAudio by mutableStateOf<Uri?>(null); var curHls by mutableStateOf(false)
    var subsOpen by mutableStateOf(false); var subsText by mutableStateOf<String?>(null); var subsName by mutableStateOf("OFF")
    var subsOffset by mutableLongStateOf(0L); var subLang by mutableStateOf("en"); var subQuery by mutableStateOf("")
    var subMsg by mutableStateOf<String?>(null)
    val subHits = mutableStateListOf<com.reverender.r95.net.SubtitleItem>()
    var cueSize by mutableFloatStateOf(1f); var cue by mutableStateOf("")
    var fullscreen by mutableStateOf(false)
    /** LOAD shows the video deck or the game deck */
    var mode by mutableStateOf("video")
    /** what CAST sends to the TV (a single http stream), null when this tape can't be cast */
    var castUrl: String? = null

    private var factory: DefaultMediaSourceFactory? = null
    private var player: ExoPlayer? = null
    fun msf(ctx: android.content.Context): DefaultMediaSourceFactory = factory ?: run {
        val http = DefaultHttpDataSource.Factory().setUserAgent(Site.USER_AGENT).setAllowCrossProtocolRedirects(true)
        DefaultMediaSourceFactory(DefaultDataSource.Factory(ctx, http)).also { factory = it }
    }
    fun exo(ctx: android.content.Context): ExoPlayer = player ?: ExoPlayer.Builder(ctx.applicationContext).setMediaSourceFactory(msf(ctx.applicationContext))
        .setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(), true)
        .setWakeMode(C.WAKE_MODE_NETWORK) // keeps streaming with the screen off
        .build().also { exo ->
            player = exo
            exo.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(p: Boolean) {
                    playing = p; if (p) status = "PLAY"
                    // a media foreground service while the tape plays, so sleep doesn't stop the sound
                    if (p || exo.playbackState != Player.STATE_BUFFERING) com.reverender.r95.audio.VhsKeepAlive.set(ctx, p, "VHS TAPE")
                }
                override fun onPlaybackStateChanged(st: Int) {
                    when (st) {
                        Player.STATE_BUFFERING -> status = "TRACKING…"
                        Player.STATE_ENDED -> { status = "END OF TAPE"; playing = false }
                        Player.STATE_READY -> if (!exo.isPlaying) status = "PAUSE"
                    }
                }
                override fun onVideoSizeChanged(v: VideoSize) {
                    if (v.width > 0 && v.height > 0) aspect = v.width * v.pixelWidthHeightRatio / v.height
                }
                override fun onCues(cueGroup: androidx.media3.common.text.CueGroup) {
                    cue = cueGroup.cues.mapNotNull { it.text?.toString() }.joinToString("\n")
                }
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) { status = "TAPE ERROR"; msg = "THIS TAPE WON'T PLAY. TRY ANOTHER."; searching = true }
            })
        }
    fun pause() { player?.pause() }
    fun release() { player?.release(); player = null }
}

// ---------------------------------------------------------------- the video deck

internal class TapeHit(val title: String, val sub: String, val play: () -> Unit)

@Composable
internal fun VhsVideo(d: VideoDeck, onExit: () -> Unit, onSwitch: (String) -> Unit = {}) {
    val ctx = LocalContext.current
    val state = LocalAppState.current
    val scope = rememberCoroutineScope()
    val o = LocalOsd.current
    val msf = d.msf(ctx)
    val exo = d.exo(ctx)
    var title by d::title; var loaded by d::loaded; val playing = d.playing
    var status by d::status; val aspect = d.aspect; var searching by d::searching
    var fxOpen by d::fxOpen; var speedSel by d::speedSel; var vol by d::vol
    var query by d::query; var source by d::source
    val hits = d.hits
    var msg by d::msg
    var job by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    val blink = rememberBlink()
    var tick by d::tick
    var curItem by d::curItem; var curAudio by d::curAudio; var curHls by d::curHls
    var subsOpen by d::subsOpen; var subsText by d::subsText; var subsName by d::subsName
    var subsOffset by d::subsOffset; var subLang by d::subLang; var subQuery by d::subQuery
    var subMsg by d::subMsg
    val subHits = d.subHits
    var cueSize by d::cueSize; var cue by d::cue
    var fullscreen by d::fullscreen
    val fxOn = OsPrefs.vidFxOn
    val openKb = LocalOpenKb.current

    // leaving the deck (home): the tape stays in, paused, with its place, search and subtitles
    DisposableEffect(Unit) { onDispose { job?.cancel(); exo.pause(); d.fullscreen = false } }
    // the tape is out of sight while searching: pause it
    LaunchedEffect(searching) { if (searching) exo.pause() }
    // back: full screen → tape → search → home
    androidx.activity.compose.BackHandler(enabled = loaded && !searching && !subsOpen) { if (fullscreen) fullscreen = false else searching = true }
    // CAST: hand the tape to a TV (single-stream tapes; YouTube with separate audio can't be cast)
    DisposableEffect(Unit) {
        com.reverender.r95.audio.CastBridge.source = {
            d.castUrl?.let { u -> com.reverender.r95.audio.CastBridge.CastSource(u, title.ifEmpty { "VHS TAPE" }, com.reverender.r95.audio.CastBridge.mimeFor(u), exo.currentPosition) { exo.pause() } }
        }
        com.reverender.r95.audio.CastBridge.onReturn = { at -> exo.seekTo(at); exo.play() }
        onDispose { com.reverender.r95.audio.CastBridge.source = null; com.reverender.r95.audio.CastBridge.onReturn = null }
    }
    // FX OFF also takes the CRT and glitches off the video while it shows
    val showingTape = loaded && !searching && !subsOpen
    // full screen: every effect off (CRT, glitches, picture FX) — just the film
    androidx.compose.runtime.SideEffect { VhsCrtGate.off = (showingTape && !fxOn) || fullscreen }
    DisposableEffect(Unit) { onDispose { VhsCrtGate.off = false } }
    // full screen: no bars, and landscape for wide tapes
    val view = androidx.compose.ui.platform.LocalView.current
    LaunchedEffect(fullscreen) {
        var c: android.content.Context? = ctx
        while (c is android.content.ContextWrapper && c !is android.app.Activity) c = c.baseContext
        val act = c as? android.app.Activity ?: return@LaunchedEffect
        val ctl = androidx.core.view.WindowCompat.getInsetsController(act.window, view)
        if (fullscreen) {
            ctl.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            // turn the phone either way: the picture follows and fits portrait and landscape
            act.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        } else {
            ctl.show(androidx.core.view.WindowInsetsCompat.Type.navigationBars())
            act.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    // pause when you switch to another app; keep playing when the phone just goes to sleep (screen off)
    LaunchedEffect(Unit) {
        val pm = ctx.getSystemService(android.content.Context.POWER_SERVICE) as android.os.PowerManager
        androidx.compose.runtime.snapshotFlow { com.reverender.r95.core.AppVisibility.visible }.collect {
            if (!it) { delay(300); if (pm.isInteractive && !com.reverender.r95.core.AppVisibility.visible) exo.pause() }
        }
    }
    // counter / timeline refresh
    LaunchedEffect(Unit) { while (true) { com.reverender.r95.core.AppVisibility.awaitForeground(); tick = exo.currentPosition; delay(250) } }

    /** (re)load the current tape, with the subtitles if any; keepPos keeps the place and the play/pause state */
    fun apply(keepPos: Boolean) {
        val item = curItem ?: return
        val pos = if (keepPos) exo.currentPosition else 0L
        val wasPlaying = !keepPos || exo.playWhenReady
        val b = item.buildUpon()
        if (curHls) b.setMimeType(MimeTypes.APPLICATION_M3U8)
        subsText?.let { txt ->
            val f = java.io.File(ctx.cacheDir, "vhs_subs_${System.nanoTime()}.vtt")
            f.writeText(com.reverender.r95.ui.apps.srtToVtt(com.reverender.r95.ui.apps.shiftSubtitles(txt, subsOffset)))
            b.setSubtitleConfigurations(listOf(
                MediaItem.SubtitleConfiguration.Builder(Uri.fromFile(f)).setMimeType(MimeTypes.TEXT_VTT).setLanguage(subLang).setSelectionFlags(C.SELECTION_FLAG_DEFAULT).build(),
            ))
        }
        val mi = b.build()
        val audio = curAudio
        if (audio == null) exo.setMediaItem(mi, pos)
        else exo.setMediaSource(androidx.media3.exoplayer.source.MergingMediaSource(msf.createMediaSource(mi), msf.createMediaSource(MediaItem.fromUri(audio))), pos)
        exo.playbackParameters = PlaybackParameters(when (speedSel) { "EP" -> 0.85f; "SLP" -> 0.7f; else -> 1f })
        exo.volume = vol
        exo.prepare(); exo.playWhenReady = wasPlaying
        if (subsText == null) cue = ""
    }
    fun startTape(t: String, item: MediaItem?, audio: Uri? = null, hls: Boolean = false) {
        state.player.duck()
        title = t; loaded = true; searching = false; status = "LOADING"
        if (item == null) return
        curItem = item; curAudio = audio; curHls = hls
        d.castUrl = item.localConfiguration?.uri?.toString()?.takeIf { audio == null && it.startsWith("http") }
        subsText = null; subsName = "OFF"; subsOffset = 0; subHits.clear(); subMsg = null; cue = ""
        subQuery = vhsSafe(t.substringBefore(" — ")).replace(Regex("[_.]+"), " ").trim().take(30)
        apply(false)
    }
    fun findSubs() {
        val q = subQuery.trim()
        if (q.length < 2) { subMsg = "TYPE AT LEAST 2 LETTERS"; return }
        Haptics.tick(); subHits.clear(); subMsg = "SEARCHING SUBTITLES…"
        scope.launch {
            try {
                val r = com.reverender.r95.net.SubtitleClient.search(q, subLang)
                subHits.clear(); subHits.addAll(r.take(40)); subMsg = if (r.isEmpty()) "NO SUBTITLES FOUND" else null
            } catch (x: kotlinx.coroutines.CancellationException) { throw x }
            catch (x: Exception) { subMsg = "SUBTITLE SEARCH FAILED" }
        }
    }
    fun loadSub(item: com.reverender.r95.net.SubtitleItem) {
        Haptics.clunk("play"); subMsg = "LOADING SUBTITLES…"
        scope.launch {
            try {
                subsText = com.reverender.r95.net.SubtitleClient.get(item.id).text
                subsOffset = 0; subsName = vhsSafe(item.title).take(14); subMsg = null
                apply(true); subsOpen = false
            } catch (x: kotlinx.coroutines.CancellationException) { throw x }
            catch (x: Exception) { subMsg = "SUBTITLE DOWNLOAD FAILED" }
        }
    }
    val pickSubs = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) runCatching {
            subsText = ctx.contentResolver.openInputStream(uri)!!.bufferedReader().use { it.readText() }
            subsOffset = 0; subsName = vhsSafe(uri.lastPathSegment?.substringAfterLast('/') ?: "FILE").take(14)
            apply(true); subsOpen = false
        }
    }
    androidx.activity.compose.BackHandler(enabled = subsOpen) { subsOpen = false }
    fun playYoutube(id: String, t: String) {
        // Play edition: YouTube's own player in a VHS frame (no stream extraction)
        if (com.reverender.r95.BuildConfig.PLAY) { exo.pause(); com.reverender.r95.ui.vhs.VhsYt.open(id, t); return }
        startTape(t, null)
        scope.launch {
            try {
                val p = com.reverender.r95.net.YtStreams.resolve(id)
                startTape(t.ifEmpty { p.title }, MediaItem.fromUri(Uri.parse(p.video)), p.audio?.let(Uri::parse), p.hls)
            } catch (x: kotlinx.coroutines.CancellationException) { throw x }
            catch (x: Exception) { status = "TAPE ERROR"; msg = "YOUTUBE TAPE COULD NOT BE LOADED"; searching = true }
        }
    }
    fun search() {
        val q = query.trim()
        if (q.length < 2) { msg = "TYPE AT LEAST 2 LETTERS"; return }
        job?.cancel(); hits.clear(); Haptics.tick()
        msg = if (source == "YT") "SEARCHING YOUTUBE…" else "SEARCHING THE ARCHIVE…"
        job = scope.launch {
            try {
                if (source == "YT") {
                    val r = com.reverender.r95.net.YouTubeClient.search(q)
                    hits.clear(); hits.addAll(r.map { a -> TapeHit(a.title, "YOUTUBE") { playYoutube(a.videoId, a.title) } })
                    msg = if (r.isEmpty()) "NO TAPES FOUND" else null
                } else {
                    val (all, _) = com.reverender.r95.net.ArchiveVideoSearch.search(q) { list ->
                        scope.launch { hits.clear(); hits.addAll(list.take(30).map { v -> TapeHit(v.title, "ARCHIVE ." + v.ext.uppercase()) { startTape(v.title, MediaItem.fromUri(v.url)) } }); msg = null }
                    }
                    hits.clear(); hits.addAll(all.take(30).map { v -> TapeHit(v.title, "ARCHIVE ." + v.ext.uppercase()) { startTape(v.title, MediaItem.fromUri(v.url)) } })
                    msg = if (all.isEmpty()) "NO TAPES FOUND" else null
                }
            } catch (x: kotlinx.coroutines.CancellationException) { throw x }
            catch (x: Exception) { msg = "SEARCH FAILED. CHECK THE CONNECTION." }
        }
    }
    val pick = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val name = runCatching { ctx.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c -> if (c.moveToFirst()) c.getString(0) else null } }.getOrNull() ?: "VIDEO"
        startTape(name.substringBeforeLast('.'), MediaItem.fromUri(uri))
    }
    fun eject() { Haptics.clunk("eject"); exo.stop(); exo.clearMediaItems(); curItem = null; d.castUrl = null; fullscreen = false; cue = ""; subsText = null; subsName = "OFF"; loaded = false; d.playing = false; status = "INSERT TAPE"; searching = true; subsOpen = false }

    Column(Modifier.fillMaxSize().padding(horizontal = pxDp(2f), vertical = pxDp(2f)), verticalArrangement = Arrangement.spacedBy(pxDp(1.4f))) {
        if (subsOpen && loaded && !searching) {
            // ---------------- subtitles (like VIDEO_STREAM_95's, in OSD style)
            VhsHeader("SUBTITLES", "NOW: " + subsName)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(pxDp(3f)), verticalAlignment = Alignment.CenterVertically) {
                com.reverender.r95.ui.apps.SUB_LANGS.forEach { (code, _) -> VhsKey(code.uppercase(), selected = subLang == code, boxed = true) { subLang = code } }
            }
            RegisterKb(VhsKb({ subQuery }, { subQuery = it.take(30) }, label = "FIND SUBTITLES") { findSubs() })
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(3f)), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f).pointerInput(Unit) { detectTapGestures(onLongPress = { subQuery = "" }) { openKb() } }) {
                    VhsText("FIND:" + subQuery.takeLast(10) + if (blink) "_" else " ")
                }
                VhsKey("FILE", boxed = true) { pickSubs.launch(arrayOf("*/*")) }
            }
            // sync / size / off
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(2.5f)), verticalAlignment = Alignment.CenterVertically) {
                VhsText("SYNC")
                VhsKey("-", boxed = true) { if (subsText != null) { subsOffset -= 500; apply(true) } }
                VhsText("%+.1fS".format(java.util.Locale.US, subsOffset / 1000f))
                VhsKey("+", boxed = true) { if (subsText != null) { subsOffset += 500; apply(true) } }
                Spacer(Modifier.weight(1f))
                VhsKey("OFF", selected = subsText == null, boxed = true) { subsText = null; subsName = "OFF"; cue = ""; apply(true) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(pxDp(2.5f)), verticalAlignment = Alignment.CenterVertically) {
                VhsText("SIZE")
                listOf("S" to 0.75f, "M" to 1f, "L" to 1.3f, "XL" to 1.6f).forEach { (k, v) -> VhsKey(k, selected = cueSize == v, boxed = true) { cueSize = v } }
            }
            Column(Modifier.fillMaxWidth().weight(1f).then(if (subHits.isNotEmpty()) Modifier.verticalScroll(rememberScrollState()) else Modifier), verticalArrangement = Arrangement.spacedBy(pxDp(1.6f))) {
                subMsg?.let { VhsText(it.take(22), color = o.fg.copy(alpha = 0.7f)) }
                subHits.forEachIndexed { i, h ->
                    Column(Modifier.fillMaxWidth().pointerInput(h.id) { detectTapGestures { loadSub(h) } }) {
                        VhsText("%02d ".format(i + 1) + vhsSafe(h.title).take(19))
                        VhsText("   " + vhsSafe(h.source + " " + h.lang).take(19), color = o.fg.copy(alpha = 0.6f))
                    }
                }
            }
            Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { Haptics.tick(); subsOpen = false } }) {
                Column {
                    VhsText("TAP A RESULT TO LOAD", invert = true)
                    VhsText("TAP HERE: BACK TO TAPE", invert = true)
                }
            }
        } else if (searching || !loaded) {
            // ---------------- the tape library (search)
            VhsHeader("VIDEO DECK", if (loaded) "NEW TAPE?" else "LOAD A TAPE")
            VhsDeckSwitch("video", onSwitch)
            Row(horizontalArrangement = Arrangement.spacedBy(pxDp(3f)), verticalAlignment = Alignment.CenterVertically) {
                VhsKey("YOUTUBE", selected = source == "YT", boxed = true) { source = "YT" }
                VhsKey("ARCHIVE", selected = source == "IA", boxed = true) { source = "IA" }
                VhsKey("FILE", boxed = true) { pick.launch(arrayOf("video/*")) }
            }
            RegisterKb(VhsKb({ query }, { query = it.take(24) }, label = "FIND A VIDEO") { search() })
            // the phone keyboard types the search: swipe up or tap the FIND line (hold it to clear)
            Box(Modifier.fillMaxWidth().pointerInput(Unit) { detectTapGestures(onLongPress = { query = "" }) { openKb() } }) {
                VhsText("FIND:" + query.takeLast(16) + if (blink) "_" else " ")
            }
            // results
            Column(Modifier.fillMaxWidth().weight(1f).then(if (hits.isNotEmpty()) Modifier.verticalScroll(rememberScrollState()) else Modifier), verticalArrangement = Arrangement.spacedBy(pxDp(1.6f))) {
                msg?.let { VhsText(it.take(22), color = o.fg.copy(alpha = 0.7f)) }
                hits.forEachIndexed { i, h ->
                    Column(Modifier.fillMaxWidth().pointerInput(h) { detectTapGestures { Haptics.clunk("play"); h.play() } }) {
                        VhsText("%02d ".format(i + 1) + h.title.uppercase().take(19))
                        VhsText("   " + h.sub.take(19), color = o.fg.copy(alpha = 0.6f))
                    }
                }
            }
            Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(loaded) { detectTapGestures { Haptics.tick(); if (loaded) searching = false else onExit() } }) {
                Column {
                    VhsText("TAP A TAPE TO PLAY IT", invert = true)
                    VhsText(if (loaded) "TAP HERE: BACK TO TAPE" else "TAP HERE TO RETURN", invert = true)
                }
            }
        } else if (fullscreen) {
            // full screen is drawn by the shell over everything (VhsFullscreenVideo); nothing here
            Spacer(Modifier.weight(1f))
        } else {
            // ---------------- playback
            Box(Modifier.fillMaxWidth().weight(1f).background(Color.Black), contentAlignment = Alignment.Center) {
                Box(Modifier.aspectRatio(aspect.coerceIn(0.4f, 3f))) {
                    VhsPicture(exo, Modifier.fillMaxSize(), fxOn)
                    if (fxOn && OsPrefs.vidNoise > 0.01f) VhsSnow(OsPrefs.vidNoise, Modifier.fillMaxSize())
                }
                // subtitles in the OSD font, boxed like teletext
                if (cue.isNotBlank()) androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = pxDp(3f))) {
                    val perLine = with(LocalDensity.current) { (maxWidth.toPx() / (6 * o.px * cueSize)).toInt() - 1 }.coerceAtLeast(8)
                    val lines = cueLines(vhsSafe(cue.replace(Regex("<[^>]+>"), "")), perLine).takeLast(4)
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        lines.forEach { l -> VhsText(l, scale = cueSize, modifier = Modifier.background(Color.Black.copy(alpha = 0.6f))) }
                    }
                }
                // OSD on the picture, top-left, like a VCR
                Column(Modifier.align(Alignment.TopStart).padding(pxDp(1.5f))) {
                    VhsText(when { status == "PLAY" -> "▶PLAY"; status == "PAUSE" -> "❚❚PAUSE"; else -> status }.take(14))
                }
                Column(Modifier.align(Alignment.TopEnd).padding(pxDp(1.5f))) { VhsText(speedSel) }
            }
            VhsText(title.uppercase().take(22))
            // transport
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(2.5f)), verticalAlignment = Alignment.CenterVertically) {
                VhsKey("◀◀", boxed = true, scale = 1.2f) { Haptics.clunk("play"); exo.seekTo((exo.currentPosition - 10_000).coerceAtLeast(0)) }
                VhsKey(if (playing) "❚❚" else "▶", selected = playing, scale = 1.2f) { Haptics.clunk(if (playing) "pause" else "play"); if (playing) exo.pause() else { if (exo.playbackState == Player.STATE_ENDED) exo.seekTo(0); exo.play() } }
                VhsKey("■", boxed = true, scale = 1.2f) { Haptics.clunk("stop"); exo.pause(); exo.seekTo(0); status = "STOP" }
                VhsKey("▶▶", boxed = true, scale = 1.2f) { Haptics.clunk("play"); exo.seekTo(exo.currentPosition + 10_000) }
                VhsKey("⏏", boxed = true, scale = 1.2f) { eject() }
            }
            // cast to a TV, full screen, and all effects on / off
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(2.5f)), verticalAlignment = Alignment.CenterVertically) {
                val castBtn = remember { arrayOfNulls<android.view.View>(1) }
                // the system's Cast picker, driven by our own key (the real button stays invisible)
                AndroidView(
                    factory = { c ->
                        val themed = android.view.ContextThemeWrapper(c, androidx.appcompat.R.style.Theme_AppCompat_Light)
                        androidx.mediarouter.app.MediaRouteButton(themed).also { btn ->
                            if (com.reverender.r95.audio.CastBridge.init(c)) runCatching { com.google.android.gms.cast.framework.CastButtonFactory.setUpMediaRouteButton(c.applicationContext, btn) }
                            castBtn[0] = btn
                        }
                    },
                    modifier = Modifier.size(1.dp).graphicsLayer { alpha = 0f },
                )
                VhsKey(if (com.reverender.r95.audio.CastBridge.connected) "CASTING" else "CAST", selected = com.reverender.r95.audio.CastBridge.connected, boxed = true) {
                    when {
                        d.castUrl == null && !com.reverender.r95.audio.CastBridge.connected -> status = "CAN'T CAST THIS"
                        castBtn[0] == null || !com.reverender.r95.audio.CastBridge.init(ctx) -> status = "NO CAST HERE"
                        else -> castBtn[0]?.performClick()
                    }
                }
                VhsKey("FULL", boxed = true) { fullscreen = true }
                Spacer(Modifier.weight(1f))
                VhsKey(if (fxOn) "FX ON" else "FX OFF", selected = fxOn, boxed = true) { OsPrefs.setVidFxOn(!fxOn) }
            }
            // counter + timeline
            val pos = tick; val dur = exo.duration.takeIf { it > 0 } ?: 0L
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(pxDp(2.5f))) {
                    listOf("SP" to 1f, "EP" to 0.85f, "SLP" to 0.7f).forEach { (k, v) -> VhsKey(k, selected = speedSel == k) { speedSel = k; exo.playbackParameters = PlaybackParameters(v) } }
                }
                Spacer(Modifier.weight(1f))
                VhsText("%d:%02d:%02d".format(pos / 3_600_000, pos / 60_000 % 60, pos / 1000 % 60))
            }
            VhsTimeline(if (dur > 0) pos.toFloat() / dur else 0f) { v -> if (dur > 0) { exo.seekTo((v * dur).toLong()); tick = exo.currentPosition } }
            Row(Modifier.fillMaxWidth()) { VhsText("BEGIN"); Spacer(Modifier.weight(1f)); VhsText("END") }
            Row(verticalAlignment = Alignment.CenterVertically) {
                VhsText("VOL ")
                Box(Modifier.weight(1f)) { VhsBar(vol) { vol = it; exo.volume = it } }
            }
            if (fxOpen) {
                // picture effects
                @Composable fun fx(label: String, key: String, v: Float) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        VhsText(label.padEnd(6))
                        Box(Modifier.weight(1f)) { VhsBar(v, slider = true, centreMark = key == "color") { OsPrefs.setVid(key, if (key == "color" && kotlin.math.abs(it - 0.5f) < 0.03f) 0.5f else it) } }
                        Spacer(Modifier.width(pxDp(2f)))
                        VhsText((v * 100).roundToInt().toString().padStart(3))
                    }
                }
                fx("COLOR", "color", OsPrefs.vidColor)
                fx("SOFT", "blur", OsPrefs.vidBlur)
                fx("FRINGE", "chroma", OsPrefs.vidChroma)
                fx("SNOW", "noise", OsPrefs.vidNoise)
                Row(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    VhsKey("RESET FX", boxed = true) {
                        Haptics.clunk("stop")
                        OsPrefs.setVid("color", 0.45f); OsPrefs.setVid("blur", 0.25f); OsPrefs.setVid("chroma", 0.4f); OsPrefs.setVid("noise", 0.3f)
                    }
                }
            }
            Box(Modifier.fillMaxWidth().background(o.fg).padding(pxDp(1f))) {
                Row(Modifier.fillMaxWidth()) {
                    Box(Modifier.weight(1f).pointerInput(Unit) { detectTapGestures { Haptics.tick(); fxOpen = !fxOpen } }) {
                        VhsText(if (fxOpen) "FX▼" else "FX▲", invert = true)
                    }
                    Box(Modifier.weight(1f).pointerInput(Unit) { detectTapGestures { Haptics.tick(); subsOpen = true } }) { VhsText("SUBS", invert = true) }
                    Box(Modifier.weight(1f).pointerInput(Unit) { detectTapGestures { Haptics.tick(); searching = true } }) { VhsText("FIND", invert = true) }
                    Box(Modifier.pointerInput(Unit) { detectTapGestures { Haptics.tick(); onExit() } }) { VhsText("MENU", invert = true) }
                }
            }
        }
    }
}

/**
 * The picture: ExoPlayer on a TextureView (so Compose can treat it as a layer), with the effects done by
 * the GPU on Android 12+: colour (saturation), softness (blur) and a real red/cyan colour fringe
 * (the red channel shifted one way, green+blue the other, added back together).
 */
@Composable
private fun VhsPicture(exo: ExoPlayer, modifier: Modifier, fxOn: Boolean = true) {
    val dens = LocalDensity.current.density
    val sat = if (fxOn) OsPrefs.vidColor * 2f else 1f
    val blur = if (fxOn) OsPrefs.vidBlur else 0f
    val ca = if (fxOn) OsPrefs.vidChroma else 0f
    val effect = remember(sat, blur, ca) {
        if (Build.VERSION.SDK_INT < 31) null else runCatching { vhsEffect(sat, blur * 3.2f * dens, ca * 4.5f * dens) }.getOrNull()
    }
    AndroidView(
        factory = { c -> android.view.TextureView(c).also { exo.setVideoTextureView(it) } },
        onRelease = { exo.clearVideoTextureView(it) },
        modifier = modifier.graphicsLayer { renderEffect = effect },
    )
}

@androidx.annotation.RequiresApi(31)
private fun vhsEffect(sat: Float, blurPx: Float, shiftPx: Float): androidx.compose.ui.graphics.RenderEffect? {
    var e: android.graphics.RenderEffect? = null
    if (kotlin.math.abs(sat - 1f) > 0.02f) {
        val cm = android.graphics.ColorMatrix().apply { setSaturation(sat) }
        e = android.graphics.RenderEffect.createColorFilterEffect(android.graphics.ColorMatrixColorFilter(cm))
    }
    if (shiftPx > 0.3f) {
        val red = android.graphics.ColorMatrixColorFilter(floatArrayOf(1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f))
        val cyan = android.graphics.ColorMatrixColorFilter(floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f))
        val l = if (e == null) android.graphics.RenderEffect.createOffsetEffect(-shiftPx, 0f) else android.graphics.RenderEffect.createOffsetEffect(-shiftPx, 0f, e)
        val r = if (e == null) android.graphics.RenderEffect.createOffsetEffect(shiftPx, 0f) else android.graphics.RenderEffect.createOffsetEffect(shiftPx, 0f, e)
        e = android.graphics.RenderEffect.createBlendModeEffect(
            android.graphics.RenderEffect.createColorFilterEffect(red, l),
            android.graphics.RenderEffect.createColorFilterEffect(cyan, r),
            android.graphics.BlendMode.PLUS,
        )
    }
    if (blurPx > 0.3f) {
        e = if (e == null) android.graphics.RenderEffect.createBlurEffect(blurPx, blurPx * 0.35f, android.graphics.Shader.TileMode.CLAMP)
        else android.graphics.RenderEffect.createBlurEffect(blurPx, blurPx * 0.35f, e, android.graphics.Shader.TileMode.CLAMP)
    }
    return e?.asComposeRenderEffect()
}

/** Tape snow over the picture: specks and grainy horizontal streaks, ~15 fps. */
@Composable
private fun VhsSnow(amount: Float, modifier: Modifier) {
    var t by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) { while (true) { com.reverender.r95.core.AppVisibility.awaitForeground(); t = System.currentTimeMillis(); delay(66) } }
    Canvas(modifier) {
        val rnd = java.util.Random(t / 66)
        val a = amount
        repeat((40 + 260 * a * a).toInt()) {
            drawRect(Color.White.copy(alpha = 0.08f + rnd.nextFloat() * 0.35f * a), Offset(rnd.nextFloat() * size.width, rnd.nextFloat() * size.height), Size(1f + rnd.nextFloat() * 3f, 1f + rnd.nextFloat() * 1.5f))
        }
        repeat((2 + 10 * a).toInt()) {
            val y = rnd.nextFloat() * size.height
            drawRect(Color.White.copy(alpha = 0.03f + 0.08f * a * rnd.nextFloat()), Offset(0f, y), Size(size.width, 1f + rnd.nextFloat() * 2f))
        }
    }
}

/** Word-wraps a subtitle cue to the OSD width. */
private fun cueLines(text: String, perLine: Int): List<String> {
    val out = ArrayList<String>()
    for (para in text.split('\n')) {
        var line = ""
        for (w in para.split(' ').filter { it.isNotBlank() }) {
            val word = if (w.length > perLine) w.take(perLine) else w
            line = if (line.isEmpty()) word else if (line.length + 1 + word.length <= perLine) "$line $word" else { out += line; word }
        }
        if (line.isNotEmpty()) out += line
    }
    return out
}

/**
 * Full-screen video, drawn by the shell above everything (no launcher edges, no CRT, no picture effects).
 * The picture keeps its shape and fits the screen whichever way the phone is turned. Tap for the controls.
 */
@Composable
internal fun VhsFullscreenVideo(d: VideoDeck) {
    val ctx = LocalContext.current
    val o = LocalOsd.current
    val exo = d.exo(ctx)
    var ctrls by remember { mutableStateOf(false) }
    LaunchedEffect(ctrls) { if (ctrls) { delay(3500); ctrls = false } }
    Box(Modifier.fillMaxSize().background(Color.Black).pointerInput(Unit) { detectTapGestures { ctrls = !ctrls } }, contentAlignment = Alignment.Center) {
        Box(Modifier.aspectRatio(d.aspect.coerceIn(0.4f, 3f))) { VhsPicture(exo, Modifier.fillMaxSize(), false) }
        if (d.cue.isNotBlank()) androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = pxDp(6f))) {
            val perLine = with(LocalDensity.current) { (maxWidth.toPx() / (6 * o.px * d.cueSize)).toInt() - 1 }.coerceAtLeast(8)
            val lines = cueLines(vhsSafe(d.cue.replace(Regex("<[^>]+>"), "")), perLine).takeLast(4)
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                lines.forEach { l -> VhsText(l, scale = d.cueSize, modifier = Modifier.background(Color.Black.copy(alpha = 0.6f))) }
            }
        }
        if (ctrls) Row(Modifier.align(Alignment.TopStart).fillMaxWidth().padding(pxDp(3f)), horizontalArrangement = Arrangement.spacedBy(pxDp(3f)), verticalAlignment = Alignment.CenterVertically) {
            VhsKey(if (d.playing) "❚❚" else "▶", selected = d.playing, scale = 1.2f) { if (d.playing) exo.pause() else exo.play(); ctrls = true }
            VhsKey("◀◀", boxed = true, scale = 1.2f) { exo.seekTo((exo.currentPosition - 10_000).coerceAtLeast(0)); ctrls = true }
            VhsKey("▶▶", boxed = true, scale = 1.2f) { exo.seekTo(exo.currentPosition + 10_000); ctrls = true }
            Spacer(Modifier.weight(1f))
            VhsKey("EXIT", boxed = true, scale = 1.2f) { d.fullscreen = false }
        }
    }
}
