package com.reverender.r95.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder

/**
 * Keeps the VHS video deck's sound going with the screen off: while a tape plays, the app runs as a media
 * foreground service, so Android's sleep modes (Doze, app standby) don't cut the stream.
 */
class VhsKeepAlive : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val nm = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= 26) nm.createNotificationChannel(NotificationChannel(CH, "VHS tape playing", NotificationManager.IMPORTANCE_LOW))
        val open = packageManager.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val title = intent?.getStringExtra("title")?.takeIf { it.isNotBlank() } ?: "VHS TAPE"
        val n = (if (Build.VERSION.SDK_INT >= 26) Notification.Builder(this, CH) else @Suppress("DEPRECATION") Notification.Builder(this))
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("▶ PLAY")
            .setContentText(title)
            .setOngoing(true)
            .setContentIntent(open)
            .build()
        runCatching {
            if (Build.VERSION.SDK_INT >= 29) startForeground(ID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
            else startForeground(ID, n)
        }.onFailure { stopSelf() }
        return START_NOT_STICKY
    }

    companion object {
        private const val CH = "r95_vhs_tape"
        private const val ID = 9595
        private var on = false

        /** call when the deck starts or stops playing (start only works while the app is on screen) */
        fun set(ctx: Context, playing: Boolean, title: String) {
            val app = ctx.applicationContext
            if (playing) {
                runCatching {
                    val i = Intent(app, VhsKeepAlive::class.java).putExtra("title", title)
                    if (Build.VERSION.SDK_INT >= 26) app.startForegroundService(i) else app.startService(i)
                    on = true
                }
            } else if (on) {
                on = false
                runCatching { app.stopService(Intent(app, VhsKeepAlive::class.java)) }
            }
        }
    }
}
