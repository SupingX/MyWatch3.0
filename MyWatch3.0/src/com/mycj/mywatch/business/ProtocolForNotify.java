package com.mycj.mywatch.business;

import java.util.Calendar;
import java.util.Date;
import android.util.Log;
import com.mycj.mywatch.bean.HeartRateData;
import com.mycj.mywatch.bean.SleepData;
import com.mycj.mywatch.bean.StepData;
import com.mycj.mywatch.util.DataUtil;
import com.mycj.mywatch.util.DateUtil;

public class ProtocolForNotify extends AbstractProtocolForNotify {

	private final String TAG = "ProtocolForNotify";
	public final static String DATE_FORMAT = "yyyyMMdd";
	public final static int NOTIFY_REMIND = 0x01;
	public final static int NOTIFY_SYNC_TIME = 0x02;
	public final static int NOTIFY_CAMERA = 0x03;
	public final static int NOTIFY_MUSIC = 0x04;
	public final static int NOTIFY_STEP = 0x05;
	public final static int NOTIFY_HEART_RATE = 0x06;
	public final static int NOTIFY_HISTORY_HEART_RATE = 0x07;
	public final static int NOTIFY_HISTORY_STEP = 0x08;
	public final static int NOTIFY_HISTORY_SLEEP = 0x09;
	public final static int NOTIFY_HISTORY_DISTACE = 0x10;
	public final static int NOTIFY_HISTORY_CAL = 0x11;
	public final static int NOTIFY_HISTORY_SPORT_TIME = 0x12;
	public final static int NOTIFY_HISTORY_SLEEP_FOR_TODAY = 0x13;

	private static ProtocolForNotify mProtocolForNotify;

	private ProtocolForNotify() {

	}

	public static ProtocolForNotify instance() {
		if (mProtocolForNotify == null) {
			mProtocolForNotify = new ProtocolForNotify();
		}
		return mProtocolForNotify;
	}

	public int getTypeFromData(byte[] data) {
		String dataStr = DataUtil.byteToHexString(data);
		String pro = dataStr.substring(0, 2);
		String obj = dataStr.substring(2, 4);
		logV(dataStr);
		if (pro.equals("F3") && dataStr.length() >= 4) {
			return NOTIFY_REMIND;
		} else if (pro.equals("F4") && dataStr.length() == 2) {
			return NOTIFY_SYNC_TIME;
		} else if (pro.equals("F5") && dataStr.length() >= 4) {
			return NOTIFY_CAMERA;
		} else if (pro.equals("F6") && dataStr.length() >= 6) {
			return NOTIFY_MUSIC;
		} else if (pro.equals("F7") && dataStr.length() == 32) {
			return NOTIFY_STEP;
		} else if (pro.equals("F9") && dataStr.length() == 20) {
			return NOTIFY_HEART_RATE;
		} else if (pro.equals("FE") && obj.equals("04") && dataStr.length() == 12) {
			return NOTIFY_HISTORY_HEART_RATE;
		} else if (pro.equals("FE") && obj.equals("00") && dataStr.length() == 14) {
			return NOTIFY_HISTORY_STEP;
		} else if (pro.equals("FE") && obj.equals("05") && dataStr.length() == 30) {
			return NOTIFY_HISTORY_SLEEP;
		} else if (pro.equals("FE") && obj.equals("01") && dataStr.length() == 14) {
			return NOTIFY_HISTORY_DISTACE;
		} else if (pro.equals("FE") && obj.equals("02") && dataStr.length() == 14) {
			return NOTIFY_HISTORY_CAL;
		} else if (pro.equals("FE") && obj.equals("03") && dataStr.length() == 14) {
			return NOTIFY_HISTORY_SPORT_TIME;
		} else if (pro.equals("FE") && obj.equals("06") && dataStr.length() == 30) {
			return NOTIFY_HISTORY_SLEEP_FOR_TODAY;
		} else {
			logE("错误的数据");
			return -1;
		}
	}

