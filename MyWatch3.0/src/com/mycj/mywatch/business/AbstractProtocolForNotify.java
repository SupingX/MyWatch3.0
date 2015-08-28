package com.mycj.mywatch.business;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mycj.mywatch.bean.HeartRateData;
import com.mycj.mywatch.bean.SleepData;
import com.mycj.mywatch.bean.StepData;

public abstract class AbstractProtocolForNotify {
	/**
	 * 防丢
	 * 0xF3
	 * @param data
	 * @return 手环呼叫 App 开始:01;手环呼叫 App 停止:02;
	 */
	public abstract int notifyForRemind(byte []data);
	/**
	 * 请求时间同步
	 * 0xf4
	 * @param data
	 */
	public abstract boolean notifyForSyncTime(byte []data);
	/**
	 * 拍照
	 * 0xf5
	 * @param data
	 */
	public abstract int notifyForCamera(byte []data);
	/**
	 * 音乐播放控制
	 * 0xf6
	 * @param data
	 */
	public abstract int notifyForMusic(byte []data);
	/**
	 * 计步器
	 * 0xF7
	 * @param data
	 * @return
	 */
	public abstract StepData notifyForStepData(byte []data);
	/**
	 * 心率
	 * 0xF9
	 * @param data
	 * @return
	 */
	public abstract HeartRateData notifyForHeartRateData(byte []data);
	
	/**
	 * 历史心率数据
	 * 0xFE
	 * @param data
	 * @return Map<Integer,HeartRateData>  key ：当前日期  value ：心率数据 
	 */
	public abstract HeartRateData notifyForHistoryDataToHearRateData(byte []data);
	/**
	 * 历史计步数据
	 * @param data
	 * @return
	 */
	public abstract	StepData notifyForHistoryDataToStepData(byte []data);
	/**
	 * 历史距离数据
	 * @param data
	 * @return
	 */
	public abstract	StepData notifyForHistoryDataToDistanceData(byte []data);
	/**
	 * 历史卡洛里数据
	 * @param data
	 * @return
	 */
	public abstract	StepData notifyForHistoryDataToCalData(byte []data);
	/**
	 * 历史运动时间数据
	 * @param data
	 * @return
	 */
	public abstract	StepData notifyForHistoryDataToSportTimeData(byte []data);
	/**
	 * 历史睡眠数据
	 * @param data
	 * @return
	 */
	public abstract SleepData notifyForHistoryDataToSleepData(byte []data);
	/**
	 * 当天睡眠数据
	 * @param data
	 * @return
	 */
	public abstract SleepData notifyForHistoryDataToTodaySleepData(byte []data);
}
