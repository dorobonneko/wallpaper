package com.moe.LiveVisualizer.draw.circle;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Paint;
import java.util.Random;
import com.moe.LiveVisualizer.utils.ColorList;

public class UnKnow1 extends RingDraw {
	private float[] points;
	public UnKnow1(ImageDraw draw) {
		super(draw);
	}
	@Override
	public void onDraw(Canvas canvas, int color_mode) {
		super.onDraw(canvas, color_mode);
		drawCircleImage(canvas);
	}

	@Override
	public void drawGraph(double[] buffer, Canvas canvas, int color_mode, boolean useMode) {
		PointF center=getPointF();
		Paint paint=getPaint();
		if (points == null)
			points = new float[20];
		canvas.save();
		int count=-1;
		for (int i=0;i < 5;i++) {
			if (useMode)
				checkMode(color_mode, paint);
			//计算高度
			for (int n=0;n < 4;n++) {
                float height=(float)buffer[++count];
                if ( height < points[count] )
                    points[count]=Math.max(0,points[count]-(points[count]-height)*getInterpolator((points[count]-height)/points[count])*0.8f);
                else if(height>points[i])
                    points[count]=points[count]+(height-points[count])*getInterpolator((height-points[count])/height);
			}
			canvas.rotate(i * 15, center.x, center.y);
			Path path=new Path();
			path.moveTo(center.x, center.y - getRadius() - Math.abs((points[i * 4 + 3] - points[i * 4]) / 2));
			path.cubicTo(center.x + getRadius() / 2 + points[i * 4], center.y - getRadius() - points[i * 4], center.x + getRadius() + points[i * 4], center.y - getRadius() / 2 - points[i * 4], center.x + getRadius() + Math.abs((points[i * 4] - points[i * 4 + 1]) / 2), center.y);
			path.cubicTo(center.x + getRadius() + points[i * 4 + 1], center.y + getRadius() / 2 + points[i * 4 + 1], center.x + getRadius() / 2 + points[i * 4 + 1], center.y + getRadius() + points[i * 4 + 1], center.x, center.y + getRadius() + Math.abs((points[i * 4 + 1] - points[i * 4 + 2]) / 2));
			path.cubicTo(center.x - getRadius() / 2 - points[i * 4 + 2], center.y + getRadius() + points[i * 4 + 2], center.x - getRadius() - points[i * 4 + 2], center.y + getRadius() / 2 + points[i * 4 + 2], center.x - getRadius() - Math.abs((points[i * 4 + 2] - points[i * 4 + 3]) / 2), center.y);
			path.cubicTo(center.x - getRadius() - points[i * 4 + 3], center.y - getRadius() / 2 - points[i * 4 + 3], center.x - getRadius() / 2 - points[i * 4 + 3], center.y - getRadius() - points[i * 4 + 3], center.x, center.y - getRadius() - Math.abs((points[i * 4 + 3] - points[i * 4]) / 2));
			path.addCircle(center.x, center.y, getRadius(), Path.Direction.CCW);
			path.close();
			canvas.drawPath(path, paint);

		}
		canvas.restore();
	}
}
