package com.mycj.mywatch.service;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.conn.ConnectError;
import com.litesuits.bluetooth.conn.ConnectListener;
import com.litesuits.bluetooth.conn.ConnectState;
import com.litesuits.bluetooth.conn.TimeoutCallback;
import com.litesuits.bluetooth.scan.PeriodScanCallback;
import com.mycj.mywatch.BaseApp;
import com.mycj.mywatch.DialogActivity;
import com.mycj.mywatch.MainActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.bean.Constant;
import com.mycj.mywatch.util.SharedPreferenceUtil;

public abstract class AbstractLiteBlueService extends Service {
	/** Intent for broadcast */
	public static final String LITE_GATT_DEVICE_FOUND = "com.liteblue.device_found";
	public static final String LITE_GATT_CONNECTED = "com.liteblue.gatt_connected";
	public static final String LITE_GATT_DISCONNECTED = "com.liteblue.gatt_disconnected";
	public static final String LITE_GATT_SERVICE_DISCOVERED = "com.liteblue.service_discovered";
	public static final String LITE_GATT_SERVICE_DISCOVERING = "com.liteblue.service_discovering";
	public static final String LITE_CHARACTERISTIC_READ = "com.liteblue.characteristic_read";
	public static final String LITE_CHARACTERISTIC_NOTIFICATION = "com.liteblue.characteristic_notification";
	public static final String LITE_CHARACTERISTIC_WRITE = "com.liteblue.characteristic_write";
	public static final String LITE_CHARACTERISTIC_CHANGED = "com.liteblue.characteristic_changed";
	public static final String LITE_CHARACTERISTIC_RSSI_CHANGED = "com.liteblue.characteristic_rssi";

	public static final String LITE_READ_RSSI = "com.liteblue.read_rssi";
	public static final String LITE_SCANNING = "com.liteblue.scanning";
	public static final String LITE_STATE_CHANGE = "com.liteblue.state_change";
	public static final String LITE_GATT_CONNECTTING = "com.liteblue.gatt_connectting";
	public static final String SHARE_BINDING_DEVICE_NAME = "share_binding_device_name";
	public static final String SHARE_BINDING_DEVICE_ADRESS = "share_binding_device_address";
	/** Intent extras */
	public static final String EXTRA_DEVICE = "DEVICE";
	public static final String EXTRA_RSSI = "RSSI";
	public static final String EXTRA_ADDR = "ADDRESS";
	public static final String EXTRA_CONNECTED = "CONNECTED";
	public static final String EXTRA_STATUS = "STATUS";
	public static final String EXTRA_UUID = "UUID";
	public static final String EXTRA_VALUE = "VALUE";
	public static final String EXTRA_REQUEST = "REQUEST";
	public static final String EXTRA_REASON = "REASON";
	public static final String EXTRA_ERROR = "REASON";
	public static final String EXTRA_SCAN_RECORD = "SCAN_RECORD";
	public static final String EXTRA_STATE = "STATE";

	public static final String DESC_CCC = "00002902-0000-1000-8000-00805f9b34fb";
	//
	public static String BLE_SERVICE;
	public static String BLE_CHARACTERISTIC_NOTIFY;
	public static String BLE_CHARACTERISTIC_WRITE;

	private final MyBinder mBinder = new MyBinder();

	private LiteBluetooth mLiteBluetooth;
	private MyLiteBlueConnectListener mConnLisener;
	private ConnectState currentState;
	private BluetoothDevice currentBluetoothDevice;
	private BluetoothGatt mBluetoothGatt;
	private boolean isScanning;

	/**
	 * characteristic写入特性
	 */
	private BluetoothGattCharacteristic currentCharacteristicWrite;

	private BluetoothAdapter mBluetoothAdapter;

	private MyPeriodScanCallback mPeriodScanCallback;
	private DefaultScanCallBack leCallBack = new DefaultScanCallBack();

