package com.reverender.r95.ui.apps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reverender.r95.core.AppGroup
import com.reverender.r95.core.AppInfo
import com.reverender.r95.core.Apps
import com.reverender.r95.ui.desktop.WindowManager
import com.reverender.r95.ui.win95.Field
import com.reverender.r95.ui.win95.Skin
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import com.reverender.r95.ui.win95.bevel

/** How to use each window, written for the Help and Support Center. */
private val HELP: Map<String, List<String>> = mapOf(
    "player" to listOf(
        "Press PLAY for the built-in deck; RND SONG jumps to a random track and JUMP POS to a random spot in it.",
        "Drag the TRACK POSITION SCRUBBER to seek and the VOL slider on the left to change the volume.",
        "The top menu changes the wallpaper and colour theme, toggles the CRT filter and the bass glitch.",
        "Radio & input: device files, Internet Archive MP3s, YouTube Music, internet radio and the worldwide station directory.",
        "CAST sends the current track to a Chromecast or TV on your network.",
        "💾 C:\\MUSIC opens every song on this phone as an Explorer tree — no file picker each time.",
        "Music keeps playing with the screen locked (EQ, effects and radio included); swiping the app away stops it.",
        "When another app plays (Spotify, YouTube Music…), the buttons switch to REMOTE: PREV / PLAY / NEXT control that app. With notification access (full APK) the LCD also shows its song and the scrubber seeks. 📼 BACK TO DECK returns to VAPOR_AMP's own music.",
    ),
    "music" to listOf(
        "Allow access to music once; every MP3, FLAC, OGG, M4A and WAV on the phone is listed by folder, like Explorer.",
        "Tap a folder to open it and ⬆ to go up. Tap a song to select it, tap it again to play.",
        "▶ Play folder loads the whole folder into VAPOR_AMP's playlist; ⟳ re-scans after you add music.",
    ),
    "monitor" to listOf(
        "Plug the phone into a TV or monitor (USB-C to HDMI). The chosen world or visualizer fills the TV with CRT glass.",
        "Pick what the TV shows: ANALOG_VU, LISSAJOUS, the WIN95 maze or any wallpaper world such as Hyper-Atrium or Cyber City 99.",
        "The phone stays the controller: open DSP_FX_95 or GRAPHIC_EQ here and play with the sound.",
        "Wireless displays work when Android offers them as a second screen; a Chromecast that mirrors shows the phone screen instead.",
    ),
    "plasma" to listOf(
        "A music visualizer: it reacts to whatever is playing in VAPOR_AMP 95, radio and video included.",
        "Tilt the phone to steer the gyro core (Settings → Phone: sound & motion → PLASMA_EQ gyro core).",
        "Tap FX in the title bar for bloom, colour split and other screen effects for this window.",
    ),
    "browser" to listOf(
        "Type a website or search words in the address bar and press GO; the home page is DuckDuckGo Lite.",
        "◄ ► go back and forward, ↻ reloads, 🏠 goes home and ＋ NEW opens another browser window.",
        "Pages are shown as clean text; ↗ OPEN EXTERNAL opens the page in your phone's own browser.",
    ),
    "video" to listOf(
        "Search YouTube or the Internet Archive and press ▶ OPEN / ▶ PLAY to stream a video here.",
        "OPEN FILE plays a video stored on this device.",
        "Subtitles: search by title and language, load a file, shift them with −0.5s / +0.5s and pick a size.",
        "While a video plays the searches fold away; tap 🔎 SEARCH to show them again. CAST sends internet videos to a TV.",
    ),
    "vectordreams" to listOf(
        "Tap the polygon core to harvest VECTRA; quick taps build a CORE CHAIN and every 10th tap is a critical ×3.",
        "Open BUILD to buy structures that produce VECTRA every second; MAX INVEST buys as many as you can afford.",
        "UPGRADES, RESEARCH, XP / LEVEL and PRESTIGE unlock as the city grows. OVERDRIVE gives ×2.5 for 20 seconds.",
        "Catch the flying data comets for bonus VECTRA, a pulse storm or a production surge.",
    ),
    "emulator" to listOf(
        "LOAD ROM opens a game file (.nes, .smc/.sfc, .gb, .gbc, .gba, .n64/.z64/.v64); the system is detected automatically.",
        "Search Archive finds games on the Internet Archive; add GB, GBA, NES, SNES or N64 to the search to filter.",
        "The TOUCH PAD window shows the original controller for the running console; several fingers work at once.",
        "On N64 games the left half of the game screen is the analog stick. Load only games you are entitled to play.",
    ),
    "vaporpet" to listOf(
        "Keep the four meters up: Feed (meal or snack), Play the left/right guessing game, Clean up messes and give Medicine when sick.",
        "Turn the Light off to let the cat sleep. Tap the little screen to give it a cuddle.",
        "The cat also lives on the desktop: drag and throw it, tap it to open this app, hold it to clean, tap the bowl to refill it.",
    ),
    "sticky" to listOf("Add colourful notes that stay on this device.", "Pick a colour and type; notes save automatically.", "📌 Pin keeps a note in the phone's notification shade; ↗ Share and Copy send it anywhere."),
    "notepad" to listOf("Write text, then Download .txt or Open… a text file.", "Save writes straight back to the file you opened (or asks for a name once).", "Find shows Find / Replace with the number of matches; Date inserts today's date and time; ↗ Share sends the text.", "Wrap and A− / A+ change the layout and text size. New asks once before clearing."),
    "calc" to listOf("A classic calculator with memory keys.", "SCI adds √, x², xʸ, 1/x, π, sin, cos, tan (degrees), log and ln.", "TAPE shows the last 30 results — tap one to reuse it; COPY puts the display on the clipboard."),
    "planner" to listOf("Pick a day in the calendar and write a note for it.", "Days with notes are marked.", "⏰ Remind me: type a time and the phone shows your plans then, even with the app closed.", "📅 Calendar copies the plan into your phone's calendar app."),
    "todo" to listOf("Type a task and add it; tick it off when done.", "Tap ☆ to star a task: starred tasks stay on top. Tap a task's text to edit it.", "↗ Share sends the list; Clear done tidies it."),
    "focus" to listOf("Start a focus session, then take a break when the timer rings.", "Pause, Resume or Reset at any time.", "The countdown also runs in the notification shade, and the alarm rings (with a buzz) even if you leave the app."),
    "drum909" to listOf("Paint steps on the 16-step grid for each drum sound and press PLAY.", "Change the tempo, swing and each drum's level and tune; save patterns A–D."),
    "synth95" to listOf("Slide your fingers across the keys or the XY pad; several notes play at once.", "Change the waveform, filter, envelope and arpeggiator; shift octaves with OCT."),
    "cassette" to listOf(
        "Play, pause, stop and eject the tape deck that shapes the master sound.", "Pick a tape type and drag the reels to scratch.",
        "The keys clunk like a real deck and REW / FF hum while held (Haptic transport).",
        "Gyro tape warp: tilt or turn the phone to bend the pitch, shake it for flutter — also with the screen off while music plays.",
    ),
    "fx" to listOf(
        "Shape the sound with reverb, echo, speed and modulation effects.", "Choose a preset or move the sliders; changes apply to everything that plays.",
        "🔌 EXTERNAL APPS IN captures what Spotify, YouTube and other apps play (Android 10+) for the visualizers; tick \"Play it through this rack\" to hear it with this EQ, effects and tape. The original app keeps playing too, so turn it down. Apps that block capture stay silent.",
        "🎹 MIDI: plug in a USB or Bluetooth MIDI controller. Pick a slider, press LEARN and turn a knob; keys play VAPOR_SYNTH_95 and pitch bend rides TAPE SPEED.",
    ),
    "eq" to listOf(
        "Raise or lower each frequency band and the preamp.", "Presets give quick sound curves; FLAT resets them.",
        "\"Apply EQ + Sub Excite to all phone audio\" puts these bands on Android's own output (Android 9+), so Spotify, YouTube and games sound the same way. Some phones lock the global output; apps that announce their audio session are still covered.",
    ),
    "settings" to listOf(
        "Window opacity, the desktop clock, the CRT scanline filter and sounds.", "Screensaver, taskbar position and auto-hide, and full visual quality for slower devices.",
        "Updates: shows your version; ⬇ Download latest version opens the download in your browser, then open the file to update.",
        "Display: Text size makes all text a little larger or smaller. Performance: Wallpaper resolution (Auto aims for a steady 60 fps).",
        "Desktop gestures: pick what a sideways swipe on the empty desktop opens. The taskbar hides after 1–30 s unless \"Always show taskbar\" is on.",
        "Phone groups: home launcher mode, Win95 notification dialogs (full APK), the dock screensaver, the live wallpaper, haptics, gyro warp and \"react to all phone audio\".",
    ),
    "customize" to listOf("Pick the desktop style (Windows 95, Longhorn or VHS) and its window colours.", "Choose a wallpaper or background colour and turn depth effects and desktop toys on or off.", "VHS turns the phone into a 1980s VCR menu: a light-weight screen without windows or wallpapers, ideal as a launcher. Tap the letters to search apps (the key you press lights up; with nothing typed you get the radio line and spectrum bars), swipe up for the phone keyboard (its enter key starts the top match; it also types in the video and subtitle searches), hold ⏏LOAD until the tape is in to open the VIDEO DECK (search YouTube or the Internet Archive, or open a file, and play it here with VCR controls, SUBS for subtitles and COLOR / SOFT / FRINGE / SNOW picture effects with RESET FX), swipe down for the notification shade, swipe ◀ or ▶ for your app shortcuts, play music with the built-in VAPOR_AMP (hold ▶ to play or pause; with the music stopped, ◀◀ ▶▶ and the station keys only pick and PLAY starts it; SP EP SLP SLO FF set the tape speed; PLAYLIST for your own songs from the phone with SHUF and RPT, DECK or a radio station from the line under SEARCH; ◀◀ ▶▶ jump to a random deck song and spot, or the previous / next station; TRACKING is clean in the middle and drifts the picture and sound either way), and tap the bar at the bottom for SETUP (colours, CRT with GLITCH and CHROMA sliders, bloom, clock, swipes, pinned apps, radio tuner, back to Win95/Longhorn)."),
    "revy" to listOf("Ask Revy anything: it searches the web and sums up the answer.", "Revy can also open apps, change the wallpaper or theme and play music for you. Tap 🎤 to talk."),
    "clock" to listOf("Pick a face for the desktop clock.", "Tap the desktop clock any time to come back here."),
)
private val VIZ_HELP = listOf(
    "A music visualizer: it reacts to whatever is playing in VAPOR_AMP 95, radio and video included.",
    "Tap FX in the title bar for bloom, colour split and other screen effects for this window.",
    "Open several visualizers at once, or use Visualizers in the Start menu (hold an item to preview it).",
)

