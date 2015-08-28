package com.mycj.mywatch.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.litepal.crud.DataSupport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycj.mywatch.BaseFragment;
import com.mycj.mywatch.R;
import com.mycj.mywatch.service.LiteBlueService;
import com.mycj.mywatch.util.DataUtil;
import com.mycj.mywatch.view.HeartRateView;
import com.mycj.mywatch.view.SleepCountView;

public class HeartRateFragment extends BaseFragment implements OnClickListener{
	private final String SDF = "yyyy-MM-dd";
	private Handler mhandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				mhandler.postDelayed(run, 1000);
				break;

			default:
				break;
			}
		};
	};
	private Runnable r = new Runnable() {

		@Override
		public void run() {
			
			
		}
	};
	private BroadcastReceiver mReceiver = new BroadcastReceiver()	{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(LiteBlueService.LITE_DATA_HEART_RATE)) {
				final int hr = intent.getIntExtra(LiteBlueService.EXTRA_HEART_RATE, 0);
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						updateUi(hr);
					}
				});
			}
		}
		
	};
	
	private SleepCountView sleepCountView;
	private TextView tvDate;
	private TextView tvTotal;
	private TextView tvAwak;
	private TextView tvDeep;
	private TextView tvLight;
	private TextView tvHeartRate;
	private TextView tvMax;
	private TextView tvMin;
	private TextView tvAvg;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_heart_rate, container,false);
		initViews(view);
		setListener();
		
		return view;
	}
	
	@Override
	public void onResume() {
		updateTextDay(new Date());
		mhandler.post(run);
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {


		default:
			break;
		}
	}
	
	private List<Integer> listHr = new ArrayList<Integer>();
	private int max=0;
	private int min= 240;
	private float total= 0;	
	
	private Runnable run = new Runnable() {
		@Override
		public void run() {
			Random r = new Random();
			int f = r.nextInt(240);
			updateUi(f);
			
			mhandler.sendEmptyMessage(0);
		}
	};
	
	
	private HeartRateView hrView;

	private void initViews(View view) {
		tvHeartRate = (TextView) view.findViewById(R.id.tv_heart_rate);
		tvMax = (TextView) view.findViewById(R.id.tv_hr_max);
		tvMin = (TextView) view.findViewById(R.id.tv_hr_min);
		tvAvg = (TextView) view.findViewById(R.id.tv_hr_avg);
		hrView = (HeartRateView) view.findViewById(R.id.heart_rate);
		
	}

	private void setListener(){
	}

	private void updateTextDay(Date date){
	}
	
	
	private void updateUi(int f){
		hrView.addData((float)f);
		tvHeartRate.setText(f+"");
		listHr.add(f);
		total+=f;
		
		max = Math.max(f, max);
		min = Math.min(f, min);
		Log.e("", "心率增加中");
		
		tvMax.setText(max+"");
		tvMin.setText(min+"");
		if (listHr.size()>0) {
			tvAvg.setText(DataUtil.format(total/listHr.size()));
		}else{
			tvAvg.setText(0+"");
		}
		
	}
	
}
