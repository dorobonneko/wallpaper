package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import com.moe.yaohuo.R;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import com.moe.utils.ImageCache;
import com.moe.utils.PreferenceUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder>
{
	private List<String> list;
	public EmojiAdapter(List<String> list){
		this.list=list;
	}
	@Override
	public EmojiAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
	
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.face,p1,false));
	}

	@Override
	public void onBindViewHolder(EmojiAdapter.ViewHolder vh, int p2)
	{
		vh.title.setText(list.get(p2));
		//ImageCache.load(PreferenceUtils.getHost(vh.icon.getContext())+"/face/"+list.get(p2)+".gif",vh.icon);
		Glide.with(vh.icon.getContext()).load(PreferenceUtils.getHost(vh.icon.getContext())+"/face/"+list.get(p2)+".gif").diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.yaohuo).into(vh.icon);
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return list.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		ImageView icon;
		TextView title;
		public ViewHolder(View v){
			super(v);
			icon=(ImageView)v.findViewById(android.R.id.icon);
			title=(TextView)v.findViewById(android.R.id.title);
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			if(oicl!=null)oicl.onItemClick(EmojiAdapter.this,this);
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
