package com.moe.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class ReplyItem implements Parcelable
{

	
	
	private String title,summary;
	private int id;


	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setSummary(String summary)
	{
		this.summary = summary;
	}

	public String getSummary()
	{
		return summary;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
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
		p1.writeString(summary);
		p1.writeInt(id);
	}
	public static Parcelable.Creator<ReplyItem> CREATOR=new Parcelable.Creator<ReplyItem>(){

		@Override
		public ReplyItem createFromParcel(Parcel p1)
		{
			// TODO: Implement this method
			return new ReplyItem(p1);
		}

		@Override
		public ReplyItem[] newArray(int p1)
		{
			// TODO: Implement this method
			return new ReplyItem[p1];
		}

};
		public ReplyItem(){}
		public ReplyItem(Parcel p1){
			title=p1.readString();
			summary=p1.readString();
			id=p1.readInt();
		}
}
