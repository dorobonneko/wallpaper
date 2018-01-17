package com.moe.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class MsgItem implements Parcelable
{
	private int id;
	private String title,from,time;
	private int view;
	
	public MsgItem()
	{}
	public MsgItem(Parcel p)
	{
		id = p.readInt();
		title = p.readString();
		from = p.readString();
		time = p.readString();
		view = p.readInt();
	}
	public void setView(int view)
	{
		this.view = view;
	}

	public int getView()
	{
		return view;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setFrom(String from)
	{
		this.from = from;
	}

	public String getFrom()
	{
		return from;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getTime()
	{
		return time;
	}

	@Override
	public int describeContents()
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Integer)
			return id==((Integer)obj).intValue();
		else if(obj instanceof MsgItem)
			return id==((MsgItem)obj).getId();
		return super.equals(obj);
	}
	
	@Override
	public void writeToParcel(Parcel p1, int p2)
	{
		p1.writeInt(id);
		p1.writeString(title);
		p1.writeString(from);
		p1.writeString(time);
		p1.writeInt(view);
	}
	public static Parcelable.Creator<MsgItem> CREATOR=new Parcelable.Creator<MsgItem>(){

		@Override
		public MsgItem createFromParcel(Parcel p1)
		{
			// TODO: Implement this method
			return new MsgItem(p1);
		}

		@Override
		public MsgItem[] newArray(int p1)
		{
			// TODO: Implement this method
			return new MsgItem[p1];
		}

	};
	
}
