package com.moe.widget;
import android.widget.FrameLayout;
import android.util.AttributeSet;
import android.content.Context;
import com.moe.res.ThemeManager;

public class PrimaryView extends FrameLayout
{
	public PrimaryView(Context context,AttributeSet attrs){
		super(context,attrs);
		ThemeManager.getInstance().subscriptionColorPrimary(this);
	}
}
