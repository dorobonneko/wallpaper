package com.moe.preference;
import android.support.v7.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;

public class ExitPreference extends Preference
{
	private AlertDialog ad;
	public ExitPreference(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context,attrs,defStyleAttr,defStyleRes);
	}

    public ExitPreference(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
	}

    public ExitPreference(android.content.Context context, android.util.AttributeSet attrs) {
		super(context,attrs);
	}

    public ExitPreference(android.content.Context context) {
		super(context);
	}

	@Override
	protected void onClick()
	{
		if(ad==null){
			ad = new AlertDialog.Builder(getContext()).setMessage("确认退出？").setPositiveButton("退出", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						getSharedPreferences().edit().putInt("uid",0).putString("pwd","").putString("name",null).putString("cookie","").commit();
					}
				}).setNegativeButton("手滑了", null).show();
		}else ad.show();
	}
	
}
