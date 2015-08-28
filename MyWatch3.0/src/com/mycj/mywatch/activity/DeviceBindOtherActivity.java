package com.mycj.mywatch.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import com.mycj.mywatch.BaseActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.R.layout;
import com.mycj.mywatch.service.AbstractLiteBlueService;
import com.mycj.mywatch.service.LiteBlueService;
import com.mycj.mywatch.service.SimpleBlueService;
import com.mycj.mywatch.view.ActionSheetDialog;
import com.mycj.mywatch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.mycj.mywatch.view.ActionSheetDialog.SheetItemColor;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DeviceBindOtherActivity extends BaseActivity implements OnClickListener {
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:

				break;
			case 0x0101:
			
				break;

			default:
				break;
			}
		};
	};
	private ProgressDialog progressDialog;
	private LiteBlueService mLiteBlueService;
	private List<BluetoothDevice> listDevices;
	private int[] rssis = new int[100];
	private MyAdapter mAdapter;
	private ListView lvDevice;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(AbstractLiteBlueService.LITE_GATT_DEVICE_FOUND)) {

				final BluetoothDevice device = intent.getParcelableExtra(AbstractLiteBlueService.EXTRA_DEVICE);
				final Integer rssi = intent.getExtras().getInt(AbstractLiteBlueService.EXTRA_RSSI);
				runOnUiThread(new Runnable() {
					public void run() {

						if (listDevices.contains(device)) {
							int pos = listDevices.indexOf(device);
							rssis[pos] = rssi;
							mAdapter.notifyDataSetChanged();
						} else {
							listDevices.add(device);
							int pos = listDevices.indexOf(device);
							rssis[pos] = rssi;
							mAdapter.notifyDataSetChanged();
						}
					}
				});
			} else if (action.equals(AbstractLiteBlueService.LITE_GATT_CONNECTED)) {

			} else if (action.equals(AbstractLiteBlueService.LITE_GATT_SERVICE_DISCOVERED)) {
				//
				Log.e("", "------------已连接跳转吧-----------");
				runOnUiThread(new Runnable() {
					public void run() {
						progressDialog.dismiss();
						showShortToast("已连接至-->");
						finish();
					}
				});
			}

//			else if (action.equals(SimpleBlueService.ACTION_DEVICE_FOUND)) {
//				final BluetoothDevice device = intent.getParcelableExtra(SimpleBlueService.EXTRA_DEVICE);
//				final Integer rssi = intent.getExtras().getInt(SimpleBlueService.EXTRA_RSSI);
//				runOnUiThread(new Runnable() {
//					public void run() {
//						if (listDevices.contains(device)) {
//							int pos = listDevices.indexOf(device);
//							rssis[pos] = rssi;
//							mAdapter.notifyDataSetChanged();
//						} else {
//							listDevices.add(device);
//							int pos = listDevices.indexOf(device);
//							rssis[pos] = rssi;
//							mAdapter.notifyDataSetChanged();
//						}
//					}
//				});
//			}
//			else if (action.equals(SimpleBlueService.ACTION_CONNECTION_STATE)) {
//				final Integer rssi = intent.getExtras().getInt(SimpleBlueService.EXTRA_CONNECT_STATE);
//				switch (rssi) {
//				case BluetoothProfile.STATE_CONNECTED:
//					break;
//				case BluetoothProfile.STATE_DISCONNECTING:
//					break;
//				case BluetoothProfile.STATE_CONNECTING:
//					break;
//				case BluetoothProfile.STATE_DISCONNECTED:
//					
//					break;
//				default:
//					break;
//				}
//			}
//			else if (action.equals(SimpleBlueService.ACTION_SERVICE_DISCOVERED)) {
//				Log.e("", "------------已连接跳转吧-----------");
//				runOnUiThread(new Runnable() {
//					public void run() {
//						progressDialog.dismiss();
//						showShortToast("已连接至-->");
//						finish();
//					}
//				});
//			}
		}
	};
	private RelativeLayout rlDevice;
	private ImageView imgLoading;
