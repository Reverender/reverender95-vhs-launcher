package com.reverender.r95.net

import android.content.Context
import android.os.Handler
import android.os.Looper
import app.cash.quickjs.QuickJs
import com.reverender.r95.AppState
import com.reverender.r95.audio.AmpEngine
import com.reverender.r95.core.RevyRequests
import com.reverender.r95.ui.desktop.WindowManager
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

/** The page globals Revy's code calls (window.* functions and DOM reads), answered by the app. */
interface RevyHost {
    fun call(name: String, args: String): String
    fun get(key: String): String
}

/**
 * Revy's built-in brain: the site's own JavaScript (the RRDesktop brain in rr-os-styles.js and the command
 * rules of rr-revy.js, unchanged) running in an embedded QuickJS engine, with a shim mapping its page calls
 * onto the app. Gemini Nano does not exist here, so the site's no-on-device-AI path is the one that runs.
 */
object RevyBrain {
    private val exec = Executors.newSingleThreadExecutor { Thread(it, "revy-js").apply { isDaemon = true } }
    private val dispatcher = exec.asCoroutineDispatcher()
    private var js: QuickJs? = null
    private val main = Handler(Looper.getMainLooper())
    lateinit var state: AppState
    lateinit var wm: WindowManager
    private lateinit var appCtx: Context
    private val prefs by lazy { appCtx.getSharedPreferences("r95_revy_ls", Context.MODE_PRIVATE) }

    fun install(ctx: Context, s: AppState, w: WindowManager) { appCtx = ctx.applicationContext; state = s; wm = w }

    /** Runs [block] on the UI thread and waits for its answer (the page is single-threaded too). */
    private fun <T> onMain(block: () -> T): T {
        if (Looper.myLooper() == Looper.getMainLooper()) return block()
        var out: Result<T>? = null
        val l = CountDownLatch(1)
        main.post { out = runCatching(block); l.countDown() }
        l.await()
        return out!!.getOrThrow()
    }

    private val siteToApp by lazy { com.reverender.r95.core.SITE_WINDOW.entries.associate { (k, v) -> v to k } }
    private fun appIdFor(siteId: String): String? {
        siteToApp[siteId]?.let { return it }
        val id = siteId.removePrefix("win-")
        return id.takeIf { com.reverender.r95.core.Apps.byId(it) != null }
    }

    private val host = object : RevyHost {
        override fun call(name: String, args: String): String = try {
            val a = JSONArray(args)
            onMain { act(name, a) }
        } catch (e: Exception) { android.util.Log.w("R95", "revy host $name", e); "0" }

        override fun get(key: String): String = try { onMain { read(key) } } catch (e: Exception) { "" }
    }

