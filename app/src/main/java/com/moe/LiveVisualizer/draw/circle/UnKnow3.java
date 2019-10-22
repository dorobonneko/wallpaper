package com.moe.LiveVisualizer.draw.circle;
import com.moe.LiveVisualizer.internal.ImageDraw;
import android.graphics.Canvas;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import com.moe.LiveVisualizer.utils.ColorList;
import android.graphics.PointF;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Path;

public class UnKnow3 extends RingDraw
{
	private float[] points;
	private float width;
	private Path lines=new Path();
	public UnKnow3(ImageDraw draw){
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
		/*float halfWidth=width/2;*/
		float halfBorder=getBorderWidth()/2f;
		float radius=getRadius();
		for(int i=0;i<points.length;i++){
			float height=(float)(buffer[i]/127f*radius/2);
			if(height<points[i])
				height=points[i]-(points[i]-height)*getInterpolator(1-(points[i]-height)/getRadius()/2);
			if(height<0)height=0;
			points[i]=height;
			if(useMode)
				checkMode(color_mode,paint);
			double value=degress*i;
			lines.moveTo((float)((radius-points[i])*Math.cos(value)),(float)((radius-points[i])*Math.sin(value)));
			lines.lineTo((float)((radius+points[i])*Math.cos(value)),(float)((radius+points[i])*Math.sin(value)));
			value=degress*(i+1);
			lines.lineTo((float)((radius+points[i])*Math.cos(value)),(float)((radius+points[i])*Math.sin(value)));
			lines.lineTo((float)((radius-points[i])*Math.cos(value)),(float)((radius-points[i])*Math.sin(value)));
              lines.close();
			if(useMode){
			canvas.drawPath(lines,paint);
			lines.reset();
			}
		}
		if(!useMode)
			canvas.drawPath(lines,paint);
			lines.reset();
		canvas.restore();

	}
}
