package com.reverender.r95.os

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Mechanical tape-deck feel: crisp clunks on the transport keys, a soft motor hum while
 * rewinding / fast-forwarding, and short thumps on the bass-drum glitch.
 */
object Haptics {
    private val vib: Vibrator? by lazy {
        runCatching {
            if (Build.VERSION.SDK_INT >= 31) (OsPrefs.app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            else @Suppress("DEPRECATION") (OsPrefs.app.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
        }.getOrNull()?.takeIf { it.hasVibrator() }
    }
    private val amp: Boolean get() = Build.VERSION.SDK_INT >= 26 && vib?.hasAmplitudeControl() == true
    private var humming = false
    private var lastThump = 0L

    private fun ok() = runCatching { OsPrefs.haptics }.getOrDefault(false) && vib != null

    private fun oneShot(ms: Long, a: Int) {
        val v = vib ?: return
        runCatching {
            if (Build.VERSION.SDK_INT >= 26) v.vibrate(VibrationEffect.createOneShot(ms, if (amp) a.coerceIn(1, 255) else VibrationEffect.DEFAULT_AMPLITUDE))
            else @Suppress("DEPRECATION") v.vibrate(ms)
        }
    }

    private fun wave(t: LongArray, a: IntArray, repeat: Int) {
        val v = vib ?: return
        runCatching {
            if (Build.VERSION.SDK_INT >= 26) v.vibrate(if (amp) VibrationEffect.createWaveform(t, a, repeat) else VibrationEffect.createWaveform(t, repeat))
            else @Suppress("DEPRECATION") v.vibrate(t, repeat)
        }
    }

    /** Play / Stop / Pause / Eject: a two-stage key clunk (latch, then the mechanism landing). */
    fun clunk(kind: String) {
        if (!ok()) return
        stopHum()
        val v = vib ?: return
        if (Build.VERSION.SDK_INT >= 30 && kind != "eject") {
            val c = runCatching {
                VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, if (kind == "stop") 1f else 0.85f)
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, if (kind == "play") 0.7f else 0.5f, 28)
                    .compose()
            }.getOrNull()
            if (c != null && runCatching { v.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_CLICK, VibrationEffect.Composition.PRIMITIVE_THUD) }.getOrDefault(false)) {
                runCatching { v.vibrate(c) }; return
            }
        }
        when (kind) {
            "eject" -> wave(longArrayOf(0, 14, 40, 26, 30, 50), intArrayOf(0, 255, 0, 180, 0, 90), -1)
            "stop" -> wave(longArrayOf(0, 12, 22, 18), intArrayOf(0, 255, 0, 140), -1)
            "pause" -> oneShot(14, 170)
            else -> wave(longArrayOf(0, 10, 26, 22), intArrayOf(0, 230, 0, 160), -1)
        }
    }

    /** Light tick for ordinary buttons. */
    fun tick() { if (ok()) oneShot(8, 90) }

    /** Rewind / fast-forward motor: a faint, fast ripple until [stopHum]. */
    fun hum(ff: Boolean) {
        if (!ok()) return
        humming = true
        if (amp) wave(longArrayOf(0, 18, 12, 18, 12), intArrayOf(0, if (ff) 46 else 38, 14, if (ff) 52 else 42, 14), 1)
        else wave(longArrayOf(0, 6, 40), intArrayOf(0, 255, 0), 1)
    }

    fun stopHum() { if (humming) { humming = false; runCatching { vib?.cancel() } } }

    /** Bass-drum glitch: a short thump, at most ~6 per second. */
    fun thump(strength: Double) {
        if (!runCatching { OsPrefs.bassHaptics }.getOrDefault(false) || vib == null) return
        val now = System.currentTimeMillis()
        if (now - lastThump < 160) return
        lastThump = now
        oneShot(22, (80 + strength.coerceIn(0.0, 1.2) * 140).toInt())
    }
}
