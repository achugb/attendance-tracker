package com.spandexman.attendancetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbManager {
	
	public static final String clmn_id = "_id";
	public static final String clmn_title = "title";
	public static final String clmn_attended = "attended";
	public static final String clmn_missed = "missed";
	
	public static final String DB_NAME = "data";
	public static final String DB_TABLE = "attendance";
	public static final int DB_VERSION = 2;
	
	public static final String DB_CMD_CREATE = "create table " + DB_TABLE
		+ " (" + clmn_id + " integer primary key autoincrement, " 
		+ clmn_title + " text not null, "
		+ clmn_attended + " integer default 0, "
		+ clmn_missed + "  integer default 0);";
		
	public Context ctxt;
	public DbHelper mydbhelper;
	public SQLiteDatabase mydb;
	
	private static class DbHelper extends SQLiteOpenHelper
	{
		DbHelper(Context context)
		{
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CMD_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.v("DbManager_attendance_tracker","upgrading database from " 
					+ oldVersion + " to " + newVersion
					+ ". Old data will be destroyed.");
			db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
			onCreate(db);
		}
		
	}
	
	public DbManager(Context context)
	{
		this.ctxt = context; 
	}
	
	public DbManager open() throws SQLException
	{
		mydbhelper = new DbHelper(ctxt);
		mydb = mydbhelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		mydbhelper.close();
	}
	
	public long createEntry(String title)
	{
		ContentValues init = new ContentValues();
		init.put(clmn_title, title);
		init.put(clmn_attended, 0);
		init.put(clmn_missed, 0);
		
		return mydb.insert(DB_TABLE, null, init);
	}
	
	public Boolean deleteRow(long rowId)
	{
		return mydb.delete(DB_TABLE, clmn_id + "=" + rowId, null) > 0;
	}
	
	public Boolean updateRow(long rowId, String title, int att, int miss)
	{
		ContentValues update = new ContentValues();
		update.put(clmn_title, title);
		update.put(clmn_attended, att);
		update.put(clmn_missed, miss);
		
		return mydb.update(DB_TABLE, update, clmn_id + "=" + rowId, null)>0;
	}
	
	public Cursor fetchSingleData(long rowId)
	{
		Cursor return_cursor = mydb.query(DB_TABLE,
				new String[] {clmn_id, clmn_title, clmn_attended, clmn_missed},
				 clmn_id + "=" + rowId, null, null, null, null);
		
		if (return_cursor != null)
			return_cursor.moveToFirst();
		
		return return_cursor;
	}
	
	public Cursor fetchAllData()
	{
		return mydb.query(DB_TABLE, new String[] {clmn_id, clmn_title, clmn_attended, clmn_missed},
				null, null, null, null, null);
	}

}
