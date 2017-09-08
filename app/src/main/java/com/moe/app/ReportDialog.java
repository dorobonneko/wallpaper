package com.moe.app;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import com.moe.yaohuo.R;
import android.view.View;
import android.content.DialogInterface;
import android.widget.Spinner;
import java.util.ArrayList;
import android.widget.EditText;
import android.widget.ArrayAdapter;
public class ReportDialog implements DialogInterface.OnClickListener
{
	private ArrayList<String> list;
	private OnClickListener l;
	private Spinner spinner;
	private EditText why;
	private AlertDialog ad;
	public ReportDialog(Context context,OnClickListener l){
		this.l=l;
		View v=LayoutInflater.from(context).inflate(R.layout.report_view,null);
		spinner=(Spinner)v.findViewById(R.id.spinner);
		why=(EditText)v.findViewById(R.id.report_why);
		list=new ArrayList<>();
		list.add("外站网址");
		list.add("色情信息");
		list.add("反动信息");
		list.add("恶意刷币");
		list.add("其它违规");
		spinner.setAdapter(new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,list));
		
		ad=new AlertDialog.Builder(context).setView(v).setTitle("举报贴子")
		.setPositiveButton("确定",this).setNegativeButton("取消",null).create();
	}

	
	@Override
	public void onClick(DialogInterface p1, int p2)
	{
		if(l!=null)l.onClick(list.get(spinner.getSelectedItemPosition()),why.getText().toString().trim());
	}

public abstract interface OnClickListener{
	void onClick(String type,String why);
}
	public void show(){
		ad.show();
	}
}
