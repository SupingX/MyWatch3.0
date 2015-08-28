package com.mycj.mywatch.bean;

public class Place {
	private String woeid;
	private String name;
	
	public Place() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Place(String woeid, String name) {
		super();
		this.woeid = woeid;
		this.name = name;
	}

	public String getWoeid() {
		return woeid;
	}
	public void setWoeid(String woeid) {
		this.woeid = woeid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
