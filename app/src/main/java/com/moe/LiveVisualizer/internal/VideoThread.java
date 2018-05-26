package com.moe.LiveVisualizer.internal;
import android.media.MediaPlayer;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.view.Surface;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import java.io.IOException;
import android.graphics.Bitmap;
import java.io.File;
import android.media.MediaCodec;
import android.graphics.BitmapFactory;
import java.util.ArrayList;
import android.media.MediaDataSource;
import java.io.InputStream;
import java.io.FileDescriptor;
import java.util.Iterator;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.os.Handler;
import android.view.TextureView;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class VideoThread extends Thread
{
	private MediaMetadataRetriever meta;
	private String path;
	private long duration,current,oldTime;
	private boolean playing;
	private ArrayList<Bitmap> list=new ArrayList<>();
	public VideoThread(String path){
		this.path=path;
		meta=new MediaMetadataRetriever();
		}

	public void notifyVisiableChanged(boolean visible)
	{
		playing=visible;
		if(playing)
			oldTime=System.currentTimeMillis();
	}

	public void onSizeChanged()
	{
		// TODO: Implement this method
	}
	@Override
	public void run()
	{
		meta.setDataSource(path);
		duration=Long.parseLong(meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
		try{
		for(long i=0;i<duration;i+=33){
			Bitmap bit=meta.getFrameAtTime(i*1000,meta.OPTION_CLOSEST);
			if(bit!=null){
				list.add(bit);
				break;
				}
		}}catch(Exception e){}
		
	}
	public Bitmap getImage(){
		try{
		if(current>=list.size())
			current=0;
		Bitmap b=list.get((int)current);
		current++;
		return b;
		}catch(Exception e){}
		return null;
	}
	public void release(){
		meta.release();
		Iterator<Bitmap> iterator=list.iterator();
		while(iterator.hasNext()){
			iterator.next().recycle();
			iterator.remove();
		}
	}
	
}
