/** WaveEngine — фаза / усиление / несущая; дрейф, пока нет захвата. */
package ru.akarakuts.echostation.puzzle

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

data class WaveParams(
    val targetPhase: Float,
    val targetAmplitude: Float,
    val noiseSeed: Int,
    val tolerance: Float,
    val holdFrames: Int,
    val targetFrequency: Float = 4f,
    val useFrequency: Boolean = false,
    val drift: Float = 0f,
    val hideAmplitude: Boolean = false,
    val glitchEvery: Int = 0,
    val glitchFrames: Int = 0,
    val envelope: String = "sine",
)

class WaveEngine(private val params: WaveParams) : PuzzleEngine {
    var phase: Float = 0f
        private set
    var amplitude: Float = 0.3f
        private set
    var frequency: Float = 4f
        private set
    private var hold: Int = 0
    private var tick: Int = 0
    private var glitchLeft: Int = 0

    val usesFrequency: Boolean get() = params.useFrequency
    val showsAmplitude: Boolean get() = !params.hideAmplitude
    val lockRatio: Float get() = hold.toFloat() / params.holdFrames.coerceAtLeast(1)
    val isGlitching: Boolean get() = glitchLeft > 0

    init {
        reset()
    }

    override fun reset() {
        val rng = java.util.Random(params.noiseSeed.toLong())
        val twoPi = (PI * 2).toFloat()
        phase = rng.nextFloat() * twoPi
        amplitude = if (params.hideAmplitude) {
            params.targetAmplitude
        } else {
            0.2f + rng.nextFloat() * 0.5f
        }
        frequency = if (params.useFrequency) {
            2.2f + rng.nextFloat() * 3.4f
        } else {
            params.targetFrequency
        }
        if (error() < params.tolerance) {
            phase = (phase + 1.2f) % twoPi
        }
        hold = 0
        tick = 0
        glitchLeft = 0
    }

    fun setPhase(value: Float) {
        phase = value.coerceIn(0f, (PI * 2).toFloat())
        tickHold()
    }

    fun setAmplitude(value: Float) {
        if (params.hideAmplitude) return
        amplitude = value.coerceIn(0.05f, 1.2f)
        tickHold()
    }

    fun setFrequency(value: Float) {
        if (!params.useFrequency) return
        frequency = value.coerceIn(2f, 6.2f)
        tickHold()
    }

    fun inBand(): Boolean = !isGlitching && error() <= params.tolerance

    fun error(): Float {
        val twoPi = (PI * 2).toFloat()
        val phaseErr = abs(phase - params.targetPhase)
        val phaseWrapped = minOf(phaseErr, twoPi - phaseErr)
        val ampErr = if (params.hideAmplitude) 0f else abs(amplitude - params.targetAmplitude)
        val freqErr = if (params.useFrequency) abs(frequency - params.targetFrequency) * 0.28f else 0f
        return phaseWrapped * 0.35f + ampErr + freqErr
    }

    private fun tickHold() {
        hold = if (inBand()) hold + 1 else 0
    }

    /** Кадр UI: удержание в полосе и дрейф, пока захват не начался. */
    fun sampleHold() {
        tick++
        if (params.glitchEvery > 0 && tick % params.glitchEvery == 0) {
            glitchLeft = params.glitchFrames.coerceAtLeast(1)
        }
        if (glitchLeft > 0) {
            glitchLeft--
            hold = 0
            return
        }
        if (!inBand() && params.drift > 0f) {
            phase = (phase + params.drift) % (PI * 2).toFloat()
        }
        tickHold()
    }

    override fun isSolved(): Boolean = hold >= params.holdFrames

    override fun progress(): Float {
        if (isSolved()) return 1f
        val closeness = (1f - (error() / (params.tolerance * 3f)).coerceIn(0f, 1f))
        return (closeness * 0.72f + lockRatio * 0.28f).coerceIn(0f, 0.99f)
    }

    fun sample(t: Float, useTarget: Boolean): Float {
        val p = if (useTarget) params.targetPhase else phase
        val a = if (useTarget) params.targetAmplitude else amplitude
        val f = if (useTarget) params.targetFrequency else frequency
        val raw = sin(t * f + p) * a
        return if (params.envelope == "double") {
            raw * (0.55f + 0.45f * sin(t * 2f))
        } else {
            raw
        }
    }

    override fun hint(): PuzzleHint {
        return when {
            params.useFrequency && abs(frequency - params.targetFrequency) > params.tolerance -> PuzzleHint(
                messageEn = "Match the carrier — the amber ghost should pulse at the same rate.",
                messageRu = "Подгони несущую: янтарная тень должна пульсировать в том же темпе.",
                highlightIds = listOf("frequency"),
            )
            abs(phase - params.targetPhase) > params.tolerance * 0.5f -> PuzzleHint(
                messageEn = "Nudge the phase dial toward the amber ghost wave.",
                messageRu = "Подкрути фазу к янтарной «тени» волны.",
                highlightIds = listOf("phase"),
            )
            else -> PuzzleHint(
                messageEn = "Match the gain to the amber trace and hold the lock.",
                messageRu = "Подгони усиление к янтарной кривой и удержи захват.",
                highlightIds = listOf("amplitude"),
            )
        }
    }
}
