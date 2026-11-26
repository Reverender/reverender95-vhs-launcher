package com.reverender.r95.audio

import android.content.Context
import android.net.Uri
import android.view.ContextThemeWrapper
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.cast.CastPlayer
import androidx.media3.cast.SessionAvailabilityListener
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider

/** Tells the Cast SDK to use Google's Default Media Receiver (plays MP3 / MP4 URLs). */
class CastOptionsProvider : OptionsProvider {
    override fun getCastOptions(context: Context): CastOptions =
        CastOptions.Builder().setReceiverApplicationId(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID).build()

    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? = null
}

/**
 * CAST for VAPOR_AMP and VIDEO_STREAM_95. When a Cast session starts, whatever the app wants to
 * cast (current track / video URL + position) is handed to the TV and local playback pauses.
 */
@OptIn(UnstableApi::class)
object CastBridge {
    private var castPlayer: CastPlayer? = null
    var connected by mutableStateOf(false); private set
    var deviceStatus by mutableStateOf(""); private set

    /** What to send when a session starts: url, title, mime, start position. */
    var source: (() -> CastSource?)? = null
    /** Called when the session ends, with the position the TV reached. */
    var onReturn: ((Long) -> Unit)? = null

    class CastSource(val url: String, val title: String, val mime: String, val positionMs: Long, val onCastStarted: () -> Unit)

    /** Null when Google Play services / Cast aren't available on this phone. */
    fun init(context: Context): Boolean {
        if (castPlayer != null) return true
        val ctx = runCatching { CastContext.getSharedInstance(context.applicationContext) }.getOrNull() ?: return false
        val p = CastPlayer(ctx)
        p.setSessionAvailabilityListener(object : SessionAvailabilityListener {
            override fun onCastSessionAvailable() {
                connected = true
                val s = source?.invoke() ?: run { deviceStatus = "CONNECTED — PLAY SOMETHING TO CAST"; return }
                if (!s.url.startsWith("http")) { deviceStatus = "THIS SOURCE CAN'T BE CAST (LOCAL FILE)"; return }
                val item = MediaItem.Builder().setUri(Uri.parse(s.url)).setMimeType(s.mime)
                    .setMediaMetadata(MediaMetadata.Builder().setTitle(s.title).setArtist("REVERENDER_95").build()).build()
                p.setMediaItem(item, s.positionMs)
                p.prepare(); p.play()
                s.onCastStarted()
                deviceStatus = "CASTING: ${s.title}"
            }

            override fun onCastSessionUnavailable() {
                val pos = p.currentPosition
                connected = false
                deviceStatus = ""
                onReturn?.invoke(pos)
            }
        })
        castPlayer = p
        return true
    }

    fun togglePlay() { castPlayer?.let { if (it.isPlaying) it.pause() else it.play() } }

    fun mimeFor(url: String): String = when (url.substringBefore('?').substringAfterLast('.').lowercase()) {
        "mp4", "m4v" -> MimeTypes.VIDEO_MP4; "webm" -> MimeTypes.VIDEO_WEBM; "ogv" -> "video/ogg"; "mov" -> "video/quicktime"
        "aac", "m4a" -> MimeTypes.AUDIO_AAC; "ogg" -> MimeTypes.AUDIO_OGG; "flac" -> MimeTypes.AUDIO_FLAC; else -> MimeTypes.AUDIO_MPEG
    }
}

/** The standard Cast button (hidden by the SDK when no Cast device is on the network). */
@Composable
fun CastButton(modifier: Modifier = Modifier) {
    AndroidView(
        factory = { ctx ->
            val themed = ContextThemeWrapper(ctx, androidx.appcompat.R.style.Theme_AppCompat_Light)
            MediaRouteButton(themed).also { btn ->
                if (CastBridge.init(ctx)) runCatching { CastButtonFactory.setUpMediaRouteButton(ctx.applicationContext, btn) }
            }
        },
        modifier = modifier,
    )
}
