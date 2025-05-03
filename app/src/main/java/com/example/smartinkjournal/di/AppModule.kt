package com.example.smartinkjournal.di

import android.content.Context
import com.example.smartinkjournal.data.repository.NoteRepository
import com.example.smartinkjournal.data.storage.FileStorage
import com.example.smartinkjournal.domain.usecase.LoadNotesUseCase
import com.example.smartinkjournal.domain.usecase.RecognizeTextUseCase
import com.example.smartinkjournal.domain.usecase.SaveNoteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AppModule defines how application-level dependencies are provided
 * using Dagger Hilt's dependency injection system.
 *
 * All provided instances are scoped as singletons and shared across the app.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides a singleton instance of FileStorage to handle local file I/O.
     */
    @Provides
    @Singleton
    fun provideFileStorage(@ApplicationContext context: Context): FileStorage {
        return FileStorage(context)
    }

    /**
     * Provides a singleton instance of NoteRepository,
     * which acts as an abstraction over FileStorage.
     */
    @Provides
    @Singleton
    fun provideNoteRepository(storage: FileStorage): NoteRepository {
        return NoteRepository(storage)
    }

    /**
     * Provides a singleton instance of SaveNoteUseCase,
     * which allows saving notes through the repository.
     */
    @Provides
    @Singleton
    fun provideSaveNoteUseCase(repo: NoteRepository): SaveNoteUseCase {
        return SaveNoteUseCase(repo)
    }

    /**
     * Provides a singleton instance of LoadNotesUseCase,
     * which retrieves saved notes from the repository.
     */
    @Provides
    @Singleton
    fun provideLoadNotesUseCase(repo: NoteRepository): LoadNotesUseCase {
        return LoadNotesUseCase(repo)
    }

    /**
     * Provides a singleton instance of RecognizeTextUseCase,
     * which handles handwriting recognition using ML Kit.
     */
    @Provides
    @Singleton
    fun provideRecognizeTextUseCase(@ApplicationContext context: Context): RecognizeTextUseCase {
        return RecognizeTextUseCase(context)
    }
}
