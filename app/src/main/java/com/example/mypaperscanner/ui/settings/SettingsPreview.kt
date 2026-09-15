package com.example.mypaperscanner.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mypaperscanner.ui.theme.MyPaperScannerTheme

@Preview(showBackground = true)
@Composable
fun SettingsScreenLightPreview() {
    MyPaperScannerTheme(darkTheme = false) {
        SettingsScreen(
            isDarkMode = false,
            onDarkModeChange = {},
            onBackClick = {},
            onShowTutorialClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenDarkPreview() {
    MyPaperScannerTheme(darkTheme = true) {
        SettingsScreen(
            isDarkMode = true,
            onDarkModeChange = {},
            onBackClick = {},
            onShowTutorialClick = {}
        )
    }
}
