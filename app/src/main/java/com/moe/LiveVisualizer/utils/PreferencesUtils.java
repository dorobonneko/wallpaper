package com.moe.LiveVisualizer.utils;
import android.content.Context;
import android.net.Uri;
import android.database.Cursor;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import com.moe.LiveVisualizer.service.SharedPreferences;
import android.os.Handler;
import android.content.ContentResolver;
import android.database.ContentObserver;

public class PreferencesUtils
{
	private ContentObserver observer;
	private Context context;
	private Map<String,String> buffer;
	public PreferencesUtils(Context context){
		this.context=context;
		context.getContentResolver().registerContentObserver(Uri.parse(SharedPreferences.URI),true,observer=new ContentObserver(new Handler()){
			public void onChange(boolean p1,Uri p2){
				buffer.put(p2.getQueryParameter("key"),getString(null,p2));
			}
		});
		buffer=new HashMap<>();
	}
	public synchronized String getString(String key,String defaultVlue){
		String value=buffer.get(key);
		if(value!=null)return value;
		Cursor cursor=context.getContentResolver().query(getUriBuilder().appendQueryParameter("key",key).build(),null,null,null,null,null);
		if(cursor.moveToFirst())
			value=cursor.getString(1);
		cursor.close();
		if(value==null)
			value=defaultVlue;
			buffer.put(key,value);
		return value;
	}
	public int getInt(String key,int defaultValue){
		return Integer.parseInt(getString(key,defaultValue+""));
	}
	public boolean getBoolean(String key,boolean defaultVlue){
		return Boolean.parseBoolean(getString(key,defaultVlue+""));
	}
	public static String getString(Context context,Uri uri){
		if(context!=null){
		String value=null;
		Cursor cursor=context.getContentResolver().query(uri,null,null,null,null,null);
		if(cursor.moveToFirst())
			value=cursor.getString(1);
			cursor.close();
		return value;
		}
		return uri.getQueryParameter("value");
	}
	public static int getInt(Context context,Uri uri,int defaultValue){
		String value=getString(context,uri);
		if(value==null)
			return defaultValue;
			else
			return Integer.parseInt(value);
	}
	public static boolean getBoolean(Context context,Uri uri,boolean defaultValue){
		String value=getString(context,uri);
		if(value==null)
			return defaultValue;
		else
			return Boolean.parseBoolean(value);
	}
	public static Uri.Builder getUriBuilder(){
		return new Uri.Builder().scheme("content").authority("moe").path("moe");
	}
	public void close(){
		if(observer!=null)
			context.getContentResolver().unregisterContentObserver(observer);
			context=null;
	}
}
