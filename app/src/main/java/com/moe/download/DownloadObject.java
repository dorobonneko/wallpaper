package com.moe.download;
import java.lang.reflect.*;
import android.database.sqlite.*;
import android.os.Parcelable;
import android.os.Parcel;

public class DownloadObject implements Parcelable
{

	
	
	private int _id;
	private String table;
	public DownloadObject(String tableName){
		table=tableName;
	}
	public DownloadObject(){
		table=this.getClass().getSimpleName();
	}
	protected DownloadObject(Parcel p){
		_id=p.readInt();
		table=p.readString();
	}
	public void setId(int id)
	{
		_id = id;
	}

	public int getId()
	{
		return _id==0?hashCode():_id;
	}

	public String getTableName()
	{
		return table;
	}
	public boolean save(){
		DownloadDatabase dd=DownloadDatabase.getInstance();
		return dd.save(this);
	}
	public void update(){
		DownloadDatabase dd=DownloadDatabase.getInstance();
		dd.update(this);
	}
	public void delete(){
		DownloadDatabase dd=DownloadDatabase.getInstance();
		dd.delete(this);
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
		p1.writeInt(_id);
		p1.writeString(table);
	}
	public static Parcelable.Creator<DownloadObject> CREATOR=new Parcelable.Creator<DownloadObject>(){

		@Override
		public DownloadObject createFromParcel(Parcel p1)
		{

			return new DownloadObject(p1);
		}

		@Override
		public DownloadObject[] newArray(int p1)
		{
			return new DownloadObject[p1];
		}

	};
}