private val GROUP_ICON = mapOf(AppGroup.PROGRAMS to "📁", AppGroup.GAMES to "🎮", AppGroup.OFFICE to "🏬", AppGroup.STUDIO to "🎛️", AppGroup.VISUALS to "👁️", AppGroup.SYSTEM to "⚙️")
private val GROUP_TITLE = mapOf(
    AppGroup.PROGRAMS to "Music, video and the web", AppGroup.GAMES to "Games", AppGroup.OFFICE to "Office Plaza",
    AppGroup.STUDIO to "Audio & Studio", AppGroup.VISUALS to "Visualizers", AppGroup.SYSTEM to "Your desktop",
)

private class HelpColors(val head: Brush, val headText: Color, val body: Brush, val title: Color, val text: Color, val link: Color, val bar: Color, val modern: Boolean)
private fun helpColors(): HelpColors =
    if (Skin.classic) HelpColors(
        Brush.horizontalGradient(listOf(Skin.vars.title1, Skin.vars.title2)), Color.White,
        Brush.verticalGradient(listOf(W95.Face, W95.Face)), Skin.vars.title1, W95.Text, Skin.vars.title1, W95.Face, false,
    ) else HelpColors(
        Brush.verticalGradient(listOf(Color(0xFF2C3A47), Color(0xFF11191D))), Color(0xFFE7F4F8),
        Brush.verticalGradient(listOf(Skin.accent.d.copy(alpha = 0.92f), Color(0xFF151C22))), Skin.accent.l, Color(0xFFE6EEF5), Color(0xFFFFFFFF), Color(0xFFEEF1F2), true,
    )

