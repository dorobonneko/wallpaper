package com.moe.adapter;
import com.moe.entity.PictureItem;
import android.support.v7.widget.RecyclerView;
import java.util.List;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import com.moe.yaohuo.R;
import com.moe.utils.ImageCache;
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder>
{
	private List<PictureItem> list;
	public PictureAdapter(List<PictureItem> list){
		this.list=list;
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{

		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.picture_item,p1,false));
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int p2)
	{
		PictureItem ri=list.get(p2);
		vh.title.setText(ri.getTitle());
		ImageCache.loadNo(ri.getUrl(),vh.picture);
	}

	@Override
	public int getItemCount()
	{
		return list.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		TextView title;
		ImageView picture;
		public ViewHolder(View v){
			super(v);
			title=(TextView)v.findViewById(android.R.id.title);
			picture=(ImageView)v.findViewById(android.R.id.icon);
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			if(oicl!=null)oicl.onItemClick(PictureAdapter.this,this);
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

