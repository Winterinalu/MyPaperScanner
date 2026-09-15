package com.example.mypaperscanner.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileOutputStream

object ThumbnailUtils {
    
    fun generateThumbnail(context: Context, file: File, isPdf: Boolean): String? {
        val thumbnailFile = File(context.cacheDir, "thumb_${file.nameWithoutExtension}.jpg")
        if (thumbnailFile.exists()) return thumbnailFile.absolutePath

        return try {
            val bitmap = if (isPdf) {
                renderPdfThumbnail(file)
            } else {
                decodeImageThumbnail(file)
            }

            if (bitmap != null) {
                FileOutputStream(thumbnailFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
                }
                bitmap.recycle()
                thumbnailFile.absolutePath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun renderPdfThumbnail(file: File): Bitmap? {
        val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(pfd)
        if (renderer.pageCount == 0) {
            renderer.close()
            pfd.close()
            return null
        }
        val page = renderer.openPage(0)
        val bitmap = Bitmap.createBitmap(page.width / 2, page.height / 2, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        renderer.close()
        pfd.close()
        return bitmap
    }

    private fun decodeImageThumbnail(file: File): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.absolutePath, options)
        
        val targetWidth = 300
        val targetHeight = 300
        var inSampleSize = 1
        
        if (options.outHeight > targetHeight || options.outWidth > targetWidth) {
            val halfHeight = options.outHeight / 2
            val halfWidth = options.outWidth / 2
            while (halfHeight / inSampleSize >= targetHeight && halfWidth / inSampleSize >= targetWidth) {
                inSampleSize *= 2
            }
        }
        
        options.inJustDecodeBounds = false
        options.inSampleSize = inSampleSize
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }
}