/** A little picture of the app: its window in miniature. */
@Composable
private fun AppPicture(app: AppInfo, pic: Int, c: HelpColors) {
    Box(Modifier.size(pic.dp, (pic * 0.8f).dp).clip(RoundedCornerShape(if (c.modern) 4.dp else 0.dp)).background(Color(0xFF0D0221)).then(if (c.modern) Modifier.border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(4.dp)) else Modifier.bevel(true))) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxWidth().height((pic * 0.16f).dp).background(c.head))
            Box(Modifier.fillMaxWidth().weight(1f).background(Brush.verticalGradient(listOf(Color(0xFF1B0B3A), Color(0xFF3A1060), Color(0xFF0D0221)))), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    val y0 = size.height * 0.62f
                    for (i in 0 until 4) drawLine(Color(0x66FF00A0), Offset(0f, y0 + i * size.height * 0.1f), Offset(size.width, y0 + i * size.height * 0.1f), 1f)
                }
                T(app.icon, size = (pic * 0.32f).sp)
            }
        }
    }
}

/** Help and Support Center, in the look of the current desktop style. */
@Composable
fun HelpApp(wm: WindowManager?) {
    val c = helpColors()
    val history = remember { mutableStateListOf<String>("home") }
    var index by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    val page = history[index]
    fun go(p: String) { while (history.size > index + 1) history.removeAt(history.size - 1); history.add(p); index = history.size - 1 }
    val apps = Apps.all.filter { it.id != "emutouch" && it.id != "help" }

    Column(Modifier.fillMaxSize().background(c.bar)) {
        // toolbar: Back, Forward, Home | Index
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ToolBtn("◀", "Back", index > 0) { index-- }
            ToolBtn("▶", "", index < history.size - 1) { index++ }
            ToolBtn("🏠", "Home", true) { go("home") }
            ToolBtn("📖", "Index", true) { go("index") }
            ToolBtn("✦", "Ask Revy", true) { wm?.open("revy") }
        }
        // header: Search + Help and Support Center
        Row(Modifier.fillMaxWidth().background(c.head).padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            T("Search", color = c.headText, bold = true, size = 14.sp)
            Spacer(Modifier.width(8.dp))
            Field(query, { query = it; if (it.isNotBlank() && page != "search") go("search") }, Modifier.width(150.dp), onDone = { if (query.isNotBlank()) go("search") })
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(20.dp).background(Color.White.copy(alpha = 0.18f), CircleShape).border(1.dp, Color.White, CircleShape), contentAlignment = Alignment.Center) { T("?", color = Color.White, bold = true, size = 12.sp) }
                    Spacer(Modifier.width(5.dp))
                    T("Help and Support Center", color = c.headText, bold = true, size = 14.sp, maxLines = 1)
                }
                T("REVERENDER 95 · " + (if (c.modern) "Longhorn" else "Windows 95") + " Edition", color = c.headText.copy(alpha = 0.8f), size = 9.sp)
            }
        }
        Box(Modifier.fillMaxWidth().weight(1f).background(c.body)) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(10.dp)) {
                when {
                    page == "home" -> HomePage(apps, c, wm, ::go)
                    page == "index" -> {
                        Heading("Index", c)
                        apps.sortedBy { it.title.lowercase() }.forEach { a -> Link("${a.icon}  ${a.title}", c) { go("app:" + a.id) } }
                    }
                    page == "search" -> {
                        Heading("Search results", c)
                        val q = query.trim().lowercase()
                        val hits = apps.filter { a -> q.isEmpty() || a.title.lowercase().contains(q) || a.desc.lowercase().contains(q) || (HELP[a.id] ?: VIZ_HELP).any { it.lowercase().contains(q) } }
                        if (hits.isEmpty()) T("No topics found for \"$query\".", color = c.text, size = 12.sp)
                        hits.forEach { a -> TopicRow(a, c) { go("app:" + a.id) } }
                    }
                    page.startsWith("group:") -> {
                        val g = AppGroup.valueOf(page.removePrefix("group:"))
                        Heading(GROUP_TITLE[g] ?: g.label, c)
                        apps.filter { it.group == g }.forEach { a -> TopicRow(a, c) { go("app:" + a.id) } }
                    }
                    page == "phone" -> PhonePage(c, wm)
                    page.startsWith("app:") -> AppPage(apps.firstOrNull { it.id == page.removePrefix("app:") } ?: apps.first(), c, wm)
                }
            }
        }
    }
}

