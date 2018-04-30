package com.moe.LiveVisualizer.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.view.MotionEvent;
import android.view.View;
import android.util.AttributeSet;
import android.content.Intent;
import android.util.TypedValue;
import android.graphics.ComposeShader;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Point;
import android.graphics.Xfermode;
import android.graphics.PointF;
import android.view.GestureDetector;
import java.util.HashMap;
import android.icu.math.MathContext;

public class ColorPickerView extends View{
	private int betweenWidth;
	private float minSize;
	private LinearGradient mValShader,mHueShader;
	private Paint mHSVPaint,mHSVPickerPaint,mHuePaint,mHuePickerPaint,mAlphaPaint,circlePaint,mAlphaPickerPaint;
	//hsv
	private float mHue = 360f;
    private float mSat = 1f;
    private float mVal = 1f;
	//rect
	private RectF mHueRect,mSatValRect,mAlphaRect,mCircleRect;
	private int mHueWidth;
	//alpha
	private int alpha=0xff;
	private PointF circlePoint;
	private int circleRadius;
	private GestureDetector gestureDetector;
	//sv选中圆半径
	private float SVPickerRadius;
	public ColorPickerView(Context context){
		this(context,null);
	}
	public ColorPickerView(Context context,AttributeSet attrs){
		super(context,attrs);
		//setWillNotDraw(false);
		initPaint();
		gestureDetector=new GestureDetector(gesture);
		gestureDetector.setIsLongpressEnabled(false);
		mHueRect=new RectF();
		mSatValRect=new RectF();
		mAlphaRect=new RectF();
		circlePoint=new PointF();
		mCircleRect=new RectF();
		betweenWidth=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16,getResources().getDisplayMetrics());
		//minSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100,getResources().getDisplayMetrics());
		mHueWidth=(int)betweenWidth;
		setFocusable(true);
		setFocusableInTouchMode(true);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}
	private void initPaint(){
		mHSVPaint=new Paint();
		mHSVPickerPaint=new Paint();
		mHuePaint=new Paint();
		mHuePickerPaint=new Paint();
		mAlphaPaint=new Paint();
		circlePaint=new Paint();
		mAlphaPickerPaint=new Paint();
		mHSVPickerPaint.setStyle(Paint.Style.STROKE);
		mHuePickerPaint.setStyle(Paint.Style.STROKE);
		mAlphaPickerPaint.setStyle(Paint.Style.STROKE);
		mHuePickerPaint.setColor(0xff000000);
		mAlphaPickerPaint.setColor(0xff000000);
		mHSVPickerPaint.setColor(0xff7f7f7f);
		mHuePickerPaint.setStrokeWidth(2);
		mAlphaPickerPaint.setStrokeWidth(2);
		mHSVPickerPaint.setStrokeWidth(2);
		mHSVPickerPaint.setDither(true);
		mHSVPickerPaint.setAntiAlias(true);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width=MeasureSpec.getSize(widthMeasureSpec);
		int width_mode=MeasureSpec.getMode(width);
		int height=MeasureSpec.getSize(heightMeasureSpec);
		int height_mode=MeasureSpec.getMode(heightMeasureSpec);
		minSize=Math.min(width,height);
		if(width>height){
			//width=(int)(minSize*0.95f);
			width=height;
		}else{
			height=width;
		}
		super.onMeasure(width, height);
		
		/*switch(width_mode){
			case MeasureSpec.UNSPECIFIED:
				break;
			case MeasureSpec.AT_MOST:
				width=(int)minSize;
				break;
			case MeasureSpec.EXACTLY:
				break;
		}
		switch(height_mode){
			case MeasureSpec.UNSPECIFIED:
				break;
			case MeasureSpec.AT_MOST:
				height=width;
				break;
			case MeasureSpec.EXACTLY:
				break;
		}*/
		setMeasuredDimension(width,height);
		mHueRect.left=width-3*betweenWidth;
		mHueRect.top=betweenWidth;
		mHueRect.right=height-betweenWidth;
		mHueRect.bottom=height-4*betweenWidth;
		
		mAlphaRect.left=betweenWidth;
		mAlphaRect.top=height-3*betweenWidth;
		mAlphaRect.right=width-4*betweenWidth;
		mAlphaRect.bottom=height-betweenWidth;
		
		circlePoint.x=mHueRect.left+mHueRect.width()/2;
		circlePoint.y=mAlphaRect.top+mAlphaRect.height()/2;
		
		circleRadius=(int)(betweenWidth*1.2f);
		mCircleRect.left=circlePoint.x-circleRadius;
		mCircleRect.top=circlePoint.y-circleRadius;
		mCircleRect.right=circlePoint.x+circleRadius;
		mCircleRect.bottom=circlePoint.y+circleRadius;
		
		mSatValRect.left=betweenWidth;
		mSatValRect.top=betweenWidth;
		mSatValRect.right=mHueRect.left-betweenWidth;
		mSatValRect.bottom=mAlphaRect.top-betweenWidth;
		SVPickerRadius=betweenWidth/2.0f;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		drawHsv(canvas);
		drawLine(canvas);
		drawAlpha(canvas);
		drawCircle(canvas);
	}
	private void drawHsv(Canvas canvas){
		mHSVPaint.setShader(generateSVShader());
		canvas.drawRect(mSatValRect,mHSVPaint);
		Point point=satValToPoint(mSat,mVal);
		//mHSVPickerPaint.setColor(point.y<mSatValRect.bottom-mSatValRect.height()/2?0xff000000:0xffffffff);
		canvas.drawCircle(point.x,point.y,SVPickerRadius,mHSVPickerPaint);
	}
	private void drawLine(Canvas canvas){
		
        //初始化H线性着色器
		if(mHuePaint.getShader()==null)
			mHuePaint.setShader(generateHueShader());
        canvas.drawRect(mHueRect, mHuePaint);
		Point point=hueToPoint(mHue);
		canvas.drawRect(mHueRect.left-1,point.y-4,mHueRect.right+1,point.y+4,mHuePickerPaint);
	}
	private void drawAlpha(Canvas canvas){
		LinearGradient alpah=new LinearGradient(mAlphaRect.left,mAlphaRect.top,mAlphaRect.right,mAlphaRect.top,0,Color.HSVToColor(new float[]{mHue, mSat, mVal}),LinearGradient.TileMode.CLAMP);
		mAlphaPaint.setShader(alpah);
		canvas.drawRect(mAlphaRect,mAlphaPaint);
		float x=alphaToPoint(alpha);
		canvas.drawRect(x-4,mAlphaRect.top-1,x+4,mAlphaRect.bottom-1,mAlphaPickerPaint);
	}
	private void drawCircle(Canvas canvas){
		circlePaint.setColor(Color.HSVToColor(alpha,new float[]{mHue,mSat,mVal}));
		canvas.drawCircle(circlePoint.x,circlePoint.y,circleRadius,circlePaint);
	}
	private LinearGradient generateHueShader(){
		if (mHueShader == null) {
            int[] hue = new int[361];
            int count = 0;
            for (int i = hue.length - 1; i >= 0; i--, count++) {
                hue[count] = Color.HSVToColor(new float[]{i, 1f, 1f});
            }
            mHueShader = new LinearGradient(
				mHueRect.left,
				mHueRect.top,
				mHueRect.left,
				mHueRect.bottom,
				hue,
				null,
				LinearGradient.TileMode.CLAMP);
            }
		return mHueShader;
	}
	private ComposeShader generateSVShader() {
        //明度线性着色器
        if (mValShader == null) {
            mValShader = new LinearGradient(mSatValRect.left, mSatValRect.top, mSatValRect.left, mSatValRect.bottom,
											0xffffffff, 0xff000000, LinearGradient.TileMode.CLAMP);
        }
        //HSV转化为RGB
        int rgb = Color.HSVToColor(new float[]{mHue, 1f, 1f});
        //饱和线性着色器
        LinearGradient satShader = new LinearGradient(mSatValRect.left, mSatValRect.top, mSatValRect.right, mSatValRect.top,
											  0xffffffff, rgb, LinearGradient.TileMode.CLAMP);
        //组合着色器 = 明度线性着色器 + 饱和度线性着色器
		return new ComposeShader(mValShader, satShader, PorterDuff.Mode.MULTIPLY);
    }
	private float alphaToPoint(int alpha){
		return mAlphaRect.left+mAlphaRect.width()/255.0f*alpha;
	}
	private Point hueToPoint(float hue) {
        final RectF rect = mHueRect;
        final float height = rect.height();

        Point p = new Point();
        p.y = (int) (height - (hue * height / 360f) + rect.top);
        p.x = (int) rect.left;
        return p;
    }

    private Point satValToPoint(float sat, float val) {
        final float height = mSatValRect.height();
        final float width = mSatValRect.width();

        Point p = new Point();
        p.x = (int) (sat * width + mSatValRect.left);
        p.y = (int) ((1f - val) * height + mSatValRect.top);
        return p;
    }

    private float[] pointToSatVal(float x, float y) {
        final RectF rect = mSatValRect;
        float[] result = new float[2];

        float width = rect.width();
        float height = rect.height();

        if (x < rect.left) {
            x = 0f;
        } else if (x > rect.right) {
            x = width;
        } else {
            x = x - rect.left;
        }

        if (y < rect.top) {
            y = 0f;
        } else if (y > rect.bottom) {
            y = height;
        } else {
            y = y - rect.top;
        }
        result[0] = 1.f / width * x;
        result[1] = 1.f - (1.f / height * y);
        return result;
    }

    private float pointToHue(float y) {
        final RectF rect = mHueRect;
        float height = rect.height();
        if (y < rect.top) {
            y = 0f;
        } else if (y > rect.bottom) {
            y = height;
        } else {
            y = y - rect.top;
        }
        return 360f - (y * 360f / height);
    }
	private int pointToAlpha(float x){
		if(x<mAlphaRect.left)x=0;
		else if(x>mAlphaRect.right)x=mAlphaRect.width();
		else x-=mAlphaRect.left;
		return (int)(x*255f/mAlphaRect.width());
	}
	public void setOnColorCheckedListener(OnColorCheckedListener o){
		this.occl=o;
	}
	private OnColorCheckedListener occl;
	public abstract interface OnColorCheckedListener{
		void onColorChecked(int color);
	}
	private static final enum Area{
		N,A,H,SV,C;
	}
	private class Touch{
		Area touchArea;
		int touchId;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		
		boolean flag=gestureDetector.onTouchEvent(event);
		if(event.getAction()==MotionEvent.ACTION_UP||event.getAction()==MotionEvent.ACTION_CANCEL)
		gesture.onLongPress(event);
		return flag;
	}

	/*@Override
	public boolean onGenericMotionEvent(MotionEvent event)
	{
		// TODO: Implement this method
		return gestureDetector.onGenericMotionEvent(event);
	}*/
	
	private GestureDetector.OnGestureListener gesture=new GestureDetector.OnGestureListener(){
		private HashMap<Integer,Touch> touch=new HashMap<>();
		private boolean checkSV(float x,float y){
			return mSatValRect.left-SVPickerRadius<=x&&x<=mSatValRect.right+SVPickerRadius&&y>=mSatValRect.top-SVPickerRadius&&y<=mSatValRect.bottom+SVPickerRadius;
		}
		@Override
		public boolean onDown(MotionEvent p1)
		{
			Touch touchData=new Touch();
			if(checkSV(p1.getX(p1.getActionIndex()),p1.getY(p1.getActionIndex())))
				touchData.touchArea=Area.SV;
			else if(mHueRect.contains(p1.getX(p1.getActionIndex()),p1.getY(p1.getActionIndex())))
				touchData.touchArea=Area.H;
			else if(mAlphaRect.contains(p1.getX(p1.getActionIndex()),p1.getY(p1.getActionIndex())))
				touchData.touchArea=Area.A;
			else if(mCircleRect.contains(p1.getX(p1.getActionIndex()),p1.getY(p1.getActionIndex())))
				touchData.touchArea=Area.C;
				else
				touchData.touchArea=Area.N;
				touchData.touchId=p1.getPointerId(p1.getActionIndex());
				touch.put(p1.getPointerId(p1.getActionIndex()),touchData);
			return true;
		}

		@Override
		public void onShowPress(MotionEvent p1)
		{
			// TODO: Implement this method
		}

		@Override
		public boolean onSingleTapUp(MotionEvent event)
		{
			notifyChanged(event,true);
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent p1, MotionEvent event, float x, float y)
		{
			notifyChanged(event,false);
			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent p1)
		{
			int id=p1.getPointerId(p1.getActionIndex());
			touch.remove(id);
		}

		@Override
		public boolean onFling(MotionEvent p1, MotionEvent p2, float p3, float p4)
		{
			// TODO: Implement this method
			return false;
		}
		private void notifyChanged(MotionEvent event,boolean end){
			int id=event.getPointerId(event.getActionIndex());
			Touch touchData=touch.get(id);
			if(touchData==null){
				return;
			}
			switch(touchData.touchArea){
				case SV:
					float[] xy=pointToSatVal(event.getX(event.getActionIndex()),event.getY(event.getActionIndex()));
					mSat=xy[0];
					mVal=xy[1];
					invalidate(mSatValRect);
					break;
				case H:
					mHue=pointToHue(event.getY(event.getActionIndex()));
					invalidate(mHueRect);
					break;
				case A:
					alpha=pointToAlpha(event.getX(event.getActionIndex()));
					invalidate(mAlphaRect);
					break;
				case C:
					if(occl!=null&&end)
						occl.onColorChecked(Color.HSVToColor(alpha,new float[]{mHue,mSat,mVal}));
					break;
			}
		}
	};
	private void invalidate(RectF rectf){
		invalidate((int)rectf.left-1,(int)rectf.top-1,(int)rectf.right+1,(int)rectf.bottom+1);
	}
}

