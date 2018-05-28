package com.moe.LiveVisualizer.duang;
import android.util.DisplayMetrics;
import android.graphics.Canvas;
import java.util.Random;
import android.graphics.Paint;

public class Rain extends Duang
{
	private Paint paint;
	private float[] point;
	private float speed;
	public Rain (DisplayMetrics display,int max,int min,int wind,int speed){
		super(display,max,min,wind,speed);
		point=new float[4];
		paint=new Paint();
		paint.setColor(0xffffffff);
	}

	@Override
	public void draw(Canvas canvas)
	{
		if(getOffsetY()>getMaxHeight()||getOffsetX()<=0||getOffsetX()>getMaxWidth())
		{
			setOffsetX(getRandom().nextInt(getMaxWidth()));
			setOffsetY(-getSize());
		}else if(getOffsetY()<0){
			setOffsetY(getOffsetY()+speed);
		}else{
			setOffsetY(getOffsetY()+speed);
			canvas.drawLines(point,paint);
		}
		
	}

	@Override
	public void random(Random random)
	{
		paint.setStrokeWidth(random.nextFloat()*2+3);
		setOffsetX(random.nextInt(getMaxWidth()));
		//setSize(random.nextFloat()*(getMaxSize()-getMinSize())+getMinSize());
		if(isFirst())
			setOffsetY(-random.nextInt(getMaxHeight()));
		else
			setOffsetY(-getSize());
			speed=getSize()/getMaxSize()*getSpeed()+1;
		
	}

	@Override
	protected void setOffsetX(float offsetX)
	{
		point[0]=offsetX;
		point[2]=offsetX;
	}

	@Override
	protected void setOffsetY(float offsetY)
	{
		point[1]=offsetY;
		point[3]=getOffsetY()-getSize();
	}

	@Override
	public float getOffsetX()
	{
		// TODO: Implement this method
		return point[0];
	}

	@Override
	public float getOffsetY()
	{
		// TODO: Implement this method
		return point[1];
	}


	
}
