package com.example.androidchatapp.Models;

import java.util.ArrayList;

public class NewGroup {
    private ArrayList<String> users;
    private String name;

    public ArrayList<String> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NewGroup(){

    }
    public NewGroup(ArrayList<String> usernames, String groupName){
        users = usernames;
        name = groupName;
    }
}
