package com.reverender.r95.audio

import com.reverender.r95.core.Catalog

/**
 * Process-wide bridge between the playback service (audio thread) and the UI.
 * The service writes PCM into [analyzer] and reads [eq]; the UI reads frames and writes EQ gains.
 */
object AudioBus {
    val eq = EqSettings(Catalog.eqFrequencies.size)
    val analyzer = SpectrumAnalyzer()
    val fx = FxSettings()

    /** WAVE ROUTER 95 "source input gain" (pre-EQ), 0.2..2.5. */
    @Volatile var inputGain = 1f

    /** Set by the studio (909 / synth) engine while it is making sound. */
    @Volatile var studioAnalyzer: SpectrumAnalyzer? = null
    @Volatile var studioActive = false
    /** Set by the mic / line-in capture while it is running. */
    @Volatile var micAnalyzer: SpectrumAnalyzer? = null

    /** Android's output visualizer (all phone audio), while SystemVisualizer runs. */
    @Volatile var systemAnalyzer: SpectrumAnalyzer? = null
    /** Widgets, the dock saver, the live wallpaper … want the analyser fed while the app is in the background. */
    val backgroundListeners = java.util.concurrent.atomic.AtomicInteger(0)
    val analysisWanted: Boolean get() = com.reverender.r95.core.AppVisibility.foreground || backgroundListeners.get() > 0

    /** How far the speaker lags behind the analyser. Tunable in SETTINGS (AV sync). */
    @Volatile var outputLatencyMs: Int = 280

    /** Frame matching what is audible right now; silence once audio has stopped flowing. */
    fun currentFrame(nowNanos: Long = System.nanoTime()): AudioFrame {
        micAnalyzer?.let { mic ->
            val f = mic.frameAt(nowNanos - 40_000_000L)
            if (nowNanos - f.timeNanos < 500_000_000L) return f
        }
        if (studioActive) studioAnalyzer?.let { st ->
            val f = st.frameAt(nowNanos - 70_000_000L)
            if (nowNanos - f.timeNanos < 500_000_000L) return f
        }
        val target = nowNanos - outputLatencyMs * 1_000_000L
        val f = analyzer.frameAt(target)
        if (nowNanos - f.timeNanos <= 1_500_000_000L) return f
        // nothing of our own playing: follow the rest of the phone if the system visualizer is on
        systemAnalyzer?.let { sys ->
            val g = sys.frameAt(nowNanos - 30_000_000L)
            if (nowNanos - g.timeNanos < 600_000_000L) return g
        }
        return AudioFrame.SILENT
    }
}
