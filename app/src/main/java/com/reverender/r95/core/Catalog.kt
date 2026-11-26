package com.reverender.r95.core

/** Everything the site hard-codes (tracks, stations, EQ presets, app list), in one place. */
object Site {
    const val BASE_URL = "https://reverender.com/"
    const val USER_AGENT = "REVERENDER95-Android/0.1 (native)"
}

data class Track(val title: String, val url: String)

data class Station(val name: String, val url: String, val tag: String = "PRESET")

object Catalog {
    private val trackTitles = listOf(
        "MACINTOSH_ DREAMS", "HELIOS_PLAZA_95", "LATE_NIGHT_VHS", "CYBER_PALM_BREEZE", "NEON_TESTAROSSA",
        "TOKYO_PAGER_99", "POSEIDON_FOUNTAIN", "SEGA_SATURN_BOOT", "VIRTUAL_ATRIUM", "CRYSTAL_PEPSI_WAVE",
        "MALL_ESCALATOR", "DIALUP_SERENADE", "LASERDISC_SUNSET", "TURBO_OUTRUN_86", "WIN95_MAZE_SAVER",
        "SAKURA_CASSETTE", "ECHO_CHAMBER_FM", "HYPERCUBE_LOUNGE", "DELOREAN_OVERDRIVE", "VAPOR_WEATHER_CH",
        "ZERO_GRAVITY_SPA", "CHROME_DOLPHIN", "FLOPPY_DISK_SOUL", "MIDNIGHT_SIGN_OFF",
    )
    private val trackFiles = listOf(
        "334b409e7f3ff6e317a7.mp3", "dcc90d4f77cf74ed157a.mp3", "d72169f8b848a854a91a.mp3", "047090cc070c563a09f7.mp3",
        "257a0e5cbea5291dda5d.mp3", "f9bec7ad8716cc91fecc.mp3", "ed1f5db9f8ce70414b68.mp3", "a295194c6c52d84c0390.mp3",
        "219fcc14350c424a6336.mp3", "f1bd56e9b96918498f6f.mp3", "d80f4cc44efc5e311238.mp3", "626f3b69440f754e4583.mp3",
        "693ac6358099e8207773.mp3", "ef588969eb1caf9264f8.mp3", "dccbe3026c942dd45e1c.mp3", "f09c402e666685d099e3.mp3",
        "23ddcda2dea4c75a98de.mp3", "fd30f8e66751ee78ff4f.mp3", "c30fbc29b0d6724671ac.mp3", "c4950ba27a7a48b8c25e.mp3",
        "b12656ae800368a4fa73.mp3", "6316fe5bc9bb1a0f0f4d.mp3", "0bea12963c3f2d4b030d.mp3", "2311f66f99d515908466.mp3",
    )

    /** The 24-track deck, streamed from the site root exactly like the web player does. */
    val deck: List<Track> = trackTitles.zip(trackFiles) { t, f -> Track(t, Site.BASE_URL + f) }

    /** the radio stations on the VHS home row: the defaults until you add or remove one, then your own list */
    val radioPresets = androidx.compose.runtime.mutableStateListOf<Station>()
    fun loadStations(ctx: android.content.Context) {
        val saved = ctx.getSharedPreferences("radio_presets", android.content.Context.MODE_PRIVATE).getString("list", null)
        val list = saved?.let { runCatching {
            val a = org.json.JSONArray(it); List(a.length()) { i -> a.getJSONObject(i).let { o -> Station(o.getString("n"), o.getString("u")) } }
        }.getOrNull() } ?: defaultStations
        radioPresets.clear(); radioPresets.addAll(list)
    }
    private fun saveStations(ctx: android.content.Context) {
        val a = org.json.JSONArray(); radioPresets.forEach { a.put(org.json.JSONObject().put("n", it.name).put("u", it.url)) }
        ctx.getSharedPreferences("radio_presets", android.content.Context.MODE_PRIVATE).edit().putString("list", a.toString()).apply()
    }
    /** false if it was already there */
    fun addStation(ctx: android.content.Context, s: Station): Boolean {
        if (radioPresets.any { it.url == s.url }) return false
        radioPresets.add(s); saveStations(ctx); return true
    }
    fun removeStation(ctx: android.content.Context, i: Int): Station? {
        if (i !in radioPresets.indices || radioPresets.size <= 1) return null
        return radioPresets.removeAt(i).also { saveStations(ctx) }
    }
    fun insertStation(ctx: android.content.Context, i: Int, s: Station) { radioPresets.add(i.coerceIn(0, radioPresets.size), s); saveStations(ctx) }

