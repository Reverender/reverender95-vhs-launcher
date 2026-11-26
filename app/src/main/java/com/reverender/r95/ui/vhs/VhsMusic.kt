package com.reverender.r95.ui.vhs

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.reverender.r95.audio.AmpEngine
import com.reverender.r95.os.OsPrefs
import com.reverender.r95.os.Haptics
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** a song found online: [url] is "yt:<video id>" (resolved when it plays) or a direct archive.org MP3 */
internal class MusicHit(val title: String, val sub: String, val url: String)

/** The music finder's state survives leaving the page. */
internal object MusicFind {
    var query by mutableStateOf("")
    var source by mutableStateOf("YT")
    var msg by mutableStateOf<String?>(null)
    val hits = mutableStateListOf<MusicHit>()
    var job: Job? = null

    /** put the song in the PLAYLIST (once) and, if [play], start it on the home screen player */
    fun add(h: MusicHit, play: Boolean) {
        val list = OsPrefs.vhsPlaylist
        var i = list.indexOfFirst { it.second == h.url }
        if (i < 0) { list.add(vhsSafe(h.title).take(60) to h.url); OsPrefs.savePlaylist(); i = list.lastIndex }
        AmpEngine.customAudioFiles.clear()
        AmpEngine.customAudioFiles.addAll(list.map { (n, u) -> AmpEngine.CustomFile(n, u) })
        if (play) AmpEngine.playCustomLocalFile(i)
    }
}

/**
 * MUSIC DECK in the LOAD menu: find songs on YouTube or the Internet Archive. Tap a song to play it on the home
 * screen player (it goes into the PLAYLIST too); + only adds it to the PLAYLIST.
 */
@Composable
internal fun VhsMusic(onExit: () -> Unit, onSwitch: (String) -> Unit, onPlayed: () -> Unit) {
    val o = LocalOsd.current
    val scope = rememberCoroutineScope()
    val openKb = LocalOpenKb.current
    val blink = rememberBlink()
    val f = MusicFind
    val inList = OsPrefs.vhsPlaylist.map { it.second }.toSet()

    fun search() {
        val q = f.query.trim()
        if (q.length < 2) { f.msg = "TYPE AT LEAST 2 LETTERS"; return }
        f.job?.cancel(); f.hits.clear(); Haptics.tick()
        f.msg = if (f.source == "YT") "SEARCHING YOUTUBE…" else "SEARCHING THE ARCHIVE…"
        f.job = scope.launch {
            try {
                if (f.source == "YT") {
                    val r = com.reverender.r95.net.YouTubeClient.search(q)
                    f.hits.clear(); f.hits.addAll(r.map { MusicHit(it.title, "YOUTUBE", "yt:" + it.videoId) })
                    f.msg = if (r.isEmpty()) "NO SONGS FOUND" else null
                } else {
                    com.reverender.r95.net.ArchiveMp3Search.search(q) { st ->
                        when (st) {
                            is com.reverender.r95.net.ArchiveMp3Search.Status.Results -> {
                                f.hits.clear()
                                f.hits.addAll(st.hits.take(40).map { h -> MusicHit(h.name.substringBeforeLast('.'), "ARCHIVE " + h.title.take(12), h.url) })
                                f.msg = null
                            }
                            is com.reverender.r95.net.ArchiveMp3Search.Status.Msg -> if (f.hits.isEmpty()) f.msg = if (st.error) "NO SONGS FOUND" else "SEARCHING THE ARCHIVE…"
                        }
                    }
                    if (f.hits.isEmpty()) f.msg = "NO SONGS FOUND"
                }
            } catch (x: kotlinx.coroutines.CancellationException) { throw x }
            catch (x: Exception) { f.msg = "SEARCH FAILED. CHECK THE CONNECTION." }
        }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = pxDp(2f), vertical = pxDp(2f)), verticalArrangement = Arrangement.spacedBy(pxDp(1.4f))) {
        VhsHeader("MUSIC DECK", "FIND A SONG")
        VhsDeckSwitch("music", onSwitch)
        Row(horizontalArrangement = Arrangement.spacedBy(pxDp(3f)), verticalAlignment = Alignment.CenterVertically) {
            VhsKey("YOUTUBE", selected = f.source == "YT", boxed = true) { if (f.source != "YT") { f.source = "YT"; f.hits.clear(); f.msg = null } }
            VhsKey("ARCHIVE", selected = f.source == "IA", boxed = true) { if (f.source != "IA") { f.source = "IA"; f.hits.clear(); f.msg = null } }
        }
        RegisterKb(VhsKb({ f.query }, { f.query = it.take(24) }) { search() })
        // the phone keyboard types the search: swipe up or tap the FIND line (hold it to clear)
        Box(Modifier.fillMaxWidth().pointerInput(Unit) { detectTapGestures(onLongPress = { f.query = "" }) { openKb() } }) {
            VhsText("FIND:" + f.query.takeLast(16) + if (blink) "_" else " ")
        }
        Column(Modifier.fillMaxWidth().weight(1f).then(if (f.hits.isNotEmpty()) Modifier.verticalScroll(rememberScrollState()) else Modifier), verticalArrangement = Arrangement.spacedBy(pxDp(1.6f))) {
            if (f.hits.isEmpty() && f.msg == null) {
                VhsText("TYPE AN ARTIST OR SONG,", color = o.fg.copy(alpha = 0.7f))
                VhsText("PRESS OK", color = o.fg.copy(alpha = 0.7f))
            }
            f.msg?.let { VhsText(it.take(22), color = o.fg.copy(alpha = 0.7f)) }
            f.hits.forEach { h ->
                val added = h.url in inList
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(2f)), verticalAlignment = Alignment.CenterVertically) {
                    // + : into the playlist only
                    VhsKey(if (added) "✓" else "+", selected = added, boxed = true) { if (!added) { Haptics.tick(); f.add(h, play = false) } }
                    // the song: play it now on the home screen player
                    Column(Modifier.weight(1f).pointerInput(h) { detectTapGestures { Haptics.clunk("play"); f.add(h, play = true); onPlayed() } }) {
                        VhsText(vhsSafe(h.title).uppercase().take(19))
                        VhsText(vhsSafe(h.sub).uppercase().take(19), color = o.fg.copy(alpha = 0.6f))
                    }
                }
            }
        }
        Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { Haptics.tick(); onExit() } }) {
            Column {
                VhsText("TAP SONG=PLAY  +=ADD", invert = true)
                VhsText("TAP HERE TO RETURN", invert = true)
            }
        }
    }
}
