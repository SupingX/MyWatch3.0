package com.mycj.mywatch.activity;

import com.mycj.mywatch.BaseActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.R.layout;
import com.mycj.mywatch.adapter.NumberWheelAdapter;
import com.mycj.mywatch.bean.Constant;
import com.mycj.mywatch.business.ProtocolForWrite;
import com.mycj.mywatch.service.LiteBlueService;
import com.mycj.mywatch.util.DisplayUtil;
import com.mycj.mywatch.util.SharedPreferenceUtil;
import com.mycj.mywatch.view.OnWheelChangedListener;
import com.mycj.mywatch.view.OnWheelScrollListener;
import com.mycj.mywatch.view.WheelAdapter;
import com.mycj.mywatch.view.WheelView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class ClockActivity extends BaseActivity implements OnClickListener {

	private RelativeLayout frClock;
	private View timepickerview1;
	private CheckBox cbClock;
	private TextView tvSave;
	private boolean isChecked;
	private WheelView hourWV;
	private WheelView minWV;
	private LiteBlueService mLiteBlueService;
	private TextView tvClock;
	private FrameLayout frHome;
	private int hour;
	private int min;
	private Handler mHandler = new Handler() {

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clock);
		mLiteBlueService = getLiteBlueService();
		initViews();
		setListener();

	}

	@Override
	protected void onResume() {
		setClockTime();
		super.onResume();
	}

	@Override
	public void initViews() {
		frClock = (RelativeLayout) findViewById(R.id.rl_clock);
		tvSave = (TextView) findViewById(R.id.tv_save);
		tvClock = (TextView) findViewById(R.id.tv_clock);
		cbClock = (CheckBox) findViewById(R.id.cb_clock);
		frHome = (FrameLayout) findViewById(R.id.fl_home);
		hour = (int) SharedPreferenceUtil.get(this, Constant.SHARE_CLOCK_HOUR, 0);
		min = (int) SharedPreferenceUtil.get(this, Constant.SHARE_CLOCK_MIN, 0);
		initNumberPicker();
	}

	private void initNumberPicker() {
		LayoutInflater inflater1 = LayoutInflater.from(ClockActivity.this);
		timepickerview1 = inflater1.inflate(R.layout.timepicker, null);
		// 设置时间
		hourWV = (WheelView) timepickerview1.findViewById(R.id.hour);
		hourWV.setAdapter(new NumberWheelAdapter(0, 23));
		hourWV.setCyclic(true);// 可循环滚动
		hourWV.setLabel("时");// 文字
		hourWV.setCurrentItem(hour);
		// 设置分钟
		minWV = (WheelView) timepickerview1.findViewById(R.id.min);
		minWV.setAdapter(new NumberWheelAdapter(0, 59));
		minWV.setCyclic(true);// 可循环滚动
		minWV.setLabel("分");
		minWV.setCurrentItem(min);
		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		int textSize = 0;
		int screenheight = DisplayUtil.getScreenMetrics(this).y;
		textSize = (screenheight / 100) * 3;
		hourWV.TEXT_SIZE = textSize;
		minWV.TEXT_SIZE = textSize;
		//
		OnWheelChangedListener hourWheelListener;
		OnWheelScrollListener hourOnWheelScrollListener;

		// 动态添加
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		param.addRule(RelativeLayout.CENTER_IN_PARENT);
		timepickerview1.setLayoutParams(param);
		frClock.addView(timepickerview1);
	}

	@Override
	public void setListener() {
		frClock.setOnClickListener(this);
		tvSave.setOnClickListener(this);
		cbClock.setOnClickListener(this);
		frHome.setOnClickListener(this);

	}

	private void setClockTime() {

		isChecked = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_CHECK_BOX_CLOCK, false);
		Log.v("", "isChecked : " + isChecked);
		cbClock.setChecked(isChecked);
		tvClock.setText(parseText(hour) + " : " + parseText(min));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_home:
			finish();
			break;
		case R.id.cb_clock:
			isChecked = !isChecked;
			cbClock.setChecked(isChecked);
			Log.v("", "isChecked : " + isChecked);
			break;
		case R.id.tv_save:

			int hour = hourWV.getCurrentItem();
			int min = minWV.getCurrentItem();
			Log.v("", "hour : min - - - >" + hour + " : " + min);
			SharedPreferenceUtil.put(this, Constant.SHARE_CLOCK_HOUR, hour);
			SharedPreferenceUtil.put(this, Constant.SHARE_CLOCK_MIN, min);
			SharedPreferenceUtil.put(this, Constant.SHARE_CHECK_BOX_CLOCK, isChecked);

			if (mLiteBlueService.isServiceDiscovered()&&mLiteBlueService.isBinded()&&isChecked) {
				final ProgressDialog syncDialog = showProgressDialog("正在设置...");
				mLiteBlueService.writeCharacticsUseConnectListener(ProtocolForWrite.instance().getByteForAlarmClock(hour, min));
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						syncDialog.dismiss();
					}
				}, 1000);
				
			} else {
				showShortToast("手环没有绑定");
			}
			finish();
			break;

		default:
			break;
		}

	}
}
