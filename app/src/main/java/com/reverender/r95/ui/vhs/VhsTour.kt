package com.reverender.r95.ui.vhs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.reverender.r95.BuildConfig
import com.reverender.r95.os.Haptics
import com.reverender.r95.os.OsPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

/** the help tour: on first boot (and again from SETUP > TOUR) */
internal object VhsTour {
    var open by mutableStateOf(false)
}

/** what the finger does on the screenshot (positions as 0..1000 of the phone's full screen) */
private sealed class Move {
    object None : Move()
    class Tap(val x: Int, val y: Int) : Move()
    class Hold(val x: Int, val y: Int) : Move()
    class Swipe(val x0: Int, val y0: Int, val x1: Int, val y1: Int) : Move()
}

/** one step: a title, a few lines, the screen before and after, and the move in between */
private class TourPage(val title: String, val lines: List<String>, val before: String? = null, val after: String? = null, val move: Move = Move.None)

// the screenshots (assets/tour) are the whole screen (the launcher runs full screen)
private const val SHOT_TOP = 0f
private const val SHOT_BOTTOM = 1f

private fun tourPages(): List<TourPage> = buildList {
    add(TourPage("WELCOME", listOf("YOUR PHONE IS NOW A VCR.", "THIS IS YOUR HOME SCREEN.", "NEXT: A STEP BY STEP TOUR"), "t01-home"))
    add(TourPage("YOUR APPS", listOf("SWIPE UP FOR ALL YOUR", "APPS. TAP ONE TO START IT."), "t01-home", "t02-drawer", Move.Swipe(500, 420, 500, 120)))
    add(TourPage("APP INFO / UNINSTALL", listOf("HOLD AN APP IN THE LIST", "FOR APP INFO OR TO", "UNINSTALL IT."), "t02-drawer", "t03-drawer-hold", Move.Hold(250, 340)))
    add(TourPage("FIND AN APP", listOf("TAP NEXT TO APPS: AND TYPE", "A FEW LETTERS. ENTER", "STARTS THE FIRST ONE."), "t01-home", "t04-kb", Move.Tap(460, 133)))
    add(TourPage("FIND A RADIO STATION", listOf("TAP APPS: AND IT TURNS TO", "RADIO: TYPE A STYLE OR A", "NAME. HOLD ONE TO ADD IT."), "t01-home", "t04b-radio", Move.Tap(150, 133)))
    add(TourPage("NOTIFICATIONS", listOf("SWIPE DOWN FOR YOUR", "PHONE'S NOTIFICATIONS."), "t01-home", "t05-shade", Move.Swipe(500, 40, 500, 600)))
    add(TourPage("SETUP", listOf("TAP THE BAR AT THE BOTTOM", "TO OPEN SETUP."), "t01-home", "t06-setup", Move.Tap(500, 950)))
    add(TourPage("CHANGE THE LOOK", listOf("TAP A SETTING TO CHANGE", "IT: CRT, THEME, FONT,", "WALLPAPER, DRAWER, TITLE."), "t06-setup", "t07-setup-crt", Move.Tap(200, 244)))
    add(TourPage("CLOCK", listOf("HOLD THE CLOCK TO OPEN", "YOUR CLOCK APP."), "t01-home", "t08-clock", Move.Hold(760, 483)))
    add(TourPage("RADIO", listOf("TAP A STATION. SWIPE THE", "ROW FOR MORE. HOLD ONE", "TO TAKE IT OFF THE ROW."), "t01-home", "t09-station", Move.Tap(650, 180)))
    add(TourPage("PLAY", listOf("HOLD ▶ TO PLAY (A HOLD, SO", "A BRUSH OF THE THUMB WON'T", "START IT). ◀◀ ▶▶ SWITCH."), "t09-station", "t10-playing", Move.Hold(200, 483)))
    add(TourPage("EQUALIZER", listOf("HOLD EQ TO SHAPE THE", "SOUND. TAP A PRESET NEXT", "TO IT FOR A QUICK CHANGE."), "t10-playing", "t11-eq", Move.Hold(80, 704)))
    add(TourPage("PLAYLIST", listOf("HOLD PLAYLIST FOR THE", "SONGS IN THE DECK."), "t01-home", "t12-playlist", Move.Hold(200, 794)))
    add(TourPage("LOAD A TAPE", listOf("HOLD ⏏LOAD FOR THE VIDEO,", "GAME AND MUSIC DECKS."), "t01-home", "t13-load", Move.Hold(880, 133)))
    add(TourPage("GAMES", if (BuildConfig.PLAY) listOf("GAMES: LOAD YOUR OWN GAME", "FILE, OR GET A FREE", "HOMEBREW GAME.") else listOf("GAMES: SEARCH A TITLE OR", "LOAD YOUR OWN GAME FILE."), "t13-load", "t14-games", Move.Tap(460, 133)))
    add(TourPage("MUSIC", listOf("MUSIC: FIND SONGS AND", "ADD THEM TO THE DECK."), "t13-load", "t15-music", Move.Tap(700, 133)))
    if (BuildConfig.NOTIF) {
        add(TourPage("VHS CHAT", listOf("SWIPE SIDEWAYS FOR A MENU", "OF YOUR CHOICE. HERE:", if (BuildConfig.PLAY) "MESSAGES FROM YOUR APPS." else "YOUR CHAT MESSAGES."), "t01-home", "t16-chat", Move.Swipe(850, 400, 150, 400)))
        add(TourPage("READ + REPLY", listOf("TAP A CHAT TO READ IT", "AND REPLY. SWIPE ONE", "AWAY TO CLEAR IT."), "t16-chat", "t17-chat-open", Move.Tap(330, 160)))
    }
    add(TourPage("VHS WEB", listOf(if (BuildConfig.NOTIF) "THE OTHER SIDE: VHS WEB." else "SWIPE SIDEWAYS: VHS WEB.", "LEAVING IT WIPES COOKIES", "AND HISTORY. (SET: SETUP)"), "t01-home", "t18-web", Move.Swipe(150, 400, 850, 400)))
    add(TourPage("READY", listOf("BACK KEY: ONE STEP BACK.", "SETUP > TOUR: THIS AGAIN.", "BE KIND, REWIND."), "t10-playing"))
}

