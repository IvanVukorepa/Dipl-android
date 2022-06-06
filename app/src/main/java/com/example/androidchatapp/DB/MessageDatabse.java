package com.example.androidchatapp.DB;

public class MessageDatabse {
    private int id;
    private String chatName;
    private String username;
    private String messageContent;
    private String imageURI;
    private String datetime;
    private String guid;

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

    public String getImageURI() {
        return imageURI;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getGuid() {
        return guid;
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

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
