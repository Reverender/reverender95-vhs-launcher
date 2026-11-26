package com.reverender.r95.audio

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Pure-Kotlin DSP used by the audio pipeline. No Android imports, so it can be
 * unit-tested on the JVM.
 */

/** One RBJ-cookbook biquad, Direct Form I, per channel state. */
class Biquad(private val channels: Int) {
    private var b0 = 1.0; private var b1 = 0.0; private var b2 = 0.0
    private var a1 = 0.0; private var a2 = 0.0
    private val x1 = DoubleArray(channels); private val x2 = DoubleArray(channels)
    private val y1 = DoubleArray(channels); private val y2 = DoubleArray(channels)

    enum class Type { PEAK, LOW_SHELF, HIGH_SHELF }

    fun set(type: Type, freq: Double, q: Double, gainDb: Double, sampleRate: Int) {
        val f = min(freq, sampleRate * 0.45)
        val a = 10.0.pow(gainDb / 40.0)
        val w0 = 2.0 * PI * f / sampleRate
        val cw = cos(w0); val sw = sin(w0)
        val alpha = sw / (2.0 * q)
        var nb0: Double; var nb1: Double; var nb2: Double
        var na0: Double; var na1: Double; var na2: Double
        when (type) {
            Type.PEAK -> {
                nb0 = 1 + alpha * a; nb1 = -2 * cw; nb2 = 1 - alpha * a
                na0 = 1 + alpha / a; na1 = -2 * cw; na2 = 1 - alpha / a
            }
            Type.LOW_SHELF -> {
                val s = 2 * sqrt(a) * alpha
                nb0 = a * ((a + 1) - (a - 1) * cw + s); nb1 = 2 * a * ((a - 1) - (a + 1) * cw); nb2 = a * ((a + 1) - (a - 1) * cw - s)
                na0 = (a + 1) + (a - 1) * cw + s; na1 = -2 * ((a - 1) + (a + 1) * cw); na2 = (a + 1) + (a - 1) * cw - s
            }
            Type.HIGH_SHELF -> {
                val s = 2 * sqrt(a) * alpha
                nb0 = a * ((a + 1) + (a - 1) * cw + s); nb1 = -2 * a * ((a - 1) + (a + 1) * cw); nb2 = a * ((a + 1) + (a - 1) * cw - s)
                na0 = (a + 1) - (a - 1) * cw + s; na1 = 2 * ((a - 1) - (a + 1) * cw); na2 = (a + 1) - (a - 1) * cw - s
            }
        }
        b0 = nb0 / na0; b1 = nb1 / na0; b2 = nb2 / na0; a1 = na1 / na0; a2 = na2 / na0
    }

    fun setRaw(nb0: Double, nb1: Double, nb2: Double, na1: Double, na2: Double) {
        b0 = nb0; b1 = nb1; b2 = nb2; a1 = na1; a2 = na2
    }

    fun process(x: Double, ch: Int): Double {
        val y = b0 * x + b1 * x1[ch] + b2 * x2[ch] - a1 * y1[ch] - a2 * y2[ch]
        x2[ch] = x1[ch]; x1[ch] = x
        y2[ch] = y1[ch]; y1[ch] = y
        return y
    }

    fun reset() { x1.fill(0.0); x2.fill(0.0); y1.fill(0.0); y2.fill(0.0) }
}

/**
 * The website's 8-band graphic EQ (lowshelf, 6 x peaking Q1.2, highshelf) plus preamp,
 * applied to interleaved 16-bit PCM in place.
 */
class EqBank(private val sampleRate: Int, private val channels: Int, private val freqs: FloatArray) {
    private val filters = Array(freqs.size) { Biquad(channels) }
    private var preampLinear = 1.0
    private var bypass = true
    private var appliedVersion = -1

    fun syncWith(settings: EqSettings) {
        val v = settings.version
        if (v == appliedVersion) return
        appliedVersion = v
        val gains = settings.gains
        bypass = !settings.enabled || (gains.all { abs(it) < 0.05f } && abs(settings.preampDb) < 0.05f)
        preampLinear = 10.0.pow(settings.preampDb / 20.0)
        for (i in freqs.indices) {
            val type = when (i) {
                0 -> Biquad.Type.LOW_SHELF
                freqs.size - 1 -> Biquad.Type.HIGH_SHELF
                else -> Biquad.Type.PEAK
            }
            filters[i].set(type, freqs[i].toDouble(), if (type == Biquad.Type.PEAK) 1.2 else 0.707, gains.getOrElse(i) { 0f }.toDouble(), sampleRate)
        }
    }

