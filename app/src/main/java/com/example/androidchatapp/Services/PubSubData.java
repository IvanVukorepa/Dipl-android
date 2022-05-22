package com.example.androidchatapp.Services;

import android.app.admin.DelegatedAdminReceiver;

public class PubSubData {

    public String type;
    public String from;
    public String event;
    public String fromUserId;
    public String group;
    public String dataType;
    public Message data;

    public PubSubData(String Group, Message Data){
        event = null;
        group = Group;
        data = Data;
    }
}
