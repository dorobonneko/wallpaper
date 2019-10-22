package com.moe.LiveVisualizer.draw.circle;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Canvas;
import com.moe.LiveVisualizer.internal.OnColorSizeChangedListener;
import android.graphics.Shader;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.graphics.PorterDuffXfermode;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import com.moe.LiveVisualizer.draw.CircleDraw;
import com.moe.LiveVisualizer.utils.ColorList;

public class CenterRadialDraw extends RingDraw
{

	private float[] points;
	public CenterRadialDraw(ImageDraw draw){
		super(draw);
	}
	/*@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		Paint paint=getP;
		paint.setStrokeCap(getRound());
		paint.setStrokeWidth(borderWidth);
		switch(color_mode){
			case 0:
				switch ( getEngine().getColorList().size() )
				{
					case 0:
						paint.setColor(0xff39c5bb);
						drawGraph(getFft(), canvas,color_mode,false);
						break;
					case 1:
						paint.setColor(getEngine().getColorList().get(0));
						drawGraph(getFft(), canvas,color_mode,false);
						break;
					default:
						final int layer=canvas.saveLayer(0,0,canvas.getWidth(),canvas.getHeight(),null,Canvas.ALL_SAVE_FLAG);
								drawGraph(getFft(),canvas,color_mode,false);
								if ( shader == null )
									shader = new SweepGradient(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, getEngine().getColorList().toArray(), null);
								if(shaderBuffer==null){
									shaderBuffer=Bitmap.createBitmap(canvas.getWidth(),canvas.getHeight(),Bitmap.Config.ARGB_4444);
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
								/*break;
							}
				break;
			case 1:
			case 2:
			case 4:
				drawGraph(getFft(),canvas,color_mode,true);
				break;
			case 3:
				int color=getColor();
				paint.setColor(getEngine().getPreference().getBoolean("nenosync",false)?color:0xffffffff);
				paint.setShadowLayer(borderWidth,0,0,color);
				drawGraph(getFft(),canvas,color_mode,false);
				paint.setShadowLayer(0,0,0,0);
				break;
		}
		paint.reset();
	}*/

	
	@Override
	public void drawGraph(double[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		if(points==null||points.length!=size())
			points=new float[size()];
		Paint paint=getPaint();
		paint.setStrokeWidth(getBorderWidth());
		canvas.save();
		PointF point=getPointF();
		canvas.rotate(-90,point.x,point.y);
		float degress=360f/points.length;
		for(int i=0;i<points.length;i++){
			if(useMode)
				checkMode(color_mode,paint);
			float height=(float)(buffer[i]/127d*getBorderHeight());
			if(height<points[i])
				height=points[i]-(points[i]-height)*getInterpolator(1-(points[i]-height)/getBorderHeight());
				if(height<0)height=0;
			points[i]=height;
			if(paint.getStrokeCap()==Paint.Cap.ROUND)
				canvas.drawLine(point.x,point.y-height,point.x,point.y,paint);
			else
				canvas.drawRect(point.x-getBorderWidth()/2,point.y-height,point.x+getBorderWidth()/2,point.y,paint);
			canvas.rotate(degress,point.x,point.y);
		}
		canvas.restore();
	}


	
	
}
