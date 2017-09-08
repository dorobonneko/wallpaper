package com.moe.utils;
import android.graphics.Bitmap;
import java.util.Map;
import java.util.LinkedHashMap;
import android.content.Context;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.io.InputStream;
import android.os.Handler;
import android.os.Message;
import android.os.Looper;
import android.graphics.BitmapFactory;
import java.io.FileInputStream;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import android.graphics.drawable.Drawable;

public class ImageLoad implements Handler.Callback
{
	private Builder builder;
	private Drawable bit;
	private String fragment;
	private Handler handler;
	private ImageLoad(Builder b){
		this.builder=b;
		if(b.data!=null){
				fragment="";
				for(String key:b.data.keySet())
				fragment+=(key+"="+b.data.get(key)+"&");
				fragment=fragment.substring(0,fragment.length()-1);
			}
			if(b.cb!=null&&Thread.currentThread().getName().equals("main"))
			handler=new Handler(this);		
	}

	@Override
	public boolean handleMessage(Message p1)
	{
		switch(p1.what){
			case 0:
				if(builder.cb!=null)
					builder.cb.onLoad(builder.url,bit,builder.o);
				break;
		}
		return true;
	}

	
	public void execute(){
		new Load().start();
	}
	public Drawable response(){
		new Load().run();
		return bit;
	}
	private class Load extends Thread
	{

		@Override
		public void run()
		{
			HttpURLConnection huc=null;
			OutputStream post = null;
			InputStream is = null;
			File cache = null;
			FileOutputStream fos = null;
			try
			{
				if(builder.context!=null){
					cache=builder.context.getExternalCacheDir();
					if(!cache.exists())cache.mkdirs();
					cache=new File(cache,builder.url.hashCode()+"");
					if(cache.exists()){
						bit=Drawable.createFromPath(cache.getAbsolutePath());
						if(handler!=null)
						handler.sendEmptyMessage(0);
						return;
					}
				}
				huc = (HttpURLConnection)new URL(builder.method.equals("GET")&&fragment!=null?builder.url+"?"+fragment:builder.url).openConnection();
				if(huc instanceof HttpsURLConnection){
				}
				if(builder.header!=null){
					for(String key:builder.header.keySet())
					huc.setRequestProperty(key,builder.header.get(key));
				}
				if(builder.method.equals("POST")){
					post=huc.getOutputStream();
					post.write(fragment.getBytes());
				}
				is=huc.getInputStream();
				bit=Drawable.createFromStream(is,null);
				if(handler!=null)
					handler.sendEmptyMessage(0);
				/*if(cache!=null&&bit!=null){
					fos=new FileOutputStream(cache);
					((BitmapDrawable)bit).getBitmap().compress(Bitmap.CompressFormat.PNG,80,fos);
					fos.flush();
				}*/
				
				
			}
			catch (IOException e)
			{if(handler!=null)
				handler.sendEmptyMessage(0);
				if(cache!=null)cache.delete();
				}finally{
				try
				{
					if (fos != null)fos.close();
				}
				catch (IOException e)
				{}
				try
				{
					if (is != null)is.close();
				}
				catch (IOException e)
				{}
				try
				{
					if (post != null)post.close();
				}
				catch (IOException e)
				{}
				if (huc != null)huc.disconnect();
			}
		}
		
	}
	public abstract interface Callback{
		void onLoad(String url,Drawable b,Object o);
	}
	public static class Builder{
		 Map<String,String> data,header;
		 Callback cb;
		 String url;
		 String method;
		 Context context;
		 Object o;
		public Builder(String url){
			this.url=url;
		}
		public Builder(Context context,String url){
			this(url);
			this.context=context;
		}
		public Builder data(String key,String value){
			if(data==null)data=new LinkedHashMap<>();
			data.put(key,value);
			return this;
		}
		public Builder header(String key,String value){
			if(header==null)header=new LinkedHashMap<>();
			header.put(key,value);
			return this;
		}
		public Builder callback(Callback cb,Object o){
			this.cb=cb;
			this.o=o;
			return this;
		}
		public Builder callback(Callback c){
			return this.callback(c,null);
		}
		public ImageLoad get(){
			this.method="GET";
			return new ImageLoad(this);
		}
		public ImageLoad post(){
			this.method="POST";
			return new ImageLoad(this);
		}
	}
}
