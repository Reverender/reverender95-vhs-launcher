package com.reverender.r95.audio

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/**
 * TR_909_95 + VAPOR_SYNTH_95 sound engine, ported from rr-studio-909-synth.
 * Pure Kotlin: renders interleaved stereo float blocks. The Android side
 * (StudioAudio) pushes these blocks into an AudioTrack.
 */

class DrumVoiceDef(val id: String, val code: String, val name: String, var level: Int, tune0: Int, var decay: Int, var tone: Int) { var tune: Double = tune0.toDouble() }

private fun expRamp(a: Double, b: Double, t: Double, dur: Double): Double =
    if (t >= dur) b else a * (b / a).pow(t / dur)

/** A single drum hit being rendered. */
private class DrumHit(val voice: Int, val def: DrumVoiceDef, accent: Boolean, val sr: Double) {
    var t = 0.0
    val u = (if (accent) 1.32 else 1.0) * def.level / 100.0
    val tune = 2.0.pow(def.tune / 12.0)
    val d = max(0.15, def.decay / 60.0)
    val m = def.tone / 100.0
    var phase1 = 0.0; var phase2 = 0.0
    val sq = DoubleArray(6)
    val bp1 = Svf(); val bp2 = Svf(); val hpf = Svf()
    var choked = false; var chokeT = 0.0
    val length: Double = when (def.id) {
        "bd" -> 0.44 * d; "sd" -> 0.26 * d; "lt", "mt", "ht" -> 0.32 * d; "rs" -> 0.055 * d
        "cp" -> 0.28 * d; "ch" -> 0.075 * d + 0.02; "oh" -> 0.42 * d + 0.02; else -> 0.88 * d + 0.02
    }
    val done get() = t >= length || (choked && t - chokeT > 0.025)

    fun next(): Double {
        val dt = 1.0 / sr
        var y = 0.0
        when (def.id) {
            "bd" -> {
                val f = if (t < 0.045) expRamp(148 * tune, 52 * tune, t, 0.045) else expRamp(52 * tune, 34 * tune, t - 0.045, 0.36 * d - 0.045)
                phase1 += 2 * PI * f * dt
                y += sin(phase1) * expRamp(min(1.4, u * 1.15), 0.001, t, 0.42 * d)
                if (t < 0.025) {
                    val fc = expRamp(920 * (0.6 + m * 0.8), 80.0, t, 0.018)
                    phase2 += fc * dt
                    y += tri(phase2) * expRamp(u * (0.35 + m * 0.45), 0.001, t, 0.02)
                }
            }
            "sd" -> {
                val f1 = expRamp(185 * 1.35 * tune, 185 * tune, t, 0.035); val f2 = expRamp(330 * 1.35 * tune, 330 * tune, t, 0.035)
                phase1 += f1 * dt; phase2 += f2 * dt
                y += tri(phase1) * expRamp(u * 0.55, 0.001, t, 0.14 * d) + tri(phase2) * expRamp(u * 0.35, 0.001, t, 0.14 * d)
                hpf.process(Random.nextDouble() * 2 - 1, 900 + m * 1600, 0.707, sr)
                y += hpf.high * expRamp(u * (0.45 + m * 0.5), 0.001, t, 0.24 * d)
            }
            "lt", "mt", "ht" -> {
                val base = when (def.id) { "lt" -> 92.0; "mt" -> 138.0; else -> 196.0 }
                val f = if (t < 0.08 * d) expRamp(base * 1.65 * tune, base * tune, t, 0.08 * d) else expRamp(base * tune, base * 0.68 * tune, t - 0.08 * d, 0.2 * d)
                phase1 += 2 * PI * f * dt
                y += sin(phase1) * expRamp(u * 0.9, 0.001, t, 0.3 * d)
            }
            "rs" -> {
                phase1 += 480 * tune * dt; phase2 += 1680 * tune * dt
                bp1.process(sqr(phase1), 480 * tune, 6.5, sr); bp2.process(sqr(phase2), 1680 * tune, 6.5, sr)
                y += bp1.band * expRamp(u * 0.55, 0.001, t, 0.045 * d) + bp2.band * expRamp(u * 0.7, 0.001, t, 0.045 * d)
            }
            "cp" -> {
                bp1.process(Random.nextDouble() * 2 - 1, 1150 * tune, 1.9, sr)
                val env = when {
                    t < 0.033 -> { val k = (t % 0.011); expRamp(u * 0.85, 0.04, k, 0.009) }
                    else -> expRamp(u * 0.75, 0.001, t - 0.033, 0.26 * d - 0.033)
                }
                y += bp1.band * env * 2.2
            }
            else -> { // ch / oh / cr: six detuned squares + noise through a high-pass
                val freqs = doubleArrayOf(263.0, 400.0, 421.0, 474.0, 587.0, 845.0)
                val det = if (def.id == "cr") 0.88 else 1.0
                var s = 0.0
                for (k in 0 until 6) { sq[k] += freqs[k] * tune * det * dt; s += sqr(sq[k]) * 0.14 }
                s += (Random.nextDouble() * 2 - 1) * 0.22
                hpf.process(s, (if (def.id == "cr") 4200.0 else 6800.0) * (0.75 + m * 0.45), 0.707, sr)
                val ve = when (def.id) { "ch" -> 0.075 * d; "oh" -> 0.42 * d; else -> 0.88 * d }
                var env = expRamp(u * 0.58, 1e-4, t, ve)
                if (choked) env *= expRamp(1.0, 1e-3, t - chokeT, 0.025)
                y += hpf.high * env
            }
        }
        t += dt
        return y
    }

