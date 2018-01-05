package com.moe.preference;
import android.support.v7.preference.Preference;
import android.support.v7.app.AlertDialog;
import com.moe.yaohuo.R;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import com.avos.avoscloud.AVObject;
import com.moe.utils.PreferenceUtils;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.AVException;
import android.widget.Toast;
import android.view.ViewGroup;
import android.util.TypedValue;
import android.widget.FrameLayout;

public class ReplyBugPreference extends Preference implements DialogInterface.OnClickListener
{
	private Callback call;
	private EditText bug;
	private AlertDialog alert;
	public ReplyBugPreference(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context,attrs,defStyleAttr,defStyleRes);
	}

    public ReplyBugPreference(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
	}

    public ReplyBugPreference(android.content.Context context, android.util.AttributeSet attrs) {
		super(context,attrs);
	}

    public ReplyBugPreference(android.content.Context context) {
		super(context);
	}

	@Override
	protected void onClick()
	{
		if(alert==null){
			bug=(EditText) LayoutInflater.from(getContext()).inflate(R.layout.send_money,null);
			bug.setSingleLine(false);
			int size=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16,getContext().getResources().getDisplayMetrics());
			
			alert=new AlertDialog.Builder(getContext()).setTitle("反馈").setView(bug,size,size/2,size,0).setPositiveButton("取消",null).setNegativeButton("提交",this).create();
			}
			alert.show();
	}

	@Override
	public void onClick(DialogInterface p1, int p2)
	{
		AVObject av=new AVObject("bug");
		av.add("uid",PreferenceUtils.getUid(getContext()));
		av.add("bug",bug.getText().toString());
		av.saveInBackground(call==null?call=new Callback():call);
	}
	private class Callback extends SaveCallback{
	@Override
	public void done(AVException p1)
	{
		if(p1==null)
		Toast.makeText(getContext(),"提交成功",Toast.LENGTH_SHORT).show();
			else
		Toast.makeText(getContext(),p1.getMessage(),Toast.LENGTH_SHORT).show();
	}
	}
}
