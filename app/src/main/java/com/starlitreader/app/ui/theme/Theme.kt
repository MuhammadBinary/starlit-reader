package com.starlitreader.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = StarGold,
    background = MidnightBlue,
    surface = DeepIndigo,
    onPrimary = MidnightBlue,
    onBackground = ParchmentCream,
    onSurface = ParchmentCream
)

private val LightColors = lightColorScheme(
    primary = DeepIndigo,
    background = ParchmentCream,
    surface = Color(0xFFFFFBF2),
    onPrimary = ParchmentCream,
    onBackground = DeepIndigo,
    onSurface = DeepIndigo
)

@Composable
fun StarlitReaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
