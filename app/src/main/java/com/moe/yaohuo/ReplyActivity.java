package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.view.MenuItem;
import com.moe.adapter.EmojiAdapter;
import java.util.ArrayList;
import com.moe.entity.ListItem;
import com.moe.entity.FloorItem;
import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.support.v7.widget.GridLayoutManager;
import java.io.InputStream;
import com.moe.utils.StringUtils;
import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import android.view.Menu;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.adapter.FileAdapter;
import android.content.Intent;
import android.net.Uri;
import java.io.FileNotFoundException;
import com.moe.entity.FileItem;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import com.moe.app.EmojiDialog;
import java.net.SocketTimeoutException;

public class ReplyActivity extends EventActivity implements View.OnClickListener,EmojiAdapter.OnItemClickListener
{
	private ArrayList<String> list;
	private ArrayList<FileItem> filelist;
	private EmojiAdapter ea;
	private ListItem bbs;
	private FloorItem floor;
	private EditText text;
	private CheckBox notify;
	private boolean send;
	private FileAdapter file;
	private RecyclerView rv;
	private EmojiDialog emoji;
	private View progressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			list = savedInstanceState.getStringArrayList("list");
			bbs=savedInstanceState.getParcelable("bbs");
			floor=savedInstanceState.getParcelable("floor");
			send=savedInstanceState.getBoolean("send");
		}
		else
		{
			bbs = getIntent().getExtras().getParcelable("bbs");
			floor = getIntent().getExtras().getParcelable("floor");
		}
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("回复");
		View view=LayoutInflater.from(this).inflate(R.layout.reply_view,(ViewGroup)findViewById(R.id.main_index),true);
		text = (EditText)view.findViewById(R.id.reply_content);
		progressBar=view.findViewById(R.id.progressbar);
		if(send)progressBar.setVisibility(View.VISIBLE);
		if (floor != null)
		{
			if(floor.getUser()==null){
				Toast.makeText(this,"无用户数据",Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			text.setHint("@" + floor.getUser().getName());
			getSupportActionBar().setTitle("回复" + floor.getFloor() + "楼");
		}
		view.findViewById(R.id.emoji).setOnClickListener(this);
		view.findViewById(R.id.file).setOnClickListener(this);
		notify = (CheckBox)view.findViewById(R.id.reply_notify);
		rv = (RecyclerView)view.findViewById(R.id.list);
		rv.setNestedScrollingEnabled(false);
		if (list == null)list = new ArrayList<>();
		rv.setAdapter(ea = new EmojiAdapter(list));
		ea.setOnItemClickListener(this);
		GridLayoutManager glm=new GridLayoutManager(this, 4);
		glm.setAutoMeasureEnabled(true);
		rv.setLayoutManager(glm);
		RecyclerView file=(RecyclerView)view.findViewById(R.id.filelist);
		file.setNestedScrollingEnabled(false);
		LinearLayoutManager llm=new LinearLayoutManager(this);
		llm.setAutoMeasureEnabled(true);
		file.setLayoutManager(llm);
		if(filelist==null)filelist=new ArrayList<>();
		file.setAdapter(this.file=new FileAdapter(filelist));
		if(savedInstanceState!=null){
			text.setText(savedInstanceState.getCharSequence("text"));
			notify.setChecked(savedInstanceState.getBoolean("notify"));
		}
		if (list.size() == 0)
		{
			//加载emoji
			try
			{
				InputStream is=getAssets().open("face");
				for (String face:StringUtils.getString(is).split(","))
					list.add(face);
				ea.notifyDataSetChanged();
				is.close();
			}
			catch (IOException e)
			{}
		}
		
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			
			case R.id.send:
				if (send)
					Toast.makeText(this, "正在发送，请勿重复点击", Toast.LENGTH_SHORT).show();
				else
				{
					if (text.getText().toString().trim().length() < 2)
					{
						Toast.makeText(this, "内容小于两个字符", Toast.LENGTH_SHORT).show();
					}else{
						send=true;
						progressBar.setVisibility(View.VISIBLE);
					new Thread(){
						public void run()
						{
							send();
						}
					}.start();
					}
				}
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data)
	{
		
		if(resultCode==RESULT_OK&&requestCode==284){
			final View view=LayoutInflater.from(this).inflate(R.layout.book_view, null);
			final EditText name=(EditText) view.findViewById(android.R.id.title);
			final EditText desc=(EditText) view.findViewById(android.R.id.summary);
			name.setHint("需要指名后缀");
			desc.setHint("文件描述");
			new AlertDialog.Builder(this).setTitle("输入文件名称").setView(view).setPositiveButton("确定", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface d, int p2)
					{
						if(TextUtils.isEmpty(name.getText().toString().trim())){
							handler.obtainMessage(5,"文件添加失败").sendToTarget();
							return;
							}
						FileItem fi=new FileItem();
						fi.setName(name.getText().toString());
						fi.setDesc(desc.getText().toString());
						fi.setUrl(data.getDataString());
						filelist.add(fi);
						file.notifyItemInserted(filelist.size()-1);
						
					}
				}).setNegativeButton("取消", null).show();
			
			}
			if(emoji!=null)emoji.onActivityResult(requestCode,resultCode,data);
	}
	
	@Override
	public void onClick(View p1)
	{
		switch (p1.getId())
		{
			case R.id.emoji:
				if (rv.getVisibility() == rv.VISIBLE)
					rv.setVisibility(rv.GONE);
				else
					rv.setVisibility(rv.VISIBLE);
				break;
			case R.id.file:
				if(floor!=null)
					Toast.makeText(getApplicationContext(),"文件回复对楼层无效",Toast.LENGTH_SHORT).show();
				rv.setVisibility(rv.GONE);
				startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("*/*"),284);
				break;
		}
	}

	
	private void send()
	{
		Connection conn=Jsoup.connect(PreferenceUtils.getHost(this) +( filelist.size()>0?getString(R.string.reply_file): getString(R.string.reply)))
			.timeout(10000)
			.data("lpage", "1")
			.data("vpage","1")
			.data("classid",bbs.getClassid()+"")
			.data("siteid", "1000")
			.data("face", "")
			.data("id", bbs.getId() + "")
			//.data("action", "add")
			.data("sendmsg", notify.isChecked() ?"1": "0")
			.data("sid", PreferenceUtils.getCookie(this))
			.userAgent(PreferenceUtils.getUserAgent())
			.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(this));
		if(floor!=null){
			conn.data("sendmsg2","1")
				.data("reply",floor.getFloor()+"")		
				.data("touserid",floor.getUser().getUid()+"");
		}
		if(filelist.size()>0){
			conn.data("action","gomod");
			conn.data("book_content",text.getText().toString().replaceAll("\n","[br]"));
			}
			else{
			conn.data("action","add");
			conn.data("content", text.getText().toString().replaceAll("\n","[br]"));
			}
		for(FileItem file:filelist){
			try
			{
				conn.data("book_file", file.getName(), getContentResolver().openInputStream(Uri.parse(file.getUrl())));
				conn.data("book_file_info",file.getDesc());
				
			}
			catch (FileNotFoundException e)
			{}
		}
		conn.data("num",filelist.size()+"");
		
		Document doc=null;
		try
		{
			doc=conn.post();
			Elements elements=doc.getElementsByClass("tip");
			if(elements.size()>0){

				try{
					if(elements.get(0).child(0).childNode(0).toString().equals("回复成功！")){
						handler.sendEmptyMessage(0);
					}else{
						handler.obtainMessage(1,"回复失败").sendToTarget();
					}

				}catch(Exception e){
					handler.obtainMessage(1,elements.get(0).childNode(0).toString().substring(1)).sendToTarget();
				}
			}else
				handler.obtainMessage(1,"流响应错误").sendToTarget();
		}
		catch (IOException e)
		{
			if(e instanceof SocketTimeoutException)
				handler.sendEmptyMessage(0);
				else
			handler.obtainMessage(1,e.getMessage()).sendToTarget();}
		
			send=false;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.send,menu);
		return true;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelable("bbs",bbs);
		outState.putParcelable("floor",floor);
		outState.putBoolean("notify", notify.isChecked());
		outState.putCharSequence("text", text.getText());
		outState.putStringArrayList("list", list);
		outState.putBoolean("send",send);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onItemClick(RecyclerView.Adapter ra, RecyclerView.ViewHolder vh)
	{
		text.getText().insert(text.getSelectionStart(), "[img]/face/" + list.get(vh.getAdapterPosition()) + ".gif[/img]");
	}

	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					setResult(RESULT_OK);
					finish();
					break;
				case 1:
					progressBar.setVisibility(View.INVISIBLE);
					Toast.makeText(getApplicationContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
					break;
				}
		}

	};
	
}
