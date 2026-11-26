package com.reverender.r95.os

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.RemoteViews
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.reverender.r95.MainActivity
import com.reverender.r95.R
import com.reverender.r95.audio.AudioBus
import com.reverender.r95.audio.FxSettings
import com.reverender.r95.audio.PlaybackService
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Win95-window home screen widgets: CASSETTE_95, VAPOR_AMP 95, DSP_FX_95 presets, ANALOG_VU. */
class CassetteWidget : AppWidgetProvider() { override fun onUpdate(c: Context, m: AppWidgetManager, ids: IntArray) = WidgetHub.updateAll(c) }
class AmpWidget : AppWidgetProvider() { override fun onUpdate(c: Context, m: AppWidgetManager, ids: IntArray) = WidgetHub.updateAll(c) }
class FxWidget : AppWidgetProvider() { override fun onUpdate(c: Context, m: AppWidgetManager, ids: IntArray) = WidgetHub.updateAll(c) }
class VuWidget : AppWidgetProvider() {
    override fun onUpdate(c: Context, m: AppWidgetManager, ids: IntArray) = WidgetHub.updateAll(c)
    override fun onDisabled(c: Context) = WidgetHub.refreshTicker(c)
}

/** Button taps from the widgets. */
class WidgetActionReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        OsPrefs.init(ctx)
        val action = intent.getStringExtra("a") ?: return
        val pending = goAsync()
        WidgetHub.act(ctx.applicationContext, action) { pending.finish() }
    }
}

object WidgetHub {
    private val main = android.os.Handler(android.os.Looper.getMainLooper())
    var currentPreset = "clean"
    private var controller: MediaController? = null
    private var reelAngle = 0f

    private fun ids(ctx: Context, cls: Class<*>) = AppWidgetManager.getInstance(ctx).getAppWidgetIds(ComponentName(ctx, cls))
    private fun anyWidgets(ctx: Context) = listOf(CassetteWidget::class.java, AmpWidget::class.java, FxWidget::class.java, VuWidget::class.java).any { ids(ctx, it).isNotEmpty() }

