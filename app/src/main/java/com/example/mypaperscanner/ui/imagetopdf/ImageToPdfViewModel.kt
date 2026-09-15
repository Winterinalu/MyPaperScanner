package com.example.mypaperscanner.ui.imagetopdf

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mypaperscanner.data.DocumentRepository
import com.example.mypaperscanner.util.PdfConverter
import kotlinx.coroutines.launch

class ImageToPdfViewModel(private val repository: DocumentRepository) : ViewModel() {

    var selectedImages by mutableStateOf<List<Uri>>(emptyList())
    var isConverting by mutableStateOf(false)
    var conversionComplete by mutableStateOf(false)

    fun onImagesSelected(uris: List<Uri>) {
        selectedImages = uris
    }

    fun convert(context: Context, onSuccess: () -> Unit) {
        if (selectedImages.isEmpty()) return
        
        isConverting = true
        viewModelScope.launch {
            val newId = PdfConverter.imagesToPdf(context, selectedImages, repository)
            isConverting = false
            if (newId != -1L) {
                conversionComplete = true
                onSuccess()
            }
        }
    }
}

class ImageToPdfViewModelFactory(private val repository: DocumentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageToPdfViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageToPdfViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
