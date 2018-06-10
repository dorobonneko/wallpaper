package com.moe.LiveVisualizer.draw.circle;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.service.LiveWallpaper;
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
	public CircleTriangleDraw(ImageDraw draw)
	{
		super(draw);
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
			size = (int)((length - getSpaceWidth()*2) / (getBorderWidth()*2 + getSpaceWidth()*2));
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
		paint.setStrokeCap(getRound());
		paint.setStrokeWidth(getBorderWidth());
		switch(color_mode){
			case 0:
				switch ( getEngine().getColorList().size() )
				{
					case 0:
						paint.setColor(0xff39c5bb);
						drawGraph(getFft(), canvas, color_mode,false);
						break;
					case 1:
						paint.setColor(getEngine().getColorList().get(0));
						drawGraph(getFft(), canvas, color_mode,false);
						break;
					default:
						final int layer=canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
								drawGraph(getFft(), canvas, color_mode,false);
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
						}
				break;
			case 1:
			case 2:
			case 4:
				drawGraph(getFft(), canvas, color_mode,true);
				break;
			case 3:
				int color=getColor();
				paint.setColor(getEngine().getPreference().getBoolean("nenosync",false)?color:0xffffffff);
				paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
				drawGraph(getFft(), canvas, color_mode,false);
				paint.setShadowLayer(0, 0, 0, 0);
				break;
		}
		paint.reset();
		drawCircleImage(canvas);

	}

	@Override
	public void drawGraph(byte[] buffer, Canvas canvas,  final int color_mode,boolean useMode)
	{
		PointF point=getPointF();
		float radius=getRadius();
		final float radialHeight=getDirection()==OUTSIDE?getBorderHeight():getRadius();
		Paint paint=getPaint();
		float width=getBorderWidth()/2;
		//paint.setStrokeWidth(0);
		if ( points == null || points.length != size() )
			points = new float[size()];
		int color_step=0;
		float degress_step=360f / size();
		
		canvas.save();
		final PointF center=getPointF();
		//canvas.rotate(degress_step / 2.0f, center.x, center.y);
		float[] lines=new float[8];
		for ( int i=0;i < size();i ++ )
		{
			if(useMode)
				switch ( color_mode){
					case 1:
						paint.setColor(getEngine().getColorList().get(color_step));
						color_step++;
						if ( color_step >= getEngine().getColorList().size() )
							color_step = 0;
						break;
					case 2:
						paint.setColor(0xff000000|(int)(Math.random()*0xffffff));
						break;
					case 4:
						int color=getEngine().getColorList().get(color_step);
						paint.setColor(getEngine().getPreference().getBoolean("nenosync",false)?color:0xffffffff);
						color_step++;
						if ( color_step >= getEngine().getColorList().size() )
							color_step = 0;
						paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
						break;
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
