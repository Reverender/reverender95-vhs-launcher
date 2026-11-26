package com.reverender.r95.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** What Revy asked another window to do (the site submits that window's form); the window consumes it. */
object RevyRequests {
    var browserUrl by mutableStateOf<String?>(null)
    var videoQuery by mutableStateOf<String?>(null)
    var videoSource by mutableStateOf("auto")
    var emuQuery by mutableStateOf<String?>(null)
    /** test hook: load the first ROM result automatically */
    var emuAutoPick = false
    var musicQuery by mutableStateOf<String?>(null)
    /** a YouTube video (id, title) to play in VIDEO_STREAM_95 */
    var videoYoutube by mutableStateOf<Pair<String, String>?>(null)
}
