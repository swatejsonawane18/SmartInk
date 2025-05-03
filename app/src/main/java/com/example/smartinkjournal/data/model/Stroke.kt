//package com.example.smartinkjournal.data.model
//
//import kotlinx.serialization.Serializable
//
//@Serializable
//data class Stroke(
//    val points: List<PointFWithTime>
//)

package com.example.smartinkjournal.data.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi /**
 * Represents a stroke made up of a sequence of points.
 */
@Serializable
data class Stroke(
    val points: List<PointFWithTime>
)