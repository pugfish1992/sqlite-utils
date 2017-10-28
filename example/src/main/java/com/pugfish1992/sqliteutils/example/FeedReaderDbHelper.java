package com.pugfish1992.sqliteutils.example;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pugfish1992.sqliteutils.example.FeedReaderContract.FeedEntry;

import com.pugfish1992.sqliteutils.library.Column;
import com.pugfish1992.sqliteutils.library.DataType;
import com.pugfish1992.sqliteutils.library.TableCreator;
import com.pugfish1992.sqliteutils.library.TableUtils;

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        TableCreator
                .tableName(FeedEntry.TABLE_NAME)
                .addColumn(Column.nameAndDataType(FeedEntry._ID, DataType.INTEGER).isPrimaryKey(true))
                .addColumn(Column.nameAndDataType(FeedEntry.COLUMN_NAME_TITLE, DataType.TEXT).isNotNull(true))
                .addColumn(Column.nameAndDataType(FeedEntry.COLUMN_NAME_SUBTITLE, DataType.TEXT).defaultValue("default text"))
                .create(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TableUtils.deleteTableIfExists(FeedEntry.TABLE_NAME, db);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}