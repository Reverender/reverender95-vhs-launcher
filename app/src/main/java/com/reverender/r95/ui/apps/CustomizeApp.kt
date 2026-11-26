package com.reverender.r95.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reverender.r95.LocalAppState
import com.reverender.r95.audio.UiSfx
import com.reverender.r95.core.DisplayFx
import com.reverender.r95.ui.desktop.WindowManager
import com.reverender.r95.ui.viz.SolidColors
import com.reverender.r95.ui.viz.js.Dom
import com.reverender.r95.ui.viz.site.OsWallpaper
import com.reverender.r95.ui.viz.site.OsWallpaperThumb
import com.reverender.r95.ui.win95.Btn
import com.reverender.r95.ui.win95.Field
import com.reverender.r95.ui.win95.GroupBox
import com.reverender.r95.ui.win95.Skin
import com.reverender.r95.ui.win95.Slider95
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import com.reverender.r95.ui.win95.bevel

private val THEME_BTNS = listOf(
    "vaporwave" to "Vapor", "bw" to "B&W Noir", "matrix" to "Matrix", "outrun" to "Outrun", "ice" to "Cyber Ice",
    "sakura" to "Sakura", "acid" to "Acid 303", "amber" to "Amber CRT", "mallsoft" to "Mallsoft", "midnight" to "Midnight",
    "synthwave" to "Synthwave", "toxic" to "Toxic", "ocean" to "Deep Ocean", "crimson" to "Crimson", "lavender" to "Lavender",
)
internal val WORLDS_MULTI = listOf(0 to "01. Sunset Outrun", 1 to "02. Cyber City 99", 2 to "03. Helios Mall", 3 to "04. Twin Moons", 4 to "05. Fuji Canyon",
    5 to "06. Hyper-Atrium", 6 to "07. Arcade 95", 7 to "08. Poseidon Spa", 8 to "09. Chrono Drift", 24 to "10. Aether Spires", 25 to "11. Prism Citadel",
    26 to "12. Celestial Atrium", 27 to "13. Tidal Vault", 28 to "14. Chronosphere Hall")
internal val WORLDS_MORE = listOf(13 to "15. Neon Rain Alley", 14 to "16. Dreamwave Coast", 15 to "17. Chrome Cathedral", 16 to "18. Saturn Highway",
    17 to "19. Liminal Atrium", 18 to "20. Aurora Observatory", 19 to "21. Pixel Sunset")
internal val WORLDS_LIGHT = listOf(20 to "22. Low-Poly Dunes", 21 to "23. Neon Tunnel", 22 to "24. Liminal Columns", 23 to "25. Poolroom Rings")
internal val WORLDS_BACKROOMS = listOf(9 to "10. Level 0 Yellow", 10 to "11. Level 1 Halls", 11 to "12. Poolrooms Spa", 12 to "13. Abyssal Pools")
private val DEPTH_FX = listOf("gridWarp" to "3D Grid Wave", "solarRings" to "Solar Rings", "starWarp" to "Star Warp", "skySpectrum" to "Horizon FFT",
    "cyberRain" to "Cyber Rain", "monoliths" to "3D Props", "depthMotes" to "Depth Motes", "parallaxRings" to "Parallax Rings", "glassShards" to "Glass Shards",
    "auroraRibbons" to "Aurora Veil", "cometTrails" to "Comet Trails", "lightShafts" to "Light Shafts", "depthRidges" to "Depth Ridges")

