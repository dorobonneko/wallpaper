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
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.moe.LiveVisualizer.inter.Draw;
import android.animation.ValueAnimator;
import android.animation.ObjectAnimator;

public class WallpaperThread extends Thread implements SharedPreferences.OnSharedPreferenceChangeListener
{

	private LiveWallpaper.WallpaperEngine engine;
	//private Handler handler;
	private ImageDraw imageDraw;
	private long oldTime;
	private double[] buffer;
	private byte[] fft;
	private Paint paint=new Paint();
	private int fpsDelay=33;
	//private ValueAnimator colorAnime;
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
		if(engine.getColorList()!=null)
		onSharedPreferenceChanged(engine.getSharedPreferences(),"color_mode");
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences p1, String p2)
	{
		switch(p2){
			case "highfps"://false
				fpsDelay=p1.getBoolean(p2,false)?16:33;
				break;
			case "downspeed"://50
				if(imageDraw!=null)imageDraw.setDownSpeed(p1.getInt("downspeed",50));
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
			/*case "color_mode":
				if(p1.getString(p2,"0").equals("4")){
					if(colorAnime!=null){
						colorAnime.cancel();
					}
					if(engine.getColorList().size()>0){
					colorAnime=ObjectAnimator.ofInt(engine.getColorList().toArray());
					colorAnime.setDuration(engine.getColorList().size()*30000);
					try{colorAnime.start();}catch(Exception e){}
					}
					}else if(colorAnime!=null){
						colorAnime.cancel();
						colorAnime=null;
					}
				break;*/
		}
	}
	/*public int getColor(){
		try{
		if(colorAnime!=null)return colorAnime.getAnimatedValue();
		}catch(Exception e){}
		return 0xff39c5bb;
	}
	public void notifyColorChanged(){
		if(engine.getSharedPreferences().getString("color_mode","0").equals("4")){
			if(colorAnime!=null){
				colorAnime.cancel();
			}
			if(engine.getColorList().size()>0){
			colorAnime=ObjectAnimator.ofInt(engine.getColorList().toArray());
			colorAnime.setDuration(engine.getColorList().size()*5000);
			try{colorAnime.start();}catch(Exception e){}
			}
		}
	}*/
	/*public void updateFft(double[] fft)
	{
		this.fft=fft;
	}*/
	
	public void close()
	{
		imageDraw = null;
		engine.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	/*public void onUpdate(byte[] p2)
	{
		this.buffer = p2;
		/*if ( engine.isVisible() && handler != null )
		 {
		 handler.removeMessages(0);
		 handler.sendMessageDelayed(handler.obtainMessage(0, p2), 26);
		 }
	}*/

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
						/*if ( engine.getSharedPreferences().getBoolean("artwork", false) && engine.getArtwork() != null )
						 {
						 Bitmap buffer=engine.getArtwork();
						 Matrix matrix=new Matrix();
						 float scale=Math.max(((float)canvas.getWidth() / buffer.getWidth()), ((float)canvas.getHeight() / buffer.getHeight()));
						 matrix.setScale(scale, scale);
						 float offsetX=(buffer.getWidth() * scale - canvas.getWidth()) / 2;
						 float offsetY=(buffer.getHeight() * scale - canvas.getHeight()) / 2;
						 matrix.postTranslate(-offsetX, -offsetY);
						 canvas.drawBitmap(engine.getArtwork(), matrix, null);
						 }
						 else*/
						if(!engine.isReady()){
							canvas.drawColor(0xff0096ff);
							canvas.drawText((engine.getError()==null?"无法启动":engine.getError()),canvas.getWidth()/2,(canvas.getHeight()-paint.descent()-paint.ascent())/2.0f,paint);
						}else if ( engine.isGif() && engine.getMovie() != null )
						{
							canvas.drawColor(0xff000000);
							try{
							final GifDecoder movie=engine.getMovie();
							movie.advance();
							Bitmap bit=movie.getNextFrame();
							if ( bit == null )
							{movie.resetFrameIndex();
								bit = movie.getNextFrame();}
							if ( bit != null )
							{
								Matrix matrix=new Matrix();
								float scale=Math.min(canvas.getWidth() / (float)bit.getWidth(), canvas.getHeight() / (float)bit.getHeight());
								matrix.setScale(scale, scale);
								matrix.postTranslate((canvas.getWidth() - bit.getWidth() * scale) / 2.0f, (canvas.getHeight() - bit.getHeight() * scale) / 2.0f);
								canvas.drawBitmap(bit, matrix, null);
								bit.recycle();
								delay=movie.getDelay(movie.getCurrentFrameIndex());
							}
							}catch(Exception e){
								canvas.drawColor(0xff0096ff);
								canvas.drawText("Gif出错",canvas.getWidth()/2,(canvas.getHeight()-paint.descent()-paint.ascent())/2.0f,paint);
							}
						}
						else if ( engine.getWallpaper() != null )
							canvas.drawBitmap(engine.getWallpaper(), 0, 0, null);
						else
							canvas.drawColor(0xff000000);
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
