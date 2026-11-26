package com.reverender.r95.os

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.dreams.DreamService
import com.reverender.r95.audio.AudioBus
import com.reverender.r95.audio.SystemVisualizer

/**
 * CRT dock screensaver: Android starts it by itself while the phone is charging or docked on a stand
 * (Settings → Display → Screen saver → "When to start"). Shows ANALOG_VU, LISSAJOUS, the WIN95 maze
 * or a wallpaper world in the chosen colour theme, driven by whatever is playing.
 */
class R95Dream : DreamService() {
    private var listening = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        OsPrefs.init(this)
        isInteractive = false
        isFullscreen = true
        isScreenBright = true
        setContentView(SaverView(this, OsPrefs.dreamSaver, OsPrefs.dreamTheme))
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        if (!listening) { listening = true; AudioBus.backgroundListeners.incrementAndGet(); SystemVisualizer.acquire() }
    }

    override fun onDreamingStopped() {
        if (listening) { listening = false; AudioBus.backgroundListeners.decrementAndGet(); SystemVisualizer.release() }
        super.onDreamingStopped()
    }

    companion object {
        fun openSettings(ctx: Context) {
            runCatching { ctx.startActivity(Intent(Settings.ACTION_DREAM_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
                .onFailure { runCatching { ctx.startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } }
        }
    }
}
