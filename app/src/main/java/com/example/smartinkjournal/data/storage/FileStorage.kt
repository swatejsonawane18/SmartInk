package com.example.smartinkjournal.data.storage

import android.content.Context
import android.util.Log
import com.example.smartinkjournal.data.model.Note
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * FileStorage is a low-level data access class responsible for
 * persisting and retrieving notes to/from the internal file system.
 *
 * Notes are stored as individual JSON files under the "notes" directory
 * in the app's private internal storage.
 */
@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
class FileStorage(private val context: Context) {

    // Directory path where all notes will be stored
    private val notesDir = File(context.filesDir, "notes")

    // Ensure the directory exists when the storage is initialized
    init {
        if (!notesDir.exists()) notesDir.mkdirs()
    }

    /**
     * Saves a single note to a file using JSON serialization.
     *
     * @param note The note object to serialize and write to disk.
     */
    fun saveNote(note: Note) {
        val json = Json.encodeToString(note)
        val file = File(notesDir, "${note.id}.json")
        file.writeText(json)
        Log.d("FileStorage", "Saved note to: ${file.absolutePath}")
    }

    /**
     * Loads and deserializes all notes from the notes directory.
     *
     * @return A list of successfully parsed Note objects. Returns an empty list if none exist.
     */
    fun loadNotes(): List<Note> =
        notesDir.listFiles()?.mapNotNull {
            // Gracefully ignore files that fail to parse
            runCatching { Json.decodeFromString<Note>(it.readText()) }.getOrNull()
        } ?: emptyList()
}
