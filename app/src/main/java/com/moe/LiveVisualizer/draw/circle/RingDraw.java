package com.moe.LiveVisualizer.draw.circle;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import com.moe.LiveVisualizer.internal.OnColorSizeChangedListener;
import android.graphics.Shader;
import android.util.TypedValue;
import com.moe.LiveVisualizer.draw.CircleDraw;

public abstract class RingDraw extends CircleDraw
{
	public final static int OUTSIDE=0;
	public final static int INSIDE=1;
	private int direction;
	private Paint paint;
	private float degress=0;
	private float radius,degress_step;//圆形半径
	private boolean cutCenterImage;
	private ImageDraw draw;
	private Shader shader;
	private Bitmap shaderBuffer;
	private int borderHeight,size;
	private float spaceWidth,borderWidth;
	
	public RingDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		super(draw,engine);
		this.draw=draw;
		borderHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, engine.getPreference().getInt("borderHeight", 100), engine.getContext().getResources().getDisplayMetrics());
		spaceWidth = engine.getPreference().getInt("spaceWidth", 20);
		direction=Integer.parseInt(engine.getPreference().getString("direction",OUTSIDE+""));
		paint = new Paint();
		paint.setStrokeCap(getEngine().getPreference().getBoolean("round",true)?Paint.Cap.ROUND:Paint.Cap.SQUARE);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(0xff39c5bb);
		paint.setStyle(Paint.Style.FILL);
		borderWidth=(engine.getPreference().getInt("borderWidth",30));
		radius=engine.getPreference().getInt("circleRadius",Math.min(engine.getDisplayWidth(),engine.getDisplayHeight())/6);
		cutCenterImage=engine.getPreference().getBoolean("cutImage",true);
		degress_step=engine.getPreference().getInt("degress",10)/100f*10;
		engine.registerColorSizeChangedListener(new OnColorSizeChangedListener(){

				@Override
				public void onColorSizeChanged()
				{
					shader = null;
					if ( shaderBuffer != null )
						shaderBuffer.recycle();
					shaderBuffer = null;
				}
			});
			onSizeChanged();
	}
	public float getBorderHeight(){
		return borderHeight;
	}
	public float getSpaceWidth(){
		return spaceWidth;
	}
	@Override
	public void onBorderHeightChanged(int height)
	{
		borderHeight = height;
	}

	@Override
	public void notifySizeChanged()
	{
		super.notifySizeChanged();
		shader=null;
		if(shaderBuffer!=null)
			shaderBuffer.recycle();
			shaderBuffer=null;
			onSizeChanged();
	}



	@Override
	public void onSpaceWidthChanged(int space)
	{
		spaceWidth = space;
		onSizeChanged();
	}

	@Override
	public int size()
	{
		return size;
	}
	public void onSizeChanged()
	{
		final double length=Math.min(getEngine().getDisplayWidth(),getEngine().getDisplayHeight()) / 3 * Math.PI;
		try
		{
			size = (int)((length - spaceWidth) / (borderWidth + spaceWidth));
		}
		catch (Exception e)
		{}
		try
		{
			size = size > getEngine().getFftSize() ?getEngine().getFftSize(): size;
		}
		catch (Exception e)
		{}

	}

	@Override
	public void onBorderWidthChanged(int width)
	{
		this.borderWidth=width;
		onSizeChanged();
	}
	public float getBorderWidth(){
		return borderWidth;
	}
	public void setShader(Shader shader)
	{
		this.shader = shader;
	}

	public Shader getShader()
	{
		return shader;
	}

	public void setShaderBuffer(Bitmap shaderBuffer)
	{
		this.shaderBuffer = shaderBuffer;
	}

	public Bitmap getShaderBuffer()
	{
		return shaderBuffer;
	}
	public Matrix getCenterScale()
	{
		return draw.getCenterScale();
	}
	
	public void setDirection(int direction){
		this.direction=direction;
	}
	public int getDirection(){
		return direction;
	}
	public void setRadius(int radius)
	{
		this.radius=radius;
	}
	public void setDegressStep(float step){
		degress_step=step;
	}
	
	final public void setCutImage(boolean cut)
	{
		cutCenterImage=cut;
	}
	final public Paint getPaint(){
		return paint;
	}
	public void drawCircleImage(Canvas canvas){
		PointF point=getPointF();
		if(getEngine().getCircleImage()!=null){
			final int layer=canvas.saveLayer(0,0,canvas.getWidth(),canvas.getHeight(),null,canvas.ALL_SAVE_FLAG);
			//canvas.drawColor(0xffffffff);
			if(getEngine().getPreference().getBoolean("circleSwitch",true)){
				canvas.rotate(degress,point.x,point.y);
				degress+=degress_step;
				if ( degress >= 360 )degress = 0;
			}
			final Bitmap circle=getEngine().getCircleImage();
			if(getCenterScale()!=null){
				Matrix matrix=getCenterScale();
				float scale=Math.max(radius*2/circle.getWidth(),radius*2/circle.getHeight());
				matrix.setScale(scale,scale);
				matrix.postTranslate(point.x-circle.getWidth()*scale/2,point.y-circle.getHeight()*scale/2);
			}
			if(cutCenterImage){
				canvas.drawCircle(point.x,point.y, radius, paint);
				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
				if(getCenterScale()!=null)
					canvas.drawBitmap(circle,getCenterScale(),paint);
				else
					canvas.drawBitmap(circle,point.x-circle.getWidth()/2,point.y-circle.getHeight()/2, paint);
				paint.setXfermode(null);
			}else{
				if(getCenterScale()!=null)
					canvas.drawBitmap(circle,getCenterScale(),paint);
				else
					canvas.drawBitmap(circle,point.x-circle.getWidth()/2,point.y-circle.getHeight()/2, paint);
			}
			canvas.restoreToCount(layer);

		}
	}
	public float getRadius(){
		return radius;
	}
}
