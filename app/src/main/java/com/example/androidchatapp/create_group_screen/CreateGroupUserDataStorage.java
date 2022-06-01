package com.example.androidchatapp.create_group_screen;

import android.content.Context;
import com.example.androidchatapp.DB.GroupsDataSource;
import com.example.androidchatapp.Models.Group;
import com.example.androidchatapp.Services.AuthTokenService;


import java.util.ArrayList;

public class CreateGroupUserDataStorage {
    public static ArrayList<String> usernames = new ArrayList<>();
    public static ArrayList<String> selectedUsernames = new ArrayList<>();


    public static void fillData(final Context context, final CreateGroupUserListAdapter adapter){
        usernames.clear();
        GroupsDataSource groupsDataSource = new GroupsDataSource(context);
        groupsDataSource.open();
        ArrayList<Group> groups = groupsDataSource.getAllGroupsForUser(AuthTokenService.getPayloadData("username"));
        groupsDataSource.close();

        for (Group g: groups){
            String username = getUsername(g.getGroupName());
            if (!username.equals("")){
                usernames.add(username);
            }
        }

        adapter.notifyDataSetChanged();
    }


    public static String getUsername(String name){
        String myUsername = AuthTokenService.getPayloadData("username");
        if (name.contains(myUsername + "_")){
            return name.replace(myUsername + "_", "");
        } else if(name.contains("_" + myUsername)){
            return name.replace("_" + myUsername, "");
        } else {
            return "";
        }
    }
}
