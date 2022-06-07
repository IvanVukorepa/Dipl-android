package com.example.androidchatapp.Services;

import android.provider.ContactsContract;

public class Message {
    public String user;
    public String message;
    public String imageURI;
    public String time;
    public String guid;

    public Message(String input){
        user = "test";
        message = input;
        time = "";
        guid = "";
        imageURI = "";
    }

    public Message(String User, String Content){
        user = User;
        message = Content;
        time = "";
        imageURI = "";
    }

    public Message(String User, String Content, String Image){
        user = User;
        message = Content;
        imageURI = Image;
        time = "";
    }

    public Message(String User, String Content, String Time, String Guid){
        user = User;
        message = Content;
        time = Time;
        guid = Guid;
        imageURI = "";
    }

    public Message(String User, String Content, String Image, String Time, String Guid){
        user = User;
        message = Content;
        time = Time;
        guid = Guid;
        imageURI = Image;
    }

}
