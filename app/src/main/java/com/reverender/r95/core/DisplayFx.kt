package com.reverender.r95.core

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import com.reverender.r95.AppState
import com.reverender.r95.ui.viz.js.Dom

/**
 * Desktop Fun FX & display effects, ported from rr-startup-visuals.js: bloom, chromatic aberration (RGB
 * split), neon pointer trails, click sparks, hover glow, and the 3-mode performance cycle
 * (N / w / J / Z / rt / it and the Yt, de, he, me, gt, tt steps with their j() texts).
 */
object DisplayFx {
    private lateinit var state: AppState
    private lateinit var appCtx: Context
    private val prefs by lazy { appCtx.getSharedPreferences("r95_fun_fx", Context.MODE_PRIVATE) }

    @set:JvmName("setTrailInternal") var trail by mutableStateOf(false); private set
    @set:JvmName("setSparksInternal") var sparks by mutableStateOf(false); private set
    @set:JvmName("setHoverInternal") var hover by mutableStateOf(false); private set
    @set:JvmName("setBloomInternal") var bloom by mutableStateOf(true); private set
    @set:JvmName("setBloomStrengthInternal") var bloomStrength by mutableIntStateOf(50); private set
    @set:JvmName("setChromaticInternal") var chromatic by mutableStateOf(false); private set
    @set:JvmName("setChromaticStrengthInternal") var chromaticStrength by mutableIntStateOf(0); private set
    var fullEffect by mutableStateOf(false); private set
    /** basic, checking-standard, standard, checking-device-max, device-max, checking-full, full-pending, full */
    var mode by mutableStateOf("basic"); private set
    private var busy = false
    private var runtimeTier: String? = null

    class Caps(val tier: String, val maxBloom: Int, val maxChromaticStrength: Int, val maxOffset: Double, val visualizerRgbMax: Int)

    fun install(ctx: Context, s: AppState) {
        appCtx = ctx.applicationContext; state = s
        trail = prefs.getBoolean("trail", false); sparks = prefs.getBoolean("sparks", false); hover = prefs.getBoolean("hover", false)
        bloom = prefs.getBoolean("bloom", true); bloomStrength = prefs.getInt("bloom_strength", 50)
        val high = hardwareTier() == "high"
        chromatic = prefs.getBoolean("chromatic", high); chromaticStrength = prefs.getInt("chromatic_strength", if (high) 28 else 0)
        fullEffect = prefs.getBoolean("full", false)
        mode = prefs.getString("mode", "basic") ?: "basic"
        if (mode.startsWith("checking") || mode == "full-pending") mode = if (fullEffect) "full" else "basic"
    }

    private fun save() = prefs.edit().putBoolean("trail", trail).putBoolean("sparks", sparks).putBoolean("hover", hover)
        .putBoolean("bloom", bloom).putInt("bloom_strength", bloomStrength).putBoolean("chromatic", chromatic)
        .putInt("chromatic_strength", chromaticStrength).putBoolean("full", fullEffect).putString("mode", mode).apply()

