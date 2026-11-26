package com.reverender.r95.audio

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.random.Random

/**
 * DSP_FX_95's 18-unit rack and CASSETTE_95's tape model, ported from the website's
 * Web Audio graph (updateDspFx / updateCassetteDsp). Pure Kotlin, stereo float.
 *
 * Values use the website's own slider ranges, so presets copy across 1:1.
 */
class FxSettings {
    @Volatile var lpf = 20000f        // 250..20000 Hz
    @Volatile var hpf = 20f           // 20..3500 Hz
    @Volatile var echoTime = 0.28f    // 0.05..0.85 s
    @Volatile var echoMix = 0f        // 0..0.85
    @Volatile var echoFeedback = 0.38f // 0..0.88
    @Volatile var drive = 0f          // 0..100
    @Volatile var subDb = 0f          // 0..15
    @Volatile var chorus = 0f         // 0..100
    @Volatile var tremolo = 0f        // 0..100
    @Volatile var ring = 0f           // 0..100
    @Volatile var pan = 0f            // -1..1
    @Volatile var reverb = 0f         // 0..100
    @Volatile var crush = 0f          // 0..100
    @Volatile var phaser = 0f         // 0..100
    @Volatile var flanger = 0f        // 0..100
    @Volatile var width = 0f          // 0..100
    @Volatile var comp = 0f           // 0..100
    // CASSETTE_95
    @Volatile var tapeOn = false
    @Volatile var wow = 12f           // 0..100
    @Volatile var hiss = 8f           // 0..100
    @Volatile var bias = 25f          // 0..100
    @Volatile var hissActive = false  // only hiss while something plays
    @Volatile var version = 0
    // Gyro tape warp: the phone's rotation rate (rad/s, signed) and shake (0..1) bend the tape like a hand on the reel
    @Volatile var gyroOn = false
    @Volatile var gyroRate = 0f
    @Volatile var gyroShake = 0f
    @Volatile var gyroDepth = 0.6f
    // VHS theme TRACKING knob: 0 = aligned, 1 = far out (wow & flutter, hiss, dull top end)
    @Volatile var vhsTrack = 0f

    fun touch() { version++ }

    fun anyActive(): Boolean =
        lpf < 19990f || hpf > 21f || echoMix > 0.001f || drive > 0.5f || subDb > 0.05f || chorus > 0.5f ||
            tremolo > 0.5f || ring > 0.5f || abs(pan) > 0.01f || reverb > 0.5f || crush > 1f || phaser > 0.5f ||
            flanger > 0.5f || width > 0.5f || comp > 0.5f || tapeOn || gyroOn || vhsTrack > 0.02f

    /** 0..100 "modulation" figure shown on the rack, same formula as the site. */
    fun matrixPercent(): Int =
        round((chorus + tremolo + ring + echoMix * 100 + drive + reverb + crush + phaser + flanger + width + comp) / 11f).toInt()

    fun resetFx() {
        lpf = 20000f; hpf = 20f; echoTime = 0.28f; echoMix = 0f; echoFeedback = 0.35f; drive = 0f; subDb = 0f
        chorus = 0f; tremolo = 0f; ring = 0f; pan = 0f; reverb = 0f; crush = 0f; phaser = 0f; flanger = 0f; width = 0f; comp = 0f
    }

