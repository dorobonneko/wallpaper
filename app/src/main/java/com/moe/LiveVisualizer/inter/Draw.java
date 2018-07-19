package com.moe.LiveVisualizer.inter;
import android.graphics.Canvas;
import android.graphics.Shader;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Matrix;
import android.graphics.Paint;

public interface Draw
{

	boolean isFinalized();

	void finalized();

	void draw(Canvas canvas);
	void onDraw(Canvas canvas,int color_mode)throws NullPointerException;
	void drawGraph(byte[] buffer,Canvas canvas,int color_mode,boolean useMode)throws NullPointerException;
	byte[] getFft();
	byte[] getWave();
	//float getDownSpeed();
	
	LiveWallpaper.WallpaperEngine getEngine();
	void onBorderWidthChanged(int width);
	void onBorderHeightChanged(int height);
	void onSpaceWidthChanged(int space);
	void onDrawHeightChanged(float height);
	int size();
	int getColor();
	//void setShader(Shader shader);
	Shader getShader();
	void setRound(boolean round);
	//void setCutImage(boolean cut);
	//Matrix getCenterScale();
	void setOffsetX(int x);
	void setOffsetY(int y);
	void notifySizeChanged();
	float getInterpolator(float interpolator);
	void checkMode(int mode,Paint paint);
	void setAntialias(boolean antialias);
}
