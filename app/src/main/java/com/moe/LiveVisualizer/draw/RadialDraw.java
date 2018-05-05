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
private int borderHeight,size;
private float spaceWidth,drawHeight;
	@Override
	public void onBorderHeightChanged(int height)
	{
		borderHeight=height;
		onSizeChanged();
	}

	@Override
	public void onSpaceWidthChanged(int space)
	{
		spaceWidth=space;
		onSizeChanged();
	}

	@Override
	public int size()
	{
		return size;
	}
private void onSizeChanged(){
	try
	{
		size = (int)((getEngine().getWidth() - spaceWidth) / (paint.getStrokeWidth() + spaceWidth));
	}
	catch (Exception e)
	{}
	try{
	size = size > getEngine().getFftSize() ?getEngine().getFftSize(): size;
	spaceWidth = (getEngine().getWidth()-size*paint.getStrokeWidth()) / ((float)size-1);
		
	}catch(Exception e){}
	
}

@Override
public void onDrawHeightChanged(float height)
{
	drawHeight=height;
}


	@Override
	public void onBorderWidthChanged(int width)
	{
		paint.setStrokeWidth(width);
		onSizeChanged();
	}

	private Paint paint;
	private float[] points;
	public RadialDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine)
	{
		super(draw,engine);
		paint = new Paint();
		paint.setStrokeWidth(engine.getSharedPreferences().getInt("borderWidth",30));
		paint.setStrokeCap(getEngine().getSharedPreferences().getBoolean("round",true)?Paint.Cap.ROUND:Paint.Cap.SQUARE);
		borderHeight=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,engine.getSharedPreferences().getInt("borderHeight",100),engine.getContext().getResources().getDisplayMetrics());
		spaceWidth=engine.getSharedPreferences().getInt("spaceWidth",20);
		drawHeight=engine.getHeight()-engine.getSharedPreferences().getInt("height",10)/100.0f*engine.getHeight();
		onSizeChanged();
		
	}

	@Override
	public void setRound(boolean round)
	{
		if(paint!=null)
			paint.setStrokeCap(round?Paint.Cap.ROUND:Paint.Cap.SQUARE);
	}
	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		if(color_mode==2){
			drawLine(getFft(),canvas,color_mode,true);
		}else if(color_mode==4){
			paint.setColor(0xffffffff);
			//paint.setStyle(Paint.Style.STROKE);
			paint.setShadowLayer(paint.getStrokeWidth(),0,0,getColor());
			drawLine(getFft(),canvas,color_mode,false);
			paint.setShadowLayer(0,0,0,0);
			//paint.setStyle(Paint.Style.FILL);
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
						if ( getShader() == null )
							setShader(new LinearGradient(0, 0, canvas.getWidth(), 0, getEngine().getColorList().toArray(), null, LinearGradient.TileMode.CLAMP));
						paint.setShader(getShader());
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
		//int borderWidth=getEngine().getSharedPreferences().getInt("borderWidth", 30);
		
		if(points==null||points.length!=size)
			points=new float[size];
		float x=0;//起始像素
		//int step=buffer.length / size;
		int colorStep=0;
		int mode=Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode", "0"));
		//if ( mode == 3 )
			//paint.setColor(getEngine().getColor());
		final float halfWidth=paint.getStrokeWidth()/2;
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
			//canvas.drawRect(x, y - height, x += paint.getStrokeWidth(), y, paint);
			//canvas.drawRect(x-=paint.getStrokeWidth(), y+ height, x += borderWidth, y, paint);
			canvas.drawLine(x+=halfWidth,drawHeight-height,x,drawHeight,paint);
			canvas.drawLine(x,drawHeight+height,x,drawHeight,paint);
			x+=halfWidth+spaceWidth;
		}

	}
}
