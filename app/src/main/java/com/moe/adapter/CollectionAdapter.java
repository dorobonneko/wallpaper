package com.moe.adapter;
import java.util.List;
import android.view.View;
import android.widget.ImageView;
import com.moe.yaohuo.R;
import com.moe.entity.CollectionItem;
import com.moe.adapter.SearchHistoryAdapter.ViewHolder;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder>
{
	private List<? extends CollectionItem> list;
	public CollectionAdapter(List<? extends CollectionItem> list){
		this.list=list;
	}

	@Override
	public com.moe.adapter.CollectionAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		// TODO: Implement this method
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.collection_item,p1,false));
	}


	@Override
	public int getItemCount()
	{
		return list.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int p2)
	{
		vh.title.setText(list.get(p2).getTitle());
		vh.summary.setText(list.get(p2).getSummary());
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		TextView title,summary;
		public ViewHolder(View v){
			super(v);
			title = (TextView)v.findViewById(android.R.id.title);
			summary = (TextView)v.findViewById(android.R.id.summary);
			
			v.findViewById(android.R.id.icon).setOnClickListener(this);
			itemView.setOnClickListener(this);	

		}

		@Override
		public void onClick(View p1)
		{
			switch(p1.getId()){
				case android.R.id.icon:
					if(oal!=null)oal.onDelete(CollectionAdapter.this,this);
					break;
				default:
					if(oicl!=null)oicl.onItemClick(CollectionAdapter.this,this);
					break;
			}
		}


	}
	public void setOnItemClickListener(OnItemClickListener l){
		oicl=l;
	}
	
	private OnItemClickListener oicl;
	public abstract interface OnItemClickListener{
		void onItemClick(CollectionAdapter sha,ViewHolder vh);
	}
	public void setOnDeleteListener(OnDeleteListener l){
		oal=l;
	}
	
	private OnDeleteListener oal;
	public abstract interface OnDeleteListener{
		void onDelete(CollectionAdapter sha,ViewHolder vh);
	}
}
