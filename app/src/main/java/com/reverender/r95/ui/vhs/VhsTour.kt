package com.reverender.r95.ui.vhs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.reverender.r95.BuildConfig
import com.reverender.r95.os.Haptics
import com.reverender.r95.os.OsPrefs
import kotlinx.coroutines.delay

/** the help tour: on first boot (and again from SETUP > TOUR) */
internal object VhsTour {
    var open by mutableStateOf(false)
}

private class TourPage(val title: String, val lines: List<String>)

private fun tourPages(): List<TourPage> = buildList {
    add(TourPage("WELCOME", listOf("YOUR PHONE IS NOW A VCR.", "THIS TOUR SHOWS WHAT", "EVERYTHING DOES.", "", "TAP NEXT TO GO ON.", "HOLD SKIP TO LEAVE.")))
    add(TourPage("HOME SCREEN", listOf("▲ SWIPE UP: YOUR APPS", "▼ SWIPE DOWN: PHONE", "  NOTIFICATIONS", "◀▶ SWIPE SIDEWAYS:", "  A MENU OR APP OF", "  YOUR CHOICE (SETUP)", "TAP THE HELP BAR: SETUP")))
    add(TourPage("CLOCK + TITLES", listOf("HOLD THE CLOCK: OPENS", "  YOUR CLOCK APP", "TITLE1: YOUR OWN TEXT", "SECOND LINE: A NEWS", "  TICKER (RSS FEED)", "EMPTY TITLES: THE BARS", "  DISAPPEAR")))
    add(TourPage("APP DRAWER", listOf("SWIPE UP, TYPE TO FIND", "  AN APP, ENTER = START", "HOLD AN APP: APP INFO", "  OR UNINSTALL", "4 LOOKS: SETUP > DRAWER", "PIN APPS: SETUP")))
    add(TourPage("MUSIC DECK", listOf("PLAY / ◀◀ / ▶▶ ON HOME", "RADIO: STATIONS, HOLD", "  ONE TO REMOVE IT", "FIND A STATION, HOLD IT", "  TO ADD IT", "SONG NAMES SCROLL WHILE", "  PLAYING. EQ + PLAYLIST")))
    add(TourPage("VIDEO + GAMES", listOf("LOAD: THE TAPE DECK", "  (VIDEOS) AND THE", "  GAME DECK",
        if (BuildConfig.PLAY) "YOUTUBE: YOUTUBE'S OWN" else "SEARCH YOUTUBE OR",
        if (BuildConfig.PLAY) "  PLAYER IN A VHS FRAME" else "  THE ARCHIVE",
        if (BuildConfig.PLAY) "GAMES: YOUR OWN FILES" else "GAMES: SEARCH OR FILE",
        "NES SNES GB GBA N64")))
    add(TourPage("VHS WEB", listOf("A SMALL WEB BROWSER", "HOMEPAGE: SETUP>WEBHOME", "LEAVING IT WIPES", "  COOKIES, HISTORY", "  AND DATA")))
    if (BuildConfig.NOTIF) {
        add(TourPage("NOTIFICATIONS", listOf("TOP RIGHT: HOW MANY", "  NOTIFICATIONS YOU", "  HAVE, NEW ONES FLASH", "  THE APP'S NAME", "NEEDS NOTIFICATION", "  ACCESS: SETUP>NOTIFS")))
        add(TourPage("VHS CHAT", listOf("YOUR CHAT MESSAGES ON", "  ONE SCREEN, REPLY", "  RIGHT THERE", "SWIPE A MESSAGE AWAY", "  TO CLEAR IT", "SWIPE THE BLANK SPACE", "  SIDEWAYS: HOME")))
    }
    add(TourPage("SETUP", listOf("THEME + TEXT COLOURS", "CRT: TV STYLES, PLUS", "  SNOW, GLITCH, SCAN,", "  LINES, CURVE SLIDERS", "FONT, WALLPAPER, RSS", "TOUR: THIS HELP AGAIN")))
    add(TourPage("READY", listOf("BACK KEY: ONE MENU BACK", "HOME KEY: HOME SCREEN", "", "ENJOY THE TAPE.", "TAP START.")))
}

/**
 * The help tour over the whole screen. NEXT / BACK page through it; SKIP only works when held down (the bar
 * fills up), so it isn't skipped by a stray tap.
 */
