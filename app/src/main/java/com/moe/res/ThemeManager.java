package com.moe.res;
import android.view.View;
import android.widget.TextView;
import android.content.res.Resources;
import android.content.res.TypedArray;
import java.util.Map;
import android.content.Context;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.moe.yaohuo.R;
import java.util.Iterator;

public class ThemeManager implements Application.ActivityLifecycleCallbacks
{
	private static final int ACCENT=1;
	private static final int PRIMARY=2;
	private static final int NORMAL=4;
	private static final int TEXTPRIMARY=8;
	private static final int TEXTSECONDARY=16;
	private Map<Context,List<View>> list;
	private static ThemeManager tm;
	private ThemeSet themeSet,prepare;
	private ThemeManager(){
		list=new HashMap<>();
		
	}
	public static ThemeManager getInstance(){
		if(tm==null)
			tm=new ThemeManager();
			return tm;
	}
	public static ThemeSet getThemeSet(){
		return getInstance().themeSet;
	}
	public void init(Application theme){
		theme.registerActivityLifecycleCallbacks(this);
		themeSet=new ThemeSet();
		TypedArray ta=theme.obtainStyledAttributes(new int[]{
			android.support.v7.appcompat.R.attr.colorPrimary,
			android.support.v7.appcompat.R.attr.colorAccent,
			android.support.v7.appcompat.R.attr.colorControlNormal,
			android.R.attr.textColorPrimary,
			android.R.attr.textColorSecondary
		});
		themeSet.setPrimary(ta.getColor(0,0xff000000));
		themeSet.setAccent(ta.getColor(1,0xffffffff));
		themeSet.setControlNormal(ta.getColor(2,0xff000000));
		themeSet.setTextPrimary(ta.getColor(3,0xff000000));
		themeSet.setTextSecondary(ta.getColor(4,0xffbbbbbb));
	}
	private void injectView(View v){
		List<View> context=list.get(v.getContext());
		if(context==null){
			context=new ArrayList<View>();
			if(v.getContext() instanceof Activity){
				((Application)v.getContext().getApplicationContext()).registerActivityLifecycleCallbacks(this);
			}
		}
		context.add(v);
	}
	public ThemeManager subscriptionColorPrimary(View v){
		injectView(v);
		v.setTag(R.id.type,PRIMARY);
		return this;
	}
	public ThemeManager subscriptionColorAccent(View v){
		injectView(v);
		v.setTag(R.id.type,ACCENT);
		return this;
	}
	public ThemeManager subscriptionColorControlNormal(View v){
		injectView(v);
		v.setTag(R.id.type,NORMAL);
		return this;
	}
	public ThemeManager subscriptionTextColorPrimary(TextView tv){
		injectView(tv);
		tv.setTag(R.id.type,TEXTPRIMARY);
		if(themeSet!=null)
			tv.setBackgroundColor(themeSet.getPrimary());
		return this;
	}
	public ThemeManager subscriptionTextColorSecondary(TextView tv){
		injectView(tv);
		tv.setTag(R.id.type,TEXTSECONDARY);
		return this;
	}
	
	
	
	
	@Override
	public void onActivityCreated(Activity p1, Bundle p2)
	{
		// TODO: Implement this method
	}

	@Override
	public void onActivityStarted(Activity p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onActivityResumed(Activity p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onActivityPaused(Activity p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onActivityStopped(Activity p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onActivitySaveInstanceState(Activity p1, Bundle p2)
	{
		// TODO: Implement this method
	}
	public ThemeManager setAccent(int color){
		if(prepare==null)prepare=new ThemeSet();
		prepare.setAccent(color);
		return this;
	}
	public ThemeManager setPrimary(int color){
		if(prepare==null)prepare=new ThemeSet();
		prepare.setPrimary(color);
		
		return this;
	}
	public ThemeManager setControlNormal(int color){
		if(prepare==null)prepare=new ThemeSet();
		prepare.setControlNormal(color);
		
		return this;
	}
	public ThemeManager setTextPrimary(int color){
		if(prepare==null)prepare=new ThemeSet();
		prepare.setTextPrimary(color);
		
		return this;
	}
	public ThemeManager setTextSecondary(int color){
		if(prepare==null)prepare=new ThemeSet();
		prepare.setTextSecondary(color);
		
		return this;
	}
	public void apply(){
		if(themeSet==null)
			themeSet=prepare;
			else
			themeSet.setThemeSet(prepare);
		if(themeSet==null)return;
		Iterator<List<View>> iter=list.values().iterator();
		while(iter.hasNext()){
			Iterator<View> views=iter.next().iterator();
			while(views.hasNext()){
				View v=views.next();
				switch((Integer)v.getTag(R.id.type)){
					case ACCENT:
						break;
					case PRIMARY:
						v.setBackgroundColor(themeSet.getPrimary());
						break;
					case NORMAL:
						break;
					case TEXTPRIMARY:
						((TextView)v).setTextColor(themeSet.getTextPrimary());
						break;
					case TEXTSECONDARY:
						((TextView)v).setTextColor(themeSet.getTextSecondary());
						break;
				}
			}
		}
	}
	@Override
	public void onActivityDestroyed(Activity p1)
	{
		List<View> context=list.get(p1);
		if(context!=null){
			Iterator<View> iter=context.iterator();
			while(iter.hasNext())
				iter.remove();
			}
	}
	
	public abstract interface ThemeRefreshListener{
		void onThemeRefresh(ThemeSet ts);
	}
	public final class ThemeSet{
		private int primary,accent,controlNormal,textPrimary,textSecondary;
		public void reset(){
			primary=0;
			accent=0;
			controlNormal=0;
			textPrimary=0;
			textSecondary=0;
		}
		public void setThemeSet(ThemeSet theme){
			if(theme==null)return;
			if(theme.accent!=0)
				accent=theme.accent;
			if(theme.primary!=0)
				primary=theme.primary;
			if(theme.controlNormal!=0)
				controlNormal=theme.controlNormal;
			if(theme.textPrimary!=0)
				textPrimary=theme.textPrimary;
			if(theme.textSecondary!=0)
				textSecondary=theme.textSecondary;
		}
		public void setPrimary(int primary)
		{
			this.primary = primary;
		}

		public int getPrimary()
		{
			return primary;
		}

		public void setAccent(int accent)
		{
			this.accent = accent;
		}

		public int getAccent()
		{
			return accent;
		}

		public void setControlNormal(int controlNormal)
		{
			this.controlNormal = controlNormal;
		}

		public int getControlNormal()
		{
			return controlNormal;
		}

		public void setTextPrimary(int textPrimary)
		{
			this.textPrimary = textPrimary;
		}

		public int getTextPrimary()
		{
			return textPrimary;
		}

		public void setTextSecondary(int textSecondary)
		{
			this.textSecondary = textSecondary;
		}

		public int getTextSecondary()
		{
			return textSecondary;
		}}
}
