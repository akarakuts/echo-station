/** PuzzleViewModel — сессия пазла, подсказка, победа, multi-step. */
package ru.akarakuts.echostation.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.akarakuts.echostation.data.ProgressRepository
import ru.akarakuts.echostation.puzzle.CableEngine
import ru.akarakuts.echostation.puzzle.CassetteEngine
import ru.akarakuts.echostation.puzzle.EngineFactory
import ru.akarakuts.echostation.puzzle.FrequencyEngine
import ru.akarakuts.echostation.puzzle.MultiEngine
import ru.akarakuts.echostation.puzzle.PuzzleEngine
import ru.akarakuts.echostation.puzzle.PuzzleHint
import ru.akarakuts.echostation.puzzle.WaveEngine
import ru.akarakuts.echostation.story.ContentRepository
import ru.akarakuts.echostation.story.LevelDef

class PuzzleViewModel(
    private val levelId: Int,
    private val content: ContentRepository,
    private val progressRepo: ProgressRepository,
) : ViewModel() {
    val level: LevelDef = content.level(levelId) ?: error("Unknown level $levelId")
    var engine: PuzzleEngine = EngineFactory.create(level)
        private set
    var hint: PuzzleHint? by mutableStateOf(null)
        private set
    var solved by mutableStateOf(false)
        private set
    var multiStep by mutableIntStateOf(0)
        private set
    var multiTotal by mutableIntStateOf(1)
        private set
    var revision by mutableIntStateOf(0)
        private set
    var lockPulse by mutableStateOf(false)
        private set
    var lastProgress by mutableStateOf(0f)
        private set
    var justAdvanced by mutableStateOf(false)
        private set
    private var advancing = false

    val settings = progressRepo.settings.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun howKey(): String {
        val current = (engine as? MultiEngine)?.current ?: engine
        return when (current) {
            is WaveEngine -> "wave"
            is CableEngine -> if (current.isUntangle) "cable_untangle" else "cable_match"
            is CassetteEngine -> "cassette"
            is FrequencyEngine -> "frequency"
            else -> "wave"
        }
    }

    init {
        syncMulti()
        // Кабель/кассета могут стартовать уже собранными — без жеста bump не сработает.
        checkSolved()
    }

    private fun syncMulti() {
        val m = engine as? MultiEngine
        multiStep = (m?.step ?: 0) + 1
        multiTotal = m?.total ?: 1
    }

    fun bump() {
        revision++
        val p = engine.progress()
        lockPulse = p >= 1f || p > lastProgress + 0.12f
        lastProgress = p
        checkSolved()
    }

    fun reset() {
        engine.reset()
        hint = null
        solved = false
        lockPulse = false
        lastProgress = 0f
        justAdvanced = false
        advancing = false
        syncMulti()
        revision++
    }

    fun showHint() {
        hint = engine.hint()
    }

    fun checkSolved() {
        val multi = engine as? MultiEngine
        if (multi != null) {
            if (multi.current.isSolved()) {
                if (multi.step < multi.total - 1) {
                    if (!advancing) {
                        advancing = true
                        justAdvanced = true
                        viewModelScope.launch {
                            delay(300)
                            multi.tryAdvance()
                            advancing = false
                            justAdvanced = false
                            hint = null
                            syncMulti()
                            revision++
                        }
                    }
                } else if (multi.isSolved()) {
                    markSolved()
                }
            }
            return
        }
        if (engine.isSolved()) markSolved()
    }

    private fun markSolved() {
        if (solved) return
        solved = true
        viewModelScope.launch {
            progressRepo.clearLevel(level.id, level.storyRewardId)
        }
    }

    class Factory(
        private val levelId: Int,
        private val content: ContentRepository,
        private val progressRepo: ProgressRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PuzzleViewModel(levelId, content, progressRepo) as T
    }
}
