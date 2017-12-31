package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.widget.ViewFlipper;
import android.view.Menu;
import com.moe.entity.UserItem;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import com.moe.utils.UserUtils;
import java.util.ArrayList;
import com.moe.entity.BbsItem;
import com.moe.utils.BbsUtils;
import android.widget.Spinner;
import com.moe.adapter.BbsSpinner;
import android.widget.EditText;
import android.widget.Toast;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import org.jsoup.Connection;
import java.io.IOException;
import org.jsoup.nodes.Document;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.adapter.VoteAdapter;
import com.moe.entity.FileItem;
import android.content.Intent;
import com.moe.adapter.FileAdapter;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import java.io.FileNotFoundException;
import android.net.Uri;
import java.io.FileInputStream;
import java.net.SocketTimeoutException;
import android.widget.ProgressBar;
import android.widget.FrameLayout;
import android.view.Gravity;
import com.moe.app.EmojiDialog;

public class AddBbsActivity extends EventActivity implements TabLayout.OnTabSelectedListener,View.OnClickListener
{
	private VoteAdapter vote;
	private ViewFlipper toggle;
	private UserItem ui;
	private TextView money;
	private ArrayList<BbsItem> list;
	private Spinner spinner;
	private boolean send;
	private EditText title,content;
	private BbsItem bbs;
	private ArrayList<FileItem> filelist;
	private FileAdapter file;
	private ProgressBar pb;
	private EmojiDialog emoji;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("发表新帖");
		ViewGroup group=(ViewGroup)findViewById(R.id.main_index);
		View v=LayoutInflater.from(this).inflate(R.layout.add_book,group,false);
		group.addView(v);
		title=(EditText)findViewById(android.R.id.title);
		content=(EditText)findViewById(android.R.id.summary);
		spinner=(Spinner)findViewById(R.id.spinner);
		money=(TextView)findViewById(R.id.money);
		toggle=(ViewFlipper)findViewById(R.id.viewflipper);
		toggle.setInAnimation(this,R.anim.slide_in);
		toggle.setOutAnimation(this,R.anim.slide_out);
		//投票列表处理
		findViewById(R.id.add_vote).setOnClickListener(this);
		RecyclerView rv=(RecyclerView)findViewById(R.id.vote_list);
		rv.setNestedScrollingEnabled(false);
		LinearLayoutManager llm=new LinearLayoutManager(this);
		llm.setAutoMeasureEnabled(true);
		rv.setLayoutManager(llm);
		rv.setAdapter(vote=new VoteAdapter());
		//
		//文件列表处理
		findViewById(R.id.file_add).setOnClickListener(this);
		RecyclerView file=(RecyclerView)findViewById(R.id.filelist);
		LinearLayoutManager manager=new LinearLayoutManager(this);
		manager.setAutoMeasureEnabled(true);
		file.setLayoutManager(manager);
		file.setAdapter(this.file=new FileAdapter(filelist=new ArrayList<>()));
		//
		TabLayout tl=(TabLayout)findViewById(R.id.tablayout);
		tl.addTab(tl.newTab().setText("普通"));
		tl.addTab(tl.newTab().setText("撒币"));
		tl.addTab(tl.newTab().setText("投票"));
		tl.addTab(tl.newTab().setText("文件"));
		tl.setOnTabSelectedListener(this);
		tl.getTabAt(0).select();
		bbs=getIntent().getParcelableExtra("bbs");
		pb=new ProgressBar(this);
		pb.setVisibility(pb.INVISIBLE);
		FrameLayout.LayoutParams fl=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		fl.gravity=Gravity.CENTER_HORIZONTAL;
		group.addView(pb,fl);
		loadData();
	}

	private void loadData()
	{
		new Thread(){
			public void run(){
				load();
			}
		}.start();
	}
