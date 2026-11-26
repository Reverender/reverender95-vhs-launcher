package com.reverender.r95.ui.vhs

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.service.notification.StatusBarNotification
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import com.reverender.r95.BuildConfig
import com.reverender.r95.os.Haptics
import kotlinx.coroutines.delay

/** one chat message; [from] = null for your own */
internal class ChatMsg(val from: String?, val text: String, val time: Long)

internal class Chat(val key: String, name: String) {
    var name by mutableStateOf(name)
    val msgs = mutableStateListOf<ChatMsg>()
    var unread by mutableIntStateOf(0)
    var last by mutableLongStateOf(0L)
    var group by mutableStateOf(false)
}

/**
 * VHS CHAT (VHS+ APK only): WhatsApp messages, taken from WhatsApp's notifications, with replies sent through
 * the notification's own reply button (the same thing Android Auto and smartwatches use). So:
 * - a chat shows up once a message arrives (WhatsApp's history can't be read), and is kept here (last 80);
 * - replying works while WhatsApp still has that chat's notification up (or until WhatsApp drops its reply
 *   button); otherwise OPEN jumps into WhatsApp itself.
 */
internal object VhsChats {
    private val PKGS = setOf("com.whatsapp", "com.whatsapp.w4b")
    /** UI check only: also take this package's messaging notifications (the emulator has no WhatsApp) */
    @Volatile var testPkg: String? = null
    val chats = mutableStateListOf<Chat>()
    var open by mutableStateOf<String?>(null)
    private val reply = HashMap<String, Notification.Action>()
    private val markRead = HashMap<String, Notification.Action>()
    private val tapIntent = HashMap<String, PendingIntent>()
    private var loaded = false
    var replyReady by mutableIntStateOf(0) // bumps when reply buttons change, so the screen updates

    fun canReply(key: String) = reply.containsKey(key)
    fun canMarkRead(key: String) = markRead.containsKey(key)
    fun chat(key: String?) = chats.firstOrNull { it.key == key }

    private fun prefs(ctx: Context) = ctx.getSharedPreferences("vhs_chats", Context.MODE_PRIVATE)

