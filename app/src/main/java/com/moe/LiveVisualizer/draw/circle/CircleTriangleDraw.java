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
import com.moe.LiveVisualizer.utils.ColorList;

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
		super.onDraw(canvas,color_mode);
		drawCircleImage(canvas);

	}

	@Override
	public void drawGraph(byte[] buffer, Canvas canvas,  final int color_mode,boolean useMode)
	{
		final LiveWallpaper.WallpaperEngine engine=getEngine();
		if(getEngine()==null)return;
		final ColorList colorList=engine.getColorList();
		if(colorList==null)return;
		PointF point=getPointF();
		float radius=getRadius();
		final float radialHeight=getDirection()==OUTSIDE?getBorderHeight():getRadius();
		Paint paint=getPaint();
		paint.setStrokeWidth(getBorderWidth());
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
		for ( int i=0;i < points.length;i ++ )
		{
			if(useMode)
				switch ( color_mode){
					case 1:
						paint.setColor(colorList.get(color_step));
						color_step++;
						if ( color_step >= colorList.size() )
							color_step = 0;
						break;
					case 2:
						paint.setColor(0xff000000|(int)(Math.random()*0xffffff));
						break;
					case 4:
						int color=colorList.get(color_step);
						paint.setColor(engine.getPreference().getBoolean("nenosync",false)?color:0xffffffff);
						color_step++;
						if ( color_step >= colorList.size() )
							color_step = 0;
						paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
						break;
				}
			float height=(float) (buffer[i] / 127d * radialHeight);
			if ( height < points[i] )
				height=points[i]-(points[i]-height)*getInterpolator(1-(points[i]-height)/radialHeight);
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
