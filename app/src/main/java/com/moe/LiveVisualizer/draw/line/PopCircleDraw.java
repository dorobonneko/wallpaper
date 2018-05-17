package com.moe.LiveVisualizer.draw.line;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.LinearGradient;
import android.view.SurfaceHolder;
import android.graphics.Shader;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.draw.LineDraw;

public class PopCircleDraw extends LineDraw
{
	private float[] points;
	public PopCircleDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		super(draw,engine);
		}

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		Paint paint=getPaint();
		switch(color_mode){
			case 0:
				switch(getEngine().getColorList().size()){
					case 0:
						paint.setColor(0xff39c5bb);
						drawGraph(getFft(),canvas,color_mode,false);
						break;
					case 1:
						paint.setColor(getEngine().getColorList().get(0));
						drawGraph(getFft(),canvas,color_mode,false);
						break;
					default:

						paint.setShader(getShader());
						drawGraph(getFft(),canvas,color_mode,false);	
						paint.setShader(null);
						break;
				}
				break;
			case 1:
				drawGraph(getFft(),canvas,color_mode,true);
				break;
			case 2:
				drawGraph(getFft(),canvas,color_mode,true);
				break;
			case 3:
				int color=getColor();
				paint.setColor(getEngine().getSharedPreferences().getBoolean("nenosync",false)?color:0xffffffff);
				paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
				drawGraph(getFft(),canvas,color_mode,false);
				paint.setShadowLayer(0,0,0,0);
				break;
		}
		
	}
	
	@Override
	public void drawGraph(byte[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		Paint paint=getPaint();
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
		if(points==null||points.length!=size())
			points=new float[size()];
		float radius=getBorderWidth()/2.0f;
		float x=radius;//起始像素
		float y=canvas.getHeight() - getEngine().getSharedPreferences().getInt("height", 10) / 100.0f * canvas.getHeight();
		int colorStep=0;
		int mode=Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode", "0"));
		//if ( mode == 3 )
		//	paint.setColor(getEngine().getColor());
		canvas.drawLine(0,y,canvas.getWidth(),y,paint);
		for ( int i=0;i < size();i ++ )
		{
			if(useMode){
				if ( mode == 1 )
				{
					paint.setColor(getEngine().getColorList().get(colorStep));
					colorStep++;
					if ( colorStep >= getEngine().getColorList().size() )colorStep = 0;
				}
				else if ( mode == 2 )
				{
					paint.setColor(0xff000000|(int)(Math.random()*0xffffff));
				}
			}
			float height=(float)(buffer[i]/127d*getBorderHeight());
			if(height>points[i])
				points[i]=height;
			else
				height=points[i]-(points[i]-height)*getDownSpeed();
			if(height<0)height=0;
			points[i]=height;
			canvas.drawCircle(x,y-height-radius,radius,paint);
			x+=(getSpaceWidth()+getBorderWidth());
		}
		paint.setStyle(Paint.Style.FILL);
	}

	
	
}