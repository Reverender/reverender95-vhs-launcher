@file:Suppress("NOTHING_TO_INLINE", "unused", "FunctionName")

package com.reverender.r95.ui.viz.js

/*
 * Small JavaScript value model used by code translated from the website (tools/tx.js).
 * Numbers are Double, strings String, booleans Boolean; arrays JsArr; objects JsObj;
 * `undefined`/`null` are Kotlin null.
 */

class JsArr(initial: Int = 8) : ArrayList<Any?>(initial)
open class JsObj : HashMap<String, Any?>(8)
fun interface JsFn { fun call(args: Array<Any?>): Any? }

object Rt {
    @JvmField var mouseX = 0.0
    @JvmField var mouseY = 0.0
    @JvmField var freqDataArr = DoubleArray(1024)
    @JvmField var timeDataArr = DoubleArray(1024) { 128.0 }
    private val win = java.util.concurrent.ConcurrentHashMap<String, Any>()
    private val t0 = System.nanoTime()
    fun nowMs(): Double = (System.nanoTime() - t0) / 1_000_000.0

    /** Math.random(); seedable (mulberry32) so frames can be compared against the browser in tests. */
    private var seed = 0L
    private var seeded = false
    fun seed(s: Long) { seed = s and 0xFFFFFFFFL; seeded = true }
    fun random(): Double {
        if (!seeded) return Math.random()
        seed = (seed + 0x6D2B79F5L) and 0xFFFFFFFFL
        var t = seed.toInt()
        t = (t xor (t ushr 15)) * (t or 1)
        t = (t + ((t xor (t ushr 7)) * (t or 61))) xor t
        return ((t xor (t ushr 14)).toLong() and 0xFFFFFFFFL) / 4294967296.0
    }
    fun win(k: String): Any? = win[k]
    fun setWin(k: String, v: Any?) { if (v == null) win.remove(k) else win[k] = v }
    fun dateNow(): Double = System.currentTimeMillis().toDouble()
    fun newDate(vararg a: Any?): Any? = JsEl("date").also { it.dateMs = if (a.isNotEmpty()) num(a[0]) else dateNow() }

    // timers run on the frame loop (same thread as the translated code)
    private class Timer(val id: Double, val at: Double, val fn: Any?)
    private val timers = ArrayList<Timer>()
    private var timerId = 0.0
    fun setTimeout(fn: Any?, ms: Double): Double { timerId += 1; timers.add(Timer(timerId, nowMs() + (if (ms.isNaN()) 0.0 else ms), fn)); return timerId }
    fun clearTimeout(id: Any?) { val d = num(id); timers.removeAll { it.id == d } }
    /** Call from the frame loop. */
    fun runTimers() {
        if (timers.isEmpty()) return
        val now = nowMs()
        val due = timers.filter { it.at <= now }
        timers.removeAll(due)
        for (t in due) (t.fn as? JsFn)?.call(arrayOf())
    }

    // localStorage, backed by SharedPreferences when the app sets [store]
    var store: android.content.SharedPreferences? = null
    fun ls_getItem(k: Any?): Any? = store?.getString("ls_" + str(k), null)
    fun ls_setItem(k: Any?, v: Any?) { store?.edit()?.putString("ls_" + str(k), str(v))?.apply() }
    fun ls_removeItem(k: Any?) { store?.edit()?.remove("ls_" + str(k))?.apply() }
    fun object_keys(o: Any?): JsArr { val a = JsArr(); (o as? JsObj)?.keys?.forEach { a.add(it) }; return a }
    fun object_assign(t: Any?, vararg src: Any?): Any? { val d = t as? JsObj ?: return t; src.forEach { (it as? JsObj)?.let { m -> d.putAll(m) } }; return d }
}

fun jsZeros(n: Double, fill: Any? = 0.0): JsArr { val c = jsIdx(n).coerceAtLeast(0); val a = JsArr(c); repeat(c) { a.add(fill) }; return a }
fun jsTypeOf(x: Any?): String = when (x) { null -> "undefined"; is Number -> "number"; is String -> "string"; is Boolean -> "boolean"; is JsFn -> "function"; else -> "object" }
fun jsGetContext(c: Any?): Ctx = (c as JsEl).ctx()

