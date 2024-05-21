package com.example.basicsqlite.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.basicsqlite.rv.DataRVAdapter;

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
    public void popLastRow(SQLiteDatabase db, DataRVAdapter rvAdapter, int position){
        db.execSQL("DELETE FROM " + TABLE_NAME
                + " WHERE " + COLUMN_ID + " = "
                + "(SELECT MAX(" + COLUMN_ID
                + ") FROM " + TABLE_NAME + ");"
        );

        DataRVAdapter adapter = rvAdapter;
        adapter.popLastItem(position);
    }

    public void updateData(String row_id, String title, String data, int num){
        SQLiteDatabase db = DatabaseHelper.this.getWritableDatabase();
        ContentValues values = assignTable(title, data, num);

        long result = db.update(TABLE_NAME, values, "_id=?", new String[]{row_id});

        if (result == -1){ Toast.makeText(ctx, "Failed to Update", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(ctx, "Successfully updates", Toast.LENGTH_SHORT).show();
    }

    public void addData(String title, String data, int number){
        SQLiteDatabase db = DatabaseHelper.this.getWritableDatabase();
        ContentValues values = assignTable(title, data, number);

        long result = db.insert(TABLE_NAME, null, values);

        String callback;
        if (result == -1){
            callback = "Failure";
        } else callback = "Successs";

        Toast.makeText(ctx, callback, Toast.LENGTH_SHORT).show();
    }

    public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = DatabaseHelper.this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id} );

        if (result == -1){
            Toast.makeText(ctx, "Failed to Delete", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(ctx, "Successfully Deleted", Toast.LENGTH_SHORT).show();
    }

    private ContentValues assignTable(String title, String data, int number){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NUMBER, number);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DATA, data);
        return values;
    }

}
