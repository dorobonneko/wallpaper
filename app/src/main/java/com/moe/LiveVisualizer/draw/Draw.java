package com.moe.LiveVisualizer.draw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Shader;
import com.moe.LiveVisualizer.LiveWallpaper.WallpaperEngine;
import android.graphics.Color;
import android.animation.ValueAnimator;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.animation.TypeEvaluator;
import android.animation.PropertyValuesHolder;
import android.graphics.Matrix;

abstract class Draw implements com.moe.LiveVisualizer.inter.Draw
{
	private int[] fade=new int[2];
	private Handler handler;
	private int index;
	private ImageDraw draw;
	private ValueAnimator anime;
	private LiveWallpaper.WallpaperEngine engine;
	Draw(ImageDraw draw, LiveWallpaper.WallpaperEngine engine)
	{
		handler = new Handler(Looper.getMainLooper(), new Handler.Callback(){

				@Override
				public boolean handleMessage(Message p1)
				{
				anime.start();
					return false;
				}
			});
		this.draw = draw;
		this.engine = engine;
		anime=new ValueAnimator();
		anime.setRepeatCount(0);
		anime.setIntValues(0);
		anime.setRepeatMode(ValueAnimator.RESTART);
	}

	@Override
	public float getDownSpeed()
	{
		// TODO: Implement this method
		return draw.getDownSpeed();
	}


	@Override
	public void setFade(Shader shader)
	{
		draw.setFade(shader);
	}

	@Override
	public Shader getFade()
	{
		// TODO: Implement this method
		return draw.getFade();
	}

	@Override
	public LiveWallpaper.WallpaperEngine getEngine()
	{
		// TODO: Implement this method
		return engine;
	}

	@Override
	public double[] getFft()
	{
		return draw.getFft();
	}

	@Override
	public void setShader(Shader shader)
	{
		draw.setShader(shader);
	}

	@Override
	public Shader getShader()
	{
		return draw.getShader();
	}
	
	@Override
	public int getColor()
	{
		switch(engine.getColorList().size()){
			case 0:
				return 0xff39c5bb;
			case 1:
				return engine.getColorList().get(0);
				default:
		if(anime.isRunning()){
			if(anime.getDuration()==5000)
				return fade[0];
				else
			return evaluate(anime.getAnimatedFraction(),fade);
		}else if(anime.getDuration()==5000){
				//进入渐变
				int readyIndex=index+1;
				if(readyIndex>=engine.getColorList().size())
					readyIndex=0;
					fade[0]=engine.getColorList().get(index);
					fade[1]=engine.getColorList().get(readyIndex);
					anime.setDuration(2000);
					handler.sendEmptyMessage(0);
					
			}else{
				index++;
				if(index>=engine.getColorList().size())
					index=0;
				fade[0]=(engine.getColorList().get(index));
				anime.setDuration(5000);
				handler.sendEmptyMessage(0);
			}
			return fade[0];
			}
	}
/*

private int getMiddleColor(int c1,int c2){
	return Color.argb(Color.alpha(c1)+Color.alpha(c2)/2,Color.red(c1)+Color.red(c2)/2,Color.green(c1)+Color.green(c2)/2,Color.blue(c1)+Color.blue(c2)/2);
}*/



	public void draw(Canvas canvas)
	{
		onDraw(canvas, Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode", "0")));
	}

	public Integer evaluate(float fraction,int... n)
	{
		int p2=n[0];
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

		return Color.argb(alpha,red,green,blue);
	}

	private int getCurrentColor(int start, int end,float fraction) {  
		return start-(int)((start-end)*fraction);
	}

	@Override
	public void setCutImage(boolean cut)
	{
		
	}

	@Override
	public Matrix getCenterScale()
	{
		return draw.getCenterScale();
	}

	

}
