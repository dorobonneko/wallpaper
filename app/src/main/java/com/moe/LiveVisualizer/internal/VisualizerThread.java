package com.moe.LiveVisualizer.internal;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.os.HandlerThread;

public class VisualizerThread extends HandlerThread
{
	private Handler handler;
	private Visualizer mVisualizer;
	private LiveWallpaper.WallpaperEngine engine;
	private Object locked=new Object();
	private String error_msg;
	public VisualizerThread(LiveWallpaper.WallpaperEngine engine)
	{
		super("visualizer");
		this.engine = engine;
	}
	public Visualizer getVisualizer()
	{
		return mVisualizer;
	}

	@Override
	public void destroy()
	{
		if(handler!=null)
		handler.sendEmptyMessage(2);
		quit();
	}
	

	@Override
	protected void onLooperPrepared()
	{
		// TODO: Implement this method
		super.onLooperPrepared();
		handler = new Handler(){
			public void handleMessage(Message msg)
			{
				synchronized(locked){
				switch ( msg.what )
				{
					case 0:
						if(error_msg!=null)break;
							if ( mVisualizer != null )break;
							try
							{
								mVisualizer = new Visualizer(0);
								if(!mVisualizer.getEnabled()){
									mVisualizer.setCaptureSize(engine.getCaptureSize());
									//mVisualizer.setDataCaptureListener(engine, mVisualizer.getMaxCaptureRate()/2, false, true);
									mVisualizer.setEnabled(engine.isVisible());
								}
							}
							catch (Exception e)
							{
								mVisualizer=null;
								Handler handler=new Handler(Looper.getMainLooper());
								handler.post(new Runnable(){

										@Override
										public void run()
										{
											try
											{
												mVisualizer = new Visualizer(0);
												if(!mVisualizer.getEnabled()){
													mVisualizer.setCaptureSize(engine.getCaptureSize());
													//mVisualizer.setDataCaptureListener(engine, mVisualizer.getMaxCaptureRate()/2, false, true);
													VisualizerThread.this.handler.obtainMessage(3).sendToTarget();
												}
												//mVisualizer.setEnabled(engine.isVisible());
											}
											catch (Exception e)
											{
												mVisualizer=null;
												error_msg=e.getMessage();
											}
										}
									});
									
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
									sendEmptyMessage(0);
									//msg.obj=0;
									//check();
								}
							}
							catch (Exception e)
							{
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
						//handler.getLooper().quit();
						break;
					case 3:
						try
						{
							if(mVisualizer!=null)
							mVisualizer.setEnabled(engine.isVisible());
						}
						catch (Exception e)
						{
							//error_msg=e.getMessage();
						}
						break;
				}
				}
			}
		};
		handler.obtainMessage(0).sendToTarget();
	}
	
	public synchronized void check()
	{
		if ( handler != null )
		{
			//if(mVisualizer==null)
			//	handler.obtainMessage(0).sendToTarget();
		//	else
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
