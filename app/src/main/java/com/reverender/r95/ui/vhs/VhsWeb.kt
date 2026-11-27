package com.reverender.r95.ui.vhs

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.reverender.r95.os.Haptics
import com.reverender.r95.os.OsPrefs

/** the address typed into VHS WEB: a web address, or else words to search for */
private fun webTarget(input: String): String {
    val t = input.trim()
    if (t.isEmpty()) return OsPrefs.vhsWebHome
    if (t.startsWith("http://") || t.startsWith("https://")) return t
    if (!t.contains(' ') && t.contains('.')) return "https://$t"
    return "https://lite.duckduckgo.com/lite/?q=" + java.net.URLEncoder.encode(t, "UTF-8")
}

/**
 * VHS WEB: a small, quick web browser. Opens on the homepage from SETUP every time; on the way back home
 * it forgets everything (cookies, history, cache, site storage, form data).
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun VhsWeb(onExit: () -> Unit) {
    val ctx = LocalContext.current
    val o = LocalOsd.current
    val openKb = LocalOpenKb.current
    val blink = rememberBlink()
    var url by remember { mutableStateOf(OsPrefs.vhsWebHome) }
    var title by remember { mutableStateOf("") }
    var progress by remember { mutableIntStateOf(0) }
    var typing by remember { mutableStateOf<String?>(null) }
    var canBack by remember { mutableStateOf(false) }
    var canFwd by remember { mutableStateOf(false) }
    val web = remember {
        android.webkit.WebView(ctx).apply {
            setBackgroundColor(android.graphics.Color.BLACK)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            settings.setSupportMultipleWindows(false)
            settings.mediaPlaybackRequiresUserGesture = true
            webViewClient = object : android.webkit.WebViewClient() {
                override fun shouldOverrideUrlLoading(v: android.webkit.WebView, r: android.webkit.WebResourceRequest): Boolean {
                    val u = r.url
                    // web pages stay here; other links (mail, phone, app links) go to their apps
                    if (u.scheme == "http" || u.scheme == "https") return false
                    runCatching { ctx.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, u).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) }
                    return true
                }
                override fun doUpdateVisitedHistory(v: android.webkit.WebView, u: String?, reload: Boolean) {
                    u?.let { url = it }; canBack = v.canGoBack(); canFwd = v.canGoForward()
                }
            }
            webChromeClient = object : android.webkit.WebChromeClient() {
                override fun onProgressChanged(v: android.webkit.WebView, p: Int) { progress = p }
                override fun onReceivedTitle(v: android.webkit.WebView, t: String?) { title = t.orEmpty() }
            }
            loadUrl(webTarget(OsPrefs.vhsWebHome))
        }
    }
    // leaving: wipe every trace, then let the page go
    DisposableEffect(Unit) {
        onDispose {
            runCatching {
                web.stopLoading(); web.loadUrl("about:blank")
                web.clearHistory(); web.clearCache(true); web.clearFormData(); web.clearSslPreferences()
                android.webkit.CookieManager.getInstance().removeAllCookies(null)
                android.webkit.CookieManager.getInstance().flush()
                android.webkit.WebStorage.getInstance().deleteAllData()
                android.webkit.WebViewDatabase.getInstance(ctx).clearHttpAuthUsernamePassword()
                web.destroy()
            }
        }
    }
    fun go(input: String) { typing = null; web.loadUrl(webTarget(input)) }
    BackHandler {
        when {
            typing != null -> typing = null
            web.canGoBack() -> web.goBack()
            else -> onExit()
        }
    }
    if (typing != null) {
        RegisterKb(VhsKb({ typing.orEmpty() }, { typing = it.take(500) }, raw = true, label = "WEB ADDRESS OR SEARCH") { go(typing.orEmpty()) })
        androidx.compose.runtime.LaunchedEffect(Unit) { kotlinx.coroutines.delay(50); openKb() }
    }
    Column(Modifier.fillMaxSize().imePadding().padding(horizontal = pxDp(2f), vertical = pxDp(2f)), verticalArrangement = Arrangement.spacedBy(pxDp(1.4f))) {
        // the controls
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(pxDp(2f)), verticalAlignment = Alignment.CenterVertically) {
            VhsKey("◀", boxed = true) { if (web.canGoBack()) web.goBack() }
            VhsKey("▶", boxed = true) { if (web.canGoForward()) web.goForward() }
            VhsKey(if (progress in 1..99) "STOP" else "RELOAD", boxed = true) { if (progress in 1..99) web.stopLoading() else web.reload() }
            VhsKey("HOME", boxed = true) { go(OsPrefs.vhsWebHome) }
        }
        // the address line: tap to type an address or a search
        Box(Modifier.fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { Haptics.tick(); typing = "" } }) {
            val shown = typing?.let { vhsSafe(it).takeLast(19) + if (blink) "_" else " " }
                ?: vhsSafe(url.removePrefix("https://").removePrefix("http://").removePrefix("www.")).take(21)
            VhsText(shown, invert = true)
        }
        // loading: a tape counter style bar
        Box(Modifier.fillMaxWidth().height(pxDp(1.2f))) {
            if (progress in 1..99) Box(Modifier.fillMaxWidth(progress / 100f).height(pxDp(1.2f)).background(o.fg))
        }
        AndroidView({ web }, Modifier.fillMaxWidth().weight(1f))
        Box(Modifier.vhsAutoHide().fillMaxWidth().background(o.fg).padding(pxDp(1f)).pointerInput(Unit) { detectTapGestures { Haptics.tick(); onExit() } }) {
            Column {
                VhsText(vhsSafe(title).trim().ifEmpty { "VHS WEB" }.take(21), invert = true)
                VhsText("TAP HERE TO RETURN", invert = true)
            }
        }
    }
}
