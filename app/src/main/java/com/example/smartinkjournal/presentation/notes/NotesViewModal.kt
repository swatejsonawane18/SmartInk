@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.smartinkjournal.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.smartinkjournal.data.model.Note
import com.example.smartinkjournal.domain.usecase.LoadNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the list of notes and query state.
 */
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val loadNotesUseCase: LoadNotesUseCase
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    var query: String by mutableStateOf("")
        private set

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            val allNotes = loadNotesUseCase()
            _notes.value = allNotes
        }
    }

    fun updateQuery(newQuery: String) {
        query = newQuery
        viewModelScope.launch {
            val filtered = loadNotesUseCase().filter {
                it.recognizedText.contains(query, ignoreCase = true)
            }
            _notes.value = filtered
        }
    }
}
