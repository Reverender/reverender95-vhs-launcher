// GENERATED from the REVERENDER_95 website source by tools/tx.js — do not edit by hand.
@file:Suppress("NAME_SHADOWING", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "UNUSED_VALUE", "VARIABLE_WITH_REDUNDANT_INITIALIZER", "LocalVariableName", "FunctionName", "UNUSED_ANONYMOUS_PARAMETER", "UNNECESSARY_NOT_NULL_ASSERTION", "SENSELESS_COMPARISON", "UNREACHABLE_CODE", "KotlinConstantConditions", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNCHECKED_CAST", "USELESS_CAST", "RedundantExplicitType", "CanBeVal", "unused", "LiftReturnOrAssignment", "CascadeIf", "ReplaceWithOperatorAssignment", "ControlFlowWithEmptyBody", "ReplaceJavaStaticMethodWithKotlinAnalog", "ReplaceManualRangeWithIndicesCalls", "IntroduceWhenSubject", "RedundantIf", "SimplifyBooleanWithConstants")
package com.reverender.r95.ui.viz.siteos

import com.reverender.r95.ui.viz.js.*

fun U_(v21_e: Any? = null): Any? {

var v22_s: String = ""
var v23_l: Any? = null
var v24_h: String = ""
var v25_i: Double = 0.0
v22_s = ("" + str(jsInvoke(jsOrSD(v21_e) { "#000" }, "trim")))
if ((((v22_s.lowercase()).indexOf("rgb").toDouble() == 0.0) && (v22_s.indexOf("(").toDouble() > 0.0))) {
v23_l = js_map(jsSplit(jsSlice(v22_s, (v22_s.indexOf("(").toDouble() + 1.0), v22_s.indexOf(")").toDouble()), ",")) L0@{ v26_q: Any?, _, _ ->

return@L0 jsParseFloat(v26_q)
null
}
return jsArr((jsInt(num(jsGet(v23_l, 0.0))) or jsInt(0.0)).toDouble(), (jsInt(num(jsGet(v23_l, 1.0))) or jsInt(0.0)).toDouble(), (jsInt(num(jsGet(v23_l, 2.0))) or jsInt(0.0)).toDouble())
}
v24_h = ("" + str(jsInvoke(v22_s, "replace", "#", "")))
if ((v24_h.length.toDouble() == 3.0)) {
v24_h = (((((("" + jsCharAt(v24_h, 0.0)) + jsCharAt(v24_h, 0.0)) + jsCharAt(v24_h, 1.0)) + jsCharAt(v24_h, 1.0)) + jsCharAt(v24_h, 2.0)) + jsCharAt(v24_h, 2.0))
}
v25_i = jsOrN(jsParseInt(jsSlice(v24_h, 0.0, 6.0), 16.0)) { 0.0 }
return jsArr((jsInt((jsInt(v25_i) shr jsInt(16.0)).toDouble()) and jsInt(255.0)).toDouble(), (jsInt((jsInt(v25_i) shr jsInt(8.0)).toDouble()) and jsInt(255.0)).toDouble(), (jsInt(v25_i) and jsInt(255.0)).toDouble())
return null
}

fun U(v27_e: Any? = null, v28_c: Any? = null, v29_l: Double = Double.NaN): Any? {
var v27_e = v27_e
var v28_c = v28_c
v27_e = U_(v27_e)
v28_c = U_(v28_c)
return jsArr((num(jsGet(v27_e, 0.0)) + ((num(jsGet(v28_c, 0.0)) - num(jsGet(v27_e, 0.0))) * v29_l)), (num(jsGet(v27_e, 1.0)) + ((num(jsGet(v28_c, 1.0)) - num(jsGet(v27_e, 1.0))) * v29_l)), (num(jsGet(v27_e, 2.0)) + ((num(jsGet(v28_c, 2.0)) - num(jsGet(v27_e, 2.0))) * v29_l)))
return null
}

fun o(v30_e: Any? = null, v31_c: Double = Double.NaN): String {

return (if ((v31_c).isNaN()) (((((("rgb(" + jsStr((jsInt(num(jsGet(v30_e, 0.0))) or jsInt(0.0)).toDouble())) + ",") + jsStr((jsInt(num(jsGet(v30_e, 1.0))) or jsInt(0.0)).toDouble())) + ",") + jsStr((jsInt(num(jsGet(v30_e, 2.0))) or jsInt(0.0)).toDouble())) + ")") else (((((((("rgba(" + jsStr((jsInt(num(jsGet(v30_e, 0.0))) or jsInt(0.0)).toDouble())) + ",") + jsStr((jsInt(num(jsGet(v30_e, 1.0))) or jsInt(0.0)).toDouble())) + ",") + jsStr((jsInt(num(jsGet(v30_e, 2.0))) or jsInt(0.0)).toDouble())) + ",") + jsStr(v31_c)) + ")"))
return "undefined"
}

fun V(v32_e: Any? = null, v33_c: Any? = null, v34_l: Double = Double.NaN, v35_i: Double = Double.NaN): String {

return o(U(v32_e, v33_c, v34_l), v35_i)
return "undefined"
}

fun B(v36_e: Any? = null, v37_c: Double = Double.NaN): String {

return o(U_(v36_e), v37_c)
return "undefined"
}

fun se(v38_e: Double = Double.NaN): Any? {

var v39_c: Double = 0.0
v39_c = jsOrN(((jsInt(v38_e) ushr jsInt(0.0)).toLong() and 0xFFFFFFFFL).toDouble()) { 1.0 }
return JsFn L1@{ args ->

v39_c = (jsInt(v39_c) xor jsInt((jsInt(v39_c) shl jsInt(13.0)).toDouble())).toDouble()
v39_c = (jsInt(v39_c) xor jsInt(((jsInt(v39_c) ushr jsInt(17.0)).toLong() and 0xFFFFFFFFL).toDouble())).toDouble()
v39_c = (jsInt(v39_c) xor jsInt((jsInt(v39_c) shl jsInt(5.0)).toDouble())).toDouble()
return@L1 ((((jsInt(v39_c) ushr jsInt(0.0)).toLong() and 0xFFFFFFFFL).toDouble() % 100000.0) / 100000.0)
null
}
return null
}

fun ke(v40_e: Any? = null): Any? {

var v41_c: Any? = null
var v42_l: Double = 0.0
var v43_i: Double = 0.0
var v44_g: Any? = null
fun v45_d(v48_T: Double = Double.NaN, v49_M: Double = Double.NaN): Double {

return (num(jsGet(v41_c, (jsInt((num(jsGet(v41_c, (jsInt(v48_T) and jsInt(255.0)).toDouble())) + v49_M)) and jsInt(511.0)).toDouble())) / 255.0)
return Double.NaN
}
fun v46_f(v50_T: Double = Double.NaN): Double {

return ((v50_T * v50_T) * (3.0 - (2.0 * v50_T)))
return Double.NaN
}
fun v47_D(v51_T: Double = Double.NaN, v52_M: Double = Double.NaN): Double {

var v53_w: Double = 0.0
var v54_A: Double = 0.0
var v55_N: Double = 0.0
var v56_z: Double = 0.0
var v57_p: Double = 0.0
var v58_k: Double = 0.0
var v59_W: Double = 0.0
var v60_m: Double = 0.0
var v61_J: Double = 0.0
var v62_G: Double = 0.0
v53_w = kotlin.math.floor(v51_T)
v54_A = kotlin.math.floor(v52_M)
v55_N = (v51_T - v53_w)
v56_z = (v52_M - v54_A)
v57_p = v45_d(v53_w, v54_A)
v58_k = v45_d((v53_w + 1.0), v54_A)
v59_W = v45_d(v53_w, (v54_A + 1.0))
v60_m = v45_d((v53_w + 1.0), (v54_A + 1.0))
v61_J = v46_f(v55_N)
v62_G = v46_f(v56_z)
return (((v57_p + ((v58_k - v57_p) * v61_J)) + ((v59_W - v57_p) * v62_G)) + (((((v57_p - v58_k) - v59_W) + v60_m) * v61_J) * v62_G))
return Double.NaN
}
v41_c = jsZeros(512.0)
run {
v42_l = 0.0
L2@ while ((v42_l < 256.0)) {
jsSet(v41_c, v42_l, v42_l)
v42_l++
}
}
run {
v42_l = 255.0
L3@ while ((v42_l > 0.0)) {
v43_i = (jsInt((num(jsCall(v40_e)) * (v42_l + 1.0))) or jsInt(0.0)).toDouble()
v44_g = jsGet(v41_c, v42_l)
jsSet(v41_c, v42_l, jsGet(v41_c, v43_i))
jsSet(v41_c, v43_i, v44_g)
v42_l--
}
}
run {
v42_l = 0.0
L4@ while ((v42_l < 256.0)) {
jsSet(v41_c, (v42_l + 256.0), jsGet(v41_c, v42_l))
v42_l++
}
}
return JsFn L5@{ args ->
var v63_M: Any? = args.getOrNull(0)
var v64_w: Any? = args.getOrNull(1)
var v65_A: Any? = args.getOrNull(2)
var v66_N: Double = 0.0
var v67_z: Double = 0.0
var v68_p: Double = 0.0
var v69_k: Double = 0.0
var v70_W: Double = 0.0
v66_N = 0.0
v67_z = 0.5
v68_p = 1.0
v69_k = 0.0
run {
v70_W = 0.0
L6@ while ((v70_W < jsOrND(v65_A) { 5.0 })) {
v66_N += (v47_D((num(v63_M) * v68_p), (num(v64_w) * v68_p)) * v67_z)
v69_k += v67_z
v67_z *= 0.5
v68_p *= 2.03
v70_W++
}
}
return@L5 (v66_N / v69_k)
null
}
return null
}

fun a(v71_e: Ctx, v72_c: Any? = null, v73_l: Any? = null, v74_i: Any? = null, v75_g: Double = Double.NaN, v76_d: Double = Double.NaN): Grad {

var v77_f: Grad? = null
v77_f = v71_e.createLinearGradient(0.0, (if ((v75_g).isNaN()) 0.0 else v75_g), 0.0, (if ((v76_d).isNaN()) num(v73_l) else v76_d))
js_forEach(v74_i) L7@{ v78_D: Any?, _, _ ->

v77_f!!.addColorStop(num(jsGet(v78_D, 0.0)), jsGet(v78_D, 1.0))
null
}
return v77_f!!
error("undefined")
}

fun we(v79_e: Ctx, v80_c: Double = Double.NaN, v81_l: Double = Double.NaN, v82_i: Double = Double.NaN, v83_g: Any? = null, v84_d: Double = Double.NaN): Unit {

var v85_f: Grad? = null
v85_f = v79_e.createRadialGradient(v80_c, v81_l, 0.0, v80_c, v81_l, v82_i)
v85_f!!.addColorStop(0.0, B(v83_g, v84_d))
v85_f!!.addColorStop(1.0, B(v83_g, 0.0))
v79_e.fillStyle = v85_f!!
v79_e.fillRect((v80_c - v82_i), (v81_l - v82_i), (v82_i * 2.0), (v82_i * 2.0))
}

fun ie(v86_e: Ctx, v87_c: Any? = null, v88_l: Any? = null, v89_i: Any? = null, v90_g: Double = Double.NaN, v91_d: Any? = null, v92_f: Double = Double.NaN, v93_D: Double = Double.NaN, v94_T: Double = Double.NaN): Unit {

var v95_M: Double = 0.0
var v96_w: Double = 0.0
var v97_A: Double = 0.0
var v98_N: Double = 0.0
var v99_z: Any? = null
var v100_p: Double = 0.0
var v101_k: Grad? = null
v86_e.save()
v86_e.globalCompositeOperation = "lighter"
run {
v95_M = 0.0
L8@ while ((v95_M < v90_g)) {
v96_w = (num(jsCall(v89_i)) * num(v87_c))
v97_A = (num(jsCall(v89_i)) * num(v88_l))
v98_N = ((v92_f + (num(jsCall(v89_i)) * (v93_D - v92_f))) * jsMin(num(v87_c), num(v88_l)))
v99_z = jsGet(v91_d, (jsInt((num(jsCall(v89_i)) * jsLen(v91_d))) or jsInt(0.0)).toDouble())
v100_p = (v94_T * (0.35 + (num(jsCall(v89_i)) * 0.65)))
v101_k = v86_e.createRadialGradient(v96_w, v97_A, (v98_N * 0.55), v96_w, v97_A, v98_N)
v101_k!!.addColorStop(0.0, B(v99_z, (v100_p * 0.55)))
v101_k!!.addColorStop(0.86, B(v99_z, v100_p))
v101_k!!.addColorStop(1.0, B(v99_z, 0.0))
v86_e.fillStyle = v101_k!!
v86_e.beginPath()
v86_e.arc(v96_w, v97_A, v98_N, 0.0, (Math.PI * 2.0))
v86_e.fill()
v95_M++
}
}
v86_e.restore()
}

fun S(v102_e: Ctx, v103_c: Any? = null, v104_l: Any? = null, v105_i: Any? = null): Unit {

var v106_g: Double = 0.0
var v107_d: Double = 0.0
var v108_f: Double = 0.0
var v109_D: Double = 0.0
v102_e.save()
v102_e.globalCompositeOperation = "lighter"
v102_e.lineCap = "round"
run {
v106_g = 0.0
L9@ while ((v106_g < num(jsGet(v105_i, "lines")))) {
v107_d = (v106_g / (num(jsGet(v105_i, "lines")) - 1.0))
v108_f = (((v107_d - 0.5) * num(jsGet(v105_i, "spread"))) * num(v104_l))
v109_D = kotlin.math.sin((v107_d * Math.PI))
v102_e.strokeStyle = V(jsGet(v105_i, "c1"), jsGet(v105_i, "c2"), v107_d, (num(jsGet(v105_i, "alpha")) * (0.25 + (0.75 * v109_D))))
v102_e.lineWidth = jsMax(1.0, ((num(jsGet(v105_i, "width")) * num(v104_l)) * (0.4 + v109_D)))
v102_e.beginPath()
v102_e.moveTo((-0.1 * num(v103_c)), ((num(jsGet(v105_i, "y0")) * num(v104_l)) + v108_f))
v102_e.bezierCurveTo((num(jsGet(v105_i, "cx1")) * num(v103_c)), ((num(jsGet(v105_i, "cy1")) * num(v104_l)) + (v108_f * num(jsGet(v105_i, "twist")))), (num(jsGet(v105_i, "cx2")) * num(v103_c)), ((num(jsGet(v105_i, "cy2")) * num(v104_l)) - (v108_f * num(jsGet(v105_i, "twist")))), (1.1 * num(v103_c)), ((num(jsGet(v105_i, "y1")) * num(v104_l)) + (v108_f * 0.6)))
v102_e.stroke()
v106_g++
}
}
v102_e.restore()
}

fun Y(v110_e: Ctx, v111_c: Any? = null, v112_l: Any? = null, v113_i: Any? = null, v114_g: Double = Double.NaN): Unit {

var v115_d: Any? = null
var v116_f: Ctx? = null
var v117_D: Any? = null
var v118_T: Any? = null
var v119_M: Double = 0.0
var v120_w: Double = 0.0
v115_d = Dom.createElement("canvas")
jsSet(v115_d, "width", (run { jsSet(v115_d, "height", 96.0); jsGet(v115_d, "height") }))
v116_f = jsGetContext(v115_d)
v117_D = v116_f!!.createImageData(96.0, 96.0)
v118_T = jsGet(v117_D, "data")
run {
v119_M = 0.0
L10@ while ((v119_M < jsLen(v118_T))) {
v120_w = (jsInt((num(jsCall(v113_i)) * 255.0)) or jsInt(0.0)).toDouble()
jsSet(v118_T, v119_M, (run { jsSet(v118_T, (v119_M + 1.0), (run { jsSet(v118_T, (v119_M + 2.0), v120_w); jsGet(v118_T, (v119_M + 2.0)) })); jsGet(v118_T, (v119_M + 1.0)) }))
jsSet(v118_T, (v119_M + 3.0), (jsInt((v114_g * 255.0)) or jsInt(0.0)).toDouble())
v119_M += 4.0
}
}
v116_f!!.putImageData(v117_D, 0.0, 0.0)
v110_e.save()
v110_e.globalCompositeOperation = "overlay"
v110_e.fillStyle = v110_e.createPattern(v115_d, "repeat")
v110_e.fillRect(0.0, 0.0, num(v111_c), num(v112_l))
v110_e.restore()
}

fun ce(v121_e: Ctx, v122_c: Any? = null, v123_l: Any? = null, v124_i: Any? = null, v125_g: Any? = null): Unit {

var v126_d: Any? = null
var v127_f: Double = 0.0
var v128_D: Double = 0.0
var v129_T: Any? = null
var v130_M: Ctx? = null
var v131_w: Any? = null
var v132_A: Any? = null
var v133_N: Any? = null
var v134_z: Any? = null
var v135_p: Double = 0.0
var v136_k: Double = 0.0
var v137_W: Double = 0.0
var v138_m: Double = 0.0
var v139_J: Double = 0.0
var v140_G: Double = 0.0
var v141_ne: Double = 0.0
var v142_ae: Any? = null
var v143_de: Double = 0.0
var v144_q: Any? = null
var v145_H: Double = 0.0
var v146_R: Double = 0.0
v126_d = ke(v124_i)
v127_f = jsMax(64.0, jsRound((num(v122_c) / 6.0)))
v128_D = jsMax(36.0, jsRound((num(v123_l) / 6.0)))
v129_T = Dom.createElement("canvas")
jsSet(v129_T, "width", v127_f)
jsSet(v129_T, "height", v128_D)
v130_M = jsGetContext(v129_T)
v131_w = v130_M!!.createImageData(v127_f, v128_D)
v132_A = jsGet(v131_w, "data")
v133_N = U_(jsGet(v125_g, "color"))
v134_z = U_(jsGet(v125_g, "shade"))
v135_p = jsOrND(jsGet(v125_g, "scale")) { 3.2 }
run {
v136_k = 0.0
L11@ while ((v136_k < v128_D)) {
v137_W = (v136_k / v128_D)
v138_m = jsMax(0.0, jsMin(1.0, ((v137_W - num(jsGet(v125_g, "y0"))) / (num(jsGet(v125_g, "y1")) - num(jsGet(v125_g, "y0"))))))
v139_J = kotlin.math.sin((v138_m * Math.PI))
run {
v140_G = 0.0
L12@ while ((v140_G < v127_f)) {
v141_ne = (v140_G / v127_f)
v142_ae = jsCall(v126_d, (((v141_ne * v135_p) * (num(v122_c) / num(v123_l))) * 0.6), ((v137_W * v135_p) * 1.7), 6.0)
v143_de = (jsMax(0.0, ((num(v142_ae) - (1.0 - num(jsGet(v125_g, "cover")))) / num(jsGet(v125_g, "cover")))) * v139_J)
v143_de = jsMin(1.0, (v143_de * 1.8))
v144_q = jsCall(v126_d, (((v141_ne * v135_p) * (num(v122_c) / num(v123_l))) * 0.6), (((v137_W - 0.03) * v135_p) * 1.7), 4.0)
v145_H = jsMax(0.0, jsMin(1.0, (((num(v142_ae) - num(v144_q)) * 4.0) + 0.35)))
v146_R = (((v136_k * v127_f) + v140_G) * 4.0)
jsSet(v132_A, v146_R, (num(jsGet(v134_z, 0.0)) + ((num(jsGet(v133_N, 0.0)) - num(jsGet(v134_z, 0.0))) * v145_H)))
jsSet(v132_A, (v146_R + 1.0), (num(jsGet(v134_z, 1.0)) + ((num(jsGet(v133_N, 1.0)) - num(jsGet(v134_z, 1.0))) * v145_H)))
jsSet(v132_A, (v146_R + 2.0), (num(jsGet(v134_z, 2.0)) + ((num(jsGet(v133_N, 2.0)) - num(jsGet(v134_z, 2.0))) * v145_H)))
jsSet(v132_A, (v146_R + 3.0), ((v143_de * 255.0) * num((if (jsEq(jsGet(v125_g, "alpha"), null)) 1.0 else jsGet(v125_g, "alpha")))))
v140_G++
}
}
v136_k++
}
}
v130_M!!.putImageData(v131_w, 0.0, 0.0)
v121_e.save()
v121_e.imageSmoothingEnabled = true
v121_e.imageSmoothingQuality = "high"
v121_e.drawImage(v129_T, 0.0, 0.0, num(v122_c), num(v123_l))
v121_e.restore()
}

fun pe(v147_e: Ctx, v148_c: Any? = null, v149_l: Any? = null, v150_i: Any? = null, v151_g: Any? = null): Unit {

js_forEach(v151_g) L13@{ v152_d: Any?, _, _ ->

var v153_f: Double = 0.0
var v154_D: Double = 0.0
var v155_T: Double = 0.0
var v156_M: Double = 0.0
var v157_w: Double = 0.0
var v158_A: Double = 0.0
v147_e.beginPath()
v147_e.moveTo(0.0, num(v149_l))
v153_f = 80.0
v154_D = (num(jsCall(v150_i)) * 10.0)
v155_T = (num(jsCall(v150_i)) * 10.0)
run {
v156_M = 0.0
L14@ while ((v156_M <= v153_f)) {
v157_w = (v156_M / v153_f)
v158_A = ((num(jsGet(v152_d, "y")) + (kotlin.math.sin(((v157_w * num(jsGet(v152_d, "f"))) + v154_D)) * num(jsGet(v152_d, "amp")))) + ((kotlin.math.sin((((v157_w * num(jsGet(v152_d, "f"))) * 2.7) + v155_T)) * num(jsGet(v152_d, "amp"))) * 0.35))
v147_e.lineTo((v157_w * num(v148_c)), (v158_A * num(v149_l)))
v156_M++
}
}
v147_e.lineTo(num(v148_c), num(v149_l))
v147_e.closePath()
v147_e.fillStyle = a(v147_e, v148_c, v149_l, jsArr(jsArr(0.0, jsGet(v152_d, "top")), jsArr(1.0, jsGet(v152_d, "bot"))), ((num(jsGet(v152_d, "y")) - num(jsGet(v152_d, "amp"))) * num(v149_l)), num(v149_l))
v147_e.fill()
if (truthy(jsGet(v152_d, "rim"))) {
v147_e.save()
v147_e.clip()
v147_e.globalCompositeOperation = "lighter"
v147_e.fillStyle = a(v147_e, v148_c, v149_l, jsArr(jsArr(0.0, jsGet(v152_d, "rim")), jsArr(1.0, "rgba(0,0,0,0)")), ((num(jsGet(v152_d, "y")) - num(jsGet(v152_d, "amp"))) * num(v149_l)), ((num(jsGet(v152_d, "y")) + 0.06) * num(v149_l)))
v147_e.fillRect(0.0, 0.0, num(v148_c), num(v149_l))
v147_e.restore()
}
null
}
}

var g_v: Any? = null

fun P(v159_e: Ctx, v160_c: Any? = null, v161_l: Double = Double.NaN, v162_i: Double = Double.NaN, v163_g: Any? = null): Unit {

var v164_d: Any? = null
var v165_f: Any? = null
var v166_D: Any? = null
var v167_T: Double = 0.0
var v168_M: Double = 0.0
var v169_w: Double = 0.0
var v170_A: Double = 0.0
var v171_N: Double = 0.0
var v172_z: Double = 0.0
var v173_p: Any? = null
var v174_k: Double = 0.0
v164_d = v159_e.createImageData(num(v160_c), jsMax(1.0, (v162_i - v161_l)))
v165_f = jsGet(v164_d, "data")
v166_D = js_map(v163_g) { a0, a1, _ -> U_(a0) }
v167_T = (jsLen(v166_D) - 1.0)
run {
v168_M = 0.0
L15@ while ((v168_M < (v162_i - v161_l))) {
v169_w = ((v168_M / jsMax(1.0, ((v162_i - v161_l) - 1.0))) * v167_T)
v170_A = jsMin((v167_T - 1.0), kotlin.math.floor(v169_w))
v171_N = (v169_w - v170_A)
run {
v172_z = 0.0
L16@ while ((v172_z < num(v160_c))) {
v173_p = (if (((v171_N * 16.0) > (num(jsGet(g_v, (((jsInt(v168_M) and jsInt(3.0)).toDouble() * 4.0) + (jsInt(v172_z) and jsInt(3.0)).toDouble()))) + 0.5))) jsGet(v166_D, (v170_A + 1.0)) else jsGet(v166_D, v170_A))
v174_k = (((v168_M * num(v160_c)) + v172_z) * 4.0)
jsSet(v165_f, v174_k, jsGet(v173_p, 0.0))
jsSet(v165_f, (v174_k + 1.0), jsGet(v173_p, 1.0))
jsSet(v165_f, (v174_k + 2.0), jsGet(v173_p, 2.0))
jsSet(v165_f, (v174_k + 3.0), 255.0)
v172_z++
}
}
v168_M++
}
}
v159_e.putImageData(v164_d, 0.0, v161_l)
}

var g_oe: Any? = null

var g_L: Any? = null

fun rrOsWallIds(): Any? {

return js_map(jsConcat(g_oe, g_L)) L50@{ v336_w: Any?, _, _ ->

return@L50 jsGet(v336_w, "id")
null
}
return null
}

fun rrOsWallPixel(v337_id: String = "undefined"): Double {

var v338_all: Any? = null
var v339_i: Double = 0.0
v338_all = jsConcat(g_oe, g_L)
run {
v339_i = 0.0
L51@ while ((v339_i < jsLen(v338_all))) {
if (jsEq(jsGet(jsGet(v338_all, v339_i), "id"), v337_id)) {
return jsOrND(jsGet(jsGet(v338_all, v339_i), "pixel")) { 0.0 }
}
v339_i++
}
}
return 0.0
return Double.NaN
}

fun rrOsWallRender(v340_id: String = "undefined", v341_c: Any? = null, v342_l: Double = Double.NaN, v343_i: Double = Double.NaN, v344_g: Any? = null, v345_d: Double = Double.NaN): Double {

var v346_all: Any? = null
var v347_e: Any? = null
var v348_k: Double = 0.0
var v349_f: Double = 0.0
var v350_D: Double = 0.0
var v351_T: Double = 0.0
var v352_M: Double = 0.0
var v353_w: Ctx? = null
v346_all = jsConcat(g_oe, g_L)
v347_e = null
run {
v348_k = 0.0
L52@ while ((v348_k < jsLen(v346_all))) {
if (jsEq(jsGet(jsGet(v346_all, v348_k), "id"), v340_id)) {
v347_e = jsGet(v346_all, v348_k)
}
v348_k++
}
}
if (!(truthy(v347_e))) {
return 0.0
}
v349_f = jsOrND(jsGet(v347_e, "pixel")) { 0.0 }
v350_D = (if (truthy(v349_f)) (1.0 / v349_f) else jsOrN(v345_d) { 1.0 })
v351_T = jsMax(16.0, jsRound((v342_l * v350_D)))
v352_M = jsMax(9.0, jsRound((v343_i * v350_D)))
if ((truthy(v349_f) && (v342_l < 400.0))) {
v351_T = jsMax(32.0, jsRound((v342_l / 2.0)))
v352_M = jsMax(18.0, jsRound((v343_i / 2.0)))
}
jsSet(v341_c, "width", v351_T)
jsSet(v341_c, "height", v352_M)
v353_w = jsGetContext(v341_c)
v353_w!!.save()
jsInvoke(v347_e, "draw", v353_w!!, v351_T, v352_M, v344_g, se((jsInt(2654435769.0) xor jsInt((jsLen(jsGet(v347_e, "id")) * 2654435761.0))).toDouble()))
v353_w!!.restore()
return v349_f
return Double.NaN
}

fun SiteOsWallpapersInit() {
"use strict"
g_v = jsArr(0.0, 8.0, 2.0, 10.0, 12.0, 4.0, 14.0, 6.0, 3.0, 11.0, 1.0, 9.0, 15.0, 7.0, 13.0, 5.0)
g_oe = jsArr(jsObj("id" to "w95-sky", "name" to "Daydream Sky", "draw" to JsFn L17@{ args ->
var v175_e: Ctx = (args.getOrNull(0) as Ctx)
var v176_c: Any? = args.getOrNull(1)
var v177_l: Any? = args.getOrNull(2)
var v178_i: Any? = args.getOrNull(3)
var v179_g: Any? = args.getOrNull(4)
v175_e.fillStyle = a(v175_e, v176_c, v177_l, jsArr(jsArr(0.0, jsGet(jsGet(v178_i, "sky"), 1.0)), jsArr(0.45, jsGet(jsGet(v178_i, "sky"), 2.0)), jsArr(0.82, V(jsGet(jsGet(v178_i, "sky"), 3.0), "#ffffff", 0.25)), jsArr(1.0, V(jsGet(jsGet(v178_i, "sky"), 4.0), "#ffffff", 0.35))))
v175_e.fillRect(0.0, 0.0, num(v176_c), num(v177_l))
we(v175_e, (num(v176_c) * 0.72), (num(v177_l) * 0.78), (num(v177_l) * 0.75), jsGet(jsGet(v178_i, "sun"), 2.0), 0.35)
ce(v175_e, v176_c, v177_l, v179_g, jsObj("y0" to 0.05, "y1" to 1.05, "color" to V(jsGet(jsGet(v178_i, "sky"), 4.0), "#ffffff", 0.78), "shade" to V(jsGet(jsGet(v178_i, "sky"), 2.0), jsGet(jsGet(v178_i, "sky"), 3.0), 0.45), "cover" to 0.46, "scale" to 2.6))
ce(v175_e, v176_c, v177_l, se(7.0), jsObj("y0" to 0.45, "y1" to 1.15, "color" to "#ffffff", "shade" to V(jsGet(jsGet(v178_i, "sky"), 3.0), "#ffffff", 0.3), "cover" to 0.34, "scale" to 4.2, "alpha" to 0.85))
Y(v175_e, v176_c, v177_l, v179_g, 0.06)
null
}), jsObj("id" to "w95-dither", "name" to "Dither Sunset", "pixel" to 4.0, "draw" to JsFn L18@{ args ->
var v180_e: Ctx = (args.getOrNull(0) as Ctx)
var v181_c: Any? = args.getOrNull(1)
var v182_l: Any? = args.getOrNull(2)
var v183_i: Any? = args.getOrNull(3)
var v184_g: Any? = args.getOrNull(4)
var v185_d: Double = 0.0
var v186_f: Double = 0.0
var v187_D: Double = 0.0
var v188_T: Double = 0.0
var v189_M: Any? = null
var v190_w: Double = 0.0
var v191_A: Double = 0.0
var v192_N: Double = 0.0
var v194_p: Double = 0.0
var v195_k: Double = 0.0
var v196_W: Double = 0.0
var v197_m: Double = 0.0
var v198_J: Any? = null
var v199_G: Double = 0.0
var v200_ne: Double = 0.0
var v201_ae: Double = 0.0
fun v193_z(v202_de: Double = Double.NaN, v203_q: Double = Double.NaN, v204_H: Double = Double.NaN, v205_R: Any? = null, v206_le: Any? = null): Unit {

var v207_K: Any? = null
var v208_he: Double = 0.0
var v209_ue: Double = 0.0
v207_K = se(v204_H)
v180_e.beginPath()
v180_e.moveTo(0.0, v185_d)
v208_he = 0.0
v209_ue = v202_de
L19@ while ((v208_he <= num(v181_c))) {
v209_ue = (v202_de - (num(jsCall(v207_K)) * v203_q))
v180_e.lineTo(jsRound(v208_he), jsRound(v209_ue))
v208_he += (3.0 + (num(jsCall(v207_K)) * (num(v181_c) / 24.0)))

}
v180_e.lineTo(num(v181_c), v185_d)
v180_e.closePath()
v180_e.fillStyle = B(v205_R)
v180_e.fill()
if (truthy(v206_le)) {
v180_e.strokeStyle = B(v206_le)
v180_e.lineWidth = 1.0
v180_e.stroke()
}
}
v185_d = jsRound((num(v182_l) * 0.62))
P(v180_e, v181_c, 0.0, v185_d, jsArr(jsGet(jsGet(v183_i, "sky"), 0.0), jsGet(jsGet(v183_i, "sky"), 1.0), jsGet(jsGet(v183_i, "sky"), 2.0), jsGet(jsGet(v183_i, "sky"), 3.0), jsGet(jsGet(v183_i, "sky"), 4.0)))
v186_f = (num(v181_c) * 0.5)
v187_D = (v185_d - (num(v182_l) * 0.02))
v188_T = (num(v182_l) * 0.27)
v180_e.save()
v180_e.beginPath()
v180_e.arc(v186_f, v187_D, v188_T, Math.PI, 0.0)
v180_e.clip()
v189_M = Dom.createElement("canvas")
jsSet(v189_M, "width", kotlin.math.ceil((v188_T * 2.0)))
jsSet(v189_M, "height", kotlin.math.ceil(v188_T))
P(jsGetContext(v189_M), jsGet(v189_M, "width"), 0.0, num(jsGet(v189_M, "height")), jsArr(jsGet(jsGet(v183_i, "sun"), 2.0), jsGet(jsGet(v183_i, "sun"), 1.0), jsGet(jsGet(v183_i, "sun"), 0.0)))
v180_e.drawImage(v189_M, (v186_f - v188_T), (v187_D - v188_T))
v180_e.fillStyle = B(jsGet(v183_i, "sunSlice"))
run {
v190_w = 0.0
L20@ while ((v190_w < 7.0)) {
v191_A = ((v187_D - (v188_T * 0.08)) - ((v190_w * v188_T) * 0.12))
v192_N = jsMax(1.0, (((7.0 - v190_w) * 0.34) * (num(v182_l) / 160.0)))
v180_e.fillRect((v186_f - v188_T), jsRound(v191_A), (v188_T * 2.0), jsRound(v192_N))
v190_w++
}
}
v180_e.restore()
v193_z((v185_d - 2.0), (num(v182_l) * 0.13), 11.0, V(jsGet(v183_i, "mtnBot"), jsGet(v183_i, "mtnTop"), 0.35), null)
v193_z(v185_d, (num(v182_l) * 0.08), 23.0, jsGet(v183_i, "mtnBot"), jsGet(v183_i, "mtnStroke"))
P(v180_e, v181_c, v185_d, num(v182_l), jsArr(jsGet(jsGet(v183_i, "floor"), 0.0), jsGet(jsGet(v183_i, "floor"), 1.0), jsGet(jsGet(v183_i, "floor"), 2.0)))
v180_e.strokeStyle = B(jsGet(v183_i, "primary"))
v180_e.lineWidth = 1.0
run {
v194_p = 0.0
L21@ while ((v194_p < 12.0)) {
v195_k = Math.pow((v194_p / 11.0), 2.1)
v196_W = jsRound(((v185_d + 1.0) + (v195_k * (num(v182_l) - v185_d))))
v180_e.beginPath()
v180_e.moveTo(0.0, (v196_W + 0.5))
v180_e.lineTo(num(v181_c), (v196_W + 0.5))
v180_e.stroke()
v194_p++
}
}
run {
v197_m = -14.0
L22@ while ((v197_m <= 14.0)) {
v180_e.beginPath()
v180_e.moveTo((v186_f + (v197_m * 3.0)), v185_d)
v180_e.lineTo((v186_f + ((v197_m * num(v181_c)) * 0.11)), num(v182_l))
v180_e.stroke()
v197_m++
}
}
v198_J = se(5.0)
v180_e.fillStyle = "#ffffff"
run {
v199_G = 0.0
L23@ while ((v199_G < 60.0)) {
v200_ne = (num(jsCall(v198_J)) * num(v181_c))
v201_ae = ((num(jsCall(v198_J)) * v185_d) * 0.55)
v180_e.globalAlpha = (0.4 + (num(jsCall(v198_J)) * 0.6))
v180_e.fillRect((jsInt(v200_ne) or jsInt(0.0)).toDouble(), (jsInt(v201_ae) or jsInt(0.0)).toDouble(), 1.0, 1.0)
v199_G++
}
}
v180_e.globalAlpha = 1.0
null
}), jsObj("id" to "w95-pattern", "name" to "Desktop Weave", "pixel" to 2.0, "draw" to JsFn L24@{ args ->
var v210_e: Ctx = (args.getOrNull(0) as Ctx)
var v211_c: Any? = args.getOrNull(1)
var v212_l: Any? = args.getOrNull(2)
var v213_i: Any? = args.getOrNull(3)
var v214_g: Any? = args.getOrNull(4)
var v215_d: Any? = null
var v216_f: Any? = null
var v217_D: Any? = null
var v218_T: Any? = null
var v219_M: Ctx? = null
var v220_w: Double = 0.0
var v221_A: Double = 0.0
var v222_N: Double = 0.0
var v223_z: Any? = null
var v224_p: Double = 0.0
var v225_k: Double = 0.0
var v226_W: Double = 0.0
v215_d = U(jsGet(jsGet(v213_i, "floor"), 1.0), jsGet(jsGet(v213_i, "sky"), 2.0), 0.35)
v216_f = U(v215_d, jsArr(255.0, 255.0, 255.0), 0.24)
v217_D = U(v215_d, jsArr(0.0, 0.0, 0.0), 0.4)
v210_e.fillStyle = o(v215_d)
v210_e.fillRect(0.0, 0.0, num(v211_c), num(v212_l))
v218_T = Dom.createElement("canvas")
jsSet(v218_T, "width", (run { jsSet(v218_T, "height", 8.0); jsGet(v218_T, "height") }))
v219_M = jsGetContext(v218_T)
v219_M!!.fillStyle = o(v215_d)
v219_M!!.fillRect(0.0, 0.0, 8.0, 8.0)
v219_M!!.fillStyle = o(v216_f)
js_forEach(jsArr(jsArr(0.0, 0.0, 3.0, 1.0), jsArr(0.0, 0.0, 1.0, 3.0), jsArr(4.0, 4.0, 3.0, 1.0), jsArr(4.0, 4.0, 1.0, 3.0))) L25@{ v227_m: Any?, _, _ ->

v219_M!!.fillRect(num(jsGet(v227_m, 0.0)), num(jsGet(v227_m, 1.0)), num(jsGet(v227_m, 2.0)), num(jsGet(v227_m, 3.0)))
null
}
v219_M!!.fillStyle = o(v217_D)
js_forEach(jsArr(jsArr(1.0, 3.0, 3.0, 1.0), jsArr(3.0, 1.0, 1.0, 3.0), jsArr(5.0, 7.0, 3.0, 1.0), jsArr(7.0, 5.0, 1.0, 3.0))) L26@{ v228_m: Any?, _, _ ->

v219_M!!.fillRect(num(jsGet(v228_m, 0.0)), num(jsGet(v228_m, 1.0)), num(jsGet(v228_m, 2.0)), num(jsGet(v228_m, 3.0)))
null
}
v210_e.fillStyle = v210_e.createPattern(v218_T, "repeat")
v210_e.fillRect(0.0, 0.0, num(v211_c), num(v212_l))
v220_w = jsRound((jsMin(num(v211_c), num(v212_l)) * 0.24))
v221_A = jsRound(((num(v211_c) / 2.0) - (v220_w / 2.0)))
v222_N = jsRound(((num(v212_l) / 2.0) - (v220_w / 2.0)))
v210_e.fillStyle = "#000"
v210_e.fillRect((v221_A + 3.0), (v222_N + 3.0), v220_w, v220_w)
v210_e.fillStyle = B(jsGet(jsGet(v213_i, "sky"), 1.0))
v210_e.fillRect(v221_A, v222_N, v220_w, v220_w)
v210_e.fillStyle = "#fff"
v210_e.fillRect(v221_A, v222_N, v220_w, 2.0)
v210_e.fillRect(v221_A, v222_N, 2.0, v220_w)
v210_e.fillStyle = "rgba(0,0,0,.6)"
v210_e.fillRect(v221_A, ((v222_N + v220_w) - 2.0), v220_w, 2.0)
v210_e.fillRect(((v221_A + v220_w) - 2.0), v222_N, 2.0, v220_w)
v223_z = Dom.createElement("canvas")
jsSet(v223_z, "width", (v220_w - 8.0))
jsSet(v223_z, "height", (v220_w - 8.0))
P(jsGetContext(v223_z), jsGet(v223_z, "width"), 0.0, num(jsGet(v223_z, "height")), jsArr(jsGet(jsGet(v213_i, "sky"), 1.0), jsGet(jsGet(v213_i, "sky"), 2.0), jsGet(jsGet(v213_i, "sky"), 3.0), jsGet(jsGet(v213_i, "sky"), 4.0)))
v210_e.drawImage(v223_z, (v221_A + 4.0), (v222_N + 4.0))
v210_e.save()
v210_e.beginPath()
v210_e.rect((v221_A + 4.0), (v222_N + 4.0), (v220_w - 8.0), (v220_w - 8.0))
v210_e.clip()
v210_e.fillStyle = B(jsGet(jsGet(v213_i, "sun"), 0.0))
v210_e.beginPath()
v210_e.arc((v221_A + (v220_w / 2.0)), (v222_N + (v220_w * 0.72)), (v220_w * 0.28), Math.PI, 0.0)
v210_e.fill()
v210_e.fillStyle = B(jsGet(jsGet(v213_i, "sky"), 1.0))
run {
v224_p = 0.0
L27@ while ((v224_p < 4.0)) {
v210_e.fillRect(v221_A, jsRound(((v222_N + (v220_w * 0.66)) - ((v224_p * v220_w) * 0.07))), v220_w, (1.0 + (if ((v224_p < 2.0)) 1.0 else 0.0)))
v224_p++
}
}
v210_e.fillStyle = B(jsGet(jsGet(v213_i, "floor"), 1.0))
v210_e.fillRect(v221_A, jsRound((v222_N + (v220_w * 0.72))), v220_w, v220_w)
v210_e.strokeStyle = B(jsGet(v213_i, "primary"))
v210_e.lineWidth = 1.0
run {
v225_k = 0.0
L28@ while ((v225_k < 4.0)) {
v226_W = (jsRound(((v222_N + (v220_w * 0.72)) + ((Math.pow((v225_k / 3.0), 1.8) * v220_w) * 0.24))) + 0.5)
v210_e.beginPath()
v210_e.moveTo(v221_A, v226_W)
v210_e.lineTo((v221_A + v220_w), v226_W)
v210_e.stroke()
v225_k++
}
}
v210_e.restore()
null
}), jsObj("id" to "w95-setup", "name" to "Setup.exe", "draw" to JsFn L29@{ args ->
var v229_e: Ctx = (args.getOrNull(0) as Ctx)
var v230_c: Any? = args.getOrNull(1)
var v231_l: Any? = args.getOrNull(2)
var v232_i: Any? = args.getOrNull(3)
var v233_g: Any? = args.getOrNull(4)
var v234_d: Any? = null
var v235_f: Double = 0.0
var v236_D: Double = 0.0
var v237_T: Double = 0.0
var v238_M: Double = 0.0
var v239_w: Double = 0.0
var v240_A: Double = 0.0
var v241_N: Double = 0.0
var v242_z: Double = 0.0
v234_d = U(jsGet(jsGet(v232_i, "sky"), 2.0), jsArr(0.0, 0.0, 160.0), 0.45)
v235_f = 24.0
run {
v236_D = 0.0
L30@ while ((v236_D < v235_f)) {
v229_e.fillStyle = o(U(v234_d, jsArr(0.0, 0.0, 0.0), (v236_D / (v235_f - 1.0))))
v229_e.fillRect(0.0, kotlin.math.floor(((v236_D * num(v231_l)) / v235_f)), num(v230_c), (kotlin.math.ceil((num(v231_l) / v235_f)) + 1.0))
v236_D++
}
}
v237_T = jsRound(jsMin((num(v230_c) * 0.075), (num(v231_l) * 0.12)))
v229_e.textBaseline = "top"
v229_e.font = (("italic 900 " + jsStr(v237_T)) + "px \"Times New Roman\", Times, Georgia, serif")
v229_e.fillStyle = "#000"
v229_e.fillText("REVERENDER 95", ((num(v230_c) * 0.045) + (v237_T * 0.06)), ((num(v231_l) * 0.05) + (v237_T * 0.06)))
v229_e.fillStyle = "#fff"
v229_e.fillText("REVERENDER 95", (num(v230_c) * 0.045), (num(v231_l) * 0.05))
v229_e.font = (("italic 700 " + jsStr(jsRound((v237_T * 0.5)))) + "px \"Times New Roman\", Times, Georgia, serif")
v229_e.fillStyle = "#000"
v229_e.fillText("Setup", ((num(v230_c) * 0.045) + 3.0), (((num(v231_l) * 0.05) + (v237_T * 1.12)) + 3.0))
v229_e.fillStyle = B(jsGet(jsGet(v232_i, "sun"), 2.0))
v229_e.fillText("Setup", (num(v230_c) * 0.045), ((num(v231_l) * 0.05) + (v237_T * 1.12)))
v238_M = (num(v230_c) * 0.26)
v239_w = jsMax(14.0, (num(v231_l) * 0.024))
v240_A = ((num(v230_c) - v238_M) - (num(v230_c) * 0.05))
v241_N = ((num(v231_l) - v239_w) - (num(v231_l) * 0.12))
v229_e.fillStyle = "#c0c0c0"
v229_e.fillRect((v240_A - 3.0), (v241_N - 3.0), (v238_M + 6.0), (v239_w + 6.0))
v229_e.fillStyle = "#fff"
v229_e.fillRect((v240_A - 3.0), (v241_N - 3.0), (v238_M + 6.0), 2.0)
v229_e.fillStyle = "#404040"
v229_e.fillRect((v240_A - 3.0), ((v241_N + v239_w) + 1.0), (v238_M + 6.0), 2.0)
v229_e.fillStyle = "#000080"
run {
v242_z = 0.0
L31@ while ((v242_z < 14.0)) {
v229_e.fillRect(((v240_A + 2.0) + (v242_z * (v238_M / 19.0))), (v241_N + 2.0), ((v238_M / 19.0) - 3.0), (v239_w - 4.0))
v242_z++
}
}
v229_e.font = (jsStr(jsRound((v239_w * 0.9))) + "px Tahoma, \"MS Sans Serif\", Arial, sans-serif")
v229_e.fillStyle = "rgba(255,255,255,.75)"
v229_e.fillText("Copying files…  74%", v240_A, (v241_N - (v239_w * 1.6)))
null
}), jsObj("id" to "w95-stars", "name" to "Starfield.scr", "pixel" to 3.0, "draw" to JsFn L32@{ args ->
var v243_e: Ctx = (args.getOrNull(0) as Ctx)
var v244_c: Any? = args.getOrNull(1)
var v245_l: Any? = args.getOrNull(2)
var v246_i: Any? = args.getOrNull(3)
var v247_g: Any? = args.getOrNull(4)
var v248_d: Double = 0.0
var v249_f: Double = 0.0
var v250_D: Double = 0.0
var v251_T: Double = 0.0
var v252_M: Double = 0.0
var v253_w: Double = 0.0
var v254_A: Double = 0.0
var v255_N: Double = 0.0
var v256_z: Any? = null
v243_e.fillStyle = "#000"
v243_e.fillRect(0.0, 0.0, num(v244_c), num(v245_l))
v248_d = (num(v244_c) / 2.0)
v249_f = (num(v245_l) / 2.0)
run {
v250_D = 0.0
L33@ while ((v250_D < 520.0)) {
v251_T = ((num(jsCall(v247_g)) * Math.PI) * 2.0)
v252_M = ((Math.pow(num(jsCall(v247_g)), 0.6) * Math.hypot(num(v244_c), num(v245_l))) * 0.55)
v253_w = (v248_d + (kotlin.math.cos(v251_T) * v252_M))
v254_A = (v249_f + (kotlin.math.sin(v251_T) * v252_M))
v255_N = (jsMax(0.0, (v252_M / (Math.hypot(num(v244_c), num(v245_l)) * 0.55))) * 7.0)
v256_z = (if ((num(jsCall(v247_g)) < 0.18)) (if ((num(jsCall(v247_g)) < 0.5)) jsGet(v246_i, "primary") else jsGet(v246_i, "secondary")) else "#ffffff")
v243_e.strokeStyle = B(v256_z, (0.35 + ((0.65 * v252_M) / (num(v244_c) * 0.6))))
v243_e.lineWidth = 1.0
v243_e.beginPath()
v243_e.moveTo(jsRound(v253_w), jsRound(v254_A))
v243_e.lineTo(jsRound((v253_w + (kotlin.math.cos(v251_T) * v255_N))), jsRound((v254_A + (kotlin.math.sin(v251_T) * v255_N))))
v243_e.stroke()
v250_D++
}
}
null
}), jsObj("id" to "w95-pipes", "name" to "Pipes.scr", "draw" to JsFn L34@{ args ->
var v257_e: Ctx = (args.getOrNull(0) as Ctx)
var v258_c: Any? = args.getOrNull(1)
var v259_l: Any? = args.getOrNull(2)
var v260_i: Any? = args.getOrNull(3)
var v261_g: Any? = args.getOrNull(4)
var v262_d: Double = 0.0
var v263_f: Double = 0.0
var v264_D: Double = 0.0
var v265_T: Any? = null
var v266_M: Double = 0.0
var v267_w: Any? = null
var v270_z: Double = 0.0
var v271_p: Any? = null
var v272_k: Double = 0.0
var v273_W: Double = 0.0
var v274_m: Any? = null
var v275_J: Double = 0.0
var v276_G: Double = 0.0
var v277_ne: Double = 0.0
var v278_ae: Double = 0.0
var v279_de: Any? = null
fun v268_A(v280_q: Double = Double.NaN, v281_H: Double = Double.NaN, v282_R: Double = Double.NaN, v283_le: Double = Double.NaN, v284_K: Any? = null): Unit {

var v285_he: Boolean = false
var v286_ue: Grad? = null
v285_he = (v281_H == v283_le)
v286_ue = (if (v285_he) v257_e.createLinearGradient(0.0, (v281_H - v266_M), 0.0, (v281_H + v266_M)) else v257_e.createLinearGradient((v280_q - v266_M), 0.0, (v280_q + v266_M), 0.0))
v286_ue!!.addColorStop(0.0, V(v284_K, "#000", 0.65))
v286_ue!!.addColorStop(0.35, V(v284_K, "#fff", 0.45))
v286_ue!!.addColorStop(0.55, B(v284_K))
v286_ue!!.addColorStop(1.0, V(v284_K, "#000", 0.75))
v257_e.fillStyle = v286_ue!!
if (v285_he) {
v257_e.fillRect(jsMin(v280_q, v282_R), (v281_H - v266_M), kotlin.math.abs((v282_R - v280_q)), (v266_M * 2.0))
} else {
v257_e.fillRect((v280_q - v266_M), jsMin(v281_H, v283_le), (v266_M * 2.0), kotlin.math.abs((v283_le - v281_H)))
}
}
fun v269_N(v287_q: Double = Double.NaN, v288_H: Double = Double.NaN, v289_R: Any? = null): Unit {

var v290_le: Grad? = null
v290_le = v257_e.createRadialGradient((v287_q - (v266_M * 0.5)), (v288_H - (v266_M * 0.5)), (v266_M * 0.1), v287_q, v288_H, (v266_M * 1.45))
v290_le!!.addColorStop(0.0, V(v289_R, "#fff", 0.7))
v290_le!!.addColorStop(0.45, B(v289_R))
v290_le!!.addColorStop(1.0, V(v289_R, "#000", 0.7))
v257_e.fillStyle = v290_le!!
v257_e.beginPath()
v257_e.arc(v287_q, v288_H, (v266_M * 1.4), 0.0, (Math.PI * 2.0))
v257_e.fill()
}
v257_e.fillStyle = "#000"
v257_e.fillRect(0.0, 0.0, num(v258_c), num(v259_l))
v262_d = jsRound((jsMin(num(v258_c), num(v259_l)) / 11.0))
v263_f = (kotlin.math.ceil((num(v258_c) / v262_d)) + 1.0)
v264_D = (kotlin.math.ceil((num(v259_l) / v262_d)) + 1.0)
v265_T = jsArr(jsGet(v260_i, "primary"), jsGet(v260_i, "secondary"), jsGet(v260_i, "accent"), jsGet(jsGet(v260_i, "sun"), 1.0), jsGet(v260_i, "mtnTop"))
v266_M = (v262_d * 0.17)
v267_w = jsArr(jsArr(1.0, 0.0), jsArr(-1.0, 0.0), jsArr(0.0, 1.0), jsArr(0.0, -1.0))
run {
v270_z = 0.0
L35@ while ((v270_z < 10.0)) {
v271_p = jsGet(v265_T, (v270_z % jsLen(v265_T)))
v272_k = (jsInt((num(jsCall(v261_g)) * v263_f)) or jsInt(0.0)).toDouble()
v273_W = (jsInt((num(jsCall(v261_g)) * v264_D)) or jsInt(0.0)).toDouble()
v274_m = jsGet(v267_w, (jsInt((num(jsCall(v261_g)) * 4.0)) or jsInt(0.0)).toDouble())
run {
v275_J = 0.0
L36@ while ((v275_J < 48.0)) {
v276_G = (1.0 + (jsInt((num(jsCall(v261_g)) * 4.0)) or jsInt(0.0)).toDouble())
v277_ne = (v272_k + (num(jsGet(v274_m, 0.0)) * v276_G))
v278_ae = (v273_W + (num(jsGet(v274_m, 1.0)) * v276_G))
v268_A((v272_k * v262_d), (v273_W * v262_d), (v277_ne * v262_d), (v278_ae * v262_d), v271_p)
v269_N((v272_k * v262_d), (v273_W * v262_d), v271_p)
v272_k = v277_ne
v273_W = v278_ae
if (((((v272_k < 0.0) || (v273_W < 0.0)) || (v272_k > v263_f)) || (v273_W > v264_D))) {
break@L36
}
v279_de = jsGet(v267_w, (jsInt((num(jsCall(v261_g)) * 4.0)) or jsInt(0.0)).toDouble())
if ((!jsEq(jsGet(v279_de, 0.0), -(num(jsGet(v274_m, 0.0)))) || !jsEq(jsGet(v279_de, 1.0), -(num(jsGet(v274_m, 1.0)))))) {
v274_m = v279_de
}
v275_J++
}
}
v269_N((v272_k * v262_d), (v273_W * v262_d), v271_p)
v270_z++
}
}
null
}), jsObj("id" to "w95-mystify", "name" to "Mystify.scr", "draw" to JsFn L37@{ args ->
var v291_e: Ctx = (args.getOrNull(0) as Ctx)
var v292_c: Any? = args.getOrNull(1)
var v293_l: Any? = args.getOrNull(2)
var v294_i: Any? = args.getOrNull(3)
var v295_g: Any? = args.getOrNull(4)
v291_e.fillStyle = "#000"
v291_e.fillRect(0.0, 0.0, num(v292_c), num(v293_l))
v291_e.globalCompositeOperation = "lighter"
v291_e.lineJoin = "round"
js_forEach(jsArr(jsGet(v294_i, "primary"), jsGet(v294_i, "secondary"), jsGet(v294_i, "accent"))) L38@{ v296_d: Any?, v297_f: Double, _ ->

var v298_D: Any? = null
var v299_T: Any? = null
var v300_M: Double = 0.0
var v301_w: Double = 0.0
v298_D = jsArr()
v299_T = jsArr()
run {
v300_M = 0.0
L39@ while ((v300_M < 4.0)) {
jsPush(v298_D, jsArr((num(jsCall(v295_g)) * num(v292_c)), (num(jsCall(v295_g)) * num(v293_l))))
jsPush(v299_T, jsArr((((num(jsCall(v295_g)) - 0.5) * num(v292_c)) * 0.022), (((num(jsCall(v295_g)) - 0.5) * num(v293_l)) * 0.022)))
v300_M++
}
}
run {
v301_w = 0.0
L40@ while ((v301_w < 18.0)) {
v291_e.strokeStyle = B(v296_d, (0.12 + (0.88 * (v301_w / 17.0))))
v291_e.lineWidth = jsMax(1.2, (jsMin(num(v292_c), num(v293_l)) / 500.0))
v291_e.beginPath()
js_forEach(v298_D) L41@{ v302_A: Any?, v303_N: Double, _ ->

if (truthy(v303_N)) {
v291_e.lineTo(num(jsGet(v302_A, 0.0)), num(jsGet(v302_A, 1.0)))
} else {
v291_e.moveTo(num(jsGet(v302_A, 0.0)), num(jsGet(v302_A, 1.0)))
}
null
}
v291_e.closePath()
v291_e.stroke()
js_forEach(v298_D) L42@{ v304_A: Any?, v305_N: Double, _ ->

jsAddAssign(v304_A, 0.0, jsGet(jsGet(v299_T, v305_N), 0.0))
jsAddAssign(v304_A, 1.0, jsGet(jsGet(v299_T, v305_N), 1.0))
if (((num(jsGet(v304_A, 0.0)) < 0.0) || (num(jsGet(v304_A, 0.0)) > num(v292_c)))) {
jsOpAssign(jsGet(v299_T, v305_N), 0.0, "*", -1.0)
}
if (((num(jsGet(v304_A, 1.0)) < 0.0) || (num(jsGet(v304_A, 1.0)) > num(v293_l)))) {
jsOpAssign(jsGet(v299_T, v305_N), 1.0, "*", -1.0)
}
null
}
v301_w++
}
}
null
}
v291_e.globalCompositeOperation = "source-over"
null
}))
g_L = jsArr(jsObj("id" to "lh-aurora", "name" to "Aurora Plex", "accent" to "jade", "draw" to JsFn L43@{ args ->
var v306_e: Ctx = (args.getOrNull(0) as Ctx)
var v307_c: Any? = args.getOrNull(1)
var v308_l: Any? = args.getOrNull(2)
var v309_i: Any? = args.getOrNull(3)
var v310_g: Any? = args.getOrNull(4)
var v311_d: Double = 0.0
var v312_f: Grad? = null
v306_e.fillStyle = a(v306_e, v307_c, v308_l, jsArr(jsArr(0.0, "#020a10"), jsArr(0.55, "#06222a"), jsArr(1.0, "#010507")))
v306_e.fillRect(0.0, 0.0, num(v307_c), num(v308_l))
we(v306_e, (num(v307_c) * 0.3), (num(v308_l) * 0.62), (num(v307_c) * 0.55), "#1aa38a", 0.22)
S(v306_e, v307_c, v308_l, jsObj("lines" to 70.0, "spread" to 0.18, "width" to 0.0016, "alpha" to 0.1, "c1" to "#2df0b0", "c2" to "#3fa8ff", "y0" to 0.78, "cx1" to 0.28, "cy1" to 0.38, "cx2" to 0.62, "cy2" to 0.92, "y1" to 0.44, "twist" to 1.6))
S(v306_e, v307_c, v308_l, jsObj("lines" to 40.0, "spread" to 0.08, "width" to 0.0012, "alpha" to 0.12, "c1" to "#b6ffde", "c2" to "#2bd8c0", "y0" to 0.7, "cx1" to 0.35, "cy1" to 0.5, "cx2" to 0.7, "cy2" to 0.8, "y1" to 0.52, "twist" to 1.2))
v311_d = (num(v308_l) * 0.705)
v312_f = v306_e.createLinearGradient(0.0, 0.0, num(v307_c), 0.0)
v312_f!!.addColorStop(0.0, "rgba(120,255,220,0)")
v312_f!!.addColorStop(0.45, "rgba(190,255,236,.9)")
v312_f!!.addColorStop(1.0, "rgba(120,255,220,0)")
v306_e.fillStyle = v312_f!!
v306_e.fillRect(0.0, v311_d, num(v307_c), jsMax(1.0, (num(v308_l) * 0.0018)))
we(v306_e, (num(v307_c) * 0.45), v311_d, (num(v307_c) * 0.18), "#7dffd8", 0.18)
Y(v306_e, v307_c, v308_l, v310_g, 0.05)
null
}), jsObj("id" to "lh-jade", "name" to "Jade Horizon", "accent" to "jade", "draw" to JsFn L44@{ args ->
var v313_e: Ctx = (args.getOrNull(0) as Ctx)
var v314_c: Any? = args.getOrNull(1)
var v315_l: Any? = args.getOrNull(2)
var v316_i: Any? = args.getOrNull(3)
var v317_g: Any? = args.getOrNull(4)
var v318_d: Double = 0.0
var v319_f: Double = 0.0
v313_e.fillStyle = a(v313_e, v314_c, v315_l, jsArr(jsArr(0.0, "#0b2f2c"), jsArr(0.5, "#1d6a5c"), jsArr(0.66, "#7fd1b2"), jsArr(0.665, "#d9fff0"), jsArr(0.7, "#0f3b33"), jsArr(1.0, "#03100e")))
v313_e.fillRect(0.0, 0.0, num(v314_c), num(v315_l))
v313_e.save()
v313_e.globalCompositeOperation = "lighter"
run {
v318_d = 0.0
L45@ while ((v318_d < 9.0)) {
v313_e.strokeStyle = (("rgba(160,255,220," + jsStr((0.05 + (v318_d * 0.008)))) + ")")
v313_e.lineWidth = (num(v315_l) * 0.002)
v313_e.beginPath()
v313_e.ellipse((num(v314_c) * 0.5), (num(v315_l) * (1.4 + (v318_d * 0.02))), (num(v314_c) * (0.9 + (v318_d * 0.05))), (num(v315_l) * (0.75 + (v318_d * 0.01))), 0.0, (Math.PI * 1.08), (Math.PI * 1.92))
v313_e.stroke()
v318_d++
}
}
v313_e.restore()
we(v313_e, (num(v314_c) * 0.5), (num(v315_l) * 0.665), (num(v314_c) * 0.45), "#b4ffe2", 0.32)
run {
v319_f = 0.0
L46@ while ((v319_f < 6.0)) {
v313_e.fillStyle = (("rgba(255,255,255," + jsStr((0.025 + (num(jsCall(v317_g)) * 0.03)))) + ")")
v313_e.save()
v313_e.translate((num(v314_c) * (0.1 + (v319_f * 0.17))), 0.0)
v313_e.rotate(-0.35)
v313_e.fillRect(0.0, -(num(v315_l)), (num(v314_c) * 0.035), (num(v315_l) * 3.0))
v313_e.restore()
v319_f++
}
}
Y(v313_e, v314_c, v315_l, v317_g, 0.05)
null
}), jsObj("id" to "lh-slate", "name" to "Slate Panes", "accent" to "slate", "draw" to JsFn L47@{ args ->
var v320_e: Ctx = (args.getOrNull(0) as Ctx)
var v321_c: Any? = args.getOrNull(1)
var v322_l: Any? = args.getOrNull(2)
var v323_i: Any? = args.getOrNull(3)
var v324_g: Any? = args.getOrNull(4)
var v325_d: Double = 0.0
var v326_f: Double = 0.0
var v327_D: Double = 0.0
var v328_T: Double = 0.0
var v329_M: Double = 0.0
var v330_w: Grad? = null
v320_e.fillStyle = a(v320_e, v321_c, v322_l, jsArr(jsArr(0.0, "#2a3138"), jsArr(1.0, "#0b0e11")))
v320_e.fillRect(0.0, 0.0, num(v321_c), num(v322_l))
we(v320_e, (num(v321_c) * 0.75), (num(v322_l) * 0.25), (num(v321_c) * 0.6), "#6f8fae", 0.25)
run {
v325_d = 0.0
L48@ while ((v325_d < 9.0)) {
v326_f = (num(v321_c) * (0.18 + (num(jsCall(v324_g)) * 0.25)))
v327_D = (num(v322_l) * (0.3 + (num(jsCall(v324_g)) * 0.5)))
v328_T = (((num(jsCall(v324_g)) * num(v321_c)) * 0.9) - (v326_f * 0.2))
v329_M = (((num(jsCall(v324_g)) * num(v322_l)) * 0.8) - (v327_D * 0.2))
v320_e.save()
v320_e.translate((v328_T + (v326_f / 2.0)), (v329_M + (v327_D / 2.0)))
v320_e.rotate((-0.22 + (num(jsCall(v324_g)) * 0.1)))
v330_w = v320_e.createLinearGradient((-v326_f / 2.0), (-v327_D / 2.0), (v326_f / 2.0), (v327_D / 2.0))
v330_w!!.addColorStop(0.0, "rgba(200,225,245,.16)")
v330_w!!.addColorStop(0.5, "rgba(160,190,215,.05)")
v330_w!!.addColorStop(1.0, "rgba(200,225,245,.11)")
v320_e.fillStyle = v330_w!!
v320_e.strokeStyle = "rgba(220,240,255,.22)"
v320_e.lineWidth = 1.2
v320_e.beginPath()
if (true) {
v320_e.roundRect((-v326_f / 2.0), (-v327_D / 2.0), v326_f, v327_D, 12.0)
} else {
v320_e.rect((-v326_f / 2.0), (-v327_D / 2.0), v326_f, v327_D)
}
v320_e.fill()
v320_e.stroke()
v320_e.restore()
v325_d++
}
}
Y(v320_e, v321_c, v322_l, v324_g, 0.05)
null
}), jsObj("id" to "lh-ember", "name" to "Ember Glass", "accent" to "amber", "draw" to JsFn L49@{ args ->
var v331_e: Ctx = (args.getOrNull(0) as Ctx)
var v332_c: Any? = args.getOrNull(1)
var v333_l: Any? = args.getOrNull(2)
var v334_i: Any? = args.getOrNull(3)
var v335_g: Any? = args.getOrNull(4)
v331_e.fillStyle = a(v331_e, v332_c, v333_l, jsArr(jsArr(0.0, "#120804"), jsArr(0.6, "#3a1806"), jsArr(1.0, "#080302")))
v331_e.fillRect(0.0, 0.0, num(v332_c), num(v333_l))
S(v331_e, v332_c, v333_l, jsObj("lines" to 60.0, "spread" to 0.2, "width" to 0.0015, "alpha" to 0.09, "c1" to "#ffb347", "c2" to "#ff5e2b", "y0" to 0.3, "cx1" to 0.3, "cy1" to 0.85, "cx2" to 0.65, "cy2" to 0.2, "y1" to 0.7, "twist" to 1.4))
ie(v331_e, v332_c, v333_l, v335_g, 26.0, jsArr("#ffb35a", "#ff7b2e", "#ffd9a0"), 0.01, 0.05, 0.16)
Y(v331_e, v332_c, v333_l, v335_g, 0.05)
null
}))
}
