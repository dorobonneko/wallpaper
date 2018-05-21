package com.moe.LiveVisualizer.fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.moe.LiveVisualizer.R;

public class DuangSettingFragment extends PreferenceFragment
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName("moe");
		addPreferencesFromResource(R.xml.duang);
	}

	
}
