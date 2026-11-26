package com.reverender.r95.ui.viz.js

import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * A page element stand-in for translated site code. Properties live in the map (so translated
 * code reads/writes them as usual); natives can observe them through [onSet] and back them with [getter].
 */
open class JsEl(val id: String) : JsObj() {
    var dateMs = 0.0
    var getter: ((String) -> Any?)? = null
    var onSet: ((String, Any?) -> Unit)? = null
    val classes = LinkedHashSet<String>()
    val attrs = HashMap<String, String>()
    val style = JsObj()
    val dataset = JsObj()

    fun prop(key: String): Any? {
        getter?.invoke(key)?.let { return it }
        return when (key) {
            "style" -> style
            "dataset" -> dataset
            "classList" -> this
            "id" -> id
            else -> this[key]
        }
    }
    fun setProp(key: String, value: Any?) {
        this[key] = value
        onSet?.invoke(key, value)
    }

    // ---- canvas
    private var bmp: Bitmap? = null
    private var cv: Canvas? = null
    private var ctx2d: Ctx? = null
    fun ctx(): Ctx {
        val w = maxOf(1, num(this["width"]).toInt()); val h = maxOf(1, num(this["height"]).toInt())
        if (bmp == null || bmp!!.width != w || bmp!!.height != h) {
            bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888); cv = Canvas(bmp!!)
            ctx2d = Ctx().also { it.attach(cv!!, w.toDouble(), h.toDouble()) }
        }
        return ctx2d!!
    }
    /** The canvas bitmap for the UI to draw (null until the site code first drew). */
    val bitmap: Bitmap? get() = bmp

    open fun invoke(m: String, a: Array<out Any?>): Any? = when (m) {
        "contains" -> classes.contains(str(a.getOrNull(0)))
        "add" -> { a.forEach { classes.add(str(it)) }; onSet?.invoke("class", null); null }
        "remove" -> { a.forEach { classes.remove(str(it)) }; onSet?.invoke("class", null); null }
        "toggle" -> { val c = str(a.getOrNull(0)); val force = a.getOrNull(1); val on = if (force is Boolean) force else !classes.contains(c); if (on) classes.add(c) else classes.remove(c); onSet?.invoke("class", null); on }
        "getAttribute" -> attrs[str(a.getOrNull(0))]
        "setAttribute" -> { attrs[str(a.getOrNull(0))] = str(a.getOrNull(1)); onSet?.invoke("attr:" + str(a.getOrNull(0)), a.getOrNull(1)); null }
        "getPropertyValue" -> (style[str(a.getOrNull(0))] ?: "")
        "setProperty" -> { style[str(a.getOrNull(0))] = a.getOrNull(1); null }
        "closest", "querySelector" -> null
        "getBoundingClientRect" -> jsObj("width" to num(this["width"]), "height" to num(this["height"]), "left" to 0.0, "top" to 0.0)
        "getTime" -> dateMs
        "getHours" -> java.util.Calendar.getInstance().apply { timeInMillis = dateMs.toLong() }.get(java.util.Calendar.HOUR_OF_DAY).toDouble()
        "getMinutes" -> java.util.Calendar.getInstance().apply { timeInMillis = dateMs.toLong() }.get(java.util.Calendar.MINUTE).toDouble()
        "getSeconds" -> java.util.Calendar.getInstance().apply { timeInMillis = dateMs.toLong() }.get(java.util.Calendar.SECOND).toDouble()
        "getMilliseconds" -> (dateMs.toLong() % 1000).toDouble()
        else -> null
    }
}

/** document.* for translated code. Natives register the elements they show. */
object Dom {
    private val els = HashMap<String, JsEl>()
    @JvmField val documentElement = JsEl("html")
    @JvmField val body = JsEl("body")
    var widthDp = 390.0
    var heightDp = 844.0
    fun innerWidth() = widthDp
    fun innerHeight() = heightDp

    private var anon = 0
    /** document.createElement: an unregistered element (offscreen canvases). */
    fun createElement(tag: Any?): Any? = JsEl("_${str(tag)}_${anon++}")
    fun register(el: JsEl) { els[el.id] = el }
    fun unregister(id: String) { els.remove(id) }
    fun el(id: String): JsEl = els.getOrPut(id) { JsEl(id) }
    fun has(id: String) = els.containsKey(id)

    fun getElementById(id: Any?): Any? = els[str(id)]
    fun querySelector(sel: Any?): Any? = str(sel).let { s -> if (s.startsWith("#")) els[s.substring(1).substringBefore(' ')] else null }
    fun querySelectorAll(sel: Any?): Any? = JsArr()
    fun computedStyle(e: Any?): Any? = (e as? JsEl)?.style ?: documentElement.style
}

/** Site functions/variables that live in the native app (audio graph, transport, window manager). */
object Ext {
    val fns = HashMap<String, (Array<out Any?>) -> Any?>()
    val vars = HashMap<String, Any?>()
    val getters = HashMap<String, () -> Any?>()
    val setters = HashMap<String, (Any?) -> Unit>()
    fun call(name: String, vararg a: Any?): Any? = fns[name]?.invoke(a)
    fun fnRef(name: String): Any? = fns[name]?.let { f -> JsFn { args -> f(args) } }
    fun v(name: String): Any? = getters[name]?.invoke() ?: vars[name]
    fun setV(name: String, value: Any?) { setters[name]?.invoke(value) ?: run { vars[name] = value } }
    fun postAdd(name: String, d: Double): Double { val c = num(v(name)); setV(name, c + d); return c }
    fun typeOf(name: String): String = if (fns.containsKey(name)) "function" else jsTypeOf(v(name))
}
