/** MultiEngine — последовательность мини-пазлов без выхода на Hub (финал). */
package ru.akarakuts.echostation.puzzle

class MultiEngine(private val engines: List<PuzzleEngine>) : PuzzleEngine {
    var step: Int = 0
        private set

    val total: Int get() = engines.size
    val current: PuzzleEngine get() = engines[step.coerceIn(0, engines.lastIndex)]

    init {
        require(engines.isNotEmpty())
        reset()
    }

    override fun reset() {
        step = 0
        engines.forEach { it.reset() }
    }

    /** Move to next mini-puzzle when current is solved. */
    fun tryAdvance(): Boolean {
        if (!current.isSolved()) return false
        if (step < engines.lastIndex) {
            step++
            return true
        }
        return false
    }

    override fun isSolved(): Boolean =
        step == engines.lastIndex && current.isSolved()

    override fun progress(): Float {
        if (isSolved()) return 1f
        val done = step + current.progress()
        return (done / total.coerceAtLeast(1)).coerceIn(0f, 0.99f)
    }

    override fun hint(): PuzzleHint = current.hint()
}
