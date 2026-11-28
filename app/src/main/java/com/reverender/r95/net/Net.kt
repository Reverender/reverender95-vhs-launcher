package com.reverender.r95.net

import com.reverender.r95.core.Site
import com.reverender.r95.core.Station
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.math.min
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class RevySource(val title: String, val url: String)
data class RevyReply(val text: String, val sources: List<RevySource>)
data class ChatTurn(val role: String, val content: String)

private fun open(url: String, timeoutMs: Int): HttpURLConnection =
    (URL(url).openConnection() as HttpURLConnection).apply {
        connectTimeout = 12_000
        readTimeout = timeoutMs
        setRequestProperty("User-Agent", Site.USER_AGENT)
        setRequestProperty("Accept", "application/json")
    }

private fun HttpURLConnection.readBody(): String {
    val stream = if (responseCode in 200..299) inputStream else (errorStream ?: throw IOException("HTTP $responseCode"))
    return stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
}

/** Talks to the site's existing revy.php: same JSON the web chat sends. */
object RevyClient {
    suspend fun ask(message: String, history: List<ChatTurn>): RevyReply = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("message", message)
            // The website sends only the user's earlier questions (last 4) as context.
            val ctx = JSONArray()
            history.filter { it.role == "user" }.takeLast(4).forEach {
                ctx.put(JSONObject().put("role", it.role).put("content", it.content))
            }
            put("context", ctx)
        }.toString()

        val conn = open(Site.BASE_URL + "revy.php", 40_000).apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }
        try {
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val json = JSONObject(conn.readBody())
            if (!json.optBoolean("ok")) {
                throw IOException(json.optString("error").ifBlank { "Revy could not answer that." })
            }
            val sources = buildList {
                val arr = json.optJSONArray("sources") ?: JSONArray()
                for (i in 0 until arr.length()) {
                    val s = arr.optJSONObject(i) ?: continue
                    val url = s.optString("url")
                    if (url.startsWith("http")) add(RevySource(s.optString("title").ifBlank { url }, url))
                }
            }
            RevyReply(json.optString("reply"), sources)
        } finally {
            conn.disconnect()
        }
    }
}

/** radio-browser.info station search, with the same mirrors the website falls back through. */
object RadioBrowser {
    private val mirrors = listOf(
        "https://de1.api.radio-browser.info",
        "https://nl1.api.radio-browser.info",
        "https://at1.api.radio-browser.info",
        "https://all.api.radio-browser.info",
    )

    suspend fun search(query: String): List<Station> = withContext(Dispatchers.IO) {
        val q = URLEncoder.encode(query.trim(), "UTF-8")
        var lastError: Exception? = null
        for (m in mirrors) {
            try {
                val conn = open("$m/json/stations/search?limit=40&hidebroken=true&order=clickcount&reverse=true&name=$q", 10_000)
                val arr = try { JSONArray(conn.readBody()) } finally { conn.disconnect() }
                return@withContext buildList {
                    for (i in 0 until arr.length()) {
                        val o = arr.optJSONObject(i) ?: continue
                        val url = o.optString("url_resolved").ifBlank { o.optString("url") }
                        if (!url.startsWith("http")) continue
                        val cc = o.optString("countrycode")
                        val codec = o.optString("codec")
                        add(Station(o.optString("name").trim().ifBlank { url }, url, listOf(cc, codec).filter { it.isNotBlank() }.joinToString(" · ")))
                    }
                }.distinctBy { it.url }
            } catch (e: Exception) {
                lastError = e
            }
        }
        throw lastError ?: IOException("No radio directory reachable")
    }
}

// ------------------------------------------------------------------ Internet Archive

data class ArchiveItem(val id: String, val title: String, val creator: String)
data class ArchiveFile(val name: String, val size: Long, val format: String)

/**
 * Google Play edition: archive.org searches only find free content — items under a Creative Commons or
 * public-domain licence, or in the archive's public-domain collections. (Looking up collections by name is
 * left as it is; the items found in them are filtered.)
 */
object ArchiveFree {
    private const val FREE = "(licenseurl:*creativecommons.org* OR licenseurl:*publicdomain* OR " +
        "collection:(prelinger OR feature_films OR classic_tv OR classic_cartoons OR silent_films OR " +
        "publicdomainmovies OR pd_films OR netlabels OR oldtimeradio OR audio_bookspoetry OR librivoxaudio))"
    fun q(q: String): String =
        if (!com.reverender.r95.BuildConfig.PLAY || q.trimStart().startsWith("mediatype:collection")) q else "($q) AND $FREE"
}

