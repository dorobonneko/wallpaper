package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.text.TextUtils;
import org.jsoup.Jsoup;
import android.content.SharedPreferences;
import java.io.IOException;
import org.jsoup.Connection;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import java.util.Set;
import java.util.Map;
import com.moe.utils.PreferenceUtils;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
	private SharedPreferences moe;
private TextInputEditText username,passwd;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		moe=getSharedPreferences("moe",0);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_view);
		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		username=(TextInputEditText)findViewById(R.id.login_view_username);
		passwd=(TextInputEditText)findViewById(R.id.login_view_password);
		findViewById(R.id.login_view_login).setOnClickListener(this);
	}

	@Override
	public void finish()
	{
		// TODO: Implement this method
		super.finish();
		overridePendingTransition(0, 0);
	}

	@Override
	public void onClick(View p1)
	{
		boolean flag=true;
		if(TextUtils.isEmpty(username.getText().toString().trim()))
		{
			username.setError("用户名不能为空");
			username.requestFocus();
			flag=false;
		}
		if(TextUtils.isEmpty(passwd.getText().toString().trim()))
		{
			passwd.setError("密码不能为空");
			passwd.requestFocus();
			flag=false;
		}
		if(flag){
			new Thread(){
				public void run(){
					try
					{
						Connection.Response responde=Jsoup.connect(PreferenceUtils.getHost(LoginActivity.this)+"/waplogin.aspx").data("logname", username.getText().toString().trim()).data("logpass", passwd.getText().toString().trim()).data("action", "login").method(Connection.Method.POST).execute();
						String error=responde.parse().getElementsByClass("tip").get(0).child(0).childNode(0).toString();
							if(error.equals("登录成功！")){
								//Map<String,String> map=responde.cookies();
								//StringBuffer sb=new StringBuffer();
								//for(String key:map.keySet())
								//sb.append(key).append("=").append(map.get(key)).append(";");
								String uid=Jsoup.connect(PreferenceUtils.getHost(LoginActivity.this)).cookie(PreferenceUtils.getCookieName(getApplicationContext()),responde.cookie(PreferenceUtils.getCookieName(getApplicationContext()))).get().getElementsByAttributeValueMatching("href","touserid=").get(0).attr("href");
								uid.substring(uid.indexOf("touserid=")+9);
								Matcher matcher=Pattern.compile("[0-9]{1,}").matcher(uid);
								if(matcher.find())
									moe.edit().putInt("uid",Integer.parseInt(matcher.group())).commit();
								
								handler.obtainMessage(2,responde.cookie(PreferenceUtils.getCookieName(getApplicationContext()))).sendToTarget();
								}
							else if(error.indexOf("密码")!=-1){
								handler.obtainMessage(1,error).sendToTarget();
							}else{
								handler.obtainMessage(0,error).sendToTarget();
							}
						
					}
					catch (IOException e)
					{}
				}
			}.start();
		}
	}

	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					username.setError(msg.obj.toString());
					username.requestFocus();
					break;
				case 1:
					passwd.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL|InputType.TYPE_CLASS_TEXT);
					passwd.setError(msg.obj.toString());
					passwd.requestFocus();
					break;
				case 2:
					moe.edit().putString("pwd",passwd.getText().toString().trim()).putString("cookie",msg.obj.toString()).commit();
					setResult(RESULT_OK);
					finish();
					break;
			}
		}
		
	};
}
