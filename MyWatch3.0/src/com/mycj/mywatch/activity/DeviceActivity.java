package com.mycj.mywatch.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mycj.mywatch.BaseActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.R.layout;
import com.mycj.mywatch.bean.Constant;
import com.mycj.mywatch.business.ProtocolForWrite;
import com.mycj.mywatch.service.AbstractLiteBlueService;
import com.mycj.mywatch.service.LiteBlueService;
import com.mycj.mywatch.util.SharedPreferenceUtil;
import com.mycj.mywatch.view.ActionSheetDialog;
import com.mycj.mywatch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.mycj.mywatch.view.ActionSheetDialog.SheetItemColor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DeviceActivity extends BaseActivity implements OnClickListener {

	private LiteBlueService mLiteBlueService;
	private FrameLayout flHome;
	private RelativeLayout rlSearch;
	private RelativeLayout rlBindOther;
	private TextView tvDeviceName;
	private TextView tvDeviceAddress;
	private TextView tvDeviceConnectState;
	private ImageView imgDeviceExit;
	private CheckBox cbRemind;

	private boolean isRemindOpen;

	private Handler mHandler = new Handler() {
	};
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(AbstractLiteBlueService.LITE_GATT_CONNECTED)) {

				runOnUiThread(new Runnable() {
					public void run() {
						setDeviceConnectState();
						setCurrentDevice();
					}
				});

			} else if (action.equals(AbstractLiteBlueService.LITE_GATT_SERVICE_DISCOVERED)) {
				runOnUiThread(new Runnable() {
					public void run() {
						setDeviceConnectState();
						setCurrentDevice();
					}
				});
			} else if (action.equals(AbstractLiteBlueService.LITE_GATT_SERVICE_DISCOVERING)) {
				runOnUiThread(new Runnable() {
					public void run() {
						setDeviceConnectState();
						setCurrentDevice();
					}
				});
			} else if (action.equals(AbstractLiteBlueService.LITE_GATT_DISCONNECTED)) {
				runOnUiThread(new Runnable() {
					public void run() {
						setDeviceConnectState();
						setCurrentDevice();
					}
				});
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		mLiteBlueService = getLiteBlueService();
		initViews();
		setListener();
		Log.e("", "mLiteBlueService : " + mLiteBlueService);
		registerReceiver(mReceiver, LiteBlueService.getIntentFilter());
		if (!mLiteBlueService.isEnable()) { //
			showIosDialog();
		} else {
			if (mLiteBlueService.isBinded()) {// 当本地绑定时，开启搜索。
				mLiteBlueService.startScanUsePeriodScanCallback();
			}
		}
		
		isRemindOpen = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_CHECK_REMIND, false);//
		
	}

	@Override
	protected void onResume() {
		setCurrentDevice();
		setDeviceConnectState();
		setCheckBoxRemind();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void initViews() {
		flHome = (FrameLayout) findViewById(R.id.fl_home);
		rlSearch = (RelativeLayout) findViewById(R.id.rl_search);
		rlBindOther = (RelativeLayout) findViewById(R.id.rl_bind_other);
		cbRemind = (CheckBox) findViewById(R.id.cb_remind);
		imgDeviceExit = (ImageView) findViewById(R.id.img_current_device_exit);

		tvDeviceName = (TextView) findViewById(R.id.tv_current_device_name);
		tvDeviceAddress = (TextView) findViewById(R.id.tv_current_device_address);
		tvDeviceConnectState = (TextView) findViewById(R.id.tv_current_device_connect_state);

	}

	@Override
	public void setListener() {
		flHome.setOnClickListener(this);
		rlSearch.setOnClickListener(this);
		rlBindOther.setOnClickListener(this);
		imgDeviceExit.setOnClickListener(this);
		cbRemind.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_home:
			finish();
			break;
		case R.id.rl_search:
			startActivity(DeviceSearchDeviceActivity.class);
			break;
		case R.id.rl_bind_other:
			startActivity(DeviceBindOtherActivity.class);
			break;
		case R.id.cb_remind:
			isRemindOpen = !isRemindOpen;
			cbRemind.setChecked(isRemindOpen);
			SharedPreferenceUtil.put(this, Constant.SHARE_CHECK_REMIND, isRemindOpen);
			if (mLiteBlueService.isBinded() && mLiteBlueService.isServiceDiscovered()) {
				final ProgressDialog syncDialog = showProgressDialog("正在设置...");
				mLiteBlueService.writeCharacticsUseConnectListener(ProtocolForWrite.instance().getByteForAvoidLose(isRemindOpen?0x02:0xA2));
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						syncDialog.dismiss();
					}
				}, 1000);
			} else {
				showAlertDialog("", "请连接手环");
			}
			
			break;
		case R.id.img_current_device_exit:
			exitBinded();
			break;

		default:
			break;
		}
	}

	private void setCurrentDevice() {
		String name = mLiteBlueService.getBindedDeviceName();
		String address = mLiteBlueService.getBindedDeviceAddress();
		if (name.equals("") && address.equals("")) {
			tvDeviceName.setText("无绑定");
			tvDeviceAddress.setText("");
		} else {
			tvDeviceName.setText(name);
			tvDeviceAddress.setText(address);
		}
	}

	private void setDeviceConnectState() {
		if (mLiteBlueService.isConnected()) {
			tvDeviceConnectState.setText("已连接");
		} else if (mLiteBlueService.isServiceDiscovering()) {
			tvDeviceConnectState.setText("匹配中...");
		} else if (mLiteBlueService.isServiceDiscovered()) {
			tvDeviceConnectState.setText("已连接匹配的设备");
		} else {
			tvDeviceConnectState.setText("未连接");
		}

	}

	private void setCheckBoxRemind() {
		isRemindOpen = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_CHECK_REMIND, false);
		cbRemind.setChecked(isRemindOpen);
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

	private void exitBinded() {
		if (mLiteBlueService.isBinded()) {
			final ActionSheetDialog dialog = new ActionSheetDialog(this).builder();
			dialog.setTitle(getResources().getString(R.string.contact_binding));
			dialog.addSheetItem(getResources().getString(R.string.confirm), SheetItemColor.Red, new OnSheetItemClickListener() {
				@Override
				public void onClick(int which) {
					mLiteBlueService.closeAll();
					final ProgressDialog showProgressDialog = showProgressDialog("连接关闭中...");
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							showProgressDialog.dismiss();
							mLiteBlueService.setCurrentBluetoothDevice(null);
							mLiteBlueService.saveBindedDevie(null);
							setCurrentDevice();
							setDeviceConnectState();
//							tvDeviceConnectState.setText("未连接");
						}
					}, 200);
				}
			}).show();
		} else {
			showShortToast(R.string.no_device_binding);
		}
	}

}
