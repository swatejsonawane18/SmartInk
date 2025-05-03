package com.example.smartinkjournal.domain.usecase

import com.example.smartinkjournal.data.model.Note
import com.example.smartinkjournal.data.repository.NoteRepository
import kotlinx.serialization.InternalSerializationApi

/**
 * Use case responsible for saving a Note.
 *
 * Abstracts the repository layer from the ViewModel or UI layer,
 * and provides a clean way to trigger save operations as a use case.
 *
 * This use case is `suspend` to support being called from a coroutine,
 * which allows for non-blocking file I/O when saving notes.
 */
@OptIn(InternalSerializationApi::class)
class SaveNoteUseCase(private val repository: NoteRepository) {

    /**
     * Saves the provided [note] using the repository.
     * This function is a suspend function and must be called from a coroutine.
     *
     * @param note The Note object to be persisted.
     */
    suspend operator fun invoke(note: Note) = repository.saveNote(note)
}
