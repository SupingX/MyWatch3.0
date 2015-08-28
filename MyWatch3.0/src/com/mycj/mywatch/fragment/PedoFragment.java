package com.mycj.mywatch.fragment;

import java.util.Random;

import com.mycj.mywatch.BaseFragment;
import com.mycj.mywatch.R;
import com.mycj.mywatch.bean.Constant;
import com.mycj.mywatch.service.AbstractLiteBlueService;
import com.mycj.mywatch.service.LiteBlueService;
import com.mycj.mywatch.util.DataUtil;
import com.mycj.mywatch.util.SharedPreferenceUtil;
import com.mycj.mywatch.view.StepArcView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PedoFragment extends BaseFragment{
	private StepArcView stepCircleView;
	private Handler mHandler = new Handler(){
		
	};
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(AbstractLiteBlueService.LITE_GATT_DEVICE_FOUND)) {
			}else if (action.equals(AbstractLiteBlueService.LITE_GATT_CONNECTED)) {
				
			}else if (action.equals(AbstractLiteBlueService.LITE_GATT_SERVICE_DISCOVERED)) {
			}else if (action.equals(LiteBlueService.LITE_DATA_STEP)) {	
				final int[] datas = intent.getIntArrayExtra(LiteBlueService.EXTRA_STEP);
				if (datas!=null) {
					completeStep = datas[0];
					getActivity().runOnUiThread(new  Runnable() {
						public void run() {
							setCompleteStepValue();
							setCompleteCal(datas[1]);
							setCompleteDistance(datas[2]);
							setCompleteTime(datas[3], datas[4], datas[5]);
						}
					});
				}
			}else if (action.equals(AbstractLiteBlueService.LITE_CHARACTERISTIC_RSSI_CHANGED)) {
				int rssi = intent.getExtras().getInt(AbstractLiteBlueService.LITE_CHARACTERISTIC_RSSI_CHANGED);
				Message msg = mHandler.obtainMessage();
				msg.arg1 = rssi;
				msg.what = 1;
				mHandler.sendMessage(msg);
			}
		}
	};
	
	private Handler mhandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				mhandler.removeCallbacks(r);
				mhandler.postDelayed(r, 1000);
				break;

			default:
				break;
			}
		};
	};
	private Runnable r = new Runnable() {
		
		@Override
		public void run() {
			stepCircleView.setProgress(new Random().nextInt(10000));
			mhandler.sendEmptyMessage(1);
		}
	};
	private TextView tvComplete;
	private TextView tvCompleteGre;
	private TextView tvPedoMax;
	private TextView tvCal;
	private TextView tvDistance;
	private TextView tvTime;
	private int max;
	private int completeStep;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		
		View view = inflater.inflate(R.layout.fragment_pedo_pedo, container,false);
		stepCircleView = (StepArcView) view.findViewById(R.id.scv);
		stepCircleView.setMax(500);
		stepCircleView.setProgress(0);
//		mhandler.post(r);
		
		tvComplete = (TextView) view.findViewById(R.id.tv_pedo_complete);
		tvCompleteGre = (TextView) view.findViewById(R.id.tv_pedo_complete_gre);
		tvPedoMax = (TextView) view.findViewById(R.id.tv_pedo_right);
		tvCal = (TextView) view.findViewById(R.id.tv_pedo_cal);
		tvDistance = (TextView) view.findViewById(R.id.tv_pedo_distance);
		tvTime = (TextView) view.findViewById(R.id.tv_pedo_time);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
	
	@Override
	public void onResume() {
		getActivity().registerReceiver(mReceiver, LiteBlueService.getIntentFilter());
	
		setMaxStepValue();
		setCompleteStepValue();
		
		
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mhandler.removeCallbacks(r);
		getActivity().unregisterReceiver(mReceiver);
	}
	
	private void setMaxStepValue(){
		//最大步数
		max = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_PEDOMETER_TARGET, 500);
		tvPedoMax.setText(max+"");
		stepCircleView.setMax(max);
	}
	
	private void setCompleteStepValue() {
		//完成步数
		completeStep = getActivity().getIntent().getIntExtra("step", 0);
		tvComplete.setText(completeStep+"");
		tvCompleteGre.setText(DataUtil.format(completeStep/(max*100))+"%");
		stepCircleView.setProgress(completeStep);
	}
	
	private void setCompleteCal(int cal) {
		tvCal.setText(cal+"");
	}
	private void setCompleteDistance(int dis) {
		tvDistance.setText(dis+"");
	}
	private void setCompleteTime(int hour,int min,int second) {
		tvTime.setText(hour+"H "+ min+"M "+second+" S");
	}
}
