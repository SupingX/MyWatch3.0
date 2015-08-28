package com.mycj.mywatch.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.litepal.crud.DataSupport;

import android.R.integer;
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
import com.mycj.mywatch.bean.HeartRateData;
import com.mycj.mywatch.bean.SleepData;
import com.mycj.mywatch.util.DateUtil;
import com.mycj.mywatch.view.HistoryView;
import com.mycj.mywatch.view.SleepCountView;
import com.mycj.mywatch.view.SleepCountView.OnScrollListener;
import com.mycj.mywatch.view.SleepCountView.OnSleepDataChangeListener;

public class HeartRateHistoryFragment extends BaseFragment implements OnClickListener{
	private final String SDF = "yyyy-MM";
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
	private TextView tvDate;
	private TextView tvPreious;
	private TextView tvNext;
	private HistoryView sleepHistoryView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_heart_rate_history, container,false);
		initViews(view);
		setListener();
		return view;
	}
	
	@Override
	public void onResume() {
		
		
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_preious:
		//1.动画变化
		startAnimation(tvPreious);
		//2。日期变化 -1
		Date date = getDateForDiff(-1);
		tvDate.setText(DateUtil.dateToString(date, SDF));
		//3。查询数据库，根据日期
		List<Float> list = getData(date);
		sleepHistoryView.setData(list);
	
		break;
	case R.id.tv_next:
		//1.动画变化
		startAnimation(tvPreious);
		//2。日期变化 -1
		Date dateNext = getDateForDiff(1);
		tvDate.setText(DateUtil.dateToString(dateNext, SDF));
		//3。查询数据库，根据日期
		List<Float> listNext = getData(dateNext);
		sleepHistoryView.setData(listNext);
		break;
		default:
			break;
		}
	}
	
	
	
	private List<Float> getData(Date date) {
		List<Float> data = new ArrayList<Float>();
		//获取本月天数
		Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	Log.v("", "——————————————————————月份 ："+c.get(Calendar.MONTH));
    	int monthMaxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
    	Log.e("", "本月天数 monthMaxDay : " + monthMaxDay );
    	//遍历查找每天的数据
		for (int i = 0; i < monthMaxDay; i++) {
			c.set(Calendar.DAY_OF_MONTH, i);
			HeartRateData hrData = findHeartRateDateByDate(c.getTime());
			//有数据就添加
			if (hrData!=null) {
				data.add((float) hrData.getAvghr());
				
			}else{
				//没有数据就为0
				data.add(0f);
			}
		}
		//从数据库中获取一个月所有的天数的数据
//		List<SleepData> list = findAllSleepDateByMonth(date);
		return data;
	}
	
	
	/**
	 *	根据日期查找HeartRateData
	 * @param date
	 * @return
	 */
	private HeartRateData findHeartRateDateByDate(Date date){
		String sql = DateUtil.dateToString(date, "yyyyMMdd");
		List<HeartRateData>  hrData = DataSupport.where("date=?",sql).find(HeartRateData.class);
		if (hrData!=null && hrData.size()>0) {
			return hrData.get(0);
		}else {
			return null;
		}
	}
	
	
	private Date getDateForDiff(int diff){
		String dateStr = tvDate.getText().toString();
		Date date = DateUtil.stringToDate(dateStr, SDF);
		Date diffDate = DateUtil.getDateOfDiffMonth(date, diff);
		return diffDate;
	}
	

	private void initViews(View view) {
		tvDate = (TextView) view.findViewById(R.id.tv_sleep_history_date);
		tvPreious = (TextView) view.findViewById(R.id.tv_preious);
		tvNext = (TextView) view.findViewById(R.id.tv_next);
		sleepHistoryView = (HistoryView) view.findViewById(R.id.history_sleep);
		sleepHistoryView.setTextY(new String[]{"40","80","120","160","200","240"});
		tvDate.setText(DateUtil.dateToString(new Date(), SDF));//默认本月
	}
	

	private void setListener(){
		tvPreious.setOnClickListener(this);
		tvNext.setOnClickListener(this);
	}

	
	
}
