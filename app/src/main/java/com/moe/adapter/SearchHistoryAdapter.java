package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import java.util.List;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.moe.yaohuo.R;
import android.widget.TextView;
import android.widget.ImageButton;
import android.view.View;
import android.view.Gravity;
import android.widget.ImageView;
public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>
{
	private List<String> list;

	public SearchHistoryAdapter(List<String> list)
	{
		this.list=list;
	}
	
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{

		return new ViewHolder(this,LayoutInflater.from(p1.getContext()).inflate(R.layout.history_item, p1,false));
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int p2)
	{

		if (p2 == list.size())
		{
			vh.add.setVisibility(ImageButton.GONE);
			vh.title.setText("清空搜索记录");
			}
		else
		{
			vh.add.setVisibility(ImageButton.VISIBLE);
			vh.title.setText(list.get(p2));
		}
	}

	@Override
	public int getItemCount()
	{
		return list.size() == 0 ?0: list.size() + 1;
	}
	
	
	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		TextView title;
		ImageView add;
		SearchHistoryAdapter sha;
		public ViewHolder(SearchHistoryAdapter sha,View v){
			super(v);
			this.sha=sha;
				title = (TextView)v.findViewById(android.R.id.title);
				add = (ImageView)v.findViewById(android.R.id.icon);
				add.setOnClickListener(this);
				itemView.setOnClickListener(this);	
			
		}

		@Override
		public void onClick(View p1)
		{
			switch(p1.getId()){
				case android.R.id.icon:
					if(sha.getOnAddListener()!=null)sha.getOnAddListener().onAdd(sha,this);
					break;
				default:
				if(sha.getOnItemClickListener()!=null)sha.getOnItemClickListener().onItemClick(sha,this);
				break;
			}
		}

		
	}
	public void setOnItemClickListener(OnItemClickListener l){
		oicl=l;
	}
	public OnItemClickListener getOnItemClickListener(){
		return oicl;
	}
	private OnItemClickListener oicl;
	public abstract interface OnItemClickListener{
		void onItemClick(SearchHistoryAdapter sha,ViewHolder vh);
	}
	public void setOnAddListener(OnAddListener l){
		oal=l;
	}
	public OnAddListener getOnAddListener(){
		return oal;
	}
	private OnAddListener oal;
	public abstract interface OnAddListener{
		void onAdd(SearchHistoryAdapter sha,ViewHolder vh);
	}
}
