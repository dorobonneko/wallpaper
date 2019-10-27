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
	private byte[] wave;
	private double[] fft;
	private VisualizerThread visualizer;
	public FftThread(LiveWallpaper.WallpaperEngine engine){
		super(FftThread.class.getSimpleName());
		this.engine=engine;
		fft=new double[engine.getCaptureSize()/4];
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
	public double[] getFft(){
		return fft;
	}
	public byte[] getWave(){
		return wave;
	}

	
/*private byte[] fft(byte[] fft){
	byte[] model = new byte[fft.length / 2 + 1];
	if(fft[0]==0)return model;
	model[0] = (byte) Math.abs(fft[1]);
	int j = 1;

	for (int i = 2; i < fft.length;i+=2) {

		model[j++] = (byte) Math.hypot(fft[i], fft[i + 1]);
	}
	return model;
}*/
	private double[] fft(byte[] fft){
		double[] model = new double[fft.length / 4 ];
		//if(fft[0]==0)return model;
		//model[0] = Math.abs(fft[1]);
		for (int i = 2,j=0; j < model.length;i+=2,j++) {
			model[j] = Math.hypot(fft[i], fft[i + 1]);
			}
		return model;
	}
	private byte[] fftHypot(byte[] wave){
		byte[] fft=new byte[wave.length];
		for (int n = 2; n < wave.length-1;n++)
		{    
			//第k个点频率 getSamplingRate() * k /(getCaptureSize()/2)  
			//fft[n - 1] = (byte) ((int)Math.hypot(wave[k] == -1 ?0: wave[k], wave[k + 1] == -1 ?0: wave[k + 1]) & 0x7f);   
			int k=n-2;
			fft[k]=(byte)Math.hypot(wave[n],wave[n+1]);
			fft[k]=(byte)(fft[k]*fft[k]*0.95d);
			if(fft[k]<0)
				fft[k]=Byte.MAX_VALUE;
		}
		return fft;
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
				fft=fft(wave);
				
				//System.arraycopy(wave,0,fft,0,wave.length);
				}
				}catch(Exception e){}
				try
				{
					long duration=System.currentTimeMillis()-oldTime;
					Thread.sleep(duration>33?0:33-duration);
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
