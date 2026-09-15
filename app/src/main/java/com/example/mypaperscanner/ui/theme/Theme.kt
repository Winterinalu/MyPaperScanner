package com.example.mypaperscanner.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BrandBlueDark,
    onPrimary = BrandInk,
    primaryContainer = BrandBlue,
    onPrimaryContainer = BrandBackground,
    secondary = BrandGreenDark,
    onSecondary = BrandInk,
    secondaryContainer = BrandGreen,
    onSecondaryContainer = BrandBackground,
    tertiary = BrandYellowDark,
    onTertiary = BrandInk,
    background = BrandBackgroundDark,
    onBackground = BrandInkDark,
    surface = BrandBackgroundDark,
    onSurface = BrandInkDark,
    error = Error,
    onError = OnError
)

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = BlueOnPrimary,
    primaryContainer = BluePrimaryContainer,
    onPrimaryContainer = BlueOnPrimaryContainer,
    secondary = BrandGreen,
    onSecondary = GreenOnSecondary,
    secondaryContainer = GreenSecondaryContainer,
    onSecondaryContainer = GreenOnSecondaryContainer,
    tertiary = BrandYellow,
    onTertiary = BrandInk,
    background = BrandBackground,
    onBackground = BrandInk,
    surface = BrandBackground,
    onSurface = BrandInk,
    error = Error,
    onError = OnError
)

@Composable
fun MyPaperScannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