    fun load(ctx: Context) {
        if (loaded || !BuildConfig.NOTIF) return
        loaded = true
        runCatching {
            val arr = org.json.JSONArray(prefs(ctx).getString("chats", "[]"))
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val c = Chat(o.getString("k"), o.getString("n"))
                c.unread = o.optInt("u"); c.last = o.optLong("l"); c.group = o.optBoolean("g")
                val ms = o.getJSONArray("m")
                for (j in 0 until ms.length()) { val m = ms.getJSONObject(j); c.msgs.add(ChatMsg(if (m.isNull("f")) null else m.getString("f"), m.getString("t"), m.getLong("w"))) }
                chats.add(c)
            }
        }
    }

    private fun save(ctx: Context) = runCatching {
        val arr = org.json.JSONArray()
        for (c in chats.take(40)) {
            val ms = org.json.JSONArray()
            for (m in c.msgs.takeLast(80)) ms.put(org.json.JSONObject().put("f", m.from ?: org.json.JSONObject.NULL).put("t", m.text).put("w", m.time))
            arr.put(org.json.JSONObject().put("k", c.key).put("n", c.name).put("u", c.unread).put("l", c.last).put("g", c.group).put("m", ms))
        }
        prefs(ctx).edit().putString("chats", arr.toString()).apply()
    }

    /** a notification came in (or was updated): pick up a WhatsApp chat's messages and its reply button */
    fun onPosted(ctx: Context, s: StatusBarNotification) {
        if (!BuildConfig.NOTIF || (s.packageName !in PKGS && s.packageName != testPkg)) return
        load(ctx)
        val n = s.notification ?: return
        if (n.flags and Notification.FLAG_GROUP_SUMMARY != 0) return
        val style = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(n) ?: return
        val me = style.user.name?.toString()
        val title = (style.conversationTitle ?: n.extras.getCharSequence(Notification.EXTRA_TITLE))?.toString()?.trim().orEmpty()
        if (title.isEmpty()) return
        val key = (if (android.os.Build.VERSION.SDK_INT >= 26) n.shortcutId else null) ?: title
        val c = chat(key) ?: Chat(key, title).also { chats.add(0, it) }
        c.name = title.replace(Regex("\\s*\\(\\d+ (berichten|messages)\\)$"), "")
        c.group = style.isGroupConversation
        var added = 0
        for (m in style.messages) {
            val text = m.text?.toString() ?: continue
            val who = m.person?.name?.toString()
            val mine = who == null || who == me
            val from = if (mine) null else who
            val dup = c.msgs.any { it.text == text && it.from == from && (kotlin.math.abs(it.time - m.timestamp) < 1500 || (mine && kotlin.math.abs(it.time - m.timestamp) < 120_000)) }
            if (dup) continue
            val at = c.msgs.indexOfFirst { it.time > m.timestamp }.let { if (it < 0) c.msgs.size else it }
            c.msgs.add(at, ChatMsg(from, text, m.timestamp))
            if (!mine) added++
        }
        while (c.msgs.size > 80) c.msgs.removeAt(0)
        if (added > 0) {
            c.last = maxOf(c.last, c.msgs.lastOrNull()?.time ?: 0L)
            if (open != key) c.unread += added
            chats.sortByDescending { it.last }
        }
        n.actions?.firstOrNull { !it.remoteInputs.isNullOrEmpty() }?.let { reply[key] = it }
        n.actions?.firstOrNull {
            (android.os.Build.VERSION.SDK_INT >= 28 && it.semanticAction == Notification.Action.SEMANTIC_ACTION_MARK_AS_READ) ||
                it.title?.toString()?.lowercase()?.let { t -> "read" in t || "gelezen" in t } == true
        }?.let { markRead[key] = it }
        n.contentIntent?.let { tapIntent[key] = it }
        replyReady++
        save(ctx)
    }

    /** send [text] into chat [key] through WhatsApp's reply button; null = sent, else what went wrong */
    fun send(ctx: Context, key: String, text: String): String? {
        val a = reply[key] ?: return "NO REPLY BUTTON: TAP OPEN"
        val ris = a.remoteInputs ?: return "NO REPLY BUTTON: TAP OPEN"
        val intent = android.content.Intent()
        val b = android.os.Bundle(); for (r in ris) b.putCharSequence(r.resultKey, text)
        android.app.RemoteInput.addResultsToIntent(ris, intent, b)
        return try {
            a.actionIntent.send(ctx, 0, intent)
            chat(key)?.let { c -> c.msgs.add(ChatMsg(null, text, System.currentTimeMillis())); c.last = System.currentTimeMillis(); chats.sortByDescending { it.last } }
            save(ctx); null
        } catch (e: PendingIntent.CanceledException) {
            reply.remove(key); replyReady++; "REPLY EXPIRED: TAP OPEN"
        }
    }

    fun markRead(ctx: Context, key: String) {
        chat(key)?.unread = 0; save(ctx)
        markRead.remove(key)?.let { a -> runCatching { a.actionIntent.send() } }
        replyReady++
    }
    fun seen(ctx: Context, key: String) { chat(key)?.let { if (it.unread != 0) { it.unread = 0; save(ctx) } } }

    /** WhatsApp itself: in this chat if its notification is still there, else the app */
    fun openApp(ctx: Context, key: String?) {
        val pi = key?.let { tapIntent[it] }
        if (pi != null) {
            // Android 14+: a notification's tap action may only start WhatsApp's screen when we say so
            val opts = android.app.ActivityOptions.makeBasic()
            if (android.os.Build.VERSION.SDK_INT >= 34) opts.setPendingIntentBackgroundActivityStartMode(android.app.ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
            if (runCatching { pi.send(ctx, 0, null, null, null, null, opts.toBundle()) }.onFailure { android.util.Log.w("r95", "chat open", it) }.isSuccess) return
        }
        // the app (found among the installed apps: package visibility hides it from a plain lookup)
        val app = com.reverender.r95.os.Launcher.apps.firstOrNull { it.component.packageName in PKGS }
        if (app != null) { com.reverender.r95.os.Launcher.launch(ctx, app, fullScreen = true); return }
        val li = PKGS.firstNotNullOfOrNull { ctx.packageManager.getLaunchIntentForPackage(it) } ?: return
        runCatching { ctx.startActivity(li.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }

    /** swiped away: off the list (it comes back with the next message), and WhatsApp's notification marked read */
    fun dismiss(ctx: Context, key: String) {
        markRead.remove(key)?.let { a -> runCatching { a.actionIntent.send() } }
        chats.removeAll { it.key == key }; reply.remove(key); tapIntent.remove(key); replyReady++
        save(ctx)
    }
}

/** text cut into lines of at most [w] characters, broken between words */
private fun wrap(s: String, w: Int): List<String> {
    val out = ArrayList<String>()
    for (para in s.split('\n')) {
        var line = ""
        for (word0 in para.split(' ').filter { it.isNotEmpty() }) {
            var word = word0
            while (word.length > w) { if (line.isNotEmpty()) { out.add(line); line = "" }; out.add(word.take(w)); word = word.drop(w) }
            line = if (line.isEmpty()) word else if (line.length + 1 + word.length <= w) "$line $word" else { out.add(line); word }
        }
        if (line.isNotEmpty()) out.add(line)
    }
    return out.ifEmpty { listOf("") }
}

private fun hhmm(t: Long): String = java.text.SimpleDateFormat(if (com.reverender.r95.os.OsPrefs.vhs24h) "HH:mm" else "h:mma", java.util.Locale.US).format(java.util.Date(t)).uppercase()

/** VHS CHAT: the chat list, or one chat with its messages and a line to type a reply */
@Composable
internal fun VhsChat(onExit: () -> Unit) {
    val ctx = LocalContext.current
    val o = LocalOsd.current
    remember { VhsChats.load(ctx) }
    val key = VhsChats.open
    BackHandler(enabled = key != null) { VhsChats.open = null }
    Column(Modifier.fillMaxSize().padding(horizontal = pxDp(2f), vertical = pxDp(2f)), verticalArrangement = Arrangement.spacedBy(pxDp(1.4f))) {
        val c = VhsChats.chat(key)
        if (c == null) ChatList(onExit) else ChatView(c)
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.ChatList(onExit: () -> Unit) {
    val ctx = LocalContext.current
    val o = LocalOsd.current
    val chats = VhsChats.chats
    VhsHeader("WHATSAPP", if (!VhsNotifs.connected) "NO NOTIFS ACCESS" else "${chats.size} CHAT" + if (chats.size == 1) "" else "S")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(2.5f)), verticalAlignment = Alignment.CenterVertically) {
        VhsKey("OPEN WHATSAPP", boxed = true) { Haptics.tick(); VhsChats.openApp(ctx, null) }
    }
    Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(pxDp(1.6f))) {
        if (!VhsNotifs.connected) {
            VhsText("SETUP > NOTIFS:", color = o.fg.copy(alpha = 0.7f)); VhsText("GRANT ACCESS FIRST", color = o.fg.copy(alpha = 0.7f))
        }
        if (chats.isEmpty()) {
            VhsText("NO MESSAGES YET", color = o.fg.copy(alpha = 0.7f))
            VhsText("NEW WHATSAPP MESSAGES", color = o.fg.copy(alpha = 0.7f))
            VhsText("COME IN HERE", color = o.fg.copy(alpha = 0.7f))
        }
        for (c in chats) {
            // swipe a chat left or right to dismiss it (it slides away and fades); tap to open it
            var dx by remember(c.key) { mutableStateOf(0f) }
            val scope = androidx.compose.runtime.rememberCoroutineScope()
            Column(Modifier.fillMaxWidth()
                .graphicsLayer { translationX = dx; alpha = (1f - kotlin.math.abs(dx) / size.width).coerceIn(0f, 1f) }
                .pointerInput(c.key) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (kotlin.math.abs(dx) > size.width * 0.3f) { Haptics.clunk("eject"); VhsChats.dismiss(ctx, c.key) }
                            else scope.launch { androidx.compose.animation.core.animate(dx, 0f) { v, _ -> dx = v } }
                        },
                        onDragCancel = { dx = 0f },
                    ) { ch, d -> ch.consume(); dx += d }
                }
                .pointerInput(c.key) { detectTapGestures { Haptics.tick(); VhsChats.open = c.key; VhsChats.seen(ctx, c.key) } }) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    VhsText(vhsSafe(c.name).trim().take(if (c.unread > 0) 16 else 21), invert = c.unread > 0)
                    Spacer(Modifier.weight(1f))
                    if (c.unread > 0) VhsText(c.unread.coerceAtMost(99).toString())
                }
                c.msgs.lastOrNull()?.let { m ->
                    val who = if (m.from == null) "ME: " else if (c.group) vhsSafe(m.from).trim().substringBefore(' ').take(8) + ": " else ""
                    VhsText((who + vhsSafe(m.text).replace(Regex(" +"), " ").trim()).take(21), color = o.fg.copy(alpha = 0.6f))
                }
            }
        }
    }
    Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { Haptics.tick(); onExit() } }) {
        Column {
            VhsText("TAP CHAT  SWIPE: REMOVE", invert = true)
            VhsText("TAP HERE TO RETURN", invert = true)
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.ChatView(c: Chat) {
    val ctx = LocalContext.current
    val o = LocalOsd.current
    val openKb = LocalOpenKb.current
    val blink = rememberBlink()
    var draft by remember(c.key) { mutableStateOf("") }
    var msg by remember(c.key) { mutableStateOf<String?>(null) }
    LaunchedEffect(msg) { if (msg != null) { delay(3500); msg = null } }
    VhsChats.replyReady // re-read the reply buttons when they change
    fun send() {
        val t = draft.trim()
        if (t.isEmpty()) return
        val err = VhsChats.send(ctx, c.key, t)
        if (err == null) { draft = ""; Haptics.clunk("play") } else { msg = err; Haptics.tick() }
    }
    RegisterKb(VhsKb({ draft }, { draft = it.take(600) }, raw = true) { send() })
    LaunchedEffect(c.msgs.size) { VhsChats.seen(ctx, c.key) }
    VhsHeader(vhsSafe(c.name).trim().take(20), if (VhsChats.canReply(c.key)) "WHATSAPP  REPLY ON" else "WHATSAPP  READ ONLY")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(2.5f)), verticalAlignment = Alignment.CenterVertically) {
        VhsKey("◀CHATS", boxed = true) { Haptics.tick(); VhsChats.open = null }
        VhsKey("OPEN", boxed = true) { Haptics.tick(); VhsChats.openApp(ctx, c.key) }
        if (VhsChats.canMarkRead(c.key)) VhsKey("READ", boxed = true) { Haptics.tick(); VhsChats.markRead(ctx, c.key) }
    }
    val scroll = rememberScrollState()
    LaunchedEffect(c.msgs.size, scroll.maxValue) { scroll.scrollTo(scroll.maxValue) }
    Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(scroll), verticalArrangement = Arrangement.spacedBy(pxDp(1.2f))) {
        var lastFrom: String? = "\u0000"
        for (m in c.msgs) {
            val mine = m.from == null
            Column(Modifier.fillMaxWidth(), horizontalAlignment = if (mine) Alignment.End else Alignment.Start) {
                if (m.from != lastFrom) {
                    val who = if (mine) "ME" else if (c.group) vhsSafe(m.from!!).trim().take(14) else vhsSafe(c.name).trim().substringBefore(' ').take(14)
                    VhsText("$who " + hhmm(m.time), color = o.fg.copy(alpha = 0.55f))
                }
                val lines = wrap(vhsSafe(m.text).replace(Regex(" +"), " ").trim().ifEmpty { "(EMOJI / MEDIA)" }, 19)
                Column(horizontalAlignment = if (mine) Alignment.End else Alignment.Start) {
                    for (l in lines) VhsText(if (mine) "$l " else " $l", invert = mine)
                }
            }
            lastFrom = m.from
        }
    }
    msg?.let { VhsText(it, invert = true) }
    // the reply line: tap it for the keyboard; enter / SEND sends
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(1.5f)), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).pointerInput(Unit) { detectTapGestures(onLongPress = { draft = "" }) { openKb() } }) {
            val shown = vhsSafe(draft).replace(Regex(" +"), " ")
            VhsText(">" + (if (draft.isEmpty()) "" else shown.takeLast(14)) + if (blink) "_" else " ")
        }
        VhsKey("SEND", boxed = true) { send() }
    }
    Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { Haptics.tick(); VhsChats.open = null } }) {
        Column {
            VhsText("TAP > TO TYPE A REPLY", invert = true)
            VhsText("TAP HERE FOR CHATS", invert = true)
        }
    }
}
