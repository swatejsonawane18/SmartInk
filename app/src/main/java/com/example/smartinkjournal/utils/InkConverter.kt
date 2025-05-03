package com.example.smartinkjournal.utils

import com.example.smartinkjournal.data.model.Stroke
import com.example.smartinkjournal.data.model.PointFWithTime
import com.google.mlkit.vision.digitalink.Ink
import kotlinx.serialization.InternalSerializationApi

/**
 * Utility object that converts app-specific stroke data to ML Kit's Ink format.
 * Used for handwriting recognition via ML Kit's Digital Ink APIs.
 */
@OptIn(InternalSerializationApi::class)
object InkConverter {

    /**
     * Converts a list of strokes (custom model) into an ML Kit Ink object.
     *
     * @param strokes List of custom Stroke objects representing handwriting input.
     * @return Ink object compatible with ML Kit's recognizer.
     */
    fun convert(strokes: List<Stroke>): Ink {
        val inkBuilder = Ink.Builder()

        // Convert each stroke
        strokes.forEach { stroke ->
            val strokeBuilder = Ink.Stroke.builder()

            // Add each point in the stroke to the Ink.Stroke
            stroke.points.forEach { point ->
                strokeBuilder.addPoint(
                    Ink.Point.create(point.x, point.y, point.timestamp)
                )
            }

            // Add the constructed stroke to the Ink object
            inkBuilder.addStroke(strokeBuilder.build())
        }

        return inkBuilder.build()
    }
}
