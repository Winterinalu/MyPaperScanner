package com.example.mypaperscanner.data

import kotlinx.coroutines.flow.Flow

class DocumentRepository(private val documentDao: DocumentDao) {
    val allDocuments: Flow<List<ScannedDocument>> = documentDao.getAllDocuments()

    fun getRecentDocuments(limit: Int): Flow<List<ScannedDocument>> {
        return documentDao.getRecentDocuments(limit)
    }

    suspend fun getDocumentById(id: Long): ScannedDocument? {
        return documentDao.getDocumentById(id)
    }

    suspend fun insertDocument(document: ScannedDocument): Long {
        return documentDao.insertDocument(document)
    }

    suspend fun updateDocument(document: ScannedDocument) {
        documentDao.updateDocument(document)
    }

    suspend fun deleteDocument(document: ScannedDocument) {
        documentDao.deleteDocument(document)
    }

    suspend fun deleteDocumentById(id: Long) {
        documentDao.deleteDocumentById(id)
    }
}
