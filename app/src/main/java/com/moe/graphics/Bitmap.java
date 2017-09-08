package com.moe.graphics;

public class Bitmap{
	private boolean recycler;
	private android.graphics.Bitmap bitmap;

	public void setBitmap(android.graphics.Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}

	public android.graphics.Bitmap getBitmap()
	{
		return bitmap;
	}
	public void setCanRecycle(boolean can){
		recycler=can;
	}
	public boolean canRecycler(){
		return recycler;
	}
}
