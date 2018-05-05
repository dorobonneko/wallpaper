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
	private int degress=0;
	private float radius;//圆形半径
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
		radius=engine.getWidth()/6f;
		point=new PointF();
		point.x=engine.getWidth()/2.0f;
		point.y=engine.getHeight()/2.0f;
		cutCenterImage=engine.getSharedPreferences().getBoolean("cutImage",true);
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
		if ( getEngine().getCircleImage() == null )
		{
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawCircle(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, canvas.getWidth() / 6, paint);
			paint.setStyle(Paint.Style.FILL);
		}
		else
		{
			final int layer=canvas.saveLayer(0,0,canvas.getWidth(),canvas.getHeight(),null,canvas.ALL_SAVE_FLAG);
			//canvas.drawColor(0xffffffff);
			if(getEngine().getSharedPreferences().getBoolean("circleSwitch",true)){
				canvas.rotate(degress,point.x,point.y);
				degress++;
				if ( degress >= 360 )degress = 0;
			}
			final Bitmap circle=getEngine().getCircleImage();
			if(getCenterScale()!=null){
				Matrix matrix=getCenterScale();
				float scale=Math.max(radius*2/circle.getWidth(),radius*2/circle.getHeight());
				matrix.setScale(scale,scale);
				matrix.postTranslate((getEngine().getWidth()-circle.getWidth()*scale)/2,(getEngine().getHeight()-circle.getHeight()*scale)/2);
			}
			if(cutCenterImage){
				canvas.drawCircle(point.x,point.y, radius, paint);
				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
				if(getCenterScale()!=null)
					canvas.drawBitmap(circle,getCenterScale(),paint);
				else
					canvas.drawBitmap(circle,(getEngine().getWidth()-circle.getWidth())/2,(getEngine().getHeight()-circle.getHeight())/2, paint);
				paint.setXfermode(null);
			}else{
				if(getCenterScale()!=null)
					canvas.drawBitmap(circle,getCenterScale(),paint);
				else
					canvas.drawBitmap(circle,(getEngine().getWidth()-circle.getWidth())/2,(getEngine().getHeight()-circle.getHeight())/2, paint);
			}
			canvas.restoreToCount(layer);

		}
	}
}
