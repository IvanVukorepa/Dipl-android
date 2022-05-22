package com.example.androidchatapp.Services;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.androidchatapp.main_screen.MainActivity;

public class MyPreferences {

    String PREFS_NAME = "MyPreferences";
    SharedPreferences sharedPreferences;

    public MyPreferences(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public void setString(String key, String value){
        sharedPreferences.edit().putString(key, value).commit();
    }

    public String getString(String key){
        return sharedPreferences.getString(key, "");
    }
}
