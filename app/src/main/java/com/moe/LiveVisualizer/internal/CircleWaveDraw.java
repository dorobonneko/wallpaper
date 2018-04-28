package com.moe.LiveVisualizer.internal;
import android.graphics.Paint;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;

public class CircleWaveDraw extends ImageDraw
{
	private static CircleWaveDraw line;
	private Paint paint;
	private ImageDraw draw;
	private float[] tmpData;
	public static CircleWaveDraw getInstance(ImageDraw draw,LiveWallpaper.MoeEngine engine){
		if(line==null){
			synchronized(LineDraw.class){
				if(line==null)line=new CircleWaveDraw(draw,engine);
			}
		}
		return line;
	}
	private CircleWaveDraw(ImageDraw draw,LiveWallpaper.MoeEngine engine){
		super(engine);
		this.draw=draw;
		paint=new Paint();
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(2);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
	}

	@Override
	protected byte[] getBuffer()
	{
		return draw.getBuffer();
	}

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		// TODO: Implement this method
	}

	
}
