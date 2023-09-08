package com.moe.LiveVisualizer.draw.line;
import com.moe.LiveVisualizer.draw.LineDraw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import com.moe.LiveVisualizer.utils.ColorList;

public class SquareDraw extends LineDraw
{
	private float[] points;
	public SquareDraw(ImageDraw draw){
		super(draw);
	}

	private int squareSize(){
		return (int)(getBorderHeight()/getBorderWidth());
	}
	@Override
	public void drawGraph(double[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		Paint paint=getPaint();
		if ( points == null || points.length != size() )
			points = new float[size()];
		float x=getStartOffset();//起始像素
		final float halfWidth=getBorderWidth()/8;
        float itemHeight=(float) Math.floor(getBorderHeight()/20f);
        float squareHeight=itemHeight*0.7f;
		for ( int i=0;i < points.length;i ++ )
		{
			if(useMode)
				checkMode(color_mode,paint);
            float height=(float)(buffer[i] / (double)Byte.MAX_VALUE * getBorderHeight());
            if ( height < points[i] )
                points[i]=Math.max(0,points[i]-(points[i]-height)*getInterpolator((points[i]-height)/points[i]*0.8f)*0.45f);
            else if(height>points[i])
                points[i]=points[i]+(height-points[i])*getInterpolator((height-points[i])/height);
			float offsetLeft=x;
			float offsetRight=x+getBorderWidth();
			x+=getSpaceWidth();
            double size=Math.ceil(points[i]/itemHeight);
			for(int n=0;n<size;n++){
				if ( paint.getStrokeCap() != Paint.Cap.ROUND )
					canvas.drawRect(offsetLeft,getDrawHeight()-n*itemHeight-squareHeight,offsetRight,getDrawHeight()-n*itemHeight,paint);
				else
					canvas.drawRoundRect(offsetLeft,getDrawHeight()-n*itemHeight-squareHeight,offsetRight,getDrawHeight()-n*itemHeight,halfWidth,halfWidth,paint);
			}
			

		}
	}


	
}