    // ------------------------------------------------------------ device tier (N, w)
    /** N(): phones count as mobile, so mid at best; low with ≤ 4 GB memory or ≤ 4 cores. */
    fun hardwareTier(): String {
        val am = appCtx.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val mi = android.app.ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }
        val gb = mi.totalMem / 1_073_741_824.0
        val cores = Runtime.getRuntime().availableProcessors()
        val low = am.isLowRamDevice || gb <= 4.2 || cores <= 4
        return if (low) "low" else "mid"
    }
    private fun rank(t: String) = when (t) { "high" -> 2; "mid" -> 1; else -> 0 }
    private fun tierOf(n: Int) = if (n >= 2) "high" else if (n == 1) "mid" else "low"

    fun caps(): Caps {
        val t = hardwareTier()
        val e = runtimeTier ?: t
        val i = if (rank(e) < rank(t)) e else t
        val c = when (i) {
            "low" -> Caps(i, 48, 38, 1.05, 0)
            "mid" -> Caps(i, 72, 72, 2.15, 58)
            else -> Caps(i, 100, 100, 3.6, 100)
        }
        return if (fullEffect) Caps(c.tier, 100, 100, 3.6, 100) else c
    }

    /** Effect switch-off in battery mode (st() = 0). */
    private val active get() = !state.lowPower

    /** Bloom overlay: blur (css px at full size) and opacity, from le(). */
    fun bloomParams(): Pair<Float, Float>? {
        if (!active || !bloom) return null
        val e = minOf(bloomStrength, caps().maxBloom)
        if (e <= 0) return null
        val i = e / 100f; val n = i * i * (3 - 2 * i)
        val blur = 2.25f * (0.75f + 3.15f * n)
        return blur / 0.4f to 0.6075f * n
    }

    /** Chromatic offset in css px (red −m, blue +m, with a 0.08 vertical slant), from et(). */
    fun chromaticOffset(density: Float): Float {
        if (!active || !chromatic || chromaticStrength <= 0) return 0f
        val c = caps()
        var m = c.maxOffset * minOf(c.maxChromaticStrength, chromaticStrength) / 100.0
        if (density > 2.5f) m *= 0.82
        return m.toFloat()
    }

    fun setTrail(v: Boolean) { trail = v; save() }
    fun setSparks(v: Boolean) { sparks = v; save() }
    fun setHover(v: Boolean) { hover = v; save() }
    fun setBloom(v: Boolean) { bloom = v; markCustom(); save() }
    fun setBloomStrength(v: Int) { bloomStrength = v.coerceIn(0, 100); markCustom(); save() }
    fun setChromatic(v: Boolean) { chromatic = v; markCustom(); save() }
    fun setChromaticStrength(v: Int) { chromaticStrength = v.coerceIn(0, 100); markCustom(); save() }
    private fun markCustom() { if (!busy && mode in listOf("standard", "device-max", "full")) { fullEffect = false } }

    // ------------------------------------------------------------ frame test (J) + verdict (Z)
    class Report(val fps: Double, val p95: Double, val dropped: Double, val frames: Int)

    private suspend fun frameTest(ms: Long): Report {
        val u = ms.coerceIn(700, 1800)
        val gaps = ArrayList<Double>()
        var start = 0L; var last = 0L
        while (true) {
            val t = withFrameNanos { it }
            if (start == 0L) start = t
            if (last != 0L) gaps.add((t - last) / 1e6)
            last = t
            if ((t - start) / 1e6 >= u) break
        }
        val h = gaps.filter { it > 0 && it < 250 }
        val sorted = h.sorted()
        val avg = if (h.isNotEmpty()) h.average() else 1000.0 / 30
        val p95 = if (sorted.isNotEmpty()) sorted[minOf(sorted.size - 1, (sorted.size * 0.95).toInt())] else 1000.0 / 30
        val dropped = if (h.isNotEmpty()) h.count { it > 1000.0 / 45 }.toDouble() / h.size else 1.0
        return Report(minOf(240.0, 1000 / avg), p95, dropped, h.size)
    }

    private fun verdict(r: Report, full: Boolean): Boolean {
        val i = hardwareTier(); var n = rank(i)
        val o = r.fps; val s = r.p95; val u = r.dropped
        val bad = o < 39 || u > 0.34 || s > 48
        val meh = o < 47 || u > 0.23 || s > 40
        val ok = o < 53 || u > 0.15 || s > 32
        if (bad) n = maxOf(0, n - 2) else if (meh || ok) n = maxOf(0, n - 1)
        val capable = i != "low" && o >= 50 && u <= 0.18 && s <= 36 && r.frames >= 18
        if (full && !capable && n == rank(i)) n = maxOf(0, n - 1)
        runtimeTier = tierOf(n)
        return capable
    }

    private fun lcd(t: String) = Dom.el("lcd-status").setProp("textContent", t)

    private fun applyPreset(bloomOn: Boolean, bStrength: Int, chrOn: Boolean, cStrength: Int, crt: Boolean) {
        bloom = bloomOn; bloomStrength = bStrength; chromatic = chrOn; chromaticStrength = cStrength
        if (state.scanlines != crt) state.setScanlines(crt)
    }
    /** rt(): device-tuned standard. */
    private fun standardPreset() { val high = caps().tier == "high"; applyPreset(true, 50, high, if (high) 28 else 0, false) }
    /** it(…, true): device maximum. */
    private fun deviceMaxPreset() { val c = caps(); applyPreset(true, c.maxBloom, c.maxChromaticStrength > 0, c.maxChromaticStrength, true) }

    // ------------------------------------------------------------ the button (ve)
    suspend fun onButton() {
        if (busy || mode == "full-pending") return
        when (mode) {
            "standard" -> stepDeviceMax()
            "device-max" -> stepFull()
            "full" -> stepBackToStandard()
            else -> stepStandard()
        }
    }

    private suspend fun stepStandard() {
        busy = true; mode = "checking-standard"
        try {
            fullEffect = false; applyPreset(true, 50, false, 0, false); runtimeTier = hardwareTier()
            lcd("STANDARD PRESET // MEASURING DEVICE PERFORMANCE…")
            verdict(frameTest(1250), false)
            standardPreset(); mode = "standard"
            lcd("STANDARD PRESET READY // DEVICE PROFILE: ${caps().tier.uppercase()}")
        } finally { busy = false; save() }
    }

    private suspend fun stepDeviceMax() {
        busy = true; mode = "checking-device-max"
        try {
            runtimeTier = hardwareTier(); fullEffect = false
            val c = caps(); applyPreset(true, 100, c.maxChromaticStrength > 0, c.maxChromaticStrength, true)
            lcd("DEVICE MAXIMUM // MEASURING LIVE PERFORMANCE…")
            verdict(frameTest(1350), false)
            deviceMaxPreset(); mode = "device-max"
            lcd("DEVICE MAXIMUM READY // ${caps().tier.uppercase()} PROFILE")
        } finally { busy = false; save() }
    }

    private suspend fun stepFull() {
        busy = true; mode = "checking-full"
        try {
            fullEffect = true; applyPreset(true, 100, true, 100, true); runtimeTier = hardwareTier()
            lcd("FULL EFFECT // TESTING LIVE DEVICE CAPACITY…")
            if (verdict(frameTest(1450), true)) {
                goFull("verified"); lcd("MODE 3 // DEVICE CHECK PASSED — UNRESTRICTED MAXIMUM ACTIVE")
            } else {
                mode = "full-pending"; lcd("MODE 3 // DEVICE WARNING — CHOOSE AN OPTION")
            }
        } finally { busy = false; save() }
    }

    private suspend fun stepBackToStandard() {
        busy = true; mode = "checking-standard"
        try {
            fullEffect = false; runtimeTier = hardwareTier(); standardPreset()
            lcd("RETURNING TO STANDARD // RECHECKING DEVICE…")
            verdict(frameTest(1100), false)
            standardPreset(); mode = "standard"
            lcd("STANDARD PRESET READY // DEVICE PROFILE: ${caps().tier.uppercase()}")
        } finally { busy = false; save() }
    }

    /** tt(): unrestricted maximum. */
    fun goFull(how: String) {
        fullEffect = true; mode = "full"; applyPreset(true, 100, true, 100, true)
        lcd(if (how == "verified") "FULL EFFECT VERIFIED // UNRESTRICTED MAXIMUM" else "FULL EFFECT ENABLED BY USER // UNRESTRICTED MAXIMUM")
        save()
    }

    /** gt(): back to the device maximum. */
    fun restoreDeviceMax() {
        fullEffect = false; deviceMaxPreset(); mode = "device-max"
        lcd("DEVICE MAXIMUM RESTORED // ${caps().tier.uppercase()} PROFILE"); save()
    }

    // ------------------------------------------------------------ j(): status + button texts
    val statusText: String get() {
        val v = caps(); val r = v.tier.uppercase(); val crt = if (state.scanlines) "ON" else "OFF"
        return when (mode) {
            "basic" -> "MODE 0/3 · BASIC STARTUP EFFECTS // DEVICE CHECK REQUIRED"
            "checking-standard" -> "MODE 1/3 · CHECKING DEVICE // BUILDING STANDARD PRESET"
            "standard" -> {
                val b = minOf(v.maxBloom, bloomStrength); val c = if (chromatic) minOf(v.maxChromaticStrength, chromaticStrength) else 0
                "MODE 1/3 · DEVICE-TUNED STANDARD // $r · BLOOM $b% · CHROMATIC $c% · VIZ RGB ${if (c > 0) minOf(v.visualizerRgbMax, c) else 0}% · CRT $crt"
            }
            "checking-device-max" -> "MODE 2/3 · MEASURING DEVICE MAXIMUM // APPLYING EFFECT LOAD"
            "device-max" -> "MODE 2/3 · DEVICE MAXIMUM ACTIVE // $r · BLOOM ${v.maxBloom}% · CHROMATIC ${v.maxChromaticStrength}% · VIZ RGB ${v.visualizerRgbMax}% · CRT $crt"
            "checking-full" -> "MODE 3/3 · CHECKING UNRESTRICTED MAXIMUM // LIVE DEVICE TEST"
            "full-pending" -> "MODE 3/3 · UNRESTRICTED MAXIMUM // WARNING — CHOOSE AN OPTION"
            "full" -> "MODE 3/3 · UNRESTRICTED MAXIMUM ACTIVE // FULL INTENSITY // BLOOM ${v.maxBloom}% · CHROMATIC ${v.maxChromaticStrength}% · VIZ RGB ${v.visualizerRgbMax}% · CRT $crt"
            else -> "BASIC EFFECT SETTINGS // CLICK TO START THE 3-MODE DEVICE CYCLE"
        }
    }
    val buttonText: String get() = when (mode) {
        "checking-standard" -> "STEP 1/3 · CHECKING DEVICE…"
        "standard" -> "MODE 1/3 · STANDARD PRESET ACTIVE"
        "checking-device-max" -> "STEP 2/3 · CHECKING DEVICE MAXIMUM…"
        "device-max" -> "MODE 2/3 · DEVICE MAXIMUM ACTIVE"
        "checking-full" -> "STEP 3/3 · CHECKING MAXIMUM…"
        "full-pending" -> "MODE 3/3 · WAITING FOR YOUR DECISION"
        "full" -> "MODE 3/3 · UNRESTRICTED MAXIMUM ACTIVE"
        else -> "STEP 1/3 · BUILD STANDARD PRESET"
    }
}
