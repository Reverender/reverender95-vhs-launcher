package com.reverender.r95.os

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * C:\MUSIC\ — every MP3 / FLAC / OGG / M4A / WAV on the phone, indexed through MediaStore (no file picker
 * every session) and arranged by the folders they live in, like Windows 95 Explorer.
 */
object MusicLibrary {
    class Song(val id: Long, val name: String, val title: String, val artist: String, val durationMs: Long, val sizeBytes: Long, val uri: String, val ext: String)
    class Folder(val name: String, val path: String) {
        val folders = sortedMapOf<String, Folder>(String.CASE_INSENSITIVE_ORDER)
        val songs = ArrayList<Song>()
        fun count(): Int = songs.size + folders.values.sumOf { it.count() }
    }

    var root by mutableStateOf<Folder?>(null); private set
    var scanning by mutableStateOf(false); private set
    var total by mutableStateOf(0); private set

    val permission: String get() = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
    fun permitted(ctx: Context) = ctx.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    fun scan(ctx: Context) {
        if (scanning || !permitted(ctx)) return
        scanning = true
        val app = ctx.applicationContext
        Thread {
            val top = Folder("C:\\MUSIC", "")
            var n = 0
            runCatching {
                val coll = if (Build.VERSION.SDK_INT >= 29) MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL) else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                val pathCol = if (Build.VERSION.SDK_INT >= 29) MediaStore.Audio.Media.RELATIVE_PATH else @Suppress("DEPRECATION") MediaStore.Audio.Media.DATA
                val proj = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE, pathCol)
                app.contentResolver.query(coll, proj, "${MediaStore.Audio.Media.SIZE} > 0", null, "${MediaStore.Audio.Media.DISPLAY_NAME} COLLATE NOCASE")?.use { c ->
                    while (c.moveToNext()) {
                        val id = c.getLong(0)
                        val name = c.getString(1) ?: continue
                        val ext = name.substringAfterLast('.', "").uppercase()
                        var rel = c.getString(6) ?: ""
                        if (Build.VERSION.SDK_INT < 29) rel = rel.substringBeforeLast('/').removePrefix("/storage/emulated/0/").removePrefix("/")
                        val parts = rel.trim('/').split('/').filter { it.isNotBlank() }
                        var f = top
                        var path = ""
                        for (p in parts) { path += "/$p"; f = f.folders.getOrPut(p) { Folder(p, path) } }
                        val artist = c.getString(3)?.takeIf { it != "<unknown>" } ?: ""
                        f.songs.add(Song(id, name, c.getString(2) ?: name, artist, c.getLong(4), c.getLong(5),
                            ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString(), ext))
                        n++
                    }
                }
            }
            android.os.Handler(android.os.Looper.getMainLooper()).post { root = top; total = n; scanning = false }
        }.start()
    }

    fun find(path: String): Folder? {
        var f = root ?: return null
        for (p in path.trim('/').split('/').filter { it.isNotBlank() }) f = f.folders[p] ?: return null
        return f
    }

    /** "8.4 MB" / "4:12" like the Explorer columns */
    fun size(b: Long) = if (b >= 1_048_576) "%.1f MB".format(b / 1_048_576.0) else "${(b / 1024).coerceAtLeast(1)} KB"
    fun time(ms: Long) = "%d:%02d".format(ms / 60000, ms / 1000 % 60)
}
