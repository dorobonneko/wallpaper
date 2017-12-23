package com.moe.download;
import java.lang.reflect.*;
import android.database.sqlite.*;

public class DownloadObject
{
	private String table;
	public DownloadObject(String tableName){
		table=tableName;
	}
	public DownloadObject(){
		table=this.getClass().getSimpleName();
	}

	public String getTableName()
	{
		return table;
	}
	public void save(){
		DownloadDatabase dd=DownloadDatabase.getInstance();
		dd.save(this);
	}
	public void update(){
		
	}
	public void delete(){
		
	}
}
