package com.example.costa.epeleptic_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Costa on 04.01.16.
 */

class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "epileptic", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table statistic ("
                + "id integer primary key autoincrement,"
                + "time text,"
                + "month text,"
                + "year text,"
                + "longitude double,"
                + "latitude double,"
                + "epileptic text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}

