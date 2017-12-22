package com.moe.graphics;

import com.bumptech.glide.request.target.ViewTarget;
import android.widget.ImageView;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import android.graphics.drawable.Drawable;
import com.moe.yaohuo.R;

public class ImageViewTarget extends ViewTarget<ImageView,GlideDrawable>
{

	@Override
	public void onResourceReady(GlideDrawable p1, GlideAnimation<? super GlideDrawable> p2)
	{
		getView().setImageDrawable(p1);
		getView().setTag(R.id.state,true);
	}
	
	public ImageViewTarget(ImageView iv){
		super(iv);
	}

	@Override
	public void onLoadFailed(Exception e, Drawable errorDrawable)
	{
		if(errorDrawable!=null)
		getView().setImageDrawable(errorDrawable);
		getView().setTag(R.id.state,false);
	}
	
}
