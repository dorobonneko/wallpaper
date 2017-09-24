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
import android.animation.ValueAnimator;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.animation.TypeEvaluator;
import android.graphics.RectF;

public class ViewImage extends ImageView implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener,ScaleGestureDetector.OnScaleGestureListener
{
	private Matrix matrix;
	@Override
	public boolean onScale(ScaleGestureDetector p1)
	{
		float scale=p1.getScaleFactor();
		scale=scale<1&&getScale()<=0.5?1:scale;
		matrix.postScale(scale, scale, p1.getFocusX(), p1.getFocusY());
		invalidate();
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector p1)
	{
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector p1)
	{

	}


	@Override
	public boolean onSingleTapConfirmed(MotionEvent p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onDoubleTap(final MotionEvent ev)
	{
		Drawable d=getDrawable();
		if (d == null)return true;
		float width=d.getIntrinsicWidth();
		float height=d.getIntrinsicHeight();
		final float[] scale=new float[]{getScale(),1};
		switch (this.scale)
		{
			case Source:
				this.scale = Scale.Width;
				scale[1] = getWidth() / width;
				break;
			case Width:
				this.scale = Scale.Height;
				scale[1] = getHeight() / height;
				break;
			case Height:
				this.scale = Scale.Source;
				break;
		}
		ValueAnimator vx=ObjectAnimator.ofFloat(new float[]{scale[0],scale[1]});
		vx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

				@Override
				public void onAnimationUpdate(ValueAnimator p1)
				{
					float scaleV=(float)p1.getAnimatedValue() / getScale();
					matrix.postScale(scaleV, scaleV, ev.getX(), ev.getY());
					invalidate();
				}
			});
		vx.addListener(new Animator.AnimatorListener(){

				@Override
				public void onAnimationStart(Animator p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onAnimationEnd(Animator p1)
				{
					checkPosition();
				}

				@Override
				public void onAnimationCancel(Animator p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onAnimationRepeat(Animator p1)
				{
					// TODO: Implement this method
				}
			});
		vx.setDuration(300);
		vx.start();
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent p1)
	{
		// TODO: Implement this method
		return true;
	}

	@Override
	public boolean onDown(MotionEvent p1)
	{
		return true;
	}

	@Override
	public void onShowPress(MotionEvent p1)
	{
	}

	@Override
	public boolean onSingleTapUp(MotionEvent p1)
	{
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent p1, MotionEvent p2, float p3, float p4)
	{
		/*float centerX=0,centerY=0;
		 for(int i=0;i<p2.getPointerCount();i++){
		 centerX+=p2.getX(i);
		 centerY+=p2.getY(i);
		 }*/
		matrix.postTranslate(-p3, -p4);
		invalidate();
		return scaleGesture.onTouchEvent(p2);
	}

	@Override
	public void onLongPress(MotionEvent p1)
	{

	}

	@Override
	public boolean onFling(MotionEvent p1, MotionEvent p2, float p3, float p4)
	{
		checkPosition();
		return true;
	}

	private Scale scale=Scale.Source;
	private int imageWidth,imageHeight;
	private GestureDetector gesture;
	private ScaleGestureDetector scaleGesture;
	public ViewImage(Context context)
	{
		super(context);
		setScaleType(ScaleType.CENTER);
		//setWillNotDraw(false);
		gesture = new GestureDetector(this);
		gesture.setIsLongpressEnabled(false);
		gesture.setOnDoubleTapListener(this);
		scaleGesture = new ScaleGestureDetector(context, this);
		matrix = new Matrix();
	}

	@Override
	public void setImageDrawable(Drawable drawable)
	{
		super.setImageDrawable(drawable);
		if (drawable != null)
		{
			imageWidth = drawable.getIntrinsicWidth();
			imageHeight = drawable.getIntrinsicHeight();
		}
	}



	@Override
	protected void onDraw(Canvas canvas)
	{
		//if(scaleX==1)tx=0;
		//if(scaleY==1)ty=0;
		canvas.setMatrix(matrix);
		//canvas.drawColor(0xffff0000);
		//canvas.scale(scaleX,scaleY,canvas.getWidth()/2,canvas.getHeight()/2);
		//canvas.translate(tx/scaleX,ty/scaleY);
		super.onDraw(canvas);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		// TODO: Implement this method
		return gesture.onTouchEvent(event);
	}

	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent event)
	{
		// TODO: Implement this method
		return gesture.onGenericMotionEvent(event);
	}
	private void checkPosition()
	{
		float scale=getScale();
		float[] trans=getTranslation();
		float[] translate=new float[4];
		translate[0]=trans[0];
		translate[1]=trans[1];
		RectF imagePos=new RectF();
		imagePos.left = (getWidth() - imageWidth) / 2 * scale + trans[0];
		imagePos.right = imagePos.left + imageWidth * scale;
		imagePos.top = (getHeight() - imageHeight) / 2 * scale + trans[1];
		imagePos.bottom = imagePos.top + imageHeight * scale;
		if (imagePos.right - imagePos.left <= getWidth())
		{
			if (imagePos.left < 0)
				translate[0]-=imagePos.left;
			else 
			if (imagePos.right > getWidth())
				translate[0]+=(getWidth() - imagePos.right);
		}
		else
		{
			if (imagePos.left > 0)
				translate[0]-=imagePos.left;
			else
			if (imagePos.right < getWidth())
				translate[0]+=(getWidth() - imagePos.right);
		}
		if (imagePos.bottom - imagePos.top <= getHeight())
		{
			if (imagePos.bottom > getHeight())
				translate[1]+=(getHeight() - imagePos.bottom);
			else
			if (imagePos.top < 0)
				translate[1] -=imagePos.top;
		}
		else
		{
			if (imagePos.top > 0)
				translate[1] -=imagePos.top;
			else
			if (imagePos.bottom < getHeight())
				translate[1]+=(getHeight() - imagePos.bottom);
		}
		ValueAnimator transAnime=new ValueAnimator();
		transAnime.setObjectValues(new float[][]{trans,translate});
		transAnime.setEvaluator(new TypeEvaluator<float[]>(){

				@Override
				public float[] evaluate(float p1, float[] p2, float[] p3)
				{
					float[] tran=new float[]{(p3[0]-p2[0])*p1-p3[2],(p3[1]-p2[1])*p1-p3[3]};
					p3[2]+=tran[0];
					p3[3]+=tran[1];
					return tran;
				}
			});
		transAnime.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

				@Override
				public void onAnimationUpdate(ValueAnimator p1)
				{
					float[] trans=(float[]) p1.getAnimatedValue();
					matrix.postTranslate(trans[0],trans[1]);
					invalidate();
				}
			});
		transAnime.setDuration(300);
		transAnime.start();
	}
	public float getScale()
	{
		float[] values=new float[9];
		matrix.getValues(values);
		return values[Matrix.MSCALE_X];
	}
	public float[] getTranslation()
	{
		float[] values=new float[9];
		matrix.getValues(values);
		return new float[]{values[Matrix.MTRANS_X],values[Matrix.MTRANS_Y]};
	}
	private enum Scale
	{
		Source,Width,Height;
	}
}
