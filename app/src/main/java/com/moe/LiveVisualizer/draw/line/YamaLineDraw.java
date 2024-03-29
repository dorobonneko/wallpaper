package com.moe.LiveVisualizer.draw.line;
import android.graphics.*;

import android.util.TypedValue;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import com.moe.LiveVisualizer.draw.LineDraw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.utils.ColorList;

public class YamaLineDraw extends LineDraw
{
	private float[] points;
	public YamaLineDraw(ImageDraw draw)
	{
		super(draw);
		}
	
	/*@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		Paint paint=getPaint();
		switch(color_mode){
			case 0:
				switch ( getEngine().getColorList().size() )
				{
					case 0:
						paint.setColor(0xff39c5bb);
						drawGraph(getFft(), canvas, color_mode, false);
						break;
					case 1:
						paint.setColor(getEngine().getColorList().get(0));
						drawGraph(getFft(), canvas, color_mode, false);
						break;
					default:
								paint.setShader(getShader());
								drawGraph(getFft(), canvas, color_mode, false);								
								paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
								paint.setShader(null);
								break;
							}
				break;
			case 1:
			case 4:
			case 2:
				drawGraph(getFft(), canvas, color_mode, true);
				break;
			case 3:
				int color=getColor();
				paint.setColor(getEngine().getPreference().getBoolean("nenosync",false)?color:0xffffffff);
				paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
				drawGraph(getFft(), canvas, color_mode, false);
				paint.setShadowLayer(0, 0, 0, 0);
				break;
		}
		paint.reset();

	}*/
	@Override
	public void drawGraph(double[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		Paint paint=getPaint();
		if ( points == null || points.length != size() )
			points = new float[size()];
		final float halfWidth=getBorderWidth() / 2;
		float x=getStartOffset();
		for ( int i=0;i < points.length;i ++ )
		{
			if(useMode)
				checkMode(color_mode,paint);
			float height=(float)(buffer[i] / 127d * getBorderHeight());
			if ( height < points[i] )
                points[i]=Math.max(0,points[i]-(points[i]-height)*getInterpolator((points[i]-height)/points[i]*0.8f)*0.45f);
            else if(height>points[i])
                points[i]=points[i]+(height-points[i])*getInterpolator((height-points[i])/height);
			height=points[i];
			canvas.drawLine(halfWidth, getDrawHeight(), x+=halfWidth, getDrawHeight()-height, paint);
			canvas.drawLine(x, getDrawHeight() - height, canvas.getWidth()-halfWidth, getDrawHeight(), paint);
			x += halfWidth + getSpaceWidth();
			

		}

	}
}
