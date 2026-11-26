package com.reverender.r95.os

import android.content.Context
import android.content.IntentFilter
import android.media.audiofx.AudioEffect
import android.os.Build
import com.reverender.r95.audio.EffectSessionReceiver
import com.reverender.r95.audio.GlobalEq
import com.reverender.r95.audio.SystemVisualizer

/** Starts the Android integrations once per process (from the activity and from the playback service). */
object OsBoot {
    private var done = false
    fun init(ctx: Context) {
        OsPrefs.init(ctx)
        if (done) return
        done = true
        val app = ctx.applicationContext
        // other players' "open audio effect session" broadcasts reach runtime receivers on every Android version
        val f = IntentFilter().apply {
            addAction(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
            addAction(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
        }
        runCatching {
            if (Build.VERSION.SDK_INT >= 33) app.registerReceiver(EffectSessionReceiver(), f, Context.RECEIVER_EXPORTED)
            else app.registerReceiver(EffectSessionReceiver(), f)
        }
        GlobalEq.refresh()
        GyroWarp.refresh()
        SystemVisualizer.refresh()
        Midi.start(app)
    }

    /** App came to the front or went away: sensors and visualizer taps follow. */
    fun visibilityChanged() {
        GyroWarp.refresh()
        SystemVisualizer.refresh()
    }
}
