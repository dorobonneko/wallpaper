package com.moe.fragment;
import java.util.ArrayList;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import com.moe.yaohuo.R;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.view.Divider;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import android.os.Handler;
import java.io.IOException;
import android.os.Message;
import com.moe.adapter.ActivedAdapter;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.text.Html;

public class ActivedFragment extends UserSpaceFragment implements SwipeRefreshLayout.OnRefreshListener
{
	private ArrayList<CharSequence> list;
	private ActivedAdapter activedAdapter;
	private SwipeRefreshLayout refresh;
	private int id,page=1,total;
	private boolean canload,first;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if(savedInstanceState!=null){
			list=savedInstanceState.getParcelableArrayList("list");
			id=savedInstanceState.getInt("id");
			page=savedInstanceState.getInt("page");
			total=savedInstanceState.getInt("total");
			canload=savedInstanceState.getBoolean("canload");
			first=savedInstanceState.getBoolean("first");
		}else{
			id=getArguments().getInt("uid");
		}
		return inflater.inflate(R.layout.list_view,container,false);
	}

	@Override
	protected void onStateChanged(boolean state)
	{
		refresh.setEnabled(state);
	}


	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putCharSequenceArrayList("list",list);
		outState.putInt("id",id);
		outState.putInt("page",page);
		outState.putInt("total",total);
		outState.putBoolean("canload",canload);
		outState.putBoolean("first",first);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		refresh=(SwipeRefreshLayout)view.findViewById(R.id.refresh);
		refresh.setOnRefreshListener(this);
		RecyclerView rv=(RecyclerView)refresh.getChildAt(1);
		rv.setLayoutManager(new LinearLayoutManager(getContext()));
		if(list==null)list=new ArrayList<>();
		rv.setAdapter(activedAdapter=new ActivedAdapter(list));
		rv.addOnScrollListener(new Scroll());
		rv.addItemDecoration(new Divider(getResources().getDimensionPixelSize(R.dimen.cellSpacing)));
		rv.setItemAnimator(null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		if(list.size()==0)
			onRefresh();
	}

	@Override
	public void onRefresh()
	{
		page=1;
		first=true;
		canload=true;
		loadMore();

	}
	private void loadMore(){
		refresh.setRefreshing(true);
		new Thread(){
			public void run(){
				handler.obtainMessage(0,load()).sendToTarget();
			}
		}.start();
	}
	private List<CharSequence> load(){
		try
		{
			Document doc=Jsoup.connect(PreferenceUtils.getHost(getContext()) + getResources().getString(R.string.log))
				.data("action", "my")
				.data("siteid", "1000")
				.data("classid", "0")
				.data("touserid", id + "")
				.data("page",page+"")
				.userAgent(PreferenceUtils.getUserAgent())
				.cookie(PreferenceUtils.getCookieName(getContext()), PreferenceUtils.getCookie(getContext())).get();
			try{
				total=Integer.parseInt(doc.getElementsByAttributeValue("name","getTotal").get(0).attr("value"));
			}catch(Exception e){}

			Elements elements=doc.getElementsByAttributeValueStarting("class","line");
			List<CharSequence> list=new ArrayList<>();
			for(int i=0;i<elements.size();i++){
				Element item=elements.get(i);
				list.add(Html.fromHtml(item.html()));
			}
			return list;
		}
		catch (IOException e)
		{}
		return null;
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					if(msg.obj!=null){
						if(first){
							first=false;
							list.clear();
						}
						list.addAll((List)msg.obj);
						activedAdapter.notifyDataSetChanged();
						page++;
						canload=list.size()<total;
					}
					refresh.setRefreshing(false);
					break;
			}
		}

	};
	private class Scroll extends RecyclerView.OnScrollListener
	{

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy)
		{
			if (dy>0&&canload && !refresh.isRefreshing())
			{
				LinearLayoutManager glm=(LinearLayoutManager)recyclerView.getLayoutManager();
				if(glm.findLastVisibleItemPosition() > glm.getItemCount() - 5)loadMore();
			}

		}

	}
}
