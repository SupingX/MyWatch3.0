package com.mycj.mywatch.bean;

import org.litepal.crud.DataSupport;

public class SleepData extends DataSupport{
	private int id;
	private int[] sleeps; //一天的数据集合
	private String date;//格式yyMMdd
	public SleepData() {
		super();
	}
	public SleepData(int []sleep, String date) {
		super();
		this.setSleeps((sleep));
		this.date = date;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int[] getSleeps() {
		return sleeps;
	}
	public void setSleeps(int[] sleeps) {
		this.sleeps = sleeps;
	}
	
}
