/** ProgressRepository — DataStore: прогресс, туториал, отметки и настройки. */
package ru.akarakuts.echostation.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.akarakuts.echostation.story.EpilogueTone
import ru.akarakuts.echostation.story.GameSettings
import ru.akarakuts.echostation.story.Progress
import ru.akarakuts.echostation.story.derivedMarks

private val Context.dataStore by preferencesDataStore("echo_station")

class ProgressRepository(private val context: Context) {
    private val clearedKey = stringPreferencesKey("cleared")
    private val rewardsKey = stringPreferencesKey("rewards")
    private val epilogueKey = stringPreferencesKey("epilogue")
    private val seenHowKey = stringPreferencesKey("seen_how")
    private val marksKey = stringPreferencesKey("marks")
    private val tonesKey = stringPreferencesKey("seen_tones")
    private val soundKey = booleanPreferencesKey("sound")
    private val hapticsKey = booleanPreferencesKey("haptics")
    private val ambianceKey = booleanPreferencesKey("ambiance")
    private val reduceMotionKey = booleanPreferencesKey("reduce_motion")
    private val nameKey = stringPreferencesKey("display_name")
    private val langKey = stringPreferencesKey("language")

    val progress: Flow<Progress> = context.dataStore.data.map { prefs ->
        val base = Progress(
            clearedLevelIds = prefs[clearedKey].orEmpty().split(',').mapNotNull { it.toIntOrNull() }.toSet(),
            collectedRewardIds = prefs[rewardsKey].orEmpty().split(',').filter { it.isNotBlank() }.toSet(),
            epilogueTone = prefs[epilogueKey]?.let { runCatching { EpilogueTone.valueOf(it) }.getOrNull() },
            seenHowKeys = prefs[seenHowKey].orEmpty().split(',').filter { it.isNotBlank() }.toSet(),
            unlockedMarks = prefs[marksKey].orEmpty().split(',').filter { it.isNotBlank() }.toSet(),
            seenEpilogueTones = prefs[tonesKey].orEmpty().split(',').filter { it.isNotBlank() }.toSet(),
        )
        base.copy(unlockedMarks = base.derivedMarks())
    }

    val settings: Flow<GameSettings> = context.dataStore.data.map { prefs ->
        GameSettings(
            sound = prefs[soundKey] ?: true,
            haptics = prefs[hapticsKey] ?: true,
            ambiance = prefs[ambianceKey] ?: true,
            reduceMotion = prefs[reduceMotionKey] ?: false,
            displayName = prefs[nameKey].orEmpty(),
            language = "",
        )
    }

    suspend fun clearLevel(levelId: Int, rewardId: String) {
        context.dataStore.edit { prefs ->
            val cleared = prefs[clearedKey].orEmpty().split(',').filter { it.isNotBlank() }.toMutableSet()
            cleared += levelId.toString()
            prefs[clearedKey] = cleared.sorted().joinToString(",")
            val rewards = prefs[rewardsKey].orEmpty().split(',').filter { it.isNotBlank() }.toMutableSet()
            rewards += rewardId
            prefs[rewardsKey] = rewards.sorted().joinToString(",")
            val marks = prefs[marksKey].orEmpty().split(',').filter { it.isNotBlank() }.toMutableSet()
            val ids = cleared.mapNotNull { it.toIntOrNull() }.toSet()
            if (2 in ids) marks += "first_word"
            if (50 in ids) marks += "name_found"
            if (80 in ids) marks += "letter_done"
            prefs[marksKey] = marks.sorted().joinToString(",")
        }
    }

    suspend fun markHowSeen(key: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[seenHowKey].orEmpty().split(',').filter { it.isNotBlank() }.toMutableSet()
            set += key
            prefs[seenHowKey] = set.sorted().joinToString(",")
        }
    }

    suspend fun setEpilogue(tone: EpilogueTone) {
        context.dataStore.edit { prefs ->
            prefs[epilogueKey] = tone.name
            val tones = prefs[tonesKey].orEmpty().split(',').filter { it.isNotBlank() }.toMutableSet()
            tones += tone.name
            prefs[tonesKey] = tones.sorted().joinToString(",")
            if (tones.size >= 3) {
                val marks = prefs[marksKey].orEmpty().split(',').filter { it.isNotBlank() }.toMutableSet()
                marks += "three_tones"
                prefs[marksKey] = marks.sorted().joinToString(",")
            }
        }
    }

    suspend fun updateSettings(transform: (GameSettings) -> GameSettings) {
        context.dataStore.edit { prefs ->
            val cur = GameSettings(
                sound = prefs[soundKey] ?: true,
                haptics = prefs[hapticsKey] ?: true,
                ambiance = prefs[ambianceKey] ?: true,
                reduceMotion = prefs[reduceMotionKey] ?: false,
                displayName = prefs[nameKey].orEmpty(),
                language = prefs[langKey].orEmpty(),
            )
            val next = transform(cur)
            prefs[soundKey] = next.sound
            prefs[hapticsKey] = next.haptics
            prefs[ambianceKey] = next.ambiance
            prefs[reduceMotionKey] = next.reduceMotion
            prefs[nameKey] = next.displayName
            prefs.remove(langKey)
        }
    }

    /** Сброс кадров и эпилога; архив и отметки остаются. */
    suspend fun resetLevels() {
        context.dataStore.edit { prefs ->
            prefs.remove(clearedKey)
            prefs.remove(epilogueKey)
        }
    }

    suspend fun resetProgress() {
        context.dataStore.edit { prefs ->
            prefs.remove(clearedKey)
            prefs.remove(rewardsKey)
            prefs.remove(epilogueKey)
            prefs.remove(seenHowKey)
            prefs.remove(marksKey)
            prefs.remove(tonesKey)
        }
    }
}
