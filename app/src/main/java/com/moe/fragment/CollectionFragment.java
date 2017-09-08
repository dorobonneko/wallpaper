package com.moe.fragment;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import com.moe.yaohuo.R;
import android.support.v4.widget.SwipeRefreshLayout;
import com.moe.adapter.CollectionAdapter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.entity.CollectionItem;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import org.jsoup.nodes.Document;
import java.io.IOException;
import org.jsoup.select.Elements;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.moe.adapter.CollectionAdapter.ViewHolder;
import com.moe.yaohuo.WebViewActivity;
import android.content.Intent;
import android.net.Uri;
import com.moe.entity.ListItem;
import com.moe.yaohuo.BbsActivity;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
public class CollectionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,CollectionAdapter.OnItemClickListener,CollectionAdapter.OnDeleteListener
{
	private SwipeRefreshLayout refresh;
	private int page=1,total=-1;
	private CollectionAdapter ca;
	private ArrayList<CollectionItem> list;
	private boolean canload=true,isfirst;
	private AlertDialog ad;
	private View progress;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if(savedInstanceState!=null){
			list=savedInstanceState.getParcelableArrayList("list");
			page=savedInstanceState.getInt("page");
			total=savedInstanceState.getInt("total");
			canload=savedInstanceState.getBoolean("canload");
			isfirst=savedInstanceState.getBoolean("isfirst");
		}
		return inflater.inflate(R.layout.list_view, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		progress=view.findViewById(R.id.progressbar);
		refresh = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
		refresh.setOnRefreshListener(this);
		RecyclerView rv=(RecyclerView)refresh.getChildAt(1);
		rv.setLayoutManager(new LinearLayoutManager(getContext()));
		if (list == null)list = new ArrayList<>();
		rv.setAdapter(ca = new CollectionAdapter(list));
		ca.setOnItemClickListener(this);
		ca.setOnDeleteListener(this);
		rv.addOnScrollListener(new Scroll());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		if (list.size() == 0){
			refresh.setRefreshing(true);
			onRefresh();
			}
	}

	@Override
	public void onItemClick(CollectionAdapter sha, CollectionAdapter.ViewHolder vh)
	{
		String url=list.get(vh.getAdapterPosition()).getSummary();
		if(url.matches("^http.?://.*?")){
			getActivity().startActivity(new Intent(getContext(),WebViewActivity.class).setData(Uri.parse(url)));
		}else if(url.startsWith("/")){
			Matcher m=Pattern.compile("/bbs(-|/book_view.aspx|/view.aspx)(\\?.*?&(amp;|)id=|\\?id=|)(\\d+)").matcher(url);
			if(m.find()){
				ListItem bbs=new ListItem();
				bbs.setId(Integer.parseInt(m.group(4)));
				getActivity().startActivity(new Intent(getContext(), BbsActivity.class).putExtra("bbs", bbs));

			}
		}
	}

