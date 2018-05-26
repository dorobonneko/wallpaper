package com.moe.LiveVisualizer.widget;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.view.MotionEvent;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.graphics.Region;
import android.util.TypedValue;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RegionIterator;
import android.os.Handler;
import android.os.Looper;

public class CropView extends View implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener,ScaleGestureDetector.OnScaleGestureListener,Animator.AnimatorListener
{
	private Bitmap bit;
	private float aspectX,aspectY;
	private Paint paint,shadow;
	private boolean two,crop;
	private Scale scale=Scale.Source;
	//private int imageWidth,imageHeight;
	private GestureDetector gesture;
	private ScaleGestureDetector scaleGesture;
	private Matrix matrix;
	private RectF vertical=new RectF(),horizontal=new RectF(),bounds=new RectF();
	private int width;
	private int height;
	private Region regionv=new Region(),regionh=new Region();
	private float borderWidth;
	private boolean isScale;//是否正在缩放动画
	private int direction=-1;//裁剪框方向
	private DIRECTION mDirection=DIRECTION.NONE;//在裁剪框上的位置
	public CropView(Context context)
	{
		this(context, null);
	}
	public CropView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		shadow = new Paint();
		shadow.setColor(0xa0000000);
		shadow.setStyle(Paint.Style.FILL);
		paint = new Paint();
		paint.setColor(0xffffffff);
		//paint.setStrokeWidth(4);
		paint.setStyle(Paint.Style.FILL);
		gesture = new GestureDetector(this);
		gesture.setIsLongpressEnabled(false);
		gesture.setOnDoubleTapListener(this);
		scaleGesture = new ScaleGestureDetector(context, this);
		matrix = new Matrix();
		borderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
	}

	@Override
	public boolean onScale(ScaleGestureDetector p1)
	{
		float scale=p1.getScaleFactor();
		//scale=scale<1&&getScale()*scale<=getMinScale()?1:scale;
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
	public void onScaleEnd(final ScaleGestureDetector scaleEvent)
	{
		checkScale(scaleEvent.getFocusX(),scaleEvent.getFocusY());
	}
	private void checkScale(final float x,final float y){
		if ( getScale() < getMinScale() &&!isScale)
		{
			isScale = true;
			ValueAnimator vx=ObjectAnimator.ofFloat(new float[]{getScale(),getMinScale()});
			vx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

					@Override
					public void onAnimationUpdate(ValueAnimator p1)
					{
						float scaleV=(float)p1.getAnimatedValue() / getScale();
						matrix.postScale(scaleV, scaleV, x,y);
						invalidate();
					}
				});
			vx.addListener(this);
			vx.setDuration(300);
			vx.start();
		}
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

		if ( bit == null )return true;
		float width=bit.getWidth();
		float height=bit.getHeight();
		final float[] scale=new float[]{getScale(),1};
		switch ( this.scale )
		{
			case Source:
				this.scale = Scale.Width;
				scale[1] = getIWidth() / width;
				break;
			case Width:
				this.scale = Scale.Height;
				scale[1] = getIHeight() / height;
				break;
			case Height:
				this.scale = Scale.Source;
				break;
		}
		scale[1]=(scale[1]<getMinScale()?getMinScale():scale[1]);
		isScale = true;
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
		vx.addListener(this);
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
		if(!crop)return true;
		if(regionv.contains((int)p1.getX(),(int)p1.getY())){
			direction=0;
			mDirection=getDirection(regionv.getBounds(),p1.getX(),p1.getY());
		}else if(regionh.contains((int)p1.getX(),(int)p1.getY())){
			direction=1;
			mDirection=getDirection(regionh.getBounds(),p1.getX(),p1.getY());
			}else direction=-1;
		return true;
	}
	private DIRECTION getDirection(Rect rect,float x,float y){
		//计算具体触摸点位置
		if(x<rect.left+borderWidth*5&&y<rect.top+borderWidth*5)
			return DIRECTION.TOPLEFT;
		if(x>rect.right-borderWidth*5&&y<rect.top+borderWidth*5)
			return DIRECTION.TOPRIGHT;
		if(x<rect.left+borderWidth*5&&y>rect.bottom-borderWidth*5)
			return DIRECTION.BOTTOMLEFT;
		if(x>rect.right-borderWidth*5&&y>rect.bottom-borderWidth*5)
			return DIRECTION.BOTTOMRIGHT;
			//点击四角时以x轴作为比例判断
		if(y<rect.top+borderWidth)
			return DIRECTION.TOP;
		if(x<rect.left+borderWidth)
			return DIRECTION.LEFT;
		if(x>rect.right-borderWidth)
			return DIRECTION.RIGHT;
		if(y>rect.bottom-borderWidth)
			return DIRECTION.BOTTOM;
		return DIRECTION.NONE;
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
	public boolean onScroll(MotionEvent p1, MotionEvent p2, float offsetX, float offsetY)
	{
		offsetX=-offsetX;
		offsetY=-offsetY;
		RectF rect=null;
		switch(direction){
			case 0:
				rect=vertical;
				break;
			case 1:
				rect=horizontal;
				break;
			default:
		matrix.postTranslate(offsetX, offsetY);
		break;
		}
		if(rect!=null){
			RectF imagePos=getImageRect();
			switch(mDirection){
				case TOP:
				case BOTTOM:
					rect.top+=offsetY;
					rect.bottom+=offsetY;
					if(rect.top<imagePos.top){
						//根据图片修正
						float offset=(imagePos.top-rect.top);
						rect.top+=offset;
						rect.bottom+=offset;
					}
					if(rect.top<0){
						//根据view修正裁剪框并修正图片位置
						if(imagePos.top<0)
							matrix.postTranslate(0,-rect.top);
						float offset=-rect.top;
						rect.top+=offset;
						rect.bottom+=offset;
					}
					if(rect.bottom>imagePos.bottom){
						float offset=(rect.bottom-imagePos.bottom);
						rect.top-=offset;
						rect.bottom-=offset;
					}
					if(rect.bottom>getIHeight()){
						if(imagePos.bottom>getIHeight())
							matrix.postTranslate(0,getIHeight()-rect.bottom);
						float offset=(rect.bottom-getIHeight());
						rect.top-=offset;
						rect.bottom-=offset;
					}
					break;
				case LEFT:
				case RIGHT:
					rect.left+=offsetX;
					rect.right+=offsetX;
					if(rect.left<imagePos.left){
						//根据图片修正
						float offset=(imagePos.left-rect.left);
						rect.left+=offset;
						rect.right+=offset;
					}
					if(rect.left<0){
						//根据view修正裁剪框并修正图片位置
						if(imagePos.left<0)
							matrix.postTranslate(-rect.left,0);
						float offset=-rect.left;
						rect.left+=offset;
						rect.right+=offset;
					}
					if(rect.right>imagePos.right){
						float offset=(rect.right-imagePos.right);
						rect.left-=offset;
						rect.right-=offset;
					}
					if(rect.right>getIWidth()){
						if(imagePos.right>getIWidth())
							matrix.postTranslate(getIWidth()-rect.right,0);
						float offset=(rect.right-getIWidth());
						rect.left-=offset;
						rect.right-=offset;
					}
					
					break;
				case TOPLEFT:
				offsetY=direction==0?offsetX/aspectX*aspectY:offsetX/aspectY*aspectX;
					rect.left+=offsetX;
					rect.top+=offsetY;
					if(rect.width()<borderWidth*10){
						float offset=(rect.left-(rect.right-borderWidth*10));
						rect.left-=offset;
						rect.top-=direction==0?offset/aspectX*aspectY:offset/aspectY*aspectX;
					}
					if(rect.height()<borderWidth*10){
						float offset=(rect.top-(rect.bottom-borderWidth*10));
						rect.top-=offset;
						rect.left-=direction==0?offset/aspectY*aspectX:offset/aspectX*aspectY;
					}
					if(rect.left<0){
						rect.top+=direction==0?(-rect.left/aspectX*aspectY):(-rect.left/aspectY*aspectX);
						rect.left=0;
					}
					if(rect.top<0){
						rect.left+=direction==0?-rect.top/aspectY*aspectX:(-rect.top/aspectX*aspectY);
						rect.top=0;
					}
					break;
				case BOTTOMLEFT:
					offsetY=-(direction==0?offsetX/aspectX*aspectY:offsetX/aspectY*aspectX);
					rect.left+=offsetX;
					rect.bottom+=offsetY;
					if(rect.width()<borderWidth*10){
						float offset=(rect.left-(rect.right-borderWidth*10));
						rect.left-=offset;
						rect.bottom+=direction==0?offset/aspectX*aspectY:offset/aspectY*aspectX;
					}
					if(rect.height()<borderWidth*10){
						float offset=(rect.bottom-(rect.top+borderWidth*10));
						rect.bottom-=offset;
						rect.left+=direction==0?offset/aspectY*aspectX:offset/aspectX*aspectY;
					}
					if(rect.left<0){
						rect.bottom-=direction==0?(-rect.left/aspectX*aspectY):(-rect.left/aspectY*aspectX);
						rect.left=0;
					}
					if(rect.bottom>getIHeight()){
						rect.left+=direction==0?(rect.bottom-getIHeight())/aspectY*aspectX:((rect.bottom-getIHeight())/aspectX*aspectY);
						rect.bottom=getIHeight();
					}
					break;
				case TOPRIGHT:
					offsetY=direction==0?offsetX/aspectX*aspectY:offsetX/aspectY*aspectX;
					rect.right+=offsetX;
					rect.top-=offsetY;
					if(rect.width()<borderWidth*10){
						float offset=(rect.right-(rect.left+borderWidth*10));
						rect.right-=offset;
						rect.top+=direction==0?offset/aspectX*aspectY:offset/aspectY*aspectX;
					}
					if(rect.height()<borderWidth*10){
						float offset=(rect.top-(rect.bottom-borderWidth*10));
						rect.top-=offset;
						rect.right+=direction==0?offset/aspectY*aspectX:offset/aspectX*aspectY;
					}
					if(rect.right>getIWidth()){
						rect.top-=direction==0?((getIWidth()-rect.right)/aspectX*aspectY):((getIWidth()-rect.right)/aspectY*aspectX);
						rect.right=getIWidth();
					}
					if(rect.top<0){
						rect.right-=direction==0?-rect.top/aspectY*aspectX:(-rect.top/aspectX*aspectY);
						rect.top=0;
					}
					break;
				case BOTTOMRIGHT:
					offsetY=(direction==0?offsetX/aspectX*aspectY:offsetX/aspectY*aspectX);
					rect.right+=offsetX;
					rect.bottom+=offsetY;
					if(rect.width()<borderWidth*10){
						float offset=(rect.right-(rect.left+borderWidth*10));
						rect.right-=offset;
						rect.bottom-=direction==0?offset/aspectX*aspectY:offset/aspectY*aspectX;
					}
					if(rect.height()<borderWidth*10){
						float offset=(rect.bottom-(rect.top+borderWidth*10));
						rect.bottom-=offset;
						rect.right-=direction==0?offset/aspectY*aspectX:offset/aspectX*aspectY;
					}
					if(rect.right>getIWidth()){
						rect.bottom+=direction==0?(getIWidth()-rect.right)/aspectX*aspectY:(getIWidth()-rect.right)/aspectY*aspectX;
						rect.right=getIWidth();
					}
					if(rect.bottom>getIHeight()){
						rect.right-=direction==0?(rect.bottom-getIHeight())/aspectY*aspectX:((rect.bottom-getIHeight())/aspectX*aspectY);
						rect.bottom=getIHeight();
					}
					break;
				case NONE:
					rect.left+=offsetX;
					rect.right+=offsetX;
					rect.top+=offsetY;
					rect.bottom+=offsetY;
					if(rect.left<0){
						rect.right-=rect.left;
						rect.left=0;
					}
					if(rect.right>getIWidth()){
						rect.left-=(rect.right-getIWidth());
						rect.right=getIWidth();
					}
					if(rect.top<0){
						rect.bottom-=rect.top;
						rect.top=0;
					}
					if(rect.bottom>getIHeight()){
						rect.top-=(rect.bottom-getIHeight());
						rect.bottom=getIHeight();
					}
					break;
			}
			
			
		}
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
		//checkPosition();
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		if ( bit != null )
		{
			float scaleX = Math.min((float)width / bit.getWidth(), (float)height / bit.getHeight());
			matrix.setScale(scaleX, scaleX);
			matrix.postTranslate((width - bit.getWidth() * scaleX) / 2f, (height - bit.getHeight() * scaleX) / 2f);
			rect();
		}
	}

	public int getIWidth()
	{
		return width;
	}

	public int getIHeight()
	{
		return height;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if ( event.getAction() == event.ACTION_UP )
		{
			scaleGesture.onTouchEvent(event);
			checkScale(event.getX(),event.getY());
			checkPosition();
		}
		return gesture.onTouchEvent(event);
	}

	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent event)
	{
		// TODO: Implement this method
		return gesture.onGenericMotionEvent(event);
	}
	private RectF getBounds()
	{
		if ( crop )
		{
			if ( two )
			{
				bounds.left = Math.min(vertical.left, horizontal.left);
				bounds.top = Math.min(vertical.top, horizontal.top);
				bounds.right = Math.max(vertical.right, horizontal.right);
				bounds.bottom = Math.max(vertical.bottom, horizontal.bottom);
			}
			else
			{
				bounds.set(vertical);
			}
		}
		else
		{
			bounds.left = 0;
			bounds.top = 0;
			bounds.right = getIWidth();
			bounds.bottom = getIHeight();
		}
		return bounds;
	}
	private float getMinScale()
	{
		if ( bit == null )return 1;
		RectF bounds=getBounds();
		return Math.max(bounds.width() / bit.getWidth(), bounds.height() / bit.getHeight());
	}
	private void checkPosition()
	{
		if ( isScale||bit==null )return;
		float[] trans=getTranslation();
		float[] translate=new float[4];
		translate[0] = trans[0];
		translate[1] = trans[1];
		RectF imagePos=getImageRect();
		RectF border=getBounds();

		if ( imagePos.right - imagePos.left <= border.width() )
		{
			if ( imagePos.left < border.left )
				translate[0] += (border.left - imagePos.left);
			else 
			if ( imagePos.right > border.right )
				translate[0] -= (imagePos.right - border.right);
		}
		else
		{
			if ( imagePos.left > border.left )
				translate[0] -= (imagePos.left - border.left);
			else
			if ( imagePos.right < border.right )
				translate[0] += (border.right - imagePos.right);
		}
		if ( imagePos.bottom - imagePos.top <= border.height() )
		{
			if ( imagePos.bottom > border.bottom )
				translate[1] -= (imagePos.bottom - border.bottom);
			else
			if ( imagePos.top < border.top )
				translate[1] += (border.top - imagePos.top);
		}
		else
		{
			if ( imagePos.top > border.top )
				translate[1] -= (imagePos.top - border.top);
			else
			if ( imagePos.bottom < border.bottom )
				translate[1] += (border.bottom - imagePos.bottom);
		}
		ValueAnimator transAnime=new ValueAnimator();
		transAnime.setObjectValues(new float[][]{trans,translate});
		transAnime.setEvaluator(new TypeEvaluator<float[]>(){

				@Override
				public float[] evaluate(float p1, float[] p2, float[] p3)
				{
					float[] tran=new float[]{(p3[0] - p2[0]) * p1 - p3[2],(p3[1] - p2[1]) * p1 - p3[3]};
					p3[2] += tran[0];
					p3[3] += tran[1];
					return tran;
				}
			});
		transAnime.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

				@Override
				public void onAnimationUpdate(ValueAnimator p1)
				{
					float[] trans=(float[]) p1.getAnimatedValue();
					matrix.postTranslate(trans[0], trans[1]);
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
	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(0xff000000);
		if ( bit == null )return;
		canvas.drawBitmap(bit, matrix, null);
		if ( !crop )return;
		Region backRegion=new Region(0, 0, canvas.getWidth(), canvas.getHeight());
		Rect v=new Rect();
		vertical.roundOut(v);
		backRegion.op(v, Region.Op.XOR);
		regionv.set(v);
		Path vp=new Path();
		vp.moveTo(v.left + borderWidth * 5, v.top + borderWidth);
		vp.lineTo(v.right - borderWidth * 5, v.top + borderWidth);
		vp.lineTo(v.right - borderWidth, v.top + borderWidth * 5);
		vp.lineTo(v.right - borderWidth, v.bottom - borderWidth * 5);
		vp.lineTo(v.right - borderWidth * 5, v.bottom - borderWidth);
		vp.lineTo(v.left + borderWidth * 5, v.bottom - borderWidth);
		vp.lineTo(v.left + borderWidth, v.bottom - borderWidth * 5);
		vp.lineTo(v.left + borderWidth, v.top + borderWidth * 5);
		vp.close();
		vp.addCircle(v.centerX(),v.centerY(),borderWidth*5,Path.Direction.CCW);
		Region vpr=new Region();
		vpr.setPath(vp, new Region(0, 0, canvas.getWidth(), canvas.getHeight()));
		regionv.op(vpr, Region.Op.XOR);
		RegionIterator iteratorv=new RegionIterator(regionv);
		while ( iteratorv.next(v) )
			canvas.drawRect(v, paint);
		if ( two )
		{
			Rect h=new Rect();
			horizontal.roundOut(h);
			backRegion.op(h, Region.Op.DIFFERENCE);
			regionh.set(h);
			Path hp=new Path();
			hp.moveTo(h.left + borderWidth * 5, h.top + borderWidth);
			hp.lineTo(h.right - borderWidth * 5, h.top + borderWidth);
			hp.lineTo(h.right - borderWidth, h.top + borderWidth * 5);
			hp.lineTo(h.right - borderWidth, h.bottom - borderWidth * 5);
			hp.lineTo(h.right - borderWidth * 5, h.bottom - borderWidth);
			hp.lineTo(h.left + borderWidth * 5, h.bottom - borderWidth);
			hp.lineTo(h.left + borderWidth, h.bottom - borderWidth * 5);
			hp.lineTo(h.left + borderWidth, h.top + borderWidth * 5);
			hp.close();
			hp.addCircle(h.centerX(),h.centerY(),borderWidth*5,Path.Direction.CCW);
			Region hpr=new Region();
			hpr.setPath(hp, new Region(0, 0, canvas.getWidth(), canvas.getHeight()));
			regionh.op(hpr, Region.Op.XOR);
			RegionIterator iteratorh=new RegionIterator(regionh);
			while ( iteratorh.next(h) )
				canvas.drawRect(h, paint);
		}
		
		RegionIterator bgi=new RegionIterator(backRegion);
		Rect bgr=new Rect();
		while ( bgi.next(bgr) )
			canvas.drawRect(bgr, shadow);
	}
	public void setImage(Bitmap bit)
	{
		if ( this.bit != null )
			this.bit.recycle();
		this.bit = bit;
		if ( bit != null )
		{
			float scaleX = Math.min((float)getIWidth() / bit.getWidth(), (float)getIHeight() / bit.getHeight());
			matrix.setScale(scaleX, scaleX);
			matrix.postTranslate((getIWidth() - bit.getWidth() * scaleX) / 2f, (getIHeight() - bit.getHeight() * scaleX) / 2f);
		}
		rect();
		invalidate();
		//setWillNotDraw(bit==null);

	}
	public void setCrop(float aspectX, float aspectY, boolean two)
	{
		crop = true;
		this.aspectX = aspectX;
		this.aspectY = aspectY;
		this.two = two;
		rect();
		invalidate();
	}
	private void rect()
	{
		if(bit==null)return;
		if ( aspectX<= 0 || aspectY<= 0 || bit == null )return;
		float aspect=aspectX/aspectY;
		float width,height;
		RectF image=getImageRect();
		width=image.width();
		height=image.width()/aspect;
		if(height>image.height()){
			height=image.height();
			width=image.height()*aspect;
		}
		vertical.left = (getIWidth() - width) / 2;
		vertical.top = (getIHeight() - height) / 2;
		vertical.right = vertical.left + width;
		vertical.bottom = vertical.top + height;
		if ( two )
		{
			width=image.width();
			height=image.width()*aspect;
			if(height>image.height()){
				height=image.height();
				width=image.height()/aspect;
			}
			horizontal.left = (getIWidth() - width) / 2;
			horizontal.right = horizontal.left + width;
			horizontal.top = (getIHeight() - height) / 2;
			horizontal.bottom = horizontal.top + height;
		}

	}
	public void removeCrop()
	{
		crop = false;
	}
	public boolean isCrop()
	{
		return crop;
	}
	public void crop(final CropCallback callback)
	{
		if(crop)
		new Thread(){
			public void run()
			{
				//裁剪图片
				doCrop(callback);
			}
		}.start();
		else if(callback!=null)
			callback.success(null,null);
	}
	private void doCrop(final CropCallback call){
		if(bit==null){
			if(call!=null)
				call.success(null,null);
			return;}
		RectF image=getImageRect();
		float scale=getScale();
		Rect vCrop=new Rect();
		vCrop.left=(int)((vertical.left-image.left)/scale);
		vCrop.top=(int)((vertical.top-image.top)/scale);
		vCrop.right=(int)(vertical.width()/scale);
		vCrop.bottom=(int)(vertical.height()/scale);
		final Bitmap b1=Bitmap.createBitmap(bit,vCrop.left,vCrop.top,vCrop.right,vCrop.bottom,null,false);
		if(two){
			Rect hCrop=new Rect();
			hCrop.left=(int)((horizontal.left-image.left)/scale);
			hCrop.top=(int)((horizontal.top-image.top)/scale);
			hCrop.right=(int)(horizontal.width()/scale);
			hCrop.bottom=(int)(horizontal.height()/scale);
			final Bitmap b2=Bitmap.createBitmap(bit,hCrop.left,hCrop.top,hCrop.right,hCrop.bottom,null,false);
			call.success(b1,b2);
			}else
			call.success(b1,null);
		/*new Handler(Looper.getMainLooper()).post(new Runnable(){

				@Override
				public void run()
				{
					call.success(b1,null);
				}
			});*/
	}
	@Override
	public void onAnimationStart(Animator p1)
	{
		isScale = true;
	}

	@Override
	public void onAnimationEnd(Animator p1)
	{
		isScale = false;
		checkPosition();
	}

	@Override
	public void onAnimationCancel(Animator p1)
	{
		isScale = false;
	}

	@Override
	public void onAnimationRepeat(Animator p1)
	{
	}
	public abstract interface CropCallback
	{
		void success(Bitmap b1, Bitmap b2);
	}
	private enum DIRECTION{
		TOPLEFT,TOPRIGHT,BOTTOMLEFT,BOTTOMRIGHT,NONE,TOP,RIGHT,BOTTOM,LEFT;
	}
	private RectF getImageRect(){
		if(bit==null)return null;
		float scale=getScale();
		float[] trans=getTranslation();
		float imageWidth=bit.getWidth() * scale;
		float imageHeight=bit.getHeight() * scale;
		RectF imagePos=new RectF();
		imagePos.left = trans[0];
		imagePos.right = imagePos.left + imageWidth;
		imagePos.top = trans[1];
		imagePos.bottom = imagePos.top + imageHeight;
		return imagePos;
	}
}
