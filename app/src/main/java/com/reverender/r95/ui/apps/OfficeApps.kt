package com.reverender.r95.ui.apps

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reverender.r95.LocalAppState
import com.reverender.r95.StickyNote
import com.reverender.r95.TodoItem
import com.reverender.r95.audio.UiSfx
import com.reverender.r95.ui.viz.ClockFaces
import com.reverender.r95.ui.viz.argb
import com.reverender.r95.ui.win95.Btn
import com.reverender.r95.ui.win95.Check
import com.reverender.r95.ui.win95.Field
import com.reverender.r95.ui.win95.T
import com.reverender.r95.ui.win95.W95
import com.reverender.r95.ui.win95.bevel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val noteColors = linkedMapOf("yellow" to 0xFFFFF3A0, "pink" to 0xFFFFC9E0, "mint" to 0xFFC8F5DC, "sky" to 0xFFC9E6FF, "lilac" to 0xFFE2D2FF)

// ================================================================ STICKY_NOTES

/** STICKY_NOTES_95, ported from ke(): up to 8 notes, tabs named by their first line, 5 paper colours. */
@Composable
fun StickyApp() {
    val state = LocalAppState.current
    val ctx = LocalContext.current
    val clip = androidx.compose.ui.platform.LocalClipboardManager.current
    var current by remember { mutableIntStateOf(0) }
    if (current >= state.stickies.size) current = state.stickies.size - 1
    val note = state.stickies[current]
    val paper = Color(noteColors[note.color] ?: 0xFFFFF3A0)
    Column(Modifier.fillMaxSize().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            state.stickies.forEachIndexed { i, n ->
                val on = i == current
                Box(
                    Modifier.widthIn(max = 90.dp).background(Color(noteColors[n.color] ?: 0xFFFFF3A0)).border(1.dp, Color(0x73000000)).clickable { current = i }
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                ) { T((n.text.lineSequence().firstOrNull()?.ifBlank { null } ?: "Note ${i + 1}").take(12), size = 10.sp, bold = on, maxLines = 1, color = Color(0xFF2B2410)) }
            }
        }
        Box(
            Modifier.fillMaxWidth().weight(1f).background(paper).bevel(false)
                .drawBehind { val step = 22.dp.toPx(); var y = step; while (y < size.height) { drawLine(Color(0x12000000), Offset(0f, y), Offset(size.width, y), 1.dp.toPx()); y += step } }
                .padding(horizontal = 6.dp),
        ) {
            val st = W95.text.copy(fontSize = 13.sp, lineHeight = 22.sp, color = Color(0xFF2B2410))
            if (note.text.isEmpty()) T("Write a note… it stays on this device.", color = Color(0x882B2410), modifier = Modifier.padding(top = 3.dp))
            BasicTextField(
                value = note.text,
                onValueChange = { state.stickies[current] = note.copy(text = it); state.saveStickies() },
                textStyle = st, cursorBrush = SolidColor(Color.Black),
                modifier = Modifier.fillMaxSize().padding(top = 3.dp),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                noteColors.forEach { (name, c) ->
                    Box(
                        Modifier.size(22.dp).then(if (name == note.color) Modifier.border(2.dp, Color(0xFF1F5C5C), CircleShape) else Modifier).padding(2.dp)
                            .background(Color(c), CircleShape).border(1.dp, Color(0x80000000), CircleShape)
                            .clickable { state.stickies[current] = note.copy(color = name); state.saveStickies() },
                    )
                }
            }
            Btn("+", {
                if (state.stickies.size < 8) {
                    state.stickies.add(StickyNote("", noteColors.keys.toList()[state.stickies.size % noteColors.size]))
                    current = state.stickies.size - 1; state.saveStickies()
                }
            }, Modifier.widthIn(min = 28.dp), bold = true)
            Btn("Delete", { if (state.stickies.size > 1) { com.reverender.r95.os.OfficeNotify.unpinNote(ctx, current); state.stickies.removeAt(current); current = (current - 1).coerceAtLeast(0); state.saveStickies() } }, enabled = state.stickies.size > 1, size = 11.sp)
        }
        // native upgrades: pin to the notification shade, share, copy
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Btn("📌 Pin", { com.reverender.r95.os.OfficeNotify.pinNote(ctx, current, note.text) }, Modifier.weight(1f), size = 11.sp)
            Btn("↗ Share", { shareText(ctx, note.text) }, Modifier.weight(1f), size = 11.sp, enabled = note.text.isNotBlank())
            Btn("Copy", { clip.setText(androidx.compose.ui.text.AnnotatedString(note.text)) }, Modifier.weight(1f), size = 11.sp, enabled = note.text.isNotBlank())
        }
    }
}

