package com.moe.utils;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtils
{
	public static  Bitmap scaleBitmap(Bitmap origin,float ratio){
		if(origin == null)return null;
		int width = origin.getWidth();
		int height = origin.getHeight();
		Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM= null;
		try{
		newBM=Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
		}catch(Exception e){}
		/*if(newBM.equals(origin)){
			return newBM;
        }
        origin.recycle();*/
		return newBM;
    }
}
