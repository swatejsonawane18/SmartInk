package com.example.smartinkjournal.data.repository

import com.example.smartinkjournal.data.model.Note
import com.example.smartinkjournal.data.storage.FileStorage
import kotlinx.serialization.InternalSerializationApi

/**
 * Repository class that acts as a bridge between the data layer (storage)
 * and the domain/use-case layer.
 *
 * It encapsulates access to file-based note storage and provides a clean
 * interface for use cases to work with notes.
 */
@OptIn(InternalSerializationApi::class)
class NoteRepository(private val storage: FileStorage) {

    /**
     * Saves a given note using the file storage implementation.
     *
     * @param note The note object to save.
     */
    fun saveNote(note: Note) = storage.saveNote(note)

    /**
     * Loads all saved notes from storage.
     *
     * @return A list of notes, or an empty list if none found.
     */
    fun loadNotes(): List<Note> = storage.loadNotes()
}
