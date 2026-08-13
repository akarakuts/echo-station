/** MainActivity — точка входа Compose. */
package ru.akarakuts.echostation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.graphics.toArgb
import androidx.core.os.LocaleListCompat
import ru.akarakuts.echostation.ui.EchoStationApp
import ru.akarakuts.echostation.ui.theme.EchoStationTheme
import ru.akarakuts.echostation.ui.theme.StationNight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!AppCompatDelegate.getApplicationLocales().isEmpty) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        }
        val night = StationNight.toArgb()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(night),
            navigationBarStyle = SystemBarStyle.dark(night),
        )
        setContent {
            EchoStationTheme {
                EchoStationApp()
            }
        }
    }
}
