package com.moe.fragment;
import android.view.View;
import android.os.Bundle;
import android.view.LayoutInflater;
import com.moe.yaohuo.R;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import org.jsoup.Jsoup;
import android.content.SharedPreferences;
import java.io.IOException;
import android.content.res.Resources.NotFoundException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import com.moe.entity.BbsItem;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Message;
import com.moe.adapter.BbsAdapter;
import android.support.v7.widget.GridLayoutManager;
import com.moe.view.Divider;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.content.Intent;
import com.moe.yaohuo.ListActivity;
import com.moe.utils.BbsUtils;
import android.widget.Toast;
public class BbsFragment extends AnimeFragment implements SwipeRefreshLayout.OnRefreshListener,BbsAdapter.OnItemClickListener
{
	private List<BbsItem> list=new ArrayList<>();
	private BbsAdapter ba;
	private SwipeRefreshLayout srl;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.list_view,container,false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		srl=(SwipeRefreshLayout)view.findViewById(R.id.refresh);
		srl.setOnRefreshListener(this);
		RecyclerView rv=(RecyclerView)srl.getChildAt(1);
		rv.setLayoutManager(new GridLayoutManager(getActivity(),2));
		rv.setAdapter(ba=new BbsAdapter(list=new ArrayList<>()));
		rv.addItemDecoration(new Divider(getResources().getDisplayMetrics()));
		ba.setOnItemClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		if(list.size()==0){
			onRefresh();
			srl.setRefreshing(true);
			}
	}

	@Override
	public void onRefresh()
	{
		new Thread(){
			public void run(){
				
				handler.obtainMessage(0,BbsUtils.getBbs(getActivity())).sendToTarget();
			}
		}.start();
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					srl.setRefreshing(false);
					if(msg.obj!=null){
						list.clear();
						list.addAll((List)msg.obj);
						ba.notifyDataSetChanged();
					}else
					Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
					break;
			}
		}
	
	};

	@Override
	public void onItemClick(RecyclerView.Adapter ra, RecyclerView.ViewHolder vh)
	{
		getActivity().startActivity(new Intent(getActivity(),ListActivity.class).putExtra("bbs",list.get(vh.getAdapterPosition())));
	}

	
}
