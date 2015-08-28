package com.mycj.mywatch.fragment;

import com.mycj.mywatch.BaseFragment;
import com.mycj.mywatch.R;
import com.mycj.mywatch.SleepSettingEndTimeActivity;
import com.mycj.mywatch.SleepSettingStartTimeActivity;
import com.mycj.mywatch.activity.PedometerSettingAgeActivity;
import com.mycj.mywatch.activity.PedometerSettingHeightActivity;
import com.mycj.mywatch.activity.PedometerSettingTargetActivity;
import com.mycj.mywatch.activity.PedometerSettingWeightActivity;
import com.mycj.mywatch.bean.Constant;
import com.mycj.mywatch.business.ProtocolForWrite;
import com.mycj.mywatch.service.LiteBlueService;
import com.mycj.mywatch.util.SharedPreferenceUtil;
import com.mycj.mywatch.view.ActionSheetDialog;
import com.mycj.mywatch.view.CleanEditText;
import com.mycj.mywatch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.mycj.mywatch.view.ActionSheetDialog.SheetItemColor;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HeartRateSettingFragment extends BaseFragment implements OnClickListener{
	private RelativeLayout rlMax;
	private RelativeLayout rlMin;
	private CleanEditText edMax;
	private CleanEditText edMin;
	private TextView tvSave;
	private LiteBlueService mLiteBlueService;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_heart_rate_setting, container,false);
		rlMax = (RelativeLayout) view.findViewById(R.id.rl_max_hr);
		rlMin = (RelativeLayout) view.findViewById(R.id.rl_min_hr);
		edMax = (CleanEditText) view.findViewById(R.id.et_max_hr);
		edMin = (CleanEditText) view.findViewById(R.id.et_min_hr);
		tvSave = (TextView) view.findViewById(R.id.tv_save);
		//选择所有内容
		edMax.selectAll();
		edMin.selectAll();
		//将光标移到最后一位
		edMax.setSelection(edMax.getText().length());
		edMin.setSelection(edMax.getText().length());
		setListener();

		return view;
	}


	@Override
	public void onResume() {
		mLiteBlueService = getLiteBlueService();
		setDefalutValue();
		super.onResume();
	}
	
	
	private void setDefalutValue() {
		int max = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_HEART_RATE_MAX, 240);
		int min = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_HEART_RATE_MIN, 40);
		edMax.setText(max+"");
		edMin.setText(min+"");
	}
	

	private void setListener() {
		rlMax.setOnClickListener(this);
		rlMin.setOnClickListener(this);
		tvSave.setOnClickListener(this);
		
		edMax.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    //如果获得焦点，则弹出键盘
				   if (hasFocus) {
			            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			       }
			}
		});
		rlMin.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				//如果获得焦点，则弹出键盘
				if (hasFocus) {
				      getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_max_hr:
			break;
		case R.id.rl_min_hr:
			break;
		case R.id.tv_save:
			if (edMax.getText()!=null&&!edMax.getText().toString().equals("")) {
				int max = Integer.valueOf(edMax.getText().toString());
				Log.v("", "max : "+max);
				if (max>240 || max <40) {
					showToast("设置范围40-240");
					return ;
				}
				SharedPreferenceUtil.put(getActivity(), Constant.SHARE_HEART_RATE_MAX, max);
				hideSoft();
			 
			}else{
				showToast("设置范围40-240");
				return;
			}
			if (edMin.getText()!=null&&!edMin.getText().toString().equals("")) {
				int min = Integer.valueOf(edMin.getText().toString());
				Log.v("", "min : "+min);
				if (min<40 ||min>240) {
					showToast("设置范围40-240");
					return ;
				}
				SharedPreferenceUtil.put(getActivity(), Constant.SHARE_HEART_RATE_MIN, min);
				hideSoft();
			}else{
				showToast("设置范围40-240");
				return;
			}
			
			wtiteHeartRateToWatch();
			
			break;

		default:
			break;
		}
	}
	  
	private void wtiteHeartRateToWatch() {
		int max = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_HEART_RATE_MAX, 240);
		int min = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_HEART_RATE_MIN, 40);
		try {
			if (mLiteBlueService.isBinded()&&mLiteBlueService.isServiceDiscovered()) {
				mLiteBlueService.writeCharacticsUseConnectListener(ProtocolForWrite.instance().getByteForHeartRate(max, min));
			}
		} catch (Exception e) {
			showToast("设置失败");
		}
	}


	private boolean hideSoft(){
	    if(null != getActivity().getCurrentFocus()){
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager =(InputMethodManager)  getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
		return false;
	}
	
}
