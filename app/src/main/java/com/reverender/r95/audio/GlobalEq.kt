package com.reverender.r95.audio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.media.audiofx.DynamicsProcessing
import android.os.Build
import com.reverender.r95.core.Catalog
import com.reverender.r95.os.OsPrefs

/**
 * GRAPHIC_EQ for the whole phone: the 8 bands, the preamp and DSP_FX_95's Sub Excite on Android's
 * DynamicsProcessing effect (Android 9+).
 *  - Attached to the global output mix (session 0) where the phone allows it.
 *  - Also attached to every app that announces its audio session (Spotify, YouTube Music, most players
 *    send ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION), which works even where session 0 is locked.
 * A limiter after the EQ keeps boosts from clipping. While it is on, VAPOR_AMP's own EQ steps aside
 * so its music is not equalised twice.
 */
object GlobalEq {
    val supported = Build.VERSION.SDK_INT >= 28
    private val effects = HashMap<Int, Any>() // session -> DynamicsProcessing
    @Volatile var globalActive = false; private set
    var status = "OFF"; private set
    private var lastEq = -1; private var lastFx = -1
    private val main = android.os.Handler(android.os.Looper.getMainLooper())
    private val poll = object : Runnable { override fun run() { sync(); if (effects.isNotEmpty()) main.postDelayed(this, 150) } }

    private fun on() = runCatching { OsPrefs.globalEq }.getOrDefault(false) && supported

    fun refresh() {
        if (!on()) { effects.values.forEach { runCatching { (it as AudioEffect).release() } }; effects.clear(); globalActive = false; status = if (supported) "OFF" else "NEEDS ANDROID 9"; return }
        if (!effects.containsKey(0)) attach(0)
        globalActive = effects.containsKey(0)
        updateStatus()
        lastEq = -1; lastFx = -1; sync()
        main.removeCallbacks(poll); main.post(poll)
    }

    private fun updateStatus() {
        val apps = effects.keys.count { it != 0 }
        status = when {
            globalActive && apps > 0 -> "ON: ALL PHONE AUDIO + $apps APP SESSION(S)"
            globalActive -> "ON: ALL PHONE AUDIO"
            apps > 0 -> "ON: $apps APP SESSION(S)"
            else -> "WAITING FOR AN APP TO OPEN ITS AUDIO SESSION"
        }
    }

    /** From the effect-session broadcasts of other players. */
    fun onSession(session: Int, open: Boolean) {
        if (session <= 0) return
        if (open) { if (on() && !effects.containsKey(session)) { attach(session); lastEq = -1; sync(); main.removeCallbacks(poll); main.post(poll) } }
        else effects.remove(session)?.let { runCatching { (it as AudioEffect).release() } }
        updateStatus()
    }

    private fun attach(session: Int) {
        if (Build.VERSION.SDK_INT < 28) return
        val n = Catalog.eqFrequencies.size
        val cfg = DynamicsProcessing.Config.Builder(DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION, 2, true, n, false, 0, true, 2, true).build()
        val dp = runCatching { DynamicsProcessing(Int.MAX_VALUE, session, cfg) }.getOrNull() ?: return
        runCatching {
            val fr = Catalog.eqFrequencies
            for (ch in 0 until 2) {
                for (b in 0 until n) {
                    // each band runs up to the midpoint (geometric) between its centre and the next one
                    val cut = if (b < n - 1) kotlin.math.sqrt(fr[b] * fr[b + 1]) else 20000f
                    dp.setPreEqBandByChannelIndex(ch, b, DynamicsProcessing.EqBand(true, cut, 0f))
                }
                dp.setPostEqBandByChannelIndex(ch, 0, DynamicsProcessing.EqBand(true, 90f, 0f))
                dp.setPostEqBandByChannelIndex(ch, 1, DynamicsProcessing.EqBand(true, 20000f, 0f))
                dp.setLimiterByChannelIndex(ch, DynamicsProcessing.Limiter(true, true, 0, 1f, 60f, 10f, -1f, 0f))
            }
            dp.enabled = true
            effects[session] = dp
        }.onFailure { runCatching { dp.release() } }
    }

    /** Pushes the EQ sliders, the preamp and Sub Excite into every attached effect when they change. */
    fun sync() {
        if (Build.VERSION.SDK_INT < 28 || effects.isEmpty()) return
        val eq = AudioBus.eq; val fx = AudioBus.fx
        if (eq.version == lastEq && fx.version == lastFx) return
        lastEq = eq.version; lastFx = fx.version
        val pre = if (eq.enabled) eq.preampDb else 0f
        val dead = ArrayList<Int>()
        for ((s, e) in effects) {
            val dp = e as DynamicsProcessing
            val ok = runCatching {
                if (!dp.hasControl()) error("lost")
                for (ch in 0 until 2) {
                    for (b in eq.gains.indices) {
                        val band = dp.getPreEqBandByChannelIndex(ch, b)
                        band.gain = (if (eq.enabled) eq.gains[b] else 0f) + pre
                        dp.setPreEqBandByChannelIndex(ch, b, band)
                    }
                    val sub = dp.getPostEqBandByChannelIndex(ch, 0)
                    sub.gain = fx.subDb
                    dp.setPostEqBandByChannelIndex(ch, 0, sub)
                }
            }.isSuccess
            if (!ok) dead.add(s)
        }
        dead.forEach { s -> effects.remove(s)?.let { runCatching { (it as AudioEffect).release() } } }
        if (dead.contains(0)) globalActive = false
        if (dead.isNotEmpty()) updateStatus()
    }
}

/** ACTION_OPEN / CLOSE_AUDIO_EFFECT_CONTROL_SESSION from other music apps. */
class EffectSessionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        OsPrefs.init(context)
        val session = intent.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, -1)
        when (intent.action) {
            AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION -> GlobalEq.onSession(session, true)
            AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION -> GlobalEq.onSession(session, false)
        }
    }
}
