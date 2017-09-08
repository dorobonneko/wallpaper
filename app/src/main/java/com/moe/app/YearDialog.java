package com.moe.app;
import android.widget.TextView;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import java.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import com.moe.yaohuo.R;
import android.content.DialogInterface;
public class YearDialog implements View.OnClickListener,DialogInterface.OnClickListener
{
	private TextView year;
	private AlertDialog ad;
	private Calendar c;
	public YearDialog(Context context)
	{
		c = Calendar.getInstance();
		View v=LayoutInflater.from(context).inflate(R.layout.year_picker_view, null);
		year = (TextView) v.findViewById(R.id.year);
		v.findViewById(R.id.left).setOnClickListener(this);
		v.findViewById(R.id.right).setOnClickListener(this);
		update();
		ad = new AlertDialog.Builder(context).setView(v).setTitle("日期选择").setPositiveButton("确定", this).setNegativeButton("取消", null).create();
	}
	private void update()
	{
		year.setText(c.get(c.YEAR) + "年" + (c.get(c.MONTH) + 1) + "月");

	}

	@Override
	public void onClick(View p1)
	{
		switch (p1.getId())
		{
			case R.id.left:
				c.set(c.get(c.MONTH) == 0 ?c.get(c.YEAR) - 1: c.get(c.YEAR), c.get(c.MONTH) == 0 ?12: c.get(c.MONTH), 0);
				break;
			case R.id.right:
				c.set(c.get(c.MONTH) == 11 ?c.get(c.YEAR) + 1: c.get(c.YEAR), c.get(c.MONTH) == 11 ?1: c.get(c.MONTH)+2, 0);

				break;
		}
		update();
	}

	@Override
	public void onClick(DialogInterface p1, int p2)
	{
		if (ool != null)ool.ok(c.get(c.YEAR), c.get(c.MONTH));
	}
	public void show()
	{
		ad.show();
	}
	private OnOkListener ool;
	public void setOnOkListener(OnOkListener l)
	{
		ool = l;
	}
	public abstract interface OnOkListener
	{
		void ok(int year, int month);
	}
}
