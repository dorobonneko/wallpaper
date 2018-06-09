package com.moe.LiveVisualizer.draw.circle;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Paint;
import java.util.Random;

public class UnKnow1 extends RingDraw
{
	private float[] points;
	public UnKnow1(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		super(draw,engine);
	}
	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		drawGraph(getFft(),canvas,color_mode,true);
		drawCircleImage(canvas);
	}

	@Override
	public void drawGraph(byte[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		PointF center=getPointF();
		Paint paint=getPaint();
		if(points==null)
			points=new float[20];
		canvas.save();
		int count=-1;
		int color_step=0;
		for(int i=0;i<5;i++){
			if(useMode){
				if(color_mode==1){
					paint.setColor(getEngine().getColorList().get(color_step));
					color_step++;
					if(color_step>=getEngine().getColorList().size())
						color_step=0;
				}else if(color_mode==2){
					paint.setColor(0xff000000|(int)(Math.random()*0xffffff));
				}
			}
			//计算高度
			for(int n=0;n<4;n++){
			byte height=buffer[++count];
			if(height>points[count])
				points[count]=height;
				else
				points[count]=points[count]-(points[count]-height)*getDownSpeed();
				if(points[count]<0)points[count]=0;
			}
			canvas.rotate(i*15,center.x,center.y);
			Path path=new Path();
			path.moveTo(center.x,center.y-getRadius()-Math.abs((points[i*4+3]-points[i*4])/2));
			path.cubicTo(center.x+getRadius()/2+points[i*4],center.y-getRadius()-points[i*4],center.x+getRadius()+points[i*4],center.y-getRadius()/2-points[i*4],center.x+getRadius()+Math.abs((points[i*4]-points[i*4+1])/2),center.y);
			path.cubicTo(center.x+getRadius()+points[i*4+1],center.y+getRadius()/2+points[i*4+1],center.x+getRadius()/2+points[i*4+1],center.y+getRadius()+points[i*4+1],center.x,center.y+getRadius()+Math.abs((points[i*4+1]-points[i*4+2])/2));
			path.cubicTo(center.x-getRadius()/2-points[i*4+2],center.y+getRadius()+points[i*4+2],center.x-getRadius()-points[i*4+2],center.y+getRadius()/2+points[i*4+2],center.x-getRadius()-Math.abs((points[i*4+2]-points[i*4+3])/2),center.y);
			path.cubicTo(center.x-getRadius()-points[i*4+3],center.y-getRadius()/2-points[i*4+3],center.x-getRadius()/2-points[i*4+3],center.y-getRadius()-points[i*4+3],center.x,center.y-getRadius()-Math.abs((points[i*4+3]-points[i*4])/2));
			path.addCircle(center.x,center.y,getRadius(),Path.Direction.CCW);
			path.close();
			canvas.drawPath(path,paint);
			
		}
		canvas.restore();
	}
}
