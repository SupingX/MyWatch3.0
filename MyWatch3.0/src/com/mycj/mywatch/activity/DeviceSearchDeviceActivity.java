package com.mycj.mywatch.activity;

import com.mycj.mywatch.BaseActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.R.id;
import com.mycj.mywatch.R.layout;
import com.mycj.mywatch.business.ProtocolForWrite;
import com.mycj.mywatch.service.AbstractLiteBlueService;
import com.mycj.mywatch.service.LiteBlueService;
import com.mycj.mywatch.service.SimpleBlueService;
import com.mycj.mywatch.view.RadarView;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DeviceSearchDeviceActivity extends BaseActivity implements OnClickListener{

	private RadarView radar;
//	private SimpleBlueService mSimpleBlueService;	
	
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(AbstractLiteBlueService.LITE_GATT_DEVICE_FOUND)) {
			

			} else if (action.equals(AbstractLiteBlueService.LITE_GATT_DISCONNECTED)) {
				tvDisconnect.setText("disconneted");
				tvRssi.setText("-----");
				mHandler.removeCallbacks(run);
			} else if (action.equals(AbstractLiteBlueService.LITE_GATT_SERVICE_DISCOVERED)) {
				tvDisconnect.setText("");
				if (mLiteBlueService.isBinded()) {
					mHandler.removeCallbacks(run);
					mHandler.post(run);
				}
			} else if (action.equals(AbstractLiteBlueService.LITE_CHARACTERISTIC_RSSI_CHANGED)) {
				final int rssi = intent.getExtras().getInt(LiteBlueService.EXTRA_RSSI);
				runOnUiThread( new Runnable() {
					public void run() {
						tvRssi.setText(String.valueOf(rssi));
						imgRssi.setImageResource(getRssiImg(rssi));
					}
				});
			}else if (action.equals(SimpleBlueService.ACTION_REMOTE_RSSI)) {
				final int rssi = intent.getExtras().getInt(SimpleBlueService.EXTRA_RSSI);
				runOnUiThread( new Runnable() {
					public void run() {
						tvRssi.setText(String.valueOf(rssi));
						imgRssi.setImageResource(getRssiImg(rssi));
						tvDisconnect.setText("");
					}
				});
			} 	else if (action.equals(SimpleBlueService.ACTION_CONNECTION_STATE)) {
				final Integer rssi = intent.getExtras().getInt(SimpleBlueService.EXTRA_CONNECT_STATE);
				switch (rssi) {
				case BluetoothProfile.STATE_CONNECTED:
					mHandler.removeCallbacks(run);
					mHandler.post(run);
					break;
				case BluetoothProfile.STATE_DISCONNECTING:
					break;
				case BluetoothProfile.STATE_CONNECTING:
					break;
				case BluetoothProfile.STATE_DISCONNECTED:
					mHandler.removeCallbacks(run);
					break;
				default:
					break;
				}
			}
			
		}
	};
	private LiteBlueService mLiteBlueService;
	private Runnable run;

	private TextView tvRssi;

	private TextView tvDisconnect;

	private ImageView imgRssi;
	
	private Handler  mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				mHandler.removeCallbacks(run);
				mHandler.postDelayed(run, 3000);
				break;

			default:
				break;
			}
		};
	};

	private RelativeLayout rlDevice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_search_device);
		mLiteBlueService = getLiteBlueService();
//		mSimpleBlueService = getSimpleBlueService();
		initViews();
		setListener();
		registerReceiver(mReceiver, LiteBlueService.getIntentFilter());
//		registerReceiver(mReceiver, SimpleBlueService.getIntentFilter());
		
		run = new Runnable() {
			@Override
			public void run() {
				Log.v("", "______请求rssi_______");
			
					mLiteBlueService.readRemoteRssi();
//					mSimpleBlueService.readRemoteRssi();
					mHandler.sendEmptyMessage(0);
				}
			
		
		};
	}
	
	@Override
	protected void onResume() {
		radar.start();
		if (mLiteBlueService.isServiceDiscovered()&&mLiteBlueService.isBinded()) {
			mHandler.post(run);
			tvDisconnect.setText("");
			mLiteBlueService.writeCharacticsUseConnectListener(ProtocolForWrite.instance().getByteForAvoidLose(0x01));
		}else{
			tvDisconnect.setText("disconneted");
			tvRssi.setText("-----");
		}
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		radar.stop();
		if (mLiteBlueService.isServiceDiscovered()&&mLiteBlueService.isBinded()) {
			mLiteBlueService.writeCharacticsUseConnectListener(ProtocolForWrite.instance().getByteForAvoidLose(0xA1));
			mHandler.removeCallbacks(run);
		}
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
	
		mHandler.removeCallbacks(run);
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	@Override
	public void initViews() {
		radar = (RadarView) findViewById(R.id.radar);
		tvRssi = (TextView) findViewById(R.id.tv_search_rssi);
		tvDisconnect = (TextView) findViewById(R.id.tv_search_disconnect);
		imgRssi = (ImageView) findViewById(R.id.img_search_rssi);
		rlDevice = (RelativeLayout) findViewById(R.id.rl_device);
		
	}

	@Override
	public void setListener() {
		rlDevice.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_device:
			finish();
			break;

		default:
			break;
		}
	}
}
