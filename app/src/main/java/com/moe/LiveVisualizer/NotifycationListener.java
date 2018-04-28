package com.moe.LiveVisualizer;
import android.content.*;
import android.view.*;
import java.io.*;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.graphics.Palette;
import java.util.List;
import android.service.notification.NotificationListenerService;
import android.media.RemoteController;
import android.service.notification.StatusBarNotification;

public class NotifycationListener extends NotificationListenerService implements RemoteController.OnClientUpdateListener,Palette.PaletteAsyncListener
{
	private SharedPreferences moe;
private AudioManager am;
private RemoteController controller;
	@Override
	public void onCreate()
	{
		moe=getSharedPreferences("moe",0);
		super.onCreate();
		
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn)
	{
		// TODO: Implement this method
		super.onNotificationPosted(sbn);
		try{
			if(controller==null&&Build.VERSION.SDK_INT>18&&Build.VERSION.SDK_INT<21&&(moe.getString("color_mode","0").equals("3")||moe.getBoolean("artwork",false))){
				controller=new RemoteController(this,this);
				am=((AudioManager)getSystemService(AUDIO_SERVICE));
				if(am.registerRemoteController(controller)){
					Display display=((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
					controller.setArtworkConfiguration(display.getWidth(),display.getHeight());
					controller.setSynchronizationMode(controller.POSITION_SYNCHRONIZATION_CHECK);

				}


			}
		}catch(Exception e){}
	}

	@Override
	public void onDestroy()
	{
		if(am!=null&&controller!=null)
		am.unregisterRemoteController(controller);
		super.onDestroy();
	}
	
	@Override
	public void onGenerated(Palette p1)
	{
		List<Palette.Swatch> list=p1.getSwatches();
		if(list.size()>0)
		sendBroadcast(new Intent("artwork_color").putExtra("color",list.get(list.size()/2).getRgb()));
	}


	@Override
	public void onClientChange(boolean p1)
	{
		
	}

	@Override
	public void onClientPlaybackStateUpdate(int p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onClientPlaybackStateUpdate(int p1, long p2, long p3, float p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void onClientTransportControlUpdate(int p1)
	{
		// TODO: Implement this method
	}
private Bitmap bit;
	@Override
	public void onClientMetadataUpdate(final RemoteController.MetadataEditor p1)
	{
		if(bit==p1.getBitmap(p1.BITMAP_KEY_ARTWORK,null))return;
		bit=p1.getBitmap(p1.BITMAP_KEY_ARTWORK,null);
		if(moe.getBoolean("artwork",false)&&p1.getBitmap(p1.BITMAP_KEY_ARTWORK,null)!=null){
			new Thread(){
				public void run(){
			FileOutputStream fos = null;
			try
			{
				fos=new FileOutputStream(new File(getExternalCacheDir(), "artwork"));
				p1.getBitmap(p1.BITMAP_KEY_ARTWORK, null).compress(Bitmap.CompressFormat.JPEG, 100, fos);
				sendBroadcast(new Intent("artwork"));
				
			}
			catch (FileNotFoundException e)
			{}finally{
				try
				{
					if ( fos != null )fos.close();
				}
				catch (IOException e)
				{}
			}
			}
			}.start();
		}
			try{Palette.generateAsync(p1.getBitmap(p1.BITMAP_KEY_ARTWORK,null),this);}catch(Exception e){}
	}
} 
