package com.mycj.mywatch.activity;


import com.mycj.mywatch.BaseActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.fragment.HeartRateFragment;
import com.mycj.mywatch.fragment.HeartRateHistoryFragment;
import com.mycj.mywatch.fragment.HeartRateSettingFragment;
import com.mycj.mywatch.fragment.PedoFragment;
import com.mycj.mywatch.fragment.PedoHistoryFragment;
import com.mycj.mywatch.fragment.PedoSettingFragment;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

@SuppressLint("CommitTransaction")
public class HeartRateActivity extends BaseActivity implements OnClickListener{

	private TextView tvHreatRate;
	private TextView tvHistory;
	private TextView tvSetting;
	private FrameLayout rlBack;
	private TextView tvTitle;
	private HeartRateFragment hrFragment;
	private HeartRateHistoryFragment hrHistoryFragment;
	private HeartRateSettingFragment hrSettingFragment;
	private TextView tvSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_heart_rate);

		initViews();
		setListener();
		//初始化tab
		updateTab(0);
	}
	
	@Override
	public void initViews() {
		tvHreatRate = (TextView) findViewById(R.id.tv_hr_bottom);
		tvHistory = (TextView) findViewById(R.id.tv_history_bottom);
		tvSetting = (TextView) findViewById(R.id.tv_setting_bottom);
		tvSave = (TextView) findViewById(R.id.tv_save);
		tvTitle = (TextView) findViewById(R.id.tv_pedo_title);
		rlBack = (FrameLayout) findViewById(R.id.fl_home);
	
	}


	@Override
	public void setListener() {
		tvHreatRate.setOnClickListener(this);
		tvHistory.setOnClickListener(this);
		tvSetting.setOnClickListener(this);
		rlBack.setOnClickListener(this);
		tvSave.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tv_hr_bottom:
			updateTab(0);
			break;
		case R.id.tv_history_bottom:
			updateTab(1);
			break;
		case R.id.tv_setting_bottom:
			updateTab(2);
			break;
		case R.id.fl_home:
			finish();
			break;
			
		default:
			break;
		}
	}

	/**
		 * 更新底部选中状态
		 * @param id
		 */
		private void updateTab(int id) {
			clearTab();
			FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
			switch (id) {
			case 0:
				tvHreatRate.setTextColor(getResources().getColor(R.color.color_top_blue));
				tvTitle.setText("HEART RATE");
				setDrawable(tvHreatRate, R.drawable.ic_tab_hr);
				
				if(hrFragment==null){
					hrFragment = new HeartRateFragment();
				}
				beginTransaction.replace(R.id.frame_heart_rate, hrFragment);
				break;
			case 1:
				if(hrHistoryFragment==null){
					hrHistoryFragment = new HeartRateHistoryFragment();
				}
				tvTitle.setText("HISTORY");
				beginTransaction.replace(R.id.frame_heart_rate, hrHistoryFragment);
				tvHistory.setTextColor(getResources().getColor(R.color.color_top_blue));
				setDrawable(tvHistory, R.drawable.ic_pedo_tab_history);
				break;
			case 2:
				if(hrSettingFragment==null){
					hrSettingFragment = new HeartRateSettingFragment();
				}
				tvTitle.setText("SETTING");
				beginTransaction.replace(R.id.frame_heart_rate, hrSettingFragment);
				tvSetting.setTextColor(getResources().getColor(R.color.color_top_blue));
				setDrawable(tvSetting, R.drawable.ic_pedo_tab_setting);
				break;
	
			default:
				break;
			}
	//		beginTransaction.addToBackStack(null);
			beginTransaction.commitAllowingStateLoss();
	//		beginTransaction.commit();
		}

	/**
	 * 清楚底部选中状态
	 */
	private void clearTab() {
		tvHreatRate.setTextColor(getResources().getColor(R.color.grey));
		tvHistory.setTextColor(getResources().getColor(R.color.grey));
		tvSetting.setTextColor(getResources().getColor(R.color.grey));
		setDrawable(tvHreatRate, R.drawable.ic_tab_hr_unpressed);
		setDrawable(tvHistory, R.drawable.ic_pedo_tab_history_unpress);
		setDrawable(tvSetting, R.drawable.ic_pedo_tab_setting_unpress);
	}

	/**
	 * 设置TextView 图片
	 * @param tv
	 * @param resourceid
	 */
	private void setDrawable(TextView tv , int resourceid){
		 Resources res = getResources();
		 Drawable img = res.getDrawable(resourceid);
		 // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		 img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
		 tv.setCompoundDrawables(null, img, null, null); //
	}
	
	 public boolean onTouchEvent(MotionEvent event) {
	        if(null != this.getCurrentFocus()){
	            /**
	             * 点击空白位置 隐藏软键盘
	             */
	            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
	        }
	        return super .onTouchEvent(event);
	 }
}
