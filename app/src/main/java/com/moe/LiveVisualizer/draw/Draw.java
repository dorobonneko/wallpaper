package com.moe.LiveVisualizer.draw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Shader;
import com.moe.LiveVisualizer.LiveWallpaper.WallpaperEngine;

abstract class Draw implements com.moe.LiveVisualizer.inter.Draw
{
	private ImageDraw draw;
	private LiveWallpaper.WallpaperEngine engine;
	Draw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		this.draw=draw;
		this.engine=engine;
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





	public void draw(Canvas canvas){
		onDraw(canvas,Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode","0")));
	}
	
}
