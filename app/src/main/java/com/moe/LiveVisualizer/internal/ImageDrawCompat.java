package com.moe.LiveVisualizer.internal;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;

public class ImageDrawCompat extends ImageDraw
{
	private ImageDrawCompat(LiveWallpaper.WallpaperEngine engine){
		super(engine);
	}
	public static ImageDrawCompat getInstance(LiveWallpaper.WallpaperEngine engine){
		return new ImageDrawCompat(engine);
	}
	@Override
	public void onDraw(Canvas canvas,int mode)
	{
		// TODO: Implement this method
	}

	
}
