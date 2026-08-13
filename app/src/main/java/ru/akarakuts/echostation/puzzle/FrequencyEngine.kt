/** FrequencyEngine — каждый маркер своего слота; лишние насечки — ложные. */
package ru.akarakuts.echostation.puzzle

import kotlin.math.abs

data class FrequencyParams(
    val markerCount: Int,
    val slotCount: Int,
    val tolerance: Float,
    val seed: Int,
    val targets: List<Float>,
    val assigned: Boolean = true,
    val legend: Boolean = false,
    val zones: Boolean = true,
)

class FrequencyEngine(private val params: FrequencyParams) : PuzzleEngine {
    var positions: FloatArray
        private set
    val slots: List<Float>
    val targets: List<Float> get() = params.targets
    val showLegend: Boolean get() = params.legend
    val showZones: Boolean get() = params.zones
    var dragging: Int? = null
        private set

    init {
        val rng = java.util.Random(params.seed.toLong())
        val count = params.markerCount.coerceAtLeast(1)
        slots = (0 until params.slotCount.coerceAtLeast(count)).map { i ->
            (i + 0.5f) / params.slotCount.coerceAtLeast(1)
        }
        positions = FloatArray(count) { rng.nextFloat() }
        reset()
    }

    override fun reset() {
        val rng = java.util.Random(params.seed.toLong() + 7)
        for (i in positions.indices) {
            positions[i] = rng.nextFloat()
        }
        if (isSolved()) {
            for (i in positions.indices) {
                positions[i] = (positions[i] + 0.37f) % 1f
            }
        }
        dragging = null
    }

    fun beginDrag(marker: Int) {
        if (marker in positions.indices) dragging = marker
    }

    fun dragTo(pos: Float) {
        val m = dragging ?: return
        positions[m] = pos.coerceIn(0f, 1f)
    }

    fun endDrag() {
        val m = dragging ?: return
        val nearest = slots.minByOrNull { abs(it - positions[m]) } ?: positions[m]
        positions[m] = nearest
        dragging = null
    }

    fun markerLocked(index: Int): Boolean {
        if (index !in positions.indices || index !in params.targets.indices) return false
        return abs(positions[index] - params.targets[index]) <= params.tolerance
    }

    override fun isSolved(): Boolean {
        if (positions.size != params.targets.size) return false
        if (params.assigned) {
            return positions.indices.all { i -> abs(positions[i] - params.targets[i]) <= params.tolerance }
        }
        val used = BooleanArray(positions.size)
        for (t in params.targets) {
            var best = -1
            var bestErr = Float.MAX_VALUE
            for (i in positions.indices) {
                if (used[i]) continue
                val err = abs(positions[i] - t)
                if (err < bestErr) {
                    bestErr = err
                    best = i
                }
            }
            if (best < 0 || bestErr > params.tolerance) return false
            used[best] = true
        }
        return true
    }

    override fun progress(): Float {
        if (isSolved()) return 1f
        if (positions.isEmpty()) return 0f
        return positions.indices.count { markerLocked(it) }.toFloat() / positions.size
    }

    override fun hint(): PuzzleHint {
        val i = positions.indices.firstOrNull { !markerLocked(it) } ?: 0
        val t = params.targets.getOrElse(i) { 0f }
        return PuzzleHint(
            messageEn = "Park marker ${i + 1} near ${(t * 100).toInt()} — colour matches the ghost pip.",
            messageRu = "Поставь маркер ${i + 1} около ${(t * 100).toInt()} — цвет совпадает с призрачной меткой.",
            highlightIds = listOf(i.toString()),
        )
    }
}
