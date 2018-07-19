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
import com.moe.LiveVisualizer.internal.ImageThread;
import android.graphics.SweepGradient;

public abstract class RingDraw extends CircleDraw implements OnColorSizeChangedListener
{
	public final static int OUTSIDE=0;
	public final static int INSIDE=1;
	private int direction;
	private Paint paint;
	private float degress=0;
	private float radius,degress_step;//圆形半径
	private boolean cutCenterImage,antialias;
	private ImageDraw draw;
	private Shader shader;
	private Bitmap shaderBuffer;
	private int borderHeight,size;
	private float spaceWidth,borderWidth;
	
	public RingDraw(ImageDraw draw){
		super(draw);
		LiveWallpaper.WallpaperEngine engine=getEngine();
		this.draw=draw;
		borderHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, engine.getPreference().getInt("borderHeight", 100), engine.getContext().getResources().getDisplayMetrics());
		spaceWidth = engine.getPreference().getInt("spaceWidth", 20);
		direction=Integer.parseInt(engine.getPreference().getString("direction",OUTSIDE+""));
		paint = new Paint();
		//paint.setStrokeCap(getEngine().getPreference().getBoolean("round",true)?Paint.Cap.ROUND:Paint.Cap.SQUARE);
		paint.setColor(0xff39c5bb);
		borderWidth=(engine.getPreference().getInt("borderWidth",30));
		radius=engine.getPreference().getInt("circleRadius",Math.min(engine.getDisplayWidth(),engine.getDisplayHeight())/6);
		cutCenterImage=engine.getPreference().getBoolean("cutImage",true);
		degress_step=engine.getPreference().getInt("degress",10)/100f*10;
		engine.registerColorSizeChangedListener(this);
			onSizeChanged();
	}

	@Override
	public void setAntialias(boolean antialias)
	{
		this.antialias=antialias;
		//paint.setAntiAlias(antialias);
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
		try
		{
			size = (int)(2*getRadius()*Math.PI/spaceWidth);
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

	public Bitmap getShaderBuffer(int width,int height)
	{
		if(!isFinalized()&&shaderBuffer==null){
			if(shader==null)
			shader=new SweepGradient(getPointF().x, getPointF().y, getEngine().getColorList().toArray(), null);
			shaderBuffer= Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
			Canvas shaderCanvas=new Canvas(shaderBuffer);
			paint.reset();
			paint.setShader(getShader());
			shaderCanvas.drawRect(0, 0, shaderCanvas.getWidth(), shaderCanvas.getHeight(), paint);
			paint.reset();
		}
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

	@Override
	public void onDraw(Canvas canvas, int color_mode) throws NullPointerException
	{
		if(isFinalized())return;
		Paint paint=getPaint();
		paint.setStrokeCap(getRound());
		paint.setAntiAlias(antialias);
		paint.setDither(antialias);
		switch(color_mode){
			case 0:
				switch ( getEngine().getColorList().size() )
				{
					case 0:
						paint.setColor(0xff39c5bb);
						drawGraph(getFft(), canvas, color_mode,false);
						break;
					case 1:
						paint.setColor(getEngine().getColorList().get(0));
						drawGraph(getFft(), canvas, color_mode,false);
						break;
					default:
						final int layer=canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
						drawGraph(getFft(), canvas, color_mode,false);
						paint.reset();
						Bitmap buffer=getShaderBuffer(canvas.getWidth(),canvas.getHeight());
						if(buffer!=null&&!buffer.isRecycled()){
							//paint.setStyle(Paint.Style.FILL);
							paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
							canvas.drawBitmap(buffer, 0, 0, paint);
							//paint.setXfermode(null);
						}
						canvas.restoreToCount(layer);
						break;
				}
				break;
			case 1:
			case 2:
				if(getEngine()!=null)
				switch ( getEngine().getColorList().size() )
				{
					case 0:
						paint.setColor(0xff39c5bb);
						break;
					default:
						paint.setColor(getEngine().getColorList().get(0));
						break;
				}
			case 4:
				drawGraph(getFft(), canvas, color_mode,true);
				break;
			case 3:
				int color=getColor();
				paint.setColor(getEngine().getPreference().getBoolean("nenosync",false)?color:0xffffffff);
				paint.setShadowLayer(getBorderWidth(),0,0,color);
				drawGraph(getFft(), canvas, color_mode,false);
				paint.clearShadowLayer();
				break;
		}
		paint.reset();
	}

	
	public void drawCircleImage(Canvas canvas){
		final LiveWallpaper.WallpaperEngine engine=getEngine();
		if(engine==null)return;
		final ImageThread image=engine.getCircleImage();
		if(image==null)return;
		final Bitmap circle=image.getImage();
		if(circle==null||circle.isRecycled())return;
		final int layer=canvas.saveLayer(0,0,canvas.getWidth(),canvas.getHeight(),null,canvas.ALL_SAVE_FLAG);
		PointF point=getPointF();
		//canvas.drawColor(0xffffffff);
			if(engine.getPreference().getBoolean("circleSwitch",true)){
				degress+=degress_step;
				if ( degress >= 360 )degress = 0;
			}
			canvas.rotate(degress,point.x,point.y);
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
	public float getRadius(){
		return radius;
	}

	@Override
	public void finalized()
	{
		draw=null;
		onColorSizeChanged();
			if(getEngine()!=null)
				getEngine().unRegisterOnColorSizeChanged(this);
				paint.reset();
		super.finalized();

	}
	@Override
	public void onColorSizeChanged()
	{
		shader = null;
		if ( shaderBuffer != null )
			shaderBuffer.recycle();
		shaderBuffer = null;
	}

	@Override
	public void setOffsetX(int x)
	{
		// TODO: Implement this method
		super.setOffsetX(x);
		onColorSizeChanged();
	}

	@Override
	public void setOffsetY(int y)
	{
		// TODO: Implement this method
		super.setOffsetY(y);
		onColorSizeChanged();
	}
	
}
