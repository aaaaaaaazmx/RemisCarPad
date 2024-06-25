package com.drc.remiscarmini;

import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.IBinder;

public class NotificationService extends Service {

	// 获取消息线程
	private MessageThread messageThread = null;

	// 点击查看
	private Intent messageIntent = null;
	private PendingIntent messagePendingIntent = null;

	// 通知栏消息
	private int messageNotificationID = 1000;
	Notification.Builder builder1 = null;
	//private Notification messageNotification = null;
	private NotificationManager messageNotificatioManager = null;

	private String taskNumber = "";

	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 初始化
		handRefresh = new Handler();

		builder1 = new Notification.Builder(this);
		builder1.setSmallIcon(R.mipmap.ic_launcher); //设置图标
		builder1.setTicker("新消息");

		builder1.setWhen(System.currentTimeMillis()); //发送时间
		builder1.setAutoCancel(false);//打开程序后图标消失
		builder1.setSound(imageTranslateUri(R.raw.msg));

		messageNotificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		messageIntent = new Intent(this, DetailActivity.class);
		messagePendingIntent = PendingIntent.getActivity(this, 0, messageIntent, 0);
		


		// 开启线程
		messageThread = new MessageThread();
		messageThread.isRunning = true;
		messageThread.start();

		return super.onStartCommand(intent, flags, startId);
	}

	private Uri imageTranslateUri(int resId) {

		Resources r = getResources();
		Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + r.getResourcePackageName(resId) + "/"
				+ r.getResourceTypeName(resId) + "/" + r.getResourceEntryName(resId));

		return uri;
	}

	/**
	 * 从服务器端获取消息
	 * 
	 */
	class MessageThread extends Thread {
		// 设置是否循环推送
		public boolean isRunning = true;

		public void run() {
			while (isRunning) {
				try {
					// 间隔时间
					Thread.sleep(10000);
					// 获取服务器消息
					// String serverMessage = getServerMessage();
					PostRefresh();

					// if (serverMessage != null && !"".equals(serverMessage)) {
					// 更新通知栏
					// messageNotification.setLatestEventInfo(
					// getApplicationContext(), "新消息", "您有新消息。"
					// + serverMessage, messagePendingIntent);
					// messageNotificatioManager.notify(messageNotificationID,
					// messageNotification);
					// // 每次通知完，通知ID递增一下，避免消息覆盖掉
					// messageNotificationID++;
					// }
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		// System.exit(0);
		//messageThread.isRunning = false;
		super.onDestroy();
	}

	/**
	 * 模拟发送消息
	 * 
	 * @return 返回服务器要推送的消息，否则如果为空的话，不推送
	 */
	// public String getServerMessage() {
	// return ;
	// }

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
			// TODO Auto-generated method stub

			if (params[1].equals("getTaskInfo")) {
				method = 0;
			} else if (params[1].equals("getAddressLocation")) {
				method = 1;
			}

			return WebProxy.getString(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
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

	private void getTaskInfoCallback(String content) {
		try {
			if (content != null && !content.equals("null")) {

				JSONObject json = new JSONObject(content);
				if (json.length() > 0) {
					String taskNum = json.getJSONObject("0").getString("taskNum");
					if (!"".equals(taskNum) && !taskNum.equals(taskNumber)) {
						
//											//builder1.setContentIntent(messagePendingIntent);
//						builder1.setContentTitle("新任务");
//						builder1.setContentText("您有新的任务。" + json.getJSONObject("0").getString("SceneAddress"));
//						Notification notification1 = builder1.build();
//						//notification1.flags = Notification.FLAG_NO_CLEAR;
//						messageNotificatioManager.notify(124, notification1); // 通过通知管理器发送通知
//
//						//messageNotification.setLatestEventInfo(getApplicationContext(), "新任务",
//						//		"您有新的任务。" + json.getJSONObject("0").getString("SceneAddress"), messagePendingIntent);
//						//messageNotificatioManager.notify(messageNotificationID, messageNotification);
//						// 每次通知完，通知ID递增一下，避免消息覆盖掉
//						messageNotificationID++;
//						taskNumber = taskNum;


						builder1.setContentTitle("新任务");
						builder1.setContentText("您有新的任务。" + json.getJSONObject("0").getString("SceneAddress"));
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
							NotificationChannel channel = new NotificationChannel("5996773", "安卓10a", NotificationManager.IMPORTANCE_DEFAULT);
							channel.enableLights(true);//是否在桌面icon右上角展示小红点
							channel.setLightColor(Color.GREEN);//小红点颜色
							channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
							AudioAttributes audioAttributes = new AudioAttributes.Builder()
									.setUsage(AudioAttributes.USAGE_MEDIA)
									.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
									.build();
							channel.setSound(imageTranslateUri(R.raw.msg),audioAttributes);
							messageNotificatioManager.createNotificationChannel(channel);
						}
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
							builder1.setChannelId("5996773");
						}
						builder1.setSound(imageTranslateUri(R.raw.msg));
						Notification notification1 = builder1.build();

						//messageNotificatioManager.notify(notification1); // 通过通知管理器发送通知
						startForeground(5996773, notification1);
						//messageNotification.setLatestEventInfo(getApplicationContext(), "新任务",
						//		"您有新的任务。" + json.getJSONObject("0").getString("SceneAddress"), messagePendingIntent);
						//messageNotificatioManager.notify(messageNotificationID, messageNotification);
						// 每次通知完，通知ID递增一下，避免消息覆盖掉
						messageNotificationID++;
						taskNumber = taskNum;
					}
				} else {
					// alert("当前无新任务");
				}

			} else {
				// alert("δ�ҵ�����!");
				// Toast.makeText(getApplicationContext(), "δ�ҵ�����",
				// Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			String err = e.toString();
			System.out.println(err);
		}
	}
}
