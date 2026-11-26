package com.reverender.r95.os

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Switches for the Android integrations (haptics, gyro warp, system EQ, MIDI, dock screensaver …).
 * Kept apart from AppState because services, widgets and the live wallpaper read them without the UI.
 */
object OsPrefs {
    private var prefs: SharedPreferences? = null
    lateinit var app: Context; private set

    fun init(ctx: Context) {
        if (prefs != null) return
        app = ctx.applicationContext
        val p = app.getSharedPreferences("r95_os", Context.MODE_PRIVATE)
        prefs = p
        haptics = p.getBoolean("haptics", true)
        bassHaptics = p.getBoolean("bass_haptics", false)
        gyroWarp = p.getBoolean("gyro_warp", false)
        gyroDepth = p.getFloat("gyro_depth", 0.6f)
        gyroPlasma = p.getBoolean("gyro_plasma", true)
        systemReactive = p.getBoolean("system_reactive", false)
        globalEq = p.getBoolean("global_eq", false)
        dspMonitor = p.getBoolean("dsp_monitor", false)
        retroAlerts = p.getBoolean("retro_alerts", false)
        alertOverlay = p.getBoolean("alert_overlay", false)
        alertChime = p.getBoolean("alert_chime", true)
        dreamSaver = p.getString("dream_saver", "vumeter") ?: "vumeter"
        dreamTheme = p.getString("dream_theme", "vaporwave") ?: "vaporwave"
        liveWallpaper = p.getInt("live_wp", 5)
        liveTheme = p.getString("live_theme", "vaporwave") ?: "vaporwave"
        monitorScene = p.getString("monitor_scene", "wp:5") ?: "wp:5"
        midiMap = p.getString("midi_map", DEFAULT_MIDI) ?: DEFAULT_MIDI
        floatingApps = p.getBoolean("floating_apps", true)
        pinned = (p.getString("pinned_apps", "") ?: "").split('|').filter { it.isNotBlank() }
        swipeLeft = p.getString("swipe_left", "") ?: ""
        swipeRight = p.getString("swipe_right", "") ?: ""
        wallScale = p.getFloat("wall_scale", -1f)
        textScale = p.getFloat("text_scale", 1.12f)
        vhsBg = p.getLong("vhs_bg", 0xFF1A1AD0); vhsFg = p.getLong("vhs_fg", 0xFFFFFFFF)
        vhsCrt = p.getBoolean("vhs_crt", true); vhs24h = p.getBoolean("vhs_24h", false)
        vhsGlitch = p.getFloat("vhs_glitch", 0.5f); vhsSnow = p.getFloat("vhs_snow", 0.5f); vhsLines = p.getFloat("vhs_lines", 0.6f)
        // the old CRT ON/OFF switch was off: start the three CRT sliders at 0
        if (!vhsCrt && !p.contains("vhs_snow")) { vhsGlitch = 0f; vhsSnow = 0f; vhsLines = 0f; vhsCrt = true }
        vhsBlur = p.getFloat("vhs_blur", 0.35f); vhsBloom = p.getBoolean("vhs_bloom", true)
        runCatching {
            val a = org.json.JSONArray(p.getString("vhs_playlist", "[]"))
            for (i in 0 until a.length()) a.optJSONObject(i)?.let { o -> vhsPlaylist.add(o.optString("n") to o.optString("u")) }
        }
        vidFxOn = p.getBoolean("vhs_vid_fx", true)
        vhsTitle1 = p.getString("vhs_title1", null) ?: "VHS/VCR MENU"; vhsTitle2 = p.getString("vhs_title2", null) ?: "REVERENDER 95"
        vhsUiBlur = p.getFloat("vhs_ui_blur", 0f); vhsWall = p.getInt("vhs_wall", -1).takeIf { it < 3 } ?: -1; vhsVizTheme = p.getInt("vhs_viz", 0); vhsFont = p.getInt("vhs_font", 0); vhsWide = p.getString("vhs_wide", "LOAD") ?: "LOAD"
        vhsPic = p.getInt("vhs_pic", 0); vhsPicDim = p.getFloat("vhs_pic_dim", 0.35f)
        vidColor = p.getFloat("vhs_vid_color", 0.45f); vidBlur = p.getFloat("vhs_vid_blur", 0.25f); vidChroma = p.getFloat("vhs_vid_chroma", 0.4f); vidNoise = p.getFloat("vhs_vid_noise", 0.3f)
        vhsChroma = p.getFloat("vhs_chroma", 0.35f); vhsTrack = p.getFloat("vhs_track", 0.5f)
        vhsCurve = p.getFloat("vhs_curve_amt", 0.3f)
        vhsGrad = p.getInt("vhs_grad", -1); vhsFgMix = p.getBoolean("vhs_fg_mix", false); vhsHelpAuto = p.getBoolean("vhs_help_auto", false)
        vhsSwipe = listOf("up", "down", "left", "right").associateWith { p.getString("vhs_swipe_$it", "") ?: "" }
    }

