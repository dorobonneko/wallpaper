package com.moe.download;


import java.io.*;
import okhttp3.*;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;
import com.moe.entity.DownloadItem;
import com.moe.services.DownloadService;
import com.moe.utils.DocumentFileUtils;
import com.moe.utils.PreferenceUtils;
import java.util.zip.GZIPInputStream;

public class Download extends Thread
{
	private int errorSize=0;
	private DownloadItem dItem;
	private SharedPreferences setting;
	private DownloadService service;
	private InputStream is=null;
	private OutputStream os=null;
	private Call call;
	public Download(DownloadItem pi, DownloadService context)
	{
		this.dItem = pi;
		setting = context.getSharedPreferences("setting",0);
		this.service = context;
	}


	public int getErrorSize()
	{
		return errorSize;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof DownloadItem)
			return dItem.equals(obj);
		return super.equals(obj);
	}

	public DownloadItem getDownloadItem()
	{
		return dItem;
	}
	public void close()
	{
		try
		{
			if (os!=null)os.close();
		}
		catch (Exception e)
		{}
		try
		{
			if (is!=null)is.close();
		}
		catch (Exception e)
		{}
		if (call!=null)call.cancel();
	}
	@Override
	public void run()
	{
		dItem.setState(DownloadService.State.LOADING);
		File file=new File(dItem.getDir(),dItem.getTitle());
		dItem.setCurrent(file.length());
		try
		{
			Request.Builder build=new Request.Builder();
			build.url(dItem.getUrl());
			build.addHeader("Accept","*/*");
			build.addHeader("Connection","Keep-Alive");
			//request.addHeader("Icy-MetaData", "1");
			build.addHeader("Accept-Encoding","gzip");
			build.addHeader("Referer",dItem.getReferer()==null?"":dItem.getReferer());
			build.addHeader("Cookie",dItem.getCookie()==null?"":dItem.getCookie());
			build.addHeader("Range","bytes="+dItem.getCurrent()+"-");
			call = service.getOkHttp().newCall(build.build());
			Response response=call.execute();
			is = response.body().byteStream();
			if ("gzip".equalsIgnoreCase(response.header("Content-Encoding")))
				is = new GZIPInputStream(is);
			if (dItem.getTotal()<1)
			{
				long length =0;
				try
				{
					length = Long.parseLong(response.header("Content-Length"));
				}
				catch (Exception e)
				{}
				if (length==0)
					length = response.body().contentLength();
				dItem.setTotal(length);
				//更新数据长度
				dItem.update();
			}

			switch (response.code())
			{
				case 200:
					//is.skip(fi.cu);
					dItem.setCurrent(0);
					//os=new FileOutputStream(file);
				case 206:
					if (file.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().getAbsolutePath()))
					{
						File parent=file.getParentFile();
						if (!parent.exists())parent.mkdirs();
						os = new FileOutputStream(file,response.code()==206);
					}
					else if (setting.getString("sdcard",null)!=null)
					{
						os = service.getContentResolver().openOutputStream(DocumentFileUtils.getDocumentFilePath(DocumentFile.fromTreeUri(service,Uri.parse(setting.getString("sdcard",null))),file).getUri(),response.code()==206?"wa":"w");
					}
					else
						throw new IOException();
					byte[] buffer=new byte[Integer.parseInt(setting.getString("buffer_size","4096"))];
					int len=-1;
					while ((len=is.read(buffer))!=-1)
					{
						os.write(buffer,0,len);
						dItem.setCurrent(dItem.getCurrent()+len);
						dItem.update();
					}
					os.flush();
					break;
				case 416:
					//dItem.setState(DownloadService.State.SUCCESS);
					//service.onItemEnd(this,true);
					break;
				default:
					throw new IOException();
			}
			dItem.setState(DownloadService.State.SUCCESS);
			service.onItemEnd(this,true);
		}
		catch (Exception e)
		{
			errorSize++;
			dItem.setState(DownloadService.State.ERROR);
			service.onItemEnd(this,false);
		}
		finally
		{
			close();
		}
	}

}
