package com.reverender.r95.ui.apps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reverender.r95.AppState
import com.reverender.r95.LocalAppState
import com.reverender.r95.ui.win95.Skin
import com.reverender.r95.ui.win95.T
import kotlinx.coroutines.delay
import org.json.JSONObject
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

// ===================================================================== state (rr-vaporpet-v1)

/** The cat `a` and the shared bits of the site's VAPORPET module, used by the window and the desktop walker. */
object VaporPet {
    class Cat {
        var name = "Catfield"; var born = System.currentTimeMillis(); var last = born
        var food = 70.0; var fun_ = 70.0; var energy = 80.0; var health = 100.0
        var poop = 0; var poopT = born; var sick = false; var asleep = false; var lights = true
        var weight = 4.0; var care = 0; var roam = true; var wins = 0; var bowl = 60.0
    }
    var a = Cat(); private set
    private var app: AppState? = null
    /** Bumped by k() so Compose redraws. */
    var version by mutableIntStateOf(0); private set
    var msg by mutableStateOf(""); private set

    /** e: the LCD's animation state */
    class Lcd { var x = 30.0; var dir = 1; var act: String? = null; var actT = 0L; var game: Game? = null; var hearts = 0L; var sweep = 0L; var lightT = 0L; var pill = 0L; var foodT = 0L; var foodKind = "" }
    class Game { var round = 0; var wins = 0; var pick: String? = null; var reveal = 0L; val res = ArrayList<Boolean>() }
    val e = Lcd()
    var sheet by mutableStateOf<String?>(null)

    fun install(state: AppState) {
        if (app != null) return
        app = state
        a = load(state.loadBlob("pet"))
        v((System.currentTimeMillis() - a.last) / 6e4); a.last = System.currentTimeMillis(); S()
        msg = oe()
    }
    private fun load(s: String?): Cat = Cat().apply {
        val o = runCatching { JSONObject(s!!) }.getOrNull() ?: return@apply
        if (!o.has("born")) return@apply
        name = o.optString("name", "Catfield").let { if (it == "NEON") "Catfield" else it }; born = o.optLong("born", born); last = o.optLong("last", born)
        food = o.optDouble("food", 70.0); fun_ = o.optDouble("fun", 70.0); energy = o.optDouble("energy", 80.0); health = o.optDouble("health", 100.0)
        poop = o.optInt("poop"); poopT = o.optLong("poopT", born); sick = o.optBoolean("sick"); asleep = o.optBoolean("asleep"); lights = o.optBoolean("lights", true)
        weight = o.optDouble("weight", 4.0); care = o.optInt("care"); roam = o.optBoolean("roam", true); wins = o.optInt("wins"); bowl = o.optDouble("bowl", 60.0)
    }
    /** S(): save */
    fun S() {
        val c = a
        app?.saveBlob("pet", JSONObject().put("name", c.name).put("born", c.born).put("last", c.last).put("food", c.food).put("fun", c.fun_).put("energy", c.energy)
            .put("health", c.health).put("poop", c.poop).put("poopT", c.poopT).put("sick", c.sick).put("asleep", c.asleep).put("lights", c.lights).put("weight", c.weight)
            .put("care", c.care).put("roam", c.roam).put("roamMigrated", true).put("wins", c.wins).put("bowl", c.bowl).toString())
    }
    fun Y(n: Double) = max(0.0, min(100.0, n))
    fun ce() = (System.currentTimeMillis() - a.born) / 864e5
    fun pe() = ce().let { if (it < 1) "Kitten" else if (it < 4) "Cat" else "Vapor Cat" }
    fun N() = ce().let { if (it < 1) "${max(1, floor(it * 24).toInt())} h old" else floor(it).toInt().let { d -> "$d " + if (d == 1) "day old" else "days old" } }
    /** v(): advance by n minutes (max a day) */
    fun v(n0: Double) {
        val n = min(n0, 1440.0); if (n <= 0) return
        val c = a
        if (c.asleep) { c.energy = Y(c.energy + n * 0.55); c.food = Y(c.food - n * 0.08); c.fun_ = Y(c.fun_ - n * 0.04); if (c.energy >= 100 && c.lights) c.asleep = false }
        else { c.food = Y(c.food - n * 0.2); c.fun_ = Y(c.fun_ - n * 0.25); c.energy = Y(c.energy - n * 0.14) }
        if (!c.asleep && c.energy < 8) c.asleep = true
        if (!c.asleep) {
            val u = (System.currentTimeMillis() - c.poopT) / 6e4
            if (u > 80) { val f = min(4 - c.poop, floor(u / 80).toInt()); if (f > 0) c.poop += f; c.poopT = System.currentTimeMillis() }
        }
        val r = (if (c.poop >= 3) 1 else 0) + (if (c.food < 5) 1 else 0) + (if (c.energy < 5) 1 else 0)
        if (!c.sick && r > 0 && Math.random() < min(0.5, n * 0.004 * r)) c.sick = true
        c.health = if (c.sick) Y(c.health - n * 0.12) else Y(c.health + n * 0.05)
        c.weight = max(2.0, min(30.0, c.weight))
    }
    /** the 30 s tick */
    fun tick30() { val n = System.currentTimeMillis(); v((n - a.last) / 6e4); a.last = n; S(); k() }
    fun P(): List<String> = buildList {
        val c = a
        if (c.sick) add("sick"); if (c.poop > 0) add("mess"); if (c.food < 25) add("hungry"); if (c.fun_ < 25) add("bored")
        if (c.energy < 18 && !c.asleep) add("tired"); if (!c.lights && !c.asleep) add("dark")
    }
    fun oe(): String {
        val c = a
        if (c.asleep) return "${c.name} is asleep. Zzz…"
        when (P().firstOrNull()) {
            "sick" -> return "${c.name} feels sick — give medicine."
            "mess" -> return "There is a mess to clean up!"
            "hungry" -> return "${c.name} is hungry."
            "bored" -> return "${c.name} is bored — play a game!"
            "tired" -> return "${c.name} is sleepy — turn the light off."
            "dark" -> return "It is dark… ${c.name} can not see."
        }
        val f = (c.food + c.fun_ + c.energy + c.health) / 4
        return if (f > 75) "${c.name} is very happy!" else if (f > 50) "${c.name} is doing fine." else "${c.name} could use some attention."
    }
    /** d(): status line */
    fun d(t: String) { msg = t }
    /** k(): refresh (the site writes the mood into the status line on every refresh) */
    fun k() { d(oe()); version++ }
    fun f(n: String, u: Long = 1500) { e.act = n; e.actT = System.currentTimeMillis() + u }
    fun M() { sheet = null }

