package com.moe.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class FloorItem implements Parcelable
{
	private String content,time;
	private int floor;
	private UserItem user;
	private int money;
	private byte sendmoney,delete;
	private long reid;
	private String name;
	private int uid;
	public FloorItem(){}
	public FloorItem(Parcel p1){
		content=p1.readString();
		time=p1.readString();
		floor=p1.readInt();
		user=p1.readParcelable(UserItem.class.getClassLoader());
		money=p1.readInt();
		sendmoney=p1.readByte();
		delete=p1.readByte();
		reid=p1.readLong();
		name=p1.readString();
		uid=p1.readInt();
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setUid(int uid)
	{
		this.uid = uid;
	}

	public int getUid()
	{
		return uid;
	}
	public void setReid(long reid)
	{
		this.reid = reid;
	}

	public long getReid()
	{
		return reid;
	}

	
	public void setSendmoney(boolean sendmoney)
	{
		this.sendmoney = (byte)(sendmoney?1:0);
	}

	public boolean isSendmoney()
	{
		return sendmoney==1;
	}

	public void setDelete(boolean delete)
	{
		this.delete = (byte)(delete?1:0);
	}

	public boolean isDelete()
	{
		return delete==1;
	}

	public void setMoney(int money)
	{
		this.money = money;
	}

	public int getMoney()
	{
		return money;
	}

	@Override
	public int describeContents()
	{
		// TODO: Implement this method
		return 0;
	}
	
	
	public void setContent(String content)
	{
		this.content = content;
	}

	public String getContent()
	{
		return content;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getTime()
	{
		return time;
	}

	public void setFloor(int floor)
	{
		this.floor = floor;
	}

	public int getFloor()
	{
		return floor;
	}

	public void setUser(UserItem user)
	{
		this.user = user;
	}

	public UserItem getUser()
	{
		return user;
	}

	@Override
	public void writeToParcel(Parcel p1, int p2)
	{
		p1.writeString(content);
		p1.writeString(time);
		p1.writeInt(floor);
		p1.writeParcelable(user,p2);
		p1.writeInt(money);
		p1.writeByte(sendmoney);
		p1.writeByte(delete);
		p1.writeLong(reid);
		p1.writeString(name);
		p1.writeInt(uid);
	}
	public static Parcelable.Creator<FloorItem> CREATOR=new Parcelable.Creator<FloorItem>(){

		@Override
		public FloorItem createFromParcel(Parcel p1)
		{
			// TODO: Implement this method
			return new FloorItem(p1);
		}

		@Override
		public FloorItem[] newArray(int p1)
		{
			// TODO: Implement this method
			return new FloorItem[p1];
		}
	};
	

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof FloorItem)
			return ((FloorItem)obj).getFloor()==floor;
		return this==obj;
	}
	
	}
	