/** Method call on a dynamic value (DOM stand-ins, objects holding functions). */
fun jsInvoke(o: Any?, m: String, vararg a: Any?): Any? = when (o) {
    null -> null
    is JsEl -> o.invoke(m, a)
    is JsObj -> (o[m] as? JsFn)?.call(arrayOf(*a))
    is JsArr -> when (m) { "keys" -> jsArrayFrom(o.size.toDouble()) { _, i, _ -> i }; "flat" -> o; else -> null }
    is String -> when (m) { "trim" -> o.trim(); "toUpperCase" -> o.uppercase(); "toLowerCase" -> o.lowercase(); "startsWith" -> o.startsWith(str(a.getOrNull(0))); "endsWith" -> o.endsWith(str(a.getOrNull(0))); "replace" -> o.replace(str(a.getOrNull(0)), str(a.getOrNull(1))); else -> null }
    else -> null
}

fun jsArr(vararg v: Any?): JsArr { val a = JsArr(maxOf(4, v.size)); for (x in v) a.add(x); return a }
fun jsObj(vararg kv: Pair<String, Any?>): JsObj { val o = JsObj(); for ((k, v) in kv) o[k] = v; return o }

fun num(x: Any?): Double = when (x) {
    is Double -> x
    is Number -> x.toDouble()
    is Boolean -> if (x) 1.0 else 0.0
    is String -> { val t = x.trim(); if (t.isEmpty()) 0.0 else t.toDoubleOrNull() ?: Double.NaN }
    null -> Double.NaN
    else -> Double.NaN
}

fun truthy(x: Any?): Boolean = when (x) {
    null -> false
    is Boolean -> x
    is Double -> x != 0.0 && !x.isNaN()
    is Number -> x.toDouble().let { it != 0.0 && !it.isNaN() }
    is String -> x.isNotEmpty()
    else -> true
}

fun jsStr(d: Double): String {
    if (d.isNaN()) return "NaN"
    if (d.isInfinite()) return if (d > 0) "Infinity" else "-Infinity"
    if (d == Math.rint(d) && kotlin.math.abs(d) < 1e15) return d.toLong().toString()
    val a = kotlin.math.abs(d)
    if (a < 1e-3 || a >= 1e21) {
        if (a >= 1e-7 && a < 1e-3) return java.math.BigDecimal(d.toString()).toPlainString()
    }
    return d.toString()
}

fun str(x: Any?): String = when (x) {
    null -> "undefined"
    is String -> x
    is Double -> jsStr(x)
    is Number -> jsStr(x.toDouble())
    is Boolean -> x.toString()
    is JsArr -> x.joinToString(",") { if (it == null) "" else str(it) }
    is JsObj -> "[object Object]"
    else -> x.toString()
}

private val POW10 = DoubleArray(12) { Math.pow(10.0, it.toDouble()) }
fun jsToFixed(d: Double, digits: Double): String {
    val n = digits.toInt().coerceIn(0, 11)
    if (d.isNaN()) return "NaN"
    if (kotlin.math.abs(d) >= 1e15) return jsStr(d)
    val p = POW10[n]
    val neg = d < 0
    val scaled = Math.floor(kotlin.math.abs(d) * p + 0.5)
    val whole = (scaled / p).toLong()
    val frac = (scaled - whole * p).toLong()
    val sb = StringBuilder(16)
    if (neg && scaled != 0.0) sb.append('-')
    sb.append(whole)
    if (n > 0) {
        sb.append('.')
        val fs = frac.toString()
        for (i in fs.length until n) sb.append('0')
        sb.append(fs)
    }
    return sb.toString()
}

