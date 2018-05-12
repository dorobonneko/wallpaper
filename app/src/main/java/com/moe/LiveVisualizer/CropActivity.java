package com.moe.LiveVisualizer;
import android.app.Activity;
import android.os.Bundle;
import com.moe.LiveVisualizer.widget.CropView;
import android.content.Intent;
import android.net.Uri;
import android.graphics.BitmapFactory;
import java.io.FileNotFoundException;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Bitmap;
import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;
import java.io.OutputStream;
import java.io.IOException;
import android.graphics.Matrix;

public class CropActivity extends Activity implements CropView.CropCallback
{
	private CropView crop;
	private MenuItem cropItem;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setTitle("裁剪图片");
		crop=new CropView(this);
		setContentView(crop);
		/*
		 intent.putExtra("aspectX", display.getWidth());
		 intent.putExtra("aspectY", display.getHeight());
		 intent.putExtra("outputX",display.getWidth());
		 intent.putExtra("outputY",display.getHeight());
		 intent.putExtra("output", Uri.fromFile(wallpaper));
		 intent.putExtra("return-data",false);
		 intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());*/
		Intent intent=getIntent();
		Uri uri=intent.getData();
		try
		{
			crop.setImage(BitmapFactory.decodeStream(getContentResolver().openInputStream(uri)));
		}
		catch (Exception e)
		{
			Toast.makeText(getApplicationContext(),"无法读取文件",Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		crop.setCrop(intent.getIntExtra("aspectX",0),intent.getIntExtra("aspectY",0),intent.getBooleanExtra("two",false));
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},342);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		// TODO: Implement this method
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode==342){
			if(grantResults[0]!=PackageManager.PERMISSION_GRANTED)
				finish();
				}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		cropItem=menu.add(0,0,0,"裁剪");
		cropItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case android.R.id.home:
				finish();
				break;
			case 0:
				if(item.getTitle().length()>2)
					break;
				item.setTitle("正在裁剪");
				crop.crop(this);
				break;
		}
		return true;
	}

	@Override
	public void success(Bitmap b1, Bitmap b2)
	{
		if(b1!=null||b2!=null)
			if(getIntent().getBooleanExtra("return-data",false)){
				Intent intent=new Intent();
				intent.putExtra(Intent.EXTRA_STREAM,b1);
				setResult(RESULT_OK,intent);
				finish();
				return;
			}
			int outputX=getIntent().getIntExtra("outputX",-1);
			int outputY=getIntent().getIntExtra("outputY",-1);
			b1=scale(b1,outputX,outputY);
			if( b1!=null&&getIntent().getParcelableExtra("output")!=null){
				OutputStream os=null;
				try
				{
					os=getContentResolver().openOutputStream((Uri)getIntent().getParcelableExtra("output"));
				
				b1.compress(Bitmap.CompressFormat.valueOf((getIntent().getStringExtra("outputFormat")==null?Bitmap.CompressFormat.PNG.toString():getIntent().getStringExtra("outputFormat"))),100,os);
				os.flush();
				}
				catch (Exception e)
				{}finally{
					try
					{
						if ( os != null )os.close();
					}
					catch (IOException e)
					{}
				}
			}
			b2=scale(b2,outputY,outputX);
		if( b2!=null&&getIntent().getParcelableExtra("output2")!=null){
			OutputStream os=null;
			try
			{
				os=getContentResolver().openOutputStream((Uri)getIntent().getParcelableExtra("output2"));

				b2.compress(Bitmap.CompressFormat.valueOf((getIntent().getStringExtra("outputFormat")==null?Bitmap.CompressFormat.PNG.toString():getIntent().getStringExtra("outputFormat"))),100,os);
				os.flush();
			}
			catch (Exception e)
			{}finally{
				try
				{
					if ( os != null )os.close();
				}
				catch (IOException e)
				{}
			}
		}
			setResult(RESULT_OK);
				finish();
	}

	private Bitmap scale(Bitmap src,float width,float height){
		if(src==null)return null;
		if(width<1||height<1)return src;
		Matrix matrix=new Matrix();
		matrix.setScale(width/src.getWidth(),height/src.getHeight());
		Bitmap bit=Bitmap.createBitmap(src,0,0,src.getWidth(),src.getHeight(),matrix,false);
		if(bit!=src)
			src.recycle();
			return bit;
	}
}
