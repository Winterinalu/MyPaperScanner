package com.example.mypaperscanner.ui.library

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mypaperscanner.data.DocumentRepository
import com.example.mypaperscanner.data.ScannedDocument
import com.example.mypaperscanner.util.ImageConverter
import com.example.mypaperscanner.util.PdfConverter
import com.example.mypaperscanner.util.ThumbnailUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class LibraryViewModel(private val repository: DocumentRepository) : ViewModel() {

    val documents: StateFlow<List<ScannedDocument>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedIds = mutableStateListOf<Long>()
    var isSelectionMode by mutableStateOf(false)

    fun checkAndGenerateThumbnails(context: Context) {
        viewModelScope.launch {
            documents.value.forEach { doc ->
                if (doc.thumbnailPath == null) {
                    val file = File(doc.filePath)
                    if (file.exists()) {
                        val isPdf = doc.filePath.endsWith(".pdf", ignoreCase = true)
                        val thumbPath = ThumbnailUtils.generateThumbnail(context, file, isPdf)
                        if (thumbPath != null) {
                            repository.updateDocument(doc.copy(thumbnailPath = thumbPath))
                        }
                    }
                }
            }
        }
    }

    fun toggleSelection(docId: Long) {
        if (selectedIds.contains(docId)) {
            selectedIds.remove(docId)
            if (selectedIds.isEmpty()) isSelectionMode = false
        } else {
            selectedIds.add(docId)
            isSelectionMode = true
        }
    }

    fun clearSelection() {
        selectedIds.clear()
        isSelectionMode = false
    }

    fun deleteSelected() {
        viewModelScope.launch {
            val docsToDelete = documents.value.filter { it.id in selectedIds }
            docsToDelete.forEach { doc ->
                repository.deleteDocument(doc)
                File(doc.filePath).delete()
            }
            clearSelection()
        }
    }

    fun deleteDocument(document: ScannedDocument) {
        viewModelScope.launch {
            repository.deleteDocument(document)
            val file = File(document.filePath)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    fun renameDocument(document: ScannedDocument, newTitle: String) {
        viewModelScope.launch {
            val updatedDoc = document.copy(title = newTitle)
            repository.updateDocument(updatedDoc)
        }
    }

    fun exportToImage(context: Context, document: ScannedDocument, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val file = File(document.filePath)
            if (file.exists()) {
                val uri = Uri.fromFile(file)
                val newIds = ImageConverter.pdfToImages(
                    context = context,
                    pdfUri = uri,
                    format = Bitmap.CompressFormat.JPEG,
                    quality = 80,
                    repository = repository,
                    titlePrefix = document.title.substringBeforeLast(".") + "_converted"
                )
                if (newIds.isNotEmpty()) {
                    onSuccess()
                }
            }
        }
    }

    fun convertToPdf(context: Context, document: ScannedDocument, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val file = File(document.filePath)
            if (file.exists()) {
                val uri = Uri.fromFile(file)
                val newId = PdfConverter.imagesToPdf(
                    context = context,
                    imageUris = listOf(uri),
                    repository = repository,
                    title = document.title.substringBeforeLast(".") + "_converted.pdf"
                )
                if (newId != -1L) {
                    onSuccess()
                }
            }
        }
    }
}

class LibraryViewModelFactory(private val repository: DocumentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LibraryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
