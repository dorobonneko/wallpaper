package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import java.util.List;
import android.view.View;
import android.widget.TextView;
import com.moe.yaohuo.R;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import java.util.ArrayList;
import com.moe.entity.FileItem;
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder>
{
	private List<FileItem> list;
	public FileAdapter(List<FileItem> list){
		this.list=list;
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{

		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.file_item,p1,false));
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int p2)
	{
		vh.title.setText(list.get(p2).getName());
	}

	@Override
	public int getItemCount()
	{
		return list.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		TextView title;
		public ViewHolder(View v){
			super(v);
			title=(TextView)v.findViewById(android.R.id.title);
			v.findViewById(R.id.delete).setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			list.remove(getAdapterPosition());
			notifyItemRemoved(getAdapterPosition());
			
		}


	}
	
}
