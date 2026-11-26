package com.reverender.r95.net

import com.reverender.r95.core.Site
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.Normalizer
import java.util.concurrent.atomic.AtomicInteger

private fun httpJson(url: String, timeoutMs: Int = 15000): String? = runCatching {
    val c = (URL(url).openConnection() as HttpURLConnection).apply {
        connectTimeout = minOf(12000, timeoutMs); readTimeout = timeoutMs
        setRequestProperty("User-Agent", Site.USER_AGENT); setRequestProperty("Accept", "application/json")
    }
    if (c.responseCode !in 200..299) null else c.inputStream.bufferedReader().use { it.readText() }
}.getOrNull()

private fun enc(s: String) = URLEncoder.encode(s, "UTF-8").replace("+", "%20")

/** The site's station directory (window.searchRadioStations): presets filtered locally + radio-browser.info. */
object StationDirectory {
    data class Row(val name: String, val country: String, val url: String)

    private class P(val name: String, val country: String, val tags: String, val url: String)
    private val PRESETS = listOf(
        P("Nightwave Plaza (Vaporwave)", "INT", "vaporwave synthwave future funk", "https://radio.plaza.one/mp3"),
        P("SomaFM Vaporwaves", "US", "vaporwave chillwave synth", "https://ice1.somafm.com/vaporwaves-128-mp3"),
        P("Synthwave Radio EU", "EU", "synthwave retrowave outrun 80s", "https://stream.synthwaveradio.eu/listen/synthwaveradio.eu/radio.mp3"),
        P("SomaFM DEF CON Radio", "US", "electronic hacker industrial techno", "https://ice1.somafm.com/defcon-128-mp3"),
        P("SomaFM Groove Salad", "US", "ambient chillout lofi downtempo", "https://ice1.somafm.com/groovesalad-128-mp3"),
        P("SomaFM Secret Agent", "US", "lounge jazz spy vintage", "https://ice1.somafm.com/secretagent-128-mp3"),
        P("SomaFM Drone Zone", "US", "ambient space drone sleep", "https://ice1.somafm.com/dronezone-128-mp3"),
        P("SomaFM Underground 80s", "US", "80s new wave synthpop uk", "https://ice1.somafm.com/u80s-128-mp3"),
        P("SomaFM Beat Blender", "US", "deep house chill lofi electronic", "https://ice1.somafm.com/beatblender-128-mp3"),
        P("SomaFM Space Station Soma", "US", "space ambient electronica", "https://ice1.somafm.com/spacestation-128-mp3"),
        P("Smooth 80s London", "UK", "80s pop classic retro", "https://media-ssl.musicradio.com/Smooth80s"),
        P("181.fm The Buzz", "US", "90s rock alternative grunge", "https://listen.181fm.com/181-buzz_128k.mp3"),
        P("181.fm Awesome 80s", "US", "80s pop rock retro", "https://listen.181fm.com/181-awesome80s_128k.mp3"),
        P("SwissRadio Jazz", "CH", "jazz smooth jazz swing blues", "https://stream.srg-ssr.ch/m/rsj/mp3_128"),
        P("SomaFM Boot Liquor", "US", "country americana roots", "https://ice2.somafm.com/bootliquor-128-mp3"),
        P("SomaFM Metal Detector", "US", "metal hard rock heavy", "https://ice1.somafm.com/metal-128-mp3"),
    )

    private suspend fun directory(q: String): JSONArray? = withContext(Dispatchers.IO) {
        val servers = listOf("https://de1.api.radio-browser.info", "https://fi1.api.radio-browser.info", "https://at1.api.radio-browser.info", "https://all.api.radio-browser.info")
        val c = enc(q)
        val paths = listOf(
            "/json/stations/search?name=$c&limit=30&hidebroken=true&order=clickcount&reverse=true",
            "/json/stations/search?tag=${c.lowercase()}&limit=30&hidebroken=true&order=clickcount&reverse=true",
        )
        for (s in servers) for (pth in paths) {
            val body = withTimeoutOrNull(3500) { httpJson(s + pth, 3500) } ?: continue
            val a = runCatching { JSONArray(body) }.getOrNull() ?: continue
            if (a.length() > 0) return@withContext a
        }
        null
    }

