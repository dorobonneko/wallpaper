package com.moe.internal;
import android.app.Notification;
import com.moe.entity.DownloadItem;
import java.util.HashMap;
import android.content.Context;
import com.moe.yaohuo.R;
import com.moe.download.Download;
import com.moe.services.DownloadService;
import java.io.File;
import java.text.*;
import com.moe.utils.NumberUtils;
public class NotificationList
{
	private Context context;
	private HashMap<String,Notification.Builder> list;
	private static NotificationList nl;
	private NotificationList(Context context){
		list=new HashMap<>();
		this.context=context;
	}
	public static NotificationList getInstance(Context context){
		if(nl==null)nl=new NotificationList(context);
		return nl;
	}
	public Notification.Builder getNotification(DownloadItem di){
		Notification.Builder builder=list.get(di.getUrl());
		if(builder==null)
			builder=new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.yaohuo);
			builder.setTicker(di.getTitle()+(di.getState()==DownloadService.State.SUCCESS?"下载成功":"正在下载"));
			builder.setOngoing(di.isLoading());
			builder.setProgress(100,(int)(((double)di.getCurrent())/di.getTotal()*100),di.getState()==DownloadService.State.SUCCESS);
			builder.setContentTitle(di.getTitle());
			builder.setSubText(di.getState()==DownloadService.State.SUCCESS?"下载完成":(NumberUtils.getSize(di.getCurrent())+"/"+NumberUtils.getSize(di.getTotal())));
			return builder;
	}
	public void remove(DownloadItem di){
		list.remove(di.getUrl());
	}
}
