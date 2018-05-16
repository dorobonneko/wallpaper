package com.moe.LiveVisualizer.draw;
import android.graphics.Paint;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.util.TypedValue;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.LinearGradient;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;

public class YamaLineDraw extends Draw
{
	private int borderHeight,size,borderWidth;
	private float spaceWidth,drawHeight;
	private Paint paint;
	private float[] points;
	public YamaLineDraw(ImageDraw draw, LiveWallpaper.WallpaperEngine engine)
	{
		super(draw, engine);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeCap(getEngine().getSharedPreferences().getBoolean("round", true) ?Paint.Cap.ROUND: Paint.Cap.SQUARE);
		borderHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, engine.getSharedPreferences().getInt("borderHeight", 100), engine.getContext().getResources().getDisplayMetrics());
		spaceWidth = engine.getSharedPreferences().getInt("spaceWidth", 20);
		drawHeight = engine.getDisplayHeight() - engine.getSharedPreferences().getInt("height", 10) / 100.0f * engine.getDisplayHeight();
		borderWidth = engine.getSharedPreferences().getInt("borderWidth", 30);
		paint.setStrokeWidth(borderWidth);
		onSizeChanged();

	}
	@Override
	public void onBorderHeightChanged(int height)
	{
		borderHeight = height;
		onSizeChanged();
	}

	@Override
	public void onSpaceWidthChanged(int space)
	{
		spaceWidth = space;
		onSizeChanged();
	}

	@Override
	public int size()
	{
		return size;
	}
	private void onSizeChanged()
	{
		spaceWidth=getEngine().getSharedPreferences().getInt("spaceWidth", 20);
		try
		{
			size = (int)((getEngine().getDisplayWidth() - spaceWidth) / (borderWidth + spaceWidth));
		}
		catch (Exception e)
		{}
		try
		{
			size = size > getEngine().getFftSize() ?getEngine().getFftSize(): size;
			spaceWidth = (getEngine().getDisplayWidth() - size * borderWidth) / ((float)size - 1);

		}
		catch (Exception e)
		{}

	}
	@Override
	public void notifySizeChanged()
	{
		onSizeChanged();
	}


	@Override
	public void onDrawHeightChanged(float height)
	{
		drawHeight = height;
	}


	@Override
	public void onBorderWidthChanged(int width)
	{
		borderWidth = width;
		paint.setStrokeWidth(width);
		onSizeChanged();
	}

	@Override
	public void setRound(boolean round)
	{
		if ( paint != null )
			paint.setStrokeCap(round ?Paint.Cap.ROUND: Paint.Cap.SQUARE);
	}
	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		if ( color_mode == 2 )
		{
			drawLine(getFft(), canvas, color_mode, true);
		}
		else if ( color_mode == 3 )
		{
			int color=getColor();
			paint.setColor(getEngine().getSharedPreferences().getBoolean("nenosync",false)?color:0xffffffff);
			paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
			drawLine(getFft(), canvas, color_mode, false);
			paint.setShadowLayer(0, 0, 0, 0);
			//paint.setStyle(Paint.Style.FILL);
		}
		else
			switch ( getEngine().getColorList().size() )
			{
				case 0:
					paint.setColor(0xff39c5bb);
					drawLine(getFft(), canvas, color_mode, false);
					break;
				case 1:
					paint.setColor(getEngine().getColorList().get(0));
					drawLine(getFft(), canvas, color_mode, false);
					break;
				default:
					switch ( color_mode )
					{
						case 0:
							/*final Bitmap src=Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
							 final Canvas tmpCanvas=new Canvas(src);

							 drawLine(getFft(), tmpCanvas,color_mode,false);								
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
							 */
							paint.setShader(getShader());
							drawLine(getFft(), canvas, color_mode, false);								
							paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
							paint.setShader(null);
							break;
						default:
							drawLine(getFft(), canvas, color_mode, true);
							paint.setShader(null);

							break;
					}
					break;
			}

	}
	private void drawLine(double[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		if ( points == null || points.length != size )
			points = new float[size];
		int colorStep=0;
		int mode=Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode", "0"));
		final float halfWidth=paint.getStrokeWidth() / 2;
		float x=0;
		for ( int i=0;i < size;i ++ )
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
			float height=(float)(buffer[i] / 127 * borderHeight);
			if ( height > points[i] )
				points[i] = height;
			else
				height = points[i] - (points[i] - height) * getDownSpeed();
			if ( height < 0 )height = 0;
			points[i] = height;
			
			canvas.drawLine(halfWidth, drawHeight, x+=halfWidth, drawHeight-height, paint);
			canvas.drawLine(x, drawHeight - height, canvas.getWidth()-halfWidth, drawHeight, paint);
			x += halfWidth + spaceWidth;
			

		}

	}
}
