package com.moe.LiveVisualizer.service;
import android.content.ContentProvider;
import android.net.Uri;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import com.moe.LiveVisualizer.database.MoeData;

public class SharedPreferences extends ContentProvider
{
	public static final String URI="content://moe/moe";
	private MoeData data;
	@Override
	public boolean onCreate()
	{
		data=new MoeData(getContext());
		return true;
	}

	@Override
	public void shutdown()
	{
		// TODO: Implement this method
		super.shutdown();
		data.close();
	}

	@Override
	public Cursor query(Uri p1, String[] p2, String p3, String[] p4, String p5)
	{
		return data.query(p1.getQueryParameter("key"));
	}

	@Override
	public String getType(Uri p1)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Uri insert(Uri p1, ContentValues p2)
	{
		data.insert(p1.getQueryParameter("key"),p1.getQueryParameter("value"));
		return p1;
	}

	@Override
	public int delete(Uri p1, String p2, String[] p3)
	{
		getContext().getContentResolver().notifyChange(p1,null);
		return data.delete(p1.getQueryParameter("key"));
	}

	@Override
	public int update(Uri p1, ContentValues p2, String p3, String[] p4)
	{
		getContext().getContentResolver().notifyChange(p1,null);
		return data.update(p1.getQueryParameter("key"),p1.getQueryParameter("value"));
	}
}
