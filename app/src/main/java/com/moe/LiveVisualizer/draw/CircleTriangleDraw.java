package com.moe.LiveVisualizer.draw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.LinearGradient;
import android.graphics.PointF;

public class CircleTriangleDraw extends RingDraw
{

	private float[] points;
	private int size;
	public CircleTriangleDraw(ImageDraw draw, LiveWallpaper.WallpaperEngine engine)
	{
		super(draw, engine);
	}

	@Override
	public int size()
	{
		// TODO: Implement this method
		return size;
	}

	@Override
	public void onSizeChanged()
	{
		final double length=Math.min(getEngine().getDisplayWidth(),getEngine().getDisplayHeight()) / 3 * Math.PI;
		try
		{
			size = (int)((length / 2.0f - getSpaceWidth()) / (getPaint().getStrokeWidth()*2 + getSpaceWidth()));
		}
		catch (Exception e)
		{}
		try
		{
			size = size > getEngine().getFftSize() ?getEngine().getFftSize(): size;
		}
		catch (Exception e)
		{}
	}
	

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		Paint paint=getPaint();

		if ( color_mode == 2 )
		{
			drawLines(getFft(), canvas, true, color_mode);
		}
		else if ( color_mode == 4 )
		{
			int color=getColor();
			paint.setColor(getEngine().getSharedPreferences().getBoolean("nenosync",false)?color:0xffffffff);
			paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
			drawLines(getFft(), canvas, false, color_mode);
			paint.setShadowLayer(0, 0, 0, 0);
		}
		else
			switch ( getEngine().getColorList().size() )
			{
				case 0:
					paint.setColor(0xff39c5bb);
					drawLines(getFft(), canvas, false, color_mode);
					break;
				case 1:
					paint.setColor(getEngine().getColorList().get(0));
					drawLines(getFft(), canvas, false, color_mode);
					break;
				default:
					switch ( color_mode )
					{
						case 0:
							//final Bitmap src=Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
							//final Canvas tmpCanvas=new Canvas(src);
							final int layer=canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
							drawLines(getFft(), canvas, false, color_mode);
							if ( getShader() == null )
								setShader( new SweepGradient(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, getEngine().getColorList().toArray(), null));
							if ( getShaderBuffer() == null )
							{
								setShaderBuffer( Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_4444));
								Canvas shaderCanvas=new Canvas(getShaderBuffer());
								paint.setShader(getShader());
								shaderCanvas.drawRect(0, 0, shaderCanvas.getWidth(), shaderCanvas.getHeight(), paint);
								paint.setShader(null);
							}
							paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
							//canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
							canvas.drawBitmap(getShaderBuffer(), 0, 0, paint);
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
							if ( shader == null )
								setFade(shader = new LinearGradient(0, 0, 0, canvas.getHeight(), getEngine().getColorList().toArray(), null, LinearGradient.TileMode.CLAMP));
							paint.setShader(shader);
						default:
							drawLines(getFft(), canvas, true, color_mode);
							paint.setShader(null);

							break;
					}
					break;
			}
		drawCircleImage(canvas);

	}

	private void drawLines(double[] buffer, Canvas canvas, boolean useMode, final int mode)
	{
		PointF point=getPointF();
		float radius=getRadius();
		final float radialHeight=getDirection()==OUTSIDE?getBorderHeight():getRadius();
		Paint paint=getPaint();
		float width=paint.getStrokeWidth()/2;
		//paint.setStrokeWidth(0);
		if ( points == null || points.length != size() )
			points = new float[size()];
		int colorStep=0;
		float degress_step=360f / size();
		
		canvas.save();
		final PointF center=getPointF();
		//canvas.rotate(degress_step / 2.0f, center.x, center.y);
		float[] lines=new float[8];
		for ( int i=0;i < size();i ++ )
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
					paint.setColor(0xff000000 | (int)(Math.random() * 0xffffff));
				}
			float height=(float) (buffer[i] / 127d * radialHeight);
			if ( height > points[i] )
				points[i] = height;
			else
				height = points[i] - (points[i] - height) * getDownSpeed();
			if ( height < 0 )height = 0;
			points[i] = height;
			//if(paint.getStrokeCap()==Paint.Cap.ROUND){
				lines[0]=point.x-width-getSpaceWidth()/2;
				lines[1]=point.y-radius;
				lines[2]=point.x;
				lines[3]=point.y-radius+(getDirection()==OUTSIDE?-height:height);
				System.arraycopy(lines,2,lines,4,2);
				lines[6]=point.x+width+getSpaceWidth()/2;
				lines[7]=point.y-radius;
				canvas.drawLines(lines, paint);
				//canvas.drawArc(point.x-radius,point.y-radius,point.x+radius,point.y+radius,0,360,false,paint);
			//}else{
				//canvas.drawRect(point.x-paint.getStrokeWidth()/2, point.y-radius, point.x + paint.getStrokeWidth()/2, point.y -radius+(getDirection()==OUTSIDE?- height:height), paint);
			//}
			canvas.rotate(degress_step, center.x, center.y);
			//degress+=degress_step;
		}
		canvas.restore();
		//paint.setStyle(Paint.Style.FILL);
		//paint.setStrokeWidth(width*2);
	}
	
}
