package com.moe.fragment;
import android.support.v4.app.Fragment;
import com.moe.yaohuo.R;
import android.view.LayoutInflater;
import android.view.View;
import java.util.ArrayList;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ViewGroup;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import com.moe.view.Divider;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import org.jsoup.select.Elements;
import android.os.Handler;
import java.io.IOException;
import android.os.Message;
import com.moe.entity.PictureItem;
import com.moe.adapter.PictureAdapter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;
public class PictureAlbum extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{
	private ArrayList<PictureItem> list;
	private PictureAdapter pa;
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
		refresh=(SwipeRefreshLayout)view.findViewById(R.id.refresh);
		refresh.setOnRefreshListener(this);
		RecyclerView rv=(RecyclerView)refresh.getChildAt(1);
		rv.setLayoutManager(new GridLayoutManager(getContext(),2));
		if(list==null)list=new ArrayList<>();
		rv.setAdapter(pa=new PictureAdapter(list));
		rv.addOnScrollListener(new Scroll());
		rv.addItemDecoration(new Divider(0x00000000,3,getResources().getDisplayMetrics()));
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
	private List<PictureItem> load(){
		try
		{
			Document doc=Jsoup.connect(PreferenceUtils.getHost(getContext()) + "/album/myalbum.aspx")
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

			Elements elements=doc.getElementsByClass("content").get(0).children();
			List<PictureItem> list=new ArrayList<>();
			for(int i=0;i<elements.size()-1;i++){
				PictureItem ri=new PictureItem();
				Element item=elements.get(i);
				Matcher matcher=Pattern.compile("&id=([0-9]{1,})").matcher(item.attr("href"));
				matcher.find();
				ri.setId(Integer.parseInt(matcher.group(1)));
				item=item.child(0);
				ri.setUrl(item.absUrl("src"));
				ri.setTitle(item.attr("alt"));
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
						pa.notifyDataSetChanged();
						page++;
						//canload=list.size()<total;
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
				GridLayoutManager glm=(GridLayoutManager)recyclerView.getLayoutManager();
				if(glm.findLastVisibleItemPosition() > glm.getItemCount() - 5)loadMore();
			}

		}

	}
	}
