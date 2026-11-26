package com.reverender.r95.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.first

/**
 * Whether the app is on screen. In the background nothing is drawn or polled (battery);
 * music keeps playing. Everything resumes when the app comes back.
 */
object AppVisibility {
    @Volatile var foreground = false // the activity's onStart switches it on; services alone stay "background"
        private set
    var visible by mutableStateOf(true)
        private set

    /**
     * The Win95 / Longhorn desktop is the active style (false while the VHS shell runs): desktop-only
     * things (wallpaper thread, plasma tilt sensor, Backrooms hum) only run while it's true.
     */
    @Volatile var desktopActive = false
        private set
    fun setDesktop(on: Boolean) { desktopActive = on; runCatching { com.reverender.r95.os.OsBoot.visibilityChanged() } }

    fun set(fg: Boolean) { foreground = fg; visible = fg; runCatching { com.reverender.r95.os.OsBoot.visibilityChanged() } }

    /** Suspends while the app is in the background. */
    suspend fun awaitForeground() { if (!foreground) snapshotFlow { visible }.first { it } }
}