	@Override
	public void onDelete(CollectionAdapter sha, final CollectionAdapter.ViewHolder vh)
	{
		if(vh.getAdapterPosition()==-1)return;
		new Thread(){
			public void run(){
				try
				{
					Document doc=Jsoup.connect(PreferenceUtils.getHost(getContext()) + "/bbs/favlist_del.aspx")
						.data("action", "godel")
						.data("siteid", "1000")
						.data("classid", "0")
						.data("id", list.get(vh.getAdapterPosition()).getId() + "")
						.data("page", "1")
						.data("favtypeid", list.get(vh.getAdapterPosition()).getTypeid() + "")
						.userAgent(PreferenceUtils.getUserAgent())
						.cookie(PreferenceUtils.getCookieName(getContext()),PreferenceUtils.getCookie(getContext())).get();
						if(doc.text().indexOf("成功")!=-1){
							handler.obtainMessage(2,vh.getAdapterPosition()).sendToTarget();
							return;
						}
				}
				catch (IOException e)
				{}
				handler.sendEmptyMessage(0);
			}
		}.start();
	}



	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelableArrayList("list",list);
		outState.putInt("page",page);
		outState.putInt("total",total);
		outState.putBoolean("canload",canload);
		outState.putBoolean("isfirst",isfirst);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRefresh()
	{
		page = 1;
		canload = true;
		isfirst = true;
		loadMore();
	}
	public void loadMore()
	{
		total=-1;
		if(!refresh.isRefreshing())
		progress.setVisibility(View.VISIBLE);
		new Thread(){
			public void run()
			{
				try
				{
					Document doc=Jsoup.connect(PreferenceUtils.getHost(getContext()) + "/bbs/favlist.aspx")
						.data("siteid", "1000")
						.data("classid", "0")
						.data("page",page+"")
						.userAgent(PreferenceUtils.getUserAgent())
						.cookie(PreferenceUtils.getCookieName(getContext()),PreferenceUtils.getCookie(getContext())).get();
					Elements elements=doc.getElementsByAttributeValueMatching("class", "line(1|2)");
					try
					{
						total = Integer.parseInt(doc.getElementsByAttributeValue("name", "getTotal").get(0).attr("value"));
					}
					catch (Exception e)
					{}
					Pattern pattern=Pattern.compile("\"(.*?)\">(.*?)<.*?favtypeid=(\\d*).*?id=(\\d*)", Pattern.DOTALL);
					List<CollectionItem> list=new ArrayList<>();
					for (int i=0;i < elements.size();i++)
					{
						Matcher matcher=pattern.matcher(elements.get(i).html());
						if (matcher.find())
						{
							CollectionItem ci=new CollectionItem();
							ci.setSummary(matcher.group(1));
							ci.setTitle(matcher.group(2));
							ci.setTypeid(Integer.parseInt(matcher.group(3)));
							ci.setId(Integer.parseInt(matcher.group(4)));
							list.add(ci);
						}
					}
					handler.obtainMessage(1, list).sendToTarget();
					return;
				}
				catch (IOException e)
				{}
				handler.sendEmptyMessage(0);
			}
		}.start();
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					progress.setVisibility(View.INVISIBLE);
					refresh.setRefreshing(false);
					Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
					break;
				case 1:
					progress.setVisibility(View.INVISIBLE);
					refresh.setRefreshing(false);
					if (msg.obj != null)
					{
						page++;
						if (isfirst)
						{
							isfirst=false;
							list.clear();
						}
						list.addAll((List)msg.obj);
						ca.notifyDataSetChanged();
						canload = list.size() < total;
					
					}else
					sendEmptyMessage(0);
					break;
				case 2:
					list.remove(((Integer)msg.obj).intValue());
					ca.notifyItemRemoved(msg.obj);
					break;
				case 3:
					Toast.makeText(getContext(),"删除失败",Toast.LENGTH_SHORT).show();
					break;
				case 4:
					Toast.makeText(getActivity(),"添加失败",Toast.LENGTH_SHORT).show();
					break;
				case 5:
					onRefresh();
					break;
			}
		}

	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.add(0,0,0,"add");
		menu.getItem(0).setIcon(R.drawable.plus).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case 0:
				if(ad==null){
					View v=LayoutInflater.from(getContext()).inflate(R.layout.book_view,null);
					final EditText title=(EditText) v.findViewById(android.R.id.title);
					final EditText summary=(EditText) v.findViewById(android.R.id.summary);
					ad = new AlertDialog.Builder(getActivity()).setTitle("新建收藏").setView(v).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								boolean flag=true;
								if(title.getText().toString().trim().length()==0){
									flag=false;
								}
								if(summary.getText().toString().trim().length()==0){
									flag=false;
								}
								if(flag){
									new Thread(){
										public void run(){
											try
											{
												Document doc=Jsoup.connect(PreferenceUtils.getHost(getContext()) + "/bbs/favlist_add.aspx")
													.data("favtypeid", "0")
													.data("title", title.getText().toString().trim())
													.data("url", summary.getText().toString().trim())
													.data("action", "goadd")
													.data("siteid", "1000")
													.data("classid", "0")
													.data("sid", PreferenceUtils.getCookie(getContext()))
													.userAgent(PreferenceUtils.getUserAgent())
													.cookie(PreferenceUtils.getCookieName(getContext()), PreferenceUtils.getCookie(getContext())).post();
													if(doc.text().indexOf("成功")!=-1)
														handler.sendEmptyMessage(5);
														else
														handler.sendEmptyMessage(4);
													return;
											}
											catch (IOException e)
											{}
											handler.sendEmptyMessage(4);
										}
									}.start();
								}else
								handler.sendEmptyMessage(4);
							}
						}).create();
				}
				ad.show();
				break;
		}
		return true;
	}
	private class Scroll extends RecyclerView.OnScrollListener
	{

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy)
		{
			LinearLayoutManager llm=(LinearLayoutManager) recyclerView.getLayoutManager();
			if(canload&&!refresh.isRefreshing()&&progress.getVisibility()!=View.VISIBLE&&llm.findLastVisibleItemPosition()>llm.getItemCount()-3)loadMore();
			
		}
		
	}
}
