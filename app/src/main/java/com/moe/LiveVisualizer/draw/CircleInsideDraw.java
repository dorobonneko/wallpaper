package com.moe.LiveVisualizer.draw;
import android.graphics.Canvas;
import android.content.Context;
import android.util.AttributeSet;
import com.moe.LiveVisualizer.LiveWallpaper;
import com.moe.LiveVisualizer.internal.ImageDraw;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.graphics.RectF;
import android.graphics.PointF;
import com.moe.LiveVisualizer.internal.OnColorSizeChangedListener;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.SweepGradient;
import android.graphics.LinearGradient;
import android.graphics.Matrix;

public class CircleInsideDraw extends CircleDraw
{
	
	
	private int size;
	private float spaceWidth,borderHeight;
	
	private Shader shader;
	private float[] points;
	private Bitmap shaderBuffer;
	
	public CircleInsideDraw(ImageDraw draw, LiveWallpaper.WallpaperEngine engine)
	{
		super(draw, engine);
		borderHeight=engine.getWidth()/6f;
		//borderHeight=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,engine.getSharedPreferences().getInt("borderHeight",100),engine.getContext().getResources().getDisplayMetrics());
		spaceWidth=engine.getSharedPreferences().getInt("spaceWidth",20);
		//drawHeight=engine.getHeight()-engine.getSharedPreferences().getInt("height",10)/100.0f*engine.getHeight();
		onSizeChanged();

		engine.registerColorSizeChangedListener(new OnColorSizeChangedListener(){

				@Override
				public void onColorSizeChanged()
				{
					shader = null;
					if(shaderBuffer!=null)
						shaderBuffer.recycle();
					shaderBuffer=null;
				}
			});
	}

	
	
	@Override
	public void onBorderHeightChanged(int height)
	{
		//borderHeight=height;
		//onSizeChanged();
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
		final double length=getEngine().getWidth() / 3 * Math.PI;
		try
		{
			size = (int)((length - spaceWidth) / (getPaint().getStrokeWidth() + spaceWidth));
		}
		catch (Exception e)
		{}
		try{
			size = size > getEngine().getFftSize() ?getEngine().getFftSize(): size;
		}catch(Exception e){}

	}

	@Override
	public void onDrawHeightChanged(float height)
	{
		//无效
	}

	@Override
	public void onBorderWidthChanged(int width)
	{
		getPaint().setStrokeWidth(width);
		onSizeChanged();
	}

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		Paint paint=getPaint();
		drawCircleImage(canvas);
		if(color_mode==2){
			drawLines(getFft(),canvas,true,color_mode);
		}else if(color_mode==4){
			paint.setColor(0xffffffff);
			paint.setShadowLayer(paint.getStrokeWidth(),0,0,getColor());
			drawLines(getFft(),canvas,false,color_mode);
			paint.setShadowLayer(0,0,0,0);
		}else
			switch ( getEngine().getColorList().size() )
			{
				case 0:
					paint.setColor(0xff39c5bb);
					drawLines(getFft(), canvas, false,color_mode);
					break;
				case 1:
					paint.setColor(getEngine().getColorList().get(0));
					drawLines(getFft(), canvas, false,color_mode);
					break;
				default:
					switch( color_mode)
					{
						case 0:
							//final Bitmap src=Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
							//final Canvas tmpCanvas=new Canvas(src);
							final int layer=canvas.saveLayer(0,0,canvas.getWidth(),canvas.getHeight(),null,Canvas.ALL_SAVE_FLAG);
							drawLines(getFft(), canvas, false,color_mode);
							if ( shader == null )
								shader = new SweepGradient(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, getEngine().getColorList().toArray(), null);
							if(shaderBuffer==null){
								shaderBuffer=Bitmap.createBitmap(getEngine().getWidth(),getEngine().getHeight(),Bitmap.Config.ARGB_4444);
								Canvas shaderCanvas=new Canvas(shaderBuffer);
								paint.setShader(shader);
								shaderCanvas.drawRect(0,0,shaderCanvas.getWidth(),shaderCanvas.getHeight(),paint);
								paint.setShader(null);
							}
							paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
							//canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
							canvas.drawBitmap(shaderBuffer, 0, 0, paint);
							//paint.setShader(null);
							paint.setXfermode(null);
							canvas.restoreToCount(layer);
							//canvas.drawBitmap(src, 0, 0, paint);
							//src.recycle();
							/*if ( shader == null )
							 shader = new SweepGradient(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, getEngine().getColorList().toArray(), null);
							 paint.setShader(shader);
							 drawLines(getFft(), canvas, false,color_mode);
							 paint.setShader(null);*/
							break;
						case 3:
							Shader shader=getFade();
							if(shader==null)
								setFade(shader=new LinearGradient(0,0,0,canvas.getHeight(),getEngine().getColorList().toArray(),null,LinearGradient.TileMode.CLAMP));
							paint.setShader(shader);
						default:
							drawLines(getFft(), canvas, true,color_mode);
							paint.setShader(null);

							break;
					}
					break;
			}
			
	}

	private void drawLines(double[] buffer, Canvas canvas, boolean useMode,final int mode)
	{
		Paint paint=getPaint();
		//final int borderWidth=getEngine().getSharedPreferences().getInt("borderWidth", 30);
		if(points==null||points.length!=size)
			points=new float[size];
		//spaceWidth=(float)(length/2.0f/borderWidth/size);
		//final int step=buffer.length / size;
		int colorStep=0;
		float degress_step=360.0f / size;
		//float degress=0;
		final int y=(canvas.getHeight() - canvas.getWidth() / 3) / 2-(int)(paint.getStrokeWidth()/2);
		//if(mode==3)
		//	paint.setColor(getEngine().getColor());
		canvas.save();
		final PointF center=new PointF();
		center.x = canvas.getWidth() / 2.0f;
		center.y = canvas.getHeight() / 2.0f;
		canvas.rotate(degress_step / 2.0f, center.x, center.y);
		float offsetX=(canvas.getWidth() - paint.getStrokeWidth()) / 2.0f+paint.getStrokeWidth()/2;
		//int end=size-1;
		for ( int i=0;i < size;i ++ )
		{
			if ( useMode )
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
			float height=(float) (buffer[i] / 127d * borderHeight);
			if(height>points[i])
				points[i]=height;
			else
				height=points[i]-(points[i]-height)*getDownSpeed();
			if(height<0)height=0;
			points[i]=height;
			canvas.drawLine(offsetX,y,offsetX,y+height,paint);
			//canvas.drawRect(offsetX, y, offsetX + borderWidth, y - height, paint);
			canvas.rotate(degress_step, center.x, center.y);
			//degress+=degress_step;
			/*if ( i == end )
			{
				if ( degress_step > 0 )
				{
					canvas.restore();
					canvas.save();
					degress_step = -degress_step;
					canvas.rotate(degress_step / 2.0f, center.x, center.y);
					i = -1;
				}
				else
				{
					break;
				}
			}*/
		}
		canvas.restore();
	}
	
}
