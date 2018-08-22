package com.moe.LiveVisualizer.draw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.service.LiveWallpaper;
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
	private boolean rotation;
	public CircleDraw(ImageDraw draw){
		super(draw);
		LiveWallpaper.WallpaperEngine engine=draw.getEngine();
		point=new PointF();
		point.x=engine.getPreference().getInt("offsetX",Math.min(engine.getDisplayWidth(),engine.getDisplayHeight())/2);
		point.y=engine.getPreference().getInt("offsetY",Math.max(engine.getDisplayHeight(),engine.getDisplayWidth())/2);
		if(engine.getDisplayWidth()>engine.getDisplayHeight()){
			float x=point.x;
			point.x=point.y;
			point.y=x;
		}
	}

	public void setVisualizerRotation(boolean rotation)
	{
		this.rotation=rotation;
	}

	@Override
	public void notifySizeChanged()
	{
		point.x=getEngine().getPreference().getInt("offsetX",Math.min(getEngine().getDisplayWidth(),getEngine().getDisplayHeight())/2);
		point.y=getEngine().getPreference().getInt("offsetY",Math.max(getEngine().getDisplayHeight(),getEngine().getDisplayWidth())/2);
		if(getEngine().getDisplayWidth()>getEngine().getDisplayHeight()){
			float x=point.x;
			point.x=point.y;
			point.y=x;
		}
	}
public boolean isVisualizerRotation(){
	return rotation;
}



	
	@Override
	public void setOffsetX(int x)
	{
		if(getEngine().getDisplayWidth()>getEngine().getDisplayHeight())
			point.y=x;
			else
			point.x=x;
	}

	@Override
	public void setOffsetY(int y)
	{
		if(getEngine().getDisplayWidth()>getEngine().getDisplayHeight())
			point.x=y;
			else
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

	@Override
	public void finalized()
	{
		// TODO: Implement this method
		super.finalized();
	}
	
}
