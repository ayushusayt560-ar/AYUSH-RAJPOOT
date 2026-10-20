package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class MusicTheme {
    LOADING_ARPEGGIO,
    HOME_AMBIENT,
    VICTORY_FANFARE
}

/**
 * Cheerful, bright music-box & celesta melody engine for Loading & Home screens.
 * Uses high-register bells and bright major pentatonic tones (no scary low drones).
 * 100% offline, zero asset dependencies.
 */
class BackgroundMusicManager {

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val sampleRate = 44100
    @Volatile
    private var isPlaying = false
    @Volatile
    private var isMuted = false
    @Volatile
    private var currentTheme: MusicTheme = MusicTheme.HOME_AMBIENT

    fun playVictoryJingle() {
        stop()
        currentTheme = MusicTheme.VICTORY_FANFARE
        isPlaying = true

        playbackJob = scope.launch {
            try {
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = minBufferSize.coerceAtLeast(sampleRate * 2)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack = track
                track.play()
                runVictoryFanfare(track)
            } catch (_: Exception) {}
        }
    }

    fun playTheme(theme: MusicTheme, muted: Boolean = false) {
        isMuted = muted
        if (isPlaying && currentTheme == theme) return

        stop()
        currentTheme = theme
        isPlaying = true

        playbackJob = scope.launch {
            try {
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = minBufferSize.coerceAtLeast(sampleRate * 2)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack = track
                track.play()

                when (theme) {
                    MusicTheme.LOADING_ARPEGGIO -> runCheerfulArpeggio(track)
                    MusicTheme.HOME_AMBIENT -> runCheerfulMusicBox(track)
                    MusicTheme.VICTORY_FANFARE -> runVictoryFanfare(track)
                }
            } catch (_: Exception) {
                // Silently fallback if device audio service is busy
            }
        }
    }

    /**
     * Sweet, cheerful music-box melody in bright C/G Major.
     * Crisp, bell-like chimes without low spooky drones.
     */
    private fun runCheerfulMusicBox(track: AudioTrack) {
        // Cheerful melodic sequence (note frequencies in Hz + duration in seconds)
        // C5=523.25, D5=587.33, E5=659.25, G5=783.99, A5=880.00, B5=987.77, C6=1046.50
        val melody = listOf(
            Pair(523.25, 0.28), // C5
            Pair(659.25, 0.28), // E5
            Pair(783.99, 0.28), // G5
            Pair(659.25, 0.28), // E5
            Pair(880.00, 0.35), // A5
            Pair(783.99, 0.55), // G5
            Pair(0.0, 0.15),    // rest

            Pair(587.33, 0.28), // D5
            Pair(739.99, 0.28), // F#5
            Pair(880.00, 0.28), // A5
            Pair(739.99, 0.28), // F#5
            Pair(987.77, 0.35), // B5
            Pair(880.00, 0.55), // A5
            Pair(0.0, 0.15),    // rest

            Pair(659.25, 0.28), // E5
            Pair(783.99, 0.28), // G5
            Pair(1046.50, 0.45),// C6
            Pair(987.77, 0.28), // B5
            Pair(783.99, 0.28), // G5
            Pair(1046.50, 0.70),// C6
            Pair(0.0, 0.40)     // pleasant pause before loop
        )

        while (isPlaying && playbackJob?.isActive == true) {
            for ((freq, duration) in melody) {
                if (!isPlaying || playbackJob?.isActive != true) break

                val sampleCount = (sampleRate * duration).toInt()
                val buffer = ShortArray(sampleCount * 2)

                if (freq <= 0.0 || isMuted) {
                    track.write(buffer, 0, buffer.size)
                    continue
                }

                for (i in 0 until sampleCount) {
                    val t = i.toDouble() / sampleRate
                    // Music box bell envelope: instant percussive attack (0.004s) + bright exponential decay
                    val attack = 0.005
                    val env = if (t < attack) {
                        t / attack
                    } else {
                        exp(-(t - attack) * 5.0)
                    }

                    // Pure chime harmonics: fundamental sine + soft bell octave & 3rd harmonic
                    val s1 = sin(2.0 * PI * freq * t)
                    val s2 = sin(2.0 * PI * (freq * 2.0) * t) * 0.35
                    val s3 = sin(2.0 * PI * (freq * 3.0) * t) * 0.12

                    val sampleVal = (s1 + s2 + s3) * env * 4500.0
                    val shortVal = sampleVal.coerceIn(-28000.0, 28000.0).toInt().toShort()

                    // Subtle stereo widening
                    buffer[i * 2] = shortVal
                    buffer[i * 2 + 1] = shortVal
                }

                track.write(buffer, 0, buffer.size)
            }
        }
    }

