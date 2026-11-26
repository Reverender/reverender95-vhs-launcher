package com.reverender.r95.audio

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.reverender.r95.core.Catalog
import com.reverender.r95.core.Station
import com.reverender.r95.core.Track
import kotlin.random.Random

enum class SourceMode(val label: String) { DECK("BUILT-IN DECK"), RADIO("INTERNET RADIO"), FILES("DEVICE FILES"), ARCHIVE("INTERNET ARCHIVE"), STREAM("CUSTOM STREAM") }

/**
 * UI-side handle on the playback service. Exposes Compose state for VAPOR_AMP,
 * CASSETTE_95 and the taskbar, and wraps every transport command.
 */
class PlayerConnection(private val context: Context) {
    private var future: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    var connected by mutableStateOf(false); private set
    var isPlaying by mutableStateOf(false); private set
    var isBuffering by mutableStateOf(false); private set
    var mode by mutableStateOf(SourceMode.DECK); private set
    var deckIndex by mutableStateOf(0); private set
    var station by mutableStateOf<Station?>(null); private set
    var nowPlaying by mutableStateOf(""); private set
    var error by mutableStateOf<String?>(null); private set
    var volume by mutableFloatStateOf(0.85f); private set
    var speed by mutableFloatStateOf(1f); private set
    var lastJump by mutableStateOf("--:--"); private set
    var bufferedPercent by mutableStateOf(0); private set

    /** Device files and Internet Archive tracks share one local playlist, like the website. */
    val localPlaylist = mutableStateListOf<Track>()
    var localIndex by mutableStateOf(-1); private set

    /** "Play next" queue. */
    val upNext = mutableStateListOf<Track>()

    private var randomSeekPending = false