inline fun jsInt(d: Double): Int = if (d.isNaN() || d.isInfinite()) 0 else d.toLong().toInt()
inline fun jsIdx(d: Double): Int = if (d.isNaN()) -1 else d.toInt()
fun jsRound(d: Double): Double = Math.floor(d + 0.5)
fun jsMax(a: Double, b: Double): Double = if (a.isNaN() || b.isNaN()) Double.NaN else if (a > b) a else b
fun jsMin(a: Double, b: Double): Double = if (a.isNaN() || b.isNaN()) Double.NaN else if (a < b) a else b
fun jsMaxN(vararg v: Double): Double { var r = Double.NEGATIVE_INFINITY; for (x in v) { if (x.isNaN()) return Double.NaN; if (x > r) r = x }; return r }
fun jsMinN(vararg v: Double): Double { var r = Double.POSITIVE_INFINITY; for (x in v) { if (x.isNaN()) return Double.NaN; if (x < r) r = x }; return r }
fun jsHypot(vararg v: Double): Double { var s = 0.0; for (x in v) s += x * x; return Math.sqrt(s) }
fun jsParseInt(x: Any?, radix: Double = Double.NaN): Double {
    val r = if (radix.isNaN() || radix == 0.0) 10 else radix.toInt()
    var s = str(x).trim()
    if (r == 16 || radix.isNaN()) { val neg = s.startsWith("-"); val b = s.removePrefix("-").removePrefix("+"); if (b.startsWith("0x") || b.startsWith("0X")) return jsParseInt((if (neg) "-" else "") + b.substring(2), 16.0) }
    if (r == 10) return Regex("^[+-]?\\d+").find(s)?.value?.toDouble() ?: Double.NaN
    val digits = "0123456789abcdefghijklmnopqrstuvwxyz".substring(0, r)
    val neg = s.startsWith("-"); s = s.removePrefix("-").removePrefix("+").lowercase()
    val m = s.takeWhile { it in digits }
    if (m.isEmpty()) return Double.NaN
    val v = m.fold(0.0) { a, ch -> a * r + digits.indexOf(ch) }
    return if (neg) -v else v
}
fun jsParseFloat(x: Any?): Double = str(x).trim().let { s -> Regex("^[+-]?(\\d+\\.?\\d*|\\.\\d+)([eE][+-]?\\d+)?").find(s)?.value?.toDouble() ?: Double.NaN }
fun jsNumberFn_isFinite(x: Any?): Boolean = x is Number && x.toDouble().isFinite()
fun jsNumberFn_isNaN(x: Any?): Boolean = x is Number && x.toDouble().isNaN()
fun jsNumberFn_isInteger(x: Any?): Boolean = x is Number && x.toDouble().let { it.isFinite() && it == Math.floor(it) }
fun jsNumberFn_parseFloat(x: Any?): Double = jsParseFloat(x)
fun jsNumberFn_parseInt(x: Any?): Double = jsParseInt(x)

fun jsEq(a: Any?, b: Any?): Boolean {
    if (a === b) return true
    if (a == null || b == null) return false
    if (a is Number && b is Number) return a.toDouble() == b.toDouble()
    if (a is String && b is String) return a == b
    if (a is Boolean && b is Boolean) return a == b
    return false
}

inline fun jsAnd(a: Any?, b: () -> Any?): Any? = if (truthy(a)) b() else a
inline fun jsOr(a: Any?, b: () -> Any?): Any? = if (truthy(a)) a else b()
inline fun jsNullish(a: Any?, b: () -> Any?): Any? = a ?: b()
inline fun jsOrN(a: Double, b: () -> Double): Double = if (a != 0.0 && !a.isNaN()) a else b()
inline fun jsOrS(a: String, b: () -> String): String = if (a.isNotEmpty()) a else b()
inline fun jsOrB(a: Boolean, b: () -> Boolean): Boolean = a || b()
inline fun jsOrND(a: Any?, b: () -> Double): Double = if (truthy(a)) num(a) else b()
inline fun jsOrSD(a: Any?, b: () -> String): String = if (truthy(a)) str(a) else b()
inline fun jsOrBD(a: Any?, b: () -> Boolean): Boolean = if (truthy(a)) true else b()
inline fun jsOrCD(a: Any?, b: () -> Ctx): Ctx = if (truthy(a)) a as Ctx else b()
inline fun jsOrGD(a: Any?, b: () -> Grad): Grad = if (truthy(a)) a as Grad else b()

fun jsLen(x: Any?): Double = when (x) {
    is JsArr -> x.size.toDouble()
    is String -> x.length.toDouble()
    is DoubleArray -> x.size.toDouble()
    is JsObj -> num(x["length"])
    else -> Double.NaN
}

fun jsGet(o: Any?, k: Any?): Any? = when (o) {
    is JsArr -> when (k) {
        is Double -> { val i = k.toInt(); if (i.toDouble() == k && i >= 0 && i < o.size) o[i] else null }
        is Number -> { val i = k.toInt(); if (i >= 0 && i < o.size) o[i] else null }
        "length" -> o.size.toDouble()
        is String -> k.toIntOrNull()?.let { if (it >= 0 && it < o.size) o[it] else null }
        else -> null
    }
    is JsEl -> o.prop(keyOf(k))
    is JsObj -> o[keyOf(k)]
    is String -> when (k) {
        is Number -> { val i = k.toInt(); if (i >= 0 && i < o.length) o[i].toString() else null }
        "length" -> o.length.toDouble()
        else -> null
    }
    is DoubleArray -> if (k is Number) { val i = k.toInt(); if (i >= 0 && i < o.size) o[i] else null } else if (k == "length") o.size.toDouble() else null
    else -> null
}