	@Override
	public int notifyForRemind(byte[] data) {
		String dataStr = DataUtil.byteToHexString(data);
		if (getTypeFromData(data) == NOTIFY_REMIND) {
			logV("防丢");
			String orderStr = dataStr.substring(2, 4);
			int order = Integer.parseInt(orderStr, 16);
			logV(order + "");
			return order;
		} else {
			logE("错误的数据");
			return -1;
		}
	}

	@Override
	public boolean notifyForSyncTime(byte[] data) {
		String dataStr = DataUtil.byteToHexString(data);
		if (getTypeFromData(data) == NOTIFY_SYNC_TIME) {
			logV("请求同步时间");
			return true;
		} else {
			logE("错误的数据");
			return false;
		}
	}

	@Override
	public int notifyForCamera(byte[] data) {
		String dataStr = DataUtil.byteToHexString(data);
		if (getTypeFromData(data) == NOTIFY_CAMERA) {
			logV("拍照");
			return  Integer.parseInt(dataStr.substring(2, 4), 16);
		} else {
			logE("错误的数据");
			return -1;
		}
	}

	@Override
	public int notifyForMusic(byte[] data) {
		String dataStr = DataUtil.byteToHexString(data);
		logV(dataStr);
		if (getTypeFromData(data) == NOTIFY_MUSIC) {
			logV("音乐控制");
			String orderStr = dataStr.substring(2, 6);
			int order = Integer.parseInt(orderStr, 16);
			logV(order + "");
			return order;
		} else {
			logE("错误的数据");
			return -1;
		}
	}

	@Override
	public StepData notifyForStepData(byte[] data) {
		StepData stepData = null;
		String dataStr = DataUtil.byteToHexString(data);
		if (getTypeFromData(data) == NOTIFY_STEP) {
			logV("计步器");
			String stepStr = dataStr.substring(2, 10);
			String distanceStr = dataStr.substring(10, 18);
			String calStr = dataStr.substring(18, 26);
			String hourStr = dataStr.substring(26, 28);
			String minStr = dataStr.substring(28, 30);
			String secondStr = dataStr.substring(30, 32);

			int step = Integer.parseInt(stepStr, 16);
			int distance = Integer.parseInt(distanceStr, 16);
			int cal = Integer.parseInt(calStr, 16);
			int hour = Integer.parseInt(hourStr, 16);
			int minute = Integer.parseInt(minStr, 16);
			int second = Integer.parseInt(secondStr, 16);

			stepData = new StepData(step, distance, cal, hour, minute, second);
			logV(stepData.toString());
		} else {
			logE("错误的数据");
		}
		return stepData;
	}

	@Override
	public HeartRateData notifyForHeartRateData(byte[] data) {
		HeartRateData hrData = null;
		String dataStr = DataUtil.byteToHexString(data);
		if (getTypeFromData(data) == NOTIFY_HEART_RATE) {
			logV("心率");
			String hrStr = dataStr.substring(2, 6);
			String avgStr = dataStr.substring(14, 16);
			String minStr = dataStr.substring(16, 18);
			String maxStr = dataStr.substring(18, 20);

			int hr = Integer.parseInt(hrStr, 16);
			int avghr = Integer.parseInt(avgStr, 16);
			int maxHr = Integer.parseInt(minStr, 16);
			int minHr = Integer.parseInt(maxStr, 16);
			hrData = new HeartRateData(hr, avghr, maxHr, minHr);
			logV(hrData.toString());
		} else {
			logE("错误的数据");
		}
		return hrData;
	}

	@Override
	public HeartRateData notifyForHistoryDataToHearRateData(byte[] data) {
		HeartRateData hrData = null;
		String dataStr = DataUtil.byteToHexString(data);
		if (getTypeFromData(data) == NOTIFY_HISTORY_HEART_RATE) {
			logV("历史数据 心率");
			String dateStr = dataStr.substring(4, 6);
			String avgStr = dataStr.substring(6, 8);
			String minStr = dataStr.substring(8, 10);
			String maxStr = dataStr.substring(11, 12);

			int date = Integer.parseInt(dateStr, 16);
			Date today = new Date();
			String historyDate = getHistoryDate(today, date + 1);
			int avghr = Integer.parseInt(avgStr, 16);
			int maxHr = Integer.parseInt(minStr, 16);
			int minHr = Integer.parseInt(maxStr, 16);

			hrData = new HeartRateData(historyDate, avghr, maxHr, minHr);
		} else {
			logE("错误的数据");
		}
		return hrData;
	}

