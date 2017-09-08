package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import com.moe.widget.ViewImage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.GlideUrl;
import java.util.HashMap;
import java.util.Map;
import com.moe.utils.PreferenceUtils;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.os.Handler;
import android.os.Message;

public class ViewImageActivity extends AppCompatActivity
{
	private String url;
	private int count;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if(savedInstanceState==null){
			url=getIntent().getStringExtra("url");
		}else{
			url=savedInstanceState.getString("url");
		}
		ImageView iv=new ViewImage(this);
		setContentView(iv,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		GlideUrl gu=new GlideUrl(url, new Headers(){

				@Override
				public Map<String, String> getHeaders()
				{
					Map<String,String> map=new HashMap<>();
					map.put("Cookie",PreferenceUtils.getCookieName(getApplicationContext())+"="+PreferenceUtils.getCookie(getApplicationContext()));
					return map;
				}
			});
		Glide.with(this).load(gu).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.yaohuo).into(iv);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putString("url",url);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(0,0);
	}
	private float x,y;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		switch(ev.getAction()){
			case ev.ACTION_DOWN:
				x=ev.getRawX();
				y=ev.getRawY();
				//super.dispatchTouchEvent(ev);
				break;
			case ev.ACTION_UP:
				if(ev.getRawX()==x&&ev.getRawY()==y){
					count++;
					handler.removeMessages(0);
					handler.sendEmptyMessageDelayed(0,400);
					return true;
					}
				break;
		}
		return super.dispatchTouchEvent(ev);
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					if(count==1)
						finish();
					count=0;
					break;
			}
		}
		
	};
}
