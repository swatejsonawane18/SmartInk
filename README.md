# SmartInk Journal

SmartInk Journal is a handwriting-focused Android journaling app built using Kotlin and Jetpack Compose. It allows users to create handwritten notes using their finger or stylus, recognizes handwriting using AI, and makes notes searchable by recognized text.

---

## ✅ Features

- ✍️ **Handwriting Input**: Draw with finger or stylus on a custom canvas.
- 🔁 **Undo/Redo**: Modify strokes easily.
- 🧹 **Stroke Smoothing**: Cleans up jittery handwriting.
- 🤖 **Handwriting Recognition**: Uses ML Kit Digital Ink to convert handwriting to text.
- 💾 **Persistent Storage**: Notes saved as JSON with raw strokes, recognized text, and timestamp.
- 🕑 **Note History**: View past notes with thumbnails and search recognized text.
- 📄 **Export to PDF**: Save notes (ink + text) as downloadable PDFs.

---

## 🧠 App Architecture

This app follows a Clean Architecture + MVVM pattern:

```
com.example.smartinkjournal
│
├── data/
│   ├── model/        → Data models (Note, Stroke, PointFWithTime)
│   ├── repository/   → NoteRepository (abstracts persistence)
│   └── storage/      → FileStorage (JSON-based storage)
│
├── domain/
│   └── usecase/      → Business logic (SaveNote, LoadNotes, RecognizeText)
│
├── presentation/
│   ├── canvas/       → Drawing canvas UI + ViewModel
│   ├── notes/        → Notes list UI + ViewModel
│   └── MainActivity  → Composable navigation + layout
│
└── utils/            → Helpers: smoothing, bitmap creation, PDF export
```

---

## 🧾 Stroke Data Format

Notes are saved as JSON files storing structured stroke data like this:

```json
{
  "id": "d4f29...",
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

This allows full note reconstruction, search indexing, and ML inference.

---

## ✨ Handwriting Recognition (ML Kit)

SmartInk Journal uses **ML Kit's Digital Ink Recognition**:

1. **Ink Conversion**  
   `List<Stroke>` is converted to `Ink` using `InkConverter`.

2. **Model Setup**  
   English model (`en-US`) is downloaded at launch using `RemoteModelManager`.

3. **Text Recognition**  
   Using `DigitalInkRecognizer.recognize(ink)` the top result is extracted.

4. **Error Handling**  
   Recognition is skipped if the model isn't ready or fails.

---

## 🖨️ PDF Export

When exporting a note:
- The recognized text and timestamp are printed on the top.
- The strokes are scaled and drawn in vector form.
- The PDF is saved to:

```
storage/emulated/0/Download/Note_YYYYMMDD_HHMMSS.pdf
```

You will need file access permission to access the file from outside the app.

---

## 🛠️ Setup Instructions

1. **Clone the repo:**

```bash
git clone https://github.com/yourusername/smartinkjournal.git
```

2. **Open in Android Studio**  
   Make sure you have Kotlin + Compose support enabled.

3. **Run the app**  
   Test on a real device or emulator (Android 8.0+ recommended).

---

## 🛡️ Permissions

Required for PDF export:

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

(For Android 10+, also handle scoped storage or request `MANAGE_EXTERNAL_STORAGE`)

---

## 📁 Deliverables Checklist

- ✅ Source code in Kotlin
- ✅ Custom Canvas for inking
- ✅ AI-powered recognition via ML Kit
- ✅ Persistent JSON-based stroke storage
- ✅ Undo/Redo
- ✅ Searchable note history
- ✅ Thumbnail preview
- ✅ Export to PDF
- ✅ Clean MVVM architecture using Hilt DI

---
