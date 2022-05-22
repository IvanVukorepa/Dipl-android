package com.example.androidchatapp.Models;

import com.example.androidchatapp.Services.Message;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class UserGroup {

    public String username;
    public String group;
    public String chatName;

    public UserGroup(String Username, String Group, String ChatName){
        username = Username;
        group = Group;
        chatName = ChatName;
    }

    public UserGroup(){
        username="";
        group="";
        chatName="";
    }
}
