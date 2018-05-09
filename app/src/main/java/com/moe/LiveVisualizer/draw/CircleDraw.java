package com.moe.LiveVisualizer.draw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.Matrix;

public abstract class CircleDraw extends Draw
{
	private PointF point;
	public CircleDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		super(draw,engine);
		point=new PointF();
		point.x=engine.getSharedPreferences().getInt("offsetX",engine.getWidth()/2);
		point.y=engine.getSharedPreferences().getInt("offsetY",engine.getHeight()/2);
	}

	@Override
	public abstract void setRound(boolean round);


	
	@Override
	public void setOffsetX(int x)
	{
		point.x=x;
	}

	@Override
	public void setOffsetY(int y)
	{
		point.y=y;
	}

	
	@Override
	final public void onDrawHeightChanged(float height)
	{
		// TODO: Implement this method
	}

	public PointF getPointF(){
		return point;
	}
	
}