    private fun put(f: SharedPreferences.Editor.() -> Unit) { prefs?.edit()?.apply { f(); apply() } }

    @set:JvmName("hapticsInternal") var haptics by mutableStateOf(true); private set
    fun setHaptics(v: Boolean) { haptics = v; put { putBoolean("haptics", v) } }
    @set:JvmName("bassHapticsInternal") var bassHaptics by mutableStateOf(false); private set
    fun setBassHaptics(v: Boolean) { bassHaptics = v; put { putBoolean("bass_haptics", v) } }

    @set:JvmName("gyroWarpInternal") var gyroWarp by mutableStateOf(false); private set
    fun setGyroWarp(v: Boolean) { gyroWarp = v; put { putBoolean("gyro_warp", v) }; GyroWarp.refresh() }
    @set:JvmName("gyroDepthInternal") var gyroDepth by mutableStateOf(0.6f); private set
    fun setGyroDepth(v: Float) { gyroDepth = v; put { putFloat("gyro_depth", v) } }
    @set:JvmName("gyroPlasmaInternal") var gyroPlasma by mutableStateOf(true); private set
    fun setGyroPlasma(v: Boolean) { gyroPlasma = v; put { putBoolean("gyro_plasma", v) }; GyroWarp.refresh() }

    /** visualizers follow everything the phone plays (Android's output visualizer, session 0) */
    @set:JvmName("systemReactiveInternal") var systemReactive by mutableStateOf(false); private set
    fun setSystemReactive(v: Boolean) { systemReactive = v; put { putBoolean("system_reactive", v) }; com.reverender.r95.audio.SystemVisualizer.refresh() }

    @set:JvmName("globalEqInternal") var globalEq by mutableStateOf(false); private set
    fun setGlobalEq(v: Boolean) { globalEq = v; put { putBoolean("global_eq", v) }; com.reverender.r95.audio.GlobalEq.refresh() }

    /** play other apps' captured audio back through the DSP rack */
    @set:JvmName("dspMonitorInternal") var dspMonitor by mutableStateOf(false); private set
    fun setDspMonitor(v: Boolean) { dspMonitor = v; put { putBoolean("dsp_monitor", v) } }

    @set:JvmName("retroAlertsInternal") var retroAlerts by mutableStateOf(false); private set
    fun setRetroAlerts(v: Boolean) { retroAlerts = v; put { putBoolean("retro_alerts", v) } }
    @set:JvmName("alertOverlayInternal") var alertOverlay by mutableStateOf(false); private set
    fun setAlertOverlay(v: Boolean) { alertOverlay = v; put { putBoolean("alert_overlay", v) } }
    @set:JvmName("alertChimeInternal") var alertChime by mutableStateOf(true); private set
    fun setAlertChime(v: Boolean) { alertChime = v; put { putBoolean("alert_chime", v) } }

    @set:JvmName("dreamSaverInternal") var dreamSaver by mutableStateOf("vumeter"); private set
    fun setDreamSaver(v: String) { dreamSaver = v; put { putString("dream_saver", v) } }
    @set:JvmName("dreamThemeInternal") var dreamTheme by mutableStateOf("vaporwave"); private set
    fun setDreamTheme(v: String) { dreamTheme = v; put { putString("dream_theme", v) } }

