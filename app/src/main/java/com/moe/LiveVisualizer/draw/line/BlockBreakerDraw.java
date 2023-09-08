package com.moe.LiveVisualizer.draw.line;
import com.moe.LiveVisualizer.draw.LineDraw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import com.moe.LiveVisualizer.utils.ColorList;

public class BlockBreakerDraw extends LineDraw
{
	private float[] points,breaker;
	public BlockBreakerDraw(ImageDraw draw)
	{
		super(draw);
	}

	@Override
	public void drawGraph(double[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		Paint paint=getPaint();
		if (points == null || points.length != size())
		{
			points = new float[size()];
			breaker = new float[points.length];
		}
		float x=getStartOffset();//起始像素
		final float halfWidth=getBorderWidth() / 2;
		for (int i=0;i < points.length;i ++)
		{
			if (useMode)
				checkMode(color_mode,paint);
			float height=(float)(buffer[i] / (double)Byte.MAX_VALUE * getBorderHeight());
			if (height < points[i])
				points[i]=Math.max(0,points[i]-(points[i]-height)*getInterpolator((points[i]-height)/points[i]*0.99f));
            else if(height>points[i]){
                points[i]=points[i]+(height-points[i])*getInterpolator((height-points[i])/height*0.99f);
            }
			if (height < breaker[i])
				breaker[i] =Math.max(0,breaker[i]-(breaker[i]-height)*getInterpolator((breaker[i]-height)/breaker[i]*0.89f)*0.45f);
            else if(height>breaker[i])
			    breaker[i]=points[i];
			canvas.drawRect(x, getDrawHeight() - points[i], x + getBorderWidth() , getDrawHeight(), paint);
			canvas.drawRect(x, getDrawHeight() - breaker[i] - halfWidth, x + getBorderWidth() , getDrawHeight() - breaker[i], paint);
			x += getSpaceWidth();
		}

	}
}
