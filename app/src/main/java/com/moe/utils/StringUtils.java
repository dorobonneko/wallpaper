package com.moe.utils;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.io.IOException;
import java.net.URLEncoder;
import android.content.Context;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{
	public static InputStream getInputStream(String url){
	try
		{
			HttpURLConnection huc=(HttpURLConnection)new URL(url).openConnection();
			return huc.getInputStream();
		}
		catch (IOException e)
		{}
		return null;
	}
	public static String getString(String url){
		return getString("GET",url);
	}
	public static String getString(String method,String url){
		return getString(method,null,url);
	}
	public static String getString(String method,String referer,String url){
		try{
		HttpURLConnection huc=(HttpURLConnection)new URL(url).openConnection();
		huc.setRequestMethod(method);
		if(referer!=null)
			huc.setRequestProperty("Referer",referer);
		huc.setRequestProperty("X-Requested-With","XMLHttpRequest");
		InputStream is=huc.getInputStream();
		String data=getString(is);
		huc.disconnect();
		return data;
		}catch(Exception e){}
		return null;
	}
	public static String getString(InputStream is){
		
		try{
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		byte[] buffer=new byte[4096];
		int len=-1;
		while((len=is.read(buffer))!=-1){
			baos.write(buffer,0,len);
		}
		baos.flush();
		is.close();
		String data=baos.toString();
		baos.close();
		return data;
		}catch(Exception e){}
		return null;
	}
	public static String direct(Context context,String input){
		if(PreferenceUtils.isDirect(context)){
			StringBuffer sb=new StringBuffer(input);
			Matcher matcher=Pattern.compile("(?s)([^\"'=]|^|\\s|\\s>|\">|br>){1}(http(|s)://[0-9a-zA-Z%\\?:=\\.\\-/_&;!#\\$\\(\\)\\*\\+,:@\\[\\]]+)",Pattern.DOTALL).matcher(input);
			while(matcher.find()){
				String url="<a href='"+matcher.group(2)+"'>"+matcher.group(2)+"</a>";
				int start=matcher.start(2);
				sb.replace(start,matcher.end(2),url);
				matcher.reset(sb.toString());
				matcher=matcher.region(start+url.length(),sb.length());
			}
			input=sb.toString();
			}
		return input;
	}
	public static String replaceRtlf(String input){
		return input.replaceAll("\n","[br]");
	}
}
