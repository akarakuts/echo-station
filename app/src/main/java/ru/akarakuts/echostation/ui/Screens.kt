/** Screens — Home/Hub/Puzzle/Story/Archive/Settings/Epilogue с атмосферой и i18n. */
package ru.akarakuts.echostation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.akarakuts.echostation.BuildConfig
import ru.akarakuts.echostation.R
import ru.akarakuts.echostation.audio.AudioEngine
import ru.akarakuts.echostation.audio.HapticKind
import ru.akarakuts.echostation.data.ProgressRepository
import ru.akarakuts.echostation.puzzle.PuzzleType
import ru.akarakuts.echostation.story.AppLocales
import ru.akarakuts.echostation.story.ContentRepository
import ru.akarakuts.echostation.story.EpilogueTone
import ru.akarakuts.echostation.story.LevelDef
import ru.akarakuts.echostation.story.Progress
import ru.akarakuts.echostation.story.RewardKind
import ru.akarakuts.echostation.story.StoryReward
import ru.akarakuts.echostation.story.bodyFor
import ru.akarakuts.echostation.story.derivedMarks
import ru.akarakuts.echostation.story.isLevelCleared
import ru.akarakuts.echostation.story.isLevelUnlocked
import ru.akarakuts.echostation.story.nextPlayableLevel
import ru.akarakuts.echostation.story.titleFor
import ru.akarakuts.echostation.puzzle.CableEngine
import ru.akarakuts.echostation.puzzle.CassetteEngine
import ru.akarakuts.echostation.puzzle.FrequencyEngine
import ru.akarakuts.echostation.puzzle.MultiEngine
import ru.akarakuts.echostation.puzzle.WaveEngine
import ru.akarakuts.echostation.story.GameSettings
import ru.akarakuts.echostation.ui.puzzle.PuzzleBoard
import ru.akarakuts.echostation.ui.puzzle.SyncMeter
import ru.akarakuts.echostation.ui.theme.Amber
import ru.akarakuts.echostation.ui.theme.AmberDeep
import ru.akarakuts.echostation.ui.theme.Paper
import ru.akarakuts.echostation.ui.theme.PaperInk
import ru.akarakuts.echostation.ui.theme.ScopeBlue
import ru.akarakuts.echostation.ui.theme.ScopeGlow
import ru.akarakuts.echostation.ui.theme.SolveBurst
import ru.akarakuts.echostation.ui.theme.StationAtmosphere
import ru.akarakuts.echostation.ui.theme.StationHero
import ru.akarakuts.echostation.ui.theme.StationHubBackdrop
import ru.akarakuts.echostation.ui.theme.StationInk
import ru.akarakuts.echostation.ui.theme.StationNight
import ru.akarakuts.echostation.ui.theme.StationPanel
import ru.akarakuts.echostation.ui.theme.StationPhotoFrame
import ru.akarakuts.echostation.ui.theme.StaticGray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
private fun storyLang(): String {
    val sys = LocalConfiguration.current.locales[0] ?: Locale.getDefault()
    return AppLocales.storyLang(sys)
}

