package com.moe.graphics;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Path;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.app.Application;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.graphics.drawable.Animatable;
import android.animation.ValueAnimator;
import android.animation.ObjectAnimator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;

public class ProgressDrawable extends Drawable implements Application.ActivityLifecycleCallbacks,Animatable,ValueAnimator.AnimatorUpdateListener
{

	
	private ValueAnimator anime;
	private int state=-1;
	private Paint paint;
	private float progress;
	private Context context;
	public ProgressDrawable(Context context){
		this.context=context;
		if(context instanceof Activity)
		((Application)context.getApplicationContext()).registerActivityLifecycleCallbacks(this);
		renderAnime();
		paint=new Paint();
		TypedArray ta=context.obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.colorControlNormal});
		paint.setColor(ta.getColor(0,0xff000000));
		ta.recycle();
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,context.getResources().getDisplayMetrics()));
	}
	private void renderAnime(){
		anime=ObjectAnimator.ofFloat(new float[]{0,1});
		anime.setDuration(500);
		//anime.setInterpolator(new FastOutSlowInInterpolator());
		anime.addUpdateListener(this);
		anime.setRepeatCount(anime.INFINITE);
		anime.setRepeatMode(anime.RESTART);
	}
	public void setProgressState(int state)
	{
		this.state = state;
		if(getCallback()!=null)
			getCallback().invalidateDrawable(this);
			if(!isRunning())
				start();
	}

	public int getProgressState()
	{
		return state;
	}
	
	@Override
	public void draw(Canvas p1)
	{
		//p1.drawColor(0xff000000);
		switch(state){
			case State.PROGRESS:
				p1.save();
				p1.rotate(progress*360,p1.getWidth()/2,p1.getHeight()/2);
				Path refresh=new Path();
				//refresh.addArc(getIntrinsicWidth()*0.25f,getIntrinsicHeight()*0.25f,getIntrinsicWidth()*0.75f,getIntrinsicHeight()*0.75f,70,290);
				//refresh.arcTo(getIntrinsicWidth()*0.25f-3,getIntrinsicHeight()*0.25f-3,getIntrinsicWidth()*0.75f-3,getIntrinsicHeight()*0.75f-3,70,290,true);
				refresh.moveTo(p1.getWidth()*0.5f+paint.getStrokeWidth()/2,p1.getHeight()/2);
				refresh.rLineTo(p1.getWidth()*0.25f,0);
				refresh.rLineTo(-p1.getWidth()/4*0.5f,p1.getHeight()/4*0.5f);
				//refresh.rLineTo(-p1.getWidth()/4*0.5f,-p1.getHeight()/4*0.5f);
				refresh.close();
				paint.setStyle(Paint.Style.FILL);
				p1.drawPath(refresh,paint);
				paint.setStyle(Paint.Style.STROKE);
				p1.drawArc(p1.getWidth()*0.35f,p1.getHeight()*0.35f,p1.getWidth()*0.65f,p1.getHeight()*0.65f,60,300,false,paint);
				//p1.drawRect(0,0,p1.getWidth()/4,10,paint);
				p1.restore();
				break;
			case State.ERROR:
				paint.setStyle(Paint.Style.FILL);
				p1.drawLine(p1.getWidth()*0.35f,p1.getHeight()*0.35f,p1.getWidth()*0.65f,p1.getHeight()*0.65f,paint);
				p1.drawLine(p1.getWidth()*0.65f,p1.getHeight()*0.35f,p1.getWidth()*0.35f,p1.getHeight()*0.65f,paint);
				break;
			case State.SUCCESS:
				paint.setStyle(Paint.Style.FILL);
				p1.drawLine(p1.getWidth()*0.35f,p1.getHeight()*0.5f,p1.getWidth()*0.5f,p1.getHeight()*0.65f,paint);
				p1.drawLine(p1.getWidth()*0.5f-paint.getStrokeWidth()/2,p1.getHeight()*0.65f,p1.getWidth()*0.7f,p1.getHeight()*0.35f,paint);
				break;
		}
		
	}

	@Override
	public void setAlpha(int p1)
	{
		paint.setAlpha(p1);
	}
	@Override
	public void onAnimationUpdate(ValueAnimator p1)
	{
		progress=p1.getAnimatedValue();
		if(getCallback()!=null)
			getCallback().invalidateDrawable(this);
	}


	@Override
	public void start()
	{
		anime.start();
	}

	@Override
	public void stop()
	{
		anime.cancel();
	}

	@Override
	public boolean isRunning()
	{
		return anime!=null&&anime.isRunning();
	}
	@Override
	public void setColorFilter(ColorFilter p1)
	{
		paint.setColorFilter(p1);
	}

	@Override
	public int getOpacity()
	{
		return PixelFormat.TRANSLUCENT;
	}
	@Override
	public void onActivityCreated(Activity p1, Bundle p2)
	{
		// TODO: Implement this method
	}

	@Override
	public void onActivityStarted(Activity p1)
	{
		if(context==p1)
		anime.start();
	}

	@Override
	public void onActivityResumed(Activity p1)
	{if(context==p1)
		anime.resume();
	}

	@Override
	public void onActivityPaused(Activity p1)
	{if(context==p1)
		anime.pause();
	}

	@Override
	public void onActivityStopped(Activity p1)
	{if(context==p1)
		anime.end();
	}

	@Override
	public void onActivitySaveInstanceState(Activity p1, Bundle p2)
	{
		// TODO: Implement this method
	}

	@Override
	public void onActivityDestroyed(Activity p1)
	{
		if(context==p1){
			context=null;
			setCallback(null);
		((Application)p1.getApplicationContext()).unregisterActivityLifecycleCallbacks(this);
		}
	}
	
	public static class State{
		public final static int PROGRESS=0;
		public final static int ERROR=1;
		public final static int SUCCESS=2;
	}
}
