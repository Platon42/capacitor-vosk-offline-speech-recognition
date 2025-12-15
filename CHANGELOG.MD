
---

# Changelog

All notable changes to this project will be documented in this file.

The format is based on **[Keep a Changelog](https://keepachangelog.com/en/1.1.0/)**
and this project follows **[Semantic Versioning](https://semver.org/)**.

---

## [Unreleased]

### Planned

* iOS implementation (AVAudioEngine + Vosk)
* Configurable model path
* Optional automatic stop on silence
* Streaming confidence scores

---

## [1.1.0] – 2025-12-15

### 🚀 Added

* **Capacitor 8 support**
* Automatic build on GitHub install via `prepare` script
* Web stub implementation to prevent Vite / web build crashes
* Explicit `exports` field in `package.json` for modern bundlers
* Error event (`error`) with structured payload
* Model readiness check via `available()`

### 🔄 Changed

* Plugin name unified to
  **`VoskOfflineSpeechRecognition`** across:

    * Android `@CapacitorPlugin`
    * JS `registerPlugin`
    * TypeScript definitions
* Event payloads standardized:

    * `partialResult` → `{ value: string }`
    * `onResult` → `{ value: string }`
    * `error` → `{ error: string }`
* Android plugin rewritten for **Capacitor 8 annotations & permissions**
* Android Gradle setup updated:

    * AGP 8.x
    * Java 17
    * SDK 34
* Model loading moved to plugin startup (`load()`)

### 🛠 Fixed

* `"plugin is not implemented on android"` error caused by:

    * Capacitor 6 incompatibility
    * mismatched plugin names
* Vite build errors related to missing `main/module/exports`
* TypeScript errors in web builds (`addListener`, `unavailable`)
* Potential race condition when starting recognition before model load

### ❌ Removed

* Deprecated `finalResult` event
* Deprecated payload fields `matches` / `result`
* Legacy Capacitor 6 dependencies
* Direct dependency on `project(':capacitor-android')`

### ⚠️ Breaking Changes

* Requires **Capacitor >= 8**
* Event payload shape has changed (`value` instead of `matches/result`)
* Plugin must be referenced as `VoskOfflineSpeechRecognition`

---

## [1.0.4] – 2023-xx-xx

### Added

* Initial Android Vosk integration
* Partial and final recognition events
* Basic permission handling

### Known Issues

* Capacitor 6 only
* No web stub (breaks Vite builds)
* Inconsistent event payloads
* `"plugin is not implemented"` on newer Capacitor versions

---

## Upgrade Guide (1.0.x → 1.1.0)

1. Upgrade Capacitor to **v8**
2. Replace event listeners:

   ```diff
   - event.matches
   - event.result
   + event.value
   ```
3. Update plugin import:

   ```ts
   import { VoskOfflineSpeechRecognition } from 'vosk-offline-speech-recognition';
   ```
4. Ensure model is placed in:

   ```
   android/app/src/main/assets/vosk-model-small-pt/
   ```

---