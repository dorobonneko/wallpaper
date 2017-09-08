package com.moe.utils;
import android.content.Context;

public class UrlUtils
{
	public static String getAbsUrl(Context context,String p1){
		return p1.startsWith("http") ?p1: p1.startsWith("//") ?"http:" + p1:p1.startsWith("/")?PreferenceUtils.getHost(context) + p1:PreferenceUtils.getHost(context)+"/"+ p1;
	}
}
