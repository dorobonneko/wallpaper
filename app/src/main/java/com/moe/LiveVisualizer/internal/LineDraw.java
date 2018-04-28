package com.moe.LiveVisualizer.internal;
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

public class LineDraw extends ImageDraw
{
	private Paint paint;
	private ImageDraw draw;
	public static LineDraw getInstance(ImageDraw draw, LiveWallpaper.MoeEngine engine)
	{
		return new LineDraw(draw, engine);
		
	}
	private LineDraw(ImageDraw draw, LiveWallpaper.MoeEngine engine)
	{
		super(engine);
		this.draw = draw;
		paint = new Paint();
	}

	@Override
	protected byte[] getBuffer()
	{
		return draw.getBuffer();
	}

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		switch ( getEngine().getColorList().size() )
		{
			case 0:
				paint.setColor(0xff39c5bb);
				drawLine(getBuffer(), canvas,color_mode,false);
				break;
			case 1:
				paint.setColor(getEngine().getColorList().get(0));
				drawLine(getBuffer(), canvas,color_mode,false);
				break;
			default:
				if ( color_mode == 0 )
				{

					final Bitmap src=Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
					final Canvas tmpCanvas=new Canvas(src);

					drawLine(getBuffer(), tmpCanvas,color_mode,false);								
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
				}
				else
				{
					drawLine(getBuffer(), canvas,color_mode,true);
				}
				break;
		}

	}
	private void drawLine(byte[] buffer, Canvas canvas,int color_mode,boolean useMode)
	{
		int borderWidth=getEngine().getSharedPreferences().getInt("borderWidth", 30);
		float spaceWidth=getEngine().getSharedPreferences().getInt("spaceWidth", 20);
		int borderHeight=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getEngine().getSharedPreferences().getInt("borderHeight", 30), getEngine().getContext().getResources().getDisplayMetrics());
		int size=0;
		try
		{
			size = (int)((canvas.getWidth() - spaceWidth) / (borderWidth + spaceWidth));
		}
		catch (Exception e)
		{}
		if ( size == 0 )return;
		size = size > buffer.length ?buffer.length: size;
		spaceWidth = (canvas.getWidth()-size*borderWidth) / ((float)size-1);
		float x=0;//起始像素
		float y=canvas.getHeight() - getEngine().getSharedPreferences().getInt("height", 10) / 100.0f * canvas.getHeight();
		int step=buffer.length / size;
		int colorStep=0;
		int mode=Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode", "0"));
		if ( mode == 3 )
			paint.setColor(getEngine().getColor());
		for ( int i=0;i < size;i ++ )
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
				paint.setColor(getEngine().getColorList().getRandom());
			}
			}
			canvas.drawRect(x, y + (Math.abs(buffer[i * step]) - 128) / 128.0f * borderHeight, x += borderWidth, y, paint);
			x+=spaceWidth;
		}

	}
}
