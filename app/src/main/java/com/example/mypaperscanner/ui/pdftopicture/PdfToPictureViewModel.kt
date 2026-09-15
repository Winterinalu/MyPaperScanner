package com.example.mypaperscanner.ui.pdftopicture

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
import com.example.mypaperscanner.util.ImageConverter
import kotlinx.coroutines.launch

class PdfToPictureViewModel(private val repository: DocumentRepository) : ViewModel() {

    var selectedPdf by mutableStateOf<Uri?>(null)
    var isConverting by mutableStateOf(false)
    var outputFormat by mutableStateOf(Bitmap.CompressFormat.JPEG)
    var quality by mutableStateOf(80)

    fun onPdfSelected(uri: Uri?) {
        selectedPdf = uri
    }

    fun convert(context: Context, onSuccess: () -> Unit) {
        val uri = selectedPdf ?: return
        
        isConverting = true
        viewModelScope.launch {
            val newIds = ImageConverter.pdfToImages(context, uri, outputFormat, quality, repository)
            isConverting = false
            if (newIds.isNotEmpty()) {
                onSuccess()
            }
        }
    }
}

class PdfToPictureViewModelFactory(private val repository: DocumentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PdfToPictureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PdfToPictureViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
