package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.moe.entity.MoneyItem;
import java.util.List;
import android.widget.TextView;
import com.moe.yaohuo.R;
import android.view.LayoutInflater;
public class MoneyAdapter extends RecyclerView.Adapter<MoneyAdapter.ViewHolder>
{
	private List<MoneyItem> list;
	public MoneyAdapter(List<MoneyItem> list){
		this.list=list;
	}
	@Override
	public MoneyAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		// TODO: Implement this method
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.money_item,p1,false));
	}

	@Override
	public void onBindViewHolder(MoneyAdapter.ViewHolder vh, int p2)
	{
		MoneyItem mi=list.get(p2);
		vh.title.setText(mi.getTitle());
		vh.time.setText(mi.getTime());
		vh.money.setText(mi.getMoney());
		vh.who.setText(mi.getWho());
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return list.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder{
		private TextView title,money,time,who;
		public ViewHolder(View v){
			super(v);
			title=(TextView) v.findViewById(R.id.title);
			money=(TextView) v.findViewById(R.id.money);
			time=(TextView) v.findViewById(R.id.time);
			who=(TextView) v.findViewById(R.id.who);
		}
	}
}
