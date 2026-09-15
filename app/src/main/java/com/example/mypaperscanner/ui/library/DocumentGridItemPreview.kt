package com.example.mypaperscanner.ui.library

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mypaperscanner.data.DocumentSourceType
import com.example.mypaperscanner.data.ScannedDocument
import com.example.mypaperscanner.ui.theme.MyPaperScannerTheme

@Preview(showBackground = true)
@Composable
fun DocumentGridItemPreview() {
    MyPaperScannerTheme {
        DocumentGridItem(
            document = ScannedDocument(
                id = 1,
                title = "Scan_1_178947...",
                filePath = "sample.img",
                createdAt = System.currentTimeMillis(),
                pageCount = 1,
                sourceType = DocumentSourceType.IMAGE,
                thumbnailPath = null // We'll see how it looks without a thumbnail first
            ),
            selected = false,
            onClick = {},
            onLongClick = {},
            onDelete = {},
            onShare = {},
            onExportAsImage = {},
            onConvertToPdf = {},
            onSaveToDownloads = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DocumentGridItemWithThumbnailPreview() {
    MyPaperScannerTheme {
        DocumentGridItem(
            document = ScannedDocument(
                id = 2,
                title = "My Document",
                filePath = "sample.pdf",
                createdAt = System.currentTimeMillis(),
                pageCount = 5,
                sourceType = DocumentSourceType.SCAN,
                thumbnailPath = "https://example.com/thumb.jpg"
            ),
            selected = false,
            onClick = {},
            onLongClick = {},
            onDelete = {},
            onShare = {},
            onExportAsImage = {},
            onConvertToPdf = {},
            onSaveToDownloads = {}
        )
    }
}
