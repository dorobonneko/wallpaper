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
import com.moe.LiveVisualizer.service.LiveWallpaper;
import com.moe.LiveVisualizer.utils.ColorList;

public class UnKnow2 extends RingDraw
{
	private float[] points;
	private float width;
	private float[] lines=new float[4];
	private Path path1=new Path(),path2=new Path();
	
	public UnKnow2(ImageDraw draw){
		super(draw);
	}
	@Override
	public void onSizeChanged()
	{
		// TODO: Implement this method
		super.onSizeChanged();
		width=(float)(2*getRadius()*Math.PI/(size()-1));
		
	}

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		drawCircleImage(canvas);
		super.onDraw(canvas, color_mode);
	}
	
	@Override
	public void drawGraph(double[] buffer, Canvas canvas, int color_mode, boolean useMode)
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
		double degress=2d/size()*Math.PI;
		/*float halfWidth=width/2;
		float halfBorder=getBorderWidth()/2f;*/
		float radius=getRadius();
		for(int i=0;i<points.length;i++){
			float height=(float)(buffer[i]/127f*getRadius()/2);
			if ( height < points[i] )
                points[i]=Math.max(0,points[i]-(points[i]-height)*getInterpolator((points[i]-height)/points[i])*1f);
            else if(height>points[i])
                points[i]=points[i]+(height-points[i])*getInterpolator((height-points[i])/height*0.6f);
                height=points[i];
			double value=degress*i;
			if (i==0){
                path1.moveTo(lines[0]=(float)((radius-points[i])*Math.cos(value)),lines[1]=(float)((radius-points[i])*Math.sin(value)));
				path2.moveTo(lines[2]=(float)((radius+points[i])*Math.cos(value)),lines[3]=(float)((radius+points[i])*Math.sin(value)));
				
            }else{
                path1.lineTo(lines[0]=(float)((radius-points[i])*Math.cos(value)),lines[1]=(float)((radius-points[i])*Math.sin(value)));
				path2.lineTo(lines[2]=(float)((radius+points[i])*Math.cos(value)),lines[3]=(float)((radius+points[i])*Math.sin(value)));
            }
			if(useMode)
				checkMode(color_mode,paint);
			canvas.drawLines(lines,paint);
			//canvas.rotate(degress,center.x,center.y);
		}
		path1.close();
		path2.close();
		canvas.drawPath(path1,paint);
		canvas.drawPath(path2,paint);
		path1.reset();
		path2.reset();
		canvas.restore();
		
	}
}
