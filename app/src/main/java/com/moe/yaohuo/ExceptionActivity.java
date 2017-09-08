package com.moe.yaohuo;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.widget.TextView;
import android.content.Intent;

public class ExceptionActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		NestedScrollView nsv=new NestedScrollView(this);
		nsv.setFitsSystemWindows(true);
		TextView tv=new TextView(this);
		nsv.addView(tv);
		setContentView(nsv);
		tv.setText(getIntent().getStringExtra(Intent.EXTRA_TEXT));
	}
	
}
