package com.example.moble_project.test.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MemoDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "memo_test.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MEMOS = "memos";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTENT = "content";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_MEMOS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_CONTENT + " TEXT)";

    public MemoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Here you can implement the code to manage the database schema changes
    }

    public List<String> getAllMemoTitles() {
        List<String> memoTitles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_MEMOS,
                new String[]{COLUMN_ID, COLUMN_CONTENT},
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String memoContent = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                // 메모 내용에서 앞부분 10글자만 가져오기
                String truncatedContent = memoContent.length() > 10
                        ? memoContent.substring(0, 15) + "..."
                        : memoContent;
                memoTitles.add(truncatedContent);
            }
            cursor.close();
        }

        return memoTitles;
    }

    public String getMemoContent(String title) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_CONTENT
        };

        String selection = COLUMN_CONTENT + " LIKE ?";
        String[] selectionArgs = {title};

        Cursor cursor = db.query(
                TABLE_MEMOS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String memoContent = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
            cursor.close();
            return memoContent;
        }

        return null;
    }

    public long getMemoId(String memoTitle) {
        SQLiteDatabase db = getReadableDatabase();
        long memoId = -1;

        String[] projection = {COLUMN_ID};
        String selection = COLUMN_CONTENT + " = ?";
        String[] selectionArgs = {memoTitle};

        Cursor cursor = db.query(
                TABLE_MEMOS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            memoId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
        }

        cursor.close();
        return memoId;
    }

}
