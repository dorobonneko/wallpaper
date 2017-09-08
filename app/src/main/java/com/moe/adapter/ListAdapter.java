package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.moe.yaohuo.R;
import com.moe.entity.ListItem;
import java.util.List;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.widget.TextView;
import android.text.Html;
import com.moe.internal.ImageGetter;
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>
{
	private View parent;
	private boolean anime=false;
	private List<ListItem> list;
	public ListAdapter(List<ListItem> list){
		this.list=list;
	}
	@Override
	public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		parent=p1;
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.list_item,p1,false));
	}
	public void setAnime(boolean anime){
		this.anime=anime;
	}
	

	@Override
	public void onBindViewHolder(ListAdapter.ViewHolder vh, int p2)
	{
		vh.itemView.setId(p2);
		ListItem li=list.get(p2);
		vh.title.setText(Html.fromHtml(li.getTitle(),new ImageGetter(vh.title,false),null));
		vh.author.setText(Html.fromHtml(li.getAuthor()));
		vh.progress.setText(Html.fromHtml(li.getProgress()));
		vh.time.setText(li.getTime());
		StringBuffer sb=new StringBuffer();
		for(String pro:li.getProperty())
		sb.append("<font color='#00bcd4'=>").append(pro).append("</font>/");
		if(sb.length()>0){
			sb.deleteCharAt(sb.length()-1);
			vh.property.setText(Html.fromHtml(sb.toString()));
		}else
		vh.property.setText(null);
		if(anime){
			final float[] value=new float[]{0,1};
			Animator anime=ObjectAnimator.ofFloat(vh.itemView,"Alpha",value);
			anime.setDuration(500);
			anime.start();
		}
	}

	@Override
	public int getItemCount()
	{
		return list.size();
	}

	
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		private TextView title,author,progress,time,property;
		public ViewHolder(View v){
			super(v);
			title=(TextView)v.findViewById(R.id.list_item_title);
			author=(TextView)v.findViewById(R.id.list_item_author);
			progress=(TextView)v.findViewById(R.id.list_item_progress);
			time=(TextView)v.findViewById(R.id.list_item_time);
			property=(TextView)v.findViewById(R.id.list_item_property);
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			if(oicl!=null)oicl.onItemClick(ListAdapter.this,this);
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
