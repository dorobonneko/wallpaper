package com.moe.app;
import android.app.Application;
import android.content.Intent;
import android.content.Context;
import com.moe.yaohuo.ExceptionActivity;
import com.tencent.bugly.crashreport.CrashReport;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.moe.download.*;
import android.content.res.Resources.Theme;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.content.res.TypedArray;
import com.moe.res.ThemeManager;


public class Application extends Application implements Thread.UncaughtExceptionHandler
{
	@Override
	public void uncaughtException(final Thread p1, Throwable p2)
	{
		
				
					StringBuffer sb=new StringBuffer();
					sb.append(p2.getMessage());
					for (StackTraceElement ste:p2.getStackTrace())
						sb.append("\n").append(ste.toString());
					startActivity(new Intent(this, ExceptionActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(Intent.EXTRA_TEXT, sb.toString()));
					if (p1.getName().equals("main"))
					android.os.Process.killProcess(android.os.Process.myPid());
				
		
	}


	@Override
	public void onCreate()
	{
		super.onCreate();
		ThemeManager.getInstance().init(this);
		//AVOSCloud.initialize(this,"qfengymRPe5vYAFDu74h2CtQ-gzGzoHsz","QenSliDeF7Sj21iOo0pM1IK7");
		DownloadDatabase.init(this);
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
		//BmobUpdateAgent.initAppVersion();
	}

}