/** archive.org search + file listing. Native apps have no CORS limits, so no relay is needed. */
object ArchiveClient {
    suspend fun search(query: String, mediatype: String, rows: Int = 50): List<ArchiveItem> = withContext(Dispatchers.IO) {
        val q = ArchiveFree.q("($mediatype) AND (${query.trim()})")
        val url = "https://archive.org/advancedsearch.php?q=" + URLEncoder.encode(q, "UTF-8") +
            "&fl%5B%5D=identifier&fl%5B%5D=title&fl%5B%5D=creator&rows=$rows&page=1&output=json"
        val conn = open(url, 20_000)
        val json = try { JSONObject(conn.readBody()) } finally { conn.disconnect() }
        val docs = json.optJSONObject("response")?.optJSONArray("docs") ?: JSONArray()
        buildList {
            for (i in 0 until docs.length()) {
                val d = docs.optJSONObject(i) ?: continue
                val creator = d.opt("creator").let { if (it is JSONArray) it.optString(0) else it?.toString().orEmpty() }
                add(ArchiveItem(d.optString("identifier"), d.optString("title").ifBlank { d.optString("identifier") }, creator))
            }
        }
    }

    suspend fun files(id: String): List<ArchiveFile> = withContext(Dispatchers.IO) {
        val conn = open("https://archive.org/metadata/" + URLEncoder.encode(id, "UTF-8"), 20_000)
        val json = try { JSONObject(conn.readBody()) } finally { conn.disconnect() }
        val arr = json.optJSONArray("files") ?: JSONArray()
        buildList {
            for (i in 0 until arr.length()) {
                val f = arr.optJSONObject(i) ?: continue
                add(ArchiveFile(f.optString("name"), f.optString("size").toLongOrNull() ?: 0L, f.optString("format")))
            }
        }
    }

    fun fileUrl(id: String, name: String) = "https://archive.org/download/" + URLEncoder.encode(id, "UTF-8") + "/" +
        name.split("/").joinToString("/") { URLEncoder.encode(it, "UTF-8").replace("+", "%20") }
}

// ------------------------------------------------------------------ YouTube (via the site's keyless relay)

data class YouTubeItem(val videoId: String, val title: String, val snippet: String, val thumbnail: String)

object YouTubeClient {
    suspend fun search(query: String, batch: Int = 0): List<YouTubeItem> = withContext(Dispatchers.IO) {
        val conn = open(Site.BASE_URL + "youtube-search.php", 30_000).apply {
            requestMethod = "POST"; doOutput = true; setRequestProperty("Content-Type", "application/json")
        }
        try {
            conn.outputStream.use { it.write(JSONObject().put("query", query).put("batch", batch).toString().toByteArray()) }
            val json = JSONObject(conn.readBody())
            if (!json.optBoolean("ok")) throw IOException(json.optString("error").ifBlank { "YouTube search failed" })
            val arr = json.optJSONArray("items") ?: JSONArray()
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.optJSONObject(i) ?: continue
                    add(YouTubeItem(o.optString("videoId"), o.optString("title"), o.optString("snippet"), o.optString("thumbnail")))
                }
            }
        } finally { conn.disconnect() }
    }
}

// ------------------------------------------------------------------ subtitles (site relay)

data class SubtitleItem(val id: String, val source: String, val title: String, val lang: String)
data class SubtitleFile(val name: String, val format: String, val text: String)

object SubtitleClient {
    suspend fun search(query: String, lang: String): List<SubtitleItem> = withContext(Dispatchers.IO) {
        val conn = open(Site.BASE_URL + "subtitle-search.php?action=search&q=" + URLEncoder.encode(query, "UTF-8") + "&lang=" + lang, 30_000)
        val json = try { JSONObject(conn.readBody()) } finally { conn.disconnect() }
        if (!json.optBoolean("ok")) throw IOException(json.optString("error").ifBlank { "Subtitle search failed" })
        val arr = json.optJSONArray("items") ?: JSONArray()
        buildList {
            for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                add(SubtitleItem(o.optString("id"), o.optString("source"), o.optString("release").ifBlank { o.optString("title") }, o.optString("lang")))
            }
        }
    }

    suspend fun get(id: String): SubtitleFile = withContext(Dispatchers.IO) {
        val conn = open(Site.BASE_URL + "subtitle-search.php?action=get&id=" + URLEncoder.encode(id, "UTF-8"), 30_000)
        val json = try { JSONObject(conn.readBody()) } finally { conn.disconnect() }
        if (!json.optBoolean("ok")) throw IOException(json.optString("error").ifBlank { "Could not download subtitles" })
        SubtitleFile(json.optString("name"), json.optString("format", "srt"), json.optString("text"))
    }
}

// ------------------------------------------------------------------ NET_SURF_95 (site relay: search + reader mode)

data class WebResult(val title: String, val url: String, val snippet: String)

object NetSurfClient {
    suspend fun search(q: String): List<WebResult> = withContext(Dispatchers.IO) {
        val conn = open(Site.BASE_URL + "netsurf-reader.php?action=search&q=" + URLEncoder.encode(q, "UTF-8"), 30_000)
        val json = try { JSONObject(conn.readBody()) } finally { conn.disconnect() }
        if (!json.optBoolean("ok")) throw IOException(json.optString("error").ifBlank { "Search failed" })
        val arr = json.optJSONArray("results") ?: JSONArray()
        buildList {
            for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                add(WebResult(o.optString("title"), o.optString("url"), o.optString("snippet")))
            }
        }
    }

    /** Readable text (markdown) of a page, produced by the site's reader relay. */
    suspend fun read(url: String): String = withContext(Dispatchers.IO) {
        val conn = open(Site.BASE_URL + "netsurf-reader.php?action=read&url=" + URLEncoder.encode(url, "UTF-8"), 40_000)
        val json = try { JSONObject(conn.readBody()) } finally { conn.disconnect() }
        if (!json.optBoolean("ok")) throw IOException(json.optString("error").ifBlank { "Page could not be loaded" })
        json.optString("content")
    }
}

