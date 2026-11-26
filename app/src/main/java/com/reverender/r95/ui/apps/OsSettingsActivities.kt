package com.reverender.r95.ui.apps

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.reverender.r95.audio.SystemVisualizer
import com.reverender.r95.core.Themes
import com.reverender.r95.os.OsPrefs
import com.reverender.r95.os.R95Dream
import com.reverender.r95.os.R95LiveWallpaper
import com.reverender.r95.os.SaverView
import com.reverender.r95.ui.win95.Btn
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import com.reverender.r95.ui.win95.bevel

/**
 * Opened from Android's own "Settings" button on the live-wallpaper preview (and the screen-saver list):
 * a Win95 properties sheet to switch the world / saver and its colour theme, with a live preview.
 */
open class LiveWallpaperSettingsActivity : ComponentActivity() {
    open val dream = false
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        OsPrefs.init(this)
        setContent { OsPropertiesSheet(dream) { finish() } }
    }
}

/** The same sheet for the dock screensaver (Android's screen-saver settings gear). */
class DreamSettingsActivity : LiveWallpaperSettingsActivity() { override val dream = true }

@Composable
private fun OsPropertiesSheet(dream: Boolean, close: () -> Unit) {
    val ctx = LocalContext.current
    val scene = if (dream) OsPrefs.dreamSaver else "wp:${OsPrefs.liveWallpaper}"
    val theme = if (dream) OsPrefs.dreamTheme else OsPrefs.liveTheme
    val askMic = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    Box(Modifier.fillMaxSize().background(Color(0xFF008080)).safeDrawingPadding().padding(8.dp), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 560.dp).fillMaxWidth().background(W95.Face).bevel(true).padding(3.dp)) {
            // title bar
            Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFF000080), Color(0xFF1084D0)))).padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                T(if (dream) "🌙 CRT_SAVER.EXE — Properties" else "🖼 LIVE_WALLPAPER.EXE — Properties", color = Color.White, bold = true, size = 13.sp, modifier = Modifier.weight(1f), maxLines = 1)
                Box(Modifier.background(W95.Face).bevel(true).clickable { close() }.padding(horizontal = 6.dp)) { T("✕", size = 12.sp, bold = true) }
            }
            // monitor preview
            Box(Modifier.fillMaxWidth().padding(10.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.width(230.dp).aspectRatio(1.35f).background(Color(0xFFC0C0C0)).bevel(true).padding(10.dp)) {
                    AndroidView(
                        factory = { SaverView(it, scene, theme).apply { crt = true } },
                        update = { it.mode = scene; it.themeId = theme },
                        modifier = Modifier.fillMaxSize().border(2.dp, Color.Black),
                    )
                }
            }
            T(if (dream) "Screen saver" else "World", size = 12.sp, bold = true, modifier = Modifier.padding(start = 8.dp))
            val opts: List<Pair<String, String>> = if (dream) SaverView.MODES + ALL_WORLDS.map { "wp:${it.first}" to it.second }.filter { w -> SaverView.MODES.none { it.first == w.first } }
                else ALL_WORLDS.map { "wp:${it.first}" to it.second }
            LazyVerticalGrid(GridCells.Adaptive(150.dp), Modifier.fillMaxWidth().height(210.dp).padding(6.dp).background(Color.White).bevel(false).padding(3.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                items(opts, key = { it.first }) { (k, label) ->
                    val sel = k == scene
                    Box(Modifier.fillMaxWidth().background(if (sel) Color(0xFF000080) else Color.Transparent).clickable {
                        if (dream) OsPrefs.setDreamSaver(k) else OsPrefs.setLiveWallpaper(k.removePrefix("wp:").toInt())
                    }.padding(horizontal = 6.dp, vertical = 6.dp)) { T(label, size = 12.sp, color = if (sel) Color.White else Color.Black, maxLines = 1) }
                }
            }
            T("Colour theme", size = 12.sp, bold = true, modifier = Modifier.padding(start = 8.dp, top = 4.dp))
            LazyVerticalGrid(GridCells.Adaptive(104.dp), Modifier.fillMaxWidth().height(118.dp).padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                items(Themes.all, key = { it.id }) { t ->
                    Btn(t.label, { if (dream) OsPrefs.setDreamTheme(t.id) else OsPrefs.setLiveTheme(t.id) }, Modifier.fillMaxWidth().height(30.dp), pressed = t.id == theme, size = 11.sp)
                }
            }
            val mic = remember { mutableStateOf(SystemVisualizer.permitted()) }
            if (!mic.value) Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                T("To react to Spotify, YouTube & co. Android needs the microphone permission for its audio visualizer.", size = 10.sp, color = W95.Shadow, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(6.dp))
                Btn("Allow", { askMic.launch(Manifest.permission.RECORD_AUDIO); mic.value = true }, size = 11.sp)
            }
            Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End)) {
                if (dream) Btn("Screen saver settings…", { R95Dream.openSettings(ctx) }, size = 12.sp)
                else Btn("Set as wallpaper…", { R95LiveWallpaper.open(ctx) }, size = 12.sp)
                Btn("OK", close, Modifier.width(80.dp), size = 12.sp)
            }
        }
    }
}
