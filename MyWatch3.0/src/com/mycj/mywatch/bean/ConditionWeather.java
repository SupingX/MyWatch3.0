package com.mycj.mywatch.bean;

public class ConditionWeather {
	
	private String date;
	private String temp;//当前温度
	private String text; //当时天气
	
	
	public ConditionWeather() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public ConditionWeather(String date, String temp, String text) {
		super();
		this.date = date;
		this.temp = temp;
		this.text = text;
	}


	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
