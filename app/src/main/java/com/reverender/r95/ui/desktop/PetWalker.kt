package com.reverender.r95.ui.desktop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reverender.r95.LocalAppState
import com.reverender.r95.ui.apps.PET_O
import com.reverender.r95.ui.apps.VaporPet
import com.reverender.r95.ui.apps.petSprite
import com.reverender.r95.ui.win95.T
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/** #rr-pet-walker: rr-os-styles' desktop cat (all units are CSS px = dp). */
private object Walker {
    const val be = 48.0; const val Oe = 36.0; const val X = 30.0; const val ge = 16.0
    class Leap(val el: Win, val fx: Double, val tx: Double, val d: Double, val fromLeft: Boolean)
    class Grab(val ox: Double, val oy: Double, val sx: Double, val sy: Double, var moved: Boolean, val hist: ArrayList<DoubleArray>, val at: Long)
    class Squash(val kind: String, val until: Long, val amt: Double)
    class Dust(val x: Double, val y: Double, val dx: Double, val dy: Double, val at: Long)

    var x = 40.0; var y = 0.0; var vx = 0.0; var vy = 0.0; var a = 0.0; var va = 0.0; var dir = 1
    var mode = "walk"; var on: Win? = null; var grab: Grab? = null; var act = "walk"; var actEnd = 0L; var leap: Leap? = null
    var thrown = false; var wasThrown = false; var dizzy = 0L; var onUntil = 0L; var nextJump = System.currentTimeMillis() + 15_000
    var after: String? = null; var impact = 0.0; var squash: Squash? = null; var nextBowl = 0L; var eatTick = 0L
    var fxTemp: String? = null; var fxUntil = 0L; var lastPlayMsg = 0L; var cleanedByHold = false; var hop = false
    var started = false; var Ce = 0L; var me = 0L
    // render results
    var sprite = PET_O.getValue("sit"); var flip = false; var rot = 0.0; var sx = 1.0; var sy = 1.0; var originMid = false
    var fx = ""; var bubble = ""; var shakeAt = 0L; var lastZ = ""; var bowlPop = 0L
    val dust = ArrayList<Dust>()
    var laidX = 0f; var laidY = 0f
    var W = 390.0; var H = 700.0
    var wins: List<Win> = emptyList(); var dens = 1f

    fun Ge() = H
    fun Ae() = min(W - X - 10, 150.0)
    class R(val el: Win, val left: Double, val top: Double, val right: Double, val bottom: Double) { val width get() = right - left }
    fun rectOf(w: Win) = R(w, w.x / dens.toDouble(), w.y / dens.toDouble(), (w.x + w.w) / dens.toDouble(), (w.y + w.h) / dens.toDouble())
    /** Ze(): visible windows */
    fun Ze(): List<R> = wins.filter { !it.minimized && !it.maximized }.map { rectOf(it) }.filter { it.width >= 60 && it.bottom - it.top >= 40 }

    fun Xe(n: String, u: Long = 1400) { fxTemp = n; fxUntil = System.currentTimeMillis() + u }
    /** ze(): medicine by tapping a sick cat */
    fun ze() {
        val c = VaporPet.a; if (!c.sick) return
        c.sick = false; c.health = VaporPet.Y(c.health + 25); c.care++; VaporPet.S(); Xe("pill", 1600)
        act = "shake"; actEnd = System.currentTimeMillis() + 600; after = "lick"
        VaporPet.d("${c.name} took the medicine and feels better!"); VaporPet.k()
    }
    /** Je(): clean by holding */
    fun Je() {
        val c = VaporPet.a; if (c.poop == 0) return
        c.poop = 0; c.poopT = System.currentTimeMillis(); c.care++; VaporPet.S(); Xe("sparkle", 1500)
        VaporPet.d("All clean — ${c.name} is purring."); VaporPet.k()
    }
    fun refillBowl() {
        VaporPet.a.bowl = 100.0; VaporPet.S(); bowlPop = System.currentTimeMillis()
        dustAt(Ae() + X / 2, Ge() - ge, "floor", 4)
        VaporPet.d("Bowl refilled for ${VaporPet.a.name}."); VaporPet.k(); nextBowl = 0
    }

