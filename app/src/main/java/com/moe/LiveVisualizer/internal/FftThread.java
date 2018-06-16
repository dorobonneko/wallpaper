package com.moe.LiveVisualizer.internal;
import com.moe.LiveVisualizer.service.LiveWallpaper;

public class FftThread extends Thread
{
	private Object lock=new Object();
	private LiveWallpaper.WallpaperEngine engine;
	private byte[] fft,wave;
	private VisualizerThread visualizer;
	public FftThread(LiveWallpaper.WallpaperEngine engine){
		this.engine=engine;
		fft=new byte[engine.getFftSize()];
		wave=new byte[engine.getCaptureSize()];
		visualizer=new VisualizerThread(engine);
		visualizer.start();
	}
	public void notifyVisibleChanged(boolean visiable){
		
		synchronized(lock){
			if(visualizer!=null)visualizer.check();
			if (visiable)
			lock.notify();
		}
	}
	public byte[] getFft(){
		return fft;
	}
	public byte[] getWave(){
		try{
		if(visualizer!=null&&visualizer.isInit())
			visualizer.getVisualizer().getWaveForm(wave);
			}catch(Exception e){}
		return wave;
	}
	public boolean isReady(){
		return visualizer!=null&&visualizer.isInit();
	}
	public String getError(){
		if(isReady())
			return visualizer.getMessage();
			return "";
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
				try{
				if(visualizer!=null&&visualizer.isInit())
					visualizer.getVisualizer().getFft(wave);
				for (int n = 1; n < fft.length;n++)    
				{    
					//第k个点频率 getSamplingRate() * k /(getCaptureSize()/2)  
					int k=2 * n;
					fft[n - 1] = (byte) ((int)Math.hypot(wave[k] == -1 ?0: wave[k], wave[k + 1] == -1 ?0: wave[k + 1]) & 0x7f);   
				}
				}catch(Exception e){}
				try
				{
					Thread.sleep(66);
				}
				catch (InterruptedException e)
				{}
			//}
		}
	}

	@Override
	public void destroy()
	{
		// TODO: Implement this method
		super.destroy();
		if(visualizer!=null)
			visualizer.destroy();
	}
	
}
