package com.moe.utils;
import android.icu.text.DecimalFormat;

public class NumberUtils
{
	private static DecimalFormat df=new DecimalFormat("0.00");
	public static String getSize(long bytes){
		if(bytes<1024)
			return bytes+"B";
		double size=bytes/1024.0;
		if(size<1024)
			return df.format(size)+"k";
		size/=1024;
		if(size<1024)
			return df.format(size)+"M";
			else
			return df.format(size/1024)+"G";
		
	}
}