@Composable
fun HomeScreen(
    progressRepo: ProgressRepository,
    onNightShift: () -> Unit,
    onContinueLevel: (Int) -> Unit,
    onArchive: () -> Unit,
    onSettings: () -> Unit,
) {
    val settings by progressRepo.settings.collectAsStateWithLifecycle(null)
    val progress by progressRepo.progress.collectAsStateWithLifecycle(Progress())
    val reduce = settings?.reduceMotion == true
    val next = progress.nextPlayableLevel()
    val inProgress = progress.clearedLevelIds.isNotEmpty() && next != null
    val dawn = progress.epilogueTone != null
    val fontScale = LocalDensity.current.fontScale
    val titleSp = if (fontScale > 1.3f) 28.sp else 32.sp
    var clock by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())) }
    LaunchedEffect(Unit) {
        while (true) {
            clock = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            delay(15_000)
        }
    }
    StationAtmosphere(reduceMotion = reduce, dawn = dawn) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            Text(
                text = "ORION-7  ·  1994  ·  $clock",
                style = MaterialTheme.typography.labelLarge,
                color = Amber,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp, bottom = 4.dp),
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge.copy(
                    brush = Brush.linearGradient(listOf(Amber, ScopeGlow)),
                    fontSize = titleSp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    textAlign = TextAlign.Center,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(R.string.subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = StationInk.copy(alpha = 0.88f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
            )
            StationHero(reduceMotion = reduce)
            val duty = settings?.displayName.orEmpty()
            if (duty.isNotBlank()) {
                Text(
                    text = stringResource(R.string.duty_hello, duty),
                    style = MaterialTheme.typography.labelLarge,
                    color = ScopeGlow,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                )
            }
            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(22.dp), ambientColor = Amber.copy(alpha = 0.35f))
                    .background(
                        Brush.verticalGradient(
                            listOf(StationPanel.copy(alpha = 0.92f), StationNight.copy(alpha = 0.88f)),
                        ),
                        RoundedCornerShape(22.dp),
                    )
                    .border(1.dp, Amber.copy(alpha = 0.28f), RoundedCornerShape(22.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = {
                        if (inProgress && next != null) onContinueLevel(next) else onNightShift()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = StationNight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .semantics { contentDescription = "night shift" },
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        if (inProgress && next != null) {
                            stringResource(R.string.cta_continue, next)
                        } else {
                            stringResource(R.string.cta_night_shift)
                        },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                    )
                }
                if (inProgress) {
                    TextButton(onClick = onNightShift, modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp)) {
                        Text(stringResource(R.string.hub_title), color = ScopeGlow, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                OutlinedButton(
                    onClick = onArchive,
                    modifier = Modifier.fillMaxWidth().height(48.dp).semantics { contentDescription = "archive" },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = StationPanel, contentColor = ScopeGlow),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ScopeBlue.copy(alpha = 0.75f)),
                ) { Text(stringResource(R.string.archive), style = MaterialTheme.typography.bodyLarge) }
                OutlinedButton(
                    onClick = onSettings,
                    modifier = Modifier.fillMaxWidth().height(48.dp).semantics { contentDescription = "settings" },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = StationPanel, contentColor = Amber),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberDimBorder()),
                ) { Text(stringResource(R.string.settings), style = MaterialTheme.typography.bodyLarge) }
            }
        }
    }
}

@Composable private fun AmberDimBorder() = Amber.copy(alpha = 0.45f)

@Composable
fun HubScreen(
    content: ContentRepository,
    progressRepo: ProgressRepository,
    onOpenLevel: (Int) -> Unit,
    onEpilogue: () -> Unit,
    onBack: () -> Unit,
) {
    val progress by progressRepo.progress.collectAsStateWithLifecycle(Progress())
    val settings by progressRepo.settings.collectAsStateWithLifecycle(null)
    val levels = remember { content.levels() }
    val byAct = remember(levels) { levels.groupBy { it.act } }
    val cleared = progress.clearedLevelIds.size
    val total = levels.size.coerceAtLeast(1)

    val next = progress.nextPlayableLevel()
    val gridState = rememberLazyGridState()
    LaunchedEffect(next) {
        val id = next ?: return@LaunchedEffect
        val index = when {
            id <= 20 -> id
            id <= 50 -> 21 + (id - 20)
            else -> 52 + (id - 50)
        }
        gridState.animateScrollToItem(index.coerceAtLeast(0))
    }

    StationAtmosphere(reduceMotion = settings?.reduceMotion == true, dawn = progress.epilogueTone != null) {
        StationScreen {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.hub_title), style = MaterialTheme.typography.headlineMedium, color = Amber)
                StationBackButton(onBack)
            }
            StationHubBackdrop()
            Row(
                Modifier.fillMaxWidth().padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.progress_label), color = StaticGray, style = MaterialTheme.typography.bodyMedium)
                Text("$cleared / $total", color = ScopeGlow, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            }
            LinearProgressIndicator(
                progress = { cleared / total.toFloat() },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp).height(8.dp),
                color = Amber,
                trackColor = StationPanel,
            )
            if (next != null) {
                Button(
                    onClick = { onOpenLevel(next) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = StationNight),
                    shape = RoundedCornerShape(12.dp),
                ) { Text(stringResource(R.string.cta_continue, next), fontWeight = FontWeight.SemiBold) }
            } else if (80 in progress.clearedLevelIds) {
                Button(
                    onClick = onEpilogue,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ScopeBlue),
                    shape = RoundedCornerShape(12.dp),
                ) { Text(stringResource(R.string.epilogue_title)) }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                state = gridState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(1 to R.string.act_noise, 2 to R.string.act_name, 3 to R.string.act_letter).forEach { (act, titleRes) ->
                    item(span = { GridItemSpan(5) }) {
                        Text(
                            stringResource(titleRes),
                            color = ScopeGlow,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp),
                        )
                    }
                    items(byAct[act].orEmpty(), key = { it.id }) { level ->
                        FrameChip(level, progress, isNext = level.id == next, onOpen = onOpenLevel)
                    }
                }
            }
        }
    }
}

