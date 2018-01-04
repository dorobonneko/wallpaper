package com.moe.adapter;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import java.util.List;
import android.widget.TextView;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.CardView;
import android.content.res.TypedArray;
import android.support.design.widget.TabLayout;
import com.moe.yaohuo.R;
import com.moe.internal.TextViewClickMode;
import android.util.TypedValue;
import android.support.v4.view.ViewCompat;

public class ActivedAdapter extends RecyclerView.Adapter<ActivedAdapter.ViewHolder>
{
	private List<CharSequence> list;
	public ActivedAdapter(List<CharSequence> list){
		this.list=list;
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int type)
	{
		CardView cv=new CardView(parent.getContext());
		TextView tv=new TextView(parent.getContext());
		cv.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT));
		cv.addView(tv,new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,CardView.LayoutParams.WRAP_CONTENT));
		tv.setId(android.R.id.title);
		TypedArray ta=parent.getContext().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.listPreferredItemHeightSmall});
		tv.setMinHeight(ta.getDimensionPixelSize(0,0));
		ta.recycle();
		ViewCompat.setElevation(cv,0);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,tv.getResources().getDimension(R.dimen.title));
		new TextViewClickMode(tv);
		return new ViewHolder(cv);
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int position)
	{
		vh.title.setText(list.get(position));
	}

	@Override
	public int getItemCount()
	{
		return list.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder{
		TextView title;
		public ViewHolder(View v){
			super(v);
			title=(TextView) v.findViewById(android.R.id.title);
		}
	}
}
