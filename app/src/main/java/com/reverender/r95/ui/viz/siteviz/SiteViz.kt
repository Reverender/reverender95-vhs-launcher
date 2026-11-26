// GENERATED from the REVERENDER_95 website source by tools/tx.js — do not edit by hand.
@file:Suppress("NAME_SHADOWING", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "UNUSED_VALUE", "VARIABLE_WITH_REDUNDANT_INITIALIZER", "LocalVariableName", "FunctionName", "UNUSED_ANONYMOUS_PARAMETER", "UNNECESSARY_NOT_NULL_ASSERTION", "SENSELESS_COMPARISON", "UNREACHABLE_CODE", "KotlinConstantConditions", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNCHECKED_CAST", "USELESS_CAST", "RedundantExplicitType", "CanBeVal", "unused", "LiftReturnOrAssignment", "CascadeIf", "ReplaceWithOperatorAssignment", "ControlFlowWithEmptyBody", "ReplaceJavaStaticMethodWithKotlinAnalog", "ReplaceManualRangeWithIndicesCalls", "IntroduceWhenSubject", "RedundantIf", "SimplifyBooleanWithConstants")
package com.reverender.r95.ui.viz.siteviz

import com.reverender.r95.ui.viz.js.*

var g_THEMES: Any? = null

var g_currentMeshType: String = ""

fun setMeshType(v42_e: String): Unit {

g_currentMeshType = v42_e
}

fun generateMesh(v43_e: String, v44_a: Double, v45_o: Double, v46_r: Double): Any? {

val v47_t: Any? = jsArr()
val v48_l: Any? = jsArr()
if ((v43_e == "icosa")) {
val v49_u: Double = ((1.0 + kotlin.math.sqrt(5.0)) / 2.0)
val v50_f: Any? = jsArr(jsArr(-1.0, v49_u, 0.0), jsArr(1.0, v49_u, 0.0), jsArr(-1.0, -v49_u, 0.0), jsArr(1.0, -v49_u, 0.0), jsArr(0.0, -1.0, v49_u), jsArr(0.0, 1.0, v49_u), jsArr(0.0, -1.0, -v49_u), jsArr(0.0, 1.0, -v49_u), jsArr(v49_u, 0.0, -1.0), jsArr(v49_u, 0.0, 1.0), jsArr(-v49_u, 0.0, -1.0), jsArr(-v49_u, 0.0, 1.0))
js_forEach(v50_f) L0@{ v51_M: Any?, v52_n: Double, _ ->

val v53_s: Double = ((1.0 + (v45_o * 0.45)) + (if (((v52_n % 3.0) == 0.0)) (v46_r * 0.3) else 0.0))
jsPush(v47_t, jsArr((num(jsGet(v51_M, 0.0)) * v53_s), (num(jsGet(v51_M, 1.0)) * v53_s), (num(jsGet(v51_M, 2.0)) * v53_s)))
null
}
run {
var v54_M: Double = 0.0
L1@ while ((v54_M < jsLen(v47_t))) {
run {
var v55_n: Double = (v54_M + 1.0)
L2@ while ((v55_n < jsLen(v47_t))) {
if ((jsHypot((num(jsGet(jsGet(v50_f, v54_M), 0.0)) - num(jsGet(jsGet(v50_f, v55_n), 0.0))), (num(jsGet(jsGet(v50_f, v54_M), 1.0)) - num(jsGet(jsGet(v50_f, v55_n), 1.0))), (num(jsGet(jsGet(v50_f, v54_M), 2.0)) - num(jsGet(jsGet(v50_f, v55_n), 2.0)))) < 2.2)) {
jsPush(v48_l, jsArr(v54_M, v55_n))
}
v55_n++
}
}
v54_M++
}
}
} else {
if ((v43_e == "torus")) {
val v56_u: Double = (1.6 + (v45_o * 0.4))
val v57_f: Double = (0.65 + (v46_r * 0.3))
val v58_M: Double = 14.0
val v59_n: Double = 8.0
run {
var v60_s: Double = 0.0
L3@ while ((v60_s < v58_M)) {
val v61_i: Double = (((v60_s / v58_M) * Math.PI) * 2.0)
run {
var v62_g: Double = 0.0
L4@ while ((v62_g < v59_n)) {
val v63_m: Double = (((v62_g / v59_n) * Math.PI) * 2.0)
jsPush(v47_t, jsArr(((v56_u + (v57_f * kotlin.math.cos(v63_m))) * kotlin.math.cos(v61_i)), ((v56_u + (v57_f * kotlin.math.cos(v63_m))) * kotlin.math.sin(v61_i)), (v57_f * kotlin.math.sin(v63_m))))
val v64_d: Double = ((v60_s * v59_n) + v62_g)
jsPush(v48_l, jsArr(v64_d, ((v60_s * v59_n) + ((v62_g + 1.0) % v59_n))))
jsPush(v48_l, jsArr(v64_d, ((((v60_s + 1.0) % v58_M) * v59_n) + v62_g)))
v62_g++
}
}
v60_s++
}
}
} else {
val v65_u: Double = 22.0
run {
var v66_f: Double = 0.0
L5@ while ((v66_f < v65_u)) {
val v67_M: Double = (((v66_f / (v65_u - 1.0)) - 0.5) * 3.6)
val v68_n: Double = ((v66_f * 0.45) + (v44_a * 2.0))
val v69_s: Double = (1.1 + ((Rt.freqDataArr[jsIdx((v66_f * 4.0))] / 255.0) * 0.6))
jsPush(v47_t, jsArr((kotlin.math.cos(v68_n) * v69_s), v67_M, (kotlin.math.sin(v68_n) * v69_s)), jsArr((kotlin.math.cos((v68_n + Math.PI)) * v69_s), v67_M, (kotlin.math.sin((v68_n + Math.PI)) * v69_s)))
val v70_i: Double = (v66_f * 2.0)
jsPush(v48_l, jsArr(v70_i, (v70_i + 1.0)))
if ((v66_f > 0.0)) {
jsPush(v48_l, jsArr((v70_i - 2.0), v70_i), jsArr((v70_i - 1.0), (v70_i + 1.0)))
}
v66_f++
}
}
}
}
return jsObj("verts" to v47_t, "edges" to v48_l)
return null
}

fun rrSetText(v71_e: Any?, v72_a: String): Unit {
var v72_a = v72_a
if (!(truthy(v71_e))) {
return
}
v72_a = v72_a
val v73_o: Any? = jsGet(v71_e, "firstChild")
if (((truthy(v73_o) && jsEq(v73_o, jsGet(v71_e, "lastChild"))) && jsEq(jsGet(v73_o, "nodeType"), 3.0))) {
if (!jsEq(jsGet(v73_o, "data"), v72_a)) {
jsSet(v73_o, "data", v72_a)
}
} else {
if (!jsEq(jsGet(v71_e, "textContent"), v72_a)) {
jsSet(v71_e, "textContent", v72_a)
}
}
}

fun renderHypercubeVisualizer(v74_e: Double, v75_a: Double, v76_o: Double, v77_r: Any?): Unit {

val v78_t: Any? = Dom.getElementById("canvas-hypercube")
if (((!(truthy(v78_t)) || jsEq(jsGet(v78_t, "width"), 0.0)) || !(truthy(Ext.call("canvasWindowIsActive", v78_t))))) {
return
}
val v79_l: Ctx = jsGetContext(v78_t)
val v80_u: Any? = jsGet(v78_t, "width")
val v81_f: Any? = jsGet(v78_t, "height")
v79_l.fillStyle = "rgba(4, 2, 10, 0.34)"
v79_l.fillRect(0.0, 0.0, num(v80_u), num(v81_f))
val p6: Any? = generateMesh(g_currentMeshType, v74_e, v75_a, v76_o)
var v82_M: Any? = jsGet(p6, "verts")
var v83_n: Any? = jsGet(p6, "edges")
val v84_s: Double = ((v74_e * 0.9) + (v75_a * 0.6))
val v85_i: Double = (v74_e * 1.2)
val v86_g: Any? = js_map(v82_M) L7@{ p8: Any?, _, _ ->
var v87_m: Any? = jsGet(p8, 0.0)
var v88_d: Any? = jsGet(p8, 1.0)
var v89_y: Any? = jsGet(p8, 2.0)
val v90_c: Double = ((num(v87_m) * kotlin.math.cos(v85_i)) - (num(v89_y) * kotlin.math.sin(v85_i)))
val v91_S: Double = ((num(v87_m) * kotlin.math.sin(v85_i)) + (num(v89_y) * kotlin.math.cos(v85_i)))
val v92_v: Double = ((num(v88_d) * kotlin.math.cos(v84_s)) - (v91_S * kotlin.math.sin(v84_s)))
val v93_T: Double = ((num(v88_d) * kotlin.math.sin(v84_s)) + (v91_S * kotlin.math.cos(v84_s)))
val v94_b: Double = ((jsMin(num(v80_u), num(v81_f)) * 0.72) / jsMax(0.5, (4.5 + v93_T)))
return@L7 jsObj("px" to ((num(v80_u) / 2.0) + (v90_c * v94_b)), "py" to ((num(v81_f) / 2.0) + (v92_v * v94_b)), "depth" to v93_T)
null
}
js_forEach(v83_n) L9@{ p10: Any?, v97_y: Double, _ ->
var v95_m: Any? = jsGet(p10, 0.0)
var v96_d: Any? = jsGet(p10, 1.0)
val v98_c: Any? = jsGet(v86_g, v95_m)
val v99_S: Any? = jsGet(v86_g, v96_d)
if ((!(truthy(v98_c)) || !(truthy(v99_S)))) {
return@L9 null
}
v79_l.strokeStyle = (if (((v97_y % 2.0) == 0.0)) jsGet(v77_r, "primary") else jsGet(v77_r, "secondary"))
v79_l.lineWidth = jsMax(1.0, (2.5 - ((num(jsGet(v98_c, "depth")) + num(jsGet(v99_S, "depth"))) * 0.25)))
v79_l.beginPath()
v79_l.moveTo(num(jsGet(v98_c, "px")), num(jsGet(v98_c, "py")))
v79_l.lineTo(num(jsGet(v99_S, "px")), num(jsGet(v99_S, "py")))
v79_l.stroke()
null
}
js_forEach(v86_g) L11@{ v100_m: Any?, _, _ ->

v79_l.fillStyle = jsGet(v77_r, "accent")
v79_l.beginPath()
v79_l.arc(num(jsGet(v100_m, "px")), num(jsGet(v100_m, "py")), jsMax(1.5, (4.0 - (num(jsGet(v100_m, "depth")) * 0.5))), 0.0, (Math.PI * 2.0))
v79_l.fill()
null
}
}

var g_waterfallHistory: Any? = null

var g_MAX_WATERFALL_ROWS: Double = 0.0

fun renderWaterfallVisualizer(v101_e: Double, v102_a: Any?): Unit {

val v103_o: Any? = Dom.getElementById("canvas-waterfall")
if (((!(truthy(v103_o)) || jsEq(jsGet(v103_o, "width"), 0.0)) || !(truthy(Ext.call("canvasWindowIsActive", v103_o))))) {
return
}
val v104_r: Ctx = jsGetContext(v103_o)
val v105_t: Any? = jsGet(v103_o, "width")
val v106_l: Any? = jsGet(v103_o, "height")
v104_r.fillStyle = "#04010a"
v104_r.fillRect(0.0, 0.0, num(v105_t), num(v106_l))
val v107_u: Double = 36.0
val v108_f: Any? = jsArr()
run {
var v109_M: Double = 0.0
L12@ while ((v109_M < v107_u)) {
jsPush(v108_f, jsOrN(Rt.freqDataArr[jsIdx((v109_M * 2.0))]) { 0.0 })
v109_M++
}
}
jsUnshift(g_waterfallHistory, v108_f)
if ((jsLen(g_waterfallHistory) > g_MAX_WATERFALL_ROWS)) {
jsPop(g_waterfallHistory)
}
run {
var v110_M: Double = (jsLen(g_waterfallHistory) - 1.0)
L13@ while ((v110_M >= 0.0)) {
val v111_n: Any? = jsGet(g_waterfallHistory, v110_M)
val v112_s: Double = (v110_M / g_MAX_WATERFALL_ROWS)
val v113_i: Double = ((num(v106_l) * 0.86) - (v112_s * (num(v106_l) * 0.62)))
val v114_g: Double = (num(v105_t) * (0.88 - (v112_s * 0.35)))
val v115_m: Double = (((num(v105_t) - v114_g) / 2.0) + (v112_s * 18.0))
v104_r.beginPath()
v104_r.moveTo(v115_m, v113_i)
run {
var v116_d: Double = 0.0
L14@ while ((v116_d < v107_u)) {
v104_r.lineTo((v115_m + ((v116_d / (v107_u - 1.0)) * v114_g)), (v113_i - (((num(jsGet(v111_n, v116_d)) / 255.0) * (num(v106_l) * 0.34)) * (1.0 - (v112_s * 0.45)))))
v116_d++
}
}
v104_r.lineTo((v115_m + v114_g), v113_i)
v104_r.closePath()
v104_r.fillStyle = "rgba(8, 2, 18, 0.9)"
v104_r.fill()
v104_r.strokeStyle = (if (((v110_M % 2.0) == 0.0)) jsGet(v102_a, "primary") else jsGet(v102_a, "secondary"))
v104_r.globalAlpha = jsMax(0.2, (1.0 - (v112_s * 0.7)))
v104_r.lineWidth = (if ((v110_M == 0.0)) 2.2 else 1.2)
v104_r.stroke()
v104_r.globalAlpha = 1.0
v110_M--
}
}
rrSetText(Dom.getElementById("bass-meter"), ("BASS: " + jsStr(jsRound((v101_e * 100.0))) + "%"))
}

var g_wormTravel: Double = 0.0

fun renderTunnelVisualizer(v117_e: Double, v118_a: Double, v119_o: Double, v120_r: Double, v121_t: Any?): Unit {

val v122_l: Any? = Dom.getElementById("canvas-tunnel")
if (((!(truthy(v122_l)) || jsEq(jsGet(v122_l, "width"), 0.0)) || !(truthy(Ext.call("canvasWindowIsActive", v122_l))))) {
return
}
val v123_u: Ctx = jsGetContext(v122_l)
val v124_f: Any? = jsGet(v122_l, "width")
val v125_M: Any? = jsGet(v122_l, "height")
v123_u.fillStyle = "#03010a"
v123_u.fillRect(0.0, 0.0, num(v124_f), num(v125_M))
val v126_n: Double = jsParseFloat(jsOrND(jsGet(Dom.getElementById("tunnel-speed"), "value")) { 2.2 })
g_wormTravel += ((v118_a * v126_n) * (1.1 + (v119_o * 1.6)))
val v127_s: Double = 20.0
val v128_i: Double = 24.0
val v129_g: Double = (Math.hypot(num(v124_f), num(v125_M)) * 0.6)
val v130_m: Any? = jsArr()
run {
var v131_d: Double = 0.0
L15@ while ((v131_d < v127_s)) {
L15c@ do {
val v132_y: Double = (((v131_d - (g_wormTravel % 1.0)) + v127_s) % v127_s)
val v133_c: Double = (v132_y / v127_s)
if ((v133_c <= 0.03)) {
break@L15c
}
val v134_S: Double = Math.pow(v133_c, 2.0)
val v135_v: Double = (v134_S * v129_g)
val v136_T: Double = (kotlin.math.floor(g_wormTravel) + (v127_s - v131_d))
val v137_b: Double = ((v136_T * 0.25) - (g_wormTravel * 0.25))
val v138_h: Double = ((num(v124_f) * 0.5) + ((kotlin.math.sin((v137_b + (v117_e * 1.1))) * (num(v124_f) * 0.22)) * (1.0 - v134_S)))
val v139_p: Double = ((num(v125_M) * 0.5) + ((kotlin.math.cos(((v137_b * 0.85) + (v117_e * 0.9))) * (num(v125_M) * 0.22)) * (1.0 - v134_S)))
val v140_w: Double = ((v137_b * 0.45) + (v117_e * 0.6))
val v141_A: Double = jsMax(0.08, (jsMin(1.0, (v133_c * 4.2)) * jsMin(1.0, ((1.0 - v133_c) * 5.5))))
val v142_C: Any? = jsArr()
run {
var v143_E: Double = 0.0
L16@ while ((v143_E < v128_i)) {
val v144_k: Double = ((((v143_E / v128_i) * Math.PI) * 2.0) + v140_w)
val v145_R: Double = (((Rt.freqDataArr[jsIdx(((v143_E * 3.0) % 64.0))] / 255.0) * 18.0) * v134_S)
val v146_F: Double = jsMax(2.0, ((v135_v + (kotlin.math.sin(((v144_k * 4.0) + (v117_e * 3.2))) * ((v119_o * 14.0) * v134_S))) + v145_R))
jsPush(v142_C, jsObj("x" to (v138_h + (kotlin.math.cos(v144_k) * v146_F)), "y" to (v139_p + (kotlin.math.sin(v144_k) * v146_F))))
v143_E++
}
}
jsPush(v130_m, jsObj("norm" to v133_c, "pts" to v142_C, "alpha" to v141_A, "absZ" to v136_T))
} while (false)
v131_d++
}
}
js_sort(v130_m) L17@{ v147_d: Any?, v148_y: Any? ->

(num(jsGet(v147_d, "norm")) - num(jsGet(v148_y, "norm")))
}
run {
var v149_d: Double = 0.0
L18@ while ((v149_d < jsLen(v130_m))) {
val v150_y: Any? = jsGet(v130_m, v149_d)
if ((v149_d > 0.0)) {
val v151_c: Any? = jsGet(v130_m, (v149_d - 1.0))
v123_u.strokeStyle = jsGet(v121_t, "secondary")
v123_u.globalAlpha = (num(jsGet(v150_y, "alpha")) * 0.55)
v123_u.lineWidth = (0.8 + (num(jsGet(v150_y, "norm")) * 1.2))
v123_u.beginPath()
run {
var v152_S: Double = 0.0
L19@ while ((v152_S < v128_i)) {
v123_u.moveTo(num(jsGet(jsGet(jsGet(v151_c, "pts"), v152_S), "x")), num(jsGet(jsGet(jsGet(v151_c, "pts"), v152_S), "y")))
v123_u.lineTo(num(jsGet(jsGet(jsGet(v150_y, "pts"), v152_S), "x")), num(jsGet(jsGet(jsGet(v150_y, "pts"), v152_S), "y")))
v152_S += 2.0
}
}
v123_u.stroke()
}
v123_u.strokeStyle = (if (((num(jsGet(v150_y, "absZ")) % 2.0) == 0.0)) jsGet(v121_t, "primary") else jsGet(v121_t, "accent"))
v123_u.globalAlpha = num(jsGet(v150_y, "alpha"))
v123_u.lineWidth = (1.2 + (num(jsGet(v150_y, "norm")) * 2.4))
v123_u.beginPath()
js_forEach(jsGet(v150_y, "pts")) L20@{ v153_c: Any?, v154_S: Double, _ ->

(if ((v154_S == 0.0)) v123_u.moveTo(num(jsGet(v153_c, "x")), num(jsGet(v153_c, "y"))) else v123_u.lineTo(num(jsGet(v153_c, "x")), num(jsGet(v153_c, "y"))))
}
v123_u.closePath()
v123_u.stroke()
v149_d++
}
}
v123_u.globalAlpha = 1.0
}

fun renderPlasmaVisualizer(v155_e: Double, v156_a: Double, v157_o: Double, v158_r: Any?): Unit {

val v159_t: Any? = Dom.getElementById("canvas-plasma")
if (((!(truthy(v159_t)) || jsEq(jsGet(v159_t, "width"), 0.0)) || !(truthy(Ext.call("canvasWindowIsActive", v159_t))))) {
return
}
val v160_l: Ctx = jsGetContext(v159_t)
val v161_u: Any? = jsGet(v159_t, "width")
val v162_f: Any? = jsGet(v159_t, "height")
val v163_M: Double = (num(v161_u) / 2.0)
val v164_n: Double = (num(v162_f) / 2.0)
v160_l.fillStyle = "rgba(3, 1, 10, 0.38)"
v160_l.fillRect(0.0, 0.0, num(v161_u), num(v162_f))
val v165_s: Double = ((jsMin(num(v161_u), num(v162_f)) * 0.22) * (1.0 + (v156_a * 0.3)))
val v166_i: Grad = v160_l.createRadialGradient(v163_M, v164_n, (v165_s * 0.1), v163_M, v164_n, (v165_s * 1.9))
v166_i.addColorStop(0.0, jsGet(v158_r, "accent"))
v166_i.addColorStop(0.35, ("rgba(" + str(jsGet(v158_r, "altWaveRgb")) + ", 0.45)"))
v166_i.addColorStop(1.0, "transparent")
v160_l.fillStyle = v166_i
v160_l.beginPath()
v160_l.arc(v163_M, v164_n, (v165_s * 1.9), 0.0, (Math.PI * 2.0))
v160_l.fill()
val v167_g: Double = (v155_e * 1.1) + com.reverender.r95.os.GyroWarp.plasmaPitch() // native: tilt the gyro core
val v168_m: Double = (v155_e * 0.85) + com.reverender.r95.os.GyroWarp.plasmaRoll()
fun v169_d(v171_c: Double, v172_S: Double, v173_v: Double): Any? {

val v174_T: Double = ((v171_c * kotlin.math.cos(v168_m)) - (v173_v * kotlin.math.sin(v168_m)))
val v175_b: Double = ((v171_c * kotlin.math.sin(v168_m)) + (v173_v * kotlin.math.cos(v168_m)))
val v176_h: Double = ((v172_S * kotlin.math.cos(v167_g)) - (v175_b * kotlin.math.sin(v167_g)))
val v177_p: Double = ((v172_S * kotlin.math.sin(v167_g)) + (v175_b * kotlin.math.cos(v167_g)))
val v178_w: Double = (3.2 / (3.2 + (v177_p * 0.45)))
return jsObj("x" to (v163_M + ((v174_T * v165_s) * v178_w)), "y" to (v164_n + ((v176_h * v165_s) * v178_w)), "z" to v177_p)
return null
}
v160_l.strokeStyle = jsGet(v158_r, "primary")
v160_l.lineWidth = 1.2
run {
var v179_c: Double = -2.0
L21@ while ((v179_c <= 2.0)) {
val v180_S: Double = ((v179_c / 3.0) * (Math.PI * 0.5))
val v181_v: Double = kotlin.math.cos(v180_S)
val v182_T: Double = kotlin.math.sin(v180_S)
v160_l.beginPath()
run {
var v183_b: Double = 0.0
L22@ while ((v183_b <= 24.0)) {
val v184_h: Double = (((v183_b / 24.0) * Math.PI) * 2.0)
val v185_p: Any? = v169_d((kotlin.math.cos(v184_h) * v181_v), v182_T, (kotlin.math.sin(v184_h) * v181_v))
if ((v183_b == 0.0)) {
v160_l.moveTo(num(jsGet(v185_p, "x")), num(jsGet(v185_p, "y")))
} else {
v160_l.lineTo(num(jsGet(v185_p, "x")), num(jsGet(v185_p, "y")))
}
v183_b++
}
}
v160_l.stroke()
v179_c++
}
}
val v170_y: Double = 40.0
run {
var v186_c: Double = 0.0
L23@ while ((v186_c < v170_y)) {
val v187_S: Double = ((((v186_c / v170_y) * Math.PI) * 2.0) + (v155_e * 0.4))
val v188_v: Double = (Rt.freqDataArr[jsIdx((v186_c * 2.0))] / 255.0)
val v189_T: Double = 1.15
val v190_b: Double = (1.15 + (v188_v * 0.95))
val v191_h: Any? = v169_d((kotlin.math.cos(v187_S) * v189_T), (kotlin.math.sin((v187_S * 2.0)) * 0.25), (kotlin.math.sin(v187_S) * v189_T))
val v192_p: Any? = v169_d((kotlin.math.cos(v187_S) * v190_b), (kotlin.math.sin((v187_S * 2.0)) * 0.25), (kotlin.math.sin(v187_S) * v190_b))
v160_l.strokeStyle = (if (((v186_c % 2.0) == 0.0)) jsGet(v158_r, "primary") else jsGet(v158_r, "secondary"))
v160_l.lineWidth = (if ((num(jsGet(v191_h, "z")) < 0.0)) 2.4 else 1.2)
v160_l.beginPath()
v160_l.moveTo(num(jsGet(v191_h, "x")), num(jsGet(v191_h, "y")))
v160_l.lineTo(num(jsGet(v192_p, "x")), num(jsGet(v192_p, "y")))
v160_l.stroke()
v186_c++
}
}
rrSetText(Dom.getElementById("high-meter"), ("TREBLE: " + jsStr(jsRound((v157_o * 100.0))) + "%"))
}

var g_lissPeakCaps: Any? = null

fun renderLissajousVisualizer(v193_e: Double, v194_a: Double, v195_o: Double, v196_r: Any?): Unit {

val v197_t: Any? = Dom.getElementById("canvas-lissajous")
if (((!(truthy(v197_t)) || jsEq(jsGet(v197_t, "width"), 0.0)) || !(truthy(Ext.call("canvasWindowIsActive", v197_t))))) {
return
}
val v198_l: Ctx = jsGetContext(v197_t)
val v199_u: Any? = jsGet(v197_t, "width")
val v200_f: Any? = jsGet(v197_t, "height")
v198_l.fillStyle = "rgba(4, 2, 12, 0.42)"
v198_l.fillRect(0.0, 0.0, num(v199_u), num(v200_f))
val v201_M: Double = (num(v199_u) * 0.55)
val v202_n: Double = 20.0
val v203_s: Double = ((v201_M - 24.0) / v202_n)
val v204_i: Double = (v203_s * 0.62)
val v205_g: Double = 6.0
val v206_m: Double = -5.0
run {
var v211_v: Double = 0.0
L24@ while ((v211_v < v202_n)) {
val v212_T: Double = (Rt.freqDataArr[jsIdx((v211_v * 3.0))] / 255.0)
val v213_b: Double = jsMax(4.0, (v212_T * (num(v200_f) - 32.0)))
if ((v213_b > num(jsGet(g_lissPeakCaps, v211_v)))) {
jsSet(g_lissPeakCaps, v211_v, v213_b)
} else {
jsSet(g_lissPeakCaps, v211_v, jsMax(4.0, (num(jsGet(g_lissPeakCaps, v211_v)) - 1.8)))
}
val v214_h: Double = (12.0 + (v211_v * v203_s))
val v215_p: Double = ((num(v200_f) - 10.0) - v213_b)
v198_l.fillStyle = (if (((v211_v % 2.0) == 0.0)) jsGet(v196_r, "primary") else jsGet(v196_r, "secondary"))
v198_l.fillRect(v214_h, v215_p, v204_i, v213_b)
v198_l.fillStyle = jsGet(v196_r, "accent")
v198_l.beginPath()
v198_l.moveTo(v214_h, v215_p)
v198_l.lineTo((v214_h + v205_g), (v215_p + v206_m))
v198_l.lineTo(((v214_h + v204_i) + v205_g), (v215_p + v206_m))
v198_l.lineTo((v214_h + v204_i), v215_p)
v198_l.closePath()
v198_l.fill()
v198_l.fillStyle = jsGet(v196_r, "sunSlice")
v198_l.strokeStyle = jsGet(v196_r, "primary")
v198_l.lineWidth = 0.7
v198_l.beginPath()
v198_l.moveTo((v214_h + v204_i), v215_p)
v198_l.lineTo(((v214_h + v204_i) + v205_g), (v215_p + v206_m))
v198_l.lineTo(((v214_h + v204_i) + v205_g), ((num(v200_f) - 10.0) + v206_m))
v198_l.lineTo((v214_h + v204_i), (num(v200_f) - 10.0))
v198_l.closePath()
v198_l.fill()
v198_l.stroke()
val v216_w: Double = ((num(v200_f) - 14.0) - num(jsGet(g_lissPeakCaps, v211_v)))
v198_l.fillStyle = "#ffffff"
v198_l.fillRect(v214_h, v216_w, v204_i, 3.0)
v211_v++
}
}
val v207_d: Double = (num(v199_u) * 0.78)
val v208_y: Double = (num(v200_f) * 0.5)
val v209_c: Double = jsMin((num(v199_u) * 0.19), (num(v200_f) * 0.43))
v198_l.strokeStyle = "rgba(255,255,255,0.16)"
v198_l.lineWidth = 1.0
v198_l.beginPath()
v198_l.arc(v207_d, v208_y, v209_c, 0.0, (Math.PI * 2.0))
v198_l.stroke()
v198_l.beginPath()
v198_l.ellipse(v207_d, v208_y, v209_c, (v209_c * 0.35), (v193_e * 0.4), 0.0, (Math.PI * 2.0))
v198_l.stroke()
v198_l.beginPath()
v198_l.ellipse(v207_d, v208_y, (v209_c * 0.35), v209_c, (v193_e * 0.4), 0.0, (Math.PI * 2.0))
v198_l.stroke()
val v210_S: Double = 128.0
run {
var v217_v: Double = 0.0
L25@ while ((v217_v < (v210_S - 1.0))) {
val v218_T: Double = ((Rt.timeDataArr[jsIdx(v217_v)] - 128.0) / 128.0)
val v219_b: Double = ((Rt.timeDataArr[jsIdx(((v217_v + 32.0) % v210_S))] - 128.0) / 128.0)
val v220_h: Double = ((Rt.timeDataArr[jsIdx(((v217_v + 64.0) % v210_S))] - 128.0) / 128.0)
val v221_p: Double = (2.2 / (2.2 + (v220_h * 0.6)))
val v222_w: Double = (v207_d + (((v218_T * v209_c) * (1.1 + (v194_a * 0.45))) * v221_p))
val v223_A: Double = (v208_y + (((v219_b * v209_c) * (1.1 + (v195_o * 0.45))) * v221_p))
val v224_C: Double = ((Rt.timeDataArr[jsIdx((v217_v + 1.0))] - 128.0) / 128.0)
val v225_E: Double = ((Rt.timeDataArr[jsIdx(((v217_v + 33.0) % v210_S))] - 128.0) / 128.0)
val v226_k: Double = (v207_d + (((v224_C * v209_c) * (1.1 + (v194_a * 0.45))) * v221_p))
val v227_R: Double = (v208_y + (((v225_E * v209_c) * (1.1 + (v195_o * 0.45))) * v221_p))
v198_l.strokeStyle = (if (((v217_v % 2.0) == 0.0)) jsGet(v196_r, "primary") else jsGet(v196_r, "accent"))
v198_l.lineWidth = jsMax(1.0, (2.2 * v221_p))
v198_l.beginPath()
v198_l.moveTo(v222_w, v223_A)
v198_l.lineTo(v226_k, v227_R)
v198_l.stroke()
v217_v++
}
}
}

fun renderCyberCanyonVisualizer(v228_e: Double, v229_a: Double, v230_o: Double, v231_r: Any?): Unit {

val v232_t: Any? = Dom.getElementById("canvas-cybergrid")
if (((!(truthy(v232_t)) || jsEq(jsGet(v232_t, "width"), 0.0)) || !(truthy(Ext.call("canvasWindowIsActive", v232_t))))) {
return
}
val v233_l: Ctx = jsGetContext(v232_t)
val v234_u: Any? = jsGet(v232_t, "width")
val v235_f: Any? = jsGet(v232_t, "height")
val v236_M: Double = (num(v235_f) * 0.38)
v233_l.fillStyle = "#05010f"
v233_l.fillRect(0.0, 0.0, num(v234_u), num(v235_f))
val v237_n: Double = ((jsMin(num(v234_u), num(v235_f)) * 0.2) * (1.0 + (v230_o * 0.15)))
val v238_s: Grad = v233_l.createLinearGradient(0.0, (v236_M - v237_n), 0.0, v236_M)
v238_s.addColorStop(0.0, jsGet(v231_r, "accent"))
v238_s.addColorStop(1.0, jsGet(v231_r, "secondary"))
v233_l.fillStyle = v238_s
v233_l.beginPath()
v233_l.arc((num(v234_u) / 2.0), v236_M, v237_n, Math.PI, (Math.PI * 2.0))
v233_l.fill()
val v239_i: Double = 15.0
val v240_g: Double = 16.0
val v241_m: Double = (v229_a % 1.0)
val v242_d: Any? = jsArr()
run {
var v243_y: Double = 0.0
L26@ while ((v243_y < v239_i)) {
val v244_c: Double = ((v243_y + v241_m) / v239_i)
val v245_S: Double = Math.pow(v244_c, 1.85)
val v246_v: Double = (v236_M + (v245_S * (num(v235_f) - v236_M)))
val v247_T: Any? = jsArr()
run {
var v248_b: Double = 0.0
L27@ while ((v248_b <= v240_g)) {
val v249_h: Double = ((v248_b / v240_g) - 0.5)
val v250_p: Double = ((num(v234_u) / 2.0) + ((v249_h * num(v234_u)) * (0.26 + (v245_S * 1.25))))
val v251_w: Double = ((Math.pow(kotlin.math.abs((v249_h * 2.0)), 2.1) * (num(v235_f) * 0.42)) * v245_S)
val v252_A: Double = (((Rt.freqDataArr[jsIdx((kotlin.math.abs((v248_b - (v240_g / 2.0))) * 4.0))] / 255.0) * (num(v235_f) * 0.2)) * v245_S)
val v253_C: Double = ((v246_v - v251_w) - (v252_A * (0.5 + (0.5 * kotlin.math.sin((((v228_e * 2.2) + (v248_b * 0.6)) - (v244_c * 3.5)))))))
jsPush(v247_T, jsObj("x" to v250_p, "y" to v253_C, "persp" to v245_S))
v248_b++
}
}
jsPush(v242_d, v247_T)
v243_y++
}
}
run {
var v254_y: Double = 0.0
L28@ while ((v254_y < (v239_i - 1.0))) {
val v255_c: Any? = jsGet(v242_d, v254_y)
val v256_S: Any? = jsGet(v242_d, (v254_y + 1.0))
run {
var v257_v: Double = 0.0
L29@ while ((v257_v < v240_g)) {
v233_l.fillStyle = (if ((((v254_y + v257_v) % 2.0) == 0.0)) "rgba(9, 2, 22, 0.92)" else "rgba(18, 5, 38, 0.92)")
v233_l.strokeStyle = (if (((v257_v == (v240_g / 2.0)) || (v257_v == ((v240_g / 2.0) - 1.0)))) jsGet(v231_r, "accent") else (if (((v254_y % 2.0) == 0.0)) jsGet(v231_r, "primary") else jsGet(v231_r, "secondary")))
v233_l.lineWidth = (0.8 + (num(jsGet(jsGet(v256_S, v257_v), "persp")) * 1.3))
v233_l.beginPath()
v233_l.moveTo(num(jsGet(jsGet(v255_c, v257_v), "x")), num(jsGet(jsGet(v255_c, v257_v), "y")))
v233_l.lineTo(num(jsGet(jsGet(v255_c, (v257_v + 1.0)), "x")), num(jsGet(jsGet(v255_c, (v257_v + 1.0)), "y")))
v233_l.lineTo(num(jsGet(jsGet(v256_S, (v257_v + 1.0)), "x")), num(jsGet(jsGet(v256_S, (v257_v + 1.0)), "y")))
v233_l.lineTo(num(jsGet(jsGet(v256_S, v257_v), "x")), num(jsGet(jsGet(v256_S, v257_v), "y")))
v233_l.closePath()
v233_l.fill()
v233_l.stroke()
v257_v++
}
}
v254_y++
}
}
rrSetText(Dom.getElementById("canyon-alt"), ("ALT: " + jsStr(jsRound((1200.0 + (v230_o * 850.0)))) + "M"))
}

var g_MATRIX_STREAMS: Any? = null

var g_GLYPHS: String = ""

fun renderMatrixVisualizer(v260_e: Double, v261_a: Double, v262_o: Double, v263_r: Any?): Unit {

val v264_t: Any? = Dom.getElementById("canvas-matrix")
if (((!(truthy(v264_t)) || jsEq(jsGet(v264_t, "width"), 0.0)) || !(truthy(Ext.call("canvasWindowIsActive", v264_t))))) {
return
}
val v265_l: Ctx = jsGetContext(v264_t)
val v266_u: Any? = jsGet(v264_t, "width")
val v267_f: Any? = jsGet(v264_t, "height")
v265_l.fillStyle = "rgba(2, 0, 8, 0.28)"
v265_l.fillRect(0.0, 0.0, num(v266_u), num(v267_f))
val v268_M: Double = 18.0
val v269_n: Double = (num(v266_u) / v268_M)
run {
var v270_s: Double = 0.0
L31@ while ((v270_s < v268_M)) {
val v271_i: Double = ((Rt.freqDataArr[jsIdx((v270_s * 3.0))] / 255.0) * (num(v267_f) * 0.36))
v265_l.fillStyle = "rgba(12, 4, 28, 0.65)"
v265_l.strokeStyle = jsGet(v263_r, "secondary")
v265_l.lineWidth = 1.0
v265_l.fillRect((v270_s * v269_n), (num(v267_f) - v271_i), (v269_n - 2.0), v271_i)
v265_l.strokeRect((v270_s * v269_n), (num(v267_f) - v271_i), (v269_n - 2.0), v271_i)
v270_s++
}
}
js_forEach(jsArr(0.0, 1.0, 2.0)) L32@{ v272_s: Any?, _, _ ->

val v273_i: Double = (if (jsEq(v272_s, 0.0)) 8.0 else (if (jsEq(v272_s, 1.0)) 11.0 else 14.0))
val v274_g: Double = (if (jsEq(v272_s, 0.0)) 0.38 else (if (jsEq(v272_s, 1.0)) 0.72 else 1.0))
v265_l.font = ("bold " + jsStr(v273_i) + "px \"Courier New\", monospace")
v265_l.globalAlpha = v274_g
js_forEach(g_MATRIX_STREAMS) L33@{ v275_m: Any?, v276_d: Double, _ ->

if (!jsEq(jsGet(v275_m, "zLayer"), v272_s)) {
return@L33 null
}
val v277_y: Double = (Rt.freqDataArr[jsIdx(((v276_d * 2.0) % 64.0))] / 255.0)
jsAddAssign(v275_m, "y", (num(jsGet(v275_m, "speed")) * ((1.0 + (v261_a * 2.0)) + (v277_y * 1.4))))
if ((num(jsGet(v275_m, "y")) > (num(v267_f) + 50.0))) {
jsSet(v275_m, "y", -20.0)
}
val v278_c: Double = (num(jsGet(v275_m, "xFrac")) * num(v266_u))
val v279_S: Double = (if (jsEq(v272_s, 2.0)) 6.0 else 4.0)
run {
var v280_v: Double = 0.0
L34@ while ((v280_v < v279_S)) {
val v281_T: String = jsCharAt(g_GLYPHS, (kotlin.math.floor((((v260_e * 12.0) + num(jsGet(v275_m, "seed"))) + (v280_v * 5.0))) % g_GLYPHS.length.toDouble()))
v265_l.fillStyle = (if ((v280_v == 0.0)) "#ffffff" else (if ((v280_v == 1.0)) jsGet(v263_r, "accent") else (if (((v276_d % 2.0) == 0.0)) jsGet(v263_r, "primary") else jsGet(v263_r, "secondary"))))
v265_l.fillText(v281_T, v278_c, (num(jsGet(v275_m, "y")) - (v280_v * v273_i)))
v280_v++
}
}
null
}
null
}
v265_l.globalAlpha = 1.0
}

var g_ribbonHistory: Any? = null

var g_MAX_RIBBON_HIST: Double = 0.0

fun renderRibbonVisualizer(v282_e: Double, v283_a: Double, v284_o: Double, v285_r: Any?): Unit {

val v286_t: Any? = Dom.getElementById("canvas-ribbon")
if (((!(truthy(v286_t)) || jsEq(jsGet(v286_t, "width"), 0.0)) || !(truthy(Ext.call("canvasWindowIsActive", v286_t))))) {
return
}
val v287_l: Ctx = jsGetContext(v286_t)
val v288_u: Any? = jsGet(v286_t, "width")
val v289_f: Any? = jsGet(v286_t, "height")
v287_l.fillStyle = "rgba(3, 1, 10, 0.36)"
v287_l.fillRect(0.0, 0.0, num(v288_u), num(v289_f))
val v290_M: Double = 32.0
val v291_n: Any? = jsArr()
run {
var v292_s: Double = 0.0
L35@ while ((v292_s < v290_M)) {
val v293_i: Double = (v292_s / (v290_M - 1.0))
val v294_g: Double = kotlin.math.sin((v293_i * Math.PI))
val v295_m: Double = ((((Rt.timeDataArr[jsIdx((v292_s * 3.0))] - 128.0) / 128.0) * (num(v289_f) * 0.26)) * v294_g)
val v296_d: Double = (((kotlin.math.sin(((v293_i * 8.0) + (v282_e * 4.0))) * (num(v289_f) * 0.22)) * v294_g) * (0.3 + (v284_o * 1.1)))
jsPush(v291_n, (v295_m + v296_d))
v292_s++
}
}
jsUnshift(g_ribbonHistory, v291_n)
if ((jsLen(g_ribbonHistory) > g_MAX_RIBBON_HIST)) {
jsPop(g_ribbonHistory)
}
run {
var v297_s: Double = (jsLen(g_ribbonHistory) - 1.0)
L36@ while ((v297_s >= 0.0)) {
val v298_i: Double = (v297_s / g_MAX_RIBBON_HIST)
val v299_g: Double = (1.0 - (v298_i * 0.48))
val v300_m: Double = ((num(v288_u) * 0.5) + (v298_i * 24.0))
val v301_d: Double = ((num(v289_f) * 0.56) - (v298_i * (num(v289_f) * 0.28)))
val v302_y: Double = ((num(v288_u) * 0.86) * v299_g)
val v303_c: Any? = jsGet(g_ribbonHistory, v297_s)
if ((v297_s < (jsLen(g_ribbonHistory) - 1.0))) {
val v304_S: Double = ((v297_s + 1.0) / g_MAX_RIBBON_HIST)
val v305_v: Double = (1.0 - (v304_S * 0.48))
val v306_T: Double = ((num(v288_u) * 0.5) + (v304_S * 24.0))
val v307_b: Double = ((num(v289_f) * 0.56) - (v304_S * (num(v289_f) * 0.28)))
val v308_h: Double = ((num(v288_u) * 0.86) * v305_v)
val v309_p: Any? = jsGet(g_ribbonHistory, (v297_s + 1.0))
v287_l.strokeStyle = jsGet(v285_r, "secondary")
v287_l.globalAlpha = ((1.0 - v298_i) * 0.45)
v287_l.lineWidth = 1.0
v287_l.beginPath()
run {
var v310_w: Double = 0.0
L37@ while ((v310_w < v290_M)) {
val v311_A: Double = ((v310_w / (v290_M - 1.0)) - 0.5)
v287_l.moveTo((v306_T + (v311_A * v308_h)), (v307_b + (num(jsGet(v309_p, v310_w)) * v305_v)))
v287_l.lineTo((v300_m + (v311_A * v302_y)), (v301_d + (num(jsGet(v303_c, v310_w)) * v299_g)))
v310_w += 2.0
}
}
v287_l.stroke()
}
v287_l.strokeStyle = (if ((v297_s == 0.0)) jsGet(v285_r, "accent") else (if (((v297_s % 2.0) == 0.0)) jsGet(v285_r, "primary") else jsGet(v285_r, "secondary")))
v287_l.globalAlpha = jsMax(0.18, (1.0 - (v298_i * 0.78)))
v287_l.lineWidth = (if ((v297_s == 0.0)) 2.6 else 1.4)
v287_l.beginPath()
run {
var v312_S: Double = 0.0
L38@ while ((v312_S < v290_M)) {
val v313_v: Double = ((v312_S / (v290_M - 1.0)) - 0.5)
val v314_T: Double = (v300_m + (v313_v * v302_y))
val v315_b: Double = (v301_d + (num(jsGet(v303_c, v312_S)) * v299_g))
if ((v312_S == 0.0)) {
v287_l.moveTo(v314_T, v315_b)
} else {
v287_l.lineTo(v314_T, v315_b)
}
v312_S++
}
}
v287_l.stroke()
v297_s--
}
}
v287_l.globalAlpha = 1.0
rrSetText(Dom.getElementById("mid-meter"), ("MID: " + jsStr(jsRound((v284_o * 100.0))) + "%"))
}

var g_vuLeft: Double = 0.0

var g_vuRight: Double = 0.0

var g_peakHoldL: Double = 0.0

var g_peakHoldR: Double = 0.0

fun renderVuMeterVisualizer(v316_e: Double, v317_a: Double, v318_o: Double, v319_r: Any?): Unit {

val v320_t: Any? = Dom.getElementById("canvas-vumeter")
if (((!(truthy(v320_t)) || jsEq(jsGet(v320_t, "width"), 0.0)) || !(truthy(Ext.call("canvasWindowIsActive", v320_t))))) {
return
}
val v321_l: Ctx = jsGetContext(v320_t)
val v322_u: Any? = jsGet(v320_t, "width")
val v323_f: Any? = jsGet(v320_t, "height")
v321_l.fillStyle = "#06020e"
v321_l.fillRect(0.0, 0.0, num(v322_u), num(v323_f))
val v324_M: Double = jsMin(1.0, ((v316_e * 1.15) + (v317_a * 0.35)))
val v325_n: Double = jsMin(1.0, ((v317_a * 0.85) + (v318_o * 0.75)))
g_vuLeft += ((v324_M - g_vuLeft) * 0.25)
g_vuRight += ((v325_n - g_vuRight) * 0.25)
g_peakHoldL = jsMax(g_vuLeft, (g_peakHoldL - 0.008))
g_peakHoldR = jsMax(g_vuRight, (g_peakHoldR - 0.008))
val v326_s: Any? = jsArr(jsObj("label" to "L-CH ANALOG", "val" to g_vuLeft, "peak" to g_peakHoldL, "cx" to (num(v322_u) * 0.24)), jsObj("label" to "R-CH ANALOG", "val" to g_vuRight, "peak" to g_peakHoldR, "cx" to (num(v322_u) * 0.76)))
js_forEach(v326_s) L39@{ v334_v: Any?, _, _ ->

val v335_T: Double = (num(v322_u) * 0.4)
val v336_b: Double = (num(v323_f) * 0.8)
val v337_h: Double = (num(jsGet(v334_v, "cx")) - (v335_T / 2.0))
val v338_p: Double = (num(v323_f) * 0.1)
val v339_w: Grad = v321_l.createRadialGradient(num(jsGet(v334_v, "cx")), (v338_p + (v336_b * 0.85)), 5.0, num(jsGet(v334_v, "cx")), (v338_p + (v336_b * 0.5)), (v335_T * 0.7))
v339_w.addColorStop(0.0, "#2a1044")
v339_w.addColorStop(0.7, "#120622")
v339_w.addColorStop(1.0, "#06020d")
v321_l.fillStyle = v339_w
v321_l.strokeStyle = jsGet(v319_r, "primary")
v321_l.lineWidth = 1.8
v321_l.fillRect(v337_h, v338_p, v335_T, v336_b)
v321_l.strokeRect(v337_h, v338_p, v335_T, v336_b)
val v340_A: Any? = jsGet(v334_v, "cx")
val v341_C: Double = (v338_p + (v336_b * 0.88))
val v342_E: Double = jsMin((v335_T * 0.44), (v336_b * 0.7))
run {
var v345_F: Double = 0.0
L40@ while ((v345_F <= 10.0)) {
val v346_O: Double = (Math.PI * (1.18 + ((v345_F / 10.0) * 0.64)))
v321_l.strokeStyle = (if ((v345_F >= 7.0)) jsGet(v319_r, "accent") else jsGet(v319_r, "secondary"))
v321_l.lineWidth = (if (((v345_F % 2.0) == 0.0)) 1.8 else 1.0)
v321_l.beginPath()
v321_l.moveTo((num(v340_A) + (kotlin.math.cos(v346_O) * (v342_E * 0.88))), (v341_C + (kotlin.math.sin(v346_O) * (v342_E * 0.88))))
v321_l.lineTo((num(v340_A) + (kotlin.math.cos(v346_O) * (v342_E * 1.02))), (v341_C + (kotlin.math.sin(v346_O) * (v342_E * 1.02))))
v321_l.stroke()
v345_F++
}
}
v321_l.strokeStyle = jsGet(v319_r, "primary")
v321_l.lineWidth = 1.6
v321_l.beginPath()
v321_l.arc(num(v340_A), v341_C, (v342_E * 0.88), (Math.PI * 1.18), (Math.PI * 1.82))
v321_l.stroke()
v321_l.strokeStyle = jsGet(v319_r, "accent")
v321_l.lineWidth = 3.2
v321_l.beginPath()
v321_l.arc(num(v340_A), v341_C, (v342_E * 0.88), (Math.PI * 1.64), (Math.PI * 1.82))
v321_l.stroke()
val v343_k: Double = (Math.PI * (1.18 + (num(jsGet(v334_v, "peak")) * 0.64)))
v321_l.strokeStyle = jsGet(v319_r, "secondary")
v321_l.globalAlpha = 0.6
v321_l.lineWidth = 1.4
v321_l.beginPath()
v321_l.moveTo(num(v340_A), v341_C)
v321_l.lineTo((num(v340_A) + (kotlin.math.cos(v343_k) * (v342_E * 1.02))), (v341_C + (kotlin.math.sin(v343_k) * (v342_E * 1.02))))
v321_l.stroke()
v321_l.globalAlpha = 1.0
val v344_R: Double = (Math.PI * (1.18 + (num(jsGet(v334_v, "val")) * 0.64)))
v321_l.strokeStyle = "rgba(0,0,0,0.65)"
v321_l.lineWidth = 3.0
v321_l.beginPath()
v321_l.moveTo((num(v340_A) + 3.0), (v341_C + 2.0))
v321_l.lineTo(((num(v340_A) + (kotlin.math.cos(v344_R) * (v342_E * 1.05))) + 3.0), ((v341_C + (kotlin.math.sin(v344_R) * (v342_E * 1.05))) + 2.0))
v321_l.stroke()
v321_l.strokeStyle = "#ffffff"
v321_l.lineWidth = 2.2
v321_l.beginPath()
v321_l.moveTo(num(v340_A), v341_C)
v321_l.lineTo((num(v340_A) + (kotlin.math.cos(v344_R) * (v342_E * 1.05))), (v341_C + (kotlin.math.sin(v344_R) * (v342_E * 1.05))))
v321_l.stroke()
v321_l.fillStyle = jsGet(v319_r, "primary")
v321_l.beginPath()
v321_l.arc(num(v340_A), v341_C, 5.0, 0.0, (Math.PI * 2.0))
v321_l.fill()
v321_l.fillStyle = "rgba(255,255,255,0.05)"
v321_l.beginPath()
v321_l.moveTo(v337_h, v338_p)
v321_l.lineTo((v337_h + (v335_T * 0.55)), v338_p)
v321_l.lineTo(v337_h, (v338_p + (v336_b * 0.65)))
v321_l.closePath()
v321_l.fill()
v321_l.fillStyle = jsGet(v319_r, "primary")
v321_l.font = "bold 10px \"Courier New\", monospace"
v321_l.fillText(str(jsGet(v334_v, "label")), (v337_h + 6.0), (v338_p + 13.0))
null
}
val v327_i: Double = 10.0
val v328_g: Double = (num(v322_u) * 0.05)
val v329_m: Double = ((num(v322_u) * 0.5) - (v328_g / 2.0))
val v330_d: Double = (num(v323_f) * 0.14)
val v331_y: Double = (num(v323_f) * 0.72)
val v332_c: Double = (v331_y / v327_i)
run {
var v347_v: Double = 0.0
L41@ while ((v347_v < v327_i)) {
val v348_T: Double = (((v327_i - 1.0) - v347_v) / v327_i)
val v349_b: Boolean = (jsMax(g_vuLeft, g_vuRight) >= v348_T)
v321_l.fillStyle = (if (v349_b) (if ((v347_v < 2.0)) jsGet(v319_r, "accent") else (if ((v347_v < 5.0)) jsGet(v319_r, "secondary") else jsGet(v319_r, "primary"))) else "#150924")
v321_l.fillRect(v329_m, (v330_d + (v347_v * v332_c)), v328_g, (v332_c - 2.0))
v347_v++
}
}
val v333_S: String = jsToFixed(((jsMax(g_vuLeft, g_vuRight) * 24.0) - 20.0), 1.0)
rrSetText(Dom.getElementById("vu-db-readout"), ("PEAK: " + (if ((num(v333_S) > 0.0)) "+" else "") + v333_S + " dB"))
}

var g_EQ_FREQS: Any? = null

var g_eqPeakCaps: Any? = null

var g_dspChainWired: Boolean = false

fun renderEqVisualizer(v350_e: Double, v351_a: Double, v352_o: Any?): Unit {

val v353_r: Any? = Dom.getElementById("canvas-eq")
if (((!(truthy(v353_r)) || jsEq(jsGet(v353_r, "width"), 0.0)) || !(truthy(Ext.call("canvasWindowIsActive", v353_r))))) {
return
}
if ((truthy(Ext.v("audioCtx")) && !g_dspChainWired)) {
Ext.call("ensureDspChain")
}
val v354_t: Ctx = jsGetContext(v353_r)
val v355_l: Any? = jsGet(v353_r, "width")
val v356_u: Any? = jsGet(v353_r, "height")
v354_t.fillStyle = "rgba(4, 1, 12, 0.42)"
v354_t.fillRect(0.0, 0.0, num(v355_l), num(v356_u))
v354_t.strokeStyle = "rgba(255,255,255,0.09)"
v354_t.lineWidth = 1.0
run {
var v364_d: Double = 1.0
L42@ while ((v364_d <= 3.0)) {
val v365_y: Double = ((v364_d / 4.0) * num(v356_u))
v354_t.beginPath()
v354_t.moveTo(0.0, v365_y)
v354_t.lineTo(num(v355_l), v365_y)
v354_t.stroke()
v364_d++
}
}
val v357_f: Double = 24.0
val v358_M: Double = ((num(v355_l) - 16.0) / v357_f)
val v359_n: Double = (v358_M * 0.64)
val v360_s: Double = 4.0
val v361_i: Double = -4.0
run {
var v366_d: Double = 0.0
L43@ while ((v366_d < v357_f)) {
val v367_y: Double = (Rt.freqDataArr[jsIdx((v366_d * 3.0))] / 255.0)
val v368_c: Double = jsMax(3.0, (v367_y * (num(v356_u) - 16.0)))
if ((v368_c > num(jsGet(g_eqPeakCaps, v366_d)))) {
jsSet(g_eqPeakCaps, v366_d, v368_c)
} else {
jsSet(g_eqPeakCaps, v366_d, jsMax(3.0, (num(jsGet(g_eqPeakCaps, v366_d)) - 1.5)))
}
val v369_S: Double = (8.0 + (v366_d * v358_M))
val v370_v: Double = ((num(v356_u) - 6.0) - v368_c)
v354_t.fillStyle = (if (((v366_d % 2.0) == 0.0)) ("rgba(" + str(jsGet(v352_o, "waveRgb")) + ", 0.52)") else ("rgba(" + str(jsGet(v352_o, "altWaveRgb")) + ", 0.52)"))
v354_t.fillRect(v369_S, v370_v, v359_n, v368_c)
v354_t.fillStyle = jsGet(v352_o, "accent")
v354_t.beginPath()
v354_t.moveTo(v369_S, v370_v)
v354_t.lineTo((v369_S + v360_s), (v370_v + v361_i))
v354_t.lineTo(((v369_S + v359_n) + v360_s), (v370_v + v361_i))
v354_t.lineTo((v369_S + v359_n), v370_v)
v354_t.closePath()
v354_t.fill()
v354_t.fillStyle = "#ffffff"
v354_t.fillRect(v369_S, ((num(v356_u) - 9.0) - num(jsGet(g_eqPeakCaps, v366_d))), v359_n, 2.0)
v366_d++
}
}
val v362_g: Any? = js_map(g_EQ_FREQS) L44@{ v371_d: Any?, v372_y: Double, _ ->

val v373_c: Double = jsParseFloat(jsOrND(jsGet(Dom.getElementById(("eq-b-" + jsStr(v372_y))), "value")) { 0.0 })
return@L44 jsObj("x" to (((v372_y / (jsLen(g_EQ_FREQS) - 1.0)) * (num(v355_l) - 28.0)) + 14.0), "y" to ((num(v356_u) * 0.5) - ((v373_c / 12.0) * (num(v356_u) * 0.4))))
null
}
val v363_m: Grad = v354_t.createLinearGradient(0.0, 0.0, 0.0, num(v356_u))
v363_m.addColorStop(0.0, ("rgba(" + str(jsGet(v352_o, "altWaveRgb")) + ", 0.42)"))
v363_m.addColorStop(1.0, "transparent")
v354_t.fillStyle = v363_m
v354_t.beginPath()
v354_t.moveTo(num(jsGet(jsGet(v362_g, 0.0), "x")), num(v356_u))
js_forEach(v362_g) L45@{ v374_d: Any?, v375_y: Double, _ ->

if ((v375_y == 0.0)) {
v354_t.lineTo(num(jsGet(v374_d, "x")), num(jsGet(v374_d, "y")))
} else {
val v376_c: Any? = jsGet(v362_g, (v375_y - 1.0))
val v377_S: Double = ((num(jsGet(v376_c, "x")) + num(jsGet(v374_d, "x"))) * 0.5)
v354_t.bezierCurveTo(v377_S, num(jsGet(v376_c, "y")), v377_S, num(jsGet(v374_d, "y")), num(jsGet(v374_d, "x")), num(jsGet(v374_d, "y")))
}
null
}
v354_t.lineTo(num(jsGet(jsGet(v362_g, (jsLen(v362_g) - 1.0)), "x")), num(v356_u))
v354_t.closePath()
v354_t.fill()
v354_t.strokeStyle = jsGet(v352_o, "primary")
v354_t.lineWidth = 2.4
v354_t.beginPath()
js_forEach(v362_g) L46@{ v378_d: Any?, v379_y: Double, _ ->

if ((v379_y == 0.0)) {
v354_t.moveTo(num(jsGet(v378_d, "x")), num(jsGet(v378_d, "y")))
} else {
val v380_c: Any? = jsGet(v362_g, (v379_y - 1.0))
val v381_S: Double = ((num(jsGet(v380_c, "x")) + num(jsGet(v378_d, "x"))) * 0.5)
v354_t.bezierCurveTo(v381_S, num(jsGet(v380_c, "y")), v381_S, num(jsGet(v378_d, "y")), num(jsGet(v378_d, "x")), num(jsGet(v378_d, "y")))
}
null
}
v354_t.stroke()
js_forEach(v362_g) L47@{ v382_d: Any?, v383_y: Double, _ ->

v354_t.fillStyle = (if (((v383_y % 2.0) == 0.0)) jsGet(v352_o, "accent") else "#ffffff")
v354_t.strokeStyle = "#000"
v354_t.lineWidth = 1.0
v354_t.beginPath()
v354_t.arc(num(jsGet(v382_d, "x")), num(jsGet(v382_d, "y")), (4.0 + (v351_a * 2.0)), 0.0, (Math.PI * 2.0))
v354_t.fill()
v354_t.stroke()
null
}
}

fun renderFxVisualizer(v384_e: Double, v385_a: Double, v386_o: Double, v387_r: Any?): Unit {

val v388_t: Any? = Dom.getElementById("canvas-fx")
if (((!(truthy(v388_t)) || jsEq(jsGet(v388_t, "width"), 0.0)) || !(truthy(Ext.call("canvasWindowIsActive", v388_t))))) {
return
}
val v389_l: Ctx = jsGetContext(v388_t)
val v390_u: Any? = jsGet(v388_t, "width")
val v391_f: Any? = jsGet(v388_t, "height")
val v392_M: Double = jsParseFloat(jsOrND(jsGet(Dom.getElementById("fx-dmix"), "value")) { 0.0 })
val v393_n: Double = jsParseFloat(jsOrND(jsGet(Dom.getElementById("fx-speed"), "value")) { 1.0 })
val v394_s: Double = (jsParseFloat(jsOrND(jsGet(Dom.getElementById("fx-drive"), "value")) { 0.0 }) / 100.0)
val v395_i: Double = (jsParseFloat(jsOrND(jsGet(Dom.getElementById("fx-chorus"), "value")) { 0.0 }) / 100.0)
val v396_g: Double = (jsParseFloat(jsOrND(jsGet(Dom.getElementById("fx-ring"), "value")) { 0.0 }) / 100.0)
val v397_m: Double = jsParseFloat(jsOrND(jsGet(Dom.getElementById("fx-pan"), "value")) { 0.0 })
val v398_d: Double = (jsParseFloat(jsOrND(jsGet(Dom.getElementById("fx-reverb"), "value")) { 0.0 }) / 100.0)
val v399_y: Double = (jsParseFloat(jsOrND(jsGet(Dom.getElementById("fx-phaser"), "value")) { 0.0 }) / 100.0)
v389_l.fillStyle = ("rgba(4, 1, 12, " + jsToFixed((0.38 - (jsMax(v392_M, (v398_d * 0.7)) * 0.22)), 2.0) + ")")
v389_l.fillRect(0.0, 0.0, num(v390_u), num(v391_f))
val v400_c: Double = ((num(v390_u) * 0.5) + (v397_m * (num(v390_u) * 0.22)))
val v401_S: Double = (num(v391_f) * 0.5)
val v402_v: Double = 7.0
run {
var v405_h: Double = 1.0
L48@ while ((v405_h <= v402_v)) {
val v406_p: Double = (((v405_h + ((v384_e * v393_n) * 1.8)) % v402_v) / v402_v)
val v407_w: Double = (v406_p * (num(v390_u) * 0.44))
val v408_A: Double = (v406_p * (num(v391_f) * 0.44))
v389_l.strokeStyle = (if (((v405_h % 2.0) == 0.0)) jsGet(v387_r, "primary") else jsGet(v387_r, "secondary"))
v389_l.globalAlpha = ((1.0 - v406_p) * (0.25 + (jsMaxN(v392_M, v398_d, v399_y) * 0.65)))
v389_l.lineWidth = (1.0 + (v394_s * 2.5))
v389_l.beginPath()
v389_l.ellipse(v400_c, v401_S, v407_w, v408_A, ((v384_e * 0.5) * (if (((v405_h % 2.0) == 0.0)) 1.0 else -1.0)), 0.0, (Math.PI * 2.0))
v389_l.stroke()
v405_h++
}
}
v389_l.globalAlpha = 1.0
run {
var v409_h: Double = 1.0
L49@ while ((v409_h >= 0.0)) {
v389_l.strokeStyle = (if ((v409_h == 0.0)) jsGet(v387_r, "accent") else jsGet(v387_r, "secondary"))
v389_l.lineWidth = (if ((v409_h == 0.0)) 2.2 else 1.4)
v389_l.beginPath()
val v410_p: Double = 72.0
run {
var v411_w: Double = 0.0
L50@ while ((v411_w < v410_p)) {
val v412_A: Double = (v411_w / (v410_p - 1.0))
val v413_C: Double = (v412_A * num(v390_u))
val v414_E: Double = ((Rt.timeDataArr[jsIdx((v411_w * 2.0))] - 128.0) / 128.0)
val v415_k: Double = (if ((v396_g > 0.0)) ((kotlin.math.sin(((v412_A * 36.0) + (v384_e * 24.0))) * v396_g) * 0.45) else 0.0)
val v416_R: Double = (if ((v409_h == 1.0)) (kotlin.math.sin(((v384_e * 5.0) + (v412_A * 8.0))) * (4.0 + ((v395_i + v399_y) * 14.0))) else 0.0)
val v417_F: Double = ((v401_S + ((v414_E + v415_k) * (num(v391_f) * (0.38 + (v394_s * 0.25))))) + v416_R)
if ((v411_w == 0.0)) {
v389_l.moveTo(v413_C, v417_F)
} else {
v389_l.lineTo(v413_C, v417_F)
}
v411_w++
}
}
v389_l.stroke()
v409_h--
}
}
val v403_T: Double = 8.0
val v404_b: Double = ((num(v391_f) - 10.0) / v403_T)
run {
var v418_h: Double = 0.0
L51@ while ((v418_h < v403_T)) {
val v419_p: Double = (((v403_T - 1.0) - v418_h) / v403_T)
val v420_w: Boolean = ((v385_a * (1.0 - jsMax(0.0, v397_m))) >= v419_p)
val v421_A: Boolean = (((v386_o * (1.0 + jsMin(0.0, v397_m))) + (v385_a * 0.5)) >= v419_p)
v389_l.fillStyle = (if (v420_w) (if ((v418_h < 2.0)) jsGet(v387_r, "accent") else jsGet(v387_r, "primary")) else "#140824")
v389_l.fillRect(4.0, (5.0 + (v418_h * v404_b)), 6.0, (v404_b - 2.0))
v389_l.fillStyle = (if (v421_A) (if ((v418_h < 2.0)) jsGet(v387_r, "accent") else jsGet(v387_r, "secondary")) else "#140824")
v389_l.fillRect((num(v390_u) - 10.0), (5.0 + (v418_h * v404_b)), 6.0, (v404_b - 2.0))
v418_h++
}
}
}

var g_cassetteTransportMode: String = ""

var g_cassetteReelAngle: Double = 0.0

var g_cassetteTapeProgress: Double = 0.0

var g_cassetteScratchActive: Boolean = false

var g_currentTapeType: String = ""

fun renderCassetteVisualizer(v422_e: Double, v423_a: Double, v424_o: Double, v425_r: Double, v426_t: Double, v427_l: Any?): Unit {

val v428_u: Double = jsParseFloat(jsOrND(jsGet(Dom.getElementById("fx-speed"), "value")) { 1.0 })
val v429_f: Double = (jsParseFloat(jsOrND(jsGet(Dom.getElementById("cas-wow"), "value")) { 12.0 }) / 100.0)
val v430_M: Any? = jsAnd(jsEq(Rt.win("__rrVideo95PlaybackKind"), "native")) { Rt.win("__rrVideo95MediaElement") }
val v431_n: Boolean = jsEq(Rt.win("__rrVideo95PlaybackKind"), "youtube")
val v432_s: Any? = (if (truthy(v430_M)) Rt.win("__rrVideo95MediaElement") else Ext.v("audioEl"))
val v433_i: Any? = jsOr((!(!(truthy(Rt.win("__rrVideo95AudioActive")))) || !(!(truthy(Rt.win("__rrVideo95YoutubeVisualActive")))))) { Ext.v("isPlaying") }
if (g_cassetteScratchActive) {

} else {
if ((g_cassetteTransportMode == "ff")) {
g_cassetteReelAngle -= (v423_a * 22.0)
val v487_Z: Double = (2.35 + (kotlin.math.sin((v422_e * 38.0)) * 0.25))
if (truthy(jsAnd(v430_M) { v432_s })) {
jsSet(v432_s, "playbackRate", v487_Z)
if ((jsNumberFn_isFinite(jsGet(v432_s, "duration")) && (num(jsGet(v432_s, "duration")) > 0.0))) {
val v488_H: Double = (num(jsGet(v432_s, "currentTime")) + (v423_a * 14.0))
if ((v488_H >= (num(jsGet(v432_s, "duration")) - 0.4))) {
jsSet(v432_s, "currentTime", 0.0)
Ext.call("cassetteDeckPlay")
} else {
jsSet(v432_s, "currentTime", v488_H)
}
}
} else {
if (v431_n) {
Ext.call("setVideo95YoutubeCassetteRate", 2.0)
g_cassetteTapeProgress = ((g_cassetteTapeProgress + (v423_a * 0.12)) % 1.0)
} else {
jsSet(Ext.v("audioEl"), "playbackRate", v487_Z)
if ((truthy(jsAnd(!(truthy(Ext.v("useSynthFallback")))) { jsGet(Ext.v("audioEl"), "duration") }) && (num(jsGet(Ext.v("audioEl"), "duration"))).isFinite())) {
val v489_H: Double = (num(jsGet(Ext.v("audioEl"), "currentTime")) + (v423_a * 14.0))
if ((v489_H >= (num(jsGet(Ext.v("audioEl"), "duration")) - 0.4))) {
jsSet(Ext.v("audioEl"), "currentTime", 0.0)
Ext.call("cassetteDeckPlay")
} else {
jsSet(Ext.v("audioEl"), "currentTime", v489_H)
}
} else {
g_cassetteTapeProgress = ((g_cassetteTapeProgress + (v423_a * 0.12)) % 1.0)
}
}
}
if (truthy(jsAnd(Ext.v("casCueBandpass")) { Ext.v("audioCtx") })) {
jsInvoke(jsGet(Ext.v("casCueBandpass"), "frequency"), "setTargetAtTime", (1500.0 + (kotlin.math.sin((v422_e * 44.0)) * 450.0)), jsGet(Ext.v("audioCtx"), "currentTime"), 0.015)
}
} else {
if ((g_cassetteTransportMode == "rew")) {
g_cassetteReelAngle += (v423_a * 22.0)
val v490_Z: Double = ((2.15 + (kotlin.math.sin((v422_e * 46.0)) * 0.35)) + (kotlin.math.cos((v422_e * 95.0)) * 0.18))
if (truthy(jsAnd(v430_M) { v432_s })) {
jsSet(v432_s, "playbackRate", 0.5)
if ((jsNumberFn_isFinite(jsGet(v432_s, "duration")) && (num(jsGet(v432_s, "duration")) > 0.0))) {
val v491_H: Double = (num(jsGet(v432_s, "currentTime")) - (v423_a * 14.0))
if ((v491_H <= 0.15)) {
jsSet(v432_s, "currentTime", 0.0)
Ext.call("playMechanicalClick", 140.0, 0.07)
Ext.call("cassetteDeckPlay")
} else {
jsSet(v432_s, "currentTime", v491_H)
}
}
} else {
if (v431_n) {
Ext.call("setVideo95YoutubeCassetteRate", 0.5)
g_cassetteTapeProgress = (((g_cassetteTapeProgress - (v423_a * 0.12)) + 1.0) % 1.0)
} else {
jsSet(Ext.v("audioEl"), "playbackRate", jsMax(0.5, v490_Z))
if ((truthy(jsAnd(!(truthy(Ext.v("useSynthFallback")))) { jsGet(Ext.v("audioEl"), "duration") }) && (num(jsGet(Ext.v("audioEl"), "duration"))).isFinite())) {
val v492_H: Double = (num(jsGet(Ext.v("audioEl"), "currentTime")) - (v423_a * 14.0))
if ((v492_H <= 0.15)) {
jsSet(Ext.v("audioEl"), "currentTime", 0.0)
Ext.call("playMechanicalClick", 140.0, 0.07)
Ext.call("cassetteDeckPlay")
} else {
jsSet(Ext.v("audioEl"), "currentTime", v492_H)
}
} else {
g_cassetteTapeProgress = (((g_cassetteTapeProgress - (v423_a * 0.12)) + 1.0) % 1.0)
}
}
}
if (truthy(jsAnd(Ext.v("casCueBandpass")) { Ext.v("audioCtx") })) {
jsInvoke(jsGet(Ext.v("casCueBandpass"), "frequency"), "setTargetAtTime", (1250.0 + (kotlin.math.cos((v422_e * 52.0)) * 520.0)), jsGet(Ext.v("audioCtx"), "currentTime"), 0.015)
}
} else {
if ((truthy(v433_i) && (g_cassetteTransportMode == "play"))) {
val v493_Z: Double = ((kotlin.math.sin((v422_e * 3.1)) * (v429_f * 0.045)) + (kotlin.math.sin((v422_e * 21.0)) * (v429_f * 0.015)))
val v494_H: Double = jsMax(0.4, jsMin(2.0, (v428_u + v493_Z)))
if (truthy(jsAnd(v430_M) { v432_s })) {
jsSet(v432_s, "playbackRate", v494_H)
} else {
if (v431_n) {
Ext.call("setVideo95YoutubeCassetteRate", v494_H)
} else {
jsSet(Ext.v("audioEl"), "playbackRate", v494_H)
}
}
g_cassetteReelAngle -= ((v423_a * 2.6) * v494_H)
}
}
}
}
if (((truthy(jsAnd(v430_M) { v432_s }) && jsNumberFn_isFinite(jsGet(v432_s, "duration"))) && (num(jsGet(v432_s, "duration")) > 0.0))) {
g_cassetteTapeProgress = jsMax(0.0, jsMin(1.0, (num(jsGet(v432_s, "currentTime")) / num(jsGet(v432_s, "duration")))))
} else {
if (((truthy(jsAnd((!v431_n && !(truthy(Ext.v("useSynthFallback"))))) { jsGet(Ext.v("audioEl"), "duration") }) && (num(jsGet(Ext.v("audioEl"), "duration"))).isFinite()) && (num(jsGet(Ext.v("audioEl"), "duration")) > 0.0))) {
g_cassetteTapeProgress = jsMax(0.0, jsMin(1.0, (num(jsGet(Ext.v("audioEl"), "currentTime")) / num(jsGet(Ext.v("audioEl"), "duration")))))
} else {
if ((truthy(v433_i) && (g_cassetteTransportMode == "play"))) {
g_cassetteTapeProgress = ((g_cassetteTapeProgress + (v423_a * 0.008)) % 1.0)
}
}
}
val v434_g: Any? = Dom.getElementById("cas-status-counter")
if (truthy(v434_g)) {
val v495_Z: Double = kotlin.math.floor((g_cassetteTapeProgress * 9999.0))
val v496_H: Double = (if (truthy(jsAnd(v430_M) { v432_s })) jsOrN(num(jsGet(v432_s, "playbackRate"))) { 1.0 } else (if (v431_n) 1.0 else jsOrN(num(jsGet(Ext.v("audioEl"), "playbackRate"))) { 1.0 }))
val v497_fe: String = (if ((g_cassetteTransportMode == "ff")) "+55.0cm/s" else (if ((g_cassetteTransportMode == "rew")) "-55.0cm/s" else (if (truthy(v433_i)) (jsToFixed((4.76 * v496_H), 2.0) + "cm/s") else "0.00cm/s")))
rrSetText(v434_g, ("CNT: " + (jsStr(v495_Z)).padStart(jsIdx(4.0), "0"[0]) + " | SPD: " + v497_fe))
}
val v435_m: Any? = Dom.getElementById("canvas-cassette")
if (((!(truthy(v435_m)) || jsEq(jsGet(v435_m, "width"), 0.0)) || !(truthy(Ext.call("canvasWindowIsActive", v435_m))))) {
return
}
val v436_d: Ctx = jsGetContext(v435_m)
val v437_y: Any? = jsGet(v435_m, "width")
val v438_c: Any? = jsGet(v435_m, "height")
val v439_S: Boolean = jsEq(jsInvoke(Dom.documentElement, "getAttribute", "data-rr-os"), "longhorn")
if ((!(truthy(Rt.win("__rrCasAcc"))) || ((Rt.nowMs() - num(Rt.win("__rrCasAccT"))) > 1500.0))) {
Rt.setWin("__rrCasAccT", Rt.nowMs())
val v498_Z: Any? = Dom.computedStyle(Dom.documentElement)
Rt.setWin("__rrCasAcc", jsOrSD(jsInvoke(jsOrSD(jsInvoke(v498_Z, "getPropertyValue", "--os-accent")) { "" }, "trim")) { "#7890a6" })
Rt.setWin("__rrCasAccL", jsOrSD(jsInvoke(jsOrSD(jsInvoke(v498_Z, "getPropertyValue", "--os-accent-light")) { "" }, "trim")) { "#d3e0ec" })
}
val v440_v: Any? = Rt.win("__rrCasAcc")
val v441_T: Any? = Rt.win("__rrCasAccL")
fun v442_b(v499_Z: Double, v500_H: Double, v501_fe: Double, v502_we: Double, v503_Se: Double): Unit {
var v503_Se = v503_Se
v503_Se = jsMinN(v503_Se, (v501_fe / 2.0), (v502_we / 2.0))
v436_d.beginPath()
v436_d.moveTo((v499_Z + v503_Se), v500_H)
v436_d.arcTo((v499_Z + v501_fe), v500_H, (v499_Z + v501_fe), (v500_H + v502_we), v503_Se)
v436_d.arcTo((v499_Z + v501_fe), (v500_H + v502_we), v499_Z, (v500_H + v502_we), v503_Se)
v436_d.arcTo(v499_Z, (v500_H + v502_we), v499_Z, v500_H, v503_Se)
v436_d.arcTo(v499_Z, v500_H, (v499_Z + v501_fe), v500_H, v503_Se)
v436_d.closePath()
}
val v443_h: Double = jsMin((num(v437_y) / 100.0), (num(v438_c) / 64.0))
val v444_p: Grad = v436_d.createLinearGradient(0.0, 0.0, 0.0, num(v438_c))
if (v439_S) {
v444_p.addColorStop(0.0, "#1b232b")
v444_p.addColorStop(1.0, "#0c1115")
} else {
v444_p.addColorStop(0.0, "#2a1450")
v444_p.addColorStop(0.62, "#6e2f86")
v444_p.addColorStop(1.0, "#c95b8f")
}
v436_d.fillStyle = v444_p
v436_d.fillRect(0.0, 0.0, num(v437_y), num(v438_c))
if (!v439_S) {
v436_d.globalAlpha = 0.16
v436_d.fillStyle = jsOrSD(jsGet(v427_l, "sunSlice")) { "#ffb36b" }
run {
var v504_Z: Double = 0.0
L52@ while ((v504_Z < 7.0)) {
val v505_H: Double = (num(v438_c) * (0.62 + (v504_Z * 0.055)))
v436_d.fillRect(0.0, v505_H, num(v437_y), jsMax(1.0, ((num(v438_c) * 0.022) * (1.0 - (v504_Z * 0.1)))))
v504_Z++
}
}
v436_d.globalAlpha = 1.0
} else {
val v506_Z: Grad = v436_d.createRadialGradient((num(v437_y) * 0.5), (num(v438_c) * 1.05), 0.0, (num(v437_y) * 0.5), (num(v438_c) * 1.05), (num(v437_y) * 0.7))
v506_Z.addColorStop(0.0, v440_v)
v506_Z.addColorStop(1.0, "transparent")
v436_d.globalAlpha = 0.28
v436_d.fillStyle = v506_Z
v436_d.fillRect(0.0, 0.0, num(v437_y), num(v438_c))
v436_d.globalAlpha = 1.0
}
var v445_w: Double = jsMin((num(v437_y) * 0.9), ((num(v438_c) * 0.9) * 1.58))
var v446_A: Double = (v445_w / 1.58)
val v447_C: Double = ((num(v437_y) - v445_w) / 2.0)
val v448_E: Double = ((num(v438_c) - v446_A) / 2.0)
val v449_k: Double = (num(v437_y) * 0.5)
val v450_R: Double = (v445_w * 0.035)
v436_d.save()
v436_d.shadowColor = "rgba(0,0,0,.45)"
v436_d.shadowBlur = (v443_h * 3.0)
v436_d.shadowOffsetY = (v443_h * 1.2)
v442_b(v447_C, v448_E, v445_w, v446_A, v450_R)
val v451_F: Grad = v436_d.createLinearGradient(0.0, v448_E, 0.0, (v448_E + v446_A))
if (v439_S) {
v451_F.addColorStop(0.0, "rgba(70,82,94,.96)")
v451_F.addColorStop(0.5, "rgba(38,46,54,.97)")
v451_F.addColorStop(1.0, "rgba(24,30,36,.98)")
} else {
v451_F.addColorStop(0.0, "#e6e6ea")
v451_F.addColorStop(0.55, "#c9cbd2")
v451_F.addColorStop(1.0, "#a9acb6")
}
v436_d.fillStyle = v451_F
v436_d.fill()
v436_d.restore()
v436_d.lineWidth = jsMax(1.0, (v443_h * 0.35))
v436_d.strokeStyle = (if (v439_S) "rgba(0,0,0,.7)" else "#3a3a44")
v442_b(v447_C, v448_E, v445_w, v446_A, v450_R)
v436_d.stroke()
v436_d.strokeStyle = (if (v439_S) "rgba(255,255,255,.18)" else "rgba(255,255,255,.85)")
v436_d.lineWidth = jsMax(1.0, (v443_h * 0.3))
v442_b((v447_C + (v443_h * 0.6)), (v448_E + (v443_h * 0.6)), (v445_w - (v443_h * 1.2)), (v446_A - (v443_h * 1.2)), (v450_R * 0.8))
v436_d.stroke()
if (v439_S) {
val v507_Z: Grad = v436_d.createLinearGradient(0.0, v448_E, 0.0, (v448_E + (v446_A * 0.5)))
v507_Z.addColorStop(0.0, "rgba(255,255,255,.16)")
v507_Z.addColorStop(1.0, "rgba(255,255,255,0)")
v436_d.fillStyle = v507_Z
v442_b((v447_C + 1.0), (v448_E + 1.0), (v445_w - 2.0), (v446_A * 0.5), v450_R)
v436_d.fill()
}
val v452_O: Double = jsMax(2.0, (v445_w * 0.018))
js_forEach(jsArr(jsArr((v447_C + (v445_w * 0.045)), (v448_E + (v446_A * 0.07))), jsArr((v447_C + (v445_w * 0.955)), (v448_E + (v446_A * 0.07))), jsArr((v447_C + (v445_w * 0.045)), (v448_E + (v446_A * 0.93))), jsArr((v447_C + (v445_w * 0.955)), (v448_E + (v446_A * 0.93))), jsArr(v449_k, (v448_E + (v446_A * 0.93))))) L53@{ p54: Any?, _, _ ->
var v508_Z: Any? = jsGet(p54, 0.0)
var v509_H: Any? = jsGet(p54, 1.0)
val v510_fe: Grad = v436_d.createRadialGradient((num(v508_Z) - (v452_O * 0.3)), (num(v509_H) - (v452_O * 0.3)), 0.0, num(v508_Z), num(v509_H), v452_O)
v510_fe.addColorStop(0.0, (if (v439_S) "#9aa6b2" else "#ffffff"))
v510_fe.addColorStop(1.0, (if (v439_S) "#3a444e" else "#8a8d96"))
v436_d.fillStyle = v510_fe
v436_d.beginPath()
v436_d.arc(num(v508_Z), num(v509_H), v452_O, 0.0, 7.0)
v436_d.fill()
v436_d.strokeStyle = (if (v439_S) "rgba(0,0,0,.6)" else "#55575e")
v436_d.lineWidth = jsMax(1.0, (v452_O * 0.28))
v436_d.beginPath()
v436_d.moveTo((num(v508_Z) - (v452_O * 0.6)), num(v509_H))
v436_d.lineTo((num(v508_Z) + (v452_O * 0.6)), num(v509_H))
v436_d.moveTo(num(v508_Z), (num(v509_H) - (v452_O * 0.6)))
v436_d.lineTo(num(v508_Z), (num(v509_H) + (v452_O * 0.6)))
v436_d.stroke()
null
}
val v453_P: Double = (v447_C + (v445_w * 0.085))
val v454_I: Double = (v448_E + (v446_A * 0.085))
val v455_U_: Double = (v445_w * 0.83)
val v456_B: Double = (v446_A * 0.6)
v442_b(v453_P, v454_I, v455_U_, v456_B, (v450_R * 0.6))
if (v439_S) {
val v511_Z: Grad = v436_d.createLinearGradient(0.0, v454_I, 0.0, (v454_I + v456_B))
v511_Z.addColorStop(0.0, "rgba(220,232,242,.94)")
v511_Z.addColorStop(1.0, "rgba(176,192,206,.94)")
v436_d.fillStyle = v511_Z
} else {
v436_d.fillStyle = "#fbf3e4"
}
v436_d.fill()
v436_d.strokeStyle = (if (v439_S) "rgba(0,0,0,.35)" else "rgba(60,40,40,.45)")
v436_d.lineWidth = jsMax(1.0, (v443_h * 0.25))
v436_d.stroke()
val v457_L: Double = (v456_B * 0.2)
v436_d.save()
v442_b(v453_P, v454_I, v455_U_, v456_B, (v450_R * 0.6))
v436_d.clip()
if (v439_S) {
val v512_Z: Grad = v436_d.createLinearGradient(v453_P, 0.0, (v453_P + v455_U_), 0.0)
v512_Z.addColorStop(0.0, v440_v)
v512_Z.addColorStop(1.0, v441_T)
v436_d.fillStyle = v512_Z
v436_d.fillRect(v453_P, v454_I, v455_U_, v457_L)
} else {
val v513_Z: Any? = jsArr("#3a1a6e", "#8c3c9c", "#ff7aa8", "#ffb36b", "#ffd86b")
js_forEach(v513_Z) L55@{ v514_H: Any?, v515_fe: Double, _ ->

v436_d.fillStyle = v514_H
v436_d.fillRect(v453_P, (v454_I + ((v515_fe * v457_L) / jsLen(v513_Z))), v455_U_, ((v457_L / jsLen(v513_Z)) + 0.5))
null
}
}
v436_d.strokeStyle = (if (v439_S) "rgba(40,56,70,.22)" else "rgba(120,80,120,.22)")
v436_d.lineWidth = 1.0
run {
var v516_Z: Double = 1.0
L56@ while ((v516_Z <= 2.0)) {
val v517_H: Double = ((v454_I + v457_L) + ((v516_Z * v456_B) * 0.095))
v436_d.beginPath()
v436_d.moveTo((v453_P + (v455_U_ * 0.17)), v517_H)
v436_d.lineTo((v453_P + (v455_U_ * 0.96)), v517_H)
v436_d.stroke()
v516_Z++
}
}
v436_d.restore()
val v458_G: Double = (v453_P + (v455_U_ * 0.03))
val v459_U: Double = ((v454_I + v457_L) + (v456_B * 0.035))
val v460_V: Double = (v456_B * 0.19)
v436_d.fillStyle = (if (v439_S) v440_v else "#ff3fa4")
v442_b(v458_G, v459_U, v460_V, v460_V, (v460_V * 0.18))
v436_d.fill()
v436_d.fillStyle = "#fff"
v436_d.font = ("900 " + jsStr(jsRound((v460_V * 0.78))) + "px \"Arial Black\", Arial, sans-serif")
v436_d.textAlign = "center"
v436_d.textBaseline = "middle"
v436_d.fillText("A", (v458_G + (v460_V / 2.0)), (v459_U + (v460_V * 0.54)))
val v461_D: String = jsOrSD(jsGet(Ext.v("TRACK_TITLES"), Ext.v("currentTrackIndex"))) { "VAPOR_MASTER" }
v436_d.textAlign = "left"
v436_d.textBaseline = "alphabetic"
v436_d.fillStyle = (if (v439_S) "#0f1a24" else "#2a0e38")
v436_d.font = ("700 " + jsStr(jsMax(9.0, jsRound((v456_B * 0.105)))) + "px \"Courier New\", monospace")
val v462_K: Double = (v455_U_ * 0.76)
var v463_S_: Any? = v461_D
L57@ while (((jsLen(v463_S_) > 3.0) && (num(jsGet(v436_d.measureText(str(v463_S_)), "width")) > v462_K))) {
v463_S_ = jsSliceArr(v463_S_, 0.0, -1.0)

}
v436_d.fillText((str(v463_S_) + (if (!jsEq(v463_S_, v461_D)) "…" else "")), (v453_P + (v455_U_ * 0.18)), (v459_U + (v460_V * 0.62)))
val v464_z: String = jsOrSD(jsGet(jsObj("type1" to "TYPE I · NORMAL", "type2" to "TYPE II · CrO2", "type4" to "TYPE IV · METAL"), g_currentTapeType)) { g_currentTapeType.uppercase() }
v436_d.font = ("700 " + jsStr(jsMax(8.0, jsRound((v456_B * 0.07)))) + "px Tahoma, Arial, sans-serif")
v436_d.fillStyle = (if (v439_S) "rgba(15,26,36,.7)" else "rgba(42,14,56,.65)")
v436_d.fillText(("C-90 · " + v464_z), (v453_P + (v455_U_ * 0.18)), ((v454_I + v457_L) + (v456_B * 0.235)))
val v465_X: Double = (v455_U_ * 0.62)
val v466_q: Double = (v456_B * 0.44)
val v467_Q: Double = (v449_k - (v465_X / 2.0))
val v468_W: Double = (((v454_I + v456_B) - v466_q) - (v456_B * 0.07))
v442_b(v467_Q, v468_W, v465_X, v466_q, (v466_q * 0.22))
val v469_te: Grad = v436_d.createLinearGradient(0.0, v468_W, 0.0, (v468_W + v466_q))
v469_te.addColorStop(0.0, (if (v439_S) "#05080b" else "#2b2730"))
v469_te.addColorStop(1.0, (if (v439_S) "#141c22" else "#4a4452"))
v436_d.fillStyle = v469_te
v436_d.fill()
v436_d.strokeStyle = (if (v439_S) "rgba(255,255,255,.25)" else "#2a2630")
v436_d.lineWidth = jsMax(1.0, (v443_h * 0.3))
v436_d.stroke()
val v470_le: Double = (v468_W + (v466_q * 0.5))
val v471_ie: Double = (v449_k - (v465_X * 0.27))
val v472_J: Double = (v449_k + (v465_X * 0.27))
val v473_de: Double = jsMin((v465_X * 0.24), (v466_q * 0.62))
val v474_ee: Double = (v473_de * 0.42)
val v475_se: Double = (v474_ee + ((1.0 - g_cassetteTapeProgress) * (v473_de - v474_ee)))
val v476_x: Double = (v474_ee + (g_cassetteTapeProgress * (v473_de - v474_ee)))
v436_d.save()
v442_b(v467_Q, v468_W, v465_X, v466_q, (v466_q * 0.22))
v436_d.clip()
js_forEach(jsArr(jsArr(v471_ie, v475_se), jsArr(v472_J, v476_x))) L58@{ p59: Any?, _, _ ->
var v518_Z: Any? = jsGet(p59, 0.0)
var v519_H: Any? = jsGet(p59, 1.0)
val v520_fe: Grad = v436_d.createRadialGradient(num(v518_Z), v470_le, v474_ee, num(v518_Z), v470_le, num(v519_H))
v520_fe.addColorStop(0.0, "#2a160d")
v520_fe.addColorStop(0.85, "#5a3420")
v520_fe.addColorStop(1.0, "#3a2014")
v436_d.fillStyle = v520_fe
v436_d.beginPath()
v436_d.arc(num(v518_Z), v470_le, num(v519_H), 0.0, 7.0)
v436_d.fill()
v436_d.strokeStyle = "rgba(255,220,180,.10)"
v436_d.lineWidth = 1.0
run {
var v521_we: Double = (v474_ee + 2.0)
L60@ while ((v521_we < num(v519_H))) {
v436_d.beginPath()
v436_d.arc(num(v518_Z), v470_le, v521_we, 0.0, 7.0)
v436_d.stroke()
v521_we += jsMax(2.0, (v443_h * 0.7))
}
}
null
}
v436_d.strokeStyle = "#3a2014"
v436_d.lineWidth = jsMax(1.5, (v443_h * 0.5))
v436_d.beginPath()
v436_d.moveTo(v471_ie, (v470_le + v475_se))
v436_d.lineTo(v472_J, (v470_le + v476_x))
v436_d.stroke()
val v477_N: Grad = v436_d.createLinearGradient(v467_Q, v468_W, (v467_Q + v465_X), (v468_W + v466_q))
v477_N.addColorStop(0.0, "rgba(255,255,255,.18)")
v477_N.addColorStop(0.35, "rgba(255,255,255,.03)")
v477_N.addColorStop(0.6, "rgba(255,255,255,0)")
v477_N.addColorStop(0.8, "rgba(255,255,255,.08)")
v436_d.fillStyle = v477_N
v436_d.fillRect(v467_Q, v468_W, v465_X, v466_q)
v436_d.restore()
js_forEach(jsArr(v471_ie, v472_J)) L61@{ v522_Z: Any?, _, _ ->

v436_d.fillStyle = (if (v439_S) "#c9d4de" else "#f4f4f6")
v436_d.beginPath()
v436_d.arc(num(v522_Z), v470_le, v474_ee, 0.0, 7.0)
v436_d.fill()
v436_d.strokeStyle = "rgba(0,0,0,.45)"
v436_d.lineWidth = 1.0
v436_d.stroke()
v436_d.save()
v436_d.translate(num(v522_Z), v470_le)
v436_d.rotate(g_cassetteReelAngle)
v436_d.fillStyle = (if (v439_S) "#1a2229" else "#3a3a44")
v436_d.beginPath()
v436_d.arc(0.0, 0.0, (v474_ee * 0.55), 0.0, 7.0)
v436_d.fill()
v436_d.fillStyle = (if (v439_S) "#c9d4de" else "#f4f4f6")
run {
var v523_H: Double = 0.0
L62@ while ((v523_H < 6.0)) {
v436_d.save()
v436_d.rotate(((v523_H * Math.PI) / 3.0))
v436_d.fillRect((-v474_ee * 0.09), (-v474_ee * 0.6), (v474_ee * 0.18), (v474_ee * 0.26))
v436_d.restore()
v523_H++
}
}
v436_d.restore()
null
}
v436_d.fillStyle = (if (v439_S) "rgba(15,26,36,.45)" else "rgba(42,14,56,.4)")
run {
var v524_Z: Double = 0.0
L63@ while ((v524_Z <= 10.0)) {
val v525_H: Double = ((v467_Q + (v465_X * 0.33)) + (((v524_Z / 10.0) * v465_X) * 0.34))
v436_d.fillRect(v525_H, (v468_W + (v466_q * 0.42)), 1.0, (if (((v524_Z % 5.0) == 0.0)) (v466_q * 0.16) else (v466_q * 0.09)))
v524_Z++
}
}
val v478_ae: Double = (v445_w * 0.6)
val v479_j: Double = (v445_w * 0.72)
val v480_oe: Double = (v448_E + (v446_A * 0.76))
val v481_Y: Double = (v448_E + v446_A)
v436_d.beginPath()
v436_d.moveTo((v449_k - (v478_ae / 2.0)), v480_oe)
v436_d.lineTo((v449_k + (v478_ae / 2.0)), v480_oe)
v436_d.lineTo((v449_k + (v479_j / 2.0)), v481_Y)
v436_d.lineTo((v449_k - (v479_j / 2.0)), v481_Y)
v436_d.closePath()
v436_d.fillStyle = (if (v439_S) "rgba(14,18,22,.95)" else "#b5b8c1")
v436_d.fill()
v436_d.strokeStyle = (if (v439_S) "rgba(255,255,255,.12)" else "#7c7f88")
v436_d.lineWidth = jsMax(1.0, (v443_h * 0.25))
v436_d.stroke()
val v482_ne: Double = (v480_oe + ((v481_Y - v480_oe) * 0.52))
val v483_he: Double = jsMax(2.0, (v446_A * 0.03))
js_forEach(jsArr(-0.24, 0.24)) L64@{ v526_Z: Any?, _, _ ->

v436_d.fillStyle = "#16141a"
v436_d.beginPath()
v436_d.arc((v449_k + (v479_j * num(v526_Z))), v482_ne, v483_he, 0.0, 7.0)
v436_d.fill()
null
}
js_forEach(jsArr(-0.38, 0.38)) L65@{ v527_Z: Any?, _, _ ->

v436_d.fillStyle = "#16141a"
v442_b(((v449_k + (v479_j * num(v527_Z))) - v483_he), (v482_ne - (v483_he * 0.8)), (v483_he * 2.0), (v483_he * 1.6), (v483_he * 0.4))
v436_d.fill()
null
}
val v484_ve: Double = (if (((g_cassetteTransportMode == "ff") || (g_cassetteTransportMode == "rew"))) ((kotlin.math.sin((v422_e * 40.0)) * v443_h) * 0.5) else (((kotlin.math.sin((v422_e * 9.0)) * v424_o) * v443_h) * 0.6))
v436_d.strokeStyle = "#4a2a18"
v436_d.lineWidth = jsMax(1.5, (v443_h * 0.55))
v436_d.beginPath()
v436_d.moveTo((v449_k - (v479_j * 0.46)), (v481_Y - (v443_h * 0.7)))
v436_d.quadraticCurveTo(v449_k, ((v481_Y - (v443_h * 0.7)) + v484_ve), (v449_k + (v479_j * 0.46)), (v481_Y - (v443_h * 0.7)))
v436_d.stroke()
val v485_Ce: Double = jsMin(1.0, (v424_o * 1.4))
v436_d.fillStyle = "#16141a"
v442_b((v449_k - (v445_w * 0.06)), (v481_Y - (v446_A * 0.07)), (v445_w * 0.12), (v446_A * 0.06), 2.0)
v436_d.fill()
if ((((g_cassetteTransportMode == "play") || (g_cassetteTransportMode == "ff")) || (g_cassetteTransportMode == "rew"))) {
v436_d.globalAlpha = (0.35 + (v485_Ce * 0.5))
v436_d.fillStyle = (if (v439_S) v440_v else jsOrSD(jsGet(v427_l, "accent")) { "#ff7aa8" })
v442_b((v449_k - (v445_w * 0.05)), (v481_Y - (v446_A * 0.06)), ((v445_w * 0.1) * (0.3 + (v485_Ce * 0.7))), (v446_A * 0.04), 2.0)
v436_d.fill()
v436_d.globalAlpha = 1.0
}
v436_d.font = ("700 " + jsStr(jsMax(8.0, jsRound((v446_A * 0.05)))) + "px Tahoma, Arial, sans-serif")
v436_d.fillStyle = (if (v439_S) "rgba(220,232,242,.75)" else "rgba(40,40,52,.75)")
val v486_ye: String = (if (g_cassetteScratchActive) "SCRATCHING" else g_cassetteTransportMode.uppercase())
v436_d.fillText(v486_ye, (v447_C + (v445_w * 0.08)), (v448_E + (v446_A * 0.88)))
}

var g_NEW_VISUALIZER_TARGETS: Any? = null

fun renderNewVisualizers(v529_e: Double, v530_a: Double, v531_o: Double, v532_r: Double, v533_t: Any?): Unit {

val v534_l: Double = (Math.PI * 2.0)
fun v535_u(v537_M: Double): Double {

return (jsOrN(Rt.freqDataArr[jsIdx((v537_M % Rt.freqDataArr.size.toDouble()))]) { 0.0 } / 255.0)
}
fun v536_f(v538_M: Double): Double {

return ((jsOrN(Rt.timeDataArr[jsIdx((v538_M % Rt.timeDataArr.size.toDouble()))]) { 128.0 } - 128.0) / 128.0)
}
js_forEach(g_NEW_VISUALIZER_TARGETS) L67@{ p68: Any?, _, _ ->
var v542_S: Any? = null
var v539_M: Any? = jsGet(p68, "kind")
var v540_n: Any? = jsGet(p68, "canvas")
var v541_s: Any? = jsGet(p68, "win")
if (truthy(jsOr(jsOr((((!(truthy(v540_n)) || !(truthy(v541_s))) || (num(jsGet(v540_n, "width")) < 2.0)) || (num(jsGet(v540_n, "height")) < 2.0))) { jsInvoke(jsGet(v541_s, "classList"), "contains", "minimized") }) { Ext.call("rrWindowHidden", v541_s) })) {
return@L67 null
}
val v543_i: Ctx = jsGetContext(v540_n)
if (!(truthy(v543_i))) {
return@L67 null
}
val v544_g: Any? = jsGet(v540_n, "width")
val v545_m: Any? = jsGet(v540_n, "height")
val v546_d: Double = jsMin(num(v544_g), num(v545_m))
val v547_y: Double = (num(v544_g) / 2.0)
val v548_c: Double = (num(v545_m) / 2.0)
if ((((!(jsNumberFn_isFinite(v544_g)) || !(jsNumberFn_isFinite(v545_m))) || (num(v544_g) < 2.0)) || (num(v545_m) < 2.0))) {
return@L67 null
}
v543_i.fillStyle = (if (jsEq(v539_M, "sonar")) "rgba(2, 10, 14, 0.30)" else "rgba(4, 2, 12, 0.34)")
v543_i.fillRect(0.0, 0.0, num(v544_g), num(v545_m))
if (jsEq(v539_M, "orbital")) {
val v549_v: Double = (v546_d * (0.07 + (v530_a * 0.075)))
val v550_T: Grad = v543_i.createRadialGradient(v547_y, v548_c, (v549_v * 0.2), v547_y, v548_c, (v549_v * 3.2))
v550_T.addColorStop(0.0, "#ffffff")
v550_T.addColorStop(0.35, jsGet(v533_t, "accent"))
v550_T.addColorStop(0.7, ("rgba(" + str(jsGet(v533_t, "altWaveRgb")) + ", " + jsStr((0.35 + (v530_a * 0.4))) + ")"))
v550_T.addColorStop(1.0, "transparent")
v543_i.fillStyle = v550_T
v543_i.beginPath()
v543_i.arc(v547_y, v548_c, (v549_v * 3.2), 0.0, v534_l)
v543_i.fill()
v543_i.strokeStyle = jsGet(v533_t, "accent")
v543_i.lineWidth = 1.3
run {
var v552_h: Double = 0.0
L69@ while ((v552_h < 24.0)) {
val v553_p: Double = (((v552_h / 24.0) * v534_l) + (v529_e * 0.6))
val v554_w: Double = (v549_v * (1.1 + (v535_u((v552_h * 3.0)) * 1.4)))
v543_i.beginPath()
v543_i.moveTo((v547_y + (kotlin.math.cos(v553_p) * v549_v)), (v548_c + (kotlin.math.sin(v553_p) * v549_v)))
v543_i.lineTo((v547_y + (kotlin.math.cos(v553_p) * v554_w)), (v548_c + (kotlin.math.sin(v553_p) * v554_w)))
v543_i.stroke()
v552_h++
}
}
val v551_b: Any? = jsArr()
run {
var v555_h: Double = 0.0
L70@ while ((v555_h < 6.0)) {
val v556_p: Double = v535_u(((v555_h * 10.0) + 2.0))
val v557_w: Double = ((v546_d * (0.13 + (v555_h * 0.058))) * (1.0 + (v530_a * 0.08)))
val v558_A: Double = (v557_w * (0.34 + ((v555_h % 2.0) * 0.08)))
val v559_C: Double = (((v555_h - 2.5) * 0.24) + (kotlin.math.sin(((v529_e * 0.3) + v555_h)) * 0.08))
val v560_E: Double = kotlin.math.cos(v559_C)
val v561_k: Double = kotlin.math.sin(v559_C)
v543_i.strokeStyle = (if (truthy((v555_h % 2.0))) jsGet(v533_t, "secondary") else jsGet(v533_t, "primary"))
v543_i.globalAlpha = (0.35 + (v556_p * 0.45))
v543_i.lineWidth = (1.0 + (v556_p * 1.5))
v543_i.beginPath()
run {
var v568_B: Double = 0.0
L71@ while ((v568_B <= 48.0)) {
val v569_L: Double = ((v568_B / 48.0) * v534_l)
val v570_G: Double = ((kotlin.math.cos(v569_L) * v557_w) * (1.0 + (v536_f((v568_B * 2.0)) * 0.06)))
val v571_U: Double = ((kotlin.math.sin(v569_L) * v558_A) * (1.0 + (v536_f((v568_B * 2.0)) * 0.06)))
val v572_V: Double = ((v547_y + (v570_G * v560_E)) - (v571_U * v561_k))
val v573_D: Double = ((v548_c + (v570_G * v561_k)) + (v571_U * v560_E))
if ((v568_B == 0.0)) {
v543_i.moveTo(v572_V, v573_D)
} else {
v543_i.lineTo(v572_V, v573_D)
}
v568_B++
}
}
v543_i.stroke()
val v562_R: Double = (((v529_e * (0.55 + ((5.0 - v555_h) * 0.14))) + (v555_h * 1.3)) + (v530_a * 0.35))
val v563_F: Double = (kotlin.math.cos(v562_R) * v557_w)
val v564_O: Double = (kotlin.math.sin(v562_R) * v558_A)
val v565_P: Double = ((v547_y + (v563_F * v560_E)) - (v564_O * v561_k))
val v566_I: Double = ((v548_c + (v563_F * v561_k)) + (v564_O * v560_E))
val v567_U_: Double = kotlin.math.sin(v562_R)
jsPush(v551_b, jsObj("x" to v565_P, "y" to v566_I, "z" to v567_U_, "r" to (((3.2 + (v555_h * 0.7)) * (1.0 + (v556_p * 0.9))) * (0.85 + ((v567_U_ + 1.0) * 0.2))), "col" to jsGet(jsArr(jsGet(v533_t, "accent"), jsGet(v533_t, "primary"), jsGet(v533_t, "secondary")), (v555_h % 3.0)), "energy" to v556_p, "hasRing" to ((v555_h == 3.0) || (v555_h == 5.0)), "moonAng" to (v529_e * (2.4 + (v555_h * 0.5)))))
v555_h++
}
}
v543_i.globalAlpha = 1.0
js_sort(v551_b) L72@{ v574_h: Any?, v575_p: Any? ->

(num(jsGet(v574_h, "z")) - num(jsGet(v575_p, "z")))
}
js_forEach(v551_b) L73@{ v576_h: Any?, _, _ ->

v543_i.strokeStyle = jsGet(v576_h, "col")
v543_i.globalAlpha = 0.28
v543_i.lineWidth = 1.0
v543_i.beginPath()
v543_i.moveTo(v547_y, v548_c)
v543_i.lineTo(num(jsGet(v576_h, "x")), num(jsGet(v576_h, "y")))
v543_i.stroke()
v543_i.globalAlpha = 1.0
if (truthy(jsGet(v576_h, "hasRing"))) {
v543_i.strokeStyle = jsGet(v533_t, "accent")
v543_i.lineWidth = 1.4
v543_i.beginPath()
v543_i.ellipse(num(jsGet(v576_h, "x")), num(jsGet(v576_h, "y")), (num(jsGet(v576_h, "r")) * 2.2), (num(jsGet(v576_h, "r")) * 0.7), 0.35, 0.0, v534_l)
v543_i.stroke()
}
v543_i.fillStyle = jsGet(v576_h, "col")
v543_i.shadowColor = jsGet(v576_h, "col")
v543_i.shadowBlur = (8.0 + (num(jsGet(v576_h, "energy")) * 14.0))
v543_i.beginPath()
v543_i.arc(num(jsGet(v576_h, "x")), num(jsGet(v576_h, "y")), num(jsGet(v576_h, "r")), 0.0, v534_l)
v543_i.fill()
val v577_p: Double = (num(jsGet(v576_h, "x")) + (kotlin.math.cos(num(jsGet(v576_h, "moonAng"))) * (num(jsGet(v576_h, "r")) * 2.1)))
val v578_w: Double = (num(jsGet(v576_h, "y")) + (kotlin.math.sin(num(jsGet(v576_h, "moonAng"))) * (num(jsGet(v576_h, "r")) * 1.1)))
v543_i.fillStyle = "#ffffff"
v543_i.beginPath()
v543_i.arc(v577_p, v578_w, jsMax(1.5, (num(jsGet(v576_h, "r")) * 0.32)), 0.0, v534_l)
v543_i.fill()
v543_i.shadowBlur = 0.0
null
}
} else {
if (jsEq(v539_M, "fractal")) {
var v579_h: Any? = null
var v580_v: Double = 0.0
var v581_T: Double = 0.0
var v582_b: Double = 0.0
v579_h = JsFn L74@{ args ->
var v583_p: Any? = args.getOrNull(0)
var v584_w: Any? = args.getOrNull(1)
var v585_A: Any? = args.getOrNull(2)
var v586_C: Any? = args.getOrNull(3)
var v587_E: Any? = args.getOrNull(4)
var v588_k: Any? = args.getOrNull(5)
if (((num(v587_E) <= 0.0) || (num(v585_A) < 2.5))) {
return@L74 null
}
val v589_R: Double = v535_u(((num(v587_E) * 9.0) + (num(v588_k) * 3.0)))
val v590_F: Double = (num(v585_A) * (0.85 + (v589_R * 0.45)))
val v591_O: Double = (num(v583_p) + (kotlin.math.cos(num(v586_C)) * v590_F))
val v592_P: Double = (num(v584_w) + (kotlin.math.sin(num(v586_C)) * v590_F))
v543_i.strokeStyle = (if ((num(v587_E) > 4.0)) jsGet(v533_t, "primary") else (if ((num(v587_E) > 2.0)) jsGet(v533_t, "secondary") else jsGet(v533_t, "accent")))
v543_i.lineWidth = jsMax(0.8, ((num(v587_E) * 0.75) * (1.0 + (v530_a * 0.35))))
v543_i.globalAlpha = (0.45 + ((num(v587_E) / v580_v) * 0.55))
v543_i.beginPath()
v543_i.moveTo(num(v583_p), num(v584_w))
v543_i.lineTo(v591_O, v592_P)
v543_i.stroke()
if ((num(v587_E) <= 2.0)) {
val v593_I: Double = (1.8 + ((v532_r + v589_R) * 5.5))
v543_i.fillStyle = (if (jsEq(v587_E, 1.0)) "#ffffff" else jsGet(v533_t, "accent"))
v543_i.beginPath()
v543_i.arc(v591_O, v592_P, v593_I, 0.0, v534_l)
v543_i.fill()
}
jsCall(v579_h, v591_O, v592_P, (num(v585_A) * 0.72), ((num(v586_C) - v582_b) + (v581_T * 0.5)), (num(v587_E) - 1.0), (num(v588_k) * 2.0))
jsCall(v579_h, v591_O, v592_P, (num(v585_A) * 0.72), ((num(v586_C) + v582_b) + (v581_T * 0.5)), (num(v587_E) - 1.0), ((num(v588_k) * 2.0) + 1.0))
null
}
v542_S = v579_h
v580_v = 7.0
v581_T = ((kotlin.math.sin((v529_e * 1.4)) * 0.12) + ((v531_o - 0.3) * 0.25))
v582_b = (0.38 + (v530_a * 0.42))
jsCall(v579_h, v547_y, (num(v545_m) * 0.95), (v546_d * (0.22 + (v530_a * 0.1))), (-Math.PI / 2.0), v580_v, 1.0)
v543_i.globalAlpha = 1.0
v543_i.strokeStyle = jsGet(v533_t, "primary")
v543_i.lineWidth = 1.6
v543_i.beginPath()
run {
var v594_p: Double = 0.0
L75@ while ((v594_p < 48.0)) {
val v595_w: Double = ((v594_p / 47.0) * num(v544_g))
val v596_A: Double = ((num(v545_m) * 0.95) + (v536_f((v594_p * 3.0)) * (8.0 + (v530_a * 18.0))))
if ((v594_p == 0.0)) {
v543_i.moveTo(v595_w, v596_A)
} else {
v543_i.lineTo(v595_w, v596_A)
}
v594_p++
}
}
v543_i.stroke()
} else {
if (jsEq(v539_M, "prism")) {
val v597_v: Double = ((v529_e * 0.95) + (v530_a * 0.8))
val v598_T: Double = (0.35 + (kotlin.math.sin((v529_e * 0.7)) * 0.25))
val v599_b: Double = (v546_d * (0.24 + (v530_a * 0.12)))
val v600_h: Any? = jsArr(jsArr(0.0, -1.25, 0.0), jsArr(0.0, 1.25, 0.0), jsArr(-1.0, 0.0, -1.0), jsArr(1.0, 0.0, -1.0), jsArr(1.0, 0.0, 1.0), jsArr(-1.0, 0.0, 1.0))
val v601_p: Any? = js_map(v600_h) L76@{ p77: Any?, _, _ ->
var v604_C: Any? = jsGet(p77, 0.0)
var v605_E: Any? = jsGet(p77, 1.0)
var v606_k: Any? = jsGet(p77, 2.0)
val v607_R: Double = ((num(v604_C) * kotlin.math.cos(v597_v)) - (num(v606_k) * kotlin.math.sin(v597_v)))
val v608_F: Double = ((num(v604_C) * kotlin.math.sin(v597_v)) + (num(v606_k) * kotlin.math.cos(v597_v)))
val v609_O: Double = ((num(v605_E) * kotlin.math.cos(v598_T)) - (v608_F * kotlin.math.sin(v598_T)))
val v610_P: Double = ((num(v605_E) * kotlin.math.sin(v598_T)) + (v608_F * kotlin.math.cos(v598_T)))
val v611_I: Double = (3.2 / (3.2 + (v610_P * 0.45)))
return@L76 jsObj("x" to (v547_y + ((v607_R * v599_b) * v611_I)), "y" to (v548_c + ((v609_O * v599_b) * v611_I)), "z" to v610_P)
null
}
v543_i.strokeStyle = "#ffffff"
v543_i.lineWidth = (2.4 + (v530_a * 3.0))
v543_i.shadowColor = "#ffffff"
v543_i.shadowBlur = (10.0 + (v530_a * 12.0))
v543_i.beginPath()
run {
var v612_C: Double = 0.0
L78@ while ((v612_C <= 24.0)) {
val v613_E: Double = (v612_C / 24.0)
val v614_k: Double = (v613_E * (v547_y - (v599_b * 0.45)))
val v615_R: Double = (((v548_c - (v546_d * 0.06)) + (((1.0 - v613_E) * v546_d) * 0.08)) + ((v536_f((v612_C * 4.0)) * (10.0 + (v531_o * 22.0))) * kotlin.math.sin((v613_E * Math.PI))))
if ((v612_C == 0.0)) {
v543_i.moveTo(v614_k, v615_R)
} else {
v543_i.lineTo(v614_k, v615_R)
}
v612_C++
}
}
v543_i.stroke()
v543_i.shadowBlur = 0.0
val v602_w: Any? = jsArr("#ff1744", "#ff9100", "#ffea00", "#00e676", "#00e5ff", "#2979ff", "#d500f9")
js_forEach(v602_w) L79@{ v616_C: Any?, v617_E: Double, _ ->

val v618_k: Double = v535_u(((v617_E * 12.0) + 4.0))
val v619_R: Double = ((v617_E - 3.0) * (v546_d * (0.045 + (v530_a * 0.035))))
val v620_F: Double = (v547_y + (v599_b * 0.35))
val v621_O: Double = (v548_c + ((v617_E - 3.0) * 2.5))
v543_i.strokeStyle = v616_C
v543_i.lineWidth = (1.8 + (v618_k * 3.5))
v543_i.globalAlpha = (0.65 + (v618_k * 0.35))
v543_i.beginPath()
run {
var v622_P: Double = 0.0
L80@ while ((v622_P <= 28.0)) {
val v623_I: Double = (v622_P / 28.0)
val v624_U_: Double = (v620_F + (v623_I * (num(v544_g) - v620_F)))
val v625_B: Double = ((v621_O + (v623_I * v619_R)) + (kotlin.math.sin((((v623_I * 10.0) - (v529_e * 8.0)) + v617_E)) * ((v618_k * 18.0) * v623_I)))
if ((v622_P == 0.0)) {
v543_i.moveTo(v624_U_, v625_B)
} else {
v543_i.lineTo(v624_U_, v625_B)
}
v622_P++
}
}
v543_i.stroke()
null
}
v543_i.globalAlpha = 1.0
val v603_A: Any? = jsArr(jsArr(0.0, 2.0, 3.0), jsArr(0.0, 3.0, 4.0), jsArr(0.0, 4.0, 5.0), jsArr(0.0, 5.0, 2.0), jsArr(1.0, 2.0, 3.0), jsArr(1.0, 3.0, 4.0), jsArr(1.0, 4.0, 5.0), jsArr(1.0, 5.0, 2.0))
js_forEach(v603_A) L81@{ v626_C: Any?, v627_E: Double, _ ->

v543_i.fillStyle = (if (((v627_E % 2.0) == 0.0)) ("rgba(" + str(jsGet(v533_t, "waveRgb")) + ", " + jsStr((0.12 + (v531_o * 0.18))) + ")") else ("rgba(" + str(jsGet(v533_t, "altWaveRgb")) + ", " + jsStr((0.12 + (v530_a * 0.18))) + ")"))
v543_i.strokeStyle = (if ((v627_E < 4.0)) jsGet(v533_t, "primary") else jsGet(v533_t, "secondary"))
v543_i.lineWidth = 1.6
v543_i.beginPath()
v543_i.moveTo(num(jsGet(jsGet(v601_p, jsGet(v626_C, 0.0)), "x")), num(jsGet(jsGet(v601_p, jsGet(v626_C, 0.0)), "y")))
v543_i.lineTo(num(jsGet(jsGet(v601_p, jsGet(v626_C, 1.0)), "x")), num(jsGet(jsGet(v601_p, jsGet(v626_C, 1.0)), "y")))
v543_i.lineTo(num(jsGet(jsGet(v601_p, jsGet(v626_C, 2.0)), "x")), num(jsGet(jsGet(v601_p, jsGet(v626_C, 2.0)), "y")))
v543_i.closePath()
v543_i.fill()
v543_i.stroke()
null
}
} else {
if (jsEq(v539_M, "sonar")) {
val v628_v: Double = (v546_d * 0.44)
val v629_T: Double = ((v529_e * (1.6 + (v530_a * 1.2))) % v534_l)
v543_i.strokeStyle = jsGet(v533_t, "primary")
v543_i.lineWidth = 0.9
run {
var v631_h: Double = 1.0
L82@ while ((v631_h <= 4.0)) {
v543_i.globalAlpha = (0.25 + (if ((v631_h == 4.0)) 0.25 else 0.0))
v543_i.beginPath()
v543_i.arc(v547_y, v548_c, ((v628_v * v631_h) / 4.0), 0.0, v534_l)
v543_i.stroke()
v631_h++
}
}
v543_i.globalAlpha = 0.25
v543_i.beginPath()
v543_i.moveTo((v547_y - v628_v), v548_c)
v543_i.lineTo((v547_y + v628_v), v548_c)
v543_i.moveTo(v547_y, (v548_c - v628_v))
v543_i.lineTo(v547_y, (v548_c + v628_v))
v543_i.stroke()
v543_i.strokeStyle = jsGet(v533_t, "secondary")
v543_i.lineWidth = 1.8
v543_i.globalAlpha = 0.85
v543_i.beginPath()
val v630_b: Double = 64.0
run {
var v632_h: Double = 0.0
L83@ while ((v632_h <= v630_b)) {
val v633_p: Double = ((v632_h / v630_b) * v534_l)
val v634_w: Double = (v628_v * (0.28 + (v535_u((v632_h * 2.0)) * 0.68)))
val v635_A: Double = (v547_y + (kotlin.math.cos(v633_p) * v634_w))
val v636_C: Double = (v548_c + (kotlin.math.sin(v633_p) * v634_w))
if ((v632_h == 0.0)) {
v543_i.moveTo(v635_A, v636_C)
} else {
v543_i.lineTo(v635_A, v636_C)
}
v632_h++
}
}
v543_i.closePath()
v543_i.stroke()
run {
var v637_h: Double = 0.0
L84@ while ((v637_h < 26.0)) {
val v638_p: Double = (v629_T - (v637_h * 0.045))
v543_i.fillStyle = jsGet(v533_t, "primary")
v543_i.globalAlpha = ((1.0 - (v637_h / 26.0)) * 0.09)
v543_i.beginPath()
v543_i.moveTo(v547_y, v548_c)
v543_i.arc(v547_y, v548_c, v628_v, (v638_p - 0.05), v638_p)
v543_i.closePath()
v543_i.fill()
v637_h++
}
}
v543_i.globalAlpha = 1.0
v543_i.strokeStyle = jsGet(v533_t, "accent")
v543_i.lineWidth = 2.2
v543_i.beginPath()
v543_i.moveTo(v547_y, v548_c)
v543_i.lineTo((v547_y + (kotlin.math.cos(v629_T) * v628_v)), (v548_c + (kotlin.math.sin(v629_T) * v628_v)))
v543_i.stroke()
run {
var v639_h: Double = 0.0
L85@ while ((v639_h < 12.0)) {
val v640_p: Double = ((v639_h * 2.39996) % v534_l)
val v641_w: Double = v535_u(((v639_h * 6.0) + 3.0))
val v642_A: Double = (v628_v * (0.2 + (((v639_h * 29.0) % 70.0) / 100.0)))
val v643_C: Double = (v547_y + (kotlin.math.cos(v640_p) * v642_A))
val v644_E: Double = (v548_c + (kotlin.math.sin(v640_p) * v642_A))
val v645_k: Double = ((((v629_T - v640_p) % v534_l) + v534_l) % v534_l)
val v646_R: Double = (jsMax(0.0, (1.0 - (v645_k / 1.8))) * (0.5 + (v641_w * 0.9)))
if ((v646_R > 0.08)) {
v543_i.fillStyle = jsGet(v533_t, "accent")
v543_i.globalAlpha = jsMin(1.0, v646_R)
v543_i.beginPath()
v543_i.arc(v643_C, v644_E, (2.0 + (v641_w * 4.5)), 0.0, v534_l)
v543_i.fill()
v543_i.strokeStyle = jsGet(v533_t, "primary")
v543_i.lineWidth = 1.0
v543_i.beginPath()
v543_i.arc(v643_C, v644_E, (((1.0 - v646_R) * 16.0) + 3.0), 0.0, v534_l)
v543_i.stroke()
}
v639_h++
}
}
v543_i.globalAlpha = 1.0
} else {
if (jsEq(v539_M, "magnetic")) {
val v647_v: Double = (v546_d * (0.24 + (v530_a * 0.06)))
val v648_T: Double = (v547_y - v647_v)
val v649_b: Double = (v547_y + v647_v)
val v650_h: Double = 16.0
run {
var v651_p: Double = 0.0
L86@ while ((v651_p < v650_h)) {
val v652_w: Double = (if (((v651_p % 2.0) == 0.0)) 1.0 else -1.0)
val v653_A: Double = (kotlin.math.floor((v651_p / 2.0)) + 1.0)
val v654_C: Double = v535_u((v653_A * 8.0))
val v655_E: Double = (((v546_d * (0.06 + (v653_A * 0.065))) * (1.0 + (v654_C * 0.65))) * v652_w)
v543_i.strokeStyle = (if (((v651_p % 2.0) == 0.0)) jsGet(v533_t, "primary") else jsGet(v533_t, "secondary"))
v543_i.lineWidth = (1.1 + (v654_C * 2.0))
v543_i.globalAlpha = (0.35 + (v654_C * 0.55))
v543_i.beginPath()
run {
var v660_P: Double = 0.0
L87@ while ((v660_P <= 40.0)) {
val v661_I: Double = (v660_P / 40.0)
val v662_U_: Double = kotlin.math.sin((v661_I * Math.PI))
val v663_B: Double = ((v648_T + ((v649_b - v648_T) * v661_I)) + (kotlin.math.sin((v661_I * v534_l)) * ((v546_d * 0.06) * (v653_A / 8.0))))
val v664_L: Double = ((v536_f(((v660_P * 2.0) + (v651_p * 3.0))) * (14.0 * v654_C)) * v662_U_)
val v665_G: Double = ((v548_c + (v662_U_ * v655_E)) + v664_L)
if ((v660_P == 0.0)) {
v543_i.moveTo(v663_B, v665_G)
} else {
v543_i.lineTo(v663_B, v665_G)
}
v660_P++
}
}
v543_i.stroke()
val v656_k: Double = (((v529_e * ((0.35 + (v654_C * 0.8)) + (v653_A * 0.04))) + (v651_p * 0.13)) % 1.0)
val v657_R: Double = kotlin.math.sin((v656_k * Math.PI))
val v658_F: Double = ((v648_T + ((v649_b - v648_T) * v656_k)) + (kotlin.math.sin((v656_k * v534_l)) * ((v546_d * 0.06) * (v653_A / 8.0))))
val v659_O: Double = (v548_c + (v657_R * v655_E))
v543_i.globalAlpha = 0.95
v543_i.fillStyle = jsGet(v533_t, "accent")
v543_i.beginPath()
v543_i.arc(v658_F, v659_O, (2.0 + (v654_C * 3.5)), 0.0, v534_l)
v543_i.fill()
v651_p++
}
}
js_forEach(jsArr(jsArr(v648_T, jsGet(v533_t, "primary"), "N"), jsArr(v649_b, jsGet(v533_t, "secondary"), "S"))) L88@{ p89: Any?, _, _ ->
var v666_p: Any? = jsGet(p89, 0.0)
var v667_w: Any? = jsGet(p89, 1.0)
var v668_A: Any? = jsGet(p89, 2.0)
val v669_C: Double = (7.0 + (v530_a * 8.0))
v543_i.fillStyle = v667_w
v543_i.shadowColor = v667_w
v543_i.shadowBlur = 14.0
v543_i.beginPath()
v543_i.arc(num(v666_p), v548_c, v669_C, 0.0, v534_l)
v543_i.fill()
v543_i.shadowBlur = 0.0
v543_i.fillStyle = "#000"
v543_i.font = "bold 9px monospace"
v543_i.fillText(str(v668_A), (num(v666_p) - 3.0), (v548_c + 3.0))
null
}
v543_i.globalAlpha = 1.0
} else {
if (jsEq(v539_M, "kaleido")) {
val v670_v: Double = 12.0
v543_i.save()
v543_i.translate(v547_y, v548_c)
v543_i.rotate((v529_e * 0.25))
run {
var v671_T: Double = 0.0
L90@ while ((v671_T < v670_v)) {
v543_i.save()
v543_i.rotate(((v671_T * v534_l) / v670_v))
if (((v671_T % 2.0) == 1.0)) {
v543_i.scale(1.0, -1.0)
}
v543_i.beginPath()
run {
var v674_p: Double = 0.0
L91@ while ((v674_p < 16.0)) {
val v675_w: Double = v535_u((v674_p * 4.0))
val v676_A: Double = (((v674_p / 15.0) * (v546_d * 0.45)) * (0.4 + (v675_w * 0.75)))
val v677_C: Double = (((v674_p / 15.0) * (v534_l / v670_v)) * (0.8 + (v531_o * 0.8)))
val v678_E: Double = (kotlin.math.cos(v677_C) * v676_A)
val v679_k: Double = (kotlin.math.sin(v677_C) * v676_A)
if ((v674_p == 0.0)) {
v543_i.moveTo(v678_E, v679_k)
} else {
v543_i.lineTo(v678_E, v679_k)
}
v674_p++
}
}
v543_i.strokeStyle = (if (((v671_T % 3.0) == 0.0)) jsGet(v533_t, "accent") else (if (((v671_T % 2.0) == 0.0)) jsGet(v533_t, "primary") else jsGet(v533_t, "secondary")))
v543_i.lineWidth = (1.4 + (v530_a * 1.8))
v543_i.globalAlpha = 0.75
v543_i.stroke()
val v672_b: Double = (v546_d * (0.14 + (v530_a * 0.22)))
val v673_h: Double = (v546_d * (0.04 + (v532_r * 0.08)))
v543_i.fillStyle = (if (((v671_T % 2.0) == 0.0)) jsGet(v533_t, "primary") else jsGet(v533_t, "secondary"))
v543_i.globalAlpha = (0.16 + (v531_o * 0.22))
v543_i.beginPath()
v543_i.moveTo(0.0, 0.0)
v543_i.lineTo((v672_b * 0.6), v673_h)
v543_i.lineTo(v672_b, 0.0)
v543_i.closePath()
v543_i.fill()
v543_i.restore()
v671_T++
}
}
v543_i.restore()
v543_i.globalAlpha = 1.0
} else {
if (jsEq(v539_M, "starforge")) {
val v680_v: Double = (0.45 + (v530_a * 1.65))
run {
var v682_b: Double = 0.0
L92@ while ((v682_b < 42.0)) {
L92c@ do {
val v683_h: Double = (v682_b * 73.19)
val v684_p: Double = (1.0 - (((((v529_e * v680_v) + (v682_b / 42.0)) % 1.0) + 1.0) % 1.0))
val v685_w: Double = (1.0 / jsMax(0.08, (v684_p * 1.8)))
val v686_A: Double = v683_h
val v687_C: Double = (0.14 + (((v682_b * 19.0) % 75.0) / 100.0))
val v688_E: Double = (v547_y + (((kotlin.math.cos(v686_A) * v687_C) * (num(v544_g) * 0.36)) * v685_w))
val v689_k: Double = (v548_c + (((kotlin.math.sin(v686_A) * v687_C) * (num(v545_m) * 0.36)) * v685_w))
if (((((v688_E < -40.0) || (v688_E > (num(v544_g) + 40.0))) || (v689_k < -40.0)) || (v689_k > (num(v545_m) + 40.0)))) {
break@L92c
}
val v690_R: Double = (1.0 / jsMax(0.08, ((v684_p + (0.06 * (0.5 + v530_a))) * 1.8)))
val v691_F: Double = (v547_y + (((kotlin.math.cos(v686_A) * v687_C) * (num(v544_g) * 0.36)) * v690_R))
val v692_O: Double = (v548_c + (((kotlin.math.sin(v686_A) * v687_C) * (num(v545_m) * 0.36)) * v690_R))
v543_i.strokeStyle = (if (((v682_b % 2.0) == 0.0)) jsGet(v533_t, "primary") else jsGet(v533_t, "accent"))
v543_i.globalAlpha = jsMin(1.0, ((1.0 - v684_p) * 0.9))
v543_i.lineWidth = jsMax(0.7, ((1.0 - v684_p) * 2.2))
v543_i.beginPath()
v543_i.moveTo(v691_F, v692_O)
v543_i.lineTo(v688_E, v689_k)
v543_i.stroke()
if (((v682_b % 2.0) == 0.0)) {
val v693_P: Double = ((((1.0 - v684_p) * (v546_d * 0.065)) * (1.0 + (v535_u((v682_b * 3.0)) * 0.8))) + 1.5)
val v694_I: Double = (5.0 + (v682_b % 3.0))
val v695_U_: Double = ((v529_e * (1.2 + ((v682_b % 4.0) * 0.4))) + v683_h)
v543_i.fillStyle = "rgba(10, 4, 24, 0.85)"
v543_i.strokeStyle = (if (((v682_b % 4.0) == 0.0)) jsGet(v533_t, "accent") else jsGet(v533_t, "secondary"))
v543_i.beginPath()
run {
var v696_B: Double = 0.0
L93@ while ((v696_B < v694_I)) {
val v697_L: Double = (v695_U_ + ((v696_B / v694_I) * v534_l))
val v698_G: Double = (v693_P * (0.72 + (0.28 * kotlin.math.sin((v683_h + (v696_B * 2.3))))))
val v699_U: Double = (v688_E + (kotlin.math.cos(v697_L) * v698_G))
val v700_V: Double = (v689_k + (kotlin.math.sin(v697_L) * v698_G))
if ((v696_B == 0.0)) {
v543_i.moveTo(v699_U, v700_V)
} else {
v543_i.lineTo(v699_U, v700_V)
}
v696_B++
}
}
v543_i.closePath()
v543_i.fill()
v543_i.stroke()
}
} while (false)
v682_b++
}
}
v543_i.globalAlpha = 0.55
v543_i.strokeStyle = jsGet(v533_t, "primary")
v543_i.lineWidth = 1.2
val v681_T: Double = (v546_d * (0.11 + (v530_a * 0.05)))
v543_i.beginPath()
v543_i.arc(v547_y, v548_c, v681_T, 0.0, v534_l)
v543_i.stroke()
v543_i.globalAlpha = 1.0
}
}
}
}
}
}
}
v543_i.strokeStyle = jsGet(v533_t, "primary")
v543_i.lineWidth = 1.0
v543_i.globalAlpha = 0.45
v543_i.strokeRect(3.5, 3.5, (num(v544_g) - 7.0), (num(v545_m) - 7.0))
v543_i.globalAlpha = 1.0
null
}
}

fun rrVizFrame(v701_y: Double, v702_d: Double, v703_sb: Double, v704_sm: Double, v705_sh: Double, v706_cs: Double, v707_c: Any?): Unit {

renderHypercubeVisualizer(v701_y, v703_sb, v704_sm, v707_c)
renderWaterfallVisualizer(v703_sb, v707_c)
renderTunnelVisualizer(v701_y, v702_d, v703_sb, v704_sm, v707_c)
renderPlasmaVisualizer(v701_y, v703_sb, v705_sh, v707_c)
renderLissajousVisualizer(v701_y, v703_sb, v704_sm, v707_c)
renderCyberCanyonVisualizer(v701_y, v706_cs, v703_sb, v707_c)
renderMatrixVisualizer(v701_y, v703_sb, v705_sh, v707_c)
renderRibbonVisualizer(v701_y, v703_sb, v704_sm, v707_c)
renderNewVisualizers(v701_y, v703_sb, v704_sm, v705_sh, v707_c)
renderVuMeterVisualizer(v703_sb, v704_sm, v705_sh, v707_c)
renderEqVisualizer(v701_y, v703_sb, v707_c)
renderFxVisualizer(v701_y, v703_sb, v704_sm, v707_c)
renderCassetteVisualizer(v701_y, v702_d, v703_sb, v704_sm, v705_sh, v707_c)
}

fun rrVizTheme(v708_key: String): Any? {

return jsOr(jsGet(g_THEMES, v708_key)) { jsGet(g_THEMES, "vaporwave") }
return null
}

fun rrVizSetMesh(v709_m: String): Unit {

setMeshType(v709_m)
}

fun SiteVizInit() {
g_THEMES = jsObj("vaporwave" to jsObj("label" to "VAPOR", "sky" to jsArr("#0d0428", "#1e1042", "#4a1d66", "#ff68a8", "#ffd2a6"), "sun" to jsArr("#d834ff", "#b266ff", "#c2ffff"), "sunSlice" to "#250d49", "mtnTop" to "#a300b8", "mtnBot" to "#22033d", "mtnStroke" to "#ff00c8", "floor" to jsArr("#170432", "#281060", "#076885"), "grid" to "rgba(130,245,255,0.65)", "waveRgb" to "0, 247, 255", "altWaveRgb" to "255, 0, 160", "primary" to "#00f7ff", "secondary" to "#ff00a0", "accent" to "#ffff00", "strips" to jsArr("#240014", "#ff1e00", "#ff9900", "#ff0055", "#00f7ff")), "bw" to jsObj("label" to "B&W NOIR", "sky" to jsArr("#000000", "#121212", "#2b2b2b", "#777777", "#e0e0e0"), "sun" to jsArr("#ffffff", "#bbbbbb", "#555555"), "sunSlice" to "#0a0a0a", "mtnTop" to "#888888", "mtnBot" to "#111111", "mtnStroke" to "#ffffff", "floor" to jsArr("#050505", "#1b1b1b", "#383838"), "grid" to "rgba(255,255,255,0.65)", "waveRgb" to "255, 255, 255", "altWaveRgb" to "180, 180, 180", "primary" to "#ffffff", "secondary" to "#aaaaaa", "accent" to "#dcdcdc", "strips" to jsArr("#000000", "#ffffff", "#888888", "#444444", "#ffffff")), "matrix" to jsObj("label" to "MATRIX", "sky" to jsArr("#000800", "#001800", "#00300a", "#00661a", "#66ff88"), "sun" to jsArr("#00ff66", "#00b844", "#004411"), "sunSlice" to "#001100", "mtnTop" to "#009933", "mtnBot" to "#001a06", "mtnStroke" to "#00ff66", "floor" to jsArr("#000d02", "#002609", "#004d14"), "grid" to "rgba(0,255,102,0.60)", "waveRgb" to "0, 255, 102", "altWaveRgb" to "120, 255, 160", "primary" to "#00ff66", "secondary" to "#88ffaa", "accent" to "#ffffff", "strips" to jsArr("#001100", "#00ff66", "#00aa33", "#005511", "#88ffaa")), "outrun" to jsObj("label" to "OUTRUN", "sky" to jsArr("#17020a", "#4a0714", "#8c1820", "#e04416", "#ffd166"), "sun" to jsArr("#fff07a", "#ff7b24", "#e52b36"), "sunSlice" to "#320711", "mtnTop" to "#b51d35", "mtnBot" to "#21040a", "mtnStroke" to "#ffe45e", "floor" to jsArr("#18030a", "#4a0714", "#852018"), "grid" to "rgba(255,228,94,0.72)", "waveRgb" to "255, 228, 94", "altWaveRgb" to "255, 59, 48", "primary" to "#ffe45e", "secondary" to "#ff3b30", "accent" to "#fff0d6", "strips" to jsArr("#21040a", "#8c1820", "#e04416", "#ff7b24", "#ffe45e")), "ice" to jsObj("label" to "CYBER ICE", "sky" to jsArr("#010a17", "#041d3d", "#0b3c6e", "#3898ff", "#d4f5ff"), "sun" to jsArr("#ffffff", "#80e5ff", "#0066cc"), "sunSlice" to "#03162e", "mtnTop" to "#1a75ff", "mtnBot" to "#031126", "mtnStroke" to "#aaffff", "floor" to jsArr("#020c1c", "#072447", "#0e4d7a"), "grid" to "rgba(170,255,255,0.65)", "waveRgb" to "140, 245, 255", "altWaveRgb" to "0, 140, 255", "primary" to "#aaffff", "secondary" to "#0088ff", "accent" to "#ffffff", "strips" to jsArr("#021024", "#0066ff", "#00bbff", "#66e0ff", "#ffffff")), "sakura" to jsObj("label" to "SAKURA 95", "sky" to jsArr("#1a0518", "#3d0e36", "#7a1f62", "#ff77aa", "#ffe6f2"), "sun" to jsArr("#fff0f5", "#ff85b3", "#d9006c"), "sunSlice" to "#2e0827", "mtnTop" to "#d63384", "mtnBot" to "#29051f", "mtnStroke" to "#ffb3d9", "floor" to jsArr("#1f0419", "#4a0e3a", "#8c2667"), "grid" to "rgba(255,200,230,0.65)", "waveRgb" to "255, 182, 217", "altWaveRgb" to "255, 51, 153", "primary" to "#ffb3d9", "secondary" to "#ff3399", "accent" to "#fff5fa", "strips" to jsArr("#2b0520", "#ff3385", "#ff80b3", "#ffccf2", "#ffffff")), "acid" to jsObj("label" to "ACID 303", "sky" to jsArr("#09001a", "#1b0044", "#2e0066", "#55aa00", "#ccff00"), "sun" to jsArr("#ffff00", "#99ff00", "#00cc66"), "sunSlice" to "#160033", "mtnTop" to "#66cc00", "mtnBot" to "#12002b", "mtnStroke" to "#ccff00", "floor" to jsArr("#0d0021", "#22004d", "#1c4d00"), "grid" to "rgba(204,255,0,0.65)", "waveRgb" to "204, 255, 0", "altWaveRgb" to "0, 255, 153", "primary" to "#ccff00", "secondary" to "#00ff99", "accent" to "#ffffff", "strips" to jsArr("#14002e", "#7700ff", "#00ff88", "#aaff00", "#ffff00")), "amber" to jsObj("label" to "AMBER CRT", "sky" to jsArr("#0f0600", "#261100", "#4d2200", "#b35900", "#ffcc66"), "sun" to jsArr("#fff0b3", "#ffaa00", "#993d00"), "sunSlice" to "#1f0d00", "mtnTop" to "#cc6600", "mtnBot" to "#1a0900", "mtnStroke" to "#ffbb33", "floor" to jsArr("#120700", "#2e1400", "#592800"), "grid" to "rgba(255,176,0,0.65)", "waveRgb" to "255, 176, 0", "altWaveRgb" to "255, 102, 0", "primary" to "#ffb000", "secondary" to "#ff6600", "accent" to "#ffe699", "strips" to jsArr("#1a0a00", "#803300", "#cc5200", "#ff9900", "#ffd27f")), "mallsoft" to jsObj("label" to "MALLSOFT", "sky" to jsArr("#10212b", "#1f4252", "#4a7c8c", "#f299a9", "#fff0e6"), "sun" to jsArr("#ffffff", "#ffd1dc", "#7fffd4"), "sunSlice" to "#193340", "mtnTop" to "#5ca8a8", "mtnBot" to "#142933", "mtnStroke" to "#a6ffea", "floor" to jsArr("#122630", "#244d5c", "#43808c"), "grid" to "rgba(166,255,234,0.68)", "waveRgb" to "127, 255, 212", "altWaveRgb" to "255, 141, 161", "primary" to "#7fffd4", "secondary" to "#ff8da1", "accent" to "#ffffff", "strips" to jsArr("#162d38", "#4ca6a6", "#7fffd4", "#ff99aa", "#fff5eb")), "midnight" to jsObj("label" to "MIDNIGHT", "sky" to jsArr("#05000a", "#120024", "#29004a", "#800033", "#ff0055"), "sun" to jsArr("#ff0055", "#9900ff", "#00e5ff"), "sunSlice" to "#0d001a", "mtnTop" to "#77003c", "mtnBot" to "#0c0017", "mtnStroke" to "#ff0055", "floor" to jsArr("#07000f", "#190033", "#380030"), "grid" to "rgba(255,0,85,0.65)", "waveRgb" to "255, 0, 85", "altWaveRgb" to "0, 229, 255", "primary" to "#ff0055", "secondary" to "#00e5ff", "accent" to "#d580ff", "strips" to jsArr("#080012", "#4a0072", "#99004d", "#ff0055", "#00e5ff")), "synthwave" to jsObj("label" to "SYNTHWAVE", "sky" to jsArr("#09051f", "#21104b", "#542080", "#a02cff", "#ff4fa3"), "sun" to jsArr("#ff70c8", "#b45cff", "#37d9ff"), "sunSlice" to "#160a35", "mtnTop" to "#8e35d8", "mtnBot" to "#100726", "mtnStroke" to "#00f5ff", "floor" to jsArr("#09051f", "#21104b", "#3d176b"), "grid" to "rgba(0,245,255,0.76)", "waveRgb" to "0, 245, 255", "altWaveRgb" to "255, 60, 172", "primary" to "#00f5ff", "secondary" to "#ff3cac", "accent" to "#ffd6ff", "strips" to jsArr("#100726", "#542080", "#a02cff", "#ff3cac", "#00f5ff")), "toxic" to jsObj("label" to "TOXIC", "sky" to jsArr("#030d0b", "#09241b", "#17472a", "#5f8f20", "#c8ff00"), "sun" to jsArr("#eaff70", "#9bff00", "#00e5a8"), "sunSlice" to "#071711", "mtnTop" to "#3d7a32", "mtnBot" to "#04120e", "mtnStroke" to "#baff00", "floor" to jsArr("#030d0b", "#09241b", "#17472a"), "grid" to "rgba(186,255,0,0.68)", "waveRgb" to "186, 255, 0", "altWaveRgb" to "0, 229, 168", "primary" to "#baff00", "secondary" to "#00e5a8", "accent" to "#e5ffc2", "strips" to jsArr("#04120e", "#17472a", "#3d7a32", "#9bff00", "#00e5a8")), "ocean" to jsObj("label" to "DEEP OCEAN", "sky" to jsArr("#030817", "#101b3a", "#242c63", "#4b4e9b", "#c07bce"), "sun" to jsArr("#f2b5d4", "#c07bce", "#6e68c9"), "sunSlice" to "#0c102b", "mtnTop" to "#47458c", "mtnBot" to "#080d24", "mtnStroke" to "#d5a6ff", "floor" to jsArr("#05091b", "#151d42", "#30295f"), "grid" to "rgba(181,166,255,0.65)", "waveRgb" to "181, 166, 255", "altWaveRgb" to "75, 78, 155", "primary" to "#b5a6ff", "secondary" to "#f2a7d8", "accent" to "#e8e5ff", "strips" to jsArr("#080d24", "#30295f", "#6e68c9", "#b5a6ff", "#f2a7d8")), "crimson" to jsObj("label" to "CRIMSON", "sky" to jsArr("#180009", "#3d0010", "#70001e", "#c00038", "#ff6b81"), "sun" to jsArr("#ffb3c1", "#ff1744", "#8f0030"), "sunSlice" to "#32000d", "mtnTop" to "#c00038", "mtnBot" to "#21000a", "mtnStroke" to "#ffb3c1", "floor" to jsArr("#180009", "#430015", "#70001e"), "grid" to "rgba(255,107,129,0.65)", "waveRgb" to "255, 23, 68", "altWaveRgb" to "255, 179, 193", "primary" to "#ffb3c1", "secondary" to "#ff1744", "accent" to "#ffffff", "strips" to jsArr("#21000a", "#8f0030", "#c00038", "#ff1744", "#ffb3c1")), "lavender" to jsObj("label" to "LAVENDER", "sky" to jsArr("#100d25", "#29204b", "#514078", "#9b7bff", "#ffb8e8"), "sun" to jsArr("#f4eaff", "#c5a3ff", "#ff9de2"), "sunSlice" to "#21163d", "mtnTop" to "#9b7bff", "mtnBot" to "#17102d", "mtnStroke" to "#d9ccff", "floor" to jsArr("#100d25", "#30245a", "#514078"), "grid" to "rgba(217,204,255,0.68)", "waveRgb" to "217, 204, 255", "altWaveRgb" to "255, 157, 226", "primary" to "#d9ccff", "secondary" to "#ff9de2", "accent" to "#ffffff", "strips" to jsArr("#17102d", "#7958b8", "#9b7bff", "#ff9de2", "#d9ccff")))
g_currentMeshType = "icosa"
g_waterfallHistory = jsArr()
g_MAX_WATERFALL_ROWS = 24.0
g_wormTravel = 0.0
g_lissPeakCaps = jsZeros(24.0)
g_MATRIX_STREAMS = jsArrayFrom(36.0) L30@{ v258_e: Any?, v259_a: Double, _ ->

jsObj("xFrac" to (v259_a / 36.0), "y" to (Rt.random() * 220.0), "zLayer" to (v259_a % 3.0), "speed" to ((1.0 + ((v259_a % 3.0) * 0.85)) * (0.8 + ((v259_a % 5.0) * 0.15))), "seed" to (v259_a * 19.0))
}
g_GLYPHS = "0123456789ABCDEFｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉ"
g_ribbonHistory = jsArr()
g_MAX_RIBBON_HIST = 14.0
g_vuLeft = 0.0
g_vuRight = 0.0
g_peakHoldL = 0.0
g_peakHoldR = 0.0
g_EQ_FREQS = jsArr(60.0, 170.0, 350.0, 1000.0, 3000.0, 6000.0, 12000.0, 16000.0)
g_eqPeakCaps = jsZeros(32.0)
g_dspChainWired = false
g_cassetteTransportMode = "play"
g_cassetteReelAngle = 0.0
g_cassetteTapeProgress = 0.25
g_cassetteScratchActive = false
g_currentTapeType = "type1"
g_NEW_VISUALIZER_TARGETS = js_map(jsArr("orbital", "fractal", "prism", "sonar", "magnetic", "kaleido", "starforge")) L66@{ v528_e: Any?, _, _ ->

jsObj("kind" to v528_e, "canvas" to Dom.getElementById(("canvas-" + str(v528_e))), "win" to Dom.getElementById(("win-" + str(v528_e))))
}
jsSet(g_THEMES, "backrooms", jsObj("label" to "BACKROOMS", "sky" to jsArr("#3d3514", "#5c5020", "#807030", "#b59f45", "#d9c66c"), "sun" to jsArr("#fff8bd", "#e3ca66", "#9c862b"), "sunSlice" to "#2e270d", "mtnTop" to "#9c8736", "mtnBot" to "#332b0d", "mtnStroke" to "#fae88c", "floor" to jsArr("#2b240b", "#4f4318", "#786626"), "grid" to "rgba(255, 240, 150, 0.45)", "waveRgb" to "255, 243, 148", "altWaveRgb" to "196, 164, 56", "primary" to "#fff394", "secondary" to "#c9a93b", "accent" to "#ffffff", "strips" to jsArr("#29220a", "#6e5d20", "#a68e36", "#d9be57", "#fff394")))
}
