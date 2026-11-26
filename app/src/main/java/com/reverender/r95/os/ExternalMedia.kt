package com.reverender.r95.os

import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.SystemClock
import android.view.KeyEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * VAPOR_AMP 95 as a remote for other players (Spotify, YouTube Music, podcasts …).
 *  - Full APK with notification access: the other app's media session — title, artist, app name,
 *    position, play/pause/next/previous and seeking.
 *  - Standard APK: Android's media keys go to whichever app is playing (play/pause/next/previous);
 *    Android doesn't tell other apps what is playing without notification access.
 */
object ExternalMedia {
    var inControl by mutableStateOf(false); private set
    var app by mutableStateOf(""); private set
    var title by mutableStateOf(""); private set
    var artist by mutableStateOf(""); private set
    var playing by mutableStateOf(false); private set
    var durationMs by mutableLongStateOf(0L); private set
    var canSeek by mutableStateOf(false); private set
    var hasDetails by mutableStateOf(false); private set
    private var controller: MediaController? = null
    private var posBase = 0L; private var posAt = 0L; private var speed = 1f
    /** keep the controls after pausing the other app from here, so PLAY resumes it */
    private var holdUntil = 0L
    private var released = false

    val positionMs: Long get() = if (!playing) posBase else posBase + ((SystemClock.elapsedRealtime() - posAt) * speed).toLong()

    /** Call about once a second while VAPOR_AMP is on screen. */
    fun refresh(ctx: Context, ourPlaying: Boolean) {
        if (ourPlaying) { released = false; drop(); return }
        val c = findController(ctx)
        if (c != null) {
            controller = c
            hasDetails = true
            val m = c.metadata
            title = m?.getString(android.media.MediaMetadata.METADATA_KEY_TITLE).orEmpty()
            artist = m?.getString(android.media.MediaMetadata.METADATA_KEY_ARTIST) ?: m?.getString(android.media.MediaMetadata.METADATA_KEY_ALBUM_ARTIST).orEmpty()
            durationMs = m?.getLong(android.media.MediaMetadata.METADATA_KEY_DURATION) ?: 0L
            app = runCatching { ctx.packageManager.getApplicationLabel(ctx.packageManager.getApplicationInfo(c.packageName, 0)).toString() }.getOrDefault(c.packageName)
            val st = c.playbackState
            playing = st?.state == PlaybackState.STATE_PLAYING || st?.state == PlaybackState.STATE_BUFFERING
            posBase = st?.position ?: 0L; posAt = st?.lastPositionUpdateTime?.takeIf { it > 0 } ?: SystemClock.elapsedRealtime(); speed = st?.playbackSpeed ?: 1f
            canSeek = (st?.actions ?: 0L) and PlaybackState.ACTION_SEEK_TO != 0L && durationMs > 0
            if (playing) holdUntil = SystemClock.elapsedRealtime() + 10 * 60_000L
            inControl = !released && (playing || SystemClock.elapsedRealtime() < holdUntil)
            return
        }
        // no notification access: media keys only
        hasDetails = false; controller = null; canSeek = false; durationMs = 0
        val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val active = am.isMusicActive
        if (active) { playing = true; holdUntil = SystemClock.elapsedRealtime() + 10 * 60_000L }
        else if (playing) playing = false
        app = "OTHER APP"; title = ""; artist = ""
        inControl = !released && (active || SystemClock.elapsedRealtime() < holdUntil)
    }

    private fun drop() { inControl = false; controller = null; holdUntil = 0L }

    /** Back to VAPOR_AMP's own deck (until another app starts playing again). */
    fun release() { released = true; drop() }
    fun startedElsewhere() { released = false }

    private fun findController(ctx: Context): MediaController? = runCatching {
        val msm = ctx.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val list = msm.getActiveSessions(ComponentName(ctx, RetroNotificationService::class.java))
            .filter { it.packageName != ctx.packageName }
        list.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING } ?: list.firstOrNull()
    }.getOrNull()

    private fun key(ctx: Context, code: Int) {
        val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val t = SystemClock.uptimeMillis()
        am.dispatchMediaKeyEvent(KeyEvent(t, t, KeyEvent.ACTION_DOWN, code, 0))
        am.dispatchMediaKeyEvent(KeyEvent(t, t, KeyEvent.ACTION_UP, code, 0))
    }

    fun playPause(ctx: Context) {
        holdUntil = SystemClock.elapsedRealtime() + 10 * 60_000L
        val c = controller
        if (c != null) { if (playing) c.transportControls.pause() else c.transportControls.play() }
        else key(ctx, if (playing) KeyEvent.KEYCODE_MEDIA_PAUSE else KeyEvent.KEYCODE_MEDIA_PLAY)
        playing = !playing
        if (playing) posAt = SystemClock.elapsedRealtime() else posBase = positionMs
        Haptics.tick()
    }
    fun next(ctx: Context) { controller?.transportControls?.skipToNext() ?: key(ctx, KeyEvent.KEYCODE_MEDIA_NEXT); Haptics.tick() }
    fun previous(ctx: Context) { controller?.transportControls?.skipToPrevious() ?: key(ctx, KeyEvent.KEYCODE_MEDIA_PREVIOUS); Haptics.tick() }
    fun seekTo(ms: Long) { controller?.transportControls?.seekTo(ms); posBase = ms; posAt = SystemClock.elapsedRealtime() }
}
