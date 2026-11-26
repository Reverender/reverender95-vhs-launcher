package com.reverender.r95.ui.apps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reverender.r95.AppState
import com.reverender.r95.LocalAppState
import com.reverender.r95.audio.VdSfx
import com.reverender.r95.ui.viz.js.Ctx
import com.reverender.r95.ui.viz.js.Ext
import com.reverender.r95.ui.viz.js.Rt
import com.reverender.r95.ui.viz.js.str
import com.reverender.r95.ui.viz.rememberFrameTime
import com.reverender.r95.ui.win95.T
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.PI
import kotlin.math.sqrt

// ===================================================================== rules (rr-vector-dreams)

/** Game state `e` and its formulas, as in rr-vector-dreams.js. */
private class VdState {
    var energy = 0.0; var total = 0.0; var lifetime = 0.0; var xp = 0.0; var level = 1; var power = 1.0
    val owned = HashMap<String, Int>(); val bought = ArrayList<String>(); val mults = HashMap<String, Double>(); var global = 1.0
    var shards = 0.0; var earned = 0.0; val pUp = HashMap<String, Int>()
    var combo = 0; var comboAt = 0L; var odUntil = 0L; var odCooldown = 0L; var last = System.currentTimeMillis(); var phase = 1

    fun y(id: String) = pUp[id] ?: 0
    fun W() = 1.25.pow(y("echo")) * 1.5.pow(y("architect")) * 1.18.pow(y("legacy")) * 1.2.pow(y("synth"))
    fun ee() = 1 + max(0, level - 1) * 0.01
    fun pe() = 1 + min(15, max(0, combo)) * 0.05
    fun z() = System.currentTimeMillis() < odUntil
    fun Ee() = if (z()) 2.5 else 1.0
    fun ownedCount() = VdData.buildings.sumOf { owned[it.id] ?: 0 }
    fun ve() = listOf(5, 15, 30, 60, 120).count { ownedCount() >= it }
    fun ae() = 1 + listOf(.05, .1, .15, .2, .25).take(ve()).sum()
    /** te(): power per pulse */
    fun te() = power * 2.0.pow(y("touch")) * 1.5.pow(y("overclock")) * W() * ee() * pe()
    /** B(): VECTRA per second */
    fun B() = VdData.buildings.sumOf { (owned[it.id] ?: 0) * it.rate * (mults[it.id] ?: 1.0) } * global * W() * ee() * ae() * Ee()
    fun econ() = max(.5, 1 - .05 * y("economy"))
    fun ie(b: VdBuilding) = max(1.0, ceil(b.base * 1.16.pow(owned[b.id] ?: 0) * econ()))
    /** ue(): how many units MAX INVEST can buy and their total cost */
    fun ue(b: VdBuilding): Pair<Int, Double> {
        var a = 0; var r = 0.0; val s = owned[b.id] ?: 0
        while (a < 5000) {
            val f = max(1.0, ceil(b.base * 1.16.pow(s + a) * econ()))
            if (!f.isFinite() || f > energy - r) break
            r += f; a++
        }
        return a to r
    }
    fun q(l: Int) = floor(100 * max(1, l).toDouble().pow(1.35))
    fun j() = 1 + .1 * y("echoYield")
    fun ge() = floor(sqrt(max(0.0, if (lifetime > 0) lifetime else total) / 1e7) * j())
    fun me() = max(0.0, ge() - earned)

    /** G(): XP, levelling; returns true on level-up */
    fun G(t0: Double): Boolean {
        if (!(t0 > 0)) return false
        xp += t0 * (1 + 0.3 * y("dreamxp"))
        var up = false
        while (xp >= q(level)) { xp -= q(level); level++; up = true }
        return up
    }
    /** Q(): energy gain (and 1% of it as XP) */
    fun Q(t: Double): Boolean { if (!(t > 0)) return false; energy += t; total += t; lifetime += t; return G(t * 0.01) }

    fun toJson(): String = JSONObject().apply {
        put("energy", energy); put("total", total); put("lifetime", lifetime); put("xp", xp); put("level", level); put("power", power); put("global", global)
        put("owned", JSONObject(owned as Map<*, *>)); put("bought", JSONArray(bought)); put("mults", JSONObject(mults as Map<*, *>))
        put("shards", shards); put("earned", earned); put("pUp", JSONObject(pUp as Map<*, *>)); put("odUntil", odUntil); put("odCooldown", odCooldown)
        put("last", System.currentTimeMillis())
    }.toString()

    companion object {
        /** we(): load, clamp shard claims, then add offline production. */
        fun load(s: String?): VdState = VdState().apply {
            val o = runCatching { JSONObject(s!!) }.getOrNull()
            if (o != null) {
                energy = o.optDouble("energy", 0.0); total = o.optDouble("total", 0.0); lifetime = max(o.optDouble("lifetime", 0.0), total)
                xp = o.optDouble("xp", 0.0); level = max(1, o.optInt("level", 1)); power = o.optDouble("power", 1.0); global = o.optDouble("global", 1.0)
                o.optJSONObject("owned")?.let { j -> j.keys().forEach { owned[it] = j.optInt(it) } }
                o.optJSONArray("bought")?.let { a -> for (i in 0 until a.length()) a.optString(i).let { if (it !in bought) bought.add(it) } }
                o.optJSONObject("mults")?.let { j -> j.keys().forEach { mults[it] = j.optDouble(it, 1.0) } }
                shards = o.optDouble("shards", 0.0); earned = o.optDouble("earned", 0.0)
                o.optJSONObject("pUp")?.let { j -> j.keys().forEach { pUp[it] = j.optInt(it) } }
                odUntil = o.optLong("odUntil"); odCooldown = o.optLong("odCooldown"); last = o.optLong("last", System.currentTimeMillis())
                val i = ge(); if (i - earned > 3) earned = max(0.0, i - 3)
                val a = (6 + 6 * y("offline") + 2 * y("afterhours")) * 3600.0
                val r = min(a, max(0.0, (System.currentTimeMillis() - last) / 1e3))
                Q(B() * r)
            }
            last = System.currentTimeMillis()
        }
    }
}

/** m(): the site's number format. */
private fun m(t0: Double): String {
    var t = t0
    if (t < 1e3) return if (t < 10) "%.1f".format(java.util.Locale.US, t) else "%,d".format(java.util.Locale.US, floor(t).toLong())
    val i = listOf("K", "M", "B", "T", "Qa", "Qi")
    var a = -1
    do { t /= 1e3; a++ } while (t >= 1e3 && a < i.size - 1)
    return (if (t < 10) "%.1f" else "%.0f").format(java.util.Locale.US, t) + i[a]
}

private val NEON_CYAN = Color(0xFF69F7FF)
private val NEON_PINK = Color(0xFFFF54D8)
private val NEON_GOLD = Color(0xFFFFE36E)
private val VD_INK = Color(0xFF0A0418)

// ===================================================================== live layer (reverender_vd_live_v1)

private class VdLive {
    var sfx = false; var quest = 0; val prog = HashMap<String, Double>(); var comets = 0
    val unlocked = HashSet<String>(); val seen = HashSet<String>(); var panel: String? = null; var dockInit = false
    fun toJson() = JSONObject().apply {
        put("sfx", sfx); put("quest", quest); put("prog", JSONObject(prog as Map<*, *>)); put("comets", comets)
        put("unlocked", JSONArray(unlocked.toList())); put("seen", JSONArray(seen.toList())); put("panel", panel ?: JSONObject.NULL); put("dockInit", dockInit)
    }.toString()
    companion object {
        fun load(s: String?) = VdLive().apply {
            val o = runCatching { JSONObject(s!!) }.getOrNull() ?: return@apply
            sfx = false; quest = o.optInt("quest"); comets = o.optInt("comets"); dockInit = o.optBoolean("dockInit")
            o.optJSONObject("prog")?.let { j -> j.keys().forEach { prog[it] = j.optDouble(it) } }
            o.optJSONArray("unlocked")?.let { a -> for (i in 0 until a.length()) unlocked.add(a.optString(i)) }
            o.optJSONArray("seen")?.let { a -> for (i in 0 until a.length()) seen.add(a.optString(i)) }
            panel = o.optString("panel").takeIf { it.isNotEmpty() && it != "null" }
        }
    }
}