/** A fixed-column grid of cells inside the scrolling column. */
@Composable
private fun Grid(count: Int, cols: Int, gap: Int = 3, cell: @Composable (Int, Modifier) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(gap.dp)) {
        for (r in 0 until (count + cols - 1) / cols) Row(horizontalArrangement = Arrangement.spacedBy(gap.dp)) {
            for (c in 0 until cols) { val i = r * cols + c; if (i < count) cell(i, Modifier.weight(1f)) else Spacer(Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun Note(text: String) = T(text, size = 9.sp, mono = true, color = W95.Text, modifier = Modifier.fillMaxWidth().padding(top = 4.dp).background(Color(0x14000000)).padding(5.dp))

/** CUSTOMIZE_95.EXE — [Wallpapers, Colors & Depth FX] */
@Composable
fun CustomizeApp(wm: WindowManager) {
    val state = LocalAppState.current
    val lh = state.osStyle == "longhorn"
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(4.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        GroupBox("Desktop Style — your wallpaper stays as it is") {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(Triple("win95", "Windows 95", "Classic + vaporwave") to "w95-dither", Triple("longhorn", "Longhorn", "Dark glass, 2005") to "lh-aurora",
                    Triple("vhs", "VHS", "VCR menu, light") to "vhs").forEach { (t, thumb) ->
                    val sel = state.osStyle == t.first
                    Column(
                        Modifier.weight(1f).background(if (sel) W95.Face.copy(alpha = 0.7f) else W95.Face).bevel(!sel)
                            .clickable {
                                UiSfx.play()
                                if (state.osStyle != t.first) Dom.el("lcd-status").setProp("textContent", "DESKTOP STYLE: ${t.second.uppercase()}")
                                state.setOsStyle(t.first)
                            }.padding(4.dp),
                    ) {
                        if (thumb == "vhs") com.reverender.r95.ui.vhs.VhsThumb(Modifier.fillMaxWidth().aspectRatio(1.66f).border(1.dp, Color.Black))
                        else OsWallpaperThumb(thumb, state.theme.id, Modifier.fillMaxWidth().aspectRatio(1.66f).border(1.dp, Color.Black))
                        T(t.second, bold = true, size = 11.sp, modifier = Modifier.padding(top = 2.dp))
                        T(t.third, size = 9.sp, color = W95.Shadow)
                    }
                }
            }
        }
        if (lh) GroupBox("Longhorn Window Colors") {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                    com.reverender.r95.ui.win95.LH_ACCENTS.forEach { a ->
                        val sel = state.lhAccent == a.id
                        Row(
                            Modifier.weight(1f).background(if (sel) W95.Face.copy(alpha = 0.7f) else W95.Face).bevel(!sel).clickable { UiSfx.play(); state.setLhAccent(a.id) }.padding(horizontal = 4.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center,
                        ) {
                            Box(Modifier.size(12.dp).background(Brush.radialGradient(listOf(a.l, a.a, a.d)), CircleShape).border(1.dp, a.d, CircleShape))
                            Spacer(Modifier.width(4.dp)); T(a.label, size = 11.sp, maxLines = 1)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.End) { Btn("Clock themes…", { wm.open("clock") }, size = 11.sp) }
            }
        }
        GroupBox("Background Colors — 15 vaporwave & CRT themes (color the animated worlds)") {
            Grid(THEME_BTNS.size, 3) { i, m -> val (id, l) = THEME_BTNS[i]; Btn(l, { UiSfx.play(); state.setTheme(id) }, m, pressed = state.themeId == id, size = 12.sp) }
        }
        GroupBox("Style Wallpapers") {
            Column {
                @Composable fun set(title: String, list: List<OsWallpaper.Info>) {
                    T(title, size = 10.sp, bold = true, modifier = Modifier.padding(vertical = 3.dp))
                    Grid(list.size, 4, 4) { i, m ->
                        val w = list[i]; val sel = state.osWallpaper == w.id
                        Column(m.background(if (sel) W95.Face.copy(alpha = 0.7f) else W95.Face).bevel(!sel).clickable { UiSfx.play(); state.setOsWallpaper(w.id) }.padding(3.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            OsWallpaperThumb(w.id, state.theme.id, Modifier.fillMaxWidth().aspectRatio(1.66f).border(1.dp, Color.Black))
                            T(w.name, size = 10.sp, maxLines = 1)
                        }
                    }
                }
                set("Longhorn wallpapers", OsWallpaper.longhorn)
                set("Windows 95 wallpapers", OsWallpaper.win95)
                Note("Style wallpapers are drawn once when chosen, so they use no CPU while you work. Windows 95 wallpapers follow the color theme above. The animated worlds further down work in every style.")
            }
        }
        @Composable fun worlds(title: String, list: List<Pair<Int, String>>, note: String? = null) = GroupBox(title) {
            Column {
                Grid(list.size, 2) { i, m -> val (id, l) = list[i]; Btn(l, { UiSfx.play(); state.setWallpaper(id) }, m, pressed = state.osWallpaper == null && state.wallpaper == id, size = 12.sp) }
                note?.let { Note(it) }
            }
        }
        worlds("Multi-Layer 3D Worlds (14 Scenes)", WORLDS_MULTI)
        worlds("Additional Animated Worlds (7 Scenes)", WORLDS_MORE)
        worlds("Lightweight 3D Worlds (4 Scenes)", WORLDS_LIGHT, "These four projected 3D scenes use the lightweight Canvas renderer and no external 3D engine or texture downloads. They are also used automatically when the device needs a lower-cost scene.")
        worlds("Backrooms & Poolrooms (Liminal Worlds)", WORLDS_BACKROOMS)
        GroupBox("Solid Backgrounds — Win95/98, Vaporwave & CRT Palettes") {
            Column {
                Grid(SolidColors.size, 3) { i, m ->
                    val (name, c) = SolidColors[i]
                    val sel = state.osWallpaper == null && state.wallpaper < 0 && state.solidColor == c
                    Box(m) {
                        Btn(name, { UiSfx.play(); state.setSolid(c) }, Modifier.fillMaxWidth(), pressed = sel, size = 12.sp)
                        Box(Modifier.padding(start = 3.dp, top = 3.dp, bottom = 3.dp).width(4.dp).height(22.dp).background(Color(c)))
                    }
                }
                var hex by remember { mutableStateOf("#6a5cff") }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 5.dp)) {
                    T("Custom solid color:", size = 12.sp, bold = true)
                    Spacer(Modifier.width(6.dp))
                    val col = runCatching { Color(0xFF000000 or hex.removePrefix("#").toLong(16)) }.getOrNull()
                    Box(Modifier.size(width = 28.dp, height = 20.dp).background(col ?: Color.Gray).bevel(false))
                    Spacer(Modifier.width(4.dp))
                    Field(hex, { hex = it.take(7) }, Modifier.width(84.dp))
                    Spacer(Modifier.width(4.dp))
                    Btn("APPLY CUSTOM", { runCatching { state.setSolid(0xFF000000 or hex.removePrefix("#").toLong(16)) } }, size = 11.sp)
                }
            }
        }
        GroupBox("Background Depth FX (toggle any combination)") {
            Column {
                Grid(DEPTH_FX.size, 2) { i, m -> val (id, l) = DEPTH_FX[i]; Btn(l, { state.toggleLayer(id) }, m, pressed = state.bgLayers[id] == true, size = 12.sp) }
                Note("AUTO 3D QUALITY: ${if (state.lowPower) "LIGHTWEIGHT 3D" else "FULL SCENE"} // DEVICE: ${DisplayFx.hardwareTier().uppercase()}")
            }
        }
        GroupBox("Desktop Fun") { VaporToybox() }
    }
}

/** #rr-customize-desktop-fun: the VAPOR TOYBOX tiles. */
@Composable
private fun VaporToybox() {
    val state = LocalAppState.current
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFFFF00A0), Color(0xFF7A3FC8), Color(0xFF00C8FF))), RoundedCornerShape(3.dp)).padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            T("✨ VAPOR TOYBOX", color = Color.White, bold = true, size = 12.sp, modifier = Modifier.weight(1f))
            T("DESKTOP FUN & DISPLAY FX", color = Color.White, bold = true, size = 8.sp)
        }
        ToyGroup("🌈 Glow & colour") {
            Grid(3, 3) { i, m ->
                when (i) {
                    0 -> Toy("🌈", "Bloom glow", DisplayFx.bloom, m) { DisplayFx.setBloom(!DisplayFx.bloom) }
                    1 -> Toy("🔴", "RGB split", DisplayFx.chromatic, m) { DisplayFx.setChromatic(!DisplayFx.chromatic) }
                    else -> Toy("📺", "CRT scanlines", state.scanlines, m) { state.setScanlines(!state.scanlines) }
                }
            }
            Row(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToySlider("Bloom intensity", DisplayFx.bloomStrength, Modifier.weight(1f)) { DisplayFx.setBloomStrength(it) }
                ToySlider("RGB split strength", DisplayFx.chromaticStrength, Modifier.weight(1f)) { DisplayFx.setChromaticStrength(it) }
            }
        }
        ToyGroup("🖱️ Cursor & clicks") {
            Grid(4, 4) { i, m ->
                when (i) {
                    0 -> Toy("🖱️", "Neon trails", DisplayFx.trail, m) { DisplayFx.setTrail(!DisplayFx.trail) }
                    1 -> Toy("✨", "Click sparks", DisplayFx.sparks, m) { DisplayFx.setSparks(!DisplayFx.sparks) }
                    2 -> Toy("💡", "Hover glow", DisplayFx.hover, m) { DisplayFx.setHover(!DisplayFx.hover) }
                    else -> Toy("🔊", "Deck clicks", state.uiSfx, m) { state.setUiSfx(!state.uiSfx) }
                }
            }
        }
        ToyGroup("🖥️ Desktop") {
            Grid(2, 4) { i, m ->
                if (i == 0) Toy("🪟", "Wobbly windows", state.wobble, m) { state.setWobble(!state.wobble) }
                else Toy("🕒", "Desktop clock", state.desktopClock, m) { state.setDesktopClock(!state.desktopClock) }
            }
        }
        ToyGroup("🌴 Vapor toys") {
            val toys = com.reverender.r95.ui.desktop.VaporToys.all
            Grid(toys.size, 4) { i, m ->
                val (t, _) = toys[i]
                Toy(t.second, t.third, state.toys[t.first] == true, m, isNew = true) { state.toggleToy(t.first) }
            }
        }
    }
}

