package com.icatch.mobilecam.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.icatch.mobilecam.Log.AppLog;

public class CameraSlotSQLiteHelper extends SQLiteOpenHelper{

	private  String databaseCreate = "CREATE TABLE IF NOT EXISTS " +
			DATABASE_NAME +
			" " +
			"(_id INTEGER PRIMARY KEY AUTOINCREMENT, isOccupied INTEGER, cameraName VARCHAR, imageBuffer BLOB)";
    private String tableDrop = "drop table if exists "+DATABASE_TABLE;
	// database version
	private static final int DATABASE_VERSION = 1;
	// database name
	private static final String DATABASE_NAME = "cameraSlotDb112.db";
	public static final String DATABASE_TABLE = "cameraSlotInfo";
	private String CREATE_CAMINFODB = "CREATE TABLE IF NOT EXISTS cameraSlotInfo (_id integer primary key autoincrement, isOccupied integer, cameraName varchar, cameraType integer, imageBuffer blob)";
	//private String CREATE_CAMINFODB = "CREATE TABLE IF NOT EXISTS caminfo (_id INTEGER PRIMARY KEY AUTOINCREMENT, slotname VARCHAR, cname VARCHAR,imagebuffer BLOB)";


	public CameraSlotSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		AppLog.d("tigertiger","start CREATE_CAMINFODB");
		db.execSQL(CREATE_CAMINFODB);
		AppLog.d("tigertiger", "end CREATE_CAMINFODB");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(tableDrop);
		onCreate(db);
	}

}
