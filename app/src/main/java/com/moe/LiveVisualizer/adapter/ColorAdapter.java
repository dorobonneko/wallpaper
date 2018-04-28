package com.moe.LiveVisualizer.adapter;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.View;
import com.moe.LiveVisualizer.utils.ColorList;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;

public class ColorAdapter extends BaseAdapter
{
	private ColorList list;
	public ColorAdapter(ColorList list){
		this.list=list;
	}
	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return list.size();
	}

	@Override
	public Object getItem(int p1)
	{
		return list.get(p1);
	}

	@Override
	public long getItemId(int p1)
	{
		return p1;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		View view;
		if(p2!=null)
			view=p2;
			else
		view=LayoutInflater.from(p3.getContext()).inflate(android.R.layout.simple_list_item_1,p3,false);
		TextView text=(TextView) view;
		text.setTextColor(list.get(p1));
		final float[] hsv=new float[3];
		Color.colorToHSV(list.get(p1),hsv);
		text.setText("HSV:"+hsv[0]+" "+hsv[1]+" "+hsv[2]+" #"+Integer.toHexString(list.get(p1)).toUpperCase());
		return view;
	}
	}
