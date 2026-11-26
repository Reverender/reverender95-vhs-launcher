package com.reverender.r95.os

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

/**
 * Win95 Home Launcher Mode: REVERENDER_95 can be picked as the phone's home screen (an optional
 * HOME activity-alias that is off until the user switches it on), and the Start menu lists the
 * phone's installed apps to launch them.
 */
object Launcher {
    class PhoneApp(val label: String, val component: ComponentName, val icon: ImageBitmap?)

    val apps = mutableStateListOf<PhoneApp>()
    var loading by mutableStateOf(false); private set
    private var loadedAt = 0L

    private fun alias(ctx: Context) = ComponentName(ctx, "com.reverender.r95.HomeAlias")

    fun homeEnabled(ctx: Context): Boolean =
        ctx.packageManager.getComponentEnabledSetting(alias(ctx)) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED

    /** Offers REVERENDER_95 as a home app, then opens Android's "Default home app" picker. */
    fun setHomeMode(ctx: Context, on: Boolean) {
        ctx.packageManager.setComponentEnabledSetting(
            alias(ctx),
            if (on) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP,
        )
        openHomeSettings(ctx)
    }

    fun openHomeSettings(ctx: Context) {
        val i = Intent(Settings.ACTION_HOME_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { ctx.startActivity(i) }.onFailure { runCatching { ctx.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } }
    }

    /** True when this launch came in as the phone's home screen. */
    fun isHomeIntent(i: Intent?): Boolean = i?.categories?.contains(Intent.CATEGORY_HOME) == true

    /** The phone's launchable apps, A–Z (loaded off the UI thread, refreshed at most every 30 s). */
    fun load(ctx: Context) {
        if (loading || System.currentTimeMillis() - loadedAt < 30_000 && apps.isNotEmpty()) return
        loading = true
        val app = ctx.applicationContext
        Thread {
            val pm = app.packageManager
            val res = runCatching {
                @Suppress("DEPRECATION")
                pm.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
                    .filter { it.activityInfo.packageName != app.packageName }
                    .map { ri ->
                        val icon = runCatching { ri.loadIcon(pm).toBitmap(64, 64).asImageBitmap() }.getOrNull()
                        PhoneApp(ri.loadLabel(pm).toString(), ComponentName(ri.activityInfo.packageName, ri.activityInfo.name), icon)
                    }
                    .sortedBy { it.label.lowercase() }
            }.getOrDefault(emptyList())
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                apps.clear(); apps.addAll(res); loading = false; loadedAt = System.currentTimeMillis()
            }
        }.start()
    }

    /**
     * Opens a phone app. With "floating windows" on it asks Android for a freeform window
     * (Samsung pop-up view, Android desktop windowing, or the developer option "freeform windows"):
     * a centred window about 3/4 of the screen. Phones without freeform simply open it full screen.
     */
    fun launch(ctx: Context, a: PhoneApp, fullScreen: Boolean = false) = launch(ctx, a.component, fullScreen)

    fun launch(ctx: Context, component: ComponentName, fullScreen: Boolean = false) {
        val i = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setComponent(component)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        val opts = if (!fullScreen && runCatching { OsPrefs.floatingApps }.getOrDefault(true)) floatingOptions(ctx) else null
        runCatching { ctx.startActivity(i, opts) }.onFailure { runCatching { ctx.startActivity(i) } }
    }

    private fun floatingOptions(ctx: Context): android.os.Bundle? = runCatching {
        val dm = ctx.resources.displayMetrics
        val w = (dm.widthPixels * 0.78f).toInt(); val h = (dm.heightPixels * 0.62f).toInt()
        val l = (dm.widthPixels - w) / 2; val t = (dm.heightPixels - h) / 3
        val o = android.app.ActivityOptions.makeBasic()
        o.setLaunchBounds(android.graphics.Rect(l, t, l + w, t + h))
        // WINDOWING_MODE_FREEFORM = 5 (not public API; ignored where the phone has no freeform windows)
        runCatching { android.app.ActivityOptions::class.java.getMethod("setLaunchWindowingMode", Int::class.javaPrimitiveType).invoke(o, 5) }
        o.toBundle()
    }.getOrNull()

    fun find(flat: String): PhoneApp? = apps.firstOrNull { it.component.flattenToString() == flat }

    /** Android's App info page (permissions, storage, force stop, uninstall). */
    fun appInfo(ctx: Context, pkg: String) {
        runCatching { ctx.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:$pkg")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }

    /** Android's own uninstall confirmation. */
    fun uninstall(ctx: Context, pkg: String) {
        @Suppress("DEPRECATION")
        val i = Intent(Intent.ACTION_UNINSTALL_PACKAGE, android.net.Uri.parse("package:$pkg")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { ctx.startActivity(i) }.onFailure { appInfo(ctx, pkg) }
        loadedAt = 0L // re-read the list next time
    }

    /** Opens a desktop shortcut target: "app:<window id>" or "pkg:<component>". */
    /** Pulls down the phone's notification shade, like swiping down on a normal home screen. */
    fun openNotifications(ctx: Context): Boolean = runCatching {
        @android.annotation.SuppressLint("WrongConstant")
        val sb = ctx.getSystemService("statusbar") ?: return false
        sb.javaClass.getMethod("expandNotificationsPanel").invoke(sb)
        true
    }.getOrElse { android.util.Log.w("R95", "notification shade", it); false }

    fun openTarget(ctx: Context, target: String, fullScreen: Boolean = false, openWindow: (String) -> Unit) {
        when {
            target.startsWith("app:") -> openWindow(target.removePrefix("app:"))
            target.startsWith("pkg:") -> ComponentName.unflattenFromString(target.removePrefix("pkg:"))?.let { launch(ctx, it, fullScreen) }
        }
    }

    /** Does this phone advertise freeform windows? (Samsung pop-up view works even when this says no.) */
    fun freeformLikely(ctx: Context): Boolean =
        ctx.packageManager.hasSystemFeature("android.software.freeform_window_management") ||
            runCatching { android.provider.Settings.Global.getInt(ctx.contentResolver, "enable_freeform_support", 0) == 1 }.getOrDefault(false) ||
            Build.MANUFACTURER.equals("samsung", true)
}