/** Plain download to a file (ROMs, emulator cores). Reports progress 0..1. */
object Downloader {
    suspend fun download(url: String, dest: java.io.File, progress: (Float) -> Unit = {}): java.io.File = withContext(Dispatchers.IO) {
        var u = url
        var conn: HttpURLConnection
        var redirects = 0
        while (true) {
            conn = open(u, 60_000)
            conn.instanceFollowRedirects = false
            val code = conn.responseCode
            if (code in 300..399 && redirects < 6) { u = java.net.URL(java.net.URL(u), conn.getHeaderField("Location")).toString(); conn.disconnect(); redirects++; continue }
            if (code !in 200..299) { conn.disconnect(); throw IOException("HTTP $code") }
            break
        }
        val total = conn.getHeaderField("Content-Length")?.toLongOrNull() ?: -1L
        // a login / error page instead of the file (restricted Internet Archive items answer 200 with HTML)
        if ((conn.contentType ?: "").startsWith("text/html", true)) { conn.disconnect(); throw IOException("This file needs a login on the Internet Archive") }
        dest.parentFile?.mkdirs()
        val main = android.os.Handler(android.os.Looper.getMainLooper())
        var lastPct = -1
        val tmp = java.io.File(dest.path + ".part")
        try {
            conn.inputStream.use { input ->
                tmp.outputStream().use { out ->
                    val buf = ByteArray(64 * 1024); var done = 0L
                    while (true) {
                        val n = input.read(buf); if (n < 0) break
                        out.write(buf, 0, n); done += n
                        if (total > 0) { val pct = (done * 100 / total).toInt(); if (pct != lastPct) { lastPct = pct; val f = done.toFloat() / total; main.post { progress(f) } } }
                    }
                }
            }
        } finally { conn.disconnect() }
        if (tmp.length() == 0L) { tmp.delete(); throw IOException("The download was empty") }
        if (!tmp.renameTo(dest)) { tmp.copyTo(dest, overwrite = true); tmp.delete() }
        dest
    }
}

// ------------------------------------------------------------------ VIDEO_STREAM_95 deep Archive search (rr-video-stream zt())

/** A playable Archive video file found by [ArchiveVideoSearch]. */
data class ArchiveVideo(val title: String, val identifier: String, val name: String, val path: String, val ext: String, val size: Long, val matchScore: Int, val url: String, val subtitles: List<String>)

object ArchiveVideoSearch {
    private val EXT = setOf("mp4", "m4v", "webm", "ogv", "ogg", "mov")
    private const val MIN = 100L * 1024 * 1024
    class Q(val original: String, val title: String, val normalized: String, val tokens: List<String>)
    class Plan(val query: String, val label: String)

    /** Ae() */
    fun norm(e: String) = java.text.Normalizer.normalize(e, java.text.Normalizer.Form.NFKD).replace(Regex("[\\u0300-\\u036f]"), "")
        .lowercase().replace(Regex("[^a-z0-9]+"), " ").trim().replace(Regex("\\s+"), " ")
    private fun toks(e: String) = norm(e).split(" ").filter { it.isNotEmpty() }
    private fun ext(e: String) = Regex("\\.([a-z0-9]+)$", RegexOption.IGNORE_CASE).find(e)?.groupValues?.get(1)?.lowercase() ?: ""

    /** Bt() */
    fun parse(e: String): Q {
        val t = e.trim()
        val o = t.replace(Regex("\\b(?:videos?|movies?|films?|streams?)\\b", RegexOption.IGNORE_CASE), " ").trim()
        val n = norm(o)
        return Q(t, o, n, toks(n).distinct().filter { it.length > 1 }.take(10))
    }
    /** Ft() */
    fun plans(e: Q): Triple<List<Plan>, String, String> {
        val t = e.normalized.replace("\\", "\\\\").replace("\"", "\\\"")
        val meta = { a: String -> "(title:$a* OR description:$a* OR subject:$a*)" }
        val o = e.tokens.joinToString(" AND ") { "title:$it*" }
        val n = e.tokens.joinToString(" AND ") { meta(it) }
        val r = ArrayList<Plan>()
        fun c(a: String, s: String) { if (a.isNotEmpty() && r.none { it.query == "mediatype:movies AND ($a)" }) r.add(Plan("mediatype:movies AND ($a)", s)) }
        if (t.isNotEmpty()) c("title:\"$t\"", "exact title")
        if (o.isNotEmpty()) c(o, "title terms")
        if (n.isNotEmpty()) c(n, "item metadata")
        if (e.tokens.size in 2..6) for (a in 0 until e.tokens.size - 1) {
            val s = e.tokens.toMutableList(); val j = s[a] + s[a + 1]; s.removeAt(a); s.removeAt(a); s.add(a, j)
            c(s.joinToString(" AND ") { "title:$it*" }, "joined-word title")
            c(s.joinToString(" AND ") { meta(it) }, "joined-word metadata")
        }
        return Triple(r, o, n)
    }