    /** The site's audioEl events, handled by VAPOR_AMP's engine (AmpEngine). */
    interface Events {
        fun onLoadedMetadata() {}
        fun onPlaying() {}
        fun onWaiting() {}
        fun onEnded() {}
        fun onError() {}
    }
    var events: Events? = null
    /** A failure in the engine's handlers must never take the app down. */
    private inline fun safe(block: () -> Unit) { try { block() } catch (e: Exception) { android.util.Log.e("R95", "player event", e) } }

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing; AudioBus.fx.hissActive = playing; if (playing) safe { events?.onPlaying() } }
        override fun onPlaybackStateChanged(state: Int) {
            isBuffering = state == Player.STATE_BUFFERING
            if (state == Player.STATE_READY) { safe { events?.onLoadedMetadata() }; if (randomSeekPending) { randomSeekPending = false; jumpRandomPosition() } }
            if (state == Player.STATE_BUFFERING && controller?.playWhenReady == true) safe { events?.onWaiting() }
            if (state == Player.STATE_ENDED) { if (upNext.isNotEmpty()) playNextFromQueue() else safe { events?.onEnded() } }
        }
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO && upNext.isNotEmpty()) { playNextFromQueue(); return }
            syncFromController()
        }
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) { syncFromController() }
        override fun onPlayerError(e: PlaybackException) {
            safe { events?.onError() }
            error = when (mode) {
                SourceMode.RADIO, SourceMode.STREAM -> "Stream offline or blocked (${e.errorCodeName})"
                else -> "Track failed to load (${e.errorCodeName})"
            }
        }
    }

    fun connect() {
        if (future != null) return
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val f = MediaController.Builder(context, token).buildAsync()
        future = f
        f.addListener({
            val c = runCatching { f.get() }.getOrNull() ?: return@addListener
            controller = c
            c.addListener(listener)
            connected = true
            isPlaying = c.isPlaying
            c.volume = volume
            syncFromController()
        }, ContextCompat.getMainExecutor(context))
    }

    fun release() {
        controller?.removeListener(listener)
        future?.let { MediaController.releaseFuture(it) }
        future = null; controller = null; connected = false
    }

    private fun syncFromController() {
        val c = controller ?: return
        val id = c.currentMediaItem?.mediaId.orEmpty()
        val md = c.mediaMetadata
        when {
            id.startsWith("deck:") -> {
                mode = SourceMode.DECK
                deckIndex = id.removePrefix("deck:").toIntOrNull() ?: c.currentMediaItemIndex
                nowPlaying = Catalog.deck.getOrNull(deckIndex)?.title ?: ""
            }
            id.startsWith("local:") -> {
                localIndex = id.removePrefix("local:").toIntOrNull() ?: c.currentMediaItemIndex
                nowPlaying = localPlaylist.getOrNull(localIndex)?.title ?: md.title?.toString().orEmpty()
            }
            id.startsWith("radio:") || id.startsWith("stream:") -> {
                // Stream (ICY) titles arrive as the title; the station name is kept as artist.
                val name = md.artist?.toString() ?: station?.name.orEmpty()
                val song = md.title?.toString().orEmpty()
                nowPlaying = if (song.isNotBlank()) "$name — $song" else name
            }
            id.startsWith("q:") -> nowPlaying = md.title?.toString().orEmpty()
        }
    }

    private fun item(id: String, t: Track, album: String) = MediaItem.Builder()
        .setMediaId(id)
        .setUri(Uri.parse(t.url))
        .setMediaMetadata(MediaMetadata.Builder().setTitle(t.title).setArtist("REVERENDER_95").setAlbumTitle(album).build())
        .build()

    /** [randomOffset]: start somewhere random in the track, like the website's deck. */
    fun playDeck(index: Int, randomOffset: Boolean = false) {
        val c = controller ?: return
        error = null; station = null; mode = SourceMode.DECK
        val i = index.mod(Catalog.deck.size)
        c.setMediaItem(item("deck:$i", Catalog.deck[i], "VAPOR_AMP DECK"))
        c.repeatMode = Player.REPEAT_MODE_OFF
        c.playbackParameters = PlaybackParameters(speed, speed)
        c.prepare(); c.play()
        deckIndex = i
        nowPlaying = Catalog.deck[deckIndex].title
        randomSeekPending = randomOffset
    }

    private fun playStreamItem(id: String, s: Station, album: String, newMode: SourceMode) {
        val c = controller ?: return
        error = null; station = s; mode = newMode
        val item = MediaItem.Builder()
            .setMediaId(id)
            .setUri(Uri.parse(s.url))
            .setMediaMetadata(MediaMetadata.Builder().setArtist(s.name).setAlbumTitle(album).build())
            .build()
        c.setMediaItem(item)
        c.repeatMode = Player.REPEAT_MODE_OFF
        c.playbackParameters = PlaybackParameters(speed, speed)
        c.prepare(); c.play()
        nowPlaying = s.name
    }

    /** Play any single URL (device file or imported archive MP3). */
    fun playUrl(url: String, title: String, album: String) {
        val c = controller ?: return
        error = null; station = null; mode = SourceMode.FILES
        c.setMediaItem(item("file:$url", Track(title, url), album))
        c.repeatMode = Player.REPEAT_MODE_OFF
        c.playbackParameters = PlaybackParameters(speed, speed)
        c.prepare(); c.play()
        nowPlaying = title
    }

    fun playStation(s: Station) = playStreamItem("radio:${s.url}", s, "VAPOR_AMP RADIO", SourceMode.RADIO)

    fun playCustomStream(url: String) {
        val u = url.trim()
        if (!u.startsWith("http")) { error = "Enter a stream URL starting with http(s)://"; return }
        playStreamItem("stream:$u", Station(u.substringAfter("://").take(40), u, "CUSTOM"), "CUSTOM STREAM", SourceMode.STREAM)
    }

    /** Adds tracks to the local playlist (device files or archive MP3s) and optionally plays the first. */
    fun addLocal(tracks: List<Track>, playFirst: Boolean, sourceMode: SourceMode) {
        if (tracks.isEmpty()) return
        val start = localPlaylist.size
        localPlaylist.addAll(tracks)
        if (playFirst) playLocal(start, sourceMode)
    }

    fun playLocal(index: Int, sourceMode: SourceMode = SourceMode.FILES) {
        val c = controller ?: return
        if (index !in localPlaylist.indices) return
        error = null; station = null; mode = sourceMode
        c.setMediaItems(localPlaylist.mapIndexed { i, t -> item("local:$i", t, "LOCAL PLAYLIST") }, index, 0L)
        c.repeatMode = Player.REPEAT_MODE_ALL
        c.playbackParameters = PlaybackParameters(speed, speed)
        c.prepare(); c.play()
        localIndex = index
        nowPlaying = localPlaylist[index].title
    }

    fun removeLocal(index: Int) {
        if (index !in localPlaylist.indices) return
        val playingIt = (mode == SourceMode.FILES || mode == SourceMode.ARCHIVE) && index == localIndex
        localPlaylist.removeAt(index)
        if (playingIt) { if (localPlaylist.isNotEmpty()) playLocal(index.coerceAtMost(localPlaylist.size - 1), mode) else stop() }
        else if (index < localIndex) localIndex--
    }

    fun queueNext(t: Track) { upNext.add(t) }

    private fun playNextFromQueue() {
        val c = controller ?: return
        val t = upNext.removeFirstOrNull() ?: return
        error = null; mode = SourceMode.ARCHIVE
        c.setMediaItem(item("q:${t.url}", t, "UP NEXT"))
        c.prepare(); c.play()
        nowPlaying = t.title
    }

    fun togglePlay() {
        val c = controller ?: return
        when {
            c.mediaItemCount == 0 -> playDeck(0, randomOffset = true)
            c.isPlaying -> c.pause()
            else -> {
                if (c.playbackState == Player.STATE_IDLE) c.prepare()
                c.play()
            }
        }
    }

    fun play() { val c = controller ?: return; if (c.mediaItemCount == 0) playDeck(0) else { if (c.playbackState == Player.STATE_IDLE) c.prepare(); c.play() } }
    fun pause() { controller?.pause() }
    fun stop() { controller?.run { pause(); seekToDefaultPosition() } }

    fun next() = skip(+1)
    fun previous() = skip(-1)

    private fun skip(dir: Int) {
        val c = controller ?: return
        if (dir > 0 && upNext.isNotEmpty()) { playNextFromQueue(); return }
        if (mode == SourceMode.RADIO) {
            val presets = Catalog.radioPresets
            val i = presets.indexOfFirst { it.url == station?.url }
            playStation(presets[(if (i < 0) 0 else i + dir).mod(presets.size)])
        } else if (c.mediaItemCount == 0) {
            playDeck(0)
        } else if (dir > 0) c.seekToNextMediaItem() else c.seekToPreviousMediaItem()
    }

    /** JUMP POS: seek to a random point in the current track. */
    fun jumpRandomPosition() {
        val c = controller ?: return
        val d = c.duration
        if (d <= 0) return
        val pos = (d * (0.05 + Random.nextDouble() * 0.85)).toLong()
        c.seekTo(pos)
        val s = pos / 1000
        lastJump = "%02d:%02d".format(s / 60, s % 60)
    }

    /** RND SONG: a different deck track, starting at a random offset. */
    fun randomSong() {
        var i = Random.nextInt(Catalog.deck.size)
        if (mode == SourceMode.DECK && i == deckIndex) i = (i + 1) % Catalog.deck.size
        playDeck(i, randomOffset = true)
    }

    fun changeVolume(v: Float) { volume = v.coerceIn(0f, 1f); controller?.volume = volume }

    /** Like the site: slowing down also lowers pitch (no time-stretch) for the vapor sound. */
    fun changeSpeed(v: Float) {
        speed = v.coerceIn(0.5f, 1.5f)
        rate = speed
        controller?.playbackParameters = PlaybackParameters(speed, speed)
    }

    /** The site's `audioEl.playbackRate` (cassette transport, wow & flutter): rate and pitch together. */
    var rate = 1f; private set
    fun setRate(v: Float) {
        val r = v.coerceIn(0.25f, 4f)
        if (kotlin.math.abs(r - rate) < 0.004f) return
        rate = r
        controller?.playbackParameters = PlaybackParameters(r, r)
    }

    /** Cassette REW / FF cue: short scrubbing seeks. */
    fun cue(ms: Long) {
        val c = controller ?: return
        if (c.duration <= 0) return
        c.seekTo((c.currentPosition + ms).coerceIn(0L, c.duration))
    }

    /** The stream / file URL now loaded (for Cast). */
    fun currentUrl(): String? = controller?.currentMediaItem?.localConfiguration?.uri?.toString()

    fun positionMs(): Long = controller?.currentPosition ?: 0L
    fun durationMs(): Long = controller?.duration?.takeIf { it > 0 } ?: 0L
    fun pollBuffer() { bufferedPercent = controller?.bufferedPercentage ?: 0 }
    fun seekTo(ms: Long) { controller?.seekTo(ms) }
    /** Load a deck track without starting it (the site's selectTrack on page load). */
    fun cueDeck(index: Int) { deckIndex = index.mod(Catalog.deck.size); nowPlaying = Catalog.deck[deckIndex].title }
    fun playWhenReady(): Boolean = controller?.playWhenReady == true

    /** VIDEO_95 and RETRO_EMU_95 pause the music while they play, like the website. */
    fun duck() { if (isPlaying) controller?.pause() }
}
