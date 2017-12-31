package com.moe.widget;
import android.util.AttributeSet;
import android.content.Context;
import android.content.res.TypedArray;
import com.moe.yaohuo.R;

public class Toolbar extends android.support.v7.widget.Toolbar
{
	public Toolbar(Context context,AttributeSet attrs){
		super(context,attrs);
		TypedArray ta=context.obtainStyledAttributes(attrs,new int[]{android.support.v7.appcompat.R.attr.titleTextColor,android.support.v7.appcompat.R.attr.subtitleTextColor});
		setTitleTextColor(ta.getColor(0,0xff000000));
		setSubtitleTextColor(ta.getColor(1,0xffbbbbbb));
		ta.recycle();
	}
}
