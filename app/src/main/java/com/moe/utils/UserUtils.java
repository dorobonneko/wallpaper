package com.moe.utils;
import com.moe.entity.UserItem;
import android.content.Context;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import com.moe.yaohuo.R;
import org.jsoup.nodes.Element;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.jsoup.select.Elements;
import android.support.v4.util.LruCache;
import com.moe.entity.FloorItem;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.widget.Adapter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.moe.utils.UserUtils.Callback;
import java.util.Iterator;
public class UserUtils
{
	private static UserUtils uu;
	private Context context;
	private LruCache<Integer,UserItem> cache=new LruCache<Integer,UserItem>(1000){

		@Override
		protected int sizeOf(Integer key, UserItem value)
		{
			return 1;
		}



	};
	private UserUtils(Context context)
	{
		this.context = context.getApplicationContext();
	}
	public static UserUtils getInstance(Context context)
	{
		if (uu == null)uu = new UserUtils(context);
		return uu;
	}
	/**public void getUserItem(final FloorItem fi,final RecyclerView.Adapter adapter,final int position){
		UserItem ui=cache.get(fi.getUid());
		if(ui!=null){
			fi.setUser(ui);
			//adapter.notifyItemChanged(position);
			return;
		}
	final Handler handler=new Handler(){
		public void handleMessage(Message msg){
			adapter.notifyItemChanged(position);
				}
	};
	new Thread(){
		public void run(){
			UserItem ui=getUserItem(fi.getUid(),true);
			//if(ui!=null)
			//cache.put(fi.getUid(),ui);
			fi.setUser(ui);
			handler.sendEmptyMessage(0);
		}
	}.start();
	}*/
	public UserItem getUserItem(int id,boolean usecache)
	{
		if (id < 0)return null;
		UserItem ui=null;
		//if (id != PreferenceUtils.getUid(context))
		if(usecache)
		ui=cache.get(id);
		if (ui != null)return ui;
		try
		{
			ui = new UserItem();
			Document doc = Jsoup.connect(PreferenceUtils.getHost(context) + "/bbs/userinfomore.aspx")
				.data("touserid", id + "")
				.data("classid", "0")
				.data("siteid", "1000")
				.userAgent(PreferenceUtils.getUserAgent())
				.cookie(PreferenceUtils.getCookieName(context), PreferenceUtils.getCookie(context))
				.get();
			if (id == PreferenceUtils.getUid(context))
			{
				Elements msgs=doc.getElementsByAttributeValueStarting("href", "/bbs/messagelist.aspx");
				if (msgs.size() > 0)
				{
					Matcher matcher=Pattern.compile("(?s).*?([0-9]{1,})", Pattern.DOTALL).matcher(msgs.get(0).text());
					if (matcher.find())
						ui.setMsg(Integer.parseInt(matcher.group(1)));
				}
			}
			try
			{
				ui.setLogo(doc.getElementsByAttributeValue("alt", "头像").get(0).absUrl("src"));
			}
			catch (Exception e)
			{}
			ui.setUid(id);

			ui.setState(doc.getElementsByAttributeValue("alt", "ONLINE").size());
			Matcher matcher=Pattern.compile("(?s)个人资料(.*?)【.*?【昵称】：(.*?)\\s【妖晶】：([\\d-]*)\\s【经验】：(\\d*)\\s【等级】：(\\d*).*?【头衔】：(.*?)\\s.*?【性别】：(.)\\s【年龄】：(\\d*).*?【积时】：(.*?)\\s【注册时间】：(.*?)\\s【.*?【身高】：(.*?)\\s【体重】：(.*?)\\s【星座】：(.*?)\\s", Pattern.DOTALL).matcher(doc.text());
			if (matcher.find())
			{
				try
				{
					ui.setSign(matcher.group(1).substring(6));
				}
				catch (Exception e)
				{}
				ui.setName(matcher.group(2));
				ui.setExper(Long.parseLong(matcher.group(4)));
				String money=matcher.group(3);
				boolean f=money.startsWith("-");
				ui.setMoney(Long.parseLong(f ?money.substring(1): money));
				if (f)
					ui.setMoney(-ui.getMoney());
				try
				{
					ui.setLevel(Integer.parseInt(matcher.group(5)));
				}
				catch (Exception e)
				{}
				ui.setNickName(matcher.group(6));
				ui.setSex(matcher.group(7).equals("男") ?0: 1);
				ui.setAge(matcher.group(8));
				ui.setTime(matcher.group(9));
				ui.setRegisterTime(matcher.group(10));
				ui.setHeight(matcher.group(11));
				ui.setWeight(matcher.group(12));
				ui.setStar(matcher.group(13));
				cache.put(id,ui);
				return ui;
			}
		}
		catch (Exception e)
		{}
		return null;
	}
	public static UserItem getUserInfo(Context context, int id,boolean useCache)
	{
		return getInstance(context).getUserItem(id,useCache);
	}
	public static UserItem getUserInfo(Context context, int id)
	{
		return getInstance(context).getUserItem(id,true);
	}
	
	private static List<LoadThread> list_thread=new ArrayList<>();
	
	public static void loadUserItem(Context context,int id,Callback call){
		if(!Thread.currentThread().getName().equalsIgnoreCase("main"))
		throw new RuntimeException("you must call in main thread");
		if(list_thread.contains(id)){
			list_thread.get(list_thread.indexOf(id)).add(call);
		}else{
			LoadThread lt=new LoadThread(id,context);
			lt.add(call);
			lt.start();
			list_thread.add(lt);
		}
	}
	public abstract interface Callback{
		void onLoad(UserItem ui);
	}
	static class LoadThread extends Thread implements Handler.Callback
	{
		private int id;
		private Context context;
		private List<Callback> call=new ArrayList<>();
		private Handler handler;
		private UserItem ui;
		public LoadThread(int id,Context context){
			this.id=id;
			this.context=context;
			handler=new Handler(this);
		}
		public void add(UserUtils.Callback call)
		{
			this.call.add(call);
		}
		@Override
		public void run()
		{
			ui=getInstance(context).getUserItem(id,true);
			
			handler.sendEmptyMessage(0);
		}

		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof Integer)
				return id==((Integer)obj).intValue();
			return super.equals(obj);
		}

		@Override
		public boolean handleMessage(Message p1)
		{
			Iterator<Callback> ic=call.iterator();
			while(ic.hasNext()){
				ic.next().onLoad(ui);
				ic.remove();
			}
			list_thread.remove(this);
			return true;
		}

		
	}
}
