package com.moe.graphics;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.util.TypedValue;

public class MessageDrawable extends Drawable implements Drawable.Callback
{

	private int msg;
	private Paint paint;
	public MessageDrawable(Context context){
		TypedArray ta=context.obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.colorControlNormal});
		paint=new Paint();
		paint.setColor(ta.getColor(0,0xffffffff));
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,context.getResources().getDisplayMetrics()));
		paint.setStrokeWidth(1.75f);
		ta.recycle();
	}
	public void setMsgSize(int msg)
	{
		this.msg = msg;
		Callback c=getCallback();
		if(c!=null)c.invalidateDrawable(this);
	}

	


	public int getMsgSize()
	{
		return msg;
	}
	@Override
	public void draw(Canvas p1)
	{
		if(msg!=0){
			p1.drawCircle(getIntrinsicWidth()/2,getIntrinsicHeight()/2,p1.getWidth()/4,paint);
		}
		int color=paint.getColor();
		paint.setColor(0xffffffff);
		p1.drawText(""+(msg==0?"消息":msg),getIntrinsicWidth()/2,(getIntrinsicHeight()-paint.ascent()-paint.descent())/2,paint);
		paint.setColor(color);
	}

	@Override
	public void setAlpha(int p1)
	{
		paint.setAlpha(p1);
	}

	@Override
	public void setColorFilter(ColorFilter p1)
	{
		paint.setColorFilter(p1);
	}

	@Override
	public int getOpacity()
	{
		return PixelFormat.TRANSPARENT;
	}
	@Override
	public void invalidateDrawable(Drawable p1)
	{
		if(getCallback()!=null)
			getCallback().invalidateDrawable(this);
	}

	@Override
	public void scheduleDrawable(Drawable p1, Runnable p2, long p3)
	{
		if(getCallback()!=null)getCallback().scheduleDrawable(this,p2,p3);
	}

	@Override
	public void unscheduleDrawable(Drawable p1, Runnable p2)
	{
		if(getCallback()!=null)
			getCallback().unscheduleDrawable(this,p2);
	}
	
}
