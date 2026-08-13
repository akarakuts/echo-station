/** CableEngine — пары реле: match (соедини цвет) или untangle (разведи пересечения). */
package ru.akarakuts.echostation.puzzle

data class CableParams(
    val pairCount: Int,
    val allowCrossing: Boolean,
    val seed: Int,
    val mode: String = "match",
    val lockCorrect: Boolean = false,
    val hideDigits: Boolean = false,
    val highlightPairId: Int = -1,
)

data class CablePort(val id: Int, val side: Side, var index: Int, val pairId: Int) {
    enum class Side { LEFT, RIGHT }
}

class CableEngine(private val params: CableParams) : PuzzleEngine {
    val ports: List<CablePort>
    private val requiredPairs: Map<Int, Int>
    private val homeIndex: Map<Int, Int>
    private val connections = linkedMapOf<Int, Int>()
    var selectedLeft: Int? = null
        private set
    var selectedRight: Int? = null
        private set
    var justRejected: Boolean = false
        private set

    val isUntangle: Boolean get() = params.mode == "untangle"
    val hideDigits: Boolean get() = params.hideDigits
    val highlightPairId: Int get() = params.highlightPairId

    init {
        val rng = java.util.Random(params.seed.toLong())
        val n = params.pairCount.coerceIn(2, 6)
        val rightOrder = (0 until n).shuffled(rng)
        val left = (0 until n).map { i ->
            CablePort(id = i, side = CablePort.Side.LEFT, index = i, pairId = i)
        }
        val right = (0 until n).map { i ->
            val pairId = rightOrder[i]
            CablePort(id = n + i, side = CablePort.Side.RIGHT, index = i, pairId = pairId)
        }
        ports = left + right
        homeIndex = ports.associate { it.id to it.index }
        requiredPairs = left.associate { lp ->
            val rp = right.first { it.pairId == lp.pairId }
            lp.id to rp.id
        }
        reset()
    }

    override fun reset() {
        connections.clear()
        selectedLeft = null
        selectedRight = null
        justRejected = false
        ports.forEach { it.index = homeIndex.getValue(it.id) }
        if (isUntangle) {
            requiredPairs.forEach { (l, r) -> connections[l] = r }
        }
    }

    fun tap(portId: Int) {
        val port = ports.firstOrNull { it.id == portId } ?: return
        justRejected = false
        if (isUntangle) {
            tapUntangle(port)
            return
        }
        if (port.side == CablePort.Side.LEFT) {
            selectedLeft = port.id
            return
        }
        val left = selectedLeft ?: return
        val expected = requiredPairs[left]
        if (expected != port.id) {
            justRejected = true
            selectedLeft = null
            return
        }
        if (params.lockCorrect && connections[left] == expected) {
            selectedLeft = null
            return
        }
        connections.entries.removeAll { it.key == left || it.value == port.id }
        connections[left] = port.id
        selectedLeft = null
    }

    private fun tapUntangle(port: CablePort) {
        if (port.side != CablePort.Side.RIGHT) return
        val sel = selectedRight
        if (sel == null) {
            selectedRight = port.id
            return
        }
        if (sel == port.id) {
            selectedRight = null
            return
        }
        val a = ports.first { it.id == sel }
        val tmp = a.index
        a.index = port.index
        port.index = tmp
        selectedRight = null
    }

    fun moveRightTo(portId: Int, toIndex: Int) {
        if (!isUntangle) return
        val n = ports.count { it.side == CablePort.Side.RIGHT }
        val port = ports.firstOrNull { it.id == portId && it.side == CablePort.Side.RIGHT } ?: return
        val target = toIndex.coerceIn(0, n - 1)
        if (target == port.index) return
        val other = ports.firstOrNull { it.side == CablePort.Side.RIGHT && it.index == target } ?: return
        val tmp = port.index
        port.index = other.index
        other.index = tmp
        justRejected = false
    }

    fun connectionList(): List<Pair<Int, Int>> = connections.toList()

    fun isPairCorrect(leftId: Int): Boolean = connections[leftId] == requiredPairs[leftId]

    private fun crossings(): Int {
        if (params.allowCrossing) return 0
        val edges = connections.map { (l, r) ->
            val li = ports.first { it.id == l }.index
            val ri = ports.first { it.id == r }.index
            li to ri
        }
        var c = 0
        for (i in edges.indices) {
            for (j in i + 1 until edges.size) {
                val (a1, b1) = edges[i]
                val (a2, b2) = edges[j]
                if ((a1 - a2) * (b1 - b2) < 0) c++
            }
        }
        return c
    }

    override fun isSolved(): Boolean {
        if (connections.size != requiredPairs.size) return false
        if (crossings() > 0) return false
        return requiredPairs.all { (l, r) -> connections[l] == r }
    }

    override fun progress(): Float {
        if (isSolved()) return 1f
        val n = requiredPairs.size.coerceAtLeast(1)
        val correct = requiredPairs.count { (l, r) -> connections[l] == r }
        val crossPenalty = if (params.allowCrossing) 0f else crossings().toFloat() / (n * 2f)
        return ((correct.toFloat() / n) - crossPenalty).coerceIn(0f, 0.99f)
    }

    override fun hint(): PuzzleHint {
        if (isUntangle) {
            return PuzzleHint(
                messageEn = "Swap two right-hand ports until the cables no longer cross.",
                messageRu = "Меняй правые порты местами, пока кабели не перестанут пересекаться.",
            )
        }
        val missing = requiredPairs.entries.firstOrNull { (l, r) -> connections[l] != r }
        return if (missing != null) {
            PuzzleHint(
                messageEn = "Connect matching colours — left #${missing.key + 1} to its pair.",
                messageRu = "Соедини одинаковые цвета: левый №${missing.key + 1} с парой.",
                highlightIds = listOf(missing.key.toString(), missing.value.toString()),
            )
        } else {
            PuzzleHint(
                messageEn = "Untangle crossing cables.",
                messageRu = "Убери пересечения кабелей.",
            )
        }
    }
}
