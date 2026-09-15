package com.example.mypaperscanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class ScannedDocument(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val filePath: String,
    val createdAt: Long,
    val pageCount: Int,
    val sourceType: DocumentSourceType,
    val thumbnailPath: String? = null
)

enum class DocumentSourceType {
    SCAN, IMAGE, WORD
}
