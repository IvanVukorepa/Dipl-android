package com.example.androidchatapp.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE="create table Messages"
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "user TEXT NOT NULL,"
            + "chatName TEXT NOT NULL,"
            + "username TEXT NOT NULL,"
            + "messageContent TEXT NOT NULL)";

    private static final String GROUPS_TABLE="create table Groups"
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "userName TEXT NOT NULL,"
            + "groupName TEXT NOT NULL,"
            + "datetime TEXT NOT NULL,"
            + "guid TEXT NOT NULL)";

    public DBHelper(Context context){
        super(context, "messagesDatabase.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
        sqLiteDatabase.execSQL(GROUPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old_version, int new_version) {
        Log.w(DBHelper.class.getName(), "Upgrading database from version " + old_version
                + " to " + new_version + ", which will delete all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Messages");
        onCreate(sqLiteDatabase);
    }
}
