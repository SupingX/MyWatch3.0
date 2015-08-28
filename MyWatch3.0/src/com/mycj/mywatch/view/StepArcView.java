package com.mycj.mywatch.view;

import com.mycj.mywatch.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class StepArcView extends View{
	
	private Paint mCompletePaint;
	private float mmWidth;
	private float mmHeight;
	private float strokeWidth = 20;
	private float bgStrokeWidth = 5;
	private Paint mBackgroundPint;
	
	private int progress=25;
	private int max = 100;
	
	public StepArcView(Context context) {
		super(context);
		init(context);
	}

	public StepArcView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public StepArcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mCompletePaint = new Paint();
		mCompletePaint.setAntiAlias(true); //消除锯齿
		mCompletePaint.setColor(getResources().getColor(R.color.color_top_blue));
		mCompletePaint.setStyle(Paint.Style.STROKE); //绘制空心圆
		mCompletePaint.setStrokeWidth(strokeWidth); //设置进度条宽度
		mCompletePaint.setStrokeJoin(Paint.Join.ROUND);
		mCompletePaint.setStrokeCap(Paint.Cap.ROUND); //设置圆角
		
		mBackgroundPint = new Paint();
		mBackgroundPint.setAntiAlias(true); //消除锯齿
		mBackgroundPint.setColor(getResources().getColor(R.color.color_bg_arc));
		mBackgroundPint.setStyle(Paint.Style.STROKE); //绘制空心圆
		mBackgroundPint.setStrokeWidth(bgStrokeWidth); //设置进度条宽度
		mBackgroundPint.setStrokeJoin(Paint.Join.ROUND);
		mBackgroundPint.setStrokeCap(Paint.Cap.ROUND); //设置圆角
		
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int mWidth = measureWidth(widthMeasureSpec);
		int mHeight = measureHeight(heightMeasureSpec);
		
		
		setMeasuredDimension(mWidth, mHeight*3/4);
//		setMeasuredDimension(mWidth, mHeight);
	}
	
	
	private int measureHeight(int heightMeasureSpec) {
		int result = 0;
		int mode = MeasureSpec.getMode(heightMeasureSpec);
		int size = MeasureSpec.getSize(heightMeasureSpec);
		if (mode==MeasureSpec.EXACTLY) {
			result = size;
		}else if(mode ==MeasureSpec.AT_MOST){
			result = getHeight()-getPaddingTop()-getPaddingBottom();
			result = Math.min(result, size);
		}
		return result;
	}

	private int measureWidth(int widthMeasureSpec) {
		int result = 0;
		int mode = MeasureSpec.getMode(widthMeasureSpec);
		int size = MeasureSpec.getSize(widthMeasureSpec);
		if (mode==MeasureSpec.EXACTLY) {
			result = size;
		}else if(mode ==MeasureSpec.AT_MOST){
			result = getWidth()-getPaddingLeft()-getPaddingRight();
			result = Math.min(result, size);
		}
		return result;
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		 mmWidth = getWidth();
		 mmHeight = getHeight();
		drawArc(canvas);
		
		
	}

	private void drawArc(Canvas canvas) {
		RectF oval = new RectF(strokeWidth/2, strokeWidth/2, mmWidth-strokeWidth/2, mmHeight * 4/3-strokeWidth);
//		RectF oval = new RectF(strokeWidth/2, strokeWidth/2, mmWidth-strokeWidth/2, mmWidth-strokeWidth);
		canvas.drawArc(oval,  -210,240, false, mBackgroundPint);
		canvas.drawArc(oval,  -210,progressToDeGree(progress), false, mCompletePaint);
	}


	/**
	 * progress转angle （进度转角度）
	 * @param progress
	 * @return
	 */
	private float progressToDeGree(int progress) {
		return 	240*progress / max;
	}
	
	public void setProgress(int progress){
		this.progress = progress;
		invalidate();
		if (mOnProgressListener!=null) {
			mOnProgressListener.onChange(progress);
		}
	}
	
	public void setMax(int max){
		this.max = max;
		invalidate();
	}
	
	private OnProgressListener mOnProgressListener;
	public void setOnProgressListener(OnProgressListener l){
		this.mOnProgressListener = l;
	}
	public interface OnProgressListener{
		public void onChange(int progress);
	}
	
}
