package com.moe.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class CollectionItem implements Parcelable
{
	private String title,summary;
	private int id,typeid;
	public CollectionItem(){}
	public CollectionItem(Parcel p){
		title=p.readString();
		summary=p.readString();
		id=p.readInt();
		typeid=p.readInt();
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public void setTypeid(int typeid)
	{
		this.typeid = typeid;
	}

	public int getTypeid()
	{
		return typeid;
	}

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
		p1.writeInt(typeid);
	}
	public static Parcelable.Creator<CollectionItem> CREATOR=new Parcelable.Creator<CollectionItem>(){

		@Override
		public CollectionItem createFromParcel(Parcel p1)
		{
			// TODO: Implement this method
			return new CollectionItem(p1);
		}

		@Override
		public CollectionItem[] newArray(int p1)
		{
			// TODO: Implement this method
			return new CollectionItem[p1];
		}
	};
		
}
