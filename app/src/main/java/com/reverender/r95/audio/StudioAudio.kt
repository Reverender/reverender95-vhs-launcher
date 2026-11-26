package com.reverender.r95.audio

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Process

/**
 * Low-latency output for TR_909_95 and VAPOR_SYNTH_95. A render thread runs while either
 * instrument is making sound and parks itself after a few seconds of silence.
 */
/** AudioTrack factory that works from Android 5.0 (API 21) upward. */
internal fun makeTrack(sr: Int, float: Boolean, stereo: Boolean, usage: Int, content: Int, bufBytes: Int, static: Boolean = false): AudioTrack {
    val attrs = AudioAttributes.Builder().setUsage(usage).setContentType(content).build()
    val fmt = AudioFormat.Builder().setSampleRate(sr)
        .setEncoding(if (float) AudioFormat.ENCODING_PCM_FLOAT else AudioFormat.ENCODING_PCM_16BIT)
        .setChannelMask(if (stereo) AudioFormat.CHANNEL_OUT_STEREO else AudioFormat.CHANNEL_OUT_MONO).build()
    return AudioTrack(attrs, fmt, bufBytes, if (static) AudioTrack.MODE_STATIC else AudioTrack.MODE_STREAM, android.media.AudioManager.AUDIO_SESSION_ID_GENERATE)
}

object StudioAudio {
    const val SR = 48000
    val core = StudioCore(SR)
    @Volatile private var thread: Thread? = null
    @Volatile private var keepAlive = 0L
    @Volatile private var stopRequested = false

    /** App swiped away: silence the instruments now. */
    fun stopAll() { stopRequested = true }

    /** Call on any user interaction with the instruments. */
    fun wake() {
        keepAlive = System.nanoTime()
        stopRequested = false
        if (thread?.isAlive == true) return
        AudioBus.studioAnalyzer = core.analyzer
        val t = Thread({ run() }, "r95-studio")
        thread = t
        t.start()
    }

    private fun run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
        val minBuf = AudioTrack.getMinBufferSize(SR, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_FLOAT)
        val frames = 256
        // low-latency output path (the same fast mixer route AAudio uses), so MIDI keys and pads answer quickly
        val track = if (android.os.Build.VERSION.SDK_INT >= 26) runCatching {
            AudioTrack.Builder()
                .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                .setAudioFormat(AudioFormat.Builder().setSampleRate(SR).setEncoding(AudioFormat.ENCODING_PCM_FLOAT).setChannelMask(AudioFormat.CHANNEL_OUT_STEREO).build())
                .setBufferSizeInBytes(maxOf(minBuf, frames * 4 * 4))
                .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
        }.getOrNull() ?: makeTrack(SR, true, true, AudioAttributes.USAGE_GAME, AudioAttributes.CONTENT_TYPE_MUSIC, maxOf(minBuf, frames * 8 * 4))
        else makeTrack(SR, true, true, AudioAttributes.USAGE_GAME, AudioAttributes.CONTENT_TYPE_MUSIC, maxOf(minBuf, frames * 8 * 4))
        val buf = FloatArray(frames * 2)
        try {
            track.play()
            AudioBus.studioActive = true
            while (true) {
                if (stopRequested) break
                if (core.busy()) keepAlive = System.nanoTime()
                if (System.nanoTime() - keepAlive > 4_000_000_000L) break
                core.render(buf, frames)
                track.write(buf, 0, buf.size, AudioTrack.WRITE_BLOCKING)
            }
        } catch (_: Exception) {
        } finally {
            AudioBus.studioActive = false
            runCatching { track.stop() }
            track.release()
            thread = null
        }
    }
}

/**
 * Mic / line-in: feeds what the phone hears into the visualisers (the website's "Mic / Line-In" source).
 * Needs the RECORD_AUDIO permission, which the player asks for when the button is pressed.
 */
object MicCapture {
    private const val SR = 44100
    @Volatile var running = false
        private set
    private val analyzer = SpectrumAnalyzer().apply { configure(SR) }
    private var thread: Thread? = null

