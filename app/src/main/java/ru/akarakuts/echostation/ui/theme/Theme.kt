/** Theme — янтарный / синий осциллограф, тёмный фон станции. */
package ru.akarakuts.echostation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Amber = Color(0xFFE8A54B)
val AmberDim = Color(0xFFB8792E)
val AmberDeep = Color(0xFF8A5A22)
val ScopeBlue = Color(0xFF3A7CA5)
val ScopeGlow = Color(0xFF6EC1E4)
val ScopeDeep = Color(0xFF1E4A66)
val StationNight = Color(0xFF0B1018)
val StationPanel = Color(0xFF1C2430)
val StationInk = Color(0xFFE8EEF4)
val StaticGray = Color(0xFFB4BCC8)
val Paper = Color(0xFFF2E4C4)
val PaperInk = Color(0xFF3A2A18)

private val colors = darkColorScheme(
    primary = Amber,
    onPrimary = StationNight,
    secondary = ScopeBlue,
    onSecondary = StationInk,
    background = StationNight,
    onBackground = StationInk,
    surface = StationPanel,
    onSurface = StationInk,
    outline = StaticGray,
)

private val typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 18.sp,
        lineHeight = 26.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = EchoMono,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = EchoMono,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.4.sp,
    ),
)

@Composable
fun EchoStationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content,
    )
}
