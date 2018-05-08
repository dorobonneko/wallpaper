package com.moe.LiveVisualizer.internal;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.content.SharedPreferences;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Movie;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.moe.LiveVisualizer.inter.Draw;
import android.animation.ValueAnimator;
import android.animation.ObjectAnimator;

public class WallpaperThread extends Thread implements SharedPreferences.OnSharedPreferenceChangeListener
{

	private LiveWallpaper.WallpaperEngine engine;
	private ImageDraw imageDraw;
	private long oldTime;
	private double[] buffer;
	private byte[] fft;
	private Paint paint=new Paint();
	private int fpsDelay=33;
	private Matrix wallpaperMatrix;//缩放壁纸用
	public WallpaperThread(LiveWallpaper.WallpaperEngine engine)
	{
		this.engine = engine;
		imageDraw = new ImageDraw(engine);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,18,engine.getContext().getResources().getDisplayMetrics()));
		paint.setColor(0xff000000);
		engine.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(engine.getSharedPreferences(),"highfps");
		onSharedPreferenceChanged(engine.getSharedPreferences(),"downspeed");
		//if(engine.getColorList()!=null)
		onSharedPreferenceChanged(engine.getSharedPreferences(),"scaleImage");
		//onSharedPreferenceChanged(engine.getSharedPreferences(),"scaleIamge");
		//onSharedPreferenceChanged(engine.getSharedPreferences(),"cutImage");
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences p1, String p2)
	{
		switch(p2){
			case "highfps"://false
				fpsDelay=p1.getBoolean(p2,false)?16:33;
				break;
			case "downspeed"://50
				if(imageDraw!=null)imageDraw.setDownSpeed(p1.getInt("downspeed",15));
				break;
			case "borderWidth"://30px
				if(imageDraw!=null)imageDraw.setBorderWidth(p1.getInt(p2,30));
				break;
			case "borderHeight"://100dp
			if(imageDraw!=null)
				imageDraw.setBorderHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,p1.getInt(p2,100),engine.getContext().getResources().getDisplayMetrics()));
			break;
			case "spaceWidth"://20px
			if(imageDraw!=null)
				imageDraw.setSpaceWidth(p1.getInt(p2,20));
				break;
			case "height"://10%
			if(imageDraw!=null)
				imageDraw.setDrawHeight(engine.getHeight()-p1.getInt(p2,10)/100.0f*engine.getHeight());
				break;
			case "round"://圆角
				if(imageDraw!=null)
					imageDraw.setRound(p1.getBoolean(p2,true));
				break;
			case "scaleImage":
				if(p1.getBoolean(p2,true))
					wallpaperMatrix=new Matrix();
					else
					wallpaperMatrix=null;
				if(imageDraw!=null)
					imageDraw.setCenterScale(wallpaperMatrix!=null);
				break;
			case "cutImage":
				if(imageDraw!=null)
					imageDraw.setCutImage(p1.getBoolean(p2,true));
				break;
			case "offsetX":
				if(imageDraw!=null)
					imageDraw.setOffsetX(p1.getInt(p2,engine.getWidth()/2));
					break;
			case "offsetY":
				if(imageDraw!=null)
					imageDraw.setOffsetY(p1.getInt(p2,engine.getHeight()/2));
					break;
			case "degress":
				if(imageDraw!=null)
					imageDraw.setDegressStep(p1.getInt(p2,10)/100f*10);
				break;
			case "circleRadius":
				if(imageDraw!=null)
					imageDraw.setCircleRadius(p1.getInt(p2,Math.min(engine.getWidth(),engine.getHeight())/6));
				break;
			
		}
	}
	
	public void close()
	{
		imageDraw = null;
		engine.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void run()
	{

		/*	Looper.prepare();
		 handler = new Handler(){
		 public void handleMessage(Message msg)
		 {*/
		while ( imageDraw != null )
		{
			long delay=0;
			SurfaceHolder sh=engine.getSurfaceHolder();
			if ( sh != null && engine.isVisible() )
			{
				synchronized ( sh )
				{
					final Canvas canvas=sh.lockCanvas();
					if ( canvas != null )
					{
						if(!engine.isReady()){//启动失败，蓝屏警告
							canvas.drawColor(0xff0096ff);
							canvas.drawText((engine.getError()==null?"无法启动":engine.getError()),canvas.getWidth()/2,(canvas.getHeight()-paint.descent()-paint.ascent())/2.0f,paint);
						}
						else{
							canvas.drawColor(0xff000000);//先涂黑
							Bitmap bitmap=engine.getWallpaper();//获取背景
							if(bitmap!=null){
								if(wallpaperMatrix!=null){
									float scale=Math.min((float)engine.getWidth()/bitmap.getWidth(),(float)engine.getHeight()/bitmap.getHeight());
									wallpaperMatrix.setScale(scale,scale);
									wallpaperMatrix.postTranslate((engine.getWidth()-bitmap.getWidth()*scale)/2,(engine.getHeight()-bitmap.getHeight()*scale)/2);
									canvas.drawBitmap(bitmap,wallpaperMatrix,null);
								}else{
									canvas.drawBitmap(bitmap,(engine.getWidth()-bitmap.getWidth())/2f,(engine.getHeight()-bitmap.getHeight())/2f,null);
								}
							}
							}
						if ( imageDraw != null &&engine.getVisualizer()!=null)
						{
							try{
							if(fft==null)
								fft=new byte[engine.getCaptureSize()];
								engine.getVisualizer().getFft(fft);
							if(buffer==null)
								buffer = new double[engine.getFftSize()];    
							//model[0] =(byte)(fft[0]&0x7f);  

							for (int n = 1; n < buffer.length;n++)    
							{    
								//第k个点频率 getSamplingRate() * k /(getCaptureSize()/2)  
								int k=2*n;
								buffer[n-1] = Math.hypot(fft[k]==-1?0:fft[k], fft[k + 1]==-1?0:fft[k+1]);   
							}
							Draw draw=imageDraw.lockData(buffer);
							if ( draw != null )
								draw.draw(canvas);
							}catch(Exception e){}
						}
						try
						{
							sh.unlockCanvasAndPost(canvas);
						}
						catch (Exception E)
						{}
					}
				}
			}
			long blank=(System.nanoTime() - oldTime)/1000000;
			try
			{
				long space=delay==0?fpsDelay:delay;
				sleep(blank > space?0: (space - blank));
				oldTime = System.nanoTime();
				
			}
			catch (InterruptedException e)
			{}

		}

		/*		}
		 };
		 Looper.loop();
		 */

	}

}
