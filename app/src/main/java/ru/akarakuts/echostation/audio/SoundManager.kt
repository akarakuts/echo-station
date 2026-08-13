/** AudioEngine — петля комнаты, SFX из raw, синус для частоты; фолбэк ToneGenerator. */
package ru.akarakuts.echostation.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.SoundPool
import android.media.ToneGenerator
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import ru.akarakuts.echostation.R
import kotlin.math.PI
import kotlin.math.sin

class AudioEngine(context: Context) {
    private val app = context.applicationContext
    private var pool: SoundPool? = null
    private var hum: MediaPlayer? = null
    private var tone: ToneGenerator? = null
    private var pitchTrack: AudioTrack? = null
    private val ids = HashMap<String, Int>()
    private var lastLock = false

    private fun ensurePool() {
        if (pool != null) return
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        pool = SoundPool.Builder().setMaxStreams(4).setAudioAttributes(attrs).build()
        fun load(name: String, res: Int) {
            runCatching { ids[name] = pool!!.load(app, res, 1) }
        }
        load("tick", R.raw.tick)
        load("lock", R.raw.lock)
        load("reject", R.raw.reject)
        load("solve", R.raw.solve)
        load("reel", R.raw.reel)
        load("relay", R.raw.relay)
    }

    private fun ensureTone() {
        if (tone == null) {
            tone = runCatching { ToneGenerator(AudioManager.STREAM_MUSIC, 55) }.getOrNull()
        }
    }

    fun startHum(sound: Boolean, ambiance: Boolean) {
        if (!sound || !ambiance) {
            stopHum()
            return
        }
        if (hum != null) return
        hum = runCatching {
            MediaPlayer.create(app, R.raw.hum_loop)?.apply {
                isLooping = true
                setVolume(0.10f, 0.10f)
                start()
            }
        }.getOrNull()
    }

    fun stopHum() {
        runCatching {
            hum?.stop()
            hum?.release()
        }
        hum = null
    }

    fun setLockCloseness(sound: Boolean, closeness: Float) {
        if (!sound) return
        val inLock = closeness >= 0.72f
        if (inLock && !lastLock) playLock(true)
        lastLock = inLock
        hum?.setVolume(
            (0.10f * (1f - closeness * 0.55f)).coerceIn(0.03f, 0.12f),
            (0.10f * (1f - closeness * 0.55f)).coerceIn(0.03f, 0.12f),
        )
    }

    private fun playPool(key: String, fallback: () -> Unit) {
        ensurePool()
        val id = ids[key]
        if (id != null && pool != null) {
            val ok = pool!!.play(id, 0.5f, 0.5f, 1, 0, 1f)
            if (ok == 0) fallback()
        } else {
            fallback()
        }
    }

    fun playTick(enabled: Boolean) {
        if (!enabled) return
        playPool("tick") {
            ensureTone()
            tone?.startTone(ToneGenerator.TONE_DTMF_1, 20)
        }
    }

    fun playClick(enabled: Boolean) {
        if (!enabled) return
        playPool("tick") {
            ensureTone()
            tone?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
        }
    }

    fun playLock(enabled: Boolean) {
        if (!enabled) return
        playPool("lock") {
            ensureTone()
            tone?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
        }
    }

    fun playReject(enabled: Boolean) {
        if (!enabled) return
        playPool("reject") {
            ensureTone()
            tone?.startTone(ToneGenerator.TONE_PROP_NACK, 50)
        }
    }

    fun playSolve(enabled: Boolean) {
        if (!enabled) return
        playPool("solve") {
            ensureTone()
            tone?.startTone(ToneGenerator.TONE_PROP_ACK, 140)
        }
    }

    fun playReel(enabled: Boolean) {
        if (!enabled) return
        playPool("reel") {
            ensureTone()
            tone?.startTone(ToneGenerator.TONE_DTMF_4, 30)
        }
    }

    fun playRelay(enabled: Boolean) {
        if (!enabled) return
        playPool("relay") {
            ensureTone()
            tone?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
        }
    }

    private var lastPitchHz = 0f
    private var lastPitchAt = 0L

    fun playPitch(enabled: Boolean, hz: Float) {
        if (!enabled) {
            stopPitch()
            return
        }
        val freq = hz.coerceIn(180f, 980f)
        val now = android.os.SystemClock.uptimeMillis()
        if (pitchTrack != null && kotlin.math.abs(freq - lastPitchHz) < 18f && now - lastPitchAt < 90L) return
        lastPitchHz = freq
        lastPitchAt = now
        stopPitch()
        val sampleRate = 16000
        val n = (sampleRate * 0.12).toInt()
        val buf = ShortArray(n) { i ->
            (sin(2.0 * PI * freq * i / sampleRate) * 8000).toInt().toShort()
        }
        val minBuf = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        pitchTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            maxOf(minBuf, n * 2),
            AudioTrack.MODE_STATIC,
        ).apply {
            write(buf, 0, buf.size)
            play()
        }
    }

    fun stopPitch() {
        runCatching {
            pitchTrack?.stop()
            pitchTrack?.release()
        }
        pitchTrack = null
        lastPitchHz = 0f
    }

    fun haptic(view: View?, enabled: Boolean, kind: HapticKind = HapticKind.TICK) {
        if (!enabled || view == null) return
        val code = if (Build.VERSION.SDK_INT >= 30) {
            when (kind) {
                HapticKind.TICK -> HapticFeedbackConstants.CLOCK_TICK
                HapticKind.LOCK -> HapticFeedbackConstants.CONFIRM
                HapticKind.REJECT -> HapticFeedbackConstants.REJECT
                HapticKind.SOLVE -> HapticFeedbackConstants.CONFIRM
            }
        } else {
            when (kind) {
                HapticKind.TICK -> HapticFeedbackConstants.KEYBOARD_TAP
                HapticKind.LOCK -> HapticFeedbackConstants.KEYBOARD_TAP
                HapticKind.REJECT -> HapticFeedbackConstants.LONG_PRESS
                HapticKind.SOLVE -> HapticFeedbackConstants.LONG_PRESS
            }
        }
        view.performHapticFeedback(code)
    }

    fun release() {
        stopHum()
        stopPitch()
        pool?.release()
        pool = null
        ids.clear()
        tone?.release()
        tone = null
    }
}

enum class HapticKind { TICK, LOCK, REJECT, SOLVE }
