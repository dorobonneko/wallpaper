package com.moe.LiveVisualizer.duang;
import android.util.DisplayMetrics;
import android.graphics.Canvas;
import java.util.Random;
import android.graphics.Matrix;
import android.graphics.Camera;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Sakura extends Duang
{
	private Matrix matrix;
	private Camera camera;
	private float speed,wind;
	public Sakura(){
		matrix=new Matrix();
		camera=new Camera();
	}

	@Override
	public void draw(Canvas canvas)
	{
		if(getOffsetX()>getMaxWidth()+getSize()||getOffsetY()>getMaxHeight()+getSize()){
			matrix.postTranslate(-getOffsetX(),-getOffsetY());
			setOffsetX(getRandom().nextInt((int)(getMaxWidth()-getSize())));
			setOffsetY(-getSize());
			matrix.postTranslate(getOffsetX(),getOffsetY());
		}else if(getOffsetY()>-getSize()){
			setOffsetX(getOffsetX()+wind);
			setOffsetY(getOffsetY()+speed);
			matrix.postTranslate(wind,speed);
			canvas.drawBitmap(getEngine().getBuffer(),matrix,null);
			}else {
				setOffsetY(getOffsetY()+speed);
				matrix.postTranslate(0,speed);
			}
	}

	@Override
	public void random(Random random)
	{
		setOffsetX(random.nextInt((int)(getMaxWidth()-getSize())));
		if(isFirst())
		setOffsetY(-random.nextInt(getMaxHeight()));
		else
		setOffsetY(-getSize());
		speed=getSize()/getMaxSize()*getSpeed()/5+1;
		wind=random.nextFloat()*getWind();
		camera.save();
		camera.rotateX(random.nextInt(360));
		camera.rotateY(random.nextInt(360));
		camera.rotateZ(random.nextInt(360));
		camera.getMatrix(matrix);
		camera.restore();
		Bitmap bitmap=getEngine().getBuffer();
		if(bitmap!=null){
		float scale=getSize()/bitmap.getWidth();
		matrix.postScale(scale,scale);
		}matrix.postTranslate(getOffsetX(),getOffsetY());
	}


	
}
