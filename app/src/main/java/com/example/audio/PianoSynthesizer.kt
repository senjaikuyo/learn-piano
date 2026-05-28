package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class PianoSynthesizer {

    private val sampleRate = 22050 // Optimized sample rate for memory and responsiveness
    private val scope = CoroutineScope(Dispatchers.Default)

    // Instrument Types
    enum class InstrumentType {
        GRAND_PIANO,
        UPRIGHT_PIANO,
        ELECTRIC_PIANO,
        ORGAN
    }

    private var activeInstrument = InstrumentType.GRAND_PIANO
    private var volumeFactor = 1.0f // Slider 0.0 - 1.0
    private var reverbEnabled = false

    fun setInstrument(type: InstrumentType) {
        activeInstrument = type
    }

    fun setVolume(volume: Float) {
        volumeFactor = volume.coerceIn(0.0f, 1.0f)
    }

    fun setReverbEnabled(enabled: Boolean) {
        reverbEnabled = enabled
    }

    // Play a midi note by frequency
    fun playNote(midiNote: Int) {
        val frequency = midiNoteToFrequency(midiNote)
        scope.launch {
            try {
                synthesizeAndPlay(frequency, activeInstrument)
            } catch (e: Exception) {
                Log.e("PianoSynthesizer", "Error playing note $midiNote ($frequency Hz)", e)
            }
        }
    }

    private fun midiNoteToFrequency(note: Int): Double {
        return 440.0 * Math.pow(2.0, (note - 69) / 12.0)
    }

    private fun synthesizeAndPlay(frequency: Double, instrument: InstrumentType) {
        // Duration of sound based on instrument profile
        val durationSecs = when (instrument) {
            InstrumentType.ORGAN -> 1.5
            InstrumentType.ELECTRIC_PIANO -> 1.2
            else -> 1.8 // Grand / Upright pianos decay slower
        }
        
        val totalSamples = (sampleRate * durationSecs).toInt()
        val buffer = ShortArray(totalSamples)

        val omega1 = 2.0 * Math.PI * frequency / sampleRate
        val omega2 = 2.0 * Math.PI * (frequency * 2.0) / sampleRate
        val omega3 = 2.0 * Math.PI * (frequency * 3.0) / sampleRate

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            
            // Generate harmonics and apply ADSR envelopment
            val sampleVal = when (instrument) {
                InstrumentType.GRAND_PIANO -> {
                    // String decay is exponential with warm 2nd harmonic
                    val fundamental = sin(omega1 * i)
                    val secondHarmonic = sin(omega2 * i) * 0.25
                    val decay = Math.exp(-2.2 * t)
                    (fundamental + secondHarmonic) * decay
                }
                InstrumentType.UPRIGHT_PIANO -> {
                    // More bright, punchy mid-to-high harmonics
                    val fundamental = sin(omega1 * i)
                    val secondHarmonic = sin(omega2 * i) * 0.35
                    val thirdHarmonic = sin(omega3 * i) * 0.15
                    val decay = Math.exp(-2.8 * t)
                    (fundamental + secondHarmonic + thirdHarmonic) * decay
                }
                InstrumentType.ELECTRIC_PIANO -> {
                    // Soft FM-like, fundamental decays nicely, sine chime starts fast
                    val fundamental = sin(omega1 * i) * 0.8
                    val chime = sin(omega2 * i) * 0.5 * Math.exp(-12.0 * t) // sharp decay hammer strike
                    val decay = Math.exp(-1.5 * t)
                    (fundamental + chime) * decay
                }
                InstrumentType.ORGAN -> {
                    // Full pipes, slow decay, strong fundamental and odd harmonics
                    val fundamental = sin(omega1 * i) * 0.6
                    val thirdHarmonic = sin(omega3 * i) * 0.3
                    val envelope = if (t < 0.1) {
                        t / 0.1 // Attack
                    } else if (t > durationSecs - 0.2) {
                        (durationSecs - t) / 0.2 // Release
                    } else {
                        0.9 // Sustain
                    }
                    (fundamental + thirdHarmonic) * envelope
                }
            }

            // Apply Reverb (very simple feedback delay effect)
            var finalSample = sampleVal
            if (reverbEnabled && i > 1500) {
                // Add minor echo from 1500 samples ago
                finalSample += buffer[i - 1500] * 0.25
            }

            // Master volume scaler and safety clippage
            val scaledSample = (finalSample * Short.MAX_VALUE * volumeFactor * 0.65).toInt()
            buffer[i] = scaledSample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        // Initialize low-latency AudioTrack and stream our procedurally generated sound array
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
        
        // Clean up when static play finishes
        scope.launch {
            try {
                kotlinx.coroutines.delay((durationSecs * 1000).toLong() + 500)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                // AudioTrack already released
            }
        }
    }
}
