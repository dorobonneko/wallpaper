package com.moe.download;
import java.io.InputStream;
import org.jsoup.Jsoup;
import android.content.Context;
import android.net.Uri;
import java.io.FileNotFoundException;
import com.moe.utils.PreferenceUtils;
import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.jsoup.nodes.Document;

public class LogoUpload extends Thread
{
	private InputStream input;
	private Context context;
	public LogoUpload(Context context,Bitmap bit,Callback call){
		this.context=context;
		this.call=call;
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		bit.compress(Bitmap.CompressFormat.JPEG,80,baos);
		input=new ByteArrayInputStream(baos.toByteArray());
		try
		{
			baos.close();
		}
		catch (IOException e)
		{}

	}
	public LogoUpload(Context context,Uri input,Callback call){
		this.context=context;
		this.call=call;
		try
		{
			this.input = context.getContentResolver().openInputStream(input);
		}
		catch (FileNotFoundException e)
		{}
	}
	@Override
	public void run()
	{
		try
		{
			Jsoup.connect(PreferenceUtils.getHost(context) + "/album/admin_WAPadd.aspx")
			.timeout(10000)
				.data("book_title", "headlogo")
				.data("toclassid", "236")
				.data("ishidden", "1")
				.data("book_file", "file.gif", input)
				.data("action", "gomod")
				.data("classid", "0")
				.data("siteid", "1000")
				.data("num", "1")
				.data("smalltypeid", "0")
				.userAgent(PreferenceUtils.getUserAgent())
				.cookie(PreferenceUtils.getCookieName(context),PreferenceUtils.getCookie(context)).post();
			Document doc=Jsoup.connect(PreferenceUtils.getHost(context)+"/album/albumlist.aspx")
			.data("siteid","1000")
			.data("classid","0")
			.data("smalltypeid","0")
			.data("touserid",PreferenceUtils.getUid(context)+"")
			.userAgent(PreferenceUtils.getUserAgent())
			.cookie(PreferenceUtils.getCookieName(context),PreferenceUtils.getCookie(context))
			.get();
			String url=doc.getElementsByAttributeValue("alt","load...").get(0).attr("src").substring(1).replace("S","");
			Jsoup.connect(PreferenceUtils.getHost(context)+"/bbs/ModifyHead.aspx")
				.data("toheadimg",url)
				.data("action","gomod")
				.data("siteid","1000")
				.data("classid","0")
				.data("needpassword",context.getSharedPreferences("moe",0).getString("pwd",""))
				.userAgent(PreferenceUtils.getUserAgent())
				.cookie(PreferenceUtils.getCookieName(context),PreferenceUtils.getCookie(context)).post();
				if(call!=null)call.callback(true);
		}
		catch (IOException e)
		{
			if(call!=null)call.callback(false);
		}finally{
			try
			{
				if (input != null)input.close();
			}
			catch (IOException e)
			{}
			call=null;
		}
	}
	public abstract interface Callback{
		void callback(boolean call);
	}
	private Callback call;

}
