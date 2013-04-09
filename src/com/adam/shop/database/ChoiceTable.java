package com.adam.shop.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ChoiceTable {
    // Database table
    public static final String TABLE = "choice";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_CHECKED = "checked";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table " + TABLE + "(" + COLUMN_ID +
            " integer primary key autoincrement, " + COLUMN_NAME + " text not null, " + "" +
            COLUMN_QUANTITY + " integer not null default 0, " + COLUMN_CHECKED + " boolean not null default 0, " +
            "UNIQUE("+COLUMN_NAME+"));";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(ChoiceTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(database);
    }

}
