package com.moe.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class UbbItem implements Parcelable
{
	private String title,data;
	private int mode;
	public UbbItem(){}
	public UbbItem(Parcel p)
	{
		title=p.readString();
		data=p.readString();
		mode=p.readInt();
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public String getData()
	{
		return data;
	}

	public void setMode(int mode)
	{
		this.mode = mode;
	}

	public int getMode()
	{
		return mode;
	}
	@Override
	public int describeContents()
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p1, int p2)
	{
		p1.writeString(title);
		p1.writeString(data);
		p1.writeInt(mode);
	}
	
	public static Parcelable.Creator<UbbItem> CREATOR=new Parcelable.Creator<UbbItem>(){

		@Override
		public UbbItem createFromParcel(Parcel p1)
		{
			// TODO: Implement this method
			return new UbbItem(p1);
		}

		@Override
		public UbbItem[] newArray(int p1)
		{
			// TODO: Implement this method
			return new UbbItem[p1];
		}
		
		};
}