    @set:JvmName("liveWallpaperInternal") var liveWallpaper by mutableStateOf(5); private set
    fun setLiveWallpaper(v: Int) { liveWallpaper = v; put { putInt("live_wp", v) } }
    @set:JvmName("liveThemeInternal") var liveTheme by mutableStateOf("vaporwave"); private set
    fun setLiveTheme(v: String) { liveTheme = v; put { putString("live_theme", v) } }

    /** "wp:<id>" for a site wallpaper, or a visualizer id */
    @set:JvmName("monitorSceneInternal") var monitorScene by mutableStateOf("wp:5"); private set
    fun setMonitorScene(v: String) { monitorScene = v; put { putString("monitor_scene", v) } }

    @set:JvmName("floatingAppsInternal") var floatingApps by mutableStateOf(true); private set
    fun setFloatingApps(v: Boolean) { floatingApps = v; put { putBoolean("floating_apps", v) } }

    /** phone apps pinned to the taskbar, as flattened component names, left to right */
    @set:JvmName("pinnedInternal") var pinned by mutableStateOf(listOf<String>()); private set
    fun setPinnedList(v: List<String>) { pinned = v; put { putString("pinned_apps", v.joinToString("|")) } }
    fun togglePin(c: String) = setPinnedList(if (c in pinned) pinned - c else pinned + c)

    /** desktop swipe shortcuts: "" (off), "app:<window id>" or "pkg:<component>" */
    @set:JvmName("swipeLeftInternal") var swipeLeft by mutableStateOf(""); private set
    fun setSwipeLeft(v: String) { swipeLeft = v; put { putString("swipe_left", v) } }
    @set:JvmName("swipeRightInternal") var swipeRight by mutableStateOf(""); private set
    fun setSwipeRight(v: String) { swipeRight = v; put { putString("swipe_right", v) } }

    // ---------------------------------------------------------------- VHS shell
    @set:JvmName("vhsBgInternal") var vhsBg by mutableStateOf(0xFF1A1AD0); private set
    @set:JvmName("vhsFgInternal") var vhsFg by mutableStateOf(0xFFFFFFFF); private set
    fun setVhsColors(bg: Long, fg: Long) { vhsBg = bg; vhsFg = fg; put { putLong("vhs_bg", bg); putLong("vhs_fg", fg) } }
    @set:JvmName("vhsCrtInternal") var vhsCrt by mutableStateOf(true); private set
    fun setVhsCrt(v: Boolean) { vhsCrt = v; put { putBoolean("vhs_crt", v) } }
    /** VHS CRT tube curve at the corners: 0 = flat … 1 = very round */
    @set:JvmName("vhsCurveInternal") var vhsCurve by mutableStateOf(0.3f); private set
    fun setVhsCurve(v: Float) { vhsCurve = v.coerceIn(0f, 1f); put { putFloat("vhs_curve_amt", vhsCurve) } }
    @set:JvmName("vhs24hInternal") var vhs24h by mutableStateOf(false); private set
    fun setVhs24h(v: Boolean) { vhs24h = v; put { putBoolean("vhs_24h", v) } }
    /** VHS tape glitches (tracking noise, tearing, static, colour fringe): 0 = clean … 1 = worn-out tape */
    @set:JvmName("vhsGlitchInternal") var vhsGlitch by mutableStateOf(0.5f); private set
    /** CRT motion blur: phosphor persistence on moving pictures (0 = off) */
    @set:JvmName("vhsBlurInternal") var vhsBlur by mutableStateOf(0.35f); private set
    fun setVhsBlur(v: Float) { vhsBlur = v.coerceIn(0f, 1f); put { putFloat("vhs_blur", vhsBlur) } }
    /** soft glow around the OSD text, like a bright CRT */
    @set:JvmName("vhsBloomInternal") var vhsBloom by mutableStateOf(true); private set
    /** chromatic aberration: red / cyan fringes beside the OSD text, 0..1 */
    @set:JvmName("vhsChromaInternal") var vhsChroma by mutableStateOf(0.35f); private set
    fun setVhsChroma(v: Float) { vhsChroma = v.coerceIn(0f, 1f); put { putFloat("vhs_chroma", vhsChroma) } }
    /** VCR TRACKING knob 0..1: 0.5 is a clean picture and sound, either side drifts out of alignment */
    @set:JvmName("vhsTrackInternal") var vhsTrack by mutableStateOf(0.5f); private set
    fun setVhsTrack(v: Float) { vhsTrack = v.coerceIn(0f, 1f); put { putFloat("vhs_track", vhsTrack) } }
    /** VHS video player effects (0..1): colour (0.5 = normal, more = saturated), softness, colour fringe, snow */
    @set:JvmName("vidColorInternal") var vidColor by mutableStateOf(0.45f); private set
    @set:JvmName("vidBlurInternal") var vidBlur by mutableStateOf(0.25f); private set
    @set:JvmName("vidChromaInternal") var vidChroma by mutableStateOf(0.4f); private set
    @set:JvmName("vidNoiseInternal") var vidNoise by mutableStateOf(0.3f); private set
    fun setVid(key: String, v: Float) {
        val x = v.coerceIn(0f, 1f)
        when (key) { "color" -> vidColor = x; "blur" -> vidBlur = x; "chroma" -> vidChroma = x; "noise" -> vidNoise = x }
        put { putFloat("vhs_vid_$key", x) }
    }
    /** the VHS playlist: (name, content uri) of files from the phone, kept between launches */
    val vhsPlaylist = androidx.compose.runtime.mutableStateListOf<Pair<String, String>>()
    fun savePlaylist() {
        val a = org.json.JSONArray()
        vhsPlaylist.forEach { (n, u) -> a.put(org.json.JSONObject().put("n", n).put("u", u)) }
        put { putString("vhs_playlist", a.toString()) }
    }

