package com.moe.LiveVisualizer.draw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Paint;
import android.graphics.Canvas;

public abstract class LineDraw extends Draw
{
	private int size;
	private float spaceWidth,borderHeight,borderWidth,drawHeight;
	private Paint paint;
	public LineDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		super(draw,engine);
		paint=new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeCap(getEngine().getSharedPreferences().getBoolean("round", true) ?Paint.Cap.ROUND: Paint.Cap.SQUARE);
		borderHeight = engine.getSharedPreferences().getInt("borderHeight", 100);
		spaceWidth = engine.getSharedPreferences().getInt("spaceWidth", 20);
		drawHeight = engine.getDisplayHeight() - engine.getSharedPreferences().getInt("height", 10) / 100.0f * engine.getDisplayHeight();
		borderWidth = engine.getSharedPreferences().getInt("borderWidth", 30);
		paint.setStrokeWidth(borderWidth);
		notifySizeChanged();
	}
	public Paint getPaint(){
		return paint;
	}
	public float getDrawHeight(){
		return drawHeight;
	}
	public float getSpaceWidth(){
		return spaceWidth;
	}
	public float getBorderWidth(){
		return borderWidth;
	}
	public float getBorderHeight(){
		return borderHeight;
	}
	@Override
	final public void onDrawHeightChanged(float height)
	{
		this.drawHeight=height;
	}

	@Override
	final public void onBorderHeightChanged(int height)
	{
		this.borderHeight=height;
		notifySizeChanged();
	}

	@Override
	public void draw(Canvas canvas)
	{
		paint.setStrokeWidth(borderWidth);
		paint.setStrokeCap(getRound());
		super.draw(canvas);
	}

	


	@Override
	final public void onBorderWidthChanged(int width)
	{
		this.borderWidth=width;
		paint.setStrokeWidth(width);
		notifySizeChanged();
	}

	@Override
	final public void onSpaceWidthChanged(int space)
	{
		this.spaceWidth=space;
		notifySizeChanged();
	}
	@Override
	public int size()
	{
		return size;
	}

	@Override
	public void notifySizeChanged()
	{
		spaceWidth=getEngine().getSharedPreferences().getInt("spaceWidth", 20);
		try
		{
			size = (int)((getEngine().getDisplayWidth() - spaceWidth) / (borderWidth + spaceWidth));
		}
		catch (Exception e)
		{}
		try
		{
			size = size > getEngine().getFftSize() ?getEngine().getFftSize(): size;
			spaceWidth = (getEngine().getDisplayWidth() - size * borderWidth) / ((float)size - 1);

		}
		catch (Exception e)
		{}
	}

}