@Composable
private fun FrameChip(level: LevelDef, progress: Progress, isNext: Boolean, onOpen: (Int) -> Unit) {
    val unlocked = progress.isLevelUnlocked(level.id)
    val cleared = progress.isLevelCleared(level.id)
    val typeName = puzzleTitle(level.puzzleType.name)
    val status = when {
        cleared -> stringResource(R.string.status_caught)
        unlocked -> stringResource(R.string.status_open)
        else -> stringResource(R.string.status_noise)
    }
    val cd = stringResource(R.string.cd_frame_chip, level.id, typeName, status)
    val borderColor = when {
        isNext -> Amber
        cleared -> Amber.copy(alpha = 0.7f)
        unlocked -> ScopeBlue
        else -> Color(0xFF8A8490)
    }
    val typeColor = when (level.puzzleType) {
        PuzzleType.WAVE -> ScopeGlow
        PuzzleType.CABLE -> Amber
        PuzzleType.CASSETTE -> Color(0xFFE07A5F)
        PuzzleType.FREQUENCY -> ScopeBlue
        PuzzleType.MULTI -> Color(0xFFC9A0DC)
    }
    Box(
        modifier = Modifier
            .height(56.dp)
            .shadow(if (isNext) 10.dp else 0.dp, RoundedCornerShape(10.dp), ambientColor = Amber.copy(alpha = 0.4f))
            .background(
                when {
                    cleared -> StationPanel
                    unlocked -> StationPanel.copy(alpha = 0.92f)
                    else -> StationNight.copy(alpha = 0.72f)
                },
                RoundedCornerShape(10.dp),
            )
            .border(if (isNext) 2.dp else 1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(enabled = unlocked) { onOpen(level.id) }
            .testTag("frame-${level.id}")
            .semantics { contentDescription = cd },
        contentAlignment = Alignment.Center,
    ) {
        if (!unlocked) {
            Canvas(Modifier.fillMaxSize()) {
                var x = -size.height
                while (x < size.width + size.height) {
                    drawLine(Color(0xFF8A8490).copy(alpha = 0.35f), Offset(x, 0f), Offset(x + size.height, size.height), 1.2f)
                    x += 7f
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(Modifier.size(12.dp)) {
                val c = Offset(size.width / 2f, size.height / 2f)
                val r = size.minDimension / 2f
                when (level.puzzleType) {
                    PuzzleType.WAVE -> drawCircle(typeColor, r, c)
                    PuzzleType.CABLE -> drawRect(typeColor, Offset(c.x - r * 0.7f, c.y - r * 0.7f), androidx.compose.ui.geometry.Size(r * 1.4f, r * 1.4f))
                    PuzzleType.CASSETTE -> {
                        val p = Path().apply {
                            moveTo(c.x, c.y - r)
                            lineTo(c.x + r, c.y)
                            lineTo(c.x, c.y + r)
                            lineTo(c.x - r, c.y)
                            close()
                        }
                        drawPath(p, typeColor)
                    }
                    PuzzleType.FREQUENCY -> drawLine(typeColor, Offset(c.x - r, c.y), Offset(c.x + r, c.y), 3f)
                    PuzzleType.MULTI -> {
                        drawLine(typeColor, Offset(c.x - r, c.y), Offset(c.x + r, c.y), 2.5f)
                        drawLine(typeColor, Offset(c.x, c.y - r), Offset(c.x, c.y + r), 2.5f)
                    }
                }
            }
            Text(
                text = level.id.toString(),
                color = when {
                    isNext -> Amber
                    cleared -> Amber
                    unlocked -> ScopeGlow
                    else -> Color(0xFFD0CCD4)
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun PuzzleScreen(
    levelId: Int,
    content: ContentRepository,
    progressRepo: ProgressRepository,
    sound: AudioEngine,
    onSolved: (rewardId: String) -> Unit,
    onBack: () -> Unit,
) {
    val vm: PuzzleViewModel = viewModel(
        key = "puzzle-$levelId",
        factory = PuzzleViewModel.Factory(levelId, content, progressRepo),
    )
    val settings by progressRepo.settings.collectAsStateWithLifecycle(null)
    val progress by progressRepo.progress.collectAsStateWithLifecycle(Progress())
    val view = LocalView.current
    val lang = storyLang()
    val reduceMotion = settings?.reduceMotion == true
    val scope = rememberCoroutineScope()
    var showFlash by remember { mutableStateOf(false) }
    var sync by remember { mutableFloatStateOf(0f) }
    var showHow by remember { mutableStateOf(false) }
    var showLeave by remember { mutableStateOf(false) }
    val howKey = vm.howKey()
    LaunchedEffect(howKey, progress.seenHowKeys) {
        showHow = howKey !in progress.seenHowKeys
    }
    LaunchedEffect(vm.justAdvanced) {
        if (vm.justAdvanced) sound.playRelay(soundOn(settings))
    }
    fun leaveNow() {
        sound.stopPitch()
        onBack()
    }
    BackHandler {
        if (vm.engine.progress() < 0.15f) leaveNow() else showLeave = true
    }
    LaunchedEffect(vm.solved) {
        if (vm.solved) {
            showFlash = true
            sound.playSolve(soundOn(settings))
            sound.haptic(view, hapticsOn(settings), HapticKind.SOLVE)
            val voice = content.reward(vm.level.storyRewardId)?.kind == RewardKind.VOICE
            delay(if (voice) 1500 else 450)
            onSolved(vm.level.storyRewardId)
        }
    }

    StationAtmosphere(reduceMotion = reduceMotion) {
        Box(Modifier.fillMaxSize()) {
            StationScreen {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.frame_n, levelId),
                            color = Amber,
                            style = MaterialTheme.typography.headlineMedium,
                            maxLines = 1,
                        )
                        Text(
                            puzzleTitle(vm.level.puzzleType.name),
                            color = StationInk,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                        )
                        if (vm.multiTotal > 1) {
                            Text(stringResource(R.string.multi_step, vm.multiStep, vm.multiTotal), color = ScopeBlue, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    StationBackButton {
                        if (vm.engine.progress() < 0.15f) leaveNow() else showLeave = true
                    }
                }
                Spacer(Modifier.height(6.dp))
                SyncMeter(progress = sync.coerceAtLeast(if (vm.solved) 1f else 0f).let { if (it == 0f) vm.engine.progress() else it })
                Spacer(Modifier.height(6.dp))
                Column(
                    Modifier
                        .weight(1f, fill = true)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(18.dp), ambientColor = ScopeBlue.copy(alpha = 0.35f))
                            .border(1.dp, Amber.copy(alpha = 0.28f), RoundedCornerShape(18.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(StationNight.copy(alpha = 0.4f), StationPanel.copy(alpha = 0.75f)),
                                ),
                                RoundedCornerShape(18.dp),
                            )
                            .padding(14.dp),
                    ) {
                        PuzzleBoard(
                            engine = vm.engine,
                            revision = vm.revision,
                            onChanged = {
                                val current = (vm.engine as? MultiEngine)?.current ?: vm.engine
                                val cable = current as? CableEngine
                                val cassette = current as? CassetteEngine
                                val rejected = cable?.justRejected == true
                                when {
                                    rejected -> {
                                        sound.playReject(soundOn(settings))
                                        sound.haptic(view, hapticsOn(settings), HapticKind.REJECT)
                                    }
                                    cassette?.justSwapped == true -> {
                                        sound.playReel(soundOn(settings))
                                        if (cassette.justLocked) sound.playLock(soundOn(settings))
                                    }
                                    else -> {
                                        sound.playTick(soundOn(settings))
                                        sound.haptic(view, hapticsOn(settings), HapticKind.TICK)
                                    }
                                }
                                vm.bump()
                                sync = vm.engine.progress()
                                sound.setLockCloseness(soundOn(settings), sync)
                                if (vm.lockPulse) {
                                    sound.playLock(soundOn(settings))
                                    sound.haptic(view, hapticsOn(settings), HapticKind.LOCK)
                                }
                            },
                            onProgress = {
                                sync = it
                                sound.setLockCloseness(soundOn(settings), it)
                            },
                            onPitch = { hz ->
                                if (hz == null) sound.stopPitch() else sound.playPitch(soundOn(settings), hz)
                            },
                            boardModifier = Modifier.fillMaxWidth(),
                        )
                    }
                    vm.hint?.let { h ->
                        Spacer(Modifier.height(8.dp))
                        Text(h.textFor(lang), style = MaterialTheme.typography.bodyMedium, color = Amber)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = {
                            sound.playClick(soundOn(settings))
                            vm.reset()
                            sync = 0f
                        },
                        modifier = Modifier.weight(1f).height(48.dp).semantics { contentDescription = "reset" },
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = StationPanel, contentColor = StationInk),
                        shape = RoundedCornerShape(10.dp),
                    ) { Text(stringResource(R.string.reset), style = MaterialTheme.typography.bodyLarge) }
                    OutlinedButton(
                        onClick = {
                            sound.playClick(soundOn(settings))
                            vm.showHint()
                        },
                        modifier = Modifier.weight(1f).height(48.dp).semantics { contentDescription = "hint" },
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = StationPanel, contentColor = StationInk),
                        shape = RoundedCornerShape(10.dp),
                    ) { Text(stringResource(R.string.hint), style = MaterialTheme.typography.bodyLarge) }
                }
            }
            SolveBurst(active = showFlash, reduceMotion = reduceMotion)
            AnimatedVisibility(visible = showFlash, enter = fadeIn(), exit = fadeOut()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(R.string.solved_flash),
                        color = Amber,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .background(StationPanel.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                    )
                }
            }
        }
    }
    if (showHow) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(puzzleTitle(vm.level.puzzleType.name)) },
            text = { Text(puzzleHowTo(vm.engine)) },
            confirmButton = {
                TextButton(onClick = {
                    showHow = false
                    scope.launch { progressRepo.markHowSeen(howKey) }
                }) { Text(stringResource(R.string.got_it)) }
            },
        )
    }
    if (showLeave) {
        AlertDialog(
            onDismissRequest = { showLeave = false },
            title = { Text(stringResource(R.string.leave_puzzle)) },
            text = { Text(stringResource(R.string.leave_puzzle_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showLeave = false
                    leaveNow()
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showLeave = false }) { Text(stringResource(R.string.stay)) }
            },
        )
    }
}

