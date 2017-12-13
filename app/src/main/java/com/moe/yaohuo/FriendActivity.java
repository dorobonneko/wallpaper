package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.WindowManager;
import android.support.v7.widget.Toolbar;
import com.moe.entity.FriendItem;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.support.v4.widget.SwipeRefreshLayout;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import android.net.Uri;
import com.moe.utils.UserUtils;
import java.util.List;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.adapter.FriendAdapter;
import android.os.Message;
import android.widget.Toast;
import com.moe.view.Divider;
import com.moe.adapter.FriendAdapter.ViewHolder;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

public class FriendActivity extends EventActivity implements SwipeRefreshLayout.OnRefreshListener,FriendAdapter.OnItemClickListener,FriendAdapter.OnDeleteListener
{
	private SwipeRefreshLayout refresh;
	private int type,page,total;
	private boolean canload,isfirst;
	private ArrayList<FriendItem> list;
	private FriendAdapter fa;
	private View progress;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.list_view,(ViewGroup)findViewById(R.id.main_index),true);
		progress=findViewById(R.id.progressbar);
		refresh=(SwipeRefreshLayout)findViewById(R.id.refresh);
		refresh.setOnRefreshListener(this);
		RecyclerView rv=(RecyclerView)refresh.getChildAt(1);
		rv.addOnScrollListener(new Scroll());
		rv.setLayoutManager(new LinearLayoutManager(this));
		rv.addItemDecoration(new Divider(0xffaaaaaa,0,0,0,1,getResources().getDisplayMetrics()));
		if(savedInstanceState!=null){
			type=savedInstanceState.getInt("type");
			list=savedInstanceState.getParcelableArrayList("list");
			page=savedInstanceState.getInt("page");
			total=savedInstanceState.getInt("total");
			canload=savedInstanceState.getBoolean("canload");
			isfirst=savedInstanceState.getBoolean("isfirst");
		}else{
			type=getIntent().getAction().equals("white")?0:1;
		}
		if(type==0)
			getSupportActionBar().setTitle("好友");
			else
			getSupportActionBar().setTitle("黑名单");
			if(list==null)list=new ArrayList<>();
			rv.setAdapter(fa=new FriendAdapter(list));
			fa.setOnItemClickListener(this);
			fa.setOnDeleteListener(this);
			if(list.size()==0){
				refresh.setRefreshing(true);
			onRefresh();
			}
	}

	@Override
	public void onItemClick(FriendAdapter sha, FriendAdapter.ViewHolder vh)
	{
		try{
		startActivity(new Intent(this,SendMessageActivity.class).putExtra("uid",list.get(vh.getAdapterPosition()).getUi().getUid()));
		}catch(Exception e){}
	}

	@Override
	public void onDelete(FriendAdapter sha, final FriendAdapter.ViewHolder vh)
	{
		final FriendItem fi=list.get(vh.getAdapterPosition());
		new AlertDialog.Builder(this).setTitle("确认删除？").setMessage(fi.getUi()==null?null:fi.getUi().getName()).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					new Thread(){
						public void run(){
							try
							{
								Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/friendlist_del.aspx")
									.data("action", "godel")
									.data("siteid", "1000")
									.data("classid", "0")
									.data("id", fi.getId() + "")
									.data("page", "1")
									.data("friendtype", fi.getType() + "")
									.userAgent(PreferenceUtils.getUserAgent())
									.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext())).get();
									if(doc.text().indexOf("成功")!=-1){
										handler.obtainMessage(3,vh.getAdapterPosition()).sendToTarget();
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
		isfirst=true;
		loadMore();
	}
	private void loadMore(){
		if(!refresh.isRefreshing())
			progress.setVisibility(View.VISIBLE);
		new Thread(){
			public void run(){
				try
				{
					Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/FriendList.aspx")
						.data("siteid", "1000")
						.data("classid", "0")
						.data("friendtype", type + "")
						.data("page", page + "")
						.userAgent(PreferenceUtils.getUserAgent())
						.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext())).get();
					try{
						total=Integer.parseInt(doc.getElementsByAttributeValue("name","getTotal").get(0).attr("value"));
					}catch(Exception e){}
					
						Elements elements=doc.getElementsByAttributeValueMatching("class","line(1|2)");
						List<FriendItem> list=new ArrayList<>();
						for(int i=0;i<elements.size();i++){
							try{
							Element line=elements.get(i);
							FriendItem fi=new FriendItem();
							fi.setType(type);
							fi.setUi(UserUtils.getUserInfo(getApplicationContext(),Integer.parseInt(Uri.parse(line.child(0).attr("href")).getQueryParameter("touserid"))));
							fi.setId(Integer.parseInt(Uri.parse(line.child(2).attr("href")).getQueryParameter("id")));
							fi.setTime(line.childNode(10).toString());
							list.add(fi);
							}catch(Exception e){}
						}
						handler.obtainMessage(1,list).sendToTarget();
						return;
				}
				catch (IOException e)
				{}
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putInt("page",page);
		outState.putInt("total",total);
		outState.putBoolean("canload",canload);
		outState.putBoolean("isfirest",isfirst);
		outState.putParcelableArrayList("list",list);
		outState.putInt("type",type);
		super.onSaveInstanceState(outState);
	}

	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					progress.setVisibility(View.INVISIBLE);
					refresh.setRefreshing(false);
					Toast.makeText(getApplicationContext(),"加载失败",Toast.LENGTH_SHORT).show();
					break;
				case 1:
					refresh.setRefreshing(false);
					progress.setVisibility(progress.INVISIBLE);
					if(msg.obj!=null){
					if(isfirst){
						isfirst=false;
						list.clear();
						}
						list.addAll((List)msg.obj);
						fa.notifyDataSetChanged();
						page++;
						canload=list.size()<total;
						}
					break;
				case 2:
					Toast.makeText(getApplicationContext(),"删除失败",Toast.LENGTH_SHORT).show();
					break;
				case 3:
					list.remove(((Integer)msg.obj).intValue());
					fa.notifyItemRemoved(msg.obj);
					break;
			}
		}
		
	};
	private class Scroll extends RecyclerView.OnScrollListener
	{

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy)
		{
			if (dy>0&&canload && !refresh.isRefreshing()&&progress.getVisibility()!=View.VISIBLE)
			{
				LinearLayoutManager glm=(LinearLayoutManager)recyclerView.getLayoutManager();
				if(glm.findLastVisibleItemPosition() > glm.getItemCount() - 4)loadMore();
			}
		}
		
	}
}
