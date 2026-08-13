/** StationArt — герой главного экрана и polaroid-кадры станции. */
package ru.akarakuts.echostation.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun StationHero(modifier: Modifier = Modifier, reduceMotion: Boolean = false) {
    val phase = if (reduceMotion) {
        0.4f
    } else {
        val transition = rememberInfiniteTransition(label = "hero")
        val p by transition.animateFloat(
            0f, (PI * 2).toFloat(),
            infiniteRepeatable(tween(4500, easing = LinearEasing), RepeatMode.Restart),
            label = "p",
        )
        p
    }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(132.dp),
    ) {
        val w = size.width
        val h = size.height
        // Brass bezel
        drawRoundRect(
            brush = Brush.linearGradient(listOf(Color(0xFF3A3228), Color(0xFF1A1612))),
            cornerRadius = CornerRadius(18f, 18f),
        )
        drawRoundRect(
            Amber.copy(alpha = 0.22f),
            cornerRadius = CornerRadius(18f, 18f),
            style = Stroke(1.6f),
        )
        val inset = 10f
        val inner = Size(w - inset * 2, h - inset * 2)
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF0C141F), Color(0xFF15100C))),
            topLeft = Offset(inset, inset),
            size = inner,
            cornerRadius = CornerRadius(12f, 12f),
        )

        // Scope face
        val sx = inset + 16f
        val sy = inset + 14f
        val sw = inner.width * 0.58f
        val sh = inner.height - 28f
        drawRoundRect(
            Color(0xFF07140F),
            topLeft = Offset(sx, sy),
            size = Size(sw, sh),
            cornerRadius = CornerRadius(10f, 10f),
        )
        drawRoundRect(
            ScopeGlow.copy(alpha = 0.28f),
            topLeft = Offset(sx, sy),
            size = Size(sw, sh),
            cornerRadius = CornerRadius(10f, 10f),
            style = Stroke(1.4f),
        )
        for (i in 1..3) {
            val y = sy + sh * i / 4f
            drawLine(ScopeGlow.copy(alpha = 0.12f), Offset(sx + 8f, y), Offset(sx + sw - 8f, y), 1f)
        }
        val mid = sy + sh / 2f
        val live = Path()
        val ghost = Path()
        val steps = 32
        for (i in 0..steps) {
            val t = i / steps.toFloat()
            val x = sx + 10f + t * (sw - 20f)
            val y1 = mid + sin(t * PI * 4 + phase).toFloat() * (sh * 0.22f)
            val y2 = mid + sin(t * PI * 4 + 0.4f).toFloat() * (sh * 0.18f)
            if (i == 0) {
                live.moveTo(x, y1)
                ghost.moveTo(x, y2)
            } else {
                live.lineTo(x, y1)
                ghost.lineTo(x, y2)
            }
        }
        drawBloomPath(ghost, Amber.copy(alpha = 0.55f), 2.2f)
        drawBloomPath(live, ScopeGlow, 2.6f)

        // Knobs column
        val kx = sx + sw + 28f
        val ky = sy + 18f
        for (i in 0..2) {
            val cy = ky + i * 42f
            drawCircle(Color(0xFF2A241C), radius = 16f, center = Offset(kx, cy))
            drawCircle(Amber.copy(alpha = 0.85f), radius = 5f, center = Offset(kx, cy))
            drawCircle(Amber.copy(alpha = 0.18f), radius = 22f, center = Offset(kx, cy))
            val ang = phase * 0.3f + i
            drawLine(
                Amber,
                Offset(kx, cy),
                Offset(kx + cosA(ang) * 11f, cy + sinA(ang) * 11f),
                2.2f,
            )
        }
        // Frequency digits
        drawRoundRect(
            Color(0xFF0A1A12),
            topLeft = Offset(kx + 28f, sy + 12f),
            size = Size(w - kx - 44f, 28f),
            cornerRadius = CornerRadius(4f, 4f),
        )
    }
}

private fun cosA(a: Float) = kotlin.math.cos(a)
private fun sinA(a: Float) = kotlin.math.sin(a)