@Composable
private fun ToolBtn(icon: String, label: String, enabled: Boolean, onClick: () -> Unit) {
    Row(Modifier.clickable(enabled = enabled, onClick = onClick).padding(horizontal = 3.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        T(icon, size = 14.sp, color = if (enabled) W95.Text else W95.Shadow)
        if (label.isNotEmpty()) { Spacer(Modifier.width(3.dp)); T(label, size = 11.sp, color = if (enabled) W95.Text else W95.Shadow) }
    }
}

@Composable
private fun Heading(t: String, c: HelpColors) = T(t, color = c.title, bold = true, size = 19.sp, modifier = Modifier.padding(top = 2.dp, bottom = 8.dp))

@Composable
private fun Link(t: String, c: HelpColors, onClick: () -> Unit) {
    Row(Modifier.clickable(onClick = onClick).padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        T("•", color = c.text, size = 11.sp); Spacer(Modifier.width(6.dp)); T(t, color = c.link, bold = true, size = 12.sp)
    }
}

@Composable
private fun Task(t: String, c: HelpColors, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(16.dp).background(Brush.verticalGradient(listOf(Color(0xFF7BD86A), Color(0xFF2C8A2A)))).border(1.dp, Color.White), contentAlignment = Alignment.Center) { T("➜", color = Color.White, bold = true, size = 10.sp) }
        Spacer(Modifier.width(7.dp)); T(t, color = c.text, size = 12.sp)
    }
}

