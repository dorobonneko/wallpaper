package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.view.MenuItem;
import android.webkit.CookieManager;
import com.moe.utils.PreferenceUtils;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.moe.widget.ProgressBar;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;
import android.webkit.DownloadListener;
import android.support.v7.app.AlertDialog;
import android.webkit.URLUtil;
import java.net.URLDecoder;
import android.webkit.WebSettings;
import android.view.Menu;
import android.net.Uri;
import android.content.Intent;
import android.content.DialogInterface;
import com.moe.services.DownloadService;
import com.moe.entity.DownloadItem;
import android.os.Environment;
import android.text.ClipboardManager;
import android.widget.Toast;
import android.annotation.SuppressLint;

public class WebViewActivity extends EventActivity implements DownloadListener
{
	private WebView wv;
	private String url;
	private ProgressBar pb;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.webview,(ViewGroup)findViewById(R.id.main_index),true);
		wv=(WebView)findViewById(R.id.webview);
		pb=(ProgressBar)findViewById(R.id.progressbar);
		pb.setMax(100);
		if(savedInstanceState==null)
		url=getIntent().getDataString();
		else
		url=savedInstanceState.getString("url");
		//CookieManager.getInstance().setCookie(PreferenceUtils.getHost(this),"sidyaohuo="+PreferenceUtils.getCookie(this));
		CookieManager.getInstance().setCookie(url,PreferenceUtils.getCookieName(getApplicationContext())+"="+PreferenceUtils.getCookie(this));
		//CookieManager.getInstance().flush();
		wv.setWebViewClient(new ViewClient());
		wv.setWebChromeClient(new ChromeClient());
		wv.setDownloadListener(this);
		WebSettings set= wv.getSettings();
		set.setAppCacheEnabled(false);
		set.setBuiltInZoomControls(true);
		set.setDisplayZoomControls(false);
		set.setDomStorageEnabled(true);
		set.setEnableSmoothTransition(true);
		set.setJavaScriptEnabled(true);
		set.setUseWideViewPort(true);
		set.setSupportZoom(true);
		wv.loadUrl(url);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putString("url",wv.getUrl());
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.webview,menu);
		return true;
	}

	@Override
	public void finish()
	{
		if(wv!=null){
		wv.pauseTimers();
		wv.stopLoading();
		wv.destroy();
		wv=null;}
		super.finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				if(wv.canGoBack())
					wv.goBack();
					else
					finish();
				break;
			case R.id.webview_view:
				try{startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(wv.getUrl())));}
				catch(Exception e){
					Toast.makeText(getApplicationContext(),"没有可用程序",Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.webview_other:
				try{startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(wv.getUrl()),"*/*"));}
				catch(Exception e){
					Toast.makeText(getApplicationContext(),"没有可用程序",Toast.LENGTH_SHORT).show();
				}
			break;
			case R.id.webview_copy:
				((ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setText(wv.getUrl());
				Toast.makeText(getApplicationContext(),"已复制到剪贴板",Toast.LENGTH_SHORT).show();
			break;
			case R.id.webview_refresh:
				wv.reload();
				break;
			case R.id.webview_close:
				finish();
				break;
			default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	class ChromeClient extends WebChromeClient
	{
		
		@Override
		public void onProgressChanged(WebView view, int newProgress)
		{
			
			pb.setProgress(newProgress);
		}

		@Override
		public void onReceivedTitle(WebView view, String title)
		{
			setTitle(title);
		}
		
		
	}
	class ViewClient extends WebViewClient
	{
		@SuppressLint("NewApi")
		@Override
		public boolean shouldOverrideUrlLoading(WebView view,android.webkit.WebResourceRequest request)
		{
			switch(request.getUrl().getScheme()){
				case "http":
				case "https":
				break;
				default:
				try{
				startActivity(new Intent(Intent.ACTION_VIEW).setData(request.getUrl()));
				}catch(Exception e){}
				return true;
			}
			return false;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			Uri uri=Uri.parse(url);
			switch(uri.getScheme()){
				case "http":
				case "https":
					break;
				default:
					try{
				startActivity(new Intent(Intent.ACTION_VIEW).setData(uri));
					}catch(Exception e){}
					return true;
			}
			return super.shouldOverrideUrlLoading(view, url);
		}
		

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
		{
			handler.proceed();
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			
			pb.setVisibility(pb.VISIBLE);
			getSupportActionBar().setSubtitle(url);
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			pb.setVisibility(pb.INVISIBLE);
			
		}
		
	}

	@Override
	public void onBackPressed()
	{
		if(wv.canGoBack())wv.goBack();
		else
		super.onBackPressed();
	}

	@Override
	public void onDownloadStart(final String url, String useragent, String content, final String type, final long size)
	{
		if(getSharedPreferences("setting",0).getBoolean("download_outside",false)&&!url.startsWith(PreferenceUtils.getHost(this)))
			startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(url),"*/*"));
			else{
		final String name=URLDecoder.decode(URLUtil.guessFileName(url,content,type));
		new AlertDialog.Builder(this).setTitle("确认下载").setMessage(name).setPositiveButton("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					DownloadItem di=new DownloadItem();
					di.setUrl(url);
					di.setTitle(name);
					di.setReferer(wv.getUrl());
					di.setTotal(size);
					di.setDir(getSharedPreferences("setting",0).getString("path",Environment.getExternalStorageDirectory().getAbsolutePath()+"/yaohuo"));
					di.setType(type);
					di.setTime(System.currentTimeMillis());
					di.setCookie(CookieManager.getInstance().getCookie(url));
					di.save();
					startService(new Intent(getApplicationContext(),DownloadService.class).putExtra("down",di));
				}
			}).setNegativeButton("取消", null).show();
			}
	}

	
	
}
