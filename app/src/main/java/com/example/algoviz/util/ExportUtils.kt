package com.example.algoviz.util

import android.content.ContentValues
import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextPaint
import com.example.algoviz.domain.model.AiMessage
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ExportUtils {

    fun exportChatToPdf(context: Context, messages: List<AiMessage>, fileName: String): Boolean {
        return try {
            val pdfDocument = PdfDocument()
            var pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas

            val paint = TextPaint()
            paint.textSize = 12f

            var yPosition = 50f
            val xPosition = 50f
            val lineSpacing = 16f

            messages.forEach { msg ->
                val lines = "(${msg.role.uppercase()}): ${msg.content}".split("\n")
                lines.forEach { line ->
                    if (yPosition > 800) {
                        pdfDocument.finishPage(page)
                        pageInfo = PdfDocument.PageInfo.Builder(595, 842, pdfDocument.pages.size + 1).create()
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        yPosition = 50f
                    }
                    canvas.drawText(line, xPosition, yPosition, paint)
                    yPosition += lineSpacing
                }
                yPosition += lineSpacing // Extra space between messages
            }

            pdfDocument.finishPage(page)

            val outputStream = getOutputStream(context, "$fileName.pdf", "application/pdf")
            if (outputStream != null) {
                pdfDocument.writeTo(outputStream)
                pdfDocument.close()
                outputStream.close()
                true
            } else {
                pdfDocument.close()
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun exportChatToTxt(context: Context, messages: List<AiMessage>, fileName: String): Boolean {
        return try {
            val outputStream = getOutputStream(context, "$fileName.txt", "text/plain")
            if (outputStream != null) {
                val sb = StringBuilder()
                messages.forEach { msg ->
                    sb.append("(${msg.role.uppercase()}): ${msg.content}\n\n")
                }
                outputStream.write(sb.toString().toByteArray())
                outputStream.close()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun getOutputStream(context: Context, fileName: String, mimeType: String): OutputStream? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let { resolver.openOutputStream(it) }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            FileOutputStream(file)
        }
    }
}
