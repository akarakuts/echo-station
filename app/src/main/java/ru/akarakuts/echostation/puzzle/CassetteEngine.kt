/** CassetteEngine — спектрограмма: соседний (или любой) обмен, верные полоски фиксируются. */
package ru.akarakuts.echostation.puzzle

data class CassetteParams(
    val cols: Int,
    val rows: Int,
    val seed: Int,
    val adjacentOnly: Boolean = true,
    val lockCorrect: Boolean = false,
)

class CassetteEngine(private val params: CassetteParams) : PuzzleEngine {
    val cols: Int get() = params.cols
    val rows: Int get() = params.rows
    private val size: Int = params.cols * params.rows
    var tiles: IntArray = IntArray(size) { it }
        private set
    var selected: Int? = null
        private set
    var justSwapped: Boolean = false
        private set
    var justLocked: Boolean = false
        private set
    val locksCorrect: Boolean get() = params.lockCorrect
    val tileCount: Int get() = size

    init {
        reset()
    }

    override fun reset() {
        val rng = java.util.Random(params.seed.toLong())
        tiles = IntArray(size) { it }
        do {
            for (i in size - 1 downTo 1) {
                val j = rng.nextInt(i + 1)
                val tmp = tiles[i]
                tiles[i] = tiles[j]
                tiles[j] = tmp
            }
        } while (isSolved() && size > 1)
        selected = null
        justSwapped = false
        justLocked = false
    }

    fun isSlotCorrect(index: Int): Boolean = index in tiles.indices && tiles[index] == index

    fun tap(index: Int) {
        justSwapped = false
        justLocked = false
        if (index !in 0 until size) return
        if (params.lockCorrect && isSlotCorrect(index)) {
            selected = null
            return
        }
        val sel = selected
        if (sel == null) {
            selected = index
            return
        }
        if (sel == index) {
            selected = null
            return
        }
        if (params.lockCorrect && isSlotCorrect(sel)) {
            selected = null
            return
        }
        val canSwap = if (params.adjacentOnly) areAdjacent(sel, index) else true
        if (canSwap) {
            val tmp = tiles[sel]
            tiles[sel] = tiles[index]
            tiles[index] = tmp
            justSwapped = true
            justLocked = isSlotCorrect(sel) || isSlotCorrect(index)
        }
        selected = null
    }

    private fun areAdjacent(a: Int, b: Int): Boolean {
        val ax = a % params.cols
        val ay = a / params.cols
        val bx = b % params.cols
        val by = b / params.cols
        return (abs(ax - bx) + abs(ay - by)) == 1
    }

    private fun abs(v: Int) = if (v < 0) -v else v

    override fun isSolved(): Boolean = tiles.indices.all { tiles[it] == it }

    override fun progress(): Float {
        if (isSolved()) return 1f
        return tiles.indices.count { tiles[it] == it }.toFloat() / size.coerceAtLeast(1)
    }

    override fun hint(): PuzzleHint {
        val wrong = tiles.indices.firstOrNull { tiles[it] != it } ?: 0
        val correctPos = tiles.indexOf(wrong)
        val how = if (params.adjacentOnly) "adjacent tiles" else "any two tiles"
        val howRu = if (params.adjacentOnly) "соседние тайлы" else "любые два тайла"
        return PuzzleHint(
            messageEn = "Swap $how so strip ${wrong + 1} reaches its slot.",
            messageRu = "Меняй $howRu, чтобы полоска ${wrong + 1} встала на место.",
            highlightIds = listOf(wrong.toString(), correctPos.toString()),
        )
    }
}