    /** Re(): pick a behaviour (u = sitting on a window) */
    fun Re(n: Long, u: Boolean = false) {
        val c = VaporPet.a
        if (c.asleep) { act = "sleep"; actEnd = n + 6000; return }
        val F = c.energy < 35; val S = c.fun_ > 60
        val Z = if (u) listOf("walk" to 3.0, "sit" to 3.0, "lick" to 1.5, "loaf" to (if (F) 3.0 else 1.0), "yawn" to 1.0, "leave" to 2.5)
        else listOf("walk" to 5.0, "run" to (if (S) 2.5 else 1.0), "sit" to 3.0, "lick" to 1.5, "stretch" to 1.0, "yawn" to (if (F) 2.0 else 0.6), "nap" to (if (F) 3.0 else 0.7), "jump" to (if (S) 2.0 else 1.2))
        var E = Math.random() * Z.sumOf { it.second }
        for ((k, w) in Z) { E -= w; if (E <= 0) { act = k; break } }
        if (act == "jump" && n < nextJump) act = "walk"
        actEnd = n + when (act) {
            "walk" -> 3e3 + Math.random() * 4e3; "run" -> 1200 + Math.random() * 1400; "sit" -> 2500 + Math.random() * 4e3; "lick" -> 2200 + Math.random() * 1800
            "stretch" -> 1300.0; "yawn" -> 1100.0; "nap" -> 8e3 + Math.random() * 12e3; "loaf" -> 6e3 + Math.random() * 8e3; "jump" -> 6e3; "leave" -> 9e3; else -> 3e3
        }.toLong()
        if ((act == "walk" || act == "run") && Math.random() < 0.4) dir = -dir
        if (act == "jump") leap = Ue()
        if (act == "jump" && leap == null) act = "walk"
    }
    /** Ue(): choose a window to leap onto */
    fun Ue(): Leap? {
        val n = Ge(); var u: Leap? = null
        Ze().forEach { r ->
            val E = n - r.top
            if (E < 70 || E > max(430.0, H * 0.85) || r.width < 120 || r.top < 30) return@forEach
            if (r.bottom < n - 6 && r.bottom > n - Oe) return@forEach
            var ee = abs(x - (r.left - 90)) < abs(x - (r.right + 40))
            var j = if (ee) r.left - 70 - be else r.right + 70
            if (j < 4 || j > W - be - 4) { ee = !ee; j = if (ee) r.left - 70 - be else r.right + 70 }
            if (j < 4 || j > W - be - 4) return@forEach
            val Z = if (ee) r.left + 24 else r.right - be - 24
            val ye = abs(j - x) + E * 0.5
            if (u == null || ye < u!!.d) u = Leap(r.el, j, Z, ye, ee)
        }
        return u
    }
    /** Ke(): impact squash + dust */
    fun Ke(n: String, u: Double, F: Boolean = false) {
        val s = abs(u)
        if (!thrown && !F) return
        if (s < 160 && !F) return
        squash = Squash(n, System.currentTimeMillis() + min(190.0, 90 + s / 12).toLong(), min(0.42, 0.12 + s / 5200))
        if (n == "wallL" || n == "wallR") dir = if (n == "wallL") -1 else 1
        val E = if (n == "wallL") x else if (n == "wallR") x + be else x + be / 2
        val Z = if (n == "ceil") y else if (n == "floor") y + Oe else y + Oe / 2
        dustAt(E, Z, n, min(6, 2 + (s / 400).roundToInt()))
    }
    /** it(): dust specks */
    fun dustAt(n: Double, u: Double, F: String, s: Int) {
        if (dust.size > 24) return
        val now = System.currentTimeMillis()
        repeat(s) {
            val ee = when (F) { "floor" -> Math.PI + Math.random() * Math.PI; "ceil" -> Math.random() * Math.PI; "wallL" -> -Math.PI / 2 + Math.random() * Math.PI; else -> Math.PI / 2 + Math.random() * Math.PI }
            val j = 10 + Math.random() * 16
            dust.add(Dust(n, u, cos(ee) * j, sin(ee) * j, now))
        }
    }
    /** ot(): land on y = n (on window u) */
    fun ot(n: Double, u: Win?) {
        val F = max(impact, abs(vy)); val hard = thrown && F > 700
        impact = 0.0; wasThrown = thrown; y = n - Oe; vy = 0.0; vx = 0.0; va = 0.0; a = 0.0; on = u; mode = "walk"; thrown = false; leap = null
        val E = System.currentTimeMillis(); val Z = wasThrown; wasThrown = false
        if (Z) Ke("floor", F, true)
        if (hard && F > 1300) { dizzy = E + 1600; act = "dizzy"; actEnd = dizzy; after = "shake"; return }
        if (hard) { act = "arch"; actEnd = E + 900; after = "shake"; return }
        if (Z) { act = "landed"; actEnd = E + 280; after = "lick"; return }
        act = "landed"; actEnd = E + 260
        if (u != null) onUntil = E + 6000 + (Math.random() * 12000).toLong()
    }
    /** C(): flight */
    fun C(n: Double) {
        val g = 2400.0; val F = Ge(); val px = x; val py = y
        vy += g * n
        if (thrown) { vx *= 0.6.pow(n); va *= 0.5.pow(n) }
        x += vx * n; y += vy * n
        if (thrown) a += va * n
        if (x < 0) { Ke("wallL", vx); x = 0.0; vx = abs(vx) * 0.55; va = -va * 0.6 }
        if (x > W - be) { Ke("wallR", vx); x = W - be; vx = -abs(vx) * 0.55; va = -va * 0.6 }
        if (y < 0) { Ke("ceil", vy); y = 0.0; vy = abs(vy) * 0.5 }
        for (r in Ze()) {
            if (x + be <= r.left + 4 || x >= r.right - 4 || y + Oe <= r.top || y >= r.bottom) continue
            if (py + Oe <= r.top + 2 && vy >= 0) {
                if (thrown && abs(vy) > 520) { Ke("floor", vy); impact = max(impact, abs(vy)); y = r.top - Oe; vy = -vy * 0.32; va *= 0.5; vx *= 0.7 }
                else { ot(r.top, r.el); return }
            } else if (py >= r.bottom - 2 && vy < 0) { Ke("ceil", vy); y = r.bottom; vy = abs(vy) * 0.4 }
            else if (px + be <= r.left + 6) { Ke("wallR", vx); x = r.left - be; vx = -abs(vx) * 0.5; va = -va * 0.6 }
            else if (px >= r.right - 6) { Ke("wallL", vx); x = r.right; vx = abs(vx) * 0.5; va = -va * 0.6 }
        }
        if (y + Oe >= F) {
            if (thrown && abs(vy) > 520) { Ke("floor", vy); impact = max(impact, abs(vy)); y = F - Oe; vy = -vy * 0.35; vx *= 0.7; va *= 0.6 }
            else ot(F, null)
        }
    }
    /** h(): grounded behaviour; returns the pose */
    fun h(n: Double, u: Long): String {
        val c = VaporPet.a
        val F = Ge(); val lim = W - be - 4; val E = on
        var Z: R? = null
        if (E != null) {
            if (E.minimized || E.maximized || E !in wins) { mode = "air"; on = null; vx = 0.0; vy = 0.0; return "fall" }
            Z = rectOf(E); y = Z.top - Oe
        } else y = F - Oe
        if (act == "dizzy" || act == "arch" || act == "landed" || act == "shake") {
            if (u > actEnd) {
                val ee = after; after = null
                when (ee) { "shake" -> { act = "shake"; actEnd = u + 600 }; "lick" -> { act = "lick"; actEnd = u + 1800 + (Math.random() * 1200).toLong() }; else -> Re(u, E != null) }
            }
            return if (act == "landed") "land" else act
        }
        if (c.asleep && act != "sleep") { act = "sleep"; actEnd = u + 6000 }
        if (!c.asleep && act == "sleep") Re(u, E != null)
        if (E != null && u > onUntil && act != "leave") { act = "leave"; actEnd = u + 9000 }
        if (E == null && !c.asleep && c.food < 55 && u > nextBowl && act in listOf("walk", "run", "sit", "lick", "yawn", "stretch", "sicksit")) { act = "tobowl"; actEnd = u + 15000; nextBowl = u + 25000 }
        if (c.sick && E == null && (act == "run" || act == "jump")) { act = "sicksit"; actEnd = u + 4000 }
        if (u > actEnd && act != "crouch") Re(u, E != null)
        val j = if (Z != null) Z.left + 2 else 4.0; val ye = if (Z != null) Z.right - be - 2 else lim
        when (act) {
            "sleep", "nap", "loaf" -> return "sleep"
            "sit" -> return if (c.sick) "sicksit" else "sit"
            "sicksit" -> return "sicksit"
            "lick" -> return "lick"
            "tobowl" -> {
                if (E != null) { Re(u, true); return "sit" }
                val Me = Ae(); val Pe = if (x + be / 2 < Me + X / 2) -1 else 1
                val xe = if (Pe < 0) Me - be + 10 else Me + X - 10; val qe = xe - x
                if (abs(qe) > 3) {
                    dir = if (qe > 0) 1 else -1
                    val et = abs(qe) > 220 && !c.sick
                    x += dir * min(abs(qe), n * (if (et) 170 else 55))
                    return if (et) "run" else "walk"
                }
                dir = if (Pe < 0) 1 else -1
                if (c.bowl <= 0) { act = "beg"; actEnd = u + 4000; nextBowl = u + 30000; VaporPet.d("${c.name} is waiting at an empty bowl."); return "beg" }
                act = "eatbowl"; actEnd = u + 6000; eatTick = u; return "eatbowl"
            }
            "eatbowl" -> {
                if (u - eatTick > 700) {
                    eatTick = u
                    if (c.bowl > 0) { c.bowl = max(0.0, c.bowl - 8); c.food = VaporPet.Y(c.food + 6); c.weight += 0.1; VaporPet.S(); VaporPet.k() }
                }
                if (c.food >= 95 || c.bowl <= 0) { act = "lick"; actEnd = u + 2200; c.weight = (c.weight * 10).roundToInt() / 10.0; VaporPet.S() }
                return "eatbowl"
            }
            "beg" -> return "beg"
            "stretch" -> return "stretch"
            "yawn" -> return "yawn"
            "run", "walk" -> {
                var rt = if (act == "run") 170.0 else if (E != null) 55.0 else 48.0
                if (c.sick) rt *= 0.6
                x += dir * n * rt
                if (x > ye) { x = ye; dir = -1 }
                if (x < j) { x = j; dir = 1 }
                return act
            }
            "leave" -> {
                if (E == null || Z == null) { Re(u); return "sit" }
                dir = if (x + be / 2 - Z.left < Z.right - x - be / 2) -1 else 1
                x += dir * n * 60
                if (x <= Z.left - be * 0.35 || x >= Z.right - be * 0.65) { mode = "air"; on = null; vx = dir * 150.0; vy = -380.0; thrown = false; hop = true }
                return "walk"
            }
            "jump" -> {
                val De = leap
                if (De == null || De.el !in wins || De.el.minimized) { Re(u); return "sit" }
                val Ye = De.fx - x
                if (abs(Ye) > 3) { dir = if (Ye > 0) 1 else -1; x += dir * min(abs(Ye), n * 150); return "run" }
                act = "crouch"; actEnd = u + 380; dir = if (De.tx > x) 1 else -1
                return "crouch"
            }
            "crouch" -> {
                if (u < actEnd) return "crouch"
                val r = leap ?: run { Re(u); return "sit" }
                val s = rectOf(r.el); val yy = y + Oe - s.top + 30
                val I = -sqrt(2 * 2400 * max(40.0, yy)); val re = -I / 2400
                val ve = if (r.fromLeft) s.left - be + 16 else s.right - 16
                vx = (ve - x) / re; vy = I; mode = "air"; thrown = false
                nextJump = u + 20000 + (Math.random() * 25000).toLong()
                return "jump"
            }
        }
        return "sit"
    }