private class Quest(val t: String, val type: String, val n: Int, val id: String? = null)
private val QUESTS = listOf(
    Quest("Pulse the core 30 times", "pulses", 30), Quest("Build 5 Vector Cursors", "own", 5, "cursor"), Quest("Reach a ×10 combo chain", "combo", 10),
    Quest("Install your first upgrade", "upgrades", 1), Quest("Build a Neon Sign Foundry", "own", 1, "sign"), Quest("Catch a Data Comet", "comets", 1),
    Quest("Engage OVERDRIVE", "overdrive", 1), Quest("Own 15 structures", "structures", 15), Quest("Build a Grid Generator", "own", 1, "grid"),
    Quest("Reach pilot level 5", "level", 5), Quest("Build a Polygon Sculptor", "own", 1, "sculptor"), Quest("Reach a ×20 combo chain", "combo", 20),
)
/** le(): quests after the list cycle through four kinds. */
private fun questAt(c: Int): Quest {
    if (c < QUESTS.size) return QUESTS[c]
    val h = c - QUESTS.size; val te = h % 4; val fe = h / 4 + 1
    return when (te) {
        0 -> Quest("Own ${15 + fe * 15} structures", "structures", 15 + fe * 15)
        1 -> Quest("Pulse the core ${50 + fe * 25} times", "pulses", 50 + fe * 25)
        2 -> Quest("Catch ${min(5, fe)} Data Comet${if (fe > 1) "s" else ""}", "comets", min(5, fe))
        else -> Quest("Reach pilot level ${5 + fe * 3}", "level", 5 + fe * 3)
    }
}

private class TabDef(val id: String, val label: String, val icon: String, val need: String, val ok: (VdState) -> Boolean)
private val TABS = listOf(
    TabDef("buildings", "BUILD", "🏗", "Gather 10 VECTRA") { it.total >= 10 || it.ownedCount() > 0 },
    TabDef("upgrades", "UPGRADES", "⬆", "Own 3 structures") { it.ownedCount() >= 3 || it.bought.isNotEmpty() },
    TabDef("research", "RESEARCH", "🔬", "Reach pilot level 3") { it.level >= 3 },
    TabDef("xp", "XP / LEVEL", "★", "Reach pilot level 2") { it.level >= 2 },
    TabDef("prestige", "PRESTIGE", "✦", "Earn 1M VECTRA all-time") { it.lifetime >= 1e6 || it.shards > 0 || it.earned > 0 },
)

private val UPGRADE_GLYPH = mapOf(
    "pulse2" to "↯", "pulse5" to "⌁", "cursor2" to "⌖", "grid2" to "▦", "sculpt2" to "◇", "arcade2" to "▣", "tower2" to "△", "portal2" to "◉",
    "sun2" to "☼", "dimension2" to "✧", "core2" to "◈", "hologram2" to "▧", "soundwave2" to "♫", "skybridge2" to "⌁", "dreamforge2" to "♢",
    "nebula2" to "✺", "timeloop2" to "⟳", "omnigrid2" to "▦", "singularity2" to "✹", "click10" to "ϟ", "click25" to "✳",
    "all1" to "✦", "all2" to "✧", "all3" to "✺", "all4" to "☼", "all5" to "∞", "all6" to "✹",
)
/** Re[]: the hue of each building's skyline unit. */
private val UNIT_HUE = intArrayOf(190, 326, 184, 270, 318, 194, 286, 42, 256, 190, 174, 302, 188, 28, 272, 330, 188, 48)

private class Toast(val text: String, val kind: String, val at: Long)
private class Float_(val text: String, val dx: Float, val kind: String, val at: Long)

