/** PuzzleViews — Compose UI для Wave/Cable/Cassette/Frequency/Multi. */
package ru.akarakuts.echostation.ui.puzzle

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import ru.akarakuts.echostation.R
import ru.akarakuts.echostation.puzzle.CableEngine
import ru.akarakuts.echostation.puzzle.CablePort
import ru.akarakuts.echostation.puzzle.CassetteEngine
import ru.akarakuts.echostation.puzzle.FrequencyEngine
import ru.akarakuts.echostation.puzzle.MultiEngine
import ru.akarakuts.echostation.puzzle.PuzzleEngine
import ru.akarakuts.echostation.puzzle.WaveEngine
import ru.akarakuts.echostation.ui.theme.Amber
import ru.akarakuts.echostation.ui.theme.ScopeBlue
import ru.akarakuts.echostation.ui.theme.ScopeGlow
import ru.akarakuts.echostation.ui.theme.StationPanel
import ru.akarakuts.echostation.ui.theme.drawBloomPath
import kotlin.math.PI

val PairPalette = listOf(
    Color(0xFFE8A54B),
    Color(0xFF6EC1E4),
    Color(0xFFE07A5F),
    Color(0xFF81B29A),
    Color(0xFFC9A0DC),
    Color(0xFFF2CC8F),
)

fun pairColor(pairId: Int): Color = PairPalette[pairId.mod(PairPalette.size)]

@Composable
fun PuzzleBoard(
    engine: PuzzleEngine,
    revision: Int,
    onChanged: () -> Unit,
    onProgress: (Float) -> Unit = {},
    onPitch: (Float?) -> Unit = {},
    boardModifier: Modifier = Modifier,
) {
    when (engine) {
        is WaveEngine -> WaveBoard(engine, revision, onChanged, onProgress, boardModifier)
        is CableEngine -> CableBoard(engine, revision, onChanged, boardModifier)
        is CassetteEngine -> CassetteBoard(engine, revision, onChanged, boardModifier)
        is FrequencyEngine -> FrequencyBoard(engine, revision, onChanged, onPitch, boardModifier)
        is MultiEngine -> PuzzleBoard(engine.current, revision, onChanged, onProgress, onPitch, boardModifier)
        else -> Text("Unknown puzzle", modifier = boardModifier)
    }
}

@Composable
fun SyncMeter(progress: Float, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.sync, (progress * 100).toInt().coerceIn(0, 100)),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = if (progress > 0.72f) ScopeGlow else Amber,
        )
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .semantics { contentDescription = "sync" },
            color = if (progress > 0.72f) ScopeGlow else Amber,
            trackColor = StationPanel,
            strokeCap = StrokeCap.Round,
        )
    }
}

