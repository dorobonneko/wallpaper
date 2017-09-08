package com.moe.fragment;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.animation.Animation;
import android.content.res.TypedArray;

public class Fragment extends android.support.v4.app.Fragment
{

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		
		view.setOnClickListener(null);
		super.onViewCreated(view, savedInstanceState);
	}


	
	public boolean onBackPressed(){
		return false;
	}
}