    /** te(): one animation frame; returns false when skipped by the idle throttle */
    fun frame(nowMs: Long): Boolean {
        val o = PET_O; val c = VaporPet.a
        if (!started) { started = true; y = Ge() - Oe; mode = "walk"; on = null; Re(System.currentTimeMillis()) }
        val n = nowMs
        val u = min(0.05, (n - (if (Ce == 0L) n else Ce)) / 1e3); Ce = n
        val F = mode != "walk" || act == "run" || act == "crouch" || act == "jump" || act == "tobowl"
        if (!F && n - me < 85) return false
        val dt = min(0.1, (n - (if (me == 0L) n else me)) / 1e3).let { if (it == 0.0) u else it }; me = n
        val E = System.currentTimeMillis()
        // long-press clean (holdT)
        grab?.let { g -> if (!g.moved && !cleanedByHold && E - g.at > 700 && c.poop > 0) { Je(); cleanedByHold = true } }
        if (mode == "walk" && on == null && grab == null) { /* resize: stay on the floor */ }
        val Z = when (mode) {
            "air" -> { C(u); if (mode != "air") h(0.0, E) else if (thrown) "tumble" else if (vy < 0) "jump" else "fall" }
            "drag" -> { a += (va * 0.02 - a) * 0.2; va *= 0.8; "dangle" }
            else -> h(dt, E)
        }
        var j = ""
        var ee: Array<String> = when (Z) {
            "walk" -> if ((E / 170) % 2 == 1L) o.getValue("walk1") else o.getValue("walk2")
            "run" -> if ((E / 95) % 2 == 1L) o.getValue("run1") else o.getValue("run2")
            "eatbowl" -> if ((E / 300) % 3 != 0L) o.getValue("eatdown") else o.getValue("crouch")
            "beg" -> if ((E / 900) % 2 == 1L) o.getValue("sitB") else o.getValue("yawn")
            "sicksit" -> if ((E / 2000) % 3 == 0L) o.getValue("yawn") else o.getValue("sad")
            "sit" -> if ((E / 1400) % 5 == 0L) o.getValue("sitB") else if ((E / 2600) % 7 == 3L) o.getValue("happy") else o.getValue("sit")
            "lick" -> if ((E / 330) % 2 == 1L) o.getValue("lick1") else o.getValue("lick2")
            "stretch" -> o.getValue("stretch"); "yawn" -> o.getValue("yawn")
            "sleep" -> { j = "zzz"; if ((E / 1300) % 2 == 1L) o.getValue("sleep") else o.getValue("sleepB") }
            "crouch" -> o.getValue("crouch"); "jump" -> o.getValue("jump"); "fall" -> o.getValue("run1"); "land" -> o.getValue("land")
            "dizzy" -> { j = "dizzy"; o.getValue("no") }
            "dangle" -> o.getValue("dangle")
            "arch" -> { j = "hiss"; o.getValue("arch") }
            "shake" -> o.getValue("sit")
            else -> o.getValue("no")
        }
        var ye = dir < 0 && Z != "dangle"; var Me = 0.0; var Pe = 1.0; var xe = 1.0
        val now = System.currentTimeMillis()
        if (Z == "tumble") {
            if (dir == 0) dir = 1
            val et = hypot(vx, vy); val rt = atan2(vy, if (abs(vx) > 0) abs(vx) else 0.001) * 180 / Math.PI
            if (abs(vx) > 60) dir = if (vx < 0) -1 else 1
            ye = dir < 0
            if (et > 700) {
                ee = if (vy < -abs(vx) * 0.6) o.getValue("jump") else if (vy > abs(vx) * 0.9) o.getValue("splay") else o.getValue("run1")
                Me = (if (ee === o["splay"]) rt * 0.25 else rt * 0.8) * (if (ye) -1 else 1); a = Me
            } else if (et > 260) { ee = if (vy > 0) o.getValue("splay") else o.getValue("dangle"); Me = a + (if (vx >= 0) 1 else -1) * 14; a = Me }
            else { ee = o.getValue("splay"); Me = a }
            val De = squash
            if (De != null && now < De.until) {
                val Ye = De.amt
                when (De.kind) {
                    "floor" -> { ee = o.getValue("land"); Pe = 1 + Ye; xe = 1 - Ye; Me = 0.0 }
                    "ceil" -> { ee = o.getValue("splay"); Pe = 1 + Ye; xe = 1 - Ye; Me = 0.0 }
                    else -> { ee = o.getValue("wall"); ye = De.kind == "wallR"; Pe = 1 - Ye; xe = 1 + Ye * 0.6; Me = 0.0 }
                }
            }
        } else if (Z == "dangle") Me = max(-35.0, min(35.0, a))
        else if (Z == "jump") Me = -12.0 * dir
        else if (Z == "land" && squash != null && now < squash!!.until + 80) { Pe = 1.18; xe = 0.84 }
        sprite = ee; flip = ye; rot = Me; sx = Pe; sy = xe
        originMid = (Z == "tumble" && Pe == 1.0) || Z == "dangle"
        if (Z == "shake" && lastZ != "shake") shakeAt = E
        lastZ = Z
        if (j.isEmpty() && fxTemp != null && E < fxUntil) j = fxTemp!!
        else if (j.isEmpty() && c.sick && mode == "walk") j = "sick"
        else if (j.isEmpty() && c.poop > 0 && mode == "walk") j = "dirty"
        fx = j
        val I = VaporPet.P()
        bubble = if (mode != "walk" || (j.isNotEmpty() && j != "dirty" && j != "sick")) "" else if (c.sick) "💊" else when (I.firstOrNull()) {
            "hungry" -> "🍙"; "bored" -> "🎾"; "mess" -> "💩"; "sick" -> "💊"; "tired" -> "💤"; else -> ""
        }
        dust.removeAll { E - it.at > 520 }
        return true
    }

