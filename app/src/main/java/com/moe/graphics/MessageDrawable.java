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
import android.graphics.RectF;

public class MessageDrawable extends Drawable implements Drawable.Callback
{

	private int msg;
	private Paint paint;
	public MessageDrawable(Context context){
		//TypedArray ta=context.obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.colorControlNormal});
		paint=new Paint();
		//paint.setColor(0xffffffff);
		//paint.setTextAlign(Paint.Align.CENTER);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,12,context.getResources().getDisplayMetrics()));
		paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,context.getResources().getDisplayMetrics())*0.75f);
		paint.setStyle(Paint.Style.STROKE);
		//ta.recycle();
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
		paint.setColor(0xffffffff);
		int width=p1.getWidth(),height=p1.getHeight();
		RectF round=new RectF(-width*0.25f,-height*0.17f,width*0.25f,height*0.17f);
		paint.setStyle(Paint.Style.STROKE);
		p1.drawRoundRect(round,paint.getStrokeWidth()*2,paint.getStrokeWidth()*2,paint);
		paint.setStyle(Paint.Style.FILL);
		p1.drawLines(new float[]{round.left+paint.getStrokeWidth(),round.top+paint.getStrokeWidth(),0,0,0,0,round.right-paint.getStrokeWidth(),round.top+paint.getStrokeWidth()},paint);
		if(msg!=0){
			paint.setColor(0xffff0000);
			p1.drawCircle(round.right,round.top,round.right/2,paint);
			paint.setColor(0xffffffff);
			p1.drawText(""+msg,round.right-paint.measureText(""+msg)/2,round.top-(paint.descent()+paint.ascent())/2,paint);
		}
		/*int color=paint.getColor();
		paint.setColor(0xffffffff);
		p1.drawText(""+(msg==0?"消息":msg),getIntrinsicWidth()/2,(getIntrinsicHeight()-paint.ascent()-paint.descent())/2,paint);
		paint.setColor(color);*/
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
