package com.moe.widget;
import android.widget.FrameLayout;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;

public class PopupBackground extends FrameLayout
{
	private Rect rect;
	public PopupBackground(Context context){
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.clipRect(0,0,canvas.getWidth(),canvas.getHeight());
		super.onDraw(canvas);
		if(rect!=null)
		canvas.clipRect(rect,Region.Op.DIFFERENCE);
	}

	@Override
	public void onDrawForeground(Canvas canvas)
	{
		// TODO: Implement this method
		//super.onDrawForeground(canvas);
	}
	
	public void setShowRect(Rect rect){
		this.rect=rect;
		invalidate();
	}
}
