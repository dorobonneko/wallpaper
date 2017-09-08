package com.moe.utils;
import android.content.SharedPreferences;
import android.content.Context;
import com.moe.yaohuo.R;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class PreferenceUtils
{
	private static PreferenceUtils pu;
	private SharedPreferences moe;
	private PreferenceUtils(Context c){
		moe=c.getSharedPreferences("moe",0);
	}
	public SharedPreferences getPreference(){
		return moe;
	}
	public static PreferenceUtils getInstance(Context context){
		if(pu==null)pu=new PreferenceUtils(context);
		return pu;
	}
	public static boolean isDirect(Context applicationContext)
	{
		
		return getInstance(applicationContext).getPreference().getBoolean("direct",false);
	}
	public static String getHost(Context moe){
		return getInstance(moe).getPreference().getString("host",moe.getResources().getStringArray(R.array.host)[0]);
	}
	public static String getCookie(Context moe){
		return getInstance(moe).getPreference().getString("cookie","");
	}
	public static String getUserAgent(){
		return "Mozilla/5.0 (Linux; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 Mobile Safari/537.36" + (int)(Math.random() * Integer.MAX_VALUE);
	}
	public static boolean isLogin(Context context){
		return getUid(context)!=0;
	}
	public static int getUid(Context context){
		return getInstance(context).getPreference().getInt("uid",0);
	}
	public static String getCookieName(Context context){
		Matcher matcher= Pattern.compile("//(.*?)[\\.]").matcher(getHost(context));
		if(matcher.find())
			return "sid"+matcher.group(1);
			else
			return null;
	}
}
