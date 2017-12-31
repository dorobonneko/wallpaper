package com.moe.download;
import android.database.sqlite.SQLiteOpenHelper;
import java.lang.reflect.Field;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.database.Cursor;
import android.database.SQLException;


public class DownloadDatabase extends SQLiteOpenHelper
{
	private static DownloadDatabase dd;
	private DownloadDatabase(Context context)
	{
		super(context.getApplicationContext(),"download",null,3);
	}

	protected void update(DownloadObject p0)
	{
		SQLiteDatabase sql=dd.getWritableDatabase();
		Field[] field=p0.getClass().getDeclaredFields();
		field = Filter.filter(field,Field.class,new Filter.Filter<Field>(){

				@Override
				public boolean isAccept(Field item)
				{
					return ClassUtils.isAccept(item);
				}
			});
		
		StringBuffer keys=new StringBuffer();
		for (Field field_:field)
		{
			keys.append(field_.getName()).append("=?,");
		}
		keys.deleteCharAt(keys.length()-1);
		SQLiteStatement state=sql.compileStatement("update "+p0.getTableName()+" set "+keys.toString()+" where _id=?");
		state.acquireReference();
		for (int i=0;i<field.length;i++)
		{
			FieldUtils.bind(state,field[i],p0,i+1);
		}
		state.bindLong(field.length+1,p0.getId());
		state.executeUpdateDelete();
		state.close();
		state.releaseReference();
		
	}

	protected void delete(DownloadObject p0)
	{
		SQLiteDatabase sql=getReadableDatabase();
		SQLiteStatement state=sql.compileStatement("delete from "+p0.getTableName()+" where _id=?");
		state.bindLong(1,p0.getId());
		state.executeUpdateDelete();
		state.close();
	}

	protected void save(DownloadObject p0)
	{
		SQLiteDatabase sql=dd.getReadableDatabase();
		Field[] field=p0.getClass().getDeclaredFields();
		field = Filter.filter(field,Field.class,new Filter.Filter<Field>(){

				@Override
				public boolean isAccept(Field item)
				{
					return ClassUtils.isAccept(item);
				}
			});
		if (!dd.tableIsExist(p0.getTableName(),sql))
		{
			StringBuffer fields=new StringBuffer();
			for (Field field_:field)
				fields.append(field_.getName()).append(" ").append(ClassUtils.getType(field_)).append(",");
			fields.append("_id Integer Primary Key");
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
		keys.append("_id");
		values.append("?");
		SQLiteStatement state=sql.compileStatement("insert into "+p0.getTableName()+"("+keys.toString()+") values("+values.toString()+")");
		for (int i=0;i<field.length;i++)
		{
			FieldUtils.bind(state,field[i],p0,i+1);
		}
		state.bindLong(field.length+1,p0.getId());
		state.executeInsert();
		state.close();
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
