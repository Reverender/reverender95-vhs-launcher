package com.reverender.r95.os

import android.content.Context
import android.content.pm.PackageManager
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager
import android.media.midi.MidiOutputPort
import android.media.midi.MidiReceiver
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.reverender.r95.audio.SiteStudio
import com.reverender.r95.audio.StudioAudio
import com.reverender.r95.ui.viz.js.Dom
import com.reverender.r95.ui.viz.js.num
import com.reverender.r95.ui.viz.js.str

/**
 * USB / Bluetooth MIDI controllers (android.media.midi): knobs and faders (CC) move DSP_FX_95's sliders —
 * LPF cutoff, echo feedback, bitcrusher … through MIDI learn — and keys play VAPOR_SYNTH_95.
 * Messages arrive on a MIDI thread and are applied on the UI thread within a frame; the synth itself runs
 * on a low-latency audio track.
 */
object Midi {
    /** DSP_FX_95 parameters a knob can drive (input id suffix to label). */
    val PARAMS = listOf(
        "speed" to "TAPE SPEED", "lpf" to "LPF CUTOFF", "hpf" to "HPF CUTOFF", "dtime" to "ECHO TIME", "dmix" to "ECHO MIX",
        "dfdbk" to "ECHO FDBK", "drive" to "OVERDRIVE", "sub" to "SUB EXCITE", "chorus" to "CHORUS WOW", "tremolo" to "TREMOLO",
        "ring" to "RING MOD", "pan" to "STEREO PAN", "reverb" to "REVERB HALL", "crush" to "BITCRUSHER", "phaser" to "PHASER JET",
        "flanger" to "FLANGER", "width" to "3D WIDENER", "comp" to "COMP PUNCH", "cas-wow" to "WOW & FLUTTER",
    )

    val supported: Boolean get() = Build.VERSION.SDK_INT >= 23 &&
        runCatching { OsPrefs.app.packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI) }.getOrDefault(false)

    val devices = mutableStateListOf<String>()
    var lastEvent by mutableStateOf("NO MIDI YET"); private set
    /** parameter waiting for the next knob that moves (MIDI learn), or null */
    var learning by mutableStateOf<String?>(null)
    private val open = HashMap<Int, Pair<MidiDevice, MidiOutputPort>>()
    private var started = false
    private val main = android.os.Handler(android.os.Looper.getMainLooper())
    private val pending = java.util.concurrent.ConcurrentHashMap<String, Float>()
    @Volatile private var flushQueued = false

    fun map(): Map<Int, String> = OsPrefs.midiMap.split(',').mapNotNull { e ->
        val (k, v) = e.split('=').takeIf { it.size == 2 } ?: return@mapNotNull null
        k.trim().toIntOrNull()?.let { it to v.trim() }
    }.toMap()

    fun start(ctx: Context) {
        if (started || Build.VERSION.SDK_INT < 23 || !supported) return
        started = true
        val mm = ctx.getSystemService(Context.MIDI_SERVICE) as? MidiManager ?: return
        @Suppress("DEPRECATION") mm.devices.forEach { openDevice(mm, it) }
        @Suppress("DEPRECATION") mm.registerDeviceCallback(object : MidiManager.DeviceCallback() {
            override fun onDeviceAdded(device: MidiDeviceInfo) { openDevice(mm, device) }
            override fun onDeviceRemoved(device: MidiDeviceInfo) {
                open.remove(device.id)?.let { (d, p) -> runCatching { p.close(); d.close() } }
                refreshNames()
            }
        }, main)
    }

    private fun name(info: MidiDeviceInfo): String =
        info.properties.getString(MidiDeviceInfo.PROPERTY_NAME) ?: info.properties.getString(MidiDeviceInfo.PROPERTY_PRODUCT) ?: "MIDI device ${info.id}"

    private fun refreshNames() {
        devices.clear()
        open.keys.forEach { id -> open[id]?.first?.info?.let { devices.add(name(it)) } }
    }

    private fun openDevice(mm: MidiManager, info: MidiDeviceInfo) {
        if (info.outputPortCount == 0 || open.containsKey(info.id)) return
        mm.openDevice(info, { dev ->
            if (dev == null) return@openDevice
            val port = dev.openOutputPort(0) ?: return@openDevice
            port.connect(Parser)
            open[info.id] = dev to port
            refreshNames()
        }, main)
    }

    /** Splits the byte stream into messages (running status included). */
    private object Parser : MidiReceiver() {
        private var status = 0; private val data = IntArray(2); private var n = 0
        override fun onSend(msg: ByteArray, offset: Int, count: Int, timestamp: Long) {
            for (i in offset until offset + count) {
                val b = msg[i].toInt() and 0xFF
                if (b >= 0xF8) continue // clock / realtime
                if (b >= 0x80) { status = b; n = 0; continue }
                if (status == 0) continue
                data[n++] = b
                val need = when (status and 0xF0) { 0xC0, 0xD0 -> 1; else -> 2 }
                if (n >= need) { handle(status, data[0], data[1]); n = 0 }
            }
        }
    }

    private fun handle(st: Int, d1: Int, d2: Int) {
        when (st and 0xF0) {
            0x90 -> if (d2 > 0) note(d1, true) else note(d1, false)
            0x80 -> note(d1, false)
            0xB0 -> cc(d1, d2)
            0xE0 -> { // pitch bend → tape speed
                val v = ((d2 shl 7) or d1) / 16383f
                queue("speed", v)
            }
        }
    }

    private fun note(n: Int, down: Boolean) {
        StudioAudio.wake()
        StudioAudio.core.synth.press(n, down)
        main.post { lastEvent = "NOTE ${com.reverender.r95.audio.SynthParams.noteName(n)} ${if (down) "ON" else "OFF"}" }
    }

    private fun cc(c: Int, v: Int) {
        val learn = learning
        if (learn != null) {
            main.post {
                val m = map().toMutableMap().filterValues { it != learn }.toMutableMap(); m[c] = learn
                OsPrefs.setMidiMap(m.entries.joinToString(",") { "${it.key}=${it.value}" })
                learning = null
                lastEvent = "CC $c → ${PARAMS.firstOrNull { it.first == learn }?.second ?: learn}"
            }
            return
        }
        val p = map()[c] ?: run { main.post { lastEvent = "CC $c = $v (UNMAPPED)" }; return }
        queue(p, v / 127f)
    }

    /** Coalesces knob moves and applies them once per frame on the UI thread. */
    private fun queue(param: String, norm: Float) {
        pending[param] = norm
        if (flushQueued) return
        flushQueued = true
        main.post {
            flushQueued = false
            var fx = false; var cas = false
            for ((p, v) in pending.entries.toList()) {
                pending.remove(p)
                val id = if (p == "cas-wow") "cas-wow" else "fx-$p"
                val e = Dom.el(id)
                val min = num(e["min"]); val max = num(e["max"])
                if (min.isNaN() || max.isNaN()) continue
                // the LPF knob sweeps logarithmically, like a hardware filter
                val x = if (p == "lpf" || p == "hpf") min * Math.pow(max / min, v.toDouble()) else min + (max - min) * v
                e.setProp("value", str(Math.round(x * 1000) / 1000.0))
                if (p == "cas-wow") cas = true else fx = true
                lastEvent = "${PARAMS.firstOrNull { it.first == p }?.second ?: p}: ${(v * 100).toInt()}%"
            }
            runCatching { if (cas) SiteStudio.updateCassetteDsp() else if (fx) SiteStudio.updateDspFx() }
            com.reverender.r95.ui.viz.site.SiteHost.uiNow()
        }
    }
}