    companion object {
        val PRESETS = listOf("clean" to "Bypass", "slowed" to "Slow+Rev", "mall" to "Mall PA", "nightcore" to "Nightcore",
            "vhs" to "VHS Dub", "cyberbot" to "CyberBot", "dreamhall" to "DreamHall", "arcade8bit" to "8-Bit Crush")

        /** Returns the tape speed for the preset (the rack's TAPE SPEED slider). */
        fun applyPreset(s: FxSettings, name: String): Float {
            s.resetFx()
            var speed = 1f
            when (name) {
                "slowed" -> { speed = .82f; s.lpf = 3600f; s.hpf = 30f; s.echoTime = .38f; s.echoMix = .38f; s.echoFeedback = .52f; s.drive = 8f; s.subDb = 6.5f; s.chorus = 35f; s.reverb = 62f; s.width = 45f; s.comp = 30f }
                "mall" -> { speed = .92f; s.lpf = 2200f; s.hpf = 240f; s.echoTime = .46f; s.echoMix = .55f; s.echoFeedback = .65f; s.drive = 12f; s.subDb = 2f; s.chorus = 45f; s.reverb = 78f; s.width = 65f }
                "nightcore" -> { speed = 1.26f; s.lpf = 19000f; s.hpf = 60f; s.echoTime = .18f; s.echoMix = .2f; s.echoFeedback = .4f; s.drive = 18f; s.subDb = 5.5f; s.phaser = 35f; s.width = 50f; s.comp = 65f }
                "vhs" -> { speed = .95f; s.lpf = 2900f; s.hpf = 110f; s.echoTime = .33f; s.echoMix = .34f; s.echoFeedback = .58f; s.drive = 26f; s.subDb = 4.5f; s.chorus = 75f; s.tremolo = 28f; s.flanger = 42f; s.comp = 45f }
                "cyberbot" -> { speed = 1.04f; s.lpf = 8500f; s.hpf = 160f; s.echoTime = .14f; s.echoMix = .32f; s.echoFeedback = .6f; s.drive = 38f; s.subDb = 7.5f; s.ring = 52f; s.flanger = 68f; s.crush = 35f; s.comp = 55f }
                "dreamhall" -> { speed = .88f; s.lpf = 6200f; s.echoTime = .42f; s.echoMix = .45f; s.echoFeedback = .64f; s.subDb = 5f; s.chorus = 55f; s.reverb = 88f; s.phaser = 58f; s.width = 85f }
                "arcade8bit" -> { speed = 1.06f; s.lpf = 5400f; s.hpf = 140f; s.echoTime = .16f; s.echoMix = .25f; s.drive = 32f; s.crush = 76f; s.phaser = 48f; s.flanger = 50f; s.comp = 70f }
            }
            s.touch()
            return speed
        }

        /** CASSETTE_95 tape formulations: wow, hiss, bias. */
        val TAPES = listOf("type1" to "Type I Ferric", "type2" to "Type II CrO2", "type4" to "Type IV Metal", "worn" to "Warped 1984")
        fun applyTape(s: FxSettings, type: String) {
            when (type) {
                "type1" -> { s.wow = 14f; s.hiss = 14f; s.bias = 38f }
                "type2" -> { s.wow = 8f; s.hiss = 6f; s.bias = 18f }
                "type4" -> { s.wow = 4f; s.hiss = 3f; s.bias = 8f }
                "worn" -> { s.wow = 76f; s.hiss = 38f; s.bias = 82f }
            }
            s.touch()
        }
    }
}

/** Topology-preserving-transform state-variable filter: stable under fast modulation. */
class Svf {
    private var ic1 = 0.0; private var ic2 = 0.0
    var low = 0.0; var band = 0.0; var high = 0.0
    fun process(x: Double, cutoff: Double, q: Double, sr: Double): Double {
        val g = tan(PI * min(cutoff, sr * 0.45) / sr)
        val k = 1.0 / max(0.05, q)
        val a1 = 1.0 / (1.0 + g * (g + k)); val a2 = g * a1; val a3 = g * a2
        val v3 = x - ic2
        val v1 = a1 * ic1 + a2 * v3
        val v2 = ic2 + a2 * ic1 + a3 * v3
        ic1 = 2 * v1 - ic1; ic2 = 2 * v2 - ic2
        low = v2; band = v1; high = x - k * v1 - v2
        return low
    }
    fun reset() { ic1 = 0.0; ic2 = 0.0 }
}

private class DelayLine(maxSamples: Int) {
    private val buf = FloatArray(maxSamples + 4)
    private var w = 0
    fun write(x: Float) { buf[w] = x; w = (w + 1) % buf.size }
    /** Fractional read [delay] samples behind the write head. */
    fun read(delay: Float): Float {
        val d = delay.coerceIn(1f, (buf.size - 3).toFloat())
        val pos = w - d
        var i = kotlin.math.floor(pos).toInt()
        val f = pos - i
        i = ((i % buf.size) + buf.size) % buf.size
        val a = buf[i]; val b = buf[(i + 1) % buf.size]
        return a + (b - a) * f
    }
    fun clear() = buf.fill(0f)
}

private class Comb(size: Int) {
    private val buf = FloatArray(size); private var i = 0; private var store = 0f
    fun process(x: Float, fb: Float, damp: Float): Float {
        val y = buf[i]
        store = y * (1 - damp) + store * damp
        buf[i] = x + store * fb
        i = (i + 1) % buf.size
        return y
    }
    fun clear() { buf.fill(0f); store = 0f }
}

private class AllPass(size: Int) {
    private val buf = FloatArray(size); private var i = 0
    fun process(x: Float): Float {
        val b = buf[i]; val y = -x + b
        buf[i] = x + b * 0.5f
        i = (i + 1) % buf.size
        return y
    }
    fun clear() = buf.fill(0f)
}

