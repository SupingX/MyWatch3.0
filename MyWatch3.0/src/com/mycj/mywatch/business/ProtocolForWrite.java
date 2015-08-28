package com.mycj.mywatch.business;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import com.mycj.mywatch.util.DataUtil;

public class ProtocolForWrite extends AbstractProtocolForWrite {
	private final String TAG = "ProtocolForWrite";
	
	private static ProtocolForWrite mProtocolForWrite;
	private ProtocolForWrite(){
		super();
	}
	public static ProtocolForWrite instance(){
		if (mProtocolForWrite==null) {
			mProtocolForWrite = new ProtocolForWrite();
		}
		return mProtocolForWrite;
	}
	
	@Override
	public byte[] getByteForRemind(int type, String number) {
		StringBuffer sb = new StringBuffer();
		sb.append("F1");
		sb.append(DataUtil.toHexString(type));
		sb.append("00");
		int length = number.length();
		sb.append(DataUtil.toHexString(length));
		sb.append(number);
		logV("来电提醒协议 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}

	@Override
	public byte[] getByteForWeather(int weather, int unit, int temperature) {
		StringBuffer sb = new StringBuffer();
		sb.append("F2");
		sb.append(DataUtil.toHexString(weather));
		sb.append(DataUtil.toHexString(unit));
		sb.append(DataUtil.toHexString(temperature));
		logV("天气预报 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}

	@Override
	public byte[] getByteForAvoidLose(int order) {
		StringBuffer sb = new StringBuffer();
		sb.append("F3");
		sb.append(DataUtil.toHexString(order));
		logV("防丢 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}

	@Override
	public byte[] getByteForSyncTime(Date date) {
		StringBuffer sb = new StringBuffer();
		sb.append("F4");
		// 解析日期
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		//
		sb.append(DataUtil.toHexString(year - 1900));
		sb.append(DataUtil.toHexString(month));
		sb.append(DataUtil.toHexString(day));
		sb.append(DataUtil.toHexString(hour));
		sb.append(DataUtil.toHexString(minute));
		sb.append(DataUtil.toHexString(second));

		logV("时间同步 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}

	@Override
	public byte[] getByteForStep(int order) {
		StringBuffer sb = new StringBuffer();
		sb.append("F7");
		sb.append(DataUtil.toHexString(order));
		logV("计步控制 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}

	@Override
	public byte[] getByteForSleep(int result) {
		StringBuffer sb = new StringBuffer();
		sb.append("F8");
		sb.append(DataUtil.toHexString(result));
		logV("睡眠检测 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}

	@Override
	public byte[] getByteForHeartRate(int maxHr, int minHr) {
		StringBuffer sb = new StringBuffer();
		sb.append("F9");
		sb.append("00");
		sb.append("66");
		sb.append(DataUtil.toHexString(minHr));
		sb.append(DataUtil.toHexString(maxHr));
		logV("设置心率 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}

	@Override
	public byte[] getByteForMissedCallAndMessage(int missedCall, int missedNumber) {
		StringBuffer sb = new StringBuffer();
		sb.append("FA");
		sb.append(getMissingCallSmsHexString(missedCall));
		sb.append(getMissingCallSmsHexString(missedNumber));
		logV("未接来电未接信息数量 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}

	@Override
	public byte[] getByteForSyncHistoryData() {
		StringBuffer sb = new StringBuffer();
		sb.append("FE");
		sb.append("00");
		logV("请求历史数据同步 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}

	@Override
	public byte[] getByteForSleepQualityOfToday(int quality) {
		StringBuffer sb = new StringBuffer();
		sb.append("FE");
		sb.append("AA");
		logV("请求当天睡眠质量 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}
	
	@Override
	public byte[] getByteForSleepTime(int start, int end) {
		StringBuffer sb = new StringBuffer();
		sb.append("FE");
		sb.append("06");
		sb.append(DataUtil.toHexString(start));
		sb.append(DataUtil.toHexString(end));
		logV("设置睡眠时间周期 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}
	@Override
	public byte[] getByteForAlarmClock(int hour, int minute) {
		StringBuffer sb = new StringBuffer();
		sb.append("E0");
		sb.append("00");
		sb.append("00");
		sb.append("00");
		sb.append(DataUtil.toHexString(hour));
		sb.append(DataUtil.toHexString(minute));
		logV("设置闹钟时间 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}

	@Override
	public byte[] getByteForCamera(int order) {
		StringBuffer sb = new StringBuffer();
		sb.append("F5");
		sb.append(DataUtil.toHexString(order));
		logV("遥控拍照 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}

	@Override
	public byte[] getByteForShutDown() {
		StringBuffer sb = new StringBuffer();
		sb.append("E1");
		sb.append("01");
		logV("关机 : " + sb.toString());
		return DataUtil.hexStringToByte(sb.toString());
	}

	private void logV(String msg) {
		Log.v(TAG, "**  " + msg + "  **");
	};

	private void logE(String msg) {
		Log.e(TAG, "**  " + msg + "  **");
	};

	private String getMissingCallSmsHexString(int value) {
		String result = "";
		String hex = Integer.toHexString(value);
		if (hex.length() == 1) {
			result = "000" + hex;
		} else if (hex.length() == 2) {
			result = "00" + hex;
		} else if (hex.length() == 3) {
			result = "0" + hex;
		}
		return result;
	}

	
}