/** the screenshots, decoded once (they're small) */
private val shots = mutableStateMapOf<String, ImageBitmap>()

@Composable
private fun rememberShot(name: String?): ImageBitmap? {
    if (name == null) return null
    val ctx = LocalContext.current
    LaunchedEffect(name) {
        if (shots[name] == null) withContext(Dispatchers.IO) {
            runCatching { ctx.assets.open("tour/$name.webp").use { android.graphics.BitmapFactory.decodeStream(it) } }.getOrNull()
        }?.let { shots[name] = it.asImageBitmap() }
    }
    return shots[name]
}

/**
 * The screenshot of a step, played as a loop: the screen before, a finger doing the move (a tap ripple, a
 * hold filling a ring, a swipe sliding across), then the screen after.
 */
@Composable
private fun TourShot(p: TourPage, modifier: Modifier) {
    val o = LocalOsd.current
    val before = rememberShot(p.before)
    val after = rememberShot(p.after)
    // the loop's clock, in seconds
    var t by remember(p) { mutableFloatStateOf(0f) }
    LaunchedEffect(p) {
        val start = System.nanoTime()
        while (isActive) { androidx.compose.runtime.withFrameNanos { }; t = ((System.nanoTime() - start) / 1e9f) % 4.6f }
    }
    Box(modifier.aspectRatio(1080f / (1920f * (SHOT_BOTTOM - SHOT_TOP))).border(pxDp(0.6f), o.fg).padding(pxDp(0.6f))) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width.toInt(); val h = size.height.toInt()
            // before → after: cross-fade at 1.7–2.0 s, back at the end of the loop
            val a = if (p.after == null) 0f else when {
                t < 1.7f -> 0f
                t < 2.0f -> (t - 1.7f) / 0.3f
                t < 4.3f -> 1f
                else -> 1f - (t - 4.3f) / 0.3f
            }
            if (before != null) drawImage(before, dstSize = IntSize(w, h), alpha = 1f - a)
            if (after != null && a > 0f) drawImage(after, dstSize = IntSize(w, h), alpha = a)
            if (before == null && after == null) drawRect(o.fg.copy(alpha = 0.1f))
            // the finger, in the screenshot's own coordinates
            fun at(x: Int, y: Int) = Offset(x / 1000f * size.width, ((y / 1000f) - SHOT_TOP) / (SHOT_BOTTOM - SHOT_TOP) * size.height)
            val r = size.width * 0.055f
            val show = when { t < 0.3f -> t / 0.3f; t < 1.7f -> 1f; t < 2.0f -> 1f - (t - 1.7f) / 0.3f; else -> 0f }
            fun finger(c: Offset, pressed: Boolean) {
                drawCircle(Color.Black.copy(alpha = 0.35f * show), r * 1.15f, c + Offset(r * 0.12f, r * 0.12f))
                drawCircle(Color.White.copy(alpha = (if (pressed) 0.85f else 0.6f) * show), r, c)
                drawCircle(o.bg.copy(alpha = show), r, c, style = Stroke(r * 0.18f))
            }
            when (val m = p.move) {
                is Move.Tap -> {
                    val c = at(m.x, m.y); val pressed = t in 0.8f..1.1f
                    if (t in 0.8f..1.6f) { val k = (t - 0.8f) / 0.8f; drawCircle(Color.White.copy(alpha = (1f - k) * 0.9f), r * (1f + 1.6f * k), c, style = Stroke(r * 0.2f)) }
                    finger(c, pressed)
                }
                is Move.Hold -> {
                    val c = at(m.x, m.y); val k = ((t - 0.5f) / 1.1f).coerceIn(0f, 1f)
                    finger(c, t > 0.5f)
                    if (t > 0.5f && t < 1.7f) drawArc(Color.White.copy(alpha = show), -90f, 360f * k, false, c - Offset(r * 1.5f, r * 1.5f), Size(r * 3f, r * 3f), style = Stroke(r * 0.3f))
                }
                is Move.Swipe -> {
                    val s = at(m.x0, m.y0); val e = at(m.x1, m.y1)
                    val k = ((t - 0.6f) / 0.7f).coerceIn(0f, 1f).let { it * it * (3 - 2 * it) }
                    val c = s + (e - s) * k
                    if (k > 0f && show > 0f) drawLine(Color.White.copy(alpha = 0.5f * show), s, c, strokeWidth = r * 0.5f)
                    finger(c, k > 0f && k < 1f)
                }
                Move.None -> {}
            }
        }
    }
}

