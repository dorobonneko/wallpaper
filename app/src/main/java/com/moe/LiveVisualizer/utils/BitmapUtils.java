package com.moe.LiveVisualizer.utils;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.renderscript.RenderScript;
import android.renderscript.Allocation;
import android.renderscript.ScriptIntrinsicBlur;
import android.content.Context;
import android.renderscript.Element;

public class BitmapUtils
{
	public static Bitmap scale(Bitmap bit,float scaleX,float scaleY){
		if(bit==null)return null;
		Matrix matrix=new Matrix();
		matrix.postScale(scaleX,scaleY);
		Bitmap buffer=Bitmap.createBitmap(bit,0,0,bit.getWidth(),bit.getHeight(),matrix,true);
		return buffer;
	}
	public static Bitmap scaleWithSize(Bitmap bit,int width,int height){
		if(bit==null)return null;
		float scaleX=((float)width)/bit.getWidth();
		float scaleY=((float)height)/bit.getHeight();
		if(scaleX==1&&scaleY==1)return bit;
		return scale(bit,scaleX,scaleY);
	}
	public static Bitmap blur(Context context,Bitmap bit){
		if(bit==null)return null;
		RenderScript rs=RenderScript.create(context);
		Bitmap blurredBitmap =BitmapUtils.scaleWithSize(bit,bit.getWidth()/4,bit.getHeight()/4);

		final Allocation input = Allocation.createFromBitmap(rs,blurredBitmap,Allocation.MipmapControl.MIPMAP_FULL,Allocation.USAGE_SHARED);
		final Allocation output = Allocation.createTyped(rs,input.getType());
		//(3)
		// Load up an instance of the specific script that we want to use.
		ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs,Element.U8_4(rs));
		//(4)
		scriptIntrinsicBlur.setInput(input);
		//(5)
		// Set the blur radius
		scriptIntrinsicBlur.setRadius(25);
		//(6)
		// Start the ScriptIntrinisicBlur
		scriptIntrinsicBlur.forEach(output);
		//(7)
		// Copy the output to the blurred bitmap
		output.copyTo(blurredBitmap);
		//(8)
		rs.destroy();
		return blurredBitmap;
	}
}