    /** D(): dock action */
    fun D(n: String) {
        val c = a
        if (e.game != null && n != "play") return
        if (n == "feed") { if (c.asleep) { d("${c.name} is asleep. Turn the light on first."); return }; sheet = "feed"; return }
        if (n == "stats") { sheet = "stats"; return }
        M()
        if (c.asleep && n != "light") { d("${c.name} is asleep. Turn the light on first."); return }
        when (n) {
            "play" -> {
                if (c.energy < 10) { f("no", 1400); d("Too tired to play."); return }
                e.game = Game(); sheet = "game"; return
            }
            "clean" -> {
                if (c.poop == 0) { d("Already spotless!"); return }
                c.poop = 0; c.poopT = System.currentTimeMillis(); c.care++; f("lick", 1800); d("All clean. Fresh!"); e.sweep = System.currentTimeMillis()
            }
            "light" -> {
                e.lightT = System.currentTimeMillis(); c.lights = !c.lights
                if (!c.lights) { if (c.energy < 90) c.asleep = true; d(if (c.asleep) "Lights off. Good night, ${c.name}." else "Lights off, but ${c.name} is wide awake.") }
                else if (c.asleep) { c.asleep = false; d("Good morning!") } else d("Lights on.")
            }
            "med" -> {
                if (!c.sick) { f("no", 1400); d("${c.name} does not need medicine.") }
                else { c.sick = false; c.health = Y(c.health + 25); c.care++; e.pill = System.currentTimeMillis(); pendingHappyAt = System.currentTimeMillis() + 1100; d("${c.name} feels better!") }
            }
        }
        S(); k()
    }
    internal var pendingHappyAt = 0L
    fun feed(kind: String) {
        val c = a; M()
        if (kind == "close") return
        if (c.food >= 98 && kind == "meal") { f("no", 1400); d("${c.name} is full!"); return }
        if (kind == "meal") { c.food = Y(c.food + 32); c.weight += 1 } else { c.food = Y(c.food + 8); c.fun_ = Y(c.fun_ + 12); c.weight += 1 }
        c.care++; f("eat", 2000); e.foodKind = kind; e.foodT = System.currentTimeMillis()
        d(if (kind == "meal") "Yum! A full meal." else "A sweet snack!"); S(); k()
    }
    fun guess(g: String) {
        val E = e.game ?: return
        if (E.reveal != 0L) return
        val Z = if (Math.random() < 0.5) "L" else "R"
        E.pick = Z; E.reveal = System.currentTimeMillis()
        val ok = g == Z; if (ok) E.wins++; E.res.add(ok)
        d(if (ok) "Yes! Good guess." else "Nope, the other way."); version++
    }
    /** the 900 ms after a guess */
    fun afterGuess() {
        val E = e.game ?: return; val c = a
        E.reveal = 0; E.round++
        if (E.round >= 5) {
            val j = E.wins >= 3
            c.fun_ = Y(c.fun_ + if (j) 30 else 12); c.energy = Y(c.energy - 10); c.weight = max(2.0, c.weight - 1)
            if (j) c.wins++; c.care++
            e.game = null; M(); f(if (j) "happy" else "yawn", if (j) 2600 else 1400); if (j) e.hearts = System.currentTimeMillis()
            d(if (j) "${c.name} won ${E.wins} of 5 and is thrilled!" else "${c.name} won ${E.wins} of 5 — fun anyway."); S(); k()
        }
        version++
    }
    fun rename(s: String) { a.name = s.trim().ifEmpty { "Catfield" }.take(12); S(); k() }
    fun startOver() { val r = a.roam; a = Cat().also { it.roam = r }; S(); M(); k(); d("A new kitten has arrived!") }
    fun tapLcd() { if (!a.asleep && e.game == null) { f("happy", 1500); e.hearts = System.currentTimeMillis(); a.fun_ = Y(a.fun_ + 2); S(); k() } }
}