@Composable
private fun puzzleTitle(type: String): String = when (type) {
    "WAVE" -> stringResource(R.string.puzzle_wave)
    "CABLE" -> stringResource(R.string.puzzle_cable)
    "CASSETTE" -> stringResource(R.string.puzzle_cassette)
    "FREQUENCY" -> stringResource(R.string.puzzle_frequency)
    "MULTI" -> stringResource(R.string.puzzle_multi)
    else -> type
}

@Composable
private fun puzzleHowTo(engine: ru.akarakuts.echostation.puzzle.PuzzleEngine): String {
    val current = (engine as? MultiEngine)?.current ?: engine
    return when (current) {
        is WaveEngine -> stringResource(R.string.how_wave)
        is CableEngine -> stringResource(if (current.isUntangle) R.string.how_cable_untangle else R.string.how_cable_match)
        is CassetteEngine -> stringResource(R.string.how_cassette)
        is FrequencyEngine -> stringResource(R.string.how_frequency)
        else -> ""
    }
}

@Composable
private fun kindLabel(kind: RewardKind): String = when (kind) {
    RewardKind.LOG -> stringResource(R.string.kind_log)
    RewardKind.VOICE -> stringResource(R.string.kind_voice)
    RewardKind.PHOTO -> stringResource(R.string.kind_photo)
    RewardKind.LETTER -> stringResource(R.string.kind_letter)
}

