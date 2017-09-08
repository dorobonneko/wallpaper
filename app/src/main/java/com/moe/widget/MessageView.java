package com.moe.widget;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.Bitmap;
import android.graphics.Path;

public class MessageView extends AppCompatTextView
{
	private Path path=null;
	//private Paint p=new Paint();
	public MessageView(Context context,AttributeSet attrs){
		super(context,attrs);
		/*p.setColor(0xffff0000);
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
		canvas.drawColor(0xffff8888);
		super.onDraw(canvas);
		/*Bitmap src=Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
		Canvas mas=new Canvas(src);
		mas.drawCircle(getWidth()/2,getHeight()/2,(getWidth()< getHeight()?getWidth():getHeight())/2,p);
		Bitmap dst=Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
		Canvas c=new Canvas(dst);
		c.drawBitmap(src,0,0,null);
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