@Composable
fun WaveBoard(
    engine: WaveEngine,
    revision: Int,
    onChanged: () -> Unit,
    onProgress: (Float) -> Unit = {},
    boardModifier: Modifier = Modifier,
) {
    var sweep by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(revision) {
        while (isActive && !engine.isSolved()) {
            delay(48)
            sweep += 0.12f
            engine.sampleHold()
            onProgress(engine.progress())
            if (engine.isSolved()) onChanged()
        }
    }
    val inBand = engine.inBand()
    Column(modifier = boardModifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp),
        ) {
            val w = size.width
            val h = size.height
            drawRoundRect(
                brush = Brush.linearGradient(listOf(Color(0xFF2A241C), Color(0xFF12100C))),
                cornerRadius = CornerRadius(16f, 16f),
            )
            val m = 10f
            drawRoundRect(
                Color(0xFF06140F),
                topLeft = Offset(m, m),
                size = Size(w - m * 2, h - m * 2),
                cornerRadius = CornerRadius(10f, 10f),
            )
            val ring = if (inBand) ScopeGlow.copy(alpha = 0.55f) else ScopeGlow.copy(alpha = 0.22f)
            drawRoundRect(
                ring,
                topLeft = Offset(m, m),
                size = Size(w - m * 2, h - m * 2),
                cornerRadius = CornerRadius(10f, 10f),
                style = Stroke(if (inBand) 2.4f else 1.4f),
            )
            val mid = h / 2f
            for (i in 1..4) {
                val y = h * i / 5f
                drawLine(ScopeGlow.copy(alpha = 0.10f), Offset(m + 8f, y), Offset(w - m - 8f, y), 1f)
            }
            for (i in 1..6) {
                val x = w * i / 7f
                drawLine(ScopeGlow.copy(alpha = 0.07f), Offset(x, m + 8f), Offset(x, h - m - 8f), 1f)
            }
            fun pathFor(target: Boolean): Path {
                val p = Path()
                val steps = 48
                for (i in 0..steps) {
                    val t = i / steps.toFloat() * (PI * 2).toFloat() + sweep
                    val x = m + 10f + (w - m * 2 - 20f) * i / steps.toFloat()
                    val y = mid - engine.sample(t, target) * (h * 0.38f)
                    if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
                }
                return p
            }
            drawBloomPath(pathFor(true), Amber.copy(alpha = if (inBand) 0.85f else 0.7f), 2.4f)
            drawBloomPath(pathFor(false), ScopeGlow, if (inBand) 3.4f else 2.8f)
            if (engine.isGlitching) {
                drawRect(Color(0xFF3A1818).copy(alpha = 0.35f), topLeft = Offset(m, m), size = Size(w - m * 2, h - m * 2))
                for (g in 0 until 7) {
                    val gy = m + 12f + (h - m * 2) * g / 7f
                    drawLine(ScopeGlow.copy(alpha = 0.35f), Offset(m + 8f, gy), Offset(w - m - 8f, gy + 6f), 2f)
                }
            }
            val lockW = (w - m * 2 - 20f) * engine.lockRatio.coerceIn(0f, 1f)
            drawRoundRect(
                (if (inBand) ScopeGlow else Amber).copy(alpha = 0.55f),
                topLeft = Offset(m + 10f, h - m - 14f),
                size = Size(lockW, 5f),
                cornerRadius = CornerRadius(3f, 3f),
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(stringResource(R.string.phase), style = MaterialTheme.typography.labelLarge, color = Amber)
        Slider(
            value = engine.phase,
            onValueChange = {
                engine.setPhase(it)
                onChanged()
            },
            valueRange = 0f..(PI * 2).toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = Amber,
                activeTrackColor = Amber,
                inactiveTrackColor = StationPanel,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "phase" },
        )
        if (engine.showsAmplitude) {
            Text(stringResource(R.string.amplitude), style = MaterialTheme.typography.labelLarge, color = ScopeBlue)
            Slider(
                value = engine.amplitude,
                onValueChange = {
                    engine.setAmplitude(it)
                    onChanged()
                },
                valueRange = 0.05f..1.2f,
                colors = SliderDefaults.colors(
                    thumbColor = ScopeGlow,
                    activeTrackColor = ScopeBlue,
                    inactiveTrackColor = StationPanel,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "amplitude" },
            )
        }
        if (engine.usesFrequency) {
            Text(stringResource(R.string.carrier), style = MaterialTheme.typography.labelLarge, color = ScopeGlow)
            Slider(
                value = engine.frequency,
                onValueChange = {
                    engine.setFrequency(it)
                    onChanged()
                },
                valueRange = 2f..6.2f,
                colors = SliderDefaults.colors(
                    thumbColor = ScopeGlow,
                    activeTrackColor = Amber,
                    inactiveTrackColor = StationPanel,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "frequency" },
            )
        }
    }
}

