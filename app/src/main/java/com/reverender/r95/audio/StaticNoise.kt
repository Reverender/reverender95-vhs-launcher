package com.reverender.r95.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack

/**
 * The site's startProceduralSynth / stopProceduralSynth: looping white noise at 0.055 gain into the
 * master (follows the volume), 0.45 s attack, 0.3 s release, while a stream waits or fails.
 */
object StaticNoise {
    private const val SR = 22050
    private const val LEVEL = 0.055f
    @Volatile private var target = 0f
    @Volatile private var thread: Thread? = null
    @Volatile var volume = 0.8f
    val active: Boolean get() = target > 0f

    fun start() {
        target = LEVEL
        if (thread?.isAlive == true) return
        thread = Thread({ run() }, "r95-static").also { it.start() }
    }

    fun stop() { target = 0f }

    private fun run() {
        // 16-bit mono: the buffer must be a whole number of 2-byte frames
        val minBuf = AudioTrack.getMinBufferSize(SR, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT).coerceAtLeast(2048)
        val track = runCatching { makeTrack(SR, false, false, AudioAttributes.USAGE_MEDIA, AudioAttributes.CONTENT_TYPE_MUSIC, (maxOf(minBuf, SR) + 1) and 1.inv()) }
            .getOrNull()
        if (track == null || track.state != AudioTrack.STATE_INITIALIZED) { track?.release(); thread = null; return }
        val buf = ShortArray(512)
        var g = 0f
        val up = LEVEL / (0.45f * SR); val down = LEVEL / (0.30f * SR)
        try {
            track.play()
            while (true) {
                for (i in buf.indices) {
                    g = if (g < target) minOf(target, g + up) else maxOf(target, g - down)
                    buf[i] = ((Math.random() * 2 - 1) * g * volume * 32767).toInt().toShort()
                }
                track.write(buf, 0, buf.size)
                if (g <= 0f && target <= 0f) break
            }
        } catch (_: Exception) {
        } finally {
            runCatching { track.stop() }
            track.release()
            thread = null
        }
    }
}
