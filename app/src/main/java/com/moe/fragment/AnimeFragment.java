package com.moe.fragment;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.os.Bundle;
import android.content.res.Configuration;
import android.view.animation.Animation;
import android.view.animation.AlphaAnimation;

public class AnimeFragment extends Fragment
{
	

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
