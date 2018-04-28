package com.moe.LiveVisualizer.internal;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Paint;
import android.graphics.Rect;

public class ShadowDrawable extends Drawable
{
private Paint paint=new Paint();
public ShadowDrawable(){
	paint.setColor(0xffffffff);
	paint.setShadowLayer(15,0,15,0xff000000);
}
	@Override
	public void draw(Canvas p1)
	{
		//p1.drawColor(0xffffffff);
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
		// TODO: Implement this method
		return PixelFormat.TRANSLUCENT;
	}
	
}
