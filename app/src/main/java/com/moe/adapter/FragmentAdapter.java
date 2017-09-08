package com.moe.adapter;
import android.view.View;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import java.util.List;
import android.support.v4.app.FragmentManager;

public class FragmentAdapter extends FragmentPagerAdapter
{
	private List<Fragment> list;
	public FragmentAdapter(FragmentManager manager,List<Fragment> list){
		super(manager);
		this.list=list;
	}
	@Override
	public Fragment getItem(int p1)
	{
		// TODO: Implement this method
		return list.get(p1);
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		
		switch(position){
			case 0:
				return "发帖";
			case 1:
				return "回复";
			case 2:
				return "相册";
		}
		return null;
	}
	

	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return list.size();
	}

	
	
}
