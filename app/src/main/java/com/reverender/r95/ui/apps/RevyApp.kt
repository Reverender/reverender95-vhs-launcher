package com.reverender.r95.ui.apps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.reverender.r95.net.ChatTurn
import com.reverender.r95.net.RevyBrain
import com.reverender.r95.net.RevyClient
import com.reverender.r95.net.RevySource
import com.reverender.r95.ui.win95.Btn
import com.reverender.r95.ui.win95.Field
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import com.reverender.r95.ui.win95.bevel
import kotlinx.coroutines.launch
import java.util.Locale

private class RevyMsg(val role: String, val text: String, val sources: List<RevySource> = emptyList(), val error: Boolean = false)

private const val REVY_CAN = "I can open and close any app, change the wallpaper, theme or desktop style, toggle CRT, play, pause or skip music, set the volume, do maths and unit conversions, tell the time and date, and look things up on the web for you. Type \"help\" for the full list."
private const val REVY_VOICE = " I speak with a British voice from this device whether or not Gemini Nano is available. This chat is temporary and clears when you close the window."
private const val REVY_HELLO = "Ah, good day to you. Revy here, at your service. Gemini Nano isn't available on this device, so I'm running on REVERENDER's built-in brain — and it's quick. $REVY_CAN$REVY_VOICE"
private const val REVY_SEARCH_FAIL = "I am sorry, that little search did not come through. The web connection may be having a wobble, and Gemini Nano is unavailable to help me reason around it. We can try the search again with different words, or I can still chat, tell a joke or story, explain something general, or help with supported REVERENDER controls. What would you like to try?"

/** REVY.EXE — AI DESKTOP COMPANION, like rr-revy.js: temporary chat, built-in brain, web search, British voice. */
@Composable
fun RevyChatApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val msgs = remember { mutableStateListOf(RevyMsg("assistant", REVY_HELLO)) }
    val history = remember { mutableStateListOf<ChatTurn>() }
    var input by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var footer by remember { mutableStateOf("READY · BUILT-IN BRAIN · TEMPORARY CHAT") }
    var voiceOn by remember { mutableStateOf(true) }
    var listening by remember { mutableStateOf(false) }
    var speaking by remember { mutableStateOf(false) }
    val list = rememberLazyListState()
    LaunchedEffect(msgs.size) { if (msgs.isNotEmpty()) list.animateScrollToItem(msgs.size - 1) }

    // British voice from this device (speechSynthesis en-GB)
    val tts = remember { arrayOfNulls<TextToSpeech>(1) }
    DisposableEffect(Unit) {
        tts[0] = TextToSpeech(context.applicationContext) { ok ->
            if (ok == TextToSpeech.SUCCESS) tts[0]?.let { t -> t.language = Locale.UK; t.setSpeechRate(1.0f); t.setPitch(1.0f) }
        }
        onDispose { tts[0]?.stop(); tts[0]?.shutdown() }
    }
    fun speak(t: String) {
        if (!voiceOn) return
        val clean = t.replace(Regex("https?://\\S+"), "").replace("•", "").take(3500)
        speaking = true
        tts[0]?.speak(clean, TextToSpeech.QUEUE_FLUSH, null, "revy")
    }
    fun add(role: String, text: String, sources: List<RevySource> = emptyList(), error: Boolean = false) {
        msgs.add(RevyMsg(role, text, sources, error))
        if (role == "user" || role == "assistant") { history.add(ChatTurn(role, text.take(700))); while (history.size > 8) history.removeAt(0) }
    }

    fun send(raw: String) {
        val n = raw.trim().take(1000)
        if (n.isEmpty() || busy) return
        add("user", n); input = ""
        busy = true; footer = "REVY IS THINKING · BUILT-IN BRAIN…"
        val ctx = history.toList()
        scope.launch {
            try {
                val r = RevyBrain.handle(n, ctx)
                if (r.kind != "search") { add("assistant", r.reply); speak(r.reply); footer = r.footer.ifEmpty { "BUILT-IN SKILL" } }
                else {
                    footer = "LOOKING UP SOURCES…"
                    try {
                        val c = RevyClient.ask(n, ctx.dropLast(0))
                        add("assistant", c.text, c.sources); speak(c.text)
                        footer = "SEARCH ANSWER READY · ${c.sources.size} SOURCES · ON-DEVICE AI UNAVAILABLE"
                    } catch (e: Exception) {
                        add("assistant", REVY_SEARCH_FAIL); speak(REVY_SEARCH_FAIL)
                        footer = "SEARCH CONNECTION ISSUE · COMPANION MODE ACTIVE"
                    }
                }
            } catch (e: Throwable) {
                android.util.Log.w("R95", "revy", e)
                add("assistant", "Revy could not complete that request just now.", error = true)
                footer = "BUILT-IN BRAIN UNAVAILABLE"
            } finally { busy = false }
        }
    }

    // 🎙 TALK: device speech recognition, then send
    val recognizer = remember { if (SpeechRecognizer.isRecognitionAvailable(context)) SpeechRecognizer.createSpeechRecognizer(context) else null }
    DisposableEffect(Unit) { onDispose { recognizer?.destroy() } }
    fun listen() {
        val r = recognizer ?: run { footer = "SPEECH INPUT IS NOT AVAILABLE ON THIS DEVICE"; return }
        r.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p: Bundle?) { listening = true; footer = "LISTENING… SPEAK NOW" }
            override fun onResults(b: Bundle?) {
                listening = false
                val t = b?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                if (t.isNotBlank()) send(t) else footer = "I DID NOT CATCH THAT · TRY AGAIN"
            }
            override fun onPartialResults(b: Bundle?) { b?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let { input = it } }
            override fun onError(e: Int) { listening = false; footer = "I DID NOT CATCH THAT · TRY AGAIN" }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(b: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onEvent(t: Int, b: Bundle?) {}
        })
        r.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        })
    }
    val askMic = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok -> if (ok) listen() else footer = "MICROPHONE PERMISSION DENIED" }

    Column(Modifier.fillMaxSize()) {
        // .revy-ai-menubar
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 6.dp, vertical = 3.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            T("HELP(ASK REVY)", size = 11.sp, bold = true, modifier = Modifier.clickable { send("help") })
            T("MEMORY: OFF", size = 11.sp, bold = true)
            T("AI: ON-DEVICE", size = 11.sp, bold = true)
            T("GEMINI NANO NOT SUPPORTED ON THIS DEVICE · WEB SEARCH FALLBACK", size = 11.sp, bold = true)
        }
        Column(Modifier.fillMaxSize().weight(1f).padding(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth().background(Color(0xFF0A0A14)).border(1.dp, Color(0xFF333344)).padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).background(if (busy) Color(0xFFFFCC00) else Color(0xFF00FF66), CircleShape))
                Spacer(Modifier.width(6.dp))
                T("REVY ONLINE · WEBSITE-ONLY AI", color = Color(0xFF00F7FF), size = 10.sp, bold = true, mono = true, modifier = Modifier.weight(1f))
                T(if (voiceOn) "VOICE: DEVICE ENGLISH" else "VOICE: OFF", color = Color(0xFFFF66CC), size = 10.sp, bold = true, mono = true)
            }
            LazyColumn(Modifier.fillMaxWidth().weight(1f).background(Color(0xFF05040C)).bevel(false).padding(6.dp), state = list, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(msgs) { m -> RevyBubble(m) { url -> com.reverender.r95.core.RevyRequests.browserUrl = url; RevyBrain.wm.open("browser") } }
            }
            T("TEMPORARY CHAT ONLY · WEBSITE CONTROLS ONLY · NO OPERATING-SYSTEM ACCESS", size = 9.sp, mono = true, bold = true, color = W95.Shadow)
            Field(input, { input = it.take(1000) }, Modifier.fillMaxWidth().height(64.dp), singleLine = false, hint = "Talk to Revy — ask a question or control the desktop…", onDone = { send(input) })
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Btn(if (voiceOn) "🔊 VOICE: ON" else "🔇 VOICE: OFF", { voiceOn = !voiceOn; if (!voiceOn) tts[0]?.stop() }, pressed = voiceOn, size = 11.sp)
                Btn(if (listening) "🎙 LISTENING" else "🎙 TALK", {
                    if (listening) { recognizer?.stopListening(); listening = false }
                    else if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) listen()
                    else askMic.launch(Manifest.permission.RECORD_AUDIO)
                }, pressed = listening, size = 11.sp)
                Btn("CLEAR", {
                    tts[0]?.stop(); msgs.clear(); history.clear(); msgs.add(RevyMsg("assistant", REVY_HELLO))
                    footer = "CHAT CLEARED · TEMPORARY CONTEXT RESET"
                }, size = 11.sp)
                Spacer(Modifier.weight(1f))
                Btn(if (busy) "THINKING…" else "SEND ↗", { send(input) }, enabled = !busy, bold = true, size = 11.sp)
            }
            T(footer, size = 9.sp, mono = true, bold = true, maxLines = 1)
        }
    }
}