    private val defaultStations: List<Station> = listOf(
        Station("🌴 Nightwave Plaza", "https://radio.plaza.one/mp3"),
        Station("🇬🇧 Smooth 80s London", "https://media-ssl.musicradio.com/Smooth80s"),
        Station("🇺🇸 US Jazz Station", "http://jazz-wr01.ice.infomaniak.ch/jazz-wr01-128.mp3"),
        Station("🇺🇸 US Country Classics", "https://ice2.somafm.com/bootliquor-128-mp3"),
        Station("🎷 Vaporjazz Radio", "https://stream.synthwaveradio.eu/listen/synthwaveradio.eu/radio.mp3"),
        Station("🇺🇸 181.fm The Buzz (80s)", "https://listen.181fm.com/181-buzz_128k.mp3"),
        Station("🌊 SomaFM Vaporwaves", "https://ice1.somafm.com/vaporwaves-128-mp3"),
        Station("👾 SomaFM DEF CON", "https://ice1.somafm.com/defcon-128-mp3"),
        // looked up live in the radio directory (fixed hardcore stream addresses come and go)
        Station("🔊 Hardcore Radio", "dir:gabber hardcore|http://liquiddoom.net:8000/hardcore"),
    )
    init { radioPresets.addAll(defaultStations) }

    val eqFrequencies: FloatArray = floatArrayOf(60f, 170f, 350f, 1000f, 3000f, 6000f, 12000f, 16000f)

    val eqPresets: LinkedHashMap<String, FloatArray> = linkedMapOf(
        "FLAT" to floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
        "VAPOR" to floatArrayOf(6f, 4.5f, 1f, -2.5f, -1f, 2.5f, 5f, 6.5f),
        "BASS" to floatArrayOf(9.5f, 7.5f, 3.5f, 0f, -1.5f, 0f, 1.5f, 2.5f),
        "RADIO" to floatArrayOf(-10f, -6f, 3f, 8f, 6f, -3f, -9f, -12f),
        "CYBER" to floatArrayOf(7f, 3f, -4f, -2f, 4f, 7.5f, 9f, 8f),
    )
}

enum class AppGroup(val label: String, val icon: String, val inStartMenu: Boolean = true) {
    PROGRAMS("Programs", "📁"),
    GAMES("Games", "🎮"),
    OFFICE("Office Plaza", "🏬"),
    STUDIO("Audio & Studio", "🎛️"),
    VISUALS("Visualizers", "👁️"),
    SYSTEM("System", "⚙️", inStartMenu = false),
}

/** One entry per window on the website, all rebuilt natively. Order = the site's Start menu order. */
/** App id -> the website's window id (for its phone layout, controls and canvases). */
val SITE_WINDOW = mapOf(
    "player" to "win-player", "revy" to "win-revy-chat", "clock" to "win-clock-themes", "vectordreams" to "win-game-vectordreams",
    "emulator" to "win-game-emulator", "video" to "win-video-streams",
)
fun siteWindowId(appId: String) = SITE_WINDOW[appId] ?: "win-$appId"
/** Windows that carry the site's visualizer FX button (they host a .viz-canvas). */
val VIZ_FX_WINDOWS = setOf("cassette", "cybergrid", "eq", "fractal", "fx", "hypercube", "kaleido", "lissajous", "magnetic", "matrix", "orbital", "plasma", "prism", "ribbon", "sonar", "starforge", "tunnel", "vumeter", "waterfall")

