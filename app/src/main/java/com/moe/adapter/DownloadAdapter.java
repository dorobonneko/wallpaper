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
public class DownloadAdapter extends RecyclerView.Adapter
{
	private List<DownloadItem> selected;
	private List<DownloadItem> list;
	private DecimalFormat format=new DecimalFormat("0.00");
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
				File file=new File(di.getDir());
				//long length=file.length();
				try{
				vh.progress.setProgress((int)(((double)file.length())/di.getTotal()*vh.progress.getMax()));
				vh.size.setText(format.format(file.length()/1024.0/1024)+"M/"+format.format(di.getTotal()/1024.0/1024)+"M");
				}catch(Exception e){}
				if(selected.contains(di))
					vh.bg.setBackgroundColor(p1.itemView.getResources().getColor(R.color.divider));
				else
					vh.bg.setBackgroundDrawable(null);
				break;
			case 1:
				((ViewHolder2)p1).title.setText(di.getTitle());
				((ViewHolder2)p1).size.setText(format.format((di.getTotal()<1?new File(di.getDir()).length():di.getTotal())/1024.0/1024)+"M");
				if(selected.contains(di))
					((ViewHolder2)p1).bg.setBackgroundColor(p1.itemView.getResources().getColor(R.color.divider));
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
		private ProgressBar progress;
		private ImageView state;
		private View bg;
		public ViewHolder(View v){
			super(v);
			bg=v.findViewById(R.id.background);
			size=(TextView)v.findViewById(R.id.download_item_view_size);
			title=(TextView)v.findViewById(R.id.download_item_view_title);
			progress=(ProgressBar)v.findViewById(R.id.download_item_view_progress);
			state=(ImageView)v.findViewById(R.id.download_item_view_state);
			progress.setMax(100);
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
	public class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnLongClickListener,View.OnClickListener{
		private TextView title,size;
		private View bg;
		public ViewHolder2(View v){
			super(v);
			bg=v.findViewById(R.id.background);
			size=(TextView)v.findViewById(R.id.download_success_view_size);
			title=(TextView)v.findViewById(R.id.download_success_view_title);
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
