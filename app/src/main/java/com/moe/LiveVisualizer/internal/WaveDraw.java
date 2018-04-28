package com.moe.LiveVisualizer.internal;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Path;

public class WaveDraw extends ImageDraw
{
	private static WaveDraw line;
	private Paint paint;
	private ImageDraw draw;
	private float[] tmpData;
	public static WaveDraw getInstance(ImageDraw draw,LiveWallpaper.MoeEngine engine){
		if(line==null){
			synchronized(LineDraw.class){
				if(line==null)line=new WaveDraw(draw,engine);
			}
		}
		return line;
	}
	private WaveDraw(ImageDraw draw,LiveWallpaper.MoeEngine engine){
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
		switch(color_mode){
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
		}
		wave(getBuffer(),canvas);
	}
	private void wave(byte[] buffer,Canvas canvas){
		float width=((float)canvas.getWidth()/64);
		float offsetX=0;
		Path path=new Path();
		path.moveTo(0,canvas.getHeight()/2);
		int step=buffer.length/64;
		float[] points=new float[6];
		path.moveTo(0,canvas.getHeight()/2.0f+buffer[0]);//起始点
		for(int i=1;i<buffer.length-step;i+=step){
			//第一个控制点
			points[0]=offsetX+=width/4.0f;
			points[1]=canvas.getHeight()/2.0f+buffer[i];
			//第二个控制点
			points[2]=offsetX+=width/2.0f;
			points[3]=points[1];
			//终点
			points[4]=offsetX+=width/2.0f;
			points[5]=canvas.getHeight()/2.0f;
			path.quadTo(points[0],points[1],points[2],points[3]);
		}
		canvas.drawPath(path,paint);
		}
	
}
