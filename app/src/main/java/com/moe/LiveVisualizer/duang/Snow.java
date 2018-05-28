package com.moe.LiveVisualizer.duang;
import android.util.DisplayMetrics;
import android.graphics.Canvas;
import java.util.Random;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.Matrix;

public class Snow extends Duang
{
	private Path path;
	private Paint paint;
	private Matrix matrix=new Matrix();
	private float windSpeed,speed;
	private boolean wind;
	public Snow(DisplayMetrics display,int maxSize,int minSize,int wind,int speed){
		super(display,maxSize,minSize,wind,speed);
		paint=new Paint();
		paint.setColor(0xffffffff);
		path=new Path();
	}

	@Override
	public void draw(Canvas canvas)
	{
		setOffsetY(getOffsetY()+speed);
		if(getOffsetX()<-getSize()||getOffsetX()>getMaxWidth()||getOffsetY()>getMaxHeight()){
			path.offset(-getOffsetX(),-getOffsetY());
			setOffsetY(-getSize());
			setOffsetX(getRandom().nextInt(getMaxWidth())-getSize());
			path.offset(getOffsetX(),getOffsetY());
			}
		else if(getOffsetY()>-getSize()){
			setOffsetX(getOffsetX()+(wind?windSpeed:-windSpeed));
			path.offset(wind?windSpeed:-windSpeed,speed);
			canvas.drawPath(path,paint);
			}else
			path.offset(0,speed);
	}

	@Override
	public void random(Random random)
	{
		setOffsetX(random.nextInt((int)(getMaxWidth()-getSize())));
		//setSize(random.nextFloat()*(getMaxSize()-getMinSize())+getMinSize());
		if(isFirst())
			setOffsetY(-random.nextInt(getMaxHeight()));
		else
			setOffsetY(-getSize());
		float scale=getSize()/24;
		matrix.setScale(scale,scale);
		resetPath();
		//随机生成风向
		wind=random.nextBoolean();
		//根据尺寸计算风力
		windSpeed=random.nextFloat()*getWind();
		//windSpeed=getSize()/getMaxSize()*getWind();
		//根据尺寸计算下降速度
		speed=getSize()/getMaxSize()*getSpeed()/10+1;
		}
	private void resetPath(){
		path.reset();
		path.moveTo(20.79f,13.95f);
		path.lineTo(18.46f,14.57f);
		path.lineTo(16.46f,13.44f);
		path.lineTo(16.46f,10.56f);//
		path.lineTo(18.46f,9.43f);
		path.lineTo(20.79f,10.05f);
		path.lineTo(21.31f,8.12f);
		path.lineTo(19.54f,7.65f);
		path.lineTo(20,5.88f);
		path.lineTo(18.07f,5.36f);
		path.lineTo(17.45f,7.69f);
		path.lineTo(15.45f,8.82f);
		path.lineTo(13,7.38f);
		path.lineTo(13,5.12f);//
		path.lineTo(14.71f,3.41f);
		path.lineTo(13.29f,2f);
		path.lineTo(12,3.29f);
		path.lineTo(10.71f,2f);
		path.lineTo(9.29f,3.41f);
		path.lineTo(11,5.12f);
		path.lineTo(11,7.38f);//
		path.lineTo(8.5f,8.82f);
		path.lineTo(6.5f,7.69f);
		path.lineTo(5.92f,5.36f);
		path.lineTo(4,5.88f);
		path.lineTo(4.47f,7.65f);
		path.lineTo(2.7f,8.12f);
		path.lineTo(3.22f,10.05f);
		path.lineTo(5.55f,9.43f);
		path.lineTo(7.55f,10.56f);
		path.lineTo(7.55f,13.45f);//
		path.lineTo(5.55f,14.58f);
		path.lineTo(3.22f,13.96f);
		path.lineTo(2.7f,15.89f);
		path.lineTo(4.47f,16.36f);
		path.lineTo(4,18.12f);
		path.lineTo(5.93f,18.64f);
		path.lineTo(6.55f,16.31f);
		path.lineTo(8.55f,15.18f);
		path.lineTo(11,16.62f);
		path.lineTo(11,18.88f);//
		path.lineTo(9.29f,20.59f);
		path.lineTo(10.71f,22f);
		path.lineTo(12,20.71f);
		path.lineTo(13.29f,22f);
		path.lineTo(14.7f,20.59f);
		path.lineTo(13,18.88f);
		path.lineTo(13,16.62f);//
		path.lineTo(15.5f,15.17f);
		path.lineTo(17.5f,16.3f);
		path.lineTo(18.12f,18.63f);
		path.lineTo(20,18.12f);
		path.lineTo(19.53f,16.35f);
		path.lineTo(21.3f,15.88f);
		path.lineTo(20.79f,13.95f);
		path.moveTo(9.5f,10.56f);
		path.lineTo(12,9.11f);
		path.lineTo(14.5f,10.56f);
		path.lineTo(14.5f,13.44f);//
		path.lineTo(12,14.89f);
		path.lineTo(9.5f,13.44f);
		path.lineTo(9.5f,10.56f);//
		path.close();
		path.transform(matrix);
		path.offset(getOffsetX(),getOffsetY());
	}
	
}