//	private SimpleBlueService simpleBlueService;
	private ObjectAnimator startAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_device_bind_other);
		mLiteBlueService = getLiteBlueService();
//		simpleBlueService = getSimpleBlueService();

		
		listDevices = new ArrayList<>();
		mAdapter = new MyAdapter();
		initViews();
		setListener();

		 registerReceiver(mReceiver, LiteBlueService.getIntentFilter());
//		registerReceiver(mReceiver, SimpleBlueService.getIntentFilter());

		 if (!mLiteBlueService.isEnable()) {
		 showIosDialog();
		 } else {
		
		 mLiteBlueService.startScanUsePeriodScanCallback();
		 startAnimation(imgLoading);
		 }

		startAnimation = startAnimation(imgLoading);
		startAnimation.start();
//		mHandler.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				simpleBlueService.scanDevice(true);
//			}
//		}, 200);
	}

	

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		if (mLiteBlueService != null && mLiteBlueService.isScanning()) {
			mLiteBlueService.stopScanUsePeriodScanCallback();
		}
//		simpleBlueService.scanDevice(false);
		
		super.onDestroy();
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
							mLiteBlueService.startScanUsePeriodScanCallback();
						} else {
							// 未打开
						}
					}
				}, 5000);
				mLiteBlueService.enable();
			}
		}).show();
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listDevices.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.item_device, parent, false);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
				holder.tvRssi = (TextView) convertView.findViewById(R.id.tv_rssi);
				holder.imgRssi = (ImageView) convertView.findViewById(R.id.img_rssi);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tvName.setText(listDevices.get(position).getName());
			holder.tvAddress.setText(listDevices.get(position).getAddress());

			// holder.tvRssi.setText(String.valueOf(listRssis.get(position).intValue()));
			// holder.imgRssi.setImageResource(getRssiImg(listRssis.get(position)));
			holder.tvRssi.setText(String.valueOf(rssis[position]));
			holder.imgRssi.setImageResource(getRssiImg(rssis[position]));
			return convertView;
		}

		class ViewHolder {
			TextView tvName;
			TextView tvAddress;
			TextView tvRssi;
			ImageView imgRssi;
		}
	}

	@Override
	public void initViews() {
		lvDevice = (ListView) findViewById(R.id.lv_device);
		lvDevice.setAdapter(mAdapter);
		rlDevice = (RelativeLayout) findViewById(R.id.rl_device);
		imgLoading = (ImageView) findViewById(R.id.ic_loading);
	}

	@Override
	public void setListener() {
		rlDevice.setOnClickListener(this);
		imgLoading.setOnClickListener(this);
		lvDevice.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
				progressDialog = showProgressDialog("正在连接");
				BluetoothDevice device = listDevices.get(position);
//				listDevices.clear();
				//方法1
				mLiteBlueService.stopScanUsePeriodScanCallback();
				mLiteBlueService.closeAll();
				mLiteBlueService.connnect(device);
//				方法2
//				if (simpleBlueService.isScanning()) {
//					simpleBlueService.scanDevice(false);
//				}
//				simpleBlueService.close();
//				simpleBlueService.connect(device);
				
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						progressDialog.dismiss();
					}
				}, 5000);

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_device:
			Log.e("", "退出");
			finish();
			break;
		case R.id.ic_loading:
			listDevices.clear();
//			if(simpleBlueService.isScanning()){
//				simpleBlueService.scanDevice(false);
//			}
			if(mLiteBlueService.isScanning()){
				mLiteBlueService.stopScanUsePeriodScanCallback();
			}
			
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
//					simpleBlueService.scanDevice(true);
					mLiteBlueService.startScanUsePeriodScanCallback();
				}
			}, 1000);
			break;

		default:
			break;
		}
	}

}