    // pointer handling: Ee / Le / Se
    fun down(px: Double, py: Double) {
        cleanedByHold = false
        grab = Grab(px - x, py - y, px, py, false, arrayListOf(doubleArrayOf(px, py, System.nanoTime() / 1e6)), System.currentTimeMillis())
    }
    fun move(px: Double, py: Double) {
        val g = grab ?: return
        if (abs(px - g.sx) + abs(py - g.sy) > 6) g.moved = true
        if (!g.moved) return
        mode = "drag"; on = null; leap = null
        val F = max(-be / 2, min(W - be / 2, px - g.ox)); val s = max(-Oe / 2, min(H - Oe / 2, py - g.oy))
        va = (F - x) * 0.9; x = F; y = s
        g.hist.add(doubleArrayOf(px, py, System.nanoTime() / 1e6)); while (g.hist.size > 6) g.hist.removeAt(0)
    }
    fun up(isUp: Boolean, open: () -> Unit) {
        val g = grab ?: return
        grab = null
        val c = VaporPet.a
        if (!g.moved) {
            if (!isUp || cleanedByHold) { cleanedByHold = false; return }
            if (c.sick) { ze(); return }
            open(); return
        }
        val F = g.hist; val s = F.first(); val E = F.last(); val Z = max(16.0, E[2] - s[2]) / 1e3
        vx = max(-2600.0, min(2600.0, (E[0] - s[0]) / Z)); vy = max(-2600.0, min(2600.0, (E[1] - s[1]) / Z))
        va = vx * 0.9; mode = "air"; on = null; thrown = true; leap = null; impact = 0.0; dir = if (vx < 0) -1 else 1
        val ee = hypot(vx, vy); val j = System.currentTimeMillis()
        if (ee > 250) {
            c.fun_ = VaporPet.Y(c.fun_ + min(8.0, 2 + ee / 450)); c.energy = VaporPet.Y(c.energy - 1); c.care++
            if (j - lastPlayMsg > 4000) { lastPlayMsg = j; VaporPet.d("Wheee! ${c.name} loves flying around.") }
        }
        VaporPet.S(); VaporPet.k()
    }
}

