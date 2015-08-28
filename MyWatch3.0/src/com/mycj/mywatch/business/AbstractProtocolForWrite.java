package com.mycj.mywatch.business;

import java.util.Date;

public abstract class AbstractProtocolForWrite {
	/**
	 * 来电提醒
	 * 协议头0xF1
	 * 例子:0x F1 80 0B 14 13800138000 
	 * @param type	电话 0x80 短信0x20 没有0x00 为一个十六进制Integer
	 * @param number 电话号码
	 * @return
	 */
	public abstract byte[] getByteForRemind(int type,String number);
	/**
	 * 天气
	 * 0xF2
	 * 例子:0x F2 01 01 ff
	 * @param weather 天气
	 * @param unit 温度单位
	 * @param temperature 温度值
	 * @return
	 */
	public abstract byte[] getByteForWeather(int weather,int unit,int temperature );
	/**
	 * 防丢
	 * 0xF3
	 * 例子:0xF3 01
	 * @param order  命令 ：App 查找手环开始:0x01;　App 查找手环停止:0xa1;　超范围报警开始:0x02;　超范围报警停止:0xa2;
	 * @return
	 */
	public abstract byte[] getByteForAvoidLose(int order);
	
	/**
	 * 时间同步
	 * 0xF4
	 * 例子:0x 11 11 11 11 11 11 11 
	 * 年，表示从 1900~2156 年， 例：0x00 为 1900 年
	 * 月，例：0x01 为 1 月
	 * @param date 当前时间 
	 * @return
	 */
	public abstract byte[] getByteForSyncTime(Date 	date);
	/**
	 * 计步控制
	 * 0xF7
	 * 例子:0x F7 02
	 * @param order 计步清零:0x02;请求计步数据:0x04;
	 * @return
	 */
	public abstract byte[] getByteForStep(int order);
	/**
	 * 睡眠检测
	 * 0xF8
	 * 例子:0x F8 01
	 * @param result 01~05
	 * @return
	 */
	public abstract byte[] getByteForSleep(int result);
	/**
	 * 设置最大心率
	 * 0xF9
	 * 例子:0x F9 00 66 fe
	 * @param maxHr
	 * @param minHr
	 * @return
	 */
	public abstract byte[] getByteForHeartRate(int maxHr,int minHr);
	/**
	 * 未接短信／电话数量
	 * 0xfa
	 * 例子:0x fa 1111 1111
	 * @param missedCall
	 * @param missedNumber
	 * @return
	 */
	public abstract byte[] getByteForMissedCallAndMessage(int missedCall,int missedNumber);
	/**
	 * 请求历史数据同步
	 * 0xfe
	 * 例子:0x fa 00
	 * @return
	 */
	public abstract byte[] getByteForSyncHistoryData();
	/**
	 * 请求当天睡眠质量
	 * oxfe
	 * 例子:0x fa aa
	 * @param quality
	 * @return
	 */
	public abstract byte[] getByteForSleepQualityOfToday(int quality);
	
	/**
	 * 设置睡眠时间周期
	 * 	 oxfe
	 * 例子:0x fa 06 bb  aa
	 * @param start 0-23
	 * @param end 0-23
	 * @return
	 */
	public abstract byte[] getByteForSleepTime(int start,int end);
	/**
	 * 设置闹钟设置时间
	 * 0xE0
	 * 例子:0x e0 00 00 00 11 11
	 * @param hour
	 * @param minute
	 * @return
	 */
	public abstract byte[] getByteForAlarmClock(int hour,int minute);
	/**
	 * 遥控拍照
	 * 0xf5
	 * 例子:0x f5 01
	 * @param order　退出拍照：01;进入拍照:02;
	 * @return
	 */
	public abstract byte[] getByteForCamera(int order);
	/**
	 * 关机
	 * 0xE1
	 * 例子:0x e1 01
	 * @return
	 */
	public abstract byte[] getByteForShutDown();
	
	
}
