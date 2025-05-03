package com.example.smartinkjournal.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.example.smartinkjournal.data.model.Stroke
import androidx.core.graphics.createBitmap
import kotlinx.serialization.InternalSerializationApi

/**
 * Utility object for converting stroke data into a Bitmap.
 * This is primarily used for exporting handwritten notes or for OCR processing.
 */
@OptIn(InternalSerializationApi::class)
object BitmapUtils {

    /**
     * Creates a Bitmap from a list of strokes.
     *
     * @param strokes List of strokes to be drawn.
     * @param width Width of the resulting bitmap.
     * @param height Height of the resulting bitmap.
     * @return A Bitmap representing the handwritten strokes.
     */
    fun createBitmapFromStrokes(
        strokes: List<Stroke>,
        width: Int = 1080,
        height: Int = 1920
    ): Bitmap {
        val bitmap = createBitmap(width, height) // Creates a blank bitmap of given size
        val canvas = Canvas(bitmap) // Canvas to draw on the bitmap

        // Paint configuration for stroke drawing
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = 4f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        // Draw each stroke path onto the canvas
        strokes.forEach { stroke ->
            val path = Path()
            stroke.points.forEachIndexed { index, point ->
                if (index == 0) path.moveTo(point.x, point.y)
                else path.lineTo(point.x, point.y)
            }
            canvas.drawPath(path, paint)
        }

        return bitmap
    }
}
