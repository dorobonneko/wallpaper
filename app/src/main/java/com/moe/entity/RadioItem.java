package com.moe.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class RadioItem implements Parcelable
{

	private int vid,progress,count;
	private String title;

	public void setVid(int vid)
	{
		this.vid = vid;
	}

	public int getVid()
	{
		return vid;
	}

	public void setProgress(int progress)
	{
		this.progress = progress;
	}

	public int getProgress()
	{
		return progress;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public int getCount()
	{
		return count;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
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
		p1.writeInt(progress);
		p1.writeInt(vid);
		p1.writeInt(count);
		p1.writeString(title);
	}
	public static Parcelable.Creator<RadioItem> CREATOR=new Parcelable.Creator<RadioItem>(){

		@Override
		public RadioItem createFromParcel(Parcel p1)
		{
			return null;
		}

		@Override
		public RadioItem[] newArray(int p1)
		{
			return new RadioItem[p1];
		}
	};
		private RadioItem(Parcel p){
			progress=p.readInt();
			vid=p.readInt();
			count=p.readInt();
			title=p.readString();
		}
		public RadioItem(){}
}
