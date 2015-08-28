package com.mycj.mywatch.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class LoadWeatherJsonTask extends AsyncTask<String, Integer, String> {
	private final static int MSG_PLACE = 0x100004;
	private final static int MSG_FORECAST = 0x10005;
	// 用于区分每次加载的地址
	private int id;
	private Context mContext;
	private Handler mHandler;
	public LoadWeatherJsonTask(int id, Context context,Handler handler) {
		super();
		this.id = id;
		this.mContext = context;
		this.mHandler = handler;

	}
	@Override
	protected String doInBackground(String... params) {
		Log.e("", "------后台获取中------> ");
	
		// 这里下载数据
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		InputStreamReader isr = null;
		BufferedReader bufferReader = null;
		StringBuffer json = new StringBuffer();
		try {
			URL url = new URL(params[0]);
	
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			// conn.setConnectTimeout(10000);// 设置请求时间为10ms
			// conn.setReadTimeout(5000);// 设置读取时间为5ms
			conn.setRequestProperty("Content-type", "text/html");
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("contentType", "utf-8");
			conn.setDoInput(true);
			 conn.setUseCaches(false); // 不使用缓冲
			conn.connect();
			int responseCode = conn.getResponseCode();// 获取网络访问地址的回传码，如果为200则表示成功
			if (responseCode == 200) {
				inputStream = conn.getInputStream();// 获得网络地址返回的二进制流数据
			}
			// lenght = inputStream.available();
			isr = new InputStreamReader(inputStream);
			 bufferReader = new BufferedReader(isr);
			String inputLine = "";
			while ((inputLine = bufferReader.readLine()) != null) {
				json.append(inputLine);
			}
		} catch (MalformedURLException e1) {
			Log.v("", "异常 e1");
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			Log.v("", "异常 e");
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (isr!=null) {
					isr.close();
				}
				if (bufferReader!=null) {
					bufferReader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		
		}
		return json.toString();
	}

	@Override
	protected void onPostExecute(String result) {
		Log.e("", "------异步下载结束------>  InputStream : " + result);
		if (mOnProgressChangeListener!=null) {
			mOnProgressChangeListener.onPostExecute(id);
		}
	
		if (result != null&&!result.trim().equals("")) {
			if (mOnProgressChangeListener!=null) {
				mOnProgressChangeListener.onPostExecute(id);
			}
			switch (id) {
			case MSG_FORECAST:
				Log.v("", "< ------ 获取forecastjson完毕 ------ > ");
				Message msg4 = mHandler.obtainMessage();
				Bundle b4 = new Bundle();
				b4.putString("json", result);
				msg4.setData(b4);
				msg4.what = MSG_FORECAST;
				mHandler.sendMessage(msg4);
				
				break;
			case MSG_PLACE:
				Log.v("", "< ------ 获取城市json完毕 ------ > ");
				Message msg5 = mHandler.obtainMessage();
				Bundle b5 = new Bundle();
				b5.putString("json", result);
				msg5.setData(b5);
				msg5.what = MSG_PLACE;
				mHandler.sendMessage(msg5);
				break;

			default:
				break;
			}
		}
		else{
			if (mOnProgressChangeListener!=null) {
				mOnProgressChangeListener.onError(id);
			}
		}

		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		if (mOnProgressChangeListener!=null) {
			mOnProgressChangeListener.onPreExecute(id);
		}
		super.onPreExecute();
	}


	/**
	 * 加载前和加载后的回调
	 * @author Administrator
	 *
	 */
	public interface OnProgressChangeListener{
		/**
		 * 加载前
		 * @param id
		 */
		public void onPreExecute(int id );
		/**
		 * 加载后
		 * @param id
		 */
		public void onPostExecute(int id);
		/**
		 * 异常时
		 * @param id
		 */
		public void onError(int id);
	}
	private OnProgressChangeListener mOnProgressChangeListener;
	public void setOnProgressChangeListener (OnProgressChangeListener l){
		this.mOnProgressChangeListener = l;
	}
}