// ===================================================================== the LCD (q())

private val PET_ICONS = mapOf(
    "feed" to listOf("...kkk...", "..kkkkk..", ".kkkkkkk.", ".kkkkkkk.", "kkkkkkkkk", "kk.....kk", "kk.....kk", ".k.....k.", "..kkkkk.."),
    "play" to listOf("...kkk...", ".kk...kk.", ".k.kkk.k.", "k.k...k.k", "k.k...k.k", "k.k...k.k", ".k.kkk.k.", ".kk...kk.", "...kkk..."),
    "clean" to listOf("....k....", "...kkk...", "k...k...k", ".k.....k.", "...k.k...", "..k...k..", ".k.kkk.k.", "...kkk...", "....k...."),
    "light" to listOf("...kkk...", "..k...k..", ".k.....k.", ".k.....k.", ".k.....k.", "..k...k..", "...kkk...", "...kkk...", "....k...."),
    "med" to listOf("...kkk...", "...kkk...", "...kkk...", "kkkkkkkkk", "kkkkkkkkk", "kkkkkkkkk", "...kkk...", "...kkk...", "...kkk..."),
    "stats" to listOf(".........", "......kk.", "......kk.", "...kk.kk.", "...kk.kk.", "kk.kk.kk.", "kk.kk.kk.", "kk.kk.kk.", "kkkkkkkkk"),
)

private class Pal(val bg: Color, val bg2: Color, val k: Color, val m: Color, val l: Color, val w: Color, val p: Color)
private fun W(lights: Boolean) = if (lights) Pal(Color(0xFFCFE2CF), Color(0xFFBCD3BF), Color(0xFF1D2B28), Color(0xFF5A7A6E), Color(0xFF9DBCAB), Color(0xFFE9F3E6), Color(0xFF7D6A8A))
else Pal(Color(0xFF26324A), Color(0xFF1E2A40), Color(0xFF0B111D), Color(0xFF3E5175), Color(0xFF56709C), Color(0xFF9FB3D6), Color(0xFF7A6A9A))

/** se(): draw a 16-wide sprite */
internal fun DrawScope.petSprite(rows: Array<String>, x: Float, y: Float, s: Float, pal: Map<Char, Color>, flip: Boolean) {
    for (j in rows.indices) for (c in 0 until 16) {
        val col = pal[rows[j][if (flip) 15 - c else c]] ?: continue
        drawRect(col, Offset(x + c * s, y + j * s), Size(s, s))
    }
}

/** ne: the LCD's idle behaviour */
private class Ne { var act = "walk"; var end = 0L; var hop = 0L }
private val ne = Ne()
private var de = 0

/** ae() */
private fun pickIdle(n: Long) {
    val a = VaporPet.a; val e = VaporPet.e
    val u = a.energy < 35; val F = a.fun_ > 60
    val E = listOf("walk" to 5.0, "run" to (if (F) 2.0 else 0.8), "sit" to 3.0, "lick" to 1.5, "stretch" to 1.0, "yawn" to (if (u) 2.0 else 0.6), "hop" to (if (F) 1.6 else 0.6), "loaf" to (if (u) 2.5 else 0.6))
    var r = Math.random() * E.sumOf { it.second }
    for ((k, w) in E) { r -= w; if (r <= 0) { ne.act = k; break } }
    ne.end = n + when (ne.act) { "walk" -> 2500 + Math.random() * 3e3; "run" -> 900 + Math.random() * 900; "sit" -> 2e3 + Math.random() * 3e3; "lick" -> 1800 + Math.random() * 1500; "stretch" -> 1200.0; "yawn" -> 1e3; "hop" -> 900.0; else -> 4e3 + Math.random() * 4e3 }.toLong()
    if ((ne.act == "walk" || ne.act == "run") && Math.random() < 0.4) e.dir = -e.dir
    if (ne.act == "hop") ne.hop = n
}

