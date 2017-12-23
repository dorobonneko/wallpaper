package com.moe.download;
import android.content.Context;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;
import com.moe.entity.DownloadItem;
import java.io.IOException;
import java.io.File;
import android.content.SharedPreferences;
import android.support.v4.provider.DocumentFile;
import android.net.Uri;
import com.moe.utils.DocumentFileUtils;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.moe.services.DownloadService;
import android.os.Environment;
import com.moe.utils.PreferenceUtils;
import javax.net.ssl.HttpsURLConnection;
import okhttp3.*;

public class Download extends Thread
{
	private DownloadItem pi;
	private SharedPreferences setting;
	private DownloadService service;
	private InputStream is=null;
	private OutputStream os=null;
	private Call call;
	public Download(DownloadItem pi,DownloadService context){
		this.pi=pi;
		setting=context.getSharedPreferences("setting",0);
		this.service=context;
		}

	public void setDownloadItem(DownloadItem di)
	{
		this.pi=di;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof DownloadItem)
			return pi.equals(obj);
		return super.equals(obj);
	}

	public DownloadItem getDownloadItem()
	{
		return pi;
	}
	public void close(){
		try
		{
			if (os != null)os.close();
		}
		catch (Exception e)
		{}
		try
		{
			if (is != null)is.close();
		}
		catch (Exception e)
		{}
	}
	@Override
	public void run()
	{
		pi.setState(DownloadService.State.LOADING);
		File file=new File(pi.getDir());
		pi.setCurrent(file.length());
		try{
			Request.Builder build=new Request.Builder();
			build.url(pi.getUrl());
			build.addHeader("Accept","*/*");
			build.addHeader("Accept", "*/*");
			build.addHeader("Connection", "Keep-Alive");
		//request.addHeader("Icy-MetaData", "1");
			build.addHeader("Accept-Encoding","gzip");
			build.addHeader("Referer",pi.getReferer()==null?"":pi.getReferer());
			build.addHeader("Cookie",PreferenceUtils.getCookieName(service)+"="+PreferenceUtils.getCookie(service));
			build.addHeader("Range", "bytes=" + pi.getCurrent()+ "-");
			call=service.getOkHttp().newCall(build.build());
			Response response=call.execute();
		is=response.body().byteStream();
		if("gzip".equalsIgnoreCase(response.header("Content-Encoding")))
			is=new GZIPInputStream(is);
			if(pi.getTotal()<1){
				long length =0;
				try{
				length=Long.parseLong(response.header("Content-Length"));
				}catch(Exception e){}
				if(length==0)
					length=response.body().contentLength();
				pi.setTotal(length);
				//更新数据长度
				pi.update();
			}
		
			switch(response.code()){
				case 200:
					//is.skip(fi.cu);
					pi.setCurrent(0);
					//os=new FileOutputStream(file);
				case 206:
					if(file.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())){
						File parent=file.getParentFile();
						if(!parent.exists())parent.mkdirs();
						os=new FileOutputStream(file,response.code()==206);
					}else if(setting.getString("sdcard",null)!=null){
						os=service.getContentResolver().openOutputStream(DocumentFileUtils.getDocumentFilePath(DocumentFile.fromTreeUri(service,Uri.parse(setting.getString("sdcard",null))),file).getUri(),response.code()==206?"wa":"w");
					}else
						throw new IOException();
					break;
				default:
				throw new IOException();
			}
			byte[] buffer=new byte[Integer.parseInt(setting.getString("buffer_size","4096"))];
			int len=-1;
			while((len=is.read(buffer))!=-1){
				os.write(buffer,0,len);
				pi.setCurrent(pi.getCurrent()+len);
				pi.update();
			}
			os.flush();
			pi.setState(DownloadService.State.SUCCESS);
			pi.update();
			service.onItemEnd(this,true);
			}catch(Exception e){
			pi.setState(DownloadService.State.ERROR);
			service.onItemEnd(this,false);
			}
		finally{
			try
			{
				if (os != null)os.close();
			}
			catch (IOException e)
			{}
			try
			{
				if (is != null)is.close();
			}
			catch (IOException e)
			{}
			if (call != null)call.cancel();
		}
	}
	
}
