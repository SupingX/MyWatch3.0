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

public class SleepSettingFragment extends BaseFragment implements OnClickListener{
	private RelativeLayout rlStart;
	private RelativeLayout rlEnd;
	private TextView tvStartHour;
	private TextView tvStartmin;
	private TextView tvEndHour;
	private TextView tvEndMin;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_sleep_setting, container,false);
		
		rlStart = (RelativeLayout) view.findViewById(R.id.rl_start_time);
		rlEnd = (RelativeLayout) view.findViewById(R.id.rl_end_time);
		
		tvStartHour = (TextView) view.findViewById(R.id.tv_sleep_hour_start);
		tvStartmin = (TextView) view.findViewById(R.id.tv_sleep_min_start);
		tvEndHour = (TextView) view.findViewById(R.id.tv_sleep_hour_end);
		tvEndMin = (TextView) view.findViewById(R.id.tv_sleep_min_end);
		setListener();
		return view;
	}


	@Override
	public void onResume() {
		setDefalutValue();
		super.onResume();
	}
	
	
	private void setDefalutValue() {
		int startHour = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_SLEEP_START_HOUR, 00);
		int startMin = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_SLEEP_START_MIN, 0);
		int endHour = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_SLEEP_END_HOUR, 0);
		int endMin = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_SLEEP_END_MIN, 0);
		
		tvStartHour.setText(formatValue(startHour));
		tvStartmin.setText(formatValue(startMin));
		tvEndHour.setText(formatValue(endHour));
		tvEndMin.setText(formatValue(endMin));
	}
	
	private String formatValue(int value){
		return value<10?"0"+value:String.valueOf(value);
	}

	private void setListener() {
		rlStart.setOnClickListener(this);
		rlEnd.setOnClickListener(this);
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_start_time:
			Intent iStart = new Intent(getActivity(),SleepSettingStartTimeActivity.class);
			iStart.putExtra("start_hour", Integer.valueOf(tvStartHour.getText().toString()));
			iStart.putExtra("start_min", Integer.valueOf(tvStartmin.getText().toString()));
			startActivity(iStart);
			break;
		case R.id.rl_end_time:
			Intent iEnd= new Intent(getActivity(),SleepSettingEndTimeActivity.class);
			iEnd.putExtra("end_hour", Integer.valueOf(tvEndHour.getText().toString()));
			iEnd.putExtra("end_min", Integer.valueOf(tvEndMin.getText().toString()));
			startActivity(iEnd);
			break;

		default:
			break;
		}
	}
	
	
}
