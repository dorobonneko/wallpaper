package com.moe.LiveVisualizer.duang;
import android.graphics.Canvas;
import java.util.Random;
import android.util.DisplayMetrics;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.Matrix;

public class Bubble extends Duang
{
	private float speed,wind,height;
	private Paint paint;
	private Matrix matrix;
	private boolean direction;
	public Bubble(){
		paint=new Paint();
		//paint.setStyle(Paint.Style.STROKE);
		//paint.setColor(0x00000000);
		//paint.setStrokeWidth(0);
		matrix=new Matrix();
		//paint.setAlpha(0x7f);
	}
	@Override
	public void draw(Canvas canvas)
	{
		if(getOffsetX()<-getSize()||getOffsetX()>getMaxWidth()||getOffsetY()<-getSize()){
			matrix.postTranslate(-getOffsetX(),-getOffsetY());
			setOffsetY(getMaxHeight());
			setOffsetX(getRandom().nextInt(getMaxWidth())-getSize());
			matrix.postTranslate(getOffsetX(),getOffsetY());
		}else if(getOffsetY()<getMaxHeight()){
			float offset=(direction&&getOffsetY()>height?wind:-wind);
			setOffsetX(getOffsetX()+offset);
			setOffsetY(getOffsetY()-speed);
			matrix.postTranslate(offset,-speed);
			canvas.drawBitmap(getEngine().getBuffer(),matrix,paint);
			}else{
			setOffsetY(getOffsetY()-speed);
			matrix.postTranslate(0,-speed);
		}
	}

	@Override
	public void random(Random random)
	{
		paint.setColorFilter(new PorterDuffColorFilter(random.nextInt(0x7f7f7f)+0x7f7f7f7f,PorterDuff.Mode.SRC_IN));
		setOffsetX(random.nextInt(getMaxWidth())-getSize());
		if(isFirst())
		setOffsetY(random.nextInt(getMaxHeight())+getMaxHeight());
		else
		setOffsetY(getMaxHeight());
		speed=getSize()/getMaxSize()*getSpeed()/5+1;
		wind=random.nextFloat()*getWind();
		if(getEngine().getBuffer()!=null)
		matrix.setScale(getSize()/getEngine().getBuffer().getWidth(),getSize()/getEngine().getBuffer().getHeight());
		matrix.postTranslate(getOffsetX(),getOffsetY());
		direction=random.nextBoolean();
		height=random.nextInt(getMaxHeight()/2)+getMaxHeight()/4;
	}
	
	
}
