package com.example.androidchatapp.DB;

public class MessageDatabse {
    private int id;
    private String chatName;
    private String username;
    private String messageContent;

    public int getId(){
        return id;
    }

    public String getChatName(){
        return chatName;
    }

    public String getUsername(){
        return username;
    }

    public String getMessageContent(){
        return messageContent;
    }

    public void setId(int messageId){
        id = messageId;
    }

    public void setChatName(String ChatName){
        chatName = ChatName;
    }

    public void setUsername(String Username){
        username = Username;
    }

    public void setMessageContent(String message){
        messageContent = message;
    }
}