@Composable
private fun TopicRow(a: AppInfo, c: HelpColors, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        AppPicture(a, 44, c); Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) { T(a.title, color = c.link, bold = true, size = 12.sp); T(a.desc.substringAfter("— ").replaceFirstChar { it.uppercase() }, color = c.text, size = 11.sp) }
    }
}

@Composable
private fun HomePage(apps: List<AppInfo>, c: HelpColors, wm: WindowManager?, go: (String) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val wide = maxWidth > 520.dp
        val left: @Composable () -> Unit = {
            Column {
                Heading("Pick a Help topic", c)
                listOf(AppGroup.PROGRAMS, AppGroup.GAMES, AppGroup.OFFICE, AppGroup.STUDIO, AppGroup.VISUALS, AppGroup.SYSTEM).forEach { g ->
                    val list = apps.filter { it.group == g }
                    if (list.isEmpty()) return@forEach
                    Row(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Box(Modifier.width(56.dp)) { AppPicture(list.first(), 48, c) }
                        Column(Modifier.weight(1f)) {
                            Row(Modifier.clickable { go("group:" + g.name) }.padding(bottom = 2.dp)) { T("${GROUP_ICON[g]} ${GROUP_TITLE[g]}", color = c.title, bold = true, size = 13.sp) }
                            (if (g == AppGroup.VISUALS) list.take(4) else list).forEach { a -> Link(a.title, c) { go("app:" + a.id) } }
                            if (g == AppGroup.VISUALS && list.size > 4) Link("All ${list.size} visualizers…", c) { go("group:" + g.name) }
                        }
                    }
                }
            }
        }
        val right: @Composable () -> Unit = {
            Column {
                Heading("Ask for assistance", c)
                Task("Ask Revy, the desktop companion, any question", c) { wm?.open("revy") }
                Task("Browse every window in the Index", c) { go("index") }
                Spacer(Modifier.height(10.dp))
                Heading("Pick a task", c)
                Task("Change the wallpaper and colours with Customize", c) { wm?.open("customize") }
                Task("Pick a face for the desktop clock", c) { wm?.open("clock") }
                Task("Adjust the desktop in Settings", c) { wm?.open("settings") }
                if (com.reverender.r95.BuildConfig.FULL) Task("Get the newest version with ⬇ Update in the Start menu", c) { go("app:settings") }
                Task("Arrange the windows again with Player Only Reset", c) { wm?.playerOnlyReset() }
                Spacer(Modifier.height(10.dp))
                Heading("📱 Your phone", c)
                Task("Everything REVERENDER_95 can do on Android", c) { go("phone") }
                Spacer(Modifier.height(10.dp))
                Heading("Did you know?", c)
                listOf(
                    "Tap a Start menu item again to close that app; tap beside the menu to close it.",
                    "Swipe up on an empty part of the desktop for the Start menu, swipe down to search every app, sideways for your own shortcuts.",
                    "Long-press a phone app for Open full screen, 📌 Pin to taskbar, App info and Uninstall.",
                    "Tap the taskbar clock to open the phone's clock app.",
                    "Hold a visualizer in the Start menu to see a live preview.",
                    "Double-tap a title bar to maximise a window; drag the corner to resize it.",
                    "Your cat lives on the desktop — try throwing it onto a window.",
                ).forEach { Row(Modifier.padding(vertical = 2.dp)) { T("•", color = c.text, size = 11.sp); Spacer(Modifier.width(6.dp)); T(it, color = c.text, size = 11.sp) } }
            }
        }
        if (wide) Row { Box(Modifier.weight(1f)) { left() }; Spacer(Modifier.width(14.dp)); Box(Modifier.weight(1f)) { right() } }
        else Column { left(); Spacer(Modifier.height(6.dp)); right() }
    }
}

