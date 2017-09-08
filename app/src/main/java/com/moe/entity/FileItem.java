package com.moe.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class FileItem implements Parcelable
{

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getUrl()
	{
		return url;
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
		p1.writeString(name);
		p1.writeString(url);
		p1.writeString(desc);
	}
	public static Parcelable.Creator<FileItem> CREATOR=new Parcelable.Creator<FileItem>(){

		@Override
		public FileItem createFromParcel(Parcel p1)
		{
			// TODO: Implement this method
			return new FileItem(p1);
		}

		@Override
		public FileItem[] newArray(int p1)
		{
			// TODO: Implement this method
			return new FileItem[p1];
		}
		
		};
	private String name,url;
	private String desc;
	public FileItem(){}
	private FileItem(Parcel p){
		name=p.readString();
		url=p.readString();
		desc=p.readString();
	}
}