    private fun tri(p: Double): Double { val x = p - floor(p); return 4 * abs(x - 0.5) - 1 }
    private fun sqr(p: Double): Double = if (p - floor(p) < 0.5) 1.0 else -1.0
}

class Drum909(private val sr: Int) {
    val voices = listOf(
        DrumVoiceDef("bd", "BD", "BASS DRUM", 100, 0, 68, 65), DrumVoiceDef("sd", "SD", "SNARE DRUM", 92, 0, 58, 70),
        DrumVoiceDef("lt", "LT", "LOW TOM", 85, 0, 62, 50), DrumVoiceDef("mt", "MT", "MID TOM", 85, 0, 58, 55),
        DrumVoiceDef("ht", "HT", "HI TOM", 85, 0, 52, 60), DrumVoiceDef("rs", "RS", "RIM SHOT", 82, 0, 45, 70),
        DrumVoiceDef("cp", "CP", "HAND CLAP", 88, 0, 65, 65), DrumVoiceDef("ch", "CH", "CLOSED HAT", 78, 0, 35, 75),
        DrumVoiceDef("oh", "OH", "OPEN HAT", 80, 0, 75, 75), DrumVoiceDef("cr", "CR", "CRASH / RIDE", 76, 0, 90, 70),
    )
    /** 4 patterns x 10 voices x 16 steps; 0 = off, 1 = on, 2 = accent. */
    val patterns: Array<Array<IntArray>> = Array(4) { Array(10) { IntArray(16) } }
    val muted = BooleanArray(10)
    @Volatile var playing = false
    @Volatile var bpm = 118
    @Volatile var swing = 54
    @Volatile var drive = 18
    @Volatile var masterVol = 85
    @Volatile var currentPat = 0
    @Volatile var chain = false
    @Volatile var roll = false
    @Volatile var selectedVoice = 0
    @Volatile var currentStep = 0
        private set
    /** Bumped on every step so the UI can light the playhead. */
    @Volatile var stepTick = 0L
        private set
    /** Bitmask of voices hit on the last step / pad tap (for pad flashes). */
    @Volatile var lastHits = 0
        private set

    private val hits = ArrayList<DrumHit>()
    private val pending = java.util.concurrent.ConcurrentLinkedQueue<Pair<Int, Boolean>>()
    private var samplesToNext = 0.0
    private var rollCounter = 0.0

    fun trigger(voice: Int, accent: Boolean = false) { pending.add(voice to accent) }

    fun start() { currentStep = 0; samplesToNext = 0.0; playing = true }
    fun stop() { playing = false }

    private fun hit(voice: Int, accent: Boolean) {
        val def = voices[voice]
        if (def.level <= 0) return
        if (def.id == "ch") hits.forEach { if (it.def.id == "oh" && !it.choked) { it.choked = true; it.chokeT = it.t } }
        if (hits.size > 48) hits.removeAt(0)
        hits.add(DrumHit(voice, def, accent, sr.toDouble()))
        lastHits = lastHits or (1 shl voice)
    }

    private fun stepDurationSamples(step: Int): Double {
        val l = 60.0 / bpm / 4
        val d = (swing - 50) / 50.0
        return (if (step % 2 == 0) l * (1 + d * 0.65) else l * (1 - d * 0.65)) * sr
    }

    private fun driveCurve(x: Double): Double {
        val k = drive * 2.2
        return if (k == 0.0) x else (3 + k) * x * 20 * (PI / 180) / (PI + k * abs(x))
    }

