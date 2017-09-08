package com.moe.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class FriendItem implements Parcelable
{
	private UserItem ui;
	private int id,type;
	private String time;
	public FriendItem(){}
	public FriendItem(Parcel p){
		ui=p.readParcelable(UserItem.class.getClassLoader());
		id=p.readInt();
		type=p.readInt();
		time=p.readString();
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getTime()
	{
		return time;
	}
	public void setUi(UserItem ui)
	{
		this.ui = ui;
	}

	public UserItem getUi()
	{
		return ui;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}
	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p1, int p2)
	{
		p1.writeParcelable(ui,p2);
		p1.writeInt(id);
		p1.writeInt(type);
		p1.writeString(time);
	}
	public static Parcelable.Creator<FriendItem> CREATOR=new Parcelable.Creator<FriendItem>(){

		@Override
		public FriendItem createFromParcel(Parcel p1)
		{
			
			return new FriendItem(p1);
		}

		@Override
		public FriendItem[] newArray(int p1)
		{
			return new FriendItem[p1];
		}

		};
}