    private val cache = object : LinkedHashMap<String, Pair<Long, List<JSONObject>>>() { override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Pair<Long, List<JSONObject>>>?) = size > 120 }
    /** _e(q, 1): one page of 100 docs (cached 10 min) */
    private fun docs(q: String): List<JSONObject> {
        synchronized(cache) { cache[q]?.let { if (System.currentTimeMillis() - it.first < 600_000) return it.second } }
        val url = "https://archive.org/advancedsearch.php?q=" + URLEncoder.encode(ArchiveFree.q(q), "UTF-8") + "&rows=100&page=1&output=json" +
            listOf("identifier", "title", "mediatype", "collection", "description", "subject").joinToString("") { "&fl%5B%5D=$it" }
        val conn = open(url, 25_000)
        val code = conn.responseCode
        if (code !in 200..299) { conn.disconnect(); throw IOException("Internet Archive search failed ($code).") }
        val json = try { JSONObject(conn.readBody()) } finally { conn.disconnect() }
        val arr = json.optJSONObject("response")?.optJSONArray("docs") ?: JSONArray()
        val out = (0 until arr.length()).mapNotNull { arr.optJSONObject(it) }
        synchronized(cache) { cache[q] = System.currentTimeMillis() to out }
        return out
    }
    private fun strs(v: Any?): List<String> = when (v) { is JSONArray -> (0 until v.length()).map { v.optString(it) }; null, JSONObject.NULL -> emptyList(); else -> listOf(v.toString()) }
    private fun joined(v: Any?) = strs(v).joinToString(" ")
    /** st() */
    private fun collections(d: JSONObject) = strs(d.opt("collection")).map { it.trim() }.filter { Regex("^[a-z0-9._-]{2,120}$", RegexOption.IGNORE_CASE).matches(it) }
    /** ct(): unique by identifier, sorted by title-token score */
    private fun rank(e: List<JSONObject>, t: Q): List<JSONObject> {
        val o = LinkedHashMap<String, Pair<JSONObject, Int>>()
        e.forEach { n ->
            val id = n.optString("identifier"); if (id.isEmpty()) return@forEach
            val c = toks(joined(n.opt("title")))
            val a = t.tokens.count { it in c }
            val s = o[id]; if (s == null || a > s.second) o[id] = n to a
        }
        return o.values.sortedByDescending { it.second }.map { it.first }
    }
    /** ut(): pick the best ≥100 MB file of one item and score it */
    private fun item(e: JSONObject, o: Q): ArchiveVideo? {
        val id = e.optString("identifier")
        val conn = open("https://archive.org/metadata/" + URLEncoder.encode(id, "UTF-8"), 20_000)
        val json = try { JSONObject(conn.readBody()) } finally { conn.disconnect() }
        val arr = json.optJSONArray("files") ?: return null
        val files = (0 until arr.length()).mapNotNull { arr.optJSONObject(it) }
        if (files.isEmpty()) return null
        // Qt()
        val best = files.filter { t ->
            val nm = t.optString("name"); val size = t.optString("size").toDoubleOrNull() ?: 0.0
            val priv = t.opt("private").let { it == true || it == "true" }
            nm.isNotEmpty() && size >= MIN && ext(nm) in EXT && !priv && !Regex("(?:thumb|thumbnail|sample|preview|spectrogram|poster)", RegexOption.IGNORE_CASE).containsMatchIn(nm)
        }.map { t ->
            val x = ext(t.optString("name")); val fmt = t.optString("format").lowercase()
            var r = mapOf("mp4" to 100, "m4v" to 92, "webm" to 82, "ogv" to 70, "ogg" to 65, "mov" to 55)[x] ?: 0
            if (Regex("512\\s?kb|512kb|stream", RegexOption.IGNORE_CASE).containsMatchIn(fmt + " " + t.optString("name"))) r += 25
            if (Regex("mpeg.?4|h\\.?264|avc", RegexOption.IGNORE_CASE).containsMatchIn(fmt)) r += 18
            if (Regex("original", RegexOption.IGNORE_CASE).containsMatchIn(t.optString("source"))) r -= 4
            Triple(t, r, x)
        }.maxByOrNull { it.second } ?: return null
        val a = joined(e.opt("title")).ifEmpty { id }
        val E = toks(listOf(a, joined(e.opt("description")), joined(e.opt("subject")), id).joinToString(" ")).toSet()
        val y = toks(a).toSet()
        val m = best.first.optString("name")
        val b = E + toks(m.replace(Regex("\\.[^.]+$"), ""))
        val L = if (o.tokens.isNotEmpty()) o.tokens.count { it in b }.toDouble() / o.tokens.size else 0.0
        val O = o.tokens.count { it in y }
        if (o.tokens.isNotEmpty() && L < 0.5) return null
        var T = O * 45 + Math.round(L * 55).toInt() + best.second
        if (norm(a).contains(o.normalized)) T += 35
        if (norm(m).contains(o.normalized)) T += 20
        val subs = files.filter { Regex("\\.(srt|vtt|sbv|ass|ssa)$", RegexOption.IGNORE_CASE).containsMatchIn(it.optString("name")) && !it.opt("private").let { p -> p == true || p == "true" } }
            .map { it.optString("name") }.take(30)
        return ArchiveVideo(a, id, m.substringAfterLast('/'), m, best.third, best.first.optString("size").toLongOrNull() ?: 0L, T, ArchiveClient.fileUrl(id, m), subs)
    }

    /**
     * zt(): the full deep search. [onFound] gets the growing, sorted list as matches arrive.
     * Returns the final list and the number of collections searched.
     */
    suspend fun search(text: String, onFound: (List<ArchiveVideo>) -> Unit): Pair<List<ArchiveVideo>, Int> = coroutineScope {
        val t = parse(text)
        val (plans, titleQuery, metadataQuery) = plans(t)
        val found = HashMap<String, ArchiveVideo>()
        val lock = Any()
        fun sorted() = found.values.sortedWith(compareByDescending<ArchiveVideo> { it.matchScore }.thenBy { it.title }.thenBy { it.name })
        fun add(v: ArchiveVideo) {
            val list = synchronized(lock) {
                val k = v.identifier + "/" + v.path; val s = found[k]
                if (s != null && v.matchScore <= s.matchScore) return
                found[k] = v; sorted()
            }
            onFound(list)
        }
        suspend fun pool(items: List<JSONObject>) {
            val q = java.util.concurrent.ConcurrentLinkedQueue(items)
            (0 until min(16, items.size).coerceAtLeast(0)).map {
                async(Dispatchers.IO) {
                    while (true) { val d = q.poll() ?: break; runCatching { item(d, t) }.getOrNull()?.let(::add) }
                }
            }.awaitAll()
        }
        val first = plans.take(3); val rest = plans.drop(3)
        val restJob = rest.map { p -> async(Dispatchers.IO) { runCatching { docs(p.query) } } }
        val firstRes = first.map { p -> async(Dispatchers.IO) { runCatching { docs(p.query) } } }.awaitAll()
        val A = ArrayList<JSONObject>()
        var ok = 0
        firstRes.forEach { r -> r.getOrNull()?.let { ok++; A.addAll(it) } }
        var restRes: List<Result<List<JSONObject>>>? = null
        if (ok == 0) {
            restRes = restJob.awaitAll()
            restRes.forEach { r -> r.getOrNull()?.let { ok++; A.addAll(it) } }
            if (ok == 0) throw (firstRes.firstOrNull { it.isFailure }?.exceptionOrNull() ?: IOException("All Archive.org video searches failed."))
        }
        // qt(): collections named by the results plus collections matching the phrase
        val snapA = A.toList()
        val colJob = async(Dispatchers.IO) {
            val n = LinkedHashSet<String>()
            snapA.forEach { collections(it).forEach(n::add) }
            if (t.normalized.isNotEmpty()) {
                val r = t.normalized.replace("\\", "\\\\").replace("\"", "\\\"")
                runCatching { docs("mediatype:collection AND (title:\"$r\" OR description:\"$r\" OR subject:\"$r\")") }.getOrNull()
                    ?.take(8)?.forEach { s -> s.optString("identifier").takeIf { it.isNotEmpty() }?.let(n::add) }
            }
            n.take(8)
        }
        val seen = HashSet<String>()
        val b = rank(A, t).take(90)
        b.forEach { seen.add(it.optString("identifier")) }
        val G = async { pool(b) }
        if (restRes == null) restJob.awaitAll().forEach { r -> r.getOrNull()?.let(A::addAll) }
        val L = colJob.await()
        val O = ArrayList<String>(); A.forEach { O.addAll(collections(it)) }
        val T = (L + O).distinct().take(8)
        val qq = ArrayList<JSONObject>()
        val I = metadataQuery.ifEmpty { titleQuery }
        if (I.isNotEmpty() && T.isNotEmpty()) T.map { c -> async(Dispatchers.IO) { runCatching { docs("mediatype:movies AND collection:$c AND ($I)") } } }.awaitAll().forEach { r -> r.getOrNull()?.let(qq::addAll) }
        G.await()
        val tt = rank(A + qq, t).take(220).filter { seen.add(it.optString("identifier")) }
        pool(tt)
        synchronized(lock) { sorted() } to T.size
    }
}

