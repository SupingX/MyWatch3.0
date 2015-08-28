package com.mycj.mywatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mycj.mywatch.service.LiteBlueService;
import com.mycj.mywatch.service.MusicService;
import com.mycj.mywatch.service.SimpleBlueService;
import com.mycj.mywatch.view.ActionSheetDialog;
import com.mycj.mywatch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.mycj.mywatch.view.ActionSheetDialog.SheetItemColor;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 
 * @author Administrator
 *
 */
public abstract class BaseActivity extends FragmentActivity {
	

	/**
	 * 屏幕的宽度、高度、密度
	 */
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected float mDensity;

	protected List<AsyncTask<Void, Void, Boolean>> mAsyncTasks = new ArrayList<AsyncTask<Void, Void, Boolean>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		mLoadingDialog = new FlippingLoadingDialog(this, "请求提交中");
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		mDensity = metric.density;
	}

	@Override
	protected void onDestroy() {
		clearAsyncTask();
		super.onDestroy();
	}

	/** 初始化视图 **/
	public abstract void initViews();

	/** 初始化事件 **/
	public abstract void setListener();
	public String parseText(int value ){
		return value<10?"0"+value:String.valueOf(value);
	}
	protected void putAsyncTask(AsyncTask<Void, Void, Boolean> asyncTask) {
		mAsyncTasks.add(asyncTask.execute());
	}

	protected void clearAsyncTask() {
		Iterator<AsyncTask<Void, Void, Boolean>> iterator = mAsyncTasks
				.iterator();
		while (iterator.hasNext()) {
			AsyncTask<Void, Void, Boolean> asyncTask = iterator.next();
			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
		}
		mAsyncTasks.clear();
	}
	
	public ObjectAnimator startAnimation(ImageView v){
		ObjectAnimator animation  = ObjectAnimator.ofFloat(v, "rotation", 0f,360f);
		animation.setDuration(2000);
		animation.setAutoCancel(true);
		animation.setInterpolator(new LinearInterpolator());
		animation.setRepeatCount(-1);
		animation.setRepeatMode(Animation.RESTART);
		animation.setTarget(v);
		return animation;
	}
	
	public void stopAnimation(ObjectAnimator animation){
		if (animation != null) {
			animation.cancel();
		}
	}
	
//	protected void showLoadingDialog(String text) {
//		if (text != null) {
//			mLoadingDialog.setText(text);
//		}
//		mLoadingDialog.show();
//	}

//	protected void dismissLoadingDialog() {
//		if (mLoadingDialog.isShowing()) {
//			mLoadingDialog.dismiss();
//		}
//	}
	
	/**
	 * signal 5 -1 ~ -42
	 * 
	 * signal 4 -43 ~ -54
	 * 
	 * signal 3 -55 ~ -66
	 * 
	 * signal 2 -67 ~ -78
	 * 
	 * signal 1 -79 ~ -90
	 * 
	 * signal 0 else (-91 ~ -Max && 0 ~ Max)
	 * 
	 * @param integer
	 * @return
	 */
	public int getRssiImg(Integer integer) {
		if (integer <= 0 && integer >= -42) {
			return R.drawable.ic_signal_5;
		} else if (integer <= -43 && integer > -54) {
			return R.drawable.ic_signal_4;
		} else if (integer <= -55 && integer > -66) {
			return R.drawable.ic_signal_3;
		} else if (integer <= -67 && integer > -78) {
			return R.drawable.ic_signal_2;
		} else if (integer <= -79 && integer >= -90) {
			return R.drawable.ic_signal_1;
		} else {
			return R.drawable.ic_signal_0;
		}
	}
	/**
	 * 等待框
	 * @param msg
	 * @return
	 */
	protected ProgressDialog showProgressDialog(String msg) {
		ProgressDialog pDialog;
		pDialog = new ProgressDialog(this);
			pDialog.setCancelable(false);
			pDialog.setMessage(msg);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.show();
			return pDialog;
	}
	
	

	
	/** 短暂显示Toast提示(来自res) **/
	protected void showShortToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
	}

	/** 短暂显示Toast提示(来自String) **/
	protected void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/** 长时间显示Toast提示(来自res) **/
	protected void showLongToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
	}

	/** 长时间显示Toast提示(来自String) **/
	protected void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	/** 显示自定义Toast提示(来自res) **/
	protected void showCustomToast(int resId) {
		View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(
				R.layout.common_toast, null);
		((TextView) toastRoot.findViewById(R.id.toast_text))
				.setText(getString(resId));
		Toast toast = new Toast(BaseActivity.this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	/** 显示自定义Toast提示(来自String) **/
	protected void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(
				R.layout.common_toast, null);
		((TextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(BaseActivity.this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	/** Debug输出Log日志 **/
	protected void showLogDebug(String tag, String msg) {
		Log.d(tag, msg);
	}

	/** Error输出Log日志 **/
	protected void showLogError(String tag, String msg) {
		Log.e(tag, msg);
	}

	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	/** 含有Bundle通过Class跳转界面 **/
	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/** 通过Action跳转界面 **/
	protected void startActivity(String action) {
		startActivity(action, null);
	}

	/** 含有Bundle通过Action跳转界面 **/
	protected void startActivity(String action, Bundle bundle) {
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/** 含有标题和内容的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).setCancelable(true)
				.show();
		return alertDialog;
	}

	/** 含有标题、内容、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message,
			String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}

	/** 含有标题、内容、图标、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message,
			int icon, String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).setIcon(icon)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}

	/** 默认退出 **/
	protected void defaultFinish() {
		super.finish();
	}
	
	protected LiteBlueService getLiteBlueService() {
		BaseApp app = (BaseApp) getApplication();
		return app.getBlueService();
	}
	protected MusicService getMusicService() {
		BaseApp app = (BaseApp) getApplication();
		return app.getMusicService();
	}
	protected SimpleBlueService getSimpleBlueService() {
		BaseApp app = (BaseApp) getApplication();
		return app.getSimpleBlueService();
	}
	
		/**
		 * 是否有网络
		 * @param context
		 * @return
		 */
	public  Boolean isNetWork(Context context) {
	    // 获得网络状态管理器
	    ConnectivityManager conn = (ConnectivityManager) context
	            .getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (conn == null) {
	        return false;
	    } else {
	        NetworkInfo netinfo[] = conn.getAllNetworkInfo();
	        if (netinfo != null) {
	            for (NetworkInfo net : netinfo) {
	                if (net.getState() == NetworkInfo.State.CONNECTED) {
	                    return true;
	                }
	            }

	        }

	    }
	    return false;
	}
	
}
