//package com.example.smartinkjournal.data.model
//
//import kotlinx.serialization.Serializable
//
//@Serializable
//data class PointFWithTime(
//    val x: Float,
//    val y: Float,
//    val timestamp: Long
//)

package com.example.smartinkjournal.data.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi /**
 * Represents a single touch point in a stroke, including the x, y coordinates and timestamp.
 */
@Serializable
data class PointFWithTime(
    val x: Float,
    val y: Float,
    val timestamp: Long
)
