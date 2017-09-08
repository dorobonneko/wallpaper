package com.moe.fragment.preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.os.Bundle;
import com.moe.yaohuo.R;
import android.support.v7.preference.Preference;
import com.moe.yaohuo.MoneyActivity;
import android.content.Intent;
public class CenterPreference extends PreferenceFragment implements Preference.OnPreferenceClickListener
{

	@Override
	public void onCreatePreferences(Bundle p1, String p2)
	{
		addPreferencesFromResource(R.xml.center);
		findPreference("money").setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference p1)
	{
		switch(p1.getKey()){
			case "money":
				getActivity().startActivity(new Intent(getContext(),MoneyActivity.class));
				break;
		}
		return true;
	}

	
}
