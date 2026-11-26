package com.reverender.r95.ui.vhs

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.reverender.r95.BuildConfig
import com.reverender.r95.os.Haptics
import com.reverender.r95.os.Launcher
import kotlinx.coroutines.delay

/**
 * The notification area in the VHS title bars (VHS+ APK only): one pixel icon per app with notifications,
 * drawn in the TEXT colour, and how many there are. A new one briefly swaps the icons for the app's name.
 */
internal object VhsNotifs {
    class Item(val pkg: String, val icon: ImageBitmap?)
    var items by mutableStateOf<List<Item>>(emptyList())
    var count by mutableIntStateOf(0)
    /** the app whose notification just came in (shown for a moment), and a counter to restart the timer */
    var flash by mutableStateOf<String?>(null)
    var flashN by mutableIntStateOf(0)
    var connected by mutableStateOf(false)
    private var keys = emptySet<String>()
    private val icons = HashMap<String, ImageBitmap?>()

    private fun keep(own: String, s: StatusBarNotification): Boolean {
        val n = s.notification ?: return false
        return s.packageName != own && s.isClearable && !s.isOngoing && n.flags and Notification.FLAG_GROUP_SUMMARY == 0
    }

    /** Re-read the phone's notifications; [posted] = the one that just arrived (for the name flash). */
    fun refresh(svc: NotificationListenerService, posted: StatusBarNotification? = null) {
        if (!BuildConfig.NOTIF) return
        val all = runCatching { svc.activeNotifications }.getOrNull() ?: return
        val shown = all.filter { keep(svc.packageName, it) }.sortedByDescending { it.postTime }
        val firstPerApp = LinkedHashMap<String, StatusBarNotification>()
        for (s in shown) firstPerApp.putIfAbsent(s.packageName, s)
        items = firstPerApp.values.map { s -> Item(s.packageName, icons.getOrPut(s.packageName + ":" + s.notification.icon) { iconOf(svc, s) }) }
        count = shown.size
        val now = shown.map { it.key }.toSet()
        if (posted != null && posted.key !in keys && posted.key in now) {
            flash = appName(svc, posted.packageName); flashN++
        }
        keys = now
        connected = true
    }

    fun disconnected() { connected = false; items = emptyList(); count = 0 }

    private fun appName(svc: NotificationListenerService, pkg: String): String = runCatching {
        svc.packageManager.getApplicationLabel(svc.packageManager.getApplicationInfo(pkg, 0)).toString()
    }.getOrDefault(pkg.substringAfterLast('.'))

    /** the notification's small icon at 14 x 14: chunky pixels once it's scaled up */
    private fun iconOf(svc: NotificationListenerService, s: StatusBarNotification): ImageBitmap? = runCatching {
        val n = s.notification
        val d = if (android.os.Build.VERSION.SDK_INT >= 23) n.smallIcon?.loadDrawable(svc)
            else svc.createPackageContext(s.packageName, 0).resources.getDrawable(n.icon)
        d ?: return null
        val b = android.graphics.Bitmap.createBitmap(14, 14, android.graphics.Bitmap.Config.ARGB_8888)
        d.setBounds(0, 0, 14, 14); d.draw(android.graphics.Canvas(b))
        b.asImageBitmap()
    }.getOrNull()
}

/** The icons and count (or the incoming app's name), top right of the home screen's title bar. */
@Composable
internal fun VhsNotifArea(modifier: Modifier = Modifier) {
    if (!BuildConfig.NOTIF || !VhsNotifs.connected) return
    val o = LocalOsd.current
    val ctx = LocalContext.current
    LaunchedEffect(VhsNotifs.flashN) { if (VhsNotifs.flash != null) { delay(2600); VhsNotifs.flash = null } }
    val flash = VhsNotifs.flash
    Row(
        modifier.pointerInput(Unit) { detectTapGestures { Haptics.tick(); Launcher.openNotifications(ctx) } },
        horizontalArrangement = Arrangement.spacedBy(pxDp(1f)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (flash != null) {
            VhsText("●" + vhsSafe(flash).trim().take(12), invert = true)
        } else if (VhsNotifs.count > 0) {
            val tint = ColorFilter.tint(o.fg)
            for (it in VhsNotifs.items.take(4)) {
                val img = it.icon ?: continue
                Canvas(Modifier.size(pxDp(7f))) {
                    val sh = (o.px * 0.6f).toInt()
                    drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), dstOffset = IntOffset(sh, sh), dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                        colorFilter = ColorFilter.tint(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f)), filterQuality = FilterQuality.None)
                    drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                        colorFilter = tint, filterQuality = FilterQuality.None)
                }
            }
            VhsText(if (VhsNotifs.count > 99) "99+" else VhsNotifs.count.toString())
        }
    }
}
