//package com.example.smartinkjournal.data.model
//
//import kotlinx.serialization.Serializable
//import java.util.UUID
//
//@Serializable
//data class Note(
//    val id: String = UUID.randomUUID().toString(),
//    val strokes: List<Stroke>,
//    val recognizedText: String,
//    val timestamp: Long
//)

package com.example.smartinkjournal.data.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import java.util.UUID

@InternalSerializationApi /**
 * A Note consists of an ID, list of strokes, recognized text, and a timestamp.
 */
@Serializable
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val strokes: List<Stroke>,
    val recognizedText: String,
    val timestamp: Long
)