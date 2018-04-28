package com.moe.LiveVisualizer.widget;
import android.view.ViewGroup;
import android.content.Context;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;

public class PopupLayout extends ViewGroup
{
	private Paint paint;
	private int padding=15;
	public PopupLayout(Context context){
		super(context);
		padding=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,getResources().getDisplayMetrics());
		setPadding(padding,padding,padding,padding);
		paint=new Paint();
		paint.setColor(0xffffffff);
		paint.setShadowLayer(padding/2.0f,0,0,0xaa000000);
		setWillNotDraw(false);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
		setLayerType(View.LAYER_TYPE_SOFTWARE,null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int height=0;
		for(int i=0;i<getChildCount();i++){
			View child=getChildAt(i);
			measureChild(child,widthMeasureSpec,heightMeasureSpec);
			height+=child.getMeasuredHeight();
		}
		setMeasuredDimension(widthMeasureSpec,height);
	}
	
	@Override
	protected void onLayout(boolean p1, int p2, int p3, int p4, int p5)
	{
		if(p1){
			int y=p3;
			for(int i=0;i<getChildCount();i++){
				View child=getChildAt(i);
				child.layout(p2+padding,p3,p4,p3+child.getMeasuredHeight());
				p3+=child.getMeasuredHeight();
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO: Implement this method
		super.onDraw(canvas);
		canvas.drawRect(padding,padding,canvas.getWidth()-padding,canvas.getHeight()-padding,paint);
	}
	
}