@Composable
fun StationPhotoFrame(modifier: Modifier = Modifier, imageAsset: String? = null) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.55f),
    ) {
        val w = size.width
        val h = size.height
        drawRoundRect(Color(0xFFEDE6D6), cornerRadius = CornerRadius(8f, 8f))
        drawRoundRect(
            Color(0xFFD4C4A8),
            cornerRadius = CornerRadius(8f, 8f),
            style = Stroke(1.5f),
        )
        val inset = 12f
        val photoH = h * 0.74f
        drawRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF1A2230), Color(0xFF0C1018))),
            topLeft = Offset(inset, inset),
            size = Size(w - inset * 2, photoH),
        )
        when (imageAsset) {
            "photo_chair" -> drawChair(inset, photoH, w)
            "photo_relays" -> drawRelays(inset, photoH, w)
            "photo_drawing" -> drawKidTower(inset, photoH, w)
            "photo_thermos" -> drawThermos(inset, photoH, w)
            "photo_window" -> drawPalmWindow(inset, photoH, w)
            "photo_shelf" -> drawShelf(inset, photoH, w)
            else -> drawConsolePhoto(inset, photoH, w)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawConsolePhoto(inset: Float, photoH: Float, w: Float) {
        drawRect(
            brush = Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF243656).copy(alpha = 0.45f))),
            topLeft = Offset(w * 0.55f, inset),
            size = Size(w * 0.38f, photoH * 0.45f),
        )
        // Desk
        drawRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF5A3A22), Color(0xFF3A2416))),
            topLeft = Offset(inset, inset + photoH * 0.58f),
            size = Size(w - inset * 2, photoH * 0.42f),
        )
        // Scope
        val ox = inset + 22f
        val oy = inset + 18f
        val ow = w * 0.44f
        val oh = photoH * 0.42f
        drawRoundRect(Color(0xFF0A1612), topLeft = Offset(ox, oy), size = Size(ow, oh), cornerRadius = CornerRadius(6f, 6f))
        drawRoundRect(
            ScopeGlow.copy(alpha = 0.4f),
            topLeft = Offset(ox, oy),
            size = Size(ow, oh),
            cornerRadius = CornerRadius(6f, 6f),
            style = Stroke(1.5f),
        )
        val midY = oy + oh / 2f
        for (i in 0 until 24) {
            val x1 = ox + 10f + i * ((ow - 20f) / 24f)
            val y1 = midY + sin(i * 0.55f) * 11f
            val x2 = ox + 10f + (i + 1) * ((ow - 20f) / 24f)
            val y2 = midY + sin((i + 1) * 0.55f) * 11f
            drawLine(ScopeGlow, Offset(x1, y1), Offset(x2, y2), strokeWidth = 2.2f, cap = StrokeCap.Round)
        }
        // Lamp
        val lx = w * 0.74f
        val ly = inset + 32f
        drawCircle(Amber.copy(alpha = 0.22f), radius = 36f, center = Offset(lx, ly))
        drawCircle(Amber, radius = 8f, center = Offset(lx, ly))
        // Mug + steam
        drawRoundRect(
            Color(0xFF6B3E28),
            topLeft = Offset(w * 0.66f, inset + photoH * 0.62f),
            size = Size(26f, 30f),
            cornerRadius = CornerRadius(4f, 4f),
        )
        drawArc(
            Color(0xFF6B3E28),
            startAngle = -70f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(w * 0.66f + 20f, inset + photoH * 0.64f),
            size = Size(14f, 16f),
            style = Stroke(2.5f),
        )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawChair(inset: Float, photoH: Float, w: Float) {
    drawCircle(Color(0xFFE8E0C8).copy(alpha = 0.2f), 28f, Offset(w * 0.78f, inset + 36f))
    drawRoundRect(Color(0xFF4A3828), Offset(w * 0.32f, inset + photoH * 0.42f), Size(w * 0.28f, photoH * 0.38f), CornerRadius(6f, 6f))
    drawRoundRect(Color(0xFF6B4A32), Offset(w * 0.28f, inset + photoH * 0.28f), Size(w * 0.36f, photoH * 0.18f), CornerRadius(8f, 8f))
    drawLine(Amber.copy(alpha = 0.7f), Offset(w * 0.38f, inset + photoH * 0.78f), Offset(w * 0.62f, inset + photoH * 0.88f), 6f, StrokeCap.Round)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRelays(inset: Float, photoH: Float, w: Float) {
    for (i in 0 until 6) {
        val x = inset + 18f + i * ((w - inset * 2) / 6.4f)
        drawRoundRect(Color(0xFF2A3038), Offset(x, inset + 20f), Size(28f, photoH - 36f), CornerRadius(3f, 3f))
        drawCircle(if (i == 6) Amber else ScopeGlow, 5f, Offset(x + 14f, inset + 36f))
        drawLine(ScopeGlow.copy(alpha = 0.5f), Offset(x + 14f, inset + 48f), Offset(x + 14f, inset + photoH - 28f), 2f)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawKidTower(inset: Float, photoH: Float, w: Float) {
    drawRect(Color(0xFFF4E8C8), Offset(inset + 24f, inset + 16f), Size(w - inset * 2 - 48f, photoH - 28f))
    val cx = w / 2f
    drawLine(Color(0xFF3A2A18), Offset(cx, inset + 28f), Offset(cx, inset + photoH - 24f), 3f)
    drawLine(Color(0xFF3A2A18), Offset(cx - 40f, inset + 44f), Offset(cx + 40f, inset + 44f), 2f)
    drawCircle(Amber, 10f, Offset(cx, inset + 28f))
    drawCircle(Color(0xFF3A2A18).copy(alpha = 0.3f), 18f, Offset(w * 0.72f, inset + 40f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawThermos(inset: Float, photoH: Float, w: Float) {
    val x = w * 0.42f
    drawRoundRect(Color(0xFF6B3E28), Offset(x, inset + photoH * 0.22f), Size(w * 0.16f, photoH * 0.62f), CornerRadius(10f, 10f))
    drawRoundRect(Amber.copy(alpha = 0.5f), Offset(x + 6f, inset + photoH * 0.18f), Size(w * 0.12f, 16f), CornerRadius(4f, 4f))
    drawCircle(Amber.copy(alpha = 0.25f), 40f, Offset(w * 0.72f, inset + 40f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPalmWindow(inset: Float, photoH: Float, w: Float) {
    drawRect(Color(0xFF243656).copy(alpha = 0.55f), Offset(inset + 20f, inset + 12f), Size(w - inset * 2 - 40f, photoH * 0.7f))
    drawLine(AmberDim.copy(alpha = 0.6f), Offset(w / 2, inset + 12f), Offset(w / 2, inset + photoH * 0.7f), 3f)
    drawCircle(Color(0xFFF4ECD4).copy(alpha = 0.35f), 22f, Offset(w * 0.7f, inset + 36f))
    drawCircle(Amber.copy(alpha = 0.35f), 28f, Offset(w * 0.38f, inset + photoH * 0.55f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawShelf(inset: Float, photoH: Float, w: Float) {
    for (r in 0 until 3) {
        val y = inset + 28f + r * (photoH / 3.2f)
        drawRect(Color(0xFF4A3828), Offset(inset + 16f, y), Size(w - inset * 2 - 32f, 8f))
        if (r != 1) {
            drawRect(Color(0xFF2A241C), Offset(inset + 28f, y - 36f), Size(22f, 36f))
            drawRect(Amber.copy(alpha = 0.4f), Offset(inset + 56f, y - 40f), Size(18f, 40f))
        }
    }
}

@Composable
fun StationHubBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(72.dp)) {
        val w = size.width
        val h = size.height
        drawLine(Amber.copy(alpha = 0.35f), Offset(w * 0.12f, h * 0.88f), Offset(w * 0.12f, h * 0.18f), 3f)
        drawLine(ScopeGlow.copy(alpha = 0.3f), Offset(w * 0.08f, h * 0.32f), Offset(w * 0.16f, h * 0.32f), 2f)
        drawRoundRect(
            StationPanel.copy(alpha = 0.55f),
            Offset(w * 0.28f, h * 0.42f),
            Size(w * 0.44f, h * 0.48f),
            CornerRadius(8f, 8f),
        )
        drawRect(ScopeGlow.copy(alpha = 0.12f), Offset(w * 0.32f, h * 0.5f), Size(w * 0.36f, h * 0.22f))
        drawCircle(Amber.copy(alpha = 0.25f), 16f, Offset(w * 0.82f, h * 0.28f))
        drawRoundRect(Color(0xFF2A241C).copy(alpha = 0.5f), Offset(w * 0.78f, h * 0.55f), Size(w * 0.14f, h * 0.32f), CornerRadius(4f, 4f))
    }
}
