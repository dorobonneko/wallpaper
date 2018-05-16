package com.moe.LiveVisualizer.draw;
import android.graphics.Shader;
import android.graphics.Bitmap;
import android.graphics.PointF;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.util.TypedValue;
import com.moe.LiveVisualizer.internal.OnColorSizeChangedListener;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.LinearGradient;

public class RippleDraw extends CircleDraw
{
	private float[] points;
	private Shader shader;
	private Bitmap shaderBuffer;
	private float borderHeight;
	private Paint paint;
	public RippleDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		super(draw,engine);
		paint = new Paint();
		paint.setStrokeCap(getEngine().getSharedPreferences().getBoolean("round",true)?Paint.Cap.ROUND:Paint.Cap.SQUARE);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(0xff39c5bb);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(engine.getSharedPreferences().getInt("borderWidth",30));
		
		borderHeight=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,engine.getSharedPreferences().getInt("borderHeight",100),engine.getContext().getResources().getDisplayMetrics());
		engine.registerColorSizeChangedListener(new OnColorSizeChangedListener(){

				@Override
				public void onColorSizeChanged()
				{
					shader = null;
					if(shaderBuffer!=null)
						shaderBuffer.recycle();
					shaderBuffer=null;
				}
			});
	}

	@Override
	public void setRound(boolean round)
	{
		paint.setStrokeCap(round?Paint.Cap.ROUND:Paint.Cap.SQUARE);
	}

	
	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		Paint paint=this.paint;
		if(color_mode==2){
			drawMode(canvas,color_mode,true);
		}else if(color_mode==3){
			int color=getColor();
			paint.setColor(getEngine().getSharedPreferences().getBoolean("nenosync",false)?color:0xffffffff);
			paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
			drawMode(canvas,color_mode,false);
			paint.setShadowLayer(0,0,0,0);
		}else
			switch ( getEngine().getColorList().size() )
			{
				case 0:
					paint.setColor(0xff39c5bb);
					drawMode( canvas,color_mode,false);
					break;
				case 1:
					paint.setColor(getEngine().getColorList().get(0));
					drawMode( canvas,color_mode,false);
					break;
				default:
					switch( color_mode)
					{
						case 0:
							//final Bitmap src=Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
							//final Canvas tmpCanvas=new Canvas(src);
							final int layer=canvas.saveLayer(0,0,canvas.getWidth(),canvas.getHeight(),null,Canvas.ALL_SAVE_FLAG);
							drawMode(canvas,color_mode,false);
							if ( shader == null )
								shader = new SweepGradient(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, getEngine().getColorList().toArray(), null);
							if(shaderBuffer==null){
								shaderBuffer=Bitmap.createBitmap(canvas.getWidth(),canvas.getHeight(),Bitmap.Config.ARGB_4444);
								Canvas shaderCanvas=new Canvas(shaderBuffer);
								paint.setShader(shader);
								shaderCanvas.drawRect(0,0,shaderCanvas.getWidth(),shaderCanvas.getHeight(),paint);
								paint.setShader(null);
							}
							paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
							//canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
							canvas.drawBitmap(shaderBuffer, 0, 0, paint);
							//paint.setShader(null);
							paint.setXfermode(null);
							canvas.restoreToCount(layer);
							//canvas.drawBitmap(src, 0, 0, paint);
							//src.recycle();
							/*if ( shader == null )
							 shader = new SweepGradient(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, getEngine().getColorList().toArray(), null);
							 paint.setShader(shader);
							 drawLines(getFft(), canvas, false,color_mode);
							 paint.setShader(null);*/
							break;
						default:
							drawMode(canvas,color_mode,true);
							paint.setShader(null);

							break;
					}
					break;
			}
	}

	@Override
	public void onBorderWidthChanged(int width)
	{
		paint.setStrokeWidth(width);
	}

	@Override
	public void onBorderHeightChanged(int height)
	{
		borderHeight=height;
	}

	@Override
	public void onSpaceWidthChanged(int space)
	{
		
	}

	

	


	@Override
	public int size()
	{
		return 32;
	}

	private void drawMode(Canvas canvas,int mode,boolean useMode){
		if(points==null||points.length!=size())
			points=new float[size()];
		double[] buffer=getFft();
		Paint paint=this.paint;
		paint.setStyle(Paint.Style.STROKE);
		int colorStep=0;
		PointF point=getPointF();
		for(int i=0;i<size();i++){
			if ( useMode )
				if ( mode == 1 )
				{
					paint.setColor(getEngine().getColorList().get(colorStep));
					colorStep++;
					if ( colorStep >= getEngine().getColorList().size() )colorStep = 0;
				}
				else if ( mode == 2 )
				{
					paint.setColor(0xff000000|(int)(Math.random()*0xffffff));
				}
			float height=(float)(buffer[i]/127*borderHeight);
			if(height<points[i])
				height=points[i]-(points[i]-height)*getDownSpeed();
			if(height<0)height=0;
			points[i]=height;
			//if(paint.getStrokeCap()==Paint.Cap.ROUND)
				canvas.drawCircle(point.x,point.y,height,paint);
			//else
			//	canvas.drawRect(point.x-paint.getStrokeWidth()/2,point.y-height,point.x+paint.getStrokeWidth()/2,point.y,paint);
			//canvas.rotate(degress,point.x,point.y);
		}
		
		paint.setStyle(Paint.Style.FILL);
	}
}
