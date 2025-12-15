import { WebPlugin } from '@capacitor/core';
import type { PluginListenerHandle } from '@capacitor/core';

import type {
  PermissionStatus,
  VoskPlugin,
  AvailableResult,
  IsListeningResult,
  VoskPartialResultEvent,
  VoskFinalResultEvent,
  VoskErrorEvent,
} from './definitions';

export class VoskOfflineSpeechRecognitionWeb extends WebPlugin implements VoskPlugin {
  async requestPermissions(): Promise<PermissionStatus> {
    return { speechRecognition: 'denied' as any };
  }

  async available(): Promise<AvailableResult> {
    return { available: false };
  }

  async pauseListening(): Promise<void> {}

  async resumeListening(): Promise<void> {}

  async startListening(): Promise<void> {
    throw new Error(
        'VoskOfflineSpeechRecognition is only available on native platforms (Android/iOS).'
    );
  }

  async stopListening(): Promise<void> {}

  async isListening(): Promise<IsListeningResult> {
    return { isListening: false };
  }

  // ✅ Нужно для соответствия интерфейсу
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
  addListener(eventName: any, listener: any): Promise<PluginListenerHandle> {
    // WebPlugin уже умеет listeners — просто прокидываем
    return super.addListener(eventName, listener);
  }
}
