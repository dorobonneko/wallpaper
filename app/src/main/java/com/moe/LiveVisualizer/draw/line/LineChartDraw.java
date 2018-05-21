package com.moe.LiveVisualizer.draw.line;
import android.graphics.Paint;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.LinearGradient;
import android.util.TypedValue;
import android.graphics.Shader;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.draw.LineDraw;

public class LineChartDraw extends LineDraw
{
	public LineChartDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine)
	{
		super(draw,engine);
		}

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		Paint paint=getPaint();
		switch(color_mode){
			case 0://色带
			switch(getEngine().getColorList().size()){
				case 0:
					paint.setColor(0xff39c5bb);
					drawGraph(getFft(), canvas,color_mode,false);
					break;
				case 1:
					paint.setColor(getEngine().getColorList().get(0));
					drawGraph(getFft(), canvas,color_mode,false);
					break;
				default:
					paint.setShader(getShader());
					drawGraph(getFft(), canvas,color_mode,false);
					break;
			}
				break;
			case 1://间隔
			case 2:
			case 4:
				drawGraph(getFft(),canvas,color_mode,true);
				break;
			case 3://霓虹灯
			int color=getColor();
				paint.setColor(getEngine().getSharedPreferences().getBoolean("nenosync",false)?color:0xffffffff);
				paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
				drawGraph(getFft(),canvas,color_mode,false);
				break;
		}
		paint.reset();
	}

	@Override
	public byte[] getFft()
	{
		return getWave();
	}
	@Override
	public void drawGraph(byte[] buffer, Canvas canvas,final int color_mode, boolean useMode)
	{
		Paint paint=getPaint();
		float offsetX=0;
		float[] tmpData=new float[8];
		int color_step=0;
		for ( int i=0;i < buffer.length-1;i+=2 )
		{
			float height=((byte)(buffer[i]+128))*getBorderHeight()/256;
			/*if(height<anime[i])
				height=anime[i]-(anime[i]-height)*getDownSpeed();
			anime[i]=height;*/
			
				tmpData[0] = offsetX;
				tmpData[1] = getDrawHeight() -height ;
			/*}
			else
			{
				System.arraycopy(tmpData, 2, tmpData, 0, 2);
			}*/
			height=((byte)(buffer[i+1]+128))*getBorderHeight()/256;
			tmpData[2] = (offsetX += getSpaceWidth()+getBorderWidth());
			tmpData[3] = getDrawHeight() -height;
			System.arraycopy(tmpData,2,tmpData,4,2);
			height=((byte)(buffer[i+2]+128))*getBorderHeight()/256;
			tmpData[6]=offsetX+=getSpaceWidth()+getBorderWidth();
			tmpData[7]=getDrawHeight()-height;
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
						paint.setColor(getEngine().getSharedPreferences().getBoolean("nenosync",false)?color:0xffffffff);
						color_step++;
						if ( color_step >= getEngine().getColorList().size() )
							color_step = 0;
					paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
					break;
				}
			
			canvas.drawLines(tmpData, paint);
		}
	}
	
}
