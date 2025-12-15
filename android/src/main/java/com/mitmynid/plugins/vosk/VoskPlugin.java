package com.mitmynid.plugins.vosk;

import android.Manifest;
import android.content.Context;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import org.vosk.android.RecognitionListener;

@CapacitorPlugin(
        name = "VoskOfflineSpeechRecognition",
        permissions = {
                @Permission(strings = { Manifest.permission.RECORD_AUDIO }, alias = "speechRecognition")
        }
)
public class VoskPlugin extends Plugin {

    private final Vosk voskImplementation = new Vosk();

    @Override
    public void load() {
        super.load();
        try {
            voskImplementation.initModel(getContext());
        } catch (RuntimeException e) {
            Log.e("Vosk", "Failed to init model on load: " + e.getMessage());
        }
    }

    /**
     * Метод, который ожидает JS: requestPermissions()
     */
    @PluginMethod
    public void requestPermissions(PluginCall call) {
        requestPermissionForAlias("speechRecognition", call, "permissionsCallback");
    }

    @PermissionCallback
    private void permissionsCallback(PluginCall call) {
        JSObject result = new JSObject();
        PermissionState state = getPermissionState("speechRecognition");
        result.put("speechRecognition", state == null ? "prompt" : state.toString().toLowerCase());
        call.resolve(result);
    }

    @PluginMethod
    public void available(PluginCall call) {
        JSObject result = new JSObject();

        // ВАЖНО: SpeechRecognizer тут не нужен для Vosk, но оставим "как мягкую проверку".
        // Главный критерий — готовность модели.
        boolean modelReady = voskImplementation.isModelReady();

        result.put("available", modelReady);
        call.resolve(result);
    }

    @PluginMethod
    public void startListening(PluginCall call) {
        try {
            // Permission check
            if (getPermissionState("speechRecognition") != PermissionState.GRANTED) {
                call.reject("Microphone permission not granted");
                return;
            }

            // Model ready check
            if (!voskImplementation.isModelReady()) {
                call.reject("Model is not ready yet");
                return;
            }

            RecognitionListener recognitionListener = new RecognitionListener() {

                @Override
                public void onPartialResult(String hypothesis) {
                    if (hypothesis == null || hypothesis.isEmpty()) return;

                    try {
                        JSObject hypothesisObj = new JSObject(hypothesis);
                        String partial = hypothesisObj.optString("partial", "");

                        JSObject payload = new JSObject();
                        payload.put("value", partial);

                        notifyListeners("partialResult", payload);
                    } catch (Exception e) {
                        JSObject payload = new JSObject();
                        payload.put("error", "Failed to parse partial result: " + e.getMessage());
                        notifyListeners("error", payload);
                    }
                }

                @Override
                public void onResult(String hypothesis) {
                    // промежуточный “final-like” результат
                    emitFinal(hypothesis);
                }

                @Override
                public void onFinalResult(String hypothesis) {
                    emitFinal(hypothesis);
                }

                private void emitFinal(String hypothesis) {
                    if (hypothesis == null || hypothesis.isEmpty()) return;

                    try {
                        JSObject hypothesisObj = new JSObject(hypothesis);
                        String text = hypothesisObj.optString("text", "");
                        if (text == null || text.trim().isEmpty()) return;

                        JSObject payload = new JSObject();
                        payload.put("value", text);

                        // ✅ JS слушает "onResult"
                        notifyListeners("onResult", payload);
                    } catch (Exception e) {
                        JSObject payload = new JSObject();
                        payload.put("error", "Failed to parse final result: " + e.getMessage());
                        notifyListeners("error", payload);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    String msg = exception != null ? exception.getMessage() : "Unknown error";
                    Log.e("Vosk", "Speech recognition error: " + msg);

                    JSObject payload = new JSObject();
                    payload.put("error", msg);
                    notifyListeners("error", payload);
                }

                @Override
                public void onTimeout() {
                    Log.e("Vosk", "Speech recognition timed out.");

                    JSObject payload = new JSObject();
                    payload.put("error", "Speech recognition timed out");
                    notifyListeners("error", payload);
                }
            };

            voskImplementation.startListening(recognitionListener);

            // ✅ Важно: resolve сразу, а результаты идут через listeners
            call.resolve();

        } catch (Exception e) {
            call.reject("Failed to start speech recognition: " + e.getMessage());
        }
    }

    @PluginMethod
    public void stopListening(PluginCall call) {
        try {
            voskImplementation.stopListening();
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to stop speech recognition: " + e.getMessage());
        }
    }

    @PluginMethod
    public void pauseListening(PluginCall call) {
        try {
            voskImplementation.pause(true);
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to pause speech recognition: " + e.getMessage());
        }
    }

    @PluginMethod
    public void resumeListening(PluginCall call) {
        try {
            voskImplementation.pause(false);
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to resume speech recognition: " + e.getMessage());
        }
    }

    @PluginMethod
    public void isListening(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("isListening", voskImplementation.isListening());
        call.resolve(ret);
    }
}
