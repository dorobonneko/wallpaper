package com.moe.adapter;
import android.widget.SpinnerAdapter;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.View;
import com.moe.entity.BbsItem;
import java.util.List;
import android.widget.TextView;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Spinner;
import android.widget.AbsListView;

public class BbsSpinner extends BaseAdapter
{
	private List<BbsItem> list;
	public BbsSpinner(List<BbsItem> list){
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
		// TODO: Implement this method
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
		TextView tv=new TextView(p3.getContext());
		tv.setSingleLine();
		tv.setText(list.get(p1).getTitle());
		AbsListView.LayoutParams vl=new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,p3.getResources().getDisplayMetrics()));
		tv.setPadding(50,0,50,0);
		tv.setLayoutParams(vl);
		tv.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
		return tv;
	}

}
