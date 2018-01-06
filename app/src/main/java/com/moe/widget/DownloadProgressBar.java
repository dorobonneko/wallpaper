package com.moe.widget;
import android.widget.LinearLayout;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.content.res.TypedArray;

public class DownloadProgressBar extends LinearLayout
{
	private int max=100,progress;
	private Paint paint;
	public DownloadProgressBar(Context context,AttributeSet attrs){
		super(context,attrs);
		setWillNotDraw(false);
		paint=new Paint();
		TypedArray ta=context.obtainStyledAttributes(attrs,new int[]{android.support.v7.appcompat.R.attr.colorAccent});
		paint.setColor(ta.getColor(0,0xffbbbbbb));
		ta.recycle();
		paint.setAlpha(0x80);
		paint.setDither(true);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
	}

	public void setMax(int max)
	{
		this.max = max;
		postInvalidate();
	}

	public int getMax()
	{
		return max;
	}

	public void setProgress(int progress)
	{
		this.progress = progress;
		postInvalidate();
	}

	public int getProgress()
	{
		return progress;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawRect(0,0,getMeasuredWidth()*(((float)progress)/max),getMeasuredHeight(),paint);
	}
	
}
