package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.EditText;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.view.Gravity;
import android.view.Menu;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import java.io.IOException;
import android.widget.LinearLayout;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import org.jsoup.nodes.Document;
import com.moe.app.EmojiDialog;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;

public class SendMessageActivity extends EventActivity
{
	private EditText content,title;
	private int id;
	private EmojiDialog emoji;
	private View progressBar;
	private boolean send;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("发消息");
		content=new EditText(this);
		FrameLayout.LayoutParams param=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		param.topMargin=15;
		param.leftMargin=30;
		param.rightMargin=30;
		//param.bottomMargin=15;
		content.setLayoutParams(param);
		content.setMinLines(5);
		content.setGravity(Gravity.LEFT|Gravity.TOP);
		title=new EditText(this);
		title.setLayoutParams(param);
		title.setSingleLine();
		title.setGravity(Gravity.LEFT);
		title.setHint("标题：可忽略");
		FrameLayout parent=new FrameLayout(this);
		LinearLayout ll=new LinearLayout(this);
		ll.setOrientation(ll.VERTICAL);
		ll.addView(title);
		ll.addView(content);
		parent.addView(ll,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
		FrameLayout.LayoutParams pl=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		pl.gravity=Gravity.CENTER_HORIZONTAL;
		parent.addView(progressBar=new ProgressBar(this),pl);
		if(savedInstanceState!=null)
		progressBar.setVisibility(savedInstanceState.getBoolean("send")?View.VISIBLE:View.INVISIBLE);
		else
		progressBar.setVisibility(View.INVISIBLE);
		((FrameLayout)findViewById(R.id.main_index)).addView(parent,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT));
		if(savedInstanceState==null){
			id=getIntent().getIntExtra("uid",0);
		}else{
			id=savedInstanceState.getInt("uid");
			content.setText(savedInstanceState.getCharSequence("content"));
			title.setText(savedInstanceState.getCharSequence("title"));
		}
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putInt("uid",id);
		outState.putCharSequence("title",title.getText());
		outState.putCharSequence("content",content.getText());
		outState.putBoolean("send",send);
		super.onSaveInstanceState(outState);
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			
			case R.id.send:
				if(send)
					handler.obtainMessage(1,"正在发送").sendToTarget();
					else
				if(content.getText().toString().trim().length()>0)
				send();
				else
					Toast.makeText(this,"内容不能为空",Toast.LENGTH_SHORT).show();
				break;
			case R.id.emoji:
				if(emoji==null)emoji=new EmojiDialog(this);
				emoji.show(getWindow());
				break;
		default:
		return super.onOptionsItemSelected(item);
		}return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.send,menu);
		return true;
	}
	private void send(){
		send=true;
		progressBar.setVisibility(View.VISIBLE);
		new Thread(){
			public void run(){
				try
				{
					Document doc= Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/messagelist_add.aspx")
						.data("touseridlist", id + "")
						.data("content", content.getText().toString().replaceAll("\n","[br]"))
						.data("title", title.getText().toString())
						.data("action", "gomod")
						.data("classid", "0")
						.data("siteid", "1000")
						.data("types", "0")
						.data("issystem", "0")
						.data("sid", PreferenceUtils.getCookie(getApplicationContext()))
						.userAgent(PreferenceUtils.getUserAgent())
						.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext()))
						.post();
						if(doc.text().indexOf("成功")!=-1)
						handler.sendEmptyMessage(0);
						else
						handler.obtainMessage(1,"回复失败").sendToTarget();
						
				}
				catch (IOException e)
				{handler.obtainMessage(1,e.getMessage()).sendToTarget();}
				
			}
		}.start();
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			send=false;
			progressBar.setVisibility(View.INVISIBLE);
			switch(msg.what){
				case 0:
					setResult(RESULT_OK);
					finish();
					break;
				case 1:
					Toast.makeText(getApplicationContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
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