    /** Processes [frames] interleaved frames of [pcm] in place. */
    fun process(pcm: ShortArray, frames: Int) {
        if (bypass) return
        var idx = 0
        for (f in 0 until frames) {
            for (ch in 0 until channels) {
                var s = pcm[idx] / 32768.0 * preampLinear
                for (filter in filters) s = filter.process(s, ch)
                // Soft clip rather than wrap when boosted bands push past full scale.
                if (s > 0.95 || s < -0.95) s = kotlin.math.tanh(s)
                pcm[idx] = (max(-1.0, min(1.0, s)) * 32767.0).toInt().toShort()
                idx++
            }
        }
    }

    fun reset() = filters.forEach { it.reset() }
}

/** Shared EQ state: written by the UI, read by the audio thread. */
class EqSettings(bands: Int) {
    @Volatile var gains: FloatArray = FloatArray(bands)
        private set
    @Volatile var preampDb: Float = 0f
        private set
    @Volatile var enabled: Boolean = true
        private set
    @Volatile var version: Int = 0
        private set

    fun update(gains: FloatArray = this.gains, preampDb: Float = this.preampDb, enabled: Boolean = this.enabled) {
        this.gains = gains.copyOf()
        this.preampDb = preampDb
        this.enabled = enabled
        version++
    }
}

/** In-place iterative radix-2 FFT. */
object Fft {
    fun transform(re: FloatArray, im: FloatArray) {
        val n = re.size
        require(n == im.size && n and (n - 1) == 0) { "size must be a power of two" }
        var j = 0
        for (i in 1 until n) {
            var bit = n shr 1
            while (j and bit != 0) { j = j xor bit; bit = bit shr 1 }
            j = j xor bit
            if (i < j) {
                var t = re[i]; re[i] = re[j]; re[j] = t
                t = im[i]; im[i] = im[j]; im[j] = t
            }
        }
        var len = 2
        while (len <= n) {
            val ang = -2.0 * PI / len
            val wr = cos(ang).toFloat(); val wi = sin(ang).toFloat()
            var i = 0
            while (i < n) {
                var cr = 1f; var ci = 0f
                for (k in 0 until len / 2) {
                    val ar = re[i + k + len / 2] * cr - im[i + k + len / 2] * ci
                    val ai = re[i + k + len / 2] * ci + im[i + k + len / 2] * cr
                    re[i + k + len / 2] = re[i + k] - ar
                    im[i + k + len / 2] = im[i + k] - ai
                    re[i + k] += ar
                    im[i + k] += ai
                    val ncr = cr * wr - ci * wi
                    ci = cr * wi + ci * wr
                    cr = ncr
                }
                i += len
            }
            len = len shl 1
        }
    }
}

/** One analysed slice of audio, ready for the visualisers. Arrays are never mutated after publishing. */
class AudioFrame(
    val timeNanos: Long,
    /** 64 log-spaced bands, 0..1. */
    val bands: FloatArray,
    /** 256 mono samples, -1..1. */
    val wave: FloatArray,
    /** 256 left / right samples, for Lissajous. */
    val left: FloatArray,
    val right: FloatArray,
    val rmsL: Float,
    val rmsR: Float,
    val bass: Float,
    val mid: Float,
    val treble: Float,
    /** The website's analyser view: 256 bins, 0..255 (fftSize 512, -88..-10 dB, smoothing 0.66). */
    val web: FloatArray = FloatArray(WEB_BINS),
) {
    companion object {
        const val BANDS = 64
        const val WAVE = 256
        const val WEB_BINS = 256
        val SILENT = AudioFrame(0L, FloatArray(BANDS), FloatArray(WAVE), FloatArray(WAVE), FloatArray(WAVE), 0f, 0f, 0f, 0f, 0f)
    }
}

/**
 * Collects PCM, runs a 2048-point FFT every hop, and keeps a short history of frames
 * stamped with the time they were analysed. The UI picks the frame from
 * "now - output latency" so the visuals line up with what you actually hear.
 */
class SpectrumAnalyzer(private val clock: () -> Long = System::nanoTime) {
    private val n = 2048
    private val hop = 1024
    private val ringL = FloatArray(n)
    private val ringR = FloatArray(n)
    private var writePos = 0
    private var sinceHop = 0
    private val re = FloatArray(n)
    private val im = FloatArray(n)
    private val window = FloatArray(n) { (0.5 - 0.5 * cos(2.0 * PI * it / (n - 1))).toFloat() }
    private val smooth = FloatArray(AudioFrame.BANDS)
    private val webSmooth = FloatArray(AudioFrame.WEB_BINS)
    private var sampleRate = 44100
    private var bandEdges = IntArray(AudioFrame.BANDS + 1)

    private val history = arrayOfNulls<AudioFrame>(160)
    @Volatile private var historyHead = -1