// ------------------------------------------------------------------ RETRO_EMU_95 deep Archive ROM search (rr-retro-emu Ke())

data class ArchiveRom(val title: String, val identifier: String, val name: String, val path: String, val ext: String, val size: Long, val matchScore: Int, val url: String)

object ArchiveRomSearch {
    val ALL = setOf("gb", "gbc", "gba", "nes", "n64", "z64", "v64", "snes", "smc", "sfc", "fig", "gd3", "gd7", "dx2", "bsx", "swc")
    class Sys(val label: String, val extensions: List<String>, val collectionTerms: List<String>)
    val K = linkedMapOf(
        "nes" to Sys("NES", listOf("nes"), listOf("Nintendo Entertainment System", "NES")),
        "n64" to Sys("N64", listOf("n64", "z64", "v64"), listOf("Nintendo 64", "Nintendo 64 Games", "N64")),
        "snes" to Sys("SNES", listOf("snes", "smc", "sfc", "fig", "gd3", "gd7", "dx2", "bsx", "swc"), listOf("Super Nintendo Entertainment System", "Super Nintendo", "SNES")),
        "gb" to Sys("GB", listOf("gb"), listOf("Nintendo Game Boy", "Game Boy", "Gameboy")),
        "gbc" to Sys("GBC", listOf("gbc"), listOf("Nintendo Game Boy Color", "Game Boy Color", "Gameboy Color", "GBC")),
        "gba" to Sys("GBA", listOf("gba"), listOf("Nintendo Game Boy Advance", "Game Boy Advance", "Gameboy Advance", "GBA")),
    )
    class Q(val original: String, val title: String, val normalized: String, val tokens: List<String>, val systemKeys: List<String>, val extensions: Set<String>, val systemLabel: String)

