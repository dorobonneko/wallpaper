package com.moe.LiveVisualizer.internal;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.media.audiofx.Visualizer;
import android.os.HandlerThread;
import android.os.Handler;
import android.os.Message;

public class FftThread extends HandlerThread
{
	private Object lock=new Object();
	private LiveWallpaper.WallpaperEngine engine;
	private byte[] fft,wave;
	private VisualizerThread visualizer;
	public FftThread(LiveWallpaper.WallpaperEngine engine){
		super(FftThread.class.getSimpleName());
		this.engine=engine;
		fft=new byte[engine.getCaptureSize()];
		wave=new byte[engine.getCaptureSize()];
		//wave=new byte[engine.getCaptureSize()];
		visualizer=new VisualizerThread(engine);
		visualizer.start();
	}
	
	public void notifyVisibleChanged(boolean visiable){
		
		synchronized(lock){
			if(visualizer!=null)visualizer.check();
			if(visiable)
				lock.notify();
		}
	}
	public byte[] getFft(){
		return fft;
	}
	public byte[] getWave(){
		return wave;
	}

	

	private void fft(byte[] wave){
		for (int n = 0; n < wave.length-1;n++)
		{    
			//第k个点频率 getSamplingRate() * k /(getCaptureSize()/2)  
			//fft[n - 1] = (byte) ((int)Math.hypot(wave[k] == -1 ?0: wave[k], wave[k + 1] == -1 ?0: wave[k + 1]) & 0x7f);   
			wave[n]=(byte)Math.hypot(wave[n],wave[n+1]);
		}
	
	}
	@Override
	public void run()
	{
		while(!engine.isDestroy()){
			synchronized(lock){
				try
				{
					if (!engine.isVisible())
						lock.wait();
				}
				catch (InterruptedException e)
				{}
				}
				long oldTime=System.currentTimeMillis();
				try{
				if(visualizer!=null&&visualizer.isInit()){
					visualizer.getVisualizer().getFft(wave);
				fft(wave);
				fft(wave);
				fft(wave);
				fft(wave);
				fft(wave);
				System.arraycopy(wave,0,fft,0,wave.length);
				}
				}catch(Exception e){}
				try
				{
					long duration=System.currentTimeMillis()-oldTime;
					Thread.sleep(duration>16?0:16-duration);
				}
				catch (InterruptedException e)
				{}
			//}
		}
	}

	@Override
	public void destroy()
	{
		if(visualizer!=null)
			visualizer.destroy();
			fft=null;
			wave=null;
	}
	
}