// ================================================================ PLANNER_95

private fun dayKey(c: Calendar) = "%04d-%02d-%02d".format(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))

/** PLANNER_95, ported from S(): month grid from Monday, a note per day (a dot marks days with plans). */
@Composable
fun PlannerApp() {
    val state = LocalAppState.current
    var month by remember { mutableStateOf(Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }) }
    var selected by remember { mutableStateOf(dayKey(Calendar.getInstance())) }
    val today = dayKey(Calendar.getInstance())
    val sel2 = Color(0xFFE07A9B); val sel1 = Color(0xFF1F5C5C)
    Column(Modifier.fillMaxSize().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Btn("◀", { month = (month.clone() as Calendar).apply { add(Calendar.MONTH, -1) } })
            T(SimpleDateFormat("LLLL yyyy", Locale.getDefault()).format(month.time).replaceFirstChar { it.uppercase() }, bold = true, size = 13.sp, align = TextAlign.Center, modifier = Modifier.weight(1f))
            Btn("▶", { month = (month.clone() as Calendar).apply { add(Calendar.MONTH, 1) } })
            Btn("Today", { month = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }; selected = today }, size = 11.sp)
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) { listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { T(it, size = 10.sp, bold = true, align = TextAlign.Center, color = W95.Text.copy(alpha = 0.7f), modifier = Modifier.weight(1f).padding(vertical = 2.dp)) } }
            val lead = (month.get(Calendar.DAY_OF_WEEK) + 5) % 7
            val days = month.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (row in 0 until (lead + days + 6) / 7) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    for (col in 0 until 7) {
                        val day = row * 7 + col - lead + 1
                        if (day < 1 || day > days) { Spacer(Modifier.weight(1f).height(26.dp)); continue }
                        val k = dayKey((month.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) })
                        val isSel = k == selected; val isToday = k == today
                        Box(
                            Modifier.weight(1f).height(26.dp).background(if (isSel) sel2 else W95.Light)
                                .then(if (isToday) Modifier.border(2.dp, sel1) else Modifier.drawBehind { drawLine(W95.Shadow, Offset(size.width, 0f), Offset(size.width, size.height), 1.dp.toPx()); drawLine(W95.Shadow, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx()) })
                                .clickable { selected = k },
                            contentAlignment = Alignment.Center,
                        ) {
                            val fg = if (isSel) Color.White else if (isToday) sel1 else W95.Text
                            T("$day", color = fg, size = 12.sp, bold = isToday)
                            if (state.planner.containsKey(k)) Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 3.dp).size(4.dp).background(fg, CircleShape))
                        }
                    }
                }
            }
        }
        val parts = selected.split("-").map { it.toInt() }
        val selCal = Calendar.getInstance().apply { set(parts[0], parts[1] - 1, parts[2]) }
        val pat = android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEEdMMMM")
        T(SimpleDateFormat(pat, Locale.getDefault()).format(selCal.time), bold = true, size = 11.sp)
        Field(state.planner[selected] ?: "", { state.setPlan(selected, it) }, Modifier.fillMaxWidth().weight(1f), singleLine = false, hint = "Plans for this day…")
        // native upgrades: a reminder notification at a time of day, or hand the plan to the phone's calendar
        val ctx = LocalContext.current
        val remKey = "planner_rem_$selected"
        val prefs = remember { ctx.getSharedPreferences("r95_office", android.content.Context.MODE_PRIVATE) }
        var remAt by remember(selected) { mutableStateOf(prefs.getString(remKey, "") ?: "") }
        var timeText by remember(selected) { mutableStateOf(remAt.ifEmpty { "09:00" }) }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            T("⏰", size = 13.sp)
            Field(timeText, { timeText = it.filter { c -> c.isDigit() || c == ':' }.take(5) }, Modifier.width(62.dp))
            Btn(if (remAt.isNotEmpty()) "Reminder $remAt ✕" else "Remind me", {
                val id = com.reverender.r95.os.OfficeNotify.dayId(selected)
                if (remAt.isNotEmpty()) { com.reverender.r95.os.OfficeNotify.unschedule(ctx, id); prefs.edit().remove(remKey).apply(); remAt = "" }
                else {
                    val hm = timeText.split(':').mapNotNull { it.toIntOrNull() }
                    val at = (selCal.clone() as Calendar).apply { set(Calendar.HOUR_OF_DAY, hm.getOrElse(0) { 9 }.coerceIn(0, 23)); set(Calendar.MINUTE, hm.getOrElse(1) { 0 }.coerceIn(0, 59)); set(Calendar.SECOND, 0) }
                    if (at.timeInMillis > System.currentTimeMillis()) {
                        com.reverender.r95.os.OfficeNotify.schedule(ctx, id, at.timeInMillis, "📅 PLANNER_95", state.planner[selected]?.ifBlank { null } ?: "Your plans for today", "planner")
                        remAt = "%02d:%02d".format(at.get(Calendar.HOUR_OF_DAY), at.get(Calendar.MINUTE)); prefs.edit().putString(remKey, remAt).apply()
                    }
                }
            }, Modifier.weight(1f), pressed = remAt.isNotEmpty(), size = 11.sp)
            Btn("📅 Calendar", {
                val start = (selCal.clone() as Calendar).apply { set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0) }
                val i = android.content.Intent(android.content.Intent.ACTION_INSERT).setData(android.provider.CalendarContract.Events.CONTENT_URI)
                    .putExtra(android.provider.CalendarContract.Events.TITLE, (state.planner[selected] ?: "Plans").lineSequence().first().take(60))
                    .putExtra(android.provider.CalendarContract.Events.DESCRIPTION, state.planner[selected] ?: "")
                    .putExtra(android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.timeInMillis)
                    .putExtra(android.provider.CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                runCatching { ctx.startActivity(i) }
            }, size = 11.sp)
        }
    }
}

