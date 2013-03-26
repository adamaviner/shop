package com.adam.shop.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ShopContentProvider extends ContentProvider {

    private ShopDatabaseHelper database;

    private static final String AUTHORITY = "com.adam.shop.database.ShopContentProvider";
    public static final int CHOICES = 100;

    public static final int CHOICE_ID = 110;

    private static final String CHOICES_BASE_PATH = "choices";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CHOICES_BASE_PATH);

    public static final String CONTENT_ITEM_TYPE;
    public static final String CONTENT_TYPE;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, CHOICES_BASE_PATH, CHOICES);
        sURIMatcher.addURI(AUTHORITY, CHOICES_BASE_PATH + "/#", CHOICE_ID);
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
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CHOICE_ID:
                queryBuilder.appendWhere(ChoiceTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case CHOICES:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        Cursor cursor = queryBuilder.query(database.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsAffected;
        switch (uriType) {
            case CHOICES:
                rowsAffected = sqlDB.delete(ChoiceTable.TABLE, selection, selectionArgs);
                break;
            case CHOICE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = sqlDB.delete(ChoiceTable.TABLE, ChoiceTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsAffected = sqlDB.delete(ChoiceTable.TABLE, selection + " and " + ChoiceTable.COLUMN_ID + "=" + id, selectionArgs);
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
        switch (uriType) {
            case CHOICES:
                id = sqlDB.insert(ChoiceTable.TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(CHOICES_BASE_PATH + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated;
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
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

}