internal fun keyOf(k: Any?): String = when (k) {
    is String -> k
    is Double -> jsStr(k)
    else -> str(k)
}

fun jsSet(o: Any?, k: Any?, v: Any?) {
    when (o) {
        is JsArr -> {
            val i = when (k) { is Number -> k.toInt(); is String -> k.toIntOrNull() ?: return; else -> return }
            if (i < 0) return
            while (o.size <= i) o.add(null)
            o[i] = v
        }
        is JsEl -> o.setProp(keyOf(k), v)
        is JsObj -> o[keyOf(k)] = v
        is DoubleArray -> if (k is Number) { val i = k.toInt(); if (i >= 0 && i < o.size) o[i] = num(v) }
    }
}

fun jsAddAssign(o: Any?, k: Any?, v: Any?) {
    val cur = jsGet(o, k)
    if (cur is String || v is String) jsSet(o, k, str(cur) + str(v)) else jsSet(o, k, num(cur) + num(v))
}
fun jsOpAssign(o: Any?, k: Any?, op: String, v: Double) {
    val c = num(jsGet(o, k))
    val r = when (op) {
        "-" -> c - v; "*" -> c * v; "/" -> c / v; "%" -> c % v
        "&" -> (jsInt(c) and jsInt(v)).toDouble(); "|" -> (jsInt(c) or jsInt(v)).toDouble()
        "**" -> Math.pow(c, v)
        else -> c
    }
    jsSet(o, k, r)
}
fun jsPostAdd(o: Any?, k: Any?, d: Double): Double { val c = num(jsGet(o, k)); jsSet(o, k, c + d); return c }
fun jsPreAdd(o: Any?, k: Any?, d: Double): Double { val c = num(jsGet(o, k)) + d; jsSet(o, k, c); return c }
fun jsHas(o: Any?, k: Any?): Boolean = when (o) { is JsObj -> o.containsKey(keyOf(k)); is JsArr -> k is Number && k.toInt() in 0 until o.size; else -> false }
fun jsIsArray(x: Any?): Boolean = x is JsArr
fun jsCall(f: Any?, vararg args: Any?): Any? = (f as? JsFn)?.call(arrayOf(*args))

fun jsCharAt(s: String, i: Double): String { val k = jsIdx(i); return if (k >= 0 && k < s.length) s[k].toString() else "" }
fun jsCharCode(s: String, i: Double): Double { val k = jsIdx(i); return if (k >= 0 && k < s.length) s[k].code.toDouble() else Double.NaN }
fun jsSlice(s: String, a: Double, b: Double = s.length.toDouble()): String {
    val n = s.length
    var x = jsIdx(a); var y = if (b.isNaN()) n else b.toInt()
    if (x < 0) x = maxOf(0, n + x); if (y < 0) y = maxOf(0, n + y)
    x = minOf(x, n); y = minOf(y, n)
    return if (y > x) s.substring(x, y) else ""
}
fun jsSplit(s: String, sep: String): JsArr { val a = JsArr(); s.split(sep).forEach { a.add(it) }; return a }

