package com.moe.view;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.ViewCompat;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.os.Build;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.animation.ObjectAnimator;
import android.util.TypedValue;
import android.animation.Animator;
import android.view.animation.TranslateAnimation;
import android.support.design.widget.CoordinatorLayout.LayoutParams;

public class FloatingActionButtonBehavior extends FloatingActionButton.Behavior {
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private float oldy;
	private float height;
	private boolean isHide=false;
	private float dy;
    public FloatingActionButtonBehavior(Context context, AttributeSet attrs) {
        super();
    }
	
	@Override
	public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target)
	{
		super.onStopNestedScroll(coordinatorLayout, child, target);
		if(isHide)
		animateIn(child);
	}

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
		dy=0;
		if(oldy==0){
			oldy=child.getY();
			height=oldy+child.getHeight()+TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30,child.getResources().getDisplayMetrics());
		}
        return nestedScrollAxes ==ViewCompat.SCROLL_AXIS_VERTICAL;
		
    }

	@Override
	public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed)
	{
		// TODO: Implement this method
		super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
		if(!isHide&&Math.abs(dy+=dyConsumed)>20)animateOut(child);
		
	}
	
	
    private void animateOut(final FloatingActionButton button) {
		Animator anime=ObjectAnimator.ofFloat(button,"TranslationY",new float[]{0,height});
		anime.setDuration(300);
		anime.setInterpolator(INTERPOLATOR);
		anime.start();
		isHide=true;
    }

    private void animateIn(FloatingActionButton button) {
		Animator anime=ObjectAnimator.ofFloat(button,"TranslationY",new float[]{height,0});
		anime.setDuration(300);
		anime.setInterpolator(INTERPOLATOR);
		anime.start();
		isHide=false;
		}
}