@Composable
fun StoryCardScreen(
    rewardId: String,
    content: ContentRepository,
    progressRepo: ProgressRepository,
    onContinue: () -> Unit,
) {
    val reward = content.reward(rewardId)
    val settings by progressRepo.settings.collectAsStateWithLifecycle(null)
    val progress by progressRepo.progress.collectAsStateWithLifecycle(Progress())
    val lang = storyLang()
    val reduce = settings?.reduceMotion == true
    val kind = reward?.kind
    val fullBody = reward?.bodyFor(lang).orEmpty()
    var shown by remember(rewardId, reduce) { mutableIntStateOf(if (reduce || kind != RewardKind.LOG) fullBody.length else 0) }
    LaunchedEffect(rewardId, fullBody, reduce, kind) {
        if (kind != RewardKind.LOG || reduce) {
            shown = fullBody.length
            return@LaunchedEffect
        }
        shown = 0
        while (shown < fullBody.length) {
            delay(18)
            shown = (shown + 1).coerceAtMost(fullBody.length)
        }
    }
    val letterCount = progress.collectedRewardIds.count { it.removePrefix("r").toIntOrNull()?.let { n -> n in 51..80 } == true }
    StationAtmosphere(reduceMotion = reduce) {
        StationScreen {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .clickable(enabled = kind == RewardKind.LOG && shown < fullBody.length) { shown = fullBody.length },
                verticalArrangement = Arrangement.Center,
            ) {
                if (kind == RewardKind.VOICE) {
                    StationHero(reduceMotion = reduce)
                    Spacer(Modifier.height(12.dp))
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(StationNight.copy(alpha = 0.72f), RoundedCornerShape(12.dp))
                            .border(1.dp, ScopeGlow.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(kindLabel(RewardKind.VOICE), style = MaterialTheme.typography.labelLarge, color = ScopeGlow)
                        Text(reward?.titleFor(lang).orEmpty(), style = MaterialTheme.typography.headlineMedium, color = Amber)
                        Text(
                            fullBody,
                            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                            color = ScopeGlow.copy(alpha = 0.92f),
                        )
                    }
                } else {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .rotate(if (kind == RewardKind.LETTER) -0.6f else -1.8f)
                            .shadow(24.dp, RoundedCornerShape(4.dp), ambientColor = Color.Black.copy(alpha = 0.55f))
                            .background(Paper, RoundedCornerShape(4.dp))
                            .padding(start = 14.dp, top = 14.dp, end = 14.dp, bottom = 28.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            kind?.let { k ->
                                Text(kindLabel(k), style = MaterialTheme.typography.labelLarge, color = AmberDeep)
                            }
                            Text(
                                text = reward?.titleFor(lang).orEmpty(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = PaperInk,
                            )
                            if (kind == RewardKind.PHOTO || reward?.imageAsset != null) {
                                StationPhotoFrame(imageAsset = reward?.imageAsset)
                            }
                            if (kind == RewardKind.LETTER && letterCount in 1..79) {
                                Text(
                                    stringResource(R.string.letter_parts, letterCount),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = AmberDeep,
                                )
                            }
                            Text(
                                text = if (kind == RewardKind.LOG) fullBody.take(shown) else fullBody,
                                style = MaterialTheme.typography.bodyLarge,
                                color = PaperInk.copy(alpha = 0.86f),
                            )
                            if (kind == RewardKind.LETTER) {
                                Text(
                                    if (lang == "ru" || lang == "uk") "Мама" else "Mum",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                                    color = PaperInk,
                                    modifier = Modifier.align(Alignment.End),
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = StationNight),
                shape = RoundedCornerShape(14.dp),
            ) { Text(stringResource(R.string.continue_btn), fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
fun ArchiveScreen(
    content: ContentRepository,
    progressRepo: ProgressRepository,
    onBack: () -> Unit,
) {
    val progress by progressRepo.progress.collectAsStateWithLifecycle(Progress())
    val settings by progressRepo.settings.collectAsStateWithLifecycle(null)
    val lang = storyLang()
    var tab by remember { mutableIntStateOf(0) }
    val all = remember(progress.collectedRewardIds) {
        content.rewards().values.filter { it.id in progress.collectedRewardIds }.sortedBy { it.id }
    }
    val items = when (tab) {
        0 -> all.filter { it.kind == RewardKind.LOG }
        1 -> all.filter { it.kind == RewardKind.VOICE }
        2 -> all.filter { it.kind == RewardKind.PHOTO }
        else -> all.filter { it.kind == RewardKind.LETTER }
    }
    val marks = progress.derivedMarks()
    StationAtmosphere(reduceMotion = settings?.reduceMotion == true) {
        StationScreen {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.archive), style = MaterialTheme.typography.headlineMedium, color = Amber)
                StationBackButton(onBack)
            }
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    0 to R.string.archive_tab_log,
                    1 to R.string.archive_tab_voice,
                    2 to R.string.archive_tab_photo,
                    3 to R.string.archive_tab_letter,
                ).forEach { (i, res) ->
                    OutlinedButton(
                        onClick = { tab = i },
                        shape = RoundedCornerShape(20.dp),
                        colors = if (tab == i) {
                            ButtonDefaults.outlinedButtonColors(contentColor = Amber, containerColor = StationPanel)
                        } else {
                            ButtonDefaults.outlinedButtonColors(contentColor = StationInk, containerColor = StationNight)
                        },
                    ) { Text(stringResource(res), style = MaterialTheme.typography.bodyMedium) }
                }
            }
            if (marks.isNotEmpty()) {
                Text(stringResource(R.string.marks_title), color = ScopeGlow, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp))
                Text(
                    buildList {
                        if ("first_word" in marks) add(stringResource(R.string.mark_first_word))
                        if ("name_found" in marks) add(stringResource(R.string.mark_name_found))
                        if ("letter_done" in marks) add(stringResource(R.string.mark_letter_done))
                        if ("three_tones" in marks) add(stringResource(R.string.mark_three_tones))
                    }.joinToString(" · "),
                    color = Amber,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (items.isEmpty()) {
                Text(stringResource(R.string.archive_empty), color = StaticGray, modifier = Modifier.padding(top = 24.dp))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
                    items(items) { reward: StoryReward ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .background(StationPanel, RoundedCornerShape(12.dp))
                                .border(1.dp, ScopeBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(14.dp),
                        ) {
                            Text(kindLabel(reward.kind), style = MaterialTheme.typography.labelLarge, color = ScopeGlow)
                            Text(reward.titleFor(lang), color = Amber)
                            if (reward.kind == RewardKind.PHOTO) {
                                Spacer(Modifier.height(8.dp))
                                StationPhotoFrame(imageAsset = reward.imageAsset)
                            }
                            Text(reward.bodyFor(lang), style = MaterialTheme.typography.bodyMedium, color = StaticGray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    progressRepo: ProgressRepository,
    onBack: () -> Unit,
) {
    val settings by progressRepo.settings.collectAsStateWithLifecycle(null)
    val progress by progressRepo.progress.collectAsStateWithLifecycle(Progress())
    val scope = rememberCoroutineScope()
    var confirmReset by remember { mutableStateOf(false) }
    var confirmLevels by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }
    val s = settings ?: GameSettings()
    val sys = LocalConfiguration.current.locales[0] ?: Locale.getDefault()
    val uiLang = sys.language
    val storyNote = uiLang.lowercase(Locale.ROOT).let { it != "ru" && it != "en" && it != "uk" && !it.startsWith("en") }

    StationAtmosphere(reduceMotion = s.reduceMotion) {
        StationScreen {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium, color = Amber)
                StationBackButton(onBack)
            }
            SettingSwitch(stringResource(R.string.sound), s.sound) {
                scope.launch { progressRepo.updateSettings { it.copy(sound = !it.sound) } }
            }
            SettingSwitch(stringResource(R.string.ambiance), s.ambiance) {
                scope.launch { progressRepo.updateSettings { it.copy(ambiance = !it.ambiance) } }
            }
            SettingSwitch(stringResource(R.string.haptics), s.haptics) {
                scope.launch { progressRepo.updateSettings { it.copy(haptics = !it.haptics) } }
            }
            SettingSwitch(stringResource(R.string.reduce_motion), s.reduceMotion) {
                scope.launch { progressRepo.updateSettings { it.copy(reduceMotion = !it.reduceMotion) } }
            }
            Text(stringResource(R.string.display_name), color = StaticGray)
            BasicTextField(
                value = s.displayName,
                onValueChange = { v -> scope.launch { progressRepo.updateSettings { it.copy(displayName = v.take(24)) } } },
                textStyle = TextStyle(color = Amber),
                cursorBrush = SolidColor(Amber),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StationPanel, RoundedCornerShape(8.dp))
                    .padding(12.dp),
            )
            if (storyNote) {
                Text(stringResource(R.string.story_on_english), color = Amber, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { confirmLevels = true }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.reset_levels))
            }
            OutlinedButton(onClick = { confirmReset = true }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.reset_all))
            }
            OutlinedButton(onClick = { showPrivacy = true }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.privacy))
            }
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.about_version, BuildConfig.VERSION_NAME), color = Amber, style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.about_line), color = StaticGray, style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.about_contact), color = ScopeGlow, style = MaterialTheme.typography.bodyMedium)
            val marks = progress.derivedMarks()
            if (marks.isNotEmpty()) {
                Text(stringResource(R.string.marks_title), color = ScopeGlow, style = MaterialTheme.typography.labelLarge)
                if ("first_word" in marks) Text(stringResource(R.string.mark_first_word), color = Amber)
                if ("name_found" in marks) Text(stringResource(R.string.mark_name_found), color = Amber)
                if ("letter_done" in marks) Text(stringResource(R.string.mark_letter_done), color = Amber)
                if ("three_tones" in marks) Text(stringResource(R.string.mark_three_tones), color = Amber)
            }
            }
        }
    }
    if (confirmLevels) {
        AlertDialog(
            onDismissRequest = { confirmLevels = false },
            title = { Text(stringResource(R.string.reset_levels)) },
            text = { Text(stringResource(R.string.reset_levels_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmLevels = false
                    scope.launch { progressRepo.resetLevels() }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmLevels = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text(stringResource(R.string.reset_all)) },
            text = { Text(stringResource(R.string.reset_progress_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmReset = false
                    scope.launch { progressRepo.resetProgress() }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
    if (showPrivacy) {
        AlertDialog(
            onDismissRequest = { showPrivacy = false },
            title = { Text(stringResource(R.string.privacy)) },
            text = { Text(stringResource(R.string.privacy_body)) },
            confirmButton = {
                TextButton(onClick = { showPrivacy = false }) { Text(stringResource(R.string.got_it)) }
            },
        )
    }
}

@Composable
private fun SettingSwitch(label: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
}

@Composable
fun EpilogueChoiceScreen(
    progressRepo: ProgressRepository,
    onChosen: (EpilogueTone) -> Unit,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val settings by progressRepo.settings.collectAsStateWithLifecycle(null)
    StationAtmosphere(reduceMotion = settings?.reduceMotion == true) {
        StationScreen {
            Text(stringResource(R.string.epilogue_title), style = MaterialTheme.typography.headlineMedium, color = Amber)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    scope.launch {
                        progressRepo.setEpilogue(EpilogueTone.BROADCAST)
                        onChosen(EpilogueTone.BROADCAST)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = StationNight),
                shape = RoundedCornerShape(14.dp),
            ) { Text(stringResource(R.string.epilogue_broadcast)) }
            Button(
                onClick = {
                    scope.launch {
                        progressRepo.setEpilogue(EpilogueTone.ARCHIVE)
                        onChosen(EpilogueTone.ARCHIVE)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ScopeBlue),
                shape = RoundedCornerShape(14.dp),
            ) { Text(stringResource(R.string.epilogue_archive)) }
            OutlinedButton(
                onClick = {
                    scope.launch {
                        progressRepo.setEpilogue(EpilogueTone.LEAVE)
                        onChosen(EpilogueTone.LEAVE)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
            ) { Text(stringResource(R.string.epilogue_leave)) }
            TextButton(onClick = onBack) { Text(stringResource(R.string.back), style = MaterialTheme.typography.bodyLarge, color = ScopeGlow) }
        }
    }
}

@Composable
fun EpilogueScreen(
    tone: EpilogueTone,
    content: ContentRepository,
    progressRepo: ProgressRepository,
    onMenu: () -> Unit,
) {
    val id = when (tone) {
        EpilogueTone.BROADCAST -> "epilogue_broadcast"
        EpilogueTone.ARCHIVE -> "epilogue_archive"
        EpilogueTone.LEAVE -> "epilogue_leave"
    }
    val reward = content.reward(id)
    val settings by progressRepo.settings.collectAsStateWithLifecycle(null)
    val lang = storyLang()
    StationAtmosphere(reduceMotion = settings?.reduceMotion == true, dawn = true) {
        StationScreen {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
            Text(
                stringResource(R.string.epilogue_done),
                style = MaterialTheme.typography.displayLarge.copy(
                    brush = Brush.linearGradient(listOf(Amber, ScopeGlow)),
                ),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                reward?.bodyFor(lang).orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                color = StaticGray,
            )
            if (tone == EpilogueTone.LEAVE && settings?.displayName.orEmpty().isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    stringResource(R.string.leave_signed, settings?.displayName.orEmpty()),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Amber,
                )
            }
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onMenu,
                colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = StationNight),
                shape = RoundedCornerShape(14.dp),
            ) { Text(stringResource(R.string.to_menu)) }
            }
        }
    }
}

@Composable
fun ActBreakScreen(act: Int, progressRepo: ProgressRepository, onContinue: () -> Unit) {
    val settings by progressRepo.settings.collectAsStateWithLifecycle(null)
    StationAtmosphere(reduceMotion = settings?.reduceMotion == true) {
        StationScreen {
            Spacer(Modifier.weight(1f))
            Text(
                stringResource(if (act == 1) R.string.act_break_1 else R.string.act_break_2),
                style = MaterialTheme.typography.headlineMedium,
                color = Amber,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = StationNight),
                shape = RoundedCornerShape(14.dp),
            ) { Text(stringResource(R.string.continue_btn), fontWeight = FontWeight.SemiBold) }
        }
    }
}
