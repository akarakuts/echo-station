/** VisualEffects — ночная станция: окно, пыль, CRT, bloom, частицы победы. */
package ru.akarakuts.echostation.ui.theme

import android.app.ActivityManager
import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun StationAtmosphere(
    reduceMotion: Boolean,
    dawn: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val context = LocalContext.current
    val lifeState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsStateWithLifecycle()
    val paused = lifeState < Lifecycle.State.RESUMED
    val lowRam = remember(context) {
        runCatching {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.isLowRamDevice || am.memoryClass < 192
        }.getOrDefault(false)
    }
    val lite = reduceMotion || paused || lowRam || android.os.Build.VERSION.SDK_INT < 26
    if (lite) {
        StationAtmosphereLayer(dawn = dawn, phase = 0.4f, scan = 0.4f, twinkle = 0.5f, lite = true, content = content)
    } else {
        val transition = rememberInfiniteTransition(label = "atm")
        val phase by transition.animateFloat(
            initialValue = 0f,
            targetValue = (PI * 2).toFloat(),
            animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Restart),
            label = "phase",
        )
        val scan by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing), RepeatMode.Restart),
            label = "scan",
        )
        val twinkle by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Reverse),
            label = "twinkle",
        )
        StationAtmosphereLayer(dawn, phase, scan, twinkle, lite = false, content)
    }
}

@Composable
private fun StationAtmosphereLayer(
    dawn: Boolean,
    phase: Float,
    scan: Float,
    twinkle: Float,
    lite: Boolean,
    content: @Composable BoxScope.() -> Unit,
) {
    val stars = remember { List(if (lite) 6 else 12) { Triple(Random.nextFloat(), Random.nextFloat(), 0.4f + Random.nextFloat() * 0.6f) } }
    val dust = remember { List(if (lite) 0 else 6) { Triple(Random.nextFloat(), Random.nextFloat(), 0.6f + Random.nextFloat()) } }

    Box(Modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawRect(
                brush = Brush.verticalGradient(
                    0f to if (dawn) Color(0xFF1A1418) else Color(0xFF070B12),
                    0.35f to if (dawn) Color(0xFF2A1C18) else Color(0xFF101820),
                    0.7f to if (dawn) Color(0xFF3A2418) else Color(0xFF0E1622),
                    1f to if (dawn) Color(0xFF5A3A22) else Color(0xFF1A140E),
                ),
            )

            // Night sky window (upper right)
            val winL = w * 0.58f
            val winT = h * 0.06f
            val winW = w * 0.36f
            val winH = h * 0.22f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF08101C), Color(0xFF1A3058), Color(0xFF2A1840)),
                ),
                topLeft = Offset(winL, winT),
                size = Size(winW, winH),
                cornerRadius = CornerRadius(6f, 6f),
            )
            // Moon
            val moon = Offset(winL + winW * 0.28f, winT + winH * 0.32f)
            drawCircle(Color(0xFFE8E0C8).copy(alpha = 0.18f), radius = 22f, center = moon)
            drawCircle(Color(0xFFF4ECD4), radius = 11f, center = moon)
            // Light shaft from window
            val shaft = Path().apply {
                moveTo(winL + 4f, winT + winH)
                lineTo(winL + winW - 4f, winT + winH)
                lineTo(winL + winW + 40f, h * 0.72f)
                lineTo(winL - 50f, h * 0.78f)
                close()
            }
            drawPath(shaft, Amber.copy(alpha = 0.045f))
            stars.forEach { (sx, sy, bright) ->
                val x = winL + 8f + sx * (winW - 16f)
                val y = winT + 8f + sy * (winH - 16f)
                val a = if (lite) {
                    (0.35f + bright * 0.4f).coerceIn(0.2f, 0.8f)
                } else {
                    (0.25f + bright * 0.55f * (0.5f + 0.5f * sin(twinkle * PI * 2 + sx * 8).toFloat()))
                        .coerceIn(0.1f, 0.95f)
                }
                drawCircle(Color.White.copy(alpha = a), radius = 1.1f + bright, center = Offset(x, y))
            }
            // Window frame + mullion
            drawRoundRect(
                AmberDim.copy(alpha = 0.55f),
                topLeft = Offset(winL - 4f, winT - 4f),
                size = Size(winW + 8f, winH + 8f),
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(3.5f),
            )
            drawLine(AmberDim.copy(alpha = 0.4f), Offset(winL + winW / 2, winT), Offset(winL + winW / 2, winT + winH), 2f)
            drawLine(AmberDim.copy(alpha = 0.4f), Offset(winL, winT + winH / 2), Offset(winL + winW, winT + winH / 2), 2f)

            // Distant radio mast in the window
            val mastX = winL + winW * 0.72f
            drawLine(Color.White.copy(alpha = 0.25f), Offset(mastX, winT + winH - 6f), Offset(mastX, winT + 18f), 1.5f)
            drawLine(Color.White.copy(alpha = 0.18f), Offset(mastX - 10f, winT + 40f), Offset(mastX + 10f, winT + 40f), 1.2f)

            // Warm lamp (left)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Amber.copy(alpha = 0.28f), Amber.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(w * 0.18f, h * 0.14f),
                    radius = w * 0.55f,
                ),
                radius = w * 0.55f,
                center = Offset(w * 0.18f, h * 0.14f),
            )
            // Cool scope glow (bottom)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ScopeBlue.copy(alpha = 0.26f), Color.Transparent),
                    center = Offset(w * 0.72f, h * 0.78f),
                    radius = w * 0.48f,
                ),
                radius = w * 0.48f,
                center = Offset(w * 0.72f, h * 0.78f),
            )

            // Floor console silhouette
            val deskY = h * 0.88f
            drawRect(
                Color(0xFF121018).copy(alpha = 0.55f),
                topLeft = Offset(0f, deskY),
                size = Size(w, h - deskY),
            )
            drawLine(Amber.copy(alpha = 0.12f), Offset(0f, deskY), Offset(w, deskY), 2f)

            if (!lite) {
                drawGhostWave(h * 0.48f, phase, ScopeGlow.copy(alpha = 0.28f), 2.4f, 3.0f, h * 0.032f)
                drawGhostWave(h * 0.48f + 26f, phase + 1.15f, Amber.copy(alpha = 0.16f), 1.8f, 2.4f, h * 0.022f)
                dust.forEach { (dx, dy, speed) ->
                    val x = (dx * w + sin(phase * speed + dy * 6).toFloat() * 18f)
                    val y = (dy * h * 0.55f + cos(phase * 0.6f * speed).toFloat() * 10f)
                    drawCircle(Amber.copy(alpha = 0.12f), radius = 1.6f, center = Offset(x, y))
                }
                val sy = h * scan
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, ScopeGlow.copy(alpha = 0.06f), Color.Transparent),
                        startY = sy - 48f,
                        endY = sy + 48f,
                    ),
                    topLeft = Offset(0f, sy - 48f),
                    size = Size(w, 96f),
                )
            }

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.62f)),
                    center = Offset(w / 2, h * 0.48f),
                    radius = size.maxDimension * 0.78f,
                ),
            )
        }
        content()
    }
}

