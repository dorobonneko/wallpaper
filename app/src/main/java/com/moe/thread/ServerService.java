package com.moe.thread;
import android.content.Context;
import android.net.Uri;

public class ServerService
{
	private Context context;
	private static ServerService ss;
	private ServerService(Context context){
		this.context=context.getApplicationContext();
	}
	public static ServerService getInstance(Context context){
		if(ss==null)ss=new ServerService(context);
		return ss;
	}
	public Server load(String url){
		return null;
	}
	public Server load(Uri uri){
		return null;
	}
}
