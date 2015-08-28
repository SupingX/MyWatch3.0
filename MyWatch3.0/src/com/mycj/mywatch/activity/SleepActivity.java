package com.mycj.mywatch.activity;

import java.util.List;

import com.mycj.mywatch.BaseActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.R.layout;
import com.mycj.mywatch.fragment.PedoFragment;
import com.mycj.mywatch.fragment.PedoHistoryFragment;
import com.mycj.mywatch.fragment.PedoSettingFragment;
import com.mycj.mywatch.fragment.SleepFragment;
import com.mycj.mywatch.fragment.SleepHistoryFragment;
import com.mycj.mywatch.fragment.SleepSettingFragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class SleepActivity extends BaseActivity implements OnClickListener {
	private TextView tvSleep;
	private TextView tvHistory;
	private TextView tvSetting;
	private SleepFragment sleepFragment;
	private SleepHistoryFragment sleepHistoryFragment;
	private SleepSettingFragment sleepSettingFragment;
	private FrameLayout flBack;
	private ImageView imgSync;
	private FrameLayout flSync;
	private ObjectAnimator startAnimation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep);
		initViews();
		setListener();
		//初始化tab
		updateTab(0);
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
			tvSleep.setTextColor(getResources().getColor(R.color.color_top_blue));
			setDrawable(tvSleep, R.drawable.ic_sleep_icon);
			if(sleepFragment==null){
				sleepFragment = new SleepFragment();
			}
			beginTransaction.replace(R.id.frame_sleep, sleepFragment);
			break;
		case 1:
			if(sleepHistoryFragment==null){
				sleepHistoryFragment = new SleepHistoryFragment();
			}
			beginTransaction.replace(R.id.frame_sleep, sleepHistoryFragment);
			tvHistory.setTextColor(getResources().getColor(R.color.color_top_blue));
			setDrawable(tvHistory, R.drawable.ic_pedo_tab_history);
			break;
		case 2:
			if(sleepSettingFragment==null){
				sleepSettingFragment = new SleepSettingFragment();
			}
			beginTransaction.replace(R.id.frame_sleep, sleepSettingFragment);
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
		tvSleep.setTextColor(getResources().getColor(R.color.grey));
		tvHistory.setTextColor(getResources().getColor(R.color.grey));
		tvSetting.setTextColor(getResources().getColor(R.color.grey));
		setDrawable(tvSleep, R.drawable.ic_sleep_icon_unpressed);
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
	
	@Override
	public void initViews() {
		tvSleep = (TextView) findViewById(R.id.tv_sleep_bottom);
		tvHistory = (TextView) findViewById(R.id.tv_sleep_history_bottom);
		tvSetting = (TextView) findViewById(R.id.tv_sleep_setting_bottom);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		flBack = (FrameLayout) findViewById(R.id.fl_home);
		flSync = (FrameLayout) findViewById(R.id.fl_sync);
		imgSync = (ImageView) findViewById(R.id.img_sync_sleep);
		
	
	}

//	private void initViewPager() {
//		fragments = new ArrayList<Fragment>();
//		fragments.add(new PedoFragment());
//		fragments.add(new PedoHistoryFragment());
//		fragments.add(new PedoSettingFragment());
//		vpPedo = (ViewPager) findViewById(R.id.vp_pedo);
//		vpPedo.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
//			
//			@Override
//			public int getCount() {
//				return 3;
//			}
//			
//			@Override
//			public Fragment getItem(int idx) {
//				return fragments.get(idx);
//			}
//		});
//		vpPedo.setOnPageChangeListener(new OnPageChangeListener() {
//			
//			@Override
//			public void onPageSelected(int arg0) {
//				updateTab(arg0);
//			}
//			
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {
//				
//			}
//			
//			@Override
//			public void onPageScrollStateChanged(int arg0) {
//				
//			}
//		});
//		
//		vpPedo.setCurrentItem(0);
//	}

	@Override
	public void setListener() {
		tvSleep.setOnClickListener(this);
		tvHistory.setOnClickListener(this);
		tvSetting.setOnClickListener(this);
		flBack.setOnClickListener(this);
		flSync.setOnClickListener(this);
	}

//	@Override
//	public void onClick(View v) {
//		int id = v.getId();
//		switch (id) {
//		case R.id.tv_pedo_bottom:
//			updateTab(0);
//			vpPedo.setCurrentItem(0);
//			break;
//		case R.id.tv_history_bottom:
//			updateTab(1);
//			vpPedo.setCurrentItem(1);
//			break;
//		case R.id.tv_setting_bottom:
//			updateTab(2);
//			vpPedo.setCurrentItem(2);
//			break;
//
//		default:
//			break;
//		}
//	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tv_sleep_bottom:
			updateTab(0);
//			vpPedo.setCurrentItem(0);
			tvTitle.setText("SLEEP");
			break;
		case R.id.tv_sleep_history_bottom:
			updateTab(1);
			tvTitle.setText("HISTORY");
//			vpPedo.setCurrentItem(1);
			break;
		case R.id.tv_sleep_setting_bottom:
			updateTab(2);
			tvTitle.setText("SETTING");
//			vpPedo.setCurrentItem(2);
			break;
		case R.id.fl_home:
			finish();
			break;
		case R.id.fl_sync:
			if (!isSyncing) {
				isSyncing = true;
				startAnimation = startAnimation(imgSync);
				startAnimation.start();
				//发送跟新请求
				
				//请求成功，1.关闭动画 2.isSyncing = false;
			}
			break;
			
		default:
			break;
		}
	}
	
	private boolean isSyncing;
	private TextView tvTitle;
	
}
