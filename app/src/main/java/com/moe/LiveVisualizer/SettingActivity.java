package com.moe.LiveVisualizer;
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

public class SettingActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		init();
	}
	private void init()
	{
		ComponentName service=new ComponentName(this,LiveWallpaper.class);
		if (Build.VERSION.SDK_INT<23||(checkSelfPermission("android.permission.RECORD_AUDIO") == PackageManager.PERMISSION_GRANTED &&checkSelfPermission("android.permission.READ_PHONE_STATE")==PackageManager.PERMISSION_GRANTED))
		{
			/*if ( Build.VERSION.SDK_INT > 18 && !notificationListenerEnable() )
			{
				gotoNotificationAccessSetting();
				return;
			}*/
			if(getPackageManager().getComponentEnabledSetting(service)!=PackageManager.COMPONENT_ENABLED_STATE_ENABLED){
				getPackageManager().setComponentEnabledSetting(service,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
			}
			WallpaperInfo info=WallpaperManager.getInstance(this).getWallpaperInfo();
			if ( info == null || !getPackageName().equals(((WallpaperManager)getSystemService(WALLPAPER_SERVICE)).getWallpaperInfo().getPackageName()) )
			{

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
				setContentView(R.layout.setting_view);
				Fragment setting=getFragmentManager().findFragmentByTag("setting");
				if ( setting == null )setting = new SettingFragment();
				if ( setting.isAdded() )
					getFragmentManager().beginTransaction().show(setting).commit();
				else
					getFragmentManager().beginTransaction().add(R.id.setting_view, setting, "setting").commit();
				getActionBar().setDisplayHomeAsUpEnabled(true);
				getActionBar().setHomeButtonEnabled(true);
			}
		}
		else
		{
			if(getPackageManager().getComponentEnabledSetting(service)!=PackageManager.COMPONENT_ENABLED_STATE_DISABLED){
				getPackageManager().setComponentEnabledSetting(service,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
			}
			requestPermissions(new String[]{"android.permission.RECORD_AUDIO","android.permission.READ_PHONE_STATE"}, 432);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		// TODO: Implement this method
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if ( requestCode == 432 )
		{
			boolean flag=true;
			for(int grant:grantResults)
			if(grant==PackageManager.PERMISSION_DENIED)
				flag=false;
			if(flag)
			init();
			else
			finish();
		}
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void gotoNotificationAccessSetting()
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
	}  
}
