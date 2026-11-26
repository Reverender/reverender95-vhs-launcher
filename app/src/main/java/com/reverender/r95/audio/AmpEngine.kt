package com.reverender.r95.audio

import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import com.reverender.r95.core.Catalog
import com.reverender.r95.core.Track
import com.reverender.r95.ui.viz.js.Dom
import com.reverender.r95.ui.viz.js.str

/**
 * VAPOR_AMP's player logic, ported from the website (rr-app-core.js: togglePlayPause, playCurrentAudio,
 * seekToRandomPosition, selectTrack, playRandomTrackAndOffset, switchToInternalDeck, connectRadioStream,
 * playCustomLocalFile, the capture toggles and the audioEl event handlers). Element texts keep the site's
 * ids so the UI and the translated visualizers read the same values.
 */
object AmpEngine : PlayerConnection.Events {
    private lateinit var p: PlayerConnection
    var activeSourceMode = "deck"; private set
    private var wasPaused = false
    private var pendingRandomSeekOnLoad = false
    private var useSynthFallback = false
    private var isPlaying = false
    private var currentCustomFileIdx = 0
    val currentFileIndex: Int get() = if (activeSourceMode == "files") currentCustomFileIdx else -1
    /** playlist order: shuffle, and repeat 0 = off (stop at the end), 1 = all (wrap around), 2 = the same song */
    var shuffle by androidx.compose.runtime.mutableStateOf(false)
    var repeat by androidx.compose.runtime.mutableIntStateOf(1)
    /** next / previous playlist song, following shuffle (repeat-one still steps when you press a button) */
    fun stepFile(dir: Int): Boolean {
        val n = customAudioFiles.size
        if (n == 0) return false
        val i = when {
            shuffle && n > 1 -> { var r: Int; do { r = (0 until n).random() } while (r == currentCustomFileIdx); r }
            else -> (currentCustomFileIdx + dir).mod(n)
        }
        playCustomLocalFile(i); return true
    }
    /** customAudioFiles: device files and imported Internet Archive MP3s. */
    class CustomFile(val name: String, val url: String, val archiveUrl: String? = null)
    val customAudioFiles = androidx.compose.runtime.mutableStateListOf<CustomFile>()
    /** Archive MP3 queued to play after the current track (site: S). */
    private var pendingArchiveIdx: Int? = null
    var randomSongOnResume = false

    private fun el(id: String) = Dom.el(id)
    fun text(id: String) = str(el(id).prop("textContent") ?: "")
    private fun set(id: String, t: String) = el(id).setProp("textContent", t)

    fun install(player: PlayerConnection) {
        p = player
        p.events = this
        // initial texts on the page
        set("lcd-time", "00:00 / 00:00"); set("lcd-buffer", "BUF: 0%"); set("btn-play-pause", "▶ PLAY")
        set("status-mode", "MODE: STANDBY"); set("status-jump", "LAST JUMP: --:--")
        set("src-status-active", "SOURCE: INTERNAL MASTER DECK"); set("src-status-dsp", "DSP & VIZ: LINKED")
        // selectTrack(currentTrackIndex, true, false) with a random index, like the site on load
        val i = (Math.random() * Catalog.deck.size).toInt()
        p.cueDeck(i); pendingRandomSeekOnLoad = true
        updateTrackListUI()
    }

    private fun fmt(sec: Double): String {
        if (!sec.isFinite() || sec < 0) return "00:00"
        val s = sec.toLong(); return "%02d:%02d".format(s / 60, s % 60)
    }

    fun updateTrackListUI() { set("lcd-track-name", "TRK %02d: %s".format(p.deckIndex + 1, Catalog.deck[p.deckIndex].title)) }

    /** timeupdate / progress (called from the UI clock). */
    fun tick() {
        val d = p.durationMs() / 1000.0
        if (activeSourceMode == "radio") return
        if (!useSynthFallback && d > 0) {
            val c = p.positionMs() / 1000.0
            set("lcd-time", "${fmt(c)} / ${fmt(d)}")
            set("pos-lbl-val", "${fmt(c)} (${"%.1f".format(c / d * 100)}%)")
        }
        updateBufferDisplay()
    }

    private fun updateBufferDisplay() {
        if (useSynthFallback) { set("lcd-buffer", "SYNTH GEN"); return }
        if (activeSourceMode == "radio" || activeSourceMode == "system" || activeSourceMode == "mic") return
        p.pollBuffer()
        set("lcd-buffer", if (p.durationMs() > 0) "BUF: ${p.bufferedPercent}%" else "BUF: ...")
    }

