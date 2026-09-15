package com.example.mypaperscanner.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.example.mypaperscanner.data.DocumentRepository
import com.example.mypaperscanner.data.DocumentSourceType
import com.example.mypaperscanner.data.ScannedDocument
import com.example.mypaperscanner.util.ThumbnailUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object PdfConverter {

    suspend fun imagesToPdf(
        context: Context,
        imageUris: List<Uri>,
        repository: DocumentRepository,
        title: String? = null,
        sourceType: DocumentSourceType = DocumentSourceType.IMAGE
    ): Long = withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()
        
        try {
            imageUris.forEachIndexed { index, uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                
                val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, index + 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                pdfDocument.finishPage(page)
                bitmap.recycle()
            }

            val outputDir = context.getExternalFilesDir(null)
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", java.util.Locale.getDefault())
            val timestamp = sdf.format(java.util.Date())
            
            val finalTitle = title ?: "Scan_$timestamp.pdf"
            val outputFile = File(outputDir, finalTitle)
            
            pdfDocument.writeTo(FileOutputStream(outputFile))
            
            val doc = ScannedDocument(
                title = finalTitle,
                filePath = outputFile.absolutePath,
                createdAt = System.currentTimeMillis(),
                pageCount = imageUris.size,
                sourceType = sourceType,
                thumbnailPath = ThumbnailUtils.generateThumbnail(context, outputFile, true)
            )
            repository.insertDocument(doc)
        } catch (e: Exception) {
            e.printStackTrace()
            -1L
        } finally {
            pdfDocument.close()
        }
    }
}
