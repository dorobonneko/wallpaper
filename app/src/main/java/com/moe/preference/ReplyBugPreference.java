package com.moe.preference;
import android.support.v7.preference.Preference;
import android.support.v7.app.AlertDialog;
import com.moe.yaohuo.R;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import com.moe.utils.PreferenceUtils;
import android.widget.Toast;
import android.view.ViewGroup;
import android.util.TypedValue;
import android.widget.FrameLayout;

public class ReplyBugPreference extends Preference implements DialogInterface.OnClickListener
{
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
		}
	
}
