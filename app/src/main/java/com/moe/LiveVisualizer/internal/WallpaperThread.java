package com.moe.LiveVisualizer.internal;
import android.graphics.*;

import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import com.moe.LiveVisualizer.draw.circle.RingDraw;
import com.moe.LiveVisualizer.inter.Draw;
import com.moe.LiveVisualizer.duang.Engine;
import java.lang.reflect.Field;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import com.moe.LiveVisualizer.utils.PreferencesUtils;
import android.media.AudioManager;
import android.content.Context;
import android.os.HandlerThread;

public class WallpaperThread extends HandlerThread implements Handler.Callback,MusicListener.Callback {
	private LiveWallpaper.WallpaperEngine engine;
	private ImageDraw imageDraw;
	private Paint paint=new Paint();
	private int fpsDelay=33;
	private Matrix wallpaperMatrix;//缩放壁纸用
	private Engine mDuangEngine;//屏幕特效引擎
	private ContentObserver observer;
	private boolean rotateX,rotateY,visible=true,active;
	private Camera camera;
	private Matrix matrix=new Matrix();
    private AudioManager mAudioManager;
    private MusicListener mMusicListener;
    private Handler mHandler;
    private DrawThread mDrawThread;
	public WallpaperThread(final LiveWallpaper.WallpaperEngine engine) {
        super("WallpaperThread");
		camera = new Camera();
        mAudioManager = (AudioManager) engine.getContext().getSystemService(Context.AUDIO_SERVICE);
		this.engine = engine;
		imageDraw = new ImageDraw(this);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, engine.getContext().getResources().getDisplayMetrics()));
		paint.setColor(0xff000000);
		//engine.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		fpsDelay = engine.getPreference().getBoolean("highfps", false) ?16: 33;
		rotateX = engine.getPreference().getBoolean("rotateX", false);
		rotateY = engine.getPreference().getBoolean("rotateY", false);
		setMatrix();
		engine.setTouchEventsEnabled(engine.getPreference().getBoolean("ripple", false));
		imageDraw.setAnitialias(engine.getPreference().getBoolean("antialias", false));
		imageDraw.setDownSpeed(engine.getPreference().getInt("downspeed", 15));
		if (engine.getPreference().getBoolean("scaleImage", true))
			wallpaperMatrix = new Matrix();
		if (imageDraw != null)
			imageDraw.setCenterScale(wallpaperMatrix != null);
		if (engine.getPreference().getBoolean("duang", false)) {
			mDuangEngine = Engine.init(engine);
		}
		if (imageDraw != null)
			imageDraw.setCutImage(engine.getPreference().getBoolean("cutImage", true));
		if (imageDraw != null)
			imageDraw.setVisualizerRotation(engine.getPreference().getBoolean("visualizerRotation", false));
		engine.getContext().getContentResolver().registerContentObserver(Uri.parse(com.moe.LiveVisualizer.service.SharedPreferences.URI), true, observer = new ContentObserver(new Handler()){
																			 public void onChange(boolean change, Uri uri) {
																				 onChanged(uri.getQueryParameter("key"), uri);
																			 }
																		 });

        mMusicListener = new MusicListener(engine, this);
        mMusicListener.start();
        active = mMusicListener.isMusicActive();
        visible=engine.isVisible();
	}

    @Override
    public void onMusicActived(boolean active) {
        this.active = active;
        if (visible && mHandler != null)
            mHandler.sendEmptyMessage(2);
    }


	public byte[] getWave() {
        return null;
	}
	public double[] getFft() {
		if (mMusicListener != null)
			return mMusicListener.getFft();
        return null;
	}
	public LiveWallpaper.WallpaperEngine getEngine() {
		return engine;
	}

	public boolean isRotate() {
		return rotateX;
	}


	public void notifyVisiableChanged(boolean visible) {
        this.visible = visible;
        if (mHandler != null)
            mHandler.sendEmptyMessage(visible ?0: 1);
        if (mMusicListener != null)
            mMusicListener.onVisibleChanged(visible);
	}
