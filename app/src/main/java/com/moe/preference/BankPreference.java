package com.moe.preference;
import android.support.v7.preference.Preference;
import android.support.v7.app.AlertDialog;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import java.io.IOException;
import org.jsoup.nodes.Document;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.os.Parcelable;
import android.view.View;
import android.view.LayoutInflater;
import com.moe.yaohuo.R;
import android.widget.EditText;
import android.text.InputType;
import android.content.DialogInterface;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
public class BankPreference extends Preference
{
	private String[] data;
	
	public BankPreference(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context,attrs,defStyleAttr,defStyleRes);
	}

    public BankPreference(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
	}

    public BankPreference(android.content.Context context, android.util.AttributeSet attrs) {
		super(context,attrs);
	}

    public BankPreference(android.content.Context context) {
		super(context);
	}

	
	@Override
	protected void onClick()
	{
		new Thread(){
			public void run(){

				try{
					Document doc=Jsoup.connect(PreferenceUtils.getHost(getContext()) + "/bbs/tomybankmoney.aspx")
						.data("type",  "0")
						.data("needpassword",getSharedPreferences().getString("pwd",""))
						.data("sid",PreferenceUtils.getCookie(getContext()))
						.userAgent(PreferenceUtils.getUserAgent())
						.cookie(PreferenceUtils.getCookieName(getContext()), PreferenceUtils.getCookie(getContext())).post();
					Matcher matcher=Pattern.compile("(?s):([0-9:\\/\\s]{1,}).*?:([0-9]{1,}).*?:([0-9]{1,}).*?:([0-9]{1,}).*:([0-9{1,}])",Pattern.DOTALL).matcher(doc.text());
					if(matcher.find()){
							data=new String[]{matcher.group(1),matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(5)};
							handler.sendEmptyMessage(1);
					}
				}
				catch (IOException e)
				{}
			}
		}.start();
	}
	private void todo(final String money,final String action){
		new Thread(){
			public void run(){
				try
				{
					if(Jsoup.connect(PreferenceUtils.getHost(getContext()) + "/bbs/tomybankmoney.aspx")
						.data("action", action)
						.data("tomoney", money)
						.data("siteid","1000")
						.data("needpassword",getSharedPreferences().getString("pwd",""))
						.data("sid", PreferenceUtils.getCookie(getContext()))
						.userAgent(PreferenceUtils.getUserAgent())
						.cookie(PreferenceUtils.getCookieName(getContext()), PreferenceUtils.getCookie(getContext()))
						.post().text().indexOf("成功")!=-1){
						 handler.obtainMessage(0,"操作成功").sendToTarget(); 
						return;
						}
				}
				catch (Exception e)
				{}
				handler.obtainMessage(0,"操作失败").sendToTarget();
			}
		}.start();
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					Toast.makeText(getContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
					break;
				case 1:
					final EditText v=(EditText)LayoutInflater.from(getContext()).inflate(R.layout.send_money,null);
						v.setInputType(InputType.TYPE_CLASS_NUMBER);
						v.setHint("输入金额：");
						new AlertDialog.Builder(getContext()).setTitle("银行管理").setMessage("存款金额："+data[1]+"\n可用余额："+data[2]+"\n存款利息："+data[3]+"%\n取款利息："+data[4]+"%\n上次结算利息："+data[0])
							.setView(v).setPositiveButton("取款", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									try
									{
										if (Long.parseLong(v.getText().toString()) > Long.parseLong(data[1]))
										{
											handler.obtainMessage(0,"取款金额打于存款").sendToTarget();

										}else{
											todo(v.getText().toString(),"sub");
										}
									}
									catch (NumberFormatException e)
									{
										handler.obtainMessage(0,"取款失败").sendToTarget();
									}
								}
							}).setNegativeButton("存款", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									try
									{
										if (Long.parseLong(v.getText().toString()) > Long.parseLong(data[2]))
										{
											handler.obtainMessage(0,"存款金额大于可用余额").sendToTarget();

										}else{
											todo(v.getText().toString(),"add");
										}
									}
									catch (NumberFormatException e)
									{handler.obtainMessage(0,"存款失败").sendToTarget();}
								}
							}).setNeutralButton("取消", null).show();
					
					break;
			}
			}
		
	};
}
