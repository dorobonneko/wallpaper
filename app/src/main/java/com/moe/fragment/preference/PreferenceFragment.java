package com.moe.fragment.preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.animation.Animation;
import android.view.animation.AlphaAnimation;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

public class PreferenceFragment extends PreferenceFragmentCompat
{

	@Override
	public void onCreatePreferences(Bundle p1, String p2)
	{
		// TODO: Implement this method
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName("moe");
		
	}

	
	
	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim)
	{
		float[] alpha=new float[2];
		if(enter){
			alpha[0]=0;
			alpha[1]=1;
		}else{
			alpha[0]=1;
			alpha[1]=0;
		}
		Animation anime=new AlphaAnimation(alpha[0],alpha[1]);
		anime.setDuration(300);
		anime.setInterpolator(new FastOutSlowInInterpolator());
		return anime;
	}
}
