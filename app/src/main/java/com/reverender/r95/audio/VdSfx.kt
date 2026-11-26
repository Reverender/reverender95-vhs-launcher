package com.reverender.r95.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sin

/**
 * Vector Dreams' synth sounds, like the site's L(): oscillator → (low-pass sweep) → exponential envelope,
 * into a master gain of 0.2 with a 0.19 s feedback echo. Each sound is rendered to PCM and played once.
 */
object VdSfx {
    class Note(val f: Double, val at: Double, val dur: Double, val type: String = "triangle", val gain: Double = 0.4, val fTo: Double? = null, val lp: Pair<Double, Double>? = null)

    private const val SR = 22050
    private val exec = Executors.newSingleThreadExecutor { Thread(it, "vd-sfx").apply { isDaemon = true } }
    private var lastPulse = 0L

    private fun osc(type: String, ph: Double): Double {
        val p = ph - kotlin.math.floor(ph)
        return when (type) {
            "sine" -> sin(2 * PI * p)
            "square" -> if (p < 0.5) 1.0 else -1.0
            "sawtooth" -> 2 * p - 1
            else -> 1 - 4 * kotlin.math.abs(p - 0.5) // triangle
        }
    }

    fun play(notes: List<Note>) {
        exec.execute {
            try {
                val end = notes.maxOf { it.at + it.dur + 0.05 }
                val tail = 0.19 * 4
                val n = ((end + tail) * SR).toInt()
                val buf = DoubleArray(n)
                for (nt in notes) {
                    val s0 = (nt.at * SR).toInt(); val len = ((nt.dur + 0.05) * SR).toInt()
                    var ph = 0.0; var lpY = 0.0
                    for (i in 0 until len) {
                        val t = i.toDouble() / SR
                        val k = (t / nt.dur).coerceIn(0.0, 1.0)
                        val f = if (nt.fTo != null) nt.f * exp(ln(nt.fTo / nt.f) * k) else nt.f
                        ph += f / SR
                        var v = osc(nt.type, ph)
                        if (nt.lp != null) {
                            val fc = nt.lp.first * exp(ln(nt.lp.second / nt.lp.first) * k)
                            val a = 1 - exp(-2 * PI * fc / SR)
                            lpY += a * (v - lpY); v = lpY
                        }
                        // 1e-4 → gain in 8 ms, then exponential decay to 1e-4 at dur
                        val g = if (t < 0.008) 1e-4 * exp(ln(nt.gain / 1e-4) * t / 0.008)
                        else if (t < nt.dur) nt.gain * exp(ln(1e-4 / nt.gain) * (t - 0.008) / (nt.dur - 0.008)) else 0.0
                        val idx = s0 + i
                        if (idx < n) buf[idx] += v * g
                    }
                }
                // master 0.2 → compressor-ish soft clip, plus the 0.19 s echo (feedback .28, wet .22)
                val d = (0.19 * SR).toInt()
                val wet = DoubleArray(n)
                for (i in 0 until n) { val x = buf[i] * 0.2; val fb = if (i >= d) wet[i - d] * 0.28 else 0.0; wet[i] = x + fb }
                val out = ShortArray(n)
                for (i in 0 until n) {
                    val dry = buf[i] * 0.2; val e = if (i >= d) wet[i - d] * 0.22 else 0.0
                    val y = kotlin.math.tanh((dry + e) * 1.6) / 1.6
                    out[i] = (y.coerceIn(-1.0, 1.0) * 32767 * 0.9).toInt().toShort()
                }
                val track = AudioTrack.Builder()
                    .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
                    .setAudioFormat(AudioFormat.Builder().setSampleRate(SR).setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                    .setBufferSizeInBytes(n * 2).setTransferMode(AudioTrack.MODE_STATIC).build()
                track.write(out, 0, n)
                track.play()
                Thread.sleep(((n.toDouble() / SR) * 1000).toLong() + 60)
                track.release()
            } catch (e: Throwable) { android.util.Log.w("R95", "vd sfx", e) }
        }
    }

    private val scale = intArrayOf(0, 3, 5, 7, 10)
    private fun c(i: Int) = 220 * Math.pow(2.0, (scale[i % 5] + 12 * (i / 5)) / 12.0)

    /** The site's sound table (l[...]). */
    fun event(kind: String, combo: Int = 1, critical: Boolean = false, n: Int = 1) {
        val L = ArrayList<Note>()
        when (kind) {
            "pulse" -> {
                val now = System.currentTimeMillis(); if (now - lastPulse < 45) return; lastPulse = now
                val te = c(minOf(19, combo - 1).coerceAtLeast(0))
                L += Note(te, 0.0, 0.11, "triangle", 0.45); L += Note(te * 2, 0.0, 0.06, "sine", 0.18)
                if (critical) listOf(1.0, 1.26, 1.5, 2.0).forEachIndexed { i, m -> L += Note(te * m, 0.02 * i, 0.32, "square", 0.12, null, 4e3 to 600.0) }
            }
            "buy" -> if (n > 1) listOf(523, 659, 784, 1047, 1319).forEachIndexed { i, f -> L += Note(f.toDouble(), i * 0.035, 0.09, "square", 0.12, null, 5e3 to 1500.0) }
            else { L += Note(659.0, 0.0, 0.07, "square", 0.14, null, 5e3 to 1500.0); L += Note(988.0, 0.065, 0.12, "square", 0.13, null, 5e3 to 1500.0) }
            "upgrade" -> { listOf(523, 659, 784, 1047).forEachIndexed { i, f -> L += Note(f.toDouble(), i * 0.06, 0.28, "triangle", 0.3) }; L += Note(2093.0, 0.26, 0.5, "sine", 0.12) }
            "level" -> { listOf(392, 494, 587, 784, 988).forEachIndexed { i, f -> L += Note(f.toDouble(), i * 0.07, 0.26, "triangle", 0.32) }; L += Note(1568.0, 0.4, 0.7, "sine", 0.15) }
            "overdrive" -> { L += Note(110.0, 0.0, 1.0, "sawtooth", 0.22, 880.0, 300.0 to 6e3); L += Note(55.0, 0.95, 0.4, "sine", 0.6, 40.0) }
            "milestone" -> { L += Note(880.0, 0.0, 1.3, "sine", 0.3); L += Note(1320.0, 0.01, 1.2, "sine", 0.2); L += Note(1760.0, 0.02, 0.9, "sine", 0.1) }
            "prestige" -> { L += Note(220.0, 0.0, 2.2, "sawtooth", 0.18, 1760.0, 200.0 to 5e3); listOf(440, 554, 659, 880).forEachIndexed { i, f -> L += Note(f.toDouble(), 1.6 + i * 0.08, 0.8, "triangle", 0.22) } }
            "prestigeUp" -> { L += Note(330.0, 0.0, 0.3, "triangle", 0.3); L += Note(495.0, 0.08, 0.4, "triangle", 0.3) }
            "cometSpawn" -> { L += Note(1568.0, 0.0, 0.5, "sine", 0.12); L += Note(2093.0, 0.12, 0.6, "sine", 0.08) }
            "cometCatch" -> listOf(784, 988, 1175, 1568, 1976, 2349).forEachIndexed { i, f -> L += Note(f.toDouble(), i * 0.04, 0.22, "triangle", 0.25) }
            "quest" -> { listOf(659, 784, 988, 1319).forEachIndexed { i, f -> L += Note(f.toDouble(), i * 0.08, 0.3, "triangle", 0.3) }; L += Note(330.0, 0.0, 0.5, "sine", 0.3) }
            "panelOpen" -> L += Note(660.0, 0.0, 0.08, "triangle", 0.2)
            "panelClose" -> L += Note(440.0, 0.0, 0.08, "triangle", 0.2)
            else -> return
        }
        play(L)
    }
}
