package com.mycj.mywatch.bean;

public class Wind {
	private String chill;//寒冷
	private String direction;//方向
	private String speed; //风速
	public Wind() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Wind(String chill, String direction, String speed) {
		super();
		this.chill = chill;
		this.direction = direction;
		this.speed = speed;
	}
	public String getChill() {
		return chill;
	}
	public void setChill(String chill) {
		this.chill = chill;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	
	
	
}
