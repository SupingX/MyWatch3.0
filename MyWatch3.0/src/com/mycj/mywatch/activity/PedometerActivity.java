package com.mycj.mywatch.activity;

import java.util.ArrayList;
import java.util.List;

import com.mycj.mywatch.BaseActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.R.layout;
import com.mycj.mywatch.fragment.PedoFragment;
import com.mycj.mywatch.fragment.PedoHistoryFragment;
import com.mycj.mywatch.fragment.PedoSettingFragment;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("CommitTransaction")
public class PedometerActivity extends BaseActivity implements OnClickListener{

	private TextView tvPedo;
	private TextView tvHistory;
	private TextView tvSetting;
	private ViewPager vpPedo;
	private List<Fragment> fragments;
	private PedoFragment pedoFragment;
	private PedoHistoryFragment pedoHistoryFragment;
	private PedoSettingFragment pedoSettingFragment;
	private FrameLayout rlBack;
	private TextView tvTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedometer);

		initViews();
		setListener();
		//初始化tab
		updateTab(0);
	}
	
	@Override
	public void initViews() {
		tvPedo = (TextView) findViewById(R.id.tv_pedo_bottom);
		tvHistory = (TextView) findViewById(R.id.tv_history_bottom);
		tvSetting = (TextView) findViewById(R.id.tv_setting_bottom);
		tvTitle = (TextView) findViewById(R.id.tv_pedo_title);
		rlBack = (FrameLayout) findViewById(R.id.fl_home);
	
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
		tvPedo.setOnClickListener(this);
		tvHistory.setOnClickListener(this);
		tvSetting.setOnClickListener(this);
		rlBack.setOnClickListener(this);
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
		case R.id.tv_pedo_bottom:
			updateTab(0);
//			vpPedo.setCurrentItem(0);
			break;
		case R.id.tv_history_bottom:
			updateTab(1);
//			vpPedo.setCurrentItem(1);
			break;
		case R.id.tv_setting_bottom:
			updateTab(2);
//			vpPedo.setCurrentItem(2);
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
				tvPedo.setTextColor(getResources().getColor(R.color.color_top_blue));
				tvTitle.setText("PEDOMETER");
				setDrawable(tvPedo, R.drawable.ic_pedo_tab_icon);
				
				if(pedoFragment==null){
					pedoFragment = new PedoFragment();
				}
				beginTransaction.replace(R.id.frame_pedo, pedoFragment);
				break;
			case 1:
				if(pedoHistoryFragment==null){
					pedoHistoryFragment = new PedoHistoryFragment();
				}
				tvTitle.setText("HISTORY");
				beginTransaction.replace(R.id.frame_pedo, pedoHistoryFragment);
				tvHistory.setTextColor(getResources().getColor(R.color.color_top_blue));
				setDrawable(tvHistory, R.drawable.ic_pedo_tab_history);
				break;
			case 2:
				if(pedoSettingFragment==null){
					pedoSettingFragment = new PedoSettingFragment();
				}
				tvTitle.setText("SETTING");
				beginTransaction.replace(R.id.frame_pedo, pedoSettingFragment);
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
		tvPedo.setTextColor(getResources().getColor(R.color.grey));
		tvHistory.setTextColor(getResources().getColor(R.color.grey));
		tvSetting.setTextColor(getResources().getColor(R.color.grey));
		setDrawable(tvPedo, R.drawable.ic_pedo_tab_icon_unpress);
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
	
}
