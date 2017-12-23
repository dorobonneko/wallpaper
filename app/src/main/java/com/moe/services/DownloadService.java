package com.moe.services;
import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.moe.entity.DownloadItem;
import android.os.Environment;
import com.moe.download.Download;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;
import java.security.KeyManagementException;
import java.security.cert.CertificateException;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.widget.RemoteViews;
import com.moe.yaohuo.R;
import android.app.Notification;
import java.io.File;
import android.media.RemoteControlClient;
import android.os.Build;
import android.app.PendingIntent;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import java.text.DecimalFormat;
import com.moe.yaohuo.MainActivity;
import android.content.ComponentName;
import android.content.ClipData;
import com.moe.internal.NotificationList;
import okhttp3.*;

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
	public static SSLSocketFactory getSSLSocketFactory()
	{
		if(ssf==null){
			try{
			SSLContext sc=SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[]{new TrustManager()}, new SecureRandom());
			ssf =sc.getSocketFactory();
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
	public OkHttpClient getOkHttp(){
		if(ohc==null){
			ohc=new OkHttpClient.Builder().sslSocketFactory(getSSLSocketFactory()).readTimeout(10,TimeUnit.SECONDS).connectTimeout(15,TimeUnit.SECONDS).build();
		}
		return ohc;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		onStart(intent,startId);
		return START_STICKY;
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		DownloadItem di=intent.getParcelableExtra("down");
		switch(intent.getAction()){
			case Action_Start:
				
				di.setState(State.WAITING);
				di.update();
				loading.add(di);
				list.add(di);
				sendBroadcast(new Intent(ACTION_REFRESH).putParcelableArrayListExtra("data",loading));
				handler.sendEmptyMessageDelayed(0,1000);
				break;
			case Action_Stop:
				int index=loading.indexOf(di);
				if(index!=-1){
					di=loading.get(index);
					di.setState(DownloadService.State.PAUSE);
					sendBroadcast(new Intent(ACTION_REFRESH).putParcelableArrayListExtra("data",loading));
				}
				if(download.contains(di)){
					index=download.indexOf(di);
					Download d=download.get(index);
					if(d!=null)d.close();
				}else
					list.remove(di);
				break;
		}
		check();
		
	}
	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					sendBroadcast(new Intent(ACTION_REFRESH).putParcelableArrayListExtra("data",loading));
					sendEmptyMessageDelayed(0,1000);
					break;
			}
		}
		
	};

	@Override
	public void onCreate()
	{
		super.onCreate();
		nm=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Intent intent=getPackageManager().getLaunchIntentForPackage(getPackageName());
		intent.setClipData(ClipData.newPlainText("",""));
		download_activity=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
		registerReceiver(refresh=new NotifcationRefresh(),new IntentFilter(ACTION_REFRESH));
	}

	@Override
	public void onDestroy()
	{
		handler.removeMessages(0);
		if(refresh!=null)unregisterReceiver(refresh);
		super.onDestroy();
	}
	
	private void check(){
		if(list.size()==0)return;
		for(int size=download.size();size<5;size++){
			try{
			Download d=new Download(list.remove(0),this);
			download.add(d);
			d.start();
			}catch(IndexOutOfBoundsException e){break;}
		}
		//if(download.size()==0&&list.size()==0)
		//	stopSelf();
	}
	
	public void onItemEnd(Download down,boolean flag){
		sendBroadcast(new Intent(ACTION_REFRESH).putParcelableArrayListExtra("data",loading));
		download.remove(down);
		loading.remove(down);
		check();
		
	}
	private class NotifcationRefresh extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context p1, Intent p2)
		{
			ArrayList<DownloadItem> list=p2.getParcelableArrayListExtra("data");
			for(DownloadItem di:list){
				switch(di.getState()){
					case State.SUCCESS:
						Notification.Builder nb=NotificationList.getInstance(getApplicationContext()).getNotification(di);
						NotificationList.getInstance(getApplicationContext()).remove(di);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						Uri contentUri=Uri.fromFile(new File(di.getDir()));
						if (Build.VERSION.SDK_INT >19)
						{
							intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						}
						else
						{
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						}
						int index=di.getDir().lastIndexOf(".");
						intent.setDataAndType(contentUri,index==-1?"*/*":MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(di.getDir().substring(index+1))));
						PendingIntent pi=PendingIntent.getActivity(p1, 233, intent, PendingIntent.FLAG_ONE_SHOT);
						nb.setContentIntent(pi);
						if(di.getTitle().matches(".*.apk"))
							p1.startActivity(intent);
						nm.notify(di.getUrl().hashCode(),Build.VERSION.SDK_INT>15?nb.build():nb.getNotification());
						break;
					default:
						Notification.Builder build=NotificationList.getInstance(getApplicationContext()).getNotification(di);
						build.setContentIntent(download_activity);
						nm.notify(di.getUrl().hashCode(),Build.VERSION.SDK_INT>15?build.build():build.getNotification());
						break;
				}
				if(download.size()==0&&list.size()==0)
					stopSelf();
			}
		}
	}
	public final static String Action_Start="start";
	public final static String Action_Stop="stop";
	public static class State{
		public final static int UNKNOW=0;
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
