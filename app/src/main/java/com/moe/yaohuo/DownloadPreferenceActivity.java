package com.moe.yaohuo;
import android.os.Bundle;
import com.moe.fragment.preference.DownloadPreference;
import android.support.v4.app.Fragment;
import java.util.List;

public class DownloadPreferenceActivity extends EventActivity
{
	private DownloadPreference df;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		df=(DownloadPreference)getSupportFragmentManager().findFragmentByTag("df");
		if(df==null){
			df=new DownloadPreference();
			getSupportFragmentManager().beginTransaction().add(R.id.main_index,df).commit();
			}
		getSupportActionBar().setTitle("下载设置");
	}
	
}