    private fun O(e: String) = ArchiveVideoSearch.norm(e)
    private fun j(e: String) = O(e).split(" ").filter { it.isNotEmpty() }
    private fun ext(e: String) = e.trim().split(".").let { if (it.size > 1) it.last().lowercase() else "" }
    private fun rx(p: String) = Regex(p, RegexOption.IGNORE_CASE)
    private const val B = "(^|[^a-z0-9])"; private const val E = "(?=$|[^a-z0-9])"

    /** Pe() */
    fun parse(e: String): Q {
        val t = e.trim(); var n = t
        val o = ArrayList<String>()
        val a = listOf(
            "n64" to listOf("${B}nintendo[\\s._-]*64$E", "${B}n64$E"),
            "gba" to listOf("${B}nintendo[\\s._-]*game[\\s._-]*boy[\\s._-]*advance$E", "${B}nintendo[\\s._-]*gbba$E", "${B}nintendo[\\s._-]*gba$E", "${B}gbba$E", "${B}gba$E", "${B}game[\\s._-]*boy[\\s._-]*advance$E"),
            "gbc" to listOf("${B}nintendo[\\s._-]*game[\\s._-]*boy[\\s._-]*color$E", "${B}nintendo[\\s._-]*gbc$E", "${B}gbc$E", "${B}game[\\s._-]*boy[\\s._-]*color$E"),
            "gb" to listOf("${B}nintendo[\\s._-]*game[\\s._-]*boy(?![\\s._-]*(?:advance|color))$E", "${B}nintendo[\\s._-]*gb$E", "${B}gb$E", "${B}game[\\s._-]*boy(?![\\s._-]*(?:advance|color))$E"),
            "snes" to listOf("${B}snes$E", "${B}super[\\s._-]*nintendo(?:[\\s._-]*entertainment[\\s._-]*system)?$E"),
            "nes" to listOf("${B}nes$E", "${B}nintendo[\\s._-]*entertainment[\\s._-]*system$E"),
        )
        a.forEach { (key, pats) ->
            var hit = false
            pats.forEach { p -> val r = rx(p); if (r.containsMatchIn(n)) hit = true; n = r.replace(n, " ") }
            if (hit && key !in o) o.add(key)
        }
        n = rx("\\.(?:gb|gbc|gba|nes|n64|z64|v64|snes|smc|sfc|fig|gd3|gd7|dx2|bsx|swc)\\b").replace(n, " ")
        n = rx("\\broms?\\b").replace(n, " ")
        val r = O(n)
        val toks = j(r).filter { it.length > 1 }.take(10)
        val d = if (o.isNotEmpty()) o.flatMap { K.getValue(it).extensions }.toSet() else ALL
        return Q(t, n.trim(), r, toks, o, d, o.joinToString(" + ") { K.getValue(it).label })
    }
    class Plan(val query: String, val pages: Int)
    /** ze() */
    private fun plans(e: Q): Triple<List<Plan>, String, String> {
        val t = e.normalized.replace("\\", "\\\\").replace("\"", "\\\"")
        val meta = { i: String -> "(title:$i* OR description:$i* OR subject:$i*)" }
        val n = e.tokens.joinToString(" AND ") { "title:$it*" }
        val o = e.tokens.joinToString(" AND ") { meta(it) }
        val a = ArrayList<Plan>()
        fun r(i: String, d: Int) { if (i.isNotEmpty() && a.none { it.query == i }) a.add(Plan(i, d)) }
        if (t.isNotEmpty()) r("title:\"$t\"", 1)
        if (n.isNotEmpty()) r(n, 1)
        if (o.isNotEmpty()) r(o, 2)
        if (e.tokens.size in 2..6) for (i in 0 until e.tokens.size - 1) {
            val d = e.tokens.toMutableList(); val jw = d[i] + d[i + 1]; d.removeAt(i); d.removeAt(i); d.add(i, jw)
            r(d.joinToString(" AND ") { "title:$it*" }, 1); r(d.joinToString(" AND ") { meta(it) }, 1)
        }
        return Triple(a, n, o)
    }
    /** Ge(): one page */
    private fun page(q: String, p: Int): Pair<List<JSONObject>, Int> {
        val url = "https://archive.org/advancedsearch.php?q=" + URLEncoder.encode(ArchiveFree.q(q), "UTF-8") + "&rows=100&page=$p&output=json" +
            listOf("identifier", "title", "mediatype", "collection", "description").joinToString("") { "&fl%5B%5D=$it" }
        val conn = open(url, 25_000)
        val code = conn.responseCode
        if (code !in 200..299) { conn.disconnect(); throw IOException("Internet Archive search failed ($code).") }
        val json = try { JSONObject(conn.readBody()) } finally { conn.disconnect() }
        val resp = json.optJSONObject("response")
        val arr = resp?.optJSONArray("docs") ?: JSONArray()
        return (0 until arr.length()).mapNotNull { arr.optJSONObject(it) } to (resp?.optInt("numFound") ?: 0)
    }
    /** te() */
    private fun pages(q: String, n: Int): List<JSONObject> {
        val out = ArrayList<JSONObject>()
        for (a in 1..n) { val (docs, total) = page(q, a); out.addAll(docs); if (total == 0 || a * 100 >= total || docs.size < 100) break }
        return out
    }
    private fun strs(v: Any?): List<String> = when (v) { is JSONArray -> (0 until v.length()).map { v.optString(it) }; null, JSONObject.NULL -> emptyList(); else -> listOf(v.toString()) }
    private fun ve(d: JSONObject) = strs(d.opt("collection")).map { it.trim() }.filter { rx("^[a-z0-9._-]{2,120}$").matches(it) }
    /** $e() */
    private fun collectionQuery(e: Q): String {
        if (e.systemKeys.isNotEmpty()) {
            val n = ArrayList<String>()
            e.systemKeys.forEach { o -> K.getValue(o).collectionTerms.forEach { a -> val r = a.replace("\\", "\\\\").replace("\"", "\\\""); n.add("title:\"$r\""); n.add("description:\"$r\""); n.add("subject:\"$r\"") } }
            return "mediatype:collection AND (${n.joinToString(" OR ")})"
        }
        if (e.normalized.isEmpty()) return ""
        val t = e.normalized.replace("\\", "\\\\").replace("\"", "\\\"")
        return "mediatype:collection AND (title:\"$t\" OR description:\"$t\" OR subject:\"$t\")"
    }
    /** We() */
    private fun colScore(e: JSONObject, t: Q): Double {
        val n = O(strs(e.opt("title")).joinToString(" ") + " " + strs(e.opt("description")).joinToString(" "))
        val o = j(n).toSet(); var a = 0.0
        t.systemKeys.forEach { i -> K.getValue(i).collectionTerms.forEach { s -> val l = O(s); if (l.isNotEmpty() && n.contains(l)) a += 30 } }
        val r = t.tokens.count { it in o }
        a += if (t.tokens.isNotEmpty()) r.toDouble() / t.tokens.size * 20 else 0.0
        return a
    }
    /** ne() */
    private fun titleFrac(e: JSONObject, t: Q): Double { val n = j(strs(e.opt("title")).joinToString(" ")).toSet(); return if (t.tokens.isNotEmpty()) t.tokens.count { it in n }.toDouble() / t.tokens.size else 0.0 }
    /** the l() ranking */
    private fun rank(c: List<JSONObject>, t: Q): List<JSONObject> {
        val h = LinkedHashMap<String, JSONObject>()
        c.forEach { u -> val f = u.optString("identifier"); if (f.isEmpty()) return@forEach; val e = h[f]; if (e == null || titleFrac(u, t) > titleFrac(e, t)) h[f] = u }
        return h.values.sortedByDescending { titleFrac(it, t) }
    }
    /** Ne(): matching ROM files of one item */
    private fun item(e: JSONObject, n: Q): List<ArchiveRom> {
        val id = e.optString("identifier")
        val conn = open("https://archive.org/metadata/" + URLEncoder.encode(id, "UTF-8"), 20_000)
        if (conn.responseCode !in 200..299) { conn.disconnect(); return emptyList() }
        val a = try { JSONObject(conn.readBody()) } finally { conn.disconnect() }
        val meta = a.optJSONObject("metadata") ?: return emptyList()
        val r = a.optJSONArray("files") ?: JSONArray()
        val i = strs(meta.opt("title")).firstOrNull()?.ifEmpty { null } ?: strs(e.opt("title")).firstOrNull() ?: id
        val d = j(i).toSet()
        return (0 until r.length()).mapNotNull { r.optJSONObject(it) }.mapNotNull { s ->
            val l = s.optString("name"); val c = ext(l)
            val h = s.opt("private").let { it == true || it == "true" }
            val u = s.optString("size").toLongOrNull() ?: 0L
            if (l.isEmpty() || c !in n.extensions || c !in ALL || h || (u > 0 && u > 128L * 1024 * 1024)) return@mapNotNull null
            val base = l.replace(Regex("\\.[^.]+$"), "")
            val f = j(base).toSet(); val Ee = d + f
            val w = if (n.tokens.isNotEmpty()) n.tokens.count { it in Ee }.toDouble() / n.tokens.size else 0.0
            val joined = n.tokens.joinToString("")
            val ae = joined.length >= 4 && (O(i).replace(" ", "").contains(joined) || O(base).replace(" ", "").contains(joined))
            if (n.tokens.isNotEmpty() && w < 1 && !ae && !(n.tokens.size >= 4 && w >= 0.8)) return@mapNotNull null
            var v = when {
                n.tokens.isNotEmpty() && n.tokens.all { it in f } -> 160
                n.tokens.isNotEmpty() && n.tokens.all { it in d } -> 145
                n.tokens.isNotEmpty() && w == 1.0 -> 125
                ae -> 115
                else -> Math.round(w * 80).toInt()
            }
            if (n.tokens.isNotEmpty() && O(l).contains(n.normalized)) v += 20
            if (n.systemKeys.isNotEmpty()) v += 15
            ArchiveRom(i, id, l.substringAfterLast('/'), l, c, u, v, ArchiveClient.fileUrl(id, l))
        }
    }

