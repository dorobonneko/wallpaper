package com.moe.widget;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.graphics.Rect;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.animation.AnimatorSet;

public class ViewImage extends ImageView
{
	private final static int CLICK=0;
	private Scale scale=Scale.Source;
	//private float scaleX=1,scaleY=1;
	private int count=0;
	public ViewImage(Context context){
		super(context);
		setScaleType(ScaleType.CENTER);
		//setWillNotDraw(false);
	}

	/*@Override
	public void setImageDrawable(Drawable drawable)
	{
		// TODO: Implement this method
		super.setImageDrawable(drawable);
	}
	*/
	
	
	/*@Override
	protected void onDraw(Canvas canvas)
	{
		
		//canvas.scale(scaleX,scaleY,canvas.getWidth()/2,canvas.getHeight()/2);
		super.onDraw(canvas);
	}*/

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch(event.getAction()){
			case event.ACTION_DOWN:
				//sx=event.getX();
				//sy=event.getY();
				count++;
				handler.removeMessages(CLICK);
				handler.sendEmptyMessageDelayed(CLICK,400);
				break;
			case event.ACTION_MOVE:
				break;
			case event.ACTION_CANCEL:
			case event.ACTION_UP:
				break;
		}
		return true;
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case CLICK:
					if(count>1){
						//放大
						switch(scale){
							case Source:
								scale=Scale.Width;
								break;
							case Width:
								scale=Scale.Height;
								break;
							case Height:
								scale=Scale.Source;
								break;
						}
						animeTo();
					}
					count=0;
					break;
			}
		}
		
	};
	private void animeTo(){
		float scaleX=1,scaleY=1;
		Drawable d=getDrawable();
		if(d!=null){
			float width=d.getIntrinsicWidth();
			float height=d.getIntrinsicHeight();

			switch(scale){
				case Source:
					//scaleX=width/canvas.getWidth();
					//scaleY=height/canvas.getHeight();
					break;
				case Width:
					scaleX=getWidth()/width;
					scaleY=scaleX;
					break;
				case Height:
					scaleX=getHeight()/height;
					scaleY=scaleX;
					break;
			}
		Animator animeX=ObjectAnimator.ofFloat(this,"ScaleX",new float[]{getScaleX(),scaleX});
		Animator animeY=ObjectAnimator.ofFloat(this,"ScaleY",new float[]{getScaleY(),scaleY});
		AnimatorSet set=new AnimatorSet();
			set.setDuration(300);
			set.playTogether(new Animator[]{animeX,animeY});
			set.start();
		}
	}
	private enum Scale{
		Source,Width,Height;
	}
}
