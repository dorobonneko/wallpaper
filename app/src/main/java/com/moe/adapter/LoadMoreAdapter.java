package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;

public abstract class LoadMoreAdapter<T extends LoadMoreAdapter.ViewHolder> extends RecyclerView.Adapter<LoadMoreAdapter.ViewHolder>
{
	private List<View> bottom=new ArrayList<>();
	public void addFloorView(View v){
		bottom.add(v);
		notifyItemInserted(getItemCount()-1);
	}
	public View getFloorView(int position){
		return bottom.get(position-(getItemCount()-bottom.size()));
	}

	@Override
	public final void onBindViewHolder(LoadMoreAdapter.ViewHolder p1, int p2)
	{
		if(p2<getItemCount()-bottom.size())
		onBindViewHolderSub((T)p1,p2);
	}


	@Override
	public final LoadMoreAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		return p2==-1?onCreateViewHolderSub(p1,p2):new ViewHolder(bottom.get(p2));
	}

	public abstract void onBindViewHolderSub(T p1, int p2);
	
	public abstract T onCreateViewHolderSub(ViewGroup p1, int p2);
	@Override
	public final int getItemCount()
	{
		return bottom.size()+getItemCountSub();
	}
	public abstract int getItemCountSub();
	@Override
	public int getItemViewType(int position)
	{
		return position>=getItemCountSub()?position-getItemCountSub():-1;
	}
	
	public static class ViewHolder extends RecyclerView.ViewHolder{
		public ViewHolder(View v){
			super(v);
		}
	}
}
