package com.moe.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class BbsItem implements Parcelable
{
	private String title,progress;
	private int total,classid;
	private String imgurl;
	private String action="search";
	private String key,type;

	

	public void setType(String type)
	{
		this.type = type;
	}

	public String getType()
	{
		return type;
	}
	public void setKey(String key)
	{
		this.key = key;
	}

	public String getKey()
	{
		return key;
	}
	public void setAction(String action)
	{
		this.action = action;
	}

	public String getAction()
	{
		return action;
	}
	public void setImgurl(String imgurl)
	{
		this.imgurl = imgurl;
	}

	public String getImgurl()
	{
		return imgurl;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setProgress(String progress)
	{
		this.progress = progress;
	}

	public String getProgress()
	{
		return progress;
	}

	public void setTotal(int total)
	{
		this.total = total;
	}

	public int getTotal()
	{
		return total;
	}

	public void setClassid(int classid)
	{
		this.classid = classid;
	}

	public int getClassid()
	{
		return classid;
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
		p1.writeString(progress);
		p1.writeInt(total);
		p1.writeInt(classid);
		p1.writeString(imgurl);
		p1.writeString(action);
		p1.writeString(key);
		p1.writeString(type);
	}
	public static Parcelable.Creator<? extends BbsItem> CREATOR=new Parcelable.Creator<BbsItem>(){

		@Override
		public BbsItem createFromParcel(Parcel p1)
		{
			return new BbsItem(p1);
		}

		@Override
		public BbsItem[] newArray(int p1)
		{
			return new BbsItem[p1];
		}
	};
	public BbsItem(Parcel p1){
		setTitle(p1.readString());
		setProgress(p1.readString());
		setTotal(p1.readInt());
		setClassid(p1.readInt());
		setImgurl(p1.readString());
		setAction(p1.readString());
		setKey(p1.readString());
		setType(p1.readString());
	}
	public BbsItem()
	{}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof BbsItem)
			return ((BbsItem)obj).getClassid()==classid;
		return super.equals(obj);
	}
	
}
