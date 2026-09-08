package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * High-quality real-time procedural audio engine for relaxing wind sounds,
 * harmonic ambient soundscapes (432Hz tuning), crystalline chimes, and bubble pops.
 */
class RelaxingAudioEngine {

    private val sampleRate = 44100
    private val bufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    ).coerceAtLeast(4096)

    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val audioScope = CoroutineScope(Dispatchers.Default)

    @Volatile
    var isMuted: Boolean = false

    @Volatile
    var isWindEnabled: Boolean = true

    @Volatile
    var isMusicEnabled: Boolean = true

    @Volatile
    var isLofiEnabled: Boolean = true

    @Volatile
    var isRainActive: Boolean = false

    // Dynamic wind parameters influenced by flyer speed and altitude
    @Volatile
    var flightSpeedFactor: Float = 0.3f // 0f to 1f

    @Volatile
    var altitudeProgress: Float = 0f // 0f to 1f

    private class ActiveTone(
        val frequency: Float,
        val maxDurationSamples: Int,
        var currentSample: Int = 0,
        val baseVolume: Float = 0.4f,
        val isBubble: Boolean = false,
        val isFloral: Boolean = false,
        val isPortal: Boolean = false
    )

    private val activeTones = ConcurrentLinkedQueue<ActiveTone>()

    // Pentatonic scale frequencies based on 432Hz calming tuning
    // C5 ~ 513.7Hz, D5 ~ 577.2Hz, E5 ~ 648.0Hz, G5 ~ 769.7Hz, A5 ~ 864.0Hz, C6 ~ 1027.4Hz
    private val pentatonicNotes = floatArrayOf(
        513.7f, 577.2f, 648.0f, 769.7f, 864.0f, 1027.4f, 1154.4f, 1296.0f
    )

    // Lo-Fi Electric Piano Chords (7th and 9th voicings)
    // 4 chords: Dm9, G13, Cmaj9, Am9
    private val lofiChords = arrayOf(
        floatArrayOf(146.8f, 174.6f, 220.0f, 261.6f, 329.6f), // Dm9
        floatArrayOf(98.0f, 174.6f, 246.9f, 329.6f),          // G13
        floatArrayOf(130.8f, 164.8f, 196.0f, 246.9f, 293.7f), // Cmaj9
        floatArrayOf(110.0f, 164.8f, 196.0f, 246.9f, 293.7f)  // Am9
    )

    fun start() {
        if (synthJob != null) return

        try {
            audioTrack = AudioTrack.Builder()
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
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()

            synthJob = audioScope.launch {
                runAudioLoop()
            }
        } catch (e: Exception) {
            Log.e("RelaxingAudioEngine", "Error initializing AudioTrack: ${e.message}")
        }
    }

    fun stop() {
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }

    /**
     * Trigger a crystalline chime note when collecting a light orb.
     * Scale index advances with combos.
     */
    fun playLightChime(comboIndex: Int = 0) {
        if (isMuted) return
        val noteIdx = (comboIndex % pentatonicNotes.size).coerceIn(0, pentatonicNotes.lastIndex)
        val freq = pentatonicNotes[noteIdx]
        val durationSamples = (sampleRate * 1.8f).toInt() // Long, soothing tail
        activeTones.add(ActiveTone(freq, durationSamples, baseVolume = 0.35f, isBubble = false))
    }

    /**
     * Trigger a watery, soft pop when collecting a bubble.
     */
    fun playBubblePop() {
        if (isMuted) return
        val freq = 440f + Random.nextFloat() * 120f
        val durationSamples = (sampleRate * 0.45f).toInt()
        activeTones.add(ActiveTone(freq, durationSamples, baseVolume = 0.3f, isBubble = true))
    }

    /**
     * Trigger a sparkling floral chime when collecting a celestial flower.
     */
    fun playFlowerChime() {
        if (isMuted) return
        val durationSamples = (sampleRate * 2.2f).toInt()
        activeTones.add(ActiveTone(659.25f, durationSamples, baseVolume = 0.35f, isFloral = true))
        activeTones.add(ActiveTone(880.0f, durationSamples, baseVolume = 0.28f, isFloral = true))
        activeTones.add(ActiveTone(1108.7f, durationSamples, baseVolume = 0.22f, isFloral = true))
    }

    /**
     * Trigger a mystical cosmic portal warp sound when traveling between dimensions.
     */
    fun playPortalWarpSound() {
        if (isMuted) return
        val durationSamples = (sampleRate * 2.8f).toInt()
        activeTones.add(ActiveTone(320.0f, durationSamples, baseVolume = 0.5f, isPortal = true))
    }

    /**
     * Trigger an exhilarating light-speed hyperspace warp swoosh through the black hole.
     */
    fun playHyperspaceChime() {
        if (isMuted) return
        val durationSamples = (sampleRate * 2.5f).toInt()
        activeTones.add(ActiveTone(180.0f, durationSamples, baseVolume = 0.65f, isPortal = true))
        activeTones.add(ActiveTone(540.0f, durationSamples, baseVolume = 0.45f, isPortal = true))
        activeTones.add(ActiveTone(1080.0f, durationSamples, baseVolume = 0.35f, isFloral = true))
    }

    /**
     * Trigger a cute cheerful chirp when meeting or interacting with a cute predator.
     */
    fun playCutePredatorChime() {
        if (isMuted) return
        val durationSamples = (sampleRate * 0.8f).toInt()
        activeTones.add(ActiveTone(880.0f, durationSamples, baseVolume = 0.38f, isFloral = true))
        activeTones.add(ActiveTone(1320.0f, durationSamples, baseVolume = 0.28f, isFloral = true))
    }

    /**
     * Trigger a deep rumble warning when near an ugly predator.
     */
    fun playUglyPredatorRoar() {
        if (isMuted) return
        val durationSamples = (sampleRate * 1.5f).toInt()
        activeTones.add(ActiveTone(110.0f, durationSamples, baseVolume = 0.45f, isBubble = true))
        activeTones.add(ActiveTone(165.0f, durationSamples, baseVolume = 0.35f, isPortal = true))
    }

    /**
     * Trigger an impact damage thud when an ugly predator bites/attacks the flyer.
     */
    fun playDamageSound() {
        if (isMuted) return
        val durationSamples = (sampleRate * 0.7f).toInt()
        activeTones.add(ActiveTone(85.0f, durationSamples, baseVolume = 0.65f, isBubble = true))
        activeTones.add(ActiveTone(130.0f, durationSamples, baseVolume = 0.45f, isPortal = true))
    }

    /**
     * Trigger a deep singing bowl / gong transition chime when entering a new realm.
     */
    fun playRealmTransitionChime() {
        if (isMuted) return
        val durationSamples = (sampleRate * 3.5f).toInt()
        activeTones.add(ActiveTone(216.0f, durationSamples, baseVolume = 0.45f, isBubble = false))
        activeTones.add(ActiveTone(432.0f, durationSamples, baseVolume = 0.35f, isBubble = false))
        activeTones.add(ActiveTone(648.0f, durationSamples, baseVolume = 0.2f, isBubble = false))
    }

    private fun runAudioLoop() {
        val audioBuffer = ShortArray(1024)
        var filterState = 0.0
        var filterState2 = 0.0
        var rainFilter1 = 0.0
        var rainFilter2 = 0.0
        var currentRainVol = 0.0

        // LFO and ambient drone phases
        var ambientPhase1 = 0.0
        var ambientPhase2 = 0.0
        var ambientPhase3 = 0.0
        var lfoPhase = 0.0

        // Lo-Fi Beat Engine States
        var lofiSampleCounter = 0L
        val bpm = 68.0
        val samplesPerBeat = (sampleRate * 60.0 / bpm).toInt()
        val samplesPerBar = samplesPerBeat * 4
        var lofiChordPhases = DoubleArray(5) { 0.0 }
        var tapeFlutterPhase = 0.0

        while (audioScope.isActive) {
            if (audioTrack == null) break

            for (i in audioBuffer.indices) {
                if (isMuted) {
                    audioBuffer[i] = 0
                    continue
                }

                var sampleSum = 0.0
                lofiSampleCounter++

                // 1. Procedural Wind Synthesis
                if (isWindEnabled) {
                    val white = (Random.nextFloat() * 2f - 1f).toDouble()
                    val speed = flightSpeedFactor.coerceIn(0.1f, 1.2f).toDouble()
                    val cutoff = 0.02 + speed * 0.05 + sin(lfoPhase * 0.2) * 0.015

                    filterState += cutoff * (white - filterState)
                    filterState2 += cutoff * (filterState - filterState2)

                    val windVol = (0.10 + speed * 0.16 + sin(lfoPhase * 0.35) * 0.03).coerceIn(0.04, 0.30)
                    sampleSum += filterState2 * windVol
                }

                // 2. Procedural Calming Rain Synthesis (When active in rainy realms)
                val targetRainVol = if (isRainActive) 0.14 else 0.0
                currentRainVol += (targetRainVol - currentRainVol) * 0.0001
                if (currentRainVol > 0.001) {
                    val rainNoise = (Random.nextFloat() * 2f - 1f).toDouble()
                    rainFilter1 += 0.08 * (rainNoise - rainFilter1)
                    rainFilter2 += 0.08 * (rainFilter1 - rainFilter2)

                    // Occasional droplet sprinkle
                    var droplet = 0.0
                    if (Random.nextFloat() < 0.00035f) {
                        droplet = (Random.nextFloat() * 2f - 1f) * 0.4
                    }

                    sampleSum += (rainFilter2 * 0.8 + droplet) * currentRainVol
                }

                // 3. Relaxing Lo-Fi Music Synthesis (Rhodes E-Piano, Warm Vinyl, Chill Kick & Snare)
                if (isMusicEnabled && isLofiEnabled) {
                    // Vinyl crackle / tape noise (authentic vintage warmth)
                    val vinylHiss = (Random.nextFloat() * 2f - 1f) * 0.006
                    var vinylClick = 0.0
                    if (Random.nextFloat() < 0.00018f) {
                        vinylClick = (Random.nextFloat() * 2f - 1f) * 0.05
                    }
                    sampleSum += (vinylHiss + vinylClick)

                    // Lo-Fi Beat / Drums
                    val barPos = (lofiSampleCounter % samplesPerBar).toInt()
                    val beatPos = (lofiSampleCounter % samplesPerBeat).toInt()
                    val beatIndex = (barPos / samplesPerBeat) % 4

                    // Kick Drum on Beat 0 (Soft sub kick 80Hz -> 42Hz)
                    if (beatIndex == 0) {
                        val kickProgress = beatPos.toDouble() / (samplesPerBeat * 0.65)
                        if (kickProgress < 1.0) {
                            val kickFreq = 80.0 * (1.0 - kickProgress * 0.5)
                            val kickEnv = (1.0 - kickProgress) * (1.0 - kickProgress)
                            val kick = sin(2.0 * PI * kickFreq * (beatPos.toDouble() / sampleRate)) * kickEnv * 0.16
                            sampleSum += kick
                        }
                    }

                    // Soft Snare / Rimshot on Beat 2
                    if (beatIndex == 2) {
                        val snareProgress = beatPos.toDouble() / (samplesPerBeat * 0.35)
                        if (snareProgress < 1.0) {
                            val snareNoise = (Random.nextFloat() * 2f - 1f).toDouble()
                            val snareEnv = (1.0 - snareProgress) * (1.0 - snareProgress)
                            sampleSum += snareNoise * snareEnv * 0.065
                        }
                    }

                    // Tape flutter for authentic Lo-Fi pitch wobble
                    tapeFlutterPhase += 2.0 * PI * 1.6 / sampleRate
                    val tapeFlutter = sin(tapeFlutterPhase) * 0.0028

                    // Lo-Fi Chords: 4 chords cycling every bar
                    val currentChordIndex = ((lofiSampleCounter / samplesPerBar) % lofiChords.size).toInt()
                    val chordNotes = lofiChords[currentChordIndex]
                    val chordProgress = (barPos.toDouble() / samplesPerBar)
                    val chordEnv = (sin(chordProgress * PI)).coerceIn(0.15, 1.0)

                    var chordSample = 0.0
                    for (cIdx in chordNotes.indices) {
                        val noteFreq = chordNotes[cIdx] * (1.0 + tapeFlutter)
                        lofiChordPhases[cIdx] += 2.0 * PI * noteFreq / sampleRate
                        if (lofiChordPhases[cIdx] > 2.0 * PI) lofiChordPhases[cIdx] -= 2.0 * PI

                        // Warm electric piano tone: fundamental + subtle 2nd & 3rd harmonic
                        val sFundamental = sin(lofiChordPhases[cIdx])
                        val s2 = sin(lofiChordPhases[cIdx] * 2.0) * 0.18
                        val s3 = sin(lofiChordPhases[cIdx] * 3.0) * 0.06
                        chordSample += (sFundamental + s2 + s3) * 0.042
                    }

                    sampleSum += chordSample * chordEnv
                } else if (isMusicEnabled) {
                    // Fallback to 432Hz Zen Drone when Lo-Fi is disabled
                    val lfoSwell = (0.7 + 0.3 * sin(lfoPhase)).coerceIn(0.4, 1.0)
                    val s1 = sin(ambientPhase1)
                    val s2 = sin(ambientPhase2)
                    val s3 = sin(ambientPhase3)

                    ambientPhase1 += 2.0 * PI * 216.0 / sampleRate
                    ambientPhase2 += 2.0 * PI * 324.0 / sampleRate
                    ambientPhase3 += 2.0 * PI * 432.0 / sampleRate

                    if (ambientPhase1 > 2.0 * PI) ambientPhase1 -= 2.0 * PI
                    if (ambientPhase2 > 2.0 * PI) ambientPhase2 -= 2.0 * PI
                    if (ambientPhase3 > 2.0 * PI) ambientPhase3 -= 2.0 * PI

                    sampleSum += (s1 * 0.08 + s2 * 0.05 + s3 * 0.03) * lfoSwell
                }

                // Advance LFO for organic breathing pulsation
                lfoPhase += 2.0 * PI * 0.12 / sampleRate
                if (lfoPhase > 2.0 * PI) lfoPhase -= 2.0 * PI

                // 4. Polyphonic Active Chimes, Bubbles, Flowers & Portals
                val toneIterator = activeTones.iterator()
                while (toneIterator.hasNext()) {
                    val tone = toneIterator.next()
                    val progress = tone.currentSample.toFloat() / tone.maxDurationSamples
                    if (progress >= 1f) {
                        toneIterator.remove()
                        continue
                    }

                    val t = tone.currentSample.toDouble() / sampleRate
                    val sample: Double

                    if (tone.isBubble) {
                        val currentFreq = tone.frequency * (1.0 + 0.6 * sin(progress * PI))
                        val envelope = (1.0 - progress) * (1.0 - progress)
                        sample = sin(2.0 * PI * currentFreq * t) * envelope * tone.baseVolume
                    } else if (tone.isFloral) {
                        // Sparkling bell with shimmering vibrato
                        val decay = exp(-progress * 3.8)
                        val vibrato = 1.0 + 0.015 * sin(2.0 * PI * 6.5 * t)
                        val fundamental = sin(2.0 * PI * (tone.frequency * vibrato) * t)
                        val overtone = sin(2.0 * PI * (tone.frequency * 2.0 * vibrato) * t) * 0.35
                        sample = (fundamental + overtone) * decay * tone.baseVolume
                    } else if (tone.isPortal) {
                        // Cosmic dimensional rift: deep resonant sweeping tone
                        val sweepFreq = tone.frequency * (1.0 + 1.2 * sin(progress * PI))
                        val portalEnv = sin(progress * PI)
                        sample = sin(2.0 * PI * sweepFreq * t) * portalEnv * tone.baseVolume
                    } else {
                        val decay = exp(-progress * 4.5)
                        val fundamental = sin(2.0 * PI * tone.frequency * t)
                        val harmonic = sin(2.0 * PI * (tone.frequency * 2.76) * t) * 0.25
                        sample = (fundamental + harmonic) * decay * tone.baseVolume
                    }

                    sampleSum += sample
                    tone.currentSample++
                }

                val clamped = sampleSum.coerceIn(-1.0, 1.0)
                audioBuffer[i] = (clamped * 32767.0).toInt().toShort()
            }

            audioTrack?.write(audioBuffer, 0, audioBuffer.size)
        }
    }
}
