package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.bumptech.glide.Glide;
import android.os.Build;
import com.moe.utils.PreferenceUtils;

public class BaseActivity extends AppCompatActivity
{

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case android.R.id.home:
				finish();
				return true;
				default:
		return super.onOptionsItemSelected(item);
		}
	}
}
