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
import com.tencent.bugly.Bugly;

public class LiveWallpaper extends WallpaperService
{
	private Bitmap wallpaper,circle,bit;
	private ColorList colorList;
	private WallpaperChanged changed;
	private SharedPreferences moe;
	private WallpaperEngine engine;
	private DisplayMetrics display;
	private boolean gif;
	private GifDecoder gifDecode;

	
	public SharedPreferences getSharedPreferences()
	{
		// TODO: Implement this method
		return moe;
	}

	
	public Bitmap getWallpaperBitmap(){
		return wallpaper;
	}
	public Bitmap getCenterCircleImage()
	{
		// TODO: Implement this method
		return circle;
	}

	public boolean isGif()
	{
		// TODO: Implement this method
		return gif;
	}

	public GifDecoder getGifDecode()
	{
		// TODO: Implement this method
		return gifDecode;
	}

	public ColorList getColorList()
	{
		// TODO: Implement this method
		return colorList;
	}
	@Override
	public WallpaperService.Engine onCreateEngine()
	{
		return engine = new WallpaperEngine(this);

	}
	/*private MediaController control;
	 private MediaController.Callback callback;
	 /*private void init()
	 {
	 if ( Build.VERSION.SDK_INT > 20 )
	 {
	 MediaSessionManager m=(MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
	 m.addOnActiveSessionsChangedListener(new MediaSessionManager.OnActiveSessionsChangedListener(){

	 @Override
	 public void onActiveSessionsChanged(final List<MediaController> p1)
	 {
	 if ( control != null && callback != null )
	 control.unregisterCallback(callback);
	 if ( p1 != null && p1.size() > 0 )
	 {
	 control = p1.get(0);
	 control.registerCallback(callback = new MediaController.Callback(){
	 @Override
	 public void onMetadataChanged(MediaMetadata metadata)
	 {
	 onMetadataUpdate(metadata);
	 }
	 public void onSessionDestroyed()
	 {
	 return;
	 }

	 public void onSessionEvent(java.lang.String event, android.os.Bundle extras)
	 {
	 return;
	 }

	 public void onPlaybackStateChanged(android.media.session.PlaybackState state)
	 {
	 //if(state.getState()==state.STATE_BUFFERING&&LiveWallpaper.this.control.getMetadata()!=null)
	 // onMetadataChanged(LiveWallpaper.this.control.getMetadata());

	 return;
	 }


	 public void onQueueChanged(java.util.List<android.media.session.MediaSession.QueueItem> queue)
	 {
	 return;
	 }

	 public void onQueueTitleChanged(java.lang.CharSequence title)
	 {
	 return;
	 }

	 public void onExtrasChanged(android.os.Bundle extras)
	 {
	 return;
	 }

	 public void onAudioInfoChanged(android.media.session.MediaController.PlaybackInfo info)
	 {
	 return;
	 }

	 });
	 if ( control.getMetadata() != null )
	 onMetadataUpdate(control.getMetadata());
	 }
	 }
	 }, new ComponentName(this, NotifycationListener.class));
	 }
	 }
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();
		Bugly.init(getApplicationContext(), "39c93f2bb3", false);
		display = new DisplayMetrics();
		((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(display);
		moe = getSharedPreferences("moe", 0);
		colorList = new ColorList();
		new Thread("init_thread"){
			public void run()
			{
				loadWallpaper();
				loadColor();
				loadCircle();
			}}.start();
		final IntentFilter filter=new IntentFilter();
		filter.addAction("wallpaper_changed");
		filter.addAction("color_changed");
		filter.addAction("artwork_color");
		filter.addAction("artwork");
		filter.addAction("circle_changed");
		registerReceiver(changed = new WallpaperChanged(), filter);
		//init();
	}
	
	/*@Override
	 public void onGenerated(Palette p1)
	 {
	 if(p1==null)return;
	 List<Palette.Swatch> list=p1.getSwatches();
	 if ( list.size() > 0 && engine != null )
	 engine.setColor(list.get(list.size() / 2).getRgb());
	 //sendBroadcast(new Intent("artwork_color").putExtra("color",list.get(list.size()/2).getRgb()));

	 }


	 private void onMetadataUpdate(MediaMetadata metadate)
	 {
	 Bitmap buffer=metadate.getBitmap(metadate.METADATA_KEY_ALBUM_ART);
	 if ( buffer == null || buffer.equals(bit) )return;
	 try
	 {
	 if ( bit != null )bit.recycle();
	 }
	 catch (Exception e)
	 {}
	 bit = buffer;
	 if ( moe.getBoolean("artwork", false) && engine != null )engine.setArtwork(bit);
	 //Palette.generateAsync(bit, this);

	 /*if(moe.getBoolean("artwork",false)){
	 new Thread(){
	 public void run(){
	 FileOutputStream fos = null;
	 try
	 {
	 fos=new FileOutputStream(new File(getExternalCacheDir(), "artwork"));
	 bit.compress(Bitmap.CompressFormat.JPEG, 100, fos);
	 sendBroadcast(new Intent("artwork"));

	 }
	 catch (FileNotFoundException e)
	 {}finally{
	 try
	 {
	 if ( fos != null )fos.close();
	 }
	 catch (IOException e)
	 {}
	 }
	 }
	 }.start();
	 }
	 }*/
	private synchronized void loadCircle()
	{
		if ( this.circle != null )
		{
			this.circle.recycle();
			circle = null;
		}
		File circle_file=new File(getExternalCacheDir(), "circle");
		if ( circle_file.exists() && circle_file.isFile() )
		{
			//final Display display=((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			final Bitmap buffer = BitmapFactory.decodeFile(circle_file.getAbsolutePath());
			if ( buffer != null )
			{
				Matrix matrix = new Matrix();
				matrix.setScale(((float)display.widthPixels / 3) / buffer.getWidth(), ((float)display.widthPixels / 3) / buffer.getHeight());
				this.circle = Bitmap.createBitmap(buffer, 0, 0, buffer.getWidth(), buffer.getHeight(), matrix, true);
				if ( this.circle != buffer )
					buffer.recycle();
			}

		}
	}
	private synchronized void loadWallpaper()
	{
		gif=false;
		if ( this.wallpaper != null )
		{
			this.wallpaper.recycle();
			wallpaper = null;
		}
		if(gifDecode!=null){
			this.gifDecode.clear();
			this.gifDecode=null;
		}
		File wallpaper=new File(getExternalCacheDir(), "wallpaper");
		File gif=new File(getExternalCacheDir(), "gif");

		if ( wallpaper.exists() && wallpaper.isFile() )
		{
			if ( gif.exists() )
			{
				this.gif=true;
				if(gifDecode!=null)gifDecode.clear();
				try{
					GifDecoder decode=new GifDecoder(new GifDecoder.BitmapProvider(){

							@Override
							public Bitmap obtain(int p1, int p2, Bitmap.Config p3)
							{
								// TODO: Implement this method
								return Bitmap.createBitmap(p1,p2,p3);
							}

							@Override
							public void release(Bitmap p1)
							{
								p1.recycle();
							}
						});
					ByteArrayOutputStream baos = null;
					FileInputStream fis = null;
					try{
						fis=new FileInputStream(wallpaper);
						baos=new ByteArrayOutputStream();
						int len=0;
						byte[] buffer=new byte[512];
						while((len=fis.read(buffer))!=-1)
							baos.write(buffer,0,len);
							baos.flush();
							decode.read(baos.toByteArray());
						}catch(Exception e){}
						finally{
							try
							{
								if ( baos != null )baos.close();
							}
							catch (IOException e)
							{}
							try
							{
								if ( fis != null )fis.close();
							}
							catch (IOException e)
							{}
						}
					decode.advance();
					gifDecode=decode;
					}catch(Exception e){}
			}
			else
			{
				decodeBitmap(wallpaper);
			}
		}
	}
	private void decodeBitmap(File file)
	{
		gif=false;
		final Bitmap buffer = BitmapFactory.decodeFile(file.getAbsolutePath());
		if ( buffer != null )
		{
			Matrix matrix = new Matrix();
			matrix.setScale(((float)display.widthPixels) / buffer.getWidth(), ((float)display.heightPixels) / buffer.getHeight());
			this.wallpaper = Bitmap.createBitmap(buffer, 0, 0, buffer.getWidth(), buffer.getHeight(), matrix, true);
			if ( this.wallpaper != buffer )
				buffer.recycle();
		}
	}
	private synchronized void loadColor()
	{
		final File color=new File(getExternalCacheDir(), "color");
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
							loadWallpaper();
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
							loadCircle();
							break;
					}
				}
			}.start();

		}


	}
	
	public class WallpaperEngine extends WallpaperService.Engine implements Visualizer.OnDataCaptureListener
	{
		//private Visualizer mVisualizer;
		private WallpaperThread refresh=null;
		//private int artwork_color=0xff39c5bb;
		//private Bitmap artwork;
		private Shader shader;
		private List<OnColorSizeChangedListener> sizeListener=new ArrayList<>();
		private VisualizerThread mVisualizer;
		private LiveWallpaper live;
		public WallpaperEngine(LiveWallpaper live){
			this.live=live;
		}
		public String getError(){
			return mVisualizer.getMessage();
		}
		public boolean isReady(){
			return mVisualizer.isInit();
		}
		public void registerColorSizeChangedListener(OnColorSizeChangedListener l)
		{
			sizeListener.add(l);
		}
		public void setShader(LinearGradient linearGradient)
		{
			shader = linearGradient;
		}
		public Bitmap getCircleImage()
		{
			return live.getCenterCircleImage();
		}
		public Shader getShader()
		{
			// TODO: Implement this method
			return shader;
		}

		/*public void setArtwork(Bitmap artwork)
		 {

		 if ( this.artwork != null && !this.artwork.isRecycled() )
		 this.artwork.recycle();

		 this.artwork = artwork;
		 }

		 public Bitmap getArtwork()
		 {
		 return artwork;
		 }

		 public void setColor(int intExtra)
		 {
		 this.artwork_color = intExtra;
		 }


		 public int getColor()
		 {
		 return artwork_color;
		 }*/
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
		public boolean isGif(){
			return live.isGif();
		}
		public GifDecoder getMovie(){
			return live.getGifDecode();
		}
		public void notifyColorChanged()
		{
			shader = null;
			for ( OnColorSizeChangedListener l:sizeListener )
				l.onColorSizeChanged();
			//if ( refresh != null )refresh.notifyColorChanged();
		}
		@Override
		public void onCreate(SurfaceHolder surfaceHolder)
		{
			super.onCreate(surfaceHolder);
			refresh = new WallpaperThread(this);
			refresh.setName("wllpaper_daemon");
			refresh.setDaemon(true);
			refresh.start();
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
			super.onVisibilityChanged(visible);
		}


		@Override
		public void onWaveFormDataCapture(Visualizer p1, byte[] p2, int p3)
		{
			if ( refresh != null )
				refresh.onUpdate(p2);
			//if ( handler != null && isVisible() )handler.obtainMessage(0, p2).sendToTarget();
		}

		@Override
		public void onFftDataCapture(Visualizer p1, byte[] fft, int p3)
		{
			double[] model = new double[fft.length/ 2-1];    
			//model[0] =(byte)(fft[0]&0x7f);  
			
			for (int n = 1; n < model.length+1;n++)    
			{    
				//第k个点频率 getSamplingRate() * k /(getCaptureSize()/2)  
				int k=2*n;
				model[n-1] = Math.hypot(fft[k]==-1?0:fft[k], fft[k + 1]==-1?0:fft[k+1]);   
			}
			//model[model.length-1]=fft[1];
			if(refresh!=null)
				refresh.updateFft(model);
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
		}

	}
}
