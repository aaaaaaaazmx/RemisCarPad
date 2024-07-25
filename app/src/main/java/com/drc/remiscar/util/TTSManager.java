package com.drc.remiscar.util;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.Locale;
import java.util.Queue;

public class TTSManager implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean isInitialized = false;
    private Context context;
    private Handler handler;
    private Queue<String> messageQueue = new ArrayDeque<>();
    private int repeatCount = 0;
    private int currentRepeat = 0;

    public TTSManager(Context context) {
        this.context = context;
        this.handler = new Handler();
        initializeTTS();
    }

    private void initializeTTS() {
        if (tts == null) {
            tts = new TextToSpeech(context, this);
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    // Do nothing
                }

                @Override
                public void onDone(String utteranceId) {
                    currentRepeat++;
                    if (currentRepeat < repeatCount) {
                        speakNext();
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    // Do nothing
                }
            });
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

    public void speak(String text, int repeat) {
        this.repeatCount = repeat;
        this.currentRepeat = 0;
        messageQueue.offer(text);  // Queue the message for later playback
        if (isInitialized && tts != null) {
            speakNext();
        } else {
            Log.e("TTSManager", "TTS not initialized, queuing message");
            if (!isInitialized || null == tts) {
                retryInitialization();  // Retry initializing if failed
            }
        }
    }

    private void speakNext() {
        if (isInitialized && tts != null && !messageQueue.isEmpty()) {
            String text = messageQueue.peek();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UniqueID");
            }
        }
    }

    private void flushMessageQueue() {
        if (!messageQueue.isEmpty()) {
            speakNext();
        }
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    public void stopTTS() {
        if (tts != null) {
            tts.stop();
        }
    }

    private void retryInitialization() {
        // Retry after 10 seconds
        handler.postDelayed(this::initializeTTS, 10000);
    }
}
