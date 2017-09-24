package com.moe.internal;
import com.bumptech.glide.request.target.ViewTarget;
import android.widget.TextView;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import android.util.TypedValue;
import android.graphics.Bitmap;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.moe.utils.BitmapUtils;
import android.graphics.drawable.LevelListDrawable;
import com.bumptech.glide.request.Request;
import com.moe.yaohuo.R;
import android.graphics.drawable.Drawable;
import android.app.Activity;
import com.bumptech.glide.Glide;
public class TextViewTarget extends ViewTarget<TextView,Object> 
{
	
	private LevelListDrawable d;
	private Request request;
	public TextViewTarget(TextView tv,LevelListDrawable d){
		super(tv);
		this.d=d;
			}
	@Override
	public void onResourceReady(Object obj, GlideAnimation<? super Object> p2)
	{
		int width=getView().getWidth()-(getView().getPaddingLeft()+getView().getPaddingRight());
		if(obj instanceof GlideDrawable){
			GlideDrawable gd=(GlideDrawable) obj;
			Bitmap bit=null;
			if(!gd.isAnimated()){
			if(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,gd.getIntrinsicWidth(),getView().getResources().getDisplayMetrics())>width){
				bit=((GlideBitmapDrawable)gd).getBitmap();
				bit=BitmapUtils.scaleBitmap(bit,(float)width/bit.getWidth());
			}else if(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,gd.getIntrinsicWidth(),getView().getResources().getDisplayMetrics())<width/2)
			{
				bit=((GlideBitmapDrawable)gd).getBitmap();
				bit=BitmapUtils.scaleBitmap(bit,2);

			}
		}
		if(bit!=null)
			gd=new GlideBitmapDrawable(getView().getResources(),bit);
		d.addLevel(2,2,gd);
		d.setLevel(2);
		d.setBounds(0,0,gd.getIntrinsicWidth(),gd.getIntrinsicHeight());
		//p1.setCallback(getView());
		//d.setCallback(ImageGetter.this);
		if(gd.isAnimated()){
			getView().setTextIsSelectable(false);
			//d.setCallback((ImageGetter)getView().getTag(R.id.callback));
			gd.setLoopCount(gd.LOOP_FOREVER);
			gd.start();
		}else{
			getView().setText(getView().getText());
			//tv.invalidate();
		}
		}else if(obj instanceof Bitmap){
			Bitmap bit=(Bitmap) obj;
			if(bit.getWidth()>getView().getWidth())
			bit=BitmapUtils.scaleBitmap(bit,((float)width/bit.getWidth()));
			if(bit==null)return;
			d.addLevel(1,1,new GlideBitmapDrawable(getView().getResources(),bit));
			d.setLevel(1);
			d.setBounds(0,0,bit.getWidth(),bit.getHeight());
			getView().setText(getView().getText());
			//tv.invalidate();
		}
	}

	@Override
	public void setRequest(Request request)
	{
		this.request=request;
	}

	@Override
	public Request getRequest()
	{
		// TODO: Implement this method
		return request;
	}

	@Override
	public void onStart()
	{
		d.setCallback((ImageGetter)getView().getTag(R.id.callback));
	}

	@Override
	public void onStop()
	{
		d.setCallback(null);
	}
	

	@Override
	public void onDestroy()
	{
		Glide.clear(this);
		getView().setTag(null);
		getView().setTag(R.id.callback,null);
		d.setCallback(null);
	}
	
}