    fun seekToRandomPosition(label: String = "RANDOM JUMP") {
        val d = p.durationMs() / 1000.0
        if (!useSynthFallback && d > 2) {
            val a = d * (0.08 + Math.random() * 0.77)
            p.seekTo((a * 1000).toLong())
            set("status-jump", "JUMP: ${fmt(a)}")
            set("lcd-status", "$label -> @ ${fmt(a)}")
        } else if (useSynthFallback) {
            val a = Math.floor(15 + Math.random() * 165)
            set("status-jump", "JUMP: ${fmt(a)}")
            set("lcd-status", "$label -> @ ${fmt(a)}")
        }
    }

    fun selectTrack(i: Int, randomSeek: Boolean = true, play: Boolean = false) {
        p.cueDeck(i)
        pendingRandomSeekOnLoad = randomSeek
        set("lcd-buffer", "BUF: 0%")
        updateTrackListUI()
        if (play || isPlaying) playCurrentAudio("TRACK SWITCH JUMP")
    }

    private var playLabel = "PLAYING"
    fun playCurrentAudio(label: String = "PLAYING") {
        playLabel = label
        if (activeSourceMode != "deck") activeSourceMode = "deck"
        p.playDeck(p.deckIndex, randomOffset = false)
        isPlaying = true
    }

    fun togglePlayPause() {
        if (activeSourceMode == "mic" || activeSourceMode == "system") { stopLiveCapture(); return }
        if (!isPlaying) {
            if (wasPaused) {
                if (randomSongOnResume) { p.cueDeck((Math.random() * Catalog.deck.size).toInt()); updateTrackListUI() }
                pendingRandomSeekOnLoad = true
                if (!useSynthFallback && p.durationMs() > 0 && activeSourceMode == "deck" && !randomSongOnResume) {
                    seekToRandomPosition("RESUME DRIFT"); pendingRandomSeekOnLoad = false
                }
            }
            wasPaused = false
            when {
                activeSourceMode == "deck" && (randomSongOnResume || !p.connected || p.currentUrl() == null || !p.currentUrl()!!.endsWith(Catalog.deck[p.deckIndex].url.substringAfterLast('/'))) -> playCurrentAudio("RESUME DRIFT")
                else -> { p.play(); isPlaying = true; onPlaying() }
            }
        } else {
            p.pause()
            StaticNoise.stop(); useSynthFallback = false
            isPlaying = false
            wasPaused = true
            set("btn-play-pause", "▶ RESUME")
            set("lcd-status", "PAUSED (RESUME WILL JUMP TO NEW POS)")
            updateTrackListUI()
        }
    }

    fun jumpRandomPosition() = seekToRandomPosition("MANUAL SCRUB")

    /** pick a random deck song without starting it (VHS ◀◀ ▶▶ while stopped); play it with [playCurrentAudio] */
    fun cueRandomTrack(): String {
        var e = (Math.random() * Catalog.deck.size).toInt()
        if (e == p.deckIndex) e = (e + 1) % Catalog.deck.size
        isPlaying = false; wasPaused = false
        activeSourceMode = "deck"
        selectTrack(e, true, false)
        return Catalog.deck[e].title
    }
    /** move the playlist cursor without starting the song (VHS ◀◀ ▶▶ while stopped); returns its name */
    fun cueFileStep(dir: Int): String? {
        val n = customAudioFiles.size
        if (n == 0) return null
        currentCustomFileIdx = if (shuffle && n > 1) { var r: Int; do { r = (0 until n).random() } while (r == currentCustomFileIdx); r } else (currentCustomFileIdx + dir).mod(n)
        return customAudioFiles[currentCustomFileIdx].name
    }
    fun playCuedFile() = playCustomLocalFile(currentCustomFileIdx)

    fun playRandomTrackAndOffset() {
        var e = (Math.random() * Catalog.deck.size).toInt()
        if (e == p.deckIndex) e = (e + 1) % Catalog.deck.size
        isPlaying = true
        activeSourceMode = "deck"
        selectTrack(e, true, true)
    }

    fun switchToInternalDeck() {
        stopLiveCapture(silent = true)
        useSynthFallback = false
        isPlaying = true
        activeSourceMode = "deck"
        selectTrack(p.deckIndex, true, true)
        updateSourceModeUI("deck", "DECK: ${Catalog.deck[p.deckIndex].title}", "DSP & VIZ: LINKED")
    }

