package com.moe.LiveVisualizer.duang;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import java.util.ArrayList;
import java.util.List;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.app.Service;
import android.graphics.Canvas;
import java.util.Iterator;

public class Engine
{
	private int wind,speed,maxSize,minSize;
	private List<Duang> list;
	private LiveWallpaper.WallpaperEngine engine;
	private DisplayMetrics display=new DisplayMetrics();
	
	private Engine(LiveWallpaper.WallpaperEngine engine){
		this.engine=engine;
		wind=engine.getSharedPreferences().getInt("duang_wind",2);
		speed=engine.getSharedPreferences().getInt("duang_speed",30);
		maxSize=engine.getSharedPreferences().getInt("duang_maxSize",50);
		minSize=engine.getSharedPreferences().getInt("duang_minSize",10);
		changed();
		list=new ArrayList<>();
		int size=engine.getSharedPreferences().getInt("duang_size",50);
		for(int i=0;i<size;i++)
		list.add(new Snow(display,maxSize,minSize,wind,speed));
	}
	public static Engine init(LiveWallpaper.WallpaperEngine engine){
		return new Engine(engine);
	}
	public void draw(Canvas canvas){
		Iterator<Duang> iterator=list.iterator();
		while(iterator.hasNext())
			iterator.next().draw(canvas);
	}
	public void reset(){
		Iterator<Duang> iterator=list.iterator();
		while(iterator.hasNext())
			iterator.next().reset(false);
	}
	public void changed(){
		((WindowManager)engine.getContext().getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(display);
	}
	public void setSizeChanged(int size){
		if(size>list.size()){
			for(int i=list.size();i<size;i++){
				list.add(new Snow(display,maxSize,minSize,wind,speed));
			}
		}else{
			for(;size<list.size();size++)
			list.remove(0);
		}
	}
	public void setMaxSize(int size){
		Iterator<Duang> iterator=list.iterator();
		while(iterator.hasNext())
			iterator.next().setMaxSize(size);
	}
	public void setMinSize(int size){
		Iterator<Duang> iterator=list.iterator();
		while(iterator.hasNext())
			iterator.next().setMinSize(size);
	}
	public void setMaxSpeed(int speed){
		this.speed=speed;
		Iterator<Duang> iterator=list.iterator();
		while(iterator.hasNext())
			iterator.next().setSpeed(speed);
	}
	public void setWind(int wind){
		this.wind=wind;
		Iterator<Duang> iterator=list.iterator();
		while(iterator.hasNext())
			iterator.next().setWind(wind);
	}
}