    private fun act(name: String, a: JSONArray): String {
        when (name) {
            "restoreOrFocusWindow", "openAppFromStart" -> { val id = appIdFor(a.optString(0)) ?: return "0"; wm.open(id) }
            "closeWindow" -> { val id = appIdFor(a.optString(0)) ?: return "0"; wm.windows.filter { it.app.id == id }.toList().forEach { wm.close(it) } }
            "cycleWallpaper" -> state.cycleWallpaper(1)
            "prevWallpaper" -> state.cycleWallpaper(-1)
            "cycleTheme" -> state.cycleTheme(1)
            "prevTheme" -> state.cycleTheme(-1)
            "toggleCrtOverlay" -> { state.setScanlines(!state.scanlines); AmpEngine.toggleCrt(state.scanlines) }
            "togglePlayPause" -> AmpEngine.togglePlayPause()
            "play" -> if (!state.player.isPlaying) AmpEngine.togglePlayPause()
            "pause" -> if (state.player.isPlaying) AmpEngine.togglePlayPause()
            "playRandomTrackAndOffset" -> AmpEngine.playRandomTrackAndOffset()
            "randomizeAnimatedWallpaperSafe" -> { val l = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 11, 12, 24, 25, 26, 27, 28); state.setWallpaper(l.random()) }
            "arrangeOpenWindowsSmart" -> wm.tileOpen()
            "toggleAllVisualizers" -> wm.toggleAllVisualizers()
            "toggleFullScreen" -> return "0"
            "setBatteryPerformanceMode" -> state.setLowPower(a.optBoolean(0))
            "selectTrack" -> AmpEngine.selectTrack(a.optInt(0), a.optBoolean(1), a.optBoolean(2, true))
            "setVolume" -> { val v = a.optDouble(0, 0.8).toFloat().coerceIn(0f, 1f); state.player.changeVolume(v) }
            "setStyle" -> state.setOsStyle(a.optString(0))
            "setAccent" -> state.setLhAccent(a.optString(0))
            "lsSet" -> prefs.edit().putString(a.optString(0), a.optString(1)).apply()
            "lsDel" -> prefs.edit().remove(a.optString(0)).apply()
            "revyOpenWebsiteInBrowser" -> { RevyRequests.browserUrl = a.optString(0); wm.open("browser"); return "1" }
            "rrSearchYoutubeInVideo", "rrSearchYoutubeInVideo95" -> { RevyRequests.videoSource = "youtube"; RevyRequests.videoQuery = a.optString(0); wm.open("video") }
            "submit", "click" -> {
                val vals = JSONObject(a.optString(1, "{}"))
                when (a.optString(0)) {
                    "radio-youtube-music-form" -> { RevyRequests.musicQuery = vals.optString("radio-youtube-music-query"); wm.open("player") }
                    "video95-youtube-search-form" -> { RevyRequests.videoSource = "youtube"; RevyRequests.videoQuery = vals.optString("video95-youtube-query"); wm.open("video") }
                    "video95-search-form" -> { RevyRequests.videoSource = "archive"; RevyRequests.videoQuery = vals.optString("video95-query"); wm.open("video") }
                    "video95-youtube-load-url" -> { RevyRequests.videoSource = "youtube"; RevyRequests.videoQuery = vals.optString("video95-youtube-url-input"); wm.open("video") }
                    "emu-archive-search-form" -> { RevyRequests.emuQuery = vals.optString("emu-archive-query"); wm.open("emulator") }
                    else -> return "0"
                }
            }
            else -> return "0"
        }
        return ""
    }

    private val known = setOf("vol-slider", "lcd-track-name", "audio", "radio-youtube-music-form", "radio-youtube-music-query",
        "video95-youtube-search-form", "video95-youtube-query", "video95-search-form", "video95-query", "video95-youtube-url-input",
        "video95-youtube-load-url", "emu-archive-search-form", "emu-archive-query")

    private fun read(key: String): String = when {
        key.startsWith("has:") -> { val id = key.removePrefix("has:"); if (id in known || (id.startsWith("win-") && appIdFor(id) != null)) "1" else "0" }
        key.startsWith("ls:") -> prefs.getString(key.removePrefix("ls:"), null) ?: "\u0000"
        key == "track" -> state.player.deckIndex.toString()
        key == "track-name" -> AmpEngine.text("lcd-track-name")
        key == "volume" -> state.player.volume.toString()
        key == "paused" -> if (state.player.isPlaying) "0" else "1"
        key == "battery" -> if (state.lowPower) "1" else "0"
        key == "style" -> state.osStyle
        key == "accent" -> state.lhAccent
        key == "lang" -> java.util.Locale.getDefault().toLanguageTag()
        else -> ""
    }

    private fun engine(): QuickJs = js ?: QuickJs.create().also { q ->
        q.set("host", RevyHost::class.java, host)
        q.evaluate(appCtx.assets.open("revy/revy-bundle.js").bufferedReader().use { it.readText() }, "revy-bundle.js")
        js = q
    }

    class Answer(val kind: String, val reply: String, val footer: String)

    /** revyHandle(message, history): a built-in reply, or "search" when the site would look it up on the web. */
    suspend fun handle(message: String, history: List<ChatTurn>): Answer = withContext(dispatcher) {
        val hist = JSONArray().apply { history.forEach { put(JSONObject().put("role", it.role).put("content", it.content.take(700))) } }
        val out = engine().evaluate("revyHandle(${JSONObject.quote(message)}, ${JSONObject.quote(hist.toString())})", "call") as? String ?: "{}"
        val o = JSONObject(out)
        Answer(o.optString("kind", "reply"), o.optString("reply"), o.optString("footer"))
    }
}
