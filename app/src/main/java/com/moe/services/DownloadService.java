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
import com.moe.database.DownloadDatabase;
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

public class DownloadService extends Service
{
	private  ArrayList<DownloadItem> list=new ArrayList<>(),loading=new ArrayList<>();
	private  ArrayList<Download> download=new ArrayList<>();
	private DownloadDatabase dd;
	private static SSLSocketFactory ssf;
	public static SSLSocketFactory getSSLSocketFactory()
	{
		if(ssf==null){
			try{
			SSLContext sc=SSLContext.getInstance("SSL", "SunJSSE");
			sc.init(null, new TrustManager[]{new TrustManager()}, new SecureRandom());
			ssf =sc .getSocketFactory();
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
				dd.insert(di);
				dd.updateState(di.getUrl(),State.WAITING);
				di.setState(State.WAITING);
				loading.add(di);
				list.add(di);
				sendBroadcast(new Intent("com.moe.refresh").putParcelableArrayListExtra("data",loading));
				handler.sendEmptyMessageDelayed(0,1000);
				break;
			case Action_Stop:
				int index=loading.indexOf(di);
				if(index!=-1){
					di=loading.get(index);
					di.setState(DownloadService.State.PAUSE);
					sendBroadcast(new Intent("com.moe.refresh").putParcelableArrayListExtra("data",loading));
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
					sendBroadcast(new Intent("com.moe.refresh").putParcelableArrayListExtra("data",loading));
					sendEmptyMessageDelayed(0,1000);
					break;
			}
		}
		
	};

	@Override
	public void onCreate()
	{
		super.onCreate();
		dd=DownloadDatabase.getInstance(this);
	}

	@Override
	public void onDestroy()
	{
		handler.removeMessages(0);
		super.onDestroy();
	}
	
	private void check(){
		if(list.size()==0)return;
		int size=download.size();
		for(;size<5;size++){
			try{
			Download d=new Download(list.remove(0),this);
			download.add(d);
			d.start();
			}catch(IndexOutOfBoundsException e){break;}
		}
		if(download.size()==0&&list.size()==0)
			stopSelf();
	}
	
	public void onItemEnd(Download down,boolean flag){
		sendBroadcast(new Intent("com.moe.refresh").putParcelableArrayListExtra("data",loading));
		download.remove(down);
		loading.remove(down);
		check();
		
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
