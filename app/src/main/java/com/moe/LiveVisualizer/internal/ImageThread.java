package com.moe.LiveVisualizer.internal;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.graphics.Bitmap;
import com.bumptech.glide.gifdecoder.GifDecoder;
import java.io.File;
import java.io.FileInputStream;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import java.io.IOException;

public class ImageThread extends Thread implements Handler.Callback
{
	private Bitmap image;
	private GifDecoder gifDecoder;
	private Handler handler;
	private LiveWallpaper live;
	private File file;
	public ImageThread(LiveWallpaper live, File file)
	{
		this.live = live;
		this.file = file;
	}
	@Override
	public void run()
	{
		Looper.prepare();
		handler = new Handler(this);
		loadWallpaper();
		Looper.loop();
	}
	public void loadImage()
	{
		if ( handler != null )
			handler.obtainMessage(0).sendToTarget();
	}
	public void notiftVisiableChanged(boolean visiable){
		if(visiable&&gifDecoder!=null&&handler!=null)
			handler.sendEmptyMessage(1);
			
	}
	@Override
	public boolean handleMessage(Message p1)
	{
		switch ( p1.what )
		{
			case 0:
				loadWallpaper();
				break;
			case 1:
					try{gifDecoder.advance();}catch(Exception e){}
					image=gifDecoder.getNextFrame();
					if(live.getEngine()!=null&&live.getEngine().isVisible())
					handler.sendEmptyMessageDelayed(1,gifDecoder.getNextDelay());
				break;
		}
		return true;
	}
	public void close()
	{
		if ( handler != null )
			handler.getLooper().quit();
	}
	public Bitmap getImage()
	{
		return image;
	}
	private synchronized void loadWallpaper()
	{
		if ( this.image != null )
		{
			this.image.recycle();
			image = null;
		}
		if ( gifDecoder != null )
		{
			this.gifDecoder.clear();
			this.gifDecoder = null;
		}
		if ( file.exists() && file.isFile() )
		{
			FileInputStream fis=null;
			try
			{
				gifDecoder = new GifDecoder(new GifDecoder.BitmapProvider(){

						@Override
						public Bitmap obtain(int p1, int p2, Bitmap.Config p3)
						{
							return Bitmap.createBitmap(p1, p2, p3);
						}

						@Override
						public void release(Bitmap p1)
						{
							p1.recycle();
						}
					});

				fis = new FileInputStream(file);
				gifDecoder.read(fis, (int)file.length());
				if(gifDecoder.getFrameCount()==0){
					throw new NullPointerException("it's not gif");
				}else if(handler!=null)
					handler.sendEmptyMessage(1);

			}
			catch (Exception e)
			{
				if(gifDecoder!=null)gifDecoder.clear();
				gifDecoder=null;
				image = BitmapFactory.decodeFile(file.getAbsolutePath());
			}
			finally
			{
				try
				{
					if ( fis != null )fis.close();
				}
				catch (IOException e)
				{}
			}
		}
	}

}
