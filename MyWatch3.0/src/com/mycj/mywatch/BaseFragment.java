package com.mycj.mywatch;

import com.mycj.mywatch.service.LiteBlueService;
import com.mycj.mywatch.view.ActionSheetDialog;
import com.mycj.mywatch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.mycj.mywatch.view.ActionSheetDialog.SheetItemColor;

import android.R.anim;
import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class BaseFragment extends Fragment{
	public final  int RESULT_PEDO_TARGET = 0x01; 
	public final  int RESULT_PEDO_HEIGHT = 0x02; 
	public final  int RESULT_PEDO_WEIGHT = 0x03; 
	public final  int RESULT_PEDO_AGE = 0x04; 
	
	
	public void startAnimation(TextView v){
		ObjectAnimator animation = ObjectAnimator.ofFloat(v, "alpha", 1,0.5f,1f);
		animation.setDuration(1000);
		animation.start();
	}
	protected LiteBlueService getLiteBlueService() {
		BaseApp app = (BaseApp) getActivity().getApplication();
		return app.getBlueService();
	}
	
	protected void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}
	
	
}
