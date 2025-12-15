import { registerPlugin } from '@capacitor/core';
import type { VoskPlugin } from './definitions';

const VoskOfflineSpeechRecognition = registerPlugin<VoskPlugin>(
    'VoskOfflineSpeechRecognition',
    {
      web: () => import('./web').then((m) => new m.VoskOfflineSpeechRecognitionWeb()),
    }
);

export * from './definitions';
export { VoskOfflineSpeechRecognition };
