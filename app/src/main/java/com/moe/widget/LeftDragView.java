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

public class LeftDragView extends FrameLayout
{
	private ViewDragHelper viewDragHelper;
	public LeftDragView(Context context,AttributeSet attrs){
		super(context,attrs);
		viewDragHelper=ViewDragHelper.create(this,new DragCallback());
		viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
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
				ViewCompat.postOnAnimationDelayed(v,this,16);
				}
				else if(v.getLeft()==v.getWidth()&&v.getContext() instanceof Activity){
					((Activity)v.getContext()).getWindow().getDecorView().setAlpha(0);
					((Activity)v.getContext()).finish();
				}
		}
	}
}