    private fun pi(ctx: Context, a: String, req: Int): PendingIntent =
        PendingIntent.getBroadcast(ctx, req, Intent(ctx, WidgetActionReceiver::class.java).putExtra("a", a).setPackage(ctx.packageName),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    private fun openApp(ctx: Context, app: String?): PendingIntent =
        PendingIntent.getActivity(ctx, (app ?: "").hashCode(), Intent(ctx, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP).also { if (app != null) it.putExtra("r95_open", app) },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    /** The live player: the service's own, or a controller that starts the service. */
    private fun withPlayer(ctx: Context, then: (Player?) -> Unit) {
        PlaybackService.player?.let { then(it); return }
        controller?.takeIf { it.isConnected }?.let { then(it); return }
        val f = MediaController.Builder(ctx, SessionToken(ctx, ComponentName(ctx, PlaybackService::class.java))).buildAsync()
        f.addListener({ val c = runCatching { f.get() }.getOrNull(); controller = c; then(c) }, { main.post(it) })
    }

    fun act(ctx: Context, a: String, done: () -> Unit) {
        if (a.startsWith("fx:")) {
            val k = a.removePrefix("fx:")
            currentPreset = k
            if (com.reverender.r95.audio.SiteStudio.installed) runCatching { com.reverender.r95.audio.SiteStudio.applyFxPreset(k) }
            else {
                val speed = FxSettings.applyPreset(AudioBus.fx, k)
                withPlayer(ctx) { p -> p?.setPlaybackSpeed(speed); updateAll(ctx); done() }
                Haptics.tick(); return
            }
            Haptics.tick(); updateAll(ctx); done(); return
        }
        withPlayer(ctx) { p ->
            if (p == null || p.mediaItemCount == 0) {
                // nothing loaded yet (the app was closed): open VAPOR_AMP instead
                runCatching { openApp(ctx, if (a == "eject") "cassette" else "player").send() }
            } else when (a) {
                "play" -> { if (p.isPlaying) { p.pause(); Haptics.clunk("pause") } else { p.play(); Haptics.clunk("play") } }
                "stop" -> { p.pause(); p.seekTo(0); Haptics.clunk("stop") }
                "next" -> { p.seekToNextMediaItem(); Haptics.tick() }
                "prev" -> { p.seekToPreviousMediaItem(); Haptics.tick() }
                "rew" -> { p.seekTo((p.currentPosition - 10_000).coerceAtLeast(0)); Haptics.hum(false); main.postDelayed({ Haptics.stopHum() }, 380) }
                "ff" -> { p.seekTo(p.currentPosition + 10_000); Haptics.hum(true); main.postDelayed({ Haptics.stopHum() }, 380) }
                "eject" -> { p.pause(); Haptics.clunk("eject"); runCatching { openApp(ctx, "cassette").send() } }
            }
            main.postDelayed({ updateAll(ctx) }, 120)
            done()
        }
    }

    private fun title(p: Player?): String {
        val m = p?.mediaMetadata
        val t = m?.title?.toString().orEmpty(); val ar = m?.artist?.toString().orEmpty()
        return when { t.isBlank() -> "NO TAPE"; ar.isBlank() -> t; else -> "$ar - $t" }
    }
    private fun time(ms: Long) = "%02d:%02d".format(ms / 60000, ms / 1000 % 60)

    fun updateAll(ctx: Context) {
        val p = PlaybackService.player ?: controller
        val m = AppWidgetManager.getInstance(ctx)
        val playing = p?.isPlaying == true
        ids(ctx, CassetteWidget::class.java).takeIf { it.isNotEmpty() }?.let { ws ->
            val v = RemoteViews(ctx.packageName, R.layout.widget_cassette)
            v.setTextViewText(R.id.w_title, "📼 CASSETTE_95.EXE")
            v.setTextViewText(R.id.w_track, (if (playing) "▶ " else "■ ") + title(p).uppercase())
            v.setTextViewText(R.id.w_play, if (playing) "⏸" else "▶")
            v.setImageViewBitmap(R.id.w_reels, reels(playing))
            v.setOnClickPendingIntent(R.id.w_prev, pi(ctx, "rew", 1)); v.setOnClickPendingIntent(R.id.w_play, pi(ctx, "play", 2))
            v.setOnClickPendingIntent(R.id.w_stop, pi(ctx, "stop", 3)); v.setOnClickPendingIntent(R.id.w_next, pi(ctx, "ff", 4))
            v.setOnClickPendingIntent(R.id.w_eject, pi(ctx, "eject", 5)); v.setOnClickPendingIntent(R.id.w_title, openApp(ctx, "cassette"))
            m.updateAppWidget(ws, v)
        }
        ids(ctx, AmpWidget::class.java).takeIf { it.isNotEmpty() }?.let { ws ->
            val v = RemoteViews(ctx.packageName, R.layout.widget_amp)
            v.setTextViewText(R.id.w_title, "🎵 VAPOR_AMP 95")
            v.setTextViewText(R.id.w_time, (if (playing) "▶ " else "❚❚ ") + time(p?.currentPosition ?: 0) + (p?.duration?.takeIf { it > 0 }?.let { " / " + time(it) } ?: ""))
            v.setTextViewText(R.id.w_track, title(p))
            v.setTextViewText(R.id.w_play, if (playing) "⏸" else "▶")
            v.setOnClickPendingIntent(R.id.w_prev, pi(ctx, "prev", 11)); v.setOnClickPendingIntent(R.id.w_play, pi(ctx, "play", 12))
            v.setOnClickPendingIntent(R.id.w_stop, pi(ctx, "stop", 13)); v.setOnClickPendingIntent(R.id.w_next, pi(ctx, "next", 14))
            v.setOnClickPendingIntent(R.id.w_open, openApp(ctx, "player")); v.setOnClickPendingIntent(R.id.w_title, openApp(ctx, "player"))
            m.updateAppWidget(ws, v)
        }
        ids(ctx, FxWidget::class.java).takeIf { it.isNotEmpty() }?.let { ws ->
            val v = RemoteViews(ctx.packageName, R.layout.widget_fx)
            v.setTextViewText(R.id.w_title, "🎛 DSP_FX_95 PRESETS")
            val btns = intArrayOf(R.id.w_p0, R.id.w_p1, R.id.w_p2, R.id.w_p3, R.id.w_p4, R.id.w_p5, R.id.w_p6, R.id.w_p7)
            FxSettings.PRESETS.take(8).forEachIndexed { i, (k, label) ->
                v.setTextViewText(btns[i], label)
                v.setInt(btns[i], "setBackgroundResource", if (k == currentPreset) R.drawable.w95_button_on else R.drawable.w95_button)
                v.setOnClickPendingIntent(btns[i], pi(ctx, "fx:$k", 20 + i))
            }
            v.setOnClickPendingIntent(R.id.w_title, openApp(ctx, "fx"))
            m.updateAppWidget(ws, v)
        }
        updateVu(ctx)
        refreshTicker(ctx)
    }

    private fun updateVu(ctx: Context) {
        val ws = ids(ctx, VuWidget::class.java); if (ws.isEmpty()) return
        val v = RemoteViews(ctx.packageName, R.layout.widget_vu)
        v.setTextViewText(R.id.w_title, "📈 ANALOG_VU.EXE")
        v.setImageViewBitmap(R.id.w_meter, meters())
        v.setOnClickPendingIntent(R.id.w_title, openApp(ctx, "vumeter"))
        AppWidgetManager.getInstance(ctx).updateAppWidget(ws, v)
    }

    // ------------------------------------------------------------ live updates while music plays
    private var ticking = false
    private var tickCtx: Context? = null
    private var vuL = 0f; private var vuR = 0f
    private var n = 0
    private val tick = object : Runnable {
        override fun run() {
            val c = tickCtx ?: return
            val p = PlaybackService.player
            if (p?.isPlaying != true) { stopTicker(); updateAll(c); return }
            updateVu(c)
            if (++n % 5 == 0) { reelAngle += 40f; updateAll(c) } // reels + clock once a second
            main.postDelayed(this, 200)
        }
    }

    fun refreshTicker(ctx: Context) {
        val want = PlaybackService.player?.isPlaying == true && anyWidgets(ctx)
        if (want && !ticking) {
            ticking = true; tickCtx = ctx.applicationContext
            AudioBus.backgroundListeners.incrementAndGet()
            main.postDelayed(tick, 200)
        } else if (!want && ticking) stopTicker()
    }
    private fun stopTicker() {
        if (!ticking) return
        ticking = false; main.removeCallbacks(tick)
        AudioBus.backgroundListeners.decrementAndGet()
    }

    /** Called by the playback service when the track or play state changes. */
    fun onPlayerChanged(ctx: Context) { if (anyWidgets(ctx)) updateAll(ctx) }

    // ------------------------------------------------------------ bitmaps
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private fun reels(playing: Boolean): Bitmap {
        val b = Bitmap.createBitmap(200, 90, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        paint.color = 0xFF2A2A2A.toInt(); c.drawRoundRect(4f, 6f, 196f, 84f, 8f, 8f, paint)
        paint.color = 0xFFE8DCC0.toInt(); c.drawRect(24f, 14f, 176f, 38f, paint)
        paint.color = 0xFF202020.toInt(); paint.textSize = 14f; paint.isFakeBoldText = true; c.drawText("REVERENDER C-90", 36f, 31f, paint)
        for (k in 0..1) {
            val cx = 62f + k * 76f; val cy = 60f
            paint.color = 0xFF111111.toInt(); c.drawCircle(cx, cy, 19f, paint)
            paint.color = 0xFFF2F2F2.toInt(); c.drawCircle(cx, cy, 12f, paint)
            paint.color = 0xFF111111.toInt()
            for (i in 0 until 6) {
                val a = (reelAngle * (if (playing) 1 else 0) + i * 60f) * PI.toFloat() / 180f
                c.drawCircle(cx + cos(a) * 7f, cy + sin(a) * 7f, 2.4f, paint)
            }
        }
        return b
    }

    private fun meters(): Bitmap {
        val f = AudioBus.currentFrame()
        vuL += ((f.rmsL * 3.2f).coerceIn(0f, 1.05f) - vuL) * 0.6f
        vuR += ((f.rmsR * 3.2f).coerceIn(0f, 1.05f) - vuR) * 0.6f
        val w = 360; val h = 150
        val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        for (k in 0..1) {
            val left = 6f + k * 178f; val top = 6f; val mw = 170f; val mh = 138f
            paint.style = Paint.Style.FILL; paint.color = 0xFFF4E6B8.toInt()
            c.drawRect(left, top, left + mw, top + mh, paint)
            val px = left + mw / 2; val py = top + mh - 14f; val r = mh - 34f
            paint.style = Paint.Style.STROKE; paint.strokeWidth = 2f
            for (i in 0..20) {
                val a = (-50 + i * 5) * PI.toFloat() / 180f
                paint.color = if (i >= 15) 0xFFCC1111.toInt() else 0xFF222222.toInt()
                val r0 = r * (if (i % 5 == 0) 0.84f else 0.9f)
                c.drawLine(px + sin(a) * r0, py - cos(a) * r0, px + sin(a) * r, py - cos(a) * r, paint)
            }
            val v = if (k == 0) vuL else vuR
            val a = (-50 + v * 100) * PI.toFloat() / 180f
            paint.color = 0xFF111111.toInt(); paint.strokeWidth = 3f
            c.drawLine(px, py, px + sin(a) * r, py - cos(a) * r, paint)
            paint.style = Paint.Style.FILL; paint.textSize = 16f; paint.isFakeBoldText = true
            c.drawText(if (k == 0) "L" else "R", left + 8f, top + 20f, paint)
            c.drawText("VU", px - 10f, top + mh * 0.62f, paint)
            c.drawCircle(px, py, 6f, paint)
            paint.style = Paint.Style.STROKE; paint.color = 0xFF808080.toInt(); paint.strokeWidth = 2f
            c.drawRect(left, top, left + mw, top + mh, paint)
            paint.style = Paint.Style.FILL
        }
        return b
    }
}
