package com.moe.LiveVisualizer.draw.line;
import android.graphics.Canvas;
import android.content.SharedPreferences;
import com.moe.LiveVisualizer.utils.ColorList;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.draw.CircleDraw;
import com.moe.LiveVisualizer.draw.LineDraw;

public class RadialDraw extends LineDraw
{
	private float[] points;
	public RadialDraw(ImageDraw draw)
	{
		super(draw);
	}

	@Override
	public void drawGraph(double[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		Paint paint=getPaint();
		if ( points == null || points.length != size() )
			points = new float[size()];
		float x=getStartOffset();//起始像素
		final float halfWidth=getBorderWidth()/ 2;
		for ( int i=0;i < points.length;i ++ )
		{
			if(useMode)
				checkMode(color_mode,paint);
			float height=(float)(buffer[i] / 127d * getBorderHeight());
			if ( height < points[i] )
				points[i]=Math.max(0,points[i]-(points[i]-height)*getInterpolator((points[i]-height)/points[i]*0.8f)*0.45f);
            else if(height>points[i])
                points[i]=points[i]+(height-points[i])*getInterpolator((height-points[i])/height);
			if ( paint.getStrokeCap() != Paint.Cap.ROUND )
			{
				canvas.drawRect(x, getDrawHeight() - points[i], x + getBorderWidth() , getDrawHeight()+points[i], paint);
				x += getSpaceWidth();
			}
			else
			{
				canvas.drawLine(x + halfWidth, getDrawHeight() - points[i], x+halfWidth, getDrawHeight()+points[i], paint);
				//canvas.drawLine(x, getDrawHeight() + height + halfWidth, x, getDrawHeight() + halfWidth, paint);
				x += getSpaceWidth();
			}

		}

	}
}
