package com.example.myexpenses.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = NeonCyan.copy(alpha = 0.3f),
    onPrimaryContainer = NeonCyan,
    secondary = NeonPurple,
    onSecondary = Color.White,
    secondaryContainer = NeonPurple.copy(alpha = 0.3f),
    onSecondaryContainer = NeonPurple,
    tertiary = NeonPink,
    onTertiary = Color.White,
    tertiaryContainer = NeonPink.copy(alpha = 0.3f),
    onTertiaryContainer = NeonPink,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.3f),
    onErrorContainer = ErrorRed,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DeepSpace,
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    outline = NeonCyan.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = LightCyan,
    onPrimary = Color.White,
    primaryContainer = LightCyan.copy(alpha = 0.2f),
    onPrimaryContainer = LightCyan,
    secondary = LightPurple,
    onSecondary = Color.White,
    secondaryContainer = LightPurple.copy(alpha = 0.2f),
    onSecondaryContainer = LightPurple,
    tertiary = NeonPink,
    onTertiary = Color.White,
    tertiaryContainer = NeonPink.copy(alpha = 0.2f),
    onTertiaryContainer = NeonPink,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,
    background = LightBackground,
    onBackground = Color.Black,
    surface = LightSurface,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color.Black.copy(alpha = 0.7f),
    outline = LightCyan.copy(alpha = 0.5f)
)

val LocalSpacing = compositionLocalOf { Spacing() }

data class Spacing(
    val extraSmall: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(4f),
    val small: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(8f),
    val medium: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(16f),
    val large: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(24f),
    val extraLarge: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(32f)
)

@Composable
fun TimsAIExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}