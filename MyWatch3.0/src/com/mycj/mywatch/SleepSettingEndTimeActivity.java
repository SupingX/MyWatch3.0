package com.mycj.mywatch;

import com.mycj.mywatch.adapter.NumberWheelAdapter;
import com.mycj.mywatch.bean.Constant;
import com.mycj.mywatch.util.DisplayUtil;
import com.mycj.mywatch.util.SharedPreferenceUtil;
import com.mycj.mywatch.view.OnWheelChangedListener;
import com.mycj.mywatch.view.OnWheelScrollListener;
import com.mycj.mywatch.view.WheelView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class SleepSettingEndTimeActivity extends BaseActivity implements OnClickListener{

	private View timepickerview1;
	private TextView tvSave;
	private WheelView hourWV;
	private WheelView minWV;
	private RelativeLayout rlSetting;
	private TextView tvEndTime;
	private RelativeLayout rlEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep_setting_end_time);
		initViews();
		setListener();
	}

	private void initNumberPicker() {
		LayoutInflater inflater1 = LayoutInflater.from(this);
		timepickerview1 = inflater1.inflate(R.layout.timepicker, null);
		// 设置时间
		hourWV = (WheelView) timepickerview1.findViewById(R.id.hour);
		hourWV.setAdapter(new NumberWheelAdapter(0, 23));
		hourWV.setCyclic(true);// 可循环滚动
		hourWV.setLabel("时");// 文字
		hourWV.setCurrentItem(0, true);
		// 设置分钟
		minWV = (WheelView) timepickerview1.findViewById(R.id.min);
		minWV.setAdapter(new NumberWheelAdapter(0, 59));
		minWV.setCyclic(true);// 可循环滚动
		minWV.setLabel("分");
		minWV.setCurrentItem(0, true);
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
		rlEnd.addView(timepickerview1);
	}

	@Override
	public void initViews() {
		rlSetting = (RelativeLayout) findViewById(R.id.rl_setting);
		tvSave = (TextView) findViewById(R.id.tv_save);
		//初始化
		tvEndTime = (TextView) findViewById(R.id.tv_end_time);
		int hour = getIntent().getIntExtra("end_hour", 0);
		int min = getIntent().getIntExtra("end_min", 0);
		tvEndTime.setText(formatValue(hour) + " : " + formatValue(min));
		rlEnd = (RelativeLayout) findViewById(R.id.rl_end);
		initNumberPicker();
	}

	private String formatValue(int value) {
		return value < 10 ? "0" + value : String.valueOf(value);
	}

	@Override
	public void setListener() {
		rlSetting.setOnClickListener(this);
		tvSave.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_save:
			// if (mLiteBlueService.isServiceDiscovered()) {
			int hour = hourWV.getCurrentItem();
			int min = minWV.getCurrentItem();
			Log.v("", "hour : min - - - >" + hour + " : " + min);
			SharedPreferenceUtil.put(this, Constant.SHARE_SLEEP_END_HOUR, hour);
			SharedPreferenceUtil.put(this, Constant.SHARE_SLEEP_END_MIN, min);
			finish();
			// }else{
			// showShortToast("手环没有绑定");
			// }
			break;
		case R.id.rl_setting:
			finish();
			break;

		default:
			break;
		}
	}
}
