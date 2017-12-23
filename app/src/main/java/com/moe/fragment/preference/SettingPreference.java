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
import android.content.pm.*;
import android.content.pm.PackageManager.*;
import android.widget.*;
import android.support.v7.app.*;
import java.util.*;
public class SettingPreference extends PreferenceFragment implements Preference.OnPreferenceChangeListener,Preference.OnPreferenceClickListener
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
		try
		{
			findPreference("setting_update").setSummary("v" + getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName);
		}
		catch (PackageManager.NameNotFoundException e)
		{}
		findPreference("setting_update").setOnPreferenceClickListener(this);
		findPreference("setting_faq").setOnPreferenceClickListener(this);
		findPreference("setting_dev").setOnPreferenceClickListener(this);
		findPreference("setting_about").setOnPreferenceClickListener(this);
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

	@Override
	public boolean onPreferenceClick(Preference p1)
	{
		switch(p1.getKey()){
			case "setting_update":
				break;
			case "setting_faq":
				break;
				case "setting_dev":
			
				
				break;
			case "setting_about":
				break;
		}
		return false;
	}



	

	
}
