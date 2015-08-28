package com.mycj.mywatch.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.mycj.mywatch.R;
import com.mycj.mywatch.bean.ConditionWeather;
import com.mycj.mywatch.bean.Forecast;
import com.mycj.mywatch.bean.Place;
import com.mycj.mywatch.bean.Wind;

public class YahooUtil {
	public final static String FORECAST = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22";
	public final static String WIND = "http://query.yahooapis.com/v1/public/yql?q=select%20wind%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20";
	public final static String CONDITION = "http://query.yahooapis.com/v1/public/yql?q=select%20item.condition%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20";
	
	
	public final static String PLACE_ID = "http://query.yahooapis.com/v1/public/yql?q=select%20*from%20geo.places%20where%20text%3D%22";
	public final static String PLACE_END = "%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	
	public final static String END = 	"%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";;
	
	
	/**
	 * 获取Forecast预报url
	 * @param city
	 * @return
	 */
	public static String getForecastUrl(String city) {
		if (city != null&&!city.trim().equals("")) {
			Log.v("YahooUtil", "getForecastUrl()");
			return FORECAST+city+END;
			
		}else{
			return null;
		}
	}
	
	/**
	 * 获取Wind风 url
	 * @param city
	 * @return
	 */
	public static String getWindUrl(String city) {
		if (city != null) {
			return WIND+city+END;
		}else{
			return null;
		}
	}
	
	public static String getConditionUrl(String city){
		if (city != null) {
			return CONDITION+city+END;
		}else{
			return null;
		}
	}

	
	/**
	 * 根据城市名称从yahoo获取城市woeid
	 * @param city
	 */
	public static String getPlaceUrl(String city){
		if (city != null) {
			return PLACE_ID+city+PLACE_END;
		}else{
			return null;
		}
	}
	/**
	 * 根据城市名称从yahoo获取城市woeid
	 * @param city
	 */
	public static String getPlaceNameUrl(String city){
		if (city != null) {
			return PLACE_ID+city+PLACE_END;
		}else{
			return null;
		}
	}
	
	
	/**
	 * 从JSON数据中提取forcast信息
	 * @param data
	 * @return
	 * @throws JSONException
	 */
	public static List<Forecast> parseForecastsFromJson(String data) throws JSONException {
		List<Forecast> list = new ArrayList<>();
		JSONObject json = new JSONObject(data).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item");
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
	
	/**
	 * 从JSON数据中提取wind信息
	 * @param data
	 * @return
	 * @throws JSONException
	 */
	public static Wind parseWindFromJson(String data) throws JSONException {
		JSONObject json = new JSONObject(data).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("wind");
		String chill = json.getString("chill");
		String direction = json.getString("direction");
		String speed = json.getString("speed");
		return new Wind(chill, direction, speed);
	}
	
	/**
	 * 从JSON数据中提取ConditionWeather信息
	 * @param data
	 * @return
	 * @throws JSONException
	 */
	public static ConditionWeather parseConditionWeatherFromJson(String data) throws JSONException {
		JSONObject json = new JSONObject(data).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONObject("condition");
		;
		String date = json.getString("date");
		String temp = json.getString("temp");
		String text = json.getString("text");
		ConditionWeather weather = new ConditionWeather(date, temp, text);
		return weather;
	}
	
	public static List<Place> parseWoeidFromJson(String data) throws JSONException {
		 List<Place> list = new ArrayList<>();
		JSONObject json = new JSONObject(data).getJSONObject("query");
		if (!json.isNull("results")) {
			JSONObject jsonResults = json.getJSONObject("results");
			try {
				//强行被Array
				JSONArray jsonArrayPlace = jsonResults.getJSONArray("place");
				Log.v("YahooUtil", "jsonPlace 搜索匹配地址结果的个数 : "+ jsonArrayPlace.length());
				for (int i = 0; i < jsonArrayPlace.length(); i++) {
					String woeid = ((JSONObject)jsonArrayPlace.get(0)).getString("woeid");
					String name = ((JSONObject)jsonArrayPlace.get(0)).getString("name");
					list.add(new Place(woeid, name));
				}
				
				return list;
			} catch (Exception e) {
				//强行失败时
				JSONObject jsonPlace = jsonResults.getJSONObject("place");
	
				String woeid = jsonPlace.getString("woeid");
				String name = jsonPlace.getString("name");
				Log.v("YahooUtil", "获取name : "+name );
				Log.v("YahooUtil", "获取woeid : "+woeid );
				list.add(new Place(woeid, name));
				return list;
			}
		
		}else{
			Log.v("YahooUtil", "获取woeid : 没有对应地址" );
			return  null;
		}
	}
	
	public static int getIcon(String text){
		if (text.equals("Rain")) {
			return WeatherIcon.RAIN.getValue();
		}else if(text.equals("Mostly Sunny")){
			return WeatherIcon.SUN.getValue();
		} else if(text.equals("Thunderstorms")){
			return WeatherIcon.STORM.getValue();
		} else if(text.equals("Sunny")){
			return WeatherIcon.SUN.getValue();
		}else if(text.equals("Mostly Cloudy")){
			return WeatherIcon.CLOUDY.getValue();
		}else {
			return WeatherIcon.NAN.getValue();
		}
		
	}
	
	
	
	enum WeatherIcon{
		STORM(R.drawable.yahoo_weather_024),
		RAIN(R.drawable.yahoo_weather_014),
		NAN(R.drawable.yahoo_weather_011),
		SUN(R.drawable.yahoo_weather_006),
		CLOUDY(R.drawable.yahoo_weather_002);
		
		private int icon;
		WeatherIcon(int w){
			this.icon = w;
		}
		
		public int getValue(){
			return this.icon;
		}
		
	}
	
	
	
}
