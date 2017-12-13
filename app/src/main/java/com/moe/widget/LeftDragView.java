package com.moe.widget;
import android.widget.FrameLayout;
import android.util.AttributeSet;
import android.content.Context;
import android.view.MotionEvent;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap;

public class LeftDragView extends FrameLayout
{
	private ViewDragHelper viewDragHelper;
	private int left,bgColor;
	private Paint paint;
	private Bitmap bit;
	public LeftDragView(Context context,AttributeSet attrs){
		super(context,attrs);
		bgColor=0xaa000000;
		paint=new Paint();
		viewDragHelper=ViewDragHelper.create(this,1,new DragCallback());
		viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
		
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		// TODO: Implement this method
		super.onLayout(changed, left, top, right, bottom);
		if(changed)ViewCompat.offsetLeftAndRight(getChildAt(0),this.left);
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime)
	{
		canvas.drawColor(bgColor);
		
		LinearGradient lg=new LinearGradient(0,0,50,0,0x00000000,0xaa00000,LinearGradient.TileMode.REPEAT);
		paint.setShader(lg);
		if(bit==null){
			bit=Bitmap.createBitmap(50,child.getHeight(),Bitmap.Config.ARGB_8888);
			Canvas c=new Canvas(bit);
			Rect rect=new Rect();
			rect.right=bit.getWidth();
			rect.bottom=bit.getHeight();
			c.drawRect(rect,paint);
		}
		canvas.drawBitmap(bit,child.getLeft()-50,0,null);
		return super.drawChild(canvas, child, drawingTime);
	}

	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		getChildAt(0).setBackgroundColor(0xffeeeeee);
		//setBackgroundColor(0xaa000000);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		if(!isEnabled())return false;
		if(event.getAction()==event.ACTION_CANCEL||event.getAction()==event.ACTION_UP){
			viewDragHelper.cancel();
			return false;
		}
		return viewDragHelper.shouldInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		viewDragHelper.processTouchEvent(event);
		return true;
	}
	class DragCallback extends ViewDragHelper.Callback
	{

		@Override
		public void onEdgeDragStarted(int edgeFlags, int pointerId)
		{
			viewDragHelper.captureChildView(getChildAt(0),pointerId);
		}

		@Override
		public boolean tryCaptureView(View p1, int p2)
		{
			return false;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx)
		{
			return Math.max(Math.min(left,child.getWidth()),0);
		}

		@Override
		public int getViewHorizontalDragRange(View child)
		{
			return child.getWidth();
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy)
		{
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			LeftDragView.this.left=left;
			bgColor=(Color.argb((int)(((1-(double)left/changedView.getWidth()))*0xaa),0,0,0));
			getChildAt(0).setAlpha((1-(float)left/changedView.getWidth()));
			invalidate();
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel)
		{
			super.onViewReleased(releasedChild, xvel, yvel);
			if(xvel<-1000)
				viewDragHelper.settleCapturedViewAt(0,0);
			else if(xvel>1000)
				viewDragHelper.settleCapturedViewAt(releasedChild.getWidth(),0);
			else{
				if(releasedChild.getLeft()>releasedChild.getWidth()/2)
					viewDragHelper.settleCapturedViewAt(releasedChild.getWidth(),0);
				else
					viewDragHelper.settleCapturedViewAt(0,0);
			}
			ViewCompat.postOnAnimation(releasedChild,new StateAnime(releasedChild));
		}
	}
	class StateAnime implements Runnable
	{
		View v;
		public StateAnime(View v){
			this.v=v;
		}
		@Override
		public void run()
		{
			if(viewDragHelper.continueSettling(true)){
				ViewCompat.postOnAnimation(v,this);
			}
				else if(v.getLeft()==v.getWidth()&&v.getContext() instanceof Activity){
					((Activity)v.getContext()).getWindow().getDecorView().setAlpha(0);
					((Activity)v.getContext()).finish();
				}
		}
	}
}