/** .revy-message: REVY / YOU label, text with tappable links, and the sources list. */
@Composable
private fun RevyBubble(m: RevyMsg, openUrl: (String) -> Unit) {
    val bot = m.role != "user"
    Column(
        Modifier.fillMaxWidth().padding(start = if (bot) 0.dp else 28.dp, end = if (bot) 28.dp else 0.dp)
            .background(if (m.error) Color(0x33FF0040) else if (bot) Color(0x1A00F7FF) else Color(0x26FF00A0))
            .border(1.dp, if (bot) Color(0x5900F7FF) else Color(0x66FF00A0)).padding(7.dp),
    ) {
        T(if (bot) "REVY" else "YOU", color = if (bot) Color(0xFF00F7FF) else Color(0xFFFF66CC), size = 9.sp, bold = true, mono = true)
        val linkColor = Color(0xFF7FDBFF)
        val text = buildAnnotatedString {
            var last = 0
            if (bot) Regex("https?://[^\\s<>'\"`]+").findAll(m.text).forEach { r ->
                val f = r.value.trimEnd(')', ',', '.', '!', '?', ';', ':')
                append(m.text.substring(last, r.range.first))
                pushStringAnnotation("url", f); pushStyle(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)); append(f); pop(); pop()
                append(r.value.substring(f.length)); last = r.range.last + 1
            }
            append(m.text.substring(last))
        }
        ClickableText(text, style = W95.text.copy(color = Color(0xFFE8E8F0), fontSize = 12.sp, lineHeight = 17.sp)) { off ->
            text.getStringAnnotations("url", off, off).firstOrNull()?.let { openUrl(it.item) }
        }
        if (m.sources.isNotEmpty()) {
            T("SOURCES", color = Color(0xFFFF66CC), size = 9.sp, bold = true, mono = true, modifier = Modifier.padding(top = 4.dp))
            m.sources.take(6).forEachIndexed { i, s ->
                T("${i + 1}. ${s.title}", color = linkColor, size = 10.sp, maxLines = 1, modifier = Modifier.clickable { openUrl(s.url) }.padding(vertical = 1.dp))
            }
        }
    }
}
