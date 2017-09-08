package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import com.moe.entity.ListItem;
import org.jsoup.Jsoup;
import android.content.SharedPreferences;
import java.io.IOException;
import android.content.res.Resources.NotFoundException;
import org.jsoup.nodes.Document;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import com.moe.utils.ImageCache;
import com.moe.thread.ImageLoadThread;
import com.moe.utils.ImageLoad;
import android.graphics.drawable.LevelListDrawable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import com.moe.utils.PreferenceUtils;
import com.moe.widget.CircleImageView;
import java.util.List;
import org.jsoup.nodes.Node;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.xml.sax.XMLReader;
import android.text.Editable;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.text.Layout;
import android.text.style.ClickableSpan;
import android.text.Selection;
import android.text.style.URLSpan;
import com.moe.utils.UrlUtils;
import android.content.Intent;
import android.view.Gravity;
import com.moe.widget.ProgressBar;
import com.moe.utils.BitmapUtils;
import com.moe.entity.UserItem;
import com.moe.utils.UserUtils;
import android.net.Uri;
import com.moe.entity.FloorItem;
import java.util.ArrayList;
import com.moe.internal.ImageGetter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.adapter.FloorAdapter;
import com.moe.view.Divider;
import com.moe.internal.TextViewClickMode;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.Menu;
import android.widget.Toast;
import com.moe.app.ReportDialog;
import android.widget.ImageView;
import com.moe.entity.RadioItem;
import com.moe.adapter.VotedAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import com.moe.entity.BbsItem;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.annotation.SuppressLint;
import android.support.v4.widget.NestedScrollView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import android.support.v4.view.ViewCompat;
@SuppressLint("NewApi")
public class BbsActivity extends EventActivity implements 
Html.TagHandler,
NestedScrollView.OnScrollChangeListener,
View.OnClickListener,
FloorAdapter.OnItemClickListener,
ReportDialog.OnClickListener,
VotedAdapter.OnItemClickListener,
SwipeRefreshLayout.OnRefreshListener,
View.OnTouchListener
{

	
	private FloorAdapter fa;
	private ArrayList<FloorItem> list;
	private UserItem ui;
	private SharedPreferences moe;
	private ListItem bbs;
	private String time,content,mode,mode_summary,max,min;
	private TextView author,summary,tv_content,bbs_mode,subtitle;
	private ViewGroup bbs_mode_s;
	private ProgressBar pb;
	private boolean load,canload=true;
	private int page=1,total;
	private View progress;
	private ReportDialog report;
	private ImageView icon;
	private ArrayList<RadioItem> radiolist;
	private VotedAdapter va;
	private SwipeRefreshLayout refresh;
	private MenuItem manager;
	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState)
	{
		moe = getSharedPreferences("moe", 0);
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.bbs_view,(ViewGroup)findViewById(R.id.main_index),true);
		//getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left);
		author = (TextView)findViewById(android.R.id.title);
		summary = (TextView)findViewById(android.R.id.summary);
		tv_content = (TextView)findViewById(R.id.content);
		//tv_content_summary=(TextView)findViewById(R.id.content_summary);
		bbs_mode = (TextView)findViewById(R.id.bbs_view_mode);
		bbs_mode_s = (ViewGroup)findViewById(R.id.summary);
		pb = (ProgressBar)findViewById(R.id.progressbar);
		subtitle=(TextView)findViewById(R.id.subTitle);
		//tv_content.setTextIsSelectable(true);
		ImageView reply=(ImageView) findViewById(R.id.edit);
		reply.setVisibility(View.VISIBLE);
		reply.setImageResource(R.drawable.ic_quick_reply);
		reply.setOnClickListener(this);
		TextViewClickMode tvcm=new TextViewClickMode(tv_content);
		//new TextViewClickMode(tv_content_summary);
		//tv_content.setTextIsSelectable(false);
		icon= (CircleImageView)findViewById(android.R.id.icon);
		icon.setOnClickListener(this);
		RecyclerView rv=(RecyclerView)findViewById(R.id.list);
		LinearLayoutManager llm=new LinearLayoutManager(this);
		llm.setAutoMeasureEnabled(true);
		rv.setLayoutManager(llm);
		ViewCompat.setNestedScrollingEnabled(rv,false);
		rv.addItemDecoration(new Divider(0,0,0,10));
		rv.setItemAnimator(null);
		RecyclerView radio=(RecyclerView)findViewById(R.id.radiolist);
		LinearLayoutManager llma=new LinearLayoutManager(this);
		llma.setAutoMeasureEnabled(true);
		radio.setLayoutManager(llma);
		ViewCompat.setNestedScrollingEnabled(radio,false);
		//rv.addItemDecoration(new Divider(0x00000000, 10));
		NestedScrollView scroll=((NestedScrollView)findViewById(R.id.scroll));
		scroll.setOnTouchListener(this);
		scroll.setOnScrollChangeListener(this);
		progress=findViewById(R.id.reply_progressbar);
		refresh=(SwipeRefreshLayout) findViewById(R.id.refresh);
		refresh.setOnRefreshListener(this);
		if (savedInstanceState == null)
		{
			bbs = getIntent().getParcelableExtra("bbs");
			if (bbs == null)
			{finish();return;}
			if (bbs.getTitle() != null)
				getSupportActionBar().setTitle(bbs.getTitle());

		}
		else
		{
			radiolist=savedInstanceState.getParcelableArrayList("radiolist");
			total=savedInstanceState.getInt("total");
			page = savedInstanceState.getInt("page");
			list = savedInstanceState.getParcelableArrayList("list");
			canload = savedInstanceState.getBoolean("canload");
			ui = savedInstanceState.getParcelable("ui");
			bbs = savedInstanceState.getParcelable("bbs");
			time = savedInstanceState.getString("time");
			content = savedInstanceState.getString("content");
			mode = savedInstanceState.getString("mode");
			mode_summary = savedInstanceState.getString("mode_summary");
			max = savedInstanceState.getString("max");
			min = savedInstanceState.getString("min");

		}
		if (list == null)list = new ArrayList<>();
		rv.setAdapter(fa = new FloorAdapter(list,bbs));
		fa.setOnItemClickListener(this);
		if(radiolist==null)radiolist=new ArrayList<>();
		radio.setAdapter(va=new VotedAdapter(radiolist));
		va.setOnItemClickListener(this);
		if (bbs.getUserid() == 0)
		{load();}
		else handler.sendEmptyMessage(1);
	}

	@Override
	public void onRefresh()
	{
		page=1;
		list.clear();
		fa.notifyDataSetChanged();
		load();
	}

	
	private void load()
	{
		refresh.setRefreshing(true);
		new Thread(){
			public void run()
			{
				loadData();
			}
		}.start();
	}
	private void loadData()
	{
		Document doc=null;
		try
		{
			doc = Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + getString(R.string.view))
				.data("id", bbs.getId() + "")
				.userAgent(PreferenceUtils.getUserAgent())
				.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext()))
				.get();
			//if (bbs.getTitle() == null)
				bbs.setTitle(doc.title());
			Matcher matcher=Pattern.compile("论坛>(.*?)>帖子").matcher(doc.getElementsByClass("title").get(0).text());
			if(matcher.find())
				bbs.setBbs(matcher.group(1));
			Elements elements=doc.getElementsByClass("content");
			if (elements.size() == 0)
			{
				handler.sendEmptyMessage(0);
				return;
			}
			Element content=elements.get(0);
			matcher=Pattern.compile("(?s)\\(阅([0-9]{1,})\\)",Pattern.DOTALL).matcher(content.text());
			if(matcher.find())
				bbs.setProgress("<font color='#0097a7'>阅"+matcher.group(1)+"</font>");
			String content_text = content.getElementsByClass("bbscontent").get(0).toString();
			if(PreferenceUtils.isDirect(getApplicationContext())){
			StringBuffer sb=new StringBuffer(content_text);
			matcher=Pattern.compile("(?s)([^\"'=]|^|\\s|\\s>|\">|br>){1}(http(|s)://[0-9a-zA-Z%\\?:=\\.\\-/_&;!#\\$\\(\\)\\*\\+,:@\\[\\]]+)",Pattern.DOTALL).matcher(content_text);
			while(matcher.find()){
				String url="<a href='"+matcher.group(2)+"'>"+matcher.group(2)+"</a>";
				int start=matcher.start(2);
				sb.replace(start,matcher.end(2),url);
				matcher.reset(sb.toString());
				matcher=matcher.region(start+url.length(),sb.length());
			}
			BbsActivity.this.content=sb.toString();
			}else{
				BbsActivity.this.content=content_text;
			}
			List<Node> nodes=content.childNodes();
			if (nodes.get(0).toString().indexOf("标题") != -1)
			{
				//2
				time = nodes.get(2).toString();
			}
			else
			{
				matcher=Pattern.compile("([0-9]{1,}).*?([0-9]{1,})", Pattern.DOTALL).matcher(nodes.get(0).toString());
				matcher.find();
				max = matcher.group(1);
				min = matcher.group(2);
				if (nodes.get(0).toString().indexOf("赏") != -1)
				{
					//4
					mode = "悬赏贴";
					time = nodes.get(4).toString();
				}
				else if (nodes.get(0).toString().indexOf("礼") != -1)
				{
					//6
					mode = "撒币贴";
					mode_summary = nodes.get(2).toString();
					time = nodes.get(6).toString();
				}
			}
			time = time.substring(5) + " ";
			String classId=doc.getElementsByAttributeValueContaining("href", "classid=").get(0).attr("href");
			classId = classId.substring(classId.indexOf("classid=") + 8);
			//classId.substring(0,classId.indexOf("&"));
			bbs.setClassid(Integer.parseInt(classId));
			String userid=doc.getElementsByClass("subtitle").get(1).childNode(1).attr("href");
			bbs.setUserid(Integer.parseInt(userid.substring(userid.lastIndexOf("=") + 1)));
			ui = UserUtils.getUserInfo(BbsActivity.this, bbs.getUserid());
			handler.sendEmptyMessage(1);
			Elements vote=doc.getElementsByClass("toupiao");
			if(vote.size()==1){
				Element voted=vote.get(0);
				String[] item=voted.html().split("<br>");
				List<RadioItem> votes=new ArrayList<>();
				Pattern pattern=Pattern.compile("(?s)vid=([0-9]{1,}).*?\\][0-9]{1,}.(.*?)\\(([0-9]{1,})\\)",Pattern.DOTALL);
				for(String line:item){
					matcher=pattern.matcher(line);
					if(matcher.find()){
						RadioItem ri=new RadioItem();
						ri.setVid(Integer.parseInt(matcher.group(1)));
						ri.setTitle(matcher.group(2));
						ri.setProgress(Integer.parseInt(matcher.group(3)));
						votes.add(ri);
					}
				}
				int count=0;
				for(RadioItem ri:votes)
					count+=ri.getProgress();
				for(RadioItem ri:votes)
					ri.setCount(count);
				handler.obtainMessage(6,votes).sendToTarget();
			}
			loadReply();
		}
		catch (Exception e)
		{
			handler.sendEmptyMessage(4);
			}
		
	}
	

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelableArrayList("radiolist",radiolist);
		outState.putInt("total",total);
		outState.putInt("page", page);
		outState.putParcelableArrayList("list", list);
		outState.putBoolean("canload", canload);
		outState.putParcelable("bbs", bbs);
		outState.putString("time", time);
		outState.putString("content", content);
		outState.putParcelable("ui", ui);
		outState.putString("mode", mode);
		outState.putString("mode_summary", mode_summary);
		outState.putString("max", max);
		outState.putString("min", min);

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
					getSupportActionBar().setTitle("拒绝访问！");
					break;
				case 1://加载主要数据
					refresh.setRefreshing(false);
					getSupportActionBar().setTitle(bbs.getTitle());
					author.setText(Html.fromHtml((bbs.getAuthor() != null ?bbs.getAuthor(): ui != null && ui.getName() != null ?ui.getName(): "")+"<font color='#aaaa00'> Lv"+(ui!=null?ui.getLevel():0)+"</font>"));
					summary.setText(Html.fromHtml(time + bbs.getProgress()));
					//int index=content.indexOf("<!--listE-->");
					tv_content.setText(Html.fromHtml(content, new ImageGetter(tv_content,true), BbsActivity.this));
					//tv_content_summary.setText(Html.fromHtml(content.substring(index),new ImageGetter(tv_content_summary),null));
					subtitle.setText(bbs.getBbs());
					if (mode != null)
					{
						bbs_mode_s.setVisibility(View.VISIBLE);
						((TextView)bbs_mode_s.getChildAt(0)).setText(mode_summary);
						((TextView)bbs_mode_s.getChildAt(2)).setText(min + "/" + max);
						bbs_mode.setText(mode);
						pb.setMax(Integer.parseInt(max));
						pb.setProgress(Integer.parseInt(min));
					}
					if (ui != null){
						if(ui.getLogo() != null)
							Glide.with(BbsActivity.this).load(ui.getLogo()).diskCacheStrategy(DiskCacheStrategy.ALL).into(icon);
						//ImageCache.load(ui.getLogo(),icon);
						if(ui.getUid()!=PreferenceUtils.getUid(getApplicationContext()))
							if(manager!=null)
							manager.setVisible(false);
						}
					break;
				case 2://加载回复
					//progress.clearAnimation();
					//progress.requestLayout();
					load = false;
					//canload =((List)msg.obj).size()!=0&&((List)msg.obj).size()==10&&list.get(list.size() - 1).getFloor() != 1;
					canload=list.size()<total&&(list.size()>0?list.get(list.size()-1).getFloor()!=1:false);
					if(canload)
						progress.setVisibility(View.INVISIBLE);
					else
						progress.setVisibility(View.GONE);
					
					page++;
					/*
					if (msg.obj != null)
					{
						page++;
						int size=list.size();
						boolean f=list.addAll((List)msg.obj);
						fa.notifyItemRangeInserted(size, list.size() - size);		
						//canload =((List)msg.obj).size()!=0&&((List)msg.obj).size()==10&&list.get(list.size() - 1).getFloor() != 1;
						canload=f&&list.size()<total&&(list.size()>0?list.get(list.size()-1).getFloor()!=1:false);
					}*/
					break;
					case 3:
					progress.setVisibility(View.VISIBLE);
					//progress.requestLayout();
						break;
					case 4:
						refresh.setRefreshing(false);
						getSupportActionBar().setTitle("加载失败");
						break;
					case 5:
						Toast.makeText(getApplicationContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
						break;
					case 6://投票数据
					radiolist.clear();
					radiolist.addAll((List)msg.obj);
					va.notifyDataSetChanged();
						break;
					case 7:
						if(msg.obj!=null){
							list.add((FloorItem)msg.obj);
							fa.notifyItemInserted(list.size()-1);
						}
						break;
			}
		}

	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.manager:
				new AlertDialog.Builder(this).setItems(new String[]{"修改贴子","文件续传","附件管理","删除贴子","结束贴子"}, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							switch(p2){
								case 0:
									startActivityForResult(new Intent(getApplicationContext(),ModBbsActivity.class).putExtra("bbs",bbs),814);
									break;
								case 1:
									startActivityForResult(new Intent(getApplicationContext(),FileAddActivity.class).putExtra("bbs",bbs),814);
									break;
								case 2:
									startActivityForResult(new Intent(getApplicationContext(),FileManagerActivity.class).putExtra("bbs",bbs),814);
									break;
								case 3:
									Toast.makeText(getApplicationContext(),"这个功能被泡面C关了",Toast.LENGTH_SHORT).show();
									break;
								case 4:
									final EditText v=(EditText)LayoutInflater.from(getApplicationContext()).inflate(R.layout.send_money,null);
									v.setHint("结束理由");
									v.setHintTextColor(0xffaaaaaa);
									v.setTextColor(0xff111111);
									new AlertDialog.Builder(BbsActivity.this).setTitle("确认结束？").setView(v).setNegativeButton("手滑", null).setPositiveButton("结束", new DialogInterface.OnClickListener(){

											@Override
											public void onClick(DialogInterface p1, int p2)
											{
												new Thread(){public void run(){
														try
														{
															Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/book_view_end.aspx")
																.data("action", "gomod")
																.data("id", bbs.getId() + "")
																.data("classid", bbs.getClassid() + "")
																.data("siteid", "1000")
																.data("tops", "2")
																.data("needpassword",getSharedPreferences("moe",0).getString("pwd",""))
																.data("whylock",v.getText().toString())
																.userAgent(PreferenceUtils.getUserAgent())
																.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext())).post();
																if(doc.text().indexOf("成功")!=-1){
																	handler.obtainMessage(5,"结贴成功").sendToTarget();
																	return;
																}
														}
														catch (IOException e)
														{}
														handler.obtainMessage(5,"结贴失败").sendToTarget();
													}}.start();
											}
										}).show();
									break;
							}
						}
					}).show();
				break;
			case R.id.collection:
				new Thread(){
					public void run(){
						try
						{
							Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/Share.aspx")
								.data("action", "fav")
								.data("siteid", "1000")
								.data("classid", bbs.getClassid() + "")
								.data("id", bbs.getId() + "")
								.userAgent(PreferenceUtils.getUserAgent())
								.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext()))
								.get();
							if(doc.getElementsByClass("tip").size()>0)
								handler.obtainMessage(5,"收藏失败").sendToTarget();
							else
								handler.obtainMessage(5,"收藏成功").sendToTarget();
						}
						catch (IOException e)
						{handler.obtainMessage(5,"网络连接失败，请稍候再试").sendToTarget();}
					}
				}.start();
				break;
			case R.id.report:
				if(report==null)report=new ReportDialog(this,this);
				report.show();
				break;
			default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onClick(final String type, final String why)
	{
		new Thread(){
			public void run(){
				try
				{
					Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/Report_add.aspx")
						.data("reporttype", type)
						.data("reportwhy", why)
						.data("action", "gomod")
						.data("page", "1")
						.data("siteid","1000")
						.data("classid",bbs.getClassid()+"")
						.data("id",bbs.getId()+"")
						.data("sid", PreferenceUtils.getCookie(getApplicationContext()))
						.userAgent(PreferenceUtils.getUserAgent())
						.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext()))
						.post();
					handler.obtainMessage(5,doc.getElementsByClass("tip").get(0).child(0).childNode(0)).sendToTarget();
				}
				catch (IOException e)
				{handler.obtainMessage(5,"网络连接失败").sendToTarget();}
			}
		}.start();
	}


	//过滤tag
	private int start;
	@Override
	public void handleTag(boolean p1, String p2, Editable p3, XMLReader p4)
	{
		if (p2.equals("script"))
		{
			if (p1)
			{
				start = p3.length();
			}
			else
			{
				p3.delete(start, p3.length());
			}
		}
	}
	

	private void loadReply()
	{
		load = true;
		handler.sendEmptyMessage(3);
		new Thread(){
			public void run()
			{
				reply();
			}
		}.start();
	}
	private void reply()
	{
		Document doc=null;
		try
		{
			doc = Jsoup.connect(PreferenceUtils.getHost(this) + getString(R.string.reply))
				.data("page", page + "").data("lpage","1").data("getTotal",  "").data("id", bbs.getId() + "").data("classid", bbs.getClassid() + "")
				.userAgent(PreferenceUtils.getUserAgent())
				.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext()))
				.get();
		}
		catch (IOException e)
		{handler.sendEmptyMessage(2);return;}
		try{
			total=Integer.parseInt(doc.getElementsByAttributeValue("name","getTotal").get(0).attr("value"));
		}catch(Exception e){}
		List<FloorItem> lfi=new ArrayList<>();
		Elements elements=doc.getElementsByAttributeValueMatching("class", "^line(1|2)$");
		for (int i=0;i < elements.size();i++)
		{
			FloorItem fi=new FloorItem();
			Element line=elements.get(i);
			Matcher matcher=Pattern.compile("(?s)reply=([0-9]{1,}).*?touserid=([0-9]{1,}).*?\\](.*)<br>.*?>(.*?)<.*?a>(.*)",Pattern.DOTALL).matcher(line.html());
			matcher.find();
			fi.setFloor(Integer.parseInt(matcher.group(1)));
			fi.setUid(Integer.parseInt(matcher.group(2)));
			//fi.setUser(UserUtils.getUserInfo(getApplicationContext(),Integer.parseInt(matcher.group(2))));
			fi.setContent(matcher.group(3));
			fi.setName(matcher.group(4));
			fi.setTime(matcher.group(5));
			/*if("悬赏贴".equals(mode)&&bbs.getUserid()==PreferenceUtils.getUid(getApplicationContext()))
				fi.setSendmoney(true);
				else*/
			fi.setSendmoney(line.html().matches("(?s).*?SendMoney.aspx.*?"));
			fi.setDelete(line.html().matches("(?s).*?Book_re_del.aspx.*?"));
			if(fi.isSendmoney()){
				matcher=Pattern.compile("(?s)reid=([0-9]{1,})&").matcher(line.html());
				matcher.find();
				fi.setReid(Long.parseLong(matcher.group(1)));
			}
			/*Uri uri=Uri.parse(line.getElementsByAttributeValueMatching("href", ".*?/bbs/Book_re.aspx.*?").get(0).absUrl("href"));
			fi.setFloor(Integer.parseInt(uri.getQueryParameter("reply")));
			fi.setUser(UserUtils.getUserInfo(getApplicationContext(), Integer.parseInt(uri.getQueryParameter("touserid"))));
			fi.setTime(line.childNode(line.childNodeSize() - 1).toString());
			List<Node> nodes=line.childNodes();
			int count=2;
			if (line.getElementsByAttributeValueMatching("href", ".*?/bbs/Book_re_del.aspx.*?").size() == 1 || line.toString().indexOf("得金:") != -1)
			{
				count = 4;
			}
			StringBuffer sb=new StringBuffer();
			for (;count < nodes.size() - 2;count++)
				sb.append(nodes.get(count).toString());
			sb.delete(0, 1);
			fi.setContent(sb.toString());
			*/
			
			matcher=Pattern.compile("得金:([0-9]{1,})<",Pattern.DOTALL).matcher(line.html());
			if(matcher.find())
				fi.setMoney(Integer.parseInt(matcher.group(1)));
			//lfi.add(fi);
			handler.obtainMessage(7,fi).sendToTarget();
		}
		handler.obtainMessage(2, lfi).sendToTarget();
	}

	@Override
	public boolean onTouch(View p1, MotionEvent p2)
	{
		switch(p2.getAction()){
			case p2.ACTION_DOWN:
				tv_content.setEnabled(false);
				tv_content.setFocusableInTouchMode(false);
				p1.onTouchEvent(p2);
				return true;
			case p2.ACTION_CANCEL:
			case p2.ACTION_UP:
				tv_content.setEnabled(true);
				tv_content.setFocusableInTouchMode(true);
				//View v=getWindow().getCurrentFocus();
				//if(v!=null)v.clearFocus();
				break;
		}
		return false;
	}

	
	@Override
	public void onScrollChange(NestedScrollView view, int x, int y, int oldx, int oldy)
	{
		int height=((ViewGroup)view).getChildAt(0).getBottom();
		if(canload&&!load&&height>view.getHeight()&&height-y-view.getHeight()<700){
			loadReply();
		}
	}

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.edit:
				Bundle bundle=new Bundle();
				bundle.putParcelable("bbs",bbs);
				startActivityForResult(new Intent(this,ReplyActivity.class).putExtras(bundle),333);
				break;
			case android.R.id.icon:
				if(ui!=null){
					startActivity(new Intent(this,UserInfoActivity.class).putExtra("uid",ui.getUid()));
				}
				break;
		}
	}

	@Override
	public void onItemClick(RecyclerView.Adapter ra, final RecyclerView.ViewHolder vh)
	{
		if(ra instanceof VotedAdapter){
			new Thread(){
				public void run(){
					try
					{
						Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/book_view_toVote.aspx")
							.data("siteid", "1000")
							.data("classid", bbs.getClassid() + "")
							.data("vid", "" + radiolist.get(vh.getAdapterPosition()).getVid())
							.data("vpage", "1")
							.data("lpage", "2")
							.data("id", "" + bbs.getId())
							.userAgent(PreferenceUtils.getUserAgent())
							.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext())).get();
							handler.obtainMessage(5,doc.getElementsByClass("tip").get(0).child(0).text()).sendToTarget();
					}
					catch (IOException e)
					{handler.obtainMessage(5,"网络连接失败").sendToTarget();}
				}
			}.start();
		}else{
		Bundle bundle=new Bundle();
		bundle.putParcelable("bbs",bbs);
		bundle.putParcelable("floor",list.get(vh.getAdapterPosition()));
		startActivityForResult(new Intent(this,ReplyActivity.class).putExtras(bundle),333);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode==RESULT_OK)
		switch(requestCode){
			case 333:
			list.clear();
			fa.notifyDataSetChanged();
			page=1;
			loadReply();
			break;
			case 814:
				onRefresh();
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.bbs_manager,menu);
		manager=menu.getItem(0);
		if(ui!=null&&ui.getUid()!=PreferenceUtils.getUid(getApplicationContext()))
			manager.setVisible(false);
		return true;
	}

	@Override
	protected void onPause()
	{
		// TODO: Implement this method
		super.onPause();
		View v=getWindow().getCurrentFocus();
		if(v!=null)v.clearFocus();
	}
}
