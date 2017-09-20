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
import com.moe.fragment.ListFragment;
import android.view.Gravity;
import android.support.design.widget.FloatingActionButton;
import android.content.Intent;
import com.moe.fragment.BbsFragment;
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
import com.moe.download.LogoUpload;


public class MainActivity extends BaseActivity implements 
NavigationView.OnNavigationItemSelectedListener,
DrawerLayout.DrawerListener,
View.OnClickListener,
View.OnLongClickListener,
LogoUpload.Callback
{
	private UserItem ui;
	private Snackbar snack;
	private NavigationView nmv;
	private ActionBarDrawerToggle abdt;
	private DrawerLayout drawerlayout;
	private Fragment current;
	private int id;
	private CircleImageView logo;
	private SharedPreferences moe;
	private TextView username;
	private Network network;
	private TextView msg;
    @Override
	@SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState)
    {
		moe=getSharedPreferences("moe",0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		nmv = (NavigationView)findViewById(R.id.main_leftselectedView);
		drawerlayout = (DrawerLayout)findViewById(R.id.main_drawerlayout);
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		toolbar.setTitleTextColor(0xffffffff);
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
		drawerlayout.addDrawerListener(this);
		logo = (CircleImageView)nmv.getHeaderView(0).findViewById(R.id.logo);
		logo.setOnClickListener(this);
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
		if (savedInstanceState != null)
		{
			ui=savedInstanceState.getParcelable("ui");
			id = savedInstanceState.getInt("id");
			current = getSupportFragmentManager().findFragmentByTag(id + "");
			handler.sendEmptyMessage(0);
		}
		else
		{
			onNavigationItemSelected(nmv.getMenu().findItem(R.id.menu_new));
			if(PreferenceUtils.isLogin(this)){
			username.setText(moe.getString("name",null));
			}
			//loadInfo();
		}
		IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(network=new Network(),filter);
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
			switch (p1.getItemId())
			{
				case R.id.menu_new:
					open(p1.getItemId(), ListFragment.class);
					break;
				case R.id.menu_bbs:
					open(p1.getItemId(), BbsFragment.class);
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
			}
			getSupportActionBar().setTitle(p1.getTitle());
			}
		return true;
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
						logo.setImageResource(R.drawable.yaohuo);
					}else{
						username.setText(ui.getName());
						//ImageCache.load(ui.getLogo(),logo);
						Glide.with(MainActivity.this).load(ui.getLogo()).diskCacheStrategy(DiskCacheStrategy.ALL).into(logo);
						moe.edit().putString("name",ui.getName()).commit();
						if(ui.getMsg()>0){
							MainActivity.this.msg.setVisibility(View.VISIBLE);
							MainActivity.this.msg.setText(ui.getMsg()+"");
							//MainActivity.this.msg.setClipToOutline(true);
							}
					}
					break;
				case 1:
					Toast.makeText(getApplicationContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
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
		if(!(current instanceof ListFragment)){
			onNavigationItemSelected(nmv.getMenu().findItem(R.id.menu_new).setChecked(true));
		}
		else{
			if (snack == null)
				snack = Snackbar.make(drawerlayout.getChildAt(0), "确认退出？", 1500).setAction("退出", this).setActionTextColor(getResources().getColor(R.color.primary)).setCallback(new Snackbar.Callback(){
					public void onDismissed(Snackbar bar,int event){
						switch(event){
							case Snackbar.Callback.DISMISS_EVENT_SWIPE:
								snack=null;
						}
					}
				});
			if (snack.isShown())snack.dismiss();
			else snack.show();
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
		if(ui==null)loadInfo();
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
			case R.id.logo:
				if(!PreferenceUtils.isLogin(this))
				startActivityForResult(new Intent(this, LoginActivity.class),233);
				else
				startActivity(new Intent(this,UserInfoActivity.class).putExtra("uid",moe.getInt("uid",0)));
				break;
			case R.id.edit:
				if(!PreferenceUtils.isLogin(this))
					startActivityForResult(new Intent(this, LoginActivity.class),233);
				else
				startActivity(new Intent(this,AddBbsActivity.class));
				break;
			case R.id.message:
				if(PreferenceUtils.isLogin(this))
					startActivity(new Intent(this, MessageActivity.class));
				break;
			case android.R.id.title:
				RecyclerView rv=(RecyclerView)findViewById(R.id.list);
				if(rv!=null){
					rv.smoothScrollToPosition(0);
				}
				break;
			case android.support.v7.appcompat.R.id.snackbar_action:
				super.finish();
				break;
		}
	}

	@Override
	public boolean onLongClick(View p1)
	{
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
		switch(requestCode){
			case 233:
				if(resultCode==RESULT_OK)
				loadInfo();
				break;
			case 482:
				new LogoUpload(this,data.getData(),this).start();
				break;
			case 2381:
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
		if(call)loadInfo();
		else
		handler.obtainMessage(1,"头像上传失败").sendToTarget();
	}


	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		if( current instanceof DownloadFragment)
			current.onHiddenChanged(false);
		loadInfo();
	}

	private void loadInfo(){
		new Thread(){
			public void run(){
				ui=UserUtils.getUserInfo(getApplicationContext(),moe.getInt("uid",0));
				handler.sendEmptyMessage(0);
			}
		}.start();
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
}
