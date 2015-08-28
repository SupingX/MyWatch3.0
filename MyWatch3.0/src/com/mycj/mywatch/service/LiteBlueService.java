package com.mycj.mywatch.service;

import java.util.Date;

import android.content.Intent;
import android.content.IntentFilter;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mycj.mywatch.BaseApp;
import com.mycj.mywatch.R;
import com.mycj.mywatch.activity.CameraActivity;
import com.mycj.mywatch.bean.HeartRateData;
import com.mycj.mywatch.bean.SleepData;
import com.mycj.mywatch.bean.StepData;
import com.mycj.mywatch.business.ProtocolForNotify;
import com.mycj.mywatch.business.ProtocolForWrite;

public class LiteBlueService extends AbstractLiteBlueService {

	public final static String LITE_DATA_STEP = "lite_data_step";
	public final static String LITE_DATA_SYNC_TIME = "lite_data_sync_time";
	public final static String LITE_DATA_CAMERA = "lite_data_camera";
	public final static String LITE_DATA_MUSIC = "lite_data_music";
	public final static String LITE_DATA_REMIND = "lite_data_remind";
	public final static String LITE_DATA_HEART_RATE = "lite_data_heart_rate";
	public final static String LITE_DATA_HISTORY_HEART_RATE = "lite_data_history_heart_rate";
	public final static String LITE_DATA_HISTORY_STEP = "lite_data_history_step";
	public final static String LITE_DATA_HISTORY_SLEEP = "lite_data_history_sleep";
	public final static String LITE_DATA_HISTORY_DISTANCE = "lite_data_history_distance";
	public final static String LITE_DATA_HISTORY_CAL = "lite_data_history_cal";
	public final static String LITE_DATA_HISTORY_SPORT_TIME = "lite_data_history_sport_time";
	public final static String LITE_DATA_HISTORY_SLEEP_FOR_TODAY = "lite_data_history_sleep_for_today";

	public final static String EXTRA_STEP = "extra_step";
	public final static String EXTRA_SLEEP = "extra_sleep";
	public final static String EXTRA_HEART_RATE = "EXTRA_HEART_RATE";
	public final static String EXTRA_CAMERA = "EXTRA_CAMERA";

	public static IntentFilter getIntentFilter() {
		IntentFilter intentFilter = AbstractLiteBlueService.getIntentFilter();
		intentFilter.addAction(LITE_DATA_STEP);
		intentFilter.addAction(LITE_DATA_SYNC_TIME);
		intentFilter.addAction(LITE_DATA_CAMERA);
		intentFilter.addAction(LITE_DATA_MUSIC);
		intentFilter.addAction(LITE_DATA_REMIND);
		intentFilter.addAction(LITE_DATA_HEART_RATE);
		intentFilter.addAction(LITE_DATA_HISTORY_HEART_RATE);
		intentFilter.addAction(LITE_DATA_HISTORY_STEP);
		intentFilter.addAction(LITE_DATA_HISTORY_SLEEP);
		intentFilter.addAction(LITE_DATA_HISTORY_DISTANCE);
		intentFilter.addAction(LITE_DATA_HISTORY_CAL);
		intentFilter.addAction(LITE_DATA_HISTORY_SPORT_TIME);
		intentFilter.addAction(LITE_DATA_HISTORY_SLEEP_FOR_TODAY);
		return intentFilter;
	}

