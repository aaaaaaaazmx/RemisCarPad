package com.drc.remiscar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drc.remiscar.util.TTSManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NotificationService extends Service {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final String TAG = "NotificationService";
    private NotificationManager messageNotificationManager;
    private String taskNumber = "";
    private NotificationCompat.Builder builder1;
    private Intent messageIntent;
    private PendingIntent messagePendingIntent;
    private Handler handRefresh;
    private TTSManager ttsManager;
    private static final int NOTIFICATION_ID = 123665;
    private final IBinder binder = new LocalBinder();
    private PowerManager.WakeLock wakeLock;

    public class LocalBinder extends Binder {
        NotificationService getService() {
            return NotificationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NotificationService::WakeLock");

        ttsManager = new TTSManager(this);
        ttsManager.setAudioAttributes(AudioAttributes.USAGE_ALARM);

        createNotificationChannel();

        Notification notification = buildForegroundNotification();
        startForeground(NOTIFICATION_ID, notification);

        handRefresh = new Handler();
        builder1 = new NotificationCompat.Builder(this, "foreground_service_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("新消息")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        messageNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        messageIntent = new Intent(this, DetailActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            messagePendingIntent = PendingIntent.getActivity(this, 0, messageIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            messagePendingIntent = PendingIntent.getActivity(this, 0, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        MessageThread messageThread = new MessageThread();
        messageThread.start();
    }

    private Uri imageTranslateUri(int resId) {
        Resources r = getResources();
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + r.getResourcePackageName(resId) + "/"
                + r.getResourceTypeName(resId) + "/" + r.getResourceEntryName(resId));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "120跟班";
            String description = "正在运行";
            String channelId = "foreground_service_channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setBypassDnd(true);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
//             channel.setSound(imageTranslateUri(R.raw.msg), audioAttributes);
            channel.setSound(null, audioAttributes);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification buildForegroundNotification() {
        Intent notificationIntent = new Intent(this, DetailActivity.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return new NotificationCompat.Builder(this, "foreground_service_channel")
                .setContentTitle("Service Running")
                .setContentText("Click to return to app")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setTicker("Ticker text")
                .build();
    }

    @Override
    public void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        super.onDestroy();
    }

    class MessageThread extends Thread {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(10000);
                    handRefresh.post(NotificationService.this::getTaskInfo);
                } catch (InterruptedException e) {
                    Log.e(TAG, "MessageThread interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void getTaskInfo() {
        try {
            String url = DetailActivity._Url;
            new loadJSONStringTask().execute(url, "getTaskInfo");
        } catch (Exception e) {
            Log.e(TAG, "Error getting task info", e);
        }
    }

    private class loadJSONStringTask extends AsyncTask<String, Integer, String> {
        int method = -1;

        @Override
        protected String doInBackground(String... params) {
            if ("getTaskInfo".equals(params[1])) {
                method = 0;
            } else if ("getAddressLocation".equals(params[1])) {
                method = 1;
            }
            String result = null;
            try {
                result = WebProxy.getString(params[0], WebProxy.WebRequestType.Get, "");
            } catch (IOException e) {
                Log.e(TAG, "调用服务出错:" + e);
            }
            return result;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String result) {
            if (method == 0) {
                getTaskInfoCallback(result);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getTaskInfoCallback(String content) {
        try {
            if (content != null && !"null".equals(content)) {
                JSONObject result = JSONObject.parseObject(content);
                if ("10000".equals(result.getString("code"))) {
                    JSONArray json = JSONArray.parseArray(result.getString("data"));
                    if (!json.isEmpty()) {
                        JSONObject task = json.getJSONObject(0);
                        String carOutTime = task.getString("carOutTime");
                        // 如果这个时间不为null，那么就不播报了。说明有人接单了。
                        if (!TextUtils.isEmpty(carOutTime)) {
                            return;
                        }
                        String taskNum = task.getString("taskNum");
                        if (!taskNum.isEmpty() && !taskNum.equals(taskNumber)) {
                            wakeLock.acquire(10*60*1000000L); // 10 minutes

                            builder1.setContentTitle("新任务")
                                    .setContentText("您有新的任务。" + task.getString("sceneAddress"))
                                    .setSound(imageTranslateUri(R.raw.msg))
                                    .setFullScreenIntent(messagePendingIntent, true);

                            Notification notification = builder1.build();
                            messageNotificationManager.notify(taskNum.hashCode(), notification);
                            taskNumber = taskNum;

                            // TTS播报新任务
                            ttsManager.speak("您有新的任务", 3, new TTSManager.OnTTSCompletionListener() {
                                @Override
                                public void onCompletion() {
                                    if (wakeLock.isHeld()) {
                                        // wakeLock.release();
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "重复任务，忽略通知");
                        }
                    } else {
                        Log.d(TAG, "当前无新任务");
                    }
                } else {
                    Log.d(TAG, "任务代码非10000");
                }
            } else {
                Log.d(TAG, "内容为空或null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getTaskInfoCallback", e);
            if (wakeLock.isHeld()) {
                // wakeLock.release();
            }
        }
    }

    public void stopTTS() {
        if (ttsManager != null) {
            ttsManager.stopTTS();
        }
    }
}