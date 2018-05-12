package com.moe.LiveVisualizer;
import android.preference.PreferenceActivity;
import android.os.PersistableBundle;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.Preference;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import java.lang.ref.SoftReference;
import android.net.Uri;
import java.io.File;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Activity;
import java.lang.reflect.Field;
import android.widget.ProgressBar;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.os.Build;
import android.widget.Toast;
import java.io.IOException;
import android.os.Handler;
import android.os.Message;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.app.Service;
import com.moe.LiveVisualizer.preference.SeekBarPreference;

public class CircleSettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener
{
	private DisplayMetrics display;
	
	private final static int CIRCLE=0x03;
	
	private final static int CIRCLE_SUCCESS=0x06;
	private final static int CIRCLE_DISMISS=0x07;
	private ProgressDialog circle_dialog;
	private AlertDialog circle_delete;
	private SoftReference<Uri> weak;
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		display =new DisplayMetrics();
		( (WindowManager)getActivity().getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(display);

		getPreferenceManager().setSharedPreferencesName("moe");
		addPreferencesFromResource(R.xml.circle_setting);
		findPreference("circle_image").setOnPreferenceClickListener(this);
		int width=Math.min(display.widthPixels,display.heightPixels);
		int height=Math.max(display.widthPixels,display.heightPixels);
		SeekBarPreference offsetX=(SeekBarPreference)findPreference("offsetX");
		offsetX.setMax(width);
		offsetX.setDefaultValue(width/2);
		SeekBarPreference offsetY=(SeekBarPreference) findPreference("offsetY");
		offsetY.setMax(height);
		offsetY.setDefaultValue(height);
		SeekBarPreference circle_radius=(SeekBarPreference) findPreference("circleRadius");
		circle_radius.setMax(width);
		circle_radius.setDefaultValue(width/3);
	}

	@Override
	public boolean onPreferenceClick(Preference p1)
	{
		switch(p1.getKey()){
			case "circle_image":
				final File circle=new File(getActivity().getExternalFilesDir(null), "circle");
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
				}
				else
				{
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					/*intent.putExtra("crop", "true");
					 //width:height
					 intent.putExtra("aspectX", 1);
					 intent.putExtra("aspectY", 1);
					 intent.putExtra("outputX", display.widthPixels / 3);
					 intent.putExtra("outputY", display.widthPixels / 3);
					 intent.putExtra("output", Uri.fromFile(circle));
					 intent.putExtra("return-data", false);
					 intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());*/
					try
					{startActivityForResult(Intent.createChooser(intent, "Choose Image"), CIRCLE);}
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
				case CIRCLE:
					weak=new SoftReference<Uri>(data.getData());
					if ( circle_dialog == null )
					{
						circle_dialog = new ProgressDialog(getActivity());
						circle_dialog.setMessage("如何处理图片");
						circle_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						circle_dialog.setButton(ProgressDialog.BUTTON1,"裁剪", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									try{
										Field progressBar=circle_dialog.getClass().getDeclaredField("mProgress");
										progressBar.setAccessible(true);
										((ProgressBar)progressBar.get(circle_dialog)).setVisibility(ProgressBar.VISIBLE);
									}catch(Exception e){}
									new Thread(){
										public void run()
										{
											final File tmp=new File(getActivity().getExternalFilesDir(null), "tmpImage");
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
												final File circle_file=new File(getActivity().getExternalFilesDir(null), "circle");
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
												intent.putExtra("aspectX", 1);
												intent.putExtra("aspectY", 1);
												int width=Math.min(display.widthPixels,display.heightPixels)/3;
												intent.putExtra("outputX", width);
												intent.putExtra("outputY", width);
												intent.putExtra("output", Uri.fromFile(circle_file));
												intent.putExtra("return-data", false);
												intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
												try
												{
													startActivityForResult(intent, CIRCLE_SUCCESS);
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
											handler.obtainMessage(CIRCLE_DISMISS).sendToTarget();
										}
									}.start();
								}
							});
						circle_dialog.setButton(ProgressDialog.BUTTON2,"GIF", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									try{
										Field progressBar=circle_dialog.getClass().getDeclaredField("mProgress");
										progressBar.setAccessible(true);
										((ProgressBar)progressBar.get(circle_dialog)).setVisibility(ProgressBar.VISIBLE);
									}catch(Exception e){}
									new Thread(){
										public void run(){
											final File tmp=new File(getActivity().getExternalFilesDir(null), "circle");
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
											getActivity().sendBroadcast(new Intent("circle_changed"));
											handler.obtainMessage(CIRCLE_DISMISS).sendToTarget();
										}
									}.start();
								}
							});
						circle_dialog.setButton(ProgressDialog.BUTTON3,"取消",handler.obtainMessage(CIRCLE_SUCCESS));
						circle_dialog.setCanceledOnTouchOutside(false);
					}
					circle_dialog.show();
					try
					{
						Field progressBar=circle_dialog.getClass().getDeclaredField("mProgress");
						progressBar.setAccessible(true);
						((ProgressBar)progressBar.get(circle_dialog)).setVisibility(ProgressBar.GONE);
						Field show=Dialog.class.getDeclaredField("mShowing");
						show.setAccessible(true);
						show.setBoolean(circle_dialog,false);
					}
					catch (Exception e)
					{}
					break;
				case CIRCLE_SUCCESS:
					getActivity().sendBroadcast(new Intent("circle_changed"));
					break;
					}
					}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case CIRCLE_DISMISS:
					if(circle_dialog!=null){
						try{
							Field show=Dialog.class.getDeclaredField("mShowing");
							show.setAccessible(true);
							show.setBoolean(circle_dialog,true);
						}catch(Exception e){}
						circle_dialog.dismiss();
					}
					break;
			}
		}

	};
}
