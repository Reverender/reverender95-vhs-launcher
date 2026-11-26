package com.reverender.r95

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.reverender.r95.audio.PlayerConnection
import com.reverender.r95.ui.desktop.DesktopScreen
import com.reverender.r95.ui.desktop.WindowManager
import com.reverender.r95.ui.viz.argb
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import com.reverender.r95.ui.win95.bevel
import kotlinx.coroutines.delay

class MainActivity : androidx.fragment.app.FragmentActivity() {
    private lateinit var player: PlayerConnection
    private lateinit var appState: AppState
    private val wm = WindowManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        runCatching { registerReceiver(screenOff, android.content.IntentFilter(android.content.Intent.ACTION_SCREEN_OFF).apply { addAction(android.content.Intent.ACTION_USER_PRESENT) }); screenOffOn = true }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // Bundled emoji font: icons render on every Android version, offline.
        if (androidx.emoji2.text.EmojiCompat.isConfigured().not()) {
            androidx.emoji2.text.EmojiCompat.init(androidx.emoji2.bundled.BundledEmojiCompatConfig(applicationContext).setReplaceAll(true))
        }
        com.reverender.r95.core.Perf.install(this)
        player = PlayerConnection(applicationContext)
        appState = AppState(applicationContext, player)
        // the VHS-only APK: just the VCR launcher. BuildConfig.VHS_ONLY is a constant, so in that APK R8 drops
        // the desktop branch below and with it everything only the desktop uses.
        if (BuildConfig.VHS_ONLY) { startVhsOnly(savedInstanceState); return }
        com.reverender.r95.ui.desktop.SiteRects.load(this)
        if (savedInstanceState == null && intent?.getIntExtra("r95_wallpaper", -99) == -99) appState.startupPicks()
        com.reverender.r95.ui.viz.site.SiteHost.install(applicationContext, player)
        com.reverender.r95.audio.AmpEngine.install(player)
        com.reverender.r95.audio.SiteStudio.install(appState)
        com.reverender.r95.core.DisplayFx.install(applicationContext, appState)
        com.reverender.r95.net.RevyBrain.install(applicationContext, appState, wm)
        player.connect()
        com.reverender.r95.os.OsBoot.init(this)
        com.reverender.r95.os.CrtMonitor.attach(this)
        askForNotifications()

