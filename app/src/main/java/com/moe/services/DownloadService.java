package com.moe.services;
import android.app.*;
import android.content.*;
import android.os.*;
import javax.net.ssl.*;

import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.moe.download.Download;
import com.moe.entity.DownloadItem;
import com.moe.internal.NotificationList;
import java.io.File;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import java.util.TimerTask;
import java.util.Timer;

public class DownloadService extends Service
{
	public final static String ACTION_REFRESH="com.moe.yaohuo.DOWNLOAD_REFRESH";
	private  ArrayList<DownloadItem> list=new ArrayList<>(),loading=new ArrayList<>();
	private  ArrayList<Download> download=new ArrayList<>();
	private NotificationManager nm;
	private static SSLSocketFactory ssf;
	private NotifcationRefresh refresh;
	//private DecimalFormat format=new DecimalFormat("0.00");
	private PendingIntent download_activity;
	private OkHttpClient ohc;
	private SharedPreferences setting;
	private Timer timer;
	public static SSLSocketFactory getSSLSocketFactory()
	{
		if (ssf==null)
		{
			try
			{
				SSLContext sc=SSLContext.getInstance("TLS");
				sc.init(null,new TrustManager[]{new TrustManager()},new SecureRandom());
				ssf = sc.getSocketFactory();
			}
			catch (Exception e)
			{}
		}
		return ssf;
	}
	@Override
	public IBinder onBind(Intent p1)
	{
		return null;
	}
	public OkHttpClient getOkHttp()
	{
		if (ohc==null)
		{
			ohc = new OkHttpClient.Builder().sslSocketFactory(getSSLSocketFactory()).readTimeout(10,TimeUnit.SECONDS).connectTimeout(15,TimeUnit.SECONDS).build();
		}
		return ohc;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		onStart(intent,startId);
		return START_NOT_STICKY;
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		if (intent==null)return;
		DownloadItem di=intent.getParcelableExtra("down");
		if (di.isLoading())
		{
			di.setState(State.PAUSE);
			di.update();
			int index=download.indexOf(di);
			if (index!=-1)
				download.remove(index).close();
			loading.remove(di);
			list.remove(di);
			sendBroadcast(new Intent(ACTION_REFRESH).putExtra("data",di));
		}
		else
		{
			di.setState(State.WAITING);
			di.update();
			loading.add(di);
			list.add(di);
			sendBroadcast(new Intent(ACTION_REFRESH).putExtra("data",di));
		}
		check();
	}

	private class Alarm extends TimerTask
	{

		@Override
		public void run()
		{
			sendBroadcast(new Intent(ACTION_REFRESH).putParcelableArrayListExtra("data",loading));
		}
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Intent intent=getPackageManager().getLaunchIntentForPackage(getPackageName());
		intent.setClipData(ClipData.newPlainText("",""));
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		download_activity = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
		registerReceiver(refresh=new NotifcationRefresh(),new IntentFilter(ACTION_REFRESH));
		setting = getSharedPreferences("setting",0);
		//handler.sendEmptyMessageDelayed(0,1000);
		timer=new Timer();
		timer.schedule(new Alarm(),0,1000);
	}

	@Override
	public void onDestroy()
	{
		timer.cancel();
		if (refresh!=null)unregisterReceiver(refresh);
		super.onDestroy();
	}

	private void check()
	{
		if (list.size()==0)return;
		for (int size=download.size();size<5;size++)
		{
			try
			{
				Download d=new Download(list.remove(0),this);
				download.add(d);
				d.start();
			}
			catch (IndexOutOfBoundsException e)
			{break;}
		}
		//if(download.size()==0&&list.size()==0)
		//	stopSelf();
	}

	public void onItemEnd(Download down, boolean flag)
	{
		sendBroadcast(new Intent(ACTION_REFRESH).putExtra("data",down.getDownloadItem()));
		down.getDownloadItem().update();
		if (!flag&&down.getErrorSize()<Integer.parseInt(setting.getString("error_size","0")))
			down.start();
		else
		{
			download.remove(down);
			loading.remove(down);
			check();
		}


	}
	private class NotifcationRefresh extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context p1, Intent p2)
		{
			ArrayList<DownloadItem> list=p2.getParcelableArrayListExtra("data");
			if (list==null)
				refresh((DownloadItem)p2.getParcelableExtra("data"));
			else
				for (DownloadItem di:list)
					refresh(di);

		}
		private void refresh(DownloadItem di)
		{
			if (di!=null)
				switch (di.getState())
				{
					case State.SUCCESS:
						Notification.Builder nb=NotificationList.getInstance(getApplicationContext()).getNotification(di);
						NotificationList.getInstance(getApplicationContext()).remove(di);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						Uri contentUri=Uri.fromFile(new File(di.getDir(),di.getTitle()));
						if (Build.VERSION.SDK_INT>19)
						{
							intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						}
						else
						{
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						}
						int index=di.getDir().lastIndexOf(".");
						intent.setDataAndType(contentUri,index==-1?"*/*":di.getType()==null?MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(di.getDir().substring(index+1))):di.getType());
						PendingIntent pi=PendingIntent.getActivity(getApplicationContext(),233,intent,PendingIntent.FLAG_UPDATE_CURRENT);
						nb.setContentIntent(pi);
						nb.setAutoCancel(true);
						if (di.getTitle().matches(".*.apk"))
							startActivity(intent);
						nm.notify(di.getUrl().hashCode(),Build.VERSION.SDK_INT>15?nb.build():nb.getNotification());
						break;
					default:
						Notification.Builder build=NotificationList.getInstance(getApplicationContext()).getNotification(di);
						build.setContentIntent(download_activity);
						nm.notify(di.getUrl().hashCode(),Build.VERSION.SDK_INT>15?build.build():build.getNotification());
						break;
				}
		}
	}
	public static class State
	{
		public final static int WAITING=1;
		public final static int LOADING=2;
		public final static int SUCCESS=3;
		public final static int ERROR=4;
		public final static int PAUSE=5;
	}
	private static class TrustManager implements X509TrustManager
	{

		@Override
		public void checkClientTrusted(X509Certificate[] p1, String p2) throws CertificateException
		{
			// TODO: Implement this method
		}

		@Override
		public void checkServerTrusted(X509Certificate[] p1, String p2) throws CertificateException
		{
			// TODO: Implement this method
		}

		@Override
		public X509Certificate[] getAcceptedIssuers()
		{
			// TODO: Implement this method
			return new X509Certificate[0];
		}


	}
}
