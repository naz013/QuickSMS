package com.hexrain.design.quicksms.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class Database {
    private static final String DB_NAME = "quick_base";
    private static final int DB_VERSION = 1;
    private static final String MESSAGES_TABLE_NAME = "messages_table";

    private DBHelper dbHelper;
    private static Context mContext;
    private SQLiteDatabase db;

    private static final String MESSAGES_TABLE_CREATE =
            "create table " + MESSAGES_TABLE_NAME + "(" +
                    Constants.COLUMN_ID + " integer primary key autoincrement, " +
                    Constants.COLUMN_TEXT + " VARCHAR(255), " +
                    Constants.COLUMN_NUMBER + " VARCHAR(255), " +
                    Constants.COLUMN_TECH_INT + " INTEGER, " +
                    Constants.COLUMN_TECH_LINT + " INTEGER, " +
                    Constants.COLUMN_TECH_VAR + " VARCHAR(255), " +
                    Constants.COLUMN_VAR2 + " VARCHAR(255), " +
                    Constants.COLUMN_DATE_TIME + " INTEGER " +
                    ");";

    public class DBHelper extends SQLiteOpenHelper {


        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(MESSAGES_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public Database(Context c) {
        mContext = c;
    }

    public Database open() throws SQLiteException {
        dbHelper = new DBHelper(mContext);

        db = dbHelper.getWritableDatabase();

        System.gc();
        return this;
    }

    public boolean isOpen () {
        return db != null && db.isOpen();
    }

    public SQLiteDatabase getDatabase() {
        return db;
    }

    public void close() {
        if( dbHelper != null )
            dbHelper.close();
    }

    //Working with SMS templates table

    public long addTemplate (String text, long dateTime) {
        openGuard();
        ContentValues cv = new ContentValues();
        cv.put(Constants.COLUMN_TEXT, text);
        cv.put(Constants.COLUMN_DATE_TIME, dateTime);
        //Log.d(LOG_TAG, "data is inserted " + cv);
        return db.insert(MESSAGES_TABLE_NAME, null, cv);
    }

    public boolean updateTemplate(long rowId, String text, long dateTime){
        openGuard();
        ContentValues args = new ContentValues();
        args.put(Constants.COLUMN_TEXT, text);
        args.put(Constants.COLUMN_DATE_TIME, dateTime);
        return db.update(MESSAGES_TABLE_NAME, args, Constants.COLUMN_ID + "=" + rowId, null) > 0;
    }

    public Cursor getTemplate(long id) throws SQLException {
        openGuard();
        return db.query(MESSAGES_TABLE_NAME, null, Constants.COLUMN_ID  +
                "=" + id, null, null, null, null, null);
    }

    public Cursor queryTemplates() throws SQLException {
        openGuard();
        return db.query(MESSAGES_TABLE_NAME, null, null, null, null, null, null);
    }

    public boolean deleteTemplate(long rowId) {
        openGuard();
        return db.delete(MESSAGES_TABLE_NAME, Constants.COLUMN_ID + "=" + rowId, null) > 0;
    }

    public void openGuard() throws SQLiteException {
        if(isOpen()) return;
        open();
        if(isOpen()) return;
        //Log.d(LOG_TAG, "open guard failed");
        throw new SQLiteException("Could not open database");
    }
}