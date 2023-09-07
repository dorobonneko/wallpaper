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
import android.app.WallpaperManager;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.media.MediaPlayer;
import android.content.SharedPreferences;
import com.moe.LiveVisualizer.utils.PreferencesUtils;
import android.content.pm.PackageManager;
import com.moe.LiveVisualizer.activity.CrashActivity;
import android.os.Build;
import android.util.Log;
import com.moe.LiveVisualizer.draw.circle.RingDraw;
public class LiveWallpaper extends WallpaperService implements Thread.UncaughtExceptionHandler {
	private ColorList colorList;
	private ChangedReceiver changed;
	//private SharedPreferences moe;
	private ImageThread background,centerImage,background_h;
	private VideoThread video;
	private File videoFile;
	@Override
	public WallpaperService.Engine onCreateEngine() {
		return new WallpaperEngine();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(this);
		colorList = new ColorList();
		final IntentFilter filter=new IntentFilter();
		filter.addAction("wallpaper_changed");
		filter.addAction("circle_changed");
		filter.addAction("color_changed");
        filter.setPriority(1000);
		registerReceiver(changed = new ChangedReceiver(), filter);
		loadColor();
		videoFile = new File(getExternalFilesDir(null), "video");
		if (videoFile.exists()) {
			try {
				video = new VideoThread(videoFile.getAbsolutePath());
				video.start();
			} catch (Exception e) {}

		} else {
			background = new ImageThread(this, new File(getExternalFilesDir(null), "wallpaper"));
			background.start();
			background_h = new ImageThread(this, new File(getExternalFilesDir(null), "wallpaper_p"));
			background_h.start();
		}
		centerImage = new ImageThread(this, new File(getExternalFilesDir(null), "circle"));
		centerImage.start();


	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}

	@Override
	public void uncaughtException(final Thread p1, final Throwable p2) {
		new Thread(){
			public void run() {
				if (p2 == null || p2.getMessage() == null)return;
				StringBuffer sb=new StringBuffer(p2.getMessage());
				try {
					sb.append("\n").append(getPackageManager().getPackageInfo(getPackageName(), 0).versionName).append("\n").append(Build.MODEL).append(" ").append(Build.VERSION.RELEASE).append("\n");
				} catch (PackageManager.NameNotFoundException e) {}
				for (StackTraceElement element:p2.getStackTrace())
					sb.append("\n").append(element.toString());
				Intent intent=new Intent(getApplicationContext(), CrashActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
				startActivity(intent);
			}
		}.start();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {}
		stopSelf();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	public ImageThread getWallpaper(int direction) {
		return direction == 0 ?background: background != null && background.isGif() ?background: background_h;
	}
	public ImageThread getCenterCircleImage() {
		return centerImage;
	}
	public ColorList getColorList() {
		if (colorList == null)
			colorList = new ColorList();
		return colorList;
	}
	private synchronized void loadColor() {
		new Thread("init_thread"){
			public void run() {
				final File color=new File(getExternalFilesDir(null), "color");
				if (!(color.exists() && color.isFile()))return;
				BufferedReader read=null;
				try {
					read = new BufferedReader(new InputStreamReader(new FileInputStream(color)));
					String line;
					colorList.clear();
					while ((line = read.readLine()) != null) {
						try {
							colorList.add(Integer.parseInt(line));
						} catch (NumberFormatException e) {}
					}
				} catch (IOException i) {} finally {
					try {
						if (read != null)read.close();
					} catch (IOException e) {}

				}
				sendBroadcast(new Intent("color"));
			}
		}.start();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (changed != null)
			unregisterReceiver(changed);
        if (video != null)
            video.destroy();
        if (background != null)
            background.destroy();
        if (background_h != null)
            background_h.destroy();
        if (centerImage != null)
            centerImage.destroy();
	}

	class ChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context p1, final Intent p2) {
			switch (p2.getAction()) {
				case "wallpaper_changed":
					if (!videoFile.exists()) {
						if (video != null) {video.destroy();video = null;}
						if (background == null) {
							background = new ImageThread(LiveWallpaper.this, new File(getExternalFilesDir(null), "wallpaper"));
							background.start();
                        } else {
                            background.reLoad();
                        }
						if (background_h == null) {
							background_h = new ImageThread(LiveWallpaper.this, new File(getExternalFilesDir(null), "wallpaper_p"));
							background_h.start();
						} else {
							background_h.reLoad();
						}
					} else {
						if (video != null)
							video.destroy();
                        video = new VideoThread(videoFile.getAbsolutePath());
                        video.start();
						if (background != null) {
							background.destroy();
							background = null;
                        }
						if (background_h != null) {
							background_h.destroy();
							background_h = null;
                        }
					}

					break;
				case "color_changed":
					loadColor();
					break;
				case "circle_changed":
					if (centerImage != null)
						centerImage.reLoad();
					break;
			}

		}


	}

