//package com.mycj.mywatch.service;
//
//
//import android.app.Service;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothGatt;
//import android.bluetooth.BluetoothGattCallback;
//import android.bluetooth.BluetoothGattCharacteristic;
//import android.bluetooth.BluetoothGattDescriptor;
//import android.bluetooth.BluetoothGattService;
//import android.bluetooth.BluetoothManager;
//import android.content.Intent;
//import android.os.Binder;
//import android.os.IBinder;
//import android.util.Log;
//import java.util.List;
//import java.util.UUID;
//
//public class BluetoothLeService extends Service {
//	public static final String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
//	public static final String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
//	public static final String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
//	public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
//	public static final String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
//	public static final String EXTRA_DATA_LEN = "com.example.bluetooth.le.EXTRA_DATA_LEN";
//	private static final int STATE_CONNECTED = 2;
//	private static final int STATE_CONNECTING = 1;
//	private static final int STATE_DISCONNECTED;
//	private static final String TAG = BluetoothLeService.class.getSimpleName();
//	public static final UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString("");
//	public static final UUID UUID_ISSC_RX = UUID.fromString("");
//	private final boolean D = false;
//	private final IBinder mBinder = new LocalBinder();
//	private BluetoothAdapter mBluetoothAdapter;
//	private String mBluetoothDeviceAddress;
//	private BluetoothGatt mBluetoothGatt;
//	private BluetoothManager mBluetoothManager;
//	private int mConnectionState = 0;
//	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
//		public void onCharacteristicChanged(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic) {
//			BluetoothLeService.this.broadcastUpdate("com.example.bluetooth.le.ACTION_DATA_AVAILABLE", paramBluetoothGattCharacteristic);
//		}
//
//		public void onCharacteristicRead(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, int paramInt) {
//			if (paramInt == 0)
//				BluetoothLeService.this.broadcastUpdate("com.example.bluetooth.le.ACTION_DATA_AVAILABLE", paramBluetoothGattCharacteristic);
//		}
//
//		public void onCharacteristicWrite(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, int paramInt) {
//			super.onCharacteristicWrite(paramBluetoothGatt, paramBluetoothGattCharacteristic, paramInt);
//		}
//
//		public void onConnectionStateChange(BluetoothGatt paramBluetoothGatt, int paramInt1, int paramInt2) {
//			if (paramInt2 == 2) {
//				BluetoothLeService.this.mConnectionState = 2;
//				BluetoothLeService.this.broadcastUpdate("com.example.bluetooth.le.ACTION_GATT_CONNECTED");
//				BluetoothLeService.this.mBluetoothGatt.discoverServices();
//			}
//			do
//				return;
//			while (paramInt2 != 0);
//			BluetoothLeService.this.mConnectionState = 0;
//			BluetoothLeService.this.broadcastUpdate("com.example.bluetooth.le.ACTION_GATT_DISCONNECTED");
//		}
//
//		public void onReadRemoteRssi(BluetoothGatt paramBluetoothGatt, int paramInt1, int paramInt2) {
//			BluetoothLeService.this.m_rssi = paramInt1;
//		}
//
//		public void onReliableWriteCompleted(BluetoothGatt paramBluetoothGatt, int paramInt) {
//			super.onReliableWriteCompleted(paramBluetoothGatt, paramInt);
//		}
//
//		public void onServicesDiscovered(BluetoothGatt paramBluetoothGatt, int paramInt) {
//			if (paramInt == 0)
//				BluetoothLeService.this.broadcastUpdate("com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED");
//		}
//	};
//	public int m_rssi;
//
//	private void broadcastUpdate(String paramString) {
//		sendBroadcast(new Intent(paramString));
//	}
//
//	private void broadcastUpdate(String paramString, BluetoothGattCharacteristic paramBluetoothGattCharacteristic) {
//		Intent localIntent = new Intent(paramString);
//		int k;
//		if (UUID_HEART_RATE_MEASUREMENT.equals(paramBluetoothGattCharacteristic.getUuid()))
//			if ((0x1 & paramBluetoothGattCharacteristic.getProperties()) != 0) {
//				k = 18;
//				localIntent.putExtra("com.example.bluetooth.le.EXTRA_DATA", String.valueOf(paramBluetoothGattCharacteristic.getIntValue(k, 1).intValue()));
//			}
//		byte[] arrayOfByte;
//		while (true) {
//			sendBroadcast(localIntent);
//			return;
//			k = 17;
//			break;
//			arrayOfByte = paramBluetoothGattCharacteristic.getValue();
//			if ((arrayOfByte == null) || (arrayOfByte.length <= 0))
//				continue;
//			// if (Public.b_hex)
//			if (true)
//				break label138;
//			localIntent.putExtra("com.example.bluetooth.le.EXTRA_DATA", new String(arrayOfByte) + "\n");
//			localIntent.putExtra("com.example.bluetooth.le.EXTRA_DATA_LEN", arrayOfByte.length);
//		}
//		// label138: StringBuilder localStringBuilder = new
//		// StringBuilder(arrayOfByte.length);
//		int i = arrayOfByte.length;
//		for (int j = 0;; j++) {
//			if (j >= i) {
//				// localIntent.putExtra("com.example.bluetooth.le.EXTRA_DATA",
//				// localStringBuilder.toString() + "\n");
//				break;
//			}
//			byte b = arrayOfByte[j];
//			Object[] arrayOfObject = new Object[1];
//			arrayOfObject[0] = Byte.valueOf(b);
//			// localStringBuilder.append(String.format("%02X ", arrayOfObject));
//		}
//	}
//
//	public void close() {
//		if (this.mBluetoothGatt == null)
//			return;
//		this.mBluetoothGatt.close();
//		this.mBluetoothGatt = null;
//	}
//
//	public boolean connect(String paramString) {
//		if ((this.mBluetoothAdapter == null) || (paramString == null))
//			return false;
//		if ((this.mBluetoothDeviceAddress != null) && (paramString.equals(this.mBluetoothDeviceAddress)) && (this.mBluetoothGatt != null)) {
//			if (this.mBluetoothGatt.connect()) {
//				this.mConnectionState = 1;
//				return true;
//			}
//			return false;
//		}
//		BluetoothDevice localBluetoothDevice = this.mBluetoothAdapter.getRemoteDevice(paramString);
//		if (localBluetoothDevice == null)
//			return false;
//		this.mBluetoothGatt = localBluetoothDevice.connectGatt(this, false, this.mGattCallback);
//		this.mBluetoothDeviceAddress = paramString;
//		this.mConnectionState = 1;
//		return true;
//	}
//
//	public void disconnect() {
//		if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null))
//			return;
//		this.mBluetoothGatt.disconnect();
//	}
//
//	public List<BluetoothGattService> getSupportedGattServices() {
//		if (this.mBluetoothGatt == null)
//			return null;
//		return this.mBluetoothGatt.getServices();
//	}
//
//	public int get_rssi() {
//		return this.m_rssi;
//	}
//
//	public boolean initialize() {
//		if (this.mBluetoothManager == null) {
//			this.mBluetoothManager = ((BluetoothManager) getSystemService("bluetooth"));
//			if (this.mBluetoothManager == null)
//				return false;
//		}
//		this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
//		return this.mBluetoothAdapter != null;
//	}
//
//	public IBinder onBind(Intent paramIntent) {
//		return this.mBinder;
//	}
//
//	public boolean onUnbind(Intent paramIntent) {
//		close();
//		return super.onUnbind(paramIntent);
//	}
//
//	public void readCharacteristic(BluetoothGattCharacteristic paramBluetoothGattCharacteristic) {
//		if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null))
//			return;
//		this.mBluetoothGatt.readCharacteristic(paramBluetoothGattCharacteristic);
//	}
//
//	public void setCharacteristicNotification(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, boolean paramBoolean) {
//		if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null))
//			;
//		BluetoothGattDescriptor localBluetoothGattDescriptor1;
//		do {
//			do {
//				return;
//				this.mBluetoothGatt.setCharacteristicNotification(paramBluetoothGattCharacteristic, paramBoolean);
//				if (!UUID_HEART_RATE_MEASUREMENT.equals(paramBluetoothGattCharacteristic.getUuid()))
//					continue;
//				BluetoothGattDescriptor localBluetoothGattDescriptor2 = paramBluetoothGattCharacteristic.getDescriptor(UUID.fromString(""));
//				localBluetoothGattDescriptor2.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//				this.mBluetoothGatt.writeDescriptor(localBluetoothGattDescriptor2);
//			} while (!UUID_ISSC_RX.equals(paramBluetoothGattCharacteristic.getUuid()));
//			localBluetoothGattDescriptor1 = paramBluetoothGattCharacteristic.getDescriptor(UUID.fromString(""));
//		} while (localBluetoothGattDescriptor1 == null);
//		localBluetoothGattDescriptor1.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//		this.mBluetoothGatt.writeDescriptor(localBluetoothGattDescriptor1);
//	}
//
//	public void update_rssi() {
//		if (this.mBluetoothGatt != null)
//			this.mBluetoothGatt.readRemoteRssi();
//	}
//
//	public void writeCharacteristic(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, byte[] paramArrayOfByte) {
//		if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null))
//			Log.w(TAG, "BluetoothAdapter not initialized");
//		if ((0x4 | paramBluetoothGattCharacteristic.getProperties()) > 0)
//			paramBluetoothGattCharacteristic.setWriteType(1);
//		while (true) {
//			paramBluetoothGattCharacteristic.setValue(paramArrayOfByte);
//			this.mBluetoothGatt.writeCharacteristic(paramBluetoothGattCharacteristic);
//			return;
//			paramBluetoothGattCharacteristic.setWriteType(2);
//		}
//	}
//
//	public class LocalBinder extends Binder {
//		public LocalBinder() {
//		}
//
//		BluetoothLeService getService() {
//			return BluetoothLeService.this;
//		}
//	}
//}
//
///*
// * Location:
// * C:\Users\Administrator\Desktop\JessTech_Ble_Tool_v1.4_classes_dex2jar.jar
// * Qualified Name: com.jesstech.bluetooth.bledemo.BluetoothLeService JD-Core
// * Version: 0.6.0
// */