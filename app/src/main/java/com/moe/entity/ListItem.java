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
private int anime;
	private List<String> property;
	public ListItem(){}
	public ListItem(Parcel p1){
		setTitle(p1.readString());
		setAuthor(p1.readString());
		setTime(p1.readString());
		setProgress(p1.readString());
		setId(p1.readInt());
		setIndex(p1.readInt());
		List<String> ls=new ArrayList<>();
		p1.readStringList(ls);
		setProperty(ls);
		setUserid(p1.readInt());
		setClassid(p1.readInt());
		setBbs(p1.readString());
		setAnime(p1.readInt()==1);
	}
	public void setAnime(boolean anime)
	{
		this.anime = anime?1:0;
	}

	public boolean isAnime()
	{
		return anime==1;
	}
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
	p1.writeInt(anime);
	}
	public static final Parcelable.Creator<ListItem> CREATOR=new Parcelable.Creator<ListItem>(){

		@Override
		public ListItem createFromParcel(Parcel p1)
		{
			
			return new ListItem(p1);
		}

		@Override
		public ListItem[] newArray(int p1)
		{
			return new ListItem[p1];
		}
		

    };
}
