package com.moe.entity;
import com.moe.download.Download;
import com.moe.services.DownloadService;
import android.os.Parcelable;
import android.os.Parcel;

public class DownloadItem implements Parcelable
{

	
		public DownloadItem(){}
		public DownloadItem(Parcel p){
			title=p.readString();
			url=p.readString();
			dir=p.readString();
			referer=p.readString();
			total=p.readLong();
			state=p.readInt();
		}
	private String title,dir,url;
	private long total;
	private int state;
private String referer;
	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setDir(String dir)
	{
		this.dir = dir;
	}

	public String getDir()
	{
		return dir;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getUrl()
	{
		return url;
	}

	public void setState(int current)
	{
		this.state = current;
	}

	public int getState()
	{
		return state;
	}

	public void setTotal(long total)
	{
		this.total = total;
	}

	public long getTotal()
	{
		return total;
	}
	public boolean isLoading(){
		switch(state){
			case DownloadService.State.WAITING:
			case DownloadService.State.LOADING:
			return true;
			default:
			return false;
		}
	}
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Download)
			return ((Download)obj).getDownloadItem().getUrl().equals(url);
		else if(obj instanceof DownloadItem)
		return ((DownloadItem)obj).getUrl().equals(url);
		else
		return this==obj;
	}
	public void setReferer(String referer)
	{
		this.referer = referer;
	}

	public String getReferer()
	{
		return referer;
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
		p1.writeString(url);
		p1.writeString(dir);
		p1.writeString(referer);
		p1.writeLong(total);
		p1.writeInt(state);
	}
	public static Parcelable.Creator<DownloadItem> CREATOR=new Parcelable.Creator<DownloadItem>(){

		@Override
		public DownloadItem createFromParcel(Parcel p1)
		{

			return new DownloadItem(p1);
		}

		@Override
		public DownloadItem[] newArray(int p1)
		{
			return new DownloadItem[p1];
		}

	};
	}