private fun DrawScope.drawGhostWave(
    mid: Float,
    phase: Float,
    color: Color,
    stroke: Float,
    cycles: Float,
    amp: Float,
) {
    val path = Path()
    val steps = 32
    for (i in 0..steps) {
        val t = i / steps.toFloat()
        val x = size.width * t
        val y = mid + sin(t * PI * cycles + phase).toFloat() * amp
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    drawPath(path, color, style = Stroke(width = stroke, cap = StrokeCap.Round))
}

fun DrawScope.drawBloomPath(path: Path, color: Color, coreWidth: Float) {
    drawPath(path, color.copy(alpha = color.alpha * 0.28f), style = Stroke(coreWidth * 2.2f, cap = StrokeCap.Round))
    drawPath(path, color, style = Stroke(coreWidth, cap = StrokeCap.Round))
}

@Composable
fun SolveBurst(active: Boolean, reduceMotion: Boolean) {
    if (!active || reduceMotion) return
    val seeds = remember { List(12) { Random.nextFloat() to Random.nextFloat() } }
    val transition = rememberInfiniteTransition(label = "burst")
    val t by transition.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(1100, easing = LinearEasing), RepeatMode.Restart),
        label = "t",
    )
    Canvas(Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        drawCircle(Amber.copy(alpha = 0.12f * (1f - t)), radius = size.minDimension * 0.15f * (0.4f + t), center = Offset(cx, cy))
        seeds.forEachIndexed { i, _ ->
            val ang = (i / 12f) * PI.toFloat() * 2f
            val dist = t * size.minDimension * 0.42f
            val x = cx + cos(ang) * dist
            val y = cy + sin(ang) * dist
            drawCircle(
                color = (if (i % 2 == 0) Amber else ScopeGlow).copy(alpha = 1f - t),
                radius = 4.5f * (1f - t),
                center = Offset(x, y),
            )
        }
    }
}
