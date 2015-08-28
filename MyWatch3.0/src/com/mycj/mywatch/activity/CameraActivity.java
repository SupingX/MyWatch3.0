package com.mycj.mywatch.activity;

import com.mycj.mywatch.BaseActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.business.CameraManager;
import com.mycj.mywatch.business.CameraManager.CameraOpenReady;
import com.mycj.mywatch.service.AbstractLiteBlueService;
import com.mycj.mywatch.service.LiteBlueService;
import com.mycj.mywatch.util.DisplayUtil;
import com.mycj.mywatch.view.CameraSurfaceView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class CameraActivity extends BaseActivity implements OnClickListener {
	private final String TAG = "CameraActivity";
	private FrameLayout frame;
	private CameraSurfaceView cameraSurfaceView;
	private ImageView imgTakePicture;
	private float previewRate = -1f;
	private CameraManager mCameraManager;
	private Handler mHandler = new Handler() {

	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(LiteBlueService.LITE_DATA_CAMERA)) {
				int notifyForCamera = intent.getExtras().getInt(LiteBlueService.EXTRA_HEART_RATE);
				switch (notifyForCamera) {
				case 0x00:
					mHandler.post(takePictureThread);
					break;
				case 0x01:
					finish();
					break;

				default:
					break;
				}
			}

		}
	};

	private Runnable openThread = new Runnable() {
	
		@Override
		public void run() {
			CameraManager.instance().openCamera(new CameraOpenReady() {
				// 打开完成后，开启预览
				@Override
				public void openReady() {
					SurfaceHolder mSurfaceHolder = cameraSurfaceView.getSurfaceHolder();
					boolean isSurfaceCreate = mSurfaceHolder.isCreating();
					Log.d(TAG, "mSurfaceHolder : " + isSurfaceCreate);
					CameraManager.instance().startPreview(mSurfaceHolder, previewRate);
				}
			});
		}
	};

	private Runnable takePictureThread = new Runnable() {
	
		@Override
		public void run() {
			CameraManager.instance().takePicture();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 拍照过程屏幕一直处于高亮
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // //设置手机屏幕朝向，一共有7种

		setContentView(R.layout.activity_camera);
		registerReceiver(mReceiver, LiteBlueService.getIntentFilter());
		mCameraManager = CameraManager.instance();
		initViews();
		setListener();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// 打开相机
		// mHandler.post(openThread);
		new Thread(openThread).start();

	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(openThread);
		mHandler.removeCallbacks(takePictureThread);
		CameraManager.instance().stopPreviewCamera();
	}

	@Override
	public void initViews() {
		frame = (FrameLayout) findViewById(R.id.frame_camera);
		imgTakePicture = (ImageView) findViewById(R.id.img_take_picture);
		cameraSurfaceView = (CameraSurfaceView) findViewById(R.id.surface_camera);
		initCameraSurfaceViewParams();
	}

	private void initCameraSurfaceViewParams() {
		ViewGroup.LayoutParams params = cameraSurfaceView.getLayoutParams();
		Point p = DisplayUtil.getScreenMetrics(this);
		params.width = p.x;
		params.height = p.y;
		// 默认全屏的比例预览
		previewRate = DisplayUtil.getScreenRate(this);
		cameraSurfaceView.setLayoutParams(params);

	}

	@Override
	public void setListener() {
		imgTakePicture.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_take_picture:
			mHandler.post(takePictureThread);
			break;

		default:
			break;
		}
	}
}
