# 📝 SmartInk Journal

SmartInk Journal is a handwriting-focused journaling app built using **Kotlin** and **Jetpack Compose**. It enables users to write notes using their finger or stylus, converts handwriting to text using **ML Kit**, and saves all data persistently with search and export features.

---

## ✅ Features

- ✍️ **Handwriting Input**: Draw on a smooth canvas using finger or stylus.
- 🔁 **Undo/Redo**: Supports inking history.
- 🧼 **Stroke Smoothing**: Removes jitter and cleans handwriting lines.
- 🤖 **Handwriting Recognition**: ML Kit (Digital Ink) recognizes ink and shows text.
- 🕵️ **Searchable Notes**: Search through recognized text.
- 🖼️ **Thumbnails**: Preview ink strokes visually in notes list.
- 💾 **Persistent Storage**: Notes stored as JSON with metadata.
- 📄 **Export to PDF**: Generate downloadable PDF with ink and recognized text.

---

## 🧠 App Architecture

SmartInk Journal follows **Clean Architecture + MVVM** with **Hilt DI**:

```
com.example.smartinkjournal
│
├── data/
│   ├── model/        → Note, Stroke, PointFWithTime
│   ├── repository/   → NoteRepository (logic wrapper)
│   └── storage/      → FileStorage (JSON save/load)
│
├── domain/
│   └── usecase/      → SaveNote, LoadNotes, RecognizeText
│
├── presentation/
│   ├── canvas/       → Canvas UI, toggle, handwriting
│   ├── notes/        → Notes screen, filtering
│   └── MainActivity  → App bar, navigation, PDF logic
│
└── utils/            → InkConverter, smoothStroke, PDFExporter, BitmapUtils
```

---

## ✍️ Stroke Data Format

Each note is saved as a JSON file that includes strokes, text, and metadata.

```json
{
  "id": "d4f29a...",
  "timestamp": 1689008123,
  "recognizedText": "Hello world",
  "strokes": [
    {
      "points": [
        { "x": 120.5, "y": 350.3, "timestamp": 1689008000 },
        { "x": 122.1, "y": 352.9, "timestamp": 1689008002 }
      ]
    }
  ]
}
```

This enables:
- ✅ Accurate replay of handwriting
- ✅ AI inference
- ✅ Text search

---

## 🤖 How Handwriting Recognition is Implemented

### 1. Ink Format Conversion

The app uses `ML Kit Digital Ink Recognition` for converting strokes into readable text.

- The raw `List<Stroke>` from the canvas is converted into ML Kit’s `Ink` format via a utility class:

```kotlin
val inkBuilder = Ink.Builder()
for (stroke in strokes) {
    val strokeBuilder = Ink.Stroke.builder()
    stroke.points.forEach {
        strokeBuilder.addPoint(Ink.Point.create(it.x, it.y, it.timestamp))
    }
    inkBuilder.addStroke(strokeBuilder.build())
}
```

---

### 2. Model Download & Management

- On app launch, the `en-US` handwriting model is downloaded using:

```kotlin
val model = DigitalInkRecognitionModel.builder(identifier).build()
RemoteModelManager.getInstance().download(model, conditions).await()
```

- The app checks if the model is ready. Until then, UI shows:
> "Converting to text..."

---

### 3. Recognition Flow

- Once the user toggles the switch or saves a note, the app calls:

```kotlin
val result = recognizer.recognize(ink).await()
val text = result.candidates.firstOrNull()?.text ?: ""
```

- The recognized text is stored in the `Note` model and also shown live when toggled.

---

### 4. Bitmap Use (for Export and Preview)

While recognition is handled via vector `Ink`, Bitmaps are used in:

#### a. PDF Export

- A blank `Bitmap` is created via:

```kotlin
val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
```

- Then a `Canvas` is drawn using strokes to paint the handwriting:

```kotlin
canvas.drawPath(path, paint)
```

- This bitmap is embedded into the PDF output.

#### b. Thumbnail Previews

- In the Notes list, strokes are drawn on a scaled-down `Canvas` using Jetpack Compose for quick visual feedback.

---

## ⚠️ Error Handling

The app gracefully handles:

- **Model not downloaded** → Displays `"Converting to text..."` until ready.
- **Empty strokes** → Skips recognition or export.
- **No match recognized** → Displays `"(No recognized text)"`.
- **IO/PDF Failures** → Catches exceptions and notifies user.

---

## 🖨️ PDF Export

Each exported note PDF includes:

- 📝 Recognized text
- 🗓️ Timestamp
- ✒️ Ink drawing rendered from strokes

### Output Path:
```
/storage/emulated/0/Download/Note_YYYYMMDD_HHMMSS.pdf
```

✅ Compatible with Android 8+

---

## 📱 Demo

Actual Mobile Demo - 

https://github.com/user-attachments/assets/ad4c1cb8-84df-4b5a-bec4-d6d1e0a205bb

Emulator Demo - 

https://github.com/user-attachments/assets/899889a4-fc5b-4aa7-bf7f-067b6926e2ee

---

## 🛠️ Setup

1. **Clone the repo**

```bash
git clone https://github.com/YOUR_USERNAME/smartinkjournal.git
```

2. **Open in Android Studio**  
   Requires Kotlin 1.8+ and Jetpack Compose support.

3. **Run on Emulator or Device**

---

## 🔐 Permissions

Add this to `AndroidManifest.xml` for PDF export:

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

> ⚠️ For Android 11+, handle scoped storage via `MANAGE_EXTERNAL_STORAGE` or MediaStore.

---

## ✅ Deliverables Summary

- [x] Kotlin-based drawing canvas
- [x] Undo/Redo logic
- [x] Handwriting recognition (ML Kit)
- [x] Toggle handwriting ↔ text
- [x] Search recognized notes
- [x] JSON stroke storage
- [x] PDF export
- [x] MVVM + Clean Architecture
- [x] Hilt dependency injection

---

## 📄 License

MIT License © Swatej
