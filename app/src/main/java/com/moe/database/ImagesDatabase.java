package com.moe.database;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteStatement;
import java.util.List;
import android.database.Cursor;
import java.util.ArrayList;

public class ImagesDatabase extends SQLiteOpenHelper
{
	private static ImagesDatabase shd;
	private SQLiteDatabase sql;
	private ImagesDatabase(Context context){
		super(context.getApplicationContext(),"image",null,3);
		sql=getReadableDatabase();
	}
	public static ImagesDatabase getInstance(Context context){
		if(shd==null)shd=new ImagesDatabase(context);
		return shd;
	}
	@Override
	public void onCreate(SQLiteDatabase p1)
	{
		p1.execSQL("create table images(key TEXT primary key,time INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
	{

	}
	public void insert(String key){
		SQLiteStatement state=sql.compileStatement("insert into images values(?,?)");
		state.bindString(1,key);
		state.bindLong(2,System.currentTimeMillis());
		try{
			state.executeInsert();
		}catch(Exception e){
			state.close();
			state=sql.compileStatement("update images set time=? where key=?");
			state.bindLong(1,System.currentTimeMillis());
			state.bindString(2,key);
			state.executeUpdateDelete();
		}
		state.close();
	}
	public List<String> query(){
		Cursor c=sql.query("images",new String[]{"key"},null,null,null,null,"time desc");
		List<String> list=new ArrayList<>();
		while(c.moveToNext()){
			list.add(c.getString(0));
		}
		c.close();
		return list;
	}
	public void clear(){
		SQLiteStatement state=sql.compileStatement("delete from images");
		state.executeUpdateDelete();
		state.close();
	}
	public void delete(String url){
		SQLiteStatement state=sql.compileStatement("delete from images where key=?");
		state.bindString(1,url);
		state.executeUpdateDelete();
		state.close();
	}
}
