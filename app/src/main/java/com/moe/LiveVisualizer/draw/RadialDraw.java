package com.moe.LiveVisualizer.draw;
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

public class RadialDraw extends Draw
{
	private Paint paint;
	private float[] points;
	public RadialDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine)
	{
		super(draw,engine);
		paint = new Paint();
	}

	
	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		if(color_mode==2){
			drawLine(getFft(),canvas,color_mode,true);
		}else
		switch ( getEngine().getColorList().size() )
		{
			case 0:
				paint.setColor(0xff39c5bb);
				drawLine(getFft(), canvas,color_mode,false);
				break;
			case 1:
				paint.setColor(getEngine().getColorList().get(0));
				drawLine(getFft(), canvas,color_mode,false);
				break;
			default:
				switch( color_mode )
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
						if ( getEngine().getShader() == null )
							getEngine().setShader(new LinearGradient(0, 0, canvas.getWidth(), 0, getEngine().getColorList().toArray(), null, LinearGradient.TileMode.CLAMP));
						paint.setShader(getEngine().getShader());
						drawLine(getFft(), canvas,color_mode,false);								
						paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
						paint.setShader(null);
					break;
					case 3:
						Shader shader=getFade();
						if(shader==null)
							setFade(shader=new LinearGradient(0,0,0,canvas.getHeight(),getEngine().getColorList().toArray(),null,LinearGradient.TileMode.CLAMP));
							paint.setShader(shader);
					default:
						drawLine(getFft(), canvas,color_mode,true);
						paint.setShader(null);
						
					break;
				}
				break;
		}

	}
	private void drawLine(double[] buffer, Canvas canvas,int color_mode,boolean useMode)
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
		if(points==null||points.length!=size)
			points=new float[size];
		spaceWidth = (canvas.getWidth()-size*borderWidth) / ((float)size-1);
		float x=0;//起始像素
		float y=canvas.getHeight() - getEngine().getSharedPreferences().getInt("height", 10) / 100.0f * canvas.getHeight();
		//int step=buffer.length / size;
		int colorStep=0;
		int mode=Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode", "0"));
		//if ( mode == 3 )
			//paint.setColor(getEngine().getColor());
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
				paint.setColor((int)(Math.random()*0xffffff)|0xff000000);
			}
			}
			float height=(float)(buffer[i]/ 127.0 * borderHeight);
			if(height>points[i])
				points[i]=height;
				else
				height=points[i]-(points[i]-height)*getDownSpeed();
				if(height<0)height=0;
				points[i]=height;
			canvas.drawRect(x, y - height, x += borderWidth, y, paint);
			canvas.drawRect(x-=borderWidth, y+ height, x += borderWidth, y, paint);
			
			x+=spaceWidth;
		}

	}
}
