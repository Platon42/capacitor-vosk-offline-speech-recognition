import type { PermissionState, PluginListenerHandle } from '@capacitor/core';

export interface PermissionStatus {
  /**
   * Permission state for speechRecognition alias.
   *
   * Android: RECORD_AUDIO
   * iOS: speech recognition + microphone
   */
  speechRecognition: PermissionState;
}

export interface AvailableResult {
  available: boolean;
}

export interface IsListeningResult {
  isListening: boolean;
}

/** Event payloads (совпадают с notifyListeners() в Android) */
export interface VoskPartialResultEvent {
  value: string;
}

export interface VoskFinalResultEvent {
  value: string;
}

export interface VoskErrorEvent {
  error: string;
}

export interface VoskPlugin {
  // Control
  startListening(): Promise<void>;
  stopListening(): Promise<void>;
  pauseListening(): Promise<void>;
  resumeListening(): Promise<void>;

  // State
  isListening(): Promise<IsListeningResult>;
  available(): Promise<AvailableResult>;

  // Permissions
  requestPermissions(): Promise<PermissionStatus>;

  // Listeners
  addListener(
      eventName: 'partialResult',
      listener: (data: VoskPartialResultEvent) => void
  ): Promise<PluginListenerHandle>;

  addListener(
      eventName: 'onResult',
      listener: (data: VoskFinalResultEvent) => void
  ): Promise<PluginListenerHandle>;

  addListener(
      eventName: 'error',
      listener: (data: VoskErrorEvent) => void
  ): Promise<PluginListenerHandle>;

  // Optional, но очень полезно (Capacitor обычно это предоставляет)
  removeAllListeners?(): Promise<void>;
}
