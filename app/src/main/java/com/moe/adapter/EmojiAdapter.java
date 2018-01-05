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
import android.view.MotionEvent;
public class EmojiAdapter extends EventAdapter<EmojiAdapter.ViewHolder>
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
		try{Glide.with(vh.icon.getContext()).load(PreferenceUtils.getHost(vh.icon.getContext())+"/face/"+list.get(p2)+".gif").asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.yaohuo).into(vh.icon);}catch(Exception e){}
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return list.size();
	}
	
	public class ViewHolder extends EventAdapter.ViewHolder implements View.OnTouchListener
	{

		@Override
		public boolean onTouch(View p1, MotionEvent p2)
		{
			return oitl!=null&&oitl.OnItemTouch(p2);
		}
		
		ImageView icon;
		TextView title;
		public ViewHolder(View v){
			super(EmojiAdapter.this,v);
			icon=(ImageView)v.findViewById(android.R.id.icon);
			title=(TextView)v.findViewById(android.R.id.title);
			if(oitl!=null)v.setOnTouchListener(this);
		}
	}
	private OnItemTouchListener oitl;
	public void setOnItemTouchListener(OnItemTouchListener oitl){
		this.oitl=oitl;
	}
	public abstract interface OnItemTouchListener{
		boolean OnItemTouch(MotionEvent event);
	}
}
