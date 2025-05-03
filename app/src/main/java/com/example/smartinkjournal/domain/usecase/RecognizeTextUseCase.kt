package com.example.smartinkjournal.domain.usecase

import android.content.Context
import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Use case for recognizing handwriting input using ML Kit's Digital Ink Recognition.
 * Ensures the recognition model is downloaded before processing.
 */
class RecognizeTextUseCase(context: Context) {

    private val modelIdentifier = DigitalInkRecognitionModelIdentifier.EN_US
    private val model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
    private val options = DigitalInkRecognizerOptions.builder(model).build()
    private val recognizer = DigitalInkRecognition.getClient(options)

    private val remoteModelManager = RemoteModelManager.getInstance()

    /**
     * Downloads the recognition model if not already available.
     */
    private suspend fun ensureModelReady(): Boolean = withContext(Dispatchers.IO) {
        val isDownloaded = remoteModelManager.isModelDownloaded(model).await()
        if (isDownloaded) {
            Log.d("MLKit", "Model already downloaded.")
            return@withContext true
        }

        return@withContext try {
            val conditions = DownloadConditions.Builder().build()
            remoteModelManager.download(model, conditions).await()
            Log.d("MLKit", "Model downloaded successfully.")
            true
        } catch (e: Exception) {
            Log.e("MLKit", "Model download failed", e)
            false
        }
    }

    /**
     * Recognizes handwriting from the provided Ink object.
     *
     * @param ink A set of strokes representing the user’s handwriting.
     * @return The best recognition candidate or empty string if model not ready or fails.
     */
    suspend fun recognizeInk(ink: Ink): String {
        if (!ensureModelReady()) {
            Log.w("MLKit", "Model not ready. Skipping recognition.")
            return ""
        }

        return try {
            val result = recognizer.recognize(ink).await()
            result.candidates.firstOrNull()?.text ?: ""
        } catch (e: Exception) {
            Log.e("MLKit", "Recognition failed", e)
            ""
        }
    }
}