    /**
     * Bright sparkling loading arpeggio.
     */
    private fun runCheerfulArpeggio(track: AudioTrack) {
        val notes = listOf(
            Pair(523.25, 0.22), // C5
            Pair(659.25, 0.22), // E5
            Pair(783.99, 0.22), // G5
            Pair(880.00, 0.22), // A5
            Pair(1046.50, 0.40),// C6
            Pair(0.0, 0.10)
        )

        while (isPlaying && playbackJob?.isActive == true) {
            for ((freq, duration) in notes) {
                if (!isPlaying || playbackJob?.isActive != true) break

                val sampleCount = (sampleRate * duration).toInt()
                val buffer = ShortArray(sampleCount * 2)

                if (freq <= 0.0 || isMuted) {
                    track.write(buffer, 0, buffer.size)
                    continue
                }

                for (i in 0 until sampleCount) {
                    val t = i.toDouble() / sampleRate
                    val attack = 0.005
                    val env = if (t < attack) {
                        t / attack
                    } else {
                        exp(-(t - attack) * 6.5)
                    }

                    val s1 = sin(2.0 * PI * freq * t)
                    val s2 = sin(2.0 * PI * (freq * 2.0) * t) * 0.3
                    val sampleVal = (s1 + s2) * env * 4800.0
                    val shortVal = sampleVal.coerceIn(-30000.0, 30000.0).toInt().toShort()

                    buffer[i * 2] = shortVal
                    buffer[i * 2 + 1] = shortVal
                }

                track.write(buffer, 0, buffer.size)
            }
        }
    }

    /**
     * Triumphant, celebratory victory fanfare played upon level completion.
     * Ascending energetic chimes resolving to a brilliant sparkling major chord.
     */
    private fun runVictoryFanfare(track: AudioTrack) {
        // Celebratory melodic sequence:
        // G4 -> C5 -> E5 -> G5 -> A5 -> B5 -> High C6 Chord!
        val fanfare = listOf(
            Pair(listOf(392.00), 0.11),              // G4
            Pair(listOf(523.25), 0.11),              // C5
            Pair(listOf(659.25), 0.11),              // E5
            Pair(listOf(783.99), 0.16),              // G5
            Pair(listOf(880.00), 0.13),              // A5
            Pair(listOf(987.77), 0.14),              // B5
            Pair(listOf(523.25, 659.25, 783.99, 1046.50), 0.85) // C-E-G-C6 Victory Chord!
        )

        for ((chord, duration) in fanfare) {
            if (!isPlaying || playbackJob?.isActive != true) break

            val sampleCount = (sampleRate * duration).toInt()
            val buffer = ShortArray(sampleCount * 2)

            if (isMuted) {
                track.write(buffer, 0, buffer.size)
                continue
            }

            for (i in 0 until sampleCount) {
                val t = i.toDouble() / sampleRate
                val attack = 0.006
                val env = if (t < attack) {
                    t / attack
                } else {
                    exp(-(t - attack) * 3.8)
                }

                var sampleVal = 0.0
                chord.forEach { freq ->
                    val s1 = sin(2.0 * PI * freq * t)
                    val s2 = sin(2.0 * PI * (freq * 2.0) * t) * 0.35
                    sampleVal += (s1 + s2)
                }

                val gain = 5500.0 * env / chord.size.coerceAtLeast(1)
                val shortVal = (sampleVal * gain).coerceIn(-30000.0, 30000.0).toInt().toShort()

                buffer[i * 2] = shortVal
                buffer[i * 2 + 1] = shortVal
            }

            track.write(buffer, 0, buffer.size)
        }
    }

    fun setMuted(muted: Boolean) {
        isMuted = muted
    }

    fun isMuted(): Boolean = isMuted

    fun stop() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