    fun updateSourceModeUI(mode: String, a: String, o: String = "DSP & VIZ: ACTIVE") {
        activeSourceMode = mode
        set("src-status-active", a); set("src-status-dsp", o)
    }

    // ------------------------------------------------------------ radio
    var radioName = ""; private set
    var pressedStation = -1; private set

    fun playInternetRadio(i: Int) {
        val s = Catalog.radioPresets.getOrNull(i) ?: return
        pressedStation = i
        if (s.url.startsWith("dir:")) { tuneFromDirectory(s.url.removePrefix("dir:"), s.name.substringAfter(' ')); return }
        el("custom-radio-url").setProp("value", s.url)
        connectRadioStream(s.url, s.name.substringAfter(' '))
    }

    private val dirScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.Main)
    private val dirFound = HashMap<String, List<String>>()
    private var dirJob: kotlinx.coroutines.Job? = null
    /**
     * A preset given as "dir:<search terms>|<stream>|<stream>…": hardcore streams move port or go dark often, so
     * the known addresses are tried first, then the radio directory's live stations for the terms. A stream
     * that has not started within 12 seconds is skipped for the next one.
     */
    private fun tuneFromDirectory(spec: String, name: String) {
        val parts = spec.split('|')
        val terms = parts[0]
        val fixed = parts.drop(1).filter { it.isNotBlank() }
        dirJob?.cancel()
        val me = pressedStation
        dirJob = dirScope.launch {
            val tried = HashSet<String>()
            suspend fun attempt(u: String): Boolean {
                if (!tried.add(u)) return false
                android.util.Log.i("r95", "radio: $name -> $u")
                el("custom-radio-url").setProp("value", u); connectRadioStream(u, name)
                for (k in 0 until 24) { kotlinx.coroutines.delay(500); if (pressedStation != me) return true; if (p.isPlaying) return true }
                return false
            }
            for (u in fixed) if (attempt(u)) return@launch
            set("lcd-status", "SCANNING THE DIAL…")
            val found = dirFound[terms] ?: run {
                val urls = LinkedHashSet<String>()
                for (t in terms.split(' ').filter { it.isNotBlank() }) {
                    val (rows, unavailable) = runCatching { kotlinx.coroutines.withTimeoutOrNull(9000) { com.reverender.r95.net.StationDirectory.search(t) } }.getOrNull()
                        ?: (emptyList<com.reverender.r95.net.StationDirectory.Row>() to true)
                    if (!unavailable) rows.filter { !it.url.contains("somafm") }.take(5).forEach { urls.add(it.url) }
                }
                urls.toList().also { if (it.isNotEmpty()) dirFound[terms] = it }
            }
            for (u in found) if (attempt(u)) return@launch
            if (pressedStation == me) set("lcd-status", "NO SIGNAL")
        }
    }

    fun playCustomStreamUrl() {
        val a = str(el("custom-radio-url").prop("value") ?: "").trim()
        if (a.isEmpty()) return
        pressedStation = -1
        connectRadioStream(a, "CUSTOM STREAM")
    }

    fun connectRadioStream(url: String, name: String) {
        stopLiveCapture(silent = true)
        StaticNoise.stop(); useSynthFallback = false
        radioName = name
        set("lcd-track-name", "RADIO: $name")
        set("lcd-status", "TUNING INTO $name...")
        updateSourceModeUI("radio", "RADIO: $name", "CONNECTING STREAM...")
        p.playStation(com.reverender.r95.core.Station(name, url, ""))
        isPlaying = true
    }

    // ------------------------------------------------------------ files / archive
    fun handleCustomFilesSelected(files: List<CustomFile>) {
        if (files.isEmpty()) return
        val start = customAudioFiles.size
        customAudioFiles.addAll(files)
        playCustomLocalFile(start)
    }

    fun playCustomLocalFile(i: Int) {
        val a = customAudioFiles.getOrNull(i) ?: return
        stopLiveCapture(silent = true)
        StaticNoise.stop(); useSynthFallback = false
        currentCustomFileIdx = i
        activeSourceMode = "files"
        isPlaying = true
        filePlaying = a
        if (a.url.startsWith("yt:")) {
            // a YouTube song from the music finder: get today's stream address, then play the sound only
            set("lcd-status", "LOADING…")
            dirScope.launch {
                val r = runCatching { com.reverender.r95.net.YtStreams.resolve(a.url.removePrefix("yt:")) }.getOrNull()
                if (currentCustomFileIdx != i || activeSourceMode != "files") return@launch
                val u = r?.audio ?: r?.video?.takeIf { !r.hls }
                if (u == null) { set("lcd-status", "SONG COULD NOT BE LOADED"); return@launch }
                p.playUrl(u, a.name, "LOCAL PLAYLIST")
            }
            return
        }
        p.playUrl(a.url, a.name, "LOCAL PLAYLIST")
    }
    private var filePlaying: CustomFile? = null

    fun removeCustomFileFromPlaylist(i: Int) {
        if (i !in customAudioFiles.indices) return
        val playingIt = activeSourceMode == "files" && i == currentCustomFileIdx
        customAudioFiles.removeAt(i)
        pendingArchiveIdx?.let { s -> pendingArchiveIdx = when { i < s -> s - 1; i == s -> if (customAudioFiles.isNotEmpty()) minOf(i, customAudioFiles.size - 1) else null; else -> s } }
        if (i < currentCustomFileIdx) currentCustomFileIdx--
        if (playingIt) {
            if (customAudioFiles.isNotEmpty()) playCustomLocalFile(minOf(i, customAudioFiles.size - 1))
            else {
                p.stop(); currentCustomFileIdx = 0; isPlaying = false; StaticNoise.stop()
                set("btn-play-pause", "▶ PLAY")
                set("lcd-status", "PLAYLIST EMPTY · CHOOSE A TRACK OR RETURN TO THE DECK")
                updateSourceModeUI("deck", "PLAYLIST EMPTY", "READY")
            }
        }
    }

    fun isCurrentFile(i: Int) = activeSourceMode == "files" && i == currentCustomFileIdx

    /** Site X(): import an Internet Archive MP3 (playNow = tap, otherwise hold = add / queue). */
    fun importArchive(title: String, name: String, url: String, playNow: Boolean) {
        var i = customAudioFiles.indexOfFirst { it.archiveUrl == url }
        val added = i < 0
        val busy = isPlaying && (activeSourceMode != "deck" || p.isPlaying)
        if (added) {
            i = customAudioFiles.size
            customAudioFiles.add(CustomFile("$title — $name", url, archiveUrl = url))
            set("src-status-active", "IMPORTED IA MP3: $name")
            if (!playNow) {
                val filesMode = activeSourceMode == "files"
                if (filesMode && busy) { } else if (busy) { if (pendingArchiveIdx == null) pendingArchiveIdx = i } else { pendingArchiveIdx = null; playCustomLocalFile(i) }
                set("lcd-status", if (busy) "ADDED TO PLAYLIST: $name · WILL PLAY AFTER THE CURRENT TRACK" else "ADDED AND PLAYING ARCHIVE MP3: $name")
            }
        }
        if (playNow) {
            pendingArchiveIdx = null
            playCustomLocalFile(i)
            set("lcd-status", "PLAYING ARCHIVE MP3 NOW: $name")
        } else if (!added) set("lcd-status", "ALREADY IN PLAYLIST: $name")
    }

    fun inPlaylist(url: String) = customAudioFiles.any { it.archiveUrl == url }

    // ------------------------------------------------------------ live inputs
    val liveActive: Boolean get() = MicCapture.running && (activeSourceMode == "mic" || activeSourceMode == "system")

    fun startedMic() {
        p.pause(); StaticNoise.stop()
        isPlaying = true
        set("btn-play-pause", "⏹️ LIVE")
        set("lcd-track-name", "INPUT: MIC / LINE-IN")
        set("lcd-status", "LIVE MIC / AUX AUDIO -> ROUTED TO EQ & DSP")
        set("lcd-buffer", "LIVE AUX")
        set("status-mode", "MIC / LINE-IN")
        updateSourceModeUI("mic", "SOURCE: LIVE MIC / AUX LINE-IN", "EQ + 12 DSP + VIZ ON")
    }
    fun micDenied() = updateSourceModeUI("deck", "MIC/AUX PERMISSION DENIED", "STANDBY")

    fun startedSystem() {
        p.pause(); StaticNoise.stop()
        isPlaying = true
        set("btn-play-pause", "⏹️ LIVE")
        set("lcd-track-name", "INPUT: PC/TAB LOOPBACK")
        set("lcd-status", "STREAMING LIVE PC / TAB AUDIO -> EQ & DSP FX")
        set("lcd-buffer", "LIVE PCM")
        set("status-mode", "PC/TAB AUDIO")
        updateSourceModeUI("system", "SOURCE: LIVE PC / TAB AUDIO", "EQ + 12 DSP + VIZ ON")
    }
    fun systemUnavailable() {
        updateSourceModeUI("deck", "SCREEN AUDIO CAPTURE UNAVAILABLE IN THIS MOBILE BROWSER", "USE FILES OR MIC/AUX · TRY CURRENT DESKTOP BROWSER")
        set("lcd-status", "SCREEN CAPTURE API NOT AVAILABLE HERE. USE FILES OR MIC/AUX.")
    }
    fun systemCanceled() = updateSourceModeUI("deck", "PC/TAB CAPTURE CANCELED", "STANDBY")

    /** stopLiveCaptureStream */
    var onStopCapture: (() -> Unit)? = null
    fun stopLiveCapture(silent: Boolean = false) {
        val was = activeSourceMode == "system" || activeSourceMode == "mic"
        if (MicCapture.running) { onStopCapture?.invoke(); MicCapture.stop() }
        if (was) {
            isPlaying = false
            set("btn-play-pause", "▶ PLAY")
            updateSourceModeUI("deck", "LIVE INPUT STOPPED", "STANDBY")
        }
    }

    // ------------------------------------------------------------ audioEl events
    override fun onLoadedMetadata() {
        updateBufferDisplay()
        if (pendingRandomSeekOnLoad && isPlaying && activeSourceMode == "deck") {
            seekToRandomPosition(playLabel.takeIf { it != "PLAYING" } ?: "BUFFER OFFSET JUMP")
            pendingRandomSeekOnLoad = false
        }
    }

    override fun onPlaying() {
        StaticNoise.stop(); useSynthFallback = false
        isPlaying = true; wasPaused = false
        when (activeSourceMode) {
            "deck" -> {
                set("btn-play-pause", "⏸ PAUSE")
                set("status-mode", "DECK: ${Catalog.deck[p.deckIndex].title}")
                updateTrackListUI(); updateBufferDisplay()
            }
            "radio" -> {
                set("btn-play-pause", "⏸ PAUSE")
                set("lcd-status", "ON AIR: $radioName [LIVE STREAM]")
                set("lcd-buffer", "LIVE FM")
                set("status-mode", "RADIO: $radioName")
                updateSourceModeUI("radio", "ON AIR: $radioName", "EQ + DSP + VIZ ACTIVE")
            }
            "files" -> {
                val a = filePlaying ?: return
                set("btn-play-pause", "⏸ PAUSE")
                set("lcd-track-name", "FILE: ${a.name.take(18)}")
                set("lcd-status", "PLAYING LOCAL DEVICE AUDIO: ${a.name}")
                set("status-mode", "LOCAL FILE")
                updateSourceModeUI("files", "FILE: ${a.name}", "EQ + 12 DSP + VIZ ON")
            }
        }
    }

    override fun onWaiting() {
        if (isPlaying && !wasPaused && activeSourceMode != "mic" && activeSourceMode != "system") {
            StaticNoise.volume = p.volume * 0.1f; StaticNoise.start(); useSynthFallback = true; updateBufferDisplay()
        }
    }

    override fun onEnded() {
        StaticNoise.stop(); useSynthFallback = false
        pendingArchiveIdx?.let { s -> pendingArchiveIdx = null; if (s in customAudioFiles.indices) { playCustomLocalFile(s); return } }
        if (activeSourceMode == "files" && customAudioFiles.isNotEmpty()) {
            when {
                repeat == 2 -> playCustomLocalFile(currentCustomFileIdx)
                shuffle -> stepFile(1)
                repeat == 0 && currentCustomFileIdx >= customAudioFiles.size - 1 -> { isPlaying = false; set("lcd-status", "END OF PLAYLIST") }
                else -> playCustomLocalFile((currentCustomFileIdx + 1) % customAudioFiles.size)
            }
            return
        }
        playRandomTrackAndOffset()
    }

    override fun onError() {
        when (activeSourceMode) {
            "radio" -> updateSourceModeUI("radio", "STREAM OFFLINE / BLOCKED", "TRY ANOTHER STATION")
            else -> if (isPlaying) { StaticNoise.volume = p.volume * 0.1f; StaticNoise.start(); useSynthFallback = true; set("lcd-status", "SYNTH FALLBACK (NO MP3)"); updateBufferDisplay() }
        }
    }

    // ------------------------------------------------------------ menu bar
    fun toggleCrt(on: Boolean) { set("lcd-status", if (on) "CRT SCANLINE FILTER: ON" else "CRT SCANLINE FILTER: OFF") }
}