    /** Returns (rows, directoryUnavailable). Empty query searches "vaporwave", like the site. */
    suspend fun search(query: String): Pair<List<Row>, Boolean> {
        val y = query.trim()
        val g = y.ifEmpty { "vaporwave" }
        val r = g.lowercase()
        val local = PRESETS.filter { y.isEmpty() || "${it.name} ${it.tags} ${it.country}".lowercase().contains(r) }.map { Row(it.name, it.country, it.url) }
        val found = directory(g)
        val remote = ArrayList<Row>()
        if (found != null) for (i in 0 until found.length()) {
            val e = found.optJSONObject(i) ?: continue
            val cc = (e.optString("countrycode").ifEmpty { e.optString("country").ifEmpty { e.optString("codec").ifEmpty { "FM" } } }).take(3).uppercase()
            remote.add(Row(e.optString("name").trim().ifEmpty { "Live Radio" }, cc, e.optString("url_resolved").ifEmpty { e.optString("url") }.trim()))
        }
        val seen = HashSet<String>(); val out = ArrayList<Row>()
        (local + remote).forEach { if (Regex("^https?://", RegexOption.IGNORE_CASE).containsMatchIn(it.url) && seen.add(it.url)) out.add(it) }
        if (out.isEmpty()) return PRESETS.map { Row(it.name, it.country, it.url) } to true
        return out.take(35) to false
    }
}

/**
 * The site's Internet Archive MP3 search (rr-audio-priority.js): several query plans, collection discovery,
 * per-item file lookups (16 at a time), the same match scoring, and only MP3 files of at least 4 MB.
 */
object ArchiveMp3Search {
    data class Hit(val identifier: String, val title: String, val creator: String, val name: String, val path: String, val size: Long, val matchScore: Int, val url: String)

    private const val MIN_SIZE = 4L * 1024 * 1024
    private val STOP = setOf("the", "a", "an", "and", "or", "of", "for", "to", "from", "in", "on", "at", "by", "with", "as", "is", "are", "was", "were", "song", "songs", "music", "audio", "mp3", "album", "track", "tracks")

    private fun words(e: String?): List<String> = Normalizer.normalize(e ?: "", Normalizer.Form.NFKD).replace(Regex("[\\u0300-\\u036f]"), "").lowercase()
        .replace(Regex("[^a-z0-9]+"), " ").trim().split(Regex("\\s+")).filter { it.length > 1 }
    private fun terms(e: String): List<String> { val r = words(e).distinct(); val t = r.filter { it !in STOP }; return t.ifEmpty { r } }
    private fun norm(e: String?) = words(e).joinToString(" ")
    private fun set(e: String?) = words(e).toSet()
    private fun count(r: List<String>, s: Set<String>) = r.count { it in s }
    private fun download(id: String, path: String) = "https://archive.org/download/" + enc(id) + "/" + path.split('/').joinToString("/") { enc(it) }

    private fun str(o: JSONObject, k: String): String { val v = o.opt(k); return if (v is JSONArray) (0 until v.length()).joinToString(" ") { v.optString(it) } else (v?.toString() ?: "") }

    private fun plans(phrase: String, r: List<String>): Pair<List<String>, String?> {
        val t = phrase.replace("\\", "\\\\").replace("\"", "\\\"")
        val titleAll = r.joinToString(" AND ") { "title:$it*" }
        val anyField = r.joinToString(" AND ") { "(title:$it* OR creator:$it* OR description:$it* OR subject:$it*)" }
        val n = ArrayList<String>()
        fun a(u: String) { if (u.isEmpty()) return; val q = "(mediatype:audio OR mediatype:etree) AND ($u)"; if (q !in n) n.add(q) }
        a("title:\"$t\""); a(titleAll); a(anyField)
        if (r.size in 2..6) for (s in 0 until r.size - 1) {
            val m = r.toMutableList(); m.subList(s, s + 2).clear(); m.add(s, r[s] + r[s + 1])
            a(m.joinToString(" AND ") { "title:$it*" })
            a(m.joinToString(" AND ") { "(title:$it* OR creator:$it* OR description:$it* OR subject:$it*)" })
        }
        return n to anyField
    }

