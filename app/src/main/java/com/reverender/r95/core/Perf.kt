package com.reverender.r95.core

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentCallbacks2
import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Frame pacing and memory adaptation (the app's version of the site's adaptive performance tiers).
 * Nothing is switched off: animations keep running, only their frame rate adapts.
 *  - The window asks the display for 60 Hz (120 Hz panels otherwise double every animation's work).
 *  - Low-memory devices (≤ 2 GB, or Android's low-RAM flag) animate at 30 fps.
 *  - If frames keep arriving late, or Android reports memory pressure, the app drops to 30 fps;
 *    it returns to 60 fps after a quiet stretch with headroom.
 */
object Perf {
    var ramGb = 4.0; private set
    var lowRam = false; private set
    /** 30 fps pacing in force */
    var fps30 by mutableStateOf(false); private set
    private var pressureUntil = 0L

    /** minimum time between animation steps */
    val minStepNs: Long get() = if (fps30) 32_000_000L else 15_500_000L

    /** Phones without much headroom (≤ 6 GB, or already paced at 30 fps). Flagships never throttle. */
    val weak: Boolean get() = lowRam || fps30 || ramGb < 5.8

    /**
     * Touch priority: while a finger is down (dragging, scrolling, tapping) and shortly after,
     * the animated wallpaper behind the windows steps at a lower rate on weak phones,
     * so the UI thread has the time to follow the finger. Windows, visualizers and the cat keep their full rate.
     */
    @Volatile var touching = false; private set
    private val untouch = Runnable { touching = false }
    fun touch(down: Boolean) {
        if (down) { main.removeCallbacks(untouch); touching = true }
        else { main.removeCallbacks(untouch); main.postDelayed(untouch, 350) }
    }
    /**
     * A maximized, fully opaque window covers the whole desktop: the wallpaper, toys and the windows
     * underneath can't be seen, so they stop drawing (the wallpaper keeps its last frame for the edges).
     */
    var desktopCovered by mutableStateOf(false)

    /** step for the desktop wallpaper */
    val bgStepNs: Long get() = when {
        !touching -> minStepNs
        fps30 -> 48_000_000L
        else -> 32_000_000L // every phone: the slow-moving wallpaper at 30 fps while a finger is down
    }

    private var act: java.lang.ref.WeakReference<Activity>? = null
    private var interactive = false
    private val main = android.os.Handler(android.os.Looper.getMainLooper())
    private val relax = Runnable { interactive = false; act?.get()?.let { prefer60Hz(it) } }

    /** While a window is dragged or resized, let the display run at its full rate (smooth on 90/120 Hz panels). */
    fun interactive(on: Boolean) {
        val a = act?.get() ?: return
        if (weak) return // weak phones stay at 60 Hz: a faster panel would only double the work per second
        if (on) {
            main.removeCallbacks(relax)
            if (interactive) return
            interactive = true
            runCatching {
                val attrs = a.window.attributes
                if (Build.VERSION.SDK_INT >= 23) attrs.preferredDisplayModeId = 0
                attrs.preferredRefreshRate = 0f
                a.window.attributes = attrs
            }
        } else { main.removeCallbacks(relax); main.postDelayed(relax, 500) }
    }

    fun install(activity: Activity) {
        act = java.lang.ref.WeakReference(activity)
        val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }
        ramGb = mi.totalMem / 1_073_741_824.0
        lowRam = am.isLowRamDevice || ramGb <= 2.2
        if (lowRam) fps30 = true
        prefer60Hz(activity)
        android.util.Log.i("r95", "perf: ram=%.1f GB lowRam=%s memClass=%d MB".format(ramGb, lowRam, am.memoryClass))
    }

    /** Ask for a 60 Hz display mode at the current resolution (falls back to the refresh-rate hint). */
    private fun prefer60Hz(activity: Activity) {
        runCatching {
            val w = activity.window
            val attrs = w.attributes
            if (Build.VERSION.SDK_INT >= 23) {
                @Suppress("DEPRECATION")
                val display = activity.windowManager.defaultDisplay
                val cur = display.mode
                val m = display.supportedModes.filter { it.physicalWidth == cur.physicalWidth && it.physicalHeight == cur.physicalHeight }
                    .minByOrNull { kotlin.math.abs(it.refreshRate - 60f) }
                if (m != null && m.refreshRate >= 50f) attrs.preferredDisplayModeId = m.modeId
            }
            attrs.preferredRefreshRate = 60f
            w.attributes = attrs
        }
    }

    // ------------------------------------------------------------ runtime frame monitor
    private var last = 0L; private var sum = 0L; private var count = 0; private var slowWindows = 0; private var fastWindows = 0

    /** Called once per paced desktop frame with the frame clock in ns. */
    private val wallAuto: Boolean get() = runCatching { com.reverender.r95.os.OsPrefs.wallScale }.getOrDefault(-1f) < 0f
    private var scaleCalm = 0
    /** automatic choice: slower phones start lower and every phone adapts while running */
    @Volatile var autoWallScale = -1f; private set
    /** resolution the animated wallpaper renders at (1 = full). */
    fun wallScale(): Float {
        val set = runCatching { com.reverender.r95.os.OsPrefs.wallScale }.getOrDefault(-1f)
        if (set > 0f) return set.coerceIn(0.3f, 1f)
        if (autoWallScale < 0f) autoWallScale = when { lowRam -> 0.5f; weak -> 0.75f; else -> 1f }
        return autoWallScale
    }

    fun onFrame(now: Long) {
        if (last == 0L) { last = now; return }
        val dt = now - last; last = now
        if (dt > 250_000_000L) return // resumed from background / paused
        sum += dt; count++
        if (count < 90) return
        val avg = sum / count; sum = 0; count = 0
        val budget = if (fps30) 40_000_000L else 24_000_000L
        // automatic wallpaper resolution: aim for a steady 60 fps by drawing the wallpaper with fewer pixels
        // before giving up and pacing at 30 fps
        if (!fps30 && wallAuto) {
            if (avg > 19_000_000L && autoWallScale > 0.45f) { autoWallScale = (autoWallScale - 0.1f).coerceAtLeast(0.4f); scaleCalm = 0
                android.util.Log.i("r95", "perf: frames ${avg / 1_000_000} ms → wallpaper at ${(autoWallScale * 100).toInt()}%"); return }
            if (avg < 15_500_000L && autoWallScale < 1f && ++scaleCalm >= 4) { autoWallScale = (autoWallScale + 0.05f).coerceAtMost(1f); scaleCalm = 0 }
        }
        if (avg > budget) { slowWindows++; fastWindows = 0 } else { fastWindows++; slowWindows = 0 }
        if (!fps30 && slowWindows >= 2) { fps30 = true; android.util.Log.i("r95", "perf: frames late (avg ${avg / 1_000_000} ms) → 30 fps") }
        if (fps30 && !lowRam && fastWindows >= 8 && System.currentTimeMillis() > pressureUntil) { fps30 = false; slowWindows = 0; android.util.Log.i("r95", "perf: headroom again → 60 fps") }
    }

    /** onTrimMemory: memory pressure → pace at 30 fps for a while and drop caches. */
    fun onTrim(level: Int) {
        @Suppress("DEPRECATION")
        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW && level < ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN || level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            fps30 = true; pressureUntil = System.currentTimeMillis() + 60_000
            android.util.Log.i("r95", "perf: memory pressure (trim $level) → 30 fps")
        }
    }

    // ------------------------------------------------------------ profiling: per-frame cost of each job, logged every 5 s
    private val acc = LongArray(8); private val cnt = IntArray(8)
    val NAMES = arrayOf("tick", "viz", "wall", "walker", "draw", "other", "", "")
    private var logAt = 0L; private var frames = 0; private var gc0 = -1L; private var firstFrame = 0L
    inline fun <T> time(slot: Int, block: () -> T): T { val t0 = System.nanoTime(); try { return block() } finally { add(slot, System.nanoTime() - t0) } }
    fun add(slot: Int, ns: Long) { acc[slot] += ns; cnt[slot]++ }
    /** once per paced desktop frame */
    fun profileFrame() {
        frames++
        val now = System.nanoTime()
        if (logAt == 0L) { logAt = now; firstFrame = now; gc0 = gcCount(); return }
        if (now - logAt < 5_000_000_000L) return
        val secs = (now - logAt) / 1e9
        val gc = gcCount(); val dGc = if (gc0 >= 0 && gc >= 0) gc - gc0 else -1
        val sb = StringBuilder("perf: fps=%.0f".format(frames / secs))
        for (i in NAMES.indices) if (cnt[i] > 0 && NAMES[i].isNotEmpty()) sb.append(" ${NAMES[i]}=%.2fms".format(acc[i] / 1e6 / frames))
        sb.append(" gc=$dGc heap=${(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1_048_576}MB fps30=$fps30")
        android.util.Log.i("r95", sb.toString())
        acc.fill(0); cnt.fill(0); frames = 0; logAt = now; gc0 = gc
    }
    private fun gcCount(): Long = runCatching { android.os.Debug.getRuntimeStat("art.gc.gc-count").toLong() }.getOrDefault(-1L)
}
