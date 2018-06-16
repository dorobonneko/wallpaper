package com.moe.LiveVisualizer.duang;
import android.animation.TypeEvaluator;
import android.graphics.Path;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.Random;
import android.util.DisplayMetrics;

public abstract class Duang
{
	private float offsetX,offsetY,size;
	private Random random;
	private Engine engine;
	private boolean first=true;
	public Duang(){
		random=new Random(System.nanoTime());
	}
	public Random getRandom(){
		return random;
	}
	public boolean isFirst(){
		boolean flag=first;
		first=false;
		return flag;
	}
	final void setEngine(Engine engine){
		this.engine=engine;
	}
	public Engine getEngine(){
		return engine;
	}
	public int getMinSize(){
		return engine.getMinSize();
	}
	public int getMaxSize(){
		return engine.getMxSize();
	}
	public int getWind()
	{
		return engine.getWind();
	}

	public int getMaxWidth()
	{
		return getDisplay().widthPixels;
	}


	public int getMaxHeight()
	{
		return getDisplay().heightPixels;
	}
	public DisplayMetrics getDisplay(){
		return engine.getDisplay();
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

	

	public int getSpeed()
	{
		return engine.getSpeed();
	}

	public abstract void draw(Canvas canvas);
	public abstract void random(Random random);
	public void reset(boolean random){
		if(random){
			setSize(this.random.nextFloat()*(getMaxSize()-getMinSize())+getMinSize());
			random(this.random);
			}else
			{
				offsetX=0;
				offsetY=0;
				size=0;
			}
	}
	public void release(){
		reset(false);
		engine=null;
		try
		{
			finalize();
		}
		catch (Throwable e)
		{}
	}

	
}
