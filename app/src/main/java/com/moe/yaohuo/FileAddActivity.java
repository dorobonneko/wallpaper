package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.moe.adapter.FileAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.entity.FileItem;
import java.util.ArrayList;
import com.moe.entity.BbsItem;
import android.view.View;
import android.content.Intent;
import android.view.Menu;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;
import com.moe.entity.ListItem;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import org.jsoup.Connection;
import android.net.Uri;
import org.jsoup.nodes.Document;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.FrameLayout;
import android.view.Gravity;
import java.net.SocketTimeoutException;

public class FileAddActivity extends EventActivity implements View.OnClickListener
{
	private FileAdapter fa;
	private ArrayList<FileItem> list;
	private ListItem bbs;
	private boolean sending;
	private ProgressBar pb;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("文件续传");
		ViewGroup group=(ViewGroup)findViewById(R.id.main_index);
		LayoutInflater.from(this).inflate(R.layout.file_add_view,group,true);
		findViewById(R.id.file_add).setOnClickListener(this);
		RecyclerView rv=(RecyclerView) findViewById(R.id.filelist);
		rv.setLayoutManager(new LinearLayoutManager(this));
		pb=new ProgressBar(this);
		pb.setVisibility(pb.INVISIBLE);
		FrameLayout.LayoutParams fl=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		fl.gravity=Gravity.CENTER_HORIZONTAL;
		group.addView(pb,fl);
		if(savedInstanceState==null){
			bbs=getIntent().getParcelableExtra("bbs");
		}else{
			bbs=savedInstanceState.getParcelable("bbs");
			list=savedInstanceState.getParcelableArrayList("list");
			sending=savedInstanceState.getBoolean("sending");
		}
		if(list==null)list=new ArrayList<>();
		rv.setAdapter(fa=new FileAdapter(list));
	}

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.file_add:
				startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("*/*"),428);
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data)
	{
		if(resultCode==RESULT_OK&&requestCode==428){
			View v=LayoutInflater.from(this).inflate(R.layout.book_view,null);
			final EditText title=(EditText) v.findViewById(android.R.id.title);
			final EditText summary=(EditText) v.findViewById(android.R.id.summary);
			title.setHint("文件名（需指定后缀）");
			summary.setHint("文件描述");
			new AlertDialog.Builder(this).setTitle("文件信息").setView(v).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						if(title.getText().toString().trim().length()==0)
							Toast.makeText(getApplicationContext(),"文件名不能为空",Toast.LENGTH_SHORT).show();
						else{
							FileItem fi=new FileItem();
						fi.setUrl(data.getDataString());
						fi.setName(title.getText().toString());
						fi.setDesc(summary.getText().toString());
						list.add(fi);
						fa.notifyItemInserted(list.size()-1);
						}
					}
				}).show();
			
		}
	}


	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelableArrayList("list",list);
		outState.putBoolean("sending",sending);
		outState.putParcelable("bbs",bbs);
		super.onSaveInstanceState(outState);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case 0:
				if(sending){
					Toast.makeText(getApplicationContext(),"正在发送，请勿重复点击",Toast.LENGTH_SHORT).show();
				return true;
				}
				pb.setVisibility(pb.VISIBLE);
				sending=true;
				new Thread(){
					public void run(){
						try{
						Connection conn=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext())+"/bbs/book_view_addfileadd.aspx")
						.timeout(10000)
						.data("action","gomod")
						.data("classid",bbs.getClassid()+"")
						.data("id",bbs.getId()+"")
						.data("num",list.size()+"")
						.data("needpassword",getSharedPreferences("moe",0).getString("pwd",""))
						.cookie(PreferenceUtils.getCookieName(getApplicationContext()),PreferenceUtils.getCookie(getApplicationContext()))
						.userAgent(PreferenceUtils.getUserAgent());
						for(FileItem fi:list){
						conn.data("book_file",fi.getName(),getContentResolver().openInputStream(Uri.parse(fi.getUrl())))
						.data("book_file_info",fi.getDesc().replaceAll("\n","[br]"));
						}
						Document doc=conn.post();
						if(doc.text().indexOf("成功")!=-1){
							handler.sendEmptyMessage(1);
							return;
						}
					}catch(SocketTimeoutException st){
						handler.sendEmptyMessage(1);
						return;
					}
					catch(Exception e){
						
					}
					sending=false;
					handler.sendEmptyMessage(0);
					}
				}.start();
				break;
				default:
		return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0,0,0,"添加");
		menu.getItem(0).setIcon(R.drawable.plus).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					pb.setVisibility(pb.INVISIBLE);
					Toast.makeText(getApplicationContext(),"续传失败",Toast.LENGTH_SHORT).show();
					break;
				case 1:
					setResult(RESULT_OK);
					finish();
					break;
			}
		}
		
	};
}