    private val searchCache = HashMap<String, Pair<Long, List<JSONObject>>>()
    private suspend fun page(q: String, page: Int): Pair<List<JSONObject>, Int> = withContext(Dispatchers.IO) {
        val fl = listOf("identifier", "title", "creator", "mediatype", "collection", "description", "subject").joinToString("") { "&fl[]=$it" }
        val body = httpJson("https://archive.org/advancedsearch.php?q=${enc(q)}&rows=100&page=$page&output=json$fl") ?: throw RuntimeException("Internet Archive search failed.")
        val resp = JSONObject(body).optJSONObject("response") ?: JSONObject()
        val docs = resp.optJSONArray("docs") ?: JSONArray()
        List(docs.length()) { docs.getJSONObject(it) } to resp.optInt("numFound")
    }
    private suspend fun query(q: String, pages: Int): List<JSONObject> {
        synchronized(searchCache) { searchCache[q]?.let { if (System.currentTimeMillis() - it.first < 600_000) return it.second } }
        val out = ArrayList<JSONObject>()
        for (i in 1..pages) { val (d, total) = page(q, i); out.addAll(d); if (total == 0 || i * 100 >= total || d.size < 100) break }
        synchronized(searchCache) { searchCache[q] = System.currentTimeMillis() to out }
        return out
    }
    private fun ids(v: Any?): List<String> {
        val l = when (v) { is JSONArray -> (0 until v.length()).map { v.optString(it) }; null -> emptyList(); else -> listOf(v.toString()) }
        return l.map { it.trim() }.filter { Regex("^[a-z0-9._-]{2,120}$", RegexOption.IGNORE_CASE).matches(it) }
    }

    private suspend fun collections(phrase: String, r: List<String>, docs: List<JSONObject>, meta: String?): List<String> {
        val counts = HashMap<String, Int>()
        docs.forEach { d -> ids(d.opt("collection")).forEach { counts[it] = (counts[it] ?: 0) + 1 } }
        val s = phrase.replace("\\", "\\\\").replace("\"", "\\\"")
        val qs = mutableListOf("mediatype:collection AND (title:\"$s\" OR description:\"$s\" OR subject:\"$s\")")
        if (meta != null) qs.add("mediatype:collection AND ($meta)")
        val found = LinkedHashMap<String, JSONObject>()
        coroutineScope { qs.map { q -> async { runCatching { query(q, 2) }.getOrDefault(emptyList()) } }.awaitAll() }.flatten().forEach { found[it.optString("identifier")] = it }
        val scored = found.values.mapNotNull { c ->
            val d = set(c.optString("title")); val m = set(listOf(str(c, "title"), str(c, "description"), str(c, "subject"), str(c, "creator")).joinToString(" "))
            val g = count(r, d); val i = count(r, m)
            val f = if (r.isNotEmpty()) i.toDouble() / r.size else 0.0
            val v = if (r.size <= 2) 1.0 else 0.75
            if (f < v || g < 1) null else c.optString("identifier") to (g * 100 + i * 20 + (counts[c.optString("identifier")] ?: 0) * 3)
        }.sortedByDescending { it.second }
        val a = ArrayList<String>(); scored.take(10).forEach { a.add(it.first) }
        counts.entries.sortedByDescending { it.value }.forEach { if (it.key !in a) a.add(it.key) }
        return a.distinct().take(12)
    }