@Composable
private fun ToyGroup(title: String, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().border(1.dp, W95.SoftBorder, RoundedCornerShape(4.dp)).padding(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        T(title, size = 10.sp, bold = true)
        content()
    }
}

/** A toybox toggle tile: emoji, label, ON/OFF chip (and the NEW badge on the vapor toys). */
@Composable
private fun Toy(emoji: String, label: String, on: Boolean, modifier: Modifier, isNew: Boolean = false, onClick: () -> Unit) {
    Box(modifier) {
        Row(
            Modifier.fillMaxWidth().height(40.dp)
                .background(if (on) Brush.verticalGradient(listOf(Color(0xFF6E8CB0), Color(0xFF3F5778))) else Brush.verticalGradient(listOf(W95.Face, W95.Face)))
                .bevel(!on).clickable { UiSfx.play(); onClick() }.padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            T(emoji, size = 14.sp)
            Spacer(Modifier.width(4.dp))
            T(label, size = 11.sp, bold = true, color = if (on) Color.White else W95.Text, maxLines = 2, modifier = Modifier.weight(1f))
            Box(Modifier.background(if (on) Color(0xFF1B2A3A) else Color(0xFF606870), RoundedCornerShape(2.dp)).padding(horizontal = 3.dp, vertical = 1.dp)) {
                T(if (on) "ON" else "OFF", size = 7.sp, bold = true, color = Color.White)
            }
        }
        if (isNew) Box(Modifier.align(Alignment.TopEnd).padding(end = 2.dp).background(Color(0xFFFFD000), RoundedCornerShape(2.dp)).padding(horizontal = 2.dp)) {
            T("NEW", size = 6.sp, bold = true, color = Color.Black)
        }
    }
}

@Composable
private fun ToySlider(label: String, v: Int, modifier: Modifier, onChange: (Int) -> Unit) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            T(label, size = 9.sp, bold = true, modifier = Modifier.weight(1f))
            Box(Modifier.background(Color.White).border(1.dp, W95.Shadow).padding(horizontal = 3.dp)) { T("$v%", size = 8.sp, mono = true) }
        }
        Slider95(v.toFloat(), { onChange(Math.round(it)) }, Modifier.fillMaxWidth().height(26.dp), range = 0f..100f)
    }
}

/** Every animated world (id to name), for the live wallpaper, dock saver and CRT monitor pickers. */
internal val ALL_WORLDS: List<Pair<Int, String>> get() = WORLDS_MULTI + WORLDS_MORE + WORLDS_LIGHT + WORLDS_BACKROOMS
