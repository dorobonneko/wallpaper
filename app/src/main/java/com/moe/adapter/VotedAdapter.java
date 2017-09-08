package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import java.util.List;
import android.widget.TextView;
import android.view.View;
import com.moe.yaohuo.R;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.moe.entity.RadioItem;
import java.text.DecimalFormat;
import com.moe.widget.ProgressBar;
public class VotedAdapter extends RecyclerView.Adapter<VotedAdapter.ViewHolder>
{
	private DecimalFormat df=new DecimalFormat("0.00");
	private List<RadioItem> list;
	public VotedAdapter(List<RadioItem> list){
		this.list=list;
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.radio_item,p1,false));
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int p2)
	{
		RadioItem bi=list.get(p2);
		vh.title.setText(bi.getTitle());
		vh.summary.setText(df.format(((double)bi.getProgress())/bi.getCount()*100)+"%");
		vh.subSummary.setText(bi.getProgress()+"");
		vh.progress.setMax(bi.getCount());
		vh.progress.setProgress(bi.getProgress());
	}

	@Override
	public int getItemCount()
	{
		return list.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		TextView title,summary,subSummary;
		ProgressBar progress;
		View radio;
		public ViewHolder(View v){
			super(v);
			title=(TextView)v.findViewById(android.R.id.title);
			summary=(TextView)v.findViewById(android.R.id.summary);
			subSummary=(TextView)v.findViewById(R.id.summary);
			progress=(ProgressBar)v.findViewById(R.id.progressbar);
			//radio=((ViewGroup)v).getChildAt(0);
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			if(oicl!=null)oicl.onItemClick(VotedAdapter.this,this);
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
