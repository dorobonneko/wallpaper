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

public class UbbAdapter extends RecyclerView.Adapter<UbbAdapter.ViewHolder>
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
		// TODO: Implement this method
		return list.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		Button btn;
		public ViewHolder(View v){
			super(v);
			TypedArray ta=v.getContext().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.selectableItemBackground});
			ViewCompat.setBackground(v,ta.getDrawable(0));
			ta.recycle();
			btn=(Button) v;
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			btn.setMinHeight(0);
			btn.setMinWidth(0);
			btn.setMinEms(0);
			btn.setPadding(35,20,35,20);
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			if(oicl!=null)oicl.onItemClick(UbbAdapter.this,this);
		}

		
	}
	public void setOnItemClickListener(OnItemClickListener l){
		oicl=l;
	}
	private OnItemClickListener oicl;
	public abstract interface OnItemClickListener{
		void onItemClick(RecyclerView.Adapter adapter,RecyclerView.ViewHolder vh);
	}
}
