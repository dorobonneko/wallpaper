package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class EventAdapter<T extends EventAdapter.ViewHolder> extends RecyclerView.Adapter<T>
{ 
	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
		private EventAdapter ea;
		public ViewHolder(EventAdapter ea,View v){
			super(v);
			this.ea=ea;
			if(ea.getItemClickListener()!=null)
			v.setOnClickListener(this);
			if(ea.getItemLongClickListener()!=null)
				v.setOnLongClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			if(ea.getItemClickListener()!=null)
				ea.getItemClickListener().onItemClick(ea,this);
		}

		@Override
		public boolean onLongClick(View p1)
		{
			return ea.getItemLongClickListener()!=null&&ea.getItemLongClickListener().onItemLongClick(ea,this);
		}


		
	}
	private OnItemClickListener oicl;
	public void setOnItemClickListener(OnItemClickListener l){
		oicl=l;
	}
	public abstract interface OnItemClickListener{
		void onItemClick(EventAdapter ra,ViewHolder vh);
	}
	private OnItemLongClickListener oilcl;
	public void setOnItemLongClickListener(OnItemLongClickListener l){
		oilcl=l;
	}
	public abstract interface OnItemLongClickListener{
		boolean onItemLongClick(EventAdapter ra,ViewHolder vh);
	}
	private OnItemClickListener getItemClickListener(){
		return oicl;
	}
	private OnItemLongClickListener getItemLongClickListener(){
		return oilcl;
	}
}
