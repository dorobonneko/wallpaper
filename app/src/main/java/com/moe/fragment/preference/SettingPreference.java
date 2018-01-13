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
public class SettingPreference extends PreferenceFragment
{
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);
		
		//((SwitchPreferenceCompat)findPreference("direct"))
		
		//.setChecked(getPreferenceManager().getSharedPreferences().getBoolean("direct",false));
		//((SwitchPreferenceCompat)findPreference("emoji")).setChecked(getPreferenceManager().getSharedPreferences().getBoolean("emoji",false));
		
			}

	
	

	
	

	
}
