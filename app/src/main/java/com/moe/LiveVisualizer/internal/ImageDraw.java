package com.moe.LiveVisualizer.internal;
import android.graphics.*;
import com.moe.LiveVisualizer.draw.*;
import com.moe.LiveVisualizer.draw.circle.*;
import com.moe.LiveVisualizer.draw.line.*;

import com.moe.LiveVisualizer.service.LiveWallpaper;
import com.moe.LiveVisualizer.inter.Draw;
import com.moe.LiveVisualizer.service.LiveWallpaper.WallpaperEngine;
import android.view.animation.AccelerateInterpolator;

public class ImageDraw implements OnColorSizeChangedListener
{
	private LiveWallpaper.WallpaperEngine engine;
	//private Draw[] drawList=new Draw[13];
	private Shader shader;
	private float downSpeed;
	private Matrix centerImageMatrix;
	private String mode,color_mode;
	private WallpaperThread wallpaper;
	private Draw draw;
	private boolean antialias;
	private boolean rotation;
	public ImageDraw(WallpaperThread wallpaper)
	{
		this.wallpaper=wallpaper;
		this.engine = wallpaper.getEngine();
		color_mode = engine.getPreference().getString("color_mode", "0");
		engine.registerColorSizeChangedListener(this);
		setMode(engine.getPreference().getString("visualizer_mode", "0"));
	}

	public void setVisualizerRotation(boolean rotation)
	{
		this.rotation=rotation;
		if(draw instanceof CircleDraw)
			((CircleDraw)draw).setVisualizerRotation(rotation);
	}
	public void setAnitialias(boolean antialias){
		this.antialias=antialias;
		if(draw!=null)draw.setAntialias(antialias);
	}
	public float getInterpolation(float input){
		//return value*value*getDownSpeed();
		if(Float.isInfinite(input))
			return 1;
		input=Math.abs(input);
		return (1f- ((1.0f - input) * (1.0f - input)))*getDownSpeed();
	}
	public LiveWallpaper.WallpaperEngine getEngine()
	{
		return engine;
	}
	public void setMode(String mode)
	{
		this.mode = mode;
		if(draw!=null)draw.finalized();
		draw=null;
	}
	public WallpaperThread getWallpaperThread()
	{
		return wallpaper;
	}
	public void setColorMode(String mode)
	{
		this.color_mode = mode;
	}
	public String getColorMode()
	{
		return color_mode;
	}
	public void notifySizeChanged()
	{
		shader = null;
		if(draw!=null)draw.notifySizeChanged();
		/*for (Draw draw:drawList)
			if (draw != null)
				draw.notifySizeChanged();*/
	}

	public void setDirection(int direction)
	{
		if(draw instanceof RingDraw)
			((RingDraw)draw).setDirection(direction);
		/*for (Draw draw:drawList)
			if (draw instanceof RingDraw)
				((RingDraw)draw).setDirection(direction);*/
	}
	public void setCircleRadius(int radius)
	{
		if(draw instanceof RingDraw)
			((RingDraw)draw).setRadius(radius);
		/*for (Draw draw:drawList)
			if (draw instanceof RingDraw)
				((RingDraw)draw).setRadius(radius);*/
	}
	public void setDegressStep(float step)
	{
		if(draw instanceof RingDraw)
			((RingDraw)draw).setDegressStep(step);
		/*for (Draw draw:drawList)
			if (draw instanceof RingDraw)
				((RingDraw)draw).setDegressStep(step);*/
	}
	public void setOffsetY(int y)
	{
		if(draw!=null)
			draw.setOffsetY(y);
		/*for (Draw draw:drawList)
			if (draw != null)
				draw.setOffsetY(y);*/
	}

	public void setOffsetX(int x)
	{
		if(draw!=null)
			draw.setOffsetX(x);
		/*for (Draw draw:drawList)
			if (draw != null)
				draw.setOffsetX(x);*/
	}
	public void setCenterScale(boolean scale)
	{
		if (scale)
			centerImageMatrix = new Matrix();
		else
			centerImageMatrix = null;
	}
	public Matrix getCenterScale()
	{
		return centerImageMatrix;
	}
	public void setCutImage(boolean cut)
	{
		if(draw instanceof RingDraw)
			((RingDraw)draw).setCutImage(cut);
		/*for (Draw draw:drawList)
			if (draw instanceof RingDraw)
				((RingDraw)draw).setCutImage(cut);*/

	}

	public void setRound(boolean round)
	{
		if(draw!=null)
			draw.setRound(round);
		/*for (Draw draw:drawList)
			if (draw != null)
				draw.setRound(round);*/
	}

	public void setDownSpeed(int speed)
	{
		downSpeed =speed/50f;
	}
	private float getDownSpeed()
	{
		return downSpeed;
	}
	public void setDrawHeight(float height)
	{
		if(draw!=null)
			draw.onDrawHeightChanged(height);
		/*for (Draw draw:drawList)
			if (draw != null)
				draw.onDrawHeightChanged(height);*/
	}
	public void setBorderHeight(int height)
	{
		if(draw!=null)
			draw.onBorderHeightChanged(height);
		/*for (Draw draw:drawList)
			if (draw != null)
				draw.onBorderHeightChanged(height);*/
	}
	public void setSpaceWidth(int space)
	{
		if(draw!=null)
			draw.onSpaceWidthChanged(space);
		/*for (Draw draw:drawList)
			if (draw != null)
				draw.onSpaceWidthChanged(space);*/
	}
	public void setBorderWidth(int width)
	{
		if(draw!=null)
			draw.onBorderWidthChanged(width);
		/*for (Draw draw:drawList)
			if (draw != null)
				draw.onBorderWidthChanged(width);*/
				}
	@Override
	public void onColorSizeChanged()
	{
		shader = null;
	}
	public byte[] getWave()
	{
		
		return wallpaper.getWave();
	}
	public byte[] getFft()
	{
		return wallpaper.getFft();
	}