// ---- arrays
private fun arr(x: Any?): JsArr = x as? JsArr ?: JsArr(0)
fun jsPush(a: Any?, vararg v: Any?): Double { val l = arr(a); for (x in v) l.add(x); return l.size.toDouble() }
fun jsPop(a: Any?): Any? { val l = arr(a); return if (l.isEmpty()) null else l.removeAt(l.size - 1) }
fun jsShift(a: Any?): Any? { val l = arr(a); return if (l.isEmpty()) null else l.removeAt(0) }
fun jsUnshift(a: Any?, vararg v: Any?): Double { val l = arr(a); l.addAll(0, v.toList()); return l.size.toDouble() }
fun jsSplice(a: Any?, start: Double, count: Double = Double.POSITIVE_INFINITY): JsArr {
    val l = arr(a); val out = JsArr()
    var s = jsIdx(start); if (s < 0) s = maxOf(0, l.size + s); s = minOf(s, l.size)
    val c = if (count.isInfinite()) l.size - s else count.toInt().coerceIn(0, l.size - s)
    repeat(c) { out.add(l.removeAt(s)) }
    return out
}
fun jsSliceArr(a: Any?, start: Double = 0.0, end: Double = Double.NaN): JsArr {
    val l = arr(a); val n = l.size
    var x = jsIdx(start).let { if (it < 0) maxOf(0, n + it) else minOf(it, n) }
    var y = if (end.isNaN()) n else end.toInt().let { if (it < 0) maxOf(0, n + it) else minOf(it, n) }
    val out = JsArr(maxOf(0, y - x)); for (i in x until y) out.add(l[i]); return out
}
fun jsConcat(a: Any?, vararg v: Any?): JsArr { val out = JsArr(); out.addAll(arr(a)); for (x in v) if (x is JsArr) out.addAll(x) else out.add(x); return out }
fun jsReverse(a: Any?): Any? { arr(a).reverse(); return a }
fun jsJoin(a: Any?, sep: String): String = arr(a).joinToString(sep) { if (it == null) "" else str(it) }
fun jsIncludes(a: Any?, v: Any?): Boolean = when (a) { is JsArr -> a.any { jsEq(it, v) }; is String -> a.contains(str(v)); else -> false }
fun jsIndexOf(a: Any?, v: Any?): Double = when (a) { is JsArr -> a.indexOfFirst { jsEq(it, v) }.toDouble(); is String -> a.indexOf(str(v)).toDouble(); else -> -1.0 }
fun jsFill(a: Any?, v: Any?): Any? { val l = arr(a); for (i in l.indices) l[i] = v; return a }
fun jsArrayFromArr(src: Any?): JsArr { val out = JsArr(); when (src) { is JsArr -> out.addAll(src); is DoubleArray -> src.forEach { out.add(it) } }; return out }

inline fun jsArrayFrom(n: Double, f: (Any?, Double, Any?) -> Any?): JsArr {
    val c = jsIdx(n).coerceAtLeast(0); val out = JsArr(c)
    for (i in 0 until c) out.add(f(null, i.toDouble(), null))
    return out
}
inline fun js_forEach(a: Any?, f: (Any?, Double, Any?) -> Any?) {
    val l = a as? JsArr ?: return
    var i = 0
    while (i < l.size) { f(l[i], i.toDouble(), l); i++ }
}
inline fun js_map(a: Any?, f: (Any?, Double, Any?) -> Any?): JsArr {
    val l = a as? JsArr ?: return JsArr(0); val out = JsArr(l.size)
    for (i in 0 until l.size) out.add(f(l[i], i.toDouble(), l))
    return out
}
inline fun js_filter(a: Any?, f: (Any?, Double, Any?) -> Any?): JsArr {
    val l = a as? JsArr ?: return JsArr(0); val out = JsArr(l.size)
    for (i in 0 until l.size) if (truthy(f(l[i], i.toDouble(), l))) out.add(l[i])
    return out
}
inline fun js_some(a: Any?, f: (Any?, Double, Any?) -> Any?): Boolean { val l = a as? JsArr ?: return false; for (i in 0 until l.size) if (truthy(f(l[i], i.toDouble(), l))) return true; return false }
inline fun js_every(a: Any?, f: (Any?, Double, Any?) -> Any?): Boolean { val l = a as? JsArr ?: return true; for (i in 0 until l.size) if (!truthy(f(l[i], i.toDouble(), l))) return false; return true }
inline fun js_find(a: Any?, f: (Any?, Double, Any?) -> Any?): Any? { val l = a as? JsArr ?: return null; for (i in 0 until l.size) if (truthy(f(l[i], i.toDouble(), l))) return l[i]; return null }
inline fun js_findIndex(a: Any?, f: (Any?, Double, Any?) -> Any?): Double { val l = a as? JsArr ?: return -1.0; for (i in 0 until l.size) if (truthy(f(l[i], i.toDouble(), l))) return i.toDouble(); return -1.0 }
inline fun js_reduce(a: Any?, init: Any?, f: (Any?, Any?, Double, Any?) -> Any?): Any? { var acc = init; val l = a as? JsArr ?: return acc; for (i in 0 until l.size) acc = f(acc, l[i], i.toDouble(), l); return acc }
fun js_sort(a: Any?, f: (Any?, Any?) -> Any?): Any? {
    val l = a as? JsArr ?: return a
    // JS sort is stable (TimSort); Collections.sort is stable too. Guard against inconsistent comparators.
    try { l.sortWith { x, y -> val r = num(f(x, y)); if (r < 0) -1 else if (r > 0) 1 else 0 } } catch (_: IllegalArgumentException) {}
    return a
}
fun js_sortDefault(a: Any?): Any? { (a as? JsArr)?.sortBy { str(it) }; return a }
