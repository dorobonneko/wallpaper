package com.moe.thread;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import android.graphics.BitmapFactory;
import java.io.File;
import android.content.Context;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import android.text.Spannable;
import android.view.KeyEvent;
import com.moe.utils.ImageLoad;
import android.graphics.drawable.Drawable;

public class ImageLoadThread
{
	private OnLoadSuccessListener olsl;
	private Context cache;
	private ThreadPoolExecutor tpe;
	public ImageLoadThread(Context cache){
		this.cache=cache;
		tpe=new ThreadPoolExecutor(5,10,30,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(150));
	}
	public void loadImage(String url){
		tpe.execute(new Load(url));
	}
	public void setOnloadSuccessListener(OnLoadSuccessListener o){
		olsl=o;
	}
	public abstract interface OnLoadSuccessListener{
		void onLoadSuccess(String url,Drawable b);
		void onLoadError(String url);
	}
	private final Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					Object[] o=(Object[])msg.obj;
					if(olsl!=null)olsl.onLoadSuccess(o[0].toString(),(Drawable)o[1]);
					break;
				case 1:
					if(olsl!=null)olsl.onLoadError(msg.obj.toString());
					break;
			}
		}
		
	};
	private class Load extends Thread
	{
		private String url;
		public Load(String url){
			this.url=url;
		}
		
		@Override
		public void run()
		{
		
			Drawable b=new ImageLoad.Builder(cache,url).get().response();
			if(b==null)
			handler.obtainMessage(1,url).sendToTarget();
			else
			handler.obtainMessage(0,new Object[]{url,b}).sendToTarget();
			
		}
		
		
	}
	
}
