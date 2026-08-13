/** EchoStationApp — Navigation Compose: Home/Hub/Puzzle/Story/Archive/Settings/Epilogue. */
package ru.akarakuts.echostation.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.akarakuts.echostation.audio.AudioEngine
import ru.akarakuts.echostation.data.ProgressRepository
import ru.akarakuts.echostation.story.ContentRepository
import ru.akarakuts.echostation.story.EpilogueTone
import ru.akarakuts.echostation.story.Progress

object Routes {
    const val HOME = "home"
    const val HUB = "hub"
    const val PUZZLE = "puzzle/{levelId}"
    const val STORY = "story/{rewardId}"
    const val ARCHIVE = "archive"
    const val SETTINGS = "settings"
    const val EPILOGUE_CHOICE = "epilogue_choice"
    const val EPILOGUE = "epilogue/{tone}"
    const val ACT_BREAK = "act_break/{n}"
    fun puzzle(id: Int) = "puzzle/$id"
    fun story(id: String) = "story/$id"
    fun epilogue(tone: EpilogueTone) = "epilogue/${tone.name}"
    fun actBreak(n: Int) = "act_break/$n"
}

@Composable
fun EchoStationApp() {
    val context = LocalContext.current.applicationContext
    val content = remember { ContentRepository(context) }
    val progressRepo = remember { ProgressRepository(context) }
    val sound = remember { AudioEngine(context) }
    val settings by progressRepo.settings.collectAsStateWithLifecycle(null)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, settings?.sound, settings?.ambiance) {
        val obs = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START ->
                    sound.startHum(settings?.sound != false, settings?.ambiance != false)
                Lifecycle.Event.ON_STOP -> {
                    sound.stopHum()
                    sound.stopPitch()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(obs)
            sound.stopHum()
            sound.stopPitch()
        }
    }
    DisposableEffect(Unit) {
        onDispose { sound.release() }
    }
    LaunchedEffect(settings?.sound, settings?.ambiance) {
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            sound.startHum(settings?.sound != false, settings?.ambiance != false)
        }
    }
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Routes.HOME,
        enterTransition = {
            fadeIn(tween(280)) + slideInHorizontally(tween(320)) { it / 16 }
        },
        exitTransition = { fadeOut(tween(180)) },
        popEnterTransition = { fadeIn(tween(240)) },
        popExitTransition = {
            fadeOut(tween(200)) + slideOutHorizontally(tween(260)) { it / 16 }
        },
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                progressRepo = progressRepo,
                onNightShift = { nav.navigate(Routes.HUB) },
                onContinueLevel = { id ->
                    nav.navigate(Routes.HUB)
                    nav.navigate(Routes.puzzle(id))
                },
                onArchive = { nav.navigate(Routes.ARCHIVE) },
                onSettings = { nav.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.HUB) {
            HubScreen(
                content = content,
                progressRepo = progressRepo,
                onOpenLevel = { id -> nav.navigate(Routes.puzzle(id)) },
                onEpilogue = { nav.navigate(Routes.EPILOGUE_CHOICE) },
                onBack = { nav.popBackStack() },
            )
        }
        composable(
            Routes.PUZZLE,
            arguments = listOf(navArgument("levelId") { type = NavType.IntType }),
        ) { entry ->
            val levelId = entry.arguments?.getInt("levelId") ?: 1
            PuzzleScreen(
                levelId = levelId,
                content = content,
                progressRepo = progressRepo,
                sound = sound,
                onSolved = { rewardId ->
                    nav.navigate(Routes.story(rewardId)) {
                        popUpTo(Routes.puzzle(levelId)) { inclusive = true }
                    }
                },
                onBack = { nav.popBackStack() },
            )
        }
        composable(
            Routes.STORY,
            arguments = listOf(navArgument("rewardId") { type = NavType.StringType }),
        ) { entry ->
            val rewardId = entry.arguments?.getString("rewardId").orEmpty()
            val progress by progressRepo.progress.collectAsStateWithLifecycle(Progress())
            StoryCardScreen(
                rewardId = rewardId,
                content = content,
                progressRepo = progressRepo,
                onContinue = {
                    val n = rewardId.removePrefix("r").toIntOrNull()
                    when {
                        rewardId == "r080" || n == 80 -> {
                            if (progress.epilogueTone != null) {
                                nav.popBackStack(Routes.HUB, inclusive = false)
                                if (nav.currentDestination?.route != Routes.HUB) {
                                    nav.navigate(Routes.HUB)
                                }
                            } else {
                                nav.navigate(Routes.EPILOGUE_CHOICE) {
                                    popUpTo(Routes.HUB)
                                }
                            }
                        }
                        n == 20 -> nav.navigate(Routes.actBreak(1)) { popUpTo(Routes.HUB) }
                        n == 50 -> nav.navigate(Routes.actBreak(2)) { popUpTo(Routes.HUB) }
                        n != null && n in 1..79 -> nav.navigate(Routes.puzzle(n + 1)) {
                            popUpTo(Routes.HUB)
                        }
                        else -> {
                            nav.popBackStack(Routes.HUB, inclusive = false)
                            if (nav.currentDestination?.route != Routes.HUB) {
                                nav.navigate(Routes.HUB)
                            }
                        }
                    }
                },
            )
        }
        composable(
            Routes.ACT_BREAK,
            arguments = listOf(navArgument("n") { type = NavType.IntType }),
        ) { entry ->
            val n = entry.arguments?.getInt("n") ?: 1
            ActBreakScreen(
                act = n,
                progressRepo = progressRepo,
                onContinue = {
                    val next = if (n == 1) 21 else 51
                    nav.navigate(Routes.puzzle(next)) { popUpTo(Routes.HUB) }
                },
            )
        }
        composable(Routes.ARCHIVE) {
            ArchiveScreen(content, progressRepo, onBack = { nav.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                progressRepo = progressRepo,
                onBack = { nav.popBackStack() },
            )
        }
        composable(Routes.EPILOGUE_CHOICE) {
            EpilogueChoiceScreen(
                progressRepo = progressRepo,
                onChosen = { tone -> nav.navigate(Routes.epilogue(tone)) },
                onBack = { nav.popBackStack() },
            )
        }
        composable(
            Routes.EPILOGUE,
            arguments = listOf(navArgument("tone") { type = NavType.StringType }),
        ) { entry ->
            val tone = runCatching {
                EpilogueTone.valueOf(entry.arguments?.getString("tone").orEmpty())
            }.getOrDefault(EpilogueTone.BROADCAST)
            EpilogueScreen(
                tone = tone,
                content = content,
                progressRepo = progressRepo,
                onMenu = {
                    nav.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
            )
        }
    }
}
