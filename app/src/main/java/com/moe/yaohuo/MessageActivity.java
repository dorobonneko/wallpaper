package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.moe.entity.MsgItem;
import java.util.ArrayList;
import android.support.v7.widget.RecyclerView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.adapter.MessageAdapter;
import java.util.List;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import org.jsoup.nodes.Document;
import java.io.IOException;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.os.Handler;
import android.os.Message;
import com.moe.view.Divider;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.widget.Toast;

public class MessageActivity extends EventActivity implements SwipeRefreshLayout.OnRefreshListener,MessageAdapter.OnItemClickListener,MessageAdapter.OnDeleteListener
{
	private ArrayList<MsgItem> list;
	private SwipeRefreshLayout refresh;
	private MessageAdapter ma;
	private int page=1;
	private boolean canload=true,isFirst;
	private int total;
	private AlertDialog clear;
	private boolean[] clear_item=new boolean[3];
	private View progress;
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelableArrayList("list",list);
		outState.putInt("page",page);
		outState.putBoolean("canload",canload);
		outState.putBoolean("isFirst",isFirst);
		outState.putInt("total",total);
		outState.putBooleanArray("clear",clear_item);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("消息");
		LayoutInflater.from(this).inflate(R.layout.list_view,(ViewGroup)findViewById(R.id.main_index),true);
		if(savedInstanceState!=null){
			list=savedInstanceState.getParcelableArrayList("list");
			page=savedInstanceState.getInt("page");
			canload=savedInstanceState.getBoolean("canload");
			isFirst=savedInstanceState.getBoolean("isFirst");
			total=savedInstanceState.getInt("total");
			clear_item=savedInstanceState.getBooleanArray("clear");
		}
		progress=findViewById(R.id.progressbar);
		if(list==null)list=new ArrayList<>();
		refresh=(SwipeRefreshLayout)findViewById(R.id.refresh);
		refresh.setOnRefreshListener(this);
		RecyclerView rv=(RecyclerView)findViewById(R.id.list);
		rv.setLayoutManager(new LinearLayoutManager(this));
		rv.setAdapter(ma=new MessageAdapter(list));
		rv.addOnScrollListener(new Scroll());
		rv.addItemDecoration(new Divider(5,1,5,5,getResources().getDisplayMetrics()));
		//((DefaultItemAnimator)rv.getItemAnimator()).setSupportsChangeAnimations(false);
		rv.setItemAnimator(null);
		ma.setOnItemClickListener(this);
		ma.setOnDeleteListener(this);
		if(list.size()==0)
			onRefresh();
	}

	@Override
	public void onDelete(MessageAdapter sha, final com.moe.adapter.MessageAdapter.ViewHolder vh)
	{
		new AlertDialog.Builder(this).setTitle("确认删除？").setMessage("与该用户的所有会话").setNegativeButton("手滑了", null).setPositiveButton("删除", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
				new Thread(){
					public void run(){
						try
						{
							Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/messagelist_del.aspx")
								.data("action", "godelother")
								.data("siteid", "1000")
								.data("classid", "0")
								.data("id", list.get(vh.getAdapterPosition()).getId()+"")
								.data("page", "1")
								.data("types", "0")
								.data("issystem", "")
								.userAgent(PreferenceUtils.getCookie(getApplicationContext()))
								.cookie(PreferenceUtils.getCookieName(getApplicationContext()),PreferenceUtils.getCookie(getApplicationContext()))
								.get();
								if(doc.text().indexOf("成功")!=-1){
									handler.sendEmptyMessage(1);
									return;
								}
						}
						catch (IOException e)
						{}
						handler.sendEmptyMessage(2);
					}
				}.start();
				}
			}).show();
	}


	

	@Override
	public void onRefresh()
	{
		page=1;
		canload=true;
		isFirst=true;
		loadMore();
	}

	private void loadMore()
	{
		if(!refresh.isRefreshing())
			progress.setVisibility(View.VISIBLE);
		new Thread(){
			public void run(){
				handler.obtainMessage(0,load()).sendToTarget();
			}
		}.start();
	}

	private List<MsgItem> load(){
		Document doc=null;
		try
		{
			doc = Jsoup.connect(PreferenceUtils.getHost(this) + "/bbs/messagelist.aspx")
				.data("action", "class")
				.data("siteid", "1000")
				.data("classid", "0")
				.data("types", "0")
				.data("issystem", "")
				.data("key", "")
				.data("getTotal", "")
				.data("page", page + "")
				.userAgent(PreferenceUtils.getUserAgent())
				.cookie(PreferenceUtils.getCookieName(getApplicationContext()),PreferenceUtils.getCookie(getApplicationContext()))
				.get();
		}
		catch (IOException e)
		{return null;}
		try{
		total=Integer.parseInt(doc.getElementsByAttributeValue("name","getTotal").get(0).attr("value"));
		}catch(Exception e){}
		List<MsgItem> list=new ArrayList<>();
		Elements elements=doc.getElementsByAttributeValueMatching("class","^line(1|2)$");
		for(int i=0;i<elements.size();i++){
			MsgItem mi=new MsgItem();
			Element element=elements.get(i);
			mi.setView(element.getElementsByAttributeValue("alt","新").size());
			Matcher matcher=Pattern.compile("&amp;id=([0-9]{1,})&.*?>(.*?)</a>(.*?)<br>(.*?)\\[",Pattern.DOTALL).matcher(element.toString());
			matcher.find();
			mi.setId(Integer.parseInt(matcher.group(1)));
			mi.setTitle(matcher.group(2));
			mi.setFrom(matcher.group(3));
			mi.setTime(matcher.group(4));
			mi.setFrom(mi.getFrom().substring(0,mi.getFrom().length()-2));
			list.add(mi);
		}
		return list;
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					if(msg.obj!=null){
						page++;
						int size=list.size();
						if(isFirst){
							isFirst=false;
							list.clear();
							ma.notifyItemRangeRemoved(0,size);
						}
						size=list.size();
						list.addAll((List)msg.obj);
						ma.notifyItemRangeInserted(size,list.size()-size);
						canload=list.size()<total;
					}
					refresh.setRefreshing(false);
					progress.setVisibility(View.INVISIBLE);
					break;
					case 1:
						onRefresh();
						break;
					case 2:
						Toast.makeText(getApplicationContext(),"删除失败",Toast.LENGTH_SHORT).show();
						break;
			}
		}
		
	};
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.clear:
				if (clear == null)clear = new AlertDialog.Builder(this).setTitle("选中需要清除的项目").setMultiChoiceItems(new String[]{"清空聊天消息","清空系统消息","清空收藏消息"}, clear_item, new DialogInterface.OnMultiChoiceClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2, boolean p3)
							{
								clear_item[p2]=p3;
							}
						}).setPositiveButton("确定", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								clear();
							}
						}).setNegativeButton("取消", null).create();
						clear.show();
			break;
			default:
		return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.clear,menu);
		return true;
	}
	private class Scroll extends RecyclerView.OnScrollListener
	{

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy)
		{
			LinearLayoutManager llm=(LinearLayoutManager) recyclerView.getLayoutManager();
			if(canload&&!refresh.isRefreshing()&&progress.getVisibility()!=View.VISIBLE&&llm.findLastVisibleItemPosition()>=list.size()-3)
				loadMore();
		}
		
	}
	private void clear(){
		new Thread(){
			public void run(){
				for(int i=0;i<clear_item.length;i++)
				if(clear_item[i]){
					try
					{
						Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/messagelist_del.aspx")
							.data("action", "godelall")
							.data("siteid", "1000")
							.data("classid", "0")
							.data("id", "0")
							.data("page", "1")
							.data("types", "0")
							.data("issystem", i + "")
							.userAgent(PreferenceUtils.getUserAgent())
							.cookie(PreferenceUtils.getCookieName(getApplicationContext()),PreferenceUtils.getCookie(getApplicationContext())).execute();
					}
					catch (IOException e)
					{}
				}
				handler.sendEmptyMessage(1);
			}
		}.start();
	}

	@Override
	public void onItemClick(RecyclerView.Adapter ra, RecyclerView.ViewHolder vh)
	{
		list.get(vh.getAdapterPosition()).setView(0);
		ra.notifyItemChanged(vh.getAdapterPosition());
		startActivity(new Intent(this,MessageViewActivity.class).putExtra("id",list.get(vh.getAdapterPosition()).getId()));
	}

	
}
