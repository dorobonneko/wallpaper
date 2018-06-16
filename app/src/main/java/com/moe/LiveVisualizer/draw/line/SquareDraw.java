package com.moe.LiveVisualizer.draw.line;
import com.moe.LiveVisualizer.draw.LineDraw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class SquareDraw extends LineDraw
{
	private int[] points;
	public SquareDraw(ImageDraw draw){
		super(draw);
	}

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		Paint paint=getPaint();
		switch(color_mode){
			case 0:
				switch ( getEngine().getColorList().size() )
				{
					case 0:
						paint.setColor(0xff39c5bb);
						drawGraph(getFft(), canvas, color_mode, false);
						break;
					case 1:
						paint.setColor(getEngine().getColorList().get(0));
						drawGraph(getFft(), canvas, color_mode, false);
						break;
					default:
						paint.setShader(getShader());
						drawGraph(getFft(), canvas, color_mode, false);								
						paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
						paint.setShader(null);
				}
				break;
			case 1:
			case 4:
			case 2:
				drawGraph(getFft(), canvas, color_mode, true);
				break;
			case 3:
				int color=getColor();
				paint.setColor(getEngine().getPreference().getBoolean("nenosync",false)?color:0xffffffff);
				paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
				drawGraph(getFft(), canvas, color_mode, false);
				paint.setShadowLayer(0, 0, 0, 0);
				break;
		}
		paint.reset();
	}
	private int squareSize(){
		return (int)(getBorderHeight()/getBorderWidth());
	}
	@Override
	public void drawGraph(byte[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		Paint paint=getPaint();
		if ( points == null || points.length != size() )
			points = new int[size()];
		float x=0;//起始像素
		int color_step=0;
		final float halfWidth=getBorderWidth()/8;
		for ( int i=0;i < size();i ++ )
		{
			if(useMode)
				switch ( color_mode){
					case 1:
						paint.setColor(getEngine().getColorList().get(color_step));
						color_step++;
						if ( color_step >= getEngine().getColorList().size() )
							color_step = 0;
						break;
					case 2:
						paint.setColor(0xff000000|(int)(Math.random()*0xffffff));
						break;
					case 4:
						int color=getEngine().getColorList().get(color_step);
						paint.setColor(getEngine().getPreference().getBoolean("nenosync",false)?color:0xffffffff);
						color_step++;
						if ( color_step >= getEngine().getColorList().size() )
							color_step = 0;
						paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
						break;
				}
			int height=(int)Math.round(buffer[i] / 127d * squareSize());
			if ( height < points[i] )
				height=(int)(points[i]-(points[i]-height)*getInterpolator(1-(points[i]-height)/squareSize()));
				if ( height < 0 )height = 0;
			points[i] = height;
			float offsetLeft=x;
			float offsetRight=x+getBorderWidth();
			x+=getSpaceWidth();
			for(int n=0;n<height;n++){
				if ( paint.getStrokeCap() != Paint.Cap.ROUND )
					canvas.drawRect(offsetLeft,getDrawHeight()-(n+1)*getBorderWidth()+halfWidth,offsetRight,getDrawHeight()-n*getBorderWidth(),paint);
				else
					canvas.drawRoundRect(offsetLeft,getDrawHeight()-(n+1)*getBorderWidth()+halfWidth,offsetRight,getDrawHeight()-n*getBorderWidth(),halfWidth,halfWidth,paint);
			}
			

		}
	}


	
}
