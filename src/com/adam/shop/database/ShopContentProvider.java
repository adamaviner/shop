package com.adam.shop.database;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class ShopContentProvider extends ContentProvider {
    private ShopDatabaseHelper database;

    private static final String AUTHORITY = "com.adam.shop.database.ShopContentProvider";

    public static final int CHOICES = 1;
    public static final int CHOICE_ID = 2;
    public static final int PRODUCT_ID = 3;
    public static final int PRODUCTS = 4;
    public static final int SEARCH_SUGGEST = 5;

    private static final String CHOICES_BASE_PATH = "choices";
    private static final String PRODUCTS_BASE_PATH = "products";
    public static final Uri CHOICES_URI = Uri.parse("content://" + AUTHORITY + "/" + CHOICES_BASE_PATH);
    public static final Uri PRODUCTS_URI = Uri.parse("content://" + AUTHORITY + "/" + PRODUCTS_BASE_PATH);
    public static final Uri SUGGEST_URI = Uri.parse("content://" + AUTHORITY + "/" + SearchManager.SUGGEST_URI_PATH_QUERY);

    public static final String CONTENT_ITEM_TYPE;
    public static final String CONTENT_TYPE;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, CHOICES_BASE_PATH, CHOICES);
        sURIMatcher.addURI(AUTHORITY, CHOICES_BASE_PATH + "/#", CHOICE_ID);
        sURIMatcher.addURI(AUTHORITY, PRODUCTS_BASE_PATH, PRODUCTS);
        sURIMatcher.addURI(AUTHORITY, PRODUCTS_BASE_PATH + "/#", PRODUCT_ID);

        //for search suggestions
        sURIMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        sURIMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

        CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/choice";
        CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/choices";
    }

    @Override
    public boolean onCreate() {
        database = new ShopDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ChoiceTable.TABLE);
        Cursor cursor = null;
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CHOICE_ID:
                queryBuilder.appendWhere(ChoiceTable.COLUMN_ID + "=" + uri.getLastPathSegment());
            case CHOICES:
                cursor = queryBuilder.query(database.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                queryBuilder.setTables(ProductTable.TABLE);
                queryBuilder.appendWhere(ProductTable.ID + "=" + uri.getLastPathSegment());
                cursor = queryBuilder.query(database.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCTS:
                queryBuilder.setTables(ProductTable.TABLE);
                cursor = queryBuilder.query(database.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SEARCH_SUGGEST:
                cursor = textSearch(selectionArgs[0], null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        if (cursor == null) return null;
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor textSearch(String query, String[] columns) {
        String selection = ProductTable.TABLE_FTS + " MATCH ?";
        String[] selectionArgs = new String[]{query + "*"};
        return query(selection, selectionArgs, columns);
    }

    private Cursor query(final String selection, final String[] selectionArgs, final String[] columns) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ProductTable.TABLE_FTS);
        queryBuilder.setProjectionMap(buildProjectionMap());
        Cursor cursor = queryBuilder.query(database.getReadableDatabase(), columns, selection, selectionArgs, null, null, null);

        return cursor;
    }

    private HashMap<String, String> buildProjectionMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(ProductTable.NAME, ProductTable.NAME);
        map.put(ProductTable.QUANTITY, ProductTable.QUANTITY);
        map.put(ProductTable.DESCRIPTION, ProductTable.DESCRIPTION);
        map.put(ProductTable.CATEGORY, ProductTable.CATEGORY);
        map.put(ProductTable.TREATMENT, ProductTable.TREATMENT);
        map.put(ProductTable.PRODUCT_ID, ProductTable.PRODUCT_ID);
        map.put(ProductTable.ID, ProductTable.ID);
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " + SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        return map;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsAffected;
        String id;
        switch (uriType) {
            case CHOICES:
                rowsAffected = sqlDB.delete(ChoiceTable.TABLE, selection, selectionArgs);
                break;
            case CHOICE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = sqlDB.delete(ChoiceTable.TABLE, ChoiceTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsAffected = sqlDB.delete(ChoiceTable.TABLE, selection + " and " + ChoiceTable.COLUMN_ID + "=" + id, selectionArgs);
                }
                break;
            case PRODUCT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    sqlDB.delete(ProductTable.TABLE, ProductTable.ID + "=" + id, null);
                    rowsAffected = sqlDB.delete(ProductTable.TABLE_FTS, ProductTable.ROW_ID + "=" + id, null);
                } else {
                    sqlDB.delete(ProductTable.TABLE, selection + " and " + ProductTable.ID + "=" + id, selectionArgs);
                    rowsAffected = sqlDB.delete(ProductTable.TABLE_FTS, selection + " and " + ProductTable.ROW_ID + "=" + id, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id;
        Uri returnURI;
        switch (uriType) {
            case CHOICES:
                id = sqlDB.insert(ChoiceTable.TABLE, null, values);
                returnURI = Uri.parse(CHOICES_BASE_PATH + "/" + id);
                break;
            case PRODUCTS:
                id = sqlDB.insert(ProductTable.TABLE, null, values);
                if (id != -1) // if insertion worked we insert to the fts table as well.
                    id = sqlDB.insert(ProductTable.TABLE_FTS, null, values);
                returnURI = Uri.parse(PRODUCTS_BASE_PATH + "/" + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnURI;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case CHOICES:
                rowsUpdated = sqlDB.update(ChoiceTable.TABLE, values, selection, selectionArgs);
                break;
            case CHOICE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(ChoiceTable.TABLE, values, ChoiceTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(ChoiceTable.TABLE, values, ChoiceTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            case PRODUCT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    sqlDB.update(ProductTable.TABLE, values, ProductTable.ID + "=" + id, null);
                    rowsUpdated = sqlDB.update(ProductTable.TABLE_FTS, values, ProductTable.ROW_ID + "=" + id, null);
                } else {
                }

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}