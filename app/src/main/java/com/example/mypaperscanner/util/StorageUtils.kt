package com.example.mypaperscanner.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object StorageUtils {
    fun saveFileToUri(context: Context, sourceFilePath: String, destinationUri: Uri): Boolean {
        return try {
            val sourceFile = File(sourceFilePath)
            if (!sourceFile.exists()) return false
            
            context.contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                sourceFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
