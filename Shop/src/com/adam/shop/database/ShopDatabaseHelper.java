package com.adam.shop.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShopDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "todotable.db";
	private static final int DATABASE_VERSION = 4;
	
	public ShopDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		ChoiceTable.onCreate(database);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		ChoiceTable.onUpgrade(database, oldVersion, newVersion);
	}
	
}
