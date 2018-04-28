package com.moe.LiveVisualizer.internal;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Paint;

public class PopCircleDraw extends ImageDraw
{
	private Paint paint;
	public PopCircleDraw(ImageDraw draw,LiveWallpaper.MoeEngine engine){
		super(engine);
		paint=new Paint();
	}

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		switch(getEngine().getColorList().size()){
			case 0:
				paint.setColor(0xff39c5bb);
				drawPop(canvas,color_mode,false);
				break;
			case 1:
				paint.setColor(getEngine().getColorList().get(0));
				drawPop(canvas,color_mode,false);
				break;
			default:
			if(color_mode==0){
				
			}else
			drawPop(canvas,color_mode,true);
			break;
		}
	}

	private void drawPop(Canvas camvas,int color_mode,boolean useMode){
		
	}
	
	
}
