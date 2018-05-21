package com.moe.LiveVisualizer.internal;
import android.graphics.*;

import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import com.moe.LiveVisualizer.draw.circle.RingDraw;
import com.moe.LiveVisualizer.inter.Draw;
import com.moe.LiveVisualizer.duang.Engine;

public class WallpaperThread extends Thread implements SharedPreferences.OnSharedPreferenceChangeListener
{
	private LiveWallpaper.WallpaperEngine engine;
	private ImageDraw imageDraw;
	private long oldTime;
	private Paint paint=new Paint();
	private int fpsDelay=33;
	private Matrix wallpaperMatrix;//缩放壁纸用
	private Engine mDuangEngine;//屏幕特效引擎
	public WallpaperThread(LiveWallpaper.WallpaperEngine engine)
	{
		this.engine = engine;
		imageDraw = new ImageDraw(engine);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, engine.getContext().getResources().getDisplayMetrics()));
		paint.setColor(0xff000000);
		engine.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(engine.getSharedPreferences(), "highfps");
		onSharedPreferenceChanged(engine.getSharedPreferences(), "downspeed");
		//if(engine.getColorList()!=null)
		onSharedPreferenceChanged(engine.getSharedPreferences(), "scaleImage");
		onSharedPreferenceChanged(engine.getSharedPreferences(),"duang");
		//onSharedPreferenceChanged(engine.getSharedPreferences(),"cutImage");
	}

	public void onSizeChanged()
	{
		if (imageDraw != null)
			imageDraw.notifySizeChanged();
		onSharedPreferenceChanged(engine.getSharedPreferences(), "height");
		if(mDuangEngine!=null)
			mDuangEngine.changed();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences p1, String p2)
	{
		switch (p2)
		{
			case "highfps"://false
				fpsDelay = p1.getBoolean(p2, false) ?16: 33;
				break;
			case "downspeed"://50
				if (imageDraw != null)imageDraw.setDownSpeed(p1.getInt("downspeed", 15));
				break;
			case "borderWidth"://30px
				if (imageDraw != null)imageDraw.setBorderWidth(p1.getInt(p2, 30));
				break;
			case "borderHeight"://100dp
				if (imageDraw != null)
					imageDraw.setBorderHeight(p1.getInt(p2, 100));
				break;
			case "spaceWidth"://20px
				if (imageDraw != null)
					imageDraw.setSpaceWidth(p1.getInt(p2, 20));
				break;
			case "height"://10%
				if (imageDraw != null)
					imageDraw.setDrawHeight(engine.getDisplayHeight() - p1.getInt(p2, 10) / 100f * engine.getDisplayHeight());
				break;
			case "round"://圆角
				if (imageDraw != null)
					imageDraw.setRound(p1.getBoolean(p2, true));
				break;
			case "scaleImage":
				if (p1.getBoolean(p2, true))
					wallpaperMatrix = new Matrix();
				else
					wallpaperMatrix = null;
				if (imageDraw != null)
					imageDraw.setCenterScale(wallpaperMatrix != null);
				break;
			case "cutImage":
				if (imageDraw != null)
					imageDraw.setCutImage(p1.getBoolean(p2, true));
				break;
			case "offsetX":
				if (imageDraw != null)
					imageDraw.setOffsetX(p1.getInt(p2, Math.min(engine.getDisplayWidth(), engine.getDisplayHeight()) / 2));
				break;
			case "offsetY":
				if (imageDraw != null)
					imageDraw.setOffsetY(p1.getInt(p2, Math.max(engine.getDisplayHeight(), engine.getDisplayWidth()) / 2));
				break;
			case "degress":
				if (imageDraw != null)
					imageDraw.setDegressStep(p1.getInt(p2, 10) / 100f * 10);
				break;
			case "circleRadius":
				if (imageDraw != null)
					imageDraw.setCircleRadius(p1.getInt(p2, Math.min(engine.getDisplayWidth(), engine.getDisplayHeight()) / 6));
				break;
			case "direction":
				if (imageDraw != null)
					imageDraw.setDirection(Integer.parseInt(p1.getString(p2, RingDraw.OUTSIDE + "")));
				break;
			case "gpu":
				SurfaceHolder holder=engine.getSurfaceHolder();
				if (holder != null)
					holder.setType(p1.getBoolean("gpu", false) ?holder.SURFACE_TYPE_PUSH_BUFFERS: holder.SURFACE_TYPE_GPU);
				break;
			case "color_direction":
				if (imageDraw != null)
					imageDraw.setShader(null);
				break;
			case "duang":
				if(p1.getBoolean(p2,false)){
					mDuangEngine=Engine.init(engine);
					}else if(mDuangEngine!=null)
					{
						mDuangEngine.reset();
						mDuangEngine=null;
					}
				break;
			case "duang_size":
				if(mDuangEngine!=null)
					mDuangEngine.setSizeChanged(p1.getInt(p2,50));
				break;
			case "duang_minSize":
				if(mDuangEngine!=null)
					mDuangEngine.setMinSize(p1.getInt(p2,10));
				break;
			case "duang_maxSize":
				if(mDuangEngine!=null)
					mDuangEngine.setMaxSize(p1.getInt(p2,50));
				break;
			case "duang_speed":
				if(mDuangEngine!=null)
					mDuangEngine.setMaxSpeed(p1.getInt(p2,30));
				break;
			case "duang_wind":
				if(mDuangEngine!=null)
					mDuangEngine.setWind(p1.getInt(p2,2));
				break;
		}
	}

	public void close()
	{
		imageDraw = null;
		engine.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		if(mDuangEngine!=null)
		mDuangEngine.reset();
	}

	@Override
	public void run()
	{

		/*	Looper.prepare();
		 handler = new Handler(){
		 public void handleMessage(Message msg)
		 {*/
		while (imageDraw != null)
		{
			long delay=0;
			SurfaceHolder sh=engine.getSurfaceHolder();
			if (sh != null && engine.isVisible())
			{
				synchronized (sh)
				{
					final Canvas canvas=sh.lockCanvas();
					if (canvas != null)
					{
						canvas.drawColor(0,PorterDuff.Mode.CLEAR);
						if (!engine.isReady())
						{//启动失败，蓝屏警告
							canvas.drawColor(0xff0096ff);
							canvas.drawText((engine.getError() == null ?"无法启动": engine.getError()), canvas.getWidth() / 2, (canvas.getHeight() - paint.descent() - paint.ascent()) / 2.0f, paint);
						}
						else
						{
							//canvas.drawColor(0xff000000);//先涂黑
							Bitmap bitmap=engine.getWallpaper();//获取背景
							if (bitmap != null)
							{
								if (wallpaperMatrix != null)
								{
									float scale=Math.min((float)canvas.getWidth() / bitmap.getWidth(), (float)canvas.getHeight() / bitmap.getHeight());
									wallpaperMatrix.setScale(scale, scale);
									wallpaperMatrix.postTranslate((canvas.getWidth() - bitmap.getWidth() * scale) / 2, (canvas.getHeight() - bitmap.getHeight() * scale) / 2);
									canvas.drawBitmap(bitmap, wallpaperMatrix, null);
								}
								else
								{
									canvas.drawBitmap(bitmap, (canvas.getWidth() - bitmap.getWidth()) / 2f, (canvas.getHeight() - bitmap.getHeight()) / 2f, null);
								}
							}
						}
						//背景
						if (imageDraw != null && engine.getVisualizer() != null)
						{
							try
							{
								Draw draw=imageDraw.lockData();
								if (draw != null)
									draw.draw(canvas);
							}
							catch (Exception e)
							{}
						}
						if(mDuangEngine!=null)
							mDuangEngine.draw(canvas);
						try
						{
							sh.unlockCanvasAndPost(canvas);
						}
						catch (Exception E)
						{}
					}
				}
			}
			long blank=(System.nanoTime() - oldTime) / 1000000;
			try
			{
				long space=delay == 0 ?fpsDelay: delay;
				sleep(blank > space ?0: (space - blank));
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
