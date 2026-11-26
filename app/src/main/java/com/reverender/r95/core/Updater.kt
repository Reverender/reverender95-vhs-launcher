package com.reverender.r95.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

/** Start menu ⬇ Update: downloads reverender_95.apk from the website and opens the system installer. */
object Updater {
    const val APK_URL = Site.BASE_URL + "reverender_95.apk"
    /** label for the Start menu row ("" = idle) */
    var status by mutableStateOf(""); private set
    private var busy = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun start(context: Context) {
        if (busy) return
        val ctx = context.applicationContext
        // Android 8+: the user allows this app to install updates once
        if (Build.VERSION.SDK_INT >= 26 && !ctx.packageManager.canRequestPackageInstalls()) {
            status = "Allow installs, then tap again"
            runCatching {
                ctx.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + ctx.packageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            return
        }
        busy = true
        status = "Downloading… 0%"
        scope.launch {
            try {
                val dir = File(ctx.cacheDir, "update").apply { mkdirs() }
                val apk = File(dir, "reverender_95.apk").apply { delete() }
                com.reverender.r95.net.Downloader.download(APK_URL, apk) { p -> status = "Downloading… ${(p * 100).toInt()}%" }
                status = "Installing…"
                val uri = if (Build.VERSION.SDK_INT >= 24) FileProvider.getUriForFile(ctx, ctx.packageName + ".update", apk) else Uri.fromFile(apk)
                val i = Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/vnd.android.package-archive")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                ctx.startActivity(i)
                status = ""
            } catch (e: Exception) {
                android.util.Log.w("r95", "update", e)
                status = "Update failed — tap to retry"
            } finally { busy = false }
        }
    }
}