	@Override
	public void parseData(byte[] data) {
		ProtocolForNotify notify = ProtocolForNotify.instance();
		int type = notify.getTypeFromData(data);
		switch (type) {
		case ProtocolForNotify.NOTIFY_REMIND:
			// 防丢
			BaseApp app = (BaseApp) getApplication();
			musicService = app.getMusicService();
			int notifyForRemind = notify.notifyForRemind(data);
			if (notifyForRemind == 0x01) {
				musicService.play(R.raw.a1);
				// showSystemDialog("查找手机","查找手机中","关闭");
				startDialogActivity("查找手机", "查找手机中", "关闭");
			} else if (notifyForRemind == 0x02) {
				if (musicService != null && musicService.isPlaying()) {
					musicService.stop();
					musicService.release();
				}
			}

			break;
		case ProtocolForNotify.NOTIFY_SYNC_TIME:
			// 请求时间同步
			boolean notifyForSyncTime = notify.notifyForSyncTime(data);
			if (notifyForSyncTime) {
				writeCharacticsUseConnectListener(ProtocolForWrite.instance().getByteForSyncTime(new Date()));
			}

			break;
		case ProtocolForNotify.NOTIFY_CAMERA:
			// kai
			Intent it = new Intent(this, CameraActivity.class);
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(it);
			// 拍照
			int notifyForCamera = notify.notifyForCamera(data);
			bleTakeCamera(notifyForCamera);

			// if (notifyForCamera==0x00) {
			//
			// }else if (notifyForCamera ==0x01) {
			// //guan
			// bleStopCamera();
			//
			// }
			break;
		case ProtocolForNotify.NOTIFY_MUSIC:

			// 音乐播放控制
			int notifyForMusic = notify.notifyForMusic(data);
			switch (notifyForMusic) {
			case 0x0108:// 播放
				musicService.playByCurrentPlayingPosition(musicService.getPlayingPosition());
				break;
			case 0x0109:// 停止
				musicService.stop();
				break;
			case 0x0200:// 上一曲
				musicService.playDown();
				break;
			case 0x0300:// 下一曲
				musicService.playUp();
				break;
			case 0x0500:// 音量+

				break;
			case 0x0600:// 音量-

				break;

			default:
				break;
			}

			break;
		case ProtocolForNotify.NOTIFY_STEP:
			// 计步器
			StepData stepData = notify.notifyForStepData(data);
			if (stepData != null) {
				int[] datas = new int[] { stepData.getStep(), stepData.getCal(), stepData.getDistance(), stepData.getHour(), stepData.getMinute(), stepData.getSecond() };
				bleDataForStep(datas);
			}
			break;
		case ProtocolForNotify.NOTIFY_HEART_RATE:
			HeartRateData hrData = notify.notifyForHeartRateData(data);
			if (hrData != null) {
				bleDataForHeartRate(hrData.getHr());
			}

			// 心率
			break;
		case ProtocolForNotify.NOTIFY_HISTORY_HEART_RATE:
			// 历史数据 -- 心率

			HeartRateData HeartRateData = notify.notifyForHistoryDataToHearRateData(data);
			if (HeartRateData!=null) {
				HeartRateData.save();
			}

			break;
		case ProtocolForNotify.NOTIFY_HISTORY_STEP:
			// 历史数据 -- 计步
			StepData notifyForHistoryDataToStepData = notify.notifyForHistoryDataToStepData(data);
			if (notifyForHistoryDataToStepData!=null) {
				notifyForHistoryDataToStepData.save();
			}
			break;
		case ProtocolForNotify.NOTIFY_HISTORY_SLEEP:
			// 历史数据 -- 睡眠
			SleepData notifyForHistoryDataToSleepData = notify.notifyForHistoryDataToSleepData(data);
			if (notifyForHistoryDataToSleepData!=null) {
				notifyForHistoryDataToSleepData.save();
			}
			break;
		case ProtocolForNotify.NOTIFY_HISTORY_DISTACE:
			// 历史数据 -- 距离
			StepData notifyForHistoryDataToDistanceData = notify.notifyForHistoryDataToDistanceData(data);
			if (notifyForHistoryDataToDistanceData!=null) {
				notifyForHistoryDataToDistanceData.save();
			}
			break;
		case ProtocolForNotify.NOTIFY_HISTORY_CAL:
			// 历史数据 -- 卡洛里
			StepData notifyForHistoryDataToCalData = notify.notifyForHistoryDataToCalData(data);
			if (notifyForHistoryDataToCalData!=null) {
				notifyForHistoryDataToCalData.save();
			}
			break;
		case ProtocolForNotify.NOTIFY_HISTORY_SPORT_TIME:
			// 历史数据 -- 运动时间
			StepData notifyForHistoryDataToSportTimeData = notify.notifyForHistoryDataToSportTimeData(data);
			if (notifyForHistoryDataToSportTimeData!=null) {
				notifyForHistoryDataToSportTimeData.save();
			}
			break;
		case ProtocolForNotify.NOTIFY_HISTORY_SLEEP_FOR_TODAY:
			// 历史数据 -- 今天睡眠数据
			SleepData todaySleepData = notify.notifyForHistoryDataToTodaySleepData(data);
			if (todaySleepData != null) {
				bleDataForSleep(todaySleepData.getSleeps());
			}
			break;

		default:
			break;
		}
	}



	private void bleTakeCamera(int notifyForCamera) {
		Intent intent = new Intent(LITE_DATA_CAMERA);
		intent.putExtra(EXTRA_CAMERA, notifyForCamera);
		sendBroadcast(intent);
	}

	private void bleDataForHeartRate(int hr) {
		Intent intent = new Intent(LITE_DATA_HEART_RATE);
		intent.putExtra(EXTRA_HEART_RATE, hr);
		sendBroadcast(intent);
	}

	private void bleDataForSleep(int[] sleeps) {
		Intent intent = new Intent(LITE_DATA_HISTORY_SLEEP_FOR_TODAY);
		intent.putExtra(EXTRA_SLEEP, sleeps);
		sendBroadcast(intent);

	}

	private void bleDataForStep(int[] datas) {
		Intent intent = new Intent(LITE_DATA_STEP);
		intent.putExtra(EXTRA_STEP, datas);
		sendBroadcast(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		BLE_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
		BLE_CHARACTERISTIC_NOTIFY = "0000fff1-0000-1000-8000-00805f9b34fb";
		BLE_CHARACTERISTIC_WRITE = "0000fff2-0000-1000-8000-00805f9b34fb";

	}

	public int getToalSleep(int[] sleeps) {
		int total = 0;
		for (int i = 0; i < sleeps.length; i++) {

			total += getSleepValue(sleeps[i]);
		}
		return total;
	}

	private int getSleepValue(int sleepValue) {
		float value = 0f;
		switch (sleepValue) {
		// case 0:
		// rectPaint.setColor(colorAwake);
		// result = 1f;
		// break;
		// case 1:
		// rectPaint.setColor(colorAwake);
		// total += 0.25f;// 获得总的睡眠时间
		// result = 1f;
		// break;
		// case 2:
		// total += 0.75f;// 获得总的睡眠时间
		// rectPaint.setColor(colorLight);
		// result = 2 / 3f;
		// break;
		// case 3:
		// result = 2 / 3f;
		// total += 1f;// 获得总的睡眠时间
		// rectPaint.setColor(colorLight);
		// break;
		// case 4:
		// total += 1f;// 获得总的睡眠时间
		// result = 1 / 3f;
		// rectPaint.setColor(colorDeep);
		// break;
		// case 5:
		// total += 1f;// 获得总的睡眠时间
		// result = 1 / 3f;
		// rectPaint.setColor(colorDeep);
		// break;
		default:
			break;
		}
		return 0;
	}

}
