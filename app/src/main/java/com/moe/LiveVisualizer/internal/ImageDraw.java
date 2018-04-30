package com.moe.LiveVisualizer.internal;
import android.content.SharedPreferences;
import com.moe.LiveVisualizer.utils.ColorList;
import android.graphics.Canvas;
import java.lang.ref.WeakReference;
import com.moe.LiveVisualizer.LiveWallpaper;

public abstract class ImageDraw
{
	private LiveWallpaper.WallpaperEngine engine;
	private byte[] buffer;
	private ImageDraw draw,line,chart,circle,pop_circle;
	protected ImageDraw(LiveWallpaper.WallpaperEngine engine){
		this.engine=engine;
	}
	ImageDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		this.draw=draw;
		this.engine=engine;
	}
	protected LiveWallpaper.WallpaperEngine getEngine(){
		return engine;
	}
	protected byte[] getBuffer(){
		return draw==null?this.buffer:draw.getBuffer();
	}
	final public ImageDraw lockData(byte[] buffer){
		if(buffer==null)return null;
		this.buffer=buffer;
		switch(engine.getSharedPreferences().getString("visualizer_mode","0")){
			case "0"://柱形图
			return line==null?line=new LineDraw(this,engine):line;
			case "1"://折线图
				return chart==null?chart=new LineChartDraw(this,engine):chart;
			case "2"://圆形射线
				return circle==null?circle=new CircleLineDraw(this,engine):circle;
			case "3"://弹弹圈
				return pop_circle==null?pop_circle=new PopCircleDraw(this,engine):pop_circle;
			case "4":
				//return WaveDraw.getInstance(this,engine);
		}
		return this;
	}
	public void draw(Canvas canvas){
		onDraw(canvas,Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode","0")));
	}
	public abstract void onDraw(Canvas canvas,int color_mode);
}
