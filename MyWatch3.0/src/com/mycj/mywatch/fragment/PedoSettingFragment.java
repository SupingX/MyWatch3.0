package com.mycj.mywatch.fragment;

import com.mycj.mywatch.BaseFragment;
import com.mycj.mywatch.R;
import com.mycj.mywatch.activity.PedometerSettingAgeActivity;
import com.mycj.mywatch.activity.PedometerSettingHeightActivity;
import com.mycj.mywatch.activity.PedometerSettingTargetActivity;
import com.mycj.mywatch.activity.PedometerSettingWeightActivity;
import com.mycj.mywatch.bean.Constant;
import com.mycj.mywatch.util.SharedPreferenceUtil;
import com.mycj.mywatch.view.ActionSheetDialog;
import com.mycj.mywatch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.mycj.mywatch.view.ActionSheetDialog.SheetItemColor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PedoSettingFragment extends BaseFragment implements OnClickListener{
	private RelativeLayout rlTarget;
	private RelativeLayout rlHeight;
	private RelativeLayout rlWeight;
	private RelativeLayout rlGender;
	private RelativeLayout rlAge;
	private TextView tvTarget;
	private TextView tvHeight;
	private TextView tvWeight;
	private TextView tvGender;
	private TextView tvAge;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		
		View view = inflater.inflate(R.layout.fragment_pedo_setting, container,false);
		
		rlTarget = (RelativeLayout) view.findViewById(R.id.rl_target);
		rlHeight = (RelativeLayout) view.findViewById(R.id.rl_height);
		rlWeight = (RelativeLayout) view.findViewById(R.id.rl_weight);
		rlGender = (RelativeLayout) view.findViewById(R.id.rl_gender);
		rlAge = (RelativeLayout) view.findViewById(R.id.rl_age);
		
		tvTarget = (TextView) view.findViewById(R.id.tv_target_value);
		tvHeight = (TextView) view.findViewById(R.id.tv_height_value);
		tvWeight = (TextView) view.findViewById(R.id.tv_weight_value);
		tvGender = (TextView) view.findViewById(R.id.tv_gender_value);
		tvAge = (TextView) view.findViewById(R.id.tv_age_value);
		
		setListener();
		
		return view;
	}


	@Override
	public void onResume() {
		setDefalutValue();
		super.onResume();
	}
	
	
	private void setDefalutValue() {
		int target = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_PEDOMETER_TARGET, 500);
		int height = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_PEDOMETER_HEIGHT, 170);
		int weight = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_PEDOMETER_WEIGHT, 60);
		int gender = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_PEDOMETER_GENDER, 0);
		int age = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_PEDOMETER_AGE, 18);
		
		tvTarget.setText(target+"");
		tvHeight.setText(height+"");
		tvWeight.setText(weight+"");
		tvGender.setText(gender==0?"Female":"Male");//0:女；1：男
		tvAge.setText(age+"");
		
	}


	private void setListener() {
		rlTarget.setOnClickListener(this);
		rlHeight.setOnClickListener(this);
		rlWeight.setOnClickListener(this);
		rlGender.setOnClickListener(this);
		rlAge.setOnClickListener(this);
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_target:
			Intent iTarget = new Intent(getActivity(),PedometerSettingTargetActivity.class);
			iTarget.putExtra("target", tvTarget.getText().toString());
			startActivity(iTarget);
			break;
		case R.id.rl_height:
			Intent iHeight= new Intent(getActivity(),PedometerSettingHeightActivity.class);
			iHeight.putExtra("height", tvHeight.getText().toString());
			startActivity(iHeight);
			break;
		case R.id.rl_weight:
			Intent iWeight= new Intent(getActivity(),PedometerSettingWeightActivity.class);
			iWeight.putExtra("weight", tvWeight.getText().toString());
			startActivity(iWeight);
			break;
		case R.id.rl_gender:
			ActionSheetDialog dialog = showIosDialog("选择性别","男", "女");
			dialog.show();
			break;
		case R.id.rl_age:
			Intent iAge= new Intent(getActivity(),PedometerSettingAgeActivity.class);
			iAge.putExtra("age", tvAge.getText().toString());
			startActivity(iAge);
			break;

		default:
			break;
		}
	}
	
	
	private ActionSheetDialog showIosDialog(String title,String sheet1,String sheet2) {
		ActionSheetDialog dialog = new ActionSheetDialog(getActivity()).builder();
		dialog.setTitle(title);
		dialog.addSheetItem(sheet1, SheetItemColor.Red, new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				tvGender.setText("Male");
				SharedPreferenceUtil.put(getActivity(), Constant.SHARE_PEDOMETER_GENDER,1);
			}
		}).addSheetItem(sheet2, SheetItemColor.Blue, new OnSheetItemClickListener() {
			
			@Override
			public void onClick(int which) {
				tvGender.setText("Female");
				SharedPreferenceUtil.put(getActivity(), Constant.SHARE_PEDOMETER_GENDER,0);
			}
		});
		return dialog;
	}
}
