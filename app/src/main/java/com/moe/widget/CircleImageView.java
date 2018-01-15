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
import android.graphics.drawable.Drawable;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Color;

public class CircleImageView extends AppCompatImageView
{
	private Path path=null;
	private boolean useCircle;
	private Paint paint,shadow;
	private RectF size;
	private Region clip;
	private float shadowRadius=3;
	public CircleImageView(Context context,AttributeSet attrs){
		super(context,attrs);
		TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.circle_imageview);
		useCircle=ta.getBoolean(R.styleable.circle_imageview_useCircle,false);
		ta.recycle();
		if(useCircle){
			paint=new Paint();
			paint.setDither(true);
			paint.setAntiAlias(true);
			paint.setColor(0xffffffff);
			paint.setStrokeWidth(5);
			paint.setStyle(Paint.Style.STROKE);
		}
		clip=new Region();
		shadow=new Paint();
		/*p.setAntiAlias(true);
		p.setDither(true);*/
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO: Implement this method
		super.onMeasure(widthMeasureSpec,heightMeasureSpec);
		
	}

	
	@Override
	protected void onDraw(Canvas canvas)
	{
		if(paint!=null)
		size=new RectF(paint.getStrokeWidth()/2+shadowRadius,paint.getStrokeWidth()/2,canvas.getWidth()-paint.getStrokeWidth()/2-shadowRadius,canvas.getHeight()-paint.getStrokeWidth()/2-shadowRadius*2);
		if(path==null){
			path=new Path();
			path.addCircle(canvas.getWidth()/2,canvas.getHeight()/2-shadowRadius,(canvas.getWidth()< canvas.getHeight()?canvas.getWidth():canvas.getHeight())/2-shadowRadius,Path.Direction.CW);
			clip.setPath(path,new Region(0,0,canvas.getWidth(),canvas.getHeight()));
		}
		canvas.save();
		if(shadow.getShader()==null){
			RadialGradient mRadialGradient= new RadialGradient(canvas.getWidth() / 2, canvas.getHeight() / 2,
															   (canvas.getWidth()< canvas.getHeight()?canvas.getWidth():canvas.getHeight())/2, new int[] { Color.TRANSPARENT,Color.TRANSPARENT,0x5d000000, Color.TRANSPARENT },
															   null, Shader.TileMode.CLAMP);
			shadow.setShader(mRadialGradient);
		}
		canvas.clipRegion(clip,Region.Op.XOR);
		canvas.drawCircle(canvas.getWidth()/2,canvas.getHeight()/2,(canvas.getWidth()< canvas.getHeight()?canvas.getWidth():canvas.getHeight())/2,shadow);
		canvas.restore();
		Drawable d=getDrawable();
		if(d!=null){
			Bitmap buffer=Bitmap.createBitmap(canvas.getWidth(),canvas.getHeight(),Bitmap.Config.ARGB_8888);
			Canvas buffer_canvas=new Canvas(buffer);
			buffer_canvas.clipPath(path);
			d.draw(buffer_canvas);
			//canvas.clipPath(path);
			canvas.drawBitmap(buffer,0,0,null);
			//canvas.clipPath(path,Region.Op.REVERSE_DIFFERENCE);
			//canvas.drawPath(path,new Paint());
			buffer.recycle();
			}
		//canvas.restore();
		//canvas.drawPath(path,refresh);
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
