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
	public void drawGraph(byte[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		final LiveWallpaper.WallpaperEngine engine=getEngine();
		if(engine==null)return;
		final ColorList colorList=engine.getColorList();
		if(colorList==null)return;
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
		Path lines=new Path();
		float radius=getRadius();
		for(int i=0;i<points.length;i++){
			float height=buffer[i]/127f*radius/2;
			if(height<points[i])
				height=points[i]-(points[i]-height)*getInterpolator(1-(points[i]-height)/getRadius()/2);
			if(height<0)height=0;
			points[i]=height;
			double value=degress*i;
			//前面
			lines.moveTo((float)((radius-points[i])*Math.cos(value)),(float)((radius-points[i])*Math.sin(value)));
			lines.lineTo((float)((radius+points[i])*Math.cos(value)),(float)((radius+points[i])*Math.sin(value)));
			value=degress*(i+1);
			lines.lineTo((float)((radius+points[i])*Math.cos(value)),(float)((radius+points[i])*Math.sin(value)));
			lines.lineTo((float)((radius-points[i])*Math.cos(value)),(float)((radius-points[i])*Math.sin(value)));
              lines.close();
			if(useMode){
				switch(color_mode){
					case 1:
						paint.setColor(colorList.get(color_step++));
						if(color_step>=colorList.size())
							color_step=0;
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
			}
			if(useMode){
			canvas.drawPath(lines,paint);
			lines.reset();
			}
		}
		if(!useMode)
			canvas.drawPath(lines,paint);
		canvas.restore();

	}
}
