package com.moe.entity;
import android.os.Parcel;

public class FileManagerItem extends CollectionItem
{
	private String content,size;
	public FileManagerItem(){}
	public FileManagerItem(Parcel p){
		super(p);
		content=p.readString();
		size=p.readString();
	}

	

	public void setSize(String size)
	{
		this.size = size;
	}

	public String getSize()
	{
		return size;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getContent()
	{
		return content;
	}
	@Override
	public void writeToParcel(Parcel p1, int p2)
	{
		// TODO: Implement this method
		super.writeToParcel(p1, p2);
		p1.writeString(content);
		p1.writeString(size);
	}
	
}
