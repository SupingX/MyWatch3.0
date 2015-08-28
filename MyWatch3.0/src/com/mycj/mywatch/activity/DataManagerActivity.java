package com.mycj.mywatch.activity;

import com.mycj.mywatch.BaseActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.R.layout;
import com.mycj.mywatch.business.ProtocolForWrite;
import com.mycj.mywatch.service.LiteBlueService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class DataManagerActivity extends BaseActivity implements OnClickListener {

	private RelativeLayout rlSyncAll;
	private RelativeLayout rlClearAll;
	private FrameLayout frMore;
	private LiteBlueService mLiteBlueService;
	private Handler mHandler = new Handler() {

	};
	private RelativeLayout rlMore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_manager);
		mLiteBlueService = getLiteBlueService();
		initViews();
		setListener();
	}

	@Override
	public void initViews() {
		rlSyncAll = (RelativeLayout) findViewById(R.id.rl_sync_all);
		rlClearAll = (RelativeLayout) findViewById(R.id.rl_clear_all);
		rlMore = (RelativeLayout) findViewById(R.id.rl_setting);
	}

	@Override
	public void setListener() {
		rlSyncAll.setOnClickListener(this);
		rlClearAll.setOnClickListener(this);
		rlMore.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_sync_all:
			if (mLiteBlueService.isBinded() && mLiteBlueService.isServiceDiscovered()) {

				mLiteBlueService.writeCharacticsUseConnectListener(ProtocolForWrite.instance().getByteForSyncHistoryData());
				final ProgressDialog syncDialog = showProgressDialog("正在同步...");
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						syncDialog.dismiss();
					}
				}, 3000);
			}else{
				showAlertDialog("", "请连接手环");
			}
			break;
		case R.id.rl_clear_all:
			final ProgressDialog clearDialog = showProgressDialog("正在删除...");
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					clearDialog.dismiss();
				}
			}, 1000);
			
			break;
		case R.id.rl_setting:
			finish();
			break;

		default:
			break;
		}
	}
}
