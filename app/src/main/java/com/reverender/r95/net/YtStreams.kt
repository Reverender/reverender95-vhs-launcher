package com.reverender.r95.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.MediaFormat
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import org.schabi.newpipe.extractor.stream.DeliveryMethod
import org.schabi.newpipe.extractor.stream.StreamInfo
import java.net.HttpURLConnection
import java.net.URL

/**
 * VIDEO_STREAM_95's YouTube playback. The site plays YouTube in its official embed (a web view);
 * natively, NewPipeExtractor finds the stream addresses and the app's own player plays them.
 */
object YtStreams {
    /** What to play: one muxed stream, or a video-only stream with a separate audio stream, or HLS (live). */
    class Playable(val title: String, val video: String, val audio: String?, val hls: Boolean)

    private object HttpDownloader : Downloader() {
        private const val UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0"
        override fun execute(request: Request): Response {
            val conn = URL(request.url()).openConnection() as HttpURLConnection
            conn.requestMethod = request.httpMethod()
            conn.connectTimeout = 20_000; conn.readTimeout = 30_000
            conn.instanceFollowRedirects = true
            conn.setRequestProperty("User-Agent", UA)
            request.headers().forEach { (k, vs) -> conn.setRequestProperty(k, vs.joinToString(", ")) }
            request.dataToSend()?.let { data -> conn.doOutput = true; conn.outputStream.use { it.write(data) } }
            val code = conn.responseCode
            if (code == 429) { conn.disconnect(); throw ReCaptchaException("reCaptcha Challenge requested", request.url()) }
            val stream = if (code >= 400) conn.errorStream else conn.inputStream
            val body = stream?.bufferedReader()?.use { it.readText() } ?: ""
            val headers: Map<String, List<String>> = conn.headerFields.entries.filter { it.key != null }.associate { it.key!! to it.value }
            val latest = conn.url.toString()
            conn.disconnect()
            return Response(code, conn.responseMessage ?: "", headers, body, latest)
        }
    }

    @Volatile private var ready = false
    private fun init() { if (!ready) synchronized(this) { if (!ready) { NewPipe.init(HttpDownloader); ready = true } } }

    suspend fun resolve(videoId: String): Playable = withContext(Dispatchers.IO) {
        // the Google Play edition never pulls streams out of YouTube (YouTube's own player is used instead)
        if (com.reverender.r95.BuildConfig.PLAY) throw IllegalStateException("not in this edition")
        init()
        val info = StreamInfo.getInfo(ServiceList.YouTube, "https://www.youtube.com/watch?v=$videoId")
        if (info.hlsUrl.isNullOrEmpty().not() && info.videoStreams.isEmpty() && info.videoOnlyStreams.isEmpty()) return@withContext Playable(info.name, info.hlsUrl, null, true)
        val prog = { d: DeliveryMethod -> d == DeliveryMethod.PROGRESSIVE_HTTP }
        // best audio: m4a first (plays everywhere), highest bitrate
        val audio = info.audioStreams.filter { it.isUrl && prog(it.deliveryMethod) }
            .sortedWith(compareByDescending<org.schabi.newpipe.extractor.stream.AudioStream> { it.format == MediaFormat.M4A }.thenByDescending { it.averageBitrate })
            .firstOrNull()
        // video-only up to 720p (mp4 first) + audio, else the best muxed stream
        val vo = info.videoOnlyStreams.filter { it.isUrl && prog(it.deliveryMethod) && it.height in 1..720 }
            .sortedWith(compareByDescending<org.schabi.newpipe.extractor.stream.VideoStream> { it.height }.thenByDescending { it.format == MediaFormat.MPEG_4 })
            .firstOrNull()
        if (vo != null && audio != null) return@withContext Playable(info.name, vo.content, audio.content, false)
        val muxed = info.videoStreams.filter { it.isUrl && prog(it.deliveryMethod) }.maxByOrNull { it.height }
        if (muxed != null) return@withContext Playable(info.name, muxed.content, null, false)
        if (!info.hlsUrl.isNullOrEmpty()) return@withContext Playable(info.name, info.hlsUrl, null, true)
        if (audio != null) return@withContext Playable(info.name, audio.content, null, false)
        throw IllegalStateException("No playable stream")
    }
}
