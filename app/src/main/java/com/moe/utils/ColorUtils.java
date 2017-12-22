package com.moe.utils;
import android.graphics.Color;

public class ColorUtils
{
	public static boolean isLight(int rgb){
		boolean r=isLightSigle(Color.red(rgb));
		boolean g=isLightSigle(Color.green(rgb));
		boolean b=isLightSigle(Color.blue(rgb));
		int i=0;
		if(r)i++;
		if(g)i++;
		if(b)i++;
		//String s=Integer.toHexString(rgb);
		return i>1;
	}
	public static boolean isLightSigle(int color){
		return color>0xa5;
	}
}
