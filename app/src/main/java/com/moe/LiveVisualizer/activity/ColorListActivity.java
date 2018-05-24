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
			OutputStream os=null;
			try
			{
				os = new FileOutputStream(colorFile);
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
		sendBroadcast(new Intent("color_changed"));
		}}.start();
	}
	
	private void read(){
		File color=new File(getExternalFilesDir(null),"color");
		if(!(color.exists()&&color.isFile()))return;
		BufferedReader read=null;
		try
		{
			read=new BufferedReader(new InputStreamReader(new FileInputStream(color)));
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
}
