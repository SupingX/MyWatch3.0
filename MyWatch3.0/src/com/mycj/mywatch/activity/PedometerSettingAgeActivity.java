package com.mycj.mywatch.activity;

import com.mycj.mywatch.BaseActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.R.layout;
import com.mycj.mywatch.bean.Constant;
import com.mycj.mywatch.util.SharedPreferenceUtil;
import com.mycj.mywatch.view.CleanEditText;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PedometerSettingAgeActivity extends BaseActivity implements OnClickListener{
	private RelativeLayout rlSetting;
	private CleanEditText edAge;
	private TextView tvSave;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedometer_setting_age);
		initViews();
		setListener();
	}
	@Override
	public void initViews() {
		rlSetting = (RelativeLayout) findViewById(R.id.rl_setting);
		edAge = (CleanEditText) findViewById(R.id.ed_age);
		tvSave = (TextView) findViewById(R.id.tv_save);
		//初始化文本值
		String lastHeight = getIntent().getStringExtra("age");
		if (lastHeight!=null) {
			edAge.setText(lastHeight);
		}
		//选择所有内容
		edAge.selectAll();
		//将光标移到最后一位
		edAge.setSelection(edAge.getText().length());
	}
	@Override
	public void setListener() {
		rlSetting.setOnClickListener(this);
		tvSave.setOnClickListener(this);
		edAge.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    //如果获得焦点，则弹出键盘
				   if (hasFocus) {
			            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			       }
			}
		});
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_save:
			String value = edAge.getText().toString();
			if (value!=null&&!value.trim().equals("")) {
				int result  = Integer.valueOf(value);
				if (result <1 | result>200) {
					showShortToast("设置错误");
					return ;
				}
				SharedPreferenceUtil.put(this, Constant.SHARE_PEDOMETER_AGE, result);
				finish();
			}else{
				showShortToast("设置错误");
			}
			break;
		case R.id.rl_setting:
			finish();
			break;
		default:
			break;
		}
	}
}