private void load(){
	ui=UserUtils.getUserInfo(this,getSharedPreferences("moe",0).getInt("uid",0));
	list=(ArrayList<BbsItem>)BbsUtils.getBbs(getApplicationContext());
	handler.sendEmptyMessage(0);
}
	private  Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					if(ui!=null){
						money.setText("剩余妖精："+ui.getMoney());
					}
					if(list!=null){
						BbsItem bi=new BbsItem();
						bi.setTitle("选择板块");
						list.add(0,bi);
						spinner.setAdapter(new BbsSpinner(list));
						((BbsSpinner)spinner.getAdapter()).notifyDataSetChanged();
						if(bbs!=null){
							spinner.setSelection(list.indexOf(bbs));
						}
					}
					break;
				case 1:
					pb.setVisibility(pb.INVISIBLE);
					Toast.makeText(AddBbsActivity.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
					send=false;
					break;
				case 2:
					send=false;
					finish();
					break;
					
			}
		}
	
};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.send:
				if(spinner.getSelectedItemPosition()==0){
					Toast.makeText(getApplicationContext(),"请选好板块再发布",Toast.LENGTH_SHORT).show();
					return true;
				}
				if(send){
					Toast.makeText(getApplicationContext(),"正在发送，请勿重复点击",Toast.LENGTH_SHORT).show();
					return true;
				}
				boolean flag=true;
				if(title.getText().toString().trim().length()<2){
					flag=false;
					Toast.makeText(getApplicationContext(),"标题小于两个字符",Toast.LENGTH_SHORT).show();
				}else if(content.getText().toString().trim().length()<15){
					flag=false;
					Toast.makeText(getApplicationContext(),"内容小于15个字符",Toast.LENGTH_SHORT).show();
				}
				if(flag){
					send=true;
					pb.setVisibility(pb.VISIBLE);
					new Thread(){
						public void run(){
							send();
						}
					}.start();
					
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
	public void onTabSelected(TabLayout.Tab p1)
	{
		toggle.setDisplayedChild(p1.getPosition());
	}

	@Override
	public void onTabUnselected(TabLayout.Tab p1)
	{
		
	}

	@Override
	public void onTabReselected(TabLayout.Tab p1)
	{
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.send,menu);
		return true;
	}

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.add_vote:
				vote.add();
				break;
			case R.id.file_add:
				startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("*/*"),431);
				break;
		}
	}


	private void send(){
		String path=null;
		switch(toggle.getDisplayedChild()){
			case 0:
				path=getString(R.string.add);
				break;
			case 1:
				path=getString(R.string.sendmoney);
				break;
			case 2:
				path=getString(R.string.vote);
				break;
			case 3:
				path=getString(R.string.addfile);
				break;
		}
		Connection conn=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext())+path);
		conn.timeout(30000);
		conn.data("book_title",title.getText().toString())
		.data("action","gomod")
		.data("book_content",content.getText().toString().replaceAll("\n","[br]"))
		.data("sid",PreferenceUtils.getCookie(this))
		.data("siteid","1000")
		.data("classid",list.get(spinner.getSelectedItemPosition()).getClassid()+"")
		.userAgent(PreferenceUtils.getUserAgent())
		.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(this));
		switch(toggle.getDisplayedChild()){
			case 0:
				conn.data("sendmoney",((EditText)findViewById(R.id.n_sendmoney)).getText().toString());
				break;
			case 1:
				conn.data("freemoney",((EditText)findViewById(R.id.freemoney)).getText().toString())
				.data("freerule1","0")
				.data("freerule2",((EditText)findViewById(R.id.freerule2)).getText().toString());
				break;
			case 2:
				conn.data("sendmoney",((EditText)findViewById(R.id.v_sendmoney)).getText().toString());
				for(VoteAdapter.Vote v:vote.getList())
				conn.data("vote",v.getValue());
				conn.data("page","1");
				break;
			case 3:
				conn.data("sendmoney",((EditText)findViewById(R.id.f_sendmoney)).getText().toString());
				for(FileItem file:filelist){
					try
					{
						Uri uri=Uri.parse(file.getUrl());
						//switch(uri.getScheme()){
							//case "content":
								conn.data("book_file", file.getName(),getContentResolver().openInputStream(uri));
								//break;
							//case "file":
								//conn.data("book_file", file.getName(),new FileInputStream(uri.getPath()));
								//break;
						//}
						conn.data("book_file_info",file.getDesc());
					}
					catch (FileNotFoundException e)
					{}
				}
				conn.data("num",filelist.size()+"");
				
				break;
		}
		Document doc;
		try
		{
			doc=conn.post();
			if(doc.text().indexOf("返回主题")!=-1)
				handler.sendEmptyMessage(2);
				else
				handler.obtainMessage(1,doc.text()).sendToTarget();
				
					
		}catch(SocketTimeoutException st){
			handler.sendEmptyMessage(2);
		}
		catch (IOException e)
		{
			handler.obtainMessage(1,e.getMessage()).sendToTarget();
		} //发表失败
		//handler.obtainMessage(1,"发布失败").sendToTarget();
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data)
	{
		if(resultCode==RESULT_OK&&requestCode==431){
			final View view=LayoutInflater.from(this).inflate(R.layout.book_view, null);
			final EditText name=(EditText) view.findViewById(android.R.id.title);
			final EditText desc=(EditText) view.findViewById(android.R.id.summary);
			name.setHint("需要指名后缀");
			//desc.setHint("文件描述，不能小于一个字符");
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
	
}
