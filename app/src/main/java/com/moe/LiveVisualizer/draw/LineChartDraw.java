package com.moe.LiveVisualizer.draw;
import android.graphics.Paint;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.LinearGradient;
import android.util.TypedValue;
import android.graphics.Shader;
import com.moe.LiveVisualizer.internal.ImageDraw;

public class LineChartDraw extends Draw
{
	private Paint paint;
	private float[] tmpData;
	public LineChartDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine)
	{
		super(draw,engine);
		paint = new Paint();
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(2);
		paint.setAntiAlias(true);
		paint.setDither(true);
	}

	

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		if(color_mode==2){
			spaceLineChart(getFft(),canvas,color_mode);
		}else
		switch ( getEngine().getColorList().size() )
		{
			case 0:
				paint.setColor(0xff39c5bb);
				lineChart(getFft(), canvas);
				break;
			case 1:
				paint.setColor(getEngine().getColorList().get(0));
				lineChart(getFft(), canvas);
				break;
			default:
				switch ( color_mode )
				{
					case 0://色带

						/*final Bitmap src=Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
						final Canvas tmpCanvas=new Canvas(src);

						lineChart(getFft(), tmpCanvas);
						if ( getEngine().getColorList().size() > 1 )
						{										
							paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
							if ( getEngine().getShader() == null )
								getEngine().setShader(new LinearGradient(0, 0, canvas.getWidth(), 0, getEngine().getColorList().toArray(), null, LinearGradient.TileMode.CLAMP));
							paint.setShader(getEngine().getShader());
							tmpCanvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
							//canvas.drawBitmap(shader, 0, 0, paint);
							paint.setShader(null);
							paint.setXfermode(null);
							canvas.drawBitmap(src, 0, 0, paint);
							src.recycle();
							//break;
						}*/
						if ( getEngine().getShader() == null )
								getEngine().setShader(new LinearGradient(0, 0, canvas.getWidth(), 0, getEngine().getColorList().toArray(), null, LinearGradient.TileMode.CLAMP));
							paint.setShader(getEngine().getShader());
						lineChart(getFft(), canvas);
						paint.setShader(null);
						break;
					case 1://间隔
						spaceLineChart(getFft(), canvas, color_mode);
						break;
					case 2://random
						spaceLineChart(getFft(), canvas, color_mode);
						break;
					case 3:
						Shader shader=getFade();
						if(shader==null)
							setFade(shader=new LinearGradient(0,0,0,canvas.getHeight(),getEngine().getColorList().toArray(),null,LinearGradient.TileMode.CLAMP));
						paint.setShader(shader);
					default:
						lineChart(getFft(), canvas);
						paint.setShader(null);

						break;
				}
				break;
		}


	}

	private void lineChart(double[] a, Canvas canvas)
	{
		float borderHeight=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,getEngine().getSharedPreferences().getInt("borderHeight",0),getEngine().getContext().getResources().getDisplayMetrics());
		float offsetY=canvas.getHeight() - getEngine().getSharedPreferences().getInt("height", 10) / 100.0f * canvas.getHeight();
		
		if ( tmpData == null || tmpData.length != 4 * a.length )
		{
			tmpData = new float[4 * a.length];
		}
		float step=((float)canvas.getWidth() / a.length*2);
		float offsetX=0;
		//float offsetY=canvas.getHeight()/2.0f; 
		for ( int i=0;i < a.length/2;i++ )
		{
			if ( i == 0 )
			{
				tmpData[4 * i] = offsetX;
				tmpData[1 + 4 * i] =  offsetY - (float)(a[1 + i]/127.0*borderHeight);
			}
			else
			{
				System.arraycopy(tmpData, 4 * i - 2, tmpData, 4 * i, 2);
			}
			tmpData[2 + 4 * i] = offsetX += step;
			tmpData[3 + 4 * i] = offsetY -(float)( a[1 + i]/127.0*borderHeight);

		}
		canvas.drawLines(tmpData, paint);

	}
	//单独画线
	private void spaceLineChart(double[] data, Canvas canvas, int color_mode)
	{
		float borderHeight=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,getEngine().getSharedPreferences().getInt("borderHeight",0),getEngine().getContext().getResources().getDisplayMetrics());
		float offsetY=canvas.getHeight() - getEngine().getSharedPreferences().getInt("height", 10) / 100.0f * canvas.getHeight();
		
		float step=((float)canvas.getWidth() / data.length*2);
		float offsetX=0;
		float[] tmpData=new float[4];
		int color=0;
		//float offsetY=canvas.getHeight()/2.0f;
		for ( int i=0;i < data.length/2;i++ )
		{
			if ( i == 0 )
			{
				tmpData[0] = offsetX;
				tmpData[1] = offsetY -(float)( data[1 + i]/127.0f*borderHeight) ;
			}
			else
			{
				System.arraycopy(tmpData, 2, tmpData, 0, 2);
			}
			tmpData[2] = offsetX += step;
			tmpData[3] = offsetY -(float)( data[1 + i]/127.0f*borderHeight);
			if ( color_mode == 1 )
			{
				paint.setColor(getEngine().getColorList().get(color));
				color++;
				if ( color >= getEngine().getColorList().size() )
					color = 0;
			}
			else if ( color_mode == 2 )
			{
				paint.setColor(0xff000000|(int)(Math.random()*0xffffff));
			}
			canvas.drawLines(tmpData, paint);
		}
	}
}