/**
 * The help tour over the whole screen: each step shows the real screen with a finger doing the move, and
 * what happens. NEXT / BACK page through it; SKIP only works when held down (the bar fills up), so it isn't
 * skipped by a stray tap.
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
    val p = pages[page.coerceIn(0, pages.lastIndex)]
    Box(Modifier.fillMaxSize().background(o.bg).pointerInput(Unit) { detectTapGestures { } }) {
        // (the bottom row sits well clear of the CRT's curved corners, and the keys are big enough for a thumb)
        Column(Modifier.fillMaxSize().padding(start = pxDp(3f), end = pxDp(3f), top = pxDp(3f), bottom = pxDp(4f)), verticalArrangement = Arrangement.spacedBy(pxDp(1.6f))) {
            VhsHeader("HELP TOUR", "%02d/%02d".format(page + 1, pages.size))
            Box(Modifier.fillMaxWidth().background(o.fg).padding(pxDp(1f))) { VhsText(p.title, invert = true) }
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                TourShot(p, Modifier.fillMaxHeight())
            }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(pxDp(1.2f))) {
                p.lines.forEach { VhsText(it) }
            }
            if (hint) VhsText("HOLD SKIP TO SKIP", color = o.fg.copy(alpha = 0.7f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                // SKIP: hold it down. (The touch area reaches well above and below the key: the CRT's curve draws the
                // bottom row a little higher than where it sits.)
                Box(Modifier.pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(); holding = true; Haptics.tick()
                        val start = System.currentTimeMillis()
                        do { val e = awaitPointerEvent() } while (e.changes.any { it.pressed })
                        holding = false
                        if (System.currentTimeMillis() - start < 1400) hint = true
                    }
                }.padding(vertical = pxDp(6f))) {
                    Box(Modifier.border(pxDp(0.5f), o.fg)) {
                        Box(Modifier.matchParentSize()) { Box(Modifier.fillMaxHeight().fillMaxWidth(hold).background(o.fg.copy(alpha = 0.45f))) }
                        Box(Modifier.padding(horizontal = pxDp(2f), vertical = pxDp(3f))) { VhsText(if (holding) "SKIPPING…" else "HOLD SKIP") }
                    }
                }
                fun back() { Haptics.tick(); page-- }
                fun next() { Haptics.tick(); if (page == pages.lastIndex) done() else page++ }
                Row(horizontalArrangement = Arrangement.spacedBy(pxDp(3f))) {
                    if (page > 0) Box(Modifier.pointerInput(page) { detectTapGestures { back() } }.padding(vertical = pxDp(6f))) { VhsKey("◀BACK", boxed = true, scale = 1.3f) { back() } }
                    Box(Modifier.pointerInput(page) { detectTapGestures { next() } }.padding(vertical = pxDp(6f))) {
                        VhsKey(if (page == pages.lastIndex) "START" else "NEXT▶", boxed = true, selected = true, scale = 1.3f) { next() }
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
