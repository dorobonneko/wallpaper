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
import com.moe.adapter.EventAdapter;
import android.widget.PopupWindow;
import android.view.Gravity;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.util.TypedValue;
import com.bumptech.glide.Glide;
import com.moe.utils.PreferenceUtils;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import android.support.v4.view.ViewCompat;
import android.graphics.drawable.VectorDrawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.widget.FrameLayout;
import android.view.Display;
import android.graphics.Rect;
import com.moe.widget.PopupBackground;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.json.JSONException;
import android.widget.Spinner;
import android.widget.TextView;
import android.text.TextUtils;
public class EmojiDialog extends AlertDialog implements TabLayout.OnTabSelectedListener,View.OnClickListener,EventAdapter.OnItemClickListener,EventAdapter.OnItemLongClickListener,EmojiAdapter.OnItemTouchListener,PopupWindow.OnDismissListener
{
	private ArrayList<String> emoji_list,images_list;
	private EditText text;
	private ViewFlipper toggle;
	private ProgressDialog progress;
	private Upload up;
	private ImagesDatabase database;
	private ArrayList<UbbItem> ubb_list,ubb_ready_list;
	//private EditText ubb_key;//已不再使用
	private RecyclerView ready;
	private EventAdapter images,emoji,ubb,ubb_ready;
	private PopupWindow emoji_show;
	private JSONArray ubb_json;
	private AlertDialog ubb_add;
	private Activity activity;
	public EmojiDialog(Activity activity)
	{
		super(activity);
		this.activity=activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if (savedInstanceState!=null)
		{
			emoji_list = savedInstanceState.getParcelableArrayList("emoji_list");
			images_list = savedInstanceState.getParcelableArrayList("images");
			ubb_list = savedInstanceState.getParcelableArrayList("ubb_list");
			ubb_ready_list = savedInstanceState.getParcelableArrayList("ubb_ready_list");
		}
		database = ImagesDatabase.getInstance(getContext());
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
		toggle = (ViewFlipper)findViewById(R.id.toggle);
		toggle.setInAnimation(getContext(),R.anim.slide_in);
		toggle.setOutAnimation(getContext(),R.anim.slide_out);
		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.file_add).setOnClickListener(this);
		//ubb_key = (EditText) findViewById(R.id.key);
		findViewById(R.id.insert).setOnClickListener(this);
		findViewById(R.id.clear).setOnClickListener(this);
		findViewById(R.id.ubb_add).setOnClickListener(this);
		//emoji
		RecyclerView emoji=(RecyclerView) findViewById(R.id.emoji_list);
		emoji.setLayoutManager(new GridLayoutManager(getContext(),4));
		if (emoji_list==null)emoji_list = new ArrayList<>();
		this.emoji = new EmojiAdapter(emoji_list);
		if (emoji_list.size()==0)
			try
			{
				InputStream is=getContext().getAssets().open("face");
				for (String face:StringUtils.getString(is).split(","))
					emoji_list.add(face);
				this.emoji.notifyDataSetChanged();
				is.close();
			}
			catch (IOException e)
			{}
		emoji.setAdapter(this.emoji);
		this.emoji.setOnItemClickListener(this);
		this.emoji.setOnItemLongClickListener(this);
		((EmojiAdapter)this.emoji).setOnItemTouchListener(this);
		emoji.getLayoutManager().setAutoMeasureEnabled(false);
		//images
		if (images_list==null)images_list = new ArrayList<>();
		if (images_list.size()==0)
			images_list.addAll(database.query());
		RecyclerView images=(RecyclerView) findViewById(R.id.image_list);
		images.setLayoutManager(new GridLayoutManager(getContext(),4));
		images.setAdapter(this.images=new ImagesAdapter(images_list));
		this.images.setOnItemClickListener(this);
		this.images.setOnItemLongClickListener(this);
		images.getLayoutManager().setAutoMeasureEnabled(false);
		//ubb
		RecyclerView ubb=(RecyclerView) findViewById(R.id.ubb_list);
		ubb.setLayoutManager(new AutoLayoutManager());
		ubb.getLayoutManager().setAutoMeasureEnabled(false);
		if (ubb_list==null)
			ubb_list = new ArrayList<>();
		this.ubb = new UbbAdapter(ubb_list);
		this.ubb.setOnItemClickListener(this);
		this.ubb.setOnItemLongClickListener(this);
		ubb.setAdapter(this.ubb);
		//ubb_ready
		ready = (RecyclerView) findViewById(R.id.ubb_ready);
		ready.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
		if (ubb_ready_list==null)ubb_ready_list = new ArrayList<>();
		ready.setAdapter(ubb_ready=new UbbAdapter(ubb_ready_list));
		ubb_ready.setOnItemClickListener(this);
		ready.addItemDecoration(new Divider(0xffaaaaaa,0,0,0,2,getContext().getResources().getDisplayMetrics()));
		ViewCompat.setBackground(ready,VectorDrawableCompat.create(getContext().getResources(),R.drawable.ubb_ready_background,getContext().getTheme()));
		if (ubb_list.size()==0)
			loadUbb();

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
		emoji_show = null;
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
		switch (p1.getId())
		{
			case R.id.cancel:
				dismiss();
				break;
			case R.id.file_add:
				activity.startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),9731);
				break;
			case R.id.insert:
				StringBuffer sb=new StringBuffer(text.getText().subSequence(text.getSelectionStart(),text.getSelectionEnd()));
				for (UbbItem ui:ubb_ready_list)
				{
					int index=ui.getData().indexOf("=");
					sb.insert(0,"["+ui.getData()+"]").append("[/"+(index==-1?ui.getData():ui.getData().substring(0,index))+"]");
				}
				text.getText().replace(text.getSelectionStart(),text.getSelectionEnd(),sb.toString());
				dismiss();
				break;
			case R.id.clear:
				int size=ubb_ready_list.size();
				ubb_ready_list.clear();
				ubb_ready.notifyItemRangeRemoved(0,size);
				break;
			case R.id.ubb_add:
				if (ubb_add==null)
				{
					final View v=LayoutInflater.from(getContext()).inflate(R.layout.ubb_add_view,null);
					ubb_add = new AlertDialog.Builder(getContext()).setTitle("添加ubb").setView(v).setPositiveButton("取消",null).setNegativeButton("添加",new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								TextView title=(TextView) v.findViewById(R.id.title);
								TextView data=(TextView) v.findViewById(R.id.summary);
								Spinner mode=(Spinner) v.findViewById(R.id.mode);
								boolean flag=true;
								if (TextUtils.isEmpty(title.getText()))
									flag = false;
								if (TextUtils.isEmpty(data.getText()))
									flag = false;
								if (flag)
								{
									JSONObject jo=new JSONObject();
									try
									{
										jo.put("tit",title.getText());
										jo.put("data",data.getText());
										jo.put("mode",mode.getSelectedItemPosition());
										ubb_json.put(jo);
										commit();
										UbbItem ui=new UbbItem();
										ui.setTitle(title.getText().toString());
										ui.setData(data.getText().toString());
										ui.setMode(mode.getSelectedItemPosition());
										ubb_list.add(ui);
										ubb.notifyItemInserted(ubb_list.size()-1);
									}
									catch (JSONException e)
									{}
								}
								else
								{
									Toast.makeText(getContext(),"内容不能为空",Toast.LENGTH_SHORT).show();
								}

							}
						}).create();
				}
				ubb_add.show();
				break;
		}
	}

	@Override
	public void onItemClick(EventAdapter ra, EventAdapter.ViewHolder vh)
	{
		if (vh.getAdapterPosition()==-1)return;
		if (text!=null)
		{
			String data=null;
			if (ra==images)
			{
				data = "[img]"+images_list.get(vh.getAdapterPosition())+"[/img]";
			}
			else if (ra==emoji)
			{
				data = "[img]/face/"+emoji_list.get(vh.getAdapterPosition())+".gif[/img]";
			}
			else if (ra==ubb_ready)
			{
				ubb_ready_list.remove(vh.getAdapterPosition());
				ubb.notifyItemRemoved(vh.getAdapterPosition());
				return;
			}
			else if (ra==ubb)
			{
				UbbItem ui=ubb_list.get(vh.getAdapterPosition());
				switch (ui.getMode())
				{
					case 0:
						ubb_ready_list.add(ubb_list.get(vh.getAdapterPosition()));
						ubb_ready.notifyItemInserted(ubb_ready_list.size()-1);
						ready.scrollToPosition(ubb_ready_list.size()-1);
						return;
					case 1:
						int index=ui.getData().indexOf("=");
						data = "["+ui.getData()+"]"+text.getText().subSequence(text.getSelectionStart(),text.getSelectionEnd()).toString()+"[/"+(index==-1?ui.getData():ui.getData().substring(0,index))+"]";
						break;
					case 2:
						data = "["+ui.getData()+"]";
						break;
				}
			}
			text.getText().replace(text.getSelectionStart(),text.getSelectionEnd(),data);
			dismiss();
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode==9731&&resultCode==Activity.RESULT_OK)
		{
			File file=null;
			DocumentFile docfile=null;
			switch (data.getData().getScheme())
			{
				case "file":
					file = new File(data.getData().getPath());
					break;
				case "content":
					Cursor c=android.support.v4.content.ContentResolverCompat.query(getContext().getContentResolver(),data.getData(),new String[]{MediaStore.Files.FileColumns.DATA},null,null,null,null);
					if (c.moveToNext())
						try
						{
							file = new File(c.getString(0));
						}
						catch (Exception e)
						{
							docfile = DocumentFile.fromSingleUri(getContext(),data.getData());
						}
					c.close();
					break;
			}
			if (file==null&&docfile==null)
			{
				Toast.makeText(getContext(),"文件获取失败",Toast.LENGTH_SHORT).show();
				return;
			}
			if (progress==null)
			{progress = new ProgressDialog(getContext());
				progress.setCancelable(false);
				progress.setCanceledOnTouchOutside(false);
				progress.setTitle("正在上传");
				progress.setButton("取消",new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							if (up!=null)up.close();
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
				up = new Upload(file==null?getContext().getContentResolver().openInputStream(data.getData()):new FileInputStream(file),file==null?docfile.length():file.length());
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
			switch (msg.what)
			{
				case 0:
					Toast.makeText(getContext(),"上传失败",Toast.LENGTH_SHORT).show();
					break;
				case 1:
					progress.setMessage(up.getProgress()+"%");
					progress.setProgress(up.getProgress());
					if (up.getStates()>0)
					{
						if (up.getPid()==null)
							progress.setMessage("上传失败");
						else
						{
							images_list.add(0,"http://ww1.sinaimg.cn/large/"+up.getPid()+".jpg");
							images.notifyItemInserted(0);
							database.insert(images_list.get(0));
							progress.dismiss();
						}
					}
					else
					if (progress.isShowing())
						sendEmptyMessageDelayed(1,100);
					break;
				case 2:
					//加载ubb
					try
					{
						ubb_json = (JSONArray) msg.obj;
						for (int i=0;i<ubb_json.length();i++)
						{
							JSONObject jo=ubb_json.getJSONObject(i);
							UbbItem ui=new UbbItem();
							ui.setTitle(jo.getString("tit"));
							ui.setData(jo.getString("data"));
							ui.setMode(jo.getInt("mode"));
							ubb_list.add(ui);
							ubb.notifyItemInserted(ubb_list.size()-1);
						}
					}
					catch (Exception e)
					{}
					break;
			}
		}

	};

	@Override
	public boolean onItemLongClick(EventAdapter adapter, final EventAdapter.ViewHolder vh)
	{
		if (adapter==images)
		{
			new AlertDialog.Builder(getContext()).setTitle("确认删除？").setNegativeButton("手滑了",null).setPositiveButton("确认",new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						database.delete(images_list.get(vh.getAdapterPosition()));
						images_list.remove(vh.getAdapterPosition());
						images.notifyItemRemoved(vh.getAdapterPosition());
					}
				}).show();
		}
		else if (adapter==emoji)
		{
			if (emoji_show==null)
			{
				FrameLayout group=new PopupBackground(getContext());
				ImageView image=new ImageView(getContext());
				image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				int size=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,72,getContext().getResources().getDisplayMetrics());
				group.addView(image,size,size);
				emoji_show = new PopupWindow();
				emoji_show.setContentView(group);
				Display display=getWindow().getWindowManager().getDefaultDisplay();
				emoji_show.setWidth(display.getWidth());
				emoji_show.setHeight(display.getHeight());
				emoji_show.getContentView().setPadding(10,10,10,10);
				ViewCompat.setBackground(image,VectorDrawableCompat.create(getContext().getResources(),R.drawable.imagestip,getContext().getTheme()));
				emoji_show.setOnDismissListener(this);
				ViewCompat.setBackground(group,VectorDrawableCompat.create(getContext().getResources(),R.drawable.popup_background,getContext().getTheme()));
			}
			PopupBackground pb=(PopupBackground) emoji_show.getContentView();
			ImageView image=(ImageView)pb.getChildAt(0);
			Glide.with(getContext()).load(PreferenceUtils.getHost(getContext())+"/face/"+emoji_list.get(vh.getAdapterPosition())+".gif").diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.yaohuo).into(image);
			emoji_show.showAtLocation(vh.itemView,Gravity.START,0,0);
			Rect rect=new Rect();
			vh.itemView.getGlobalVisibleRect(rect);
			//pb.setShowRect(rect);
			image.setX(rect.left+(rect.right-rect.left-image.getLayoutParams().width)/2+TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getContext().getResources().getDisplayMetrics()));
			image.setY(vh.itemView.getTop()+image.getLayoutParams().height/2);
			//WindowManager.LayoutParams wl=getWindow().getAttributes();
			//wl.alpha=0.8f;
			//getWindow().setAttributes(wl);
		}
		else if (adapter==ubb)
		{
			//删除ubb
			new AlertDialog.Builder(getContext()).setTitle("确认删除？").setMessage(ubb_list.get(vh.getAdapterPosition()).getTitle()).setNegativeButton("手滑了",null).setPositiveButton("确认",new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						ubb_json.remove(vh.getAdapterPosition());
						ubb_list.remove(vh.getAdapterPosition());
						ubb.notifyItemRemoved(vh.getAdapterPosition());
						commit();
					}
				}).show();
		}
		return true;
	}

	@Override
	public boolean OnItemTouch(MotionEvent event)
	{
		switch (event.getAction())
		{
			case event.ACTION_CANCEL:
			case event.ACTION_UP:
				if (emoji_show!=null&&emoji_show.isShowing())
					emoji_show.dismiss();
				break;
		}
		return false;
	}

	@Override
	public void onDismiss()
	{
		WindowManager.LayoutParams wl=getWindow().getAttributes();
		wl.alpha = 1f;
		getWindow().setAttributes(wl);
	}



	@Override
	public void show()
	{
		throw new RuntimeException("you must call show with window");
	}
	public void show(Window window)
	{
		View v=window.getCurrentFocus();
		if (v!=null&&v instanceof EditText)
		{
			text = (EditText) v;
			super.show();
		}
		else
		{
			Toast.makeText(getContext(),"无可编辑视图",Toast.LENGTH_SHORT).show();
		}
	}
	private void loadUbb()
	{
		new Thread(){
			public void run()
			{
				File file=new File(getContext().getFilesDir(),"ubb");
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				byte[] buffer=new byte[512];
				int len=0;

				if (!file.exists())
				{
					try
					{
						OutputStream os=getContext().openFileOutput("ubb",getContext().MODE_PRIVATE);
						InputStream is=getContext().getAssets().open("ubb");
						while ((len=is.read(buffer))!=-1)
						{
							baos.write(buffer,0,len);
							os.write(buffer,0,len);
						}
						baos.flush();
						os.flush();
						is.close();
						os.close();
					}
					catch (IOException e)
					{}
				}
				else
				{
					try
					{
						InputStream is=getContext().openFileInput("ubb");
						while ((len=is.read(buffer))!=-1)
						{
							baos.write(buffer,0,len);
						}
						baos.flush();
						is.close();
					}
					catch (IOException e)
					{}
				}
				try
				{
					handler.obtainMessage(2,new JSONArray(baos.toString())).sendToTarget();
				}
				catch (JSONException e)
				{}
				try
				{
					baos.close();
				}
				catch (IOException e)
				{}
			}
		}.start();
	}
	private Object sync=new Object();
	private void commit()
	{
		new Thread(){
			public void run()
			{
				synchronized (sync)
				{
					try
					{
						OutputStream os=getContext().openFileOutput("ubb",getContext().MODE_PRIVATE);
						os.write(ubb_json.toString().getBytes());
						os.flush();
						os.close();
					}
					catch (IOException e)
					{}
				}
			}
		}.start();
	}
}