private val CAT = mapOf('k' to Color(0xFF3B220F), 'w' to Color(0xFFF28A2E), 'p' to Color(0xFFFFC7A0), 'e' to Color(0xFF2A1608))
private var walkerFrame by mutableIntStateOf(0)
/** what the cat's composition depends on (layer, bubble, effects); the per-frame motion only redraws via [walkerFrame] */
private var walkerKey by mutableIntStateOf(0)
private fun walkerCovered(t: Walker): Boolean {
    val isUp = t.mode != "walk" || t.on != null
    return !isUp && t.grab == null && t.wins.any { w -> !w.minimized && t.rectOf(w).let { r -> w.maximized || (t.x + t.be > r.left && t.x < r.right && t.y + t.Oe > r.top && t.y < r.bottom) } }
}

/** Runs the walker; call once inside the desktop area. */
@Composable
fun PetWalkerDriver(wm: WindowManager) {
    val state = LocalAppState.current
    LaunchedEffect(Unit) {
        VaporPet.install(state)
        var last30 = System.currentTimeMillis(); var lastPace = 0L
        while (true) {
            withFrameNanos { }
            com.reverender.r95.core.AppVisibility.awaitForeground()
            // the cat is cheap: always a full 60 fps, even when the rest of the desktop is paced at 30
            if (System.nanoTime() - lastPace < 15_500_000L) continue
            lastPace = System.nanoTime()
            if (state.lowPower) { kotlinx.coroutines.delay(500); continue }
            Walker.wins = wm.windows.toList(); Walker.dens = wm.density
            if (wm.areaW > 0) { Walker.W = (wm.areaW / wm.density).toDouble(); Walker.H = (wm.areaH / wm.density).toDouble() }
            val now = System.currentTimeMillis()
            if (now - last30 > 30_000) { last30 = now; VaporPet.tick30() }
            if (com.reverender.r95.core.Perf.time(3) { Walker.frame(System.nanoTime() / 1_000_000) }) walkerFrame++
            run {
                val t = Walker
                val key = java.util.Objects.hash(t.mode != "walk" || t.on != null, t.bubble, walkerCovered(t), t.dust.isEmpty(), t.fx,
                    if (t.fx.isNotEmpty()) walkerFrame else 0) // the effect glyphs animate in composition
                if (key != walkerKey) walkerKey = key
            }
        }
    }
}