    /** Ke(): returns the final list and the number of collections searched. */
    suspend fun search(t: Q, onFound: (List<ArchiveRom>) -> Unit): Pair<List<ArchiveRom>, Int> = coroutineScope {
        val (plans, titleQuery, metadataQuery) = plans(t)
        val found = HashMap<String, ArchiveRom>(); val lock = Any()
        fun sorted() = found.values.sortedWith(compareByDescending<ArchiveRom> { it.matchScore }.thenBy { it.title }.thenBy { it.name })
        fun add(list: List<ArchiveRom>) {
            val out = synchronized(lock) {
                var ch = false
                list.forEach { f -> val k = f.identifier + "/" + f.path; val y = found[k]; if (y == null || f.matchScore > y.matchScore) { found[k] = f; ch = true } }
                if (!ch) return; sorted()
            }
            onFound(out)
        }
        suspend fun pool(items: List<JSONObject>) {
            val q = java.util.concurrent.ConcurrentLinkedQueue(items)
            (0 until min(12, items.size)).map { async(Dispatchers.IO) { while (true) { val d = q.poll() ?: break; runCatching { item(d, t) }.getOrNull()?.takeIf { it.isNotEmpty() }?.let(::add) } } }.awaitAll()
        }
        val restJob = plans.drop(3).map { p -> async(Dispatchers.IO) { runCatching { pages(p.query, p.pages) } } }
        val first = plans.take(3).map { p -> async(Dispatchers.IO) { runCatching { pages(p.query, p.pages) } } }.awaitAll()
        val Ed = ArrayList<JSONObject>(); var y = 0
        first.forEach { r -> r.getOrNull()?.let { y++; Ed.addAll(it) } }
        var restDone = false
        if (y == 0) {
            restJob.awaitAll().forEach { r -> r.getOrNull()?.let { y++; Ed.addAll(it) } }; restDone = true
            if (y == 0) throw (first.firstOrNull { it.isFailure }?.exceptionOrNull() ?: IOException("All Archive.org search methods failed."))
        }
        val snap = Ed.toList()
        val colJob = async(Dispatchers.IO) {
            val o = LinkedHashSet<String>(); snap.forEach { ve(it).forEach(o::add) }
            val a = ArrayList<String>()
            val cq = collectionQuery(t)
            if (cq.isNotEmpty()) runCatching { pages(cq, 1) }.getOrNull()?.map { it to colScore(it, t) }?.sortedByDescending { it.second }
                ?.take(if (t.systemKeys.isNotEmpty()) 10 else 5)?.forEach { (d, _) -> d.optString("identifier").takeIf { it.isNotEmpty() }?.let(a::add) }
            (a + o).distinct().take(if (t.systemKeys.isNotEmpty()) 10 else 6)
        }
        val seen = HashSet<String>()
        val oe = rank(Ed, t).take(90); oe.forEach { seen.add(it.optString("identifier")) }
        val fe = async { pool(oe) }
        if (!restDone) restJob.awaitAll().forEach { r -> r.getOrNull()?.let(Ed::addAll) }
        val ae = colJob.await()
        val v = ArrayList<String>(); Ed.forEach { v.addAll(ve(it)) }
        val C = (ae + v).distinct().take(if (t.systemKeys.isNotEmpty()) 10 else 6)
        val Ce = metadataQuery.ifEmpty { titleQuery }
        val ke = ArrayList<JSONObject>()
        if (Ce.isNotEmpty() && C.isNotEmpty()) C.map { s -> async(Dispatchers.IO) { runCatching { pages("collection:$s AND ($Ce)", 2) } } }.awaitAll().forEach { r -> r.getOrNull()?.let(ke::addAll) }
        fe.await()
        pool(rank(Ed + ke, t).take(220).filter { seen.add(it.optString("identifier")) })
        synchronized(lock) { sorted() } to C.size
    }
}
