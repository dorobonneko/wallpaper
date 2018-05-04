package com.moe.LiveVisualizer.draw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Shader;
import com.moe.LiveVisualizer.LiveWallpaper.WallpaperEngine;

abstract class Draw implements com.moe.LiveVisualizer.inter.Draw
{
	private int index;
	private ImageDraw draw;
	private long oldTime;
	private LiveWallpaper.WallpaperEngine engine;
	Draw(ImageDraw draw, LiveWallpaper.WallpaperEngine engine)
	{
		this.draw = draw;
		this.engine = engine;
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
	public int getColor()
	{
		switch ( engine.getColorList().size() )
		{
			case 0:
				return 0xff39c5bb;
			case 1:
				return engine.getColorList().get(0);
			default:
				if ( System.nanoTime() - oldTime > 5000000000l )
				{
					index++;
					if ( index >= engine.getColorList().size() )
						index = 0;
					oldTime = System.nanoTime();
				}
				return engine.getColorList().get(index);
		}


	}






	public void draw(Canvas canvas)
	{
		onDraw(canvas, Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode", "0")));
	}

}