/** #rr-pet-bowl + #rr-pet-walker. [up] = the layer above the windows (cat in the air or on a window). */
@Composable
fun BoxScope.PetWalkerLayer(wm: WindowManager, up: Boolean) {
    val state = LocalAppState.current
    if (state.lowPower) return
    walkerKey
    val t = Walker
    val isUp = t.mode != "walk" || t.on != null
    if (!up) {
        // the bowl
        Canvas(
            Modifier.offset { walkerFrame; IntOffset((t.Ae() * density).roundToInt(), ((t.Ge() - t.ge) * density).roundToInt()) }.size(30.dp, 16.dp)
                .graphicsLayer { walkerFrame; val pop = System.currentTimeMillis() - t.bowlPop; if (pop < 300) { val k = 1 + 0.25f * sin(pop / 300f * Math.PI.toFloat()); scaleX = k; scaleY = k } }
                .pointerInput(Unit) { detectTapGestures { t.refillBowl() } },
        ) { walkerFrame; drawBowl(VaporPet.a.bowl) }
    }
    if (isUp == up) {
        Box(
            Modifier.offset { walkerFrame; IntOffset((t.x * density).roundToInt(), (t.y * density).roundToInt()) }.size(48.dp, 36.dp),
        ) {
            Canvas(
                Modifier.size(48.dp, 36.dp).graphicsLayer {
                    walkerFrame
                    rotationZ = t.rot.toFloat(); scaleX = t.sx.toFloat(); scaleY = t.sy.toFloat()
                    transformOrigin = if (t.originMid) TransformOrigin(0.5f, 0.45f) else TransformOrigin(0.5f, 1f)
                    shadowElevation = 0f
                    val shake = System.currentTimeMillis() - t.shakeAt
                    if (t.lastZ == "shake" && shake < 600) { val k = shake / 600f; translationX = sin(k * 6 * Math.PI.toFloat()) * 2 * density * (1 - k); rotationZ += sin(k * 6 * Math.PI.toFloat()) * 4 * (1 - k) }
                },
            ) {
                walkerFrame
                val s = size.width / 16f
                petSprite(t.sprite, 0f, 2 * density, s, mapOf('k' to Color.Black.copy(alpha = 0.35f)), t.flip) // drop shadow
                petSprite(t.sprite, 0f, 0f, s, CAT, t.flip)
            }
            if (t.bubble.isNotEmpty()) Box(Modifier.offset(26.dp, (-22).dp).background(Color.White, RoundedCornerShape(9.dp)).border(1.dp, Color.Black, RoundedCornerShape(9.dp)).padding(horizontal = 5.dp)) { T(t.bubble, color = Color.Black, size = 13.sp) }
            WalkerFx(t.fx)
        }
    }

    // the touch target lives only in the top layer and stays mounted, so a drag survives the cat changing layers
    if (up) {
        val covered = walkerCovered(t)
        if (!covered) Box(
            Modifier.offset { walkerFrame; t.laidX = t.x.toFloat(); t.laidY = t.y.toFloat(); IntOffset((t.x * density).roundToInt(), (t.y * density).roundToInt()) }.size(48.dp, 36.dp)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val d0 = awaitFirstDown(); d0.consume()
                        val dd = density.toDouble()
                        t.down(t.laidX + d0.position.x / dd, t.laidY + d0.position.y / dd)
                        var ok = false
                        while (true) {
                            val ev = awaitPointerEvent()
                            val ch = ev.changes.firstOrNull { it.id == d0.id } ?: break
                            if (!ch.pressed) { ok = true; ch.consume(); break }
                            if (ch.positionChange() != Offset.Zero) { t.move(t.laidX + ch.position.x / dd, t.laidY + ch.position.y / dd); ch.consume() }
                        }
                        t.up(ok) { wm.open("vaporpet") }
                    }
                },
        )
    }
    if (up && t.dust.isNotEmpty()) Canvas(Modifier.matchParentSize()) {
        walkerFrame
        val now = System.currentTimeMillis()
        t.dust.toList().forEach { d ->
            val k = ((now - d.at) / 500f).coerceIn(0f, 1f); val sz = 4 * density * (1 - 0.6f * k)
            val c = Offset(((d.x + d.dx * k) * density).toFloat(), ((d.y + d.dy * k) * density).toFloat())
            drawRect(Color.Black.copy(alpha = 0.15f * (1 - k)), c - Offset(sz / 2 + density, sz / 2 + density), Size(sz + 2 * density, sz + 2 * density))
            drawRect(Color.White.copy(alpha = 0.75f * (1 - k)), c - Offset(sz / 2, sz / 2), Size(sz, sz))
        }
    }
}