/** q(): one LCD frame, in the 96 × 56 grid scaled to the canvas. */
private fun DrawScope.drawLcd(step: Boolean) {
    val o = PET_O; val a = VaporPet.a; val e = VaporPet.e
    val sc = size.width / 96f
    fun m(x: Number, y: Number, w: Number, h: Number, c: Color) = drawRect(c, Offset(x.toFloat() * sc, y.toFloat() * sc), Size(w.toFloat() * sc, h.toFloat() * sc))
    val u = System.currentTimeMillis(); val F = W(a.lights)
    if (step) de++
    // J(): room
    m(0, 0, 96, 56, F.bg); m(0, 44, 96, 12, F.bg2)
    for (x in 0 until 96 step 6) m(x, 44, 3, 1, F.l)
    m(30, 47, 36, 1, F.l); m(28, 48, 40, 2, F.l); m(30, 50, 36, 1, F.l)
    val win = if (a.lights) Color(0xFFE7F1E4) else Color(0xFF1A2338)
    m(70, 3, 20, 13, F.k); m(71, 4, 18, 11, win)
    if (a.lights) {
        m(71 + ((u / 6e4) % 1 * 15).roundToInt(), 5, 3, 3, F.m)
        val ee = 71 + ((u / 23e3) % 1 * 26).roundToInt() - 6
        clipRect(71 * sc, 4 * sc, 89 * sc, 15 * sc) { m(ee, 10, 6, 2, F.l); m(ee + 1, 9, 3, 1, F.l) }
    } else {
        m(84, 5, 3, 3, F.w); m(85, 5, 2, 2, win)
        listOf(Triple(73, 11, 0.0), Triple(76, 6, 1.3), Triple(81, 12, 2.1), Triple(87, 10, 3.4)).forEach { (x, y, p) -> if (sin(u / 1800.0 + p) > -0.2) m(x, y, 1, 1, F.w) }
    }
    m(79, 4, 1, 11, F.k); m(71, 9, 18, 1, F.k)
    val j0 = sin(u / 1500.0).roundToInt()
    m(8, 38, 6, 6, F.m); m(9, 39, 4, 1, F.l); m(10, 32, 1, 6, F.k); m(8 + j0, 33, 2, 2, F.k); m(11 + j0, 34, 2, 2, F.k); m(9 + j0, 30, 2, 2, F.k)
    m(20, 13, 18, 1, F.k); m(24, 9, 8, 4, F.m); m(25, 10, 2, 2, F.bg); m(29, 10, 2, 2, F.bg)
    m(84, 41, 8, 3, F.m); m(85, 44, 6, 1, F.k)
    if (a.bowl > 5) m(85, 40, max(1, (6 * a.bowl / 100).roundToInt()), 1, F.k)
    if (a.lights && a.fun_ > 55 && !a.asleep) {
        val ye = u / 1e3; val Me = 48 + (sin(ye * 0.7) * 30).roundToInt(); val Pe = 18 + (sin(ye * 1.3) * 6).roundToInt(); val xe = ((u / 180) % 2).toInt()
        m(Me, Pe, 1, 2, F.k); m(Me - 1 - xe, Pe - xe, 1 + xe, 1 + xe, F.m); m(Me + 1, Pe - xe, 1 + xe, 1 + xe, F.m)
    }
    // the cat
    val pal = mapOf('k' to F.k, 'w' to F.w, 'p' to F.p, 'e' to F.k)
    var E: Array<String>; var Z = e.dir < 0; val ee = 20; var j = 0
    if (e.act != null && u > e.actT) e.act = null
    if (VaporPet.pendingHappyAt in 1..u) { VaporPet.pendingHappyAt = 0; VaporPet.f("happy", 1500) }
    val game = e.game
    if (a.asleep) { E = if ((u / 1300) % 2 == 1L) o.getValue("sleep") else o.getValue("sleepB"); e.x = 30.0 }
    else if (game != null) {
        e.x = 32.0
        if (game.reveal != 0L) { E = o.getValue("sit"); Z = game.pick == "L" }
        else { E = if ((u / 700) % 3 == 0L) o.getValue("sitB") else o.getValue("sit"); Z = (u / 500) % 2 == 0L }
    } else if (e.act == "eat") E = if ((u / 260) % 2 == 1L) o.getValue("eat") else o.getValue("sit")
    else if (e.act == "happy") { val Me = (u % 600) / 600.0; j = (sin(Me * PI) * 6).roundToInt(); E = if (j > 2) o.getValue("jump") else o.getValue("happy") }
    else if (e.act == "no") { E = o.getValue("no"); Z = (u / 220) % 2 == 0L }
    else if (e.act == "lick") E = if ((de shr 2) % 2 == 1) o.getValue("lick1") else o.getValue("lick2")
    else if (e.act != null) E = o[e.act!!] ?: o.getValue("sit")
    else if (a.sick || a.health < 40) E = if ((u / 2000) % 3 == 0L) o.getValue("yawn") else o.getValue("sad")
    else {
        if (u > ne.end) pickIdle(u)
        E = when (ne.act) {
            "walk" -> { if (step) e.x += e.dir; if ((de shr 1) % 2 == 1) o.getValue("walk1") else o.getValue("walk2") }
            "run" -> { if (step) e.x += e.dir * 2.5; if (de % 2 == 1) o.getValue("run1") else o.getValue("run2") }
            "sit" -> if ((u / 1400) % 4 == 0L) o.getValue("sitB") else if (a.fun_ > 75 && (u / 2200) % 4 == 1L) o.getValue("happy") else o.getValue("sit")
            "lick" -> if ((de shr 2) % 2 == 1) o.getValue("lick1") else o.getValue("lick2")
            "stretch" -> o.getValue("stretch"); "yawn" -> o.getValue("yawn")
            "loaf" -> if ((u / 1300) % 2 == 1L) o.getValue("sleep") else o.getValue("sleepB")
            "hop" -> {
                val p = (u - ne.hop) / 700.0
                if (p < 0.15) o.getValue("crouch") else if (p < 1) { j = (sin((p - 0.15) / 0.85 * PI) * 9).roundToInt(); if (step) e.x += e.dir * 0.8; o.getValue("jump") } else o.getValue("land")
            }
            else -> o.getValue("sit")
        }
        if (e.x > 60) { e.x = 60.0; e.dir = -1 }
        if (e.x < 18) { e.x = 18.0; e.dir = 1 }
        Z = e.dir < 0
    }
    val xe = e.x.roundToInt()
    val qe = max(10, 24 - j * 2)
    m(xe + 16 - qe / 2.0, 44, qe, 1, F.m)
    drawContext.canvas.save(); drawContext.canvas.scale(sc, sc)
    petSprite(E, xe.toFloat(), (ee - j).toFloat(), 2f, pal, Z)
    drawContext.canvas.restore()
    // Vapor Cat stripes
    if (VaporPet.pe() == "Vapor Cat" && E !== o["sleep"] && E !== o["sleepB"] && E !== o["stretch"]) {
        val low = E === o["walk1"] || E === o["walk2"] || E === o["run1"] || E === o["run2"] || E === o["crouch"] || E === o["land"]
        val De = if (low) ".........kkkkkkk" else ".......kkkkkkk.."
        val Ye = if (E === o["run1"] || E === o["run2"] || E === o["land"]) 4 else if (E === o["crouch"]) 6 else 3
        for (r in 0 until 16) if (De[if (Z) 15 - r else r] == 'k') m(xe + r * 2, ee - j + Ye * 2, 2, 2, F.k)
    }
    // G(): poop
    for (s in 0 until a.poop) {
        val px = 70 - s * 9; val py = 40
        listOf(2 to 0, 1 to 1, 2 to 1, 3 to 1, 0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2, 0 to 3, 1 to 3, 2 to 3, 3 to 3, 4 to 3, 5 to 3).forEach { (dx, dy) -> m(px + dx, py + dy, 1, 1, F.k) }
        val z = ((u / 500) % 3).toInt(); m(px + 1, py - 3 - z, 1, 1, F.m); m(px + 4, py - 2 - (z + 1) % 3, 1, 1, F.m)
    }
    if (e.sweep != 0L && u - e.sweep < 900) { val y = ((u - e.sweep) / 900.0 * 96).roundToInt(); m(y, 30, 2, 14, F.k); m(y - 2, 42, 6, 2, F.m) }
    if (e.sweep != 0L && u - e.sweep < 1800) {
        val I = (u - e.sweep) / 1800.0
        listOf(20 to 36, 44 to 30, 66 to 38, 80 to 28).forEachIndexed { lt, (x, y) -> if (sin(I * 6 + lt) > 0) { m(x, y - 1, 1, 3, F.k); m(x - 1, y, 3, 1, F.k) } }
    }
    if (e.foodT != 0L && u - e.foodT < 2000) {
        val re = 1 - (u - e.foodT) / 2e3; val ve = max(1, (7 * re).roundToInt()); val Te = if (Z) xe - 9 else xe + 34
        m(Te, 38, ve, 6, F.k); m(Te + 1, 39, max(0, ve - 2), 1, F.w)
        if ((u / 200) % 2 == 1L) m(Te + 2, 44 - ((u / 100) % 3).toInt(), 1, 1, F.k)
    }
    if (e.pill != 0L && u - e.pill < 1200) { val p = (u - e.pill) / 1200.0; val nx = (90 - p * (90 - xe - 20)).roundToInt(); val ny = (10 + p * 14).roundToInt(); m(nx, ny, 4, 2, F.k); m(nx + 2, ny, 2, 2, F.w) }
    if (e.act == "happy" || (e.hearts != 0L && u - e.hearts < 1600)) {
        val He = if (e.hearts != 0L) e.hearts else e.actT - 1500
        val We = ((u - He) % 1600) / 1600.0
        listOf((xe + 26) to 0.0, (xe + 4) to 0.45).forEach { (hx, off) ->
            val lt = (We + off) % 1; val dt = (18 - lt * 14).roundToInt()
            if (lt < 0.9) listOf(1 to 0, 3 to 0, 0 to 1, 1 to 1, 2 to 1, 3 to 1, 4 to 1, 1 to 2, 2 to 2, 3 to 2, 2 to 3).forEach { (dx, dy) -> m(hx + dx, dt + dy, 1, 1, F.k) }
        }
    }
    if (a.asleep) {
        val p = android.graphics.Paint().apply { color = android.graphics.Color.argb(255, (F.w.red * 255).toInt(), (F.w.green * 255).toInt(), (F.w.blue * 255).toInt()); textSize = 8 * sc; typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD) }
        val je = (u % 2400) / 2400.0
        drawContext.canvas.nativeCanvas.drawText("z", (56 + (je * 6).roundToInt()) * sc, (24 - (je * 8).roundToInt()) * sc, p)
        val j2 = (je + 0.5) % 1
        drawContext.canvas.nativeCanvas.drawText("Z", (62 + (j2 * 6).roundToInt()) * sc, (18 - (j2 * 8).roundToInt()) * sc, p)
    }
    if (a.sick) { val Ve = sin(u / 500.0).roundToInt(); m(xe + 30, 16 + Ve, 5, 1, F.k); m(xe + 32, 14 + Ve, 1, 5, F.k) }
    if (e.lightT != 0L && u - e.lightT < 600) m(0, 0, 96, 56, Color.Black.copy(alpha = (0.5 * (1 - (u - e.lightT) / 600.0)).toFloat()))
    m(0, 0, 96, 2, Color.White.copy(alpha = 0.06f))
}