// ===================================================================== the window

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VectorDreamsApp() {
    val state = LocalAppState.current
    val g = remember { VdState.load(state.loadBlob("vectordreams")) }
    val live = remember { VdLive.load(state.loadBlob("vectordreams_live")) }
    var tick by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("Tap the polygon core to awaken the city.") }
    val toasts = remember { mutableStateListOf<Toast>() }
    val floats = remember { mutableStateListOf<Float_>() }
    var shakeAt by remember { mutableStateOf(0L) }; var shakeHard by remember { mutableStateOf(false) }
    var event by remember { mutableStateOf<Pair<String, Long>?>(null) } // storm / surge until
    var comet by remember { mutableStateOf<Triple<Long, Boolean, Pair<Float, Float>>?>(null) } // start, ltr, y0/y1
    var nextComet by remember { mutableStateOf(System.currentTimeMillis() + (18_000 + Math.random() * 14_000).toLong()) }
    var confirmPrestige by remember { mutableStateOf(false) }
    var confirmReset by remember { mutableStateOf(false) }
    var questDoneAt by remember { mutableStateOf(0L) }
    val time = rememberFrameTime()
    fun saveAll() { state.saveBlob("vectordreams", g.toJson()); state.saveBlob("vectordreams_live", live.toJson()) }
    fun sfx(k: String, combo: Int = 1, crit: Boolean = false, n: Int = 1) { if (live.sfx) VdSfx.event(k, combo, crit, n) }
    fun shake(hard: Boolean) { shakeAt = System.currentTimeMillis(); shakeHard = hard }
    /** D(): world toast + flash */
    fun worldFeedback(t: String, kind: String) { toasts.add(Toast(t, kind, System.currentTimeMillis())); while (toasts.size > 4) toasts.removeAt(0) }
    fun say(t: String) { message = t }
    fun levelCheck(up: Boolean) { if (up) { say("VECTOR LEVEL ${g.level} // Permanent output increased by 1%."); sfx("level") } }
    /** fe()/Te(): district synergy milestones */
    fun milestone(before: Int, after: Int) {
        val r = listOf(5, 15, 30, 60, 120).firstOrNull { before < it && after >= it } ?: return
        val s = Math.round((g.ae() - 1) * 100)
        say("DISTRICT SYNERGY UNLOCKED // CITY OUTPUT +$s%."); worldFeedback("CITY SYNERGY // +$s% PRODUCTION", "upgrade"); sfx("milestone")
    }
    fun questProgress(type: String, v: Double) { live.prog[type] = (live.prog[type] ?: 0.0) + v }
    fun questValue(c: Quest): Double = when (c.type) {
        "own" -> (g.owned[c.id] ?: 0).toDouble(); "structures" -> g.ownedCount().toDouble(); "level" -> g.level.toDouble()
        "upgrades" -> g.bought.size.toDouble(); "combo" -> live.prog["comboBest"] ?: 0.0; else -> live.prog[c.type] ?: 0.0
    }
    /** ce(): a reward scaled to production and pulse power */
    fun reward(a: Double, b: Double) = max(25.0, max(g.B() * a, g.te() * b))
    /** ue(): quest check */
    fun checkQuest() {
        val c = questAt(live.quest)
        if (min(c.n.toDouble(), questValue(c)) >= c.n) {
            val te = reward(30.0 + live.quest * 3, 40.0)
            levelCheck(g.Q(te)); live.quest++; live.prog.clear(); live.prog["comboBest"] = 0.0; saveAll()
            say("QUEST COMPLETE // ${c.t.uppercase()} // +${m(te)} VECTRA"); worldFeedback("QUEST COMPLETE +${m(te)}", "upgrade"); sfx("quest")
            questDoneAt = System.currentTimeMillis()
        }
    }
    /** Re(): unlock dock panels */
    fun checkUnlocks() {
        TABS.forEach { t ->
            if (t.id !in live.unlocked && t.ok(g)) {
                live.unlocked.add(t.id); sfx("milestone"); worldFeedback("${t.label} PANEL UNLOCKED", "upgrade"); saveAll()
            }
        }
    }
    LaunchedEffect(Unit) {
        if (!live.dockInit) { TABS.forEach { if (it.ok(g)) { live.unlocked.add(it.id); live.seen.add(it.id) } }; live.dockInit = true }
        if (live.panel != null && live.panel !in live.unlocked) live.panel = null
        saveAll()
    }
    // 250 ms production tick, 1 s quest/unlock checks, 10 s autosave
    LaunchedEffect(Unit) {
        var n = 0
        while (true) {
            delay(250)
            com.reverender.r95.core.AppVisibility.awaitForeground()
            val now = System.currentTimeMillis()
            val d = min(2.0, max(0.0, (now - g.last) / 1e3))
            if (d > 0) levelCheck(g.Q(g.B() * d))
            g.last = now
            if (now - g.comboAt > 2200) g.combo = 0
            event?.let { (k, until) -> if (now >= until) event = null else if (k == "surge") g.Q(g.B() * 2 * 0.25) }
            if (++n % 4 == 0) { checkQuest(); checkUnlocks() }
            if (n % 40 == 0) saveAll()
            // data comets: 40–90 s apart (18–32 s after opening)
            if (comet == null && now >= nextComet) { comet = Triple(now, Math.random() < 0.5, (12 + Math.random() * 40).toFloat() to (12 + Math.random() * 45).toFloat()); sfx("cometSpawn") }
            comet?.let { if (now - it.first > 7000) { comet = null; nextComet = now + (40_000 + Math.random() * 50_000).toLong() } }
            tick++
        }
    }
    DisposableEffect(Unit) { onDispose { saveAll() } }

    /** Ce()/Me(): pulse the core */
    fun pulse() {
        val now = System.currentTimeMillis()
        g.combo = if (now - g.comboAt <= 2200) min(99, g.combo + 1) else 1
        g.comboAt = now
        val i = g.te(); val crit = g.combo > 0 && g.combo % 10 == 0; val r = if (crit) i * 3 else i
        var up = g.Q(r); up = g.G(1.0 + if (crit) 5 else 0) || up
        worldFeedback((if (crit) "CRITICAL PULSE // " else "CORE CHAIN ×${g.combo} // ") + "+${m(r)} VECTRA", if (crit) "critical" else "core")
        if (crit) say("CRITICAL RESONANCE // 3× HARVEST + BONUS XP.")
        // __vdEvent("pulse")
        var extra = 0.0
        if (event?.first == "storm") { extra = g.te() * 4; g.Q(extra) }
        floats.add(Float_("+" + m(r + extra), (Math.random() - 0.5).toFloat() * 34f, if (crit) "crit" else "", now)); while (floats.size > 14) floats.removeAt(0)
        live.prog["comboBest"] = max(live.prog["comboBest"] ?: 0.0, g.combo.toDouble())
        com.reverender.r95.ui.viz.sitevd.rrVdRing(now / 1000.0 % 100000, crit)
        sfx("pulse", g.combo, crit); if (crit) shake(false)
        questProgress("pulses", 1.0); levelCheck(up); checkQuest(); tick++
    }
    fun overdrive() {
        val t = System.currentTimeMillis()
        if (g.z() || t < g.odCooldown) return
        g.odUntil = t + 20_000; g.odCooldown = t + 65_000
        say("OVERDRIVE ENGAGED // PRODUCTION ×2.5 FOR 20 SECONDS."); saveAll(); worldFeedback("OVERDRIVE // ×2.5 CITY OUTPUT", "upgrade")
        sfx("overdrive"); shake(true); questProgress("overdrive", 1.0); checkQuest(); tick++
    }
    fun catchComet() {
        comet = null; nextComet = System.currentTimeMillis() + (40_000 + Math.random() * 50_000).toLong()
        live.comets++; questProgress("comets", 1.0)
        val fe = Math.random()
        val n = when {
            fe < 0.45 -> { val u = reward(60.0, 80.0); g.Q(u); "+${m(u)} DATA CACHE" }
            fe < 0.75 -> { event = "storm" to System.currentTimeMillis() + 12_000; "PULSE STORM ×5 · 12s" }
            else -> { event = "surge" to System.currentTimeMillis() + 20_000; "VECTRA SURGE ×3 · 20s" }
        }
        say("DATA COMET CAUGHT // $n"); worldFeedback(n, "critical"); sfx("cometCatch"); shake(false); checkQuest(); saveAll(); tick++
    }
    fun buy(b: VdBuilding) {
        val before = g.ownedCount(); val r = g.ie(b)
        if (g.energy < r) return
        g.energy -= r; g.owned[b.id] = (g.owned[b.id] ?: 0) + 1
        levelCheck(g.G(max(5.0, sqrt(r) * 0.35)))
        say("${b.name} constructed. A new structure rises on the Neon Coast.")
        worldFeedback("NEW STRUCTURE // ${b.name.uppercase()}", "building"); milestone(before, g.ownedCount())
        sfx("buy"); checkQuest(); tick++
    }
    fun buyMax(b: VdBuilding) {
        val before = g.ownedCount(); val (count, cost) = g.ue(b)
        if (count < 1) return
        var s = 0.0
        for (p in 0 until count) s += max(5.0, sqrt(max(1.0, ceil(b.base * 1.16.pow((g.owned[b.id] ?: 0) + p) * g.econ()))) * 0.35)
        g.energy -= cost; g.owned[b.id] = (g.owned[b.id] ?: 0) + count
        levelCheck(g.G(s))
        say("MAX INVEST COMPLETE // $count ${b.name} units constructed."); saveAll()
        worldFeedback("CITY EXPANSION // +$count ${b.name.uppercase()}", "building"); milestone(before, g.ownedCount())
        sfx("buy", n = count); checkQuest(); tick++
    }
    fun install(u: VdUpgrade) {
        g.energy -= u.cost; g.bought.add(u.id)
        levelCheck(g.G(max(15.0, sqrt(u.cost) * 0.5)))
        when (u.effect) { "click" -> g.power *= u.mult; "building" -> u.target?.let { g.mults[it] = (g.mults[it] ?: 1.0) * u.mult }; else -> g.global *= u.mult }
    }
    fun upgrade(u: VdUpgrade) {
        if (u.id in g.bought || g.energy < u.cost) return
        install(u)
        say("System upgrade installed: ${u.name} // Visual systems recalibrated."); worldFeedback("UPGRADE ONLINE // ${u.name.uppercase()}", "upgrade")
        sfx("upgrade"); checkQuest(); tick++
    }
    fun upgradeAll() {
        var i = 0
        VdData.upgrades.filter { it.id !in g.bought }.sortedBy { it.cost }.forEach { if (g.energy >= it.cost) { install(it); i++ } }
        if (i == 0) { say("NO AFFORDABLE UPGRADES // Build more VECTRA."); return }
        say("MAX INVEST COMPLETE // $i system upgrades installed."); saveAll(); worldFeedback("$i SYSTEMS INSTALLED // CITY OVERDRIVE", "upgrade")
        sfx("upgrade"); checkQuest(); tick++
    }
    fun prestigeUp(x: VdPrestige) {
        val a = g.y(x.id); val r = x.base * 2.0.pow(a)
        if (a >= x.max || g.shards < r) return
        g.shards -= r; g.pUp[x.id] = a + 1
        if (x.id == "memory") g.energy += 1e3 * (a + 1)
        say(if (x.id == "legacy" || x.id == "synth" || x.id == "architect") "REVERENDER SYSTEM UPGRADED: ${x.name} // PRODUCTION RESONANCE INCREASED" else "ECHO ARCHIVE MODULE INSTALLED: ${x.name}")
        saveAll(); worldFeedback("ARCHIVE AWAKENED // ${x.name.uppercase()}", "upgrade"); sfx("prestigeUp"); tick++
    }
    fun doPrestige() {
        val t = g.me(); if (t < 1) return
        g.shards += t; g.earned += t
        g.energy = 1e3 * g.y("memory"); g.total = 0.0; g.power = 1.0; g.owned.clear(); g.bought.clear(); g.mults.clear(); g.global = 1.0
        g.combo = 0; g.comboAt = 0; g.odUntil = 0; g.odCooldown = 0; g.phase = 1; g.last = System.currentTimeMillis()
        say("PRESTIGE COMPLETE // ${m(t)} ECHO SHARDS CLAIMED."); saveAll(); worldFeedback("NEW CYCLE // ${m(t)} ECHO SHARDS", "upgrade")
        sfx("prestige"); shake(true); tick++
    }
    /** Ke(): open / close a dock panel */
    fun openPanel(id: String?) {
        var c = id
        if (c != null && live.panel == c) c = null
        if (c != null && c !in live.unlocked) return
        live.panel = c; if (c != null) live.seen.add(c)
        saveAll(); sfx(if (c != null) "panelOpen" else "panelClose"); tick++
    }

    // externs the translated city layer reads
    Ext.fns["vdOwned"] = { a -> (g.owned[str(a.getOrNull(0))] ?: 0).toDouble() }

    val now = System.currentTimeMillis()
    val sh = now - shakeAt < 420
    BoxWithConstraints(Modifier.fillMaxSize()) {
    val worldH = (maxHeight - (if (live.panel != null) 600.dp else 400.dp)).coerceAtLeast(220.dp)
    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF14082C), Color(0xFF07020F)))).verticalScroll(rememberScrollState())) {
        tick
        // header.vd-top
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row {
                    T("VECTOR ", color = Color.White, bold = true, size = 20.sp, mono = true)
                    T("DREAMS", color = NEON_CYAN, bold = true, size = 20.sp, mono = true)
                }
                T("A SYNTHWAVE IDLE EXPEDITION // EST. 1989", color = Color(0xFFD9C8FF), size = 8.sp, mono = true)
                T("A REVERENDER ORIGINAL  ✦  DREAMS BUILT IN VECTRA", color = Color(0xFFB79CFF), size = 7.sp, mono = true, modifier = Modifier.padding(top = 3.dp))
            }
            Box(Modifier.border(2.dp, NEON_CYAN.copy(alpha = 0.8f), RoundedCornerShape(3.dp)).background(Color(0xCC0A0418)).clickable {
                live.sfx = !live.sfx; saveAll(); if (live.sfx) VdSfx.event("prestigeUp"); tick++
            }.padding(horizontal = 9.dp, vertical = 5.dp)) { T(if (live.sfx) "🔊 SFX ON" else "🔇 SFX OFF", color = NEON_CYAN, bold = true, mono = true, size = 13.sp) }
        }
        Box(Modifier.fillMaxWidth().height(2.dp).background(Brush.horizontalGradient(listOf(NEON_PINK, NEON_CYAN, Color(0xFFA78BFF)))))
        // .vd-world / .vd-sky
        Box(
            Modifier.padding(8.dp).fillMaxWidth().height(worldH).border(2.dp, Color(0xFFA78BFF).copy(alpha = 0.7f))
                .graphicsLayer {
                    if (sh) { val k = (1 - (now - shakeAt) / 420f) * (if (shakeHard) 6f else 3f); translationX = sin(now / 22f) * k * density; translationY = sin(now / 17f) * k * 0.6f * density }
                },
        ) {
            VdSky(g, live, time.value, Modifier.fillMaxSize())
            // the translated city layer (rr-os-styles x(C))
            VdCityLayer(g, Modifier.fillMaxSize())
            // .vd-audio-reactor
            val spec = remember { DoubleArray(4) }
            val active = state.player.isPlaying
            if (active) { val f = Rt.freqDataArr; var b = 0.0; for (k in 0 until 8) b += f[k]; var l = 0.0; for (k in 0 until 256) l += f[k]; spec[0] += ((b / 8 / 255) - spec[0]) * 0.2; spec[3] += ((l / 256 / 255) - spec[3]) * 0.2 } else { spec[0] *= 0.9; spec[3] *= 0.9 }
            Ext.vars["vdBass"] = spec[0]
            Row(Modifier.align(Alignment.TopStart).padding(6.dp).background(Color(0xD9080418), RoundedCornerShape(10.dp)).border(1.dp, NEON_CYAN.copy(alpha = 0.6f), RoundedCornerShape(10.dp)).padding(horizontal = 7.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).background(if (active) Color(0xFF3CFF9E) else Color(0xFF7A86A8), RoundedCornerShape(4.dp)))
                Spacer(Modifier.width(5.dp))
                T(if (active) "AUDIO LINK // LIVE" else "AUDIO LINK // STANDBY", color = Color.White, size = 7.sp, mono = true, bold = true)
                Spacer(Modifier.width(8.dp))
                T("%02d".format(Math.round(spec[3] * 99)), color = NEON_PINK, size = 8.sp, mono = true, bold = true)
            }
            // .vd-world-progress
            val n = g.ownedCount()
            val nb = VdData.buildings.firstOrNull { (g.owned[it.id] ?: 0) == 0 } ?: VdData.buildings.last()
            val cost = g.ie(nb)
            Column(Modifier.align(Alignment.TopStart).padding(start = 6.dp, top = 30.dp).widthIn(max = 220.dp).fillMaxWidth(0.5f).background(Color(0xD9080418), RoundedCornerShape(3.dp)).border(1.dp, NEON_CYAN.copy(alpha = 0.6f), RoundedCornerShape(3.dp)).padding(5.dp)) {
                Row {
                    T((if (n >= 1) "NEXT BUILD // " else "FIRST BUILD // ") + nb.name.uppercase(), color = Color.White, bold = true, mono = true, size = 7.sp, maxLines = 1, modifier = Modifier.weight(1f))
                    T("${m(min(g.energy, cost))} / ${m(cost)}", color = Color(0xFFD9C8FF), mono = true, size = 7.sp)
                }
                Box(Modifier.padding(top = 3.dp).fillMaxWidth().height(6.dp).border(1.dp, NEON_CYAN.copy(alpha = 0.5f))) {
                    Box(Modifier.fillMaxWidth((g.energy / cost).toFloat().coerceIn(0f, 1f)).fillMaxHeight().background(Brush.horizontalGradient(listOf(NEON_PINK, NEON_CYAN))))
                }
            }
            // .vd-upgrade-constellation
            Column(Modifier.align(Alignment.TopEnd).padding(top = 52.dp, end = 6.dp).fillMaxWidth(0.42f).background(Color(0xCC080418), RoundedCornerShape(6.dp)).border(1.dp, NEON_CYAN.copy(alpha = 0.55f), RoundedCornerShape(6.dp)).padding(7.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(7.dp).background(Color(0xFF3CFF9E), RoundedCornerShape(4.dp))); Spacer(Modifier.width(5.dp))
                    T("SYSTEMS ONLINE", color = Color.White, size = 7.sp, mono = true, bold = true)
                }
                if (g.bought.isEmpty()) T("NO MODULES INSTALLED", color = Color(0xFF9C93B8), size = 7.sp, mono = true, modifier = Modifier.padding(top = 5.dp))
                else FlowRow(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    g.bought.forEach { id ->
                        val u = VdData.upgrades.firstOrNull { it.id == id } ?: return@forEach
                        val col = when (u.effect) { "all" -> NEON_GOLD; "click" -> NEON_PINK; else -> NEON_CYAN }
                        Box(Modifier.size(16.dp).border(1.dp, col, RoundedCornerShape(3.dp)).background(col.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) { T(UPGRADE_GLYPH[id] ?: "✦", color = col, size = 9.sp) }
                    }
                }
            }
            // comet
            comet?.let { (start, ltr, ys) ->
                val k = ((now - start) / 7000f).coerceIn(0f, 1f)
                BoxWithConstraints(Modifier.fillMaxSize()) {
                    val x = (if (ltr) k else 1 - k) * (maxWidth.value + 60) - 30
                    val y = (ys.first + (ys.second - ys.first) * k) / 100f * maxHeight.value
                    Box(Modifier.offset(x.dp, y.dp).size(34.dp).pointerInput(start) { detectTapGestures { catchComet() } }, contentAlignment = Alignment.Center) {
                        Canvas(Modifier.fillMaxSize()) { drawLine(Brush.horizontalGradient(listOf(Color.Transparent, NEON_CYAN)), Offset(if (ltr) -40f * density else size.width + 40f * density, size.height / 2), Offset(size.width / 2, size.height / 2), 3f * density) }
                        T("◆", color = Color.White, size = 18.sp, modifier = Modifier.graphicsLayer { rotationZ = (System.currentTimeMillis() % 1200) / 1200f * 360f })
                    }
                }
            }
            // world toasts
            toasts.removeAll { now - it.at > 1250 }
            toasts.lastOrNull()?.let { t ->
                val k = (now - t.at) / 1250f
                T(t.text, color = when (t.kind) { "critical" -> NEON_GOLD; "upgrade" -> NEON_CYAN; else -> Color.White }, bold = true, mono = true, size = 10.sp,
                    align = TextAlign.Center, modifier = Modifier.align(Alignment.Center).offset(y = (-20 - k * 30).dp).graphicsLayer { alpha = (1 - k).coerceIn(0f, 1f) }
                        .background(Color(0xB3080418), RoundedCornerShape(3.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
            }
            T("THE REVERENDER POLYGON ENGINE", color = Color(0xFFD9C8FF), size = 6.sp, mono = true, modifier = Modifier.align(Alignment.BottomStart).padding(6.dp))
            T("X: 089.04 // Y: 24.88 // Z: ∞", color = Color(0xFFD9C8FF), size = 6.sp, mono = true, modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp))
        }
        // .vd-dock
        FlowRow(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            TABS.forEach { t ->
                val locked = t.id !in live.unlocked; val active = live.panel == t.id; val isNew = !locked && t.id !in live.seen
                Box(
                    Modifier.then(if (isNew) Modifier.border(3.dp, NEON_GOLD.copy(alpha = 0.25f + 0.5f * ((sin(now / 1400f * 2 * PI.toFloat()) + 1) / 2)), RoundedCornerShape(5.dp)) else Modifier)
                        .border(1.dp, if (locked) Color(0xFF55506A) else if (active) NEON_PINK else NEON_CYAN.copy(alpha = 0.7f), RoundedCornerShape(3.dp))
                        .background(if (active) NEON_PINK.copy(alpha = 0.25f) else Color(0x99120A26), RoundedCornerShape(3.dp))
                        .clickable(enabled = !locked) { openPanel(t.id) }.padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    T((if (locked) "🔒" else t.icon) + " " + t.label, color = if (locked) Color(0xFF6C6784) else Color.White, size = 13.sp, bold = !locked)
                    if (isNew) Box(Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-10).dp).graphicsLayer { alpha = if ((now / 500) % 2 == 0L) 1f else 0.3f }
                        .background(Color(0xFFFFE36E), RoundedCornerShape(2.dp)).padding(horizontal = 3.dp)) { T("NEW", color = Color(0xFF10051F), size = 6.sp, bold = true) }
                }
            }
        }
        // .vd-console (when a panel is open)
        live.panel?.let { panel -> VdConsole(g, panel, ::openPanel, ::buy, ::buyMax, ::upgrade, ::upgradeAll, { confirmPrestige = true }, ::prestigeUp,
            onSave = { saveAll(); say("Progress saved to this browser.") }, onReset = { confirmReset = true }) }
        // .vd-core-row
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(82.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures { pulse() } }) {
                    val t = time.value; val c = center
                    val od = g.z()
                    drawCircle(Brush.radialGradient(listOf((if (od) NEON_PINK else Color(0xFFB76CFF)).copy(alpha = 0.55f), Color.Transparent), c, size.minDimension * 0.55f), size.minDimension * 0.55f, c)
                    val s = size.minDimension * (0.34f + 0.015f * sin(t * 4f) + min(15, g.combo) * 0.002f)
                    val d = Path().apply { moveTo(c.x, c.y - s); lineTo(c.x + s, c.y); lineTo(c.x, c.y + s); lineTo(c.x - s, c.y); close() }
                    drawPath(d, Brush.linearGradient(listOf(Color(0xFFF2D7FF), Color(0xFFB050FF), NEON_CYAN), Offset(c.x - s, c.y - s), Offset(c.x + s, c.y + s)))
                    drawPath(d, Color.White.copy(alpha = 0.85f), style = Stroke(2.2f * density))
                    val s2 = s * 0.42f
                    drawPath(Path().apply { moveTo(c.x, c.y - s2); lineTo(c.x + s2, c.y); lineTo(c.x, c.y + s2); lineTo(c.x - s2, c.y); close() }, Color.White.copy(alpha = 0.9f), style = Stroke(1.4f * density))
                    drawCircle(Color.White, 3.5f * density, c)
                }
                // floating +N
                floats.removeAll { now - it.at > 900 }
                floats.forEach { f ->
                    val k = (now - f.at) / 900f
                    T(f.text, color = if (f.kind == "crit") NEON_GOLD else Color.White, bold = true, mono = true, size = if (f.kind == "crit") 13.sp else 11.sp,
                        modifier = Modifier.align(Alignment.TopCenter).offset(x = f.dx.dp, y = (-k * 40).dp).graphicsLayer { alpha = 1 - k })
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                T("${m(g.energy)} VECTRA", color = Color.White, bold = true, mono = true, size = 16.sp)
                T("${m(g.B())} / SEC", color = NEON_CYAN, mono = true, size = 11.sp)
                T("+${m(g.te())} per pulse", color = Color(0xFFD9C8FF), mono = true, size = 9.sp)
                Row(Modifier.padding(top = 4.dp).widthIn(max = 180.dp), verticalAlignment = Alignment.CenterVertically) {
                    T("CORE CHAIN", color = Color(0xFFD9C8FF), mono = true, size = 8.sp, modifier = Modifier.weight(1f)); T("×" + "%.2f".format(java.util.Locale.US, g.pe()), color = NEON_GOLD, bold = true, mono = true, size = 8.sp)
                }
                Box(Modifier.widthIn(max = 180.dp).fillMaxWidth().height(4.dp).background(Color(0xFF2A1B48))) {
                    Box(Modifier.fillMaxWidth(min(15, max(0, g.combo)) / 15f).fillMaxHeight().background(Brush.horizontalGradient(listOf(NEON_CYAN, NEON_PINK))))
                }
                event?.let { (k, until) ->
                    val evSh = (now % 1600) / 1600f * 400f
                    T((if (k == "storm") "⚡ PULSE STORM // EVERY PULSE ×5" else "☄ VECTRA SURGE // PRODUCTION ×3") + " · ${ceil((until - now) / 1e3).toInt()}s", color = Color.White, bold = true, mono = true, size = 8.sp,
                        modifier = Modifier.padding(top = 4.dp).background(Brush.linearGradient(listOf(Color(0xFFFF2FB6), Color(0xFF7B5BFF), Color(0xFF23D8FF), Color(0xFFFF2FB6)), Offset(-evSh, 0f), Offset(400f - evSh, 0f), androidx.compose.ui.graphics.TileMode.Repeated), RoundedCornerShape(3.dp)).padding(horizontal = 9.dp, vertical = 5.dp))
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(104.dp)) {
                val c = now < g.odUntil; val l = now < g.odCooldown
                Column(
                    Modifier.fillMaxWidth().border(2.dp, if (c) NEON_GOLD else NEON_PINK, RoundedCornerShape(4.dp))
                        .background(Brush.verticalGradient(if (c) listOf(Color(0xFFFF8A00), NEON_PINK) else listOf(Color(0xFFB0247A), Color(0xFF4A1676))), RoundedCornerShape(4.dp))
                        .graphicsLayer { alpha = if (!c && l) 0.5f else 1f }
                        .clickable(enabled = c || !l) { overdrive() }.padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    T(if (c) "OVERDRIVE LIVE" else if (l) "RECHARGING" else "OVERDRIVE ×2.5", color = Color.White, bold = true, mono = true, size = 11.sp)
                    T(if (c) "${ceil((g.odUntil - now) / 1e3).toInt()} SEC LEFT" else if (l) "${ceil((g.odCooldown - now) / 1e3).toInt()} SEC" else "20 SEC BOOST", color = Color.White, mono = true, size = 7.sp)
                }
                T(if (g.z()) "PRODUCTION SURGE // ×2.5" else "CORE CHAIN BOOST // CLICK TO BUILD", color = Color(0xFFD9C8FF), mono = true, size = 6.sp, align = TextAlign.Center, modifier = Modifier.padding(top = 3.dp))
            }
        }
        // .vd-quest
        val qd = questAt(live.quest); val qh = min(qd.n.toDouble(), questValue(qd))
        val qk = ((now - questDoneAt) / 900f).coerceIn(0f, 1f) // .vd-quest-done: a 0.9 s gold flash
        Column(Modifier.padding(horizontal = 8.dp).fillMaxWidth().background(if (qk < 1f) androidx.compose.ui.graphics.lerp(NEON_GOLD.copy(alpha = 0.55f), Color(0x99120A26), qk) else Color(0x99120A26))
            .border(1.dp, if (qk < 1f) NEON_GOLD else Color(0xFFA78BFF).copy(alpha = 0.6f))) {
            Row(Modifier.padding(horizontal = 10.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                T("QUEST ${live.quest + 1}", color = NEON_PINK, bold = true, mono = true, size = 11.sp); Spacer(Modifier.width(8.dp))
                T(qd.t, color = Color.White, bold = true, mono = true, size = 11.sp, modifier = Modifier.weight(1f))
                T("${if (qh < 1e3) floor(qh).toInt().toString() else m(qh)} / ${qd.n}", color = Color.White, mono = true, size = 11.sp)
            }
            Box(Modifier.fillMaxWidth(qh.toFloat() / qd.n).height(3.dp).background(Brush.horizontalGradient(listOf(NEON_PINK, NEON_CYAN))))
        }
        T(message, color = Color(0xFFD9C8FF), mono = true, size = 9.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
        // .vd-metrics
        Row(Modifier.padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("ALL-TIME ENERGY" to m(g.lifetime), "STRUCTURES" to "${g.ownedCount()}", "PHASE" to "%02d".format(min(9, 1 + (kotlin.math.log10(max(1.0, g.total)) / 3).toInt()))).forEach { (k, v) ->
                Column(Modifier.weight(1f).background(Color(0x99120A26)).border(1.dp, Color(0xFFA78BFF).copy(alpha = 0.6f)).padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    T(k, color = Color(0xFFD9C8FF), mono = true, size = 7.sp); T(v, color = Color.White, bold = true, mono = true, size = 12.sp)
                }
            }
        }
        // .vd-progression-strip
        Column(Modifier.padding(8.dp).fillMaxWidth().background(Color(0x99120A26)).border(1.dp, Color(0xFFA78BFF).copy(alpha = 0.6f)).padding(8.dp)) {
            Row { T("VECTOR LEVEL ${g.level}", color = Color.White, mono = true, size = 8.sp, modifier = Modifier.weight(1f)); T("${m(g.xp)} / ${m(g.q(g.level))} XP", color = Color.White, bold = true, mono = true, size = 8.sp) }
            Box(Modifier.padding(vertical = 4.dp).fillMaxWidth().height(5.dp).border(1.dp, NEON_CYAN.copy(alpha = 0.4f))) {
                Box(Modifier.fillMaxWidth(min(1.0, g.xp / g.q(g.level)).toFloat()).fillMaxHeight().background(Brush.horizontalGradient(listOf(NEON_CYAN, NEON_PINK))))
            }
            T("CITY SYNERGY // +${Math.round((g.ae() - 1) * 100)}%", color = NEON_GOLD, mono = true, size = 7.sp)
            Row { T("✦ ECHO SHARDS", color = Color.White, mono = true, size = 7.sp, modifier = Modifier.weight(1f)); T(m(g.shards), color = Color.White, bold = true, mono = true, size = 8.sp) }
        }
    }
    }

    if (confirmPrestige) VdConfirm(
        "Initiate Prestige? You have reached ${m(g.me())} Echo Shard milestone(s) from lifetime VECTRA. Current VECTRA, buildings and standard upgrades reset; level, XP, lifetime VECTRA, shards and Archive modules remain.",
        { confirmPrestige = false; doPrestige() }, { confirmPrestige = false })
    if (confirmReset) VdConfirm("Reset VECTOR DREAMS and erase its local save?", {
        confirmReset = false
        val fresh = VdState(); g.energy = 0.0; g.total = 0.0; g.lifetime = 0.0; g.xp = 0.0; g.level = 1; g.power = 1.0; g.owned.clear(); g.bought.clear(); g.mults.clear()
        g.global = 1.0; g.shards = 0.0; g.earned = 0.0; g.pUp.clear(); g.combo = 0; g.comboAt = 0; g.odUntil = 0; g.odCooldown = 0; g.last = fresh.last; g.phase = 1
        say("New simulation initialized."); saveAll(); tick++
    }, { confirmReset = false })
}

