package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import com.moe.entity.MoneyItem;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.adapter.MoneyAdapter;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import java.io.IOException;
import org.jsoup.nodes.Document;
import android.view.Menu;
import android.support.v7.app.AlertDialog;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import android.os.Handler;
import android.os.Message;
import android.content.DialogInterface;
import android.widget.TextView;
import java.util.Calendar;
import com.moe.app.YearDialog;

public class MoneyActivity extends EventActivity implements SwipeRefreshLayout.OnRefreshListener,YearDialog.OnOkListener
{
	private SwipeRefreshLayout refresh;
	private ArrayList<MoneyItem> list;
	private MoneyAdapter ma;
	private int page,total;
	private boolean canload;
	private YearDialog ad;
	private int[] date;
	private View progress;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if(savedInstanceState!=null){
			list=savedInstanceState.getParcelableArrayList("list");
			page=savedInstanceState.getInt("page");
			total=savedInstanceState.getInt("total");
			canload=savedInstanceState.getBoolean("canload");
			date=savedInstanceState.getIntArray("date");
		}
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("交易详单");
		LayoutInflater.from(this).inflate(R.layout.list_view,(ViewGroup)findViewById(R.id.main_index),true);
		progress=findViewById(R.id.progressbar);
		refresh=(SwipeRefreshLayout)findViewById(R.id.refresh);
		refresh.setOnRefreshListener(this);
		RecyclerView rv=(RecyclerView)findViewById(R.id.list);
		rv.setLayoutManager(new LinearLayoutManager(this));
		rv.addOnScrollListener(new Scroll());
		if(list==null)list=new ArrayList<>();
		rv.setAdapter(ma=new MoneyAdapter(list));
		if(list.size()==0)
			onRefresh();
	}

	@Override
	public void ok(int year, int month)
	{
		if(date==null)date=new int[2];
		date[0]=year;
		date[1]=month+1;
		onRefresh();
	}


	@Override
	public void onRefresh()
	{
		canload=true;
		page=1;
		loadMore();
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					progress.setVisibility(View.INVISIBLE);
					refresh.setRefreshing(false);
					if(msg.obj!=null){
						int size=list.size();
						if(page==1){
							list.clear();
							ma.notifyItemRangeRemoved(0,size);
							}
							size=list.size();
							list.addAll((ArrayList)msg.obj);
							ma.notifyItemRangeInserted(size,list.size()-size);
						page++;
						canload=list.size()<total&&total!=0;
					}
					break;
			}
		}
	
};
	private void loadMore()
	{
		if(!refresh.isRefreshing())
		progress.setVisibility(View.VISIBLE);
		new Thread(){
			public void run(){
				try
				{
					Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/banklist.aspx")
						.data("action", "search")
						.data("siteid", "1000")
						.data("getTotal", "")
						.data("page", page + "")
						.data("key", PreferenceUtils.getUid(getApplicationContext()) + "")
						.data("toyear", (date==null?Calendar.getInstance().get(Calendar.YEAR):date[0])+"")
						.data("tomonth",(date==null?Calendar.getInstance().get(Calendar.MONTH)+1:date[1])+"")
						.userAgent(PreferenceUtils.getUserAgent())
						.cookie(PreferenceUtils.getCookieName(getApplicationContext()),PreferenceUtils.getCookie(getApplicationContext())).get();
					try{
						total=Integer.parseInt(doc.getElementsByAttributeValue("name","getTotal").get(0).attr("value"));
					}catch(Exception e){}
						Elements elements=doc.getElementsByTag("tr");
						ArrayList list=new ArrayList();
						for(int i=1;i<elements.size();i++){
							MoneyItem mi=new MoneyItem();
							Element item=elements.get(i);
							Elements td=item.getElementsByTag("td");
							mi.setTitle(td.get(0).text());
							mi.setMoney(td.get(1).text());
							mi.setWho(td.get(2).text());
							mi.setTime(td.get(3).text());
							list.add(mi);
						}
						handler.obtainMessage(0,list).sendToTarget();
						return;
				}
				catch (IOException e)
				{}
				handler.sendEmptyMessage(0);
			}
		}.start();
	}


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0,0,0,"filter");
		menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_filter_variant)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case 0:
				if(ad==null){
					ad=new YearDialog(this);
					ad.setOnOkListener(this);
				}
				ad.show();
				break;
			default:
		return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putIntArray("date",date);
		outState.putParcelableArrayList("list",list);
		outState.putInt("page",page);
		outState.putInt("total",total);
		outState.putBoolean("canload",canload);
		super.onSaveInstanceState(outState);
	}
	private class Scroll extends RecyclerView.OnScrollListener
	{

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy)
		{
			if (canload && !refresh.isRefreshing()&&progress.getVisibility()!=View.VISIBLE)
			{
				LinearLayoutManager ll=(LinearLayoutManager) recyclerView.getLayoutManager();
				if(ll.findLastVisibleItemPosition() > ll.getItemCount() - 4)loadMore();
			}
			
		}

	}
}
