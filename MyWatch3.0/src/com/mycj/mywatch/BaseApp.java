package com.mycj.mywatch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import com.mycj.mywatch.bean.Constant;
import com.mycj.mywatch.business.ProtocolForWrite;
import com.mycj.mywatch.service.AbstractLiteBlueService;
import com.mycj.mywatch.service.LiteBlueService;
import com.mycj.mywatch.service.MusicService;
import com.mycj.mywatch.service.SimpleBlueService;
import com.mycj.mywatch.util.MessageUtil;
import com.mycj.mywatch.util.SharedPreferenceUtil;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BaseApp extends Application {

	private SimpleBlueService mSimpleBlueService;
	private LiteBlueService mLiteBlueService;
	private MusicService mMusicService;

	/**
	 * 蓝牙服务通信
	 */
	private ServiceConnection liteBlueConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mLiteBlueService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mLiteBlueService = (LiteBlueService) ((AbstractLiteBlueService.MyBinder) service).getService();
		}
	};

	/**
	 * 音乐服务通信
	 */
	private ServiceConnection musicConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mMusicService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mMusicService = ((MusicService.MyBinder) service).getMusicService();
		}
	};

	private ServiceConnection mSimpleConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSimpleBlueService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mSimpleBlueService = ((SimpleBlueService.MyBinder) service).getService();
		}
	};

	/**
	 * 短信监听
	 */
	private ContentObserver newMmsContentObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange, Uri uri) {
			int mNewSmsCount = MessageUtil.getNewSmsCount(getApplicationContext()) + MessageUtil.getNewMmsCount(getApplicationContext());
			int phone = MessageUtil.readMissCall(getApplicationContext());
			Log.e("baseApp", "mNewSmsCount___________未读短信____________________" + mNewSmsCount);
			Log.e("baseApp", "mNewSmsCount___________未姐电话____________________" + phone);
			doWriteUnReadPhoneAndSmsToWatch(mNewSmsCount, phone);
			super.onChange(selfChange, uri);
			mHandle.removeCallbacks(task);
			mHandle.post(task);
		}
	};

	/**
	 * 电话监听
	 */
	private ContentObserver newCallContentObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange, Uri uri) {
			int mNewSmsCount = MessageUtil.getNewSmsCount(getApplicationContext()) + MessageUtil.getNewMmsCount(getApplicationContext());
			int phone = MessageUtil.readMissCall(getApplicationContext());
			Log.e("baseApp", "mNewSmsCount___________未读短信____________________" + mNewSmsCount);
			Log.e("baseApp", "mNewSmsCount___________未姐电话____________________" + phone);
			doWriteUnReadPhoneAndSmsToWatch(mNewSmsCount, phone);
			super.onChange(selfChange, uri);
			mHandle.removeCallbacks(task);
			mHandle.post(task);
		}

	};

	private TelephonyManager telephony;
	private final static int DIFF = 4 * 1000; // 4秒运行一次
	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			int mmsCount = MessageUtil.getNewMmsCount(getApplicationContext());
			int msmCount = MessageUtil.getNewSmsCount(getApplicationContext());
			int phoneCount = MessageUtil.readMissCall(getApplicationContext());

			Log.e("", "========================================phoneCount : " + phoneCount);
			if (mmsCount == 0 && msmCount == 0 && phoneCount == 0) {
				task.cancel();
				mHandle.removeCallbacks(task);
				doWriteUnReadPhoneAndSmsToWatch(0, 0);
			} else {
				mHandle.sendEmptyMessage(1);
			}
		}
	};
	private Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mHandle.postDelayed(task, DIFF);
				break;
			default:
				break;
			}
		};

	};


	/**
	 * 电话状态监听
	 */
	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			Log.v("BaseApp", "CallListener call state changed ---- [incomingNumber : " + incomingNumber + ",state : " + state + "]");
			// String m = null;
			// // 如果当前状态为空闲,上次状态为响铃中的话,则认为是未接来电
			// if (lastetState == TelephonyManager.CALL_STATE_RINGING && state
			// == TelephonyManager.CALL_STATE_IDLE) {
			// sendSmgWhenMissedCall(incomingNumber);
			// }
			// // 最后改变当前值
			// lastetState = state;
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				Log.v("BaseApp", "电话来了");
				doWriteIncomingPhoneToWatch(incomingNumber);
				mHandle.removeCallbacks(task);
				mHandle.post(task);
			}
		}
	};

	private BroadcastReceiver mPhoneReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			telephony.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
			
			if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
				Bundle bundle = intent.getExtras();
				SmsMessage msg = null;
				if (null != bundle) {
					Object[] smsObj = (Object[]) bundle.get("pdus");
					for (Object obj : smsObj) {
						msg = SmsMessage.createFromPdu((byte[]) obj);
						Date date = new Date(msg.getTimestampMillis());// 时间
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String receiveTime = format.format(date);
						String address = msg.getOriginatingAddress();
						System.out.println("number:" + address + "   body:" + msg.getDisplayMessageBody() + "  time:" + msg.getTimestampMillis());
						doWriteIncomingSmsToWatch(address);
					}
				}
			}
		};
	};


	@Override
	public void onCreate() {
		// 蓝牙服务
		Intent liteIntent = new Intent(this, LiteBlueService.class);
		bindService(liteIntent, liteBlueConn, Context.BIND_AUTO_CREATE);
		// 音乐服务
		Intent musicIntent = new Intent(this, MusicService.class);
		bindService(musicIntent, musicConnection, Context.BIND_AUTO_CREATE);
		// 服务
		// Intent simpleIntent = new Intent(this, SimpleBlueService.class);
		// bindService(simpleIntent, mSimpleConnection,
		// Context.BIND_AUTO_CREATE);
		telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		

			IntentFilter filter = new IntentFilter();
			filter.addAction("android.intent.action.PHONE_STATE");
			filter.addAction("android.provider.Telephony.SMS_RECEIVED");
			registerReceiver(mPhoneReceiver,filter);
			registerObserver();
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		unbindService(liteBlueConn);
		unbindService(musicConnection);
		// unbindService(mSimpleConnection);
		mSimpleBlueService.close();
		super.onTerminate();
	}

	/**
	 * 来电提醒
	 * 
	 * @param incomingNumber
	 */
	private void doWriteIncomingPhoneToWatch(String incomingNumber) {
		boolean isCallRemind;isCallRemind = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_CHECK_REMIND_CALL, false);
		if (isCallRemind&&mLiteBlueService.isServiceDiscovered() && mLiteBlueService.isBinded()) {
			mLiteBlueService.writeCharacticsUseConnectListener(ProtocolForWrite.instance().getByteForRemind(0x80, incomingNumber));
		}
	};

	/**
	 * 来短信提醒
	 */
	private void doWriteIncomingSmsToWatch(String incomingNumber) {
		boolean isCallRemind;isCallRemind = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_CHECK_REMIND_CALL, false);
		if (isCallRemind&&mLiteBlueService.isServiceDiscovered() && mLiteBlueService.isBinded()) {
			mLiteBlueService.writeCharacticsUseConnectListener(ProtocolForWrite.instance().getByteForRemind(0x20, incomingNumber));
		}
	}

	/**
	 * 未接来电和未读短信提醒
	 */
	private void doWriteUnReadPhoneAndSmsToWatch(int sms, int phone) {
		boolean isCallRemind;isCallRemind = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_CHECK_REMIND_CALL, false);
		if (isCallRemind&&mLiteBlueService.isServiceDiscovered() && mLiteBlueService.isBinded()) {
			mLiteBlueService.writeCharacticsUseConnectListener(ProtocolForWrite.instance().getByteForMissedCallAndMessage(sms, phone));
		}
	}

	/**
	 * 注册监听短信
	 */
	private void registerObserver() {
		unregisterObserver();
		// 在服务创建的时候注册ContentObserver，之后就会一直存在
		getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, newMmsContentObserver);
		getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, newCallContentObserver);
	}

	private void unregisterObserver() {
		try {
			if (newMmsContentObserver != null) {
				getContentResolver().unregisterContentObserver(newMmsContentObserver);
			}
			if (newCallContentObserver != null) {
				getContentResolver().unregisterContentObserver(newCallContentObserver);
			}
		} catch (Exception e) {
			Log.e("", "unregisterObserver fail");
		}
	}

	public LiteBlueService getBlueService() {
		return mLiteBlueService;
	}

	public MusicService getMusicService() {
		return mMusicService;
	}

	public SimpleBlueService getSimpleBlueService() {
		return mSimpleBlueService;
	}

}