@Composable
private fun VdConfirm(text: String, ok: () -> Unit, cancel: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = cancel) {
        Column(Modifier.background(com.reverender.r95.ui.win95.W95.Face).padding(12.dp)) {
            T(text, size = 12.sp)
            Row(Modifier.padding(top = 10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End)) {
                com.reverender.r95.ui.win95.Btn("OK", ok); com.reverender.r95.ui.win95.Btn("Cancel", cancel)
            }
        }
    }
}

// ===================================================================== the panel (C())

@Composable
private fun VdConsole(
    g: VdState, panel: String, open: (String?) -> Unit, buy: (VdBuilding) -> Unit, buyMax: (VdBuilding) -> Unit, upgrade: (VdUpgrade) -> Unit,
    upgradeAll: () -> Unit, prestige: () -> Unit, prestigeUp: (VdPrestige) -> Unit, onSave: () -> Unit, onReset: () -> Unit,
) {
    Column(Modifier.padding(8.dp).fillMaxWidth().background(Color(0xE60A0418)).border(1.dp, NEON_CYAN.copy(alpha = 0.5f))) {
        Row(Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            T(when (panel) { "buildings" -> "CONSTRUCT THE CITY"; "upgrades" -> "INSTALL SYSTEM UPGRADES"; "research" -> "CITY RESEARCH"; "xp" -> "PILOT PROFILE // EXPERIENCE"; else -> "PRESTIGE // ECHO ARCHIVE" },
                color = Color.White, bold = true, mono = true, size = 10.sp, modifier = Modifier.weight(1f))
            T("VECTOR SYSTEMS", color = Color(0xFF9C93B8), mono = true, size = 8.sp)
            Spacer(Modifier.width(8.dp))
            Box(Modifier.clickable { open(null) }.padding(horizontal = 6.dp)) { T("✕", color = Color.White, size = 13.sp) }
        }
        val dim = Color(0xFF9C93B8)
        @Composable fun Card(enabled: Boolean, icon: String, title: String, sub: String, price: String, extra: (@Composable () -> Unit)? = null, onClick: () -> Unit) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp).background(if (enabled) Color(0xFF1E1240) else Color(0xFF130C26)).border(1.dp, if (enabled) NEON_CYAN.copy(alpha = 0.6f) else Color(0xFF3A3256))
                    .clickable(enabled = enabled, onClick = onClick).padding(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                T(icon, color = NEON_CYAN, size = 18.sp, modifier = Modifier.width(28.dp))
                Column(Modifier.weight(1f)) { T(title, color = if (enabled) Color.White else dim, bold = true, size = 11.sp); T(sub, color = dim, size = 9.sp); extra?.invoke() }
                T(price, color = if (enabled) NEON_GOLD else dim, mono = true, bold = true, size = 10.sp)
            }
        }
        when (panel) {
            "buildings" -> VdData.buildings.forEach { c ->
                val l = g.ie(c); val own = g.owned[c.id] ?: 0; val w = g.ue(c)
                val h = own * c.rate * (g.mults[c.id] ?: 1.0) * g.global * g.W() * g.ee(); val S = if (g.B() > 0) min(100.0, h / g.B() * 100) else 0.0
                Card(g.energy >= l, c.icon, "${c.name} · $own", "${c.desc}\n+${m(c.rate)} BASE VECTRA / SEC", "${m(l)} ◈", extra = {
                    T("${m(h)} / SEC CURRENT OUTPUT", color = NEON_CYAN, mono = true, size = 8.sp)
                    Box(Modifier.fillMaxWidth().height(3.dp).background(Color(0xFF2A1B48))) { Box(Modifier.fillMaxWidth((S / 100).toFloat()).fillMaxHeight().background(NEON_CYAN)) }
                }) { buy(c) }
                Box(
                    Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, bottom = 4.dp).background(if (w.first > 0) Color(0xFF2A1450) else Color(0xFF130C26)).border(1.dp, if (w.first > 0) NEON_PINK.copy(alpha = 0.6f) else Color(0xFF3A3256))
                        .clickable(enabled = w.first > 0) { buyMax(c) }.padding(4.dp), contentAlignment = Alignment.Center,
                ) { T("MAX INVEST · " + (if (w.first > 0) "${w.first} UNITS · ${m(w.second)} ◈" else "UNAVAILABLE"), color = if (w.first > 0) Color.White else dim, mono = true, size = 8.sp, bold = true) }
            }
            "upgrades" -> {
                val c = VdData.upgrades.filter { it.id !in g.bought }
                val any = c.any { g.energy >= it.cost }
                Column(Modifier.padding(4.dp)) {
                    Box(Modifier.fillMaxWidth().background(if (any) Color(0xFF2A1450) else Color(0xFF130C26)).border(1.dp, if (any) NEON_PINK else Color(0xFF3A3256)).clickable(enabled = any) { upgradeAll() }.padding(6.dp), contentAlignment = Alignment.Center) {
                        T("MAX INVEST // INSTALL ALL AFFORDABLE", color = if (any) Color.White else dim, bold = true, mono = true, size = 9.sp)
                    }
                    T("Installs affordable system upgrades in one action, without buying anything you cannot afford.", color = dim, size = 8.sp, modifier = Modifier.padding(top = 2.dp))
                }
                if (c.isEmpty()) T("All known upgrades installed. Expand the city to unlock the next era.", color = Color.White, size = 10.sp, modifier = Modifier.padding(8.dp))
                c.forEach { u ->
                    val w = when (u.effect) { "click" -> "CORE VISUAL: stronger pulse, brighter reactor and larger impact"; "all" -> "CITY VISUAL: brighter skyline, stronger grid and wider energy field"; else -> "DISTRICT VISUAL: upgraded ${u.target} structure glows and animates" }
                    Card(g.energy >= u.cost, "✦", u.name, u.desc, "${m(u.cost)} ◈", extra = { T(w, color = NEON_PINK, size = 8.sp) }) { upgrade(u) }
                }
            }
            "xp" -> Column(Modifier.padding(8.dp)) {
                val c = g.q(g.level)
                T("✦ VECTOR PILOT // LEVEL ${g.level}", color = Color.White, bold = true, size = 12.sp)
                Box(Modifier.padding(vertical = 5.dp).fillMaxWidth().height(8.dp).border(1.dp, NEON_CYAN.copy(alpha = 0.5f))) { Box(Modifier.fillMaxWidth(min(1.0, g.xp / c).toFloat()).fillMaxHeight().background(Brush.horizontalGradient(listOf(NEON_CYAN, NEON_PINK)))) }
                T("${m(g.xp)} / ${m(c)} XP to level ${g.level + 1}", color = Color.White, bold = true, size = 10.sp)
                T("Lifetime VECTRA: ${m(g.lifetime)}", color = Color.White, size = 10.sp)
                T("Each level grants +1% production and your level is retained through Prestige.", color = Color.White, size = 10.sp)
                Box(Modifier.padding(vertical = 6.dp).fillMaxWidth().height(1.dp).background(dim))
                T("FIELD EXPERIENCE", color = Color.White, bold = true, size = 10.sp)
                listOf("• Harvesting energy grants XP over time.", "• Constructing a building grants bonus XP.", "• Installing an upgrade grants a larger XP reward.", "• Every level permanently improves production.").forEach { T(it, color = Color.White, size = 10.sp) }
                T("Keep expanding the city to increase your pilot level. XP is never lost when you activate Prestige.", color = dim, size = 9.sp, modifier = Modifier.padding(top = 4.dp))
            }
            "prestige" -> Column(Modifier.padding(8.dp)) {
                val c = g.me()
                val l = 1e7 * ((g.earned + c + 1) / g.j()).pow(2); val gg = 1e7 * ((g.earned + c) / g.j()).pow(2)
                val w = max(0.0, min(100.0, (g.lifetime - gg) / max(1.0, l - gg) * 100))
                Brand("THE ECHO ARCHIVE · LEGACY SYSTEMS")
                T("✧ THE ECHO ARCHIVE", color = Color.White, bold = true, size = 12.sp)
                T("Earn lifetime VECTRA through core pulses and city production. Every lifetime milestone unlocks Echo Shards; initiate Prestige to claim the available shards and reset your city for a stronger next cycle.", color = Color.White, size = 10.sp)
                Column(Modifier.padding(vertical = 6.dp).fillMaxWidth().border(1.dp, NEON_CYAN.copy(alpha = 0.4f)).padding(6.dp)) {
                    T("HOW TO EARN ECHO SHARDS", color = Color.White, bold = true, size = 10.sp)
                    T("FIRST SHARD: 10,000,000 lifetime VECTRA. Each further shard requires the next quadratic milestone. Echo Yield Protocol lowers the milestone requirement and increases your shard rewards.", color = Color.White, size = 9.sp)
                    Box(Modifier.padding(vertical = 4.dp).fillMaxWidth().height(6.dp).border(1.dp, NEON_CYAN.copy(alpha = 0.5f))) { Box(Modifier.fillMaxWidth((w / 100).toFloat()).fillMaxHeight().background(Brush.horizontalGradient(listOf(NEON_PINK, NEON_GOLD)))) }
                    T("${m(g.lifetime)} / ${m(l)} LIFETIME VECTRA · ${"%.1f".format(java.util.Locale.US, w)}% TO NEXT SHARD", color = dim, mono = true, size = 8.sp)
                }
                T("“THE CITY ENDS. THE SIGNAL DOESN’T.”", color = NEON_PINK, bold = true, size = 10.sp, align = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Row(Modifier.padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("SHARDS AVAILABLE" to m(c), "SHARDS BANKED" to m(g.shards), "PRESTIGE BONUS" to "×" + "%.2f".format(java.util.Locale.US, g.W())).forEach { (k, v) ->
                        Column(Modifier.weight(1f).border(1.dp, Color(0xFFA78BFF).copy(alpha = 0.5f)).padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) { T(k, color = dim, size = 7.sp, mono = true); T(v, color = Color.White, bold = true, size = 12.sp) }
                    }
                }
                Box(Modifier.fillMaxWidth().background(if (c >= 1) Brush.horizontalGradient(listOf(NEON_PINK, Color(0xFF7A3FC8))) else Brush.horizontalGradient(listOf(Color(0xFF221836), Color(0xFF221836)))).clickable(enabled = c >= 1) { prestige() }.padding(8.dp), contentAlignment = Alignment.Center) {
                    T("✧ INITIATE PRESTIGE · ${m(c)} SHARDS", color = if (c >= 1) Color.White else dim, bold = true, mono = true, size = 11.sp)
                }
                T("Prestige resets current VECTRA, buildings and standard upgrades. Your pilot level, XP, lifetime total, Echo Shards and Archive modules are retained. Prestige claims the milestones you have reached; no shards are taken away.", color = dim, size = 8.sp, modifier = Modifier.padding(vertical = 4.dp))
                Box(Modifier.padding(vertical = 4.dp).fillMaxWidth().height(1.dp).background(dim))
                T("PERMANENT ARCHIVE MODULES", color = Color.White, bold = true, size = 10.sp)
                VdData.prestige.forEach { h ->
                    val S = g.y(h.id); val Y = h.base * 2.0.pow(S)
                    Card(S < h.max && g.shards >= Y, h.icon, "${h.name} · RANK $S/${h.max}", h.desc, if (S >= h.max) "MAX" else "${m(Y)} ✦") { prestigeUp(h) }
                }
            }
            else -> Column(Modifier.padding(8.dp)) {
                Brand("VECTOR DREAMS · SYSTEMS DIVISION")
                T("VECTRA RESEARCH DIVISION", color = Color.White, bold = true, size = 11.sp)
                T("Current era: ${g.phase} · Total energy: ${m(g.total)}\nStructures online: ${g.ownedCount()}\nEnergy production: ${m(g.B())} VECTRA/sec", color = Color.White, size = 10.sp)
                T("\nEach structure increases passive production. Purchases become more expensive as your network grows. Upgrade modules multiply individual systems or the entire city. New districts, reactors and reality-scale structures extend the progression into the late game. Your progress is stored locally and offline production is calculated when you return.", color = Color.White, size = 10.sp)
                T("\nMISSION: Build the Neon Coast into a self-sustaining polygon metropolis. Keep the core pulsing.", color = Color.White, bold = true, size = 10.sp)
            }
        }
        // footer.vd-footer
        Row(Modifier.fillMaxWidth().padding(6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            T("LOCAL SAVE // AUTOSAVE ENABLED", color = Color(0xFF9C93B8), mono = true, size = 8.sp, modifier = Modifier.weight(1f))
            com.reverender.r95.ui.win95.Btn("SAVE", onSave, size = 10.sp); com.reverender.r95.ui.win95.Btn("RESET", onReset, size = 10.sp)
        }
    }
}

