package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.moe.yaohuo.R;
import android.view.LayoutInflater;
import java.util.List;
import android.view.View;
import android.widget.TextView;
import com.moe.entity.MsgItem;
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{
	private List<MsgItem> list;
	public MessageAdapter(List<MsgItem> list){
		this.list=list;
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{

		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.message_item,p1,false));
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int p2)
	{
		MsgItem mi=list.get(p2);
		vh.title.setText(mi.getTitle());
		vh.summary.setText(mi.getFrom());
		vh.subsummary.setText(mi.getTime());
		if(mi.getView()==1)
			vh.new_m.setVisibility(View.VISIBLE);
			else
			vh.new_m.setVisibility(View.INVISIBLE);
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return list.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		TextView title,summary,subsummary;
		View new_m;
		public ViewHolder(View v){
			super(v);
			new_m=v.findViewById(R.id.new_msg);
			v.findViewById(R.id.delete).setOnClickListener(this);
			title=(TextView)v.findViewById(android.R.id.title);
			summary=(TextView)v.findViewById(android.R.id.summary);
			subsummary=(TextView)v.findViewById(R.id.summary);
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			switch(p1.getId()){
				case R.id.delete:
					if(odl!=null)odl.onDelete(MessageAdapter.this,this);
					break;
					default:
			if(oicl!=null)oicl.onItemClick(MessageAdapter.this,this);
			break;
			}
		}


	}
	private OnItemClickListener oicl;
	public void setOnItemClickListener(OnItemClickListener l){
		oicl=l;
	}
	public abstract interface OnItemClickListener{
		void onItemClick(RecyclerView.Adapter ra,RecyclerView.ViewHolder vh);
	}
	public void setOnDeleteListener(OnDeleteListener l){
		odl=l;
	}

	private OnDeleteListener odl;
	public abstract interface OnDeleteListener{
		void onDelete(MessageAdapter sha,ViewHolder vh);
	}
}


