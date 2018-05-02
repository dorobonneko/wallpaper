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
import com.moe.LiveVisualizer.draw.CircleLineDraw;
import com.moe.LiveVisualizer.inter.Draw;

public class ImageDraw implements OnColorSizeChangedListener
{
	private LiveWallpaper.WallpaperEngine engine;
	private double[] fft;
	private Draw line,chart,circle,pop_circle;
	private Shader shader;
	private float downSpeed;
	public ImageDraw(LiveWallpaper.WallpaperEngine engine){
		this.engine=engine;
		engine.registerColorSizeChangedListener(this);
	}

	public void setDownSpeed(int speed)
	{
		downSpeed=speed/100.0f;
	}
	public float getDownSpeed(){
		return downSpeed;
	}

	@Override
	public void onColorSizeChanged()
	{
		setFade(null);
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
			this.shader=shader;
	}
	final public Shader getFade(){
		return shader;
	}
	private Draw get(){
		switch(engine.getSharedPreferences().getString("visualizer_mode","0")){
			case "0"://柱形图
				return line==null?line=new RadialDraw(this,engine):line;
			case "1"://折线图
				return chart==null?chart=new LineChartDraw(this,engine):chart;
			case "2"://圆形射线
				return circle==null?circle=new CircleLineDraw(this,engine):circle;
			case "3"://弹弹圈
				return pop_circle==null?pop_circle=new PopCircleDraw(this,engine):pop_circle;
			case "4":
				//return WaveDraw.getInstance(this,engine);
		}
		return null;
	}
	
}