	public static IntentFilter getIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(LITE_GATT_DEVICE_FOUND);
		intentFilter.addAction(LITE_GATT_CONNECTED);
		intentFilter.addAction(LITE_GATT_DISCONNECTED);
		intentFilter.addAction(LITE_GATT_SERVICE_DISCOVERED);
		intentFilter.addAction(LITE_CHARACTERISTIC_READ);
		intentFilter.addAction(LITE_CHARACTERISTIC_NOTIFICATION);
		intentFilter.addAction(LITE_CHARACTERISTIC_WRITE);
		intentFilter.addAction(LITE_CHARACTERISTIC_CHANGED);
		intentFilter.addAction(LITE_READ_RSSI);
		intentFilter.addAction(LITE_SCANNING);
		intentFilter.addAction(LITE_STATE_CHANGE);
		intentFilter.addAction(LITE_GATT_CONNECTTING);
		intentFilter.addAction(LITE_CHARACTERISTIC_RSSI_CHANGED);
		intentFilter.addAction(LITE_GATT_SERVICE_DISCOVERING);
		return intentFilter;
	}

	public boolean isConnected() {
		return currentState != null && currentState == ConnectState.Connected;
	}

	public boolean isServiceDiscovered() {
		return currentState != null && currentState == ConnectState.ServiceDiscovered;
	}

	public boolean isServiceDiscovering() {
		return currentState != null && currentState == ConnectState.ServiceDiscovering;
	}

	public void startScanUsePeriodScanCallback() {
		Log.i("LiteBlueService", "开始搜索！");
		mLiteBluetooth.stopScan(mPeriodScanCallback);
		mLiteBluetooth.startScan(mPeriodScanCallback);
		isScanning = true;
	}

	public void stopScanUsePeriodScanCallback() {
		Log.i("LiteBlueService", "结束搜索！");
		mLiteBluetooth.stopScan(mPeriodScanCallback);
		isScanning = false;
	}

	public void startScan() {
		mBluetoothAdapter.startLeScan(leCallBack);
		isScanning = true;
	}

	public void stopScan() {
		mBluetoothAdapter.stopLeScan(leCallBack);
		isScanning = false;
	}

	public boolean isScanning() {
		return isScanning;
	}

	public void closeAll() {
		Log.i("LiteBlueService", "关闭所有链接！");
		currentBluetoothDevice = null;
		mLiteBluetooth.closeAllConnects();
	}

	public void readRemoteRssi() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.readRemoteRssi();
		}
	}

	public void connnect(BluetoothDevice device) {
		// if (mBluetoothGatt != null) {
		// mConnLisener.closeBluetoothGatt(mBluetoothGatt);
		// }
		mLiteBluetooth.connect(device, true, mConnLisener);
	}

	public void openBluetooth() {
		if (!isEnable()) {
			Toast.makeText(getApplicationContext(), "请打开蓝牙", Toast.LENGTH_SHORT).show();
			// mLiteBlueService.enable(MainActivity.this);
		} else {
			stopScanUsePeriodScanCallback();
			startScanUsePeriodScanCallback();
		}
	}

	public boolean isEnable() {
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Log.i("BleManager", "getBlueState()--蓝牙未打开");
			return false;
		} else {
			return true;
		}
	}

	public void enable() {
		mBluetoothAdapter.enable();
	}

	public void saveBindedDevie(BluetoothDevice device) {
		String name = "";
		String address = "";
		if (device != null) {
			Log.e("", "device.name" + device.getName() + ":" + device.getAddress());
			name = device.getName();
			address = device.getAddress();
		}
		SharedPreferenceUtil.put(this, SHARE_BINDING_DEVICE_NAME, name);
		SharedPreferenceUtil.put(this, SHARE_BINDING_DEVICE_ADRESS, address);
	}

	public void setCurrentBluetoothDevice(BluetoothDevice currentBluetoothDevice) {
		this.currentBluetoothDevice = currentBluetoothDevice;
	}

	public BluetoothDevice getCurrentBluetoothDevice() {
		return currentBluetoothDevice;
	}

	/**
	 * 写入数据
	 * 
	 * @param address
	 * @param value
	 */
	public synchronized void writeCharacticsUseConnectListener(byte[] value) {
		synchronized (currentCharacteristicWrite) {
			try {
				if (currentCharacteristicWrite == null) {
					Log.e("BleManager", "writeCharactics()--没有找到write属性");
					return;
				}
				mConnLisener.characteristicWrite(mBluetoothGatt, currentCharacteristicWrite, value, new TimeoutCallback() {
					@Override
					public void onTimeout(BluetoothGatt gatt) {
						// Log.e("BleManager", "writeCharactics()超时...");
					}
				});
				Log.v("LiteBlueService", "writeCharactics()--写入write属性");
			} catch (Exception e) {
				Log.e("LiteBlueService", "writeCharactics()--写入数据失败...");
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param currentState
	 *            the currentState to set
	 */
	public void setCurrentState(ConnectState currentState) {
		this.currentState = currentState;
	}

	/**
	 * @return the currentState
	 */
	public ConnectState getCurrentState() {
		return currentState;
	}

	public void enable(Activity activity) {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		// startActivityForResult(enableBtIntent,1);
		// startActivity(enableBtIntent);
		activity.startActivityForResult(enableBtIntent, 1);
	}

	/**
	 * 解析数据
	 * 
	 * @param data
	 */
	public abstract void parseData(byte[] data);

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			default:
				break;
			}
		};
	};

	private int writeInterval = 200; // 100 ms

	/**
	 * 写入数据 旧
	 * 
	 * @param address
	 * @param value
	 */
	// public synchronized void writeCharacticsUseConnectListener(byte[] value)
	// {
	// try {
	// if (currentCharacteristicWrite == null) {
	// Log.e("BleManager", "writeCharactics()--没有找到write属性");
	// return;
	// }
	// mConnLisener.characteristicWrite(mBluetoothGatt,
	// currentCharacteristicWrite, value, new TimeoutCallback() {
	// @Override
	// public void onTimeout(BluetoothGatt gatt) {
	// Log.e("BleManager", "writeCharactics()超时...");
	// }
	// });
	// Log.v("BleManager", "writeCharactics()--写入write属性");
	// } catch (Exception e) {
	// Log.e("BleManager", "writeCharactics()--写入数据失败...");
	// e.printStackTrace();
	// }
	// }

	@Override
	public IBinder onBind(Intent intent) {
		Log.e("LiteBlueService", "==onBind==");
		return mBinder;
	}

	public MusicService musicService;

	@Override
	public void onCreate() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mPeriodScanCallback = new MyPeriodScanCallback(10000, mBluetoothAdapter);
		mLiteBluetooth = new LiteBluetooth(getApplicationContext());
		Log.i("LiteBlueService", "==onCreate==");
		mConnLisener = new MyLiteBlueConnectListener();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {

		Log.i("LiteBlueService", "==onDestroy==");
		currentBluetoothDevice = null;
		super.onDestroy();
	}

	protected void bleStateChanged(ConnectState state) {
		Intent intent = new Intent(LITE_STATE_CHANGE);
		intent.putExtra(EXTRA_STATE, state);
		sendBroadcast(intent);
	}

	protected void bleDeviceFound(BluetoothDevice device, int rssi, byte[] scanRecord) {
		Intent intent = new Intent(LITE_GATT_DEVICE_FOUND);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_RSSI, rssi);
		intent.putExtra(EXTRA_SCAN_RECORD, scanRecord);
		sendBroadcast(intent);
	}

	protected void bleGattConnected() {
		Intent intent = new Intent(LITE_GATT_CONNECTED);
		sendBroadcast(intent);
	}

	protected void bleGattServiceDiscovering() {
		Intent intent = new Intent(LITE_GATT_SERVICE_DISCOVERING);
		sendBroadcast(intent);
	}

	protected void bleGattDisConnected() {
		Intent intent = new Intent(LITE_GATT_DISCONNECTED);
		sendBroadcast(intent);
	}

	protected void bleCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

	}

	protected void bleGattServiceDiscovered(BluetoothGatt gatt) {
		Intent intent = new Intent(LITE_GATT_SERVICE_DISCOVERED);
		BluetoothDevice device = gatt.getDevice();
		intent.putExtra(EXTRA_DEVICE, device);
		sendBroadcast(intent);
	}

	protected void bleConnectting() {
		Intent intent = new Intent(LITE_GATT_CONNECTTING);
		sendBroadcast(intent);

	}

	protected void bleCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		byte[] value = characteristic.getValue();
		Intent intent = new Intent(LITE_CHARACTERISTIC_CHANGED);
		intent.putExtra(EXTRA_VALUE, value);
		sendBroadcast(intent);

	}

	protected void bleScanning() {
		Intent intent = new Intent(LITE_SCANNING);
		sendBroadcast(intent);
	}

	protected void bleReadRemoteRssi(int rssi) {
		Intent intent = new Intent(LITE_CHARACTERISTIC_RSSI_CHANGED);
		intent.putExtra(EXTRA_RSSI, rssi);
		sendBroadcast(intent);
	}

	public class MyBinder extends Binder {
		public AbstractLiteBlueService getService() {
			return AbstractLiteBlueService.this;
		}
	}

	private void createNotify() {
		Intent editIntent = new Intent(this, MainActivity.class);
		editIntent.putExtra("notify", 0xDEF);
		editIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent mClick = PendingIntent.getActivity(this, 0x001, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
				.setSmallIcon(R.drawable.ic_launcher).setContentTitle(getResources().getString(R.string.app_name)).setTicker("手环断线").setContentText("手机与手环连接断开")
				// .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
				.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.a2)).setContentIntent(mClick).setAutoCancel(true).setOnlyAlertOnce(true);
		// notification.defaults |= Notification.DEFAULT_LIGHTS; // 通知灯光
		// notification.defaults |= Notification.DEFAULT_VIBRATE; // 震动
		NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nManager.notify(0xDEF, mBuilder.build());
	}

	// /**
	// * 一直响铃
	// *
	// * @param droidGap
	// * @param player
	// * @return
	// * @throws Exception
	// * @throws IOException
	// */
	// private MediaPlayer ring(MediaPlayer player) throws Exception,
	// IOException {
	// Uri alert =
	// RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	// // MediaPlayer player = new MediaPlayer();
	// player.setDataSource(getApplicationContext(), alert);
	// final AudioManager audioManager = (AudioManager)
	// getSystemService(Context.AUDIO_SERVICE);
	// if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0)
	// {
	// player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
	// player.setLooping(true);
	// player.prepare();
	// player.start();
	// }
	// return player;
	// }
	
	
	
	public void startDialogActivity(String title, String msg, String btn) {
		Intent it =new Intent(this,DialogActivity.class); 
		it.putExtra("title", title);
		it.putExtra("msg", msg);
		it.putExtra("btn", btn);
		 it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 startActivity(it);
	}
	public void showSystemDialog(String title, String msg, String btn) {
		
		
		Log.e("", "service创建窗口");
		View v = LayoutInflater.from(this).inflate(R.layout.view_service_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(v);
		final AlertDialog dialog = builder.create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		// d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
		dialog.show();
		/* set size & pos */
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (display.getHeight() > display.getWidth()) {
			// lp.height = (int) (display.getHeight() * 0.5);
			lp.width = (int) (display.getWidth() * 1.0);
		} else {
			// lp.height = (int) (display.getHeight() * 0.75);
			lp.width = (int) (display.getWidth() * 0.5);
		}
		dialog.getWindow().setAttributes(lp);
		TextView tvBtn = (TextView) dialog.getWindow().findViewById(R.id.btn_neg);
		TextView tvMsg = (TextView) dialog.getWindow().findViewById(R.id.txt_msg);
		TextView tvTitle = (TextView) dialog.getWindow().findViewById(R.id.txt_title);
		tvBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (musicService!=null&&musicService.isPlaying()) {
					musicService.stop();
				}
			}
		});
		tvBtn.setText(btn);
		tvMsg.setText(msg);
		tvTitle.setText(title);
		/* update ui data */
		// lv = (ListView) d.getWindow().findViewById(R.id.listview);
		// SimpleAdapter adapter = new SimpleAdapter(mContext, getListData(),
		// R.layout.list_item,
		// new String[]{"item_text", "item_img"},
		// new int[]{R.id.item_text, R.id.item_img});
		// lv.setAdapter(adapter);
		//
		// /* set listener */
		// lv.setOnItemClickListener(new OnItemClickListener() {
		// public void onItemClick(AdapterView<?> parent, View view, int pos,
		// long id) {
		// d.dismiss();
		// }
		// });

	}

	private void notifycationDisconnected() {
		// mgr = (NotificationManager)
		// getSystemService(Context.NOTIFICATION_SERVICE);
		// PendingIntent intent =
		// PendingIntent.getActivity(getApplicationContext(), 0xefd, new
		// Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		// Notification nt = new Notification();
		// // nt.defaults = Notification.DEFAULT_SOUND;
		// nt.tickerText = "手环断开...";
		// nt.flags = Notification.FLAG_AUTO_CANCEL;
		// nt.contentIntent = intent;
		// // nt.setLatestEventInfo(
		// // this,
		// // "Hello,there!",
		// // "Hello,there,I'm john.",
		// // intent);
		// mgr.notify(0xDEF, nt);
		createNotify();
		// AlertDialog setNegativeButton = new
		// AlertDialog(this).builder().setMsg("手环连接断开").setCancelable(false).setTitle("手环连接断开").setNegativeButton("返回",
		// new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (musicService!=null&&musicService.isPlaying()) {
		// musicService.stop();
		// musicService.release();
		// }
		// }
		// });
		// setNegativeButton.show();
		// 使用来电铃声的铃声路径

		// try {
		// MediaPlayer player = ring(new MediaPlayer());
		// if (player.isPlaying()) {
		// player.reset(); // 到初始化状态，这里需要判断是否正在响铃，如果直接在开启一次会出现2个铃声一直循环响起，您不信可以尝试
		// } else if (!player.isPlaying()) {
		// ring(player);
		// }
		// } catch (Exception e) {
		// e.printStackTrace(); // To change body of catch statement use File |
		// // Settings | File Templates.
		// }

		// Uri uri =
		// RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		// MediaPlayer mMediaPlayer = null;
		// try {
		// if (mMediaPlayer == null) {
		// mMediaPlayer = new MediaPlayer();
		// }
		// if (mMediaPlayer.isPlaying()) {
		// mMediaPlayer.reset(); //
		// } else if (!mMediaPlayer.isPlaying()) {
		// mMediaPlayer.setDataSource(this, uri);
		// mMediaPlayer.setLooping(false); // 循环播放
		// mMediaPlayer.prepare();
		// mMediaPlayer.start();
		// }
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// } catch (IllegalStateException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	private class DefaultScanCallBack implements BluetoothAdapter.LeScanCallback {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			Log.i("", "搜索————--------");
			bleDeviceFound(device, rssi, scanRecord);
		}

	}

	public String getBindedDeviceName() {
		return (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_BINDING_DEVICE_NAME, "");
	}

	public String getBindedDeviceAddress() {
		return (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_BINDING_DEVICE_ADRESS, "");
	}

	/**
	 * 搜索回调Callback
	 * 
	 * @author Administrator
	 *
	 */
	private class MyPeriodScanCallback extends PeriodScanCallback {

		protected MyPeriodScanCallback(long timeoutMillis, BluetoothAdapter adapter) {
			super(timeoutMillis, adapter);
		}

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			String name = getBindedDeviceName();
			String address = getBindedDeviceAddress();
			if (name.equals(device.getName()) && address.equals(device.getAddress())) {
				Log.i("LiteBlueService", "name : address " + (name.equals("") ? "无绑定" : name) + " : " + (address.equals("") ? "无绑定" : address));
				connnect(device);
			}
			bleDeviceFound(device, rssi, scanRecord);
		}

		@Override
		public void onScanTimeout() {

			if (!isServiceDiscovered()) {

				// 搜索设备超时,延迟2秒，再次搜索。
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						startScanUsePeriodScanCallback();
					}
				}, 2000);
			}
		}

	}

	/**
	 * 是否绑定
	 * 
	 * @return
	 */
	public boolean isBinded() {
		String name = getBindedDeviceName();
		String address = getBindedDeviceAddress();
		if (!name.equals("") && !address.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * LiteBlue ConnectListener
	 * 
	 * @author Administrator
	 */
	private class MyLiteBlueConnectListener extends ConnectListener {

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			Log.i("LiteBlueService", "<<<< onCharacteristicChanged() >>>>");
			bleCharacteristicChanged(gatt, characteristic);
			byte[] data = characteristic.getValue();
			parseData(data);
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			Log.i("LiteBlueService", "<<<< onCharacteristicWrite() >>>>");
			bleCharacteristicWrite(gatt, characteristic, status);
		}

		@Override
		public void onFailed(ConnectError error) {
			Log.i("LiteBlueService", "<<<< onFailed()--ConnectError : [" + error.getCode() + "," + error.getMessage() + "] >>>>");
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt) {
			mBluetoothGatt = gatt;
			Log.i("LiteBlueService", "<<<< onServicesDiscovered()--BluetoothGatt.services : [" + gatt.getServices() + "] >>>>");
			setCurrentBluetoothDevice(gatt.getDevice());

			BluetoothGattService service = gatt.getService(UUID.fromString(BLE_SERVICE));

			if (service != null) {
				BluetoothGattCharacteristic characteristicNotify = service.getCharacteristic(UUID.fromString(BLE_CHARACTERISTIC_NOTIFY));
				currentCharacteristicWrite = service.getCharacteristic(UUID.fromString(BLE_CHARACTERISTIC_WRITE));
				Log.i("LiteBlueService", "找到了指定的service！！");
				if (characteristicNotify != null && currentCharacteristicWrite != null) {
					enableCharacteristicNotification(gatt, characteristicNotify, DESC_CCC);
					stopScanUsePeriodScanCallback();
					saveBindedDevie(gatt.getDevice());
					Log.i("LiteBlueService", "找到了指定的characteristic-Write！！");
					Log.i("LiteBlueService", "找到了指定的characteristic-Notify！！");
					bleGattServiceDiscovered(gatt);
				} else {
					if (mLiteBluetooth != null) {
						mBluetoothGatt.close();
						mBluetoothGatt = null;
					}
				}
			}
		}

		/*
		 * 
		 * Initialed(0, "初始化状态：连接未建立"), Scanning(1, "扫描中..."), Connecting(2,
		 * "设备连接中..."), Connected(3, "设备已连接"), ServiceDiscovering(4,
		 * "服务发现中..."), ServiceDiscovered(5, "已发现服务"), DisConnected(6, "连接已断开");
		 */
		@Override
		public void onStateChanged(ConnectState state) {

			Log.i("LiteBlueService", "<<<< onStateChanged()--ConnectState : [" + state.getCode() + "," + state.getMessage() + "] >>>>");
			switch (state.getCode()) {
			case 0:
				currentState = ConnectState.Initialed;
				break;
			case 1:
				currentState = ConnectState.Scanning;
				bleScanning();
				break;
			case 2:
				currentState = ConnectState.Connecting;
				bleConnectting();
				break;
			case 3:
				currentState = ConnectState.Connected;
				bleGattConnected();
				break;
			case 4:
				currentState = ConnectState.ServiceDiscovering;
				bleGattServiceDiscovering();
				break;
			case 5:
				currentState = ConnectState.ServiceDiscovered;
				break;
			case 6:
				currentState = ConnectState.DisConnected;

				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (currentState == ConnectState.Connected || currentState == ConnectState.ServiceDiscovered) {
							// 不想令
						} else {
							// 香呤
//							showSystemDialog("手段断线", "手环断线了。", "关闭");
							startDialogActivity("手段断线", "手环断线了。", "关闭");
						}
					}

				}, 5000);
				currentBluetoothDevice = null;
				currentCharacteristicWrite = null;
				mBluetoothGatt.close();
				mBluetoothGatt = null;
				bleGattDisConnected();
				if (!isScanning) {
					startScanUsePeriodScanCallback();// 掉线时开始运行
				}
				break;

			default:
				break;
			}
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			Log.i("LiteBlueService", "<<<< onReadRemoteRssi()--rssi : [" + rssi + "] >>>>");
			bleReadRemoteRssi(rssi);
		}
	}

	private int writeCount = 0;
	private NotificationManager mgr;

	/**
	 * 写入属性
	 * 
	 * @param value
	 */
	public void writeValueToDevice(final byte[] value) {
		int actualTimeInterval = writeCount * writeInterval;
		Log.i("LiteBlueServiceTest", "" + actualTimeInterval);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mConnLisener.characteristicWrite(mBluetoothGatt, currentCharacteristicWrite, value, new TimeoutCallback() {

					@Override
					public void onTimeout(BluetoothGatt gatt) {

					}
				});
				Log.i("LiteBlueServiceTest", "writeValueToDevice()" + value);
				writeCount--;
				if (writeCount < 0) {
					writeCount = 0;
				}
			}
		}, actualTimeInterval);
		writeCount++;
	}
}
