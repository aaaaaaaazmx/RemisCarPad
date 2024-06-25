package com.drc.remiscarmini;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebProxy {
	private static int timeoutConnection = 8000;  
	private static int timeoutSocket = 10000;  
	
	public static String getString(final String url) {
		String retSrc = "";
		try {
			OkHttpClient client = new OkHttpClient();  			
		    Request request = new Request.Builder()  
		        .url(url)  
		        .build();  
		  
		    Response response = client.newCall(request).execute();
		    {  
		    	retSrc= response.body().string();  
		    } 		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSrc;
	}
	public static JSONArray getJsonArray(final String url) {
		JSONArray arr = null;
		String src = getString(url);
		try {
			arr = new JSONArray(src);
		} catch (JSONException e) {
			e.printStackTrace();
			
				throw new RuntimeException("",e);
			
		}
		return arr;
	}
	public static String[] getStringArray(final String url) {

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
	public static String[] getStringArray(final String url,String label) {

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
	public static JSONObject getJson(final String url) {
		JSONObject json = null;
		String src = getString(url);
		try {
			json = new JSONObject(src);
		} catch (JSONException e) {
//			e.printStackTrace();
			throw new RuntimeException("fail2Json", e);
		}
		return json;
	}
	

}
