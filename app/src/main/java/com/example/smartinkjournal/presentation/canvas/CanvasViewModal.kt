@file:OptIn(InternalSerializationApi::class)

package com.example.smartinkjournal.presentation.canvas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartinkjournal.data.model.Note
import com.example.smartinkjournal.data.model.PointFWithTime
import com.example.smartinkjournal.data.model.Stroke
import com.example.smartinkjournal.domain.usecase.RecognizeTextUseCase
import com.example.smartinkjournal.domain.usecase.SaveNoteUseCase
import com.example.smartinkjournal.utils.InkConverter
import com.example.smartinkjournal.utils.smoothStroke
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

/**
 * ViewModel for managing the state of the drawing canvas.
 * Handles strokes, undo/redo logic, and note saving with text recognition.
 */
@HiltViewModel
class CanvasViewModel @Inject constructor(
    private val recognizeTextUseCase: RecognizeTextUseCase,
    private val saveNoteUseCase: SaveNoteUseCase
) : ViewModel() {

    private val _strokes = MutableStateFlow<List<Stroke>>(emptyList())
    val strokes: StateFlow<List<Stroke>> = _strokes

    private val _recognizedText = MutableStateFlow<String?>(null)
    val recognizedText: StateFlow<String?> = _recognizedText

    private val undoStack = mutableListOf<Stroke>()
    private val redoStack = mutableListOf<Stroke>()

    fun addStroke(stroke: List<PointFWithTime>) {
        val smoothed = smoothStroke(stroke)
        val newStroke = Stroke(smoothed)
        undoStack.add(newStroke)
        redoStack.clear()
        _strokes.value = _strokes.value + newStroke
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val last = undoStack.removeLast()
            redoStack.add(last)
            _strokes.value = _strokes.value.dropLast(1)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val restored = redoStack.removeLast()
            undoStack.add(restored)
            _strokes.value = _strokes.value + restored
        }
    }

    fun setStrokes(strokes: List<Stroke>) {
        _strokes.value = strokes
        undoStack.clear()
        redoStack.clear()
        undoStack.addAll(strokes)
    }

    fun clearCanvas() {
        _strokes.value = emptyList()
        undoStack.clear()
        redoStack.clear()
        _recognizedText.value = null
    }

    fun clearRecognizedText() {
        _recognizedText.value = null
    }

    suspend fun saveNote(existingNote: Note? = null) {
        val ink = InkConverter.convert(_strokes.value)
        val text = recognizeTextUseCase.recognizeInk(ink)
        val note = Note(
            id = (existingNote?.id ?: 0).toString(),
            strokes = _strokes.value,
            recognizedText = text,
            timestamp = System.currentTimeMillis()
        )
        saveNoteUseCase(note)
    }

    fun recognizeCurrentText() {
        viewModelScope.launch {
            _recognizedText.value = "Converting to text..."
            val ink = InkConverter.convert(_strokes.value)
            val result = recognizeTextUseCase.recognizeInk(ink)
            _recognizedText.value = if (result.isBlank()) "(No recognized text)" else result
        }
    }
}
