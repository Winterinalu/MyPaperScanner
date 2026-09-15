package com.example.mypaperscanner.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mypaperscanner.data.DocumentRepository
import com.example.mypaperscanner.data.ScannedDocument
import com.example.mypaperscanner.util.ThumbnailUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(private val repository: DocumentRepository) : ViewModel() {

    val recentDocuments: StateFlow<List<ScannedDocument>> = repository.getRecentDocuments(5)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun checkAndGenerateThumbnails(context: Context) {
        viewModelScope.launch {
            recentDocuments.value.forEach { doc ->
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
}

class HomeViewModelFactory(private val repository: DocumentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
