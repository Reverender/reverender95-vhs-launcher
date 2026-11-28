package com.reverender.r95.ui.vhs

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Google Play edition: YouTube videos play in YouTube's own embedded player (with its ads, controls and
 * branding, as YouTube's terms ask), shown inside a VHS frame. Nothing is pulled out of the stream: no EQ, no
 * visualizer, no audio-only or background play. Closing it stops the video.
 */
internal object VhsYt {
    var id by mutableStateOf<String?>(null)
    var title by mutableStateOf("")
    fun open(videoId: String, t: String) {
        // anything else playing stops while YouTube plays
        runCatching { com.reverender.r95.audio.AmpEngine.pauseIfPlaying() }
        title = t; id = videoId
    }
    fun close() { id = null }
}

private fun embedHtml(id: String): String {
    val safe = id.filter { it.isLetterOrDigit() || it == '-' || it == '_' }.take(20)
    return """<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1">
<style>html,body{margin:0;height:100%;background:#000;overflow:hidden}iframe{position:absolute;inset:0;width:100%;height:100%;border:0}</style>
</head><body><iframe src="https://www.youtube.com/embed/$safe?autoplay=1&playsinline=1&rel=0&origin=https://reverender.com"
 allow="autoplay; encrypted-media; picture-in-picture" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe></body></html>"""
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun VhsYtEmbedHost() {
    val id = VhsYt.id ?: return
    val ctx = LocalContext.current
    val o = LocalOsd.current
    BackHandler { VhsYt.close() }
    val web = remember(id) {
        android.webkit.WebView(ctx).apply {
            setBackgroundColor(android.graphics.Color.BLACK)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            webViewClient = object : android.webkit.WebViewClient() {
                override fun shouldOverrideUrlLoading(v: android.webkit.WebView, r: android.webkit.WebResourceRequest): Boolean {
                    // "watch on YouTube" and other links leave for the YouTube app or the browser
                    runCatching { ctx.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, r.url).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) }
                    return true
                }
            }
            // the page's own address is reverender.com, so YouTube gets a referrer (its embeds need one)
            loadDataWithBaseURL("https://reverender.com/", embedHtml(id), "text/html", "utf-8", null)
        }
    }
    DisposableEffect(web) {
        onDispose { runCatching { web.stopLoading(); web.loadUrl("about:blank"); web.destroy() } }
    }
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.92f)).pointerInput(Unit) { detectTapGestures { } }, contentAlignment = Alignment.Center) {
        Column(Modifier.fillMaxWidth().padding(pxDp(3f)), verticalArrangement = Arrangement.spacedBy(pxDp(2f))) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                VhsText("PLAY ▶ " + vhsSafe(VhsYt.title).take(22))
                VhsKey("EJECT", boxed = true) { VhsYt.close() }
            }
            Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).border(pxDp(0.6f), o.fg)) {
                AndroidView({ web }, Modifier.fillMaxSize().padding(pxDp(0.6f)))
            }
            VhsText("YOUTUBE PLAYER", color = o.fg.copy(alpha = 0.6f))
        }
    }
}
