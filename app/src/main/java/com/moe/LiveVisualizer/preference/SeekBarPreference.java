package com.moe.LiveVisualizer.preference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.moe.LiveVisualizer.R;
import android.widget.TextView;
import android.widget.SeekBar;
import android.content.res.TypedArray;

public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener
{
	private CharSequence unit;
	private TextView tips;
	private SeekBar seekbar;
	private int progress,max=100;
	private boolean init;
	public SeekBarPreference(Context context,AttributeSet attrs){
		super(context,attrs);
		TypedArray ta=context.obtainStyledAttributes(attrs,new int[]{R.attr.unit,android.R.attr.max});
		max=ta.getInt(1,max);
		unit=ta.getString(0);
		ta.recycle();
	}

	@Override
	protected View onCreateView(ViewGroup parent)
	{
		// TODO: Implement this method
		return LayoutInflater.from(getContext()).inflate(R.layout.seekbar_preference,parent,false);
	}

	@Override
	protected void onBindView(View view)
	{
		super.onBindView(view);
		seekbar=(SeekBar)view.findViewById(R.id.seekbar);
		seekbar.setMax(max);
		seekbar.setProgress(progress);
		seekbar.setOnSeekBarChangeListener(this);
		tips=(TextView)view.findViewById(R.id.tips);
		tips.setText(seekbar.getProgress()+(unit!=null?unit.toString():""));
	}
	public void setMax(int max){
		this.max=max;
		notifyChanged();
	}
	@Override
	public void onProgressChanged(SeekBar p1, int p2, boolean p3)
	{
		this.progress=p2;
		tips.setText(p2+(unit!=null?unit.toString():""));
	}

	@Override
	public void onStartTrackingTouch(SeekBar p1)
	{
		// TODO: Implement this method
	}
	public void setUnit(CharSequence unit){
		this.unit=unit;
		notifyChanged();
	}

	@Override
	public void onStopTrackingTouch(SeekBar p1)
	{
		if(callChangeListener(p1.getProgress())){
			if(shouldPersist())persistInt(p1.getProgress());
		}
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue)
	{
		init=true;
		if(restorePersistedValue){
		progress=getPersistedInt(defaultValue!=null?defaultValue:progress);
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		progress=a.getInt(index,progress);
		return progress;
	}

	@Override
	public void setDefaultValue(Object defaultValue)
	{
		if(!init)
			progress=defaultValue==null?progress:(int)defaultValue;
		
			
	}
	
}