// ===================================================================== the window (#rr-pet-root)

private class VpTheme(val bg: Brush, val card: Color, val ink: Color, val sub: Color, val a1: Color, val a2: Color, val modern: Boolean)
private fun vpTheme(): VpTheme =
    if (Skin.classic) VpTheme(Brush.verticalGradient(listOf(Color(0xFFF6EEF7), Color(0xFFEFE4F1))), Color.White, Color(0xFF2B2136), Color(0xFF7A6B86), Skin.vars.title2, Skin.vars.title1, false)
    else VpTheme(Brush.verticalGradient(listOf(Color(0xFF161D24), Color(0xFF0F1418))), Color.White.copy(alpha = 0.075f), Color(0xFFE6EEF5), Color(0xFF93A4B4), Skin.accent.l, Skin.accent.a, true)

@Composable
fun PetApp() {
    val state = LocalAppState.current
    remember { VaporPet.install(state); VaporPet.k(); 0 }
    val p = VaporPet; val a = p.a; val e = p.e
    val th = vpTheme()
    var frame by remember { mutableIntStateOf(0) }
    // le(): ~11 fps
    LaunchedEffect(Unit) { while (true) { delay(90); com.reverender.r95.core.AppVisibility.awaitForeground(); frame++ } }
    LaunchedEffect(Unit) { while (true) { delay(30_000); p.tick30() } }
    val game = e.game
    LaunchedEffect(game?.reveal) { if (game != null && game.reveal != 0L) { delay(900); p.afterGuess() } }
    DisposableEffect(Unit) { onDispose { p.e.game = null; p.M(); p.S() } }
    p.version
    val card = RoundedCornerShape(14.dp)
    Column(Modifier.fillMaxSize().background(th.bg).verticalScroll(rememberScrollState()).padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // header
        Row(verticalAlignment = Alignment.CenterVertically) {
            T(a.name, color = th.ink, bold = true, size = 17.sp, maxLines = 1, modifier = Modifier.weight(1f, fill = false))
            Spacer(Modifier.width(8.dp))
            Box(Modifier.clip(RoundedCornerShape(999.dp)).background(Brush.linearGradient(listOf(th.a1, th.a2))).padding(horizontal = 8.dp, vertical = 3.dp)) {
                T(p.pe(), color = if (th.modern) Color(0xFF0F1418) else Color.White, bold = true, size = 10.sp)
            }
            Spacer(Modifier.weight(1f))
            T(p.N(), color = th.sub, size = 11.sp)
        }
        // screen
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(if (th.modern) listOf(Color(0xFF2B3640), Color(0xFF12181D)) else listOf(Color(0xFF3A2F4F), Color(0xFF1C1626)))).padding(10.dp),
        ) {
            Canvas(Modifier.fillMaxWidth().aspectRatio(96f / 56f).clip(RoundedCornerShape(10.dp)).pointerInput(Unit) { detectTapGestures { p.tapLcd() } }) {
                val step = frame; drawLcd(step != lastStep); lastStep = step
                // .rr-vp-screen::after: glare + scan lines
                drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.16f), Color.Transparent), Offset.Zero, Offset(size.width * 0.38f, size.height * 0.38f)))
                var y = 0f; while (y < size.height) { drawRect(Color.Black.copy(alpha = 0.05f), Offset(0f, y), Size(size.width, density)); y += 3 * density }
            }
            if (p.P().isNotEmpty() && !a.asleep) Box(Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-8).dp).size(22.dp).border(3.dp, Color(0x40FF3D7F), CircleShape).background(Color(0xFFFF3D7F), CircleShape), contentAlignment = Alignment.Center) {
                T("!", color = Color.White, bold = true, size = 13.sp)
            }
        }
        // sheet
        p.sheet?.let { s -> PetSheet(s, th) }
        // message
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(th.card).padding(horizontal = 12.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
            T(p.msg, color = th.ink, bold = true, size = 12.sp, align = TextAlign.Center)
        }
        // meters
        Column(Modifier.fillMaxWidth().clip(card).background(th.card).padding(horizontal = 12.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(listOf("Hunger" to a.food, "Happy" to a.fun_), listOf("Energy" to a.energy, "Health" to a.health)).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { (label, v0) ->
                        val v = v0.roundToInt()
                        Column(Modifier.weight(1f)) {
                            Row { T(label, color = th.sub, size = 11.sp, modifier = Modifier.weight(1f)); T("$v%", color = th.ink, bold = true, size = 11.sp) }
                            Spacer(Modifier.height(4.dp))
                            Canvas(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(3.dp)).background(if (th.modern) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f))) {
                                val cols = if (v < 25) listOf(Color(0xFFFF3D7F), Color(0xFFFF7AA8)) else if (v < 55) listOf(Color(0xFFF0A23C), Color(0xFFFFCF5C)) else listOf(th.a2, th.a1)
                                val w = size.width * v / 100f; val br = Brush.horizontalGradient(cols, 0f, max(1f, w))
                                var x = 0f; val seg = 6 * density; val gap = 2 * density
                                while (x < w) { drawRect(br, Offset(x, 0f), Size(min(seg, w - x), size.height)); x += seg + gap }
                            }
                        }
                    }
                }
            }
        }
        // dock
        val need = mapOf("feed" to (a.food < 25), "play" to (a.fun_ < 25), "clean" to (a.poop > 0), "light" to ((a.energy < 18 && a.lights && !a.asleep) || (!a.lights && !a.asleep)), "med" to a.sick)
        listOf(listOf("feed" to "Feed", "play" to "Play", "clean" to "Clean"), listOf("light" to "Light", "med" to "Medicine", "stats" to "Stats")).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (id, label) ->
                    Box(Modifier.weight(1f).clip(card).background(th.card).clickable { p.D(id) }.padding(top = 10.dp, bottom = 8.dp)) {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            val on = id == "light" && !a.lights
                            Canvas(Modifier.size(27.dp)) {
                                val px = size.width / 9f; val br = if (on) Brush.linearGradient(listOf(th.sub, th.sub)) else Brush.linearGradient(listOf(th.a1, th.a2), Offset.Zero, Offset(size.width, size.height))
                                PET_ICONS.getValue(id).forEachIndexed { r, line -> line.forEachIndexed { c, ch -> if (ch == 'k') drawRect(br, Offset(c * px, r * px), Size(px, px)) } }
                            }
                            T(label, color = th.ink, bold = true, size = 11.sp)
                        }
                        if (need[id] == true) Box(Modifier.align(Alignment.TopEnd).padding(top = 7.dp, end = 9.dp).size(12.dp).background(th.card.copy(alpha = 1f).takeIf { !th.modern } ?: Color(0xFF1C2329), CircleShape).padding(2.dp).background(Color(0xFFFF3D7F), CircleShape))
                    }
                }
            }
        }
    }
}
private var lastStep = -1

