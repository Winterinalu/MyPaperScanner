package com.example.mypaperscanner.ui

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Library : Screen("library")
    object ImageToPdf : Screen("image_to_pdf")
    object PdfToPicture : Screen("pdf_to_picture")
    object PdfViewer : Screen("pdf_viewer/{docId}") {
        fun createRoute(docId: Long) = "pdf_viewer/$docId"
    }
    object Onboarding : Screen("onboarding")
    object Tutorial : Screen("tutorial")
    object Settings : Screen("settings")
}
