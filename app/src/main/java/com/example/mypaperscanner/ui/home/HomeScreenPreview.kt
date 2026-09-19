package com.example.mypaperscanner.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mypaperscanner.data.DocumentSourceType
import com.example.mypaperscanner.data.ScannedDocument
import com.example.mypaperscanner.ui.theme.MyPaperScannerTheme

@Preview(showBackground = true)
@Composable
fun HomeScreenModernPreview() {
    val mockDocuments = listOf(
        ScannedDocument(
            id = 1,
            title = "Project Proposal.pdf",
            filePath = "/path/to/proposal.pdf",
            createdAt = System.currentTimeMillis(),
            pageCount = 12,
            sourceType = DocumentSourceType.SCAN
        ),
        ScannedDocument(
            id = 2,
            title = "Receipt Sept 15.jpg",
            filePath = "/path/to/receipt.jpg",
            createdAt = System.currentTimeMillis() - 3600000,
            pageCount = 1,
            sourceType = DocumentSourceType.SCAN
        ),
        ScannedDocument(
            id = 3,
            title = "Meeting Notes.pdf",
            filePath = "/path/to/notes.pdf",
            createdAt = System.currentTimeMillis() - 86400000,
            pageCount = 3,
            sourceType = DocumentSourceType.SCAN
        )
    )

    MyPaperScannerTheme(darkTheme = false) {
        HomeScreenContent(
            recentDocuments = mockDocuments,
            isRefreshing = false,
            onRefresh = {},
            onScanClick = {},
            onImageToPdfClick = {},
            onPdfToPictureClick = {},
            onLibraryClick = {},
            onDocumentClick = {},
            onSettingsClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenModernDarkPreview() {
    val mockDocuments = listOf(
        ScannedDocument(
            id = 1,
            title = "Confidential Report.pdf",
            filePath = "/path/to/report.pdf",
            createdAt = System.currentTimeMillis(),
            pageCount = 45,
            sourceType = DocumentSourceType.SCAN
        ),
        ScannedDocument(
            id = 2,
            title = "Invoice_2026.pdf",
            filePath = "/path/to/invoice.pdf",
            createdAt = System.currentTimeMillis() - 7200000,
            pageCount = 2,
            sourceType = DocumentSourceType.SCAN
        )
    )

    MyPaperScannerTheme(darkTheme = true) {
        HomeScreenContent(
            recentDocuments = mockDocuments,
            isRefreshing = false,
            onRefresh = {},
            onScanClick = {},
            onImageToPdfClick = {},
            onPdfToPictureClick = {},
            onLibraryClick = {},
            onDocumentClick = {},
            onSettingsClick = {}
        )
    }
}
