package com.mycj.mywatch.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.litepal.crud.DataSupport;

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
import com.mycj.mywatch.bean.SleepData;
import com.mycj.mywatch.util.DateUtil;
import com.mycj.mywatch.view.SleepCountView;
import com.mycj.mywatch.view.SleepCountView.OnScrollListener;
import com.mycj.mywatch.view.SleepCountView.OnSleepDataChangeListener;

public class SleepFragment extends BaseFragment implements OnClickListener{
	private final String SDF = "yyyy-MM-dd";
	private Handler mhandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
	
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
	private SleepCountView sleepCountView;
	private TextView tvDate;
	private TextView tvTotal;
	private TextView tvAwak;
	private TextView tvDeep;
	private TextView tvLight;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sleep_sleep, container,false);
		initViews(view);
		setListener();
		
		sleepCountView.setSleepData(new int[]{1,2,3,4,2,2,2,2,1,5,5,5,4,4,2,5,5,2,4,4,1,2,2,5});
		return view;
	}
	
	@Override
	public void onResume() {
		updateTextDay(new Date());
		
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
	
	private SleepData getSleepDateFromSql(String dateStr) {
		List<SleepData> sleepDatas = DataSupport.where("date=?",dateStr).find(SleepData.class);
		if (sleepDatas!=null && sleepDatas.size()>0	) {
			return sleepDatas.get(0);
		}
		return null;
	}

	private String getDateForDiff(int diff){
		
		String dateStr = tvDate.getText().toString();
		Date date = DateUtil.stringToDate(dateStr, SDF);
		Date diffDate = DateUtil.getDateOfDiffDay(date, diff);
		return DateUtil.dateToString(diffDate, SDF);
	}
	

	private void initViews(View view) {
		tvDate = (TextView) view.findViewById(R.id.tv_sleep_date);
		Log.e("","tvDate ------------"	 +tvDate);
		tvDate.setText(DateUtil.dateToString(new Date(), SDF));
		
		tvTotal = (TextView) view.findViewById(R.id.tv_sleep_total);
		tvAwak = (TextView) view.findViewById(R.id.tv_sleep_awak);
		tvDeep = (TextView) view.findViewById(R.id.tv_sleep_deep);
		tvLight = (TextView) view.findViewById(R.id.tv_sleep_light);
		
		sleepCountView = (SleepCountView) view.findViewById(R.id.sleep_count);
		//TEST
		
	}

	private void setListener(){
		sleepCountView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void previous() {
			}
			@Override
			public void next() {
			}
		});
		sleepCountView.setOnSleepDataChangeListener(new OnSleepDataChangeListener() {
			@Override
			public void onchange(int[]sleeps) {
				tvAwak.setText(sleepCountView.getAwak()+"");
				tvDeep.setText(sleepCountView.getDeep()+"");
				tvLight.setText(sleepCountView.getLight()+"");
				tvTotal.setText(sleepCountView.getTotal()+"");
			}
		});
	}

	private void updateTextDay(Date date){
		String dateStr = DateUtil.dateToString(date, "yyyy年MM月dd日");
		boolean isSameDay = DateUtil.isSameDayOfMillis(new Date().getTime(), date.getTime());
		if (isSameDay) {
			tvDate.setText("今天："+dateStr);
		}else{
			tvDate.setText(dateStr);
		}
	}
	
	
}