@Composable
private fun Brand(sub: String) {
    Column(Modifier.padding(bottom = 6.dp)) {
        Row { T("REVERENDER", color = Color.White, bold = true, mono = true, size = 11.sp); T(" // 1989", color = NEON_PINK, bold = true, mono = true, size = 11.sp) }
        T(sub, color = Color(0xFF9C93B8), mono = true, size = 7.sp)
    }
}

// ===================================================================== the world

/** The CSS scene behind the city: sky, stars, striped sun, orbits, mountains, skyline units, wire core, grid floor. */
@Composable
private fun VdSky(g: VdState, live: VdLive, t: Float, modifier: Modifier) {
    val d = LocalDensity.current.density
    Canvas(modifier) {
        val w = size.width; val h = size.height; val hz = h * 0.74f
        drawRect(Brush.verticalGradient(listOf(Color(0xFF16053A), Color(0xFF4B1470), Color(0xFFB23A9A), Color(0xFF2A0C46)), 0f, hz), size = Size(w, hz))
        // stars
        for (i in 0 until 50) { val x = ((i * 97.13f) % 1f + (i * 0.6180339f) % 1f) % 1f * w; val y = ((i * 0.3819f) % 1f) * hz * 0.6f; drawCircle(Color.White.copy(alpha = 0.3f + 0.5f * ((sin(t * 2 + i) + 1) / 2)), d * (0.6f + (i % 3) * 0.3f), Offset(x, y)) }
        // orbits
        drawOval(Color(0xFFA78BFF).copy(alpha = 0.55f), Offset(w * 0.25f, h * 0.06f), Size(w * 0.85f, h * 0.22f), style = Stroke(2f * d))
        drawOval(NEON_CYAN.copy(alpha = 0.35f), Offset(w * 0.3f, h * 0.18f), Size(w * 0.55f, h * 0.2f), style = Stroke(1.4f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * d, 10 * d), t * 20)))
        // striped sun
        val sr = min(w, h) * 0.22f; val sc = Offset(w * 0.5f, hz - sr * 0.95f)
        drawCircle(Brush.verticalGradient(listOf(Color(0xFFFFF27A), Color(0xFFFF9E5E), Color(0xFFFF4FA3)), sc.y - sr, sc.y + sr), sr, sc)
        for (k in 0 until 7) { val y = sc.y + sr * (0.05f + k * 0.14f); drawRect(Color(0xFF7A1F78), Offset(sc.x - sr, y), Size(sr * 2, sr * (0.03f + k * 0.012f))) }
        // mountains
        val m1 = Path().apply { moveTo(0f, hz); lineTo(w * 0.12f, hz - h * 0.22f); lineTo(w * 0.32f, hz - h * 0.08f); lineTo(w * 0.5f, hz - h * 0.3f); lineTo(w * 0.7f, hz - h * 0.1f); lineTo(w * 0.86f, hz - h * 0.24f); lineTo(w, hz - h * 0.12f); lineTo(w, hz); close() }
        drawPath(m1, Brush.verticalGradient(listOf(Color(0xFF6A2B8C), Color(0xFF230A3F)), hz - h * 0.3f, hz))
        drawPath(m1, NEON_PINK.copy(alpha = 0.5f), style = Stroke(1.2f * d))
        // skyline units (he()): per owned building type, 1–6 glowing blocks
        val lefts = intArrayOf(5, 13, 21, 29, 37, 45, 53, 61, 69, 77, 85, 92)
        VdData.buildings.forEachIndexed { hh, b ->
            val S = g.owned[b.id] ?: 0; if (S == 0) return@forEachIndexed
            val Y = min(6, 1 + floor(kotlin.math.ln(max(1, S).toDouble()) / kotlin.math.ln(2.0)).toInt())
            val up = VdData.upgrades.any { it.effect == "building" && it.target == b.id && it.id in g.bought }
            for (L in 0 until Y) {
                val E = (lefts[(hh * 3 + L * 4) % lefts.size] + (if (L > 0) (hh % 2) * 3 else 0)) % 94
                val I = min(62f, 19 + hh * 2.35f + L * 4) * d; val le = (9 + (hh % 4) * 3 + (if (L > 0) 2 else 0)) * d
                val hue = UNIT_HUE.getOrElse(hh) { 190 }.toFloat()
                val col = Color.hsv(hue, 0.65f, 1f); val x = w * E / 100f
                val bob = sin(t * 1.6f + hh * 0.42f + L * 0.16f) * d
                drawRect(Color(0xF0140A2E), Offset(x, hz - I + bob), Size(le, I))
                drawRect(col.copy(alpha = if (up) 0.95f else 0.6f), Offset(x, hz - I + bob), Size(le, I), style = Stroke((if (up) 1.6f else 1f) * d))
                var wy = hz - I + 4 * d + bob
                while (wy < hz - 3 * d) { drawRect(col.copy(alpha = 0.55f), Offset(x + le * 0.3f, wy), Size(le * 0.4f, 1.6f * d)); wy += 6 * d }
            }
        }
        // the wire core (svg .vd-vector-art), gently turning
        val k = min(w / 640f, h / 390f); val ox = (w - 640 * k) / 2; val oy = (h - 390 * k) / 2 - h * 0.04f
        rotateWire(t, ox, oy, k, d)
        // floor + grid
        drawRect(Brush.verticalGradient(listOf(Color(0xFF2A0C46), Color(0xFF0A0418)), hz, h), Offset(0f, hz), Size(w, h - hz))
        drawLine(Brush.horizontalGradient(listOf(Color.Transparent, NEON_PINK, NEON_CYAN, NEON_PINK, Color.Transparent)), Offset(0f, hz), Offset(w, hz), 2.5f * d)
        val rr = (t * 0.6f) % 1f
        for (i in 0 until 10) { val p = (i + rr) / 10f; val y = hz + p * p * (h - hz); drawLine(NEON_PINK.copy(alpha = 0.25f + p * 0.4f), Offset(0f, y), Offset(w, y), d) }
        for (i in -10..10) drawLine(NEON_PINK.copy(alpha = 0.35f), Offset(w / 2 + i * w * 0.02f, hz), Offset(w / 2 + i * w * 0.14f, h), d)
        drawContext.canvas.nativeCanvas.drawText("1989", w * 0.74f, hz + 16 * d, android.graphics.Paint().apply { color = 0xCCFFFFFF.toInt(); textSize = 9 * d; typeface = android.graphics.Typeface.MONOSPACE; letterSpacing = 0.3f; isAntiAlias = true })
    }
}

