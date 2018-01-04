package com.moe.widget;
import android.widget.LinearLayout;
import android.util.AttributeSet;
import android.content.Context;
import com.moe.graphics.ProgressDrawable;
import android.widget.ImageView;
import com.moe.yaohuo.R;
import android.widget.TextView;

public class LoadMoreView extends LinearLayout
{
	private ProgressDrawable pd;
	private TextView summary;
	public LoadMoreView(Context context,AttributeSet attrs){
		super(context,attrs);
		pd=new ProgressDrawable(context);
	}
	public void setState(int state){
		pd.setProgressState(state);
		switch(state){
			case State.PROGRESS:
				setSummary("正在加载");
				break;
			case State.ERROR:
				setSummary("加载失败");
				break;
			case State.SUCCESS:
				setSummary("加载成功");
				break;
		}
	}
	public int getState(){
		return pd.getProgressState();
	}
	public void setSummary(CharSequence c){
		summary.setText(c);
	}
	public CharSequence getSummary(){
		return summary.getText();
	}
	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		((ImageView)findViewById(R.id.state)).setImageDrawable(pd);
		summary=(TextView) findViewById(android.R.id.summary);
	}
	public static class State extends ProgressDrawable.State{
		}
}
