package com.example.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var musicJob: Job? = null
    var soundEnabled: Boolean = true
    var musicEnabled: Boolean = true
    private var currentEraIndex: Int = 1

    fun updateEra(eraIndex: Int) {
        currentEraIndex = eraIndex
    }

    fun playTapSound() {
        if (!soundEnabled) return
        scope.launch {
            generateBeep(freq = 520.0 + (currentEraIndex * 35.0), durationMs = 45, volume = 0.45f, decay = true)
        }
    }

    fun playUpgradeSound() {
        if (!soundEnabled) return
        scope.launch {
            // Harmonic arpeggio (C - E - G - C)
            val baseFreq = 440.0
            generateBeep(baseFreq, 60, 0.4f)
            delay(40)
            generateBeep(baseFreq * 1.25, 60, 0.4f)
            delay(40)
            generateBeep(baseFreq * 1.5, 90, 0.45f)
        }
    }

    fun playEraFanfare() {
        if (!soundEnabled) return
        scope.launch {
            // Triumphant chord fanfare
            val root = 392.0 // G4
            generateBeep(root, 100, 0.5f)
            delay(80)
            generateBeep(root * 1.25, 100, 0.5f)
            delay(80)
            generateBeep(root * 1.5, 120, 0.5f)
            delay(100)
            generateBeep(root * 2.0, 300, 0.6f, decay = true)
        }
    }

    fun playExtinctionSound(type: String) {
        if (!soundEnabled) return
        scope.launch {
            when {
                type.contains("Ascension", ignoreCase = true) -> {
                    // Heavenly ascending chord
                    val chord = listOf(523.25, 659.25, 783.99, 1046.50)
                    for (f in chord) {
                        generateBeep(f, 400, 0.35f, decay = true)
                        delay(70)
                    }
                }
                type.contains("Nuclear", ignoreCase = true) || type.contains("Meteor", ignoreCase = true) -> {
                    // Low dramatic rumble
                    generateNoiseRumble(durationMs = 600, volume = 0.6f)
                }
                else -> {
                    // Descending glitch
                    var freq = 600.0
                    repeat(8) {
                        generateBeep(freq, 40, 0.4f)
                        freq *= 0.82
                        delay(35)
                    }
                }
            }
        }
    }

    fun playEarthquakeSound() {
        if (!soundEnabled) return
        scope.launch {
            generateNoiseRumble(durationMs = 450, volume = 0.5f)
        }
    }

    fun playAchievementSound() {
        if (!soundEnabled) return
        scope.launch {
            // Triumphant arpeggio: C5 -> E5 -> G5 -> C6
            generateBeep(523.25, 70, 0.45f)
            delay(80)
            generateBeep(659.25, 70, 0.45f)
            delay(80)
            generateBeep(783.99, 70, 0.45f)
            delay(80)
            generateBeep(1046.50, 220, 0.55f, decay = true)
        }
    }

    fun playGemSound() {
        if (!soundEnabled) return
        scope.launch {
            // Crystalline bell shimmer (A5 -> E6 -> A6)
            generateBeep(880.0, 50, 0.35f, decay = true)
            delay(50)
            generateBeep(1318.51, 60, 0.4f, decay = true)
            delay(50)
            generateBeep(1760.0, 180, 0.45f, decay = true)
        }
    }

    fun playButtonTapSound() {
        if (!soundEnabled) return
        scope.launch {
            generateBeep(freq = 620.0, durationMs = 35, volume = 0.35f, decay = true)
        }
    }

    fun playSwitchJarSound() {
        if (!soundEnabled) return
        scope.launch {
            // Distinctive glass slide & chime sound
            generateBeep(587.33, 50, 0.35f) // D5
            delay(40)
            generateBeep(880.00, 120, 0.45f, decay = true) // A5
        }
    }

    fun playPurchaseSuccessSound() {
        if (!soundEnabled) return
        scope.launch {
            // Ascending major chime (F5 -> A5 -> C6 -> F6)
            val base = 698.46
            generateBeep(base, 60, 0.4f)
            delay(50)
            generateBeep(base * 1.2599, 60, 0.4f)
            delay(50)
            generateBeep(base * 1.4983, 70, 0.45f)
            delay(60)
            generateBeep(base * 2.0, 240, 0.5f, decay = true)
        }
    }

    fun playShieldSound() {
        if (!soundEnabled) return
        scope.launch {
            // Protective resonant forcefield
            generateBeep(440.0, 120, 0.4f)
            delay(80)
            generateBeep(880.0, 280, 0.5f, decay = true)
        }
    }

    fun startAmbientMusic() {
        musicJob?.cancel()
        musicJob = scope.launch {
            while (isActive) {
                if (musicEnabled) {
                    val baseFreq = when (currentEraIndex) {
                        1 -> 146.83 // D3 primordial deep
                        2 -> 164.81 // E3
                        3 -> 174.61 // F3 tribal warmth
                        4 -> 196.00 // G3 agricultural
                        5 -> 220.00 // A3 city
                        6 -> 246.94 // B3 industrial
                        7 -> 261.63 // C4 digital
                        8 -> 293.66 // D4 space
                        else -> 329.63 // E4 ascension
                    }

                    // Play gentle ambient chord pad
                    generateChordPad(baseFreq, durationMs = 2800, volume = 0.15f)
                    delay(3200)
                } else {
                    delay(1000)
                }
            }
        }
    }

    fun stopAmbientMusic() {
        musicJob?.cancel()
        musicJob = null
    }

    fun destroy() {
        stopAmbientMusic()
        scope.launch {
            // Cancel background tasks
        }
    }

    private suspend fun generateBeep(freq: Double, durationMs: Int, volume: Float, decay: Boolean = false) {
        try {
            val sampleRate = 22050
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                var amplitude = volume
                if (decay) {
                    amplitude *= (1.0f - (i.toFloat() / numSamples))
                }
                val sample = (sin(2.0 * PI * freq * t) * amplitude * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
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
            delay(durationMs.toLong() + 20)
            try { audioTrack.stop() } catch (_: Exception) {}
            try { audioTrack.release() } catch (_: Exception) {}
        } catch (_: Exception) {
            // Audio failure fallback safe
        }
    }

    private suspend fun generateChordPad(rootFreq: Double, durationMs: Int, volume: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)
            val freqs = listOf(rootFreq, rootFreq * 1.2599, rootFreq * 1.4983) // Root, major 3rd, 5th

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                // Attack & release envelope
                val progress = i.toFloat() / numSamples
                val envelope = when {
                    progress < 0.2f -> progress / 0.2f
                    progress > 0.8f -> (1.0f - progress) / 0.2f
                    else -> 1.0f
                }
                var mixed = 0.0
                for (f in freqs) {
                    mixed += sin(2.0 * PI * f * t)
                }
                mixed = (mixed / freqs.size) * envelope * volume * Short.MAX_VALUE
                buffer[i] = mixed.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

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
            delay(durationMs.toLong() + 20)
            try { audioTrack.stop() } catch (_: Exception) {}
            try { audioTrack.release() } catch (_: Exception) {}
        } catch (_: Exception) {
            // Audio playback safely ignored
        }
    }

    private suspend fun generateNoiseRumble(durationMs: Int, volume: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)
            val random = java.util.Random()

            for (i in 0 until numSamples) {
                val envelope = 1.0f - (i.toFloat() / numSamples)
                val noise = (random.nextFloat() * 2.0f - 1.0f) * volume * envelope * Short.MAX_VALUE
                buffer[i] = noise.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
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
            delay(durationMs.toLong() + 20)
            try { audioTrack.stop() } catch (_: Exception) {}
            try { audioTrack.release() } catch (_: Exception) {}
        } catch (_: Exception) {
            // Fallback safe
        }
    }
}
