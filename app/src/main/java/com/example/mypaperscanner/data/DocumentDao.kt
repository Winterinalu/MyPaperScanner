package com.example.mypaperscanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<ScannedDocument>>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: Long): ScannedDocument?

    @Query("SELECT * FROM documents ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentDocuments(limit: Int): Flow<List<ScannedDocument>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: ScannedDocument): Long

    @Update
    suspend fun updateDocument(document: ScannedDocument)

    @Delete
    suspend fun deleteDocument(document: ScannedDocument)
    
    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocumentById(id: Long)
}
