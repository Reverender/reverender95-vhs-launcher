package com.reverender.r95

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.content.edit
import com.reverender.r95.audio.AudioBus
import com.reverender.r95.audio.FxSettings
import com.reverender.r95.audio.PlayerConnection
import com.reverender.r95.core.Catalog
import com.reverender.r95.core.Themes
import com.reverender.r95.core.VaporTheme
import com.reverender.r95.net.ChatTurn
import com.reverender.r95.net.RevySource
import org.json.JSONArray
import org.json.JSONObject

data class ChatMessage(val role: String, val text: String, val sources: List<RevySource> = emptyList())
data class StickyNote(val text: String, val color: String)
data class TodoItem(val text: String, val done: Boolean, val star: Boolean = false)

enum class TaskbarPos { BOTTOM, TOP, LEFT, RIGHT }

/** All user settings and app data, persisted in SharedPreferences, exposed as Compose state. */
class AppState(context: Context, val player: PlayerConnection) {
    private val prefs = context.getSharedPreferences("r95", Context.MODE_PRIVATE).also { p ->
        // v2 = the website's Longhorn defaults: reset look settings saved by the first build once.
        if (p.getInt("ui_v", 1) < 2) p.edit { remove("wallpaper"); remove("clock_face"); remove("scanlines"); remove("tb_pos"); putInt("ui_v", 2) }
    }
    private fun b(k: String, d: Boolean) = prefs.getBoolean(k, d)
    private fun putB(k: String, v: Boolean) = prefs.edit { putBoolean(k, v) }

