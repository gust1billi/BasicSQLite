package com.example.basicsqlite.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context ctx;

    private static final String TABLE_NAME = "data_library";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "data_title";
    private static final String COLUMN_DATA = "data_core";
    private static final String COLUMN_NUMBER = "data_number";

    private static final String DATABASE_NAME= "DataLibrary.db";
    private static final int DATABASE_VERSION= 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE "+ TABLE_NAME + " ("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TITLE + " TEXT, " + COLUMN_DATA + " TEXT, "
                        + COLUMN_NUMBER + " INTEGER);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addData(String title, String data, int number){
        SQLiteDatabase db = DatabaseHelper.this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DATA, data);
        values.put(COLUMN_NUMBER, number);
        long result = db.insert(TABLE_NAME, null, values);

        String callback;
        if (result == -1){
            callback = "Failure";
        } else callback = "Successs";

        Toast.makeText(ctx, callback, Toast.LENGTH_SHORT).show();
    }
}
