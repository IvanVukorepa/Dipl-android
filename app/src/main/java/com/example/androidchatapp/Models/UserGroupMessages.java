package com.example.androidchatapp.Models;

import com.example.androidchatapp.Services.Message;

import java.util.ArrayList;

public class UserGroupMessages {
    public String username;
    public String group;
    public String chatName;
    public String message;

    public UserGroupMessages(String Username, String Group, String ChatName){
        username = Username;
        group = Group;
        chatName = ChatName;
        //message = new ArrayList<>();
    }

    public UserGroupMessages(){
        username="";
        group="";
        chatName="";
        //message = new ArrayList<>();
    }
}
