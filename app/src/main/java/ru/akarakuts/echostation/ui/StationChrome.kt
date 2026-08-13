/** StationChrome — общие inset-поля экрана и хелперы языка/звука. */
package ru.akarakuts.echostation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.akarakuts.echostation.R
import ru.akarakuts.echostation.puzzle.PuzzleHint
import ru.akarakuts.echostation.story.GameSettings
import ru.akarakuts.echostation.ui.theme.ScopeGlow

@Composable
fun StationScreen(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        content = content,
    )
}

@Composable
fun StationBackButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.heightIn(min = 48.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Text(
            stringResource(R.string.back),
            color = ScopeGlow,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

fun soundOn(settings: GameSettings?): Boolean = settings?.sound != false

fun hapticsOn(settings: GameSettings?): Boolean = settings?.haptics != false

fun PuzzleHint.textFor(lang: String): String =
    if (lang == "ru" || lang == "uk") messageRu else messageEn