    // ---------------------------------------------------------------- look
    var themeId by mutableStateOf(prefs.getString("theme", "vaporwave") ?: "vaporwave"); private set
    val theme: VaporTheme get() = Themes.byId(themeId)
    fun setTheme(id: String) { themeId = id; prefs.edit { putString("theme", id) } }
    /** cycleWallpaper / prevWallpaper: the site's ◀ ▶ order. */
    fun cycleWallpaper(dir: Int) {
        val order = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 24, 25, 26, 27, 28, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 11, 12)
        val y = order.indexOf(wallpaper)
        setWallpaper(if (y < 0 || osWallpaper != null) order[if (dir > 0) 0 else order.size - 1] else order[(y + dir).mod(order.size)])
    }
    fun cycleTheme(dir: Int) { val i = Themes.all.indexOfFirst { it.id == themeId }; setTheme(Themes.all[(i + dir).mod(Themes.all.size)].id) }

    /** Index into Scenes.all, or -1 when a solid colour is shown. */
    @set:JvmName("setWallpaperInternal") var wallpaper by mutableIntStateOf(prefs.getInt("wallpaper", 29)); private set
    var solidColor by mutableLongStateOf(prefs.getLong("solid", 0xFF008080)); private set
    fun setWallpaper(i: Int) { clearOsWallpaper(); wallpaper = i; prefs.edit { putInt("wallpaper", i) } }
    fun setSolid(c: Long) { clearOsWallpaper(); solidColor = c; wallpaper = -1; prefs.edit { putLong("solid", c); putInt("wallpaper", -1) } }
    /** A Style Wallpaper (rr-os-styles k()): shown instead of the animated world until another background is picked. */
    var osWallpaper by mutableStateOf(prefs.getString("os_wallpaper", null)?.takeIf { com.reverender.r95.ui.viz.site.OsWallpaper.find(it) != null }); private set
    fun setOsWallpaper(id: String, user: Boolean = true) {
        val info = com.reverender.r95.ui.viz.site.OsWallpaper.find(id) ?: return
        osWallpaper = id; prefs.edit { putString("os_wallpaper", id) }
        if (user && osStyle == "longhorn" && info.style == osStyle && info.accent != null) setLhAccent(info.accent)
    }
    private fun clearOsWallpaper() { if (osWallpaper != null) { osWallpaper = null; prefs.edit { remove("os_wallpaper") } } }

    val bgLayers = mutableStateMapOf<String, Boolean>().apply {
        // first run: the website's default layers
        (prefs.getString("bg_layers", null) ?: com.reverender.r95.ui.viz.site.SiteWallpaper.defaultLayers.joinToString(","))
            .split(",").filter { it.isNotBlank() }.forEach { put(it, true) }
    }
    fun toggleLayer(id: String) {
        if (bgLayers[id] == true) bgLayers.remove(id) else bgLayers[id] = true
        prefs.edit { putString("bg_layers", bgLayers.keys.joinToString(",")) }
    }

    @set:JvmName("setAnimatedWallpaperInternal") var animatedWallpaper by mutableStateOf(b("anim_wall", true)); private set
    fun setAnimatedWallpaper(v: Boolean) { animatedWallpaper = v; putB("anim_wall", v) }
    @set:JvmName("setReactiveSunInternal") var reactiveSun by mutableStateOf(b("reactive_sun", true)); private set
    fun setReactiveSun(v: Boolean) { reactiveSun = v; putB("reactive_sun", v) }
    @set:JvmName("setScanlinesInternal") var scanlines by mutableStateOf(b("scanlines", false)); private set
    fun setScanlines(v: Boolean) { scanlines = v; putB("scanlines", v) }

    /** OS look (rr-os-style-v1): "longhorn" or "win95", and the Longhorn accent. */
    @set:JvmName("setOsStyleInternal") var osStyle by mutableStateOf(prefs.getString("os_style", "longhorn") ?: "longhorn"); private set
    fun setOsStyle(v: String) { osStyle = v; prefs.edit { putString("os_style", v) } }
    @set:JvmName("setLhAccentInternal") var lhAccent by mutableStateOf(prefs.getString("lh_accent", "slate") ?: "slate"); private set
    fun setLhAccent(v: String) { lhAccent = v; prefs.edit { putString("lh_accent", v) } }

    /**
     * What the site does on every visit (unless the device is in battery mode): pick Longhorn 75% / Win95 25%
     * (Longhorn with the Slate accent), start on the mallsoft theme, and pick a random animated wallpaper.
     */
    fun startupPicks() {
        if (lowPower) return
        setOsStyle(if (Math.random() < 0.75) "longhorn" else "win95")
        if (osStyle == "longhorn") setLhAccent("slate")
        setTheme("mallsoft")
        // each launch steps to the next of these four worlds: Helios Mall, Aether Spires, Prism Citadel, Low-Poly Dunes
        val list = intArrayOf(2, 24, 25, 20)
        if (osWallpaper == null) {
            val n = prefs.getInt("startup_wall_idx", -1) + 1
            prefs.edit { putInt("startup_wall_idx", n % list.size) }
            setWallpaper(list[n % list.size])
        }
    }
    @set:JvmName("setBassGlitchInternal") var bassGlitch by mutableStateOf(b("bass_glitch", false)); private set
    fun setBassGlitch(v: Boolean) { bassGlitch = v; putB("bass_glitch", v) }

    /** Battery saver: 30 fps visuals, static wallpaper, no glitch / wobble. */
    @set:JvmName("setLowPowerInternal") var lowPower by mutableStateOf(b("low_power", false)); private set
    fun setLowPower(v: Boolean) { lowPower = v; putB("low_power", v) }

    // ---------------------------------------------------------------- desktop
    @set:JvmName("setDesktopClockInternal") var desktopClock by mutableStateOf(b("desk_clock", true)); private set
    fun setDesktopClock(v: Boolean) { desktopClock = v; putB("desk_clock", v) }
    @set:JvmName("setClockFaceInternal") var clockFace by mutableStateOf((prefs.getString("clock_face", "classic") ?: "classic").let { f -> if (com.reverender.r95.ui.viz.ClockFaces.analog.any { it.id == f }) f else "classic" }); private set
    /** Windows 95 desktop-clock style: not stored, every visit starts with Neon 1989. */
    @set:JvmName("setW95ClockInternal") var w95Clock by mutableStateOf("neon"); private set
    fun setW95Clock(v: String) { w95Clock = v }
    fun setClockFace(f: String) { clockFace = f; prefs.edit { putString("clock_face", f) } }

    @set:JvmName("setWindowOpacityInternal") var windowOpacity by mutableFloatStateOf(prefs.getFloat("win_opacity", 1f)); private set
    fun setWindowOpacity(v: Float) { windowOpacity = v; prefs.edit { putFloat("win_opacity", v) } }

    @set:JvmName("setTaskbarPosInternal") var taskbarPos by mutableStateOf(TaskbarPos.entries.getOrElse(prefs.getInt("tb_pos", 0)) { TaskbarPos.BOTTOM }); private set
    fun setTaskbarPos(p: TaskbarPos) { taskbarPos = p; prefs.edit { putInt("tb_pos", p.ordinal) } }
    @set:JvmName("setPinTaskbarInternal") var pinTaskbar by mutableStateOf(b("tb_pin", true)); private set
    fun setPinTaskbar(v: Boolean) { pinTaskbar = v; putB("tb_pin", v) }
    var taskbarHideSec by mutableIntStateOf(prefs.getInt("tb_hide60", 60)); private set
    /** Screensaver "Preview" button (bumped to show it now). */
    var saverPreview by mutableIntStateOf(0)
    fun setTaskbarHide(v: Int) { taskbarHideSec = v; prefs.edit { putInt("tb_hide60", v) } }

    @set:JvmName("setWobbleInternal") var wobble by mutableStateOf(b("wobble", true)); private set
    fun setWobble(v: Boolean) { wobble = v; putB("wobble", v) }

    /** Customize → Fun → Vapor toys (all off by default, like the site). */
    val toys = mutableStateMapOf<String, Boolean>().apply {
        prefs.getString("toys", "")!!.split(",").filter { it.isNotBlank() }.forEach { put(it, true) }
    }
    fun toggleToy(id: String) {
        if (toys[id] == true) toys.remove(id) else toys[id] = true
        prefs.edit { putString("toys", toys.keys.joinToString(",")) }
    }
    @set:JvmName("setUiSfxInternal") var uiSfx by mutableStateOf(b("ui_sfx", true)); private set
    fun setUiSfx(v: Boolean) { uiSfx = v; putB("ui_sfx", v) }
    @set:JvmName("setMuteHumInternal") var muteHum by mutableStateOf(b("mute_hum", false)); private set
    fun setMuteHum(v: Boolean) { muteHum = v; putB("mute_hum", v) }

    @set:JvmName("setScreensaverInternal") var screensaver by mutableStateOf(b("saver_on", false)); private set
    fun setScreensaver(v: Boolean) { screensaver = v; putB("saver_on", v) }
    @set:JvmName("setScreensaverMinInternal") var screensaverMin by mutableIntStateOf(prefs.getInt("saver_min", 5)); private set
    fun setScreensaverMin(v: Int) { screensaverMin = v; prefs.edit { putInt("saver_min", v) } }

    var latencyMs by mutableIntStateOf(prefs.getInt("latency", 280)); private set
    fun setLatency(v: Int) { latencyMs = v; AudioBus.outputLatencyMs = v; prefs.edit { putInt("latency", v) } }

    @set:JvmName("setRandomResumeInternal") var randomResume by mutableStateOf(b("random_resume", true)); private set
    fun setRandomResume(v: Boolean) { randomResume = v; putB("random_resume", v) }

    // ---------------------------------------------------------------- EQ
    val eqGains = mutableStateListOf<Float>().apply {
        val saved = prefs.getString("eq_gains", null)?.split(",")?.mapNotNull { it.toFloatOrNull() }
        addAll(if (saved != null && saved.size == Catalog.eqFrequencies.size) saved else List(Catalog.eqFrequencies.size) { 0f })
    }
    @set:JvmName("setEqPreampInternal") var eqPreamp by mutableFloatStateOf(prefs.getFloat("eq_preamp", 0f)); private set
    @set:JvmName("setEqEnabledInternal") var eqEnabled by mutableStateOf(b("eq_on", true)); private set
    var eqPreset by mutableStateOf(prefs.getString("eq_preset", "FLAT") ?: "FLAT"); private set

    fun setEqBand(i: Int, db: Float) { eqGains[i] = db; eqPreset = "CUSTOM"; pushEq() }
    fun setEqPreamp(db: Float) { eqPreamp = db; pushEq() }
    fun setEqEnabled(v: Boolean) { eqEnabled = v; pushEq() }
    fun applyEqPreset(name: String) {
        val g = Catalog.eqPresets[name] ?: return
        g.forEachIndexed { i, v -> eqGains[i] = v }
        eqPreset = name
        pushEq()
    }

    private fun pushEq() {
        AudioBus.eq.update(eqGains.toFloatArray(), eqPreamp, eqEnabled)
        prefs.edit {
            putString("eq_gains", eqGains.joinToString(","))
            putFloat("eq_preamp", eqPreamp)
            putBoolean("eq_on", eqEnabled)
            putString("eq_preset", eqPreset)
        }
    }

    @set:JvmName("setInputGainInternal") var inputGain by mutableFloatStateOf(prefs.getFloat("in_gain", 1f)); private set
    fun setInputGain(v: Float) { inputGain = v; AudioBus.inputGain = v; prefs.edit { putFloat("in_gain", v) } }

    // ---------------------------------------------------------------- DSP_FX + CASSETTE
    /** Bumped whenever an FX value changes so the rack UI recomposes. */
    var fxTick by mutableIntStateOf(0); private set
    var fxPreset by mutableStateOf("clean"); private set
    fun fx(edit: FxSettings.() -> Unit) { AudioBus.fx.edit(); AudioBus.fx.touch(); fxPreset = "custom"; fxTick++ }
    fun applyFxPreset(name: String) {
        val speed = FxSettings.applyPreset(AudioBus.fx, name)
        player.changeSpeed(speed)
        fxPreset = name; fxTick++
    }
    var tapeType by mutableStateOf("type1"); private set
    fun setTape(t: String) { tapeType = t; FxSettings.applyTape(AudioBus.fx, t); fxTick++ }

    // ---------------------------------------------------------------- WRITE_95
    var notepadText by mutableStateOf(prefs.getString("notepad", "") ?: ""); private set
    fun setNotepad(t: String) { notepadText = t; prefs.edit { putString("notepad", t) } }
    @set:JvmName("setNotepadNameInternal") var notepadName by mutableStateOf(prefs.getString("notepad_name", "Untitled") ?: "Untitled"); private set
    fun setNotepadName(n: String) { notepadName = n; prefs.edit { putString("notepad_name", n) } }
    @set:JvmName("setNotepadSizeInternal") var notepadSize by mutableIntStateOf(prefs.getInt("notepad_size13", 13)); private set
    fun setNotepadSize(v: Int) { notepadSize = v.coerceIn(10, 26); prefs.edit { putInt("notepad_size13", notepadSize) } }
    @set:JvmName("setNotepadWrapInternal") var notepadWrap by mutableStateOf(b("notepad_wrap", true)); private set
    fun setNotepadWrap(v: Boolean) { notepadWrap = v; putB("notepad_wrap", v) }

    // ---------------------------------------------------------------- STICKY_NOTES
    val stickies = mutableStateListOf<StickyNote>().apply {
        val arr = runCatching { JSONArray(prefs.getString("stickies", "[]")) }.getOrNull() ?: JSONArray()
        for (i in 0 until arr.length()) arr.optJSONObject(i)?.let { add(StickyNote(it.optString("text"), it.optString("color", "yellow"))) }
        if (isEmpty()) add(StickyNote("", "yellow"))
    }
    fun saveStickies() {
        val arr = JSONArray()
        stickies.forEach { arr.put(JSONObject().put("text", it.text).put("color", it.color)) }
        prefs.edit { putString("stickies", arr.toString()) }
    }

    // ---------------------------------------------------------------- PLANNER_95
    val planner = mutableStateMapOf<String, String>().apply {
        val o = runCatching { JSONObject(prefs.getString("planner", "{}")!!) }.getOrNull() ?: JSONObject()
        o.keys().forEach { k -> put(k, o.optString(k)) }
    }
    fun setPlan(day: String, text: String) {
        if (text.isBlank()) planner.remove(day) else planner[day] = text
        val o = JSONObject(); planner.forEach { (k, v) -> o.put(k, v) }
        prefs.edit { putString("planner", o.toString()) }
    }

    // ---------------------------------------------------------------- TASKS_95
    val todos = mutableStateListOf<TodoItem>().apply {
        val arr = runCatching { JSONArray(prefs.getString("todos", "[]")) }.getOrNull() ?: JSONArray()
        for (i in 0 until arr.length()) arr.optJSONObject(i)?.let { add(TodoItem(it.optString("text"), it.optBoolean("done"), it.optBoolean("star"))) }
    }
    fun saveTodos() {
        val arr = JSONArray()
        todos.forEach { arr.put(JSONObject().put("text", it.text).put("done", it.done).put("star", it.star)) }
        prefs.edit { putString("todos", arr.toString()) }
    }

    // ---------------------------------------------------------------- FOCUS_95
    var focusWork by mutableIntStateOf(prefs.getInt("focus_work", 25)); private set
    var focusBreak by mutableIntStateOf(prefs.getInt("focus_break", 5)); private set
    var focusDone by mutableIntStateOf(prefs.getInt("focus_done", 0)); private set
    fun setFocus(work: Int, brk: Int) { focusWork = work.coerceIn(1, 120); focusBreak = brk.coerceIn(1, 60); prefs.edit { putInt("focus_work", focusWork); putInt("focus_break", focusBreak) } }
    fun addFocusDone() { focusDone++; prefs.edit { putInt("focus_done", focusDone) } }

    // ---------------------------------------------------------------- generic JSON blobs (pet, games)
    fun loadBlob(key: String): String? = prefs.getString("blob_$key", null)
    fun saveBlob(key: String, json: String) { prefs.edit { putString("blob_$key", json) } }

    // ---------------------------------------------------------------- Revy (session only, like the website)
    val chat = mutableStateListOf(
        ChatMessage("assistant", "Hi, I'm Revy 🤖 Ask me anything and I'll search the web and summarise what I find."),
    )
    var revyBusy by mutableStateOf(false)
    fun chatHistory(): List<ChatTurn> = chat.map { ChatTurn(it.role, it.text) }

    init {
        AudioBus.outputLatencyMs = latencyMs
        AudioBus.inputGain = inputGain
        AudioBus.eq.update(eqGains.toFloatArray(), eqPreamp, eqEnabled)
        FxSettings.applyTape(AudioBus.fx, tapeType)
    }
}

val LocalAppState = staticCompositionLocalOf<AppState> { error("AppState not provided") }
