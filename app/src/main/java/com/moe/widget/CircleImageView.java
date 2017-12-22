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
import android.content.res.TypedArray;
import com.moe.yaohuo.R;
import android.graphics.RectF;

public class CircleImageView extends AppCompatImageView
{
	private Path path=null;
	private boolean useCircle;
	private Paint paint;
	private RectF size;
	public CircleImageView(Context context,AttributeSet attrs){
		super(context,attrs);
		TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.circle_imageview);
		useCircle=ta.getBoolean(R.styleable.circle_imageview_useCircle,false);
		ta.recycle();
		if(useCircle){
			paint=new Paint();
			paint.setColor(0xffffffff);
			paint.setStrokeWidth(5);
			paint.setStyle(Paint.Style.STROKE);
		}
		/*p.setColor(0xff000000);
		p.setAntiAlias(true);
		p.setDither(true);*/
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(path==null){
			path=new Path();
		size=new RectF(0,0,getWidth(),getHeight());
		path.addCircle(getWidth()/2,getHeight()/2,(getWidth()< getHeight()?getWidth():getHeight())/2,Path.Direction.CW);
		}
		canvas.clipPath(path);
		super.onDraw(canvas);
		if(paint!=null)
			canvas.drawArc(size,0,360,false,paint);
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
