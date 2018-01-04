package com.moe.thread;
import android.os.Handler;
import android.os.Message;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.IOException;
import android.content.Context;
import com.moe.utils.PreferenceUtils;
import java.io.ByteArrayOutputStream;
import org.json.JSONObject;
import com.moe.utils.StringUtils;

public class Upload extends Thread
{
	public InputStream input,in;
	private HttpURLConnection huc;
	private OutputStream out;
	private long length;
	private double len;
	private String pid;
	private int state;
	public Upload(InputStream in,long length){
		input=in;
		this.length=length;
	}
	public int getStates(){
		return state;
	}
	public String getPid(){
		return pid;
	}
	public int getProgress()
	{
		return (int)(len/length*100);
	}
	public void close(){
		try
		{
			if (input != null)input.close();
		}
		catch (IOException e)
		{}
		try
		{
			if (in != null)in.close();
		}
		catch (IOException e)
		{}
		try
		{
			if (out != null)out.close();
		}
		catch (IOException e)
		{}
		if (huc != null)huc.disconnect();
	}
	@Override
	public void run()
	{
		try
		{
			huc = (HttpURLConnection) new URL("http://x.mouto.org/wb/x.php?up").openConnection();
			huc.setRequestProperty("Content-Length",length+"");
			huc.setRequestProperty("Content-Type","image/*");
			huc.setDoInput(true);
			huc.setDoOutput(true);
			huc.connect();
			out=huc.getOutputStream();
			byte[] buffer=new byte[512];
			int buffer_size=-1;
			while((buffer_size=input.read(buffer))!=-1)
			{
				out.write(buffer,0,buffer_size);
				len+=buffer_size;
			}
			out.flush();
			in=huc.getInputStream();
			
			pid=new JSONObject(StringUtils.getString(in)).getString("pid");
			state=1;
		}
		catch (Exception e)
		{
			state=2;
		}finally{
			close();
		}
		
	}
	
}