@Composable
fun CableBoard(
    engine: CableEngine,
    revision: Int,
    onChanged: () -> Unit,
    boardModifier: Modifier = Modifier,
) {
    val left = engine.ports.filter { it.side == CablePort.Side.LEFT }.sortedBy { it.index }
    val right = engine.ports.filter { it.side == CablePort.Side.RIGHT }.sortedBy { it.index }
    val n = left.size.coerceAtLeast(1)
    Box(
        modifier = boardModifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                if (engine.justRejected) Color(0xFF3A1818) else StationPanel,
                RoundedCornerShape(12.dp),
            )
            .padding(12.dp)
            .then(
                if (engine.isUntangle) {
                    Modifier.pointerInput(revision) {
                        var dragId: Int? = null
                        detectDragGestures(
                            onDragStart = { offset ->
                                dragId = null
                                if (offset.x < size.width * 0.62f) return@detectDragGestures
                                val row = (offset.y / size.height * n).toInt().coerceIn(0, n - 1)
                                dragId = engine.ports.firstOrNull {
                                    it.side == CablePort.Side.RIGHT && it.index == row
                                }?.id
                            },
                            onDrag = { change, _ ->
                                val id = dragId ?: return@detectDragGestures
                                val row = (change.position.y / size.height * n).toInt().coerceIn(0, n - 1)
                                engine.moveRightTo(id, row)
                                onChanged()
                                change.consume()
                            },
                            onDragEnd = { dragId = null },
                            onDragCancel = { dragId = null },
                        )
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val h = size.height
            val w = size.width
            engine.connectionList().forEach { (lId, rId) ->
                val lp = engine.ports.first { it.id == lId }
                val rp = engine.ports.first { it.id == rId }
                val y1 = (lp.index + 0.5f) / n * h
                val y2 = (rp.index + 0.5f) / n * h
                val cable = Path().apply {
                    moveTo(36f, y1)
                    cubicTo(w * 0.38f, y1, w * 0.62f, y2, w - 36f, y2)
                }
                val ok = engine.isPairCorrect(lId)
                val hi = lp.pairId == engine.highlightPairId
                val color = pairColor(lp.pairId).copy(alpha = if (ok) 0.95f else if (hi) 0.8f else 0.45f)
                drawBloomPath(cable, color, if (ok || hi) 3.8f else 2.6f)
            }
            if (engine.justRejected) {
                drawCircle(Amber.copy(alpha = 0.85f), 10f, Offset(w / 2f, h / 2f))
                drawCircle(Color.White.copy(alpha = 0.7f), 4f, Offset(w / 2f, h / 2f))
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            for (row in 0 until n) {
                val lp = left[row]
                val rp = right[row]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PortDot(
                        pairId = lp.pairId,
                        hideDigits = engine.hideDigits,
                        selected = engine.selectedLeft == lp.id,
                        highlight = lp.pairId == engine.highlightPairId,
                        onClick = {
                            engine.tap(lp.id)
                            onChanged()
                        },
                    )
                    PortDot(
                        pairId = rp.pairId,
                        hideDigits = engine.hideDigits,
                        selected = engine.selectedRight == rp.id,
                        highlight = rp.pairId == engine.highlightPairId,
                        onClick = {
                            engine.tap(rp.id)
                            onChanged()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun PortDot(
    pairId: Int,
    hideDigits: Boolean,
    selected: Boolean,
    highlight: Boolean,
    onClick: () -> Unit,
) {
    val color = pairColor(pairId)
    val label = "${pairId + 1}"
    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick)
            .semantics { contentDescription = "port $label" },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 3f
            val fill = if (selected) color else color.copy(alpha = if (highlight) 0.7f else 0.4f)
            val stroke = if (highlight) Amber else color
            drawPortShape(pairId, c, r, fill, stroke)
        }
        if (!hideDigits) {
            Text(label, color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.labelLarge)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPortShape(
    pairId: Int,
    c: Offset,
    r: Float,
    fill: Color,
    stroke: Color,
) {
    val strokeStyle = Stroke(3f)
    when (pairId.mod(6)) {
        0 -> {
            drawCircle(fill, r, c)
            drawCircle(stroke, r, c, style = strokeStyle)
        }
        1 -> {
            val s = Size(r * 1.6f, r * 1.6f)
            val o = Offset(c.x - s.width / 2f, c.y - s.height / 2f)
            drawRect(fill, o, s)
            drawRect(stroke, o, s, style = strokeStyle)
        }
        2 -> {
            val p = Path().apply {
                moveTo(c.x, c.y - r)
                lineTo(c.x + r, c.y)
                lineTo(c.x, c.y + r)
                lineTo(c.x - r, c.y)
                close()
            }
            drawPath(p, fill)
            drawPath(p, stroke, style = strokeStyle)
        }
        3 -> {
            drawLine(stroke, Offset(c.x - r, c.y), Offset(c.x + r, c.y), 7f, StrokeCap.Round)
        }
        4 -> {
            drawLine(stroke, Offset(c.x - r, c.y), Offset(c.x + r, c.y), 5f, StrokeCap.Round)
            drawLine(stroke, Offset(c.x, c.y - r), Offset(c.x, c.y + r), 5f, StrokeCap.Round)
        }
        else -> {
            drawCircle(fill.copy(alpha = 0.15f), r, c)
            drawCircle(stroke, r, c, style = Stroke(3.5f))
        }
    }
}

@Composable
fun CassetteBoard(
    engine: CassetteEngine,
    revision: Int,
    onChanged: () -> Unit,
    boardModifier: Modifier = Modifier,
) {
    val cols = engine.cols
    val rows = engine.rows
    val n = (cols * rows).coerceAtLeast(1)
    @Suppress("UNUSED_VARIABLE")
    val unusedRevision = revision
    Column(modifier = boardModifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(18.dp)
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            for (i in 0 until n) {
                val hFrac = (i + 1f) / n
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(ScopeGlow.copy(alpha = 0.15f + 0.7f * hFrac), RoundedCornerShape(2.dp)),
                )
            }
        }
        for (r in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (c in 0 until cols) {
                    val idx = r * cols + c
                    val value = engine.tiles[idx]
                    val selected = engine.selected == idx
                    val correct = engine.isSlotCorrect(idx)
                    val border = when {
                        selected -> Amber
                        correct -> ScopeGlow
                        else -> ScopeBlue
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.4f)
                            .background(
                                when {
                                    selected -> Amber.copy(alpha = 0.5f)
                                    correct -> ScopeGlow.copy(alpha = 0.18f)
                                    else -> StationPanel
                                },
                                RoundedCornerShape(6.dp),
                            )
                            .border(if (correct) 2.dp else 1.dp, border, RoundedCornerShape(6.dp))
                            .clickable {
                                engine.tap(idx)
                                onChanged()
                            }
                            .semantics { contentDescription = "strip ${value + 1}" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                            val barH = size.height * ((value + 1f) / n)
                            drawRoundRect(
                                color = (if (correct) ScopeGlow else Amber).copy(alpha = 0.9f),
                                topLeft = Offset(size.width * 0.28f, size.height - barH),
                                size = Size(size.width * 0.44f, barH),
                                cornerRadius = CornerRadius(3f, 3f),
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun FrequencyBoard(
    engine: FrequencyEngine,
    revision: Int,
    onChanged: () -> Unit,
    onPitch: (Float?) -> Unit = {},
    boardModifier: Modifier = Modifier,
) {
    val anyLocked = engine.positions.indices.any { engine.markerLocked(it) }
    val pulseA = if (anyLocked) {
        val pulse = rememberInfiniteTransition(label = "freq")
        val a by pulse.animateFloat(
            0.45f, 1f,
            infiniteRepeatable(tween(900), RepeatMode.Reverse),
            label = "a",
        )
        a
    } else {
        1f
    }
    Column(modifier = boardModifier.fillMaxWidth()) {
        if (engine.showZones) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.zone_fish), style = MaterialTheme.typography.labelLarge, color = ScopeGlow.copy(alpha = 0.8f))
                Text(stringResource(R.string.zone_closed), style = MaterialTheme.typography.labelLarge, color = Amber)
                Text(stringResource(R.string.zone_official), style = MaterialTheme.typography.labelLarge, color = ScopeBlue)
            }
            Spacer(Modifier.height(6.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp)
                .background(StationPanel, RoundedCornerShape(12.dp))
                .padding(16.dp),
        ) {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val boardW = maxWidth
                val boardH = maxHeight
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(revision) {
                            val w = size.width.toFloat()
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val nearest = engine.positions.indices.minByOrNull { i ->
                                        kotlin.math.abs(engine.positions[i] * w - offset.x)
                                    }
                                    if (nearest != null) {
                                        engine.beginDrag(nearest)
                                        engine.dragTo(offset.x / w)
                                        val target = engine.targets.getOrElse(nearest) { engine.positions[nearest] }
                                        onPitch(220f + target * 660f)
                                    }
                                },
                                onDragEnd = {
                                    engine.endDrag()
                                    onPitch(null)
                                    onChanged()
                                },
                                onDragCancel = {
                                    engine.endDrag()
                                    onPitch(null)
                                    onChanged()
                                },
                                onDrag = { change, _ ->
                                    engine.dragTo(change.position.x / w)
                                    val i = engine.dragging
                                    if (i != null) onPitch(220f + engine.positions[i] * 660f)
                                    onChanged()
                                    change.consume()
                                },
                            )
                        },
                ) {
                val y = size.height / 2f
                if (engine.showZones) {
                    drawRect(ScopeGlow.copy(alpha = 0.07f), Offset(0f, 0f), Size(size.width / 3f, size.height))
                    drawRect(Amber.copy(alpha = 0.10f), Offset(size.width / 3f, 0f), Size(size.width / 3f, size.height))
                    drawRect(ScopeBlue.copy(alpha = 0.08f), Offset(size.width * 2f / 3f, 0f), Size(size.width / 3f, size.height))
                }
                drawLine(ScopeBlue.copy(alpha = 0.55f), Offset(0f, y), Offset(size.width, y), strokeWidth = 5f)
                drawLine(ScopeGlow.copy(alpha = 0.35f), Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5f)
                engine.slots.forEachIndexed { si, s ->
                    val closed = si == engine.slots.size / 2
                    drawCircle(
                        (if (closed) Amber else ScopeGlow).copy(alpha = if (closed) 0.28f else 0.14f),
                        radius = if (closed) 18f else 16f,
                        center = Offset(s * size.width, y),
                    )
                    drawCircle(Amber.copy(alpha = 0.45f), radius = 4f, center = Offset(s * size.width, y))
                }
                engine.targets.forEachIndexed { i, t ->
                    val c = pairColor(i)
                    drawCircle(c.copy(alpha = 0.22f), radius = 20f, center = Offset(t * size.width, y - 28f))
                    drawCircle(c.copy(alpha = 0.85f), radius = 6f, center = Offset(t * size.width, y - 28f))
                }
                engine.positions.forEachIndexed { i, p ->
                    val c = pairColor(i)
                    val locked = engine.markerLocked(i)
                    val a = if (locked) pulseA else 1f
                    drawCircle(c.copy(alpha = 0.28f * a), radius = if (locked) 26f else 22f, center = Offset(p * size.width, y))
                    drawCircle(c.copy(alpha = a), radius = if (locked) 16f else 14f, center = Offset(p * size.width, y))
                    drawCircle(if (locked) Amber else Color.White, radius = 5f, center = Offset(p * size.width, y))
                }
                }
                engine.targets.forEachIndexed { i, t ->
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (boardW.toPx() * t - 22.dp.toPx()).roundToInt(),
                                    (boardH.toPx() / 2f - 22.dp.toPx()).roundToInt(),
                                )
                            }
                            .size(44.dp)
                            .clickable {
                                engine.beginDrag(i)
                                engine.dragTo(t)
                                engine.endDrag()
                                onChanged()
                            }
                            .semantics { contentDescription = "freq $i" },
                    )
                }
            }
        }
        if (engine.showLegend) {
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.legend_gap), style = MaterialTheme.typography.labelLarge, color = pairColor(0))
            Text(stringResource(R.string.legend_call), style = MaterialTheme.typography.labelLarge, color = pairColor(1))
        }
    }
}
