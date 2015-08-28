package com.mycj.mywatch.view;


import com.mycj.mywatch.business.CameraManager;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	
	private static final String TAG ="CameraSurfaceView";
	private SurfaceHolder mSurfaceHolder;
	public CameraSurfaceView(Context context) {
		super(context);
		init(context);
	}
	

	public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SurfaceHolder getSurfaceHolder(){
		return this.mSurfaceHolder;
	}

	private void init(Context context) {
		mSurfaceHolder = getHolder();
		//translucent 半透明 ；transparent透明
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 设置该surfaceView不用自己维护缓冲区
		
//		mSurfaceHolder.setFixedSize(480, 88);//设置分辨率
		mSurfaceHolder.addCallback(this);
	}


	/**
	 * from SurfaceView.CallBack 
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v(TAG, "<!-- surfaceChanged -->");
	
	}



	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(TAG, "<!-- surfaceCreated -->");
		
	}



	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(TAG, "<!-- surfaceDestroyed -->");
		   CameraManager.instance().stopPreviewCamera();  
	}
	

}
