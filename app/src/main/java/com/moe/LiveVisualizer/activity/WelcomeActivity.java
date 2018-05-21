package com.moe.LiveVisualizer.activity;
import android.app.Activity;
import android.os.Bundle;
import android.content.ComponentName;
import android.os.Build;
import android.app.WallpaperInfo;
import android.widget.Toast;
import android.content.Intent;
import android.app.WallpaperManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import com.moe.LiveVisualizer.service.LiveWallpaper;

public class WelcomeActivity extends Activity
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
				Toast.makeText(getApplicationContext(),"先激活动态壁纸才能继续使用",Toast.LENGTH_LONG).show();
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
				ComponentName setting=new ComponentName(this,SettingActivity.class);
				if(getPackageManager().getComponentEnabledSetting(setting)!=PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
				getPackageManager().setComponentEnabledSetting(setting,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
				}
				startActivity(new Intent(this,SettingActivity.class));
				finish();
				getPackageManager().setComponentEnabledSetting(new ComponentName(this,WelcomeActivity.class),PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
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
			for(int grant:grantResults){
				if(grant==PackageManager.PERMISSION_DENIED)
					flag=false;
				break;
			}
			if(flag)
				init();
			else{
				Toast.makeText(getApplicationContext(),"未给权限，已退出",Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

	
}
