package com.mycj.mywatch.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

import com.mycj.mywatch.bean.ConditionWeather;
import com.mycj.mywatch.bean.Forecast;

public class PullXmlUtil {
	// private XmlPullParser parser;
	private final static String FORECAST = "forecast";
	private static final JSONObject[] JSONArray = null;

	// private PullXmlUtil(InputStream in) {
	// try {
	// Log.e("PullXmlUtil", "____________________in:" + in);
	// parser = Xml.newPullParser();// 得到Pull解析器
	// parser.setInput(in, "UTF-8");// 设置输入流的编码
	// } catch (XmlPullParserException e) {
	// e.printStackTrace();
	// }
	// }

	// public static PullXmlUtil instance(InputStream in) {
	// PullXmlUtil pullXmlUtil = new PullXmlUtil(in);
	// return pullXmlUtil;
	// }

	public static List<Forecast> getForecasts(InputStream in) throws Exception {

		Log.e("PullXmlUtil", "____________________getForecasts()_________in : " + in.available());
		List<Forecast> list = null;
		Forecast forecast = null;
		XmlPullParser parser = Xml.newPullParser();// 得到Pull解析器
		parser.setInput(in, "UTF-8");// 设置输入流的编码
		int eventType = parser.getEventType();
		Log.e("PullXmlUtil", "____________________getForecasts()_____解析开始");
		while (eventType != XmlPullParser.END_DOCUMENT) {// 如果没有到文件的结尾，就不断地解析
			Log.e("PullXmlUtil", "____________________getForecasts()_____解析中。。。");
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT: // 文件的开始
				list = new ArrayList<Forecast>();
				break;
			case XmlPullParser.START_TAG:// 文件的tag开始
				String tagName = parser.getName();// 获得当前标签的名称
				Log.i("PullXmlUtil", "_________________________________________________tag:" + tagName);
				// 解析tag为FORECAST的标签
				if (tagName.equals(FORECAST)) { // 当当前标签的名称是FORECAST
					// 解析属性pro

					String date = parser.getAttributeValue(1);
					String day = parser.getAttributeValue(2);
					String high = parser.getAttributeValue(3);
					String low = parser.getAttributeValue(4);
					String text = parser.getAttributeValue(5);
					Log.v("PullXmlUtil", "_________________________________________________tag:" + date);
					Log.v("PullXmlUtil", "_________________________________________________tag:" + day);
					Log.v("PullXmlUtil", "_________________________________________________tag:" + high);
					Log.v("PullXmlUtil", "_________________________________________________tag:" + low);
					Log.v("PullXmlUtil", "_________________________________________________tag:" + text);
					forecast = new Forecast(date, day, high, low, text);
				}
				Log.v("PullXmlUtil", "_________________________________________________list.size:" + list.size());
				// 解析其他的标签
				// code

				break;
			case XmlPullParser.END_TAG:// 标签的结束
				if (forecast != null) {
					list.add(forecast);// 加入集合
					forecast = null;// forecast置空
				}
				break;
			default:
				break;

			}
			eventType = parser.next();
		}
		Log.e("PullXmlUtil", "_________________________________________________over" + list.size());
		return list;
	}
	
	public static List<Forecast> getForecastsFromJson(String  data) throws JSONException  {
		List<Forecast> list = new ArrayList<>();
		JSONObject json = new JSONObject(data )
					.getJSONObject("query")
					.getJSONObject("results")
					.getJSONObject("channel")
					.getJSONObject("item");
		JSONArray jsonArray = json.getJSONArray("forecast");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.get(i);
			String date = jsonObj.getString("date");
			String day = jsonObj.getString("day");
			String high = jsonObj.getString("high");
			String low = jsonObj.getString("low");
			String text = jsonObj.getString("text");
			Forecast forecast = new Forecast(date, day, high, low, text);
			list.add(forecast);
		}
		return list;
	}
	
	
	public static ConditionWeather getConditionWeatherFromJson(String  data) throws JSONException  {
		JSONObject json = new JSONObject(data )
					.getJSONObject("query")
					.getJSONObject("results")
					.getJSONObject("channel")
					.getJSONObject("item")
					.getJSONObject("condition");
					;
			String date = json.getString("date");
			String temp = json.getString("temp");
			String text = json.getString("text");
			ConditionWeather weather = new ConditionWeather(date, temp, text);
		return weather;
	}
//	public static String getWoeidFromJson(String  data) throws JSONException  {
//		JSONObject json = new JSONObject(data )
//		.getJSONObject("query")
//		.getJSONObject("results")
//		.getJSONObject("place")
//		;
//		
////		JSONArray jsonArray = json.getJSONArray("place");
////		JSONObject jsonWoeid = (JSONObject) jsonArray.get(0);
////		Log.v("", "______________________________"+jsonWoeid);
//		String woeid = json.getString("woeid");
//		return woeid;
//	}
	
}