// ================================================================ TASKS_95

/** TASKS_95, ported from Y(): add with Enter, tick off, × to remove, "Clear done". */
@Composable
fun TodoApp() {
    val state = LocalAppState.current
    var input by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf(-1) }
    val ctx = LocalContext.current
    fun add() {
        val t = input.trim(); if (t.isEmpty()) return
        if (editing in state.todos.indices) state.todos[editing] = state.todos[editing].copy(text = t.take(140)) else state.todos.add(TodoItem(t.take(140), false))
        editing = -1; state.saveTodos(); input = ""
    }
    Column(Modifier.fillMaxSize().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Field(input, { input = it.take(140) }, Modifier.weight(1f), hint = "Add a task and press Enter", onDone = ::add)
            Btn(if (editing >= 0) "Save" else "Add", ::add)
        }
        LazyColumn(Modifier.fillMaxWidth().weight(1f).background(Color.White).bevel(false).padding(4.dp)) {
            if (state.todos.isEmpty()) item { T("Nothing to do. Enjoy the vapor.", color = Color(0x99000000), size = 12.sp, align = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(10.dp)) }
            itemsIndexed(state.todos) { i, item ->
                Row(
                    Modifier.fillMaxWidth().drawBehind { drawLine(Color(0x33000000), Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2f, 3f))) }.padding(vertical = 4.dp, horizontal = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Check("", item.done, { state.todos[i] = item.copy(done = it); state.saveTodos() })
                    androidx.compose.foundation.text.BasicText(
                        item.text,
                        style = W95.text.copy(fontSize = 12.sp, color = if (item.done) Color(0x80000000) else Color.Black, textDecoration = if (item.done) TextDecoration.LineThrough else TextDecoration.None,
                            fontWeight = if (item.star) androidx.compose.ui.text.font.FontWeight.Bold else null),
                        modifier = Modifier.weight(1f).clickable { editing = i; input = item.text }.padding(horizontal = 6.dp),
                    )
                    // ★ priority: starred tasks float to the top
                    T(if (item.star) "★" else "☆", size = 15.sp, color = if (item.star) Color(0xFFE0A000) else Color(0x66000000), modifier = Modifier.clickable {
                        state.todos[i] = item.copy(star = !item.star)
                        val sorted = state.todos.sortedWith(compareBy({ it.done }, { !it.star }))
                        state.todos.clear(); state.todos.addAll(sorted); state.saveTodos()
                    }.padding(horizontal = 4.dp))
                    Btn("×", { state.todos.removeAt(i); state.saveTodos() }, size = 12.sp)
                }
            }
        }
        val left = state.todos.count { !it.done }
        Row(verticalAlignment = Alignment.CenterVertically) {
            T("$left to do · ${state.todos.size - left} done", size = 10.sp, modifier = Modifier.weight(1f))
            Btn("↗ Share", { shareText(ctx, state.todos.joinToString("\n") { (if (it.done) "☑ " else "☐ ") + (if (it.star) "★ " else "") + it.text }) }, size = 11.sp, enabled = state.todos.isNotEmpty())
            Spacer(Modifier.width(4.dp))
            Btn("Clear done", { state.todos.removeAll { it.done }; state.saveTodos() }, size = 11.sp)
        }
    }
}

