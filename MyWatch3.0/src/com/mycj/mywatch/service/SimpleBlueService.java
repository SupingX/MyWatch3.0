package com.mycj.mywatch.service;

import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class SimpleBlueService extends Service {
	private final String TAG = "SimpleBlueService";
	public final static String ACTION_DEVICE_FOUND = "ACTION_DEVICE_FOUND";
	public final static String ACTION_SERVICE_DISCOVERED = "ACTION_SERVICE_DISCOVERED";
	public final static String ACTION_REMOTE_RSSI = "ACTION_REMOTE_RSSI";
	public final static String ACTION_CONNECTION_STATE = "ACTION_CONNECTION_STATE";
	
	public final static String EXTRA_DEVICE = "EXTRA_DEVICE";
	public final static String EXTRA_RSSI = "EXTRA_RSSI";
	public final static String EXTRA_CONNECT_STATE = "EXTRA_CONNECT_STATE";

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private AbstractSimpleBluetooth mSimpleBluetooth;
	private MyBinder myBinder = new MyBinder();

	private LiteBlueService mLiteBlueService;
	
	private boolean isScanning;
	private Handler mHander = new Handler() {

	};

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	public class MyBinder extends Binder {
		public SimpleBlueService getService() {
			return SimpleBlueService.this;
		}
	}

	public void scanDevice(boolean scan) {
		if (scan) {
//			mHander.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					isScanning = false;
//					mSimpleBluetooth.stopScan();
//				}
//			}, 10 * 1000);
			isScanning = true;
			mSimpleBluetooth.startScan();
		} else {
			isScanning = false;
			mSimpleBluetooth.stopScan();
		}
	}
	
	private int connectState;
	
	public int getConnectState(){
		return connectState;
	}

	public boolean isScanning() {
		return this.isScanning;
	}
	public void close()	{
		mSimpleBluetooth.close();
	}
	
	public void connect(BluetoothDevice device){
		mSimpleBluetooth.connect(device);
	}
	public void readRemoteRssi() {
		mSimpleBluetooth.readRemoteRssi();
	}
	
	@Override
	public void onCreate() {
		logV("onCreate");
		if (this.mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		}
		if (mBluetoothAdapter == null) {
//			mBluetoothAdapter = mBluetoothManager.getAdapter();
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		mSimpleBluetooth = new SimpleBluetooth(getApplicationContext(), mBluetoothAdapter);
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		logE("service销毁！");
		mSimpleBluetooth.close();
		connectState = BluetoothProfile.STATE_DISCONNECTED;
		super.onDestroy();
	}
	public static IntentFilter getIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_DEVICE_FOUND);
		intentFilter.addAction(ACTION_SERVICE_DISCOVERED);
		intentFilter.addAction(ACTION_REMOTE_RSSI);
		intentFilter.addAction(ACTION_CONNECTION_STATE);
		return intentFilter;
	}

	private void logV(String msg) {
		Log.v(TAG, "**   " + msg + "  **");
	}

	private void logE(String msg) {
		Log.e(TAG, "**   " + msg + "  **");
	};
	
	
	
	private class SimpleBluetooth extends AbstractSimpleBluetooth {
		
		
		public SimpleBluetooth(Context context, BluetoothAdapter mBluetoothAdapter) {
			super(context, mBluetoothAdapter);
		}

		@Override
		public void onLeScanCallBack(BluetoothDevice device, int rssi, byte[] scanRecord) {
			sendBroadcastForDeviceFound(device, rssi);
		}

		@Override
		public void onServicesDiscoveredCallBack(BluetoothGatt gatt, int status) {
			BluetoothGattService service = gatt.getService(UUID.fromString(BLE_SERVICE));
			if (service!=null) {
				Log.e("", "_______________匹配service______________");
				BluetoothGattCharacteristic characteristicWrite = service.getCharacteristic(UUID.fromString(BLE_CHARACTERISTIC_WRITE));
				BluetoothGattCharacteristic characteristicNotify = service.getCharacteristic(UUID.fromString(BLE_CHARACTERISTIC_NOTIFY));
				if (characteristicWrite!=null && characteristicNotify!=null) {
					Log.e("", "_______________匹配characteristicWrite_________________________");
					Log.e("", "_______________匹配characteristicNotify_________________________");
					BluetoothGattDescriptor descriptor = characteristicNotify.getDescriptor(UUID.fromString(DESC_CCC));
					descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
					gatt.writeDescriptor(descriptor);
					sendBroadcastForServiceDiscovered(gatt, status);
					saveDevice(gatt.getDevice());//绑定蓝牙
				}else{
					//没有连接匹配的服务,断开连接.防止连接不匹配的蓝牙?
				}
			}
		}

		@Override
		public void onReadRemoteRssiCallBack(BluetoothGatt gatt, int rssi, int status) {
			sendBroadcastForRemoteRssi(gatt,rssi, status);
		}

		
		@Override
		public void onConnectionStateChangeCallBack(BluetoothGatt gatt, int status, int newState) {
			connectState = newState;
			switch (newState) {
			case BluetoothProfile.STATE_CONNECTED:
				logV("已连接");
		
				gatt.discoverServices();
				break;
			case BluetoothProfile.STATE_DISCONNECTING:
				logV("断开中");
				connectState = newState;
				break;
			case BluetoothProfile.STATE_CONNECTING:
				logV("连接中");
				connectState = newState;
				break;
			case BluetoothProfile.STATE_DISCONNECTED:
				logV("已断开");
				connectState = newState;
				//重连
				reconnect(gatt);
				break;
			default:
				break;
			}
			sendBroadcastForConnectionState(gatt,newState);
		}

		@Override
		public void onCharacteristicWriteCallBack(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

		}

		@Override
		public void onCharacteristicChangedCallBack(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

		}

	}

	public void sendBroadcastForDeviceFound(BluetoothDevice device, int rssi) {
		Intent intent = new Intent(ACTION_DEVICE_FOUND);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_RSSI, rssi);
		sendBroadcast(intent);
	}
	
	public void sendBroadcastForConnectionState(BluetoothGatt gatt, int newState) {
		Intent intent = new Intent(ACTION_CONNECTION_STATE);
		intent.putExtra(EXTRA_CONNECT_STATE, newState);
		sendBroadcast(intent);
		
	}

	private void reconnect(BluetoothGatt gatt) {
			mSimpleBluetooth.startScan();
			isScanning= true;
	}

	public void sendBroadcastForRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
		Intent intent = new Intent(ACTION_REMOTE_RSSI);
		intent.putExtra(EXTRA_RSSI, rssi);
		sendBroadcast(intent);
	}

	public void sendBroadcastForServiceDiscovered(BluetoothGatt gatt, int status) {
		Intent intent = new Intent(ACTION_SERVICE_DISCOVERED);
		intent.putExtra(EXTRA_DEVICE, gatt.getDevice());
		sendBroadcast(intent);
	}

}
