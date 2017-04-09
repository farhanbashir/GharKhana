package com.example.muhammadfarhanbashir.gharkhana.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by fbashir on 9/27/2016.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Emcor.db";
    private static final String TABLE_NAME = "data_table";
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "_name";
    private static final String KEY_DATA = "_data";
    private static final String KEY_DATETIME = "_datetime";

    String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_NAME+" TEXT,"+KEY_DATA+" TEXT,"+KEY_DATETIME+" LONG)";
    String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public boolean addData(String name, String data, long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, name);
            values.put(KEY_DATA, data);
            values.put(KEY_DATETIME, time);

            boolean createSuccessfull = db.insert(TABLE_NAME, null, values) > 0;
            db.close();
            return createSuccessfull;
        }catch (Exception e){
            Log.e("problem",e+"");
            return false;
        }
    }

    public boolean updateData(LocalData data)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, data.name);
        values.put(KEY_DATA, data.data);
        values.put(KEY_DATETIME, data.time);

        // updating row
        return db.update(TABLE_NAME, values, KEY_NAME + " = ?",new String[]{data.name}) > 0;
    }

    public int getCount()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT * FROM "+TABLE_NAME;
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();

        return recordCount;
    }

    public LocalData getData(String name)
    {
        LocalData data = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_NAME+"='"+name+"'", null);
        if(cursor != null && cursor.moveToFirst())
        {
            data = new LocalData(cursor.getString(1),
                    cursor.getString(2), cursor.getLong(3));

        }
        return data;
    }

    public class LocalData{
        public String name;
        public String data;
        public long time;
        //public Date datetime;

        public LocalData(String name, String data, long time)
        {
            this.name = name;
            this.data = data;
            this.time = time;
        }
    }
}
