package com.moe.LiveVisualizer.draw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Shader;
import com.moe.LiveVisualizer.service.LiveWallpaper.WallpaperEngine;
import android.graphics.Color;
import android.animation.ValueAnimator;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.animation.TypeEvaluator;
import android.animation.PropertyValuesHolder;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.animation.ArgbEvaluator;
import com.moe.LiveVisualizer.utils.ColorList;

abstract class Draw implements com.moe.LiveVisualizer.inter.Draw
{
	private boolean round,finalize;
	private int[] fade=new int[2];
	private Handler handler;
	private int index,color_step;
	private ImageDraw draw;
	private ValueAnimator anime;
	private LiveWallpaper.WallpaperEngine engine;
	private boolean isInterval;
	private ArgbEvaluator argb;
	Draw(ImageDraw draw)
	{
		
		this.draw = draw;
		this.engine = draw.getEngine();
		round=engine.getPreference().getBoolean("round",false);
		
	}

	@Override
	final public void setRound(boolean round)
	{
		this.round=round;
	}

	public Paint.Cap getRound(){
		return round?Paint.Cap.ROUND:Paint.Cap.SQUARE;
	}
	@Override
	public LiveWallpaper.WallpaperEngine getEngine()
	{
		return engine;
	}

	@Override
	public double[] getFft()
	{
		return draw.getFft();
	}

	@Override
	public byte[] getWave()
	{
		return draw.getWave();
	}

	@Override
	public Shader getShader()
	{
		return draw.getShader();
	}
	
	@Override
	public int getColor()
	{
		if(handler==null){
			handler = new Handler(Looper.getMainLooper(), new Handler.Callback(){

					@Override
					public boolean handleMessage(Message p1)
					{
						anime.start();
						return false;
					}
				});
				argb=new ArgbEvaluator();
			anime=new ValueAnimator();
			anime.setRepeatCount(0);
			anime.setIntValues(0);
			anime.setRepeatMode(ValueAnimator.RESTART);
		}
		switch(engine.getColorList().size()){
			case 0:
				return 0xff39c5bb;
			case 1:
				return engine.getColorList().get(0);
				default:
		if(anime.isRunning()){
			if(isInterval)
				return fade[0];
				else
			return evaluate(anime.getAnimatedFraction(),fade);
		}else if(isInterval){
				//进入渐变
				int readyIndex=index+1;
				if(readyIndex>=engine.getColorList().size())
					readyIndex=0;
					fade[0]=engine.getColorList().get(index);
					fade[1]=engine.getColorList().get(readyIndex);
					anime.setDuration(getEngine().getPreference().getInt("nenofade",2)*1000);
					isInterval=false;
					handler.sendEmptyMessage(0);
					
			}else{
				index++;
				if(index>=engine.getColorList().size())
					index=0;
				fade[0]=(engine.getColorList().get(index));
				isInterval=true;
				anime.setDuration(getEngine().getPreference().getInt("nenointerval",5)*1000);
				handler.sendEmptyMessage(0);
			}
			return fade[0];
			}
	}

	@Override
	public float getInterpolator(float interpolator)
	{
		if(draw==null)return interpolator;
		return draw.getInterpolation(interpolator);
	}


	public void draw(Canvas canvas)
	{
		color_step=0;
		if(draw!=null)
		onDraw(canvas, Integer.parseInt(draw.getColorMode()));
	}

	public Integer evaluate(float fraction,int... n)
	{
		return (Integer)argb.evaluate(fraction,n[0],n[1]);
		/*int p2=n[0];
		int p3=n[1];
		int startA=Color.alpha(p2);
		int endA=Color.alpha(p3);
		int startRed = Color.red(p2);
		int startGreen = Color.green(p2);
		int startBlue = Color.blue(p2); 
		int endRed = Color.red(p3);
		int endGreen = Color.green(p3);
		int endBlue = Color.blue(p3); 

		int alpha=getCurrentColor(startA,endA,fraction);
		int red=getCurrentColor(startRed,endRed,fraction);
		int green=getCurrentColor(startGreen,endGreen,fraction);
		int blue=getCurrentColor(startBlue,endBlue,fraction);

		return Color.argb(alpha,red,green,blue);*/
	}

	/*private int getCurrentColor(int start, int end,float fraction) {  
		return start-(int)((start-end)*fraction);
	}*/

	

	
	@Override
	public void setOffsetX(int x)
	{
		// TODO: Implement this method
	}

	@Override
	public void setOffsetY(int y)
	{
		// TODO: Implement this method
	}

	@Override
	public void finalized()
	{
	draw=null;
	anime=null;
	engine=null;
	finalize=true;
	}

	@Override
	public boolean isFinalized()
	{
		// TODO: Implement this method
		return finalize;
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		finalized();
	}

	@Override
	public void checkMode(int mode, Paint paint)
	{
		final LiveWallpaper.WallpaperEngine engine=getEngine();
		if(getEngine()==null)return;
		final ColorList colorList=engine.getColorList();
		if(colorList==null)return;
		switch (mode)
		{
			case 1:
				paint.setColor(colorList.get(color_step));
				color_step++;
				if (color_step >= colorList.size())
					color_step = 0;
				
				break;
			case 2:
				paint.setColor(0xff000000 | (int)(Math.random() * 0xffffff));
				break;
			case 4:
				int color=colorList.get(color_step);
				paint.setColor(engine.getPreference().getBoolean("nenosync", false) ?color: 0xffffffff);
				color_step++;
				if (color_step >= colorList.size())
					color_step = 0;
				paint.setShadowLayer(paint.getStrokeWidth(), 0, 0, color);
				break;
		}
	}
	

}