data class AppInfo(
    val id: String,
    val title: String,
    val icon: String,
    val group: AppGroup,
    val desc: String,
    val defaultW: Int = 360,
    val defaultH: Int = 460,
    /** Title-bar text, copied from the website's window. */
    val windowTitle: String = title,
    val onDesktop: Boolean = true,
)

object Apps {
    val all: List<AppInfo> = listOf(
        // Programs
        AppInfo("player", "VAPOR_AMP 95", "📼", AppGroup.PROGRAMS, "VAPOR_AMP 95 — play music and control your listening setup.", 380, 680, "VAPOR_AMP 95 — [MASTER DECK + RADIO]"),
        AppInfo("music", "C:\\MUSIC", "💾", AppGroup.PROGRAMS, "C:\\MUSIC — every song on this phone in an Explorer tree; play a folder in VAPOR_AMP.", 420, 520, "💾 EXPLORER.EXE — [C:\\MUSIC]"),
        AppInfo("monitor", "CRT_MONITOR_95", "📺", AppGroup.PROGRAMS, "CRT_MONITOR_95 — worlds and visualizers full screen on a TV over USB-C/HDMI; the phone is the rack.", 380, 360, "📺 CRT_MONITOR_95.EXE — [Second Screen]"),
        AppInfo("browser", "NET_SURF_95", "🌐", AppGroup.PROGRAMS, "NET_SURF_95 — browse the web in a retro reader.", 400, 560, "🌐 NET_SURF_95.EXE — [World Wide Web Navigator]"),
        AppInfo("video", "VIDEO_STREAM_95", "🎬", AppGroup.PROGRAMS, "VIDEO_STREAM_95 — stream Internet Archive films and local videos.", 400, 600, "🎬 VIDEO_STREAM_95 // INTERNET ARCHIVE + YOUTUBE PLAYER"),
        // Games
        AppInfo("vectordreams", "VECTOR DREAMS // 1989", "🔺", AppGroup.GAMES, "VECTOR DREAMS // 1989 — build a neon polygon city and harvest vector energy.", 400, 640, "🔺 GAMES — [VECTOR DREAMS // 1989]"),
        AppInfo("emulator", "RETRO_EMU_95", "👾", AppGroup.GAMES, "RETRO_EMU_95 — play NES, SNES, Game Boy, GBA and N64 games.", 400, 600, "👾 GAMES — [RETRO_EMU_95 // NES·SNES·N64·GB·GBC·GBA]"),
        AppInfo("emutouch", "RETRO_EMU_95 // TOUCH PAD", "🕹️", AppGroup.SYSTEM, "RETRO_EMU_95 touch controls.", 390, 285, "🕹️ RETRO_EMU_95 // TOUCH PAD", onDesktop = false),
        AppInfo("vaporpet", "VAPORPET_95", "🐱", AppGroup.GAMES, "VAPORPET_95 — look after a tiny pixel cat.", 340, 580, "🐱 VAPORPET_95.EXE — [Pocket Pixel Cat]"),
        // Office Plaza
        AppInfo("sticky", "STICKY_NOTES_95", "📝", AppGroup.OFFICE, "STICKY_NOTES_95 — colourful notes that stay on this device.", 300, 360, "📝 STICKY_NOTES_95.EXE"),
        AppInfo("notepad", "WRITE_95", "🗒️", AppGroup.OFFICE, "WRITE_95 — write and save text files.", 360, 460, "🗒️ WRITE_95.EXE — [Untitled]"),
        AppInfo("calc", "CALC_95", "🧮", AppGroup.OFFICE, "CALC_95 — a classic calculator.", 280, 400, "🧮 CALC_95.EXE"),
        AppInfo("planner", "PLANNER_95", "📅", AppGroup.OFFICE, "PLANNER_95 — a calendar with notes for each day.", 360, 540, "📅 PLANNER_95.EXE — [Calendar]"),
        AppInfo("todo", "TASKS_95", "✅", AppGroup.OFFICE, "TASKS_95 — a simple to-do list.", 340, 460, "✅ TASKS_95.EXE — [To-Do]"),
        AppInfo("focus", "FOCUS_95", "⏳", AppGroup.OFFICE, "FOCUS_95 — a focus / break work timer.", 300, 460, "⏳ FOCUS_95.EXE — [Work Timer]"),
        // Audio & Studio
        AppInfo("drum909", "TR_909_95", "🥁", AppGroup.STUDIO, "TR_909_95 — program beats on a 909-style drum machine.", 400, 660, "🥁 TR_909_95.EXE — [Vaporwave Rhythm Composer & Loop Sequencer]"),
        AppInfo("synth95", "VAPOR_SYNTH_95", "🎹", AppGroup.STUDIO, "VAPOR_SYNTH_95 — play a polyphonic touch synth with arp.", 400, 660, "🎹 VAPOR_SYNTH_95.EXE — [Polyphonic Touch Synthesizer & Arp]"),
        AppInfo("cassette", "CASSETTE_95", "📻", AppGroup.STUDIO, "CASSETTE_95 — use the cassette-inspired audio player.", 380, 560, "📻 CASSETTE_95.EXE — [3D Hi-Fi Tape Transport & Analog Deck]"),
        AppInfo("fx", "DSP_FX_95", "⚡", AppGroup.STUDIO, "DSP_FX_95 — shape sound with effects and audio presets.", 380, 600, "⚡ DSP_FX_95.EXE — [18-Unit Cyber Studio Rack]"),
        AppInfo("eq", "GRAPHIC_EQ", "🎚️", AppGroup.STUDIO, "GRAPHIC_EQ — adjust the sound frequency balance.", 360, 460, "🎚️ GRAPHIC_EQ.EXE — [3D Iso-Spectrum & 8-Band Deck]"),
        // Visualizers (site order)
        AppInfo("hypercube", "HYPER_3D", "🌐", AppGroup.VISUALS, "HYPER_3D — watch a reactive 3D visualizer.", windowTitle = "🌐 HYPER_3D.EXE — [Depth Mesh]"),
        AppInfo("waterfall", "WATERFALL", "📊", AppGroup.VISUALS, "WATERFALL — view flowing audio-reactive frequency patterns.", windowTitle = "📊 WATERFALL.EXE — [3D Ridge FFT]"),
        AppInfo("tunnel", "NEON_WORM", "🌀", AppGroup.VISUALS, "NEON_WORM — travel through a flowing neon tunnel.", windowTitle = "🌀 NEON_WORM.EXE — [3D Spline Mesh]"),
        AppInfo("plasma", "PLASMA_EQ", "⚛️", AppGroup.VISUALS, "PLASMA_EQ — watch a colourful, music-reactive plasma display.", windowTitle = "⚛️ PLASMA_EQ.EXE — [3D Gyro Core]"),
        AppInfo("lissajous", "LISSAJOUS", "📈", AppGroup.VISUALS, "LISSAJOUS — explore audio-reactive geometric curves.", windowTitle = "📈 LISSAJOUS.EXE — [3D Isometric EQ & Phase Sphere]"),
        AppInfo("cybergrid", "CANYON_3D", "⛰️", AppGroup.VISUALS, "CANYON_3D — fly over a terrain made of the spectrum.", windowTitle = "⛰️ CANYON_3D.EXE — [Z-Buffer Synth Valley]"),
        AppInfo("matrix", "HEX_RAIN", "📟", AppGroup.VISUALS, "HEX_RAIN — falling hex code that rides the music.", windowTitle = "📟 HEX_RAIN.EXE — [3D Parallax Kana & City FFT]"),
        AppInfo("ribbon", "RIBBON_3D", "🎗️", AppGroup.VISUALS, "RIBBON_3D — a twisting ribbon driven by the waveform.", windowTitle = "🎗️ RIBBON_3D.EXE — [3D Quad Wave Surface]"),
        AppInfo("vumeter", "ANALOG_VU", "🎛️", AppGroup.VISUALS, "ANALOG_VU — twin needle VU meters.", 360, 320, "🎛️ ANALOG_VU.EXE — [Backlit 3D Studio Deck]"),
        AppInfo("orbital", "ORBITAL_ATLAS", "🪐", AppGroup.VISUALS, "ORBITAL_ATLAS — planets orbiting to the beat.", windowTitle = "🪐 ORBITAL_ATLAS.EXE — [Celestial Mechanics]"),
        AppInfo("fractal", "FRACTAL_BLOOM", "🌿", AppGroup.VISUALS, "FRACTAL_BLOOM — a fractal tree that blooms with the music.", windowTitle = "🌿 FRACTAL_BLOOM.EXE — [Recursive Garden]"),
        AppInfo("prism", "PRISMATIC_GLASS", "🔺", AppGroup.VISUALS, "PRISMATIC_GLASS — refracting prisms of light.", windowTitle = "🔺 PRISMATIC_GLASS.EXE — [Spectral Refraction]"),
        AppInfo("sonar", "SONAR_DEPTH", "📡", AppGroup.VISUALS, "SONAR_DEPTH — a sonar sweep that pings on every beat.", windowTitle = "📡 SONAR_DEPTH.EXE — [Abyssal Scanner]"),
        AppInfo("magnetic", "MAGNETIC_FIELD", "🧲", AppGroup.VISUALS, "MAGNETIC_FIELD — field lines bending around the bass.", windowTitle = "🧲 MAGNETIC_FIELD.EXE — [Flux Topology]"),
        AppInfo("kaleido", "KALEIDO_ENGINE", "💠", AppGroup.VISUALS, "KALEIDO_ENGINE — a mirrored kaleidoscope of the spectrum.", windowTitle = "💠 KALEIDO_ENGINE.EXE — [Radial Optics]"),
        AppInfo("starforge", "STARFORGE", "☄️", AppGroup.VISUALS, "STARFORGE — a galaxy of particles forged by the beat.", windowTitle = "☄️ STARFORGE.EXE — [Deep-Space Flight]"),
        // Not in the Start submenus on the site: opened from the tray, the clock, or the bottom of the Start menu
        AppInfo("settings", "Settings", "⚙️", AppGroup.SYSTEM, "Settings — change desktop and interface preferences.", 360, 620, "⚙️ SETTINGS.EXE — [Control Panel]"),
        AppInfo("customize", "Customize", "🎨", AppGroup.SYSTEM, "Customize — wallpapers, colours and depth effects.", 380, 620, "🎨 CUSTOMIZE_95.EXE — [Wallpapers, Colors & Depth FX]"),
        AppInfo("revy", "REVY.EXE", "🟪", AppGroup.SYSTEM, "REVY — ask questions; Revy searches the web and sums it up.", 360, 540, "🟪 REVY.EXE — AI DESKTOP COMPANION"),
        AppInfo("clock", "CLOCK_GADGET", "🕒", AppGroup.SYSTEM, "CLOCK_GADGET — pick a face for the desktop clock.", 340, 480, "🕒 CLOCK_GADGET.EXE — [Clock Themes]"),
        AppInfo("help", "Help and Support", "❓", AppGroup.SYSTEM, "Help and Support Center — what every window does and how to use it.", 640, 480, "❓ HELP_95.EXE — [Help and Support Center]", onDesktop = false),
    )

    fun byId(id: String): AppInfo? = all.firstOrNull { it.id == id }
}
