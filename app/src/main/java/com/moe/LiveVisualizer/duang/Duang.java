package com.moe.LiveVisualizer.duang;
import android.animation.TypeEvaluator;
import android.graphics.Path;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.Random;
import android.util.DisplayMetrics;

public abstract class Duang
{
	private float offsetX,offsetY,size,speed;
	private Random random;
	private DisplayMetrics display;
	private int mWind,maxSize,minSize;
	public Duang(DisplayMetrics display,int maxSize,int minSize,int wind,int speed){
		this.display=display;
		random=new Random(System.currentTimeMillis());
		this.minSize=minSize;
		this.maxSize=maxSize;
		this.mWind=wind;
		this.speed=speed;
	}
	public void setMinSize(int size){
		this.minSize=size;
	}
	public void setMaxSize(int size){
		this.maxSize=size;
	}
	public int getMinSize(){
		return minSize;
	}
	public int getMaxSize(){
		return maxSize;
	}
	protected void setWind(int wind)
	{
		this.mWind = wind;
	}

	public int getWind()
	{
		return mWind;
	}

	public int getMaxWidth()
	{
		return display.widthPixels;
	}


	public int getMaxHeight()
	{
		return display.heightPixels;
	}
	public DisplayMetrics getDisplay(){
		return display;
	}
	protected void setOffsetX(float offsetX)
	{
		this.offsetX = offsetX;
	}

	public float getOffsetX()
	{
		return offsetX;
	}

	protected void setOffsetY(float offsetY)
	{
		this.offsetY = offsetY;
	}

	public float getOffsetY()
	{
		return offsetY;
	}

	protected void setSize(float size)
	{
		this.size = size;
	}

	public float getSize()
	{
		return size;
	}

	protected void setSpeed(float speed)
	{
		this.speed = speed;
	}

	public float getSpeed()
	{
		return speed;
	}

	public abstract void draw(Canvas canvas);
	public abstract void random(Random random);
	public void reset(boolean random){
		if(random)
			random(this.random);
			else
			{
				offsetX=0;
				offsetY=0;
				size=0;
				speed=0;
				mWind=0;
			}
	}
	public void release(){
		reset(false);
		try
		{
			finalize();
		}
		catch (Throwable e)
		{}
	}
	}
