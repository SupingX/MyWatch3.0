package com.mycj.mywatch.bean;

import org.litepal.crud.DataSupport;

public class HeartRateData extends DataSupport{
	
	private String date;
	private int id;
	private int hr;
	private int avghr;
	private int maxHr;
	private int minHr;
	public HeartRateData(String date,int hr, int avghr, int maxHr, int minHr) {
		super();
		this.date = date;
		this.hr = hr;
		this.avghr = avghr;
		this.maxHr = maxHr;
		this.minHr = minHr;
	}
	public HeartRateData(int hr, int avghr, int maxHr, int minHr) {
		super();
		this.hr = hr;
		this.avghr = avghr;
		this.maxHr = maxHr;
		this.minHr = minHr;
	}
	public HeartRateData( String date,int avghr, int maxHr, int minHr) {
		super();
		this.date = date;
		this.avghr = avghr;
		this.maxHr = maxHr;
		this.minHr = minHr;
	}
	public HeartRateData( int avghr, int maxHr, int minHr) {
		super();
		this.avghr = avghr;
		this.maxHr = maxHr;
		this.minHr = minHr;
	}
	public HeartRateData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getHr() {
		return hr;
	}
	public void setHr(int hr) {
		this.hr = hr;
	}
	public int getAvghr() {
		return avghr;
	}
	public void setAvghr(int avghr) {
		this.avghr = avghr;
	}
	public int getMaxHr() {
		return maxHr;
	}
	public void setMaxHr(int maxHr) {
		this.maxHr = maxHr;
	}
	public int getMinHr() {
		return minHr;
	}
	public void setMinHr(int minHr) {
		this.minHr = minHr;
	}
	
	@Override
	public String toString() {
		return "[ hr : "+ this.hr 
				+ ", avghr : " + this.avghr
				+ ", minHr : " + this.minHr
				+ ", maxHr : " + this.maxHr
				+ " ]";
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
