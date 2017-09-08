package com.moe.adapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter
{
	private List<? extends View> list;
	public ViewPagerAdapter(List<? extends View> l){
		list=l;
	}
	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View p1, Object p2)
	{
		return p1==p2;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		View view=list.get(position);
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		container.removeView((View)object);
			}

	@Override
	public void destroyItem(View container, int position, Object object)
	{
		destroyItem((ViewGroup)container,position,object);
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		return list.get(position).getTag().toString();
	}
	
}
