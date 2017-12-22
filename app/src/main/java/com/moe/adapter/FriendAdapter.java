package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import com.moe.entity.FriendItem;
import com.moe.yaohuo.R;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.ImageView;
import com.moe.utils.ImageCache;
import android.content.Intent;
import com.moe.yaohuo.UserSpaceActivity;
import com.moe.yaohuo.UserInfoActivity;
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>
{
	private List<FriendItem> list;
	public FriendAdapter(List<FriendItem> list){
		this.list=list;
	}
	@Override
	public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.friend_item,p1,false));
	}

	@Override
	public void onBindViewHolder(FriendAdapter.ViewHolder p1, int p2)
	{
		FriendItem fi=list.get(p2);
		if(fi.getUi()!=null){
		p1.title.setText(fi.getUi().getName());
		ImageCache.load(fi.getUi().getLogo(),p1.icon);
		}
		p1.time.setText(fi.getTime());
	}

	@Override
	public int getItemCount()
	{
		return list.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		TextView title,time;
		ImageView icon;
		public ViewHolder(View v){
			super(v);
			title=(TextView) v.findViewById(android.R.id.title);
			time=(TextView) v.findViewById(android.R.id.summary);
			icon=(ImageView) v.findViewById(android.R.id.icon);
			icon.setOnClickListener(this);
			v.findViewById(R.id.delete).setOnClickListener(this);
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			switch(p1.getId()){
				case android.R.id.icon:
					try{
					p1.getContext().startActivity(new Intent(p1.getContext(),UserInfoActivity.class).putExtra("uid",list.get(getAdapterPosition()).getUi().getUid()));
					}catch(Exception e){}
					break;
				case R.id.delete:
					if(oal!=null)oal.onDelete(FriendAdapter.this,this);
					break;
				default:
					if(oicl!=null)oicl.onItemClick(FriendAdapter.this,this);
					break;
			}
		}


	}
	public void setOnItemClickListener(OnItemClickListener l){
		oicl=l;
	}
	private OnItemClickListener oicl;
	public abstract interface OnItemClickListener{
		void onItemClick(FriendAdapter sha,ViewHolder vh);
	}
	public void setOnDeleteListener(OnDeleteListener l){
		oal=l;
	}
	private OnDeleteListener oal;
	public abstract interface OnDeleteListener{
		void onDelete(FriendAdapter sha,ViewHolder vh);
	}
}
