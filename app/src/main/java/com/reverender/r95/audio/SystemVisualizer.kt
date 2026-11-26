package com.reverender.r95.audio

import android.Manifest
import android.content.pm.PackageManager
import android.media.audiofx.Visualizer
import com.reverender.r95.os.OsPrefs

/**
 * "React to all phone audio": Android's output visualizer on the global mix (session 0) taps whatever
 * the phone plays — Spotify, YouTube, games — for the visualizers, the live wallpaper and the dock
 * screensaver, with no screen-capture prompt. Needs the microphone permission (Android's rule for
 * the visualizer); some phones restrict session 0, then [status] says so.
 */
object SystemVisualizer {
    private var viz: Visualizer? = null
    val analyzer = SpectrumAnalyzer().apply { configure(48000) }
    @Volatile var running = false; private set
    var status = "OFF"; private set
    private var pcm = ShortArray(0)
    /** things (live wallpaper, dock saver, widgets) that want system audio even when the switch is off */
    private val users = java.util.concurrent.atomic.AtomicInteger(0)

    fun acquire() { users.incrementAndGet(); refresh() }
    fun release() { if (users.decrementAndGet() < 0) users.set(0); refresh() }

    fun permitted(): Boolean = runCatching {
        OsPrefs.app.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }.getOrDefault(false)

    fun refresh() {
        val want = (runCatching { OsPrefs.systemReactive }.getOrDefault(false) && com.reverender.r95.core.AppVisibility.foreground) || users.get() > 0
        if (want && !running) start() else if (!want && running) stop()
    }

    private fun start() {
        if (!permitted()) { status = "NEEDS MIC PERMISSION"; return }
        val v = runCatching { Visualizer(0) }.getOrElse { status = "BLOCKED ON THIS PHONE"; return }
        runCatching {
            v.enabled = false
            val range = Visualizer.getCaptureSizeRange()
            v.captureSize = range[1].coerceAtMost(1024)
            runCatching { v.scalingMode = Visualizer.SCALING_MODE_NORMALIZED }
            v.setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                override fun onWaveFormDataCapture(vz: Visualizer?, wave: ByteArray, rate: Int) {
                    if (pcm.size != wave.size) pcm = ShortArray(wave.size)
                    var any = false
                    for (i in wave.indices) {
                        val s = ((wave[i].toInt() and 0xFF) - 128) shl 8
                        if (s != 0) any = true
                        pcm[i] = s.toShort()
                    }
                    if (any) analyzer.feed(pcm, pcm.size, 1)
                }
                override fun onFftDataCapture(vz: Visualizer?, fft: ByteArray, rate: Int) {}
            }, Visualizer.getMaxCaptureRate(), true, false)
            v.enabled = true
            viz = v; running = true; status = "LISTENING TO ALL PHONE AUDIO"
            AudioBus.systemAnalyzer = analyzer
        }.onFailure { runCatching { v.release() }; status = "BLOCKED ON THIS PHONE" }
    }

    private fun stop() {
        AudioBus.systemAnalyzer = null
        runCatching { viz?.enabled = false; viz?.release() }
        viz = null; running = false; status = "OFF"
    }
}
