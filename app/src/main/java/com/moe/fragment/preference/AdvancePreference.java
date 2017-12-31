package com.moe.fragment.preference;
import android.os.Bundle;
import com.moe.yaohuo.R;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import com.moe.yaohuo.MainActivity;

public class AdvancePreference extends PreferenceFragment implements Preference.OnPreferenceChangeListener
{
	ListPreference host;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.advance);
		host=(ListPreference) findPreference("host");
		host.setSummary(getPreferenceManager().getSharedPreferences().getString("host",null));
		//host.setValue(host.getSummary()==null?null:host.getSummary().toString());
		host.setOnPreferenceChangeListener(this);
		//findPreference("exit_mode").setOnPreferenceChangeListener(this);
		
	}
	@Override
	public boolean onPreferenceChange(Preference p1, Object p2)
	{
		switch(p1.getKey()){
			case "host":
				host.setSummary(p2.toString());
				break;
			case "exit_mode":
				break;
		}
		return true;
	}
	
}
