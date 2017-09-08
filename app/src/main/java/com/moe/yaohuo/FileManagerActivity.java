package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.support.v7.widget.Toolbar;
import com.moe.entity.ListItem;
import android.view.MenuItem;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.moe.entity.CollectionItem;
import java.util.ArrayList;
import java.util.List;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.adapter.CollectionAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;
import com.moe.adapter.CollectionAdapter.ViewHolder;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import com.moe.entity.FileManagerItem;
import android.widget.EditText;
import android.view.View;
import org.jsoup.Connection;

public class FileManagerActivity extends EventActivity implements SwipeRefreshLayout.OnRefreshListener,CollectionAdapter.OnItemClickListener,CollectionAdapter.OnDeleteListener
{
	private SwipeRefreshLayout refresh;
	private ListItem bbs;
	private CollectionAdapter ca;
	private ArrayList<FileManagerItem> list;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("附件管理");
		LayoutInflater.from(this).inflate(R.layout.list_view, (ViewGroup)findViewById(R.id.main_index), true);
		refresh = (SwipeRefreshLayout)findViewById(R.id.refresh);
		refresh.setOnRefreshListener(this);
		RecyclerView rv=(RecyclerView) findViewById(R.id.list);
		rv.setLayoutManager(new LinearLayoutManager(this));
		if (savedInstanceState == null)
		{
			bbs = getIntent().getParcelableExtra("bbs");
			load();
		}
		else
		{
			bbs = savedInstanceState.getParcelable("bbs");
			list = savedInstanceState.getParcelableArrayList("list");
		}
		if (list == null)list = new ArrayList<>();
		rv.setAdapter(ca = new CollectionAdapter(list));
		ca.setOnItemClickListener(this);
		ca.setOnDeleteListener(this);
		if (list.size() == 0)
		{
			refresh.setRefreshing(true);
			load();
		}
	}

	@Override
	public void onRefresh()
	{
		load();
	}

	@Override
	public void onItemClick(CollectionAdapter sha, CollectionAdapter.ViewHolder vh)
	{
		View v=LayoutInflater.from(this).inflate(R.layout.book_view,null);
		final EditText title=(EditText) v.findViewById(android.R.id.title);
		final EditText content=(EditText) v.findViewById(android.R.id.summary);
		title.setHint("标题：");
		content.setHint("文件说明：");
		final FileManagerItem fmi=list.get(vh.getAdapterPosition());
		title.setText(fmi.getTitle());
		content.setText(fmi.getContent());
		new AlertDialog.Builder(this).setTitle("编辑文件").setView(v).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					new Thread(){
						public void run(){
							Connection conn=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext())+"/bbs/book_view_modfile.aspx")
							.data("action","gomod")
							.data("book_id",fmi.getId()+"");
							String name=title.getText().toString().trim();
							int index=name.lastIndexOf(".");
							if(index==-1)
							conn.data("book_file_title",name)
							.data("book_ext","");
							else{
							conn.data("book_file_title",name.substring(0,index))	
							.data("book_ext",name.substring(index+1));
							}
							conn.data("id",bbs.getId()+"")
							.data("book_size",fmi.getSummary())
							.data("book_file_info",content.getText().toString().replaceAll("\n","[br]"))
							.data("classid",bbs.getClassid()+"")
							.data("siteid","1000")
							.data("needpassword",getSharedPreferences("moe",0).getString("pwd",""))
							.data("num","1")
							.data("sid",PreferenceUtils.getCookie(getApplicationContext()))
							.userAgent(PreferenceUtils.getUserAgent())
							.cookie(PreferenceUtils.getCookieName(getApplicationContext()),PreferenceUtils.getCookie(getApplicationContext()));
							try
							{
								Document doc=conn.post();
								if(doc.text().indexOf("成功")!=-1){
									handler.sendEmptyMessage(5);
									return;
								}
							}
							catch (IOException e)
							{}
							handler.sendEmptyMessage(4);
						}
					}.start();
				}
			}).show();
	}

	@Override
	public void onDelete(CollectionAdapter sha, final CollectionAdapter.ViewHolder vh)
	{
		new AlertDialog.Builder(this).setTitle("确定删除？").setMessage(list.get(vh.getAdapterPosition()).getTitle()).setNegativeButton("手滑了", null).setPositiveButton("删除", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					new Thread(){
						public void run()
						{
							try
							{
								Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/book_view_modfile_del.aspx")
									.data("action", "godel")
									.data("delid", list.get(vh.getAdapterPosition()).getId() + "")
									.data("id", "" + bbs.getId())
									.data("siteid", "1000")
									.data("classid", bbs.getClassid() + "")
									.data("lpage", "")
									.data("needpassword", getSharedPreferences("moe", 0).getString("pwd", ""))
									.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext()))
									.userAgent(PreferenceUtils.getUserAgent()).get();
								if (doc.text().indexOf("成功") != -1)
								{
									handler.obtainMessage(2, vh.getAdapterPosition()).sendToTarget();
									return;
								}
							}
							catch (IOException e)
							{}
							handler.sendEmptyMessage(3);
						}
					}.start();
				}
			}).show();
	}




	private void load()
	{
		new Thread(){
			public void run()
			{
				try
				{
					Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/book_view_modfile.aspx")
						.data("action", "go")
						.data("id", "" + bbs.getId())
						.data("siteid", "1000")
						.data("classid", bbs.getClassid() + "")
						.data("lpage", "1")
						.data("needpassword", getSharedPreferences("moe", 0).getString("pwd", ""))
						.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext()))
						.userAgent(PreferenceUtils.getUserAgent()).get();
					Elements book_id=doc.getElementsByAttributeValue("name", "book_id");
					Elements book_title=doc.getElementsByAttributeValue("name", "book_file_title");
					Elements book_ext=doc.getElementsByAttributeValue("name", "book_ext");
					Elements book_size=doc.getElementsByAttributeValue("name", "book_size");
					Elements book_file_info=doc.getElementsByAttributeValue("name", "book_file_info");
					List<CollectionItem> list=new ArrayList<>();
					for (int i=0;i < book_id.size();i++)
					{
						FileManagerItem ci=new FileManagerItem();
						ci.setId(Integer.parseInt(book_id.get(i).val()));
						ci.setTitle(book_title.get(i).val() + "." + book_ext.get(i).val());
						ci.setSummary(book_size.get(i).val());
						ci.setContent(book_file_info.get(i).text());
						list.add(ci);
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

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelable("bbs", bbs);
		outState.putParcelableArrayList("list", list);
		super.onSaveInstanceState(outState);
	}

	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					refresh.setRefreshing(false);
					Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					refresh.setRefreshing(false);
					list.clear();
					list.addAll((List)msg.obj);
					ca.notifyDataSetChanged();
					break;
				case 2:
					list.remove(((Integer)msg.obj).intValue());
					ca.notifyItemRemoved(msg.obj);
					break;
				case 3:
					Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
					break;
				case 4:
					Toast.makeText(getApplicationContext(), "更改失败", Toast.LENGTH_SHORT).show();
					
					break;
					case 5:
						onRefresh();
						break;
			}
		}

	};
}
