package com.example.smartinkjournal.domain.usecase

import com.example.smartinkjournal.data.model.Note
import com.example.smartinkjournal.data.repository.NoteRepository
import kotlinx.serialization.InternalSerializationApi

/**
 * Use case for loading all saved notes.
 *
 * This separates the business logic (use case) from the UI and data layers.
 */
@OptIn(InternalSerializationApi::class)
class LoadNotesUseCase(
    private val repository: NoteRepository
) {
    /**
     * Loads and returns all notes from the repository.
     *
     * This operator function allows the use case to be called like a function: `loadNotesUseCase()`.
     */
    operator fun invoke(): List<Note> = repository.loadNotes()
}