/** The six-sided wire structure of the scene SVG (viewBox 640 × 390), with a slow 3D turn like its CSS animation. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.rotateWire(t: Float, ox: Float, oy: Float, k: Float, d: Float) {
    val turn = sin(t * 0.35f) * 0.5f
    fun p(x: Float, y: Float): Offset { val dx = (x - 320) * kotlin.math.cos(turn); return Offset(ox + (320 + dx) * k, oy + y * k) }
    val brush = Brush.linearGradient(listOf(NEON_PINK, NEON_CYAN, Color(0xFFA78BFF)), p(210f, 45f), p(430f, 300f))
    fun poly(pts: List<Pair<Float, Float>>, a: Float, fill: Color? = null) {
        val path = Path().apply { pts.forEachIndexed { i, (x, y) -> val q = p(x, y); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }
        if (fill != null) drawPath(path, fill)
        drawPath(path, brush, alpha = a * 0.92f, style = Stroke(2.2f * k * 1.4f))
    }
    poly(listOf(320f to 45f, 430f to 105f, 430f to 235f, 320f to 300f, 210f to 235f, 210f to 105f), 1f)
    poly(listOf(320f to 75f, 405f to 122f, 405f to 218f, 320f to 267f, 235f to 218f, 235f to 122f), 0.75f)
    poly(listOf(320f to 104f, 380f to 138f, 380f to 204f, 320f to 238f, 260f to 204f, 260f to 138f), 0.65f)
    listOf(listOf(320f, 45f, 320f, 300f), listOf(210f, 105f, 430f, 235f), listOf(430f, 105f, 210f, 235f), listOf(320f, 45f, 260f, 204f), listOf(320f, 45f, 380f, 204f), listOf(210f, 105f, 380f, 204f), listOf(430f, 105f, 260f, 204f))
        .forEach { (a, b, c, e) -> drawLine(brush, p(a, b), p(c, e), 2.2f * k * 1.4f, alpha = 0.6f) }
    poly(listOf(320f to 130f, 355f to 150f, 355f to 190f, 320f to 210f, 285f to 190f, 285f to 150f), 1f, Color(0x1AFF4BD2))
}

private var vdErrAt = 0L
private fun vdLogErr(e: Throwable) { val n = System.currentTimeMillis(); if (n - vdErrAt > 5000) { vdErrAt = n; android.util.Log.w("R95", "vd city frame failed", e) } }

/** rr-os-styles' city canvas (translated), drawn every frame at up to 30 fps. */
@Composable
private fun VdCityLayer(g: VdState, modifier: Modifier) {
    val state = LocalAppState.current
    val time = rememberFrameTime()
    val ctx = remember { Ctx() }
    // its own layer: the site's canvas starts each frame with clearRect, which must not punch through the window
    Canvas(modifier.graphicsLayer { compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen }) {
        val t = time.value
        Ext.vars["vdBought"] = g.bought.size.toDouble(); Ext.vars["vdOverdrive"] = g.z(); Ext.vars["vdReduced"] = state.lowPower
        val nc = drawContext.canvas.nativeCanvas
        val cw = size.width / density; val ch = size.height / density
        ctx.begin(nc, cw.toDouble(), ch.toDouble(), density)
        try { com.reverender.r95.ui.viz.sitevd.rrVdCity(ctx, cw.toDouble(), ch.toDouble(), (System.currentTimeMillis() / 1000.0) % 100000) } catch (e: Throwable) { vdLogErr(e) }
        ctx.end()
        t
    }
}
