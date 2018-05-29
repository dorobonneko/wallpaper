package com.moe.LiveVisualizer.activity;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.app.WallpaperInfo;
import android.widget.Toast;
import android.net.Uri;
import android.view.MenuItem;
import android.content.ActivityNotFoundException;
import android.provider.Settings;
import android.content.pm.PermissionInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import com.moe.LiveVisualizer.R;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import com.moe.LiveVisualizer.fragment.SettingFragment;
import com.moe.LiveVisualizer.fragment.CircleSettingFragment;
import com.moe.LiveVisualizer.fragment.DuangSettingFragment;
import android.content.SharedPreferences;
import android.content.ContentValues;
import android.widget.FrameLayout;

public class SettingActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	private MenuItem centerCircle,duang;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (Intent.ACTION_MAIN.equals(getIntent().getAction()))
			init();
		else
		{
			Toast.makeText(this, "别点了，进不来的", Toast.LENGTH_SHORT).show();
			finish();}
	}
	private void init()
	{
		ComponentName service=new ComponentName(this, LiveWallpaper.class);
		if (Build.VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.RECORD_AUDIO") == PackageManager.PERMISSION_GRANTED)
		{
			/*if ( Build.VERSION.SDK_INT > 18 && !notificationListenerEnable() )
			 {
			 gotoNotificationAccessSetting();
			 return;
			 }*/
			if (getPackageManager().getComponentEnabledSetting(service) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
			{
				getPackageManager().setComponentEnabledSetting(service, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
			}
			WallpaperInfo info=WallpaperManager.getInstance(this).getWallpaperInfo();
			if (info == null || !getPackageName().equals(((WallpaperManager)getSystemService(WALLPAPER_SERVICE)).getWallpaperInfo().getPackageName()))
			{
				Toast.makeText(getApplicationContext(), "先激活动态壁纸才能继续使用", Toast.LENGTH_LONG).show();
				try
				{
					Intent intent =new Intent(
						WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
					intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
									new ComponentName(this, LiveWallpaper.class));
					startActivity(intent);
				}
				catch (Exception e1)
				{
					try
					{
						startActivity(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
					}
					catch (Exception e)
					{
						AlertDialog dialog= new AlertDialog.Builder(this).setMessage("无法打开动态壁纸设置界面，你仍然可以通过设置来启用").create();
						dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){

								@Override
								public void onDismiss(DialogInterface p1)
								{
									// TODO: Implement this method
									finish();
								}
							});
						dialog.show();
					}

				}
				finish();
			}
			else
			{
				FrameLayout frame=new FrameLayout(this);
				frame.setFitsSystemWindows(true);
				frame.setId(android.R.id.widget_frame);
				((FrameLayout)findViewById(android.R.id.content)).addView(frame);
				Fragment setting=getFragmentManager().findFragmentByTag("setting");
				if (setting == null)
				{setting = new SettingFragment();

					if (setting.isAdded())
						getFragmentManager().beginTransaction().show(setting).commit();
					else
						getFragmentManager().beginTransaction().add(android.R.id.widget_frame, setting, "setting").commit();
				}
				if(getActionBar()!=null){
				getActionBar().setDisplayHomeAsUpEnabled(true);
				getActionBar().setHomeButtonEnabled(true);
				}
				getSharedPreferences("moe",0).registerOnSharedPreferenceChangeListener(this);
			}
		}
		else
		{
			if (getPackageManager().getComponentEnabledSetting(service) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
			{
				getPackageManager().setComponentEnabledSetting(service, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
			}
			requestPermissions(new String[]{"android.permission.RECORD_AUDIO","android.permission.READ_PHONE_STATE"}, 432);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		// TODO: Implement this method
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 432)
		{
			boolean flag=true;
			for (int grant:grantResults)
			{
				if (grant == PackageManager.PERMISSION_DENIED)
					flag = false;
				break;
			}
			if (flag)
				init();
			else
			{
				Toast.makeText(getApplicationContext(), "未给权限，已退出", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		centerCircle = menu.add(0, 0, 0, "中心圆");
		centerCircle.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		duang=menu.add(1,1,1,"屏幕特效");
		duang.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		Fragment fragment=getFragmentManager().findFragmentByTag("setting");
		centerCircle.setVisible(fragment != null && fragment.isVisible());
		duang.setVisible(fragment!=null&&fragment.isVisible());
		return true;
	}





	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				return true;
			case 0:
				centerCircle.setVisible(false);
				duang.setVisible(false);
				Fragment circle_setting=getFragmentManager().findFragmentByTag("circle_setting");
				if (circle_setting == null)circle_setting = new CircleSettingFragment();
				if (circle_setting.isAdded())
					getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentByTag("setting")).show(circle_setting).commit();
				else
					getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentByTag("setting")).add(android.R.id.widget_frame, circle_setting, "circle_setting").commit();

				return true;
			case 1:
				centerCircle.setVisible(false);
				duang.setVisible(false);
				Fragment duang_setting=getFragmentManager().findFragmentByTag("daung_setting");
				if (duang_setting == null)duang_setting = new DuangSettingFragment();
				if (duang_setting.isAdded())
					getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentByTag("setting")).show(duang_setting).commit();
				else
					getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentByTag("setting")).add(android.R.id.widget_frame, duang_setting, "duang_setting").commit();
				
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed()
	{
		Fragment circle_setting=getFragmentManager().findFragmentByTag("circle_setting");
		Fragment duang_setting=getFragmentManager().findFragmentByTag("duang_setting");
		if (circle_setting != null && !circle_setting.isHidden())
		{
			getFragmentManager().beginTransaction().hide(circle_setting).show(getFragmentManager().findFragmentByTag("setting")).commit();
			if (centerCircle != null)centerCircle.setVisible(true);
			if(duang!=null)duang.setVisible(true);
		}
		else if(duang_setting!=null&&!duang_setting.isHidden()){
			getFragmentManager().beginTransaction().hide(duang_setting).show(getFragmentManager().findFragmentByTag("setting")).commit();
			if (centerCircle != null)centerCircle.setVisible(true);
			if(duang!=null)duang.setVisible(true);
		}else
			super.onBackPressed();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences p1, String p2)
	{
		Uri.Builder build=new Uri.Builder();
		build.scheme("content").authority("moe").path("moe").appendQueryParameter("key",p2).appendQueryParameter("value",p1.getAll().get(p2)+"");
		getContentResolver().update(build.build(),new ContentValues(),null,null);
	}

	@Override
	public void finish()
	{
		// TODO: Implement this method
		super.finish();
		getSharedPreferences("moe",0).unregisterOnSharedPreferenceChangeListener(this);
	}


	/*private void gotoNotificationAccessSetting()
	 {  
	 try
	 {  
	 final Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
	 intent.putExtra("package", getPackageName());
	 intent.putExtra("uid", getApplicationInfo().uid);

	 startActivity(intent); 
	 }
	 catch (ActivityNotFoundException e)
	 {  try
	 {  
	 final Intent intent = new Intent();  
	 final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");  
	 intent.setComponent(cn);  
	 intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");  
	 startActivity(intent);   
	 }
	 catch (Exception ex)
	 {  
	 return;
	 }  
	 } 
	 finish();
	 }  
	 private boolean notificationListenerEnable()
	 {  
	 boolean enable = false;  
	 String flat= Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");  
	 if ( flat != null )
	 {  
	 enable = flat.contains(NotifycationListener.class.getName());  
	 }  
	 return enable;  
	 }  */
	 
}
