package com.moe.LiveVisualizer.duang;
import android.util.DisplayMetrics;
import android.graphics.Canvas;
import java.util.Random;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Matrix;

public class Graph extends Duang
{
	private float rotate,offsetX,offsetY;
	private int alpha;
	private Paint paint=new Paint();
	private Path path=new Path();
	private boolean direction;
	private Matrix matrix=new Matrix();
	private boolean repeat;
	public Graph(){
		paint.setStrokeWidth(2);
	}
	@Override
	public void draw(Canvas canvas)
	{
		alpha+=repeat?-4:3;
		if(alpha<0||getOffsetX()<-getSize()||getOffsetX()>getMaxWidth()+getSize()||getOffsetY()<-getSize()||getOffsetY()>getMaxHeight()+getSize()){
			reset(true);
		}
		if(alpha>0x8f){
			repeat=true;
			alpha=0x8f;
		}
		paint.setAlpha(alpha);
		scrollBy(offsetX,offsetY);
		path.offset(offsetX,offsetY);
		matrix.setRotate(direction?rotate:-rotate,getOffsetX(),getOffsetY());
		path.transform(matrix);
		//paint.setStyle(Paint.Style.FILL);
		canvas.drawPath(path,paint);
		//paint.setStyle(Paint.Style.STROKE);
		//paint.setShadowLayer(2,0,0,paint.getColor()|0xff000000);
		//canvas.drawPath(path,paint);
		//paint.setShadowLayer(0,0,0,0);
	}

	@Override
	public void random(Random random)
	{
		alpha=0;
		repeat=false;
		//生成左标
		setOffsetX(random.nextInt(getMaxWidth()/3)+getMaxWidth()/3);
		setOffsetY(random.nextInt(getMaxWidth()/3)+(getMaxHeight()-getMaxWidth()/3)/2);
		//生成图形
		path.reset();
		switch(GRAPH.$VALUES[random.nextInt(3)]){
			case SQUARE:
				path.addRect(getOffsetX()-getSize(),getOffsetY()-getSize(),getOffsetX()+getSize(),getOffsetY()+getSize(),Path.Direction.CW);
				break;
			case TRIANGLE:
				double degress=2d/3*Math.PI;
				path.moveTo(getOffsetX()+(float)(getSize()*Math.cos(0)),getOffsetY()+(float)(getSize()*Math.sin(0)));
				path.lineTo(getOffsetX()+(float)(getSize()*Math.cos(degress)),getOffsetY()+(float)(getSize()*Math.sin(degress)));
				path.lineTo(getOffsetX()+(float)(getSize()*Math.cos(degress*2)),getOffsetY()+(float)(getSize()*Math.sin(2*degress)));
				path.close();
				break;
			case ROUND:
				path.addCircle(getOffsetX(),getOffsetY(),getSize(),Path.Direction.CW);
				break;
		}
		//随机颜色
		paint.setColor(random.nextInt(0xffffff)|0x5f000000);
		//
		offsetX=(random.nextFloat()*getWind()+1f)*(random.nextBoolean()?2.5f:-2.5f);
		offsetY=(random.nextFloat()*getSpeed()+0.01f)*(random.nextBoolean()?0.25f:-0.25f);
		direction=random.nextBoolean();
		rotate=random.nextFloat()+1f;
		//matrix.reset();
		//matrix.setTranslate(getOffsetX(),getOffsetY());
	}
	
	enum GRAPH{
		SQUARE,ROUND,TRIANGLE;
	}

	@Override
	public float getSize()
	{
		return super.getSize()/2;
	}
	private void scrollBy(float x,float y){
		setOffsetX(getOffsetX()+x);
		setOffsetY(getOffsetY()+y);
	}
}