@Composable
private fun WalkerFx(fx: String) {
    if (fx.isEmpty()) return
    val now = System.currentTimeMillis()
    when (fx) {
        "zzz" -> {
            listOf(0L, 1300L).forEachIndexed { i, delay ->
                val k = ((now + 2600 - delay) % 2600) / 2600f
                val al = if (k < 0.25f) k / 0.25f else 1 - (k - 0.25f) / 0.75f
                T("z", color = Color.White, bold = true, size = if (i == 0) 12.sp else 9.sp,
                    modifier = Modifier.offset((30 + i * 8 + 8 * k).dp, (-16 - i * 8 + 4 - 16 * k).dp).graphicsLayer { alpha = al })
            }
        }
        "dizzy" -> T("✦ ✧ ✦", color = Color(0xFFFFE08A), bold = true, size = 10.sp, modifier = Modifier.offset(6.dp, (-10).dp).graphicsLayer { rotationZ = (now % 1100) / 1100f * 360f })
        "hiss" -> T("!!", color = Color(0xFFFF5C8A), bold = true, size = 13.sp, modifier = Modifier.offset(34.dp, (-12 + 2 * sin(now / 72.0)).dp))
        else -> {
            val txt = mapOf("sick" to "✚", "dirty" to "≋", "rub" to "✧", "sparkle" to "✦ ✧ ✦", "pill" to "♥")[fx] ?: return
            val col = when (fx) { "sick" -> Color(0xFF8CFF9E); "dirty" -> Color(0xFFC9A27A); "pill" -> Color(0xFFFF7AA8); else -> Color(0xFFFFF3A0) }
            T(txt, color = col, bold = true, size = 12.sp, modifier = Modifier.offset(30.dp, (-16 + 2 * sin(now / 140.0)).dp))
        }
    }
}

/** $e(): the bowl, 15 × 8 pixels */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBowl(bowl: Double) {
    val p = size.width / 15f
    fun r(x: Int, y: Int, w: Int, h: Int, c: Color) = drawRect(c, Offset(x * p, y * p), Size(w * p, h * p))
    val n = Color(0xFF3B220F); val u = Color(0xFFFF7AA8); val F = Color(0xFFC94F80); val s1 = Color(0xFF8A5A2B); val E = Color(0xFFC98A4B)
    val Z = (bowl / 100 * 4).roundToInt()
    for (ee in 0 until Z) {
        val j = 9 - max(0, Z - 1 - ee) * 2 + (if (ee == 0) 2 else 0)
        val ye = ((15 - j) / 2.0).roundToInt()
        for (m in 0 until j) r(ye + m, 3 - ee + 1, 1, 1, if ((m + ee) % 2 == 1) s1 else E)
    }
    r(0, 4, 15, 1, n); r(1, 7, 13, 1, n); r(0, 4, 1, 2, n); r(14, 4, 1, 2, n); r(1, 6, 1, 1, n); r(13, 6, 1, 1, n)
    r(1, 5, 13, 1, u); r(2, 6, 11, 1, F); r(3, 5, 2, 1, Color.White)
}
