package com.example.mypaperscanner.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.example.mypaperscanner.data.DocumentRepository
import com.example.mypaperscanner.data.DocumentSourceType
import com.example.mypaperscanner.data.ScannedDocument
import com.example.mypaperscanner.util.ThumbnailUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ImageConverter {

    suspend fun pdfToImages(
        context: Context,
        pdfUri: Uri,
        format: Bitmap.CompressFormat,
        quality: Int,
        repository: DocumentRepository,
        titlePrefix: String? = null
    ): List<Long> = withContext(Dispatchers.IO) {
        val insertedIds = mutableListOf<Long>()
        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        
        try {
            pfd = context.contentResolver.openFileDescriptor(pdfUri, "r")
            if (pfd == null) return@withContext emptyList()
            
            renderer = PdfRenderer(pfd)
            val outputDir = context.getExternalFilesDir(null)
            val timestamp = System.currentTimeMillis()
            val extension = if (format == Bitmap.CompressFormat.PNG) "png" else "jpg"
            
            val baseTitle = titlePrefix ?: "PDF_Page"

            for (i in 0 until renderer.pageCount) {
                renderer.openPage(i).use { page ->
                    val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    
                    val fileName = "${baseTitle}_${i + 1}_$timestamp.$extension"
                    val outputFile = File(outputDir, fileName)
                    
                    FileOutputStream(outputFile).use { out ->
                        bitmap.compress(format, quality, out)
                    }
                    
                    val doc = ScannedDocument(
                        title = fileName,
                        filePath = outputFile.absolutePath,
                        createdAt = System.currentTimeMillis(),
                        pageCount = 1,
                        sourceType = DocumentSourceType.IMAGE,
                        thumbnailPath = ThumbnailUtils.generateThumbnail(context, outputFile, false)
                    )
                    insertedIds.add(repository.insertDocument(doc))
                    bitmap.recycle()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            renderer?.close()
            pfd?.close()
        }
        insertedIds
    }

    suspend fun saveImagesAsDocuments(
        context: Context,
        imageUris: List<Uri>,
        repository: DocumentRepository,
        titlePrefix: String? = null,
        sourceType: DocumentSourceType = DocumentSourceType.IMAGE
    ): List<Long> = withContext(Dispatchers.IO) {
        val insertedIds = mutableListOf<Long>()
        val outputDir = context.getExternalFilesDir(null)
        val timestamp = System.currentTimeMillis()
        val baseTitle = titlePrefix ?: "Scan"

        imageUris.forEachIndexed { index, uri ->
            try {
                val fileName = "${baseTitle}_${index + 1}_$timestamp.jpg"
                val outputFile = File(outputDir, fileName)
                
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }
                
                val doc = ScannedDocument(
                    title = fileName,
                    filePath = outputFile.absolutePath,
                    createdAt = System.currentTimeMillis(),
                    pageCount = 1,
                    sourceType = sourceType,
                    thumbnailPath = ThumbnailUtils.generateThumbnail(context, outputFile, false)
                )
                insertedIds.add(repository.insertDocument(doc))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        insertedIds
    }
}