    /** Adds this machine's output into [out] (stereo interleaved). */
    fun render(out: FloatArray, frames: Int) {
        while (true) { val p = pending.poll() ?: break; hit(p.first, p.second) }
        val vol = masterVol / 100.0
        for (f in 0 until frames) {
            if (playing) {
                if (samplesToNext <= 0) {
                    lastHits = 0
                    val step = currentStep
                    val pat = patterns[currentPat]
                    for (v in 0 until 10) if (!muted[v] && pat[v][step] > 0) hit(v, pat[v][step] == 2)
                    stepTick++
                    samplesToNext += stepDurationSamples(step)
                    currentStep = (step + 1) % 16
                    if (currentStep == 0 && chain) currentPat = (currentPat + 1) % 4
                }
                samplesToNext -= 1.0
                if (roll) {
                    rollCounter -= 1.0
                    if (rollCounter <= 0) { hit(selectedVoice, true); rollCounter = 60.0 / bpm / 8 * sr }
                }
            } else if (roll) {
                rollCounter -= 1.0
                if (rollCounter <= 0) { hit(selectedVoice, true); rollCounter = 60.0 / bpm / 8 * sr }
            }
            var s = 0.0
            if (hits.isNotEmpty()) {
                var i = 0
                while (i < hits.size) {
                    val h = hits[i]
                    if (h.done) { hits.removeAt(i); continue }
                    s += h.next(); i++
                }
            }
            val y = (driveCurve(s) * vol).toFloat()
            out[f * 2] += y; out[f * 2 + 1] += y
        }
    }

    fun clearPattern(p: Int = currentPat) { patterns[p].forEach { it.fill(0) } }

    fun copyPatternToNext() { val dst = (currentPat + 1) % 4; for (v in 0 until 10) patterns[currentPat][v].copyInto(patterns[dst][v]) }

    /** Same groove generator as the website's RND button. */
    fun randomize() {
        val e = Array(10) { IntArray(16) }
        val r = Random
        for (t in 0 until 16) {
            if (t % 4 == 0 || (t == 14 && r.nextDouble() < .5)) e[0][t] = 2
            if (t == 4 || t == 12) e[1][t] = 2
            if (r.nextDouble() < .22) e[2 + r.nextInt(3)][t] = 1
            if (r.nextDouble() < .18) e[5][t] = 1
            if (t == 4 || t == 12 || r.nextDouble() < .15) e[6][t] = 1
            if (t % 2 == 0 || r.nextDouble() < .55) e[7][t] = if (t % 4 == 2) 2 else 1
            if (t % 4 == 2 && r.nextDouble() < .65) { e[8][t] = 1; e[7][t] = 0 }
            if (t == 0 && r.nextDouble() < .6) e[9][t] = 1
        }
        for (v in 0 until 10) e[v].copyInto(patterns[currentPat][v])
    }
}

// ------------------------------------------------------------------ synth

enum class Wave(val label: String) { SAW("SAW"), SQR("SQR"), TRI("TRI"), SIN("SIN") }
enum class FilterMode(val label: String) { LPF("LPF"), BPF("BPF"), HPF("HPF") }

class Chord(val name: String, val intervals: IntArray)

class SynthParams {
    @Volatile var octave = 3
    @Volatile var wave1 = Wave.SAW
    @Volatile var wave2 = Wave.SQR
    @Volatile var filterMode = FilterMode.LPF
    @Volatile var chordIdx = 0
    @Volatile var arpIdx = 0
    @Volatile var hold = false
    @Volatile var cutoff = 2800f     // 60..16000
    @Volatile var res = 4.5f         // 0.5..20
    @Volatile var envMod = 45f       // 0..100
    @Volatile var detune = 14f       // 0..50 cents
    @Volatile var atk = 0.015f       // 0.005..1.5 s
    @Volatile var dec = 0.32f        // 0.04..2
    @Volatile var sus = 0.65f        // 0..1
    @Volatile var rel = 0.45f        // 0.02..3
    @Volatile var osc2Mix = 50f      // 0..100
    @Volatile var chorus = 35f       // 0..100
    @Volatile var delay = 30f        // 0..85
    @Volatile var gain = 80f         // 0..100

    companion object {
        val CHORDS = listOf(
            Chord("OFF", intArrayOf(0)), Chord("MAJ", intArrayOf(0, 4, 7)), Chord("MIN", intArrayOf(0, 3, 7)),
            Chord("VAPOR 9", intArrayOf(0, 3, 7, 10, 14)), Chord("5TH PWR", intArrayOf(0, 7, 12)),
        )
        val ARPS = listOf("OFF", "UP", "DOWN", "UP/DN", "RAND")
        fun noteName(n: Int): String = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")[n.mod(12)] + (n / 12 - 1)
    }
}

