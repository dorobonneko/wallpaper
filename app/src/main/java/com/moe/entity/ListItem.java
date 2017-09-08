package com.moe.entity;
import android.os.Parcelable;
import android.os.Parcel;
import java.util.List;
import java.util.ArrayList;

public class ListItem implements Parcelable
{
private String title,author,time,progress,bbs;
private int id,index;
private int classid,userid;
	private List<String> property;
	public void setBbs(String bbs){
		this.bbs=bbs;
	}
	public String getBbs(){
		return bbs;
	}
	public void setClassid(int classid)
	{
		this.classid = classid;
	}

	public int getClassid()
	{
		return classid;
	}

	public void setUserid(int userid)
	{
		this.userid = userid;
	}

	public int getUserid()
	{
		return userid;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getTime()
	{
		return time;
	}

	public void setProgress(String progress)
	{
		this.progress = progress;
	}

	public String getProgress()
	{
		return progress;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public int getIndex()
	{
		return index;
	}

	public void setProperty(List<String> property)
	{
		this.property = property;
	}

	public List<String> getProperty()
	{
		return property;
	}
	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p1, int p2)
	{
	p1.writeString(title);
	p1.writeString(author);
	p1.writeString(time);
	p1.writeString(progress);
	p1.writeInt(id);
	p1.writeInt(index);
	p1.writeStringList(property);
	p1.writeInt(userid);
	p1.writeInt(classid);
	p1.writeString(bbs);
	}
	public static final Parcelable.Creator<ListItem> CREATOR=new Parcelable.Creator<ListItem>(){

		@Override
		public ListItem createFromParcel(Parcel p1)
		{
			ListItem li=new ListItem();
			li.setTitle(p1.readString());
			li.setAuthor(p1.readString());
			li.setTime(p1.readString());
			li.setProgress(p1.readString());
			li.setId(p1.readInt());
			li.setIndex(p1.readInt());
			List<String> ls=new ArrayList<>();
			p1.readStringList(ls);
			li.setProperty(ls);
			li.setUserid(p1.readInt());
			li.setClassid(p1.readInt());
			li.setBbs(p1.readString());
			return li;
		}

		@Override
		public ListItem[] newArray(int p1)
		{
			return new ListItem[p1];
		}
		

    };
}
