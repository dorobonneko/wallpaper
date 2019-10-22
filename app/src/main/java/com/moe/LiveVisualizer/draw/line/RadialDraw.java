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

	/*@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		Paint paint=getPaint();
		//paint.setStrokeCap(Paint.Cap.SQUARE);
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
					}
				break;
			case 1:
			case 2:
				if(getEngine()!=null)
				switch ( getEngine().getColorList().size() )
				{
					case 0:
						paint.setColor(0xff39c5bb);
						break;
					default:
						paint.setColor(getEngine().getColorList().get(0));
						break;
				}
			case 4:
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
		float x=0;//起始像素
		final float halfWidth=getBorderWidth()/ 2;
		for ( int i=0;i < points.length;i ++ )
		{
			if(useMode)
				checkMode(color_mode,paint);
			float height=(float)(buffer[i] / 127d * getBorderHeight());
			if ( height < points[i] )
				height=points[i]-(points[i]-height)*getInterpolator(1-(points[i]-height)/getBorderHeight());
			if ( height < 0 )height = 0;
			points[i] = height;
			if ( paint.getStrokeCap() != Paint.Cap.ROUND )
			{
				canvas.drawRect(x, getDrawHeight() - height, x + getBorderWidth() , getDrawHeight()+height, paint);
				//if(getRound()==Paint.Cap.ROUND)
				//canvas.drawArc(x,getDrawHeight()-height+halfWidth,x+getBorderWidth(),getDrawHeight()-height-halfWidth,180,360,true,paint);
				//canvas.drawRect(x, getDrawHeight() + height, x + getBorderWidth(), getDrawHeight(), paint);
				x += getSpaceWidth();
			}
			else
			{
				canvas.drawLine(x + halfWidth, getDrawHeight() - height, x+halfWidth, getDrawHeight()+height, paint);
				//canvas.drawLine(x, getDrawHeight() + height + halfWidth, x, getDrawHeight() + halfWidth, paint);
				x += getSpaceWidth();
			}

		}

	}
}
