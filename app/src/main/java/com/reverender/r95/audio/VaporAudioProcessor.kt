package com.reverender.r95.audio

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import com.reverender.r95.core.Catalog
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Sits inside ExoPlayer's audio sink. Every block of decoded 16-bit PCM passes through here:
 * the 8-band EQ is applied in place, then the result is handed to the spectrum analyser
 * that drives all the visualisers. Other PCM encodings pass through untouched.
 */
@OptIn(UnstableApi::class)
class VaporAudioProcessor : BaseAudioProcessor() {
    private var eq: EqBank? = null
    private var fx: FxChain? = null
    private var channels = 2
    private var scratch = ShortArray(0)
    private var floats = FloatArray(0)

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT || inputAudioFormat.channelCount !in 1..2) {
            eq = null
            return AudioProcessor.AudioFormat.NOT_SET // inactive: audio passes straight through
        }
        channels = inputAudioFormat.channelCount
        eq = EqBank(inputAudioFormat.sampleRate, channels, Catalog.eqFrequencies)
        fx = FxChain(inputAudioFormat.sampleRate)
        AudioBus.analyzer.configure(inputAudioFormat.sampleRate)
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val bank = eq
        val bytes = inputBuffer.remaining()
        if (bytes == 0) return
        val frameBytes = 2 * channels
        val frames = bytes / frameBytes
        val samples = frames * channels
        if (scratch.size < samples) scratch = ShortArray(samples)

        inputBuffer.order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until samples) scratch[i] = inputBuffer.short
        // Consume any trailing partial frame so the sink never stalls on it.
        inputBuffer.position(inputBuffer.limit())

        val inGain = AudioBus.inputGain
        if (inGain < 0.99f || inGain > 1.01f) {
            for (i in 0 until samples) scratch[i] = (scratch[i] * inGain).toInt().coerceIn(-32768, 32767).toShort()
        }
        if (bank != null && !GlobalEq.globalActive) {
            bank.syncWith(AudioBus.eq)
            bank.process(scratch, frames)
        }
        val chain = fx
        if (chain != null && AudioBus.fx.anyActive()) {
            // DSP_FX_95 + CASSETTE_95 work in stereo float; mono sources are widened and folded back.
            if (floats.size < frames * 2) floats = FloatArray(frames * 2)
            for (f in 0 until frames) {
                val l = scratch[f * channels] / 32768f
                floats[f * 2] = l
                floats[f * 2 + 1] = if (channels > 1) scratch[f * channels + 1] / 32768f else l
            }
            chain.process(floats, frames, AudioBus.fx)
            for (f in 0 until frames) {
                val l = softClip(floats[f * 2]); val r = softClip(floats[f * 2 + 1])
                if (channels > 1) {
                    scratch[f * 2] = (l * 32767f).toInt().toShort(); scratch[f * 2 + 1] = (r * 32767f).toInt().toShort()
                } else scratch[f] = ((l + r) * 0.5f * 32767f).toInt().toShort()
            }
        }
        if (AudioBus.analysisWanted) AudioBus.analyzer.feed(scratch, frames, channels)

        val out = replaceOutputBuffer(samples * 2)
        out.order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until samples) out.putShort(scratch[i])
        out.flip()
    }

    private fun softClip(x: Float): Float = if (x > 0.9f || x < -0.9f) kotlin.math.tanh(x.toDouble()).toFloat() else x

    override fun onFlush() {
        eq?.reset()
        fx?.reset()
        AudioBus.analyzer.clear()
    }

    override fun onReset() {
        eq = null
        fx = null
        scratch = ShortArray(0)
    }
}
