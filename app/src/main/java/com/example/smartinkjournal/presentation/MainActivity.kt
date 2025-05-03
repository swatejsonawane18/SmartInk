@file:OptIn(ExperimentalMaterial3Api::class, InternalSerializationApi::class)

package com.example.smartinkjournal.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartinkjournal.R
import com.example.smartinkjournal.data.model.Note
import com.example.smartinkjournal.presentation.canvas.CanvasViewModel
import com.example.smartinkjournal.presentation.canvas.DrawingCanvas
import com.example.smartinkjournal.presentation.notes.NotesScreen
import com.example.smartinkjournal.presentation.notes.NotesViewModel
import com.example.smartinkjournal.utils.PdfExporter.exportNoteAsPdf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val canvasViewModel: CanvasViewModel by viewModels()
    private val notesViewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartInkJournalApp(canvasViewModel, notesViewModel)
        }
    }
}

@Composable
fun SmartInkJournalApp(
    canvasViewModel: CanvasViewModel,
    notesViewModel: NotesViewModel
) {
    var editingNote by remember { mutableStateOf<Note?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val strokes by canvasViewModel.strokes.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Column {
                // Top App Bar with App Title
                TopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.toolbar_title),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                // Toolbar with action icons if editing a note
                if (editingNote != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { canvasViewModel.undo() }) {
                            Icon(Icons.Filled.RotateLeft, contentDescription = stringResource(R.string.undo))
                        }
                        IconButton(onClick = { canvasViewModel.redo() }) {
                            Icon(Icons.Filled.RotateRight, contentDescription = stringResource(R.string.redo))
                        }
                        IconButton(onClick = { canvasViewModel.clearCanvas() }) {
                            Icon(Icons.Filled.CleaningServices, contentDescription = stringResource(R.string.erase_all))
                        }
                        IconButton(onClick = {
                            coroutineScope.launch {
                                canvasViewModel.saveNote(editingNote)
                                delay(100)
                                notesViewModel.loadNotes()
                                canvasViewModel.clearCanvas()
                                editingNote = null
                            }
                        }) {
                            Icon(Icons.Filled.Save, contentDescription = stringResource(R.string.save))
                        }
                        IconButton(onClick = {
                            editingNote?.let { exportNoteAsPdf(context, it.copy(strokes = strokes)) }
                        }) {
                            Icon(Icons.Filled.FileDownload, contentDescription = stringResource(R.string.export_pdf))
                        }
                        IconButton(onClick = {
                            canvasViewModel.clearCanvas()
                            editingNote = null
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.close_without_saving))
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (editingNote == null) {
                FloatingActionButton(
                    onClick = {
                        canvasViewModel.clearCanvas()
                        editingNote = Note(
                            strokes = emptyList(),
                            recognizedText = "",
                            timestamp = System.currentTimeMillis()
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_note))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
        ) {
            // Show drawing canvas when editing
            if (editingNote != null) {
                DrawingCanvas(
                    viewModel = canvasViewModel,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    strokes = strokes,
                    isReadOnly = false
                )

                Spacer(modifier = Modifier.height(8.dp))
            } else {
                // Show notes list screen when not editing
                NotesScreen(
                    viewModel = notesViewModel,
                    onNoteClick = { note ->
                        canvasViewModel.setStrokes(note.strokes)
                        editingNote = note
                    }
                )
            }
        }
    }
}
