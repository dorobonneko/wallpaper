package com.moe.LiveVisualizer.internal;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.moe.LiveVisualizer.LiveWallpaper;

public class VisualizerThread extends Thread
{
	private Handler handler;
	private Visualizer mVisualizer;
	private LiveWallpaper.WallpaperEngine engine;
	private Object locked=new Object();
	public VisualizerThread(LiveWallpaper.WallpaperEngine engine){
		this.engine=engine;
	}

	public void release()
	{
		handler.obtainMessage(2).sendToTarget();
	}

	@Override
	public void run()
	{
		Looper.prepare();
		handler=new Handler(){
			public void handleMessage(Message msg){
				switch(msg.what){
					case 0:
						synchronized(locked){
							if(mVisualizer!=null)break;
							try{
						mVisualizer = new Visualizer(0);
						mVisualizer.setEnabled(false);
						mVisualizer.setCaptureSize(mVisualizer.getCaptureSizeRange()[0]*2);
						mVisualizer.setDataCaptureListener(engine, mVisualizer.getMaxCaptureRate(), false, true);
						mVisualizer.setEnabled(engine.isVisible());
						}catch(Exception e){
							new Handler(Looper.getMainLooper()).post(new Runnable(){
								public void run(){
									Toast.makeText(engine.getContext(),"",Toast.LENGTH_SHORT).show();
									}
									});
						}
						}
						break;
					case 1:
						if(mVisualizer!=null)
						mVisualizer.setEnabled(engine.isVisible());
						break;
					case 2:
						if(mVisualizer!=null){
							mVisualizer.setEnabled(false);
							mVisualizer.release();
						}
						break;
				}
			}
		};
		Looper.loop();
	}
	public synchronized void check(){
		if(handler!=null){
		if(mVisualizer==null)
			handler.obtainMessage(0).sendToTarget();
			else
			handler.obtainMessage(1).sendToTarget();
			}
			
	}
	public boolean isInit(){
		return mVisualizer!=null;
	}
}
