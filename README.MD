---

# capacitor-vosk-offline-speech-recognition

Offline speech recognition plugin for **Capacitor 8** using **Vosk**.

This plugin enables **fully offline speech-to-text** on Android using the Vosk engine.
No internet connection, no external APIs, no data leaving the device.

> ⚠️ Currently **Android only**. Web is stubbed, iOS is not implemented yet.

---

## Features

* ✅ Offline speech recognition (Vosk)
* ✅ Android (Capacitor 8)
* ✅ Partial and final results
* ✅ Microphone permission handled via Capacitor
* ❌ No web support (stub only)
* ❌ No iOS support (yet)

---

## Installation

### Install from GitHub (recommended)

```bash
npm install github:Platon42/capacitor-vosk-offline-speech-recognition
npx cap sync android
```

> ℹ️ The package uses a `prepare` script, so it is built automatically when installed from GitHub.

---

## Android setup

### 1. Add microphone permission

In **app** `android/app/src/main/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

(The plugin also declares this permission internally.)

---

### 2. Add a Vosk model

Download a Vosk model, for example:

* [https://alphacephei.com/vosk/models](https://alphacephei.com/vosk/models)

Place it here:

```
android/app/src/main/assets/vosk-model-small-pt/
```

📌 The folder name **must match exactly**, because the plugin loads it by name.

---

## Usage

### Import the plugin

```ts
import { VoskOfflineSpeechRecognition } from 'vosk-offline-speech-recognition';
```

---

### Check availability

```ts
const { available } = await VoskOfflineSpeechRecognition.available();
console.log('Vosk available:', available);
```

> `available === true` means the model has been successfully loaded.

---

### Request microphone permission

```ts
const perm = await VoskOfflineSpeechRecognition.requestPermissions();

if (perm.speechRecognition !== 'granted') {
  console.error('Microphone permission not granted');
}
```

---

### Start listening

```ts
await VoskOfflineSpeechRecognition.startListening();
```

---

### Stop listening

```ts
await VoskOfflineSpeechRecognition.stopListening();
```

---

## Events

### Partial recognition result

```ts
const partial = await VoskOfflineSpeechRecognition.addListener(
  'partialResult',
  (event) => {
    console.log('Partial:', event.value);
  }
);
```

Payload:

```ts
{
  value: string;
}
```

---

### Final recognition result

```ts
const final = await VoskOfflineSpeechRecognition.addListener(
  'onResult',
  (event) => {
    console.log('Final:', event.value);
  }
);
```

Payload:

```ts
{
  value: string;
}
```

---

### Error event

```ts
const error = await VoskOfflineSpeechRecognition.addListener(
  'error',
  (event) => {
    console.error('Vosk error:', event.error);
  }
);
```

Payload:

```ts
{
  error: string;
}
```

---

### Remove listeners

```ts
await partial.remove();
await final.remove();
await error.remove();

// or
await VoskOfflineSpeechRecognition.removeAllListeners();
```

---

## API Reference (TypeScript)

```ts
interface VoskOfflineSpeechRecognitionPlugin {
  // Control
  startListening(): Promise<void>;
  stopListening(): Promise<void>;
  pauseListening(): Promise<void>;
  resumeListening(): Promise<void>;

  // State
  isListening(): Promise<{ isListening: boolean }>;
  available(): Promise<{ available: boolean }>;

  // Permissions
  requestPermissions(): Promise<{ speechRecognition: PermissionState }>;

  // Events
  addListener(
    eventName: 'partialResult',
    listener: (data: { value: string }) => void
  ): Promise<PluginListenerHandle>;

  addListener(
    eventName: 'onResult',
    listener: (data: { value: string }) => void
  ): Promise<PluginListenerHandle>;

  addListener(
    eventName: 'error',
    listener: (data: { error: string }) => void
  ): Promise<PluginListenerHandle>;

  removeAllListeners(): Promise<void>;
}
```

---

## Notes

* Recognition runs **entirely offline**
* The model is loaded **asynchronously** on plugin startup
* `available()` may return `false` briefly after app launch
* Always request microphone permission **before** calling `startListening()`

---

## License

Apache-2.0

---

## Credits

* Vosk Speech Recognition — [https://alphacephei.com/vosk/](https://alphacephei.com/vosk/)
* Original plugin by **rubendsm**
* Capacitor 8 fork and updates by **Platon42**

---