private class SynthVoice(val note: Int, val p: SynthParams, val sr: Double) {
    val freq = 440.0 * 2.0.pow((note - 69) / 12.0)
    var ph1 = Random.nextDouble(); var ph2 = Random.nextDouble()
    var t = 0.0
    var released = false; var relT = 0.0; var relFrom = 0.0; var relDur = 0.0
    val filt = Svf()
    var amp = 0.0

    fun release(fast: Boolean) {
        if (released) return
        released = true; relT = t; relFrom = max(1e-4, amp); relDur = if (fast) 0.02 else max(0.02, p.rel.toDouble())
    }
    val done get() = released && t - relT > relDur + 0.01

    private fun polyBlep(t: Double, dt: Double): Double = when {
        t < dt -> { val x = t / dt; x + x - x * x - 1 }
        t > 1 - dt -> { val x = (t - 1) / dt; x * x + x + x + 1 }
        else -> 0.0
    }

    private fun osc(w: Wave, ph: Double, dt: Double): Double = when (w) {
        Wave.SAW -> 2 * ph - 1 - polyBlep(ph, dt)
        Wave.SQR -> {
            var v = if (ph < 0.5) 1.0 else -1.0
            v += polyBlep(ph, dt); v -= polyBlep((ph + 0.5) % 1.0, dt); v
        }
        Wave.TRI -> 4 * abs(ph - 0.5) - 1
        Wave.SIN -> sin(2 * PI * ph)
    }

    fun next(): Double {
        val atk = max(0.005, p.atk.toDouble()); val dec = p.dec.toDouble(); val sus = p.sus.toDouble()
        val f2 = freq * 0.5 * 2.0.pow(p.detune / 1200.0)
        val dt1 = freq / sr; val dt2 = f2 / sr
        val s = osc(p.wave1, ph1, dt1) * 0.65 + osc(p.wave2, ph2, dt2) * (p.osc2Mix / 100.0 * 0.65)
        ph1 = (ph1 + dt1) % 1.0; ph2 = (ph2 + dt2) % 1.0
        // filter envelope: cutoff -> peak over attack, then toward sustain level over decay
        val base = max(40.0, p.cutoff.toDouble())
        val peak = min(18000.0, base + p.envMod / 100.0 * 7500)
        val target = max(50.0, base + (peak - base) * sus * 0.5)
        val fc = when {
            t < atk -> expRamp(base, max(60.0, peak), t, atk)
            t < atk + dec -> expRamp(max(60.0, peak), target, t - atk, dec)
            else -> target
        }
        filt.process(s, fc, p.res.toDouble(), sr)
        val y = when (p.filterMode) { FilterMode.LPF -> filt.low; FilterMode.BPF -> filt.band; FilterMode.HPF -> filt.high }
        amp = if (released) expRamp(relFrom, 1e-4, t - relT, relDur) else when {
            t < atk -> 0.45 * t / atk
            t < atk + dec -> 0.45 + (0.45 * sus - 0.45) * (t - atk) / dec
            else -> max(0.001, 0.45 * sus)
        }
        t += 1.0 / sr
        return y * amp
    }
}

class PolySynth(private val sr: Int, val p: SynthParams, private val bpmSource: () -> Int) {
    private val voices = ArrayList<SynthVoice>()
    private val events = java.util.concurrent.ConcurrentLinkedQueue<Pair<Int, Boolean>>()
    /** Notes currently held by fingers or HOLD (UI reads this). */
    val held: MutableSet<Int> = java.util.Collections.newSetFromMap(java.util.concurrent.ConcurrentHashMap<Int, Boolean>())
    @Volatile var activeCount = 0
        private set
    private var panicRequested = false

    private val chorusDl = FloatArray(sr / 10); private var chorusW = 0
    private val echo = FloatArray(sr); private var echoW = 0
    private var lfo = 0.0
    private var arpCounter = 0.0; private var arpStep = 0; private var arpNote = -1; private var arpOffAt = 0.0

    /** Finger/keyboard press of a root note: expands chords like the website. */
    fun press(root: Int, down: Boolean) {
        for (i in SynthParams.CHORDS[p.chordIdx].intervals) {
            val n = root + i
            if (down) { held.add(n); if (p.arpIdx == 0) events.add(n to true) }
            else if (!p.hold) { held.remove(n); if (p.arpIdx == 0) events.add(n to false) }
        }
    }

