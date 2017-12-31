package com.moe.fragment.preference;
import android.os.Bundle;
import com.moe.yaohuo.R;
import android.support.v7.app.AlertDialog;
import android.content.pm.PackageManager;
import android.support.v7.preference.Preference;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import java.util.List;
import com.avos.avoscloud.AVException;
import android.widget.Toast;
import com.avos.avoscloud.GetCallback;
import android.content.DialogInterface;
import com.moe.entity.DownloadItem;
import android.os.Environment;
import android.content.Intent;
import com.moe.services.DownloadService;

public class AboutPreference extends PreferenceFragment implements Preference.OnPreferenceClickListener
{
	private AlertDialog.Builder dialog;

	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.about);
		try
		{
			findPreference("setting_update").setSummary("v" + getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName);
		}
		catch (PackageManager.NameNotFoundException e)
		{}
		findPreference("setting_update").setOnPreferenceClickListener(this);
		findPreference("setting_faq").setOnPreferenceClickListener(this);
		findPreference("setting_dev").setOnPreferenceClickListener(this);
		findPreference("setting_about").setOnPreferenceClickListener(this);
		
	}
	@Override
	public boolean onPreferenceClick(Preference p1)
	{
		AVQuery<AVObject> aq=new AVQuery<AVObject>("message");
		switch(p1.getKey()){
			case "setting_update":
				aq.setClassName("update");
				aq.orderByDescending("version_int");
				try
				{
					aq.whereGreaterThan("version_int",getContext().getPackageManager().getPackageInfo(getContext().getPackageName(),PackageManager.GET_CONFIGURATIONS).versionCode);
				}
				catch (PackageManager.NameNotFoundException e)
				{}
				aq.findInBackground(new FindCallback<AVObject>(){

						@Override
						public void done(List<AVObject> p1, AVException p2)
						{
							if(p2==null){
								if(p1.size()>0){
									final AVObject aob=p1.get(0);
									AVQuery<AVObject> aq=new AVQuery<>("message");
									aq.whereEqualTo("name",aob.get("version"));
									aq.findInBackground(new FindCallback<AVObject>(){

											@Override
											public void done(List<AVObject> p1, AVException p2)
											{
												if(p1.size()>0)
													new AlertDialog.Builder(getActivity()).setTitle("v"+aob.get("version").toString()).setMessage(p1.get(0).get("msg").toString()).setPositiveButton("取消",null).setNegativeButton("下载",new DialogInterface.OnClickListener(){

															@Override
															public void onClick(DialogInterface p1, int p2)
															{
																DownloadItem di=new DownloadItem();
																di.setUrl(aob.get("url").toString());
																di.setTitle("妖火"+aob.get("version").toString());
																di.setReferer(null);
																di.setTotal(9);
																di.setDir(getContext().getSharedPreferences("setting",0).getString("path",Environment.getExternalStorageDirectory().getAbsolutePath()+"/yaohuo"));
																di.setType(null);
																di.setTime(System.currentTimeMillis());
																//di.setCookie(CookieManager.getInstance().getCookie(url));
																di.save();
																getContext().startService(new Intent(getContext(),DownloadService.class).putExtra("down",di));
																
															}
														}).show();
														else
													Toast.makeText(getActivity(),"获取新版本出错",Toast.LENGTH_SHORT).show();
												
												
											}
										});
									}else
									Toast.makeText(getActivity(),"当前是最新版本",Toast.LENGTH_SHORT).show();
							}else
								show("错误",p2.getMessage());
						}
					});
				break;
			case "setting_faq":
				try
				{
					aq.whereEqualTo("name",getContext().getPackageManager().getPackageInfo(getContext().getPackageName(),PackageManager.GET_CONFIGURATIONS).versionName);
				}
				catch (PackageManager.NameNotFoundException e)
				{}
				aq.findInBackground(new FindCallback<AVObject>(){

						@Override
						public void done(List<AVObject> p1, AVException p2)
						{
							if(p2==null){
							if(p1.size()>0)
								show(p1.get(0).get("name")+"更新日志",p1.get(0).get("msg").toString());
								}else
								show("错误",p2.getMessage());
						}
					});
				break;
			case "setting_dev":
				aq.getInBackground("5a41d10d67f356006139da40",new GetCallback<AVObject>(){

						@Override
						public void done(AVObject p1, AVException p2)
						{
							if(p2==null)
								show("计划开发",p1.get("msg").toString());
							else
								show("错误",p2.getMessage());
						}
					});

				break;
			case "setting_about":
				aq.getInBackground("5a41d0dc570c35003218239e",new GetCallback<AVObject>(){

						@Override
						public void done(AVObject p1, AVException p2)
						{
							if(p2==null)
								show("关于",p1.get("msg").toString());
							else
								show("错误",p2.getMessage());
						}
					});
				break;
		}
		return false;
	}

	private void show(String title,CharSequence message){
		try{
		if(dialog==null)dialog=new AlertDialog.Builder(getActivity());
		dialog.setTitle(title).setMessage(message).show();
		}catch(Exception e){}
	}
	
}
