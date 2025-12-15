package com.mitmynid.plugins.vosk;

import android.content.Context;
import android.util.Log;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.StorageService;

import java.io.IOException;

public class Vosk {

    private Model model;
    private Recognizer recognizer;
    private SpeechService speechService;

    private boolean isListening = false;
    private volatile boolean modelReady = false;

    public void initModel(Context context) {
        try {
            StorageService.unpack(
                context,
                "vosk-model-small-pt",
                "model",
                (unpackedModel) -> {
                    this.model = unpackedModel;
                    this.modelReady = true;
                    Log.d("Vosk", "Model loaded successfully");
                },
                (exception) -> {
                    this.modelReady = false;
                    Log.e("Vosk", "Error unpacking model: " + exception.getMessage());
                }
            );
        } catch (Exception e) {
            this.modelReady = false;
            Log.e("Vosk", "Error initiating model: " + e.getMessage());
        }
    }

    public boolean isModelReady() {
        return modelReady && model != null;
    }

    public void startListening(RecognitionListener listener) throws IOException {
        if (!isModelReady()) {
            throw new RuntimeException("Model is not ready");
        }

        // если уже слушаем — остановить предыдущую сессию
        if (speechService != null) {
            stopListening();
        }

        recognizer = new Recognizer(model, 16000.0f);
        speechService = new SpeechService(recognizer, 16000.0f);
        speechService.startListening(listener);
        isListening = true;
    }

    public void stopListening() {
        try {
            if (speechService != null) {
                speechService.stop();
                // если в твоей версии есть shutdown() — лучше:
                // speechService.shutdown();
            }
        } finally {
            speechService = null;
            recognizer = null;
            isListening = false;
        }
    }

    public void pause(boolean checked) {
        if (speechService != null) {
            speechService.setPause(checked);
        }
        isListening = !checked;
    }

    public boolean isListening() {
        return isListening;
    }
}