@Composable
private fun PetSheet(s: String, th: VpTheme) {
    val p = VaporPet; val a = p.a
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(if (th.modern) Color.White.copy(alpha = 0.07f) else Color.White).border(1.dp, th.a1.copy(alpha = 0.35f), RoundedCornerShape(14.dp)).padding(horizontal = 12.dp, vertical = 10.dp)) {
        @Composable fun Title(t: String, close: (() -> Unit)?) = Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            T(t, color = th.ink, bold = true, size = 12.sp, modifier = Modifier.weight(1f))
            if (close != null) Box(Modifier.size(24.dp).clip(CircleShape).background(if (th.modern) Color.White.copy(alpha = 0.07f) else Color.Black.copy(alpha = 0.07f)).clickable { close() }, contentAlignment = Alignment.Center) { T("×", color = th.ink, bold = true, size = 15.sp) }
        }
        @Composable fun Choice(mod: Modifier, b: String, small: String?, onClick: () -> Unit) = Column(
            mod.clip(RoundedCornerShape(10.dp)).background(if (th.modern) Color.White.copy(alpha = 0.06f) else Color.White)
                .border(1.dp, if (th.modern) Color.White.copy(alpha = 0.1f) else Color(0x263C1E50), RoundedCornerShape(10.dp)).clickable(onClick = onClick).padding(horizontal = 6.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp),
        ) { T(b, color = th.ink, bold = true, size = 12.sp); if (small != null) T(small, color = th.sub, size = 10.sp) }
        when (s) {
            "feed" -> {
                Title("Feed ${a.name}") { p.feed("close") }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Choice(Modifier.weight(1f), "Meal", "Fills hunger") { p.feed("meal") }
                    Choice(Modifier.weight(1f), "Snack", "A bit of joy") { p.feed("snack") }
                }
            }
            "game" -> {
                val g = p.e.game ?: return@Column
                Title("Which way will ${a.name} look?", null)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Choice(Modifier.weight(1f), "◀ Left", null) { p.guess("L") }
                    Choice(Modifier.weight(1f), "Right ▶", null) { p.guess("R") }
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)) {
                    for (i in 0 until 5) Box(Modifier.size(9.dp).clip(RoundedCornerShape(2.dp)).background(
                        if (i < g.round) (if (g.res.getOrNull(i) == true) Color(0xFF2FBF8F) else Color(0xFFB9AEC2)) else if (i == g.round) th.a1 else Color.Black.copy(alpha = 0.12f)))
                }
            }
            "stats" -> {
                Title("Profile") { p.M() }
                var name by remember { mutableStateOf(a.name) }
                var armed by remember { mutableStateOf(false) }
                Row(Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    T("Name", color = th.sub, size = 11.sp); Spacer(Modifier.width(8.dp))
                    BasicTextField(name, { v -> name = v.take(12); p.rename(name) }, singleLine = true,
                        textStyle = TextStyle(color = th.ink, fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(if (th.modern) Color.White.copy(alpha = 0.08f) else Color.White)
                            .border(1.dp, if (th.modern) Color.White.copy(alpha = 0.14f) else Color(0x333C1E50), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 5.dp))
                }
                Row(Modifier.padding(bottom = 6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val wt = if (a.weight % 1.0 == 0.0) a.weight.toInt().toString() else "%.1f".format(java.util.Locale.US, a.weight)
                    listOf(p.pe() to "Stage", p.N().replace(" old", "") to "Age", "$wt g" to "Weight", "${a.care}" to "Care", "${a.wins}" to "Wins").forEach { (b, l) ->
                        Column(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(if (th.modern) Color.White.copy(alpha = 0.07f) else Color.Black.copy(alpha = 0.04f)).padding(horizontal = 2.dp, vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            T(b, color = th.ink, bold = true, size = 12.sp, maxLines = 1); T(l.uppercase(), color = th.sub, size = 9.sp)
                        }
                    }
                }
                Box(Modifier.clickable { if (armed) p.startOver() else armed = true }.padding(vertical = 2.dp)) {
                    T(if (armed) "Sure? Tap again to start over" else "Start over with a new kitten", color = if (th.modern) Skin.accent.l else Color(0xFFC4005A), bold = true, size = 11.sp)
                }
            }
        }
    }
}
