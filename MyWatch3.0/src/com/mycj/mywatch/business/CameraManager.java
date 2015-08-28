package com.mycj.mywatch.business;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.litesuits.bluetooth.utils.FileUtil;
import com.litesuits.bluetooth.utils.ImageUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;

public class CameraManager {
	private final static String TAG = "Camera";
	private static CameraManager mCameraManager;
	private float previewRate = -1f;
	private Camera mCamera;
	private boolean isPreviewing = false;
	private Parameters parameters;
	private CameraSizeComparator sizeComparator = new CameraSizeComparator();

	private CameraManager() {

	}

	public static synchronized CameraManager instance() {
		if (mCameraManager == null) {
			mCameraManager = new CameraManager();
		}
		return mCameraManager;
	}

	/**
	 * 开启相机
	 * 
	 * @param callback
	 */
	public void openCamera(CameraOpenReady callback) {
		Log.v(TAG, "<-- 开启相机 -->");
		try {
//			int size = Camera.getNumberOfCameras();
//			Log.v(TAG, "<-- 相机个数:" + size + "-->");
			mCamera = Camera.open();
		} catch (Exception e) {
			Log.v(TAG, "<-- 开启相机  异常-->");
		}
		Log.v(TAG, "<-- 开启相机 :"+mCamera+" 已开启-->");
	
		callback.openReady();
	}

	/**
	 * 开启预览
	 * 
	 * @param mHolder
	 * @param previewRate
	 */
	public void startPreview(SurfaceHolder mHolder, float previewRate) {
		Log.v(TAG, "<-- 开启预览 -->");

		// 正在预览时，停止预览?
		if (isPreviewing) {
			Log.v(TAG, "<-- 正在预览中，关闭预览先 -->");
			stopPreview();
			return;
		}
		
		Log.v(TAG, "<-- mCamera：+"+mCamera+" -->");
		if (mCamera != null) {
	
				Log.v(TAG, "<-- 开启预览准备工作 设置参数 -->");
				parameters = mCamera.getParameters();
				// 设置存储的格式
				parameters.setPictureFormat(PixelFormat.JPEG);
				// parameters.setPreviewFrameRate(3);// 每秒3帧 每秒从摄像头里面获得3个画面
				// parameters.set("jpeg-quality", 85);// 设置照片质量
				// 打印支持的size
				printSupportPictureSize(parameters);
				printSupportPreviewSize(parameters);
				// 设置size
				Log.v(TAG, "<-- 设置pictureSize -->");
				Size pictureSize = getRightSize(parameters.getSupportedPictureSizes(), previewRate, 800);
				parameters.setPictureSize(pictureSize.width, pictureSize.height);
				Log.v(TAG, "<-- 设置previewSize -->");
				Size previewSize = getRightSize(parameters.getSupportedPreviewSizes(), previewRate, 800);
				parameters.setPreviewSize(previewSize.width, previewSize.height);
				// 旋转90度 3D Z轴
				Log.v(TAG, "<-- 设置旋转90度 -->");
				mCamera.setDisplayOrientation(90);
				// 打印聚焦模式
				printSupportFocusMode(parameters);
				// 设置聚焦模式
				List<String> modes = parameters.getSupportedFocusModes();
				if (modes.contains("continuous-video")) {
					Log.v(TAG, "<-- 设置聚焦模式 ：continuous-video -->");
					parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				}
				
				// 将参数设置 传给mCamera
				mCamera.setParameters(parameters);
				Log.v(TAG, "<-- 开启预览准备工作 参数设置成功 -->");
				try {
				// 设置holder
				mCamera.setPreviewDisplay(mHolder);// 把摄像头获得画面显示在SurfaceView控件里面

				// 开启预览
				mCamera.startPreview();
			} catch (Exception e) {
				Log.v(TAG, "<-- 预览异常 -->");
				// 预览异常
				mCamera.release();
				mCamera = null;
				isPreviewing = false;
			}

			// 预览成功
			isPreviewing = true;
			this.previewRate = previewRate;

			// 重新get一次
			parameters = mCamera.getParameters();
			// 最终设置
			Log.v(TAG, "<-- 最终预览设置值 -->");
			Log.v(TAG, "<-- preview @ width:" + parameters.getPreviewSize().width + ",height:" + parameters.getPreviewSize().height + " -->");
			Log.v(TAG, "<-- preview @ width:" + parameters.getPictureSize().width + ",height:" + parameters.getPictureSize().height + " -->");
			Log.v(TAG, "<-- 最终预览设置值 -->");
		}

	}

