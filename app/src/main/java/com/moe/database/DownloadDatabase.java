package com.moe.database;
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

public class DownloadDatabase extends SQLiteOpenHelper
{
	private Context context;
	private SQLiteDatabase sql;
	private static DownloadDatabase dd;
	private SharedPreferences setting;
	private DownloadDatabase(Context context){
		super(context.getApplicationContext(),"download",null,3);
		sql=getReadableDatabase();
		this.context=context.getApplicationContext();
		setting=context.getSharedPreferences("setting",0);
	}
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
			ldi.add(di);
		}
		cursor.close();
		return ldi;
	}
	public boolean insert(DownloadItem di){
		boolean flag=true;
		SQLiteStatement state=sql.compileStatement("insert into download values(?,?,?,?,?,?)");
		state.acquireReference();
		state.bindString(1,di.getTitle());
		state.bindString(2,di.getUrl());
		state.bindLong(3,0);
		state.bindLong(4,di.getTotal());
		state.bindString(5,di.getDir());
		state.bindString(6,di.getReferer());
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

	public void updateTotal(String url, long length)
	{
		SQLiteStatement state=sql.compileStatement("update download set total=? where url=?");
		state.acquireReference();
		state.bindLong(1,length);
		state.bindString(2,url);
		state.executeUpdateDelete();
		state.close();
		state.releaseReference();
	}
	public static DownloadDatabase getInstance(Context context){
		if(dd==null)dd=new DownloadDatabase(context);
		return dd;
	}

	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
	{
		// TODO: Implement this method
	}

	@Override
	public void onCreate(SQLiteDatabase p1)
	{
		p1.execSQL("create table download(title TEXT,url TEXT primary key,state INTEGER,total INTEGER,dir TEXT,referer TEXT)");
	}


	
}
