package com.moe.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class MoneyItem implements Parcelable
{
	private String title,money,time,who;
	public MoneyItem(){}
	public MoneyItem(Parcel p){
		title=p.readString();
		money=p.readString();
		time=p.readString();
		who=p.readString();
	}
	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setMoney(String money)
	{
		this.money = money;
	}

	public String getMoney()
	{
		return money;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getTime()
	{
		return time;
	}

	public void setWho(String who)
	{
		this.who = who;
	}

	public String getWho()
	{
		return who;
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
		p1.writeString(money);
		p1.writeString(time);
		p1.writeString(who);
	}
	
	
	public static Parcelable.Creator<MoneyItem> CREATOR=new Parcelable.Creator<MoneyItem>(){

		@Override
		public MoneyItem createFromParcel(Parcel p1)
		{
			return null;
		}

		@Override
		public MoneyItem[] newArray(int p1)
		{
			return new MoneyItem[p1];
		}
	};
		
}
