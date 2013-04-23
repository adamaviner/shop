package com.adam.shop.database;

import android.app.SearchManager;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: token
 * Date: 4/22/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProductTable {
    // Database table
    public static final String TABLE = "products";
    public static final String TABLE_FTS = "products_fts";

    public static final String COLUMN_ID = BaseColumns._ID;

    public static final String COLUMN_PRODUCT_ID = "productID";
    public static final String COLUMN_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String COLUMN_DESCRIPTION = SearchManager.SUGGEST_COLUMN_TEXT_2;
    public static final String COLUMN_TREATMENT = "treatment";
    public static final String COLUMN_CATEGORY = "category";

    // Database creation SQL statement
    private static final String productsCreate = "create table " + TABLE + "(" +
            COLUMN_ID + " integer primary key autoincrement, " + COLUMN_PRODUCT_ID + " integer, " +
            COLUMN_NAME + " varchar(200), " + COLUMN_DESCRIPTION + " varchar(200), " +
            COLUMN_TREATMENT + " varchar(200), " + COLUMN_CATEGORY + " varchar(200), unique(" + COLUMN_NAME + "));";

    private static final String productsFTSCreate = "create virtual table " + TABLE_FTS + " using fts3(" +
            COLUMN_ID + " integer primary key autoincrement, " + COLUMN_PRODUCT_ID + " integer, " +
            COLUMN_NAME + " varchar(200), " + COLUMN_DESCRIPTION + " varchar(200), " +
            COLUMN_TREATMENT + " varchar(200), " + COLUMN_CATEGORY + " varchar(200), unique(" + COLUMN_NAME + "));";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(productsCreate);
        database.execSQL(productsFTSCreate);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(ChoiceTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_FTS);
        onCreate(database);
    }
}
