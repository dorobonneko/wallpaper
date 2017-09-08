package com.moe.fragment.preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.os.Bundle;
import com.moe.yaohuo.R;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.support.v7.preference.Preference;
import android.content.Intent;
import com.moe.yaohuo.DirectoryActivity;
import android.app.Activity;
import android.os.Environment;
import android.support.v7.preference.ListPreference;
public class SettingPreference extends PreferenceFragment implements Preference.OnPreferenceClickListener,Preference.OnPreferenceChangeListener
{
	ListPreference host;
	Preference path;
	@Override
	public void onCreatePreferences(Bundle p1, String p2)
	{
		addPreferencesFromResource(R.xml.setting);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		((SwitchPreferenceCompat)findPreference("direct"))
		
		.setChecked(getPreferenceManager().getSharedPreferences().getBoolean("direct",false));
		((SwitchPreferenceCompat)findPreference("emoji")).setChecked(getPreferenceManager().getSharedPreferences().getBoolean("emoji",false));
		
		path=findPreference("download_path");
		path.setOnPreferenceClickListener(this);
		path.setSummary(getPreferenceManager().getSharedPreferences().getString("path",Environment.getExternalStorageDirectory().getAbsolutePath()+"/yaohuo"));
		host=(ListPreference) findPreference("host");
		host.setSummary(getPreferenceManager().getSharedPreferences().getString("host",null));
		host.setValue(host.getSummary().toString());
		host.setOnPreferenceChangeListener(this);
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
	public boolean onPreferenceChange(Preference p1, Object p2)
	{
		switch(p1.getKey()){
			case "host":
				host.setSummary(p2.toString());
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

	
}
