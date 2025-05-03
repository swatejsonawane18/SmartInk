@file:OptIn(ExperimentalMaterial3Api::class, InternalSerializationApi::class)

package com.example.smartinkjournal.presentation.notes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStroke  // for drawing paths
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.smartinkjournal.data.model.Note
import com.example.smartinkjournal.data.model.Stroke
import kotlinx.serialization.InternalSerializationApi
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    onNoteClick: (Note) -> Unit = {}
) {
    val notes by viewModel.notes.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        OutlinedTextField(
            value = viewModel.query,
            onValueChange = { viewModel.updateQuery(it) },
            label = { Text("Search recognized text...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(notes) { note ->
                NoteCard(note = note, onClick = { onNoteClick(note) })
            }
        }
    }
}

@Composable
fun NoteCard(note: Note, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.recognizedText.ifBlank { "(No recognized text)" },
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(note.timestamp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            NoteThumbnail(
                strokes = note.strokes,
                size = 80.dp
            )
        }
    }
}

@Composable
fun NoteThumbnail(
    strokes: List<Stroke>,
    size: Dp,
    strokeColor: Color = Color.Black.copy(alpha = 0.9f),
    strokeWidth: Float = 1f
) {
    Canvas(modifier = Modifier.size(size)) {
        val canvasPx = size.toPx()
        val allPoints = strokes.flatMap { it.points }
        if (allPoints.isEmpty()) return@Canvas

        val minX = allPoints.minOf { it.x }
        val maxX = allPoints.maxOf { it.x }
        val minY = allPoints.minOf { it.y }
        val maxY = allPoints.maxOf { it.y }

        val width = (maxX - minX).coerceAtLeast(1f)
        val height = (maxY - minY).coerceAtLeast(1f)
        val padding = 0.05f * canvasPx
        val scale = minOf((canvasPx - 2 * padding) / width, (canvasPx - 2 * padding) / height)

        val offsetX = (canvasPx - (width * scale)) / 2 - (minX * scale)
        val offsetY = (canvasPx - (height * scale)) / 2 - (minY * scale)

        strokes.forEach { stroke ->
            val path = Path().apply {
                stroke.points.firstOrNull()?.let {
                    moveTo(it.x * scale + offsetX, it.y * scale + offsetY)
                }
                stroke.points.drop(1).forEach {
                    lineTo(it.x * scale + offsetX, it.y * scale + offsetY)
                }
            }
            drawPath(path, strokeColor, style = DrawStroke(width = strokeWidth))
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
