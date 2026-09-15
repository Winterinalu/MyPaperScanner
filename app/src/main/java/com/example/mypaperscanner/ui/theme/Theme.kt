package com.example.mypaperscanner.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BrandBlue,
    onPrimary = Color.Black,
    primaryContainer = BrandBlueDark,
    onPrimaryContainer = Color.Black,
    secondary = BrandGreen,
    onSecondary = Color.Black,
    secondaryContainer = BrandGreenDark,
    onSecondaryContainer = Color.Black,
    tertiary = BrandYellow,
    onTertiary = Color.Black,
    background = BrandBackgroundDark,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    error = Error,
    onError = OnError
)

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    primaryContainer = BluePrimaryContainer,
    onPrimaryContainer = BlueOnPrimaryContainer,
    secondary = BrandGreen,
    onSecondary = Color.White,
    secondaryContainer = GreenSecondaryContainer,
    onSecondaryContainer = GreenOnSecondaryContainer,
    tertiary = BrandYellow,
    onTertiary = Color.Black,
    background = BrandBackground,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
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