	@Override
	public StepData notifyForHistoryDataToStepData(byte[] data) {
		StepData stepData = null;
		String dataStr = DataUtil.byteToHexString(data);
		if (getTypeFromData(data) == NOTIFY_HISTORY_STEP) {
			logV("历史数据 计步");
			String dateStr = dataStr.substring(4, 6);
			String stepVale = dataStr.substring(6, 14);
			int date = Integer.parseInt(dateStr, 16);
			Date today = new Date();
			String historyDate = getHistoryDate(today, date + 1);
			int step = Integer.parseInt(stepVale, 16);
			stepData = new StepData(historyDate, step);
		} else {
			logE("错误的数据");
		}
		return stepData;
	}

	@Override
	public SleepData notifyForHistoryDataToSleepData(byte[] data) {
		SleepData sleepData = null;
		String dataStr = DataUtil.byteToHexString(data);
		if (getTypeFromData(data) == NOTIFY_HISTORY_SLEEP) {
			logV("历史数据 睡眠");
			// 获取历史日期
			String dateStr = dataStr.substring(4, 6);
			int date = Integer.parseInt(dateStr, 16);
			Date today = new Date();
			String historyDate = getHistoryDate(today, date + 1);
			// 获取每天时间段的值
			String sleepVale1 = dataStr.substring(6, 7);
			String sleepVale2 = dataStr.substring(7, 8);
			String sleepVale3 = dataStr.substring(8, 9);
			String sleepVale4 = dataStr.substring(9, 10);
			String sleepVale5 = dataStr.substring(10, 11);
			String sleepVale6 = dataStr.substring(11, 12);
			String sleepVale7 = dataStr.substring(12, 13);
			String sleepVale8 = dataStr.substring(13, 14);
			String sleepVale9 = dataStr.substring(14, 15);
			String sleepVale10 = dataStr.substring(15, 16);
			String sleepVale11 = dataStr.substring(16, 17);
			String sleepVale12 = dataStr.substring(17, 18);
			String sleepVale13 = dataStr.substring(18, 19);
			String sleepVale14 = dataStr.substring(19, 20);
			String sleepVale15 = dataStr.substring(20, 21);
			String sleepVale16 = dataStr.substring(21, 22);
			String sleepVale17 = dataStr.substring(22, 23);
			String sleepVale18 = dataStr.substring(23, 24);
			String sleepVale19 = dataStr.substring(24, 25);
			String sleepVale20 = dataStr.substring(25, 26);
			String sleepVale21 = dataStr.substring(26, 27);
			String sleepVale22 = dataStr.substring(27, 28);
			String sleepVale23 = dataStr.substring(28, 29);
			String sleepVale24 = dataStr.substring(29, 30);
			int sleep1 = Integer.parseInt(sleepVale1, 16);
			int sleep2 = Integer.parseInt(sleepVale2, 16);
			int sleep3 = Integer.parseInt(sleepVale3, 16);
			int sleep4 = Integer.parseInt(sleepVale4, 16);
			int sleep5 = Integer.parseInt(sleepVale5, 16);
			int sleep6 = Integer.parseInt(sleepVale6, 16);
			int sleep7 = Integer.parseInt(sleepVale7, 16);
			int sleep8 = Integer.parseInt(sleepVale8, 16);
			int sleep9 = Integer.parseInt(sleepVale9, 16);
			int sleep10 = Integer.parseInt(sleepVale10, 16);
			int sleep11 = Integer.parseInt(sleepVale11, 16);
			int sleep12 = Integer.parseInt(sleepVale12, 16);
			int sleep13 = Integer.parseInt(sleepVale13, 16);
			int sleep14 = Integer.parseInt(sleepVale14, 16);
			int sleep15 = Integer.parseInt(sleepVale15, 16);
			int sleep16 = Integer.parseInt(sleepVale16, 16);
			int sleep17 = Integer.parseInt(sleepVale17, 16);
			int sleep18 = Integer.parseInt(sleepVale18, 16);
			int sleep19 = Integer.parseInt(sleepVale19, 16);
			int sleep20 = Integer.parseInt(sleepVale20, 16);
			int sleep21 = Integer.parseInt(sleepVale21, 16);
			int sleep22 = Integer.parseInt(sleepVale22, 16);
			int sleep23 = Integer.parseInt(sleepVale23, 16);
			int sleep24 = Integer.parseInt(sleepVale24, 16);

			int[] sleeps = new int[] { sleep1, sleep2, sleep3, sleep4, sleep5, sleep6, sleep7, sleep8, sleep9, sleep10, sleep11, sleep12, sleep13, sleep14, sleep15, sleep16, sleep17, sleep18,
					sleep19, sleep20, sleep21, sleep22, sleep23, sleep24 };

			// new
			sleepData = new SleepData(sleeps, historyDate);

		} else {
			logE("错误的数据");
		}
		return sleepData;
	}

