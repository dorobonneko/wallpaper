package com.moe.LiveVisualizer;
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
	private WallpaperChanged changed;
	private SharedPreferences moe;
	private WallpaperEngine engine;
	private DisplayMetrics display;
	private ImageThread background,centerImage;
	
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		display = new DisplayMetrics();
		((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(display);
		moe = getSharedPreferences("moe", 0);
		colorList = new ColorList();
		final IntentFilter filter=new IntentFilter();
		filter.addAction("wallpaper_changed");
		filter.addAction("color_changed");
		filter.addAction("artwork_color");
		filter.addAction("artwork");
		filter.addAction("circle_changed");
		registerReceiver(changed = new WallpaperChanged(), filter);
		loadColor();
		background=new ImageThread(this,new File(getExternalFilesDir(null),"wallpaper"));
		background.start();
		centerImage=new ImageThread(this,new File(getExternalFilesDir(null),"circle"));
		centerImage.start();
	}
	
	public SharedPreferences getSharedPreferences()
	{
		return moe;
	}
	public WallpaperEngine getEngine(){
		return engine;
	}

	public Bitmap getWallpaperBitmap(){
		return background!=null?background.getImage():null;
	}
	public Bitmap getCenterCircleImage()
	{
		return centerImage!=null?centerImage.getImage():null;
	}
	public ColorList getColorList()
	{
		return colorList;
	}
	@Override
	public WallpaperService.Engine onCreateEngine()
	{
		return engine = new WallpaperEngine(this);

	}
	public DisplayMetrics getDisplay(){
		return display;
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
		if ( engine != null )
		{
			engine.notifyColorChanged();
		}
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

	class WallpaperChanged extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context p1, final Intent p2)
		{
			new Thread("broadcat_thread"){
				public void run()
				{
					switch ( p2.getAction() )
					{
						case "wallpaper_changed":
							if(background!=null)
								background.loadImage();
							break;
						case "color_changed":
							loadColor();
							break;
						case "artwork_color":
							//if ( engine != null )
							//	engine.setColor(p2.getIntExtra("color", 0xff39c5bb));
							break;
						case "artwork":
						//	if ( engine != null )
						//		engine.setArtwork(BitmapFactory.decodeFile(new File(getExternalCacheDir(), "artwork").getAbsolutePath()));
							break;
						case "circle_changed":
							if(centerImage!=null)
								centerImage.loadImage();
							break;
					}
				}
			}.start();

		}


	}
	
	public class WallpaperEngine extends WallpaperService.Engine
	{
		private WallpaperThread refresh=null;
		private List<OnColorSizeChangedListener> sizeListener;
		private VisualizerThread mVisualizer;
		private LiveWallpaper live;
		public WallpaperEngine(LiveWallpaper live){
			this.live=live;
		}
		/*public int getColor(){
			if(refresh!=null)
				return refresh.getColor();
			return 0xff39c5bb;
		}*/
		public int getCaptureSize()
		{
			return 1024;
		}
		public int getFftSize(){
			return getCaptureSize()/4;
		}
		public String getError(){
			if(mVisualizer!=null)
			return mVisualizer.getMessage();
			return null;
		}
		public boolean isReady(){
			if(mVisualizer!=null)
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
			return live.getWallpaperBitmap();
		}
		public void notifyColorChanged()
		{
			/*if(refresh!=null)
			refresh.notifyColorChanged();*/
			if(sizeListener!=null)
			for ( OnColorSizeChangedListener l:sizeListener )
				l.onColorSizeChanged();
			//if ( refresh != null )refresh.notifyColorChanged();
		}
		public Visualizer getVisualizer(){
			return mVisualizer!=null?mVisualizer.getVisualizer():null;
		}
		@Override
		public void onCreate(SurfaceHolder surfaceHolder)
		{
			super.onCreate(surfaceHolder);
			sizeListener=new ArrayList<>();
			refresh = new WallpaperThread(this);
			refresh.setName("wllpaper_daemon");
			refresh.setDaemon(true);
			refresh.start();
			if(colorList!=null)
			notifyColorChanged();
			mVisualizer=new VisualizerThread(this);
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
			if(background!=null)
				background.notiftVisiableChanged(visible);
			if(centerImage!=null)
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
			if(sizeListener!=null)
				sizeListener.clear();
				mVisualizer=null;
				refresh=null;
				sizeListener=null;
		}
		public int getWidth(){
			return display.widthPixels;
		}
		public int getHeight(){
			return display.heightPixels;
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder)
		{
			holder.setType(holder.SURFACE_TYPE_GPU);
			super.onSurfaceCreated(holder);
		}
		
	}
}
