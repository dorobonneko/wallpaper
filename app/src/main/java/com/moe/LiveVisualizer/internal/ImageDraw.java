package com.moe.LiveVisualizer.internal;
import android.content.SharedPreferences;
import com.moe.LiveVisualizer.utils.ColorList;
import android.graphics.Canvas;
import java.lang.ref.WeakReference;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Shader;
import com.moe.LiveVisualizer.draw.PopCircleDraw;
import com.moe.LiveVisualizer.draw.RadialDraw;
import com.moe.LiveVisualizer.draw.LineChartDraw;
import com.moe.LiveVisualizer.draw.CircleRadialDraw;
import com.moe.LiveVisualizer.inter.Draw;
import android.graphics.Matrix;
import com.moe.LiveVisualizer.draw.CenterRadialDraw;
import com.moe.LiveVisualizer.draw.RippleDraw;
import com.moe.LiveVisualizer.draw.CircleDraw;
import com.moe.LiveVisualizer.draw.RingDraw;
import com.moe.LiveVisualizer.draw.CircleTriangleDraw;

public class ImageDraw implements OnColorSizeChangedListener
{
	private LiveWallpaper.WallpaperEngine engine;
	private double[] fft;
	private Draw[] drawList=new Draw[7];
	private Shader shader,fade;
	private float downSpeed;
	private Matrix centerImageMatrix;
	public ImageDraw(LiveWallpaper.WallpaperEngine engine){
		this.engine=engine;
		engine.registerColorSizeChangedListener(this);
	}

	public void notifySizeChanged()
	{
		shader=null;
		fade=null;
		for(Draw draw:drawList)
			if(draw!=null)
				draw.notifySizeChanged();
	}

	public void setDirection(int direction)
	{
		for(Draw draw:drawList)
			if(draw instanceof RingDraw)
				((RingDraw)draw).setDirection(direction);
	}
	public void setCircleRadius(int radius){
		for(Draw draw:drawList)
		if(draw instanceof RingDraw)
		((RingDraw)draw).setRadius(radius);
	}
	public void setDegressStep(float step){
		for(Draw draw:drawList)
		if(draw instanceof RingDraw)
		((RingDraw)draw).setDegressStep(step);
	}
	public void setOffsetY(int y)
	{
		for(Draw draw:drawList)
			if(draw!=null)
				draw.setOffsetY(y);
	}

	public void setOffsetX(int x)
	{
		for(Draw draw:drawList)
			if(draw!=null)
				draw.setOffsetX(x);
	}
	public void setCenterScale(boolean scale){
		if(scale)
			centerImageMatrix=new Matrix();
			else
			centerImageMatrix=null;
	}
	public Matrix getCenterScale(){
		return centerImageMatrix;
	}
	public void setCutImage(boolean cut)
	{
		for(Draw draw:drawList)
		if(draw instanceof RingDraw)
		((RingDraw)draw).setCutImage(cut);
		
	}

	public void setRound(boolean round)
	{
		for(Draw draw:drawList)
			if(draw!=null)
				draw.setRound(round);
	}

	public void setDownSpeed(int speed)
	{
		downSpeed=speed/50.0f;
	}
	public float getDownSpeed(){
		return downSpeed;
	}
	public void setDrawHeight(float height){
		for(Draw draw:drawList)
			if(draw!=null)
				draw.onDrawHeightChanged(height);
	}
	public void setBorderHeight(int height){
		for(Draw draw:drawList)
			if(draw!=null)
				draw.onBorderHeightChanged(height);
				}
	public void setSpaceWidth(int space){
		for(Draw draw:drawList)
			if(draw!=null)
				draw.onSpaceWidthChanged(space);
				}
	public void setBorderWidth(int width){
		for(Draw draw:drawList)
			if(draw!=null)
				draw.onBorderWidthChanged(width);}
	@Override
	public void onColorSizeChanged()
	{
		fade=null;
		shader=null;
	}
	public double[] getFft(){
		return this.fft;
	}
	/*protected byte[] getBuffer(){
	 return draw==null?this.buffer:draw.getBuffer();
	 }*/
	final public Draw lockData(double[] fft){
		if(fft==null)return null;
		this.fft=fft;
		return get();
	}
	/*final public Draw lockData(byte[] buffer){
		if(buffer==null)return null;
		this.buffer=buffer;
		return get();
	}*/
	final public void setFade(Shader shader){
			this.fade=shader;
	}
	final public Shader getFade(){
		return fade;
	}
	private Draw get(){
		switch(engine.getSharedPreferences().getString("visualizer_mode","0")){
			case "0"://柱形图
				return drawList[0]==null?drawList[0]=new RadialDraw(this,engine):drawList[0];
			case "1"://折线图
				return drawList[1]==null?drawList[1]=new LineChartDraw(this,engine):drawList[1];
			case "2"://圆形射线
				return drawList[2]==null?drawList[2]=new CircleRadialDraw(this,engine):drawList[2];
			case "3"://弹弹圈
				return drawList[3]==null?drawList[3]=new PopCircleDraw(this,engine):drawList[3];
			case "4"://圆环三角
				return drawList[4]==null?drawList[4]=new CircleTriangleDraw(this,engine):drawList[4];
			case "5"://射线
				return drawList[5]==null?drawList[5]=new CenterRadialDraw(this,engine):drawList[5];
			case "6"://波纹
				return drawList[6]==null?drawList[6]=new RippleDraw(this,engine):drawList[6];
		}
		return null;
	}
	public void setShader(Shader shader){
		this.shader=shader;
	}
	public Shader getShader(){
		return shader;
	}
}
