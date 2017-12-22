package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.MenuItem;
import com.moe.entity.UserItem;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import com.moe.utils.ImageCache;
import android.widget.TextView;
import com.moe.utils.UserUtils;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.view.Menu;
import com.moe.utils.PreferenceUtils;
import android.content.Intent;
import android.net.Uri;

public class UserDataActivity extends EventActivity implements View.OnClickListener,UserUtils.Callback
{
	private UserItem ui;
	private int uid;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if(savedInstanceState==null){
			uid=getIntent().getIntExtra("uid",0);
			ui=getIntent().getParcelableExtra("ui");
			if(ui!=null)uid=ui.getUid();
		}else{
			uid=savedInstanceState.getInt("uid");
			ui=savedInstanceState.getParcelable("ui");
		}
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("个人资料");
		LayoutInflater.from(this).inflate(R.layout.user_data_view,(ViewGroup)findViewById(R.id.main_index),true);
		/*findViewById(R.id.click_logo).setOnClickListener(this);
		findViewById(R.id.click_name).setOnClickListener(this);
		findViewById(R.id.click_sex).setOnClickListener(this);
		findViewById(R.id.click_birthday).setOnClickListener(this);
		findViewById(R.id.click_style).setOnClickListener(this);
		findViewById(R.id.click_exit).setOnClickListener(this);
		*///handler.sendEmptyMessage(0);
		if(ui==null)
			UserUtils.loadUserItem(this,uid,this);
			else
			onLoad(ui);
	}

	@Override
	public void onLoad(UserItem ui)
	{
		if(ui!=null)
		{
			this.ui=ui;
			ImageCache.load(ui.getLogo(),(ImageView)findViewById(android.R.id.icon));
			((TextView)findViewById(R.id.user_data_name)).setText(ui.getName());
			((TextView)findViewById(R.id.user_data_uid)).setText(ui.getUid()+"");
			ImageView sex=(ImageView)findViewById(R.id.user_data_sex);
			sex.setImageDrawable(VectorDrawableCompat.create(getResources(),ui.getState()==1?(ui.getSex()==1?R.drawable.gender_female_online:R.drawable.gender_male_online):(ui.getSex()==1?R.drawable.gender_female_offline:R.drawable.gender_male_offline),getTheme()));
			((TextView)findViewById(R.id.user_data_level)).setText(ui.getLevel()+"");
			((TextView)findViewById(R.id.user_data_nickName)).setText(ui.getNickName());
			((TextView)findViewById(R.id.user_data_age)).setText(ui.getAge()+"");
			((TextView)findViewById(R.id.user_data_sign)).setText(ui.getSign());
		}
	}

	/*
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					if(ui!=null){
						ImageCache.load(ui.getLogo(),(ImageView)findViewById(android.R.id.icon));
						((TextView)findViewById(R.id.name)).setText(ui.getName());
						((TextView)findViewById(R.id.uid)).setText(ui.getUid()+"");
						ImageView sex=(ImageView)findViewById(R.id.sex);
						switch(ui.getSex()){
							case 0:
								//sex.setImageResource(R.drawable.gender_male);
								break;
							case 1:
								//sex.setImageResource(R.drawable.gender_female);
								break;
						}
						((TextView)findViewById(R.id.style)).setText(ui.getSign());
					}
					break;
			}
		}
		
	};*/
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putInt("uid",uid);
		outState.putParcelable("ui",ui);
		super.onSaveInstanceState(outState);
	}

	
	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.click_logo:
				break;
			case R.id.click_name:
				break;
			case R.id.click_sex:
				break;
			case R.id.click_birthday:
				break;
			case R.id.click_style:
				break;
			case R.id.click_exit:
				getSharedPreferences("moe",0).edit().putInt("uid",0).putString("cookie","").putString("name",null).commit();
				finish();
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO: Implement this method
		if(uid==PreferenceUtils.getUid(this))
		menu.add(0,0,0,"修改资料");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case 0:
				startActivity(new Intent(this,WebViewActivity.class).setData(Uri.parse(PreferenceUtils.getHost(this)+"/bbs/modifyuserinfo.aspx?siteid=1000")));
		break;}
		return super.onOptionsItemSelected(item);
	}
	
}
