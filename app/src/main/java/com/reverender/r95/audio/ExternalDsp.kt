package com.reverender.r95.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.reverender.r95.core.Catalog
import com.reverender.r95.os.OsPrefs

/**
 * "Process external apps": other apps' captured playback (Android 10+ playback capture) runs through the
 * same GRAPHIC_EQ + 18-unit DSP_FX_95 + CASSETTE chain as VAPOR_AMP and is played back here.
 * Android has no way to silence the source app, so this is a processed copy layered on the original —
 * best with that app's volume turned down, or for effects that add to the sound (Slow+Rev, Mall PA, echo).
 */
class ExternalDsp(private val sr: Int, private val channels: Int) {
    private val eq = EqBank(sr, channels, Catalog.eqFrequencies)
    private val fx = FxChain(sr)
    private var track: AudioTrack? = null
    private var floats = FloatArray(0)
    private var out = ShortArray(0)

    fun process(pcm: ShortArray, frames: Int) {
        if (!runCatching { OsPrefs.dspMonitor }.getOrDefault(false)) { stopTrack(); return }
        val t = track ?: startTrack() ?: return
        val samples = frames * channels
        if (out.size < samples) out = ShortArray(samples)
        System.arraycopy(pcm, 0, out, 0, samples)
        eq.syncWith(AudioBus.eq)
        eq.process(out, frames)
        if (AudioBus.fx.anyActive()) {
            if (floats.size < frames * 2) floats = FloatArray(frames * 2)
            for (f in 0 until frames) {
                val l = out[f * channels] / 32768f
                floats[f * 2] = l; floats[f * 2 + 1] = if (channels > 1) out[f * channels + 1] / 32768f else l
            }
            fx.process(floats, frames, AudioBus.fx)
            for (f in 0 until frames) {
                val l = kotlin.math.tanh(floats[f * 2].toDouble()).toFloat(); val r = kotlin.math.tanh(floats[f * 2 + 1].toDouble()).toFloat()
                if (channels > 1) { out[f * 2] = (l * 32767).toInt().toShort(); out[f * 2 + 1] = (r * 32767).toInt().toShort() }
                else out[f] = ((l + r) * 0.5f * 32767).toInt().toShort()
            }
        }
        runCatching { t.write(out, 0, samples) }
    }

    private fun startTrack(): AudioTrack? {
        val mask = if (channels > 1) AudioFormat.CHANNEL_OUT_STEREO else AudioFormat.CHANNEL_OUT_MONO
        val min = AudioTrack.getMinBufferSize(sr, mask, AudioFormat.ENCODING_PCM_16BIT)
        val t = runCatching {
            AudioTrack(
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build(),
                AudioFormat.Builder().setSampleRate(sr).setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(mask).build(),
                maxOf(min, 8192), AudioTrack.MODE_STREAM, android.media.AudioManager.AUDIO_SESSION_ID_GENERATE,
            ).also { it.play() }
        }.getOrNull()
        track = t
        return t
    }

    private fun stopTrack() { track?.let { runCatching { it.stop() }; it.release() }; track = null }

    fun release() = stopTrack()
}
