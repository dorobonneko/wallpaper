package com.moe.app;
import android.support.v7.app.AlertDialog;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.View;
import android.widget.EditText;
import android.view.LayoutInflater;
import com.moe.yaohuo.R;
import android.widget.Toast;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.widget.ViewFlipper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import com.moe.adapter.EmojiAdapter;
import java.util.ArrayList;
import java.io.InputStream;
import com.moe.utils.StringUtils;
import java.io.IOException;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.content.Intent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.provider.MediaStore;
import android.database.Cursor;
import java.io.File;
import com.moe.thread.Upload;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import com.moe.adapter.ImagesAdapter;
import com.moe.database.ImagesDatabase;
import android.view.WindowManager;
import com.moe.entity.UbbItem;
import com.moe.view.AutoLayoutManager;
import com.moe.adapter.UbbAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.view.Divider;
import android.support.v4.provider.DocumentFile;
import android.net.Uri;
public class EmojiDialog extends AlertDialog implements TabLayout.OnTabSelectedListener,View.OnClickListener,EmojiAdapter.OnItemClickListener,ImagesAdapter.OnItemLongClickListener,UbbAdapter.OnItemClickListener
{
	private ArrayList<String> emoji_list,images_list;
	private EditText text;
	private ViewFlipper toggle;
	private Activity activity;
	private ProgressDialog progress;
	private Upload up;
	private ImagesAdapter ia;
	private ImagesDatabase database;
	private ArrayList<UbbItem> ubb_list,ubb_ready_list;
	private UbbAdapter ubb;
	private EditText ubb_key;
	private RecyclerView ready;
	public EmojiDialog(Activity activity){
		super(activity);
		this.activity=activity;
		}

	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if(savedInstanceState!=null){
			emoji_list=savedInstanceState.getParcelableArrayList("emoji_list");
			images_list=savedInstanceState.getParcelableArrayList("images");
			ubb_list=savedInstanceState.getParcelableArrayList("ubb_list");
			ubb_ready_list=savedInstanceState.getParcelableArrayList("ubb_ready_list");
		}
		database=ImagesDatabase.getInstance(activity);
		super.onCreate(savedInstanceState);
		//LayoutInflater.from(getContext()).inflate(R.layout.emoji_view,null);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		//setCancelable(false);
		setCanceledOnTouchOutside(false);
		setContentView(R.layout.emoji_view);
		TabLayout tab=(TabLayout)findViewById(R.id.tablayout);
		tab.addTab(tab.newTab().setText("表情"));
		tab.addTab(tab.newTab().setText("UBB"));
		tab.addTab(tab.newTab().setText("图库"));
		tab.setOnTabSelectedListener(this);
		toggle=(ViewFlipper)findViewById(R.id.toggle);
		toggle.setInAnimation(getContext(),R.anim.slide_in);
		toggle.setOutAnimation(getContext(),R.anim.slide_out);
		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.file_add).setOnClickListener(this);
		ubb_key=(EditText) findViewById(R.id.key);
		findViewById(R.id.insert).setOnClickListener(this);
		findViewById(R.id.clear).setOnClickListener(this);
		//emoji
		RecyclerView emoji=(RecyclerView) findViewById(R.id.emoji_list);
		emoji.setLayoutManager(new GridLayoutManager(getContext(),4));
		if(emoji_list==null)emoji_list=new ArrayList<>();
		EmojiAdapter ea=new EmojiAdapter(emoji_list);
		if(emoji_list.size()==0)
			try
			{
				InputStream is=getContext().getAssets().open("face");
				for (String face:StringUtils.getString(is).split(","))
					emoji_list.add(face);
				ea.notifyDataSetChanged();
				is.close();
			}
			catch (IOException e)
			{}
		emoji.setAdapter(ea);
		ea.setOnItemClickListener(this);
		emoji.getLayoutManager().setAutoMeasureEnabled(false);
		//images
		if(images_list==null)images_list=new ArrayList<>();
		if(images_list.size()==0)
			images_list.addAll(database.query());
		RecyclerView images=(RecyclerView) findViewById(R.id.image_list);
		images.setLayoutManager(new GridLayoutManager(getContext(),4));
		images.setAdapter(ia=new ImagesAdapter(images_list));
		ia.setOnItemClickListener(this);
		ia.setOnItemLongClickListener(this);
		images.getLayoutManager().setAutoMeasureEnabled(false);
		//ubb
		RecyclerView ubb=(RecyclerView) findViewById(R.id.ubb_list);
		ubb.setLayoutManager(new AutoLayoutManager());
		ubb.getLayoutManager().setAutoMeasureEnabled(false);
		if(ubb_list==null)
			ubb_list=new ArrayList<>();
		if(ubb_list.size()==0){
			try
			{
				JSONArray ja=new JSONArray(StringUtils.getString(getContext().getAssets().open("ubb")));
				for(int i=0;i<ja.length();i++){
					JSONObject jo=ja.getJSONObject(i);
					UbbItem ui=new UbbItem();
					ui.setTitle(jo.getString("tit"));
					ui.setData(jo.getString("data"));
					ui.setMode(jo.getInt("mode"));
					ubb_list.add(ui);
				}
			}
			catch (Exception e)
			{}
		}
		UbbAdapter ua=new UbbAdapter(ubb_list);
		ua.setOnItemClickListener(this);
		ubb.setAdapter(ua);
		//ubb_ready
		ready=(RecyclerView) findViewById(R.id.ubb_ready);
		ready.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
		if(ubb_ready_list==null)ubb_ready_list=new ArrayList<>();
		ready.setAdapter(this.ubb=new UbbAdapter(ubb_ready_list));
		this.ubb.setOnItemClickListener(this);
		ready.addItemDecoration(new Divider(0xffaaaaaa,0,0,0,2,getContext().getResources().getDisplayMetrics()));
	}

	@Override
	public Bundle onSaveInstanceState()
	{
		// TODO: Implement this method
		Bundle b=super.onSaveInstanceState();
		b.putStringArrayList("emoji_list",emoji_list);
		b.putStringArrayList("images",images_list);
		b.putParcelableArrayList("ubb_list",ubb_list);
		b.putParcelableArrayList("ubb_ready_list",ubb_ready_list);
		return b;
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
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.cancel:
				dismiss();
				break;
			case R.id.file_add:
				activity.startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),9731);
				break;
			case R.id.insert:
				StringBuffer sb=new StringBuffer(ubb_key.getText());
				for(UbbItem ui:ubb_ready_list){
					int index=ui.getData().indexOf("=");
					sb.insert(0,"["+ui.getData()+"]").append("[/"+(index==-1?ui.getData():ui.getData().substring(0,index))+"]");
				}
				text.getText().replace(text.getSelectionStart(),text.getSelectionEnd(),sb.toString());
				dismiss();
				break;
			case R.id.clear:
				int size=ubb_ready_list.size();
				ubb_ready_list.clear();
				ubb.notifyItemRangeRemoved(0,size);
				break;
		}
	}

	@Override
	public void onItemClick(RecyclerView.Adapter ra, RecyclerView.ViewHolder vh)
	{
		if(vh.getAdapterPosition()==-1)return;
		if(text!=null){
			String data=null;
			if(ra instanceof ImagesAdapter){
				data="[img]"+images_list.get(vh.getAdapterPosition())+"[/img]";
			}else if(ra instanceof EmojiAdapter){
				data="[img]/face/"+emoji_list.get(vh.getAdapterPosition())+".gif[/img]";
			}else if(ra==ubb){
				ubb_ready_list.remove(vh.getAdapterPosition());
				ubb.notifyItemRemoved(vh.getAdapterPosition());
				return;
			}else{
				UbbItem ui=ubb_list.get(vh.getAdapterPosition());
				switch(ui.getMode()){
					case 0:
						ubb_ready_list.add(ubb_list.get(vh.getAdapterPosition()));
						ubb.notifyItemInserted(ubb_ready_list.size()-1);
						ready.scrollToPosition(ubb_ready_list.size()-1);
						return;
					case 1:
						int index=ui.getData().indexOf("=");
						data="["+ui.getData()+"]"+ubb_key.getText().toString()+"[/"+(index==-1?ui.getData():ui.getData().substring(0,index))+"]";
						break;
					case 2:
						data="["+ui.getData()+"]";
						break;
				}
			}
			text.getText().insert(text.getSelectionEnd(),data);
			dismiss();
			}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode==9731&&resultCode==Activity.RESULT_OK){
			File file=null;
			DocumentFile docfile=null;
			switch(data.getData().getScheme()){
				case "file":
					file=new File(data.getData().getPath());
					break;
				case "content":
					Cursor c=android.support.v4.content.ContentResolverCompat.query(activity.getContentResolver(),data.getData(),new String[]{MediaStore.Files.FileColumns.DATA},null,null,null,null);
					if(c.moveToNext())
						try{
						file=new File(c.getString(0));
						}catch(Exception e){
							docfile=DocumentFile.fromSingleUri(getContext(),data.getData());
						}
					c.close();
					break;
			}
			if(file==null&&docfile==null){
				Toast.makeText(getContext(),"文件获取失败",Toast.LENGTH_SHORT).show();
				return;
			}
			if(progress==null){progress=new ProgressDialog(activity);
				progress.setCancelable(false);
				progress.setCanceledOnTouchOutside(false);
				progress.setTitle("正在上传");
				progress.setButton("取消", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							if(up!=null)up.close();
							p1.dismiss();
						}
					});
				progress.setMax(100);
				progress.setProgressStyle(android.R.attr.progressBarStyleHorizontal);
			}
			progress.show();
			handler.sendEmptyMessage(1);
			try
			{
				up=new Upload(file==null?getContext().getContentResolver().openInputStream (data.getData()):new FileInputStream(file),file==null?docfile.length():file.length());
				up.start();
			}
			catch (FileNotFoundException e)
			{handler.sendEmptyMessage(0);}
		}
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					Toast.makeText(getContext(),"上传失败",Toast.LENGTH_SHORT).show();
					break;
				case 1:
					progress.setMessage(up.getProgress()+"%");
					progress.setProgress(up.getProgress());
					if(up.getStates()>0){
						if(up.getPid()==null)
							progress.setMessage("上传失败");
							else{
							images_list.add(0,"http://ww1.sinaimg.cn/large/"+up.getPid()+".jpg");
							ia.notifyItemInserted(0);
							database.insert(images_list.get(0));
							progress.dismiss();
							}
					}else
					if(progress.isShowing())
						sendEmptyMessageDelayed(1,1000);
					break;
				case 2:
					break;
			}
		}
	
	};

	@Override
	public boolean onItemLongClick(RecyclerView.Adapter adapter, final RecyclerView.ViewHolder vh)
	{
		new AlertDialog.Builder(activity).setTitle("确认删除？").setNegativeButton("手滑了", null).setPositiveButton("确认", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					database.delete(images_list.get(vh.getAdapterPosition()));
					images_list.remove(vh.getAdapterPosition());
					ia.notifyItemRemoved(vh.getAdapterPosition());
				}
			}).show();
		return true;
	}



	@Override
	public void show()
	{
		throw new RuntimeException("you must call show with window");
	}
	public void show(Window window){
		View v=window.getCurrentFocus();
		if(v!=null&& v instanceof EditText){
			text=(EditText) v;
			super.show();
		}else{
			Toast.makeText(getContext(),"无可编辑视图",Toast.LENGTH_SHORT).show();
		}
	}
}
