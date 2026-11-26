package com.reverender.r95.audio

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

/**
 * "Share PC/Tab": the site asks the browser to share a tab's audio (getDisplayMedia). The Android
 * equivalent is playback capture of other apps (Android 10+), which needs the screen-capture consent
 * and a mediaProjection foreground service while it runs. The audio only feeds the visualisers.
 */
object SystemCapture {
    val supported: Boolean get() = Build.VERSION.SDK_INT >= 29
    private var projection: MediaProjection? = null
    var pendingResult: ((Boolean) -> Unit)? = null

    fun consentIntent(ctx: Context): Intent =
        (ctx.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager).createScreenCaptureIntent()

    private var consentCode = 0
    private var consentData: Intent? = null

    /** Called with the consent activity result; [pendingResult] gets the outcome once capture runs. */
    fun onConsent(ctx: Context, resultCode: Int, data: Intent?) {
        if (!supported || data == null || resultCode != android.app.Activity.RESULT_OK) { pendingResult?.invoke(false); pendingResult = null; return }
        consentCode = resultCode; consentData = data
        // the projection may only be created once the mediaProjection service is in the foreground
        ctx.startForegroundServiceCompat(Intent(ctx, ProjectionService::class.java))
    }

    /** From ProjectionService.onStartCommand, after startForeground. */
    internal fun begin(ctx: Context) {
        val data = consentData ?: return
        consentData = null
        val mpm = ctx.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val p = runCatching { mpm.getMediaProjection(consentCode, data) }.getOrNull()
        val ok = if (p != null) {
            projection = p
            p.registerCallback(object : MediaProjection.Callback() { override fun onStop() { stop(ctx.applicationContext) } }, android.os.Handler(android.os.Looper.getMainLooper()))
            startRecord(p)
        } else false
        if (!ok) stop(ctx.applicationContext)
        pendingResult?.invoke(ok); pendingResult = null
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(29)
    private fun startRecordQ(p: MediaProjection): Boolean {
        val cfg = AudioPlaybackCaptureConfiguration.Builder(p)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
            .addMatchingUsage(AudioAttributes.USAGE_GAME)
            .addMatchingUsage(AudioAttributes.USAGE_UNKNOWN)
            // never our own output: the DSP monitor plays the processed copy back and must not hear itself
            .excludeUid(android.os.Process.myUid())
            .build()
        val fmt = AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(48000).setChannelMask(AudioFormat.CHANNEL_IN_STEREO).build()
        val rec = runCatching { AudioRecord.Builder().setAudioFormat(fmt).setAudioPlaybackCaptureConfig(cfg).setBufferSizeInBytes(16384).build() }.getOrNull() ?: return false
        return MicCapture.startWith(rec, "system", 2)
    }

    private fun startRecord(p: MediaProjection): Boolean = if (Build.VERSION.SDK_INT >= 29) startRecordQ(p) else false

    val running: Boolean get() = MicCapture.running && MicCapture.kind == "system"

    fun stop(ctx: Context) {
        if (MicCapture.kind == "system") MicCapture.stop()
        runCatching { projection?.stop() }
        projection = null
        ctx.stopService(Intent(ctx, ProjectionService::class.java))
    }
}

private fun Context.startForegroundServiceCompat(i: Intent) {
    if (Build.VERSION.SDK_INT >= 26) startForegroundService(i) else startService(i)
}

/** Foreground service required by Android while another app's audio is being captured. */
class ProjectionService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) nm.createNotificationChannel(NotificationChannel("r95_capture", "Audio sharing", NotificationManager.IMPORTANCE_LOW))
        val n: Notification = NotificationCompat.Builder(this, "r95_capture")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("REVERENDER_95")
            .setContentText("Routing other apps' audio into the visualizers and the DSP rack")
            .setOngoing(true)
            .build()
        if (Build.VERSION.SDK_INT >= 29) startForeground(77, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION) else startForeground(77, n)
        SystemCapture.begin(this)
        return START_NOT_STICKY
    }
}
