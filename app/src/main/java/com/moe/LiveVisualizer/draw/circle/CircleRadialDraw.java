package com.moe.LiveVisualizer.draw.circle;
import android.graphics.Paint;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.RadialGradient;
import android.graphics.SweepGradient;
import android.graphics.Point;
import android.graphics.PointF;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.internal.OnColorSizeChangedListener;
import android.graphics.RectF;
import android.content.SharedPreferences;
import com.moe.LiveVisualizer.utils.ColorList;

public class CircleRadialDraw extends RingDraw
{
	private float[] points;
		public CircleRadialDraw(ImageDraw draw)
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
	public int size()
	{
		return super.size()/2;
	}

	@Override
	public void drawGraph(double[] buffer, Canvas canvas, final int color_mode,boolean useMode)
	{
		PointF point=getPointF();
		float radius=getRadius();
		final float radialHeight=getDirection()==OUTSIDE?getBorderHeight():getRadius();
		Paint paint=getPaint();
		paint.setStrokeWidth(getBorderWidth());
		if ( points == null || points.length != size() )
			points = new float[size()];
		float degress_step=180f / size();
		canvas.save();
		final PointF center=getPointF();
		canvas.rotate(degress_step / 2.0f, center.x, center.y);
		int end=size() - 1;
		for ( int i=0;i < points.length;i ++ )
		{
			if(useMode)
				checkMode(color_mode,paint);
			float height=(float) (buffer[i] / 127d * radialHeight);
			if ( height < points[i] )
                points[i]=Math.max(0,points[i]-(points[i]-height)*getInterpolator((points[i]-height)/points[i])*0.8f);
            else if(height>points[i])
                points[i]=points[i]+(height-points[i])*getInterpolator((height-points[i])/height);
                height=points[i];
			if(paint.getStrokeCap()==Paint.Cap.ROUND)
			canvas.drawLine(point.x,point.y-radius, point.x, point.y -radius+(getDirection()==OUTSIDE?- height:height), paint);
			else
			canvas.drawRect(point.x-getBorderWidth()/2, point.y-radius, point.x + getBorderWidth()/2, point.y -radius+(getDirection()==OUTSIDE?- height:height), paint);
			canvas.rotate(degress_step, center.x, center.y);
			//degress+=degress_step;
			if ( i == end )
			{
				if ( degress_step > 0 )
				{
					canvas.restore();
					canvas.save();
					degress_step = -degress_step;
					canvas.rotate(degress_step / 2.0f, center.x, center.y);
					i = -1;
				}
				else
				{
					break;
				}
			}
		}
		canvas.restore();
	}

}