    private val filesCache = HashMap<String, Pair<Long, JSONArray>>()
    private suspend fun files(id: String): JSONArray = withContext(Dispatchers.IO) {
        synchronized(filesCache) { filesCache[id]?.let { if (System.currentTimeMillis() - it.first < 600_000) return@withContext it.second } }
        val a = httpJson("https://archive.org/metadata/${enc(id)}/files")?.let { runCatching { JSONObject(it).optJSONArray("result") }.getOrNull() }
            ?: httpJson("https://archive.org/metadata/${enc(id)}")?.let { runCatching { JSONObject(it).optJSONArray("files") }.getOrNull() } ?: JSONArray()
        synchronized(filesCache) { filesCache[id] = System.currentTimeMillis() to a }
        a
    }

    private suspend fun itemHits(e: JSONObject, r: List<String>): List<Hit> {
        val id = e.optString("identifier"); if (id.isEmpty()) return emptyList()
        val fs = files(id); if (fs.length() == 0) return emptyList()
        val n = str(e, "title").ifEmpty { id }; val s = str(e, "creator"); val u = str(e, "description"); val w = str(e, "subject")
        val c = set(n); val d = set(s); val M = set(u); val g = set(w); val all = set(listOf(n, s, u, w, id).joinToString(" "))
        val out = ArrayList<Hit>()
        for (k in 0 until fs.length()) {
            val v = fs.optJSONObject(k) ?: continue
            val b = v.optString("name"); val priv = v.opt("private").toString().let { it == "true" || it == "1" }
            val size = v.optString("size").toLongOrNull() ?: continue
            if (!b.endsWith(".mp3", true) || priv || size < MIN_SIZE || Regex("(?:cover|artwork|thumbnail|spectrogram|poster|metadata|sample|preview|snippet)", RegexOption.IGNORE_CASE).containsMatchIn(b)) continue
            val base = b.substringAfterLast('/').replace(Regex("\\.mp3$", RegexOption.IGNORE_CASE), "")
            val L = set(base)
            val N = c + d + L
            val j = count(r, N); val V = count(r, L); val z = count(r, c); val Q = count(r, d)
            val l = if (r.isNotEmpty()) count(r, all + L).toDouble() / r.size else 0.0
            val pmin = maxOf(1, Math.ceil(r.size * 0.3).toInt())
            if (j < pmin) continue
            if (r.size > 2 && l < 0.8) continue
            if (r.size <= 2 && l < 1) continue
            out.add(Hit(id, n, s, b.substringAfterLast('/'), b, size, z * 120 + Q * 95 + V * 110 + count(r, M) * 14 + count(r, g) * 10 + Math.round(l * 30).toInt(), download(id, b)))
        }
        return out
    }

    sealed class Status { data class Msg(val text: String, val error: Boolean) : Status(); data class Results(val hits: List<Hit>) : Status() }

