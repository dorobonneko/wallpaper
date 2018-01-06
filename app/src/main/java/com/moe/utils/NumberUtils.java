package com.moe.utils;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import java.util.Date;

public class NumberUtils
{
	private static DecimalFormat df=new DecimalFormat("0.00");
	public static String getSize(long bytes){
		if(bytes<1024)
			return bytes+"B";
		double size=bytes/1024.0;
		if(size<1024)
			return df.format(size)+"K";
		size/=1024;
		if(size<1024)
			return df.format(size)+"M";
			else
			return df.format(size/1024)+"G";
		
	}
	public static String getTime(long time){
		time=time/1000;
		long s=time%60;
		long m=time/60;
		long h=0;
		if(m>60)
		{
			h=m/60;
			m=m%60;
		}
		return h==0?m==0?s+"秒":m+"分钟":h+"小时";
		//return h==0?((m<10?"0"+m:""+m)+":"+(s<10?"0"+s:s)):((h<10?"0"+h:""+h)+":"+(m<10?"0"+m:m)+":"+(s<10?"0"+s:s));
	}
	
}
