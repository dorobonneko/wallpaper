package com.moe.widget;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Canvas;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class ProgressBar extends View
{
	private int progress,max=100;
	private int height=10;
	private int bg,high;
	private Paint p=new Paint();
	public ProgressBar(Context context,AttributeSet attrs){
		super(context,attrs);
		setWillNotDraw(false);
		TypedArray ta=context.obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.colorControlHighlight,android.support.v7.appcompat.R.attr.colorAccent});
		bg=ta.getColor(0,0xff2d2d2d);
		high=ta.getColor(1,0xffffffff);
		ta.recycle();
		p.setColor(high);
	}

	public void setMax(int parseInt)
	{
		this.max=parseInt;
		postInvalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec,height);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		Path path=new Path();
		path.moveTo(getHeight()/2,0);
		path.lineTo(getWidth()-getHeight()*0.5f,0);
		RectF rectf=new RectF();
		rectf.set(getWidth()-getHeight(),0,getWidth(),getHeight());
		path.arcTo(rectf,270,180,true);
		path.lineTo(getHeight()/2,getHeight());
		rectf.set(0,0,getHeight(),getHeight());
		path.arcTo(rectf,90,180,true);
		path.lineTo(getWidth()-getHeight()*0.5f,0);
		//path.setLastPoint(getHeight()/2,0);
		//path.close();
		path.setFillType(Path.FillType.EVEN_ODD);
		canvas.clipPath(path);
		canvas.drawColor(bg);
		canvas .drawRect(0,0,(int)(((double)progress)/max*getWidth()),getHeight(),p);
	}
	public void setProgress(int progress){
		this.progress=progress<0?0:progress>max?max:progress;
		postInvalidate();
	}
}
