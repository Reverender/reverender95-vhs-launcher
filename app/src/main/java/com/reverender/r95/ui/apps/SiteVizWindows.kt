package com.reverender.r95.ui.apps

import androidx.compose.ui.graphics.Color

import androidx.compose.runtime.remember

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reverender.r95.ui.desktop.Win
import com.reverender.r95.ui.viz.js.Dom
import com.reverender.r95.ui.viz.js.num
import com.reverender.r95.ui.viz.js.str
import com.reverender.r95.ui.viz.site.SiteCanvas
import com.reverender.r95.ui.viz.site.SiteHost
import com.reverender.r95.ui.viz.site.VizFx
import com.reverender.r95.ui.win95.Btn
import com.reverender.r95.ui.win95.Slider95
import com.reverender.r95.ui.win95.StatusCell
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import com.reverender.r95.ui.viz.siteviz.rrVizSetMesh

/** Status bar of a site window: cells with an id are live (the site's code writes them). */
private val STATUS = mapOf(
    "waterfall" to listOf("" to "3D Z-BUFFER SPECTROGRAM", "bass-meter" to "BASS: 0%"),
    "plasma" to listOf("" to "3D GYRO HARMONIC CORE", "high-meter" to "TREBLE: 0%"),
    "cybergrid" to listOf("" to "OCCLUDED Z-BUFFER MESH", "canyon-alt" to "ALT: 1200M"),
    "matrix" to listOf("" to "3-LAYER DEPTH MATRIX", "" to "60 FPS STREAM"),
    "ribbon" to listOf("" to "3D PERSPECTIVE RIBBON", "mid-meter" to "MID: 0%"),
    "vumeter" to listOf("" to "L/R BALLISTIC + LED BAR", "vu-db-readout" to "PEAK: -6.0 dB"),
    "orbital" to listOf("" to "3D ORBITAL SYSTEM", "" to "GRAVITY: LIVE"),
    "fractal" to listOf("" to "RECURSIVE ORGANIC GEOMETRY", "" to "GROWTH FIELD"),
    "prism" to listOf("" to "GLASS REFRACTION ENGINE", "" to "RGB DISPERSION"),
    "sonar" to listOf("" to "ACTIVE ECHO MAPPING", "" to "PING / RETURN"),
    "magnetic" to listOf("" to "MAGNETIC FLUX LINES", "" to "POLARITY: ALTERNATING"),
    "kaleido" to listOf("" to "MIRRORED RADIAL OPTICS", "" to "12-FOLD SYMMETRY"),
    "starforge" to listOf("" to "PROCEDURAL ASTEROID FIELD", "" to "WARP VECTOR"),
)

@Composable
fun SiteStatusBar(cells: List<Pair<String, String>>) {
    SiteHost.uiFrame
    Row(Modifier.fillMaxWidth().padding(top = 3.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        cells.forEach { (id, def) -> StatusCell(if (id.isEmpty()) def else SiteHost.text(id).ifEmpty { def }, Modifier.weight(1f)) }
    }
}

/** A site visualizer window: its canvas, its own controls, status bar, and the FX panel. */
@Composable
fun SiteVizWindow(appId: String, win: Win?) {
    val fx = win?.vizFx ?: VizFx()
    Box(Modifier.fillMaxSize().padding(4.dp)) {
        Column(Modifier.fillMaxSize()) {
            SiteCanvas("canvas-$appId", Modifier.fillMaxWidth().weight(1f).border(1.dp, W95.Border), fx)
            when (appId) {
                "hypercube" -> Row(Modifier.fillMaxWidth().padding(top = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Btn("Icosahedron", { rrVizSetMesh("icosa") }, size = 12.sp)
                    Btn("Cyber Torus", { rrVizSetMesh("torus") }, size = 12.sp)
                    Btn("DNA Spiral", { rrVizSetMesh("helix") }, size = 12.sp)
                }
                "tunnel" -> Row(Modifier.fillMaxWidth().padding(top = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                    T("WARP:", bold = true, size = 11.sp)
                    Spacer(Modifier.width(4.dp))
                    SiteRange("tunnel-speed", 0.5f, 5f, Modifier.weight(1f))
                    Spacer(Modifier.width(4.dp))
                    Btn("❖ Shape", { win?.let { w -> w.shape = SHAPES[(SHAPES.indexOf(w.shape) + 1) % SHAPES.size] } }, size = 12.sp)
                }
            }
            STATUS[appId]?.let { SiteStatusBar(it) }
        }
        if (win?.fxOpen == true) VizFxPanel(fx, Modifier.align(Alignment.TopEnd).padding(top = 2.dp, end = 2.dp))
    }
}

val SHAPES = listOf("box", "octagon", "crt", "trapezoid")

/** A range input bound to the site page element [id] (its value is what the site's code reads). */
@Composable
fun SiteRange(id: String, min: Float, max: Float, modifier: Modifier = Modifier, onChange: ((Float) -> Unit)? = null) {
    SiteHost.uiFrame
    val el = Dom.el(id)
    val v = num(el.prop("value")).toFloat().let { if (it.isNaN()) min else it }
    Slider95((v - min) / (max - min), { f ->
        val nv = min + f * (max - min)
        el.setProp("value", str(Math.round(nv * 100) / 100.0))
        onChange?.invoke(nv)
    }, modifier)
}

@Composable
internal fun VizFxPanel(fx: VizFx, modifier: Modifier) {
    Column(
        modifier.width(150.dp).background(W95.Face, RoundedCornerShape(3.dp)).border(1.dp, W95.Border, RoundedCornerShape(3.dp)).padding(5.dp),
    ) {
        T("VISUALIZER FX", bold = true, size = 10.sp)
        @Composable fun row(label: String, value: Float, min: Float, max: Float, unit: String, set: (Float) -> Unit) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                T(label, size = 9.sp, modifier = Modifier.width(52.dp), maxLines = 2)
                Slider95((value - min) / (max - min), { set(Math.round(min + it * (max - min)).toFloat()) }, Modifier.weight(1f).height(18.dp))
                T("${value.toInt()}$unit", size = 9.sp, mono = true, modifier = Modifier.width(30.dp))
            }
        }
        row("HUE", fx.hue, 0f, 360f, "°") { fx.hue = it }
        row("SATURATION", fx.saturation, 0f, 220f, "%") { fx.saturation = it }
        row("CONTRAST", fx.contrast, 65f, 175f, "%") { fx.contrast = it }
        row("LOCAL GLOW", fx.glow, 0f, 100f, "%") { fx.glow = it }
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("RESET" to "reset", "VAPOR" to "vapor", "ICE" to "ice", "MONO" to "mono").forEach { (l, k) -> Btn(l, { fx.preset(k) }, Modifier.weight(1f), size = 8.sp) }
        }
        T("Effects are applied to this visualizer only.", size = 8.sp, color = W95.Shadow)
    }
}


/** The visualizer's canvas alone (the start menu's live preview). */
@Composable
fun SiteCanvasPreview(appId: String, modifier: Modifier) {
    val fx = remember { VizFx() }
    SiteCanvas("canvas-$appId", modifier.background(Color(0xFF05020D)).border(1.dp, W95.Border), fx)
}
