package com.mycj.mywatch.activity;

import com.mycj.mywatch.BaseActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.bean.Constant;
import com.mycj.mywatch.util.SharedPreferenceUtil;
import com.mycj.mywatch.view.CleanEditText;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PedometerSettingHeightActivity extends BaseActivity implements OnClickListener{
	private RelativeLayout rlSetting;
	private CleanEditText edHeight;
	private TextView tvSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedometer_setting_height);
		initViews();
		setListener();
	}

	@Override
	public void initViews() {
		rlSetting = (RelativeLayout) findViewById(R.id.rl_setting);
		edHeight = (CleanEditText) findViewById(R.id.ed_height);
		tvSave = (TextView) findViewById(R.id.tv_save);
		//初始化文本值
		String lastHeight = getIntent().getStringExtra("height");
		if (lastHeight!=null) {
			edHeight.setText(lastHeight);
		}
		//选择所有内容
		edHeight.selectAll();
		//将光标移到最后一位
		edHeight.setSelection(edHeight.getText().length());
	}

	@Override
	public void setListener() {
		rlSetting.setOnClickListener(this);
		tvSave.setOnClickListener(this);
		edHeight.setOnFocusChangeListener(new OnFocusChangeListener() {
			
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
			String value = edHeight.getText().toString();
			if (value!=null&&!value.trim().equals("")) {
				int result  = Integer.valueOf(value);
				if (result <1 | result>300) {
					showShortToast("设置错误");
					return ;
				}
				SharedPreferenceUtil.put(this, Constant.SHARE_PEDOMETER_HEIGHT, result);
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
