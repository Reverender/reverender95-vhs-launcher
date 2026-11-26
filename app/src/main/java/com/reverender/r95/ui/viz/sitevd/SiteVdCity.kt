// GENERATED from the REVERENDER_95 website source by tools/tx.js — do not edit by hand.
@file:Suppress("NAME_SHADOWING", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "UNUSED_VALUE", "VARIABLE_WITH_REDUNDANT_INITIALIZER", "LocalVariableName", "FunctionName", "UNUSED_ANONYMOUS_PARAMETER", "UNNECESSARY_NOT_NULL_ASSERTION", "SENSELESS_COMPARISON", "UNREACHABLE_CODE", "KotlinConstantConditions", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNCHECKED_CAST", "USELESS_CAST", "RedundantExplicitType", "CanBeVal", "unused", "LiftReturnOrAssignment", "CascadeIf", "ReplaceWithOperatorAssignment", "ControlFlowWithEmptyBody", "ReplaceJavaStaticMethodWithKotlinAnalog", "ReplaceManualRangeWithIndicesCalls", "IntroduceWhenSubject", "RedundantIf", "SimplifyBooleanWithConstants")
package com.reverender.r95.ui.viz.sitevd

import com.reverender.r95.ui.viz.js.*

var g_Ge: Any? = null

fun rrVdRing(v5_t: Double = Double.NaN, v6_crit: Boolean = false): Unit {

jsPush(g_Ge, jsObj("t" to v5_t, "crit" to v6_crit))
if ((jsLen(g_Ge) > 12.0)) {
jsShift(g_Ge)
}
}

fun ze(v7_C: String = "undefined"): Any? {

return Ext.call("vdOwned", v7_C)
return null
}

fun Je(v8_C: Double = Double.NaN, v9_h: Double = Double.NaN): Double {

var v10_te: Double = 0.0
v10_te = (kotlin.math.sin(((v8_C * 127.1) + (v9_h * 311.7))) * 43758.5453)
return (v10_te - kotlin.math.floor(v10_te))
return Double.NaN
}

fun rrVdCity(v11_h: Ctx, v12_te: Double = Double.NaN, v13_fe: Double = Double.NaN, v14_C: Double = Double.NaN): Unit {

var v15_u: Any? = null
var v16_F: Double = 0.0
var v17_S_: Any? = null
var v18_E: Double = 0.0
var v19_Z: Double = 0.0
var v20_ee: Double = 0.0
var v21_j: Double = 0.0
var v22_ye: Any? = null
var v23_Me: Any? = null
var v24_Pe: Double = 0.0
var v25_xe: Double = 0.0
var v26_qe: Double = 0.0
var v27_et: Double = 0.0
var v28_rt: Grad? = null
var v29_De: Double = 0.0
var v30_Ye: Double = 0.0
var v31_r: Grad? = null
var v32_s: Double = 0.0
var v33_y: Double = 0.0
var v34_I: Double = 0.0
var v35_re: Double = 0.0
var v36_ve: Double = 0.0
var v37_Te: Double = 0.0
var v38__e: Double = 0.0
var v39_Ne: Double = 0.0
var v40_Qe: Double = 0.0
var v41_He: Double = 0.0
var v42_We: Double = 0.0
var v43_je: Double = 0.0
var v44_Ve: Double = 0.0
var v45_nt: Double = 0.0
var v46_lt: Double = 0.0
var v47_dt: Double = 0.0
var v48_ct: Double = 0.0
var v49_xt: Double = 0.0
var v50_Ot: Double = 0.0
var v51_Yt: Double = 0.0
var v52_S_t: Double = 0.0
var v53_ft: Double = 0.0
var v54_Pt: Double = 0.0
var v55_Kt: Double = 0.0
var v56_Jt: Double = 0.0
var v57_Xt: Double = 0.0
var v58_wt: Any? = null
var v59_vt: Double = 0.0
var v60_gt: Double = 0.0
var v61_pt: Double = 0.0
var v62_yt: Double = 0.0
var v63_Qt: Boolean = false
var v64_kt: Double = 0.0
var v65_bt: Any? = null
var v66_St: Any? = null
var v67_ht: Double = 0.0
var v68_It: Double = 0.0
var v69_Mt: Double = 0.0
var v70_qt: Double = 0.0
var v71_Zt: Double = 0.0
var v72_st: Double = 0.0
var v73_er: Double = 0.0
var v74_tr: Double = 0.0
var v75_Bt: Boolean = false
var v76__t: Any? = null
var v77_zt: Double = 0.0
var v78_rr: Double = 0.0
var v79_At: Double = 0.0
var v80_nr: Double = 0.0
var v81_Dt: Double = 0.0
var v82_Ct: Double = 0.0
var v83_Et: Double = 0.0
var v84_Nt: Double = 0.0
var v85_Vt: Double = 0.0
var v86_Wt: Double = 0.0
var v87_Ut: Double = 0.0
var v88_Ht: Double = 0.0
var v89_jt: Double = 0.0
var v90_Tt: Double = 0.0
var v91_Gt: Double = 0.0
var v92_Rt: Grad? = null
var v93_Lt: Double = 0.0
var v94_Ft: Any? = null
var v95_mt: Double = 0.0
if ((!(truthy(v12_te)) || !(truthy(v13_fe)))) {
return
}
v11_h.clearRect(0.0, 0.0, v12_te, v13_fe)
v15_u = Ext.v("vdBought")
v16_F = (1.0 + jsMin(2.0, (num(v15_u) * 0.06)))
v17_S_ = Ext.v("vdOverdrive")
v18_E = (v12_te * 0.5)
v19_Z = (v13_fe * 0.44)
v20_ee = (v13_fe * 0.74)
v21_j = (jsMin(v12_te, v13_fe) / 100.0)
v22_ye = Ext.v("vdBass")
v23_Me = Ext.v("vdReduced")
v11_h.save()
v11_h.globalCompositeOperation = "lighter"
if (truthy(ze("dimension"))) {
run {
v24_Pe = 0.0
L0@ while ((v24_Pe < 2.0)) {
v11_h.beginPath()
run {
v25_xe = 0.0
L1@ while ((v25_xe <= v12_te)) {
v26_qe = ((v13_fe * (0.12 + (v24_Pe * 0.07))) + ((kotlin.math.sin(((((v25_xe / v12_te) * 6.0) + (v14_C * 0.4)) + v24_Pe)) * v13_fe) * 0.04))
if (truthy(v25_xe)) { v11_h.lineTo(v25_xe, v26_qe) } else { v11_h.moveTo(v25_xe, v26_qe) }
v25_xe += (v12_te / 40.0)
}
}
v11_h.strokeStyle = (if (truthy(v24_Pe)) "rgba(255,90,220,.22)" else "rgba(90,255,220,.22)")
v11_h.lineWidth = ((6.0 * v21_j) * 0.4)
v11_h.stroke()
v24_Pe++
}
}
}
if (truthy(ze("nebulacore"))) {
run {
v27_et = 0.0
L2@ while ((v27_et < 3.0)) {
v28_rt = v11_h.createRadialGradient((v12_te * (0.2 + (v27_et * 0.3))), (v13_fe * 0.2), 0.0, (v12_te * (0.2 + (v27_et * 0.3))), (v13_fe * 0.2), (v12_te * 0.18))
v28_rt!!.addColorStop(0.0, "rgba(150,90,255,.18)")
v28_rt!!.addColorStop(1.0, "rgba(150,90,255,0)")
v11_h.fillStyle = v28_rt!!
v11_h.fillRect(0.0, 0.0, v12_te, (v13_fe * 0.5))
v27_et++
}
}
}
if (truthy(ze("omnigrid"))) {
v11_h.strokeStyle = "rgba(105,247,255,.14)"
v11_h.lineWidth = 1.0
run {
v29_De = -10.0
L3@ while ((v29_De <= 10.0)) {
v11_h.beginPath()
v11_h.moveTo((v18_E + ((v29_De * v12_te) * 0.02)), 0.0)
v11_h.lineTo((v18_E + ((v29_De * v12_te) * 0.12)), (v13_fe * 0.28))
v11_h.stroke()
v29_De++
}
}
}
if (truthy(ze("timeloop"))) {
v11_h.setLineDash(jsArr(((6.0 * v21_j) * 0.3), ((8.0 * v21_j) * 0.3)))
run {
v30_Ye = 0.0
L4@ while ((v30_Ye < jsMin(4.0, num(ze("timeloop"))))) {
v11_h.beginPath()
v11_h.strokeStyle = "rgba(255,227,110,.35)"
v11_h.lineWidth = 1.5
v11_h.arc(v18_E, v19_Z, ((26.0 + (v30_Ye * 6.0)) * v21_j), (v14_C * (0.3 + (v30_Ye * 0.1))), ((v14_C * (0.3 + (v30_Ye * 0.1))) + (Math.PI * 1.6)))
v11_h.stroke()
v30_Ye++
}
}
v11_h.setLineDash(jsArr())
}
if (truthy(ze("singularity"))) {
v31_r = v11_h.createRadialGradient(v18_E, v19_Z, 0.0, v18_E, v19_Z, ((20.0 * v21_j) * (1.0 + (kotlin.math.sin((v14_C * 2.0)) * 0.1))))
v31_r!!.addColorStop(0.0, "rgba(255,255,255,.55)")
v31_r!!.addColorStop(0.4, "rgba(255,84,216,.25)")
v31_r!!.addColorStop(1.0, "rgba(255,84,216,0)")
v11_h.fillStyle = v31_r!!
v11_h.fillRect(0.0, 0.0, v12_te, v13_fe)
}
run {
v32_s = 0.0
L5@ while ((v32_s < jsMin(4.0, num(ze("portal"))))) {
v33_y = (v12_te * (if (truthy((v32_s % 2.0))) 0.86 else 0.14))
v34_I = (v13_fe * (0.3 + (kotlin.math.floor((v32_s / 2.0)) * 0.22)))
run {
v35_re = 0.0
L6@ while ((v35_re < 3.0)) {
v11_h.beginPath()
v11_h.ellipse(v33_y, v34_I, ((7.0 - (v35_re * 2.0)) * v21_j), ((10.0 - (v35_re * 2.5)) * v21_j), ((v14_C * (1.0 + v35_re)) * (if (truthy((v32_s % 2.0))) 1.0 else -1.0)), 0.0, (Math.PI * 1.7))
v11_h.strokeStyle = (if (truthy((v35_re % 2.0))) "rgba(255,84,216,.6)" else "rgba(105,247,255,.6)")
v11_h.lineWidth = 1.6
v11_h.stroke()
v35_re++
}
}
v32_s++
}
}
run {
v36_ve = 0.0
L7@ while ((v36_ve < jsMin(12.0, num(ze("sculptor"))))) {
v37_Te = (((v14_C * 0.25) * v16_F) + (v36_ve * 0.9))
v38__e = ((30.0 + (Je(v36_ve, 1.0) * 14.0)) * v21_j)
v39_Ne = (v18_E + ((kotlin.math.cos(v37_Te) * v38__e) * 1.3))
v40_Qe = (v19_Z + ((kotlin.math.sin(v37_Te) * v38__e) * 0.55))
v41_He = (3.0 + (v36_ve % 3.0))
v42_We = ((2.2 + (Je(v36_ve, 2.0) * 1.6)) * v21_j)
v11_h.beginPath()
run {
v43_je = 0.0
L8@ while ((v43_je <= v41_He)) {
v44_Ve = ((v14_C * (0.8 + (v36_ve * 0.05))) + (((v43_je / v41_He) * Math.PI) * 2.0))
v45_nt = (v39_Ne + (kotlin.math.cos(v44_Ve) * v42_We))
v46_lt = (v40_Qe + (kotlin.math.sin(v44_Ve) * v42_We))
if (truthy(v43_je)) { v11_h.lineTo(v45_nt, v46_lt) } else { v11_h.moveTo(v45_nt, v46_lt) }
v43_je++
}
}
v11_h.strokeStyle = "rgba(167,139,255,.75)"
v11_h.lineWidth = 1.3
v11_h.stroke()
v36_ve++
}
}
v47_dt = jsMin((if (truthy(v23_Me)) 18.0 else 40.0), num(ze("cursor")))
run {
v48_ct = 0.0
L9@ while ((v48_ct < v47_dt)) {
v49_xt = (v48_ct % 3.0)
v50_Ot = ((18.0 + (v49_xt * 6.0)) * v21_j)
v51_Yt = (((0.5 + (v49_xt * 0.18)) * v16_F) * (if (truthy(v17_S_)) 2.0 else 1.0))
v52_S_t = (((v48_ct / jsMax(1.0, v47_dt)) * Math.PI) * 2.0)
run {
v53_ft = 0.0
L10@ while ((v53_ft < (if (truthy(v23_Me)) 1.0 else 4.0))) {
v54_Pt = (((v14_C * v51_Yt) + v52_S_t) - (v53_ft * 0.06))
v55_Kt = (v18_E + ((kotlin.math.cos(v54_Pt) * v50_Ot) * 1.35))
v56_Jt = (v19_Z + ((kotlin.math.sin(v54_Pt) * v50_Ot) * 0.6))
v11_h.fillStyle = (if (truthy(v53_ft)) (("rgba(105,247,255," + jsStr((0.35 - (v53_ft * 0.08)))) + ")") else (if ((v49_xt == 1.0)) "#ff8ae6" else "#bffaff"))
v11_h.beginPath()
v11_h.arc(v55_Kt, v56_Jt, (((if (truthy(v53_ft)) 0.9 else 1.4) * v21_j) * 0.6), 0.0, (Math.PI * 2.0))
v11_h.fill()
v53_ft++
}
}
v48_ct++
}
}
v11_h.restore()
v57_Xt = jsMin(10.0, num(ze("tower")))
v58_wt = jsArr()
run {
v59_vt = 0.0
L11@ while ((v59_vt < v57_Xt)) {
v60_gt = (v12_te * (0.08 + (Je(v59_vt, 3.0) * 0.84)))
v61_pt = (v13_fe * (0.18 + (Je(v59_vt, 4.0) * 0.14)))
v62_yt = (2.4 * v21_j)
jsPush(v58_wt, jsArr(v60_gt, (v20_ee - v61_pt)))
v11_h.fillStyle = "rgba(20,10,45,.92)"
v11_h.fillRect((v60_gt - (v62_yt / 2.0)), (v20_ee - v61_pt), v62_yt, v61_pt)
v11_h.strokeStyle = "rgba(255,84,216,.7)"
v11_h.lineWidth = 1.0
v11_h.strokeRect((v60_gt - (v62_yt / 2.0)), (v20_ee - v61_pt), v62_yt, v61_pt)
v63_Qt = (kotlin.math.sin(((v14_C * 3.0) + v59_vt)) > 0.4)
v11_h.fillStyle = (if (v63_Qt) "#ff3b5c" else "rgba(255,59,92,.25)")
v11_h.beginPath()
v11_h.arc(v60_gt, ((v20_ee - v61_pt) - (1.5 * v21_j)), (0.9 * v21_j), 0.0, (Math.PI * 2.0))
v11_h.fill()
v59_vt++
}
}
if ((truthy(ze("skybridge")) && (jsLen(v58_wt) > 1.0))) {
v11_h.strokeStyle = "rgba(105,247,255,.55)"
v11_h.lineWidth = 1.4
run {
v64_kt = 0.0
L12@ while (((v64_kt < (jsLen(v58_wt) - 1.0)) && (v64_kt < 6.0))) {
v65_bt = jsGet(v58_wt, v64_kt)
v66_St = jsGet(v58_wt, (v64_kt + 1.0))
v11_h.beginPath()
v11_h.moveTo(num(jsGet(v65_bt, 0.0)), (num(jsGet(v65_bt, 1.0)) + (3.0 * v21_j)))
v11_h.quadraticCurveTo(((num(jsGet(v65_bt, 0.0)) + num(jsGet(v66_St, 0.0))) / 2.0), (jsMin(num(jsGet(v65_bt, 1.0)), num(jsGet(v66_St, 1.0))) - (6.0 * v21_j)), num(jsGet(v66_St, 0.0)), (num(jsGet(v66_St, 1.0)) + (3.0 * v21_j)))
v11_h.stroke()
v64_kt++
}
}
}
run {
v67_ht = 0.0
L13@ while ((v67_ht < jsMin(8.0, num(ze("hologram"))))) {
v68_It = (v12_te * (0.05 + (Je(v67_ht, 5.0) * 0.9)))
v69_Mt = (v13_fe * (0.08 + (Je(v67_ht, 6.0) * 0.12)))
v70_qt = ((4.0 + (Je(v67_ht, 7.0) * 4.0)) * v21_j)
v11_h.strokeStyle = (("rgba(105,247,255," + jsStr((0.35 + (kotlin.math.sin(((v14_C * 2.0) + v67_ht)) * 0.15)))) + ")")
v11_h.lineWidth = 1.0
v11_h.strokeRect(v68_It, (v20_ee - v69_Mt), v70_qt, v69_Mt)
v11_h.fillStyle = "rgba(105,247,255,.06)"
v11_h.fillRect(v68_It, (v20_ee - v69_Mt), v70_qt, v69_Mt)
v67_ht++
}
}
v71_Zt = jsMin(30.0, num(ze("sign")))
run {
v72_st = 0.0
L14@ while ((v72_st < v71_Zt)) {
v73_er = (v12_te * (0.04 + (Je(v72_st, 8.0) * 0.92)))
v74_tr = (v20_ee - (v13_fe * (0.03 + (Je(v72_st, 9.0) * 0.1))))
v75_Bt = (kotlin.math.sin(((v14_C * (2.0 + (Je(v72_st, 10.0) * 4.0))) + (v72_st * 7.0))) > -0.6)
v11_h.fillStyle = (if (v75_Bt) (if (((v72_st % 3.0) == 0.0)) "#ff54d8" else (if (((v72_st % 3.0) == 1.0)) "#69f7ff" else "#ffe36e")) else "rgba(255,255,255,.08)")
v11_h.globalAlpha = (if (v75_Bt) 0.85 else 1.0)
v11_h.fillRect(v73_er, v74_tr, ((2.2 + (Je(v72_st, 11.0) * 2.0)) * v21_j), (0.9 * v21_j))
v11_h.globalAlpha = 1.0
v72_st++
}
}
v76__t = ze("grid")
if (truthy(v76__t)) {
v77_zt = jsMin(14.0, (4.0 + num(v76__t)))
v78_rr = (((v14_C * 0.6) * v16_F) % 1.0)
v11_h.strokeStyle = (("rgba(255,84,216," + jsStr(jsMin(0.6, (0.25 + (num(v76__t) * 0.02))))) + ")")
v11_h.lineWidth = 1.0
run {
v79_At = 0.0
L15@ while ((v79_At < v77_zt)) {
v80_nr = ((v79_At + v78_rr) / v77_zt)
v81_Dt = (v20_ee + (Math.pow(v80_nr, 2.0) * (v13_fe - v20_ee)))
v11_h.beginPath()
v11_h.moveTo(0.0, v81_Dt)
v11_h.lineTo(v12_te, v81_Dt)
v11_h.stroke()
v79_At++
}
}
run {
v82_Ct = -8.0
L16@ while ((v82_Ct <= 8.0)) {
v11_h.beginPath()
v11_h.moveTo((v18_E + ((v82_Ct * v12_te) * 0.02)), v20_ee)
v11_h.lineTo((v18_E + ((v82_Ct * v12_te) * 0.14)), v13_fe)
v11_h.stroke()
v82_Ct++
}
}
}
run {
v83_Et = 0.0
L17@ while ((v83_Et < jsMin(16.0, num(ze("arcade"))))) {
v84_Nt = (v12_te * (0.06 + ((v83_Et / 16.0) * 0.88)))
v85_Vt = (v20_ee + ((v13_fe - v20_ee) * 0.35))
v86_Wt = (3.0 * v21_j)
v87_Ut = (5.0 * v21_j)
v11_h.fillStyle = "rgba(25,12,50,.95)"
v11_h.fillRect(v84_Nt, v85_Vt, v86_Wt, v87_Ut)
v11_h.fillStyle = (("hsl(" + jsStr((((v14_C * 120.0) + (v83_Et * 40.0)) % 360.0))) + ",90%,65%)")
v11_h.fillRect((v84_Nt + (0.5 * v21_j)), (v85_Vt + (0.6 * v21_j)), (v86_Wt - v21_j), (v87_Ut * 0.4))
v83_Et++
}
}
if (truthy(ze("soundwave"))) {
v88_Ht = 24.0
v89_jt = (v12_te / v88_Ht)
run {
v90_Tt = 0.0
L18@ while ((v90_Tt < v88_Ht)) {
v91_Gt = ((0.25 + (num(v22_ye) * 0.9)) * (0.4 + (0.6 * kotlin.math.abs(kotlin.math.sin(((v14_C * 4.0) + (v90_Tt * 0.7)))))))
v11_h.fillStyle = "rgba(105,247,255,.35)"
v11_h.fillRect(((v90_Tt * v89_jt) + 1.0), (v13_fe - ((v91_Gt * v13_fe) * 0.12)), (v89_jt - 2.0), ((v91_Gt * v13_fe) * 0.12))
v90_Tt++
}
}
}
if (truthy(ze("sun"))) {
v92_Rt = v11_h.createRadialGradient(v18_E, v20_ee, 0.0, v18_E, v20_ee, (v12_te * 0.45))
v92_Rt!!.addColorStop(0.0, "rgba(255,190,90,.28)")
v92_Rt!!.addColorStop(1.0, "rgba(255,120,60,0)")
v11_h.fillStyle = v92_Rt!!
v11_h.fillRect(0.0, 0.0, v12_te, v13_fe)
}
v11_h.save()
v11_h.globalCompositeOperation = "lighter"
run {
v93_Lt = (jsLen(g_Ge) - 1.0)
L19@ while ((v93_Lt >= 0.0)) {
L19c@ do {
v94_Ft = jsGet(g_Ge, v93_Lt)
v95_mt = ((v14_C - num(jsGet(v94_Ft, "t"))) / 0.7)
if ((v95_mt >= 1.0)) {
jsSplice(g_Ge, v93_Lt, 1.0)
break@L19c
}
v11_h.beginPath()
v11_h.ellipse(v18_E, v19_Z, (((6.0 + (v95_mt * 40.0)) * v21_j) * 1.3), (((6.0 + (v95_mt * 40.0)) * v21_j) * 0.6), 0.0, 0.0, (Math.PI * 2.0))
v11_h.strokeStyle = (((if (truthy(jsGet(v94_Ft, "crit"))) "rgba(255,227,110," else "rgba(105,247,255,") + jsStr(((1.0 - v95_mt) * 0.8))) + ")")
v11_h.lineWidth = (((if (truthy(jsGet(v94_Ft, "crit"))) 3.0 else 1.6) * (1.0 - v95_mt)) + 0.5)
v11_h.stroke()
} while (false)
v93_Lt--
}
}
if (truthy(v17_S_)) {
v11_h.fillStyle = (("rgba(255,84,216," + jsStr((0.06 + (kotlin.math.sin((v14_C * 8.0)) * 0.03)))) + ")")
v11_h.fillRect(0.0, 0.0, v12_te, v13_fe)
}
v11_h.restore()
}

fun SiteVdCityInit() {
g_Ge = jsArr()
}
