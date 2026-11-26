// GENERATED from the REVERENDER_95 website source by tools/tx.js — do not edit by hand.
@file:Suppress("NAME_SHADOWING", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "UNUSED_VALUE", "VARIABLE_WITH_REDUNDANT_INITIALIZER", "LocalVariableName", "FunctionName", "UNUSED_ANONYMOUS_PARAMETER", "UNNECESSARY_NOT_NULL_ASSERTION", "SENSELESS_COMPARISON", "UNREACHABLE_CODE", "KotlinConstantConditions", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNCHECKED_CAST", "USELESS_CAST", "RedundantExplicitType", "CanBeVal", "unused", "LiftReturnOrAssignment", "CascadeIf", "ReplaceWithOperatorAssignment", "ControlFlowWithEmptyBody", "ReplaceJavaStaticMethodWithKotlinAnalog", "ReplaceManualRangeWithIndicesCalls", "IntroduceWhenSubject", "RedundantIf", "SimplifyBooleanWithConstants")
package com.reverender.r95.ui.viz.sitemini

import com.reverender.r95.ui.viz.js.*

var g_T: Any? = null

var g_m: Double = 0.0

var g_O: Double = 0.0

var g_E: Any? = null

var g_x: Double = 0.0

var g_k: Double = 0.0

fun rrMiniOscClick(): Unit {

g_m = ((g_m + 1.0) % jsLen(g_T))
g_O = (Rt.nowMs() + 1800.0)
val v13_r: Any? = Dom.getElementById("lcd-status")
if (truthy(v13_r)) {
jsSet(v13_r, "textContent", ("VIZ [" + jsStr((g_m + 1.0)) + "/" + jsStr(jsLen(g_T)) + "]: " + str(jsGet(g_T, g_m))))
}
}

fun R(v14_a: Double, v15_r: Double): Double {

val v16_g: Double = (if (truthy(jsAnd(!("object" == "undefined")) { Rt.freqDataArr })) Rt.freqDataArr[jsIdx(v14_a)] else 0.0)
if ((v16_g > 1.0)) {
return (v16_g / 255.0)
}
val v17_n: Any? = jsOr(jsOr(jsAnd(!(Ext.typeOf("isPlaying") == "undefined")) { Ext.v("isPlaying") }) { Rt.win("__retroEmuAudioActive") }) { Rt.win("__rrVideo95AudioActive") }
if (!(truthy(v17_n))) {
return 0.04
}
return (0.18 + ((0.35 * kotlin.math.abs(kotlin.math.sin(((v15_r * 4.2) + (v14_a * 0.37))))) * kotlin.math.abs(kotlin.math.cos(((v15_r * 2.7) - (v14_a * 0.19))))))
return Double.NaN
}

fun v(v18_a: Double, v19_r: Double): Double {

val v20_g: Double = (if (truthy(jsAnd(jsAnd(!("object" == "undefined")) { Rt.timeDataArr }) { Rt.timeDataArr.size.toDouble() })) Rt.timeDataArr[jsIdx(v18_a)] else 128.0)
if ((kotlin.math.abs((v20_g - 128.0)) > 1.0)) {
return ((v20_g - 128.0) / 128.0)
}
val v21_n: Any? = jsOr(jsOr(jsAnd(!(Ext.typeOf("isPlaying") == "undefined")) { Ext.v("isPlaying") }) { Rt.win("__retroEmuAudioActive") }) { Rt.win("__rrVideo95AudioActive") }
if (!(truthy(v21_n))) {
return 0.0
}
return ((kotlin.math.sin(((v19_r * 7.0) + (v18_a * 0.22))) * 0.45) + (kotlin.math.cos(((v19_r * 13.0) - (v18_a * 0.11))) * 0.25))
return Double.NaN
}

var g_W: Double = 0.0

var g_C: Double = 0.0

fun rrMiniOscFrame(v22_a: Double): Unit {

val v23_f: Any? = Dom.getElementById("mini-osc")
if (!(truthy(v23_f))) {
return
}
val v24_t: Ctx = jsGetContext(v23_f)
val v25_r: Any? = null
if (false) {
return
}
val v26_g: Double = (if (truthy(jsOr((jsOrND(Rt.win("__rrVisualQualityStage")) { 0.0 } >= 1.0)) { jsInvoke(jsGet(Dom.body, "classList"), "contains", "rr-adaptive-performance") })) 50.0 else (1000.0 / 30.0))
if (((v22_a - g_W) < v26_g)) {
return
}
g_W = v22_a
val v27_n: Double = jsMax(220.0, jsRound(jsOrND(jsOr(g_C) { jsGet(v23_f, "width") }) { 260.0 }))
val v28_o: Double = jsMax(32.0, jsRound(jsOrND(jsOr(jsGet(v23_f, "height")) { jsGet(v23_f, "clientHeight") }) { 32.0 }))
if (!jsEq(jsGet(v23_f, "width"), v27_n)) {
jsSet(v23_f, "width", v27_n)
}
val v29_u: Double = (v22_a * 0.001)
val v30_h: Any? = (if (truthy(jsAnd(!(Ext.typeOf("THEMES") == "undefined")) { jsGet(Ext.v("THEMES"), Ext.v("currentThemeKey")) })) jsGet(Ext.v("THEMES"), Ext.v("currentThemeKey")) else jsObj("primary" to "#00f7ff", "secondary" to "#ff00a0", "accent" to "#ffff00"))
v24_t.save()
v24_t.fillStyle = "#060c10"
v24_t.fillRect(0.0, 0.0, v27_n, v28_o)
v24_t.strokeStyle = "rgba(0, 247, 255, 0.09)"
v24_t.lineWidth = 1.0
v24_t.beginPath()
v24_t.moveTo(0.0, (v28_o * 0.5))
v24_t.lineTo(v27_n, (v28_o * 0.5))
v24_t.stroke()
if ((g_m == 0.0)) {
v24_t.strokeStyle = jsGet(v30_h, "secondary")
v24_t.lineWidth = 2.2
v24_t.beginPath()
val v31_l: Double = 64.0
run {
var v32_e: Double = 0.0
L0@ while ((v32_e <= v31_l)) {
val v33_s: Double = ((v32_e / v31_l) * v27_n)
val v34_i: Double = ((v28_o * 0.5) + (v((v32_e * 2.0), v29_u) * (v28_o * 0.42)))
if ((v32_e == 0.0)) {
v24_t.moveTo(v33_s, (v34_i + 1.0))
} else {
v24_t.lineTo(v33_s, (v34_i + 1.0))
}
v32_e++
}
}
v24_t.stroke()
v24_t.strokeStyle = "#d8ffff"
v24_t.lineWidth = 1.5
v24_t.beginPath()
run {
var v35_e: Double = 0.0
L1@ while ((v35_e <= v31_l)) {
val v36_s: Double = ((v35_e / v31_l) * v27_n)
val v37_i: Double = ((v28_o * 0.5) + (v((v35_e * 2.0), v29_u) * (v28_o * 0.42)))
if ((v35_e == 0.0)) {
v24_t.moveTo(v36_s, v37_i)
} else {
v24_t.lineTo(v36_s, v37_i)
}
v35_e++
}
}
v24_t.stroke()
} else {
if ((g_m == 1.0)) {
val v38_l: Double = 32.0
val v39_e: Double = (v27_n / v38_l)
val v40_s: Double = jsMax(2.0, (v39_e - 2.0))
run {
var v41_i: Double = 0.0
L2@ while ((v41_i < v38_l)) {
val v42_M: Double = jsMin(1.0, (R((v41_i * 2.0), v29_u) * 1.15))
if ((v42_M >= num(jsGet(g_E, v41_i)))) {
jsSet(g_E, v41_i, v42_M)
} else {
jsSet(g_E, v41_i, jsMax(0.0, (num(jsGet(g_E, v41_i)) - 0.012)))
}
val v43_y: Double = jsRound((v42_M * (v28_o - 3.0)))
val v44_d: Double = jsRound(((v41_i * v39_e) + 1.0))
run {
var v46_p: Double = (v28_o - 2.0)
L3@ while ((v46_p > ((v28_o - 2.0) - v43_y))) {
val v47_b: Double = ((v28_o - v46_p) / v28_o)
v24_t.fillStyle = (if ((v47_b > 0.78)) jsGet(v30_h, "accent") else (if ((v47_b > 0.45)) jsGet(v30_h, "secondary") else jsGet(v30_h, "primary")))
v24_t.fillRect(v44_d, (v46_p - 2.0), v40_s, 2.0)
v46_p -= 3.0
}
}
val v45_w: Double = jsMax(1.0, jsRound(((v28_o - 2.0) - (num(jsGet(g_E, v41_i)) * (v28_o - 3.0)))))
v24_t.fillStyle = "#ffffff"
v24_t.fillRect(v44_d, v45_w, v40_s, 1.5)
v41_i++
}
}
} else {
if ((g_m == 2.0)) {
val v48_l: Double = 48.0
js_forEach(jsArr(jsObj("scale" to 0.46, "col" to jsGet(v30_h, "secondary"), "alpha" to 0.48, "shift" to 14.0), jsObj("scale" to 0.36, "col" to jsGet(v30_h, "primary"), "alpha" to 0.78, "shift" to 0.0))) L4@{ v49_e: Any?, _, _ ->

v24_t.fillStyle = jsGet(v49_e, "col")
v24_t.globalAlpha = num(jsGet(v49_e, "alpha"))
v24_t.beginPath()
v24_t.moveTo(0.0, (v28_o * 0.5))
run {
var v50_s: Double = 0.0
L5@ while ((v50_s <= v48_l)) {
val v51_i: Double = ((v50_s / v48_l) * v27_n)
val v52_M: Double = ((kotlin.math.abs(v(((v50_s * 2.0) + num(jsGet(v49_e, "shift"))), v29_u)) * v28_o) * num(jsGet(v49_e, "scale")))
v24_t.lineTo(v51_i, ((v28_o * 0.5) - v52_M))
v50_s++
}
}
run {
var v53_s: Double = v48_l
L6@ while ((v53_s >= 0.0)) {
val v54_i: Double = ((v53_s / v48_l) * v27_n)
val v55_M: Double = ((kotlin.math.abs(v(((v53_s * 2.0) + num(jsGet(v49_e, "shift"))), v29_u)) * v28_o) * num(jsGet(v49_e, "scale")))
v24_t.lineTo(v54_i, ((v28_o * 0.5) + v55_M))
v53_s--
}
}
v24_t.closePath()
v24_t.fill()
null
}
v24_t.globalAlpha = 1.0
v24_t.fillStyle = "#ffffff"
v24_t.fillRect(0.0, ((v28_o * 0.5) - 0.5), v27_n, 1.0)
} else {
if ((g_m == 3.0)) {
var v56_l: Double = 0.0
var v57_e: Double = 0.0
run {
var v66_c: Double = 0.0
L7@ while ((v66_c < 16.0)) {
v56_l += R((v66_c * 2.0), v29_u)
v57_e += R(((v66_c * 2.0) + 1.0), v29_u)
v66_c++
}
}
val v58_s: Double = jsMin(1.0, ((v56_l / 16.0) * 1.35))
val v59_i: Double = jsMin(1.0, ((v57_e / 16.0) * 1.35))
g_x = (if ((v58_s >= g_x)) v58_s else jsMax(0.0, (g_x - 0.01)))
g_k = (if ((v59_i >= g_k)) v59_i else jsMax(0.0, (g_k - 0.01)))
val v60_M: Double = (v27_n * 0.38)
val v61_y: Double = 18.0
val v62_d: Double = ((v60_M - 14.0) / v61_y)
js_forEach(jsArr(jsObj("label" to "L", "y" to 3.0, "val" to v58_s, "pk" to g_x), jsObj("label" to "R", "y" to (v28_o - 12.0), "val" to v59_i, "pk" to g_k))) L8@{ v67_c: Any?, _, _ ->

v24_t.fillStyle = jsGet(v30_h, "primary")
v24_t.font = "bold 9px \"Courier New\", monospace"
v24_t.fillText(str(jsGet(v67_c, "label")), 3.0, (num(jsGet(v67_c, "y")) + 8.0))
val v68_U_: Double = jsRound((num(jsGet(v67_c, "val")) * v61_y))
run {
var v69_S: Double = 0.0
L9@ while ((v69_S < v61_y)) {
val v70_A: Double = (13.0 + (v69_S * v62_d))
v24_t.fillStyle = (if ((v69_S < v68_U_)) (if ((v69_S > (v61_y * 0.78))) "#ff2a55" else (if ((v69_S > (v61_y * 0.55))) jsGet(v30_h, "accent") else jsGet(v30_h, "primary"))) else "rgba(255,255,255,0.12)")
v24_t.fillRect(v70_A, num(jsGet(v67_c, "y")), jsMax(2.0, (v62_d - 1.5)), 8.0)
val v71_P: Double = ((v27_n - 13.0) - ((v69_S + 1.0) * v62_d))
v24_t.fillRect(v71_P, num(jsGet(v67_c, "y")), jsMax(2.0, (v62_d - 1.5)), 8.0)
v69_S++
}
}
null
}
val v63_w: Double = (v27_n * 0.5)
val v64_p: Double = (v28_o * 0.5)
val v65_b: Double = (v28_o * 0.42)
v24_t.strokeStyle = jsGet(v30_h, "secondary")
v24_t.strokeRect((v63_w - (v65_b * 1.3)), 2.0, (v65_b * 2.6), (v28_o - 4.0))
v24_t.strokeStyle = "#aaffff"
v24_t.lineWidth = 1.3
v24_t.beginPath()
run {
var v72_c: Double = 0.0
L10@ while ((v72_c < 36.0)) {
val v73_U_: Double = v((v72_c * 3.0), v29_u)
val v74_S: Double = v(((v72_c * 3.0) + 9.0), (v29_u + 0.2))
val v75_A: Double = (v63_w + ((v73_U_ * v65_b) * 1.1))
val v76_P: Double = (v64_p + ((v74_S * v65_b) * 0.85))
if ((v72_c == 0.0)) {
v24_t.moveTo(v75_A, v76_P)
} else {
v24_t.lineTo(v75_A, v76_P)
}
v72_c++
}
}
v24_t.stroke()
} else {
if ((g_m == 4.0)) {
val v77_l: Double = 20.0
val v78_e: Double = 4.0
val v79_s: Double = (v27_n / v77_l)
val v80_i: Double = (v28_o / v78_e)
val v81_M: Double = kotlin.math.floor((v29_u * 8.0))
run {
var v83_d: Double = 0.0
L11@ while ((v83_d < v77_l)) {
val v84_w: Double = R((v83_d * 3.0), v29_u)
run {
var v85_p: Double = 0.0
L12@ while ((v85_p < v78_e)) {
val v86_b: Double = (if (((((v83_d + v85_p) + v81_M) % 3.0) == 0.0)) v84_w else (v84_w * 0.5))
v24_t.fillStyle = (if ((v85_p < 2.0)) jsGet(v30_h, "secondary") else jsGet(v30_h, "primary"))
v24_t.globalAlpha = jsMin(0.95, (0.12 + (v86_b * 0.88)))
v24_t.fillRect(((v83_d * v79_s) + 1.0), ((v85_p * v80_i) + 1.0), (v79_s - 2.0), (v80_i - 2.0))
v85_p++
}
}
v83_d++
}
}
v24_t.globalAlpha = 1.0
val v82_y: Double = (4.0 + (R(1.0, v29_u) * 10.0))
v24_t.strokeStyle = "#ffffff"
v24_t.lineWidth = 1.6
v24_t.beginPath()
v24_t.moveTo((v27_n * 0.5), ((v28_o * 0.5) - v82_y))
v24_t.lineTo(((v27_n * 0.5) + (v82_y * 1.6)), (v28_o * 0.5))
v24_t.lineTo((v27_n * 0.5), ((v28_o * 0.5) + v82_y))
v24_t.lineTo(((v27_n * 0.5) - (v82_y * 1.6)), (v28_o * 0.5))
v24_t.closePath()
v24_t.stroke()
}
}
}
}
}
if ((v22_a < g_O)) {
val v87_l: String = (jsStr((g_m + 1.0)) + "/5 " + str(jsGet(g_T, g_m)))
v24_t.font = "bold 9px \"Courier New\", monospace"
val v88_e: Double = (num(jsGet(v24_t.measureText(v87_l), "width")) + 8.0)
v24_t.fillStyle = "rgba(0, 0, 0, 0.82)"
v24_t.fillRect(((v27_n - v88_e) - 2.0), 2.0, v88_e, 13.0)
v24_t.strokeStyle = jsGet(v30_h, "primary")
v24_t.strokeRect(((v27_n - v88_e) - 2.0), 2.0, v88_e, 13.0)
v24_t.fillStyle = "#ffffff"
v24_t.fillText(v87_l, ((v27_n - v88_e) + 2.0), 11.0)
}
v24_t.restore()
}

fun rrMiniOscMode(): Double {

return g_m
return Double.NaN
}

fun SiteMiniInit() {
g_T = jsArr("WAVEFORM SCOPE", "LED SPECTRUM + PEAKS", "MIRROR NEON RIBBON", "STEREO VU + PHASE", "CYBER MATRIX PULSE")
g_m = 3.0
g_O = 0.0
g_E = jsZeros(32.0)
g_x = 0.0
g_k = 0.0
g_W = 0.0
g_C = 0.0
}
