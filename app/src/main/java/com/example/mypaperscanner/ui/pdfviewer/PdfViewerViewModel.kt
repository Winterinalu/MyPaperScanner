package com.example.mypaperscanner.ui.pdfviewer

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mypaperscanner.data.DocumentRepository
import com.example.mypaperscanner.data.ScannedDocument
import com.example.mypaperscanner.util.ImageConverter
import com.example.mypaperscanner.util.PdfConverter
import kotlinx.coroutines.launch
import java.io.File

class PdfViewerViewModel(private val repository: DocumentRepository) : ViewModel() {

    var document by mutableStateOf<ScannedDocument?>(null)
    var isLoading by mutableStateOf(false)
    var isConverting by mutableStateOf(false)

    fun loadDocument(id: Long) {
        isLoading = true
        viewModelScope.launch {
            document = repository.getDocumentById(id)
            isLoading = false
        }
    }

    fun convertToImage(context: Context, onSuccess: () -> Unit) {
        val doc = document ?: return
        isConverting = true
        viewModelScope.launch {
            val file = File(doc.filePath)
            if (file.exists()) {
                val uri = Uri.fromFile(file)
                val newIds = ImageConverter.pdfToImages(
                    context = context,
                    pdfUri = uri,
                    format = Bitmap.CompressFormat.JPEG,
                    quality = 80,
                    repository = repository,
                    titlePrefix = doc.title.substringBeforeLast(".") + "_converted"
                )
                isConverting = false
                if (newIds.isNotEmpty()) {
                    onSuccess()
                }
            } else {
                isConverting = false
            }
        }
    }

    fun convertToPdf(context: Context, onSuccess: () -> Unit) {
        val doc = document ?: return
        isConverting = true
        viewModelScope.launch {
            val file = File(doc.filePath)
            if (file.exists()) {
                val uri = Uri.fromFile(file)
                val newId = PdfConverter.imagesToPdf(
                    context = context,
                    imageUris = listOf(uri),
                    repository = repository,
                    title = doc.title.substringBeforeLast(".") + "_converted.pdf"
                )
                isConverting = false
                if (newId != -1L) {
                    onSuccess()
                }
            } else {
                isConverting = false
            }
        }
    }
}

class PdfViewerViewModelFactory(private val repository: DocumentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PdfViewerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PdfViewerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
