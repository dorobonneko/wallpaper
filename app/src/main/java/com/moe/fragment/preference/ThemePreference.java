package com.moe.fragment.preference;
import android.os.Bundle;
import com.moe.yaohuo.R;

public class ThemePreference extends PreferenceFragment
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName("theme");
		addPreferencesFromResource(R.xml.theme);
	}
	
}
