package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import com.moe.entity.UbbItem;
import android.widget.Button;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;

public class UbbAdapter extends EventAdapter<UbbAdapter.ViewHolder>
{
	private List<UbbItem> list;
	public UbbAdapter(List<UbbItem> list){
		this.list=list;
	}

	@Override
	public UbbAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		Button b=new Button(p1.getContext());
		b.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,RecyclerView.LayoutParams.WRAP_CONTENT));
		return new ViewHolder(b);
	}

	@Override
	public void onBindViewHolder(UbbAdapter.ViewHolder p1, int p2)
	{
		p1.btn.setText(list.get(p2).getTitle());
	}

	@Override
	public int getItemCount()
	{
		return list.size();
	}
	
	public class ViewHolder extends EventAdapter.ViewHolder{
		Button btn;
		public ViewHolder(View v){
			super(UbbAdapter.this,v);
			TypedArray ta=v.getContext().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.selectableItemBackground});
			ViewCompat.setBackground(v,ta.getDrawable(0));
			ta.recycle();
			btn=(Button) v;
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			btn.setMinHeight(0);
			btn.setMinWidth(0);
			btn.setMinEms(0);
			int _16=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16,v.getResources().getDisplayMetrics());
			btn.setPadding(_16,_16/2,_16,_16/2);
			}
		
	}
	
}
