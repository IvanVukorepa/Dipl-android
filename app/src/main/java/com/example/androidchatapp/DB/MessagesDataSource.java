package com.example.androidchatapp.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.androidchatapp.Services.Message;

import java.util.ArrayList;
import java.util.List;

public class MessagesDataSource {

    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public MessagesDataSource(Context context){
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        database.close();
    }

    public void addMessageToDB(String user, String username, String group, String content, String imageURI, String datetime, String guid){
        ContentValues values = new ContentValues();

        values.put("user", user);
        values.put("chatName", group);
        values.put("username", username);
        values.put("messageContent", content);
        values.put("imageURI", imageURI);
        values.put("datetime", datetime);
        values.put("guid", guid);
        Log.e("addmessage", "adding " + content + " to db");
        database.insert("Messages", null, values);
    }

    public ArrayList<MessageDatabse> getAllMessagesForChat(String user, String chatName){
        ArrayList<MessageDatabse> messages = new ArrayList<>();

        Cursor cursor = database.rawQuery("select * from messages where user=" + "\"" + user + "\" and" +" chatName=" + "\"" + chatName + "\"", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            MessageDatabse message = new MessageDatabse();

            message.setId(cursor.getInt(0));
            message.setChatName(cursor.getString(2));
            message.setUsername(cursor.getString(3));
            message.setMessageContent(cursor.getString(4));
            message.setImageURI(cursor.getString(5));
            message.setDatetime(cursor.getString(6));
            message.setGuid(cursor.getString(7));
            messages.add(message);
            cursor.moveToNext();
        }

        cursor.close();
        return messages;
    }
}
