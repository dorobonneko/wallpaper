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
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Build;

public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener
{
	private CharSequence unit;
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
		View view =LayoutInflater.from(getContext()).inflate(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP?R.layout.seekbar_preference_material:R.layout.seekbar_preference,parent,false);
		return view;
	}

	@Override
	protected void onBindView(View view)
	{
		super.onBindView(view);
		final SeekBar seekbar=(SeekBar)view.findViewById(R.id.seekbar);
		seekbar.setOnSeekBarChangeListener(this);
		TextView tips=(TextView)view.findViewById(R.id.tips);
		seekbar.setTag(tips);
		((View)view.findViewById(R.id.plus)).setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					seekbar.setProgress(seekbar.getProgress()+1);
					onStopTrackingTouch(seekbar);
				}
			});
		((View)view.findViewById(R.id.minus)).setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					seekbar.setProgress(seekbar.getProgress()-1);
					onStopTrackingTouch(seekbar);
				}
			});
		seekbar.setMax(max);
		seekbar.setProgress(progress);
		tips.setText(seekbar.getProgress()+(unit!=null?unit.toString():""));
	}
	public void setMax(int max){
		this.max=max;
		notifyChanged();
	}
	@Override
	public void onProgressChanged(SeekBar p1, int p2, boolean p3)
	{
		if(p1.getTag()!=null)
			((TextView)p1.getTag()).setText(p2+(unit!=null?unit.toString():""));
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
			if(shouldPersist()){
				progress=p1.getProgress();
				persistInt(p1.getProgress());
				}
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

	@Override
	protected Parcelable onSaveInstanceState()
	{
		return new  SavedState(super.onSaveInstanceState());
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		SavedState save=(SeekBarPreference.SavedState) state;
		super.onRestoreInstanceState(save.getSuperState());
		max=save.max;
		progress=save.progress;
		unit=save.unit;
		
	}
	
	class SavedState extends BaseSavedState{
		int max,progress;
		CharSequence unit;
		public SavedState(Parcelable parcel){
			super(parcel);
		}
		public SavedState(Parcel parcel){
			super(parcel);
			max=parcel.readInt();
			progress=parcel.readInt();
			unit=(CharSequence)parcel.readValue(CharSequence.class.getClassLoader());
		}
		@Override
		public void writeToParcel(Parcel dest, int flags)
		{
			// TODO: Implement this method
			super.writeToParcel(dest, flags);
			dest.writeInt(SeekBarPreference.this.max);
			dest.writeInt(SeekBarPreference.this.progress);
			dest.writeValue(SeekBarPreference.this.unit);
		}
	}
}
