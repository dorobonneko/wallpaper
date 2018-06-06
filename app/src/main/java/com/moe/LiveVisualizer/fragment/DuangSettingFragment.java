package com.moe.LiveVisualizer.fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.moe.LiveVisualizer.R;
import android.preference.ListPreference;
import android.preference.Preference;
import android.content.Intent;
import android.app.Activity;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import android.os.Handler;
import android.os.Looper;

public class DuangSettingFragment extends PreferenceFragment implements ListPreference.OnPreferenceChangeListener
{
	private ListPreference duang_screen;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName("moe");
		addPreferencesFromResource(R.xml.duang);
		duang_screen=(ListPreference) findPreference("duang_screen");
		duang_screen.setOnPreferenceChangeListener(this);
		duang_screen.setSummary(duang_screen.getEntries()[duang_screen.findIndexOfValue(getPreferenceManager().getSharedPreferences().getString("duang_screen","0"))]);
		}

	@Override
	public boolean onPreferenceChange(Preference p1, Object p2)
	{
		switch(p1.getKey()){
			case "duang_screen":
				if(p2.equals("4")){
					startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),4832);
					return false;
				}
				duang_screen.setSummary(duang_screen.getEntries()[duang_screen.findIndexOfValue(p2.toString())]);
				break;
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==4832&&resultCode==Activity.RESULT_OK){
			save(data);
		}
	}


	private void save(final Intent data){
		new Thread(){
			public void run(){
				InputStream is=null;
				OutputStream os=null;
				try
				{
					is = getActivity().getContentResolver().openInputStream(data.getData());
					os=new FileOutputStream(new File(getActivity().getExternalFilesDir(null),"duang"));
					int len=0;
					byte[] buffer=new byte[2048];
					while((len=is.read(buffer))!=-1)
						os.write(buffer,0,len);
						os.flush();
					new Handler(Looper.getMainLooper()).post(new Runnable(){

							@Override
							public void run()
							{
								duang_screen.setSummary(duang_screen.getEntries()[4]);
								getPreferenceManager().getSharedPreferences().edit().putString("duang_screen","4").commit();
							}
						});
					}
				catch (Exception e)
				{}finally{
					try
					{
						if (os != null)
							os.close();
					}
					catch (IOException e)
					{}
					try
					{
						if (is != null)
							is.close();
					}
					catch (IOException e)
					{}
				}
			}
		}.start();
	}
}
