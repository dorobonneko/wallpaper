package com.moe.LiveVisualizer.internal;
import android.content.Context;
import android.media.AudioManager;
import android.service.wallpaper.WallpaperService;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.os.HandlerThread;
import android.os.Handler;
import android.os.Message;

public class MusicListener extends HandlerThread implements Handler.Callback{
    private AudioManager mAudioManager;
    private boolean visible=true,destroy;
    private FftThread fftThread;
    private Callback call;
    private Handler mHandler;
    private boolean flag;
    public MusicListener(LiveWallpaper.WallpaperEngine engine,Callback call) {
        super("音乐状态监控");
        this.call=call;
        visible=engine.isVisible();
        mAudioManager = (AudioManager) engine.getContext().getSystemService(Context.AUDIO_SERVICE);
        fftThread = new FftThread(engine);
		fftThread.start();
    }
    public boolean isMusicActive(){
        return flag;
    }
    public double[] getFft(){
        if(fftThread!=null)
            return fftThread.getFft();
            return null;
    }
    public void onVisibleChanged(boolean visible) {
        this.visible = visible;
        fftThread.notifyChanged(visible&&mAudioManager.isMusicActive());
        if(visible){
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessage(0);
        }else{
            mHandler.removeMessages(0);
        }
    }

    @Override
    protected void onLooperPrepared() {
        mHandler=new Handler(this);
        if(visible)
            mHandler.sendEmptyMessage(0);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (flag != mAudioManager.isMusicActive()) {
            flag=mAudioManager.isMusicActive();
            if (fftThread != null)
                fftThread.notifyChanged(flag&&visible);
            if(call!=null)
                call.onMusicActived(flag);
        }
        mHandler.sendEmptyMessageDelayed(0,1000);
        return true;
    }


    public void destroy(){
        destroy=true;
        if(fftThread!=null)
        fftThread.destroy();
    }
    public interface Callback{
        void onMusicActived(boolean active);
    }
}
