package com.example.mypaperscanner.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mypaperscanner.data.DocumentRepository
import com.example.mypaperscanner.data.ScannedDocument
import com.example.mypaperscanner.util.ThumbnailUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(private val repository: DocumentRepository) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val recentDocuments: StateFlow<List<ScannedDocument>> = repository.getRecentDocuments(5)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refresh(context: Context) {
        viewModelScope.launch {
            _isRefreshing.value = true
            performThumbnailCheck(context, force = true)
            _isRefreshing.value = false
        }
    }

    fun checkAndGenerateThumbnails(context: Context, force: Boolean = false) {
        viewModelScope.launch {
            performThumbnailCheck(context, force = force)
        }
    }

    private suspend fun performThumbnailCheck(context: Context, force: Boolean) {
        recentDocuments.value.forEach { doc ->
            val thumbnailPath = doc.thumbnailPath
            val thumbFile = thumbnailPath?.let { File(it) }
            
            val oneDayMillis = 24 * 60 * 60 * 1000L
            val isExpired = (thumbFile != null && thumbFile.exists()) && 
                           (System.currentTimeMillis() - thumbFile.lastModified() > oneDayMillis)
            
            val needsGeneration = thumbnailPath == null || 
                                 thumbFile == null || 
                                 !thumbFile.exists() || 
                                 (force && isExpired)

            if (needsGeneration) {
                val file = File(doc.filePath)
                if (file.exists()) {
                    val isPdf = doc.filePath.endsWith(".pdf", ignoreCase = true)
                    val thumbPath = ThumbnailUtils.generateThumbnail(
                        context, 
                        file, 
                        isPdf, 
                        force = force && isExpired,
                    )
                    if (thumbPath != null && thumbPath != doc.thumbnailPath) {
                        repository.updateDocument(doc.copy(thumbnailPath = thumbPath))
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
