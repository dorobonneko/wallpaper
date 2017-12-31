package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Handler;
import android.os.Message;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import com.moe.entity.ListItem;
import java.io.IOException;
import android.widget.EditText;
import android.view.Menu;
import android.widget.Toast;
import com.moe.app.EmojiDialog;
import android.content.Intent;

public class ModBbsActivity extends EventActivity
{
	private boolean success,sending;
	private ListItem bbs;
	private EditText title,content;
	private EmojiDialog emoji;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("修改贴子");
		LayoutInflater.from(this).inflate(R.layout.book_view,(ViewGroup)findViewById(R.id.main_index),true);
		title=(EditText) findViewById(android.R.id.title);
		content=(EditText) findViewById(android.R.id.summary);
		if(savedInstanceState==null){
			bbs=getIntent().getParcelableExtra("bbs");
			load();
		}else{
			bbs=savedInstanceState.getParcelable("bbs");
			success=savedInstanceState.getBoolean("success");
			title.setText(savedInstanceState.getCharSequence("title"));
			content.setText(savedInstanceState.getCharSequence("content"));
			if(!success)
				getSupportActionBar().setTitle("加载失败");
		}
	}

	private void load()
	{
		new Thread(){
			public void run(){
				try
				{
					Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/book_view_mod.aspx")
						.data("action", "go")
						.data("id", bbs.getId() + "")
						.data("siteid", "1000")
						.data("classid", bbs.getClassid() + "")
						.data("lpage", "1")
						.data("needpassword",getSharedPreferences("moe",0).getString("pwd",""))
						.userAgent(PreferenceUtils.getUserAgent())
						.cookie(PreferenceUtils.getCookieName(getApplicationContext()),PreferenceUtils.getCookie(getApplicationContext())).get();
						handler.obtainMessage(1,new String[]{doc.getElementsByAttributeValue("name","book_title").get(0).val(),doc.getElementsByAttributeValue("name","book_content").get(0).text()}).sendToTarget();
						return;
				}
				catch (Exception e)
				{}
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putBoolean("success",success);
		outState.putParcelable("bbs",bbs);
		outState.putCharSequence("title",title.getText());
		outState.putCharSequence("content",content.getText());
		super.onSaveInstanceState(outState);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.send,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.send:
				if(sending){
					Toast.makeText(getApplicationContext(),"正在发布",Toast.LENGTH_SHORT).show();
					return true;
					}
				if(success){
					if(title.getText().toString().trim().length()<5||content.getText().toString().trim().length()<15)
						Toast.makeText(getApplicationContext(),"字数未达到限制",Toast.LENGTH_SHORT).show();
						else
					new Thread(){
						public void run(){
							try
							{
								Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/book_view_mod.aspx")
									.data("action", "gomod")
									.data("id", bbs.getId()+"")
									.data("classid", bbs.getClassid()+"")
									.data("needpassword",getSharedPreferences("moe",0).getString("pwd",""))
									.data("siteid", "1000")
									.data("lpage", "1")
									.data("book_title",title.getText().toString())
									.data("book_content",content.getText().toString().replaceAll("\n","[br]"))
									.data("sid", PreferenceUtils.getCookie(getApplicationContext()))
									.userAgent(PreferenceUtils.getUserAgent())
									.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext()))
									.post();
									if(doc.text().indexOf("成功")!=-1){
										handler.sendEmptyMessage(2);
										return;
										}
							}
							catch (IOException e)
							{}
							handler.sendEmptyMessage(3);
						}
					}.start();
				}else
				Toast.makeText(getApplicationContext(),"加载失败，无法更改",Toast.LENGTH_SHORT).show();
				break;
			case R.id.emoji:
				if(emoji==null)emoji=new EmojiDialog(this);
				emoji.show(getWindow());
				break;
			default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					getSupportActionBar().setTitle("加载失败");
					break;
				case 1:
					success=true;
					String[] str=(String[]) msg.obj;
					title.setText(str[0]);
					content.setText(str[1]);
					break;
				case 2:
					setResult(RESULT_OK);
					finish();
					break;
				case 3:
					sending=false;
					Toast.makeText(getApplicationContext(),"修改失败",Toast.LENGTH_SHORT).show();
					break;
			}
		}
		
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		if(emoji!=null)emoji.onActivityResult(requestCode,resultCode,data);
	}
	
}
