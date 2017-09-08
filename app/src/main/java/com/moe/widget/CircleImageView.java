package com.moe.widget;
import android.widget.ImageView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.annotation.SuppressLint;
import android.support.v7.widget.AppCompatImageView;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Xfermode;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap;
import android.graphics.Region;
import android.graphics.Path;

public class CircleImageView extends AppCompatImageView
{
	private Path path=null;
	//private Paint p=new Paint();
	public CircleImageView(Context context,AttributeSet attrs){
		super(context,attrs);
		/*p.setColor(0xff000000);
		p.setAntiAlias(true);
		p.setDither(true);*/
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(path==null){
			path=new Path();
		path.addCircle(getWidth()/2,getHeight()/2,(getWidth()< getHeight()?getWidth():getHeight())/2,Path.Direction.CW);
		}
		canvas.clipPath(path);
		super.onDraw(canvas);
		/*Bitmap src=Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
			Canvas mas=new Canvas(src);
			mas.drawCircle(getWidth()/2,getHeight()/2,(getWidth()< getHeight()?getWidth():getHeight())/2,p);
		Bitmap dst=Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
		Canvas c=new Canvas(dst);
		super.onDraw(c);
		int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);  
		//canvas.save();
		canvas.drawBitmap(dst,0,0,p);
		p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawBitmap(src,0,0,p);
		p.setXfermode(null);
		canvas.restoreToCount(sc);
		dst.recycle();
		src.recycle();*/
	}
	
	
}
