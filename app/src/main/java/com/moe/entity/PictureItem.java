package com.moe.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class PictureItem implements Parcelable
{
	private int id;
	private String title,url;

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
		p1.writeInt(id);
		p1.writeString(title);
		p1.writeString(url);
	}
	
	public static Parcelable.Creator<PictureItem> CREATOR=new Parcelable.Creator<PictureItem>(){

		@Override
		public PictureItem createFromParcel(Parcel p1)
		{
			// TODO: Implement this method
			return new PictureItem(p1);
		}

		@Override
		public PictureItem[] newArray(int p1)
		{
			// TODO: Implement this method
			return new PictureItem[p1];
		}
		
		};
		private PictureItem(Parcel p1){
			id=p1.readInt();
			title=p1.readString();
			url=p1.readString();
		}
		public PictureItem(){}
}