// ================================================================ FOCUS_95

/** FOCUS_95, ported from ce(): focus / break timer, ring progress, chime, finished-sessions count. */
@Composable
fun FocusApp() {
    val state = LocalAppState.current
    var mode by remember { mutableStateOf("work") }
    var running by remember { mutableStateOf(false) }
    var remaining by remember { mutableStateOf(state.focusWork * 60.0) }
    var endsAt by remember { mutableLongStateOf(0L) }
    var workText by remember { mutableStateOf(state.focusWork.toString()) }
    var breakText by remember { mutableStateOf(state.focusBreak.toString()) }
    fun total() = (if (mode == "work") state.focusWork else state.focusBreak) * 60.0
    fun switch(m: String) { mode = m; running = false; remaining = total() }
    val fctx = LocalContext.current
    // native upgrade: a countdown in the notification shade and an alarm at the end, even if the app is closed
    LaunchedEffect(running, endsAt) {
        val nid = com.reverender.r95.os.OfficeNotify.FOCUS_ID
        if (running) {
            val label = if (mode == "work") "⏳ FOCUS_95 — focus" else "☕ FOCUS_95 — break"
            com.reverender.r95.os.OfficeNotify.show(fctx, nid + 1, label, "Ends at " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(java.util.Date(endsAt)), "focus", ongoing = true, countdownTo = endsAt)
            com.reverender.r95.os.OfficeNotify.schedule(fctx, nid, endsAt, if (mode == "work") "⏳ Focus done!" else "☕ Break over", if (mode == "work") "Time for a ${state.focusBreak}-minute break." else "Back to focus.", "focus")
        } else {
            com.reverender.r95.os.OfficeNotify.cancel(fctx, nid + 1); com.reverender.r95.os.OfficeNotify.unschedule(fctx, nid)
        }
    }
    LaunchedEffect(running) {
        while (running) {
            remaining = ((endsAt - System.currentTimeMillis()) / 1000.0).coerceAtLeast(0.0)
            if (remaining <= 0) {
                running = false; chime(); com.reverender.r95.os.Haptics.clunk("eject")
                com.reverender.r95.os.OfficeNotify.unschedule(fctx, com.reverender.r95.os.OfficeNotify.FOCUS_ID)
                if (mode == "work") state.addFocusDone()
                switch(if (mode == "work") "brk" else "work")
            }
            delay(250)
        }
    }
    /** ae(): parseInt and clamp on change. */
    fun commit() {
        val w = (workText.toIntOrNull() ?: 25).coerceIn(1, 120); val b = (breakText.toIntOrNull() ?: 5).coerceIn(1, 60)
        state.setFocus(w, b); workText = w.toString(); breakText = b.toString()
        if (!running) remaining = total()
    }
    Column(Modifier.fillMaxSize().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Btn("Focus", { switch("work") }, Modifier.weight(1f), pressed = mode == "work")
            Btn("Break", { switch("brk") }, Modifier.weight(1f), pressed = mode == "brk")
        }
        Box(Modifier.align(Alignment.CenterHorizontally).size(170.dp), contentAlignment = Alignment.Center) {
            val face = W95.Face
            Canvas(Modifier.fillMaxSize()) {
                val p = (1 - remaining / total().coerceAtLeast(1.0)).toFloat() * 360f
                drawCircle(Color(0x26000000))
                drawArc(if (mode == "work") Color(0xFFE07A9B) else Color(0xFF1F5C5C), -90f, p, true)
                drawCircle(face, size.minDimension / 2 * 0.78f)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val r = remaining
                T("%02d:%02d".format((r / 60).toInt(), (r % 60).toInt()), size = 32.sp, bold = true, mono = true)
                T(if (mode == "work") "F O C U S" else "B R E A K", size = 10.sp, bold = true, color = W95.Text.copy(alpha = 0.7f))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Btn(if (running) "Pause" else if (remaining < total()) "Resume" else "Start", {
                if (running) running = false else { endsAt = System.currentTimeMillis() + (remaining * 1000).toLong(); running = true }
            }, Modifier.weight(1f))
            Btn("Reset", { switch(mode) }, Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            T("Focus ", size = 11.sp); Field(workText, { workText = it.filter(Char::isDigit).take(3) }, Modifier.width(54.dp), onDone = ::commit)
            T(" min", size = 11.sp); Spacer(Modifier.width(10.dp))
            T("Break ", size = 11.sp); Field(breakText, { breakText = it.filter(Char::isDigit).take(2) }, Modifier.width(54.dp), onDone = ::commit)
            T(" min", size = 11.sp)
        }
        LaunchedEffect(workText, breakText) { delay(600); if (workText.isNotEmpty() && breakText.isNotEmpty()) commit() }
        OffStatus("${state.focusDone} focus sessions finished")
    }
}

/** Three-note sine chime (660, 880, 990 Hz) like the website's timer. */
private fun chime() {
    Thread {
        val sr = 22050
        val n = (sr * 1.1).toInt()
        val pcm = ShortArray(n)
        listOf(660.0, 880.0, 990.0).forEachIndexed { k, f ->
            val start = (k * 0.18 * sr).toInt()
            for (i in 0 until (0.55 * sr).toInt()) {
                val idx = start + i; if (idx >= n) break
                val t = i / sr.toDouble()
                val env = if (t < 0.02) t / 0.02 else kotlin.math.exp(-(t - 0.02) * 9)
                pcm[idx] = (pcm[idx] + kotlin.math.sin(2 * Math.PI * f * t) * env * 0.12 * 32767).toInt().coerceIn(-32768, 32767).toShort()
            }
        }
        try {
            val track = com.reverender.r95.audio.makeTrack(sr, false, false, android.media.AudioAttributes.USAGE_ALARM, android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION, n * 2, static = true)
            track.write(pcm, 0, n); track.play(); Thread.sleep(1300); track.release()
        } catch (_: Exception) { }
    }.start()
}

// ================================================================ CLOCK_GADGET

@Composable
fun ClockApp() {
    val state = LocalAppState.current
    val w95 = com.reverender.r95.ui.win95.Skin.classic
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(4.dp)) {
        // #rr-clock-theme-grid: the Windows 95 styles or the Longhorn faces
        androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxWidth()) {
            val cols = (maxWidth.value / 74f).toInt().coerceIn(1, 5)
            val items: List<Pair<String, String>> = if (w95) ClockFaces.w95 else ClockFaces.analog.map { it.id to it.name }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items.chunked(cols).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        row.forEach { (id, name) ->
                            val sel = if (w95) state.w95Clock == id else state.clockFace == id
                            Column(
                                Modifier.weight(1f).background(if (sel) W95.Face.copy(alpha = 0.8f) else W95.Face).bevel(!sel)
                                    .clickable { UiSfx.play(); if (w95) state.setW95Clock(id) else state.setClockFace(id) }.padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                if (w95) com.reverender.r95.ui.viz.W95ClockPreview(id, Modifier.size(width = 86.dp, height = 52.dp).then(if (sel) Modifier.border(2.dp, Color(0xFFFF00A0)) else Modifier))
                                else com.reverender.r95.ui.viz.AnalogClock(ClockFaces.face(id), live = false, Modifier.size(52.dp))
                                T(name, size = 11.sp, bold = w95, maxLines = 1)
                            }
                        }
                        repeat(cols - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
        T(
            if (w95) "Pick a style for the desktop clock. Each visit starts with the original Neon 1989 clock."
            else "Pick a face for the desktop clock. Click the clock any time to come back here.",
            size = 11.sp, bold = w95, modifier = Modifier.padding(top = 6.dp),
        )
    }
}

// ================================================================ WRITE_95

/** WRITE_95, ported from rr-os-styles.js a(): New (with "Sure? Click again"), Open…, Download .txt, Wrap, A−/A+. */
@Composable
fun NotepadApp() {
    val state = LocalAppState.current
    val context = LocalContext.current
    var armed by remember { mutableStateOf(false) }
    LaunchedEffect(armed) { if (armed) { delay(2500); armed = false } }
    LaunchedEffect(state.notepadName) { com.reverender.r95.ui.desktop.WindowTitles.override["notepad"] = "📝 WRITE_95.EXE — [${state.notepadName}]" }
    var docUri by remember { mutableStateOf<Uri?>(null) }
    var saved by remember { mutableStateOf("") }
    val openDoc = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            val text = context.contentResolver.openInputStream(uri)!!.bufferedReader().use { it.readText() }
            val name = runCatching { context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c -> if (c.moveToFirst()) c.getString(0) else null } }.getOrNull()
            state.setNotepad(text); state.setNotepadName(name ?: uri.lastPathSegment?.substringAfterLast('/') ?: "Untitled")
            docUri = uri
            runCatching { context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION) }
        }
    }
    val saveDoc = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching { context.contentResolver.openOutputStream(uri)!!.bufferedWriter().use { it.write(state.notepadText) } }
            .onSuccess { docUri = uri; saved = "Saved" }
    }
    var findOn by remember { mutableStateOf(false) }
    var find by remember { mutableStateOf("") }
    var repl by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Btn(if (armed) "Sure? Click again" else "New", {
                if (state.notepadText.isNotEmpty() && !armed) armed = true
                else { armed = false; state.setNotepadName("Untitled"); state.setNotepad("") }
            }, size = 11.sp)
            Btn("Open…", { openDoc.launch(arrayOf("text/plain", "text/markdown", "text/csv", "text/*")) }, size = 11.sp)
            Btn("Download .txt", {
                val n = state.notepadName
                saveDoc.launch(if (Regex("\\.\\w+$").containsMatchIn(n)) n else (if (n == "Untitled") "note" else n) + ".txt")
            }, size = 11.sp)
            // native upgrades
            Btn("Save", {
                val u = docUri
                if (u == null) saveDoc.launch(if (state.notepadName.contains('.')) state.notepadName else state.notepadName.replace("Untitled", "note") + ".txt")
                else saved = if (runCatching { context.contentResolver.openOutputStream(u, "wt")!!.bufferedWriter().use { it.write(state.notepadText) } }.isSuccess) "Saved" else "Save failed"
            }, size = 11.sp)
            Btn("Find", { findOn = !findOn }, pressed = findOn, size = 11.sp)
            Btn("Date", { state.setNotepad(state.notepadText + (if (state.notepadText.isEmpty() || state.notepadText.endsWith("\n")) "" else " ") + SimpleDateFormat("EEE d MMM yyyy HH:mm", Locale.getDefault()).format(java.util.Date())) }, size = 11.sp)
            Btn("↗ Share", { shareText(context, state.notepadText) }, size = 11.sp, enabled = state.notepadText.isNotBlank())
            Btn("Wrap", { state.setNotepadWrap(!state.notepadWrap) }, pressed = state.notepadWrap, size = 11.sp)
            Btn("A−", { state.setNotepadSize(state.notepadSize - 1) }, size = 11.sp)
            Btn("A+", { state.setNotepadSize(state.notepadSize + 1) }, size = 11.sp)
        }
        if (findOn) Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Field(find, { find = it }, Modifier.weight(1f), hint = "Find")
            Field(repl, { repl = it }, Modifier.weight(1f), hint = "Replace with")
            val hits = if (find.isEmpty()) 0 else Regex(Regex.escape(find), RegexOption.IGNORE_CASE).findAll(state.notepadText).count()
            T("$hits", size = 11.sp, mono = true)
            Btn("All", { if (find.isNotEmpty()) state.setNotepad(state.notepadText.replace(find, repl, ignoreCase = true)) }, size = 11.sp, enabled = hits > 0)
        }
        Box(
            Modifier.fillMaxWidth().weight(1f).background(Color.White).bevel(false).padding(6.dp)
                .let { if (state.notepadWrap) it else it.horizontalScroll(rememberScrollState()) },
        ) {
            if (state.notepadText.isEmpty()) T("Start typing — WRITE_95 saves as you go.", color = Color(0xFF9A9A9A), mono = true, size = state.notepadSize.sp)
            BasicTextField(
                value = state.notepadText,
                onValueChange = state::setNotepad,
                textStyle = W95.text.copy(fontFamily = W95.mono, fontSize = state.notepadSize.sp, color = Color.Black),
                cursorBrush = SolidColor(Color.Black),
                modifier = if (state.notepadWrap) Modifier.fillMaxSize() else Modifier.width(2000.dp).fillMaxSize(),
            )
        }
        val words = Regex("\\S+").findAll(state.notepadText).count()
        LaunchedEffect(state.notepadText) { saved = "" }
        OffStatus("$words words · ${state.notepadText.length} characters · " + (saved.ifEmpty { if (docUri != null) "kept on this device" else "saved on this device" }))
    }
}

