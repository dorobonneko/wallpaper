package com.moe.internal;
import android.widget.TextView;
import android.text.Html;
import com.moe.utils.BitmapUtils;
import com.moe.utils.ImageLoad;
import com.moe.utils.UrlUtils;
import com.moe.utils.PreferenceUtils;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import java.util.Map;
import java.util.HashMap;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import android.graphics.Bitmap;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import java.util.Set;
import android.graphics.drawable.LevelListDrawable;
import com.bumptech.glide.RequestManager;
import com.moe.yaohuo.R;
import java.util.ArrayList;
import java.util.HashSet;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.request.Request;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.app.Activity;
import java.util.Iterator;
public class ImageGetter implements Html.ImageGetter,ImageLoad.Callback,Drawable.Callback
{
	private TextView tv;
	private boolean crop;
	private boolean anime;
	private int width,height;
	private Set<ViewTarget> targets=null;
	public ImageGetter(TextView tv,boolean crop){
		this.tv=tv;
		this.crop=crop;
		tv.setTag(R.id.callback,this);
		DisplayMetrics dm=tv.getResources().getDisplayMetrics();
		height=dm.heightPixels;
		width=dm.widthPixels;
		anime=tv.getContext().getSharedPreferences("moe",0).getBoolean("emoji",false);
		targets=new HashSet<>();
	}
	@Override
	public android.graphics.drawable.Drawable getDrawable(String p1)
	{
		final LevelListDrawable d=new LevelListDrawable();
		/*ImageLoad il=new ImageLoad.Builder(tv.getContext(),UrlUtils.getAbsUrl(tv.getContext(),p1))
			.header("User-Agent",PreferenceUtils.getUserAgent())
			.header("Cookie", PreferenceUtils.getCookieName(tv.getContext())+"=" + PreferenceUtils.getCookie(tv.getContext()))
			.header("Content-Type","image/*")
			.header("Accept","**")
			.callback(this,d).get();
		il.execute();*/
		
		GlideUrl gu=new GlideUrl(UrlUtils.getAbsUrl(tv.getContext(), p1), new Headers(){

				@Override
				public Map<String, String> getHeaders()
				{
					Map<String,String> map=new HashMap<>();
					map.put("Cookie",PreferenceUtils.getCookieName(tv.getContext())+"="+PreferenceUtils.getCookie(tv.getContext()));
					return map;
				}
			});
		try{
			if(tv.getContext() instanceof Activity&&((Activity)tv.getContext()).isDestroyed())return d;
		DrawableTypeRequest dtr=Glide.with(tv.getContext()).load(gu);
			ViewTarget vt=new TextViewTarget(tv,d);
			targets.add(vt);
		if(!anime)
			dtr.asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(vt);
			else
			dtr.diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into(vt);
		}catch(Exception e){}
			return d;
	}
	@Override
	public void invalidateDrawable(Drawable p1)
	{
//		tv.getGlobalVisibleRect(rect);
//		if(rect.left<0||rect.top<0||rect.right<0||rect.bottom<0||rect.left>width||rect.top>height)
//			return;
//			else
		/*if(((Activity)tv.getContext()).isDestroyed())
			Glide.clear(tv);
			else
		*/
		if(tv.getContext() instanceof Activity&&((Activity)tv.getContext()).isDestroyed()){
		Iterator<ViewTarget> i=targets.iterator();
		while(i.hasNext()){
			Glide.clear(i.next());
			i.remove();
		}
		}else
		tv.setText(tv.getText());
	}

	@Override
	public void scheduleDrawable(Drawable p1, Runnable p2, long p3)
	{
		
	}

	@Override
	public void unscheduleDrawable(Drawable p1, Runnable p2)
	{
		// TODO: Implement this method
	}
	
	@Override
	public void onLoad(String url, Drawable b,Object o)
	{
		LevelListDrawable d=(LevelListDrawable) o;
		if (b != null)
		{
			if(url.endsWith("gif"))
				toString();
			if(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,b.getIntrinsicWidth(),tv.getResources().getDisplayMetrics())>tv.getWidth()-(tv.getPaddingLeft()+tv.getPaddingRight())){
				Bitmap bit=null;
				if(b instanceof BitmapDrawable)
				bit=((BitmapDrawable)b).getBitmap();
				else if(b instanceof GlideBitmapDrawable)
				bit=((GlideBitmapDrawable)b).getBitmap();
				b=new BitmapDrawable(BitmapUtils.scaleBitmap(bit,((float)(tv.getWidth()-(tv.getPaddingLeft()+tv.getPaddingRight()))/bit.getWidth())));
			}else if(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,b.getIntrinsicWidth(),tv.getResources().getDisplayMetrics())<(tv.getWidth()-(tv.getPaddingLeft()+tv.getPaddingRight())/2))
				{
					Bitmap bit=null;
					if(b instanceof BitmapDrawable)
						bit=((BitmapDrawable)b).getBitmap();
					else if(b instanceof GlideBitmapDrawable)
						bit=((GlideBitmapDrawable)b).getBitmap();
					b=new BitmapDrawable(BitmapUtils.scaleBitmap(bit,2));
					
				}
			if(b==null)return;
			d.addLevel(0,1,b);
			d.setLevel(1);
			d.setBounds(0, 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,b.getIntrinsicWidth(),tv.getResources().getDisplayMetrics()),(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,b.getIntrinsicHeight(),tv.getResources().getDisplayMetrics()));
			//d.setLevel(1);
			tv.invalidate();
			tv.setText(tv.getText());
		}
	}
	
}
