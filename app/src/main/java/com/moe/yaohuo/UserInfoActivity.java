package com.moe.yaohuo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import com.moe.entity.UserItem;
import com.moe.utils.UserUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.text.Html;
import com.moe.utils.ImageCache;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.content.Intent;
import android.net.Uri;
import com.moe.utils.PreferenceUtils;
import android.view.Menu;
import java.io.IOException;
import android.os.Handler;
import android.os.Message;
import org.jsoup.Jsoup;
import android.widget.Toast;
import android.view.MenuItem;

public class UserInfoActivity extends BaseActivity implements UserUtils.Callback,View.OnClickListener
{
	private int uid;
	private UserItem ui;
	private ImageView logo,usersex;
	private TextView username,usersign,useruid,userspace;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if(savedInstanceState==null){
			uid=getIntent().getIntExtra("uid",0);
		}else{
			uid=savedInstanceState.getInt("uid");
			ui=savedInstanceState.getParcelable("ui");
		}
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info_view);
		
		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		if(Build.VERSION.SDK_INT>=19)
		{
			CollapsingToolbarLayout.LayoutParams param=(CollapsingToolbarLayout.LayoutParams)toolbar.getLayoutParams();
			param.topMargin=getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));

		}
		//toolbar.setTitleTextColor(0xffffffff);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(null);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		username=(TextView) findViewById(R.id.user_info_view_username);
		usersign=(TextView) findViewById(R.id.user_info_view_usersign);
		useruid=(TextView) findViewById(R.id.user_info_view_uid);
		userspace=(TextView) findViewById(R.id.user_info_view_userspace);
		usersex=(ImageView) findViewById(R.id.user_info_view_sex);
		logo=(ImageView) findViewById(R.id.icon);
		findViewById(R.id.user_info_view_space).setOnClickListener(this);
		findViewById(R.id.friend).setOnClickListener(this);
		findViewById(R.id.send).setOnClickListener(this);
		logo.setOnClickListener(this);
		useruid.setText(uid+"");
		if(uid==PreferenceUtils.getUid(this))
			findViewById(R.id.user_info_view_bottom).setVisibility(View.GONE);
		if(ui==null){
			new Thread(){
				public void run(){
					handler.obtainMessage(0,UserUtils.getUserInfo(getApplicationContext(),uid,false)).sendToTarget();
				}
			}.start();
			
		}else{
			onLoad(ui);
		}
	}

	@Override
	public void onLoad(UserItem ui)
	{
		if(ui!=null){
		this.ui=ui;
			username.setText(Html.fromHtml(ui.getName()));
			usersign.setText(ui.getSign());
			userspace.setText(username.getText()+"的空间");
			ImageCache.load(ui.getLogo(),logo);
			usersex.setImageDrawable(VectorDrawableCompat.create(getResources(),ui.getState()==1?(ui.getSex()==1?R.drawable.gender_female_online:R.drawable.gender_male_online):(ui.getSex()==1?R.drawable.gender_female_offline:R.drawable.gender_male_offline),getTheme()));
		}
	}

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.icon:
				if(ui!=null)
				startActivity(new Intent(this,ViewImageActivity.class).setData(Uri.parse(ui.getLogo())));
				break;
			case R.id.user_info_view_space:
				startActivity(new Intent(this,UserSpaceActivity.class).putExtra("uid",uid));
				break;
			case R.id.friend:
				if(uid!=PreferenceUtils.getUid(this))
					new Thread(){
						public void run(){
							try
							{
								Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/FriendList.aspx")
									.data("siteid", "1000")
									.data("action", "addfriend")
									.data("friendtype", "0")
									.data("touserid", uid + "")
									.data("sid", PreferenceUtils.getCookie(getApplicationContext()))
									.userAgent(PreferenceUtils.getUserAgent())
									.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext())).post();
								handler.obtainMessage(1,"已加为好友").sendToTarget();
							}
							catch (IOException e)
							{handler.obtainMessage(1,"网络连接失败").sendToTarget();}
						}
					}.start();
				break;
			case R.id.send:
				startActivity(new Intent(this,SendMessageActivity.class).putExtra("uid",uid));
				break;
		}
	}

	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					onLoad((UserItem)msg.obj);
					break;
				case 1:
					Toast.makeText(getApplicationContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
					break;
			}
		}

	};

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelable("ui",ui);
		outState.putInt("uid",uid);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.user_info,menu);
		menu.findItem(R.id.black).setVisible(uid!=PreferenceUtils.getUid(this));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.more_info:
				startActivity(new Intent(this,UserDataActivity.class).putExtra("uid",uid));
				break;
			case R.id.black:
				if(uid!=PreferenceUtils.getUid(this))
					new Thread(){
						public void run(){
							try
							{
								Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/FriendList.aspx")
									.data("siteid", "1000")
									.data("action", "addfriend")
									.data("friendtype", "1")
									.data("touserid", uid + "")
									.data("sid", PreferenceUtils.getCookie(getApplicationContext()))
									.userAgent(PreferenceUtils.getUserAgent())
									.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext())).post();
								handler.obtainMessage(1,"已拉黑").sendToTarget();
							}
							catch (IOException e)
							{handler.obtainMessage(1,"网络连接失败").sendToTarget();}
						}
					}.start();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish()
	{
		// TODO: Implement this method
		super.finish();
		overridePendingTransition(0,0);
	}
	
}
