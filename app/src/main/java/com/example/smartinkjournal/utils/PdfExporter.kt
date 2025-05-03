package com.example.smartinkjournal.utils

import android.content.Context
import android.graphics.*
import android.os.Environment
import android.widget.Toast
import com.example.smartinkjournal.R
import com.example.smartinkjournal.data.model.Note
import android.graphics.pdf.PdfDocument
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import com.example.smartinkjournal.utils.smoothStroke
import kotlinx.serialization.InternalSerializationApi

/**
 * Utility object responsible for exporting a handwritten note to a PDF file.
 */
@OptIn(InternalSerializationApi::class)
object PdfExporter {

    /**
     * Creates and exports a PDF containing the handwritten strokes and recognized text.
     *
     * @param context Application context for accessing resources and file directories.
     * @param note The note to be exported.
     */
    fun exportNoteAsPdf(context: Context, note: Note) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Paint for drawing ink
        val paint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 3f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        // Paint for text
        val textPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 14f
        }

        // Draw recognized text
        canvas.drawText(
            context.getString(R.string.recognized_text_label),
            40f, 40f, textPaint
        )
        canvas.drawText(note.recognizedText, 40f, 60f, textPaint)

        canvas.drawText(
            context.getString(R.string.timestamp_label, formatTimestamp(note.timestamp)),
            40f, 90f, textPaint
        )

        // Check for strokes to draw
        val allPoints = note.strokes.flatMap { it.points }
        if (allPoints.isEmpty()) {
            Toast.makeText(
                context,
                context.getString(R.string.no_strokes_to_export),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Calculate scale and offset
        val minX = allPoints.minOf { it.x }
        val maxX = allPoints.maxOf { it.x }
        val minY = allPoints.minOf { it.y }
        val maxY = allPoints.maxOf { it.y }

        val scaleX = 500f / (maxX - minX).coerceAtLeast(1f)
        val scaleY = 600f / (maxY - minY).coerceAtLeast(1f)
        val scale = minOf(scaleX, scaleY)

        val offsetX = 50f
        val offsetY = 130f

        // Draw strokes
        for (stroke in note.strokes) {
            val smoothed = smoothStroke(stroke.points)
            val path = Path()
            smoothed.firstOrNull()?.let {
                path.moveTo((it.x - minX) * scale + offsetX, (it.y - minY) * scale + offsetY)
            }
            smoothed.drop(1).forEach {
                path.lineTo((it.x - minX) * scale + offsetX, (it.y - minY) * scale + offsetY)
            }
            canvas.drawPath(path, paint)
        }

        pdfDocument.finishPage(page)

        // Save the PDF file to Downloads
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "Note_${sdf.format(Date(note.timestamp))}.pdf"
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )

        try {
            FileOutputStream(file).use { pdfDocument.writeTo(it) }
            Log.d("PDF", "Saved at: ${file.absolutePath}")
            Toast.makeText(
                context,
                context.getString(R.string.pdf_saved_to, file.absolutePath),
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Log.e("PDF", "Error saving PDF", e)
            Toast.makeText(
                context,
                context.getString(R.string.pdf_export_failed),
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            pdfDocument.close()
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
