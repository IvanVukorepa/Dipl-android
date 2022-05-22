package com.example.androidchatapp.Services;

public class Message {
    public String user;
    public String message;
    public String time;
    public String guid;

    public Message(String input){
        user = "test";
        message = input;
        time = "";
        guid = "";
    }

    public Message(String User, String Content){
        user = User;
        message = Content;
        time = "";
    }

    public Message(String User, String Content, String Time, String Guid){
        user = User;
        message = Content;
        time = Time;
        guid = Guid;
    }
}