        setContent {
            val baseDensity = androidx.compose.ui.platform.LocalDensity.current
            CompositionLocalProvider(
                LocalAppState provides appState,
                // a little larger text than the website (Settings → Display → Text size)
                androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(baseDensity.density, baseDensity.fontScale * com.reverender.r95.os.OsPrefs.textScale),
            ) {
                // Test hook used by the GitHub emulator smoke test: open apps straight away.
                val autoOpen = intent?.getStringExtra("r95_open")
                val skipBoot = intent?.getBooleanExtra("r95_skip_boot", false) == true
                // test hook: --es r95_style vhs|win95|longhorn
                val testStyle = intent?.getStringExtra("r95_style")
                LaunchedEffect(testStyle) { if (testStyle != null) appState.setOsStyle(testStyle) }
                val testWall = intent?.getIntExtra("r95_wallpaper", -99) ?: -99
                LaunchedEffect(testWall) { if (testWall != -99) appState.setWallpaper(testWall) }
                var phase by remember { mutableStateOf(if (savedInstanceState == null && !skipBoot) Phase.BOOT else Phase.DESKTOP) }
                var booting by remember { mutableStateOf(savedInstanceState == null && !skipBoot) }
                LaunchedEffect(phase, autoOpen) {
                    if (phase == Phase.DESKTOP && wm.windows.isEmpty()) {
                        kotlinx.coroutines.delay(400)
                        // Like the website: VAPOR_AMP is open when the desktop appears.
                        val ids = if (autoOpen.isNullOrBlank()) listOf("player") else autoOpen.split(',').map { it.trim() }.filter { it.isNotEmpty() }
                        ids.filter { it != "none" }.forEach { wm.open(it) }
                    }
                }
                BackHandler(enabled = phase == Phase.DESKTOP) {
                    // as the phone's home screen there is nothing "behind" the desktop to go back to
                    if (!wm.handleBack() && !com.reverender.r95.os.Launcher.isHomeIntent(intent)) moveTaskToBack(true)
                }
                when (phase) {
                    // #rr-boot over the desktop (which starts behind it, like the site), then the CRT power-on
                    // VHS: the light-weight VCR-menu shell instead of the desktop (no boot animation either)
                    Phase.BOOT, Phase.DESKTOP -> if (appState.osStyle == "vhs") {
                        if (phase == Phase.BOOT) LaunchedEffect(Unit) { phase = Phase.DESKTOP }
                        com.reverender.r95.ui.vhs.VhsShell(onShutDown = { phase = Phase.SHUTDOWN })
                    } else androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {
                        if (phase == Phase.BOOT) LaunchedEffect(Unit) { phase = Phase.DESKTOP }
                        DesktopScreen(wm, onShutDown = { phase = Phase.SHUTDOWN })
                        if (booting) com.reverender.r95.ui.desktop.BootOverlay { booting = false }
                    }
                    Phase.SHUTDOWN -> ShutdownScreen(
                        onTap = { phase = Phase.DESKTOP },
                        onDone = {
                            player.stop()
                            finishAndRemoveTask()
                        },
                    )
                }
            }
        }
    }

    /** VHS-only APK: the music engine and the VHS shell, nothing of the desktop. */
    private fun startVhsOnly(savedInstanceState: Bundle?) {
        // test hook: --es r95_vhs_rom name.nes (a file in the app's external files folder) opens it in the game deck
        val testDir = getExternalFilesDir(null)
        intent?.getStringExtra("r95_vhs_rom")?.let { n -> testDir?.let { com.reverender.r95.ui.vhs.VhsTestHooks.rom = java.io.File(it, n) } }
        com.reverender.r95.ui.viz.site.SiteHost.install(applicationContext, player)
        com.reverender.r95.audio.AmpEngine.install(player)
        // test hook: --ei r95_vhs_radio N tunes radio preset N a moment after start
        intent?.getIntExtra("r95_vhs_radio", -1)?.takeIf { it >= 0 }?.let { n ->
            android.os.Handler(mainLooper).postDelayed({ android.util.Log.i("r95", "radio test: preset $n"); com.reverender.r95.audio.AmpEngine.playInternetRadio(n) }, 4000)
        }
        com.reverender.r95.audio.SiteStudio.install(appState)
        player.connect()
        com.reverender.r95.os.OsPrefs.init(this)
        // test hook: --ef r95_vhs_uiblur 0.6 sets the UI motion blur
        intent?.getFloatExtra("r95_vhs_uiblur", -1f)?.takeIf { it >= 0f }?.let { com.reverender.r95.os.OsPrefs.setVhsUiBlur(it) }
        // test hooks: --ei r95_vhs_wall N / --ei r95_vhs_viz N pick the 3D wallpaper / visualizer look
        intent?.getIntExtra("r95_vhs_wall", -2)?.takeIf { it >= -1 }?.let { com.reverender.r95.os.OsPrefs.setVhsWall(it) }
        intent?.getIntExtra("r95_vhs_viz", -1)?.takeIf { it >= 0 }?.let { com.reverender.r95.os.OsPrefs.setVhsVizTheme(it) }
        intent?.getIntExtra("r95_vhs_font", -1)?.takeIf { it >= 0 }?.let { com.reverender.r95.os.OsPrefs.setVhsFont(it) }
        intent?.getIntExtra("r95_vhs_curve", -1)?.takeIf { it >= 0 }?.let { com.reverender.r95.os.OsPrefs.setVhsCurve(it) }
        // --es r95_vhs_titles "LINE1~LINE2" (_ for a space): the two home-screen title lines
        intent?.getStringExtra("r95_vhs_titles")?.let { t -> val parts = (t.replace('_', ' ') + "~").split("~"); com.reverender.r95.os.OsPrefs.setVhsTitles(parts[0], parts[1]) }
        // --ei r95_vhs_grad N (-1 off) / --ez r95_vhs_mix true: multicolour background / pastel text
        intent?.getIntExtra("r95_vhs_grad", -2)?.takeIf { it >= -1 }?.let { g ->
            com.reverender.r95.os.OsPrefs.setVhsGrad(g)
            if (g >= 0) com.reverender.r95.os.OsPrefs.setVhsColors(com.reverender.r95.ui.vhs.VHS_GRADS[g].second.first(), com.reverender.r95.os.OsPrefs.vhsFg)
        }
        if (intent?.hasExtra("r95_vhs_mix") == true) com.reverender.r95.os.OsPrefs.setVhsFgMix(intent!!.getBooleanExtra("r95_vhs_mix", false))
        com.reverender.r95.os.GyroWarp.refresh()
        askForNotifications()
        setContent {
            val baseDensity = androidx.compose.ui.platform.LocalDensity.current
            CompositionLocalProvider(
                LocalAppState provides appState,
                androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(baseDensity.density, baseDensity.fontScale * com.reverender.r95.os.OsPrefs.textScale),
            ) {
                var off by remember { mutableStateOf(false) }
                if (off) ShutdownScreen(onTap = { off = false }, onDone = { player.stop(); finishAndRemoveTask() })
                else com.reverender.r95.ui.vhs.VhsShell(onShutDown = { off = true })
            }
        }
    }

    /** Test hook: open more windows in the running session (am start --es r95_open id,id). */
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        if (BuildConfig.VHS_ONLY) return
        intent.getStringExtra("r95_emu_search")?.let { q -> com.reverender.r95.core.RevyRequests.emuAutoPick = true; com.reverender.r95.core.RevyRequests.emuQuery = q; runCatching { wm.open("emulator") } }
        intent.getStringExtra("r95_open")?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() && it != "none" }?.forEach { runCatching { wm.open(it) } }
    }

    override fun onTrimMemory(level: Int) { super.onTrimMemory(level); com.reverender.r95.core.Perf.onTrim(level) }

    override fun onStart() { super.onStart(); android.util.Log.i("r95", "life: start"); com.reverender.r95.core.AppVisibility.set(true) }
    private var lastStopMs = 0L
    // the screen went off while (or just after) the VHS launcher showed: play the CRT switch-on when it comes back
    private val screenOff = object : android.content.BroadcastReceiver() {
        override fun onReceive(c: android.content.Context?, i: android.content.Intent?) {
            if (i?.action == android.content.Intent.ACTION_USER_PRESENT) { com.reverender.r95.ui.vhs.VhsPowerOn.present = true; return }
            if (com.reverender.r95.core.AppVisibility.desktopActive) return
            if (com.reverender.r95.core.AppVisibility.visible || android.os.SystemClock.uptimeMillis() - lastStopMs < 2500) {
                com.reverender.r95.ui.vhs.VhsPowerOn.present = false
                com.reverender.r95.ui.vhs.VhsPowerOn.pending = true
            }
        }
    }
    private var screenOffOn = false
    override fun onStop() {
        lastStopMs = android.os.SystemClock.uptimeMillis(); android.util.Log.i("r95", "life: stop")
        if (!com.reverender.r95.core.AppVisibility.desktopActive && !(getSystemService(POWER_SERVICE) as android.os.PowerManager).isInteractive && !com.reverender.r95.ui.vhs.VhsPowerOn.pending) {
            com.reverender.r95.ui.vhs.VhsPowerOn.present = false
            com.reverender.r95.ui.vhs.VhsPowerOn.pending = true
        }
        com.reverender.r95.core.AppVisibility.set(false); super.onStop()
    }

    override fun onDestroy() {
        if (screenOffOn) { runCatching { unregisterReceiver(screenOff) }; screenOffOn = false }
        if (!BuildConfig.VHS_ONLY) com.reverender.r95.os.CrtMonitor.detach(this)
        player.release()
        super.onDestroy()
    }

    private fun askForNotifications() {
        // Android 13+: the media notification (lock-screen controls) needs this permission.
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            val prefs = getSharedPreferences("r95", MODE_PRIVATE)
            if (!prefs.getBoolean("asked_notif", false)) {
                prefs.edit().putBoolean("asked_notif", true).apply()
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    private enum class Phase { BOOT, DESKTOP, SHUTDOWN }
}

@Composable
private fun ShutdownScreen(onTap: () -> Unit, onDone: () -> Unit) {
    LaunchedEffect(Unit) { delay(2200); onDone() }
    Box(
        Modifier.fillMaxSize().background(Color.Black).clickable { onTap() },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            T(
                "It's now safe to turn off\nyour computer.",
                color = Color(0xFFFF9A00), bold = true, size = 22.sp, align = TextAlign.Center,
            )
            Spacer(Modifier.height(14.dp))
            T("(tap to cancel)", color = W95.Shadow, size = 11.sp)
        }
    }
}
