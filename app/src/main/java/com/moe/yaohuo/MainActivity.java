package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.content.res.Configuration;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.support.design.widget.Snackbar;
import android.widget.TextView;
import android.view.View;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import com.moe.fragment.BbsListFragment;
import android.view.Gravity;
import android.support.design.widget.FloatingActionButton;
import android.content.Intent;
import com.moe.fragment.BbsClassFragment;
import android.view.ViewGroup;
import org.jsoup.Jsoup;
import com.moe.utils.UserUtils;
import android.content.SharedPreferences;
import com.moe.entity.UserItem;
import com.moe.utils.ImageCache;
import com.moe.widget.CircleImageView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Menu;
import com.moe.utils.PreferenceUtils;
import com.moe.download.Download;
import com.moe.fragment.DownloadFragment;
import com.moe.fragment.preference.SettingPreference;
import com.moe.fragment.preference.CenterPreference;
import com.moe.fragment.CollectionFragment;
import android.annotation.SuppressLint;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.lang.reflect.Field;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.support.v4.content.ContentResolverCompat;
import com.moe.thread.LogoUpload;
import com.tencent.bugly.crashreport.CrashReport;
import android.widget.ImageView;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.moe.graphics.ImageViewTarget;
import android.support.graphics.drawable.VectorDrawableCompat;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import android.content.res.TypedArray;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVObject;
import android.content.pm.PackageManager;
import com.avos.avoscloud.AVException;
import java.util.List;
import com.avos.avoscloud.FindCallback;
import com.moe.entity.DownloadItem;
import com.moe.services.DownloadService;
import android.os.Environment;


