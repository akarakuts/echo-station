/** StoryModels — уровни, награды, прогресс и настройки. */
package ru.akarakuts.echostation.story

import ru.akarakuts.echostation.puzzle.PuzzleType

enum class RewardKind { LOG, VOICE, PHOTO, LETTER }

enum class EpilogueTone { BROADCAST, ARCHIVE, LEAVE }

data class LevelDef(
    val id: Int,
    val act: Int,
    val puzzleType: PuzzleType,
    val difficulty: Int,
    val paramsJson: String,
    val storyRewardId: String,
    val unlockPrevId: Int?,
)

data class StoryReward(
    val id: String,
    val kind: RewardKind,
    val titles: Map<String, String>,
    val bodies: Map<String, String>,
    val imageAsset: String?,
    val archiveKey: String?,
)

data class Progress(
    val clearedLevelIds: Set<Int> = emptySet(),
    val collectedRewardIds: Set<String> = emptySet(),
    val epilogueTone: EpilogueTone? = null,
    val seenHowKeys: Set<String> = emptySet(),
    val unlockedMarks: Set<String> = emptySet(),
    val seenEpilogueTones: Set<String> = emptySet(),
)

data class GameSettings(
    val sound: Boolean = true,
    val haptics: Boolean = true,
    val ambiance: Boolean = true,
    val reduceMotion: Boolean = false,
    val displayName: String = "",
    /** Устарело: язык только системный; поле оставлено для схемы DataStore. */
    val language: String = "",
)

fun Progress.isLevelUnlocked(levelId: Int): Boolean {
    if (levelId <= 1) return true
    return (levelId - 1) in clearedLevelIds
}

fun Progress.isLevelCleared(levelId: Int): Boolean = levelId in clearedLevelIds

/** Первый незакрытый открытый кадр; null — письмо уже собрано. */
fun Progress.nextPlayableLevel(maxLevel: Int = 80): Int? {
    if (maxLevel in clearedLevelIds) return null
    for (id in 1..maxLevel) {
        if (id !in clearedLevelIds && isLevelUnlocked(id)) return id
    }
    return 1
}

fun Progress.derivedMarks(): Set<String> {
    val marks = unlockedMarks.toMutableSet()
    if (2 in clearedLevelIds) marks += "first_word"
    if (50 in clearedLevelIds) marks += "name_found"
    if (80 in clearedLevelIds) marks += "letter_done"
    if (seenEpilogueTones.size >= 3) marks += "three_tones"
    return marks
}
