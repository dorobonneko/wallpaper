package com.moe.LiveVisualizer.draw.circle;
import android.graphics.Canvas;
import com.moe.LiveVisualizer.internal.ImageDraw;
import android.graphics.Path;
import android.graphics.PointF;
import java.util.List;
import java.util.ArrayList;
import android.graphics.Paint;

public class HeartDraw extends RingDraw
{
	private List<PointF> mPointDatas;
    private List<PointF> mPointControlls;
	
	public HeartDraw(ImageDraw draw){
		super(draw);
	}
	@Override
	public void drawGraph(double[] buffer, Canvas canvas, int color_mode, boolean useMode) throws NullPointerException
	{
		if(mPointControlls==null){
			mPointDatas = new ArrayList<>();
			mPointControlls = new ArrayList<>();
			}else{
				mPointDatas.clear();
				mPointControlls.clear();
			}
			PointF center=getPointF();
			
		float mCenterX = center.x;
        float mCenterY = center.y;
		float mCircleRadius=getRadius();
        

        //初始化数据点数据和辅助点位置
        mPointDatas.add(new PointF(mCenterX, mCenterY - mCircleRadius));
        mPointDatas.add(new PointF(mCenterX + mCircleRadius, mCenterY));
        mPointDatas.add(new PointF(mCenterX, mCenterY + mCircleRadius));
        mPointDatas.add(new PointF(mCenterX - mCircleRadius, mCenterY));

        mPointControlls.add(new PointF(mCenterX + mCircleRadius / 2, mCenterY - mCircleRadius));
        mPointControlls.add(new PointF(mCenterX + mCircleRadius, mCenterY - mCircleRadius / 2));

        mPointControlls.add(new PointF(mCenterX + mCircleRadius, mCenterY + mCircleRadius / 2));
        mPointControlls.add(new PointF(mCenterX + mCircleRadius / 2, mCenterY + mCircleRadius));

        mPointControlls.add(new PointF(mCenterX - mCircleRadius / 2, mCenterY + mCircleRadius));
        mPointControlls.add(new PointF(mCenterX - mCircleRadius, mCenterY + mCircleRadius / 2));

        mPointControlls.add(new PointF(mCenterX - mCircleRadius, mCenterY - mCircleRadius / 2));
        mPointControlls.add(new PointF(mCenterX - mCircleRadius / 2, mCenterY - mCircleRadius));
			mPointDatas.get(0).y +=mCircleRadius/2f;
			mPointControlls.get(2).x -= 20.0;

			mPointControlls.get(3).y -= 80.0;
			mPointControlls.get(4).y -= 80.0;
			mPointControlls.get(5).x += 20.0;
			mPointDatas.get(1).x-=10;
			mPointDatas.get(3).x+=10;
		
		Path path = new Path();
        path.moveTo(mPointDatas.get(0).x, mPointDatas.get(0).y);
        for (int i = 0; i < mPointDatas.size(); i++) {
            if (i == mPointDatas.size() - 1) {
                path.cubicTo(mPointControlls.get(2 * i).x, mPointControlls.get(2 * i).y, mPointControlls.get(2 * i + 1).x, mPointControlls.get(2 * i + 1).y, mPointDatas.get(0).x, mPointDatas.get(0).y);

            } else {
                path.cubicTo(mPointControlls.get(2 * i).x, mPointControlls.get(2 * i).y, mPointControlls.get(2 * i + 1).x, mPointControlls.get(2 * i + 1).y, mPointDatas.get(i + 1).x, mPointDatas.get(i + 1).y);
            }

        }
		Paint paint=getPaint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(getBorderWidth());
		canvas.drawPath(path,paint);
	}
	
}
