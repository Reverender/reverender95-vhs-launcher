package com.reverender.r95.ui.vhs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/** The news ticker in the home screen's title block: headlines from an RSS (or Atom) feed, every 15 minutes. */
internal object VhsRss {
    const val DEFAULT = "https://feeds.bbci.co.uk/news/world/rss.xml"
    var headlines by mutableStateOf<List<String>>(emptyList())
    var status by mutableStateOf("TUNING IN THE NEWS…")
    private var loadedUrl = ""

    /** keeps the headlines fresh while the home screen shows; [url] blank = off */
    suspend fun run(url: String) {
        if (url.isBlank()) { headlines = emptyList(); return }
        if (url != loadedUrl) { headlines = emptyList(); status = "TUNING IN THE NEWS…" }
        delay(1200) // (typing a new address: wait until it stops changing)
        while (true) {
            val r = runCatching { fetch(url) }
            r.onSuccess { if (it.isNotEmpty()) { headlines = it; loadedUrl = url } else status = "NO NEWS IN THIS FEED" }
            r.onFailure { if (headlines.isEmpty()) status = "NEWS FEED UNAVAILABLE"; android.util.Log.w("r95", "rss: $url", it) }
            delay(if (r.isSuccess) 15 * 60_000L else 60_000L)
        }
    }

    private suspend fun fetch(url0: String): List<String> = withContext(Dispatchers.IO) {
        var url = url0.trim().let { if (!it.startsWith("http")) "https://$it" else it }
        var body: java.io.InputStream? = null
        var conn: java.net.HttpURLConnection? = null
        // follow redirects by hand (http → https across hosts too)
        for (hop in 0 until 5) {
            val c = (java.net.URL(url).openConnection() as java.net.HttpURLConnection).apply {
                connectTimeout = 10_000; readTimeout = 15_000; instanceFollowRedirects = false
                setRequestProperty("User-Agent", "Mozilla/5.0 (Android) REVERENDER_95 VHS")
                setRequestProperty("Accept", "application/rss+xml, application/atom+xml, application/xml, text/xml, */*")
            }
            val code = c.responseCode
            if (code in 300..399) { url = java.net.URL(java.net.URL(url), c.getHeaderField("Location")).toString(); c.disconnect(); continue }
            if (code != 200) { c.disconnect(); error("HTTP $code") }
            conn = c; body = c.inputStream; break
        }
        val input = body ?: error("too many redirects")
        try {
            val xp = android.util.Xml.newPullParser()
            xp.setFeature(org.xmlpull.v1.XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            xp.setInput(input, null)
            val out = ArrayList<String>()
            var inItem = false
            var ev = xp.eventType
            while (ev != org.xmlpull.v1.XmlPullParser.END_DOCUMENT && out.size < 25) {
                when (ev) {
                    org.xmlpull.v1.XmlPullParser.START_TAG -> when (xp.name.substringAfter(':').lowercase()) {
                        "item", "entry" -> inItem = true
                        "title" -> if (inItem) {
                            val t = xp.nextText().replace(Regex("<[^>]+>"), "").replace(Regex("\\s+"), " ").trim()
                            if (t.isNotEmpty()) out.add(t)
                        }
                    }
                    org.xmlpull.v1.XmlPullParser.END_TAG -> if (xp.name.substringAfter(':').lowercase() in setOf("item", "entry")) inItem = false
                }
                ev = xp.next()
            }
            out
        } finally { runCatching { input.close() }; conn?.disconnect() }
    }

    /** the ticker text: every headline, separated by diamonds */
    fun ticker(): String = if (headlines.isEmpty()) status else headlines.joinToString("  ◆  ")
}
