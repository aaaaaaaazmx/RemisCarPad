package com.drc.remiscar;

import android.accounts.NetworkErrorException;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@TargetApi(Build.VERSION_CODES.ECLAIR)
public class WebProxy {
	private static final int timeoutConnection = 8000;
	private static final int timeoutSocket = 10000;
	enum WebRequestType
    {
        Get,
        Post
    }

	public static String getString(final String url, WebRequestType webRequestType, String arg) throws  IOException {
		String retSrc = "";

			OkHttpClient client = new OkHttpClient().newBuilder()
					.readTimeout(10, TimeUnit.SECONDS)
					.readTimeout(10, TimeUnit.SECONDS)
					.retryOnConnectionFailure(true)
					.build();
			Log.i("123123123 url", url);
            Request request = null;
            if(webRequestType == WebRequestType.Get) {
                request = new Request.Builder()
					.addHeader("ak","UgBlAG0AaQBzAC4AMQAyADAAOgBaAGgAdQBaAGgAbwB1AA==")
						.addHeader("merCode","666666")
                        .url(url)
                        .build();
            }else
            {
                MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
                RequestBody body = RequestBody.create(mediaType,arg);
                request = new Request.Builder()
                        .post(body)
                        .addHeader("ak","UgBlAG0AaQBzAC4AMQAyADAAOgBaAGgAdQBaAGgAbwB1AA==")
						.addHeader("merCode","666666")
                        .url(url)
                        .build();
            }
		Response response = null;
		try {
			response = client.newCall(request).execute();
			retSrc= response.body().string();
			Log.i("123123123", retSrc);
		} catch (IOException e) {
			throw e;
		}
		return retSrc;
	}
	public static JSONArray getJsonArray(final String url) throws NetworkErrorException, IOException {
		JSONArray arr = null;
		String src = null;
		src = getString(url, WebRequestType.Get,"");
		try {
			arr = new JSONArray(src);
		} catch (JSONException e) {
			e.printStackTrace();
			
				throw new RuntimeException("",e);
			
		}
		return arr;
	}
	public static String[] getStringArray(final String url) throws NetworkErrorException, IOException {

		String[] strs = null;
		JSONArray arr= getJsonArray(url);
		try {
			
			int len = arr.length();
			strs = new String[len];
			for (int i=0; i<len; i++) {
				strs[i] = arr.getJSONObject(i).getString("label");
			}
		} catch (Exception e) {
//			e.printStackTrace();
			throw new RuntimeException("fail2StringArray", e);
		}
		return strs;
	}
	public static String[] getStringArray(final String url,String label) throws NetworkErrorException, IOException {

		String[] strs = null;
		JSONArray arr= getJsonArray(url);
		try {
			
			int len = arr.length();
			strs = new String[len];
			for (int i=0; i<len; i++) {
				strs[i] = arr.getJSONObject(i).getString(label);
			}
		} catch (Exception e) {
//			e.printStackTrace();
			throw new RuntimeException("fail2StringArray", e);
		}
		return strs;
	}
	public static String[] getStringArray(JSONObject json, String arrName) {
		String[] strs = null;
		try {
			JSONArray arr= json.getJSONArray(arrName);
			int len = arr.length();
			strs = new String[len];
			for (int i=0; i<len; i++) {
				strs[i] = arr.getJSONObject(i).getString("label");
			}
		} catch (Exception e) {
//			e.printStackTrace();
			throw new RuntimeException("fail2StringArray", e);
		}
		return strs;
	}
	@SneakyThrows
	public static JSONObject getJson(final String url) throws NetworkErrorException, IOException {
		JSONObject json = null;
		String src = getString(url,WebRequestType.Get,"");
		try {
			json = new JSONObject(src);
		} catch (JSONException e) {
//			e.printStackTrace();
			throw new RuntimeException("fail2Json", e);
		}
		return json;
	}
	

}
