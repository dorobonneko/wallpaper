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

public class Application extends android.app.Application implements Thread.UncaughtExceptionHandler
{

	@Override
	public void uncaughtException(Thread p1, Throwable p2)
	{
		StringBuffer sb=new StringBuffer(p2.getMessage());
		for(StackTraceElement element:p2.getStackTrace())
		sb.append("\n").append(element.toString());
		Intent intent=new Intent(this,CrashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Intent.EXTRA_TEXT,sb.toString());
		startActivity(intent);
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
