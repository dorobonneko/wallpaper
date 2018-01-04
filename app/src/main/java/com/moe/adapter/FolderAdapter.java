package com.moe.adapter;
import android.view.View;
import java.util.List;
import java.io.File;
import android.view.ViewGroup;
import com.moe.yaohuo.R;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
public class FolderAdapter extends LoadMoreAdapter<FolderAdapter.ViewHolder>
{

	
	private List<File> list;
	public FolderAdapter(List<File> list){
		this.list=list;
	}
	@Override
	public FolderAdapter.ViewHolder onCreateViewHolderSub(ViewGroup p1, int p2)
	{
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.folder_item_view,p1,false));
	}

	@Override
	public void onBindViewHolderSub(FolderAdapter.ViewHolder p1, int p2)
	{
		p1.name.setText(list.get(p2).getName());
		if(list.get(p2).isDirectory())
			p1.icon.setImageResource(R.drawable.folder);
			else
			p1.icon.setImageResource(R.drawable.file);
	}

	@Override
	public int getItemCountSub()
	{
		return list.size();
	}
	public class ViewHolder extends LoadMoreAdapter.ViewHolder implements View.OnClickListener{
		TextView name;
		ImageView icon;
		public ViewHolder(View v){
			super(v);
			v.setOnClickListener(this);
			name=(TextView)v.findViewById(R.id.folder_item_view_name);
			icon=(ImageView)v.findViewById(R.id.folder_item_view_icon);
		}

		@Override
		public void onClick(View p1)
		{
			if(oicl!=null)oicl.onItemClick(FolderAdapter.this,this);
		}

		
	}
	public void setOnItemClickListener(OnItemClickListener l){
		oicl=l;
	}
OnItemClickListener oicl;
public abstract interface OnItemClickListener{
	void onItemClick(RecyclerView.Adapter ra,RecyclerView.ViewHolder vh);
}
}
