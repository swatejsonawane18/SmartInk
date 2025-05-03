@file:OptIn(InternalSerializationApi::class)

package com.example.smartinkjournal.presentation.canvas

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartinkjournal.data.model.PointFWithTime
import kotlinx.serialization.InternalSerializationApi
import com.example.smartinkjournal.data.model.Stroke as InkStroke

/**
 * Canvas screen for drawing or toggling to view recognized text.
 */
@Composable
fun DrawingCanvas(
    viewModel: CanvasViewModel,
    modifier: Modifier = Modifier,
    strokes: List<InkStroke>,
    isReadOnly: Boolean = false
) {
    val currentStroke = remember { mutableStateListOf<PointFWithTime>() }
    var showRecognized by remember { mutableStateOf(false) }
    val recognizedText by viewModel.recognizedText.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {

        Box(modifier = Modifier.weight(1f)) {
            if (showRecognized) {
                // Trigger recognition only when switching to recognized view
                LaunchedEffect(showRecognized) {
                    viewModel.recognizeCurrentText()
                }

                Text(
                    text = recognizedText ?: "(No recognized text)",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .then(
                            if (!isReadOnly) Modifier.pointerInput(Unit) {
                                while (true) {
                                    awaitPointerEventScope {
                                        val event = awaitPointerEvent(PointerEventPass.Initial)
                                        val change = event.changes.firstOrNull() ?: return@awaitPointerEventScope
                                        val pos = change.position

                                        if (change.pressed) {
                                            currentStroke.add(PointFWithTime(pos.x, pos.y, System.currentTimeMillis()))
                                        } else if (currentStroke.isNotEmpty()) {
                                            viewModel.addStroke(currentStroke.toList())
                                            currentStroke.clear()
                                        }
                                        event.changes.forEach { it.consume() }
                                    }
                                }
                            } else Modifier
                        )
                ) {
                    for (stroke in strokes) {
                        val path = Path().apply {
                            stroke.points.firstOrNull()?.let { moveTo(it.x, it.y) }
                            stroke.points.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                        drawPath(path, Color.Black, style = Stroke(width = 4.dp.toPx()))
                    }

                    if (!isReadOnly) {
                        val previewPath = Path().apply {
                            currentStroke.firstOrNull()?.let { moveTo(it.x, it.y) }
                            currentStroke.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                        drawPath(previewPath, Color.Red, style = Stroke(width = 4.dp.toPx()))
                    }
                }
            }
        }

        // Toggle switch between handwriting and recognized text
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Handwriting", modifier = Modifier.padding(end = 8.dp))
            Switch(
                checked = showRecognized,
                onCheckedChange = {
                    showRecognized = it
                    if (it) viewModel.recognizeCurrentText()
                }
            )
            Text("Recognized Text", modifier = Modifier.padding(start = 8.dp))
        }
    }
}
