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

    public static final String ID = BaseColumns._ID;
    public static final String ROW_ID = "rowid";

    public static final String PRODUCT_ID = "productID";
    public static final String NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String DESCRIPTION = SearchManager.SUGGEST_COLUMN_TEXT_2;
    public static final String TREATMENT = "treatment";
    public static final String CATEGORY = "category";
    public static final String QUANTITY = "quantity";

    // Database creation SQL statement
    private static final String baseCreate = ID + " integer primary key autoincrement, " + PRODUCT_ID + " integer, " +
            NAME + " varchar(200), " + DESCRIPTION + " varchar(200), " +
            TREATMENT + " varchar(200), " + CATEGORY + " varchar(200), " +
            QUANTITY + " integer not null default 0, " +
            "unique(" + NAME + ", " + DESCRIPTION + "));";

    private static final String productsCreate = "create table " + TABLE + "(" + baseCreate;
    private static final String productsFTSCreate = "create virtual table " + TABLE_FTS + " using fts3(" + baseCreate;

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
