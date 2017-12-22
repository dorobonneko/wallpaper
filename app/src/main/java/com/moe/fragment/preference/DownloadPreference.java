package com.moe.fragment.preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.os.Bundle;
import com.moe.yaohuo.R;
import android.support.v7.preference.Preference;
import android.os.Environment;
import com.moe.yaohuo.DirectoryActivity;
import android.content.Intent;
import android.app.Activity;
import android.support.v7.preference.ListPreference;
public class DownloadPreference extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener,Preference.OnPreferenceChangeListener
{
	Preference path;
	ListPreference buffer;
	@Override
	public void onCreatePreferences(Bundle p1, String p2)
	{
		// TODO: Implement this method
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName("setting");
		addPreferencesFromResource(R.xml.download);
		path=findPreference("download_path");
		path.setOnPreferenceClickListener(this);
		path.setSummary(getPreferenceManager().getSharedPreferences().getString("path",Environment.getExternalStorageDirectory().getAbsolutePath()+"/yaohuo"));
		buffer=(ListPreference) findPreference("buffer_size");
		buffer.setOnPreferenceChangeListener(this);
		buffer.setSummary(buffer.getValue()+"Byte");
	}
	@Override
	public boolean onPreferenceClick(Preference p1)
	{
		switch(p1.getKey()){
			case "download_path":
				startActivityForResult(new Intent(getContext(),DirectoryActivity.class),641);
				break;
		}
		return true;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode==Activity.RESULT_OK){
			switch(requestCode){
				case 641:
					getPreferenceManager().getSharedPreferences().edit().putString("path",data.getDataString()).commit();
					path.setSummary(data.getDataString());
					break;
			}
		}
	}

	@Override
	public boolean onPreferenceChange(Preference p1, Object p2)
	{
		switch(p1.getKey()){
			case "buffer_size":
				buffer.setSummary(p2.toString()+"Byte");
				break;
		}
		return true;
	}

	
}
