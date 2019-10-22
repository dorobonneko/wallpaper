package com.moe.LiveVisualizer.activity;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import com.moe.LiveVisualizer.widget.ColorPickerView;
import android.content.SharedPreferences;
import java.io.OutputStream;
import android.os.Handler;
import android.os.Message;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.moe.LiveVisualizer.utils.ColorList;
import android.widget.ListView;
import com.moe.LiveVisualizer.adapter.ColorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.widget.PopupWindow;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;
import android.graphics.drawable.BitmapDrawable;
import com.moe.LiveVisualizer.internal.ShadowDrawable;
import android.view.ContextThemeWrapper;
import com.moe.LiveVisualizer.widget.PopupLayout;
import android.content.Intent;
import android.widget.EditText;
import android.content.DialogInterface;
import android.graphics.Color;
import com.moe.LiveVisualizer.app.ColorDialog;
import android.widget.*;
import android.net.*;

public class ColorListActivity extends Activity implements ColorPickerView.OnColorCheckedListener,ListView.OnItemClickListener,View.OnClickListener
{
	private ColorList colorList;
	private static final int INPUT=0;
	private AlertDialog colorPicker;
	private ListView listview;
	private Object fileLock=new Object();
	private PopupWindow popup;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		colorList=new ColorList();
		listview=new ListView(this);
		listview.setAdapter(new ColorAdapter(colorList));
		listview.setOnItemClickListener(this);
		listview.setFitsSystemWindows(true);
		setContentView(listview);
		setTitle("色彩组");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		read();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuItem item=menu.add(0,0,0,"手动输入");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item=menu.add(1,1,1,"调色板");
		menu.add(2,2,2,"导入");
		menu.add(3,3,3,"导出");
		menu.add(4,4,4,"清空");
		//VectorDrawableCompat plus=VectorDrawableCompat.create(getResources(),R.drawable.plus,getTheme());
		//plus.setTint(0xffffffff);
		//item.setIcon(plus);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case android.R.id.home:
				finish();
				return true;
			case 0:
				if(colorPicker==null){
				 final EditText view=new EditText(this);
				// view.setOnColorCheckedListener(this);
					colorPicker = new AlertDialog.Builder(this).setTitle("输入十六进制颜色").setView(view).setPositiveButton("取消", null).setNegativeButton("确定", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								try{
								colorList.add( Color.parseColor(view.getText().toString()));
								((ColorAdapter)listview.getAdapter()).notifyDataSetChanged();
								notifyColorFileChanged();
								}catch(Exception e){}
							}
						}).create();
				 }
				 colorPicker.show();
				break;
			case 1:
				
				ColorDialog cd=(ColorDialog) getFragmentManager().findFragmentByTag("color");
				if(cd==null){cd=new ColorDialog();
				cd.setOnColorCheckedListener(this);}
				if(cd.isAdded())getFragmentManager().beginTransaction().show(cd).commit();
				else cd.show(getFragmentManager(),"color");
				break;
			case 2:
				Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("*/*");
				startActivityForResult(intent,2534);
				break;
			case 3:
				if(colorList.isEmpty()){
					Toast.makeText(this,"没有数据",Toast.LENGTH_SHORT).show();
				}else{
					final File backup=getExternalFilesDir("backup");
					if(!backup.exists())
						backup.mkdirs();
					final EditText filename=new EditText(this);
					new AlertDialog.Builder(this).setTitle("文件命名").setView(filename).setPositiveButton("保存", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								final File file=new File(backup,filename.getText().toString().trim().concat(".mcl"));
								if(file.exists()){
									Toast.makeText(ColorListActivity.this,"文件已存在",Toast.LENGTH_SHORT).show();
									return;
								}
								new Thread(){
									public void run(){
										save(file);
										runOnUiThread(new Runnable(){

												@Override
												public void run()
												{
													Toast.makeText(ColorListActivity.this,"保存成功\n".concat(backup.getAbsolutePath()),Toast.LENGTH_SHORT).show();
												}
											});
									}
								}.start();
							}
						}).setNegativeButton(android.R.string.cancel, null).show();
				}
				break;
				case 4:
					colorList.clear();
					((ColorAdapter)listview.getAdapter()).notifyDataSetChanged();
					notifyColorFileChanged();
					break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onColorChecked(int color)
	{
		colorList.add(color);
		((ColorAdapter)listview.getAdapter()).notifyDataSetChanged();
		notifyColorFileChanged();
		//colorPicker.dismiss();
		ColorDialog cd=(ColorDialog) getFragmentManager().findFragmentByTag("color");
		if(cd!=null)cd.dismiss();
		}

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		if(popup==null){
			PopupLayout group=new PopupLayout(this);
			group.addView(new View(this),new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,p2.getHeight()));
			TextView remove=new TextView(this);
			remove.setText("移除");
			remove.setOnClickListener(this);
			remove.setGravity(Gravity.CENTER_VERTICAL);
			group.addView(remove,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,p2.getHeight()));
			popup=new PopupWindow(p2.getContext());
			popup.setContentView(group);
			popup.setWidth(p2.getHeight()*2);
			popup.setHeight(p2.getHeight()*2);
			popup.setBackgroundDrawable(new ShadowDrawable());
			popup.setTouchable(true);
			popup.setOutsideTouchable(true);
			remove.setId(popup.hashCode());
		}
		((PopupLayout)popup.getContentView()).getChildAt(0).setBackgroundColor(colorList.get(p3));
		popup.showAsDropDown(p2,(p2.getWidth()-p2.getHeight())/2,-(int)(p2.getHeight()/2.0f));
		popup.getContentView().setId(p3);
	}


	/*private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case INPUT:
					break;
			}
		}
		
	};*/
	private void notifyColorFileChanged(){
		new Thread(){
			public void run(){
		synchronized(fileLock){
			File colorFile=new File(getExternalFilesDir(null),"color");
			save(colorFile);
		}
		sendBroadcast(new Intent("color_changed"));
		}}.start();
	}
	private void save(File file){
		OutputStream os=null;
		try
		{
			os = new FileOutputStream(file);
			for(int i=0;i<colorList.size();i++){
				os.write((colorList.get(i)+"\n").getBytes());
			}
			os.flush();
		}
		catch (IOException e)
		{}finally{
			try
			{
				if ( os != null )os.close();
			}
			catch (IOException e)
			{}
		}
	}
	private void read(){
		File color=new File(getExternalFilesDir(null),"color");
		if(!(color.exists()&&color.isFile()))return;
		override(Uri.fromFile(color));
	}
	private void append(Uri file){
		BufferedReader read=null;
		try
		{
			read=new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(file)));
			String line;
			while((line=read.readLine())!=null){
				try{
					colorList.add(Integer.parseInt(line));
					((ColorAdapter)listview.getAdapter()).notifyDataSetChanged();
				}catch(NumberFormatException e){}
			}
		}catch(IOException i){}
		finally{
			try
			{
				if ( read != null )read.close();
			}
			catch (IOException e)
			{}
		}
		notifyColorFileChanged();
	}
	private void override(Uri file){
		colorList.clear();
		((ColorAdapter)listview.getAdapter()).notifyDataSetChanged();
		BufferedReader read=null;
		try
		{
			read=new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(file)));
			String line;
			while((line=read.readLine())!=null){
				try{
					colorList.add(Integer.parseInt(line));
					((ColorAdapter)listview.getAdapter()).notifyDataSetChanged();
				}catch(NumberFormatException e){}
			}
		}catch(IOException i){}
		finally{
			try
			{
				if ( read != null )read.close();
			}
			catch (IOException e)
			{}
		}
		notifyColorFileChanged();
	}
	@Override
	public void onClick(View p1)
	{
		if(popup!=null&&p1.getId()==popup.hashCode()){
			popup.dismiss();
			colorList.remove(popup.getContentView().getId());
			((ColorAdapter)listview.getAdapter()).notifyDataSetChanged();
			notifyColorFileChanged();
			}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data)
	{
		switch(requestCode){
			case 2534:
				if(resultCode==RESULT_OK){
					new AlertDialog.Builder(this).setTitle("操作").setPositiveButton("追加", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								append(data.getData());
							}
						}).setNegativeButton("覆盖", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								override(data.getData());
							}
						}).setNeutralButton(android.R.string.cancel, null).show();
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
