package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import java.util.List;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import com.moe.entity.ReplyItem;
import com.moe.yaohuo.R;
import android.text.Html;
import com.moe.internal.ImageGetter;
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder>
{
	private List<ReplyItem> list;
	public ReplyAdapter(List<ReplyItem> list){
		this.list=list;
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{

		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.reply_item,p1,false));
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int p2)
	{
		ReplyItem ri=list.get(p2);
		vh.title.setText(Html.fromHtml(ri.getTitle(),new ImageGetter(vh.title,true),null));
		vh.summary.setText(ri.getSummary());
		}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return list.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		TextView title,summary;
		public ViewHolder(View v){
			super(v);
			title=(TextView)v.findViewById(android.R.id.title);
			summary=(TextView)v.findViewById(android.R.id.summary);
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			if(oicl!=null)oicl.onItemClick(ReplyAdapter.this,this);
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
