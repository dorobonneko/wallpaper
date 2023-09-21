package com.moe.LiveVisualizer.internal;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.os.HandlerThread;
import java.util.Arrays;

public class VisualizerThread extends HandlerThread implements Handler.Callback {
	private Handler handler;
	private Visualizer mVisualizer;
	private LiveWallpaper.WallpaperEngine engine;
    private byte[] wave;
	private double[] fft,old;
	public VisualizerThread(LiveWallpaper.WallpaperEngine engine) {
		super("visualizer");
		this.engine = engine;
        fft = new double[engine.getFftSize()];
        old = new double[engine.getFftSize()];
		wave = new byte[engine.getCaptureSize()];
        initVisualizer();
        start();
	}
	public Visualizer getVisualizer() {
		return mVisualizer;
	}
    public double[] getFft() {
        if(!handler.hasMessages(0))
            handler.sendEmptyMessage(0);
        synchronized(old){
            return old;
        }
    }
    public void setEnabled(boolean enable) {
        /*synchronized (this) {
            if (mVisualizer != null) {
                if (mVisualizer.getEnabled() == enable)
                    return;
                mVisualizer.setEnabled(enable);
                if (!enable) {
                    handler.removeMessages(0);
                    fill(old);
                    
                }
            } else if (enable) {
                //初始化visualizer
                initVisualizer();
            }
        }*/
        if(!enable)
            handler.removeMessages(0);
    }
    private void initVisualizer() {
        mVisualizer = new Visualizer(0);
        if(mVisualizer.getEnabled())
           mVisualizer.setEnabled(false);
        mVisualizer.setCaptureSize(engine.getCaptureSize());
        mVisualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
        mVisualizer.setEnabled(true);
        
        
    }
    @Override
    public void start() {
        super.start();
        handler = new Handler(getLooper(), this);
    }

	public void destroy() {
        synchronized(this){
		if (mVisualizer != null) {
            if (mVisualizer.getEnabled())
                mVisualizer.setEnabled(false);
            mVisualizer.release();
        }
        }
		quit();
	}

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                synchronized (this) {
                try{
                    if (mVisualizer != null && mVisualizer.getEnabled()) {
                        long oldTime=System.currentTimeMillis();
                        mVisualizer.getFft(wave);
                        fft(wave);
                        synchronized(old){
                            System.arraycopy(fft, 0, old, 0, old.length);
                        }
                        long distance=System.currentTimeMillis()-oldTime;
                        handler.sendEmptyMessageDelayed(0,distance>100?0:100-distance);
                    }
                }catch(Exception e){
                    if(mVisualizer!=null){
                        if(mVisualizer.getEnabled())
                            mVisualizer.setEnabled(false);
                            mVisualizer.release();
                    }
                    initVisualizer();
                }
                }
                break;
        }
        return true;
    }
	private void fill(double[] b) {
        for (int i=0;i < b.length;i++)
            b[i] = 0;
    }
    private double a(double data1, double data2) {
        double d=Math.hypot(data1, data2);
        d = Math.min(127,d>64?d*0.7d:d*1.3d);
        return d;
    }
    /*private double[] fft(byte[] wave) {
        wave[0]=0;
        wave[1]=0;
        for (int i = 0,j=0; i<wave.length&&j<fft.length;i += 4,j++) {
            //this.fft[j] = Math.sqrt(Math.pow(Math.hypot(wave[i],wave[i + 1]),2)-10);
            this.fft[j] =a(a(wave[i], wave[i + 1]),a(wave[i+2],wave[i+3]));
        }
        return this.fft;
	}*/
    private double[] fft(byte[] wave){
        for(int i=0;i<fft.length;i++){
            fft[i]=Math.hypot(wave[i],wave[i+1]);
        }
        return fft;
    }
    
}
