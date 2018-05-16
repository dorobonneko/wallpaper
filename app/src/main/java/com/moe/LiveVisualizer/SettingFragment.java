package com.moe.LiveVisualizer;
import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import java.io.*;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import com.moe.LiveVisualizer.preference.SeekBarPreference;
import android.view.WindowManager;
import java.lang.reflect.Field;
import android.widget.ProgressBar;
import java.lang.ref.WeakReference;
import java.lang.ref.SoftReference;
import android.widget.Toast;
import java.util.Arrays;

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,Preference.OnPreferenceChangeListener
{
	private final static int WALLPAPER=0X02;
	private final static int WALLPAPER_SUCCESS=0x04;
	private final static int WALLPAPER_DISMISS=0x05;
	private AlertDialog delete;
	private ProgressDialog gif_dialog=null;
	private ListPreference color_mode,visualizer_mode,color_direction;
	private DisplayMetrics display;
	private SoftReference<Uri> weak;
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		display =new DisplayMetrics();
		( (WindowManager)getActivity().getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(display);
		super.onActivityCreated(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName("moe");
		addPreferencesFromResource(R.xml.setting);
		findPreference("background").setOnPreferenceClickListener(this);
		//findPreference("artwork").setEnabled(Build.VERSION.SDK_INT>18);
		color_mode = (ListPreference) findPreference("color_mode");
		visualizer_mode = (ListPreference) findPreference("visualizer_mode");
		color_direction=(ListPreference) findPreference("color_direction");
		color_mode.setOnPreferenceChangeListener(this);
		visualizer_mode.setOnPreferenceChangeListener(this);
		color_direction.setOnPreferenceChangeListener(this);
		onPreferenceChange(color_mode, getPreferenceManager().getSharedPreferences().getString("color_mode", "0"));
		onPreferenceChange(visualizer_mode, getPreferenceManager().getSharedPreferences().getString("visualizer_mode", "0"));
		onPreferenceChange(color_direction,getPreferenceManager().getSharedPreferences().getString("color_direction","0"));

		((SeekBarPreference)findPreference("borderHeight")).setMax(250);
	}

	@Override
	public boolean onPreferenceChange(Preference p1, Object p2)
	{
		switch ( p1.getKey() )
		{
			case "color_mode":
				try{
				color_mode.setSummary(color_mode.getEntries()[Arrays.binarySearch(color_mode.getEntryValues(),p2.toString())]);
				}catch(Exception e){}
				break;
			case "visualizer_mode":
				visualizer_mode.setSummary(visualizer_mode.getEntries()[Integer.parseInt(p2.toString())]);
				break;
			case "color_direction":
				color_direction.setSummary(color_direction.getEntries()[Integer.parseInt(p2.toString())]);
				break;
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
				final File wallpaper=new File(getActivity().getExternalFilesDir(null), "wallpaper");
				final File wallpaper_p=new File(getActivity().getExternalFilesDir(null),"wallpaper_p");
				if ( wallpaper.exists() )
				{
					if ( delete == null )
					{
						delete = new AlertDialog.Builder(getActivity()).setTitle("确认").setMessage("是否清除当前背景？").setPositiveButton("取消", null).setNegativeButton("确定", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									wallpaper.delete();
									wallpaper_p.delete();
									getActivity().sendBroadcast(new Intent("wallpaper_changed"));

								}
							}).create();
					}
					delete.show();
				}
				else
				{
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					/*intent.putExtra("crop", "true");
					 //width:height
					 Display display=getActivity().getWindowManager().getDefaultDisplay();
					 intent.putExtra("aspectX", display.getWidth());
					 intent.putExtra("aspectY", display.getHeight());
					 intent.putExtra("outputX",display.getWidth());
					 intent.putExtra("outputY",display.getHeight());
					 intent.putExtra("output", Uri.fromFile(wallpaper));
					 intent.putExtra("return-data",false);
					 intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());*/
					try
					{
						startActivityForResult(Intent.createChooser(intent, "Choose Image"), WALLPAPER);
					}
					catch (Exception e)
					{}
				}
				break;
			
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data)
	{
		if ( resultCode == Activity.RESULT_OK )
			switch ( requestCode )
			{
				case WALLPAPER:
					weak=new SoftReference<Uri>(data.getData());
					if ( gif_dialog == null )
					{
						gif_dialog = new ProgressDialog(getActivity());
						gif_dialog.setMessage("如何处理图片");
						gif_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						gif_dialog.setButton(ProgressDialog.BUTTON1,"裁剪", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									try{
										Field progressBar=gif_dialog.getClass().getDeclaredField("mProgress");
										progressBar.setAccessible(true);
										((ProgressBar)progressBar.get(gif_dialog)).setVisibility(ProgressBar.VISIBLE);
									}catch(Exception e){}
									new Thread(){
										public void run()
										{
											final File tmp=new File(getActivity().getExternalFilesDir(null),"tmpImage");
											FileOutputStream fos=null;
											InputStream is=null;
											try
											{
												fos = new FileOutputStream(tmp);
												is = getActivity().getContentResolver().openInputStream(weak.get());
												byte[] buffer=new byte[16*1024];
												int len;
												while ( (len = is.read(buffer)) != -1 )
													fos.write(buffer, 0, len);
												fos.flush();
												final File wallpaper=new File(getActivity().getExternalFilesDir(null), "wallpaper");
												Intent intent = new Intent("com.android.camera.action.CROP");
												intent.setClass(getActivity(),CropActivity.class);
												/*if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N )
												{
													intent.setDataAndType(FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", tmp), "image/*");
													intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
												}
												else*/
													intent.setDataAndType(Uri.fromFile(tmp), "image/*");

												intent.putExtra("crop", "true");
												int width=Math.min(display.widthPixels,display.heightPixels);
												int height=Math.max(display.widthPixels,display.heightPixels);
												intent.putExtra("aspectX", width);
												intent.putExtra("aspectY", height);
												intent.putExtra("outputX", width);
												intent.putExtra("outputY", height);
												intent.putExtra("output", Uri.fromFile(wallpaper));
												intent.putExtra("output2",Uri.fromFile(new File(getActivity().getExternalFilesDir(null),"wallpaper_p")));
												intent.putExtra("return-data", false);
												intent.putExtra("two",true);
												intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
												try
												{
													startActivityForResult(intent, WALLPAPER_SUCCESS);
												}
												catch (Exception e)
												{Toast.makeText(getActivity(),"请安装一个裁剪图片的软件",Toast.LENGTH_LONG).show();}
												
											}
											catch (Exception e)
											{}
											finally
											{
												try
												{
													if ( fos != null )fos.close();
												}
												catch (IOException e)
												{}
												try
												{
													if ( is != null )is.close();
												}
												catch (IOException e)
												{}
											}
											handler.obtainMessage(WALLPAPER_DISMISS).sendToTarget();
											}
									}.start();
								}
							});
							gif_dialog.setButton(ProgressDialog.BUTTON2,"GIF", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									try{
									Field progressBar=gif_dialog.getClass().getDeclaredField("mProgress");
									progressBar.setAccessible(true);
									((ProgressBar)progressBar.get(gif_dialog)).setVisibility(ProgressBar.VISIBLE);
									}catch(Exception e){}
									new Thread(){
										public void run(){
											final File tmp=new File(getActivity().getExternalFilesDir(null), "wallpaper");
											FileOutputStream fos=null;
											InputStream is=null;
											try
											{
												fos = new FileOutputStream(tmp);
												is = getActivity().getContentResolver().openInputStream(weak.get());
												byte[] buffer=new byte[16*1024];
												int len;
												while ( (len = is.read(buffer)) != -1 )
													fos.write(buffer, 0, len);
												fos.flush();
											}
											catch (Exception e)
											{}
											finally
											{
												try
												{
													if ( fos != null )fos.close();
												}
												catch (IOException e)
												{}
												try
												{
													if ( is != null )is.close();
												}
												catch (IOException e)
												{}
											}
											getActivity().sendBroadcast(new Intent("wallpaper_changed"));
											handler.obtainMessage(WALLPAPER_DISMISS).sendToTarget();
										}
									}.start();
								}
							});
							gif_dialog.setButton(ProgressDialog.BUTTON3,"取消",handler.obtainMessage(WALLPAPER_DISMISS));
							gif_dialog.setCanceledOnTouchOutside(false);
					}
					gif_dialog.show();
					try
					{
						Field progressBar=gif_dialog.getClass().getDeclaredField("mProgress");
						progressBar.setAccessible(true);
						((ProgressBar)progressBar.get(gif_dialog)).setVisibility(ProgressBar.GONE);
						Field show=Dialog.class.getDeclaredField("mShowing");
						show.setAccessible(true);
						show.setBoolean(gif_dialog,false);
					}
					catch (Exception e)
					{}
					/*if(data.getData()==null)
					 getActivity().sendBroadcast(new Intent("wallpaper_changed"));
					 else{

					 }*/
					break;
				case WALLPAPER_SUCCESS:
					getActivity().sendBroadcast(new Intent("wallpaper_changed"));
					break;
				
			}
	}

	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case WALLPAPER_DISMISS:
					if(gif_dialog!=null){
						try{
							Field show=Dialog.class.getDeclaredField("mShowing");
							show.setAccessible(true);
							show.setBoolean(gif_dialog,true);
						}catch(Exception e){}
						gif_dialog.dismiss();
					}
					break;
					
			}
		}
	
};
}
