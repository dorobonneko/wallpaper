package com.moe.fragment;
import android.view.View;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.design.widget.AppBarLayout;
import com.moe.yaohuo.R;

public abstract class UserSpaceFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		AppBarLayout abl=(AppBarLayout) container.getRootView().findViewById(R.id.appbarlayout);
		//if(abl!=null)abl.addOnOffsetChangedListener(this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	protected abstract void onStateChanged(boolean state);

	@Override
	public void onOffsetChanged(AppBarLayout p1, int p2)
	{
		onStateChanged(p2==0);
	}

	
}
