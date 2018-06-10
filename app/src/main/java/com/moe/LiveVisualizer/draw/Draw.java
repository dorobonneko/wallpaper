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

abstract class Draw implements com.moe.LiveVisualizer.inter.Draw
{
	private boolean round;
	private int[] fade=new int[2];
	private Handler handler;
	private int index;
	private ImageDraw draw;
	private ValueAnimator anime;
	private LiveWallpaper.WallpaperEngine engine;
	private boolean isInterval;
	Draw(ImageDraw draw)
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
		this.engine = draw.getEngine();
		round=engine.getPreference().getBoolean("round",false);
		anime=new ValueAnimator();
		anime.setRepeatCount(0);
		anime.setIntValues(0);
		anime.setRepeatMode(ValueAnimator.RESTART);
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
	public float getDownSpeed()
	{
		// TODO: Implement this method
		return draw.getDownSpeed();
	}
	@Override
	public LiveWallpaper.WallpaperEngine getEngine()
	{
		// TODO: Implement this method
		return engine;
	}

	@Override
	public byte[] getFft()
	{
		return draw.getFft();
	}

	@Override
	public byte[] getWave()
	{
		return draw.getWave();
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
/*

private int getMiddleColor(int c1,int c2){
	return Color.argb(Color.alpha(c1)+Color.alpha(c2)/2,Color.red(c1)+Color.red(c2)/2,Color.green(c1)+Color.green(c2)/2,Color.blue(c1)+Color.blue(c2)/2);
}*/



	public void draw(Canvas canvas)
	{
		onDraw(canvas, Integer.parseInt(draw.getColorMode()));
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
	public void setOffsetX(int x)
	{
		// TODO: Implement this method
	}

	@Override
	public void setOffsetY(int y)
	{
		// TODO: Implement this method
	}



	

}
