package com.reverender.r95.os

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.runtime.mutableStateListOf
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Win95/Longhorn notification hijacker: incoming Android notifications (texts, Discord, system alerts)
 * are shown as retro warning dialogs with a pitched-down 90s chime — inside the desktop while the app is
 * open, and (with "Display over other apps") on top of everything else.
 */
object RetroAlerts {
    data class Alert(val key: String, val app: String, val title: String, val text: String, val kind: String, val intent: android.app.PendingIntent?, val at: Long = System.currentTimeMillis())

    /** dialogs waiting on the desktop, newest last */
    val queue = mutableStateListOf<Alert>()

    fun listenerEnabled(ctx: Context): Boolean {
        val flat = Settings.Secure.getString(ctx.contentResolver, "enabled_notification_listeners") ?: return false
        return flat.contains(ComponentName(ctx, RetroNotificationService::class.java).flattenToString())
    }

    fun openListenerSettings(ctx: Context) {
        runCatching { ctx.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }

    fun canOverlay(ctx: Context) = Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(ctx)
    fun openOverlaySettings(ctx: Context) {
        runCatching { ctx.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, android.net.Uri.parse("package:" + ctx.packageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }

    fun post(ctx: Context, a: Alert) {
        if (!runCatching { OsPrefs.retroAlerts }.getOrDefault(false)) return
        val main = android.os.Handler(android.os.Looper.getMainLooper())
        main.post {
            queue.removeAll { it.key == a.key }
            queue.add(a)
            while (queue.size > 4) queue.removeAt(0)
            if (OsPrefs.alertChime) Chime.play(a.kind)
            if (!com.reverender.r95.core.AppVisibility.foreground && OsPrefs.alertOverlay && canOverlay(ctx)) Overlay.show(ctx.applicationContext, a)
        }
    }

    fun dismiss(a: Alert) { queue.remove(a) }

    /** REVERENDER_95's own events (planner reminders, focus timer): a desktop dialog + chime while the app is open. */
    fun postOwn(a: Alert) {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            if (!com.reverender.r95.core.AppVisibility.foreground) return@post
            queue.removeAll { it.key == a.key }; queue.add(a)
            if (runCatching { OsPrefs.alertChime }.getOrDefault(true)) Chime.play(a.kind)
        }
    }

    fun open(ctx: Context, a: Alert) {
        dismiss(a)
        runCatching { a.intent?.send() }
    }

    /** A synthesized "ding" chord, pitched down and slightly detuned like a worn tape of the 90s system sounds. */
    object Chime {
        private const val SR = 22050
        private val cache = HashMap<String, ShortArray>()
        private fun render(kind: String): ShortArray = cache.getOrPut(kind) {
            // warning: falling minor third; message: rising major chord
            val notes = when (kind) { "error" -> doubleArrayOf(392.0, 330.0); "msg" -> doubleArrayOf(523.25, 659.25, 783.99); else -> doubleArrayOf(659.25, 523.25, 783.99) }
                .map { it * 0.72 } // pitched down
            val n = (SR * 1.15).toInt()
            ShortArray(n) { i ->
                val t = i.toDouble() / SR
                var s = 0.0
                notes.forEachIndexed { k, f ->
                    val st = k * 0.085
                    if (t >= st) {
                        val u = t - st
                        val env = exp(-u * 3.2) * minOf(1.0, u * 300)
                        val wow = 1 + 0.004 * sin(2 * PI * 5.5 * t)
                        s += env * (sin(2 * PI * f * wow * u + 0.9 * sin(2 * PI * f * 2.0 * u) * exp(-u * 6)) * 0.55 + sin(2 * PI * f * 2.01 * u) * 0.12)
                    }
                }
                (s / notes.size * 26000).coerceIn(-32767.0, 32767.0).toInt().toShort()
            }
        }

        fun play(kind: String) {
            Thread {
                runCatching {
                    val pcm = render(kind)
                    val t = AudioTrack(
                        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build(),
                        AudioFormat.Builder().setSampleRate(SR).setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build(),
                        pcm.size * 2, AudioTrack.MODE_STATIC, android.media.AudioManager.AUDIO_SESSION_ID_GENERATE,
                    )
                    t.write(pcm, 0, pcm.size); t.play()
                    Thread.sleep(1300); t.release()
                }
            }.start()
        }
    }

    /** The same dialog drawn over other apps (plain views: no activity needed). */
    private object Overlay {
        private var view: View? = null
        private val main = android.os.Handler(android.os.Looper.getMainLooper())
        private val hide = Runnable { remove() }

        fun show(ctx: Context, a: Alert) {
            remove()
            val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val d = ctx.resources.displayMetrics.density
            fun px(v: Int) = (v * d).toInt()
            val longhorn = runCatching { ctx.getSharedPreferences("r95", Context.MODE_PRIVATE).getString("os_style", "longhorn") != "win95" }.getOrDefault(true)
            val root = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                background = GradientDrawable().apply {
                    setColor(if (longhorn) Color.rgb(0xEE, 0xF2, 0xF4) else Color.rgb(0xC0, 0xC0, 0xC0))
                    setStroke(px(2), if (longhorn) Color.rgb(0x2C, 0x3A, 0x47) else Color.WHITE)
                    cornerRadius = if (longhorn) px(7).toFloat() else 0f
                }
                elevation = px(8).toFloat()
            }
            val title = TextView(ctx).apply {
                text = "  ${if (a.kind == "error") "⚠" else "✉"} ${a.app}"
                setTextColor(Color.WHITE); typeface = Typeface.DEFAULT_BOLD; textSize = 13f
                background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                    if (longhorn) intArrayOf(Color.rgb(0x2C, 0x3A, 0x47), Color.rgb(0x11, 0x19, 0x1D)) else intArrayOf(Color.rgb(0, 0, 0x80), Color.rgb(0x10, 0x84, 0xD0)))
                setPadding(px(6), px(5), px(6), px(5))
            }
            val body = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; setPadding(px(12), px(10), px(12), px(6)); gravity = Gravity.CENTER_VERTICAL }
            body.addView(TextView(ctx).apply { text = if (a.kind == "error") "⚠️" else "💬"; textSize = 26f; setPadding(0, 0, px(10), 0) })
            body.addView(TextView(ctx).apply {
                text = buildString { if (a.title.isNotBlank()) append(a.title).append('\n'); append(a.text) }
                setTextColor(Color.BLACK); textSize = 13f; maxLines = 5
            }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            val buttons = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.END; setPadding(px(8), px(2), px(8), px(8)) }
            fun btn(label: String, onTap: () -> Unit) = TextView(ctx).apply {
                text = label; setTextColor(Color.BLACK); textSize = 13f; gravity = Gravity.CENTER
                background = GradientDrawable().apply { setColor(if (longhorn) Color.rgb(0xF6, 0xF8, 0xF9) else Color.rgb(0xC0, 0xC0, 0xC0)); setStroke(px(1), Color.rgb(0x40, 0x40, 0x40)); cornerRadius = if (longhorn) px(3).toFloat() else 0f }
                setPadding(px(16), px(6), px(16), px(6))
                setOnClickListener { onTap() }
            }
            buttons.addView(btn("Open") { remove(); open(ctx, a) })
            buttons.addView(View(ctx), LinearLayout.LayoutParams(px(6), 1))
            buttons.addView(btn("OK") { remove(); dismiss(a) })
            root.addView(title, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
            root.addView(body); root.addView(buttons)
            val lp = WindowManager.LayoutParams(
                (ctx.resources.displayMetrics.widthPixels * 0.86f).toInt().coerceAtMost(px(420)),
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT,
            ).apply { gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL; y = px(48) }
            if (runCatching { wm.addView(root, lp) }.isSuccess) {
                view = root
                main.postDelayed(hide, 8000)
            }
        }

        fun remove() {
            main.removeCallbacks(hide)
            val v = view ?: return
            view = null
            runCatching { (v.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(v) }
        }
    }
}

/** Reads notifications as they arrive (only once the user grants notification access). */
class RetroNotificationService : NotificationListenerService() {
    override fun onCreate() { super.onCreate(); OsPrefs.init(this) }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == packageName || sbn.isOngoing || !sbn.isClearable) return
        val n = sbn.notification ?: return
        if (n.flags and Notification.FLAG_GROUP_SUMMARY != 0) return
        val ex = n.extras
        val title = ex.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = (ex.getCharSequence(Notification.EXTRA_BIG_TEXT) ?: ex.getCharSequence(Notification.EXTRA_TEXT))?.toString().orEmpty()
        if (title.isBlank() && text.isBlank()) return
        val app = runCatching { packageManager.getApplicationLabel(packageManager.getApplicationInfo(sbn.packageName, 0)).toString() }.getOrDefault(sbn.packageName)
        val kind = when (n.category) {
            Notification.CATEGORY_MESSAGE, Notification.CATEGORY_EMAIL, Notification.CATEGORY_SOCIAL -> "msg"
            Notification.CATEGORY_ERROR, Notification.CATEGORY_ALARM, Notification.CATEGORY_SYSTEM -> "error"
            else -> "info"
        }
        RetroAlerts.post(this, RetroAlerts.Alert(sbn.key, app, title, text, kind, n.contentIntent))
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        android.os.Handler(android.os.Looper.getMainLooper()).post { RetroAlerts.queue.removeAll { it.key == sbn.key } }
    }
}
