package com.mycj.mywatch.bean;

import org.litepal.crud.DataSupport;

/**
 * 计步器数据
 * @author Administrator
 *
 */
public class StepData extends DataSupport {
	private String date; //yyyyMMdd
	private int id;
	private int step;
	private int distance;
	private int cal;
	private int hour;
	private int minute;
	private int second;
	
	public StepData() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public StepData(String date, int hour,int min, int second) {
		super();
		this.date = date;
		this.hour = hour;
		this.minute = min;
		this.second = second;
	}
	
	public StepData(String date, int step) {
		super();
		this.date = date;
		this.step = step;
	}
	public StepData(int cal,String date) {
		super();
		this.date = date;
		this.cal = cal;
	}
	public StepData(int step, int distance, int cal, int hour, int minute, int second) {
		super();
		this.step = step;
		this.distance = distance;
		this.cal = cal;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}
	public StepData(String date, int step, int distance, int cal, int hour, int minute, int second) {
		super();
		this.date = date;
		this.step = step;
		this.distance = distance;
		this.cal = cal;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getCal() {
		return cal;
	}
	public void setCal(int cal) {
		this.cal = cal;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public int getSecond() {
		return second;
	}
	public void setSecond(int second) {
		this.second = second;
	}
	
	@Override
	public String toString() {
		return "[ step : "+ this.step 
				+ ", distance : " + this.distance
				+ ", cal : " + this.cal
				+ ", hour : " + this.hour
				+ ", mimute : " + this.minute
				+ ", second : " + this.second
				+ " ]";
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}
