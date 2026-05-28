package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RoyalBlueLight,
    secondary = LightSlate,
    tertiary = AccentGold,
    background = DarkSlate,
    surface = DarkSlate,
    onPrimary = KeyWhite,
    onSecondary = KeyWhite,
    onBackground = KeyWhite,
    onSurface = KeyWhite
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalBlue,
    secondary = DarkSlate,
    tertiary = AccentGold,
    background = BGSoft,
    surface = KeyWhite,
    onPrimary = KeyWhite,
    onSecondary = DarkSlate,
    onBackground = DarkSlate,
    onSurface = DarkSlate
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to prioritize brand Royal Blue identity
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
