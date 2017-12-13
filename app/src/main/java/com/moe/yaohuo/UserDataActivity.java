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

public class UserDataActivity extends EventActivity implements View.OnClickListener
{
	private UserItem ui;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if(savedInstanceState==null){
			ui=getIntent().getParcelableExtra("ui");
		}else{
			ui=savedInstanceState.getParcelable("ui");
		}
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("个人资料");
		LayoutInflater.from(this).inflate(R.layout.user_data,(ViewGroup)findViewById(R.id.main_index),true);
		findViewById(R.id.click_logo).setOnClickListener(this);
		findViewById(R.id.click_name).setOnClickListener(this);
		findViewById(R.id.click_sex).setOnClickListener(this);
		findViewById(R.id.click_birthday).setOnClickListener(this);
		findViewById(R.id.click_style).setOnClickListener(this);
		findViewById(R.id.click_exit).setOnClickListener(this);
		handler.sendEmptyMessage(0);
	}
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
								sex.setImageResource(R.drawable.gender_male);
								break;
							case 1:
								sex.setImageResource(R.drawable.gender_female);
								break;
						}
						((TextView)findViewById(R.id.style)).setText(ui.getSign());
					}
					break;
			}
		}
		
	};
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
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
	
}
