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
	private Paint paint;
	private float degress=0;
	private float radius,degress_step;//圆形半径
	private PointF point;
	private boolean cutCenterImage;
	public CircleDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		super(draw,engine);
		paint = new Paint();
		paint.setStrokeCap(getEngine().getSharedPreferences().getBoolean("round",true)?Paint.Cap.ROUND:Paint.Cap.SQUARE);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(0xff39c5bb);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(engine.getSharedPreferences().getInt("borderWidth",30));
		radius=engine.getSharedPreferences().getInt("circleRadius",Math.min(engine.getWidth(),engine.getHeight())/6);
		point=new PointF();
		point.x=engine.getSharedPreferences().getInt("offsetX",engine.getWidth()/2);
		point.y=engine.getSharedPreferences().getInt("offsetY",engine.getHeight()/2);
		cutCenterImage=engine.getSharedPreferences().getBoolean("cutImage",true);
		degress_step=engine.getSharedPreferences().getInt("degress",10)/100f*10;
	}

	public void setRadius(int radius)
	{
		this.radius=radius;
	}
	public void setDegressStep(float step){
		degress_step=step;
	}
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
	final public void setCutImage(boolean cut)
	{
		cutCenterImage=cut;
	}
	final public Paint getPaint(){
		return paint;
	}
	@Override
	final public void setRound(boolean round)
	{
		if(getPaint()!=null)
			paint.setStrokeCap(round?Paint.Cap.ROUND:Paint.Cap.SQUARE);
	}
	public void drawCircleImage(Canvas canvas){
		/*if ( getEngine().getCircleImage() == null )
		{
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawCircle(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, canvas.getWidth() / 6, paint);
			paint.setStyle(Paint.Style.FILL);
		}
		else*/
		if(getEngine().getCircleImage()!=null){
			final int layer=canvas.saveLayer(0,0,canvas.getWidth(),canvas.getHeight(),null,canvas.ALL_SAVE_FLAG);
			//canvas.drawColor(0xffffffff);
			if(getEngine().getSharedPreferences().getBoolean("circleSwitch",true)){
				canvas.rotate(degress,point.x,point.y);
				degress+=degress_step;
				if ( degress >= 360 )degress = 0;
			}
			final Bitmap circle=getEngine().getCircleImage();
			if(getCenterScale()!=null){
				Matrix matrix=getCenterScale();
				float scale=Math.max(radius*2/circle.getWidth(),radius*2/circle.getHeight());
				matrix.setScale(scale,scale);
				matrix.postTranslate(point.x-circle.getWidth()*scale/2,point.y-circle.getHeight()*scale/2);
			}
			if(cutCenterImage){
				canvas.drawCircle(point.x,point.y, radius, paint);
				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
				if(getCenterScale()!=null)
					canvas.drawBitmap(circle,getCenterScale(),paint);
				else
					canvas.drawBitmap(circle,point.x-circle.getWidth()/2,point.y-circle.getHeight()/2, paint);
				paint.setXfermode(null);
			}else{
				if(getCenterScale()!=null)
					canvas.drawBitmap(circle,getCenterScale(),paint);
				else
					canvas.drawBitmap(circle,point.x-circle.getWidth()/2,point.y-circle.getHeight()/2, paint);
			}
			canvas.restoreToCount(layer);

		}
	}

	@Override
	final public void onDrawHeightChanged(float height)
	{
		// TODO: Implement this method
	}

	public PointF getPointF(){
		return point;
	}
	public float getRadius(){
		return radius;
	}
}
