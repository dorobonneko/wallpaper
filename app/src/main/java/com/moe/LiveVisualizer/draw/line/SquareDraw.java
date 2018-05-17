package com.moe.LiveVisualizer.draw.line;
import com.moe.LiveVisualizer.draw.LineDraw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;

public class SquareDraw extends LineDraw
{
	public SquareDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		super(draw,engine);
	}

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
	}

	@Override
	public void drawGraph(byte[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		// TODO: Implement this method
	}


	
}