	/**
	 * 停止预览
	 */
	public void stopPreviewCamera() {
		Log.v(TAG, "<-- 停止预览 -->");
		if (null != mCamera) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			isPreviewing = false;
			previewRate = -1f;
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * 拍照
	 */
	public synchronized void takePicture() {
		Log.v(TAG, "<-- 拍照:" + isPreviewing + mCamera+" -->");
		if (isPreviewing && mCamera != null) {
			mCamera.takePicture(new MyShutterCallback(), null, new MyJpegPictureCallback());
		}
	}

	/** ↓↓↓↓↓↓↓↓↓↓↓↓↓ 为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量 ↓↓↓↓↓↓↓↓↓↓↓↓↓ **/
	// 快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
	private class MyShutterCallback implements ShutterCallback{

		@Override
		public void onShutter() {
				Log.v(TAG, "<--  咔嚓~ -->");
		}
		
	}
	// 拍摄的未压缩原数据的回调,可以为null
	private class MyRawPictureCallback implements Camera.PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.v(TAG, "<-- rawCallback  -->");
		} 
	
		
	};
	// 对jpeg图像数据的回调,最重要的一个回调
	private class MyJpegPictureCallback implements Camera.PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.v(TAG, "<-- jpegCallback  -->");
			Bitmap bitmap = null;
			if (null != data) {
				// 解析成位图
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				mCamera.stopPreview();
				isPreviewing = false;
			}
			// 保存图片到Sd—card
			if (null != bitmap) {
				// 旋转90度
				Bitmap rotaBitmap = ImageUtil.getRotateBitmap(bitmap, 90.0f);
				new SavePictureTask().execute(rotaBitmap);
			}

			// 再次进入预览
			mCamera.startPreview();
			isPreviewing = true;
			

		}
	};

	/** ↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑ **/

	private class SavePictureTask extends AsyncTask<Bitmap, Void, Void> {

		@Override
		protected Void doInBackground(Bitmap... params) {
			Log.v(TAG, "<-- 异步存储中...  -->");
			try {
				FileUtil.saveBitmap(params[0]);
			} catch (Exception e) {
				Log.v(TAG, "<-- 异步存储异常...  -->");
				e.printStackTrace();
			}
			return null;
		}

	}

	/**
	 * 
	 * @param list
	 * @param rate
	 * @param minWidth
	 * @return
	 */
	public Size getRightSize(List<Camera.Size> list, float rate, int minWidth) {
		// 排序,按照自定义的规则
		Collections.sort(list, sizeComparator);
		int i = 0;
		for (Size size : list) {
			if ((size.width >= minWidth) && equalRate(size, rate)) {
				Log.i(TAG, "<-- Size : w = " + size.width + "，h = " + size.height + " -->");
				break;
			}
			i++;
		}
		if (i == list.size()) {
			i = 0;// 如果没找到，就选最小的size
		}
		return list.get(i);

	}

	/**
	 * 打印所有支持的pictureSize
	 * 
	 * @param parameters
	 */
	public void printSupportPictureSize(Parameters parameters) {
		List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
		Log.i(TAG, "<-- supportedPictureSizes start -->");
		for (int i = 0; i < supportedPictureSizes.size(); i++) {
			Size size = supportedPictureSizes.get(i);
			Log.i(TAG, "<-- " + size.width + "," + size.height + " -->");
		}
		Log.i(TAG, "<-- supportedPictureSizes end -->");
	}

	/**
	 * 打印所有支持的previewSize
	 * 
	 * @param parameters
	 */
	public void printSupportPreviewSize(Parameters parameters) {
		List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
		Log.i(TAG, "<-- supportedPreviewSizes start -->");
		for (int i = 0; i < supportedPreviewSizes.size(); i++) {
			Size size = supportedPreviewSizes.get(i);
			Log.i(TAG, "<-- " + size.width + "," + size.height + " -->");
		}
		Log.i(TAG, "<-- supportedPreviewSizes end -->");
	}

	/**
	 * 打印支持的聚焦模式
	 * 
	 * @param params
	 */
	public void printSupportFocusMode(Camera.Parameters params) {
		List<String> focusModes = params.getSupportedFocusModes();
		Log.i(TAG, "<-- supportFocusMode start -->");
		for (String mode : focusModes) {
			Log.i(TAG, "focusModes--" + mode);
		}
		Log.i(TAG, "<-- supportFocusMode end -->");
	}

	/**
	 * 停止预览
	 */
	private void stopPreview() {
		if (mCamera != null) {
			mCamera.stopPreview();
		}
	}

	// 比例
	private boolean equalRate(Size size, float rate) {
		float r = (float) (size.width) / (float) (size.height);

		if (Math.abs(r - rate) <= 0.03) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 比较Camera.Size大小
	 * 
	 * @author Administrator
	 *
	 */
	public class CameraSizeComparator implements Comparator<Camera.Size> {

		@Override
		public int compare(Size lhs, Size rhs) {
			if (lhs.width == rhs.width) {
				return 0;
			} else if (lhs.width > rhs.width) {
				return 1;
			} else {
				return -1;
			}
		}

	}

	/**
	 * 相机开启完毕回调
	 * 
	 * @author Administrator
	 *
	 */
	public interface CameraOpenReady {
		public void openReady();
	}

}
