package com.moe.adapter;
import com.moe.entity.DownloadItem;
import java.util.List;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import com.moe.yaohuo.R;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.ImageView;
import com.moe.services.DownloadService;
import android.view.LayoutInflater;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import com.moe.utils.NumberUtils;
import com.moe.widget.DownloadProgressBar;
public class DownloadAdapter extends RecyclerView.Adapter
{
	private List<DownloadItem> selected;
	private List<DownloadItem> list;
	public DownloadAdapter(List<DownloadItem> ldi,List<DownloadItem> selected){
		list=ldi;
		this.selected=selected;
	}
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		LayoutInflater inflater=LayoutInflater.from(p1.getContext());
		return p2==1?new ViewHolder2(inflater.inflate(R.layout.download_success_view,p1,false)):new ViewHolder(inflater.inflate(R.layout.download_item_view,p1,false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder p1, int p2)
	{
		DownloadItem di=list.get(p2);
		switch(p1.getItemViewType()){
			case 0:
				ViewHolder vh=(ViewHolder)p1;
				vh.title.setText(di.getTitle());
				if(di.isLoading())
					vh.state.setImageResource(R.drawable.pause);
						else
					vh.state.setImageResource(R.drawable.play);
				try{
				vh.progress.setProgress((int)(((double)di.getCurrent())/di.getTotal()*vh.progress.getMax()));
				long change=di.getCurrent()-(vh.getOldSize()==0?di.getCurrent():vh.getOldSize());
				if(!di.isLoading())change=0;
				vh.size.setText(NumberUtils.getSize(di.getCurrent())+"/"+NumberUtils.getSize(di.getTotal())+" | "+NumberUtils.getSize(change)+"/S"+(change==0?"":(" | 剩余"+NumberUtils.getTime((long)((di.getTotal()-di.getCurrent())/((double)change)*1000)))));
				}catch(Exception e){}
				if(selected.contains(di))
					vh.progress.setBackgroundColor(p1.itemView.getResources().getColor(R.color.primary_light));
				else
					vh.progress.setBackgroundDrawable(null);
					vh.setOldSize(di.getCurrent());
				break;
			case 1:
				((ViewHolder2)p1).title.setText(di.getTitle());
				((ViewHolder2)p1).size.setText(NumberUtils.getSize(di.getTotal()<1?new File(di.getDir()).length():di.getTotal()));
				if(selected.contains(di))
					((ViewHolder2)p1).bg.setBackgroundColor(p1.itemView.getResources().getColor(R.color.primary_light));
				else
					((ViewHolder2)p1).bg.setBackgroundDrawable(null);
				break;
		}
		
	}

	@Override
	public int getItemCount()
	{
		return list.size();
	}

	@Override
	public int getItemViewType(int position)
	{
		return list.get(position).getState()==DownloadService.State.SUCCESS?1:0;
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,View.OnClickListener{
		private TextView title,size;
		private DownloadProgressBar progress;
		private ImageView state;
		private long oldSize;
		public ViewHolder(View v){
			super(v);
			size=(TextView)v.findViewById(android.R.id.summary);
			title=(TextView)v.findViewById(android.R.id.title);
			progress=(DownloadProgressBar)v.findViewById(R.id.progressBar);
			state=(ImageView)v.findViewById(R.id.download_item_view_state);
			progress.setMax(100);
			v.setOnLongClickListener(this);
			v.setOnClickListener(this);
		}

		public void setOldSize(long oldSize)
		{
			this.oldSize = oldSize;
		}

		public long getOldSize()
		{
			return oldSize;
		}

		@Override
		public boolean onLongClick(View p1)
		{
			if(oilcl!=null)return oilcl.onItemLongClick(DownloadAdapter.this,this);
			return false;
		}

		@Override
		public void onClick(View p1)
		{
			if(oicl!=null)oicl.onItemClick(DownloadAdapter.this,this);
		}


		
	}
	public class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnLongClickListener,View.OnClickListener{
		private TextView title,size;
		private View bg;
		public ViewHolder2(View v){
			super(v);
			bg=v.findViewById(R.id.background);
			size=(TextView)v.findViewById(android.R.id.summary);
			title=(TextView)v.findViewById(android.R.id.title);
			v.setOnLongClickListener(this);
			v.setOnClickListener(this);
		}
		@Override
		public boolean onLongClick(View p1)
		{
			if(oilcl!=null)return oilcl.onItemLongClick(DownloadAdapter.this,this);
			return false;
		}

		@Override
		public void onClick(View p1)
		{
		if(oicl!=null)oicl.onItemClick(DownloadAdapter.this,this);
		}

		
	}
	public abstract interface OnItemLongClickListener{
		boolean onItemLongClick(DownloadAdapter adapter,RecyclerView.ViewHolder vh);
	}
	public void setOnItemLongClickListener(OnItemLongClickListener o){
		oilcl=o;
	}
	private OnItemLongClickListener oilcl;
	private OnItemClickListener oicl;
	public void setOnItemClickListener(OnItemClickListener l){
		oicl=l;
	}
	public abstract interface OnItemClickListener{
		void onItemClick(RecyclerView.Adapter ra,RecyclerView.ViewHolder vh);
	}
	
}
