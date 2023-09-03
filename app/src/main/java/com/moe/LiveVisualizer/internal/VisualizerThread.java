package com.moe.LiveVisualizer.internal;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.os.HandlerThread;

public class VisualizerThread extends HandlerThread {
	private Handler handler;
	private Visualizer mVisualizer;
	private LiveWallpaper.WallpaperEngine engine;
	private Object locked=new Object();
	private String error_msg;
	public VisualizerThread(LiveWallpaper.WallpaperEngine engine) {
		super("visualizer");
		this.engine = engine;
	}
	public Visualizer getVisualizer() {
		return mVisualizer;
	}

	public void destroy() {
		if (handler != null)
            handler.sendEmptyMessage(2);
		quit();
	}


	@Override
	protected void onLooperPrepared() {
		// TODO: Implement this method
		super.onLooperPrepared();
		handler = new Handler(){
			public void handleMessage(Message msg) {
				synchronized (locked) {
                    switch (msg.what) {
                        case 0:

                            break;
                        case 1:
                            if (mVisualizer != null) {
                                try {
                                    if (mVisualizer.setEnabled(msg.obj) != Visualizer.SUCCESS) {
                                        mVisualizer.release();
                                        mVisualizer = null;
                                        if((Boolean)msg.obj)
                                            init(msg.obj);
                                        //msg.obj=0;
                                        //check();
                                    }
                                } catch (Exception e) {
                                    //error_msg=e.getMessage();
                                }
                            } else if((Boolean)msg.obj){
                                init(msg.obj);
                            }

                            break;
                        case 2:
                            if (mVisualizer != null) {
                                mVisualizer.setEnabled(false);
                                mVisualizer.release();
                            }
                            //handler.getLooper().quit();
                            break;
                        case 3:
                            try {
                                if (mVisualizer != null) {
                                    mVisualizer.setEnabled(msg.obj);
                                    //if(call!=null)call.onReady(mVisualizer);
                                }
                            } catch (Exception e) {
                                //error_msg=e.getMessage();
                            }
                            break;
                    }
				}
			}
		};
	}
	private void init(final boolean enable) {
        if (error_msg != null)return;
        if (mVisualizer != null)return;
        try {
            mVisualizer = new Visualizer(0);
            if (!mVisualizer.getEnabled()) {
                mVisualizer.setCaptureSize(engine.getCaptureSize());
                mVisualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
                //mVisualizer.setDataCaptureListener(engine, mVisualizer.getMaxCaptureRate()/2, false, true);
                VisualizerThread.this.handler.obtainMessage(3).sendToTarget();
            }
        } catch (Exception e) {
            mVisualizer = null;
            Handler handler=new Handler(Looper.getMainLooper());
            handler.post(new Runnable(){

                    @Override
                    public void run() {
                        try {
                            mVisualizer = new Visualizer(0);
                            if (!mVisualizer.getEnabled()) {
                                mVisualizer.setCaptureSize(engine.getCaptureSize());
                                mVisualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
                                //mVisualizer.setDataCaptureListener(engine, mVisualizer.getMaxCaptureRate()/2, false, true);
                                VisualizerThread.this.handler.obtainMessage(3, enable).sendToTarget();
                            }
                            //mVisualizer.setEnabled(engine.isVisible());
                        } catch (Exception e) {
                            mVisualizer = null;
                            error_msg = e.getMessage();
                        }
                    }
                });

        }
    }
	public synchronized void check(boolean enable) {
		if (handler != null) {
			//if(mVisualizer==null)
			//	handler.obtainMessage(0).sendToTarget();
            //	else
            handler.obtainMessage(1, enable).sendToTarget();
		}

	}
	public String getMessage() {
		return error_msg;
	}
	public boolean isInit() {
		return mVisualizer != null;
	}
}
