package com.moe.LiveVisualizer.draw;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.LinearGradient;
import android.view.SurfaceHolder;
import android.graphics.Shader;
import com.moe.LiveVisualizer.internal.ImageDraw;

public class PopCircleDraw extends Draw
{
	private Paint paint;
	private float[] points;
	private AnimeThread anime;
	public PopCircleDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		super(draw,engine);
		paint=new Paint();
		paint.setStrokeWidth(3);
		paint.setStyle(Paint.Style.STROKE);
	}

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		/*if(anime==null){
			anime=new AnimeThread();
			anime.start();
			}
			*/
			animeDraw(canvas,color_mode);
		
	}
	private void animeDraw(Canvas canvas,int color_mode){
		if(color_mode==2){
			drawPop(canvas,color_mode,true);
		}else
		switch(getEngine().getColorList().size()){
			case 0:
				paint.setColor(0xff39c5bb);
				drawPop(canvas,color_mode,false);
				break;
			case 1:
				paint.setColor(getEngine().getColorList().get(0));
				drawPop(canvas,color_mode,false);
				break;
			default:
				switch(color_mode){
					case 0:
					/*final Bitmap src=Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
					final Canvas tmpCanvas=new Canvas(src);
					drawPop(tmpCanvas,color_mode,false);	
					if ( getEngine().getShader() == null )
						getEngine().setShader(new LinearGradient(0, 0, canvas.getWidth(), 0, getEngine().getColorList().toArray(), null, LinearGradient.TileMode.CLAMP));
					paint.setShader(getEngine().getShader());
					paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
					paint.setStyle(Paint.Style.FILL);
					tmpCanvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
					paint.setStyle(Paint.Style.STROKE);
					//canvas.drawBitmap(shader, 0, 0, paint);
					paint.setShader(null);
					paint.setXfermode(null);
					canvas.drawBitmap(src, 0, 0, paint);
					src.recycle();*/
						if ( getEngine().getShader() == null )
							getEngine().setShader(new LinearGradient(0, 0, canvas.getWidth(), 0, getEngine().getColorList().toArray(), null, LinearGradient.TileMode.CLAMP));
						paint.setShader(getEngine().getShader());
						drawPop(canvas,color_mode,false);	
						paint.setShader(null);
					break;
					case 3:
						Shader shader=getFade();
						if(shader==null)
							setFade(shader=new LinearGradient(0,0,0,canvas.getHeight(),getEngine().getColorList().toArray(),null,LinearGradient.TileMode.CLAMP));
						paint.setShader(shader);
					default:
						drawPop(canvas,color_mode,true);
						paint.setShader(null);

						break;
				}
				break;
		}
	}
	private void drawPop(Canvas canvas,int color_mode,boolean useMode){
		double[] buffer=getFft();
		int borderWidth=getEngine().getSharedPreferences().getInt("borderWidth", 30);
		float spaceWidth=getEngine().getSharedPreferences().getInt("spaceWidth", 20);
		int borderHeight=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getEngine().getSharedPreferences().getInt("borderHeight", 30), getEngine().getContext().getResources().getDisplayMetrics());
		int size=0;
		try
		{
			size = (int)((canvas.getWidth() - spaceWidth) / (borderWidth + spaceWidth));
		}
		catch (Exception e)
		{}
		if ( size == 0 )return;
		size = size > buffer.length ?buffer.length: size;
		if(points==null||points.length!=size)
			points=new float[size];
		spaceWidth = (canvas.getWidth()-size*borderWidth) / ((float)size-1);
		float radius=borderWidth/2.0f;
		float x=radius;//起始像素
		float y=canvas.getHeight() - getEngine().getSharedPreferences().getInt("height", 10) / 100.0f * canvas.getHeight();
		int step=buffer.length / size;
		int colorStep=0;
		int mode=Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode", "0"));
		//if ( mode == 3 )
		//	paint.setColor(getEngine().getColor());
			canvas.drawLine(0,y,canvas.getWidth(),y,paint);
		for ( int i=0;i < size;i ++ )
		{
			if(useMode){
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
			}
			//if(points[i]==0)points[i]=y-borderWidth/2.0f;
			//float offset=currentP-points[i];
			//float offsetY=offset>0?(Math.abs(offset)>5?5:offset):(Math.abs(offset)<-3?-3:offset);
			//points[i]+=offsetY;
			float height=(float)(buffer[i]/127.0d*borderHeight);
			if(height>points[i])
				points[i]=height;
			else
				height=points[i]-(points[i]-height)*getDownSpeed();
			if(height<0)height=0;
			points[i]=height;
			
			canvas.drawCircle(x,y-height-radius,radius,paint);
			x+=(spaceWidth+borderWidth);
		}
	}
	
	class AnimeThread extends Thread
	{

		@Override
		public void run()
		{
			while(true){
				SurfaceHolder sh=getEngine().getSurfaceHolder();
				if(sh!=null){
					Canvas canvas=sh.lockCanvas();
					if(canvas!=null){
				animeDraw(canvas,Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode","0")));
				try{
					sh.unlockCanvasAndPost(canvas);
				}catch(Exception e){}
				}
				}
			try
			{
				sleep(33);
				if(!getEngine().getSharedPreferences().getString("visualizer_mode","0").equals("3")){
					anime=null;
					interrupt();
					break;
					}
			}
			catch (InterruptedException e)
			{}
			}
		}
		
	}
}
