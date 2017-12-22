package com.moe.app;
import android.app.Application;
import android.content.Intent;
import android.content.Context;
import com.moe.yaohuo.ExceptionActivity;
import com.tencent.bugly.crashreport.CrashReport;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Application extends Application implements Thread.UncaughtExceptionHandler
{
	private Context context;
	@Override
	public void uncaughtException(final Thread p1, Throwable p2)
	{
		
				
					StringBuffer sb=new StringBuffer();
					sb.append(p2.getMessage());
					for (StackTraceElement ste:p2.getStackTrace())
						sb.append("\n").append(ste.toString());
					startActivity(new Intent(context, ExceptionActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(Intent.EXTRA_TEXT, sb.toString()));
					if (p1.getName().equals("main"))
					android.os.Process.killProcess(android.os.Process.myPid());
				
		
	}


	@Override
	public void onCreate()
	{
		super.onCreate();
		context=this;
		CrashReport.UserStrategy cu=new CrashReport.UserStrategy(this);
		cu.setAppChannel("moe");
		cu.setAppPackageName(getPackageName());
		try
		{
			cu.setAppVersion(getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName);
		}
		catch (PackageManager.NameNotFoundException e)
		{}
		CrashReport.initCrashReport(this, "04dc5a19f4", true, cu);
		//Thread.currentThread().setDefaultUncaughtExceptionHandler(this);
		BmobUpdateAgent.initAppVersion();
	}

}