public class MainActivity extends BaseActivity implements 
NavigationView.OnNavigationItemSelectedListener,
DrawerLayout.DrawerListener,
View.OnClickListener,
View.OnLongClickListener,
LogoUpload.Callback,
SharedPreferences.OnSharedPreferenceChangeListener
{
	private UserItem ui;
	private Snackbar snack;
	private NavigationView nmv;
	private ActionBarDrawerToggle abdt;
	private DrawerLayout drawerlayout;
	private Fragment current;
	private int id;
	private ImageView logo;
	private SharedPreferences moe;
	private TextView username;
	private Network network;
	private TextView msg;

	
    @Override
	protected void onCreate(Bundle savedInstanceState)
    {
		moe=getSharedPreferences("moe",0);
		moe.registerOnSharedPreferenceChangeListener(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		nmv = (NavigationView)findViewById(R.id.main_leftselectedView);
		drawerlayout = (DrawerLayout)findViewById(R.id.main_drawerlayout);
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		
		setSupportActionBar(toolbar);
		try
		{
			Field title=Toolbar.class.getDeclaredField("mTitleTextView");
			title.setAccessible(true);
			TextView o=(TextView) title.get(toolbar);
			o.setId(android.R.id.title);
			o.setOnClickListener(this);
		}
		catch (Exception e)
		{}
		ActionBar ab=getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		abdt = new ActionBarDrawerToggle(this, drawerlayout, toolbar, 0, 0);
		drawerlayout.addDrawerListener(abdt);
		nmv.setNavigationItemSelectedListener(this);
		if("侧边栏".equals(moe.getString("exit_mode",null)))
		reloadExit(true);
		drawerlayout.addDrawerListener(this);
		logo = (ImageView)nmv.getHeaderView(0).findViewById(R.id.logo);
		logo.setOnLongClickListener(this);
		msg=(TextView)nmv.getHeaderView(0).findViewById(R.id.msg_size);
		
		/*msg.setOutlineProvider(new ViewOutlineProvider(){

				@Override
				public void getOutline(View p1, Outline p2)
				{
					p2.setOval(0,0,p1.getWidth(),p1.getHeight());
				}
			});
		msg.setClipToOutline(true);*/
		findViewById(R.id.edit).setOnClickListener(this);
		nmv.getHeaderView(0).findViewById(R.id.message).setOnClickListener(this);
		username=(TextView)nmv.getHeaderView(0).findViewById(R.id.nav_header_main_info);
		username.setOnClickListener(this);
		
		if (savedInstanceState != null)
		{
			ui=savedInstanceState.getParcelable("ui");
			id = savedInstanceState.getInt("id");
			current = getSupportFragmentManager().findFragmentByTag(id + "");
			//handler.sendEmptyMessage(0);
		}
		else
		{
			onNavigationItemSelected(nmv.getMenu().findItem(R.id.menu_new));
			//username.setText(PreferenceUtils.isLogin(this)?moe.getString("name","未登录"):"未登录");
			
			//loadInfo();
		}
		IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(network=new Network(),filter);
		//关闭主界面的侧滑关闭
		findViewById(R.id.dragView).setEnabled(false);
		//CrashReport.testJavaCrash();
		loadInfo();
		if(moe.getBoolean("auot_update",false))
		checkUpdate();
    }

	@Override
	public boolean onNavigationItemSelected(MenuItem p1)
	{
		if (drawerlayout.isDrawerOpen(Gravity.START))
		{
			nmv.setTag(p1);
			drawerlayout.closeDrawers();
		}
		else{
			if(p1.getItemId()!=R.id.menu_exit)
			getSupportActionBar().setTitle(p1.getTitle());
			switch (p1.getItemId())
			{
				
				case R.id.menu_new:
					open(p1.getItemId(), BbsListFragment.class);
					break;
				case R.id.menu_bbs:
					open(p1.getItemId(), BbsClassFragment.class);
					break;
				case R.id.menu_center:
					open(p1.getItemId(),CenterPreference.class);
					break;
				case R.id.menu_download:
					open(p1.getItemId(), DownloadFragment.class);
					break;
				case R.id.menu_setting:
					open(p1.getItemId(),SettingPreference.class);
					break;
				case R.id.menu_collection:
					open(p1.getItemId(),CollectionFragment.class);
					break;
				case R.id.menu_exit:
					super.finish();
					break;
				
			}
			}
		return true;
	}
	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					MainActivity.this.msg.setVisibility(View.INVISIBLE);
					if(ui==null){
						username.setText(moe.getString("name","未登录"));
						//logo.setImageResource(R.drawable.yaohuo);
					}else{
						username.setText(ui.getName());
						//ImageCache.load(ui.getLogo(),logo);
						try{
							if(logo.getTag(R.id.state)==null||!(Boolean)logo.getTag(R.id.state))
								Glide.with(MainActivity.this).load(ui.getLogo()).listener(new RequestListener<String,GlideDrawable>(){

										@Override
										public boolean onException(Exception p1, String p2, Target<GlideDrawable> p3, boolean p4)
										{
											logo.setTag(R.id.state,false);
											return false;
										}

										@Override
										public boolean onResourceReady(GlideDrawable p1, String p2, Target<GlideDrawable> p3, boolean p4, boolean p5)
										{
											logo.setTag(R.id.state,true);
											return false;
										}
									}).error(VectorDrawableCompat.create(getResources(), R.drawable.logo_background, getTheme())).diskCacheStrategy(DiskCacheStrategy.ALL).into(logo);
							}catch(Exception e){}
						moe.edit().putString("name",ui.getName()).commit();
						/*if(ui.getMsg()>0){
							MainActivity.this.msg.setVisibility(View.VISIBLE);
							MainActivity.this.msg.setText(ui.getMsg()+"");
							//MainActivity.this.msg.setClipToOutline(true);
							}*/
					}
					break;
				case 1:
					Toast.makeText(getApplicationContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
					break;
				case 2:
					//退出标记，留空
					break;
			}
		}

	};

	@Override
	public void finish()
	{
		if (drawerlayout.isDrawerOpen(Gravity.START))
		{
			drawerlayout.closeDrawers();
			return;
		}else if(current instanceof com.moe.fragment.Fragment){
			if(((com.moe.fragment.Fragment)current).onBackPressed())return;
		}
		if(!(current instanceof BbsListFragment)){
			onNavigationItemSelected(nmv.getMenu().findItem(R.id.menu_new).setChecked(true));
		}
		else{
			switch(moe.getString("exit_mode","按钮")){
				case "按钮":
			if (snack == null)
				snack = Snackbar.make(findViewById(R.id.coordinatorlayout), "确认退出？", 1500).setAction("退出", this).setActionTextColor(getResources().getColor(R.color.primary)).addCallback(new Snackbar.Callback(){
					public void onDismissed(Snackbar bar,int event){
						switch(event){
							case Snackbar.Callback.DISMISS_EVENT_SWIPE:
								bar.removeCallback(this);
								snack = Snackbar.make(findViewById(R.id.coordinatorlayout), "确认退出？", 1500).setAction("退出", MainActivity.this).setActionTextColor(getResources().getColor(R.color.primary)).addCallback(this);
						}
					}
				});
			if (snack.isShown())snack.dismiss();
			else snack.show();
			break;
			case "双击":
				if(handler.hasMessages(2))
					super.finish();
					else{
					handler.sendEmptyMessageDelayed(2,2000);
					handler.obtainMessage(1,"再点一次退出！").sendToTarget();
					}
				break;
			}
			}
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		try{if(network!=null)
		unregisterReceiver(network);
		}catch(Exception e){}
		outState.putParcelable("ui",ui);
		outState.putInt("id", id);
		super.onSaveInstanceState(outState);
	}



	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		abdt.syncState();
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
			return abdt.onOptionsItemSelected(item);
		
		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		// TODO: Implement this method
		super.onConfigurationChanged(newConfig);
		abdt.onConfigurationChanged(newConfig);
	}
	@Override
	public void onDrawerSlide(View p1, float p2)
	{
		// TODO: Implement this method
	}

	@Override
	public void onDrawerOpened(View p1)
	{
		p1.setTag(null);
		//loadInfo();
		//else
		//ImageCache.load(ui.getLogo(),logo);
	}

	@Override
	public void onDrawerClosed(View p1)
	{
		if (p1.getTag() != null)
			onNavigationItemSelected((MenuItem)p1.getTag());
	}

	@Override
	public void onDrawerStateChanged(int p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onClick(View p1)
	{
		switch (p1.getId())
		{
			case R.id.nav_header_main_info:
				if(!PreferenceUtils.isLogin(this))
				startActivityForResult(new Intent(this, LoginActivity.class),233);
				else
				startActivity(new Intent(this,UserInfoActivity.class).putExtra("uid",PreferenceUtils.getUid(getApplicationContext())));
				break;
			case R.id.edit:
				if(!PreferenceUtils.isLogin(this))
					startActivityForResult(new Intent(this, LoginActivity.class),233);
				else
				startActivity(new Intent(this,AddBbsActivity.class));
				break;
			case R.id.message:
				if(PreferenceUtils.isLogin(this)){
					startActivity(new Intent(this, MessageActivity.class));
					drawerlayout.closeDrawer(Gravity.START);
					}
				break;
			case android.R.id.title:
				/*RecyclerView rv=(RecyclerView)findViewById(R.id.list);
				if(rv!=null){
					rv.smoothScrollToPosition(0);
				}*/
				if(current instanceof BbsListFragment)
					((BbsListFragment)current).onRefresh();
				break;
			case android.support.v7.appcompat.R.id.snackbar_action:
				super.finish();
				break;
			
		}
	}

	@Override
	public boolean onLongClick(View p1)
	{
		if(PreferenceUtils.isLogin(this))
		switch(p1.getId()){
			case R.id.logo:
				new AlertDialog.Builder(this).setMessage("上传头像").setNeutralButton("取消", null).setNegativeButton("拍照", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),2381);
						}
					}).setPositiveButton("相册", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),482);
						}
					}).show();
				break;
		}
		return true;
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode==RESULT_OK)
		switch(requestCode){
			case 233:
				loadInfo();
				break;
			case 482:
				new LogoUpload(this,data.getData(),this).start();
				break;
			case 2381://相机
				Bitmap bit=(Bitmap) data.getExtras().get("data");
				new LogoUpload(this,bit,this).start();
				break;
			default:
			//if(current!=null)current.onActivityResult(requestCode,resultCode,data);
			break;
		}
		super.onActivityResult(requestCode,resultCode,data);
	}

	@Override
	public void callback(boolean call)
	{
		if(call){
			logo.setTag(R.id.state,false);
			loadInfo();
		}else
		handler.obtainMessage(1,"头像上传失败").sendToTarget();
	}


	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		if( current instanceof DownloadFragment)
			current.onHiddenChanged(false);
		//loadInfo();
	}

	private void loadInfo(){
		if(PreferenceUtils.isLogin(this))
		new Thread(){
			public void run(){
				ui=UserUtils.getUserInfo(getApplicationContext(),PreferenceUtils.getUid(getApplicationContext()),false);
				handler.sendEmptyMessage(0);
			}
		}.start();
		else{
		username.setText("未登录");
		logo.setImageDrawable(VectorDrawableCompat.create(getResources(),R.drawable.logo_background,getTheme()));
		}
		
	}
	public void reloadExit(boolean exit)
	{
		if(exit)
			nmv.getMenu().findItem(R.id.menu_exit).setVisible(true);
		else
			nmv.getMenu().findItem(R.id.menu_exit).setVisible(false);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences p1, String p2)
	{
		switch(p2){
			case "login":
				loadInfo();
				break;
			case "name":
				username.setText(p1.getString("name","未登录"));
				break;
			case "exit_mode":
				reloadExit("侧边栏".equals(p1.getString(p2,null)));
				break;
		}
	}

	private void open(int tag, Class fragclass)
	{
		if (id == tag)return;
		id = tag;
		Fragment frag=getSupportFragmentManager().findFragmentByTag(tag + "");
		try
		{
			if (frag == null)frag = (Fragment)fragclass.newInstance();
		}
		catch (InstantiationException e)
		{}
		catch (IllegalAccessException e)
		{}
		FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
		if (current != null)
			ft.hide(current);
		if (frag.isAdded())
			ft.show(frag);
		else
			ft.add(R.id.main_index, frag, tag + "");
		ft.commit();
		current = frag;
	}
	private class Network extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context p1, Intent p2)
		{
			NetworkInfo ni=p2.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if(ni.isConnected()&&ni.isAvailable()){
				loadInfo();
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		moe.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}
private void checkUpdate(){
	AVQuery<AVObject> aq=new AVQuery<>();
	aq.setClassName("update");
	aq.orderByDescending("version_int");
	try
	{
		aq.whereGreaterThan("version_int",getPackageManager().getPackageInfo(getPackageName(),PackageManager.GET_CONFIGURATIONS).versionCode);
	}
	catch (PackageManager.NameNotFoundException e)
	{}
	aq.findInBackground(new FindCallback<AVObject>(){

			@Override
			public void done(List<AVObject> p1, AVException p2)
			{
				if(p2==null){
					if(p1.size()>0){
						final AVObject aob=p1.get(0);
						AVQuery<AVObject> aq=new AVQuery<>("message");
						aq.whereEqualTo("name",aob.get("version"));
						aq.findInBackground(new FindCallback<AVObject>(){

								@Override
								public void done(List<AVObject> p1, AVException p2)
								{
									if(p1!=null&&p1.size()>0)
										new AlertDialog.Builder(MainActivity.this).setTitle("v"+aob.get("version").toString()).setMessage(p1.get(0).get("msg").toString()).setPositiveButton("取消",null).setNegativeButton("下载",new DialogInterface.OnClickListener(){

												@Override
												public void onClick(DialogInterface p1, int p2)
												{
													DownloadItem di=new DownloadItem();
													di.setUrl(aob.get("url").toString());
													di.setTitle("妖火"+aob.get("version").toString()+".apk");
													di.setReferer(null);
													di.setTotal(9);
													di.setDir(getSharedPreferences("setting",0).getString("path",Environment.getExternalStorageDirectory().getAbsolutePath()+"/yaohuo"));
													di.setType(null);
													di.setTime(System.currentTimeMillis());
													//di.setCookie(CookieManager.getInstance().getCookie(url));
													di.save();
													startService(new Intent(getApplicationContext(),DownloadService.class).putExtra("down",di));

												}
											}).show();
									else
										Toast.makeText(getApplicationContext(),"获取新版本出错",Toast.LENGTH_SHORT).show();


								}
							});
					}}
			}
		});
}
	
	
}
