package com.example.mypaperscanner.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.mypaperscanner.ui.theme.MyPaperScannerTheme

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun FullBrandDialogViewPreview() {
    MyPaperScannerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray)
        ) {
            // Let's render the BrandDialog directly to see if the default Dialog wrapper behavior or width matches the screenshot.
            // But wait, Compose Preview cannot render window Dialog perfectly unless we call the underlying content or check its properties.
            // Let's call BrandDialog but use custom properties or see how it looks if we render it.
            BrandDialog(
                onDismissRequest = {},
                title = "Save Scan As",
                text = "Choose the output format for your document. Multi-page scans saved as Images will be split into individual files.",
                confirmButton = {
                    NeubrutalistButton(onClick = {}) { Text("PDF") }
                },
                dismissButton = {
                    NeubrutalistButton(onClick = {}) { Text("Images") }
                }
            )
        }
    }
}