	final public Draw lockData()
	{
		if(draw==null){
			get();
			if(draw!=null)
				draw.setAntialias(antialias);
				if(draw instanceof CircleDraw)
					((CircleDraw)draw).setVisualizerRotation(rotation);
			}
		return draw;
		
	}

	private Draw get()
	{
		switch (mode)
		{
			case "0"://柱形图
				return draw=new RadialDraw(this);
			case "1"://折线图
				return draw= new LineChartDraw(this);
			case "2"://圆形射线
				return draw = new CircleRadialDraw(this);
			case "3"://弹弹圈
				return draw = new PopCircleDraw(this);
			case "4"://圆环三角
				return draw = new CircleTriangleDraw(this);
			case "5"://射线
				return draw = new CenterRadialDraw(this);
			case "6"://波纹
				return draw = new RippleDraw(this);
			case "7"://离散
				return draw = new CircleDisperseDraw(this);
			case "8"://山坡线
				return draw = new YamaLineDraw(this);
			case "9"://方块
				return draw = new SquareDraw(this);
			case "10"://打砖块
				return draw = new BlockBreakerDraw(this);
			case "11":
				return draw = new UnKnow1(this);
			case "12":
			return draw = new UnKnow2(this);
			case "13":
				return draw=new UnKnow3(this);
			case "14"://心形
				return draw=new HeartDraw(this);
		}
		return null;
		/*switch (mode)
		{
			case "0"://柱形图
				return drawList[0] == null ?drawList[0] = new RadialDraw(this): drawList[0];
			case "1"://折线图
				return drawList[1] == null ?drawList[1] = new LineChartDraw(this): drawList[1];
			case "2"://圆形射线
				return drawList[2] == null ?drawList[2] = new CircleRadialDraw(this): drawList[2];
			case "3"://弹弹圈
				return drawList[3] == null ?drawList[3] = new PopCircleDraw(this): drawList[3];
			case "4"://圆环三角
				return drawList[4] == null ?drawList[4] = new CircleTriangleDraw(this): drawList[4];
			case "5"://射线
				return drawList[5] == null ?drawList[5] = new CenterRadialDraw(this): drawList[5];
			case "6"://波纹
				return drawList[6] == null ?drawList[6] = new RippleDraw(this): drawList[6];
			case "7"://离散
				return drawList[7] == null ?drawList[7] = new CircleDisperseDraw(this): drawList[7];
			case "8"://山坡线
				return drawList[8] == null ?drawList[8] = new YamaLineDraw(this): drawList[8];
			case "9"://方块
				return drawList[9] == null ?drawList[9] = new SquareDraw(this): drawList[9];
			case "10"://打砖块
				return drawList[10] == null ?drawList[10] = new BlockBreakerDraw(this): drawList[10];
			case "11":
				return drawList[11] == null ?drawList[11] = new UnKnow1(this): drawList[11];
			case "12":
				return drawList[12] == null ?drawList[12] = new UnKnow2(this): drawList[12];
		}
		return null;*/
	}
	public void resetShader()
	{
		this.shader = null;
	}
	public synchronized Shader getShader()
	{
		if (shader == null)
		{
			switch (engine.getPreference().getString("color_direction", "0"))
			{
				case "0"://lefttoright
					shader = new LinearGradient(0, 0, engine.getDisplayWidth(), 0, engine.getColorList().toArray(), null, LinearGradient.TileMode.CLAMP);
					break;//righttoleft
				case "1":
					shader = new LinearGradient(engine.getDisplayWidth(), 0, 0, 0, engine.getColorList().toArray(), null, LinearGradient.TileMode.CLAMP);

					break;//toptobottom
				case "2":shader = new LinearGradient(0, 0, 0, engine.getDisplayHeight(), engine.getColorList().toArray(), null, LinearGradient.TileMode.CLAMP);

					break;//bottomtotop
				case "3":shader = new LinearGradient(0, engine.getDisplayHeight(), 0, 0, engine.getColorList().toArray(), null, LinearGradient.TileMode.CLAMP);

					break;//toplefttobottomright
				case "4":shader = new LinearGradient(0, 0, engine.getDisplayWidth(), engine.getDisplayHeight(), engine.getColorList().toArray(), null, LinearGradient.TileMode.CLAMP);

					break;//toprighttobottomleft
				case "5":shader = new LinearGradient(engine.getDisplayWidth(), 0, 0, engine.getDisplayHeight(), engine.getColorList().toArray(), null, LinearGradient.TileMode.CLAMP);

					break;//bottomlefttotopright
				case "6":shader = new LinearGradient(0, engine.getDisplayHeight(), engine.getDisplayWidth(), 0, engine.getColorList().toArray(), null, LinearGradient.TileMode.CLAMP);

					break;//bottomrighttotopright
				case "7":shader = new LinearGradient(engine.getDisplayWidth(), engine.getDisplayHeight(), 0, 0, engine.getColorList().toArray(), null, LinearGradient.TileMode.CLAMP);

					break;
			}
		}
		return shader;
	}
}
