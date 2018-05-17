package com.moe.LiveVisualizer.draw.line;
import android.graphics.Canvas;
import android.content.SharedPreferences;
import com.moe.LiveVisualizer.utils.ColorList;
import com.moe.LiveVisualizer.LiveWallpaper;
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
	public RadialDraw(ImageDraw draw, LiveWallpaper.WallpaperEngine engine)
	{
		super(draw, engine);

	}

	@Override
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
					}
				break;
			case 1:
				drawGraph(getFft(), canvas, color_mode, true);
				break;
			case 2:
				drawGraph(getFft(), canvas, color_mode, true);
				break;
			case 3:
				int color=getColor();
				paint.setColor(getEngine().getSharedPreferences().getBoolean("nenosync",false)?color:0xffffffff);
				paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
				drawGraph(getFft(), canvas, color_mode, false);
				paint.setShadowLayer(0, 0, 0, 0);
				break;
		}
	}

	@Override
	public void drawGraph(byte[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		Paint paint=getPaint();
		if ( points == null || points.length != size() )
			points = new float[size()];
		float x=0;//起始像素
		int colorStep=0;
		int mode=Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode", "0"));
		final float halfWidth=getBorderWidth()/ 2;
		for ( int i=0;i < size();i ++ )
		{
			if ( useMode )
			{
				if ( mode == 1 )
				{
					paint.setColor(getEngine().getColorList().get(colorStep));
					colorStep++;
					if ( colorStep >= getEngine().getColorList().size() )colorStep = 0;
				}
				else if ( mode == 2 )
				{
					paint.setColor((int)(Math.random() * 0xffffff) | 0xff000000);
				}
			}
			float height=(float)(buffer[i] / 127d * getBorderHeight());
			if ( height > points[i] )
				points[i] = height;
			else
				height = points[i] - (points[i] - height) * getDownSpeed();
			if ( height < 0 )height = 0;
			points[i] = height;
			if ( paint.getStrokeCap() == Paint.Cap.SQUARE )
			{
				canvas.drawRect(x, getDrawHeight() - height, x += getBorderWidth() , getDrawHeight(), paint);
				canvas.drawRect(x -= getBorderWidth(), getDrawHeight() + height, x += getBorderWidth(), getDrawHeight(), paint);
				x += getSpaceWidth();
			}
			else
			{
				canvas.drawLine(x += halfWidth, getDrawHeight() - height-halfWidth, x, getDrawHeight()-halfWidth, paint);
				canvas.drawLine(x, getDrawHeight() + height + halfWidth, x, getDrawHeight() + halfWidth, paint);
				x += halfWidth + getSpaceWidth();
			}

		}

	}
}