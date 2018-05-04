package com.moe.LiveVisualizer.internal;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.moe.LiveVisualizer.LiveWallpaper;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.BuglyLog;

public class VisualizerThread extends Thread
{
	private Handler handler;
	private Visualizer mVisualizer;
	private LiveWallpaper.WallpaperEngine engine;
	private Object locked=new Object();
	private String error_msg;
	public VisualizerThread(LiveWallpaper.WallpaperEngine engine)
	{
		this.engine = engine;
	}
	public Visualizer getVisualizer()
	{
		return mVisualizer;
	}
	public void release()
	{
		handler.obtainMessage(2).sendToTarget();
	}

	@Override
	public void run()
	{
		Looper.prepare();
		handler = new Handler(){
			public void handleMessage(Message msg)
			{
				switch ( msg.what )
				{
					case 0:
						synchronized ( locked )
						{
							if ( mVisualizer != null )break;
							try
							{
								mVisualizer = new Visualizer(0);
								mVisualizer.setEnabled(false);
								mVisualizer.setCaptureSize(engine.getCaptureSize());
								//mVisualizer.setDataCaptureListener(engine, mVisualizer.getMaxCaptureRate()/2, false, true);
								mVisualizer.setEnabled(engine.isVisible());
							}
							catch (Exception e)
							{
								error_msg = e.getMessage();
								/*new Handler(Looper.getMainLooper()).post(new Runnable(){
								 public void run(){
								 Toast.makeText(engine.getContext(),"没有录音权限",Toast.LENGTH_LONG).show();
								 }
								 });*/
							}
						}
						break;
					case 1:
						if ( mVisualizer != null )
						{
							try
							{
								if ( mVisualizer.setEnabled(engine.isVisible()) != Visualizer.SUCCESS )
								{
									mVisualizer.release();
									mVisualizer = null;
									//msg.obj=0;
									check();
								}
							}
							catch (Exception e)
							{
								BuglyLog.e(getName(), e.getMessage());
								//error_msg=e.getMessage();
							}
						}
						break;
					case 2:
						if ( mVisualizer != null )
						{
							mVisualizer.setEnabled(false);
							mVisualizer.release();
						}
						break;
					case 3:
						try
						{
							mVisualizer.setEnabled(engine.isVisible());
						}
						catch (Exception e)
						{
							BuglyLog.e(getName(), e.getMessage());
							//error_msg=e.getMessage();
						}
						break;
				}
			}
		};
		Looper.loop();
	}
	public synchronized void check()
	{
		if ( handler != null )
		{
			if ( mVisualizer == null )
			{
				try
				{
					mVisualizer = new Visualizer(0);
					mVisualizer.setEnabled(false);
					mVisualizer.setCaptureSize(engine.getCaptureSize());
					//mVisualizer.setDataCaptureListener(engine, mVisualizer.getMaxCaptureRate()/2, false, true);
					handler.obtainMessage(3).sendToTarget();
					//mVisualizer.setEnabled(engine.isVisible());
				}
				catch (Exception e)
				{
					//error_msg=e.getMessage();
					//BuglyLog.e(getName(),e.getMessage());
					/*new Handler(Looper.getMainLooper()).post(new Runnable(){
					 public void run(){
					 Toast.makeText(engine.getContext(),"没有录音权限",Toast.LENGTH_LONG).show();
					 }
					 });*/
					handler.obtainMessage(0).sendToTarget();
				}
				//handler.obtainMessage(0).sendToTarget();
			}
			else
				handler.obtainMessage(1).sendToTarget();
		}

	}
	public String getMessage()
	{
		return error_msg;
	}
	public boolean isInit()
	{
		return mVisualizer != null;
	}
}