    fun configure(rate: Int) {
        sampleRate = rate
        val minF = 30.0; val maxF = min(16000.0, rate / 2.0 - 1)
        bandEdges = IntArray(AudioFrame.BANDS + 1) { i ->
            val f = minF * (maxF / minF).pow(i.toDouble() / AudioFrame.BANDS)
            max(1, min(n / 2 - 1, (f * n / rate).toInt()))
        }
        for (i in 1 until bandEdges.size) if (bandEdges[i] <= bandEdges[i - 1]) bandEdges[i] = min(n / 2 - 1, bandEdges[i - 1] + 1)
        clear()
    }

    fun clear() {
        ringL.fill(0f); ringR.fill(0f); smooth.fill(0f)
        writePos = 0; sinceHop = 0
    }

    /** Feed interleaved 16-bit samples. Called on the audio thread. */
    fun feed(pcm: ShortArray, frames: Int, channels: Int) {
        var idx = 0
        for (f in 0 until frames) {
            val l = pcm[idx] / 32768f
            val r = if (channels > 1) pcm[idx + 1] / 32768f else l
            idx += channels
            ringL[writePos] = l; ringR[writePos] = r
            writePos = (writePos + 1) and (n - 1)
            if (++sinceHop >= hop) { sinceHop = 0; analyse() }
        }
    }

    private fun analyse() {
        var sumL = 0f; var sumR = 0f
        for (i in 0 until n) {
            val p = (writePos + i) and (n - 1)
            val l = ringL[p]; val r = ringR[p]
            re[i] = (l + r) * 0.5f * window[i]; im[i] = 0f
            if (i >= n - hop) { sumL += l * l; sumR += r * r }
        }
        Fft.transform(re, im)
        val bands = FloatArray(AudioFrame.BANDS)
        for (b in 0 until AudioFrame.BANDS) {
            var peak = 0f
            for (k in bandEdges[b] until max(bandEdges[b] + 1, bandEdges[b + 1])) {
                val mag = sqrt(re[k] * re[k] + im[k] * im[k])
                if (mag > peak) peak = mag
            }
            // Magnitude -> dB -> 0..1 over a 70 dB window, with a gentle tilt so highs aren't starved.
            val db = 20f * (ln(peak / (n / 4f) + 1e-9f) / ln(10f)) + 6f * b / AudioFrame.BANDS
            val v = ((db + 70f) / 70f).coerceIn(0f, 1f)
            smooth[b] = if (v > smooth[b]) v else smooth[b] * 0.82f + v * 0.18f
            bands[b] = smooth[b]
        }
        // WebAudio AnalyserNode emulation for the site's wallpapers: 4 of our bins = 1 bin of a 512-point FFT
        val web = FloatArray(AudioFrame.WEB_BINS)
        for (k in 0 until AudioFrame.WEB_BINS) {
            var e = 0f
            for (j in k * 4 until min(n / 2, k * 4 + 4)) e += re[j] * re[j] + im[j] * im[j]
            val mag = sqrt(e) / n * 0.84f
            webSmooth[k] = 0.66f * webSmooth[k] + 0.34f * mag
            val db = 20f * (ln(webSmooth[k] + 1e-12f) / ln(10f))
            web[k] = (255f * (db + 88f) / 78f).coerceIn(0f, 255f)
        }
        val wave = FloatArray(AudioFrame.WAVE); val left = FloatArray(AudioFrame.WAVE); val right = FloatArray(AudioFrame.WAVE)
        val start = writePos - AudioFrame.WAVE * 2
        for (i in 0 until AudioFrame.WAVE) {
            val p = (start + i * 2) and (n - 1)
            left[i] = ringL[p]; right[i] = ringR[p]; wave[i] = (ringL[p] + ringR[p]) * 0.5f
        }
        fun avg(from: Int, to: Int): Float { var s = 0f; for (i in from until to) s += bands[i]; return s / (to - from) }
        val frame = AudioFrame(
            timeNanos = clock(),
            bands = bands, wave = wave, left = left, right = right,
            rmsL = sqrt(sumL / hop), rmsR = sqrt(sumR / hop),
            bass = avg(0, 14), mid = avg(14, 40), treble = avg(40, AudioFrame.BANDS),
            web = web,
        )
        val next = (historyHead + 1).mod(history.size)
        history[next] = frame
        historyHead = next
    }

    /** Latest frame analysed at or before [atNanos]; falls back to the oldest available. */
    fun frameAt(atNanos: Long): AudioFrame {
        val head = historyHead
        if (head < 0) return AudioFrame.SILENT
        var best: AudioFrame? = null
        for (i in 0 until history.size) {
            val f = history[(head - i).mod(history.size)] ?: break
            best = f
            if (f.timeNanos <= atNanos) return f
        }
        return best ?: AudioFrame.SILENT
    }

    fun latestTime(): Long = history.getOrNull(historyHead)?.timeNanos ?: 0L
}
