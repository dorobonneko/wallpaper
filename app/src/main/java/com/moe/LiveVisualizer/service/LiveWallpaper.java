package com.moe.LiveVisualizer.service;
import android.content.*;
import android.graphics.*;
import android.view.*;
import com.moe.LiveVisualizer.internal.*;
import java.io.*;
import java.util.*;

import android.media.audiofx.Visualizer;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.moe.LiveVisualizer.utils.ColorList;

public class LiveWallpaper extends WallpaperService
{
	private ColorList colorList;
	private ChangedReceiver changed;
	private SharedPreferences moe;
	private ImageThread background,centerImage,background_h;


	@Override
	public void onCreate()
	{
		super.onCreate();
		moe = getSharedPreferences("moe", 0);
		colorList = new ColorList();
		final IntentFilter filter=new IntentFilter();
		filter.addAction("wallpaper_changed");
		filter.addAction("circle_changed");
		filter.addAction("color_changed");
		registerReceiver(changed = new ChangedReceiver(), filter);
		loadColor();
		background = new ImageThread(this, new File(getExternalFilesDir(null), "wallpaper"));
		background.start();
		centerImage = new ImageThread(this, new File(getExternalFilesDir(null), "circle"));
		centerImage.start();
		background_h=new ImageThread(this,new File(getExternalFilesDir(null),"wallpaper_p"));
		background_h.start();
	}

	public SharedPreferences getSharedPreferences()
	{
		return moe;
	}


	public Bitmap getWallpaperBitmap(int direction)
	{
		return direction==0?(background != null ?background.getImage(): null):(background!=null&&background.isGif()?background.getImage():background_h!=null?background_h.getImage():null);
	}
	public Bitmap getCenterCircleImage()
	{
		return centerImage != null ?centerImage.getImage(): null;
	}
	public ColorList getColorList()
	{
		return colorList;
	}
	@Override
	public WallpaperService.Engine onCreateEngine()
	{
		return new WallpaperEngine(this);

	}
	
	private synchronized void loadColor()
	{
		new Thread("init_thread"){
			public void run()
			{
				final File color=new File(getExternalFilesDir(null), "color");
				if ( !(color.exists() && color.isFile()) )return;
				BufferedReader read=null;
				try
				{
					read = new BufferedReader(new InputStreamReader(new FileInputStream(color)));
					String line;
					colorList.clear();
					while ( (line = read.readLine()) != null )
					{
						try
						{
							colorList.add(Integer.parseInt(line));
						}
						catch (NumberFormatException e)
						{}
					}
				}
				catch (IOException i)
				{}
				finally
				{
					try
					{
						if ( read != null )read.close();
					}
					catch (IOException e)
					{}

				}
				sendBroadcast(new Intent("color"));
			}
		}.start();
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if ( changed != null )
			unregisterReceiver(changed);
	}

	class ChangedReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context p1, final Intent p2)
		{
			switch ( p2.getAction() )
			{
				case "wallpaper_changed":
					if ( background != null )
						background.loadImage();
					if(background_h!=null)
						background_h.loadImage();
					break;
				case "color_changed":
					loadColor();
					break;
				case "circle_changed":
					if ( centerImage != null )
						centerImage.loadImage();
					break;
			}

		}


	}

	public class WallpaperEngine extends WallpaperService.Engine
	{
		private int direction,width,height;
		private Color colorReceiver;
		private WallpaperThread refresh=null;
		private List<OnColorSizeChangedListener> sizeListener;
		private VisualizerThread mVisualizer;
		private LiveWallpaper live;
		public WallpaperEngine(LiveWallpaper live)
		{
			this.live = live;
		}
		public int getCaptureSize()
		{
			return 1024;
		}
		public int getFftSize()
		{
			return getCaptureSize() / 4;
		}
		public String getError()
		{
			if ( mVisualizer != null )
				return mVisualizer.getMessage();
			return null;
		}
		public boolean isReady()
		{
			if ( mVisualizer != null )
				return mVisualizer.isInit();
			return false;
		}
		public void registerColorSizeChangedListener(OnColorSizeChangedListener l)
		{
			sizeListener.add(l);
		}

		public Bitmap getCircleImage()
		{
			return live.getCenterCircleImage();
		}


		public ColorList getColorList()
		{
			return live.getColorList();
		}
		public Context getContext()
		{
			return live;
		}
		public Bitmap getWallpaper()
		{
			return live.getWallpaperBitmap(direction);
		}
		
		public Visualizer getVisualizer()
		{
			return mVisualizer != null ?mVisualizer.getVisualizer(): null;
		}
		@Override
		public void onCreate(SurfaceHolder surfaceHolder)
		{
			super.onCreate(surfaceHolder);
			IntentFilter filter=new IntentFilter();
			filter.addAction("color");
			registerReceiver(colorReceiver = new Color(), filter);
			sizeListener = new ArrayList<>();
			refresh = new WallpaperThread(this);
			refresh.setName("wllpaper_daemon");
			refresh.setDaemon(true);
			refresh.start();
			if ( colorList != null )
				notifyColorsChanged();
			mVisualizer = new VisualizerThread(this);
			mVisualizer.setName("visualizerThread");
			mVisualizer.setDaemon(true);
			mVisualizer.start();
		}

		public SharedPreferences getSharedPreferences()
		{
			return live.getSharedPreferences();
		}
		@Override
		public void onVisibilityChanged(boolean visible)
		{
			mVisualizer.check();
			if ( background != null )
				background.notiftVisiableChanged(visible);
			if ( centerImage != null )
				centerImage.notiftVisiableChanged(visible);
			//super.onVisibilityChanged(visible);
		}
		@Override
		public void onDestroy()
		{
			super.onDestroy();
			if ( mVisualizer != null )
				mVisualizer.release();
			if ( refresh != null )refresh.close();
			if ( sizeListener != null )
				sizeListener.clear();
			mVisualizer = null;
			refresh = null;
			sizeListener = null;
			if(colorReceiver!=null)
				unregisterReceiver(colorReceiver);
		}
		public int getDisplayWidth()
		{
			return width;
		}
		public int getDisplayHeight()
		{
			return height;
		}

		public void notifyColorsChanged()
		{
			if ( sizeListener != null )
				for ( OnColorSizeChangedListener l:sizeListener )
					l.onColorSizeChanged();
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			direction=width<height?0:1;
			this.width=width;
			this.height=height;
			if(refresh!=null)
				refresh.onSizeChanged();
		}
		public int getDirection(){
			return direction;
		}
		@Override
		public void onSurfaceCreated(SurfaceHolder holder)
		{
			holder.setType(moe.getBoolean("gpu",false)?holder.SURFACE_TYPE_PUSH_BUFFERS:holder.SURFACE_TYPE_GPU);
			super.onSurfaceCreated(holder);
		}
		class Color extends BroadcastReceiver
		{

			@Override
			public void onReceive(Context p1, Intent p2)
			{
				notifyColorsChanged();
			}


		}
	}
}
