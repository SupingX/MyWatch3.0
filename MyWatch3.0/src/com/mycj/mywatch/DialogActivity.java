package com.mycj.mywatch;


import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class DialogActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_dialog);
		Intent intent = getIntent();
		TextView tvBtn = (TextView) findViewById(R.id.btn_neg);
		TextView tvMsg = (TextView) findViewById(R.id.txt_msg);
		TextView tvTitle = (TextView) findViewById(R.id.txt_title);
		
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (display.getHeight() > display.getWidth()) {
			// lp.height = (int) (display.getHeight() * 0.5);
			lp.width = (int) (display.getWidth() * 1.0);
		} else {
			// lp.height = (int) (display.getHeight() * 0.75);
			lp.width = (int) (display.getWidth() * 0.5);
		}
		getWindow().setAttributes(lp);
		
		if (intent!=null) {
			String title = intent.getStringExtra("title");
			String msg = intent.getStringExtra("msg");
			String btn = intent.getStringExtra("btn");
			tvBtn.setText(btn);
			tvMsg.setText(msg);
			tvTitle.setText(title);
		}
	
		tvBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			finish();
				if (	getMusicService()!=null&&	getMusicService().isPlaying()) {
					getMusicService().stop();
				}
			}
		});
	}

	@Override
	public void initViews() {
		
	}

	@Override
	public void setListener() {
		
	}
}
