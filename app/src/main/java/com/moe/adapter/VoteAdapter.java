package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.moe.yaohuo.R;
import java.util.List;
import java.util.ArrayList;
import com.moe.entity.ListItem;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.ViewHolder>
{
	private List<Vote> list=new ArrayList<>();
	public void add(){
		list.add(new Vote());
		notifyItemInserted(list.size()-1);
	}
	public List<Vote> getList(){
		return list;
	}
	@Override
	public VoteAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		// TODO: Implement this method
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.vote_item,p1,false));
	}

	@Override
	public void onBindViewHolder(VoteAdapter.ViewHolder p1, int p2)
	{
		p1.value.setHint("投票项"+(p2+1));
		p1.value.setText(list.get(p2).getValue());
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return list.size();
	}

	@Override
	public int getItemViewType(int position)
	{
		return position;
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,TextWatcher
	{
		EditText value;
		@Override
		public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
		{
			// TODO: Implement this method
		}

		@Override
		public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
		{
			// TODO: Implement this method
		}

		@Override
		public void afterTextChanged(Editable p1)
		{
			list.get(getAdapterPosition()).setValue(p1.toString());
		}

		public ViewHolder(View v){
			super(v);
			value=((EditText)v.findViewById(R.id.vote_value));
			value.addTextChangedListener(this);
			v.findViewById(R.id.vote_delete).setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			list.remove(getAdapterPosition());
			notifyItemRemoved(getAdapterPosition());
		}

		
	}
	public class Vote{
		private String value;


		public void setValue(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}}
}
