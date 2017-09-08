package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.moe.entity.BbsItem;
import java.util.List;
import com.moe.yaohuo.R;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import com.moe.utils.ImageCache;
public class BbsAdapter extends RecyclerView.Adapter<BbsAdapter.ViewHolder>
{
	private List<BbsItem> list;
	public BbsAdapter(List<BbsItem> list){
		this.list=list;
	}
	@Override
	public BbsAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		// TODO: Implement this method
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.bbs_item,p1,false));
	}

	@Override
	public void onBindViewHolder(BbsAdapter.ViewHolder vh, int p2)
	{
		BbsItem bi=list.get(p2);
		vh.title.setText(bi.getTitle());
		vh.summary.setText(bi.getProgress());
		ImageCache.load(bi.getImgurl(),vh.logo);
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return list.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		ImageView logo;
		TextView title,summary;
		public ViewHolder(View v){
			super(v);
			logo=(ImageView)v.findViewById(android.R.id.icon);
			title=(TextView)v.findViewById(android.R.id.title);
			summary=(TextView)v.findViewById(android.R.id.summary);
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			if(oicl!=null)oicl.onItemClick(BbsAdapter.this,this);
		}

		
	}
	private OnItemClickListener oicl;
	public void setOnItemClickListener(OnItemClickListener l){
		oicl=l;
	}
	public abstract interface OnItemClickListener{
		void onItemClick(RecyclerView.Adapter ra,RecyclerView.ViewHolder vh);
	}
}
