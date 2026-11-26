package com.reverender.r95.os

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.reverender.r95.MainActivity

/**
 * Office Plaza on the phone: PLANNER_95 reminders, FOCUS_95's countdown and end alarm, and sticky notes
 * pinned to the notification shade. Alarms are inexact-while-idle (no special alarm permission), so a
 * reminder can arrive up to a few minutes late on a sleeping phone.
 */
object OfficeNotify {
    private const val CH = "office95"
    const val FOCUS_ID = 9501
    private const val PIN_BASE = 9600

    private fun channel(ctx: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val nm = ctx.getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(CH) == null)
            nm.createNotificationChannel(NotificationChannel(CH, "Office Plaza (reminders, focus timer, pinned notes)", NotificationManager.IMPORTANCE_DEFAULT))
    }

    private fun openApp(ctx: Context, app: String): PendingIntent = PendingIntent.getActivity(ctx, app.hashCode(),
        Intent(ctx, MainActivity::class.java).putExtra("r95_open", app).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    fun show(ctx: Context, id: Int, title: String, text: String, app: String, ongoing: Boolean = false, countdownTo: Long = 0L, actions: List<Pair<String, PendingIntent>> = emptyList()) {
        channel(ctx)
        val b = NotificationCompat.Builder(ctx, CH)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title).setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(openApp(ctx, app))
            .setOngoing(ongoing).setAutoCancel(!ongoing).setOnlyAlertOnce(ongoing)
        if (countdownTo > 0) { b.setWhen(countdownTo).setUsesChronometer(true); if (Build.VERSION.SDK_INT >= 24) b.setChronometerCountDown(true) }
        actions.forEach { (label, pi) -> b.addAction(0, label, pi) }
        runCatching { androidx.core.app.NotificationManagerCompat.from(ctx).notify(id, b.build()) }
    }

    fun cancel(ctx: Context, id: Int) = androidx.core.app.NotificationManagerCompat.from(ctx).cancel(id)

    private fun alarmPi(ctx: Context, id: Int, title: String, text: String, app: String) = PendingIntent.getBroadcast(ctx, id,
        Intent(ctx, OfficeAlarmReceiver::class.java).putExtra("id", id).putExtra("title", title).putExtra("text", text).putExtra("app", app),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    /** A notification at [atMillis], even if the app is closed. */
    fun schedule(ctx: Context, id: Int, atMillis: Long, title: String, text: String, app: String) {
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = alarmPi(ctx, id, title, text, app)
        if (Build.VERSION.SDK_INT >= 23) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, pi) else am.set(AlarmManager.RTC_WAKEUP, atMillis, pi)
    }

    fun unschedule(ctx: Context, id: Int) {
        (ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(alarmPi(ctx, id, "", "", ""))
    }

    /** PLANNER_95: one reminder id per day. */
    fun dayId(day: String) = 10000 + (day.hashCode() and 0x3FFFFFF)

    // ------------------------------------------------------------ pinned sticky notes
    fun pinNote(ctx: Context, index: Int, text: String) {
        val unpin = PendingIntent.getBroadcast(ctx, PIN_BASE + index, Intent(ctx, OfficeAlarmReceiver::class.java).putExtra("unpin", PIN_BASE + index),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        show(ctx, PIN_BASE + index, "📝 " + (text.lineSequence().firstOrNull()?.take(40)?.ifBlank { null } ?: "Sticky note"), text.ifBlank { "(empty note)" }, "sticky",
            ongoing = true, actions = listOf("Unpin" to unpin))
    }
    fun unpinNote(ctx: Context, index: Int) = cancel(ctx, PIN_BASE + index)
}

class OfficeAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, i: Intent) {
        val unpin = i.getIntExtra("unpin", -1)
        if (unpin >= 0) { OfficeNotify.cancel(ctx, unpin); return }
        val id = i.getIntExtra("id", 0)
        val title = i.getStringExtra("title") ?: "REVERENDER_95"; val text = i.getStringExtra("text") ?: ""
        OfficeNotify.show(ctx, id, title, text, i.getStringExtra("app") ?: "planner")
        // with the desktop open it also pops up as a Win95 dialog
        RetroAlerts.postOwn(RetroAlerts.Alert("own-$id", "REVERENDER_95", title, text, if (id == OfficeNotify.FOCUS_ID) "info" else "msg", null))
        if (id == OfficeNotify.FOCUS_ID) { OsPrefs.init(ctx); Haptics.clunk("eject") }
    }
}
