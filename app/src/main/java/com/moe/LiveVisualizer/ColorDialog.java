package com.moe.LiveVisualizer;
import android.app.DialogFragment;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import com.moe.LiveVisualizer.widget.ColorPickerView;
import android.view.WindowManager;
import android.view.Window;
import android.app.Dialog;

public class ColorDialog extends DialogFragment implements ColorPickerView.OnColorCheckedListener
{
	public ColorPickerView.OnColorCheckedListener l;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		return inflater.inflate(R.layout.color_picker,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		ColorPickerView picker=(ColorPickerView) view;
		picker.setOnColorCheckedListener(this);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onColorChecked(int color)
	{
		if(l!=null)l.onColorChecked(color);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		Dialog d= super.onCreateDialog(savedInstanceState);
		d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		return d;
	}

	
	public void setOnColorCheckedListener(ColorPickerView.OnColorCheckedListener l){
		this.l=l;
	}

	
}
