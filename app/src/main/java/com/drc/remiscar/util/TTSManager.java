package com.drc.remiscar.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
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
    private OnTTSCompletionListener completionListener;
    private AudioManager audioManager;

    public interface OnTTSCompletionListener {
        void onCompletion();
    }

    public TTSManager(Context context) {
        this.context = context;
        this.handler = new Handler();
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
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
                    } else if (completionListener != null) {
                        completionListener.onCompletion();
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    // Handle error
                    if (completionListener != null) {
                        completionListener.onCompletion();
                    }
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

    public void speak(String text, int repeat, OnTTSCompletionListener listener) {
        this.repeatCount = repeat;
        this.currentRepeat = 0;
        this.completionListener = listener;
        messageQueue.offer(text);
        if (isInitialized && tts != null) {
            attemptToSpeak();
        } else {
            Log.e("TTSManager", "TTS not initialized, queuing message");
            if (!isInitialized || null == tts) {
                retryInitialization();
            }
        }
    }

    private void attemptToSpeak() {
        if (requestAudioFocus()) { // 请求音频焦点
            speakNext();
        } else {
            Log.e("TTSManager", "Failed to gain audio focus, retrying...");
            handler.postDelayed(this::attemptToSpeak, 5000); // 5秒后重试
        }
    }

    private boolean requestAudioFocus() {
        int result = audioManager.requestAudioFocus(focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK); // 尝试允许在其他音频背景下播报
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // 失去音频焦点
                Log.e("TTSManager", "Lost audio focus");
            }
        }
    };

    private void speakNext() {
        if (isInitialized && tts != null && !messageQueue.isEmpty()) {
            String text = messageQueue.peek();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UniqueID");
            }
        } else if (completionListener != null) {
            completionListener.onCompletion();
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


    // New stop method that clears the queue and stops the TTS
    public void stopSpeaking() {
        if (tts != null && (!messageQueue.isEmpty() || tts.isSpeaking())) {
            tts.stop();  // Immediately stop speaking
            messageQueue.clear();  // Clear the message queue
           /* if (completionListener != null) {
                completionListener.onCompletion();  // Notify that TTS has stopped
            }*/
        }
    }

    private void retryInitialization() {
        // Retry after 10 seconds
        handler.postDelayed(this::initializeTTS, 10000);
    }

    public void setAudioAttributes(int usage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(usage)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build());
        }
    }
}