    /** Runs the search; [emit] receives status lines and (growing) result lists on the caller's dispatcher. */
    suspend fun search(raw: String, emit: (Status) -> Unit) {
        val o = raw.trim()
        val i = terms(o).take(10)
        if (o.length < 2 || i.isEmpty()) { emit(Status.Msg("ENTER AT LEAST 2 CHARACTERS TO SEARCH FOR MP3 TRACKS.", true)); return }
        emit(Status.Msg("DEEP SEARCHING INTERNET ARCHIVE AUDIO... FILTER: .MP3 ONLY. FIRST MATCHES APPEAR AUTOMATICALLY.", false))
        val (planList, meta) = plans(norm(o), i)
        val F = LinkedHashMap<String, Hit>(); val lock = Mutex()
        val seenItems = HashSet<String>()
        fun sorted() = F.values.sortedWith(compareByDescending<Hit> { it.matchScore }.thenBy { it.title }.thenBy { it.name })
        suspend fun merge(l: List<Hit>) {
            if (l.isEmpty()) return
            var changed = false
            lock.withLock { l.forEach { h -> val k = h.identifier + "/" + h.path; val x = F[k]; if (x == null || h.matchScore > x.matchScore) { F[k] = h; changed = true } } }
            if (changed) { val snap = lock.withLock { sorted() }; withContext(Dispatchers.Main) { emit(Status.Results(snap)) } }
        }
        fun rank(l: List<JSONObject>): List<JSONObject> {
            val p = LinkedHashMap<String, Pair<JSONObject, Int>>()
            l.forEach { y -> val h = y.optString("identifier"); if (h.isEmpty()) return@forEach
                val E = norm(listOf(str(y, "title"), str(y, "creator"), str(y, "description"), str(y, "subject")).joinToString(" "))
                val x = i.count { E.contains(it) }
                val te = p[h]; if (te == null || x > te.second) p[h] = y to x }
            return p.values.sortedByDescending { it.second }.map { it.first }
        }
        suspend fun scan(items: List<JSONObject>) = coroutineScope {
            val idx = AtomicInteger(0)
            List(minOf(16, items.size)) {
                launch(Dispatchers.IO) {
                    while (true) { val h = idx.getAndIncrement(); if (h >= items.size) break; ensureActive(); merge(runCatching { itemHits(items[h], i) }.getOrDefault(emptyList())) }
                }
            }.forEach { it.join() }
        }
        try {
            coroutineScope {
                val first = planList.take(3); val rest = planList.drop(3)
                val restJob = async { rest.map { q -> async { runCatching { query(q, 1) } } }.awaitAll() }
                val firstRes = first.map { q -> async { runCatching { query(q, 1) } } }.awaitAll()
                val g = ArrayList<JSONObject>(); var ok = 0
                firstRes.forEach { r -> r.getOrNull()?.let { ok++; g.addAll(it) } }
                val colJob = async { runCatching { collections(norm(o), i, g.toList(), meta) }.getOrDefault(emptyList()) }
                if (ok == 0) {
                    restJob.await().forEach { r -> r.getOrNull()?.let { ok++; g.addAll(it) } }
                    if (ok == 0) throw RuntimeException("All Internet Archive audio searches failed.")
                }
                val b = rank(g).take(90); b.forEach { seenItems.add(it.optString("identifier")) }
                val scanJob = async { scan(b) }
                restJob.await().forEach { r -> r.getOrNull()?.let { g.addAll(it) } }
                var N = colJob.await().toMutableList()
                g.forEach { d -> ids(d.opt("collection")).forEach { N.add(it) } }
                N = N.distinct().take(10).toMutableList()
                val j = ArrayList<JSONObject>()
                val V = meta ?: planList.firstOrNull()
                if (N.isNotEmpty()) {
                    N.map { l -> async {
                        val parts = ArrayList<JSONObject>()
                        if (V != null) runCatching { query("(mediatype:audio OR mediatype:etree) AND collection:$l AND ($V)", 2) }.getOrNull()?.let { parts.addAll(it) }
                        runCatching { query("(mediatype:audio OR mediatype:etree) AND collection:$l", 1) }.getOrNull()?.let { parts.addAll(it) }
                        parts
                    } }.awaitAll().forEach { j.addAll(it) }
                }
                scanJob.await()
                val more = rank(g + j).take(300).filter { seenItems.add(it.optString("identifier")) }
                scan(more)
            }
            val U = lock.withLock { sorted() }
            if (U.isEmpty()) emit(Status.Msg("NO PRECISE MATCHES FOUND. RESULTS MUST MATCH THE ARTIST / TITLE / FILE NAME AND CONTAIN A VERIFIED MP3 FILE OF AT LEAST 4 MB. TRY A MORE SPECIFIC ARTIST, TRACK OR ALBUM SEARCH.", true))
            else emit(Status.Results(U))
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Status.Msg("INTERNET ARCHIVE MP3 SEARCH FAILED. CHECK YOUR CONNECTION AND TRY AGAIN.", true))
        }
    }
}
