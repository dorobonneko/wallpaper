package com.moe.LiveVisualizer.internal;
import android.graphics.Paint;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.RadialGradient;
import android.graphics.SweepGradient;

public class CircleLineDraw extends ImageDraw
{
	private Paint paint;
	private ImageDraw draw;
	private int degress=0;
	private Shader shader;
	public static CircleLineDraw getInstance(ImageDraw draw,LiveWallpaper.MoeEngine engine){
		return new CircleLineDraw(draw,engine);
	}
	private CircleLineDraw(ImageDraw draw,LiveWallpaper.MoeEngine engine){
		super(engine);
		engine.registerColorSizeChangedListener(new OnColorSizeChangedListener(){

				@Override
				public void onColorSizeChanged()
				{
					shader=null;
				}
			});
		this.draw=draw;
		paint=new Paint();
		paint.setStrokeCap(Paint.Cap.ROUND);
		//paint.setStrokeWidth(2);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(0xff39c5bb);
		
	}

	@Override
	protected byte[] getBuffer()
	{
		return draw.getBuffer();
	}

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		if(getEngine().getCircleImage()==null){
			paint.setStyle(Paint.Style.STROKE);
		canvas.drawCircle(canvas.getWidth()/2.0f,canvas.getHeight()/2.0f,canvas.getWidth()/6,paint);
		}else{
			paint.setStyle(Paint.Style.FILL);
			final Bitmap src=Bitmap.createBitmap(canvas.getWidth()/3,canvas.getWidth()/3,Bitmap.Config.ARGB_8888);
			Canvas tmp=new Canvas(src);
			tmp.save();
			tmp.drawCircle(tmp.getWidth()/2.0f,tmp.getHeight()/2.0f,canvas.getWidth()/6.0f,paint);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			tmp.rotate(degress,tmp.getWidth()/2.0f,tmp.getHeight()/2.0f);
			degress++;
			if(degress>=360)degress=0;
			tmp.drawBitmap(getEngine().getCircleImage(),0,0,paint);
			tmp.restore();
			paint.setXfermode(null);
			canvas.drawBitmap(src,(canvas.getWidth()-tmp.getWidth())/2.0f,(canvas.getHeight()-tmp.getHeight())/2.0f,null);
			src.recycle();
		}
		switch(getEngine().getColorList().size()){
			case 0:
				paint.setColor(0xff39c5bb);
				drawLines(getBuffer(),canvas,false);
				break;
			case 1:
				paint.setColor(getEngine().getColorList().get(0));
				drawLines(getBuffer(),canvas,false);
			break;
			default:
				if(color_mode==0){
					final Bitmap src=Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
					final Canvas tmpCanvas=new Canvas(src);
					drawLines(getBuffer(), tmpCanvas,false);
					paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
					if ( getEngine().getShader() == null||shader==null )
						shader=new SweepGradient(canvas.getWidth()/2.0f, canvas.getHeight()/2.0f, getEngine().getColorList().toArray(),null);
					paint.setShader(shader);
					tmpCanvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
					//canvas.drawBitmap(shader, 0, 0, paint);
					paint.setShader(null);
					paint.setXfermode(null);
					canvas.drawBitmap(src, 0, 0, paint);
					src.recycle();
					}else
				drawLines(getBuffer(),canvas,true);
			break;
		}
		
	}

	private void drawLines(byte[] buffer,Canvas canvas,boolean useMode){
		double length=canvas.getWidth()/3*Math.PI;

		int borderWidth=getEngine().getSharedPreferences().getInt("borderWidth", 30);
		int spaceWidth=getEngine().getSharedPreferences().getInt("spaceWidth", 20);
		int borderHeight=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getEngine().getSharedPreferences().getInt("borderHeight", 30), getEngine().getContext().getResources().getDisplayMetrics());
		int size=0;
		try{
			size=(int)(length - spaceWidth) / (borderWidth + spaceWidth);
		}catch(Exception e){}
		if(size==0)return;
		size=size>buffer.length?buffer.length:size;
		int step=buffer.length / size;
		int colorStep=0;
		float degress_step=360.0f/size;
		float degress=0;
		int y=(canvas.getHeight()-canvas.getWidth()/3)/2;
		int mode=Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode", "0"));
		if(mode==3)
			paint.setColor(getEngine().getColor());
		canvas.save();
			for ( int i=0;i < size;i ++ )
		{
			if(useMode)
			if ( mode == 1 )
			{
				paint.setColor(getEngine().getColorList().get(colorStep));
				colorStep++;
				if ( colorStep >= getEngine().getColorList().size() )colorStep = 0;
			}
			else if ( mode == 2 )
			{
				paint.setColor(getEngine().getColorList().getRandom());
			}
			canvas.drawRect((canvas.getWidth()-borderWidth)/2.0f, y, (canvas.getWidth()-borderWidth)/2.0f + borderWidth,y + (Math.abs(buffer[i * step]) - 128) / 128.0f * borderHeight , paint);
			canvas.rotate(degress,canvas.getWidth()/2.0f,canvas.getHeight()/2.0f);
			degress+=degress_step;
			if(degress>=360)break;
		}
		canvas.restore();
	}
	
}
