package com.mycj.mywatch.bean;

public class Forecast {
	private String date;//日期
	private String day;//星期
	private String high;//最高温度
	private String low;//最低温度
	private String text;//天气描述
	
	public Forecast(String date, String day, String high, String low, String text) {
		super();
		this.date = date;
		this.day = day;
		this.high = high;
		this.low = low;
		this.text = text;
	}
	public Forecast() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public String getLow() {
		return low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