	public class WallpaperEngine extends WallpaperService.Engine {
		private PreferencesUtils utils;
		private int direction,width,height;
		private Color colorReceiver;
		private WallpaperThread refresh=null;
		private List<OnColorSizeChangedListener> sizeListener=new ArrayList<>();
		//private VisualizerThread mVisualizer;
		private boolean destroy;
		private boolean touchEvent;
		public WallpaperEngine() {
			Log.d(toString(), "init");
			utils = new PreferencesUtils(getApplicationContext());
			refresh = new WallpaperThread(this);
			refresh.setName("wllpaper_daemon");
			refresh.setPriority(Thread.MAX_PRIORITY);
		}

		public void unRegisterOnColorSizeChanged(OnColorSizeChangedListener p0) {
			if (sizeListener != null)
				sizeListener.remove(p0);
		}

		public boolean isDestroy() {
			return destroy;
		}
		public int getCaptureSize() {
			return 512;
		}
		public int getFftSize() {
			return 255;
		}
		public void registerColorSizeChangedListener(OnColorSizeChangedListener l) {
			sizeListener.add(l);
		}

		public ImageThread getCircleImage() {
			return LiveWallpaper.this.getCenterCircleImage();
		}


		public ColorList getColorList() {
			return LiveWallpaper.this.getColorList();
		}
		public Context getContext() {
			return getApplicationContext();
		}
		public ImageThread getWallpaper() {
			return LiveWallpaper.this.getWallpaper(direction);
		}
		/*
         public Visualizer getVisualizer()
         {
         return mVisualizer != null ?mVisualizer.getVisualizer(): null;
         }*/
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			Log.d(toString(), "onCreate");
			//super.onCreate(surfaceHolder);
            IntentFilter filter=new IntentFilter();
            filter.addAction("color");
            filter.addAction("wallpaper_changed");
            filter.addAction("circle_changed");
            filter.addAction("color_changed");
            filter.setPriority(1);
			registerReceiver(colorReceiver = new Color(), filter);
			//sizeListener = new ArrayList<>();
			//if ( colorList != null )
			//	notifyColorsChanged();
			refresh.start();
		}
		@Override
		public void onTouchEvent(MotionEvent event) {
			if (!touchEvent)return;
			switch (direction) {
				case 0:
					if (background != null)
						background.onTouchEvent(event);
                    break;
				case 1:
					if (background_h != null)
						background_h.onTouchEvent(event);
					break;
			}
		}

		@Override
		public void setTouchEventsEnabled(boolean enabled) {
			touchEvent = enabled;
			if (background != null)
				background.setRipple(enabled);
			if (background_h != null)
				background_h.setRipple(enabled);
			super.setTouchEventsEnabled(enabled);
		}

		public PreferencesUtils getPreference() {
			return utils;
		}
		@Override
		public void onVisibilityChanged(boolean visible) {
			System.gc();
			Log.d(toString(), "visiable");
			//mVisualizer.check();
			switch (direction) {
				case 0:
                    if (background != null)
                        background.notifyVisiableChanged(visible);
                    break;
                case 1:
                    if (background_h != null)
                        background_h.notifyVisiableChanged(visible);
                    break;
            }
			if (centerImage != null)
				centerImage.notifyVisiableChanged(visible);
			/*if(video!=null)
             video.notifyVisiableChanged(visible);*/
			if (refresh != null)
				refresh.notifyVisiableChanged(visible);
			//super.onVisibilityChanged(visible);
		}
		@Override
		public void onDestroy() {
			Log.d(toString(), "destroy");
			if (refresh != null)refresh.destroy();
			if (sizeListener != null)
				sizeListener.clear();
			refresh = null;
			sizeListener = null;
			if (colorReceiver != null)
				unregisterReceiver(colorReceiver);
			if (utils != null)
				utils.close();
            destroy = true;
		}
		public int getDisplayWidth() {
			return width;
		}
		public int getDisplayHeight() {
			return height;
		}
        public void notifyPropertyChanged() {
            if (refresh != null)
                refresh.notifyPropertyChanged();
        }
		public void notifyColorsChanged() {
			if (sizeListener != null)
				for (OnColorSizeChangedListener l:sizeListener)
					l.onColorSizeChanged();
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			Log.d(toString(), "surfacechanged");
			direction = width < height ?0: 1;
			this.width = width;
			this.height = height;
			if (refresh != null)
				refresh.onSizeChanged();
			if (video != null)
				video.onSizeChanged();
		}
		public int getDirection() {
			return direction;
		}
		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			Log.d(toString(), "surfaceCreated");
			holder.setType(getPreference().getBoolean("gpu", false) ?holder.SURFACE_TYPE_PUSH_BUFFERS: holder.SURFACE_TYPE_GPU);
			super.onSurfaceCreated(holder);

		}
		class Color extends BroadcastReceiver {

			@Override
			public void onReceive(Context p1, Intent p2) {
                if ("color".equals(p2.getAction()))
                    notifyColorsChanged();
                else
                    notifyPropertyChanged();
			}


		}
	}
}
