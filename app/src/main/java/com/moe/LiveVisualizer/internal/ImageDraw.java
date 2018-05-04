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
import com.moe.LiveVisualizer.draw.CircleInsideDraw;

public class ImageDraw implements OnColorSizeChangedListener
{
	private LiveWallpaper.WallpaperEngine engine;
	private double[] fft;
	private Draw line,chart,circle,pop_circle,circle_inside;
	private Shader shader,fade;
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
	public void setDrawHeight(float height){
		if(line!=null)
			line.onDrawHeightChanged(height);
		if(chart!=null)
			chart.onDrawHeightChanged(height);
		if(circle!=null)
			circle.onDrawHeightChanged(height);
		if(pop_circle!=null)
			pop_circle.onDrawHeightChanged(height);
		
	}
	public void setBorderHeight(int height){
		if(line!=null)
			line.onBorderHeightChanged(height);
		if(chart!=null)
			chart.onBorderHeightChanged(height);
		if(circle!=null)
			circle.onBorderHeightChanged(height);
		if(pop_circle!=null)
			pop_circle.onBorderHeightChanged(height);
		if(circle_inside!=null)
			circle_inside.onBorderHeightChanged(height);
	}
	public void setSpaceWidth(int space){
		if(line!=null)
			line.onSpaceWidthChanged(space);
		if(chart!=null)
			chart.onSpaceWidthChanged(space);
		if(circle!=null)
			circle.onSpaceWidthChanged(space);
		if(pop_circle!=null)
			pop_circle.onSpaceWidthChanged(space);
		if(circle_inside!=null)
			circle_inside.onSpaceWidthChanged(space);
	}
	public void setBorderWidth(int width){
		if(line!=null)
			line.onBorderWidthChanged(width);
		if(chart!=null)
			chart.onBorderWidthChanged(width);
		if(circle!=null)
			circle.onBorderWidthChanged(width);
		if(pop_circle!=null)
		pop_circle.onBorderWidthChanged(width);
		if(circle_inside!=null)
			circle_inside.onBorderWidthChanged(width);
	}
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
				return line==null?line=new RadialDraw(this,engine):line;
			case "1"://折线图
				return chart==null?chart=new LineChartDraw(this,engine):chart;
			case "2"://圆形射线
				return circle==null?circle=new CircleLineDraw(this,engine):circle;
			case "3"://弹弹圈
				return pop_circle==null?pop_circle=new PopCircleDraw(this,engine):pop_circle;
			case "4"://
				return circle_inside==null?circle_inside=new CircleInsideDraw(this,engine):circle_inside;
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