@Composable
internal fun VhsTourHost() {
    LaunchedEffect(Unit) { if (!OsPrefs.vhsTourDone) VhsTour.open = true }
    if (!VhsTour.open) return
    val o = LocalOsd.current
    val pages = remember { tourPages() }
    var page by remember { mutableIntStateOf(0) }
    var hold by remember { mutableFloatStateOf(0f) }
    var holding by remember { mutableStateOf(false) }
    var hint by remember { mutableStateOf(false) }
    fun done() { Haptics.clunk("eject"); OsPrefs.setVhsTourDone(true); VhsTour.open = false; page = 0 }
    BackHandler { if (page > 0) page-- else hint = true }
    // the SKIP bar fills over 1.5 s while held; let go early and it empties
    LaunchedEffect(holding) {
        if (!holding) { hold = 0f; return@LaunchedEffect }
        val start = System.nanoTime()
        while (hold < 1f) {
            delay(16); hold = ((System.nanoTime() - start) / 1.5e9f).coerceAtMost(1f)
        }
        done()
    }
    LaunchedEffect(hint) { if (hint) { delay(1500); hint = false } }
    val p = pages[page]
    Box(Modifier.fillMaxSize().background(o.bg).pointerInput(Unit) { detectTapGestures { } }) {
        // (the bottom row sits well clear of the CRT's curved corners, and the keys are big enough for a thumb)
        Column(Modifier.fillMaxSize().padding(start = pxDp(3f), end = pxDp(3f), top = pxDp(3f), bottom = pxDp(9f)), verticalArrangement = Arrangement.spacedBy(pxDp(1.6f))) {
            VhsHeader("HELP TOUR", "%02d/%02d".format(page + 1, pages.size))
            Box(Modifier.fillMaxWidth().background(o.fg).padding(pxDp(1f))) { VhsText(p.title, invert = true) }
            Column(Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(pxDp(1.4f))) {
                p.lines.forEach { VhsText(it) }
            }
            if (hint) VhsText("HOLD SKIP TO SKIP", color = o.fg.copy(alpha = 0.7f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                // SKIP: hold it down
                Box(Modifier.border(pxDp(0.5f), o.fg).pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(); holding = true; Haptics.tick()
                        val start = System.currentTimeMillis()
                        do { val e = awaitPointerEvent() } while (e.changes.any { it.pressed })
                        holding = false
                        if (System.currentTimeMillis() - start < 1400) hint = true
                    }
                }) {
                    Box(Modifier.matchParentSize()) { Box(Modifier.fillMaxHeight().fillMaxWidth(hold).background(o.fg.copy(alpha = 0.45f))) }
                    Box(Modifier.padding(horizontal = pxDp(2f), vertical = pxDp(3f))) { VhsText(if (holding) "SKIPPING…" else "HOLD SKIP") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(pxDp(3f))) {
                    if (page > 0) VhsKey("◀BACK", boxed = true, scale = 1.3f) { Haptics.tick(); page-- }
                    VhsKey(if (page == pages.lastIndex) "START" else "NEXT▶", boxed = true, selected = true, scale = 1.3f) {
                        Haptics.tick(); if (page == pages.lastIndex) done() else page++
                    }
                }
            }
        }
    }
}

/**
 * Google Play edition: the prominent disclosure shown before the phone's notification-access settings open
 * (Play's rule for apps that read notifications). Nothing is read until the user agrees there and grants access.
 */
internal object VhsNotifDisclosure { var open by mutableStateOf(false) }

@Composable
internal fun VhsNotifDisclosureHost() {
    if (!VhsNotifDisclosure.open) return
    val o = LocalOsd.current
    val ctx = androidx.compose.ui.platform.LocalContext.current
    BackHandler { VhsNotifDisclosure.open = false }
    Box(Modifier.fillMaxSize().background(o.bg).pointerInput(Unit) { detectTapGestures { } }) {
        Column(Modifier.fillMaxSize().padding(pxDp(3f)), verticalArrangement = Arrangement.spacedBy(pxDp(1.4f))) {
            VhsHeader("NOTIFICATIONS", "BEFORE YOU GRANT")
            Column(Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(pxDp(1.2f))) {
                listOf(
                    "THIS APP CAN READ YOUR", "NOTIFICATIONS, TO:", "- COUNT THEM AND SHOW",
                    "  THE APP NAME (TOP", "  RIGHT ON HOME)", "- SHOW MESSAGES FROM", "  YOUR APPS IN VHS CHAT", "  AND LET YOU REPLY", "", "IT STAYS ON THIS PHONE:", "NOTHING IS SENT TO US", "OR ANYONE ELSE, AND",
                    "NOTHING IS STORED", "AFTER THE APP CLOSES.", "TURN IT OFF ANY TIME", "IN ANDROID SETTINGS.",
                ).forEach { VhsText(it) }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                VhsKey("NO THANKS", boxed = true) { Haptics.tick(); VhsNotifDisclosure.open = false }
                VhsKey("AGREE▶", boxed = true, selected = true) {
                    Haptics.tick(); VhsNotifDisclosure.open = false
                    com.reverender.r95.os.RetroAlerts.openListenerSettings(ctx)
                }
            }
            VhsText("PRIVACY: REVERENDER.COM/PRIVACY", color = o.fg.copy(alpha = 0.6f))
        }
    }
}
