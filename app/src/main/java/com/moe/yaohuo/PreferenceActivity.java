package com.moe.yaohuo;
import android.os.Bundle;
import com.moe.fragment.preference.DownloadPreference;
import android.support.v4.app.Fragment;
import java.util.List;
import com.moe.fragment.preference.AdvancePreference;
import com.moe.fragment.preference.AboutPreference;

public class PreferenceActivity extends EventActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState==null)
		switch(getIntent().getAction()){
			case "download":
				getSupportFragmentManager().beginTransaction().add(R.id.main_index,new DownloadPreference(),"cache").commit();
				getSupportActionBar().setTitle("下载设置");
				break;
			case "advance":
				getSupportFragmentManager().beginTransaction().add(R.id.main_index,new AdvancePreference(),"cache").commit();
				getSupportActionBar().setTitle("高级设置");
				break;
			case "about":
				getSupportFragmentManager().beginTransaction().add(R.id.main_index,new AboutPreference(),"cache").commit();
				getSupportActionBar().setTitle("关于");
				break;
		}
		else
			getSupportFragmentManager().beginTransaction().add(R.id.main_index,getSupportFragmentManager().findFragmentByTag("cache"),"cache").commit();
		
		
	}
	
}
