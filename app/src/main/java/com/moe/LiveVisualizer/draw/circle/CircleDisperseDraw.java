package com.moe.LiveVisualizer.draw.circle;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.PointF;
import android.graphics.LinearGradient;
import com.moe.LiveVisualizer.utils.ColorList;

public class CircleDisperseDraw extends RingDraw
{
	private float[] points;
	
	public CircleDisperseDraw(ImageDraw draw)
	{
		super(draw);
	}
	
	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		super.onDraw(canvas,color_mode);
		drawCircleImage(canvas);

	}

	@Override
	public void drawGraph(byte[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		PointF point=getPointF();
		float radius=getRadius();
		final float radialHeight=getDirection()==OUTSIDE?getBorderHeight():getRadius();
		Paint paint=getPaint();
		paint.setStrokeWidth(getBorderWidth());
		if ( points == null || points.length != size() )
			points = new float[size()];
		float degress_step=360f / size();
		canvas.save();
		//final PointF center=getPointF();
		canvas.rotate(degress_step / 2f, point.x, point.y);
		for ( int i=0;i < points.length;i ++ )
		{
			if(useMode)
				checkMode(color_mode,paint);
			float height=(float) (buffer[i] / 127d * radialHeight);
			if ( height < points[i] )
				height=points[i]-(points[i]-height)*getInterpolator(1-(points[i]-height)/radialHeight);
			if ( height < 0 )height = 0;
			points[i] = height;
			if(paint.getStrokeCap()==Paint.Cap.ROUND)
				canvas.drawLine(point.x,point.y-radius+(getDirection()==OUTSIDE?- height:height), point.x, point.y -radius+(getDirection()==OUTSIDE?- height:height), paint);
			else
				canvas.drawRect(point.x-getBorderWidth()/2,  point.y -radius+(getDirection()==OUTSIDE?- height:height), point.x + getBorderWidth()/2, point.y -radius+(getDirection()==OUTSIDE?- height-getBorderWidth():height+getBorderWidth()), paint);
			canvas.rotate(degress_step, point.x, point.y);
			//degress+=degress_step;
		}
		canvas.restore();
	}


	
}
