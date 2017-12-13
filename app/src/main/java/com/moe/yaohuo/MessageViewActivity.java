package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.view.MenuItem;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import org.jsoup.nodes.Document;
import java.io.IOException;
import org.jsoup.select.Elements;
import com.moe.entity.ListItem;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import com.moe.entity.FloorItem;
import com.moe.utils.UserUtils;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.adapter.FloorAdapter;
import com.moe.view.Divider;
import android.widget.ImageView;
import android.view.View;
import android.content.Intent;
import com.moe.entity.UserItem;
import android.widget.Toast;
import com.moe.utils.StringUtils;

public class MessageViewActivity extends EventActivity implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener
{
	private int id,uid;
	private SwipeRefreshLayout refresh;
	private ArrayList<FloorItem> list;
	private FloorAdapter fa;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("查看消息");
		ImageView iv=(ImageView)findViewById(R.id.edit);
		iv.setVisibility(iv.VISIBLE);
		iv.setImageResource(R.drawable.reply);
		iv.setOnClickListener(this);
		LayoutInflater.from(this).inflate(R.layout.list_view,(ViewGroup)findViewById(R.id.main_index),true);
		refresh=(SwipeRefreshLayout)findViewById(R.id.refresh);
		refresh.setOnRefreshListener(this);
		RecyclerView rv=(RecyclerView)findViewById(R.id.list);
		rv.setLayoutManager(new LinearLayoutManager(this));
		rv.addItemDecoration(new Divider(getResources().getDisplayMetrics()));
		if(savedInstanceState!=null){
			list=savedInstanceState.getParcelableArrayList("list");
			id=savedInstanceState.getInt("id");
			uid=savedInstanceState.getInt("uid");
		}else{
			id=getIntent().getIntExtra("id",0);
		}
		if(list==null)list=new ArrayList<>();
		ListItem li=new ListItem();
		li.setId(id);
		rv.setAdapter(fa=new FloorAdapter(list,li));
		
		if(list.size()==0){
			refresh.setRefreshing(true);
		onRefresh();
		}
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					if(msg.obj!=null){
						int size=list.size();
						list.clear();
						fa.notifyItemRangeRemoved(0,size);
						list.addAll((List)msg.obj);
						fa.notifyItemRangeInserted(0,list.size());
					}else
					Toast.makeText(getApplicationContext(),"加载失败",Toast.LENGTH_SHORT).show();
					refresh.setRefreshing(false);
					break;
			}
		}
		
	};

	@Override
	public void onRefresh()
	{
		new Thread(){
			public void run(){
				handler.obtainMessage(0,load()).sendToTarget();
			}
		}.start();
	}

	
	
	private List<FloorItem> load(){
		
		try
		{
			Document doc=Jsoup.connect(PreferenceUtils.getHost(this) + "/bbs/messagelist_view.aspx")
				.data("siteid", "1000")
				.data("classid", "0")
				.data("types", "0")
				.data("issystem", "")
				.data("id", "" + id)
				.data("page", "1")
				.userAgent(PreferenceUtils.getUserAgent())
				.cookie(PreferenceUtils.getCookieName(getApplicationContext()),PreferenceUtils.getCookie(this)).get();
				Elements elements=doc.getElementsByAttributeValueMatching("class","the_(me|user)");
				List<FloorItem> list=new ArrayList<>();
				for(int i=0;i<elements.size();i++){
					Element element=elements.get(i);
					FloorItem fi=new FloorItem();
					fi.setContent(StringUtils.direct(getApplicationContext(),element.getElementsByClass("con").get(0).html()));
					element=element.getElementsByClass("u_name").get(0);
					Element name=element.child(0);
					if(name.tagName().equals("a")){
					fi.setUser(UserUtils.getUserInfo(getApplicationContext(),Integer.parseInt(name.text().substring(name.text().lastIndexOf("(")+1,name.text().lastIndexOf(")")))));
					if(fi.getUser()!=null)uid=fi.getUser().getUid();
					fi.setTime(element.child(1).text());
					}else{
						UserItem ui=new UserItem();
						ui.setName("我");
						fi.setUser(ui);
						fi.setTime(element.child(0).text());
					}
					list.add(fi);
				}
				return list;
		}
		catch (IOException e)
		{}
		return null;
	}

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.edit:
				if(list.size()>0)
				startActivityForResult(new Intent(this,SendMessageActivity.class).putExtra("uid",uid),452);
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode==452&&resultCode==RESULT_OK){
			refresh.setRefreshing(true);
			onRefresh();
		}
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelableArrayList("list",list);
		outState.putInt("id",id);
		outState.putInt("uid",uid);
		super.onSaveInstanceState(outState);
	}

	
}
