// GENERATED from the REVERENDER_95 website source by tools/tx.js — do not edit by hand.
@file:Suppress("NAME_SHADOWING", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "UNUSED_VALUE", "VARIABLE_WITH_REDUNDANT_INITIALIZER", "LocalVariableName", "FunctionName", "UNUSED_ANONYMOUS_PARAMETER", "UNNECESSARY_NOT_NULL_ASSERTION", "SENSELESS_COMPARISON", "UNREACHABLE_CODE", "KotlinConstantConditions", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNCHECKED_CAST", "USELESS_CAST", "RedundantExplicitType", "CanBeVal", "unused", "LiftReturnOrAssignment", "CascadeIf", "ReplaceWithOperatorAssignment", "ControlFlowWithEmptyBody", "ReplaceJavaStaticMethodWithKotlinAnalog", "ReplaceManualRangeWithIndicesCalls", "IntroduceWhenSubject", "RedundantIf", "SimplifyBooleanWithConstants")
package com.reverender.r95.ui.viz.site

import com.reverender.r95.ui.viz.js.*

var g_THEMES: Any? = null

var g_bgLayers: Any? = null

var g_clickRipples: Any? = null

var g_STARS: Any? = null

var g_RAIN_DROPS: Any? = null

var g_SAKURA_PETALS: Any? = null

var g_CITY_BUILDINGS: Any? = null

fun getCityBuildings(): Any? {

if (truthy(g_CITY_BUILDINGS)) {
return g_CITY_BUILDINGS
}
g_CITY_BUILDINGS = jsArrayFrom(34.0) L3@{ v70_e: Any?, v71_a: Double, _ ->

val v72_o: Double = (v71_a % 3.0)
val v73_r: Any? = jsArrayFrom(22.0) L4@{ v74_t: Any?, v75_l: Double, _ ->

jsArrayFrom(5.0) L5@{ v76_u: Any?, v77_f: Double, _ ->

val v78_M: Double = kotlin.math.abs(kotlin.math.sin((((v71_a * 19.3) + (v75_l * 7.1)) + (v77_f * 13.7))))
return@L5 jsObj("lit" to (v78_M > 0.35), "isAccent" to (v78_M > 0.82), "phase" to (v78_M * 6.28))
null
}
}
return@L3 jsObj("xFrac" to (((v71_a / 34.0) * 1.04) - 0.02), "wFrac" to (if ((v72_o == 0.0)) 0.038 else (if ((v72_o == 1.0)) 0.032 else 0.026)), "hFrac" to ((if ((v72_o == 0.0)) 0.22 else (if ((v72_o == 1.0)) 0.16 else 0.11)) + (((v71_a * 7.0) % 9.0) * 0.021)), "layer" to v72_o, "hasSpire" to ((v71_a % 3.0) == 0.0), "smoothEq" to 0.0, "winPattern" to v73_r)
null
}
return g_CITY_BUILDINGS
return null
}

fun drawPalmTree(v79_e: Ctx, v80_a: Double, v81_o: Double, v82_r: Double, v83_t: Double, v84_l: Double, v85_u: Any?, v86_f: Double = 1.0, v87_M: Double = 1.0): Unit {

if (((v86_f <= 0.02) || (v82_r <= 0.04))) {
return
}
v79_e.save()
v79_e.globalAlpha = v86_f
v79_e.translate(v80_a, v81_o)
val v88_n: Double = (152.0 * v82_r)
val v89_s: Double = (((v87_M * 24.0) + (kotlin.math.sin(v83_t) * 12.0)) * v82_r)
val v90_i: Double = (v89_s * 0.38)
val v91_g: Double = (-v88_n * 0.52)
val v92_m: Double = v89_s
val v93_d: Double = -v88_n
v79_e.fillStyle = "rgba(4, 1, 14, 0.55)"
v79_e.beginPath()
v79_e.ellipse(0.0, 0.0, (22.0 * v82_r), (5.5 * v82_r), 0.0, 0.0, (Math.PI * 2.0))
v79_e.fill()
val v94_y: Double = 14.0
run {
var v97_v: Double = 0.0
L6@ while ((v97_v < v94_y)) {
val v98_T: Double = (v97_v / v94_y)
val v99_b: Double = ((v97_v + 1.0) / v94_y)
val v100_h: Double = (1.0 - v98_T)
val v101_p: Double = (1.0 - v99_b)
val v102_w: Double = ((((2.0 * v100_h) * v98_T) * v90_i) + ((v98_T * v98_T) * v92_m))
val v103_A: Double = ((((2.0 * v100_h) * v98_T) * v91_g) + ((v98_T * v98_T) * v93_d))
val v104_C: Double = ((((2.0 * v101_p) * v99_b) * v90_i) + ((v99_b * v99_b) * v92_m))
val v105_E: Double = ((((2.0 * v101_p) * v99_b) * v91_g) + ((v99_b * v99_b) * v93_d))
val v106_k: Double = ((7.2 * (1.0 - (v98_T * 0.58))) * v82_r)
val v107_R: Double = (((7.2 * (1.0 - (v99_b * 0.58))) * 1.08) * v82_r)
v79_e.fillStyle = (if (((v97_v % 2.0) == 0.0)) jsGet(v85_u, "sunSlice") else jsGet(v85_u, "mtnBot"))
v79_e.beginPath()
v79_e.moveTo((v102_w - v106_k), v103_A)
v79_e.lineTo((v102_w + v106_k), v103_A)
v79_e.lineTo((v104_C + v107_R), v105_E)
v79_e.lineTo((v104_C - v107_R), v105_E)
v79_e.closePath()
v79_e.fill()
v79_e.strokeStyle = (if (((v97_v % 2.0) == 0.0)) jsGet(v85_u, "primary") else jsGet(v85_u, "secondary"))
v79_e.lineWidth = jsMax(0.8, (1.4 * v82_r))
v79_e.beginPath()
if ((v87_M >= 0.0)) {
v79_e.moveTo((v102_w - v106_k), v103_A)
v79_e.lineTo((v104_C - v107_R), v105_E)
} else {
v79_e.moveTo((v102_w + v106_k), v103_A)
v79_e.lineTo((v104_C + v107_R), v105_E)
}
v79_e.stroke()
v97_v++
}
}
v79_e.translate(v92_m, v93_d)
val v95_c: Any? = jsArr(jsArr(-3.5, 3.5), jsArr(3.5, 3.5), jsArr(0.0, 6.2), jsArr(-2.0, 1.0))
js_forEach(v95_c) L7@{ p8: Any?, v110_b: Double, _ ->
var v108_v: Any? = jsGet(p8, 0.0)
var v109_T: Any? = jsGet(p8, 1.0)
v79_e.fillStyle = (if (((v110_b % 2.0) == 0.0)) jsGet(v85_u, "sunSlice") else "#180526")
v79_e.strokeStyle = jsGet(v85_u, "accent")
v79_e.lineWidth = (0.8 * v82_r)
v79_e.beginPath()
v79_e.arc((num(v108_v) * v82_r), (num(v109_T) * v82_r), (3.6 * v82_r), 0.0, (Math.PI * 2.0))
v79_e.fill()
v79_e.stroke()
null
}
val v96_S: Any? = jsArr(jsObj("ang" to -2.45, "len" to 64.0, "drop" to 26.0, "layer" to 0.0), jsObj("ang" to -1.95, "len" to 76.0, "drop" to 22.0, "layer" to 1.0), jsObj("ang" to -1.45, "len" to 82.0, "drop" to 18.0, "layer" to 0.0), jsObj("ang" to -0.95, "len" to 74.0, "drop" to 14.0, "layer" to 1.0), jsObj("ang" to -0.42, "len" to 58.0, "drop" to 10.0, "layer" to 1.0), jsObj("ang" to 0.42, "len" to 58.0, "drop" to 10.0, "layer" to 1.0), jsObj("ang" to 0.95, "len" to 74.0, "drop" to 14.0, "layer" to 1.0), jsObj("ang" to 1.45, "len" to 82.0, "drop" to 18.0, "layer" to 0.0), jsObj("ang" to 1.95, "len" to 76.0, "drop" to 22.0, "layer" to 1.0), jsObj("ang" to 2.45, "len" to 64.0, "drop" to 26.0, "layer" to 0.0))
js_forEach(v96_S) L9@{ v111_v: Any?, v112_T: Double, _ ->

val v113_b: Double = (if ((num(jsGet(v111_v, "ang")) >= 0.0)) 1.0 else -1.0)
val v114_h: Double = ((kotlin.math.sin(((v83_t * 1.4) + (v112_T * 0.7))) * 0.05) + ((v84_l * 0.06) * v113_b))
val v115_p: Double = (num(jsGet(v111_v, "ang")) + v114_h)
val v116_w: Double = ((kotlin.math.sin(v115_p) * num(jsGet(v111_v, "len"))) * v82_r)
val v117_A: Double = ((((-(kotlin.math.cos(v115_p)) * num(jsGet(v111_v, "len"))) * 0.48) * v82_r) + (num(jsGet(v111_v, "drop")) * v82_r))
val v118_C: Double = (v116_w * 0.52)
val v119_E: Double = ((((-(kotlin.math.abs(kotlin.math.cos(v115_p))) * num(jsGet(v111_v, "len"))) * 0.42) * v82_r) - (10.0 * v82_r))
v79_e.strokeStyle = (if (jsEq(jsGet(v111_v, "layer"), 1.0)) jsGet(v85_u, "primary") else jsGet(v85_u, "secondary"))
v79_e.lineWidth = jsMax(0.9, (1.7 * v82_r))
v79_e.beginPath()
v79_e.moveTo(0.0, 0.0)
v79_e.quadraticCurveTo(v118_C, v119_E, v116_w, v117_A)
v79_e.stroke()
val v120_k: Double = 13.0
v79_e.fillStyle = (if (jsEq(jsGet(v111_v, "layer"), 1.0)) jsGet(v85_u, "mtnBot") else jsGet(v85_u, "sunSlice"))
v79_e.strokeStyle = (if (jsEq(jsGet(v111_v, "layer"), 1.0)) jsGet(v85_u, "mtnStroke") else jsGet(v85_u, "secondary"))
v79_e.lineWidth = jsMax(0.5, (0.85 * v82_r))
v79_e.beginPath()
run {
var v121_R: Double = 1.0
L10@ while ((v121_R <= v120_k)) {
val v122_F: Double = (v121_R / (v120_k + 1.0))
val v123_O: Double = (1.0 - v122_F)
val v124_P: Double = ((((2.0 * v123_O) * v122_F) * v118_C) + ((v122_F * v122_F) * v116_w))
val v125_I: Double = ((((2.0 * v123_O) * v122_F) * v119_E) + ((v122_F * v122_F) * v117_A))
val v126_U_: Double = (((2.0 * (1.0 - (2.0 * v122_F))) * v118_C) + ((2.0 * v122_F) * v116_w))
val v127_B: Double = (((2.0 * (1.0 - (2.0 * v122_F))) * v119_E) + ((2.0 * v122_F) * v117_A))
val v128_L: Double = jsOrN(Math.hypot(v126_U_, v127_B)) { 1.0 }
val v129_G: Double = (v126_U_ / v128_L)
val v130_U: Double = (v127_B / v128_L)
val v131_V: Double = kotlin.math.sin((v122_F * Math.PI))
val v132_D: Double = ((7.0 + (v131_V * 18.0)) * v82_r)
val v133_K: Double = (2.6 * v82_r)
val v134_S_: Double = ((v124_P + ((v129_G * v132_D) * 0.32)) + ((v113_b * 2.2) * v82_r))
val v135_z: Double = ((v125_I + ((v130_U * v132_D) * 0.25)) + (v132_D * 0.92))
v79_e.moveTo((v124_P - ((v129_G * v133_K) * 0.5)), (v125_I - ((v130_U * v133_K) * 0.5)))
v79_e.lineTo(v134_S_, v135_z)
v79_e.lineTo((v124_P + ((v129_G * v133_K) * 0.5)), (v125_I + ((v130_U * v133_K) * 0.5)))
v121_R++
}
}
v79_e.fill()
v79_e.stroke()
null
}
v79_e.restore()
}

fun drawFloatingMonolith(v136_e: Ctx, v137_a: Double, v138_o: Double, v139_r: Double, v140_t: Double, v141_l: Double, v142_u: Any?): Unit {

val v143_f: Double = (v139_r * (1.0 + (v141_l * 0.22)))
val v144_M: Any? = jsObj("x" to v137_a, "y" to (v138_o - (v143_f * 1.18)))
val v145_n: Any? = jsObj("x" to v137_a, "y" to (v138_o + (v143_f * 0.98)))
val v146_s: Any? = jsArrayFrom(4.0) L11@{ v147_i: Any?, v148_g: Double, _ ->

val v149_m: Double = (v140_t + ((v148_g * Math.PI) / 2.0))
return@L11 jsObj("x" to (v137_a + (kotlin.math.cos(v149_m) * v143_f)), "y" to (v138_o + (kotlin.math.sin(v149_m) * (v143_f * 0.35))))
null
}
v136_e.fillStyle = "rgba(12, 3, 28, 0.78)"
v136_e.beginPath()
v136_e.moveTo(num(jsGet(v144_M, "x")), num(jsGet(v144_M, "y")))
v136_e.lineTo(num(jsGet(jsGet(v146_s, 0.0), "x")), num(jsGet(jsGet(v146_s, 0.0), "y")))
v136_e.lineTo(num(jsGet(v145_n, "x")), num(jsGet(v145_n, "y")))
v136_e.lineTo(num(jsGet(jsGet(v146_s, 2.0), "x")), num(jsGet(jsGet(v146_s, 2.0), "y")))
v136_e.closePath()
v136_e.fill()
v136_e.strokeStyle = jsGet(v142_u, "primary")
v136_e.lineWidth = 1.6
v136_e.beginPath()
run {
var v150_i: Double = 0.0
L12@ while ((v150_i < 4.0)) {
val v151_g: Any? = jsGet(v146_s, ((v150_i + 1.0) % 4.0))
v136_e.moveTo(num(jsGet(jsGet(v146_s, v150_i), "x")), num(jsGet(jsGet(v146_s, v150_i), "y")))
v136_e.lineTo(num(jsGet(v151_g, "x")), num(jsGet(v151_g, "y")))
v136_e.moveTo(num(jsGet(v144_M, "x")), num(jsGet(v144_M, "y")))
v136_e.lineTo(num(jsGet(jsGet(v146_s, v150_i), "x")), num(jsGet(jsGet(v146_s, v150_i), "y")))
v136_e.moveTo(num(jsGet(v145_n, "x")), num(jsGet(v145_n, "y")))
v136_e.lineTo(num(jsGet(jsGet(v146_s, v150_i), "x")), num(jsGet(jsGet(v146_s, v150_i), "y")))
v150_i++
}
}
v136_e.stroke()
}

fun drawRetroSun(v152_e: Ctx, v153_a: Double, v154_o: Double, v155_r: Double, v156_t: Double, v157_l: Double, v158_u: Any?, v159_f: Boolean = true): Unit {

val v160_M: Grad = v152_e.createRadialGradient(v153_a, v154_o, (v155_r * 0.35), v153_a, v154_o, (v155_r * 1.9))
v160_M.addColorStop(0.0, ("rgba(" + str(jsGet(v158_u, "altWaveRgb")) + ", " + jsStr((0.4 + (v157_l * 0.22))) + ")"))
v160_M.addColorStop(1.0, "transparent")
v152_e.fillStyle = v160_M
v152_e.beginPath()
v152_e.arc(v153_a, v154_o, (v155_r * 1.9), 0.0, (Math.PI * 2.0))
v152_e.fill()
if (truthy(jsGet(g_bgLayers, "solarRings"))) {
run {
var v162_s: Double = 1.0
L13@ while ((v162_s <= 4.0)) {
val v163_i: Double = ((v155_r + (v162_s * 20.0)) + ((Rt.freqDataArr[jsIdx((v162_s * 6.0))] / 255.0) * 22.0))
v152_e.strokeStyle = (if (((v162_s % 2.0) == 0.0)) jsGet(v158_u, "primary") else jsGet(v158_u, "secondary"))
v152_e.globalAlpha = jsMax(0.14, (0.56 - (v162_s * 0.1)))
v152_e.lineWidth = (2.0 + (v157_l * 3.0))
v152_e.beginPath()
v152_e.arc(v153_a, v154_o, v163_i, Math.PI, (Math.PI * 2.0))
v152_e.stroke()
v152_e.globalAlpha = 1.0
v162_s++
}
}
}
v152_e.save()
v152_e.beginPath()
v152_e.arc(v153_a, v154_o, v155_r, 0.0, (Math.PI * 2.0))
v152_e.clip()
val v161_n: Grad = v152_e.createLinearGradient(v153_a, (v154_o - v155_r), v153_a, (v154_o + v155_r))
v161_n.addColorStop(0.0, jsGet(jsGet(v158_u, "sun"), 0.0))
v161_n.addColorStop(0.55, jsGet(jsGet(v158_u, "sun"), 1.0))
v161_n.addColorStop(1.0, jsGet(jsGet(v158_u, "sun"), 2.0))
v152_e.fillStyle = v161_n
v152_e.fillRect((v153_a - v155_r), (v154_o - v155_r), (v155_r * 2.0), (v155_r * 2.0))
if (v159_f) {
v152_e.fillStyle = jsGet(v158_u, "sunSlice")
run {
var v164_s: Double = 0.0
L14@ while ((v164_s < 8.0)) {
val v165_i: Double = (((v154_o - (v155_r * 0.05)) + (v164_s * (v155_r * 0.13))) + ((v156_t * 6.0) % 14.0))
v152_e.fillRect((v153_a - v155_r), v165_i, (v155_r * 2.0), (3.0 + (v164_s * 1.9)))
v164_s++
}
}
}
v152_e.restore()
}

fun drawPerspectiveFloor(v166_e: Ctx, v167_a: Double, v168_o: Double, v169_r: Double, v170_t: Double, v171_l: Double, v172_u: Double, v173_f: Double, v174_M: Any?, v175_n: String = "checkered"): Unit {

val v176_s: Double = (v171_l % 1.0)
val v177_i: Double = kotlin.math.floor(v171_l)
val v178_g: Grad = v166_e.createLinearGradient(0.0, v170_t, 0.0, v168_o)
v178_g.addColorStop(0.0, jsGet(jsGet(v174_M, "floor"), 0.0))
v178_g.addColorStop(0.38, jsGet(jsGet(v174_M, "floor"), 1.0))
v178_g.addColorStop(1.0, jsGet(jsGet(v174_M, "floor"), 2.0))
v166_e.fillStyle = v178_g
v166_e.fillRect(0.0, v170_t, v167_a, (v168_o - v170_t))
val v179_m: Grad = v166_e.createLinearGradient(0.0, (v170_t - 22.0), 0.0, (v170_t + 34.0))
v179_m.addColorStop(0.0, "transparent")
v179_m.addColorStop(0.45, ("rgba(" + str(jsGet(v174_M, "altWaveRgb")) + ", " + jsToFixed((0.34 + (v173_f * 0.16)), 3.0) + ")"))
v179_m.addColorStop(0.65, ("rgba(" + str(jsGet(v174_M, "waveRgb")) + ", 0.25)"))
v179_m.addColorStop(1.0, "transparent")
v166_e.fillStyle = v179_m
v166_e.fillRect(0.0, (v170_t - 22.0), v167_a, 56.0)
var v180_d: Double = (v170_t - 7.0)
js_forEach(jsGet(v174_M, "strips")) L15@{ v187_h: Any?, v188_p: Double, _ ->

v166_e.fillStyle = v187_h
val v189_w: Double = (if (((v188_p == 1.0) || (v188_p == 4.0))) (2.8 + (v173_f * 2.2)) else 2.0)
v166_e.fillRect(0.0, v180_d, v167_a, v189_w)
v180_d += v189_w
null
}
val v181_y: Double = v180_d
val v182_c: Double = (v168_o - v181_y)
if ((v182_c <= 4.0)) {
return
}
v166_e.save()
v166_e.beginPath()
v166_e.rect(0.0, v181_y, v167_a, v182_c)
v166_e.clip()
val v183_S: Double = 20.0
val v184_v: Double = 16.0
run {
var v190_h: Double = 0.0
L16@ while ((v190_h < v183_S)) {
val v191_p: Double = ((v190_h + v176_s) / v183_S)
val v192_w: Double = (((v190_h + 1.0) + v176_s) / v183_S)
val v193_A: Double = Math.pow(v191_p, 1.92)
val v194_C: Double = Math.pow(jsMin(1.2, v192_w), 1.92)
val v195_E: Double = (v181_y + (v193_A * v182_c))
val v196_k: Double = (v181_y + (v194_C * v182_c))
val v197_R: Double = Math.pow(jsMin(1.0, (v191_p * 2.5)), 1.55)
val v198_F: Double = ((v167_a * 0.017) + ((v193_A * v167_a) * 0.118))
val v199_O: Double = ((v167_a * 0.017) + ((v194_C * v167_a) * 0.118))
run {
var v200_P: Double = (-v184_v / 2.0)
L17@ while ((v200_P < (v184_v / 2.0))) {
val v201_I: Double = (((((v190_h - v177_i) + v200_P) % 2.0) + 2.0) % 2.0)
val v202_U_: Double = (v169_r + (v200_P * v198_F))
val v203_B: Double = (v169_r + ((v200_P + 1.0) * v198_F))
val v204_L: Double = (v169_r + (v200_P * v199_O))
val v205_G: Double = (v169_r + ((v200_P + 1.0) * v199_O))
if ((v175_n == "highway")) {
val v206_U: Boolean = ((v200_P >= -2.0) && (v200_P <= 1.0))
if (v206_U) {
val v207_V: Double = (if (((v200_P == -1.0) || (v200_P == 0.0))) 0.12 else 0.04)
v166_e.fillStyle = ("rgba(7, 2, 19, " + jsToFixed((0.84 * v197_R), 3.0) + ")")
v166_e.beginPath()
v166_e.moveTo(v202_U_, v195_E)
v166_e.lineTo(v203_B, v195_E)
v166_e.lineTo(v205_G, v196_k)
v166_e.lineTo(v204_L, v196_k)
v166_e.closePath()
v166_e.fill()
v166_e.fillStyle = ("rgba(" + str(jsGet(v174_M, "waveRgb")) + ", " + jsToFixed((v207_V * v197_R), 3.0) + ")")
v166_e.fill()
} else {
val v208_V: Double = kotlin.math.abs((v200_P + 0.5))
val v209_D: Double = ((v197_R * (if ((v201_I == 0.0)) 0.28 else 0.11)) * jsMax(0.35, (1.0 - (v208_V * 0.08))))
v166_e.fillStyle = (if ((v201_I == 0.0)) ("rgba(" + str(jsGet(v174_M, "waveRgb")) + ", " + jsToFixed(v209_D, 3.0) + ")") else ("rgba(" + str(jsGet(v174_M, "altWaveRgb")) + ", " + jsToFixed(v209_D, 3.0) + ")"))
v166_e.beginPath()
v166_e.moveTo(v202_U_, v195_E)
v166_e.lineTo(v203_B, v195_E)
v166_e.lineTo(v205_G, v196_k)
v166_e.lineTo(v204_L, v196_k)
v166_e.closePath()
v166_e.fill()
}
} else {
if ((v175_n == "ocean")) {
val v210_U: Boolean = (kotlin.math.abs((v200_P + 0.5)) < 2.2)
val v211_V: Double = (0.15 + (0.18 * kotlin.math.sin(((((v190_h - v177_i) * 0.65) + (v200_P * 0.75)) + (v172_u * 2.1)))))
val v212_D: Double = (if (v210_U) ((0.22 + (v173_f * 0.16)) * (1.0 - (kotlin.math.abs((v200_P + 0.5)) / 2.4))) else 0.0)
val v213_K: Double = (v197_R * jsMin(0.85, ((if ((v201_I == 0.0)) (v211_V + 0.1) else (v211_V * 0.58)) + v212_D)))
v166_e.fillStyle = (if ((v210_U && (v201_I == 0.0))) ("rgba(" + str(jsGet(v174_M, "altWaveRgb")) + ", " + jsToFixed(v213_K, 3.0) + ")") else ("rgba(" + str(jsGet(v174_M, "waveRgb")) + ", " + jsToFixed(v213_K, 3.0) + ")"))
v166_e.beginPath()
v166_e.moveTo(v202_U_, v195_E)
v166_e.lineTo(v203_B, v195_E)
v166_e.lineTo(v205_G, v196_k)
v166_e.lineTo(v204_L, v196_k)
v166_e.closePath()
v166_e.fill()
} else {
val v214_U: Double = (v197_R * (0.18 + (v193_A * 0.34)))
v166_e.fillStyle = (if ((v201_I == 0.0)) ("rgba(" + str(jsGet(v174_M, "waveRgb")) + ", " + jsToFixed(v214_U, 3.0) + ")") else ("rgba(8, 2, 22, " + jsToFixed((v214_U * 0.9), 3.0) + ")"))
v166_e.beginPath()
v166_e.moveTo(v202_U_, v195_E)
v166_e.lineTo(v203_B, v195_E)
v166_e.lineTo(v205_G, v196_k)
v166_e.lineTo(v204_L, v196_k)
v166_e.closePath()
v166_e.fill()
if ((((v201_I == 0.0) && (v191_p > 0.38)) && (kotlin.math.abs(v200_P) <= 4.0))) {
v166_e.strokeStyle = ("rgba(255, 255, 255, " + jsToFixed((v197_R * 0.22), 3.0) + ")")
v166_e.lineWidth = 0.9
v166_e.stroke()
}
}
}
v200_P++
}
}
if ((v175_n == "highway")) {
val v215_P: Double = ((((v190_h - v177_i) % 2.0) + 2.0) % 2.0)
js_forEach(jsArr(-2.0, 2.0)) L18@{ v216_I: Any?, _, _ ->

val v217_U_: Double = (v169_r + (num(v216_I) * v198_F))
val v218_B: Double = (v169_r + (num(v216_I) * v199_O))
val v219_L: Double = jsMax(1.5, (3.0 + (v193_A * 14.0)))
val v220_G: Double = jsMax(2.0, (3.0 + (v194_C * 14.0)))
v166_e.fillStyle = (if ((v215_P == 0.0)) jsGet(v174_M, "primary") else jsGet(v174_M, "secondary"))
v166_e.globalAlpha = (v197_R * 0.92)
v166_e.beginPath()
v166_e.moveTo((v217_U_ - (v219_L * 0.5)), v195_E)
v166_e.lineTo((v217_U_ + (v219_L * 0.5)), v195_E)
v166_e.lineTo((v218_B + (v220_G * 0.5)), v196_k)
v166_e.lineTo((v218_B - (v220_G * 0.5)), v196_k)
v166_e.closePath()
v166_e.fill()
null
}
if ((v215_P == 0.0)) {
v166_e.fillStyle = jsGet(v174_M, "accent")
v166_e.globalAlpha = (v197_R * 0.95)
val v221_I: Double = (1.6 + (v193_A * 9.0))
val v222_U_: Double = (1.6 + (v194_C * 9.0))
v166_e.beginPath()
v166_e.moveTo((v169_r - v221_I), v195_E)
v166_e.lineTo((v169_r + v221_I), v195_E)
v166_e.lineTo((v169_r + v222_U_), v196_k)
v166_e.lineTo((v169_r - v222_U_), v196_k)
v166_e.closePath()
v166_e.fill()
js_forEach(jsArr(-1.0, 1.0)) L19@{ v223_B: Any?, _, _ ->

val v224_L: Double = (v169_r + (num(v223_B) * v198_F))
val v225_G: Double = (v169_r + (num(v223_B) * v199_O))
val v226_U: Double = (0.8 + (v193_A * 4.0))
val v227_V: Double = (0.8 + (v194_C * 4.0))
v166_e.fillStyle = jsGet(v174_M, "primary")
v166_e.beginPath()
v166_e.moveTo((v224_L - v226_U), v195_E)
v166_e.lineTo((v224_L + v226_U), v195_E)
v166_e.lineTo((v225_G + v227_V), v196_k)
v166_e.lineTo((v225_G - v227_V), v196_k)
v166_e.closePath()
v166_e.fill()
null
}
}
v166_e.globalAlpha = 1.0
}
v190_h++
}
}
val v185_T: Grad = v166_e.createRadialGradient(v169_r, v181_y, 6.0, v169_r, (v181_y + (v182_c * 0.78)), (v167_a * 0.48))
v185_T.addColorStop(0.0, ("rgba(" + str(jsGet(v174_M, "waveRgb")) + ", " + jsToFixed((0.3 + (v173_f * 0.2)), 3.0) + ")"))
v185_T.addColorStop(0.45, ("rgba(" + str(jsGet(v174_M, "altWaveRgb")) + ", 0.14)"))
v185_T.addColorStop(1.0, "transparent")
v166_e.fillStyle = v185_T
v166_e.fillRect(0.0, v181_y, v167_a, v182_c)
val v186_b: Double = 26.0
run {
var v228_h: Double = -v186_b
L20@ while ((v228_h <= v186_b)) {
val v229_p: Double = (v169_r + (v228_h * (v167_a * 0.017)))
val v230_w: Double = (v169_r + (v228_h * (v167_a * 0.135)))
v166_e.strokeStyle = (if (((v228_h % 2.0) == 0.0)) jsGet(v174_M, "grid") else ("rgba(" + str(jsGet(v174_M, "waveRgb")) + ", 0.28)"))
v166_e.lineWidth = (if (((v228_h % 2.0) == 0.0)) 1.35 else 0.85)
v166_e.beginPath()
v166_e.moveTo(v229_p, v181_y)
v166_e.lineTo(v230_w, v168_o)
v166_e.stroke()
v228_h++
}
}
run {
var v231_h: Double = 0.0
L21@ while ((v231_h < v183_S)) {
val v232_p: Double = ((v231_h + v176_s) / v183_S)
val v233_w: Double = Math.pow(v232_p, 1.92)
val v234_A: Double = (v181_y + (v233_w * v182_c))
val v235_C: Double = Math.pow(jsMin(1.0, (v232_p * 2.7)), 1.5)
v166_e.beginPath()
val v236_E: Double = 38.0
run {
var v238_R: Double = 0.0
L22@ while ((v238_R <= v236_E)) {
val v239_F: Double = (v238_R / v236_E)
val v240_O: Double = (v239_F * v167_a)
var v241_P: Double = 0.0
if (truthy(jsGet(g_bgLayers, "gridWarp"))) {
val v243_U_: Double = (kotlin.math.abs((v239_F - 0.5)) * 2.0)
val v244_B: Double = (if ((v175_n == "highway")) jsMax(0.18, jsMin(1.0, Math.pow((v243_U_ * 1.65), 2.0))) else 1.0)
val v245_L: Double = jsMin(60.0, kotlin.math.floor((v243_U_ * 32.0)))
val v246_G: Double = ((((Rt.freqDataArr[jsIdx(v245_L)] / 255.0) * 12.0) * v235_C) * v244_B)
val v247_U: Double = (((kotlin.math.sin(((((v239_F * Math.PI) * 2.0) + (v172_u * 1.5)) - (v232_p * 2.0))) * (3.5 + (v173_f * 6.5))) * v235_C) * v244_B)
v241_P = ((v246_G + v247_U) * kotlin.math.sin((v239_F * Math.PI)))
}
val v242_I: Double = (v234_A - v241_P)
if ((v238_R == 0.0)) {
v166_e.moveTo(v240_O, v242_I)
} else {
v166_e.lineTo(v240_O, v242_I)
}
v238_R++
}
}
val v237_k: Double = (v235_C * (0.32 + (v233_w * 0.66)))
v166_e.strokeStyle = ("rgba(" + str(jsGet(v174_M, "waveRgb")) + ", " + jsToFixed(v237_k, 3.0) + ")")
v166_e.lineWidth = (1.0 + (v233_w * 1.75))
v166_e.stroke()
v231_h++
}
}
v166_e.restore()
}

fun rrSceneSmoothstep(v248_e: Double, v249_a: Double, v250_o: Double): Double {

val v251_r: Double = jsMax(0.000001, (v249_a - v248_e))
val v252_t: Double = jsMax(0.0, jsMin(1.0, ((v250_o - v248_e) / v251_r)))
return ((v252_t * v252_t) * (3.0 - (2.0 * v252_t)))
return Double.NaN
}

fun rrSceneMod(v253_e: Double, v254_a: Double): Double {

return (((v253_e % v254_a) + v254_a) % v254_a)
return Double.NaN
}

fun rrSceneViewportFade(v255_e: Double, v256_a: Double, v257_o: Double, v258_r: Double, v259_t: Double, v260_l: Double, v261_u: Double): Double {

val v262_f: Double = jsMax(12.0, jsOrN(v261_u) { 64.0 })
val v263_M: Double = jsMax(0.0, jsOrN(v257_o) { 0.0 })
val v264_n: Double = jsMax(0.0, jsOrN(v258_r) { 0.0 })
val v265_s: Double = rrSceneSmoothstep(-v262_f, ((v263_M * 2.0) + v262_f), (v255_e + v263_M))
val v266_i: Double = rrSceneSmoothstep(-v262_f, ((v263_M * 2.0) + v262_f), (v259_t - (v255_e - v263_M)))
val v267_g: Double = rrSceneSmoothstep(-v262_f, ((v264_n * 2.0) + v262_f), (v256_a + v264_n))
val v268_m: Double = rrSceneSmoothstep(-v262_f, ((v264_n * 2.0) + v262_f), (v260_l - (v256_a - v264_n)))
return jsMinN(v265_s, v266_i, v267_g, v268_m)
return Double.NaN
}

fun drawPerspectiveCorridor(v269_e: Double, v270_a: Double, v271_o: Double, v272_r: Any?): Unit {

val v273_t: Any? = jsArr()
val v274_l: Double = (v271_o * v270_a)
run {
var v275_u: Double = 0.0
L23@ while ((v275_u < v269_e)) {
val v276_f: Double = (((v275_u * 5.0) % jsMax(1.0, v269_e)) * 0.045)
val v277_M: Double = (rrSceneMod(((v275_u + v276_f) + v274_l), v269_e) / v269_e)
val v278_n: Double = rrSceneSmoothstep(0.012, 0.085, v277_M)
val v279_s: Double = (1.0 - rrSceneSmoothstep(0.88, 0.985, v277_M))
val v280_i: Double = (v278_n * v279_s)
if ((v280_i > 0.002)) {
jsPush(v273_t, jsObj("norm" to v277_M, "alpha" to v280_i, "idx" to v275_u))
}
v275_u++
}
}
js_sort(v273_t) L24@{ v281_u: Any?, v282_f: Any? ->

(num(jsGet(v281_u, "norm")) - num(jsGet(v282_f, "norm")))
}
js_forEach(v273_t) L25@{ v283_u: Any?, _, _ ->

val v284_f: Double = Math.pow(num(jsGet(v283_u, "norm")), 1.85)
jsCall(v272_r, jsGet(v283_u, "norm"), v284_f, jsGet(v283_u, "alpha"), jsGet(v283_u, "idx"))
null
}
}

fun renderScene0_SunsetOutrun(v285_e: Ctx, v286_a: Double, v287_o: Double, v288_r: Double, v289_t: Double, v290_l: Double, v291_u: Double, v292_f: Double, v293_M: Double, v294_n: Any?): Unit {

val v295_s: Double = ((jsMin(v286_a, v287_o) * 0.22) * (1.0 + (v292_f * 0.12)))
drawRetroSun(v285_e, v288_r, (v289_t - (v295_s * 0.35)), v295_s, v290_l, v292_f, v294_n, true)
if (truthy(jsGet(g_bgLayers, "monoliths"))) {
val v300_y: Double = (kotlin.math.sin((v290_l * 1.8)) * 10.0)
drawFloatingMonolith(v285_e, ((v286_a * 0.2) + (Rt.mouseX * 20.0)), ((v289_t - (v287_o * 0.25)) + v300_y), 40.0, (v290_l * 0.7), v292_f, v294_n)
drawFloatingMonolith(v285_e, ((v286_a * 0.8) + (Rt.mouseX * 20.0)), ((v289_t - (v287_o * 0.25)) - v300_y), 40.0, (-v290_l * 0.7), v293_M, v294_n)
}
val v296_i: Any? = jsArr(jsArr(0.02, 0.0), jsArr(0.12, 0.28), jsArr(0.22, 0.42), jsArr(0.31, 0.68), jsArr(0.41, 0.46), jsArr(0.5, 0.22), jsArr(0.59, 0.52), jsArr(0.69, 0.72), jsArr(0.79, 0.44), jsArr(0.88, 0.29), jsArr(0.98, 0.0))
val v297_g: Any? = js_map(v296_i) L26@{ p27: Any?, v303_S: Double, _ ->
var v301_y: Any? = jsGet(p27, 0.0)
var v302_c: Any? = jsGet(p27, 1.0)
val v304_v: Double = (if (truthy(jsGet(g_bgLayers, "skySpectrum"))) ((Rt.freqDataArr[jsIdx((v303_S * 5.0))] / 255.0) * (v287_o * 0.08)) else 0.0)
return@L26 jsObj("x" to ((v286_a * num(v301_y)) + (Rt.mouseX * 12.0)), "y" to ((v289_t - (num(v302_c) * (v287_o * 0.26))) - v304_v))
null
}
v285_e.beginPath()
v285_e.moveTo(num(jsGet(jsGet(v297_g, 0.0), "x")), v289_t)
js_forEach(v297_g) L28@{ v305_y: Any?, _, _ ->

v285_e.lineTo(num(jsGet(v305_y, "x")), num(jsGet(v305_y, "y")))
}
v285_e.lineTo(num(jsGet(jsGet(v297_g, (jsLen(v297_g) - 1.0)), "x")), v289_t)
v285_e.closePath()
val v298_m: Grad = v285_e.createLinearGradient(0.0, (v289_t - (v287_o * 0.28)), 0.0, v289_t)
v298_m.addColorStop(0.0, jsGet(v294_n, "mtnTop"))
v298_m.addColorStop(1.0, jsGet(v294_n, "mtnBot"))
v285_e.fillStyle = v298_m
v285_e.fill()
v285_e.strokeStyle = jsGet(v294_n, "mtnStroke")
v285_e.lineWidth = 1.6
v285_e.stroke()
v285_e.strokeStyle = jsGet(v294_n, "primary")
v285_e.globalAlpha = 0.45
v285_e.lineWidth = 1.0
v285_e.beginPath()
js_forEach(v297_g) L29@{ v306_y: Any?, v307_c: Double, _ ->

v285_e.moveTo(num(jsGet(v306_y, "x")), num(jsGet(v306_y, "y")))
v285_e.lineTo((num(jsGet(v306_y, "x")) + (if (((v307_c % 2.0) == 0.0)) 22.0 else -22.0)), v289_t)
if ((v307_c < (jsLen(v297_g) - 1.0))) {
v285_e.moveTo(num(jsGet(v306_y, "x")), num(jsGet(v306_y, "y")))
v285_e.lineTo(((num(jsGet(v306_y, "x")) + num(jsGet(jsGet(v297_g, (v307_c + 1.0)), "x"))) * 0.5), v289_t)
}
null
}
v285_e.stroke()
v285_e.globalAlpha = 1.0
drawPerspectiveFloor(v285_e, v286_a, v287_o, v288_r, v289_t, v291_u, v290_l, v292_f, v294_n, "highway")
val v299_d: Double = (v290_l * 1.6)
drawPerspectiveCorridor(6.0, 0.4, v291_u, JsFn L30@{ args ->
var v308_y: Any? = args.getOrNull(0)
var v309_c: Any? = args.getOrNull(1)
var v310_S: Any? = args.getOrNull(2)
var v311_v: Any? = args.getOrNull(3)
val v312_T: Double = (v289_t + (num(v309_c) * (v287_o - v289_t)))
val v313_b: Double = ((0.18 + (num(v309_c) * 1.12)) * (jsMin(v286_a, v287_o) / 840.0))
js_forEach(jsArr(-1.0, 1.0)) L31@{ v314_h: Any?, _, _ ->

val v315_p: Double = (v288_r + (num(v314_h) * ((v286_a * 0.14) + ((num(v309_c) * v286_a) * 0.38))))
drawPalmTree(v285_e, v315_p, v312_T, v313_b, (v299_d + num(v311_v)), v292_f, v294_n, num(v310_S), (-(num(v314_h)) * 0.85))
v285_e.save()
v285_e.globalAlpha = num(v310_S)
v285_e.fillStyle = (if (((num(v311_v) % 2.0) == 0.0)) jsGet(v294_n, "primary") else jsGet(v294_n, "secondary"))
val v316_w: Double = (12.0 + (num(v309_c) * 36.0))
v285_e.fillRect((v315_p - (num(v314_h) * (16.0 * num(v309_c)))), (v312_T - v316_w), (3.0 + (num(v309_c) * 3.5)), v316_w)
v285_e.restore()
null
}
null
})
}

fun renderScene1_CyberCity(v317_e: Ctx, v318_a: Double, v319_o: Double, v320_r: Double, v321_t: Double, v322_l: Double, v323_u: Double, v324_f: Double, v325_M: Double, v326_n: Any?): Unit {

run {
var v328_i: Double = 0.0
L32@ while ((v328_i < 4.0)) {
val v329_g: Double = ((v318_a * (0.18 + (v328_i * 0.21))) + (Rt.mouseX * 10.0))
val v330_m: Double = (kotlin.math.sin(((v322_l * (0.5 + (v328_i * 0.18))) + (v328_i * 1.3))) * 0.45)
v317_e.save()
v317_e.translate(v329_g, v321_t)
v317_e.rotate(v330_m)
val v331_d: Grad = v317_e.createLinearGradient(0.0, 0.0, 0.0, (-v319_o * 0.58))
v331_d.addColorStop(0.0, ("rgba(" + str(jsGet(v326_n, "waveRgb")) + ", " + jsToFixed((0.24 + (v324_f * 0.18)), 3.0) + ")"))
v331_d.addColorStop(1.0, "transparent")
v317_e.fillStyle = v331_d
v317_e.beginPath()
v317_e.moveTo(-8.0, 0.0)
v317_e.lineTo(-90.0, (-v319_o * 0.58))
v317_e.lineTo(90.0, (-v319_o * 0.58))
v317_e.lineTo(8.0, 0.0)
v317_e.closePath()
v317_e.fill()
v317_e.restore()
v328_i++
}
}
val v327_s: Double = ((jsMin(v318_a, v319_o) * 0.195) * (1.0 + (v324_f * 0.08)))
drawRetroSun(v317_e, v320_r, (v321_t - (v327_s * 0.65)), v327_s, v322_l, v324_f, v326_n, true)
js_forEach(jsArr(0.0, 1.0, 2.0)) L33@{ v332_i: Any?, _, _ ->

val v333_g: Double = ((if (jsEq(v332_i, 0.0)) 8.0 else (if (jsEq(v332_i, 1.0)) 18.0 else 30.0)) * Rt.mouseX)
val v334_m: Double = (if (jsEq(v332_i, 0.0)) 0.58 else (if (jsEq(v332_i, 1.0)) 0.85 else 1.0))
js_forEach(getCityBuildings()) L34@{ v335_d: Any?, v336_y: Double, _ ->

if (!jsEq(jsGet(v335_d, "layer"), v332_i)) {
return@L34 null
}
val v337_c: Double = (if (truthy(jsGet(g_bgLayers, "skySpectrum"))) ((Rt.freqDataArr[jsIdx(((v336_y * 3.0) % 96.0))] / 255.0) * (v319_o * 0.085)) else 0.0)
jsAddAssign(v335_d, "smoothEq", ((v337_c - num(jsGet(v335_d, "smoothEq"))) * 0.14))
val v338_S: Double = kotlin.math.floor((num(jsGet(v335_d, "wFrac")) * v318_a))
val v339_v: Double = ((num(jsGet(v335_d, "hFrac")) * v319_o) + num(jsGet(v335_d, "smoothEq")))
val v340_T: Double = jsRound(((num(jsGet(v335_d, "xFrac")) * v318_a) + v333_g))
val v341_b: Double = (v321_t - v339_v)
val v342_h: Double = (if (((v340_T + (v338_S * 0.5)) < v320_r)) 1.0 else -1.0)
val v343_p: Double = (v342_h * jsMax(6.0, (v338_S * 0.34)))
val v344_w: Double = -6.0
v317_e.save()
v317_e.globalAlpha = v334_m
v317_e.fillStyle = "#05010d"
v317_e.strokeStyle = jsGet(v326_n, "secondary")
v317_e.lineWidth = 1.0
v317_e.beginPath()
if ((v342_h > 0.0)) {
v317_e.moveTo((v340_T + v338_S), v341_b)
v317_e.lineTo(((v340_T + v338_S) + v343_p), (v341_b + v344_w))
v317_e.lineTo(((v340_T + v338_S) + v343_p), v321_t)
v317_e.lineTo((v340_T + v338_S), v321_t)
} else {
v317_e.moveTo(v340_T, v341_b)
v317_e.lineTo((v340_T + v343_p), (v341_b + v344_w))
v317_e.lineTo((v340_T + v343_p), v321_t)
v317_e.lineTo(v340_T, v321_t)
}
v317_e.closePath()
v317_e.fill()
v317_e.stroke()
v317_e.fillStyle = jsGet(v326_n, "mtnTop")
v317_e.beginPath()
v317_e.moveTo(v340_T, v341_b)
v317_e.lineTo((v340_T + v338_S), v341_b)
v317_e.lineTo(((v340_T + v338_S) + v343_p), (v341_b + v344_w))
v317_e.lineTo((v340_T + v343_p), (v341_b + v344_w))
v317_e.closePath()
v317_e.fill()
v317_e.stroke()
v317_e.fillStyle = (if (jsEq(v332_i, 0.0)) jsGet(v326_n, "mtnBot") else jsGet(v326_n, "sunSlice"))
v317_e.strokeStyle = (if (jsEq(v332_i, 0.0)) jsGet(v326_n, "secondary") else jsGet(v326_n, "primary"))
v317_e.lineWidth = (if (jsEq(v332_i, 2.0)) 1.5 else 1.1)
v317_e.fillRect(v340_T, v341_b, v338_S, v339_v)
v317_e.strokeRect(v340_T, v341_b, v338_S, v339_v)
if ((truthy(jsGet(v335_d, "hasSpire")) && (num(v332_i) >= 1.0))) {
val v345_A: Double = (16.0 + (num(jsGet(v335_d, "smoothEq")) * 0.5))
v317_e.strokeStyle = jsGet(v326_n, "accent")
v317_e.lineWidth = 1.6
v317_e.beginPath()
v317_e.moveTo((v340_T + (v338_S * 0.5)), v341_b)
v317_e.lineTo((v340_T + (v338_S * 0.5)), (v341_b - v345_A))
v317_e.stroke()
}
if (((num(v332_i) >= 1.0) && (v338_S >= 16.0))) {
val v346_A: Double = jsMin(jsLen(jsGet(v335_d, "winPattern")), kotlin.math.floor(((v339_v - 12.0) / 10.0)))
val v347_C: Double = jsMin(4.0, jsMax(2.0, kotlin.math.floor(((v338_S - 6.0) / 7.0))))
val v348_E: Double = ((v338_S - 6.0) / v347_C)
run {
var v349_k: Double = 0.0
L35@ while ((v349_k < v346_A)) {
val v350_R: Double = ((v321_t - 10.0) - (v349_k * 10.0))
if ((v350_R < (v341_b + 6.0))) {
break@L35
}
run {
var v351_F: Double = 0.0
L36@ while ((v351_F < v347_C)) {
L36c@ do {
val v352_O: Any? = jsGet(jsGet(jsGet(v335_d, "winPattern"), v349_k), v351_F)
if (!(truthy(jsGet(v352_O, "lit")))) {
break@L36c
}
v317_e.fillStyle = (if (truthy(jsGet(v352_O, "isAccent"))) jsGet(v326_n, "accent") else (if (((v336_y % 2.0) == 0.0)) jsGet(v326_n, "primary") else jsGet(v326_n, "secondary")))
v317_e.fillRect(((v340_T + 4.0) + (v351_F * v348_E)), v350_R, jsMax(2.0, (v348_E - 3.0)), 5.0)
} while (false)
v351_F++
}
}
v349_k++
}
}
}
v317_e.restore()
null
}
null
}
drawPerspectiveFloor(v317_e, v318_a, v319_o, v320_r, v321_t, v323_u, v322_l, v324_f, v326_n, "highway")
drawPerspectiveCorridor(6.0, 0.48, v323_u, JsFn L37@{ args ->
var v353_i: Any? = args.getOrNull(0)
var v354_g: Any? = args.getOrNull(1)
var v355_m: Any? = args.getOrNull(2)
var v356_d: Any? = args.getOrNull(3)
val v357_y: Double = (v321_t + (num(v354_g) * (v319_o - v321_t)))
val v358_c: Double = (v319_o * (0.14 + (num(v354_g) * 0.44)))
js_forEach(jsArr(-1.0, 1.0)) L38@{ v359_S: Any?, _, _ ->

val v360_v: Double = (v320_r + (num(v359_S) * ((v318_a * 0.14) + ((num(v354_g) * v318_a) * 0.36))))
v317_e.save()
v317_e.globalAlpha = num(v355_m)
v317_e.strokeStyle = (if (((num(v356_d) % 2.0) == 0.0)) jsGet(v326_n, "primary") else jsGet(v326_n, "secondary"))
v317_e.lineWidth = (1.5 + (num(v354_g) * 3.0))
v317_e.beginPath()
v317_e.moveTo(v360_v, v357_y)
v317_e.lineTo(v360_v, (v357_y - v358_c))
v317_e.lineTo((v360_v - (num(v359_S) * (18.0 + (num(v354_g) * 48.0)))), (v357_y - v358_c))
v317_e.stroke()
v317_e.fillStyle = jsGet(v326_n, "accent")
v317_e.fillRect(((v360_v - (num(v359_S) * (18.0 + (num(v354_g) * 48.0)))) - 4.0), ((v357_y - v358_c) - 2.0), (8.0 + (num(v354_g) * 10.0)), (4.0 + (num(v354_g) * 4.0)))
v317_e.restore()
null
}
null
})
}

var g_BUST_RINGS: Any? = null

fun renderScene2_HeliosMall(v361_e: Ctx, v362_a: Double, v363_o: Double, v364_r: Double, v365_t: Double, v366_l: Double, v367_u: Double, v368_f: Double, v369_M: Double, v370_n: Any?): Unit {

if (truthy(jsGet(g_bgLayers, "skySpectrum"))) {
val v378_S: Double = 9.0
val v379_v: Double = (v362_a * 0.84)
val v380_T: Double = (v364_r - (v379_v / 2.0))
val v381_b: Double = (v379_v / v378_S)
run {
var v382_h: Double = 0.0
L39@ while ((v382_h < v378_S)) {
val v383_p: Double = (v380_T + (v382_h * v381_b))
val v384_w: Double = ((v363_o * 0.09) + ((Rt.freqDataArr[jsIdx((v382_h * 5.0))] / 255.0) * (v363_o * 0.08)))
v361_e.fillStyle = jsGet(v370_n, "mtnBot")
v361_e.strokeStyle = jsGet(v370_n, "secondary")
v361_e.lineWidth = 1.4
v361_e.beginPath()
v361_e.moveTo(v383_p, v365_t)
v361_e.lineTo(v383_p, (v365_t - (v384_w * 0.6)))
v361_e.arc((v383_p + (v381_b / 2.0)), (v365_t - (v384_w * 0.6)), (v381_b / 2.0), Math.PI, 0.0, false)
v361_e.lineTo((v383_p + v381_b), v365_t)
v361_e.closePath()
v361_e.fill()
v361_e.stroke()
v382_h++
}
}
}
drawPerspectiveFloor(v361_e, v362_a, v363_o, v364_r, v365_t, v367_u, v366_l, v368_f, v370_n, "checkered")
val v371_s: Double = ((v365_t - (v363_o * 0.16)) + (kotlin.math.sin((v366_l * 1.5)) * 6.0))
val v372_i: Double = ((jsMin(v362_a, v363_o) * 0.155) * (1.0 + (v368_f * 0.12)))
drawRetroSun(v361_e, v364_r, v371_s, v372_i, v366_l, v368_f, v370_n, false)
val v373_g: Double = ((jsMin(v362_a, v363_o) * 0.135) * (1.0 + (v368_f * 0.06)))
val v374_m: Double = (v366_l * 0.85)
val v375_d: Double = (0.18 + (kotlin.math.sin((v366_l * 0.7)) * 0.06))
val v376_y: Double = 14.0
val v377_c: Any? = js_map(g_BUST_RINGS) L40@{ v385_S: Any?, v386_v: Double, _ ->

val v387_T: Any? = jsArr()
val v388_b: Double = (1.0 + ((Rt.freqDataArr[jsIdx((v386_v * 4.0))] / 255.0) * 0.08))
run {
var v389_h: Double = 0.0
L41@ while ((v389_h < v376_y)) {
val v390_p: Double = ((((v389_h / v376_y) * Math.PI) * 2.0) + v374_m)
val v391_w: Double = (if ((((v386_v >= 2.0) && (v386_v <= 4.0)) && (kotlin.math.cos((v390_p - v374_m)) > 0.5))) (Math.pow(kotlin.math.cos((v390_p - v374_m)), 3.0) * 0.12) else 0.0)
val v392_A: Double = ((kotlin.math.sin(v390_p) * (num(jsGet(v385_S, "rx")) + v391_w)) * v388_b)
val v393_C: Double = ((kotlin.math.cos(v390_p) * (num(jsGet(v385_S, "rz")) + v391_w)) * v388_b)
val v394_E: Double = ((num(jsGet(v385_S, "y")) * kotlin.math.cos(v375_d)) - (v393_C * kotlin.math.sin(v375_d)))
val v395_k: Double = ((num(jsGet(v385_S, "y")) * kotlin.math.sin(v375_d)) + (v393_C * kotlin.math.cos(v375_d)))
val v396_R: Double = (3.2 / (3.2 + (v395_k * 0.45)))
jsPush(v387_T, jsObj("x" to (v364_r + ((v392_A * v373_g) * v396_R)), "y" to (v371_s + ((v394_E * v373_g) * v396_R))))
v389_h++
}
}
return@L40 v387_T
null
}
v361_e.save()
v361_e.fillStyle = "rgba(12, 3, 28, 0.78)"
v361_e.beginPath()
run {
var v397_S: Double = 0.0
L42@ while ((v397_S < jsLen(g_BUST_RINGS))) {
val v398_v: Double = ((num(jsGet(jsGet(g_BUST_RINGS, v397_S), "rx")) * v373_g) * 1.02)
val v399_T: Double = (v371_s + (num(jsGet(jsGet(g_BUST_RINGS, v397_S), "y")) * v373_g))
if ((v397_S == 0.0)) {
v361_e.moveTo((v364_r - v398_v), v399_T)
} else {
v361_e.lineTo((v364_r - v398_v), v399_T)
}
v397_S++
}
}
run {
var v400_S: Double = (jsLen(g_BUST_RINGS) - 1.0)
L43@ while ((v400_S >= 0.0)) {
v361_e.lineTo((v364_r + ((num(jsGet(jsGet(g_BUST_RINGS, v400_S), "rx")) * v373_g) * 1.02)), (v371_s + (num(jsGet(jsGet(g_BUST_RINGS, v400_S), "y")) * v373_g)))
v400_S--
}
}
v361_e.closePath()
v361_e.fill()
run {
var v401_S: Double = 0.0
L44@ while ((v401_S < jsLen(v377_c))) {
val v402_v: Any? = jsGet(v377_c, v401_S)
v361_e.strokeStyle = (if (((v401_S % 2.0) == 0.0)) jsGet(v370_n, "primary") else jsGet(v370_n, "secondary"))
v361_e.lineWidth = 1.4
v361_e.beginPath()
run {
var v403_T: Double = 0.0
L45@ while ((v403_T <= v376_y)) {
val v404_b: Any? = jsGet(v402_v, (v403_T % v376_y))
if ((v403_T == 0.0)) {
v361_e.moveTo(num(jsGet(v404_b, "x")), num(jsGet(v404_b, "y")))
} else {
v361_e.lineTo(num(jsGet(v404_b, "x")), num(jsGet(v404_b, "y")))
}
v403_T++
}
}
v361_e.stroke()
if ((v401_S < (jsLen(v377_c) - 1.0))) {
val v405_T: Any? = jsGet(v377_c, (v401_S + 1.0))
v361_e.strokeStyle = jsGet(v370_n, "primary")
v361_e.globalAlpha = 0.65
v361_e.lineWidth = 1.1
v361_e.beginPath()
run {
var v406_b: Double = 0.0
L46@ while ((v406_b < v376_y)) {
v361_e.moveTo(num(jsGet(jsGet(v402_v, v406_b), "x")), num(jsGet(jsGet(v402_v, v406_b), "y")))
v361_e.lineTo(num(jsGet(jsGet(v405_T, v406_b), "x")), num(jsGet(jsGet(v405_T, v406_b), "y")))
v406_b++
}
}
v361_e.stroke()
v361_e.globalAlpha = 1.0
}
v401_S++
}
}
v361_e.restore()
if (truthy(jsGet(g_bgLayers, "monoliths"))) {
js_forEach(jsArr(-1.0, 1.0)) L47@{ v407_S: Any?, v408_v: Double, _ ->

val v409_T: Double = (((v366_l * 0.55) + (v408_v * 0.5)) % 1.0)
val v410_b: Double = kotlin.math.sin((v409_T * Math.PI))
val v411_h: Double = (v364_r + (num(v407_S) * ((v362_a * 0.16) + (((v409_T - 0.5) * v362_a) * 0.18))))
val v412_p: Double = (v365_t + ((v363_o - v365_t) * 0.12))
val v413_w: Double = ((v363_o * 0.22) * (1.0 + (v369_M * 0.2)))
val v414_A: Double = (v412_p - (kotlin.math.sin((v409_T * Math.PI)) * v413_w))
val v415_C: Double = kotlin.math.atan2(((-(kotlin.math.cos((v409_T * Math.PI))) * Math.PI) * v413_w), ((num(v407_S) * v362_a) * 0.18))
v361_e.save()
v361_e.globalAlpha = v410_b
v361_e.translate(v411_h, v414_A)
v361_e.rotate(v415_C)
if ((num(v407_S) < 0.0)) {
v361_e.scale(1.0, -1.0)
}
val v416_E: Double = (jsMin(v362_a, v363_o) / 950.0)
v361_e.scale(v416_E, v416_E)
v361_e.fillStyle = jsGet(v370_n, "sunSlice")
v361_e.strokeStyle = jsGet(v370_n, "accent")
v361_e.lineWidth = 2.0
v361_e.beginPath()
v361_e.moveTo(38.0, 0.0)
v361_e.quadraticCurveTo(18.0, -18.0, -4.0, -14.0)
v361_e.lineTo(-14.0, -30.0)
v361_e.quadraticCurveTo(-18.0, -14.0, -26.0, -10.0)
v361_e.quadraticCurveTo(-44.0, -6.0, -56.0, -18.0)
v361_e.lineTo(-48.0, 0.0)
v361_e.lineTo(-56.0, 14.0)
v361_e.quadraticCurveTo(-36.0, 4.0, -12.0, 10.0)
v361_e.lineTo(-4.0, 22.0)
v361_e.lineTo(4.0, 10.0)
v361_e.quadraticCurveTo(24.0, 10.0, 38.0, 0.0)
v361_e.closePath()
v361_e.fill()
v361_e.stroke()
v361_e.restore()
null
}
}
drawPerspectiveCorridor(6.0, 0.42, v367_u, JsFn L48@{ args ->
var v417_S: Any? = args.getOrNull(0)
var v418_v: Any? = args.getOrNull(1)
var v419_T: Any? = args.getOrNull(2)
val v420_b: Double = (v363_o * (0.14 + (num(v418_v) * 0.46)))
val v421_h: Double = (10.0 + (num(v418_v) * 34.0))
val v422_p: Double = (v365_t + (num(v418_v) * (v363_o - v365_t)))
val v423_w: Double = (v422_p - v420_b)
js_forEach(jsArr(-1.0, 1.0)) L49@{ v424_A: Any?, _, _ ->

val v425_C: Double = ((v364_r + (num(v424_A) * ((v362_a * 0.13) + ((num(v418_v) * v362_a) * 0.38)))) - (v421_h / 2.0))
v361_e.save()
v361_e.globalAlpha = num(v419_T)
val v426_E: Grad = v361_e.createLinearGradient(v425_C, v423_w, (v425_C + v421_h), v423_w)
v426_E.addColorStop(0.0, jsGet(v370_n, "sunSlice"))
v426_E.addColorStop(0.5, jsGet(v370_n, "mtnBot"))
v426_E.addColorStop(1.0, jsGet(v370_n, "sunSlice"))
v361_e.fillStyle = v426_E
v361_e.strokeStyle = jsGet(v370_n, "primary")
v361_e.lineWidth = 1.5
v361_e.fillRect(v425_C, v423_w, v421_h, v420_b)
v361_e.strokeRect(v425_C, v423_w, v421_h, v420_b)
v361_e.fillStyle = jsGet(v370_n, "secondary")
v361_e.strokeStyle = jsGet(v370_n, "accent")
val v427_k: Double = (7.0 * (0.5 + (num(v418_v) * 0.5)))
v361_e.fillRect((v425_C - (v421_h * 0.22)), (v423_w - v427_k), (v421_h * 1.44), v427_k)
v361_e.strokeRect((v425_C - (v421_h * 0.22)), (v423_w - v427_k), (v421_h * 1.44), v427_k)
v361_e.restore()
null
}
null
})
}

fun renderScene3_TwinMoonOcean(v428_e: Ctx, v429_a: Double, v430_o: Double, v431_r: Double, v432_t: Double, v433_l: Double, v434_u: Double, v435_f: Double, v436_M: Double, v437_n: Any?): Unit {

var v438_s: Double = 0.0
var v439_i: Double = 0.0
var v440_g: Double = 0.0
var v441_m: Grad? = null
var v442_d: Double = 0.0
var v443_y: Double = 0.0
var v444_c: Double = 0.0
var v445_S: Double = 0.0
var v446_v: Grad? = null
v438_s = ((jsMin(v429_a, v430_o) * 0.21) * (1.0 + (v435_f * 0.12)))
v439_i = (v431_r - (v429_a * 0.12))
v440_g = (v432_t - (v438_s * 0.42))
v441_m = v428_e.createRadialGradient((v439_i - (v438_s * 0.3)), (v440_g - (v438_s * 0.3)), (v438_s * 0.05), v439_i, v440_g, v438_s)
v441_m!!.addColorStop(0.0, "#ffffff")
v441_m!!.addColorStop(0.6, jsGet(v437_n, "secondary"))
v441_m!!.addColorStop(1.0, jsGet(v437_n, "sunSlice"))
v428_e.fillStyle = v441_m!!
v428_e.beginPath()
v428_e.arc(v439_i, v440_g, v438_s, 0.0, (Math.PI * 2.0))
v428_e.fill()
v428_e.fillStyle = "rgba(0,0,0,0.25)"
js_forEach(jsArr(jsArr(0.2, -0.1, 0.22), jsArr(-0.3, 0.3, 0.15), jsArr(0.1, 0.4, 0.18))) L50@{ v447_T: Any?, _, _ ->

v428_e.beginPath()
v428_e.arc((v439_i + (num(jsGet(v447_T, 0.0)) * v438_s)), (v440_g + (num(jsGet(v447_T, 1.0)) * v438_s)), (num(jsGet(v447_T, 2.0)) * v438_s), 0.0, (Math.PI * 2.0))
v428_e.fill()
null
}
v442_d = ((jsMin(v429_a, v430_o) * 0.1) * (1.0 + (v436_M * 0.16)))
v443_y = ((v431_r + (v429_a * 0.24)) + (Rt.mouseX * 22.0))
v444_c = ((v432_t - (v430_o * 0.28)) + (kotlin.math.sin((v433_l * 1.4)) * 10.0))
v445_S = (-0.35 + (kotlin.math.sin((v433_l * 0.7)) * 0.08))
v428_e.save()
v428_e.translate(v443_y, v444_c)
v428_e.rotate(v445_S)
js_forEach(jsArr(2.6, 2.1, 1.7)) L51@{ v448_T: Any?, v449_b: Double, _ ->

v428_e.strokeStyle = (if (((v449_b % 2.0) == 0.0)) jsGet(v437_n, "primary") else jsGet(v437_n, "accent"))
v428_e.lineWidth = (if ((v449_b == 0.0)) 3.5 else 2.0)
v428_e.beginPath()
v428_e.ellipse(0.0, 0.0, (v442_d * num(v448_T)), ((v442_d * num(v448_T)) * 0.3), 0.0, Math.PI, (Math.PI * 2.0))
v428_e.stroke()
null
}
v428_e.rotate(-v445_S)
v446_v = v428_e.createRadialGradient((-v442_d * 0.3), (-v442_d * 0.3), (v442_d * 0.1), 0.0, 0.0, v442_d)
v446_v!!.addColorStop(0.0, jsGet(v437_n, "accent"))
v446_v!!.addColorStop(0.5, jsGet(v437_n, "primary"))
v446_v!!.addColorStop(1.0, "#000")
v428_e.fillStyle = v446_v!!
v428_e.beginPath()
v428_e.arc(0.0, 0.0, v442_d, 0.0, (Math.PI * 2.0))
v428_e.fill()
v428_e.rotate(v445_S)
js_forEach(jsArr(2.6, 2.1, 1.7)) L52@{ v450_T: Any?, v451_b: Double, _ ->

v428_e.strokeStyle = (if (((v451_b % 2.0) == 0.0)) jsGet(v437_n, "primary") else jsGet(v437_n, "accent"))
v428_e.lineWidth = (if ((v451_b == 0.0)) 3.5 else 2.0)
v428_e.beginPath()
v428_e.ellipse(0.0, 0.0, (v442_d * num(v450_T)), ((v442_d * num(v450_T)) * 0.3), 0.0, 0.0, Math.PI)
v428_e.stroke()
null
}
v428_e.restore()
drawPerspectiveFloor(v428_e, v429_a, v430_o, v431_r, v432_t, v434_u, v433_l, v435_f, v437_n, "ocean")
drawPerspectiveCorridor(7.0, 0.45, v434_u, JsFn L53@{ args ->
var v456_w: Double = 0.0
var v457_A: Double = 0.0
var v458_C: Double = 0.0
var v452_T: Any? = args.getOrNull(0)
var v453_b: Any? = args.getOrNull(1)
var v454_h: Any? = args.getOrNull(2)
var v455_p: Any? = args.getOrNull(3)
v456_w = (v432_t + (num(v453_b) * (v430_o - v432_t)))
v457_A = (kotlin.math.sin(((v433_l * 2.5) + (num(v452_T) * 5.0))) * (12.0 * num(v453_b)))
v458_C = (10.0 + (num(v453_b) * 42.0))
js_forEach(jsArr(-1.0, 1.0)) L54@{ v459_E: Any?, _, _ ->
var v460_k: Double = 0.0

v460_k = (v431_r + (num(v459_E) * ((v429_a * 0.16) + ((num(v453_b) * v429_a) * 0.38))))
v428_e.save()
v428_e.globalAlpha = num(v454_h)
drawFloatingMonolith(v428_e, v460_k, ((v456_w - (v458_C * 1.5)) + v457_A), v458_C, ((v433_l * 1.2) + num(v455_p)), v435_f, v437_n)
v428_e.restore()
null
}
null
})
}

fun renderScene4_FujiCanyon(v461_e: Ctx, v462_a: Double, v463_o: Double, v464_r: Double, v465_t: Double, v466_l: Double, v467_u: Double, v468_f: Double, v469_M: Double, v470_n: Any?): Unit {

val v471_s: Double = ((jsMin(v462_a, v463_o) * 0.23) * (1.0 + (v468_f * 0.12)))
drawRetroSun(v461_e, v464_r, (v465_t - (v471_s * 0.3)), v471_s, v466_l, v468_f, v470_n, true)
val v472_i: Double = (v462_a * 0.72)
val v473_g: Double = ((v463_o * 0.32) + (if (truthy(jsGet(g_bgLayers, "skySpectrum"))) (v468_f * 28.0) else 0.0))
v461_e.fillStyle = jsGet(v470_n, "mtnBot")
v461_e.strokeStyle = jsGet(v470_n, "primary")
v461_e.lineWidth = 2.0
v461_e.beginPath()
v461_e.moveTo((v464_r - (v472_i * 0.5)), v465_t)
v461_e.quadraticCurveTo((v464_r - (v472_i * 0.16)), (v465_t - (v473_g * 0.35)), (v464_r - (v472_i * 0.07)), (v465_t - v473_g))
v461_e.lineTo((v464_r + (v472_i * 0.07)), (v465_t - v473_g))
v461_e.quadraticCurveTo((v464_r + (v472_i * 0.16)), (v465_t - (v473_g * 0.35)), (v464_r + (v472_i * 0.5)), v465_t)
v461_e.closePath()
v461_e.fill()
v461_e.stroke()
v461_e.strokeStyle = jsGet(v470_n, "secondary")
v461_e.globalAlpha = 0.6
v461_e.lineWidth = 1.1
v461_e.beginPath()
run {
var v474_m: Double = -5.0
L55@ while ((v474_m <= 5.0)) {
v461_e.moveTo((v464_r + (((v474_m / 5.0) * v472_i) * 0.07)), (v465_t - v473_g))
v461_e.lineTo((v464_r + (((v474_m / 5.0) * v472_i) * 0.48)), v465_t)
v474_m++
}
}
v461_e.stroke()
v461_e.globalAlpha = 1.0
drawPerspectiveFloor(v461_e, v462_a, v463_o, v464_r, v465_t, v467_u, v466_l, v468_f, v470_n, "checkered")
drawPerspectiveCorridor(5.0, 0.4, v467_u, JsFn L56@{ args ->
var v475_m: Any? = args.getOrNull(0)
var v476_d: Any? = args.getOrNull(1)
var v477_y: Any? = args.getOrNull(2)
var v478_c: Any? = args.getOrNull(3)
val v479_S: Double = (v465_t + (num(v476_d) * (v463_o - v465_t)))
val v480_v: Double = ((0.14 + (num(v476_d) * 1.15)) * (jsMin(v462_a, v463_o) / 760.0))
v461_e.save()
v461_e.globalAlpha = num(v477_y)
v461_e.translate((v464_r + ((Rt.mouseX * 18.0) * num(v476_d))), v479_S)
v461_e.scale(v480_v, v480_v)
v461_e.fillStyle = (if (((num(v478_c) % 2.0) == 0.0)) jsGet(v470_n, "secondary") else jsGet(v470_n, "sunSlice"))
v461_e.strokeStyle = jsGet(v470_n, "primary")
v461_e.lineWidth = 2.0
v461_e.fillRect(-130.0, -170.0, 16.0, 170.0)
v461_e.strokeRect(-130.0, -170.0, 16.0, 170.0)
v461_e.fillRect(114.0, -170.0, 16.0, 170.0)
v461_e.strokeRect(114.0, -170.0, 16.0, 170.0)
v461_e.fillStyle = jsGet(v470_n, "secondary")
v461_e.strokeStyle = jsGet(v470_n, "accent")
v461_e.fillRect(-168.0, -186.0, 336.0, 16.0)
v461_e.strokeRect(-168.0, -186.0, 336.0, 16.0)
v461_e.fillRect(-144.0, -150.0, 288.0, 12.0)
v461_e.strokeRect(-144.0, -150.0, 288.0, 12.0)
v461_e.restore()
null
})
if (truthy(jsGet(g_bgLayers, "monoliths"))) {
v461_e.fillStyle = jsGet(v470_n, "accent")
js_forEach(g_SAKURA_PETALS) L57@{ v481_m: Any?, _, _ ->

jsSet(v481_m, "x", ((num(jsGet(v481_m, "x")) + (0.0016 * num(jsGet(v481_m, "z")))) % 1.0))
jsSet(v481_m, "y", ((num(jsGet(v481_m, "y")) + ((0.002 + (v469_M * 0.005)) * num(jsGet(v481_m, "z")))) % 1.0))
v461_e.fillRect((num(jsGet(v481_m, "x")) * v462_a), (num(jsGet(v481_m, "y")) * v463_o), (4.5 * num(jsGet(v481_m, "z"))), (4.5 * num(jsGet(v481_m, "z"))))
null
}
}
}

var g_TESSERACT_VERTS: Any? = null

var g_TESSERACT_EDGES: Any? = null

fun renderScene5_HyperAtrium(v486_e: Ctx, v487_a: Double, v488_o: Double, v489_r: Double, v490_t: Double, v491_l: Double, v492_u: Double, v493_f: Double, v494_M: Double, v495_n: Any?): Unit {

val v496_s: Double = 0.0
val v497_i: Double = 18.0
v486_e.save()
v486_e.strokeStyle = jsGet(v495_n, "grid")
v486_e.lineWidth = 1.25
run {
var v518_I: Double = -v497_i
L61@ while ((v518_I <= v497_i)) {
val v519_U_: Double = (v489_r + (v518_I * (v487_a * 0.095)))
val v520_B: Double = (v489_r + (v518_I * (v487_a * 0.016)))
v486_e.beginPath()
v486_e.moveTo(v519_U_, v496_s)
v486_e.quadraticCurveTo((v489_r + (v518_I * (v487_a * 0.042))), (v490_t * 0.45), v520_B, v490_t)
v486_e.stroke()
v518_I++
}
}
val v498_g: Double = 10.0
val v499_m: Double = v492_u
val v500_d: Double = rrSceneMod(v499_m, 1.0)
val v501_y: Double = kotlin.math.floor(v499_m)
run {
var v521_I: Double = 0.0
L62@ while ((v521_I < v498_g)) {
val v522_U_: Double = ((v521_I + v500_d) / v498_g)
val v523_B: Double = Math.pow(v522_U_, 1.85)
val v524_L: Double = (v490_t - (v523_B * (v490_t * 0.92)))
val v525_G: Double = (v487_a * (0.28 + (v523_B * 0.95)))
val v526_U: Double = (22.0 + (v523_B * 65.0))
val v527_V: Double = (rrSceneSmoothstep(0.012, 0.14, v522_U_) * (1.0 - rrSceneSmoothstep(0.84, 0.985, v522_U_)))
v486_e.strokeStyle = (if ((((((v521_I - v501_y) % 2.0) + 2.0) % 2.0) == 0.0)) jsGet(v495_n, "primary") else jsGet(v495_n, "secondary"))
v486_e.globalAlpha = (v527_V * 0.58)
v486_e.lineWidth = (1.0 + (v523_B * 2.2))
v486_e.beginPath()
v486_e.moveTo((v489_r - (v525_G * 0.5)), (v524_L + v526_U))
v486_e.quadraticCurveTo(v489_r, (v524_L - (v526_U * 0.6)), (v489_r + (v525_G * 0.5)), (v524_L + v526_U))
v486_e.stroke()
v521_I++
}
}
v486_e.restore()
if (truthy(jsGet(g_bgLayers, "skySpectrum"))) {
val v528_I: Double = 11.0
val v529_U_: Double = (v487_a * 0.76)
val v530_B: Double = (v489_r - (v529_U_ / 2.0))
val v531_L: Double = (v529_U_ / v528_I)
run {
var v532_G: Double = 0.0
L63@ while ((v532_G < v528_I)) {
val v533_U: Double = (v530_B + (v532_G * v531_L))
val v534_V: Double = ((Rt.freqDataArr[jsIdx((v532_G * 4.0))] / 255.0) * (v488_o * 0.09))
val v535_D: Double = ((v488_o * 0.1) + v534_V)
v486_e.fillStyle = jsGet(v495_n, "mtnBot")
v486_e.strokeStyle = (if (((v532_G % 2.0) == 0.0)) jsGet(v495_n, "primary") else jsGet(v495_n, "secondary"))
v486_e.lineWidth = 1.4
v486_e.fillRect((v533_U + 1.0), (v490_t - v535_D), (v531_L - 2.0), v535_D)
v486_e.strokeRect((v533_U + 1.0), (v490_t - v535_D), (v531_L - 2.0), v535_D)
v532_G++
}
}
}
drawPerspectiveFloor(v486_e, v487_a, v488_o, v489_r, v490_t, v492_u, v491_l, v493_f, v495_n, "checkered")
drawPerspectiveCorridor(6.0, 0.4, v492_u, JsFn L64@{ args ->
var v536_I: Any? = args.getOrNull(0)
var v537_U_: Any? = args.getOrNull(1)
var v538_B: Any? = args.getOrNull(2)
var v539_L: Any? = args.getOrNull(3)
val v540_G: Double = (v490_t + (num(v537_U_) * (v488_o - v490_t)))
val v541_U: Double = (v490_t - (num(v537_U_) * (v490_t * 0.86)))
val v542_V: Double = (v540_G - v541_U)
val v543_D: Double = (8.0 + (num(v537_U_) * 30.0))
val v544_K: Double = (v540_G - (v542_V * 0.52))
js_forEach(jsArr(-1.0, 1.0)) L65@{ v545_S_: Any?, _, _ ->

val v546_z: Double = ((v489_r + (num(v545_S_) * ((v487_a * 0.14) + ((num(v537_U_) * v487_a) * 0.36)))) - (v543_D / 2.0))
v486_e.save()
v486_e.globalAlpha = num(v538_B)
val v547_X: Grad = v486_e.createLinearGradient(v546_z, v541_U, (v546_z + v543_D), v541_U)
v547_X.addColorStop(0.0, jsGet(v495_n, "sunSlice"))
v547_X.addColorStop(0.5, jsGet(v495_n, "mtnTop"))
v547_X.addColorStop(1.0, jsGet(v495_n, "sunSlice"))
v486_e.fillStyle = v547_X
v486_e.strokeStyle = (if (((num(v539_L) % 2.0) == 0.0)) jsGet(v495_n, "primary") else jsGet(v495_n, "secondary"))
v486_e.lineWidth = 1.5
v486_e.fillRect(v546_z, v541_U, v543_D, v542_V)
v486_e.strokeRect(v546_z, v541_U, v543_D, v542_V)
val v548_q: Double = ((30.0 + (num(v537_U_) * 95.0)) * num(v545_S_))
v486_e.fillStyle = jsGet(v495_n, "sunSlice")
v486_e.strokeStyle = jsGet(v495_n, "accent")
v486_e.lineWidth = 1.6
v486_e.beginPath()
v486_e.rect((v546_z + (v543_D * 0.5)), v544_K, v548_q, (5.0 + (num(v537_U_) * 10.0)))
v486_e.fill()
v486_e.stroke()
if (truthy(jsGet(g_bgLayers, "monoliths"))) {
val v549_Q: Double = ((v546_z + (v543_D * 0.5)) - (num(v545_S_) * (22.0 * num(v537_U_))))
val v550_W: Double = (6.0 + (num(v537_U_) * 18.0))
val v551_te: Double = (8.0 + (num(v537_U_) * 24.0))
v486_e.fillStyle = jsGet(v495_n, "mtnBot")
v486_e.strokeStyle = jsGet(v495_n, "primary")
v486_e.fillRect((v549_Q - (v550_W * 0.5)), (v540_G - v551_te), v550_W, v551_te)
v486_e.strokeRect((v549_Q - (v550_W * 0.5)), (v540_G - v551_te), v550_W, v551_te)
drawFloatingMonolith(v486_e, v549_Q, (((v540_G - v551_te) - (10.0 + (num(v537_U_) * 20.0))) + ((kotlin.math.sin(((v491_l * 2.4) + num(v539_L))) * 4.0) * num(v537_U_))), (5.0 + (num(v537_U_) * 14.0)), ((v491_l * 1.2) + num(v539_L)), v493_f, v495_n)
}
v486_e.restore()
null
}
null
})
val v502_c: Double = ((v490_t - (v488_o * 0.16)) + (kotlin.math.sin((v491_l * 1.6)) * 8.0))
val v503_S: Double = ((jsMin(v487_a, v488_o) * 0.115) * (1.0 + (v493_f * 0.22)))
val v504_v: Grad = v486_e.createRadialGradient(v489_r, v502_c, (v503_S * 0.1), v489_r, v502_c, (v503_S * 2.2))
v504_v.addColorStop(0.0, ("rgba(" + str(jsGet(v495_n, "waveRgb")) + ", " + jsStr((0.42 + (v493_f * 0.28))) + ")"))
v504_v.addColorStop(0.5, ("rgba(" + str(jsGet(v495_n, "altWaveRgb")) + ", 0.22)"))
v504_v.addColorStop(1.0, "transparent")
v486_e.fillStyle = v504_v
v486_e.beginPath()
v486_e.arc(v489_r, v502_c, (v503_S * 2.2), 0.0, (Math.PI * 2.0))
v486_e.fill()
val v505_T: Double = (v491_l * 0.85)
val v506_b: Double = (v491_l * 0.62)
val v507_h: Double = (v491_l * 0.74)
val v508_p: Double = 0.35
val v509_w: Double = kotlin.math.cos(v505_T)
val v510_A: Double = kotlin.math.sin(v505_T)
val v511_C: Double = kotlin.math.cos(v506_b)
val v512_E: Double = kotlin.math.sin(v506_b)
val v513_k: Double = kotlin.math.cos(v507_h)
val v514_R: Double = kotlin.math.sin(v507_h)
val v515_F: Double = kotlin.math.cos(v508_p)
val v516_O: Double = kotlin.math.sin(v508_p)
val v517_P: Any? = js_map(g_TESSERACT_VERTS) L66@{ p67: Any?, _, _ ->
var v552_I: Any? = jsGet(p67, 0.0)
var v553_U_: Any? = jsGet(p67, 1.0)
var v554_B: Any? = jsGet(p67, 2.0)
var v555_L: Any? = jsGet(p67, 3.0)
val v556_G: Double = ((num(v552_I) * v509_w) - (num(v555_L) * v510_A))
val v557_U: Double = ((num(v552_I) * v510_A) + (num(v555_L) * v509_w))
val v558_V: Double = ((num(v553_U_) * v511_C) - (v557_U * v512_E))
val v559_D: Double = ((num(v553_U_) * v512_E) + (v557_U * v511_C))
val v560_K: Double = (2.6 / (2.6 - (v559_D * 0.55)))
val v561_S_: Double = (v556_G * v560_K)
val v562_z: Double = (v558_V * v560_K)
val v563_X: Double = (num(v554_B) * v560_K)
val v564_q: Double = ((v561_S_ * v513_k) - (v563_X * v514_R))
val v565_Q: Double = ((v561_S_ * v514_R) + (v563_X * v513_k))
val v566_W: Double = ((v562_z * v515_F) - (v565_Q * v516_O))
val v567_te: Double = ((v562_z * v516_O) + (v565_Q * v515_F))
val v568_le: Double = (3.5 / (3.5 + (v567_te * 0.45)))
return@L66 jsObj("px" to (v489_r + ((v564_q * v503_S) * v568_le)), "py" to (v502_c + ((v566_W * v503_S) * v568_le)), "w2" to v559_D)
null
}
v486_e.save()
js_forEach(g_TESSERACT_EDGES) L68@{ p69: Any?, v571_B: Double, _ ->
var v569_I: Any? = jsGet(p69, 0.0)
var v570_U_: Any? = jsGet(p69, 1.0)
val v572_L: Any? = jsGet(v517_P, v569_I)
val v573_G: Any? = jsGet(v517_P, v570_U_)
val v574_U: Boolean = ((num(jsGet(v572_L, "w2")) + num(jsGet(v573_G, "w2"))) > 0.0)
v486_e.strokeStyle = (if (v574_U) jsGet(v495_n, "accent") else (if (((v571_B % 2.0) == 0.0)) jsGet(v495_n, "primary") else jsGet(v495_n, "secondary")))
v486_e.lineWidth = (if (v574_U) 2.2 else 1.5)
v486_e.beginPath()
v486_e.moveTo(num(jsGet(v572_L, "px")), num(jsGet(v572_L, "py")))
v486_e.lineTo(num(jsGet(v573_G, "px")), num(jsGet(v573_G, "py")))
v486_e.stroke()
null
}
js_forEach(v517_P) L70@{ v575_I: Any?, _, _ ->

v486_e.fillStyle = (if ((num(jsGet(v575_I, "w2")) > 0.0)) "#ffffff" else jsGet(v495_n, "primary"))
v486_e.beginPath()
v486_e.arc(num(jsGet(v575_I, "px")), num(jsGet(v575_I, "py")), (if ((num(jsGet(v575_I, "w2")) > 0.0)) 3.2 else 2.2), 0.0, (Math.PI * 2.0))
v486_e.fill()
null
}
v486_e.restore()
}

fun renderScene6_ArcadeOdyssey(v576_e: Ctx, v577_a: Double, v578_o: Double, v579_r: Double, v580_t: Double, v581_l: Double, v582_u: Double, v583_f: Double, v584_M: Double, v585_n: Any?): Unit {

if (truthy(jsGet(g_bgLayers, "skySpectrum"))) {
val v595_T: Double = 14.0
run {
var v596_b: Double = 0.0
L71@ while ((v596_b < v595_T)) {
val v597_h: Double = ((v596_b / (v595_T - 1.0)) - 0.5)
val v598_p: Double = ((Rt.freqDataArr[jsIdx((v596_b * 4.0))] / 255.0) * (v578_o * 0.16))
v576_e.strokeStyle = (if (((v596_b % 2.0) == 0.0)) jsGet(v585_n, "primary") else jsGet(v585_n, "secondary"))
v576_e.globalAlpha = 0.6
v576_e.lineWidth = 2.0
v576_e.beginPath()
v576_e.moveTo((v579_r + ((v597_h * v577_a) * 0.88)), v580_t)
v576_e.lineTo((v579_r + ((v597_h * v577_a) * 0.24)), ((v580_t - (v578_o * 0.38)) - v598_p))
v576_e.stroke()
v596_b++
}
}
v576_e.globalAlpha = 1.0
}
drawPerspectiveFloor(v576_e, v577_a, v578_o, v579_r, v580_t, v582_u, v581_l, v583_f, v585_n, "checkered")
val v586_s: Double = ((v580_t - (v578_o * 0.17)) + (kotlin.math.sin((v581_l * 1.6)) * 7.0))
val v587_i: Double = ((jsMin(v577_a, v578_o) * 0.135) * (1.0 + (v583_f * 0.1)))
val v588_g: Double = (kotlin.math.sin((v581_l * 0.75)) * 0.52)
val v589_m: Double = kotlin.math.cos(v588_g)
val v590_d: Double = kotlin.math.sin(v588_g)
fun v591_y(v599_T: Double, v600_b: Double, v601_h: Double): Any? {

val v602_p: Double = ((v599_T * v589_m) - (v601_h * v590_d))
val v603_w: Double = ((v599_T * v590_d) + (v601_h * v589_m))
val v604_A: Double = (3.4 / (3.4 + (v603_w * 0.5)))
return jsObj("x" to (v579_r + ((v602_p * v587_i) * v604_A)), "y" to (v586_s + ((v600_b * v587_i) * v604_A)))
return null
}
val v592_c: Any? = jsArr(v591_y(-1.1, -0.9, -0.6), v591_y(1.1, -0.9, -0.6), v591_y(1.1, 0.8, -0.6), v591_y(-1.1, 0.8, -0.6))
val v593_S: Any? = jsArr(v591_y(-0.75, -0.65, 0.7), v591_y(0.75, -0.65, 0.7), v591_y(0.75, 0.55, 0.7), v591_y(-0.75, 0.55, 0.7))
v576_e.save()
v576_e.fillStyle = "rgba(10, 3, 26, 0.88)"
v576_e.strokeStyle = jsGet(v585_n, "primary")
v576_e.lineWidth = 2.0
v576_e.beginPath()
js_forEach(v592_c) L72@{ v605_T: Any?, v606_b: Double, _ ->

(if ((v606_b == 0.0)) v576_e.moveTo(num(jsGet(v605_T, "x")), num(jsGet(v605_T, "y"))) else v576_e.lineTo(num(jsGet(v605_T, "x")), num(jsGet(v605_T, "y"))))
}
v576_e.closePath()
v576_e.fill()
v576_e.stroke()
v576_e.strokeStyle = jsGet(v585_n, "secondary")
v576_e.lineWidth = 1.4
v576_e.beginPath()
run {
var v607_T: Double = 0.0
L73@ while ((v607_T < 4.0)) {
v576_e.moveTo(num(jsGet(jsGet(v592_c, v607_T), "x")), num(jsGet(jsGet(v592_c, v607_T), "y")))
v576_e.lineTo(num(jsGet(jsGet(v593_S, v607_T), "x")), num(jsGet(jsGet(v593_S, v607_T), "y")))
val v608_b: Double = ((v607_T + 1.0) % 4.0)
v576_e.moveTo(num(jsGet(jsGet(v593_S, v607_T), "x")), num(jsGet(jsGet(v593_S, v607_T), "y")))
v576_e.lineTo(num(jsGet(jsGet(v593_S, v608_b), "x")), num(jsGet(jsGet(v593_S, v608_b), "y")))
v607_T++
}
}
v576_e.stroke()
val v594_v: Any? = jsArr(v591_y(-0.88, -0.68, -0.62), v591_y(0.88, -0.68, -0.62), v591_y(0.88, 0.48, -0.62), v591_y(-0.88, 0.48, -0.62))
v576_e.fillStyle = "#04000c"
v576_e.strokeStyle = jsGet(v585_n, "accent")
v576_e.beginPath()
js_forEach(v594_v) L74@{ v609_T: Any?, v610_b: Double, _ ->

(if ((v610_b == 0.0)) v576_e.moveTo(num(jsGet(v609_T, "x")), num(jsGet(v609_T, "y"))) else v576_e.lineTo(num(jsGet(v609_T, "x")), num(jsGet(v609_T, "y"))))
}
v576_e.closePath()
v576_e.fill()
v576_e.stroke()
v576_e.strokeStyle = jsGet(v585_n, "primary")
v576_e.lineWidth = 2.0
v576_e.beginPath()
run {
var v611_T: Double = 0.0
L75@ while ((v611_T <= 16.0)) {
val v612_b: Any? = v591_y((((v611_T / 16.0) * 1.5) - 0.75), ((((Rt.timeDataArr[jsIdx((v611_T * 4.0))] - 128.0) / 128.0) * 0.35) - 0.1), -0.64)
if ((v611_T == 0.0)) {
v576_e.moveTo(num(jsGet(v612_b, "x")), num(jsGet(v612_b, "y")))
} else {
v576_e.lineTo(num(jsGet(v612_b, "x")), num(jsGet(v612_b, "y")))
}
v611_T++
}
}
v576_e.stroke()
v576_e.restore()
if (truthy(jsGet(g_bgLayers, "monoliths"))) {
js_forEach(jsArr(-1.0, 1.0)) L76@{ v613_T: Any?, v614_b: Double, _ ->

val v615_h: Double = ((v579_r + ((num(v613_T) * v577_a) * 0.28)) + (Rt.mouseX * 16.0))
val v616_p: Double = ((v580_t - (v578_o * 0.22)) + (kotlin.math.sin(((v581_l * 2.0) + v614_b)) * 10.0))
val v617_w: Double = (jsMin(v577_a, v578_o) / 920.0)
v576_e.save()
v576_e.translate(v615_h, v616_p)
v576_e.rotate(((num(v613_T) * 0.22) + (kotlin.math.sin(((v581_l * 1.4) + v614_b)) * 0.12)))
v576_e.scale(v617_w, v617_w)
v576_e.fillStyle = jsGet(v585_n, "sunSlice")
v576_e.strokeStyle = jsGet(v585_n, "primary")
v576_e.lineWidth = 2.0
v576_e.fillRect(-52.0, -32.0, 104.0, 64.0)
v576_e.strokeRect(-52.0, -32.0, 104.0, 64.0)
v576_e.strokeStyle = jsGet(v585_n, "secondary")
v576_e.strokeRect(-40.0, -20.0, 80.0, 28.0)
js_forEach(jsArr(-20.0, 20.0)) L77@{ v618_A: Any?, _, _ ->

v576_e.strokeStyle = jsGet(v585_n, "accent")
v576_e.beginPath()
v576_e.arc(num(v618_A), -6.0, 9.0, 0.0, (Math.PI * 2.0))
v576_e.stroke()
null
}
v576_e.restore()
null
}
}
drawPerspectiveCorridor(6.0, 0.4, v582_u, JsFn L78@{ args ->
var v619_T: Any? = args.getOrNull(0)
var v620_b: Any? = args.getOrNull(1)
var v621_h: Any? = args.getOrNull(2)
var v622_p: Any? = args.getOrNull(3)
val v623_w: Double = (v578_o * (0.14 + (num(v620_b) * 0.44)))
val v624_A: Double = (14.0 + (num(v620_b) * 44.0))
val v625_C: Double = (v580_t + (num(v620_b) * (v578_o - v580_t)))
val v626_E: Double = (v625_C - v623_w)
js_forEach(jsArr(-1.0, 1.0)) L79@{ v627_k: Any?, _, _ ->

val v628_R: Double = ((v579_r + (num(v627_k) * ((v577_a * 0.15) + ((num(v620_b) * v577_a) * 0.36)))) - (v624_A / 2.0))
val v629_F: Double = ((-(num(v627_k)) * v624_A) * 0.35)
v576_e.save()
v576_e.globalAlpha = num(v621_h)
v576_e.fillStyle = "#05010d"
v576_e.strokeStyle = jsGet(v585_n, "secondary")
v576_e.beginPath()
v576_e.moveTo((if ((num(v627_k) > 0.0)) v628_R else (v628_R + v624_A)), v626_E)
v576_e.lineTo(((if ((num(v627_k) > 0.0)) v628_R else (v628_R + v624_A)) + v629_F), (v626_E - 4.0))
v576_e.lineTo(((if ((num(v627_k) > 0.0)) v628_R else (v628_R + v624_A)) + v629_F), (v625_C - 4.0))
v576_e.lineTo((if ((num(v627_k) > 0.0)) v628_R else (v628_R + v624_A)), v625_C)
v576_e.closePath()
v576_e.fill()
v576_e.stroke()
v576_e.fillStyle = jsGet(v585_n, "sunSlice")
v576_e.strokeStyle = (if (((num(v622_p) % 2.0) == 0.0)) jsGet(v585_n, "primary") else jsGet(v585_n, "secondary"))
v576_e.lineWidth = 1.6
v576_e.fillRect(v628_R, v626_E, v624_A, v623_w)
v576_e.strokeRect(v628_R, v626_E, v624_A, v623_w)
v576_e.fillStyle = jsGet(v585_n, "accent")
v576_e.fillRect((v628_R + 2.0), (v626_E + 2.0), (v624_A - 4.0), (v623_w * 0.14))
v576_e.fillStyle = jsGet(v585_n, "secondary")
v576_e.fillRect((v628_R + (v624_A * 0.15)), (v626_E + (v623_w * 0.22)), (v624_A * 0.7), (v623_w * 0.28))
v576_e.restore()
null
}
null
})
}

fun renderScene7_PoseidonSpa(v630_e: Ctx, v631_a: Double, v632_o: Double, v633_r: Double, v634_t: Double, v635_l: Double, v636_u: Double, v637_f: Double, v638_M: Double, v639_n: Any?): Unit {

var v640_s: Double = 0.0
var v641_i: Double = 0.0
var v642_g: Double = 0.0
var v643_m: Double = 0.0
var v644_d: Double = 0.0
var v645_y: Double = 0.0
var v646_c: Double = 0.0
var v647_S: Double = 0.0
var v648_v: Double = 0.0
var v650_b: Double = 0.0
var v651_h: Double = 0.0
var v652_p: Double = 0.0
var v653_w: Double = 0.0
var v654_A: Double = 0.0
var v655_C: Double = 0.0
var v656_E: Double = 0.0
var v657_k: Double = 0.0
var v658_R: Double = 0.0
var v659_F: Double = 0.0
var v660_O: Double = 0.0
var v661_P: Double = 0.0
var v662_I: Double = 0.0
var v663_U_: Double = 0.0
var v664_B: Double = 0.0
var v665_L: Double = 0.0
var v666_G: Double = 0.0
var v667_U: Double = 0.0
var v668_V: Double = 0.0
var v669_D: Double = 0.0
var v670_K: Double = 0.0
var v671_S_: Double = 0.0
var v672_z: Double = 0.0
var v673_X: Double = 0.0
var v674_q: Double = 0.0
var v675_Q: Double = 0.0
var v676_W: Double = 0.0
fun v649_T(v677_te: Double, v678_le: Double, v679_ie: Double, v680_J: Double, v681_de: String, v682_ee: Any?): Unit {

var v683_se: Any? = null
var v684_x: Double = 0.0
var v685_N: Double = 0.0
var v686_ae: Double = 0.0
v683_se = jsArr()
run {
v684_x = 0.0
L80@ while ((v684_x < 10.0)) {
v685_N = ((((Math.PI * 2.0) * v684_x) / 10.0) - (Math.PI / 2.0))
jsPush(v683_se, jsArr(((kotlin.math.cos(v685_N) * v678_le) * v648_v), ((v677_te * v648_v) + ((kotlin.math.sin(v685_N) * v679_ie) * v648_v))))
v684_x++
}
}
v630_e.fillStyle = "#111b2b"
v630_e.strokeStyle = v682_ee
v630_e.lineWidth = 1.6
v630_e.beginPath()
js_forEach(v683_se) L81@{ v687_j: Any?, v688_oe: Double, _ ->

if (!(truthy(v688_oe))) {
v630_e.moveTo(num(jsGet(v687_j, 0.0)), num(jsGet(v687_j, 1.0)))
} else {
v630_e.lineTo(num(jsGet(v687_j, 0.0)), num(jsGet(v687_j, 1.0)))
}
null
}
run {
v686_ae = (jsLen(v683_se) - 1.0)
L82@ while ((v686_ae >= 0.0)) {
v630_e.lineTo((num(jsGet(jsGet(v683_se, v686_ae), 0.0)) * 0.88), (num(jsGet(jsGet(v683_se, v686_ae), 1.0)) + (v680_J * v648_v)))
v686_ae--
}
}
v630_e.closePath()
v630_e.fill()
v630_e.stroke()
v630_e.fillStyle = v681_de
v630_e.strokeStyle = v682_ee
v630_e.lineWidth = 2.1
v630_e.beginPath()
js_forEach(v683_se) L83@{ v689_j: Any?, v690_oe: Double, _ ->

if (!(truthy(v690_oe))) {
v630_e.moveTo(num(jsGet(v689_j, 0.0)), num(jsGet(v689_j, 1.0)))
} else {
v630_e.lineTo(num(jsGet(v689_j, 0.0)), num(jsGet(v689_j, 1.0)))
}
null
}
v630_e.closePath()
v630_e.fill()
v630_e.stroke()
v630_e.fillStyle = "rgba(48,190,220,0.22)"
v630_e.strokeStyle = "rgba(125,235,255,0.68)"
v630_e.lineWidth = 1.0
v630_e.beginPath()
js_forEach(v683_se) L84@{ v691_j: Any?, v692_oe: Double, _ ->
var v693_Y: Double = 0.0
var v694_ne: Double = 0.0

v693_Y = (num(jsGet(v691_j, 0.0)) * 0.72)
v694_ne = ((v677_te * v648_v) + ((num(jsGet(v691_j, 1.0)) - (v677_te * v648_v)) * 0.68))
if (!(truthy(v692_oe))) {
v630_e.moveTo(v693_Y, v694_ne)
} else {
v630_e.lineTo(v693_Y, v694_ne)
}
null
}
v630_e.closePath()
v630_e.fill()
v630_e.stroke()
}
v640_s = 15.0
v641_i = (v631_a * 0.95)
v642_g = (v633_r - (v641_i / 2.0))
v643_m = (v641_i / v640_s)
run {
v644_d = 0.0
L85@ while ((v644_d < v640_s)) {
v645_y = (v642_g + (v644_d * v643_m))
v646_c = ((v632_o * 0.14) + ((Rt.freqDataArr[jsIdx((v644_d * 3.0))] / 255.0) * (v632_o * 0.08)))
v630_e.fillStyle = jsGet(v639_n, "mtnBot")
v630_e.strokeStyle = jsGet(v639_n, "primary")
v630_e.lineWidth = 1.6
v630_e.beginPath()
v630_e.moveTo(v645_y, v634_t)
v630_e.lineTo(v645_y, (v634_t - v646_c))
v630_e.lineTo((v645_y + v643_m), (v634_t - v646_c))
v630_e.lineTo((v645_y + v643_m), v634_t)
v630_e.lineTo((v645_y + (v643_m * 0.88)), (v634_t - (v646_c * 0.38)))
v630_e.lineTo((v645_y + (v643_m * 0.5)), (v634_t - (v646_c * 0.52)))
v630_e.lineTo((v645_y + (v643_m * 0.12)), (v634_t - (v646_c * 0.38)))
v630_e.closePath()
v630_e.fill()
v630_e.stroke()
v644_d++
}
}
drawPerspectiveFloor(v630_e, v631_a, v632_o, v633_r, v634_t, v636_u, v635_l, v637_f, v639_n, "checkered")
v647_S = (v634_t + ((v632_o - v634_t) * 0.28))
v648_v = (jsMin(v631_a, v632_o) / 800.0)
v630_e.save()
v630_e.translate(v633_r, v647_S)
v649_T(0.0, 142.0, 34.0, 20.0, "#283b50", jsGet(v639_n, "primary"))
v649_T(-46.0, 96.0, 24.0, 15.0, "#24364a", jsGet(v639_n, "accent"))
v649_T(-88.0, 54.0, 16.0, 11.0, "#31465b", jsGet(v639_n, "primary"))
v630_e.fillStyle = "#18283b"
v630_e.strokeStyle = jsGet(v639_n, "accent")
v630_e.lineWidth = 2.0
v630_e.beginPath()
v630_e.moveTo((-22.0 * v648_v), (-88.0 * v648_v))
v630_e.lineTo((-15.0 * v648_v), (-128.0 * v648_v))
v630_e.lineTo(0.0, (-140.0 * v648_v))
v630_e.lineTo((15.0 * v648_v), (-128.0 * v648_v))
v630_e.lineTo((22.0 * v648_v), (-88.0 * v648_v))
v630_e.closePath()
v630_e.fill()
v630_e.stroke()
v630_e.fillStyle = "#62cfe7"
v630_e.strokeStyle = "#d7fbff"
v630_e.lineWidth = 1.4
v630_e.beginPath()
v630_e.moveTo(0.0, (-166.0 * v648_v))
v630_e.lineTo((13.0 * v648_v), (-143.0 * v648_v))
v630_e.lineTo(0.0, (-132.0 * v648_v))
v630_e.lineTo((-13.0 * v648_v), (-143.0 * v648_v))
v630_e.closePath()
v630_e.fill()
v630_e.stroke()
v650_b = (-238.0 * v648_v)
v651_h = (-160.0 * v648_v)
run {
v652_p = 0.0
L86@ while ((v652_p < 9.0)) {
v653_w = ((v652_p - 4.0) / 4.0)
v654_A = ((v635_l * (2.1 + ((v652_p % 3.0) * 0.22))) + (v652_p * 1.7))
v655_C = ((kotlin.math.sin(v654_A) * 3.5) * v648_v)
v642_g = ((v653_w * 3.2) * v648_v)
v656_E = (((v653_w * (9.0 + (kotlin.math.abs(v653_w) * 10.0))) * v648_v) + v655_C)
v657_k = (((v653_w * (17.0 + (kotlin.math.abs(v653_w) * 19.0))) * v648_v) + (v655_C * 1.4))
v658_R = ((v650_b + ((kotlin.math.sin((v654_A * 0.7)) * 4.0) * v648_v)) - ((v637_f * 8.0) * v648_v))
v630_e.globalAlpha = (0.62 + (0.2 * kotlin.math.sin((v654_A * 1.3))))
v630_e.strokeStyle = (if (((v652_p % 3.0) == 0.0)) "#e7fdff" else "#65dff5")
v630_e.lineWidth = ((if (((v652_p % 3.0) == 0.0)) 2.1 else 1.2) * v648_v)
v630_e.beginPath()
v630_e.moveTo(v642_g, v651_h)
v630_e.lineTo(v656_E, v658_R)
v630_e.lineTo(v657_k, (-183.0 * v648_v))
v630_e.stroke()
v659_F = (((v635_l * (0.7 + (v652_p * 0.035))) + (v652_p * 0.113)) % 1.0)
v660_O = (v656_E + ((v657_k - v656_E) * v659_F))
v661_P = ((v658_R + (((-183.0 * v648_v) - v658_R) * v659_F)) + ((kotlin.math.sin((v659_F * Math.PI)) * 7.0) * v648_v))
v662_I = ((1.4 + ((v652_p % 3.0) * 0.45)) * v648_v)
v630_e.fillStyle = "#d9fbff"
v630_e.globalAlpha = (0.45 + (0.45 * kotlin.math.sin((v659_F * Math.PI))))
v630_e.beginPath()
v630_e.moveTo(v660_O, (v661_P - v662_I))
v630_e.lineTo((v660_O + (v662_I * 0.65)), v661_P)
v630_e.lineTo(v660_O, (v661_P + v662_I))
v630_e.lineTo((v660_O - (v662_I * 0.65)), v661_P)
v630_e.closePath()
v630_e.fill()
v652_p++
}
}
run {
v663_U_ = 0.0
L87@ while ((v663_U_ < 8.0)) {
v664_B = (((if (truthy((v663_U_ % 2.0))) 1.0 else -1.0) * (60.0 + (kotlin.math.floor((v663_U_ / 2.0)) * 12.0))) * v648_v)
v665_L = ((-42.0 - ((v663_U_ % 3.0) * 2.0)) * v648_v)
v666_G = ((kotlin.math.sin(((v635_l * 2.6) + v663_U_)) * 2.0) * v648_v)
v630_e.globalAlpha = (0.4 + (0.18 * kotlin.math.sin(((v635_l * 2.0) + v663_U_))))
v630_e.strokeStyle = "#7fe8f8"
v630_e.lineWidth = (1.6 * v648_v)
v630_e.beginPath()
v630_e.moveTo(v664_B, v665_L)
v630_e.lineTo((v664_B + v666_G), (v665_L + (21.0 * v648_v)))
v630_e.lineTo((v664_B + (v666_G * 1.5)), (v665_L + (37.0 * v648_v)))
v630_e.stroke()
v663_U_++
}
}
v630_e.globalAlpha = 1.0
v630_e.restore()
drawPerspectiveCorridor(7.0, 0.45, v636_u, JsFn L88@{ args ->
var v698_J: Double = 0.0
var v699_de: Double = 0.0
var v700_ee: Double = 0.0
var v701_se: Double = 0.0
var v695_te: Any? = args.getOrNull(0)
var v696_le: Any? = args.getOrNull(1)
var v697_ie: Any? = args.getOrNull(2)
v698_J = (v632_o * (0.12 + (num(v696_le) * 0.35)))
v699_de = (12.0 + (num(v696_le) * 38.0))
v700_ee = (v634_t + (num(v696_le) * (v632_o - v634_t)))
v701_se = (v700_ee - v698_J)
js_forEach(jsArr(-1.0, 1.0)) L89@{ v702_x: Any?, _, _ ->
var v703_N: Double = 0.0

v703_N = ((v633_r + (num(v702_x) * ((v631_a * 0.18) + ((num(v696_le) * v631_a) * 0.38)))) - (v699_de / 2.0))
v630_e.save()
v630_e.globalAlpha = num(v697_ie)
v630_e.fillStyle = "#18283b"
v630_e.strokeStyle = jsGet(v639_n, "primary")
v630_e.lineWidth = 1.8
v630_e.beginPath()
v630_e.moveTo(v703_N, v700_ee)
v630_e.lineTo(v703_N, (v701_se + (v699_de * 0.22)))
v630_e.lineTo((v703_N + (v699_de * 0.5)), v701_se)
v630_e.lineTo((v703_N + v699_de), (v701_se + (v699_de * 0.22)))
v630_e.lineTo((v703_N + v699_de), v700_ee)
v630_e.closePath()
v630_e.fill()
v630_e.stroke()
v630_e.restore()
null
}
null
})
v630_e.save()
run {
v667_U = 0.0
L90@ while ((v667_U < 9.0)) {
v668_V = ((v633_r - (v631_a * 0.52)) + ((v667_U * v631_a) * 0.13))
v630_e.globalAlpha = (0.12 + ((v667_U % 3.0) * 0.035))
v630_e.strokeStyle = (if (truthy((v667_U % 2.0))) jsGet(v639_n, "accent") else jsGet(v639_n, "primary"))
v630_e.lineWidth = (1.0 + ((v667_U % 3.0) * 0.6))
v630_e.beginPath()
v630_e.moveTo((v633_r + ((v668_V - v633_r) * 0.08)), (v634_t - (v632_o * 0.04)))
v630_e.lineTo(v668_V, (v634_t - (v632_o * (0.24 + ((v667_U % 3.0) * 0.025)))))
v630_e.stroke()
v667_U++
}
}
run {
v669_D = 1.0
L91@ while ((v669_D <= 5.0)) {
v670_K = (v634_t - (v632_o * (0.035 + (v669_D * 0.036))))
v671_S_ = (0.12 + (v669_D * 0.145))
v630_e.globalAlpha = (0.1 + (v669_D * 0.018))
v630_e.strokeStyle = (if (truthy((v669_D % 2.0))) jsGet(v639_n, "primary") else jsGet(v639_n, "secondary"))
v630_e.lineWidth = (1.0 + (v669_D * 0.25))
v630_e.beginPath()
v630_e.moveTo((v633_r - (v631_a * v671_S_)), v670_K)
v630_e.lineTo((v633_r + (v631_a * v671_S_)), v670_K)
v630_e.stroke()
v669_D++
}
}
v630_e.restore()
v630_e.save()
v630_e.globalAlpha = 0.24
v630_e.fillStyle = "#0b2437"
v630_e.strokeStyle = "rgba(110,225,245,0.35)"
v630_e.lineWidth = 1.2
v630_e.beginPath()
v630_e.moveTo((v633_r - (v631_a * 0.12)), (v634_t + (v632_o * 0.54)))
v630_e.lineTo((v633_r + (v631_a * 0.12)), (v634_t + (v632_o * 0.54)))
v630_e.lineTo((v633_r + (v631_a * 0.46)), (v632_o * 0.98))
v630_e.lineTo((v633_r - (v631_a * 0.46)), (v632_o * 0.98))
v630_e.closePath()
v630_e.fill()
v630_e.stroke()
v630_e.globalAlpha = 0.22
v630_e.strokeStyle = "#8eeeff"
run {
v672_z = 0.0
L92@ while ((v672_z < 9.0)) {
v673_X = ((v672_z + 1.0) / 10.0)
v674_q = (v634_t + (v632_o * (0.56 + (v673_X * 0.4))))
v675_Q = (v631_a * (0.035 + (v673_X * 0.12)))
v676_W = (v633_r + (((kotlin.math.sin(((v672_z * 2.1) + (v635_l * 0.22))) * v631_a) * 0.18) * v673_X))
v630_e.beginPath()
v630_e.moveTo((v676_W - v675_Q), v674_q)
v630_e.lineTo((v676_W + v675_Q), v674_q)
v630_e.stroke()
v672_z++
}
}
v630_e.restore()
}

fun renderScene8_ChronoDrift(v704_e: Ctx, v705_a: Double, v706_o: Double, v707_r: Double, v708_t: Double, v709_l: Double, v710_u: Double, v711_f: Double, v712_M: Double, v713_n: Any?): Unit {

var v714_s: Double = 0.0
var v715_i: Double = 0.0
var v716_g: Double = 0.0
var v717_m: Double = 0.0
var v718_d: Double = 0.0
var v719_y: Double = 0.0
var v720_c: Double = 0.0
var v721_S: Double = 0.0
var v722_v: Double = 0.0
var v723_T: Double = 0.0
var v724_b: Double = 0.0
var v725_h: Double = 0.0
var v726_p: Double = 0.0
var v727_w: Double = 0.0
var v728_A: Double = 0.0
var v729_C: Boolean = false
var v730_E: Double = 0.0
var v731_k: Any? = null
var v732_R: Double = 0.0
var v733_F: Double = 0.0
var v734_O: Double = 0.0
var v735_P: Double = 0.0
var v736_I: Double = 0.0
var v737_U_: Double = 0.0
var v738_B: Double = 0.0
var v739_L: Double = 0.0
var v740_G: Double = 0.0
var v741_U: Double = 0.0
var v742_V: Double = 0.0
var v743_D: Double = 0.0
var v744_K: Double = 0.0
var v745_S_: Double = 0.0
var v746_z: Double = 0.0
var v747_X: Double = 0.0
var v748_q: Double = 0.0
var v749_Q: Double = 0.0
var v750_W: Double = 0.0
var v751_te: Double = 0.0
var v752_le: Double = 0.0
var v753_ie: Double = 0.0
var v754_J: Double = 0.0
var v755_de: Double = 0.0
var v756_ee: Double = 0.0
var v757_se: Double = 0.0
var v758_x: Double = 0.0
v714_s = jsMax(0.0, jsMin(2.0, jsOrN(num(Rt.win("__rrVisualQualityStage"))) { 0.0 }))
v715_i = (if ((v714_s == 0.0)) 1.0 else (if ((v714_s == 1.0)) 0.75 else 0.5))
v716_g = jsMin(v705_a, v706_o)
v717_m = (if (("number" == "number")) Rt.mouseX else 0.0)
v718_d = (Math.PI * 2.0)
v704_e.save()
v704_e.fillStyle = jsGet(v713_n, "accent")
v719_y = jsRound((70.0 * v715_i))
run {
v720_c = 0.0
L93@ while ((v720_c < v719_y)) {
v721_S = (((((v720_c * 197.31) - ((v717_m * 8.0) * (1.0 + (v720_c % 3.0)))) % v705_a) + v705_a) % v705_a)
v722_v = ((v720_c * 83.71) % (v708_t * 0.82))
v704_e.globalAlpha = (0.18 + (0.32 * (0.5 + (0.5 * kotlin.math.sin(((v709_l * 0.55) + (v720_c * 1.7)))))))
v723_T = (if (((v720_c % 9.0) == 0.0)) 2.2 else 1.1)
v704_e.fillRect(v721_S, v722_v, v723_T, v723_T)
v720_c++
}
}
v704_e.restore()
v724_b = (v707_r - (v717_m * 12.0))
v725_h = (v708_t - (v716_g * 0.13))
v726_p = (v716_g * 0.37)
v704_e.save()
v704_e.translate(v724_b, v725_h)
v704_e.lineWidth = jsMax(1.0, (v716_g / 420.0))
v704_e.strokeStyle = jsGet(v713_n, "secondary")
v704_e.globalAlpha = 0.32
v704_e.beginPath()
v704_e.arc(0.0, 0.0, v726_p, 0.0, v718_d)
v704_e.stroke()
v704_e.globalAlpha = 0.18
v704_e.beginPath()
v704_e.arc(0.0, 0.0, (v726_p * 1.06), 0.0, v718_d)
v704_e.stroke()
v704_e.beginPath()
run {
v727_w = 0.0
L94@ while ((v727_w < 60.0)) {
v728_A = ((v727_w / 60.0) * v718_d)
v729_C = ((v727_w % 5.0) == 0.0)
v730_E = (v726_p * (if (v729_C) 0.85 else 0.9))
v704_e.moveTo((kotlin.math.cos(v728_A) * v730_E), (kotlin.math.sin(v728_A) * v730_E))
v704_e.lineTo(((kotlin.math.cos(v728_A) * v726_p) * 0.96), ((kotlin.math.sin(v728_A) * v726_p) * 0.96))
v727_w++
}
}
v704_e.strokeStyle = jsGet(v713_n, "primary")
v704_e.globalAlpha = 0.42
v704_e.stroke()
if ((v715_i > 0.6)) {
v704_e.fillStyle = jsGet(v713_n, "primary")
v704_e.globalAlpha = 0.38
v704_e.font = (("700 " + jsStr(jsRound((v726_p * 0.085)))) + "px \"Times New Roman\", serif")
v704_e.textAlign = "center"
v704_e.textBaseline = "middle"
v731_k = jsArr("XII", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI")
run {
v732_R = 0.0
L95@ while ((v732_R < 12.0)) {
L95c@ do {
v733_F = (((v732_R / 12.0) * v718_d) - (Math.PI / 2.0))
if ((kotlin.math.sin(v733_F) > 0.25)) {
break@L95c
}
v704_e.fillText(str(jsGet(v731_k, v732_R)), ((kotlin.math.cos(v733_F) * v726_p) * 0.74), ((kotlin.math.sin(v733_F) * v726_p) * 0.74))
} while (false)
v732_R++
}
}
}
run {
v734_O = 0.0
L96@ while ((v734_O < 2.0)) {
v735_P = (v726_p * (if (truthy(v734_O)) 0.42 else 0.6))
v736_I = (if (truthy(v734_O)) 18.0 else 28.0)
v737_U_ = (v709_l * (if (truthy(v734_O)) -0.05 else 0.035))
v704_e.beginPath()
run {
v738_B = 0.0
L97@ while ((v738_B <= (v736_I * 2.0))) {
v739_L = (v737_U_ + ((v738_B / (v736_I * 2.0)) * v718_d))
v740_G = (v735_P * (if (truthy((v738_B % 2.0))) 0.95 else 1.03))
if (truthy(v738_B)) {
v704_e.lineTo((kotlin.math.cos(v739_L) * v740_G), (kotlin.math.sin(v739_L) * v740_G))
} else {
v704_e.moveTo((kotlin.math.cos(v739_L) * v740_G), (kotlin.math.sin(v739_L) * v740_G))
}
v738_B++
}
}
v704_e.strokeStyle = (if (truthy(v734_O)) jsGet(v713_n, "accent") else jsGet(v713_n, "secondary"))
v704_e.globalAlpha = (if (truthy(v734_O)) 0.22 else 0.2)
v704_e.stroke()
v734_O++
}
}
v704_e.restore()
v741_U = ((v716_g * 0.2) * (1.0 + (v711_f * 0.08)))
v742_V = (v708_t - (v741_U * 0.3))
drawRetroSun(v704_e, v707_r, v742_V, v741_U, v709_l, v711_f, v713_n, true)
v704_e.save()
v704_e.translate(v724_b, v725_h)
v704_e.lineCap = "round"
v743_D = ((v709_l * 0.07) - (Math.PI / 2.0))
v744_K = ((((v709_l * 0.07) / 12.0) - (Math.PI / 2.0)) + 1.1)
v704_e.strokeStyle = jsGet(v713_n, "accent")
v704_e.globalAlpha = 0.55
v704_e.lineWidth = jsMax(2.0, (v716_g / 160.0))
v704_e.beginPath()
v704_e.moveTo(0.0, 0.0)
v704_e.lineTo(((kotlin.math.cos(v744_K) * v726_p) * 0.5), ((kotlin.math.sin(v744_K) * v726_p) * 0.5))
v704_e.stroke()
v704_e.strokeStyle = jsGet(v713_n, "primary")
v704_e.lineWidth = jsMax(1.4, (v716_g / 260.0))
v704_e.beginPath()
v704_e.moveTo(0.0, 0.0)
v704_e.lineTo(((kotlin.math.cos(v743_D) * v726_p) * 0.8), ((kotlin.math.sin(v743_D) * v726_p) * 0.8))
v704_e.stroke()
v704_e.globalAlpha = 0.8
v704_e.fillStyle = jsGet(v713_n, "accent")
v704_e.beginPath()
v704_e.arc(0.0, 0.0, jsMax(3.0, (v716_g / 140.0)), 0.0, v718_d)
v704_e.fill()
v704_e.restore()
v704_e.save()
v704_e.lineWidth = jsMax(1.0, (v716_g / 500.0))
run {
v745_S_ = 0.0
L98@ while ((v745_S_ < 4.0)) {
v746_z = (if (truthy((v745_S_ % 2.0))) 1.0 else -1.0)
v747_X = ((v707_r + ((v746_z * v705_a) * (0.3 + (if ((v745_S_ > 1.0)) 0.13 else 0.0)))) - ((v717_m * 22.0) * (if ((v745_S_ > 1.0)) 1.4 else 1.0)))
v748_q = ((v708_t - (v706_o * (0.2 + (if ((v745_S_ > 1.0)) 0.06 else 0.0)))) + ((kotlin.math.sin(((v709_l * 0.5) + (v745_S_ * 1.9))) * v706_o) * 0.018))
v749_Q = (v716_g * (if ((v745_S_ > 1.0)) 0.075 else 0.055))
v750_W = (v749_Q * 0.55)
v751_te = (kotlin.math.sin(((v709_l * 0.3) + v745_S_)) * 0.12)
v704_e.save()
v704_e.translate(v747_X, v748_q)
v704_e.rotate(v751_te)
v704_e.strokeStyle = (if (truthy((v745_S_ % 2.0))) jsGet(v713_n, "primary") else jsGet(v713_n, "secondary"))
v704_e.globalAlpha = 0.75
v704_e.beginPath()
v704_e.moveTo(-v750_W, -v749_Q)
v704_e.lineTo(v750_W, -v749_Q)
v704_e.lineTo((v750_W * 0.12), 0.0)
v704_e.lineTo(v750_W, v749_Q)
v704_e.lineTo(-v750_W, v749_Q)
v704_e.lineTo((-v750_W * 0.12), 0.0)
v704_e.closePath()
v704_e.stroke()
v704_e.beginPath()
v704_e.moveTo((-v750_W * 1.15), -v749_Q)
v704_e.lineTo((v750_W * 1.15), -v749_Q)
v704_e.moveTo((-v750_W * 1.15), v749_Q)
v704_e.lineTo((v750_W * 1.15), v749_Q)
v704_e.stroke()
v752_le = (0.5 + (0.5 * kotlin.math.sin(((v709_l * 0.2) + v745_S_))))
v704_e.fillStyle = jsGet(v713_n, "accent")
v704_e.globalAlpha = 0.55
v704_e.beginPath()
v704_e.moveTo(((-v750_W * 0.7) * v752_le), ((-v749_Q * 0.3) * v752_le))
v704_e.lineTo(((v750_W * 0.7) * v752_le), ((-v749_Q * 0.3) * v752_le))
v704_e.lineTo(0.0, (-v749_Q * 0.02))
v704_e.closePath()
v704_e.fill()
v704_e.beginPath()
v704_e.moveTo((-v750_W * 0.8), (v749_Q * 0.98))
v704_e.lineTo((v750_W * 0.8), (v749_Q * 0.98))
v704_e.lineTo(0.0, (v749_Q * (0.98 - (0.6 * (1.0 - v752_le)))))
v704_e.closePath()
v704_e.fill()
v704_e.restore()
v745_S_++
}
}
v704_e.restore()
drawPerspectiveFloor(v704_e, v705_a, v706_o, v707_r, v708_t, (v710_u * 1.25), v709_l, v711_f, v713_n, "highway")
drawPerspectiveCorridor(7.0, 0.58, v710_u, JsFn L99@{ args ->
var v763_Y: Double = 0.0
var v764_ne: Double = 0.0
var v765_he: Double = 0.0
var v766_ve: Double = 0.0
var v767_Ce: Any? = null
var v768_ye: Double = 0.0
var v769_Z: Double = 0.0
var v770_H: Double = 0.0
var v771_fe: Double = 0.0
var v772_we: Double = 0.0
var v773_Se: Double = 0.0
var v774_ke: Double = 0.0
var v775_Ee: Double = 0.0
var v759_N: Any? = args.getOrNull(0)
var v760_ae: Any? = args.getOrNull(1)
var v761_j: Any? = args.getOrNull(2)
var v762_oe: Any? = args.getOrNull(3)
v763_Y = (v705_a * (0.1 + (num(v760_ae) * 0.72)))
v764_ne = (v706_o * (0.08 + (num(v760_ae) * 0.42)))
v765_he = (v708_t + (num(v760_ae) * (v706_o - v708_t)))
v766_ve = (v763_Y / 2.0)
v704_e.save()
v704_e.globalAlpha = (num(v761_j) * 0.9)
v767_Ce = (if (((num(v762_oe) % 2.0) == 0.0)) jsGet(v713_n, "primary") else jsGet(v713_n, "accent"))
v768_ye = jsMax(0.08, (0.12 - (num(v760_ae) * 0.02)))
v704_e.beginPath()
v704_e.ellipse(v707_r, v765_he, v766_ve, v764_ne, 0.0, Math.PI, v718_d)
v704_e.ellipse(v707_r, v765_he, (v766_ve * (1.0 - v768_ye)), (v764_ne * (1.0 - (v768_ye * 1.1))), 0.0, v718_d, Math.PI, true)
v704_e.closePath()
v704_e.globalAlpha = (num(v761_j) * 0.92)
v704_e.fillStyle = jsGet(v713_n, "mtnBot")
v704_e.fill()
v704_e.globalAlpha = (num(v761_j) * 0.55)
v704_e.fillStyle = v767_Ce
v704_e.fill()
v704_e.globalAlpha = num(v761_j)
v704_e.strokeStyle = v767_Ce
v704_e.lineWidth = (1.0 + (num(v760_ae) * 2.2))
v704_e.stroke()
v704_e.beginPath()
v704_e.ellipse(v707_r, v765_he, (v766_ve * (1.0 - v768_ye)), (v764_ne * (1.0 - (v768_ye * 1.1))), 0.0, Math.PI, v718_d)
v704_e.strokeStyle = jsGet(jsGet(v713_n, "floor"), 0.0)
v704_e.globalAlpha = (num(v761_j) * 0.8)
v704_e.lineWidth = (1.0 + (num(v760_ae) * 1.5))
v704_e.stroke()
v704_e.beginPath()
run {
v769_Z = 1.0
L100@ while ((v769_Z < 12.0)) {
v770_H = (Math.PI + ((v769_Z / 12.0) * Math.PI))
v771_fe = kotlin.math.cos(v770_H)
v772_we = kotlin.math.sin(v770_H)
v704_e.moveTo((v707_r + ((v771_fe * v766_ve) * (1.0 - (v768_ye * 0.8)))), (v765_he + ((v772_we * v764_ne) * (1.0 - (v768_ye * 0.9)))))
v704_e.lineTo((v707_r + ((v771_fe * v766_ve) * (1.0 - (v768_ye * 0.2)))), (v765_he + ((v772_we * v764_ne) * (1.0 - (v768_ye * 0.25)))))
v769_Z++
}
}
v704_e.strokeStyle = jsGet(jsGet(v713_n, "floor"), 0.0)
v704_e.globalAlpha = (num(v761_j) * 0.7)
v704_e.lineWidth = (0.8 + (num(v760_ae) * 1.4))
v704_e.stroke()
v773_Se = (3.0 + (num(v760_ae) * 15.0))
v774_ke = ((v765_he - v764_ne) - (v773_Se * 0.2))
v704_e.globalAlpha = num(v761_j)
v704_e.fillStyle = jsGet(jsGet(v713_n, "floor"), 0.0)
v704_e.beginPath()
v704_e.arc(v707_r, v774_ke, v773_Se, 0.0, v718_d)
v704_e.fill()
v704_e.strokeStyle = v767_Ce
v704_e.lineWidth = (1.0 + (num(v760_ae) * 2.0))
v704_e.stroke()
v775_Ee = ((v709_l * 0.4) + num(v762_oe))
v704_e.beginPath()
v704_e.moveTo(v707_r, v774_ke)
v704_e.lineTo((v707_r + ((kotlin.math.cos(v775_Ee) * v773_Se) * 0.75)), (v774_ke + ((kotlin.math.sin(v775_Ee) * v773_Se) * 0.75)))
v704_e.stroke()
v704_e.restore()
null
})
v753_ie = (jsMin(v705_a, v706_o) / 780.0)
v754_J = ((v707_r + (kotlin.math.sin((v709_l * 1.15)) * 22.0)) + (Rt.mouseX * 25.0))
v755_de = ((v706_o - ((v706_o - v708_t) * 0.2)) + (kotlin.math.sin((v709_l * 7.0)) * (1.2 + (v711_f * 2.0))))
v704_e.save()
v704_e.translate(v754_J, v755_de)
v704_e.scale(v753_ie, v753_ie)
v704_e.globalAlpha = (0.28 + (v711_f * 0.12))
v704_e.fillStyle = jsGet(v713_n, "primary")
v704_e.beginPath()
v704_e.moveTo(-145.0, 18.0)
v704_e.lineTo(-95.0, 8.0)
v704_e.lineTo(95.0, 8.0)
v704_e.lineTo(145.0, 18.0)
v704_e.lineTo(90.0, 35.0)
v704_e.lineTo(-90.0, 35.0)
v704_e.closePath()
v704_e.fill()
v704_e.globalAlpha = 1.0
js_forEach(jsArr(-103.0, 103.0)) L101@{ v776_N: Any?, v777_ae: Double, _ ->
var v778_j: Double = 0.0
var v779_oe: Double = 0.0
var v780_Y: Double = 0.0
var v781_ne: Double = 0.0
var v782_he: Double = 0.0

v778_j = -13.0
v779_oe = 28.0
v704_e.fillStyle = "#03040a"
v704_e.strokeStyle = "#777084"
v704_e.lineWidth = 1.8
v704_e.beginPath()
v704_e.moveTo((num(v776_N) - 14.0), v778_j)
v704_e.lineTo((num(v776_N) + 14.0), v778_j)
v704_e.lineTo((num(v776_N) + 21.0), -5.0)
v704_e.lineTo((num(v776_N) + 22.0), 16.0)
v704_e.lineTo((num(v776_N) + 15.0), v779_oe)
v704_e.lineTo((num(v776_N) - 15.0), v779_oe)
v704_e.lineTo((num(v776_N) - 22.0), 16.0)
v704_e.lineTo((num(v776_N) - 21.0), -5.0)
v704_e.closePath()
v704_e.fill()
v704_e.stroke()
v704_e.save()
v704_e.beginPath()
v704_e.moveTo((num(v776_N) - 14.0), v778_j)
v704_e.lineTo((num(v776_N) + 14.0), v778_j)
v704_e.lineTo((num(v776_N) + 21.0), -5.0)
v704_e.lineTo((num(v776_N) + 22.0), 16.0)
v704_e.lineTo((num(v776_N) + 15.0), v779_oe)
v704_e.lineTo((num(v776_N) - 15.0), v779_oe)
v704_e.lineTo((num(v776_N) - 22.0), 16.0)
v704_e.lineTo((num(v776_N) - 21.0), -5.0)
v704_e.closePath()
v704_e.clip()
v780_Y = ((v709_l * (24.0 + (v711_f * 34.0))) % 9.0)
run {
v781_ne = -5.0
L102@ while ((v781_ne < 7.0)) {
v782_he = ((-14.0 + (v781_ne * 9.0)) + v780_Y)
v704_e.fillStyle = (if (((v781_ne % 2.0) == 0.0)) "#777487" else "#4a4658")
v704_e.strokeStyle = "#171521"
v704_e.lineWidth = 1.1
v704_e.beginPath()
v704_e.moveTo((num(v776_N) - 15.0), (v782_he - 2.0))
v704_e.lineTo((num(v776_N) - 9.0), (v782_he - 3.5))
v704_e.lineTo((num(v776_N) + 9.0), (v782_he - 3.5))
v704_e.lineTo((num(v776_N) + 15.0), (v782_he - 2.0))
v704_e.lineTo((num(v776_N) + 13.0), (v782_he + 2.0))
v704_e.lineTo((num(v776_N) - 13.0), (v782_he + 2.0))
v704_e.closePath()
v704_e.fill()
v704_e.stroke()
v781_ne++
}
}
v704_e.strokeStyle = "rgba(7,7,14,0.95)"
v704_e.lineWidth = 1.5
js_forEach(jsArr(-8.0, 0.0, 8.0)) L103@{ v783_ve: Any?, _, _ ->

v704_e.beginPath()
v704_e.moveTo((num(v776_N) + num(v783_ve)), -11.0)
v704_e.lineTo((num(v776_N) + (num(v783_ve) * 0.92)), 26.0)
v704_e.stroke()
null
}
v704_e.restore()
null
}
v704_e.fillStyle = "#10091d"
v704_e.strokeStyle = jsGet(v713_n, "primary")
v704_e.lineWidth = 2.6
v704_e.beginPath()
v704_e.moveTo(-125.0, 8.0)
v704_e.lineTo(-137.0, -7.0)
v704_e.lineTo(-125.0, -31.0)
v704_e.lineTo(-91.0, -47.0)
v704_e.lineTo(91.0, -47.0)
v704_e.lineTo(125.0, -31.0)
v704_e.lineTo(137.0, -7.0)
v704_e.lineTo(125.0, 8.0)
v704_e.lineTo(90.0, 19.0)
v704_e.lineTo(-90.0, 19.0)
v704_e.closePath()
v704_e.fill()
v704_e.stroke()
v704_e.fillStyle = "#24102c"
v704_e.strokeStyle = jsGet(v713_n, "secondary")
v704_e.lineWidth = 1.8
v704_e.beginPath()
v704_e.moveTo(-125.0, -31.0)
v704_e.lineTo(-84.0, -50.0)
v704_e.lineTo(-58.0, -72.0)
v704_e.lineTo(58.0, -72.0)
v704_e.lineTo(84.0, -50.0)
v704_e.lineTo(125.0, -31.0)
v704_e.lineTo(88.0, -39.0)
v704_e.lineTo(-88.0, -39.0)
v704_e.closePath()
v704_e.fill()
v704_e.stroke()
js_forEach(jsArr(-103.0, 103.0)) L104@{ v784_N: Any?, v785_ae: Double, _ ->
var v786_j: Any? = null
var v787_oe: Double = 0.0
var v788_Y: Double = 0.0
var v789_ne: Double = 0.0
var v790_he: Any? = null
var v791_ve: Any? = null
var v792_Ce: Any? = null
var v793_ye: Any? = null
var v794_Z: Double = 0.0

v786_j = jsArr(jsArr(jsArr((num(v784_N) - 39.0), 20.0), jsArr((num(v784_N) - 36.0), 0.0), jsArr((num(v784_N) - 29.0), -18.0), jsArr((num(v784_N) - 17.0), -30.0), jsArr((num(v784_N) + 17.0), -30.0), jsArr((num(v784_N) + 29.0), -18.0), jsArr((num(v784_N) + 36.0), 0.0), jsArr((num(v784_N) + 39.0), 20.0)), jsArr(jsArr((num(v784_N) - 31.0), 18.0), jsArr((num(v784_N) - 29.0), 1.0), jsArr((num(v784_N) - 23.0), -14.0), jsArr((num(v784_N) - 13.0), -23.0), jsArr((num(v784_N) + 13.0), -23.0), jsArr((num(v784_N) + 23.0), -14.0), jsArr((num(v784_N) + 29.0), 1.0), jsArr((num(v784_N) + 31.0), 18.0)), jsArr(jsArr((num(v784_N) - 23.0), 16.0), jsArr((num(v784_N) - 21.0), 2.0), jsArr((num(v784_N) - 16.0), -10.0), jsArr((num(v784_N) - 9.0), -17.0), jsArr((num(v784_N) + 9.0), -17.0), jsArr((num(v784_N) + 16.0), -10.0), jsArr((num(v784_N) + 21.0), 2.0), jsArr((num(v784_N) + 23.0), 16.0)), jsArr(jsArr((num(v784_N) - 15.0), 14.0), jsArr((num(v784_N) - 14.0), 3.0), jsArr((num(v784_N) - 10.0), -6.0), jsArr((num(v784_N) - 5.0), -11.0), jsArr((num(v784_N) + 5.0), -11.0), jsArr((num(v784_N) + 10.0), -6.0), jsArr((num(v784_N) + 14.0), 3.0), jsArr((num(v784_N) + 15.0), 14.0)))
v704_e.fillStyle = "#03030a"
v704_e.strokeStyle = "#171126"
v704_e.lineWidth = 1.2
v704_e.beginPath()
v704_e.moveTo(num(jsGet(jsGet(jsGet(v786_j, 0.0), 0.0), 0.0)), num(jsGet(jsGet(jsGet(v786_j, 0.0), 0.0), 1.0)))
run {
v787_oe = 1.0
L105@ while ((v787_oe < jsLen(jsGet(v786_j, 0.0)))) {
v704_e.lineTo(num(jsGet(jsGet(jsGet(v786_j, 0.0), v787_oe), 0.0)), num(jsGet(jsGet(jsGet(v786_j, 0.0), v787_oe), 1.0)))
v787_oe++
}
}
v704_e.closePath()
v704_e.fill()
v704_e.stroke()
run {
v788_Y = 0.0
L106@ while ((v788_Y < (jsLen(v786_j) - 1.0))) {
run {
v789_ne = 0.0
L107@ while ((v789_ne < jsLen(jsGet(v786_j, v788_Y)))) {
v790_he = jsGet(jsGet(v786_j, v788_Y), v789_ne)
v791_ve = jsGet(jsGet(v786_j, v788_Y), ((v789_ne + 1.0) % jsLen(jsGet(v786_j, v788_Y))))
v787_oe = num(jsGet(jsGet(v786_j, (v788_Y + 1.0)), v789_ne))
v792_Ce = jsGet(jsGet(v786_j, (v788_Y + 1.0)), ((v789_ne + 1.0) % jsLen(jsGet(v786_j, (v788_Y + 1.0)))))
v704_e.beginPath()
v704_e.moveTo(num(jsGet(v790_he, 0.0)), num(jsGet(v790_he, 1.0)))
v704_e.lineTo(num(jsGet(v787_oe, 0.0)), num(jsGet(v787_oe, 1.0)))
v704_e.strokeStyle = (if (((v789_ne == 0.0) || (v789_ne == 6.0))) "rgba(255,80,210,0.85)" else "rgba(90,210,255,0.48)")
v704_e.lineWidth = (if ((v788_Y == 0.0)) 1.8 else 1.15)
v704_e.stroke()
v704_e.beginPath()
v704_e.moveTo(num(jsGet(v790_he, 0.0)), num(jsGet(v790_he, 1.0)))
v704_e.lineTo(num(jsGet(v791_ve, 0.0)), num(jsGet(v791_ve, 1.0)))
v704_e.strokeStyle = (if ((v788_Y == 0.0)) jsGet(v713_n, "secondary") else "rgba(90,210,255,0.42)")
v704_e.lineWidth = (if ((v788_Y == 0.0)) 2.2 else 1.0)
v704_e.stroke()
v789_ne++
}
}
v788_Y++
}
}
v793_ye = jsGet(v786_j, (jsLen(v786_j) - 1.0))
v704_e.beginPath()
v704_e.moveTo(num(jsGet(jsGet(v793_ye, 0.0), 0.0)), num(jsGet(jsGet(v793_ye, 0.0), 1.0)))
run {
v794_Z = 1.0
L108@ while ((v794_Z < jsLen(v793_ye))) {
v704_e.lineTo(num(jsGet(jsGet(v793_ye, v794_Z), 0.0)), num(jsGet(jsGet(v793_ye, v794_Z), 1.0)))
v794_Z++
}
}
v704_e.closePath()
v704_e.strokeStyle = jsGet(v713_n, "primary")
v704_e.lineWidth = 1.4
v704_e.stroke()
js_forEach(jsArr(1.0, 3.0, 5.0, 7.0)) L109@{ v795_H: Any?, _, _ ->

v704_e.beginPath()
v704_e.moveTo(num(jsGet(jsGet(jsGet(v786_j, 1.0), v795_H), 0.0)), num(jsGet(jsGet(jsGet(v786_j, 1.0), v795_H), 1.0)))
v704_e.lineTo(num(jsGet(jsGet(v793_ye, v795_H), 0.0)), num(jsGet(jsGet(v793_ye, v795_H), 1.0)))
v704_e.strokeStyle = "rgba(255,255,255,0.34)"
v704_e.lineWidth = 0.9
v704_e.stroke()
null
}
null
}
v704_e.fillStyle = "#061927"
v704_e.strokeStyle = jsGet(v713_n, "primary")
v704_e.lineWidth = 2.0
v704_e.beginPath()
v704_e.moveTo(-63.0, -73.0)
v704_e.lineTo(-48.0, -101.0)
v704_e.lineTo(48.0, -101.0)
v704_e.lineTo(63.0, -73.0)
v704_e.lineTo(48.0, -55.0)
v704_e.lineTo(-48.0, -55.0)
v704_e.closePath()
v704_e.fill()
v704_e.stroke()
v704_e.fillStyle = "rgba(80,230,255,0.34)"
v704_e.beginPath()
v704_e.moveTo(-48.0, -75.0)
v704_e.lineTo(-38.0, -95.0)
v704_e.lineTo(-7.0, -95.0)
v704_e.lineTo(-7.0, -75.0)
v704_e.closePath()
v704_e.fill()
v704_e.beginPath()
v704_e.moveTo(7.0, -95.0)
v704_e.lineTo(38.0, -95.0)
v704_e.lineTo(48.0, -75.0)
v704_e.lineTo(7.0, -75.0)
v704_e.closePath()
v704_e.fill()
v704_e.strokeStyle = jsGet(v713_n, "secondary")
v704_e.lineWidth = 1.4
v704_e.beginPath()
v704_e.moveTo(0.0, -98.0)
v704_e.lineTo(0.0, -57.0)
v704_e.stroke()
v704_e.fillStyle = "#170c26"
v704_e.strokeStyle = jsGet(v713_n, "accent")
v704_e.lineWidth = 2.0
v704_e.beginPath()
v704_e.moveTo(-78.0, -53.0)
v704_e.lineTo(-54.0, -68.0)
v704_e.lineTo(54.0, -68.0)
v704_e.lineTo(78.0, -53.0)
v704_e.lineTo(67.0, -39.0)
v704_e.lineTo(-67.0, -39.0)
v704_e.closePath()
v704_e.fill()
v704_e.stroke()
v704_e.strokeStyle = jsGet(v713_n, "primary")
v704_e.lineWidth = 2.2
v704_e.beginPath()
v704_e.moveTo(-111.0, -42.0)
v704_e.lineTo(-126.0, -57.0)
v704_e.lineTo(-118.0, -63.0)
v704_e.lineTo(-78.0, -51.0)
v704_e.stroke()
v704_e.beginPath()
v704_e.moveTo(111.0, -42.0)
v704_e.lineTo(126.0, -57.0)
v704_e.lineTo(118.0, -63.0)
v704_e.lineTo(78.0, -51.0)
v704_e.stroke()
v704_e.fillStyle = "#0b0714"
v704_e.strokeStyle = jsGet(v713_n, "secondary")
v704_e.lineWidth = 2.0
v704_e.beginPath()
v704_e.moveTo(-128.0, -58.0)
v704_e.lineTo(-119.0, -67.0)
v704_e.lineTo(119.0, -67.0)
v704_e.lineTo(128.0, -58.0)
v704_e.lineTo(118.0, -52.0)
v704_e.lineTo(-118.0, -52.0)
v704_e.closePath()
v704_e.fill()
v704_e.stroke()
v704_e.fillStyle = "#05050d"
v704_e.strokeStyle = jsGet(v713_n, "accent")
v704_e.lineWidth = 1.4
v704_e.beginPath()
v704_e.moveTo(-72.0, -31.0)
v704_e.lineTo(72.0, -31.0)
v704_e.lineTo(62.0, -14.0)
v704_e.lineTo(-62.0, -14.0)
v704_e.closePath()
v704_e.fill()
v704_e.stroke()
run {
v756_ee = 0.0
L110@ while ((v756_ee < 9.0)) {
v757_se = (-58.0 + (v756_ee * 14.0))
v704_e.strokeStyle = "rgba(190,220,255,0.42)"
v704_e.lineWidth = 1.0
v704_e.beginPath()
v704_e.moveTo(v757_se, -29.0)
v704_e.lineTo(v757_se, -16.0)
v704_e.stroke()
v756_ee++
}
}
v704_e.fillStyle = "#ff304f"
v704_e.strokeStyle = "#ffb0c2"
v704_e.lineWidth = 1.2
v704_e.beginPath()
v704_e.moveTo(-116.0, -27.0)
v704_e.lineTo(-77.0, -32.0)
v704_e.lineTo(-73.0, -22.0)
v704_e.lineTo(-111.0, -17.0)
v704_e.closePath()
v704_e.fill()
v704_e.stroke()
v704_e.beginPath()
v704_e.moveTo(116.0, -27.0)
v704_e.lineTo(77.0, -32.0)
v704_e.lineTo(73.0, -22.0)
v704_e.lineTo(111.0, -17.0)
v704_e.closePath()
v704_e.fill()
v704_e.stroke()
v704_e.fillStyle = "#05050b"
v704_e.strokeStyle = jsGet(v713_n, "primary")
v704_e.lineWidth = 1.7
v704_e.beginPath()
v704_e.moveTo(-86.0, 5.0)
v704_e.lineTo(-65.0, 16.0)
v704_e.lineTo(65.0, 16.0)
v704_e.lineTo(86.0, 5.0)
v704_e.lineTo(71.0, 25.0)
v704_e.lineTo(-71.0, 25.0)
v704_e.closePath()
v704_e.fill()
v704_e.stroke()
js_forEach(jsArr(-43.0, 43.0)) L111@{ v796_N: Any?, _, _ ->

v704_e.fillStyle = "#03030a"
v704_e.strokeStyle = jsGet(v713_n, "accent")
v704_e.lineWidth = 2.0
v704_e.beginPath()
v704_e.moveTo((num(v796_N) - 8.0), 13.0)
v704_e.lineTo((num(v796_N) + 8.0), 13.0)
v704_e.lineTo((num(v796_N) + 6.0), 23.0)
v704_e.lineTo((num(v796_N) - 6.0), 23.0)
v704_e.closePath()
v704_e.fill()
v704_e.stroke()
null
}
v704_e.fillStyle = "#ff3159"
v704_e.beginPath()
v704_e.moveTo(-18.0, 1.0)
v704_e.lineTo(18.0, 1.0)
v704_e.lineTo(14.0, 5.0)
v704_e.lineTo(-14.0, 5.0)
v704_e.closePath()
v704_e.fill()
v758_x = (9.0 + (v711_f * 24.0))
js_forEach(jsArr(-43.0, 43.0)) L112@{ v797_N: Any?, v798_ae: Double, _ ->

v704_e.fillStyle = (if (truthy(v798_ae)) jsGet(v713_n, "primary") else jsGet(v713_n, "accent"))
v704_e.beginPath()
v704_e.moveTo((num(v797_N) - 4.0), 24.0)
v704_e.lineTo((num(v797_N) + 4.0), 24.0)
v704_e.lineTo((num(v797_N) + 2.0), (24.0 + v758_x))
v704_e.lineTo(num(v797_N), (29.0 + v758_x))
v704_e.lineTo((num(v797_N) - 2.0), (24.0 + v758_x))
v704_e.closePath()
v704_e.fill()
null
}
v704_e.restore()
}

fun renderScene13_NeonRainAlley(v799_e: Ctx, v800_a: Double, v801_o: Double, v802_r: Double, v803_t: Double, v804_l: Double, v805_u: Double, v806_f: Double, v807_M: Double, v808_n: Any?): Unit {

var v809_s: Grad? = null
var v810_i: Double = 0.0
var v811_g: Double = 0.0
var v812_m: Double = 0.0
var v813_d: Double = 0.0
var v814_y: Double = 0.0
var v815_c: Double = 0.0
var v816_S: Grad? = null
var v817_v: Double = 0.0
var v818_T: Double = 0.0
var v819_b: Double = 0.0
var v820_h: Double = 0.0
var v821_p: Double = 0.0
var v822_w: Double = 0.0
v809_s = v799_e.createLinearGradient(0.0, 0.0, 0.0, v801_o)
v809_s!!.addColorStop(0.0, jsGet(jsGet(v808_n, "sky"), 0.0))
v809_s!!.addColorStop(0.55, jsGet(jsGet(v808_n, "sky"), 2.0))
v809_s!!.addColorStop(1.0, jsGet(jsGet(v808_n, "floor"), 0.0))
v799_e.fillStyle = v809_s!!
v799_e.fillRect(0.0, 0.0, v800_a, v801_o)
v810_i = jsMax(40.0, (v800_a * 0.24))
v799_e.fillStyle = jsGet(v808_n, "mtnBot")
v799_e.beginPath()
v799_e.moveTo(0.0, 0.0)
v799_e.lineTo((v810_i * 1.25), 0.0)
v799_e.lineTo((v802_r - (v800_a * 0.08)), v803_t)
v799_e.lineTo(0.0, v801_o)
v799_e.closePath()
v799_e.fill()
v799_e.fillStyle = jsGet(v808_n, "sunSlice")
v799_e.beginPath()
v799_e.moveTo(v800_a, 0.0)
v799_e.lineTo((v800_a - (v810_i * 1.1)), 0.0)
v799_e.lineTo((v802_r + (v800_a * 0.08)), v803_t)
v799_e.lineTo(v800_a, v801_o)
v799_e.closePath()
v799_e.fill()
run {
v811_g = 0.0
L113@ while ((v811_g < 6.0)) {
v812_m = (18.0 + (v811_g * jsMax(17.0, (v801_o * 0.055))))
run {
v813_d = 0.0
L114@ while ((v813_d < 4.0)) {
v814_y = (10.0 + ((v813_d * v810_i) * 0.27))
v815_c = ((v800_a - (v810_i * 0.94)) + ((v813_d * v810_i) * 0.22))
v799_e.fillStyle = (if (truthy(((v811_g + v813_d) % 3.0))) jsGet(v808_n, "primary") else jsGet(v808_n, "secondary"))
v799_e.globalAlpha = 0.42
v799_e.fillRect(v814_y, v812_m, jsMax(4.0, (v810_i * 0.085)), jsMax(4.0, (v801_o * 0.018)))
v799_e.fillRect(v815_c, v812_m, jsMax(4.0, (v810_i * 0.085)), jsMax(4.0, (v801_o * 0.018)))
v813_d++
}
}
v811_g++
}
}
v799_e.globalAlpha = 1.0
v816_S = v799_e.createLinearGradient(0.0, v803_t, 0.0, v801_o)
v816_S!!.addColorStop(0.0, jsGet(jsGet(v808_n, "floor"), 1.0))
v816_S!!.addColorStop(1.0, jsGet(jsGet(v808_n, "floor"), 0.0))
v799_e.fillStyle = v816_S!!
v799_e.beginPath()
v799_e.moveTo((v802_r - (v800_a * 0.08)), v803_t)
v799_e.lineTo((v802_r + (v800_a * 0.08)), v803_t)
v799_e.lineTo(v800_a, v801_o)
v799_e.lineTo(0.0, v801_o)
v799_e.closePath()
v799_e.fill()
v799_e.strokeStyle = jsGet(v808_n, "primary")
v799_e.globalAlpha = 0.55
v799_e.lineWidth = 1.0
v799_e.beginPath()
run {
v817_v = -5.0
L115@ while ((v817_v <= 5.0)) {
v799_e.moveTo((v802_r + ((v817_v * v800_a) * 0.018)), v803_t)
v799_e.lineTo((v802_r + ((v817_v * v800_a) * 0.17)), v801_o)
v817_v++
}
}
run {
v818_T = 1.0
L116@ while ((v818_T <= 7.0)) {
v819_b = (v803_t + ((v801_o - v803_t) * Math.pow((v818_T / 8.0), 1.8)))
v799_e.moveTo(0.0, v819_b)
v799_e.lineTo(v800_a, v819_b)
v818_T++
}
}
v799_e.stroke()
v799_e.globalAlpha = 1.0
v799_e.strokeStyle = jsGet(v808_n, "grid")
v799_e.beginPath()
run {
v820_h = 0.0
L117@ while ((v820_h < 12.0)) {
v821_p = (((v820_h * 97.0) + (v804_l * 65.0)) % v800_a)
v822_w = (((v820_h * 71.0) + (v804_l * 170.0)) % v801_o)
v799_e.moveTo(v821_p, v822_w)
v799_e.lineTo(((v821_p - 5.0) - (v820_h % 5.0)), ((v822_w + 13.0) + (v820_h % 9.0)))
v820_h++
}
}
v799_e.stroke()
}

fun renderScene15_ChromeCathedral(v823_e: Ctx, v824_a: Double, v825_o: Double, v826_r: Double, v827_t: Double, v828_l: Double, v829_u: Double, v830_f: Double, v831_M: Double, v832_n: Any?): Unit {

var v833_s: Grad? = null
var v835_g: Any? = null
var v836_m: Double = 0.0
var v837_d: Double = 0.0
var v838_y: Double = 0.0
var v839_c: Double = 0.0
var v840_S: Double = 0.0
var v841_v: Double = 0.0
var v842_T: Double = 0.0
var v843_b: Double = 0.0
var v844_h: Double = 0.0
var v845_p: Double = 0.0
var v846_w: Double = 0.0
var v847_A: Double = 0.0
var v848_C: Double = 0.0
var v849_E: Grad? = null
var v850_k: Double = 0.0
var v851_R: Double = 0.0
var v852_F: Double = 0.0
var v853_O: Double = 0.0
var v854_P: Double = 0.0
var v855_I: Double = 0.0
var v856_U_: Double = 0.0
var v857_B: Any? = null
var v858_L: Grad? = null
var v859_G: Double = 0.0
var v860_U: Double = 0.0
var v861_V: Double = 0.0
fun v834_i(v862_D: Double, v863_K: Double, v864_S_: Double, v865_z: Double, v866_X: Any?, v867_q: Any?, v868_Q: Double, v869_W: Double): Unit {

v823_e.save()
v823_e.globalAlpha = v868_Q
v823_e.beginPath()
v823_e.moveTo((v862_D - (v864_S_ * 0.5)), v863_K)
v823_e.lineTo((v862_D - (v864_S_ * 0.5)), (v863_K - (v865_z * 0.62)))
v823_e.quadraticCurveTo((v862_D - (v864_S_ * 0.43)), (v863_K - (v865_z * 0.88)), v862_D, (v863_K - v865_z))
v823_e.quadraticCurveTo((v862_D + (v864_S_ * 0.43)), (v863_K - (v865_z * 0.88)), (v862_D + (v864_S_ * 0.5)), (v863_K - (v865_z * 0.62)))
v823_e.lineTo((v862_D + (v864_S_ * 0.5)), v863_K)
v823_e.closePath()
if (truthy(v866_X)) {
v823_e.fillStyle = v866_X
v823_e.fill()
}
v823_e.strokeStyle = v867_q
v823_e.lineWidth = v869_W
v823_e.stroke()
v823_e.restore()
}
v833_s = v823_e.createLinearGradient(0.0, 0.0, 0.0, v825_o)
v833_s!!.addColorStop(0.0, "#10142d")
v833_s!!.addColorStop(0.48, jsGet(jsGet(v832_n, "sky"), 1.0))
v833_s!!.addColorStop(1.0, jsGet(jsGet(v832_n, "floor"), 0.0))
v823_e.fillStyle = v833_s!!
v823_e.fillRect(0.0, 0.0, v824_a, v825_o)
v834_i(v826_r, (v827_t + (v825_o * 0.03)), (v824_a * 0.66), (v825_o * 0.84), "rgba(7,12,33,0.34)", jsGet(v832_n, "primary"), 0.95, jsMax(2.0, (v824_a * 0.003)))
v834_i(v826_r, (v827_t + (v825_o * 0.02)), (v824_a * 0.57), (v825_o * 0.76), null, "#d5faff", 0.62, jsMax(1.0, (v824_a * 0.0015)))
v834_i((v824_a * 0.17), v827_t, (v824_a * 0.23), (v825_o * 0.56), "rgba(8,12,31,0.28)", jsGet(v832_n, "secondary"), 0.74, 1.4)
v834_i((v824_a * 0.83), v827_t, (v824_a * 0.23), (v825_o * 0.56), "rgba(8,12,31,0.28)", jsGet(v832_n, "secondary"), 0.74, 1.4)
v835_g = jsArr(jsGet(v832_n, "primary"), "#4de7d1", "#b884ff", jsGet(v832_n, "accent"), "#ff75b8", "#7bd6ff")
run {
v836_m = 0.0
L118@ while ((v836_m < 6.0)) {
v837_d = (if ((v836_m < 3.0)) -1.0 else 1.0)
v838_y = (v836_m % 3.0)
v839_c = (v826_r + ((v837_d * v824_a) * (0.23 + (v838_y * 0.105))))
v840_S = (v825_o * (0.25 + ((2.0 - v838_y) * 0.045)))
v841_v = (v824_a * 0.047)
v842_T = (v827_t - (v840_S * 0.42))
v823_e.save()
v823_e.globalAlpha = (0.26 + (v831_M * 0.08))
v823_e.beginPath()
v823_e.moveTo((v839_c - (v841_v * 0.5)), (v842_T + (v840_S * 0.5)))
v823_e.lineTo((v839_c - (v841_v * 0.5)), (v842_T - (v840_S * 0.26)))
v823_e.quadraticCurveTo((v839_c - (v841_v * 0.42)), (v842_T - (v840_S * 0.49)), v839_c, (v842_T - (v840_S * 0.58)))
v823_e.quadraticCurveTo((v839_c + (v841_v * 0.42)), (v842_T - (v840_S * 0.49)), (v839_c + (v841_v * 0.5)), (v842_T - (v840_S * 0.26)))
v823_e.lineTo((v839_c + (v841_v * 0.5)), (v842_T + (v840_S * 0.5)))
v823_e.closePath()
v823_e.fillStyle = jsGet(v835_g, v836_m)
v823_e.fill()
v823_e.strokeStyle = "#d7ffff"
v823_e.lineWidth = 1.0
v823_e.stroke()
v823_e.globalAlpha = 0.55
v823_e.strokeStyle = "#071025"
v823_e.beginPath()
v823_e.moveTo(v839_c, (v842_T - (v840_S * 0.54)))
v823_e.lineTo(v839_c, (v842_T + (v840_S * 0.48)))
v823_e.moveTo((v839_c - (v841_v * 0.45)), (v842_T + (v840_S * 0.05)))
v823_e.lineTo((v839_c + (v841_v * 0.45)), (v842_T + (v840_S * 0.05)))
v823_e.stroke()
v823_e.restore()
v836_m++
}
}
run {
v843_b = 0.0
L119@ while ((v843_b < 6.0)) {
v844_h = (v843_b % 3.0)
v845_p = (if ((v843_b < 3.0)) -1.0 else 1.0)
v846_w = (v826_r + ((v845_p * v824_a) * (0.31 + (v844_h * 0.13))))
v847_A = (v825_o * (0.2 + (v844_h * 0.08)))
v848_C = (v824_a * (0.012 + (v844_h * 0.005)))
v849_E = v823_e.createLinearGradient((v846_w - v848_C), 0.0, (v846_w + v848_C), 0.0)
v849_E!!.addColorStop(0.0, "#17223b")
v849_E!!.addColorStop(0.42, "#c1f5ff")
v849_E!!.addColorStop(0.58, "#526e91")
v849_E!!.addColorStop(1.0, "#10182e")
v823_e.fillStyle = v849_E!!
v823_e.fillRect((v846_w - (v848_C * 0.5)), v847_A, v848_C, (v827_t - v847_A))
v823_e.strokeStyle = jsGet(v832_n, "primary")
v823_e.lineWidth = 1.1
v823_e.strokeRect((v846_w - (v848_C * 0.5)), v847_A, v848_C, (v827_t - v847_A))
v823_e.fillStyle = jsGet(v832_n, "secondary")
v823_e.fillRect((v846_w - (v848_C * 0.9)), (v847_A - (v825_o * 0.012)), (v848_C * 1.8), (v825_o * 0.018))
v823_e.fillRect((v846_w - (v848_C * 0.72)), (v827_t - (v825_o * 0.012)), (v848_C * 1.44), (v825_o * 0.018))
v843_b++
}
}
v850_k = (v825_o * 0.275)
v851_R = (jsMin(v824_a, v825_o) * (0.145 + (v830_f * 0.006)))
v823_e.save()
v823_e.translate(v826_r, v850_k)
v823_e.rotate((v828_l * 0.075))
run {
v852_F = 0.0
L120@ while ((v852_F < 3.0)) {
v823_e.beginPath()
v823_e.arc(0.0, 0.0, (v851_R * (1.0 - (v852_F * 0.22))), 0.0, (Math.PI * 2.0))
v823_e.strokeStyle = (if ((v852_F == 0.0)) "#ecffff" else (if ((v852_F == 1.0)) jsGet(v832_n, "primary") else jsGet(v832_n, "secondary")))
v823_e.lineWidth = (if ((v852_F == 0.0)) 2.3 else 1.25)
v823_e.globalAlpha = (0.85 - (v852_F * 0.14))
v823_e.stroke()
v852_F++
}
}
run {
v853_O = 0.0
L121@ while ((v853_O < 12.0)) {
v854_P = ((v853_O * Math.PI) / 6.0)
v855_I = (v851_R * 0.22)
v856_U_ = (v851_R * 0.92)
v857_B = jsGet(v835_g, (v853_O % jsLen(v835_g)))
v823_e.beginPath()
v823_e.moveTo((kotlin.math.cos(v854_P) * v855_I), (kotlin.math.sin(v854_P) * v855_I))
v823_e.lineTo((kotlin.math.cos((v854_P - 0.17)) * v856_U_), (kotlin.math.sin((v854_P - 0.17)) * v856_U_))
v823_e.lineTo((kotlin.math.cos((v854_P + 0.17)) * v856_U_), (kotlin.math.sin((v854_P + 0.17)) * v856_U_))
v823_e.closePath()
v823_e.fillStyle = v857_B
v823_e.globalAlpha = (0.2 + (v831_M * 0.12))
v823_e.fill()
v823_e.strokeStyle = "#e7ffff"
v823_e.globalAlpha = 0.72
v823_e.lineWidth = 0.9
v823_e.stroke()
v823_e.beginPath()
v823_e.moveTo((kotlin.math.cos(v854_P) * v855_I), (kotlin.math.sin(v854_P) * v855_I))
v823_e.lineTo((kotlin.math.cos(v854_P) * v856_U_), (kotlin.math.sin(v854_P) * v856_U_))
v823_e.strokeStyle = "#c9faff"
v823_e.lineWidth = 1.0
v823_e.stroke()
v853_O++
}
}
v823_e.beginPath()
v823_e.arc(0.0, 0.0, (v851_R * 0.18), 0.0, (Math.PI * 2.0))
v823_e.fillStyle = jsGet(v832_n, "accent")
v823_e.globalAlpha = 0.9
v823_e.fill()
v823_e.restore()
v823_e.globalAlpha = 1.0
v858_L = v823_e.createLinearGradient(0.0, v827_t, 0.0, v825_o)
v858_L!!.addColorStop(0.0, "#273454")
v858_L!!.addColorStop(0.45, jsGet(jsGet(v832_n, "floor"), 1.0))
v858_L!!.addColorStop(1.0, "#071020")
v823_e.fillStyle = v858_L!!
v823_e.fillRect(0.0, v827_t, v824_a, (v825_o - v827_t))
v823_e.beginPath()
run {
v859_G = -8.0
L122@ while ((v859_G <= 8.0)) {
v823_e.moveTo((v826_r + ((v859_G * v824_a) * 0.012)), v827_t)
v823_e.lineTo((v826_r + ((v859_G * v824_a) * 0.16)), v825_o)
v859_G++
}
}
run {
v860_U = 1.0
L123@ while ((v860_U <= 8.0)) {
v861_V = (v827_t + ((v825_o - v827_t) * Math.pow((v860_U / 9.0), 1.7)))
v823_e.moveTo(0.0, v861_V)
v823_e.lineTo(v824_a, v861_V)
v860_U++
}
}
v823_e.strokeStyle = "rgba(120,235,255,0.44)"
v823_e.lineWidth = 1.0
v823_e.stroke()
v823_e.fillStyle = jsGet(v832_n, "accent")
v823_e.globalAlpha = (0.2 + (v830_f * 0.1))
v823_e.beginPath()
v823_e.moveTo((v826_r - (v824_a * 0.018)), v827_t)
v823_e.lineTo((v826_r + (v824_a * 0.018)), v827_t)
v823_e.lineTo((v826_r + (v824_a * 0.12)), v825_o)
v823_e.lineTo((v826_r - (v824_a * 0.12)), v825_o)
v823_e.closePath()
v823_e.fill()
v823_e.globalAlpha = 1.0
}

fun renderScene17_LiminalAtrium(v870_e: Ctx, v871_a: Double, v872_o: Double, v873_r: Double, v874_t: Double, v875_l: Double, v876_u: Double, v877_f: Double, v878_M: Double, v879_n: Any?): Unit {

var v880_s: Grad? = null
var v881_i: Double = 0.0
var v882_g: Double = 0.0
var v883_m: Double = 0.0
var v884_d: Double = 0.0
var v885_y: Double = 0.0
var v886_c: Grad? = null
var v887_S: Double = 0.0
var v888_v: Double = 0.0
var v889_T: Double = 0.0
var v890_b: Double = 0.0
var v891_h: Double = 0.0
var v892_p: Any? = null
var v893_w: Any? = null
var v894_A: Double = 0.0
var v895_C: Double = 0.0
var v896_E: Double = 0.0
var v897_k: Double = 0.0
var v898_R: Any? = null
var v899_F: Any? = null
var v900_O: Double = 0.0
var v901_P: Double = 0.0
var v902_I: Double = 0.0
var v903_U_: Double = 0.0
var v904_B: Double = 0.0
var v905_L: Boolean = false
var v906_G: Double = 0.0
var v907_U: Any? = null
var v908_V: Grad? = null
var v909_D: Double = 0.0
var v910_K: Double = 0.0
var v911_S_: Double = 0.0
var v912_z: Double = 0.0
var v913_X: Double = 0.0
var v914_q: Double = 0.0
var v915_Q: Double = 0.0
var v916_W: Grad? = null
var v917_te: Double = 0.0
var v918_le: Double = 0.0
var v919_ie: Double = 0.0
var v920_J: Double = 0.0
var v921_de: Double = 0.0
var v922_ee: Double = 0.0
var v923_se: Double = 0.0
v880_s = v870_e.createLinearGradient(0.0, 0.0, 0.0, v872_o)
v880_s!!.addColorStop(0.0, jsGet(jsGet(v879_n, "sky"), 1.0))
v880_s!!.addColorStop(0.42, jsGet(jsGet(v879_n, "sky"), 2.0))
v880_s!!.addColorStop(1.0, jsGet(jsGet(v879_n, "floor"), 0.0))
v870_e.fillStyle = v880_s!!
v870_e.fillRect(0.0, 0.0, v871_a, v872_o)
v881_i = (v871_a * 0.13)
v882_g = (v872_o * 0.2)
v883_m = (v882_g + (v872_o * 0.03))
v884_d = (v873_r - v881_i)
v885_y = (v873_r + v881_i)
v870_e.fillStyle = jsGet(v879_n, "mtnBot")
v870_e.globalAlpha = 0.85
v870_e.beginPath()
v870_e.moveTo(0.0, 0.0)
v870_e.lineTo(v871_a, 0.0)
v870_e.lineTo(v885_y, v883_m)
v870_e.lineTo(v884_d, v883_m)
v870_e.closePath()
v870_e.fill()
v886_c = v870_e.createLinearGradient(0.0, 0.0, 0.0, v883_m)
v886_c!!.addColorStop(0.0, (("rgba(" + str(jsGet(v879_n, "waveRgb"))) + ",0.55)"))
v886_c!!.addColorStop(1.0, (("rgba(" + str(jsGet(v879_n, "waveRgb"))) + ",0.15)"))
v870_e.fillStyle = v886_c!!
v870_e.globalAlpha = 1.0
v870_e.beginPath()
v870_e.moveTo((v871_a * 0.27), 0.0)
v870_e.lineTo((v871_a * 0.73), 0.0)
v870_e.lineTo((v873_r + (v881_i * 0.55)), v883_m)
v870_e.lineTo((v873_r - (v881_i * 0.55)), v883_m)
v870_e.closePath()
v870_e.fill()
v870_e.strokeStyle = jsGet(v879_n, "mtnTop")
v870_e.globalAlpha = 0.5
v870_e.lineWidth = 1.0
v870_e.beginPath()
run {
v887_S = 0.0
L124@ while ((v887_S <= 6.0)) {
v888_v = (v887_S / 6.0)
v870_e.moveTo((v871_a * (0.27 + (v888_v * 0.46))), 0.0)
v870_e.lineTo((v873_r + ((v881_i * 0.55) * ((v888_v * 2.0) - 1.0))), v883_m)
v887_S++
}
}
run {
v889_T = 1.0
L125@ while ((v889_T <= 4.0)) {
v890_b = (v883_m * (1.0 - Math.pow((1.0 - (v889_T / 5.0)), 1.6)))
v891_h = ((v871_a * 0.23) + (((v881_i * 0.55) - (v871_a * 0.23)) * (v890_b / v883_m)))
v870_e.moveTo((v873_r - v891_h), v890_b)
v870_e.lineTo((v873_r + v891_h), v890_b)
v889_T++
}
}
v870_e.stroke()
v870_e.globalAlpha = 0.95
v870_e.fillStyle = jsGet(jsGet(v879_n, "floor"), 1.0)
v870_e.fillRect(v884_d, v883_m, (v881_i * 2.0), (v874_t - v883_m))
v870_e.strokeStyle = jsGet(v879_n, "mtnTop")
v870_e.globalAlpha = 0.7
v870_e.lineWidth = 1.5
v870_e.strokeRect(v884_d, v883_m, (v881_i * 2.0), (v874_t - v883_m))
v870_e.globalAlpha = 0.75
v870_e.fillStyle = jsGet(v879_n, "sunSlice")
v870_e.fillRect((v873_r - (v881_i * 0.16)), (v874_t - ((v874_t - v883_m) * 0.34)), (v881_i * 0.32), ((v874_t - v883_m) * 0.34))
v870_e.globalAlpha = 0.9
v870_e.fillStyle = jsGet(v879_n, "accent")
v870_e.fillRect((v873_r - (v881_i * 0.1)), (v874_t - ((v874_t - v883_m) * 0.42)), (v881_i * 0.2), jsMax(2.0, (v872_o * 0.006)))
v892_p = jsArr(0.3, 0.55, 0.8)
v893_w = jsArr(0.0, 0.32, 0.55, 0.71, 0.82, 0.9, 1.0)
run {
v894_A = 0.0
L126@ while ((v894_A < 2.0)) {
v895_C = (if (truthy(v894_A)) 1.0 else -1.0)
v896_E = (if (truthy(v894_A)) v871_a else 0.0)
v897_k = (if (truthy(v894_A)) v885_y else v884_d)
v898_R = JsFn L127@{ args ->
var v926_ae: Double = 0.0
var v927_j: Double = 0.0
var v924_x: Any? = args.getOrNull(0)
var v925_N: Any? = args.getOrNull(1)
v926_ae = (v872_o * num(v925_N))
v927_j = (v883_m + ((v874_t - v883_m) * ((num(v925_N) - 0.2) / 0.8)))
return@L127 (v926_ae + ((v927_j - v926_ae) * num(v924_x)))
null
}
v899_F = JsFn L128@{ args ->
var v928_x: Any? = args.getOrNull(0)
return@L128 (v896_E + ((v897_k - v896_E) * num(v928_x)))
null
}
v870_e.globalAlpha = 0.75
v870_e.fillStyle = jsGet(jsGet(v879_n, "floor"), 1.0)
v870_e.beginPath()
v870_e.moveTo(v896_E, 0.0)
v870_e.lineTo(v897_k, v883_m)
v870_e.lineTo(v897_k, v874_t)
v870_e.lineTo(v896_E, v872_o)
v870_e.closePath()
v870_e.fill()
run {
v900_O = 0.0
L129@ while ((v900_O < jsLen(v892_p))) {
v901_P = (if ((v900_O == 0.0)) 0.08 else (num(jsGet(v892_p, (v900_O - 1.0))) + 0.035))
run {
v902_I = 0.0
L130@ while ((v902_I < (jsLen(v893_w) - 1.0))) {
v903_U_ = (num(jsGet(v893_w, v902_I)) + 0.02)
v904_B = (num(jsGet(v893_w, (v902_I + 1.0))) - 0.02)
v905_L = (((((v902_I * 3.0) + (v900_O * 5.0)) + v894_A) % 3.0) == 0.0)
v870_e.fillStyle = (if (v905_L) (((("rgba(" + str(jsGet(v879_n, "waveRgb"))) + ",") + jsToFixed((0.3 + (0.05 * kotlin.math.sin((((v875_l * 0.35) + v902_I) + v900_O)))), 3.0)) + ")") else "rgba(0,0,0,0.28)")
v870_e.globalAlpha = 1.0
v870_e.beginPath()
v870_e.moveTo(num(jsCall(v899_F, v903_U_)), num(jsCall(v898_R, v903_U_, v901_P)))
v870_e.lineTo(num(jsCall(v899_F, v904_B)), num(jsCall(v898_R, v904_B, v901_P)))
v870_e.lineTo(num(jsCall(v899_F, v904_B)), num(jsCall(v898_R, v904_B, jsGet(v892_p, v900_O))))
v870_e.lineTo(num(jsCall(v899_F, v903_U_)), num(jsCall(v898_R, v903_U_, jsGet(v892_p, v900_O))))
v870_e.closePath()
v870_e.fill()
v902_I++
}
}
v870_e.globalAlpha = 0.55
v870_e.fillStyle = jsGet(v879_n, "mtnTop")
v870_e.beginPath()
v870_e.moveTo(v896_E, (v872_o * num(jsGet(v892_p, v900_O))))
v870_e.lineTo(v897_k, num(jsCall(v898_R, 1.0, jsGet(v892_p, v900_O))))
v870_e.lineTo(v897_k, (num(jsCall(v898_R, 1.0, jsGet(v892_p, v900_O))) + (v872_o * 0.006)))
v870_e.lineTo(v896_E, ((v872_o * num(jsGet(v892_p, v900_O))) + (v872_o * 0.03)))
v870_e.closePath()
v870_e.fill()
v870_e.strokeStyle = jsGet(v879_n, "primary")
v870_e.globalAlpha = 0.6
v870_e.lineWidth = 1.2
v870_e.beginPath()
v870_e.moveTo((v896_E + (((v895_C * -v871_a) * 0.05) * 0.0)), ((v872_o * num(jsGet(v892_p, v900_O))) - (v872_o * 0.035)))
v870_e.lineTo(v897_k, (num(jsCall(v898_R, 1.0, jsGet(v892_p, v900_O))) - (v872_o * 0.008)))
v870_e.stroke()
v900_O++
}
}
v870_e.strokeStyle = jsGet(v879_n, "mtnBot")
v870_e.globalAlpha = 0.7
v870_e.lineWidth = 2.0
v870_e.beginPath()
run {
v906_G = 1.0
L131@ while ((v906_G < (jsLen(v893_w) - 1.0))) {
v907_U = jsCall(v899_F, jsGet(v893_w, v906_G))
v870_e.moveTo(num(v907_U), num(jsCall(v898_R, jsGet(v893_w, v906_G), 0.08)))
v870_e.lineTo(num(v907_U), num(jsCall(v898_R, jsGet(v893_w, v906_G), 1.0)))
v906_G++
}
}
v870_e.stroke()
v894_A++
}
}
v870_e.globalAlpha = 0.5
v870_e.fillStyle = jsGet(jsGet(v879_n, "floor"), 1.0)
v870_e.beginPath()
v870_e.moveTo(v884_d, v874_t)
v870_e.lineTo(v885_y, v874_t)
v870_e.lineTo(v871_a, v872_o)
v870_e.lineTo(0.0, v872_o)
v870_e.closePath()
v870_e.fill()
v908_V = v870_e.createLinearGradient(0.0, v874_t, 0.0, v872_o)
v908_V!!.addColorStop(0.0, (("rgba(" + str(jsGet(v879_n, "waveRgb"))) + ",0.28)"))
v908_V!!.addColorStop(1.0, (("rgba(" + str(jsGet(v879_n, "waveRgb"))) + ",0)"))
v870_e.globalAlpha = 1.0
v870_e.fillStyle = v908_V!!
v870_e.beginPath()
v870_e.moveTo((v873_r - (v881_i * 0.5)), v874_t)
v870_e.lineTo((v873_r + (v881_i * 0.5)), v874_t)
v870_e.lineTo((v873_r + (v871_a * 0.22)), v872_o)
v870_e.lineTo((v873_r - (v871_a * 0.22)), v872_o)
v870_e.closePath()
v870_e.fill()
v870_e.strokeStyle = jsGet(v879_n, "primary")
v870_e.globalAlpha = 0.3
v870_e.lineWidth = 1.0
v870_e.beginPath()
run {
v909_D = -6.0
L132@ while ((v909_D <= 6.0)) {
v870_e.moveTo((v873_r + ((v909_D * v881_i) / 6.0)), v874_t)
v870_e.lineTo((v873_r + ((v909_D * v871_a) / 12.0)), v872_o)
v909_D++
}
}
v910_K = ((v876_u * 0.12) % 1.0)
run {
v911_S_ = 0.0
L133@ while ((v911_S_ < 8.0)) {
v912_z = (v874_t + ((v872_o - v874_t) * Math.pow(((v911_S_ + v910_K) / 8.0), 1.75)))
v870_e.moveTo(0.0, v912_z)
v870_e.lineTo(v871_a, v912_z)
v911_S_++
}
}
v870_e.stroke()
run {
v913_X = 0.0
L134@ while ((v913_X < 3.0)) {
v914_q = (v873_r + (((v913_X - 1.0) * v871_a) * 0.12))
v915_Q = (0.06 + (0.025 * kotlin.math.sin(((v875_l * 0.25) + (v913_X * 2.1)))))
v916_W = v870_e.createLinearGradient(0.0, 0.0, 0.0, v872_o)
v916_W!!.addColorStop(0.0, (("rgba(255,255,255," + jsToFixed(v915_Q, 3.0)) + ")"))
v916_W!!.addColorStop(1.0, "rgba(255,255,255,0)")
v870_e.globalAlpha = 1.0
v870_e.fillStyle = v916_W!!
v870_e.beginPath()
v870_e.moveTo((v914_q - (v871_a * 0.03)), 0.0)
v870_e.lineTo((v914_q + (v871_a * 0.03)), 0.0)
v870_e.lineTo(((v914_q + (v871_a * 0.09)) + (((v913_X - 1.0) * v871_a) * 0.05)), v872_o)
v870_e.lineTo(((v914_q - (v871_a * 0.05)) + (((v913_X - 1.0) * v871_a) * 0.05)), v872_o)
v870_e.closePath()
v870_e.fill()
v913_X++
}
}
v917_te = (v874_t + ((v872_o - v874_t) * 0.42))
v918_le = (v871_a * 0.11)
v870_e.globalAlpha = 0.8
v870_e.fillStyle = jsGet(v879_n, "mtnTop")
v870_e.beginPath()
v870_e.ellipse(v873_r, v917_te, v918_le, (v918_le * 0.16), 0.0, 0.0, (Math.PI * 2.0))
v870_e.fill()
v870_e.fillStyle = (("rgba(" + str(jsGet(v879_n, "waveRgb"))) + ",0.35)")
v870_e.beginPath()
v870_e.ellipse(v873_r, (v917_te - 2.0), (v918_le * 0.88), (v918_le * 0.12), 0.0, 0.0, (Math.PI * 2.0))
v870_e.fill()
v870_e.strokeStyle = jsGet(v879_n, "accent")
v870_e.lineWidth = 1.0
run {
v919_ie = 0.0
L135@ while ((v919_ie < 3.0)) {
v920_J = (((v875_l * 0.12) + (v919_ie / 3.0)) % 1.0)
v870_e.globalAlpha = (0.45 * (1.0 - v920_J))
v870_e.beginPath()
v870_e.ellipse(v873_r, (v917_te - 2.0), (((v918_le * 0.85) * v920_J) + 2.0), (((v918_le * 0.12) * v920_J) + 1.0), 0.0, 0.0, (Math.PI * 2.0))
v870_e.stroke()
v919_ie++
}
}
js_forEach(jsArr(-1.0, 1.0)) L136@{ v929_x: Any?, _, _ ->
var v930_N: Double = 0.0
var v931_ae: Double = 0.0
var v932_j: Double = 0.0
var v933_oe: Double = 0.0
var v934_Y: Double = 0.0

v930_N = (v873_r + ((num(v929_x) * v871_a) * 0.3))
v931_ae = (v874_t + ((v872_o - v874_t) * 0.62))
v932_j = (v871_a * 0.035)
v870_e.globalAlpha = 0.9
v870_e.fillStyle = jsGet(v879_n, "mtnBot")
v870_e.fillRect((v930_N - v932_j), (v931_ae - (v932_j * 0.9)), (v932_j * 2.0), (v932_j * 0.9))
v870_e.fillStyle = jsGet(jsGet(v879_n, "floor"), 1.0)
v870_e.globalAlpha = 0.95
run {
v933_oe = 0.0
L137@ while ((v933_oe < 5.0)) {
v934_Y = (((-Math.PI / 2.0) + ((v933_oe - 2.0) * 0.45)) + (kotlin.math.sin(((v875_l * 0.3) + v933_oe)) * 0.03))
v870_e.beginPath()
v870_e.ellipse((v930_N + ((kotlin.math.cos(v934_Y) * v932_j) * 0.9)), ((v931_ae - (v932_j * 0.9)) + ((kotlin.math.sin(v934_Y) * v932_j) * 1.3)), (v932_j * 0.25), (v932_j * 0.9), (v934_Y + (Math.PI / 2.0)), 0.0, (Math.PI * 2.0))
v870_e.fill()
v933_oe++
}
}
null
}
v870_e.fillStyle = "#ffffff"
run {
v921_de = 0.0
L138@ while ((v921_de < 14.0)) {
v922_ee = ((v873_r + (((((v921_de * 0.137) % 1.0) - 0.5) * v871_a) * 0.4)) + (kotlin.math.sin(((v875_l * 0.2) + v921_de)) * 10.0))
v923_se = ((((v921_de * 0.291) + (v875_l * 0.012)) % 1.0) * v872_o)
v870_e.globalAlpha = (0.25 + (0.15 * kotlin.math.sin(((v875_l * 0.5) + v921_de))))
v870_e.fillRect(v922_ee, v923_se, 1.6, 1.6)
v921_de++
}
}
v870_e.globalAlpha = 1.0
}

fun renderScene24_AetherSpires(v935_e: Ctx, v936_a: Double, v937_o: Double, v938_r: Double, v939_t: Double, v940_l: Double, v941_u: Double, v942_f: Double, v943_M: Double, v944_n: Any?): Unit {

val v945_s: Double = (jsMin(v936_a, v937_o) * 0.78)
val v946_i: Double = (Rt.mouseX * 55.0)
val v947_g: Double = (Rt.mouseY * 28.0)
val v948_m: Double = (v938_r - (v946_i * 0.35))
val v949_d: Double = ((v939_t + (kotlin.math.sin((v940_l * 0.45)) * 8.0)) + (v947_g * 0.25))
val v950_y: Grad = v935_e.createRadialGradient(v948_m, (v949_d + ((v937_o - v949_d) * 0.42)), 8.0, v948_m, (v949_d + ((v937_o - v949_d) * 0.45)), (jsMax(v936_a, v937_o) * 0.75))
v950_y.addColorStop(0.0, ("rgba(" + str(jsGet(v944_n, "waveRgb")) + ", " + jsToFixed((0.42 + (v942_f * 0.25)), 3.0) + ")"))
v950_y.addColorStop(0.32, ("rgba(" + str(jsGet(v944_n, "altWaveRgb")) + ", 0.24)"))
v950_y.addColorStop(0.68, jsGet(jsGet(v944_n, "floor"), 1.0))
v950_y.addColorStop(1.0, jsGet(jsGet(v944_n, "floor"), 0.0))
v935_e.fillStyle = v950_y
v935_e.fillRect(0.0, (v949_d - 10.0), v936_a, ((v937_o - v949_d) + 10.0))
v935_e.save()
v935_e.globalCompositeOperation = "lighter"
run {
var v957_p: Double = 0.0
L139@ while ((v957_p < 9.0)) {
val v958_w: Double = ((-0.95 + ((v957_p / 8.0) * 1.9)) + (kotlin.math.sin(((v940_l * 0.3) + v957_p)) * 0.06))
val v959_A: Double = (jsMax(v936_a, v937_o) * 0.85)
val v960_C: Double = ((24.0 + ((v957_p % 3.0) * 18.0)) + (v942_f * 26.0))
val v961_E: Double = (v948_m + (kotlin.math.sin(v958_w) * v959_A))
val v962_k: Double = (v949_d - ((kotlin.math.cos(v958_w) * v959_A) * 0.55))
val v963_R: Grad = v935_e.createLinearGradient(v948_m, v949_d, v961_E, v962_k)
v963_R.addColorStop(0.0, (if (((v957_p % 2.0) == 0.0)) ("rgba(" + str(jsGet(v944_n, "waveRgb")) + ", 0.16)") else ("rgba(" + str(jsGet(v944_n, "altWaveRgb")) + ", 0.14)")))
v963_R.addColorStop(1.0, "transparent")
v935_e.fillStyle = v963_R
v935_e.beginPath()
v935_e.moveTo((v948_m - 6.0), v949_d)
v935_e.lineTo((v961_E - v960_C), v962_k)
v935_e.lineTo((v961_E + v960_C), v962_k)
v935_e.lineTo((v948_m + 6.0), v949_d)
v935_e.closePath()
v935_e.fill()
v957_p++
}
}
v935_e.restore()
val v951_c: Double = (v949_d - (v937_o * 0.15))
val v952_S: Double = ((jsMin(v936_a, v937_o) * 0.24) * (1.0 + (v942_f * 0.08)))
run {
var v964_p: Double = 0.0
L140@ while ((v964_p < 3.0)) {
val v965_w: Double = 32.0
val v966_A: Double = (v952_S * (1.0 - (v964_p * 0.21)))
val v967_C: Double = ((0.55 + (v964_p * 0.22)) + (kotlin.math.sin(((v940_l * 0.4) + v964_p)) * 0.12))
val v968_E: Double = ((if (((v964_p % 2.0) == 0.0)) 1.0 else -1.0) * (v940_l * (0.35 + (v964_p * 0.12))))
v935_e.save()
v935_e.translate(v948_m, v951_c)
v935_e.rotate((v968_E * 0.25))
run {
var v969_k: Double = 0.0
L141@ while ((v969_k < v965_w)) {
val v970_R: Double = ((((v969_k / v965_w) * Math.PI) * 2.0) + v968_E)
val v971_F: Double = (((((v969_k + 0.72) / v965_w) * Math.PI) * 2.0) + v968_E)
val v972_O: Double = kotlin.math.sin(((v970_R + v971_F) * 0.5))
v935_e.strokeStyle = (if (((v969_k % 2.0) == 0.0)) jsGet(v944_n, "primary") else jsGet(v944_n, "accent"))
v935_e.globalAlpha = (0.25 + ((v972_O + 1.0) * 0.34))
v935_e.lineWidth = ((1.4 + ((v972_O + 1.0) * 1.6)) * (if ((v964_p == 0.0)) 1.2 else 0.85))
v935_e.beginPath()
v935_e.ellipse(0.0, 0.0, v966_A, (v966_A * kotlin.math.cos(v967_C)), 0.0, v970_R, v971_F)
v935_e.stroke()
v969_k++
}
}
v935_e.restore()
v964_p++
}
}
val v953_v: Double = 22.0
val v954_T: Double = rrSceneMod((v941_u * 0.65), 1.0)
run {
var v973_p: Double = v953_v
L142@ while ((v973_p >= 1.0)) {
L142c@ do {
val v974_w: Double = (v941_u * 0.65)
val v975_A: Double = (v973_p + kotlin.math.floor(v974_w))
val v976_C: Double = ((v973_p - v954_T) * 0.24)
val v977_E: Double = (((v973_p + 1.0) - v954_T) * 0.24)
val v978_k: Double = (1.0 / (0.35 + v976_C))
val v979_R: Double = (1.0 / (0.35 + v977_E))
val v980_F: Double = ((kotlin.math.sin(((v976_C * 0.8) - (v940_l * 1.4))) * 32.0) * v978_k)
val v981_O: Double = ((kotlin.math.sin(((v977_E * 0.8) - (v940_l * 1.4))) * 32.0) * v979_R)
val v982_P: Double = (v949_d + ((((v937_o - v949_d) * 0.62) * v978_k) * 0.42))
val v983_I: Double = (v949_d + ((((v937_o - v949_d) * 0.62) * v979_R) * 0.42))
val v984_U_: Double = ((v936_a * 0.11) * v978_k)
val v985_B: Double = ((v936_a * 0.11) * v979_R)
val v986_L: Double = (v948_m + ((v980_F + v981_O) * 0.5))
val v987_G: Double = ((v982_P + v983_I) * 0.5)
val v988_U: Double = ((jsMax(v984_U_, v985_B) + kotlin.math.abs((v980_F - v981_O))) + 12.0)
val v989_V: Double = ((kotlin.math.abs((v982_P - v983_I)) * 0.5) + 12.0)
val v990_D: Double = rrSceneViewportFade(v986_L, v987_G, v988_U, v989_V, v936_a, v937_o, jsMax(48.0, (v937_o * 0.075)))
val v991_K: Double = jsMax(v982_P, v983_I)
val v992_S_: Double = (1.0 - rrSceneSmoothstep((v937_o * 0.72), (v937_o * 0.985), v991_K))
val v993_z: Double = ((jsMin(0.92, (v978_k * 0.65)) * v990_D) * v992_S_)
if ((v993_z <= 0.002)) {
break@L142c
}
v935_e.save()
v935_e.globalAlpha = v993_z
v935_e.fillStyle = (if (((v975_A % 2.0) == 0.0)) ("rgba(" + str(jsGet(v944_n, "waveRgb")) + ", 0.18)") else "rgba(8, 3, 24, 0.76)")
v935_e.strokeStyle = (if (((v975_A % 2.0) == 0.0)) jsGet(v944_n, "primary") else jsGet(v944_n, "secondary"))
v935_e.lineWidth = jsMax(0.8, ((1.8 * v978_k) * 0.4))
v935_e.beginPath()
v935_e.moveTo(((v948_m + v981_O) - v985_B), v983_I)
v935_e.lineTo(((v948_m + v981_O) + v985_B), v983_I)
v935_e.lineTo(((v948_m + v980_F) + v984_U_), v982_P)
v935_e.lineTo(((v948_m + v980_F) - v984_U_), v982_P)
v935_e.closePath()
v935_e.fill()
v935_e.stroke()
v935_e.restore()
} while (false)
v973_p--
}
}
val v955_b: Double = 10.0
val v956_h: Any? = jsArr()
run {
var v994_p: Double = 0.0
L143@ while ((v994_p < v955_b)) {
val v995_w: Double = (rrSceneMod((v994_p + (v941_u * 0.32)), v955_b) / v955_b)
jsPush(v956_h, jsObj("idx" to v994_p, "norm" to v995_w, "persp" to Math.pow(v995_w, 1.75)))
v994_p++
}
}
js_sort(v956_h) L144@{ v996_p: Any?, v997_w: Any? ->

(num(jsGet(v996_p, "norm")) - num(jsGet(v997_w, "norm")))
}
js_forEach(v956_h) L145@{ p146: Any?, _, _ ->
var v998_p: Any? = jsGet(p146, "idx")
var v999_w: Any? = jsGet(p146, "norm")
var v1000_A: Any? = jsGet(p146, "persp")
val v1001_C: Double = (if (((num(v998_p) % 2.0) == 0.0)) -1.0 else 1.0)
val v1002_E: Double = ((((num(v998_p) * 3.0) % 5.0) - 2.0) * 0.04)
val v1003_k: Double = (kotlin.math.sin(((v940_l * 1.7) + (num(v998_p) * 1.3))) * (16.0 * num(v1000_A)))
val v1004_R: Double = ((Rt.freqDataArr[jsIdx(((num(v998_p) * 7.0) % 64.0))] / 255.0) * (42.0 * num(v1000_A)))
val v1005_F: Double = (v948_m + (v1001_C * ((v936_a * (0.14 + kotlin.math.abs(v1002_E))) + ((num(v1000_A) * v936_a) * 0.36))))
val v1006_O: Double = (((v949_d + ((num(v1000_A) - 0.32) * (v937_o * 0.42))) + v1003_k) - (v1004_R * 0.35))
val v1007_P: Double = ((18.0 + (num(v1000_A) * 74.0)) * (jsMin(v936_a, v937_o) / 860.0))
val v1008_I: Double = ((v1007_P * (2.5 + ((num(v998_p) % 3.0) * 0.55))) + v1004_R)
val v1009_U_: Double = (v1007_P * 1.55)
val v1010_B: Double = (((v940_l * 0.35) * v1001_C) + num(v998_p))
val v1011_L: Double = (rrSceneSmoothstep(0.015, 0.14, num(v999_w)) * (1.0 - rrSceneSmoothstep(0.84, 0.985, num(v999_w))))
val v1012_G: Double = (v1006_O - v1008_I)
val v1013_U: Double = (v1006_O + (v1009_U_ * 2.35))
val v1014_V: Double = rrSceneViewportFade(v1005_F, ((v1012_G + v1013_U) * 0.5), (v1007_P * 1.5), ((v1013_U - v1012_G) * 0.5), v936_a, v937_o, jsMax(48.0, (jsMin(v936_a, v937_o) * 0.075)))
val v1015_D: Double = (v1011_L * v1014_V)
if ((v1015_D <= 0.002)) {
return@L145 null
}
v935_e.save()
v935_e.globalAlpha = v1015_D
val v1016_K: Double = 6.0
val v1017_S_: Any? = jsArr()
run {
var v1021_Q: Double = 0.0
L147@ while ((v1021_Q < v1016_K)) {
val v1022_W: Double = (v1010_B + (((v1021_Q / v1016_K) * Math.PI) * 2.0))
jsPush(v1017_S_, jsObj("x" to (v1005_F + (kotlin.math.cos(v1022_W) * v1007_P)), "y" to (v1006_O + ((kotlin.math.sin(v1022_W) * v1007_P) * 0.32)), "z" to kotlin.math.sin(v1022_W)))
v1021_Q++
}
}
val v1018_z: Grad = v935_e.createLinearGradient(v1005_F, (v1006_O + (v1009_U_ * 0.5)), v1005_F, (v1006_O + (v1009_U_ * 2.4)))
v1018_z.addColorStop(0.0, jsGet(v944_n, "primary"))
v1018_z.addColorStop(1.0, "transparent")
v935_e.strokeStyle = v1018_z
v935_e.lineWidth = jsMax(1.0, (2.6 * num(v1000_A)))
v935_e.beginPath()
v935_e.moveTo(v1005_F, (v1006_O + (v1009_U_ * 0.65)))
v935_e.lineTo((v1005_F + ((kotlin.math.sin(((v940_l * 3.0) + num(v998_p))) * 6.0) * num(v1000_A))), (v1006_O + (v1009_U_ * 2.35)))
v935_e.stroke()
run {
var v1023_Q: Double = 0.0
L148@ while ((v1023_Q < v1016_K)) {
L148c@ do {
val v1024_W: Any? = jsGet(v1017_S_, v1023_Q)
val v1025_te: Any? = jsGet(v1017_S_, ((v1023_Q + 1.0) % v1016_K))
if (((num(jsGet(v1024_W, "z")) + num(jsGet(v1025_te, "z"))) < -0.2)) {
break@L148c
}
v935_e.fillStyle = (if (((v1023_Q % 2.0) == 0.0)) jsGet(jsGet(v944_n, "floor"), 0.0) else jsGet(v944_n, "sunSlice"))
v935_e.strokeStyle = jsGet(v944_n, "secondary")
v935_e.lineWidth = 1.2
v935_e.beginPath()
v935_e.moveTo(num(jsGet(v1024_W, "x")), num(jsGet(v1024_W, "y")))
v935_e.lineTo(num(jsGet(v1025_te, "x")), num(jsGet(v1025_te, "y")))
v935_e.lineTo(v1005_F, (v1006_O + v1009_U_))
v935_e.closePath()
v935_e.fill()
v935_e.stroke()
} while (false)
v1023_Q++
}
}
val v1019_X: Double = 3.0
run {
var v1026_Q: Double = 0.0
L149@ while ((v1026_Q < v1019_X)) {
val v1027_W: Double = (v1026_Q / v1019_X)
val v1028_te: Double = ((v1026_Q + 1.0) / v1019_X)
val v1029_le: Double = (v1006_O - (v1008_I * v1027_W))
val v1030_ie: Double = (v1006_O - (v1008_I * v1028_te))
val v1031_J: Double = (v1007_P * (0.72 * (1.0 - (v1027_W * 0.65))))
val v1032_de: Double = (v1007_P * (0.72 * (1.0 - (v1028_te * 0.88))))
run {
var v1033_ee: Double = 0.0
L150@ while ((v1033_ee < v1016_K)) {
L150c@ do {
val v1034_se: Double = ((v1010_B + (v1026_Q * 0.25)) + (((v1033_ee / v1016_K) * Math.PI) * 2.0))
val v1035_x: Double = ((v1010_B + (v1026_Q * 0.25)) + ((((v1033_ee + 1.0) / v1016_K) * Math.PI) * 2.0))
if ((kotlin.math.sin(((v1034_se + v1035_x) * 0.5)) < -0.15)) {
break@L150c
}
val v1036_N: Double = (v1005_F + (kotlin.math.cos(v1034_se) * v1031_J))
val v1037_ae: Double = (v1029_le + ((kotlin.math.sin(v1034_se) * v1031_J) * 0.28))
val v1038_j: Double = (v1005_F + (kotlin.math.cos(v1035_x) * v1031_J))
val v1039_oe: Double = (v1029_le + ((kotlin.math.sin(v1035_x) * v1031_J) * 0.28))
val v1040_Y: Double = (v1005_F + (kotlin.math.cos(v1034_se) * v1032_de))
val v1041_ne: Double = (v1030_ie + ((kotlin.math.sin(v1034_se) * v1032_de) * 0.28))
val v1042_he: Double = (v1005_F + (kotlin.math.cos(v1035_x) * v1032_de))
val v1043_ve: Double = (v1030_ie + ((kotlin.math.sin(v1035_x) * v1032_de) * 0.28))
v935_e.fillStyle = (if ((((v1033_ee + v1026_Q) % 2.0) == 0.0)) jsGet(v944_n, "mtnBot") else ("rgba(" + str(jsGet(v944_n, "waveRgb")) + ", 0.26)"))
v935_e.strokeStyle = (if ((v1026_Q == (v1019_X - 1.0))) jsGet(v944_n, "accent") else jsGet(v944_n, "primary"))
v935_e.lineWidth = 1.25
v935_e.beginPath()
v935_e.moveTo(v1036_N, v1037_ae)
v935_e.lineTo(v1038_j, v1039_oe)
v935_e.lineTo(v1042_he, v1043_ve)
v935_e.lineTo(v1040_Y, v1041_ne)
v935_e.closePath()
v935_e.fill()
v935_e.stroke()
} while (false)
v1033_ee++
}
}
v1026_Q++
}
}
val v1020_q: Double = (v1006_O - v1008_I)
v935_e.fillStyle = "#ffffff"
v935_e.shadowColor = jsGet(v944_n, "primary")
v935_e.shadowBlur = 12.0
v935_e.beginPath()
v935_e.arc(v1005_F, v1020_q, jsMax(2.0, (4.5 * num(v1000_A))), 0.0, (Math.PI * 2.0))
v935_e.fill()
v935_e.shadowBlur = 0.0
v935_e.strokeStyle = ("rgba(" + str(jsGet(v944_n, "waveRgb")) + ", " + jsToFixed((0.22 + (v942_f * 0.25)), 3.0) + ")")
v935_e.lineWidth = (1.0 + (num(v1000_A) * 1.4))
v935_e.beginPath()
v935_e.moveTo(v1005_F, v1020_q)
v935_e.lineTo(v948_m, v951_c)
v935_e.stroke()
v935_e.restore()
null
}
}

fun renderScene25_PrismCitadel(v1044_e: Ctx, v1045_a: Double, v1046_o: Double, v1047_r: Double, v1048_t: Double, v1049_l: Double, v1050_u: Double, v1051_f: Double, v1052_M: Double, v1053_n: Any?): Unit {

val v1054_s: Double = (v1047_r - (Rt.mouseX * 26.0))
val v1055_i: Double = (v1048_t + (Rt.mouseY * 14.0))
v1044_e.save()
val v1056_g: Double = 5.0
val v1057_m: Double = 14.0
run {
var v1079_L: Double = 1.0
L151@ while ((v1079_L <= v1056_g)) {
val v1080_G: Double = (v1079_L / v1056_g)
val v1081_U: Double = (v1055_i * (1.0 - Math.pow(v1080_G, 1.35)))
v1044_e.strokeStyle = (if (((v1079_L % 2.0) == 0.0)) jsGet(v1053_n, "primary") else jsGet(v1053_n, "secondary"))
v1044_e.globalAlpha = (0.16 + ((1.0 - v1080_G) * 0.22))
v1044_e.lineWidth = 1.2
v1044_e.beginPath()
run {
var v1082_V: Double = 0.0
L152@ while ((v1082_V <= v1057_m)) {
val v1083_D: Double = ((v1082_V / v1057_m) * v1045_a)
val v1084_K: Double = (kotlin.math.sin(((v1082_V / v1057_m) * Math.PI)) * ((v1046_o * 0.08) * v1080_G))
if ((v1082_V == 0.0)) {
v1044_e.moveTo(v1083_D, (v1081_U - v1084_K))
} else {
v1044_e.lineTo(v1083_D, (v1081_U - v1084_K))
}
v1082_V++
}
}
v1044_e.stroke()
v1079_L++
}
}
v1044_e.restore()
val v1058_d: Grad = v1044_e.createLinearGradient(0.0, v1055_i, 0.0, v1046_o)
v1058_d.addColorStop(0.0, jsGet(jsGet(v1053_n, "floor"), 0.0))
v1058_d.addColorStop(1.0, jsGet(jsGet(v1053_n, "floor"), 0.0))
v1044_e.fillStyle = v1058_d
v1044_e.fillRect(0.0, v1055_i, v1045_a, (v1046_o - v1055_i))
val v1059_y: Double = 11.0
val v1060_c: Double = 11.0
val v1061_S: Double = (v1050_u * 0.75)
val v1062_v: Double = rrSceneMod(v1061_S, 1.0)
val v1063_T: Double = kotlin.math.floor((v1050_u * 0.75))
run {
var v1085_L: Double = 1.0
L153@ while ((v1085_L <= v1059_y)) {
val v1086_G: Double = ((v1085_L + v1062_v) / (v1059_y + 1.0))
val v1087_U: Double = Math.pow(v1086_G, 1.95)
val v1088_V: Double = (v1055_i + (v1087_U * (v1046_o - v1055_i)))
val v1089_D: Double = ((8.0 + (v1087_U * 52.0)) * (v1045_a / 1180.0))
val v1090_K: Double = (v1089_D * 0.36)
val v1091_S_: Double = (if (truthy((jsInt((v1085_L - v1063_T)) and jsInt(1.0)).toDouble())) 0.5 else 0.0)
run {
var v1092_z: Double = -v1060_c
L154@ while ((v1092_z <= v1060_c)) {
L154c@ do {
val v1093_X: Double = (v1092_z + v1091_S_)
val v1094_q: Double = (v1054_s + ((v1093_X * v1089_D) * 1.82))
if (((v1094_q < (-v1089_D * 2.0)) || (v1094_q > (v1045_a + (v1089_D * 2.0))))) {
break@L154c
}
val v1095_Q: Double = jsMin(95.0, (kotlin.math.abs(jsRound((v1093_X * 5.0))) + (v1085_L * 3.0)))
val v1096_W: Double = (Rt.freqDataArr[jsIdx(v1095_Q)] / 255.0)
val v1097_te: Double = (((4.0 + (v1096_W * 44.0)) + (kotlin.math.sin((((v1093_X * 0.9) + ((v1085_L - v1063_T) * 0.7)) + (v1049_l * 2.2))) * 8.0)) * v1087_U)
val v1098_le: Double = (v1088_V - v1097_te)
val v1099_ie: Any? = jsArr()
run {
var v1104_x: Double = 0.0
L155@ while ((v1104_x < 6.0)) {
val v1105_N: Double = ((((v1104_x / 6.0) * Math.PI) * 2.0) + (Math.PI / 6.0))
jsPush(v1099_ie, jsObj("x" to (v1094_q + (kotlin.math.cos(v1105_N) * v1089_D)), "y" to (v1098_le + (kotlin.math.sin(v1105_N) * v1090_K))))
v1104_x++
}
}
val v1100_J: Double = (1.0 - rrSceneSmoothstep(0.84, 0.995, v1086_G))
val v1101_de: Double = rrSceneViewportFade(v1094_q, (v1098_le + ((v1097_te + 4.0) * 0.5)), (v1089_D * 1.22), (((v1097_te + 4.0) * 0.5) + v1090_K), v1045_a, v1046_o, jsMax(44.0, (v1046_o * 0.065)))
val v1102_ee: Double = (v1100_J * v1101_de)
if ((v1102_ee <= 0.002)) {
break@L154c
}
v1044_e.save()
v1044_e.globalAlpha = v1044_e.globalAlpha * v1102_ee
v1044_e.fillStyle = jsGet(jsGet(v1053_n, "floor"), 0.0)
v1044_e.strokeStyle = jsGet(v1053_n, "secondary")
v1044_e.lineWidth = jsMax(0.7, (1.3 * v1087_U))
run {
var v1106_x: Double = 0.0
L156@ while ((v1106_x < 3.0)) {
val v1107_N: Any? = jsGet(v1099_ie, v1106_x)
val v1108_ae: Any? = jsGet(v1099_ie, (v1106_x + 1.0))
v1044_e.beginPath()
v1044_e.moveTo(num(jsGet(v1107_N, "x")), num(jsGet(v1107_N, "y")))
v1044_e.lineTo(num(jsGet(v1108_ae, "x")), num(jsGet(v1108_ae, "y")))
v1044_e.lineTo(num(jsGet(v1108_ae, "x")), ((num(jsGet(v1108_ae, "y")) + v1097_te) + 4.0))
v1044_e.lineTo(num(jsGet(v1107_N, "x")), ((num(jsGet(v1107_N, "y")) + v1097_te) + 4.0))
v1044_e.closePath()
v1044_e.fill()
v1044_e.stroke()
v1106_x++
}
}
val v1103_se: Boolean = ((v1096_W > 0.42) || ((((v1092_z + v1085_L) - v1063_T) % 4.0) == 0.0))
v1044_e.fillStyle = (if (v1103_se) ("rgba(" + str(jsGet(v1053_n, "waveRgb")) + ", " + jsToFixed((0.24 + (v1096_W * 0.45)), 3.0) + ")") else ("rgba(18, 7, 38, " + jsToFixed((0.88 * jsMin(1.0, (v1086_G * 2.4))), 3.0) + ")"))
v1044_e.strokeStyle = (if (v1103_se) jsGet(v1053_n, "accent") else jsGet(v1053_n, "primary"))
v1044_e.beginPath()
js_forEach(v1099_ie) L157@{ v1109_x: Any?, v1110_N: Double, _ ->

(if ((v1110_N == 0.0)) v1044_e.moveTo(num(jsGet(v1109_x, "x")), num(jsGet(v1109_x, "y"))) else v1044_e.lineTo(num(jsGet(v1109_x, "x")), num(jsGet(v1109_x, "y"))))
}
v1044_e.closePath()
v1044_e.fill()
v1044_e.stroke()
v1044_e.restore()
} while (false)
v1092_z++
}
}
v1085_L++
}
}
val v1064_b: Double = v1054_s
val v1065_h: Double = ((v1055_i - (v1046_o * 0.16)) + (kotlin.math.sin((v1049_l * 1.5)) * 9.0))
val v1066_p: Double = ((jsMin(v1045_a, v1046_o) * 0.145) * (1.0 + (v1051_f * 0.16)))
v1044_e.save()
v1044_e.strokeStyle = "#ffffff"
v1044_e.shadowColor = "#ffffff"
v1044_e.shadowBlur = 14.0
v1044_e.lineWidth = (3.2 + (v1051_f * 3.0))
v1044_e.beginPath()
v1044_e.moveTo((v1064_b - (v1045_a * 0.18)), 0.0)
v1044_e.lineTo(v1064_b, v1065_h)
v1044_e.stroke()
val v1067_w: Any? = jsArr("#ff1e56", "#ff7b00", "#ffee00", "#00ff88", "#00f7ff", "#595cff", "#d400ff")
js_forEach(v1067_w) L158@{ v1111_L: Any?, v1112_G: Double, _ ->

val v1113_U: Double = (((v1112_G - 3.0) * 0.11) + (kotlin.math.sin(((v1049_l * 1.4) + v1112_G)) * 0.025))
val v1114_V: Double = (v1064_b + (v1045_a * (0.28 + (v1112_G * 0.045))))
val v1115_D: Double = ((v1055_i + ((v1112_G - 2.0) * (v1046_o * 0.055))) + ((Rt.freqDataArr[jsIdx((v1112_G * 8.0))] / 255.0) * 24.0))
v1044_e.strokeStyle = v1111_L
v1044_e.shadowColor = v1111_L
v1044_e.lineWidth = (2.2 + (v1052_M * 2.2))
v1044_e.beginPath()
v1044_e.moveTo(v1064_b, v1065_h)
v1044_e.lineTo(v1114_V, v1115_D)
v1044_e.stroke()
null
}
v1044_e.restore()
val v1068_A: Double = (v1049_l * 0.78)
val v1069_C: Double = (0.42 + (kotlin.math.sin((v1049_l * 0.6)) * 0.22))
val v1070_E: Double = kotlin.math.cos(v1068_A)
val v1071_k: Double = kotlin.math.sin(v1068_A)
val v1072_R: Double = kotlin.math.cos(v1069_C)
val v1073_F: Double = kotlin.math.sin(v1069_C)
val v1074_O: Any? = jsArr(jsArr(0.0, -1.25, 0.0), jsArr(-1.08, 0.62, -0.68), jsArr(1.08, 0.62, -0.68), jsArr(0.0, 0.62, 1.15))
val v1075_P: Any? = js_map(v1074_O) L159@{ p160: Any?, _, _ ->
var v1116_L: Any? = jsGet(p160, 0.0)
var v1117_G: Any? = jsGet(p160, 1.0)
var v1118_U: Any? = jsGet(p160, 2.0)
jsArr(v1116_L, -(num(v1117_G)), -(num(v1118_U)))
}
val v1076_I: Any? = jsArr(jsArr(0.0, 1.0, 2.0), jsArr(0.0, 2.0, 3.0), jsArr(0.0, 3.0, 1.0), jsArr(1.0, 3.0, 2.0))
fun v1077_U_(p161: Any?, v1122_V: Double): Any? {
var v1119_L: Any? = jsGet(p161, 0.0)
var v1120_G: Any? = jsGet(p161, 1.0)
var v1121_U: Any? = jsGet(p161, 2.0)
val v1123_D: Double = (v1068_A * v1122_V)
val v1124_K: Double = kotlin.math.cos(v1123_D)
val v1125_S_: Double = kotlin.math.sin(v1123_D)
val v1126_z: Double = ((num(v1119_L) * v1124_K) - (num(v1121_U) * v1125_S_))
val v1127_X: Double = ((num(v1119_L) * v1125_S_) + (num(v1121_U) * v1124_K))
val v1128_q: Double = ((num(v1120_G) * v1072_R) - (v1127_X * v1073_F))
val v1129_Q: Double = ((num(v1120_G) * v1073_F) + (v1127_X * v1072_R))
val v1130_W: Double = (3.4 / (3.4 + (v1129_Q * 0.48)))
return jsObj("x" to (v1064_b + ((v1126_z * v1066_p) * v1130_W)), "y" to (v1065_h + ((v1128_q * v1066_p) * v1130_W)), "z" to v1129_Q)
return null
}
val v1078_B: Any? = jsArr()
js_forEach(jsArr(jsObj("verts" to js_map(v1074_O) L162@{ v1131_L: Any?, _, _ ->

v1077_U_(v1131_L, 1.0)
}, "tint" to jsGet(v1053_n, "waveRgb"), "stroke" to jsGet(v1053_n, "primary")), jsObj("verts" to js_map(v1075_P) L163@{ v1132_L: Any?, _, _ ->

v1077_U_(v1132_L, -0.85)
}, "tint" to jsGet(v1053_n, "altWaveRgb"), "stroke" to jsGet(v1053_n, "accent")))) L164@{ v1133_L: Any?, _, _ ->

js_forEach(v1076_I) L165@{ p166: Any?, _, _ ->
var v1134_G: Any? = jsGet(p166, 0.0)
var v1135_U: Any? = jsGet(p166, 1.0)
var v1136_V: Any? = jsGet(p166, 2.0)
val v1137_D: Any? = jsGet(jsGet(v1133_L, "verts"), v1134_G)
val v1138_K: Any? = jsGet(jsGet(v1133_L, "verts"), v1135_U)
val v1139_S_: Any? = jsGet(jsGet(v1133_L, "verts"), v1136_V)
jsPush(v1078_B, jsObj("pts" to jsArr(v1137_D, v1138_K, v1139_S_), "z" to (((num(jsGet(v1137_D, "z")) + num(jsGet(v1138_K, "z"))) + num(jsGet(v1139_S_, "z"))) / 3.0), "tint" to jsGet(v1133_L, "tint"), "stroke" to jsGet(v1133_L, "stroke")))
null
}
null
}
js_sort(v1078_B) L167@{ v1140_L: Any?, v1141_G: Any? ->

(num(jsGet(v1141_G, "z")) - num(jsGet(v1140_L, "z")))
}
js_forEach(v1078_B) L168@{ v1142_L: Any?, _, _ ->

val v1143_G: Double = jsMax(0.14, jsMin(0.55, (0.34 - (num(jsGet(v1142_L, "z")) * 0.16))))
v1044_e.fillStyle = ("rgba(" + str(jsGet(v1142_L, "tint")) + ", " + jsToFixed(v1143_G, 3.0) + ")")
v1044_e.strokeStyle = jsGet(v1142_L, "stroke")
v1044_e.lineWidth = 1.9
v1044_e.beginPath()
v1044_e.moveTo(num(jsGet(jsGet(jsGet(v1142_L, "pts"), 0.0), "x")), num(jsGet(jsGet(jsGet(v1142_L, "pts"), 0.0), "y")))
v1044_e.lineTo(num(jsGet(jsGet(jsGet(v1142_L, "pts"), 1.0), "x")), num(jsGet(jsGet(jsGet(v1142_L, "pts"), 1.0), "y")))
v1044_e.lineTo(num(jsGet(jsGet(jsGet(v1142_L, "pts"), 2.0), "x")), num(jsGet(jsGet(jsGet(v1142_L, "pts"), 2.0), "y")))
v1044_e.closePath()
v1044_e.fill()
v1044_e.stroke()
null
}
}

fun renderScene26_CelestialAtrium(v1144_e: Ctx, v1145_a: Double, v1146_o: Double, v1147_r: Double, v1148_t: Double, v1149_l: Double, v1150_u: Double, v1151_f: Double, v1152_M: Double, v1153_n: Any?): Unit {

val v1154_s: Double = (v1147_r - (Rt.mouseX * 22.0))
val v1155_i: Double = (v1148_t + (Rt.mouseY * 12.0))
val v1156_g: Grad = v1144_e.createLinearGradient(0.0, (v1155_i - 20.0), 0.0, v1146_o)
v1156_g.addColorStop(0.0, jsGet(v1153_n, "sunSlice"))
v1156_g.addColorStop(0.45, jsGet(jsGet(v1153_n, "floor"), 1.0))
v1156_g.addColorStop(1.0, jsGet(jsGet(v1153_n, "floor"), 0.0))
v1144_e.fillStyle = v1156_g
v1144_e.fillRect(0.0, v1155_i, v1145_a, (v1146_o - v1155_i))
val v1157_m: Double = ((v1150_u * 0.48) % 1.0)
val v1158_d: Double = 14.0
run {
var v1164_b: Double = 1.0
L169@ while ((v1164_b <= v1158_d)) {
val v1165_h: Double = ((v1164_b + v1157_m) / v1158_d)
val v1166_p: Double = Math.pow(v1165_h, 1.8)
val v1167_w: Double = (v1155_i + (v1166_p * (v1146_o - v1155_i)))
val v1168_A: Double = (v1155_i - (v1166_p * (v1155_i * 0.92)))
val v1169_C: Double = (18.0 + (v1166_p * 82.0))
v1144_e.strokeStyle = (if (((v1164_b % 2.0) == 0.0)) jsGet(v1153_n, "primary") else ("rgba(" + str(jsGet(v1153_n, "waveRgb")) + ", 0.42)"))
v1144_e.lineWidth = (0.9 + (v1166_p * 1.5))
v1144_e.beginPath()
v1144_e.moveTo(0.0, (v1167_w - v1169_C))
v1144_e.quadraticCurveTo(v1154_s, (v1167_w + (v1169_C * 0.45)), v1145_a, (v1167_w - v1169_C))
v1144_e.stroke()
v1144_e.strokeStyle = (if (((v1164_b % 2.0) == 0.0)) jsGet(v1153_n, "secondary") else ("rgba(" + str(jsGet(v1153_n, "altWaveRgb")) + ", 0.35)"))
v1144_e.beginPath()
v1144_e.moveTo(0.0, (v1168_A + (v1169_C * 0.8)))
v1144_e.quadraticCurveTo(v1154_s, (v1168_A - (v1169_C * 0.55)), v1145_a, (v1168_A + (v1169_C * 0.8)))
v1144_e.stroke()
v1164_b++
}
}
run {
var v1170_b: Double = -10.0
L170@ while ((v1170_b <= 10.0)) {
val v1171_h: Double = (v1154_s + (v1170_b * (v1145_a * 0.095)))
val v1172_p: Double = (v1154_s + (v1170_b * (v1145_a * 0.015)))
val v1173_w: Double = (v1154_s + (v1170_b * (v1145_a * 0.125)))
v1144_e.strokeStyle = (if (((v1170_b % 2.0) == 0.0)) ("rgba(" + str(jsGet(v1153_n, "waveRgb")) + ", 0.42)") else ("rgba(" + str(jsGet(v1153_n, "altWaveRgb")) + ", 0.25)"))
v1144_e.lineWidth = (if ((v1170_b == 0.0)) 1.8 else 1.0)
v1144_e.beginPath()
v1144_e.moveTo(v1171_h, 0.0)
v1144_e.quadraticCurveTo((v1154_s + (v1170_b * (v1145_a * 0.045))), v1155_i, v1173_w, v1146_o)
v1144_e.stroke()
v1170_b++
}
}
drawPerspectiveCorridor(6.0, 0.36, v1150_u, JsFn L171@{ args ->
var v1174_b: Any? = args.getOrNull(0)
var v1175_h: Any? = args.getOrNull(1)
var v1176_p: Any? = args.getOrNull(2)
var v1177_w: Any? = args.getOrNull(3)
val v1178_A: Double = (v1145_a * (0.14 + (num(v1175_h) * 0.42)))
val v1179_C: Double = (v1155_i + (num(v1175_h) * (v1146_o - v1155_i)))
val v1180_E: Double = (v1155_i - (num(v1175_h) * (v1155_i * 0.86)))
val v1181_k: Double = (6.0 + (num(v1175_h) * 22.0))
val v1182_R: Double = rrSceneViewportFade(v1154_s, ((v1180_E + v1179_C) * 0.5), (v1178_A + (v1181_k * 0.75)), ((v1179_C - v1180_E) * 0.5), v1145_a, v1146_o, jsMax(48.0, (jsMin(v1145_a, v1146_o) * 0.07)))
val v1183_F: Double = (num(v1176_p) * v1182_R)
if ((v1183_F <= 0.002)) {
return@L171 null
}
v1144_e.save()
v1144_e.globalAlpha = (v1183_F * 0.9)
js_forEach(jsArr(-1.0, 1.0)) L172@{ v1184_O: Any?, _, _ ->

val v1185_P: Double = (v1154_s + (num(v1184_O) * v1178_A))
v1144_e.fillStyle = jsGet(v1153_n, "sunSlice")
v1144_e.strokeStyle = (if (((num(v1177_w) % 2.0) == 0.0)) jsGet(v1153_n, "primary") else jsGet(v1153_n, "accent"))
v1144_e.lineWidth = (1.4 + (num(v1175_h) * 1.6))
v1144_e.fillRect((v1185_P - (v1181_k * 0.5)), v1180_E, v1181_k, (v1179_C - v1180_E))
v1144_e.strokeRect((v1185_P - (v1181_k * 0.5)), v1180_E, v1181_k, (v1179_C - v1180_E))
val v1186_I: Double = (v1185_P - (num(v1184_O) * (24.0 * num(v1175_h))))
val v1187_U_: Double = ((v1180_E + ((v1179_C - v1180_E) * 0.26)) + ((kotlin.math.sin(((v1149_l * 2.0) + num(v1177_w))) * 6.0) * num(v1175_h)))
v1144_e.beginPath()
v1144_e.moveTo(v1185_P, (v1180_E + (12.0 * num(v1175_h))))
v1144_e.lineTo(v1186_I, v1187_U_)
v1144_e.stroke()
v1144_e.beginPath()
v1144_e.arc(v1186_I, v1187_U_, (4.0 + (num(v1175_h) * 10.0)), 0.0, (Math.PI * 2.0))
v1144_e.stroke()
null
}
v1144_e.restore()
null
})
val v1159_y: Double = v1154_s
val v1160_c: Double = (v1155_i - (v1146_o * 0.05))
val v1161_S: Double = ((jsMin(v1145_a, v1146_o) * 0.23) * (1.0 + (v1151_f * 0.08)))
val v1162_v: Grad = v1144_e.createRadialGradient(v1159_y, v1160_c, 2.0, v1159_y, v1160_c, (v1161_S * 0.42))
v1162_v.addColorStop(0.0, "#ffffff")
v1162_v.addColorStop(0.35, jsGet(v1153_n, "accent"))
v1162_v.addColorStop(0.7, ("rgba(" + str(jsGet(v1153_n, "altWaveRgb")) + ", 0.38)"))
v1162_v.addColorStop(1.0, "transparent")
v1144_e.fillStyle = v1162_v
v1144_e.beginPath()
v1144_e.arc(v1159_y, v1160_c, (v1161_S * 0.42), 0.0, (Math.PI * 2.0))
v1144_e.fill()
val v1163_T: Any? = jsArr(jsObj("rFrac" to 1.0, "speed" to 0.42, "tiltX" to 0.38, "tiltY" to 0.25, "col" to jsGet(v1153_n, "primary"), "planetSize" to 9.5), jsObj("rFrac" to 0.82, "speed" to -0.58, "tiltX" to 0.92, "tiltY" to -0.48, "col" to jsGet(v1153_n, "accent"), "planetSize" to 7.5), jsObj("rFrac" to 0.66, "speed" to 0.76, "tiltX" to -0.64, "tiltY" to 0.72, "col" to jsGet(v1153_n, "secondary"), "planetSize" to 6.5), jsObj("rFrac" to 0.5, "speed" to -0.95, "tiltX" to 1.18, "tiltY" to 0.32, "col" to jsGet(v1153_n, "primary"), "planetSize" to 5.2), jsObj("rFrac" to 0.34, "speed" to 1.35, "tiltX" to 0.45, "tiltY" to -1.05, "col" to "#ffffff", "planetSize" to 4.0))
js_forEach(v1163_T) L173@{ v1188_b: Any?, v1189_h: Double, _ ->

val v1190_p: Double = ((v1161_S * num(jsGet(v1188_b, "rFrac"))) * (1.0 + ((Rt.freqDataArr[jsIdx((v1189_h * 10.0))] / 255.0) * 0.08)))
val v1191_w: Double = (num(jsGet(v1188_b, "tiltX")) + (kotlin.math.sin(((v1149_l * 0.45) + v1189_h)) * 0.28))
val v1192_A: Double = (num(jsGet(v1188_b, "tiltY")) + ((v1149_l * num(jsGet(v1188_b, "speed"))) * 0.55))
val v1193_C: Double = kotlin.math.cos(v1191_w)
val v1194_E: Double = kotlin.math.sin(v1191_w)
val v1195_k: Double = kotlin.math.cos(v1192_A)
val v1196_R: Double = kotlin.math.sin(v1192_A)
val v1197_F: Double = 48.0
v1144_e.strokeStyle = jsGet(v1188_b, "col")
v1144_e.lineWidth = (if ((v1189_h == 0.0)) 2.4 else 1.65)
v1144_e.beginPath()
run {
var v1209_S_: Double = 0.0
L174@ while ((v1209_S_ <= v1197_F)) {
val v1210_z: Double = (((v1209_S_ / v1197_F) * Math.PI) * 2.0)
val v1211_X: Double = (kotlin.math.cos(v1210_z) * v1190_p)
val v1212_q: Double = (kotlin.math.sin(v1210_z) * v1190_p)
val v1213_Q: Double = (-v1212_q * v1194_E)
val v1214_W: Double = (v1212_q * v1193_C)
val v1215_te: Double = ((v1211_X * v1195_k) - (v1214_W * v1196_R))
val v1216_le: Double = ((v1211_X * v1196_R) + (v1214_W * v1195_k))
val v1217_ie: Double = (3.6 / (3.6 + ((v1216_le / v1161_S) * 0.55)))
val v1218_J: Double = (v1159_y + (v1215_te * v1217_ie))
val v1219_de: Double = (v1160_c + (v1213_Q * v1217_ie))
if ((v1209_S_ == 0.0)) {
v1144_e.moveTo(v1218_J, v1219_de)
} else {
v1144_e.lineTo(v1218_J, v1219_de)
}
v1209_S_++
}
}
v1144_e.stroke()
val v1198_O: Double = (((v1149_l * num(jsGet(v1188_b, "speed"))) * 1.6) + (v1189_h * 1.4))
val v1199_P: Double = (kotlin.math.cos(v1198_O) * v1190_p)
val v1200_I: Double = (kotlin.math.sin(v1198_O) * v1190_p)
val v1201_U_: Double = (-v1200_I * v1194_E)
val v1202_B: Double = (v1200_I * v1193_C)
val v1203_L: Double = ((v1199_P * v1195_k) - (v1202_B * v1196_R))
val v1204_G: Double = ((v1199_P * v1196_R) + (v1202_B * v1195_k))
val v1205_U: Double = (3.6 / (3.6 + ((v1204_G / v1161_S) * 0.55)))
val v1206_V: Double = (v1159_y + (v1203_L * v1205_U))
val v1207_D: Double = (v1160_c + (v1201_U_ * v1205_U))
val v1208_K: Double = ((num(jsGet(v1188_b, "planetSize")) * v1205_U) * (1.0 + (v1152_M * 0.22)))
v1144_e.fillStyle = jsGet(v1188_b, "col")
v1144_e.strokeStyle = "#ffffff"
v1144_e.lineWidth = 1.2
v1144_e.beginPath()
v1144_e.arc(v1206_V, v1207_D, v1208_K, 0.0, (Math.PI * 2.0))
v1144_e.fill()
v1144_e.stroke()
null
}
}

fun renderScene27_TidalVault(v1220_e: Ctx, v1221_a: Double, v1222_o: Double, v1223_r: Double, v1224_t: Double, v1225_l: Double, v1226_u: Double, v1227_f: Double, v1228_M: Double, v1229_n: Any?): Unit {

var v1230_s: Double = 0.0
var v1231_i: Double = 0.0
var v1232_g: Double = 0.0
var v1233_m: Double = 0.0
var v1234_d: Double = 0.0
var v1235_y: Double = 0.0
var v1236_c: Double = 0.0
var v1237_S: Grad? = null
var v1238_v: Double = 0.0
var v1239_T: Double = 0.0
var v1240_b: Double = 0.0
var v1241_h: Double = 0.0
var v1242_p: Double = 0.0
var v1243_w: Double = 0.0
var v1244_A: Double = 0.0
var v1245_C: Grad? = null
var v1246_E: Double = 0.0
var v1247_k: Double = 0.0
var v1248_R: Double = 0.0
var v1249_F: Double = 0.0
var v1250_O: Double = 0.0
var v1251_P: Double = 0.0
var v1252_I: Double = 0.0
var v1253_U_: Grad? = null
var v1254_B: Double = 0.0
var v1255_L: Double = 0.0
var v1256_G: Double = 0.0
var v1257_U: Double = 0.0
var v1258_V: Double = 0.0
var v1259_D: Double = 0.0
var v1260_K: Double = 0.0
var v1261_S_: Double = 0.0
var v1262_z: Double = 0.0
var v1263_X: Double = 0.0
var v1264_q: Double = 0.0
var v1265_Q: Double = 0.0
var v1266_W: Double = 0.0
var v1267_te: Double = 0.0
var v1268_le: Double = 0.0
var v1269_ie: Double = 0.0
var v1270_J: Double = 0.0
var v1271_de: Double = 0.0
var v1272_ee: Double = 0.0
var v1273_se: Grad? = null
var v1274_x: Double = 0.0
var v1275_N: Double = 0.0
var v1276_ae: Double = 0.0
var v1277_j: Double = 0.0
var v1278_oe: Double = 0.0
var v1279_Y: Double = 0.0
var v1280_ne: Double = 0.0
var v1281_he: Double = 0.0
var v1282_ve: Grad? = null
var v1283_Ce: Double = 0.0
var v1284_ye: Double = 0.0
var v1285_Z: Double = 0.0
var v1286_H: Double = 0.0
var v1287_fe: Double = 0.0
var v1288_we: Double = 0.0
var v1289_Se: Double = 0.0
var v1290_ke: Double = 0.0
var v1291_Ee: Double = 0.0
var v1292_Pe: Double = 0.0
var v1293_Ie: Double = 0.0
var v1294_Te: Double = 0.0
var v1295_ue: Grad? = null
var v1296_me: Double = 0.0
var v1297_re: Double = 0.0
var v1298_ge: Double = 0.0
var v1299_pe: Double = 0.0
var v1300_Me: Double = 0.0
var v1301_ce: Double = 0.0
var v1302_be: Double = 0.0
var v1303_Ae: Double = 0.0
var v1304_Be: Double = 0.0
var v1305_Re: Double = 0.0
var v1306_Le: Boolean = false
var v1307__e: Double = 0.0
var v1308_Fe: Double = 0.0
var v1309_Oe: Double = 0.0
var v1310_Ve: Double = 0.0
var v1311_Ge: Double = 0.0
var v1312_qe: Double = 0.0
var v1313_Ke: Double = 0.0
var v1314_S_e: Double = 0.0
v1230_s = jsMax(0.0, jsMin(2.0, jsOrN(num(Rt.win("__rrVisualQualityStage"))) { 0.0 }))
v1231_i = (if ((v1230_s == 0.0)) 1.0 else (if ((v1230_s == 1.0)) 0.75 else 0.5))
v1232_g = jsMin(v1221_a, v1222_o)
v1233_m = (Math.PI * 2.0)
v1234_d = (if (("number" == "number")) Rt.mouseX else 0.0)
v1235_y = (v1223_r - (v1234_d * 10.0))
v1236_c = v1224_t
v1237_S = v1220_e.createLinearGradient(0.0, 0.0, 0.0, v1222_o)
v1237_S!!.addColorStop(0.0, "#1c7894")
v1237_S!!.addColorStop(0.35, "#0d4566")
v1237_S!!.addColorStop(0.62, "#082a4a")
v1237_S!!.addColorStop(1.0, "#030b1c")
v1220_e.fillStyle = v1237_S!!
v1220_e.fillRect(0.0, 0.0, v1221_a, v1222_o)
v1220_e.fillStyle = (("rgba(" + str(jsGet(v1229_n, "altWaveRgb"))) + ",0.10)")
v1220_e.fillRect(0.0, 0.0, v1221_a, v1222_o)
v1220_e.save()
v1220_e.globalCompositeOperation = "lighter"
v1220_e.strokeStyle = (("rgba(190,250,255," + jsToFixed((0.2 + (v1228_M * 0.35)), 3.0)) + ")")
v1220_e.lineWidth = jsMax(1.0, (v1232_g / 500.0))
run {
v1238_v = 0.0
L175@ while ((v1238_v < 3.0)) {
v1220_e.beginPath()
run {
v1239_T = 0.0
L176@ while ((v1239_T <= v1221_a)) {
v1240_b = (((v1222_o * (0.03 + (v1238_v * 0.035))) + ((kotlin.math.sin(((((v1239_T / v1221_a) * 11.0) + (v1225_l * (0.5 + (v1238_v * 0.2)))) + (v1238_v * 2.0))) * v1222_o) * 0.012)) + ((kotlin.math.sin((((v1239_T / v1221_a) * 23.0) - (v1225_l * 0.4))) * v1222_o) * 0.006))
if (truthy(v1239_T)) {
v1220_e.lineTo(v1239_T, v1240_b)
} else {
v1220_e.moveTo(v1239_T, v1240_b)
}
v1239_T += (v1221_a / 36.0)
}
}
v1220_e.stroke()
v1238_v++
}
}
run {
v1241_h = 0.0
L177@ while ((v1241_h < 6.0)) {
v1242_p = ((v1221_a * (0.12 + (v1241_h * 0.155))) + ((kotlin.math.sin(((v1225_l * 0.15) + v1241_h)) * v1221_a) * 0.02))
v1243_w = (v1221_a * (0.03 + ((v1241_h % 3.0) * 0.012)))
v1244_A = ((0.07 + (0.04 * kotlin.math.sin(((v1225_l * 0.3) + (v1241_h * 1.7))))) + (v1228_M * 0.1))
v1245_C = v1220_e.createLinearGradient(0.0, 0.0, 0.0, (v1222_o * 0.85))
v1245_C!!.addColorStop(0.0, (("rgba(200,250,255," + jsToFixed(v1244_A, 3.0)) + ")"))
v1245_C!!.addColorStop(1.0, "rgba(200,250,255,0)")
v1220_e.fillStyle = v1245_C!!
v1220_e.beginPath()
v1220_e.moveTo((v1242_p - (v1243_w * 0.4)), 0.0)
v1220_e.lineTo((v1242_p + (v1243_w * 0.4)), 0.0)
v1220_e.lineTo(((v1242_p + (v1243_w * 1.6)) + ((v1242_p - v1223_r) * 0.25)), (v1222_o * 0.85))
v1220_e.lineTo(((v1242_p - (v1243_w * 1.2)) + ((v1242_p - v1223_r) * 0.25)), (v1222_o * 0.85))
v1220_e.closePath()
v1220_e.fill()
v1241_h++
}
}
v1220_e.restore()
v1220_e.save()
v1220_e.fillStyle = "#06233c"
v1220_e.globalAlpha = 0.85
run {
v1246_E = 0.0
L178@ while ((v1246_E < 12.0)) {
v1247_k = (if (truthy((v1246_E % 2.0))) 1.0 else -1.0)
v1248_R = kotlin.math.floor((v1246_E / 2.0))
v1249_F = (v1235_y + ((v1247_k * v1221_a) * (0.2 + (v1248_R * 0.07))))
v1250_O = (v1222_o * (0.1 + (((v1248_R * 3.0) % 5.0) * 0.025)))
v1251_P = (v1221_a * (0.025 + ((v1248_R % 2.0) * 0.012)))
v1220_e.fillRect((v1249_F - (v1251_P / 2.0)), (v1236_c - v1250_O), v1251_P, v1250_O)
v1220_e.beginPath()
v1220_e.arc(v1249_F, (v1236_c - v1250_O), (v1251_P * 0.5), Math.PI, v1233_m)
v1220_e.fill()
v1246_E++
}
}
v1220_e.beginPath()
v1220_e.moveTo(0.0, v1236_c)
run {
v1252_I = 0.0
L179@ while ((v1252_I <= 10.0)) {
v1220_e.lineTo(((v1252_I * v1221_a) / 10.0), (v1236_c - (v1222_o * (0.02 + (((v1252_I * 7.0) % 5.0) * 0.008)))))
v1252_I++
}
}
v1220_e.lineTo(v1221_a, v1236_c)
v1220_e.closePath()
v1220_e.fill()
v1220_e.restore()
v1253_U_ = v1220_e.createLinearGradient(0.0, v1236_c, 0.0, v1222_o)
v1253_U_!!.addColorStop(0.0, "#0b3550")
v1253_U_!!.addColorStop(1.0, "#04101f")
v1220_e.fillStyle = v1253_U_!!
v1220_e.fillRect(0.0, v1236_c, v1221_a, (v1222_o - v1236_c))
v1220_e.strokeStyle = "rgba(120,220,240,0.16)"
v1220_e.lineWidth = 1.0
v1220_e.beginPath()
v1254_B = ((v1226_u * 0.18) % 1.0)
run {
v1255_L = 0.0
L180@ while ((v1255_L < 10.0)) {
v1256_G = ((v1255_L + v1254_B) / 10.0)
v1257_U = (v1236_c + (Math.pow(v1256_G, 1.7) * (v1222_o - v1236_c)))
run {
v1258_V = 0.0
L181@ while ((v1258_V <= v1221_a)) {
v1259_D = (v1257_U + (kotlin.math.sin((((v1258_V / v1221_a) * 9.0) + (v1255_L * 1.3))) * (2.0 + (v1256_G * 6.0))))
if (truthy(v1258_V)) {
v1220_e.lineTo(v1258_V, v1259_D)
} else {
v1220_e.moveTo(v1258_V, v1259_D)
}
v1258_V += (v1221_a / 24.0)
}
}
v1255_L++
}
}
v1220_e.stroke()
v1220_e.fillStyle = (("rgba(" + str(jsGet(v1229_n, "waveRgb"))) + ",0.16)")
v1220_e.beginPath()
v1220_e.moveTo((v1235_y - (v1221_a * 0.03)), v1236_c)
v1220_e.lineTo((v1235_y + (v1221_a * 0.03)), v1236_c)
v1220_e.lineTo((v1235_y + (v1221_a * 0.26)), v1222_o)
v1220_e.lineTo((v1235_y - (v1221_a * 0.26)), v1222_o)
v1220_e.closePath()
v1220_e.fill()
v1220_e.strokeStyle = (("rgba(" + str(jsGet(v1229_n, "waveRgb"))) + ",0.45)")
v1220_e.lineWidth = 1.2
v1220_e.beginPath()
v1220_e.moveTo((v1235_y - (v1221_a * 0.03)), v1236_c)
v1220_e.lineTo((v1235_y - (v1221_a * 0.26)), v1222_o)
v1220_e.moveTo((v1235_y + (v1221_a * 0.03)), v1236_c)
v1220_e.lineTo((v1235_y + (v1221_a * 0.26)), v1222_o)
run {
v1260_K = 0.0
L182@ while ((v1260_K < 9.0)) {
v1261_S_ = ((v1260_K + v1254_B) / 9.0)
v1262_z = (v1236_c + (Math.pow(v1261_S_, 1.7) * (v1222_o - v1236_c)))
v1263_X = (v1221_a * (0.03 + (0.23 * Math.pow(v1261_S_, 1.7))))
v1220_e.moveTo((v1235_y - v1263_X), v1262_z)
v1220_e.lineTo((v1235_y + v1263_X), v1262_z)
v1260_K++
}
}
v1220_e.stroke()
if (truthy(jsAnd(jsAnd(!("object" == "undefined")) { Rt.freqDataArr }) { Rt.freqDataArr.size.toDouble() })) {
run {
v1264_q = 0.0
L183@ while ((v1264_q < 12.0)) {
v1265_Q = ((v1264_q + 0.5) / 12.0)
v1266_W = (v1236_c + (Math.pow(v1265_Q, 1.7) * (v1222_o - v1236_c)))
v1267_te = (v1221_a * (0.03 + (0.23 * Math.pow(v1265_Q, 1.7))))
v1268_le = (jsOrN(Rt.freqDataArr[jsIdx(kotlin.math.floor((2.0 + (v1264_q * 3.0))))]) { 0.0 } / 255.0)
v1269_ie = (1.5 + (Math.pow(v1265_Q, 1.7) * 7.0))
v1220_e.fillStyle = (((("rgba(" + str(jsGet(v1229_n, "waveRgb"))) + ",") + jsToFixed((0.15 + (v1268_le * 0.8)), 3.0)) + ")")
v1220_e.fillRect(((v1235_y - v1267_te) - v1269_ie), (v1266_W - (v1269_ie * (0.6 + (v1268_le * 2.5)))), (v1269_ie * 0.8), (v1269_ie * (0.6 + (v1268_le * 2.5))))
v1220_e.fillRect(((v1235_y + v1267_te) + (v1269_ie * 0.2)), (v1266_W - (v1269_ie * (0.6 + (v1268_le * 2.5)))), (v1269_ie * 0.8), (v1269_ie * (0.6 + (v1268_le * 2.5))))
v1264_q++
}
}
}
v1270_J = (v1232_g * 0.2)
v1271_de = v1235_y
v1272_ee = (v1236_c - (v1270_J * 0.62))
v1220_e.save()
v1220_e.translate(v1271_de, v1272_ee)
v1273_se = v1220_e.createRadialGradient(0.0, 0.0, (v1270_J * 0.6), 0.0, 0.0, (v1270_J * 1.12))
v1273_se!!.addColorStop(0.0, "#0f3c55")
v1273_se!!.addColorStop(0.7, "#2f7f99")
v1273_se!!.addColorStop(1.0, "#0a2234")
v1220_e.fillStyle = v1273_se!!
v1220_e.beginPath()
v1220_e.arc(0.0, 0.0, (v1270_J * 1.12), 0.0, v1233_m)
v1220_e.fill()
v1220_e.strokeStyle = jsGet(v1229_n, "primary")
v1220_e.globalAlpha = (0.6 + (v1227_f * 0.4))
v1220_e.lineWidth = (jsMax(1.5, (v1232_g / 300.0)) * (1.0 + (v1227_f * 2.2)))
v1220_e.stroke()
v1220_e.globalAlpha = 1.0
v1220_e.fillStyle = "#9fe7f2"
run {
v1274_x = 0.0
L184@ while ((v1274_x < 16.0)) {
v1275_N = ((v1274_x / 16.0) * v1233_m)
v1220_e.beginPath()
v1220_e.arc(((kotlin.math.cos(v1275_N) * v1270_J) * 1.03), ((kotlin.math.sin(v1275_N) * v1270_J) * 1.03), jsMax(1.5, (v1270_J * 0.03)), 0.0, v1233_m)
v1220_e.fill()
v1274_x++
}
}
v1220_e.fillStyle = "#082234"
v1220_e.beginPath()
v1220_e.arc(0.0, 0.0, (v1270_J * 0.92), 0.0, v1233_m)
v1220_e.fill()
v1220_e.save()
v1220_e.rotate(((v1225_l * 0.07) + (v1226_u * 0.05)))
v1220_e.strokeStyle = jsGet(v1229_n, "secondary")
v1220_e.globalAlpha = 0.75
v1220_e.lineWidth = 1.0
v1220_e.beginPath()
run {
v1276_ae = 0.0
L185@ while ((v1276_ae < 48.0)) {
v1277_j = ((v1276_ae / 48.0) * v1233_m)
v1278_oe = (v1270_J * (if (truthy((v1276_ae % 4.0))) 0.82 else 0.76))
v1220_e.moveTo((kotlin.math.cos(v1277_j) * v1278_oe), (kotlin.math.sin(v1277_j) * v1278_oe))
v1220_e.lineTo(((kotlin.math.cos(v1277_j) * v1270_J) * 0.88), ((kotlin.math.sin(v1277_j) * v1270_J) * 0.88))
v1276_ae++
}
}
v1220_e.stroke()
v1220_e.restore()
v1220_e.save()
v1220_e.rotate((-v1225_l * 0.11))
v1220_e.strokeStyle = "#bff6ff"
v1220_e.globalAlpha = 0.8
v1220_e.lineWidth = jsMax(2.0, (v1270_J * 0.05))
v1220_e.lineCap = "round"
v1220_e.beginPath()
run {
v1279_Y = 0.0
L186@ while ((v1279_Y < 6.0)) {
v1280_ne = ((v1279_Y / 6.0) * v1233_m)
v1220_e.moveTo(((kotlin.math.cos(v1280_ne) * v1270_J) * 0.16), ((kotlin.math.sin(v1280_ne) * v1270_J) * 0.16))
v1220_e.lineTo(((kotlin.math.cos(v1280_ne) * v1270_J) * 0.6), ((kotlin.math.sin(v1280_ne) * v1270_J) * 0.6))
v1279_Y++
}
}
v1220_e.stroke()
v1220_e.beginPath()
v1220_e.arc(0.0, 0.0, (v1270_J * 0.6), 0.0, v1233_m)
v1220_e.lineWidth = jsMax(1.5, (v1270_J * 0.03))
v1220_e.stroke()
v1220_e.restore()
v1281_he = (v1270_J * (0.3 + (v1227_f * 0.22)))
v1282_ve = v1220_e.createRadialGradient(0.0, 0.0, 0.0, 0.0, 0.0, v1281_he)
v1282_ve!!.addColorStop(0.0, (("rgba(255,255,255," + jsToFixed((0.85 + (v1227_f * 0.1)), 3.0)) + ")"))
v1282_ve!!.addColorStop(0.35, (("rgba(" + str(jsGet(v1229_n, "waveRgb"))) + ",0.75)"))
v1282_ve!!.addColorStop(1.0, (("rgba(" + str(jsGet(v1229_n, "waveRgb"))) + ",0)"))
v1220_e.globalAlpha = 1.0
v1220_e.fillStyle = v1282_ve!!
v1220_e.beginPath()
v1220_e.arc(0.0, 0.0, v1281_he, 0.0, v1233_m)
v1220_e.fill()
v1220_e.strokeStyle = (((("rgba(" + str(jsGet(v1229_n, "waveRgb"))) + ",") + jsToFixed((0.12 + (v1227_f * 0.5)), 3.0)) + ")")
v1220_e.lineWidth = 2.0
v1220_e.beginPath()
v1220_e.arc(0.0, 0.0, (v1270_J * (1.18 + (((v1225_l * 0.35) % 1.0) * 0.6))), 0.0, v1233_m)
v1220_e.stroke()
v1220_e.restore()
drawPerspectiveCorridor(jsRound((6.0 * (0.6 + (v1231_i * 0.4)))), 0.12, v1226_u, JsFn L187@{ args ->
var v1319_Ue: Double = 0.0
var v1320_De: Double = 0.0
var v1321_Ne: Double = 0.0
var v1315_je: Any? = args.getOrNull(0)
var v1316_We: Any? = args.getOrNull(1)
var v1317_Qe: Any? = args.getOrNull(2)
var v1318_Ze: Any? = args.getOrNull(3)
v1319_Ue = (v1236_c + (num(v1316_We) * (v1222_o - v1236_c)))
v1320_De = ((v1222_o * 0.06) + ((num(v1316_We) * v1222_o) * 0.62))
v1321_Ne = (4.0 + ((num(v1316_We) * v1221_a) * 0.045))
js_forEach(jsArr(-1.0, 1.0)) L188@{ v1322_Xe: Any?, _, _ ->
var v1323_Je: Double = 0.0

v1323_Je = (v1235_y + (num(v1322_Xe) * ((v1221_a * 0.05) + ((num(v1316_We) * v1221_a) * 0.42))))
v1220_e.save()
v1220_e.globalAlpha = (num(v1317_Qe) * 0.95)
v1220_e.fillStyle = "#0c3047"
v1220_e.fillRect((v1323_Je - (v1321_Ne / 2.0)), (v1319_Ue - v1320_De), v1321_Ne, v1320_De)
v1220_e.fillStyle = "#16506b"
v1220_e.fillRect((v1323_Je - (v1321_Ne / 2.0)), (v1319_Ue - v1320_De), (v1321_Ne * 0.35), v1320_De)
v1220_e.fillStyle = "#1d6683"
v1220_e.fillRect((v1323_Je - (v1321_Ne * 0.7)), (v1319_Ue - v1320_De), (v1321_Ne * 1.4), jsMax(2.0, (v1320_De * 0.04)))
v1220_e.fillRect((v1323_Je - (v1321_Ne * 0.7)), (v1319_Ue - jsMax(2.0, (v1320_De * 0.035))), (v1321_Ne * 1.4), jsMax(2.0, (v1320_De * 0.035)))
v1220_e.strokeStyle = jsGet(v1229_n, "primary")
v1220_e.globalAlpha = (num(v1317_Qe) * 0.55)
v1220_e.lineWidth = 1.0
v1220_e.strokeRect((v1323_Je - (v1321_Ne / 2.0)), (v1319_Ue - v1320_De), v1321_Ne, v1320_De)
v1220_e.fillStyle = (("rgba(" + str(jsGet(v1229_n, "waveRgb"))) + ",0.8)")
v1220_e.globalAlpha = (num(v1317_Qe) * jsMin(1.0, ((0.35 + (0.15 * kotlin.math.sin(((v1225_l * 0.6) + num(v1318_Ze))))) + (v1227_f * 0.6))))
v1220_e.fillRect((v1323_Je - (v1321_Ne * 0.3)), (v1319_Ue - (v1320_De * 0.55)), (v1321_Ne * 0.6), jsMax(1.5, (v1320_De * 0.025)))
v1220_e.restore()
null
}
null
})
v1220_e.fillStyle = "rgba(180,240,255,0.7)"
v1220_e.beginPath()
v1283_Ce = jsRound((22.0 * v1231_i))
run {
v1284_ye = 0.0
L189@ while ((v1284_ye < v1283_Ce)) {
v1285_Z = (((v1225_l + (v1226_u * 0.15)) * 0.12) + (v1284_ye * 0.035))
v1286_H = ((v1223_r + ((kotlin.math.sin((v1285_Z * 1.1)) * v1221_a) * 0.36)) + ((kotlin.math.sin((v1284_ye * 1.7)) * v1221_a) * 0.03))
v1287_fe = (((v1222_o * 0.32) + ((kotlin.math.sin(((v1285_Z * 1.9) + 1.0)) * v1222_o) * 0.1)) + ((kotlin.math.cos((v1284_ye * 2.3)) * v1222_o) * 0.025))
v1288_we = (if ((kotlin.math.cos((v1285_Z * 1.1)) >= 0.0)) 1.0 else -1.0)
v1289_Se = (v1232_g * 0.008)
v1220_e.moveTo((v1286_H + ((v1288_we * v1289_Se) * 1.6)), v1287_fe)
v1220_e.lineTo((v1286_H - (v1288_we * v1289_Se)), (v1287_fe - (v1289_Se * 0.6)))
v1220_e.lineTo((v1286_H - ((v1288_we * v1289_Se) * 0.6)), v1287_fe)
v1220_e.lineTo((v1286_H - ((v1288_we * v1289_Se) * 1.6)), (v1287_fe - (v1289_Se * 0.5)))
v1220_e.lineTo((v1286_H - ((v1288_we * v1289_Se) * 1.6)), (v1287_fe + (v1289_Se * 0.5)))
v1220_e.lineTo((v1286_H - ((v1288_we * v1289_Se) * 0.6)), v1287_fe)
v1220_e.lineTo((v1286_H - (v1288_we * v1289_Se)), (v1287_fe + (v1289_Se * 0.6)))
v1220_e.closePath()
v1284_ye++
}
}
v1220_e.fill()
v1220_e.save()
v1220_e.globalCompositeOperation = "lighter"
run {
v1290_ke = 0.0
L190@ while ((v1290_ke < 3.0)) {
v1291_Ee = (v1222_o * (1.05 - ((((v1225_l * 0.012) + (v1290_ke * 0.33)) % 1.0) * 1.2)))
v1292_Pe = ((v1221_a * (0.18 + (v1290_ke * 0.32))) + ((kotlin.math.sin(((v1225_l * 0.25) + (v1290_ke * 2.0))) * v1221_a) * 0.03))
v1293_Ie = (v1232_g * (0.03 + (v1290_ke * 0.008)))
v1294_Te = ((1.0 + (0.08 * kotlin.math.sin(((v1225_l * 1.6) + v1290_ke)))) + (v1227_f * 0.08))
v1295_ue = v1220_e.createRadialGradient(v1292_Pe, v1291_Ee, 0.0, v1292_Pe, v1291_Ee, (v1293_Ie * 1.4))
v1295_ue!!.addColorStop(0.0, (((("rgba(" + str(jsGet(v1229_n, "altWaveRgb"))) + ",") + jsToFixed((0.4 + (v1228_M * 0.45)), 3.0)) + ")"))
v1295_ue!!.addColorStop(1.0, (("rgba(" + str(jsGet(v1229_n, "altWaveRgb"))) + ",0)"))
v1220_e.fillStyle = v1295_ue!!
v1220_e.beginPath()
v1220_e.ellipse(v1292_Pe, v1291_Ee, (v1293_Ie * v1294_Te), ((v1293_Ie * 0.75) / v1294_Te), 0.0, Math.PI, v1233_m)
v1220_e.closePath()
v1220_e.fill()
v1220_e.strokeStyle = (("rgba(" + str(jsGet(v1229_n, "altWaveRgb"))) + ",0.45)")
v1220_e.lineWidth = 1.0
v1220_e.beginPath()
run {
v1296_me = -2.0
L191@ while ((v1296_me <= 2.0)) {
v1297_re = (v1292_Pe + ((v1296_me * v1293_Ie) * 0.3))
v1220_e.moveTo(v1297_re, v1291_Ee)
run {
v1298_ge = 1.0
L192@ while ((v1298_ge <= 6.0)) {
v1220_e.lineTo((v1297_re + ((kotlin.math.sin((((v1225_l * 1.2) + (v1298_ge * 0.8)) + v1296_me)) * v1293_Ie) * 0.15)), (v1291_Ee + ((v1298_ge * v1293_Ie) * 0.35)))
v1298_ge++
}
}
v1296_me++
}
}
v1220_e.stroke()
v1290_ke++
}
}
v1220_e.restore()
v1220_e.strokeStyle = "rgba(210,250,255,0.45)"
v1220_e.lineWidth = 1.0
v1220_e.beginPath()
v1299_pe = jsRound((28.0 * v1231_i))
run {
v1300_Me = 0.0
L193@ while ((v1300_Me < v1299_pe)) {
v1301_ce = ((((v1225_l + (v1226_u * 0.25)) * (0.03 + ((v1300_Me % 5.0) * 0.008))) + (v1300_Me * 0.137)) % 1.0)
v1302_be = ((v1221_a * ((v1300_Me * 0.211) % 1.0)) + (kotlin.math.sin(((v1225_l * 1.3) + v1300_Me)) * 4.0))
v1303_Ae = (v1222_o * (1.0 - v1301_ce))
v1304_Be = (1.5 + ((v1300_Me % 4.0) * 1.1))
v1220_e.moveTo((v1302_be + v1304_Be), v1303_Ae)
v1220_e.arc(v1302_be, v1303_Ae, v1304_Be, 0.0, v1233_m)
v1300_Me++
}
}
v1220_e.stroke()
run {
v1305_Re = 0.0
L194@ while ((v1305_Re < 5.0)) {
v1306_Le = (v1305_Re < 3.0)
v1307__e = (if (v1306_Le) (v1221_a * (0.02 + (v1305_Re * 0.05))) else (v1221_a * (0.9 + ((v1305_Re - 3.0) * 0.06))))
v1308_Fe = (v1222_o * (0.45 + ((v1305_Re % 3.0) * 0.12)))
v1309_Oe = (v1221_a * 0.012)
v1220_e.fillStyle = (if (truthy((v1305_Re % 2.0))) "#06301f" else "#0a3d29")
v1220_e.globalAlpha = 0.92
v1220_e.beginPath()
v1310_Ve = 10.0
v1220_e.moveTo((v1307__e - v1309_Oe), v1222_o)
run {
v1311_Ge = 0.0
L195@ while ((v1311_Ge <= v1310_Ve)) {
v1312_qe = (v1311_Ge / v1310_Ve)
v1220_e.lineTo(((v1307__e - (v1309_Oe * (1.0 - (v1312_qe * 0.7)))) + (((kotlin.math.sin((((v1225_l * 0.6) + (v1312_qe * 3.0)) + v1305_Re)) * v1312_qe) * v1221_a) * 0.025)), (v1222_o - (v1312_qe * v1308_Fe)))
v1311_Ge++
}
}
run {
v1313_Ke = v1310_Ve
L196@ while ((v1313_Ke >= 0.0)) {
v1314_S_e = (v1313_Ke / v1310_Ve)
v1220_e.lineTo(((v1307__e + (v1309_Oe * (1.0 - (v1314_S_e * 0.7)))) + (((kotlin.math.sin((((v1225_l * 0.6) + (v1314_S_e * 3.0)) + v1305_Re)) * v1314_S_e) * v1221_a) * 0.025)), (v1222_o - (v1314_S_e * v1308_Fe)))
v1313_Ke--
}
}
v1220_e.closePath()
v1220_e.fill()
v1305_Re++
}
}
v1220_e.globalAlpha = 1.0
}

fun renderScene28_ChronosphereHall(v1324_e: Ctx, v1325_a: Double, v1326_o: Double, v1327_r: Double, v1328_t: Double, v1329_l: Double, v1330_u: Double, v1331_f: Double, v1332_M: Double, v1333_n: Any?): Unit {

var v1334_s: Double = 0.0
var v1335_i: Double = 0.0
var v1336_g: Double = 0.0
var v1337_m: Double = 0.0
var v1338_d: Double = 0.0
var v1339_y: Double = 0.0
var v1340_c: Double = 0.0
var v1341_S: Any? = null
var v1342_v: Any? = null
var v1343_T: Grad? = null
var v1344_b: Double = 0.0
var v1345_h: Double = 0.0
var v1346_p: Double = 0.0
var v1347_w: Double = 0.0
var v1348_A: Double = 0.0
var v1349_C: Double = 0.0
var v1350_E: Double = 0.0
var v1351_k: Any? = null
var v1352_R: Grad? = null
var v1353_F: Double = 0.0
var v1354_O: Double = 0.0
var v1355_P: Any? = null
var v1356_I: Boolean = false
var v1357_U_: Any? = null
var v1358_B: Any? = null
var v1359_L: Double = 0.0
var v1360_G: Double = 0.0
var v1361_U: Double = 0.0
var v1362_V: Double = 0.0
var v1363_D: Double = 0.0
var v1364_K: Any? = null
var v1365_S_: Any? = null
var v1366_z: Double = 0.0
var v1367_X: Double = 0.0
var v1368_q: Double = 0.0
var v1369_Q: Double = 0.0
var v1370_W: Double = 0.0
var v1371_te: Any? = null
var v1372_le: Double = 0.0
var v1373_ie: Double = 0.0
var v1374_J: Double = 0.0
var v1375_de: Double = 0.0
var v1376_ee: Double = 0.0
var v1378_x: Double = 0.0
var v1379_N: Double = 0.0
var v1380_ae: Double = 0.0
var v1381_j: Any? = null
var v1382_oe: Double = 0.0
var v1383_Y: Double = 0.0
var v1384_ne: Double = 0.0
var v1385_he: Double = 0.0
var v1386_ve: Any? = null
var v1387_Ce: Double = 0.0
var v1388_ye: Double = 0.0
var v1389_Z: Grad? = null
var v1390_H: Grad? = null
var v1391_fe: Grad? = null
var v1392_we: Double = 0.0
var v1393_Se: Double = 0.0
var v1394_ke: Double = 0.0
var v1395_Ee: Double = 0.0
var v1396_Pe: Double = 0.0
var v1397_Ie: Double = 0.0
var v1398_Te: Double = 0.0
var v1399_ue: Double = 0.0
var v1400_me: Grad? = null
fun v1377_se(v1412_re: Any?): Unit {

var v1413_ge: Double = 0.0
var v1414_pe: Double = 0.0
var v1415_Me: Double = 0.0
var v1416_ce: Double = 0.0
var v1417_be: Double = 0.0
var v1418_Ae: Double = 0.0
var v1419_Be: Double = 0.0
var v1420_Re: Double = 0.0
var v1421_Le: Double = 0.0
v1413_ge = (0.7 + (0.3 * ((num(jsGet(v1412_re, "z")) * 0.5) + 0.5)))
v1414_pe = (num(jsGet(v1412_re, "r")) * v1413_ge)
run {
v1415_Me = 3.0
L197@ while ((v1415_Me >= 1.0)) {
v1416_ce = (num(jsGet(v1412_re, "a")) - (v1415_Me * 0.12))
v1417_be = (v1368_q + (kotlin.math.cos(v1416_ce) * num(jsGet(v1412_re, "orx"))))
v1418_Ae = ((v1369_Q + (kotlin.math.sin(v1416_ce) * num(jsGet(v1412_re, "ory")))) - (v1370_W * 0.1))
v1324_e.globalAlpha = (0.08 * (4.0 - v1415_Me))
v1324_e.strokeStyle = jsGet(v1333_n, "primary")
v1324_e.lineWidth = 1.0
v1324_e.beginPath()
v1324_e.arc(v1417_be, v1418_Ae, v1414_pe, 0.0, v1337_m)
v1324_e.stroke()
v1415_Me--
}
}
v1324_e.globalAlpha = 0.9
v1324_e.fillStyle = jsGet(jsGet(v1333_n, "floor"), 0.0)
v1324_e.beginPath()
v1324_e.arc(num(jsGet(v1412_re, "x")), num(jsGet(v1412_re, "y")), v1414_pe, 0.0, v1337_m)
v1324_e.fill()
v1324_e.strokeStyle = (if (truthy((num(jsGet(v1412_re, "c")) % 2.0))) jsGet(v1333_n, "secondary") else jsGet(v1333_n, "accent"))
v1324_e.lineWidth = (1.2 + v1413_ge)
v1324_e.stroke()
v1324_e.beginPath()
run {
v1419_Be = 0.0
L198@ while ((v1419_Be < 12.0)) {
v1420_Re = ((v1419_Be / 12.0) * v1337_m)
v1324_e.moveTo((num(jsGet(v1412_re, "x")) + ((kotlin.math.cos(v1420_Re) * v1414_pe) * 0.78)), (num(jsGet(v1412_re, "y")) + ((kotlin.math.sin(v1420_Re) * v1414_pe) * 0.78)))
v1324_e.lineTo((num(jsGet(v1412_re, "x")) + ((kotlin.math.cos(v1420_Re) * v1414_pe) * 0.92)), (num(jsGet(v1412_re, "y")) + ((kotlin.math.sin(v1420_Re) * v1414_pe) * 0.92)))
v1419_Be++
}
}
v1421_Le = (((kotlin.math.floor((v1329_l * (1.0 + (num(jsGet(v1412_re, "c")) * 0.3)))) / 60.0) * v1337_m) - (Math.PI / 2.0))
v1324_e.moveTo(num(jsGet(v1412_re, "x")), num(jsGet(v1412_re, "y")))
v1324_e.lineTo((num(jsGet(v1412_re, "x")) + ((kotlin.math.cos(v1421_Le) * v1414_pe) * 0.8)), (num(jsGet(v1412_re, "y")) + ((kotlin.math.sin(v1421_Le) * v1414_pe) * 0.8)))
v1324_e.moveTo(num(jsGet(v1412_re, "x")), num(jsGet(v1412_re, "y")))
v1324_e.lineTo((num(jsGet(v1412_re, "x")) + ((kotlin.math.cos(((v1421_Le / 12.0) + num(jsGet(v1412_re, "c")))) * v1414_pe) * 0.5)), (num(jsGet(v1412_re, "y")) + ((kotlin.math.sin(((v1421_Le / 12.0) + num(jsGet(v1412_re, "c")))) * v1414_pe) * 0.5)))
v1324_e.lineWidth = 1.0
v1324_e.stroke()
v1324_e.globalAlpha = 1.0
}
v1334_s = jsMax(0.0, jsMin(2.0, jsOrN(num(Rt.win("__rrVisualQualityStage"))) { 0.0 }))
v1335_i = (if ((v1334_s == 0.0)) 1.0 else (if ((v1334_s == 1.0)) 0.75 else 0.5))
v1336_g = jsMin(v1325_a, v1326_o)
v1337_m = (Math.PI * 2.0)
v1338_d = (if (("number" == "number")) Rt.mouseX else 0.0)
v1339_y = (v1327_r - (v1338_d * 14.0))
v1340_c = v1328_t
v1341_S = (if (truthy(jsAnd(jsAnd(!("object" == "undefined")) { Rt.freqDataArr }) { Rt.freqDataArr.size.toDouble() })) Rt.freqDataArr else null)
v1342_v = JsFn L199@{ args ->
var v1401_re: Any? = args.getOrNull(0)
return@L199 (if (truthy(v1341_S)) (jsOrND(jsGet(v1341_S, (num(v1401_re) % jsLen(v1341_S)))) { 0.0 } / 255.0) else (0.15 + (0.1 * kotlin.math.sin(((v1329_l * 1.3) + num(v1401_re))))))
null
}
v1343_T = v1324_e.createLinearGradient(0.0, 0.0, 0.0, v1326_o)
v1343_T!!.addColorStop(0.0, jsGet(jsGet(v1333_n, "sky"), 0.0))
v1343_T!!.addColorStop(0.55, jsGet(jsGet(v1333_n, "sky"), 1.0))
v1343_T!!.addColorStop(1.0, jsGet(jsGet(v1333_n, "floor"), 0.0))
v1324_e.fillStyle = v1343_T!!
v1324_e.fillRect(0.0, 0.0, v1325_a, v1326_o)
js_forEach(jsArr(jsArr(0.22, 0.25, jsGet(v1333_n, "waveRgb")), jsArr(0.78, 0.18, jsGet(v1333_n, "altWaveRgb")))) L200@{ v1402_re: Any?, v1403_ge: Double, _ ->
var v1404_pe: Double = 0.0
var v1405_Me: Double = 0.0
var v1406_ce: Double = 0.0
var v1407_be: Grad? = null

v1404_pe = ((v1325_a * num(jsGet(v1402_re, 0.0))) + ((kotlin.math.sin(((v1329_l * 0.05) + v1403_ge)) * v1325_a) * 0.02))
v1405_Me = (v1326_o * num(jsGet(v1402_re, 1.0)))
v1406_ce = (v1336_g * (0.45 + (v1332_M * 0.1)))
v1407_be = v1324_e.createRadialGradient(v1404_pe, v1405_Me, 0.0, v1404_pe, v1405_Me, v1406_ce)
v1407_be!!.addColorStop(0.0, (("rgba(" + str(jsGet(v1402_re, 2.0))) + ",0.22)"))
v1407_be!!.addColorStop(1.0, (("rgba(" + str(jsGet(v1402_re, 2.0))) + ",0)"))
v1324_e.fillStyle = v1407_be!!
v1324_e.fillRect((v1404_pe - v1406_ce), (v1405_Me - v1406_ce), (v1406_ce * 2.0), (v1406_ce * 2.0))
null
}
v1324_e.fillStyle = "#ffffff"
run {
v1344_b = 0.0
L201@ while ((v1344_b < jsRound((70.0 * v1335_i)))) {
v1345_h = ((((((v1344_b * 211.7) - (v1329_l * (2.0 + (v1344_b % 3.0)))) - (v1338_d * 6.0)) % v1325_a) + v1325_a) % v1325_a)
v1346_p = ((v1344_b * 97.3) % (v1340_c * 0.95))
v1324_e.globalAlpha = (0.2 + (0.3 * (0.5 + (0.5 * kotlin.math.sin(((v1329_l * 0.6) + v1344_b))))))
v1324_e.fillRect(v1345_h, v1346_p, (if (truthy((v1344_b % 7.0))) 1.1 else 2.0), (if (truthy((v1344_b % 7.0))) 1.1 else 2.0))
v1344_b++
}
}
v1324_e.globalAlpha = 1.0
v1347_w = v1339_y
v1348_A = (v1340_c + ((v1326_o - v1340_c) * 0.5))
v1349_C = (v1325_a * 0.62)
v1350_E = ((v1326_o - v1340_c) * 0.46)
v1351_k = JsFn L202@{ args ->
var v1408_re: Any? = args.getOrNull(0)
var v1409_ge: Any? = args.getOrNull(1)
return@L202 jsArr((v1347_w + ((kotlin.math.cos(num(v1408_re)) * v1349_C) * num(v1409_ge))), (v1348_A + ((kotlin.math.sin(num(v1408_re)) * v1350_E) * num(v1409_ge))))
null
}
v1352_R = v1324_e.createRadialGradient(v1347_w, v1348_A, 0.0, v1347_w, v1348_A, v1349_C)
v1352_R!!.addColorStop(0.0, jsGet(jsGet(v1333_n, "floor"), 1.0))
v1352_R!!.addColorStop(1.0, jsGet(jsGet(v1333_n, "floor"), 0.0))
v1324_e.fillStyle = v1352_R!!
v1324_e.beginPath()
v1324_e.ellipse(v1347_w, v1348_A, v1349_C, v1350_E, 0.0, 0.0, v1337_m)
v1324_e.fill()
v1324_e.strokeStyle = jsGet(v1333_n, "secondary")
v1324_e.lineWidth = 1.2
js_forEach(jsArr(1.0, 0.82, 0.55, 0.3)) L203@{ v1410_re: Any?, v1411_ge: Double, _ ->

v1324_e.globalAlpha = (0.55 - (v1411_ge * 0.1))
v1324_e.beginPath()
v1324_e.ellipse(v1347_w, v1348_A, (v1349_C * num(v1410_re)), (v1350_E * num(v1410_re)), 0.0, 0.0, v1337_m)
v1324_e.stroke()
null
}
v1324_e.globalAlpha = 1.0
run {
v1353_F = 0.0
L204@ while ((v1353_F < 60.0)) {
v1354_O = (((v1353_F / 60.0) * v1337_m) - (Math.PI / 2.0))
v1355_P = jsCall(v1342_v, (2.0 + (v1353_F * 2.0)))
v1356_I = ((v1353_F % 5.0) == 0.0)
v1357_U_ = jsCall(v1351_k, v1354_O, 0.86)
v1358_B = jsCall(v1351_k, v1354_O, (if (v1356_I) 0.97 else 0.93))
v1359_L = (0.55 + (0.45 * ((kotlin.math.sin(v1354_O) * 0.5) + 0.5)))
v1360_G = ((4.0 + ((num(v1355_P) * v1336_g) * 0.12)) * v1359_L)
v1361_U = ((if (v1356_I) 5.0 else 3.0) * v1359_L)
v1324_e.strokeStyle = (if (v1356_I) jsGet(v1333_n, "accent") else jsGet(v1333_n, "primary"))
v1324_e.globalAlpha = (0.5 + (0.5 * num(v1355_P)))
v1324_e.lineWidth = (v1361_U * 0.5)
v1324_e.beginPath()
v1324_e.moveTo(num(jsGet(v1357_U_, 0.0)), num(jsGet(v1357_U_, 1.0)))
v1324_e.lineTo(num(jsGet(v1358_B, 0.0)), num(jsGet(v1358_B, 1.0)))
v1324_e.stroke()
v1324_e.fillStyle = (((("rgba(" + str((if (truthy((v1353_F % 2.0))) jsGet(v1333_n, "waveRgb") else jsGet(v1333_n, "altWaveRgb")))) + ",") + jsToFixed((0.35 + (num(v1355_P) * 0.6)), 3.0)) + ")")
v1324_e.fillRect((num(jsGet(v1358_B, 0.0)) - (v1361_U / 2.0)), (num(jsGet(v1358_B, 1.0)) - v1360_G), v1361_U, v1360_G)
v1353_F++
}
}
v1324_e.globalAlpha = 1.0
v1324_e.lineCap = "round"
v1362_V = ((v1329_l * 0.05) - (Math.PI / 2.0))
v1363_D = (((v1329_l * 0.05) / 12.0) + 0.8)
v1364_K = jsCall(v1351_k, v1363_D, 0.45)
v1365_S_ = jsCall(v1351_k, v1362_V, 0.72)
v1324_e.strokeStyle = jsGet(v1333_n, "accent")
v1324_e.globalAlpha = 0.85
v1324_e.lineWidth = jsMax(3.0, (v1336_g / 120.0))
v1324_e.beginPath()
v1324_e.moveTo(v1347_w, v1348_A)
v1324_e.lineTo(num(jsGet(v1364_K, 0.0)), num(jsGet(v1364_K, 1.0)))
v1324_e.stroke()
v1324_e.strokeStyle = jsGet(v1333_n, "primary")
v1324_e.lineWidth = jsMax(2.0, (v1336_g / 200.0))
v1324_e.beginPath()
v1324_e.moveTo(v1347_w, v1348_A)
v1324_e.lineTo(num(jsGet(v1365_S_, 0.0)), num(jsGet(v1365_S_, 1.0)))
v1324_e.stroke()
v1324_e.globalAlpha = 1.0
run {
v1366_z = 0.0
L205@ while ((v1366_z < 2.0)) {
v1367_X = (((v1329_l * 0.45) + (v1366_z * 0.5)) % 1.0)
v1324_e.strokeStyle = (((("rgba(" + str(jsGet(v1333_n, "waveRgb"))) + ",") + jsToFixed(((1.0 - v1367_X) * (0.15 + (v1331_f * 0.7))), 3.0)) + ")")
v1324_e.lineWidth = (1.5 + (v1331_f * 4.0))
v1324_e.beginPath()
v1324_e.ellipse(v1347_w, v1348_A, (v1349_C * v1367_X), (v1350_E * v1367_X), 0.0, 0.0, v1337_m)
v1324_e.stroke()
v1366_z++
}
}
v1368_q = v1339_y
v1369_Q = ((v1340_c - (v1326_o * 0.06)) + ((kotlin.math.sin((v1329_l * 0.45)) * v1326_o) * 0.012))
v1370_W = ((v1336_g * 0.15) * (1.0 + (v1331_f * 0.07)))
v1371_te = jsArr()
v1372_le = 6.0
run {
v1373_ie = 0.0
L206@ while ((v1373_ie < v1372_le)) {
v1374_J = ((v1329_l * (0.18 + ((v1373_ie % 3.0) * 0.04))) + ((v1373_ie / v1372_le) * v1337_m))
v1375_de = (v1370_W * (1.9 + ((v1373_ie % 2.0) * 0.45)))
v1376_ee = (v1370_W * 0.55)
jsPush(v1371_te, jsObj("a" to v1374_J, "x" to (v1368_q + (kotlin.math.cos(v1374_J) * v1375_de)), "y" to ((v1369_Q + (kotlin.math.sin(v1374_J) * v1376_ee)) - (v1370_W * 0.1)), "z" to kotlin.math.sin(v1374_J), "r" to (v1370_W * (0.2 + ((v1373_ie % 3.0) * 0.05))), "c" to v1373_ie, "orx" to v1375_de, "ory" to v1376_ee, "sp" to (0.18 + ((v1373_ie % 3.0) * 0.04))))
v1373_ie++
}
}
js_forEach(js_filter(v1371_te) L207@{ v1422_re: Any?, _, _ ->

return@L207 (num(jsGet(v1422_re, "z")) < 0.0)
null
}) { a0, a1, _ -> v1377_se(a0); null }
v1378_x = jsRound((8.0 * (0.6 + (v1335_i * 0.4))))
run {
v1379_N = 0.0
L208@ while ((v1379_N < v1378_x)) {
v1380_ae = (((v1379_N / v1378_x) * v1337_m) + (v1329_l * 0.02))
v1381_j = jsCall(v1351_k, v1380_ae, 1.08)
v1382_oe = (0.5 + (0.5 * ((kotlin.math.sin(v1380_ae) * 0.5) + 0.5)))
v1383_Y = ((v1336_g * 0.03) * v1382_oe)
v1384_ne = ((v1336_g * (0.12 + ((v1379_N % 3.0) * 0.05))) * v1382_oe)
v1385_he = (((kotlin.math.sin(((v1329_l * 0.5) + (v1379_N * 1.7))) * v1336_g) * 0.015) + ((v1331_f * v1336_g) * 0.02))
v1386_ve = jsGet(v1381_j, 0.0)
v1387_Ce = (((num(jsGet(v1381_j, 1.0)) - v1384_ne) - ((v1336_g * 0.04) * v1382_oe)) - v1385_he)
v1388_ye = (kotlin.math.sin(((v1329_l * 0.2) + v1379_N)) * 0.08)
v1324_e.save()
v1324_e.translate(num(v1386_ve), (v1387_Ce + (v1384_ne / 2.0)))
v1324_e.rotate(v1388_ye)
v1324_e.globalAlpha = 0.95
v1324_e.fillStyle = jsGet(v1333_n, "mtnBot")
v1324_e.beginPath()
v1324_e.moveTo((-v1383_Y / 2.0), (v1384_ne / 2.0))
v1324_e.lineTo((v1383_Y / 2.0), (v1384_ne / 2.0))
v1324_e.lineTo((v1383_Y / 2.0), ((-v1384_ne / 2.0) + (v1383_Y * 0.4)))
v1324_e.lineTo((v1383_Y * 0.1), (-v1384_ne / 2.0))
v1324_e.lineTo((-v1383_Y * 0.2), ((-v1384_ne / 2.0) + (v1383_Y * 0.6)))
v1324_e.lineTo((-v1383_Y / 2.0), ((-v1384_ne / 2.0) + (v1383_Y * 0.2)))
v1324_e.closePath()
v1324_e.fill()
v1324_e.fillStyle = jsGet(v1333_n, "mtnTop")
v1324_e.globalAlpha = 0.5
v1324_e.fillRect((-v1383_Y / 2.0), ((-v1384_ne / 2.0) + (v1383_Y * 0.3)), (v1383_Y * 0.3), (v1384_ne - (v1383_Y * 0.3)))
v1324_e.strokeStyle = jsGet(v1333_n, "secondary")
v1324_e.globalAlpha = 0.6
v1324_e.lineWidth = 1.0
v1324_e.stroke()
v1324_e.fillStyle = jsGet(v1333_n, "accent")
v1324_e.globalAlpha = 0.9
v1324_e.fillRect((-v1383_Y * 0.7), ((v1384_ne / 2.0) - (v1383_Y * 0.25)), (v1383_Y * 1.4), (v1383_Y * 0.25))
v1324_e.restore()
v1324_e.fillStyle = "rgba(0,0,0,0.25)"
v1324_e.beginPath()
v1324_e.ellipse(num(jsGet(v1381_j, 0.0)), num(jsGet(v1381_j, 1.0)), (v1383_Y * 1.2), (v1383_Y * 0.35), 0.0, 0.0, v1337_m)
v1324_e.fill()
v1379_N++
}
}
v1324_e.globalAlpha = 1.0
v1389_Z = v1324_e.createLinearGradient(0.0, v1369_Q, 0.0, v1348_A)
v1389_Z!!.addColorStop(0.0, (((("rgba(" + str(jsGet(v1333_n, "waveRgb"))) + ",") + jsToFixed((0.25 + (v1331_f * 0.3)), 3.0)) + ")"))
v1389_Z!!.addColorStop(1.0, (("rgba(" + str(jsGet(v1333_n, "waveRgb"))) + ",0.02)"))
v1324_e.fillStyle = v1389_Z!!
v1324_e.beginPath()
v1324_e.moveTo((v1368_q - (v1370_W * 0.3)), v1369_Q)
v1324_e.lineTo((v1368_q + (v1370_W * 0.3)), v1369_Q)
v1324_e.lineTo((v1347_w + (v1370_W * 0.9)), v1348_A)
v1324_e.lineTo((v1347_w - (v1370_W * 0.9)), v1348_A)
v1324_e.closePath()
v1324_e.fill()
v1390_H = v1324_e.createRadialGradient(v1368_q, v1369_Q, (v1370_W * 0.6), v1368_q, v1369_Q, (v1370_W * 1.8))
v1390_H!!.addColorStop(0.0, (((("rgba(" + str(jsGet(v1333_n, "waveRgb"))) + ",") + jsToFixed((0.25 + (v1331_f * 0.35)), 3.0)) + ")"))
v1390_H!!.addColorStop(1.0, (("rgba(" + str(jsGet(v1333_n, "waveRgb"))) + ",0)"))
v1324_e.fillStyle = v1390_H!!
v1324_e.beginPath()
v1324_e.arc(v1368_q, v1369_Q, (v1370_W * 1.8), 0.0, v1337_m)
v1324_e.fill()
v1391_fe = v1324_e.createRadialGradient((v1368_q - (v1370_W * 0.35)), (v1369_Q - (v1370_W * 0.4)), (v1370_W * 0.05), v1368_q, v1369_Q, v1370_W)
v1391_fe!!.addColorStop(0.0, "rgba(255,255,255,0.35)")
v1391_fe!!.addColorStop(0.5, (("rgba(" + str(jsGet(v1333_n, "altWaveRgb"))) + ",0.18)"))
v1391_fe!!.addColorStop(1.0, (("rgba(" + str(jsGet(v1333_n, "waveRgb"))) + ",0.35)"))
v1324_e.fillStyle = v1391_fe!!
v1324_e.beginPath()
v1324_e.arc(v1368_q, v1369_Q, v1370_W, 0.0, v1337_m)
v1324_e.fill()
v1324_e.save()
v1324_e.beginPath()
v1324_e.arc(v1368_q, v1369_Q, (v1370_W * 0.96), 0.0, v1337_m)
v1324_e.clip()
v1392_we = ((v1329_l * 0.35) + (v1330_u * 0.25))
v1324_e.lineCap = "round"
run {
v1393_Se = 0.0
L209@ while ((v1393_Se < 3.0)) {
v1324_e.strokeStyle = (if ((v1393_Se == 0.0)) jsGet(v1333_n, "primary") else (if ((v1393_Se == 1.0)) jsGet(v1333_n, "secondary") else jsGet(v1333_n, "accent")))
v1324_e.globalAlpha = 0.75
v1324_e.lineWidth = (jsMax(1.5, (v1336_g / 260.0)) * (1.0 + v1332_M))
v1324_e.beginPath()
run {
v1394_ke = 0.0
L210@ while ((v1394_ke <= 40.0)) {
v1395_Ee = (v1394_ke / 40.0)
v1396_Pe = ((v1392_we + ((v1393_Se / 3.0) * v1337_m)) + (v1395_Ee * 5.2))
v1397_Ie = ((v1395_Ee * v1370_W) * 0.92)
v1398_Te = (v1368_q + (kotlin.math.cos(v1396_Pe) * v1397_Ie))
v1399_ue = (v1369_Q + ((kotlin.math.sin(v1396_Pe) * v1397_Ie) * 0.62))
if (truthy(v1394_ke)) {
v1324_e.lineTo(v1398_Te, v1399_ue)
} else {
v1324_e.moveTo(v1398_Te, v1399_ue)
}
v1394_ke++
}
}
v1324_e.stroke()
v1393_Se++
}
}
v1400_me = v1324_e.createRadialGradient(v1368_q, v1369_Q, 0.0, v1368_q, v1369_Q, (v1370_W * (0.22 + (v1331_f * 0.15))))
v1400_me!!.addColorStop(0.0, "rgba(255,255,255,0.95)")
v1400_me!!.addColorStop(1.0, (("rgba(" + str(jsGet(v1333_n, "waveRgb"))) + ",0)"))
v1324_e.globalAlpha = 1.0
v1324_e.fillStyle = v1400_me!!
v1324_e.beginPath()
v1324_e.arc(v1368_q, v1369_Q, (v1370_W * 0.4), 0.0, v1337_m)
v1324_e.fill()
v1324_e.restore()
v1324_e.strokeStyle = "rgba(255,255,255,0.55)"
v1324_e.lineWidth = 1.5
v1324_e.beginPath()
v1324_e.arc(v1368_q, v1369_Q, v1370_W, 0.0, v1337_m)
v1324_e.stroke()
v1324_e.strokeStyle = jsGet(v1333_n, "accent")
v1324_e.globalAlpha = 0.7
v1324_e.lineWidth = 1.2
v1324_e.beginPath()
v1324_e.ellipse(v1368_q, v1369_Q, (v1370_W * 1.12), (v1370_W * 0.22), -0.25, 0.0, v1337_m)
v1324_e.stroke()
v1324_e.globalAlpha = 1.0
v1324_e.fillStyle = "rgba(255,255,255,0.5)"
v1324_e.beginPath()
v1324_e.ellipse((v1368_q - (v1370_W * 0.4)), (v1369_Q - (v1370_W * 0.45)), (v1370_W * 0.18), (v1370_W * 0.08), -0.6, 0.0, v1337_m)
v1324_e.fill()
js_forEach(js_filter(v1371_te) L211@{ v1423_re: Any?, _, _ ->

return@L211 (num(jsGet(v1423_re, "z")) >= 0.0)
null
}) { a0, a1, _ -> v1377_se(a0); null }
}

fun renderExtraBackgroundDepth(v1424_e: Ctx, v1425_a: Double, v1426_o: Double, v1427_r: Double, v1428_t: Double, v1429_l: Double, v1430_u: Double, v1431_f: Any?, v1432_M: Double, v1433_n: Double): Unit {

var v1434_s: Double = 0.0
var v1435_i: Double = 0.0
var v1436_g: Double = 0.0
var v1437_m: Boolean = false
var v1438_d: Double = 0.0
var v1439_y: Any? = null
var v1440_c: Double = 0.0
var v1441_S: Double = 0.0
var v1442_v: Double = 0.0
var v1443_T: Double = 0.0
var v1444_b: Double = 0.0
var v1445_h: Double = 0.0
var v1446_p: Double = 0.0
var v1447_w: Double = 0.0
var v1448_A: Double = 0.0
var v1449_C: Double = 0.0
var v1450_E: Double = 0.0
var v1451_k: Double = 0.0
var v1452_R: Double = 0.0
var v1453_F: Double = 0.0
var v1454_O: Double = 0.0
var v1455_P: Double = 0.0
var v1456_I: Double = 0.0
var v1457_U_: Double = 0.0
var v1458_B: Double = 0.0
var v1459_L: Double = 0.0
var v1460_G: Double = 0.0
var v1461_U: Double = 0.0
var v1462_V: Double = 0.0
var v1463_D: Double = 0.0
var v1464_K: Double = 0.0
var v1465_S_: Double = 0.0
var v1466_z: Double = 0.0
var v1467_X: Double = 0.0
var v1468_q: Double = 0.0
var v1469_Q: Double = 0.0
var v1470_W: Double = 0.0
var v1471_te: Double = 0.0
var v1472_le: Double = 0.0
var v1473_ie: Double = 0.0
var v1474_J: Double = 0.0
var v1475_de: Double = 0.0
var v1476_ee: Double = 0.0
var v1477_se: Double = 0.0
var v1478_x: Double = 0.0
var v1479_N: Double = 0.0
var v1480_ae: Double = 0.0
var v1481_j: Double = 0.0
var v1482_oe: Double = 0.0
var v1483_Y: Double = 0.0
var v1484_ne: Double = 0.0
v1434_s = jsMax(0.0, jsMin(3.0, jsOrND(Rt.win("__rrVisualQualityStage")) { 0.0 }))
if (truthy(jsOr((v1434_s >= 2.0)) { Rt.win("__rrTealSafeModeActive") })) {
return
}
v1435_i = (if ((v1434_s == 1.0)) 0.56 else 1.0)
if (truthy(jsGet(g_bgLayers, "depthMotes"))) {
v1436_g = jsOrND(Rt.win("__rrAdaptiveCanvasScale")) { 1.0 }
v1437_m = !(!(truthy(jsOr(jsOr(Rt.win("__rrAdaptivePerformanceActive")) { Rt.win("__rrAudioPrioritySaverActive") }) { Rt.win("batteryPerformanceMode") }) || (v1436_g < 0.82)))
v1438_d = (if ((v1434_s == 1.0)) 34.0 else (if (v1437_m) 42.0 else 72.0))
v1439_y = jsArr(jsGet(v1431_f, "primary"), jsGet(v1431_f, "secondary"), jsGet(v1431_f, "accent"))
run {
v1440_c = 0.0
L212@ while ((v1440_c < 3.0)) {
v1424_e.beginPath()
run {
v1441_S = v1440_c
L213@ while ((v1441_S < v1438_d)) {
v1442_v = (0.18 + (((v1441_S * 37.0) % 79.0) / 100.0))
v1443_T = (v1441_S * 12.9898)
v1444_b = ((((((v1441_S * 0.61803398875) + 0.13) + (v1427_r * (0.006 + (v1442_v * 0.016)))) % 1.0) + 1.0) % 1.0)
v1445_h = (((((((v1441_S * 0.371) + 0.07) + (v1427_r * (0.004 + (v1442_v * 0.009)))) + (kotlin.math.sin(((v1427_r * 0.6) + v1443_T)) * 0.018)) % 1.0) + 1.0) % 1.0)
v1446_p = ((v1444_b * v1425_a) + ((Rt.mouseX * v1442_v) * 18.0))
v1447_w = ((v1445_h * v1426_o) + ((Rt.mouseY * v1442_v) * 12.0))
v1448_A = ((0.65 + (v1442_v * 1.65)) + ((v1428_t * v1442_v) * 0.9))
v1424_e.moveTo((v1446_p + v1448_A), v1447_w)
v1424_e.arc(v1446_p, v1447_w, v1448_A, 0.0, (Math.PI * 2.0))
v1441_S += 3.0
}
}
v1424_e.fillStyle = jsGet(v1439_y, v1440_c)
v1424_e.globalAlpha = ((0.18 + (v1430_u * 0.1)) * v1435_i)
v1424_e.fill()
v1440_c++
}
}
v1424_e.globalAlpha = 1.0
}
if ((truthy(jsGet(g_bgLayers, "parallaxRings")) && (v1434_s == 0.0))) {
v1449_C = (v1432_M + (Rt.mouseX * 12.0))
v1450_E = ((v1433_n + (v1426_o * 0.08)) + (Rt.mouseY * 7.0))
run {
v1451_k = 0.0
L214@ while ((v1451_k < 2.0)) {
v1424_e.beginPath()
run {
v1452_R = v1451_k
L215@ while ((v1452_R < 7.0)) {
v1453_F = (((v1427_r * (0.11 + (v1452_R * 0.012))) + (v1452_R / 7.0)) % 1.0)
v1454_O = (0.15 + (v1453_F * 0.85))
v1455_P = (v1425_a * (0.035 + (v1454_O * 0.36)))
v1456_I = (v1426_o * (0.008 + (v1454_O * 0.105)))
v1457_U_ = (v1449_C + (((kotlin.math.sin(((v1427_r * 0.22) + v1452_R)) * v1425_a) * 0.025) * v1454_O))
v1458_B = (v1450_E + ((((v1452_R - 3.0) * v1426_o) * 0.018) * v1454_O))
v1459_L = (kotlin.math.sin(((v1427_r * 0.13) + (v1452_R * 0.7))) * 0.08)
v1424_e.ellipse(v1457_U_, v1458_B, v1455_P, v1456_I, v1459_L, 0.0, (Math.PI * 2.0))
v1452_R += 2.0
}
}
v1424_e.strokeStyle = (if (truthy(v1451_k)) jsGet(v1431_f, "secondary") else jsGet(v1431_f, "primary"))
v1424_e.lineWidth = (1.1 + (v1428_t * 0.8))
v1424_e.globalAlpha = (0.13 + (v1429_l * 0.08))
v1424_e.stroke()
v1451_k++
}
}
v1424_e.globalAlpha = 1.0
}
if (truthy(jsGet(g_bgLayers, "glassShards"))) {
run {
v1460_G = 0.0
L216@ while ((v1460_G < 2.0)) {
v1424_e.beginPath()
run {
v1461_U = v1460_G
L217@ while ((v1461_U < (if ((v1434_s == 1.0)) 8.0 else 16.0))) {
v1462_V = (0.18 + (((v1461_U * 29.0) % 77.0) / 100.0))
v1463_D = (v1461_U * 7.13)
v1464_K = ((((((v1461_U * 0.754877666) + 0.19) + (v1427_r * (0.002 + (v1462_V * 0.006)))) % 1.0) + 1.0) % 1.0)
v1465_S_ = (0.12 + (((v1461_U * 0.317) + (kotlin.math.sin(((v1427_r * 0.25) + v1463_D)) * 0.04)) % 0.76))
v1466_z = ((v1464_K * v1425_a) + ((Rt.mouseX * v1462_V) * 28.0))
v1467_X = ((v1465_S_ * v1426_o) + ((Rt.mouseY * v1462_V) * 18.0))
v1468_q = ((7.0 + (v1462_V * 24.0)) * (1.0 + (v1428_t * 0.12)))
v1469_Q = ((v1427_r * (0.12 + (v1462_V * 0.2))) + v1463_D)
v1424_e.moveTo((v1466_z + (kotlin.math.cos(v1469_Q) * v1468_q)), (v1467_X + (kotlin.math.sin(v1469_Q) * v1468_q)))
v1424_e.lineTo((v1466_z + ((kotlin.math.cos((v1469_Q + 2.25)) * v1468_q) * 0.68)), (v1467_X + ((kotlin.math.sin((v1469_Q + 2.25)) * v1468_q) * 0.68)))
v1424_e.lineTo((v1466_z + ((kotlin.math.cos((v1469_Q + 3.8)) * v1468_q) * 0.42)), (v1467_X + ((kotlin.math.sin((v1469_Q + 3.8)) * v1468_q) * 0.42)))
v1424_e.lineTo((v1466_z + ((kotlin.math.cos((v1469_Q + 5.05)) * v1468_q) * 0.82)), (v1467_X + ((kotlin.math.sin((v1469_Q + 5.05)) * v1468_q) * 0.82)))
v1424_e.closePath()
v1461_U += 2.0
}
}
v1424_e.fillStyle = (if (truthy(v1460_G)) jsGet(v1431_f, "secondary") else jsGet(v1431_f, "primary"))
v1424_e.globalAlpha = ((0.065 + (v1430_u * 0.025)) * v1435_i)
v1424_e.fill()
v1424_e.strokeStyle = jsGet(v1431_f, "accent")
v1424_e.globalAlpha = (0.12 * v1435_i)
v1424_e.lineWidth = 0.8
v1424_e.stroke()
v1460_G++
}
}
v1424_e.globalAlpha = 1.0
}
if (truthy(jsGet(g_bgLayers, "auroraRibbons"))) {
run {
v1470_W = 0.0
L218@ while ((v1470_W < (if ((v1434_s == 1.0)) 2.0 else 3.0))) {
v1424_e.beginPath()
run {
v1471_te = 0.0
L219@ while ((v1471_te <= v1425_a)) {
v1472_le = (((v1426_o * (0.16 + (v1470_W * 0.105))) + ((kotlin.math.sin(((((v1471_te / v1425_a) * 5.0) + (v1427_r * 0.16)) + v1470_W)) * v1426_o) * 0.035)) + ((kotlin.math.sin((((v1471_te / v1425_a) * 9.0) - (v1427_r * 0.11))) * v1426_o) * 0.012))
if ((v1471_te == 0.0)) {
v1424_e.moveTo(v1471_te, v1472_le)
} else {
v1424_e.lineTo(v1471_te, v1472_le)
}
v1471_te += jsMax(18.0, (v1425_a / 28.0))
}
}
v1424_e.strokeStyle = (if ((v1470_W == 1.0)) jsGet(v1431_f, "secondary") else jsGet(v1431_f, "primary"))
v1424_e.lineWidth = jsMax(7.0, (v1426_o * 0.035))
v1424_e.globalAlpha = ((0.075 + (v1430_u * 0.035)) * v1435_i)
v1424_e.stroke()
v1470_W++
}
}
v1424_e.globalAlpha = 1.0
}
if (truthy(jsGet(g_bgLayers, "cometTrails"))) {
v1424_e.beginPath()
run {
v1473_ie = 0.0
L220@ while ((v1473_ie < (if ((v1434_s == 1.0)) 6.0 else 12.0))) {
v1474_J = ((((v1473_ie * 149.0) + (v1427_r * (28.0 + ((v1473_ie % 4.0) * 9.0)))) % (v1425_a + 90.0)) - 45.0)
v1475_de = (((v1473_ie * 83.0) + (kotlin.math.sin(((v1427_r * 0.24) + v1473_ie)) * 12.0)) % jsMax(1.0, v1433_n))
v1476_ee = (12.0 + ((v1473_ie % 5.0) * 8.0))
v1424_e.moveTo(v1474_J, v1475_de)
v1424_e.lineTo((v1474_J - v1476_ee), (v1475_de + (v1476_ee * 0.22)))
v1473_ie++
}
}
v1424_e.strokeStyle = jsGet(v1431_f, "accent")
v1424_e.lineWidth = 1.15
v1424_e.globalAlpha = ((0.2 + (v1430_u * 0.12)) * v1435_i)
v1424_e.stroke()
v1424_e.globalAlpha = 1.0
}
if (truthy(jsGet(g_bgLayers, "lightShafts"))) {
v1424_e.beginPath()
run {
v1477_se = 0.0
L221@ while ((v1477_se < (if ((v1434_s == 1.0)) 4.0 else 7.0))) {
v1478_x = ((v1432_M + (((v1477_se - 3.0) * v1425_a) * 0.055)) + (Rt.mouseX * 8.0))
v1479_N = (v1425_a * (0.025 + ((v1477_se % 3.0) * 0.008)))
v1424_e.moveTo((v1478_x - v1479_N), 0.0)
v1424_e.lineTo((v1478_x + v1479_N), 0.0)
v1424_e.lineTo((v1432_M + (((v1477_se - 3.0) * v1425_a) * 0.13)), v1433_n)
v1424_e.closePath()
v1477_se++
}
}
v1424_e.fillStyle = jsGet(v1431_f, "accent")
v1424_e.globalAlpha = ((0.035 + (v1429_l * 0.025)) * v1435_i)
v1424_e.fill()
v1424_e.globalAlpha = 1.0
}
if (truthy(jsGet(g_bgLayers, "depthRidges"))) {
run {
v1480_ae = 0.0
L222@ while ((v1480_ae < (if ((v1434_s == 1.0)) 2.0 else 3.0))) {
v1481_j = (v1433_n + (v1426_o * (0.06 + (v1480_ae * 0.075))))
v1424_e.beginPath()
v1424_e.moveTo(0.0, (v1481_j + (v1426_o * 0.07)))
run {
v1482_oe = 0.0
L223@ while ((v1482_oe <= 8.0)) {
v1483_Y = ((v1482_oe * v1425_a) / 8.0)
v1484_ne = (v1481_j + ((kotlin.math.sin(((v1482_oe * 1.7) + (v1480_ae * 0.8))) * v1426_o) * (0.018 + (v1480_ae * 0.006))))
v1424_e.lineTo(v1483_Y, v1484_ne)
v1482_oe++
}
}
v1424_e.lineTo(v1425_a, v1426_o)
v1424_e.lineTo(0.0, v1426_o)
v1424_e.closePath()
v1424_e.fillStyle = (if ((v1480_ae == 1.0)) jsGet(v1431_f, "secondary") else jsGet(v1431_f, "primary"))
v1424_e.globalAlpha = ((0.045 + (v1480_ae * 0.018)) * v1435_i)
v1424_e.fill()
v1480_ae++
}
}
v1424_e.globalAlpha = 1.0
}
}

fun createRoomProjector(v1485_e: Double, v1486_a: Double, v1487_o: Double, v1488_r: Double): Any? {

return JsFn L224@{ args ->
var v1489_t: Any? = args.getOrNull(0)
var v1490_l: Any? = args.getOrNull(1)
var v1491_u: Any? = args.getOrNull(2)
return@L224 jsObj("x" to (v1487_o + ((num(v1489_t) * v1485_e) * num(v1491_u))), "y" to (v1488_r + (if ((num(v1490_l) < 0.0)) ((num(v1490_l) * v1488_r) * num(v1491_u)) else ((num(v1490_l) * (v1486_a - v1488_r)) * num(v1491_u)))))
null
}
return null
}

fun drawQuad3D(v1492_e: Ctx, v1493_a: Any?, v1494_o: Any?, v1495_r: Any?, v1496_t: Any?, v1497_l: Any?, v1498_u: Any?, v1499_f: Double): Unit {

v1492_e.beginPath()
v1492_e.moveTo(num(jsGet(v1493_a, "x")), num(jsGet(v1493_a, "y")))
v1492_e.lineTo(num(jsGet(v1494_o, "x")), num(jsGet(v1494_o, "y")))
v1492_e.lineTo(num(jsGet(v1495_r, "x")), num(jsGet(v1495_r, "y")))
v1492_e.lineTo(num(jsGet(v1496_t, "x")), num(jsGet(v1496_t, "y")))
v1492_e.closePath()
if (truthy(v1497_l)) {
v1492_e.fillStyle = v1497_l
v1492_e.fill()
}
if (truthy(v1498_u)) {
v1492_e.strokeStyle = v1498_u
v1492_e.lineWidth = jsOrN(v1499_f) { 1.0 }
v1492_e.stroke()
}
}

fun rrBackroomsNearFade(v1500_e: Double, v1501_a: Double, v1502_o: Double): Double {

var v1503_r: Double = 0.0
var v1504_t: Double = 0.0
v1503_r = jsMax(0.0, jsMin(1.0, ((v1500_e - v1501_a) / (v1502_o - v1501_a))))
v1504_t = ((v1503_r * v1503_r) * (3.0 - (2.0 * v1503_r)))
return (1.0 - v1504_t)
return Double.NaN
}

fun renderScene9_BackroomsLevel0(v1505_e: Ctx, v1506_a: Double, v1507_o: Double, v1508_r: Double, v1509_t: Double, v1510_l: Double, v1511_u: Double, v1512_f: Double, v1513_M: Double, v1514_n: Any?): Unit {

var v1515_s: Any? = null
var v1516_i: Grad? = null
var v1517_g: Grad? = null
var v1518_m: Double = 0.0
var v1519_d: Double = 0.0
var v1520_y: Any? = null
var v1521_c: Any? = null
var v1522_S: Any? = null
var v1523_v: Any? = null
var v1524_T: Grad? = null
var v1525_b: Double = 0.0
var v1526_h: Double = 0.0
var v1527_p: Double = 0.0
var v1528_w: Double = 0.0
var v1529_A: Double = 0.0
var v1530_C: Double = 0.0
var v1531_E: Double = 0.0
var v1532_k: Double = 0.0
var v1533_R: Double = 0.0
var v1534_F: Double = 0.0
var v1535_O: Double = 0.0
var v1536_P: Double = 0.0
var v1537_I: Double = 0.0
var v1538_U_: Double = 0.0
var v1539_B: Boolean = false
var v1540_L: Double = 0.0
var v1541_G: Double = 0.0
var v1542_U: Double = 0.0
var v1543_V: Double = 0.0
var v1544_D: Double = 0.0
var v1545_K: Double = 0.0
var v1546_S_: Double = 0.0
var v1547_z: Double = 0.0
var v1548_X: Double = 0.0
var v1549_q: Double = 0.0
var v1550_Q: Double = 0.0
var v1551_W: Double = 0.0
var v1552_te: Double = 0.0
var v1553_le: Double = 0.0
var v1554_ie: Grad? = null
v1515_s = createRoomProjector(v1506_a, v1507_o, v1508_r, v1509_t)
v1516_i = v1505_e.createLinearGradient(0.0, 0.0, 0.0, v1509_t)
v1516_i!!.addColorStop(0.0, "#c7b863")
v1516_i!!.addColorStop(0.65, "#8c7d32")
v1516_i!!.addColorStop(1.0, "#1f1a07")
v1505_e.fillStyle = v1516_i!!
v1505_e.fillRect(0.0, 0.0, v1506_a, v1509_t)
v1517_g = v1505_e.createLinearGradient(0.0, v1509_t, 0.0, v1507_o)
v1517_g!!.addColorStop(0.0, "#1a1505")
v1517_g!!.addColorStop(0.35, "#594a18")
v1517_g!!.addColorStop(1.0, "#8c7527")
v1505_e.fillStyle = v1517_g!!
v1505_e.fillRect(0.0, v1509_t, v1506_a, (v1507_o - v1509_t))
v1505_e.lineWidth = 1.0
run {
v1518_m = -12.0
L225@ while ((v1518_m <= 12.0)) {
v1519_d = (v1518_m * 0.14)
v1520_y = jsCall(v1515_s, v1519_d, -1.0, 0.04)
v1521_c = jsCall(v1515_s, v1519_d, -1.0, 1.3)
v1505_e.strokeStyle = "rgba(42, 35, 10, 0.55)"
v1505_e.beginPath()
v1505_e.moveTo(num(jsGet(v1520_y, "x")), num(jsGet(v1520_y, "y")))
v1505_e.lineTo(num(jsGet(v1521_c, "x")), num(jsGet(v1521_c, "y")))
v1505_e.stroke()
v1522_S = jsCall(v1515_s, v1519_d, 1.0, 0.04)
v1523_v = jsCall(v1515_s, v1519_d, 1.0, 1.3)
v1524_T = v1505_e.createLinearGradient(num(jsGet(v1522_S, "x")), num(jsGet(v1522_S, "y")), num(jsGet(v1523_v, "x")), num(jsGet(v1523_v, "y")))
v1524_T!!.addColorStop(0.0, "rgba(35, 28, 6, 0.32)")
v1524_T!!.addColorStop(0.58, "rgba(35, 28, 6, 0.24)")
v1524_T!!.addColorStop(0.82, "rgba(35, 28, 6, 0.09)")
v1524_T!!.addColorStop(1.0, "rgba(35, 28, 6, 0)")
v1505_e.strokeStyle = v1524_T!!
v1505_e.beginPath()
v1505_e.moveTo(num(jsGet(v1522_S, "x")), num(jsGet(v1522_S, "y")))
v1505_e.lineTo(num(jsGet(v1523_v, "x")), num(jsGet(v1523_v, "y")))
v1505_e.stroke()
v1518_m++
}
}
v1525_b = 16.0
v1526_h = (v1511_u * 0.42)
v1527_p = (v1526_h % 1.0)
v1528_w = kotlin.math.floor(v1526_h)
run {
v1529_A = 0.0
L226@ while ((v1529_A < v1525_b)) {
L226c@ do {
v1530_C = ((v1529_A + v1527_p) / v1525_b)
v1531_E = (((v1529_A + 1.0) + v1527_p) / v1525_b)
v1532_k = Math.pow(v1530_C, 1.95)
v1533_R = Math.pow(v1531_E, 1.95)
if ((v1533_R < 0.02)) {
break@L226c
}
v1534_F = (v1509_t - (v1532_k * v1509_t))
v1535_O = (v1509_t + (v1532_k * (v1507_o - v1509_t)))
v1536_P = jsMin(1.0, (v1530_C * 3.2))
v1505_e.lineWidth = (0.8 + (v1532_k * 2.2))
v1505_e.strokeStyle = (("rgba(40, 32, 8, " + jsToFixed((v1536_P * 0.65), 2.0)) + ")")
v1505_e.beginPath()
v1505_e.moveTo(0.0, v1534_F)
v1505_e.lineTo(v1506_a, v1534_F)
v1505_e.stroke()
v1537_I = rrBackroomsNearFade(v1530_C, 0.7, 1.06)
if ((v1537_I > 0.01)) {
v1505_e.strokeStyle = (("rgba(40, 32, 8, " + jsToFixed(((v1536_P * 0.65) * v1537_I), 3.0)) + ")")
v1505_e.beginPath()
v1505_e.moveTo(0.0, v1535_O)
v1505_e.lineTo(v1506_a, v1535_O)
v1505_e.stroke()
}
v1538_U_ = (v1529_A - v1528_w)
if ((((((v1538_U_ % 2.0) + 2.0) % 2.0) == 0.0) && (v1530_C > 0.06))) {
v1539_B = ((((v1538_U_ * 7.0) % 5.0) == 0.0) && (kotlin.math.sin(((v1510_l * 42.0) + v1538_U_)) > 0.35))
v1540_L = ((if (v1539_B) 0.22 else (0.88 + (v1512_f * 0.12))) * v1536_P)
js_forEach(jsArr(-0.42, 0.0, 0.42)) L227@{ v1555_J: Any?, _, _ ->
var v1556_de: Any? = null
var v1557_ee: Any? = null
var v1558_se: Any? = null
var v1559_x: Any? = null
var v1560_N: Any? = null
var v1561_ae: Any? = null
var v1562_j: Any? = null
var v1563_oe: Any? = null
var v1564_Y: Any? = null
var v1565_ne: Any? = null
var v1566_he: Any? = null
var v1567_ve: Grad? = null

v1556_de = jsCall(v1515_s, (num(v1555_J) - 0.08), -1.0, v1532_k)
v1557_ee = jsCall(v1515_s, (num(v1555_J) + 0.08), -1.0, v1532_k)
v1558_se = jsCall(v1515_s, (num(v1555_J) - 0.08), -1.0, v1533_R)
v1559_x = jsCall(v1515_s, (num(v1555_J) + 0.08), -1.0, v1533_R)
v1560_N = jsCall(v1515_s, (num(v1555_J) - 0.065), -1.04, v1532_k)
v1561_ae = jsCall(v1515_s, (num(v1555_J) + 0.065), -1.04, v1532_k)
v1562_j = jsCall(v1515_s, (num(v1555_J) - 0.065), -1.04, v1533_R)
v1563_oe = jsCall(v1515_s, (num(v1555_J) + 0.065), -1.04, v1533_R)
drawQuad3D(v1505_e, v1556_de, v1557_ee, v1559_x, v1558_se, "#3a3212", "#211c08", (1.0 + (v1532_k * 1.5)))
drawQuad3D(v1505_e, v1560_N, v1561_ae, v1563_oe, v1562_j, (("rgba(255, 253, 215, " + jsToFixed(v1540_L, 2.0)) + ")"), "#8a7a36", 1.0)
if (((jsEq(v1555_J, 0.0) && !v1539_B) && (v1532_k > 0.15))) {
v1564_Y = jsCall(v1515_s, 0.0, -1.0, ((v1532_k + v1533_R) * 0.5))
v1565_ne = jsCall(v1515_s, -0.28, 1.0, ((v1532_k + v1533_R) * 0.5))
v1566_he = jsCall(v1515_s, 0.28, 1.0, ((v1532_k + v1533_R) * 0.5))
v1567_ve = v1505_e.createLinearGradient(0.0, num(jsGet(v1564_Y, "y")), 0.0, num(jsGet(v1565_ne, "y")))
v1567_ve!!.addColorStop(0.0, (("rgba(255, 248, 185, " + jsToFixed((0.14 * v1536_P), 3.0)) + ")"))
v1567_ve!!.addColorStop(1.0, "rgba(255, 248, 185, 0)")
drawQuad3D(v1505_e, v1556_de, v1557_ee, v1566_he, v1565_ne, v1567_ve!!, null, 0.0)
}
null
}
}
} while (false)
v1529_A++
}
}
v1541_G = 8.0
v1542_U = (v1511_u * 0.36)
v1543_V = (v1542_U % 1.0)
v1544_D = kotlin.math.floor(v1542_U)
run {
v1545_K = 0.0
L228@ while ((v1545_K < v1541_G)) {
L228c@ do {
v1546_S_ = ((v1545_K + v1543_V) / v1541_G)
v1547_z = (((v1545_K + 0.58) + v1543_V) / v1541_G)
if ((v1546_S_ < 0.04)) {
break@L228c
}
v1548_X = Math.pow(v1546_S_, 1.95)
v1549_q = Math.pow(v1547_z, 1.95)
v1550_Q = (jsMin(1.0, (v1546_S_ * 3.8)) * rrBackroomsNearFade(v1546_S_, 0.78, 1.03))
if ((v1550_Q <= 0.02)) {
break@L228c
}
v1551_W = (v1545_K - v1544_D)
v1552_te = (((v1551_W % 3.0) + 3.0) % 3.0)
v1505_e.save()
v1505_e.globalAlpha = v1550_Q
js_forEach(jsArr(-1.0, 1.0)) L229@{ v1568_J: Any?, _, _ ->
var v1569_de: Double = 0.0
var v1570_ee: Double = 0.0
var v1571_se: Double = 0.0
var v1572_x: Any? = null
var v1573_N: Any? = null
var v1574_ae: Any? = null
var v1575_j: Any? = null
var v1576_oe: Any? = null
var v1577_Y: Any? = null
var v1578_ne: Any? = null
var v1579_he: Any? = null
var v1580_ve: Grad? = null
var v1581_Ce: Any? = null
var v1582_ye: Any? = null
var v1583_Z: Double = 0.0
var v1584_H: Double = 0.0
var v1585_fe: Any? = null
var v1586_we: Any? = null
var v1587_Se: Any? = null
var v1588_ke: Any? = null
var v1589_Ee: Any? = null
var v1590_Pe: Any? = null
var v1591_Ie: Grad? = null
var v1592_Te: Double = 0.0
var v1593_ue: Double = 0.0
var v1594_me: Any? = null
var v1595_re: Any? = null
var v1596_ge: Any? = null
var v1597_pe: Any? = null
var v1598_Me: Double = 0.0
var v1599_ce: Any? = null
var v1600_be: Any? = null

v1569_de = (if (((v1552_te == 0.0) && jsEq(v1568_J, -1.0))) 0.16 else (if (((v1552_te == 1.0) && jsEq(v1568_J, 1.0))) 0.16 else 0.25))
v1570_ee = (num(v1568_J) * v1569_de)
v1571_se = (num(v1568_J) * (v1569_de + 0.52))
v1572_x = jsCall(v1515_s, v1570_ee, -1.0, v1548_X)
v1573_N = jsCall(v1515_s, v1571_se, -1.0, v1548_X)
v1574_ae = jsCall(v1515_s, v1570_ee, 1.0, v1548_X)
v1575_j = jsCall(v1515_s, v1571_se, 1.0, v1548_X)
v1576_oe = jsCall(v1515_s, v1570_ee, -1.0, v1549_q)
v1577_Y = jsCall(v1515_s, v1571_se, -1.0, v1549_q)
v1578_ne = jsCall(v1515_s, v1570_ee, 1.0, v1549_q)
v1579_he = jsCall(v1515_s, v1571_se, 1.0, v1549_q)
v1580_ve = v1505_e.createLinearGradient(num(jsGet(v1572_x, "x")), 0.0, num(jsGet(v1576_oe, "x")), 0.0)
v1580_ve!!.addColorStop(0.0, "#87762f")
v1580_ve!!.addColorStop(1.0, "#b39d42")
drawQuad3D(v1505_e, v1572_x, v1576_oe, v1578_ne, v1574_ae, v1580_ve!!, "#3d3310", 1.4)
v1581_Ce = jsCall(v1515_s, v1570_ee, 0.88, v1548_X)
v1582_ye = jsCall(v1515_s, v1570_ee, 0.88, v1549_q)
drawQuad3D(v1505_e, v1581_Ce, v1582_ye, v1578_ne, v1574_ae, "#33290a", "#1c1604", 1.0)
if (!(v1552_te == 0.0)) {
v1583_Z = (v1548_X + ((v1549_q - v1548_X) * 0.22))
v1584_H = (v1548_X + ((v1549_q - v1548_X) * 0.78))
v1585_fe = jsCall(v1515_s, v1570_ee, -0.45, v1583_Z)
v1586_we = jsCall(v1515_s, v1570_ee, -0.45, v1584_H)
v1587_Se = jsCall(v1515_s, v1570_ee, 1.0, v1583_Z)
v1588_ke = jsCall(v1515_s, v1570_ee, 1.0, v1584_H)
v1589_Ee = jsCall(v1515_s, (v1570_ee + (num(v1568_J) * 0.08)), -0.45, v1583_Z)
v1590_Pe = jsCall(v1515_s, (v1570_ee + (num(v1568_J) * 0.08)), 1.0, v1583_Z)
drawQuad3D(v1505_e, v1585_fe, v1586_we, v1588_ke, v1587_Se, "#0e0b02", "#2e2508", 1.2)
drawQuad3D(v1505_e, v1585_fe, v1589_Ee, v1590_Pe, v1587_Se, "#473c14", "#1f1905", 1.0)
}
v1591_Ie = v1505_e.createLinearGradient(num(jsGet(v1576_oe, "x")), num(jsGet(v1576_oe, "y")), num(jsGet(v1578_ne, "x")), num(jsGet(v1578_ne, "y")))
v1591_Ie!!.addColorStop(0.0, "#ab963e")
v1591_Ie!!.addColorStop(0.5, "#d1b952")
v1591_Ie!!.addColorStop(1.0, "#9c8835")
drawQuad3D(v1505_e, v1576_oe, v1577_Y, v1579_he, v1578_ne, v1591_Ie!!, "#423812", 1.6)
v1505_e.strokeStyle = "rgba(74, 62, 18, 0.36)"
v1505_e.lineWidth = jsMax(1.0, (v1549_q * 3.0))
run {
v1592_Te = 1.0
L230@ while ((v1592_Te <= 5.0)) {
v1593_ue = (v1570_ee + ((v1571_se - v1570_ee) * (v1592_Te / 6.0)))
v1594_me = jsCall(v1515_s, v1593_ue, -1.0, v1549_q)
v1595_re = jsCall(v1515_s, v1593_ue, 0.88, v1549_q)
v1505_e.beginPath()
v1505_e.moveTo(num(jsGet(v1594_me, "x")), num(jsGet(v1594_me, "y")))
v1505_e.lineTo(num(jsGet(v1595_re, "x")), num(jsGet(v1595_re, "y")))
v1505_e.stroke()
v1592_Te++
}
}
v1596_ge = jsCall(v1515_s, v1570_ee, 0.88, v1549_q)
v1597_pe = jsCall(v1515_s, v1571_se, 0.88, v1549_q)
drawQuad3D(v1505_e, v1596_ge, v1597_pe, v1579_he, v1578_ne, "#3b300d", "#1c1604", 1.0)
if (((v1552_te == 0.0) && (v1549_q > 0.25))) {
v1598_Me = (v1570_ee + (num(v1568_J) * 0.06))
v1599_ce = jsCall(v1515_s, v1598_Me, 0.62, v1549_q)
v1600_be = jsCall(v1515_s, (v1598_Me + (num(v1568_J) * 0.022)), 0.72, v1549_q)
v1505_e.fillStyle = "#dfd399"
v1505_e.strokeStyle = "#3b300d"
v1505_e.lineWidth = 1.0
v1505_e.fillRect(jsMin(num(jsGet(v1599_ce, "x")), num(jsGet(v1600_be, "x"))), num(jsGet(v1599_ce, "y")), kotlin.math.abs((num(jsGet(v1600_be, "x")) - num(jsGet(v1599_ce, "x")))), (num(jsGet(v1600_be, "y")) - num(jsGet(v1599_ce, "y"))))
v1505_e.strokeRect(jsMin(num(jsGet(v1599_ce, "x")), num(jsGet(v1600_be, "x"))), num(jsGet(v1599_ce, "y")), kotlin.math.abs((num(jsGet(v1600_be, "x")) - num(jsGet(v1599_ce, "x")))), (num(jsGet(v1600_be, "y")) - num(jsGet(v1599_ce, "y"))))
}
null
}
v1505_e.restore()
} while (false)
v1545_K++
}
}
v1553_le = (jsMin(v1506_a, v1507_o) * 0.28)
v1554_ie = v1505_e.createRadialGradient(v1508_r, v1509_t, 4.0, v1508_r, v1509_t, v1553_le)
v1554_ie!!.addColorStop(0.0, "rgba(20, 16, 4, 0.95)")
v1554_ie!!.addColorStop(0.55, "rgba(48, 39, 10, 0.55)")
v1554_ie!!.addColorStop(1.0, "rgba(48, 39, 10, 0)")
v1505_e.fillStyle = v1554_ie!!
v1505_e.fillRect((v1508_r - v1553_le), (v1509_t - v1553_le), (v1553_le * 2.0), (v1553_le * 2.0))
}

fun renderScene10_BackroomsHalls(v1601_e: Ctx, v1602_a: Double, v1603_o: Double, v1604_r: Double, v1605_t: Double, v1606_l: Double, v1607_u: Double, v1608_f: Double, v1609_M: Double, v1610_n: Any?): Unit {

var v1611_s: Any? = null
var v1612_i: Grad? = null
var v1613_g: Grad? = null
var v1614_m: Grad? = null
var v1615_d: Double = 0.0
var v1616_y: Double = 0.0
var v1617_c: Double = 0.0
var v1618_S: Double = 0.0
var v1619_v: Double = 0.0
var v1620_T: Double = 0.0
var v1621_b: Double = 0.0
var v1622_h: Double = 0.0
var v1623_p: Double = 0.0
var v1624_w: Double = 0.0
var v1625_A: Double = 0.0
var v1626_C: Double = 0.0
var v1627_E: Double = 0.0
var v1628_k: Double = 0.0
var v1629_R: Double = 0.0
var v1630_F: Double = 0.0
var v1631_O: Double = 0.0
var v1632_P: Any? = null
var v1633_I: Any? = null
var v1634_U_: Any? = null
var v1635_B: Any? = null
var v1636_L: Any? = null
var v1637_G: Any? = null
var v1638_U: Boolean = false
var v1639_V: Any? = null
var v1640_D: Any? = null
var v1641_K: Double = 0.0
var v1642_S_: Grad? = null
v1611_s = createRoomProjector(v1602_a, v1603_o, v1604_r, v1605_t)
v1612_i = v1601_e.createLinearGradient(0.0, 0.0, 0.0, v1605_t)
v1612_i!!.addColorStop(0.0, "#2b281c")
v1612_i!!.addColorStop(0.7, "#17150e")
v1612_i!!.addColorStop(1.0, "#080704")
v1601_e.fillStyle = v1612_i!!
v1601_e.fillRect(0.0, 0.0, v1602_a, v1605_t)
v1613_g = v1601_e.createLinearGradient(0.0, v1605_t, 0.0, v1603_o)
v1613_g!!.addColorStop(0.0, "#080704")
v1613_g!!.addColorStop(0.4, "#1f1c12")
v1613_g!!.addColorStop(1.0, "#36301f")
v1601_e.fillStyle = v1613_g!!
v1601_e.fillRect(0.0, v1605_t, v1602_a, (v1603_o - v1605_t))
js_forEach(jsArr(-0.32, -0.26, 0.26, 0.32)) L231@{ v1643_z: Any?, v1644_X: Double, _ ->
var v1645_q: Any? = null
var v1646_Q: Any? = null

v1645_q = jsCall(v1611_s, v1643_z, -0.92, 0.04)
v1646_Q = jsCall(v1611_s, v1643_z, -0.92, 1.25)
v1601_e.strokeStyle = (if (((v1644_X % 2.0) == 0.0)) "#5c5338" else "#3d3724")
v1601_e.lineWidth = 4.0
v1601_e.beginPath()
v1601_e.moveTo(num(jsGet(v1645_q, "x")), num(jsGet(v1645_q, "y")))
v1601_e.lineTo(num(jsGet(v1646_Q, "x")), num(jsGet(v1646_Q, "y")))
v1601_e.stroke()
null
}
v1614_m = v1601_e.createRadialGradient(v1604_r, (v1605_t + ((v1603_o - v1605_t) * 0.35)), 8.0, v1604_r, (v1605_t + ((v1603_o - v1605_t) * 0.5)), (v1602_a * 0.35))
v1614_m!!.addColorStop(0.0, (("rgba(255, 240, 150, " + jsToFixed((0.22 + (v1608_f * 0.14)), 2.0)) + ")"))
v1614_m!!.addColorStop(0.5, "rgba(180, 150, 60, 0.08)")
v1614_m!!.addColorStop(1.0, "transparent")
v1601_e.fillStyle = v1614_m!!
v1601_e.fillRect(0.0, v1605_t, v1602_a, (v1603_o - v1605_t))
v1615_d = 14.0
v1616_y = ((v1607_u * 0.5) % 1.0)
run {
v1617_c = 0.0
L232@ while ((v1617_c < v1615_d)) {
v1618_S = ((v1617_c + v1616_y) / v1615_d)
v1619_v = Math.pow(v1618_S, 1.9)
v1620_T = (v1605_t + (v1619_v * (v1603_o - v1605_t)))
v1601_e.strokeStyle = (("rgba(15, 12, 6, " + jsToFixed(jsMin(0.8, (v1618_S * 2.0)), 2.0)) + ")")
v1601_e.lineWidth = (1.0 + (v1619_v * 2.0))
v1601_e.beginPath()
v1601_e.moveTo(0.0, v1620_T)
v1601_e.lineTo(v1602_a, v1620_T)
v1601_e.stroke()
v1617_c++
}
}
v1621_b = 8.0
v1622_h = (v1607_u * 0.4)
v1623_p = (v1622_h % 1.0)
v1624_w = kotlin.math.floor(v1622_h)
run {
v1625_A = 0.0
L233@ while ((v1625_A < v1621_b)) {
L233c@ do {
v1626_C = ((v1625_A + v1623_p) / v1621_b)
v1627_E = (((v1625_A + 0.52) + v1623_p) / v1621_b)
if ((v1626_C < 0.03)) {
break@L233c
}
v1628_k = Math.pow(v1626_C, 1.92)
v1629_R = Math.pow(v1627_E, 1.92)
v1630_F = (jsMin(1.0, (v1626_C * 3.8)) * rrBackroomsNearFade(v1626_C, 0.78, 1.03))
if ((v1630_F <= 0.02)) {
break@L233c
}
v1631_O = (v1625_A - v1624_w)
v1601_e.save()
v1601_e.globalAlpha = v1630_F
v1632_P = jsCall(v1611_s, -0.55, -0.78, v1628_k)
v1633_I = jsCall(v1611_s, 0.55, -0.78, v1628_k)
v1634_U_ = jsCall(v1611_s, -0.55, -0.78, v1629_R)
v1635_B = jsCall(v1611_s, 0.55, -0.78, v1629_R)
v1636_L = jsCall(v1611_s, -0.55, -1.0, v1629_R)
v1637_G = jsCall(v1611_s, 0.55, -1.0, v1629_R)
drawQuad3D(v1601_e, v1632_P, v1633_I, v1635_B, v1634_U_, "#211e14", "#12100a", 1.2)
drawQuad3D(v1601_e, v1636_L, v1637_G, v1635_B, v1634_U_, "#383322", "#17140c", 1.4)
v1638_U = ((((v1631_O * 3.0) % 5.0) == 0.0) && (kotlin.math.sin(((v1606_l * 55.0) + v1631_O)) > 0.15))
v1639_V = jsCall(v1611_s, -0.18, -0.77, v1629_R)
v1640_D = jsCall(v1611_s, 0.18, -0.77, v1629_R)
v1601_e.strokeStyle = (if (v1638_U) "#4f4622" else "#fff6b3")
v1601_e.lineWidth = jsMax(2.0, (v1629_R * 8.0))
v1601_e.beginPath()
v1601_e.moveTo(num(jsGet(v1639_V, "x")), num(jsGet(v1639_V, "y")))
v1601_e.lineTo(num(jsGet(v1640_D, "x")), num(jsGet(v1640_D, "y")))
v1601_e.stroke()
js_forEach(jsArr(-1.0, 1.0)) L234@{ v1647_z: Any?, _, _ ->
var v1648_X: Double = 0.0
var v1649_q: Double = 0.0
var v1650_Q: Double = 0.0
var v1651_W: Any? = null
var v1652_te: Any? = null
var v1653_le: Any? = null
var v1654_ie: Any? = null
var v1655_J: Any? = null
var v1656_de: Any? = null
var v1657_ee: Any? = null
var v1658_se: Any? = null
var v1659_x: Any? = null
var v1660_N: Any? = null
var v1661_ae: Any? = null
var v1662_j: Any? = null
var v1663_oe: Grad? = null
var v1664_Y: Any? = null
var v1665_ne: Any? = null
var v1666_he: Any? = null
var v1667_ve: Any? = null
var v1668_Ce: Any? = null
var v1669_ye: Any? = null
var v1670_Z: Any? = null
var v1671_H: Any? = null
var v1672_fe: Any? = null

v1648_X = (num(v1647_z) * 0.24)
v1649_q = (num(v1647_z) * 0.38)
v1650_Q = (num(v1647_z) * 0.78)
v1651_W = jsCall(v1611_s, v1649_q, -1.0, v1628_k)
v1652_te = jsCall(v1611_s, v1650_Q, -1.0, v1628_k)
v1653_le = jsCall(v1611_s, v1649_q, 1.0, v1628_k)
v1654_ie = jsCall(v1611_s, v1650_Q, 1.0, v1628_k)
drawQuad3D(v1601_e, v1651_W, v1652_te, v1654_ie, v1653_le, "#1c1910", "#0d0b07", 1.0)
if (((((v1631_O + (if ((num(v1647_z) > 0.0)) 1.0 else 0.0)) % 4.0) == 0.0) && (v1628_k > 0.12))) {
v1655_J = jsCall(v1611_s, (num(v1647_z) * 0.44), -0.38, v1628_k)
v1656_de = jsCall(v1611_s, (num(v1647_z) * 0.52), -0.3, v1628_k)
v1601_e.fillStyle = "#ff1e00"
v1601_e.fillRect(jsMin(num(jsGet(v1655_J, "x")), num(jsGet(v1656_de, "x"))), num(jsGet(v1655_J, "y")), kotlin.math.abs((num(jsGet(v1656_de, "x")) - num(jsGet(v1655_J, "x")))), (num(jsGet(v1656_de, "y")) - num(jsGet(v1655_J, "y"))))
}
v1657_ee = jsCall(v1611_s, v1648_X, -1.0, v1628_k)
v1658_se = jsCall(v1611_s, v1648_X, 1.0, v1628_k)
v1659_x = jsCall(v1611_s, v1648_X, -1.0, v1629_R)
v1660_N = jsCall(v1611_s, v1648_X, 1.0, v1629_R)
v1661_ae = jsCall(v1611_s, v1649_q, -1.0, v1629_R)
v1662_j = jsCall(v1611_s, v1649_q, 1.0, v1629_R)
drawQuad3D(v1601_e, v1657_ee, v1659_x, v1660_N, v1658_se, "#332e1e", "#14120b", 1.3)
v1663_oe = v1601_e.createLinearGradient(num(jsGet(v1659_x, "x")), num(jsGet(v1659_x, "y")), num(jsGet(v1661_ae, "x")), num(jsGet(v1661_ae, "y")))
v1663_oe!!.addColorStop(0.0, "#595035")
v1663_oe!!.addColorStop(1.0, "#3d3724")
drawQuad3D(v1601_e, v1659_x, v1661_ae, v1662_j, v1660_N, v1663_oe!!, "#17140c", 1.6)
v1664_Y = jsCall(v1611_s, v1648_X, 0.2, v1628_k)
v1665_ne = jsCall(v1611_s, v1648_X, 0.34, v1628_k)
v1666_he = jsCall(v1611_s, v1648_X, 0.2, v1629_R)
v1667_ve = jsCall(v1611_s, v1648_X, 0.34, v1629_R)
v1668_Ce = jsCall(v1611_s, v1649_q, 0.2, v1629_R)
v1669_ye = jsCall(v1611_s, v1649_q, 0.34, v1629_R)
drawQuad3D(v1601_e, v1664_Y, v1666_he, v1667_ve, v1665_ne, "#9e811b", "#1c1808", 1.0)
drawQuad3D(v1601_e, v1666_he, v1668_Ce, v1669_ye, v1667_ve, "#d4af37", "#1c1808", 1.0)
v1670_Z = jsCall(v1611_s, v1648_X, 0.86, v1628_k)
v1671_H = jsCall(v1611_s, v1648_X, 0.86, v1629_R)
v1672_fe = jsCall(v1611_s, v1649_q, 0.86, v1629_R)
drawQuad3D(v1601_e, v1670_Z, v1671_H, v1660_N, v1658_se, "#1c1910", "#0d0b07", 1.0)
drawQuad3D(v1601_e, v1671_H, v1672_fe, v1662_j, v1660_N, "#262216", "#0d0b07", 1.0)
null
}
v1601_e.restore()
} while (false)
v1625_A++
}
}
v1641_K = (jsMin(v1602_a, v1603_o) * 0.26)
v1642_S_ = v1601_e.createRadialGradient(v1604_r, v1605_t, 2.0, v1604_r, v1605_t, v1641_K)
v1642_S_!!.addColorStop(0.0, "rgba(8, 7, 4, 0.98)")
v1642_S_!!.addColorStop(0.6, "rgba(20, 18, 10, 0.65)")
v1642_S_!!.addColorStop(1.0, "rgba(20, 18, 10, 0)")
v1601_e.fillStyle = v1642_S_!!
v1601_e.fillRect((v1604_r - v1641_K), (v1605_t - v1641_K), (v1641_K * 2.0), (v1641_K * 2.0))
}

fun renderScene11_PoolroomsSpa(v1673_e: Ctx, v1674_a: Double, v1675_o: Double, v1676_r: Double, v1677_t: Double, v1678_l: Double, v1679_u: Double, v1680_f: Double, v1681_M: Double, v1682_n: Any?): Unit {

var v1683_s: Any? = null
var v1684_i: Grad? = null
var v1685_g: Grad? = null
var v1686_m: Double = 0.0
var v1687_d: Double = 0.0
var v1688_y: Any? = null
var v1689_c: Any? = null
var v1690_S: Any? = null
var v1691_v: Any? = null
var v1692_T: Double = 0.0
var v1693_b: Double = 0.0
var v1694_h: Double = 0.0
var v1695_p: Double = 0.0
var v1696_w: Double = 0.0
var v1697_A: Double = 0.0
var v1698_C: Double = 0.0
var v1699_E: Double = 0.0
var v1700_k: Double = 0.0
var v1701_R: Double = 0.0
var v1702_F: Double = 0.0
var v1703_O: Double = 0.0
var v1704_P: Double = 0.0
var v1705_I: Double = 0.0
var v1706_U_: Double = 0.0
var v1707_B: Double = 0.0
var v1708_L: Double = 0.0
var v1709_G: Double = 0.0
var v1710_U: Double = 0.0
var v1711_V: Double = 0.0
var v1712_D: Double = 0.0
var v1713_K: Double = 0.0
var v1714_S_: Double = 0.0
var v1715_z: Any? = null
var v1716_X: Any? = null
var v1717_q: Any? = null
var v1718_Q: Any? = null
var v1719_W: Grad? = null
var v1720_te: Double = 0.0
var v1721_le: Grad? = null
v1683_s = createRoomProjector(v1674_a, v1675_o, v1676_r, v1677_t)
v1684_i = v1673_e.createLinearGradient(0.0, 0.0, 0.0, v1677_t)
v1684_i!!.addColorStop(0.0, "#c7dadd")
v1684_i!!.addColorStop(0.55, "#a9c9cd")
v1684_i!!.addColorStop(1.0, "#568c95")
v1673_e.fillStyle = v1684_i!!
v1673_e.fillRect(0.0, 0.0, v1674_a, v1677_t)
v1685_g = v1673_e.createLinearGradient(0.0, v1677_t, 0.0, v1675_o)
v1685_g!!.addColorStop(0.0, "#006a78")
v1685_g!!.addColorStop(0.35, "#008e8b")
v1685_g!!.addColorStop(0.75, "#00717d")
v1685_g!!.addColorStop(1.0, "#004355")
v1673_e.fillStyle = v1685_g!!
v1673_e.fillRect(0.0, v1677_t, v1674_a, (v1675_o - v1677_t))
run {
v1686_m = -14.0
L235@ while ((v1686_m <= 14.0)) {
v1687_d = (v1686_m * 0.11)
v1688_y = jsCall(v1683_s, v1687_d, -1.0, 0.04)
v1689_c = jsCall(v1683_s, v1687_d, -1.0, 1.3)
v1673_e.strokeStyle = "rgba(73, 132, 142, 0.30)"
v1673_e.lineWidth = 1.0
v1673_e.beginPath()
v1673_e.moveTo(num(jsGet(v1688_y, "x")), num(jsGet(v1688_y, "y")))
v1673_e.lineTo(num(jsGet(v1689_c, "x")), num(jsGet(v1689_c, "y")))
v1673_e.stroke()
v1690_S = jsCall(v1683_s, v1687_d, 1.0, 0.04)
v1691_v = jsCall(v1683_s, v1687_d, 1.0, 1.3)
v1673_e.strokeStyle = "rgba(154, 205, 211, 0.19)"
v1673_e.beginPath()
v1673_e.moveTo(num(jsGet(v1690_S, "x")), num(jsGet(v1690_S, "y")))
v1673_e.lineTo(num(jsGet(v1691_v, "x")), num(jsGet(v1691_v, "y")))
v1673_e.stroke()
v1686_m++
}
}
v1692_T = 18.0
v1693_b = (v1679_u * 0.44)
v1694_h = (v1693_b % 1.0)
run {
v1695_p = 0.0
L236@ while ((v1695_p < v1692_T)) {
v1696_w = ((v1695_p + v1694_h) / v1692_T)
v1697_A = Math.pow(v1696_w, 1.88)
v1698_C = (v1677_t - (v1697_A * v1677_t))
v1699_E = (v1677_t + (v1697_A * (v1675_o - v1677_t)))
v1673_e.strokeStyle = (("rgba(66, 124, 132, " + jsToFixed((0.12 + (v1697_A * 0.24)), 2.0)) + ")")
v1673_e.lineWidth = (0.8 + (v1697_A * 1.5))
v1673_e.beginPath()
v1673_e.moveTo(0.0, v1698_C)
v1673_e.lineTo(v1674_a, v1698_C)
v1673_e.stroke()
v1673_e.strokeStyle = (("rgba(136, 191, 196, " + jsToFixed((0.09 + (v1697_A * 0.2)), 2.0)) + ")")
v1673_e.lineWidth = (0.8 + (v1697_A * 1.4))
v1673_e.beginPath()
run {
v1700_k = 0.0
L237@ while ((v1700_k <= 36.0)) {
v1701_R = (v1700_k / 36.0)
v1702_F = (v1701_R * v1674_a)
v1703_O = (((kotlin.math.sin((((v1701_R * 16.0) + (v1678_l * 2.6)) - (v1695_p * 0.65))) * (2.5 + (v1680_f * 7.0))) * v1697_A) + ((kotlin.math.cos((((v1701_R * 9.0) - (v1678_l * 1.8)) + (v1695_p * 0.4))) * 2.0) * v1697_A))
if ((v1700_k == 0.0)) {
v1673_e.moveTo(v1702_F, (v1699_E + v1703_O))
} else {
v1673_e.lineTo(v1702_F, (v1699_E + v1703_O))
}
v1700_k++
}
}
v1673_e.stroke()
v1695_p++
}
}
v1704_P = 7.0
v1705_I = (v1679_u * 0.36)
v1706_U_ = (v1705_I % 1.0)
v1707_B = kotlin.math.floor(v1705_I)
run {
v1708_L = 0.0
L238@ while ((v1708_L < v1704_P)) {
L238c@ do {
v1709_G = ((v1708_L + v1706_U_) / v1704_P)
v1710_U = (((v1708_L + 0.56) + v1706_U_) / v1704_P)
if ((v1709_G < 0.04)) {
break@L238c
}
v1711_V = Math.pow(v1709_G, 1.9)
v1712_D = Math.pow(v1710_U, 1.9)
v1713_K = (jsMin(1.0, (v1709_G * 3.6)) * rrBackroomsNearFade(v1709_G, 0.78, 1.03))
if ((v1713_K <= 0.02)) {
break@L238c
}
v1714_S_ = (v1708_L - v1707_B)
v1673_e.save()
v1673_e.globalAlpha = v1713_K
if ((((((v1714_S_ % 2.0) + 2.0) % 2.0) == 0.0) && (v1711_V > 0.1))) {
v1715_z = jsCall(v1683_s, -0.34, -0.65, v1711_V)
v1716_X = jsCall(v1683_s, -0.34, -0.25, v1712_D)
v1717_q = jsCall(v1683_s, 0.12, 0.85, v1711_V)
v1718_Q = jsCall(v1683_s, 0.18, 0.85, v1712_D)
v1719_W = v1673_e.createLinearGradient(num(jsGet(v1715_z, "x")), num(jsGet(v1715_z, "y")), num(jsGet(v1717_q, "x")), num(jsGet(v1717_q, "y")))
v1719_W!!.addColorStop(0.0, (("rgba(208, 235, 237, " + jsToFixed((0.065 * v1713_K), 3.0)) + ")"))
v1719_W!!.addColorStop(1.0, "rgba(180, 255, 255, 0)")
drawQuad3D(v1673_e, v1715_z, v1716_X, v1718_Q, v1717_q, v1719_W!!, null, 0.0)
}
js_forEach(jsArr(-1.0, 1.0)) L239@{ v1722_ie: Any?, _, _ ->
var v1723_J: Double = 0.0
var v1724_de: Double = 0.0
var v1725_ee: Double = 0.0
var v1726_se: Any? = null
var v1727_x: Any? = null
var v1728_N: Any? = null
var v1729_ae: Any? = null
var v1730_j: Any? = null
var v1731_oe: Any? = null
var v1732_Y: Any? = null
var v1733_ne: Any? = null
var v1734_he: Any? = null
var v1735_ve: Any? = null
var v1736_Ce: Any? = null
var v1737_ye: Any? = null
var v1738_Z: Any? = null
var v1739_H: Grad? = null
var v1740_fe: Double = 0.0
var v1741_we: Double = 0.0
var v1742_Se: Double = 0.0
var v1743_ke: Any? = null
var v1744_Ee: Any? = null
var v1745_Pe: Grad? = null
var v1746_Ie: Double = 0.0
var v1747_Te: Double = 0.0
var v1748_ue: Any? = null
var v1749_me: Any? = null
var v1750_re: Double = 0.0
var v1751_ge: Double = 0.0
var v1752_pe: Any? = null
var v1753_Me: Any? = null
var v1754_ce: Double = 0.0
var v1755_be: Any? = null
var v1756_Ae: Any? = null
var v1757_Be: Any? = null
var v1758_Re: Any? = null
var v1759_Le: Double = 0.0
var v1760__e: Double = 0.0
var v1761_Fe: Any? = null
var v1762_Oe: Any? = null

v1723_J = (num(v1722_ie) * 0.25)
v1724_de = (num(v1722_ie) * 0.44)
v1725_ee = (num(v1722_ie) * 0.18)
v1726_se = jsCall(v1683_s, v1725_ee, 0.68, v1711_V)
v1727_x = jsCall(v1683_s, v1725_ee, 0.68, v1712_D)
v1728_N = jsCall(v1683_s, v1723_J, 0.68, v1711_V)
v1729_ae = jsCall(v1683_s, v1723_J, 0.68, v1712_D)
v1730_j = jsCall(v1683_s, v1725_ee, 1.0, v1711_V)
v1731_oe = jsCall(v1683_s, v1725_ee, 1.0, v1712_D)
v1732_Y = jsCall(v1683_s, v1723_J, 1.0, v1712_D)
drawQuad3D(v1673_e, v1726_se, v1728_N, v1729_ae, v1727_x, "#7faeb4", "#326f78", 1.1)
drawQuad3D(v1673_e, v1726_se, v1727_x, v1731_oe, v1730_j, "#6fa5ac", "#2b6670", 1.1)
drawQuad3D(v1673_e, v1727_x, v1729_ae, v1732_Y, v1731_oe, "#689ca4", "#2b6670", 1.2)
v1733_ne = jsCall(v1683_s, v1723_J, -1.0, v1711_V)
v1734_he = jsCall(v1683_s, v1723_J, 0.68, v1711_V)
v1735_ve = jsCall(v1683_s, v1723_J, -1.0, v1712_D)
v1736_Ce = jsCall(v1683_s, v1723_J, 0.68, v1712_D)
v1737_ye = jsCall(v1683_s, v1724_de, -1.0, v1712_D)
v1738_Z = jsCall(v1683_s, v1724_de, 0.68, v1712_D)
v1739_H = v1673_e.createLinearGradient(num(jsGet(v1733_ne, "x")), 0.0, num(jsGet(v1735_ve, "x")), 0.0)
v1739_H!!.addColorStop(0.0, "#90bcc2")
v1739_H!!.addColorStop(1.0, "#d1e4e6")
drawQuad3D(v1673_e, v1733_ne, v1735_ve, v1736_Ce, v1734_he, v1739_H!!, "#477f88", 1.3)
v1740_fe = 8.0
v1673_e.strokeStyle = "rgba(67, 126, 135, 0.32)"
v1673_e.lineWidth = 1.0
run {
v1741_we = 1.0
L240@ while ((v1741_we < v1740_fe)) {
v1742_Se = (-1.0 + ((v1741_we / v1740_fe) * 1.68))
v1743_ke = jsCall(v1683_s, v1723_J, v1742_Se, v1711_V)
v1744_Ee = jsCall(v1683_s, v1723_J, v1742_Se, v1712_D)
v1673_e.beginPath()
v1673_e.moveTo(num(jsGet(v1743_ke, "x")), num(jsGet(v1743_ke, "y")))
v1673_e.lineTo(num(jsGet(v1744_Ee, "x")), num(jsGet(v1744_Ee, "y")))
v1673_e.stroke()
v1741_we++
}
}
v1745_Pe = v1673_e.createLinearGradient(num(jsGet(v1735_ve, "x")), num(jsGet(v1735_ve, "y")), num(jsGet(v1736_Ce, "x")), num(jsGet(v1736_Ce, "y")))
v1745_Pe!!.addColorStop(0.0, "#c1d5d8")
v1745_Pe!!.addColorStop(0.7, "#a6c5c9")
v1745_Pe!!.addColorStop(1.0, "#729fa7")
drawQuad3D(v1673_e, v1735_ve, v1737_ye, v1738_Z, v1736_Ce, v1745_Pe!!, "#3e7b85", 1.5)
run {
v1746_Ie = 1.0
L241@ while ((v1746_Ie < v1740_fe)) {
v1747_Te = (-1.0 + ((v1746_Ie / v1740_fe) * 1.68))
v1748_ue = jsCall(v1683_s, v1723_J, v1747_Te, v1712_D)
v1749_me = jsCall(v1683_s, v1724_de, v1747_Te, v1712_D)
v1673_e.beginPath()
v1673_e.moveTo(num(jsGet(v1748_ue, "x")), num(jsGet(v1748_ue, "y")))
v1673_e.lineTo(num(jsGet(v1749_me, "x")), num(jsGet(v1749_me, "y")))
v1673_e.stroke()
v1746_Ie++
}
}
run {
v1750_re = 1.0
L242@ while ((v1750_re <= 3.0)) {
v1751_ge = (v1723_J + ((v1724_de - v1723_J) * (v1750_re / 4.0)))
v1752_pe = jsCall(v1683_s, v1751_ge, -1.0, v1712_D)
v1753_Me = jsCall(v1683_s, v1751_ge, 0.68, v1712_D)
v1673_e.beginPath()
v1673_e.moveTo(num(jsGet(v1752_pe, "x")), num(jsGet(v1752_pe, "y")))
v1673_e.lineTo(num(jsGet(v1753_Me, "x")), num(jsGet(v1753_Me, "y")))
v1673_e.stroke()
v1750_re++
}
}
if (((((((v1714_S_ % 3.0) + 3.0) % 3.0) == 0.0) && jsEq(v1722_ie, 1.0)) && (v1712_D > 0.16))) {
v1754_ce = ((v1711_V + v1712_D) * 0.5)
v1755_be = jsCall(v1683_s, (v1725_ee - 0.015), 0.25, v1754_ce)
v1756_Ae = jsCall(v1683_s, (v1725_ee - 0.015), 0.98, v1754_ce)
v1757_Be = jsCall(v1683_s, (v1725_ee - 0.045), 0.25, (v1754_ce + 0.03))
v1758_Re = jsCall(v1683_s, (v1725_ee - 0.045), 0.98, (v1754_ce + 0.03))
v1673_e.strokeStyle = "#9bbfc4"
v1673_e.lineWidth = jsMax(1.5, (v1712_D * 3.5))
v1673_e.beginPath()
v1673_e.moveTo(num(jsGet(v1755_be, "x")), num(jsGet(v1755_be, "y")))
v1673_e.lineTo(num(jsGet(v1756_Ae, "x")), num(jsGet(v1756_Ae, "y")))
v1673_e.moveTo(num(jsGet(v1757_Be, "x")), num(jsGet(v1757_Be, "y")))
v1673_e.lineTo(num(jsGet(v1758_Re, "x")), num(jsGet(v1758_Re, "y")))
run {
v1759_Le = 0.0
L243@ while ((v1759_Le < 4.0)) {
v1760__e = (0.42 + (v1759_Le * 0.14))
v1761_Fe = jsCall(v1683_s, (v1725_ee - 0.015), v1760__e, v1754_ce)
v1762_Oe = jsCall(v1683_s, (v1725_ee - 0.045), v1760__e, (v1754_ce + 0.03))
v1673_e.moveTo(num(jsGet(v1761_Fe, "x")), num(jsGet(v1761_Fe, "y")))
v1673_e.lineTo(num(jsGet(v1762_Oe, "x")), num(jsGet(v1762_Oe, "y")))
v1759_Le++
}
}
v1673_e.stroke()
}
null
}
v1673_e.restore()
} while (false)
v1708_L++
}
}
v1720_te = (jsMin(v1674_a, v1675_o) * 0.26)
v1721_le = v1673_e.createRadialGradient(v1676_r, v1677_t, 4.0, v1676_r, v1677_t, v1720_te)
v1721_le!!.addColorStop(0.0, "rgba(160, 207, 212, 0.27)")
v1721_le!!.addColorStop(0.5, "rgba(78, 151, 160, 0.14)")
v1721_le!!.addColorStop(1.0, "rgba(130, 230, 238, 0)")
v1673_e.fillStyle = v1721_le!!
v1673_e.fillRect((v1676_r - v1720_te), (v1677_t - v1720_te), (v1720_te * 2.0), (v1720_te * 2.0))
}

fun renderScene12_PoolroomsAbyss(v1763_e: Ctx, v1764_a: Double, v1765_o: Double, v1766_r: Double, v1767_t: Double, v1768_l: Double, v1769_u: Double, v1770_f: Double, v1771_M: Double, v1772_n: Any?): Unit {

var v1773_s: Any? = null
var v1774_i: Grad? = null
var v1775_g: Grad? = null
var v1776_m: Any? = null
var v1777_d: Any? = null
var v1778_y: Any? = null
var v1779_c: Any? = null
var v1780_S: Grad? = null
var v1781_v: Double = 0.0
var v1782_T: Double = 0.0
var v1783_b: Any? = null
var v1784_h: Any? = null
var v1785_p: Double = 0.0
var v1786_w: Double = 0.0
var v1787_A: Double = 0.0
var v1788_C: Double = 0.0
var v1789_E: Double = 0.0
var v1790_k: Double = 0.0
var v1791_R: Double = 0.0
var v1792_F: Double = 0.0
var v1793_O: Double = 0.0
var v1794_P: Double = 0.0
var v1795_I: Double = 0.0
var v1796_U_: Double = 0.0
var v1797_B: Double = 0.0
var v1798_L: Double = 0.0
var v1799_G: Double = 0.0
var v1800_U: Double = 0.0
var v1801_V: Double = 0.0
var v1802_D: Double = 0.0
var v1803_K: Double = 0.0
var v1804_S_: Double = 0.0
var v1805_z: Double = 0.0
var v1806_X: Double = 0.0
var v1807_q: String = ""
var v1808_Q: Any? = null
var v1809_W: Any? = null
var v1810_te: Any? = null
var v1811_le: Any? = null
var v1812_ie: Any? = null
var v1813_J: Any? = null
var v1814_de: Any? = null
var v1815_ee: Any? = null
var v1816_se: Double = 0.0
var v1817_x: Grad? = null
v1773_s = createRoomProjector(v1764_a, v1765_o, v1766_r, v1767_t)
v1774_i = v1763_e.createLinearGradient(0.0, 0.0, 0.0, v1767_t)
v1774_i!!.addColorStop(0.0, "#01080b")
v1774_i!!.addColorStop(0.65, "#041821")
v1774_i!!.addColorStop(1.0, "#020b0f")
v1763_e.fillStyle = v1774_i!!
v1763_e.fillRect(0.0, 0.0, v1764_a, v1767_t)
v1775_g = v1763_e.createLinearGradient(0.0, v1767_t, 0.0, v1765_o)
v1775_g!!.addColorStop(0.0, "#010c12")
v1775_g!!.addColorStop(0.35, "#032933")
v1775_g!!.addColorStop(0.75, "#00665e")
v1775_g!!.addColorStop(1.0, "#00d9be")
v1763_e.fillStyle = v1775_g!!
v1763_e.fillRect(0.0, v1767_t, v1764_a, (v1765_o - v1767_t))
v1776_m = jsCall(v1773_s, -0.14, 1.0, 0.04)
v1777_d = jsCall(v1773_s, 0.14, 1.0, 0.04)
v1778_y = jsCall(v1773_s, -0.14, 1.0, 1.25)
v1779_c = jsCall(v1773_s, 0.14, 1.0, 1.25)
v1780_S = v1763_e.createLinearGradient(0.0, v1767_t, 0.0, v1765_o)
v1780_S!!.addColorStop(0.0, "#000508")
v1780_S!!.addColorStop(1.0, "#011820")
drawQuad3D(v1763_e, v1776_m, v1777_d, v1779_c, v1778_y, v1780_S!!, "#00f7d4", 1.5)
run {
v1781_v = -12.0
L244@ while ((v1781_v <= 12.0)) {
L244c@ do {
if ((((v1781_v == -1.0) || (v1781_v == 0.0)) || (v1781_v == 1.0))) {
break@L244c
}
v1782_T = (v1781_v * 0.12)
v1783_b = jsCall(v1773_s, v1782_T, 1.0, 0.04)
v1784_h = jsCall(v1773_s, v1782_T, 1.0, 1.25)
v1763_e.strokeStyle = "rgba(0, 247, 212, 0.28)"
v1763_e.lineWidth = 1.0
v1763_e.beginPath()
v1763_e.moveTo(num(jsGet(v1783_b, "x")), num(jsGet(v1783_b, "y")))
v1763_e.lineTo(num(jsGet(v1784_h, "x")), num(jsGet(v1784_h, "y")))
v1763_e.stroke()
} while (false)
v1781_v++
}
}
v1785_p = 16.0
v1786_w = (v1769_u * 0.4)
v1787_A = (v1786_w % 1.0)
run {
v1788_C = 0.0
L245@ while ((v1788_C < v1785_p)) {
v1789_E = ((v1788_C + v1787_A) / v1785_p)
v1790_k = Math.pow(v1789_E, 1.9)
v1791_R = (v1767_t + (v1790_k * (v1765_o - v1767_t)))
v1763_e.strokeStyle = (("rgba(0, 247, 212, " + jsToFixed((0.18 + (v1790_k * 0.45)), 2.0)) + ")")
v1763_e.lineWidth = (0.8 + (v1790_k * 1.8))
v1763_e.beginPath()
run {
v1792_F = 0.0
L246@ while ((v1792_F <= 32.0)) {
v1793_O = (v1792_F / 32.0)
v1794_P = (v1793_O * v1764_a)
v1795_I = ((kotlin.math.sin((((v1793_O * 12.0) + (v1768_l * 2.1)) - (v1788_C * 0.8))) * (3.0 + (v1770_f * 8.0))) * v1790_k)
if ((v1792_F == 0.0)) {
v1763_e.moveTo(v1794_P, (v1791_R + v1795_I))
} else {
v1763_e.lineTo(v1794_P, (v1791_R + v1795_I))
}
v1792_F++
}
}
v1763_e.stroke()
v1788_C++
}
}
v1796_U_ = 7.0
v1797_B = (v1769_u * 0.36)
v1798_L = (v1797_B % 1.0)
v1799_G = kotlin.math.floor(v1797_B)
run {
v1800_U = 0.0
L247@ while ((v1800_U < v1796_U_)) {
L247c@ do {
v1801_V = ((v1800_U + v1798_L) / v1796_U_)
v1802_D = (((v1800_U + 0.54) + v1798_L) / v1796_U_)
if ((v1801_V < 0.03)) {
break@L247c
}
v1803_K = Math.pow(v1801_V, 1.92)
v1804_S_ = Math.pow(v1802_D, 1.92)
v1805_z = (jsMin(1.0, (v1801_V * 3.8)) * rrBackroomsNearFade(v1801_V, 0.78, 1.03))
if ((v1805_z <= 0.02)) {
break@L247c
}
v1806_X = (v1800_U - v1799_G)
v1807_q = (if (((((v1806_X % 2.0) + 2.0) % 2.0) == 0.0)) "#00f7d4" else "#0bd6b8")
v1763_e.save()
v1763_e.globalAlpha = v1805_z
v1808_Q = jsCall(v1773_s, -0.28, -0.58, v1803_K)
v1809_W = jsCall(v1773_s, 0.28, -0.58, v1803_K)
v1810_te = jsCall(v1773_s, -0.28, -0.58, v1804_S_)
v1811_le = jsCall(v1773_s, 0.28, -0.58, v1804_S_)
v1812_ie = jsCall(v1773_s, -0.28, -1.0, v1804_S_)
v1813_J = jsCall(v1773_s, 0.28, -1.0, v1804_S_)
drawQuad3D(v1763_e, v1808_Q, v1809_W, v1811_le, v1810_te, "#04171f", v1807_q, (1.2 + (v1804_S_ * 1.5)))
drawQuad3D(v1763_e, v1812_ie, v1813_J, v1811_le, v1810_te, "#072530", "#097a6e", 1.2)
v1814_de = jsCall(v1773_s, -0.18, -0.58, ((v1803_K + v1804_S_) * 0.5))
v1815_ee = jsCall(v1773_s, 0.18, -0.58, ((v1803_K + v1804_S_) * 0.5))
v1763_e.strokeStyle = "#aafff5"
v1763_e.lineWidth = jsMax(1.5, (v1804_S_ * 5.0))
v1763_e.beginPath()
v1763_e.moveTo(num(jsGet(v1814_de, "x")), num(jsGet(v1814_de, "y")))
v1763_e.lineTo(num(jsGet(v1815_ee, "x")), num(jsGet(v1815_ee, "y")))
v1763_e.stroke()
js_forEach(jsArr(-1.0, 1.0)) L248@{ v1818_N: Any?, _, _ ->
var v1819_ae: Double = 0.0
var v1820_j: Double = 0.0
var v1821_oe: Double = 0.0
var v1822_Y: Any? = null
var v1823_ne: Any? = null
var v1824_he: Any? = null
var v1825_ve: Any? = null
var v1826_Ce: Any? = null
var v1827_ye: Any? = null
var v1828_Z: Any? = null
var v1829_H: Any? = null
var v1830_fe: Any? = null
var v1831_we: Any? = null
var v1832_Se: Any? = null
var v1833_ke: Any? = null
var v1834_Ee: Grad? = null
var v1835_Pe: Double = 0.0
var v1836_Ie: Double = 0.0
var v1837_Te: Any? = null
var v1838_ue: Any? = null
var v1839_me: Any? = null
var v1840_re: Double = 0.0
var v1841_ge: Double = 0.0
var v1842_pe: Any? = null
var v1843_Me: Any? = null
var v1844_ce: Any? = null
var v1845_be: Any? = null

v1819_ae = (num(v1818_N) * 0.28)
v1820_j = (num(v1818_N) * 0.5)
v1821_oe = (num(v1818_N) * 0.14)
v1822_Y = jsCall(v1773_s, v1821_oe, 0.78, v1803_K)
v1823_ne = jsCall(v1773_s, v1819_ae, 0.78, v1803_K)
v1824_he = jsCall(v1773_s, v1821_oe, 0.78, v1804_S_)
v1825_ve = jsCall(v1773_s, v1819_ae, 0.78, v1804_S_)
v1826_Ce = jsCall(v1773_s, v1821_oe, 1.0, v1803_K)
v1827_ye = jsCall(v1773_s, v1821_oe, 1.0, v1804_S_)
drawQuad3D(v1763_e, v1822_Y, v1823_ne, v1825_ve, v1824_he, "#083642", v1807_q, 1.0)
drawQuad3D(v1763_e, v1822_Y, v1824_he, v1827_ye, v1826_Ce, "#031b22", "#076b61", 1.0)
v1828_Z = jsCall(v1773_s, v1819_ae, -1.0, v1803_K)
v1829_H = jsCall(v1773_s, v1819_ae, 0.78, v1803_K)
v1830_fe = jsCall(v1773_s, v1819_ae, -1.0, v1804_S_)
v1831_we = jsCall(v1773_s, v1819_ae, 0.78, v1804_S_)
v1832_Se = jsCall(v1773_s, v1820_j, -1.0, v1804_S_)
v1833_ke = jsCall(v1773_s, v1820_j, 0.78, v1804_S_)
drawQuad3D(v1763_e, v1828_Z, v1830_fe, v1831_we, v1829_H, "#051d26", v1807_q, (1.3 + (v1804_S_ * 1.4)))
v1834_Ee = v1763_e.createLinearGradient(num(jsGet(v1830_fe, "x")), num(jsGet(v1830_fe, "y")), num(jsGet(v1831_we, "x")), num(jsGet(v1831_we, "y")))
v1834_Ee!!.addColorStop(0.0, "#07222b")
v1834_Ee!!.addColorStop(0.65, "#0b3845")
v1834_Ee!!.addColorStop(1.0, "#051b22")
drawQuad3D(v1763_e, v1830_fe, v1832_Se, v1833_ke, v1831_we, v1834_Ee!!, v1807_q, (1.5 + (v1804_S_ * 1.5)))
v1763_e.strokeStyle = "rgba(0, 247, 212, 0.25)"
v1763_e.lineWidth = 1.0
run {
v1835_Pe = 1.0
L249@ while ((v1835_Pe < 7.0)) {
v1836_Ie = (-1.0 + ((v1835_Pe / 7.0) * 1.78))
v1837_Te = jsCall(v1773_s, v1819_ae, v1836_Ie, v1803_K)
v1838_ue = jsCall(v1773_s, v1819_ae, v1836_Ie, v1804_S_)
v1839_me = jsCall(v1773_s, v1820_j, v1836_Ie, v1804_S_)
v1763_e.beginPath()
v1763_e.moveTo(num(jsGet(v1837_Te, "x")), num(jsGet(v1837_Te, "y")))
v1763_e.lineTo(num(jsGet(v1838_ue, "x")), num(jsGet(v1838_ue, "y")))
v1763_e.lineTo(num(jsGet(v1839_me, "x")), num(jsGet(v1839_me, "y")))
v1763_e.stroke()
v1835_Pe++
}
}
v1840_re = (num(v1818_N) * 0.34)
v1841_ge = (num(v1818_N) * 0.44)
v1842_pe = jsCall(v1773_s, v1840_re, -0.35, v1804_S_)
v1843_Me = jsCall(v1773_s, v1841_ge, -0.35, v1804_S_)
v1844_ce = jsCall(v1773_s, v1840_re, 0.35, v1804_S_)
v1845_be = jsCall(v1773_s, v1841_ge, 0.35, v1804_S_)
drawQuad3D(v1763_e, v1842_pe, v1843_Me, v1845_be, v1844_ce, "#010b0f", "#00f7d4", 1.2)
null
}
v1763_e.restore()
} while (false)
v1800_U++
}
}
v1816_se = (jsMin(v1764_a, v1765_o) * 0.25)
v1817_x = v1763_e.createRadialGradient(v1766_r, v1767_t, 2.0, v1766_r, v1767_t, v1816_se)
v1817_x!!.addColorStop(0.0, (("rgba(0, 247, 212, " + jsToFixed((0.28 + (v1770_f * 0.22)), 2.0)) + ")"))
v1817_x!!.addColorStop(0.35, "rgba(2, 16, 22, 0.92)")
v1817_x!!.addColorStop(1.0, "rgba(2, 16, 22, 0)")
v1763_e.fillStyle = v1817_x!!
v1763_e.fillRect((v1766_r - v1816_se), (v1767_t - v1816_se), (v1816_se * 2.0), (v1816_se * 2.0))
}

fun wp_tl(v1846_l: Ctx, v1847_a: Double, v1848_s: Double, v1849_b: Double, v1850_e: Any?, v1851_S: Double, v1852_E: Any?): Unit {

val v1853_R: Grad = v1846_l.createLinearGradient(0.0, 0.0, 0.0, v1849_b)
val v1854_U_: Any? = (if (((truthy(v1852_E) && jsIsArray(jsGet(v1852_E, "sky"))) && (jsLen(jsGet(v1852_E, "sky")) >= 2.0))) jsGet(v1852_E, "sky") else v1850_e)
js_forEach(v1854_U_) L250@{ v1855_f: Any?, v1856_r: Double, _ ->

v1853_R.addColorStop((v1856_r / jsMax(1.0, (jsLen(v1854_U_) - 1.0))), v1855_f)
}
v1846_l.fillStyle = v1853_R
v1846_l.fillRect(0.0, 0.0, v1847_a, (v1849_b + 2.0))
if ((truthy(jsAnd(g_bgLayers) { jsGet(g_bgLayers, "starWarp") }) && !("object" == "undefined"))) {
v1846_l.fillStyle = "#ffffff"
run {
var v1857_f: Double = 0.0
L251@ while ((v1857_f < jsLen(g_STARS))) {
val v1858_r: Any? = jsGet(g_STARS, v1857_f)
val v1859_y: Double = (((num(jsGet(v1858_r, "x")) + 1000.0) / 2000.0) * v1847_a)
val v1860_h: Double = (((num(jsGet(v1858_r, "y")) + 600.0) / 1200.0) * (v1849_b * 0.92))
val v1861_M: Double = (0.35 + (0.65 * kotlin.math.abs(kotlin.math.sin(((v1851_S * 1.8) + v1857_f)))))
v1846_l.globalAlpha = v1861_M
v1846_l.fillRect(v1859_y, v1860_h, (if (((v1857_f % 4.0) == 0.0)) 2.0 else 1.3), (if (((v1857_f % 4.0) == 0.0)) 2.0 else 1.3))
v1857_f += 2.0
}
}
v1846_l.globalAlpha = 1.0
}
}

fun wp_dl(v1862_l: Ctx, v1863_a: Any?, v1864_s: Any?, v1865_b: Any?, v1866_e: Any?, v1867_S: Any?, v1868_E: Any?, v1869_R: Any?): Unit {

if (!(truthy(g_bgLayers))) {
return
}
val v1870_U_: Double = jsMax(0.0, jsMin(3.0, jsOrND(Rt.win("__rrVisualQualityStage")) { 0.0 }))
if ((v1870_U_ >= 2.0)) {
return
}
val v1871_f: Double = (if ((v1870_U_ == 1.0)) 0.56 else 1.0)
if ((truthy(jsGet(g_bgLayers, "cyberRain")) && !("object" == "undefined"))) {
v1862_l.strokeStyle = jsGet(v1869_R, "primary")
v1862_l.lineWidth = 1.1
v1862_l.globalAlpha = (0.45 * v1871_f)
v1862_l.beginPath()
val v1872_r: Double = (if ((v1870_U_ == 1.0)) 2.0 else 1.0)
run {
var v1873_y: Double = 0.0
L252@ while ((v1873_y < jsLen(g_RAIN_DROPS))) {
val v1874_h: Any? = jsGet(g_RAIN_DROPS, v1873_y)
val v1875_M: Double = (((num(jsGet(v1874_h, "x")) + ((num(v1866_e) * 0.03) * num(jsGet(v1874_h, "z")))) % 1.0) * num(v1863_a))
val v1876_k: Double = (((num(jsGet(v1874_h, "y")) + ((num(v1866_e) * (0.45 + (num(v1867_S) * 0.25))) * num(jsGet(v1874_h, "z")))) % 1.0) * num(v1864_s))
v1862_l.moveTo(v1875_M, v1876_k)
v1862_l.lineTo((v1875_M - (3.0 * num(jsGet(v1874_h, "z")))), (v1876_k + (num(jsGet(v1874_h, "len")) * num(jsGet(v1874_h, "z")))))
v1873_y += v1872_r
}
}
v1862_l.stroke()
v1862_l.globalAlpha = 1.0
}
if (truthy(jsGet(g_bgLayers, "lightShafts"))) {
run {
var v1877_r: Double = 0.0
L253@ while ((v1877_r < (if ((v1870_U_ == 1.0)) 2.0 else 4.0))) {
val v1878_y: Double = ((num(v1863_a) * (0.18 + (v1877_r * 0.22))) + (kotlin.math.sin(((num(v1866_e) * 0.6) + v1877_r)) * 35.0))
val v1879_h: Grad = v1862_l.createLinearGradient(v1878_y, 0.0, v1878_y, (num(v1864_s) * 0.85))
v1879_h.addColorStop(0.0, ("rgba(" + str(jsGet(v1869_R, "waveRgb")) + ", " + jsStr((0.14 * v1871_f)) + ")"))
v1879_h.addColorStop(1.0, "transparent")
v1862_l.fillStyle = v1879_h
v1862_l.beginPath()
v1862_l.moveTo((v1878_y - 18.0), 0.0)
v1862_l.lineTo((v1878_y + 18.0), 0.0)
v1862_l.lineTo((v1878_y + 95.0), (num(v1864_s) * 0.85))
v1862_l.lineTo((v1878_y - 95.0), (num(v1864_s) * 0.85))
v1862_l.closePath()
v1862_l.fill()
v1877_r++
}
}
}
if (truthy(jsAnd(!("object" == "undefined")) { jsLen(g_clickRipples) })) {
run {
var v1880_r: Double = (jsLen(g_clickRipples) - 1.0)
L254@ while ((v1880_r >= 0.0)) {
val v1881_y: Any? = jsGet(g_clickRipples, v1880_r)
v1862_l.strokeStyle = ("rgba(" + str(jsGet(v1869_R, "waveRgb")) + ", " + jsToFixed(jsMax(0.0, num(jsGet(v1881_y, "alpha"))), 3.0) + ")")
v1862_l.lineWidth = 2.0
v1862_l.beginPath()
v1862_l.arc(num(jsGet(v1881_y, "x")), num(jsGet(v1881_y, "y")), num(jsGet(v1881_y, "r")), 0.0, (Math.PI * 2.0))
v1862_l.stroke()
v1880_r--
}
}
}
}

fun wp_Y(v1882_l: Ctx, v1883_a: Double, v1884_s: Double, v1885_b: Double, v1886_e: Double, v1887_S: Double, v1888_E: Double, v1889_R: Double, v1890_U_: Double, v1891_f: Any?): Unit {

wp_tl(v1882_l, v1883_a, v1884_s, v1886_e, jsArr("#070524", "#1d114f", "#5e1f78", "#ff5ea8", "#ffcf96"), v1887_S, v1891_f)
val v1892_r: Double = ((v1883_a * 0.72) + (if (("number" == "number")) (Rt.mouseX * 14.0) else 0.0))
val v1893_y: Double = (v1886_e * 0.42)
val v1894_h: Double = ((jsMin(v1883_a, v1884_s) * 0.13) * (1.0 + (v1889_R * 0.1)))
val v1895_M: Grad = v1882_l.createRadialGradient(v1892_r, v1893_y, (v1894_h * 0.2), v1892_r, v1893_y, (v1894_h * 2.1))
v1895_M.addColorStop(0.0, "rgba(255, 190, 235, 0.5)")
v1895_M.addColorStop(0.5, ("rgba(" + str(jsGet(v1891_f, "waveRgb")) + ", 0.22)"))
v1895_M.addColorStop(1.0, "transparent")
v1882_l.fillStyle = v1895_M
v1882_l.beginPath()
v1882_l.arc(v1892_r, v1893_y, (v1894_h * 2.1), 0.0, (Math.PI * 2.0))
v1882_l.fill()
v1882_l.fillStyle = "#fff6dc"
v1882_l.beginPath()
v1882_l.arc(v1892_r, v1893_y, v1894_h, 0.0, (Math.PI * 2.0))
v1882_l.fill()
v1882_l.fillStyle = "#321456"
v1882_l.beginPath()
v1882_l.arc((v1892_r + (v1894_h * 0.34)), (v1893_y - (v1894_h * 0.18)), (v1894_h * 0.86), 0.0, (Math.PI * 2.0))
v1882_l.fill()
v1882_l.fillStyle = "#1d093a"
v1882_l.strokeStyle = jsGet(v1891_f, "primary")
v1882_l.lineWidth = 1.4
v1882_l.beginPath()
v1882_l.moveTo((v1883_a * 0.35), v1886_e)
v1882_l.quadraticCurveTo((v1883_a * 0.44), (v1886_e - (v1884_s * 0.08)), (v1883_a * 0.53), v1886_e)
v1882_l.quadraticCurveTo((v1883_a * 0.62), (v1886_e - (v1884_s * 0.05)), (v1883_a * 0.7), v1886_e)
v1882_l.closePath()
v1882_l.fill()
v1882_l.stroke()
v1882_l.fillStyle = "#14062b"
v1882_l.strokeStyle = jsGet(v1891_f, "secondary")
v1882_l.lineWidth = 2.0
v1882_l.beginPath()
v1882_l.moveTo(0.0, v1886_e)
v1882_l.lineTo(0.0, (v1886_e - (v1884_s * 0.26)))
v1882_l.lineTo((v1883_a * 0.14), (v1886_e - (v1884_s * 0.22)))
v1882_l.lineTo((v1883_a * 0.25), (v1886_e - (v1884_s * 0.12)))
v1882_l.lineTo((v1883_a * 0.33), v1886_e)
v1882_l.closePath()
v1882_l.fill()
v1882_l.stroke()
val v1896_k: Double = (v1883_a * 0.15)
val v1897_v: Double = (v1886_e - (v1884_s * 0.21))
val v1898_w: Double = (v1884_s * 0.13)
v1882_l.fillStyle = "#ffebf7"
v1882_l.strokeStyle = jsGet(v1891_f, "primary")
v1882_l.beginPath()
v1882_l.moveTo((v1896_k - 9.0), v1897_v)
v1882_l.lineTo((v1896_k - 5.0), (v1897_v - v1898_w))
v1882_l.lineTo((v1896_k + 5.0), (v1897_v - v1898_w))
v1882_l.lineTo((v1896_k + 9.0), v1897_v)
v1882_l.closePath()
v1882_l.fill()
v1882_l.stroke()
v1882_l.fillStyle = jsGet(v1891_f, "secondary")
v1882_l.fillRect((v1896_k - 7.0), (v1897_v - (v1898_w * 0.7)), 14.0, (v1898_w * 0.2))
v1882_l.fillRect((v1896_k - 8.0), (v1897_v - (v1898_w * 0.32)), 16.0, (v1898_w * 0.2))
val v1899_G: Double = ((kotlin.math.sin((v1887_S * 1.15)) * 0.32) - 0.06)
v1882_l.save()
v1882_l.translate(v1896_k, ((v1897_v - v1898_w) - 4.0))
v1882_l.rotate(v1899_G)
val v1900_I: Grad = v1882_l.createLinearGradient(0.0, 0.0, (v1883_a * 0.75), 0.0)
v1900_I.addColorStop(0.0, "rgba(255, 255, 220, 0.75)")
v1900_I.addColorStop(0.3, ("rgba(" + str(jsGet(v1891_f, "waveRgb")) + ", 0.28)"))
v1900_I.addColorStop(1.0, "transparent")
v1882_l.fillStyle = v1900_I
v1882_l.beginPath()
v1882_l.moveTo(0.0, 0.0)
v1882_l.lineTo((v1883_a * 0.75), (-v1884_s * 0.14))
v1882_l.lineTo((v1883_a * 0.75), (v1884_s * 0.14))
v1882_l.closePath()
v1882_l.fill()
v1882_l.restore()
val v1901_A: Grad = v1882_l.createLinearGradient(0.0, v1886_e, 0.0, v1884_s)
v1901_A.addColorStop(0.0, "#123a63")
v1901_A.addColorStop(0.45, "#077b8a")
v1901_A.addColorStop(1.0, "#081b38")
v1882_l.fillStyle = v1901_A
v1882_l.fillRect(0.0, v1886_e, v1883_a, (v1884_s - v1886_e))
run {
var v1904_t: Double = 0.0
L255@ while ((v1904_t < 22.0)) {
val v1905_u: Double = (v1904_t / 22.0)
val v1906_g: Double = (v1886_e + (Math.pow(v1905_u, 1.55) * (v1884_s - v1886_e)))
val v1907_P: Double = ((18.0 + (v1905_u * 120.0)) * (1.0 + (v1889_R * 0.25)))
val v1908_d: Double = (v1892_r + (kotlin.math.sin(((v1887_S * 2.6) + (v1904_t * 0.8))) * (8.0 + (v1905_u * 18.0))))
v1882_l.fillStyle = (if (((v1904_t % 2.0) == 0.0)) "rgba(255, 244, 214, 0.55)" else ("rgba(" + str(jsGet(v1891_f, "waveRgb")) + ", 0.45)"))
v1882_l.fillRect((v1908_d - (v1907_P * 0.5)), v1906_g, v1907_P, (2.0 + (v1905_u * 3.5)))
v1904_t++
}
}
val v1902_m: Double = 7.0
run {
var v1909_t: Double = 0.0
L256@ while ((v1909_t < v1902_m)) {
val v1910_u: Double = (((v1888_E * 0.35) + (v1909_t / v1902_m)) % 1.0)
val v1911_g: Double = (v1910_u * 38.0)
v1882_l.strokeStyle = (if ((v1909_t == 0.0)) "#ffffff" else (if (((v1909_t % 2.0) == 0.0)) jsGet(v1891_f, "primary") else jsGet(v1891_f, "secondary")))
v1882_l.lineWidth = (2.4 - (v1909_t * 0.22))
v1882_l.globalAlpha = (1.0 - (v1910_u * 0.75))
v1882_l.beginPath()
run {
var v1912_P: Double = 0.0
L257@ while ((v1912_P <= 24.0)) {
val v1913_d: Double = (v1912_P / 24.0)
val v1914_o: Double = (v1886_e + (v1913_d * (v1884_s - v1886_e)))
val v1915_n: Double = (((v1883_a * (0.18 + (Math.pow(v1913_d, 1.15) * 0.46))) + (kotlin.math.sin((((v1913_d * 7.0) - (v1887_S * 2.2)) + v1909_t)) * 14.0)) + v1911_g)
if ((v1912_P == 0.0)) {
v1882_l.moveTo(v1915_n, v1914_o)
} else {
v1882_l.lineTo(v1915_n, v1914_o)
}
v1912_P++
}
}
v1882_l.stroke()
v1909_t++
}
}
v1882_l.globalAlpha = 1.0
val v1903_i: Grad = v1882_l.createLinearGradient(0.0, v1886_e, (v1883_a * 0.55), v1884_s)
v1903_i.addColorStop(0.0, "#2a0d45")
v1903_i.addColorStop(0.5, "#61195e")
v1903_i.addColorStop(1.0, "#18052e")
v1882_l.fillStyle = v1903_i
v1882_l.beginPath()
v1882_l.moveTo(0.0, v1886_e)
run {
var v1916_t: Double = 0.0
L258@ while ((v1916_t <= 24.0)) {
val v1917_u: Double = (v1916_t / 24.0)
val v1918_g: Double = (v1886_e + (v1917_u * (v1884_s - v1886_e)))
val v1919_P: Double = ((v1883_a * (0.16 + (Math.pow(v1917_u, 1.15) * 0.44))) + (kotlin.math.sin(((v1917_u * 7.0) - (v1887_S * 2.2))) * 10.0))
v1882_l.lineTo(v1919_P, v1918_g)
v1916_t++
}
}
v1882_l.lineTo(0.0, v1884_s)
v1882_l.closePath()
v1882_l.fill()
run {
var v1920_t: Double = 0.0
L259@ while ((v1920_t < 3.0)) {
val v1921_u: Double = (0.25 + (v1920_t * 0.26))
val v1922_g: Double = (v1883_a * (0.48 + (((v1887_S * (0.025 + (v1920_t * 0.01))) + (v1920_t * 0.31)) % 0.48)))
val v1923_P: Double = ((v1886_e + ((v1921_u * (v1884_s - v1886_e)) * 0.72)) + (kotlin.math.sin(((v1887_S * 2.4) + v1920_t)) * 3.0))
val v1924_d: Double = ((0.55 + (v1921_u * 0.85)) * (jsMin(v1883_a, v1884_s) / 760.0))
v1882_l.save()
v1882_l.translate(v1922_g, v1923_P)
v1882_l.scale(v1924_d, v1924_d)
v1882_l.fillStyle = "#090214"
v1882_l.strokeStyle = jsGet(v1891_f, "primary")
v1882_l.lineWidth = 2.0
v1882_l.beginPath()
v1882_l.moveTo(-36.0, 0.0)
v1882_l.lineTo(38.0, 0.0)
v1882_l.lineTo(26.0, 12.0)
v1882_l.lineTo(-28.0, 12.0)
v1882_l.closePath()
v1882_l.fill()
v1882_l.stroke()
v1882_l.fillStyle = (if (((v1920_t % 2.0) == 0.0)) ("rgba(" + str(jsGet(v1891_f, "altWaveRgb")) + ", 0.78)") else ("rgba(" + str(jsGet(v1891_f, "waveRgb")) + ", 0.78)"))
v1882_l.strokeStyle = "#ffffff"
v1882_l.beginPath()
v1882_l.moveTo(-2.0, -4.0)
v1882_l.lineTo(-2.0, -74.0)
v1882_l.lineTo(-32.0, -4.0)
v1882_l.closePath()
v1882_l.fill()
v1882_l.stroke()
v1882_l.fillStyle = ("rgba(" + str(jsGet(v1891_f, "waveRgb")) + ", 0.62)")
v1882_l.beginPath()
v1882_l.moveTo(3.0, -4.0)
v1882_l.lineTo(0.0, -64.0)
v1882_l.lineTo(28.0, -4.0)
v1882_l.closePath()
v1882_l.fill()
v1882_l.stroke()
v1882_l.restore()
v1920_t++
}
}
if (("function" == "function")) {
val v1925_t: Double = (jsMin(v1883_a, v1884_s) / 760.0)
drawPalmTree(v1882_l, (v1883_a * 0.11), (v1884_s * 0.94), (v1925_t * 1.25), (v1887_S * 1.5), v1889_R, v1891_f, 1.0, 1.1)
drawPalmTree(v1882_l, (v1883_a * 0.24), (v1884_s * 0.98), (v1925_t * 0.95), ((v1887_S * 1.5) + 1.2), v1889_R, v1891_f, 0.95, 0.75)
drawPalmTree(v1882_l, (v1883_a * 0.04), (v1884_s * 0.82), (v1925_t * 0.72), ((v1887_S * 1.5) + 2.4), v1889_R, v1891_f, 0.85, 0.9)
}
}

fun wp_z(v1926_l: Ctx, v1927_a: Double, v1928_s: Double, v1929_b: Double, v1930_e: Double, v1931_S: Double, v1932_E: Double, v1933_R: Double, v1934_U_: Double, v1935_f: Any?): Unit {

val v1936_r: Grad = v1926_l.createLinearGradient(0.0, 0.0, 0.0, v1928_s)
v1936_r.addColorStop(0.0, jsOrSD(jsAnd(jsGet(v1935_f, "sky")) { jsGet(jsGet(v1935_f, "sky"), 0.0) }) { "#04010c" })
v1936_r.addColorStop(0.35, jsOrSD(jsAnd(jsGet(v1935_f, "sky")) { jsGet(jsGet(v1935_f, "sky"), 1.0) }) { "#160736" })
v1936_r.addColorStop(0.58, jsOrSD(jsAnd(jsGet(v1935_f, "sky")) { jsGet(jsGet(v1935_f, "sky"), 2.0) }) { "#3b0e5c" })
v1936_r.addColorStop(1.0, jsOrSD(jsAnd(jsGet(v1935_f, "floor")) { jsGet(jsGet(v1935_f, "floor"), 0.0) }) { "#05010f" })
v1926_l.fillStyle = v1936_r
v1926_l.fillRect(0.0, 0.0, v1927_a, v1928_s)
run {
var v1947_i: Double = 0.0
L260@ while ((v1947_i < 3.0)) {
val v1948_t: Double = ((v1929_b + (((v1947_i - 1.0) * v1927_a) * 0.28)) + (kotlin.math.sin(((v1931_S * 0.3) + v1947_i)) * 30.0))
val v1949_u: Double = (v1930_e * (0.48 + ((v1947_i % 2.0) * 0.18)))
val v1950_g: Double = (jsMin(v1927_a, v1928_s) * (0.28 + (v1947_i * 0.05)))
val v1951_P: Grad = v1926_l.createRadialGradient(v1948_t, v1949_u, (v1950_g * 0.08), v1948_t, v1949_u, v1950_g)
v1951_P.addColorStop(0.0, (if (((v1947_i % 2.0) == 0.0)) ("rgba(" + str(jsGet(v1935_f, "altWaveRgb")) + ", 0.26)") else ("rgba(" + str(jsGet(v1935_f, "waveRgb")) + ", 0.22)")))
v1951_P.addColorStop(0.55, "rgba(130, 30, 210, 0.11)")
v1951_P.addColorStop(1.0, "transparent")
v1926_l.fillStyle = v1951_P
v1926_l.beginPath()
v1926_l.arc(v1948_t, v1949_u, v1950_g, 0.0, (Math.PI * 2.0))
v1926_l.fill()
v1947_i++
}
}
if (!("object" == "undefined")) {
run {
var v1952_i: Double = 0.0
L261@ while ((v1952_i < jsLen(g_STARS))) {
val v1953_t: Any? = jsGet(g_STARS, v1952_i)
val v1954_u: Double = (((num(jsGet(v1953_t, "x")) + 1000.0) / 2000.0) * v1927_a)
val v1955_g: Double = (((num(jsGet(v1953_t, "y")) + 600.0) / 1200.0) * v1928_s)
v1926_l.fillStyle = (if (((v1952_i % 5.0) == 0.0)) jsGet(v1935_f, "accent") else "#ffffff")
v1926_l.globalAlpha = (0.3 + (0.7 * kotlin.math.abs(kotlin.math.sin(((v1931_S * 2.0) + v1952_i)))))
v1926_l.fillRect(v1954_u, v1955_g, (if (((v1952_i % 6.0) == 0.0)) 2.2 else 1.3), (if (((v1952_i % 6.0) == 0.0)) 2.2 else 1.3))
v1952_i++
}
}
v1926_l.globalAlpha = 1.0
}
val v1937_y: Double = (v1929_b + (if (("number" == "number")) (Rt.mouseX * 18.0) else 0.0))
val v1938_h: Double = (v1930_e - (v1928_s * 0.08))
val v1939_M: Double = ((jsMin(v1927_a, v1928_s) * 0.19) * (1.0 + (v1933_R * 0.08)))
val v1940_k: Double = (-0.34 + (kotlin.math.sin((v1931_S * 0.4)) * 0.03))
val v1941_v: Any? = jsArr(jsObj("rx" to 2.55, "ry" to 0.62, "w" to 7.0, "col" to jsGet(v1935_f, "primary"), "alpha" to 0.85), jsObj("rx" to 2.22, "ry" to 0.54, "w" to 10.0, "col" to jsGet(v1935_f, "accent"), "alpha" to 0.72), jsObj("rx" to 1.82, "ry" to 0.44, "w" to 6.0, "col" to jsGet(v1935_f, "secondary"), "alpha" to 0.8), jsObj("rx" to 1.52, "ry" to 0.36, "w" to 4.0, "col" to "#ffffff", "alpha" to 0.55))
v1926_l.save()
v1926_l.translate(v1937_y, v1938_h)
v1926_l.rotate(v1940_k)
js_forEach(v1941_v) L262@{ v1956_i: Any?, _, _ ->

v1926_l.strokeStyle = jsGet(v1956_i, "col")
v1926_l.globalAlpha = num(jsGet(v1956_i, "alpha"))
v1926_l.lineWidth = num(jsGet(v1956_i, "w"))
v1926_l.beginPath()
v1926_l.ellipse(0.0, 0.0, (v1939_M * num(jsGet(v1956_i, "rx"))), (v1939_M * num(jsGet(v1956_i, "ry"))), 0.0, Math.PI, (Math.PI * 2.0))
v1926_l.stroke()
null
}
v1926_l.restore()
v1926_l.save()
v1926_l.beginPath()
v1926_l.arc(v1937_y, v1938_h, v1939_M, 0.0, (Math.PI * 2.0))
v1926_l.clip()
val v1942_w: Grad = v1926_l.createRadialGradient((v1937_y - (v1939_M * 0.35)), (v1938_h - (v1939_M * 0.35)), (v1939_M * 0.1), v1937_y, v1938_h, (v1939_M * 1.05))
v1942_w.addColorStop(0.0, "#fff2c2")
v1942_w.addColorStop(0.45, "#e87a5d")
v1942_w.addColorStop(0.78, "#7a1c6e")
v1942_w.addColorStop(1.0, "#140326")
v1926_l.fillStyle = v1942_w
v1926_l.fillRect((v1937_y - v1939_M), (v1938_h - v1939_M), (v1939_M * 2.0), (v1939_M * 2.0))
v1926_l.rotate((v1940_k * 0.35))
run {
var v1957_i: Double = -6.0
L263@ while ((v1957_i <= 6.0)) {
v1926_l.fillStyle = (if (((v1957_i % 2.0) == 0.0)) "rgba(255, 220, 160, 0.14)" else "rgba(25, 4, 48, 0.22)")
v1926_l.fillRect((v1937_y - (v1939_M * 1.4)), (v1938_h + (v1957_i * (v1939_M * 0.14))), (v1939_M * 2.8), (v1939_M * 0.07))
v1957_i++
}
}
v1926_l.restore()
v1926_l.save()
v1926_l.translate(v1937_y, v1938_h)
v1926_l.rotate(v1940_k)
js_forEach(v1941_v) L264@{ v1958_i: Any?, _, _ ->

v1926_l.strokeStyle = jsGet(v1958_i, "col")
v1926_l.globalAlpha = num(jsGet(v1958_i, "alpha"))
v1926_l.lineWidth = num(jsGet(v1958_i, "w"))
v1926_l.beginPath()
v1926_l.ellipse(0.0, 0.0, (v1939_M * num(jsGet(v1958_i, "rx"))), (v1939_M * num(jsGet(v1958_i, "ry"))), 0.0, 0.0, Math.PI)
v1926_l.stroke()
null
}
run {
var v1959_i: Double = 0.0
L265@ while ((v1959_i < 6.0)) {
val v1960_t: Double = ((v1931_S * (0.45 + (v1959_i * 0.08))) + (v1959_i * 1.05))
val v1961_u: Double = ((kotlin.math.cos(v1960_t) * v1939_M) * (1.65 + ((v1959_i % 3.0) * 0.38)))
val v1962_g: Double = ((kotlin.math.sin(v1960_t) * v1939_M) * (0.4 + ((v1959_i % 3.0) * 0.09)))
if ((kotlin.math.sin(v1960_t) > 0.0)) {
v1926_l.fillStyle = "#ffffff"
v1926_l.globalAlpha = 0.95
v1926_l.beginPath()
v1926_l.arc(v1961_u, v1962_g, (2.5 + ((v1959_i % 2.0) * 1.5)), 0.0, (Math.PI * 2.0))
v1926_l.fill()
}
v1959_i++
}
}
v1926_l.restore()
v1926_l.globalAlpha = 1.0
val v1943_G: Double = 24.0
val v1944_I: Double = (v1932_E % 1.0)
val v1945_A: Double = kotlin.math.floor(v1932_E)
val v1946_m: Any? = jsArr("#ff0077", "#ff7b00", "#ffee00", "#00ff99", "#00f7ff", "#9d00ff")
run {
var v1963_i: Double = 0.0
L266@ while ((v1963_i < v1943_G)) {
val v1964_t: Double = ((v1963_i + v1944_I) / v1943_G)
val v1965_u: Double = (((v1963_i + 1.0) + v1944_I) / v1943_G)
val v1966_g: Double = Math.pow(v1964_t, 1.85)
val v1967_P: Double = Math.pow(v1965_u, 1.85)
val v1968_d: Double = (v1930_e + (v1966_g * (v1928_s - v1930_e)))
val v1969_o: Double = (v1930_e + (v1967_P * (v1928_s - v1930_e)))
val v1970_n: Double = (((kotlin.math.sin((((1.0 - v1964_t) * 2.4) + (v1931_S * 0.9))) * v1927_a) * 0.09) * (1.0 - (v1966_g * 0.7)))
val v1971_p: Double = (((kotlin.math.sin((((1.0 - v1965_u) * 2.4) + (v1931_S * 0.9))) * v1927_a) * 0.09) * (1.0 - (v1967_P * 0.7)))
val v1972_T: Double = (v1927_a * (0.025 + (v1966_g * 0.44)))
val v1973_C: Double = (v1927_a * (0.025 + (v1967_P * 0.44)))
val v1974_S_: Double = (v1929_b + v1970_n)
val v1975_q: Double = (v1929_b + v1971_p)
v1926_l.fillStyle = ("rgba(10, 3, 28, " + jsToFixed((0.55 + (v1966_g * 0.38)), 3.0) + ")")
v1926_l.beginPath()
v1926_l.moveTo((v1974_S_ - v1972_T), v1968_d)
v1926_l.lineTo((v1974_S_ + v1972_T), v1968_d)
v1926_l.lineTo((v1975_q + v1973_C), v1969_o)
v1926_l.lineTo((v1975_q - v1973_C), v1969_o)
v1926_l.closePath()
v1926_l.fill()
run {
var v1976_F: Double = 0.0
L267@ while ((v1976_F < 6.0)) {
val v1977_O: Double = (-1.0 + ((v1976_F / 6.0) * 2.0))
val v1978_B: Double = (-1.0 + (((v1976_F + 1.0) / 6.0) * 2.0))
val v1979_L: Any? = jsGet(v1946_m, v1976_F)
val v1980_N: Double = (((((v1963_i - v1945_A) + v1976_F) % 2.0) + 2.0) % 2.0)
v1926_l.fillStyle = v1979_L
v1926_l.globalAlpha = ((if ((v1980_N == 0.0)) 0.42 else 0.18) * jsMin(1.0, (v1964_t * 2.2)))
v1926_l.beginPath()
v1926_l.moveTo((v1974_S_ + (v1977_O * v1972_T)), v1968_d)
v1926_l.lineTo((v1974_S_ + (v1978_B * v1972_T)), v1968_d)
v1926_l.lineTo((v1975_q + (v1978_B * v1973_C)), v1969_o)
v1926_l.lineTo((v1975_q + (v1977_O * v1973_C)), v1969_o)
v1926_l.closePath()
v1926_l.fill()
v1976_F++
}
}
v1926_l.globalAlpha = jsMin(1.0, (v1964_t * 2.5))
v1926_l.strokeStyle = (if ((((v1963_i - v1945_A) % 2.0) == 0.0)) jsGet(v1935_f, "primary") else jsGet(v1935_f, "secondary"))
v1926_l.lineWidth = (1.5 + (v1966_g * 2.5))
v1926_l.beginPath()
v1926_l.moveTo((v1974_S_ - v1972_T), v1968_d)
v1926_l.lineTo((v1975_q - v1973_C), v1969_o)
v1926_l.moveTo((v1974_S_ + v1972_T), v1968_d)
v1926_l.lineTo((v1975_q + v1973_C), v1969_o)
v1926_l.stroke()
if (((((((v1963_i - v1945_A) % 4.0) + 4.0) % 4.0) == 0.0) && (v1964_t > 0.08))) {
val v1981_F: Double = ((v1928_s * (0.06 + (v1966_g * 0.34))) * (1.0 + (v1933_R * 0.12)))
v1926_l.strokeStyle = jsGet(v1935_f, "accent")
v1926_l.lineWidth = (1.4 + (v1966_g * 2.4))
v1926_l.beginPath()
v1926_l.ellipse(v1974_S_, v1968_d, (v1972_T * 1.08), v1981_F, 0.0, Math.PI, (Math.PI * 2.0))
v1926_l.stroke()
}
v1963_i++
}
}
v1926_l.globalAlpha = 1.0
}

fun wp_x(v1982_l: Ctx, v1983_a: Double, v1984_s: Double, v1985_b: Double, v1986_e: Double, v1987_S: Double, v1988_E: Double, v1989_R: Double, v1990_U_: Double, v1991_f: Any?): Unit {

val v1992_r: Double = (Math.PI * 2.0)
val v1993_y: Double = jsMin(v1983_a, v1984_s)
val v1994_h: String = jsOrSD(jsAnd(v1991_f) { jsGet(v1991_f, "waveRgb") }) { "90,255,200" }
val v1995_M: String = jsOrSD(jsAnd(v1991_f) { jsGet(v1991_f, "altWaveRgb") }) { "255,90,220" }
val v1996_k: Grad = v1982_l.createLinearGradient(0.0, 0.0, 0.0, v1986_e)
v1996_k.addColorStop(0.0, "#02060f")
v1996_k.addColorStop(0.55, "#061a2c")
v1996_k.addColorStop(1.0, "#123a4c")
v1982_l.fillStyle = v1996_k
v1982_l.fillRect(0.0, 0.0, v1983_a, (v1986_e + 1.0))
v1982_l.fillStyle = "#e8fbff"
run {
var v2009_o: Double = 0.0
L268@ while ((v2009_o < 70.0)) {
val v2010_n: Double = ((v2009_o * 197.31) % v1983_a)
val v2011_p: Double = ((v2009_o * 71.73) % (v1986_e * 0.9))
v1982_l.globalAlpha = (0.25 + (0.35 * (0.5 + (0.5 * kotlin.math.sin(((v1987_S * 0.5) + (v2009_o * 2.3)))))))
val v2012_T: Double = (if (((v2009_o % 11.0) == 0.0)) 2.0 else 1.1)
v1982_l.fillRect(v2010_n, v2011_p, v2012_T, v2012_T)
v2009_o++
}
}
v1982_l.globalAlpha = 1.0
fun v1997_v(v2013_o: Ctx, v2014_n: Double, v2015_p: Double): Unit {

val v2016_T: Double = (v1986_e * (0.42 + (v2014_n * 0.12)))
val v2017_C: Double = (v1986_e * (0.08 + (v2014_n * 0.03)))
val v2018_S_: Double = (v1986_e * (0.26 - (v2014_n * 0.05)))
val v2019_q: Double = (v1983_a / 48.0)
val v2020_F: String = (if (truthy((v2014_n % 2.0))) v1995_M else v1994_h)
fun v2021_O(v2023_L: Double): Double {

return ((v2016_T + (kotlin.math.sin(((((v2023_L / v1983_a) * 4.2) + (v1987_S * (0.09 + (v2014_n * 0.03)))) + (v2014_n * 2.0))) * v2017_C)) + ((kotlin.math.sin(((((v2023_L / v1983_a) * 9.5) - (v1987_S * 0.06)) + v2014_n)) * v2017_C) * 0.35))
}
val v2022_B: Grad = v2013_o.createLinearGradient(0.0, ((v2016_T - v2018_S_) - v2017_C), 0.0, (v2016_T + v2017_C))
v2022_B.addColorStop(0.0, ("rgba(" + v2020_F + ",0)"))
v2022_B.addColorStop(0.75, ("rgba(" + v2020_F + "," + jsToFixed((0.22 * v2015_p), 3.0) + ")"))
v2022_B.addColorStop(1.0, ("rgba(" + v2020_F + "," + jsToFixed((0.5 * v2015_p), 3.0) + ")"))
v2013_o.fillStyle = v2022_B
v2013_o.beginPath()
run {
var v2024_L: Double = 0.0
L269@ while ((v2024_L <= (v1983_a + v2019_q))) {
val v2025_N: Double = v2021_O(v2024_L)
if (truthy(v2024_L)) { v2013_o.lineTo(v2024_L, v2025_N) } else { v2013_o.moveTo(v2024_L, v2025_N) }
v2024_L += v2019_q
}
}
run {
var v2026_L: Double = (v1983_a + v2019_q)
L270@ while ((v2026_L >= 0.0)) {
val v2027_N: Double = (v2021_O(v2026_L) - (v2018_S_ * (0.75 + (0.25 * kotlin.math.sin(((((v2026_L / v1983_a) * 7.0) + (v1987_S * 0.12)) + v2014_n))))))
v2013_o.lineTo(v2026_L, v2027_N)
v2026_L -= v2019_q
}
}
v2013_o.closePath()
v2013_o.fill()
v2013_o.strokeStyle = v2022_B
v2013_o.lineWidth = jsMax(1.0, (v1983_a / 900.0))
v2013_o.globalAlpha = 0.7
v2013_o.beginPath()
run {
var v2028_L: Double = ((v2014_n * 7.0) % 9.0)
L271@ while ((v2028_L <= v1983_a)) {
val v2029_N: Double = v2021_O(v2028_L)
val v2030_W: Double = (v2018_S_ * (0.45 + (0.4 * (0.5 + (0.5 * kotlin.math.sin((((v2028_L * 0.05) + (v1987_S * 0.25)) + v2014_n)))))))
v2013_o.moveTo(v2028_L, v2029_N)
v2013_o.lineTo(v2028_L, (v2029_N - v2030_W))
v2028_L += (v1983_a / 110.0)
}
}
v2013_o.stroke()
v2013_o.globalAlpha = 1.0
}
v1982_l.save()
v1982_l.globalCompositeOperation = "lighter"
run {
var v2031_o: Double = 0.0
L272@ while ((v2031_o < 3.0)) {
v1997_v(v1982_l, v2031_o, (1.0 + (v1989_R * 0.25)))
v2031_o++
}
}
v1982_l.restore()
val v1998_w: Double = ((v1987_S % 11.0) / 1.2)
if ((v1998_w < 1.0)) {
val v2032_o: Double = ((v1983_a * 0.15) + (((kotlin.math.floor((v1987_S / 11.0)) * 0.37) % 0.6) * v1983_a))
val v2033_n: Double = (v1986_e * 0.12)
val v2034_p: Double = (v2032_o + ((v1998_w * v1983_a) * 0.22))
val v2035_T: Double = (v2033_n + ((v1998_w * v1986_e) * 0.18))
val v2036_C: Grad = v1982_l.createLinearGradient((v2034_p - (v1983_a * 0.06)), (v2035_T - (v1986_e * 0.05)), v2034_p, v2035_T)
v2036_C.addColorStop(0.0, "rgba(255,255,255,0)")
v2036_C.addColorStop(1.0, ("rgba(255,255,255," + jsToFixed((0.8 * kotlin.math.sin((v1998_w * Math.PI))), 3.0) + ")"))
v1982_l.strokeStyle = v2036_C
v1982_l.lineWidth = 1.5
v1982_l.beginPath()
v1982_l.moveTo((v2034_p - (v1983_a * 0.06)), (v2035_T - (v1986_e * 0.05)))
v1982_l.lineTo(v2034_p, v2035_T)
v1982_l.stroke()
}
val v1999_G: Any? = jsArr(jsObj("h" to 0.3, "seed" to 1.3, "fill" to "#0c2333", "snow" to "rgba(220,250,255,0.55)"), jsObj("h" to 0.2, "seed" to 3.1, "fill" to "#081925", "snow" to "rgba(200,240,255,0.35)"), jsObj("h" to 0.11, "seed" to 5.7, "fill" to "#05111b", "snow" to "rgba(180,230,255,0.2)"))
fun v2000_I(v2037_o: Any?, v2038_n: Double): Double {

return (v1986_e - ((v1986_e * num(jsGet(v2037_o, "h"))) * ((0.45 + (0.35 * kotlin.math.abs(kotlin.math.sin((((v2038_n / v1983_a) * 5.1) + num(jsGet(v2037_o, "seed"))))))) + (0.2 * kotlin.math.abs(kotlin.math.sin((((v2038_n / v1983_a) * 13.7) + (num(jsGet(v2037_o, "seed")) * 2.0))))))))
}
js_forEach(v1999_G) L273@{ v2039_o: Any?, _, _ ->

v1982_l.fillStyle = jsGet(v2039_o, "fill")
v1982_l.beginPath()
v1982_l.moveTo(0.0, v1986_e)
run {
var v2040_n: Double = 0.0
L274@ while ((v2040_n <= v1983_a)) {
v1982_l.lineTo(v2040_n, v2000_I(v2039_o, v2040_n))
v2040_n += (v1983_a / 36.0)
}
}
v1982_l.lineTo(v1983_a, v1986_e)
v1982_l.closePath()
v1982_l.fill()
v1982_l.strokeStyle = jsGet(v2039_o, "snow")
v1982_l.lineWidth = 1.2
v1982_l.beginPath()
run {
var v2041_n: Double = 0.0
L275@ while ((v2041_n <= v1983_a)) {
val v2042_p: Double = v2000_I(v2039_o, v2041_n)
if (truthy(v2041_n)) { v1982_l.lineTo(v2041_n, v2042_p) } else { v1982_l.moveTo(v2041_n, v2042_p) }
v2041_n += (v1983_a / 36.0)
}
}
v1982_l.stroke()
null
}
val v2001_A: Double = (v1983_a * 0.72)
val v2002_m: Double = (v2000_I(jsGet(v1999_G, 2.0), (v1983_a * 0.72)) + 2.0)
val v2003_i: Double = (v1993_y * 0.085)
val v2004_t: Double = ((-Math.PI / 2.0) + (kotlin.math.sin((v1987_S * 0.08)) * 0.7))
val v2005_u: Grad = v1982_l.createLinearGradient(v2001_A, (v2002_m - v2003_i), (v2001_A + (kotlin.math.cos(v2004_t) * v1986_e)), ((v2002_m - v2003_i) + (kotlin.math.sin(v2004_t) * v1986_e)))
v2005_u.addColorStop(0.0, ("rgba(" + v1994_h + ",0.22)"))
v2005_u.addColorStop(1.0, ("rgba(" + v1994_h + ",0)"))
v1982_l.fillStyle = v2005_u
v1982_l.beginPath()
v1982_l.moveTo((v2001_A - (v2003_i * 0.08)), (v2002_m - (v2003_i * 0.9)))
v1982_l.lineTo((v2001_A + ((kotlin.math.cos((v2004_t - 0.05)) * v1986_e) * 1.1)), ((v2002_m - v2003_i) + ((kotlin.math.sin((v2004_t - 0.05)) * v1986_e) * 1.1)))
v1982_l.lineTo((v2001_A + ((kotlin.math.cos((v2004_t + 0.05)) * v1986_e) * 1.1)), ((v2002_m - v2003_i) + ((kotlin.math.sin((v2004_t + 0.05)) * v1986_e) * 1.1)))
v1982_l.lineTo((v2001_A + (v2003_i * 0.08)), (v2002_m - (v2003_i * 0.9)))
v1982_l.closePath()
v1982_l.fill()
v1982_l.fillStyle = "#0b1f2c"
v1982_l.fillRect((v2001_A - (v2003_i * 1.25)), (v2002_m - (v2003_i * 0.55)), (v2003_i * 2.5), (v2003_i * 0.6))
val v2006_g: Grad = v1982_l.createLinearGradient((v2001_A - v2003_i), 0.0, (v2001_A + v2003_i), 0.0)
v2006_g.addColorStop(0.0, "#5f7f92")
v2006_g.addColorStop(0.45, "#d6eef6")
v2006_g.addColorStop(1.0, "#3a5566")
v1982_l.fillStyle = v2006_g
v1982_l.beginPath()
v1982_l.arc(v2001_A, (v2002_m - (v2003_i * 0.55)), v2003_i, Math.PI, v1992_r)
v1982_l.closePath()
v1982_l.fill()
v1982_l.save()
v1982_l.beginPath()
v1982_l.arc(v2001_A, (v2002_m - (v2003_i * 0.55)), v2003_i, Math.PI, v1992_r)
v1982_l.clip()
val v2007_P: Double = (v2001_A + ((kotlin.math.cos(v2004_t) * v2003_i) * 0.5))
v1982_l.fillStyle = "#06121b"
v1982_l.fillRect((v2007_P - (v2003_i * 0.13)), (v2002_m - (v2003_i * 1.6)), (v2003_i * 0.26), (v2003_i * 1.1))
v1982_l.restore()
v1982_l.fillStyle = ("rgba(" + v1994_h + ",0.85)")
run {
var v2043_o: Double = 0.0
L276@ while ((v2043_o < 4.0)) {
v1982_l.fillRect(((v2001_A - (v2003_i * 1.05)) + ((v2043_o * v2003_i) * 0.58)), (v2002_m - (v2003_i * 0.38)), (v2003_i * 0.18), (v2003_i * 0.14))
v2043_o++
}
}
v1982_l.fillStyle = "#9fc7d6"
v1982_l.fillRect((v2001_A + (v2003_i * 1.5)), (v2002_m - (v2003_i * 1.1)), jsMax(1.0, (v2003_i * 0.04)), (v2003_i * 1.1))
v1982_l.fillStyle = "#ff6b6b"
v1982_l.globalAlpha = (0.55 + (0.25 * kotlin.math.sin((v1987_S * 0.8))))
v1982_l.beginPath()
v1982_l.arc((v2001_A + (v2003_i * 1.52)), (v2002_m - (v2003_i * 1.12)), jsMax(1.5, (v2003_i * 0.05)), 0.0, v1992_r)
v1982_l.fill()
v1982_l.globalAlpha = 1.0
val v2008_d: Grad = v1982_l.createLinearGradient(0.0, v1986_e, 0.0, v1984_s)
v2008_d.addColorStop(0.0, "#0b2a38")
v2008_d.addColorStop(1.0, "#020810")
v1982_l.fillStyle = v2008_d
v1982_l.fillRect(0.0, v1986_e, v1983_a, (v1984_s - v1986_e))
v1982_l.save()
v1982_l.beginPath()
v1982_l.rect(0.0, v1986_e, v1983_a, (v1984_s - v1986_e))
v1982_l.clip()
v1982_l.translate(0.0, (v1986_e * 2.0))
v1982_l.scale(1.0, -1.0)
v1982_l.globalCompositeOperation = "lighter"
v1982_l.globalAlpha = 0.25
run {
var v2044_o: Double = 0.0
L277@ while ((v2044_o < 2.0)) {
v1997_v(v1982_l, v2044_o, 0.6)
v2044_o++
}
}
v1982_l.globalCompositeOperation = "source-over"
v1982_l.globalAlpha = 0.85
js_forEach(jsReverse(jsSliceArr(v1999_G))) L278@{ v2045_o: Any?, _, _ ->

v1982_l.fillStyle = jsGet(v2045_o, "fill")
v1982_l.beginPath()
v1982_l.moveTo(0.0, v1986_e)
run {
var v2046_n: Double = 0.0
L279@ while ((v2046_n <= v1983_a)) {
v1982_l.lineTo(v2046_n, v2000_I(v2045_o, v2046_n))
v2046_n += (v1983_a / 36.0)
}
}
v1982_l.lineTo(v1983_a, v1986_e)
v1982_l.closePath()
v1982_l.fill()
null
}
v1982_l.restore()
v1982_l.strokeStyle = "rgba(200,245,255,0.12)"
v1982_l.lineWidth = 1.0
v1982_l.beginPath()
run {
var v2047_o: Double = 0.0
L280@ while ((v2047_o < 12.0)) {
val v2048_n: Double = (v1986_e + ((v1984_s - v1986_e) * Math.pow(((v2047_o + ((v1987_S * 0.04) % 1.0)) / 12.0), 1.6)))
val v2049_p: Double = (v1983_a * (0.15 + (((v2047_o * 37.0) % 10.0) / 20.0)))
val v2050_T: Double = (((v2047_o * 211.0) % v1983_a) - (v2049_p / 2.0))
v1982_l.moveTo(v2050_T, v2048_n)
v1982_l.lineTo((v2050_T + v2049_p), v2048_n)
v2047_o++
}
}
v1982_l.stroke()
v1982_l.fillStyle = "rgba(220,250,255,0.25)"
v1982_l.fillRect(0.0, v1986_e, v1983_a, 1.0)
}

fun wp_ll(v2051_l: Ctx, v2052_a: Double, v2053_s: Double, v2054_b: Double, v2055_e: Double, v2056_S: Double, v2057_E: Double, v2058_R: Double, v2059_U_: Double, v2060_f: Any?): Unit {

val v2061_r: Double = jsMax(4.0, jsRound((jsMin(v2052_a, v2053_s) / 115.0)))
fun v2062_y(v2078_o: Double): Double {

return (jsRound((v2078_o / v2061_r)) * v2061_r)
}
val v2063_h: Any? = (if ((truthy(jsAnd(v2060_f) { jsGet(v2060_f, "sky") }) && (jsLen(jsGet(v2060_f, "sky")) >= 5.0))) jsArr(jsGet(jsGet(v2060_f, "sky"), 0.0), jsGet(jsGet(v2060_f, "sky"), 1.0), jsGet(jsGet(v2060_f, "sky"), 2.0), jsGet(jsGet(v2060_f, "strips"), 1.0), jsGet(jsGet(v2060_f, "strips"), 2.0), jsGet(jsGet(v2060_f, "strips"), 3.0), jsGet(jsGet(v2060_f, "sky"), 3.0), jsGet(jsGet(v2060_f, "sky"), 4.0)) else jsArr("#140429", "#2b0948", "#52115e", "#871c69", "#bf2d63", "#eb5652", "#ff8e47", "#ffd066"))
val v2064_M: Double = (v2055_e / jsLen(v2063_h))
run {
var v2079_o: Double = 0.0
L281@ while ((v2079_o < jsLen(v2063_h))) {
v2051_l.fillStyle = jsGet(v2063_h, v2079_o)
v2051_l.fillRect(0.0, kotlin.math.floor((v2079_o * v2064_M)), v2052_a, kotlin.math.ceil((v2064_M + 1.0)))
if ((v2079_o > 0.0)) {
v2051_l.fillStyle = jsGet(v2063_h, (v2079_o - 1.0))
val v2080_n: Double = v2062_y((v2079_o * v2064_M))
run {
var v2081_p: Double = 0.0
L282@ while ((v2081_p < v2052_a)) {
v2051_l.fillRect(v2081_p, v2080_n, v2061_r, v2061_r)
v2081_p += (v2061_r * 2.0)
}
}
}
v2079_o++
}
}
v2051_l.fillStyle = "#fff9c4"
run {
var v2082_o: Double = 0.0
L283@ while ((v2082_o < 24.0)) {
val v2083_n: Double = v2062_y(((((v2082_o * 173.7) % 100.0) / 100.0) * v2052_a))
val v2084_p: Double = v2062_y(((((v2082_o * 61.3) % 100.0) / 100.0) * (v2055_e * 0.48)))
v2051_l.globalAlpha = (0.55 + (0.3 * kotlin.math.sin(((v2056_S * 0.7) + (v2082_o * 1.7)))))
v2051_l.fillRect(v2083_n, v2084_p, v2061_r, v2061_r)
if (((v2082_o % 3.0) == 0.0)) {
v2051_l.fillRect((v2083_n - v2061_r), v2084_p, v2061_r, v2061_r)
v2051_l.fillRect((v2083_n + v2061_r), v2084_p, v2061_r, v2061_r)
v2051_l.fillRect(v2083_n, (v2084_p - v2061_r), v2061_r, v2061_r)
v2051_l.fillRect(v2083_n, (v2084_p + v2061_r), v2061_r, v2061_r)
}
v2082_o++
}
}
v2051_l.globalAlpha = 1.0
val v2065_k: Double = v2062_y(((jsMin(v2052_a, v2053_s) * 0.21) * (1.0 + (v2058_R * 0.1))))
val v2066_v: Double = v2062_y((v2055_e - (v2065_k * 0.28)))
run {
var v2085_o: Double = -v2065_k
L284@ while ((v2085_o <= v2065_k)) {
L284c@ do {
val v2086_n: Double = ((v2085_o + v2065_k) / (v2065_k * 2.0))
if (((v2086_n > 0.48) && (kotlin.math.floor((((((v2085_o / v2061_r) + (v2056_S * 0.5)) % 3.0) + 3.0) % 3.0)) == 0.0))) {
break@L284c
}
val v2087_p: Double = v2062_y(kotlin.math.sqrt(jsMax(0.0, ((v2065_k * v2065_k) - (v2085_o * v2085_o)))))
v2051_l.fillStyle = (if ((v2086_n < 0.33)) "#fff799" else (if ((v2086_n < 0.66)) "#ff7744" else "#ff1e77"))
v2051_l.fillRect((v2062_y(v2054_b) - v2087_p), (v2066_v + v2062_y(v2085_o)), (v2087_p * 2.0), kotlin.math.ceil((v2061_r * 1.25)))
} while (false)
v2085_o += (v2061_r * 1.5)
}
}
run {
var v2088_o: Double = 0.0
L285@ while ((v2088_o < 5.0)) {
val v2089_n: Double = v2062_y((((((v2088_o * 0.23) + (v2056_S * (0.015 + ((v2088_o % 2.0) * 0.008)))) % 1.25) - 0.12) * v2052_a))
val v2090_p: Double = v2062_y((v2055_e * (0.22 + ((v2088_o % 3.0) * 0.18))))
val v2091_T: Double = (v2061_r * (10.0 + ((v2088_o % 3.0) * 4.0)))
v2051_l.fillStyle = (if (((v2088_o % 2.0) == 0.0)) "#ff9ec7" else "#ffd899")
v2051_l.fillRect((v2089_n - (v2091_T * 0.5)), v2090_p, v2091_T, (v2061_r * 2.0))
v2051_l.fillRect((v2089_n - (v2091_T * 0.35)), (v2090_p - (v2061_r * 2.0)), (v2091_T * 0.7), (v2061_r * 2.0))
v2051_l.fillRect((v2089_n - (v2091_T * 0.18)), (v2090_p - (v2061_r * 3.0)), (v2091_T * 0.36), v2061_r)
v2051_l.fillStyle = "#52115e"
v2051_l.fillRect((v2089_n - (v2091_T * 0.5)), (v2090_p + (v2061_r * 2.0)), v2091_T, v2061_r)
v2088_o++
}
}
val v2067_w: Double = (v2053_s - v2055_e)
val v2068_G: Double = 22.0
val v2069_I: Double = (v2056_S * 0.3)
val v2070_A: Double = kotlin.math.floor(v2069_I)
val v2071_m: Double = (v2069_I - v2070_A)
run {
var v2092_o: Double = -1.0
L286@ while ((v2092_o < v2068_G)) {
L286c@ do {
val v2093_n: Double = jsMax(0.0, ((v2092_o + v2071_m) / v2068_G))
val v2094_p: Double = jsMin(1.0, (((v2092_o + 1.0) + v2071_m) / v2068_G))
if ((v2094_p <= v2093_n)) {
break@L286c
}
val v2095_T: Double = kotlin.math.floor((v2055_e + (Math.pow(v2093_n, 1.65) * v2067_w)))
val v2096_C: Double = kotlin.math.ceil((v2055_e + (Math.pow(v2094_p, 1.65) * v2067_w)))
val v2097_S_: Double = jsMax(1.0, (v2096_C - v2095_T))
val v2098_q: Double = (v2092_o - v2070_A)
val v2099_F: Double = ((v2093_n + v2094_p) * 0.5)
val v2100_O: Double = jsMax((v2061_r * 2.0), (v2052_a * (0.025 + (Math.pow(v2099_F, 1.3) * 0.11))))
val v2101_B: Double = (kotlin.math.ceil(((v2052_a * 0.5) / v2100_O)) + 1.0)
run {
var v2102_L: Double = -v2101_B
L287@ while ((v2102_L < v2101_B)) {
val v2103_N: Double = jsRound((v2054_b + (v2102_L * v2100_O)))
val v2104_W: Boolean = ((v2102_L >= -2.0) && (v2102_L <= 1.0))
val v2105_sl: Boolean = ((jsInt((v2098_q + v2102_L)) and jsInt(1.0)).toDouble() == 0.0)
if (v2104_W) {
val v2106_rl: Double = jsMin(1.0, (kotlin.math.abs((v2102_L + 0.5)) / 2.5))
v2051_l.fillStyle = (if (v2105_sl) (if ((v2106_rl < 0.5)) "#b85a6e" else "#8e4466") else (if ((v2106_rl < 0.5)) "#9a4a68" else "#74385c"))
} else {
v2051_l.fillStyle = (if (v2105_sl) "#211560" else "#1a0f4c")
}
v2051_l.fillRect(v2103_N, v2095_T, jsMax(1.0, ((jsRound((v2054_b + ((v2102_L + 1.0) * v2100_O))) - v2103_N) - 1.0)), v2097_S_)
v2102_L++
}
}
} while (false)
v2092_o++
}
}
val v2072_i: Any? = jsArr(jsObj("x" to (v2052_a * 0.21), "y" to (v2055_e - (v2053_s * 0.08)), "w" to 9.0, "h" to 5.0, "bob" to 0.0), jsObj("x" to (v2052_a * 0.79), "y" to (v2055_e - (v2053_s * 0.11)), "w" to 10.0, "h" to 6.0, "bob" to 2.1), jsObj("x" to (v2052_a * 0.12), "y" to (v2055_e + (v2053_s * 0.08)), "w" to 12.0, "h" to 6.0, "bob" to 4.2), jsObj("x" to (v2052_a * 0.87), "y" to (v2055_e + (v2053_s * 0.06)), "w" to 11.0, "h" to 6.0, "bob" to 1.1))
js_forEach(v2072_i) L288@{ v2107_o: Any?, v2108_n: Double, _ ->

val v2109_p: Double = v2062_y(((kotlin.math.sin(((v2056_S * 2.0) + num(jsGet(v2107_o, "bob")))) * v2061_r) * 2.0))
val v2110_T: Double = v2062_y(num(jsGet(v2107_o, "x")))
val v2111_C: Double = v2062_y((num(jsGet(v2107_o, "y")) + v2109_p))
val v2112_S_: Double = (num(jsGet(v2107_o, "w")) * v2061_r)
val v2113_q: Double = (num(jsGet(v2107_o, "h")) * v2061_r)
v2051_l.fillStyle = (if (((v2108_n % 2.0) == 0.0)) "#00f7ff" else "#ff4da6")
v2051_l.fillRect((v2110_T - v2112_S_), v2111_C, (v2112_S_ * 2.0), (v2061_r * 2.0))
run {
var v2116_B: Double = 0.0
L289@ while ((v2116_B < num(jsGet(v2107_o, "h")))) {
val v2117_L: Double = (v2112_S_ * (1.0 - (v2116_B / (num(jsGet(v2107_o, "h")) + 1.0))))
v2051_l.fillStyle = (if (((v2116_B % 2.0) == 0.0)) "#3a125e" else "#1f0738")
v2051_l.fillRect(v2062_y((v2110_T - v2117_L)), (v2111_C + ((v2116_B + 2.0) * v2061_r)), v2062_y((v2117_L * 2.0)), v2061_r)
v2116_B++
}
}
val v2114_F: Double = (v2110_T - v2061_r)
val v2115_O: Double = (v2061_r * (10.0 + ((v2108_n % 2.0) * 4.0)))
run {
var v2118_B: Double = 0.0
L290@ while ((v2118_B < v2115_O)) {
val v2119_L: Boolean = !((((kotlin.math.floor(((v2118_B / v2061_r) - (v2056_S * 4.0))) % 3.0) + 3.0) % 3.0) == 0.0)
v2051_l.fillStyle = (if (v2119_L) "#99ffff" else "#00aaff")
v2051_l.fillRect(v2114_F, ((v2111_C + (v2061_r * 2.0)) + v2118_B), (v2061_r * 2.0), v2061_r)
v2118_B += v2061_r
}
}
null
}
val v2073_t: Double = v2062_y(((v2054_b + ((kotlin.math.sin((v2056_S * 1.4)) * v2052_a) * 0.12)) + (if (("number" == "number")) (Rt.mouseX * 36.0) else 0.0)))
val v2074_u: Double = v2062_y(((v2053_s - (v2067_w * 0.26)) + ((kotlin.math.cos((v2056_S * 2.3)) * v2061_r) * 2.0)))
val v2075_g: Any? = jsArr("......11......", ".....1221.....", "....123321....", "..1122332211..", "11222344322211", "12211344311221", "11...1551...11")
val v2076_P: Any? = jsObj("1" to "#ffffff", "2" to jsGet(v2060_f, "primary"), "3" to "#3a125e", "4" to jsGet(v2060_f, "secondary"), "5" to (if ((kotlin.math.sin((v2056_S * 1.6)) > 0.0)) "#ffd23a" else "#ff6a4a"))
val v2077_d: Double = (jsLen(jsGet(v2075_g, 0.0)) * v2061_r)
js_forEach(v2075_g) L291@{ v2120_o: Any?, v2121_n: Double, _ ->

run {
var v2122_p: Double = 0.0
L292@ while ((v2122_p < jsLen(v2120_o))) {
val v2123_T: Any? = jsGet(v2120_o, v2122_p)
if (truthy(jsGet(v2076_P, v2123_T))) {
v2051_l.fillStyle = jsGet(v2076_P, v2123_T)
v2051_l.fillRect(((v2073_t - kotlin.math.floor((v2077_d / 2.0))) + (v2122_p * v2061_r)), (v2074_u + (v2121_n * v2061_r)), v2061_r, v2061_r)
}
v2122_p++
}
}
null
}
}

fun wp_ol(v2124_l: Ctx, v2125_a: Double, v2126_s: Double, v2127_b: Double, v2128_e: Double, v2129_S: Double, v2130_E: Double, v2131_R: Double, v2132_U_: Double, v2133_f: Any?): Unit {

val v2134_r: Grad = v2124_l.createLinearGradient(0.0, 0.0, 0.0, v2126_s)
v2134_r.addColorStop(0.0, "#170d3c")
v2134_r.addColorStop(0.38, "#5b2469")
v2134_r.addColorStop(0.64, "#e35b83")
v2134_r.addColorStop(1.0, "#160d35")
v2124_l.fillStyle = v2134_r
v2124_l.fillRect(0.0, 0.0, v2125_a, v2126_s)
v2124_l.fillStyle = "#fff0cf"
run {
var v2150_d: Double = 0.0
L293@ while ((v2150_d < 18.0)) {
val v2151_o: Double = (((v2150_d * 173.17) + (v2129_S * (if (((v2150_d % 3.0) == 0.0)) 1.6 else 0.35))) % v2125_a)
val v2152_n: Double = ((v2150_d * 67.3) % (v2128_e * 0.48))
v2124_l.globalAlpha = (0.36 + ((v2150_d % 4.0) * 0.13))
v2124_l.fillRect(v2151_o, v2152_n, (if (((v2150_d % 5.0) == 0.0)) 2.0 else 1.2), (if (((v2150_d % 5.0) == 0.0)) 2.0 else 1.2))
v2150_d++
}
}
v2124_l.globalAlpha = 1.0
val v2135_y: Double = (jsMin(v2125_a, v2126_s) * (0.18 + (v2131_R * 0.012)))
val v2136_h: Double = (v2128_e - (v2135_y * 0.3))
v2124_l.save()
v2124_l.beginPath()
v2124_l.arc(v2127_b, v2136_h, v2135_y, 0.0, (Math.PI * 2.0))
v2124_l.clip()
val v2137_M: Grad = v2124_l.createLinearGradient(0.0, (v2136_h - v2135_y), 0.0, (v2136_h + v2135_y))
v2137_M.addColorStop(0.0, "#fff6bd")
v2137_M.addColorStop(0.42, "#ffb36b")
v2137_M.addColorStop(0.72, "#ff5a83")
v2137_M.addColorStop(1.0, "#8b2e9b")
v2124_l.fillStyle = v2137_M
v2124_l.fillRect((v2127_b - v2135_y), (v2136_h - v2135_y), (v2135_y * 2.0), (v2135_y * 2.0))
v2124_l.fillStyle = "#4e205f"
run {
var v2153_d: Double = 0.0
L294@ while ((v2153_d < 5.0)) {
val v2154_o: Double = (v2136_h + (v2135_y * (0.16 + (v2153_d * 0.17))))
v2124_l.fillRect((v2127_b - v2135_y), v2154_o, (v2135_y * 2.0), jsMax(2.0, (v2126_s * (0.006 + (v2153_d * 0.001)))))
v2153_d++
}
}
v2124_l.restore()
v2124_l.strokeStyle = "#ffd4b4"
v2124_l.lineWidth = 1.5
v2124_l.beginPath()
v2124_l.arc(v2127_b, v2136_h, (v2135_y * 1.08), 0.0, (Math.PI * 2.0))
v2124_l.stroke()
val v2138_k: Any? = jsArr("#5c286d", "#362052", "#21153f")
run {
var v2155_d: Double = 0.0
L295@ while ((v2155_d < 3.0)) {
val v2156_o: Double = (v2128_e + (v2126_s * (0.035 + (v2155_d * 0.035))))
v2124_l.beginPath()
v2124_l.moveTo(0.0, v2126_s)
v2124_l.lineTo(0.0, v2156_o)
run {
var v2157_n: Double = 0.0
L296@ while ((v2157_n <= 8.0)) {
val v2158_p: Double = ((v2157_n * v2125_a) / 8.0)
val v2159_T: Double = (v2156_o + ((kotlin.math.sin((((v2157_n * 1.35) + (v2155_d * 1.6)) + (v2129_S * (0.08 + (v2155_d * 0.025))))) * v2126_s) * (0.026 + (v2155_d * 0.012))))
v2124_l.lineTo(v2158_p, v2159_T)
v2157_n++
}
}
v2124_l.lineTo(v2125_a, v2126_s)
v2124_l.closePath()
v2124_l.fillStyle = jsGet(v2138_k, v2155_d)
v2124_l.globalAlpha = (0.9 - (v2155_d * 0.08))
v2124_l.fill()
v2124_l.strokeStyle = (if ((v2155_d == 0.0)) "#ff9eaa" else "#7a67b4")
v2124_l.globalAlpha = 0.62
v2124_l.lineWidth = 1.0
v2124_l.stroke()
v2155_d++
}
}
v2124_l.globalAlpha = 1.0
val v2139_v: Double = 11.0
val v2140_w: Double = 12.0
val v2141_G: Double = (v2130_E * 0.5)
val v2142_I: Double = kotlin.math.floor(v2141_G)
val v2143_A: Double = (v2141_G - v2142_I)
fun v2144_m(v2160_d: Double, v2161_o: Double): Double {

val v2162_n: Double = kotlin.math.abs((v2161_o - (v2140_w / 2.0)))
if ((v2162_n <= 1.0)) {
return 0.0
}
val v2163_p: Double = ((kotlin.math.sin(((v2160_d * 0.83) + (v2161_o * 1.27))) * 0.55) + (kotlin.math.sin((((v2160_d * 0.31) - (v2161_o * 0.71)) + 1.3)) * 0.45))
return ((0.35 + (0.65 * ((v2163_p * 0.5) + 0.5))) * jsMin(1.0, ((v2162_n - 1.0) / 3.0)))
return Double.NaN
}
val v2145_i: Any? = jsArr()
run {
var v2164_d: Double = 0.0
L297@ while ((v2164_d <= v2139_v)) {
val v2165_o: Double = ((v2164_d + v2143_A) / v2139_v)
val v2166_n: Double = Math.pow(v2165_o, 1.78)
val v2167_p: Double = (v2128_e + (v2166_n * (v2126_s - v2128_e)))
val v2168_T: Double = (v2125_a * (0.07 + (v2166_n * 0.66)))
val v2169_C: Double = (v2142_I - v2164_d)
val v2170_S_: Any? = jsArr()
run {
var v2171_q: Double = 0.0
L298@ while ((v2171_q <= v2140_w)) {
val v2172_F: Double = (v2171_q - (v2140_w / 2.0))
jsPush(v2170_S_, jsObj("x" to (v2127_b + ((v2172_F / (v2140_w / 2.0)) * v2168_T)), "y" to (v2167_p - (((v2144_m(v2169_C, v2171_q) * v2126_s) * 0.075) * (0.25 + v2166_n))), "h" to v2144_m(v2169_C, v2171_q), "wr" to v2169_C))
v2171_q++
}
}
jsPush(v2145_i, v2170_S_)
v2164_d++
}
}
val v2146_t: Any? = jsArr("#2a1850", "#3a1f60", "#4d2668", "#652d70", "#874079", "#a85584")
run {
var v2173_d: Double = 0.0
L299@ while ((v2173_d < v2139_v)) {
run {
var v2174_o: Double = 0.0
L300@ while ((v2174_o < v2140_w)) {
val v2175_n: Any? = jsGet(jsGet(v2145_i, v2173_d), v2174_o)
val v2176_p: Any? = jsGet(jsGet(v2145_i, v2173_d), (v2174_o + 1.0))
val v2177_T: Any? = jsGet(jsGet(v2145_i, (v2173_d + 1.0)), v2174_o)
val v2178_C: Any? = jsGet(jsGet(v2145_i, (v2173_d + 1.0)), (v2174_o + 1.0))
val v2179_S_: Double = kotlin.math.abs(((v2174_o + 0.5) - (v2140_w / 2.0)))
val v2180_q: Double = jsMax(0.0, jsMin(5.0, jsRound((((2.4 + ((num(jsGet(v2176_p, "h")) - num(jsGet(v2175_n, "h"))) * 3.0)) + ((num(jsGet(v2175_n, "h")) - num(jsGet(v2177_T, "h"))) * 2.0)) + (v2179_S_ * 0.12)))))
val v2181_F: Double = jsMax(0.0, jsMin(5.0, jsRound((((1.8 + ((num(jsGet(v2178_C, "h")) - num(jsGet(v2177_T, "h"))) * 3.0)) + ((num(jsGet(v2176_p, "h")) - num(jsGet(v2178_C, "h"))) * 2.0)) + (v2179_S_ * 0.12)))))
v2124_l.fillStyle = jsGet(v2146_t, v2180_q)
v2124_l.beginPath()
v2124_l.moveTo(num(jsGet(v2175_n, "x")), num(jsGet(v2175_n, "y")))
v2124_l.lineTo(num(jsGet(v2176_p, "x")), num(jsGet(v2176_p, "y")))
v2124_l.lineTo(num(jsGet(v2178_C, "x")), num(jsGet(v2178_C, "y")))
v2124_l.closePath()
v2124_l.fill()
v2124_l.fillStyle = jsGet(v2146_t, v2181_F)
v2124_l.beginPath()
v2124_l.moveTo(num(jsGet(v2175_n, "x")), num(jsGet(v2175_n, "y")))
v2124_l.lineTo(num(jsGet(v2178_C, "x")), num(jsGet(v2178_C, "y")))
v2124_l.lineTo(num(jsGet(v2177_T, "x")), num(jsGet(v2177_T, "y")))
v2124_l.closePath()
v2124_l.fill()
v2174_o++
}
}
v2173_d++
}
}
v2124_l.strokeStyle = "rgba(255,170,200,0.16)"
v2124_l.lineWidth = 0.8
v2124_l.beginPath()
run {
var v2182_d: Double = 0.0
L301@ while ((v2182_d < v2139_v)) {
run {
var v2183_o: Double = 0.0
L302@ while ((v2183_o < v2140_w)) {
L302c@ do {
if ((kotlin.math.abs(((v2183_o + 0.5) - (v2140_w / 2.0))) < 1.2)) {
break@L302c
}
val v2184_n: Any? = jsGet(jsGet(v2145_i, v2182_d), v2183_o)
val v2185_p: Any? = jsGet(jsGet(v2145_i, (v2182_d + 1.0)), (v2183_o + 1.0))
v2124_l.moveTo(num(jsGet(v2184_n, "x")), num(jsGet(v2184_n, "y")))
v2124_l.lineTo(num(jsGet(v2185_p, "x")), num(jsGet(v2185_p, "y")))
} while (false)
v2183_o++
}
}
v2182_d++
}
}
v2124_l.stroke()
val v2147_u: Double = 0.62
fun v2148_g(v2186_d: Any?, v2187_o: Double): Double {

return (v2127_b + (((num(jsGet(jsGet(v2186_d, ((v2140_w / 2.0) + 1.0)), "x")) - v2127_b) * v2147_u) * v2187_o))
}
v2124_l.fillStyle = "#1b1030"
v2124_l.beginPath()
v2124_l.moveTo(v2148_g(jsGet(v2145_i, 0.0), -1.0), num(jsGet(jsGet(jsGet(v2145_i, 0.0), (v2140_w / 2.0)), "y")))
run {
var v2188_d: Double = 0.0
L303@ while ((v2188_d <= v2139_v)) {
v2124_l.lineTo(v2148_g(jsGet(v2145_i, v2188_d), 1.0), num(jsGet(jsGet(jsGet(v2145_i, v2188_d), (v2140_w / 2.0)), "y")))
v2188_d++
}
}
run {
var v2189_d: Double = v2139_v
L304@ while ((v2189_d >= 0.0)) {
v2124_l.lineTo(v2148_g(jsGet(v2145_i, v2189_d), -1.0), num(jsGet(jsGet(jsGet(v2145_i, v2189_d), (v2140_w / 2.0)), "y")))
v2189_d--
}
}
v2124_l.closePath()
v2124_l.fill()
v2124_l.strokeStyle = "#ff7fb0"
v2124_l.lineWidth = jsMax(1.2, (v2125_a * 0.0018))
v2124_l.globalAlpha = 0.85
js_forEach(jsArr(-1.0, 1.0)) L305@{ v2190_d: Any?, _, _ ->

v2124_l.beginPath()
run {
var v2191_o: Double = 0.0
L306@ while ((v2191_o <= v2139_v)) {
val v2192_n: Double = v2148_g(jsGet(v2145_i, v2191_o), (num(v2190_d) * 0.96))
val v2193_p: Any? = jsGet(jsGet(jsGet(v2145_i, v2191_o), (v2140_w / 2.0)), "y")
if (truthy(v2191_o)) { v2124_l.lineTo(v2192_n, num(v2193_p)) } else { v2124_l.moveTo(v2192_n, num(v2193_p)) }
v2191_o++
}
}
v2124_l.stroke()
null
}
v2124_l.globalAlpha = 0.9
v2124_l.fillStyle = "#ffd98a"
run {
var v2194_d: Double = 0.0
L307@ while ((v2194_d < v2139_v)) {
L307c@ do {
if (truthy((((num(jsGet(jsGet(jsGet(v2145_i, v2194_d), 0.0), "wr")) % 2.0) + 2.0) % 2.0))) {
break@L307c
}
val v2195_o: Any? = jsGet(jsGet(jsGet(v2145_i, v2194_d), (v2140_w / 2.0)), "y")
val v2196_n: Any? = jsGet(jsGet(jsGet(v2145_i, (v2194_d + 1.0)), (v2140_w / 2.0)), "y")
val v2197_p: Double = ((v2148_g(jsGet(v2145_i, v2194_d), 1.0) - v2127_b) * 0.05)
val v2198_T: Double = ((v2148_g(jsGet(v2145_i, (v2194_d + 1.0)), 1.0) - v2127_b) * 0.05)
val v2199_C: Double = (num(v2195_o) + ((num(v2196_n) - num(v2195_o)) * 0.2))
val v2200_S_: Double = (num(v2195_o) + ((num(v2196_n) - num(v2195_o)) * 0.8))
val v2201_q: Double = (v2197_p + ((v2198_T - v2197_p) * 0.2))
val v2202_F: Double = (v2197_p + ((v2198_T - v2197_p) * 0.8))
v2124_l.beginPath()
v2124_l.moveTo((v2127_b - v2201_q), v2199_C)
v2124_l.lineTo((v2127_b + v2201_q), v2199_C)
v2124_l.lineTo((v2127_b + v2202_F), v2200_S_)
v2124_l.lineTo((v2127_b - v2202_F), v2200_S_)
v2124_l.closePath()
v2124_l.fill()
} while (false)
v2194_d++
}
}
v2124_l.globalAlpha = 1.0
val v2149_P: Grad = v2124_l.createLinearGradient(0.0, (v2128_e - (v2126_s * 0.01)), 0.0, (v2128_e + (v2126_s * 0.12)))
v2149_P.addColorStop(0.0, "rgba(227,91,131,0.85)")
v2149_P.addColorStop(1.0, "rgba(227,91,131,0)")
v2124_l.fillStyle = v2149_P
v2124_l.fillRect(0.0, (v2128_e - (v2126_s * 0.01)), v2125_a, (v2126_s * 0.13))
}

fun wp_al(v2203_l: Ctx, v2204_a: Double, v2205_s: Double, v2206_b: Double, v2207_e: Double, v2208_S: Double, v2209_E: Double, v2210_R: Double, v2211_U_: Double, v2212_f: Any?): Unit {

val v2213_r: Double = (Math.PI * 2.0)
v2203_l.fillStyle = jsOrSD(jsAnd(jsGet(v2212_f, "sky")) { jsGet(jsGet(v2212_f, "sky"), 0.0) }) { "#05010f" }
v2203_l.fillRect(0.0, 0.0, v2204_a, v2205_s)
val v2214_y: Double = 16.0
val v2215_h: Double = (v2209_E * 0.22)
val v2216_M: Double = kotlin.math.floor(v2215_h)
val v2217_k: Double = (v2215_h - v2216_M)
val v2218_v: Double = (Math.hypot(v2204_a, v2205_s) * 0.62)
val v2219_w: Double = (v2205_s * 0.5)
val v2220_G: Any? = jsArr()
run {
var v2230_o: Double = 0.0
L308@ while ((v2230_o <= v2214_y)) {
val v2231_n: Double = ((v2230_o + v2217_k) / v2214_y)
val v2232_p: Double = Math.pow(v2231_n, 2.3)
val v2233_T: Double = Math.pow((1.0 - v2231_n), 2.0)
jsPush(v2220_G, jsObj("n" to v2231_n, "p" to v2232_p, "world" to (v2216_M - v2230_o), "x" to (v2206_b + (((kotlin.math.sin((v2208_S * 0.12)) * v2204_a) * 0.09) * v2233_T)), "y" to (v2219_w + (((kotlin.math.cos((v2208_S * 0.09)) * v2205_s) * 0.05) * v2233_T)), "rad" to (5.0 + (v2232_p * v2218_v))))
v2230_o++
}
}
val v2221_I: Any? = jsGet(v2220_G, 0.0)
val v2222_A: String = jsOrSD(jsAnd(jsGet(v2212_f, "floor")) { jsGet(jsGet(v2212_f, "floor"), 1.0) }) { "#160b30" }
run {
var v2234_o: Double = 0.0
L309@ while ((v2234_o < v2214_y)) {
val v2235_n: Any? = jsGet(v2220_G, v2234_o)
val v2236_p: Any? = jsGet(v2220_G, (v2234_o + 1.0))
v2203_l.beginPath()
v2203_l.arc(num(jsGet(v2236_p, "x")), num(jsGet(v2236_p, "y")), num(jsGet(v2236_p, "rad")), 0.0, v2213_r)
v2203_l.moveTo((num(jsGet(v2235_n, "x")) + num(jsGet(v2235_n, "rad"))), num(jsGet(v2235_n, "y")))
v2203_l.arc(num(jsGet(v2235_n, "x")), num(jsGet(v2235_n, "y")), num(jsGet(v2235_n, "rad")), 0.0, v2213_r, true)
v2203_l.globalAlpha = 1.0
v2203_l.fillStyle = v2222_A
v2203_l.fill("evenodd")
if (truthy((jsInt(num(jsGet(v2235_n, "world"))) and jsInt(1.0)).toDouble())) {
v2203_l.fillStyle = ("rgba(" + str(jsGet(v2212_f, "waveRgb")) + ",0.07)")
v2203_l.fill("evenodd")
}
val v2237_T: Double = jsMax(0.0, (0.85 - (num(jsGet(v2235_n, "n")) * 1.05)))
if ((v2237_T > 0.01)) {
v2203_l.globalAlpha = v2237_T
v2203_l.fillStyle = "#000"
v2203_l.fill("evenodd")
}
v2234_o++
}
}
v2203_l.globalAlpha = 1.0
val v2223_m: Double = (v2218_v * 0.28)
val v2224_i: Grad = v2203_l.createRadialGradient(num(jsGet(v2221_I, "x")), num(jsGet(v2221_I, "y")), 0.0, num(jsGet(v2221_I, "x")), num(jsGet(v2221_I, "y")), v2223_m)
v2224_i.addColorStop(0.0, "rgba(255,255,255,0.95)")
v2224_i.addColorStop(0.08, ("rgba(" + str(jsGet(v2212_f, "waveRgb")) + ",0.8)"))
v2224_i.addColorStop(1.0, ("rgba(" + str(jsGet(v2212_f, "waveRgb")) + ",0)"))
v2203_l.save()
v2203_l.globalCompositeOperation = "lighter"
v2203_l.fillStyle = v2224_i
v2203_l.beginPath()
v2203_l.arc(num(jsGet(v2221_I, "x")), num(jsGet(v2221_I, "y")), v2223_m, 0.0, v2213_r)
v2203_l.fill()
v2203_l.restore()
val v2225_t: Grad = v2203_l.createLinearGradient(0.0, 0.0, 0.0, v2205_s)
v2225_t.addColorStop(0.0, "rgba(255,255,255,0.06)")
v2225_t.addColorStop(0.5, "rgba(0,0,0,0)")
v2225_t.addColorStop(1.0, "rgba(0,0,0,0.35)")
v2203_l.fillStyle = v2225_t
v2203_l.fillRect(0.0, 0.0, v2204_a, v2205_s)
fun v2226_u(v2238_o: Double): Any? {

return js_map(v2220_G) L310@{ v2239_n: Any?, _, _ ->

jsArr((num(jsGet(v2239_n, "x")) + (kotlin.math.cos(v2238_o) * num(jsGet(v2239_n, "rad")))), (num(jsGet(v2239_n, "y")) + (kotlin.math.sin(v2238_o) * num(jsGet(v2239_n, "rad")))))
}
}
val v2227_g: Any? = v2226_u(((Math.PI / 2.0) + 0.42))
val v2228_P: Any? = v2226_u(((Math.PI / 2.0) - 0.42))
v2203_l.beginPath()
js_forEach(v2227_g) L311@{ v2240_o: Any?, v2241_n: Double, _ ->

(if (truthy(v2241_n)) v2203_l.lineTo(num(jsGet(v2240_o, 0.0)), num(jsGet(v2240_o, 1.0))) else v2203_l.moveTo(num(jsGet(v2240_o, 0.0)), num(jsGet(v2240_o, 1.0))))
}
run {
var v2242_o: Double = (jsLen(v2228_P) - 1.0)
L312@ while ((v2242_o >= 0.0)) {
v2203_l.lineTo(num(jsGet(jsGet(v2228_P, v2242_o), 0.0)), num(jsGet(jsGet(v2228_P, v2242_o), 1.0)))
v2242_o--
}
}
v2203_l.closePath()
v2203_l.fillStyle = "rgba(0,0,0,0.45)"
v2203_l.fill()
v2203_l.strokeStyle = jsGet(v2212_f, "accent")
v2203_l.globalAlpha = 0.55
v2203_l.lineWidth = 1.2
v2203_l.beginPath()
js_forEach(v2227_g) L313@{ v2243_o: Any?, v2244_n: Double, _ ->

(if (truthy(v2244_n)) v2203_l.lineTo(num(jsGet(v2243_o, 0.0)), num(jsGet(v2243_o, 1.0))) else v2203_l.moveTo(num(jsGet(v2243_o, 0.0)), num(jsGet(v2243_o, 1.0))))
}
js_forEach(v2228_P) L314@{ v2245_o: Any?, v2246_n: Double, _ ->

(if (truthy(v2246_n)) v2203_l.lineTo(num(jsGet(v2245_o, 0.0)), num(jsGet(v2245_o, 1.0))) else v2203_l.moveTo(num(jsGet(v2245_o, 0.0)), num(jsGet(v2245_o, 1.0))))
}
v2203_l.stroke()
v2203_l.globalAlpha = 1.0
val v2229_d: Double = 12.0
v2203_l.strokeStyle = jsGet(v2212_f, "secondary")
v2203_l.lineWidth = 1.0
v2203_l.globalAlpha = 0.4
v2203_l.beginPath()
run {
var v2247_o: Double = 0.0
L315@ while ((v2247_o < v2229_d)) {
val v2248_n: Double = (((v2247_o / v2229_d) * v2213_r) + (Math.PI / v2229_d))
run {
var v2249_p: Double = 0.0
L316@ while ((v2249_p <= v2214_y)) {
val v2250_T: Any? = jsGet(v2220_G, v2249_p)
val v2251_C: Double = (num(jsGet(v2250_T, "x")) + (kotlin.math.cos(v2248_n) * num(jsGet(v2250_T, "rad"))))
val v2252_S_: Double = (num(jsGet(v2250_T, "y")) + (kotlin.math.sin(v2248_n) * num(jsGet(v2250_T, "rad"))))
if (truthy(v2249_p)) { v2203_l.lineTo(v2251_C, v2252_S_) } else { v2203_l.moveTo(v2251_C, v2252_S_) }
v2249_p++
}
}
v2247_o++
}
}
v2203_l.stroke()
run {
var v2253_o: Double = 0.0
L317@ while ((v2253_o <= v2214_y)) {
L317c@ do {
val v2254_n: Any? = jsGet(v2220_G, v2253_o)
if (truthy((((num(jsGet(v2254_n, "world")) % 4.0) + 4.0) % 4.0))) {
break@L317c
}
val v2255_p: Double = (0.25 + (0.75 * num(jsGet(v2254_n, "n"))))
v2203_l.strokeStyle = jsGet(v2212_f, "primary")
v2203_l.globalAlpha = (v2255_p * 0.22)
v2203_l.lineWidth = (2.0 + (num(jsGet(v2254_n, "p")) * 14.0))
v2203_l.beginPath()
v2203_l.arc(num(jsGet(v2254_n, "x")), num(jsGet(v2254_n, "y")), num(jsGet(v2254_n, "rad")), 0.0, v2213_r)
v2203_l.stroke()
v2203_l.globalAlpha = (v2255_p * 0.9)
v2203_l.lineWidth = (1.0 + (num(jsGet(v2254_n, "p")) * 3.5))
v2203_l.stroke()
} while (false)
v2253_o++
}
}
v2203_l.globalAlpha = 1.0
}

fun wp_el(v2256_l: Ctx, v2257_a: Double, v2258_s: Double, v2259_b: Double, v2260_e: Double, v2261_S: Double, v2262_E: Double, v2263_R: Double, v2264_U_: Double, v2265_f: Any?): Unit {

val v2266_r: Grad = v2256_l.createLinearGradient(0.0, 0.0, 0.0, v2258_s)
v2266_r.addColorStop(0.0, jsOrSD(jsAnd(jsGet(v2265_f, "sky")) { jsGet(jsGet(v2265_f, "sky"), 1.0) }) { "#120829" })
v2266_r.addColorStop(0.58, jsOrSD(jsAnd(jsGet(v2265_f, "floor")) { jsGet(jsGet(v2265_f, "floor"), 1.0) }) { "#21143d" })
v2266_r.addColorStop(1.0, jsOrSD(jsAnd(jsGet(v2265_f, "floor")) { jsGet(jsGet(v2265_f, "floor"), 0.0) }) { "#120829" })
v2256_l.fillStyle = v2266_r
v2256_l.fillRect(0.0, 0.0, v2257_a, v2258_s)
v2256_l.strokeStyle = jsGet(v2265_f, "secondary")
v2256_l.lineWidth = 1.2
run {
var v2271_v: Double = -8.0
L318@ while ((v2271_v <= 8.0)) {
v2256_l.beginPath()
v2256_l.moveTo((v2259_b + ((v2271_v * v2257_a) * 0.11)), 0.0)
v2256_l.lineTo((v2259_b + ((v2271_v * v2257_a) * 0.02)), v2260_e)
v2256_l.stroke()
v2271_v++
}
}
val v2267_y: Double = 10.0
val v2268_h: Double = 10.0
val v2269_M: Double = (v2262_E % 1.0)
val v2270_k: Double = kotlin.math.floor(v2262_E)
run {
var v2272_v: Double = 0.0
L319@ while ((v2272_v < v2267_y)) {
val v2273_w: Double = ((v2272_v + v2269_M) / v2267_y)
val v2274_G: Double = (((v2272_v + 1.0) + v2269_M) / v2267_y)
val v2275_I: Double = Math.pow(v2273_w, 1.8)
val v2276_A: Double = Math.pow(v2274_G, 1.8)
val v2277_m: Double = (v2260_e + (v2275_I * (v2258_s - v2260_e)))
val v2278_i: Double = (v2260_e + (v2276_A * (v2258_s - v2260_e)))
val v2279_t: Double = (v2257_a * (0.02 + (v2275_I * 0.12)))
val v2280_u: Double = (v2257_a * (0.02 + (v2276_A * 0.12)))
run {
var v2281_g: Double = (-v2268_h / 2.0)
L320@ while ((v2281_g < (v2268_h / 2.0))) {
v2256_l.fillStyle = (if (((jsInt(((v2272_v - v2270_k) + v2281_g)) and jsInt(1.0)).toDouble() == 0.0)) jsGet(jsGet(v2265_f, "floor"), 2.0) else jsGet(jsGet(v2265_f, "floor"), 0.0))
v2256_l.beginPath()
v2256_l.moveTo((v2259_b + (v2281_g * v2279_t)), v2277_m)
v2256_l.lineTo((v2259_b + ((v2281_g + 1.0) * v2279_t)), v2277_m)
v2256_l.lineTo((v2259_b + ((v2281_g + 1.0) * v2280_u)), v2278_i)
v2256_l.lineTo((v2259_b + (v2281_g * v2280_u)), v2278_i)
v2256_l.closePath()
v2256_l.fill()
v2281_g++
}
}
v2272_v++
}
}
run {
var v2282_v: Double = 0.0
L321@ while ((v2282_v < 6.0)) {
val v2283_w: Double = ((v2282_v + v2269_M) / 6.0)
val v2284_G: Double = Math.pow(v2283_w, 1.75)
val v2285_I: Double = (v2260_e + (v2284_G * (v2258_s - v2260_e)))
val v2286_A: Double = (v2258_s * (0.14 + (v2284_G * 0.48)))
val v2287_m: Double = (8.0 + (v2284_G * 34.0))
js_forEach(jsArr(-1.0, 1.0)) L322@{ v2288_i: Any?, _, _ ->

val v2289_t: Double = ((v2259_b + (num(v2288_i) * ((v2257_a * 0.13) + ((v2284_G * v2257_a) * 0.36)))) - (v2287_m * 0.5))
v2256_l.fillStyle = jsGet(v2265_f, "sunSlice")
v2256_l.strokeStyle = jsGet(v2265_f, "primary")
v2256_l.lineWidth = 1.5
v2256_l.fillRect(v2289_t, (v2285_I - v2286_A), v2287_m, v2286_A)
v2256_l.strokeRect(v2289_t, (v2285_I - v2286_A), v2287_m, v2286_A)
v2256_l.fillStyle = jsGet(v2265_f, "secondary")
v2256_l.fillRect((v2289_t - (v2287_m * 0.2)), ((v2285_I - v2286_A) - (6.0 * v2284_G)), (v2287_m * 1.4), ((6.0 * v2284_G) + 3.0))
null
}
v2282_v++
}
}
if (("function" == "function")) {
drawFloatingMonolith(v2256_l, v2259_b, ((v2260_e - (v2258_s * 0.12)) + (kotlin.math.sin((v2261_S * 2.0)) * 8.0)), (jsMin(v2257_a, v2258_s) * 0.11), (v2261_S * 0.9), v2263_R, v2265_f)
}
}

fun wp_j(v2290_l: Ctx, v2291_a: Double, v2292_s: Double, v2293_b: Double, v2294_e: Double, v2295_S: Double, v2296_E: Double, v2297_R: Double, v2298_U_: Double, v2299_f: Any?, v2300_r: Boolean): Unit {

val v2301_y: Boolean = !!v2300_r
val v2302_h: Grad = v2290_l.createLinearGradient(0.0, 0.0, 0.0, v2292_s)
v2302_h.addColorStop(0.0, (if (v2301_y) "#020c20" else "#07384c"))
v2302_h.addColorStop(0.36, (if (v2301_y) "#07365d" else "#0d7580"))
v2302_h.addColorStop(0.62, (if (v2301_y) "#105a82" else "#26a6a0"))
v2302_h.addColorStop(1.0, (if (v2301_y) "#010817" else "#073345"))
v2290_l.fillStyle = v2302_h
v2290_l.fillRect(0.0, 0.0, v2291_a, v2292_s)
v2290_l.fillStyle = (if (v2301_y) "rgba(86,172,255,0.10)" else "rgba(175,255,246,0.10)")
v2290_l.beginPath()
v2290_l.moveTo(0.0, 0.0)
v2290_l.lineTo(v2291_a, 0.0)
v2290_l.lineTo((v2293_b + (v2291_a * 0.18)), (v2294_e * 0.55))
v2290_l.lineTo((v2293_b - (v2291_a * 0.18)), (v2294_e * 0.55))
v2290_l.closePath()
v2290_l.fill()
run {
var v2312_t: Double = -1.0
L323@ while ((v2312_t <= 1.0)) {
val v2313_u: Double = (v2293_b + ((v2312_t * v2291_a) * 0.27))
val v2314_g: Double = (v2294_e * (0.22 + (if ((v2312_t == 0.0)) 0.0 else 0.025)))
val v2315_P: Double = (jsMin(v2291_a, v2292_s) * 0.062)
v2290_l.fillStyle = (if (v2301_y) "rgba(70,155,255,0.19)" else "rgba(190,255,248,0.16)")
v2290_l.beginPath()
v2290_l.moveTo((v2313_u - v2315_P), v2314_g)
v2290_l.lineTo((v2313_u + v2315_P), v2314_g)
v2290_l.lineTo((v2313_u + (v2315_P * 2.5)), v2292_s)
v2290_l.lineTo((v2313_u - (v2315_P * 2.5)), v2292_s)
v2290_l.closePath()
v2290_l.fill()
v2290_l.fillStyle = (if (v2301_y) "#b8e5ff" else "#d9fff9")
v2290_l.strokeStyle = (if (v2301_y) "#4caeff" else "#63f9ed")
v2290_l.lineWidth = 2.0
v2290_l.beginPath()
v2290_l.ellipse(v2313_u, v2314_g, v2315_P, (v2315_P * 0.28), 0.0, 0.0, (Math.PI * 2.0))
v2290_l.fill()
v2290_l.stroke()
v2290_l.strokeStyle = (if (v2301_y) "rgba(166,220,255,0.72)" else "rgba(255,255,255,0.72)")
v2290_l.lineWidth = 1.0
v2290_l.beginPath()
v2290_l.ellipse(v2313_u, v2314_g, (v2315_P * 0.72), (v2315_P * 0.15), 0.0, 0.0, (Math.PI * 2.0))
v2290_l.stroke()
v2312_t++
}
}
val v2303_M: Double = (v2294_e + (v2292_s * 0.025))
val v2304_k: Double = (v2291_a * 0.34)
val v2305_v: Double = (v2292_s * 0.4)
run {
var v2316_t: Double = 0.0
L324@ while ((v2316_t < 3.0)) {
val v2317_u: Double = (v2304_k * (1.0 - (v2316_t * 0.16)))
val v2318_g: Double = (v2305_v * (1.0 - (v2316_t * 0.13)))
val v2319_P: Double = (v2303_M - v2318_g)
v2290_l.beginPath()
v2290_l.moveTo((v2293_b - (v2317_u * 0.5)), v2303_M)
v2290_l.lineTo((v2293_b - (v2317_u * 0.5)), (v2319_P + (v2318_g * 0.43)))
v2290_l.quadraticCurveTo((v2293_b - (v2317_u * 0.46)), v2319_P, v2293_b, v2319_P)
v2290_l.quadraticCurveTo((v2293_b + (v2317_u * 0.46)), v2319_P, (v2293_b + (v2317_u * 0.5)), (v2319_P + (v2318_g * 0.43)))
v2290_l.lineTo((v2293_b + (v2317_u * 0.5)), v2303_M)
v2290_l.strokeStyle = (if (v2301_y) (if ((v2316_t == 0.0)) "#c9edff" else (if ((v2316_t == 1.0)) "#4b9de8" else "#9b8cff")) else (if ((v2316_t == 0.0)) "#c8fff7" else (if ((v2316_t == 1.0)) "#52f1e3" else "#b3a7ff")))
v2290_l.lineWidth = (if ((v2316_t == 0.0)) 3.0 else 1.5)
v2290_l.globalAlpha = (0.85 - (v2316_t * 0.12))
v2290_l.stroke()
v2316_t++
}
}
v2290_l.globalAlpha = 1.0
run {
var v2320_t: Double = -1.0
L325@ while ((v2320_t <= 1.0)) {
run {
var v2321_u: Double = 0.0
L326@ while ((v2321_u < 3.0)) {
val v2322_g: Double = (v2293_b + ((v2320_t * v2291_a) * (0.29 + (v2321_u * 0.095))))
val v2323_P: Double = (v2292_s * (0.18 + ((2.0 - v2321_u) * 0.045)))
val v2324_d: Double = (v2294_e - (v2323_P * 0.35))
v2290_l.fillStyle = (if (v2301_y) (if (truthy((v2321_u % 2.0))) "#0a294b" else "#123e65") else (if (truthy((v2321_u % 2.0))) "#155a70" else "#1b6b7b"))
v2290_l.fillRect((v2322_g - (v2291_a * 0.012)), v2324_d, (v2291_a * 0.024), v2323_P)
v2290_l.strokeStyle = (if (v2301_y) (if (truthy((v2321_u % 2.0))) "#458bd6" else "#82caff") else (if (truthy((v2321_u % 2.0))) "#75fff1" else "#9eeeff"))
v2290_l.lineWidth = 1.2
v2290_l.strokeRect((v2322_g - (v2291_a * 0.012)), v2324_d, (v2291_a * 0.024), v2323_P)
v2290_l.fillStyle = (if (v2301_y) "#a4d8ff" else "#b4fff4")
v2290_l.globalAlpha = 0.48
v2290_l.fillRect((v2322_g - (v2291_a * 0.018)), (v2324_d - (v2292_s * 0.012)), (v2291_a * 0.036), (v2292_s * 0.018))
v2290_l.globalAlpha = 1.0
v2321_u++
}
}
v2320_t += 2.0
}
}
val v2306_w: Grad = v2290_l.createLinearGradient(0.0, v2294_e, 0.0, v2292_s)
v2306_w.addColorStop(0.0, (if (v2301_y) "rgba(38,117,190,0.18)" else "rgba(42,220,207,0.16)"))
v2306_w.addColorStop(0.5, (if (v2301_y) "rgba(11,57,106,0.34)" else "rgba(8,122,148,0.30)"))
v2306_w.addColorStop(1.0, (if (v2301_y) "rgba(1,8,26,0.88)" else "rgba(3,37,65,0.78)"))
v2290_l.fillStyle = v2306_w
v2290_l.fillRect(0.0, v2294_e, v2291_a, (v2292_s - v2294_e))
val v2307_G: Double = 8.0
val v2308_I: Double = 10.0
val v2309_A: Double = (v2296_E % 1.0)
run {
var v2325_t: Double = 0.0
L327@ while ((v2325_t < v2307_G)) {
val v2326_u: Double = ((v2325_t + v2309_A) / v2307_G)
val v2327_g: Double = (((v2325_t + 1.0) + v2309_A) / v2307_G)
val v2328_P: Double = Math.pow(v2326_u, 1.72)
val v2329_d: Double = Math.pow(v2327_g, 1.72)
val v2330_o: Double = (v2294_e + (v2328_P * (v2292_s - v2294_e)))
val v2331_n: Double = (v2294_e + (v2329_d * (v2292_s - v2294_e)))
val v2332_p: Double = (v2291_a * (0.035 + (v2328_P * 0.16)))
val v2333_T: Double = (v2291_a * (0.035 + (v2329_d * 0.16)))
run {
var v2334_C: Double = (-v2308_I / 2.0)
L328@ while ((v2334_C < (v2308_I / 2.0))) {
val v2335_S_: Double = (v2293_b + ((v2334_C * v2332_p) / (v2308_I / 2.0)))
val v2336_q: Double = (v2293_b + (((v2334_C + 1.0) * v2332_p) / (v2308_I / 2.0)))
val v2337_F: Double = (v2293_b + (((v2334_C + 1.0) * v2333_T) / (v2308_I / 2.0)))
val v2338_O: Double = (v2293_b + ((v2334_C * v2333_T) / (v2308_I / 2.0)))
v2290_l.beginPath()
v2290_l.moveTo(v2335_S_, v2330_o)
v2290_l.lineTo(v2336_q, v2330_o)
v2290_l.lineTo(v2337_F, v2331_n)
v2290_l.lineTo(v2338_O, v2331_n)
v2290_l.closePath()
v2290_l.fillStyle = (if (v2301_y) (if (truthy((jsInt((v2325_t + v2334_C)) and jsInt(1.0)).toDouble())) "rgba(75,154,255,0.12)" else "rgba(160,210,255,0.14)") else (if (truthy((jsInt((v2325_t + v2334_C)) and jsInt(1.0)).toDouble())) "rgba(104,255,238,0.10)" else "rgba(193,255,248,0.13)"))
v2290_l.fill()
v2290_l.strokeStyle = (if (v2301_y) "rgba(112,188,255,0.42)" else "rgba(131,255,245,0.42)")
v2290_l.lineWidth = 0.8
v2290_l.stroke()
v2334_C++
}
}
v2325_t++
}
}
v2290_l.strokeStyle = (if (v2301_y) "rgba(120,190,255,0.48)" else "rgba(190,255,250,0.52)")
v2290_l.lineWidth = 1.1
run {
var v2339_t: Double = 0.0
L329@ while ((v2339_t < 8.0)) {
val v2340_u: Double = (v2294_e + (v2292_s * (0.14 + (v2339_t * 0.092))))
v2290_l.globalAlpha = (0.22 + (v2298_U_ * 0.16))
v2290_l.beginPath()
run {
var v2341_g: Double = 0.0
L330@ while ((v2341_g <= 12.0)) {
val v2342_P: Double = ((v2341_g * v2291_a) / 12.0)
val v2343_d: Double = (v2340_u + (kotlin.math.sin((((v2341_g * 0.8) + (v2295_S * 1.8)) + v2339_t)) * (2.0 + (v2297_R * 4.0))))
if ((v2341_g == 0.0)) {
v2290_l.moveTo(v2342_P, v2343_d)
} else {
v2290_l.lineTo(v2342_P, v2343_d)
}
v2341_g++
}
}
v2290_l.stroke()
v2339_t++
}
}
v2290_l.globalAlpha = 1.0
val v2310_m: Double = jsMin(v2291_a, v2292_s)
val v2311_i: Any? = jsArr(jsObj("x" to v2293_b, "y" to (v2294_e + ((v2292_s - v2294_e) * 0.43)), "r" to (v2310_m * 0.092), "color" to (if (v2301_y) "#8cc8ff" else "#ff8bd2"), "tilt" to -0.04), jsObj("x" to (v2293_b - (v2291_a * 0.25)), "y" to (v2294_e + ((v2292_s - v2294_e) * 0.63)), "r" to (v2310_m * 0.068), "color" to (if (v2301_y) "#72aaff" else "#8afff1"), "tilt" to 0.12), jsObj("x" to (v2293_b + (v2291_a * 0.25)), "y" to (v2294_e + ((v2292_s - v2294_e) * 0.62)), "r" to (v2310_m * 0.068), "color" to (if (v2301_y) "#b3a4ff" else "#ffe98b"), "tilt" to -0.1), jsObj("x" to (v2293_b + ((kotlin.math.sin((v2295_S * 0.26)) * v2291_a) * 0.045)), "y" to (v2294_e + ((v2292_s - v2294_e) * 0.78)), "r" to (v2310_m * 0.048), "color" to (if (v2301_y) "#b9eaff" else "#bca4ff"), "tilt" to 0.06))
js_forEach(v2311_i) L331@{ v2344_t: Any?, v2345_u: Double, _ ->

val v2346_g: Double = (kotlin.math.sin(((v2295_S * 1.8) + (v2345_u * 1.4))) * (3.0 + (v2297_R * 3.0)))
v2290_l.save()
v2290_l.translate(num(jsGet(v2344_t, "x")), (num(jsGet(v2344_t, "y")) + v2346_g))
v2290_l.rotate(num(jsGet(v2344_t, "tilt")))
v2290_l.strokeStyle = jsGet(v2344_t, "color")
v2290_l.lineWidth = jsMax(4.0, (v2310_m * 0.012))
v2290_l.beginPath()
v2290_l.ellipse(0.0, 0.0, (num(jsGet(v2344_t, "r")) * (1.0 + (v2297_R * 0.06))), (num(jsGet(v2344_t, "r")) * 0.34), 0.0, 0.0, (Math.PI * 2.0))
v2290_l.stroke()
v2290_l.strokeStyle = "#edffff"
v2290_l.lineWidth = jsMax(1.0, (v2310_m * 0.0028))
v2290_l.beginPath()
v2290_l.ellipse(0.0, -1.0, (num(jsGet(v2344_t, "r")) * (1.0 + (v2297_R * 0.06))), (num(jsGet(v2344_t, "r")) * 0.34), 0.0, 0.0, (Math.PI * 2.0))
v2290_l.stroke()
v2290_l.restore()
null
}
}

var g_continuousScroll: Double = 0.0

var g_smoothBass: Double = 0.0

var g_smoothMid: Double = 0.0

var g_smoothHigh: Double = 0.0

var g_glitchIntensity: Double = 0.0

var g_wpPostScroll: Double = 0.0

var g_rrWpPrevId: Double = 0.0

fun rrWallpaperTrigger(v2347_e: Double): Unit {

g_glitchIntensity = jsMin(1.3, jsMax(g_glitchIntensity, v2347_e))
}

fun rrAudioFrame(v2348_d: Double): Unit {

var v2349_C: Double = 0.0
var v2350_E: Double = 0.0
var v2351_k: Double = 0.0
run {
var v2352_q: Double = 0.0
L332@ while ((v2352_q < 16.0)) {
v2349_C += Rt.freqDataArr[jsIdx(v2352_q)]
v2352_q++
}
}
run {
var v2353_q: Double = 16.0
L333@ while ((v2353_q < 64.0)) {
v2350_E += Rt.freqDataArr[jsIdx(v2353_q)]
v2353_q++
}
}
run {
var v2354_q: Double = 64.0
L334@ while ((v2354_q < 128.0)) {
v2351_k += Rt.freqDataArr[jsIdx(v2354_q)]
v2354_q++
}
}
g_smoothBass += (((v2349_C / (16.0 * 255.0)) - g_smoothBass) * 0.16)
g_smoothMid += (((v2350_E / (48.0 * 255.0)) - g_smoothMid) * 0.16)
g_smoothHigh += (((v2351_k / (64.0 * 255.0)) - g_smoothHigh) * 0.16)
g_continuousScroll += (v2348_d * (0.7 + (g_smoothBass * 0.35)))
}

fun rrSmooth(v2355_i: Double): Double {

return (if ((v2355_i == 0.0)) g_smoothBass else (if ((v2355_i == 1.0)) g_smoothMid else (if ((v2355_i == 2.0)) g_smoothHigh else g_continuousScroll)))
return Double.NaN
}

fun rrWallpaperFrame(v2356_bgCtx: Ctx, v2357_P: Double, v2358_I: Double, v2359_y: Double, v2360_d: Double, v2361_wp: Double, v2362_c: Any?, v2363_L: Double): Unit {

val v2364_U_: Double = ((v2358_I * 0.56) + (Rt.mouseY * 10.0))
val v2365_B: Double = ((v2357_P * 0.5) + (Rt.mouseX * 18.0))
if ((((((((((v2363_L >= 2.0) || (v2361_wp == 14.0)) || (v2361_wp == 16.0)) || (v2361_wp == 18.0)) || (v2361_wp == 19.0)) || (v2361_wp == 20.0)) || (v2361_wp == 21.0)) || (v2361_wp == 22.0)) || (v2361_wp == 23.0))) {
rrPostFrame(v2356_bgCtx, v2357_P, v2358_I, v2359_y, v2360_d, v2361_wp, v2362_c, v2363_L)
g_glitchIntensity = 0.0
return
}
val v2366_U: Grad = v2356_bgCtx.createLinearGradient(0.0, 0.0, 0.0, v2364_U_)
v2366_U.addColorStop(0.0, jsGet(jsGet(v2362_c, "sky"), 0.0))
v2366_U.addColorStop(0.55, jsGet(jsGet(v2362_c, "sky"), 1.0))
v2366_U.addColorStop(0.82, jsGet(jsGet(v2362_c, "sky"), 2.0))
v2366_U.addColorStop(0.95, jsGet(jsGet(v2362_c, "sky"), 3.0))
v2366_U.addColorStop(1.0, jsGet(jsGet(v2362_c, "sky"), 4.0))
v2356_bgCtx.fillStyle = v2366_U
v2356_bgCtx.fillRect(0.0, 0.0, v2357_P, v2358_I)
if (truthy(jsGet(g_bgLayers, "starWarp"))) {
v2356_bgCtx.fillStyle = "#ffffff"
js_forEach(g_STARS) L335@{ v2367_V: Any?, v2368_D: Double, _ ->

if (truthy(jsAnd((v2363_L == 1.0)) { (v2368_D % 2.0) })) {
return@L335 null
}
jsOpAssign(v2367_V, "z", "-", (v2360_d * (80.0 + (g_smoothBass * 260.0))))
if ((num(jsGet(v2367_V, "z")) <= 10.0)) {
jsSet(v2367_V, "z", 1000.0)
jsSet(v2367_V, "x", ((Rt.random() - 0.5) * 2000.0))
jsSet(v2367_V, "y", ((Rt.random() - 0.5) * 1200.0))
}
val v2369_K: Double = (v2365_B + ((num(jsGet(v2367_V, "x")) / num(jsGet(v2367_V, "z"))) * 500.0))
val v2370_S_: Double = ((v2364_U_ * 0.45) + ((num(jsGet(v2367_V, "y")) / num(jsGet(v2367_V, "z"))) * 500.0))
if (((((v2369_K > 0.0) && (v2369_K < v2357_P)) && (v2370_S_ > 0.0)) && (v2370_S_ < v2364_U_))) {
val v2371_z: Double = jsMax(0.8, ((1.0 - (num(jsGet(v2367_V, "z")) / 1000.0)) * 2.8))
v2356_bgCtx.fillRect(v2369_K, v2370_S_, v2371_z, v2371_z)
}
null
}
}
S336@ do {
when (v2361_wp) {
0.0 -> {
renderScene0_SunsetOutrun(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
1.0 -> {
renderScene1_CyberCity(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
2.0 -> {
renderScene2_HeliosMall(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
3.0 -> {
renderScene3_TwinMoonOcean(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
4.0 -> {
renderScene4_FujiCanyon(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
5.0 -> {
renderScene5_HyperAtrium(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
6.0 -> {
renderScene6_ArcadeOdyssey(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
7.0 -> {
renderScene7_PoseidonSpa(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
8.0 -> {
renderScene8_ChronoDrift(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
9.0 -> {
renderScene9_BackroomsLevel0(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
10.0 -> {
renderScene10_BackroomsHalls(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
11.0 -> {
renderScene11_PoolroomsSpa(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
12.0 -> {
renderScene12_PoolroomsAbyss(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
13.0 -> {
renderScene13_NeonRainAlley(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
15.0 -> {
renderScene15_ChromeCathedral(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
17.0 -> {
renderScene17_LiminalAtrium(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
24.0 -> {
renderScene24_AetherSpires(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
25.0 -> {
renderScene25_PrismCitadel(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
26.0 -> {
renderScene26_CelestialAtrium(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
27.0 -> {
renderScene27_TidalVault(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
28.0 -> {
renderScene28_ChronosphereHall(v2356_bgCtx, v2357_P, v2358_I, v2365_B, v2364_U_, v2359_y, g_continuousScroll, g_smoothBass, g_smoothMid, v2362_c)
}
}
} while (false)
if ((v2363_L < 2.0)) {
renderExtraBackgroundDepth(v2356_bgCtx, v2357_P, v2358_I, v2359_y, g_smoothBass, g_smoothMid, g_smoothHigh, v2362_c, v2365_B, v2364_U_)
}
if (truthy(jsGet(g_bgLayers, "cyberRain"))) {
v2356_bgCtx.strokeStyle = jsGet(v2362_c, "primary")
js_forEach(g_RAIN_DROPS) L337@{ v2372_V: Any?, v2373_D: Double, _ ->

if (truthy(jsAnd((v2363_L == 1.0)) { (v2373_D % 2.0) })) {
return@L337 null
}
jsAddAssign(v2372_V, "y", ((v2360_d * (0.42 + (g_smoothHigh * 0.9))) * num(jsGet(v2372_V, "z"))))
if ((num(jsGet(v2372_V, "y")) > 1.0)) {
jsSet(v2372_V, "y", -0.05)
jsSet(v2372_V, "x", Rt.random())
}
val v2374_K: Double = (num(jsGet(v2372_V, "x")) * v2357_P)
val v2375_S_: Double = (num(jsGet(v2372_V, "y")) * v2358_I)
v2356_bgCtx.globalAlpha = (num(jsGet(v2372_V, "z")) * 0.5)
v2356_bgCtx.lineWidth = (num(jsGet(v2372_V, "z")) * 1.8)
v2356_bgCtx.beginPath()
v2356_bgCtx.moveTo(v2374_K, v2375_S_)
v2356_bgCtx.lineTo((v2374_K - (Rt.mouseX * 4.0)), (v2375_S_ + (num(jsGet(v2372_V, "len")) * num(jsGet(v2372_V, "z")))))
v2356_bgCtx.stroke()
null
}
v2356_bgCtx.globalAlpha = 1.0
}
rrRipples(v2356_bgCtx, v2362_c)
if ((g_glitchIntensity > 0.02)) {
val v2376_V: Double = g_glitchIntensity
g_glitchIntensity *= 0.8
v2356_bgCtx.save()
val v2377_D: Double = (4.0 + kotlin.math.floor((v2376_V * 4.0)))
run {
var v2378_K: Double = 0.0
L338@ while ((v2378_K < v2377_D)) {
val v2379_S_: Double = (Rt.random() * v2358_I)
val v2380_z: Double = (12.0 + (Rt.random() * 48.0))
val v2381_X: Double = (((Rt.random() - 0.5) * 68.0) * v2376_V)
v2356_bgCtx.shiftBand(0.0, v2379_S_, v2357_P, v2380_z, v2381_X)
v2378_K++
}
}
v2356_bgCtx.globalCompositeOperation = "screen"
run {
var v2382_K: Double = 0.0
L339@ while ((v2382_K < 3.0)) {
val v2383_S_: Double = (Rt.random() * v2358_I)
val v2384_z: Double = (4.0 + (Rt.random() * 18.0))
v2356_bgCtx.fillStyle = (if (((v2382_K % 2.0) == 0.0)) ("rgba(" + str(jsGet(v2362_c, "waveRgb")) + ", " + jsToFixed((0.35 * v2376_V), 3.0) + ")") else ("rgba(" + str(jsGet(v2362_c, "altWaveRgb")) + ", " + jsToFixed((0.35 * v2376_V), 3.0) + ")"))
v2356_bgCtx.fillRect(0.0, v2383_S_, v2357_P, v2384_z)
v2382_K++
}
}
v2356_bgCtx.restore()
} else {
g_glitchIntensity = 0.0
}
}

fun rrRipples(v2385_e: Ctx, v2386_M: Any?): Unit {

run {
var v2387_q: Double = (jsLen(g_clickRipples) - 1.0)
L340@ while ((v2387_q >= 0.0)) {
val v2388_S: Any? = jsGet(g_clickRipples, v2387_q)
jsAddAssign(v2388_S, "r", 5.0)
jsOpAssign(v2388_S, "alpha", "-", 0.025)
if ((num(jsGet(v2388_S, "alpha")) > 0.0)) {
v2385_e.strokeStyle = (((("rgba(" + str(jsGet(v2386_M, "waveRgb"))) + ", ") + jsToFixed(num(jsGet(v2388_S, "alpha")), 3.0)) + ")")
v2385_e.lineWidth = 2.0
v2385_e.beginPath()
v2385_e.arc(num(jsGet(v2388_S, "x")), num(jsGet(v2388_S, "y")), num(jsGet(v2388_S, "r")), 0.0, (Math.PI * 2.0))
v2385_e.stroke()
} else {
jsSplice(g_clickRipples, v2387_q, 1.0)
}
v2387_q--
}
}
v2385_e.globalAlpha = 1.0
}

fun rrAddRipple(v2389_x: Double, v2390_y: Double): Unit {

jsPush(g_clickRipples, jsObj("x" to v2389_x, "y" to v2390_y, "r" to 0.0, "alpha" to 1.0))
}

fun rrPostFrame(v2391_D: Ctx, v2392_h: Double, v2393_M: Double, v2394_U_: Double, v2395_R: Double, v2396_a: Double, v2397_k: Any?, v2398_s: Double): Unit {

var v2399_g: Double = 0.0
var v2400_m: Any? = null
var v2401_dd: Double = 0.0
var v2402_yy: Double = 0.0
val v2403_b: Boolean = (v2398_s >= 2.0)
val v2404_f: Double = g_smoothBass
val v2405_r: Double = g_smoothMid
val v2406_y: Double = g_smoothHigh
g_wpPostScroll += (jsMin(0.05, jsMax(0.001, v2395_R)) * (1.55 + (v2404_f * 1.4)))
val v2407_Q: Double = g_wpPostScroll
val v2408_v: Double = ((v2392_h * 0.5) + (Rt.mouseX * 18.0))
val v2409_w: Double = ((v2393_M * 0.52) + (Rt.mouseY * 10.0))
if (((v2396_a == 20.0) || (v2403_b && (((v2396_a == 0.0) || (v2396_a == 4.0)) || (v2396_a == 24.0))))) {
wp_ol(v2391_D, v2392_h, v2393_M, v2408_v, v2409_w, v2394_U_, v2407_Q, v2404_f, v2405_r, v2397_k)
} else {
if (((v2396_a == 21.0) || (v2403_b && ((((((v2396_a == 1.0) || (v2396_a == 6.0)) || (v2396_a == 8.0)) || (v2396_a == 13.0)) || (v2396_a == 16.0)) || (v2396_a == 28.0))))) {
wp_al(v2391_D, v2392_h, v2393_M, v2408_v, v2409_w, v2394_U_, v2407_Q, v2404_f, v2405_r, v2397_k)
} else {
if (((v2396_a == 22.0) || (v2403_b && ((((((((v2396_a == 2.0) || (v2396_a == 5.0)) || (v2396_a == 9.0)) || (v2396_a == 10.0)) || (v2396_a == 15.0)) || (v2396_a == 17.0)) || (v2396_a == 25.0)) || (v2396_a == 26.0))))) {
wp_el(v2391_D, v2392_h, v2393_M, v2408_v, v2409_w, v2394_U_, v2407_Q, v2404_f, v2405_r, v2397_k)
} else {
if ((v2403_b && (v2396_a == 11.0))) {
wp_j(v2391_D, v2392_h, v2393_M, v2408_v, v2409_w, v2394_U_, v2407_Q, v2404_f, v2405_r, v2397_k, false)
} else {
if ((v2403_b && (v2396_a == 12.0))) {
wp_j(v2391_D, v2392_h, v2393_M, v2408_v, v2409_w, v2394_U_, v2407_Q, v2404_f, v2405_r, v2397_k, true)
} else {
if (((v2396_a == 23.0) || v2403_b)) {
wp_j(v2391_D, v2392_h, v2393_M, v2408_v, v2409_w, v2394_U_, v2407_Q, v2404_f, v2405_r, v2397_k, false)
} else {
if ((v2396_a == 14.0)) {
wp_Y(v2391_D, v2392_h, v2393_M, v2408_v, v2409_w, v2394_U_, v2407_Q, v2404_f, v2405_r, v2397_k)
} else {
if ((v2396_a == 16.0)) {
wp_z(v2391_D, v2392_h, v2393_M, v2408_v, v2409_w, v2394_U_, v2407_Q, v2404_f, v2405_r, v2397_k)
} else {
if ((v2396_a == 18.0)) {
wp_x(v2391_D, v2392_h, v2393_M, v2408_v, v2409_w, v2394_U_, v2407_Q, v2404_f, v2405_r, v2397_k)
} else {
if ((v2396_a == 19.0)) {
wp_ll(v2391_D, v2392_h, v2393_M, v2408_v, v2409_w, v2394_U_, v2407_Q, v2404_f, v2405_r, v2397_k)
}
}
}
}
}
}
}
}
}
}
if ((v2398_s < 2.0)) {
renderExtraBackgroundDepth(v2391_D, v2392_h, v2393_M, v2394_U_, v2404_f, v2405_r, v2406_y, v2397_k, v2408_v, v2409_w)
if (truthy(jsGet(g_bgLayers, "cyberRain"))) {
v2391_D.strokeStyle = jsGet(v2397_k, "primary")
v2391_D.beginPath()
run {
v2399_g = 0.0
L341@ while ((v2399_g < jsLen(g_RAIN_DROPS))) {
L341c@ do {
v2400_m = jsGet(g_RAIN_DROPS, v2399_g)
jsAddAssign(v2400_m, "y", ((v2395_R * (0.42 + (v2406_y * 0.9))) * num(jsGet(v2400_m, "z"))))
if ((num(jsGet(v2400_m, "y")) > 1.0)) {
jsSet(v2400_m, "y", -0.05)
jsSet(v2400_m, "x", Rt.random())
}
if (truthy(jsAnd((v2398_s == 1.0)) { (v2399_g % 2.0) })) {
break@L341c
}
v2401_dd = (num(jsGet(v2400_m, "x")) * v2392_h)
v2402_yy = (num(jsGet(v2400_m, "y")) * v2393_M)
v2391_D.globalAlpha = (num(jsGet(v2400_m, "z")) * (if ((v2398_s == 1.0)) 0.28 else 0.5))
v2391_D.lineWidth = (num(jsGet(v2400_m, "z")) * 1.8)
v2391_D.beginPath()
v2391_D.moveTo(v2401_dd, v2402_yy)
v2391_D.lineTo((v2401_dd - (Rt.mouseX * 4.0)), (v2402_yy + (num(jsGet(v2400_m, "len")) * num(jsGet(v2400_m, "z")))))
v2391_D.stroke()
} while (false)
v2399_g++
}
}
v2391_D.globalAlpha = 1.0
}
rrRipples(v2391_D, v2397_k)
}
}

fun rrThemeByKey(v2410_key: String): Any? {

return jsOr(jsGet(g_THEMES, v2410_key)) { jsGet(g_THEMES, "vaporwave") }
return null
}

fun rrSetLayer(v2411_key: String, v2412_on: Boolean): Unit {

jsSet(g_bgLayers, v2411_key, v2412_on)
}

fun SiteWallpapersInit() {
g_THEMES = jsObj("vaporwave" to jsObj("label" to "VAPOR", "sky" to jsArr("#0d0428", "#1e1042", "#4a1d66", "#ff68a8", "#ffd2a6"), "sun" to jsArr("#d834ff", "#b266ff", "#c2ffff"), "sunSlice" to "#250d49", "mtnTop" to "#a300b8", "mtnBot" to "#22033d", "mtnStroke" to "#ff00c8", "floor" to jsArr("#170432", "#281060", "#076885"), "grid" to "rgba(130,245,255,0.65)", "waveRgb" to "0, 247, 255", "altWaveRgb" to "255, 0, 160", "primary" to "#00f7ff", "secondary" to "#ff00a0", "accent" to "#ffff00", "strips" to jsArr("#240014", "#ff1e00", "#ff9900", "#ff0055", "#00f7ff")), "bw" to jsObj("label" to "B&W NOIR", "sky" to jsArr("#000000", "#121212", "#2b2b2b", "#777777", "#e0e0e0"), "sun" to jsArr("#ffffff", "#bbbbbb", "#555555"), "sunSlice" to "#0a0a0a", "mtnTop" to "#888888", "mtnBot" to "#111111", "mtnStroke" to "#ffffff", "floor" to jsArr("#050505", "#1b1b1b", "#383838"), "grid" to "rgba(255,255,255,0.65)", "waveRgb" to "255, 255, 255", "altWaveRgb" to "180, 180, 180", "primary" to "#ffffff", "secondary" to "#aaaaaa", "accent" to "#dcdcdc", "strips" to jsArr("#000000", "#ffffff", "#888888", "#444444", "#ffffff")), "matrix" to jsObj("label" to "MATRIX", "sky" to jsArr("#000800", "#001800", "#00300a", "#00661a", "#66ff88"), "sun" to jsArr("#00ff66", "#00b844", "#004411"), "sunSlice" to "#001100", "mtnTop" to "#009933", "mtnBot" to "#001a06", "mtnStroke" to "#00ff66", "floor" to jsArr("#000d02", "#002609", "#004d14"), "grid" to "rgba(0,255,102,0.60)", "waveRgb" to "0, 255, 102", "altWaveRgb" to "120, 255, 160", "primary" to "#00ff66", "secondary" to "#88ffaa", "accent" to "#ffffff", "strips" to jsArr("#001100", "#00ff66", "#00aa33", "#005511", "#88ffaa")), "outrun" to jsObj("label" to "OUTRUN", "sky" to jsArr("#17020a", "#4a0714", "#8c1820", "#e04416", "#ffd166"), "sun" to jsArr("#fff07a", "#ff7b24", "#e52b36"), "sunSlice" to "#320711", "mtnTop" to "#b51d35", "mtnBot" to "#21040a", "mtnStroke" to "#ffe45e", "floor" to jsArr("#18030a", "#4a0714", "#852018"), "grid" to "rgba(255,228,94,0.72)", "waveRgb" to "255, 228, 94", "altWaveRgb" to "255, 59, 48", "primary" to "#ffe45e", "secondary" to "#ff3b30", "accent" to "#fff0d6", "strips" to jsArr("#21040a", "#8c1820", "#e04416", "#ff7b24", "#ffe45e")), "ice" to jsObj("label" to "CYBER ICE", "sky" to jsArr("#010a17", "#041d3d", "#0b3c6e", "#3898ff", "#d4f5ff"), "sun" to jsArr("#ffffff", "#80e5ff", "#0066cc"), "sunSlice" to "#03162e", "mtnTop" to "#1a75ff", "mtnBot" to "#031126", "mtnStroke" to "#aaffff", "floor" to jsArr("#020c1c", "#072447", "#0e4d7a"), "grid" to "rgba(170,255,255,0.65)", "waveRgb" to "140, 245, 255", "altWaveRgb" to "0, 140, 255", "primary" to "#aaffff", "secondary" to "#0088ff", "accent" to "#ffffff", "strips" to jsArr("#021024", "#0066ff", "#00bbff", "#66e0ff", "#ffffff")), "sakura" to jsObj("label" to "SAKURA 95", "sky" to jsArr("#1a0518", "#3d0e36", "#7a1f62", "#ff77aa", "#ffe6f2"), "sun" to jsArr("#fff0f5", "#ff85b3", "#d9006c"), "sunSlice" to "#2e0827", "mtnTop" to "#d63384", "mtnBot" to "#29051f", "mtnStroke" to "#ffb3d9", "floor" to jsArr("#1f0419", "#4a0e3a", "#8c2667"), "grid" to "rgba(255,200,230,0.65)", "waveRgb" to "255, 182, 217", "altWaveRgb" to "255, 51, 153", "primary" to "#ffb3d9", "secondary" to "#ff3399", "accent" to "#fff5fa", "strips" to jsArr("#2b0520", "#ff3385", "#ff80b3", "#ffccf2", "#ffffff")), "acid" to jsObj("label" to "ACID 303", "sky" to jsArr("#09001a", "#1b0044", "#2e0066", "#55aa00", "#ccff00"), "sun" to jsArr("#ffff00", "#99ff00", "#00cc66"), "sunSlice" to "#160033", "mtnTop" to "#66cc00", "mtnBot" to "#12002b", "mtnStroke" to "#ccff00", "floor" to jsArr("#0d0021", "#22004d", "#1c4d00"), "grid" to "rgba(204,255,0,0.65)", "waveRgb" to "204, 255, 0", "altWaveRgb" to "0, 255, 153", "primary" to "#ccff00", "secondary" to "#00ff99", "accent" to "#ffffff", "strips" to jsArr("#14002e", "#7700ff", "#00ff88", "#aaff00", "#ffff00")), "amber" to jsObj("label" to "AMBER CRT", "sky" to jsArr("#0f0600", "#261100", "#4d2200", "#b35900", "#ffcc66"), "sun" to jsArr("#fff0b3", "#ffaa00", "#993d00"), "sunSlice" to "#1f0d00", "mtnTop" to "#cc6600", "mtnBot" to "#1a0900", "mtnStroke" to "#ffbb33", "floor" to jsArr("#120700", "#2e1400", "#592800"), "grid" to "rgba(255,176,0,0.65)", "waveRgb" to "255, 176, 0", "altWaveRgb" to "255, 102, 0", "primary" to "#ffb000", "secondary" to "#ff6600", "accent" to "#ffe699", "strips" to jsArr("#1a0a00", "#803300", "#cc5200", "#ff9900", "#ffd27f")), "mallsoft" to jsObj("label" to "MALLSOFT", "sky" to jsArr("#10212b", "#1f4252", "#4a7c8c", "#f299a9", "#fff0e6"), "sun" to jsArr("#ffffff", "#ffd1dc", "#7fffd4"), "sunSlice" to "#193340", "mtnTop" to "#5ca8a8", "mtnBot" to "#142933", "mtnStroke" to "#a6ffea", "floor" to jsArr("#122630", "#244d5c", "#43808c"), "grid" to "rgba(166,255,234,0.68)", "waveRgb" to "127, 255, 212", "altWaveRgb" to "255, 141, 161", "primary" to "#7fffd4", "secondary" to "#ff8da1", "accent" to "#ffffff", "strips" to jsArr("#162d38", "#4ca6a6", "#7fffd4", "#ff99aa", "#fff5eb")), "midnight" to jsObj("label" to "MIDNIGHT", "sky" to jsArr("#05000a", "#120024", "#29004a", "#800033", "#ff0055"), "sun" to jsArr("#ff0055", "#9900ff", "#00e5ff"), "sunSlice" to "#0d001a", "mtnTop" to "#77003c", "mtnBot" to "#0c0017", "mtnStroke" to "#ff0055", "floor" to jsArr("#07000f", "#190033", "#380030"), "grid" to "rgba(255,0,85,0.65)", "waveRgb" to "255, 0, 85", "altWaveRgb" to "0, 229, 255", "primary" to "#ff0055", "secondary" to "#00e5ff", "accent" to "#d580ff", "strips" to jsArr("#080012", "#4a0072", "#99004d", "#ff0055", "#00e5ff")), "synthwave" to jsObj("label" to "SYNTHWAVE", "sky" to jsArr("#09051f", "#21104b", "#542080", "#a02cff", "#ff4fa3"), "sun" to jsArr("#ff70c8", "#b45cff", "#37d9ff"), "sunSlice" to "#160a35", "mtnTop" to "#8e35d8", "mtnBot" to "#100726", "mtnStroke" to "#00f5ff", "floor" to jsArr("#09051f", "#21104b", "#3d176b"), "grid" to "rgba(0,245,255,0.76)", "waveRgb" to "0, 245, 255", "altWaveRgb" to "255, 60, 172", "primary" to "#00f5ff", "secondary" to "#ff3cac", "accent" to "#ffd6ff", "strips" to jsArr("#100726", "#542080", "#a02cff", "#ff3cac", "#00f5ff")), "toxic" to jsObj("label" to "TOXIC", "sky" to jsArr("#030d0b", "#09241b", "#17472a", "#5f8f20", "#c8ff00"), "sun" to jsArr("#eaff70", "#9bff00", "#00e5a8"), "sunSlice" to "#071711", "mtnTop" to "#3d7a32", "mtnBot" to "#04120e", "mtnStroke" to "#baff00", "floor" to jsArr("#030d0b", "#09241b", "#17472a"), "grid" to "rgba(186,255,0,0.68)", "waveRgb" to "186, 255, 0", "altWaveRgb" to "0, 229, 168", "primary" to "#baff00", "secondary" to "#00e5a8", "accent" to "#e5ffc2", "strips" to jsArr("#04120e", "#17472a", "#3d7a32", "#9bff00", "#00e5a8")), "ocean" to jsObj("label" to "DEEP OCEAN", "sky" to jsArr("#030817", "#101b3a", "#242c63", "#4b4e9b", "#c07bce"), "sun" to jsArr("#f2b5d4", "#c07bce", "#6e68c9"), "sunSlice" to "#0c102b", "mtnTop" to "#47458c", "mtnBot" to "#080d24", "mtnStroke" to "#d5a6ff", "floor" to jsArr("#05091b", "#151d42", "#30295f"), "grid" to "rgba(181,166,255,0.65)", "waveRgb" to "181, 166, 255", "altWaveRgb" to "75, 78, 155", "primary" to "#b5a6ff", "secondary" to "#f2a7d8", "accent" to "#e8e5ff", "strips" to jsArr("#080d24", "#30295f", "#6e68c9", "#b5a6ff", "#f2a7d8")), "crimson" to jsObj("label" to "CRIMSON", "sky" to jsArr("#180009", "#3d0010", "#70001e", "#c00038", "#ff6b81"), "sun" to jsArr("#ffb3c1", "#ff1744", "#8f0030"), "sunSlice" to "#32000d", "mtnTop" to "#c00038", "mtnBot" to "#21000a", "mtnStroke" to "#ffb3c1", "floor" to jsArr("#180009", "#430015", "#70001e"), "grid" to "rgba(255,107,129,0.65)", "waveRgb" to "255, 23, 68", "altWaveRgb" to "255, 179, 193", "primary" to "#ffb3c1", "secondary" to "#ff1744", "accent" to "#ffffff", "strips" to jsArr("#21000a", "#8f0030", "#c00038", "#ff1744", "#ffb3c1")), "lavender" to jsObj("label" to "LAVENDER", "sky" to jsArr("#100d25", "#29204b", "#514078", "#9b7bff", "#ffb8e8"), "sun" to jsArr("#f4eaff", "#c5a3ff", "#ff9de2"), "sunSlice" to "#21163d", "mtnTop" to "#9b7bff", "mtnBot" to "#17102d", "mtnStroke" to "#d9ccff", "floor" to jsArr("#100d25", "#30245a", "#514078"), "grid" to "rgba(217,204,255,0.68)", "waveRgb" to "217, 204, 255", "altWaveRgb" to "255, 157, 226", "primary" to "#d9ccff", "secondary" to "#ff9de2", "accent" to "#ffffff", "strips" to jsArr("#17102d", "#7958b8", "#9b7bff", "#ff9de2", "#d9ccff")))
g_bgLayers = jsObj("gridWarp" to true, "solarRings" to true, "starWarp" to true, "skySpectrum" to true, "cyberRain" to true, "monoliths" to true, "depthMotes" to true, "parallaxRings" to false, "glassShards" to false, "auroraRibbons" to false, "cometTrails" to false, "lightShafts" to false, "depthRidges" to false)
g_clickRipples = jsArr()
g_STARS = jsArrayFrom(180.0) L0@{ _, _, _ ->

jsObj("x" to ((Rt.random() - 0.5) * 2000.0), "y" to ((Rt.random() - 0.5) * 1200.0), "z" to ((Rt.random() * 1000.0) + 50.0))
}
g_RAIN_DROPS = jsArrayFrom(75.0) L1@{ _, _, _ ->

jsObj("x" to Rt.random(), "y" to Rt.random(), "z" to (0.3 + (Rt.random() * 0.7)), "len" to (12.0 + (Rt.random() * 22.0)))
}
g_SAKURA_PETALS = jsArrayFrom(55.0) L2@{ _, _, _ ->

jsObj("x" to Rt.random(), "y" to Rt.random(), "z" to (0.4 + (Rt.random() * 0.8)))
}
g_CITY_BUILDINGS = null
g_BUST_RINGS = jsArr(jsObj("y" to -1.05, "rx" to 0.14, "rz" to 0.14), jsObj("y" to -0.92, "rx" to 0.32, "rz" to 0.34), jsObj("y" to -0.7, "rx" to 0.36, "rz" to 0.4), jsObj("y" to -0.46, "rx" to 0.33, "rz" to 0.38), jsObj("y" to -0.24, "rx" to 0.24, "rz" to 0.28), jsObj("y" to -0.05, "rx" to 0.19, "rz" to 0.2), jsObj("y" to 0.18, "rx" to 0.72, "rz" to 0.38), jsObj("y" to 0.46, "rx" to 0.56, "rz" to 0.34), jsObj("y" to 0.68, "rx" to 0.36, "rz" to 0.36), jsObj("y" to 0.9, "rx" to 0.54, "rz" to 0.54))
g_TESSERACT_VERTS = jsArr()
run {
var v482_e: Double = 0.0
L58@ while ((v482_e < 16.0)) {
jsPush(g_TESSERACT_VERTS, jsArr((if (truthy((jsInt(v482_e) and jsInt(1.0)).toDouble())) 1.0 else -1.0), (if (truthy((jsInt(v482_e) and jsInt(2.0)).toDouble())) 1.0 else -1.0), (if (truthy((jsInt(v482_e) and jsInt(4.0)).toDouble())) 1.0 else -1.0), (if (truthy((jsInt(v482_e) and jsInt(8.0)).toDouble())) 1.0 else -1.0)))
v482_e++
}
}
g_TESSERACT_EDGES = jsArr()
run {
var v483_e: Double = 0.0
L59@ while ((v483_e < 16.0)) {
run {
var v484_a: Double = (v483_e + 1.0)
L60@ while ((v484_a < 16.0)) {
val v485_o: Double = (jsInt(v483_e) xor jsInt(v484_a)).toDouble()
if ((truthy(v485_o) && !(truthy((jsInt(v485_o) and jsInt((v485_o - 1.0))).toDouble())))) {
jsPush(g_TESSERACT_EDGES, jsArr(v483_e, v484_a))
}
v484_a++
}
}
v483_e++
}
}
jsSet(g_THEMES, "backrooms", jsObj("label" to "BACKROOMS", "sky" to jsArr("#3d3514", "#5c5020", "#807030", "#b59f45", "#d9c66c"), "sun" to jsArr("#fff8bd", "#e3ca66", "#9c862b"), "sunSlice" to "#2e270d", "mtnTop" to "#9c8736", "mtnBot" to "#332b0d", "mtnStroke" to "#fae88c", "floor" to jsArr("#2b240b", "#4f4318", "#786626"), "grid" to "rgba(255, 240, 150, 0.45)", "waveRgb" to "255, 243, 148", "altWaveRgb" to "196, 164, 56", "primary" to "#fff394", "secondary" to "#c9a93b", "accent" to "#ffffff", "strips" to jsArr("#29220a", "#6e5d20", "#a68e36", "#d9be57", "#fff394")))
g_continuousScroll = 0.0
g_smoothBass = 0.0
g_smoothMid = 0.0
g_smoothHigh = 0.0
g_glitchIntensity = 0.0
g_wpPostScroll = 0.0
g_rrWpPrevId = -1.0
}
