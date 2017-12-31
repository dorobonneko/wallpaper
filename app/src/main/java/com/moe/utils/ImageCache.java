package com.moe.utils;
import android.widget.ImageView;
import android.support.v4.util.LruCache;
import android.graphics.Bitmap;
import android.os.Build;
import com.moe.thread.ImageLoadThread;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import java.util.HashMap;
import com.moe.yaohuo.R;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.HashSet;
import android.util.TypedValue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.DrawableRequestBuilder;
import android.app.Activity;
public class ImageCache implements ImageLoadThread.OnLoadSuccessListener
{
	private static ImageCache imageCache;
	private HashSet<ImageView> llm;
	private LruCache<String,Drawable> lc;
	private ImageLoadThread ilt;
	private boolean load=true;
	private Context context;
	private ImageCache(Context context){
		this.context=context;
		ilt=new ImageLoadThread(context);
		ilt.setOnloadSuccessListener(this);
		llm=new HashSet<>();
		lc=new LruCache<String,Drawable>((int)(Runtime.getRuntime().totalMemory()/1024.0/2)){
			public int sizeOf(String url,Drawable b){
				return (int)(b.getIntrinsicWidth()*b.getIntrinsicHeight()/1024.0);
			}
			public void entryRemoved(boolean evicted,String url,Bitmap old,Bitmap newValue){
				//if(evicted)remove(url);
				//else old.recycle();
			}
		};
	}

	public void pause()
	{
		load=false;
	}

	public void resume()
	{
		load=true;
	}
	public static ImageCache getInstance(Context context){
		if(imageCache==null)imageCache=new ImageCache(context);
		return imageCache;
	}
	public static void loadNo(String url,ImageView iv){
		getInstance(iv.getContext()).loadIamge(iv,url);
	}
	public static void load(String url,ImageView iv){
		//getInstance(iv.getContext()).loadIamge(iv,url);
		DrawableRequestBuilder drb=Glide.with(iv.getContext().getApplicationContext()).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE);
		//if(iv.getWidth()>0&&iv.getHeight()>0)
			//drb.override(iv.getWidth(),iv.getHeight());
			drb.centerCrop();
			drb.error(R.drawable.yaohuo);
			drb.into(iv);
	}
	
	public void loadIamge(ImageView iv,String url){
		if(url==null||iv==null)return;
		Drawable b=lc.get(url);
		if(b==null){
			iv.setImageDrawable(null);
			//iv.setImageResource(R.drawable.yaohuo);
			if(load){
				iv.setTag(url);
			llm.add(iv);
			ilt.loadImage(url);
			}
			//iv.setImageBitmap(def);
		}else{
			iv.setImageDrawable(b);
			//else
			//iv.setImageResource(R.drawable.test);
		}
	}

	@Override
	public void onLoadSuccess(String url, Drawable b)
	{
		try{
		lc.put(url,b);
			//b.setBounds(0,0,(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,b.getIntrinsicWidth(),context.getResources().getDisplayMetrics()),(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,b.getIntrinsicHeight(),context.getResources().getDisplayMetrics()));
		Iterator<ImageView> it=llm.iterator();
		while(it.hasNext()){
			ImageView c_url=it.next();
			if(url.equals(c_url.getTag()))
				it.remove();
				c_url.setImageDrawable(b);
		}
		//else
			//iv.setImageResource(R.drawable.test);
		/*if(iv.getTag()!=null){
			Object[] o=(Object[])iv.getTag();
			((RecyclerView.Adapter)o[0]).notifyItemChanged((Integer)o[1]);
		}*/
		}catch(Exception e){}
	}

	@Override
	public void onLoadError(String url)
	{
		Iterator<ImageView> it=llm.iterator();
		while(it.hasNext()){
			ImageView c_url=it.next();
			if(url.equals(c_url.getTag()))
				it.remove();
			c_url.setImageResource(R.drawable.yaohuo);
			
		}
		
	}
	
	
}
