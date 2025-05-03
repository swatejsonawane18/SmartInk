@file:OptIn(InternalSerializationApi::class)

package com.example.smartinkjournal.utils

import com.example.smartinkjournal.data.model.PointFWithTime
import kotlinx.serialization.InternalSerializationApi

/**
 * Applies a moving average smoothing algorithm to a list of touch points.
 * This helps reduce noise in handwriting input by averaging the coordinates over a window.
 *
 * @param points The original list of touch points with timestamps.
 * @param windowSize The number of neighboring points to average over. Default is 4.
 * @return A new list of smoothed points.
 */
fun smoothStroke(points: List<PointFWithTime>, windowSize: Int = 4): List<PointFWithTime> {
    // Return original if not enough points to smooth
    if (points.size < windowSize) return points

    val smoothed = mutableListOf<PointFWithTime>()

    for (i in points.indices) {
        // Define the smoothing window
        val start = maxOf(0, i - windowSize / 2)
        val end = minOf(points.size - 1, i + windowSize / 2)

        val window = points.subList(start, end + 1)

        // Calculate averages of x, y, and timestamp in the window
        val avgX = window.map { it.x }.average().toFloat()
        val avgY = window.map { it.y }.average().toFloat()
        val avgTime = window.map { it.timestamp }.average().toLong()

        // Add the averaged point to the result
        smoothed.add(PointFWithTime(avgX, avgY, avgTime))
    }

    return smoothed
}
