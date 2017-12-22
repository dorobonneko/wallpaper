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
import com.moe.yaohuo.MainActivity;
public class SettingPreference extends PreferenceFragment implements Preference.OnPreferenceChangeListener
{
	ListPreference host;
	
	@Override
	public void onCreatePreferences(Bundle p1, String p2)
	{
			}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);
		
		//((SwitchPreferenceCompat)findPreference("direct"))
		
		//.setChecked(getPreferenceManager().getSharedPreferences().getBoolean("direct",false));
		//((SwitchPreferenceCompat)findPreference("emoji")).setChecked(getPreferenceManager().getSharedPreferences().getBoolean("emoji",false));
		
		host=(ListPreference) findPreference("host");
		host.setSummary(getPreferenceManager().getSharedPreferences().getString("host",null));
		//host.setValue(host.getSummary()==null?null:host.getSummary().toString());
		host.setOnPreferenceChangeListener(this);
		findPreference("exit_mode").setOnPreferenceChangeListener(this);
	}

	
	@Override
	public boolean onPreferenceChange(Preference p1, Object p2)
	{
		switch(p1.getKey()){
			case "host":
				host.setSummary(p2.toString());
				break;
			case "exit_mode":
				((MainActivity)getActivity()).reloadExit("侧边栏".equals(p2));
				break;
		}
		return true;
	}


	

	
}
