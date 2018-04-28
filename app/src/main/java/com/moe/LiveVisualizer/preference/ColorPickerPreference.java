package com.moe.LiveVisualizer.preference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import com.moe.LiveVisualizer.widget.ColorPickerView;
import android.view.LayoutInflater;
import com.moe.LiveVisualizer.R;
import android.preference.ListPreference;

public class ColorPickerPreference extends Preference implements ColorPickerView.OnColorCheckedListener
{
	private AlertDialog dialog;
	private SharedPreferences moe;
	public ColorPickerPreference(Context context,AttributeSet attrs){
		super(context,attrs);
		moe=context.getSharedPreferences("moe",0);
	}

	@Override
	protected void onBindView(View view)
	{
		super.onBindView(view);
		View v=view.findViewById(android.R.id.widget_frame);
		ViewGroup.LayoutParams param=v.getLayoutParams();
		param.width=50;
		param.height=50;
		v.setLayoutParams(param);
		v.setVisibility(v.VISIBLE);
		v.setBackgroundColor(moe.getInt("color",0xff39c5bb));
	}

	@Override
	protected void onClick()
	{
		if(dialog==null){
			ColorPickerView picker=(ColorPickerView)LayoutInflater.from(getContext()).inflate(R.layout.color_picker,null);
			picker.setOnColorCheckedListener(this);
			dialog=new AlertDialog.Builder(getContext()).setView(picker).create();
		}
		dialog.show();
		/**/
	}

	@Override
	public void onColorChecked(int color)
	{
		if(shouldPersist()&&callChangeListener(color)){
			persistInt(color);
		}
	}

	
}
