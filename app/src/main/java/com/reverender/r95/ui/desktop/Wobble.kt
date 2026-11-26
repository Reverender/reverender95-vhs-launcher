package com.reverender.r95.ui.desktop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue

/**
 * The website's wobbly windows (updateWobblyWindows in rr-app-core.js): a damped spring on
 * rotation, skew and scale per window, CSS transform order rotateZ · skew · scale around the centre.
 */
class Wobble {
    var rot = 0.0; var vRot = 0.0; var targetRot = 0.0
    var skewX = 0.0; var vSkewX = 0.0; var targetSkewX = 0.0
    var skewY = 0.0; var vSkewY = 0.0; var targetSkewY = 0.0
    var scaleX = 1.0; var vScaleX = 0.0; var targetScaleX = 1.0
    var scaleY = 1.0; var vScaleY = 0.0; var targetScaleY = 1.0
    var settled = true
    var dragging = false
    /** redraw signal for this window only (other windows are not redrawn while one wobbles) */
    var tick by androidx.compose.runtime.mutableIntStateOf(0)
    private var wasMoving = false

    fun reset() {
        rot = 0.0; vRot = 0.0; targetRot = 0.0; skewX = 0.0; vSkewX = 0.0; targetSkewX = 0.0
        skewY = 0.0; vSkewY = 0.0; targetSkewY = 0.0; scaleX = 1.0; vScaleX = 0.0; targetScaleX = 1.0
        scaleY = 1.0; vScaleY = 0.0; targetScaleY = 1.0; settled = true
    }

    /** One animation frame. Returns true while the window is still moving. */
    fun step(): Boolean {
        val r = stepImpl()
        if (r || wasMoving) tick++ // one more redraw after it settles, at rest
        wasMoving = r
        return r
    }
    private fun stepImpl(): Boolean {
        if (settled && targetRot == 0.0 && targetSkewX == 0.0 && targetSkewY == 0.0 && targetScaleX == 1.0 && targetScaleY == 1.0) return false
        val e = 0.22; val a = 0.74
        targetRot *= 0.82; targetSkewX *= 0.82; targetSkewY *= 0.82
        targetScaleX += (1 - targetScaleX) * 0.25; targetScaleY += (1 - targetScaleY) * 0.25
        vRot = (vRot + (targetRot - rot) * e) * a; rot += vRot
        vSkewX = (vSkewX + (targetSkewX - skewX) * e) * a; skewX += vSkewX
        vSkewY = (vSkewY + (targetSkewY - skewY) * e) * a; skewY += vSkewY
        vScaleX = (vScaleX + (targetScaleX - scaleX) * e) * a; scaleX += vScaleX
        vScaleY = (vScaleY + (targetScaleY - scaleY) * e) * a; scaleY += vScaleY
        val done = Math.abs(targetRot) < .001 && Math.abs(vRot) < .001 && Math.abs(rot) < .05 &&
            Math.abs(targetSkewX) < .001 && Math.abs(vSkewX) < .001 && Math.abs(skewX) < .05 &&
            Math.abs(targetSkewY) < .001 && Math.abs(vSkewY) < .001 && Math.abs(skewY) < .05 &&
            Math.abs(targetScaleX - 1) < 5e-4 && Math.abs(vScaleX) < 5e-4 && Math.abs(scaleX - 1) < .002 &&
            Math.abs(targetScaleY - 1) < 5e-4 && Math.abs(vScaleY) < 5e-4 && Math.abs(scaleY - 1) < .002
        if (done) { reset(); return false }
        settled = false
        return true
    }

    val moving: Boolean get() = Math.abs(rot) > .05 || Math.abs(skewX) > .05 || Math.abs(skewY) > .05 || Math.abs(1 - scaleX) > .002 || Math.abs(1 - scaleY) > .002

    // ---- the site's triggers
    fun dragStart() { targetScaleX = 1.02; targetScaleY = 0.98 }
    /** [dx], [dy]: pointer movement since the last event, in CSS px (dp). */
    fun dragMove(dx: Double, dy: Double) {
        targetRot = (-dx * 0.45).coerceIn(-14.0, 14.0)
        targetSkewX = (-dx * 0.35).coerceIn(-12.0, 12.0)
        targetSkewY = (dy * 0.22).coerceIn(-8.0, 8.0)
    }
    fun dragEnd() { targetScaleX = 1.0; targetScaleY = 1.0 }
    fun pop() { targetScaleX = 1.05; targetScaleY = 0.95; targetRot = (Math.random() - 0.5) * 6 }
    fun glitch(e: Double) { targetScaleX = 1 + 0.016 * e; targetScaleY = 1 - 0.014 * e }
}

/** Frame counter that ticks while any window wobbles, so frames redraw. */
object WobbleClock { var tick by mutableLongStateOf(0L) }
