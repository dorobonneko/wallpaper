package com.moe.LiveVisualizer.app;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import android.content.res.Resources;
import java.lang.reflect.InvocationHandler;
import com.moe.LiveVisualizer.R;

public class Application extends android.app.Application implements Thread.UncaughtExceptionHandler
{

	@Override
	public void uncaughtException(Thread p1, Throwable p2)
	{
		FileOutputStream fos=null;
		try
		{
			fos = new FileOutputStream(getExternalCacheDir().getAbsolutePath() + "/log",true);
			fos.write((p2.getMessage()+"\n").getBytes());
			for(StackTraceElement element:p2.getStackTrace()){
				fos.write((element.toString()+"\n").getBytes());
			}
			fos.write("\n\n".getBytes());
			fos.flush();
		}
		catch (Exception e)
		{}finally{
			try
			{
				if ( fos != null )fos.close();
			}
			catch (IOException e)
			{}
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}


	@Override
	public void onCreate()
	{
		super.onCreate();
		//Bugly.init(this,"39c93f2bb3",false);
		Thread.currentThread().setUncaughtExceptionHandler(this);
	}
}