	private void logV(String msg) {
		Log.v(TAG, "**解析数据 ：  " + msg + "  **");
	}

	private void logE(String msg) {
		Log.e(TAG, "**解析数据 ：  " + msg + "  **");
	};

	private String getHistoryDate(Date today, int diff) {
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DAY_OF_MONTH, -diff);
		return DateUtil.dateToString(c.getTime(), "yyyyMMhh");
	}

	@Override
	public StepData notifyForHistoryDataToDistanceData(byte[] data) {
		StepData stepData = null;
		String dataStr = DataUtil.byteToHexString(data);
		if (getTypeFromData(data) == NOTIFY_HISTORY_DISTACE) {
			logV("历史数据 距离");
			String dateStr = dataStr.substring(4, 6);
			String distanceVale = dataStr.substring(6, 14);
			int date = Integer.parseInt(dateStr, 16);
			Date today = new Date();
			String historyDate = getHistoryDate(today, date + 1);
			int distance = Integer.parseInt(distanceVale, 16);
			stepData = new StepData();
			stepData.setDistance(distance);
			stepData.setDate(historyDate);
		} else {
			logE("错误的数据");
		}
		return stepData;
	}

	@Override
	public StepData notifyForHistoryDataToCalData(byte[] data) {
		StepData stepData = null;
		String dataStr = DataUtil.byteToHexString(data);
		if (getTypeFromData(data) == NOTIFY_HISTORY_CAL) {
			logV("历史数据 卡洛里");
			String dateStr = dataStr.substring(4, 6);
			String calVale = dataStr.substring(6, 14);
			int date = Integer.parseInt(dateStr, 16);
			Date today = new Date();
			String historyDate = getHistoryDate(today, date + 1);
			int cal = Integer.parseInt(calVale, 16);
			stepData = new StepData(cal, historyDate);
		} else {
			logE("错误的数据");
		}
		return stepData;
	}

	@Override
	public StepData notifyForHistoryDataToSportTimeData(byte[] data) {
		StepData stepData = null;
		String dataStr = DataUtil.byteToHexString(data);
		if (getTypeFromData(data) == NOTIFY_HISTORY_SPORT_TIME) {
			logV("历史数据  运动时间");
			String dateStr = dataStr.substring(4, 6);
			int date = Integer.parseInt(dateStr, 16);
			Date today = new Date();
			String historyDate = getHistoryDate(today, date + 1);

			String hourStr = dataStr.substring(6, 8);
			String minStr = dataStr.substring(8, 10);
			String secondStr = dataStr.substring(10, 12);
			int hour = Integer.parseInt(hourStr, 16);
			int min = Integer.parseInt(minStr, 16);
			int second = Integer.parseInt(secondStr, 16);

			stepData = new StepData(historyDate, hour, min, second);
		} else {
			logE("错误的数据");
		}
		return stepData;
	}

	@Override
	public SleepData notifyForHistoryDataToTodaySleepData(byte[] data) {
		SleepData sleepData = null;
		String dataStr = DataUtil.byteToHexString(data);
		if (getTypeFromData(data) == NOTIFY_HISTORY_SLEEP_FOR_TODAY) {
			logV("历史数据 今天的睡眠");
			// 获取历史日期
			// String dateStr = dataStr.substring(4, 6);
			// int date = Integer.parseInt(dateStr, 16);
			Date today = new Date();
			String historyDate = DateUtil.dateToString(today, DATE_FORMAT);
			// 获取每天时间段的值
			String sleepVale1 = dataStr.substring(6, 7);
			String sleepVale2 = dataStr.substring(7, 8);
			String sleepVale3 = dataStr.substring(8, 9);
			String sleepVale4 = dataStr.substring(9, 10);
			String sleepVale5 = dataStr.substring(10, 11);
			String sleepVale6 = dataStr.substring(11, 12);
			String sleepVale7 = dataStr.substring(12, 13);
			String sleepVale8 = dataStr.substring(13, 14);
			String sleepVale9 = dataStr.substring(14, 15);
			String sleepVale10 = dataStr.substring(15, 16);
			String sleepVale11 = dataStr.substring(16, 17);
			String sleepVale12 = dataStr.substring(17, 18);
			String sleepVale13 = dataStr.substring(18, 19);
			String sleepVale14 = dataStr.substring(19, 20);
			String sleepVale15 = dataStr.substring(20, 21);
			String sleepVale16 = dataStr.substring(21, 22);
			String sleepVale17 = dataStr.substring(22, 23);
			String sleepVale18 = dataStr.substring(23, 24);
			String sleepVale19 = dataStr.substring(24, 25);
			String sleepVale20 = dataStr.substring(25, 26);
			String sleepVale21 = dataStr.substring(26, 27);
			String sleepVale22 = dataStr.substring(27, 28);
			String sleepVale23 = dataStr.substring(28, 29);
			String sleepVale24 = dataStr.substring(29, 30);
			int sleep1 = Integer.parseInt(sleepVale1, 16);
			int sleep2 = Integer.parseInt(sleepVale2, 16);
			int sleep3 = Integer.parseInt(sleepVale3, 16);
			int sleep4 = Integer.parseInt(sleepVale4, 16);
			int sleep5 = Integer.parseInt(sleepVale5, 16);
			int sleep6 = Integer.parseInt(sleepVale6, 16);
			int sleep7 = Integer.parseInt(sleepVale7, 16);
			int sleep8 = Integer.parseInt(sleepVale8, 16);
			int sleep9 = Integer.parseInt(sleepVale9, 16);
			int sleep10 = Integer.parseInt(sleepVale10, 16);
			int sleep11 = Integer.parseInt(sleepVale11, 16);
			int sleep12 = Integer.parseInt(sleepVale12, 16);
			int sleep13 = Integer.parseInt(sleepVale13, 16);
			int sleep14 = Integer.parseInt(sleepVale14, 16);
			int sleep15 = Integer.parseInt(sleepVale15, 16);
			int sleep16 = Integer.parseInt(sleepVale16, 16);
			int sleep17 = Integer.parseInt(sleepVale17, 16);
			int sleep18 = Integer.parseInt(sleepVale18, 16);
			int sleep19 = Integer.parseInt(sleepVale19, 16);
			int sleep20 = Integer.parseInt(sleepVale20, 16);
			int sleep21 = Integer.parseInt(sleepVale21, 16);
			int sleep22 = Integer.parseInt(sleepVale22, 16);
			int sleep23 = Integer.parseInt(sleepVale23, 16);
			int sleep24 = Integer.parseInt(sleepVale24, 16);

			int[] sleeps = new int[] { sleep1, sleep2, sleep3, sleep4, sleep5, sleep6, sleep7, sleep8, sleep9, sleep10, sleep11, sleep12, sleep13, sleep14, sleep15, sleep16, sleep17, sleep18,
					sleep19, sleep20, sleep21, sleep22, sleep23, sleep24 };

			// new
			sleepData = new SleepData(sleeps, historyDate);
		} else {
			logE("错误的数据");
		}
		return sleepData;
	}
}
