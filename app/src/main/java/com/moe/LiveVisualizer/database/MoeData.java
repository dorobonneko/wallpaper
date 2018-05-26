package com.moe.LiveVisualizer.database;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

public class MoeData extends SQLiteOpenHelper
{
	public MoeData(Context context){
		super(context,"moe",null,3);
	}

	public int update(String key, String value)
	{
		Cursor c=query(key);
		if(c.getCount()==0)
			insert(key,value);
		else{
		SQLiteDatabase sql=getReadableDatabase();
		SQLiteStatement state=sql.compileStatement("update moe set value=? where key=?");
		state.bindAllArgsAsStrings(new String[]{value,key});
		state.executeUpdateDelete();
		state.close();
		//sql.close();
		}
		return 1;
	}

	public int delete(String key)
	{
		SQLiteDatabase sql=getReadableDatabase();
		SQLiteStatement state=sql.compileStatement("delete from moe where key=?");
		state.bindAllArgsAsStrings(new String[]{key});
		state.executeUpdateDelete();
		state.close();
		//sql.close();
		return 1;
	}

	public Cursor query(String key)
	{
		SQLiteDatabase sql=getReadableDatabase();
		synchronized(sql){
		return sql.query("moe",null,"key=?",new String[]{key},null,null,null);
		}
	}

	public void insert(String key, String value)
	{
		SQLiteDatabase sql=getReadableDatabase();
		SQLiteStatement state=sql.compileStatement("insert into moe values(?,?)");
		state.bindAllArgsAsStrings(new String[]{key,value});
		state.executeUpdateDelete();
		state.close();
		//sql.close();
	}

	@Override
	public void onCreate(SQLiteDatabase p1)
	{
		p1.execSQL("create table moe(key TEXT primary key,value TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
	{
		// TODO: Implement this method
	}
}