/** Small Freeverb-style hall per channel. */
private class Hall(sr: Int, spread: Int) {
    private val scale = sr / 44100f
    private val combs = intArrayOf(1116, 1188, 1277, 1356, 1422, 1491).map { Comb(((it + spread) * scale).toInt()) }
    private val aps = intArrayOf(556, 441, 341).map { AllPass(((it + spread) * scale).toInt()) }
    fun process(x: Float): Float {
        var s = 0f
        for (c in combs) s += c.process(x * 0.015f, 0.86f, 0.25f)
        for (a in aps) s = a.process(s)
        return s
    }
    fun clear() { combs.forEach { it.clear() }; aps.forEach { it.clear() } }
}

class FxChain(private val sr: Int) {
    private val srd = sr.toDouble()
    private var applied = -1

    // Filters (per channel)
    private val sub = arrayOf(Biquad(1), Biquad(1))
    private val hp = arrayOf(Biquad(1), Biquad(1))
    private val lp = arrayOf(Biquad(1), Biquad(1))
    private val headBump = arrayOf(Biquad(1), Biquad(1))
    private val warmth = arrayOf(Biquad(1), Biquad(1))
    private var useSub = false; private var useHp = false; private var useLp = false; private var useTape = false

    private val chorusL = DelayLine(sr / 10); private val chorusR = DelayLine(sr / 10)
    private val warpL = DelayLine(sr / 8); private val warpR = DelayLine(sr / 8)
    private var warpPos = 0.0; private var warpVel = 0.0; private var shakePh = 0.0; private var shakeAmt = 0.0
    private val flangeL = DelayLine(sr / 50); private val flangeR = DelayLine(sr / 50)
    private val echoL = DelayLine(sr); private val echoR = DelayLine(sr)
    private val hallL = Hall(sr, 0); private val hallR = Hall(sr, 23)
    private val phaseL = Array(4) { DoubleArray(2) }; private val phaseR = Array(4) { DoubleArray(2) }

    private var t = 0L
    private var compEnv = 0.0
    private var pink = FloatArray(7)
    private var flangeFbL = 0f; private var flangeFbR = 0f

    // cached derived parameters
    private var driveK = 0f; private var crushLevels = 0
    private var chorusWet = 0f; private var chorusDepth = 0f
    private var flWet = 0f; private var flDepth = 0f; private var flFb = 0f
    private var phWet = 0f; private var phDepth = 0f
    private var tremDepth = 0f; private var ringWet = 0f; private var ringHz = 0f
    private var echoWet = 0f; private var echoFb = 0f; private var echoSamples = 0f
    private var revWet = 0f; private var widthAmt = 0f
    private var compThresh = 0.0; private var compRatio = 1.0; private var compOn = false
    private var panL = 1f; private var panR = 1f; private var hissGain = 0f

    private fun sync(s: FxSettings) {
        if (s.version == applied) return
        applied = s.version
        useSub = s.subDb > 0.05f
        if (useSub) sub.forEach { it.set(Biquad.Type.LOW_SHELF, 58.0, 0.707, s.subDb.toDouble(), sr) }
        useHp = s.hpf > 21f
        if (useHp) hp.forEach { it.setPass(false, s.hpf.toDouble(), sr) }
        val trk = s.vhsTrack.coerceIn(0f, 1f)
        val lpf = if (trk > 0.02f) minOf(s.lpf, 19500f - trk * trk * 15500f) else s.lpf
        useLp = lpf < 19990f
        if (useLp) lp.forEach { it.setPass(true, lpf.toDouble(), sr) }
        useTape = s.tapeOn
        if (useTape) {
            headBump.forEach { it.set(Biquad.Type.PEAK, 95.0, 1.1, s.bias / 100.0 * 9.5, sr) }
            warmth.forEach { it.setPass(true, max(1800.0, 19500.0 - s.bias / 100.0 * 15500.0), sr) }
        }
        driveK = s.drive * 2.8f
        crushLevels = if (s.crush <= 1f) 0 else max(2, round(48.0 * (1 - s.crush / 104.0).pow(2.6)).toInt())
        val w = min(100f, s.chorus + (if (s.tapeOn) s.wow * 0.65f else 0f) + trk * 85f)
        chorusWet = w / 100f * 0.8f; chorusDepth = w / 100f * 0.0072f * sr
        flWet = s.flanger / 100f * 0.85f; flDepth = s.flanger / 100f * 0.0038f * sr; flFb = s.flanger / 100f * 0.78f
        phWet = s.phaser / 100f * 0.95f; phDepth = s.phaser / 100f * 1400f
        tremDepth = s.tremolo / 100f * 0.65f
        ringWet = s.ring / 100f * 0.75f; ringHz = 55f + s.ring / 100f * 320f
        echoWet = s.echoMix; echoFb = s.echoFeedback; echoSamples = s.echoTime * sr
        revWet = s.reverb / 100f * 1.35f
        widthAmt = s.width / 100f * 0.95f
        compOn = s.comp > 0.5f
        compThresh = -8.0 - s.comp / 100.0 * 34.0; compRatio = 1.5 + s.comp / 100.0 * 12.5
        val p = (s.pan.coerceIn(-1f, 1f) + 1f) * (PI / 4).toFloat()
        panL = cos(p) * 1.4142f; panR = sin(p) * 1.4142f
        hissGain = (if (s.tapeOn) s.hiss / 100f * 0.14f else 0f) + trk * trk * 0.12f
    }

