package com.reverender.r95.os

import android.app.Activity
import android.app.Presentation
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.Display
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Dual-screen CRT monitor (Presentation API): with a second display — USB-C/HDMI, or a wireless
 * display that Android offers as a presentation screen — the chosen world or visualizer runs full screen
 * on the TV while the phone stays the DSP_FX_95 rack controller.
 * (A Chromecast that only mirrors the phone shows the phone screen instead.)
 */
object CrtMonitor {
    var displayName by mutableStateOf<String?>(null); private set
    var showing by mutableStateOf(false); private set
    var wanted by mutableStateOf(true)
    private var activity: java.lang.ref.WeakReference<Activity>? = null
    private var pres: Presentation? = null
    private var view: SaverView? = null

    private val listener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(id: Int) = update()
        override fun onDisplayRemoved(id: Int) = update()
        override fun onDisplayChanged(id: Int) = update()
    }

    fun attach(a: Activity) {
        activity = java.lang.ref.WeakReference(a)
        (a.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager).registerDisplayListener(listener, null)
        update()
    }

    fun detach(a: Activity) {
        runCatching { (a.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager).unregisterDisplayListener(listener) }
        hide()
        activity = null
    }

    private fun target(a: Activity): Display? =
        (a.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager).getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION).firstOrNull()

    fun update() {
        val a = activity?.get() ?: return
        val d = target(a)
        displayName = d?.name
        if (d == null || !wanted) { hide(); return }
        if (pres?.display?.displayId == d.displayId) { view?.mode = OsPrefs.monitorScene; return }
        hide()
        val p = object : Presentation(a, d) {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                val v = SaverView(context, OsPrefs.monitorScene, runCatching { a.getSharedPreferences("r95", Context.MODE_PRIVATE).getString("theme", "vaporwave") }.getOrNull() ?: "vaporwave")
                view = v
                setContentView(v)
            }
        }
        if (runCatching { p.show() }.isSuccess) { pres = p; showing = true } else showing = false
    }

    fun setScene(s: String) { OsPrefs.setMonitorScene(s); view?.mode = s; update() }
    fun setTheme(t: String) { view?.themeId = t }

    fun hide() {
        runCatching { pres?.dismiss() }
        pres = null; view = null; showing = false
    }
}