    /** the two lines in the VHS menu's header box */
    @set:JvmName("vhsTitle1Internal") var vhsTitle1 by mutableStateOf("VHS/VCR MENU"); private set
    @set:JvmName("vhsTitle2Internal") var vhsTitle2 by mutableStateOf("REVERENDER 95"); private set
    fun setVhsTitles(a: String, b: String) { vhsTitle1 = a; vhsTitle2 = b; put { putString("vhs_title1", a); putString("vhs_title2", b) } }
    /** HD rumble: the vibration motor plays the music's low end */
    /** CRT motion blur on the whole interface (text, keys, lists); 0 = off */
    @set:JvmName("vhsUiBlurInternal") var vhsUiBlur by mutableStateOf(0f); private set
    fun setVhsUiBlur(v: Float) { vhsUiBlur = v.coerceIn(0f, 1f); put { putFloat("vhs_ui_blur", vhsUiBlur) } }
    /** VHS 3D wallpaper: -1 = none (solid colour / picture), else an index into VhsWalls.names */
    @set:JvmName("vhsWallInternal") var vhsWall by mutableStateOf(-1); private set
    fun setVhsWall(v: Int) { vhsWall = v; put { putInt("vhs_wall", v) } }
    /** the VHS home visualizer's look (hold the bars to change) */
    @set:JvmName("vhsVizThemeInternal") var vhsVizTheme by mutableStateOf(0); private set
    fun setVhsVizTheme(v: Int) { vhsVizTheme = v; put { putInt("vhs_viz", v) } }
    /** VHS lettering style (see VHS_FONTS) */
    @set:JvmName("vhsFontInternal") var vhsFont by mutableStateOf(0); private set
    fun setVhsFont(v: Int) { vhsFont = v; put { putInt("vhs_font", v) } }
    /** wide screens (landscape, unfolded): what sits next to the home menu — LOAD, EQ, PLAYLIST, SETUP, APPS or OFF */
    @set:JvmName("vhsWideInternal") var vhsWide by mutableStateOf("LOAD"); private set
    fun setVhsWide(v: String) { vhsWide = v; put { putString("vhs_wide", v) } }

    /** video deck: all picture effects (and the CRT over the video) on or off */
    @set:JvmName("vidFxOnInternal") var vidFxOn by mutableStateOf(true); private set
    fun setVidFxOn(v: Boolean) { vidFxOn = v; put { putBoolean("vhs_vid_fx", v) } }

