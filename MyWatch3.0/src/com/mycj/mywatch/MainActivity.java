package com.mycj.mywatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import com.mycj.mywatch.activity.CameraActivity;
import com.mycj.mywatch.activity.ClockActivity;
import com.mycj.mywatch.activity.DeviceActivity;
import com.mycj.mywatch.activity.HeartRateActivity;
import com.mycj.mywatch.activity.MoreActivity;
import com.mycj.mywatch.activity.PedometerActivity;
import com.mycj.mywatch.activity.SleepActivity;
import com.mycj.mywatch.activity.WeatherActivity;
import com.mycj.mywatch.bean.ConditionWeather;
import com.mycj.mywatch.bean.Constant;
import com.mycj.mywatch.bean.Forecast;
import com.mycj.mywatch.business.AbstractProtocolForWrite;
import com.mycj.mywatch.business.LoadWeatherJsonTask;
import com.mycj.mywatch.business.LoadWeatherJsonTask.OnProgressChangeListener;
import com.mycj.mywatch.business.ProtocolForNotify;
import com.mycj.mywatch.business.ProtocolForWrite;
import com.mycj.mywatch.service.AbstractLiteBlueService;
import com.mycj.mywatch.service.LiteBlueService;
import com.mycj.mywatch.service.SimpleBlueService;
import com.mycj.mywatch.util.DataUtil;
import com.mycj.mywatch.util.SharedPreferenceUtil;
import com.mycj.mywatch.util.YahooUtil;
import com.mycj.mywatch.view.ActionSheetDialog;
import com.mycj.mywatch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.mycj.mywatch.view.ActionSheetDialog.SheetItemColor;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnClickListener, OnProgressChangeListener {

	private ImageView imgPedo;
	private ImageView imgHeartRate;
	private ImageView imgClock;
	private ImageView imgCamera;
	private ImageView imgSleep;
	private ImageView imgDevice;
	private ImageView imgWeather;
	private ImageView imgMore;
	private FrameLayout frPedo;
	private FrameLayout frHeartRate;
	private FrameLayout frClock;
	private FrameLayout frCamera;
	private FrameLayout frSleep;
	private FrameLayout frDevice;
	private FrameLayout frWeather;
	private FrameLayout frMore;
	private boolean isLocked;
	private TextView tvWeatherText;
	private TextView tvWeatherTemp;
	private TextView tvWeatherAddress;
	private final static int MSG_PLACE = 0x100004;
	private final static int MSG_FORECAST = 0x10005;
	private final static int MSG_HEART_RATE = 0x10006;
	private final static int MSG_RSSI = 0x10007;
	private final static int MSG_RUN = 0x10008;
	private TextView tvClock;
	private TextView tvClockOnOff;
	private LiteBlueService mLiteBlueService;
	// private SimpleBlueService mSimpleBlueService;
	private int step;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(AbstractLiteBlueService.LITE_GATT_DEVICE_FOUND)) {
			} else if (action.equals(AbstractLiteBlueService.LITE_GATT_CONNECTED)) {

			} else if (action.equals(AbstractLiteBlueService.LITE_GATT_SERVICE_DISCOVERED)) {
				mHandler.removeCallbacks(runRssi);
				mHandler.post(runRssi);
			} else if (action.equals(LiteBlueService.LITE_DATA_HISTORY_SLEEP_FOR_TODAY)) {
				int[] sleeps = intent.getIntArrayExtra(LiteBlueService.EXTRA_SLEEP);
				int total = 0;
				for (int i = 0; i < sleeps.length; i++) {
					total += sleeps[i];
				}

			} else if (action.equals(LiteBlueService.LITE_DATA_STEP)) {
				int[] datas = intent.getIntArrayExtra(LiteBlueService.EXTRA_STEP);
				if (datas != null) {
					step = datas[0];
				}
				runOnUiThread(new Runnable() {
					public void run() {
						tvStep.setText("" + step);
					}
				});
			} else if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_RSSI_CHANGED)) {
				int rssi = intent.getExtras().getInt(LiteBlueService.EXTRA_RSSI);
				Message msg = mHandler.obtainMessage();
				msg.arg1 = rssi;
				msg.what = MSG_RSSI;
				mHandler.sendMessage(msg);
			} else if (action.equals(LiteBlueService.LITE_DATA_HEART_RATE)) {
				int hr = intent.getExtras().getInt(LiteBlueService.EXTRA_HEART_RATE);
				Message msg = mHandler.obtainMessage();
				msg.arg1 = hr;
				msg.what = MSG_HEART_RATE;
				mHandler.sendMessage(msg);
			}
			
			
		}
	};
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_RUN:
				mHandler.removeCallbacks(runRssi);
				mHandler.postDelayed(runRssi, 3000);
				break;
			case MSG_RSSI:
				tvRssi.setText(String.valueOf(msg.arg1));
				break;
			case MSG_FORECAST:
				Log.v("", "<!-- handler处理  之" + MSG_FORECAST);
				String json = msg.getData().getString("json");
				parseForecastJsonAndUpdateView(json);
				break;
			case MSG_HEART_RATE:
				tvHeartRate.setText(String.valueOf(msg.arg1));
				break;
			default:
				break;
			}
		}
	};
	private TextView tvStep;;
	private Runnable runRssi;
	private TextView tvRssi;
	private TextView tvHeartRate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLiteBlueService = getLiteBlueService();
		// mSimpleBlueService = getSimpleBlueService();
		initViews();
		setListener();
		registerReceiver(mReceiver, LiteBlueService.getIntentFilter());
		firstEnter();
		runRssi = new Runnable() {
			@Override
			public void run() {
				Log.v("", "______请求rssi_______");
				mLiteBlueService.readRemoteRssi();
				mHandler.sendEmptyMessage(MSG_RUN);
			}
		};

	}

	@Override
	protected void onResume() {
		// if
		// (mSimpleBlueService.getConnectState()==BluetoothProfile.STATE_CONNECTED)
		// {
		if (mLiteBlueService != null) {

			if (mLiteBlueService.isBinded() && mLiteBlueService.isServiceDiscovered()) {
				mHandler.post(runRssi);
			}
		}
		setClockTime();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		mHandler.removeCallbacks(runRssi);
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		mHandler.removeCallbacks(runRssi);
		super.onDestroy();
	}

	public void initViews() {
		imgPedo = (ImageView) findViewById(R.id.img_main_pedo);
		imgHeartRate = (ImageView) findViewById(R.id.img_main_hr);
		imgClock = (ImageView) findViewById(R.id.img_main_clock);
		imgCamera = (ImageView) findViewById(R.id.img_main_camera);
		imgSleep = (ImageView) findViewById(R.id.img_main_sleep);
		imgDevice = (ImageView) findViewById(R.id.img_main_device);
		imgWeather = (ImageView) findViewById(R.id.img_main_weather);
		imgMore = (ImageView) findViewById(R.id.img_main_more);
		frPedo = (FrameLayout) findViewById(R.id.fr_pedo);
		frHeartRate = (FrameLayout) findViewById(R.id.fr_hr);
		frClock = (FrameLayout) findViewById(R.id.fr_clock);
		frCamera = (FrameLayout) findViewById(R.id.fr_camera);
		frSleep = (FrameLayout) findViewById(R.id.fr_sleep);
		frDevice = (FrameLayout) findViewById(R.id.fr_device);
		frWeather = (FrameLayout) findViewById(R.id.fr_weather);
		frMore = (FrameLayout) findViewById(R.id.fr_more);
		// 计步
		tvStep = (TextView) findViewById(R.id.tv_main_step);
		// 天气
		tvWeatherText = (TextView) findViewById(R.id.tv_weather_text);
		tvWeatherTemp = (TextView) findViewById(R.id.tv_weather_temp);
		tvWeatherAddress = (TextView) findViewById(R.id.tv_weather_address);
		// 闹钟
		tvClock = (TextView) findViewById(R.id.tv_main_clock);
		tvClockOnOff = (TextView) findViewById(R.id.tv_main_clock_on_off);
		//心率
		tvHeartRate = (TextView) findViewById(R.id.tv_main_hr);
		// 蓝牙
		tvRssi = (TextView) findViewById(R.id.tv_main_rssi);
		

	}

	@Override
	public void setListener() {
		imgPedo.setOnClickListener(this);
		imgHeartRate.setOnClickListener(this);
		imgClock.setOnClickListener(this);
		imgCamera.setOnClickListener(this);
		imgSleep.setOnClickListener(this);
		imgDevice.setOnClickListener(this);
		imgWeather.setOnClickListener(this);
		imgMore.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_main_pedo:
			// ProtocolForWrite.instance().getByteForAlarmClock(23, 23);
			// ProtocolForWrite.instance().getByteForAvoidLose(1);
			// ProtocolForWrite.instance().getByteForCamera(1);
			// ProtocolForWrite.instance().getByteForHeartRate(23, 12);
			// ProtocolForWrite.instance().getByteForMissedCallAndMessage(1, 3);
			// ProtocolForWrite.instance().getByteForRemind(2, "13047618057");
			// ProtocolForWrite.instance().getByteForShutDown();
			// ProtocolForWrite.instance().getByteForSleep(20);
			// ProtocolForWrite.instance().getByteForSleepQualityOfToday(66);
			// ProtocolForWrite.instance().getByteForSleepTime(23, 1);
			// ProtocolForWrite.instance().getByteForStep(2);
			// ProtocolForWrite.instance().getByteForSyncHistoryData();
			// ProtocolForWrite.instance().getByteForSyncTime(new Date());
			// ProtocolForWrite.instance().getByteForWeather(1, 2, 67);
			// byte [] data = DataUtil.hexStringToByte("F60122");
			// ProtocolForNotify.instance().notifyForMusic(data);
			// ProtocolForNotify.instance().notifyForStepData(DataUtil.hexStringToByte("F7223344556677889900112233445566"));
			Intent iPedo = new Intent(this, PedometerActivity.class);
			iPedo.putExtra("step", step);
			enter(frPedo, iPedo);
			break;
		case R.id.img_main_hr:
			Intent iHr = new Intent(this, HeartRateActivity.class);
			enter(frHeartRate, iHr);
			break;
		case R.id.img_main_clock:
			Intent iClock = new Intent(this, ClockActivity.class);
			enter(frClock, iClock);
			break;
		case R.id.img_main_camera:
			Intent iCamera = new Intent(this, CameraActivity.class);
			enter(frCamera, iCamera);
			break;
		case R.id.img_main_sleep:
			Intent iSleep = new Intent(this, SleepActivity.class);
			enter(frSleep, iSleep);
			break;
		case R.id.img_main_device:
			Intent iDevice = new Intent(this, DeviceActivity.class);
			enter(frDevice, iDevice);
			break;
		case R.id.img_main_weather:
			Intent iWeather = new Intent(this, WeatherActivity.class);
			enter(frWeather, iWeather);
			break;
		case R.id.img_main_more:
			Intent iMore = new Intent(this, MoreActivity.class);
			enter(frMore, iMore);
			
			
			break;

		default:
			break;
		}
	}
	

	@Override
	public void onPreExecute(int id) {
	}

	@Override
	public void onPostExecute(int id) {
	}

	@Override
	public void onError(int id) {
		showShortToast("加载异常，请检查网络设置");
		Log.v("", "加载异常，请检查网络设置");
		loadLastWeather();
	}

	/**
	 * 解析天气并更新视图
	 * 
	 * @param json
	 */
	private void parseForecastJsonAndUpdateView(String json) {
		if (json == null) {
			return;
		}
		if (json.trim().equals("")) {
			return;
		}
		try {
			ConditionWeather weather = YahooUtil.parseConditionWeatherFromJson(json);
			tvWeatherText.setText(weather.getText());
			tvWeatherTemp.setText(weather.getTemp() + "度");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void firstEnter() {

		if (mLiteBlueService != null) {

			// 确认蓝牙
			if (!mLiteBlueService.isEnable()) {
				showIosDialog();
			} else {
				if (mLiteBlueService.isBinded()) {// 当本地绑定时，开启搜索。
					mLiteBlueService.startScanUsePeriodScanCallback();
				}
			}
		}

		// 加载天气
		LoadWeatherJsonTask taskFirst = new LoadWeatherJsonTask(MSG_FORECAST, this, mHandler);
		taskFirst.setOnProgressChangeListener(this);
		String city = (String) SharedPreferenceUtil.get(this, Constant.SHARE_PLACE, "beijing");
		if (!city.equals("")) {
			taskFirst.execute(YahooUtil.getForecastUrl(city));
			tvWeatherAddress.setText(city);
		} else {

		}

	}

	private void enter(View img, Intent intent) {
		startAnimation(img, intent);
	}

	/**
	 * 记载上次信息
	 */
	private void loadLastWeather() {
		String lastJson = (String) SharedPreferenceUtil.get(this, Constant.SHARE_JSON_FORECAST, "");
		parseForecastJsonAndUpdateView(lastJson);
	}

	private void setClockTime() {
		int hour = (int) SharedPreferenceUtil.get(this, Constant.SHARE_CLOCK_HOUR, 0);
		int min = (int) SharedPreferenceUtil.get(this, Constant.SHARE_CLOCK_MIN, 0);
		boolean isOpen = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_CHECK_BOX_CLOCK, false);
		tvClock.setText(parseText(hour) + " : " + parseText(min));
		tvClockOnOff.setText(isOpen ? "开" : "关");
	}

	private void showIosDialog() {
		ActionSheetDialog dialog = new ActionSheetDialog(this).builder();
		dialog.setTitle("打开蓝牙？");
		dialog.addSheetItem("打开", SheetItemColor.Red, new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (mLiteBlueService.isEnable()) {
							// 蓝牙打开
							if (mLiteBlueService.isBinded()) {// 当本地绑定时，开启搜索。
								mLiteBlueService.startScanUsePeriodScanCallback();
							}
						} else {
							// 未打开
						}
					}
				}, 5000);
				mLiteBlueService.enable();
			}
		}).show();
	}

	private void startAnimation(View img, final Intent intent) {
		// ObjectAnimator oaTranslationX = ObjectAnimator.ofFloat(img,
		// "translationX", 0, mScreenWidth / 2);
		// ObjectAnimator oaTranslationY = ObjectAnimator.ofFloat(img,
		// "translationY", 0, mScreenHeight / 2);
		ObjectAnimator oaScaleX = ObjectAnimator.ofFloat(img, "scaleX", 0.5f, 1.2f, 1f);
		ObjectAnimator oaScaleY = ObjectAnimator.ofFloat(img, "scaleY", 0.5f, 1.2f, 1f);

		// oaTranslationX.setDuration(1000);
		// oaTranslationY.setDuration(1000);
		// oaScaleX.setDuration(1000);
		// oaScaleY.setDuration(1000);
		AnimatorSet set = new AnimatorSet();
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				isLocked = true;
				super.onAnimationStart(animation);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				Log.e("", "动画完成");
				isLocked = false;

				startActivity(intent);
			}
		});
		set.play(oaScaleX).with(oaScaleY);
		set.setDuration(500);
		if (!isLocked) {
			set.start();
		}

	}

}
