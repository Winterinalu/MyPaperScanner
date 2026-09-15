package com.example.mypaperscanner.ui.scan

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mypaperscanner.data.DocumentRepository
import com.example.mypaperscanner.util.PdfConverter
import kotlinx.coroutines.launch
import java.io.File

class ScanViewModel(private val repository: DocumentRepository) : ViewModel() {

    private val _capturedImages = mutableStateListOf<Uri>()
    val capturedImages: List<Uri> get() = _capturedImages

    var isConverting by mutableStateOf(false)

    fun addImage(uri: Uri) {
        _capturedImages.add(uri)
    }

    fun removeImage(index: Int) {
        if (index in _capturedImages.indices) {
            val uri = _capturedImages[index]
            _capturedImages.removeAt(index)
            // Optional: delete file
            File(uri.path ?: "").delete()
        }
    }

    fun finishScan(context: Context, onSuccess: () -> Unit) {
        if (_capturedImages.isEmpty()) return
        
        isConverting = true
        viewModelScope.launch {
            val newId = PdfConverter.imagesToPdf(context, _capturedImages, repository)
            isConverting = false
            if (newId != -1L) {
                _capturedImages.clear()
                onSuccess()
            }
        }
    }
}

class ScanViewModelFactory(private val repository: DocumentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