    fun reset() {
        listOf(sub, hp, lp, headBump, warmth).forEach { arr -> arr.forEach { it.reset() } }
        chorusL.clear(); chorusR.clear(); flangeL.clear(); flangeR.clear(); echoL.clear(); echoR.clear()
        hallL.clear(); hallR.clear(); phaseL.forEach { it.fill(0.0) }; phaseR.forEach { it.fill(0.0) }
        compEnv = 0.0; flangeFbL = 0f; flangeFbR = 0f
    }

    private fun shape(x: Float): Float {
        var y = x
        if (driveK > 0f) y = ((3 + driveK) * y * 20f * (PI.toFloat() / 180f) / (PI.toFloat() + driveK * abs(y)))
        if (crushLevels > 0) y = round(y.coerceIn(-1f, 1f) * crushLevels) / crushLevels
        return y
    }

    private fun allpassChain(x: Double, st: Array<DoubleArray>, fc: Double): Double {
        val tn = tan(PI * min(fc, srd * 0.45) / srd)
        val a = (tn - 1) / (tn + 1)
        var y = x
        for (s in st) {
            val out = a * y + s[0] - a * s[1]
            s[0] = y; s[1] = out
            y = out
        }
        return y
    }

    /** Processes interleaved stereo float frames in place. */
    fun process(buf: FloatArray, frames: Int, s: FxSettings) {
        sync(s)
        val hiss = if (s.hissActive) hissGain else 0f
        for (f in 0 until frames) {
            val i = f * 2
            var l = buf[i].toDouble(); var r = buf[i + 1].toDouble()
            if (useSub) { l = sub[0].process(l, 0); r = sub[1].process(r, 0) }
            if (useHp) { l = hp[0].process(l, 0); r = hp[1].process(r, 0) }
            if (useLp) { l = lp[0].process(l, 0); r = lp[1].process(r, 0) }
            if (useTape) {
                l = warmth[0].process(headBump[0].process(l, 0), 0)
                r = warmth[1].process(headBump[1].process(r, 0), 0)
            }
            var fl = shape(l.toFloat()); var fr = shape(r.toFloat())
            val time = t.toDouble() / srd

            if (s.gyroOn) {
                // the read head drifts with the rotation rate (Doppler = up to ±6 % pitch while the phone turns)
                // and springs back to centre; shaking adds a fast 9 Hz flutter
                val target = (s.gyroRate * s.gyroDepth / 4f).coerceIn(-1f, 1f)
                warpVel += (target - warpVel) * 0.0008
                val centre = 0.06 * srd
                if (warpPos == 0.0) warpPos = centre
                warpPos += -warpVel * 0.06 + (centre - warpPos) * 0.00001
                warpPos = warpPos.coerceIn(0.004 * srd, 0.12 * srd)
                shakeAmt += (s.gyroShake * s.gyroDepth - shakeAmt) * 0.0005
                shakePh += 2 * PI * 9.0 / srd
                if (shakePh > 2 * PI) shakePh -= 2 * PI
                val d = warpPos + sin(shakePh) * shakeAmt * 0.0025 * srd
                warpL.write(fl); warpR.write(fr)
                fl = warpL.read(d.toFloat()); fr = warpR.read(d.toFloat())
            }

            if (chorusWet > 0.001f) {
                chorusL.write(fl); chorusR.write(fr)
                val m = (sin(2 * PI * 1.1 * time) * 0.5 + 0.5).toFloat()
                val m2 = (sin(2 * PI * 1.1 * time + 1.7) * 0.5 + 0.5).toFloat()
                fl += chorusL.read(0.012f * sr + m * chorusDepth) * chorusWet
                fr += chorusR.read(0.012f * sr + m2 * chorusDepth) * chorusWet
            }
            if (flWet > 0.001f) {
                val m = (sin(2 * PI * 0.25 * time) * 0.5 + 0.5).toFloat()
                flangeL.write(fl + flangeFbL * flFb); flangeR.write(fr + flangeFbR * flFb)
                flangeFbL = flangeL.read(0.001f * sr + m * flDepth); flangeFbR = flangeR.read(0.001f * sr + (1 - m) * flDepth)
                fl += flangeFbL * flWet; fr += flangeFbR * flWet
            }
            if (phWet > 0.001f) {
                val fc = 700.0 + (sin(2 * PI * 0.35 * time) * 0.5 + 0.5) * phDepth
                fl += (allpassChain(fl.toDouble(), phaseL, fc) * phWet).toFloat()
                fr += (allpassChain(fr.toDouble(), phaseR, fc) * phWet).toFloat()
            }
            if (tremDepth > 0.001f) {
                val g = 1f - tremDepth * (0.5f + 0.5f * sin(2 * PI * 4.5 * time).toFloat())
                fl *= g; fr *= g
            }
            if (ringWet > 0.001f) {
                val c = sin(2 * PI * ringHz * time).toFloat()
                fl = fl * (1 - ringWet) + fl * c * ringWet * 1.6f
                fr = fr * (1 - ringWet) + fr * c * ringWet * 1.6f
            }
            if (echoWet > 0.001f) {
                val el = echoL.read(echoSamples); val er = echoR.read(echoSamples)
                echoL.write(fl + el * echoFb); echoR.write(fr + er * echoFb)
                fl += el * echoWet; fr += er * echoWet
            }
            if (revWet > 0.001f) {
                val inMono = (fl + fr) * 0.5f
                fl += hallL.process(inMono) * revWet
                fr += hallR.process(inMono) * revWet
            }
            if (widthAmt > 0.001f) {
                val mid = (fl + fr) * 0.5f; val side = (fl - fr) * 0.5f * (1 + widthAmt * 1.6f)
                fl = mid + side; fr = mid - side
            }
            if (compOn) {
                val lvl = max(abs(fl), abs(fr)).toDouble()
                val coef = if (lvl > compEnv) 0.003 else 0.00012
                compEnv += (lvl - compEnv) * (1 - exp(-1.0 / (coef * srd)))
                val db = 20 * ln(compEnv + 1e-9) / ln(10.0)
                if (db > compThresh) {
                    val gainDb = (compThresh + (db - compThresh) / compRatio) - db
                    val g = 10.0.pow(gainDb / 20).toFloat()
                    fl *= g; fr *= g
                }
                // gentle make-up gain like Web Audio's compressor
                val makeup = 10.0.pow((-compThresh * (1 - 1 / compRatio)) * 0.25 / 20).toFloat()
                fl *= makeup; fr *= makeup
            }
            fl *= panL; fr *= panR
            if (hiss > 0f) {
                val wht = Random.nextFloat() * 2 - 1
                pink[0] = 0.99886f * pink[0] + wht * 0.0555179f; pink[1] = 0.99332f * pink[1] + wht * 0.0750759f
                pink[2] = 0.969f * pink[2] + wht * 0.153852f; pink[3] = 0.8665f * pink[3] + wht * 0.3104856f
                pink[4] = 0.55f * pink[4] + wht * 0.5329522f; pink[5] = -0.7616f * pink[5] - wht * 0.016898f
                val p = (pink.sum() + wht * 0.5362f) * 0.11f
                pink[6] = wht * 0.115926f
                fl += p * hiss; fr += p * hiss
            }
            buf[i] = fl; buf[i + 1] = fr
            t++
        }
    }
}

/** Butterworth 2nd-order LP/HP on top of the cookbook biquad. */
fun Biquad.setPass(lowPass: Boolean, freq: Double, sampleRate: Int) {
    val f = min(freq, sampleRate * 0.45)
    val w0 = 2 * PI * f / sampleRate
    val cw = cos(w0); val alpha = sin(w0) / (2 * 0.7071)
    val a0 = 1 + alpha
    if (lowPass) setRaw((1 - cw) / 2 / a0, (1 - cw) / a0, (1 - cw) / 2 / a0, -2 * cw / a0, (1 - alpha) / a0)
    else setRaw((1 + cw) / 2 / a0, -(1 + cw) / a0, (1 + cw) / 2 / a0, -2 * cw / a0, (1 - alpha) / a0)
}
