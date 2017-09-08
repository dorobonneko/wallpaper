package com.moe.database;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.database.Cursor;
import java.util.List;
import java.util.ArrayList;

public class SearchHistoryDatabase extends SQLiteOpenHelper
{
	private static SearchHistoryDatabase shd;
	private SQLiteDatabase sql;
	private SearchHistoryDatabase(Context context){
		super(context.getApplicationContext(),"history",null,3);
		sql=getReadableDatabase();
	}
	public static SearchHistoryDatabase getInstance(Context context){
		if(shd==null)shd=new SearchHistoryDatabase(context);
		return shd;
	}
	@Override
	public void onCreate(SQLiteDatabase p1)
	{
		p1.execSQL("create table search(key TEXT primary key,time INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
	{
		
	}
	public void insert(String key){
		SQLiteStatement state=sql.compileStatement("insert into search values(?,?)");
		state.bindString(1,key);
		state.bindLong(2,System.currentTimeMillis());
		try{
		state.executeInsert();
		}catch(Exception e){
			state.close();
			state=sql.compileStatement("update search set time=? where key=?");
			state.bindLong(1,System.currentTimeMillis());
			state.bindString(2,key);
			state.executeUpdateDelete();
		}
		state.close();
	}
	public List<String> query(String key){
		Cursor c=sql.query("search",new String[]{"key"},"key like ?",new String[]{"%"+key+"%"},null,null,"time desc"+(key.length()>0?" limit 0,10":""));
		List<String> list=new ArrayList<>();
		while(c.moveToNext()){
			list.add(c.getString(0));
		}
		c.close();
		return list;
	}
	public void clear(){
		SQLiteStatement state=sql.compileStatement("delete from search");
		state.executeUpdateDelete();
		state.close();
	}
}
