package com.moe.LiveVisualizer.draw;
import android.graphics.Canvas;
import android.content.Context;
import android.util.AttributeSet;
import com.moe.LiveVisualizer.LiveWallpaper;
import com.moe.LiveVisualizer.internal.ImageDraw;

public class CircleInsideDraw extends Draw
{
	public CircleInsideDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		super(draw,engine);
	}
	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		// TODO: Implement this method
	}

	@Override
	public void onBorderWidthChanged(int width)
	{
		// TODO: Implement this method
	}

	@Override
	public void onBorderHeightChanged(int height)
	{
		// TODO: Implement this method
	}

	@Override
	public void onSpaceWidthChanged(int space)
	{
		// TODO: Implement this method
	}

	@Override
	public void onDrawHeightChanged(float height)
	{
		// TODO: Implement this method
	}

	@Override
	public int size()
	{
		// TODO: Implement this method
		return 0;
	}
	
}
