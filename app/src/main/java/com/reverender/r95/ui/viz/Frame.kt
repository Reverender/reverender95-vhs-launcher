package com.reverender.r95.ui.viz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.graphics.Color
import com.reverender.r95.LocalAppState

/**
 * Seconds since first composition, ticking every display frame (or ~30 fps in low-power mode).
 * Reading `.value` inside a Canvas redraws it each tick without recomposing anything else.
 */
@Composable
fun rememberFrameTime(running: Boolean = true): State<Float> {
    val lowPower = LocalAppState.current.lowPower
    val t = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(running, lowPower) {
        if (!running) return@LaunchedEffect
        var start = -1L
        var last = 0L
        while (true) {
            com.reverender.r95.core.AppVisibility.awaitForeground()
            withFrameNanos { now ->
                if (start < 0) start = now - (t.floatValue * 1e9f).toLong()
                val minStep = if (lowPower) 33_000_000L else com.reverender.r95.core.Perf.minStepNs
                if (now - last >= minStep) {
                    last = now
                    t.floatValue = (now - start) / 1e9f
                }
            }
        }
    }
    return t
}

fun argb(v: Long): Color = Color(v)

fun Color.withA(a: Float): Color = copy(alpha = a.coerceIn(0f, 1f))
