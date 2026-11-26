package com.reverender.r95.audio

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.reverender.r95.MainActivity
import com.reverender.r95.core.Site

/**
 * Background audio: keeps music playing with the screen off, shows the media
 * notification / lock-screen controls, and owns the one ExoPlayer instance.
 */
@OptIn(UnstableApi::class)
class PlaybackService : MediaSessionService() {
    private var session: MediaSession? = null

    companion object {
        /** the one player, for widgets and other in-process callers while the service runs */
        @Volatile var player: androidx.media3.common.Player? = null; private set
    }

    override fun onCreate() {
        super.onCreate()
        com.reverender.r95.os.OsBoot.init(this)

        val renderers = object : DefaultRenderersFactory(this) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean,
            ): AudioSink = DefaultAudioSink.Builder(context)
                .setAudioProcessors(arrayOf(VaporAudioProcessor()))
                .build()
        }

        // The site's .htaccess blocks generic HTTP-library user agents, so identify ourselves.
        val http = DefaultHttpDataSource.Factory()
            .setUserAgent(Site.USER_AGENT)
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(25_000)

        val player = ExoPlayer.Builder(this, renderers)
            .setMediaSourceFactory(DefaultMediaSourceFactory(DefaultDataSource.Factory(this, http)))
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                /* handleAudioFocus = */ true,
            )
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build()

        val openApp = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        session = MediaSession.Builder(this, player)
            .setSessionActivity(openApp)
            .build()
        PlaybackService.player = player
        val ctx = applicationContext
        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                com.reverender.r95.os.GyroWarp.playing = isPlaying
                com.reverender.r95.os.GyroWarp.refresh()
                com.reverender.r95.os.WidgetHub.onPlayerChanged(ctx)
            }
            override fun onMediaMetadataChanged(mediaMetadata: androidx.media3.common.MediaMetadata) = com.reverender.r95.os.WidgetHub.onPlayerChanged(ctx)
            override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) = com.reverender.r95.os.WidgetHub.onPlayerChanged(ctx)
        })
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    /** App swiped away from recents: stop the music and remove the notification. */
    override fun onTaskRemoved(rootIntent: Intent?) {
        session?.player?.let { it.stop(); it.clearMediaItems() }
        com.reverender.r95.audio.MicCapture.stop()
        runCatching { com.reverender.r95.audio.StudioAudio.stopAll() }
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        PlaybackService.player = null
        com.reverender.r95.os.GyroWarp.playing = false
        com.reverender.r95.os.GyroWarp.refresh()
        session?.run {
            player.release()
            release()
        }
        session = null
        super.onDestroy()
    }
}
