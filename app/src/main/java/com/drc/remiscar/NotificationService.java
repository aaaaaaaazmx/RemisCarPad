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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drc.remiscar.util.TTSManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class NotificationService extends Service implements MyWebSocket.WebSocketListener {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    static boolean isDebug = true;
    private MessageThread messageThread = null;
    private MyWebSocket myWebSocket;
    private Intent messageIntent = null;
    private PendingIntent messagePendingIntent = null;
    private int messageNotificationID = 1000;
    Notification.Builder builder1 = null;
    private NotificationManager messageNotificatioManager = null;
    private String taskNumber = "";
    private TTSManager ttsManager;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("NotificationService", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ttsManager == null) {
            ttsManager = new TTSManager(this);
        }
        // 常驻通知栏
        createNotificationChannel(); // 确保通知渠道已创建
        Notification notification = buildForegroundNotification();
        startForeground(123665, notification); // 1是服务的通知ID

        handRefresh = new Handler();
        builder1 = new Notification.Builder(this);
        builder1.setSmallIcon(R.mipmap.ic_launcher);
        builder1.setTicker("新消息");
        builder1.setWhen(System.currentTimeMillis());
        builder1.setAutoCancel(true);
        builder1.setSound(imageTranslateUri(R.raw.msg));
        builder1.setDefaults(Notification.DEFAULT_ALL); // 确保激活声音，振动等

        messageNotificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        messageIntent = new Intent(this, DetailActivity.class);
        messagePendingIntent = PendingIntent.getActivity(this, 0, messageIntent, 0);

        messageThread = new MessageThread();
        messageThread.isRunning = true;
        messageThread.start();
        RequestMessage rm = new RequestMessage();
        rm.setMessage("心跳包");

        myWebSocket = new MyWebSocket(JSONObject.toJSONString(rm));
        myWebSocket.setWebSocket(this);
        return START_STICKY;
    }

    private Uri imageTranslateUri(int resId) {
        Resources r = getResources();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + r.getResourcePackageName(resId) + "/"
                + r.getResourceTypeName(resId) + "/" + r.getResourceEntryName(resId));
        return uri;
    }

    public static void e(String msg) {
        if (isDebug) {
            PrintToFileUtil.input2File(msg, "remis.log");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getMessage(String message) {
        Log.i("getMessage", message);
        // TTS播报收到的消息
        /*if (tts != null && !message.isEmpty()) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }*/
    }

    @Override
    public void closeWebSocket(int code, String reason, boolean remote) {

    }

    public static class PrintToFileUtil {
        static ScheduledExecutorService sExecutor = null;

        public static boolean input2File(final String input, final String filePath) {
            if (sExecutor == null) {
                sExecutor = Executors.newScheduledThreadPool(5);
            }
            Future<Boolean> submit = sExecutor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    PrintWriter writer = null;
                    try {
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/remis/";
                        File dir = new File(path);
                        if (!dir.exists())
                            dir.mkdirs();
                        writer = new PrintWriter(path + filePath, "UTF-8");
                        writer.write(input);
                        writer.append("------------");
                        writer.append(new Date().toString());
                        writer.flush();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    } finally {
                        if (writer != null) {
                            writer.close();
                        }
                    }
                }
            });
            try {
                return submit.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    class MessageThread extends Thread {
        public boolean isRunning = true;

        public void run() {
            while (isRunning) {
                try {
                    Thread.sleep(10000);
                    PostRefresh();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }

    Handler handRefresh = null;

    public void PostRefresh() {
        try {
            handRefresh.post(new Runnable() {
                @Override
                public void run() {
                    getTaskInfo();
                }
            });
        } catch (Exception e) {
            String err = e.toString();
            System.out.println(err);
        }
    }

    private void getTaskInfo() {
        try {
            String url = DetailActivity._Url;
            loadJSONStringTask task = new loadJSONStringTask();
            task.execute(url, "getTaskInfo");
        } catch (Exception e) {
            String err = e.toString();
            System.out.println(err);
        }
    }

    private class loadJSONStringTask extends AsyncTask<String, Integer, String> {
        int method = -1;

        @Override
        protected String doInBackground(String... params) {
            if (params[1].equals("getTaskInfo")) {
                method = 0;
            } else if (params[1].equals("getAddressLocation")) {
                method = 1;
            }
            String result = null;
            try {
                result = WebProxy.getString(params[0], WebProxy.WebRequestType.Get, "");
            } catch (IOException e) {
                e("调用服务出错:" + e);
            }
            return result;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String result) {
            switch (method) {
                case 0:
                    getTaskInfoCallback(result);
                    break;
                case 1:
                    // getAddressLocationCallback(result);
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getTaskInfoCallback(String content) {
        try {
            if (content != null && !content.equals("null")) {
                JSONObject result = JSONObject.parseObject(content);
                if (result.getString("code").equals("10000")) {
                    JSONArray json = JSONArray.parseArray(result.getString("data"));
                    if (!json.isEmpty()) {
                        JSONObject task = json.getJSONObject(0);
                        String taskNum = task.getString("taskNum");
                        if (!"".equals(taskNum) && !taskNum.equals(taskNumber)) {
                            builder1.setContentTitle("新任务");
                            builder1.setContentText("您有新的任务。" + task.getString("sceneAddress"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel channel = new NotificationChannel("5996773", "安卓10a", NotificationManager.IMPORTANCE_HIGH);
                                channel.enableLights(true);
                                channel.setLightColor(Color.GREEN);
                                channel.setShowBadge(true);
                                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .build();
                                channel.setSound(imageTranslateUri(R.raw.msg), audioAttributes);
                                messageNotificatioManager.createNotificationChannel(channel);
                            }
                            builder1.setChannelId("5996773");
                            builder1.setSound(imageTranslateUri(R.raw.msg));
                            builder1.setDefaults(Notification.DEFAULT_ALL); // 确保激活声音，振动等
                            Notification notification1 = builder1.build();
                            startForeground(5996773, notification1);
                            messageNotificationID++;
                            taskNumber = taskNum;

                            // TTS播报新任务
                            ttsManager.speak("您有新的任务: " + task.getString("sceneAddress"));
                        }
                    } else {
                        // 没有任务。
                    }
                } else {
                    // alert("当前无新任务");
                }
            } else {
                // alert("当前无新任务");
            }
        } catch (Exception e) {
            String err = e.toString();
            System.out.println(err);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "120跟班";
            String description = "正在运行";
            String channelId = "foreground_service_channel";
            int importance = NotificationManager.IMPORTANCE_HIGH; // 低重要性，足以保持通知但不会打扰用户
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification buildForegroundNotification() {
        Intent notificationIntent = new Intent(this, DetailActivity.class); // 点击通知时打开的Activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, "foreground_service_channel")
                .setContentTitle("Service Running")
                .setContentText("Click to return to app")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setTicker("Ticker text")
                .build();
    }


}
