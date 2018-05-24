package com.moe.LiveVisualizer.fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.moe.LiveVisualizer.R;
import android.preference.ListPreference;
import android.preference.Preference;

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
		onPreferenceChange(duang_screen,getPreferenceManager().getSharedPreferences().getString("duang_screen","0"));
	}

	@Override
	public boolean onPreferenceChange(Preference p1, Object p2)
	{
		switch(p1.getKey()){
			case "duang_screen":
				duang_screen.setSummary(duang_screen.getEntries()[duang_screen.findIndexOfValue(p2.toString())]);
				break;
		}
		return true;
	}


	
}