    /** "mic" or "system" while running. */
    @Volatile var kind = "mic"
        private set

    @SuppressLint("MissingPermission")
    fun start(): Boolean {
        if (running) return true
        val min = AudioRecord.getMinBufferSize(SR, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        if (min <= 0) return false
        val rec = try {
            AudioRecord(MediaRecorder.AudioSource.MIC, SR, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, min * 2)
        } catch (_: Exception) { return false }
        return startWith(rec, "mic")
    }

    /** Runs the analyser on any recorder (the mic, or other apps' playback for "Share PC/Tab"). */
    fun startWith(rec: AudioRecord, what: String, channels: Int = 1): Boolean {
        if (running) stop()
        if (rec.state != AudioRecord.STATE_INITIALIZED) { rec.release(); return false }
        kind = what
        running = true
        AudioBus.micAnalyzer = analyzer
        analyzer.configure(rec.sampleRate)
        thread = Thread({
            val buf = ShortArray(1024 * channels)
            val monitor = if (what == "system") ExternalDsp(rec.sampleRate, channels) else null
            try {
                rec.startRecording()
                while (running) {
                    val n = rec.read(buf, 0, buf.size)
                    if (n > 0) {
                        analyzer.feed(buf, n / channels, channels)
                        monitor?.process(buf, n / channels)
                    }
                }
            } catch (_: Exception) {
            } finally {
                monitor?.release()
                runCatching { rec.stop() }
                rec.release()
                AudioBus.micAnalyzer = null
                running = false
            }
        }, "r95-mic").also { it.start() }
        return true
    }

    fun stop() { running = false }
}

/** Short cassette-deck click for window open / close / buttons ("Cassette deck click sounds"). */
object UiSfx {
    private const val SR = 22050
    private val click: ShortArray by lazy {
        val n = (SR * 0.045).toInt()
        ShortArray(n) { i ->
            val t = i / SR.toDouble()
            val env = kotlin.math.exp(-t * 140)
            val tone = kotlin.math.sin(2 * Math.PI * 1900 * t) * 0.5 + (Math.random() * 2 - 1) * 0.5
            (tone * env * 9000).toInt().toShort()
        }
    }
    @Volatile var enabled = true

    fun play(pitch: Float = 1f) {
        if (!enabled) return
        Thread {
            try {
                val track = makeTrack((SR * pitch).toInt(), false, false, AudioAttributes.USAGE_ASSISTANCE_SONIFICATION, AudioAttributes.CONTENT_TYPE_SONIFICATION, click.size * 2, static = true)
                track.write(click, 0, click.size)
                track.play()
                Thread.sleep(120)
                track.release()
            } catch (_: Exception) { }
        }.start()
    }
}

/** The fluorescent-light hum that plays under the Backrooms wallpapers. */
object BackroomsHum {
    @Volatile private var running = false

    fun set(on: Boolean) {
        if (on == running) return
        running = on
        if (!on) return
        Thread({
            val sr = 22050
            val track = makeTrack(sr, false, false, AudioAttributes.USAGE_MEDIA, AudioAttributes.CONTENT_TYPE_SONIFICATION, sr)
            val buf = ShortArray(1024)
            var ph = 0.0
            try {
                track.play()
                while (running) {
                    for (i in buf.indices) {
                        ph += 1.0 / sr
                        val s = kotlin.math.sin(2 * Math.PI * 60 * ph) * 0.5 + kotlin.math.sin(2 * Math.PI * 120 * ph) * 0.3 +
                            kotlin.math.sin(2 * Math.PI * 180 * ph) * 0.12 + (Math.random() * 2 - 1) * 0.04
                        buf[i] = (s * 1400).toInt().toShort()
                    }
                    track.write(buf, 0, buf.size)
                }
            } catch (_: Exception) {
            } finally {
                runCatching { track.stop() }
                track.release()
            }
        }, "r95-hum").start()
    }
}
