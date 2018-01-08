package com.moe.download;
import java.util.*;
import android.os.*;
import android.database.sqlite.*;
import android.database.*;
import java.lang.reflect.*;
import java.util.concurrent.*;


public class DownloadQuery<T extends DownloadObject> implements Handler.Callback
{
	private String table;
	private Class class_;
	private Handler handler;
	public DownloadQuery(Class<T> class_)
	{
		this(class_,class_.getSimpleName());
	}
	public DownloadQuery(Class<T> class_, String table)
	{
		this.table = table;
		this.class_ = class_;
		handler = new Handler(this);
	}

	public void doQuery(QuerySql qs)
	{
		qs.setTable(table);
		new QueryThread(qs).start();
	}
	public void doQuery(String sql, Listener<T> listener)
	{
		QuerySql qs=new QuerySql(sql);
		qs.setListener(listener);
		qs.setTable(table);
		new QueryThread(qs).start();
	}

	@Override
	public boolean handleMessage(Message p1)
	{
		switch (p1.what)
		{
			case 0:
				if (p1.obj instanceof Object[])
				{
					Object[] o=(Object[])p1.obj;
					QuerySql qs=(QuerySql) o[0];
					qs.getListener().done(qs,(List)o[1]);
				}
				else
				{
					QuerySql qs=(QuerySql) p1.obj;
					qs.getListener().done(qs,null);
				}

				break;
		}
		return true;
	}


	public static abstract interface Listener<T extends DownloadObject>
	{
		public void done(QuerySql<T> qs, List<T> t);
	}
	class QueryThread extends Thread 
	{
		private QuerySql qs;
		public QueryThread(QuerySql qs)
		{
			this.qs = qs;
		}
		@Override
		public void run()
		{
			DownloadDatabase dd=DownloadDatabase.getInstance();
			SQLiteDatabase sql=dd.getReadableDatabase();
			Cursor cursor=null;
			try
			{
				cursor = sql.rawQuery(qs.getSql(),null);
			}
			catch (Exception e)
			{}
			if (cursor!=null)
			{
				List<T> list=new ArrayList<>();
				while (cursor.moveToNext())
				{
					try
					{
						T item=null;
						try
						{
							Constructor con=class_.getConstructor(String.class);
							item = (T) con.newInstance(new Object[]{table});
						}
						catch (Exception e)
						{
							item = (T)class_.newInstance();
						}
						for (String name:cursor.getColumnNames())
						{
							try
							{
								Field field=FieldUtils.getField(class_,name);
								if(field==null)continue;
								field.setAccessible(true);
								switch (field.getType().getSimpleName())
								{
									case "String":
										field.set(item,cursor.getString(cursor.getColumnIndex(name)));
										break;
									case "long":
										field.setLong(item,cursor.getLong(cursor.getColumnIndex(name)));
										break;
									case "int":
										field.setInt(item,cursor.getInt(cursor.getColumnIndex(name)));
										break;
									case "char":
										field.setChar(item,(char)cursor.getInt(cursor.getColumnIndex(name)));
										break;
									case "boolean":
										field.setBoolean(item,cursor.getInt(cursor.getColumnIndex(name))==1);
										break;
									case "byte":
										field.setByte(item,(byte)cursor.getShort(cursor.getColumnIndex(name)));
										break;
									case "short":
										field.setShort(item,cursor.getShort(cursor.getColumnIndex(name)));
										break;
									case "float":
										field.setFloat(item,(float)cursor.getDouble(cursor.getColumnIndex(name)));
										break;
									case "double":
										field.setDouble(item,cursor.getDouble(cursor.getColumnIndex(name)));
										break;
								}
							}
							catch (Exception e)
							{throw new RuntimeException("table is not match with "+class_.getSimpleName());}
						}
						list.add(item);
					}
					catch (Exception e)
					{}

				}
				if (cursor!=null)
					cursor.close();
				handler.obtainMessage(0,new Object[]{qs,list}).sendToTarget();

			}

		}}}
