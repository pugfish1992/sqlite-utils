package com.pugfish1992.sqliteutils.example;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pugfish1992.sqliteutils.library.Query;
import com.pugfish1992.sqliteutils.library.WhereClause;

import java.util.ArrayList;
import java.util.List;

import static com.pugfish1992.sqliteutils.example.FeedReaderContract.FeedEntry._ID;
import static com.pugfish1992.sqliteutils.example.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE;
import static com.pugfish1992.sqliteutils.example.FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE;
import static com.pugfish1992.sqliteutils.example.FeedReaderContract.FeedEntry.TABLE_NAME;

/**
 * Created by daichi on 10/29/17.
 */

public class FeedCRUDer {

    private FeedReaderDbHelper mDbHelper;

    FeedCRUDer(Context context) {
        mDbHelper = new FeedReaderDbHelper(context);
    }

    long insert(Feed feed) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(TABLE_NAME, null, feedToContentValues(feed, false));
        db.close();
        return id;
    }

    List<Feed> getAll() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Query query = new Query().addTables(TABLE_NAME);
        final List<Feed> feeds = new ArrayList<>();

        query.startQueryAndScanResult(db, new Query.ScanningResultCallback() {
            @Override
            public void onStartScanning(Cursor cursor) {}

            @Override
            public void onScanRow(ContentValues valueMap) {
                feeds.add(contentValuesToFeed(valueMap));
            }

            @Override
            public boolean onEndScanning(Cursor cursor) {
                return true;
                // If return 'false', you must call 'cursor.close;'
            }
        });

        db.close();
        return feeds;
    }

    void update(Feed feed) {
        String where = new WhereClause()
                .equalTo(_ID, feed.id)
                .toStatement();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.update(TABLE_NAME, feedToContentValues(feed, true), where, null);
        db.close();
    }

    void deleteFeed(Feed feed) {
        String where = new WhereClause()
                .equalTo(_ID, feed.id)
                .toStatement();

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, where, null);
        db.close();
    }

    private ContentValues feedToContentValues(Feed feed, boolean includeId) {
        ContentValues values = new ContentValues();
        if (includeId) {
            values.put(_ID, feed.id);
        }
        values.put(COLUMN_NAME_TITLE, feed.title);
        values.put(COLUMN_NAME_SUBTITLE, feed.subTitle);
        return values;
    }

    private Feed contentValuesToFeed(ContentValues valueMap) {
        Feed feed = new Feed();
        feed.id = valueMap.getAsLong(_ID);
        feed.title = valueMap.getAsString(COLUMN_NAME_TITLE);
        feed.subTitle = valueMap.getAsString(COLUMN_NAME_SUBTITLE);
        return feed;
    }
}
