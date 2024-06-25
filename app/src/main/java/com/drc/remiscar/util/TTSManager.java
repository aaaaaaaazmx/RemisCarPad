package com.drc.remiscar.util;


import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Locale;
import java.util.Queue;

public class TTSManager implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean isInitialized = false;
    private Context context;
    private Handler handler;
    private Queue<String> messageQueue = new ArrayDeque<>();

    public TTSManager(Context context) {
        this.context = context;
        this.handler = new Handler();
        initializeTTS();
    }

    private void initializeTTS() {
        if (tts == null) {
            tts = new TextToSpeech(context, this);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.CHINA);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTSManager", "Language not supported");
            } else {
                isInitialized = true;
                flushMessageQueue();  // Process any queued messages
            }
        } else {
            Log.e("TTSManager", "Initialization failed");
            retryInitialization();  // Retry initializing if failed
        }
    }

    public void speak(String text) {
        if (isInitialized && tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        } else {
            Log.e("TTSManager", "TTS not initialized, queuing message");
            messageQueue.offer(text);  // Queue the message for later playback
            if (!isInitialized || null == tts) {
                retryInitialization();  // Retry initializing if failed
            }
        }
    }

    private void flushMessageQueue() {
        while (!messageQueue.isEmpty()) {
            String message = messageQueue.poll();
            speak(message);
        }
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    private void retryInitialization() {
        // Retry after 10 seconds
        handler.postDelayed(this::initializeTTS, 10000);
    }
}