/** .rr-off-status */
@Composable
private fun OffStatus(text: String) {
    Box(Modifier.fillMaxWidth().bevel(false).padding(horizontal = 6.dp, vertical = 2.dp)) { T(text, size = 10.sp) }
}

// ================================================================ APK_GET_95

@Composable
fun AndroidApp() {
    val context = LocalContext.current
    // the page's user-agent check, from this device's ABI and memory
    val (best, why) = remember {
        val abi = android.os.Build.SUPPORTED_ABIS.firstOrNull() ?: ""
        val am = context.getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val mi = android.app.ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }
        when {
            abi.startsWith("x86") -> "x86_64" to "Chromebook detected"
            abi.startsWith("armeabi") || mi.totalMem <= 2L * 1024 * 1024 * 1024 -> "armv7" to "Lower-end Android device detected"
            else -> "arm64" to "Android device detected"
        }
    }
    val opts = listOf(
        listOf("arm64", "🚀", "ARM64", "reverender_95.apk", "Most phones & tablets · high-end devices"),
        listOf("armv7", "🔋", "ARMv7", "reverender_95_low.apk", "Older & lower-end Android devices"),
        listOf("x86_64", "💻", "x86_64", "reverender_95_x86_64.apk", "Chromebooks & Android emulators"),
    )
    Column(Modifier.fillMaxSize().padding(4.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        com.reverender.r95.ui.win95.Lcd(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(4.dp)) {
                Row {
                    T("PACKAGE: REVERENDER_95.APK", color = Color.White, bold = true, mono = true, size = 11.sp, modifier = Modifier.weight(1f))
                    T("READY", color = Color(0xFF00FF66), bold = true, mono = true, size = 11.sp)
                }
                T("Take the full 90s Vaporwave Audio Deck, 18-Unit DSP Rack & 3D Visualizers native on your Android phone or tablet.", color = Color(0xFFFF66CC), bold = true, mono = true, size = 10.sp)
            }
        }
        com.reverender.r95.ui.win95.GroupBox("Choose your APK build") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                opts.forEach { (abi, ico, name, file, sub) ->
                    val hl = abi == best
                    Row(
                        Modifier.fillMaxWidth().background(if (hl) Color(0xFFE8F6EE) else W95.Face).bevel(true)
                            .then(if (hl) Modifier.border(2.dp, Color(0xFF2E9E5B)) else Modifier)
                            .clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://reverender.com/$file"))) }.padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        T(ico, size = 18.sp); Spacer(Modifier.width(6.dp))
                        Column(Modifier.weight(1f)) {
                            T(name, bold = true, size = 12.sp); T(file, size = 10.sp, mono = true); T(sub, size = 9.sp, color = W95.Shadow)
                        }
                        Box(Modifier.background(Color(0xFF2E9E5B)).padding(horizontal = 4.dp, vertical = 1.dp)) { T("RECOMMENDED", size = 7.sp, bold = true, color = Color.White) }
                    }
                }
                T("$why — the ${best.uppercase()} build is highlighted.", size = 10.sp, color = W95.Shadow)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            com.reverender.r95.ui.win95.StatusCell("PLATFORM: ANDROID APK", Modifier.weight(1f))
            com.reverender.r95.ui.win95.StatusCell("VER: 1995.1", Modifier.weight(1f))
        }
    }
}


/** Android's share sheet for a piece of text. */
internal fun shareText(ctx: android.content.Context, text: String) {
    val i = android.content.Intent(android.content.Intent.ACTION_SEND).setType("text/plain").putExtra(android.content.Intent.EXTRA_TEXT, text)
    runCatching { ctx.startActivity(android.content.Intent.createChooser(i, "Share").addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) }
}
