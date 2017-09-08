package com.moe.fragment;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import com.moe.yaohuo.R;
import android.support.v4.widget.SwipeRefreshLayout;
import com.moe.entity.ReplyItem;
import java.util.List;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.os.Handler;
import android.os.Message;
import com.moe.adapter.ReplyAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.moe.view.Divider;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.content.Intent;
import com.moe.yaohuo.BbsActivity;
import com.moe.entity.ListItem;
import android.support.v7.widget.DefaultItemAnimator;
public class ReplyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,ReplyAdapter.OnItemClickListener
{
	private ArrayList<ReplyItem> list;
	private ReplyAdapter ra;
	private SwipeRefreshLayout refresh;
	private int id,page=1,total;
	private boolean canload=true,first;
	private View progress;
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
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelableArrayList("list",list);
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
		progress=view.findViewById(R.id.progressbar);
		refresh=(SwipeRefreshLayout)view.findViewById(R.id.refresh);
		refresh.setOnRefreshListener(this);
		RecyclerView rv=(RecyclerView)refresh.getChildAt(1);
		rv.setLayoutManager(new LinearLayoutManager(getContext()));
		if(list==null)list=new ArrayList<>();
		rv.setAdapter(ra=new ReplyAdapter(list));
		rv.addOnScrollListener(new Scroll());
		rv.addItemDecoration(new Divider(5,1,5,5,getResources().getDisplayMetrics()));
		((DefaultItemAnimator)rv.getItemAnimator()).setSupportsChangeAnimations(false);
		ra.setOnItemClickListener(this);
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
		if(!refresh.isRefreshing())
		progress.setVisibility(View.VISIBLE);
		new Thread(){
			public void run(){
				handler.obtainMessage(0,load()).sendToTarget();
			}
		}.start();
	}
	private List<ReplyItem> load(){
		try
		{
			Document doc=Jsoup.connect(PreferenceUtils.getHost(getContext()) + "/bbs/book_re_my.aspx")
				.data("action", "class")
				.data("siteid", "1000")
				.data("classid", "0")
				.data("touserid", id + "")
				.data("page",page+"")
				.userAgent(PreferenceUtils.getUserAgent())
				.cookie(PreferenceUtils.getCookieName(getContext()), PreferenceUtils.getCookie(getContext())).get();
			try{
				total=Integer.parseInt(doc.getElementsByAttributeValue("name","getTotal").get(0).attr("value"));
			}catch(Exception e){}
			
				Elements elements=doc.getElementsByAttributeValueMatching("class","^line(1|2)$");
				List<ReplyItem> list=new ArrayList<>();
				for(int i=0;i<elements.size();i++){
					ReplyItem ri=new ReplyItem();
					Matcher matcher=Pattern.compile("(?s)([0-9]{1,}).*?<a.*：(.*)<br>(.*?)<a.*?bbs-([0-9]{1,}).html",Pattern.DOTALL).matcher(elements.get(i).html());
					matcher.find();
					ri.setTitle(matcher.group(1)+"."+matcher.group(2));
					ri.setSummary(matcher.group(3));
					ri.setId(Integer.parseInt(matcher.group(4)));
					list.add(ri);
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
						ra.notifyDataSetChanged();
						page++;
						canload=list.size()<total;
					}
					progress.setVisibility(View.INVISIBLE);
					refresh.setRefreshing(false);
					break;
			}
		}
		
	};

	@Override
	public void onItemClick(RecyclerView.Adapter ra, RecyclerView.ViewHolder vh)
	{
		ListItem li=new ListItem();
		li.setId(list.get(vh.getAdapterPosition()).getId());
		getActivity().startActivity(new Intent(getContext(),BbsActivity.class).putExtra("bbs",li));
	}

	
	private class Scroll extends RecyclerView.OnScrollListener
	{

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy)
		{
			if (canload && !refresh.isRefreshing()&&progress.getVisibility()!=View.VISIBLE)
			{
				LinearLayoutManager glm=(LinearLayoutManager)recyclerView.getLayoutManager();
				if(glm.findLastVisibleItemPosition() > glm.getItemCount() - 4)loadMore();
			}
			
		}

	}
}