    /** own background picture (0 = the solid colour; bumped each time a new picture is chosen) */
    @set:JvmName("vhsPicInternal") var vhsPic by mutableStateOf(0); private set
    fun setVhsPic(v: Int) { vhsPic = v; put { putInt("vhs_pic", v) } }
    /** how much of the background colour lies over the picture, so the OSD stays readable */
    @set:JvmName("vhsPicDimInternal") var vhsPicDim by mutableStateOf(0.35f); private set
    fun setVhsPicDim(v: Float) { vhsPicDim = v.coerceIn(0f, 0.9f); put { putFloat("vhs_pic_dim", vhsPicDim) } }
    fun setVhsBloom(v: Boolean) { vhsBloom = v; put { putBoolean("vhs_bloom", v) } }
    /** VHS multicolour background: -1 = the plain BACK colour, else an index into VHS_GRADS */
    @set:JvmName("vhsGradInternal") var vhsGrad by mutableStateOf(-1); private set
    fun setVhsGrad(v: Int) { vhsGrad = v; put { putInt("vhs_grad", v) } }
    /** VHS text in a pastel rainbow (each letter its own colour) */
    @set:JvmName("vhsFgMixInternal") var vhsFgMix by mutableStateOf(false); private set
    fun setVhsFgMix(v: Boolean) { vhsFgMix = v; put { putBoolean("vhs_fg_mix", v) } }
    /** VHS help bars: false = always shown, true = slide away after 2 s (swipe up from the bottom brings them back) */
    @set:JvmName("vhsHelpAutoInternal") var vhsHelpAuto by mutableStateOf(false); private set
    fun setVhsHelpAuto(v: Boolean) { vhsHelpAuto = v; put { putBoolean("vhs_help_auto", v) } }
    /** VHS CRT: analog snow, specks and tape drop-outs (0 … 1) */
    @set:JvmName("vhsSnowInternal") var vhsSnow by mutableStateOf(0.5f); private set
    fun setVhsSnow(v: Float) { vhsSnow = v.coerceIn(0f, 1f); put { putFloat("vhs_snow", vhsSnow) } }
    /** VHS CRT: scanlines, the rolling bright band and the head-switching strip at the bottom (0 … 1) */
    @set:JvmName("vhsLinesInternal") var vhsLines by mutableStateOf(0.6f); private set
    fun setVhsLines(v: Float) { vhsLines = v.coerceIn(0f, 1f); put { putFloat("vhs_lines", vhsLines) } }
    fun setVhsGlitch(v: Float) { vhsGlitch = v.coerceIn(0f, 1f); put { putFloat("vhs_glitch", vhsGlitch) } }
    /** swipe up/down/left/right → "" | "vhs:setup" | "vhs:search" | "pkg:<component>" */
    @set:JvmName("vhsSwipeInternal") var vhsSwipe by mutableStateOf(mapOf<String, String>()); private set
    fun setVhsSwipe(dir: String, v: String) { vhsSwipe = vhsSwipe + (dir to v); put { putString("vhs_swipe_$dir", v) } }

    /** text size across the desktop (1 = the website's size) */
    @set:JvmName("textScaleInternal") var textScale by mutableStateOf(1.12f); private set
    fun setTextScale(v: Float) { textScale = v; put { putFloat("text_scale", v) } }

    /** wallpaper render resolution (0.35–1), or -1 = automatic */
    @set:JvmName("wallScaleInternal") var wallScale by mutableStateOf(-1f); private set
    fun setWallScale(v: Float) { wallScale = v; put { putFloat("wall_scale", v) } }

    /** "cc=param,cc=param" — the MIDI learn table */
    @set:JvmName("midiMapInternal") var midiMap by mutableStateOf(""); private set
    fun setMidiMap(v: String) { midiMap = v; put { putString("midi_map", v) } }
    const val DEFAULT_MIDI = "1=lpf,7=dmix,10=pan,71=dfdbk,72=crush,73=reverb,74=lpf,91=reverb,93=chorus"
}
