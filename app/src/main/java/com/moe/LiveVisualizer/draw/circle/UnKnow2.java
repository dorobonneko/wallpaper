package com.moe.LiveVisualizer.draw.circle;
import com.moe.LiveVisualizer.internal.ImageDraw;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;

public class UnKnow2 extends RingDraw
{
	private float[] points;
	private float width;
	public UnKnow2(ImageDraw draw){
		super(draw);
	}
	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		drawCircleImage(canvas);
		Paint paint=getPaint();
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
						paint.setStyle(Paint.Style.FILL);
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
				switch ( getEngine().getColorList().size() )
				{
					case 0:
						paint.setColor(0xff39c5bb);
						break;
					default:
						paint.setColor(getEngine().getColorList().get(0));
						break;
						}
				drawGraph(getFft(), canvas, color_mode,true);
				break;
			case 3:
				int color=getColor();
				paint.setColor(getEngine().getPreference().getBoolean("nenosync",false)?color:0xffffffff);
				paint.setShadowLayer(getBorderWidth(),0,0,color);
				drawGraph(getFft(), canvas, color_mode,false);
				paint.setShadowLayer(0, 0, 0, 0);
				break;
		}
		paint.reset();
	}

	@Override
	public void onSizeChanged()
	{
		// TODO: Implement this method
		super.onSizeChanged();
		width=(float)(2*getRadius()*Math.PI/(size()-1));
		
	}
	
	@Override
	public void drawGraph(byte[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		PointF center=getPointF();
		Paint paint=getPaint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(getBorderWidth());
		paint.setStrokeCap(getRound());
		if(points==null||points.length!=size())
			points=new float[size()];
		canvas.save();
		canvas.rotate(-90,center.x,center.y);
		canvas.translate(center.x,center.y);
		int color_step=0;
		double degress=2d/size()*Math.PI;
		/*float halfWidth=width/2;
		float halfBorder=getBorderWidth()/2f;*/
		float[] lines=new float[4];
		Path path1=new Path(),path2=new Path();
		float radius=getRadius();
		for(int i=0;i<size();i++){
			float height=buffer[i]/127f*getRadius()/2;
			if(height<points[i])
				height=points[i]-(points[i]-height)*getInterpolator(1-(points[i]-height)/getRadius()/2);
			if(height<0)height=0;
			points[i]=height;
			double value=degress*i;
			if (i==0){
                path1.moveTo(lines[0]=(float)((radius-points[i])*Math.cos(value)),lines[1]=(float)((radius-points[i])*Math.sin(value)));
				path2.moveTo(lines[2]=(float)((radius+points[i])*Math.cos(value)),lines[3]=(float)((radius+points[i])*Math.sin(value)));
				
            }else{
                path1.lineTo(lines[0]=(float)((radius-points[i])*Math.cos(value)),lines[1]=(float)((radius-points[i])*Math.sin(value)));
				path2.lineTo(lines[2]=(float)((radius+points[i])*Math.cos(value)),lines[3]=(float)((radius+points[i])*Math.sin(value)));
            }
			if(useMode){
				switch(color_mode){
					case 1:
						paint.setColor(getEngine().getColorList().get(color_step++));
						if(color_step>=getEngine().getColorList().size())
							color_step=0;
					break;
				}
			}
			canvas.drawLines(lines,paint);
			//canvas.rotate(degress,center.x,center.y);
		}
		path1.close();
		path2.close();
		canvas.drawPath(path1,paint);
		canvas.drawPath(path2,paint);
		canvas.restore();
		
	}
}
