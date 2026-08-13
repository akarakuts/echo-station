/** PuzzleEngine — общий контракт чистых движков пазлов (без Android). */
package ru.akarakuts.echostation.puzzle

enum class PuzzleType {
    WAVE, CABLE, CASSETTE, FREQUENCY, MULTI
}

data class PuzzleHint(
    val messageEn: String,
    val messageRu: String,
    val highlightIds: List<String> = emptyList(),
)

interface PuzzleEngine {
    fun reset()
    fun isSolved(): Boolean
    fun hint(): PuzzleHint
    /** 0..1 — насколько кадр уже «пойман», для полоски синхра. */
    fun progress(): Float = if (isSolved()) 1f else 0f
}