    fun panic(releaseHold: Boolean = true) {
        if (releaseHold) p.hold = false
        held.clear(); panicRequested = true
    }

    private fun noteOn(n: Int) {
        voices.filter { it.note == n && !it.released }.forEach { it.release(true) }
        val live = voices.filter { !it.released }
        if (live.size >= 8) live.first().release(true)
        voices.add(SynthVoice(n, p, sr.toDouble()))
    }

    private fun noteOff(n: Int, fast: Boolean = false) { voices.filter { it.note == n }.forEach { it.release(fast) } }

    fun render(out: FloatArray, frames: Int) {
        if (panicRequested) { panicRequested = false; voices.forEach { it.release(true) } }
        while (true) {
            val e = events.poll() ?: break
            if (e.second) noteOn(e.first) else noteOff(e.first)
        }
        val gain = p.gain / 100.0 * 0.45
        val chorusDepth = p.chorus / 100.0 * 0.0045 * sr
        val echoWet = p.delay / 100.0 * 0.55
        val echoLen = (0.31 * sr).toInt()
        val step = 60.0 / bpmSource() / 4 * sr
        for (f in 0 until frames) {
            // arpeggiator, synced to the 909 tempo
            if (p.arpIdx > 0) {
                arpCounter -= 1.0
                if (arpNote >= 0 && arpOffAt > 0) { arpOffAt -= 1.0; if (arpOffAt <= 0) { noteOff(arpNote); arpNote = -1 } }
                if (arpCounter <= 0) {
                    val notes = held.sorted()
                    if (notes.isNotEmpty()) {
                        val n = when (SynthParams.ARPS[p.arpIdx]) {
                            "UP" -> notes[arpStep % notes.size]
                            "DOWN" -> notes[notes.size - 1 - arpStep % notes.size]
                            "UP/DN" -> { val seq = notes + notes.drop(1).dropLast(1).reversed(); seq[arpStep % seq.size] }
                            else -> notes[Random.nextInt(notes.size)]
                        }
                        arpStep++
                        if (arpNote >= 0) noteOff(arpNote)
                        noteOn(n); arpNote = n; arpOffAt = max(0.045 * sr, step * 0.72)
                    }
                    arpCounter = max(0.055 * sr, step)
                }
            }
            var s = 0.0
            var i = 0
            while (i < voices.size) {
                val v = voices[i]
                if (v.done) { voices.removeAt(i); continue }
                s += v.next(); i++
            }
            val dry = s * gain
            // chorus: 18 ms delay swept by a 1.6 Hz LFO
            chorusDl[chorusW] = dry.toFloat()
            lfo += 1.6 / sr
            val d = 0.018 * sr + sin(2 * PI * lfo) * chorusDepth
            val rp = (chorusW - d + chorusDl.size) % chorusDl.size
            val i0 = rp.toInt() % chorusDl.size
            val ch = chorusDl[i0] + (chorusDl[(i0 + 1) % chorusDl.size] - chorusDl[i0]) * (rp - floor(rp))
            chorusW = (chorusW + 1) % chorusDl.size
            // vapor echo
            val er = echo[(echoW - echoLen + echo.size) % echo.size]
            echo[echoW] = (dry + er * 0.38).toFloat()
            echoW = (echoW + 1) % echo.size
            val l = dry + ch + er * echoWet
            val r = dry + ch * 0.8 + er * echoWet * 1.1
            out[f * 2] += l.toFloat(); out[f * 2 + 1] += r.toFloat()
        }
        activeCount = voices.count { !it.released }
    }

    fun hasSound(): Boolean = voices.isNotEmpty()
}

/** Both instruments mixed, with their own analyser so the visualisers react to them. */
class StudioCore(val sr: Int) {
    val drums = Drum909(sr)
    val synthParams = SynthParams()
    val synth = PolySynth(sr, synthParams) { drums.bpm }
    val analyzer = SpectrumAnalyzer()
    private var pcm = ShortArray(0)

    init { analyzer.configure(sr) }

    fun render(out: FloatArray, frames: Int) {
        java.util.Arrays.fill(out, 0, frames * 2, 0f)
        drums.render(out, frames)
        synth.render(out, frames)
        if (pcm.size < frames * 2) pcm = ShortArray(frames * 2)
        for (i in 0 until frames * 2) {
            val v = out[i].coerceIn(-1f, 1f)
            out[i] = v
            pcm[i] = (v * 32767).toInt().toShort()
        }
        analyzer.feed(pcm, frames, 2)
    }

    fun busy(): Boolean = drums.playing || drums.roll || synth.hasSound() || synth.held.isNotEmpty()
}