@Composable
private fun AppPage(a: AppInfo, c: HelpColors, wm: WindowManager?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AppPicture(a, 72, c); Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            T(a.title, color = c.title, bold = true, size = 18.sp)
            T(a.desc.substringAfter("— ").replaceFirstChar { it.uppercase() }, color = c.text, size = 12.sp)
        }
    }
    if (a.group == AppGroup.VISUALS) {
        Spacer(Modifier.height(10.dp))
        SiteCanvasPreview(a.id, Modifier.fillMaxWidth().height(150.dp))
    }
    Spacer(Modifier.height(12.dp))
    T("How to use it", color = c.title, bold = true, size = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
    (HELP[a.id] ?: VIZ_HELP).forEachIndexed { i, s ->
        Row(Modifier.padding(vertical = 3.dp)) { T("${i + 1}.", color = c.title, bold = true, size = 12.sp, modifier = Modifier.width(20.dp)); T(s, color = c.text, size = 12.sp) }
    }
    Spacer(Modifier.height(12.dp))
    Task("Open ${a.title}", c) { wm?.open(a.id) }
    Task("Find it in the Start menu under ${a.group.label}", c) { wm?.startOpen = true }
}


/** The Android integrations, what they do and where to switch them on. */
private val PHONE_HELP = listOf(
    Triple("📼 Background playback", "Music, the 18-unit DSP chain and the radio keep playing with the screen locked, controlled from the lock screen. Swiping the app away stops everything.", "player"),
    Triple("🔌 Process other apps", "DSP_FX_95 → EXTERNAL APPS IN captures Spotify, YouTube & co. for the visualizers and can play them through the rack (Android 10+).", "fx"),
    Triple("🎚 System equalizer", "GRAPHIC_EQ → \"Apply to all phone audio\": the 8 bands and Sub Excite on Android's own output (Android 9+).", "eq"),
    Triple("🎹 USB / Bluetooth MIDI", "DSP_FX_95 → MIDI CONTROLLER: knobs move the sliders (LEARN), keys play VAPOR_SYNTH_95 on a low-latency path.", "fx"),
    Triple("🏠 Home launcher", "Settings → Phone: home screen. Use the desktop as your phone's home screen; 🌴 Start → Phone Apps (or swipe down to search) opens your installed apps, in floating windows where the phone supports them.", "settings"),
    Triple("🌙 Dock screensaver", "Settings → Phone: dock screensaver. ANALOG_VU, LISSAJOUS, the WIN95 maze or a world, in any colour theme, while charging on a stand.", "settings"),
    Triple("📳 Tape haptics", "CASSETTE_95 keys clunk, REW / FF hum, and (optional) the bass-drum glitch thumps.", "cassette"),
    Triple("🌀 Gyro tape warp", "Tilt or shake the phone to bend the tape's pitch; PLASMA_EQ's core follows the tilt. Works with the screen off while music plays.", "cassette"),
    Triple("🧩 Home screen widgets", "Long-press your home screen → Widgets → REVERENDER_95: CASSETTE_95, VAPOR_AMP 95, the DSP_FX_95 preset matrix and live ANALOG_VU meters.", null),
    Triple("💬 Retro notifications", "Settings → Phone: retro notifications. Texts, Discord and alerts appear as Win95 / Longhorn dialogs with a pitched-down chime.", "settings"),
    Triple("💾 C:\\MUSIC", "Every song on the phone, by folder, without picking files each session.", "music"),
    Triple("📺 CRT monitor", "A TV over USB-C/HDMI shows a world or visualizer full screen while the phone is the rack.", "monitor"),
    Triple("🖼 Live wallpaper", "Settings → Phone: live wallpaper. Any world as your Android wallpaper, reacting to everything the phone plays; tap it for ripples. Android's own Settings button on the wallpaper opens a properties sheet to switch world and colours.", "settings"),
)

@Composable
private fun PhonePage(c: HelpColors, wm: WindowManager?) {
    Heading("📱 REVERENDER_95 on your phone", c)
    T("The app goes beyond the website: these features use Android itself. Tap one to open the window where it lives.", color = c.text, size = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
    PHONE_HELP.filter { com.reverender.r95.BuildConfig.FULL || !it.first.contains("Retro notifications") }.forEach { (title, text, app) ->
        Column(Modifier.fillMaxWidth().clickable(enabled = app != null) { app?.let { wm?.open(it) } }.padding(vertical = 5.dp)) {
            T(title, color = c.link, bold = true, size = 13.sp)
            T(text, color = c.text, size = 12.sp)
        }
    }
}
