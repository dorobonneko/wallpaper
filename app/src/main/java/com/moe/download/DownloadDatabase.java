package com.moe.download;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteStatement;
import com.moe.entity.DownloadItem;
import java.util.List;
import com.moe.services.DownloadService;
import android.database.Cursor;
import java.util.ArrayList;
import android.os.Environment;
import java.io.File;
import com.moe.utils.DocumentFileUtils;
import android.support.v4.provider.DocumentFile;
import android.net.Uri;
import java.lang.reflect.*;
import java.util.*;

public class DownloadDatabase extends SQLiteOpenHelper
{
	private Context context;
	private SQLiteDatabase sql;
	private static DownloadDatabase dd;
	private DownloadDatabase(Context context)
	{
		super(context.getApplicationContext(),"download",null,3);
		sql = getReadableDatabase();
		this.context = context.getApplicationContext();
	}

	protected void save(DownloadObject p0)
	{
		SQLiteDatabase sql=dd.getReadableDatabase();
		Field[] field=p0.getClass().getDeclaredFields();
		field = Filter.filter(field,Field.class,new Filter.Filter<Field>(){

				@Override
				public boolean isAccept(Field item)
				{
					return ClassToType.isAccess(item);
				}
			});
		if (!dd.tableIsExist(p0.getTableName(),sql))
		{
			StringBuffer fields=new StringBuffer();
			for (Field field_:field)
				fields.append(field_.getName()).append(" ").append(ClassToType.getType(field_)).append(",");
			fields.delete(fields.length()-1,fields.length());
			sql.execSQL("create table "+p0.getTableName()+"("+fields.toString()+")");
		}
		StringBuffer keys=new StringBuffer();
		StringBuffer values=new StringBuffer();
		for (Field field_:field)
		{
			//if(ClassToType.isAccess(field_)){
			keys.append(field_.getName()).append(",");
			values.append("?,");
		}
		keys.delete(keys.length()-1,keys.length());
		values.delete(values.length()-1,values.length());
		SQLiteStatement state=sql.compileStatement("insert into "+p0.getTableName()+"("+keys.toString()+") values("+values.toString()+")");
		for (int i=0;i<field.length;i++)
		{
			//if(ClassToType.isAccess(field[i]))
			try
			{
				field[i].setAccessible(true);
				switch (field[i].getType().getSimpleName())
				{
					case "String":
						state.bindString(i+1,field[i].get(p0)==null?"":field[i].get(p0).toString());
						break;
					case "long":
						state.bindLong(i+1,field[i].getLong(p0));
						break;
					case "int":
						state.bindLong(i+1,field[i].getInt(p0));
						break;
					case "char":
						state.bindLong(i+1,field[i].getChar(p0));
						break;
					case "boolean":
						state.bindLong(i+1,field[i].getBoolean(p0)?1:0);
						break;
					case "byte":
						state.bindLong(i+1,field[i].getByte(p0));
						break;
					case "short":
						state.bindLong(i+1,field[i].getShort(p0));
						break;
					case "float":
						state.bindDouble(i+1,field[i].getFloat(p0));
						break;
					case "double":
						state.bindDouble(i+1,field[i].getDouble(p0));
						break;
				}
			}
			catch (IllegalAccessException e)
			{}
			catch (IllegalArgumentException e)
			{}
		}
		state.executeInsert();
		state.close();
		sql.close();
	}
	protected boolean tableIsExist(String tableName, SQLiteDatabase db)
	{
		boolean result = false;
		if (tableName==null)
		{
			return false;
		}
		Cursor cursor = null;
		try
		{
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
				+tableName.trim()+"' ";
			cursor = db.rawQuery(sql,null);
			if (cursor.moveToNext())
			{
				result = cursor.getInt(0)>0;
			}

		}
		catch (Exception e)
		{
		}
		if (cursor!=null)cursor.close();
		return result;
	}
	/*
	 public void delete(DownloadItem di){
	 SQLiteStatement state=sql.compileStatement("delete from download where url=?");
	 state.acquireReference();
	 state.bindString(1,di.getUrl());
	 state.executeUpdateDelete();
	 state.close();
	 state.releaseReference();
	 }
	 public void delete(final DownloadItem remove, boolean p1)
	 {
	 delete(remove);
	 if(p1){
	 new Thread(){
	 public void run(){
	 File file=new File(remove.getDir());
	 if(remove.getDir().startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())){
	 file.delete();
	 }else{
	 DocumentFileUtils.getDocumentFilePath(DocumentFile.fromTreeUri(context,Uri.parse(setting.getString("sdcard",null))),file).delete();
	 }
	 }
	 }.start();
	 }
	 }
	 public List<DownloadItem> query(boolean success){
	 List<DownloadItem> ldi=new ArrayList<>();
	 Cursor cursor=sql.query("download",null,"state"+(success?"=?":"!=?"),new String[]{DownloadService.State.SUCCESS+""},null,null,null);
	 while(cursor.moveToNext()){
	 DownloadItem di=new DownloadItem();
	 di.setTitle(cursor.getString(0));
	 di.setUrl(cursor.getString(1));
	 di.setState(cursor.getInt(2)==DownloadService.State.SUCCESS?DownloadService.State.SUCCESS:0);
	 di.setTotal(cursor.getLong(3));
	 di.setDir(cursor.getString(4));
	 di.setReferer(cursor.getString(5));
	 di.setType(cursor.getString(6));
	 di.setTime(cursor.getLong(7));
	 di.setCurrent(cursor.getLong(8));
	 ldi.add(di);
	 }
	 cursor.close();
	 return ldi;
	 }
	 public boolean insert(DownloadItem di){
	 boolean flag=true;
	 SQLiteStatement state=sql.compileStatement("insert into download values(?,?,?,?,?,?,?,?,?)");
	 state.acquireReference();
	 state.bindString(1,di.getTitle());
	 state.bindString(2,di.getUrl());
	 state.bindLong(3,0);
	 state.bindLong(4,di.getTotal());
	 state.bindString(5,di.getDir());
	 state.bindString(6,di.getReferer());
	 state.bindString(7,di.getType()==null?"":di.getType());
	 state.bindLong(8,System.currentTimeMillis());
	 state.bindLong(9,di.getCurrent());
	 try{
	 state.executeInsert();
	 }catch(Exception e){
	 flag=false;
	 }
	 state.close();
	 state.releaseReference();
	 return flag;
	 }
	 public void updateState(String url, int p1)
	 {
	 SQLiteStatement state=sql.compileStatement("update download set state=? where url=?");
	 state.acquireReference();
	 state.bindLong(1,p1);
	 state.bindString(2,url);
	 state.executeUpdateDelete();
	 state.close();
	 state.releaseReference();
	 }
	 public void updateCurrent(String url, long length)
	 {
	 SQLiteStatement state=sql.compileStatement("update download set current=? where url=?");
	 state.acquireReference();
	 state.bindLong(1,length);
	 state.bindString(2,url);
	 state.executeUpdateDelete();
	 state.close();
	 state.releaseReference();
	 }
	 public void updateTotal(String url, long length)
	 {
	 SQLiteStatement state=sql.compileStatement("update download set total=? where url=?");
	 state.acquireReference();
	 state.bindLong(1,length);
	 state.bindString(2,url);
	 state.executeUpdateDelete();
	 state.close();
	 state.releaseReference();
	 }*/
	public static void init(Context context)
	{
		if (dd==null)dd = new DownloadDatabase(context);
	}
	protected static DownloadDatabase getInstance()
	{
		if (dd==null)throw new NullPointerException("数据库未初始化");
		return dd;
	}
	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
	{

	}

	@Override
	public void onCreate(SQLiteDatabase p1)
	{
		//p1.execSQL("create table download(id integer primary key,title TEXT,url TEXT,state INTEGER,total INTEGER,dir TEXT,referer TEXT,type TEXT,time INTEGER,current INTEGER,cookie TEXT)");
		//p1.execSQL("create table threads(id integer,threadid integer,current integer,end integer,state integer)");
	}



}
