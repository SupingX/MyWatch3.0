package com.mycj.mywatch.activity;

import java.util.Date;

import com.mycj.mywatch.BaseActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.R.layout;
import com.mycj.mywatch.bean.Constant;
import com.mycj.mywatch.business.ProtocolForWrite;
import com.mycj.mywatch.service.LiteBlueService;
import com.mycj.mywatch.util.SharedPreferenceUtil;
import com.mycj.mywatch.view.ActionSheetDialog;
import com.mycj.mywatch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.mycj.mywatch.view.ActionSheetDialog.SheetItemColor;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MoreActivity extends BaseActivity implements OnClickListener {

	private FrameLayout flHome;
	private RelativeLayout rlMusic;
	private RelativeLayout rlDataManager;
	private RelativeLayout rlTimeSync;
	private RelativeLayout rlShutdown;
	private RelativeLayout rlAbout;
	private CheckBox cbReminderCall;
	private boolean isChecked;
	private ImageView imgTimeSyncLoading;
	private ImageView imgShutdown;
	private ObjectAnimator timeSyncAnimation;
	private ObjectAnimator shutdownAnimation;
	private Handler mHandler = new Handler() {
	};
	private LiteBlueService mLiteBlueService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		mLiteBlueService = getLiteBlueService();
		initViews();
		setListener();

	}

	@Override
	protected void onResume() {
		setReminderCallValue();
		super.onResume();
	}

	private void setReminderCallValue() {
		isChecked = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_CHECK_REMIND_CALL, false);
		cbReminderCall.setChecked(isChecked);
	}

	@Override
	public void initViews() {
		flHome = (FrameLayout) findViewById(R.id.fl_home);
		rlMusic = (RelativeLayout) findViewById(R.id.rl_music);
		rlDataManager = (RelativeLayout) findViewById(R.id.rl_data);
		rlTimeSync = (RelativeLayout) findViewById(R.id.rl_time_sync);
		rlShutdown = (RelativeLayout) findViewById(R.id.rl_shut);
		rlAbout = (RelativeLayout) findViewById(R.id.rl_about);
		cbReminderCall = (CheckBox) findViewById(R.id.cb_remind_call);
		imgTimeSyncLoading = (ImageView) findViewById(R.id.img_time_sync_loading);
		imgShutdown = (ImageView) findViewById(R.id.img_shut_loading);
	}

	@Override
	public void setListener() {
		flHome.setOnClickListener(this);
		rlMusic.setOnClickListener(this);
		rlDataManager.setOnClickListener(this);
		rlTimeSync.setOnClickListener(this);
		rlShutdown.setOnClickListener(this);
		rlAbout.setOnClickListener(this);
		cbReminderCall.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_home:
			finish();
			break;
		case R.id.rl_music:
			startActivity(MusicActivity.class);
			break;
		case R.id.rl_data:
			startActivity(DataManagerActivity.class);
			break;
		case R.id.rl_time_sync:

			if (mLiteBlueService.isBinded() && mLiteBlueService.isServiceDiscovered()) {
				imgTimeSyncLoading.setVisibility(View.VISIBLE);
				timeSyncAnimation = startAnimation(imgTimeSyncLoading);
				timeSyncAnimation.start();
				mLiteBlueService.writeCharacticsUseConnectListener(ProtocolForWrite.instance().getByteForSyncTime(new Date()));
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						timeSyncAnimation.cancel();
						imgTimeSyncLoading.setVisibility(View.INVISIBLE);
					}
				}, 2000);
			} else {
				showAlertDialog("", "请连接手环");
			}
			break;
		case R.id.rl_shut:
			
			showIosDialog();
		
			break;
		case R.id.rl_about:

			break;
		case R.id.cb_remind_call:
			isChecked = !isChecked;
			cbReminderCall.setChecked(isChecked);
			SharedPreferenceUtil.put(this, Constant.SHARE_CHECK_REMIND_CALL, isChecked);
			break;

		default:
			break;
		}
	}
	
	private void showIosDialog() {
		ActionSheetDialog dialog = new ActionSheetDialog(this).builder();
		dialog.setTitle("关闭手环？");
		dialog.addSheetItem("关闭", SheetItemColor.Red, new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				if (mLiteBlueService.isBinded() && mLiteBlueService.isServiceDiscovered()) {
					imgShutdown.setVisibility(View.VISIBLE);
					shutdownAnimation = startAnimation(imgShutdown);
					shutdownAnimation.start();
					mLiteBlueService.writeCharacticsUseConnectListener(ProtocolForWrite.instance().getByteForShutDown());
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							shutdownAnimation.cancel();
							imgShutdown.setVisibility(View.INVISIBLE);
						}
					}, 2000);
				} else {
					showAlertDialog("", "手环已断开连接");
				}
				
			}
		}).show();
	}
}
