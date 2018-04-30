package com.moe.LiveVisualizer.internal;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;

public class RadialDraw extends ImageDraw
{
	public RadialDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		super(draw,engine);
	}

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
	}

	
}
