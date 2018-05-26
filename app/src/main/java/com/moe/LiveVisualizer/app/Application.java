package com.moe.LiveVisualizer.app;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import android.content.res.Resources;
import java.lang.reflect.InvocationHandler;
import com.moe.LiveVisualizer.R;
import android.content.Intent;
import com.moe.LiveVisualizer.activity.CrashActivity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager;
import android.os.Build;

public class Application extends android.app.Application implements Thread.UncaughtExceptionHandler
{

	@Override
	public void uncaughtException(Thread p1, Throwable p2)
	{
		StringBuffer sb=new StringBuffer(p2.getMessage());
		try
		{
			sb.append("\n").append(getPackageManager().getPackageInfo(getPackageName(), 0).versionName).append("\n").append(Build.MODEL).append(" ").append(Build.VERSION.RELEASE).append("\n");
		}
		catch (PackageManager.NameNotFoundException e)
		{}
		for (StackTraceElement element:p2.getStackTrace())
		sb.append("\n").append(element.toString());
		Intent intent=new Intent(this,CrashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Intent.EXTRA_TEXT,sb.toString());
		startActivity(intent);
		if(p1.getName().equals("main"))
		Runtime.getRuntime().exit(1);
		//android.os.Process.killProcess(android.os.Process.myPid());
	}


	@Override
	public void onCreate()
	{
		super.onCreate();
		//Bugly.init(this,"39c93f2bb3",false);
		Thread.currentThread().setUncaughtExceptionHandler(this);
	}
}
