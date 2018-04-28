package com.moe.LiveVisualizer;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.Preference;
import android.content.Intent;
import android.app.Activity;
import android.net.Uri;
import java.io.File;
import android.view.Display;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.graphics.Bitmap;

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,Preference.OnPreferenceChangeListener
{
	private final static int IMAGE_CROP=0X02;
	private final static int CIRCLE=0x03;
	private AlertDialog delete;
	private AlertDialog circle_delete;
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName("moe");
		addPreferencesFromResource(R.xml.setting);
		findPreference("background").setOnPreferenceClickListener(this);
		//findPreference("artwork").setEnabled(Build.VERSION.SDK_INT>18);
		findPreference("color_mode").setOnPreferenceChangeListener(this);
		findPreference("circle_image").setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference p1, Object p2)
	{
		switch(p1.getKey()){
			case "color_mode":
				if(p2.equals("3"))return Build.VERSION.SDK_INT>18;
		} 
		return true;
	}


	@Override
	public boolean onPreferenceClick(Preference p1)
	{
		switch ( p1.getKey() )
		{
			case "background":
				/*Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
				 intent.setType("image/*");
				 startActivityForResult(intent,GET_IMAGE);*/
				final File wallpaper=new File(getActivity().getExternalCacheDir(), "wallpaper");
				if ( wallpaper.exists() )
				{
					if ( delete == null )
					{
						delete = new AlertDialog.Builder(getActivity()).setTitle("确认").setMessage("是否清除当前背景？").setPositiveButton("取消", null).setNegativeButton("确定", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									wallpaper.delete();
									getActivity().sendBroadcast(new Intent("wallpaper_changed"));
									
								}
						}).create();
				}
				delete.show();
		}else{
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			intent.putExtra("crop", "true");
			//width:height
			Display display=getActivity().getWindowManager().getDefaultDisplay();
			intent.putExtra("aspectX", display.getWidth());
			intent.putExtra("aspectY", display.getHeight());
			intent.putExtra("outputX",display.getWidth());
			intent.putExtra("outputY",display.getHeight());
			intent.putExtra("output", Uri.fromFile(wallpaper));
			intent.putExtra("return-data",false);
			intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
			startActivityForResult(Intent.createChooser(intent, "Choose Image"), IMAGE_CROP);
		}
		break;
		case "circle_image":
				final File circle=new File(getActivity().getExternalCacheDir(), "circle");
				if ( circle.exists() )
				{
					if ( circle_delete == null )
					{
						circle_delete = new AlertDialog.Builder(getActivity()).setTitle("确认").setMessage("是否清除当前图片？").setPositiveButton("取消", null).setNegativeButton("确定", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									circle.delete();
									getActivity().sendBroadcast(new Intent("circle_changed"));

								}
							}).create();
					}
					circle_delete.show();
				}else{
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					intent.putExtra("crop", "true");
					//width:height
					Display display=getActivity().getWindowManager().getDefaultDisplay();
					intent.putExtra("aspectX", 1);
					intent.putExtra("aspectY", 1);
					intent.putExtra("outputX",display.getWidth()/3);
					intent.putExtra("outputY",display.getWidth()/3);
					intent.putExtra("output", Uri.fromFile(circle));
					intent.putExtra("return-data",false);
					intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
					startActivityForResult(Intent.createChooser(intent, "Choose Image"), CIRCLE);
				}
			break;
	}
	return false;
}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode==Activity.RESULT_OK)
			switch(requestCode){
				case IMAGE_CROP:
					getActivity().sendBroadcast(new Intent("wallpaper_changed"));
					break;
				case CIRCLE:
					getActivity().sendBroadcast(new Intent("circle_changed"));
					
					break;
			}
		}

	
}