//显示尺寸改变
	public void onSizeChanged() {
		setMatrix();
		if (imageDraw != null)
			imageDraw.notifySizeChanged();
		if (imageDraw != null) {
			imageDraw.setDrawHeight(engine.getDisplayHeight() - PreferencesUtils.getInt(engine.getContext(), PreferencesUtils.getUriBuilder().appendQueryParameter("key", "height").build(), 10) / 100f * engine.getDisplayHeight());
		}if (mDuangEngine != null)
			mDuangEngine.changed();
	}
	private void onChanged(String key, Uri uri) {
		switch (key) {
            case "num":
                if (imageDraw != null)
                    imageDraw.setNum(PreferencesUtils.getInt(null, uri, 50));
                break;
			case "highfps"://false
				fpsDelay = PreferencesUtils.getBoolean(null, uri, false) ?16: 33;
				break;
			case "downspeed"://50
				if (imageDraw != null)imageDraw.setDownSpeed(PreferencesUtils.getInt(null, uri, 15));
				break;
			case "borderWidth"://30px
				if (imageDraw != null)imageDraw.setBorderWidth(PreferencesUtils.getInt(null, uri, 30));
				break;
			case "borderHeight"://100dp
				if (imageDraw != null)
					imageDraw.setBorderHeight(PreferencesUtils.getInt(null, uri, 100));
				break;
			case "spaceWidth"://20px
				if (imageDraw != null)
					imageDraw.setSpaceWidth(PreferencesUtils.getInt(null, uri, 20));
				break;
			case "height"://10%
				if (imageDraw != null)
					imageDraw.setDrawHeight(engine.getDisplayHeight() - PreferencesUtils.getInt(null, uri, 10) / 100f * engine.getDisplayHeight());
				break;
			case "round"://圆角
				if (imageDraw != null)
					imageDraw.setRound(PreferencesUtils.getBoolean(null, uri, true));
				break;
			case "scaleImage":
				if (PreferencesUtils.getBoolean(null, uri, true))
					wallpaperMatrix = new Matrix();
				else
					wallpaperMatrix = null;
				if (imageDraw != null)
					imageDraw.setCenterScale(wallpaperMatrix != null);
				break;
			case "cutImage":
				if (imageDraw != null)
					imageDraw.setCutImage(PreferencesUtils.getBoolean(null, uri, true));
				break;
			case "offsetX":
				if (imageDraw != null)
					imageDraw.setOffsetX(PreferencesUtils.getInt(null, uri, Math.min(engine.getDisplayWidth(), engine.getDisplayHeight()) / 2));
				break;
			case "offsetY":
				if (imageDraw != null)
					imageDraw.setOffsetY(PreferencesUtils.getInt(null, uri, Math.max(engine.getDisplayHeight(), engine.getDisplayWidth()) / 2));
				break;
			case "degress":
				if (imageDraw != null)
					imageDraw.setDegressStep(PreferencesUtils.getInt(null, uri, 10) / 100f * 10);
				break;
			case "circleRadius":
				if (imageDraw != null)
					imageDraw.setCircleRadius(PreferencesUtils.getInt(null, uri, Math.min(engine.getDisplayWidth(), engine.getDisplayHeight()) / 6));
				break;
			case "direction":
				if (imageDraw != null)
					imageDraw.setDirection(PreferencesUtils.getInt(null, uri, RingDraw.OUTSIDE));
				break;
			case "gpu":
				SurfaceHolder holder=engine.getSurfaceHolder();
				if (holder != null)
					holder.setType(PreferencesUtils.getBoolean(null, uri, false) ?holder.SURFACE_TYPE_PUSH_BUFFERS: holder.SURFACE_TYPE_GPU);
				break;
			case "color_direction":
				if (imageDraw != null)
					imageDraw.resetShader();
				break;
			case "duang":
				if (PreferencesUtils.getBoolean(null, uri, false)) {
					mDuangEngine = Engine.init(engine);
				} else if (mDuangEngine != null) {
					mDuangEngine.reset();
					mDuangEngine = null;
				}
				break;
			case "duang_size":
				if (mDuangEngine != null)
					mDuangEngine.setSizeChanged(PreferencesUtils.getInt(null, uri, 50));
				break;
			case "duang_minSize":
				if (mDuangEngine != null)
					mDuangEngine.setMinSize(PreferencesUtils.getInt(null, uri, 10));
				break;
			case "duang_maxSize":
				if (mDuangEngine != null)
					mDuangEngine.setMaxSize(PreferencesUtils.getInt(null, uri, 50));
				break;
			case "duang_speed":
				if (mDuangEngine != null)
					mDuangEngine.setMaxSpeed(PreferencesUtils.getInt(null, uri, 30));
				break;
			case "duang_wind":
				if (mDuangEngine != null)
					mDuangEngine.setWind(PreferencesUtils.getInt(null, uri, 2));
				break;
			case "duang_screen":
				if (mDuangEngine != null)
					mDuangEngine.setDuang(PreferencesUtils.getInt(null, uri, 0));
				break;
			case "visualizer_mode":
				if (imageDraw != null)
					imageDraw.setMode(PreferencesUtils.getString(null, uri));
				break;
			case "color_mode":
				if (imageDraw != null)
					imageDraw.setColorMode(PreferencesUtils.getString(null, uri));
				break;
			case "rotateX":
				rotateX = PreferencesUtils.getBoolean(null, uri, false);
				setMatrix();
				break;
			case "rotateY":
				rotateY = PreferencesUtils.getBoolean(null, uri, false);
				setMatrix();
				break;
			case "ripple":
				engine.setTouchEventsEnabled(PreferencesUtils.getBoolean(null, uri, false));
				break;
			case "antialias":
				if (imageDraw != null)
					imageDraw.setAnitialias(PreferencesUtils.getBoolean(null, uri, false));
				break;
			case "visualizerRotation":
				if (imageDraw != null)
					imageDraw.setVisualizerRotation(PreferencesUtils.getBoolean(null, uri, false));
				break;
		}
	}
	private void setMatrix() {
		//matrix.reset();
		//camera.restore();
		camera.save();
		if (rotateX)
            camera.rotateX(180);
		if (rotateY)
            camera.rotateY(180);
		camera.getMatrix(matrix);
		camera.restore();
		matrix.preTranslate(rotateY ?-engine.getDisplayWidth() / 2f: 0, rotateX ?-engine.getDisplayHeight() / 2f: 0);
		matrix.postTranslate(rotateY ?engine.getDisplayWidth() / 2f: 0, rotateX ?engine.getDisplayHeight() / 2f: 0);
	}

	public void destroy() {
		if (mMusicListener != null)
			mMusicListener.destroy();
		imageDraw = null;
		//engine.getPreference().unregisterOnSharedPreferenceChangeListener(this);
		if (mDuangEngine != null)
			mDuangEngine.reset();
		if (observer != null)
			engine.getContext().getContentResolver().unregisterContentObserver(observer);
		engine = null;

	}

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler(this);
        if (visible)
            mHandler.sendEmptyMessage(0);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                if (mDrawThread != null)
                    break;
                mDrawThread = new DrawThread(active);
                mDrawThread.start();
                break;
            case 1:
                if (mDrawThread != null)
                    mDrawThread.interrupt();
                mDrawThread = null;
                break;
            case 2:
                mDrawThread.pause(active);
                break;
        }
        return true;
    }

    private void drawWallpager(Canvas canvas) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        //canvas.drawColor(0xff000000);//先涂黑
        ImageThread wallpaper=engine.getWallpaper();//获取背景
        if (wallpaper != null) {
            if (wallpaper.getImageData() != null) {
                canvas.drawBitmap(wallpaper.getImageData(), 0, wallpaper.getWidth(), 0, 0, wallpaper.getWidth(), wallpaper.getHeight(), true, null);
            } else if (wallpaper.getImage() != null) {
                final Bitmap bitmap=wallpaper.getImage();
                if (wallpaperMatrix != null) {
                    float scale=Math.min((float)canvas.getWidth() / bitmap.getWidth(), (float)canvas.getHeight() / bitmap.getHeight());
                    wallpaperMatrix.setScale(scale, scale);
                    wallpaperMatrix.postTranslate((canvas.getWidth() - bitmap.getWidth() * scale) / 2, (canvas.getHeight() - bitmap.getHeight() * scale) / 2);
                    canvas.drawBitmap(bitmap, wallpaperMatrix, null);

                } else {
                    canvas.drawBitmap(bitmap, (canvas.getWidth() - bitmap.getWidth()) / 2f, (canvas.getHeight() - bitmap.getHeight()) / 2f, null);
                }
                //bitmap.recycle();
            }
        }
    }
    class DrawThread extends Thread {
        private boolean active,cancel;
        private Object lock=new Object();
        private long oldTime;
        public DrawThread(boolean active) {
            this.active = active;
        }
        public void pause(boolean active) {
            this.active = active;
            synchronized (lock) {
                if (active)
                    lock.notify();
            }
        }

        @Override
        public void interrupt() {
            this.cancel=true;
            super.interrupt();
            synchronized (lock) {
                lock.notify();
            }
        }
        @Override
        public void run() {
            while (!cancel) {
                long delay=0;
                synchronized (lock) {
                    boolean  active=this.active;
                    LiveWallpaper.WallpaperEngine engine=WallpaperThread.this.engine;
                    if (engine == null) {
                        break;
                    }
                    SurfaceHolder sh=engine.getSurfaceHolder();
                    if (sh == null) {
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {}
                        continue;
                    }
                    final Canvas canvas=sh.lockCanvas();
                    if (canvas == null) {
                        engine.onSurfaceRedrawNeeded(sh);
                        continue;
                    }

                    //绘制背景
                    drawWallpager(canvas);
                    if (imageDraw != null && active) {

                        Draw draw=imageDraw.lockData();
                        if (draw != null) {
                            synchronized (draw) {
                                /*if (rotateX)
                                 {
                                 canvas.save();
                                 canvas.rotate(180, canvas.getWidth() / 2, canvas.getHeight() / 2);
                                 }*/
                                int save=canvas.save();
                                //setMatrix();
                                /*if(rotateX)
                                 matrix.postTranslate(0,canvas.getHeight());
                                 if(rotateY)
                                 matrix.postTranslate(canvas.getWidth(),0);*/
                                canvas.setMatrix(matrix);
                                try {
                                    draw.draw(canvas);
                                } catch (Exception e) {}
                                //canvas.setMatrix(null);
                                /*if (rotateX)*/
                                canvas.restoreToCount(save);
                            }
                        }
                    }
                    //特效
                    if (mDuangEngine != null && active)
                        mDuangEngine.draw(canvas);

                    try {
                        sh.unlockCanvasAndPost(canvas);
                        if (!active)
                            lock.wait();
                    } catch (Exception E) {}
                }
                long blank=(System.nanoTime() - oldTime) / 1000000;
                try {
                    long space=delay == 0 ?fpsDelay: delay;
                    sleep(active ?(blank > space ?0: (space - blank)): 1000);
                    oldTime = System.nanoTime();
                } catch (InterruptedException e) {}

            }
        }
    }
}
