package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.support.v7.app.ActionBar;
import com.moe.utils.StorageHelper;
import java.util.List;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ArrayList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.adapter.FolderAdapter;
import java.io.FileFilter;
import android.widget.TextView;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Build;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.AppBarLayout;
import android.content.res.TypedArray;

public class DirectoryActivity extends EventActivity implements FileFilter,Comparator<File>,FolderAdapter.OnItemClickListener,View.OnClickListener
{
	private RecyclerView list_view;
	private File current;
	private File[] index;
	private ArrayList<File> list=new ArrayList<>();
	private TextView index_message;
	private boolean file;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.folder_picker_view,(ViewGroup)findViewById(R.id.main_index),true);
		ViewGroup group=(ViewGroup)findViewById(R.id.coordinatorlayout);
		LayoutInflater.from(this).inflate(R.layout.folder_picker_bottom,group,true);
		Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
		AppBarLayout.LayoutParams al=(AppBarLayout.LayoutParams) toolbar.getLayoutParams();
		al.setScrollFlags(al.SCROLL_FLAG_SCROLL|al.SCROLL_FLAG_SNAP|al.SCROLL_FLAG_ENTER_ALWAYS|al.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
		toolbar.setLayoutParams(al);
		list_view = (RecyclerView)findViewById(R.id.folder_picker_view_list);
		list_view.setLayoutManager(new LinearLayoutManager(this));
		index_message = (TextView)findViewById(R.id.folder_picker_view_index);
		FolderAdapter fa=new FolderAdapter(list);
		list_view.setAdapter(fa);
		fa.setOnItemClickListener(this);
		index_message.setText("/");
		file = getIntent().getAction() != null;
		if (file)
		{
			findViewById(R.id.folder_picker_view_bottom).setVisibility(View.GONE);
			list_view.setPadding(0,0,0,0);
			getSupportActionBar().setTitle("选择文件");
			String[] path=StorageHelper.getAllPath(this).toArray(new String[0]);
			index = new File[path.length];
			for (int i=0;i < path.length;i++)
				index[i] = new File(path[i]);


		}
		else
		{
			getSupportActionBar().setTitle("选择文件夹");
			if (Build.VERSION.SDK_INT > 20)
			{
				index = new File[1];
				index[0] = Environment.getExternalStorageDirectory();
			}
			else
			{
				String[] path=StorageHelper.getAllPath(this).toArray(new String[0]);
				index = new File[path.length];
				for (int i=0;i < path.length;i++)
					index[i] = new File(path[i]);
			}
		}
		loadList(index);
	}
	private void loadList(String[] list)
	{
		File[] tmp=new File[list.length];
		for (int i=0;i < list.length;i++)
			tmp[i] = new File(list[i]);
		loadList(tmp);
	}
	private void loadList(File[] list)
	{
		Arrays.sort(list, this);
		this.list.clear();
		for (File f:list)
			this.list.add(f);
		list_view.getAdapter().notifyDataSetChanged();
	}
	private boolean loadDir(File file)
	{
		//file=file.getParentFile();

		if (file.isDirectory())
		{
			index_message.setText(file.getAbsolutePath());
			current = file;
			loadList(file.listFiles(this));
			return true;
		}
		return false;

	}

	@Override
	public boolean accept(File p1)
	{
		if (file)
			return !p1.getName().startsWith(".");
		else
			return p1.isDirectory() && !p1.getName().startsWith(".");
	}


	@Override
	public int compare(File p1, File p2)
	{
		if (p1.isDirectory() && p2.isFile())
			return -1;
		if (p1.isFile() && p2.isDirectory())
			return 1;
		return p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
	}

	@Override
	public void onBackPressed()
	{
		if (current != null)
		{
			for (File f:index)
			{
				if (f.equals(current))
				{
					loadList(index);
					current = null;
					index_message.setText("/");
					return;
				}
			}
			loadDir(current.getParentFile());
		}
		else
			super.onBackPressed();
	}

	@Override
	public void onItemClick(RecyclerView.Adapter adapter, RecyclerView.ViewHolder vh)
	{
		loadDir(list.get(vh.getAdapterPosition()));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				break;
			case R.id.home:
				current = null;
				index_message.setText("/");
				loadList(index);
				break;
			case R.id.new_folder:
				if(getIntent().getAction()==null&&current!=null){
					View v=LayoutInflater.from(this).inflate(R.layout.send_money,null);
					final TextView tv=(TextView)v;
					tv.setHint("名称");
					new AlertDialog.Builder(this).setMessage("新建文件夹").setView(v).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								if(tv.getText().toString().trim().length()>0){
									File f=new File(current,tv.getText().toString().trim());
									f.mkdir();
									loadDir(current);
								}
							}
						}).create().show();

		}else{
						Toast.makeText(getApplicationContext(),"当前模式不支持",Toast.LENGTH_SHORT).show();
					}
				break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.folder_home, menu);
		return true;
	}

	@Override
	public void onClick(View p1)
	{
		if (current == null)
			Toast.makeText(getApplicationContext(), "无效的目录", Toast.LENGTH_SHORT).show();
		else
		{
			setResult(RESULT_OK, new Intent().setData(Uri.parse(current.getAbsolutePath())));
			finish();
		}
	}

	


}
